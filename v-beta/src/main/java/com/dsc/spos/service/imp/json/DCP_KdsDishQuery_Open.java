package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_KdsDishQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_KdsDishQuery_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: KDS配菜/制作完成单据查询
 * @author: wangzyc
 * @create: 2021-09-27
 */
public class DCP_KdsDishQuery_Open extends SPosBasicService<DCP_KdsDishQuery_OpenReq, DCP_KdsDishQuery_OpenRes>
{
    Logger logger = LogManager.getLogger(DCP_KdsDishQuery_Open.class.getName());

    @Override
    protected boolean isVerifyFail(DCP_KdsDishQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_KdsDishQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getTerminalType())) {
            errMsg.append("机台类型不能为空");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("机台编号不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KdsDishQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_KdsDishQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_KdsDishQuery_OpenRes getResponseType() {
        return new DCP_KdsDishQuery_OpenRes();
    }

    @Override
    protected DCP_KdsDishQuery_OpenRes processJson(DCP_KdsDishQuery_OpenReq req) throws Exception {
        DCP_KdsDishQuery_OpenRes res = this.getResponseType();
        DCP_KdsDishQuery_OpenReq.level1Elm request = req.getRequest();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String shopId = request.getShopId();
        String eId = req.geteId();
        int pageSize = req.getPageSize();
        int pageNumber = req.getPageNumber();
        String terminalType = request.getTerminalType(); // terminalType=0，查goodsStatus=1； terminalType=1，查goodsStatus=2；

        int totalRecords = 0; // 总笔数
        int totalPages = 0;
        res.setDatas(new ArrayList<>());

        try
        {
            // 查询系统参数 KdsProduceQty(最大可制作份数)
            String strKdsProduceQty = PosPub.getPARA_SMS(dao, eId, shopId, "KdsProduceQty");
            int kdsProduceQty = Integer.parseInt(strKdsProduceQty);

            // 查询门店配置超时预警时间
            String sql = this.getIsOverTime(req);
            List<Map<String, Object>> getIsOverTime = this.doQueryData(sql, null);
            String isOverTime = ""; // 超时预警时间
            if (!CollectionUtils.isEmpty(getIsOverTime))
            {
                isOverTime = getIsOverTime.get(0).get("OVERTIME").toString();
            }

            //汇总数量+分页
            sql = this.getTastDetail_count(req);
            List<Map<String, Object>> getOverPlunos = this.doQueryData(sql, null);

            if (!CollectionUtils.isEmpty(getOverPlunos))
            {
                String num = getOverPlunos.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                if (req.getPageSize() != 0 && req.getPageNumber() != 0)
                {
                    totalPages = totalRecords / req.getPageSize();
                    totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                }

                StringBuffer sJoinOFNO=new StringBuffer("");
                StringBuffer sJoinPLUNO=new StringBuffer("");
                StringBuffer sJoinPROCESSTASKNO=new StringBuffer("");
                //这个去重
                StringBuffer sJoinDistinctOFNO=new StringBuffer("");

                for (Map<String, Object> tempmap : getOverPlunos)
                {
                    sJoinOFNO.append(tempmap.get("OFNO").toString()+",");
                    sJoinPLUNO.append(tempmap.get("PLUNO").toString()+",");
                    sJoinPROCESSTASKNO.append(tempmap.get("PROCESSTASKNO").toString()+",");

                    //找不到就加入
                    if (sJoinDistinctOFNO.indexOf(tempmap.get("OFNO").toString()+",")==-1)
                    {
                        sJoinDistinctOFNO.append(tempmap.get("OFNO").toString()+",");
                    }
                }
                //
                Map<String, String> mapOrder=new HashMap<String, String>();
                mapOrder.put("OFNO", sJoinDistinctOFNO.toString());
                //OFNO,这是查商品汇总对应单号的原单所有商品
                MyCommon cm=new MyCommon();
                String withasSql=cm.getFormatSourceMultiColWith(mapOrder);
                String orderPlunos_Sql = getOrderPlunos(req, withasSql);
                List<Map<String, Object>> getOrderPlunos = this.doQueryData(orderPlunos_Sql, null);

                //这是查汇总商品明细
                mapOrder.put("OFNO", sJoinOFNO.toString());
                mapOrder.put("PROCESSTASKNO", sJoinPROCESSTASKNO.toString());
                mapOrder.put("PLUNO", sJoinPLUNO.toString());
                withasSql=cm.getFormatSourceMultiColWith(mapOrder);
                mapOrder=null;


                //查DETAILS
                String sql_details=getTastDetail(req,withasSql);
                List<Map<String, Object>> getMultiDetails = this.doQueryData(sql_details, null);

                for (Map<String, Object> tempData : getOverPlunos)
                {
                    //明细商品
                    DCP_KdsDishQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                    String pluno = tempData.get("PLUNO").toString();
                    String processtaskno = tempData.get("PROCESSTASKNO").toString();
                    String ofno = tempData.get("OFNO").toString();
                    String qty = tempData.get("PQTY").toString();

                    if (getMultiDetails != null && getMultiDetails.size()>0)
                    {
                        List<Map<String, Object>>  getOverPluno=getMultiDetails.stream().filter(p->p.get("PLUNO").toString().equals(pluno) && p.get("PROCESSTASKNO").toString().equals(processtaskno) && p.get("BILLNO").toString().equals(ofno)).collect(Collectors.toList());

                        if (getOverPluno != null && getOverPluno.size()>0)
                        {
                            String pluName = getOverPluno.get(0).get("PLUNAME").toString();
                            String pluBarcode = getOverPluno.get(0).get("PLUBARCODE").toString();
                            String specAttrDetail = getOverPluno.get(0).get("SPECATTRDETAIL").toString();
                            String unitId = getOverPluno.get(0).get("PUNIT").toString();
                            String unitName = getOverPluno.get(0).get("UNAME").toString();
                            String flavorstuffDetail = getOverPluno.get(0).get("FLAVORSTUFFDETAIL").toString();
                            String isPackage = getOverPluno.get(0).get("ISPACKAGE").toString();
                            String pGoodsDetail = getOverPluno.get(0).get("PGOODSDETAIL").toString();
                            String repastType = getOverPluno.get(0).get("REPASTTYPE").toString();
                            String memo = getOverPluno.get(0).get("PLUMEMO").toString();
                            String assortedTime = getOverPluno.get(0).get("ASSORTEDTIME").toString();
                            String madeTime = "";
                            if (terminalType.equals("1"))
                            {
                                madeTime = getOverPluno.get(0).get("MAKETIME").toString();
                            }
                            String loadDocType = getOverPluno.get(0).get("LOADDOCTYPE").toString();
                            String oChannelName = getOverPluno.get(0).get("CHANNELNAME").toString();
                            String oChannelId = getOverPluno.get(0).get("CHANNELNAME").toString();
                            String appType = getOverPluno.get(0).get("APPTYPE").toString();
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
                            String billNo = getOverPluno.get(0).get("BILLNO").toString();
                            String trNo = getOverPluno.get(0).get("TRNO").toString();
                            String tableNo = getOverPluno.get(0).get("TABLENO").toString();
                            String adultCount = getOverPluno.get(0).get("GUESTNUM").toString();


                            lv1.setPluNo(pluno);
                            lv1.setPluName(pluName);
                            lv1.setPluBarcode(pluBarcode);
                            lv1.setQty(qty);
                            lv1.setSingleMakeQty(kdsProduceQty + "");
                            lv1.setUnitId(unitId);
                            lv1.setUnitName(unitName);
                            lv1.setFlavorstuffDetail(flavorstuffDetail);
                            lv1.setIsPackage(isPackage);
                            lv1.setPGoodsDetail(pGoodsDetail);
                            lv1.setRepastType(repastType);
                            lv1.setSpecAttrDetail(specAttrDetail);
                            lv1.setMemo(memo);
                            lv1.setAssortedTime(assortedTime);
                            lv1.setMadeTime(madeTime);
                            lv1.setOChannelId(oChannelId);
                            lv1.setOChannelName(oChannelName);
                            lv1.setLoadDocType(loadDocType);
                            lv1.setAppName(appName);
                            lv1.setProcessTaskNo(processtaskno);
                            lv1.setBillNo(billNo);
                            lv1.setTrNo(trNo);
                            lv1.setTableNo(tableNo);
                            lv1.setAdultCount(adultCount);

                            String isPrintCrossMenu = "";
                            // 是否打印划菜单Y/N
                            String isprintcrossmenu1 = getOverPluno.get(0).get("HISPRINTCROSSMENU").toString();
                            String isprintcrossmenu2 = getOverPluno.get(0).get("IISPRINTCROSSMENU").toString();
                            if(!Check.Null(isprintcrossmenu1))
                            {
                                isPrintCrossMenu = isprintcrossmenu1;
                            }
                            else if(!Check.Null(isprintcrossmenu2))
                            {
                                isPrintCrossMenu = isprintcrossmenu2;
                            }
                            lv1.setIsPrintCrossMenu(isPrintCrossMenu);

                            // 划菜打印机
                            String crossPrinterName = "";
                            String crossPrinterName1 = getOverPluno.get(0).get("HCROSSPRINTERNAME").toString();
                            String crossPrinterName2 = getOverPluno.get(0).get("ICROSSPRINTERNAME").toString();
                            if(!Check.Null(crossPrinterName1)){
                                crossPrinterName = crossPrinterName1;
                            }else if(!Check.Null(crossPrinterName2)){
                                crossPrinterName = crossPrinterName2;
                            }
                            lv1.setCrossPrinter(crossPrinterName);
                            //这个是汇总商品对应的明细item
                            lv1.setItemList(new ArrayList<>());
                            //这个不是汇总商品的明细，而是这个商品所在单据的所有商品
                            lv1.setGoodsList(new ArrayList<>());

                            for (Map<String, Object> tempItem : getOverPluno)
                            {
                                DCP_KdsDishQuery_OpenRes.level4Elm level4Elm = res.new level4Elm();
                                level4Elm.setItem(tempItem.get("ITEM").toString());
                                lv1.getItemList().add(level4Elm);
                            }

                            //
                            if (!CollectionUtils.isEmpty(getOrderPlunos))
                            {
                                List<Map<String, Object>>  billnos=getOrderPlunos.stream().filter(p->p.get("OFNO").toString().equals(billNo)).collect(Collectors.toList());

                                if (billnos != null && billnos.size()>0)
                                {
                                    for (Map<String, Object> getOrderPluno : billnos)
                                    {
                                        DCP_KdsDishQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                                        String pluno1 = getOrderPluno.get("PLUNO").toString();
                                        String pluname = getOrderPluno.get("PLUNAME").toString();
                                        String goodsStatus = getOrderPluno.get("GOODSSTATUS").toString();
                                        int status = Integer.parseInt(goodsStatus);
                                        String isUrge = getOrderPluno.get("ISURGE").toString();
                                        String sorttime = getOrderPluno.get("SORTTIME").toString();
                                        String otherQty = getOrderPluno.get("PQTY").toString();
                                        String otherPluBarcode = getOrderPluno.get("PLUBARCODE").toString();
                                        lv2.setItem(getOrderPluno.get("ITEM").toString());
                                        lv2.setPluNo(pluno1);
                                        lv2.setPluName(pluname);
                                        lv2.setGoodsStatus(goodsStatus);
                                        lv2.setIsUrge(isUrge);
                                        lv2.setQty(otherQty);
                                        lv2.setPluBarcode(otherPluBarcode);
                                        Long min = null;
                                        String overTime = "";
                                        if (Check.Null(isOverTime) || "0".equals(isOverTime))
                                        {
                                            overTime = "N";
                                        }
                                        else if (Check.Null(sorttime))
                                        {

                                        }
                                        else
                                        {
                                            int i = Integer.parseInt(isOverTime);
                                            min = getMin(sorttime);
                                            if (min >= i)
                                            {
                                                overTime = "Y";
                                            }
                                        }
                                        lv2.setIsOverTime(overTime);

                                        DCP_KdsDishQuery_OpenRes.level3Elm lv3 = res.new level3Elm();

                                        if (!Check.Null(sorttime))
                                        {
                                            sorttime = sds.format(sdf.parse(sorttime));
                                        }
                                        if (status > 0)
                                        {
                                            String assortedTime2 = getOrderPluno.get("ASSORTEDTIME").toString();
                                            if (!Check.Null(assortedTime2))
                                            {
                                                assortedTime2 = sds.format(sdf.parse(assortedTime2));
                                            }
                                            lv3.setAssortedTime(assortedTime2);
                                        }

                                        if (status > 1)
                                        {
                                            String madeTime2 = getOrderPluno.get("MAKETIME").toString();
                                            if (!Check.Null(madeTime2))
                                            {
                                                madeTime2 = sds.format(sdf.parse(madeTime2));
                                            }
                                            lv3.setMadeTime(madeTime2);
                                        }

                                        if (status > 2)
                                        {
                                            String pickupTime = getOrderPluno.get("COMPLETETIME").toString();
                                            if (!Check.Null(pickupTime))
                                            {
                                                pickupTime = sds.format(sdf.parse(pickupTime));
                                            }
                                            lv3.setPickupTime(pickupTime);
                                        }

                                        lv3.setOrderTime(sorttime);
                                        lv2.setTimeInfo(lv3);
                                        lv1.getGoodsList().add(lv2);
                                    }
                                }
                            }
                            res.getDatas().add(lv1);

                        }
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
        }
        catch (Exception e)
        {
            String sqlerr= "";

            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                sqlerr= e.getCause()==null || e.getCause().getMessage()==null?"":e.getCause().getMessage();
                if (Check.Null(sqlerr))  sqlerr=errors.toString();

                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******DCP_KdsDishQuery_Open报错信息1:" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******DCP_KdsDishQuery_Open报错信息2:" + e1.getMessage() + "******\r\n");
            }


            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + sqlerr);
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    /**
     * 按商品数量汇总+分页
     * @param req
     * @return
     */
    protected String getTastDetail_count(DCP_KdsDishQuery_OpenReq req)
    {
        DCP_KdsDishQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        int pageSize = req.getPageSize();
        int pageNumber = req.getPageNumber();
        String categoryId = request.getCategoryId();
        String terminalType = request.getTerminalType();
        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append("select * from ( " +
                              "select row_number() over(order by ");

        if ("0".equals(terminalType))
        {
            sqlbuf.append("ASSORTEDTIME desc) ");
        }
        else if ("1".equals(terminalType))
        {
            sqlbuf.append("MAKETIME desc) ");
        }

        sqlbuf.append(" rn, count(*) over() num, PROCESSTASKNO,OFNO,PLUNO, PQTY from ( " +
                              "SELECT PROCESSTASKNO,OFNO,PLUNO,SUM(PQTY) PQTY ");

        if ("0".equals(terminalType))
        {
            sqlbuf.append(",max(ASSORTEDTIME) ASSORTEDTIME ");
        }
        else if ("1".equals(terminalType))
        {
            sqlbuf.append(",max(MAKETIME) MAKETIME ");
        }
        sqlbuf.append(" from ( " +
                              "select b.* " +
                              "FROM DCP_PROCESSTASK a " +
                              "LEFT JOIN DCP_PROCESSTASK_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO =b.PROCESSTASKNO " +
                              "LEFT JOIN DCP_PRODUCT_SALE e ON a.EID = e.EID  AND b.OFNO = e.BILLNO  AND b.SHOPID = e.SHOPID " +
                              " WHERE a.EID = '" + req.geteId() + "' AND a.SHOPID = '" + shopId + "' AND a.OTYPE IS NOT NULL and (e.ISREFUND = 'N' OR e.ISREFUND is null) ");
        if (!Check.Null(categoryId))
        {
            sqlbuf.append(" AND b.FINALCATEGORY = '" + categoryId + "' ");
        }
        if ("0".equals(terminalType))
        {
            sqlbuf.append("  AND b.GOODSSTATUS = '1' ");
            sqlbuf.append("   and b.ASSORTEDTIME is not null ");
        }
        else if ("1".equals(terminalType))
        {
            sqlbuf.append("  AND b.GOODSSTATUS = '2' ");
            sqlbuf.append("   and b.MAKETIME is not null ");
        }


        sqlbuf.append(" ) group by PROCESSTASKNO,OFNO,pluno ) ");
        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 多单的item
     * @param req
     * @param withasSql
     * @return
     */
    protected String getTastDetail_items(DCP_KdsDishQuery_OpenReq req,String withasSql)
    {
        DCP_KdsDishQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String terminalType = request.getTerminalType();

        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("with b AS ( "
                                                       + withasSql + " ) "
                                                       + "select a.ITEM,b.* from b ");

        sqlbuf.append("inner join  DCP_PROCESSTASK_DETAIL  a on a.pluno=b.pluno and a.PROCESSTASKNO=b.PROCESSTASKNO and a.OFNO=B.OFNO " +
                              "WHERE a.EID = '" + req.geteId() + "' AND a.SHOPID = '" + shopId + "' ");
        if ("0".equals(terminalType))
        {
            sqlbuf.append("  AND a.GOODSSTATUS = '1' ");
        }
        else if ("1".equals(terminalType))
        {
            sqlbuf.append("  AND a.GOODSSTATUS = '2' ");
        }

        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询第一层返回
     * @param req
     * @param withasSql
     * @return
     * @throws Exception
     */
    protected String getTastDetail(DCP_KdsDishQuery_OpenReq req,String withasSql) throws Exception
    {
        String sql = "";
        String langType = req.getLangType();
        DCP_KdsDishQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String categoryId = request.getCategoryId();
        String terminalType = request.getTerminalType();

        StringBuffer sqlbuf = new StringBuffer("with c AS ( "
                                                       + withasSql + " ) ");
        sqlbuf.append(" SELECT  b.PROCESSTASKNO, b.PLUNO, b.PLUNAME,b.item,  " +
                              " b.PQTY , NVL(e.MAKETIME, b.MAKETIME) AS MAKETIME, b.PLUBARCODE , b.FLAVORSTUFFDETAIL, NVL(b.MEMO,a.MEMO) AS pluMEMo,  " +
                              " CASE WHEN b.SPECNAME IS NOT NULL AND b.ATTRNAME IS NULL THEN '(' || b.SPECNAME  || ')'  " +
                              "  WHEN b.SPECNAME IS  NULL AND b.ATTRNAME IS NOT NULL  THEN '('  || b.ATTRNAME || ')'  " +
                              "  WHEN b.SPECNAME IS not NULL AND b.ATTRNAME IS NOT NULL THEN '(' || b.SPECNAME || ',' || b.ATTRNAME || ')' END  AS specAttrDetail , " +
                              " b.PUNIT, d.UNAME , b.ISPACKAGE, b.PGOODSDETAIL, b.GOODSSTATUS, b.BDATE, e.ORDERTIME , NVL(e.LOADDOCTYPE,a.LOADDOCTYPE) LOADDOCTYPE, f.CHANNELNAME AS " +
                              " CHANNELNAME,e.CHANNELID, e.APPTYPE, b.OFNO BILLNO, e.TRNO , e.TABLENO, e.GUESTNUM , CASE  WHEN e.REPASTTYPE IS NULL AND a.OTYPE !=  " +
                              " 'BEFORE' AND (e.LOADDOCTYPE = 'WAIMAI' OR e.LOADDOCTYPE = 'MEITUAN' OR e.LOADDOCTYPE = 'ELEME') THEN 2 ELSE  " +
                              " TO_NUMBER(e.REPASTTYPE) END AS REPASTTYPE, e.MEMO, b.ISURGE, e.SHIPENDTIME, a.otype , NVL(e.ORDERTIME,  " +
                              " SUBSTR(a.CREATEDATETIME, 1, 14)) AS CREATEDATETIME,b.ASSORTEDTIME,b.COMPLETETIME,e.SHIPENDTIME,e.ISREFUND," +
                              " CASE WHEN e.REPASTTYPE = 2 THEN e.SHIPENDTIME " +
                              " WHEN a.otype = 'BEFORE' THEN a.CREATEDATETIME " +
                              " WHEN (e.REPASTTYPE = 1 OR e.REPASTTYPE = 0) AND a.otype != 'BEFORE' THEN e.ORDERTIME END AS sorttime ," +
                              " h.ISPRINTCROSSMENU hISPRINTCROSSMENU,h.CROSSPRINTERNAME hCROSSPRINTERNAME,i.ISPRINTCROSSMENU iISPRINTCROSSMENU,i.CROSSPRINTERNAME iCROSSPRINTERNAME" +
                              " FROM DCP_PROCESSTASK a " +
                              " INNER JOIN DCP_PROCESSTASK_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO " +
                              " inner join c on b.pluno=c.pluno and b.PROCESSTASKNO=c.PROCESSTASKNO and (b.OFNO=c.OFNO or b.ofno is null) " +
                              " LEFT JOIN DCP_UNIT_LANG d ON a.EID = d.EID AND b.PUNIT = d.UNIT AND d.LANG_TYPE = '" + langType + "' " +
                              " LEFT JOIN DCP_PRODUCT_SALE e ON a.EID = e.EID AND b.OFNO = e.BILLNO AND b.SHOPID = e.SHOPID " +
                              " LEFT JOIN CRM_CHANNEL f ON e.CHANNELID = f.CHANNELID  and a.EID = f.eid" +
                              " LEFT JOIN DCP_KITCHENPRINTSET h  ON h.EID  = a.EID  AND h.SHOPID  = a.SHOPID  AND h.ID = b.PLUNO AND h.\"TYPE\" = 'GOODS' " +
                              " LEFT JOIN DCP_KITCHENPRINTSET i  ON i.EID  = a.EID  AND i.SHOPID  = a.SHOPID  AND i.ID = b.PLUNO AND i.\"TYPE\" = 'CATEGORY'" +
                              " WHERE a.EID = '" + req.geteId() + "' AND a.SHOPID = '" + shopId + "' ");
        if (!Check.Null(categoryId))
        {
            sqlbuf.append(" AND b.FINALCATEGORY = '" + categoryId + "' ");
        }
        if ("0".equals(terminalType))
        {
            sqlbuf.append("  AND b.GOODSSTATUS = '1' ");
        }
        else if ("1".equals(terminalType))
        {
            sqlbuf.append("  AND b.GOODSSTATUS = '2' ");
        }

        sql = sqlbuf.toString();
        return sql;
    }

    @Override
    protected String getQuerySql(DCP_KdsDishQuery_OpenReq req) throws Exception {
        String sql = "";
        String langType = req.getLangType();
        DCP_KdsDishQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        int pageSize = req.getPageSize();
        int pageNumber = req.getPageNumber();
        String categoryId = request.getCategoryId();
        String terminalType = request.getTerminalType();
        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" select * from(" +
                              "SELECT row_number() OVER (ORDER BY b.pluno) AS rn, count(*) OVER () AS num , b.PROCESSTASKNO, b.PLUNO, b.PLUNAME,b.item,  " +
                              " b.PQTY , NVL(e.MAKETIME, b.MAKETIME) AS MAKETIME, b.PLUBARCODE , b.FLAVORSTUFFDETAIL, NVL(b.MEMO,a.MEMO) AS pluMEMo,  " +
                              " CASE WHEN b.SPECNAME IS NOT NULL AND b.ATTRNAME IS NULL THEN '(' || b.SPECNAME  || ')'  " +
                              "  WHEN b.SPECNAME IS  NULL AND b.ATTRNAME IS NOT NULL  THEN '('  || b.ATTRNAME || ')'  " +
                              "  WHEN b.SPECNAME IS not NULL AND b.ATTRNAME IS NOT NULL THEN '(' || b.SPECNAME || ',' || b.ATTRNAME || ')' END  AS specAttrDetail , " +
                              " b.PUNIT, d.UNAME , b.ISPACKAGE, b.PGOODSDETAIL, b.GOODSSTATUS, b.BDATE, e.ORDERTIME , NVL(e.LOADDOCTYPE,a.LOADDOCTYPE) LOADDOCTYPE, f.CHANNELNAME AS " +
                              " CHANNELNAME,e.CHANNELID, e.APPTYPE, e.BILLNO, e.TRNO , e.TABLENO, e.GUESTNUM , CASE  WHEN e.REPASTTYPE IS NULL AND a.OTYPE !=  " +
                              " 'BEFORE' AND (e.LOADDOCTYPE = 'WAIMAI' OR e.LOADDOCTYPE = 'MEITUAN' OR e.LOADDOCTYPE = 'ELEME') THEN 2 ELSE  " +
                              " TO_NUMBER(e.REPASTTYPE) END AS REPASTTYPE, e.MEMO, b.ISURGE, e.SHIPENDTIME, a.otype , NVL(e.ORDERTIME,  " +
                              " SUBSTR(a.CREATEDATETIME, 1, 14)) AS CREATEDATETIME,b.ASSORTEDTIME,e.ISREFUND, h.ISPRINTCROSSMENU hISPRINTCROSSMENU,h.CROSSPRINTERNAME hCROSSPRINTERNAME,i.ISPRINTCROSSMENU iISPRINTCROSSMENU,i.CROSSPRINTERNAME iCROSSPRINTERNAME" +
                              " FROM DCP_PROCESSTASK a " +
                              " LEFT JOIN DCP_PROCESSTASK_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO " +
                              " LEFT JOIN DCP_UNIT_LANG d ON a.EID = d.EID AND b.PUNIT = d.UNIT AND d.LANG_TYPE = '" + langType + "' " +
                              " LEFT JOIN DCP_PRODUCT_SALE e ON a.EID = e.EID AND b.OFNO = e.BILLNO AND b.SHOPID = e.SHOPID " +
                              " LEFT JOIN CRM_CHANNEL f ON e.CHANNELID = f.CHANNELID  and a.EID = f.eid" +
                              " LEFT JOIN DCP_KITCHENPRINTSET h  ON h.EID  = a.EID  AND h.SHOPID  = a.SHOPID  AND h.ID = b.PLUNO AND h.\"TYPE\" = 'GOODS' " +
                              " LEFT JOIN DCP_KITCHENPRINTSET i  ON i.EID  = a.EID  AND i.SHOPID  = a.SHOPID  AND i.ID = b.PLUNO AND i.\"TYPE\" = 'CATEGORY'" +
                              " WHERE a.EID = '" + req.geteId() + "' AND a.SHOPID = '" + shopId + "' AND a.OTYPE IS NOT NULL and (e.ISREFUND = 'N' OR e.ISREFUND is null) ");
        if (!Check.Null(categoryId)) {
            sqlbuf.append(" AND b.FINALCATEGORY = '" + categoryId + "' ");
        }
        if ("0".equals(terminalType)) {
            sqlbuf.append("  AND b.GOODSSTATUS = '1' ");
        } else if ("1".equals(terminalType)) {
            sqlbuf.append("  AND b.GOODSSTATUS = '2' ");
        }

        sqlbuf.append(" ORDER BY b.MAKETIME DESC");
        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }


    /**
     * 根据第一层的商品对应的单号，查整单商品
     * @param req
     * @param withasSql
     * @return
     */
    protected String getOrderPlunos(DCP_KdsDishQuery_OpenReq req, String withasSql)
    {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("with d AS ( "
                                                       + withasSql + " ) ");
        sqlbuf.append(" SELECT a.* , " +
                              " CASE  WHEN REPASTTYPE = 2 THEN SHIPENDTIME WHEN a.otype = 'BEFORE' THEN CREATEDATETIME WHEN (REPASTTYPE  " +
                              " = 1 OR REPASTTYPE = 0) AND a.otype != 'BEFORE' THEN ORDERTIME END AS sorttime  " +
                              " FROM ( " +
                              " SELECT d.ofno,a.ITEM, a.PLUNO, a.PLUNAME, a.GOODSSTATUS, a.ISURGE , c.otype, c.CREATEDATETIME, b.ORDERTIME," +
                              " CASE WHEN a.SPECNAME IS NOT NULL AND a.ATTRNAME IS NULL THEN '(' || a.SPECNAME  || ')'  " +
                              "  WHEN a.SPECNAME IS  NULL AND a.ATTRNAME IS NOT NULL  THEN '('  || a.ATTRNAME || ')'  " +
                              "  WHEN a.SPECNAME IS not NULL AND a.ATTRNAME IS NOT NULL THEN '(' || a.SPECNAME || ',' || a.ATTRNAME || ')' END  AS specAttrDetail , " +
                              "  b.SHIPENDTIME , CASE  WHEN b.REPASTTYPE IS NULL AND c.OTYPE != 'BEFORE' AND (b.LOADDOCTYPE = 'WAIMAI' OR b.LOADDOCTYPE = 'MEITUAN' OR b.LOADDOCTYPE = 'ELEME')" +
                              " THEN 2 ELSE TO_NUMBER(b.REPASTTYPE) END AS REPASTTYPE,a.ASSORTEDTIME,a.MAKETIME,a.COMPLETETIME,a.PQTY,a.PLUBARCODE,a.FLAVORSTUFFDETAIL,a.memo " +
                              "  FROM DCP_PROCESSTASK_DETAIL a " +
                              " inner join d on a.ofno=d.ofno " +
                              " LEFT JOIN DCP_PROCESSTASK c ON a.EID = c.EID AND a.SHOPID = c.SHOPID AND a.PROCESSTASKNO = c.PROCESSTASKNO " +
                              "  LEFT JOIN DCP_PRODUCT_SALE b ON a.EID = b.EID AND a.OFNO = b.BILLNO AND a.SHOPID = b.SHOPID  " +
                              " WHERE a.eid = '" + req.geteId() + "' AND a.SHOPID = '" + req.getRequest().getShopId() + "' ) a");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询当前门店该机台超时预警时间
     *
     * @param req
     * @return
     */
    protected String getIsOverTime(DCP_KdsDishQuery_OpenReq req) {
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
