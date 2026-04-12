package com.dsc.spos.utils.logistics;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import javax.imageio.stream.FileImageOutputStream;
import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.bouncycastle.util.encoders.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;

//新竹物流
public class Htc 
{


	/**
	 * 申请托运单
	 * @param apiUrl   地址
	 * @param Company  公司
	 * @param password 密码
	 * @param ordersn 单号 30码
	 * @param receiver 收件人
	 * @param receiver_phone 收件人电话
	 * @param receiver_address 收件人地址
	 * @param pieces 件数
	 * @param weight 重量
	 * @param sender_site 出货站号
	 * @param shipdate 配送日期
	 * @param collectAmt 代收货款
	 * @param memo 备注
	 * @param productType 温层，商品种类001 一般;003 冷凍;008 冷藏
	 * @param sender
	 * @param sender_phone
	 * @param sender_address
	 * @return
	 */
	public String TransData_Json(String apiUrl,String company, String password,String ordersn,String receiver,String receiver_phone,
			String receiver_address,String pieces,String weight,String sender_site,String shipdate,String collectAmt,String memo,
			String productType,String sender,String sender_phone,String sender_address) 
	{
		String resbody="";

		//WSDL中有
		String soapaction = "http://tempuri.org/";

		String json="";	

		Service service = new Service();
		try
		{
			JSONArray header=new JSONArray();

			JSONObject detail = new JSONObject();
			detail.put("epino", ordersn);//订单编号
			detail.put("ercsig", receiver);//收货人姓名
			detail.put("ertel1", receiver_phone);//收货人电话1
			detail.put("eraddr", receiver_address);//收货人地址
			detail.put("ejamt", pieces);//件数
			detail.put("eqamt", weight);//重量			
			if (sender_site.trim().equals("")==false) 
			{
				detail.put("esstno", sender_site);//出货站号
			}
			if (shipdate.equals("")==false) 
			{
				detail.put("eddate", shipdate);//指定日期
			}
			if (collectAmt.equals("")==false) 
			{
				detail.put("eqmny", collectAmt);//代收货款
			}
			if (memo.equals("")==false) 
			{
				detail.put("EMARK", memo);//备注
			}
			if (productType.equals("")==false) 
			{
				detail.put("eprdcl2", productType);//商品种类001 一般;003 冷凍;008 冷藏
			}			

			//逆物流用到
			if (sender.equals("")==false) 
			{
				detail.put("etcsig", sender);//发货人姓名
			}
			if (sender_phone.equals("")==false) 
			{
				detail.put("ettel1", sender_phone);//发货人电话1
			}
			if (sender_address.equals("")==false) 
			{
				detail.put("etaddr", sender_address);//发货人地址
			}			
			
			header.put(detail);

			json=header.toString();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(apiUrl);
			// 设置要调用哪个方法
			call.setOperationName(new QName(soapaction, "TransData_Json")); 

			// 设置要传递的参数--要和接口方提供的参数名一致
			call.addParameter(new QName(soapaction, "company"), 
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);

			// 设置要传递的参数--要和接口方提供的参数名一致
			call.addParameter(new QName(soapaction, "password"), 
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);

			// 设置要传递的参数--要和接口方提供的参数名一致
			call.addParameter(new QName(soapaction, "json"), 
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);

			// 要返回的数据类型（自定义类型，我这边接口提供方给我返回的是json字符串，所以我用string类型接收。这个地方一定要设置好，不然各种报错很崩溃）
			call.setReturnType(new QName(soapaction, "TransData_Json"), String.class); 

			call.setUseSOAPAction(true);
			call.setSOAPActionURI(soapaction + "TransData_Json");

			// 调用方法并传递参数-传递的参数和设置的参数要对应，顺序不能搞错了
			resbody = (String) call.invoke(new Object[] { company, password,json});

			//打印结果,我设置的接收格式为json字符串,这边直接打印出来
			//System.out.println(resbody);

			JSONArray jsonres=new JSONArray(resbody);
			for (int i = 0; i < jsonres.length(); i++) 
			{
				if (jsonres.getJSONObject(0).isNull("ErrMsg")) 
				{
					//System.out.println("HTC新竹物流接口TransData_Json调用失败");
				}
				else 
				{
					String ErrMsg=jsonres.getJSONObject(0).getString("ErrMsg");//错误信息

					if (ErrMsg.trim().equals("")==false) 
					{
						//System.out.println("HTC新竹物流接口TransData_Json调用失败=" + ErrMsg);
					}
					else 
					{
						String Num=jsonres.getJSONObject(0).getString("Num");//序号
						String success=jsonres.getJSONObject(0).getString("success");//新增 Y 修改 R 失敗 
						String edelno=jsonres.getJSONObject(0).getString("edelno");//托运单号10码
						String epino=jsonres.getJSONObject(0).getString("epino");//订单编号
						String erstno=jsonres.getJSONObject(0).getString("erstno").trim();//到著站编码
						String eqamt=jsonres.getJSONObject(0).getString("eqamt").trim();//重量

						String image=jsonres.getJSONObject(0).getString("image");//托运单图片字符串						

						hexToImage("d:/1.bmp",image);

						//System.out.println(image);

					}
				}


			}


		}
		catch (Exception ex)
		{
			
		}  

		return resbody;

	}


	/**
	 * 更新重量
	 * @param apiUrl
	 * @param Company
	 * @param password
	 * @param ordersn
	 * @param expressNO
	 * @param weight
	 * @return
	 */
	public String UpdData_Json(String apiUrl,String company, String password,String ordersn,String expressNO,String weight)
	{		
		String resbody="";

		//WSDL中有
		String soapaction = "http://tempuri.org/";

		String json="";	

		Service service = new Service();
		try
		{
			JSONArray header=new JSONArray();

			JSONObject detail = new JSONObject();
			detail.put("epino", ordersn);//订单编号
			detail.put("edelno", expressNO);//货运单号
			detail.put("eqamt", weight);//重量			

			header.put(detail);

			json=header.toString();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(apiUrl);
			// 设置要调用哪个方法
			call.setOperationName(new QName(soapaction, "UpdData_Json")); 

			// 设置要传递的参数--要和接口方提供的参数名一致
			call.addParameter(new QName(soapaction, "company"), 
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);

			// 设置要传递的参数--要和接口方提供的参数名一致
			call.addParameter(new QName(soapaction, "password"), 
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);

			// 设置要传递的参数--要和接口方提供的参数名一致
			call.addParameter(new QName(soapaction, "json"), 
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);

			// 要返回的数据类型（自定义类型，我这边接口提供方给我返回的是json字符串，所以我用string类型接收。这个地方一定要设置好，不然各种报错很崩溃）
			call.setReturnType(new QName(soapaction, "UpdData_Json"), String.class); 

			call.setUseSOAPAction(true);
			call.setSOAPActionURI(soapaction + "UpdData_Json");

			// 调用方法并传递参数-传递的参数和设置的参数要对应，顺序不能搞错了
			resbody = (String) call.invoke(new Object[] { company, password,json});

			//打印结果,我设置的接收格式为json字符串,这边直接打印出来
			//System.out.println(resbody);

			JSONArray jsonres=new JSONArray(resbody);
			for (int i = 0; i < jsonres.length(); i++) 
			{
				if (jsonres.getJSONObject(0).isNull("ErrMsg")) 
				{
					//System.out.println("HTC新竹物流接口UpdData_Json调用失败");
				}
				else 
				{
					String ErrMsg=jsonres.getJSONObject(0).getString("ErrMsg");//错误信息

					if (ErrMsg.trim().equals("")==false) 
					{
						//System.out.println("HTC新竹物流接口UpdData_Json调用失败=" + ErrMsg);
					}
					else 
					{
						String Num=jsonres.getJSONObject(0).getString("Num");//序号
						String success=jsonres.getJSONObject(0).getString("success");//新增 Y 修改 R 失敗 
						String edelno=jsonres.getJSONObject(0).getString("edelno");//托运单号
						String epino=jsonres.getJSONObject(0).getString("epino");//订单编号
						String eqamt=jsonres.getJSONObject(0).getString("eqamt").trim();//重量

						//System.out.println(eqamt);

					}
				}


			}


		}
		catch (Exception ex)
		{
		

		}  

		return resbody;
	}



	/**
	 * 确认出货
	 * @param apiUrl
	 * @param Company
	 * @param password
	 * @param ordersnList
	 * @param expressNOList
	 * @return
	 */
	public String TransReport_Json(String apiUrl,String company, String password,String[] ordersnList,String[] expressNOList)
	{
		String resbody="";

		//WSDL中有
		String soapaction = "http://tempuri.org/";

		String json="";	

		Service service = new Service();
		try
		{
			JSONArray header=new JSONArray();

			for (int i = 0; i < ordersnList.length; i++) 
			{
				JSONObject detail = new JSONObject();
				detail.put("epino", ordersnList[i]);//订单编号
				detail.put("edelno", expressNOList[i]);//货运单号

				header.put(detail);
			}		


			json=header.toString();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(apiUrl);
			// 设置要调用哪个方法
			call.setOperationName(new QName(soapaction, "TransReport_Json")); 

			// 设置要传递的参数--要和接口方提供的参数名一致
			call.addParameter(new QName(soapaction, "sCompany"), 
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);

			// 设置要传递的参数--要和接口方提供的参数名一致
			call.addParameter(new QName(soapaction, "sPassword"), 
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);

			// 设置要传递的参数--要和接口方提供的参数名一致
			call.addParameter(new QName(soapaction, "dsCusJson"), 
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);

			// 要返回的数据类型（自定义类型，我这边接口提供方给我返回的是json字符串，所以我用string类型接收。这个地方一定要设置好，不然各种报错很崩溃）
			call.setReturnType(new QName(soapaction, "TransReport_Json"), String.class); 

			call.setUseSOAPAction(true);
			call.setSOAPActionURI(soapaction + "TransReport_Json");

			// 调用方法并传递参数-传递的参数和设置的参数要对应，顺序不能搞错了
			resbody = (String) call.invoke(new Object[] { company, password,json});

			//打印结果,我设置的接收格式为json字符串,这边直接打印出来
			//System.out.println(resbody);

			JSONArray jsonres=new JSONArray(resbody);
			for (int a = 0; a < jsonres.length(); a++) 
			{
				if (jsonres.getJSONObject(a).isNull("ErrMsg")) 
				{
					//System.out.println("HTC新竹物流接口TransReport_Json调用失败");
				}
				else 
				{
					String ErrMsg=jsonres.getJSONObject(a).getString("ErrMsg");//错误信息

					if (ErrMsg.trim().equals("")==false) 
					{
						//System.out.println("HTC新竹物流接口TransReport_Json调用失败=" + ErrMsg);
					}
					else 
					{
						String Num=jsonres.getJSONObject(a).getString("Num");//序号1
						String success=jsonres.getJSONObject(a).getString("success");//新增 Y 修改 R 失敗 
						String edelno=jsonres.getJSONObject(a).getString("edelno");//托运单号
						String epino=jsonres.getJSONObject(a).getString("epino");//订单编号

						//System.out.println(epino);

					}
				}


			}


		}
		catch (Exception ex)
		{
		

		}  

		return resbody;

	}



	/**
	 * 内嵌网页方式货运状态
	 * @param apiUrlTwo
	 * @param privateKey
	 * @param iv
	 * @param v
	 * @param expressNo
	 * @return
	 */
	public String GetHtcStatus_Page(String apiUrlTwo,String privateKey,String iv,String v,String expressNo)
	{

		String resbody="";

		try 
		{

			EncryptUtils eu = new EncryptUtils();

			//key=当前日期+176天(privateKey)
			SimpleDateFormat myTempdf = new SimpleDateFormat("yyyyMMdd");
			Calendar myTempcal = Calendar.getInstance();
			String sDate=myTempdf.format(myTempcal.getTime());
			//System.out.println(sDate);

			int days=Integer.parseInt(privateKey);

			String key=PosPub.GetStringDate(sDate, days);

			//
			byte[] dataBytes = (expressNo).getBytes(StandardCharsets.UTF_8);


			String encryptExpressno=eu.Htc_DES_CBC(dataBytes, key, iv);
			eu=null;
			

			String request="no="+java.net.URLEncoder.encode(encryptExpressno, "utf-8")+"&v="+v;		

			resbody=apiUrlTwo+"?"+request;			

		} 
		catch (Exception e) 
		{
			resbody="";
		}

		return resbody;
	}


	/**
	 * 新竹物流货运状态
	 * @param apiUrlTwo
	 * @param privateKey
	 * @param iv
	 * @param v
	 * @param expressNoList
	 * @return
	 */
	public String GetHtcStatus_Xml(String apiUrlTwo,String privateKey,String iv,String v,String[] expressNoList)
	{

		String resbody="";

		try 
		{

			String strXML = null;
			Document document = DocumentHelper.createDocument();
			Element qrylist = document.addElement("qrylist");

			for (int i = 0; i < expressNoList.length; i++) 
			{
				Element order = qrylist.addElement("order");
				order.addText(" ");
				order.addAttribute("orderid", expressNoList[i]);		
			}			

			StringWriter strWtr = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter xmlWriter =new XMLWriter(strWtr, format);
			xmlWriter.write(document);

			strXML = strWtr.toString();

			document=null;



			EncryptUtils eu = new EncryptUtils();

			//key=当前日期+176天(privateKey)
			SimpleDateFormat myTempdf = new SimpleDateFormat("yyyyMMdd");
			Calendar myTempcal = Calendar.getInstance();
			String sDate=myTempdf.format(myTempcal.getTime());
			//System.out.println(sDate);

			int days=Integer.parseInt(privateKey);

			String key=PosPub.GetStringDate(sDate, days);

			//
			byte[] dataBytes = strXML.getBytes(StandardCharsets.UTF_8);

			String encryptExpressno=eu.Htc_DES_CBC(dataBytes, key, iv);			

			String request="no="+encryptExpressno+"&v="+v;		

			resbody=HttpSend.SendHtc("GetHtcStatus_Xml", request, apiUrlTwo);

			//不要用new string[] 转字符串，否则会报错			
			resbody=eu.Htc_desDecrypt(Base64.decode(resbody), key, iv);

			//resbody="<?xml version=\"1.0\" encoding=\"utf-8\"?> <rlist>   <orders ordersid=\"1234567890\">       <order orderid=\"1234567890\" wrktime=\"YYYYMMDDHHMMSS\" status=\"貨物狀況\"/>       <order orderid=\"1234567890\" wrktime=\"YYYYMMDDHHMMSS\" status=\"貨物狀況 2\"/>   </orders>    <orders ordersid=\"1234567891\">     <order orderid=\"1234567891\" wrktime=\"YYYYMMDDHHMMSS\" status=\"貨物狀況\"/>       <order orderid=\"1234567891\" wrktime=\"YYYYMMDDHHMMSS\" status=\"貨物狀況 2\"/>  </orders>  </rlist>";
			//https://www.cnblogs.com/shizhijie/p/7841368.html
			document=DocumentHelper.parseText(resbody); //将字符串转为XML

			Element rootElt = document.getRootElement(); //获取根节点

			Iterator iter = rootElt.elementIterator("orders"); //获取根节点下的子节点orders 

			while (iter.hasNext()) 
			{
				Element order = (Element) iter.next();
				String ordersid = order.attributeValue("ordersid"); 
				//System.out.println(ordersid);

				//
				Iterator iterOrder=order.elementIterator("order");
				while (iterOrder.hasNext()) 
				{
					Element orderStatus = (Element) iterOrder.next();
					String orderStatusid = orderStatus.attributeValue("orderid"); 
					String orderStatuswrktime = orderStatus.attributeValue("wrktime"); 
					String orderStatusstatus = orderStatus.attributeValue("status"); 
					//System.out.println(orderStatusid);
					//System.out.println(orderStatuswrktime);
					//System.out.println(orderStatusstatus);
				}

			}

			eu=null;

		} 
		catch (Exception e) 
		{
			resbody="";
		}

		return resbody;
	}




	/**
	 * 将十六进制字符串转化成图片d:/1.bmp
	 */ 
	private void hexToImage(String filePath,String hexString) 
	{ 
		byte[] bytes = stringToByte(hexString);
		try
		{		
			FileImageOutputStream imageOutput = new FileImageOutputStream(new File(filePath));
			imageOutput.write(bytes, 0, bytes.length);
			imageOutput.close();
		} 
		catch(Exception ex) 
		{

		} 
	}

	private byte[] stringToByte(String s) 
	{
		int length = s.length() / 2;
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) 
		{
			bytes[i] = (byte) ((Character.digit(s.charAt(i * 2), 16) << 4) | Character.digit(s.charAt((i * 2) + 1), 16));
		}
		return bytes;
	}




}
