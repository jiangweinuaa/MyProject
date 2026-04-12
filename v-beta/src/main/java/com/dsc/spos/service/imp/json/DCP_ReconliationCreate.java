package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReconliationCreateReq;
import com.dsc.spos.json.cust.res.DCP_ReconliationCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ReconliationCreate extends SPosAdvanceService<DCP_ReconliationCreateReq, DCP_ReconliationCreateRes> {

    @Override
    protected void processDUID(DCP_ReconliationCreateReq req, DCP_ReconliationCreateRes res) throws Exception {
        String eId = req.geteId();
        //String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_ReconliationCreateReq.level1Elm request = req.getRequest();
        //String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String settleSql = "select * from DCP_SETTLEDATA a " +
                " where a.eid='" + eId + "' " +
                //" and a.year='"+req.getRequest().getYear()+"' " +
                //" and a.month='"+req.getRequest().getMonth()+"' "
                " and a.bizpartnerno='" + req.getRequest().getBizPartnerNo() + "' " +
                " and a.status='0'";
        List<Map<String, Object>> settleList = this.doQueryData(settleSql, null);

        List<DCP_ReconliationCreateReq.ReconList> detailList = request.getReconList();
        for (DCP_ReconliationCreateReq.ReconList detail : detailList) {
            List<Map<String, Object>> collect = settleList.stream().filter(x -> x.get("BILLNO").toString().equals(detail.getSourceNo()) && x.get("ITEM").toString().equals(detail.getSourceNoSeq())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                //报错
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的底稿数据" + detail.getSourceNo() + "-" + detail.getSourceNoSeq());
            }
        }

        String corp = request.getCorp();
        //查询当前法人的关账日期
        String accSql = "select a.*,to_char(a.CLOSINGDATE,'yyyyMMdd') as CLOSINGDATE  from DCP_ACOUNT_SETTING a where a.eid='" + req.geteId() + "' and a.corp='" + corp + "'  and a.ACCTTYPE='1' ";
        List<Map<String, Object>> accList = this.doQueryData(accSql, null);
        if (CollectionUtils.isEmpty(accList)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的法人账套" + corp);
        }
        String closingDate = accList.get(0).get("CLOSINGDATE").toString();
        String bDate = req.getRequest().getBdate();
        if (DateFormatUtils.compareDate(bDate , closingDate) < 0) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不得小于关账日期");
        }

        String reconNo;
        if ("1".equals(request.getDataType())) {
            reconNo = getOrderNOWithDate(req, corp, "GYDZ", req.getRequest().getBdate());
        } else {
            reconNo = getOrderNOWithDate(req, corp, "KHDZ", req.getRequest().getBdate());
        }


        for (DCP_ReconliationCreateReq.ReconList detail : detailList) {
            ColumnDataValue detailColumns = new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
            detailColumns.add("CREATE_DATE", DataValues.newString(createDate));
            detailColumns.add("CREATE_TIME", DataValues.newString(createTime));
            detailColumns.add("ORGANIZATIONNO", DataValues.newString(request.getOrganizationNo()));
            detailColumns.add("CORP", DataValues.newString(request.getCorp()));
            detailColumns.add("RECONNO", DataValues.newString(reconNo));
            detailColumns.add("ITEM", DataValues.newString(detail.getItem()));
            detailColumns.add("SOURCETYPE", DataValues.newString(detail.getSourceType()));
            detailColumns.add("SOURCENO", DataValues.newString(detail.getSourceNo()));
            detailColumns.add("SOURCENOSEQ", DataValues.newString(detail.getSourceNoSeq()));
            detailColumns.add("RDATE", DataValues.newString(detail.getRDate()));
            detailColumns.add("FEE", DataValues.newString(detail.getFee()));
            detailColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
            detailColumns.add("CURRENCY", DataValues.newString(detail.getCurrency()));
            detailColumns.add("TAXRATE", DataValues.newString(detail.getTaxRate()));
            detailColumns.add("DIRECTION", DataValues.newString(detail.getDirection()));
            detailColumns.add("BILLQTY", DataValues.newString(detail.getBillQty()));
            detailColumns.add("RECONQTY", DataValues.newString(detail.getReconQty()));
            detailColumns.add("BILLPRICE", DataValues.newString(detail.getBillPrice()));
            detailColumns.add("PRETAXAMT", DataValues.newString(detail.getPreTaxAmt()));
            detailColumns.add("AMT", DataValues.newString(detail.getAmt()));
            detailColumns.add("RECONAMT", DataValues.newString(detail.getReconAmt()));
            detailColumns.add("UNPAIDAMT", DataValues.newString(detail.getUnPaidAmt()));
            detailColumns.add("CURRRECONAMT", DataValues.newString(detail.getCurrReconAmt()));
            detailColumns.add("DEPARTID", DataValues.newString(detail.getDepartId()));
            detailColumns.add("CATEGORY", DataValues.newString(detail.getCateGory()));
            detailColumns.add("TAXCODE", DataValues.newString(detail.getTaxCode()));
            detailColumns.add("ISINVOCEINCL", DataValues.newString(detail.getIsInvoiceIncl()));

            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean detailib = new InsBean("DCP_RECONDETAIL", detailColumnNames);
            detailib.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(detailib));

            List<Map<String, Object>> collect = settleList.stream().filter(x -> x.get("BILLNO").toString().equals(detail.getSourceNo()) && x.get("ITEM").toString().equals(detail.getSourceNoSeq())).collect(Collectors.toList());
//            if (collect.size() > 0) {
            Map<String, Object> sMap = collect.get(0);
            BigDecimal settleAmt = new BigDecimal(sMap.get("SETTLEAMT").toString());//已对账金额
            BigDecimal billAmt = new BigDecimal(sMap.get("BILLAMT").toString());
            settleAmt = settleAmt.add(new BigDecimal(detail.getCurrReconAmt()));
            BigDecimal unSettleAmt = billAmt.subtract(settleAmt);
            //更新状态  对账中
            UptBean ub1 = new UptBean("DCP_SETTLEDATA");
            ub1.addUpdateValue("status", new DataValue("1", Types.VARCHAR));
            ub1.addUpdateValue("SETTLEAMT", new DataValue(settleAmt.toString(), Types.VARCHAR));
            ub1.addUpdateValue("UNSETTLEAMT", new DataValue(unSettleAmt.toString(), Types.VARCHAR));
            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("BILLNO", new DataValue(detail.getSourceNo(), Types.VARCHAR));
            ub1.addCondition("ITEM", new DataValue(detail.getSourceNoSeq(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

//            } else {
//                //报错
//                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的底稿数据".toString());
//            }
        }


        ColumnDataValue mainColumns = new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
        mainColumns.add("CREATE_TIME", DataValues.newString(createTime));
        //mainColumns.add("MODIFYBY", DataValues.newString(employeeNo));
        //mainColumns.add("MODIFY_DATE", DataValues.newString(createTime));
        //mainColumns.add("MODIFY_TIME", DataValues.newString(createTime));
        mainColumns.add("STATUS", DataValues.newString(request.getStatus()));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));

        mainColumns.add("ORGANIZATIONNO", DataValues.newString(request.getOrganizationNo()));
        mainColumns.add("RECONNO", DataValues.newString(reconNo));
        mainColumns.add("BDATE", DataValues.newString(request.getBdate()));
        mainColumns.add("CORP", DataValues.newString(request.getCorp()));
        mainColumns.add("DATATYPE", DataValues.newString(request.getDataType()));
        mainColumns.add("BIZPARTNERNO", DataValues.newString(request.getBizPartnerNo()));
        mainColumns.add("YEAR", DataValues.newString(request.getYear()));
        mainColumns.add("MONTH", DataValues.newString(request.getMonth()));
        mainColumns.add("ESTRECEEXPDAY", DataValues.newString(request.getEstReceExpDay()));
        mainColumns.add("CURRRECONAMT", DataValues.newDecimal(request.getCurrReconAmt()));
        mainColumns.add("CURRRECONTAXAMT", DataValues.newDecimal(request.getCurrReconTaxAmt()));
        mainColumns.add("CURRRECONPRETAXAMT", DataValues.newDecimal(request.getCurrReconPretaxAmt()));
        mainColumns.add("PAIDRECEAMT", DataValues.newDecimal(request.getPaidReceAmt()));
        mainColumns.add("NOTPAIDRECEAMT", DataValues.newDecimal(request.getNotPaidReceAmt()));
        mainColumns.add("ISINVOICEINCL", DataValues.newString(request.getIsInvoiceIncl()));
        mainColumns.add("STARTDATE", DataValues.newString(request.getBeginDate()));
        mainColumns.add("ENDDATE", DataValues.newString(request.getEndDate()));
        mainColumns.add("CURRENCY", DataValues.newString(request.getCurrency()));
        mainColumns.add("PAYDATENO", DataValues.newString(request.getPayDateNo()));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib = new InsBean("DCP_RECONLIATION", mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        res.setReconNo(reconNo);
        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReconliationCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReconliationCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReconliationCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReconliationCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReconliationCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ReconliationCreateReq>() {
        };
    }

    @Override
    protected DCP_ReconliationCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ReconliationCreateRes();
    }

}


