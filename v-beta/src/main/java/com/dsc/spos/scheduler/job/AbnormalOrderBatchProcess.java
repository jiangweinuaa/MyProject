package com.dsc.spos.scheduler.job;

import cn.hutool.socket.nio.NioUtil;
import com.dsc.spos.json.cust.req.DCP_AbnormalOrderProcessReq;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.TokenManagerRetail;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.text.SimpleDateFormat;
import java.util.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AbnormalOrderBatchProcess extends InitJob {
    Logger logger = LogManager.getLogger(AbnormalOrderBatchProcess.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public AbnormalOrderBatchProcess ()
    {

    }
    public String doExe()
    {
        //此服务是否正在执行中
        //返回信息
        String sReturnInfo="";

        if (bRun )
        {
            logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess正在执行中,本次调用取消:************\r\n");
            this.Log("*********异常订单批量处理AbnormalOrderBatchProcess正在执行中,本次调用取消:************");
            sReturnInfo="异常订单批量处理AbnormalOrderBatchProcess正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess定时调用Start:************\r\n");
        this.Log("*********异常订单批量处理AbnormalOrderBatchProcess定时调用Start:************");
        try
        {
            //先查询下映射表有没有数据，没有数据，无须执行job
            String sql_mappingGoods = " select * from dcp_abnormalgood_mapping where rownum=1 ";
            this.Log("异常订单批量处理AbnormalOrderBatchProcess定时任务，查询异常商品映射表sql="+sql_mappingGoods);
            List<Map<String, Object>> getQData_mappingGoods=this.doQueryData(sql_mappingGoods, null);
            if (getQData_mappingGoods==null||getQData_mappingGoods.isEmpty())
            {
                logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess查询异常商品映射表dcp_abnormalgood_mapping无数据，END:************\r\n");
                this.Log("异常订单批量处理AbnormalOrderBatchProcess定时任务，查询异常商品映射表dcp_abnormalgood_mapping无数据,无需执行job");
                sReturnInfo = "";
                return sReturnInfo;
            }
            //当前日期
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,-1);//当前时间减去一个月，即一个月前的时间
            String sDate_pre = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            String orderToSaleStartTime = "";
            String orderToSaleEndTime = "";
            try
            {
                RedisPosPub redis = new RedisPosPub();
                String redisValue = redis.getString(orderRedisKeyInfo.redis_AbnormalOrderProcessToSaleTime);
                JSONObject obj = new JSONObject(redisValue);
                orderToSaleStartTime = obj.optString("orderToSaleStartTime","");
                orderToSaleEndTime = obj.optString("orderToSaleEndTime","");
                this.Log("异常订单批量处理AbnormalOrderBatchProcess定时任务，从redis获取订转销日期,开始日期orderToSaleStartTime="+orderToSaleStartTime+",结束日期orderToSaleEndTime="+orderToSaleEndTime);
            }
            catch (Exception e)
            {

            }
           if (orderToSaleStartTime==null||orderToSaleStartTime.isEmpty()||orderToSaleEndTime==null||orderToSaleEndTime.isEmpty())
           {
               orderToSaleStartTime = sDate_pre;
               orderToSaleEndTime = sDate;
               this.Log("异常订单批量处理AbnormalOrderBatchProcess定时任务，从redis获取订转销为空,取当前日期。开始日期orderToSaleStartTime="+orderToSaleStartTime+",结束日期orderToSaleEndTime="+orderToSaleEndTime);
           }



            String abnormalType = "goodsNotFound";



            String sql = " select * from ("
                       + " select distinct A.EID,A.ORDERNO,A.BDATE FROM DCP_ORDER A inner join dcp_order_abnormalinfo_detail B ON A.EID=B.EID and A.ORDERNO=B.ORDERNO "
                       + " where A.SHOP<>' ' AND A.SHOP IS NOT NULL AND A.Exceptionstatus='Y' and B.STATUS='0' and B.ABNORMALTYPE='"+abnormalType+"' "
                       + " and A.BDATE>='"+orderToSaleStartTime+"' and A.BDATE<='"+orderToSaleEndTime+"' "
                       + " ORDER BY A.BDATE )";
            this.Log("异常订单批量处理AbnormalOrderBatchProcess定时任务，查询sql="+sql);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (getQData==null||getQData.isEmpty())
            {
                logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess查询无数据，END:************\r\n");
                this.Log("异常订单批量处理AbnormalOrderBatchProcess定时任务，查询无数据！");
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
                    String sql_order = " select * from ("
                                     + " SELECT A.OITEM,C.PLUNAME,B.SHOP,B.LOADDOCTYPE,B.CHANNELID,G.GOODBARCODE FROM dcp_order_abnormalinfo_detail a "
                                     + " inner join dcp_order B on a.eid=b.eid and a.orderno=b.orderno "
                                     + " inner join dcp_order_detail c on a.eid=c.eid and a.orderno=c.orderno and a.oitem=c.item "
                                     + " left join dcp_abnormalgood_mapping  g on g.eid=a.eid and g.LOADDOCTYPE=b.LOADDOCTYPE and g.CHANNELID=b.CHANNELID  and g.goodname=c.pluname "
                                     + " where a.STATUS='0' and a.EID='"+eId+"' and a.ORDERNO='"+orderNo+"' order by A.OITEM "
                                     + " )";
                    this.Log("循环"+logStart+"【查询异常商品映射sql】="+sql_order);
                    List<Map<String, Object>> getMappingGoods=this.doQueryData(sql_order, null);
                    if (getMappingGoods==null||getMappingGoods.isEmpty())
                    {
                        this.Log("循环"+logStart+"【查询异常商品映射】没有数据");
                        continue;
                    }
                    String shop = getMappingGoods.get(0).getOrDefault("SHOP","").toString();
                    DCP_AbnormalOrderProcessReq req = new DCP_AbnormalOrderProcessReq();
                    DCP_AbnormalOrderProcessReq.levelRequest request = req.new levelRequest();
                    request.setGoodsList(new ArrayList<DCP_AbnormalOrderProcessReq.levelGoods>());
                    request.seteId(eId);
                    request.setOrderNo(orderNo);
                    request.setAbnormalType(abnormalType);
                    request.setAutoOrderToSale("Y");
                    boolean isAllMapping = true;//有没有部分映射，
                    for (Map<String, Object> map_goods : getMappingGoods)
                    {
                        String item = map_goods.getOrDefault("OITEM","").toString();
                        String pluName = map_goods.getOrDefault("PLUNAME","").toString();
                        String pluBarcode = map_goods.getOrDefault("GOODBARCODE","").toString();
                        if (pluBarcode==null||pluBarcode.trim().isEmpty())
                        {
                            isAllMapping = false;
                            this.Log("循环"+logStart+"商品项次item="+item+",商品名称pluName="+pluName+",没有找到映射的条码");
                            continue;
                        }

                        DCP_AbnormalOrderProcessReq.levelGoods goods = req.new levelGoods();
                        goods.setItem(item);
                        goods.setPluBarcode(pluBarcode);
                        request.getGoodsList().add(goods);

                    }

                    if (!request.getGoodsList().isEmpty())
                    {
                        if (!isAllMapping)
                        {
                            this.Log("循环"+logStart+"【结束】：该订单存在部分映射条码未找到！");
                            request.setAutoOrderToSale("N");
                        }
                        else
                        {
                            this.Log("循环"+logStart+"【结束】：该订单映射条码全部找到！");
                        }


                        try
                        {
                            //模拟内部调用需要token
                            TokenManagerRetail tmr=new TokenManagerRetail();
                            DCP_LoginRetailRes res_token = new DCP_LoginRetailRes();
                            DCP_LoginRetailRes.level1Elm oneLv1 =res_token.new level1Elm();
                            oneLv1.seteId(eId);
                            oneLv1.setShopId(shop);
                            oneLv1.setLangType("zh_CN");
                            oneLv1.setOpNo("admin");
                            oneLv1.setOpName("定时任务");
                            res_token.setDatas(new ArrayList<DCP_LoginRetailRes.level1Elm>());
                            res_token.getDatas().add(oneLv1);
                            String token = tmr.produce(res_token);

                            Map<String,Object> jsonMap=new HashMap<String,Object>();
                            jsonMap.put("serviceId", "DCP_AbnormalOrderProcess");
                            //这个token是无意义的
                            jsonMap.put("token", token);
                            jsonMap.put("request", request);
                            String json_ship = pj.beanToJson(jsonMap);
                            this.Log(logStart+"【调用订单异常处理DCP_AbnormalOrderProcess】请求："+ json_ship);

                            DispatchService ds = DispatchService.getInstance();
                            String resbody_ship = ds.callService(json_ship, StaticInfo.dao);
                            tmr.deleteTokenAndDB(token);
                            tmr = null;
                            this.Log(logStart+"【调用订单异常处理DCP_AbnormalOrderProcess】返回："+ resbody_ship);

                        }
                        catch (Exception e)
                        {
                            this.Log(logStart+"【调用订单异常处理DCP_AbnormalOrderProcess】异常："+ e.getMessage());
                        }




                    }
                    else
                    {
                        this.Log("循环"+logStart+"【结束】：该订单没有找到映射条码！");
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
            logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess异常，END:************\r\n");
            this.Log("异常订单批量处理AbnormalOrderBatchProcess定时任务，异常:"+e.getMessage());
        }
        finally
        {
            bRun=false;
            logger.info("\r\n*********异常订单批量处理AbnormalOrderBatchProcess定时调用END:************\r\n");
            this.Log("异常订单批量处理AbnormalOrderBatchProcess定时调用END:");
        }

        return sReturnInfo;
    }

    public void Log(String log)  {
        try
        {
            HelpTools.writelog_fileName(log, "AbnormalOrderBatchProcess");

        } catch (Exception e)
        {
            // TODO: handle exception
        }

    }
}
