package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.waimai.HelpTools;
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
public class ExceptionOrderBatchProcess extends InitJob {
    Logger logger = LogManager.getLogger(ExceptionOrderBatchProcess.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public ExceptionOrderBatchProcess()
    {

    }
    public String doExe()
    {
        //此服务是否正在执行中
        //返回信息
        String sReturnInfo="";

        if (bRun )
        {
            logger.info("\r\n*********异常订单批量处理ExceptionOrderBatchProcess正在执行中,本次调用取消:************\r\n");
            this.Log("*********异常订单批量处理ExceptionOrderBatchProcess正在执行中,本次调用取消:************");
            sReturnInfo="异常订单批量处理ExceptionOrderBatchProcess正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r\n*********异常订单批量处理ExceptionOrderBatchProcess定时调用Start:************\r\n");
        this.Log("*********异常订单批量处理ExceptionOrderBatchProcess定时调用Start:************");
        try
        {
            //当前日期
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,-1);//当前时间减去一个月，即一个月前的时间
            String sDate_pre = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            String orderToSaleStartTime = "";
            String orderToSaleEndTime = "";


            String abnormalType = "goodsNotFound";



            String sql = " select A.EID,A.ORDERNO,A.LOADDOCTYPE,A.LOADDOCORDERNO from dcp_order A"
                       + " inner join dcp_order B ON A.EID=B.EID AND A.LOADDOCTYPE=B.LOADDOCTYPE AND A.LOADDOCORDERNO=B.ORDERNO "
                       + " where A.SHOP<>' ' AND A.SHOP IS NOT NULL and A.billtype='-1' AND A.Exceptionstatus='Y' AND A.process_Status='N'  "
                       + " AND B.EXCEPTIONSTATUS='N' AND B.process_Status='Y' AND B.billtype='1' "
                       + " AND A.LOADDOCTYPE IN ('ELEME','MEITUAN','JDDJ','MTSG') ";
            this.Log("异常订单批量处理ExceptionOrderBatchProcess定时任务，查询sql="+sql);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (getQData==null||getQData.isEmpty())
            {
                logger.info("\r\n*********异常订单批量处理ExceptionOrderBatchProcess查询无数据，END:************\r\n");
                this.Log("异常订单批量处理ExceptionOrderBatchProcess定时任务，查询无数据！");
                sReturnInfo = "";
                return sReturnInfo;
            }
            ParseJson pj=new ParseJson();
            //下面开始轮询执行修复
            for (Map<String, Object> map_order : getQData)
            {
                String eId = map_order.getOrDefault("EID","").toString();
                String orderNo = map_order.getOrDefault("ORDERNO","").toString();
                String orderNo_origin = map_order.getOrDefault("LOADDOCORDERNO","").toString();
                String logStart = "异常处理，eId="+eId+",退订单号orderNo="+orderNo+",原单单号orderNo="+orderNo_origin;
                this.Log("循环"+logStart+"【开始】");
                try
                {
                    String sql_order = " select * from dcp_order_detail where eid='"+eId+"' and orderNo='"+orderNo+"' ";

                    this.Log("循环"+logStart+"【查询退订单异常商品明细sql】="+sql_order);
                    List<Map<String, Object>> getRefundOrderGoods=this.doQueryData(sql_order, null);
                    if (getRefundOrderGoods==null||getRefundOrderGoods.isEmpty())
                    {
                        this.Log("循环"+logStart+"【查询退订单异常商品明细】没有数据");
                        continue;
                    }
                    sql_order = "";
                    sql_order = " select * from dcp_order_detail where eid='"+eId+"' and orderNo='"+orderNo_origin+"' ";
                    this.Log("循环"+logStart+"【查询原单商品sql】="+sql_order);
                    List<Map<String, Object>> getOriginOrderGoods=this.doQueryData(sql_order, null);
                    if (getOriginOrderGoods==null||getOriginOrderGoods.isEmpty())
                    {
                        this.Log("循环"+logStart+"【查询原单商品明细】没有数据");
                        continue;
                    }

                    List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                    for (Map<String, Object> refund_goods : getRefundOrderGoods)
                    {
                        String pluName = refund_goods.getOrDefault("PLUNAME","").toString();
                        String item = refund_goods.getOrDefault("ITEM","").toString();
                        if (pluName.isEmpty()||item.isEmpty())
                        {
                            continue;
                        }
                        for (Map<String, Object> origin_goods : getOriginOrderGoods)
                        {
                            String packageType = origin_goods.getOrDefault("PACKAGETYPE","").toString();
                            if ("3".equals(packageType))
                            {
                                //（1、正常商品 2、套餐主商品  3、套餐子商品）
                                continue;
                            }
                            String pluName_origin = origin_goods.getOrDefault("PLUNAME","").toString();
                            String pluNo = origin_goods.getOrDefault("PLUNO","").toString();
                            String pluBarcode = origin_goods.getOrDefault("PLUBARCODE","").toString();
                            String featureNo = origin_goods.getOrDefault("FEATURENO","").toString();
                            String unit = origin_goods.getOrDefault("SUNIT","").toString();
                            String unitName = origin_goods.getOrDefault("SUNITNAME","").toString();
                            if (pluName.equals(pluName_origin))
                            {
                                UptBean up2=new UptBean("dcp_order_detail");
                                up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                up2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                up2.addCondition("ITEM", new DataValue(item, Types.VARCHAR));

                                up2.addUpdateValue("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                                up2.addUpdateValue("PLUBARCODE", new DataValue(pluBarcode, Types.VARCHAR));
                                up2.addUpdateValue("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                                up2.addUpdateValue("SUNIT", new DataValue(unit, Types.VARCHAR));
                                up2.addUpdateValue("SUNITNAME", new DataValue(unitName, Types.VARCHAR));
                                lstData.add(new DataProcessBean(up2));
                                this.Log("循环"+logStart+",【匹配成功】商品名称:"+pluName);
                                break;
                            }
                        }


                    }

                    if (lstData==null||lstData.isEmpty())
                    {
                        continue;
                    }
                    UptBean up1=new UptBean("dcp_order");
                    up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    up1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));

                    up1.addUpdateValue("EXCEPTIONSTATUS", new DataValue("N", Types.VARCHAR));
                    up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    lstData.add(new DataProcessBean(up1));
                    StaticInfo.dao.useTransactionProcessData(lstData);
                    this.Log("循环"+logStart+",【处理成功】");

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
            logger.info("\r\n*********异常订单批量处理ExceptionOrderBatchProcess异常，END:************\r\n");
            this.Log("异常订单批量处理ExceptionOrderBatchProcess定时任务，异常:"+e.getMessage());
        }
        finally
        {
            bRun=false;
            logger.info("\r\n*********异常订单批量处理ExceptionOrderBatchProcess定时调用END:************\r\n");
            this.Log("异常订单批量处理ExceptionOrderBatchProcess定时调用END:");
        }

        return sReturnInfo;
    }

    public void Log(String log)  {
        try
        {
            HelpTools.writelog_fileName(log, "ExceptionOrderBatchProcess");

        } catch (Exception e)
        {
            // TODO: handle exception
        }

    }
}
