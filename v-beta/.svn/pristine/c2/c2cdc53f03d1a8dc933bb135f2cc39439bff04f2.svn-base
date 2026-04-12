package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProductionOrderDetailQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ProductionOrderDetailQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_ProductionOrderDetailQuery_Open\DCP_ProductionOrderDetailQuery
 *   說明：生产订单商品查询
 * 服务说明：生产订单商品查询
 * @author wangzyc
 * @since  2021-4-19
 */
public class DCP_ProductionOrderDetailQuery_Open extends SPosBasicService<DCP_ProductionOrderDetailQuery_OpenReq, DCP_ProductionOrderDetailQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_ProductionOrderDetailQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        DCP_ProductionOrderDetailQuery_OpenReq.level1Elm request = req.getRequest();
        if(request==null)
        {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getEId()))
        {
            errCt++;
            errMsg.append("企业编码不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getMachShopNo()))
        {
            errCt++;
            errMsg.append("生产机构编码不可为空值, ");
            isFail = true;
        }
//        if (Check.Null(request.getStallId()))
//        {
//            errCt++;
//            errMsg.append("档口编号不可为空值, ");
//            isFail = true;
//        }
        if (CollectionUtils.isEmpty(request.getGoodsList()))
        {
            errCt++;
            errMsg.append("商品列表不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ProductionOrderDetailQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_ProductionOrderDetailQuery_OpenReq>(){};
    }

    @Override
    protected DCP_ProductionOrderDetailQuery_OpenRes getResponseType() {
        return new DCP_ProductionOrderDetailQuery_OpenRes();
    }

    @Override
    protected DCP_ProductionOrderDetailQuery_OpenRes processJson(DCP_ProductionOrderDetailQuery_OpenReq req) throws Exception {
        DCP_ProductionOrderDetailQuery_OpenRes res = null;
        res = this.getResponse();
        res.setDatas(new ArrayList<DCP_ProductionOrderDetailQuery_OpenRes.level1Elm>());
        String sql = "";
//        int totalRecords = 0;                                //总笔数
//        int totalPages = 0;
        try {
             sql = this.getGoodsList(req);
            List<Map<String, Object>> getGoodsList = this.doQueryData(sql, null);

            // 过滤
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("PROCESSTASKNO", true);
            condition.put("CREATEDATETIME", true);
            condition.put("PLUNO", true);
            condition.put("FEATURENO", true);
            condition.put("PLUBARCODE", true);
            condition.put("SUNIT", true);
            // 调用过滤函数
            List<Map<String, Object>> getMemos = MapDistinct.getMap(getGoodsList, condition);

            if(!CollectionUtils.isEmpty(getGoodsList)){
//                String num = getGoodsList.get(0).get("NUM").toString();
//                totalRecords = Integer.parseInt(num);
//                totalPages = totalRecords / req.getPageSize();
//                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> goods : getGoodsList) {
                    DCP_ProductionOrderDetailQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                    String processtaskno = goods.get("PROCESSTASKNO").toString(); // 订单号
                    String ofNo = goods.get("OFNO").toString(); // 来源订单号
                    String loadDocType = goods.get("LOADDOCTYPE").toString(); // 订单号
                    String createDateTime = goods.get("CREATEDATETIME").toString();

                    String pluno = goods.get("PLUNO").toString();

                    // Add 2021/6/4 增加商品名称逻辑  如果生产订单明细中商品名称 不存在 则商品名称从DCP_GOODS_LANG 中获取
                    String plu_name = goods.get("PLU_NAME").toString();
                    String pluName = goods.get("PLUNAME").toString();

                    String featureNo = goods.get("FEATURENO").toString();
                    String pluBarcode = goods.get("PLUBARCODE").toString();
                    String featureName = goods.get("FEATURENAME").toString();
                    String sunit = goods.get("SUNIT").toString();
                    String uName = goods.get("UNAME").toString();
                    String specName = goods.get("SPEC").toString();
                    String pQty = goods.get("PQTY").toString();
                    String oQty = goods.get("OQTY").toString();
                    String goodsStatus = goods.get("GOODSSTATUS").toString();

                    String contman = goods.get("CONTMAN").toString();
                    String conttel = goods.get("CONTTEL").toString();
                    String getman = goods.get("GETMAN").toString();
                    String getmantel = goods.get("GETMANTEL").toString();
                    String address = goods.get("ADDRESS").toString();
                    String shipDate = goods.get("SHIPDATE").toString();
                    String shipStartTime = goods.get("SHIPSTARTTIME").toString();
                    String shipEndTime = goods.get("SHIPENDTIME").toString();
                    String omemo = goods.get("OMEMO").toString();

                    String Item = goods.get("ITEM").toString();
                    String oItem = goods.get("OITEM").toString();

                    String maxitem = goods.get("MAXITEM").toString();


                    lv1.setOrderNo(ofNo);
                    lv1.setLoadDocType(loadDocType);
                    lv1.setCreateDatetime(createDateTime);
                    lv1.setOItem(oItem);
                    lv1.setItem(Item);
                    lv1.setPluNo(pluno);
                    lv1.setProQty(maxitem);

                    // Add 2021/6/4 增加商品名称逻辑  如果生产订单明细中商品名称 不存在 则商品名称从DCP_GOODS_LANG 中获取
                    if(Check.Null(pluName)){
                        lv1.setPluName(plu_name);
                    }else{
                        lv1.setPluName(pluName);
                    }

                    lv1.setPluBarcode(pluBarcode);
                    lv1.setFeatureNo(featureNo);
                    lv1.setFeatureName(featureName);
                    lv1.setSUnit(sunit);
                    lv1.setSUnitName(uName);
                    lv1.setSpecName(specName);
                    lv1.setQty(pQty);
                    lv1.setOQty(oQty);
                    lv1.setGoodsStatus(goodsStatus);

                    lv1.setMessages(new ArrayList<DCP_ProductionOrderDetailQuery_OpenRes.level2Elm>());
                    if (!CollectionUtils.isEmpty(getMemos)) {
                        for (Map<String, Object> getMemo : getMemos) {
                            DCP_ProductionOrderDetailQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                            String processtaskno1 = getMemo.get("PROCESSTASKNO").toString();
                            String ofNo1 = getMemo.get("OFNO").toString();
                            String createdatetime1 = getMemo.get("CREATEDATETIME").toString();
                            String pluno1 = getMemo.get("PLUNO").toString();
                            String featureno1 = getMemo.get("FEATURENO").toString();
                            String plubarcode1 = getMemo.get("PLUBARCODE").toString();
                            String sunit1 = getMemo.get("SUNIT").toString();

                            // 过滤此单据商品的明细
                            if (processtaskno1.equals(processtaskno) && createdatetime1.equals(createDateTime) && pluno.equals(pluno1)&& ofNo.equals(ofNo1)) {
                                if (featureno1.equals(featureNo) && plubarcode1.equals(pluBarcode) && sunit.equals(sunit1)) {
                                    String memoType = getMemo.get("MEMOTYPE").toString();
                                    String memoName = getMemo.get("MEMONAME").toString();
                                    String memo = getMemo.get("MEMO").toString();
                                    if (Check.Null(memoType) && Check.Null(memoName) && Check.Null(memo)) {
                                        continue;
                                    }
                                    lv2.setMessage(memo);
                                    lv2.setMsgType(Check.Null(memoType) == true ? "text" : memoType);
                                    lv2.setMsgName(memoName);
                                    lv1.getMessages().add(lv2);
                                }
                            }
                        }
                    }

                    DCP_ProductionOrderDetailQuery_OpenRes.level3Elm lv3 = res.new level3Elm();

                    lv3.setContMan(contman);
                    lv3.setContTel(conttel);
                    lv3.setGetMan(getman);
                    lv3.setGetManTel(getmantel);
                    lv3.setAddress(address);
                    lv3.setShipDate(shipDate);
                    lv3.setShipStartTime(shipStartTime);
                    lv3.setShipEndTime(shipEndTime);


                    lv1.setShipInfo(lv3);
                    lv1.setMemo(omemo);
                    res.getDatas().add(lv1);
                }
            }

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
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
    protected String getQuerySql(DCP_ProductionOrderDetailQuery_OpenReq req) throws Exception {
        return null;
    }

    /**
     * 查询单份的商品列表
     *
     * @param req
     * @return
     */
    public String getGoodsList(DCP_ProductionOrderDetailQuery_OpenReq req) {
        DCP_ProductionOrderDetailQuery_OpenReq.level1Elm request = req.getRequest();
        String machShopNo = request.getMachShopNo();// 生产门店
        List<DCP_ProductionOrderDetailQuery_OpenReq.level2Elm> goodsList = request.getGoodsList(); // 商品列表
        //計算起啟位置
        String langType = req.getLangType();
        String stallId = request.getStallId();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append("  " +
                " SELECT count(*) OVER () AS num, ROW_NUMBER() OVER (ORDER BY a.PROCESSTASKNO, b.ITEM, b.PLUNO) AS rn , " +
                " a.PROCESSTASKNO, a.LOADDOCTYPE,a.memo AS omemo,a.CREATEDATETIME, a.OFNO,b.ITEM,b.OITEM, b.PLUNO,b.PLUNAME, c.PLU_NAME , b.FEATURENO, d.PLUBARCODE, e.FEATURENAME, f.SUNIT," +
                " g.UNAME , b.PQTY, h.SPEC, i.MEMOTYPE, i.MEMONAME, i.MEMO , b.GOODSSTATUS, a.CONTMAN, a.CONTTEL, a.GETMAN, a.GETMANTEL, " +
                " a.ADDRESS, a.SHIPDATE, a.SHIPSTARTTIME, a.SHIPENDTIME , b.OQTY ,j.maxItem" +
                " FROM DCP_PROCESSTASK a" +
                " LEFT JOIN DCP_PROCESSTASK_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO " +
                " LEFT JOIN DCP_GOODS_LANG c ON a.EID = b.EID AND b.PLUNO = c.PLUNO AND c.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_GOODS_BARCODE d ON a.EID = b.EID AND b.PLUNO = d.PLUNO AND b.FEATURENO = d.FEATURENO AND b.PUNIT = d.UNIT AND d.STATUS = '100' " +
                " LEFT JOIN DCP_GOODS_FEATURE_LANG e ON a.EID = e.EID AND e.PLUNO = b.PLUNO AND b.FEATURENO = e.FEATURENO AND e.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_GOODS f ON a.EID = b.EID AND b.PLUNO = f.PLUNO AND f.STATUS = '100' " +
                " LEFT JOIN DCP_UNIT_LANG g ON a.EID = g.EID AND f.SUNIT = g.UNIT AND g.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_GOODS_UNIT_LANG h ON a.eid = h.eid AND b.pluno = h.pluno AND b.punit = h.OUNIT AND h.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_ORDER_DETAIL_MEMO i ON a.EID = i.EID AND a.OFNO = i.ORDERNO AND b.OITEM = i.OITEM  " +
                " LEFT JOIN ( SELECT PROCESSTASKNO, EID,SHOPID,count(item) AS MAXITEM FROM DCP_PROCESSTASK_DETAIL GROUP BY PROCESSTASKNO,EID,SHOPID ) j ON a.PROCESSTASKNO = j.PROCESSTASKNO  AND a.EID  = j.eid AND a.SHOPID  = j.SHOPID" +
                " WHERE a.EID = '"+req.geteId()+"' AND a.SHOPID = '"+machShopNo+"' ");

        if(!CollectionUtils.isEmpty(goodsList)){
            sqlbuf.append("AND (");
            for (DCP_ProductionOrderDetailQuery_OpenReq.level2Elm level2Elm : goodsList) {
                sqlbuf.append(" (a.OFNO = '"+level2Elm.getOrderNo()+"' ");
                if(!Check.Null(level2Elm.getOItem())){
                    sqlbuf.append(" and b.OITEM = '"+level2Elm.getOItem()+"'");
                }
                if(!Check.Null(level2Elm.getItem())){
                    sqlbuf.append(" and b.ITEM = '"+level2Elm.getItem()+"'");
                }
                sqlbuf.append(" )OR");
            }
            sqlbuf.deleteCharAt(sqlbuf.length()-1);
            sqlbuf.deleteCharAt(sqlbuf.length()-1);
            sqlbuf.append(")");
        }

        if(!Check.Null(stallId)){
            sqlbuf.append(" AND b.STALLID = '"+stallId+"'");
        }

        sqlbuf.append(" ORDER BY a.SHIPDATE DESC , a.PROCESSTASKNO ,b.ITEM ");
        sql = sqlbuf.toString();
        return sql;
    }
}
