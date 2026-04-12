package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dsc.spos.waimai.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderMappingShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderMappingShopQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderMappingShopQueryRes.levelResponse;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.youzan.YouZanCallBackService;
import com.dsc.spos.thirdpart.youzan.response.YouZanMultistoreListRes;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.ec.shangyou;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.dsc.spos.waimai.jddj.OStoreInfo;
import com.google.gson.reflect.TypeToken;
import com.sankuai.meituan.waimai.opensdk.vo.PoiParam;

import eleme.openapi.sdk.api.entity.user.OAuthorizedShop;
import eleme.openapi.sdk.api.entity.user.OUser;

public class DCP_OrderMappingShopQuery extends SPosAdvanceService<DCP_OrderMappingShopQueryReq,DCP_OrderMappingShopQueryRes> {

	private String shopLogFileName = "ShopsSaveLocal";
	@Override
	protected void processDUID(DCP_OrderMappingShopQueryReq req, DCP_OrderMappingShopQueryRes res) throws Exception {
	// TODO Auto-generated method stub
		String isOnline = req.getRequest().getIsOnline();
		if(isOnline.equals("N"))//取本地
		{			
			this.GetShopFromDB(req, res);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
			return;
		}
		else
		{
			//先查询在线的
			this.GetOnlineShop(req, res);
			//保存到本地
			this.SaveOnlineShop(req, res);
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
			
		}
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderMappingShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderMappingShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderMappingShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderMappingShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		
		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		
		if(req.getRequest()==null)
	    {
	    	errMsg.append("request不能为空值 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }

        
					
		if(Check.Null(req.getRequest().getLoadDocType()))
		{
			errCt++;
			errMsg.append("平台类型LoadDocType不可为空值, ");
			isFail = true;
		}
		
		if(Check.Null(req.getRequest().getIsOnline()))
		{
			errCt++;
			errMsg.append("请求类型IsOnline不可为空值, ");
			isFail = true;
		}
			
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
    
		return isFail;
	
	}

	@Override
	protected TypeToken<DCP_OrderMappingShopQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderMappingShopQueryReq>(){};
	}

	@Override
	protected DCP_OrderMappingShopQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderMappingShopQueryRes();
	}
	
	/**
	 * 在线获取所有账户下的 所有管辖门店
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private void GetOnlineShop(DCP_OrderMappingShopQueryReq req, DCP_OrderMappingShopQueryRes res) throws Exception
	{
		String docType = req.getRequest().getLoadDocType();
		String eId = req.geteId();
		String langType = req.getLangType();
		// res.setDatas(new
		// ArrayList<DCP_OrderMappingShopQueryRes.level1Elm>());
		levelResponse datas = res.new levelResponse();
		datas.setSignKey("");
		datas.setDeveloperId("");
		datas.setDatas(new ArrayList<DCP_OrderMappingShopQueryRes.level1Elm>());

		if (docType.equals("ELEME")) // 饿了么
		{
			try
			{
				// 查询ERP所有门店信息
				List<WMMappingShopModel> shopList = getErpShop(eId, langType);
				// 查询所有APPKEY
				List<Map<String, Object>> elmAppKeyList = PosPub.getWaimaiAppConfig(this.dao, eId, docType);
				//
				// List<Map<String, Object>> existMappingShops =
				// this.getMappingShopFromDB(docType);
				if (elmAppKeyList == null || elmAppKeyList.isEmpty() == true) // 为空还是走原来的配置文件
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道参数设置不正确！");
				} else
				{
					
					// 获取已经映射完成的门店
					List<WMMappingShopModel> mappingShopModels = getMappingShop(eId, docType, langType);
					
					boolean IsHasOUser = false;// 是否有总店账号
					boolean IsHasOAuthorizedShop = false;// 是否有管辖门店
					int endIndex = elmAppKeyList.size() - 1;
					for (int i = 0; i < elmAppKeyList.size(); i++)
					{
						try
						{
							Map<String, Object> map = elmAppKeyList.get(i);
							String channelId =  map.get("CHANNELID").toString();
							String elmAPPKey = map.get("APIKEY").toString();
							String elmAPPSecret = map.get("APISECRET").toString();
							String elmAPPName = "";//map.get("APPNAME").toString();
							String elmIsTest = map.get("ISTEST").toString();
							boolean elmIsSandbox = false;
							if (elmIsTest != null && elmIsTest.equals("Y"))
							{
								elmIsSandbox = true;
							}

							StringBuilder errorMeassge = new StringBuilder();
							OUser elmUser = WMELMShopService.getUser(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName,
									errorMeassge);
							if (elmUser == null)
							{
								/*
								 * throw new
								 * SPosCodeException(CODE_EXCEPTION_TYPE.E400,
								 * errorMeassge.toString());
								 */
								if (i == endIndex && IsHasOUser == false)
								{
									throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMeassge.toString());
								} else
								{
									continue;
								}

							}
							IsHasOUser = true;
							List<OAuthorizedShop> authorizedShops = elmUser.getAuthorizedShops();
							if (authorizedShops == null || authorizedShops.size() == 0)
							{
								if (i == endIndex && IsHasOAuthorizedShop == false)
								{
									throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该商户账号下授权店铺列表为空！");
								} else
								{
									continue;
								}

							}
							IsHasOAuthorizedShop = true;

							for (OAuthorizedShop oAuthorizedShop : authorizedShops)
							{
								try
								{
									DCP_OrderMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
									oneLv1.setChannelId(channelId);
									oneLv1.setIsTest(elmIsTest);
									oneLv1.setIsJbp("N");
									oneLv1.setAppKey(elmAPPKey);
									oneLv1.setAppSecret(elmAPPSecret);
									oneLv1.setAppName(elmAPPName);
								
									long shopId = oAuthorizedShop.getId();
									String orderShopNo = String.valueOf(shopId);

									String orderShopName = oAuthorizedShop.getName();

									oneLv1.setOrderShopName(orderShopName);
									oneLv1.setOrderShopNo(orderShopNo);
									
									String erpShopNo = "";
									String erpShopName = "";
									String appAuthToken = "";
									String mappingShopNo = "";
									
									HelpTools.writelog_fileName("平台类型LoadDocType:"+docType+"返回的: 平台门店ID："+orderShopNo+" 平台门店名称："+orderShopName, shopLogFileName);
									if(mappingShopModels!=null)
									{
										for (WMMappingShopModel wmMappingShopModel : mappingShopModels)
										{
											try
											{
												if (orderShopNo.equals(wmMappingShopModel.getOrderShopNo()))
												{
													erpShopNo = wmMappingShopModel.getErpShopNo();
													erpShopName = wmMappingShopModel.getErpShopName();
													appAuthToken = wmMappingShopModel.getAppAuthToken();
													mappingShopNo = wmMappingShopModel.getMappingShopNo();
													break;
												}

											} catch (Exception e)
											{
												continue;
											}

										}
									}
									
									
									
									oneLv1.setErpShopNo(erpShopNo);
									oneLv1.setAppAuthToken(appAuthToken);
									oneLv1.setErpShopName(erpShopName);

									datas.getDatas().add(oneLv1);

								} catch (Exception e)
								{

								}

							}

						} catch (Exception e)
						{
							continue;

						}

					}

				}

			} catch (Exception e)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
			}
			
		}

		else if (docType.equals("MEITUAN")) // 美团聚宝盆
		{
			try
			{

				List<Map<String, Object>> elmAppKeyList = PosPub.getWaimaiAppConfig(this.dao, eId, docType);
				if (elmAppKeyList == null || elmAppKeyList.isEmpty() == true) // 为空还是走原来的配置文件
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道参数设置不正确！");
				}
				// 美团 不支持多个
				String channelId =  elmAppKeyList.get(0).get("CHANNELID").toString();
				String mtAPPId = elmAppKeyList.get(0).get("APIKEY").toString();
				String mtAPPSignKey = elmAppKeyList.get(0).get("APISECRET").toString();
				String mtAPPName = "";//elmAppKeyList.get(0).get("APPNAME").toString();
				String mtIsJBP = elmAppKeyList.get(0).get("ISJBP").toString();
				boolean elmIsSandbox = false;

				if (mtIsJBP.equals("Y")) // 聚宝盆
				{
					// 查询所有门店信息
					List<WMMappingShopModel> shopList = getErpShop(eId, langType);
					if (shopList == null || shopList.size() == 0)
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "ERP没有维护门店！");
					}
					// 获取已经映射完成的门店
					List<WMMappingShopModel> mappingShopModels = getMappingShop(eId, docType, langType);

					for (WMMappingShopModel oneData : shopList)
					{
						DCP_OrderMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
						String erpShopNo = oneData.getErpShopNo();
						String erpShopName = oneData.getErpShopName();

						String orderShopNo = "";
						String orderShopName = "";
						String appAuthToken = "";
						String appKey = mtAPPId;
						String appSecret = mtAPPSignKey;
						String appName = mtAPPName;
						String isTest = "N";
						String isJbp = "Y";
						String mappingShopNo = "";
						if (mappingShopModels != null && mappingShopModels.size() > 0)
						{
							for (WMMappingShopModel wmMappingShopModel : mappingShopModels)
							{
								String erpShopNO_mapping = wmMappingShopModel.getErpShopNo();
								if (erpShopNo.equals(erpShopNO_mapping))
								{
									orderShopNo = wmMappingShopModel.getOrderShopNo();
									orderShopName = wmMappingShopModel.getOrderShopName();
									appAuthToken = wmMappingShopModel.getAppAuthToken();
									mappingShopNo = wmMappingShopModel.getMappingShopNo();
									break;
								}
							}
						}
						oneLv1.setChannelId(channelId);
						oneLv1.setOrderShopNo(orderShopNo);
						oneLv1.setOrderShopName(orderShopName);
						oneLv1.setErpShopNo(erpShopNo);
						oneLv1.setAppAuthToken(appAuthToken);
						oneLv1.setErpShopName(erpShopName);
						oneLv1.setAppKey(appKey);
						oneLv1.setAppSecret(appSecret);
						oneLv1.setIsTest(isTest);
						oneLv1.setAppName(appName);
						oneLv1.setIsJbp(isJbp);

						datas.getDatas().add(oneLv1);
					}
				} 
				else // MT
				{
					StringBuilder errorMeassage = new StringBuilder();
					List<String> mtShopIds = WMMTShopService.getShopIds(errorMeassage);
					if (mtShopIds == null || mtShopIds.isEmpty())
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "在线获取门店ID为空！" + errorMeassage.toString());
					}
					errorMeassage = new StringBuilder();
                    int pageSize = 200;//一次最多查询200个
                    int pageCount = mtShopIds.size()/pageSize;
                    pageCount = (mtShopIds.size() % pageSize > 0) ? pageCount + 1 : pageCount;
                    List<PoiParam> mtShopInfos = new ArrayList<>();
                    if (pageCount > 1)
                    {
                        for (int j =1;j<=pageCount;j++)
                        {
                            int startRow = (j-1) * pageSize;//0,200
                            int endRow = startRow +pageSize;//200,400

                            if (endRow > mtShopIds.size()) {
                                endRow = mtShopIds.size();
                            }
                            List<String> mtShopIds_page = new ArrayList<>();
                            for (int k = startRow; k < endRow; k++) {
                                mtShopIds_page.add(mtShopIds.get(k));
                            }
                            List<PoiParam>  mtShopInfos_page = WMMTShopService.getShopIdsInfo(mtShopIds_page, errorMeassage);
                            if (mtShopInfos_page == null || mtShopInfos_page.size()==0)
                            {
                                continue;
                            }
                            if (mtShopInfos == null)
                            {
                                mtShopInfos = new ArrayList<>();;
                            }

                            for (PoiParam poiParam : mtShopInfos_page)
                            {
                                mtShopInfos.add(poiParam);
                            }

                        }

                    }
                    else
                    {
                        // 获取门店详细信息
                        mtShopInfos = WMMTShopService.getShopIdsInfo(mtShopIds, errorMeassage);
                    }

					if (mtShopInfos == null || mtShopInfos.isEmpty())
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
								"在线获取门店详细信息为空！" + errorMeassage.toString());
					}

					// 获取已经映射完成的门店
					List<WMMappingShopModel> mappingShopModels = getMappingShop(eId, docType, langType);
					for (PoiParam mtShop : mtShopInfos)
					{
						try
						{

							DCP_OrderMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();

							String orderShopNo = mtShop.getApp_poi_code();
							String orderShopName = mtShop.getName();
							String appAuthToken = "";
							String appKey = mtAPPId;
							String appSecret = mtAPPSignKey;
							String appName = mtAPPName;
							String isTest = "N";
							String mappingShopNo = "";
							String isJbp = "N";

							String erpShopNo = "";
							String erpShopName = "";
							
							HelpTools.writelog_fileName("平台类型LoadDocType:"+docType+"返回的: 平台门店ID："+orderShopNo+" 平台门店名称："+orderShopName, shopLogFileName);
							if(mappingShopModels!=null)
							{
								for (WMMappingShopModel wmMappingShopModel : mappingShopModels)
								{
									try
									{
										if (orderShopNo.equals(wmMappingShopModel.getOrderShopNo()))
										{
											erpShopNo = wmMappingShopModel.getErpShopNo();
											erpShopName = wmMappingShopModel.getErpShopName();
											appAuthToken = wmMappingShopModel.getAppAuthToken();
											mappingShopNo = wmMappingShopModel.getMappingShopNo();
											break;
										}

									} catch (Exception e)
									{
										continue;
									}

								}
							}
							
							
                            oneLv1.setChannelId(channelId);
							oneLv1.setOrderShopNo(orderShopNo);
							oneLv1.setOrderShopName(orderShopName);
							oneLv1.setErpShopNo(erpShopNo);
							oneLv1.setAppAuthToken(appAuthToken);
							oneLv1.setErpShopName(erpShopName);
							oneLv1.setAppKey(appKey);
							oneLv1.setAppSecret(appSecret);
							oneLv1.setIsTest(isTest);
							oneLv1.setAppName(appName);
							oneLv1.setIsJbp(isJbp);
							// oneLv1.setMappingShopNO(mappingShopNo);

							datas.getDatas().add(oneLv1);

						} catch (Exception e)
						{
							continue;
						}

					}

				}

			} catch (Exception e)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
			}
			

		}
        else if (orderLoadDocType.MTSG.equals(docType))
        {
            try
            {

                List<Map<String, Object>> elmAppKeyList = PosPub.getWaimaiAppConfig(this.dao, eId, docType);
                if (elmAppKeyList == null || elmAppKeyList.isEmpty() == true) // 为空还是走原来的配置文件
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道参数设置不正确！");
                }
                // 美团 不支持多个
                String channelId =  elmAppKeyList.get(0).get("CHANNELID").toString();
                String mtAPPId = elmAppKeyList.get(0).get("APIKEY").toString();
                String mtAPPSignKey = elmAppKeyList.get(0).get("APISECRET").toString();
                String mtAPPName = "";//elmAppKeyList.get(0).get("APPNAME").toString();
                String mtIsJBP = elmAppKeyList.get(0).get("ISJBP").toString();
                boolean elmIsSandbox = false;

                StringBuilder errorMeassage = new StringBuilder();
                List<String> mtShopIds = WMSGShopService.getShopIds(errorMeassage);
                if (mtShopIds == null || mtShopIds.isEmpty())
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "在线获取门店ID为空！" + errorMeassage.toString());
                }
                errorMeassage = new StringBuilder();
                int pageSize = 200;//一次最多查询200个
                int pageCount = mtShopIds.size()/pageSize;
                pageCount = (mtShopIds.size() % pageSize > 0) ? pageCount + 1 : pageCount;
                JSONArray mtShopInfos = new JSONArray();
                if (pageCount > 1)
                {
                    for (int j =1;j<=pageCount;j++)
                    {
                        int startRow = (j-1) * pageSize;//0
                        int endRow = startRow +pageSize;//200

                        if (endRow > mtShopIds.size()) {
                            endRow = mtShopIds.size();
                        }

                        List<String> mtShopIds_page = new ArrayList<>();
                        for (int k=startRow;k<endRow;k++)
                        {
                            mtShopIds_page.add(mtShopIds.get(k));
                        }
                        JSONArray  mtShopInfos_page = WMSGShopService.getShopIdsInfo(mtShopIds_page, errorMeassage);
                        if (mtShopInfos_page == null || mtShopInfos_page.length()==0)
                        {
                            continue;
                        }
                        if (mtShopInfos == null)
                        {
                            mtShopInfos = new JSONArray();
                        }
                        for (int i=0;i<mtShopInfos_page.length();i++)
                        {
                            mtShopInfos.put(mtShopInfos_page.getJSONObject(i));
                        }

                    }

                }
                else
                {
                    // 获取门店详细信息
                    mtShopInfos = WMSGShopService.getShopIdsInfo(mtShopIds, errorMeassage);
                }


                if (mtShopInfos == null || mtShopInfos.length()==0)
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
                            "在线获取门店详细信息为空！" + errorMeassage.toString());
                }

                // 获取已经映射完成的门店
                List<WMMappingShopModel> mappingShopModels = getMappingShop(eId, docType, langType);
                for (int i=0;i<mtShopInfos.length();i++)
                {
                    try
                    {
                       JSONObject mtShop = mtShopInfos.getJSONObject(i);

                        DCP_OrderMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();

                        String orderShopNo = mtShop.optString("app_poi_code");
                        String orderShopName = mtShop.optString("name");
                        String appAuthToken = "";
                        String appKey = mtAPPId;
                        String appSecret = mtAPPSignKey;
                        String appName = mtAPPName;
                        String isTest = "N";
                        String mappingShopNo = "";
                        String isJbp = "N";

                        String erpShopNo = "";
                        String erpShopName = "";

                        HelpTools.writelog_fileName("平台类型LoadDocType:"+docType+"返回的: 平台门店ID："+orderShopNo+" 平台门店名称："+orderShopName, shopLogFileName);
                        if(mappingShopModels!=null)
                        {
                            for (WMMappingShopModel wmMappingShopModel : mappingShopModels)
                            {
                                try
                                {
                                    if (orderShopNo.equals(wmMappingShopModel.getOrderShopNo()))
                                    {
                                        erpShopNo = wmMappingShopModel.getErpShopNo();
                                        erpShopName = wmMappingShopModel.getErpShopName();
                                        appAuthToken = wmMappingShopModel.getAppAuthToken();
                                        mappingShopNo = wmMappingShopModel.getMappingShopNo();
                                        break;
                                    }

                                } catch (Exception e)
                                {
                                    continue;
                                }

                            }
                        }


                        oneLv1.setChannelId(channelId);
                        oneLv1.setOrderShopNo(orderShopNo);
                        oneLv1.setOrderShopName(orderShopName);
                        oneLv1.setErpShopNo(erpShopNo);
                        oneLv1.setAppAuthToken(appAuthToken);
                        oneLv1.setErpShopName(erpShopName);
                        oneLv1.setAppKey(appKey);
                        oneLv1.setAppSecret(appSecret);
                        oneLv1.setIsTest(isTest);
                        oneLv1.setAppName(appName);
                        oneLv1.setIsJbp(isJbp);
                        // oneLv1.setMappingShopNO(mappingShopNo);

                        datas.getDatas().add(oneLv1);

                    } catch (Exception e)
                    {
                        continue;
                    }

                }

            } catch (Exception e)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
            }

        }
        else if (orderLoadDocType.DYWM.equals(docType))
        {
            try
            {
                List<Map<String, Object>> elmAppKeyList = PosPub.getWaimaiAppConfig(this.dao, eId, docType);
                if (elmAppKeyList == null || elmAppKeyList.isEmpty() == true) // 为空还是走原来的配置文件
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道参数设置不正确！");
                }
				// 获取已经映射完成的门店
				List<WMMappingShopModel> mappingShopModels = getMappingShop(eId, docType, langType);
                //循环
				boolean IsHasOUser = false;// 是否成功过
				boolean IsHasOAuthorizedShop = false;// 是否有管辖门店
                int endIndex = elmAppKeyList.size() - 1;
                for (int i = 0; i < elmAppKeyList.size(); i++)
                {
                    try
                    {
                        Map<String, Object> map = elmAppKeyList.get(i);
                        String channelId =  map.get("CHANNELID").toString();
                        String elmAPPKey = map.get("APIKEY").toString();
                        String elmAPPSecret = map.get("APISECRET").toString();
                        String elmAPPName = "";//map.get("APPNAME").toString();
                        String account_id = map.get("BRANDID").toString();
                        String elmIsTest = map.get("ISTEST").toString();
                        boolean elmIsSandbox = false;
                        if (elmIsTest != null && elmIsTest.equals("Y"))
                        {
                            elmIsSandbox = true;
                        }
                        HelpTools.writelog_fileName("循环【抖音应用】【开始】，应用key="+elmAPPKey,shopLogFileName);
                        if (account_id==null||account_id.trim().isEmpty())
                        {
                            HelpTools.writelog_fileName("循环【抖音应用】【结束】，应用key="+elmAPPKey+",对应的商家账户ID为空，无效配置！",shopLogFileName);
                            continue;
                        }
                        int page = 1;
                        int pageSize = 100;
                        StringBuilder errorMeassge = new StringBuilder();
                        String nResult = WMDYShopService.getShopList(elmIsSandbox,elmAPPKey,elmAPPSecret,account_id,page,pageSize,errorMeassge);
                        HelpTools.writelog_fileName("循环【抖音应用】应用key="+elmAPPKey+",分页查询，第"+page+"页，返回res:"+nResult,shopLogFileName);
                        if (nResult==null||nResult.isEmpty())
                        {
                            continue;
                        }
                        org.json.JSONObject resJson = new org.json.JSONObject(nResult);
                        org.json.JSONObject extraObj = resJson.getJSONObject("extra");
                        String error_code = extraObj.get("error_code").toString();//网关层必须为0，
                        if (!"0".equals(error_code))
                        {
                        	errorMeassge.append("抖音接口返回异常:"+extraObj.optString("description",""));
                        	if (i==endIndex&& IsHasOUser == false)
							{
								throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMeassge.toString());
							}
                        	else
							{
								continue;
							}

                        }
						IsHasOUser = true;
                        org.json.JSONObject dataObj = resJson.getJSONObject("data");
                        String total = dataObj.optString("total");
                        JSONArray pois = dataObj.getJSONArray("pois");
                        if (pois==null||pois.length()==0)
                        {
							if (i == endIndex && IsHasOAuthorizedShop == false)
							{
								throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该商户账号下店铺列表为空！");
							}
							else
							{
								continue;
							}

                        }
						IsHasOAuthorizedShop = true;
                        //添加门店
						for (int j = 0; j < pois.length(); j++)
						{
							try
							{
								org.json.JSONObject poisJSONObj = pois.getJSONObject(j);
								org.json.JSONObject poiObj = poisJSONObj.getJSONObject("poi");
								//org.json.JSONObject root_account = poisJSONObj.getJSONObject("root_account");
								String orderShopNo = poiObj.optString("poi_id","");
								String orderShopName = poiObj.optString("poi_name","");
								if (orderShopNo.isEmpty())
								{
									continue;
								}
								DCP_OrderMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
								oneLv1.setChannelId(channelId);
								oneLv1.setIsTest(elmIsTest);
								oneLv1.setIsJbp("N");
								oneLv1.setAppKey(elmAPPKey);
								oneLv1.setAppSecret(elmAPPSecret);
								oneLv1.setAppName(elmAPPName);
								oneLv1.setUserId(account_id);
								oneLv1.setOrderShopNo(orderShopNo);
								oneLv1.setOrderShopName(orderShopName);


								String erpShopNo = "";
								String erpShopName = "";
								String appAuthToken = "";
								String mappingShopNo = "";

								HelpTools.writelog_fileName("平台类型LoadDocType:"+docType+"返回的: 平台门店ID："+orderShopNo+" 平台门店名称："+orderShopName, shopLogFileName);
								if(mappingShopModels!=null)
								{
									for (WMMappingShopModel wmMappingShopModel : mappingShopModels)
									{
										try
										{
											if (orderShopNo.equals(wmMappingShopModel.getOrderShopNo()))
											{
												erpShopNo = wmMappingShopModel.getErpShopNo();
												erpShopName = wmMappingShopModel.getErpShopName();
												appAuthToken = wmMappingShopModel.getAppAuthToken();
												mappingShopNo = wmMappingShopModel.getMappingShopNo();
												break;
											}

										} catch (Exception e)
										{
											continue;
										}

									}
								}

								oneLv1.setErpShopNo(erpShopNo);
								oneLv1.setAppAuthToken(appAuthToken);
								oneLv1.setErpShopName(erpShopName);
								datas.getDatas().add(oneLv1);
							}
							catch (Exception e)
							{
								HelpTools.writelog_fileName("解析poi节点，异常:"+e.getMessage(),shopLogFileName);
							}

						}
						HelpTools.writelog_fileName("循环【抖音应用】应用key="+elmAPPKey+",分页查询，第"+page+"页，解析结束",shopLogFileName);
                        int totalCount = Integer.parseInt(total);
                        int pageCount = totalCount/pageSize;
                        pageCount = (totalCount % pageSize > 0) ? pageCount + 1 : pageCount;
                        if (pageCount>=2)
						{
							for (int k =2;k<=pageCount;k++)
							{
								try
								{
									errorMeassge = new StringBuilder();
									nResult = "";
									error_code = "";
									nResult = WMDYShopService.getShopList(elmIsSandbox,elmAPPKey,elmAPPSecret,account_id,k,pageSize,errorMeassge);
									HelpTools.writelog_fileName("循环【抖音应用】应用key="+elmAPPKey+",分页查询，第"+k+"页，返回res:"+nResult,shopLogFileName);
									if (nResult==null||nResult.isEmpty())
									{
										continue;
									}
									resJson = new org.json.JSONObject(nResult);
									extraObj = resJson.getJSONObject("extra");
									error_code = extraObj.get("error_code").toString();//网关层必须为0，
									if (!"0".equals(error_code))
									{
										continue;
									}
									dataObj = resJson.getJSONObject("data");
									total = dataObj.optString("total");
									pois = dataObj.getJSONArray("pois");
									if (pois==null||pois.length()==0)
									{
										continue;
									}
									//添加门店
									for (int j = 0; j < pois.length(); j++)
									{
										try
										{
											org.json.JSONObject poisJSONObj = pois.getJSONObject(j);
											org.json.JSONObject poiObj = poisJSONObj.getJSONObject("poi");
											//org.json.JSONObject root_account = poisJSONObj.getJSONObject("root_account");
											String orderShopNo = poiObj.optString("poi_id","");
											String orderShopName = poiObj.optString("poi_name","");
											if (orderShopNo.isEmpty())
											{
												continue;
											}
											DCP_OrderMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
											oneLv1.setChannelId(channelId);
											oneLv1.setIsTest(elmIsTest);
											oneLv1.setIsJbp("N");
											oneLv1.setAppKey(elmAPPKey);
											oneLv1.setAppSecret(elmAPPSecret);
											oneLv1.setAppName(elmAPPName);
											oneLv1.setUserId(account_id);
											oneLv1.setOrderShopNo(orderShopNo);
											oneLv1.setOrderShopName(orderShopName);


											String erpShopNo = "";
											String erpShopName = "";
											String appAuthToken = "";
											String mappingShopNo = "";

											HelpTools.writelog_fileName("平台类型LoadDocType:"+docType+"返回的: 平台门店ID："+orderShopNo+" 平台门店名称："+orderShopName, shopLogFileName);
											if(mappingShopModels!=null)
											{
												for (WMMappingShopModel wmMappingShopModel : mappingShopModels)
												{
													try
													{
														if (orderShopNo.equals(wmMappingShopModel.getOrderShopNo()))
														{
															erpShopNo = wmMappingShopModel.getErpShopNo();
															erpShopName = wmMappingShopModel.getErpShopName();
															appAuthToken = wmMappingShopModel.getAppAuthToken();
															mappingShopNo = wmMappingShopModel.getMappingShopNo();
															break;
														}

													} catch (Exception e)
													{
														continue;
													}

												}
											}

											oneLv1.setErpShopNo(erpShopNo);
											oneLv1.setAppAuthToken(appAuthToken);
											oneLv1.setErpShopName(erpShopName);
											datas.getDatas().add(oneLv1);
										}
										catch (Exception e)
										{
											HelpTools.writelog_fileName("解析poi节点，异常:"+e.getMessage(),shopLogFileName);
										}

									}
									HelpTools.writelog_fileName("循环【抖音应用】应用key="+elmAPPKey+",分页查询，第"+k+"页，解析结束",shopLogFileName);
								}
								catch (Exception e)
								{
									HelpTools.writelog_fileName("循环【抖音应用】应用key="+elmAPPKey+",分页查询，第"+k+"页，异常:"+e.getMessage(),shopLogFileName);
								}

							}

						}
                    }
                    catch (Exception e)
                    {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
                    }

                }
            }
            catch (Exception e)
            {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
            }

        }
		else if (docType.equals("JDDJ"))// 京东到家
		{

			try
			{
				
				List<Map<String, Object>> elmAppKeyList = PosPub.getWaimaiAppConfig(this.dao, eId, docType);
				if (elmAppKeyList == null || elmAppKeyList.isEmpty() == true) // 为空还是走原来的配置文件
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道参数设置不正确！");
				}
				// 美团 不支持多个
				String channelId =  elmAppKeyList.get(0).get("CHANNELID").toString();
				String jddjAPPId = elmAppKeyList.get(0).get("APIKEY").toString();
				String jddjAPPSignKey = elmAppKeyList.get(0).get("APISECRET").toString();
				String jddjAPPToken = elmAppKeyList.get(0).get("TOKEN").toString();
				String jddjAPPName = "";//elmAppKeyList.get(0).get("APPNAME").toString();
				String jddjIsTest = elmAppKeyList.get(0).get("ISTEST").toString();
				String jddjAPPBrandId = elmAppKeyList.get(0).get("BRANDID").toString();
							
				
				StringBuilder error = new StringBuilder();
				List<WMMappingShopModel> mappingShopModels = getMappingShop(eId, docType, langType);
				List<Long> jddjShops = HelpJDDJHttpUtil.GetStationsByVenderId(error);
				if (jddjShops == null || jddjShops.isEmpty())
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, error.toString());
				}
				// region 循环查询获取门店名称，等详细信息
				for (long StoreNo : jddjShops)
				{
					String orderShopNo = StoreNo+"";
					DCP_OrderMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String erpShopNo = "";
					String erpShopName = "";
					String orderShopName = "";
					String appAuthToken = jddjAPPToken;
					String appKey = jddjAPPId;
					String appSecret = jddjAPPSignKey;
					String appName = jddjAPPName;
					String isTest = jddjIsTest;
					String mappingShopNo = "";
					StringBuilder errorMessage = new StringBuilder();
					Thread.sleep(500);
					OStoreInfo jddjOStoreInfo = HelpJDDJHttpUtil.getStoreInfoByStationNo(String.valueOf(StoreNo),
							errorMessage);
					if (jddjOStoreInfo != null)
					{						
						orderShopName = jddjOStoreInfo.getStationName();// 平台门店名称						
					}
					
					for (WMMappingShopModel wmMappingShopModel : mappingShopModels)
					{
						try
						{
							if (orderShopNo.equals(wmMappingShopModel.getOrderShopNo()))
							{
								erpShopNo = wmMappingShopModel.getErpShopNo();
								erpShopName = wmMappingShopModel.getErpShopName();
								appAuthToken = wmMappingShopModel.getAppAuthToken();
								mappingShopNo = wmMappingShopModel.getMappingShopNo();								
								break;
							}

						} catch (Exception e)
						{
							continue;
						}

					}

					oneLv1.setChannelId(channelId);
					oneLv1.setOrderShopNo(String.valueOf(StoreNo));// 平台门店ID
					oneLv1.setOrderShopName(orderShopName);
					oneLv1.setErpShopNo(erpShopNo);
					oneLv1.setErpShopName(erpShopName);
					oneLv1.setAppAuthToken(appAuthToken);
					oneLv1.setAppKey(appKey);
					oneLv1.setAppSecret(appSecret);
					oneLv1.setIsTest(isTest);
					oneLv1.setAppName(appName);
					// oneLv1.setMappingShopNo(mappingShopNo);

					datas.getDatas().add(oneLv1);

				}
				// endregion

			}

			catch (Exception e)
			{

			}

		}
        else if (docType.equals("SYOO"))// 商有云管家
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

            String channelId ="SYOO";

            StringBuilder error = new StringBuilder();
            List<WMMappingShopModel> mappingShopModels = getMappingShop(eId, docType, langType);

            shangyou sy=new shangyou();
            //获取所有商有云管家门店，用于门店映射
            String resbody=sy.getOpenStoreInfoList(apiUrl,authToken,signKey);
            com.alibaba.fastjson.JSONObject resjsobject= com.alibaba.fastjson.JSONObject.parseObject(resbody);
            //
            String errorCode=resjsobject.containsKey("errorCode")?resjsobject.getString("errorCode"):"";//错误代码
            String errorDesc=resjsobject.containsKey("errorMsg")?resjsobject.getString("errorMsg"):"";//错误原因
            //成功000
            if (errorCode.equals("000"))
            {
                com.alibaba.fastjson.JSONObject jsonData=resjsobject.getJSONObject("data");
                com.alibaba.fastjson.JSONArray jsonList=jsonData.getJSONArray("list");

                if (jsonList == null || jsonList.size()==0)
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商有云管家平台没有门店资料！");
                }

                for (int p = 0; p < jsonList.size(); p++)
                {

                    String orderShopNo = jsonList.getJSONObject(p).get("storeId").toString();
                    String orderShopName = jsonList.getJSONObject(p).get("storeName").toString();
                    DCP_OrderMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    String erpShopNo = "";
                    String erpShopName = "";

                    StringBuilder errorMessage = new StringBuilder();

                    if (mappingShopModels != null)
                    {
                        for (WMMappingShopModel wmMappingShopModel : mappingShopModels)
                        {
                            try
                            {
                                if (orderShopNo.equals(wmMappingShopModel.getOrderShopNo()))
                                {
                                    erpShopNo = wmMappingShopModel.getErpShopNo();
                                    erpShopName = wmMappingShopModel.getErpShopName();
                                    break;
                                }

                            } catch (Exception e)
                            {
                                continue;
                            }

                        }
                    }

                    oneLv1.setChannelId(channelId);
                    oneLv1.setOrderShopNo(orderShopNo);// 平台门店ID
                    oneLv1.setOrderShopName(orderShopName);
                    oneLv1.setErpShopNo(erpShopNo);
                    oneLv1.setErpShopName(erpShopName);
                    oneLv1.setAppAuthToken("");
                    oneLv1.setAppKey("");
                    oneLv1.setAppSecret("");
                    oneLv1.setIsTest("N");
                    oneLv1.setAppName("");
                    datas.getDatas().add(oneLv1);
                }
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商有云管家同步失败，"+errorDesc);
            }

        }
        else if (orderLoadDocType.YOUZAN.equals(docType)) //有赞
		{
			try{
				YouZanCallBackService ycb=new YouZanCallBackService();
				List<Map<String, Object>> elmAppKeyList = ycb.getYouZanList(null);
				if (elmAppKeyList == null || elmAppKeyList.isEmpty() == true){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道参数设置不正确！");
				}
				for(Map<String, Object> elmMap:elmAppKeyList){
					YouZanMultistoreListRes thisRes=new YouZanMultistoreListRes();
					thisRes=ycb.offlineSearch(thisRes.getResponseDTO().getData(), elmMap, 1, 100); 
					if(thisRes==null||!"TRUE".equals(thisRes.getSuccess().toUpperCase())||thisRes.getResponseDTO().getData()==null){
						continue;
					}
					
					String channelId =  elmMap.get("CHANNELID")==null?"":elmMap.get("CHANNELID").toString();
					String isTest =  elmMap.get("ISTEST")==null?"":elmMap.get("ISTEST").toString();
					
					String appKey =  elmMap.get("DEAPIKEY")==null?"":elmMap.get("DEAPIKEY").toString();
					
					List<YouZanMultistoreListRes.ResponseDTO.Item> dataList=thisRes.getResponseDTO().getData();
					if(dataList!=null&&dataList.size()>0){
						
						List<WMMappingShopModel> mappingShopModels = getMappingShop(eId, docType, langType);
						String erpShopNo = "";
						String erpShopName = "";
						String appSecret = "";
						String appName = "";
						String appAuthToken = "";
						
						for(YouZanMultistoreListRes.ResponseDTO.Item scData:dataList){
							DCP_OrderMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
							oneLv1.setChannelId(channelId);
							String orderShopNo=scData.getStoreId();// 平台门店ID
							oneLv1.setOrderShopNo(orderShopNo);
							if(mappingShopModels!=null&&mappingShopModels.size()>0){
								List<WMMappingShopModel>  wmMaps1=mappingShopModels.stream().filter(g->g.getOrderShopNo().equals(orderShopNo)).collect(Collectors.toList());
								if(wmMaps1!=null&&wmMaps1.size()==1){
									erpShopNo = wmMaps1.get(0).getErpShopNo();
									erpShopName = wmMaps1.get(0).getErpShopName();
									appAuthToken = wmMaps1.get(0).getAppAuthToken();
								}
							}
							
							oneLv1.setOrderShopName(scData.getStoreName());
							oneLv1.setErpShopNo(erpShopNo);
							oneLv1.setErpShopName(erpShopName);
							oneLv1.setAppAuthToken(appAuthToken);
							oneLv1.setAppKey(appKey);
							oneLv1.setAppSecret(appSecret);
							oneLv1.setIsTest(isTest);
							oneLv1.setAppName(appName);
							// oneLv1.setMappingShopNo(mappingShopNo);

							datas.getDatas().add(oneLv1);
						}
					}
				}
			}
			catch (Exception e){

			}
		}
		else
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道类型:"+docType+"接口未实现！");
		}
		res.setDatas(datas);

	}
	
	/**
	 * 把查询出来的存到本地，本地有的不存，没有的需要存
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private void SaveOnlineShop(DCP_OrderMappingShopQueryReq req, DCP_OrderMappingShopQueryRes res) throws Exception
	{
		String docType = req.getRequest().getLoadDocType();
		String eId = req.geteId();
		
		String businessID = "2";//默认2  1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
		if(res==null||res.getDatas()==null||res.getDatas().getDatas()==null)
		{
			return;
		}
		for(DCP_OrderMappingShopQueryRes.level1Elm level1Elm : res.getDatas().getDatas())
		{			
			try 
			{
				String channelId = level1Elm.getChannelId();
				String orderShopNo = level1Elm.getOrderShopNo();
				String orderShopName = level1Elm.getOrderShopName();
				String erpShopNo = level1Elm.getErpShopNo();
				String erpShopName = level1Elm.getErpShopName();
				String elmAPPKey = level1Elm.getAppKey();
				String elmAPPSecret = level1Elm.getAppSecret();
				String elmAPPName = level1Elm.getAppName();				
				String elmIsTest = level1Elm.getIsTest();
				String appAuthToken = level1Elm.getAppAuthToken();
				String isJbp = level1Elm.getIsJbp();
				if(isJbp==null||isJbp.equals("Y")==false)
				{
					isJbp = "N";
				}
				
				String MAPPINGSHOPNO = eId+"_"+erpShopNo;
				
				
				String MAPPINGSHOPINFO ="";
				try 
				{
					JSONObject obj = new JSONObject();
					obj.put("channelId", channelId);
					obj.put("orderShopNo", orderShopNo);
					obj.put("orderShopName", orderShopName);
					obj.put("erpShopNo", erpShopNo);
					obj.put("erpShopName", erpShopName);
					obj.put("appAuthToken", appAuthToken);
					obj.put("eId", eId);
					obj.put("businessId", businessID);//美团聚宝盆才有 默认2代表外卖
					obj.put("appKey", elmAPPKey);
					obj.put("appName", elmAPPName);
					obj.put("appSecret", elmAPPSecret);
					obj.put("isTest", elmIsTest);
					obj.put("isJbp", isJbp);
					obj.put("mappingShopNo", MAPPINGSHOPNO);
					MAPPINGSHOPINFO = obj.toString();
		
				} 
				catch (Exception e) 
				{
			
		
				}
				
				if (erpShopNo == null || erpShopNo.isEmpty())//目前是主键
				{
					erpShopNo=" ";
				}
				if (erpShopName != null && erpShopName.length() > 255) 
				{
					erpShopName = erpShopName.substring(0, 254);
				}
				if (orderShopName != null && orderShopName.length() > 255) 
				{
					orderShopName = orderShopName.substring(0, 254);
				}
				//查询下有没有，有的话 不插入，没有才插入
				if(this.IsExistOnlineShop(eId, docType, orderShopNo, businessID))
				{
					HelpTools.writelog_fileName("【同步线上门店到本地】本地已经存在了!开始执行Update! 平台类型LoadDocType:"+docType+" 平台门店ID："+orderShopNo+" 绑定的门店erpShopNO:"+erpShopNo, shopLogFileName);
					if(docType.equals("JDDJ")&&orderShopName.trim().length()==0)
					{
						HelpTools.writelog_fileName("【同步线上门店到本地】【京东到家接口频率太多】不更新没有获取到， 平台类型LoadDocType:"+docType+" 平台门店ID："+orderShopNo+" 绑定的门店erpShopNO:"+erpShopNo, shopLogFileName);
						continue;
					}
					UptBean ub1 = new UptBean("DCP_MAPPINGSHOP");
					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub1.addCondition("LOAD_DOCTYPE", new DataValue(docType, Types.VARCHAR));
					ub1.addCondition("ORDERSHOPNO", new DataValue(orderShopNo, Types.VARCHAR));
					ub1.addCondition("BUSINESSID", new DataValue(businessID, Types.VARCHAR));
					
					if(channelId!=null&&channelId.isEmpty()==false)
					{
						ub1.addUpdateValue("CHANNELID", new DataValue(channelId, Types.VARCHAR));
					}
					
					ub1.addUpdateValue("ORGANIZATIONNO", new DataValue(erpShopNo, Types.VARCHAR));
					ub1.addUpdateValue("SHOPID", new DataValue(erpShopNo, Types.VARCHAR));
					ub1.addUpdateValue("SHOPNAME", new DataValue(erpShopName, Types.VARCHAR));
					ub1.addUpdateValue("ORDERSHOPNAME", new DataValue(orderShopName, Types.VARCHAR));
					ub1.addUpdateValue("APPKEY", new DataValue(elmAPPKey, Types.VARCHAR));
					ub1.addUpdateValue("APPSECRET", new DataValue(elmAPPSecret, Types.VARCHAR));
					ub1.addUpdateValue("APPNAME", new DataValue(elmAPPName, Types.VARCHAR));
					ub1.addUpdateValue("ISTEST", new DataValue(elmIsTest, Types.VARCHAR));
					ub1.addUpdateValue("APPAUTHTOKEN", new DataValue(appAuthToken, Types.VARCHAR));
					ub1.addUpdateValue("ISJBP", new DataValue(isJbp, Types.VARCHAR));
					ub1.addUpdateValue("MAPPINGSHOPNO", new DataValue(MAPPINGSHOPNO, Types.VARCHAR));
					ub1.addUpdateValue("MAPPINGSHOPINFO", new DataValue(MAPPINGSHOPINFO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub1));
					HelpTools.writelog_fileName("【同步线上门店到本地】Update更新sql语句， 平台类型LoadDocType:"+docType+" 平台门店ID："+orderShopNo+" 绑定的门店erpShopNO:"+erpShopNo, shopLogFileName);
					continue;
				}
				
				
				String[] columns1 = { "EID", "ORGANIZATIONNO", "SHOPID", "LOAD_DOCTYPE", "BUSINESSID", "SHOPNAME",
						"ORDERSHOPNO", "ORDERSHOPNAME", "APPAUTHTOKEN", "MAPPINGSHOPNO", "MAPPINGSHOPINFO","APPKEY","APPSECRET","APPNAME","ISTEST", "ISJBP","CHANNELID" };
				DataValue[] insValue1 = null;
					
				insValue1 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(erpShopNo, Types.VARCHAR),//组织编号=门店编号
							new DataValue(erpShopNo, Types.VARCHAR),//ERP门店
							new DataValue(docType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
							new DataValue(businessID, Types.VARCHAR),//1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
							new DataValue(erpShopName, Types.VARCHAR),//ERP门店名称
							new DataValue(orderShopNo, Types.VARCHAR),//外卖平台门店ID
							new DataValue(orderShopName, Types.VARCHAR),//外卖平台门店名称
							new DataValue(appAuthToken, Types.VARCHAR),//token 
							new DataValue(MAPPINGSHOPNO, Types.VARCHAR),//缓存里面的key（99_10001）
							new DataValue(MAPPINGSHOPINFO, Types.VARCHAR),//缓存里面的value(json格式)		
							new DataValue(elmAPPKey, Types.VARCHAR),//
							new DataValue(elmAPPSecret, Types.VARCHAR),//
							new DataValue(elmAPPName, Types.VARCHAR),//
							new DataValue(elmIsTest, Types.VARCHAR),//	
							new DataValue(isJbp, Types.VARCHAR),
							new DataValue(channelId, Types.VARCHAR)	
					};

				InsBean ib1 = new InsBean("DCP_MAPPINGSHOP", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));		
				HelpTools.writelog_fileName("【同步线上门店到本地】添加sql语句， 平台类型LoadDocType:"+docType+" 平台门店ID："+orderShopNo, shopLogFileName);
	    } 
			catch (Exception e) 
			{
				continue;			
	    }
																						
		}
		
		try 
		{
			HelpTools.writelog_fileName("【同步线上门店到本地】开始执行语句! 平台类型LoadDocType:"+docType, shopLogFileName);
			this.doExecuteDataToDB();
			HelpTools.writelog_fileName("【同步线上门店到本地】开始执行语句! 成功！ 平台类型LoadDocType:"+docType, shopLogFileName);
			return;
	
	  } 
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步线上门店到本地】开始执行语句! 异常："+e.getMessage()+" 平台类型LoadDocType:"+docType+"'", shopLogFileName);
			return;
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
		StringBuffer sb = new StringBuffer(" select * from DCP_MAPPINGSHOP where EID='"+eId+"'");
		sb.append(" and LOAD_DOCTYPE='"+loadDocType+"' and ORDERSHOPNO='"+orderShopNO+"'");
		if (businessID != null && businessID.isEmpty() == false)
		{
			sb.append( " and BUSINESSID='"+businessID+"'");
		}
		List<Map<String, Object>> getQData = this.doQueryData(sb.toString(), null);
		if(getQData!=null&&getQData.isEmpty()==false)
		{
			isFlag = true;
		}
		
		return isFlag;
		
	}
	
	
	
	
	/**
	 * 直接从本地查询
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private void GetShopFromDB(DCP_OrderMappingShopQueryReq req, DCP_OrderMappingShopQueryRes res) throws Exception
	{
		String eId = req.geteId();
		String loadDocType = req.getRequest().getLoadDocType();
		
		String keyText = req.getRequest().getKeyTxt();	
		
		levelResponse datas = res.new levelResponse();
		datas.setSignKey("");
		datas.setDeveloperId("");
		datas.setDatas(new ArrayList<DCP_OrderMappingShopQueryRes.level1Elm>());

        StringBuffer sb =new StringBuffer( " select * from (");
        sb.append( "select A.* from DCP_MAPPINGSHOP A ");
        sb.append("  WHERE A.BUSINESSID='2' and A.ISJBP='N' and A.EID='"+eId+"' and  A.LOAD_DOCTYPE='"+loadDocType+"'");
		if (keyText != null && keyText.isEmpty() == false)
		{
            sb.append(" and (A.SHOPID like '%%"+keyText+"%%' or A.SHOPNAME like '%%"+keyText+"%%' or A.Ordershopno like '%%"+keyText+"%%' or A.Ordershopname like '%%"+keyText+"%%'  or  A.TBMEMO like '%%"+keyText+"%%'  )");
		}

        sb.append(")");
		
		HelpTools.writelog_fileName("【映射查询已经映射过的门店】查询语句："+sb.toString(),shopLogFileName);
		List<Map<String, Object>> getHeader = this.doQueryData(sb.toString(), null);
		if (getHeader != null && getHeader.isEmpty() == false)
		{		
			for (Map<String, Object> map : getHeader) 
			{
				try
				{
					DCP_OrderMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String channelId = map.get("CHANNELID").toString();//渠道编码 对应dcp_ecommerce的channelId
					String appAuthToken = map.get("APPAUTHTOKEN").toString();// 美团绑定的token
					String appKey = map.get("APPKEY").toString();// 饿了么每个门店的应用
					String appName = map.get("APPNAME").toString();// 饿了么每个门店的应用
					String appSecret = map.get("APPSECRET").toString();// 饿了么每个门店的应用
					String isTest = map.get("ISTEST").toString();// 是否测试环境
					String erpShopNo = map.get("SHOPID").toString();// ERP门店
					String erpShopName = map.get("SHOPNAME").toString();// ERP门店名称
					String orderShopNo = map.get("ORDERSHOPNO").toString();// 平台门店ID
					String orderShopName = map.get("ORDERSHOPNAME").toString();// 平台门店名称
					String isJbp = map.get("ISJBP").toString();// 是否聚宝盆
                    String userId = map.getOrDefault("USERID","").toString();// 是否聚宝盆
					
					oneLv1.setChannelId(channelId);
					oneLv1.setAppAuthToken(appAuthToken);
					oneLv1.setAppKey(appKey);
					oneLv1.setAppName(appName);
					oneLv1.setAppSecret(appSecret);
					oneLv1.setIsTest(isTest);
					oneLv1.setErpShopNo(erpShopNo);
					oneLv1.setErpShopName(erpShopName);
					oneLv1.setOrderShopNo(orderShopNo);
					oneLv1.setOrderShopName(orderShopName);
					oneLv1.setIsJbp(isJbp);
                    oneLv1.setUserId(userId);

					datas.getDatas().add(oneLv1);

				} catch (Exception e)
				{

				}
			}
		}
		res.setDatas(datas);
	}
	
	
	private List<WMMappingShopModel> getErpShop(String eId,String langType) throws Exception
	{
		StringBuffer sb =new StringBuffer( "select * from (");
        sb.append( "SELECT distinct A.ORGANIZATIONNO as SHOPID,B.ORG_NAME AS SHOPNAME FROM DCP_ORG A LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.status='100' AND B.LANG_TYPE='"+langType+"'");
        sb.append( "WHERE A.ORG_FORM='2' AND A.status='100' AND A.EID='"+eId+"'");
        sb.append( ")");
		List<Map<String, Object>> getQData = this.doQueryData(sb.toString(), null);
		List<WMMappingShopModel> shopList = new ArrayList<WMMappingShopModel>();
		if (getQData != null && getQData.isEmpty() == false)
		{
			
			
			for (Map<String, Object> map : getQData) 
			{
				WMMappingShopModel oneLv1 = new WMMappingShopModel();				
				String shopId = map.get("SHOPID").toString();
				String shopName = map.get("SHOPNAME").toString();
				oneLv1.setErpShopNo(shopId);
				oneLv1.setErpShopName(shopName);
				shopList.add(oneLv1);				
		  }
			return shopList;
			
		}
		return null;
	}
	
	private List<WMMappingShopModel> getMappingShop(String eId,String loadDocType,String langType) throws Exception
	{
		StringBuffer sb =new StringBuffer( " select * from (");
        sb.append( "select A.*, B.ORG_NAME from DCP_MAPPINGSHOP A  left join DCP_org_LANG B on A.EID=B.EID AND A.SHOPID=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langType+"' ");
        sb.append("  WHERE A.BUSINESSID='2' and A.EID='"+eId+"' and  A.LOAD_DOCTYPE='"+loadDocType+"' ");
				
		sb.append(")");
		List<Map<String, Object>> getQData = this.doQueryData(sb.toString(), null);
		List<WMMappingShopModel> shopList = new ArrayList<WMMappingShopModel>();
		if (getQData != null && getQData.isEmpty() == false)
		{
			
			
			for (Map<String, Object> map : getQData) 
			{
				WMMappingShopModel oneLv1 = new WMMappingShopModel();				
				String shopId = map.get("SHOPID").toString();
				String shopName = map.get("ORG_NAME").toString();
				String orderShopNo = map.get("ORDERSHOPNO").toString();
				String orderShopName = map.get("ORDERSHOPNAME").toString();
				String appAuthToken = map.get("APPAUTHTOKEN").toString();
				
				oneLv1.setErpShopNo(shopId);
				oneLv1.setErpShopName(shopName);
				oneLv1.setOrderShopNo(orderShopNo);
				oneLv1.setOrderShopName(orderShopName);
				oneLv1.setAppAuthToken(appAuthToken);
				
				shopList.add(oneLv1);				
		  }
			return shopList;
			
		}
		return null;
	}

}
