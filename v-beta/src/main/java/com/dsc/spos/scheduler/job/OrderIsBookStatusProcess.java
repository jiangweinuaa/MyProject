package com.dsc.spos.scheduler.job;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderShippingReq;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.TokenManagerRetail;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.dsc.spos.waimai.sftc.shop;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderQueryRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OrderIsBookStatusProcess extends InitJob  
{
	Logger logger = LogManager.getLogger(OrderIsBookStatusProcess.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中
	String jddjLogFileName = "OrderIsBookStatusJoblog";
	public OrderIsBookStatusProcess()
	{

	}

	public String doExe() throws Exception
	{
		// 返回信息
		String sReturnInfo = "";
		logger.info("\r\n***************OrderIsBookStatusProcess同步START****************\r\n");
		HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理外卖订单自动订转销】同步START！",jddjLogFileName);
		try 
		{
			//此服务是否正在执行中
			if (bRun)
			{		
				logger.info("\r\n*********OrderIsBookStatusProcess同步正在执行中,本次调用取消:************\r\n");
				HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理外卖订单自动订转销】同步正在执行中,本次调用取消！",jddjLogFileName);
				return sReturnInfo;
			}

			bRun=true;//
			String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());;
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            //先查 job 执行时间，然后再执行后续操作
            String getTimeSql = "select * from job_quartz_detail where job_name = 'OrderIsBookStatusProcess'  and STATUS = '100' ";
            List<Map<String, Object>> getTimeDatas=this.doQueryData(getTimeSql, null);
            if(getTimeDatas!= null && !getTimeDatas.isEmpty()) {
                boolean isTime = false;
                String beginTime = "";
                String endTime = "";
                for (Map<String, Object> map : getTimeDatas) {
                    beginTime = map.get("BEGIN_TIME").toString();
                    endTime = map.get("END_TIME").toString();
                    if(sTime.compareTo(beginTime)>=0 && sTime.compareTo(endTime)<0) {
                        isTime = true;
                        break;
                    }
                }
                if(!isTime) {
                    logger.info("\r\n*********OrderIsBookStatusProcess同步END,当前时间["+sTime+"];不在job设置的执行时间范围["+beginTime+"]-["+endTime+"]************\r\n");
                    HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理外卖订单自动订转销】同步END,当前时间["+sTime+"];不在job设置的执行时间范围["+beginTime+"]-["+endTime+"]",jddjLogFileName);
                    return sReturnInfo;
                }
            }
			try 
			{
                //【霸王3.0】外卖订单自动订转销
                waimaiOrderToSaleByComplete(sdate);
                HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理外卖订单自动订转销】同步END",jddjLogFileName);
                //预订单推送已完成缓存，不需要了。后端自动订转销。
                //waimaiOrderIsBookProcess(sdate);
                //历史订单也转下
                waimaiOrderToSaleByComplete_history(sdate);
			}
			catch (Exception e)
			{
				logger.error("\r\n******OrderIsBookStatusProcess同步报错信息" + e.getMessage() + "******\r\n");
				HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理预订单状态】处理预订单状态异常："+e.getMessage(),jddjLogFileName);
				sReturnInfo="错误信息:" + e.getMessage();

			}

		} 
		catch (Exception e) 
		{
			// TODO: handle exception

		}
		finally 
		{
			bRun=false;//
		}


		logger.info("\r\n***************OrderIsBookStatusProcess同步END****************\r\n");

		return sReturnInfo;
	}

	protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
		if (pData == null || pData.size() == 0) {
			return;
		}
		StaticInfo.dao.useTransactionProcessData(pData);

	}

	private String getQuerySql() throws Exception
	{
		String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());;
		String stime = new SimpleDateFormat("HHmmss").format(new Date());

		String sqlCon = "'"+orderLoadDocType.ELEME+"','"+orderLoadDocType.MEITUAN+"','"+orderLoadDocType.JDDJ+"'";
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from ( select A.* from dcp_order  A");			
		sqlbuf.append(" where  A.ISBOOK='Y' AND A.Status='2' and A.refundstatus='1' AND A.LOADDOCTYPE IN ("+sqlCon+")");
        sqlbuf.append(" and A.shop<>' ' and A.BILLTYPE='1' ");
		sqlbuf.append(" and A.Shipdate='"+sdate+"' and A.Shipendtime<='"+stime+"'");
		sqlbuf.append(" order by A.Shipendtime");
		sqlbuf.append(")");

		return sqlbuf.toString();

	}

	private void SaveOrderRedis(String redis_key,String hash_key,String hash_value) throws Exception
	{
		//region 先写缓存
		try 
		{	
			HelpTools.writelog_fileName("【OrderIsBookStatusProcess开始写缓存】"+"redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+hash_value,jddjLogFileName);	  	
			RedisPosPub redis = new RedisPosPub();
			boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
			if (isexistHashkey) {

				redis.DeleteHkey(redis_key, hash_key);//
				HelpTools.writelog_fileName("【OrderIsBookStatusProcess删除存在hash_key的缓存】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key,jddjLogFileName);
			}
			boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
			if (nret) {
				HelpTools.writelog_fileName("【OrderIsBookStatusProcess写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key,jddjLogFileName);
			} else {
				HelpTools.writelog_fileName("【OrderIsBookStatusProcess写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key,jddjLogFileName);
			}
			
		} 
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【OrderIsBookStatusProcess写缓存】Exception:"+e.getMessage()+" redis_key:"+redis_key+" hash_key:"+hash_key,jddjLogFileName);	
		}
		//endregion

	}

	private void waimaiOrderToSaleByComplete(String shipDate) throws Exception
    {
        try
        {
			String sTime = new SimpleDateFormat("HHmmss").format(new Date());
			//晚上20点-24点之间才执行。
			String edateTimeStart = "120000";
			String edateTimeEnd = "125959";
            logger.info("\r\n******OrderIsBookStatusProcess【外卖订单自动订转销】开始执行******\r\n");
			/*if (sTime.compareTo(edateTimeStart) >= 0 && sTime.compareTo(edateTimeEnd) <= 0)
			{
				HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】开始执行",jddjLogFileName);
			}
			else
			{
				HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】当前时间["+sTime+"];不在job执行时间范围["+edateTimeStart+"]-["+edateTimeEnd+"]",jddjLogFileName);
                logger.info("\r\n******OrderIsBookStatusProcess【外卖订单自动订转销】当前时间["+sTime+"];不在job执行时间范围["+edateTimeStart+"]-["+edateTimeEnd+"]******\r\n");
				return;
			}*/
            HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】开始执行",jddjLogFileName);
            if (shipDate==null||shipDate.isEmpty())
            {
                shipDate = new SimpleDateFormat("yyyyMMdd").format(new Date());;
            }
            String sqlCon = "'"+orderLoadDocType.ELEME+"','"+orderLoadDocType.MEITUAN+"','"+orderLoadDocType.JDDJ+"','"+orderLoadDocType.MTSG+"','"+orderLoadDocType.WAIMAI+"','"+orderLoadDocType.DYWM+"'";
            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from ( select A.EID,A.ORDERNO,A.SHOP,A.LOADDOCTYPE,A.CHANNELID,row_number()over (order by A.Shipendtime) rn from dcp_order  A");
            sqlbuf.append(" where  (A.Status='11' or A.Status='2') and A.BILLTYPE='1' and A.refundstatus<>'6' AND A.LOADDOCTYPE IN ("+sqlCon+")");
            if (sTime.compareTo("230000") >= 0)
            {
                sTime = "235959";//防止漏单吧。
            }
            sqlbuf.append(" and A.paystatus='3'");//鼎捷外卖，加个控制吧
            sqlbuf.append(" and A.Shipdate='"+shipDate+"'");
            sqlbuf.append(" and A.Shipendtime<='"+sTime+"'");
            sqlbuf.append(" and A.shop<>' '");//没有绑定的不能
            sqlbuf.append(" and A.EXCEPTIONSTATUS='N'");//异常标识N，才能订转销
            sqlbuf.append(" and A.ORDERTOSALE_DATETIME is null ");//未订转销的
            sqlbuf.append(" and A.TOT_OLDAMT>0 ");
            sqlbuf.append(") where rn<=1000 ");
            String sql = sqlbuf.toString();
            HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】查询sql="+sql,jddjLogFileName);
            PosPub.iTimeoutTime = 120;
            List<Map<String, Object>> getQDatas = this.doQueryData(sql, null);
            PosPub.iTimeoutTime = 30;
            if (getQDatas!=null&&!getQDatas.isEmpty())
            {
                String sql_item = "";
                TokenManagerRetail tmr=new TokenManagerRetail();
                DispatchService ds = DispatchService.getInstance();
				ParseJson pj=new ParseJson();
                for (Map<String, Object> map : getQDatas)
                {
                    try
                    {
                        //防止中途更新，在查询一次
                        sql_item = "";
                        String eId = map.get("EID").toString();
                        String orderNo = map.get("ORDERNO").toString();
                        String shop = map.get("SHOP").toString();
                        sql_item = "select * from dcp_order where (status='11' or status='2') and refundstatus<>'6' and eid='"+eId+"' and orderno='"+orderNo+"'";
                        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql_item, null);
                        if (getQDataDetail==null||getQDataDetail.isEmpty())
                        {
                            continue;
                        }

                        DCP_LoginRetailRes res_token = new DCP_LoginRetailRes();
                        DCP_LoginRetailRes.level1Elm oneLv1 =res_token.new level1Elm();
                        oneLv1.seteId(eId);
                        oneLv1.setShopId(shop);
                        oneLv1.setLangType("zh_CN");
                        res_token.setDatas(new ArrayList<DCP_LoginRetailRes.level1Elm>());
                        res_token.getDatas().add(oneLv1);
                        String token = tmr.produce(res_token);

                        DCP_OrderShippingReq req_sale = new  DCP_OrderShippingReq();
                        DCP_OrderShippingReq.levelRequest req_sale_requeset = req_sale.new levelRequest();
                        req_sale_requeset.setOpType("1");

                        req_sale_requeset.setOrderList(new String[] {orderNo});
                        Map<String,Object> jsonMap=new HashMap<String,Object>();
                        jsonMap.put("serviceId", "DCP_OrderShipping");
                        //这个token是无意义的
                        jsonMap.put("token", token);
                        jsonMap.put("plantType", "nrc");
                        jsonMap.put("request", req_sale_requeset);

                        String json_ship = pj.beanToJson(jsonMap);
                        HelpTools.writelog_fileName("【外卖订单自动订转销】订单orderNo="+orderNo+",请求： "+ json_ship,jddjLogFileName);
                        String resbody_ship = ds.callService(json_ship, StaticInfo.dao);
                        tmr.deleteTokenAndDB(token);//删掉用过的

                        HelpTools.writelog_fileName("【外卖订单自动订转销】订单orderNo="+orderNo+",返回： "+ resbody_ship,jddjLogFileName);
                        JSONObject res_json = new JSONObject(resbody_ship);
                        String success = res_json.optString("success","");
                        String serviceDescription = res_json.optString("serviceDescription","");
                        if (!"true".equalsIgnoreCase(success))
						{
						    saveErrorOrderStatusLog(eId,orderNo,map.getOrDefault("LOADDOCTYPE","").toString(),map.getOrDefault("CHANNELID","").toString(),serviceDescription);
						}


                    }
                    catch (Exception e)
                    {

                    }

                }
            }
            else
            {
                HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】查询返回无数据",jddjLogFileName);
                logger.info("\r\n******OrderIsBookStatusProcess【外卖订单自动订转销】没有需要处理的订单******\r\n");
            }
        }
        catch (Exception e)
        {
            PosPub.iTimeoutTime = 30;
            HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】异常:"+e.getMessage(),jddjLogFileName);
            logger.info("\r\n******OrderIsBookStatusProcess【外卖订单自动订转销】异常:"+e.getMessage()+"******\r\n");
        }
    }

    /**
     * 历史订单转销售
     * @param shipDate
     * @throws Exception
     */
    private void waimaiOrderToSaleByComplete_history(String shipDate) throws Exception
    {
        try
        {
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            logger.info("\r\n******OrderIsBookStatusProcess【外卖订单自动订转销】【历史订单】开始执行******\r\n");
			/*if (sTime.compareTo(edateTimeStart) >= 0 && sTime.compareTo(edateTimeEnd) <= 0)
			{
				HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】开始执行",jddjLogFileName);
			}
			else
			{
				HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】当前时间["+sTime+"];不在job执行时间范围["+edateTimeStart+"]-["+edateTimeEnd+"]",jddjLogFileName);
                logger.info("\r\n******OrderIsBookStatusProcess【外卖订单自动订转销】当前时间["+sTime+"];不在job执行时间范围["+edateTimeStart+"]-["+edateTimeEnd+"]******\r\n");
				return;
			}*/
            HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】【历史订单】开始执行",jddjLogFileName);
            if (shipDate==null||shipDate.isEmpty())
            {
                shipDate = new SimpleDateFormat("yyyyMMdd").format(new Date());;
            }
            String endDate = shipDate;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,-7);
            String beginDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            String sqlCon = "'"+orderLoadDocType.ELEME+"','"+orderLoadDocType.MEITUAN+"','"+orderLoadDocType.JDDJ+"','"+orderLoadDocType.MTSG+"','"+orderLoadDocType.WAIMAI+"','"+orderLoadDocType.DYWM+"'";
            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from ( select A.EID,A.ORDERNO,A.SHOP,A.LOADDOCTYPE,A.CHANNELID,row_number()over (order by A.Shipendtime) rn from dcp_order  A");
            sqlbuf.append(" where  (A.Status='11' or A.Status='2') and A.BILLTYPE='1' and A.refundstatus<>'6' AND A.LOADDOCTYPE IN ("+sqlCon+")");

            sqlbuf.append(" and A.paystatus='3'");//鼎捷外卖，加个控制吧
            sqlbuf.append(" and A.Shipdate<'"+endDate+"'");
            sqlbuf.append(" and A.Shipdate>='"+beginDate+"'");
            sqlbuf.append(" and A.shop<>' '");//没有绑定的不能
            sqlbuf.append(" and A.EXCEPTIONSTATUS='N'");//异常标识N，才能订转销
            sqlbuf.append(" and A.ORDERTOSALE_DATETIME is null ");//未订转销的
            sqlbuf.append(" and A.TOT_OLDAMT>0 ");
            sqlbuf.append(") where rn<=100 ");
            String sql = sqlbuf.toString();
            HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】【历史订单】查询sql="+sql,jddjLogFileName);
            PosPub.iTimeoutTime = 120;
            List<Map<String, Object>> getQDatas = this.doQueryData(sql, null);
            PosPub.iTimeoutTime = 30;
            if (getQDatas!=null&&!getQDatas.isEmpty())
            {
                String sql_item = "";
                TokenManagerRetail tmr=new TokenManagerRetail();
                DispatchService ds = DispatchService.getInstance();
                ParseJson pj=new ParseJson();
                for (Map<String, Object> map : getQDatas)
                {
                    try
                    {
                        //防止中途更新，在查询一次
                        sql_item = "";
                        String eId = map.get("EID").toString();
                        String orderNo = map.get("ORDERNO").toString();
                        String shop = map.get("SHOP").toString();
                        sql_item = "select * from dcp_order where (status='11' or status='2') and refundstatus<>'6' and eid='"+eId+"' and orderno='"+orderNo+"'";
                        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql_item, null);
                        if (getQDataDetail==null||getQDataDetail.isEmpty())
                        {
                            continue;
                        }

                        DCP_LoginRetailRes res_token = new DCP_LoginRetailRes();
                        DCP_LoginRetailRes.level1Elm oneLv1 =res_token.new level1Elm();
                        oneLv1.seteId(eId);
                        oneLv1.setShopId(shop);
                        oneLv1.setLangType("zh_CN");
                        res_token.setDatas(new ArrayList<DCP_LoginRetailRes.level1Elm>());
                        res_token.getDatas().add(oneLv1);
                        String token = tmr.produce(res_token);

                        DCP_OrderShippingReq req_sale = new  DCP_OrderShippingReq();
                        DCP_OrderShippingReq.levelRequest req_sale_requeset = req_sale.new levelRequest();
                        req_sale_requeset.setOpType("1");

                        req_sale_requeset.setOrderList(new String[] {orderNo});
                        Map<String,Object> jsonMap=new HashMap<String,Object>();
                        jsonMap.put("serviceId", "DCP_OrderShipping");
                        //这个token是无意义的
                        jsonMap.put("token", token);
                        jsonMap.put("plantType", "nrc");
                        jsonMap.put("request", req_sale_requeset);

                        String json_ship = pj.beanToJson(jsonMap);
                        HelpTools.writelog_fileName("【外卖订单自动订转销】【历史订单】订单orderNo="+orderNo+",请求： "+ json_ship,jddjLogFileName);
                        String resbody_ship = ds.callService(json_ship, StaticInfo.dao);
                        tmr.deleteTokenAndDB(token);//删掉用过的

                        HelpTools.writelog_fileName("【外卖订单自动订转销】【历史订单】订单orderNo="+orderNo+",返回： "+ resbody_ship,jddjLogFileName);
                        JSONObject res_json = new JSONObject(resbody_ship);
                        String success = res_json.optString("success","");
                        String serviceDescription = res_json.optString("serviceDescription","");
                        if (!"true".equalsIgnoreCase(success))
                        {
                            saveErrorOrderStatusLog(eId,orderNo,map.getOrDefault("LOADDOCTYPE","").toString(),map.getOrDefault("CHANNELID","").toString(),serviceDescription);
                        }


                    }
                    catch (Exception e)
                    {

                    }

                }
            }
            else
            {
                HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】【历史订单】查询返回无数据",jddjLogFileName);
                logger.info("\r\n******OrderIsBookStatusProcess【外卖订单自动订转销】【历史订单】没有需要处理的订单******\r\n");
            }
        }
        catch (Exception e)
        {
            PosPub.iTimeoutTime = 30;
            HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【外卖订单自动订转销】【历史订单】异常:"+e.getMessage(),jddjLogFileName);
            logger.info("\r\n******OrderIsBookStatusProcess【外卖订单自动订转销】【历史订单】异常:"+e.getMessage()+"******\r\n");
        }
    }

    private void waimaiOrderIsBookProcess(String shipDate) throws Exception
    {
        try
        {
            String sql = this.getQuerySql();
            HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理预订单状态】查询sql="+sql,jddjLogFileName);
            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
            {
                ParseJson pj = new ParseJson();
                for (Map<String, Object> map : getQDataDetail)
                {
                    String eId = map.get("EID").toString();
                    String orderNo = map.get("ORDERNO").toString();
                    String loadDocType = map.get("LOADDOCTYPE").toString();
                    String shopNo = map.get("SHOP").toString();
                    HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【查询预订单详细】开始，单号orderNo="+orderNo,jddjLogFileName);
                    try
                    {
                        order dcpOrder = HelpTools.GetOrderInfoByOrderNO(StaticInfo.dao, eId, loadDocType, orderNo);
                        HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【查询预订单详细】完成，单号orderNo="+orderNo,jddjLogFileName);
                        if(dcpOrder==null)
                        {
                            HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【查询预订单详细】内容为空，单号orderNo="+orderNo,jddjLogFileName);
                            continue;
                        }
                        String status_db = dcpOrder.getStatus();
                        HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理】【查询预订单详细】数据库中订单状态status="+status_db+"，单号orderNo="+orderNo,jddjLogFileName);
                        if(status_db!=null)
                        {
                            if(status_db.equals("3")||status_db.equals("12"))
                            {
                                continue;
                            }
                        }
                        String orderUpdateSataus = "11";
                        dcpOrder.setStatus(orderUpdateSataus);//变成已完成
                        String	orderDBJson =pj.beanToJson(dcpOrder);
                        HelpTools.writelog_fileName("【OrderIsBookStatusProcess查询订单更新状态】查询数据库后，赋值status=11返回："+orderDBJson,jddjLogFileName);

                        String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                        String hash_key = orderNo;
                        //先写缓存
                        SaveOrderRedis(redis_key, hash_key, orderDBJson);

                    }
                    catch (Exception e)
                    {
                        continue;
                    }


                }
            }
            else
            {
                HelpTools.writelog_fileName("【OrderIsBookStatusProcess处理预订单状态】没有需要处理的预订单！",jddjLogFileName);
                logger.info("\r\n******OrderIsBookStatusProcess没有需要处理的预订单******\r\n");
            }



        }
        catch (Exception e)
        {
            logger.error("\r\n******OrderIsBookStatusProcess处理预订单状态报错信息" + e.getMessage() + "******\r\n");
            HelpTools.writelog_fileName("【OrderIsBookStatusProcess循环处理预订单状态】处理预订单状态异常："+e.getMessage(),jddjLogFileName);
        }
    }

    /**
     * 订转销失败写订单历程(先删后插入)
     * @throws Exception
     */
    private void saveErrorOrderStatusLog (String eId, String orderNo,String loadDocType,String channelId,String errorMsg) throws  Exception
    {
        try
        {
            // region 写下日志

            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

            orderStatusLog onelv1 = new orderStatusLog();

            onelv1.setLoadDocType(loadDocType);
            onelv1.setChannelId(channelId);

            onelv1.setNeed_callback("N");
            onelv1.setNeed_notify("N");

            onelv1.seteId(eId);

            String opNO = "admin";

            String o_opName = "系统自动";

            onelv1.setOpNo(opNO);
            onelv1.setOpName(o_opName);
            onelv1.setOrderNo(orderNo);
            onelv1.setLoadDocBillType("");
            onelv1.setLoadDocOrderNo(orderNo);

            String statusType = "995";// 其他状态
            String updateStaus = "995";// 订单修改

            onelv1.setStatusType(statusType);
            onelv1.setStatus(updateStaus);

            String statusName = "订转销";
            String statusTypeName = "其他状态";
            onelv1.setStatusTypeName(statusTypeName);
            onelv1.setStatusName(statusName);

            String memo =  "订转销失败:"  + "<br>"+errorMsg;
            onelv1.setMemo(memo);
            String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            onelv1.setUpdate_time(updateDatetime);
            orderStatusLogList.add(onelv1);

            StringBuilder errorMessage = new StringBuilder();
            HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
            orderStatusLogList.clear();
            // endregion


        }
        catch (Exception e)
        {

        }

    }

}
