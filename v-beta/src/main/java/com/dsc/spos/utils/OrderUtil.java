package com.dsc.spos.utils;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderUtil 
{
	
	static Logger logger = LogManager.getLogger(OrderUtil.class.getName());
	//计算精纬度的距离
	private static final double EARTH_RADIUS = 6378.137;//地球半径,单位千米
  private static double rad(double d)
  {
      return d * Math.PI / 180.0;
  }
  
  /**
   * 
   * @param lat1 第一个纬度
   * @param lng1第一个经度
   * @param lat2第二个纬度
   * @param lng2第二个经度
   * @return 两个经纬度的距离
   */
  public static double getDistance(double lat1,double lng1,double lat2,double lng2)
  {
      double radLat1 = rad(lat1);
      double radLat2 = rad(lat2);
      double a = radLat1 - radLat2;
      double b = rad(lng1) - rad(lng2);
      
      double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
              Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
      s = s * EARTH_RADIUS;
      s = Math.round(s * 10000) / 10000;
      return s;
  }
  
  
  public static void main(String[] args) throws IOException, JSONException, URISyntaxException
  {
  	String address="苏州市工业园区金鸡湖大道1355号国际科技园二期D204";
  	address=URLEncoder.encode(address, "utf-8" ); 
  	
  	String url="https://restapi.amap.com/v3/geocode/geo?address="+address+"&output=JSON&key=66b4b07702ff29ecc1ee9fbbf6980675";
  	//String aa= OrderUtil.Sendcom("url",url,"GET");
      //System.out.println(OrderUtil.getDistance(22.75424,112.76535 , 23.014171, 113.10111));
      
    double[] baidulist= gaoDeToBaidu(120.670738, 31.293985);
    long aa= System.currentTimeMillis();  
    
  }
  
  //取得高德地图的精纬度，调用高德地图的公用方法
  //json转实体类http://www.bejson.com/
  /**
	 * 直接发送HTTP请求
   * @throws URISyntaxException 
	 */
	public static String Sendcom(String reques,String surl,String method) throws IOException, URISyntaxException 
	{
		logger.info("\r\n******直接通过传入的网址调用服务"+":请求公用调用Start******\r\n");
		
		String res="";
		
		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
		RequestConfig.Builder requestConfig = RequestConfig.custom();
		
		//连接超时时间
		requestConfig.setConnectTimeout(5000);
		//从连接池中取连接的超时时间
		requestConfig.setConnectionRequestTimeout(5000);							
		//连接建立后，请求超时时间
		requestConfig.setSocketTimeout(60000);
		
		
		httpBuilder.setDefaultRequestConfig(requestConfig.build());
		httpBuilder.disableAutomaticRetries();//禁止超时重试	
		httpBuilder.disableConnectionState();							
		httpBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0,false));//重试0次

		// 创建httpclient对象
		CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();
		
		// 创建post方式请求对象
		HttpUriRequest httpPost = null; 
		//URI ul=new URI(surl);
		
		if(method.equals("POST"))
		{
		  httpPost=new HttpPost(surl);
		}
		else
		{
			httpPost=new HttpGet(surl);
			//httpPost=new HttpGet(ul);
		}
		
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");

		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
		String timestamp = dfDate.format(cal.getTime()) + dfTime.format(cal.getTime());
		
		//httpPost.setParams(params);
		
		CloseableHttpResponse response = null;
		
		HttpEntity entity=null;
		
		// 执行请求操作，并拿到结果（同步阻塞）
		try 
		{
			response = client.execute(httpPost);
			// 获取结果实体
			entity = response.getEntity();

			if (entity != null) 
			{
				// 按指定编码转换结果实体为String类型
				res = EntityUtils.toString(entity, "utf-8");
				
				logger.info("\r\n******公用http服务"+"请求返回：" + res + "******\r\n");
			}
			else
			{
				logger.info("\r\n返回结果entity为NULL******\r\n");
			}
		}
		catch (Exception e) 
		{
			//System.out.println(e.toString());
			
			logger.error("\r\n******公用请求服务" +":请求报错" +e.toString()+"******\r\n");
		}
		finally 
		{
			if (entity != null) 
			{
				EntityUtils.consume(entity);
			}															

			if (response != null) 
			{
				response.close();
			}								

			if (httpPost != null) 
			{
				httpPost.abort();
				//httpPost.releaseConnection();
			}	

			if (client != null) 
			{
				client.close();
			}	
			logger.info("\r\n******公用请求服务"+":请求调用End******\r\n");
		}
		return res;
		
	}	
	

	//高德转百度坐标
  public static double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
    double[] bd_lat_lon = new double[2];
    double PI = 3.14159265358979324 * 3000.0 / 180.0;
    double x = gd_lon, y = gd_lat;
    double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
    double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
    bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
    bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
    return bd_lat_lon;
  }
	
  //百度转高德坐标
  public static double[] bdToGaoDe(double bd_lat, double bd_lon) {
    double[] gd_lat_lon = new double[2];
    double PI = 3.14159265358979324 * 3000.0 / 180.0;
    double x = bd_lon - 0.0065, y = bd_lat - 0.006;
    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
    gd_lat_lon[0] = z * Math.cos(theta);
    gd_lat_lon[1] = z * Math.sin(theta);
    return gd_lat_lon;
 }
  

/**
 * 判断点是否在多边形内
 * @param point 检测点
 * @param pts   多边形的顶点
 * @return      点在多边形内返回true,否则返回false
 */
public static boolean IsPtInPoly(Point2D.Double point, List<Point2D.Double> pts){
    
    int N = pts.size();
    boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
    int intersectCount = 0;//cross points count of x 
    double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
    Point2D.Double p1, p2;//neighbour bound vertices
    Point2D.Double p = point; //当前点
    
    p1 = pts.get(0);//left vertex        
    for(int i = 1; i <= N; ++i){//check all rays            
        if(p.equals(p1)){
            return boundOrVertex;//p is an vertex
        }
        
        p2 = pts.get(i % N);//right vertex            
        if(p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)){//ray is outside of our interests                
            p1 = p2; 
            continue;//next ray left point
        }
        
        if(p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)){//ray is crossing over by the algorithm (common part of)
            if(p.y <= Math.max(p1.y, p2.y)){//x is before of ray                    
                if(p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)){//overlies on a horizontal ray
                    return boundOrVertex;
                }
                
                if(p1.y == p2.y){//ray is vertical                        
                    if(p1.y == p.y){//overlies on a vertical ray
                        return boundOrVertex;
                    }else{//before ray
                        ++intersectCount;
                    } 
                }else{//cross point on the left side                        
                    double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;//cross point of y                        
                    if(Math.abs(p.y - xinters) < precision){//overlies on a ray
                        return boundOrVertex;
                    }
                    
                    if(p.y < xinters){//before ray
                        ++intersectCount;
                    } 
                }
            }
        }else{//special case when ray is crossing through the vertex                
            if(p.x == p2.x && p.y <= p2.y){//p crossing over p2                    
                Point2D.Double p3 = pts.get((i+1) % N); //next vertex                    
                if(p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)){//p.x lies between p1.x & p3.x
                    ++intersectCount;
                }else{
                    intersectCount += 2;
                }
            }
        }            
        p1 = p2;//next ray left point
    }
    
    if(intersectCount % 2 == 0){//偶数在多边形外
        return false;
    } else { //奇数在多边形内
        return true;
    }
    
}

public static boolean isinside1(double px , double py,List<Double> listlon,List<Double> listlat)
{
	Point2D.Double point = new Point2D.Double(px, py);
	List<Point2D.Double> pts = new ArrayList<Point2D.Double>();
	for (int i = 0; i < listlon.size(); i++) 
	{
		pts.add(new Point2D.Double(listlon.get(i), listlat.get(i)));
	}
	return IsPtInPoly(point, pts);
	
}

//测试一个点是否在多边形内
public static void main1(String[] args) {
 
 Point2D.Double point = new Point2D.Double(116.404072, 39.916605);
 
 List<Point2D.Double> pts = new ArrayList<Point2D.Double>();
 pts.add(new Point2D.Double(116.395, 39.910));
 pts.add(new Point2D.Double(116.394, 39.914));
 pts.add(new Point2D.Double(116.403, 39.920));
 pts.add(new Point2D.Double(116.402, 39.914));
 pts.add(new Point2D.Double(116.410, 39.913));
 
 if(IsPtInPoly(point, pts)){
     //System.out.println("点在多边形内");
 }else{
     //System.out.println("点在多边形外");
 }
}


  
//第一种计算距离的方式  https://blog.csdn.net/superdog007/article/details/53404270


/**
 * 判断该地理坐标是否在最大范围区域内
 * 
 * @param pointLon
 *            要判断的点的纵坐标
 * @param pointLat
 *            要判断的点的横坐标
 * @param lon
 *            指定区域的纵坐标组成的数组
 * @param lat
 *            指定区域的横坐标组成的数组
 * @return
 */
private static boolean isInMaxArea(double pointLon, double pointLat, Double[] lon,
		Double[] lat) {

// 获取区域横纵坐标最大值和最小值
double temp = 0.0;
for (int i = 0; i < lon.length; i++) {
    for (int j = 0; j < lon.length - i - 1; j++) {
        if (lon[j] > lon[j + 1]) {
            temp = lon[j];
            lon[j] = lon[j + 1];
            lon[j + 1] = temp;
        }
    }
}
for (int i = 0; i < lat.length; i++) {
    for (int j = 0; j < lat.length - i - 1; j++) {
        if (lat[j] > lat[j + 1]) {
            temp = lat[j];
            lat[j] = lat[j + 1];
            lat[j + 1] = temp;
        }
    }
}

// 如果在最值组成的区域外，那肯定不在重点区域内
if (pointLon < lon[0] || pointLon > lon[lon.length - 1] || pointLat < lat[0]
        || pointLat > lat[lat.length - 1]) {
    return false;
} else {
    return true;
}
}

/**
 * 判断坐标是否在重点区域内
 * 
 * @param pointLon
 *            要判断的点的纵坐标
 * @param pointLat
 *            要判断的点的横坐标
 * @param lon
 *            指定区域的纵坐标组成的数组
 * @param lat
 *            指定区域的横坐标组成的数组
 * @return
 */
private static boolean isInAccurateArea(double pointLon, double pointLat, Double[] lon,
		Double[] lat) {
// 代表有几个点
int vertexNum = lon.length;
boolean result = false;

for (int i = 0, j = vertexNum - 1; i < vertexNum; j = i++) {
    // 满足条件，与多边形相交一次，result布尔值取反一次，奇数个则在区域内
    if ((lon[i] > pointLon) != (lon[j] > pointLon)
            && (pointLat < (lat[j] - lat[i]) * (pointLon - lon[i]) / (lon[j] - lon[i])
                    + lat[i])) {
        result = !result;
    }
}
return result;
}

public static boolean isinside2(double px , double py,List<Double> listlon,List<Double> listlat)
{
	Double[] lon=new Double[listlon.size()];
	listlon.toArray(lon);
	Double[] lat=new Double[listlat.size()];
	listlat.toArray(lat);
	boolean inside= isInMaxArea(px,py,lon,lat);
	if(inside==true)
	{
		return isInAccurateArea(px, py, lon,lat);
	}
	else
	{
		return false;
	}
	
}


//第二种计算距离的方式  https://blog.csdn.net/u012898245/article/details/79450433
public static boolean isinside3(double px , double py,List<Double> listlon,List<Double> listlat)
{
	return GraphicalMain.isPointInPolygon(px, py, listlon,listlat );
}
  
}


//第三种计算距离的方式  https://blog.csdn.net/u013239236/article/details/52213661
class Point {
	/**
	 *  是否有 横断<br/>
	 *  参数为四个点的坐标
	 * @param px1
	 * @param py1
	 * @param px2
	 * @param py2
	 * @param px3
	 * @param py3
	 * @param px4
	 * @param py4
	 * @return  
	 */
	public boolean isIntersect ( double px1 , double py1 , double px2 , double py2 , double px3 , double py3 , double px4 ,  
			double py4 )  
	{  
		boolean flag = false;  
		double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);  
		if ( d != 0 )  
		{  
			double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3)) / d;  
			double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1)) / d;  
			if ( (r >= 0) && (r <= 1) && (s >= 0) && (s <= 1) )  
			{  
				flag = true;  
			}  
		}  
		return flag;  
	} 
	/**
	 *  目标点是否在目标边上边上<br/>
	 *  
	 * @param px0 目标点的经度坐标
	 * @param py0 目标点的纬度坐标
	 * @param px1 目标线的起点(终点)经度坐标
	 * @param py1 目标线的起点(终点)纬度坐标
	 * @param px2 目标线的终点(起点)经度坐标
	 * @param py2 目标线的终点(起点)纬度坐标
	 * @return
	 */
	public boolean isPointOnLine ( double px0 , double py0 , double px1 , double py1 , double px2 , double py2 )  
	{  
		boolean flag = false;  
		double ESP = 1e-9;//无限小的正数
		if ( (Math.abs(Multiply(px0, py0, px1, py1, px2, py2)) < ESP) && ((px0 - px1) * (px0 - px2) <= 0)  
				&& ((py0 - py1) * (py0 - py2) <= 0) )  
		{  
			flag = true;  
		}  
		return flag;  
	} 
	public double Multiply ( double px0 , double py0 , double px1 , double py1 , double px2 , double py2 )  
	{  
		return ((px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0));  
	}
	/**
	 * 判断目标点是否在多边形内(由多个点组成)<br/>
	 * 
	 * @param px 目标点的经度坐标
	 * @param py 目标点的纬度坐标
	 * @param polygonXA 多边形的经度坐标集合
	 * @param polygonYA 多边形的纬度坐标集合
	 * @return
	 */
	public boolean isPointInPolygon ( double px , double py , ArrayList<Double> polygonXA , ArrayList<Double> polygonYA )  
	{  
		boolean isInside = false;  
		double ESP = 1e-9;  
		int count = 0;  
		double linePoint1x;  
		double linePoint1y;  
		double linePoint2x = 180;  
		double linePoint2y;  
 
		linePoint1x = px;  
		linePoint1y = py;  
		linePoint2y = py;  
 
		for (int i = 0; i < polygonXA.size() - 1; i++)  
		{  
			double cx1 = polygonXA.get(i);  
			double cy1 = polygonYA.get(i);  
			double cx2 = polygonXA.get(i + 1);  
			double cy2 = polygonYA.get(i + 1); 
			//如果目标点在任何一条线上
			if ( isPointOnLine(px, py, cx1, cy1, cx2, cy2) )  
			{  
				return true;  
			}
			//如果线段的长度无限小(趋于零)那么这两点实际是重合的，不足以构成一条线段
			if ( Math.abs(cy2 - cy1) < ESP )  
			{  
				continue;  
			}  
			//第一个点是否在以目标点为基础衍生的平行纬度线
			if ( isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x, linePoint2y) )  
			{  
				//第二个点在第一个的下方,靠近赤道纬度为零(最小纬度)
				if ( cy1 > cy2 )  
					count++;  
			}
			//第二个点是否在以目标点为基础衍生的平行纬度线
			else if ( isPointOnLine(cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y) )  
			{  
				//第二个点在第一个的上方,靠近极点(南极或北极)纬度为90(最大纬度)
				if ( cy2 > cy1 )  
					count++;  
			}
			//由两点组成的线段是否和以目标点为基础衍生的平行纬度线相交
			else if ( isIntersect(cx1, cy1, cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y) )  
			{  
				count++;  
			}  
		}  
		if ( count % 2 == 1 )  
		{  
			isInside = true;  
		}  
 
		return isInside;  
	}  
}


class Area {
	
	public static Random rd=new Random();
	
	public Double createDouble(){
		return (double) rd.nextInt(1000);
	}
	public Area() {
		this.px = createDouble();
		this.py = createDouble();
	}
	
	public Area(Double px, Double py) {
		super();
		this.px = px;
		this.py = py;
	}
	
	public Area(Double px, Double py, String name) {
		super();
		this.px = px;
		this.py = py;
		this.name = name;
	}
	private Double px;
	private Double py;
	private String name;
	public Double getPx() {
		return px;
	}
	public void setPx(Double px) {
		this.px = px;
	}
	public Double getPy() {
		return py;
	}
	public void setPy(Double py) {
		this.py = py;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Area [px=" + px + ", py=" + py + ", name=" + name + "]";
	}
	
	public String getPoint() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("(").append(px).append(",").append(py).append(")");
		return buffer.toString();
	}	
}


class GraphicalMain {
	public static void main(String[] args) {
		//(113.944421,22.528841) (113.94629,22.529208)
		//isPointInPolygon(area.createDouble(),area.createDouble()); (114.082402,22.550271) 114.075323,22.543528)
		//isPointInPolygon(113.947871,22.52804);
		
	}
	public static Boolean isPointInPolygon( double px , double py,List<Double> listlon,List<Double> listlat ){
		
		List<Area> areas=new ArrayList<Area>();
		for(int i=0;i<listlon.size();i++)
		{
			Area a1=new Area(listlon.get(i),listlat.get(i));
			areas.add(a1);
		}
		
		ArrayList<Double> polygonXA = new ArrayList<Double>();  
		ArrayList<Double> polygonYA = new ArrayList<Double>(); 
		for(int i=0;i<areas.size();i++){
			Area area=areas.get(i);
			polygonXA.add(area.getPx());
			polygonYA.add(area.getPy());
		}
		Point point=new Point();
		Boolean flag= point.isPointInPolygon(px, py, polygonXA, polygonYA);
		StringBuffer buffer=new StringBuffer();
		buffer.append("目标点").append("(").append(px).append(",").append(py).append(")").append("\n");
		buffer.append(flag?"在":"不在").append("\t").append("由\n");
		for(int i=0;i<areas.size();i++){
			Area area=areas.get(i);
			buffer.append(area.getPoint()).append("; ");
			//buffer.append("第"+i+"个点"+area.getPoint()).append("\n");
			//System.out.println("第"+(i+1)+"个点"+area.getPoint());
		}
		StringBuffer sb=new StringBuffer();
		sb.append("目标点:").append("(").append(px).append(",").append(py).append(")").append("\n");
		//System.out.println(sb);
		buffer.append(areas.size()).append("个点组成的").append(areas.size()).append("边行内");
		//System.out.println(buffer.toString());
		return  flag;
	}
	
	
}





