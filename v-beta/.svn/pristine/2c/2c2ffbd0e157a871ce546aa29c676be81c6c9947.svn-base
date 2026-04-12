package com.dsc.spos.waimai;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.springframework.http.RequestEntity;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.OrderUtil;
import com.dsc.spos.utils.PosPub;

public class SANSONGService 
{

  /*合作伙伴方数据*/
  //闪送订单号
  //private static String issOrderNumber = "TDH2017083119524158";
  //合作方平台号
  private static String partnerNo = "1987";
  //合作方订单号（订单号已经存在,请匆重复下单！请修改，orderNumber）
  //合作方签名key
  private static String key = "wgp3o0g09s1f";

  private static String HOST = "http://open.s.bingex.com";

  private static String API_VERSION = "3";

  private static String merchantId = "SS1987";

  private static String token = "6ACvKNG2pQfLBkotz1/SpZ6nKGHxrLDLJp2hEHAWkWU=";

  private static String notifyUrl = "http://商户回调地址";

  public static void main(String[] args) throws UnsupportedEncodingException {

      /**生成签名**/
//      String s = generateSignature(partnerNo, orderNumber, merchantMobile, key);
//      //System.out.println(s);
//      //BCB76765124A29F7863D6DF182E8BA28
//
//      /**计费**/
//      calc();
//
//      /**下单**/
//      save();
      //TDH2017083119524158
  }

  /**
   * 生成密钥
   *
   * @param partnerNo
   * @param orderNo
   * @param senderMobile
   * @param key
   * @return
   * @throws UnsupportedEncodingException 
   */
  private static String generateSignature(String partnerNo, String orderNo, String senderMobile, String key) throws UnsupportedEncodingException {
      //return PosPub.encodeMD5(partnerNo.concat(orderNo).concat(senderMobile).concat(key));
  	return MD5(partnerNo.concat(orderNo).concat(senderMobile).concat(key));
  }

  /**
   * 计算费用 ,计费接口其实可以和下单接口合并成一个
   * @throws ParseException 
   * @throws IOException 
   */
  public static JSONObject addOrderFortdd(List<Map<String, Object>> listsqldate,List<Map<String, Object>> listshop,String callback,String type) throws ParseException, IOException {
  	String ssurl=listshop.get(0).get("CURL").toString();//http://open.s.bingex.com/openapi/order/v3/
	  String APPKey=listshop.get(0).get("CAPPKEY").toString();//商户编号:1987
	  String APPSecret=listshop.get(0).get("CAPPSECRET").toString();//wgp3o0g09s1f
		String APPSignKey=listshop.get(0).get("CAPPSIGNKEY").toString();//TOKEN:6ACvKNG2pQfLBkotz1/SpZ6nKGHxrLDLJp2hEHAWkWU=
		String ORDERSHOPNO=listshop.get(0).get("ORDERSHOPNO").toString();//门店号处理
		String DELMEMO=listsqldate.get(0).get("DELMEMO").toString();  
		partnerNo=APPKey;
		key=APPSecret;
	   //物流下单接口
			String url="";
			if(type.equals("1"))
			{
				//预下单
				url="calc";
			}
			if(type.equals("2"))
			{
				//下单
				url="save";
			}
			ssurl+=url;
			
  	SanSongmodel smodel=new SanSongmodel();
  	smodel.setPartnerNo(APPKey);
  	
  	Order or=new Order();
  	or.setOrderNo(listsqldate.get(0).get("ORDERNO").toString());
  	or.setAddition("0");
  	String goodname="";
		for (Map<String, Object> map : listsqldate) 
		{
			goodname+=map.get("PLUNAME").toString()+",";
	  }
		goodname=goodname.substring(0, goodname.length()-1);
		or.setGoods(goodname);
		or.setWeight("1");
		
		String pickupTime="";
		pickupTime=listsqldate.get(0).get("SHIPDATE").toString();
	  String sdtime=listsqldate.get(0).get("SHIPTIME").toString();
	  String[] datalist = new String[2];
	  if(sdtime.contains("-"))
	  {
	  	datalist=sdtime.split("-");
	  }
	  sdtime= datalist[0];
	  pickupTime+=" "+sdtime;
	  pickupTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime));
	  //2小时外为预约单  
		long longcur=System.currentTimeMillis();
		long longsta= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(pickupTime).getTime();
		//差别小时数
		long diff=(longsta-longcur)/(1000*60*60);
		if(diff>=2)
		{
			or.setAppointTime(pickupTime);
		}
		
	  
	  Merchant mh=new Merchant();
	  mh.setId("SS"+APPKey);
	  mh.setMobile(listshop.get(0).get("PHONE").toString());
	  mh.setName(listshop.get(0).get("ORG_NAME").toString());
	  
	  mh.setToken(APPSignKey);
	  or.setMerchant(mh);
	  
	  Sender sd=new Sender();
	  sd.setAddr(listshop.get(0).get("ADDRESS").toString());
	  sd.setAddrDetail(listshop.get(0).get("ADDRESS").toString());
	  sd.setCity(listshop.get(0).get("CITY").toString());
	  double[] baidulist= OrderUtil.gaoDeToBaidu(Double.parseDouble(listshop.get(0).get("LONGITUDE").toString()), Double.parseDouble(listshop.get(0).get("LATITUDE").toString()));
	  sd.setLng(baidulist[0]+"");
	  sd.setLat(baidulist[1]+"");
		sd.setMobile(listshop.get(0).get("PHONE").toString());
		sd.setName(listshop.get(0).get("ORG_NAME").toString());
		or.setSender(sd);
	  
		ReceiverList rels=new ReceiverList();
		rels.setAddr(listsqldate.get(0).get("ADDRESS").toString());
		rels.setAddrDetail(listsqldate.get(0).get("ADDRESS").toString());
		rels.setCity(listsqldate.get(0).get("CITY").toString());
	  double[] baidulist1= OrderUtil.gaoDeToBaidu(Double.parseDouble(listsqldate.get(0).get("LONGITUDE").toString()), Double.parseDouble(listsqldate.get(0).get("LATITUDE").toString()));
	  rels.setLng(baidulist1[0]+"");
	  rels.setLat(baidulist1[1]+"");
	  rels.setMobile(listsqldate.get(0).get("CONTTEL").toString());
	  rels.setName(listsqldate.get(0).get("CONTMAN").toString());
	  or.setReceiverList(new ArrayList<ReceiverList>());
	  or.getReceiverList().add(rels);
	  
		smodel.setOrder(or);
		String sign= generateSignature(APPKey, listsqldate.get(0).get("ORDERNO").toString(), listshop.get(0).get("PHONE").toString() , APPSecret);
		smodel.setSignature(sign);
		JSONObject param = (JSONObject) JSONObject.toJSON(smodel);
		String res=HttpSend.Sendhttp("POST", param.toJSONString(), ssurl);
		JSONObject jobject= JSONObject.parseObject(res);
		return jobject;
		
  }

  
  /**
   * 测试取消订单信息
   * @throws IOException 
   */
  public static JSONObject cancel(List<Map<String, Object>> listsqldate,List<Map<String, Object>> listshop) throws IOException {
  	String ssurl=listshop.get(0).get("CURL").toString();//http://open.s.bingex.com/
	  String APPKey=listshop.get(0).get("CAPPKEY").toString();//商户编号:1987
	  String APPSecret=listshop.get(0).get("CAPPSECRET").toString();//wgp3o0g09s1f
		String APPSignKey=listshop.get(0).get("CAPPSIGNKEY").toString();//TOKEN:6ACvKNG2pQfLBkotz1/SpZ6nKGHxrLDLJp2hEHAWkWU=
		String ORDERSHOPNO=listshop.get(0).get("ORDERSHOPNO").toString();//门店号处理
		String DELMEMO=listsqldate.get(0).get("DELMEMO").toString(); 
		partnerNo=APPKey;
		key=APPSecret;
  	
  	String orderNumber=listsqldate.get(0).get("ORDERNO").toString();
  	String merchantMobile=listshop.get(0).get("PHONE").toString();
  	String issOrderNumber=listsqldate.get(0).get("DELIVERYNO").toString();
  	
      String sign = getSignature(merchantMobile,orderNumber);
      String url = ssurl + String.format("/openapi/order/v" + API_VERSION + "/cancel?partnerno=%s&orderno=%s&mobile=%s&issorderno=%s&signature=%s", partnerNo, orderNumber, merchantMobile, issOrderNumber, sign);
  		String res=HttpSend.Sendhttp("GET","", url);
  		JSONObject jobject= JSONObject.parseObject(res);
  		return jobject;
  }

  
  public static String MD5(String parameter) {
    try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digestResult = md.digest(StringUtils.defaultString(parameter).getBytes("GBK"));
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < digestResult.length; i++) {
            int val = ((int) digestResult[i]) & 0xff;
            hexValue.append(StringUtils.leftPad(Integer.toHexString(val), 2, '0'));
        }
        return hexValue.toString().toUpperCase();
    } catch (Exception ex) {
       
    }
    return parameter;
}
  
  /**
   * 此接口用于商户回调
   * 测试状态推送接口
   */
//  public void testSyncOrderStatus() {
//
//      String status = "闪送完成";
//      String statuscode = "60";
//
//      String signature = getSignature(merchantMobile);
//      String params = String.format("partnerno=%s&orderno=%s&mobile=%s&signature=%s&issorderno=%s&status=%s&statuscode=%s",
//              partnerNo, orderNumber, merchantMobile, signature, issOrderNumber, status, statuscode);
//      String url = notifyUrl + "?" + params;
//      try {
//          HttpClient client = new HttpClient();
//          GetMethod httpMethod = new GetMethod();
//          try {
//              httpMethod.setURI(new URI(url));
//          } catch (URIException e) {
//              
//          }
//          try {
//              int method = client.executeMethod(httpMethod);
//              //System.out.println(method);
//              String s = httpMethod.getResponseBodyAsString();
//              //System.out.println(s);
//          } catch (IOException e) {
//              
//          }
//      } catch (Exception e) {
//          
//      }
//  }

  private static String getSignature(String merchantMobile ,String orderNumber) throws UnsupportedEncodingException {
      return generateSignature(partnerNo, orderNumber, merchantMobile, key);
  }

}

class SanSongmodel
{
  private Order order;
  private String partnerNo;
  private String signature;
  public void setOrder(Order order) {
       this.order = order;
   }
   public Order getOrder() {
       return order;
   }

  public void setPartnerNo(String partnerNo) {
       this.partnerNo = partnerNo;
   }
   public String getPartnerNo() {
       return partnerNo;
   }

  public void setSignature(String signature) {
       this.signature = signature;
   }
   public String getSignature() {
       return signature;
   }
}


class Merchant {
   private String id;
   private String mobile;
   private String name;
   private String token;
   public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

   public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getMobile() {
        return mobile;
    }

   public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

   public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}


class ReceiverList {

   private String addr;
   private String addrDetail;
   private String city;
   private String lat;
   private String lng;
   private String mobile;
   private String subNumber;
   private String name;
   public void setAddr(String addr) {
        this.addr = addr;
    }
    public String getAddr() {
        return addr;
    }

   public void setAddrDetail(String addrDetail) {
        this.addrDetail = addrDetail;
    }
    public String getAddrDetail() {
        return addrDetail;
    }

   public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }

   public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLat() {
        return lat;
    }

   public void setLng(String lng) {
        this.lng = lng;
    }
    public String getLng() {
        return lng;
    }

   public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getMobile() {
        return mobile;
    }

   public void setSubNumber(String subNumber) {
        this.subNumber = subNumber;
    }
    public String getSubNumber() {
        return subNumber;
    }

   public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}

class Sender {

   private String addr;
   private String addrDetail;
   private String city;
   private String lat;
   private String lng;
   private String mobile;
   private String name;
   public void setAddr(String addr) {
        this.addr = addr;
    }
    public String getAddr() {
        return addr;
    }

   public void setAddrDetail(String addrDetail) {
        this.addrDetail = addrDetail;
    }
    public String getAddrDetail() {
        return addrDetail;
    }

   public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }

   public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLat() {
        return lat;
    }

   public void setLng(String lng) {
        this.lng = lng;
    }
    public String getLng() {
        return lng;
    }

   public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getMobile() {
        return mobile;
    }

   public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}



class Order {

   private String orderNo;
   private String addition;
   private String goods;
   private String weight;
   private String appointTime;
   private String remark;
   private Merchant merchant;
   private List<ReceiverList> receiverList;
   private Sender sender;
   public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    public String getOrderNo() {
        return orderNo;
    }

   public void setAddition(String addition) {
        this.addition = addition;
    }
    public String getAddition() {
        return addition;
    }

   public void setGoods(String goods) {
        this.goods = goods;
    }
    public String getGoods() {
        return goods;
    }

   public void setWeight(String weight) {
        this.weight = weight;
    }
    public String getWeight() {
        return weight;
    }

   public void setAppointTime(String appointTime) {
        this.appointTime = appointTime;
    }
    public String getAppointTime() {
        return appointTime;
    }

   public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getRemark() {
        return remark;
    }

   public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
    public Merchant getMerchant() {
        return merchant;
    }

   public void setReceiverList(List<ReceiverList> receiverList) {
        this.receiverList = receiverList;
    }
    public List<ReceiverList> getReceiverList() {
        return receiverList;
    }

   public void setSender(Sender sender) {
        this.sender = sender;
    }
    public Sender getSender() {
        return sender;
    }

}

