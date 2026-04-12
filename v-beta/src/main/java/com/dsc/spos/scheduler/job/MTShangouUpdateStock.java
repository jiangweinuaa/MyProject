package com.dsc.spos.scheduler.job;

import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMSGProductService;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.A;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MTShangouUpdateStock extends InitJob {
    Logger logger = LogManager.getLogger(MTShangouUpdateStock.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中
    String goodsLogFileName = "MTShangouUpdateStock";

    public MTShangouUpdateStock()
    {

    }
    public String doExe() throws Exception
    {
        String sReturnInfo="";
        try
        {
            if (bRun )
            {
                logger.info("\r\n*********闪购商品库存同步MTShangouUpdateStock正在执行中,本次调用取消:************\r\n");
                HelpTools.writelog_fileName("【同步任务MTShangouUpdateStock】同步正在执行中,本次调用取消！",goodsLogFileName);
                sReturnInfo="闪购商品库存同步MTShangouUpdateStock正在执行中！";
                return sReturnInfo;
            }
            bRun=true;
            logger.info("\r\n*********闪购商品库存同步MTShangouUpdateStock定时调用Start:************\r\n");
            HelpTools.writelog_fileName("【同步任务MTShangouUpdateStock】定时调用Start",goodsLogFileName);

            boolean runTimeFlag = this.jobRunTimeFlag();
            if(!runTimeFlag)
            {
                sReturnInfo= "【同步任务MTShangouUpdateStock】不在job运行时间内！";
                HelpTools.writelog_fileName(sReturnInfo+"定时调用End",goodsLogFileName);
                return sReturnInfo;
            }
            /****查询所有外卖平台做过映射的门店（返回企业编码COMPANYNO，门店编码SHOP，默认仓库WAREHOUSE）****/
            String loadDocType = orderLoadDocType.MTSG;
            String sql_mappingShop = this.getMappingShopSql(loadDocType);
            HelpTools.writelog_fileName("【同步任务MTShangouUpdateStock】查询平台映射门店sql="+sql_mappingShop,goodsLogFileName);
            List<Map<String, Object>> mappingShopList = this.doQueryData(sql_mappingShop, null);
            if(mappingShopList==null||mappingShopList.isEmpty())
            {
                sReturnInfo= "【同步任务MTShangouUpdateStock】查询平台映射门店数据为空！";
                HelpTools.writelog_fileName("【同步任务MTShangouUpdateStock】查询平台映射门店数据为空！",goodsLogFileName);
                return  sReturnInfo;
            }
            /*********************开始循环门店**************************/
            String logStart = "";
            for (Map<String, Object> mapShop : mappingShopList)
            {
                String eId = mapShop.getOrDefault("EID", "").toString();
                String shopNo = mapShop.getOrDefault("SHOP", "").toString();
                String warehouse = mapShop.getOrDefault("WAREHOUSE", "").toString();
                logStart = "【循环门店】开始，企业eId="+eId+",门店shop="+shopNo;
                HelpTools.writelog_fileName(logStart,goodsLogFileName);
                try
                {
                    if(eId.isEmpty()||shopNo.isEmpty()||warehouse.isEmpty())
                    {
                        HelpTools.writelog_fileName(logStart+",仓库warehouse="+warehouse+",存在为空的基础资料！continue",goodsLogFileName);
                        continue;
                    }
                    /**************判断参数AutoOnShelf是否开启*************/
                    boolean runParaFlag = this.jobRunParaFlag(eId, shopNo);
                    if(!runParaFlag)
                    {
                        HelpTools.writelog_fileName(logStart+",参数值MTSGAutoUpdateStock不等于Y,continue;",goodsLogFileName);
                        continue;
                    }

                    /*****************根据绑定门店查询所有平台对应的商品实时库存*******************************/

                    String sql_goodsStock = this.getMappingShopGoodsStockSql(eId, shopNo, warehouse,loadDocType);
                    HelpTools.writelog_fileName(logStart+",查询映射商品实时库存sql="+sql_goodsStock,goodsLogFileName);
                    List<Map<String, Object>> goodsStockList = this.doQueryData(sql_goodsStock, null);
                    if(goodsStockList==null||goodsStockList.isEmpty())
                    {
                        HelpTools.writelog_fileName(logStart+",查询映射商品实时库存数据为空,continue",goodsLogFileName);
                        continue;
                    }
                    Map<String, Boolean> condition1 = new HashMap<String, Boolean>();
                    condition1.put("PLUNO", true);
                    condition1.put("SPECNO", true);
                    List<Map<String, Object>> sKuStockList = MapDistinct.getMap(goodsStockList, condition1);
                    condition1.clear();
                    condition1.put("PLUNO", true);
                    List<Map<String, Object>> spuStockList = MapDistinct.getMap(sKuStockList, condition1);

                    /*******************根据绑定门店查询对应的外卖平台***********************/
                    String sql_mappingShoploadDocType = " select * from DCP_MAPPINGSHOP where BUSINESSID='2' AND LOAD_dOCTYPE='"+loadDocType+"' and EID='"+eId+"' AND SHOPID='"+shopNo+"'";
                    HelpTools.writelog_fileName(logStart+",查询映射门店对应的外卖平台类型sql="+sql_mappingShoploadDocType,goodsLogFileName);
                    List<Map<String, Object>> mappingShoploadDocTypeList = this.doQueryData(sql_mappingShoploadDocType, null);
                    if(mappingShoploadDocTypeList==null||mappingShoploadDocTypeList.isEmpty())
                    {
                        HelpTools.writelog_fileName(logStart+",查询映射门店对应的外卖平台类型数据为空,continue",goodsLogFileName);
                        continue;
                    }
                    String orderShopNo = mappingShoploadDocTypeList.get(0).get("ORDERSHOPNO").toString();
                    logStart +=",三方门店ID="+orderShopNo;
                    HelpTools.writelog_fileName(logStart+",需要更新库存spu商品总数:"+spuStockList.size(),goodsLogFileName);
                    int pageSize = 200;//一次最多同步200个SPU
                    int pageCount = spuStockList.size()/pageSize;
                    pageCount = (spuStockList.size() % pageSize > 0) ? pageCount + 1 : pageCount;
                    if (pageCount>1)
                    {
                        HelpTools.writelog_fileName(logStart+",需要更新库存spu商品总数>"+pageSize+",需要分页更新。每页最多"+pageSize+"个SPU",goodsLogFileName);
                        for (int j =1;j<=pageCount;j++)
                        {
                            HelpTools.writelog_fileName(logStart+",分页更新库存，当前第"+j+"页,更新开始",goodsLogFileName);
                            int startRow = (j-1) * pageSize;//0
                            int endRow = startRow +pageSize;//200

                            if (endRow > spuStockList.size()) {
                                endRow = spuStockList.size();
                            }
                            List<Map<String,Object>> mapList_food_data = new ArrayList<>();
                            for (int k=startRow;k<endRow;k++)
                            {
                                Map<String, Object> spu_map = spuStockList.get(k);
                                String app_spu_code = spu_map.getOrDefault("PLUNO","").toString();
                                if (app_spu_code.trim().isEmpty())
                                {
                                    continue;
                                }
                                Map<String,Object> map_food_data = new HashMap<>();
                                List<Map<String,Object>> mapsKUList = new ArrayList<>();
                                for (Map<String, Object> sku_map : sKuStockList)
                                {
                                    String app_spu_code_detail = sku_map.getOrDefault("PLUNO","").toString();
                                    String sku_id = sku_map.getOrDefault("SPECNO","").toString();
                                    if (sku_id.trim().isEmpty()||!app_spu_code.equals(app_spu_code_detail))
                                    {
                                        continue;
                                    }

                                    String qty = sku_map.getOrDefault("QTY","0").toString();
                                    if (qty.isEmpty())
                                    {
                                        qty = "0";
                                    }
                                    BigDecimal qty_b = new BigDecimal("0");
                                    try
                                    {
                                        qty_b = new BigDecimal(qty);
                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    if (qty_b.compareTo(BigDecimal.ZERO)==-1)
                                    {
                                        qty_b = new BigDecimal("0");
                                    }

                                    int qty_int = qty_b.setScale(0,BigDecimal.ROUND_CEILING).intValue();
                                    if (qty_int<0)
                                    {
                                        qty_int = 0;
                                    }
                                    Map<String,Object> sku = new HashMap<>();
                                    sku.put("sku_id",sku_id);
                                    sku.put("stock",qty_int);
                                    mapsKUList.add(sku);
                                }

                                if (mapsKUList.isEmpty())
                                {
                                    continue;
                                }
                                map_food_data.put("skus",mapsKUList);
                                map_food_data.put("app_spu_code",app_spu_code);
                                mapList_food_data.add(map_food_data);
                            }
                            if (mapList_food_data.isEmpty())
                            {
                                continue;
                            }
                            StringBuilder errorMessage = new StringBuilder();
                            WMSGProductService.updateStock(orderShopNo,mapList_food_data,errorMessage);
                            HelpTools.writelog_fileName(logStart+",分页更新库存，当前第"+j+"页,更新结束",goodsLogFileName);
                        }

                    }
                    else
                    {
                        HelpTools.writelog_fileName(logStart+",需要更新库存spu商品总数<"+pageSize+",无需分页，更新开始",goodsLogFileName);
                        List<Map<String,Object>> mapList_food_data = new ArrayList<>();
                        for (Map<String, Object> spu_map : spuStockList)
                        {
                            String app_spu_code = spu_map.getOrDefault("PLUNO","").toString();
                            if (app_spu_code.trim().isEmpty())
                            {
                                continue;
                            }
                            Map<String,Object> map_food_data = new HashMap<>();
                            List<Map<String,Object>> mapsKUList = new ArrayList<>();
                            for (Map<String, Object> sku_map : sKuStockList)
                            {
                                String app_spu_code_detail = sku_map.getOrDefault("PLUNO","").toString();
                                String sku_id = sku_map.getOrDefault("SPECNO","").toString();
                                if (sku_id.trim().isEmpty()||!app_spu_code_detail.equals(app_spu_code))
                                {
                                    continue;
                                }

                                String qty = sku_map.getOrDefault("QTY","0").toString();
                                BigDecimal qty_b = new BigDecimal("0");
                                try
                                {
                                    qty_b = new BigDecimal(qty);
                                }
                                catch (Exception e)
                                {

                                }
                                if (qty_b.compareTo(BigDecimal.ZERO)==-1)
                                {
                                    qty_b = new BigDecimal("0");
                                }

                                int qty_int = qty_b.setScale(0,BigDecimal.ROUND_CEILING).intValue();
                                if (qty_int<0)
                                {
                                    qty_int = 0;
                                }
                                if (qty_int>9999)
                                {
                                    qty_int = 9999;
                                }

                                Map<String,Object> sku = new HashMap<>();
                                sku.put("sku_id",sku_id);
                                sku.put("stock",qty_int);
                                mapsKUList.add(sku);
                            }

                            if (mapsKUList.isEmpty())
                            {
                                continue;
                            }
                            map_food_data.put("skus",mapsKUList);
                            map_food_data.put("app_spu_code",app_spu_code);
                            mapList_food_data.add(map_food_data);
                        }

                        if (mapList_food_data.isEmpty())
                        {
                            continue;
                        }
                        StringBuilder errorMessage = new StringBuilder();
                        WMSGProductService.updateStock(orderShopNo,mapList_food_data,errorMessage);
                        HelpTools.writelog_fileName(logStart+",需要更新库存spu商品总数<200,无需分页，更新结束",goodsLogFileName);
                    }

                }
                catch (Exception e)
                {
                    continue;
                }

            }

        }
        catch (Exception e)
        {
            logger.error("\r\n******闪购商品库存同步MTShangouUpdateStock报错信息:" + e.getMessage()+"\r\n******\r\n");
            HelpTools.writelog_fileName("【同步任务MTShangouUpdateStock】同步正在执行中,异常:"+ e.getMessage(),goodsLogFileName);
            sReturnInfo="错误信息:" + e.getMessage();
        }
        finally {
            bRun=false;//
            logger.info("\r\n*********闪购商品库存同步MTShangouUpdateStock定时调用End:************\r\n");
            HelpTools.writelog_fileName("【同步任务MTShangouUpdateStock】定时调用End",goodsLogFileName);
        }
        return sReturnInfo;
    }

    /**
     * job运行时间，（如果没有设置，默认早上6点到晚上23点）
     * @return
     * @throws Exception
     */
    private boolean jobRunTimeFlag() throws Exception
    {
        boolean flag = true;
        String sdate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String stime = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        // 先查 job 执行时间，然后再执行后续操作
        String getTimeSql = "select * from job_quartz_detail where job_name = 'MTSGAutoUpdateStock'  and cnfflg = 'Y' ";
        List<Map<String, Object>> getTimeDatas = this.doQueryData(getTimeSql, null);
        if (getTimeDatas != null && !getTimeDatas.isEmpty())
        {
            boolean isTime = false;
            for (Map<String, Object> map : getTimeDatas)
            {
                String beginTime = map.get("BEGIN_TIME").toString();
                String endTime = map.get("END_TIME").toString();

                // 如果当前时间在 执行时间范围内， 就执行
                if (stime.compareTo(beginTime) >= 0 && stime.compareTo(endTime) < 0)
                {
                    isTime = true;
                    break;
                }
            }
            if (!isTime)
            {
                return false;
            }

        }
        else// 如果没设置执行时间， 默认6点到晚上22点执行
        {

            if (stime.compareTo("060000") >= 0 && stime.compareTo("230000") < 0)
            {

            }
            else
            {
                HelpTools.writelog_fileName("【同步任务MTShangouUpdateStock】当前时间["+stime+"];不在job默认执行时间内，默认执行时间[060000]-[230000]",goodsLogFileName);
                flag = false;
            }

        }
        return flag;
    }

    /**
     * 查询所有外卖平台做过映射的门店（返回企业编码，门店编码，默认仓库）
     * @return
     * @throws Exception
     */
    private String getMappingShopSql(String loadDocType) throws Exception
    {
        StringBuffer sqlStrBuffer = new StringBuffer("");
        String sql = "";

        sqlStrBuffer.append(" select * from (");
        sqlStrBuffer.append(" select Distinct A.EID,A.ORGANIZATIONNO as SHOP,A.OUT_COST_WAREHOUSE AS WAREHOUSE from DCP_org A");
        sqlStrBuffer.append(" inner join DCP_mappingshop B on  A.EID=B.EID and A.ORGANIZATIONNO=B.SHOPID");
        sqlStrBuffer.append(" where A.STATUS='100' and B.BUSINESSID='2' and B.LOAD_DOCTYPE='"+loadDocType+"' order by A.ORGANIZATIONNO ");
        sqlStrBuffer.append(")");

        sql = sqlStrBuffer.toString();
        return sql;
    }

    /**
     * 参数值是否开启
     * @param eId
     * @param shopNo
     * @return
     * @throws Exception
     */
    private boolean jobRunParaFlag(String eId,String shopNo) throws Exception
    {
        boolean flag = true;
        String upperPara = "MTSGAutoUpdateStock";
        //upperPara = upperPara.toUpperCase();
        String isAutoUpdateStock = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopNo, "MTSGAutoUpdateStock");

        if("Y".equals(isAutoUpdateStock))
        {
            flag = true;
        }
        else
        {
            flag = false;
        }
        return flag;
    }

    /**
     * 查询平台映射门店商品默认出货成本仓实时库存
     * @param eId
     * @param shopNo
     * @param warehouse
     * @return
     * @throws Exception
     */
    private String getMappingShopGoodsStockSql(String eId,String shopNo,String warehouse,String loadDocType) throws Exception
    {
        StringBuffer sqlStrBuffer = new StringBuffer("");
        String sql = "";

        //查询映射得商品pluno
        //更换成新的规格关联的PLUNO
        String sql_pluno = " with p as ( "
                + " select A.EID, A.SHOPID,B.PLUNO,B.SPECNO,B.ORDER_PLUNO,B.ORDER_SPECNO,C.PLUNO PLUNO_DCP,C.UNIT UNIT_DCP,C.FEATURENO from dcp_mappinggoods A "
                + " inner join dcp_mappinggoods_spec B on A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.LOAD_DOCTYPE=B.LOAD_DOCTYPE AND A.ORDER_PLUNO=B.ORDER_PLUNO "
                + " left join DCP_GOODS_BARCODE C on  C.EID=B.EID AND C.PLUBARCODE=B.SPECNO "
                + " where A.EID='"+eId+"' AND A.LOAD_DOCTYPE='"+loadDocType+"' AND  A.SHOPID='"+shopNo+"' AND A.PLUNO<>' ' AND B.SPECNO<>' ' "
                + " ) ";

        sqlStrBuffer.append(" select * from (");
        sqlStrBuffer.append(sql_pluno);
        sqlStrBuffer.append(" select P.*,B.QTY,B.LOCKQTY,B.BASEUNIT,B.WAREHOUSE from P  "
                + " left join dcp_stock B on P.EID=B.EID AND P.SHOPID=B.ORGANIZATIONNO  AND P.PLUNO_DCP=B.PLUNO AND P.FEATURENO=B.FEATURENO  and B.WAREHOUSE='"+warehouse+"'"
                + " where P.EID='"+eId+"' and P.SHOPID='"+shopNo+"'");
        sqlStrBuffer.append(")");

        sql = sqlStrBuffer.toString();
        return sql;
    }
}
