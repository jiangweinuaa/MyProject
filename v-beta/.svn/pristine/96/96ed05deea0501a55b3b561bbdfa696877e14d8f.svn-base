package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProductionOrderQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ProductionOrderQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_ProductionOrderQuery_Open
 * 說明：生产订单查询
 * 服务说明：生产订单查询
 *
 * @author wangzyc
 * @since 2021-4-19
 */
public class DCP_ProductionOrderQuery_Open extends SPosBasicService<DCP_ProductionOrderQuery_OpenReq, DCP_ProductionOrderQuery_OpenRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProductionOrderQuery_OpenReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ProductionOrderQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_ProductionOrderQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_ProductionOrderQuery_OpenRes getResponseType() {
        return new DCP_ProductionOrderQuery_OpenRes();
    }

    @Override
    protected DCP_ProductionOrderQuery_OpenRes processJson(DCP_ProductionOrderQuery_OpenReq req) throws Exception {
        DCP_ProductionOrderQuery_OpenRes res = null;
        res = this.getResponse();
        String sql = "";
        DCP_ProductionOrderQuery_OpenRes.level1Elm level1Elm = res.new level1Elm();
        int totalRecords = 0;                                //总笔数
        int totalPages = 0;
        try {
            // 查询档口范围的标签
            sql = this.getStallPluno(req);
            List<Map<String, Object>> plunos = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(plunos)) {
                // 查询 已完成 未完成 订单数
                sql = this.getOrderCount(req, plunos);
                List<Map<String, Object>> orderCount = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(orderCount)) {
                    String num1 = orderCount.get(0).get("NUM1").toString(); // 已完成
                    String num2 = orderCount.get(0).get("NUM2").toString(); // 未完成
                    level1Elm.setOrderNum(num1);
                    level1Elm.setUOrderNum(num2);

                    // 查询商品状态数
                    level1Elm.setGoodsNum(new ArrayList<DCP_ProductionOrderQuery_OpenRes.level2Elm>());
                    for (int i = 0; i < 3; i++) {
                        sql = this.getPlunoStatusCount(req, plunos, i + "");
                        List<Map<String, Object>> plunoStatusCount = this.doQueryData(sql, null);
                        if (!CollectionUtils.isEmpty(plunoStatusCount)) {
                            DCP_ProductionOrderQuery_OpenRes.level2Elm level2Elm = res.new level2Elm();
                            level2Elm.setGoodsStatus(i + "");
                            level2Elm.setGoodsNum(plunoStatusCount.get(0).get("NUM").toString());
                            level1Elm.getGoodsNum().add(level2Elm);
                        }
                    }

                    level1Elm.setGoodsList(new ArrayList<DCP_ProductionOrderQuery_OpenRes.level3Elm>());
                    sql = this.getGoodsList(req, plunos);
                    List<Map<String, Object>> goodsLists = this.doQueryData(sql, null);

                    // 过滤
                    Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                    condition.put("PROCESSTASKNO", true);
                    condition.put("CREATEDATETIME", true);
                    condition.put("PLUNO", true);
                    condition.put("FEATURENO", true);
                    condition.put("PLUBARCODE", true);
                    condition.put("SUNIT", true);
                    condition.put("ITEM", true);
                    condition.put("OITEM", true);
                    // 调用过滤函数
                    List<Map<String, Object>> getMemos = MapDistinct.getMap(goodsLists, condition);

                    if (!CollectionUtils.isEmpty(getMemos)) {
//                        int item = 0;

                        String num = getMemos.get(0).get("NUM").toString();
                        totalRecords = Integer.parseInt(num);
                        totalPages = totalRecords / req.getPageSize();
                        totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                        for (Map<String, Object> goods : getMemos) {
//                            item++;
                            DCP_ProductionOrderQuery_OpenRes.level3Elm level3Elm = res.new level3Elm();
                            String processtaskno = goods.get("PROCESSTASKNO").toString(); // 订单号
                            String ofNo = goods.get("OFNO").toString(); // 来源订单号
                            String loadDocType = goods.get("LOADDOCTYPE").toString(); // 来源单击类型
                            String createDateTime = goods.get("CREATEDATETIME").toString();

                            String proQty = goods.get("MAXITEM").toString();

                            String pluno = goods.get("PLUNO").toString();

                            // Add 2021/6/4 增加商品名称逻辑  如果生产订单明细中商品名称 不存在 则商品名称从DCP_GOODS_LANG 中获取
                            String pluName = goods.get("PLUNAME").toString();
                            String plu_name = goods.get("PLU_NAME").toString();

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
                            String orderMemo = goods.get("OMEMO").toString(); // 单据备注
                            String refundReasonName = goods.get("REFUNDREASONNAME").toString();

                            String Item = goods.get("ITEM").toString();
                            String oItem = goods.get("OITEM").toString();


                            level3Elm.setOrderNo(ofNo);
                            level3Elm.setLoadDocType(loadDocType);
                            level3Elm.setCreateDatetime(createDateTime);
                            level3Elm.setOItem(oItem);
                            level3Elm.setItem(Item);
                            level3Elm.setProQty(proQty);
                            level3Elm.setPluNo(pluno);

                            // Add 2021/6/4 增加商品名称逻辑  如果生产订单明细中商品名称 不存在 则商品名称从DCP_GOODS_LANG 中获取
                            if(Check.Null(pluName)){
                                level3Elm.setPluName(plu_name);
                            }else{
                                level3Elm.setPluName(pluName);
                            }


                            level3Elm.setPluBarcode(pluBarcode);
                            level3Elm.setFeatureNo(featureNo);
                            level3Elm.setFeatureName(featureName);
                            level3Elm.setSUnit(sunit);
                            level3Elm.setSUnitName(uName);
                            level3Elm.setSpecName(specName);
                            level3Elm.setQty(pQty);
                            level3Elm.setOQty(oQty);
                            level3Elm.setGoodsStatus(goodsStatus);

                            level3Elm.setMessages(new ArrayList<DCP_ProductionOrderQuery_OpenRes.level4Elm>());
                            if (!CollectionUtils.isEmpty(goodsLists)) {
                                for (Map<String, Object> getMemo : goodsLists) {
                                    DCP_ProductionOrderQuery_OpenRes.level4Elm level4Elm = res.new level4Elm();
                                    String processtaskno1 = getMemo.get("PROCESSTASKNO").toString();
                                    String createdatetime1 = getMemo.get("CREATEDATETIME").toString();
                                    String item1 = getMemo.get("ITEM").toString();
                                    String oItem1 = getMemo.get("OITEM").toString();
                                    String pluno1 = getMemo.get("PLUNO").toString();
                                    String featureno1 = getMemo.get("FEATURENO").toString();
                                    String plubarcode1 = getMemo.get("PLUBARCODE").toString();
                                    String sunit1 = getMemo.get("SUNIT").toString();

                                    // 过滤此单据商品的明细
                                    if (processtaskno1.equals(processtaskno) && createdatetime1.equals(createDateTime) && pluno.equals(pluno1)) {
                                        if (featureno1.equals(featureNo) && plubarcode1.equals(pluBarcode) && sunit.equals(sunit1)&&item1.equals(Item)&& oItem.equals(oItem1)) {
                                            String memoType = getMemo.get("MEMOTYPE").toString();
                                            String memoName = getMemo.get("MEMONAME").toString();
                                            String memo = getMemo.get("MEMO").toString();
                                            if (Check.Null(memoType) && Check.Null(memoName) && Check.Null(memo)) {
                                                continue;
                                            }
                                            level4Elm.setMessage(memo);
                                            level4Elm.setMsgType(Check.Null(memoType) == true ? "text" : memoType);
                                            level4Elm.setMsgName(memoName);
                                            level3Elm.getMessages().add(level4Elm);
                                        }
                                    }
                                }
                            }

                            DCP_ProductionOrderQuery_OpenRes.level5Elm level5Elm = res.new level5Elm();

                            level5Elm.setContMan(contman);
                            level5Elm.setContTel(conttel);
                            level5Elm.setGetMan(getman);
                            level5Elm.setGetManTel(getmantel);
                            level5Elm.setAddress(address);
                            level5Elm.setShipDate(shipDate);
                            level5Elm.setShipStartTime(shipStartTime);
                            level5Elm.setShipEndTime(shipEndTime);

                            level3Elm.setShipInfo(level5Elm);
                            level3Elm.setMemo(orderMemo);
                            level3Elm.setRefundReasonName(refundReasonName);
                            level1Elm.getGoodsList().add(level3Elm);
                        }
                    }

                }
            }
            res.setDatas(level1Elm);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
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
    protected String getQuerySql(DCP_ProductionOrderQuery_OpenReq req) throws Exception {
        return null;
    }

    /**
     * 查询订单完成数
     *
     * @param req
     * @param plunos
     * @return
     */
    private String getOrderCount(DCP_ProductionOrderQuery_OpenReq req, List<Map<String, Object>> plunos) {
        String stallId = req.getRequest().getStallId();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT count(DISTINCT c.ORDERNO) num1,count(DISTINCT d.ORDERNO) num2  " +
                " from DCP_PROCESSTASK  a " +
                " LEFT JOIN DCP_PROCESSTASK_DETAIL b ON a.SHOPID = b.SHOPID AND a.EID = b.EID AND  a.PROCESSTASKNO = b.PROCESSTASKNO  AND a.ORGANIZATIONNO = b.ORGANIZATIONNO " +
                " LEFT JOIN DCP_ORDER c ON a.EID  = c.EID  AND b.OFNO  = c.ORDERNO AND c.PRODUCTSTATUS = '6' AND (c.STATUS !='3' AND c.STATUS!='12')" +
                " LEFT JOIN DCP_ORDER d ON a.EID  = d.EID  AND b.OFNO  = d.ORDERNO AND d.PRODUCTSTATUS ! = '6' AND (d.STATUS !='3' AND d.STATUS!='12')" +
                " WHERE a.EID = '" + req.getRequest().getEId() + "' AND a.SHOPID = '" + req.getRequest().getMachShopNo() + "' " + getString("b", "PLUNO", plunos) + "");
       if(!Check.Null(stallId)){
           sqlbuf.append(" AND (b.STALLID  = '"+stallId+"' OR b.STALLID IS NULL)");
       }
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询商品状态 对应的数量
     *
     * @param req
     * @param plunos
     * @return
     */
    private String getPlunoStatusCount(DCP_ProductionOrderQuery_OpenReq req, List<Map<String, Object>> plunos, String goodsStatus) {
        String stallId = req.getRequest().getStallId();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT count(PLUNO) AS num FROM DCP_PROCESSTASK a");
        if (goodsStatus.equals("0")) {
            // 未制作商品
            sqlbuf.append(" LEFT JOIN DCP_PROCESSTASK_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO  AND b.GOODSSTATUS = '0' " + getString("b", "PLUNO", plunos) + "");
        } else if (goodsStatus.equals("1")) {
            // 已制作商品
            sqlbuf.append(" LEFT JOIN DCP_PROCESSTASK_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO  AND b.GOODSSTATUS = '1' " + getString("b", "PLUNO", plunos) + "");
        } else if (goodsStatus.equals("2")) {
            // 已完成商品
            sqlbuf.append(" LEFT JOIN DCP_PROCESSTASK_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO  AND b.GOODSSTATUS = '2' " + getString("b", "PLUNO", plunos) + "");
        }
        sqlbuf.append(" WHERE a.EID = '" + req.getRequest().getEId() + "' AND a.SHOPID = '" + req.getRequest().getMachShopNo() + "' ");
        if(!Check.Null(stallId)){
            sqlbuf.append(" AND (b.STALLID  = '"+stallId+"' OR b.STALLID IS NULL)");
        }
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询档口下的 商品标签
     *
     * @param req
     * @return
     */
    private String getStallPluno(DCP_ProductionOrderQuery_OpenReq req) {
        String eId = req.getRequest().getEId();
        String machShopNo = req.getRequest().getMachShopNo();
        String stallId = req.getRequest().getStallId();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        List<Map<String, Object>> tags = null;
        if (!Check.Null(stallId)) {
            sqlbuf.append("SELECT b.TAGNO FROM DCP_ORG_ORDERSET_KDS_TAG b WHERE b.EID ='" + eId + "' AND b.ORGANIZATIONNO  = '" + machShopNo + "' AND b.STALLID = '" + stallId + "'");
            try {
                tags = this.doQueryData(sqlbuf.toString(), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT DISTINCT a.ID " +
                " FROM DCP_TAGTYPE_DETAIL a WHERE a.EID = '" + req.geteId() + "' and  a.TAGGROUPTYPE = 'GOODS_PROD' ");
        if (!Check.Null(stallId) && !CollectionUtils.isEmpty(tags)) {
            sqlbuf.append(" AND a.TAGNO IN (");
            for (Map<String, Object> tag : tags) {
                sqlbuf.append("'" + tag.get("TAGNO").toString() + "',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.append(")");
        }
        sql = sqlbuf.toString();
        return sql;
    }

    private String getString(String tableNo, String name, List<Map<String, Object>> plunos) {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        if (!CollectionUtils.isEmpty(plunos)) {
            sqlbuf.append(" AND " + tableNo + "." + name + " IN (");
            for (Map<String, Object> pluno : plunos) {
                sqlbuf.append("'" + pluno.get("ID").toString() + "',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.append(")");
        }
        sql = sqlbuf.toString();
        return sql;
    }

    private String getString2(String tableNo, String name, List<String> list) {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        if (!CollectionUtils.isEmpty(list)) {
            sqlbuf.append(" AND " + tableNo + "." + name + " IN (");
            for (String str : list) {
                sqlbuf.append("'" + str + "',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.append(")");
        }
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询商品列表
     *
     * @param req
     * @return
     */
    public String getGoodsList(DCP_ProductionOrderQuery_OpenReq req, List<Map<String, Object>> plunos) {
        DCP_ProductionOrderQuery_OpenReq.level1Elm request = req.getRequest();
        List<String> productStatus = request.getProductStatus();
        List<String> goodsStatus = request.getGoodsStatus();
        List<String> specList = request.getSpecList();
        List<String> status = request.getStatus();
        List<String> featureList = request.getFeatureList();
        String pluNo = request.getPluNo();
        String isModifyShip = request.getIsModifyShip();
        List<DCP_ProductionOrderQuery_OpenReq.level2Elm> shipTime = request.getShipTime();
        DCP_ProductionOrderQuery_OpenReq.level3Elm shipDate = request.getShipDate();
        String keyTxt = request.getKeyTxt();
        String stallId = request.getStallId();
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        String langType = req.getLangType();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT * FROM (" +
                " SELECT count(DISTINCT a.PROCESSTASKNO||b.ITEM||b.PLUNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY a.PROCESSTASKNO, b.ITEM, b.PLUNO) AS rn ,j.maxItem,a.PROCESSTASKNO," +
                "  a.LOADDOCTYPE,a.memo as omemo,a.CREATEDATETIME,a.OFNO,b.PLUNAME, b.ITEM,b.OITEM, b.PLUNO, c.PLU_NAME , b.FEATURENO, d.PLUBARCODE, e.FEATURENAME, f.SUNIT, g.UNAME , b.PQTY, " +
                " h.SPEC, i.MEMOTYPE, i.MEMONAME, i.MEMO , b.GOODSSTATUS, a.CONTMAN, a.CONTTEL, a.GETMAN, a.GETMANTEL , a.ADDRESS, " +
                " a.SHIPDATE, a.SHIPSTARTTIME, a.SHIPENDTIME ,a.REFUNDREASONNAME,b.OQTY" +
                " FROM DCP_PROCESSTASK a " +
                " LEFT JOIN DCP_PROCESSTASK_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO  " +
                " LEFT JOIN DCP_GOODS_LANG c ON a.EID = b.EID AND b.PLUNO = c.PLUNO AND c.LANG_TYPE = '" + langType + "' " +
                " LEFT JOIN DCP_GOODS_BARCODE d ON a.EID = b.EID AND b.PLUNO = d.PLUNO AND b.FEATURENO = d.FEATURENO AND b.PUNIT = d.UNIT AND d.STATUS = '100' " +
                " LEFT JOIN DCP_GOODS_FEATURE_LANG e ON a.EID = e.EID AND e.PLUNO = b.PLUNO AND b.FEATURENO = e.FEATURENO AND e.LANG_TYPE = '" + langType + "' " +
                " LEFT JOIN DCP_GOODS f ON a.EID = b.EID AND b.PLUNO = f.PLUNO AND f.STATUS = '100'" +
                " LEFT JOIN DCP_UNIT_LANG g ON a.EID = g.EID AND f.SUNIT = g.UNIT AND g.LANG_TYPE = '" + langType + "' " +
                " LEFT JOIN DCP_GOODS_UNIT_LANG h on a.eid = h.eid and b.pluno =h.pluno and b.PUNIT = h.OUNIT and h.LANG_TYPE = '"+langType+"'" +
                " LEFT JOIN DCP_ORDER_DETAIL_MEMO i ON a.EID = i.EID AND a.OFNO = i.ORDERNO AND b.OITEM = i.OITEM  " +
                " LEFT JOIN ( SELECT PROCESSTASKNO, EID,SHOPID,count(item) AS MAXITEM FROM DCP_PROCESSTASK_DETAIL GROUP BY PROCESSTASKNO,EID,SHOPID ) j ON a.PROCESSTASKNO = j.PROCESSTASKNO  AND a.EID  = j.eid AND a.SHOPID  = j.SHOPID" +
                " WHERE a.EID = '" + request.getEId() + "' and a.OTYPE = 'ORDER' and (a.ISREFUND != 'Y' OR a.ISREFUND IS NULL) AND a.SHOPID = '" + request.getMachShopNo() + "' " + getString("b", "PLUNO", plunos) + "");


        if (!CollectionUtils.isEmpty(productStatus)) {
            sqlbuf.append(" " + getString2("a", "PRODUCTSTATUS", productStatus) + "");
        }
        if (!CollectionUtils.isEmpty(goodsStatus)) {
            sqlbuf.append(" " + getString2("b", "GOODSSTATUS", goodsStatus) + "");
        }
        if (!CollectionUtils.isEmpty(specList)) {
            sqlbuf.append(" " + getString2("h", "SPEC", specList) + "");
        }
        if (!Check.Null(isModifyShip)) {
            sqlbuf.append(" AND a.ISMODIFYSHIP  = '"+isModifyShip+"' ");
        }

        if(!Check.Null(stallId)){
            sqlbuf.append(" AND (b.STALLID = '"+stallId+"' OR b.STALLID is NULL )");
        }

        if (!Check.Null(keyTxt)) {
            sqlbuf.append("and (a.OFNO like '%%" + keyTxt + "%%' or a.CONTMAN like '%%" + keyTxt + "%%' or a.CONTTEL like '%%" + keyTxt + "%%')");
        }

        if (!CollectionUtils.isEmpty(status)) {
            sqlbuf.append(" " + getString2("a", "OSTATUS", status) + "");
        }

        if (!CollectionUtils.isEmpty(shipTime)) {
            sqlbuf.append("and (");
            for (DCP_ProductionOrderQuery_OpenReq.level2Elm level2Elm : shipTime) {
                sqlbuf.append(" (a.SHIPSTARTTIME >='" + level2Elm.getBeginTime() + "' and  a.SHIPENDTIME <= '" + level2Elm.getEndTime() + "') OR");
            }
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.append(")");
        }

        if(shipDate!=null){
            if(!Check.Null(shipDate.getBeginDate())&&!Check.Null(shipDate.getEndDate())){
                sqlbuf.append(" AND (a.SHIPDATE >= '"+shipDate.getBeginDate()+"' and a.SHIPDATE <= '"+shipDate.getEndDate()+"') ");
            }
        }

        if(!CollectionUtils.isEmpty(featureList)){
            sqlbuf.append(" AND  b.FEATURENO IN (");
            for (String feature : featureList) {
                sqlbuf.append("'"+feature+"',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.append(")");
        }

        if(!Check.Null(pluNo)){
            sqlbuf.append(" AND b.PLUNO like '%%"+pluNo+"%%'");
        }
        sqlbuf.append(" ORDER BY a.SHIPDATE DESC , a.PROCESSTASKNO ,b.ITEM ,i.ITEM");
        sqlbuf.append(" ) where rn>" + startRow + " and rn<=" + (startRow + pageSize));
        sql = sqlbuf.toString();
        return sql;
    }
}
