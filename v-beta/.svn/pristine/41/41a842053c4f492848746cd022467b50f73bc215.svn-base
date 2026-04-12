package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 零售生产订单定时清空任务
 * @author: wangzyc
 * @create: 2021-11-08
 */

/*****************零售生产订单定时清空任务**************************/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class KdsRetentionTime extends InitJob
{

    Logger logger = LogManager.getLogger(KdsRetentionTime.class.getName());

    static boolean bRun = false;//标记此服务是否正在执行中
    String orderLogFileName = "KdsRetentionTime";
    String jobDesc = "【零售生产订单定时清空任务 KdsRetentionTime】";

    public String doExe() throws IOException
    {
        //此服务是否正在执行中
        /**
         * 根据参数KdsRetentionTime，查询单据状态为3(已取餐)且时间超过则清除，
         */
        String sReturnInfo = "";
        try
        {
            if (bRun)
            {
                logger.debug("\r\n*********" + jobDesc + " 正在执行中,本次调用取消:************\r\n");

                sReturnInfo = jobDesc + " 正在执行中！";
                return sReturnInfo;
            }
            bRun = true;
            logger.info("\r\n********* " + jobDesc + " 定时调用Start:************\r\n");
            HelpTools.writelog_fileName(jobDesc + "定时调用Start", orderLogFileName);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dfss = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMddHHmmss");

            String sql = "SELECT eid ,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE ITEM = 'KdsRetentionTime' AND STATUS ='100'";
            List<Map<String, Object>> getKdsRetentionTime = StaticInfo.dao.executeQuerySQL(sql, null);
            if (!CollectionUtils.isEmpty(getKdsRetentionTime))
            {
                String kdsRetentionTime = "2";
                for (Map<String, Object> map : getKdsRetentionTime)
                {
                    String eid2 = map.get("EID").toString();
                    kdsRetentionTime = map.get("ITEMVALUE").toString();

                    //直接取出满足条件的SQL
                    Calendar cal = Calendar.getInstance();//获得当前时间
                    cal.add(Calendar.DATE, 0-Integer.parseInt(kdsRetentionTime));
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    String dateFormat = format.format(cal.getTime());

                    // 每次100笔
                    sql = "select * from (SELECT rownum rn,EID,SHOPID,BILLNO FROM DCP_PRODUCT_SALE where ( CASE  WHEN nvl(isbook, 'N')='Y' THEN SHIPENDTIME ELSE ORDERTIME END ) <='"+dateFormat+"' and eid='"+eid2+"') where rn>0 and rn<=100 ";
                    List<Map<String, Object>> getBillDetails = StaticInfo.dao.executeQuerySQL(sql, null);
                    if (!CollectionUtils.isEmpty(getBillDetails))
                    {
                        // 外卖订单 配送自提时间计算是否超时
                        // 堂食 打包 订单 下单时间 计算是否超时
                        for (Map<String, Object> billDetail : getBillDetails)
                        {
                            String eid = billDetail.get("EID").toString();
                            String shopid = billDetail.get("SHOPID").toString();
                            String billno = billDetail.get("BILLNO").toString();


                            List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();

                            // 零售生产任务统计 DCP_PRODUCT_SALE
                            DelBean db1 = new DelBean("DCP_PRODUCT_SALE");
                            db1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                            db1.addCondition("SHOPID", new DataValue(shopid, Types.VARCHAR));
                            db1.addCondition("BILLNO", new DataValue(billno, Types.VARCHAR));
                            lstData.add(new DataProcessBean(db1));

                            // 零售生产任务统计 DCP_PRODUCT_SALE
                            DelBean db2 = new DelBean("DCP_PRODUCT_DETAIL");
                            db2.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                            db2.addCondition("SHOPID", new DataValue(shopid, Types.VARCHAR));
                            db2.addCondition("BILLNO", new DataValue(billno, Types.VARCHAR));
                            lstData.add(new DataProcessBean(db2));
                            // 执行
                            StaticInfo.dao.useTransactionProcessData(lstData);
                            HelpTools.writelog_fileName(jobDesc + "企业:" + eid + "门店：" + shopid + "的单号" + billno + "的订单已过期，已清除！", orderLogFileName);

                        }
                    }

                }

            }

        }
        catch (Exception e)
        {
            // TODO: handle exception
            logger.error("\r\n******零售生产订单定时清空任务 KdsRetentionTime 报错信息:" + e.getMessage() + "\r\n******\r\n");
            HelpTools.writelog_fileName("【零售生产订单定时清空任务 KdsRetentionTime】同步正在执行中,异常:" + e.getMessage(), orderLogFileName);
            sReturnInfo = "错误信息:" + e.getMessage();
        }
        finally
        {
            bRun = false;//
            logger.info("\r\n*********零售生产订单定时清空任务 KdsRetentionTime定时调用End:************\r\n");
            HelpTools.writelog_fileName("【零售生产订单定时清空任务 KdsRetentionTime】定时调用End", orderLogFileName);
        }
        return sReturnInfo;
    }
}
