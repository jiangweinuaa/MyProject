package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_WMSPGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_WMSPGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_WMSPGoodsQuery  extends SPosBasicService<DCP_WMSPGoodsQueryReq, DCP_WMSPGoodsQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_WMSPGoodsQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_WMSPGoodsQueryReq> getRequestType() {
        return new TypeToken<DCP_WMSPGoodsQueryReq>(){};
    }

    @Override
    protected DCP_WMSPGoodsQueryRes getResponseType() {
        return new DCP_WMSPGoodsQueryRes();
    }

    @Override
    protected DCP_WMSPGoodsQueryRes processJson(DCP_WMSPGoodsQueryReq req) throws Exception {
        DCP_WMSPGoodsQueryRes res =this.getResponseType();
        int totalRecords = 0; //总笔数
        int totalPages = 0;
        res.setDatas(new ArrayList<>());
        try {
            String sql = getQuerySql(req);
            List<Map<String, Object>> datas = this.doQueryData(sql, null);
            //单头主键字段
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("ID", true);
            //调用过滤函数
            List<Map<String, Object>> getQHeader = MapDistinct.getMap(datas, condition);
            if (!CollectionUtils.isEmpty(getQHeader)) {
                String num = getQHeader.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);

                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                for (Map<String, Object> data : getQHeader) {
                    DCP_WMSPGoodsQueryRes.level1Elm lv1 = res.new level1Elm();
                    String id = data.get("ID").toString();
                    String name = data.get("NAME").toString();
                    String status = data.get("STATUS").toString();
                    String createopid = data.get("CREATEOPID").toString();
                    String createopname = data.get("CREATEOPNAME").toString();
                    String createtime = data.get("CREATETIME").toString();
                    String lastmodiopid = data.get("LASTMODIOPID").toString();
                    String lastmodiopname = data.get("LASTMODIOPNAME").toString();
                    String lastmoditime = data.get("LASTMODITIME").toString();


                    lv1.setId(id);
                    lv1.setName(name);
                    lv1.setStatus(status);
                    lv1.setCreateopid(createopid);
                    lv1.setCreateopname(createopname);
                    lv1.setCreatetime(createtime);
                    lv1.setLastmodiopid(lastmodiopid);
                    lv1.setLastmodiopname(lastmodiopname);
                    lv1.setLastmoditime(lastmoditime);
                    res.getDatas().add(lv1);
                }
            }

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

        }
        catch (Exception e)
        {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_WMSPGoodsQueryReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        DCP_WMSPGoodsQueryReq.level1Elm request = req.getRequest();
        String keyTxt = "";
        String status = "";
        if (request!=null)
        {
            keyTxt = request.getKeyTxt();
            status = request.getStatus();
        }

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sqlbuf.append("SELECT * FROM ( " +
                " SELECT count(DISTINCT a.ID) OVER() num, DENSE_RANK() OVER (ORDER BY A.ID) rn, a.* " +
                " FROM DCP_WMSPGOODS a" +
                " LEFT JOIN DCP_WMSPGOODS_SPEC b ON a.EID = b.EID AND a.ID = b.ID " +
                " WHERE a.eid = '" + req.geteId() + "'");
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" AND (a.NAME LIKE '%%" + keyTxt + "%%' OR B.SPECID LIKE '%%" + keyTxt + "%%' OR B.SPECNAME LIKE '%%"+keyTxt+"%%' )");
        }
        if (!Check.Null(status)) {
            sqlbuf.append(" AND a.STATUS = '" +status + "'");
        }
        sqlbuf.append(" ) where rn>" + startRow + " and rn<=" + (startRow + pageSize));
        sql = sqlbuf.toString();
        return sql;
    }
}
