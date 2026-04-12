package com.dsc.spos.utils.ec;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;

//电商-----momo购物
//测试环境:https://scmuat.momoshop.com.tw/
//正式环境:https://scmapi.momoshop.com.tw/
public class Momo 
{

	/**
	 * 未出貨訂單-廠商配送-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param company_fr_dd 转单日起，yyyy/MM/dd
	 * @param company_fr_hh 转单日起，时
	 * @param company_fr_mm 转单日起，分
	 * @param company_to_dd 转单日讫，yyyy/MM/dd
	 * @param company_to_hh 转单日讫，时
	 * @param company_to_mm 转单日讫，分
	 * @return
	 */
	public String GetOrderunsendCompanyQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String company_fr_dd,String company_fr_hh,String company_fr_mm,String company_to_dd,String company_to_hh,String company_to_mm)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendCompanyQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();
			sendInfo.put("company_fr_dd", company_fr_dd);//转单日起2016/03/18 最大区间2个月
			sendInfo.put("company_fr_hh", company_fr_hh);//00
			sendInfo.put("company_fr_mm", company_fr_mm);//00
			sendInfo.put("company_to_dd", company_to_dd);//转单日讫2016/03/18
			sendInfo.put("company_to_hh", company_to_hh);//23
			sendInfo.put("company_to_mm", company_to_mm);//59
			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrderunsendCompanyQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{

					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次
					String isRedStr=dataList.getJSONObject(a).getString("isRedStr");//灯号，正常
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001
					//配送状态，无意义
					//可出货/已確認指定配送日/暫時缺貨/無貨取消訂單/
					//收件資料有誤/不配送地區 
					//超才/未出即退/需採購協助事項/自行備註，不需momo協助
					String remark5VStr=dataList.getJSONObject(a).getString("remark5VStr");
					String msgNote=dataList.getJSONObject(a).getString("msgNote");//配送讯息，无意义
					String scmDelyDate=dataList.getJSONObject(a).getString("scmDelyDate");//约定配送日
					String delyGbStr=dataList.getJSONObject(a).getString("delyGbStr");//物流公司,无意义
					String slipNo=dataList.getJSONObject(a).getString("slipNo");//配送编号,无意义
					String orderGbStr=dataList.getJSONObject(a).getString("orderGbStr");//订单类别，一般訂單
					String msg=dataList.getJSONObject(a).getString("msg");//客户配送需求，(週一不配送)
					String lastPricDate=dataList.getJSONObject(a).getString("lastPricDate");//转单日，2014/08/29
					String delyHopeDate=dataList.getJSONObject(a).getString("delyHopeDate");//预计送达日
					String receiver=dataList.getJSONObject(a).getString("receiver");//收件人
					String receiverTel=dataList.getJSONObject(a).getString("receiverTel");//收件人电话
					String receiverMobile=dataList.getJSONObject(a).getString("receiverMobile");//收件人移动电话
					String postNo=dataList.getJSONObject(a).getString("postNo");//收件人邮政编码
					String receiverAddr=dataList.getJSONObject(a).getString("receiverAddr");//收件人地址
					String entpGoodsNo=dataList.getJSONObject(a).getString("entpGoodsNo");//商品原厂编码
					String goodsCode=dataList.getJSONObject(a).getString("goodsCode");//商品编码
					String goodsName=dataList.getJSONObject(a).getString("goodsName");//商品名称
					String goodsDtCode=dataList.getJSONObject(a).getString("goodsDtCode");//单品编码，001
					String goodsDtInfo=dataList.getJSONObject(a).getString("goodsDtInfo");//单品详细，無
					String syslast=dataList.getJSONObject(a).getString("syslast");//数量
					String buyPrice=dataList.getJSONObject(a).getString("buyPrice");//进价含税
					String describeNote=dataList.getJSONObject(a).getString("describeNote");//赠品，
					String custName=dataList.getJSONObject(a).getString("custName");//订购人，林O筠
					String invoiceNo=dataList.getJSONObject(a).getString("invoiceNo");//发票号码，JF90774668
					String invoiceDate=dataList.getJSONObject(a).getString("invoiceDate");//发票日期，2014/08/29
					String custId=dataList.getJSONObject(a).getString("custId");//个人识别码，5743
					String packYnStr=dataList.getJSONObject(a).getString("packYnStr");//群组变价商品，否
					String claimReason=dataList.getJSONObject(a).getString("claimReason");//换货原因，

					//System.out.println(completeOrderNo+":"+isRedStr);

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
	 * 未出貨訂單-廠商配送-出貨確認 (订单号+配送状态+物流公司+物流单号)
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param completeOrderNoList (completeOrderNo 订单编号，
	 * remark5VStr 配送状态，(已配送、電聯不上(2天3次以上)、已確認指定配送日、已電聯，尚未確認日期/規格、需配送加價協調中、暫時缺貨、無貨取消訂單、收件資料有誤/不配送地區、需採購協助事項、自行備註，不需momo協助)  ，
	 * msgNote 配送讯息，當配送狀態為[已配送]则不需要填写 ，
	 * scmDelyDate 约定配送日,yyyy/MM/dd  當配送狀態為[已確認指定配送日]則必須填寫，
	 * delyGbStr 物流公司,已配送，此欄位必填. (宅配通、新竹貨運、統一速達、大榮貨運、東元物流、其他宅配、郵局、便利袋、其他))，
	 * slipNo 物流单号,當配送狀態為[已配送]，此欄位必填
	 * @return
	 */
	public String OrderunsendCompanyConfirm(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,List<Map<String, Object>> completeOrderNoList)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendCompanyConfirm");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();
			for (Map<String, Object> oneData : completeOrderNoList)
			{
				String completeOrderNo= oneData.get("completeOrderNo").toString();
				String remark5VStr= oneData.get("remark5VStr").toString();
				String msgNote= oneData.get("msgNote").toString();
				String scmDelyDate= oneData.get("scmDelyDate").toString();
				String delyGbStr= oneData.get("delyGbStr").toString();
				String slipNo= oneData.get("slipNo").toString();

				JSONObject body = new JSONObject();

				body.put("completeOrderNo", completeOrderNo);//订单编号
				//配送状态，
				//(已配送、
				//電聯不上(2天3次以上)、
				//已確認指定配送日、
				//已電聯，
				//尚未確認日期/規格、
				//需配送加價協調中、
				//暫時缺貨、
				//無貨取消訂單、
				//收件資料有誤/不配送地區、
				//需採購協助事項、
				//自行備註，不需momo協助)
				body.put("remark5VStr", remark5VStr);

				if (remark5VStr.equals("已配送")==false) 
				{
					body.put("msgNote", msgNote);//配送讯息，當配送狀態為[已配送]则不需要填写
				}

				if (remark5VStr.equals("已確認指定配送日")) 
				{
					body.put("scmDelyDate", scmDelyDate);//约定配送日,yyyy/MM/dd  當配送狀態為[已確認指定配送日]則必須填寫
				}

				if (remark5VStr.equals("已配送")) 
				{
					//物流单号,當配送狀態為[已配送]，此欄位必填
					body.put("slipNo", slipNo);

					//物流公司,已配送，此欄位必填
					//(宅配通、
					//新竹貨運、
					//統一速達、
					//大榮貨運、
					//東元物流、
					//其他宅配、
					//郵局、
					//便利袋、
					//其他)
					body.put("delyGbStr", delyGbStr);
				}

				sendInfoList.put(body);
			}			


			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderunsendCompanyConfirm", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONObject resultInfo=jsonres.getJSONObject("resultInfo");
				//未執行筆數
				String undoCnt=resultInfo.getString("undoCnt");
				JSONArray undoList=resultInfo.getJSONArray("undoList");//未執行列表
				for(int a=0;a<undoList.length();a++)
				{
					String sUndoList=undoList.getString(a);

					//System.out.println("未执行的列表:"+sUndoList);					
				}

				//出庫成功筆數
				String confirmOkCnt=resultInfo.getString("confirmOkCnt");
				JSONArray confirmOkList=resultInfo.getJSONArray("confirmOkList");//出庫成功列表
				for(int a=0;a<confirmOkList.length();a++)
				{
					String sConfirmOkList=confirmOkList.getString(a);

					//System.out.println("出庫成功列表:"+sConfirmOkList);					
				}

				//出庫失敗筆數
				String confirmFailCnt=resultInfo.getString("confirmFailCnt");
				JSONArray confirmFailList=resultInfo.getJSONArray("confirmFailList");//出庫失敗列表
				for(int a=0;a<confirmFailList.length();a++)
				{
					String sConfirmFailList=confirmFailList.getString(a);

					//System.out.println("出庫失敗列表:"+sConfirmFailList);					
				}

				//保存成功笔数
				String saveMsgOkCnt=resultInfo.getString("saveMsgOkCnt");

				//保存失败笔数
				String saveMsgFailCnt=resultInfo.getString("saveMsgFailCnt");
				JSONArray saveMsgFailList=resultInfo.getJSONArray("saveMsgFailList");//保存失敗列表
				for(int a=0;a<saveMsgFailList.length();a++)
				{
					String sSaveMsgFailList=saveMsgFailList.getString(a);

					//System.out.println("保存失敗列表:"+sSaveMsgFailList);					
				}


				//建立商談成功筆數
				String addCounselOkCnt=resultInfo.getString("addCounselOkCnt");

				//建立商談失敗筆數
				String addCounselFailCnt=resultInfo.getString("addCounselFailCnt");
				JSONArray addCounselFailList=resultInfo.getJSONArray("addCounselFailList");//建立商談失敗列表
				for(int a=0;a<addCounselFailList.length();a++)
				{
					String sAddCounselFailList=addCounselFailList.getString(a);

					//System.out.println("建立商談失敗列表:"+sAddCounselFailList);					
				}


				//可接單量調整為0&申請下市成功筆數
				String setZeroOkCnt=resultInfo.getString("setZeroOkCnt");

				//可接單量調整為0&申請下市失敗筆數
				String setZeroFailCount=resultInfo.getString("setZeroFailCount");
				JSONArray setZeroMsgList=resultInfo.getJSONArray("setZeroMsgList");//可接單量調整為0&申請下市失敗列表
				for(int a=0;a<setZeroMsgList.length();a++)
				{
					String sSetZeroMsgList=setZeroMsgList.getString(a);

					//System.out.println("可接單量調整為0&申請下市失敗列表:"+sSetZeroMsgList);					
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
	 * 未出貨訂單-超商取貨-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param stores_fr_dd 转单日起，yyyy/MM/dd
	 * @param stores_fr_hh 转单日起，时
	 * @param stores_fr_mm 转单日起，分
	 * @param stores_to_dd 转单日讫，yyyy/MM/dd
	 * @param stores_to_hh 转单日讫，时
	 * @param stores_to_mm 转单日讫，分
	 * @param stores_special 特配门市 Y:是, N:否
	 * @return
	 */
	public String GetOrderunsendStoresQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String stores_fr_dd,String stores_fr_hh,String stores_fr_mm,String stores_to_dd,String stores_to_hh,String stores_to_mm,String stores_special)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendStoresQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();
			sendInfo.put("stores_fr_dd", stores_fr_dd);//转单日起2016/03/18 最大区间2个月
			sendInfo.put("stores_fr_hh", stores_fr_hh);//00
			sendInfo.put("stores_fr_mm", stores_fr_mm);//00
			sendInfo.put("stores_to_dd", stores_to_dd);//转单日讫2016/03/18
			sendInfo.put("stores_to_hh", stores_to_hh);//23
			sendInfo.put("stores_to_mm", stores_to_mm);//59
			sendInfo.put("stores_special", stores_special);//特配门市 Y:是, N:否
			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrderunsendStoresQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{

					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次
					String isRedStr=dataList.getJSONObject(a).getString("isRedStr");//灯号，正常
					String storeIdName=dataList.getJSONObject(a).getString("storeIdName");//取货门店
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001
					//配送状态，无意义
					//可出货/已確認指定配送日/暫時缺貨/無貨取消訂單/
					//收件資料有誤/不配送地區 
					//超才/未出即退/需採購協助事項/自行備註，不需momo協助
					String remark5VStr=dataList.getJSONObject(a).getString("remark5VStr");
					String msgNote=dataList.getJSONObject(a).getString("msgNote");//配送讯息，无意义
					String reason=dataList.getJSONObject(a).getString("reason");//驗退原因					
					String orderGbStr=dataList.getJSONObject(a).getString("orderGbStr");//订单类别，一般訂單
					String msg=dataList.getJSONObject(a).getString("msg");//客户配送需求，(週一不配送)
					String lastPricDate=dataList.getJSONObject(a).getString("lastPricDate");//转单日，2014/08/29
					String delyHopeDate=dataList.getJSONObject(a).getString("delyHopeDate");//预计送达日					
					String receiverMask=dataList.getJSONObject(a).getString("receiverMask");//收件人
					String receiver=dataList.getJSONObject(a).getString("receiver");//收件人					
					String entpGoodsNo=dataList.getJSONObject(a).getString("entpGoodsNo");//商品原厂编码
					String goodsCode=dataList.getJSONObject(a).getString("goodsCode");//商品编码
					String goodsName=dataList.getJSONObject(a).getString("goodsName");//商品名称
					String goodsDtCode=dataList.getJSONObject(a).getString("goodsDtCode");//单品编码，001
					String goodsDtInfo=dataList.getJSONObject(a).getString("goodsDtInfo");//单品详细，無
					String syslast=dataList.getJSONObject(a).getString("syslast");//数量
					String buyPrice=dataList.getJSONObject(a).getString("buyPrice");//进价含税
					String describeNote=dataList.getJSONObject(a).getString("describeNote");//赠品，
					String custNameMask=dataList.getJSONObject(a).getString("custNameMask");//订购人，林O筠
					String invoiceNo=dataList.getJSONObject(a).getString("invoiceNo");//发票号码，JF90774668
					String invoiceDate=dataList.getJSONObject(a).getString("invoiceDate");//发票日期，2014/08/29
					String custId=dataList.getJSONObject(a).getString("custId");//个人识别码，5743
					String packYnStr=dataList.getJSONObject(a).getString("packYnStr");//群组变价商品，否
					String boxYn=dataList.getJSONObject(a).getString("boxYn");//换货原因，

					//System.out.println(completeOrderNo+":"+isRedStr);

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
	 * 未出貨訂單-超商取貨-貨源確認
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param completeOrderNoList(completeOrderNo 订单编号
	 * remark5VStr  配送状态
	 * msgNote 配送讯息)
	 * @return
	 */
	public String OrderunsendStoresCheckGoods(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,List<Map<String, Object>> completeOrderNoList)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendStoresCheckGoods");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();

			for (Map<String, Object> oneData : completeOrderNoList)
			{
				String completeOrderNo= oneData.get("completeOrderNo").toString();
				String remark5VStr= oneData.get("remark5VStr").toString();
				String msgNote= oneData.get("msgNote").toString();

				JSONObject body = new JSONObject();

				body.put("completeOrderNo", completeOrderNo);//订单编号
				//配送状态，
				//(可出貨、
				//已確認指定配送日、
				//暫時缺貨、
				//無貨取消訂單、
				//收件資料有誤/不配送地區、
				//超才、
				//需採購協助事項、
				//自行備註，不需momo協助)
				body.put("remark5VStr", remark5VStr);
				body.put("msgNote", msgNote);//配送讯息

				//
				sendInfoList.put(body);
			}


			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderunsendStoresCheckGoods", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONObject resultInfo=jsonres.getJSONObject("resultInfo");
				//未執行筆數
				String undoCnt=resultInfo.getString("undoCnt");
				JSONArray undoList=resultInfo.getJSONArray("undoList");//未執行列表
				for(int a=0;a<undoList.length();a++)
				{
					String sUndoList=undoList.getString(a);

					//System.out.println("未执行的列表:"+sUndoList);					
				}			

				//保存成功笔数
				String saveMsgOkCnt=resultInfo.getString("saveMsgOkCnt");

				//保存失败笔数
				String saveMsgFailCnt=resultInfo.getString("saveMsgFailCnt");
				JSONArray saveMsgFailList=resultInfo.getJSONArray("saveMsgFailList");//保存失敗列表
				for(int a=0;a<saveMsgFailList.length();a++)
				{
					String sSaveMsgFailList=saveMsgFailList.getString(a);

					//System.out.println("保存失敗列表:"+sSaveMsgFailList);					
				}


				//建立商談成功筆數
				String addCounselOkCnt=resultInfo.getString("addCounselOkCnt");

				//建立商談失敗筆數
				String addCounselFailCnt=resultInfo.getString("addCounselFailCnt");
				JSONArray addCounselFailList=resultInfo.getJSONArray("addCounselFailList");//建立商談失敗列表
				for(int a=0;a<addCounselFailList.length();a++)
				{
					String sAddCounselFailList=addCounselFailList.getString(a);

					//System.out.println("建立商談失敗列表:"+sAddCounselFailList);					
				}


				//可接單量調整為0&申請下市成功筆數
				String setZeroOkCnt=resultInfo.getString("setZeroOkCnt");

				//可接單量調整為0&申請下市失敗筆數
				String setZeroFailCount=resultInfo.getString("setZeroFailCount");
				JSONArray setZeroMsgList=resultInfo.getJSONArray("setZeroMsgList");//可接單量調整為0&申請下市失敗列表
				for(int a=0;a<setZeroMsgList.length();a++)
				{
					String sSetZeroMsgList=setZeroMsgList.getString(a);

					//System.out.println("可接單量調整為0&申請下市失敗列表:"+sSetZeroMsgList);					
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
	 * 未出貨訂單-超商取貨-併箱
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param completeOrderNoList (completeOrderNo：订单编号 ,boxYn：箱号 ,remark5VStr：配送状态 )
	 * @return
	 */
	public String OrderunsendStoresCombineBox(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,List<Map<String, Object>> completeOrderNoList)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendStoresCombineBox");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();

			for (Map<String, Object> oneData : completeOrderNoList)
			{
				String completeOrderNo= oneData.get("completeOrderNo").toString();
				String boxYn= oneData.get("boxYn").toString();
				String remark5VStr= oneData.get("remark5VStr").toString();

				JSONObject body = new JSONObject();
				body.put("completeOrderNo", completeOrderNo);//订单编号
				//配送状态，
				//(可出貨、
				//已確認指定配送日、
				//暫時缺貨、
				//無貨取消訂單、
				//收件資料有誤/不配送地區、
				//超才、
				//需採購協助事項、
				//自行備註，不需momo協助)
				body.put("boxYn", boxYn);
				body.put("remark5VStr", remark5VStr);//配送讯息

				//
				sendInfoList.put(body);				
			}

			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderunsendStoresCombineBox", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONObject resultInfo=jsonres.getJSONObject("resultInfo");
				//未執行筆數
				String undoCnt=resultInfo.getString("undoCnt");
				JSONArray undoList=resultInfo.getJSONArray("undoList");//未執行列表
				for(int a=0;a<undoList.length();a++)
				{
					String sUndoList=undoList.getString(a);

					//System.out.println("未执行的列表:"+sUndoList);					
				}			

				//併箱成功笔数
				String combineOkCnt=resultInfo.getString("combineOkCnt");

				//併箱失败笔数
				String combineFailCnt=resultInfo.getString("combineFailCnt");
				JSONArray combineFailList=resultInfo.getJSONArray("combineFailList");//併箱失敗列表
				for(int a=0;a<combineFailList.length();a++)
				{
					String sCombineFailList=combineFailList.getString(a);

					//System.out.println("併箱失敗列表:"+sCombineFailList);					
				}


				//箱號重複筆數
				String combineUsedCnt=resultInfo.getString("combineUsedCnt");

				JSONArray addCounselFailList=resultInfo.getJSONArray("addCounselFailList");//箱號重複列表
				for(int a=0;a<addCounselFailList.length();a++)
				{
					String sAddCounselFailList=addCounselFailList.getString(a);

					//System.out.println("箱號重複列表列表:"+sAddCounselFailList);					
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
	 * 未出貨訂單-超商取貨-出貨確認
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param completeOrderNoList (completeOrderNo：订单编号 ,remark5VStr：配送状态 )
	 * @return
	 */
	public String OrderunsendStoresFinish(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,List<Map<String, Object>> completeOrderNoList)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendStoresFinish");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();

			for (Map<String, Object> oneData : completeOrderNoList)
			{
				String completeOrderNo= oneData.get("completeOrderNo").toString();
				String remark5VStr= oneData.get("remark5VStr").toString();

				JSONObject body = new JSONObject();
				body.put("completeOrderNo", completeOrderNo);//订单编号
				//配送状态，
				//(可出貨、
				//已確認指定配送日、
				//暫時缺貨、
				//無貨取消訂單、
				//收件資料有誤/不配送地區、
				//超才、
				//需採購協助事項、
				//自行備註，不需momo協助)
				body.put("remark5VStr", remark5VStr);//配送状态

				//
				sendInfoList.put(body);				
			}

			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderunsendStoresFinish", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONObject resultInfo=jsonres.getJSONObject("resultInfo");
				//未執行筆數
				String undoCnt=resultInfo.getString("undoCnt");
				JSONArray undoList=resultInfo.getJSONArray("undoList");//未執行列表
				for(int a=0;a<undoList.length();a++)
				{
					String sUndoList=undoList.getString(a);

					//System.out.println("未执行的列表:"+sUndoList);					
				}

				//出庫成功筆數
				String confirmOkCnt=resultInfo.getString("confirmOkCnt");
				JSONArray confirmOkList=resultInfo.getJSONArray("confirmOkList");//出庫成功列表
				for(int a=0;a<confirmOkList.length();a++)
				{
					String sConfirmOkList=confirmOkList.getString(a);

					//System.out.println("出庫成功列表:"+sConfirmOkList);					
				}

				//出庫失敗筆數
				String confirmFailCnt=resultInfo.getString("confirmFailCnt");
				JSONArray confirmFailList=resultInfo.getJSONArray("confirmFailList");//出庫失敗列表
				for(int a=0;a<confirmFailList.length();a++)
				{
					String sConfirmFailList=confirmFailList.getString(a);

					//System.out.println("出庫失敗列表:"+sConfirmFailList);					
				}

				//重複操作筆數
				String confirmRepeatCnt=resultInfo.getString("confirmRepeatCnt");

				JSONArray confirmRepeatList=resultInfo.getJSONArray("confirmRepeatList");//重複操作列表
				for(int a=0;a<confirmRepeatList.length();a++)
				{
					String sConfirmRepeatList=confirmRepeatList.getString(a);

					//System.out.println("重複操作列表:"+sConfirmRepeatList);					
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
	 * 未出貨訂單-超商取貨-列印Pdf
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param printType 列印類別 label:列印標籤 dt:列印明細  all:列印出貨總表
	 * @param completeOrderNoList (completeOrderNo：订单编号  )
	 * @return
	 */
	public String OrderunsendStoresPrintPdf(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String printType,List<Map<String, Object>> completeOrderNoList)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendStoresPrintPdf");
			//列印類別
			//label:列印標籤
			//dt:列印明細
			//all:列印出貨總表
			header.put("printType", printType);

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();

			for (Map<String, Object> oneData : completeOrderNoList)
			{
				String completeOrderNo= oneData.get("completeOrderNo").toString();

				JSONObject body = new JSONObject();
				body.put("completeOrderNo", completeOrderNo);//订单编号

				//
				sendInfoList.put(body);				
			}

			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderunsendStoresPrintPdf", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{

				//未執行筆數
				String undoCnt=jsonres.getString("undoCnt");
				JSONArray undoList=jsonres.getJSONArray("undoList");//未執行列表
				for(int a=0;a<undoList.length();a++)
				{
					String sUndoList=undoList.getString(a);

					//System.out.println("未执行的列表:"+sUndoList);					
				}

				//PDF檔串流
				String pdfData=jsonres.getString("pdfData");

			}

		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;	

	}


	/**
	 * 未出貨訂單-第三方物流-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param third_fr_dd  转单日起，yyyy/MM/dd
	 * @param third_fr_hh  转单日起，时
	 * @param third_fr_mm  转单日起，分
	 * @param third_to_dd  转单日讫，yyyy/MM/dd
	 * @param third_to_hh  转单日讫，时
	 * @param third_to_mm  转单日讫，分
	 * @param third_orderGb 訂單類別 空值(“”): 全部 10: 一般訂單 40: 交換配送訂單
	 * @param third_delyGb 物流商 61: 宅配通 62: 新竹 63: 宅急便
	 * @param third_delyTemp 配送溫層 01: 常溫 02: 冷凍 03: 冷藏
	 * @return
	 */
	public String GetOrderunsendThirdQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String third_fr_dd,String third_fr_hh,String third_fr_mm,String third_to_dd,String third_to_hh,String third_to_mm,String third_orderGb,String third_delyGb,String third_delyTemp)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendThirdQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();
			sendInfo.put("third_fr_dd", third_fr_dd);//转单日起2016/03/18 最大区间2个月
			sendInfo.put("third_fr_hh", third_fr_hh);//00
			sendInfo.put("third_fr_mm", third_fr_mm);//00
			sendInfo.put("third_to_dd", third_to_dd);//转单日讫2016/03/18
			sendInfo.put("third_to_hh", third_to_hh);//23
			sendInfo.put("third_to_mm", third_to_mm);//59

			sendInfo.put("third_orderGb", third_orderGb);//訂單類別 空值(“”): 全部 10: 一般訂單 40: 交換配送訂單
			sendInfo.put("third_delyGb", third_delyGb);//物流商 61: 宅配通 62: 新竹 63: 宅急便
			sendInfo.put("third_delyTemp", third_delyTemp);//配送溫層 01: 常溫 02: 冷凍 03: 冷藏

			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrderunsendThirdQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{

					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次
					String isRedStr=dataList.getJSONObject(a).getString("isRedStr");//灯号，正常
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001
					String remark5VStr=dataList.getJSONObject(a).getString("remark5VStr");//配送状态
					String msgNote=dataList.getJSONObject(a).getString("msgNote");//配送讯息，无意义
					String scmDelyDate=dataList.getJSONObject(a).getString("scmDelyDate");//约定配送日

					String orderDelyGbName=dataList.getJSONObject(a).getString("orderDelyGbName");//物流公司
					String outplacePost=dataList.getJSONObject(a).getString("outplacePost");//出货地址邮编
					String outplaceAddr=dataList.getJSONObject(a).getString("outplaceAddr");//出货地址
					String outplaceRtnPost=dataList.getJSONObject(a).getString("outplaceRtnPost");//回收地址邮编
					String outplaceRtnAddr=dataList.getJSONObject(a).getString("outplaceRtnAddr");//回收地址
					String orderGbStr=dataList.getJSONObject(a).getString("orderGbStr");//订单类别
					String msg=dataList.getJSONObject(a).getString("msg");//客户配送需求					
					String receiverMask=dataList.getJSONObject(a).getString("receiverMask");//收件人
					String postNo=dataList.getJSONObject(a).getString("postNo");//收件人邮政编码
					String receiverAddrMask=dataList.getJSONObject(a).getString("receiverAddrMask");//收件人地址
					String lastPricDate=dataList.getJSONObject(a).getString("lastPricDate");//转单日	
					String delyHopeDate=dataList.getJSONObject(a).getString("delyHopeDate");//預計送達日	
					String entpGoodsNo=dataList.getJSONObject(a).getString("entpGoodsNo");//商品原厂编码
					String goodsCode=dataList.getJSONObject(a).getString("goodsCode");//商品编码
					String goodsName=dataList.getJSONObject(a).getString("goodsName");//商品名称
					String goodsDtCode=dataList.getJSONObject(a).getString("goodsDtCode");//单品编码，001
					String goodsDtInfo=dataList.getJSONObject(a).getString("goodsDtInfo");//单品详细，無
					String syslast=dataList.getJSONObject(a).getString("syslast");//数量
					String buyPrice=dataList.getJSONObject(a).getString("buyPrice");//进价含税
					String describeNote=dataList.getJSONObject(a).getString("describeNote");//赠品，
					String custNameMask=dataList.getJSONObject(a).getString("custNameMask");//订购人，林O筠
					String invoiceNo=dataList.getJSONObject(a).getString("invoiceNo");//发票号码，JF90774668
					String invoiceDate=dataList.getJSONObject(a).getString("invoiceDate");//发票日期，2014/08/29
					String custId=dataList.getJSONObject(a).getString("custId");//个人识别码，5743
					String packYnStr=dataList.getJSONObject(a).getString("packYnStr");//群组变价商品，否
					String boxYn=dataList.getJSONObject(a).getString("boxYn");//箱号
					String claimReason=dataList.getJSONObject(a).getString("claimReason");//換貨原因
					String scm_msg=dataList.getJSONObject(a).getString("scm_msg");//宅單備註


					//System.out.println(completeOrderNo+":"+isRedStr);

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
	 * 未出貨訂單-第三方物流-貨源確認
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param completeOrderNoList (completeOrderNo：订单编号，
	 *  remark5VStr：配送状态，
	 *  msgNote：配送訊息 【(可出貨、已確認指定配送日、暫時缺貨、無貨取消訂單、收件資料有誤/不配送地區、已印單，未出貨客辦退、需採購協助事項、自行備註，不需momo協助)】
	 *  scmDelyDate：约定配送日 【當配送狀態為「已確認指定配送日」則必填】
	 *  scm_msg：备注  )
	 * @return
	 */
	public String OrderunsendThirdCheckGoods(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,List<Map<String, Object>> completeOrderNoList)
	{
		//
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendThirdCheckGoods");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();

			for (Map<String, Object> oneData : completeOrderNoList)
			{				
				String completeOrderNo= oneData.get("completeOrderNo").toString();
				String remark5VStr= oneData.get("remark5VStr").toString();
				String msgNote= oneData.get("msgNote").toString();
				String scmDelyDate= "";
				if (remark5VStr.equals("已確認指定配送日")) 
				{
					scmDelyDate= oneData.get("scmDelyDate").toString();
				}
				String scm_msg= oneData.get("scm_msg").toString();

				JSONObject body = new JSONObject();

				body.put("completeOrderNo", completeOrderNo);//订单编号
				//配送状态，
				//(可出貨、
				//已確認指定配送日、
				//暫時缺貨、
				//無貨取消訂單、
				//收件資料有誤/不配送地區、
				//超才、
				//需採購協助事項、
				//自行備註，不需momo協助)
				body.put("remark5VStr", remark5VStr);
				body.put("msgNote", msgNote);//配送讯息
				
				if (remark5VStr.equals("已確認指定配送日")) 
				{
					body.put("scmDelyDate", scmDelyDate);//
				}
				
				body.put("scm_msg", scm_msg);//				
				

				//
				sendInfoList.put(body);
			}


			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderunsendThirdCheckGoods", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONObject resultInfo=jsonres.getJSONObject("resultInfo");
				//未執行筆數
				String undoCnt=resultInfo.getString("undoCnt");
				JSONArray undoList=resultInfo.getJSONArray("undoList");//未執行列表
				for(int a=0;a<undoList.length();a++)
				{
					String sUndoList=undoList.getString(a);

					//System.out.println("未执行的列表:"+sUndoList);					
				}			

				//保存成功笔数
				String saveMsgOkCnt=resultInfo.getString("saveMsgOkCnt");

				//保存失败笔数
				String saveMsgFailCnt=resultInfo.getString("saveMsgFailCnt");
				JSONArray saveMsgFailList=resultInfo.getJSONArray("saveMsgFailList");//保存失敗列表
				for(int a=0;a<saveMsgFailList.length();a++)
				{
					String sSaveMsgFailList=saveMsgFailList.getString(a);

					//System.out.println("保存失敗列表:"+sSaveMsgFailList);					
				}


				//建立商談成功筆數
				String addCounselOkCnt=resultInfo.getString("addCounselOkCnt");

				//建立商談失敗筆數
				String addCounselFailCnt=resultInfo.getString("addCounselFailCnt");
				JSONArray addCounselFailList=resultInfo.getJSONArray("addCounselFailList");//建立商談失敗列表
				for(int a=0;a<addCounselFailList.length();a++)
				{
					String sAddCounselFailList=addCounselFailList.getString(a);

					//System.out.println("建立商談失敗列表:"+sAddCounselFailList);					
				}


				//可接單量調整為0&申請下市成功筆數
				String setZeroOkCnt=resultInfo.getString("setZeroOkCnt");

				//可接單量調整為0&申請下市失敗筆數
				String setZeroFailCount=resultInfo.getString("setZeroFailCount");
				JSONArray setZeroMsgList=resultInfo.getJSONArray("setZeroMsgList");//可接單量調整為0&申請下市失敗列表
				for(int a=0;a<setZeroMsgList.length();a++)
				{
					String sSetZeroMsgList=setZeroMsgList.getString(a);

					//System.out.println("可接單量調整為0&申請下市失敗列表:"+sSetZeroMsgList);					
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
	 * 未出貨訂單-第三方物流-併箱
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param completeOrderNoList (completeOrderNo：订单编号 ,boxYn：箱号 ,remark5VStr：配送状态 )
	 * @return
	 */
	public String OrderunsendThirdCombineBox(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,List<Map<String, Object>> completeOrderNoList)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendThirdCombineBox");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();

			for (Map<String, Object> oneData : completeOrderNoList)
			{
				String completeOrderNo= oneData.get("completeOrderNo").toString();
				String boxYn= oneData.get("boxYn").toString();
				String remark5VStr= oneData.get("remark5VStr").toString();

				JSONObject body = new JSONObject();
				body.put("completeOrderNo", completeOrderNo);//订单编号
				//配送状态，
				//(可出貨、
				//已確認指定配送日、
				//暫時缺貨、
				//無貨取消訂單、
				//收件資料有誤/不配送地區、
				//超才、
				//需採購協助事項、
				//自行備註，不需momo協助)
				body.put("boxYn", boxYn);
				body.put("remark5VStr", remark5VStr);//配送讯息

				//
				sendInfoList.put(body);				
			}

			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderunsendThirdCombineBox", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONObject resultInfo=jsonres.getJSONObject("resultInfo");
				//未執行筆數
				String undoCnt=resultInfo.getString("undoCnt");
				JSONArray undoList=resultInfo.getJSONArray("undoList");//未執行列表
				for(int a=0;a<undoList.length();a++)
				{
					String sUndoList=undoList.getString(a);

					//System.out.println("未执行的列表:"+sUndoList);					
				}			

				//併箱成功笔数
				String combineOkCnt=resultInfo.getString("combineOkCnt");

				//併箱失败笔数
				String combineFailCnt=resultInfo.getString("combineFailCnt");
				JSONArray combineFailList=resultInfo.getJSONArray("combineFailList");//併箱失敗列表
				for(int a=0;a<combineFailList.length();a++)
				{
					String sCombineFailList=combineFailList.getString(a);

					//System.out.println("併箱失敗列表:"+sCombineFailList);					
				}


				//箱號重複筆數
				String combineUsedCnt=resultInfo.getString("combineUsedCnt");

				JSONArray addCounselFailList=resultInfo.getJSONArray("addCounselFailList");//箱號重複列表
				for(int a=0;a<addCounselFailList.length();a++)
				{
					String sAddCounselFailList=addCounselFailList.getString(a);

					//System.out.println("箱號重複列表列表:"+sAddCounselFailList);					
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
	 * 未出貨訂單-第三方物流-出貨確認
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param completeOrderNoList (completeOrderNo：订单编号 ,remark5VStr：配送状态 )
	 * @return
	 */
	public String OrderunsendThirdFinish(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,List<Map<String, Object>> completeOrderNoList)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendThirdFinish");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();

			for (Map<String, Object> oneData : completeOrderNoList)
			{
				String completeOrderNo= oneData.get("completeOrderNo").toString();
				String remark5VStr= oneData.get("remark5VStr").toString();

				JSONObject body = new JSONObject();
				body.put("completeOrderNo", completeOrderNo);//订单编号
				//配送状态，
				//(可出貨、
				//已確認指定配送日、
				//暫時缺貨、
				//無貨取消訂單、
				//收件資料有誤/不配送地區、
				//超才、
				//需採購協助事項、
				//自行備註，不需momo協助)
				body.put("remark5VStr", remark5VStr);//配送状态

				//
				sendInfoList.put(body);				
			}

			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderunsendThirdFinish", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONObject resultInfo=jsonres.getJSONObject("resultInfo");
				//未執行筆數
				String undoCnt=resultInfo.getString("undoCnt");
				JSONArray undoList=resultInfo.getJSONArray("undoList");//未執行列表
				for(int a=0;a<undoList.length();a++)
				{
					String sUndoList=undoList.getString(a);

					//System.out.println("未执行的列表:"+sUndoList);					
				}

				//出庫成功筆數
				String confirmOkCnt=resultInfo.getString("confirmOkCnt");
				JSONArray confirmOkList=resultInfo.getJSONArray("confirmOkList");//出庫成功列表
				for(int a=0;a<confirmOkList.length();a++)
				{
					String sConfirmOkList=confirmOkList.getString(a);

					//System.out.println("出庫成功列表:"+sConfirmOkList);					
				}

				//出庫失敗筆數
				String confirmFailCnt=resultInfo.getString("confirmFailCnt");
				JSONArray confirmFailList=resultInfo.getJSONArray("confirmFailList");//出庫失敗列表
				for(int a=0;a<confirmFailList.length();a++)
				{
					String sConfirmFailList=confirmFailList.getString(a);

					//System.out.println("出庫失敗列表:"+sConfirmFailList);					
				}

				//重複操作筆數
				String confirmRepeatCnt=resultInfo.getString("confirmRepeatCnt");

				JSONArray confirmRepeatList=resultInfo.getJSONArray("confirmRepeatList");//重複操作列表
				for(int a=0;a<confirmRepeatList.length();a++)
				{
					String sConfirmRepeatList=confirmRepeatList.getString(a);

					//System.out.println("重複操作列表:"+sConfirmRepeatList);					
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
	 * 未出貨訂單-第三方物流-列印Pdf
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param printType 列印類別 label:列印標籤 dt:列印明細  all:列印出貨總表
	 * @param completeOrderNoList (completeOrderNo：订单编号  )
	 * @return
	 */
	public String OrderunsendThirdPrintPdf(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String printType,List<Map<String, Object>> completeOrderNoList)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "unsendThirdPrintPdf");
			//列印類別
			//label:列印標籤
			//dt:列印明細
			//all:列印出貨總表
			header.put("printType", printType);

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();

			for (Map<String, Object> oneData : completeOrderNoList)
			{
				String completeOrderNo= oneData.get("completeOrderNo").toString();

				JSONObject body = new JSONObject();
				body.put("completeOrderNo", completeOrderNo);//订单编号

				//
				sendInfoList.put(body);				
			}

			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderunsendThirdPrintPdf", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{

				//未執行筆數
				String undoCnt=jsonres.getString("undoCnt");
				JSONArray undoList=jsonres.getJSONArray("undoList");//未執行列表
				for(int a=0;a<undoList.length();a++)
				{
					String sUndoList=undoList.getString(a);

					//System.out.println("未执行的列表:"+sUndoList);					
				}

				//PDF檔串流
				String pdfData=jsonres.getString("pdfData");

			}

		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;	

	}

	/**
	 * 拦货订单查询
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param receiver 收件人姓名 ，可为""
	 * @param goodsCode 商品编号 可为""
	 * @param orderNo 订单编号 可为""
	 * @param entp_goods_no 原厂编号  可为""
	 * @return
	 */
	public String GetOrderstopSendingQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String receiver,String goodsCode,String orderNo,String entp_goods_no)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "stopSendingQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();

			sendInfo.put("receiver", receiver);//收件人姓名
			sendInfo.put("goodsCode", goodsCode);//商品编号
			sendInfo.put("orderNo", orderNo);//订单编号
			sendInfo.put("entp_goods_no", entp_goods_no);//原厂编号

			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrderstopSendingQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{

					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次
					String allowExecuteYN=dataList.getJSONObject(a).getString("allowExecuteYN");//“Y”:可執行「註記確定攔貨」或「註記來不及攔貨」	“N”:不可執行「註記確定攔貨」或「註記來不及攔貨」
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001
					String notAllowReason=dataList.getJSONObject(a).getString("notAllowReason");//不可執行攔貨原因,待排程處理等
					String returnReason=dataList.getJSONObject(a).getString("returnReason");//銷退原因					
					String sreceiver=dataList.getJSONObject(a).getString("receiver");//收件人			
					String entpGoodsNo=dataList.getJSONObject(a).getString("entpGoodsNo");//商品原厂编码
					String sgoodsCode=dataList.getJSONObject(a).getString("goodsCode");//商品编码
					String goodsName=dataList.getJSONObject(a).getString("goodsName");//商品名称
					String goodsDtCode=dataList.getJSONObject(a).getString("goodsDtCode");//单品编码，001
					String goodsDtInfo=dataList.getJSONObject(a).getString("goodsDtInfo");//单品详细，無
					String syslast=dataList.getJSONObject(a).getString("syslast");//数量
					String msgNote=dataList.getJSONObject(a).getString("msgNote");//配送訊息					
					String packYnStr=dataList.getJSONObject(a).getString("packYnStr");//群组变价商品，否		

					//System.out.println(completeOrderNo);

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
	 * 拦货订单-確認拦货(廠商配送且非精品倉商品才可攔貨)
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param completeOrderNoList (completeOrderNo：订单编号 ，stopAct：攔貨狀況【1: 確定攔貨  0:來不及攔貨】 )
	 * @return
	 */
	public String OrderstopSendingConfirm(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,List<Map<String, Object>> completeOrderNoList)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "stopSendingConfirm");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();

			for (Map<String, Object> oneData : completeOrderNoList)
			{
				String completeOrderNo= oneData.get("completeOrderNo").toString();
				String stopAct= oneData.get("stopAct").toString();

				JSONObject body = new JSONObject();
				body.put("completeOrderNo", completeOrderNo);//订单编号

				body.put("stopAct", stopAct);//攔貨狀況【1: 確定攔貨  0:來不及攔貨】

				//
				sendInfoList.put(body);				
			}

			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderstopSendingConfirm", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONObject resultInfo=jsonres.getJSONObject("resultInfo");
				//未執行筆數
				String undoCnt=resultInfo.getString("undoCnt");
				JSONArray undoList=resultInfo.getJSONArray("undoList");//未執行列表
				for(int a=0;a<undoList.length();a++)
				{
					String sUndoList=undoList.getString(a);

					//System.out.println("未执行的列表:"+sUndoList);					
				}

				//成功筆數
				String okCnt=resultInfo.getString("okCnt");
				JSONArray okList=resultInfo.getJSONArray("okList");//成功列表
				for(int a=0;a<okList.length();a++)
				{
					String sOkList=okList.getString(a);

					//System.out.println("成功列表:"+sOkList);					
				}

				//失敗筆數
				String failCnt=resultInfo.getString("failCnt");
				JSONArray failList=resultInfo.getJSONArray("failList");//失敗列表
				for(int a=0;a<failList.length();a++)
				{
					String sFailList=failList.getString(a);

					//System.out.println("失敗列表:"+sFailList);					
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
	 * 未回收訂單-廠商配送-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param company_fr_dd 转单日起，yyyy/MM/dd
	 * @param company_fr_hh 转单日起，时
	 * @param company_fr_mm 转单日起，分
	 * @param company_to_dd 转单日讫，yyyy/MM/dd
	 * @param company_to_hh 转单日讫，时
	 * @param company_to_mm 转单日讫，分
	 * @param company_claimGb 訂單類別 空值(“”): 全部  30: 一般回收訂單  45: 交換回收訂單
	 * @return
	 */
	public String GetOrderrecoverCompanyQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String company_fr_dd,String company_fr_hh,String company_fr_mm,String company_to_dd,String company_to_hh,String company_to_mm,String company_claimGb)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "recoverCompanyQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();
			sendInfo.put("company_fr_dd", company_fr_dd);//转单日起2016/03/18 最大区间2个月
			sendInfo.put("company_fr_hh", company_fr_hh);//00
			sendInfo.put("company_fr_mm", company_fr_mm);//00
			sendInfo.put("company_to_dd", company_to_dd);//转单日讫2016/03/18
			sendInfo.put("company_to_hh", company_to_hh);//23
			sendInfo.put("company_to_mm", company_to_mm);//59
			sendInfo.put("company_claimGb", company_claimGb);//訂單類別 空值(“”): 全部  30: 一般回收訂單  45: 交換回收訂單


			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrderrecoverCompanyQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{
					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次
					String isRedStr=dataList.getJSONObject(a).getString("isRedStr");//灯号，正常
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001
					//回收狀態
					String remark5VStr=dataList.getJSONObject(a).getString("remark5VStr");
					String msgNote=dataList.getJSONObject(a).getString("msgNote");//回收訊息					
					String returnReason=dataList.getJSONObject(a).getString("returnReason");//銷退原因					
					String delyGbStr=dataList.getJSONObject(a).getString("delyGbStr");//物流公司
					String slipNo=dataList.getJSONObject(a).getString("slipNo");//配送编号,回收單號
					String claimGbStr=dataList.getJSONObject(a).getString("claimGbStr");//订单类别		
					String msg=dataList.getJSONObject(a).getString("msg");//客户配送需求，(週一不配送)
					String lastPricDate=dataList.getJSONObject(a).getString("lastPricDate");//转单日，2014/08/29
					String delyHopeDate=dataList.getJSONObject(a).getString("delyHopeDate");//预计回收日
					String receiver=dataList.getJSONObject(a).getString("receiver");//收件人
					String receiverTel=dataList.getJSONObject(a).getString("receiverTel");//收件人电话
					String receiverMobile=dataList.getJSONObject(a).getString("receiverMobile");//收件人移动电话
					String postNo=dataList.getJSONObject(a).getString("postNo");//收件人邮政编码
					String receiverAddr=dataList.getJSONObject(a).getString("receiverAddr");//收件人地址
					String entpGoodsNo=dataList.getJSONObject(a).getString("entpGoodsNo");//商品原厂编码
					String goodsCode=dataList.getJSONObject(a).getString("goodsCode");//商品编码
					String goodsName=dataList.getJSONObject(a).getString("goodsName");//商品名称
					String goodsDtCode=dataList.getJSONObject(a).getString("goodsDtCode");//单品编码，001
					String goodsDtInfo=dataList.getJSONObject(a).getString("goodsDtInfo");//单品详细，無
					String syslast=dataList.getJSONObject(a).getString("syslast");//数量
					String buyPrice=dataList.getJSONObject(a).getString("buyPrice");//进价含税
					String describeNote=dataList.getJSONObject(a).getString("describeNote");//赠品，
					String custName=dataList.getJSONObject(a).getString("custName");//订购人，林O筠				
					String packYnStr=dataList.getJSONObject(a).getString("packYnStr");//群组变价商品，否

					//System.out.println(completeOrderNo+":"+isRedStr);

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
	 * 未回收訂單-廠商配送-回收確認
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param completeOrderNoList (completeOrderNo：订单编号 ，
	 * remark5VStr：回收狀態 【(已回收、電聯不上、指定回收日、回收異常、商品收回檢測中、收件資料有誤、客要取消退貨、須採購行政協助事項、供應商備註、momo通知取消退貨)】，
	 * msgNote：回收訊息，
	 * delyGbStr：物流公司 【(宅配通、新竹貨運、統一速達、大榮貨運、東元物流、其他宅配、郵局、便利袋、其他)】，
	 * slipNo：回收單號 )
	 * @return
	 */
	public String OrderrecoverCompanyConfirm(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,List<Map<String, Object>> completeOrderNoList)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "recoverCompanyConfirm");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			//
			JSONArray sendInfoList = new JSONArray();

			for (Map<String, Object> oneData : completeOrderNoList)
			{
				String completeOrderNo= oneData.get("completeOrderNo").toString();//订单编号
				String remark5VStr= oneData.get("remark5VStr").toString();//回收状态
				String msgNote= oneData.get("msgNote").toString();//回收讯息
				String delyGbStr= oneData.get("delyGbStr").toString();//物流厂商
				String slipNo= oneData.get("slipNo").toString();//配送编号

				JSONObject body = new JSONObject();
				body.put("completeOrderNo", completeOrderNo);//订单编号
				body.put("remark5VStr", remark5VStr);
				body.put("msgNote", msgNote);
				body.put("delyGbStr", delyGbStr);
				body.put("slipNo", slipNo);

				//
				sendInfoList.put(body);				
			}

			header.put("sendInfoList", sendInfoList);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("OrderrecoverCompanyConfirm", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONObject resultInfo=jsonres.getJSONObject("resultInfo");
				//回收成功筆數
				String confirmOkCnt=resultInfo.getString("confirmOkCnt");
				//回收失敗筆數
				String confirmFailCnt=resultInfo.getString("confirmFailCnt");
				JSONArray confirmFailList=resultInfo.getJSONArray("confirmFailList");//回收失敗列表
				for(int a=0;a<confirmFailList.length();a++)
				{
					String sConfirmFailList=confirmFailList.getString(a);

					//System.out.println("回收失敗列表:"+sConfirmFailList);					
				}

				//儲存訊息成功筆數
				String saveMsgOkCnt=resultInfo.getString("saveMsgOkCnt");
				//儲存失敗訊息筆數
				String saveMsgFailCnt=resultInfo.getString("saveMsgFailCnt");

				JSONArray saveMsgFailList=resultInfo.getJSONArray("saveMsgFailList");//儲存失敗訊息列表
				for(int a=0;a<saveMsgFailList.length();a++)
				{
					String sSaveMsgFailList=saveMsgFailList.getString(a);

					//System.out.println("儲存失敗訊息:"+sSaveMsgFailList);					
				}

				//建立商談成功筆數
				String addCounselOkCnt=resultInfo.getString("addCounselOkCnt");
				//建立商談失敗筆數
				String addCounselFailCnt=resultInfo.getString("addCounselFailCnt");
				JSONArray addCounselFailList=resultInfo.getJSONArray("addCounselFailList");//建立商談失敗列表
				for(int a=0;a<addCounselFailList.length();a++)
				{
					String sAddCounselFailList=addCounselFailList.getString(a);

					//System.out.println("建立商談失敗列表:"+sAddCounselFailList);					
				}			



				//建立商談上傳圖檔失敗筆數
				String addCounselFileFailCnt=resultInfo.getString("addCounselFileFailCnt");

				JSONArray counselFileFailList=resultInfo.getJSONArray("counselFileFailList");//建立商談上傳圖檔失敗列表
				for(int a=0;a<counselFileFailList.length();a++)
				{
					String sCounselFileFailList=counselFileFailList.getString(a);

					//System.out.println("建立商談上傳圖檔失敗列表:"+sCounselFileFailList);					
				}			


				//取消退貨成功筆數
				String addAbnormaOkCnt=resultInfo.getString("addAbnormaOkCnt");
				//取消退貨失敗筆數
				String addAbnormaFailCnt=resultInfo.getString("addAbnormaFailCnt");
				JSONArray addAbnormaFailList=resultInfo.getJSONArray("addAbnormaFailList");//取消退貨失敗列表
				for(int a=0;a<addAbnormaFailList.length();a++)
				{
					String sAddAbnormaFailList=addAbnormaFailList.getString(a);

					//System.out.println("取消退貨失敗列表:"+sAddAbnormaFailList);					
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
	 * 未回收訂單-超商取貨-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param stores_fr_dd 转单日起，yyyy/MM/dd
	 * @param stores_fr_hh 转单日起，时
	 * @param stores_fr_mm 转单日起，分
	 * @param stores_to_dd 转单日讫，yyyy/MM/dd
	 * @param stores_to_hh 转单日讫，时
	 * @param stores_to_mm 转单日讫，分
	 * @param stores_receiver 收件人
	 * @param stores_claimGb 订单类别 空值(“”): 全部 30: 一般回收訂單 45: 交換回收訂單 
	 * @param dely_gb 门市类型 21:7-11 23:便利達康

	 * @return
	 */
	public String GetOrderrecoverStoresQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String stores_fr_dd,String stores_fr_hh,String stores_fr_mm,String stores_to_dd,String stores_to_hh,String stores_to_mm,String stores_receiver,String stores_claimGb,String dely_gb)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "recoverStoresQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();
			sendInfo.put("stores_fr_dd", stores_fr_dd);//转单日起2016/03/18 最大区间2个月
			sendInfo.put("stores_fr_hh", stores_fr_hh);//00
			sendInfo.put("stores_fr_mm", stores_fr_mm);//00
			sendInfo.put("stores_to_dd", stores_to_dd);//转单日讫2016/03/18
			sendInfo.put("stores_to_hh", stores_to_hh);//23
			sendInfo.put("stores_to_mm", stores_to_mm);//59
			sendInfo.put("stores_receiver", stores_receiver);//
			sendInfo.put("stores_claimGb", stores_claimGb);//
			sendInfo.put("dely_gb", dely_gb);//

			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrderrecoverStoresQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{

					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次
					String isRedStr=dataList.getJSONObject(a).getString("isRedStr");//灯号，正常				
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001
					String remark5VStr=dataList.getJSONObject(a).getString("remark5VStr");//回收状态
					String msgNote=dataList.getJSONObject(a).getString("msgNote");//回收讯息
					String delyGbStr=dataList.getJSONObject(a).getString("delyGbStr");//物流公司
					String slipNo=dataList.getJSONObject(a).getString("slipNo");//配送编号					
					String returnReason=dataList.getJSONObject(a).getString("returnReason");//销退原因						
					String claimGbStr=dataList.getJSONObject(a).getString("claimGbStr");//订单类别，一般訂單
					String msg=dataList.getJSONObject(a).getString("msg");//客户配送需求，(週一不配送)
					String lastPricDate=dataList.getJSONObject(a).getString("lastPricDate");//转单日，2014/08/29
					String delyHopeDate=dataList.getJSONObject(a).getString("delyHopeDate");//预计送达日					
					String receiverMask=dataList.getJSONObject(a).getString("receiverMask");//收件人
					String storeAddr=dataList.getJSONObject(a).getString("storeAddr");//门市地址					
					String entpGoodsNo=dataList.getJSONObject(a).getString("entpGoodsNo");//商品原厂编码
					String goodsCode=dataList.getJSONObject(a).getString("goodsCode");//商品编码
					String goodsName=dataList.getJSONObject(a).getString("goodsName");//商品名称
					String goodsDtCode=dataList.getJSONObject(a).getString("goodsDtCode");//单品编码，001
					String goodsDtInfo=dataList.getJSONObject(a).getString("goodsDtInfo");//单品详细，無
					String syslast=dataList.getJSONObject(a).getString("syslast");//数量
					String buyPrice=dataList.getJSONObject(a).getString("buyPrice");//进价含税
					String describeNote=dataList.getJSONObject(a).getString("describeNote");//赠品，
					String custNameMask=dataList.getJSONObject(a).getString("custNameMask");//订购人，林O筠					
					String packYnStr=dataList.getJSONObject(a).getString("packYnStr");//群组变价商品，否


					//System.out.println(completeOrderNo+":"+isRedStr);

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
	 * 未回收訂單-第三方物流-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param third_fr_dd  转单日起，yyyy/MM/dd
	 * @param third_fr_hh  转单日起，时
	 * @param third_fr_mm  转单日起，分
	 * @param third_to_dd  转单日讫，yyyy/MM/dd
	 * @param third_to_hh  转单日讫，时
	 * @param third_to_mm  转单日讫，分
	 * @param third_receiver 收件人
	 * @param third_orderGb 訂單類別 空值(“”): 全部 30: 一般回收訂單 45: 交換回收訂單
	 * @param third_delyGb 物流商 61: 宅配通 62: 新竹 63: 宅急便
	 * @param third_status 訂單狀態 2:物流回收中 3:商品已退回供應商 4:已回收確認 5:回收異常
	 * @return
	 */
	public String GetOrderrecoverThirdQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String third_fr_dd,String third_fr_hh,String third_fr_mm,String third_to_dd,String third_to_hh,String third_to_mm,String third_receiver,String third_orderGb,String third_delyGb,String third_status)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "recoverThirdQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();
			sendInfo.put("third_fr_dd", third_fr_dd);//转单日起2016/03/18 最大区间2个月
			sendInfo.put("third_fr_hh", third_fr_hh);//00
			sendInfo.put("third_fr_mm", third_fr_mm);//00
			sendInfo.put("third_to_dd", third_to_dd);//转单日讫2016/03/18
			sendInfo.put("third_to_hh", third_to_hh);//23
			sendInfo.put("third_to_mm", third_to_mm);//59

			sendInfo.put("third_receiver", third_receiver);//收件人
			sendInfo.put("third_orderGb", third_orderGb);//訂單類別 空值(“”): 全部 30: 一般回收訂單 45: 交換回收訂單
			sendInfo.put("third_delyGb", third_delyGb);//物流商 61: 宅配通 62: 新竹 63: 宅急便
			sendInfo.put("third_status", third_status);//訂單狀態 2:物流回收中 3:商品已退回供應商 4:已回收確認 5:回收異常

			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrderThirdQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{

					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次					
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001

					String remark5VStr=dataList.getJSONObject(a).getString("remark5VStr");//回收状态
					String msgNote=dataList.getJSONObject(a).getString("msgNote");//回收讯息，
					String Addr=dataList.getJSONObject(a).getString("Addr");//指定回收地址
					String slipNo=dataList.getJSONObject(a).getString("slipNo");//配送编号
					String returnReason=dataList.getJSONObject(a).getString("returnReason");//退货原因
					String claimGbStr=dataList.getJSONObject(a).getString("claimGbStr");//订单类别
					String msg=dataList.getJSONObject(a).getString("msg");//客户配送需求					
					String receiverMask=dataList.getJSONObject(a).getString("receiverMask");//收件人						
					String lastPricDate=dataList.getJSONObject(a).getString("lastPricDate");//转单日	
					String delyHopeDate=dataList.getJSONObject(a).getString("delyHopeDate");//預計送達日	
					String entpGoodsNo=dataList.getJSONObject(a).getString("entpGoodsNo");//商品原厂编码
					String goodsCode=dataList.getJSONObject(a).getString("goodsCode");//商品编码
					String goodsName=dataList.getJSONObject(a).getString("goodsName");//商品名称
					String goodsDtCode=dataList.getJSONObject(a).getString("goodsDtCode");//单品编码，001
					String goodsDtInfo=dataList.getJSONObject(a).getString("goodsDtInfo");//单品详细，無
					String syslast=dataList.getJSONObject(a).getString("syslast");//数量
					String buyPrice=dataList.getJSONObject(a).getString("buyPrice");//进价含税					
					String packYnStr=dataList.getJSONObject(a).getString("packYnStr");//群组变价商品，否

					//System.out.println(completeOrderNo);

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
	 * 出貨中訂單-貴重/精品-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param fromDate 转单日起，yyyy/MM/dd
	 * @param fromHour 转单日起，时
	 * @param fromMinute 转单日起，分
	 * @param toDate 转单日讫，yyyy/MM/dd
	 * @param toHour 转单日讫，时
	 * @param toMinute 转单日讫，分
	 * @param receiver 收件人
	 * @param orderNo 订单号
	 * @param status 状态  1:已印單 2:配送中
	 * @return
	 */
	public String GetOrdersendingCompanyQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String fromDate,String fromHour,String fromMinute,String toDate,String toHour,String toMinute,String receiver,String orderNo,String status)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "sendingCompanyQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();
			sendInfo.put("fromDate", fromDate);//转单日起2016/03/18 最大区间2个月
			sendInfo.put("fromHour", fromHour);//00
			sendInfo.put("fromMinute", fromMinute);//00
			sendInfo.put("toDate", toDate);//转单日讫2016/03/18
			sendInfo.put("toHour", toHour);//23
			sendInfo.put("toMinute", toMinute);//59

			sendInfo.put("receiver", receiver);//收件人
			sendInfo.put("orderNo", orderNo);//订单号
			sendInfo.put("status", status);// 状态  1:已印單 2:配送中

			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrdersendingCompanyQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{

					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次					
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001
					String slip_i_no=dataList.getJSONObject(a).getString("slip_i_no");//出货单号
					String remark5_vStr=dataList.getJSONObject(a).getString("remark5_vStr");//配送状态
					String msg_note=dataList.getJSONObject(a).getString("msg_note");//配送讯息，
					String scm_dely_date=dataList.getJSONObject(a).getString("scm_dely_date");//约定配送日
					String dely_hope_date=dataList.getJSONObject(a).getString("dely_hope_date");//预计出货日
					String slip_no=dataList.getJSONObject(a).getString("slip_no");//配送编号
					String order_gb_str=dataList.getJSONObject(a).getString("order_gb_str");//订单类别
					String cust_name_mask=dataList.getJSONObject(a).getString("cust_name_mask");//订购人姓名			
					String receiver_mask=dataList.getJSONObject(a).getString("receiver_mask");//收件人											
					String entp_goods_no=dataList.getJSONObject(a).getString("entp_goods_no");//商品原厂编码
					String goods_code=dataList.getJSONObject(a).getString("goods_code");//商品编码
					String goods_name=dataList.getJSONObject(a).getString("goods_name");//商品名称
					String goodsdt_code=dataList.getJSONObject(a).getString("goodsdt_code");//单品编码，001
					String goodsdt_info=dataList.getJSONObject(a).getString("goodsdt_info");//单品详细，無
					String syslast=dataList.getJSONObject(a).getString("syslast");//数量


					//System.out.println(completeOrderNo);

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
	 * 出貨中訂單-超商取貨-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param fromDate 转单日起，yyyy/MM/dd
	 * @param fromHour 转单日起，时
	 * @param fromMinute 转单日起，分
	 * @param toDate 转单日讫，yyyy/MM/dd
	 * @param toHour 转单日讫，时
	 * @param toMinute 转单日讫，分
	 * @param receiver 收件人
	 * @param orderNo 订单号
	 * @param status 状态 1:已印單待驗收  2:已印單未到貨  3:商品驗退需重新出貨 4:待客戶取件
	 * @param dely_gb 门市类型 21:7-11 23:便利達康
	 * @return
	 */
	public String GetOrdersendingStoresQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String fromDate,String fromHour,String fromMinute,String toDate,String toHour,String toMinute,String receiver,String orderNo,String status,String dely_gb)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "sendingStoresQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();
			sendInfo.put("fromDate", fromDate);//转单日起2016/03/18 最大区间2个月
			sendInfo.put("fromHour", fromHour);//00
			sendInfo.put("fromMinute", fromMinute);//00
			sendInfo.put("toDate", toDate);//转单日讫2016/03/18
			sendInfo.put("toHour", toHour);//23
			sendInfo.put("toMinute", toMinute);//59

			sendInfo.put("receiver", receiver);//收件人
			sendInfo.put("orderNo", orderNo);//订单号
			sendInfo.put("status", status);// 状态 1:已印單待驗收  2:已印單未到貨  3:商品驗退需重新出貨 4:待客戶取件
			sendInfo.put("dely_gb", dely_gb);// 门市类型 21:7-11 23:便利達康

			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrdersendingStoresQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{

					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次					
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001
					String slip_i_no=dataList.getJSONObject(a).getString("slip_i_no");//出货单号
					String storeId=dataList.getJSONObject(a).getString("storeId");//门市代码
					String storeName=dataList.getJSONObject(a).getString("storeName");//门市名称
					String remark5_vStr=dataList.getJSONObject(a).getString("remark5_vStr");//配送状态
					String msg_note=dataList.getJSONObject(a).getString("msg_note");//配送讯息，
					String create_date=dataList.getJSONObject(a).getString("create_date");//印单日
					String overDays=dataList.getJSONObject(a).getString("overDays");//未到货逾期天数
					String dely_hope_date=dataList.getJSONObject(a).getString("dely_hope_date");//预计出货日
					String slip_no=dataList.getJSONObject(a).getString("slip_no");//配送编号
					String box_yn=dataList.getJSONObject(a).getString("box_yn");//并箱编号					
					String order_gb_str=dataList.getJSONObject(a).getString("order_gb_str");//订单类别
					String cust_name_mask=dataList.getJSONObject(a).getString("cust_name");//订购人姓名			
					String receiver_mask=dataList.getJSONObject(a).getString("receiver");//收件人											
					String entp_goods_no=dataList.getJSONObject(a).getString("entp_goods_no");//商品原厂编码
					String goods_code=dataList.getJSONObject(a).getString("goods_code");//商品编码
					String goods_name=dataList.getJSONObject(a).getString("goods_name");//商品名称
					String goodsdt_code=dataList.getJSONObject(a).getString("goodsdt_code");//单品编码，001
					String goodsdt_info=dataList.getJSONObject(a).getString("goodsdt_info");//单品详细，無
					String syslast=dataList.getJSONObject(a).getString("syslast");//数量
					String code_name=dataList.getJSONObject(a).getString("code_name");//说明


					//System.out.println(completeOrderNo);

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
	 * 出貨中訂單-第三方物流-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param fromDate 转单日起，yyyy/MM/dd
	 * @param fromHour 转单日起，时
	 * @param fromMinute 转单日起，分
	 * @param toDate 转单日讫，yyyy/MM/dd
	 * @param toHour 转单日讫，时
	 * @param toMinute 转单日讫，分
	 * @param receiver 收件人
	 * @param orderNo 订单号
	 * @param status 状态 1:已印單 2:配送中 
	 * @param logistics 物流商 61:宅配通 62:新竹貨運 63:宅急便
	 * @return
	 */
	public String GetOrdersendingThirdQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String fromDate,String fromHour,String fromMinute,String toDate,String toHour,String toMinute,String receiver,String orderNo,String status,String logistics)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "sendingThirdQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();
			sendInfo.put("fromDate", fromDate);//转单日起2016/03/18 最大区间2个月
			sendInfo.put("fromHour", fromHour);//00
			sendInfo.put("fromMinute", fromMinute);//00
			sendInfo.put("toDate", toDate);//转单日讫2016/03/18
			sendInfo.put("toHour", toHour);//23
			sendInfo.put("toMinute", toMinute);//59

			sendInfo.put("receiver", receiver);//收件人
			sendInfo.put("orderNo", orderNo);//订单号
			sendInfo.put("status", status);// 状态 1:已印單 2:配送中 
			sendInfo.put("logistics", logistics);// 物流商 61:宅配通 62:新竹貨運 63:宅急便

			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrdersendingThirdQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{

					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次					
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001
					String slip_i_no=dataList.getJSONObject(a).getString("slip_i_no");//出货单号


					String remark5_vStr="";//配送状态
					if (dataList.getJSONObject(a).isNull("remark5_vStr")) 
					{
						remark5_vStr=dataList.getJSONObject(a).getString("remark5_vStr");//配送状态
					}
					String msg_note="";//配送讯息，	
					if (dataList.getJSONObject(a).isNull("msg_note")) 
					{
						msg_note=dataList.getJSONObject(a).getString("msg_note");//配送讯息，	
					}
					String scm_msg="";//宅单备注
					if (dataList.getJSONObject(a).isNull("scm_msg")) 
					{
						scm_msg=dataList.getJSONObject(a).getString("scm_msg");//宅单备注
					}
					String dely_hope_date="";//约定配送日
					if (dataList.getJSONObject(a).isNull("dely_hope_date")) 
					{
						dely_hope_date=dataList.getJSONObject(a).getString("dely_hope_date");//约定配送日
					}

					String create_date=dataList.getJSONObject(a).getString("create_date");//印单日
					String slip_no=dataList.getJSONObject(a).getString("slip_no");//配送编号
					String overDays=dataList.getJSONObject(a).getString("overDays");//已印单未出货天数
					String scm_dely_date=dataList.getJSONObject(a).getString("scm_dely_date");//预计出货日					
					String box_yn=dataList.getJSONObject(a).getString("box_yn");//并箱编号					
					String order_gb_str=dataList.getJSONObject(a).getString("order_gb_str");//订单类别
					String cust_name_mask=dataList.getJSONObject(a).getString("cust_name_mask");//订购人姓名			
					String receiver_mask=dataList.getJSONObject(a).getString("receiver_mask");//收件人											
					String entp_goods_no=dataList.getJSONObject(a).getString("entp_goods_no");//商品原厂编码
					String goods_code=dataList.getJSONObject(a).getString("goods_code");//商品编码
					String goods_name=dataList.getJSONObject(a).getString("goods_name");//商品名称
					String goodsdt_code=dataList.getJSONObject(a).getString("goodsdt_code");//单品编码，001
					String goodsdt_info=dataList.getJSONObject(a).getString("goodsdt_info");//单品详细，無
					String syslast=dataList.getJSONObject(a).getString("syslast");//数量
					String code_name=dataList.getJSONObject(a).getString("code_name");//说明
					String dely_gbStr=dataList.getJSONObject(a).getString("dely_gbStr");//出货物流公司
					String outplace_post=dataList.getJSONObject(a).getString("outplace_post");//出货邮编
					String outplace_addr=dataList.getJSONObject(a).getString("outplace_addr");//出货地址
					String outplace_rtn_dely_gbStr=dataList.getJSONObject(a).getString("outplace_rtn_dely_gbStr");//回收物流公司
					String outplace_rtn_post=dataList.getJSONObject(a).getString("outplace_rtn_post");//回收邮编
					String outplace_rtn_addr=dataList.getJSONObject(a).getString("outplace_rtn_addr");//回收地址

					//System.out.println(completeOrderNo);

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
	 * 出貨中訂單-訂單返回-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param orderNo 订单编号
	 * @param slipNo 配送编号
	 * @return
	 */
	public String GetOrderorderBackQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String orderNo,String slipNo)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "orderBackQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();

			sendInfo.put("orderNo", orderNo);//订单号
			sendInfo.put("slipNo", slipNo);// 配送编号

			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrderorderBackQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{

					String itemNo=dataList.getJSONObject(a).getString("itemNo");//项次					
					String completeOrderNo=dataList.getJSONObject(a).getString("completeOrderNo");//订单编号,20140918203512-001-001-001
					String slip_i_no=dataList.getJSONObject(a).getString("slip_i_no");//出货单号					
					String goods_code=dataList.getJSONObject(a).getString("goods_code");//商品编码
					String goods_name=dataList.getJSONObject(a).getString("goods_name");//商品名称
					String goodsdt_code=dataList.getJSONObject(a).getString("goodsdt_code");//单品编码，001
					String goodsdt_info=dataList.getJSONObject(a).getString("goodsdt_info");//单品详细，無				


					//System.out.println(completeOrderNo);

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
	 * 訂單商品接單統計-查詢
	 * @param apiUrl
	 * @param entpID 统一编码
	 * @param entpCode 厂商编号
	 * @param entpPwd 密码
	 * @param otpBackNo 动态密码锁背面序号后3码,非此方式可以为""
	 * @param stDate 日期起 yyyy/MM/dd
	 * @param edDate 日期讫 yyyy/MM/dd
	 * @param goodsCode 商品编码
	 * @return
	 */
	public String GetOrderorderGoodsQuery(String apiUrl,String entpID,String entpCode,String entpPwd,String otpBackNo,String stDate,String edDate,String goodsCode)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderServlet.do";
		}
		else			
		{
			apiUrl+="/OrderServlet.do";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("doAction", "orderGoodsQuery");

			JSONObject loginInfo = new JSONObject();
			loginInfo.put("entpID", entpID);
			loginInfo.put("entpCode", entpCode);
			loginInfo.put("entpPwd", entpPwd);
			if (otpBackNo.equals("")==false) 
			{
				loginInfo.put("otpBackNo", otpBackNo);
			}			
			header.put("loginInfo", loginInfo);

			JSONObject sendInfo = new JSONObject();

			sendInfo.put("stDate", stDate);//日期起
			sendInfo.put("edDate", edDate);// 日期讫
			sendInfo.put("goodsCode", goodsCode);// 商品编码

			header.put("sendInfo", sendInfo);

			//
			String request=header.toString();

			resbody=HttpSend.SendMomo("GetOrderorderGoodsQuery", request, apiUrl);

			JSONObject jsonres = new JSONObject(resbody);

			//错误列表
			if (jsonres.isNull("basicCheckMsgList")==false) 
			{
				JSONArray basicCheckMsgList=jsonres.getJSONArray("basicCheckMsgList");

				for(int a=0;a<basicCheckMsgList.length();a++)
				{
					String ErrorMessage=basicCheckMsgList.getString(a);

					//System.out.println("错误信息:"+ErrorMessage);
				}
			}
			else 
			{
				JSONArray dataList=jsonres.getJSONArray("dataList");

				for(int a=0;a<dataList.length();a++)
				{			

					String entpGoodsNo=dataList.getJSONObject(a).getString("entpGoodsNo");//商品原厂编码					
					String goods_code=dataList.getJSONObject(a).getString("goodsCode");//商品编码
					String goodsName=dataList.getJSONObject(a).getString("goodsName");//商品名称
					String goodsDtInfo=dataList.getJSONObject(a).getString("goodsDtInfo");//单品详细	
					String buyPrice=dataList.getJSONObject(a).getString("buyPrice");//进价含税
					String orderQty=dataList.getJSONObject(a).getString("orderQty");//數量 (訂購-取消)	
					String claimQty=dataList.getJSONObject(a).getString("claimQty");//客退數量					

					//System.out.println(goods_code);

				}
			}

		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;	

	}























}
