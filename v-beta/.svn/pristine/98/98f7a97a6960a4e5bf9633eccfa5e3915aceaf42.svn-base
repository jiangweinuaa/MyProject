package com.dsc.spos.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.SoapUtil;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.event.service.internal.EventListenerServiceInitiator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 销售交易数据上传至
 *
 * YUEFANG悦方
 * XSTKZ新上铁客站（高铁站）
 * TECH-TRANS科传 (根据商场名称STORENAME=  南丰城、恒隆广场、华润置地、万象城)
 * TECH-TRANS6科传6.0版全新接口
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class TradeChangeUploadToMarket extends InitJob
{

	Logger logger = LogManager.getLogger(TradeChangeUploadToMarket.class.getName());

	public static String logFileName = "TradeChangeUploadToMarketLog";

	static boolean bRun=false;//标记此服务是否正在执行中

	public String doExe()
	{

		//返回信息
		String sReturnInfo="";

		//此服务是否正在执行中

		if (bRun){
			logger.info("\r\n*********商场交易流水上传TradeChangeUploadToMarket正在执行中,本次调用取消:************\r\n");
			sReturnInfo="定时传输任务-商场交易流水上传TradeChangeUploadToMarket正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********商场交易流水上传TradeChangeUploadToMarket定时调用Start:************\r\n");
		this.Log("*********商场交易流水上传TradeChangeUploadToMarket定时调用Start:************");

		try
		{
			String getShopDatasSql = " SELECT * FROM DCP_MARKETACCESS WHERE STATUS =1 AND SERVICEURL IS NOT NULL";
			this.Log("商场交易流水上传TradeChangeUploadToWXMarket定时任务，查询门店商场对接配置表sql="+getShopDatasSql);
			// 查询出需要上传交易数据的门店
			List<Map<String, Object>> getShopDatas = this.doQueryData(getShopDatasSql, null);
			if (getShopDatas==null||getShopDatas.isEmpty())
			{
				logger.info("\r\n商场交易流水上传TradeChangeUploadToWXMarket查询门店商场对接配置表DCP_MARKETACCESS无数据 ,结束执行，END:************\r\n");
				this.Log("商场交易流水上传TradeChangeUploadToWXMarket定时任务，查询门店商场对接配置表DCP_MARKETACCESS，无数据,无需执行job！");
				sReturnInfo = "";
				return sReturnInfo;
			}
			for (Map<String, Object> map : getShopDatas)
			{
				String eId = map.getOrDefault("EID","").toString();
				String shopId = map.getOrDefault("SHOPID","").toString();
				String logStart = "商场交易流水上传，eId="+eId+",门店shopId="+shopId+",";
				//上传的服务器地址
				String serviceUrl=map.get("SERVICEURL")==null?"":map.get("SERVICEURL").toString();
				//用户名
				String userCode=map.get("USERCODE")==null?"":map.get("USERCODE").toString();
				//密码
				String passWord=map.get("PASSWORD")==null?"":map.get("PASSWORD").toString();
				//MARKETNO	商场：商场编码
				String marketNo=map.get("MARKETNO")==null?"":map.get("MARKETNO").toString();
				//STORENO	商场：店铺编码
				String storeNo=map.get("STORENO")==null?"":map.get("STORENO").toString();
				//STORENAME	商场：店铺名称
				String storeName=map.get("STORENAME")==null?"":map.get("STORENAME").toString();
				String marketName=map.get("MARKETNAME")==null?"":map.get("MARKETNAME").toString();
				//GOODSNO	店铺统一货号
				String goodsNo=map.get("GOODSNO")==null?"":map.get("GOODSNO").toString();
				//PAYCODE	店铺统一支付方式
				String payCode=map.get("PAYCODE")==null?"":map.get("PAYCODE").toString();
				//MACHINENO	商场：店铺统一收银机号
				String machineNo=map.get("MACHINENO")==null?"":map.get("MACHINENO").toString();
				//MARKETTYPE 商场平台类型：参见DCP_FIXEDVALUE.MARKETTYPE
				String marketType=map.get("MARKETTYPE")==null?"":map.get("MARKETTYPE").toString();
				//UPLOADSCALE	上传比例  未设置时，默认100  100即100%
				String uploadScale=map.get("UPLOADSCALE")==null?"100":map.get("UPLOADSCALE").toString();
				//是否上传所有渠道类型(Y-是；空/N-否 ) 默认N
				String isAllAppType = map.getOrDefault("ISALLAPPTYPE","N").toString();

				//是否空传Y/N（不会真正传给第三方只是鼎捷侧做上传标记）默认是N
				String isEmptyTran = map.getOrDefault("ISEMPTYTRANS","N").toString();

				this.Log("循环"+logStart+"对应商场中店铺编码storeNo="+storeNo+",收银机号machineNo="+machineNo+",商场名称marketName="+marketName+",【开始】");
				try
				{
					if ("YUEFANG".equals(marketType))//詹记悦方商场
					{
						this.yuefangUpload(map);
					}
					else if ("XSTKZ".equals(marketType))//新上铁客站（高铁站）
					{
						this.xstkzUpload(map);
					}
					else if ("TECH-TRANS".equals(marketType))//TECH-TRANS科传 (支持根据商场名称STORENAME=  南丰城、恒隆广场、华润置地、万象城),客户:詹记
					{
						this.TECHTRANSUpload(map);
					}
					else if ("TECH-TRANS6".equals(marketType))//科传6.0版全新接口，客户:潮品
					{
						this.TECHTRANS6Upload(map);
					}
					else if ("HAIDING".equals(marketType))//恒隆广场，客户:邦纳博地
					{
						this.haiDingUpload(map);
					}

				}
				catch (Exception e)
				{
					this.Log("循环"+logStart+"对应商场中店铺编码storeNo="+storeNo+",收银机号machineNo="+machineNo+",商场名称marketName="+marketName+",【结束】异常："+e.getMessage());
				}
			}

		}
		catch (Exception e)
		{
			logger.info("\r\n*********商场交易流水上传TradeChangeUploadToMarket异常，END:************\r\n");
			this.Log("*********商场交易流水上传TradeChangeUploadToMarket定时任务，异常:"+e.getMessage()+"*********");
		}
		finally
		{
			bRun=false;//
			logger.info("\r\n*********商场交易流水上传TradeChangeUploadToMarket定时调用END:************\r\n");
			this.Log("*********商场交易流水上传TradeChangeUploadToMarket定时调用END:*********");
		}

		return sReturnInfo;
	}

	public void Log(String log)  {
		try
		{
			HelpTools.writelog_fileName(log, logFileName);

		} catch (Exception e)
		{
			// TODO: handle exception
		}

	}

	protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
		if (pData == null || pData.size() == 0) {
			return;
		}
		StaticInfo.dao.useTransactionProcessData(pData);
	}

	/**
	 * 詹记悦方商场
	 * @param map
	 * @throws Exception
	 */
	private void yuefangUpload(Map<String, Object> map) throws Exception
	{
		String eId = map.getOrDefault("EID","").toString();
		String shopId = map.getOrDefault("SHOPID","").toString();
		String logStart = "商场交易流水上传，eId="+eId+",门店shopId="+shopId+",";
		//上传的服务器地址
		String serviceUrl=map.get("SERVICEURL")==null?"":map.get("SERVICEURL").toString();
		//用户名
		String userCode=map.get("USERCODE")==null?"":map.get("USERCODE").toString();
		//密码
		String passWord=map.get("PASSWORD")==null?"":map.get("PASSWORD").toString();
		//MARKETNO	商场：商场编码
		String marketNo=map.get("MARKETNO")==null?"":map.get("MARKETNO").toString();
		//STORENO	商场：店铺编码
		String storeNo=map.get("STORENO")==null?"":map.get("STORENO").toString();
		//STORENAME	商场：店铺名称
		String storeName=map.get("STORENAME")==null?"":map.get("STORENAME").toString();
		String marketName=map.get("MARKETNAME")==null?"":map.get("MARKETNAME").toString();
		//GOODSNO	店铺统一货号
		String goodsNo=map.get("GOODSNO")==null?"":map.get("GOODSNO").toString();
		//PAYCODE	店铺统一支付方式
		String payCode=map.get("PAYCODE")==null?"":map.get("PAYCODE").toString();
		//MACHINENO	商场：店铺统一收银机号
		String machineNo=map.get("MACHINENO")==null?"":map.get("MACHINENO").toString();
		//MARKETTYPE 商场平台类型：参见DCP_FIXEDVALUE.MARKETTYPE
		String marketType=map.get("MARKETTYPE")==null?"":map.get("MARKETTYPE").toString();
		//UPLOADSCALE	上传比例  未设置时，默认100  100即100%
		String uploadScale=map.get("UPLOADSCALE")==null?"100":map.get("UPLOADSCALE").toString();
		//this.Log("循环"+logStart+"对应商场中店铺编码storeNo="+storeNo+",收银机号machineNo="+machineNo+",商场名称marketName="+marketName+",【开始】");
        //是否上传所有渠道类型(Y-是；空/N-否 ) 默认N
		String isAllAppType = map.getOrDefault("ISALLAPPTYPE","N").toString();

		//金额类型(0或者空表示tot_amt，1表示saleamt，2表示TOT_AMT_MERRECEIVE)
		String amountType = map.getOrDefault("AMOUNTTYPE","0").toString();

		boolean paraFlag = true;
		StringBuffer paraFlagMess = new StringBuffer("");
		if (serviceUrl.isEmpty()||serviceUrl.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("接口url地址未维护,");

		}
		if (storeNo.isEmpty()||storeNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺编号未维护,");

		}
		if (machineNo.isEmpty()||machineNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("收银机号未维护,");

		}
		if (goodsNo.isEmpty()||goodsNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺货号未维护,");
		}
		if (!paraFlag)
		{
			this.Log(logStart+"查询该门店对接商场的基本设置参数不正确！("+paraFlagMess+")");
			return;
		}

		if (storeName.isEmpty()||storeName.trim().isEmpty())
		{
			storeName = "詹记";
		}
		String goodsName = storeName;


		Calendar cal = Calendar.getInstance();// 获得当前时间
		String sDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		//上传大于等于前一天的数据
		cal.add(Calendar.DATE, -1);
		String uploadDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		String appTyoe_sqlCon = "'"+ orderLoadDocType.POS+"','"+orderLoadDocType.POSANDROID+"','"+orderLoadDocType.SCAN+"'";
		String sql_sale = " select * from dcp_Sale "
				+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND SDATE='"+sDate+"' "
				+ " and (ISUPLOADED='N' or ISUPLOADED is null) "
				+ " and apptype in ("+appTyoe_sqlCon+") "
				+ " and TYPE in ('0','1','2') ";
		if ("Y".equals(isAllAppType))
		{
			sql_sale = " select * from dcp_Sale "
					+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND SDATE='"+sDate+"' "
					+ " and (ISUPLOADED='N' or ISUPLOADED is null) "
					+ " and TYPE in ('0','1','2') ";
		}
		this.Log("循环"+logStart+"查询该门店需要上传的销售单sql="+sql_sale);
		List<Map<String, Object>> getSaleDatas = this.doQueryData(sql_sale, null);
		if (getSaleDatas==null||getSaleDatas.isEmpty())
		{
			this.Log(logStart+"查询该门店需要上传的销售单无数据！");
			return;
		}
		this.Log("循环"+logStart+"查询该门店需要上传的交易数据总条数count="+getSaleDatas.size());
		int uploadCount = 0;
		String namespace  = "http://tempuri.org/";
		String method = "TrasAdd";//webSerice方法名
		//上传格式
		//1344,1172,2202110002,110203,詹记,-0.10,2022-02-11 06:30:00
		//商铺编号,POS机编号,小票号,商品编码,商品名称,交易金额,交易日期
		String saleInfo = "";
		for (Map<String, Object> par : getSaleDatas)
		{
			try
			{
				String saleNo = par.get("SALENO").toString();
				String saleNo_origin = saleNo;
				String tot_amt = par.getOrDefault("TOT_AMT","0").toString();
				if ("1".equals(amountType))
				{
					tot_amt = par.getOrDefault("SALEAMT","0").toString();
				}
				else if ("2".equals(amountType))
				{
					tot_amt = par.getOrDefault("TOT_AMT_MERRECEIVE","0").toString();
				}

				String sdate_sale = par.get("SDATE").toString();
				String stime_sale = par.get("STIME").toString();
				String type = par.get("TYPE").toString();

				//处理下saleNo，长度10
				if (saleNo.length()>10)
				{
					if (saleNo.startsWith("RETV"))
					{
						//防止退单和原单重复，因为订单生成销售单，如果直接截取后面10位会重复
						saleNo = saleNo.substring(4);
						if (saleNo.length()>8)
						{
							saleNo = "RE"+saleNo.substring(saleNo.length() - 8);
						}
						else
						{
							saleNo = "RE"+saleNo;
						}
					}
					else
					{
						saleNo = saleNo.substring(saleNo.length()-10);
					}

				}

				//处理下交易日期时间
				String saleTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				try
				{
					Date date1 = new SimpleDateFormat("yyyyMMddHHmmss").parse(sdate_sale+stime_sale);
					saleTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date1);

				}
				catch (Exception e)
				{

				}

				BigDecimal amt = new BigDecimal("0");
				BigDecimal old_amt = new BigDecimal(tot_amt);
				BigDecimal uploadScale_b = new BigDecimal(uploadScale);
				amt = old_amt.multiply(uploadScale_b).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
				if ("0".equals(type))
				{

				}
				else
				{
					//退单，负数
					if (amt.compareTo(BigDecimal.ZERO)==1)
					{
						amt = new BigDecimal("0").subtract(amt).setScale(2,BigDecimal.ROUND_HALF_UP);
					}
				}

				//拼接传入的交易数据
				//商铺编号,POS机编号,小票号,商品编码,商品名称,交易金额,交易日期
				saleInfo = "";
				saleInfo = storeNo+","+machineNo+","+saleNo+","+goodsNo+","+goodsName+","+amt+","+saleTime;


				String webServiceUrl = serviceUrl;//"http://159.75.202.201:7003/FTPService.asmx";
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("transStr", saleInfo);//webSerice方法传参名称
				this.Log("调用webService上传接口开始，url="+webServiceUrl+",传参请求req:"+param.toString());
				SoapObject req = SoapUtil.setRequestParam(namespace, method, param);
				String res = SoapUtil.request(req,webServiceUrl);
				this.Log("调用webService上传接口结束，返回res:"+res);

				//System.out.println("返回:"+res+"\n");
				if (res!=null&&res.toLowerCase().equals("true"))
				{
					uploadCount++;
					// values
					Map<String, DataValue> values = new HashMap<String, DataValue>();
					values.put("ISUPLOADED", new DataValue("Y", Types.VARCHAR));
					// condition
					Map<String, DataValue> conditions = new HashMap<String, DataValue>();
					conditions.put("EID", new DataValue(eId, Types.VARCHAR));
					conditions.put("SALENO", new DataValue(saleNo_origin, Types.VARCHAR));
					this.doUpdate("DCP_SALE", values, conditions);
					this.Log("调用webService上传接口成功后，更新dcp_Sale表标识字段ISUPLOADED成功。");
					//region 更新下上传记录日志表
					try
					{
						String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						String updateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						String sql_upload_log = " SELECT * FROM DCP_MARKET_UPLOAD_LOG WHERE EID='"+eId+"' and SHOPID='"+shopId+"' and BDATE='"+sdate_sale+"' ";
						List<Map<String, Object>> getUploadLogDatas = this.doQueryData(sql_upload_log, null);
						if (getUploadLogDatas==null||getUploadLogDatas.isEmpty())
						{
							String[] columns2 = {"EID","SHOPID","BDATE","MARKETTYPE","MARKETNO","MARKETNAME","STORENO","STORENAME","MACHINENO","AMT","CREATEOPID","CREATEOPNAME","CREATETIME"};
							DataValue[] insValue2 = new DataValue[]
									{
											new DataValue(eId, Types.VARCHAR),
											new DataValue(shopId, Types.VARCHAR),//SHOPID
											new DataValue(sdate_sale, Types.VARCHAR),//BDATE
											new DataValue(marketType, Types.VARCHAR),//MARKETTYPE
											new DataValue(marketNo, Types.VARCHAR),//MARKETNO
											new DataValue(marketName, Types.VARCHAR),//MARKETNAME
											new DataValue(storeNo, Types.VARCHAR),//STORENO
											new DataValue(storeName, Types.VARCHAR),//STORENAME
											new DataValue(machineNo, Types.VARCHAR),//MACHINENO
											new DataValue(amt, Types.VARCHAR),//amt
											new DataValue("admin", Types.VARCHAR),//CREATEOPID
											new DataValue("定时job", Types.VARCHAR),//CREATEOPNAME
											new DataValue(lastModiTime, Types.DATE),//	CREATETIME

									};
							this.doInsert("DCP_MARKET_UPLOAD_LOG",columns2,insValue2);
						}
						else
						{
							// values
							Map<String, DataValue> values2 = new HashMap<String, DataValue>();
							values2.put("AMT", new DataValue(amt,Types.FLOAT, DataValue.DataExpression.UpdateSelf));
							values2.put("LASTMODITIME", new DataValue(lastModiTime, Types.DATE));
							values2.put("UPDATE_TIME", new DataValue(updateTime, Types.VARCHAR));
							// condition
							Map<String, DataValue> conditions2 = new HashMap<String, DataValue>();
							conditions2.put("EID", new DataValue(eId, Types.VARCHAR));
							conditions2.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
							conditions2.put("BDATE", new DataValue(sdate_sale, Types.VARCHAR));
							this.doUpdate("DCP_MARKET_UPLOAD_LOG", values2, conditions2);

						}
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_LOG表成功！");
					}
					catch ( Exception e)
					{
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_LOG表异常:"+e.getMessage());
					}
					//endregion
				}


			}
			catch (Exception e)
			{
				this.Log(logStart+"循环该门店交易数据，准备调用webService接口时，异常："+e.getMessage());
			}
		}
		this.Log("循环"+logStart+"该门店当前上传成功的交易数据总条数count="+uploadCount);

	}

	/**
	 * 新上铁客站（高铁站）上传
	 * @param map
	 * @throws Exception
	 */
	private void xstkzUpload(Map<String, Object> map) throws Exception
	{

		String eId = map.getOrDefault("EID","").toString();
		String shopId = map.getOrDefault("SHOPID","").toString();
		String logStart = "商场交易流水上传，eId="+eId+",门店shopId="+shopId+",";
		//上传的服务器地址
		String serviceUrl=map.get("SERVICEURL")==null?"":map.get("SERVICEURL").toString();
		//接入密钥
		String valiKey = map.getOrDefault("VALIKEY","").toString();
		//CORPNAME 高铁需要的，所属分公司(统一提供，必填)
		String branch = map.getOrDefault("CORPNAME","").toString();

		//MARKETNO	商场：商场编码
		String marketNo=map.get("MARKETNO")==null?"":map.get("MARKETNO").toString();
		//MARKETNAME 商场名称
		String marketName=map.get("MARKETNAME")==null?"":map.get("MARKETNAME").toString();
		//STORENO	商场：店铺编码
		String storeNo=map.get("STORENO")==null?"":map.get("STORENO").toString();
		//STORENAME	商场：店铺名称
		String storeName=map.get("STORENAME")==null?"":map.get("STORENAME").toString();
		//GOODSNO	店铺统一货号
		String goodsNo=map.get("GOODSNO")==null?"":map.get("GOODSNO").toString();
		//PAYCODE	店铺统一支付方式
		String payCode=map.get("PAYCODE")==null?"":map.get("PAYCODE").toString();
		//MACHINENO	商场：店铺统一收银机号
		String machineNo=map.get("MACHINENO")==null?"":map.get("MACHINENO").toString();
		//MARKETTYPE 商场平台类型：参见DCP_FIXEDVALUE.MARKETTYPE
		String marketType=map.get("MARKETTYPE")==null?"":map.get("MARKETTYPE").toString();
		//UPLOADSCALE	上传比例  未设置时，默认100  100即100%
		String uploadScale=map.get("UPLOADSCALE")==null?"100":map.get("UPLOADSCALE").toString();
		//this.Log("循环"+logStart+"对应商场中店铺编码storeNo="+storeNo+",收银机号machineNo="+machineNo+",商场名称marketName="+marketName+",【开始】");
		//是否上传所有渠道类型(Y-是；空/N-否 ) 默认N
		String isAllAppType = map.getOrDefault("ISALLAPPTYPE","N").toString();

		//金额类型(0或者空表示tot_amt，1表示saleamt，2表示TOT_AMT_MERRECEIVE)
		String amountType = map.getOrDefault("AMOUNTTYPE","0").toString();

		boolean paraFlag = true;
		StringBuffer paraFlagMess = new StringBuffer("");
		if (serviceUrl.isEmpty()||serviceUrl.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("接口url地址未维护,");

		}
		if (valiKey.isEmpty()||valiKey.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("接口密钥key未维护,");

		}
		if (branch.isEmpty()||branch.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("商场(车站)所属公司名称未维护,");

		}
		if (marketNo.isEmpty()||marketNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("商场(车站)ID未维护,");

		}
		if (marketName.isEmpty()||marketName.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("商场(车站)名称未维护,");

		}
		if (storeNo.isEmpty()||storeNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺(商店)编号未维护,");

		}
		if (storeName.isEmpty()||storeName.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺(商店)名称未维护,");

		}
		/*if (machineNo.isEmpty()||machineNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("收银机号未维护,");

		}*/
		/*if (goodsNo.isEmpty()||goodsNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺货号未维护,");
		}*/
		if (!paraFlag)
		{
			this.Log(logStart+"查询该门店对接商场的基本设置参数不正确！("+paraFlagMess+")");
			return;
		}

		String goodsName = "";


		Calendar cal = Calendar.getInstance();// 获得当前时间
		String sDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		//上传大于等于前一天的数据
		cal.add(Calendar.DATE, -1);
		String uploadDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		String appTyoe_sqlCon = "'"+ orderLoadDocType.POS+"','"+orderLoadDocType.POSANDROID+"','"+orderLoadDocType.SCAN+"'";
		String sql_sale = " select * from dcp_Sale "
				+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND SDATE='"+sDate+"' "
				+ " and (ISUPLOADED='N' or ISUPLOADED is null) "
				+ " and apptype in ("+appTyoe_sqlCon+") "
				+ " and TYPE in ('0','1','2') ";
		if ("Y".equals(isAllAppType))
		{
			sql_sale = " select * from dcp_Sale "
					+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND SDATE='"+sDate+"' "
					+ " and (ISUPLOADED='N' or ISUPLOADED is null) "
					+ " and TYPE in ('0','1','2') ";
		}
		this.Log("循环"+logStart+"查询该门店需要上传的销售单sql="+sql_sale);
		List<Map<String, Object>> getSaleDatas = this.doQueryData(sql_sale, null);
		if (getSaleDatas==null||getSaleDatas.isEmpty())
		{
			this.Log(logStart+"查询该门店需要上传的销售单无数据！");
			return;
		}
		this.Log("循环"+logStart+"查询该门店需要上传的交易数据总条数count="+getSaleDatas.size());
		int uploadCount = 0;
		String namespace  = "http://tempuri.org/";

		//"6EA576539AEB4E878946911DA4E0C6BD"   //测试key
		String method = "Save";
		//上传格式
		//1344,1172,2202110002,110203,詹记,-0.10,2022-02-11 06:30:00
		//商铺编号,POS机编号,小票号,商品编码,商品名称,交易金额,交易日期
		String saleInfo = "";
		for (Map<String, Object> par : getSaleDatas)
		{
			try
			{
				String saleNo = par.get("SALENO").toString();
				String saleNo_origin = saleNo;
				//String tot_amt = par.getOrDefault("TOT_AMT","0").toString();
				//String tot_amt = par.getOrDefault("SALEAMT","0").toString();
				String tot_amt = par.getOrDefault("TOT_AMT","0").toString();
				if ("1".equals(amountType))
				{
					tot_amt = par.getOrDefault("SALEAMT","0").toString();
				}
				else if ("2".equals(amountType))
				{
					tot_amt = par.getOrDefault("TOT_AMT_MERRECEIVE","0").toString();
				}
				String sdate_sale = par.get("SDATE").toString();
				String stime_sale = par.get("STIME").toString();
				String type = par.get("TYPE").toString();
				String sourceNo = par.get("MACHINE").toString();
				JSONArray tcArr = new JSONArray();
				org.json.JSONObject tradeChangeJSON = new JSONObject();
				//处理下saleNo，长度100
				if (saleNo.length()>100)
				{
					saleNo = saleNo.substring(0,100);

				}

				//处理下交易日期时间
				String saleTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				try
				{
					Date date1 = new SimpleDateFormat("yyyyMMddHHmmss").parse(sdate_sale+stime_sale);
					saleTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date1);

				}
				catch (Exception e)
				{

				}

				BigDecimal amt = new BigDecimal("0");
				BigDecimal old_amt = new BigDecimal(tot_amt);
				BigDecimal uploadScale_b = new BigDecimal(uploadScale);
				amt = old_amt.multiply(uploadScale_b).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
				BigDecimal updateLogAmt = amt.setScale(2,BigDecimal.ROUND_HALF_UP);//更新上传的金额，有正有负合计
				String transType = "销售";
				if ("0".equals(type))
				{
					transType = "销售";
				}
				else
				{
					transType = "退货";
					//退单，负数
					if (amt.compareTo(BigDecimal.ZERO)==1)
					{
						updateLogAmt = new BigDecimal("0").subtract(amt).setScale(2,BigDecimal.ROUND_HALF_UP);
					}
				}

				tradeChangeJSON.put("STATIONNAME",marketName); // 车站名称，统一提供，（固定值，可以做成门店参数）
				tradeChangeJSON.put("STATIONID",marketNo); // 车站ID，统一提供， （固定值，可以做成门店参数）
				tradeChangeJSON.put("SHOPNAME", storeName); // 商铺名称， 可查数据库， 也可做成门店参数
				tradeChangeJSON.put("SHOPNO",storeNo); //商铺ID， def5b2d2-97fb-404b-a82f-29235c56fc2f
				tradeChangeJSON.put("BILLTYPE",""); //商品分类
				tradeChangeJSON.put("BILLNO", saleNo); // 订单号
				tradeChangeJSON.put("BILLALLPRICES",amt);//商品总价
				tradeChangeJSON.put("BILLTIME",saleTime); //销售时间
				tradeChangeJSON.put("PAYMENT","非现金");//支付方式
				tradeChangeJSON.put("TRANSTYPE",transType); //交易类型
				tradeChangeJSON.put("SOURCETYPE","POS机"); // 来源类型
				tradeChangeJSON.put("SOURCENO", sourceNo); //来源设备号
				tradeChangeJSON.put("BRANCH",branch); // 所属分公司

				tcArr.put(tradeChangeJSON);
				//拼接传入的交易数据
				//商铺编号,POS机编号,小票号,商品编码,商品名称,交易金额,交易日期
				saleInfo = tcArr.toString();

				String webServiceUrl = serviceUrl;//"http://58.213.118.119:8127/Ajax/TradeChange.asmx";
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("tradeChange", saleInfo);
				param.put("valiKey", valiKey);
				this.Log("调用webService上传接口开始，url="+webServiceUrl+",传参请求req:"+param.toString());
				SoapObject req = SoapUtil.setRequestParam(namespace, method, param);
				String res = SoapUtil.request(req,webServiceUrl);
				this.Log("调用webService上传接口结束，返回res:"+res);

				//System.out.println("返回:"+res+"\n");
				if (res!=null&&res.equals("成功"))
				{
					uploadCount++;
					// values
					Map<String, DataValue> values = new HashMap<String, DataValue>();
					values.put("ISUPLOADED", new DataValue("Y", Types.VARCHAR));
					// condition
					Map<String, DataValue> conditions = new HashMap<String, DataValue>();
					conditions.put("EID", new DataValue(eId, Types.VARCHAR));
					conditions.put("SALENO", new DataValue(saleNo_origin, Types.VARCHAR));
					this.doUpdate("DCP_SALE", values, conditions);
					this.Log("调用webService上传接口成功后，更新dcp_Sale表标识字段ISUPLOADED成功。");
					//region 更新下上传记录日志表
					try
					{
						String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						String updateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						String sql_upload_log = " SELECT * FROM DCP_MARKET_UPLOAD_LOG WHERE EID='"+eId+"' and SHOPID='"+shopId+"' and BDATE='"+sdate_sale+"' ";
						List<Map<String, Object>> getUploadLogDatas = this.doQueryData(sql_upload_log, null);
						if (getUploadLogDatas==null||getUploadLogDatas.isEmpty())
						{
							String[] columns2 = {"EID","SHOPID","BDATE","MARKETTYPE","MARKETNO","MARKETNAME","STORENO","STORENAME","MACHINENO","AMT","CREATEOPID","CREATEOPNAME","CREATETIME"};
							DataValue[] insValue2 = new DataValue[]
									{
											new DataValue(eId, Types.VARCHAR),
											new DataValue(shopId, Types.VARCHAR),//SHOPID
											new DataValue(sdate_sale, Types.VARCHAR),//BDATE
											new DataValue(marketType, Types.VARCHAR),//MARKETTYPE
											new DataValue(marketNo, Types.VARCHAR),//MARKETNO
											new DataValue(marketName, Types.VARCHAR),//MARKETNAME
											new DataValue(storeNo, Types.VARCHAR),//STORENO
											new DataValue(storeName, Types.VARCHAR),//STORENAME
											new DataValue(machineNo, Types.VARCHAR),//MACHINENO
											new DataValue(updateLogAmt, Types.VARCHAR),//amt
											new DataValue("admin", Types.VARCHAR),//CREATEOPID
											new DataValue("定时job", Types.VARCHAR),//CREATEOPNAME
											new DataValue(lastModiTime, Types.DATE),//	CREATETIME

									};
							this.doInsert("DCP_MARKET_UPLOAD_LOG",columns2,insValue2);
						}
						else
						{
							// values
							Map<String, DataValue> values2 = new HashMap<String, DataValue>();
							values2.put("AMT", new DataValue(updateLogAmt,Types.FLOAT, DataValue.DataExpression.UpdateSelf));
							values2.put("LASTMODITIME", new DataValue(lastModiTime, Types.DATE));
							values2.put("UPDATE_TIME", new DataValue(updateTime, Types.VARCHAR));
							// condition
							Map<String, DataValue> conditions2 = new HashMap<String, DataValue>();
							conditions2.put("EID", new DataValue(eId, Types.VARCHAR));
							conditions2.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
							conditions2.put("BDATE", new DataValue(sdate_sale, Types.VARCHAR));
							this.doUpdate("DCP_MARKET_UPLOAD_LOG", values2, conditions2);

						}
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_LOG表成功！");
					}
					catch ( Exception e)
					{
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_LOG表异常:"+e.getMessage());
					}
					//endregion
				}


			}
			catch (Exception e)
			{
				this.Log(logStart+"循环该门店交易数据，准备调用webService接口时，异常："+e.getMessage());
			}
		}
		this.Log("循环"+logStart+"该门店当前上传成功的交易数据总条数count="+uploadCount);



	}


	/**
	 * TECH-TRANS科传 (支持根据商场名称STORENAME=  南丰城、恒隆广场、华润置地、万象城)
	 * @param map
	 * @throws Exception
	 */
	private void TECHTRANSUpload(Map<String, Object> map) throws Exception
	{

		String eId = map.getOrDefault("EID","").toString();
		String shopId = map.getOrDefault("SHOPID","").toString();
		String logStart = "商场交易流水上传，eId="+eId+",门店shopId="+shopId+",";
		//上传的服务器地址
		String serviceUrl=map.get("SERVICEURL")==null?"":map.get("SERVICEURL").toString();
		//接入密钥
		String valiKey = map.getOrDefault("VALIKEY","").toString();
		//CORPNAME 高铁需要的，所属分公司(统一提供，必填)
		String branch = map.getOrDefault("CORPNAME","").toString();

		//MARKETNO	商场：商场编码
		String marketNo=map.get("MARKETNO")==null?"":map.get("MARKETNO").toString();
		//MARKETNAME 商场名称
		String marketName=map.get("MARKETNAME")==null?"":map.get("MARKETNAME").toString();
		//STORENO	商场：店铺编码
		String storeNo=map.get("STORENO")==null?"":map.get("STORENO").toString();
		//STORENAME	商场：店铺名称
		String storeName=map.get("STORENAME")==null?"":map.get("STORENAME").toString();
		//GOODSNO	店铺统一货号
		String goodsNo=map.get("GOODSNO")==null?"":map.get("GOODSNO").toString();
		//PAYCODE	店铺统一支付方式
		String payCode=map.get("PAYCODE")==null?"":map.get("PAYCODE").toString();
		//MACHINENO	商场：店铺统一收银机号
		String machineNo=map.get("MACHINENO")==null?"":map.get("MACHINENO").toString();
		//MARKETTYPE 商场平台类型：参见DCP_FIXEDVALUE.MARKETTYPE
		String marketType=map.get("MARKETTYPE")==null?"":map.get("MARKETTYPE").toString();
		//UPLOADSCALE	上传比例  未设置时，默认100  100即100%
		String uploadScale=map.get("UPLOADSCALE")==null?"100":map.get("UPLOADSCALE").toString();
		//this.Log("循环"+logStart+"对应商场中店铺编码storeNo="+storeNo+",收银机号machineNo="+machineNo+",商场名称marketName="+marketName+",【开始】");
        //是否上传所有渠道类型(Y-是；空/N-否 ) 默认N
		String isAllAppType = map.getOrDefault("ISALLAPPTYPE","N").toString();
		//是否空传Y/N（不会真正传给第三方只是鼎捷侧做上传标记）默认是N
		String isEmptyTran = map.getOrDefault("ISEMPTYTRANS","N").toString();

		//金额类型(0或者空表示tot_amt，1表示saleamt，2表示TOT_AMT_MERRECEIVE)
		String amountType = map.getOrDefault("AMOUNTTYPE","0").toString();

		boolean paraFlag = true;
		StringBuffer paraFlagMess = new StringBuffer("");
		if (serviceUrl.isEmpty()||serviceUrl.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("接口url地址未维护,");

		}
		if (branch.isEmpty()||branch.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("商场(车站)所属公司名称未维护,");

		}

		if (marketName.isEmpty()||marketName.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("商场(车站)名称未维护,");

		}
		if (storeNo.isEmpty()||storeNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺(商店)编号未维护,");

		}
		if (storeName.isEmpty()||storeName.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺(商店)名称未维护,");

		}
		if (!paraFlag)
		{
			this.Log(logStart+"查询该门店对接商场的基本设置参数不正确！("+paraFlagMess+")");
			return;
		}

		String params=map.get("PARAMS")==null?"":map.get("PARAMS").toString().trim();
		if (Check.Null(params))
		{
			this.Log(logStart+"查询该门店对接商场的PARAMS不能为空！");
			return;
		}

		String goodsName = "";


		Calendar cal = Calendar.getInstance();// 获得当前时间
		String sDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		//上传大于等于前一天的数据
		cal.add(Calendar.DATE, -1);
		String uploadDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		String appTyoe_sqlCon = "'"+ orderLoadDocType.POS+"','"+orderLoadDocType.POSANDROID+"','"+orderLoadDocType.SCAN+"'";
		String sql_sale = " select * from dcp_Sale "
				+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND SDATE='"+sDate+"' "
				+ " and (ISUPLOADED='N' or ISUPLOADED is null) "
				+ " and apptype in ("+appTyoe_sqlCon+") "
				+ " and TYPE in ('0','1','2') ";
		if ("Y".equals(isAllAppType))
		{
			sql_sale = " select * from dcp_Sale "
					+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND SDATE='"+sDate+"' "
					+ " and (ISUPLOADED='N' or ISUPLOADED is null) "
					+ " and TYPE in ('0','1','2') ";
		}
		this.Log("循环"+logStart+"查询该门店需要上传的销售单sql="+sql_sale);
		List<Map<String, Object>> getSaleDatas = this.doQueryData(sql_sale, null);
		if (getSaleDatas==null||getSaleDatas.isEmpty())
		{
			this.Log(logStart+"查询该门店需要上传的销售单无数据！");
			return;
		}
		this.Log("循环"+logStart+"查询该门店需要上传的交易数据总条数count="+getSaleDatas.size());
		int uploadCount = 0;

		for (Map<String, Object> par : getSaleDatas)
		{
			try
			{
				String saleNo = par.get("SALENO").toString();
				String saleNo_origin = saleNo;
				//String tot_amt = par.getOrDefault("TOT_AMT","0").toString();
				//String tot_amt = par.getOrDefault("SALEAMT","0").toString();
				String tot_amt = par.getOrDefault("TOT_AMT","0").toString();
				if ("1".equals(amountType))
				{
					tot_amt = par.getOrDefault("SALEAMT","0").toString();
				}
				else if ("2".equals(amountType))
				{
					tot_amt = par.getOrDefault("TOT_AMT_MERRECEIVE","0").toString();
				}
				String sdate_sale = par.get("SDATE").toString();
				String stime_sale = par.get("STIME").toString();
				String type = par.get("TYPE").toString();
				String sourceNo = par.get("MACHINE").toString();
				//处理下saleNo，长度40
				if (saleNo.length()>40)
				{
					saleNo = saleNo.substring(0,40);
				}

				//处理下交易日期时间
				String saleTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				try
				{
					Date date1 = new SimpleDateFormat("yyyyMMddHHmmss").parse(sdate_sale+stime_sale);
					saleTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date1);

				}
				catch (Exception e)
				{

				}

				BigDecimal amt = new BigDecimal("0");
				BigDecimal old_amt = new BigDecimal(tot_amt);
				BigDecimal uploadScale_b = new BigDecimal(uploadScale);
				amt = old_amt.multiply(uploadScale_b).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
				BigDecimal updateLogAmt = amt.setScale(2,BigDecimal.ROUND_HALF_UP);//更新上传的金额，有正有负合计
				String transType = "";
				if ("0".equals(type))
				{
					transType = "SALE";
				}
				else if ("1".equals(type) || "2".equals(type))
				{
					transType = "ONLINEREFUND";
					//退单，负数
					if (amt.compareTo(BigDecimal.ZERO)==1)
					{
						//这里是负数
						updateLogAmt = new BigDecimal("0").subtract(amt).setScale(2,BigDecimal.ROUND_HALF_UP);
					}
				}
				else
				{
					//暂不处理
					continue;
				}

				//接口返回成功
				boolean b_callSuccess=false;

				//不用上传
				if (!Check.Null(isEmptyTran) && isEmptyTran.equals("Y"))
				{
					b_callSuccess=true;
				}
				else
				{
					if("华润置地".equals(marketName))
					{
						//主体请求数据
						Map<String, Object> REQUEST_DATA=new HashMap<String, Object>();
						//公共参数
						Map<String, Object> HRT_ATTRS=new HashMap<String, Object>();

						com.alibaba.fastjson.JSONObject paramsJs = new com.alibaba.fastjson.JSONObject();
						paramsJs = JSON.parseObject(params,Feature.OrderedField);
						HRT_ATTRS.put("App_Sub_ID", paramsJs.getString("App_Sub_ID"));
						HRT_ATTRS.put("App_Token", paramsJs.getString("App_Token"));
						HRT_ATTRS.put("Api_ID", paramsJs.getString("Api_ID"));
						HRT_ATTRS.put("Api_Version", paramsJs.getString("Api_Version"));
						HRT_ATTRS.put("Sign_Method", paramsJs.getString("Sign_Method"));
						HRT_ATTRS.put("Format", paramsJs.getString("Format"));
						HRT_ATTRS.put("Partner_ID", paramsJs.getString("Partner_ID"));
						HRT_ATTRS.put("Sys_ID", paramsJs.getString("Sys_ID"));
						HRT_ATTRS.put("App_Pub_ID", paramsJs.getString("App_Pub_ID"));
						String isSandBox=paramsJs.getString("Is_Sand_Box");


						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
						String nowTime=sdf1.format(Calendar.getInstance().getTime());
						HRT_ATTRS.put("Time_Stamp", nowTime);

						//收银员编号
						REQUEST_DATA.put("cashierId", paramsJs.getString("cashierId"));
						//备注
						REQUEST_DATA.put("comments", par.get("MEMO")==null?"":par.get("MEMO").toString());
						//商品编号	itemCode	String	N	非必填; 未填写时请求参数只需写"itemList": [ ][商品编码如果添加必须与imPOS中系统保持一致，数据来源于imPOS，由各项目管理员提供]
						REQUEST_DATA.put("itemList", new String[]{});
						//商场编号
						REQUEST_DATA.put("mall", paramsJs.getString("mall"));//这个配置表字段也有

						//会员手机号
						REQUEST_DATA.put("mobile", par.get("CONTTEL")==null?"":par.get("CONTTEL").toString());
						//订单号
						REQUEST_DATA.put("orderId", par.get("SALENO")==null?"":par.get("SALENO").toString());


						List<Map<String, Object>> payList=new ArrayList<Map<String, Object>>();
						Map<String, Object> payMap=new HashMap<String, Object>();
						//支付方式
						payMap.put("paymentMethod", paramsJs.getString("paymentMethod"));//这个配置表字段也有
						//支付时间	time	String	Y	必填项 格式：yyyyMMddhhmmss
						String sdate=par.get("SDATE")==null?"":par.get("SDATE").toString();
						String stime=par.get("STIME")==null?"":par.get("STIME").toString();
						String time=sdate+stime;
						payMap.put("time", time);

						//支付金额（实收金额）value 正数：销售订单 负数：退货订单
						//应收金额	payAmt 正数：销售订单 负数：退货订单
						//优惠金额	discountAmt 正数：销售订单 负数：退货订单
						payMap.put("value", updateLogAmt);
						payMap.put("payAmt", updateLogAmt);
						payMap.put("discountAmt", 0);
						payList.add(payMap);
						REQUEST_DATA.put("payList", payList);

						//店铺编号
						REQUEST_DATA.put("store", paramsJs.getString("store"));//这个配置表字段也有
						//收银机编号
						REQUEST_DATA.put("tillId", paramsJs.getString("tillId"));//这个配置表字段也有
						REQUEST_DATA.put("time", time);

						//订单总金额	totalAmt 正数：销售订单 负数：退货订单
						REQUEST_DATA.put("totalAmt", updateLogAmt);
						//订单类型	type  销售：SALE 退货：ONLINEREFUND
						REQUEST_DATA.put("type", transType);
						//店铺验证密钥(登录密码)
						REQUEST_DATA.put("checkCode", paramsJs.getString("checkCode"));
						String ofno=par.get("OFNO")==null?"":par.get("OFNO").toString();
						//关联原订单号	refOrderId	String	N	长度：最小6位，最大40位； 若是退货订单可以填写原订单号

						if("ONLINEREFUND".equals(transType)){
							REQUEST_DATA.put("refOrderId", ofno);
						}

						String signSecert=paramsJs.getString("signSecert");

						Map<String, Object> signMap1=new HashMap<String, Object>();
						signMap1.put("REQUEST_DATA", REQUEST_DATA);
						signMap1.putAll(HRT_ATTRS);
						String sg1=getOrderByLexicographic(signMap1)+signSecert;
						String sign1=DigestUtils.md5Hex(sg1).toUpperCase();
						HRT_ATTRS.put("Sign", sign1);

						Map<String, Object> REQUEST=new HashMap<String, Object>();
						REQUEST.put("HRT_ATTRS", HRT_ATTRS);
						REQUEST.put("REQUEST_DATA", REQUEST_DATA);

						Map<String, Object> paramsMap=new HashMap<String, Object>();
						paramsMap.put("REQUEST", REQUEST);

						//请求内容
						String reqStr=com.alibaba.fastjson.JSON.toJSONString(paramsMap);
						//logger.info("\r\n******请求[华润置地]入参数：  " + reqStr + "\r\n");
						this.Log("******请求[华润置地]入参数： " + reqStr+",单号="+saleNo+",金额="+updateLogAmt);
						String resbody=HttpSend.doPost(serviceUrl,reqStr,null,"");
						//logger.info("\r\n******[华润置地]返回参数：  "+ "\r\n单号="+ saleNo + "\r\n" + resbody + "******\r\n");
						this.Log("******请求[华润置地]返回参数："+resbody+",单号="+saleNo+",金额="+updateLogAmt);
						if(Check.Null(resbody) || resbody.isEmpty() )
						{
							continue;
						}
						//简单判断一下json格式
						if ((resbody.startsWith("{") && resbody.endsWith("}")) || (resbody.startsWith("[") && resbody.endsWith("]")))
						{
							JSONObject jsonres = new JSONObject(resbody);
							JSONObject return_data_res = jsonres.getJSONObject("RETURN_DATA");

							//这种异常
							//{"RETURN_CODE":"E0MI0002","RETURN_DATA":{},"RETURN_DESC":"input parm [Time_Stamp] timeout","RETURN_STAMP":"2022-10-21 09:52:26:318"}
							if (return_data_res.has("header"))
							{
								JSONObject header_res = return_data_res.getJSONObject("header");

								if (header_res.has("errcode"))
								{
									String errcode_res = header_res.get("errcode").toString();
									if (errcode_res!= null && errcode_res.equals("0000"))
									{
										b_callSuccess=true;
									}
								}
								else
								{
									b_callSuccess=false;
								}
							}
							else
							{
								b_callSuccess=false;
							}
						}
						else
						{
							logger.info("\r\n******[华润置地]返回json格式错误：  "+ "\r\n单号="+ saleNo + "******\r\n");
							continue;
						}
					}
				}

				//这段更新判断公用，上面请求规格各接口单独处理
				if (b_callSuccess)
				{
					uploadCount++;
					// values
					Map<String, DataValue> values = new HashMap<String, DataValue>();
					values.put("ISUPLOADED", new DataValue("Y", Types.VARCHAR));
					// condition
					Map<String, DataValue> conditions = new HashMap<String, DataValue>();
					conditions.put("EID", new DataValue(eId, Types.VARCHAR));
					conditions.put("SALENO", new DataValue(saleNo_origin, Types.VARCHAR));
					this.doUpdate("DCP_SALE", values, conditions);
					this.Log("调用webService上传接口成功后，更新dcp_Sale表标识字段ISUPLOADED成功。");
					//region 更新下上传记录日志表
					try
					{
						String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						String updateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						String sql_upload_log = " SELECT * FROM DCP_MARKET_UPLOAD_LOG WHERE EID='"+eId+"' and SHOPID='"+shopId+"' and BDATE='"+sdate_sale+"' ";
						List<Map<String, Object>> getUploadLogDatas = this.doQueryData(sql_upload_log, null);
						if (getUploadLogDatas==null||getUploadLogDatas.isEmpty())
						{
							String[] columns2 = {"EID","SHOPID","BDATE","MARKETTYPE","MARKETNO","MARKETNAME","STORENO","STORENAME","MACHINENO","AMT","CREATEOPID","CREATEOPNAME","CREATETIME"};
							DataValue[] insValue2 = new DataValue[]
									{
											new DataValue(eId, Types.VARCHAR),
											new DataValue(shopId, Types.VARCHAR),//SHOPID
											new DataValue(sdate_sale, Types.VARCHAR),//BDATE
											new DataValue(marketType, Types.VARCHAR),//MARKETTYPE
											new DataValue(marketNo, Types.VARCHAR),//MARKETNO
											new DataValue(marketName, Types.VARCHAR),//MARKETNAME
											new DataValue(storeNo, Types.VARCHAR),//STORENO
											new DataValue(storeName, Types.VARCHAR),//STORENAME
											new DataValue(machineNo, Types.VARCHAR),//MACHINENO
											new DataValue(updateLogAmt, Types.VARCHAR),//amt
											new DataValue("admin", Types.VARCHAR),//CREATEOPID
											new DataValue("定时job", Types.VARCHAR),//CREATEOPNAME
											new DataValue(lastModiTime, Types.DATE),//	CREATETIME

									};
							this.doInsert("DCP_MARKET_UPLOAD_LOG",columns2,insValue2);
						}
						else
						{
							// values
							Map<String, DataValue> values2 = new HashMap<String, DataValue>();
							values2.put("AMT", new DataValue(updateLogAmt,Types.FLOAT, DataValue.DataExpression.UpdateSelf));
							values2.put("LASTMODITIME", new DataValue(lastModiTime, Types.DATE));
							values2.put("UPDATE_TIME", new DataValue(updateTime, Types.VARCHAR));
							// condition
							Map<String, DataValue> conditions2 = new HashMap<String, DataValue>();
							conditions2.put("EID", new DataValue(eId, Types.VARCHAR));
							conditions2.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
							conditions2.put("BDATE", new DataValue(sdate_sale, Types.VARCHAR));
							this.doUpdate("DCP_MARKET_UPLOAD_LOG", values2, conditions2);

						}
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_LOG表成功！");
					}
					catch ( Exception e)
					{
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_LOG表异常:"+e.getMessage());
					}
					//endregion

					//region 更新下上传记录日志表
					try
					{
						String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						String updateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						String[] columns2 = {"EID","SHOPID","BDATE","SALENO","MARKETTYPE","MARKETNO","MARKETNAME","STORENO","STORENAME","MACHINENO","AMT","CREATEOPID","CREATEOPNAME","CREATETIME"};
						DataValue[] insValue2 = new DataValue[]
								{
										new DataValue(eId, Types.VARCHAR),
										new DataValue(shopId, Types.VARCHAR),//SHOPID
										new DataValue(sdate_sale, Types.VARCHAR),//BDATE
										new DataValue(saleNo_origin, Types.VARCHAR),//SALENO
										new DataValue(marketType, Types.VARCHAR),//MARKETTYPE
										new DataValue(marketNo, Types.VARCHAR),//MARKETNO
										new DataValue(marketName, Types.VARCHAR),//MARKETNAME
										new DataValue(storeNo, Types.VARCHAR),//STORENO
										new DataValue(storeName, Types.VARCHAR),//STORENAME
										new DataValue(machineNo, Types.VARCHAR),//MACHINENO
										new DataValue(updateLogAmt, Types.VARCHAR),//amt
										new DataValue("admin", Types.VARCHAR),//CREATEOPID
										new DataValue("定时job", Types.VARCHAR),//CREATEOPNAME
										new DataValue(lastModiTime, Types.DATE),//	CREATETIME

								};
						this.doInsert("DCP_MARKET_UPLOAD_DETAIL",columns2,insValue2);


						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_DETAIL表成功！");
					}
					catch ( Exception e)
					{
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_DETAIL表异常:"+e.getMessage());
					}
					//endregion

				}

			}
			catch (Exception e)
			{
				this.Log(logStart+"循环该门店交易数据，准备调用接口时，异常："+e.getMessage());
			}
		}
		this.Log("循环"+logStart+"该门店当前上传成功的交易数据总条数count="+uploadCount);



	}


	/**
	 * 科传6.0版全新接口，客户:潮品
	 * @param map
	 * @throws Exception
	 */
	private void TECHTRANS6Upload(Map<String, Object> map) throws Exception
	{
		String eId = map.getOrDefault("EID","").toString();
		String shopId = map.getOrDefault("SHOPID","").toString();
		String logStart = "商场交易流水上传，eId="+eId+",门店shopId="+shopId+",";
		//上传的服务器地址
		String serviceUrl=map.get("SERVICEURL")==null?"":map.get("SERVICEURL").toString();

		//MARKETNO	商场：商场编码
		String marketNo=map.get("MARKETNO")==null?"":map.get("MARKETNO").toString();
		//MARKETNAME 商场名称
		String marketName=map.get("MARKETNAME")==null?"":map.get("MARKETNAME").toString();
		//STORENO	商场：店铺编码
		String storeNo=map.get("STORENO")==null?"":map.get("STORENO").toString();
		//STORENAME	商场：店铺名称
		String storeName=map.get("STORENAME")==null?"":map.get("STORENAME").toString();
		//GOODSNO	店铺统一货号
		String goodsNo=map.get("GOODSNO")==null?"":map.get("GOODSNO").toString();
		//PAYCODE	店铺统一支付方式
		String payCode=map.get("PAYCODE")==null?"":map.get("PAYCODE").toString();
		//MACHINENO	商场：店铺统一收银机号
		String machineNo=map.get("MACHINENO")==null?"":map.get("MACHINENO").toString();
		//MARKETTYPE 商场平台类型：参见DCP_FIXEDVALUE.MARKETTYPE
		String marketType=map.get("MARKETTYPE")==null?"":map.get("MARKETTYPE").toString();
		//UPLOADSCALE	上传比例  未设置时，默认100  100即100%
		String uploadScale=map.get("UPLOADSCALE")==null?"100":map.get("UPLOADSCALE").toString();
		//this.Log("循环"+logStart+"对应商场中店铺编码storeNo="+storeNo+",收银机号machineNo="+machineNo+",商场名称marketName="+marketName+",【开始】");
		//是否上传所有渠道类型(Y-是；空/N-否 ) 默认N
		String isAllAppType = map.getOrDefault("ISALLAPPTYPE","N").toString();

		//金额类型(0或者空表示tot_amt，1表示saleamt，2表示TOT_AMT_MERRECEIVE)
		String amountType = map.getOrDefault("AMOUNTTYPE","0").toString();

		boolean paraFlag = true;
		StringBuffer paraFlagMess = new StringBuffer("");
		if (serviceUrl.isEmpty()||serviceUrl.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("接口url地址未维护,");

		}

		if (marketName.isEmpty()||marketName.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("商场(车站)名称未维护,");

		}
		if (storeNo.isEmpty()||storeNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺(商店)编号未维护,");

		}
		if (storeName.isEmpty()||storeName.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺(商店)名称未维护,");

		}
		if (!paraFlag)
		{
			this.Log(logStart+"查询该门店对接商场的基本设置参数不正确！("+paraFlagMess+")");
			return;
		}

		String params=map.get("PARAMS")==null?"":map.get("PARAMS").toString().trim();
		if (Check.Null(params))
		{
			this.Log(logStart+"查询该门店对接商场的PARAMS不能为空！");
			return;
		}

		String goodsName = "";


		Calendar cal = Calendar.getInstance();// 获得当前时间
		String sDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		//上传大于等于前一天的数据
		cal.add(Calendar.DATE, -1);
		String uploadDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		String appTyoe_sqlCon = "'"+ orderLoadDocType.POS+"','"+orderLoadDocType.POSANDROID+"','"+orderLoadDocType.SCAN+"'";
		String sql_sale = " select * from dcp_Sale "
				+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND SDATE='"+sDate+"' "
				+ " and (ISUPLOADED='N' or ISUPLOADED is null) "
				+ " and apptype in ("+appTyoe_sqlCon+") "
				+ " and TYPE in ('0','1','2') ";
		if ("Y".equals(isAllAppType))
		{
			sql_sale = " select * from dcp_Sale "
					+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND SDATE='"+sDate+"' "
					+ " and (ISUPLOADED='N' or ISUPLOADED is null) "
					+ " and TYPE in ('0','1','2') ";
		}
		this.Log("循环"+logStart+"查询该门店需要上传的销售单sql="+sql_sale);
		List<Map<String, Object>> getSaleDatas = this.doQueryData(sql_sale, null);
		if (getSaleDatas==null||getSaleDatas.isEmpty())
		{
			this.Log(logStart+"查询该门店需要上传的销售单无数据！");
			return;
		}
		this.Log("循环"+logStart+"查询该门店需要上传的交易数据总条数count="+getSaleDatas.size());
		int uploadCount = 0;

		for (Map<String, Object> par : getSaleDatas)
		{
			try
			{
				String saleNo = par.get("SALENO").toString();
				String saleNo_origin = saleNo;
				String tot_amt = par.getOrDefault("TOT_AMT","0").toString();
				if ("1".equals(amountType))
				{
					tot_amt = par.getOrDefault("SALEAMT","0").toString();
				}
				else if ("2".equals(amountType))
				{
					tot_amt = par.getOrDefault("TOT_AMT_MERRECEIVE","0").toString();
				}
				String tot_qty = par.getOrDefault("TOT_QTY","0").toString();

				String bDate = par.get("BDATE").toString();
				String sdate_sale = par.get("SDATE").toString();
				String stime_sale = par.get("STIME").toString();
				String type = par.get("TYPE").toString();
				String sourceNo = par.get("MACHINE").toString();
				//处理下saleNo，长度40
				if (saleNo.length()>40)
				{
					saleNo = saleNo.substring(0,40);
				}

				//处理下交易日期时间
				String saleTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				try
				{
					Date date1 = new SimpleDateFormat("yyyyMMddHHmmss").parse(sdate_sale+stime_sale);
					saleTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date1);

				}
				catch (Exception e)
				{

				}

				BigDecimal amt = new BigDecimal("0");
				BigDecimal old_amt = new BigDecimal(tot_amt);
				BigDecimal uploadScale_b = new BigDecimal(uploadScale);
				amt = old_amt.multiply(uploadScale_b).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
				BigDecimal updateLogAmt = amt.setScale(2,BigDecimal.ROUND_HALF_UP);//更新上传的金额，有正有负合计
				BigDecimal updateLogQty = new BigDecimal(tot_qty).setScale(2,BigDecimal.ROUND_HALF_UP);//更新上传的金额，有正有负合计

				String transType = "";
				if ("0".equals(type))
				{
					transType = "SALE";
				}
				else if ("1".equals(type) || "2".equals(type))
				{
					transType = "ONLINEREFUND";
					//退单，负数
					if (amt.compareTo(BigDecimal.ZERO)==1)
					{
						//这里是负数
						updateLogAmt = new BigDecimal("0").subtract(amt).setScale(2,BigDecimal.ROUND_HALF_UP);
						updateLogQty=new BigDecimal("0").subtract(updateLogQty);
					}
				}
				else
				{
					//暂不处理
					continue;
				}

				//接口返回成功
				boolean b_callSuccess=false;


				//*****主体请求数据开始**********
				Map<String, Object> REQUEST_DATA=new HashMap<String, Object>();

				//
				Map<String, Object> transHeader = new HashMap<String, Object>();
				Map<String, Object> salesTotal = new HashMap<String, Object>();
				List<Map<String, Object>> salesItem=new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> salesTender=new ArrayList<Map<String, Object>>();
				Map<String, Object> orgSalesMemo = new HashMap<String, Object>();//退单的原单信息

				//单据唯一key
				String docKey=eId +"." + sdate_sale +"." + shopId +"." + machineNo+"." +saleNo;

				com.alibaba.fastjson.JSONObject paramsJs = new com.alibaba.fastjson.JSONObject();
				paramsJs = JSON.parseObject(params,Feature.OrderedField);
				REQUEST_DATA.put("apiKey", paramsJs.getString("APIKEY"));
				REQUEST_DATA.put("signature", "");
				REQUEST_DATA.put("docKey", docKey);//组成格式：e.g.日期.店铺编号.收款机编号.销售单号 比如：20151001.SH001.01.S00000000
				REQUEST_DATA.put("transHeader", transHeader);
				REQUEST_DATA.put("salesTotal", salesTotal);
				REQUEST_DATA.put("salesItem", salesItem);
				REQUEST_DATA.put("salesTender", salesTender);
				if (transType.equals("ONLINEREFUND"))
				{
					REQUEST_DATA.put("orgSalesMemo", orgSalesMemo);
				}

				//单头transHeader
				transHeader.put("txDate", PosPub.GetStringDateLine(bDate,0));//交易日期，yyyy-mm-dd
				transHeader.put("ledgerDatetime",saleTime);//yyyy-mm-dd hh:mm:ss 客戶端发生的日期及时间
				transHeader.put("storeCode",paramsJs.getString("storeCode"));//店铺编号
				transHeader.put("tillId",paramsJs.getString("tillId"));//收款机号
				transHeader.put("docNo",saleNo);//销售单号

				//总计salesTotal
				salesTotal.put("cashier",paramsJs.getString("cashier"));//收款员编号
				salesTotal.put("netQty",updateLogQty);//净销售数量（销售为正数，退货为负数）
				salesTotal.put("netAmount",updateLogAmt);//净销售金额（销售为正数，退货为负数）

				//退货原单
				if (transType.equals("ONLINEREFUND"))
				{
					String ofno = par.get("OFNO").toString();

					String sql_Old_sale = " select * from dcp_Sale "
							+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND saleno='"+ofno +"' ";

					this.Log("循环"+logStart+"退单查原单sql="+sql_Old_sale);
					List<Map<String, Object>> getOldSaleDatas = this.doQueryData(sql_Old_sale, null);
					if (getOldSaleDatas == null || getOldSaleDatas.size()==0)
					{
						//此单不处理
						continue;
					}
					String old_bdate=getOldSaleDatas.get(0).get("BDATE").toString();
					String old_sdate_sale=getOldSaleDatas.get(0).get("SDATE").toString();
					String old_machineNo=getOldSaleDatas.get(0).get("MACHINE").toString();

					//原单据唯一key
					String old_docKey=eId +"." + old_sdate_sale +"." + shopId +"." + old_machineNo+"." +ofno;

					orgSalesMemo.put("txDate",PosPub.GetStringDateLine(old_bdate,0));//交易日期 yyyy-mm-dd
					orgSalesMemo.put("storeCode",paramsJs.getString("storeCode"));//店铺编号
					orgSalesMemo.put("tillId",paramsJs.getString("tillId"));//收款机号
					orgSalesMemo.put("docNo",ofno);//销售单号
					orgSalesMemo.put("docKey",old_docKey);//销售单的 DocKey
				}

				//固定商品
				Map<String, Object> tempSalesItem = new HashMap<String, Object>();
				tempSalesItem.put("salesLineNumber",1L);//
				tempSalesItem.put("itemCode",paramsJs.getString("itemCode"));//
				tempSalesItem.put("itemOrgId",paramsJs.getString("itemOrgId"));//
				tempSalesItem.put("itemLotNum","*");//
				tempSalesItem.put("inventoryType","0");//
				tempSalesItem.put("qty",updateLogQty);//
				tempSalesItem.put("itemDiscountLess",0);//
				tempSalesItem.put("totalDiscountLess",0);//
				tempSalesItem.put("netAmount",updateLogAmt);//
				salesItem.add(tempSalesItem);

				Map<String, Object> tempSalesTender = new HashMap<String, Object>();
				tempSalesTender.put("baseCurrencyCode",paramsJs.getString("baseCurrencyCode"));//本币币种:RMB
				tempSalesTender.put("tenderCode",paramsJs.getString("tenderCode"));//付款方式
				tempSalesTender.put("payAmount",updateLogAmt);//付款金额
				tempSalesTender.put("baseAmount",updateLogAmt);//付款金额本币
				tempSalesTender.put("excessAmount",0);//溢收
				salesTender.add(tempSalesTender);

				//对象转换
				JsonParser jp=new JsonParser();
				JsonObject jsonOB=jp.parse(com.alibaba.fastjson.JSON.toJSONString(REQUEST_DATA)).getAsJsonObject();

				//计算签名
				String signed=calculateSignature(jsonOB);
				REQUEST_DATA.put("signature",signed.toUpperCase());

				//请求内容
				String reqStr=com.alibaba.fastjson.JSON.toJSONString(REQUEST_DATA);
				logger.info("\r\n******请求[科传6.0]入参数：  " + reqStr + "\r\n");
				String resbody=HttpSend.doPost(serviceUrl,reqStr,null,"");
				logger.info("\r\n******[科传6.0]返回参数：  "+ "\r\n单号="+ saleNo + "\r\n" + resbody + "******\r\n");

				if(Check.Null(resbody) || resbody.isEmpty() )
				{
					continue;
				}
				//简单判断一下json格式
				if ((resbody.startsWith("{") && resbody.endsWith("}")) || (resbody.startsWith("[") && resbody.endsWith("]")))
				{
					JSONObject jsonres = new JSONObject(resbody);
					if (jsonres.has("errorCode"))
					{
						String errorCode = jsonres.get("errorCode").toString();
						if (errorCode.equals("0"))
						{
							b_callSuccess=true;
						}
					}
					else
					{
						b_callSuccess=false;
					}
				}
				else
				{
					logger.info("\r\n******[科传6.0]返回json格式错误：  "+ "\r\n单号="+ saleNo + "******\r\n");
					continue;
				}
				//*****主体请求数据结束**********



				//这段更新判断公用，上面请求规格各接口单独处理
				if (b_callSuccess)
				{
					uploadCount++;
					// values
					Map<String, DataValue> values = new HashMap<String, DataValue>();
					values.put("ISUPLOADED", new DataValue("Y", Types.VARCHAR));
					// condition
					Map<String, DataValue> conditions = new HashMap<String, DataValue>();
					conditions.put("EID", new DataValue(eId, Types.VARCHAR));
					conditions.put("SALENO", new DataValue(saleNo_origin, Types.VARCHAR));
					this.doUpdate("DCP_SALE", values, conditions);
					this.Log("调用webService上传接口成功后，更新dcp_Sale表标识字段ISUPLOADED成功。");
					//region 更新下上传记录日志表
					try
					{
						String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						String updateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						String sql_upload_log = " SELECT * FROM DCP_MARKET_UPLOAD_LOG WHERE EID='"+eId+"' and SHOPID='"+shopId+"' and BDATE='"+sdate_sale+"' ";
						List<Map<String, Object>> getUploadLogDatas = this.doQueryData(sql_upload_log, null);
						if (getUploadLogDatas==null||getUploadLogDatas.isEmpty())
						{
							String[] columns2 = {"EID","SHOPID","BDATE","MARKETTYPE","MARKETNO","MARKETNAME","STORENO","STORENAME","MACHINENO","AMT","CREATEOPID","CREATEOPNAME","CREATETIME"};
							DataValue[] insValue2 = new DataValue[]
									{
											new DataValue(eId, Types.VARCHAR),
											new DataValue(shopId, Types.VARCHAR),//SHOPID
											new DataValue(sdate_sale, Types.VARCHAR),//BDATE
											new DataValue(marketType, Types.VARCHAR),//MARKETTYPE
											new DataValue(marketNo, Types.VARCHAR),//MARKETNO
											new DataValue(marketName, Types.VARCHAR),//MARKETNAME
											new DataValue(storeNo, Types.VARCHAR),//STORENO
											new DataValue(storeName, Types.VARCHAR),//STORENAME
											new DataValue(machineNo, Types.VARCHAR),//MACHINENO
											new DataValue(updateLogAmt, Types.VARCHAR),//amt
											new DataValue("admin", Types.VARCHAR),//CREATEOPID
											new DataValue("定时job", Types.VARCHAR),//CREATEOPNAME
											new DataValue(lastModiTime, Types.DATE),//	CREATETIME

									};
							this.doInsert("DCP_MARKET_UPLOAD_LOG",columns2,insValue2);
						}
						else
						{
							// values
							Map<String, DataValue> values2 = new HashMap<String, DataValue>();
							values2.put("AMT", new DataValue(updateLogAmt,Types.FLOAT, DataValue.DataExpression.UpdateSelf));
							values2.put("LASTMODITIME", new DataValue(lastModiTime, Types.DATE));
							values2.put("UPDATE_TIME", new DataValue(updateTime, Types.VARCHAR));
							// condition
							Map<String, DataValue> conditions2 = new HashMap<String, DataValue>();
							conditions2.put("EID", new DataValue(eId, Types.VARCHAR));
							conditions2.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
							conditions2.put("BDATE", new DataValue(sdate_sale, Types.VARCHAR));
							this.doUpdate("DCP_MARKET_UPLOAD_LOG", values2, conditions2);

						}
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_LOG表成功！");
					}
					catch ( Exception e)
					{
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_LOG表异常:"+e.getMessage());
					}
					//endregion
				}

			}
			catch (Exception e)
			{
				this.Log(logStart+"循环该门店交易数据，准备调用接口时，异常："+e.getMessage());
			}
		}
		this.Log("循环"+logStart+"该门店当前上传成功的交易数据总条数count="+uploadCount);

	}


	/**
	 * 恒隆广场商场(邦纳博地)
	 * @param map
	 * @throws Exception
	 */
	private void haiDingUpload(Map<String, Object> map) throws Exception
	{
		String eId = map.getOrDefault("EID","").toString();
		String shopId = map.getOrDefault("SHOPID","").toString();
		String logStart = "商场交易流水上传，eId="+eId+",门店shopId="+shopId+",";
		//上传的服务器地址
		String serviceUrl=map.get("SERVICEURL")==null?"":map.get("SERVICEURL").toString();
		//用户名
		String userCode=map.get("USERCODE")==null?"":map.get("USERCODE").toString();
		//密码
		String passWord=map.get("PASSWORD")==null?"":map.get("PASSWORD").toString();
		//MARKETNO	商场：商场编码
		String marketNo=map.get("MARKETNO")==null?"":map.get("MARKETNO").toString();
		//STORENO	商场：店铺编码
		String storeNo=map.get("STORENO")==null?"":map.get("STORENO").toString();
		//STORENAME	商场：店铺名称
		String storeName=map.get("STORENAME")==null?"":map.get("STORENAME").toString();
		String marketName=map.get("MARKETNAME")==null?"":map.get("MARKETNAME").toString();
		//GOODSNO	店铺统一货号
		String goodsNo=map.get("GOODSNO")==null?"":map.get("GOODSNO").toString();
		//PAYCODE	店铺统一支付方式
		String payCode=map.get("PAYCODE")==null?"":map.get("PAYCODE").toString();
		//MACHINENO	商场：店铺统一收银机号
		String machineNo=map.get("MACHINENO")==null?"":map.get("MACHINENO").toString();
		//MARKETTYPE 商场平台类型：参见DCP_FIXEDVALUE.MARKETTYPE
		String marketType=map.get("MARKETTYPE")==null?"":map.get("MARKETTYPE").toString();
		//UPLOADSCALE	上传比例  未设置时，默认100  100即100%
		String uploadScale=map.get("UPLOADSCALE")==null?"100":map.get("UPLOADSCALE").toString();
		//this.Log("循环"+logStart+"对应商场中店铺编码storeNo="+storeNo+",收银机号machineNo="+machineNo+",商场名称marketName="+marketName+",【开始】");
		//是否上传所有渠道类型(Y-是；空/N-否 ) 默认N
		String isAllAppType = map.getOrDefault("ISALLAPPTYPE","N").toString();
        //金额类型(0或者空表示tot_amt，1表示saleamt，2表示TOT_AMT_MERRECEIVE)
		String amountType = map.getOrDefault("AMOUNTTYPE","0").toString();

		boolean paraFlag = true;
		StringBuffer paraFlagMess = new StringBuffer("");
		if (serviceUrl.isEmpty()||serviceUrl.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("接口url地址未维护,");

		}
		if (userCode.isEmpty()||userCode.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("用户名未维护,");

		}
		if (passWord.isEmpty()||passWord.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("密码未维护,");

		}
		if (storeNo.isEmpty()||storeNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺编号未维护,");

		}
		if (machineNo.isEmpty()||machineNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("收银机号未维护,");

		}
		if (goodsNo.isEmpty()||goodsNo.trim().isEmpty())
		{
			paraFlag = false;
			paraFlagMess.append("店铺货号未维护,");
		}
		if (!paraFlag)
		{
			this.Log(logStart+"查询该门店对接商场的基本设置参数不正确！("+paraFlagMess+")");
			return;
		}

		if (storeName.isEmpty()||storeName.trim().isEmpty())
		{
			storeName = "恒隆广场";
		}
		String goodsName = storeName;


		Calendar cal = Calendar.getInstance();// 获得当前时间
		String sDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		//上传大于等于前一天的数据
		cal.add(Calendar.DATE, -1);
		String uploadDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		String appTyoe_sqlCon = "'"+ orderLoadDocType.POS+"','"+orderLoadDocType.POSANDROID+"','"+orderLoadDocType.SCAN+"'";
		String sql_sale = " select * from dcp_Sale "
				+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND SDATE='"+sDate+"' "
				+ " and (ISUPLOADED='N' or ISUPLOADED is null) "
				+ " and apptype in ("+appTyoe_sqlCon+") "
				+ " and TYPE in ('0','1','2') ";
		if ("Y".equals(isAllAppType))
		{
			sql_sale = " select * from dcp_Sale "
					+ " where EID='"+eId+"' and SHOPID='"+shopId+"' AND SDATE='"+sDate+"' "
					+ " and (ISUPLOADED='N' or ISUPLOADED is null) "
					+ " and TYPE in ('0','1','2') ";
		}
		this.Log("循环"+logStart+"查询该门店需要上传的销售单sql="+sql_sale);
		List<Map<String, Object>> getSaleDatas = this.doQueryData(sql_sale, null);
		if (getSaleDatas==null||getSaleDatas.isEmpty())
		{
			this.Log(logStart+"查询该门店需要上传的销售单无数据！");
			return;
		}
		this.Log("循环"+logStart+"查询该门店需要上传的交易数据总条数count="+getSaleDatas.size());
		int uploadCount = 0;
		String namespace  = "http://tempuri.org/";
		String method = "TrasAdd";//webSerice方法名
		//上传格式
		//1344,1172,2202110002,110203,詹记,-0.10,2022-02-11 06:30:00
		//商铺编号,POS机编号,小票号,商品编码,商品名称,交易金额,交易日期
		String saleInfo = "";
		Map<String, Object> headers=new HashMap<String, Object>();
		headers.put("Content-Type", "text/xml;charset=UTF-8");
		for (Map<String, Object> par : getSaleDatas)
		{
			try
			{
				StringBuffer sb=new StringBuffer();
				String saleNo = par.get("SALENO").toString();
				String saleNo_origin = saleNo;
				//String tot_amt = par.getOrDefault("TOT_AMT","0").toString();
				//String tot_amt = par.getOrDefault("SALEAMT","0").toString();
				String tot_amt = par.getOrDefault("TOT_AMT","0").toString();
				if ("1".equals(amountType))
				{
					tot_amt = par.getOrDefault("SALEAMT","0").toString();
				}
				else if ("2".equals(amountType))
				{
					tot_amt = par.getOrDefault("TOT_AMT_MERRECEIVE","0").toString();
				}
				String sdate = par.get("SDATE").toString();
				String stime = par.get("STIME").toString();
				String type = par.get("TYPE").toString();

				String salesType="";
				//数量
				String qty="1";

				BigDecimal amt = new BigDecimal("0");
				BigDecimal old_amt = new BigDecimal(tot_amt);
				BigDecimal uploadScale_b = new BigDecimal(uploadScale);
				amt = old_amt.multiply(uploadScale_b).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);

				//销售单
				if("0".equals(type)){
					salesType="SA";
				}else if("1".equals(type)||"2".equals(type)){
					//R退货（退货金额为正数）
					salesType="SR";
				}

				sb.append(""
						+ "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
				sb.append(" "
						+ "<soap:Body>\n"
						+ "<postsalescreate xmlns=\"http://tempurl.org\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
						+ "<astr_request>\n");
				sb.append(""
						+ "     <header>\n"
						+ "        <username>"+userCode+"</username>"
						+ "        <password>"+passWord+"</password>"
						+ "        <messagetype>"+"SALESDATA"+"</messagetype>"//固定值：SALESDATA
						+ "        <messageid>"+"332"+"</messageid>"//固定值：332
						+ "        <version>"+"V332M"+"</version>"//固定值：V332M
						+ "     </header>\n");



				sb.append(""
						+ "     <salestotal>\n"
						+ "        <localstorecode>"+storeNo+"</localstorecode>"//店铺号
						+ "        <txdate_yyyymmdd>"+sdate+"</txdate_yyyymmdd>"//txdate_yyyymmdd	交易日期	string	否	长度:8 固定格式：YYYYMMDD
						+ "        <txtime_hhmmss>"+stime+"</txtime_hhmmss>"//txtime_hhmmss	交易时间	string	否	长度:6 固定格式：HHMMSS
						+ "        <mallid>"+marketNo+"</mallid>"//Mallid	商城编号	string	否	长度:2  提供正式地址时，一并提交MALLID
						//可用01或者02表示 如果专柜只有一台收银机就用01表示，如果有两台则第二台用02表示，依次类推
						+ "        <storecode>"+storeNo+"</storecode>"//店铺号
						+ "        <tillid>"+machineNo+"</tillid>"//收银机号
						+ "        <salestype>"+salesType+"</salestype>\n"//单据类型
						+ "        <txdocno>"+saleNo+"</txdocno>"//txdocno	销售单号   长度:30（不超过） 测试用 "TESTTEST202008030904010001"、"TESTTEST202007290904010001"
						//cashier	收银员编号  长度:10
						+ "        <mallitemcode>"+goodsNo+"</mallitemcode>" //货号
						+ "        <cashier>"+storeNo+"</cashier>" //收银员编号，和店铺号保持一致
						+ "        <netqty>"+qty+"</netqty>"//netqty	数量

						+ "        <originalamount>0</originalamount>"//应该是原始金额
						+ "        <sellingamount>"+amt+"</sellingamount>"//金额
						+ "        <couponqty>0</couponqty>"//
						+ "        <netamount>"+amt+"</netamount>"//netamount	销售净金额	decimal { 4 }	否	当金额是负数时，则被认为是退货
						+ "        <paidamount>"+amt+"</paidamount>"//paidamount
						+ "        <changeamount>0</changeamount>"//

//          <issueby>000000</issueby>
//          <issuedate_yyyymmdd>20190409</issuedate_yyyymmdd>
//          <issuetime_hhmmss>065349</issuetime_hhmmss>

						+ "     </salestotal>\n");

				sb.append(""
						+ "     <salesitems>\n"
				);
				//PLUBARCODE	国际条形码
				//String pluBarCode=goodsMap.get("BPLUBARCODE")==null?"":goodsMap.get("BPLUBARCODE").toString();
				sb.append(""
						+ "     <salesitem>\n"
						+ "        <iscounteritemcode>"+"1"+"</iscounteritemcode>\n"//是否专柜货号
						+ "        <lineno>"+"1"+"</lineno>"//lineno	商品行号	string	否	长度:1
						+ "        <storecode>"+storeNo+"</storecode>"//

						+ "        <mallitemcode>"+goodsNo+"</mallitemcode>"//货号
						+ "        <counteritemcode>"+goodsNo+"</counteritemcode>"//货号
						+ "        <itemcode>"+goodsNo+"</itemcode>"//货号
						+ "        <plucode>"+goodsNo+"</plucode>"//货号
						+ "        <invttype>0</invttype>"//
						+ "        <qty>"+qty+"</qty>"//qty	数量	decimal { 4 }	否	长度:1

						+ "        <exstk2sales>0</exstk2sales>"//
						+ "        <originalprice>0</originalprice>"//
						+ "        <sellingprice>0</sellingprice>"//
						+ "        <vipdiscountpercent>0</vipdiscountpercent>"//
						+ "        <vipdiscountless>0</vipdiscountless>"//
						+ "        <totaldiscountless1>0</totaldiscountless1>"//
						+ "        <totaldiscountless2>0</totaldiscountless2>"//
						+ "        <totaldiscountless>0</totaldiscountless>"//
						+ "        <netamount>"+amt+"</netamount>"//netamount	净金额	decimal { 4 }	否	当金额是负数时，则被认为是退货
						+ "        <bonusearn>0</bonusearn>"//bonusearn	获得的积分	decimal { 4 }	否	固定值：0
						+ "     </salesitem>\n");


				sb.append(""
						+ "     </salesitems>\n"
				);

				sb.append(""
						+ "     <salestenders>\n"
				);

				sb.append(""
						+ "     <salestender>\n"
						+ "        <lineno>"+"1"+"</lineno>"//lineno	商品行号	string	否	长度:1
						+ "        <tendercode>"+payCode+"</tendercode>"
						+ "        <tendertype>0</tendertype>"
						+ "        <tendercategory>0</tendercategory>"
						+ "        <payamount>"+amt+"</payamount>"//payamount	付款金额	decimal { 4 }	否	付款金额
						+ "        <baseamount>"+amt+"</baseamount>"//baseamount	本位币金额	decimal { 4 }	否	同payamount
						+ "        <excessamount>0</excessamount>"//excessamount	找零金额	decimal { 4 }	否	固定为空值
						+ "     </salestender>\n");
				sb.append(""
						+ "     </salestenders>\n"
				);

				sb.append(""
						+ "</astr_request>\n"
						+ "</postsalescreate>\n"
						+ "</soap:Body>\n");
				sb.append("</soap:Envelope>");

				//拼接传入的交易数据
				//商铺编号,POS机编号,小票号,商品编码,商品名称,交易金额,交易日期
				saleInfo = "";
				saleInfo = sb.toString();
				String result="";
				Map<String,Object> soapResultMap=new HashMap<String,Object>();
				try {
					this.Log("调用webService上传接口开始，url=" + serviceUrl + ",传参请求req:" + saleInfo);
					result = OrderPostClient.sendSoapPost("SoapPost", serviceUrl, headers, saleInfo);
					this.Log("调用webService上传接口结束，返回res:" + result);
					if (result != null && !Check.Null(result)) {
						soapResultMap = this.xmlToMap(result);
					}
				} catch (Exception e) {
					this.Log("调用webService上传接口，返回异常:" + e.getMessage());
				}

				String responseCode = soapResultMap.get("responsecode")==null?"":soapResultMap.get("responsecode").toString();
				String responsemessage = soapResultMap.get("responsemessage")==null?"":soapResultMap.get("responsemessage").toString();

				//System.out.println("返回:"+res+"\n");
				if ("0".equals(responseCode))
				{
					uploadCount++;
					// values
					Map<String, DataValue> values = new HashMap<String, DataValue>();
					values.put("ISUPLOADED", new DataValue("Y", Types.VARCHAR));
					// condition
					Map<String, DataValue> conditions = new HashMap<String, DataValue>();
					conditions.put("EID", new DataValue(eId, Types.VARCHAR));
					conditions.put("SALENO", new DataValue(saleNo_origin, Types.VARCHAR));
					this.doUpdate("DCP_SALE", values, conditions);
					this.Log("调用webService上传接口成功后，更新dcp_Sale表标识字段ISUPLOADED成功。");
					//region 更新下上传记录日志表
					try
					{
						String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						String updateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						String sql_upload_log = " SELECT * FROM DCP_MARKET_UPLOAD_LOG WHERE EID='"+eId+"' and SHOPID='"+shopId+"' and BDATE='"+sdate+"' ";
						List<Map<String, Object>> getUploadLogDatas = this.doQueryData(sql_upload_log, null);
						if (getUploadLogDatas==null||getUploadLogDatas.isEmpty())
						{
							String[] columns2 = {"EID","SHOPID","BDATE","MARKETTYPE","MARKETNO","MARKETNAME","STORENO","STORENAME","MACHINENO","AMT","CREATEOPID","CREATEOPNAME","CREATETIME"};
							DataValue[] insValue2 = new DataValue[]
									{
											new DataValue(eId, Types.VARCHAR),
											new DataValue(shopId, Types.VARCHAR),//SHOPID
											new DataValue(sdate, Types.VARCHAR),//BDATE
											new DataValue(marketType, Types.VARCHAR),//MARKETTYPE
											new DataValue(marketNo, Types.VARCHAR),//MARKETNO
											new DataValue(marketName, Types.VARCHAR),//MARKETNAME
											new DataValue(storeNo, Types.VARCHAR),//STORENO
											new DataValue(storeName, Types.VARCHAR),//STORENAME
											new DataValue(machineNo, Types.VARCHAR),//MACHINENO
											new DataValue(amt, Types.VARCHAR),//amt
											new DataValue("admin", Types.VARCHAR),//CREATEOPID
											new DataValue("定时job", Types.VARCHAR),//CREATEOPNAME
											new DataValue(lastModiTime, Types.DATE),//	CREATETIME

									};
							this.doInsert("DCP_MARKET_UPLOAD_LOG",columns2,insValue2);
						}
						else
						{
							// values
							Map<String, DataValue> values2 = new HashMap<String, DataValue>();
							values2.put("AMT", new DataValue(amt,Types.FLOAT, DataValue.DataExpression.UpdateSelf));
							values2.put("LASTMODITIME", new DataValue(lastModiTime, Types.DATE));
							values2.put("UPDATE_TIME", new DataValue(updateTime, Types.VARCHAR));
							// condition
							Map<String, DataValue> conditions2 = new HashMap<String, DataValue>();
							conditions2.put("EID", new DataValue(eId, Types.VARCHAR));
							conditions2.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
							conditions2.put("BDATE", new DataValue(sdate, Types.VARCHAR));
							this.doUpdate("DCP_MARKET_UPLOAD_LOG", values2, conditions2);

						}
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_LOG表成功！");
					}
					catch ( Exception e)
					{
						this.Log("调用webService接口成功后，更新DCP_MARKET_UPLOAD_LOG表异常:"+e.getMessage());
					}
					//endregion
				}


			}
			catch (Exception e)
			{
				this.Log(logStart+"循环该门店交易数据，准备调用webService接口时，异常："+e.getMessage());
			}
		}
		this.Log("循环"+logStart+"该门店当前上传成功的交易数据总条数count="+uploadCount);

	}

	/**
	 * 按照键值的字典顺序拼接
	 */
	public String getOrderByLexicographic(Map<String, Object> params) {
		List<String> list = new ArrayList<String>();

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			Object value=entry.getValue();
			if(value==null||value.toString().trim().length()<1){
				continue;
			}else{
				list.add(entry.getKey());
			}
		}
		Collections.sort(list);
		StringBuffer sb1=new StringBuffer();
		for(String key:list){
			sb1.append(key).append("=").append(params.get(key)).append("&");
		}
		return sb1.toString();
	}



	//计算签字:将 json 按 key 顺序排列（排除 apiKey、signature 和数值类
	protected String calculateSignature(JsonObject jObject) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		String signature;
		String contents = getJsonValue(jObject);
		signature = DigestUtils.md5Hex(contents);

		return signature;
	}

	protected String getJsonValue(JsonElement jElement)
	{
		StringBuffer value = new StringBuffer("");
		if (jElement.isJsonNull())
		{
           //value = "";

		}
		else if (jElement.isJsonArray())
		{
			JsonArray jArray = jElement.getAsJsonArray();
			for (int i = 0; i < jArray.size(); i++)
			{
				value.append(getJsonValue(jArray.get(i)));
			}
		}
		else if (jElement.isJsonObject())
		{
			JsonObject jObject = jElement.getAsJsonObject();
			Map.Entry<String, JsonElement>[] keys = jObject.entrySet().toArray(new Map.Entry[jObject.entrySet().size()]);
			Arrays.sort(keys, new Comparator<Map.Entry<String, JsonElement>>()
			{
				@Override
				public int compare(Map.Entry<String, JsonElement> entry1, Map.Entry<String, JsonElement> entry2)
				{
					return entry1.getKey().compareTo(entry2.getKey());
				}
			});

			for (Map.Entry<String, JsonElement> key : keys)
			{
				if (key.getKey().equalsIgnoreCase("apiKey") || key.getKey().equalsIgnoreCase("signature") || key.getValue().isJsonNull())
				{
					continue;
				}
				value.append(getJsonValue(key.getValue()));
			}
		}
		else
		{
			/**
			 * signature only calculate string field
			 */
			if (jElement.getAsJsonPrimitive().isString())
			{
				value.append(jElement.getAsString());
			}
		}
		return value.toString();
	}


	protected  Map<String, Object> xmlToMap(String xmlStr) throws Exception {
		Map<String,Object> mapEle = new HashMap<String, Object>();
		if(xmlStr==null||xmlStr.length() <= 0){

		}else{
			//解析返回的xml字符串，生成document对象
			Document document = DocumentHelper.parseText(xmlStr);
			//根节点
			Element root = document.getRootElement();
			//子节点
			List<Element> childElements = root.elements();
			//遍历子节点
			mapEle = getAllElements(childElements,mapEle);
		}
		return mapEle;
	}
	/**
	 * 遍历子节点
	 */
	protected  Map<String, Object> getAllElements(List<Element> childElements,Map<String,Object> mapEle) throws Exception {
		for (Element ele : childElements) {
			mapEle.put(ele.getName(), ele.getText());
			if(ele.elements().size()>0){
				mapEle = getAllElements(ele.elements(), mapEle);
			}
		}
		return mapEle;
	}
}
