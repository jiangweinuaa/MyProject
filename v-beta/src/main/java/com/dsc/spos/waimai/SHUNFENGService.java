package com.dsc.spos.waimai;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;

public class SHUNFENGService {
	public static JSONObject addOrderFortdd(List<Map<String, Object>> listsqldate, List<Map<String, Object>> listshop,
			String callback, String calltype) throws Exception {
		String ssurl = listshop.get(0).get("CURL").toString();// https://commit-openic.sf-express.com/open/api/external
		if(ssurl.isEmpty())
		{
			ssurl="https://commit-openic.sf-express.com/open/api/external";
		}
		
		String APPKey = listshop.get(0).get("CAPPKEY").toString();// 1504389204
		String APPSecret = listshop.get(0).get("CAPPSECRET").toString();// 92986391ec839f0b1aaef615bcd06929
		String APPSignKey = listshop.get(0).get("CAPPSIGNKEY").toString();//
		
		String DELMEMO = listsqldate.get(0).get("DELMEMO").toString();
//		String url = "/api/order/addOrder";
		String url = "";
		if (calltype.equals("1")) {
			// 预下单
			url = "/precreateorder";
		}
		if (calltype.equals("2")) {
			// 下单
			url = "/createorder";
		}
		ssurl += url;

		ShunFengModel pd = new ShunFengModel();
		pd.setDev_id(Integer.parseInt(APPKey));
		
		//外卖流水号/电商交易流水号		ORDER_SN
		Object orderSn = listsqldate.get(0).get("ORDER_SN");
		if(orderSn!=null&&!orderSn.toString().trim().isEmpty()){
			pd.setOrder_sequence(orderSn.toString());
		}
		
		//对应第三方店铺ID，如顺丰店铺ID
		Object ORDERSHOPNO = listshop.get(0).get("ORDERSHOPNO");// 门店号处理3243279847393测试用
		// 顺丰可以用ERP的门店编号处理
		if(ORDERSHOPNO!=null&&!ORDERSHOPNO.toString().trim().isEmpty()){
			pd.setShop_id(ORDERSHOPNO.toString());
			//1：顺丰店铺ID
			pd.setShop_type(1);
		}else{
			//ERP店铺ID
			pd.setShop_id(listshop.get(0).get("ORGANIZATIONNO").toString());
			//2：接入方店铺ID
			pd.setShop_type(2);
		}
		
		//外卖预定单		ISBOOK	NVARCHAR2(1)		Y.预定单  N.非预定单
		Object isBook = listsqldate.get(0).get("ISBOOK");
		if(isBook!=null&&isBook.toString().trim().equals("Y")){
			//顺丰 0：非预约单；1：预约单
			pd.setIs_appoint(1);
		}else{
			pd.setIs_appoint(0);
		}
		pd.setShop_order_id(listsqldate.get(0).get("ORDERNO").toString());
		
		//LOAD_DOCTYPE 1.饿了么 2.美团外卖 3.微商城 4.云POS 5.总部 6、官网 7、舞像 8 京东到家 9、APP小程序 
		//yahoosuper：yahoo超级商城，91app：91App，shopee:虾皮，letian：乐天，
		//pchome:PCHome购物，momo:MOMO购物	90.商户平台
		String loadDoctype = listsqldate.get(0).get("LOAD_DOCTYPE").toString();
		// 订单接入来源 1：美团；2：饿了么；3：百度；4：口碑；
		// 其他请直接填写中文字符串值
		if("1".equals(loadDoctype)){
			pd.setOrder_source("2");
		}else if("2".equals(loadDoctype)){
			pd.setOrder_source("1");
		}else if("3".equals(loadDoctype)){
			pd.setOrder_source("微商城");
		}else if("4".equals(loadDoctype)){
			pd.setOrder_source("云POS");
		}else if("5".equals(loadDoctype)){
			pd.setOrder_source("总部订单");
		}else if("6".equals(loadDoctype)){
			pd.setOrder_source("官网订单");
		}else if("7".equals(loadDoctype)){
			pd.setOrder_source("舞像订单");
		}else if("8".equals(loadDoctype)){
			pd.setOrder_source("京东到家");
		}else if("9".equals(loadDoctype)){
			pd.setOrder_source("APP小程序");
		}else{
			pd.setOrder_source("其他");
		}
		// 坐标类型 1：百度坐标，2：高德坐标
		pd.setLbs_type(2);
		pd.setVersion(17);

		//支付状态/货到付款标记		PAYSTATUS	NVARCHAR2(10)		1.未支付 2.部分支付 3.付清
		String PAYSTATUS = listsqldate.get(0).get("PAYSTATUS").toString();
		if (PAYSTATUS.equals("3")) {
			pd.setPay_type(1);
		} else {
			pd.setPay_type(0);
			// 先查TOTAMT 再查PAYAMT 剩下的就是要收用户的钱
			String tamt = listsqldate.get(0).get("TOT_AMT").toString();
			String pamt = listsqldate.get(0).get("PAYAMT").toString();
//			double damt = Double.parseDouble(tamt) - Double.parseDouble(pamt);
			BigDecimal damt=new BigDecimal(tamt).subtract(new BigDecimal(pamt));
//			damt = damt * 100;
			damt = damt.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
			//代收金额	单位：分
//			pd.setReceive_user_money((int) damt);
			pd.setReceive_user_money(damt.intValue());
		}
		pd.setOrder_time((int) (System.currentTimeMillis() / 1000));
		
		// 处理时间
		String pickupTime = "";
		pickupTime = listsqldate.get(0).get("SHIPDATE").toString();
		String sdtime = listsqldate.get(0).get("SHIPTIME").toString();
		String[] datalist = new String[2];

		String bdate = sdtime;
		String edate = sdtime;
		if (sdtime.contains("-")) {
			datalist = sdtime.split("-");
			bdate = datalist[0];
			edate = datalist[1];
		}

		String bdatetime = pickupTime + "" + bdate;
		String edatetime = pickupTime + "" + edate;

		pd.setExpect_time((int) (new SimpleDateFormat("yyyyMMddHHmmss").parse(bdatetime).getTime() / 1000));
		pd.setRemark(listsqldate.get(0).get("DELMEMO").toString());
		//return_flag	int	1	否	返回字段控制标志位（二进制）	1：价格，2：距离，4：重量，组合条件请相加
		//例如全部返回为填入7
		pd.setReturn_flag(7);
		pd.setPush_time((int) (System.currentTimeMillis() / 1000));
		// pd.setVersion(17);
		shop sp = new shop();
		sp.setShop_name(listshop.get(0).get("ORG_NAME").toString());
		sp.setShop_phone(listshop.get(0).get("PHONE").toString());
		sp.setShop_address(listshop.get(0).get("ADDRESS").toString());
		sp.setShop_lng(listshop.get(0).get("LONGITUDE").toString());
		sp.setShop_lat(listshop.get(0).get("LATITUDE").toString());

		receive rc = new receive();
		rc.setUser_name(listsqldate.get(0).get("CONTMAN").toString());
		rc.setUser_phone(listsqldate.get(0).get("CONTTEL").toString());
		rc.setUser_address(listsqldate.get(0).get("ADDRESS").toString());
		rc.setUser_lng(listsqldate.get(0).get("LONGITUDE").toString());
		rc.setUser_lat(listsqldate.get(0).get("LATITUDE").toString());
		rc.setCity_name(listsqldate.get(0).get("CITY").toString());

		order_detail od = new order_detail();
		BigDecimal totAmt=new BigDecimal(listsqldate.get(0).get("TOT_AMT").toString());
		//将小数位设置为0，且设置四舍五入
		totAmt=totAmt.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
		od.setTotal_price(totAmt.intValue());
		od.setProduct_type(1);
		od.setWeight_gram(1000);
		od.setProduct_num(listsqldate.size());
		od.setProduct_type_num(listsqldate.size());

		od.setProduct_detail(new ArrayList<product_detail>());
		int totqty = 0;
		
		boolean isExistCake = false;//茱莉亚需求
		
		for (Map<String, Object> map : listsqldate) {
			
			//判断下物流标签
			try 
			{
				String DELTAGNO = map.get("DELTAGNO").toString();//数据库中对应的值 标签群组标号_标签编号
				if(DELTAGNO!=null)
				{
					if(DELTAGNO.equals("13"))//兼容下以后可能直接是标签编号
					{
						isExistCake = true;
					}
					else
					{
						int indexofSpec = DELTAGNO.indexOf("_");//004_13
						String s1 = DELTAGNO.substring(0, indexofSpec);
						String s2 = DELTAGNO.substring(indexofSpec + 1, DELTAGNO.length());
						if(indexofSpec>0&&s2.equals("13"))
						{
							isExistCake = true;
						}
						
					}					
					
				}	
			} 
			catch (Exception e) 
			{
	
			}
			
			product_detail pde = new product_detail();
			pde.setProduct_name(map.get("PLUNAME").toString());
			totqty += Integer.parseInt(map.get("BQTY").toString());
			pde.setProduct_num(Integer.parseInt(map.get("BQTY").toString()));
			// product_price int(11) 0 否 物品价格
			// 此项次非必填，暂时注释掉，另价格单位可能是分，暂不确定 顺丰未详细注明此项
			// pde.setProduct_price(Integer.parseInt(map.get("PRICE").toString())
			// );
			od.getProduct_detail().add(pde);
		}
		od.setProduct_num(totqty);
		pd.setShop(sp);
		pd.setReceive(rc);
		pd.setOrder_detail(od);
		// 这三个是为预下单添加的
		if (calltype.equals("1")) {
			pd.setProduct_type(1);
			pd.setUser_address(listsqldate.get(0).get("ADDRESS").toString());
			pd.setUser_lng(listsqldate.get(0).get("LONGITUDE").toString());
			pd.setUser_lat(listsqldate.get(0).get("LATITUDE").toString());
			pd.setCity_name(listsqldate.get(0).get("CITY").toString());
			pd.setWeight(1);
		}
		
		if(isExistCake)
		{
			/*
			 * 1快餐;2送药;3百货;
					4脏衣服收;5干净衣服派;6生鲜;
					7保单;8饮品;9现场勘查;
					10快递;12文件证照;13蛋糕;
					14鲜花;15电子数码;16服装鞋帽;
					17汽车配件;18珠宝;20披萨;
					21中餐;99其他
			 */
			pd.setProduct_type(13);
		}
		

		JSONObject param = (JSONObject) JSONObject.toJSON(pd);
		// 签名
		String sign = PosPub
				.encodeBASE64(PosPub.encodeMD5(param.toJSONString() + "&" + pd.getDev_id() + "&" + APPSecret));
		ssurl += "?sign=" + sign;

		// String res=HttpSend.Sendhttp("POST", param.toJSONString(), ssurl);
		String res = HttpSend.sendShunfengPost("POST", param.toJSONString(), ssurl);
		JSONObject jobject = JSONObject.parseObject(res);
		return jobject;
	}

	public static JSONObject cancel(List<Map<String, Object>> listsqldate, List<Map<String, Object>> listshop)
			throws UnsupportedEncodingException, Exception {
		String ssurl = listshop.get(0).get("CURL").toString();// https://commit-openic.sf-express.com/open/api/external
		if(ssurl.isEmpty())
		{
			ssurl="https://commit-openic.sf-express.com/open/api/external";
		}
		
		String APPKey = listshop.get(0).get("CAPPKEY").toString();// dev_id
																	// 1504389204
		String APPSecret = listshop.get(0).get("CAPPSECRET").toString();// 92986391ec839f0b1aaef615bcd06929
		String APPSignKey = listshop.get(0).get("CAPPSIGNKEY").toString();//
		String ORDERSHOPNO = listshop.get(0).get("ORDERSHOPNO").toString();// 门店号处理3243279847393测试用
		String DELMEMO = listsqldate.get(0).get("DELMEMO").toString();
//		String url = "/api/order/addOrder";
		String url = "/cancelorder";

		JSONObject param = new JSONObject();
		param.put("dev_id", Integer.parseInt(APPKey));
		param.put("order_id", listsqldate.get(0).get("DELIVERYNO").toString());
		param.put("order_type", 1);
		param.put("push_time", (int) (System.currentTimeMillis() / 1000));
		// 签名
		String sign = PosPub.encodeBASE64(PosPub.encodeMD5(param.toJSONString() + "&" + APPKey + "&" + APPSecret));
		ssurl += url + "?sign=" + sign;
		// String res=HttpSend.Sendhttp("POST", param.toJSONString(), ssurl);
		String res = HttpSend.sendShunfengPost("POST", param.toJSONString(), ssurl);
		JSONObject jobject = JSONObject.parseObject(res);
		return jobject;

	}

}

class ShunFengModel {
	private int dev_id;
	private String shop_id;
	private String shop_order_id;
	private String order_source;
	private int lbs_type;
	private int pay_type;
	private int receive_user_money;
	private int order_time;
	private int is_appoint;
	private int expect_time;
	private String remark;
	private int return_flag;
	private int push_time;
	private int version;
	//	int(11)	1	否	店铺ID类型	1：顺丰店铺ID ；2：接入方店铺ID
	private int shop_type;
	private shop shop;
	private receive receive;
	private order_detail order_detail;

	private int product_type;
	private String user_address;
	private String user_lng;
	private String user_lat;
	private String city_name;
	private int weight;
	//取货序号
	private String order_sequence;
	

	public String getOrder_sequence() {
		return order_sequence;
	}

	public void setOrder_sequence(String order_sequence) {
		this.order_sequence = order_sequence;
	}

	public int getShop_type() {
		return shop_type;
	}

	public void setShop_type(int shop_type) {
		this.shop_type = shop_type;
	}

	public int getDev_id() {
		return dev_id;
	}

	public void setDev_id(int dev_id) {
		this.dev_id = dev_id;
	}

	public String getShop_id() {
		return shop_id;
	}

	public void setShop_id(String shop_id) {
		this.shop_id = shop_id;
	}

	public String getShop_order_id() {
		return shop_order_id;
	}

	public void setShop_order_id(String shop_order_id) {
		this.shop_order_id = shop_order_id;
	}

	public String getOrder_source() {
		return order_source;
	}

	public void setOrder_source(String order_source) {
		this.order_source = order_source;
	}

	public int getLbs_type() {
		return lbs_type;
	}

	public void setLbs_type(int lbs_type) {
		this.lbs_type = lbs_type;
	}

	public int getPay_type() {
		return pay_type;
	}

	public void setPay_type(int pay_type) {
		this.pay_type = pay_type;
	}

	public int getReceive_user_money() {
		return receive_user_money;
	}

	public void setReceive_user_money(int receive_user_money) {
		this.receive_user_money = receive_user_money;
	}

	public int getOrder_time() {
		return order_time;
	}

	public void setOrder_time(int order_time) {
		this.order_time = order_time;
	}

	public int getIs_appoint() {
		return is_appoint;
	}

	public void setIs_appoint(int is_appoint) {
		this.is_appoint = is_appoint;
	}

	public int getExpect_time() {
		return expect_time;
	}

	public void setExpect_time(int expect_time) {
		this.expect_time = expect_time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getReturn_flag() {
		return return_flag;
	}

	public void setReturn_flag(int return_flag) {
		this.return_flag = return_flag;
	}

	public int getPush_time() {
		return push_time;
	}

	public void setPush_time(int push_time) {
		this.push_time = push_time;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public shop getShop() {
		return shop;
	}

	public void setShop(shop shop) {
		this.shop = shop;
	}

	public receive getReceive() {
		return receive;
	}

	public void setReceive(receive receive) {
		this.receive = receive;
	}

	public order_detail getOrder_detail() {
		return order_detail;
	}

	public void setOrder_detail(order_detail order_detail) {
		this.order_detail = order_detail;
	}

	public int getProduct_type() {
		return product_type;
	}

	public void setProduct_type(int product_type) {
		this.product_type = product_type;
	}

	public String getUser_address() {
		return user_address;
	}

	public void setUser_address(String user_address) {
		this.user_address = user_address;
	}

	public String getUser_lng() {
		return user_lng;
	}

	public void setUser_lng(String user_lng) {
		this.user_lng = user_lng;
	}

	public String getUser_lat() {
		return user_lat;
	}

	public void setUser_lat(String user_lat) {
		this.user_lat = user_lat;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}

class shop {
	private String shop_name;
	private String shop_phone;
	private String shop_address;
	private String shop_lng;
	private String shop_lat;

	public String getShop_name() {
		return shop_name;
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	public String getShop_phone() {
		return shop_phone;
	}

	public void setShop_phone(String shop_phone) {
		this.shop_phone = shop_phone;
	}

	public String getShop_address() {
		return shop_address;
	}

	public void setShop_address(String shop_address) {
		this.shop_address = shop_address;
	}

	public String getShop_lng() {
		return shop_lng;
	}

	public void setShop_lng(String shop_lng) {
		this.shop_lng = shop_lng;
	}

	public String getShop_lat() {
		return shop_lat;
	}

	public void setShop_lat(String shop_lat) {
		this.shop_lat = shop_lat;
	}

}

class receive {
	private String user_name;
	private String user_phone;
	private String user_address;
	private String user_lng;
	private String user_lat;
	private String city_name;

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}

	public String getUser_address() {
		return user_address;
	}

	public void setUser_address(String user_address) {
		this.user_address = user_address;
	}

	public String getUser_lng() {
		return user_lng;
	}

	public void setUser_lng(String user_lng) {
		this.user_lng = user_lng;
	}

	public String getUser_lat() {
		return user_lat;
	}

	public void setUser_lat(String user_lat) {
		this.user_lat = user_lat;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
}

class order_detail {
	private int total_price;
	private int product_type;
	private int weight_gram;
	private int product_num;
	private int product_type_num;
	private List<product_detail> product_detail;

	public int getTotal_price() {
		return total_price;
	}

	public void setTotal_price(int total_price) {
		this.total_price = total_price;
	}

	public int getProduct_type() {
		return product_type;
	}

	public void setProduct_type(int product_type) {
		this.product_type = product_type;
	}

	public int getWeight_gram() {
		return weight_gram;
	}

	public void setWeight_gram(int weight_gram) {
		this.weight_gram = weight_gram;
	}

	public int getProduct_num() {
		return product_num;
	}

	public void setProduct_num(int product_num) {
		this.product_num = product_num;
	}

	public int getProduct_type_num() {
		return product_type_num;
	}

	public void setProduct_type_num(int product_type_num) {
		this.product_type_num = product_type_num;
	}

	public List<product_detail> getProduct_detail() {
		return product_detail;
	}

	public void setProduct_detail(List<product_detail> product_detail) {
		this.product_detail = product_detail;
	}

}

class product_detail {
	private String product_name;
	private int product_num;
	private int product_price;
	private String product_unit;

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public int getProduct_num() {
		return product_num;
	}

	public void setProduct_num(int product_num) {
		this.product_num = product_num;
	}

	public int getProduct_price() {
		return product_price;
	}

	public void setProduct_price(int product_price) {
		this.product_price = product_price;
	}

	public String getProduct_unit() {
		return product_unit;
	}

	public void setProduct_unit(String product_unit) {
		this.product_unit = product_unit;
	}

}
