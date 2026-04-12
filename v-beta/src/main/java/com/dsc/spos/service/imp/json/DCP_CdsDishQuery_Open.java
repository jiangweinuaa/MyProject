package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CdsDishQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_CdsDishQuery_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: KDS传菜/出餐单据查询
 * @author: wangzyc
 * @create: 2021-09-28
 */
public class DCP_CdsDishQuery_Open extends SPosBasicService<DCP_CdsDishQuery_OpenReq, DCP_CdsDishQuery_OpenRes>
{

    Logger logger = LogManager.getLogger(DCP_CdsDishQuery_Open.class.getName());

    @Override
    protected boolean isVerifyFail(DCP_CdsDishQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_CdsDishQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("编号不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CdsDishQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_CdsDishQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_CdsDishQuery_OpenRes getResponseType() {
        return new DCP_CdsDishQuery_OpenRes();
    }

    @Override
    protected DCP_CdsDishQuery_OpenRes processJson(DCP_CdsDishQuery_OpenReq req) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DCP_CdsDishQuery_OpenRes res = this.getResponseType();
        int pageSize = req.getPageSize();
        int pageNumber = req.getPageNumber();
        String eId = req.geteId();
        String shopId = req.getRequest().getShopId();

        int totalRecords = 0; // 总笔数
        int totalPages = 0;
        try {
            DCP_CdsDishQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
            lv1.setOrderList(new ArrayList<>());
            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append(this.getOrderNum(eId, shopId));
            List<Map<String, Object>> getOrderNum = this.doQueryData(sqlbuf.toString(), null);
            String orderNum = "0";
            if (!CollectionUtils.isEmpty(getOrderNum)) {
                orderNum = getOrderNum.get(0).get("ORDERNUM").toString();
            }
            lv1.setOrderNum(orderNum);
            sqlbuf.setLength(0);
            sqlbuf.append(this.getGoodsrNum(eId, shopId));
            List<Map<String, Object>> getGoodsNum = this.doQueryData(sqlbuf.toString(), null);
            String goodsNum = "0";
            if (!CollectionUtils.isEmpty(getGoodsNum)) {
                goodsNum = getGoodsNum.get(0).get("GOODSNUM").toString();
            }
            lv1.setGoodsNum(goodsNum);

            // 查询门店配置超时预警时间
            sqlbuf.setLength(0);
            sqlbuf.append(this.getIsOverTime(req));
            List<Map<String, Object>> getIsOverTime = this.doQueryData(sqlbuf.toString(), null);
            String isOverTime = ""; // 超时预警时间
            if (!CollectionUtils.isEmpty(getIsOverTime)) {
                isOverTime = getIsOverTime.get(0).get("OVERTIME").toString();
            }

            sqlbuf.setLength(0);
            sqlbuf.append(this.getQuerySql(req));
            List<Map<String, Object>> getOrderDetails = this.doQueryData(sqlbuf.toString(), null);


            // 过滤
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("BILLNO", true);
            // 调用过滤函数
            List<Map<String, Object>> getHeader = MapDistinct.getMap(getOrderDetails, condition);
            if (!CollectionUtils.isEmpty(getHeader))
            {
                String num = getHeader.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                //算總頁數
                if (req.getPageSize() != 0 && req.getPageNumber() != 0) {
                    totalPages = totalRecords / req.getPageSize();
                    totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                }

                for (Map<String, Object> oneData : getHeader)
                {
                    DCP_CdsDishQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                    String channelid = oneData.get("CHANNELID").toString();
                    String oChannelName = oneData.get("CHANNELNAME").toString();
                    String appType = oneData.get("APPTYPE").toString();
                    String loadDocType = oneData.get("LOADDOCTYPE").toString();
                    String billNo = oneData.get("BILLNO").toString();
                    String trNo = oneData.get("TRNO").toString();
                    String productstatus = oneData.get("PRODUCTSTATUS").toString();
                    String tableNo = oneData.get("TABLENO").toString();
                    String adultCount = oneData.get("GUESTNUM").toString();
                    String repastType = oneData.get("REPASTTYPE").toString();
                    String memo = oneData.get("MEMO").toString();
                    String sorttime = oneData.get("SORTTIME").toString();
                    String shipType = oneData.get("SHIPTYPE").toString();
                    String tableWareQty = oneData.get("TABLEWAREQTY").toString();
                    String orderTime = oneData.get("ORDERTIME").toString();
                    String madeTime = oneData.get("MAKETIME").toString();
                    String appName = "";
                    if ("POS".equals(appType) || "POSANDROID".equals(appType)) {
                        appName = "POS";
                    } else if ("SCAN".equals(appType)) {
                        appName = "扫码点单";
                    } else if ("WAIMAI".equals(appType)) {
                        appName = "自营外卖";
                    } else if ("ELEME".equals(appType)) {
                        appName = "饿了么";
                    } else if ("MEITUAN".equals(appType)) {
                        appName = "美团";
                    }
                    lv2.setOChannelId(channelid);
                    lv2.setProductStatus(productstatus);
                    lv2.setOChannelName(oChannelName);
                    lv2.setAppName(appName);
                    lv2.setLoadDocType(loadDocType);
                    lv2.setBillNo(billNo);
                    lv2.setTrNo(trNo);
                    lv2.setTableNo(tableNo);
                    lv2.setAdultCount(adultCount);
                    lv2.setRepastType(repastType);
                    lv2.setMemo(memo);

                    String overTime = "";
                    Long min = null;
                    if(!Check.Null(sorttime)){
                        min = getMin(sorttime);
                    }
                    if (Check.Null(isOverTime) || "0".equals(isOverTime)) {
                        overTime = "N";
                    } else if (Check.Null(sorttime)) {
                        overTime = "N";
                    } else {
                        int i = Integer.parseInt(isOverTime);
                        if (min >= i) {
                            overTime = "Y";
                        }
                    }
                    lv2.setIsOverTime(overTime);

                    lv2.setShipType(shipType);
                    lv2.setTableWareQty(tableWareQty);

                    DCP_CdsDishQuery_OpenRes.level3Elm lv3 = res.new level3Elm();
                    if (!Check.Null(sorttime)) {
                        sorttime = sds.format(sdf.parse(sorttime));
                    }
                    if (!Check.Null(madeTime)) {
                        madeTime = sds.format(sdf.parse(madeTime));
                    }
                    lv3.setSomeTime(min + "");
                    lv3.setOrderTime(sorttime);
                    lv3.setMadeTime(madeTime);
                    lv2.setTimeInfo(lv3);
                    lv2.setGoodsList(new ArrayList<>());

                    if (!CollectionUtils.isEmpty(getOrderDetails))
                    {
                        //过滤当前单据
                        List<Map<String, Object>> map_detail=getOrderDetails.stream().filter(p->p.get("BILLNO").toString().equals(billNo)).collect(Collectors.toList());

                        //过滤子商品
                        List<Map<String, Object>> map_detailNorml=map_detail.stream().filter(p->p.get("PACKAGEMITEM").toString().equals("") || p.get("PACKAGEMITEM").toString().equals("0")).collect(Collectors.toList());

                        for (Map<String, Object> twoData : map_detailNorml)
                        {
                            DCP_CdsDishQuery_OpenRes.level4Elm lv4 = res.new level4Elm();
                            String pluno = twoData.get("PLUNO").toString();
                            String oItem = twoData.get("OITEM").toString();
                            String pluName = twoData.get("PLUNAME").toString();
                            String pluBarcode = twoData.get("PLUBARCODE").toString();
                            String qty = twoData.get("QTY").toString();
                            String unitId = twoData.get("UNITID").toString();
                            String unitName = twoData.get("UNITNAME").toString();
                            String specAttrDetail = twoData.get("SPECATTRDETAIL").toString();
                            String flavorstuffDetail = twoData.get("FLAVORSTUFFDETAIL").toString();
                            String isPackage = twoData.get("ISPACKAGE").toString();
                            String pGoodsDetail = twoData.get("PGOODSDETAIL").toString();
                            String pluRepastType = twoData.get("PLUREPASTTYPE").toString();
                            String goodsStatus = twoData.get("GOODSSTATUS").toString();
                            String pluMemo = twoData.get("PLUMEMO").toString();
                            String isUrge = twoData.get("ISURGE").toString();
                            String isrefundorder = twoData.get("ISREFUNDORDER").toString();
                            String refundqty = twoData.get("REFUNDQTY").toString();

                            lv4.setPluNo(pluno);
                            lv4.setOItem(oItem);
                            lv4.setPluName(pluName);
                            lv4.setPluBarcode(pluBarcode);
                            lv4.setQty(qty);
                            lv4.setUnitId(unitId);
                            lv4.setUnitName(unitName);
                            lv4.setSpecAttrDetail(specAttrDetail);
                            lv4.setFlavorstuffDetail(flavorstuffDetail);
                            lv4.setIsPackage(isPackage);
                            lv4.setPGoodsDetail(pGoodsDetail);
                            lv4.setRepastType(pluRepastType);
                            lv4.setPGoodsDetail(pGoodsDetail);
                            lv4.setGoodsStatus(goodsStatus);
                            lv4.setMemo(pluMemo);
                            lv4.setIsUrge(isUrge);
                            lv4.setIsRefundOrder(isrefundorder);
                            lv4.setRefundQty(refundqty);

                            lv4.setPGoodsList(new ArrayList<>());
                            //套餐主商品
                            if (!Check.Null(isPackage) && isPackage.equals("Y"))
                            {
                                List<Map<String, Object>> map_detail_sub=map_detail.stream().filter(p->p.get("PACKAGEMITEM").toString().equals(lv4.getOItem())).collect(Collectors.toList());
                                if (map_detail_sub != null && map_detail_sub.size()>0)
                                {
                                    for (Map<String, Object> threeData : map_detail_sub)
                                    {
                                        DCP_CdsDishQuery_OpenRes.level4Elm lv4_sub = res.new level4Elm();
                                        String pluno_sub = threeData.get("PLUNO").toString();
                                        String oItem_sub = threeData.get("OITEM").toString();
                                        String pluName_sub = threeData.get("PLUNAME").toString();
                                        String pluBarcode_sub = threeData.get("PLUBARCODE").toString();
                                        String qty_sub = threeData.get("QTY").toString();
                                        String unitId_sub = threeData.get("UNITID").toString();
                                        String unitName_sub = threeData.get("UNITNAME").toString();
                                        String specAttrDetail_sub = threeData.get("SPECATTRDETAIL").toString();
                                        String flavorstuffDetail_sub = threeData.get("FLAVORSTUFFDETAIL").toString();
                                        String isPackage_sub = threeData.get("ISPACKAGE").toString();
                                        String pGoodsDetail_sub = threeData.get("PGOODSDETAIL").toString();
                                        String pluRepastType_sub = threeData.get("PLUREPASTTYPE").toString();
                                        String goodsStatus_sub = threeData.get("GOODSSTATUS").toString();
                                        String pluMemo_sub = threeData.get("PLUMEMO").toString();
                                        String isUrge_sub = threeData.get("ISURGE").toString();
                                        String isrefundorder_sub = threeData.get("ISREFUNDORDER").toString();
                                        String refundqty_sub = threeData.get("REFUNDQTY").toString();

                                        lv4_sub.setPluNo(pluno_sub);
                                        lv4_sub.setOItem(oItem_sub);
                                        lv4_sub.setPluName(pluName_sub);
                                        lv4_sub.setPluBarcode(pluBarcode_sub);
                                        lv4_sub.setQty(qty_sub);
                                        lv4_sub.setUnitId(unitId_sub);
                                        lv4_sub.setUnitName(unitName_sub);
                                        lv4_sub.setSpecAttrDetail(specAttrDetail_sub);
                                        lv4_sub.setFlavorstuffDetail(flavorstuffDetail_sub);
                                        lv4_sub.setIsPackage(isPackage_sub);
                                        lv4_sub.setPGoodsDetail(pGoodsDetail_sub);
                                        lv4_sub.setRepastType(pluRepastType_sub);
                                        lv4_sub.setPGoodsDetail(pGoodsDetail_sub);
                                        lv4_sub.setGoodsStatus(goodsStatus_sub);
                                        lv4_sub.setMemo(pluMemo_sub);
                                        lv4_sub.setIsUrge(isUrge_sub);
                                        lv4_sub.setIsRefundOrder(isrefundorder_sub);
                                        lv4_sub.setRefundQty(refundqty_sub);


                                        DCP_CdsDishQuery_OpenRes.level5Elm lv5_sub = res.new level5Elm();

                                        String pluOrderTime = threeData.get("SORTPLUTIME").toString();
                                        String assortedTime = threeData.get("ASSORTEDTIME").toString();
                                        String pluMakeTime = threeData.get("PLUMAKETIME").toString();
                                        String pickupTime = threeData.get("COMPLETETIME").toString();

                                        if (!Check.Null(pluOrderTime)) {
                                            pluOrderTime = sds.format(sdf.parse(pluOrderTime));
                                        }
                                        if (!Check.Null(assortedTime)) {
                                            assortedTime = sds.format(sdf.parse(assortedTime));
                                        }
                                        if (!Check.Null(pluMakeTime)) {
                                            pluMakeTime = sds.format(sdf.parse(pluMakeTime));
                                        }
                                        if (!Check.Null(pickupTime)) {
                                            pickupTime = sds.format(sdf.parse(pickupTime));
                                        }

                                        lv5_sub.setOrderTime(pluOrderTime);
                                        lv5_sub.setAssortedTime(assortedTime);
                                        lv5_sub.setMadeTime(pluMakeTime);
                                        lv5_sub.setPickupTime(pickupTime);
                                        lv4_sub.setTimeInfo(lv5_sub);

                                        lv4.getPGoodsList().add(lv4_sub);
                                    }
                                }
                            }

                            DCP_CdsDishQuery_OpenRes.level5Elm lv5 = res.new level5Elm();

                            String pluOrderTime = twoData.get("SORTPLUTIME").toString();
                            String assortedTime = twoData.get("ASSORTEDTIME").toString();
                            String pluMakeTime = twoData.get("PLUMAKETIME").toString();
                            String pickupTime = twoData.get("COMPLETETIME").toString();

                            if (!Check.Null(pluOrderTime)) {
                                pluOrderTime = sds.format(sdf.parse(pluOrderTime));
                            }
                            if (!Check.Null(assortedTime)) {
                                assortedTime = sds.format(sdf.parse(assortedTime));
                            }
                            if (!Check.Null(pluMakeTime)) {
                                pluMakeTime = sds.format(sdf.parse(pluMakeTime));
                            }
                            if (!Check.Null(pickupTime)) {
                                pickupTime = sds.format(sdf.parse(pickupTime));
                            }

                            lv5.setOrderTime(pluOrderTime);
                            lv5.setAssortedTime(assortedTime);
                            lv5.setMadeTime(pluMakeTime);
                            lv5.setPickupTime(pickupTime);
                            lv4.setTimeInfo(lv5);

                            lv2.getGoodsList().add(lv4);
                        }
                    }
                    lv1.getOrderList().add(lv2);
                }
            }
            res.setDatas(lv1);
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
    protected String getQuerySql(DCP_CdsDishQuery_OpenReq req) throws Exception {
        String sql = "";
        DCP_CdsDishQuery_OpenReq.level1Elm request = req.getRequest();
        List<String> repastTypes = request.getRepastType();
        String temp_repastTypes = "";
        if (!CollectionUtils.isEmpty(repastTypes)) {
            temp_repastTypes = PosPub.getArrayStrSQLIn(repastTypes.toArray(new String[]{}));
        }
        List<String> goodsStatus = request.getGoodsStatus();
        String goodsStatusStr = "";
        if (CollectionUtils.isEmpty(goodsStatus)) {
            goodsStatusStr = "2";
        }else {
            goodsStatusStr = PosPub.getArrayStrSQLIn(goodsStatus.toArray(new String[]{}));
        }
        int pageSize = req.getPageSize();
        int pageNumber = req.getPageNumber();
        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * from (" +
                " SELECT count(DISTINCT a.BILLNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY a.BILLNO) AS rn , a.CHANNELID,  " +
                " b.CHANNELNAME, a.APPTYPE, a.LOADDOCTYPE, a.BILLNO , a.TRNO,a.PRODUCTSTATUS, a.TABLENO, a.GUESTNUM, NVL(a.REPASTTYPE,0) REPASTTYPE, a.MEMO , " +
                "  CASE  WHEN a.REPASTTYPE = 2 THEN a.SHIPENDTIME WHEN a.REPASTTYPE = 1 OR a.REPASTTYPE = 0 OR a.REPASTTYPE is null THEN a.ORDERTIME END AS sorttime,  " +
                " a.SHIPTYPE, a.TABLEWAREQTY, a.MAKETIME, c.PLUNO , c.OITEM, c.PLUNAME, c.PLUBARCODE, c.QTY, c.UNITID , c.UNITNAME , " +
                " CASE WHEN c.SPECNAME IS NOT NULL AND c.ATTRNAME IS NULL THEN '(' || c.SPECNAME  || ')'  " +
                "  WHEN c.SPECNAME IS  NULL AND c.ATTRNAME IS NOT NULL  THEN '('  || c.ATTRNAME || ')'  " +
                "  WHEN c.SPECNAME IS not NULL AND c.ATTRNAME IS NOT NULL THEN '(' || c.SPECNAME || ',' || c.ATTRNAME || ')' END  AS specAttrDetail ," +
                "   c.FLAVORSTUFFDETAIL, c.ISPACKAGE, c.PGOODSDETAIL,  " +
                " c.REPASTTYPE pluREPASTTYPE, c.GOODSSTATUS , c.MEMO AS plumemo, c.ISURGE , CASE  WHEN c.REPASTTYPE = 2 THEN a.SHIPENDTIME WHEN  " +
                " c.REPASTTYPE = 1 OR c.REPASTTYPE = 0  OR c.REPASTTYPE is null THEN a.ORDERTIME END AS sortPlutime, c.ASSORTEDTIME, c.MAKETIME pluMakeTime,c.ISREFUNDORDER,c.REFUNDQTY, c.COMPLETETIME ,SUBSTR(a.ORDERTIME , 1, 8) ORDERTIME,c.packagemitem " +
                " FROM DCP_PRODUCT_SALE a " +
                "  LEFT JOIN CRM_CHANNEL b ON a.CHANNELID = b.CHANNELID AND a.EID = b.eid " +
                " LEFT JOIN DCP_PRODUCT_DETAIL c ON a.EID = c.EID AND a.SHOPID = c.SHOPID AND a.BILLNO = c.BILLNO and c.detailitem=0 " + //and c.detailitem=0加料商品不要
                " LEFT JOIN DCP_KDSDISHES_CONTROL d ON a.EID = d.EID AND a.SHOPID = d.SHOPID AND c.PLUNO = d.PLUNO AND c.FINALCATEGORY = d.CATEGORY " +
                " WHERE a.EID = '" + req.geteId() + "' AND a.SHOPID = '" + request.getShopId() + "' AND (a.ISREFUND = 'N' OR a.ISREFUND IS NULL) and (a.ISREFUNDORDER is null or a.ISREFUNDORDER = '1')  AND (d.UNCALL = 'N' OR d.UNCALL IS NULL) AND (a.BILLTYPE = 'SALE' or a.BILLTYPE = 'ORDER')");
        if (!Check.Null(temp_repastTypes)) {
            sqlbuf.append(" AND a.REPASTTYPE in (" + temp_repastTypes + ")");
        }

        //排序:先按预定状态排序，在按相关时间字段排序，举例
        //ORDER BY case  when a.isbook='Y' then 1 ELSE 0 END DESC,
        //case  when a.isbook='Y' then a.shipendtime ELSE a.Ordertime END DESC
        if (!Check.Null(goodsStatusStr))
        {
            sqlbuf.append(" and a.PRODUCTSTATUS in (" + goodsStatusStr + ")");

            if (goodsStatusStr.contains("0") || goodsStatusStr.contains("1") || goodsStatusStr.contains("2"))
            {
                sqlbuf.append(" ORDER BY case  when a.isbook='Y' then a.shipendtime ELSE a.ORDERTIME END ");
            }
            else if (goodsStatusStr.contains("3"))
            {
                sqlbuf.append(" AND SUBSTR(a.ORDERTIME , 1, 8) = to_char(sysdate, 'yyyyMMdd')");
                sqlbuf.append(" ORDER BY case  when a.isbook='Y' then a.shipendtime ELSE a.PICKUPTIME END ");
            }
        }

        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询待传菜订单合计
     *
     * @param eid,shopId
     * @return
     */
    protected String getOrderNum(String eid, String shopId) {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT count(*) orderNum FROM DCP_PRODUCT_SALE " +
                "WHERE eid = '" + eid + "' AND SHOPID = '" + shopId + "'   and (ISREFUND = 'N' or ISREFUND is null)  and （ISREFUNDORDER is null or ISREFUNDORDER = '1'） AND (BILLTYPE = 'SALE' or BILLTYPE = 'ORDER') AND PRODUCTSTATUS = '2'");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询待传菜订单合计
     *
     * @param eid,shopId
     * @return
     */
    protected String getGoodsrNum(String eid, String shopId) {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT sum(b.QTY-b.REFUNDQTY) goodsNum FROM DCP_PRODUCT_SALE a  " +
                "LEFT JOIN DCP_PRODUCT_DETAIL b ON a.EID  = b.EID  AND a.SHOPID  = b.SHOPID  AND a.BILLNO  = b.BILLNO  " +
                " LEFT JOIN DCP_KDSDISHES_CONTROL c ON a.EID  = c.EID  AND a.SHOPID  = c.SHOPID  AND b.PLUNO  = c.PLUNO  AND b.FINALCATEGORY = c.CATEGORY   " +
                " WHERE a.EID  = '" + eid + "' AND a.SHOPID  = '" + shopId + "' AND  (a.ISREFUND = 'N' or a.ISREFUND is null) and （b.ISREFUNDORDER is null or b.ISREFUNDORDER = '1'）   AND (a.BILLTYPE = 'SALE' or a.BILLTYPE = 'ORDER') AND b.GOODSSTATUS = '2' " +
                " AND (c.UNCALL = 'N' OR c.UNCALL IS null)");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询当前门店该机台超时预警时间
     *
     * @param req
     * @return
     */
    protected String getIsOverTime(DCP_CdsDishQuery_OpenReq req) {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_KDSBASICSET WHERE eid = '" + req.geteId() + "' AND SHOPID = '" + req.getRequest().getShopId() + "' AND MACHINEID = '" + req.getRequest().getMachineId() + "' ");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 计算下单时间和当前时间差 分钟
     *
     * @param sdate
     * @return
     * @throws ParseException
     */
    public Long getMin(String sdate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date start = simpleDateFormat.parse(sdate);
        Date end = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        long between = (end.getTime() - start.getTime()) / 1000;
        long min = between / 60;
        return min;
    }
}
