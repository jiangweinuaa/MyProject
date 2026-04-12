package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ServiceItemsDetailQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ServiceItemsDetailQuery_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @description: 服务项目详情查询
 * @author: wangzyc
 * @create: 2021-07-29
 */
public class DCP_ServiceItemsDetailQuery_Open extends SPosBasicService<DCP_ServiceItemsDetailQuery_OpenReq, DCP_ServiceItemsDetailQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_ServiceItemsDetailQuery_OpenReq req) throws Exception {
        StringBuffer errMsg = new StringBuffer("");
        DCP_ServiceItemsDetailQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getItemsNo())) {
            errMsg.append("项目编号不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ServiceItemsDetailQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_ServiceItemsDetailQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_ServiceItemsDetailQuery_OpenRes getResponseType() {
        return new DCP_ServiceItemsDetailQuery_OpenRes();
    }

    @Override
    protected DCP_ServiceItemsDetailQuery_OpenRes processJson(DCP_ServiceItemsDetailQuery_OpenReq req) throws Exception {
        DCP_ServiceItemsDetailQuery_OpenRes res = this.getResponseType();

        String apiUserCode = req.getApiUserCode();
        String langType = req.getLangType();
        String eId = "";              //从apiUserCode 查询得到企业编号
        String appType = "";          //从apiUserCode 查询得到应用类型
        String channelId = "";        //从apiUserCode 查询得到渠道编码
        String shopId = "";
//        String requestId = req.getRequestId();
//        if (Check.Null(requestId))
//            requestId = UUID.randomUUID().toString();

        try {
            //以下是云洋在基类里面进行赋值  20200915
            eId = req.geteId();
            appType = req.getApiUser().getAppType();
            channelId = req.getApiUser().getChannelId();
            shopId = req.getApiUser().getShopId();
            if (Check.Null(eId))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:" + apiUserCode + " 在crm_apiuser表中未查询到对应的eId");
            if (Check.Null(appType))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:" + apiUserCode + " 在crm_apiuser表中未查询到对应的appType");
            if (Check.Null(channelId))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:" + apiUserCode + " 在crm_apiuser表中未查询到对应的channelId");

            ///会员服务地址
//            String promUrl = PosPub.getPARA_SMS(dao, eId, "", "PromUrl");
//            if (Check.Null(promUrl))
//                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "参数PromUrl未设置 ");

            String sql = this.getQuerySql(req);
            res.setDatas(res.new level1Elm());
            List<Map<String, Object>> datas = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(datas)) {
                ////图片地址参数获取
                String isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
                String httpStr = isHttps.equals("1") ? "https://" : "http://";
                String domainName = PosPub.getPARA_SMS(this.dao, eId, "", "DomainName");
                String imagePath = "";
                if (domainName.endsWith("/")) {
                    imagePath = httpStr + domainName + "resource/image/";
                } else {
                    imagePath = httpStr + domainName + "/resource/image/";
                }

                Map<String, Object> itemData = datas.get(0);

                String itemsno = itemData.get("PLUNO").toString();
                String itemsname = itemData.get("ITEMSNAME").toString();
                String plutype = itemData.get("PLUTYPE").toString();
                String listimage = itemData.get("LISTIMAGE").toString();
                if (!Check.Null(listimage)) listimage = imagePath + listimage;
                String servicetime = itemData.get("SERVICETIME").toString();
                String coupontypeid = itemData.get("COUPONTYPEID").toString();
                String qty = itemData.get("QTY").toString();
                String serviceintroduction = itemData.get("SERVICEINTRODUCTION").toString();
                String servicenote = itemData.get("SERVICENOTE").toString();
                String memo = itemData.get("MEMO").toString();

                String reservetype = itemData.get("RESERVETYPE").toString();
                String price = itemData.get("PRICE").toString();
                String vipprice = itemData.get("VIPPRICE").toString();
                String cardprice = itemData.get("CARDPRICE").toString();

                DCP_ServiceItemsDetailQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                lv1.setItemsNo(itemsno);
                lv1.setItemsName(itemsname);
                lv1.setPluType(plutype);
                lv1.setServiceTime(servicetime);
                lv1.setMemo(memo);
                lv1.setServiceIntroduction(serviceintroduction);
                lv1.setServiceNote(servicenote);
                lv1.setCouponType(coupontypeid);
                lv1.setQty(qty);
                lv1.setListImage(listimage);


                //调用基础促销价计算   促销接口未返回价格时，后端返回空给前端，库存数为空时返回0给前端
                MyCommon comm = new MyCommon();
//                List<Map<String, Object>> getBasicProm = comm.getBasicProm(apiUserCode, req.getApiUser().getUserKey(), req.getLangType(), requestId, req.getApiUser().getCompanyId(), shopId, "", "", promUrl, datas);
//                if (getBasicProm == null || getBasicProm.isEmpty()) {
//                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "会员基础促销价(PROM_BasicPromotionCalc_Open)调用失败 ");
//                }
//                String originalPrice = "";
////                String price = "";
//                for (Map<String, Object> oneBasicProm : getBasicProm) {
//                    String pluNo = oneBasicProm.get("PLUNO").toString();
//                    String pluType = oneBasicProm.get("PLUTYPE").toString();
//                    if (pluNo.equals(itemsno) && pluType.equals(pluType)) {
//                        originalPrice = oneBasicProm.get("ORIGINALPRICE").toString();
////                        price = oneBasicProm.get("PRICE").toString();
//                        break;
//                    }
//                }
                // 一般零售价更改逻辑  改为取零售价模板
                String originalPrice = "0";
                sql = "select PLUNO,PUNIT,BASEUNIT,SUNIT from DCP_GOODS where EID = '" + eId + "' and PLUNO = '" + itemsno + "'";
                List<Map<String, Object>> getPunit = this.doQueryData(sql, null);
                if(!CollectionUtils.isEmpty(getPunit)){
                    String baseUnit = getPunit.get(0).get("BASEUNIT").toString();
                    String sUnit = getPunit.get(0).get("SUNIT").toString();
                    BigDecimal unitRatio = new BigDecimal(1);
                    List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
                            itemsno, sUnit);
                    if (!CollectionUtils.isEmpty(getQData_Ratio)) {
                        unitRatio = (BigDecimal) getQData_Ratio.get(0).get("UNIT_RATIO");
                    }

                    List<Map<String, Object>> pluList = new ArrayList<>();
                    Map<String, Object> plumap = new HashMap<>();
                    plumap.put("PLUNO", itemsno);
                    plumap.put("PUNIT", sUnit);
                    plumap.put("BASEUNIT", baseUnit);
                    plumap.put("UNITRATIO", unitRatio);
                    pluList.add(plumap);
                    List<Map<String, Object>> getPluPrice = comm.getSalePrice_distriPrice(dao, eId, req.getBELFIRM(), shopId, pluList, req.getBELFIRM());

                    Map<String, Object> condiV = new HashMap<>();
                    condiV.put("PLUNO", itemsno); //订单上的商品编码
                    condiV.put("PUNIT", sUnit); //订单上的商品单位
                    List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPluPrice, condiV, false);

                    if (!CollectionUtils.isEmpty(priceList)) {
                        originalPrice = priceList.get(0).get("PRICE").toString(); //零售价
                    }
                }

                lv1.setPrice(price);
                lv1.setOriPrice(originalPrice);
                lv1.setReserveType(reservetype);
                lv1.setVipPrice(vipprice);
                lv1.setCardPrice(cardprice);

                sql = getDetailcomponents(eId, appType, itemsno);
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);

                lv1.setDetailcomponents(new ArrayList<DCP_ServiceItemsDetailQuery_OpenRes.level3Elm>());
                if (getQData != null && getQData.isEmpty() == false) {
                    int item = 1;
                    for (Map<String, Object> oneData : getQData) {
                        DCP_ServiceItemsDetailQuery_OpenRes.level3Elm lv2 = res.new level3Elm();
                        String serialNo = oneData.get("ITEM").toString();
                        String type = oneData.get("TYPE").toString();
                        String content = "";  ///CLOB类型
                        String detailimage = oneData.get("DETAILIMAGE").toString();
                        try {
                            if (oneData.get("CONTENT") != null && !Check.Null(oneData.get("CONTENT").toString())) {
                                content = oneData.get("CONTENT").toString();
                            }
                        } catch (Exception e) {
                            content = "";
                        }

                        ////type = TEST,OUTSRC时,取content , type=IMAGE ,VIDEO取DETAILIMAGE
                        if (type.equals("IMAGE") || type.equals("VIDEO")) {
                            content = imagePath + detailimage;
                        }


                        lv2.setContent(content);
                        lv2.setItem(item + "");
                        lv2.setType(type);
                        lv1.getDetailcomponents().add(lv2);
                        item++;
                    }
                }

                sql = this.getGoodsProdImage(eId, itemsno);
                List<Map<String, Object>> getProdImages = this.doQueryData(sql, null);
                lv1.setProdImage(new ArrayList<DCP_ServiceItemsDetailQuery_OpenRes.level2Elm>());
                if (!CollectionUtils.isEmpty(getProdImages)) {
                    for (Map<String, Object> getProdImage : getProdImages) {
                        DCP_ServiceItemsDetailQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                        String prodimage = getProdImage.get("PRODIMAGE").toString();
                        if (!Check.Null(prodimage)) {
                            prodimage = imagePath + prodimage;
                        }
                        lv2.setProdImage(prodimage);
                        lv1.getProdImage().add(lv2);
                    }

                } else {
                    if (!Check.Null(listimage)) {
                        // 找不到产品图 则把商品图填充进去
                        DCP_ServiceItemsDetailQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                        lv2.setProdImage(listimage);
                        lv1.getProdImage().add(lv2);
                    }
                }
                res.setDatas(lv1);

            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ServiceItemsDetailQuery_OpenReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT a.ITEMSNO as pluno, a.ITEMSNAME, b.PLUTYPE, c.LISTIMAGE, a.SERVICETIME , a.COUPONTYPEID, a.QTY, a.SERVICEINTRODUCTION,a.PRICE,a.VIPPRICE,a.CARDPRICE,a.RESERVETYPE, " +
                " a.SERVICENOTE, a.MEMO,b.sunit as unitId  " +
                " FROM DCP_SERVICEITEMS a " +
                " LEFT JOIN DCP_GOODS b ON a.EID = b.EID AND a.ITEMSNO = b.PLUNO  and b.status='100'" +
                " LEFT JOIN DCP_GOODSIMAGE c ON a.EID = c.EID AND a.ITEMSNO = c.PLUNO AND c.APPTYPE = 'ALL' " +
                " WHERE a.eid = '" + req.geteId() + "' AND a.ITEMSNO = '" + req.getRequest().getItemsNo() + "' and a.STATUS = '100' ");
        sql = sqlbuf.toString();
        return sql;
    }

    private String getDetailcomponents(String eId, String appType, String mallGoodsId) throws Exception {

        String sql = "";
        StringBuffer sb = new StringBuffer();
        sb.append(""
                + " select pluno,item,type,content,detailimage from ("
                + " select a.*,row_number() over (partition by pluno,item order by indx ) as rn from "
                + " ("
                + " select a.pluno,a.item,a.type,a.content,a.detailimage,1 as indx from dcp_goodsimage_detailimage a"
                + " where a.eid='" + eId + "' and a.apptype='" + appType + "' and a.pluno='" + mallGoodsId + "'"
                + " union all"
                + " select a.pluno,a.item,a.type,a.content,a.detailimage,2 as indx from dcp_goodsimage_detailimage a"
                + " where a.eid='" + eId + "' and a.apptype='ALL' and a.pluno='" + mallGoodsId + "'"
                + " ) a"
                + " ) where rn='1' order by pluno,item"
                + " ");

        sql = sb.toString();
        return sql;
    }

    private String getGoodsProdImage(String eId, String mallGoodsId) throws Exception {

        String sql = "";
        StringBuffer sb = new StringBuffer();
        sb.append("select * from DCP_GOODSIMAGE_PRODIMAGE where eid = '" + eId + "' and PLUNO = '" + mallGoodsId + "' and APPTYPE = 'ALL'");
        sql = sb.toString();
        return sql;
    }
}
