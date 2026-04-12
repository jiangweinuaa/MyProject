package com.dsc.spos.scheduler.job;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SDOrderPush extends InitJob
{
Logger logger = LogManager.getLogger(SDOrderPush.class.getName());
	
	static boolean bRun=false;//标记此服务是否正在执行中
	String jddjLogFileName = "SDOrderPushlog";
	public String doExe() throws Exception
	{
		String sReturnInfo = "";
		//官网物流推送单
		HelpTools.writelog_fileName("【同步任务SDOrderPush】同步START！",jddjLogFileName);
		 //此服务是否正在执行中
		if (bRun)
		{		
			logger.info("\r\n*********同步任务SDOrderPush同步正在执行中,本次调用取消:************\r\n");
			HelpTools.writelog_fileName("【同步任务SDOrderPush】同步正在执行中,本次调用取消！",jddjLogFileName);
			return sReturnInfo;
		}
		bRun=true;//	
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
		String curdate=new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
        
		List<Map<String, Object>> listsql =new ArrayList<>();
		try
		{
			String sql="select A.*,rawtohex(A.Order_Id)as AGUID,B.ITEM,B.PLUBARCODE,B.PLUNAME,B.SPECNAME,B.ATTRNAME,B.PRICE,B.QTY,D.PAYCODE,D.PAYNAME,D.PAYSERNUM,C.MEMO CMEMO  "
					+ " from OC_ORDER A left join OC_ORDER_DETAIL B on A.eid=B.eid and A.SHOPID=B.SHOPID "
					+ " and A.ORDERNO=B.ORDERNO  and A.LOAD_DOCTYPE=B.LOAD_DOCTYPE  	"
					+ " left join OC_ORDER_DETAIL_MEMO C on A.eid=C.eid and A.SHOPID=C.SHOPID and A.ORDERNO=C.ORDERNO and B.item=C.OITEM "
					+ " left join OC_ORDER_PAY D on A.eid=D.eid and A.SHOPID=D.SHOPID and A.ORDERNO=D.ORDERNO " 
					+ " where A.UPDATE_TIME>='"+curdate+"' and  (A.SHIPTYPE='2' or (A.SHIPTYPE='3' and A.DeliveryStutas='7'  )  ) and ( (A.DeliveryStutas is null or A.DeliveryStutas='-1' )  or (A.SHIPTYPE='3' and A.DeliveryStutas='7'  )  )"
					+ " and A.SHOPID like 'BJ%'  and A.eid='99'   and A.status!='0'  and (A.Load_Doctype='4' or A.Load_Doctype='6' or A.Load_Doctype='7' or A.Load_Doctype='9'  or A.Load_Doctype='10' ) "
					+ "  and (A.ORDERTYPE='1' or A.ORDERTYPE is null or (A.ORDERTYPE='2' and A.status!='1'  )   )  "
					+ " order by A.SHOPID,A.orderno  ";
			
			   listsql=this.doQueryData(sql, null);
		}
		catch(Exception ex)
		{}
		
		if(listsql!=null&&!listsql.isEmpty())
		{
			try
			{
					//过滤一下单头
					Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
					condition.put("EID", true);
					condition.put("SHOPID", true);
					condition.put("ORDERNO", true);
					//调用过滤函数
					List<Map<String, Object>> getQHeader = MapDistinct.getMap(listsql, condition);
					
				  condition = new HashMap<String, Boolean>(); //查询条件
					condition.put("EID", true);
					condition.put("SHOPID", true);
					condition.put("ORDERNO", true);
					condition.put("ITEM", true);
					//调用过滤函数
					String shopId= "";
					String orderno="";
					String eId="";
					
					List<Map<String, Object>> getQDetail = MapDistinct.getMap(listsql, condition);
					for (Map<String, Object> map : getQHeader) 
					{
						try
						{
							//String LOAD_DOCTYPE=map.get("LOAD_DOCTYPE").toString();
							
							shopId= map.get("SHOPID").toString();
							orderno=map.get("ORDERNO").toString();
							eId=map.get("EID").toString();
							
							SDOrderPushModel SDM=new SDOrderPushModel();
							SDM.setCmd("order.create");
							SDM.setVersion("1.0");
							SDM.setTimestamp(System.currentTimeMillis()+"");
							SDM.setAppid("wdmcake_kd");
							SDM.setNeed_sign("no");
							
							SDM.setSign("");
							body by1=new body();
							order by=new order();
							String createBy = "";
							String createByName = "";
							try 
							{
								createBy = map.get("CREATEBY").toString();
								createByName = map.get("CREATEBYNAME").toString();
							} 
							catch (Exception e) 
							{
				
				
							}
							by.setKfgh(createBy);
							by.setKf_name(createByName);
							
							by.setError("0");
							by.setMsg("success");
							by.setErp_order_sn(map.get("AGUID").toString());
							
							by.setMd_erpcode(map.get("SHOPID").toString());
							//by.setMd_erpcode("910");
							
							by.setMd_name(map.get("SHOPNAME").toString());
							by.setOrder_sn(map.get("ORDERNO").toString());
							by.setMake_erpcode(map.get("SHIPPINGSHOP").toString());
							//by.setMake_erpcode("910");
							
							by.setMake_mdname(map.get("SHIPPINGSHOPNAME").toString());
							if(map.get("SHIPPINGSHOP").toString()==null||map.get("SHIPPINGSHOP").toString().isEmpty())
							{
								by.setMake_erpcode(map.get("SHOPID").toString());
								by.setMake_mdname(map.get("SHOPNAME").toString());
							}
							
							by.setOrderman(map.get("CONTMAN").toString());
							by.setOrdertel(map.get("CONTTEL").toString());
							
							by.setConsignee(map.get("GETMAN").toString());
							by.setMobile(map.get("GETMANTEL").toString());
							if(map.get("GETMAN").toString()==null||map.get("GETMAN").toString().isEmpty())
							{
								by.setConsignee(map.get("CONTMAN").toString());
							}
							if(map.get("GETMANTEL").toString()==null||map.get("GETMANTEL").toString().isEmpty())
							{
								by.setMobile(map.get("CONTTEL").toString());
							}
								
							by.setAddress(map.get("ADDRESS").toString());
							//开始拆分时间
							String SHIPDATE=map.get("SHIPDATE").toString();
							String SHIPTIME=map.get("SHIPTIME").toString();
							String[] listtime=new String[2];
							String psdate=new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(SHIPDATE)) ;
							String s1="" ;
							String s2="" ;
							
							if(SHIPTIME.contains("-"))
							{
								listtime=SHIPTIME.split("-");
							  s1=new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("HHmmss").parse(listtime[0])) ;
							  s2=new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("HHmmss").parse(listtime[1])) ;
							}
							else
							{
								s1=new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("HHmmss").parse(SHIPTIME)) ;
								
								Calendar cal = Calendar.getInstance();   
				        cal.setTime(new SimpleDateFormat("HHmmss").parse(SHIPTIME));   
				        cal.add(Calendar.MINUTE, 30);// 24小时制   
								
							  s2=new SimpleDateFormat("HH:mm").format(cal.getTime() ) ;
							}
							
							//by.setBest_time(best_time);
							by.setBest_time(psdate+" "+s1+"-"+s2);
							
							by.setShipping_id("1");
							by.setShipping_name("味多美配送");
							//配送方式如果是门店配送
							String memo=map.get("MEMO").toString();
							if(map.get("LOAD_DOCTYPE").toString().equals("4"))
							{
								//加入手工单号
								memo+=map.get("MANUALNO").toString();
							}
							
							by.setPostscript(memo);
							String CREATE_DATETIME=map.get("CREATE_DATETIME").toString();
							
							Calendar cal = Calendar.getInstance();   
			        cal.setTime(new SimpleDateFormat("yyyyMMddhhmmss").parse(CREATE_DATETIME));   
			        cal.add(Calendar.HOUR, -8);// 24小时制   
			        
							by.setAdd_time( (cal.getTime().getTime() )/1000 +"");
							
							//by.setPay_time(map.get("AAA").toString());
							String ISINVOICE=map.get("ISINVOICE").toString();
							String INVOICETYPE=map.get("INVOICETYPE").toString();
							String invtype="不需要发票";
							if(ISINVOICE.equals("Y"))
							{
								if(INVOICETYPE.equals("1"))
								{
									invtype="个人发票";
								}
								else
								{
									invtype="公司发票";
								}
							}
							
							by.setInv_type(invtype);
							
							by.setInv_payee(map.get("INVOICETITLE").toString());
							//by.setInv_content("蛋糕");
							by.setShipping_fee(map.get("SHIPFEE").toString());
							//商品总价应该不包括配送费
							double dtotamt= Double.parseDouble(map.get("TOT_AMT").toString())-Double.parseDouble(map.get("SHIPFEE").toString());
							by.setGoods_amount(dtotamt+"");
							by.setDiscount(map.get("TOT_DISC").toString());
							by.setMoney_paid(map.get("PAYAMT").toString());
							//还需支付的金额为TOT_AMT-PAYAMT
							double dfamt= Double.parseDouble(map.get("TOT_AMT").toString())-Double.parseDouble(map.get("PAYAMT").toString());
							
							by.setOrder_amount(dfamt+"");
							//by.setDeposit(map.get("AAA").toString());
							//by.setSurplus(map.get("AAA").toString());
							by.setOrder_status("1");
							if(map.get("STATUS").toString().equals("12") || map.get("STATUS").toString().equals("3"))
							{
								by.setOrder_status("2");
							}
							
							if(map.get("SHIPTYPE").toString().equals("3"))
							{
								by.setOrder_status("2");
								by.setShipping_id("3");
								by.setShipping_name("门店自提");
							}
							
							String PAYSTATUS= map.get("PAYSTATUS").toString();
							if(PAYSTATUS.equals("1"))
							{
								PAYSTATUS="0";
							}
							else
							{
								PAYSTATUS="2";
							}
							by.setPay_status(PAYSTATUS);
							
							by.setPay_id(map.get("PAYCODE").toString());
							by.setPay_name(map.get("PAYNAME").toString());
							//by.setPay_note(map.get("AAA").toString());
							by.setPay_note(map.get("PAYSERNUM").toString());
							by.setAgency_id(map.get("OUTDOCTYPE").toString());
							by.setAgency_name(map.get("OUTDOCTYPENAME").toString());
							if(map.get("LOAD_DOCTYPE").toString().equals("4")||map.get("LOAD_DOCTYPE").toString().equals("9") )
							{
								by.setAgency_id("2");
								by.setAgency_name("门店订单");
							}
							if(map.get("LOAD_DOCTYPE").toString().equals("7"))
							{
								by.setAgency_id("346");
								by.setAgency_name("舞象");
							}
							
							String ORDERTYPE=map.get("ORDERTYPE").toString();
							if(ORDERTYPE.equals("1"))
							{
								ORDERTYPE="11";
							}
							if(ORDERTYPE.equals("2"))
							{
								ORDERTYPE="13";
							}
							
							if(ORDERTYPE==null||ORDERTYPE.isEmpty())
							{
								ORDERTYPE="13";
							}
							
							by.setOrder_type(ORDERTYPE);
							
	//						by.setE_country(map.get("COUNTY").toString());
	//						by.setE_province(map.get("PROVINCE").toString());
	//						by.setE_city(map.get("CITY").toString());
							String adress =map.get("ADDRESS").toString();
							String PROVINCE=map.get("PROVINCE").toString();
							String CITY=map.get("CITY").toString();
							String COUNTY=map.get("COUNTY").toString();
							ParseJson pj = new ParseJson();
							
//							if(PROVINCE==null||PROVINCE.isEmpty()||CITY==null||CITY.isEmpty()||COUNTY==null||COUNTY.isEmpty())
//							{
//								try
//								{
//									//调用下地图
//								  //如果为配送的时候需要先把地址解析成高德的地址，然后推荐一个生产门店和配送门店
//					  			adress=URLEncoder.encode(adress, "utf-8" ); 
//					  		  //调用高德地图查找经纬度   味多美的key
//								  String surl="https://restapi.amap.com/v3/geocode/geo?address="+"北京市"+adress+"&output=JSON&key=575f128b5da35246777d2c5c24f9b68b";
//									String responseStr= OrderUtil.Sendcom("",surl,"GET");
//									//解析返回的内容
//									GaoDeGeoModel curreginfo=pj.jsonToBean(responseStr, new TypeToken<GaoDeGeoModel>(){});
//									
//									if(curreginfo.getStatus().equals("1"))
//									{
//										if(map.get("PROVINCE").toString()==null||map.get("PROVINCE").toString().isEmpty())
//										{
//											PROVINCE=curreginfo.getGeocodes().get(0).getProvince();
//										}
//										if(map.get("CITY").toString()==null||map.get("CITY").toString().isEmpty())
//										{
//											CITY=curreginfo.getGeocodes().get(0).getCity();
//										}
//										if(map.get("COUNTY").toString()==null||map.get("COUNTY").toString().isEmpty())
//										{
//											COUNTY=curreginfo.getGeocodes().get(0).getDistrict();
//										}
//									}
//								}
//								catch(Exception ex)
//								{
//									//调用地图失败
//								}
//								
//							}
							
							PROVINCE=PROVINCE.replace("省", "");
							PROVINCE=PROVINCE.replace("市", "");
							by.setE_country(PROVINCE);
							by.setE_province(map.get("CITY").toString());
							by.setE_city(map.get("COUNTY").toString());
							
							by.setScts(map.get("PROMEMO").toString());
							by.setWsts(map.get("DELMEMO").toString());
			//				by.setTo_buyer(map.get("AAA").toString());
							by.setIs_special("0");
			//				by.setTask_number(map.get("AAA").toString());
			//				by.setMake_status(map.get("AAA").toString());
			//				by.setMake_start_time(map.get("AAA").toString());
			//				by.setMake_finished_time(map.get("AAA").toString());
							by.setGoods(new ArrayList<goods>());
							String is_special="0";
							
							int numcount=0;
							for (Map<String, Object> mapdetail : getQDetail) 
							{
								if(map.get("EID").toString().equals(mapdetail.get("EID").toString())&&map.get("SHOPID").toString().equals(mapdetail.get("SHOPID").toString())&&map.get("ORDERNO").toString().equals(mapdetail.get("ORDERNO").toString()))
								{
									goods gs=new goods();
									gs.setItem(mapdetail.get("ITEM").toString());
									gs.setGoods_sn(mapdetail.get("PLUBARCODE").toString());
									gs.setGoods_name(mapdetail.get("PLUNAME").toString());
									gs.setGoods_attr(mapdetail.get("SPECNAME").toString());
									gs.setPizi(mapdetail.get("ATTRNAME").toString());
									gs.setGoods_price(mapdetail.get("PRICE").toString());
									gs.setGoods_number(mapdetail.get("QTY").toString());
									gs.setGreetings(mapdetail.get("CMEMO").toString());
									by.getGoods().add(gs);
									try
									{
									  numcount+=Integer.parseInt(mapdetail.get("QTY").toString()) ;
									}
									catch(Exception ex)
									{
										
									}
									try
									{
										String SPECNAME=mapdetail.get("SPECNAME").toString();
										if(SPECNAME!=null&&!SPECNAME.isEmpty()&&SPECNAME.length()>=2)
										{
											SPECNAME=SPECNAME.substring(0, 2);
											if(PosPub.isNumeric(SPECNAME))
											{
												int ispec=Integer.parseInt(SPECNAME);
												if(ispec>=40||SPECNAME.contains("层")  )
												{
													is_special="1";
													by.setIs_special("1");
												}
											}
										}
									}
									catch(Exception ex)
									{}
									
									if(ORDERTYPE.equals("2"))
									{
										by.setTask_number("1");
									}
									else
									{
										by.setTask_number(numcount+"");
									}
									
								}
							}
							//大蛋糕的标志改一下
							String PSHOPREMIND=map.get("PSHOPREMIND").toString();
							if(PSHOPREMIND!=null&&!PSHOPREMIND.isEmpty()&&(PSHOPREMIND.equals("2")||PSHOPREMIND.equals("3") ))
							{
								by.setIs_special("1");
							}
							//经纬度
							String LATITUDE=map.get("LATITUDE").toString();
							String LONGITUDE=map.get("LONGITUDE").toString();
							by.setLatitude(LATITUDE);
							by.setLongitude(LONGITUDE);
							
							
							by1.setOrder(by);
							
							SDM.setBody(by1);
							
						//SDM.setTimestamp("1553313060048");
							//开始发送官网订单
						//加入签名的过程
							String sbody=pj.beanToJson(by1);
							String urlcode=URLEncoder.encode(sbody, "utf-8" );
							String urlucode= string2Unicode(sbody);
							
							String skey="appid=wdmcake_kd&body="+urlucode+"&cmd=order.create&"+"timestamp="+SDM.getTimestamp()+"&version=1.0&wdmcake_kd&123456";
							//转SHA1和大写
							skey=getSha1(skey).toUpperCase();
							
							//SDM.setSign(skey);
							//SDM.setSign("123");
							
							String req=pj.beanToJson(SDM);
							//url拼接的方式调用post1553313060048
							String url="http://kd.wdmcake.cn/api/mos/index.php?";
							url+="cmd=order.create&appid=wdmcake_kd&need_sign=no"+"&timestamp="+SDM.getTimestamp()+"&version=1.0"+"&sign="+skey+"&body="+urlcode;
							
							//url=URLEncoder.encode(url, "utf-8" ); 
							
							HelpTools.writelog_fileName("【同步任务任开始：" + req,jddjLogFileName);
							//String res=HttpSend.Sendhttp("POST", req, "http://kd.wdmcake.cn/api/platform/index.php");
							String res=HttpSend.Sendhttp("POST", "", url);
							HelpTools.writelog_fileName("【同步任务任返回：" + res,jddjLogFileName);
							
							JSONObject reqobject=new JSONObject(res);
							JSONObject resbody=reqobject.getJSONObject("body");
							
							if(resbody.get("errno").toString().equals("0"))
							{
								//代表成功,更新订单成 已下单
							  // values
								Map<String, DataValue> values = new HashMap<String, DataValue>() ;
								DataValue v = new DataValue("0", Types.VARCHAR);
								values.put("DeliveryStutas", v);
								DataValue v1 = new DataValue("1", Types.VARCHAR);
								values.put("DeliveryType", v1);
								
//								DataValue v2 = new DataValue("", Types.VARCHAR);
//								values.put("PSHOPREMIND", v2);
	
								// condition
								Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
								DataValue c1 = new DataValue(shopId, Types.VARCHAR);
								conditions.put("OrganizationNO", c1);
								DataValue c2 = new DataValue(eId, Types.VARCHAR);
								conditions.put("EID", c2);      
								DataValue c3 = new DataValue(shopId, Types.VARCHAR);
								conditions.put("SHOPID", c3);    
								DataValue c4 = new DataValue(orderno, Types.VARCHAR);
								conditions.put("orderno", c4);        
								this.doUpdate("OC_ORDER", values, conditions);
							}
							else
							{
								HelpTools.writelog_fileName("【同步任务任返回：" + resbody.getString("msg"),jddjLogFileName);
							}
							
							pj=null;
						}
						catch(Exception ex)
						{
							HelpTools.writelog_fileName("【订单同步失败：" + orderno +ex.toString(),jddjLogFileName);
						}
					}
					
		  }
			catch (Exception e) 
			{
		// TODO: handle exception
				HelpTools.writelog_fileName("【同步失败：" + e.toString(),jddjLogFileName);
				bRun=false;
	    }
			
		}
		else
		{
			sReturnInfo="无符合要求的数据！";
			HelpTools.writelog_fileName("【同步任务SDOrderPush】没有需要处理任务！",jddjLogFileName);
			logger.info("\r\n******同步任务SDOrderPush没有需要处理的任务******\r\n");
			bRun=false;
		}
		bRun=false;
		return "";
	}
	
	public String string2Unicode(String string) {
    StringBuffer unicode = new StringBuffer();
    for (int i = 0; i < string.length(); i++) {
        // 取出每一个字符
        char c = string.charAt(i);
        // 转换为unicode
        unicode.append("\\u" + Integer.toHexString(c));
    }
    return unicode.toString();
}
	
	public String getSha1(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    if (null == str || str.length() == 0){
        return null;
    }
    char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};
    try {
        MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
        mdTemp.update(str.getBytes("UTF-8"));

        byte[] md = mdTemp.digest();
        int j = md.length;
        char[] buf = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
            buf[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(buf);
    } catch (Exception e) 
    {

        return null;
    }
}

	
	
	
}

class SDOrderPushModel
{
	private String cmd;
	private String need_sign;
	
	private String version;
	private String timestamp;
	private String appid;
	private String sign;
	private body body;
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public body getBody() {
	return body;
	}
	public void setBody(body body) {
	this.body = body;
	}
	public String getNeed_sign() {
	return need_sign;
	}
	public void setNeed_sign(String need_sign) {
	this.need_sign = need_sign;
	}

}

class body
{
	private order order;

	public order getOrder() {
	return order;
	}

	public void setOrder(order order) {
	this.order = order;
	}
}

class order
{
	private String error;
	private String msg;
	private String erp_order_sn;
	private String md_erpcode;
	
	//private String md_id;
	private String md_name;
	private String order_sn;
	private String make_erpcode;
	
	private String make_mdname;
	private String consignee;
	private String mobile;
	private String orderman;
	private String ordertel;
	private String address;
	private String best_time;
	
	//private String user_shipping_time;
	private String shipping_id;
	private String shipping_name;
	private String postscript;
	private String add_time;
	private String pay_time;
	private String inv_type;
	private String inv_payee;
	private String inv_content;
	private String shipping_fee;
	private String goods_amount;
	private String discount;
	private String money_paid;
	private String order_amount;
	private String deposit;
	private String surplus;
	private String order_status;
	private String pay_status;
	private String pay_id;
	private String pay_name;
	private String pay_note;
	private String pay_trade_no;
	private String agency_id;
	private String agency_name;
	private String order_type;
	private String e_country;
	private String e_province;
	private String e_city;
	private String scts;
	private String wsts;
	private String to_buyer;
	private String is_special;
	private String task_number;
	private String make_status;
	private String make_start_time;
	private String make_finished_time;
	private String longitude;
	private String latitude;
	
	private String kfgh;
	private String kf_name;
	
	private List<goods> goods;

	public String getMd_name() {
		return md_name;
	}
	public void setMd_name(String md_name) {
		this.md_name = md_name;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getMake_mdname() {
		return make_mdname;
	}
	public void setMake_mdname(String make_mdname) {
		this.make_mdname = make_mdname;
	}
	public String getConsignee() {
		return consignee;
	}
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getOrderman() {
		return orderman;
	}
	public void setOrderman(String orderman) {
		this.orderman = orderman;
	}
	public String getOrdertel() {
		return ordertel;
	}
	public void setOrdertel(String ordertel) {
		this.ordertel = ordertel;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getShipping_id() {
		return shipping_id;
	}
	public void setShipping_id(String shipping_id) {
		this.shipping_id = shipping_id;
	}
	public String getShipping_name() {
		return shipping_name;
	}
	public void setShipping_name(String shipping_name) {
		this.shipping_name = shipping_name;
	}
	public String getPostscript() {
		return postscript;
	}
	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public String getPay_time() {
		return pay_time;
	}
	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}
	public String getInv_type() {
		return inv_type;
	}
	public void setInv_type(String inv_type) {
		this.inv_type = inv_type;
	}
	public String getInv_payee() {
		return inv_payee;
	}
	public void setInv_payee(String inv_payee) {
		this.inv_payee = inv_payee;
	}
	public String getInv_content() {
		return inv_content;
	}
	public void setInv_content(String inv_content) {
		this.inv_content = inv_content;
	}
	public String getShipping_fee() {
		return shipping_fee;
	}
	public void setShipping_fee(String shipping_fee) {
		this.shipping_fee = shipping_fee;
	}
	public String getGoods_amount() {
		return goods_amount;
	}
	public void setGoods_amount(String goods_amount) {
		this.goods_amount = goods_amount;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getMoney_paid() {
		return money_paid;
	}
	public void setMoney_paid(String money_paid) {
		this.money_paid = money_paid;
	}
	public String getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(String order_amount) {
		this.order_amount = order_amount;
	}
	public String getDeposit() {
		return deposit;
	}
	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}
	public String getSurplus() {
		return surplus;
	}
	public void setSurplus(String surplus) {
		this.surplus = surplus;
	}
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	public String getPay_status() {
		return pay_status;
	}
	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}
	public String getPay_id() {
		return pay_id;
	}
	public void setPay_id(String pay_id) {
		this.pay_id = pay_id;
	}
	public String getPay_name() {
		return pay_name;
	}
	public void setPay_name(String pay_name) {
		this.pay_name = pay_name;
	}
	public String getPay_note() {
		return pay_note;
	}
	public void setPay_note(String pay_note) {
		this.pay_note = pay_note;
	}
	public String getPay_trade_no() {
		return pay_trade_no;
	}
	public void setPay_trade_no(String pay_trade_no) {
		this.pay_trade_no = pay_trade_no;
	}
	public String getAgency_id() {
		return agency_id;
	}
	public void setAgency_id(String agency_id) {
		this.agency_id = agency_id;
	}
	public String getAgency_name() {
		return agency_name;
	}
	public void setAgency_name(String agency_name) {
		this.agency_name = agency_name;
	}
	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}


	public String getScts() {
		return scts;
	}
	public void setScts(String scts) {
		this.scts = scts;
	}
	public String getWsts() {
		return wsts;
	}
	public void setWsts(String wsts) {
		this.wsts = wsts;
	}
	public String getTo_buyer() {
		return to_buyer;
	}
	public void setTo_buyer(String to_buyer) {
		this.to_buyer = to_buyer;
	}
	public String getIs_special() {
		return is_special;
	}
	public void setIs_special(String is_special) {
		this.is_special = is_special;
	}
	public String getTask_number() {
		return task_number;
	}
	public void setTask_number(String task_number) {
		this.task_number = task_number;
	}
	public String getMake_status() {
		return make_status;
	}
	public void setMake_status(String make_status) {
		this.make_status = make_status;
	}
	public String getMake_start_time() {
		return make_start_time;
	}
	public void setMake_start_time(String make_start_time) {
		this.make_start_time = make_start_time;
	}
	public String getMake_finished_time() {
		return make_finished_time;
	}
	public void setMake_finished_time(String make_finished_time) {
		this.make_finished_time = make_finished_time;
	}
	public List<goods> getGoods() {
		return goods;
	}
	public void setGoods(List<goods> goods) {
		this.goods = goods;
	}
	public String getError() {
	return error;
	}
	public void setError(String error) {
	this.error = error;
	}
	public String getMsg() {
	return msg;
	}
	public void setMsg(String msg) {
	this.msg = msg;
	}
	public String getErp_order_sn() {
	return erp_order_sn;
	}
	public void setErp_order_sn(String erp_order_sn) {
	this.erp_order_sn = erp_order_sn;
	}
	public String getMd_erpcode() {
	return md_erpcode;
	}
	public void setMd_erpcode(String md_erpcode) {
	this.md_erpcode = md_erpcode;
	}
	public String getMake_erpcode() {
	return make_erpcode;
	}
	public void setMake_erpcode(String make_erpcode) {
	this.make_erpcode = make_erpcode;
	}
	public String getBest_time() {
	return best_time;
	}
	public void setBest_time(String best_time) {
	this.best_time = best_time;
	}
	public String getE_city() {
	return e_city;
	}
	public void setE_city(String e_city) {
	this.e_city = e_city;
	}
	public String getE_province() {
	return e_province;
	}
	public void setE_province(String e_province) {
	this.e_province = e_province;
	}
	public String getE_country() {
	return e_country;
	}
	public void setE_country(String e_country) {
	this.e_country = e_country;
	}
	public String getKfgh() {
		return kfgh;
	}
	public void setKfgh(String kfgh) {
		this.kfgh = kfgh;
	}
	public String getKf_name() {
		return kf_name;
	}
	public void setKf_name(String kf_name) {
		this.kf_name = kf_name;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
}

class goods
{
	private String item;
	private String goods_sn;
	private String goods_name;
	private String goods_attr;
	private String pizi;
	private String goods_price;
	private String goods_number;
	private String greetings;
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getGoods_sn() {
		return goods_sn;
	}
	public void setGoods_sn(String goods_sn) {
		this.goods_sn = goods_sn;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getGoods_attr() {
		return goods_attr;
	}
	public void setGoods_attr(String goods_attr) {
		this.goods_attr = goods_attr;
	}
	public String getPizi() {
		return pizi;
	}
	public void setPizi(String pizi) {
		this.pizi = pizi;
	}
	public String getGoods_price() {
		return goods_price;
	}
	public void setGoods_price(String goods_price) {
		this.goods_price = goods_price;
	}
	public String getGoods_number() {
		return goods_number;
	}
	public void setGoods_number(String goods_number) {
		this.goods_number = goods_number;
	}
	public String getGreetings() {
		return greetings;
	}
	public void setGreetings(String greetings) {
		this.greetings = greetings;
	}
	
}




