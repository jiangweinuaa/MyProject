package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ReserveItemsQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReserveItemsQueryRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 预约项目查询
 * @author: wangzyc
 * @create: 2021-07-21
 */
public class DCP_ReserveItemsQuery extends SPosBasicService<DCP_ReserveItemsQueryReq, DCP_ReserveItemsQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ReserveItemsQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveItemsQueryReq.level1Elm request = req.getRequest();
        if(request==null)
        {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if(Check.Null(request.getShopId())){
            errMsg.append("所属门店不能为空 ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveItemsQueryReq> getRequestType() {
        return new TypeToken<DCP_ReserveItemsQueryReq>(){};
    }

    @Override
    protected DCP_ReserveItemsQueryRes getResponseType() {
        return new DCP_ReserveItemsQueryRes();
    }

    @Override
    protected DCP_ReserveItemsQueryRes processJson(DCP_ReserveItemsQueryReq req) throws Exception {
        DCP_ReserveItemsQueryRes res = this.getResponseType();
        res.setDatas(new ArrayList<DCP_ReserveItemsQueryRes.level1Elm>());

        int totalRecords = 0; // 总笔数
        int totalPages = 0;

        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQdata = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(getQdata)){
                String num = getQdata.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
                condition.put("ITEMSNO", true);
                condition.put("SHOPID", true);
                //调用过滤函数
                List<Map<String, Object>> getOpNos = MapDistinct.getMap(getQdata, condition);
                condition.clear();

                for (Map<String, Object> oneData : getOpNos) {
                    DCP_ReserveItemsQueryRes.level1Elm lv1 = res.new level1Elm();
                    String itemsNo = oneData.get("ITEMSNO").toString();
                    String itemsName = oneData.get("ITEMSNAME").toString();
                    String shopid = oneData.get("SHOPID").toString();
                    String serviceTime = oneData.get("SERVICETIME").toString();
                    String status = oneData.get("STATUS").toString();
                    String shopName = oneData.get("ORG_NAME").toString();

                    lv1.setItemsNo(itemsNo);
                    lv1.setItemsName(itemsName);
                    lv1.setShopId(shopid);
                    lv1.setServiceTime(serviceTime);
                    lv1.setStatus(status);
                    lv1.setShopName(shopName);

                    lv1.setOpList(new ArrayList<DCP_ReserveItemsQueryRes.level2Elm>());
                    for (Map<String, Object> twoData : getQdata) {
                        String itemsNo2 = twoData.get("ITEMSNO").toString();
                        String shopid2 = twoData.get("SHOPID").toString();

                        if(!itemsNo2.equals(itemsNo)||!shopid.equals(shopid2)){
                            continue;
                        }
                        DCP_ReserveItemsQueryRes.level2Elm lv2 = res.new level2Elm();
                        String opNo = twoData.get("OPNO").toString();
                        String opname = twoData.get("OPNAME").toString();
                        lv2.setOpNo(opNo);
                        lv2.setOpName(opname);
                        lv1.getOpList().add(lv2);
                    }

                res.getDatas().add(lv1);
                }
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ReserveItemsQueryReq req) throws Exception {
        String sql  = "";
        StringBuffer sqlbuf = new StringBuffer("");
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        DCP_ReserveItemsQueryReq.level1Elm request = req.getRequest();
        String status = request.getStatus();
        String keyTxt = request.getKeyTxt();
        String shopId = request.getShopId();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sqlbuf.append("SELECT * FROM ( " +
                " SELECT count(DISTINCT a.EID||a.ITEMSNO||a.SHOPID) OVER () AS num, DENSE_RANK() OVER (ORDER BY a.EID, a.SHOPID, a.ITEMSNO DESC) AS rn , " +
                " a.itemsNo, b.itemsName, a.SHOPID, b.SERVICETIME, a.STATUS , c.OPNO,d.ORG_NAME,e.OPNAME " +
                "  FROM DCP_RESERVEITEMS a " +
                " LEFT JOIN DCP_SERVICEITEMS b ON a.eid = b.EID AND a.ITEMSNO = b.ITEMSNO " +
                " LEFT JOIN DCP_RESERVEADVISOR c ON a.EID = b.EID AND a.SHOPID = c.SHOPID AND a.ITEMSNO = c.ITEMSNO " +
                " left join DCP_ORG_LANG d on a.eid = d.eid and d.ORGANIZATIONNO = a.shopId and d.LANG_TYPE = '"+req.getLangType()+"'" +
                " LEFT join DCP_ADVISORSET e on a.eid = e.eid and e.OPNO = c.OPNO" +
                " WHERE a.eid = '"+req.geteId()+"'");
        if(!Check.Null(status)){
            sqlbuf.append(" and a.STATUS = '"+status+"'");
        }
        if(!Check.Null(shopId)){
            sqlbuf.append(" and a.shopid = '"+shopId+"'");
        }
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" and (a.ITEMSNO = '"+keyTxt+"' or b.itemsName like '%%"+keyTxt+"%%')");
        }

        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }
}
