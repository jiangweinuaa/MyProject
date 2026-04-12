package com.dsc.spos.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.util.CollectionUtils;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 外卖待支付订单定时自动取消
 * @author: wangzyc
 * @create: 2021-07-15
 */

/*****************外卖待支付订单定时自动取消**************************/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoOrderCancel extends InitJob {

    Logger logger = LogManager.getLogger(AutoOrderCancel.class.getName());

    static boolean bRun = false;//标记此服务是否正在执行中
    String orderLogFileName = "AutoOrderCancel";
    String jobDesc = "【外卖待支付订单定时自动取消任务 AutoOrderCancel】";

    public String doExe() throws IOException {
        //此服务是否正在执行中
        //返回信息
        /**
         *  针对订单状态status==0或1&来源类型loadDocType==WAIMAI&payStatus==1的订单
         *  根据CHANNELID匹配DCP_ECOMMERCE.EXPIRETIME
         *  若已达预设倒计时截止时间，则自动调用DCP_OrderRefund，将订单状态status变更为12
         */
        String sReturnInfo = "";
        try {

            if (bRun) {
                logger.debug("\r\n*********" + jobDesc + " 正在执行中,本次调用取消:************\r\n");

                sReturnInfo = jobDesc + " 正在执行中！";
                return sReturnInfo;
            }
            bRun = true;
            logger.info("\r\n********* " + jobDesc + " 定时调用Start:************\r\n");
            HelpTools.writelog_fileName(jobDesc + "定时调用Start", orderLogFileName);

            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dfss = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMddHHmmss");


            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append(" SELECT a.EID, a.ORDERNO, a.loadDocType, a.CHANNELID, a.tot_amt,a.CREATE_DATETIME,b.EXPIRETIME,a.SHOP,c.USERCODE " +
                    " FROM dcp_order a " +
                    " LEFT JOIN DCP_ECOMMERCE b ON a.eid = b.EID AND a.CHANNELID = b.CHANNELID " +
                    " LEFT JOIN CRM_APIUSER c ON a.eid = c.eid AND a.CHANNELID  = c.CHANNELID  " +
                    " WHERE a.LOADDOCTYPE = 'WAIMAI' AND a.PAYSTATUS = '1' AND a.STATUS IN ('0', '1')");
            List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sqlbuf.toString(), null);
//            HelpTools.writelog_fileName(jobDesc + "查询sql："+sqlbuf.toString(), orderLogFileName);

            if (!CollectionUtils.isEmpty(getQData)) {
                for (Map<String, Object> oneData : getQData) {
                    String orderNo = oneData.get("ORDERNO").toString();
                    String eId = oneData.get("EID").toString();
                    String loadDocType = oneData.get("LOADDOCTYPE").toString();
                    String channelId = oneData.get("CHANNELID").toString();
                    String tot_amt = oneData.get("TOT_AMT").toString();
                    String createDateTime = oneData.get("CREATE_DATETIME").toString();
                    String expireTime = oneData.get("EXPIRETIME").toString();
                    String shopId = oneData.get("SHOP").toString();
                    String userCode = oneData.get("USERCODE").toString();
                    if (expireTime==null||expireTime.isEmpty())
                    {
                        expireTime = "15";//默认15分钟
                    }

                    int expireTime_i = 0;
                    try {
                        expireTime_i = Integer.parseInt(expireTime);
                    }
                    catch (Exception e)
                    {

                    }
                    //移动支付轮询时间是7分钟，所以设置一定要大于7分钟，如果小于7分钟，就默认10分钟吧
                    if (expireTime_i<=7)
                    {
                        expireTime_i = 10;
                    }
                    //防止，比轮询时间快，多加一分钟吧
                    expireTime_i = expireTime_i+1;


                    // 此处做个兼容 下单时间 有的精确到毫秒 有的精确到秒
                    int length = createDateTime.length();
                    Date createTimeDate = null;
                    if (length > 14) {
                        createTimeDate = dfss.parse(createDateTime); // 下单时间
                    } else {
                        createTimeDate = dfs.parse(createDateTime); // 下单时间
                    }

                    // 下单时间过期时间
                    String expireTimeDate = "";// 该订单失效时间
                    Date createDateExpireTime = null;
                    cal.setTime(createTimeDate);
                    cal.add(cal.MINUTE, expireTime_i); // DCP_ECOMMERCE/EXPIRETIME :未支付失效时长， 单位分钟
                    expireTimeDate = df.format(cal.getTime());

                    if (!Check.Null(expireTimeDate)) {
                        createDateExpireTime = df.parse(expireTimeDate);
                    }

                    // 获取当前时间 比较 预计订单失效时间
                    Date time = new Date();
                    String currentTime = df.format(time);
                    Date currentDateTime = null;
                    currentDateTime = df.parse(currentTime);

                    if (currentDateTime.compareTo(createDateExpireTime) == 1) {
                        HelpTools.writelog_fileName("*********** " + jobDesc + "OrderNo:" + orderNo + "待支付超时 下单时间为:" + createDateTime + " ,超时时间为:" + expireTimeDate + "*************", orderLogFileName);
                        HelpTools.writelog_fileName("******** " + jobDesc + "OrderNo:" + orderNo + "开始进行退单 Start ************", orderLogFileName);

                        String dcpUrl = PosPub.getDCP_INNER_URL(eId);

                        if (Check.Null(dcpUrl)) {
                            HelpTools.writelog_fileName("******** " + currentTime + jobDesc + "JOB处理获取服务地址为空(PlatformCentreURL) ", orderLogFileName);

                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, jobDesc + "服务地址参数 PlatformCentreURL为空，无法进行取消订单 ");
                        }

                        // *************************** DCP_OrderRefund_Open Begin**********************************
                        //营业日期
                        String accountDate = PosPub.getAccountDate_SMS(StaticInfo.dao, eId, shopId);
                        String refundDatetime = dfss.format(time);

                        JSONObject orderRefundReq = new JSONObject();
                        orderRefundReq.put("serviceId", "DCP_OrderRefund_Open");

                        JSONObject requestReq = new JSONObject();
                        requestReq.put("eId", eId);
                        requestReq.put("shopId", shopId);
                        requestReq.put("opNo", "");
                        requestReq.put("opName", "");
                        requestReq.put("refundType", "0");
                        requestReq.put("orderNo", orderNo);
                        requestReq.put("loadDocType", loadDocType);
                        requestReq.put("channelId", channelId);
                        requestReq.put("pickGoodsRefundType", "0");
                        requestReq.put("refundReasonNo", "");
                        requestReq.put("refundReasonName", "");
                        requestReq.put("refundReason", "");
                        requestReq.put("refundBdate", accountDate);
                        requestReq.put("refundDatetime", refundDatetime);
                        requestReq.put("tot_amt", tot_amt);

                        orderRefundReq.put("request", requestReq);

                        String orderRefundReqStr = requestReq.toString();
                        String orderRefundSign = PosPub.encodeMD5(orderRefundReqStr + userCode);

                        JSONObject orderRefundSignJson = new JSONObject();
                        orderRefundSignJson.put("sign", orderRefundSign);
                        orderRefundSignJson.put("key", userCode);

                        orderRefundReq.put("sign", orderRefundSignJson);
                        String s = orderRefundReq.toString();

                        //********** 已经准备好 DCP_OrderRefund_Open 的json，开始调用 *************
                        String orderRefundResStr = HttpSend.Sendcom(orderRefundReq.toString(), dcpUrl).trim();
                        HelpTools.writelog_fileName("*********** " + jobDesc + " 调用DCP_OrderRefund_Open接口信息：地址（" + dcpUrl + "）  请求Json：" + orderRefundReq, orderLogFileName);
                        HelpTools.writelog_fileName("*********** " + jobDesc + " 调用DCP_OrderRefund_Open接口返回信息：" + orderRefundResStr, orderLogFileName);

                        JSONObject orderRefundResJson = new JSONObject();
                        orderRefundResJson = JSON.parseObject(orderRefundResStr);//String转json

                        String orderRefundSuccess = orderRefundResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                        String orderRefundStatus = orderRefundResJson.getString("serviceStatus").toUpperCase();
                        String orderRefundServiceDescription = orderRefundResJson.getString("serviceDescription").toUpperCase();

                        if (orderRefundSuccess.toUpperCase().equals("TRUE")) {
                            // 退单成功
                            HelpTools.writelog_fileName(
                                    "*********** " + jobDesc + "  单号:" + orderNo + " 取消订单成功 " + "*************",
                                    orderLogFileName);
                        }
                        // *************************** DCP_OrderRefund_Open End**********************************
                    }
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            logger.error("\r\n******外卖待支付订单定时自动取消 AutoOrderCancel 报错信息:" + e.getMessage() + "\r\n******\r\n");
            HelpTools.writelog_fileName("【外卖待支付订单定时自动取消任务 AutoOrderCancel】同步正在执行中,异常:" + e.getMessage(), orderLogFileName);
            sReturnInfo = "错误信息:" + e.getMessage();
        } finally {
            bRun = false;//
            logger.info("\r\n*********外卖待支付订单定时自动取消AutoOrderCancel定时调用End:************\r\n");
            HelpTools.writelog_fileName("【外卖待支付订单定时自动取消任务 AutoOrderCancel】定时调用End", orderLogFileName);
        }
        return sReturnInfo;
    }
}
