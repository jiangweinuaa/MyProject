package com.dsc.spos.utils.ec;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;

//电商--虾皮网,
//API_URL:https://partner.shopeemobile.com/api
//有回调地址参考:https://open.shopee.com/documents?module=63&type=2&id=55
//取消订单：第1种是1小时之内的订单买家可直接取消CANCELLED
//第2种是卖家超时未发货，虾皮自动取消订单CANCELLED
//超过1小时的订单买家取消IN_CANCEL，等待卖家同意或拒绝，如果卖家2天后无操作，虾皮自动取消CANCELLED
public class Shopee 
{

	/**
	 * 虾皮商店授权，此方法会返回一个授权页面网址给前端，以完成店铺授权,授权后可以获取到店铺编号
	 * @param partner_id                伙伴ID
	 * @param partner_key               伙伴KEY
	 * @param completedRedirectUrl      授权成功后，跳到指定页面
	 * @return
	 * @throws IOException
	 */

	public String shopAuthPartner(String apiUrl,int partner_id,String partner_key,String completedRedirectUrl) throws IOException
	{		
		String resURL="";
		//测试环境地址https://partner.uat.shopeemobile.com/api/v1/shop/auth_partner
		//正式环境地址https://partner.shopeemobile.com/api/v1/shop/auth_partner

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/shop/auth_partner";
		}
		else			
		{
			apiUrl+="/v1/shop/auth_partner";
		}

		EncryptUtils eu = new EncryptUtils();	

		//产生令牌
		String token=eu.SHA(partner_key+completedRedirectUrl, "SHA-256");
		eu=null;

		//参数url化
		completedRedirectUrl = java.net.URLEncoder.encode(completedRedirectUrl, "utf-8");

		resURL=apiUrl+"?id=" +partner_id +"&token=" +token+"&redirect="+completedRedirectUrl ;

		return resURL;		
	}


	/**
	 * 取消授权
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param completedRedirectUrl
	 * @return
	 * @throws IOException
	 */
	public String shopCancelAuthPartner(String apiUrl,int partner_id,String partner_key,String completedRedirectUrl) throws IOException
	{		
		String resURL="";
		//测试环境地址https://partner.uat.shopeemobile.com/api/v1/shop/cancel_auth_partner
		//正式环境地址https://partner.shopeemobile.com/api/v1/shop/cancel_auth_partner

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/shop/cancel_auth_partner";
		}
		else			
		{
			apiUrl+="/v1/shop/cancel_auth_partner";
		}

		EncryptUtils eu = new EncryptUtils();

		//产生令牌
		String token=eu.SHA(partner_key+completedRedirectUrl, "SHA-256");
		eu=null;

		//参数url化
		completedRedirectUrl = java.net.URLEncoder.encode(completedRedirectUrl, "utf-8");

		resURL=apiUrl+"?id=" +partner_id +"&token=" +token+"&redirect="+completedRedirectUrl ;

		return resURL;		
	}




	/**
	 * 如果有很多笔订单，需要多次调用，每次最大100笔,返回结果中会标明是否还有下一页
	 * @param url
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param pageIndex 默认值：1
	 * @return
	 */
	public String GetOrdersByStatus(String apiUrl,int partner_id,String partner_key,int shop_id,int pageIndex,long create_time_from,long create_time_to)
	{
		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/orders/get";
		}
		else			
		{
			apiUrl+="/v1/orders/get";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("order_status", "READY_TO_SHIP");//订单状态：READY_TO_SHIP付款下单,等待发货	UNPAID	
			header.put("create_time_from", create_time_from);//创建时间开始，开始与结束之间最大15天,非必填			
			header.put("create_time_to", create_time_to);//创建时间结束,非必填			
			header.put("pagination_entries_per_page", 50);//每页条数,最大100,这里设置50是因为查明细最大50			
			header.put("pagination_offset", pageIndex);//页码		
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			
			eu=null;
			
			resbody=HttpSend.SendShopee("GetOrdersByStatus", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{
				boolean more= jsonres.getBoolean("more");//是否还有更多页
				String request_id=jsonres.getString("request_id");//错误追踪ID

				//订单单头列表Object []
				JSONArray orders = jsonres.getJSONArray("orders");
				for(int i=0;i<orders.length();i++)
				{
					String ordersn=orders.getJSONObject(i).getString("ordersn");//订单号
					String order_status=orders.getJSONObject(i).getString("order_status");//订单状态READY_TO_SHIP
					long update_time=orders.getJSONObject(i).getLong("update_time");//更新时间
					//System.out.println(ordersn);
				}
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;
	}

	/**
	 * 查询订单明细商品,最多50笔订单
	 * @param url
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param ordersn_list
	 * @return
	 */
	public String GetOrderDetails(String apiUrl,int partner_id,String partner_key,int shop_id,String[] ordersn_list)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/orders/detail";
		}
		else			
		{
			apiUrl+="/v1/orders/detail";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();

			header.put("ordersn_list", ordersn_list);
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("GetOrderDetails", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//找到的订单
			JSONArray orders = jsonres.getJSONArray("orders");
			for(int i=0;i<orders.length();i++)
			{
				String ordersn=orders.getJSONObject(i).getString("ordersn");
				String country=orders.getJSONObject(i).getString("country");//国家，2码
				String currency=orders.getJSONObject(i).getString("currency");//货币单位，3码
				boolean cod=orders.getJSONObject(i).getBoolean("cod");//是否货到付款

				String tracking_no="";
				if (orders.getJSONObject(i).has("tracking_no")) 
				{
					if (orders.getJSONObject(i).isNull("tracking_no")) 
					{
						tracking_no="";
					}
					else 
					{
						tracking_no=orders.getJSONObject(i).getString("tracking_no");//托运单号
					}
				}

				int days_to_ship=orders.getJSONObject(i).getInt("days_to_ship");//卖家几天发货
				double estimated_shipping_fee=orders.getJSONObject(i).getDouble("estimated_shipping_fee");//预计运费		
				//这个字段是没值的,要到实际发货后才可能有值
				//double actual_shipping_cost=orders.getJSONObject(i).getDouble("actual_shipping_cost");//货运成本
				double total_amount=orders.getJSONObject(i).getDouble("total_amount");//买家实际付款金额
				double escrow_amount=orders.getJSONObject(i).getDouble("escrow_amount");//卖家实际收到金额
				String order_status=orders.getJSONObject(i).getString("order_status");//订单状态
				//黑貓宅急便 (heimao)
				//7-11
				//全家 (FM)
				//Hi-Life
				//OK Mart
				//mingjie
				//中華郵政
				//賣家宅配
				String shipping_carrier=orders.getJSONObject(i).getString("shipping_carrier");//买家选择的物流商
				String payment_method=orders.getJSONObject(i).getString("payment_method");//买家付款方式
				boolean goods_to_declare=orders.getJSONObject(i).getBoolean("goods_to_declare");//海关申报，trackingNo产生后才准确
				String message_to_seller=orders.getJSONObject(i).getString("message_to_seller");//买家留言
				String note=orders.getJSONObject(i).getString("note");//卖家的备注
				long note_update_time=orders.getJSONObject(i).getLong("note_update_time");//卖家备注时间
				long create_time=orders.getJSONObject(i).getLong("create_time");//
				long update_time=orders.getJSONObject(i).getLong("update_time");//
				//这个字段是没值的,要到实际付完款后才可能有值
				//int pay_time=orders.getJSONObject(i).getInt("pay_time");//付款时间
				String dropshipper=orders.getJSONObject(i).getString("dropshipper");//印尼用
				//String credit_card_number=orders.getJSONObject(i).getString("credit_card_number");//信用卡号，后4码
				String buyer_username=orders.getJSONObject(i).getString("buyer_username");//买家用户名
				String dropshipper_phone=orders.getJSONObject(i).getString("dropshipper_phone");//印尼用				

				//收货地址
				JSONObject recipient_address=orders.getJSONObject(i).getJSONObject("recipient_address");
				String name=recipient_address.getString("name");//姓名
				String phone=recipient_address.getString("phone");//电话
				//根据不同的国家选择性栏位
				String town="";
				if(recipient_address.has("town"))
				{
					town=recipient_address.getString("town");//乡镇
				}
				String district="";
				if(recipient_address.has("district"))
				{
					district=recipient_address.getString("district");//区县
				}
				String city="";
				if(recipient_address.has("city"))
				{
					city=recipient_address.getString("city");//城市
				}
				String state="";
				if(recipient_address.has("state"))
				{
					state=recipient_address.getString("state");//省份
				}

				String rcountry="";
				if(recipient_address.has("country"))
				{
					rcountry=recipient_address.getString("country");//国家，2码
				}

				String zipcode="";
				if(recipient_address.has("zipcode"))
				{
					zipcode=recipient_address.getString("zipcode");//邮编
				}

				//全家台中新桂冠店 台中市西屯區四川路67號 店號F012636(如果是全家超商)
				String full_address=recipient_address.getString("full_address");//详细地址

				String deliverytype="";//配送商类型
				String getshop="";
				String getshopName="";
				//
				if (shipping_carrier.contains("全家 ")) 
				{
					deliverytype="8";

					String[] splitFullStrings=full_address.split(" ");
					getshopName=splitFullStrings[0];
					getshop=splitFullStrings[2].substring(2);
					//System.out.println(getshop+" , "+getshopName);

				}
				else if (shipping_carrier.contains("黑貓")) 
				{
					deliverytype="9";
				}
				else if (shipping_carrier.contains("7-11")) 
				{
					deliverytype="7";

					String[] splitFullStrings=full_address.split(" ");
					getshopName=splitFullStrings[0]+splitFullStrings[1];
					getshop=splitFullStrings[3].substring(2);
					//System.out.println(getshop+" , "+getshopName);

				}
				else if (shipping_carrier.contains("Hi-Life")||shipping_carrier.contains("萊爾富")) 
				{
					deliverytype="10";

					String[] splitFullStrings=full_address.split(" ");
					getshopName=splitFullStrings[0];
					getshop=splitFullStrings[2];
					//System.out.println(getshop+" , "+getshopName);
				}
				else if (shipping_carrier.contains("OK Mart")) 
				{
					deliverytype="11";

					String[] splitFullStrings=full_address.split(" ");
					getshopName=splitFullStrings[0];
					getshop=splitFullStrings[2];
					//System.out.println(getshop+" , "+getshopName);
				}
				else if (shipping_carrier.contains("mingjie")) 
				{
					deliverytype="12";
				}
				else if (shipping_carrier.contains("中華郵政")) 
				{
					deliverytype="13";
				}
				else if (shipping_carrier.contains("賣家宅配")) 
				{
					deliverytype="14";
				}
				else 
				{
					//
					deliverytype="";//暂不支持
				}

				//明细
				JSONArray items=orders.getJSONObject(i).getJSONArray("items");
				for(int j=0;j<items.length();j++)
				{
					long item_id=items.getJSONObject(j).getLong("item_id");//平台商品编码
					String item_name=items.getJSONObject(j).getString("item_name");//商品名称
					//对应ERP资料建立：
					//variation_sku为pluno,但是如果variation_sku=""时，item_sku=pluno
					//商家品号pluno
					String item_sku=items.getJSONObject(j).getString("item_sku");//商品SKU
					long variation_id=items.getJSONObject(j).getLong("variation_id");//规格编码
					String variation_name=items.getJSONObject(j).getString("variation_name");//规格名称
					String variation_sku=items.getJSONObject(j).getString("variation_sku");//规格sku
					//数量
					int variation_quantity_purchased=items.getJSONObject(j).getInt("variation_quantity_purchased");//购买此规格数量
					//原单价
					double variation_original_price=items.getJSONObject(j).getDouble("variation_original_price");//购买此规格的原价
					//成交单价
					double variation_discounted_price=items.getJSONObject(j).getDouble("variation_discounted_price");//折扣价
					boolean is_wholesale=items.getJSONObject(j).getBoolean("is_wholesale");//买方是否以批发价购买
					double weight=items.getJSONObject(j).getDouble("weight");//重量
					boolean is_add_on_deal=items.getJSONObject(j).getBoolean("is_add_on_deal");//是否附属品false
					boolean is_main_item=items.getJSONObject(j).getBoolean("is_main_item");//是否主商品false
					int add_on_deal_id=items.getJSONObject(j).getInt("add_on_deal_id");//附属品ID 0
				}


			}

			//找不到的订单
			JSONArray errors = jsonres.getJSONArray("errors");
			for(int a=0;a<errors.length();a++)
			{
				String errorOrderSN=errors.getString(a);
				//System.out.println(errorOrderSN);
			}

			String request_id=jsonres.getString("request_id");//错误追踪ID


		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;

	}


	/**
	 * 获取物流发货方式：3种方式(pickup、dropoff、non_integrated)
	 * @param url
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param ordersn
	 * @return
	 */
	public String GetLogisticInfo(String apiUrl,int partner_id,String partner_key,int shop_id,String ordersn)
	{

		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/logistics/init_info/get";
		}
		else			
		{
			apiUrl+="/v1/logistics/init_info/get";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("ordersn", ordersn);//订单号
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("GetLogisticInfo", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//
			String request_id=jsonres.getString("request_id");		
			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{				

				//这个栏位指定使用什么栏位去发货
				JSONObject info_needed=jsonres.getJSONObject("info_needed");	

				if(info_needed.has("pickup"))//存在就使用此方式
				{
					JSONArray bpickup=info_needed.getJSONArray("pickup");//第1种方式
					for(int c=0;c<bpickup.length();c++)
					{
						String ckeyName=bpickup.getString(c);
						if(ckeyName.equals("address_id"))
						{

						}
						if(ckeyName.equals("pickup_time_id"))
						{

						}					

					}
				}

				if(info_needed.has("dropoff"))//存在就使用此方式
				{
					JSONArray bdropoff=info_needed.getJSONArray("dropoff");//第2种方式					
					for(int c=0;c<bdropoff.length();c++)
					{
						String ckeyName=bdropoff.getString(c);
						if(ckeyName.equals("branch_id"))
						{
							//使用第2中方式dropoff的branch_id去发货

						}
						if(ckeyName.equals("sender_real_name"))
						{
							//使用真实姓名去发货
							String cxx="";

						}
						if(ckeyName.equals("tracking_no"))
						{
							//使用托运单号去发货
						}						

						break;
					}
				}

				if(info_needed.has("non_integrated"))//存在就使用此方式
				{
					JSONArray non_integrated=info_needed.getJSONArray("non_integrated");//第3种方式,已有快递单号==手工填单
					for(int c=0;c<non_integrated.length();c++)
					{
						String ckeyName=non_integrated.getString(c);
						if(ckeyName.equals("tracking_no"))
						{

						}

					}
				}

				//第1种方式pickup
				JSONObject pickup = jsonres.getJSONObject("pickup");
				//这个对象可能是空
				if (pickup.length()>0)
				{
					JSONArray address_list = pickup.getJSONArray("address_list");			
					for(int i=0;i<address_list.length();i++)
					{
						long address_id=address_list.getJSONObject(i).getLong("address_id");
						String country=address_list.getJSONObject(i).getString("country");
						String state=address_list.getJSONObject(i).getString("state");
						String city=address_list.getJSONObject(i).getString("city");
						String address=address_list.getJSONObject(i).getString("address");
						String zipcode=address_list.getJSONObject(i).getString("zipcode");
						String district=address_list.getJSONObject(i).getString("district");
						String town=address_list.getJSONObject(i).getString("town");

						//取货时间列表

						Object listArray = new JSONTokener(address_list.getJSONObject(i).optString("time_slot_list")).nextValue();

						if (listArray instanceof JSONArray)
						{
							JSONArray time_slot_list=address_list.getJSONObject(i).getJSONArray("time_slot_list");
							for (int d = 0; d < time_slot_list.length(); d++) 
							{
								String pickup_time_id= time_slot_list.getJSONObject(d).getString("pickup_time_id");//取货ID
								long date= time_slot_list.getJSONObject(d).getLong("date");//取货日期

								//System.out.println(pickup_time_id);
								//System.out.println(date);

							}

						}
						else 
						{
							JSONObject time_slot_list=address_list.getJSONObject(i).getJSONObject("time_slot_list");
							if (time_slot_list.has("error")) 
							{
								String msg= time_slot_list.getString("msg");//msg
								String error= time_slot_list.getString("error");//error

								//System.out.println(msg);							
								//System.out.println(error);

							}
							else 
							{
								String pickup_time_id= time_slot_list.getString("pickup_time_id");//取货ID
								long date= time_slot_list.getLong("date");//取货日期

								//System.out.println(pickup_time_id);
								//System.out.println(date);
							}
						}



					}	

				}


				//第2种方式dropoff
				JSONObject dropoff=jsonres.getJSONObject("dropoff");
				//这个对象可能是空
				if (dropoff.length()>0)
				{
					JSONArray branch_list=dropoff.getJSONArray("branch_list");
					for(int b=0;b<branch_list.length();b++)
					{
						long branch_id=branch_list.getJSONObject(b).getLong("branch_id");
						String country=branch_list.getJSONObject(b).getString("country");
						String state=branch_list.getJSONObject(b).getString("state");
						String city=branch_list.getJSONObject(b).getString("city");
						String address=branch_list.getJSONObject(b).getString("address");
						String zipcode=branch_list.getJSONObject(b).getString("zipcode");
						String district=branch_list.getJSONObject(b).getString("district");
						String town=branch_list.getJSONObject(b).getString("town");
					}
				}


			}



		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;

	}


	/**
	 *  发货功能，产生托运单号
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param ordersn
	 * @param mode        1:pickup  2:dropoff  3:non_integrated
	 * @param address_id        pickup用，没值给0
	 * @param pickup_time_id    pickup   没值给""
	 * @param tracking_no       3种共用             没值给""
	 * @param branch_id         dropoff  没值给0
	 * @param sender_real_name  dropoff  
	 * @return
	 */


	public String Init(String apiUrl,int partner_id,String partner_key,int shop_id,String ordersn,int mode,long address_id,String pickup_time_id,String tracking_no,long branch_id,String sender_real_name)
	{
		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/logistics/init";
		}
		else			
		{
			apiUrl+="/v1/logistics/init";
		}

		String resbody="";

		try
		{

			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("ordersn", ordersn);//订单号	
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			if(mode==1)
			{
				//pickup
				JSONObject pickup = new JSONObject();
				if(address_id!=0)
				{
					pickup.put("address_id", address_id);
				}
				if(pickup_time_id.equals("")==false)
				{
					pickup.put("pickup_time_id", pickup_time_id);
				}

				if(tracking_no.equals("")==false)
				{
					pickup.put("tracking_no", tracking_no);
				}				

				header.put("pickup", pickup);
			}


			if(mode==2)
			{
				//dropoff
				JSONObject dropoff = new JSONObject();
				if(branch_id!=0)
				{
					dropoff.put("branch_id", branch_id);
				}
				if(sender_real_name.equals("")==false)
				{
					//sender_real_name=java.net.URLEncoder.encode(sender_real_name);
					dropoff.put("sender_real_name", sender_real_name);
				}

				if(tracking_no.equals("")==false)
				{
					dropoff.put("tracking_no", tracking_no);
				}				

				header.put("dropoff", dropoff);				
			}


			if(mode==3)
			{
				//non_integrated
				JSONObject non_integrated = new JSONObject();

				if(tracking_no.equals("")==false)
				{
					non_integrated.put("tracking_no", tracking_no);
				}				

				header.put("non_integrated", non_integrated);
			}		

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();	

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("Init", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);


			String request_id=jsonres.getString("request_id");
			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				//String errormsg=jsonres.getString("msg");
			}
			else
			{
				String tracking_number="";
				if (jsonres.isNull("tracking_number")==false) 
				{
					tracking_number=jsonres.getString("tracking_number");
				}
			}

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;
	}



	/**
	 * 获取托运单url,用于打印
	 * @param url
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param ordersn_list 多单打印在1张纸，需保证物料类型相同(全是全家配送或全是7-11配送)
	 * @return
	 */
	public String GetAirwayBill(String apiUrl,int partner_id,String partner_key,int shop_id,String[] ordersn_list)
	{
		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/logistics/airway_bill/get_mass";
		}
		else			
		{
			apiUrl+="/v1/logistics/airway_bill/get_mass";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("ordersn_list", ordersn_list);//订单列表
			header.put("is_batch", true);//多个单在一张纸打印，需保证物料类型相同(全是全家配送或全是7-11配送)，否则返回还是多个链接没意义
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();	

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("GetAirwayBill", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);
			String request_id=jsonres.getString("request_id");

			/*
			//1.一订单一张纸
			if(jsonres.has("result"))
			{
				JSONObject result = jsonres.getJSONObject("result");			
				int total_count=result.getInt("total_count");			
				//对象==找不到的订单号
				JSONArray errors = result.getJSONArray("errors");
				for(int a=0;a<errors.length();a++)
				{
					String ordersn=errors.getJSONObject(a).getString("ordersn");
					//System.out.println(ordersn);

					String error_code=errors.getJSONObject(a).getString("error_code");
					//System.out.println(error_code);

					String error_description=errors.getJSONObject(a).getString("error_description");
					//System.out.println(error_description);
				}
				//对象==找到的订单号
				JSONArray airway_bills = result.getJSONArray("airway_bills");
				for(int a=0;a<airway_bills.length();a++)
				{
					String ordersn=airway_bills.getJSONObject(a).getString("ordersn");
					String airway_bill=airway_bills.getJSONObject(a).getString("airway_bill");
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
					String airway_bills=bairway_bills.get(a).toString();
				}		

				/*
				JSONArray berrors = batch_result.getJSONArray("errors");
				for(int a=0;a<berrors.length();a++)
				{
					String ordersn=berrors.get(a).toString();
					//System.out.println(ordersn);
				}
				*/
			}			
			 

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;

	}


	/**
	 * 卖家主动取消订单
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param ordersn
	 * @param cancel_reason 缺货：OUT_OF_STOCK 客户要求：CUSTOMER_REQUEST 无法交付区域：UNDELIVERABLE_AREA 不支持货到付款： COD_NOT_SUPPORTED
	 * @return
	 */
	public String CancelOrder(String apiUrl,int partner_id,String partner_key,int shop_id,String ordersn,String cancel_reason)
	{

		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/orders/cancel";
		}
		else			
		{
			apiUrl+="/v1/orders/cancel";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("ordersn", ordersn);//订单号
			header.put("cancel_reason", cancel_reason);//原因
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();	

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("CancelOrder", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//
			String request_id=jsonres.getString("request_id");		
			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{
				int modified_time=jsonres.getInt("modified_time");
			}

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;
	}


	/**
	 * 卖家拒绝买家取消订单（超过1小时订单，买家取消订单需要卖家同意）
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param ordersn
	 * @return
	 */
	public String RejectCancel(String apiUrl,int partner_id,String partner_key,int shop_id,String ordersn)
	{

		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/orders/buyer_cancellation/reject";
		}
		else			
		{
			apiUrl+="/v1/orders/buyer_cancellation/reject";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("ordersn", ordersn);//订单号
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();	

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("RejectCancel", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//
			String request_id=jsonres.getString("request_id");		
			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{
				int modified_time=jsonres.getInt("modified_time");
			}

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;
	}


	/**
	 * 卖家同意买家取消订单（超过1小时订单，买家取消订单需要卖家同意）
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param ordersn
	 * @return
	 */
	public String AcceptCancel(String apiUrl,int partner_id,String partner_key,int shop_id,String ordersn)
	{

		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/orders/buyer_cancellation/accept";
		}
		else			
		{
			apiUrl+="/v1/orders/buyer_cancellation/accept";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("ordersn", ordersn);//订单号
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();	

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("AcceptCancel", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//
			String request_id=jsonres.getString("request_id");		
			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{
				int modified_time=jsonres.getInt("modified_time");
			}

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;
	}





	/**
	 * 获虾皮支持的物流商
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @return
	 */
	public String GetLogistics(String apiUrl,int partner_id,String partner_key,int shop_id)
	{		
		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/logistics/channel/get";
		}
		else			
		{
			apiUrl+="/v1/logistics/channel/get";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("GetLogistics", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//
			String request_id=jsonres.getString("request_id");		
			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{

				JSONArray logistics=jsonres.getJSONArray("logistics");
				for (int i = 0; i < logistics.length(); i++) 
				{
					long logistic_id= logistics.getJSONObject(i).getLong("logistic_id");
					String logistic_name= logistics.getJSONObject(i).getString("logistic_name");
					boolean has_cod= logistics.getJSONObject(i).getBoolean("has_cod");
					boolean enabled= logistics.getJSONObject(i).getBoolean("enabled");
					String fee_type= logistics.getJSONObject(i).getString("fee_type");
					//System.out.println("货运厂商代码：" +logistic_id+",货运厂商名称：" +logistic_name + ",启用:" +enabled);
					//
					JSONArray sizes=logistics.getJSONObject(i).getJSONArray("sizes");
					for (int a = 0; a < sizes.length(); a++) 
					{
						long size_id= sizes.getJSONObject(a).getLong("size_id");
						String name= sizes.getJSONObject(a).getString("name");
						String default_price= sizes.getJSONObject(a).getString("default_price");
						//System.out.println("尺寸："+name+",默认价格："+default_price);
					}

					//
					JSONObject weight_limits = logistics.getJSONObject(i).getJSONObject("weight_limits");					
					double item_max_weight=weight_limits.getDouble("item_max_weight");
					double item_min_weight=weight_limits.getDouble("item_min_weight");


					JSONObject item_max_dimension = logistics.getJSONObject(i).getJSONObject("item_max_dimension");

					double height=0;
					if (item_max_dimension.has("height")) 
					{
						height=item_max_dimension.getDouble("height");
					}

					double width=0;
					if (item_max_dimension.has("width")) 
					{
						width=item_max_dimension.getDouble("width");
					}

					double length=0;
					if (item_max_dimension.has("length")) 
					{
						length=item_max_dimension.getDouble("length");
					}

					String unit="";
					if (item_max_dimension.has("unit")) 
					{
						unit=item_max_dimension.getString("unit");
					}

					//System.out.println(unit);

				}



			}

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;
	}


	/**
	 * 物流状态
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param ordersn
	 * @param tracking_number
	 * @return
	 */
	public String GetLogisticsMessage(String apiUrl,int partner_id,String partner_key,int shop_id,String ordersn,String tracking_number)
	{
		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/logistics/tracking";
		}
		else			
		{
			apiUrl+="/v1/logistics/tracking";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("ordersn", ordersn);//订单号
			header.put("tracking_number", tracking_number);//托运单号
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("GetLogisticsMessage", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//
			String request_id=jsonres.getString("request_id");		
			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{
				String tracking_no=jsonres.getString("tracking_number");

				JSONArray tracking_info=jsonres.getJSONArray("tracking_info");
				for (int a = 0; a < tracking_info.length(); a++) 
				{
					int ctime=tracking_info.getJSONObject(a).getInt("ctime");
					String description=tracking_info.getJSONObject(a).getString("description");
					String status=tracking_info.getJSONObject(a).getString("status");
				}


			}

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;

	}


	/**
	 * 获取订单物流信息(这个接口需要跟虾皮商务经理申请,否则没数据)
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param ordersn	
	 * @return
	 */
	public String GetOrderLogistics(String apiUrl,int partner_id,String partner_key,int shop_id,String ordersn)
	{
		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/logistics/order/get";
		}
		else			
		{
			apiUrl+="/v1/logistics/order/get";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("ordersn", ordersn);//订单号			
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("GetOrderLogistics", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//
			String request_id=jsonres.getString("request_id");		
			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{
				JSONObject logistics=jsonres.getJSONObject("logistics");
				String shipping_carrier=logistics.getString("shipping_carrier");
				long logistic_id=logistics.getLong("logistic_id");
				String service_code=logistics.getString("service_code");
				String first_mile_name=logistics.getString("first_mile_name");
				String last_mile_name=logistics.getString("last_mile_name");
				boolean goods_to_declare=logistics.getBoolean("last_mile_name");
				String String=logistics.getString("String");
				String zone=logistics.getString("zone");
				String lane_code=logistics.getString("lane_code");
				String warehouse_address=logistics.getString("warehouse_address");
				long warehouse_id=logistics.getLong("warehouse_id");
				boolean cod=logistics.getBoolean("cod");

				//
				JSONObject recipient_address=logistics.getJSONObject("recipient_address");

				String name=recipient_address.getString("name");
				String phone=recipient_address.getString("phone");
				String town=recipient_address.getString("town");
				String district=recipient_address.getString("district");
				String city=recipient_address.getString("city");
				String state=recipient_address.getString("state");
				String country=recipient_address.getString("country");
				String zipcode=recipient_address.getString("zipcode");
				String full_address=recipient_address.getString("full_address");


			}

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;

	}


	/**
	 * 付款方式列表
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @return
	 */
	public String GetPaymentList(String apiUrl,int partner_id,String partner_key)
	{
		//ID:印度尼西亚
		//SG:新加坡
		//MY:马来西亚
		//TH:泰国
		//VN:越南
		//PH:菲律宾
		//TW:中国台湾

		//国家：TW，支付方式：Fubon Bank Transfer
		//国家：TW，支付方式：Esun Bank Transfer
		//国家：TW，支付方式：Esun CB COD - Familymart
		//国家：TW，支付方式：Free
		//国家：TW，支付方式：Cybersource
		//国家：TW，支付方式：Airpay Credit Card
		//国家：TW，支付方式：Airpay Credit Card Installment Esun 3x
		//国家：TW，支付方式：Esun Credit Card
		//国家：TW，支付方式：Airpay Credit Card Installment Taishin 6x
		//国家：TW，支付方式：Shopee Wallet
		//国家：TW，支付方式：Bank Transfer
		//国家：TW，支付方式：Esun CB Bank Transfer
		//国家：TW，支付方式：Esun CB COD - Seven
		//国家：TW，支付方式：現付
		//国家：TW，支付方式：Buyer-Seller Self Arrange
		//国家：TW，支付方式：Airpay Credit Card Installment
		//国家：TW，支付方式：Airpay Credit Card Installment Esun 6x
		//国家：TW，支付方式：Airpay Credit Card Installment Esun 12x
		//国家：TW，支付方式：Airpay Credit Card Installment Esun 24x
		//国家：TW，支付方式：Airpay Credit Card Installment Taishin 3x
		//国家：TW，支付方式：Airpay Credit Card Installment Taishin 12x
		//国家：TW，支付方式：Airpay Credit Card Installment Taishin 24x

		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/payment/list";
		}
		else			
		{
			apiUrl+="/v1/payment/list";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("partner_id", partner_id);//伙伴id			
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("GetPaymentList", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//

			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{
				JSONArray payment_method_list=jsonres.getJSONArray("payment_method_list");		
				for (int a = 0; a < payment_method_list.length(); a++) 
				{
					String country=payment_method_list.getJSONObject(a).getString("country");
					String payment_method=payment_method_list.getJSONObject(a).getString("payment_method");

					//System.out.println("国家："+country +"，支付方式："+payment_method );
				}

			}

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;

	}


	public String GetEscrowDetails(String apiUrl,int partner_id,String partner_key,int shop_id,String ordersn)
	{
		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/orders/my_income";
		}
		else			
		{
			apiUrl+="/v1/orders/my_income";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("ordersn", ordersn);//订单号
			header.put("shopid", shop_id);//门店
			header.put("partner_id", partner_id);//伙伴id			
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			
			resbody=HttpSend.SendShopee("GetEscrowDetails", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//

			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{
				JSONObject escrowOrder=jsonres.getJSONObject("order");	

				//实收明细
				JSONObject income_details=escrowOrder.getJSONObject("income_details");

				String sCoin=income_details.getString("coin");//虾币抵扣
				String sVoucher=income_details.getString("voucher");//虾皮凭证
				String sVoucher_seller=income_details.getString("voucher_seller");//卖家凭证
				String sSeller_rebate=income_details.getString("seller_rebate");//给卖家补贴
				int sCredit_card_promotion=income_details.getInt("credit_card_promotion");//信用卡促销
				String sEscrow_amount=income_details.getString("escrow_amount");//卖家实际收到的
				String sCommission_fee=income_details.getString("commission_fee");//平台服务费
				String sShipping_fee_rebate=income_details.getString("shipping_fee_rebate");//配送费补贴
				int sCross_border_tax=income_details.getInt("cross_border_tax");//税务买方支付
				int sCredit_card_transaction_fee=income_details.getInt("credit_card_transaction_fee");//信用卡交易费

				//System.out.println(sCoin);				
				//System.out.println(sVoucher);
				//System.out.println(sVoucher_seller);
				//System.out.println(sSeller_rebate);
				//System.out.println(sCredit_card_promotion);
				//System.out.println(sEscrow_amount);
				//System.out.println(sCommission_fee);
				//System.out.println(sShipping_fee_rebate);
				//System.out.println(sCross_border_tax);
				//System.out.println(sCredit_card_transaction_fee);

				/*
				//商品分摊明细
				JSONArray items=escrowOrder.getJSONArray("items");
				for (int j = 0; j < items.length(); j++) 
				{					
					long item_id=items.getJSONObject(j).getLong("item_id");//平台商品编码
					String item_name=items.getJSONObject(j).getString("item_name");//商品名称
					//对应ERP资料建立：
					//variation_sku为pluno,但是如果variation_sku=""时，item_sku=pluno
					//商家品号pluno
					String item_sku=items.getJSONObject(j).getString("item_sku");//商品SKU
					long variation_id=items.getJSONObject(j).getLong("variation_id");//规格编码
					String variation_name=items.getJSONObject(j).getString("variation_name");//规格名称
					String variation_sku=items.getJSONObject(j).getString("variation_sku");//规格sku
					//数量
					int variation_quantity_purchased=items.getJSONObject(j).getInt("quantity_purchased");//购买此规格数量
					//原单价
					double variation_original_price=items.getJSONObject(j).getDouble("original_price");//购买此规格的原价
					//打折单价(单纯打折)
					double variation_discounted_price=items.getJSONObject(j).getDouble("discounted_price");//折扣价

					//成交价(虾币、凭证抵扣后)*****************************************************这个是最终成交价格
					String deal_price=items.getJSONObject(j).getString("deal_price");//成交价
					//信用卡促销
					String credit_card_promotion=items.getJSONObject(j).getString("credit_card_promotion");//信用卡促销
					//虾币抵扣
					String discount_from_coin=items.getJSONObject(j).getString("discount_from_coin");//虾币抵扣

					//卖家折扣
					double discount_from_voucher_seller=items.getJSONObject(j).getDouble("discount_from_voucher_seller");//
					//虾皮凭证
					double discount_from_voucher=items.getJSONObject(j).getDouble("discount_from_voucher");//
					//平台给卖家补贴
					double seller_rebate=items.getJSONObject(j).getDouble("seller_rebate");//					

					boolean is_add_on_deal=items.getJSONObject(j).getBoolean("is_add_on_deal");//是否附属品false
					boolean is_main_item=items.getJSONObject(j).getBoolean("is_main_item");//是否主商品false
					int add_on_deal_id=items.getJSONObject(j).getInt("add_on_deal_id");//附属品ID 0



				}		
				 */	


			}

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;

	}


	
	/**
	 * 退货列表,如果有很多笔订单，需要多次调用，每次最大100笔,返回结果中会标明是否还有下一页
	 * @param url
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param pageIndex 默认值：0
	 * @param create_time_from 
	 * @param create_time_to 
	 * @return
	 */
	public String GetReturnList(String apiUrl,int partner_id,String partner_key,int shop_id,int pageIndex,long create_time_from,long create_time_to)
	{
		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/returns/get";
		}
		else			
		{
			apiUrl+="/v1/returns/get";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("create_time_from", create_time_from);//创建时间开始，开始与结束之间最大15天,非必填			
			header.put("create_time_to", create_time_to);//创建时间结束,非必填			
			header.put("pagination_entries_per_page", 50);//每页条数,最大100,这里设置50是因为查明细最大50			
			header.put("pagination_offset", pageIndex);//页码		
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("GetReturnList", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			else
			{
				boolean more= jsonres.getBoolean("more");//是否还有更多页
				String request_id=jsonres.getString("request_id");//错误追踪ID

				//订单单头列表Object []
				JSONArray returns = jsonres.getJSONArray("returns");
				for(int i=0;i<returns.length();i++)
				{
					String ordersn=returns.getJSONObject(i).getString("ordersn");//订单号
					long returnsn=returns.getJSONObject(i).getLong("returnsn");//退货编号
					String reason=returns.getJSONObject(i).getString("reason");//原因码
					String text_reason=returns.getJSONObject(i).getString("text_reason");//原因
					double refund_amount=returns.getJSONObject(i).getDouble("refund_amount");//退款金额
					
					JSONObject user=returns.getJSONObject(i).getJSONObject("user");
					String email=user.get("email").toString();
					String username=user.get("username").toString();
					
					String imageUrl="";
					JSONArray images=returns.getJSONObject(i).getJSONArray("images");
					for(int a=0;a<images.length();a++)
					{												
						if (a==images.length()-1) 
						{
							imageUrl+=images.getString(a);
						}	
						else 
						{
							imageUrl+=images.getString(a)+"@@@";
						}
					}
					
					long update_time=returns.getJSONObject(i).getLong("update_time");//更新时间
					//System.out.println(ordersn);
				}
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;
	}
	
	
	/**
	 * 卖家同意买家退货
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param returnsn
	 * @return
	 */
	public String AcceptReturn(String apiUrl,int partner_id,String partner_key,int shop_id,String returnsn)
	{

		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/returns/confirm";
		}
		else			
		{
			apiUrl+="/v1/returns/confirm";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("returnsn", returnsn);//退货编号
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("AcceptReturn", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//
			String request_id=jsonres.getString("request_id");		
			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}			

		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;
	}
	
	
	/**
	 * 卖家拒绝买家退货
	 * @param apiUrl
	 * @param partner_id
	 * @param partner_key
	 * @param shop_id
	 * @param returnsn
	 * @param email
	 * @param dispute_reason
	 * @param dispute_text_reason
	 * @param images
	 * @return
	 */
	public String RejectReturn(String apiUrl,int partner_id,String partner_key,int shop_id,String returnsn,String email,String dispute_reason,String dispute_text_reason,String[] images)
	{

		//Authorization:虾皮需要认证
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/returns/dispute";
		}
		else			
		{
			apiUrl+="/v1/returns/dispute";
		}

		String resbody="";

		try
		{
			long timestamp =System.currentTimeMillis()/1000;

			JSONObject header = new JSONObject();
			header.put("returnsn", returnsn);//订单号
			header.put("email", email);
			header.put("dispute_reason", dispute_reason);
			header.put("dispute_text_reason", dispute_text_reason);
			header.put("images", images);
			header.put("partner_id", partner_id);//伙伴id			
			header.put("shopid", shop_id);//店铺id
			header.put("timestamp", timestamp);//时间戳			

			//
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();

			String Authorization=eu.HMAC_SHA256(apiUrl+"|"+request, partner_key);
			eu=null;
			
			resbody=HttpSend.SendShopee("RejectReturn", request, apiUrl,Authorization);
			JSONObject jsonres = new JSONObject(resbody);

			//
			String request_id=jsonres.getString("request_id");		
			if(jsonres.has("error"))//错误
			{
				String errorno=jsonres.getString("error");
				String errormsg=jsonres.getString("msg");
			}
			
		}
		catch (Exception ex) 
		{			
			return "";
		}			

		return resbody;
	}
	
	
}
