package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVVMMappingShopCreateReq;
import com.dsc.spos.json.cust.res.DCP_ISVVMMappingShopCreateRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ec.shangyou;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_ISVWMMappingShopCreate extends SPosAdvanceService<DCP_ISVVMMappingShopCreateReq,DCP_ISVVMMappingShopCreateRes> {

	private String shopLogFileName = "ISV_ShopsSaveLocal";
	@Override
	protected void processDUID(DCP_ISVVMMappingShopCreateReq req, DCP_ISVVMMappingShopCreateRes res) throws Exception
	{
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String redis_key = orderRedisKeyInfo.redisKey_elemeMappingshop;
		String loadDocType = req.getRequest().getLoadDocType();
		String opName = req.getOpName();
		DCP_ISVVMMappingShopCreateRes.levelResponse datas = res.new levelResponse();
		datas.setDatas(new ArrayList<DCP_ISVVMMappingShopCreateRes.level1Elm>());

		if (loadDocType.equals(orderLoadDocType.ELEME)) // 饿了么
		{
			redis_key = orderRedisKeyInfo.redisKey_elemeMappingshop;
		}
        else
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "未知平台类型！");
		}
		try
		{
			for (DCP_ISVVMMappingShopCreateReq.level1Elm level1Elm : req.getRequest().getDatas())
			{
				DCP_ISVVMMappingShopCreateRes.level1Elm oneLv1 = res.new level1Elm();
				String channelId = level1Elm.getChannelId();
				String orderShopNo = level1Elm.getOrderShopNo();
				String orderShopName = level1Elm.getOrderShopName();
				String erpShopNo = level1Elm.getErpShopNo();
				String erpShopName = level1Elm.getErpShopName();
				String appAuthToken = level1Elm.getAppAuthToken();
				String appKey = level1Elm.getAppKey();
				String appSecret = level1Elm.getAppSecret();
				String appName = level1Elm.getAppName();
				String isTest = level1Elm.getIsTest();
				String isJbp = level1Elm.getIsJbp();
				String userId = level1Elm.getUserId();
				
				String openId = eId + "_" + erpShopNo;// 映射的最终编号： 企业编号_门店编号
				StringBuilder errorMessage = new StringBuilder("");
				boolean nRet = true;

				if (erpShopNo == null || erpShopNo.isEmpty())
				{
					erpShopNo = " ";//目前是主键
				}
				
				String sql = "select * from DCP_mappingshop where  load_doctype='" + loadDocType + "' and ordershopno='"
						+ orderShopNo + "'";
				List<Map<String, Object>> getData = this.doQueryData(sql, null);
				if (getData != null && getData.isEmpty() == false)
				{
					String shopno_db = getData.get(0).get("SHOPID").toString();
					userId = getData.get(0).get("USERID").toString();
                    isJbp = getData.get(0).get("ISJBP").toString();
                    if (!"Y".equals(isJbp))
                    {
                        HelpTools.writelog_fileName("【MappingShop门店映射创建】数据库中该门店非服务商模式授权获取，不能在服务商页面操作门店绑定,isJbp=" + isJbp
                                + ",第三方平台门店shopId：" + orderShopNo, shopLogFileName);
                        nRet = false;
                    }
                    else
                    {
                        if (erpShopNo.equals(shopno_db))
                        {
                            HelpTools.writelog_fileName("【MappingShop门店映射创建】数据库中shop与前端传值erpShopNO一致，数据库shop：" + shopno_db
                                    + " 前端传erpShopNO：" + erpShopNo + " 第三方平台门店shopId：" + orderShopNo, shopLogFileName);
                            nRet = false;
                            errorMessage.append("数据库中shopID与前端传值erpShopNo一致，数据库shopId：" + shopno_db+ " 前端传erpShopNo：" + erpShopNo + " 第三方平台门店ordershopId：" + orderShopNo);
                        }
                    }



				} 
				else
				{
					HelpTools.writelog_fileName("【MappingShop门店映射创建】数据库中没有对应的第三方门店ID，第三方门店ordershopId：" + orderShopNo,
							shopLogFileName);
					 nRet = false;
					 errorMessage.append("数据库中没有对应的第三方门店ID，第三方门店ordershopId：" + orderShopNo);
				}			
				try
				{					
					if (nRet)
					{

						HelpTools.writelog_fileName(
								"【MappingShop门店映射创建】第三方门店ordershopNo：" + orderShopNo + " 映射后的门店shopId:" + openId,
								shopLogFileName);
						oneLv1.setOrderShopNo(orderShopNo);
						oneLv1.setOrderShopName(orderShopName);
						oneLv1.setErpShopNo(erpShopNo);
						oneLv1.setErpShopName(erpShopName);
						oneLv1.setResult("Y");
						oneLv1.setDescription("SUCESS");

						// 存缓存
						String hash_value = "";
						JSONObject obj = new JSONObject();
						obj.put("channelId", channelId);
						obj.put("orderShopNo", orderShopNo);
						obj.put("orderShopName", orderShopName);
						obj.put("erpShopNo", erpShopNo);
						obj.put("erpShopName", erpShopName);
						obj.put("appAuthToken", appAuthToken);
						obj.put("eId", eId);
						obj.put("businessId", "2");// 美团聚宝盆才有 默认2代表外卖
						obj.put("appKey", appKey);
						obj.put("appName", appName);
						obj.put("appSecret", appSecret);
						obj.put("isTest", isTest);
						obj.put("isJbp", isJbp);
						obj.put("userId", userId);
						obj.put("mappingShopNo", openId);

						hash_value = obj.toString();

						// 保存到数据库
						SaveDBMappingShop(loadDocType, hash_value);

						if (loadDocType.equals(orderLoadDocType.ELEME)) // 饿了么
						{
							if (HelpTools.elmMappingShopList!=null&&!HelpTools.elmMappingShopList.isEmpty())
							{
								HelpTools.elmMappingShopList.remove(orderShopNo);
							}
							if (HelpTools.elmShopIdConfigList!=null&&!HelpTools.elmShopIdConfigList.isEmpty())
							{
								HelpTools.elmShopIdConfigList.remove(orderShopNo);
							}
						}
					}
					else
					{
						HelpTools.writelog_fileName(
								"【MappingShop门店映射创建】【失败】 第三方门店ordershopId：" + orderShopNo + " 映射后的门店shopId:" + openId,
								shopLogFileName);
						oneLv1.setOrderShopNo(orderShopNo);
						oneLv1.setOrderShopName(orderShopName);
						oneLv1.setErpShopNo(erpShopNo);
						oneLv1.setErpShopName(erpShopName);
						oneLv1.setResult("N");
						oneLv1.setDescription(errorMessage.toString());
					}
					datas.getDatas().add(oneLv1);
				} 
				catch (Exception e)
				{
					oneLv1.setOrderShopNo(orderShopNo);
					oneLv1.setOrderShopName(orderShopName);
					oneLv1.setErpShopNo(erpShopNo);
					oneLv1.setErpShopName(erpShopName);
					oneLv1.setResult("N");
					oneLv1.setDescription(errorMessage.toString());

					datas.getDatas().add(oneLv1);
					continue;

				}

			}

		} 
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
		res.setDatas(datas);
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ISVVMMappingShopCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ISVVMMappingShopCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ISVVMMappingShopCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ISVVMMappingShopCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if (Check.Null(req.getRequest().getLoadDocType())) {
			errCt++;
			errMsg.append("平台类型不可为空值, ");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (DCP_ISVVMMappingShopCreateReq.level1Elm oneData : req.getRequest().getDatas()) {
			if (Check.Null(oneData.getOrderShopNo())) {
				errCt++;
				errMsg.append("外卖平台门店编号不可为空值, ");
				isFail = true;
			}

			if (isFail) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}

		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;

	}

	@Override
	protected TypeToken<DCP_ISVVMMappingShopCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ISVVMMappingShopCreateReq>(){};
	}

	@Override
	protected DCP_ISVVMMappingShopCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ISVVMMappingShopCreateRes();
	}
	
	private void SaveRedisMappingShop(String docType,String redis_key, String hash_key, String hash_value) throws Exception
	{
		try
		{
			HelpTools.writelog_waimai("【开始写缓存MappingShop门店映射创建】" + "redis_key:" + redis_key + " hash_key:" + hash_key
					+ " hash_value:" + hash_value);
			RedisPosPub redis = new RedisPosPub();
			boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
			if (isexistHashkey)
			{

				redis.DeleteHkey(redis_key, hash_key);//
				HelpTools.writelog_fileName("【MappingShop删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:"
						+ hash_key + " hash_value:" + hash_value, shopLogFileName);
			}
			boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
			if (nret)
			{
				HelpTools.writelog_fileName("【MappingShop写缓存】OK" + "redis_key:" + redis_key + " hash_key:" + hash_key
						+ " hash_value:" + hash_value, shopLogFileName);
			} 
			else
			{
				HelpTools.writelog_fileName("【MappingShop写缓存】Error" + "redis_key:" + redis_key + " hash_key:" + hash_key
						+ " hash_value:" + hash_value, shopLogFileName);
			}
			//redis.Close();
		} 
		catch (Exception e)
		{
			HelpTools.writelog_fileName("【MappingShop写缓存】Exception:" + e.getMessage(), shopLogFileName);
		}
	}

	private void SaveDBMappingShop(String docType,String req) throws Exception
	{
		try
		{
			JSONObject obj = new JSONObject(req);
			String eId = obj.get("eId").toString();
			String erpShopNo = obj.get("erpShopNo").toString();
			String erpShopName = obj.get("erpShopName").toString();
			String orderShopNo = obj.get("orderShopNo").toString();
			String orderShopName = obj.get("orderShopName").toString();
			String appAuthToken = obj.get("appAuthToken").toString();
			String businessId = obj.get("businessId").toString();
			String appKey = obj.get("appKey").toString();
			String appName = obj.get("appName").toString();
			String appSecret = obj.get("appSecret").toString();
			String isTest = obj.get("isTest").toString();
			String isJbp = obj.get("isJbp").toString();
			String channelId = obj.get("channelId").toString();

			String loadDocType = docType;// 饿了么是1
			if (eId == null || eId.length() == 0)
			{
				return;
			}

			String mappingShopNo = eId + "_" + erpShopNo;
			String mappingShopInfo = obj.toString();

			if (erpShopName != null && erpShopName.length() > 255)
			{
				erpShopName = erpShopName.substring(0, 254);
			}
			if (orderShopName != null && orderShopName.length() > 255)
			{
				orderShopName = orderShopName.substring(0, 254);
			}
            String update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			UptBean ub1 = new UptBean("DCP_MAPPINGSHOP");
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("LOAD_DOCTYPE", new DataValue(docType, Types.VARCHAR));
			ub1.addCondition("ORDERSHOPNO", new DataValue(orderShopNo, Types.VARCHAR));
			ub1.addCondition("BUSINESSID", new DataValue(businessId, Types.VARCHAR));

			ub1.addUpdateValue("ORGANIZATIONNO", new DataValue(erpShopNo, Types.VARCHAR));
			ub1.addUpdateValue("SHOPID", new DataValue(erpShopNo, Types.VARCHAR));
			ub1.addUpdateValue("SHOPNAME", new DataValue(erpShopName, Types.VARCHAR));
			ub1.addUpdateValue("MAPPINGSHOPNO", new DataValue(mappingShopNo, Types.VARCHAR));
			ub1.addUpdateValue("MAPPINGSHOPINFO", new DataValue(mappingShopInfo, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(update_time, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
			this.doExecuteDataToDB();
			HelpTools.writelog_fileName("【门店映射保存成功】" + " 映射后门店编号mappingShopNO:" + mappingShopNo, shopLogFileName);
		} 
		catch (SQLException e)
		{
			HelpTools.writelog_fileName("【门店映射执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req, shopLogFileName);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			HelpTools.writelog_fileName("【门店映射执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req, shopLogFileName);
		}
	}
	
	
	/**
	 * 线上的门店是否已经存在本地了
	 * @param eId
	 * @param loadDocType
	 * @param orderShopNO
	 * @param businessID
	 * @return
	 * @throws Exception
	 */
	private boolean IsExistOnlineShop(String eId,String loadDocType,String orderShopNO,String businessID) throws Exception
	{
		boolean isFlag = false;
		String sql = " select * from DCP_MAPPINGSHOP where EID='"+eId+"'";
		sql += " and LOAD_DOCTYPE='"+loadDocType+"' and ORDERSHOPNO='"+orderShopNO+"'";
		if (businessID != null && businessID.isEmpty() == false)
		{
			sql += " and BUSINESSID='"+businessID+"'";
		}
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);	
		if(getQData!=null&&getQData.isEmpty()==false)
		{
			isFlag = true;
		}
		
		return isFlag;
		
	}
}
