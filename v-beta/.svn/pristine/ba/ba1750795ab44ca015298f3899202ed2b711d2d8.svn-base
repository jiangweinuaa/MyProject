package com.dsc.spos.service.imp.json;

import java.net.URL;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alibaba.fastjson.asm.Type;
import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderShopStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderShopStatusUpdateRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMELMShopService;
import com.dsc.spos.waimai.WMJBPShopService;
import com.dsc.spos.waimai.WMMTShopService;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.dsc.spos.xml.utils.ParseXml;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderShopStatusUpdate extends SPosAdvanceService<DCP_OrderShopStatusUpdateReq,DCP_OrderShopStatusUpdateRes> 
{

	@Override
	protected void processDUID(DCP_OrderShopStatusUpdateReq req, DCP_OrderShopStatusUpdateRes res) throws Exception {
	// TODO Auto-generated method stub	
	    //取得 SQL
		  String eId = req.getoEId();
			String sql = null;
			String filename ="WaimaiCloseShopLog";
			HelpTools.writelog_fileName("第三方调用OrderShopStatusUpdate接口【开关店】 status(1开店  2闭店):"+req.getStatus()+" 门店Shop:"+req.getoShopId()+" 平台类型docType:"+req.getDocType(),filename);
			//新增了参数，Is_Enable_CloseWaimaiShop默认允许
			String IsEnable ="Y";
			IsEnable = PosPub.getPARA_SMS(this.dao, eId, "", "Is_Enable_CloseWaimaiShop");
			HelpTools.writelog_fileName("第三方调用OrderShopStatusUpdate接口【开关店】 中台参数Is_Enable_CloseWaimaiShop="+IsEnable,filename);
			if(IsEnable==null||IsEnable.isEmpty())
			{
				IsEnable ="Y";
			}
		
			if(IsEnable!=null&&IsEnable.equals("N"))
			{
				res.setSuccess(false);
				res.setDescription("中台参数(Is_Enable_CloseWaimaiShop)设置不允许开关店！");
				return;
			}
			
			//查詢條件
			
			String shopsql="select * from OC_MAPPINGSHOP where SHOPID='"+req.getoShopId()+"' and LOAD_DOCTYPE='"+req.getDocType()+"' ";
			List<Map<String, Object>> listshop=this.doQueryData(shopsql, null);
			String ORDERSHOPNO="";
			if(!req.getDocType().equals("4"))
			{
				if(listshop!=null&&!listshop.isEmpty())
				{
				    ORDERSHOPNO=listshop.get(0).get("ORDERSHOPNO").toString();
					if(ORDERSHOPNO!=null&&!ORDERSHOPNO.isEmpty())
					{
					}
					else
					{
						res.setSuccess(false);
						res.setDescription("当前门店不存平台映射关系");
						return;
					}
				}
				else
				{
					res.setSuccess(false);
					res.setDescription("当前门店不存平台映射关系");
					return;
				}
			}
			
			String resstatus="1";
			if(req.getStatus().equals("1"))
			{
				//开店操作
				if(req.getDocType().equals("1"))
				{
					Map<String, Object> mapAppkey = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, req.getoEId(), req.getoShopId(), "1","");
					Boolean isGoNewFunction = false;//是否走新的接口
					String elmAPPKey = "";
					String elmAPPSecret = "";
					String elmAPPName = "";			
					boolean elmIsSandbox = false;
					if (mapAppkey != null)
					{
						elmAPPKey = mapAppkey.get("APPKEY").toString();
						elmAPPSecret = mapAppkey.get("APPSECRET").toString();
						elmAPPName = mapAppkey.get("APPNAME").toString();
						String	elmIsTest = mapAppkey.get("ISTEST").toString();					
						if (elmIsTest != null && elmIsTest.equals("Y"))
						{
							elmIsSandbox = true;
						}
						isGoNewFunction = true;
					}
					boolean issucess= false;
					
					StringBuilder errorMessage=new StringBuilder();
					if(isGoNewFunction)
					{
						issucess= WMELMShopService.updateShopStatus(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,Long.parseLong(ORDERSHOPNO), 1, errorMessage);
					}
					else
					{
						issucess= WMELMShopService.updateShopStatus(Long.parseLong(ORDERSHOPNO), 1, errorMessage);
					}
					
					if(issucess==true)
					{
						//开店成功,更新下OC_MAPPINGSHOP表的状态
						res.setOrderStatus("1");
						
						UptBean up1=new UptBean("OC_MAPPINGSHOP");
						up1.addUpdateValue("ISSALETYPE", new DataValue("1", Types.VARCHAR));
						
						up1.addCondition("SHOPID", new DataValue(req.getoShopId(), Types.VARCHAR));
						up1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
						this.pData.add(new DataProcessBean(up1));
						
					}
					else
					{
						res.setSuccess(false);
						res.setDescription(errorMessage.toString());
						return;
					}
				}
			  //开店操作
				if(req.getDocType().equals("2"))
				{		
					boolean	issucess = false;
					StringBuilder errorMessage=new StringBuilder();	
					if (StaticInfo.waimaiMTIsJBP != null && StaticInfo.waimaiMTIsJBP.equals("Y"))//聚宝盆
					{
						issucess = WMJBPShopService.setShopOpen(eId, req.getoShopId(), errorMessage);
					}
					else
					{
						issucess =WMMTShopService.setShopOpen(ORDERSHOPNO, errorMessage);
					}
					
					
					
					if(issucess==true)
					{
						res.setOrderStatus("1");
						//开店成功,更新下OC_MAPPINGSHOP表的状态
						UptBean up1=new UptBean("OC_MAPPINGSHOP");
						up1.addUpdateValue("ISSALETYPE", new DataValue("1", Types.VARCHAR));
						
						up1.addCondition("SHOPID", new DataValue(req.getoShopId(), Types.VARCHAR));
						up1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
						this.pData.add(new DataProcessBean(up1));
					}
					else
					{
						res.setSuccess(false);
						res.setDescription(errorMessage.toString());
						return;
					}
				}
				//还有个京东的
			//开店操作
				if(req.getDocType().equals("3"))
				{		
					
					StringBuilder errorMessage=new StringBuilder();			
					boolean	issucess=HelpJDDJHttpUtil.updateStoreStatusInfo4Open(ORDERSHOPNO,req.getoShopId(),"","1", errorMessage);
					
					//boolean	issucess= WMJBPShopService.setShopOpen("99", req.getoShopId(), errorMessage);
					
					if(issucess==true)
					{
						res.setOrderStatus("1");
						//开店成功,更新下OC_MAPPINGSHOP表的状态
						UptBean up1=new UptBean("OC_MAPPINGSHOP");
						up1.addUpdateValue("ISSALETYPE", new DataValue("1", Types.VARCHAR));
						
						up1.addCondition("SHOPID", new DataValue(req.getoShopId(), Types.VARCHAR));
						up1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
						this.pData.add(new DataProcessBean(up1));
					}
					else
					{
						res.setSuccess(false);
						res.setDescription(errorMessage.toString());
						return;
					}
				}
				
				//开店操作
				if(req.getDocType().equals("4"))
				{
					//如果官网的,先查询是否有官网的门店，如果没有手动加一条进去
					String shopdoctype="select * from OC_MAPPINGSHOP where SHOPID='"+req.getoShopId()+"' and EID='"+req.getoEId()+"' and LOAD_DOCTYPE='4' ";
					List<Map<String, Object>> listshopdoc=this.doQueryData(shopdoctype, null);
					if(listshopdoc==null||listshopdoc.isEmpty())
					{
						//插入官网的门店
						//如果官网的,先查询是否有官网的门店，如果没有手动加一条进去
						String shopgw="select * from OC_ECOMMERCE where  EID='"+req.getoEId()+"' and ECPLATFORMNO='6' ";
						List<Map<String, Object>> listshopgw=this.doQueryData(shopgw, null);
						if(listshopgw!=null&&!listshopgw.isEmpty())
						{
							//插入门店
							String[] columns1 = { "EID", "ORGANIZATIONNO", "SHOPID", "LOAD_DOCTYPE", "BUSINESSID", "SHOPNAME",
									"ORDERSHOPNO", "ORDERSHOPNAME", "APPAUTHTOKEN", "MAPPINGSHOPNO", "MAPPINGSHOPINFO","APPKEY","APPSECRET","APPNAME","ISTEST", "STATUS" };
							DataValue[] insValue1 = null;
								
							insValue1 = new DataValue[]{
										new DataValue(req.getoEId(), Types.VARCHAR),
										new DataValue(req.getoShopId(), Types.VARCHAR),//组织编号=门店编号
										new DataValue(req.getoShopId(), Types.VARCHAR),//ERP门店
										new DataValue("4", Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
										new DataValue("2", Types.VARCHAR),//1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
										new DataValue("", Types.VARCHAR),//ERP门店名称
										new DataValue(req.getoShopId(), Types.VARCHAR),//外卖平台门店ID
										new DataValue("", Types.VARCHAR),//外卖平台门店名称
										new DataValue("", Types.VARCHAR),//token 
										new DataValue("", Types.VARCHAR),//缓存里面的key（99_10001）
										new DataValue("", Types.VARCHAR),//缓存里面的value(json格式)		
										new DataValue("", Types.VARCHAR),//
										new DataValue("", Types.VARCHAR),//
										new DataValue("", Types.VARCHAR),//
										new DataValue("", Types.VARCHAR),//	
										new DataValue("100", Types.VARCHAR)	
								};

							InsBean ib1 = new InsBean("OC_MAPPINGSHOP", columns1);
							ib1.addValues(insValue1);
							this.addProcessData(new DataProcessBean(ib1));	
							//WWWWthis.addProcessData(new DataProcessBean(ib1));
						}
					}
					this.doExecuteDataToDB();
					
					StringBuilder errorMessage=new StringBuilder();	
					//调用官网的地址获取开关店状态
					//从味多美官网上查询信息，然后保存到数据库,直接从这里取
					String method="";
					method="salesDeliver";
					JSONObject reqJsonObject=new JSONObject();
					reqJsonObject.put("cmd", "wdmwaimai_change_md_status");
					reqJsonObject.put("channel", "mall");
					JSONArray jsarrArray=new JSONArray();
					JSONObject jsmap=new JSONObject();
					jsmap.put("erp_code", req.getoShopId());
					jsmap.put("status", 1 );
					jsarrArray.put(jsmap);
					reqJsonObject.put("md", jsarrArray);
					
					String resbody=HttpSend.SendWuXiang(method, reqJsonObject.toString(), "http://www.wdmcake.cn/api/erp-wdmwaimai_change_md_status.html");
					JSONObject resJsonObject=new JSONObject(resbody);
					String code= resJsonObject.getString("code");
					String message= resJsonObject.getString("msg");
					String memoStr = message;
					boolean	issucess=false;
					if(code.equals("0"))
					{
						issucess=true;
					}
					else
					{
						issucess=false;
					}
					
					//boolean	issucess=HelpJDDJHttpUtil.updateStoreStatusInfo4Open(ORDERSHOPNO,req.getoShopId(),"","1", errorMessage);
					
					//boolean	issucess= WMJBPShopService.setShopOpen("99", req.getoShopId(), errorMessage);
					
					if(issucess==true)
					{
						res.setOrderStatus("1");
						//开店成功,更新下OC_MAPPINGSHOP表的状态
						UptBean up1=new UptBean("OC_MAPPINGSHOP");
						up1.addUpdateValue("ISSALETYPE", new DataValue("1", Types.VARCHAR));
						
						up1.addCondition("SHOPID", new DataValue(req.getoShopId(), Types.VARCHAR));
						up1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
						this.pData.add(new DataProcessBean(up1));
					}
					else
					{
						res.setSuccess(false);
						res.setDescription(errorMessage.toString());
						return;
					}
				}
				
			}
			else
			{
			//闭店操作
				if(req.getDocType().equals("1"))
				{
					Map<String, Object> mapAppkey = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, req.getoEId(), req.getoShopId(), "1","");
					Boolean isGoNewFunction = false;//是否走新的接口
					String elmAPPKey = "";
					String elmAPPSecret = "";
					String elmAPPName = "";			
					boolean elmIsSandbox = false;
					if (mapAppkey != null)
					{
						elmAPPKey = mapAppkey.get("APPKEY").toString();
						elmAPPSecret = mapAppkey.get("APPSECRET").toString();
						elmAPPName = mapAppkey.get("APPNAME").toString();
						String	elmIsTest = mapAppkey.get("ISTEST").toString();					
						if (elmIsTest != null && elmIsTest.equals("Y"))
						{
							elmIsSandbox = true;
						}
						isGoNewFunction = true;
					}
					boolean issucess= false;
					StringBuilder errorMessage=new StringBuilder();
					
					if(isGoNewFunction)
					{
						issucess= WMELMShopService.updateShopStatus(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,Long.parseLong(ORDERSHOPNO), 0, errorMessage);
					}
					else
					{
						issucess= WMELMShopService.updateShopStatus(Long.parseLong(ORDERSHOPNO), 0, errorMessage);
						
					}
					
					if(issucess==true)
					{
						res.setOrderStatus("2");
						//开店成功,更新下OC_MAPPINGSHOP表的状态
						UptBean up1=new UptBean("OC_MAPPINGSHOP");
						up1.addUpdateValue("ISSALETYPE", new DataValue("2", Types.VARCHAR));
						
						up1.addCondition("SHOPID", new DataValue(req.getoShopId(), Types.VARCHAR));
						up1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
						this.pData.add(new DataProcessBean(up1));
					}
					else
					{
						res.setSuccess(false);
						res.setDescription(errorMessage.toString());
						return;
					}
				}
			  //开店操作
				if(req.getDocType().equals("2"))
				{
					boolean issucess= false;
					StringBuilder errorMessage=new StringBuilder();
					if (StaticInfo.waimaiMTIsJBP != null && StaticInfo.waimaiMTIsJBP.equals("Y"))//聚宝盆
					{
						issucess = WMJBPShopService.setShopClose(eId, req.getoShopId(), errorMessage);
					}
					else
					{
						issucess = WMMTShopService.setShopClose(ORDERSHOPNO, errorMessage);
					}
								
					if(issucess==true)
					{
						res.setOrderStatus("2");
						//开店成功,更新下OC_MAPPINGSHOP表的状态
						UptBean up1=new UptBean("OC_MAPPINGSHOP");
						up1.addUpdateValue("ISSALETYPE", new DataValue("2", Types.VARCHAR));
						
						up1.addCondition("SHOPID", new DataValue(req.getoShopId(), Types.VARCHAR));
						up1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
						this.pData.add(new DataProcessBean(up1));
					}
					else
					{
						res.setSuccess(false);
						res.setDescription(errorMessage.toString());
						return;
					}
				}
				//还有个京东的
			//开店操作
				if(req.getDocType().equals("3"))
				{
					StringBuilder errorMessage=new StringBuilder();
					boolean issucess= HelpJDDJHttpUtil.updateStoreStatusInfo4Open(ORDERSHOPNO,req.getoShopId(),"","2", errorMessage);
					if(issucess==true)
					{
						res.setOrderStatus("2");
						//开店成功,更新下OC_MAPPINGSHOP表的状态
						UptBean up1=new UptBean("OC_MAPPINGSHOP");
						up1.addUpdateValue("ISSALETYPE", new DataValue("2", Types.VARCHAR));
						
						up1.addCondition("SHOPID", new DataValue(req.getoShopId(), Types.VARCHAR));
						up1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
						this.pData.add(new DataProcessBean(up1));
					}
					else
					{
						res.setSuccess(false);
						res.setDescription(errorMessage.toString());
						return;
					}
				}
				
				if(req.getDocType().equals("4"))
				{
					//如果官网的,先查询是否有官网的门店，如果没有手动加一条进去
					String shopdoctype="select * from OC_MAPPINGSHOP where SHOPID='"+req.getoShopId()+"' and EID='"+req.getoEId()+"' and LOAD_DOCTYPE='4' ";
					List<Map<String, Object>> listshopdoc=this.doQueryData(shopdoctype, null);
					if(listshopdoc==null||listshopdoc.isEmpty())
					{
						//插入官网的门店
						//如果官网的,先查询是否有官网的门店，如果没有手动加一条进去
						String shopgw="select * from OC_ECOMMERCE where  EID='"+req.getoEId()+"' and ECPLATFORMNO='6' ";
						List<Map<String, Object>> listshopgw=this.doQueryData(shopgw, null);
						if(listshopgw!=null&&!listshopgw.isEmpty())
						{
							//插入门店
							String[] columns1 = { "EID", "ORGANIZATIONNO", "SHOPID", "LOAD_DOCTYPE", "BUSINESSID", "SHOPNAME",
									"ORDERSHOPNO", "ORDERSHOPNAME", "APPAUTHTOKEN", "MAPPINGSHOPNO", "MAPPINGSHOPINFO","APPKEY","APPSECRET","APPNAME","ISTEST", "STATUS" };
							DataValue[] insValue1 = null;
								
							insValue1 = new DataValue[]{
										new DataValue(req.getoEId(), Types.VARCHAR),
										new DataValue(req.getoShopId(), Types.VARCHAR),//组织编号=门店编号
										new DataValue(req.getoShopId(), Types.VARCHAR),//ERP门店
										new DataValue("4", Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
										new DataValue("2", Types.VARCHAR),//1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
										new DataValue("", Types.VARCHAR),//ERP门店名称
										new DataValue(req.getoShopId(), Types.VARCHAR),//外卖平台门店ID
										new DataValue("", Types.VARCHAR),//外卖平台门店名称
										new DataValue("", Types.VARCHAR),//token 
										new DataValue("", Types.VARCHAR),//缓存里面的key（99_10001）
										new DataValue("", Types.VARCHAR),//缓存里面的value(json格式)		
										new DataValue("", Types.VARCHAR),//
										new DataValue("", Types.VARCHAR),//
										new DataValue("", Types.VARCHAR),//
										new DataValue("", Types.VARCHAR),//	
										new DataValue("100", Types.VARCHAR)	
								};

							InsBean ib1 = new InsBean("OC_MAPPINGSHOP", columns1);
							ib1.addValues(insValue1);
							this.addProcessData(new DataProcessBean(ib1));	
							//WWWWthis.addProcessData(new DataProcessBean(ib1));
						}
						this.doExecuteDataToDB();
					}
					
					StringBuilder errorMessage=new StringBuilder();	
					//调用官网的地址获取开关店状态
					//从味多美官网上查询信息，然后保存到数据库,直接从这里取
					String method="";
					method="salesDeliver";
					JSONObject reqJsonObject=new JSONObject();
					reqJsonObject.put("cmd", "wdmwaimai_change_md_status");
					reqJsonObject.put("channel", "mall");
					JSONArray jsarrArray=new JSONArray();
					JSONObject jsmap=new JSONObject();
					jsmap.put("erp_code", req.getoShopId());
					jsmap.put("status", 0 );
					jsarrArray.put(jsmap);
					reqJsonObject.put("md", jsarrArray);
					
					String resbody=HttpSend.SendWuXiang(method, reqJsonObject.toString(), "http://www.wdmcake.cn/api/erp-wdmwaimai_change_md_status.html");
					JSONObject resJsonObject=new JSONObject(resbody);
					String code= resJsonObject.getString("code");
					String message= resJsonObject.getString("msg");
					String memoStr = message;
					boolean	issucess=false;
					if(code.equals("0"))
					{
						issucess=true;
					}
					else
					{
						issucess=false;
					}
					
					if(issucess==true)
					{
						res.setOrderStatus("2");
						//开店成功,更新下OC_MAPPINGSHOP表的状态
						UptBean up1=new UptBean("OC_MAPPINGSHOP");
						up1.addUpdateValue("ISSALETYPE", new DataValue("2", Types.VARCHAR));
						
						up1.addCondition("SHOPID", new DataValue(req.getoShopId(), Types.VARCHAR));
						up1.addCondition("LOAD_DOCTYPE", new DataValue(req.getDocType(), Types.VARCHAR));
						this.pData.add(new DataProcessBean(up1));
					}
					else
					{
						res.setSuccess(false);
						res.setDescription(errorMessage.toString());
						return;
					}
				}
				
				
			}
			res.setSuccess(true);
			res.setDescription("操作成功！");
			return;
	
	}
	

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderShopStatusUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderShopStatusUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderShopStatusUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderShopStatusUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;	
		//必传值不为空
		String shop_update = req.getoShopId();
		String status = req.getStatus();
		
		if (Check.Null(shop_update)) {
			errCt++;
			errMsg.append("门店编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(status)) {
			errCt++;
			errMsg.append("营业状态不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		
	  return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderShopStatusUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderShopStatusUpdateReq>(){};
	}

	@Override
	protected DCP_OrderShopStatusUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderShopStatusUpdateRes();
	}

	protected String getString(String[] str){
		String str2 = "";
		
		for (String s:str){
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0){
			str2=str2.substring(0,str2.length()-1);
		}
				
		return str2;
	}

}
