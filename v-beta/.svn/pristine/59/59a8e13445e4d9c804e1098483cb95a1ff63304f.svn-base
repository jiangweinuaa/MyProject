package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ServiceItemsQueryReq;
import com.dsc.spos.json.cust.res.DCP_ServiceItemsQueryRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 服务项目查询
 * @author: wangzyc
 * @create: 2021-07-21
 */
public class DCP_ServiceItemsQuery extends SPosBasicService<DCP_ServiceItemsQueryReq, DCP_ServiceItemsQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ServiceItemsQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ServiceItemsQueryReq> getRequestType() {
        return new TypeToken<DCP_ServiceItemsQueryReq>(){};
    }

    @Override
    protected DCP_ServiceItemsQueryRes getResponseType() {
        return new DCP_ServiceItemsQueryRes();
    }

    @Override
    protected DCP_ServiceItemsQueryRes processJson(DCP_ServiceItemsQueryReq req) throws Exception {
        DCP_ServiceItemsQueryRes res = this.getResponseType();
        res.setDatas(new ArrayList<DCP_ServiceItemsQueryRes.level1Elm>());
        int totalRecords = 0; // 总笔数
        int totalPages = 0;


        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> data = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(data)){
                String num = data.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> oneData : data) {
                    DCP_ServiceItemsQueryRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setItemsNo(oneData.get("ITEMSNO").toString());
                    lv1.setItemsType(oneData.get("ITEMSTYPE").toString());
                    lv1.setItemsName(oneData.get("ITEMSNAME").toString());
                    lv1.setServiceTime(oneData.get("SERVICETIME").toString());
                    lv1.setCouponTypeId(oneData.get("COUPONTYPEID").toString());
                    lv1.setQty(oneData.get("QTY").toString());
                    lv1.setServiceIntroduction(oneData.get("SERVICEINTRODUCTION").toString());
                    lv1.setServiceNote(oneData.get("SERVICENOTE").toString());
                    lv1.setMemo(oneData.get("MEMO").toString());
                    lv1.setStatus(oneData.get("STATUS").toString());

                    // 新增reserveType、price、vipPrice、cardPrice； By wangzyc
                    lv1.setReserveType(oneData.get("RESERVETYPE").toString());
                    lv1.setPrice(oneData.get("PRICE").toString());
                    lv1.setVipPrice(oneData.get("VIPPRICE").toString());
                    lv1.setCardPrice(oneData.get("CARDPRICE").toString());

                    res.getDatas().add(lv1);
                }
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ServiceItemsQueryReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        String keyTxt = req.getRequest().getKeyTxt();
        String status = req.getRequest().getStatus();
        String eId = req.geteId();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sqlbuf.append("select * from( SELECT count(DISTINCT ITEMSNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY ITEMSNO ) AS rn," +
                " ITEMSNO,ITEMSTYPE,ITEMSNAME,SERVICETIME,COUPONTYPEID,QTY,SERVICEINTRODUCTION,SERVICENOTE," +
                " MEMO,STATUS,RESERVETYPE,PRICE,VIPPRICE,CARDPRICE  FROM DCP_SERVICEITEMS where eid = '"+eId+"'");
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" and (ITEMSNO = '"+keyTxt+"' OR ITEMSNAME like '%%"+keyTxt+"%%')");
        }
        if(!Check.Null(status)){
            sqlbuf.append(" and status = '"+status+"'");
        }

        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }
}
