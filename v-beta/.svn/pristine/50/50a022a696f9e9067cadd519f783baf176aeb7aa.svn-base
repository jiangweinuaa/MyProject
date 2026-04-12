package com.dsc.spos.utils.logistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.utils.FtpUtils;
import com.dsc.spos.utils.PosPub;

//7-11大智通
//202.168.204.210 (測試機) 202.168.204.211 (正式機)
//帐号：829XXX 密码：xxxxxxxxxx
//验证：不允許匿名存取  僅限廠商 IP 連結 
//出貨流程：SIN(訂單上傳)--FILEOK/SRP(訂單回復檔)--ETA(出貨通知)--EIN(物流中心驗收)--PPS(到店檔)--OL(商品代收檔)
//退貨流程：ERT(預定退貨通知)--PPS(離店資料)--EDR(物流退貨驗收通知)--EVR(廠退資料)
public class SevenEleven 
{

	/**
	 * 出货订单通知资料，处理时间每日 03:00、05:00、10:15、11:35， 商家上传至FTP 的 SIN 目錄，廠商於上線申請時指定，一個廠商僅能指定一種類別
	 * 01 旅遊 ，02 3C，03 書籍音樂 ，04 百貨 ，05 美妝 ，06 服務精品，07 線上服務 
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public boolean SIN(String apiUrl,final String ecNo,final String sonEcNo,final String sDate,String ftpuid,String ftppwd, String resbody)
	{
		//檔名格式為:3码母厂商代码+3码子厂商代码+yyyymmdd+2码序号+.xml
		//資料請勿使用 <   >   & ‘ 等特殊字
		//ReceiverName (收貨人姓名)間不得含有逗號(,)，否則會造成驗收失敗。中文字請勿 超過 5 個字，英文字勿超過 10 個字，否則該批 SIN 檔不進行處理。 

		boolean bRet=false;

		try 
		{
			//序号
			String sno="01";

			// create new filename filter
			FilenameFilter filter = new FilenameFilter() 
			{
				@Override
				public boolean accept(File dir, String name) 
				{
					if(name.lastIndexOf('.')>0)
					{
						// 获取索引位置
						int lastIndex = name.lastIndexOf(".xml");
						// 
						String str = name.substring(0,lastIndex);

						//匹配 
						if(str.startsWith(ecNo+sonEcNo+sDate))
						{
							return true;
						}
					}
					return false;
				}
			};

			//
			String sSysPro=System.getProperty("catalina.home");
			if (sSysPro==null) 
			{
				sSysPro="D:\\apache\\apache-tomcat-8.0.35";
			}

			String localpath=sSysPro+"\\webapps\\LG\\dzt\\upload";	
			File file =new File(localpath); 
			//如果文件夹不存在则创建 
			if(!file.exists()&& !file.isDirectory())      
			{
				file.mkdirs();
			}	
			else 
			{
				File[] paths=file.listFiles(filter);

				List<Integer> listIn = new ArrayList<Integer>();
				for(File path:paths)
				{					
					//
					//System.out.println(path.getName());

					// 获取索引位置
					int lastIndex = path.getName().lastIndexOf(".xml");
					//取2码序号
					String str = path.getName().substring(lastIndex-2,lastIndex);

					listIn.add(Integer.parseInt(str));
				}

				if (listIn.size()>0) 
				{
					//降序排列
					Comparator<Integer> reverseComparator = Collections.reverseOrder();
					Collections.sort(listIn, reverseComparator);				

					int maxNN=listIn.get(0)+1;

					//
					if (maxNN>99) 
					{
						maxNN=99;						
					}

					String iSN=maxNN+"";
					//赋值
					sno=PosPub.FillStr(iSN, 2, "0", true);
				}
				else 
				{
					sno="01";
				}
			}
			file=null;


			String filename=ecNo +sonEcNo+ sDate +sno+".xml";

			String filePath=localpath;			

			File xmlFile = new File(filePath +"\\"+ filename);

			Document document = DocumentHelper.createDocument();
			Element OrderDoc = document.addElement("OrderDoc");
			//DocHead
			Element DocHead=OrderDoc.addElement("DocHead");
			//文件編號,同 SIN 檔名 
			Element DocNo=DocHead.addElement("DocNo");
			DocNo.setText(ecNo +sonEcNo+ sDate +sno);
			//文件日期,yyyy-mm-dd
			Element DocDate=DocHead.addElement("DocDate");
			//格式化
			String fSdate=sDate.substring(0,4)+"-" + sDate.substring(4,6)+"-"+sDate.substring(6,8);
			DocDate.setText(fSdate);
			//eShop 母廠商編號
			Element ParentId=DocHead.addElement("ParentId");
			ParentId.setText(ecNo);

			//文档内容
			Element DocContent=OrderDoc.addElement("DocContent");

			JSONObject jsonres = new JSONObject(resbody);

			JSONArray OrderList= jsonres.getJSONArray("Order");
			for (int  pa= 0; pa < OrderList.length(); pa++) 
			{
				Element Order=DocContent.addElement("Order");

				String sOPMode= OrderList.getJSONObject(pa).getString("OPMode");
				String sEshopOrderNo= OrderList.getJSONObject(pa).getString("EshopOrderNo");
				String sEshopOrderDate= OrderList.getJSONObject(pa).getString("EshopOrderDate");
				String sServiceType= OrderList.getJSONObject(pa).getString("ServiceType");				
				String sShopperName= OrderList.getJSONObject(pa).getString("ShopperName");
				//特殊字符处理掉
				sShopperName=sShopperName.replace("<", "");
				sShopperName=sShopperName.replace(">", "");
				sShopperName=sShopperName.replace("&", "");
				sShopperName=sShopperName.replace("'", "");			

				//String sShopperPhone= OrderList.getJSONObject(pa).getString("ShopperPhone");
				//String sShopperMobilPhone= OrderList.getJSONObject(pa).getString("ShopperMobilPhone");
				//String sShopperEmail= OrderList.getJSONObject(pa).getString("ShopperEmail");
				String sReceiverName = OrderList.getJSONObject(pa).getString("ReceiverName");	
				//特殊字符处理掉
				sReceiverName=sReceiverName.replace("<", "");
				sReceiverName=sReceiverName.replace(">", "");
				sReceiverName=sReceiverName.replace("&", "");
				sReceiverName=sReceiverName.replace("'", "");		
				sReceiverName=sReceiverName.replace(",", "X");	

				//String sReceiverPhone= OrderList.getJSONObject(pa).getString("ReceiverPhone");
				String sReceiverMobilPhone= OrderList.getJSONObject(pa).getString("ReceiverMobilPhone");
				String sReceiverEmail= OrderList.getJSONObject(pa).getString("ReceiverEmail");
				//String sReceiverIDNumber= OrderList.getJSONObject(pa).getString("ReceiverIDNumber");
				String sOrderAmount= OrderList.getJSONObject(pa).getString("OrderAmount");

				//子厂商编码
				Element EshopId=Order.addElement("EshopId");
				EshopId.setText(sonEcNo);
				//通路別 A 表示 7-Eleven 
				Element OPMode=Order.addElement("OPMode");
				OPMode.setText(sOPMode);
				//eShop 訂單編號 (shipmentno)30码
				Element EshopOrderNo=Order.addElement("EshopOrderNo");
				EshopOrderNo.setText(sEshopOrderNo);
				//eShop 訂單日期
				Element EshopOrderDate=Order.addElement("EshopOrderDate");
				EshopOrderDate.setText(sEshopOrderDate);
				//服務型態代碼,付款取貨：1 取貨不付款：3 
				Element ServiceType=Order.addElement("ServiceType");
				ServiceType.setText(sServiceType);
				//購買人姓名 
				Element ShopperName=Order.addElement("ShopperName");
				ShopperName.setText(sShopperName);
				//購買人電話
				Element ShopperPhone=Order.addElement("ShopperPhone");
				ShopperPhone.setText("");
				//購買人行動電話
				Element ShopperMobilPhone=Order.addElement("ShopperMobilPhone");
				ShopperMobilPhone.setText("");
				//購買人 E-mail
				Element ShopperEmail=Order.addElement("ShopperEmail");
				ShopperEmail.setText("");
				//收貨人姓名10码
				Element ReceiverName=Order.addElement("ReceiverName");
				ReceiverName.setText(sReceiverName);
				//收貨人電話
				Element ReceiverPhone=Order.addElement("ReceiverPhone");
				ReceiverPhone.setText("");
				//收貨人行動電話 
				Element ReceiverMobilPhone=Order.addElement("ReceiverMobilPhone");
				ReceiverMobilPhone.setText(sReceiverMobilPhone);
				//收貨人 E-mail 
				Element ReceiverEmail=Order.addElement("ReceiverEmail");
				ReceiverEmail.setText(sReceiverEmail);
				//收貨人身分證字號 
				Element ReceiverIDNumber=Order.addElement("ReceiverIDNumber");
				ReceiverIDNumber.setText("");
				//訂單總金額
				Element OrderAmount=Order.addElement("OrderAmount");
				OrderAmount.setText(sOrderAmount);


				JSONArray OrderDetailList= OrderList.getJSONObject(pa).getJSONArray("OrderDetail");
				for (int  pb= 0; pb < OrderDetailList.length(); pb++) 
				{
					/*
					String sProductId=OrderDetailList.getJSONObject(pb).getString("ProductId");
					String sProductName=OrderDetailList.getJSONObject(pb).getString("ProductName");
					String sQuantity=OrderDetailList.getJSONObject(pb).getString("Quantity");
					String sUnit=OrderDetailList.getJSONObject(pb).getString("Unit");
					String sUnitPrice=OrderDetailList.getJSONObject(pb).getString("UnitPrice");
					 */
					Element OrderDetail=Order.addElement("OrderDetail");
					Element ProductId=OrderDetail.addElement("ProductId");
					ProductId.setText("");
					Element ProductName=OrderDetail.addElement("ProductName");
					ProductName.setText("");
					Element Quantity=OrderDetail.addElement("Quantity");
					Quantity.setText("");
					Element Unit=OrderDetail.addElement("Unit");
					Unit.setText("");
					Element UnitPrice=OrderDetail.addElement("UnitPrice");
					UnitPrice.setText("");
				}


				JSONArray ShipmentDetailList=OrderList.getJSONObject(pa).getJSONArray("ShipmentDetail");
				for (int  pc= 0; pc < ShipmentDetailList.length(); pc++) 
				{

					String sShipmentNo=ShipmentDetailList.getJSONObject(pc).getString("ShipmentNo");
					//
					String sShipDate=ShipmentDetailList.getJSONObject(pc).getString("ShipDate");
					//
					String sReturnDate=ShipmentDetailList.getJSONObject(pc).getString("ReturnDate");
					//
					String sLastShipment=ShipmentDetailList.getJSONObject(pc).getString("LastShipment");
					//
					String sShipmentAmount=ShipmentDetailList.getJSONObject(pc).getString("ShipmentAmount");
					//
					String sStoreId=ShipmentDetailList.getJSONObject(pc).getString("StoreId");
					//
					String sEshopType=ShipmentDetailList.getJSONObject(pc).getString("EshopType");

					Element ShipmentDetail=Order.addElement("ShipmentDetail");
					//出貨單號  (配送編號) 
					Element ShipmentNo=ShipmentDetail.addElement("ShipmentNo");
					ShipmentNo.setText(sShipmentNo);
					//出貨日期 包裹需於 ShipDate 當日 14:00 前送達物流中心 
					Element ShipDate=ShipmentDetail.addElement("ShipDate");
					ShipDate.setText(sShipDate);
					//門市店退貨日期 (為 ShipDate+8 天) 
					Element ReturnDate=ShipmentDetail.addElement("ReturnDate");
					ReturnDate.setText(sReturnDate);
					//是否為本訂單的最 後一次出貨(Y/N) 
					Element LastShipment=ShipmentDetail.addElement("LastShipment");
					LastShipment.setText(sLastShipment);
					//出貨單金額 ；請務必帶入整數金 額 不得超過 20000 
					Element ShipmentAmount=ShipmentDetail.addElement("ShipmentAmount");
					ShipmentAmount.setText(sShipmentAmount);
					//門市店代碼 
					Element StoreId=ShipmentDetail.addElement("StoreId");
					StoreId.setText(sStoreId);
					//商品型態代碼,請固定帶入 04 
					//
					Element EshopType=ShipmentDetail.addElement("EshopType");
					EshopType.setText(sEshopType);
				}

			}

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

			//
			FtpUtils ftp=new FtpUtils();
			ftp.setHostname(apiUrl);
			//默认端口
			ftp.setPort(21);
			ftp.setUsername(ftpuid);
			ftp.setPassword(ftppwd);			

			//上传
			bRet=ftp.uploadFile("SIN", filename, filePath +"\\"+ filename);
			ftp=null;

		} 
		catch (Exception e) 
		{
			bRet=false;
		}

		return bRet;
	}



	/**
	 * 回覆檔案下載結果， 03:00、05:00、10:15、11:35,置於 FTP中的 SRP目錄 829XXXyyyymmddNN.FILEOK与SIN档名对应
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public String FILEOK(String apiUrl,String ecNo,String sonEcNo) 
	{
		try 
		{
			File file = new File("D:\\TEST\\ABCDEF2019043001.FILEOK");  
			//读
			FileInputStream  in = new FileInputStream(file); 
			InputStreamReader fr = new InputStreamReader(in,"utf-8");  
			BufferedReader br = new BufferedReader(fr); 

			String json="";
			String line;  
			while ((line=br.readLine()) != null)
			{  
				//UTF-8 BOM标记
				if(line.length()>0)
				{
					if((int)line.charAt(0)==65279)
					{
						json+=line.substring(1);
					}
					else
					{
						json+=line;
					}
				}

			}           

			if (json.equals("S2.01")) //檔案傳送成功 ，傳送時間內 XML 檔存在 FTP 中 
			{
				//System.out.println("檔案傳送成功");
			}
			else if (json.equals("E1.01")) //XML 格式不正確，XML 檔案格式不正確 
			{
				//System.out.println("XML 檔案格式不正確 ");
			}
			else if (json.equals("E1.10")) //檔案名稱已存在資料庫 ，XML 檔案名稱重複
			{
				//System.out.println("XML 檔案名稱重複");
			}
			else if (json.equals("E1.14")) //檔案名稱格式不符規定 ，XML 檔案名稱規格不符 
			{
				//System.out.println("XML 檔案名稱規格不符 ");
			}
			else if (json.equals("E2.01")) //檔案未傳送 ，傳送時間內無任何檔案存在 FTP 中
			{
				//System.out.println("傳送時間內無任何檔案存在 FTP 中");
			}
			else if (json.equals("E2.04")) //檔案內容損毀，檔案無法讀取 
			{
				//System.out.println("檔案無法讀取 ");
			}
			else if (json.equals("01120")) //格式不符規定，XML 檔案內容的格式不符合規定 
			{
				//System.out.println("XML 檔案內容的格式不符合規定 ");
			}
			else  //未知其他错误
			{
				//System.out.println("未知其他错误");
			}


			br.close();  
			fr.close();  
			in.close();      
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return "";
	}


	/**
	 * 訂單回應档，純文字檔。 於每週一至週六 08:00、12:35、13:35、置於 FTP中的 SRP目錄供 eShop 廠商主動下載
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public String SRP(String apiUrl,String ecNo,String sonEcNo) 
	{
		try 
		{
			File file = new File("D:\\TEST\\ABCDEF2019043001.SRP");  
			//读
			FileInputStream  in = new FileInputStream(file); 
			InputStreamReader fr = new InputStreamReader(in,"utf-8");  
			BufferedReader br = new BufferedReader(fr); 

			String json="";
			String line;  

			List<String> allList=new ArrayList();
			while ((line=br.readLine()) != null)
			{  
				//UTF-8 BOM标记
				if(line.length()>0)
				{
					if((int)line.charAt(0)==65279)
					{
						json+=line.substring(1);
					}
					else
					{
						allList.add(line);
					}
				}

			}           

			//第一行：总笔数
			//第二行：新增成功笔数
			//第三行：新增失败笔数			
			if (allList.size()>2) 
			{
				int totalCount=Integer.parseInt(allList.get(0));
				int successCount=Integer.parseInt(allList.get(1));
				int failCount=Integer.parseInt(allList.get(2));

				//System.out.println(totalCount);
				//System.out.println(successCount);
				//System.out.println(failCount);
			}

			//解析错误明细
			//3码子廠商代號+配送编号8码+出货日期8码+门市代码6码+金额5码+1码最后出货否+货运单号30码(右对齐补空格)+5码错误代码
			for (int pd = 3; pd < allList.size(); pd++) 
			{
				String tempStr=allList.get(pd);
				if (tempStr.length()==66) 
				{
					//子厂商代码
					String sSonECNO= tempStr.substring(0,3);
					//配送编码
					String sExpressno= tempStr.substring(3,11);
					//出货日期
					String sExDate= tempStr.substring(11,19);
					//取货门店
					String sShop= tempStr.substring(19,25);
					//金额
					String sAMT= tempStr.substring(25,30);
					//是否最后一次出货
					String sLastshipment= tempStr.substring(30,31);
					//订单编号30码
					String sShipmentno= tempStr.substring(31,61);
					//错误代码
					String sErrno= tempStr.substring(61,66);

					//System.out.println(sSonECNO);
					//System.out.println(sExpressno);
					//System.out.println(sExDate);
					//System.out.println(sShop);
					//System.out.println(sAMT);
					//System.out.println(sLastshipment);
					//System.out.println(sShipmentno);
					//System.out.println(sErrno);

					if (sErrno.equals("E1.01")) //XML 內容或標籤缺少
					{
						//System.out.println("XML 內容或標籤缺少");
					}
					else if (sErrno.equals("E1.02")) //出貨單號超出分配號碼範圍
					{
						//System.out.println("出貨單號超出分配號碼範圍 ");
					}
					else if (sErrno.equals("E1.04")) //XML 檔案內出貨單號重複 
					{
						//System.out.println("XML 檔案內出貨單號重複 ");
					}
					else if (sErrno.equals("E1.05")) //出貨單號已存在 
					{
						//System.out.println("出貨單號已存在 ");
					}
					else if (sErrno.equals("E1.07")) //出貨資料的訂單已存在資料庫 
					{
						//System.out.println("出貨資料的訂單已存在資料庫  ");
					}
					else if (sErrno.equals("E1.08")) //訂單門市與出貨單門市不符
					{
						//System.out.println("訂單門市與出貨單門市不符 ");
					}
					else if (sErrno.equals("E1.09")) //日期格式不對 (YYYY-MM-DD) 
					{
						//System.out.println("日期格式不對 (YYYY-MM-DD)  ");
					}
					else if (sErrno.equals("E1.12")) //非有效的日期 (如:2013-02-31) 
					{
						//System.out.println("非有效的日期 (如:2013-02-31)  ");
					}
					else if (sErrno.equals("E1.13")) //檔案中進貨日期已過 
					{
						//System.out.println("檔案中進貨日期已過 ");
					}
					else if (sErrno.equals("E1.15")) //檔案中內容格式不符規定 
					{
						//System.out.println("檔案中內容格式不符規定 ");
					}
					else if (sErrno.equals("01107")) //XML 檔案中的母廠商代號錯誤
					{
						//System.out.println("XML 檔案中的母廠商代號錯誤");
					}
					else if (sErrno.equals("01108")) //子廠商不存在 
					{
						//System.out.println("子廠商不存在 ");
					}
					else if (sErrno.equals("01110")) //門市已關轉 ,计入成功
					{
						//System.out.println("門市已關轉 ");
					}
					else if (sErrno.equals("01112")) //XML 檔案中的上傳了未申請開放的服務類型 
					{
						//System.out.println("XML 檔案中的上傳了未申請開放的服務類型 ");
					}
					else if (sErrno.equals("01118")) //包裹金額超過規定上限 
					{
						//System.out.println("包裹金額超過規定上限 ");
					}
					else if (sErrno.equals("01120")) //XML 檔案內容的格式不符合規定 
					{
						//System.out.println("XML 檔案內容的格式不符合規定 ");
					}
					else if (sErrno.equals("01121")) //XML 檔案中的取貨人姓名為空值 
					{
						//System.out.println("XML 檔案中的取貨人姓名為空值 ");
					}
					else 
					{
						//System.out.println("其他未知错误 ");
					}

				}
			}

			br.close();  
			fr.close();  
			in.close();      
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return "";
	}


	/**
	 * 出貨通知,當日預定出貨資料：每日 08:40、12:50、13:45 三個時段。829XXXyyyymmddNN.eta , 隔日預定出貨資料：每日 20:15。 FTP中的  ETA目錄。
	 * 注意，只有02001和02002才是出货成功
	 * eta_expressno物流单号
	 * eta_ecno母厂商
	 * eta_sonecno子厂商
	 * eta_replycode通知代码
	 * eta_replyname通知名称
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public List<Map<String, Object>> ETA(String localfile)
	{
		//829XXXyyyymmddNN.eta 
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element ETAToEshopDoc = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = ETAToEshopDoc.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DocHead")) 
				{
					//System.out.println(child.elementText("DocNo"));
					//System.out.println(child.elementText("DocDate"));
					//System.out.println(child.element("From").elementText("FromPartnerCode"));
					//System.out.println(child.element("To").elementText("ToPartnerCode"));
					//System.out.println(child.elementText("DocCount"));
				}

				if (child.getName().equals("DocContent")) 
				{
					List<Element> childEle = child.elements();
					for (Element Ele : childEle) 
					{
						//eShop 母廠商代號 
						//System.out.println(Ele.elementText("ParentId"));
						//eShop 子廠商編號
						//System.out.println(Ele.elementText("EshopId"));
						//出貨單編號8码 物流单号
						//System.out.println(Ele.elementText("ShipmentNo"));
						//服務型態代碼  付款取貨：1   取貨不付款：3 
						//System.out.println(Ele.elementText("ServiceType"));
						//商品型態代碼 
						//System.out.println(Ele.elementText("EshopType"));
						//消費者領貨期限 2009-01-12
						//System.out.println(Ele.elementText("PickUpDeadline"));
						//門市店代碼 
						//System.out.println(Ele.elementText("StoreId"));
						//門市店名稱 
						//System.out.println(Ele.elementText("StoreName"));
						//路線路順 D13031
						//System.out.println(Ele.elementText("Route"));
						//區域別
						//System.out.println(Ele.elementText("Area"));
						//出貨單金額
						//System.out.println(Ele.elementText("ShipmentAmount"));
						//回應代碼（請參考訊息表）==>注意，只有02001和02002才是出货成功
						//02001 結轉物流中心
						//02002 舊門市店號、已更新 
						//02101 無此門市 
						//02102 門市不配送 
						//02103 門市關轉店 
						//02104 尚未開店 
						//02105 曾重覆出貨，無法出貨 
						//System.out.println(Ele.elementText("ReplyCode"));
						//回應詳細內容（請參考訊息表） 
						//System.out.println(Ele.elementText("ReplyDetail"));
						//檢查號碼
						//System.out.println(Ele.elementText("CheckSum"));


						Map<String, Object> map=new HashMap<String, Object>();
						map.put("eta_expressno", Ele.elementText("ShipmentNo"));
						map.put("eta_ecno", Ele.elementText("ParentId"));
						map.put("eta_sonecno", Ele.elementText("EshopId"));
						if (Ele.elementText("ReplyCode").equals("02001")) 
						{
							map.put("eta_replycode", "02001");
							map.put("eta_replyname", "結轉物流中心");
						}
						else if (Ele.elementText("ReplyCode").equals("02002")) 
						{
							map.put("eta_replycode", "02002");
							map.put("eta_replyname", "舊門市店號、已更新 ");
						}
						else if (Ele.elementText("ReplyCode").equals("02101")) 
						{
							map.put("eta_replycode", "02101");
							map.put("eta_replyname", "無此門市 ");
						}
						else if (Ele.elementText("ReplyCode").equals("02102")) 
						{
							map.put("eta_replycode", "02102");
							map.put("eta_replyname", "門市不配送 ");
						}
						else if (Ele.elementText("ReplyCode").equals("02103")) 
						{
							map.put("eta_replycode", "02103");
							map.put("eta_replyname", "門市關轉店 ");
						}
						else if (Ele.elementText("ReplyCode").equals("02104")) 
						{
							map.put("eta_replycode", "02104");
							map.put("eta_replyname", "尚未開店 ");
						}
						else if (Ele.elementText("ReplyCode").equals("02105")) 
						{
							map.put("eta_replycode", "02105");
							map.put("eta_replyname", "曾重覆出貨，無法出貨 ");
						}


						lstMaps.add(map);

					}

				}

			}


		} 
		catch (Exception e) 
		{

		}		

		return lstMaps;
	}


	/**
	 * 物流中心進貨驗收，06:00 FTP中的EIN目錄，829XXXyyyymmddNN.ein
	 * ein_expressno物流单号
	 * ein_ecno母厂商
	 * ein_sonecno子厂商
	 * ein_sdate验收日期 yyyy-mm-dd
	 * ein_status验收状态 00成功
	 * ein_statusname验收状态名称
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> EIN(String localfile)
	{
		//829XXXyyyymmddNN.ein
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element DCReceiveDoc = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = DCReceiveDoc.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DocHead")) 
				{
					//System.out.println(child.elementText("DocNo"));
					//System.out.println(child.elementText("DocDate"));
					//System.out.println(child.element("From").elementText("FromPartnerCode"));
					//System.out.println(child.element("To").elementText("ToPartnerCode"));
					//System.out.println(child.elementText("DocCount"));
				}

				if (child.getName().equals("DocContent")) 
				{
					List<Element> childEle = child.elements();
					for (Element Ele : childEle) 
					{
						//eShop 母廠商代號 
						//System.out.println(Ele.elementText("ParentId"));
						//eShop 子廠商編號
						//System.out.println(Ele.elementText("EshopId"));
						//配送編號8码
						//System.out.println(Ele.elementText("ShipmentNo"));
						//DC 進貨驗收日期 yyyy-mm-dd
						//System.out.println(Ele.elementText("DCReceiveDate"));
						//DC 進貨驗收狀態代碼 （成功－00；失敗－請參考訊 息表） 
						//09 未到貨 ,未到貨包裹將由數網系統自動更新出貨日，共計更新五次。請廠商早 日將貨品送達至物流中心。超商物流中心星期日不收貨，雖然數網系 統會自動更新出貨日，但仍然不能送貨至物流中心。 
						//31 商品破損,
						//32 超材 ,
						//33 違禁品 ,
						//34 訂單資料重複 ,
						//35 已過門市進貨日 ,
						//36 門市關轉 ,eShop 需於 3 日內更新門市，更新方式請參考 SUP 段說明。 第 1 天~第 3 天內收到更新門市資料，則隔日回 00 進驗成功；若未收 到更新資料則不回(收到 EIN 門市關轉該天起算第一天)。 若於第 4 天後仍未收到更新門市資料，則進行廠退。 
						//37 條碼規格錯誤 ,
						//38 條碼無法判讀 ,
						//39 無標籤 ,
						//3A 商品捆包 ,
						//3B 商品外袋透明 ,
						//3C 多標籤,
						//60 物流中心理貨中 ,最多回 3 天，對應 PPS 的貨態為 016。 數網系統會協助更新出貨日，物流中心若找到包裹則隔日刷讀回 00 「進驗成功」，若尚未找到則續回 60「物流中心理貨中」。 如第 4 日還是找不到包裹則回 61「商品遺失」進入判賠。 
						//61 商品遺失 ,進入判賠 	
						//62 門市不配送 ,最多回 3 天。 數網系統會協助更新出貨日。若該門市可配送，則隔日刷讀 00「進驗 成功」；若該門市還是不可配送，則續回 62「門市不配送」。 
						//63 包裹異常不配送 ,最多回 7 天。 數網系統會協助更新出貨日。 
						//99 不正常到貨 ,有貨無 ETA 
						//System.out.println(Ele.elementText("DCReceiveStatus"));
						//DC 進貨驗收狀態名稱(請參考 訊息表) 
						//System.out.println(Ele.elementText("DCRecName"));
						//門市到店日期yyyymmdd 
						////System.out.println(Ele.elementText("DCStoreDate "));


						Map<String, Object> map=new HashMap<String, Object>();
						map.put("ein_expressno", Ele.elementText("ShipmentNo"));
						map.put("ein_ecno", Ele.elementText("ParentId"));
						map.put("ein_sonecno", Ele.elementText("EshopId"));
						map.put("ein_sdate", Ele.elementText("DCReceiveDate"));
						map.put("ein_status", Ele.elementText("DCReceiveStatus"));
						map.put("ein_statusname", Ele.elementText("DCRecName"));

						lstMaps.add(map);

					}

				}

			}


		} 
		catch (Exception e) 
		{

		}		

		return lstMaps;
	}


	/**
	 * 快遞物流退貨流程--商品到/離店，每日 06:00 起至 23:00 每隔一小時一次。 829XXXyyyymmddHH.PPS,FTP中的PPS目錄，
	 * 傳101與102為真正配達門市，請以此為寄發簡訊通知消費者領貨之依據。 
	 * pps_expressno物流单号
	 * pps_ecno母厂商
	 * pps_sonecno子厂商
	 * pps_sdate到店日期
	 * pps_stime到店时间
	 * pps_sgetshopno门店编码
	 * pps_status状态
	 * pps_statusname状态名称
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> PPS(String localfile)
	{
		//829XXXyyyymmddHH.PPS
		//第 15~16 碼為 24 小時時間制前兩碼HH
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File(localfile);
			Document document = reader.read(file);
			Element PPSDoc = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = PPSDoc.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DocHead")) 
				{
					//System.out.println(child.elementText("DocNo"));
					//System.out.println(child.elementText("DocDate"));
					//System.out.println(child.element("From").elementText("FromPartnerCode"));
					//System.out.println(child.element("To").elementText("ToPartnerCode"));
					//System.out.println(child.elementText("DocCount"));
				}

				if (child.getName().equals("DocContent")) 
				{
					List<Element> childEle = child.elements();
					for (Element Ele : childEle) 
					{
						//eShop 母廠商代號 
						//System.out.println(Ele.elementText("ParentId"));
						//eShop 子廠商編號
						//System.out.println(Ele.elementText("EshopId"));
						//配送編號8码
						//System.out.println(Ele.elementText("ShipmentNo"));
						//門市店號 
						//System.out.println(Ele.elementText("StoreID"));
						//到/離店日期 yyyymmdd
						//System.out.println(Ele.elementText("StoreDate"));
						//到/離店時間 hhmmss (24 小時制) 
						//System.out.println(Ele.elementText("StoreTime"));
						//到/離店註記, 請參考下方說明 僅 101 與 102 為真正配達門市 
						//101 门市配达
						//102 EC管制品配达
						//201 EC收退
						//202 交货便收件
						//203 退货便收件
						//204 异常收退
						//303 取件遗失
						//011 作业错误
						//012车辆故障
						//013天候不佳
						//014道路中断
						//015门市停业中
						//016缺件
						//017门市报缺
						//019取件货态异常
						//System.out.println(Ele.elementText("StoreType"));
						//手機號碼 ,保留欄位, 若無資料為空值
						//System.out.println(Ele.elementText("Tel"));


						Map<String, Object> map=new HashMap<String, Object>();
						map.put("pps_expressno", Ele.elementText("ShipmentNo"));
						map.put("pps_ecno", Ele.elementText("ParentId"));
						map.put("pps_sonecno", Ele.elementText("EshopId"));
						map.put("pps_sdate", Ele.elementText("StoreDate"));
						map.put("pps_stime", Ele.elementText("StoreTime"));
						map.put("pps_sgetshopno", Ele.elementText("StoreID"));
						map.put("pps_status", Ele.elementText("StoreType"));
						if (Ele.elementText("StoreType").equals("101")) 
						{
							map.put("pps_statusname", "门市配达");
						}
						else if (Ele.elementText("StoreType").equals("102")) 
						{
							map.put("pps_statusname", "EC管制品配达");
						}
						else if (Ele.elementText("StoreType").equals("201")) 
						{
							map.put("pps_statusname", "EC收退");
						}
						else if (Ele.elementText("StoreType").equals("202")) 
						{
							map.put("pps_statusname", "交货便收件");
						}
						else if (Ele.elementText("StoreType").equals("203")) 
						{
							map.put("pps_statusname", "退货便收件");
						}
						else if (Ele.elementText("StoreType").equals("204")) 
						{
							map.put("pps_statusname", "异常收退");
						}
						else if (Ele.elementText("StoreType").equals("303")) 
						{
							map.put("pps_statusname", "取件遗失");
						}
						else if (Ele.elementText("StoreType").equals("011")) 
						{
							map.put("pps_statusname", "作业错误");
						}
						else if (Ele.elementText("StoreType").equals("012")) 
						{
							map.put("pps_statusname", "车辆故障");
						}
						else if (Ele.elementText("StoreType").equals("013")) 
						{
							map.put("pps_statusname", "天候不佳");
						}
						else if (Ele.elementText("StoreType").equals("014")) 
						{
							map.put("pps_statusname", "道路中断");
						}
						else if (Ele.elementText("StoreType").equals("015")) 
						{
							map.put("pps_statusname", "门市停业中");
						}
						else if (Ele.elementText("StoreType").equals("016")) 
						{
							map.put("pps_statusname", "缺件");
						}
						else if (Ele.elementText("StoreType").equals("017")) 
						{
							map.put("pps_statusname", "门市报缺");
						}
						else if (Ele.elementText("StoreType").equals("019")) 
						{
							map.put("pps_statusname", "取件货态异常");
						}


						lstMaps.add(map);
					}


				}

			}


		} 
		catch (Exception e) 
		{

		}		

		return lstMaps;
	}


	/**
	 * 商品代收批次銷帳檔 ，10:00 AM  純文字檔案 (big5 格式) ，FTP中的OL目錄 若為空檔將不會產生檔案
	 * 檔案名稱：OL(2)+母廠商代號(3)+子廠商代號(3)+0(1).YYYYMMDD
	 * ol_expressno物流单号
	 * ol_servicetype付款取貨：1 取貨不付款：3 
	 * ol_amt代收金额
	 * ol_collectshopno代收门店
	 * ol_exchangeno交易号码
	 * ol_posmachine交易POS机号
	 * ol_sdate交易日期
	 * ol_stime交易时间
	 * ol_collectno代收代码
	 * ol_collectpartno代收机构
	 * ol_collectdate入账日期
	 * @param localfile
	 * @return
	 */
	public List<Map<String, Object>> OL(String localfile) 
	{
		//檔案名稱：OL(2)+母廠商代號(3)+子廠商代號(3)+0(1).YYYYMMDD， 例：OL8299180.20110417
		List<Map<String, Object>> lstMaps=new ArrayList<Map<String, Object>>();
		try 
		{
			File file = new File(localfile);  
			//读
			FileInputStream  in = new FileInputStream(file); 
			InputStreamReader fr = new InputStreamReader(in,"utf-8");  
			BufferedReader br = new BufferedReader(fr); 

			String json="";
			String line;  

			List<String> allList=new ArrayList();
			while ((line=br.readLine()) != null)
			{  
				//UTF-8 BOM标记
				if(line.length()>0)
				{
					if((int)line.charAt(0)==65279)
					{
						json+=line.substring(1);
					}
					else
					{
						allList.add(line);
					}
				}

			}           

			//代收代號 
			String collectno="";
			//代收機構代號 
			String collectPartno="";
			//入/扣帳日期
			String collectdate="";
			//第一行：
			if (allList.size()>2) 
			{
				String line1=allList.get(0);
				if (line1.length()==102) 
				{
					//代收代號 
					collectno= line1.substring(1,4);
					//System.out.println(collectno);
					//代收機構代號 
					collectPartno= line1.substring(4,11);
					//System.out.println(collectPartno);
					//入/扣帳日期
					collectdate= line1.substring(11,19);
					//System.out.println(collectdate);
					//保留欄位
					//String stayno= line1.substring(19,101);			
					////System.out.println(stayno);


				}
			}

			//解析错误明细
			//3码子廠商代號+配送编号8码+出货日期8码+门市代码6码+金额5码+1码最后出货否+货运单号30码(右对齐补空格)+5码错误代码
			for (int pd = 1; pd < allList.size()-1; pd++) 
			{
				String tempStr=allList.get(pd);

				if (tempStr.length()==102) 
				{
					//代收門市店號  
					String collectshopno= tempStr.substring(1,7);
					//System.out.println(collectshopno);
					//POS機號
					String posno= tempStr.substring(7,9);
					//System.out.println(posno);
					//顧客繳費日 yyyymmdd 
					String dateno= tempStr.substring(9,17);
					//System.out.println(dateno);
					//交易號碼
					String Exchangeno= tempStr.substring(17,23);			
					//System.out.println(Exchangeno);

					//交易時間 hhmm 
					String ExchangeTime= tempStr.substring(23,27);			
					//System.out.println(ExchangeTime);

					//BarCode1， 民國年月日的取貨截止日期(6 碼) + 母廠商代碼(3 碼) 
					String barcode1= tempStr.substring(27,36);			
					//System.out.println(barcode1);

					//BarCode2 32码，配送編號(8 碼) + 服務類型(1 碼) + 配送金額(5 碼) + 檢查碼(2 碼)  靠左對齊，右邊補空白 
					String barcode2= tempStr.substring(36,68);			
					//System.out.println(barcode2);

					//配送编码
					String expressno=tempStr.substring(36,44);

					//服务类型 付款取貨：1 取貨不付款：3 
					String servicetype=tempStr.substring(44,45);

					//金额
					String amt=tempStr.substring(45,50);


					//BarCode3 補空白 
					String barcode3= tempStr.substring(68,100);			
					//System.out.println(barcode3);

					//旗標 
					String kno= tempStr.substring(100,101);			
					//System.out.println(kno);


					Map<String, Object> map=new HashMap<String, Object>();
					map.put("ol_expressno", expressno);
					map.put("ol_servicetype", servicetype);
					map.put("ol_amt", amt);
					map.put("ol_collectshopno",collectshopno);
					map.put("ol_exchangeno", Exchangeno);
					map.put("ol_posmachine", posno);
					map.put("ol_sdate", dateno);
					map.put("ol_stime", ExchangeTime);
					map.put("ol_collectno", collectno);
					map.put("ol_collectpartno", collectPartno);
					map.put("ol_collectdate", collectdate);
					lstMaps.add(map);					

				}
			}

			//最后1行
			if (allList.size()>2) 
			{
				String lineLast=allList.get(allList.size()-1);
				if (lineLast.length()==102 || lineLast.length()==26) 
				{
					//代收總金額 右靠左補 0 
					String collectAMT= lineLast.substring(1,15);
					//System.out.println(collectAMT);
					//代收總筆數 右靠左補 0 
					String collectCount= lineLast.substring(15,25);
					//System.out.println(collectCount);
					//保留欄位 空白
					//String stayno= lineLast.substring(25,101);
					////System.out.println(stayno);
				}
			}

			br.close();  
			fr.close();  
			in.close();      
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return lstMaps;
	}


	/**
	 * 快遞物流退貨流程--物流中心預定退貨資，每日 10:30 以後。 FTP中的ERT目錄 ，若為空檔將不會產生檔案
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public String ERT(String apiUrl,String ecNo,String sonEcNo)
	{
		//829XXXyyyymmddHH.PPS
		//第 15~16 碼為 24 小時時間制前兩碼HH
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File("D:\\ABCDEF2019043001.ERT");
			Document document = reader.read(file);
			Element DCReturnAdviceDoc = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = DCReturnAdviceDoc.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DocHead")) 
				{
					//System.out.println(child.elementText("DocNo"));
					//System.out.println(child.elementText("DocDate"));
					//System.out.println(child.element("From").elementText("FromPartnerCode"));
					//System.out.println(child.element("To").elementText("ToPartnerCode"));
					//System.out.println(child.elementText("DocCount"));
				}

				if (child.getName().equals("DocContent")) 
				{

					List<Element> childEle = child.elements();
					for (Element Ele : childEle) 
					{
						//eShop 母廠商代號 
						//System.out.println(Ele.elementText("ParentId"));
						//eShop 子廠商編號
						//System.out.println(Ele.elementText("EshopId"));
						//配送編號8码
						//System.out.println(Ele.elementText("ShipmentNo"));
						//出貨單金額 EX.00100 
						//System.out.println(Ele.elementText("ShipmentAmount"));
						//預計退貨類型 （01 一退） 
						//System.out.println(Ele.elementText("ReturnType"));
						//DC 預定退貨日期 yyyy-mm-dd
						//System.out.println(Ele.elementText("DCPlannedReturnDate"));	

					}						

				}

			}


		} 
		catch (Exception e) 
		{

		}		

		return "";
	}

	/**
	 * 快遞物流退貨流程--物流中心退貨驗收,隔日 05:00 以後, FTP中的EDR目錄
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public String EDR(String apiUrl,String ecNo,String sonEcNo)
	{
		//829XXXyyyymmddNN.EDR
		//第 15~16 碼為 24 小時時間制前兩碼HH
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File("D:\\ABCDEF2019043001.EDR");
			Document document = reader.read(file);
			Element DCReturnDoc = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = DCReturnDoc.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DocHead")) 
				{
					//System.out.println(child.elementText("DocNo"));
					//System.out.println(child.elementText("DocDate"));
					//System.out.println(child.element("From").elementText("FromPartnerCode"));
					//System.out.println(child.element("To").elementText("ToPartnerCode"));
					//System.out.println(child.elementText("DocCount"));
				}

				if (child.getName().equals("DocContent")) 
				{

					List<Element> childEle = child.elements();
					for (Element Ele : childEle) 
					{
						//eShop 母廠商代號 
						//System.out.println(Ele.elementText("ParentId"));
						//eShop 子廠商編號
						//System.out.println(Ele.elementText("EshopId"));
						//配送編號8码 
						//System.out.println(Ele.elementText("ShipmentNo"));
						//出貨單金額 EX.00100 
						//System.out.println(Ele.elementText("ShipmentAmount"));
						//DC 退貨驗收日期 yyyy-mm-dd
						//System.out.println(Ele.elementText("DCReturnDate"));
						//DC 退貨驗收代碼 (請參考 訊息表) 
						//01 正常一退
						//11 商品瑕疵 
						//12 門市關店 
						//13 門市轉店 
						//14 廠商要求 
						//15 違禁品 
						//21 刷 A 給 B 
						//22 消費者要求 
						//System.out.println(Ele.elementText("DCReturnCode"));	
						//DC 退貨驗收名稱(請參考 訊息表) 
						//System.out.println(Ele.elementText("DCReturnName"));	

					}						

				}

			}


		} 
		catch (Exception e) 
		{

		}		

		return "";
	}


	/**
	 * 快遞物流退貨流程--廠退資料,隔日 09:00 以後。 FTP中的EVR目錄
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public String EVR(String apiUrl,String ecNo,String sonEcNo)
	{
		//829XXXyyyymmddNN.EVR
		//第 15~16 碼為 24 小時時間制前兩碼HH
		try 
		{
			SAXReader reader = new SAXReader();
			File file = new File("D:\\ABCDEF2019043001.EVR");
			Document document = reader.read(file);
			Element EshopReturnDoc = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = EshopReturnDoc.elements();
			for (Element child : childElements) 
			{
				//System.out.println(child.getName());

				if (child.getName().equals("DocHead")) 
				{
					//System.out.println(child.elementText("DocNo"));
					//System.out.println(child.elementText("DocDate"));
					//System.out.println(child.element("From").elementText("FromPartnerCode"));
					//System.out.println(child.element("To").elementText("ToPartnerCode"));
					//System.out.println(child.elementText("DocCount"));
				}

				if (child.getName().equals("DocContent")) 
				{

					List<Element> childEle = child.elements();
					for (Element Ele : childEle) 
					{
						//eShop 母廠商代號 
						//System.out.println(Ele.elementText("ParentId"));
						//eShop 子廠商編號
						//System.out.println(Ele.elementText("EshopId"));
						//配送編號8码
						//System.out.println(Ele.elementText("ShipmentNo"));
						//出貨單金額 EX.00100 
						//System.out.println(Ele.elementText("ShipmentAmount"));
						//廠退日期  yyyy-mm-dd
						//System.out.println(Ele.elementText("DCRetDate"));
						//廠退狀態代碼 (請參考訊 息表) 
						//01 正常一退 
						//11 商品瑕疵 
						//12 門市關店 
						//13 門市轉店 
						//14 廠商要求 
						//15 違禁品 
						//16 天候路況不佳, 因應外島走海運模式 
						//21 刷 A 給 B 
						//22 消費者要求 
						//31 商品破損 
						//32 超才 
						//33 違禁品
						//34 訂單資料重複 
						//35 已過門市進貨日 
						//36 更換門市 
						//37 條碼規格錯誤 
						//38 條碼無法判讀 
						//39 條碼資料錯誤 
						//3A 商品捆包 
						//3B 商品外袋透明 
						//3C 多標籤 
						//99 不正常到貨 ,有貨無 ETA 
						//System.out.println(Ele.elementText("DCRetCode"));	
						//廠退狀態名稱 (請參考訊 息表)
						//System.out.println(Ele.elementText("DCRetName"));	

					}						

				}

			}


		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}		

		return "";
	}



	/**
	 * 帳務檔資料,每月 10 號與 25 號前產生上一週期帳務檔 ,FTP中的ACC目錄
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public String ACC(String apiUrl,String ecNo,String sonEcNo) 
	{
		try 
		{
			//成功的訂單資料檔名為 829XXX_yyyymmUS01.acc (上半月) 或 829XXX_yyyymmDS01.acc (下半月)
			//退回的訂單資料檔名為 829XXX_yyyymmUF02.acc (上半月) 或 829XXX_yyyymmDF02.acc (下半月)。 
			File file = new File("D:\\TEST\\ABCDEF201904US01.ACC");  
			//读
			FileInputStream  in = new FileInputStream(file); 
			InputStreamReader fr = new InputStreamReader(in,"utf-8");  
			BufferedReader br = new BufferedReader(fr); 

			String json="";
			String line;  

			List<String> allList=new ArrayList();
			while ((line=br.readLine()) != null)
			{  
				//UTF-8 BOM标记
				if(line.length()>0)
				{
					if((int)line.charAt(0)==65279)
					{
						json+=line.substring(1);
					}
					else
					{
						allList.add(line);
					}
				}

			}		

			//解析错误明细			
			for (int pd = 0; pd < allList.size(); pd++) 
			{
				String tempStr=allList.get(pd);
				if (tempStr.length()==56) 
				{
					//eShop 母廠商編號 
					String sECNO= tempStr.substring(0,3);
					//服務型態代碼
					String sServicetype= tempStr.substring(3,4);
					//配送編號8码 
					String sShipmentno= tempStr.substring(4,12);
					//分隔符號，固定值 + 
					String sFix1= tempStr.substring(12,13);
					//代收金額 
					String sAMT= tempStr.substring(13,22);
					//門市店代碼 
					String sShopno= tempStr.substring(22,28);
					//代收資料回傳日期 
					String sCollectDate= tempStr.substring(28,36);
					//交易狀態（1-成功 2-退貨） 
					String sExchangeStatus= tempStr.substring(36,37);
					//分隔符號，固定值 + 
					String sFix2= tempStr.substring(37,38);
					//代收佣金（整數四位+小數四位） 
					String sCollectFee= tempStr.substring(38,47);
					//保管費（整數四位+小數四位） 
					String sStayFee= tempStr.substring(47,56);

					//System.out.println(sECNO);
					//System.out.println(sServicetype);
					//System.out.println(sShipmentno);
					//System.out.println(sFix1);
					//System.out.println(sAMT);
					//System.out.println(sShopno);
					//System.out.println(sCollectDate);
					//System.out.println(sExchangeStatus);
					//System.out.println(sFix2);
					//System.out.println(sCollectFee);
					//System.out.println(sStayFee);



				}
			}

			br.close();  
			fr.close();  
			in.close();      
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return "";
	}


	/**
	 * 遺失賠償明細文字檔,每月 5、12、20 及 28 日針對上期若有遺失判賠時產生 ,FTP中的ACTR目錄
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public String ACTR(String apiUrl,String ecNo,String sonEcNo) 
	{
		try 
		{
			//檔名為 829XXXyyyymm05.actr、829XXXyyyymm12.actr 、 829XXXyyyymm20.actr、829XXXyyyymm28.actr 
			File file = new File("D:\\TEST\\ABCDEF20190405.ACTR");  
			//读
			FileInputStream  in = new FileInputStream(file); 
			InputStreamReader fr = new InputStreamReader(in,"utf-8");  
			BufferedReader br = new BufferedReader(fr); 

			String json="";
			String line;  

			List<String> allList=new ArrayList();
			while ((line=br.readLine()) != null)
			{  
				//UTF-8 BOM标记
				if(line.length()>0)
				{
					if((int)line.charAt(0)==65279)
					{
						json+=line.substring(1);
					}
					else
					{
						allList.add(line);
					}
				}

			}		

			//解析错误明细

			for (int pd = 0; pd < allList.size(); pd++) 
			{
				String tempStr=allList.get(pd);
				if (tempStr.length()==153 || tempStr.length()==184) 
				{
					//eShop 母廠商編號 
					String sECNO= tempStr.substring(0,3);
					//门店编码
					String sShopno= tempStr.substring(3,9);
					//配送编号码，右補空 白
					String sExpressno= tempStr.substring(9,17);
					//订单编号32码(出货单号)，右補空 白
					String sShipmentno= tempStr.substring(17,49);
					//扣账年月日
					String sAdate= tempStr.substring(49,57);
					//调整日期
					String sBdate= tempStr.substring(57,65);
					//进货验收日
					String sCDate= tempStr.substring(65,73);
					//门店进货日
					String sDdate= tempStr.substring(73,81);
					//代收金额，左補０ 
					String sCollectAMT= tempStr.substring(81,89);
					//应税应赔金额，左補０ 
					String sTotalAMT= tempStr.substring(89,99);
					//应赔税额，左補０ 
					String sTaxFee= tempStr.substring(99,109);
					//未稅應賠金額 ，左補０ 
					String sNoTaxAMT= tempStr.substring(109,119);
					//調整數量 ,“-1”或” 1” 
					String sQty= tempStr.substring(119,121);
					//調整代號 ,A：門市、 B：物流中 心 、 C：廠商 
					String sCode= tempStr.substring(121,122);
					//調整註記 ,0：遺失賠償、1：調整 資料、2：異常退貨(代 收又驗退) 
					String sFlag= tempStr.substring(122,123);
					//調整原因說 明 右 補空白
					String sReason= tempStr.substring(123,153);					
					//保留欄位 
					//String sStayno= tempStr.substring(153,184);

					//System.out.println(sECNO);
					//System.out.println(sShopno);
					//System.out.println(sExpressno);
					//System.out.println(sShipmentno);
					//System.out.println(sAdate);
					//System.out.println(sBdate);
					//System.out.println(sCDate);
					//System.out.println(sDdate);
					//System.out.println(sCollectAMT);
					//System.out.println(sTotalAMT);
					//System.out.println(sTaxFee);
					//System.out.println(sNoTaxAMT);
					//System.out.println(sQty);
					//System.out.println(sCode);
					//System.out.println(sFlag);
					//System.out.println(sReason);
				}
			}

			br.close();  
			fr.close();  
			in.close();      
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return "";
	}


	/**
	 * 門市代碼與出貨日更新 ,純文字檔。 每日 05:00、10:15 及 11:35 進行資料處理，超過此時間即算隔日資料
	 * 廠商需將.SUP 檔與同檔名的空檔 829XXXyyyymmddNN.SUPOK 檔自行上傳至 FTP 的 SUP 目錄名稱資料夾中。
	 * 此檔案為包裹進驗時遇到 EIN 回傳訊息代碼 36 門市關轉時，上傳此檔案修改欲更 改送達的門市店號
	 * 超過每日最後一班排程 11:35，就算隔日訂單。SUP 檔案中的驗刷日期需為隔日
	 */
	public String SUP(String apiUrl,String ecNo,String sonEcNo,String resbody) 
	{
		try 
		{
			//檔名為 829XXXyyyymmddNN.SUP  
			String sfile = "D:\\TEST\\ABCDEF2019040501.SUP";
			FileOutputStream writerStream = new FileOutputStream(sfile);
			OutputStreamWriter osw=new OutputStreamWriter(writerStream, "UTF-8");
			BufferedWriter writer = new BufferedWriter(osw);			

			JSONObject jsonres = new JSONObject(resbody);
			JSONArray OrderList= jsonres.getJSONArray("suporder");
			for (int  pa= 0; pa < OrderList.length(); pa++) 
			{
				String sjson = "";
				sjson+=sonEcNo;
				String expressno= OrderList.getJSONObject(pa).getString("expressno");
				expressno=PosPub.FillStr(expressno,8,"0",true);
				sjson+=expressno;
				String adate= OrderList.getJSONObject(pa).getString("adate");
				sjson+=adate;
				String amt= OrderList.getJSONObject(pa).getString("amt");
				amt=PosPub.FillStr(amt,5,"0",true);
				sjson+=amt;
				String shipmentno= OrderList.getJSONObject(pa).getString("shipmentno");
				shipmentno=PosPub.FillStr(shipmentno,30," ",true);
				sjson+=shipmentno;
				String lastshipment= OrderList.getJSONObject(pa).getString("lastshipment");
				sjson+=lastshipment;
				//付款取貨：1 取貨不付款：3 
				String servicetype= OrderList.getJSONObject(pa).getString("servicetype");
				sjson+=servicetype;
				String updateshopno= OrderList.getJSONObject(pa).getString("updateshopno");
				sjson+=updateshopno;
				String goodtype= OrderList.getJSONObject(pa).getString("goodtype");
				sjson+=goodtype;

				writer.write(sjson);	
				writer.newLine();
			}			

			writer.close();
			osw.close();
			writerStream.close();


			//空档829XXXyyyymmddNN.SUPOK
			String supok = "D:\\TEST\\ABCDEF2019040501.SUPOK";			
			writerStream = new FileOutputStream(supok);
			osw=new OutputStreamWriter(writerStream, "UTF-8");
			writer = new BufferedWriter(osw);			
			writer.close();
			osw.close();
			writerStream.close();


			File file = new File("D:\\TEST\\ABCDEF2019040501.SUP");  
			//读
			FileInputStream  in = new FileInputStream(file); 
			InputStreamReader fr = new InputStreamReader(in,"utf-8");  
			BufferedReader br = new BufferedReader(fr); 

			String json="";
			String line;  

			List<String> allList=new ArrayList();
			while ((line=br.readLine()) != null)
			{  
				//UTF-8 BOM标记
				if(line.length()>0)
				{
					if((int)line.charAt(0)==65279)
					{
						json+=line.substring(1);
					}
					else
					{
						allList.add(line);
					}
				}

			}		

			//解析错误明细

			for (int pd = 0; pd < allList.size(); pd++) 
			{
				String tempStr=allList.get(pd);
				if (tempStr.length()==64) 
				{
					//eShop 母廠商編號 
					String sECNO= tempStr.substring(0,3);
					//為出貨標籤的， 配送編號8码 (需與訂單編號同時存在於資料庫) 
					String sExpressno= tempStr.substring(3,11);
					//驗刷日期 YYYYMMDD (請物流中心再次驗刷日期) 
					String sAdate= tempStr.substring(11,19);
					//包裹金額 五碼，靠右對齊不足補 0 (需與為原先上傳 SIN 中該筆出貨單號的ShipmentAmount 相同) 
					String sAMT= tempStr.substring(19,24);
					//廠商訂單編號30码， 向右對齊不足補空格 (訂單編號必需為原先上傳SIN 中該筆出貨單號的訂單編號) 
					String sShipmentno= tempStr.substring(24,54);
					//是否為最後一次出貨(Y:是 N:否) 
					String sLastshipment= tempStr.substring(54,55);
					//服務類型 (需與為原先上傳 SIN 中該筆出貨單號的服務類型相同)
					String sServiceType= tempStr.substring(55,56);
					//更新後的門市店號 
					String sShopno= tempStr.substring(56,62);
					//商品型態代碼 (需與為原先上傳 SIN 中該筆出貨單號的商品型態代碼相同) 
					String sGoodType= tempStr.substring(62,64);					

					//System.out.println(sECNO);					
					//System.out.println(sExpressno);					
					//System.out.println(sAdate);
					//System.out.println(sAMT);
					//System.out.println(sShipmentno);					
					//System.out.println(sLastshipment);
					//System.out.println(sServiceType);
					//System.out.println(sShopno);
					//System.out.println(sGoodType);

				}
			}

			br.close();  
			fr.close();  
			in.close();      
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return "";
	}


	/**
	 * 門市代碼與出貨日更新處理回覆 ，每週一至週六 12:35、13:35 置於 FTP 中的 SURP 資料夾供 eShop 廠商主動下載
	 * 純文字檔。 檔名格式為 829XXXyyyymmddNN.SURP  (對應廠商上傳之 SUP 檔名)。 
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public String SURP(String apiUrl,String ecNo,String sonEcNo) 
	{
		try 
		{
			File file = new File("D:\\TEST\\ABCDEF2019040501.SURP");  
			//读
			FileInputStream  in = new FileInputStream(file); 
			InputStreamReader fr = new InputStreamReader(in,"utf-8");  
			BufferedReader br = new BufferedReader(fr); 

			String json="";
			String line;  

			List<String> allList=new ArrayList();
			while ((line=br.readLine()) != null)
			{  
				//UTF-8 BOM标记
				if(line.length()>0)
				{
					if((int)line.charAt(0)==65279)
					{
						json+=line.substring(1);
					}
					else
					{
						allList.add(line);
					}
				}

			}           

			//第一行：总笔数
			//第二行：新增成功笔数
			//第三行：新增失败笔数			
			if (allList.size()>2) 
			{
				int totalCount=Integer.parseInt(allList.get(0));
				int successCount=Integer.parseInt(allList.get(1));
				int failCount=Integer.parseInt(allList.get(2));

				//System.out.println(totalCount);
				//System.out.println(successCount);
				//System.out.println(failCount);
			}

			//解析错误明细
			//3码子廠商代號+配送编号8码+出货日期8码+门市代码6码+金额5码+1码最后出货否+货运单号30码(右对齐补空格)+5码错误代码
			for (int pd = 3; pd < allList.size(); pd++) 
			{
				String tempStr=allList.get(pd);
				if (tempStr.length()==66) 
				{
					String sSonECNO= tempStr.substring(0,3);
					String sExpressno= tempStr.substring(3,11);
					String sExDate= tempStr.substring(11,19);
					String sShop= tempStr.substring(19,25);
					String sAMT= tempStr.substring(25,30);
					String sLastshipment= tempStr.substring(30,31);
					String sShipmentno= tempStr.substring(31,61);
					String sErrno= tempStr.substring(61,66);

					//System.out.println(sSonECNO);
					//System.out.println(sExpressno);
					//System.out.println(sExDate);
					//System.out.println(sShop);
					//System.out.println(sAMT);
					//System.out.println(sLastshipment);
					//System.out.println(sShipmentno);
					//System.out.println(sErrno);

					if (sErrno.equals("E1.01")) //XML 內容或標籤缺少
					{
						//System.out.println("XML 內容或標籤缺少");
					}
					else if (sErrno.equals("E1.02")) //出貨單號超出分配號碼範圍
					{
						//System.out.println("出貨單號超出分配號碼範圍 ");
					}
					else if (sErrno.equals("E1.04")) //XML 檔案內出貨單號重複 
					{
						//System.out.println("XML 檔案內出貨單號重複 ");
					}
					else if (sErrno.equals("E1.05")) //出貨單號已存在 
					{
						//System.out.println("出貨單號已存在 ");
					}
					else if (sErrno.equals("E1.07")) //出貨資料的訂單已存在資料庫 
					{
						//System.out.println("出貨資料的訂單已存在資料庫  ");
					}
					else if (sErrno.equals("E1.08")) //訂單門市與出貨單門市不符
					{
						//System.out.println("訂單門市與出貨單門市不符 ");
					}
					else if (sErrno.equals("E1.09")) //日期格式不對 (YYYY-MM-DD) 
					{
						//System.out.println("日期格式不對 (YYYY-MM-DD)  ");
					}
					else if (sErrno.equals("E1.12")) //非有效的日期 (如:2013-02-31) 
					{
						//System.out.println("非有效的日期 (如:2013-02-31)  ");
					}
					else if (sErrno.equals("E1.13")) //檔案中進貨日期已過 
					{
						//System.out.println("檔案中進貨日期已過 ");
					}
					else if (sErrno.equals("E1.15")) //檔案中內容格式不符規定 
					{
						//System.out.println("檔案中內容格式不符規定 ");
					}
					else if (sErrno.equals("01107")) //XML 檔案中的母廠商代號錯誤
					{
						//System.out.println("XML 檔案中的母廠商代號錯誤");
					}
					else if (sErrno.equals("01108")) //子廠商不存在 
					{
						//System.out.println("子廠商不存在 ");
					}
					else if (sErrno.equals("01110")) //門市已關轉 ,计入成功
					{
						//System.out.println("門市已關轉 ");
					}
					else if (sErrno.equals("01112")) //XML 檔案中的上傳了未申請開放的服務類型 
					{
						//System.out.println("XML 檔案中的上傳了未申請開放的服務類型 ");
					}
					else if (sErrno.equals("01118")) //包裹金額超過規定上限 
					{
						//System.out.println("包裹金額超過規定上限 ");
					}
					else if (sErrno.equals("01120")) //XML 檔案內容的格式不符合規定 
					{
						//System.out.println("XML 檔案內容的格式不符合規定 ");
					}
					else if (sErrno.equals("01121")) //XML 檔案中的取貨人姓名為空值 
					{
						//System.out.println("XML 檔案中的取貨人姓名為空值 ");
					}
					else if (sErrno.equals("E2.06")) //出貨單狀態錯誤 ，該筆出貨單號的物流驗收訊息代碼非 36 
					{
						//System.out.println("出貨單狀態錯誤 ，該筆出貨單號的物流驗收訊息代碼非 36  ");
					}
					else if (sErrno.equals("E2.07")) //資料異常 ，訂單編號及出貨單號與 sin 檔案中資料不同 
					{
						//System.out.println("資料異常 ，訂單編號及出貨單號與 sin 檔案中資料不同 ");
					}
					else 
					{
						//System.out.println("其他未知错误 ");
					}

				}
			}

			br.close();  
			fr.close();  
			in.close();      
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return "";
	}


	/**
	 * 門市代碼與出貨日更新處理回覆 ，每週一至週六 12:35、13:35 置於 FTP 中的 SURP 資料夾供 eShop 廠商主動下載
	 * 純文字檔。 檔名格式為 829XXXyyyymmddNN.FILEOK  (對應廠商上傳之 SUP 檔名)。 
	 * @param apiUrl
	 * @param ecNo
	 * @param sonEcNo
	 * @return
	 */
	public String SURP_FILEOK(String apiUrl,String ecNo,String sonEcNo) 
	{
		try 
		{
			File file = new File("D:\\TEST\\ABCDEF2019043001.FILEOK");  
			//读
			FileInputStream  in = new FileInputStream(file); 
			InputStreamReader fr = new InputStreamReader(in,"utf-8");  
			BufferedReader br = new BufferedReader(fr); 

			String json="";
			String line;  
			while ((line=br.readLine()) != null)
			{  
				//UTF-8 BOM标记
				if(line.length()>0)
				{
					if((int)line.charAt(0)==65279)
					{
						json+=line.substring(1);
					}
					else
					{
						json+=line;
					}
				}

			}           

			if (json.equals("S2.01")) //檔案傳送成功 ，傳送時間內 XML 檔存在 FTP 中 
			{
				//System.out.println("檔案傳送成功");
			}
			else if (json.equals("E1.01")) //XML 格式不正確，XML 檔案格式不正確 
			{
				//System.out.println("XML 檔案格式不正確 ");
			}
			else if (json.equals("E1.10")) //檔案名稱已存在資料庫 ，XML 檔案名稱重複
			{
				//System.out.println("XML 檔案名稱重複");
			}
			else if (json.equals("E1.14")) //檔案名稱格式不符規定 ，XML 檔案名稱規格不符 
			{
				//System.out.println("XML 檔案名稱規格不符 ");
			}
			else if (json.equals("E2.01")) //檔案未傳送 ，傳送時間內無任何檔案存在 FTP 中
			{
				//System.out.println("傳送時間內無任何檔案存在 FTP 中");
			}
			else if (json.equals("E2.04")) //檔案內容損毀，檔案無法讀取 
			{
				//System.out.println("檔案無法讀取 ");
			}
			else if (json.equals("01120")) //格式不符規定，XML 檔案內容的格式不符合規定 
			{
				//System.out.println("XML 檔案內容的格式不符合規定 ");
			}			
			else  //未知其他错误
			{
				//System.out.println("未知其他错误");
			}


			br.close();  
			fr.close();  
			in.close();      
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return "";
	}



}
