package com.dsc.spos.scheduler.job;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_AbnormalOrderProcessReq;
import com.dsc.spos.json.cust.req.DCP_OrderShipping_OpenReq;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes;
import com.dsc.spos.json.cust.res.DCP_OrderShipping_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.imp.json.DCP_OrderShipping_Open;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.TokenManagerRetail;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AbnormalOrderBatchProcess_bawang extends InitJob {
    Logger logger = LogManager.getLogger(AbnormalOrderBatchProcess_bawang.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public AbnormalOrderBatchProcess_bawang()
    {

    }
    public String doExe() throws Exception
    {
        //此服务是否正在执行中
        //返回信息
        String sReturnInfo="";

        if (bRun )
        {
            logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess_bawang正在执行中,本次调用取消:************\r\n");
            this.Log("*********异常订单批量处理AbnormalOrderBatchProcess_bawang正在执行中,本次调用取消:************");
            sReturnInfo="异常订单批量处理AbnormalOrderBatchProcess_bawang正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess_bawang定时调用Start:************\r\n");
        this.Log("*********异常订单批量处理AbnormalOrderBatchProcess_bawang定时调用Start:************");
        try
        {

            //当前日期
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,-1);//当前时间减去一个月，即一个月前的时间
            String sDate_pre = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            String orderToSaleStartTime = "20221101";
            String orderToSaleEndTime = "20221123";




            String abnormalType = "goodsNotFound";



            String sql = " select * from ("
                       + " select distinct A.EID,A.ORDERNO,A.BDATE FROM DCP_ORDER A inner join dcp_order_detail B ON A.EID=B.EID and A.ORDERNO=B.ORDERNO "
                       + " where A.loaddoctype ='MEITUAN' and status<>'3' and status<>'12' "
                       + " and A.BDATE>='"+orderToSaleStartTime+"' and A.BDATE<='"+orderToSaleEndTime+"' "
                       + " AND B.Skuid like '''%'"
                       + " AND A.EID='10' "
                       + " AND A.ORDERTOSALE_DATETIME IS NULL"
                       //+ " and A.ORDERNO IN ('900297252213040068','900297272213040068')"
                       + " ORDER BY A.BDATE )";
            this.Log("异常订单批量处理AbnormalOrderBatchProcess_bawang定时任务，查询sql="+sql);
            PosPub.iTimeoutTime = 200;
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            PosPub.iTimeoutTime = 30;
            if (getQData==null||getQData.isEmpty())
            {
                logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess_bawang查询无数据，END:************\r\n");
                this.Log("异常订单批量处理AbnormalOrderBatchProcess_bawang定时任务，查询无数据！");
                sReturnInfo = "";
                return sReturnInfo;
            }
            ParseJson pj=new ParseJson();
            //下面开始轮询执行修复
            for (Map<String, Object> map_order : getQData)
            {
                String eId = map_order.getOrDefault("EID","").toString();
                String orderNo = map_order.getOrDefault("ORDERNO","").toString();
                String logStart = "异常处理，eId="+eId+",单号orderNo="+orderNo+",";
                this.Log("循环"+logStart+"【开始】");
                try
                {
                    String sql_order = " select a.shop,b.* from dcp_order a left join dcp_order_detail b on a.eid=b.eid and a.orderno=b.orderno "
                                     + " where  a.EID='"+eId+"' and a.ORDERNO='"+orderNo+"' "
                                     + "";
                    this.Log("循环"+logStart+"【查询异常商品映射sql】="+sql_order);
                    List<Map<String, Object>> getMappingGoods =this.doQueryData(sql_order, null);
                    if (getMappingGoods==null||getMappingGoods.isEmpty())
                    {
                        this.Log("循环"+logStart+"【查询异常商品映射】没有数据");
                        continue;
                    }
                    String shop = getMappingGoods.get(0).getOrDefault("SHOP","").toString();

                    List<DataProcessBean> DPB = new ArrayList<>();
                    for (Map<String, Object> map_goods : getMappingGoods)
                    {
                        String item = map_goods.getOrDefault("ITEM","").toString();
                        String packagetype = map_goods.getOrDefault("PACKAGETYPE","").toString();
                        String pluBarcode_db = map_goods.getOrDefault("PLUBARCODE","").toString();
                        String pluBarcode = pluBarcode_db.trim();
                       /* if (!"1".equals(packagetype))
                        {
                            continue;
                        }*/
                        String sql_plu = " select * from DCP_GOODS_BARCODE where  status=100 and eid='"+eId+"' and plubarcode='"+pluBarcode+"'";
                        List<Map<String, Object>> getPlu =this.doQueryData(sql_plu, null);
                        if (!CollectionUtil.isEmpty(getPlu))
                        {
                            String pluNo = getPlu.get(0).get("PLUNO").toString();
                            String sunit = getPlu.get(0).get("UNIT").toString();

                            UptBean up1 = new UptBean("dcp_order_detail");
                            up1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                            up1.addCondition("ORDERNO",new DataValue(orderNo, Types.VARCHAR));
                            up1.addCondition("ITEM",new DataValue(item, Types.VARCHAR));

                            up1.addUpdateValue("PLUNO",new DataValue(pluNo, Types.VARCHAR));
                            up1.addUpdateValue("SUNIT",new DataValue(sunit, Types.VARCHAR));
                            if (pluBarcode_db.contains(" "))
                            {
                                up1.addUpdateValue("PLUBARCODE",new DataValue(pluBarcode, Types.VARCHAR));
                            }

                            DPB.add(new DataProcessBean(up1));

                        }


                    }
                    this.useTransactionProcessData(DPB);
                    try
                    {
                      HelpTools.orderToSale(StaticInfo.dao,eId,shop,orderNo,"AbnormalOrderBatchProcess_bawang");
                    }
                    catch (Exception e)
                    {
                        //this.Log(logStart+"【调用订单异常处理DCP_AbnormalOrderProcess】异常："+ e.getMessage());
                    }

                }
                catch ( Exception e)
                {
                    this.Log("循环"+logStart+"【结束】【异常】:"+e.getMessage());
                    continue;
                }
            }



        }
        catch (Exception e)
        {
            logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess_bawang异常，END:************\r\n");
            this.Log("异常订单批量处理AbnormalOrderBatchProcess_bawang定时任务，异常:"+e.getMessage());
        }
        finally
        {
            PosPub.iTimeoutTime = 30;
            bRun=false;
            logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess_bawang定时调用END:************\r\n");
            this.Log("异常订单批量处理AbnormalOrderBatchProcess_bawang定时调用END:");
        }

        return sReturnInfo;
    }

    public void Log(String log)  {
        try
        {
            HelpTools.writelog_fileName(log, "AbnormalOrderBatchProcess_bawang");

        } catch (Exception e)
        {
            // TODO: handle exception
        }

    }

    private void useTransactionProcessData(List<DataProcessBean> DPB) throws Exception
    {
        if (!CollectionUtil.isEmpty(DPB))
        {
            StaticInfo.dao.useTransactionProcessData(DPB);
        }
    }
}
