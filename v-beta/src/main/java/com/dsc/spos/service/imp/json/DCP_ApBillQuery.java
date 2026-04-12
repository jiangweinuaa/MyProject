package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ApBillQueryReq;
import com.dsc.spos.json.cust.res.DCP_ApBillQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ApBillQuery extends SPosBasicService<DCP_ApBillQueryReq, DCP_ApBillQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ApBillQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ApBillQueryReq> getRequestType() {
        return new TypeToken<DCP_ApBillQueryReq>(){};
    }

    @Override
    protected DCP_ApBillQueryRes getResponseType() {
        return new DCP_ApBillQueryRes();
    }

    @Override
    protected DCP_ApBillQueryRes processJson(DCP_ApBillQueryReq req) throws Exception {

        String nowDate = DateFormatUtils.getNowPlainDate();

        DCP_ApBillQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);

        DCP_ApBillQueryRes.level1Elm level1Elm = res.new level1Elm();
        level1Elm.setApList(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            level1Elm.setAccountId(getQData.get(0).get("ACCOUNTID").toString());
            level1Elm.setAccountName(getQData.get(0).get("ACCOUNTNAME").toString());
            BigDecimal totfCYTATAmt=new BigDecimal(0);
            BigDecimal totfCYRevAmt=new BigDecimal(0);
            BigDecimal totunPaidAmt=new BigDecimal(0);
            for (Map<String, Object> row : getQData){

                BigDecimal fcytatAmt = new BigDecimal(Check.Null(row.get("FCYTATAMT").toString())?"0":row.get("FCYTATAMT").toString());
                BigDecimal fcyrevAmt = new BigDecimal(Check.Null(row.get("FCYREVAMT").toString())?"0":row.get("FCYREVAMT").toString());
                BigDecimal unpaidAmt = fcytatAmt.subtract(fcyrevAmt);//new BigDecimal(Check.Null(row.get("UNPAIDAMT").toString())?"0":row.get("UNPAIDAMT").toString());
                String taskId = row.get("TASKID").toString();
                if(taskId.equals("8")||taskId.equals("9")){
                    fcytatAmt=fcytatAmt.multiply(BigDecimal.valueOf(-1));
                    fcyrevAmt=fcyrevAmt.multiply(BigDecimal.valueOf(-1));
                    unpaidAmt=unpaidAmt.multiply(BigDecimal.valueOf(-1));
                }
                totfCYTATAmt=totfCYTATAmt.add(fcytatAmt);
                totfCYRevAmt=totfCYRevAmt.add(fcyrevAmt);
                totunPaidAmt=totunPaidAmt.add(unpaidAmt);

                DCP_ApBillQueryRes.ApList apList = res.new ApList();
                apList.setAccountId(level1Elm.getAccountId());
                apList.setAccountName(level1Elm.getAccountName());
                apList.setBizPartnerNo(row.get("BIZPARTNERNO").toString());
                apList.setBizPartnerName(row.get("BIZPARTNERNAME").toString());
                apList.setApNo(row.get("APNO").toString());
                apList.setPDate(row.get("PDATE").toString());
                apList.setFCYTATAmt(fcytatAmt.toString());
                apList.setFCYRevAmt(fcyrevAmt.toString());
                apList.setUnPaidAmt(unpaidAmt.toString());
                apList.setStatus(row.get("STATUS").toString());
                apList.setTaskId(taskId);
                level1Elm.getApList().add(apList);
            }

            level1Elm.setTotfCYTATAmt(totfCYTATAmt.toString());
            level1Elm.setTotfCYRevAmt(totfCYRevAmt.toString());
            level1Elm.setTotunPaidAmt(totunPaidAmt.toString());
        }

        level1Elm.setTotList(new ArrayList<>());
        //查询下本期 本年数据 accountid + pdate
        String year=nowDate.substring(0,4);
        String month=nowDate.substring(4,6);
        if(Check.NotNull(req.getRequest().getEndDate())){
            year=req.getRequest().getEndDate().substring(0,4);
            month=req.getRequest().getEndDate().substring(4,6);
        }


        String allSql="select a.*,b.account as accountname" +
                " from DCP_APBILL a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid " +
                " where a.pdate>='"+year+"0101' and a.pdate<='"+year+"1231'";
        List<Map<String, Object>> getAllQData=this.doQueryData(allSql, null);


        String setSql="select a.*,c.accountid from DCP_SETTLEDATA a" +
                " left join dcp_org b on a.PAYORGNO=b.organizationno and a.eid=b.eid " +
                " left join DCP_ACOUNT_SETTING c on c.eid=a.eid and c.corp=b.corp and c.accTtype='1' " +
                " where a.eid='"+req.geteId()+"' and a.biztype='1' and nvl(a.APAMT,0)<=0 ";
        List<Map<String, Object>> getSetQData=this.doQueryData(setSql, null);
        if(getAllQData.size()>0){
            List<Map> accList = getAllQData.stream().map(x -> {
                Map acMap = new HashMap();
                acMap.put("ACCOUNTID", x.get("ACCOUNTID").toString());
                acMap.put("ACCOUNTNAME", x.get("ACCOUNTNAME").toString());
                return acMap;
            }).distinct().collect(Collectors.toList());
            for (Map acMap : accList) {
                DCP_ApBillQueryRes.ToList toList = res.new ToList();
                String accountId = acMap.get("ACCOUNTID").toString();
                String accountName = acMap.get("ACCOUNTNAME").toString();

                List<Map<String, Object>> acRows = getAllQData.stream().filter(x -> x.get("ACCOUNTID").toString().equals(accountId)).collect(Collectors.toList());

                String monthDatef=year+month+"01";
                String monthDatee=year+month+"31";
                List<Map<String, Object>> filterRows1 = acRows.stream()
                        .filter(x -> Integer.valueOf(x.get("PDATE").toString())>=Integer.valueOf(monthDatef)
                                &&Integer.valueOf(x.get("PDATE").toString())<=Integer.valueOf(monthDatee)).collect(Collectors.toList());
                //List<Map<String, Object>> filterRows2 = acRows.stream().filter(x -> x.get("TASKID").toString().equals("8") && x.get("TASKID").toString().equals("9")).collect(Collectors.toList());


                BigDecimal ytdFcytatAmt = acRows.stream().map(x -> {
                    BigDecimal fcytatAmt = new BigDecimal(Check.Null(x.get("FCYTATAMT").toString())?"0":x.get("FCYTATAMT").toString());
                    if (x.get("TASKID").toString().equals("8") || x.get("TASKID").toString().equals("9")) {
                        fcytatAmt = fcytatAmt.multiply(BigDecimal.valueOf(-1));
                    }
                    return fcytatAmt;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal ytdFcyrevAmt = acRows.stream().map(x -> {
                    BigDecimal fcyrevAmt = new BigDecimal(Check.Null(x.get("FCYREVAMT").toString())?"0":x.get("FCYREVAMT").toString());
                    if (x.get("TASKID").toString().equals("8") || x.get("TASKID").toString().equals("9")) {
                        fcyrevAmt = fcyrevAmt.multiply(BigDecimal.valueOf(-1));
                    }
                    return fcyrevAmt;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal ytdUnpaidAmt = ytdFcytatAmt.subtract(ytdFcyrevAmt);

                BigDecimal totFCYATAmt = filterRows1.stream().map(x -> {
                    BigDecimal fcytatAmt = new BigDecimal(Check.Null(x.get("FCYTATAMT").toString())?"0":x.get("FCYTATAMT").toString());
                    if (x.get("TASKID").toString().equals("8") || x.get("TASKID").toString().equals("9")) {
                        fcytatAmt = fcytatAmt.multiply(BigDecimal.valueOf(-1));
                    }
                    return fcytatAmt;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totFCYRevAmt = filterRows1.stream().map(x -> {
                    BigDecimal fcyrevAmt = new BigDecimal(Check.Null(x.get("FCYREVAMT").toString())?"0":x.get("FCYREVAMT").toString());
                    if (x.get("TASKID").toString().equals("8") || x.get("TASKID").toString().equals("9")) {
                        fcyrevAmt = fcyrevAmt.multiply(BigDecimal.valueOf(-1));
                    }
                    return fcyrevAmt;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totUnPaidAmt = totFCYATAmt.subtract(totFCYRevAmt);

                toList.setAccountId(accountId);
                toList.setAccountName(accountName);
                toList.setTotFCYATAmt(totFCYATAmt.toString());
                toList.setTotFCYRevAmt(totFCYRevAmt.toString());
                toList.setTotUnPaidAmt(totUnPaidAmt.toString());
                toList.setYTDFCYATAmt(ytdFcytatAmt.toString());
                toList.setYTDFCYRevAmt(ytdFcyrevAmt.toString());
                toList.setYTDUnPaidAmt(ytdUnpaidAmt.toString());

                //                "totCreateAP": "string"
                List<Map<String, Object>> confirmRows = filterRows1.stream().filter(x -> x.get("STATUS").toString().equals("2")).collect(Collectors.toList());
                toList.setTotConfirm(confirmRows.size() + "");
                List<Map<String, Object>> insertRows = filterRows1.stream().filter(x -> x.get("STATUS").toString().equals("1")).collect(Collectors.toList());
                toList.setTotInsert(insertRows.size() + "");

                List<Map<String, Object>> glNoFilter = confirmRows.stream().filter(x -> Check.Null(x.get("GLNO").toString())).collect(Collectors.toList());
                toList.setUngenVoucher(glNoFilter.size() + "");

                List<Map<String, Object>> settFilterRows = getSetQData.stream().filter(x -> x.get("ACCOUNTID").toString().equals(toList.getAccountId())).collect(Collectors.toList());
                toList.setTotCreateAP(settFilterRows.size() + "");

                level1Elm.getTotList().add(toList);

            }
        }

        res.setDatas(level1Elm);


        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_ApBillQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();

        DCP_ApBillQueryReq.levelRequest request = req.getRequest();
        //   "status": "0新增1审核   -1作废",
        //        "accountId": "string",
        //        "taskId": "1：采购应付单，2：预付账款单，3：其他应付单，4 ：内部应付单，5：员工报销单，6：员工借款单，7：采购暂估单；8：预付待抵单；9：员工借支待抵单",
        //        "beginDate": "string",
        //        "endDate": "string",
        //        "bizPartnerNo": "string",
        //        "isPmtOffset": "Y-是，N-否"
        String status = request.getStatus();
        String taskId = request.getTaskId();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String bizPartnerNo = request.getBizPartnerNo();
        String isPmtOffset = request.getIsPmtOffset();
        String accountId = request.getAccountId();


        StringBuffer sqlbuf=new StringBuffer();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with apbill as ("
                + " select a.APNO from DCP_APBILL a"
                + " where a.eid='"+eId+"' and a.accountid='"+accountId+"' "
        );
        if(Check.NotNull(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }
        if(Check.NotNull(taskId)){
            sqlbuf.append(" and a.taskid='"+taskId+"' ");
        }

        if(Check.NotNull(bizPartnerNo)){
            sqlbuf.append(" and a.bizpartnerno='"+bizPartnerNo+"' ");
        }
        if(Check.NotNull(beginDate)){
            sqlbuf.append(" and a.pdate>='"+beginDate+"' ");
        }
        if(Check.NotNull(endDate)){
            sqlbuf.append(" and a.pdate<='"+endDate+"' ");
        }

        if("N".equals(isPmtOffset)){
            sqlbuf.append(" and a.taskid!='8' and a.taskid!='9' ");
        }

        sqlbuf.append(" group by a.apno");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.CREATE_DATE desc,a.create_time desc) as rn,"
                + " a.*,c.sname as bizpartnername,d.account as accountname  "
                + " from DCP_APBILL a "
                + " inner join apbill b on a.apno=b.apno" +
                " left join dcp_bizpartner c on c.eid=a.eid and a.bizpartnerno=c.bizpartnerno " +
                " left join DCP_ACOUNT_SETTING d on d.eid=a.eid and d.accountid=a.accountid "
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}

