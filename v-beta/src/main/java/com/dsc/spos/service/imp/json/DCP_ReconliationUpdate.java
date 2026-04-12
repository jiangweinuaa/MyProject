package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReconliationUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ReconliationUpdateRes;
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

public class DCP_ReconliationUpdate  extends SPosAdvanceService<DCP_ReconliationUpdateReq, DCP_ReconliationUpdateRes> {

    @Override
    protected void processDUID(DCP_ReconliationUpdateReq req, DCP_ReconliationUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_ReconliationUpdateReq.level1Elm request = req.getRequest();
        //String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        String accountSql="select * from DCP_ACOUNT_SETTING a where a.eid='"+eId+"' " +
                " and a.accountid='"+req.getRequest().getCorp()+"' " +
                " and a.CURRENTYEAR='"+req.getRequest().getYear()+"' " +
                " and a.CURRENTPERIOD='"+req.getRequest().getMonth()+"' " +
                " and to_char(a.ARCLOSINGDATE,'yyyyMMdd')>='"+req.getRequest().getBdate()+"'";
        List<Map<String, Object>> accountList = this.doQueryData(accountSql, null);
        if(accountList.size()<=0){
            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期大于应收关账日！");
        }

        String sql="select * from DCP_RECONDETAIL a where a.eid='"+eId+"' " +
                " and a.reconno='"+req.getRequest().getReconNo()+"' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        List<String> items = list.stream().map(x -> x.get("ITEM").toString()).distinct().collect(Collectors.toList());

        String settleSql="select * from DCP_SETTLEDATA a " +
                " where a.eid='"+eId+"' " +
                //" and a.year='"+req.getRequest().getYear()+"' " +
                //" and a.month='"+req.getRequest().getMonth()+"' " +
                " and a.bizpartnerno='"+req.getRequest().getBizPartnerNo()+"'";
        List<Map<String, Object>> settleList = this.doQueryData(settleSql,null);

        String corp = request.getCorp();
        //查询当前法人的关账日期
        String accSql = "select a.*,to_char(a.CLOSINGDATE,'yyyyMMdd') as CLOSINGDATE  from DCP_ACOUNT_SETTING a where a.eid='" + req.geteId() + "' and a.corp='" + corp + "'  and a.ACCTTYPE='1' ";
        List<Map<String, Object>> accList = this.doQueryData(accSql, null);
        if (CollectionUtils.isEmpty(accList)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的法人账套" + corp);
        }
        String closingDate = accList.get(0).get("CLOSINGDATE").toString();
        String bDate = req.getRequest().getBdate();
        if (DateFormatUtils.compareDate(closingDate, bDate) < 0) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不得小于关账日期");
        }

        String reconNo =req.getRequest().getReconNo();
        List<DCP_ReconliationUpdateReq.ReconList> detailList = request.getReconList();
        for (DCP_ReconliationUpdateReq.ReconList detail : detailList) {

            if(items.contains(detail.getItem())){
                continue;
            }

            ColumnDataValue detailColumns=new ColumnDataValue();
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
            detailColumns.add("RDATE", DataValues.newDate(detail.getRDate()));
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
            detailColumns.add("ISINVOCEINCL", DataValues.newString(detail.getIsInvoiceIncl()));
            detailColumns.add("TAXCODE", DataValues.newString(detail.getTaxCode()));

            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean detailib=new InsBean("DCP_RECONDETAIL",detailColumnNames);
            detailib.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(detailib));

            List<Map<String, Object>> collect = settleList.stream().filter(x -> x.get("BILLNO").toString().equals(detail.getSourceNo()) && x.get("ITEM").toString().equals(detail.getSourceNoSeq())).collect(Collectors.toList());
            if (collect.size()>0) {
                Map<String, Object> sMap = collect.get(0);
                BigDecimal settleAmt = new BigDecimal(sMap.get("SETTLEAMT").toString());
                BigDecimal billAmt = new BigDecimal(sMap.get("BILLAMT").toString());
                settleAmt=settleAmt.add(new BigDecimal(detail.getCurrReconAmt()));
                BigDecimal unSettleAmt=billAmt.subtract(settleAmt);
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

            }
        }


        UptBean ub1 = new UptBean("DCP_RECONLIATION");

        ub1.addUpdateValue("BDATE", new DataValue(req.getRequest().getBdate(), Types.VARCHAR));
        ub1.addUpdateValue("RECONNO", new DataValue(req.getRequest().getReconNo(), Types.VARCHAR));
        ub1.addUpdateValue("CORP", new DataValue(req.getRequest().getCorp(), Types.VARCHAR));
        ub1.addUpdateValue("DATATYPE", new DataValue(req.getRequest().getDataType(), Types.VARCHAR));
        ub1.addUpdateValue("BIZPARTNERNO", new DataValue(req.getRequest().getBizPartnerNo(), Types.VARCHAR));
        ub1.addUpdateValue("YEAR", new DataValue(req.getRequest().getYear(), Types.VARCHAR));
        ub1.addUpdateValue("MONTH", new DataValue(req.getRequest().getMonth(), Types.VARCHAR));
        ub1.addUpdateValue("ESTRECEEXPDAY", new DataValue(req.getRequest().getEstReceExpDay(), Types.VARCHAR));
        ub1.addUpdateValue("CURRRECONAMT", new DataValue(req.getRequest().getCurrReconAmt(), Types.VARCHAR));
        ub1.addUpdateValue("CURRRECONTAXAMT", new DataValue(req.getRequest().getCurrReconTaxAmt(), Types.VARCHAR));
        ub1.addUpdateValue("CURRRECONPRETAXAMT", new DataValue(req.getRequest().getCurrReconPretaxAmt(), Types.VARCHAR));
        ub1.addUpdateValue("PAIDRECEAMT", new DataValue(req.getRequest().getPaidReceAmt(), Types.VARCHAR));
        ub1.addUpdateValue("NOTPAIDRECEAMT", new DataValue(req.getRequest().getNotPaidReceAmt(), Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("STARTDATE", new DataValue(req.getRequest().getBeginDate(), Types.VARCHAR));
        ub1.addUpdateValue("ENDDATE", new DataValue(req.getRequest().getEndDate(), Types.VARCHAR));
        ub1.addUpdateValue("CURRENCY", new DataValue(req.getRequest().getCurrency(), Types.VARCHAR));
        ub1.addUpdateValue("PAYDATENO", new DataValue(req.getRequest().getPayDateNo(), Types.VARCHAR));
        ub1.addUpdateValue("ISINVOICEINCL", new DataValue(req.getRequest().getIsInvoiceIncl(), Types.VARCHAR));
        ub1.addUpdateValue("STATUS", new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
        ub1.addUpdateValue("MODIFY_DATE", new DataValue(createDate, Types.VARCHAR));
        ub1.addUpdateValue("MODIFY_TIME", new DataValue(createTime, Types.VARCHAR));
        ub1.addUpdateValue("MODIFYBY", new DataValue(req.getOpNO(), Types.VARCHAR));



        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("RECONNO", new DataValue(reconNo, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));




        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReconliationUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReconliationUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReconliationUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReconliationUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReconliationUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ReconliationUpdateReq>(){};
    }

    @Override
    protected DCP_ReconliationUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ReconliationUpdateRes();
    }

}


