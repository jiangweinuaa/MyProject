package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ReturnApplyQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReturnApplyQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DCP_ReturnApplyQuery extends SPosBasicService<DCP_ReturnApplyQueryReq, DCP_ReturnApplyQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ReturnApplyQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ReturnApplyQueryReq> getRequestType() {
        return new TypeToken<DCP_ReturnApplyQueryReq>(){};
    }

    @Override
    protected DCP_ReturnApplyQueryRes getResponseType() {
        return new DCP_ReturnApplyQueryRes();
    }

    @Override
    protected DCP_ReturnApplyQueryRes processJson(DCP_ReturnApplyQueryReq req) throws Exception {
        DCP_ReturnApplyQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            StringBuffer sJoinno=new StringBuffer("");
            for (Map<String, Object> row : getQData){
                DCP_ReturnApplyQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setBillNo(row.get("BILLNO").toString());
                level1Elm.setTotCqty(row.get("TOTCQTY").toString());
                level1Elm.setTotPqty(row.get("TOTPQTY").toString());
                level1Elm.setTotAmt(row.get("TOTAMT").toString());
                level1Elm.setTotDistriAmt(row.get("TOTDISTRIAMT").toString());
                //level1Elm.setTotAppCqty(row.get("TOTAPPCQTY").toString());
                //level1Elm.setTotAppPqty(row.get("TOTAPPPQTY").toString());
                //level1Elm.setTotAppAmt(row.get("TOTAPPAMT").toString());
                //level1Elm.setTotAppDistriAmt(row.get("TOTAPPDISTRIAMT").toString());
                level1Elm.setEmployeeId(row.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(row.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setCreateBy(row.get("CREATEOPID").toString());
                level1Elm.setCreateByName(row.get("CREATEBYNAME").toString());
                level1Elm.setCreateDeptId(row.get("CREATEDEPTID").toString());
                level1Elm.setCreateDeptName(row.get("CREATEDEPTNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setModifyBy(row.get("LASTMODIOPID").toString());
                level1Elm.setModifyByName(row.get("MODIFYBYNAME").toString());
                level1Elm.setModifyTime(row.get("LASTMODITIME").toString());
                level1Elm.setSubmitBy(row.get("SUBMITBY").toString());
                level1Elm.setSubmitByName(row.get("SUBMITBYNAME").toString());
                level1Elm.setSubmitTime(row.get("SUBMITTIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmTime(row.get("CONFIRMTIME").toString());
                level1Elm.setCancelBy(row.get("CANCELBY").toString());
                level1Elm.setCancelByName(row.get("CANCELBYNAME").toString());
                level1Elm.setCancelTime(row.get("CANCELTIME").toString());
                res.getDatas().add(level1Elm);
                sJoinno.append(level1Elm.getBillNo()+",");
            }
            //审核数量  查明细
            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("BILLNO", sJoinno.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);
            if(withasSql_mono.length()>0){
                StringBuffer sb = new StringBuffer();
                sb.append(" with p as ("+withasSql_mono+")" +
                        " select a.billno,a.distriprice,a.approvestatus," +
                        " a.approveqty,a.approveprice,a.pluno,a.featureno  " +
                        " from " +
                        " DCP_RETURNAPPLY_DETAIL a " +
                        " inner join p on a.billno=p.billno " +
                        " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.approvestatus='1' ");
                List<Map<String, Object>> list = this.doQueryData(sb.toString(), null);
                res.getDatas().forEach(x->{
                    String billNo = x.getBillNo();
                    List<Map<String, Object>> filterRows = list.stream().filter(y -> y.get("BILLNO").equals(billNo)).collect(Collectors.toList());
                    BigDecimal totAppPqty=new BigDecimal(0);
                    BigDecimal totAppAmt=new BigDecimal(0);
                    BigDecimal totAppDistriAmt=new BigDecimal(0);
                    for (int i=0;i<filterRows.size();i++){
                        Map<String, Object> sm = filterRows.get(i);
                        BigDecimal approveQty=Check.Null(sm.get("APPROVEQTY").toString())?new BigDecimal(0):new BigDecimal(sm.get("APPROVEQTY").toString());
                        BigDecimal approvePrice=Check.Null(sm.get("APPROVEPRICE").toString())?new BigDecimal(0):new BigDecimal(sm.get("APPROVEPRICE").toString());
                        BigDecimal distriPrice=Check.Null(sm.get("DISTRIPRICE").toString())?new BigDecimal(0):new BigDecimal(sm.get("DISTRIPRICE").toString());


                        totAppPqty=totAppPqty.add(approveQty);
                        totAppAmt=totAppAmt.add(approveQty.multiply(approvePrice));
                        totAppDistriAmt=totAppDistriAmt.add(distriPrice.multiply(approveQty));

                    }
                    x.setTotAppPqty(totAppPqty.toString());
                    x.setTotAppAmt(totAppAmt.toString());
                    x.setTotAppDistriAmt(totAppDistriAmt.toString());

                    List<Map> collect = filterRows.stream().map(y -> {
                        Map map = new HashMap();
                        map.put("pluno", y.get("PLUNO").toString());
                        map.put("featureno", y.get("FEATURENO").toString());
                        return map;
                    }).distinct().collect(Collectors.toList());
                    x.setTotAppCqty(String.valueOf(collect.size()));

                });

            }


        }

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
    protected String getQuerySql(DCP_ReturnApplyQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();


        String status = req.getRequest().getStatus();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with returnapply as ("
                + " select a.billno  from DCP_RETURNAPPLY a "
                + " where a.eid='"+eId+"' and a.organizationno='"+ req.getOrganizationNO()+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.billno like '%"+keyTxt+"%' "
                    + " ) ");
        }
        if(Check.NotNull(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        if (!Check.Null(beginDate)){
            sqlbuf.append(" and a.bdate>='"+beginDate+"' ");
        }

        if (!Check.Null(endDate)){
            sqlbuf.append(" and a.bdate<='"+endDate+"' ");
        }

        sqlbuf.append(" group by a.billno");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.status,a.bdate,a.billno,a.totCqty,a.totpqty,a.totamt,a.totdistriamt," +
                " a.totCqty  totappCqty,a.totpqty totapppqty,a.totamt totappamt,a.totdistriamt totappdistriamt,"
                + " a.CREATEOPID ,e1.op_name as createbyname,a.LASTMODIOPID,e2.op_name as modifybyname,a.createTime,a.LASTMODITIME,a.departId,d1.departname as departNAME,a.confirmby,a.confirmtime,e3.op_name as confirmbyname,"
                + " a.cancelby,e4.op_name as cancelbyname,a.SUBMITBY,e6.op_name as SUBMITBYNAME,a.canceltime,a.SUBMITTIME,a.employeeid,e0.name as employeename,a.memo,a.CREATEDEPTID,d2.departname as createdeptname "
                + " from DCP_RETURNAPPLY a "
                + " inner join returnapply b on a.BILLNO=b.billno "
                + " left join dcp_employee e0 on e0.eid=a.eid and e0.employeeno=a.EMPLOYEEID "
                + " left join platform_staffs_lang e1 on e1.eid=a.eid and e1.opno=a.CREATEOPID and e1.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e2 on e2.eid=a.eid and e2.opno=a.LASTMODIOPID and e2.lang_type='"+langType+"' "
                + " left join platform_staffs_lang e3 on e3.eid=a.eid and e3.opno=a.CONFIRMBY and e3.lang_type='"+langType+"' "
                + " left join platform_staffs_lang e4 on e4.eid=a.eid and e4.opno=a.cancelby and e4.lang_type='"+langType+"' "
//                + " left join dcp_employee e5 on e5.eid=a.eid and e5.employeeno=a.CLOSEBY "
                + " left join platform_staffs_lang e6 on e6.eid=a.eid and e6.opno=a.submitby  and e6.lang_type='"+langType+"' "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " left join dcp_department_lang d2 on d2.eid=a.eid and d2.departno=a.createdeptid and d2.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}



