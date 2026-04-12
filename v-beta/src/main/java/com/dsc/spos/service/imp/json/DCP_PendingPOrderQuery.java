package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_PendingPOrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_PendingPOrderQueryRes;
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

public class DCP_PendingPOrderQuery  extends SPosBasicService<DCP_PendingPOrderQueryReq, DCP_PendingPOrderQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_PendingPOrderQueryReq req) throws Exception {
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
    protected TypeToken<DCP_PendingPOrderQueryReq> getRequestType() {
        return new TypeToken<DCP_PendingPOrderQueryReq>(){};
    }

    @Override
    protected DCP_PendingPOrderQueryRes getResponseType() {
        return new DCP_PendingPOrderQueryRes();
    }

    @Override
    protected DCP_PendingPOrderQueryRes processJson(DCP_PendingPOrderQueryReq req) throws Exception {
        DCP_PendingPOrderQueryRes res = this.getResponse();
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

            String orderNos=getQData.stream().map(x->x.get("PORDERNO").toString()).distinct().collect(Collectors.joining(","))+",";
            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("PORDERNO", orderNos.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

            String detailSql=" with p as ("+withasSql_mono+") " +
                    " select c.*,d.isadd from dcp_porder a " +
                    " inner join p on p.porderno=a.porderno" +
                    " inner join dcp_porder_detail c on a.eid=c.eid and a.organizationno=c.organizationno and a.porderno=c.porderno " +
                    " inner join dcp_demand d on d.eid=a.eid and d.orderno=a.porderno and d.item=c.item  " +
                    " where a.eid='"+req.geteId()+"' ";
            List<Map<String, Object>> getQDetail=this.doQueryData(detailSql, null);


            for (Map<String, Object> row : getQData){
                DCP_PendingPOrderQueryRes.DatasLevel datasLevel = res.new DatasLevel();

                datasLevel.setPOrderNo(row.get("PORDERNO").toString());
                datasLevel.setBDate(row.get("BDATE").toString());
                datasLevel.setRDate(row.get("RDATE").toString());
                datasLevel.setOrganizationNo(row.get("ORGANIZATIONNO").toString());
                datasLevel.setOrganizationName(row.get("ORGANIZATIONNAME").toString());
                datasLevel.setPTemplateNo(row.get("PTMPLNO").toString());
                datasLevel.setPTemplateName(row.get("PTMPLNAME").toString());
                datasLevel.setIsUrgent(row.get("ISURGENT").toString());
                datasLevel.setIsAdd(row.get("ISADD").toString());
                datasLevel.setPreDays(Integer.parseInt(row.get("PREDAYS").toString()));

                List<Map<String, Object>> collect = getQDetail.stream().filter(
                        x -> x.get("PORDERNO").toString().equals(datasLevel.getPOrderNo())
                                && x.get("ISADD").toString().equals(datasLevel.getIsAdd())).collect(Collectors.toList());

                List<String> pfs = collect.stream().map(x -> x.get("PLUNO").toString() + "|" + x.get("FEATURENO").toString()).distinct().collect(Collectors.toList());
                datasLevel.setTotCqty(pfs.size());

                BigDecimal totPQty=new BigDecimal(0);
                BigDecimal totDistriAmt=new BigDecimal(0);
                BigDecimal totAmt=new BigDecimal(0);

                for (Map<String, Object> x:collect){
                    BigDecimal pqty = new BigDecimal(x.get("PQTY").toString());
                    totPQty=totPQty.add(pqty);
                    BigDecimal distriAmt = new BigDecimal(x.get("DISTRIAMT").toString());
                    totDistriAmt=totDistriAmt.add(distriAmt);
                    BigDecimal amt = new BigDecimal(x.get("AMT").toString());
                    totAmt=totAmt.add(amt);
                }
                datasLevel.setTotPqty(totPQty);
                datasLevel.setTotDistriAmt(totDistriAmt);
                datasLevel.setTotAmt(totAmt);
                res.getDatas().add(datasLevel);

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
    protected String getQuerySql(DCP_PendingPOrderQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""+
                 " with porder as ("+
                 " select distinct a.porderno " +
                 " from dcp_porder a " +
                 " left join dcp_demand b on a.eid=b.eid and a.porderno=b.orderno and a.organizationno=b.organizationno "+
                 " where a.eid='"+eId+"' and a.status='6' and a.SUPPLIERTYPE='FACTORY' " +
                 " AND a.RECEIPT_ORG='"+req.getOrganizationNO()+"' " +
                " and b.CLOSESTATUS='0' "
        );

        if("0".equals(req.getRequest().getGetType())){
            sqlbuf.append(" and a.PQTY-nvl(a.NOQTY,0)>0 ");
        }
        else if("1".equals(req.getRequest().getGetType())){
            sqlbuf.append(" and a.PQTY-nvl(a.STOCKOUTQTY,0)>0 ");
        }

        if("bDate".equals(req.getRequest().getDateType())){
            if(Check.NotNull(req.getRequest().getBeginDate())){
                sqlbuf.append(" and a.bDate>='"+req.getRequest().getBeginDate()+"' ");
            }
            if(Check.NotNull(req.getRequest().getEndDate())){
                sqlbuf.append(" and a.bDate<='"+req.getRequest().getEndDate()+"' ");
            }
        }
        else if("rDate".equals(req.getRequest().getDateType())){
            if(Check.NotNull(req.getRequest().getBeginDate())){
                sqlbuf.append(" and a.rDate>='"+req.getRequest().getBeginDate()+"' ");
            }
            if(Check.NotNull(req.getRequest().getEndDate())){
                sqlbuf.append(" and a.rDate<='"+req.getRequest().getEndDate()+"' ");
            }
        }

        if(CollUtil.isNotEmpty(req.getRequest().getOrgList())){
            String collect = req.getRequest().getOrgList().stream().map(x -> "'" + x + "'").distinct().collect(Collectors.joining(","));
            sqlbuf.append(" and a.organizationno in ("+collect+") ");
        }


        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.porderno like '%"+keyTxt+"%' or a.PTEMPLATENO like '%"+keyTxt+"%'"
                    + " ) ");
        }
        sqlbuf.append(" group by a.porderno");
        sqlbuf.append(" )");



        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,a.* from ( "
                + " SELECT DISTINCT a.porderno,a.bdate,a.rdate,c.org_name as organizationname,d.ptemplate_name as ptemplatename,a.ISURGENTORDER as ISURGENT,e.isadd,d.PRE_DAY as PREDAY "
                + " from dcp_porder a"
                + " inner join porder b on a.porderno=b.porderno " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.organizationno and c.lang_type='"+req.getLangType()+"' " +
                " left join DCP_PTEMPLATE d on d.eid=a.eid and d.ptemplateno=a.ptemplateno and d.doc_type='0' " +
                " left join dcp_demand e on e.eid=a.eid and e.orderno=a.porderno "
                + " where a.eid='"+eId+"' " +
                " ) a "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}



