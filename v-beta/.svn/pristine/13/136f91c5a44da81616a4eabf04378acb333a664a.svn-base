package com.dsc.spos.service.imp.json;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderMappingShopCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderMappingShopCreateRes;
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

public class DCP_OrderMappingShopCreate extends SPosAdvanceService<DCP_OrderMappingShopCreateReq,DCP_OrderMappingShopCreateRes> {

	private String shopLogFileName = "ShopsSaveLocal";
	@Override
	protected void processDUID(DCP_OrderMappingShopCreateReq req, DCP_OrderMappingShopCreateRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String redis_key = orderRedisKeyInfo.redisKey_elemeMappingshop;
		String loadDocType = req.getRequest().getLoadDocType();
		String opName = req.getOpName();
		DCP_OrderMappingShopCreateRes.levelResponse datas = res.new levelResponse();
		datas.setDatas(new ArrayList<DCP_OrderMappingShopCreateRes.level1Elm>());
		
		
		if (loadDocType.equals(orderLoadDocType.ELEME)) // 饿了么
		{
			redis_key = orderRedisKeyInfo.redisKey_elemeMappingshop;
		} 
		else if (loadDocType.equals("MEITUAN"))
		{
			redis_key = orderRedisKeyInfo.redisKey_mtMappingshop;
		}
        else if (loadDocType.equals(orderLoadDocType.MTSG))
        {
            redis_key = orderRedisKeyInfo.redisKey_sgmtMappingshop;
        }
        else if (loadDocType.equals(orderLoadDocType.DYWM))
        {
            redis_key = orderRedisKeyInfo.redisKey_dywmMappingshop;
        }
        else if (loadDocType.equals("JDDJ")) // 京东到家
		{
			redis_key = orderRedisKeyInfo.redisKey_jddjMappingshop;
		}
        else if (loadDocType.equals("SYOO")) // 商有云管家
        {
            redis_key = orderRedisKeyInfo.redisKey_syooMappingshop;
        } 
		else if (loadDocType.equals(orderLoadDocType.YOUZAN)) // 有赞
		{
			
		} 
        else
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "未知平台类型！");
		}
		
			
		if (loadDocType.equals("MEITUAN"))
		{
			/*if (StaticInfo.waimaiMTIsJBP == null || StaticInfo.waimaiMTIsJBP.equals("Y"))
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前对接是美团点评聚宝盆开放平台，请选择【美团聚宝盆门店绑定】");
			}*/
		}

		try
		{
			for (DCP_OrderMappingShopCreateReq.level1Elm level1Elm : req.getRequest().getDatas())
			{
				DCP_OrderMappingShopCreateRes.level1Elm oneLv1 = res.new level1Elm();
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
				String userId = "";
				
				String openId = eId + "_" + erpShopNo;// 映射的最终编号： 企业编号_门店编号
				StringBuilder errorMessage = new StringBuilder("");
				boolean nRet = true;

				if (erpShopNo == null || erpShopNo.length() == 0)
				{
					/*HelpTools.writelog_fileName("【MappingShop门店映射创建】前端传值erpShopNO为null或者空格， 前端传erpShopNO：" + erpShopNo
							+ " 第三方平台门店shopId：" + orderShopNo, shopLogFileName);
					continue;*/
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
					if ("Y".equals(isJbp))
					{
						nRet = false;
						errorMessage.append("该门店是通过服务商模式绑定，请到【服务商模式对接】菜单下进行相应操作！" + orderShopNo);
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMessage.toString());
					}

					if (erpShopNo.equals(shopno_db))
					{
						HelpTools.writelog_fileName("【MappingShop门店映射创建】数据库中shop与前端传值erpShopNO一致，数据库shop：" + shopno_db
								+ " 前端传erpShopNO：" + erpShopNo + " 第三方平台门店shopId：" + orderShopNo, shopLogFileName);
						 nRet = false;
						 errorMessage.append("数据库中shopID与前端传值erpShopNo一致，数据库shopId：" + shopno_db+ " 前端传erpShopNo：" + erpShopNo + " 第三方平台门店ordershopId：" + orderShopNo);
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
					    //商有云管家需要调接口，绑定门店
                        boolean bOK=true;
                        if (loadDocType.equals("SYOO"))
                        {
                            String sqlOutSaleset="select * from dcp_outsaleset where status='100' and DELIVERYTYPE='25' ";
                            List<Map<String, Object>> getQData_OutSaleset=this.doQueryData(sqlOutSaleset, null);
                            if (getQData_OutSaleset == null || getQData_OutSaleset.isEmpty() == true)
                            {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "dcp_outsaleset表中没有配置DELIVERYTYPE=25的商有云管家信息！");
                            }

                            String apiUrl=getQData_OutSaleset.get(0).get("APIURL").toString();//http://steward-qa.syoo.cn 测试环境
                            String authToken= getQData_OutSaleset.get(0).get("APPSECRET").toString();
                            String signKey=getQData_OutSaleset.get(0).get("APPSIGNKEY").toString();

                            shangyou sy=new shangyou();
                            String resbody=sy.storeBinding(apiUrl,authToken,signKey,Long.parseLong(orderShopNo),Long.parseLong(erpShopNo));
                            com.alibaba.fastjson.JSONObject resjsobject= com.alibaba.fastjson.JSONObject.parseObject(resbody);
                            //
                            String errorCode=resjsobject.containsKey("errorCode")?resjsobject.getString("errorCode"):"";//错误代码
                            String errorDesc=resjsobject.containsKey("errorMsg")?resjsobject.getString("errorMsg"):"";//错误原因
                            //成功000
                            if (errorCode.equals("000"))
                            {
                                bOK=true;
                            }
                            else
                            {
                                bOK=false;
                            }
                        }
                        if (bOK)
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

                            SaveRedisMappingShop(loadDocType, redis_key, openId, hash_value); // 主键是ERP门店编号
                            SaveRedisMappingShop(loadDocType, redis_key, orderShopNo, hash_value);// 主键是外卖平台门店编号
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
                            else if (loadDocType.equals("MEITUAN"))
                            {
                                redis_key = orderRedisKeyInfo.redisKey_mtMappingshop;
                                if (HelpTools.mtMappingShopList!=null&&!HelpTools.mtMappingShopList.isEmpty())
                                {
                                    HelpTools.mtMappingShopList.remove(orderShopNo);
                                }

                            }
                            else if (loadDocType.equals(orderLoadDocType.MTSG))
                            {
                                redis_key = orderRedisKeyInfo.redisKey_sgmtMappingshop;
                                if (HelpTools.sgmtMappingShopList!=null&&!HelpTools.sgmtMappingShopList.isEmpty())
                                {
                                    HelpTools.sgmtMappingShopList.remove(orderShopNo);
                                }

                            }
                            else if (loadDocType.equals(orderLoadDocType.DYWM))
                            {
                                redis_key = orderRedisKeyInfo.redisKey_dywmMappingshop;
                                if (HelpTools.dyMappingShopList!=null&&!HelpTools.dyMappingShopList.isEmpty())
                                {
                                    HelpTools.dyMappingShopList.remove(orderShopNo);
                                }

                            }
                            else
                            {

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
                            oneLv1.setDescription("调用"+loadDocType+"绑定接口失败！");
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
	protected List<InsBean> prepareInsertData(DCP_OrderMappingShopCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderMappingShopCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderMappingShopCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderMappingShopCreateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
	  StringBuffer errMsg = new StringBuffer("");
	  int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	  if(Check.Null(req.getRequest().getLoadDocType()))
		{
	    errCt++;
	    errMsg.append("平台类型不可为空值, ");
	    isFail = true;
	  }
	  /*if(!Check.Null(req.getDocType()))
	  {
	  	if(req.getDocType().equals("1") == false)
	  	{
	  		errCt++;
		    errMsg.append("目前只支持饿了么（平台类型=1）, ");
		    isFail = true;
	  	}
	  }*/
	  if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	  
	  for (DCP_OrderMappingShopCreateReq.level1Elm oneData : req.getRequest().getDatas()) 
	  {  	
	  	if(Check.Null(oneData.getOrderShopNo()))
	 		{
	 	    errCt++;
	 	    errMsg.append("外卖平台门店编号不可为空值, ");
	 	    isFail = true;
	 	  }
  	  if (isFail){
  			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  		}
		
	  }
	    
	  if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	  return isFail;
	
	}

	@Override
	protected TypeToken<DCP_OrderMappingShopCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderMappingShopCreateReq>(){};
	}

	@Override
	protected DCP_OrderMappingShopCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderMappingShopCreateRes();
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
            String userId = obj.get("userId").toString();

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

			// 映射的门店资料保存到数据库 存在就更新，不存在就插入
			if (this.IsExistOnlineShop(eId, docType, orderShopNo, businessId))
			{
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
				this.addProcessData(new DataProcessBean(ub1));
				this.doExecuteDataToDB();
				HelpTools.writelog_fileName("【门店映射保存成功】" + " 映射后门店编号mappingShopNO:" + mappingShopNo, shopLogFileName);
			}

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
