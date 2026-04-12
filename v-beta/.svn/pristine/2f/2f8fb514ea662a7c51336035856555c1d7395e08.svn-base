package com.dsc.spos.utils.logistics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.dsc.spos.utils.PosPub;

//便利达康，支持3大超市(1：全家，2：萊爾富，3：OK)
//https://cvsweb.cvs.com.tw/webservice/service.asmx?wsdl
//FTP 站台位置 ftp://cvsftp.cvs.com.tw FTP 帳密請洽便利達康
//便利達康後台網址 https://b2b.cvs.com.tw/cvs2
//******************************************************************
//
//大物流出貨流程：F10_OrderUpload(訂單上傳)--F03(大物流驗收檔，F07和F09是驗退)--F44(進店即時檔)--F05(取貨完成檔)
//大物流退貨流程：F61_ALL(訂單預退檔)--F84_ALL(離店檔)--F07(大物流驗退檔)
//大物流對賬:F20+F21

//******************************************************************
//驗退訂單需重出件
//店到店出貨流程：F60_C2C(訂單上傳)--F27_C2C(門店寄件代收)--F71_C2C(大物流店到店寄件驗收檔，F67_C2C和F69_C2C是驗退)--F44_C2C(即時進店檔 )--F17_ALL(取貨代收檔=取貨完成)
//店到店退貨流程：F61_ALL(訂單預退檔)--F84_ALL(離店檔)--F67_C2C(大物流驗退檔)
//店到店對賬：F620_C2C+F621_C2C

public class Cvs 
{



	/**
	 * 订单上传，每次限制100笔
	 * @param apiUrl
	 * @param ecNo 厂商代码
	 * @param collectNo 代收代号
	 * @param dcNo 大物流代码
	 * @param OrderList(
	 * ODNO:订单编号11码，物流单号
	 * STNO:取货门店编号，代码(F：全家、L：萊爾富、K：OK)+门市编号
	 * AMT:代收金额， >=0 int
	 * CUTKNM:取货人姓名
	 * CUTKTL:手机末三码，無資料代空值 
	 * PRODNM:商品別代，0 : 一般商品  1 : 票券商品
	 * ECWEB: EC網站名，例：康迅數位 www.payeasy.com.tw
	 * ECSERTEL ：EC 網站客服電話，例：02-XXXXXXXX 
	 * REALAMT：商品實際金額，int 必須為 0 或正整數。 ※選擇取貨不付款與取貨付款時，需在此欄位填上貨物「實際金額」，以利後續查帳事宜
	 * TRADETYPE：交易方式識別碼(货到付款标记) ，1：取貨付款 3：取貨不付款 
	 * )
	 * @return
	 */
	public boolean F10_OrderUpload(String apiUrl,String ecNo,String collectNo,String dcNo,List<Map<String, Object>> OrderList)
	{
		////F10CVS+EC厂商代码+YYYYMMDDhhmmss.xml 

		try 
		{
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMddHHmmss");
			String eDatetime=dfDate.format(cal.getTime());
			String filename="F10CVS" + ecNo + eDatetime +".xml";

			String filePath="D:\\";			

			File xmlFile = new File(filePath + filename);

			Document document = DocumentHelper.createDocument();
			Element ORDER_DOC = document.addElement("ORDER_DOC");

			for (Map<String, Object> oneData : OrderList)
			{
				Element ORDER = ORDER_DOC.addElement("ORDER");

				//
				String ODNO= oneData.get("ODNO").toString();
				//<![CDATA[達康購物網]]>
				//特殊＜  ＞  ＆ ‘…
				boolean isSpecial= isSpecialChar(ODNO);
				if (isSpecial) 
				{
					ODNO=String.format("<![CDATA[%s]]>", ODNO);
				}			

				//
				String STNO= oneData.get("STNO").toString();
				isSpecial= isSpecialChar(STNO);
				if (isSpecial) 
				{
					STNO=String.format("<![CDATA[%s]]>", STNO);
				}			

				String AMT=oneData.get("AMT").toString() ;

				//
				String CUTKNM= oneData.get("CUTKNM").toString();				
				isSpecial= isSpecialChar(CUTKNM);
				if (isSpecial) 
				{
					CUTKNM=String.format("<![CDATA[%s]]>", CUTKNM);
				}	
				//
				String CUTKTL= oneData.get("CUTKTL").toString();
				isSpecial= isSpecialChar(CUTKTL);
				if (isSpecial) 
				{
					CUTKTL=String.format("<![CDATA[%s]]>", CUTKTL);
				}	

				//
				String PRODNM= oneData.get("PRODNM").toString();

				String ECWEB= oneData.get("ECWEB").toString();
				isSpecial= isSpecialChar(ECWEB);
				if (isSpecial) 
				{
					ECWEB=String.format("<![CDATA[%s]]>", ECWEB);
				}		

				//
				String ECSERTEL= oneData.get("ECSERTEL").toString();
				isSpecial= isSpecialChar(ECSERTEL);
				if (isSpecial) 
				{
					ECSERTEL=String.format("<![CDATA[%s]]>", ECSERTEL);
				}		

				//
				String REALAMT= oneData.get("REALAMT").toString();

				//
				String TRADETYPE= oneData.get("TRADETYPE").toString();

				//厂商代码3码
				Element eECNO=ORDER.addElement("ECNO");
				eECNO.setText(ecNo);
				//EC订单编号，物流单号11码
				Element eODNO=ORDER.addElement("ODNO");
				eODNO.setText(ODNO);
				//取货门店编码7码，F：全家、L：萊爾富、K：OK 
				Element eSTNO=ORDER.addElement("STNO");
				eSTNO.setText(STNO);
				//代收金额5码
				Element eAMT=ORDER.addElement("AMT");
				eAMT.setText(AMT);
				//取货人姓名10码
				Element eCUTKNM=ORDER.addElement("CUTKNM");
				eCUTKNM.setText(CUTKNM);
				//手机号末3码
				Element eCUTKTL=ORDER.addElement("CUTKTL");
				eCUTKTL.setText(CUTKTL);
				//商品别代码1码，0 : 一般商品  1 : 票券商品 
				Element ePRODNM=ORDER.addElement("PRODNM");
				ePRODNM.setText(PRODNM);
				// EC 網站名稱 20码，例：康迅數位 www.payeasy.com.tw 
				Element eECWEB=ORDER.addElement("ECWEB");
				eECWEB.setText(ECWEB);
				// EC 網站客服電話 20码，例：02-XXXXXXXX
				Element eECSERTEL=ORDER.addElement("ECSERTEL");
				eECSERTEL.setText(ECSERTEL);
				//商品實際金額5码
				Element eREALAMT=ORDER.addElement("REALAMT");
				eREALAMT.setText(REALAMT);
				// 交易方式識別碼1码，1：取貨付款 3：取貨不付款  
				Element eTRADETYPE=ORDER.addElement("TRADETYPE");
				eTRADETYPE.setText(TRADETYPE);
				//代收代號3码
				Element eSERCODE=ORDER.addElement("SERCODE");
				eSERCODE.setText(collectNo);
				//大物流代號3码
				Element eEDCNO=ORDER.addElement("EDCNO");
				eEDCNO.setText(dcNo);
			}

			String TOTALS=OrderList.size()+"";

			Element ORDERCOUNT = ORDER_DOC.addElement("ORDERCOUNT");
			Element eTOTALS=ORDERCOUNT.addElement("TOTALS");
			eTOTALS.setText(TOTALS);

			FileOutputStream fos=new FileOutputStream(xmlFile);			
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter xmlWriter =new XMLWriter(fos, format);
			xmlWriter.write(document);			
			fos.close();
			fos=null;
			xmlWriter.close();	
			xmlWriter=null;
			document=null;
			xmlFile=null;

		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());

			return false;
		}

		return true;
	}


	/**
	 * 订单回复档
	 * @param localfile
	 * f11_expressno 托運單號			
	 * f11_reasonno  錯誤代碼
	 * f11_reasonname	錯誤說明			
	 * f11_ecno      取件公版代號
	 * f11_getshopno 取貨門店				
	 * @return
	 */
	public List<Map<String, Object>> F11(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element RTN_ORDER_DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = RTN_ORDER_DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("RTN_RESULT")) 
				{
					//處理日期時間yyyy-mm-dd hh24:mi:ss 
					//System.out.println(child.elementText("PROCDATE"));
					//處理成功筆數 
					//System.out.println(child.elementText("PROCCNT"));
					//處理失敗筆數 
					//System.out.println(child.elementText("ERRCNT"));
				}

				if (child.getName().equals("ERR_ORDER")) 
				{
					//錯誤代碼, 1 : 踢退（資料有誤且不成單） 	2 : 異常（資料部份有誤但成單） 
					//System.out.println(child.elementText("ERRCODE"));
					//錯誤說明 
					//System.out.println(child.elementText("ERRDESC"));
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					// 取貨門市編號7码
					//System.out.println(child.elementText("STNO"));
					//代收金額
					//System.out.println(child.elementText("AMT"));
					//取貨人姓名 
					//System.out.println(child.elementText("CUTKNM"));
					//手機末三碼 
					//System.out.println(child.elementText("CUTKTL"));
					//商品別代碼 
					//System.out.println(child.elementText("PRODNM"));
					//EC 網站名稱 
					//System.out.println(child.elementText("ECWEB"));
					//EC網站客服電話 
					//System.out.println(child.elementText("ECSERTEL"));
					//商品實際金額
					//System.out.println(child.elementText("REALAMT"));
					//交易方式識別碼 
					//System.out.println(child.elementText("TRADETYPE"));
					//代收代號  
					//System.out.println(child.elementText("SERCODE"));
					//大物流代號 
					//System.out.println(child.elementText("EDCNO"));
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f11_expressno", child.elementText("ODNO"));					
					map.put("f11_reasonno", child.elementText("ERRCODE"));
					map.put("f11_reasonname", child.elementText("ERRDESC"));					
					map.put("f11_ecno", child.elementText("ECNO"));
					map.put("f11_getshopno", child.elementText("STNO"));								

					lstMaps.add(map);		
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}





	/**
	 * 超商店铺资料档,18：30,路线路顺
	 * @param localfile
	 * f01_getshopno
	 * f01_getshopname
	 * f01_tel
	 * f01_address
	 * @return
	 */
	public List<Map<String, Object>> F01_ALL(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F01DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F01DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F01CONTENT")) 
				{
					//店铺编号F：全家、L：萊爾富、K：OK 
					//System.out.println(child.elementText("STNO"));
					//店铺名称
					//System.out.println(child.elementText("STNM"));
					//店铺电话
					//System.out.println(child.elementText("STTEL"));
					//店铺所在城市
					//System.out.println(child.elementText("STCITY"));
					//店铺所在区、乡镇
					//System.out.println(child.elementText("STCNTRY"));
					//店铺所在地址
					//System.out.println(child.elementText("STADR"));
					//店铺所在邮政编码
					//System.out.println(child.elementText("ZIPCD"));
					//路线路顺
					//System.out.println(child.elementText("DCRONO"));
					//整修起日期 無資料代 8 個 0（零）
					//System.out.println(child.elementText("SDATE"));
					///整修讫日期 無資料代 8 個 0（零）
					//System.out.println(child.elementText("EDATE"));
					//發票店號 供跨國電商提供給外籍人士使用，如有需求請來電洽詢，如無需要則請直接忽略此欄位 
					//System.out.println(child.elementText("STNEWNO"));
					
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f01_address", child.elementText("STADR"));									
					map.put("f01_tel", child.elementText("STTEL"));
					map.put("f01_getshopno", child.elementText("STNO"));					
					map.put("f01_getshopname", child.elementText("STNM"));						

					lstMaps.add(map);			
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}


	/**
	 * 大物流驗收檔(批次) ,22：30 F03173CVS20090901.xml 
	 * f03_expressno物流单号
	 * f03_ecno厂商代码
	 * f03_distributeno通路商代码
	 * f03_distributename通路商名称
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> F03(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F03DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F03DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F03CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//通路商代码 TFM：全家 TLF：萊爾富 	TOK：OK
					//System.out.println(child.elementText("CNNO"));
					//取货人姓名
					//System.out.println(child.elementText("CUName"));
					//商品别代码 0:一般商品  1:票券商品 
					//System.out.println(child.elementText("ProdType"));
					//
					//System.out.println(child.elementText("PINCODE"));		


					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f03_expressno", child.elementText("ODNO"));
					map.put("f03_ecno", child.elementText("ECNO"));
					map.put("f03_distributeno", child.elementText("CNNO"));
					if (child.elementText("CNNO").equals("TFM")) 
					{
						map.put("f03_distributename", "全家");
					}
					else if (child.elementText("CNNO").equals("TLF")) 
					{
						map.put("f03_distributename", "萊爾富");
					}
					else 
					{
						map.put("f03_distributename", "OK");
					}

					lstMaps.add(map);
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}


	/**
	 * 進店檔 ,14：00，有進店即時檔，不想用這個了
	 * @return
	 */
	public String F04()
	{
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File("D:\\1.xml");
			Document document = reader.read(file);
			Element F04DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F04DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F04CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//时间进店日期YYYYMMDD
					//System.out.println(child.elementText("DCSTDT"));								
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return "";
	}


	/**
	 * 取货完成档,22：00，F05RRRSSSYYYYMMDD.xml 
	 * @param localfile
	 * f05_expressno物流编号
	 * f05_distributeno通路商代码 F：全家、L：萊爾富、K：OK
	 * f05_distributename通路商名称
	 * f05_getshopno取货门店
	 * f05_sdate取货日期
	 * f05_bdate结账日期
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> F05(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F05DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F05DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F05CONTENT")) 
				{
					//文化物流二段条码(第一段条码,共9码)，3码EC厂商代码+订单编号前3码+3码代收代号
					String barcode1=child.elementText("BC1");
					//System.out.println(barcode1);
					//文化物流二段条码(第二段条码，共16码)，订单编号后8位+交易类型1码(1：取貨付款 3：取貨不付款 )+5码代收金额+2码检查码
					//檢查碼第一碼計算公式：各段條碼之奇數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 0，若餘數為 10 則放 1）。 
					//檢查碼第二碼計算公式：各段條碼之偶數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 8，若餘數為 10 則放 9） 
					String barcode2=child.elementText("BC2");
					//System.out.println(barcode2);
					//组11码配送编号
					String expressno= barcode1.substring(3,6) + barcode2.substring(0,8);

					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//实际取货代收日期YYYYMMDD
					//System.out.println(child.elementText("RTDT"));		
					//結帳基準日期 YYYYMMDD
					//System.out.println(child.elementText("TKDT"));		

					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f05_expressno", expressno);

					if (child.elementText("STNO").substring(0,1).equals("F")) 
					{
						map.put("f05_distributeno", "F");
						map.put("f05_distributename", "全家");
					}
					else if (child.elementText("STNO").substring(0,1).equals("L")) 
					{
						map.put("f05_distributeno", "L");
						map.put("f05_distributename", "萊爾富");
					}
					else if (child.elementText("STNO").substring(0,1).equals("K")) 
					{
						map.put("f05_distributeno", "K");
						map.put("f05_distributename", "OK");
					}
					map.put("f05_getshopno", child.elementText("STNO").substring(1));
					map.put("f05_sdate", child.elementText("RTDT").substring(1));
					map.put("f05_bdate", child.elementText("TKDT").substring(1));


					lstMaps.add(map);
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}


	/**
	 * 大物流驗退檔 ,21：30,F07RRRSSSYYYYMMDD.xml
	 * @param localfile
	 * f07_expressno物流单号
	 * f07_sdate验退日期
	 * f07_reasonno验退原因代码
	 * f07_reasonname验退原因名称
	 * f07_ecno厂商代码
	 * f07_getshopno取货门店
	 * f07_bdate结账日期
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> F07(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F07DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F07DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F07CONTENT")) 
				{
					//退貨原因 ,
					//T00 : 正常驗退 
					//T01 : 閉店、整修、無路線路順 
					//T02 : 無進貨資料 
					//T03 : 條碼錯誤 
					//T04 : 條碼重複 
					//T05 : 貨物進店後發生異常提早退貨 
					//T06 :超過三十天系統自動取消 
					//T08:超才 
					//D04 : 大物流包裝不良(滲漏) 
					//S04 :路線刪單 
					//S05:資料異常 
					//S06 : 小物流破損 
					//S07 : 門市反應商品包裝不良(滲漏) 
					//System.out.println(child.elementText("RET_M"));
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//大物流實際驗退日 YYYYMMDD
					//System.out.println(child.elementText("RTDCDT"));
					//結帳基準日  YYYYMMDD
					//System.out.println(child.elementText("FRTDCDT"));

					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f07_expressno", child.elementText("ODNO"));
					map.put("f07_sdate", child.elementText("RTDCDT"));
					map.put("f07_reasonno", child.elementText("RET_M"));
					if (child.elementText("RET_M").equals("T00")) 
					{
						map.put("f07_reasonname", "正常驗退");
					}
					else if (child.elementText("RET_M").equals("T01")) 
					{
						map.put("f07_reasonname", "閉店、整修、無路線路順 ");
					}
					else if (child.elementText("RET_M").equals("T02")) 
					{
						map.put("f07_reasonname", "無進貨資料");
					}
					else if (child.elementText("RET_M").equals("T03")) 
					{
						map.put("f07_reasonname", "條碼錯誤");
					}
					else if (child.elementText("RET_M").equals("T04")) 
					{
						map.put("f07_reasonname", "條碼重複");
					}
					else if (child.elementText("RET_M").equals("T05")) 
					{
						map.put("f07_reasonname", "貨物進店後發生異常提早退貨");						
					}
					else if (child.elementText("RET_M").equals("T06")) 
					{
						map.put("f07_reasonname", "超過三十天系統自動取消");
					}
					else if (child.elementText("RET_M").equals("T08")) 
					{
						map.put("f07_reasonname", "超才");
					}
					else if (child.elementText("RET_M").equals("D04")) 
					{
						map.put("f07_reasonname", "大物流包裝不良(滲漏)");
					}
					else if (child.elementText("RET_M").equals("S04")) 
					{
						map.put("f07_reasonname", "路線刪單");
					}
					else if (child.elementText("RET_M").equals("S05")) 
					{
						map.put("f07_reasonname", "資料異常");
					}
					else if (child.elementText("RET_M").equals("S06")) 
					{
						map.put("f07_reasonname", "小物流破損 ");
					}
					else if (child.elementText("RET_M").equals("S07")) 
					{
						map.put("f07_reasonname", "門市反應商品包裝不良(滲漏)");
					}

					map.put("f07_ecno", child.elementText("ECNO"));
					map.put("f07_getshopno", child.elementText("STNO"));
					map.put("f07_bdate", child.elementText("FRTDCDT"));		

					lstMaps.add(map);
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}


	/**
	 * 取消出货档,23：30
	 * @param localfile
	 * f09_expressno
	 * f09_reasonno
	 * f09_reasonname
	 * f09_ecno
	 * f09_getshopno
	 * @return
	 */
	public List<Map<String, Object>> F09(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F09DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F09DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F09CONTENT")) 
				{
					//取消類型,透過取消訂單原因判斷 
					//T1 : 系統取消，EC自行在後台取消 
					//D1 : 大物流取消 
					//S1 : 小物流取消 
					//N1 : 門市取消
					//System.out.println(child.elementText("RET_M"));

					//取消訂單原因 ,
					//T01 : 閉店、整修、無路線路順 
					//T02 : 無進貨資料 
					//T03 : 條碼錯誤 
					//T04 : 條碼重複 
					//T05 : 貨物進店後發生異常提早退貨 
					//T06 : 30 日未驗取消 
					//T07 : EC 自行取消 
					//T08 : 超材 
					//D01 : 大物流遺失 
					//D02 : 未送貨到小物流 
					//D04 : 大物流包裝不良(滲漏)  
					//S03 : 小物流遺失 
					//S06 : 小物流破損 
					//S07 : 門市反應商品包裝不良(滲漏) 
					//N05 : 門市遺失
					//System.out.println(child.elementText("RET_R"));

					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f09_expressno", child.elementText("ODNO"));					
					map.put("f09_reasonno", child.elementText("RET_M"));
					if (child.elementText("RET_R").equals("D01")) 
					{
						map.put("f09_reasonname", "大物流遺失 ");
					}
					else if (child.elementText("RET_R").equals("D02")) 
					{
						map.put("f09_reasonname", "未送貨到小物流 ");
					}
					else if (child.elementText("RET_R").equals("S03")) 
					{
						map.put("f09_reasonname", "小物流遺失 ");
					}					
					else if (child.elementText("RET_R").equals("N05")) 
					{
						map.put("f09_reasonname", "門市遺失");
					}
					else if (child.elementText("RET_R").equals("T01")) 
					{
						map.put("f09_reasonname", "閉店、整修、無路線路順  ");
					}		
					else if (child.elementText("RET_R").equals("T02")) 
					{
						map.put("f09_reasonname", "無進貨資料 ");
					}
					else if (child.elementText("RET_R").equals("T03")) 
					{
						map.put("f09_reasonname", "條碼錯誤 ");
					}	
					else if (child.elementText("RET_R").equals("T04")) 
					{
						map.put("f09_reasonname", "條碼重複 ");
					}	
					else if (child.elementText("RET_R").equals("T05")) 
					{
						map.put("f09_reasonname", "貨物進店後發生異常提早退貨");
					}				
					else if (child.elementText("RET_R").equals("T06")) 
					{
						map.put("f09_reasonname", "30 日未驗取消 ");
					}		
					else if (child.elementText("RET_R").equals("T07")) 
					{
						map.put("f09_reasonname", "EC自行取消 ");
					}	
					else if (child.elementText("RET_R").equals("T08")) 
					{
						map.put("f09_reasonname", "超材 ");
					}			
					else if (child.elementText("RET_R").equals("S06")) 
					{
						map.put("f09_reasonname", "小物流破損 ");
					}
					else if (child.elementText("RET_R").equals("S07")) 
					{
						map.put("f09_reasonname", "門市反應商品包裝不良(滲漏)");
					}
					else if (child.elementText("RET_R").equals("D04")) 
					{
						map.put("f09_reasonname", "大物流包裝不良(滲漏)  ");
					}

					map.put("f09_ecno", child.elementText("ECNO"));
					map.put("f09_getshopno", child.elementText("STNO"));					
					
					lstMaps.add(map);					
					

				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}

	/**
	 * 代收即时回复档,整點:10分,取貨完成檔
	 * @param localfile
	 * f17_expressno
	 * f17_bc1
	 * f17_bc2
	 * f17_getshopno
	 * f17_bdate
	 * f17_pincode
	 * @return
	 */
	public List<Map<String, Object>> F17_ALL(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F17DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F17DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F17CONTENT")) 
				{
					//文化物流二段条码(第一段条码,共9码)，3码EC厂商代码+订单编号前3码+3码代收代号
					String barcode1=child.elementText("BC1");
					//System.out.println(barcode1);
					//文化物流二段条码(第二段条码，共16码)，订单编号后8位+交易类型1码(1：取貨付款 3：取貨不付款 )+5码代收金额+2码检查码
					//檢查碼第一碼計算公式：各段條碼之奇數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 0，若餘數為 10 則放 1）。 
					//檢查碼第二碼計算公式：各段條碼之偶數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 8，若餘數為 10 則放 9） 
					String barcode2=child.elementText("BC2");
					//System.out.println(barcode2);

					//组11码配送编号
					String expressno= barcode1.substring(3,6) + barcode2.substring(0,8);

					//代收金额
					String sCollectAMT=barcode2.substring(9,14);

					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//實際代收日期 YYYYMMDD
					//System.out.println(child.elementText("RTDT"));		
					// 繳費代碼 ,空值
					//System.out.println(child.elementText("PINCODE"));		
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f17_expressno", expressno);
					map.put("f17_bc1", barcode1);
					map.put("f17_bc2", barcode2);					
					map.put("f17_getshopno", child.elementText("STNO"));					
					map.put("f17_bdate", child.elementText("RTDT"));	
					map.put("f17_pincode", child.elementText("PINCODE"));	

					lstMaps.add(map);			
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}


	/**
	 * 代收即时回复档,每 15 分鐘
	 * @return
	 */
	public String F17A()
	{
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File("D:\\1.xml");
			Document document = reader.read(file);
			Element F17ADOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F17ADOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F17ACONTENT")) 
				{
					//文化物流二段条码(第一段条码,共9码)，3码EC厂商代码+订单编号前3码+3码代收代号
					String barcode1=child.elementText("BC1");
					//System.out.println(barcode1);
					//文化物流二段条码(第二段条码，共16码)，订单编号后8位+交易类型1码(1：取貨付款 3：取貨不付款 )+5码代收金额+2码检查码
					//檢查碼第一碼計算公式：各段條碼之奇數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 0，若餘數為 10 則放 1）。 
					//檢查碼第二碼計算公式：各段條碼之偶數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 8，若餘數為 10 則放 9） 
					String barcode2=child.elementText("BC2");
					//System.out.println(barcode2);

					//组11码配送编号
					String expressno= barcode1.substring(3,6) + barcode2.substring(0,8);

					//代收金额
					String sCollectAMT=barcode2.substring(9,14);

					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//實際代收日期 YYYYMMDD
					//System.out.println(child.elementText("RTDT"));		
					// 繳費代碼 ,空值
					//System.out.println(child.elementText("PINCODE"));			
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return "";
	}

	/**
	 * 對帳檔 ,每月 5 日
	 * @param localfile
	 * f20_expressno
	 * f20_ecno						
	 * f20_getshopno
	 * f20_status
	 * f20_reason
	 * f20_tradetype
	 * f20_collectamt
	 * f20_realamt
	 * f20_fee
	 * f20_bdate
	 * @return
	 */
	public List<Map<String, Object>> F20(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F20DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F20DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F20CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//結帳狀態碼 1：取貨完成 	2：退貨完成 3：取消代收 
					//System.out.println(child.elementText("STATUS"));
					//结账状态原因，無資料空白 
					//System.out.println(child.elementText("RET_R"));
					// 交易方式識別碼，1：取貨付款 	3：取貨不付款 4：退貨不付款 5：退貨付款  6：代收款 					 
					//System.out.println(child.elementText("TRADETYPE"));
					//結帳基準日，YYYYMMDD
					//System.out.println(child.elementText("TKDT"));
					//代收金額，狀態 3 時，代收金額為負項 
					//System.out.println(child.elementText("AMT"));
					//商品實際金
					//System.out.println(child.elementText("REALAMT"));
					//手續費，狀態 3 時，手續費為負項 
					//System.out.println(child.elementText("FEE"));
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f20_expressno", child.elementText("ODNO"));
					map.put("f20_ecno", child.elementText("ECNO"));									
					map.put("f20_getshopno", child.elementText("STNO"));	
					map.put("f20_status", child.elementText("STATUS"));
					map.put("f20_reason", child.elementText("RET_R"));
					map.put("f20_tradetype", child.elementText("TRADETYPE"));
					map.put("f20_collectamt", child.elementText("AMT"));
					map.put("f20_realamt", child.elementText("REALAMT"));
					map.put("f20_fee", child.elementText("FEE"));
					map.put("f20_bdate", child.elementText("TKDT"));					
						

					lstMaps.add(map);		

				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}


	/**
	 * 應退未退對帳檔,每月 5 日
	 * @param localfile
	 * f21_expressno
	 * f21_ecno						
	 * f21_getshopno
	 * f21_status
	 * f21_reason
	 * f21_tradetype
	 * f21_collectamt
	 * f21_realamt
	 * f21_fee
	 * f21_bdate
	 * @return
	 */
	public List<Map<String, Object>> F21(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F21DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F21DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F21CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//結帳狀態碼  3：異常結案
					//System.out.println(child.elementText("STATUS"));
					//结账状态原因，
					//D01 : 大物流遺失 					
					//D02 : 未送貨到小物流 
					//S03 : 小物流遺失 
					//N05 : 門市遺失 
					//System.out.println(child.elementText("RET_R"));
					// 交易方式識別碼，1：取貨付款 	3：取貨不付款 4：寄件不付款 5：寄件付款			 
					//System.out.println(child.elementText("TRADETYPE"));
					//結帳基準日，YYYYMMDD
					//System.out.println(child.elementText("TKDT"));
					//代收金額，
					//System.out.println(child.elementText("AMT"));
					//商品實際金
					//System.out.println(child.elementText("REALAMT"));
					//手續費，
					//System.out.println(child.elementText("FEE"));
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f21_expressno", child.elementText("ODNO"));
					map.put("f21_ecno", child.elementText("ECNO"));									
					map.put("f21_getshopno", child.elementText("STNO"));	
					map.put("f21_status", child.elementText("STATUS"));
					map.put("f21_reason", child.elementText("RET_R"));
					map.put("f21_tradetype", child.elementText("TRADETYPE"));
					map.put("f21_collectamt", child.elementText("AMT"));
					map.put("f21_realamt", child.elementText("REALAMT"));
					map.put("f21_fee", child.elementText("FEE"));
					map.put("f21_bdate", child.elementText("TKDT"));					
						

					lstMaps.add(map);		


				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}

	/**
	 * 進店即時檔 ,整點:10分,F44RRRSSSYYYYMMDD.xml 
	 * @param localfile
	 *f44_expressno物流编号
	 *f44_ecno厂商代码
	 *f44_distributeno通路商代码
	 *f44_distributename通路商名称
	 *f44_getshopno取货门店
	 *f44_sdate时间进店日期
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> F44(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();

		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F44DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F44DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F44CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//实际进店日期YYYYMMDD
					//System.out.println(child.elementText("DCSTDT"));	

					String EASYECNO=child.elementText("EASYECNO");
					if (EASYECNO!=null) 
					{						
						//寄件通業務代碼 ,若無此業務,會帶空值 
						//System.out.println(EASYECNO);			
					}

					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f44_expressno", child.elementText("ODNO"));					
					map.put("f44_ecno", child.elementText("ECNO"));
					map.put("f44_distributeno", child.elementText("STNO").substring(0,1));
					if (child.elementText("STNO").substring(0,1).equals("F")) 
					{
						map.put("f44_distributename", "全家");
					}
					else if (child.elementText("STNO").substring(0,1).equals("L")) 
					{
						map.put("f44_distributename", "萊爾富");
					}
					else if (child.elementText("STNO").substring(0,1).equals("K")) 
					{
						map.put("f44_distributename", "OK");
					}

					map.put("f44_getshopno", child.elementText("STNO").substring(1));
					map.put("f44_sdate", child.elementText("FRTDCDT"));		

					lstMaps.add(map);

				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}


	/**
	 * 取消代收檔 (物流中心過刷後的出貨單，代表物流中心產生新二段條碼),應該是在大物流驗收檔之前，20：00
	 * 這個是物流中心產生條碼
	 * @param localfile
	 * f45_expressno
	 * f45_bc1
	 * f45_bc2
	 * f45_getshopno
	 * f45_bdate
	 * @return
	 */
	public List<Map<String, Object>> F45(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F45DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F45DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F45CONTENT")) 
				{
					//文化物流二段条码(第一段条码,共9码)，3码EC厂商代码+订单编号前3码+3码代收代号
					String barcode1=child.elementText("BC1");
					//System.out.println(barcode1);
					//文化物流二段条码(第二段条码，共16码)，订单编号后8位+交易类型1码(1：取貨付款 3：取貨不付款 )+5码代收金额+2码检查码
					//檢查碼第一碼計算公式：各段條碼之奇數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 0，若餘數為 10 則放 1）。 
					//檢查碼第二碼計算公式：各段條碼之偶數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 8，若餘數為 10 則放 9） 
					String barcode2=child.elementText("BC2");
					//System.out.println(barcode2);

					//组11码配送编号
					String expressno= barcode1.substring(3,6) + barcode2.substring(0,8);

					//代收金额
					String sCollectAMT=barcode2.substring(9,14);

					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//實際取货代收日期 YYYYMMDD
					//System.out.println(child.elementText("RTDT"));		
					//結帳基準日 YYYYMMDD
					//System.out.println(child.elementText("TKDT"));		
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f45_expressno", expressno);									
					map.put("f45_bc1", barcode1);
					map.put("f45_bc2", barcode2);				
					map.put("f45_getshopno", child.elementText("STNO"));					
					map.put("f45_bdate", child.elementText("RTDT"));						

					lstMaps.add(map);		
					
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}


	/**
	 * 訂單預退檔(未來三天閉店或整修店鋪之訂單清單) ,14：30，C2C也是這個
	 * @param localfile
	 * f61_expressno 托運單號			
	 * f62_ecno      EC廠商代碼
	 * f62_getshopno 取貨門店				
	 * f62_bdate  預計退貨日
	 * @return
	 */
	public List<Map<String, Object>> F61_ALL(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F61DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F61DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F61CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));					
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//订单编号
					//System.out.println(child.elementText("ODNO"));		
					//預計退貨日(即預計之最後取貨日)  , YYYYMMDD
					//System.out.println(child.elementText("RTDT"));	
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f62_expressno", child.elementText("ODNO"));									
					map.put("f62_ecno", child.elementText("ECNO"));
					map.put("f62_getshopno", child.elementText("STNO"));					
					map.put("f62_bdate", child.elementText("RTDT"));						

					lstMaps.add(map);			
					
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}


	/**
	 * 離店檔 ,每 15 分鐘 ,退貨流程
	 * @param localfile
	 * f84_expressno
	 * f84_ecno
	 * f84_getshopno
	 * f84_bdate
	 * @return
	 */
	public List<Map<String, Object>> F84_ALL(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F84DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F84DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F84CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//實際離店日YYYYMMDD
					//System.out.println(child.elementText("DCSTDT"));	

					String EASYECNO=child.elementText("EASYECNO");
					if (EASYECNO!=null) 
					{						
						//寄件通業務代碼 ,若無此業務,會帶空值 
						//System.out.println(EASYECNO);			
					}
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f84_expressno", child.elementText("ODNO"));									
					map.put("f84_ecno", child.elementText("ECNO"));
					map.put("f84_getshopno", child.elementText("STNO"));					
					map.put("f84_bdate", child.elementText("DCSTDT"));						

					lstMaps.add(map);			

				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}





	/**
	 * 店到店订单上传，F60CVS10120090901103020.xml
	 * @param apiUrl FTP地址
	 * @param ecNo   EC廠商代碼
	 * @param collectNo 取件代收代號
	 * @param pubCode 取件公版代號，固定515
	 * @param OrderList(
	 * ODNO:物流单号，有規則，取件订单编号11码，不是訂單中心的訂單號
	 * STNO:取货门店编号，代码(F：全家、L：萊爾富、K：OK)+门市编号
	 * AMT:代收金额， >=0 int
	 * CUTKNM:取货人姓名
	 * CUTKTL:手机末三码，無資料代空值 
	 * PRODNM:商品別代，0 : 一般商品  1 : 票券商品
	 * ECWEB: EC網站名，例：康迅數位 www.payeasy.com.tw
	 * ECSERTEL ：EC 網站客服電話，例：02-XXXXXXXX 
	 * REALAMT：商品實際金額，int 必須為 0 或正整數。 ※選擇取貨不付款與取貨付款時，需在此欄位填上貨物「實際金額」，以利後續查帳事宜
	 * TRADETYPE：交易方式識別碼(货到付款标记) ，1：取貨付款 3：取貨不付款 
	 * VENDOR：這裡填客戶名稱，例如：安琪兒
	 * VENDORNO：這裡填訂單中心訂單號，
	 * ORDERMODE：訂單模式 , A:新增訂單  U:更新訂單 
	 * PINCODE：寄件單號 ,1.僅於門市 KIOSK 機台輸入專用  2.編碼規則(共 12 碼)： '1'(一碼)+取件訂單編號(11 碼 ODNO) 
	 * STECNO：寄件公版代號,525(固定值)
	 * STAMT：寄件繳費金額,0(固定值)
	 * STSERCODE：寄件代收代號,998(固定值)
	 * )
	 * @return
	 */

	public boolean F60_C2C(String apiUrl,String ecNo,String collectNo,String pubCode, List<Map<String, Object>> OrderList)
	{
		////F60CVS+EC厂商代码+YYYYMMDDhhmmss.xml 
		try 
		{
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMddHHmmss");
			String eDatetime=dfDate.format(cal.getTime());
			String filename="F60CVS" + ecNo + eDatetime +".xml";

			String filePath="D:\\";			

			File xmlFile = new File(filePath + filename);

			Document document = DocumentHelper.createDocument();
			Element ORDER_DOC = document.addElement("ORDER_DOC");

			for (Map<String, Object> oneData : OrderList)
			{
				Element ORDER = ORDER_DOC.addElement("ORDER");		

				//取件訂單編號，有規則的
				String ODNO= oneData.get("ODNO").toString();
				//<![CDATA[達康購物網]]>
				//特殊＜  ＞  ＆ ‘…
				boolean isSpecial= isSpecialChar(ODNO);
				if (isSpecial) 
				{
					ODNO=String.format("<![CDATA[%s]]>", ODNO);
				}			

				//例 F001234 
				String STNO= oneData.get("STNO").toString();
				isSpecial= isSpecialChar(STNO);
				if (isSpecial) 
				{
					STNO=String.format("<![CDATA[%s]]>", STNO);
				}			

				String AMT=oneData.get("AMT").toString() ;

				//取貨人中文姓名，中文最長 10 個字 
				String CUTKNM= oneData.get("CUTKNM").toString();				
				isSpecial= isSpecialChar(CUTKNM);
				if (isSpecial) 
				{
					CUTKNM=String.format("<![CDATA[%s]]>", CUTKNM);
				}	
				//取貨人電話，手機末三碼
				String CUTKTL= oneData.get("CUTKTL").toString();
				isSpecial= isSpecialChar(CUTKTL);
				if (isSpecial) 
				{
					CUTKTL=String.format("<![CDATA[%s]]>", CUTKTL);
				}	

				//商品別代碼，0 : 一般商品  1 : 票券商品
				String PRODNM= oneData.get("PRODNM").toString();

				// EC網站名稱 ,
				String ECWEB= oneData.get("ECWEB").toString();
				isSpecial= isSpecialChar(ECWEB);
				if (isSpecial) 
				{
					ECWEB=String.format("<![CDATA[%s]]>", ECWEB);
				}		

				//EC網站客服電話
				String ECSERTEL= oneData.get("ECSERTEL").toString();
				isSpecial= isSpecialChar(ECSERTEL);
				if (isSpecial) 
				{
					ECSERTEL=String.format("<![CDATA[%s]]>", ECSERTEL);
				}		

				//商品實際金額，
				String REALAMT= oneData.get("REALAMT").toString();

				//1：取貨付款  3：取貨不付款 
				String TRADETYPE= oneData.get("TRADETYPE").toString();

				//供應商名稱，EC廠商自訂
				String VENDOR= oneData.get("VENDOR").toString();
				//供應商編碼，EC廠商自訂
				String VENDORNO= oneData.get("VENDORNO").toString();	
				//訂單模式 , A:新增訂單  U:更新訂單 
				String ORDERMODE= oneData.get("ORDERMODE").toString();
				//寄件單號 ,1.僅於門市 KIOSK 機台輸入專用  2.編碼規則(共 12 碼)： '1'(一碼)+取件訂單編號(11 碼 ODNO) 
				String PINCODE= oneData.get("PINCODE").toString();
				//寄件公版代號,525(固定值)
				String STECNO= oneData.get("STECNO").toString();
				//寄件繳費金額,0(固定值)
				String STAMT= oneData.get("STAMT").toString();
				//寄件代收代號,998(固定值)
				String STSERCODE= oneData.get("STSERCODE").toString();


				//取件公版代號，
				Element ePUBLICECNO=ORDER.addElement("PUBLICECNO");
				ePUBLICECNO.setText(pubCode);			
				//取件訂單編號，
				Element eODNO=ORDER.addElement("ODNO");
				eODNO.setText(ODNO);
				//取货门店编码7码，F：全家、L：萊爾富、K：OK 
				Element eSTNO=ORDER.addElement("STNO");
				eSTNO.setText(STNO);
				//代收金额5码
				Element eAMT=ORDER.addElement("AMT");
				eAMT.setText(AMT);
				//取货人姓名10码
				Element eCUTKNM=ORDER.addElement("CUTKNM");
				eCUTKNM.setText(CUTKNM);
				//手机号末3码
				Element eCUTKTL=ORDER.addElement("CUTKTL");
				eCUTKTL.setText(CUTKTL);
				//商品别代码1码，0 : 一般商品  1 : 票券商品 
				Element ePRODNM=ORDER.addElement("PRODNM");
				ePRODNM.setText(PRODNM);
				// EC 網站名稱 20码，例：康迅數位 www.payeasy.com.tw 
				Element eECWEB=ORDER.addElement("ECWEB");
				eECWEB.setText(ECWEB);
				// EC 網站客服電話 20码，例：02-XXXXXXXX
				Element eECSERTEL=ORDER.addElement("ECSERTEL");
				eECSERTEL.setText(ECSERTEL);
				//商品實際金額5码
				Element eREALAMT=ORDER.addElement("REALAMT");
				eREALAMT.setText(REALAMT);
				// 交易方式識別碼1码，1：取貨付款 3：取貨不付款  
				Element eTRADETYPE=ORDER.addElement("TRADETYPE");
				eTRADETYPE.setText(TRADETYPE);
				//取件代收代號3码
				Element eSERCODE=ORDER.addElement("SERCODE");
				eSERCODE.setText(collectNo);

				//這裡填客戶名稱，安琪兒
				Element eVENDOR=ORDER.addElement("VENDOR");
				eVENDOR.setText(VENDOR);
				//這裡填訂單中心訂單號，
				Element eVENDORNO=ORDER.addElement("VENDORNO");
				eVENDORNO.setText(VENDORNO);

				//訂單模式 , A:新增訂單  U:更新訂單 
				Element eORDERMODE=ORDER.addElement("ORDERMODE");
				eORDERMODE.setText(ORDERMODE);
				//寄件單號 ,1.僅於門市 KIOSK 機台輸入專用  2.編碼規則(共 12 碼)： '1'(一碼)+取件訂單編號(11 碼 ODNO) 
				Element ePINCODE=ORDER.addElement("PINCODE");
				ePINCODE.setText(PINCODE);
				//寄件公版代號,525(固定值)
				Element eSTECNO=ORDER.addElement("STECNO");
				eSTECNO.setText(STECNO);
				//寄件繳費金額,0(固定值)
				Element eSTAMT=ORDER.addElement("STAMT");
				eSTAMT.setText(STAMT);				
				//寄件代收代號,998(固定值)
				Element eSTSERCODE=ORDER.addElement("STSERCODE");
				eSTSERCODE.setText(STSERCODE);			

			}

			String TOTALS=OrderList.size()+"";

			Element ORDERCOUNT = ORDER_DOC.addElement("ORDERCOUNT");
			Element eTOTALS=ORDERCOUNT.addElement("TOTALS");
			eTOTALS.setText(TOTALS);

			FileOutputStream fos=new FileOutputStream(xmlFile);			
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter xmlWriter =new XMLWriter(fos, format);
			xmlWriter.write(document);			
			fos.close();
			fos=null;
			xmlWriter.close();	
			xmlWriter=null;
			document=null;
			xmlFile=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
			return false;
		}

		return true;
	}


	/**
	 * 大物流驗退檔 ,21：30,F67RRRSSSYYYYMMDD.xml
	 * @param localfile
	 * f67_expressno物流单号
	 * f67_sdate验退日期
	 * f67_reasonno验退原因代码
	 * f67_reasonname验退原因名称
	 * f67_ecno厂商代码
	 * f67_getshopno取货门店
	 * f67_bdate结账日期
	 * f67_orderno訂單中心訂單號
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> F67_C2C(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F67DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F67DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F67CONTENT")) 
				{
					//退貨原因 ,
					//T00 : 正常驗退 
					//T01 : 閉店、整修、無路線路順 
					//T02 : 無進貨資料 
					//T03 : 條碼異常(錯誤、重複) 
					//D04 : 大物流包裝不良(滲漏) 
					//T05 : 貨物進店後發生異常提早退貨 
					//S06 : 小物流破損 
					//S07 : 門市反應商品包裝不良(滲漏) 
					//D08 : 大物流分貨錯誤 
					//System.out.println(child.elementText("RET_M"));
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//ODNO:物流单号，有規則，取件订单编号11码，不是訂單中心的訂單號
					//System.out.println(child.elementText("ODNO"));
					//大物流實際驗退日 YYYYMMDD
					//System.out.println(child.elementText("RTDCDT"));
					//結帳基準日  YYYYMMDD
					//System.out.println(child.elementText("FRTDCDT"));
					//供應商代號
					//System.out.println(child.elementText("VENDOR"));
					//供應商原始單
					//System.out.println(child.elementText("VENDORNO"));

					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f67_expressno", child.elementText("ODNO"));
					map.put("f67_sdate", child.elementText("RTDCDT"));
					map.put("f67_reasonno", child.elementText("RET_M"));
					if (child.elementText("RET_M").equals("T00")) 
					{
						map.put("f67_reasonname", "正常驗退");
					}
					else if (child.elementText("RET_M").equals("T01")) 
					{
						map.put("f67_reasonname", "閉店、整修、無路線路順 ");
					}
					else if (child.elementText("RET_M").equals("T02")) 
					{
						map.put("f67_reasonname", "無進貨資料");
					}
					else if (child.elementText("RET_M").equals("T03")) 
					{
						map.put("f67_reasonname", "條碼異常(錯誤、重複)");
					}		
					else if (child.elementText("RET_M").equals("D04")) 
					{
						map.put("f67_reasonname", "大物流包裝不良(滲漏)");
					}
					else if (child.elementText("RET_M").equals("T05")) 
					{
						map.put("f67_reasonname", "貨物進店後發生異常提早退貨");
					}					
					else if (child.elementText("RET_M").equals("S06")) 
					{
						map.put("f67_reasonname", "小物流破損 ");
					}
					else if (child.elementText("RET_M").equals("S07")) 
					{
						map.put("f67_reasonname", "門市反應商品包裝不良(滲漏)");
					}
					else if (child.elementText("RET_M").equals("D08")) 
					{
						map.put("f67_reasonname", "大物流分貨錯誤 ");
					}

					map.put("f67_ecno", child.elementText("ECNO"));
					map.put("f67_getshopno", child.elementText("STNO"));
					map.put("f67_bdate", child.elementText("FRTDCDT"));	
					map.put("f67_orderno", child.elementText("VENDORNO"));	
					

					lstMaps.add(map);
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	
	
	/**
	 * 取消出货档,23：30 F69RRRCVSYYYYMMDD.xml 
	 * @param localfile
	 * f69_expressno
	 * f69_reasonno
	 * f69_reasonname
	 * f69_ecno
	 * f69_getshopno
	 * f69_orderno
	 * @return
	 */
	public List<Map<String, Object>> F69_C2C(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F69DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F69DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F69CONTENT")) 
				{
					//取消類型,透過取消訂單原因判斷 
					//T1 : 系統取消，EC自行在後台取消 
					//D1 : 大物流取消 
					//S1 : 小物流取消 
					//N1 : 門市取消
					//System.out.println(child.elementText("RET_M"));

					//取消訂單原因 ,
					//D01 : 大物流遺失 
					//D02 : 未送貨到小物流 
					//S03 : 小物流遺失 
					//N04 : 通路取消出貨 
					//N05 : 門市遺失 
					//T06 : 30 日未驗取消 
					//T07 : EC 自行取消 
					//T01 : 閉店、整修、無路線路順 
					//T02 : 無進貨資料 
					//T03 : 條碼錯誤 
					//T04 : 條碼重複 
					//D04 : 大物流包裝不良(滲漏) 
					//T05 : 貨物進店後發生異常提早退貨 
					//S06 : 小物流破損 
					//S07 : 門市反應商品包裝不良(滲漏)
					//System.out.println(child.elementText("RET_R"));

					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//物流单号，有規則，取件订单编号11码，不是訂單中心的訂單號
					//System.out.println(child.elementText("ODNO"));
					//供應的名稱
					//System.out.println(child.elementText("VENDOR"));
					//供應商代號，訂單號
					//System.out.println(child.elementText("VENDORNO"));

					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f69_expressno", child.elementText("ODNO"));					
					map.put("f69_reasonno", child.elementText("RET_M"));
					if (child.elementText("RET_R").equals("D01")) 
					{
						map.put("f69_reasonname", "大物流遺失 ");
					}
					else if (child.elementText("RET_R").equals("D02")) 
					{
						map.put("f69_reasonname", "未送貨到小物流 ");
					}
					else if (child.elementText("RET_R").equals("S03")) 
					{
						map.put("f69_reasonname", "小物流遺失 ");
					}
					else if (child.elementText("RET_R").equals("N04")) 
					{
						map.put("f69_reasonname", "通路取消出");
					}		
					else if (child.elementText("RET_R").equals("N05")) 
					{
						map.put("f69_reasonname", "門市遺失");
					}
					else if (child.elementText("RET_R").equals("T01")) 
					{
						map.put("f69_reasonname", "閉店、整修、無路線路順 ");
					}		
					else if (child.elementText("RET_R").equals("T02")) 
					{
						map.put("f69_reasonname", "無進貨資料 ");
					}
					else if (child.elementText("RET_R").equals("T03")) 
					{
						map.put("f69_reasonname", "條碼錯誤 ");
					}	
					else if (child.elementText("RET_R").equals("T04")) 
					{
						map.put("f69_reasonname", "條碼重複 ");
					}	
					else if (child.elementText("RET_R").equals("T05")) 
					{
						map.put("f69_reasonname", "貨物進店後發生異常提早退貨");
					}				
					else if (child.elementText("RET_R").equals("T06")) 
					{
						map.put("f69_reasonname", "30 日未驗取消 ");
					}		
					else if (child.elementText("RET_R").equals("T07")) 
					{
						map.put("f69_reasonname", "EC自行取消 ");
					}				
					else if (child.elementText("RET_R").equals("S06")) 
					{
						map.put("f69_reasonname", "小物流破損 ");
					}
					else if (child.elementText("RET_R").equals("S07")) 
					{
						map.put("f69_reasonname", "門市反應商品包裝不良(滲漏)");
					}
					else if (child.elementText("RET_R").equals("D04")) 
					{
						map.put("f69_reasonname", "大物流包裝不良(滲漏)  ");
					}

					map.put("f69_ecno", child.elementText("ECNO"));
					map.put("f69_getshopno", child.elementText("STNO"));					
					map.put("f69_orderno", child.elementText("VENDORNO"));						

					lstMaps.add(map);					
				}
			}

			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	
	

	/**
	 * 订单回复档，錯誤列表
	 * @param localfile
	 * f62_expressno 托運單號			
	 * f62_reasonno  錯誤代碼
	 * f62_reasonname	錯誤說明			
	 * f62_ecno      取件公版代號
	 * f62_getshopno 取貨門店				
	 * f62_orderno  訂單中心訂單號
	 * @return
	 */	 
	public List<Map<String, Object>> F62_C2C(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element RTN_ORDER_DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = RTN_ORDER_DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("RTN_RESULT")) 
				{
					//處理日期時間yyyy-mm-dd hh24:mi:ss 
					//System.out.println(child.elementText("PROCDATE"));
					//處理成功筆數 
					//System.out.println(child.elementText("PROCCNT"));
					//處理失敗筆數 
					//System.out.println(child.elementText("ERRCNT"));
				}

				if (child.getName().equals("ERR_ORDER")) 
				{
					//錯誤代碼, 1 : 踢退（資料有誤且不成單） 	2 : 異常（資料部份有誤但成單） 
					//System.out.println(child.elementText("ERRCODE"));
					//錯誤說明 
					//System.out.println(child.elementText("ERRDESC"));
					//取件公版代號
					//System.out.println(child.elementText("PUBLICECNO"));
					//取件訂單號碼(物流单号)
					//System.out.println(child.elementText("ODNO"));
					// 取貨門市編號7码
					//System.out.println(child.elementText("STNO"));
					//代收金額
					//System.out.println(child.elementText("AMT"));
					//取貨人姓名 
					//System.out.println(child.elementText("CUTKNM"));
					//手機末三碼 
					//System.out.println(child.elementText("CUTKTL"));
					//商品別代碼 
					//System.out.println(child.elementText("PRODNM"));
					//EC 網站名稱 
					//System.out.println(child.elementText("ECWEB"));
					//EC網站客服電話 
					//System.out.println(child.elementText("ECSERTEL"));
					//商品實際金額
					//System.out.println(child.elementText("REALAMT"));
					//交易方式識別碼 
					//System.out.println(child.elementText("TRADETYPE"));
					//代收代號  
					//System.out.println(child.elementText("SERCODE"));
					//大物流代號 
					//System.out.println(child.elementText("EDCNO"));
					//供應商名稱
					//System.out.println(child.elementText("VENDOR"));
					//供應商單號=訂單中心訂單號
					//System.out.println(child.elementText("VENDORNO"));
					//1+ODNO
					//System.out.println(child.elementText("PINCODE"));
					//寄件公版號525
					//System.out.println(child.elementText("STECNO"));
					//寄件交付金額0
					//System.out.println(child.elementText("STAMT"));
					//寄件代收代號998
					//System.out.println(child.elementText("STSERCODE"));
					//訂單模式
					//System.out.println(child.elementText("ORDERMODE"));
					
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f62_expressno", child.elementText("ODNO"));					
					map.put("f62_reasonno", child.elementText("ERRCODE"));
					map.put("f62_reasonname", child.elementText("ERRDESC"));					
					map.put("f62_ecno", child.elementText("PUBLICECNO"));
					map.put("f62_getshopno", child.elementText("STNO"));					
					map.put("f62_orderno", child.elementText("VENDORNO"));						

					lstMaps.add(map);			
					
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	
	
	/**
	 * 大物流店到店寄件驗收檔 
	 * @param localfile
	 * f71_expressno 
	 * f71_ecno
	 * f71_sendshopno 寄件門店
	 * f71_bdate 大物流驗收日期
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> F71_C2C(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F71DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F71DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F71CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));					
					//寄件代收門市編號   F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//订单编号
					//System.out.println(child.elementText("ODNO"));		
					//大物流實際驗收日   , YYYYMMDD
					//System.out.println(child.elementText("RTDT"));	
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f71_expressno", child.elementText("ODNO"));									
					map.put("f71_ecno", child.elementText("ECNO"));
					map.put("f71_sendshopno", child.elementText("STNO"));					
					map.put("f71_bdate", child.elementText("RTDT"));						

					lstMaps.add(map);			
					
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	

	/**
	 * 大物流驗收檔(批次) ,22：30 F63173CVS20090901.xml 
	 * @param localfile
	 * f63_expressno物流单号
	 * f63_ecno厂商代码
	 * f63_distributeno通路商代码
	 * f63_distributename通路商名称
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> F63(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F63DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F63DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F63CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//通路商代码 TFM：全家 TLF：萊爾富 	TOK：OK
					//System.out.println(child.elementText("CNNO"));
					//取货人姓名
					//System.out.println(child.elementText("CUName"));
					//商品别代码 0:一般商品  1:票券商品 
					//System.out.println(child.elementText("ProdType"));
					//
					//System.out.println(child.elementText("PINCODE"));		


					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f63_expressno", child.elementText("ODNO"));
					map.put("f63_ecno", child.elementText("ECNO"));
					map.put("f63_distributeno", child.elementText("CNNO"));
					if (child.elementText("CNNO").equals("TFM")) 
					{
						map.put("f63_distributename", "全家");
					}
					else if (child.elementText("CNNO").equals("TLF")) 
					{
						map.put("f63_distributename", "萊爾富");
					}
					else 
					{
						map.put("f63_distributename", "OK");
					}

					lstMaps.add(map);
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	

	/**
	 * 進店即時檔 ,整點:10分,F44RRRSSSYYYYMMDD.xml 
	 * @param localfile
	 *f44_expressno物流编号
	 *f44_ecno厂商代码
	 *f44_distributeno通路商代码
	 *f44_distributename通路商名称
	 *f44_getshopno取货门店
	 *f44_sdate时间进店日期
	 *f44_orderno 訂單中心訂單號
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> F44_C2C(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();

		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F44DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F44DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F44CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//实际进店日期YYYYMMDD
					//System.out.println(child.elementText("DCSTDT"));	

					//供應商名稱
					String VENDOR =child.elementText("VENDOR");
					//System.out.println(VENDOR);						
					//供應商代碼=訂單中心訂單號
					String VENDORNO =child.elementText("VENDORNO");
					//System.out.println(VENDORNO);	
					

					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f44_expressno", child.elementText("ODNO"));					
					map.put("f44_ecno", child.elementText("ECNO"));
					map.put("f44_distributeno", child.elementText("STNO").substring(0,1));
					if (child.elementText("STNO").substring(0,1).equals("F")) 
					{
						map.put("f44_distributename", "全家");
					}
					else if (child.elementText("STNO").substring(0,1).equals("L")) 
					{
						map.put("f44_distributename", "萊爾富");
					}
					else if (child.elementText("STNO").substring(0,1).equals("K")) 
					{
						map.put("f44_distributename", "OK");
					}

					map.put("f44_getshopno", child.elementText("STNO").substring(1));
					map.put("f44_sdate", child.elementText("FRTDCDT"));		
					map.put("f44_orderno", child.elementText("VENDORNO"));		
					
					lstMaps.add(map);

				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	
	/**
	 * 取消代收檔 (物流中心過刷後的出貨單，代表物流中心產生新二段條碼),應該是在大物流驗收檔之前，20：00
	 * 這個是物流中心產生條碼
	 * @param localfile
	 * f55_expressno
	 * f55_bc1
	 * f55_bc2
	 * f55_getshopno
	 * f55_bdate
	 * f55_orderno
	 * @return
	 */
	public List<Map<String, Object>> F55_C2C(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F55DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F55DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F55CONTENT")) 
				{
					//文化物流二段条码(第一段条码,共9码)，3码EC厂商代码+订单编号前3码+3码代收代号
					String barcode1=child.elementText("BC1");
					//System.out.println(barcode1);
					//文化物流二段条码(第二段条码，共16码)，订单编号后8位+交易类型1码(1：取貨付款 3：取貨不付款 )+5码代收金额+2码检查码
					//檢查碼第一碼計算公式：各段條碼之奇數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 0，若餘數為 10 則放 1）。 
					//檢查碼第二碼計算公式：各段條碼之偶數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 8，若餘數為 10 則放 9） 
					String barcode2=child.elementText("BC2");
					//System.out.println(barcode2);

					//组11码配送编号
					String expressno= barcode1.substring(3,6) + barcode2.substring(0,8);

					//代收金额
					String sCollectAMT=barcode2.substring(9,14);

					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//實際取货代收日期 YYYYMMDD
					//System.out.println(child.elementText("RTDT"));		
					//結帳基準日 YYYYMMDD
					//System.out.println(child.elementText("TKDT"));		
					
					//供應商名稱
					//System.out.println(child.elementText("VENDOR"));	
					//供應商代號=訂單中心訂單號
					//System.out.println(child.elementText("VENDORNO"));	
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f55_expressno", expressno);									
					map.put("f55_bc1", barcode1);
					map.put("f55_bc2", barcode2);				
					map.put("f55_getshopno", child.elementText("STNO"));					
					map.put("f55_bdate", child.elementText("RTDT"));	
					map.put("f55_orderno", child.elementText("VENDORNO"));						

					lstMaps.add(map);		
					
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	
	/**
	 * 門店寄件代收檔  ，代表寄件門店已驗收20：00
	 * @param localfile
	 * f25_orderno
	 * f25_bc1
	 * f25_bc2
	 * f25_shopno
	 * f25_bdate
	 * @return
	 */
	public List<Map<String, Object>> F25_C2C(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F25DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F25DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F25CONTENT")) 
				{
					//文化物流二段条码(第一段条码,共9码)，3码EC厂商代码+订单编号前3码+3码代收代号
					String barcode1=child.elementText("BC1");
					//System.out.println(barcode1);
					//文化物流二段条码(第二段条码，共16码)，订单编号后8位+交易类型1码(1：取貨付款 3：取貨不付款 )+5码代收金额+2码检查码
					//檢查碼第一碼計算公式：各段條碼之奇數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 0，若餘數為 10 則放 1）。 
					//檢查碼第二碼計算公式：各段條碼之偶數位之值【由左算起】加總後，值除以 11 後 之餘數（若餘數為 0 則放 8，若餘數為 10 則放 9） 
					String barcode2=child.elementText("BC2");
					//System.out.println(barcode2);

					//组11码配送编号
					String orderno= barcode1.substring(3,6) + barcode2.substring(0,8);

					//代收金额
					String sCollectAMT=barcode2.substring(9,14);

					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//實際取货代收日期 YYYYMMDD
					//System.out.println(child.elementText("RTDT"));		
					//結帳基準日 YYYYMMDD
					//System.out.println(child.elementText("TKDT"));						
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f25_orderno", orderno);									
					map.put("f25_bc1", barcode1);
					map.put("f25_bc2", barcode2);				
					map.put("f25_shopno", child.elementText("STNO"));					
					map.put("f25_bdate", child.elementText("RTDT"));										

					lstMaps.add(map);		
					
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	
	/**
	 * 門店即時寄件代收檔  ，代表寄件門店已驗收
	 * @param localfile
	 * f27_orderno	
	 * f27_shopno
	 * f27_bdate
	 * @return
	 */
	public List<Map<String, Object>> F27_C2C(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F27DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F27DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F27CONTENT")) 
				{
					//System.out.println(child.elementText("ECNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//實際取货代收日期 YYYYMMDD
					//System.out.println(child.elementText("RTDT"));		
					//訂單號碼
					//System.out.println(child.elementText("ODNO"));						
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f27_orderno", child.elementText("ODNO"));
					map.put("f27_shopno", child.elementText("STNO"));					
					map.put("f27_bdate", child.elementText("RTDT"));										

					lstMaps.add(map);		
					
				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	
	/**
	 * 對帳檔 ,每月 5 日
	 * @param localfile
	 * f620_expressno
	 * f620_ecno						
	 * f620_getshopno
	 * f620_status
	 * f620_reason
	 * f620_tradetype
	 * f620_collectamt
	 * f620_realamt
	 * f620_fee
	 * f620_bdate
	 * f620_orderno
	 * @return
	 */
	public List<Map<String, Object>> F620_C2C(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F620DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F620DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F620CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//結帳狀態碼 1：取貨完成 	2：退貨完成 3：取消代收 
					//System.out.println(child.elementText("STATUS"));
					//结账状态原因，無資料空白 
					//System.out.println(child.elementText("RET_R"));
					// 交易方式識別碼，1：取貨付款 	3：取貨不付款 4：退貨不付款 5：退貨付款  6：代收款 					 
					//System.out.println(child.elementText("TRADETYPE"));
					//結帳基準日，YYYYMMDD
					//System.out.println(child.elementText("TKDT"));
					//代收金額，狀態 3 時，代收金額為負項 
					//System.out.println(child.elementText("AMT"));
					//商品實際金
					//System.out.println(child.elementText("REALAMT"));
					//手續費，狀態 3 時，手續費為負項 
					//System.out.println(child.elementText("FEE"));
					//
					//System.out.println(child.elementText("VENDOR"));
					//
					//System.out.println(child.elementText("VENDORNO"));
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f620_expressno", child.elementText("ODNO"));
					map.put("f620_ecno", child.elementText("ECNO"));									
					map.put("f620_getshopno", child.elementText("STNO"));	
					map.put("f620_status", child.elementText("STATUS"));
					map.put("f620_reason", child.elementText("RET_R"));
					map.put("f620_tradetype", child.elementText("TRADETYPE"));
					map.put("f620_collectamt", child.elementText("AMT"));
					map.put("f620_realamt", child.elementText("REALAMT"));
					map.put("f620_fee", child.elementText("FEE"));
					map.put("f620_bdate", child.elementText("TKDT"));					
					map.put("f620_orderno", child.elementText("VENDORNO"));

					lstMaps.add(map);		

				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	
	
	/**
	 * 應退未退對帳檔,每月 5 日
	 * @param localfile
	 * f621_expressno
	 * f621_ecno						
	 * f621_getshopno
	 * f621_status
	 * f621_reason
	 * f621_tradetype
	 * f621_collectamt
	 * f621_realamt
	 * f621_fee
	 * f621_bdate
	 * f621_orderno
	 * @return
	 */
	public List<Map<String, Object>> F621_C2C(String localfile)
	{
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element F621DOC = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = F621DOC.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DOCHEAD")) 
				{
					//处理日期
					//System.out.println(child.elementText("DOCDATE"));
					//发送者
					//System.out.println(child.elementText("FROMPARTNERCODE"));
					//接收者
					//System.out.println(child.elementText("TOPARTNERCODE"));
				}

				if (child.getName().equals("F621CONTENT")) 
				{
					//EC厂商代码
					//System.out.println(child.elementText("ECNO"));
					//取貨門市編  F：全家、L：萊爾富、K：OK    ==>F001234
					//System.out.println(child.elementText("STNO"));
					//EC订单编号(物流单号)
					//System.out.println(child.elementText("ODNO"));
					//結帳狀態碼  3：異常結案
					//System.out.println(child.elementText("STATUS"));
					//结账状态原因，
					//D01 : 大物流遺失 					
					//D02 : 未送貨到小物流 
					//S03 : 小物流遺失 
					//N05 : 門市遺失 
					//System.out.println(child.elementText("RET_R"));
					// 交易方式識別碼，1：取貨付款 	3：取貨不付款 4：寄件不付款 5：寄件付款			 
					//System.out.println(child.elementText("TRADETYPE"));
					//結帳基準日，YYYYMMDD
					//System.out.println(child.elementText("TKDT"));
					//代收金額，
					//System.out.println(child.elementText("AMT"));
					//商品實際金
					//System.out.println(child.elementText("REALAMT"));
					//手續費，
					//System.out.println(child.elementText("FEE"));
					//
					//System.out.println(child.elementText("VENDOR"));
					//
					//System.out.println(child.elementText("VENDORNO"));
					
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("f621_expressno", child.elementText("ODNO"));
					map.put("f621_ecno", child.elementText("ECNO"));									
					map.put("f621_getshopno", child.elementText("STNO"));	
					map.put("f621_status", child.elementText("STATUS"));
					map.put("f621_reason", child.elementText("RET_R"));
					map.put("f621_tradetype", child.elementText("TRADETYPE"));
					map.put("f621_collectamt", child.elementText("AMT"));
					map.put("f621_realamt", child.elementText("REALAMT"));
					map.put("f621_fee", child.elementText("FEE"));
					map.put("f621_bdate", child.elementText("TKDT"));					
					map.put("f621_orderno", child.elementText("VENDORNO"));

					lstMaps.add(map);		


				}
			}


			file=null;
			document=null;
			reader=null;
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}
	
	
	/**
	 * 重出件415訂單出貨，因收到F67_C2C大物流驗退和F69_C2C取消出貨
	 * @param apiUrl
	 * @param ecNo 厂商代码
	 * @param collectNo 代收代号
	 * @param dcNo 大物流代码
	 * @param OrderList(
	 * ODNO:物流单号，有規則，取件订单编号11码，不是訂單中心的訂單號
	 * STNO:取货门店编号，代码(F：全家、L：萊爾富、K：OK)+门市编号
	 * AMT:代收金额， >=0 int
	 * CUTKNM:取货人姓名
	 * CUTKTL:手机末三码，無資料代空值 
	 * PRODNM:商品別代，0 : 一般商品  1 : 票券商品
	 * ECWEB: EC網站名，例：康迅數位 www.payeasy.com.tw
	 * ECSERTEL ：EC 網站客服電話，例：02-XXXXXXXX 
	 * REALAMT：商品實際金額，int 必須為 0 或正整數。 ※選擇取貨不付款與取貨付款時，需在此欄位填上貨物「實際金額」，以利後續查帳事宜
	 * TRADETYPE：交易方式識別碼(货到付款标记) ，1：取貨付款 3：取貨不付款 
	 * )
	 * @return
	 */
	public boolean F415_SALE_C2C(String apiUrl,String ecNo,String collectNo,String dcNo,List<Map<String, Object>> OrderList)
	{
		////F10CVS+EC厂商代码+YYYYMMDDhhmmss.xml 

		try 
		{
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMddHHmmss");
			String eDatetime=dfDate.format(cal.getTime());
			String filename="F10CVS" + ecNo + eDatetime +".xml";

			String filePath="D:\\";			

			File xmlFile = new File(filePath + filename);

			Document document = DocumentHelper.createDocument();
			Element ORDER_DOC = document.addElement("ORDER_DOC");

			for (Map<String, Object> oneData : OrderList)
			{
				Element ORDER = ORDER_DOC.addElement("ORDER");

				//
				String ODNO= oneData.get("ODNO").toString();
				//<![CDATA[達康購物網]]>
				//特殊＜  ＞  ＆ ‘…
				boolean isSpecial= isSpecialChar(ODNO);
				if (isSpecial) 
				{
					ODNO=String.format("<![CDATA[%s]]>", ODNO);
				}			

				//
				String STNO= oneData.get("STNO").toString();
				isSpecial= isSpecialChar(STNO);
				if (isSpecial) 
				{
					STNO=String.format("<![CDATA[%s]]>", STNO);
				}			

				String AMT=oneData.get("AMT").toString() ;

				//
				String CUTKNM= oneData.get("CUTKNM").toString();				
				isSpecial= isSpecialChar(CUTKNM);
				if (isSpecial) 
				{
					CUTKNM=String.format("<![CDATA[%s]]>", CUTKNM);
				}	
				//
				String CUTKTL= oneData.get("CUTKTL").toString();
				isSpecial= isSpecialChar(CUTKTL);
				if (isSpecial) 
				{
					CUTKTL=String.format("<![CDATA[%s]]>", CUTKTL);
				}	

				//
				String PRODNM= oneData.get("PRODNM").toString();

				String ECWEB= oneData.get("ECWEB").toString();
				isSpecial= isSpecialChar(ECWEB);
				if (isSpecial) 
				{
					ECWEB=String.format("<![CDATA[%s]]>", ECWEB);
				}		

				//
				String ECSERTEL= oneData.get("ECSERTEL").toString();
				isSpecial= isSpecialChar(ECSERTEL);
				if (isSpecial) 
				{
					ECSERTEL=String.format("<![CDATA[%s]]>", ECSERTEL);
				}		

				//
				String REALAMT= oneData.get("REALAMT").toString();

				//
				String TRADETYPE= oneData.get("TRADETYPE").toString();

				//厂商代码3码
				Element eECNO=ORDER.addElement("ECNO");
				eECNO.setText("415");//重出件
				//EC订单编号，物流单号11码
				Element eODNO=ORDER.addElement("ODNO");
				eODNO.setText(ODNO);
				//取货门店编码7码，F：全家、L：萊爾富、K：OK 
				Element eSTNO=ORDER.addElement("STNO");
				eSTNO.setText(STNO);
				//代收金额5码
				Element eAMT=ORDER.addElement("AMT");
				eAMT.setText(AMT);
				//取货人姓名10码
				Element eCUTKNM=ORDER.addElement("CUTKNM");
				eCUTKNM.setText(CUTKNM);
				//手机号末3码
				Element eCUTKTL=ORDER.addElement("CUTKTL");
				eCUTKTL.setText(CUTKTL);
				//商品别代码1码，0 : 一般商品  1 : 票券商品 
				Element ePRODNM=ORDER.addElement("PRODNM");
				ePRODNM.setText(PRODNM);
				// EC 網站名稱 20码，例：康迅數位 www.payeasy.com.tw 
				Element eECWEB=ORDER.addElement("ECWEB");
				eECWEB.setText(ECWEB);
				// EC 網站客服電話 20码，例：02-XXXXXXXX
				Element eECSERTEL=ORDER.addElement("ECSERTEL");
				eECSERTEL.setText(ECSERTEL);
				//商品實際金額5码
				Element eREALAMT=ORDER.addElement("REALAMT");
				eREALAMT.setText(REALAMT);
				// 交易方式識別碼1码，1：取貨付款 3：取貨不付款  
				Element eTRADETYPE=ORDER.addElement("TRADETYPE");
				eTRADETYPE.setText(TRADETYPE);
				//代收代號3码
				Element eSERCODE=ORDER.addElement("SERCODE");
				eSERCODE.setText(collectNo);
				//大物流代號3码
				Element eEDCNO=ORDER.addElement("EDCNO");
				eEDCNO.setText(dcNo);
			}

			String TOTALS=OrderList.size()+"";

			Element ORDERCOUNT = ORDER_DOC.addElement("ORDERCOUNT");
			Element eTOTALS=ORDERCOUNT.addElement("TOTALS");
			eTOTALS.setText(TOTALS);

			FileOutputStream fos=new FileOutputStream(xmlFile);			
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter xmlWriter =new XMLWriter(fos, format);
			xmlWriter.write(document);			
			fos.close();
			fos=null;
			xmlWriter.close();	
			xmlWriter=null;
			document=null;
			xmlFile=null;

		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());

			return false;
		}

		return true;
	}
	
	
	
	/**
	 * 重出件415訂單逾期未取貨退回物流中心，因收到F67_C2C大物流驗退
	 * @param apiUrl
	 * @param ecNo 厂商代码
	 * @param collectNo 代收代号
	 * @param dcNo 大物流代码
	 * @param OrderList(
	 * ODNO:物流单号，有規則，取件订单编号11码，不是訂單中心的訂單號
	 * STNO:取货门店编号，代码(F：全家、L：萊爾富、K：OK)+门市编号
	 * AMT:代收金额， >=0 int
	 * CUTKNM:取货人姓名
	 * CUTKTL:手机末三码，無資料代空值 
	 * PRODNM:商品別代，0 : 一般商品  1 : 票券商品
	 * ECWEB: EC網站名，例：康迅數位 www.payeasy.com.tw
	 * ECSERTEL ：EC 網站客服電話，例：02-XXXXXXXX 
	 * REALAMT：商品實際金額，int 必須為 0 或正整數。 ※選擇取貨不付款與取貨付款時，需在此欄位填上貨物「實際金額」，以利後續查帳事宜
	 * TRADETYPE：交易方式識別碼(货到付款标记) ，1：取貨付款 3：取貨不付款 
	 * )
	 * @return
	 */
	public boolean F415_Return_C2C(String apiUrl,String ecNo,String collectNo,String dcNo,List<Map<String, Object>> OrderList)
	{
		////F10CVS+EC厂商代码+YYYYMMDDhhmmss.xml 

		try 
		{
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMddHHmmss");
			String eDatetime=dfDate.format(cal.getTime());
			String filename="F10CVS" + ecNo + eDatetime +".xml";

			String filePath="D:\\";			

			File xmlFile = new File(filePath + filename);

			Document document = DocumentHelper.createDocument();
			Element ORDER_DOC = document.addElement("ORDER_DOC");

			for (Map<String, Object> oneData : OrderList)
			{
				Element ORDER = ORDER_DOC.addElement("ORDER");

				//
				String ODNO= oneData.get("ODNO").toString();
				//<![CDATA[達康購物網]]>
				//特殊＜  ＞  ＆ ‘…
				boolean isSpecial= isSpecialChar(ODNO);
				if (isSpecial) 
				{
					ODNO=String.format("<![CDATA[%s]]>", ODNO);
				}			

				//
				String STNO= oneData.get("STNO").toString();
				isSpecial= isSpecialChar(STNO);
				if (isSpecial) 
				{
					STNO=String.format("<![CDATA[%s]]>", STNO);
				}			

				String AMT=oneData.get("AMT").toString() ;

				//
				String CUTKNM= oneData.get("CUTKNM").toString();				
				isSpecial= isSpecialChar(CUTKNM);
				if (isSpecial) 
				{
					CUTKNM=String.format("<![CDATA[%s]]>", CUTKNM);
				}	
				//
				String CUTKTL= oneData.get("CUTKTL").toString();
				isSpecial= isSpecialChar(CUTKTL);
				if (isSpecial) 
				{
					CUTKTL=String.format("<![CDATA[%s]]>", CUTKTL);
				}	

				//
				String PRODNM= oneData.get("PRODNM").toString();

				String ECWEB= oneData.get("ECWEB").toString();
				isSpecial= isSpecialChar(ECWEB);
				if (isSpecial) 
				{
					ECWEB=String.format("<![CDATA[%s]]>", ECWEB);
				}		

				//
				String ECSERTEL= oneData.get("ECSERTEL").toString();
				isSpecial= isSpecialChar(ECSERTEL);
				if (isSpecial) 
				{
					ECSERTEL=String.format("<![CDATA[%s]]>", ECSERTEL);
				}		

				//
				String REALAMT= oneData.get("REALAMT").toString();

				//
				String TRADETYPE= oneData.get("TRADETYPE").toString();

				//厂商代码3码
				Element eECNO=ORDER.addElement("ECNO");
				eECNO.setText("415");//重出件
				//EC订单编号，物流单号11码
				Element eODNO=ORDER.addElement("ODNO");
				eODNO.setText(ODNO);
				//取货门店编码7码，F：全家、L：萊爾富、K：OK 
				Element eSTNO=ORDER.addElement("STNO");
				eSTNO.setText(STNO);
				//代收金额5码
				Element eAMT=ORDER.addElement("AMT");
				eAMT.setText(AMT);
				//取货人姓名10码
				Element eCUTKNM=ORDER.addElement("CUTKNM");
				eCUTKNM.setText(CUTKNM);
				//手机号末3码
				Element eCUTKTL=ORDER.addElement("CUTKTL");
				eCUTKTL.setText(CUTKTL);
				//商品别代码1码，0 : 一般商品  1 : 票券商品 
				Element ePRODNM=ORDER.addElement("PRODNM");
				ePRODNM.setText(PRODNM);
				// EC 網站名稱 20码，例：康迅數位 www.payeasy.com.tw 
				Element eECWEB=ORDER.addElement("ECWEB");
				eECWEB.setText(ECWEB);
				// EC 網站客服電話 20码，例：02-XXXXXXXX
				Element eECSERTEL=ORDER.addElement("ECSERTEL");
				eECSERTEL.setText(ECSERTEL);
				//商品實際金額5码
				Element eREALAMT=ORDER.addElement("REALAMT");
				eREALAMT.setText(REALAMT);
				// 交易方式識別碼1码，1：取貨付款 3：取貨不付款  
				Element eTRADETYPE=ORDER.addElement("TRADETYPE");
				eTRADETYPE.setText(TRADETYPE);
				//代收代號3码
				Element eSERCODE=ORDER.addElement("SERCODE");
				eSERCODE.setText(collectNo);
				//大物流代號3码
				Element eEDCNO=ORDER.addElement("EDCNO");
				eEDCNO.setText(dcNo);
			}

			String TOTALS=OrderList.size()+"";

			Element ORDERCOUNT = ORDER_DOC.addElement("ORDERCOUNT");
			Element eTOTALS=ORDERCOUNT.addElement("TOTALS");
			eTOTALS.setText(TOTALS);

			FileOutputStream fos=new FileOutputStream(xmlFile);			
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter xmlWriter =new XMLWriter(fos, format);
			xmlWriter.write(document);			
			fos.close();
			fos=null;
			xmlWriter.close();	
			xmlWriter=null;
			document=null;
			xmlFile=null;

		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());

			return false;
		}

		return true;
	}
	
	
	
	

	/**
	 * 特殊字符需要处理
	 * @param ch
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isSpecialChar(String ch) 
	{
		if (ch.contains("<") || ch.contains(">")|| ch.contains("&")|| ch.contains("'")|| ch.contains("...")) 
		{
			return true;
		}

		if (PosPub.isContainChinese(ch)) 
		{
			return true;
		}

		return false;
	}


}
