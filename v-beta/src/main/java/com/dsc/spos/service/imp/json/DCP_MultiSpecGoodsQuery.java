package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_MultiSpecGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_MultiSpecGoodsQuery extends SPosBasicService<DCP_MultiSpecGoodsQueryReq,DCP_MultiSpecGoodsQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_MultiSpecGoodsQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_MultiSpecGoodsQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_MultiSpecGoodsQueryReq>(){};
    }

    @Override
    protected DCP_MultiSpecGoodsQueryRes getResponseType() {
        // TODO Auto-generated method stub

        return new DCP_MultiSpecGoodsQueryRes();
    }

    @Override
    protected DCP_MultiSpecGoodsQueryRes processJson(DCP_MultiSpecGoodsQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        DCP_MultiSpecGoodsQueryRes res = this.getResponse();
        //获取当前登陆用户的语言类型
        String langType_cur = req.getLangType();
        //单头总数
        sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        int totalRecords;								//总笔数
        int totalPages;
        res.setDatas(new ArrayList<DCP_MultiSpecGoodsQueryRes.level1Elm>());
        if (getQData != null && getQData.isEmpty() == false)
        {

            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("MASTERPLUNO", true);
            //调用过滤函数
            List<Map<String, Object>> catDatas=MapDistinct.getMap(getQData, condition);

            for (Map<String, Object> oneData : catDatas)
            {
                DCP_MultiSpecGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
                String masterPluNo = oneData.get("MASTERPLUNO").toString();
                String masterPluName  = oneData.get("MASTERPLUNAME").toString();
                String memo  = oneData.get("MEMO").toString();
                String status  = oneData.get("STATUS").toString();
                oneLv1.setMasterPluNo(masterPluNo);
                oneLv1.setMasterPluName(masterPluName);
                oneLv1.setMemo(memo);
                oneLv1.setStatus(status);
                oneLv1.setMinPrice(oneData.get("MINPRICE").toString());
                oneLv1.setMaxPrice(oneData.get("MAXPRICE").toString());
                res.getDatas().add(oneLv1);

            }


        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_MultiSpecGoodsQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String eId = req.geteId();
        String keyTxt = null;
        String status = null;
        if(req.getRequest()!=null)
        {
            keyTxt = req.getRequest().getKeyTxt();
            status = req.getRequest().getStatus();
        }
        String langtype=req.getLangType();
        if(langtype==null||langtype.isEmpty())
        {
            langtype="zh_CN";
        }
        //后面说了 不分页，先留着万一又说要分页呢
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //分页起始位置
        int startRow=(pageNumber-1) * pageSize;


        String sql = "";
        sql +=" select * from (" ;
        sql +=" SELECT COUNT(DISTINCT a.masterpluno ) OVER() NUM ,dense_rank() over(ORDER BY a.masterpluno) rn,a. * ";
        sql += " FROM (";
        sql += " select a.*,b.masterpluname,b.lang_type from DCP_MSPECGOODS a ";
        sql += " left join DCP_MSPECGOODS_Lang b on a.eid=b.eid and a.masterpluno=b.masterpluno and b.lang_type='"+langtype+"' ";
        sql += " where a.eid='"+eId+"'";
        if (keyTxt != null && keyTxt.length()>0)
        {
            sql +=" AND (a.masterpluno like '%%"+keyTxt+"%%' or b.masterpluname LIKE '%%"+keyTxt+"%%' )";
        }
        if(status!=null && status.length()>0)
        {
            sql +=" and a.status='" + status + "' ";
        }

        sql += " ) a ";

        sql +=" )  WHERE rn >  "+startRow+" and rn <= "+(startRow + pageSize) ;

        return sql;
    }

}
