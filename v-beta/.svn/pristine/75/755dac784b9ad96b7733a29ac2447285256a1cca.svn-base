package com.dsc.spos.waimai;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.utils.OrderUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


//百度没有询价接口

public class BAIDUService {
	public static Long app_id = (long) 1000538;
  public static String app_key = "7f00d4d709b875751b0cbbfcf2ddf392";
  public static String urlString = "http://182.61.30.232:8186";
  public static void main(String[] args) {
//      try {
//          //String post_data = generateExamplePostData();
//      	String post_data = "";
//          //request(post_data);
//      } catch (IOException e) {
//          
//      }
  }
  
  /**
   * 生成示例post_data
   * @return json字符串形式的post_data
   * @throws IOException 
   * @throws ParseException 
   */
  public static JSONObject addOrderFortdd(List<Map<String, Object>> listsqldate,List<Map<String, Object>> listshop) throws IOException, ParseException {
  	String ssurl=listshop.get(0).get("CURL").toString();//"http://182.61.30.232:8186";
	  String APPKey=listshop.get(0).get("CAPPKEY").toString();//1000538
	  String APPSecret=listshop.get(0).get("CAPPSECRET").toString();//7f00d4d709b875751b0cbbfcf2ddf392
		String APPSignKey=listshop.get(0).get("CAPPSIGNKEY").toString();//
		String ORDERSHOPNO=listshop.get(0).get("ORDERSHOPNO").toString();//门店号处理
		String DELMEMO=listsqldate.get(0).get("DELMEMO").toString();  
  	
  	ObjectMapper mapper = new ObjectMapper();
      OrderDetail orderDetail = new OrderDetail();
//      orderDetail.setBox_price("5.00");
//      orderDetail.setSend_price("8.00");
      orderDetail.setProduct_price(listsqldate.get(0).get("TOT_AMT").toString());
      
      ArrayList<Product> products = new ArrayList<Product>();
      
      for (Map<String, Object> map : listsqldate) {
      	Product product1 = new Product();
        product1.setDish_name(map.get("PLUNAME").toString());
        product1.setDish_unit_price(map.get("BPRICE").toString());
        product1.setDish_number(Long.parseLong(map.get("BQTY").toString())  );
        product1.setDish_total_price(map.get("BAMT").toString());
        //product1.setUnit_name(map.get("UNIT_NAME").toString());
//        product1.setPackge_box_price("10.00");
//        product1.setPackge_box_number(2L);
        products.add(product1);
		}
      
//      DishDetail dishDetail1 = new DishDetail("板烧猪肉饭", 1L, "碗");
//      DishDetail dishDetail2 = new DishDetail("可乐", 1L, "大杯");
//      ArrayList<DishDetail> dishDetails1 = new ArrayList<DishDetail>();
//      dishDetails1.add(dishDetail1);
//      dishDetails1.add(dishDetail2);
//      product1.setDish_details(dishDetails1);
//      products.add(product1);
//      Product product2 = new Product();
//      product2.setDish_name("板烧牛肉饭");
//      product2.setDish_unit_price("28.00");
//      product2.setDish_number(2L);
//      product2.setDish_total_price("56.00");
//      product2.setUnit_name("份");
//      product2.setPackge_box_price("10.00");
//      product2.setPackge_box_number(2L);
//      DishDetail dishDetail3 = new DishDetail("板烧牛肉饭", 1L, "碗");
//      DishDetail dishDetail4 = new DishDetail("可乐", 1L, "大杯");
//      ArrayList<DishDetail> dishDetails2 = new ArrayList<DishDetail>();
//      dishDetails2.add(dishDetail3);
//      dishDetails2.add(dishDetail4);
//      product2.setDish_details(dishDetails2);
//      products.add(product2);
      
      orderDetail.setProducts(products);
      
      
      PostData postData = new PostData();
      Long time = System.currentTimeMillis() / 1000;
      
      postData.setOut_order_id(listsqldate.get(0).get("ORDERNO").toString());
      //这里还要取一下是今天的第几张单
      postData.setOrder_index(21L);
      
//      postData.setCity_id(131L);
//      postData.setCity_name("北京市");
      
      postData.setUser_phone(listsqldate.get(0).get("CONTTEL").toString());
      //系统中存的是高德，将此坐标转换为百度
      double[] baidulist= OrderUtil.gaoDeToBaidu(Double.parseDouble(listsqldate.get(0).get("LONGITUDE").toString()), Double.parseDouble(listsqldate.get(0).get("LATITUDE").toString()));
      postData.setUser_longitude(baidulist[0]+"");
      postData.setUser_latitude(baidulist[1]+"");
      postData.setUser_address(listsqldate.get(0).get("ADDRESS").toString());
      postData.setUser_name(listsqldate.get(0).get("GETMAN").toString());
      postData.setUser_sex(1L);
      //门店ID 先直接取门店的shopId
      postData.setWl_shop_id(ORDERSHOPNO);
      //先填门店电话，看是否需要换成公司的电话
      postData.setShop_phone(listshop.get(0).get("PHONE").toString());
      postData.setOrder_time(time);
      
      //处理时间
      String pickupTime="";
			pickupTime=listsqldate.get(0).get("SHIPDATE").toString();
		  String sdtime=listsqldate.get(0).get("SHIPTIME").toString();
		  String[] datalist = new String[2];
		  if(sdtime.contains("-"))
		  {
		  	datalist=sdtime.split("-");
		  }
		  String bdate=datalist[0];
	  	String edate=datalist[1];
	  	
  	  String bdatetime=pickupTime+""+bdate;
  	  String edatetime=pickupTime+""+edate;
      
      postData.setExpect_time_mode(2L);
      postData.setExpect_time_start(new SimpleDateFormat("yyyyMMddHHmmss").parse(bdatetime).getTime());
      postData.setExpect_time(new SimpleDateFormat("yyyyMMddHHmmss").parse(edatetime).getTime());
      
      postData.setShop_price(listsqldate.get(0).get("TOT_AMT").toString());
      postData.setUser_price(listsqldate.get(0).get("TOT_AMT").toString());
      postData.setUser_total_price(listsqldate.get(0).get("PAYAMT").toString());
      postData.setRemark(DELMEMO);
      
      String PAYSTATUS=listsqldate.get(0).get("PAYSTATUS").toString();
  		if(PAYSTATUS.equals("3"))
  		{
  			postData.setPay_type(1L);
  		}
  		else
  		{
  			postData.setPay_type(1L);
  		}
      
      postData.setNeed_invoice(0L);
      //postData.setInvoice_title("百度时代网络技术（北京）有限公司");
      postData.setOrder_detail(orderDetail);
      postData.setPush_time(time);
      
      postData.setApp_id(Long.parseLong(APPKey));
      postData.setSource_name("官网订单");
      // 生成sign
      String sign = generateSign(postData, Long.parseLong(APPKey), APPSecret);
      postData.setSign(sign);
      // 将postData转换成json字符串
      String post_data = mapper.writeValueAsString(postData);
      String res= request(post_data,ssurl+"/api/createorder");
      JSONObject resultJson = JSONObject.parseObject(res);
  		return resultJson;
  }
  
  /**
   * 生成示例post_data
   * @return json字符串形式的post_data
   * @throws IOException 
   */
  public static JSONObject getCancelOrder(List<Map<String, Object>> listsqldate,List<Map<String, Object>> listshop) throws IOException {
  	String ssurl=listshop.get(0).get("CURL").toString();//"http://182.61.30.232:8186";
	  String APPKey=listshop.get(0).get("CAPPKEY").toString();//1000538
	  String APPSecret=listshop.get(0).get("CAPPSECRET").toString();//7f00d4d709b875751b0cbbfcf2ddf392
		String APPSignKey=listshop.get(0).get("CAPPSIGNKEY").toString();//
		String ORDERSHOPNO=listshop.get(0).get("ORDERSHOPNO").toString();//门店号处理
		String DELMEMO=listsqldate.get(0).get("DELMEMO").toString();  
  	
  	ObjectMapper mapper = new ObjectMapper();
      
      PostData postData = new PostData();
      Long time = System.currentTimeMillis() / 1000;
      postData.setOrder_id(Integer.parseInt(listsqldate.get(0).get("DELIVERYNO").toString()) );
      postData.setReason_id(2);
      postData.setApp_id(Long.parseLong(APPKey));
      // 生成sign
      String sign = generateSign(postData, Long.parseLong(APPKey), APPSecret);
      postData.setSign(sign);
      // 将postData转换成json字符串
      String post_data = mapper.writeValueAsString(postData);
      
      String res= request(post_data,ssurl+"/api/createorder");
      JSONObject resultJson = JSONObject.parseObject(res);
  		return resultJson;
  }
  
  
  /**
   * 模拟post请求
   * 建议使用Apache HttpClient等第三方库简化操作
   * @param post_data
   * @throws IOException
   */
  public static String request(String post_data,String iurl) throws IOException {
      URL url = new URL(iurl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      OutputStream outputStream = connection.getOutputStream();
      outputStream.write(post_data.getBytes());
      outputStream.flush();
      if (connection.getResponseCode() != 200) {
          throw new RuntimeException("Failed : HTTP error code : "
                  + connection.getResponseCode());
      }
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String ret = null;
      while ((ret = bufferedReader.readLine()) != null) {
         // //System.out.println(ret);
      }
      connection.disconnect();
      return ret;
      
  }
  /**
   * 生成sign值
   * 生成map再拼接
   * @param post_data
   * @param app_id
   * @param app_key
   * @return
   */
  public static String generateSign(PostData post_data, Long app_id, String app_key) {
      Map<String, Object> postDataMap = transPostData2TreeMap(post_data);
      StringBuilder sb = new StringBuilder();
      for (Map.Entry<String, Object> entry : postDataMap.entrySet()) {
          if (entry.getValue() instanceof Long
                  || entry.getValue() instanceof String) {
              sb.append(String.format("%s=%s&", entry.getKey(), entry.getValue()));
          }
      }
      sb.append(app_id + "&" + app_key);
      String ret = SHA1(sb.toString());
      return  ret;
  }
  /**
   * 自定义的sha1转换函数
   * 推荐使用Apache Commons Codec中的DigestUtils.sha1Hex(stringToConvertToSHexRepresentation)来进行转换
   * @param text
   * @return
   */
  public static String SHA1(String text) {
      try {
          MessageDigest digest = MessageDigest.getInstance("SHA-1");
          digest.reset();
          digest.update(text.getBytes());
          byte messageDigest[] = digest.digest();
          // Create Hex String
          StringBuffer hexString = new StringBuffer();
          // 字节数组转换为 十六进制 数
          for (int i = 0; i < messageDigest.length; i++) {
              String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
              if (shaHex.length() < 2) {
                  hexString.append(0);
              }
              hexString.append(shaHex);
          }
          return hexString.toString();
      } catch (NoSuchAlgorithmException e) {
          
      }
      return "";
  }
  /**
   * 将PostData对象转换成TreeMap，将属性按字母升序排列，剔除sign属性
   * @param obj
   * @return
   */
  public static Map<String, Object> transPostData2TreeMap(Object obj) {
      if(obj == null){
          return null;
      }
      Map<String, Object> map = new TreeMap<String, Object>();
      try {
          BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
          PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
          for (PropertyDescriptor property : propertyDescriptors) {
              String key = property.getName();
              // 过滤class属性和sign属性
              if (!key.equals("class") && !key.equals("sign")) {
                  // 得到property对应的getter方法
                  Method getter = property.getReadMethod();
                  Object value = getter.invoke(obj);
                  if (value != null) {
                      map.put(key, value);
                  }
              }
          }
      } catch (Exception e) {
          ////System.out.println("transBean2Map Error " + e);
      }
      return map;
  }
}
class DishDetail {
  private String name;
  private Long number;
  private String unit_name;
  public DishDetail() {
  }
  public DishDetail(String name, Long number, String unit_name) {
      this.name = name;
      this.number = number;
      this.unit_name = unit_name;
  }
  public String getName() {
      return name;
  }
  public void setName(String name) {
      this.name = name;
  }
  public Long getNumber() {
      return number;
  }
  public void setNumber(Long number) {
      this.number = number;
  }
  public String getUnit_name() {
      return unit_name;
  }
  public void setUnit_name(String unit_name) {
      this.unit_name = unit_name;
  }
}

class Product {
  private String dish_name;
  private String dish_unit_price;
  private Long dish_number;
  private String dish_total_price;
  private String unit_name;
  private String packge_box_price;
  private Long packge_box_number;
  private ArrayList<DishDetail> dish_details;
  public Product() {
  }
  public Product(String dish_name,
                 String dish_unit_price,
                 Long dish_number,
                 String dish_total_price,
                 String unit_name,
                 String packge_box_price,
                 Long packge_box_number) {
      this.dish_name = dish_name;
      this.dish_unit_price = dish_unit_price;
      this.dish_number = dish_number;
      this.dish_total_price = dish_total_price;
      this.unit_name = unit_name;
      this.packge_box_price = packge_box_price;
      this.packge_box_number = packge_box_number;
  }
  public String getDish_name() {
      return dish_name;
  }
  public void setDish_name(String dish_name) {
      this.dish_name = dish_name;
  }
  public String getDish_unit_price() {
      return dish_unit_price;
  }
  public void setDish_unit_price(String dish_unit_price) {
      this.dish_unit_price = dish_unit_price;
  }
  public Long getDish_number() {
      return dish_number;
  }
  public void setDish_number(Long dish_number) {
      this.dish_number = dish_number;
  }
  public String getDish_total_price() {
      return dish_total_price;
  }
  public void setDish_total_price(String dish_total_price) {
      this.dish_total_price = dish_total_price;
  }
  public String getUnit_name() {
      return unit_name;
  }
  public void setUnit_name(String unit_name) {
      this.unit_name = unit_name;
  }
  public String getPackge_box_price() {
      return packge_box_price;
  }
  public void setPackge_box_price(String packge_box_price) {
      this.packge_box_price = packge_box_price;
  }
  public Long getPackge_box_number() {
      return packge_box_number;
  }
  public void setPackge_box_number(Long packge_box_number) {
      this.packge_box_number = packge_box_number;
  }
  public ArrayList<DishDetail> getDish_details() {
      return dish_details;
  }
  public void setDish_details(ArrayList<DishDetail> dish_details) {
      this.dish_details = dish_details;
  }
}

class OrderDetail {
  private String box_price;
  private String send_price;
  private String product_price;
  private ArrayList<Product> products;
  public OrderDetail() {
  }
  public OrderDetail(String box_price, String send_price, String product_price) {
      this.box_price = box_price;
      this.send_price = send_price;
      this.product_price = product_price;
  }
  public String getBox_price() {
      return box_price;
  }
  public void setBox_price(String box_price) {
      this.box_price = box_price;
  }
  public String getSend_price() {
      return send_price;
  }
  public void setSend_price(String send_price) {
      this.send_price = send_price;
  }
  public String getProduct_price() {
      return product_price;
  }
  public void setProduct_price(String product_price) {
      this.product_price = product_price;
  }
  public ArrayList<Product> getProducts() {
      return products;
  }
  public void setProducts(ArrayList<Product> products) {
      this.products = products;
  }
}

class PostData {
	private int order_id	;
	private int reason_id;
	private String reason_detail;
	
  private String out_order_id;
  private Long order_index;
  private Long city_id;
  private String city_name;
  private String user_phone;
  private String user_longitude;
  private String user_latitude;
  private String user_address;
  private String user_name;
  private Long user_sex;
  private String wl_shop_id;
  private String shop_phone;
  private Long order_time;
  private Long expect_time;
  private Long expect_time_start;
  
  private String shop_price;
  private String user_price;
  private String user_total_price;
  private String remark;
  private Long pay_type;
  private Long pay_status;
  private Long need_invoice;
  private String invoice_title;
  private OrderDetail order_detail;
  private Long push_time;
  private Long expect_time_mode;
  private Long app_id;
  private String source_name;
  private String sign;
  public String getOut_order_id() {
      return out_order_id;
  }
  public void setOut_order_id(String out_order_id) {
      this.out_order_id = out_order_id;
  }
  public Long getOrder_index() {
      return order_index;
  }
  public void setOrder_index(Long order_index) {
      this.order_index = order_index;
  }
  public Long getCity_id() {
      return city_id;
  }
  public void setCity_id(Long city_id) {
      this.city_id = city_id;
  }
  public String getCity_name() {
      return city_name;
  }
  public void setCity_name(String city_name) {
      this.city_name = city_name;
  }
  public String getUser_phone() {
      return user_phone;
  }
  public void setUser_phone(String user_phone) {
      this.user_phone = user_phone;
  }
  public String getUser_longitude() {
      return user_longitude;
  }
  public void setUser_longitude(String user_longitude) {
      this.user_longitude = user_longitude;
  }
  public String getUser_latitude() {
      return user_latitude;
  }
  public void setUser_latitude(String user_latitude) {
      this.user_latitude = user_latitude;
  }
  public String getUser_address() {
      return user_address;
  }
  public void setUser_address(String user_address) {
      this.user_address = user_address;
  }
  public String getUser_name() {
      return user_name;
  }
  public void setUser_name(String user_name) {
      this.user_name = user_name;
  }
  public Long getUser_sex() {
      return user_sex;
  }
  public void setUser_sex(Long user_sex) {
      this.user_sex = user_sex;
  }
  public String getWl_shop_id() {
      return wl_shop_id;
  }
  public void setWl_shop_id(String wl_shop_id) {
      this.wl_shop_id = wl_shop_id;
  }
  public String getShop_phone() {
      return shop_phone;
  }
  public void setShop_phone(String shop_phone) {
      this.shop_phone = shop_phone;
  }
  public Long getOrder_time() {
      return order_time;
  }
  public void setOrder_time(Long order_time) {
      this.order_time = order_time;
  }
  public Long getExpect_time() {
      return expect_time;
  }
  public void setExpect_time(Long expect_time) {
      this.expect_time = expect_time;
  }
  public String getShop_price() {
      return shop_price;
  }
  public void setShop_price(String shop_price) {
      this.shop_price = shop_price;
  }
  public String getUser_price() {
      return user_price;
  }
  public void setUser_price(String user_price) {
      this.user_price = user_price;
  }
  public String getUser_total_price() {
      return user_total_price;
  }
  public void setUser_total_price(String user_total_price) {
      this.user_total_price = user_total_price;
  }
  public String getRemark() {
      return remark;
  }
  public void setRemark(String remark) {
      this.remark = remark;
  }
  public Long getPay_type() {
      return pay_type;
  }
  public void setPay_type(Long pay_type) {
      this.pay_type = pay_type;
  }
  public Long getPay_status() {
      return pay_status;
  }
  public void setPay_status(Long pay_status) {
      this.pay_status = pay_status;
  }
  public Long getNeed_invoice() {
      return need_invoice;
  }
  public void setNeed_invoice(Long need_invoice) {
      this.need_invoice = need_invoice;
  }
  public String getInvoice_title() {
      return invoice_title;
  }
  public void setInvoice_title(String invoice_title) {
      this.invoice_title = invoice_title;
  }
  public OrderDetail getOrder_detail() {
      return order_detail;
  }
  public void setOrder_detail(OrderDetail order_detail) {
      this.order_detail = order_detail;
  }
  public Long getPush_time() {
      return push_time;
  }
  public void setPush_time(Long push_time) {
      this.push_time = push_time;
  }
  public Long getExpect_time_mode() {
      return expect_time_mode;
  }
  public void setExpect_time_mode(Long expect_time_mode) {
      this.expect_time_mode = expect_time_mode;
  }
  public Long getApp_id() {
      return app_id;
  }
  public void setApp_id(Long app_id) {
      this.app_id = app_id;
  }
  public String getSource_name() {
      return source_name;
  }
  public void setSource_name(String source_name) {
      this.source_name = source_name;
  }
  public String getSign() {
      return sign;
  }
  public void setSign(String sign) {
      this.sign = sign;
  }
	public Long getExpect_time_start() {
	return expect_time_start;
	}
	public void setExpect_time_start(Long expect_time_start) {
	this.expect_time_start = expect_time_start;
	}
	
	public String getReason_detail() {
	return reason_detail;
	}
	public void setReason_detail(String reason_detail) {
	this.reason_detail = reason_detail;
	}
	public int getOrder_id() {
	return order_id;
	}
	public void setOrder_id(int order_id) {
	this.order_id = order_id;
	}
	public int getReason_id() {
	return reason_id;
	}
	public void setReason_id(int reason_id) {
	this.reason_id = reason_id;
	}
	

}
