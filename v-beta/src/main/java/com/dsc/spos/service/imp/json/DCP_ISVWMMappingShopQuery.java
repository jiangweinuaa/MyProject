package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWMMappingShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWMMappingShopQueryRes;
import com.dsc.spos.json.cust.res.DCP_ISVWMMappingShopQueryRes.levelResponse;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMMappingShopModel;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.isv.ISV_HelpTools;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_ISVWMMappingShopQuery extends SPosAdvanceService<DCP_ISVWMMappingShopQueryReq,DCP_ISVWMMappingShopQueryRes> {

	private String shopLogFileName = "ISV_ShopsSaveLocal";
	@Override
	protected void processDUID(DCP_ISVWMMappingShopQueryReq req, DCP_ISVWMMappingShopQueryRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
        String loadDocType = req.getRequest().getLoadDocType();
        //先查询有没有，客户唯一标识 这个不区分 企业ID
        String clientNoSql =" SELECT * from DCP_ISVWM_CLIENT WHERE eid = '"+eId+"'";
        List<Map<String, Object>> getQDatas = this.doQueryData(clientNoSql, null);
        if (getQDatas==null||getQDatas.isEmpty())
        {
            //没有的话，需要进行应用申请
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请联系管理员先申请应用(外卖服务商模式)！");
        }
        Map<String,Object> map = getQDatas.get(0);
        String clientNo = map.get("CLIENTNO").toString();
        if (Check.Null(clientNo))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "客户应用标识为空！");
        }
        String status = map.get("STATUS").toString();//(0-待审核，100-审核通过，-1-审核失败)
        if (!"100".equals(status))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "应用未审核，无法绑定门店！");
        }

        String WaiMai_MT_ISV = map.get("MEITUAN_REGISTER").toString();;
        String WaiMai_ELM_ISV = map.get("ELEME_REGISTER").toString();;
        StringBuffer errMsg = new StringBuffer("");
        boolean nRet = false;

        if (orderLoadDocType.MEITUAN.equals(loadDocType))
        {
            if (!"Y".equals(WaiMai_MT_ISV))
            {
                errMsg.append("该应用未注册美团外卖平台类型,");
                nRet = true;
            }
        }
        else if (orderLoadDocType.ELEME.equals(loadDocType))
        {
            if (!"Y".equals(WaiMai_MT_ISV))
            {
                errMsg.append("该应用未注册饿了么外卖平台类型,");
                nRet = true;
            }
        }
        else
        {
            errMsg.append("未知平台类型,");
            nRet = true;
        }

        if (nRet)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
		String isOnline = req.getRequest().getIsOnline();
		if(isOnline.equals("N"))//取本地
		{			
			this.GetShopFromDB(clientNo,req, res);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
			return;
		}
		else
		{
			if (orderLoadDocType.ELEME.equals(loadDocType))
			{
				this.GetOnlineShop(clientNo,req,res);
				this.SaveOnlineShop(clientNo,req,res);
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "暂不支持！");
			}

		}
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ISVWMMappingShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ISVWMMappingShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ISVWMMappingShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ISVWMMappingShopQueryReq req) throws Exception {
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
	protected TypeToken<DCP_ISVWMMappingShopQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ISVWMMappingShopQueryReq>(){};
	}

	@Override
	protected DCP_ISVWMMappingShopQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ISVWMMappingShopQueryRes();
	}


	/**
	 * 在线同步查询服务端授权商户店铺列表
	 * @param clientNo
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private void GetOnlineShop(String clientNo,DCP_ISVWMMappingShopQueryReq req, DCP_ISVWMMappingShopQueryRes res) throws Exception
	{
		String eId = req.geteId();
		String loadDocType = req.getRequest().getLoadDocType();
		String langType = req.getLangType();
		if (orderLoadDocType.ELEME.equals(loadDocType))
		{
			try
			{
				String isv_Url = "http://eliutong2.digiwin.com.cn/dcpService/DCP/services/invoke";//暂时写死157的3.0
				if (!Check.Null(StaticInfo.microMarkHttpPost)&&StaticInfo.microMarkHttpPost.contains("DCP/services/invoke"))
				{
					isv_Url = StaticInfo.microMarkHttpPost;
				}
				String isvTestUrl = isv_Url.replace("/invoke","");
				if(isvTestUrl.toLowerCase().contains("localhost")||isvTestUrl.contains("127.0.0.1"))
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务商接口地址配置错误:"+isvTestUrl);
				}
				if (!ISV_HelpTools.doGetUrlConnect(isvTestUrl))
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务商接口地址无法访问:"+isvTestUrl);
				}
				JSONObject req_isv = new JSONObject();
				JSONObject request_isv = new JSONObject();
				request_isv.put("clientNo",clientNo);
				request_isv.put("isOnline","Y");
				req_isv.put("serviceId","ISV_WMClientELMShopQuery");
				req_isv.put("request",request_isv);
				String reqStr = req_isv.toString();
				HelpTools.writelog_fileName("平台类型LoadDocType:"+loadDocType+"，在线查询服务端授权商户店铺列表，请求req:"+reqStr, shopLogFileName);
				String resStr= HttpSend.doPost(isv_Url,reqStr,null,"");
				HelpTools.writelog_fileName("平台类型LoadDocType:"+loadDocType+"，在线查询服务端授权商户店铺列表，返回res:"+reqStr, shopLogFileName);
				ParseJson pj  = new ParseJson();
				DCP_ISVWMMappingShopQueryRes res_isv = pj.jsonToBean(resStr,new TypeToken<DCP_ISVWMMappingShopQueryRes>(){});
				res.setSuccess(res_isv.isSuccess());
				res.setServiceStatus(res_isv.getServiceStatus());
				res.setServiceDescription(res_isv.getServiceDescription());
				res.setDatas(res_isv.getDatas());
				if(res==null||res.getDatas()==null||res.getDatas().getDatas()==null)
				{
					return;
				}
				//和本地对比
				// 获取已经映射完成的门店
				List<WMMappingShopModel> mappingShopModels = getMappingShop(eId, loadDocType, langType);
				for(DCP_ISVWMMappingShopQueryRes.level1Elm level1Elm : res.getDatas().getDatas())
				{
					String orderShopNo = level1Elm.getOrderShopNo();
					String orderShopName = level1Elm.getOrderShopName();
					String userId = level1Elm.getUserId();

					try
					{
						String erpShopNo = "";
						String erpShopName = "";
						//String appAuthToken = "";
						String mappingShopNo = "";

						HelpTools.writelog_fileName("平台类型LoadDocType:"+loadDocType+"返回的: 平台门店ID："+orderShopNo+"，平台门店名称："+orderShopName+"，授权商户userId:"+userId, shopLogFileName);
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
										//appAuthToken = wmMappingShopModel.getAppAuthToken();
										//mappingShopNo = wmMappingShopModel.getMappingShopNo();
										break;
									}

								} catch (Exception e)
								{
									continue;
								}

							}
						}
						level1Elm.setErpShopNo(erpShopNo);
						//level1Elm.setAppAuthToken(appAuthToken);
						level1Elm.setErpShopName(erpShopName);

					}
					catch (Exception e)
					{

					}

				}

			} catch (Exception e)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
			}
		}
		else
		{

		}
	}

	/**
	 * 把查询出来的存到本地，本地有的更新，没有的需要存
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private void SaveOnlineShop(String clientNo,DCP_ISVWMMappingShopQueryReq req, DCP_ISVWMMappingShopQueryRes res) throws Exception
	{
		String docType = req.getRequest().getLoadDocType();
		String eId = req.geteId();

		String businessID = "2";//默认2  1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
		if(res==null||res.getDatas()==null||res.getDatas().getDatas()==null)
		{
			return;
		}
		String customerNo = clientNo;
		if (customerNo==null||customerNo.isEmpty())
		{
			customerNo = " ";
		}
		String update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		for(DCP_ISVWMMappingShopQueryRes.level1Elm level1Elm : res.getDatas().getDatas())
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
					isJbp = "Y";
				}
				String userId = level1Elm.getUserId();
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
					obj.put("userId", userId);
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
					HelpTools.writelog_fileName("【同步线上门店到本地】本地已经存在了!开始执行Update! 平台类型LoadDocType:"+docType+",平台门店ID："+orderShopNo+",绑定的门店erpShopNo:"+erpShopNo+",授权商户userId:"+userId, shopLogFileName);

					UptBean ub1 = new UptBean("DCP_MAPPINGSHOP");
					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub1.addCondition("LOAD_DOCTYPE", new DataValue(docType, Types.VARCHAR));
					ub1.addCondition("ORDERSHOPNO", new DataValue(orderShopNo, Types.VARCHAR));
					ub1.addCondition("BUSINESSID", new DataValue(businessID, Types.VARCHAR));

					if(channelId!=null&&channelId.isEmpty()==false)
					{
						ub1.addUpdateValue("CHANNELID", new DataValue(channelId, Types.VARCHAR));
					}

					ub1.addUpdateValue("CUSTOMERNO", new DataValue(customerNo, Types.VARCHAR));
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
					ub1.addUpdateValue("USERID", new DataValue(userId, Types.VARCHAR));
					ub1.addUpdateValue("UPDATE_TIME", new DataValue(update_time, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub1));
					HelpTools.writelog_fileName("【同步线上门店到本地】Update更新sql语句， 平台类型LoadDocType:"+docType+",平台门店ID："+orderShopNo+",绑定的门店erpShopNO:"+erpShopNo+",授权商户userId:"+userId, shopLogFileName);
					continue;
				}


				String[] columns1 = { "EID","CUSTOMERNO", "ORGANIZATIONNO", "SHOPID", "LOAD_DOCTYPE", "BUSINESSID", "SHOPNAME",
						"ORDERSHOPNO", "ORDERSHOPNAME", "APPAUTHTOKEN", "MAPPINGSHOPNO", "MAPPINGSHOPINFO","APPKEY","APPSECRET","APPNAME","ISTEST", "ISJBP","CHANNELID","USERID" };
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(customerNo, Types.VARCHAR),
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
						new DataValue(channelId, Types.VARCHAR),
						new DataValue(userId, Types.VARCHAR)
				};

				InsBean ib1 = new InsBean("DCP_MAPPINGSHOP", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));
				HelpTools.writelog_fileName("【同步线上门店到本地】添加sql语句， 平台类型LoadDocType:"+docType+",平台门店ID："+orderShopNo+",授权商户userId:"+userId, shopLogFileName);
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
	private void GetShopFromDB(String clientNo,DCP_ISVWMMappingShopQueryReq req, DCP_ISVWMMappingShopQueryRes res) throws Exception
	{
		String eId = req.geteId();
		String loadDocType = req.getRequest().getLoadDocType();
		
		String keyText = req.getRequest().getKeyTxt();	
		
		levelResponse datas = res.new levelResponse();
		datas.setSignKey("");
		datas.setDeveloperId("");
		datas.setDatas(new ArrayList<DCP_ISVWMMappingShopQueryRes.level1Elm>());
        StringBuffer sb = null;
        if (orderLoadDocType.MEITUAN.equals(loadDocType))
        {
            sb =new StringBuffer( " select * from (");
            sb.append(" select  A.ORGANIZATIONNO ORG_NO,C.ORG_NAME, B.*  from DCP_ORG A ");
            sb.append(" left join DCP_MAPPINGSHOP B ON  B.EID=A.EID AND B.SHOPID=A.ORGANIZATIONNO AND B.BUSINESSID='2' AND B.LOAD_DOCTYPE='"+loadDocType+"'");
            sb.append(" left join Dcp_Org_Lang C on C.EID=A.EID AND C.ORGANIZATIONNO=A.ORGANIZATIONNO AND C.LANG_TYPE='zh_CN'");
            sb.append("  WHERE A.ORG_FORM='2' and A.EID='"+eId+"' and (B.ISJBP='Y' or B.ISJBP is null) ");
            if (keyText != null && keyText.isEmpty() == false)
            {
                sb.append(" and (A.ORGANIZATIONNO like '%%"+keyText+"%%' or C.ORG_NAME like '%%"+keyText+"%%' or B.Ordershopno like '%%"+keyText+"%%' or B.Ordershopname like '%%"+keyText+"%%')");
            }

            sb.append(") order by ORG_NO ");
        }
        else
        {
            sb =new StringBuffer( " select * from (");
            sb.append( "select  A.SHOPID ORG_NO,A.SHOPNAME ORG_NAME, A.* from DCP_MAPPINGSHOP A ");
            sb.append("  WHERE A.BUSINESSID='2' and A.ISJBP='Y' and A.EID='"+eId+"' and  A.LOAD_DOCTYPE='"+loadDocType+"'");
            sb.append(" and A.CUSTOMERNO='"+clientNo+"'");
            if (keyText != null && keyText.isEmpty() == false)
            {
                sb.append(" and (A.SHOPID like '%%"+keyText+"%%' or A.SHOPNAME like '%%"+keyText+"%%' or A.Ordershopno like '%%"+keyText+"%%' or A.Ordershopname like '%%"+keyText+"%%'  or  A.TBMEMO like '%%"+keyText+"%%'  )");
            }

            sb.append(") order by ORDERSHOPNO ");
        }

		
		HelpTools.writelog_fileName("【映射查询已经映射过的门店】查询语句："+sb.toString(),shopLogFileName);
		List<Map<String, Object>> getHeader = this.doQueryData(sb.toString(), null);
		if (getHeader != null && getHeader.isEmpty() == false)
		{		
			for (Map<String, Object> map : getHeader) 
			{
				try
				{
					DCP_ISVWMMappingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String channelId = map.get("CHANNELID").toString();//渠道编码 对应dcp_ecommerce的channelId
					String appAuthToken = map.get("APPAUTHTOKEN").toString();// 美团绑定的token
					String appKey = map.get("APPKEY").toString();// 饿了么每个门店的应用
					String appName = map.get("APPNAME").toString();// 饿了么每个门店的应用
					String appSecret = map.get("APPSECRET").toString();// 饿了么每个门店的应用
					String isTest = map.get("ISTEST").toString();// 是否测试环境
					String erpShopNo = map.get("ORG_NO").toString();// ERP门店
					String erpShopName = map.get("ORG_NAME").toString();// ERP门店名称
					String orderShopNo = map.get("ORDERSHOPNO").toString();// 平台门店ID
					String orderShopName = map.get("ORDERSHOPNAME").toString();// 平台门店名称
					String isJbp = map.get("ISJBP").toString();// 是否聚宝盆
					String mappingShopNo = map.get("MAPPINGSHOPNO").toString();// 美团ePoiId映射的门店名称
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
					oneLv1.setMappingShopNo(mappingShopNo);
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
