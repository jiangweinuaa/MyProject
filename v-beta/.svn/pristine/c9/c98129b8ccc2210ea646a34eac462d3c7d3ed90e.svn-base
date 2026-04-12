package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_RefundDishQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_RefundDishQuery_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: KDS退菜单据查询
 * @author: wangzyc
 * @create: 2021-10-11
 */
public class DCP_RefundDishQuery_Open extends SPosBasicService<DCP_RefundDishQuery_OpenReq, DCP_RefundDishQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_RefundDishQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_RefundDishQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("机台编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getTerminalType())) {
            errMsg.append("设备模式不能为空,");
            isFail = true;
        }


        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_RefundDishQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_RefundDishQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_RefundDishQuery_OpenRes getResponseType() {
        return new DCP_RefundDishQuery_OpenRes();
    }

    @Override
    protected DCP_RefundDishQuery_OpenRes processJson(DCP_RefundDishQuery_OpenReq req) throws Exception {
        DCP_RefundDishQuery_OpenRes res = this.getResponseType();
        DCP_RefundDishQuery_OpenReq.level1Elm request = req.getRequest();
        String terminalType = request.getTerminalType();
        String eId = req.geteId();
        String shopId = request.getShopId();
        res.setDatas(new ArrayList<>());

        // 根据terminalType 做判断 0配菜端 1制作端 查询加工任务表，2 传菜端 查询零售生产任务表

        int totalRecords = 0; // 总笔数
        int totalPages = 0;

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getPlunoDetails = this.doQueryData(sql, null);

            sql = this.getIsOverTime(req);
            List<Map<String, Object>> getIsOverTime = this.doQueryData(sql, null);
            String isOverTime = ""; // 超时预警时间
            if (!CollectionUtils.isEmpty(getIsOverTime)) {
                isOverTime = getIsOverTime.get(0).get("OVERTIME").toString();
            }

            if (!CollectionUtils.isEmpty(getPlunoDetails)) {
                if ("1".equals(terminalType) || "0".equals(terminalType)) {
                    // 过滤
                    Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                    condition.put("PLUNO", true);
                    condition.put("OFNO", true);
                    condition.put("FLAVORSTUFFDETAIL", true);
                    condition.put("PLUNAME", true);
                    condition.put("PLUBARCODE", true);
                    condition.put("SPECATTRDETAIL", true);
                    condition.put("ISURGE", true);
                    condition.put("OITEM", true);
                    // 调用过滤函数
                    List<Map<String, Object>> getHeader = MapDistinct.getMap(getPlunoDetails, condition);

                    List<Map<String, Object>> collect = getHeader.stream().skip((pageNumber - 1) * pageSize)
                            .limit(pageSize)
                            .collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(collect)) {
                        totalRecords = getHeader.size();
                        //算總頁數
                        if (req.getPageSize() != 0 && req.getPageNumber() != 0) {
                            totalPages = totalRecords / req.getPageSize();
                            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                        }
                        for (Map<String, Object> getPlunoDetail : collect) {
                            DCP_RefundDishQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                            String billno = getPlunoDetail.get("OFNO").toString();
                            String oItem = getPlunoDetail.get("OITEM").toString();
                            String pluno = getPlunoDetail.get("PLUNO").toString();
                            String plunoName = getPlunoDetail.get("PLUNAME").toString();
                            String plubarCode = getPlunoDetail.get("PLUBARCODE").toString();
                            String flavorstuffdetail = getPlunoDetail.get("FLAVORSTUFFDETAIL").toString();
                            String specAttrDetail = getPlunoDetail.get("SPECATTRDETAIL").toString();
                            String isPackage = getPlunoDetail.get("ISPACKAGE").toString();
                            String pGoodsDetail = getPlunoDetail.get("PGOODSDETAIL").toString();
                            String unitid = getPlunoDetail.get("PUNIT").toString();
                            String unitName = getPlunoDetail.get("UNAME").toString();
                            String trNo = getPlunoDetail.get("TRNO").toString();
                            String tableNo = getPlunoDetail.get("TABLENO").toString();
                            String goodsStatus = getPlunoDetail.get("GOODSSTATUS").toString();
                            String isUrge = getPlunoDetail.get("ISURGE").toString();
                            String refunDreasonName = getPlunoDetail.get("REFUNDREASONNAME").toString();
                            String sorttime = getPlunoDetail.get("SORTTIME").toString();

                            int qty = 0;
                            for (Map<String, Object> getOverPluno : getPlunoDetails) {
                                String pluno2 = getOverPluno.get("PLUNO").toString();
                                String pluName2 = getOverPluno.get("PLUNAME").toString();
                                String billno2 = getOverPluno.get("OFNO").toString();
                                String flavorstuffdetail2 = getOverPluno.get("FLAVORSTUFFDETAIL").toString();
                                String plubarcode2 = getOverPluno.get("PLUBARCODE").toString();
                                String specattrdetail2 = getOverPluno.get("SPECATTRDETAIL").toString();
                                String isurge = getOverPluno.get("ISURGE").toString();
                                String oitem2 = getOverPluno.get("OITEM").toString();

                                if (pluno2.equals(pluno) && pluName2.equals(plunoName) && billno2.equals(billno) &&
                                        flavorstuffdetail2.equals(flavorstuffdetail) && plubarCode.equals(plubarcode2) &&
                                        specAttrDetail.equals(specattrdetail2) &&isurge.equals(isUrge)&&oitem2.equals(oItem) ) {
                                    qty++;
                                }
                            }

                            String overTime = "";
                            if (Check.Null(isOverTime) || "0".equals(isOverTime)) {
                                overTime = "N";
                            } else if (Check.Null(sorttime)) {
                                overTime = "N";
                            } else {
                                Long min = getMin(sorttime);
                                int i = Integer.parseInt(isOverTime);

                                if (min >= i) {
                                    overTime = "Y";
                                }
                            }

                            lv1.setBillNo(billno);
                            lv1.setOItem(oItem);
                            lv1.setPluNo(pluno);
                            lv1.setPluName(plunoName);
                            lv1.setPluBarcode(plubarCode);
                            lv1.setQty(qty+"");
                            lv1.setSpecAttrDetail(specAttrDetail);
                            lv1.setFlavorstuffDetail(flavorstuffdetail);
                            lv1.setIsPackage(isPackage);
                            lv1.setPGoodsDetail(pGoodsDetail);
                            lv1.setUnitId(unitid);
                            lv1.setUnitName(unitName);
                            lv1.setTrNo(trNo);
                            lv1.setTableNo(tableNo);
                            lv1.setGoodsStatus(goodsStatus);
                            lv1.setIsUrge(isUrge);
                            lv1.setIsOverTime(overTime);
                            lv1.setRefundReasonName(refunDreasonName);

                            res.getDatas().add(lv1);


                        }
                    }

                } else if ("2".equals(terminalType)) {
                    String num = getPlunoDetails.get(0).get("NUM").toString();
                    totalRecords = Integer.parseInt(num);
                    //算總頁數
                    if (req.getPageSize() != 0 && req.getPageNumber() != 0) {
                        totalPages = totalRecords / req.getPageSize();
                        totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                    }

                    for (Map<String, Object> getPlunoDetail : getPlunoDetails) {
                        DCP_RefundDishQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                        String billno = getPlunoDetail.get("BILLNO").toString();
                        String oItem = getPlunoDetail.get("OITEM").toString();
                        String pluno = getPlunoDetail.get("PLUNO").toString();
                        String plunoName = getPlunoDetail.get("PLUNAME").toString();
                        String plubarCode = getPlunoDetail.get("PLUBARCODE").toString();
                        String qty = getPlunoDetail.get("QTY").toString();
                        String flavorstuffdetail = getPlunoDetail.get("FLAVORSTUFFDETAIL").toString();
                        String specAttrDetail = getPlunoDetail.get("SPECATTRDETAIL").toString();
                        String isPackage = getPlunoDetail.get("ISPACKAGE").toString();
                        String pGoodsDetail = getPlunoDetail.get("PGOODSDETAIL").toString();
                        String unitid = getPlunoDetail.get("UNITID").toString();
                        String unitName = getPlunoDetail.get("UNITNAME").toString();
                        String trNo = getPlunoDetail.get("TRNO").toString();
                        String tableNo = getPlunoDetail.get("TABLENO").toString();
                        String goodsStatus = getPlunoDetail.get("GOODSSTATUS").toString();
                        String isUrge = getPlunoDetail.get("ISURGE").toString();
                        String refunDreasonName = getPlunoDetail.get("REFUNDREASONNAME").toString();
                        String sorttime = getPlunoDetail.get("SORTTIME").toString();

                        String overTime = "";
                        if (Check.Null(isOverTime) || "0".equals(isOverTime)) {
                            overTime = "N";
                        } else if (Check.Null(sorttime)) {
                            overTime = "N";
                        } else {
                            Long min = getMin(sorttime);
                            int i = Integer.parseInt(isOverTime);

                            if (min >= i) {
                                overTime = "Y";
                            }
                        }

                        lv1.setBillNo(billno);
                        lv1.setOItem(oItem);
                        lv1.setPluNo(pluno);
                        lv1.setPluName(plunoName);
                        lv1.setPluBarcode(plubarCode);
                        lv1.setQty(qty);
                        lv1.setSpecAttrDetail(specAttrDetail);
                        lv1.setFlavorstuffDetail(flavorstuffdetail);
                        lv1.setIsPackage(isPackage);
                        lv1.setPGoodsDetail(pGoodsDetail);
                        lv1.setUnitId(unitid);
                        lv1.setUnitName(unitName);
                        lv1.setTrNo(trNo);
                        lv1.setTableNo(tableNo);
                        lv1.setGoodsStatus(goodsStatus);
                        lv1.setIsUrge(isUrge);
                        lv1.setIsOverTime(overTime);
                        lv1.setRefundReasonName(refunDreasonName);

                        res.getDatas().add(lv1);

                    }
                }

            }

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
    protected String getQuerySql(DCP_RefundDishQuery_OpenReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        DCP_RefundDishQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String terminalType = request.getTerminalType(); // 模式为0 查商品状态为0的 模式为1 查商品状态为1 的
        String eId = req.geteId();

        if ("1".equals(terminalType) || "0".equals(terminalType)) {
            String KdsSidedishes = PosPub.getPARA_SMS(dao, eId, shopId, "KdsSidedishes");
            // 如果模式为 0 OR 1 查询生产任务表
            sqlbuf.append("SELECT a.* , CASE  WHEN REPASTTYPE = 2 THEN SHIPENDTIME  WHEN  (REPASTTYPE = 1 OR REPASTTYPE = 0) AND a.otype != 'BEFORE' THEN ORDERTIME END AS sorttime" +
                    "  FROM ( " +
                    " SELECT a.OFNO,  a.PROCESSTASKNO, b.PLUNO, b.PLUNAME, b.MEMO , NVL(d.ORDERTIME, SUBSTR(a.CREATEDATETIME, 1, 14)) AS CREATEDATETIME , " +
                    " d.ORDERTIME, d.SHIPENDTIME, a.OTYPE ,  CASE  WHEN b.REPASTTYPE IS NULL AND (a.LOADDOCTYPE = 'WAIMAI' OR a.LOADDOCTYPE = 'MEITUAN' OR a.LOADDOCTYPE = 'ELEME') THEN 2" +
                    " ELSE TO_NUMBER(b.REPASTTYPE) END AS REPASTTYPE , " +
                    " CASE  WHEN b.SPECNAME IS NOT NULL AND b.ATTRNAME IS NULL THEN '(' || b.SPECNAME || ')'  " +
                    " WHEN b.SPECNAME IS NULL AND  b.ATTRNAME IS NOT NULL THEN '(' || b.ATTRNAME || ')' " +
                    " WHEN b.SPECNAME IS NOT NULL AND b.ATTRNAME IS NOT NULL THEN '(' || b.SPECNAME || ',' || b.ATTRNAME || ')' END AS specAttrDetail, " +
                    " b.OITEM, b.PLUBARCODE, b.PQTY, b.FLAVORSTUFFDETAIL  , b.ISPACKAGE, b.PGOODSDETAIL, b.PUNIT, c.UNAME, d.TRNO , d.TABLENO, " +
                    " b.GOODSSTATUS, b.ISURGE, a.REFUNDREASONNAME ,  " +
                    " CASE  WHEN a.ISREFUNDORDER = '0' THEN a.ISREFUNDORDER WHEN a.ISREFUNDORDER = '1' THEN b.ISREFUNDORDER END AS ISREFUNDORDER " +
                    " FROM DCP_PROCESSTASK a " +
                    " RIGHT JOIN DCP_PROCESSTASK_DETAIL b ON a.eid = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO " +
                    " LEFT JOIN DCP_UNIT_LANG c ON b.PUNIT = c.UNIT AND a.EID = c.EID AND c.LANG_TYPE = '" + req.getLangType() + "' " +
                    " INNER JOIN DCP_PRODUCT_SALE d ON a.EID = d.EID AND a.OFNO = d.BILLNO AND a.SHOPID = d.SHOPID  " +
                    " WHERE a.eid = '" + eId + "' AND a.shopid = '" + shopId + "' AND (a.ISREFUND = 'N' OR a.ISREFUND IS NULL) AND " +
                    " a.ISREFUNDORDER IS NOT NULL AND a.OTYPE IN ('SALE', 'ORDER')  ");
            if ("0".equals(terminalType)) {
                sqlbuf.append(" and b.GOODSSTATUS = '0' ");
            } else if ("1".equals(terminalType)) {
                if ("Y".equals(KdsSidedishes)) {
                    sqlbuf.append(" and b.GOODSSTATUS in ('0','1') ");
                } else {
                    sqlbuf.append(" and b.GOODSSTATUS = '1' ");
                }
            }
            sqlbuf.append(" ) a");

        } else if ("2".equals(terminalType)) {
            // 如果模式为2 则查询临时表

            int pageSize = req.getPageSize();
            int pageNumber = req.getPageNumber();
            // 計算起啟位置
            int startRow = (pageNumber - 1) * pageSize;

            sqlbuf.append("select * from (" +
                    " select a.*,CASE  WHEN a.REPASTTYPE = 2 THEN a.SHIPENDTIME  WHEN a.REPASTTYPE = 1 OR a.REPASTTYPE = 0  THEN a.ORDERTIME END AS sorttime " +
                    "  from (" +
                    " SELECT count(DISTINCT a.BILLNO || b.PLUNO) OVER () AS num ,DENSE_RANK() OVER (ORDER BY a.BILLNO, b.PLUNO) AS rn," +
                    " CASE  WHEN a.REPASTTYPE IS NULL AND (a.LOADDOCTYPE = 'WAIMAI' OR a.LOADDOCTYPE = 'MEITUAN' OR a.LOADDOCTYPE = 'ELEME') " +
                    " THEN 2 ELSE TO_NUMBER(a.REPASTTYPE) END AS REPASTTYPE, " +
                    " CASE WHEN b.SPECNAME IS NOT NULL AND b.ATTRNAME IS NULL THEN '(' || b.SPECNAME  || ')'  " +
                    "  WHEN b.SPECNAME IS  NULL AND b.ATTRNAME IS NOT NULL  THEN '('  || b.ATTRNAME || ')'  " +
                    "  WHEN b.SPECNAME IS not NULL AND b.ATTRNAME IS NOT NULL THEN '(' || b.SPECNAME || ',' || b.ATTRNAME || ')' END  AS specAttrDetail ," +
                    " a.BILLNO, b.PLUNO , a.ISREFUNDORDER AS ISREFUND, a.BILLTYPE, b.OITEM, b.PLUNAME, b.PLUBARCODE ,a.ORDERTIME ,a.SHIPENDTIME , b.QTY,  " +
                    " b.FLAVORSTUFFDETAIL, b.ISPACKAGE, b.PGOODSDETAIL, b.UNITID , b.UNITNAME, a.TRNO, a.TABLENO, b.GOODSSTATUS, b.ISURGE , " +
                    " a.REFUNDREASONNAME , CASE  WHEN a.ISREFUNDORDER = '0' THEN a.ISREFUNDORDER WHEN a.ISREFUNDORDER = '1' THEN  " +
                    " b.ISREFUNDORDER END AS ISREFUNDORDER " +
                    " FROM DCP_PRODUCT_SALE a " +
                    " LEFT JOIN DCP_PRODUCT_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.BILLNO = b.BILLNO  " +
                    " WHERE a.eid = '" + eId + "' AND a.SHOPID = '" + shopId + "' AND (a.ISREFUND = 'N' OR a.ISREFUND IS NULL) " +
                    " AND a.ISREFUNDORDER IS NOT NULL AND a.BILLTYPE IN ('SALE', 'ORDER')");
            sqlbuf.append(" ) a ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        }
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询当前门店该机台超时预警时间
     *
     * @param req
     * @return
     */
    protected String getIsOverTime(DCP_RefundDishQuery_OpenReq req) {
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
