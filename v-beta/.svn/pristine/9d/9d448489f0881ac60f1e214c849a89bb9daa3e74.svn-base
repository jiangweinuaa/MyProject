package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_APDocCreateReq;
import com.dsc.spos.json.cust.req.DCP_ApPrepayProcessReq;
import com.dsc.spos.json.cust.res.DCP_ApPrepayProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ApPrepayProcess extends SPosAdvanceService<DCP_ApPrepayProcessReq, DCP_ApPrepayProcessRes> {

    @Override
    protected void processDUID(DCP_ApPrepayProcessReq req, DCP_ApPrepayProcessRes res) throws Exception {

        String eId = req.geteId();
        String bdate = req.getRequest().getBdate();
        String corp = req.getRequest().getCorp();
        String taskId = req.getRequest().getTaskId();
        String purOrderNo = req.getRequest().getPurOrderNo();
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());


        String accSql="select a.*,to_char(a.APCLOSINGDATE,'yyyyMMdd') as apclosingdates  from DCP_ACOUNT_SETTING a where a.eid='"+eId+"' and a.corp='"+corp+"'  and a.ACCTTYPE='1' ";
        List<Map<String, Object>> accList = this.doQueryData(accSql, null);
        if(accList.size()>0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的账套");
        }
        String accountId = accList.get(0).get("ACCOUNTID").toString();
        String apClosingDate = accList.get(0).get("APCLOSINGDATES").toString();
        if(Integer.valueOf(apClosingDate)<Integer.valueOf(bdate)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不得小于应付关账日期");
        }
        //数据源 DCP_PURORDER_PAY
        String sql="select a.*,b.supplier,b.payee,c.payee as defaultpayee,b.paydateno,b.taxcode,b.taxrate," +
                "  " +
                " from DCP_PURORDER_PAY a " +
                " inner join dcp_purorder b on a.eid=b.eid and a.organizationno=b.organizationno and a.purorderno=b.purorderno " +
                " left join dcp_bizpartner c on c.eid=a.eid and c.bizpartnerno=b.supplier " +
                " where a.eid='"+eId+"' and b.corp='"+corp+"' and b.bdate='"+bdate+"'" +
                " ";
        if(Check.NotNull(purOrderNo)){
            sql+=" and a.purorderno='"+purOrderNo+"' ";
        }
        List<Map<String, Object>> sourceList = this.doQueryData(sql, null);
        if(sourceList.size()>0){

            //应付科目数据
            String apSubjectSql="select * from DCP_APSETUPSUBJECT a where a.eid='"+eId+"' and a.accountid='"+accountId+"' ";
            List<Map<String, Object>> apSubjectList = this.doQueryData(apSubjectSql, null);
            String feeSubjectSql="select * from DCP_FEESUBJECT a where a.eid='"+eId+"' and a.accountid='"+accountId+"' ";
            List<Map<String, Object>> feeSubjectList = this.doQueryData(feeSubjectSql, null);

            List<String> purOrderNos = sourceList.stream().map(x -> x.get("PURORDERNO").toString()).distinct().collect(Collectors.toList());
            for (String thisPurorderNo : purOrderNos){
                List<Map<String, Object>> singleSourceList = sourceList.stream().filter(x -> x.get("PURORDERNO").toString().equals(thisPurorderNo)).collect(Collectors.toList());

                String apNo = this.getApNo(req);

                Map<String, Object> sm = singleSourceList.get(0);
                String organizationNo = sm.get("ORGANIZATIONNO").toString();
                String supplier = sm.get("SUPPLIER").toString();
                String payee = sm.get("PAYEE").toString();
                if(Check.Null(payee)) {
                    payee=sm.get("DEFAULTPAYEE").toString();
                }
                String payDateNo = sm.get("PAYDATENO").toString();
                String taxCode = sm.get("TAXCODE").toString();
                String taxRate = sm.get("TAXRATE").toString();

                String apSubjectId="";
                String feeSubjectId="";
                List<Map<String, Object>> filterApSubs = apSubjectList.stream().filter(x -> x.get("SETUPTYPE").toString().equals("2")).collect(Collectors.toList());
                if(filterApSubs.size()>0){
                    apSubjectId = filterApSubs.get(0).get("APSUBJECTID").toString();
                }
                List<Map<String, Object>> filterFeeSubs = feeSubjectList.stream().filter(x -> x.get("SETUPTYPE").toString().equals("2")).collect(Collectors.toList());
                if(filterFeeSubs.size()>0){
                    feeSubjectId = filterFeeSubs.get(0).get("FEESUBJECTID").toString();
                }


                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID", DataValues.newString(eId));
                mainColumns.add("STATUS", DataValues.newString("1"));
                mainColumns.add("CORP", DataValues.newString(corp));
                mainColumns.add("ACCOUNTID", DataValues.newString(accountId));
                mainColumns.add("APNO", DataValues.newString(apNo));
                mainColumns.add("PDATE", DataValues.newDate(bdate));
                mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                mainColumns.add("APTYPE", DataValues.newString("04"));//采购预付单
                mainColumns.add("ACCEMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                mainColumns.add("BIZPARTNERNO", DataValues.newString(supplier));
                mainColumns.add("RECEIVER", DataValues.newString(payee));
                mainColumns.add("TASKID", DataValues.newString(taskId));
                mainColumns.add("PAYDATENO", DataValues.newString(payDateNo));
                //mainColumns.add("PAYDUEDATE", DataValues.newDate(request.getPayDueDate()));
                mainColumns.add("TAXCODE", DataValues.newString(taxCode));
                mainColumns.add("TAXRATE", DataValues.newDecimal(taxRate));
                mainColumns.add("INCLTAX", DataValues.newString(""));
                //mainColumns.add("APPLICANT", DataValues.newString(request.getApplicant()));
                mainColumns.add("EMPLOYEENO", DataValues.newString(""));
                mainColumns.add("DEPARTNO", DataValues.newString(""));
                mainColumns.add("SOURCETYPE", DataValues.newString("5"));//采购单
                mainColumns.add("SOURCENO", DataValues.newString(thisPurorderNo));
                mainColumns.add("PENDOFFSETNO", DataValues.newString(""));
                mainColumns.add("FEESUBJECTID", DataValues.newString(feeSubjectId));
                mainColumns.add("APSUBJECTID", DataValues.newString(apSubjectId));
                mainColumns.add("GLNO", DataValues.newString(""));
                mainColumns.add("GRPPMTNO", DataValues.newString(""));
                mainColumns.add("MEMO", DataValues.newString(""));
                //mainColumns.add("payList", DataValues.newString("1"));
                //mainColumns.add("CURRENCY", DataValues.newString(request.getCurrency()));
                //mainColumns.add("EXRATE", DataValues.newDecimal(request.getExRate()));
                //mainColumns.add("FCYBTAMT", DataValues.newDecimal(request.getFCYBTAmt()));
                //mainColumns.add("FCYTAMT", DataValues.newDecimal(request.getFCYTAmt()));
                mainColumns.add("FCYREVAMT", DataValues.newDecimal(""));
                //mainColumns.add("FCYTATAMT", DataValues.newDecimal(request.getFCYTATAmt()));
                //mainColumns.add("LCYBTAMT", DataValues.newDecimal(request.getLCYBTAmt()));
                //mainColumns.add("LCYTAMT", DataValues.newDecimal(request.getLCYTAmt()));
                mainColumns.add("LCYREVAMT", DataValues.newDecimal(""));
                //mainColumns.add("LCYTATAMT", DataValues.newDecimal(request.getLCYTATAmt()));
                mainColumns.add("FCYPMTAMT", DataValues.newDecimal(""));
                mainColumns.add("LCYPMTAMT", DataValues.newDecimal(""));

                mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
                mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
                mainColumns.add("CREATE_TIME", DataValues.newString(createTime));

                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_APBILL",mainColumnNames);
                ib.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib));

            }
        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApPrepayProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApPrepayProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApPrepayProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApPrepayProcessReq req) throws Exception {
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
    protected TypeToken<DCP_ApPrepayProcessReq> getRequestType() {
        return new TypeToken<DCP_ApPrepayProcessReq>() {
        };
    }

    @Override
    protected DCP_ApPrepayProcessRes getResponseType() {
        return new DCP_ApPrepayProcessRes();
    }

    private String getApNo(DCP_ApPrepayProcessReq req) throws Exception{
        String apNo = "";
        String preFix="";
        //1：采购应付单，2：预付账款单，3：其他应付单，4 ：内部应付单，5：员工报销单，6：员工借款单，
        // 7：采购暂估单；8：预付待抵单；9：员工借支待抵单，10-应付核销单
        switch (req.getRequest().getTaskId()) {
            case "1":
                preFix = "APZK";
                break;
            case "2":
                preFix = "APYF";
                break;
            case "3":
                preFix = "APQT";
                break;
            case "4":
                preFix = "APNB";
                break;
            case "5":
                preFix = "APBX";
                break;
            case "6":
                preFix = "APJK";
                break;
            case "7":
                preFix = "APZG";
                break;
            case "8":
                preFix = "APDD";
                break;
            case "9":
                preFix = "AOJZ";
                break;
            case "10":
                preFix = "APHX";
                break;
        }

        apNo=this.getOrderNO(req, preFix);

        return apNo;
    }

}

