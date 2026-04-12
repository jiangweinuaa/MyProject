package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ReserveItemsQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ReserveItemsQuery_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 预约项目查询
 * @author: wangzyc
 * @create: 2021-07-28
 */
public class DCP_ReserveItemsQuery_Open extends SPosBasicService<DCP_ReserveItemsQuery_OpenReq, DCP_ReserveItemsQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_ReserveItemsQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveItemsQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveItemsQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_ReserveItemsQuery_OpenReq>(){};
    }

    @Override
    protected DCP_ReserveItemsQuery_OpenRes getResponseType() {
        return new DCP_ReserveItemsQuery_OpenRes();
    }

    @Override
    protected DCP_ReserveItemsQuery_OpenRes processJson(DCP_ReserveItemsQuery_OpenReq req) throws Exception {
        DCP_ReserveItemsQuery_OpenRes res  = this.getResponseType();
        res.setDatas(new ArrayList<DCP_ReserveItemsQuery_OpenRes.level1Elm>());

        int totalRecords = 0; // 总笔数
        int totalPages = 0;

        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> datas = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(datas)){
                String num = datas.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }
                for (Map<String, Object> data : datas) {
                    String itemsno = data.get("ITEMSNO").toString();
                    String itemsname = data.get("ITEMSNAME").toString();
                    String listimage = data.get("LISTIMAGE").toString();
                    String servicetime = data.get("SERVICETIME").toString();
                    String serviceintroduction = data.get("SERVICEINTRODUCTION").toString();
                    String shopDistribution = data.get("SHOPDISTRIBUTION").toString();
                    String couponType = data.get("COUPONTYPEID").toString();
                    String qty = data.get("QTY").toString();
                    String reserveType = data.get("RESERVETYPE").toString();
                    String price = data.get("PRICE").toString();
                    String vipPrice = data.get("VIPPRICE").toString();
                    String cardPrice = data.get("CARDPRICE").toString();
                    if (!Check.Null(listimage)){
                        listimage = domainName+listimage;
                    }
                    if(Check.Null(shopDistribution)){
                        shopDistribution = "Y"; // 默认到店分配为Y
                    }
                    DCP_ReserveItemsQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setItemsNo(itemsno);
                    lv1.setImageUrl(listimage);
                    lv1.setItemsName(itemsname);
                    lv1.setServiceTime(servicetime);
                    lv1.setServiceIntroduction(serviceintroduction);
                    lv1.setShopDistribution(shopDistribution);
                    lv1.setCouponType(couponType);
                    lv1.setQty(qty);

                    lv1.setReserveType(reserveType);
                    lv1.setPrice(price);
                    lv1.setVipPrice(vipPrice);
                    lv1.setCardPrice(cardPrice);

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
    protected String getQuerySql(DCP_ReserveItemsQuery_OpenReq req) throws Exception {
        String sql = "";
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        DCP_ReserveItemsQuery_OpenReq.level1Elm request = req.getRequest();
        String keyTxt = request.getKeyTxt();
        String isCouponType = request.getIsCouponType();

        String [] reserveTypes = request.getReserveType();
        String reserveType = "";
        if(reserveTypes != null && reserveTypes.length >0){
            reserveType =  PosPub.getArrayStrSQLIn(reserveTypes);
        }

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT * FROM ( " +
                " SELECT count(a.ITEMSNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY a.ITEMSNO) AS rn , a.ITEMSNO,b.COUPONTYPEID,b.qty, b.ITEMSNAME, c.LISTIMAGE, b.SERVICETIME, b.SERVICEINTRODUCTION,d.SHOPDISTRIBUTION,b.RESERVETYPE,b.PRICE,b.VIPPRICE,b.CARDPRICE " +
                " FROM DCP_RESERVEITEMS a " +
                " LEFT JOIN DCP_SERVICEITEMS b ON a.EID = b.EID AND a.ITEMSNO = b.ITEMSNO " +
                " LEFT JOIN dcp_goodsimage c ON a.eid = c.eid AND c.pluno = a.ITEMSNO AND c.apptype = 'ALL' " +
                " LEFT JOIN DCP_RESERVEPARAMETER d ON a.eid = d.eid and a.SHOPID = d.SHOPID " +
                " where a.EID = '"+req.geteId()+"' and a.SHOPID  = '"+request.getShopId()+"' and a.status = '100'" );
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" and (a.ITEMSNO = '"+keyTxt+"' OR b.ITEMSNAME like '%%"+keyTxt+"%%')");
        }
        if("Y".equals(isCouponType)){
            sqlbuf.append(" AND b.COUPONTYPEID is not null");
        }else if("N".equals(isCouponType)){
            sqlbuf.append(" AND b.COUPONTYPEID is  null");
        }
        if(!Check.Null(reserveType)){
            sqlbuf.append("and b.RESERVETYPE in ("+reserveType+") ");
        }
        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }
}
