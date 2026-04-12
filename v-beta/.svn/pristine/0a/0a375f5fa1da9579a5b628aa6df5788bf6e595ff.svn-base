package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ReturnApplyProQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReturnApplyProQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ReturnApplyProQuery extends SPosBasicService<DCP_ReturnApplyProQueryReq, DCP_ReturnApplyProQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ReturnApplyProQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ReturnApplyProQueryReq> getRequestType() {
        return new TypeToken<DCP_ReturnApplyProQueryReq>(){};
    }

    @Override
    protected DCP_ReturnApplyProQueryRes getResponseType() {
        return new DCP_ReturnApplyProQueryRes();
    }

    @Override
    protected DCP_ReturnApplyProQueryRes processJson(DCP_ReturnApplyProQueryReq req) throws Exception {
        DCP_ReturnApplyProQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;

        if(req.getPageNumber()==0){
            req.setPageNumber(1);
        }

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
                DCP_ReturnApplyProQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setBillNo(row.get("BILLNO").toString());
                level1Elm.setTotCqty(row.get("TOTCQTY").toString());
                level1Elm.setTotPqty(row.get("TOTPQTY").toString());
                level1Elm.setTotAmt(row.get("TOTAMT").toString());
                level1Elm.setTotDistriAmt(row.get("TOTDISTRIAMT").toString());
                level1Elm.setOrgNo(row.get("ORGNO").toString());
                level1Elm.setOrgName(row.get("ORGNAME").toString());
                level1Elm.setStatusCount(new ArrayList<>());

                res.getDatas().add(level1Elm);

                sJoinno.append(level1Elm.getBillNo()+",");
            }

            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("BILLNO", sJoinno.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);
            if(Check.NotNull(withasSql_mono)){
                String detailSql=" with p as ("+withasSql_mono+")" +
                        " select a.billno,a.APPROVESTATUS,a.item from DCP_RETURNAPPLY_DETAIL a" +
                        " inner join p on a.billno=p.billno ";
                List<Map<String, Object>> getQData_detail=this.doQueryData(detailSql, null);
                for(DCP_ReturnApplyProQueryRes.level1Elm l1:res.getDatas()){
                    String billNo = l1.getBillNo();
                    List<Map<String, Object>> filterRows = getQData_detail.stream().filter(x -> x.get("BILLNO").toString().equals(billNo)).collect(Collectors.toList());
                    List<String> statusList = filterRows.stream().map(x -> x.get("APPROVESTATUS").toString()).distinct().collect(Collectors.toList());
                    for(String status:statusList){
                        DCP_ReturnApplyProQueryRes.StatusCount level2Elm = res.new StatusCount();
                        level2Elm.setAppStatus(status);
                        List<Map<String, Object>> filterRows2 = filterRows.stream().filter(x -> x.get("APPROVESTATUS").toString().equals(status)).collect(Collectors.toList());
                        level2Elm.setQty(String.valueOf(filterRows2.size()));
                        l1.getStatusCount().add(level2Elm);
                    }
                }
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
    protected String getQuerySql(DCP_ReturnApplyProQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();

        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        String approveStatus = req.getRequest().getApproveStatus();
        String approveOrgNo = req.getRequest().getApproveOrgNo();
        String returnType = req.getRequest().getReturnType();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        //DCP_RETURNAPPLY_DETAIL.ORGANIZATIONNO申请组织=当前登录组织（token里获取)-getType="0或null申请组织查询"
        //DCP_RETURNAPPLY_DETAIL.APPROVEORGNO核准组织=当前登录组织（token里获取)-getType="1核准组织查询"

        sqlbuf.append(""
                + " with returnapply as ("
                + " select distinct a.billno from DCP_RETURNAPPLY  a" +
                " left join DCP_RETURNAPPLY_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.billno=b.billno " +
                " left join dcp_goods_lang c on b.eid=c.eid and b.pluno=c.pluno and c.lang_type='"+req.getLangType()+"'"
                + " where a.eid='"+eId+"' " +
                " and a.status!='0' "
        );

        if(Check.Null(req.getRequest().getGetType())||"0".equals(req.getRequest().getGetType())){
            sqlbuf.append(" and b.organizationno='"+ req.getOrganizationNO()+"' ");
        }else if("1".equals(req.getRequest().getGetType())){
            sqlbuf.append(" and b.approveorgno='"+ req.getOrganizationNO()+"' ");
        }
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.billno like '%"+keyTxt+"%' "
                    + " or b.plubarcode like '%"+keyTxt+"%'" +
                    " or b.pluno like '%"+keyTxt+"%'" +
                    " or c.plu_name like '%"+keyTxt+"%' ) ");
        }
        if(!Check.Null(approveStatus)){
            sqlbuf.append(" and b.approvestatus='"+approveStatus+"'");
        }
        if(!Check.Null(approveOrgNo)){
            sqlbuf.append(" and b.approveorgno='"+approveOrgNo+"'");
        }
        if(!Check.Null(returnType)){
            sqlbuf.append(" and b.returntype='"+returnType+"'");
        }

        if (!Check.Null(beginDate)){
            sqlbuf.append(" and a.bdate>='"+beginDate+"' ");
        }

        if (!Check.Null(endDate)){
            sqlbuf.append(" and a.bdate<='"+endDate+"' ");
        }

        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.status,a.bdate,a.billno,a.totCqty,a.totpqty,a.totamt,a.totdistriamt,a.organizationno as orgno,c.org_name as orgname "
                + " from DCP_RETURNAPPLY a"
                + " inner join returnapply b on a.billno=b.billno "
                + " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.organizationno and c.lang_type='"+req.getLangType()+"'"
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}



