package com.dsc.spos.service.imp.json;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.json.cust.req.DCP_OrderECSitePrintQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECSitePrintQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.ec.Shopee;
import com.dsc.spos.utils.logistics.GreenWorld;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderECSitePrintQuery extends SPosBasicService<DCP_OrderECSitePrintQueryReq, DCP_OrderECSitePrintQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECSitePrintQueryReq req) throws Exception 
	{	
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    
	    //必传值不为空
	    String[] ordersn_list = req.getEcOrderNo();
		
	    if(ordersn_list==null || ordersn_list.length<1)
	    {
		   	errMsg.append("订单号不能为空 ");
		   	isFail = true;
		}
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECSitePrintQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECSitePrintQueryReq>(){};
	}

	@Override
	protected DCP_OrderECSitePrintQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECSitePrintQueryRes();
	}

	@Override
	protected DCP_OrderECSitePrintQueryRes processJson(DCP_OrderECSitePrintQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		DCP_OrderECSitePrintQueryRes res = null;
		res = this.getResponse();

		String[] ordersn_list = req.getEcOrderNo();
		String eId = req.geteId();

		String lgPlatformno="";
		String deliverytype="";
		
		//物流SQL
		String sqlOrder="select * from OC_ORDER A where A.EID='"+eId+"' and A.ORDERNO='"+ordersn_list[0]+"'";
		List<Map<String, Object>> getOrderData=this.doQueryData(sqlOrder, null);
		if (getOrderData != null && getOrderData.isEmpty() == false)
		{
			deliverytype=getOrderData.get(0).get("DELIVERYTYPE").toString();//
						
			String AllPayLogisticsID=getOrderData.get(0).get("GREENWORLD_LOGISTICSID").toString();//
			String CVSPaymentNo=getOrderData.get(0).get("DELIVERYNO").toString();//
			String CVSValidationNo=getOrderData.get(0).get("GREENWORLD_VALIDNO").toString();//
			//退貨逆物流
			String rtnCVSValidationNo=getOrderData.get(0).get("GREENWORLD_RTNVALIDNO").toString();//
			String rtnAllPayLogisticsID=getOrderData.get(0).get("GREENWORLD_RTNLOGISTICSID").toString();//
			String rtnCVSPaymentNo=getOrderData.get(0).get("GREENWORLD_RTNORDERNO").toString();//
			
			
			String htmlcode="";
			
			//綠界 返回htmlcode
			if (deliverytype.equals("16")||deliverytype.equals("17") || deliverytype.equals("18") || deliverytype.equals("19")) 
			{				
				//退貨逆物流
				if (rtnCVSPaymentNo.equals("")==false) 
				{
					AllPayLogisticsID=rtnAllPayLogisticsID;
					CVSPaymentNo=rtnCVSPaymentNo;
					CVSValidationNo=rtnCVSValidationNo;
				}
				
				//直接返回失败
				if (AllPayLogisticsID.equals("")) 
				{
					res.setAirway_billsUrl("");
					res.setSuccess(false);
					res.setServiceDescription("尚未取得绿界物流单号，无法打印！");					
				}
				else 
				{
					lgPlatformno="greenworld";
					
					//物流SQL
					String sqlLogistics="select * from OC_LOGISTICS A where A.EID='"+eId+"' and A.LGPLATFORMNO='"+lgPlatformno+"'";
					List<Map<String, Object>> getLogisticsData=this.doQueryData(sqlLogistics, null);
					if (getLogisticsData != null && getLogisticsData.isEmpty() == false)
					{
						String apiUrl=getLogisticsData.get(0).get("API_URL").toString();//
						String cvs_Mode=getLogisticsData.get(0).get("CVS_MODE").toString();
						String greenworld_MerchantId=getLogisticsData.get(0).get("GREENWORLD_MERCHANTID").toString();
						String greenworld_HashKey=getLogisticsData.get(0).get("GREENWORLD_HASHKEY").toString();
						String greenworld_HashIv=getLogisticsData.get(0).get("GREENWORLD_HASHIV").toString();										
						String greenworld_OurServerUrl=getLogisticsData.get(0).get("GREENWORLD_OURSERVERURL").toString();
						String greenworld_OurServerUrlC2C=getLogisticsData.get(0).get("GREENWORLD_OURSERVERURL_CTOC").toString();
						String greenworld_MerchantID_B2C=getLogisticsData.get(0).get("GREENWORLD_MERCHANTID_BTOC").toString();
						String greenworld_Hashkey_B2C=getLogisticsData.get(0).get("GREENWORLD_HASHKEY_BTOC").toString();
						String greenworld_Hashiv_B2C=getLogisticsData.get(0).get("GREENWORLD_HASHIV_BTOC").toString();
						
						GreenWorld greenWorld=new GreenWorld();
						
						if (cvs_Mode.equals("1")) //1：C2C店到店模式 2：B2C大物流中心模式
						{
							if (deliverytype.equals("16"))//綠界7-11 
							{
								htmlcode=greenWorld.C2C_Print711OrderInfo(apiUrl, greenworld_MerchantId, greenworld_HashKey, greenworld_HashIv, AllPayLogisticsID, CVSPaymentNo, CVSValidationNo);
							}
							else if (deliverytype.equals("17"))//綠界全家
							{
								htmlcode=greenWorld.C2C_PrintFamiOrderInfo(apiUrl, greenworld_MerchantId, greenworld_HashKey, greenworld_HashIv, AllPayLogisticsID, CVSPaymentNo);
							}
							else if (deliverytype.equals("18"))//綠界萊而富
							{
								htmlcode=greenWorld.C2C_PrintHilifeOrderInfo(apiUrl, greenworld_MerchantId, greenworld_HashKey, greenworld_HashIv, AllPayLogisticsID, CVSPaymentNo);
							}
							/*
							else if (deliverytype.equals("19"))//綠界OK，暫不支持
							{
								
							}
							*/
							else 
							{
								res.setAirway_billsUrl("");
								res.setSuccess(false);
								res.setServiceDescription("綠界不支持的物流類型：" + deliverytype);
							}				
						}
						else 
						{
							htmlcode=greenWorld.B2C_Home_PrintTradeDocInfo(apiUrl, greenworld_MerchantID_B2C, greenworld_Hashkey_B2C, greenworld_Hashiv_B2C, AllPayLogisticsID);
						}					
						
						//接口返回成功
						if (htmlcode.equals("")) 
						{
							res.setAirway_billsUrl("");
							res.setSuccess(false);
							res.setServiceDescription("綠界托运单打印失败！");
						}
						else 
						{
							res.setAirway_billsUrl(htmlcode);						
							res.setSuccess(true);						
						}
					}
					else 
					{
						res.setAirway_billsUrl("");
						res.setSuccess(false);
						res.setServiceDescription("绿界货运资料未设置！");
					}	
				}			
				
			}
			else 
			{
				//蝦皮處理
				
				if(req.getEcPlatformNo().equals("shopee"))
				{					
					//电商SQL
					String sqlECommerce="select * from OC_ECOMMERCE A where A.EID='"+eId+"' and A.ECPLATFORMNO='"+req.getEcPlatformNo()+"'";
					List<Map<String, Object>> getECommerceData=this.doQueryData(sqlECommerce, null);
					if (getECommerceData != null && getECommerceData.isEmpty() == false)
					{

						String apiUrl=getECommerceData.get(0).get("API_URL").toString();
						int partner_id=Integer.parseInt(getECommerceData.get(0).get("PARTNER_ID").toString());
						int shop_id= Integer.parseInt(getECommerceData.get(0).get("STORE_ID").toString());
						String partner_key=getECommerceData.get(0).get("PARTNER_KEY").toString();

						Shopee shopee = new Shopee();
						String resbody = shopee.GetAirwayBill(apiUrl, partner_id, partner_key, shop_id, ordersn_list);

						JSONObject jsonres = new JSONObject(resbody);
						String request_id=jsonres.getString("request_id");
						String airway_bill = "";

						String error_description="";

						/*
						//1.一订单一张纸
						if(jsonres.has("result"))
						{
							JSONObject result = jsonres.getJSONObject("result");			
							//int total_count=result.getInt("total_count");	

							if (result.has("errors")) 
							{
								//对象==找不到的订单号
								JSONArray errors = result.getJSONArray("errors");			

								for(int a=0;a<errors.length();a++)
								{
									String ordersn=errors.getJSONObject(a).getString("ordersn");
									//System.out.println(ordersn);

									String error_code=errors.getJSONObject(a).getString("error_code");
									//System.out.println(error_code);

									error_description=errors.getJSONObject(a).getString("error_description");
									//System.out.println(error_description);
								}
							}

							//对象==找到的订单号
							JSONArray airway_bills = result.getJSONArray("airway_bills");
							for(int a=0;a<airway_bills.length();a++)
							{
								String ordersn=airway_bills.getJSONObject(a).getString("ordersn");
								airway_bill=airway_bills.getJSONObject(a).getString("airway_bill");
							}	
						}	
						*/
						
						//2.多订单一张纸
						if(jsonres.has("batch_result"))
						{
							JSONObject batch_result = jsonres.getJSONObject("batch_result");
							int btotal_count=batch_result.getInt("total_count");

							JSONArray bairway_bills = batch_result.getJSONArray("airway_bills");
							for(int a=0;a<bairway_bills.length();a++)
							{
								airway_bill=bairway_bills.get(a).toString();
							}
						}	
						

						if(airway_bill.equals(""))
						{
							res.setAirway_billsUrl(airway_bill);
							res.setSuccess(false);
							res.setServiceDescription("蝦皮平台返回錯誤信息：<br/>"+ error_description +"<br/>订单必须已取得物流单号，且物流厂商未验收");
						}
						else
						{
							res.setAirway_billsUrl(airway_bill);
						}
					}
				}
				else 
				{
					res.setAirway_billsUrl("");
					res.setSuccess(false);
					res.setServiceDescription("請使用右上方【打印】按鈕");
				}
			}			
		}
		else 
		{
			res.setAirway_billsUrl("");
			res.setSuccess(false);
			res.setServiceDescription("此訂單號不存在！");
		}		


		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrderECSitePrintQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");


		sql = sqlbuf.toString();
		return sql;
	}

}
