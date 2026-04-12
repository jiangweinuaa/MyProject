package com.dsc.spos.waimai.dianwoda;

import org.json.JSONArray;
import org.json.JSONObject;


public class DianwodaCityCodeService 
{
	public String cityDataJson="{\"data\":{\"cities\":[{\"city_name\":\"杭州市\",\"city_code\":\"330100\"},{\"city_name\":\"南京市\",\"city_code\":\"320100\"},{\"city_name\":\"上海市\",\"city_code\":\"310100\"},{\"city_name\":\"武汉市\",\"city_code\":\"420100\"},{\"city_name\":\"济南市\",\"city_code\":\"370100\"},{\"city_name\":\"郑州市\",\"city_code\":\"410100\"},{\"city_name\":\"长沙市\",\"city_code\":\"430100\"},{\"city_name\":\"成都市\",\"city_code\":\"510100\"},{\"city_name\":\"深圳市\",\"city_code\":\"440300\"},{\"city_name\":\"西安市\",\"city_code\":\"610100\"},{\"city_name\":\"重庆市\",\"city_code\":\"500100\"},{\"city_name\":\"北京市\",\"city_code\":\"110100\"},{\"city_name\":\"青岛市\",\"city_code\":\"370200\"},{\"city_name\":\"天津市\",\"city_code\":\"120100\"},{\"city_name\":\"厦门市\",\"city_code\":\"350200\"},{\"city_name\":\"温州市\",\"city_code\":\"330300\"},{\"city_name\":\"宁波市\",\"city_code\":\"330200\"},{\"city_name\":\"福州市\",\"city_code\":\"350100\"},{\"city_name\":\"广州市\",\"city_code\":\"440100\"},{\"city_name\":\"苏州市\",\"city_code\":\"320500\"},{\"city_name\":\"无锡市\",\"city_code\":\"320200\"},{\"city_name\":\"常州市\",\"city_code\":\"320400\"},{\"city_name\":\"东莞市\",\"city_code\":\"441900\"},{\"city_name\":\"佛山市\",\"city_code\":\"440600\"},{\"city_name\":\"合肥市\",\"city_code\":\"340100\"},{\"city_name\":\"临沂市\",\"city_code\":\"371300\"},{\"city_name\":\"绍兴市\",\"city_code\":\"330600\"},{\"city_name\":\"石家庄市\",\"city_code\":\"130100\"},{\"city_name\":\"金华市\",\"city_code\":\"330700\"},{\"city_name\":\"汕头市\",\"city_code\":\"440500\"},{\"city_name\":\"九江市\",\"city_code\":\"360400\"},{\"city_name\":\"珠海市\",\"city_code\":\"440400\"},{\"city_name\":\"南昌市\",\"city_code\":\"360100\"},{\"city_name\":\"昆明市\",\"city_code\":\"530100\"},{\"city_name\":\"南通市\",\"city_code\":\"320600\"},{\"city_name\":\"芜湖市\",\"city_code\":\"340200\"},{\"city_name\":\"南宁市\",\"city_code\":\"450100\"},{\"city_name\":\"长春市\",\"city_code\":\"220100\"},{\"city_name\":\"扬州市\",\"city_code\":\"321000\"},{\"city_name\":\"嘉兴市\",\"city_code\":\"330400\"},{\"city_name\":\"湖州市\",\"city_code\":\"330500\"},{\"city_name\":\"大连市\",\"city_code\":\"210200\"},{\"city_name\":\"沈阳市\",\"city_code\":\"210100\"},{\"city_name\":\"乌鲁木齐市\",\"city_code\":\"650100\"},{\"city_name\":\"徐州市\",\"city_code\":\"320300\"},{\"city_name\":\"盐城市\",\"city_code\":\"320900\"},{\"city_name\":\"镇江市\",\"city_code\":\"321100\"},{\"city_name\":\"烟台市\",\"city_code\":\"370600\"},{\"city_name\":\"威海市\",\"city_code\":\"371000\"},{\"city_name\":\"泉州市\",\"city_code\":\"350500\"},{\"city_name\":\"惠州市\",\"city_code\":\"441300\"},{\"city_name\":\"台州市\",\"city_code\":\"331000\"},{\"city_name\":\"潍坊市\",\"city_code\":\"370700\"},{\"city_name\":\"淄博市\",\"city_code\":\"370300\"},{\"city_name\":\"蚌埠市\",\"city_code\":\"340300\"},{\"city_name\":\"兰州市\",\"city_code\":\"620100\"},{\"city_name\":\"太原市\",\"city_code\":\"140100\"},{\"city_name\":\"绵阳市\",\"city_code\":\"510700\"},{\"city_name\":\"贵阳市\",\"city_code\":\"520100\"},{\"city_name\":\"海口市\",\"city_code\":\"460100\"},{\"city_name\":\"呼和浩特市\",\"city_code\":\"150100\"},{\"city_name\":\"香港\",\"city_code\":\"810000\"},{\"city_name\":\"桂林市\",\"city_code\":\"450300\"},{\"city_name\":\"柳州市\",\"city_code\":\"450200\"},{\"city_name\":\"玉林市\",\"city_code\":\"450900\"},{\"city_name\":\"遵义市\",\"city_code\":\"520300\"},{\"city_name\":\"三亚市\",\"city_code\":\"460200\"},{\"city_name\":\"大庆市\",\"city_code\":\"230600\"},{\"city_name\":\"哈尔滨市\",\"city_code\":\"230100\"},{\"city_name\":\"黑河市\",\"city_code\":\"231100\"},{\"city_name\":\"佳木斯市\",\"city_code\":\"230800\"},{\"city_name\":\"齐齐哈尔市\",\"city_code\":\"230200\"},{\"city_name\":\"双鸭山市\",\"city_code\":\"230500\"},{\"city_name\":\"娄底市\",\"city_code\":\"431300\"},{\"city_name\":\"邵阳市\",\"city_code\":\"430500\"},{\"city_name\":\"张家界市\",\"city_code\":\"430800\"},{\"city_name\":\"株洲市\",\"city_code\":\"430200\"},{\"city_name\":\"吉林市\",\"city_code\":\"220200\"},{\"city_name\":\"丹东市\",\"city_code\":\"210600\"},{\"city_name\":\"锦州市\",\"city_code\":\"210700\"},{\"city_name\":\"辽阳市\",\"city_code\":\"211000\"},{\"city_name\":\"营口市\",\"city_code\":\"210800\"},{\"city_name\":\"安庆市\",\"city_code\":\"340800\"},{\"city_name\":\"马鞍山市\",\"city_code\":\"340500\"},{\"city_name\":\"莆田市\",\"city_code\":\"350300\"},{\"city_name\":\"潮州市\",\"city_code\":\"445100\"},{\"city_name\":\"湛江市\",\"city_code\":\"440800\"},{\"city_name\":\"开封市\",\"city_code\":\"410200\"},{\"city_name\":\"洛阳市\",\"city_code\":\"410300\"},{\"city_name\":\"滨州市\",\"city_code\":\"371600\"},{\"city_name\":\"德州市\",\"city_code\":\"371400\"},{\"city_name\":\"日照市\",\"city_code\":\"371100\"},{\"city_name\":\"枣庄市\",\"city_code\":\"370400\"},{\"city_name\":\"忻州市\",\"city_code\":\"140900\"},{\"city_name\":\"漳州市\",\"city_code\":\"350600\"},{\"city_name\":\"中山市\",\"city_code\":\"442000\"},{\"city_name\":\"默认\",\"city_code\":\"000000\"}]},\"code\":\"success\",\"message\":\"成功\"}";

	public String getCityCodeByCityName(String cityName)
	{
		String cityCode = "530100";//默认昆明市 客户目前就在昆明市
		if(cityName==null||cityName.trim().isEmpty())
		{
			return cityCode;
		}
		
		if(cityName.contains("市")==false&&cityName.equals("香港")==false)
		{
			cityName=cityName+"市";
		}
		
		try 
		{
			JSONObject json_data = new JSONObject(cityDataJson);
			JSONArray json_city = json_data.getJSONArray("cities");
			for(int i=0;i<json_city.length();i++)
			{
				JSONObject item = json_city.getJSONObject(i);
				if(item.get("city_name").toString().equals(cityName))
				{
					cityCode = item.get("city_code").toString();
					break;					
				}
						
			}
		
			return cityCode;
			
	
		} 
		catch (Exception e) 
		{
		
			return cityCode;
		}
		
	}
}
