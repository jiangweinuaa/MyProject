package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WOReportProcessReq;
import com.dsc.spos.json.cust.res.DCP_WOReportProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_WOReportProcess  extends SPosAdvanceService<DCP_WOReportProcessReq, DCP_WOReportProcessRes> {

    @Override
    protected void processDUID(DCP_WOReportProcessReq req, DCP_WOReportProcessRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String reportNo = req.getRequest().getReportNo();
        String oprType = req.getRequest().getOprType();
        String accountDate = req.getRequest().getAccountDate();

        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());


        String woReportSql="select * from DCP_WOREPORT a " +
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.reportno='"+reportNo+"' ";
        List<Map<String, Object>> list = this.doQueryData(woReportSql, null);
        if(list.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "工单报工不存在！".toString());
        }
        String status = list.get(0).get("STATUS").toString();

        //入参oprType=unconfirm
        //1.检验单据状态非“2-过账”不可反过账！
        //2.检查过账日期是否小于库存关账日
        //3.更新DCP_WOREPORT.STATUS=0
        //4.删除DCP_WOREPORT_TRAN表对应数据
        if ("unconfirm".equals(oprType)) {
            if (!"2".equals(status)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "工单报工状态非“2-过账”不可反过账！".toString());
            }

            String accSql="select a.*,to_char(a.INVCLOSINGDATE,'yyyyMMdd') as INVCLOSINGDATES  from DCP_ACOUNT_SETTING a where a.eid='"+eId+"' and a.corp='"+req.getCorp()+"'  and a.ACCTTYPE='1' and a.status='100' ";
            List<Map<String, Object>> accList = this.doQueryData(accSql, null);
            if(accList.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的账套");
            }

            String invClosingDate = accList.get(0).get("INVCLOSINGDATES").toString();
            if (accountDate.compareTo(invClosingDate) < 0) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "过账日期小于库存关账日！".toString());
            }

            UptBean ub1 = new UptBean("DCP_WOREPORT");
            ub1.addUpdateValue("STATUS", DataValues.newInteger(0));

            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", DataValues.newDate(createTime));
            ub1.addUpdateValue("ACCOUNTOPID", new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("ACCOUNTTIME", DataValues.newDate(null));
            ub1.addCondition("EID", DataValues.newString(eId));
            ub1.addCondition("SHOPID", DataValues.newString(req.getShopId()));
            ub1.addCondition("REPORTNO", DataValues.newString(reportNo));
            ub1.addCondition("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
            this.addProcessData(new DataProcessBean(ub1));


            DelBean db2 = new DelBean("DCP_WOREPORT_TRAN");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
            db2.addCondition("REPORTNO", new DataValue(reportNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

        }

        //入参oprType=cancel
        //1.检验单据状态非“0-新建”不可作废！
        //2.更新DCP_WOREPORT.STATUS=-1，作废人，作废时间
        if ("cancel".equals(oprType)) {
            if (!"0".equals(status)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "工单报工状态非“0-新建”不可作废！".toString());
            }
            UptBean ub1 = new UptBean("DCP_WOREPORT");
            ub1.addUpdateValue("STATUS", DataValues.newInteger(-1));

            ub1.addUpdateValue("CANCELOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("CANCELTIME", DataValues.newDate(createTime));
            ub1.addCondition("EID", DataValues.newString(eId));
            ub1.addCondition("SHOPID", DataValues.newString(req.getShopId()));
            ub1.addCondition("REPORTNO", DataValues.newString(reportNo));
            this.addProcessData(new DataProcessBean(ub1));
        }


        //入参oprType=confirm
        //1.检验单据状态非“0-新建”不可过账！
        //2.检查过账日期是否小于库存关账日
        //3.更新DCP_WOREPORT.STATUS=2，记账人，记账时间；记账日期字段，入参记账日期不为空更新为入参，为空更新为系统日期
        //4.将DCP_WOREPORT_DETAIL数据写入表DCP_WOREPORT_TRAN
        if ("confirm".equals(oprType)) {
            if (!"0".equals(status)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "工单报工状态非“0-新建”不可过账！".toString());
            }

            if(Check.Null(accountDate)){
                accountDate= DateFormatUtils.getNowPlainDate();
            }

            String accSql="select a.*,to_char(a.INVCLOSINGDATE,'yyyyMMdd') as INVCLOSINGDATES  from DCP_ACOUNT_SETTING a where a.eid='"+eId+"' and a.corp='"+req.getCorp()+"'  and a.ACCTTYPE='1' and a.status='100' ";
            List<Map<String, Object>> accList = this.doQueryData(accSql, null);
            if(accList.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的账套");
            }
            String invClosingDate = accList.get(0).get("INVCLOSINGDATES").toString();
            if (accountDate.compareTo(invClosingDate) < 0) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "过账日期小于库存关账日！".toString());
            }
            UptBean ub1 = new UptBean("DCP_WOREPORT");
            ub1.addUpdateValue("STATUS", DataValues.newInteger(2));
            ub1.addUpdateValue("ACCOUNTOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("ACCOUNTTIME", DataValues.newDate(createTime));
            ub1.addUpdateValue("ACCOUNTDATE", DataValues.newDate(accountDate));
            ub1.addCondition("EID", DataValues.newString(eId));
            ub1.addCondition("SHOPID", DataValues.newString(req.getShopId()));
            ub1.addCondition("REPORTNO", DataValues.newString(reportNo));
            this.addProcessData(new DataProcessBean(ub1));

            //4.将DCP_WOREPORT_DETAIL数据写入表DCP_WOREPORT_TRAN
            String detailSql="select * from DCP_WOREPORT_DETAIL a " +
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                    " and a.reportno='"+reportNo+"' ";
            List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);
            if(CollUtil.isNotEmpty(detailList)){
                Map<String, Object> header = list.get(0);
                for (Map<String, Object> detail : detailList) {

                    ColumnDataValue detailColumns=new ColumnDataValue();
                    detailColumns.add("EID", DataValues.newString(eId));
                    detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                    detailColumns.add("SHOPID", DataValues.newString(req.getShopId()));


                    // === 明细字段 ===
                    detailColumns.add("REPORTNO", DataValues.newString(reportNo));
                    detailColumns.add("ITEM", new DataValue(detail.get("ITEM"), Types.INTEGER));

                    // 上级来源
                    detailColumns.add("OOTYPE", DataValues.newInteger(0)); // 固定为0
                    detailColumns.add("OOFNO", new DataValue(detail.get("OFNO"), Types.VARCHAR));
                    detailColumns.add("OOITEM", new DataValue(detail.get("OITEM"), Types.INTEGER));

                    // 商品与生产信息
                    detailColumns.add("PLUNO", new DataValue(detail.get("PLUNO"), Types.VARCHAR));
                    detailColumns.add("FEATURENO", new DataValue(detail.get("FEATURENO"), Types.VARCHAR));
                    detailColumns.add("PQTY", new DataValue(detail.get("PQTY"), Types.DECIMAL));
                    detailColumns.add("PUNIT", new DataValue(detail.get("PUNIT"), Types.VARCHAR));

                    // 工序信息（可能为空）
                    detailColumns.add("PITEM", new DataValue("", Types.INTEGER));
                    detailColumns.add("PROCESSNO", new DataValue("", Types.VARCHAR));
                    detailColumns.add("SITEM", new DataValue("", Types.INTEGER));
                    detailColumns.add("EQUIPNO", new DataValue(detail.get("EQUIPNO"), Types.VARCHAR));
                    detailColumns.add("EQTY", new DataValue(detail.get("EQTY"), Types.DECIMAL));
                    detailColumns.add("LABORTIME", new DataValue(detail.get("LABORTIME"), Types.DECIMAL));
                    detailColumns.add("MACHINETIME", new DataValue(detail.get("MACHINETIME"), Types.DECIMAL));

                    // === 时间与操作人字段 ===
                    detailColumns.add("ACCOUNTDATE", DataValues.newString(accountDate)); // 记账日期
                    detailColumns.add("BDATE", new DataValue(header.get("BDATE"), Types.VARCHAR)); // 单据日期
                    detailColumns.add("SDATE", DataValues.newString(header.get("BDATE"))); // 系统日期

                    detailColumns.add("CREATEOPID", new DataValue(header.get("CREATEOPID"), Types.VARCHAR));
                    detailColumns.add("LASTMODIOPID", new DataValue(header.get("LASTMODIOPID"), Types.VARCHAR));
                    detailColumns.add("CREATETIME", new DataValue(header.get("CREATETIME"), Types.DATE));
                    detailColumns.add("LASTMODITIME", new DataValue(header.get("LASTMODITIME"), Types.DATE));
                    detailColumns.add("ACCOUNTOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
                    detailColumns.add("ACCOUNTTIME", DataValues.newDate(createTime));


                    String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                    DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean detailib=new InsBean("DCP_WOREPORT_TRAN",detailColumnNames);
                    detailib.addValues(detailDataValues);
                    this.addProcessData(new DataProcessBean(detailib));

                }
            }


        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WOReportProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WOReportProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WOReportProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WOReportProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_WOReportProcessReq> getRequestType() {
        return new TypeToken<DCP_WOReportProcessReq>() {
        };
    }

    @Override
    protected DCP_WOReportProcessRes getResponseType() {
        return new DCP_WOReportProcessRes();
    }
}

