package com.dsc.spos.utils.logistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.dsc.spos.utils.FtpUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.google.common.base.Strings;

//黑猫宅急便
//FTP 站台位置：ftp://cvsftp.cvs.com.tw(FTP 帳密請洽便利達康) 
//便利達康後台網址：https://b2b.cvs.com.tw/cvs2
public class Egs 
{


	/**
	 * 查询EGS资讯
	 * @param apiUrl
	 * @return
	 */
	public String query_egs_info(String apiUrl) 
	{

		String resbody="";

		try 
		{			
			String request="cmd=query_egs_info";

			request=java.net.URLEncoder.encode(request, "utf-8");

			resbody=HttpSend.SendEGS("query_egs_info", request, apiUrl);

			String[] splitStrings=resbody.split("&");


			if (splitStrings.length>0) 
			{
				//OK|ERROR
				String[] splitStatus=splitStrings[0].split("=");
				String status=splitStatus[1];

				if (status.equals("ERROR")) 
				{
					//message错误信息
					String[] splitMessage=splitStrings[1].split("=");
					String message=splitMessage[1];
					message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
					message = message.replaceAll("\\+", "%2B");  //+
					message = URLDecoder.decode(message, "UTF-8");	
					//System.out.println(message);
				}
				else 
				{					
					//egs_version版本
					String[] splitEgs_version=splitStrings[1].split("=");
					String egs_version=splitEgs_version[1];				

					//address_db_version版本库
					String[] splitAddress_db_version=splitStrings[2].split("=");
					String address_db_version=splitAddress_db_version[1];

					//sandbox_mode启用沙箱模式
					String[] splitSandbox_mode=splitStrings[3].split("=");
					String sandbox_mode=splitSandbox_mode[1];		

					//internet_online启用网络连接
					String[] splitInternet_online=splitStrings[4].split("=");
					String internet_online=splitInternet_online[1];			
				}

			}			

		} 
		catch (Exception e) 
		{
			resbody="";
		}

		return resbody;

	}



	/**
	 * 查詢速達五碼郵遞區號對應的轉運站名稱
	 * @param apiUrl
	 * @param suda5                                                    最大限制200
	 * @return
	 */
	public String query_base(String apiUrl,String[] suda5)
	{
		String resbody="";

		try 
		{			
			String request="cmd=query_base";

			for (int i = 0; i < suda5.length; i++) 
			{
				request+="&suda5_" + (i+1) +"=" + suda5[i];
			}

			resbody=HttpSend.SendEGS("query_base", request, apiUrl);

			String[] splitStrings=resbody.split("&");

			if (splitStrings.length>0) 
			{
				//OK|ERROR
				String[] splitStatus=splitStrings[0].split("=");
				String status=splitStatus[1];

				if (status.equals("ERROR")) 
				{
					//message错误信息
					String[] splitMessage=splitStrings[1].split("=");
					String message=splitMessage[1];
					message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
					message = message.replaceAll("\\+", "%2B");  //+
					message = URLDecoder.decode(message, "UTF-8");	
					//System.out.println(message);
				}
				else 
				{
					//base
					for (int a = 1; a < splitStrings.length; a++) 
					{						
						String[] splitBase=splitStrings[a].split("=");
						String base=splitBase[1];
						//System.out.println(base);

					}

				}

			}			

		} 
		catch (Exception e) 
		{
			resbody="";
		}

		return resbody;

	}



	/**
	 * 查詢速達7碼資料(轉運站及速達5碼邮号有dash分隔)
	 * @param apiUrl
	 * @param address               最大限制200
	 * @return
	 */
	public String query_suda7_dash(String apiUrl,String[] address)
	{
		String resbody="";

		try 
		{			
			String request="cmd=query_suda7_dash";

			for (int i = 0; i < address.length; i++) 
			{
				request+="&address_" + (i+1) +"=" + java.net.URLEncoder.encode(address[i], "utf-8");
			}

			resbody=HttpSend.SendEGS("query_suda7_dash", request, apiUrl);

			String[] splitStrings=resbody.split("&");

			if (splitStrings.length>0) 
			{
				//OK|ERROR
				String[] splitStatus=splitStrings[0].split("=");
				String status=splitStatus[1];

				if (status.equals("ERROR")) 
				{
					//message错误信息
					String[] splitMessage=splitStrings[1].split("=");
					String message=splitMessage[1];
					message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
					message = message.replaceAll("\\+", "%2B");  //+
					message = URLDecoder.decode(message, "UTF-8");	
					//System.out.println(message);
				}
				else 
				{
					//suda7
					for (int a = 1; a < splitStrings.length; a++) 
					{						
						String[] splitSuda7=splitStrings[a].split("=");
						if (splitSuda7.length>1) 
						{
							String suda7=splitSuda7[1];
							//System.out.println(suda7);
						}						

					}

				}

			}			

		} 
		catch (Exception e) 
		{
			resbody="";
		}

		return resbody;		
	}


	/**
	 * 查詢地址對應的速達五碼郵遞區號
	 * @param apiUrl
	 * @param address
	 * @return
	 */
	public String query_suda5(String apiUrl,String[] address)
	{

		String resbody="";

		try 
		{			
			String request="cmd=query_suda5";

			for (int i = 0; i < address.length; i++) 
			{
				request+="&address_" + (i+1) +"=" + java.net.URLEncoder.encode(address[i], "utf-8");
			}

			resbody=HttpSend.SendEGS("query_suda5", request, apiUrl);

			String[] splitStrings=resbody.split("&");

			if (splitStrings.length>0) 
			{
				//OK|ERROR
				String[] splitStatus=splitStrings[0].split("=");
				String status=splitStatus[1];

				if (status.equals("ERROR")) 
				{
					//message错误信息
					String[] splitMessage=splitStrings[1].split("=");
					String message=splitMessage[1];
					message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
					message = message.replaceAll("\\+", "%2B");  //+
					message = URLDecoder.decode(message, "UTF-8");	
					//System.out.println(message);
				}
				else 
				{
					//suda5
					for (int a = 1; a < splitStrings.length; a++) 
					{						
						String[] splitSuda5=splitStrings[a].split("=");
						String suda5=splitSuda5[1];
						//System.out.println(suda5);

					}

				}

			}			

		} 
		catch (Exception e) 
		{
			resbody="";
		}

		return resbody;		
	}


	/**
	 * 申請速達託運單號碼
	 * @param apiUrl
	 * @param customer_id
	 * @param waybill_type 託運單類別  A=一般託運單 B=代收託運單 G=報值託運單
	 * @param count  申请数量
	 * @return
	 */
	public String query_waybill_id_range(String apiUrl,String customer_id,String waybill_type,int count)
	{
		String resbody="";

		try 
		{			
			String request="cmd=query_waybill_id_range&customer_id=" + customer_id +"&waybill_type=" + waybill_type +"&count=" + count;

			resbody=HttpSend.SendEGS("query_waybill_id_range", request, apiUrl);

			String[] splitStrings=resbody.split("&");

			if (splitStrings.length>0) 
			{
				//OK|ERROR
				String[] splitStatus=splitStrings[0].split("=");
				String status=splitStatus[1];

				if (status.equals("ERROR")) 
				{
					//message错误信息
					String[] splitMessage=splitStrings[1].split("=");
					String message=splitMessage[1];
					message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
					message = message.replaceAll("\\+", "%2B");  //+
					message = URLDecoder.decode(message, "UTF-8");	
					//System.out.println(message);
				}
				else 
				{
					//waybill_type
					String[] splitWaybill_type=splitStrings[1].split("=");
					String rwaybill_type=splitWaybill_type[1];
					//System.out.println(rwaybill_type);

					//waybill_id
					String[] splitwaybill_id=splitStrings[2].split("=");
					String waybill_id=splitwaybill_id[1];
					//System.out.println(waybill_id);					

				}

			}			

		} 
		catch (Exception e) 
		{
			resbody="";
		}

		return resbody;		

	}


	/**
	 * 查詢速達託運單號碼存量
	 * @param apiUrl
	 * @param customer_id
	 * @param waybill_type
	 * @return
	 */
	public String query_waybill_id_remain(String apiUrl,String customer_id,String waybill_type)
	{

		String resbody="";

		try 
		{			
			String request="cmd=query_waybill_id_remain&customer_id=" + customer_id +"&waybill_type=" + waybill_type;

			resbody=HttpSend.SendEGS("query_waybill_id_remain", request, apiUrl);

			String[] splitStrings=resbody.split("&");

			if (splitStrings.length>0) 
			{
				//OK|ERROR
				String[] splitStatus=splitStrings[0].split("=");
				String status=splitStatus[1];

				if (status.equals("ERROR")) 
				{
					//message错误信息
					String[] splitMessage=splitStrings[1].split("=");
					String message=splitMessage[1];
					message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
					message = message.replaceAll("\\+", "%2B");  //+
					message = URLDecoder.decode(message, "UTF-8");	
					//System.out.println(message);
				}
				else 
				{
					//waybill_id_remain
					String[] splitWaybill_id_remain=splitStrings[1].split("=");
					String waybill_id_remain=splitWaybill_id_remain[1];
					//System.out.println(waybill_id_remain);

					//waybill_type
					String[] splitWaybill_type=splitStrings[2].split("=");
					String rwaybill_type=splitWaybill_type[1];
					//System.out.println(rwaybill_type);				

				}

			}			

		} 
		catch (Exception e) 
		{
			resbody="";
		}

		return resbody;		

	}


	/**
	 * 5.11	查詢速達收寄件人地址的距離
	 * @param apiUrl
	 * @param suda5_senderpostcode   如果为空填入00000
	 * @param suda5_customerpostcode 如果为空填入99999
	 * @return
	 */
	public String query_distance(String apiUrl,String[] suda5_senderpostcode,String[] suda5_customerpostcode)
	{
		String resbody="";

		try 
		{			
			String request="cmd=query_distance";

			for (int i = 0; i < suda5_senderpostcode.length; i++) 
			{
				request+="&suda5_senderpostcode_" + (i+1) +"=" + suda5_senderpostcode[i] +"&suda5_customerpostcode_"+ (i+1) +"=" + suda5_customerpostcode[i];
			}

			resbody=HttpSend.SendEGS("query_distance", request, apiUrl);

			String[] splitStrings=resbody.split("&");

			if (splitStrings.length>0) 
			{
				//OK|ERROR
				String[] splitStatus=splitStrings[0].split("=");
				String status=splitStatus[1];

				if (status.equals("ERROR")) 
				{
					//message错误信息
					String[] splitMessage=splitStrings[1].split("=");
					String message=splitMessage[1];
					message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
					message = message.replaceAll("\\+", "%2B");  //+
					message = URLDecoder.decode(message, "UTF-8");	
					//System.out.println(message);
				}
				else 
				{
					//distance
					for (int a = 1; a < splitStrings.length; a++) 
					{						
						String[] splitDistance=splitStrings[a].split("=");
						String distance=splitDistance[1];
						//System.out.println(distance);

					}
				}

			}			

		} 
		catch (Exception e) 
		{
			resbody="";
		}

		return resbody;		

	}






	/**
	 * 傳送單筆託運單資料
	 * @param apiUrl
	 * @param customer_id     契客代码
	 * @param tracking_number 托运单号
	 * @param order_no  订单编号
	 * @param receiver_name 收件人姓名
	 * @param receiver_address 收件人地址
	 * @param receiver_suda5 收件人速达五码邮号
	 * @param receiver_mobile 收件人手机
	 * @param receiver_phone 收件人电话
	 * @param sender_name  寄件人姓名
	 * @param sender_address  寄件人地址
	 * @param sender_suda5  寄件人速达五码邮号
	 * @param sender_phone 寄件人电话
	 * @param product_price 代收货款金额
	 * @param product_name 品名
	 * @param comment  备注
	 * @param package_size 尺寸代码 0001=60cm,0002=90cm,0003=120cm,0004=150cm
	 * @param temperature 温层代码  0001=常溫  0002=冷藏  0003=冷凍
	 * @param distance 距离  00=同縣市   01=外縣市  02=離島
	 * @param delivery_date 指定配达日期  2011-04-25
	 * @param delivery_timezone 指定配达时段  1=9~12時  2=12~17時  3=17~20時  4=不限時
	 * @param create_time 建立时间 2011-04-24 11:27:59
	 * @param print_time 打印时间  2011-04-24 11:27:59
	 * @param account_id 托运单帐号
	 * @param member_no 会员编号
	 * @param taxin 进口关税 海外地区使用   0
	 * @param insurance 报值金额   0
	 * @return
	 */
	public String transfer_waybill(String apiUrl,String customer_id,String tracking_number,String order_no,String receiver_name,String receiver_address,
			String receiver_suda5,String receiver_mobile,String receiver_phone,String sender_name,String sender_address,String sender_suda5,
			String sender_phone,String product_price,String product_name,String comment,String package_size,String temperature,
			String distance,String delivery_date,String delivery_timezone,String create_time,String print_time,String account_id,
			String member_no,String taxin,String insurance)
	{
		String resbody="";

		try 
		{			
			order_no=java.net.URLEncoder.encode(order_no, "utf-8");			
			receiver_name=java.net.URLEncoder.encode(receiver_name, "utf-8");
			receiver_address=java.net.URLEncoder.encode(receiver_address, "utf-8");
			receiver_suda5=java.net.URLEncoder.encode(receiver_suda5, "utf-8");	
			receiver_mobile=java.net.URLEncoder.encode(receiver_mobile, "utf-8");	
			receiver_phone=java.net.URLEncoder.encode(receiver_phone, "utf-8");	
			sender_name=java.net.URLEncoder.encode(sender_name, "utf-8");	
			sender_address=java.net.URLEncoder.encode(sender_address, "utf-8");	
			sender_suda5=java.net.URLEncoder.encode(sender_suda5, "utf-8");	
			sender_phone=java.net.URLEncoder.encode(sender_phone, "utf-8");	
			product_price=java.net.URLEncoder.encode(product_price, "utf-8");	
			product_name=java.net.URLEncoder.encode(product_name, "utf-8");	
			comment=java.net.URLEncoder.encode(comment, "utf-8");	
			package_size=java.net.URLEncoder.encode(package_size, "utf-8");	
			temperature=java.net.URLEncoder.encode(temperature, "utf-8");	
			distance=java.net.URLEncoder.encode(distance, "utf-8");	
			delivery_date=java.net.URLEncoder.encode(delivery_date, "utf-8");	
			delivery_timezone=java.net.URLEncoder.encode(delivery_timezone, "utf-8");	
			create_time=java.net.URLEncoder.encode(create_time, "utf-8");	
			print_time=java.net.URLEncoder.encode(print_time, "utf-8");	
			account_id=java.net.URLEncoder.encode(account_id, "utf-8");	
			member_no=java.net.URLEncoder.encode(member_no, "utf-8");	
			taxin=java.net.URLEncoder.encode(taxin, "utf-8");	
			insurance=java.net.URLEncoder.encode(insurance, "utf-8");			


			String request="cmd=transfer_waybill&customer_id=" + customer_id +"&tracking_number=" + tracking_number +"&order_no=" + order_no + "&receiver_name=" + receiver_name +"&receiver_address=" + receiver_address
					+"&receiver_suda5=" +receiver_suda5+"&receiver_mobile="+receiver_mobile+"&receiver_phone="+receiver_phone+"&sender_name="+sender_name+"&sender_address"+sender_address+"&sender_suda5="+sender_suda5
					+"&sender_phone="+sender_phone+"&product_price="+product_price+"&product_name="+product_name+"&comment="+comment+"&package_size="+package_size+"&temperature="+temperature
					+"&distance="+distance+"&delivery_date="+delivery_date+"&delivery_timezone="+delivery_timezone+"&create_time="+create_time+"&print_time="+print_time+"&account_id="+account_id
					+"&member_no="+member_no+"&taxin="+taxin+"&insurance="+insurance;



			resbody=HttpSend.SendEGS("transfer_waybill", request, apiUrl);

			String[] splitStrings=resbody.split("&");

			if (splitStrings.length>0) 
			{
				//OK|ERROR
				String[] splitStatus=splitStrings[0].split("=");
				String status=splitStatus[1];

				if (status.equals("ERROR")) 
				{
					//message错误信息
					String[] splitMessage=splitStrings[1].split("=");
					String message=splitMessage[1];
					message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
					message = message.replaceAll("\\+", "%2B");  //+
					message = URLDecoder.decode(message, "UTF-8");	
					//System.out.println(message);

				}

			}			

		} 
		catch (Exception e) 
		{
			resbody="";
		}

		return resbody;		

	}


	/**
	 * FTP方式上传托运单资料 FTP的SEND目录下,BIG5,3码ftpCustomerid+mmdd+NN.eod
	 * FTP的RECEIVE下面会有.sod处理回复档，但是目前没有规格说明
	 * ftp://61.57.227.35/
	 * @param ftpCustomerID
	 * @param mmdd
	 * @param ftpuid
	 * @param ftppwd
	 * @param OrderEGSlist
	 * @return
	 */
	public boolean EOD(String apiUrl,final String ftpCustomerID,final String mmdd,String ftpuid,String ftppwd,List<Map<String, Object>> OrderEGSlist)
	{
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
						int lastIndex = name.lastIndexOf(".eod");
						// 
						String str = name.substring(0,lastIndex);

						//匹配 
						if(str.startsWith(ftpCustomerID+mmdd))
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

			String localpath=sSysPro+"\\webapps\\LG\\egs\\upload";	
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
					int lastIndex = path.getName().lastIndexOf(".eod");
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


			if (OrderEGSlist.size()>0) 
			{
				String filename=ftpCustomerID+mmdd+sno+".eod";

				String filePath=localpath;			

				File txtFile = new File(filePath +"\\"+ filename);
				FileOutputStream writerStream = new FileOutputStream(txtFile);
				OutputStreamWriter osw=new OutputStreamWriter(writerStream, "Big5");
				BufferedWriter writer = new BufferedWriter(osw);		

				for (Map<String, Object> oneData : OrderEGSlist)
				{
					String sline="";

					for (int pi = 0; pi < 30; pi++) 
					{
						String key="eod" + PosPub.FillStr(pi+1+"", 2, "0", true);

						String eod= oneData.get(key).toString();

						//分隔符|
						sline+=eod+"|";
					}

					writer.write(sline);	
					writer.newLine();
					/*
					String eod01= oneData.get("eod01").toString();
					String eod02= oneData.get("eod02").toString();
					String eod03= oneData.get("eod03").toString();
					String eod04= oneData.get("eod04").toString();
					String eod05= oneData.get("eod05").toString();
					String eod06= oneData.get("eod06").toString();
					String eod07= oneData.get("eod07").toString();
					String eod08= oneData.get("eod08").toString();
					String eod09= oneData.get("eod09").toString();
					String eod10= oneData.get("eod10").toString();
					String eod11= oneData.get("eod11").toString();
					String eod12= oneData.get("eod12").toString();
					String eod13= oneData.get("eod13").toString();
					String eod14= oneData.get("eod14").toString();
					String eod15= oneData.get("eod15").toString();
					String eod16= oneData.get("eod16").toString();
					String eod17= oneData.get("eod17").toString();
					String eod18= oneData.get("eod18").toString();
					String eod19= oneData.get("eod19").toString();
					String eod20= oneData.get("eod20").toString();
					String eod21= oneData.get("eod21").toString();
					String eod22= oneData.get("eod22").toString();
					String eod23= oneData.get("eod23").toString();
					String eod24= oneData.get("eod24").toString();
					String eod25= oneData.get("eod25").toString();
					String eod26= oneData.get("eod26").toString();
					String eod27= oneData.get("eod27").toString();
					String eod28= oneData.get("eod28").toString();
					String eod29= oneData.get("eod29").toString();
					String eod30= oneData.get("eod30").toString();
					 */			

				}

				writer.close();
				osw.close();
				writerStream.close();
				txtFile=null;

				//
				FtpUtils ftp=new FtpUtils();
				ftp.setHostname(apiUrl);
				//默认端口
				ftp.setPort(21);
				ftp.setUsername(ftpuid);
				ftp.setPassword(ftppwd);				

				//上传
				bRet=ftp.uploadFile("SEND", filename, filePath +"\\"+ filename);
				ftp=null;
			}

		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());
		}

		return bRet;
	}


	/**
	 * FTP物流状态追踪
	 * expressno 托運單號碼
	 * shipmentno 發貨單號碼
	 * sitename 物流營業所名稱
	 * inputDate 货态日期時間
	 * customer_id 客戶代碼
	 * trackId 貨態編碼
	 * trackDesc 貨態說明
	 * @return
	 */
	public List<Map<String, Object>> SOD()
	{
		List<Map<String, Object>> listExpress=new ArrayList<Map<String,Object>>();

		try 
		{
			//
			String sSysPro=System.getProperty("catalina.home");
			if (sSysPro==null) 
			{
				sSysPro="D:\\apache\\apache-tomcat-8.0.35";
			}

			String localpath=sSysPro+"\\webapps\\LG\\egs\\download";		

			File file = new File(localpath);  

			File [] files = file.listFiles();

			for (int i = 0; i < files.length; i++) 
			{
				if (files[i].isFile()) 
				{
					//读
					FileInputStream  in = new FileInputStream(files[i]); 
					InputStreamReader fr = new InputStreamReader(in,"Big5");  
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

					//905072730211|HYD2020031600001|大同營業所|20200414135810|7076259101|00001|轉運中 (即集貨)|011|
					//托運單號碼10碼+客戶端訂單號碼20碼+營業所名稱20碼+貨態輸入日期14碼+客戶代號10码+貨態ID 5碼+狀態說明60码+規格3碼
					for (int pd = 0; pd < allList.size(); pd++) 
					{
						String tempStr=allList.get(pd);

						//字符|是轉義字符
						String[] lines= tempStr.split("\\|");
											
						//
						if (lines.length>6) 
						{
							String expressno=lines[0];
							String shipmentno=lines[1];
							String sitename=lines[2];
							String inputDate=lines[3];
							String customer_id=lines[4];
							String trackId=lines[5];
							String trackDesc=lines[6];					
							//String specId=lines[7];				

							Map<String, Object> map=new HashMap<String, Object>();					
							map.put("expressno", expressno);
							map.put("shipmentno", shipmentno);
							map.put("sitename", sitename);
							map.put("inputDate", inputDate);
							map.put("customer_id", customer_id);
							map.put("trackId", trackId);
							map.put("trackDesc", trackDesc);

						
							listExpress.add(map);					
						}
					
					}

					br.close();  
					fr.close();  
					in.close(); 
				}
				    
			}

		} 
		catch (Exception e) 
		{
			return listExpress;		
		}

		return listExpress;		
	}


}
