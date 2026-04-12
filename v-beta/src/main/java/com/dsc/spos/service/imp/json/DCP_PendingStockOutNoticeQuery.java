package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_PendingStockOutNoticeQueryReq;
import com.dsc.spos.json.cust.res.DCP_PendingStockOutNoticeQueryRes;
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

public class DCP_PendingStockOutNoticeQuery extends SPosBasicService<DCP_PendingStockOutNoticeQueryReq, DCP_PendingStockOutNoticeQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_PendingStockOutNoticeQueryReq req) throws Exception {
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
    protected TypeToken<DCP_PendingStockOutNoticeQueryReq> getRequestType() {
        return new TypeToken<DCP_PendingStockOutNoticeQueryReq>(){};
    }

    @Override
    protected DCP_PendingStockOutNoticeQueryRes getResponseType() {
        return new DCP_PendingStockOutNoticeQueryRes();
    }

    @Override
    protected DCP_PendingStockOutNoticeQueryRes processJson(DCP_PendingStockOutNoticeQueryReq req) throws Exception {
        DCP_PendingStockOutNoticeQueryRes res = this.getResponse();
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
    protected String getQuerySql(DCP_PendingStockOutNoticeQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""+
                " with stockoutNotice as ("+
                " select distinct a.BILLNO " +
                " from DCP_STOCKOUTNOTICE a " +
                " left join DCP_STOCKOUTNOTICE_DETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO "+
                " where a.eid='"+eId+"' and a.status='1' and b.status='1' " +
                " AND a.DELIVERORGNO='"+req.getOrganizationNO()+"' and a.billtype='3' " +
                " and b.CLOSESTATUS='0' " +
                " and b.pqty-nvl(b.STOCKOUTQTY,0)>0 "
        );


        if("bDate".equals(req.getRequest().getDateType())){
            if(Check.NotNull(req.getRequest().getBeginDate())){
                sqlbuf.append(" and a.bDate>='"+req.getRequest().getBeginDate()+"' ");
            }
            if(Check.NotNull(req.getRequest().getEndDate())){
                sqlbuf.append(" and a.bDate<='"+req.getRequest().getEndDate()+"' ");
            }
        }
        else if("deliveryDate".equals(req.getRequest().getDateType())){
            if(Check.NotNull(req.getRequest().getBeginDate())){
                sqlbuf.append(" and a.deliveryDate>='"+req.getRequest().getBeginDate()+"' ");
            }
            if(Check.NotNull(req.getRequest().getEndDate())){
                sqlbuf.append(" and a.deliveryDate<='"+req.getRequest().getEndDate()+"' ");
            }
        }

        if(CollUtil.isNotEmpty(req.getRequest().getObjectID())){
            String objStr = req.getRequest().getObjectID().stream().distinct().map(x -> "'" + x + "'").collect(Collectors.joining(","));
            sqlbuf.append(" and a.organizationno in ("+objStr+")");
        }



        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.billno like '%"+keyTxt+"%'"
                    + " ) ");
        }
        sqlbuf.append(" group by a.billno");
        sqlbuf.append(" )");

        //todo

        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,a.* from ( "
                + " SELECT DISTINCT a.billno,a.bdate,a.ofno,a.rdate,a.DELIVERYDATE "
                + " from DCP_STOCKOUTNOTICE a"
                + " inner join stockoutNotice b on a.billno=b.billno "
                + " where a.eid='"+eId+"' " +
                " ) a "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}



