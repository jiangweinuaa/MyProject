package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_FreightQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_FreightQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

/**
 * 根据经纬度 和 下单门店查配送费
 * @author yuanyy
 *
 */
public class DCP_FreightQuery_Open extends SPosBasicService<DCP_FreightQuery_OpenReq, DCP_FreightQuery_OpenRes> {

	@Override
	protected boolean isVerifyFail(DCP_FreightQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_FreightQuery_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FreightQuery_OpenReq>(){};
	}

	@Override
	protected DCP_FreightQuery_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_FreightQuery_OpenRes();
	}

	@Override
	protected DCP_FreightQuery_OpenRes processJson(DCP_FreightQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_FreightQuery_OpenRes res = null;
		res = this.getResponse();
		
		try {
			String eId = req.getRequest().getoEId();
			String baseSetNo = req.getRequest().getBaseSetNo();
			String shopId = req.getRequest().getShopId();
			String longitude = req.getRequest().getLongitude(); // 经度
			String latitude = req.getRequest().getLatitude(); //纬度
			String address = req.getRequest().getAddress(); //详细配送地址
			
			// 经纬度为空的时候，根据配送地址查询对应经纬度
			if(Check.Null(latitude) || Check.Null(longitude)  ){ 
				MyCommon mc = new MyCommon();
				String point = mc.getLngLat(address);
				
				if(!Check.Null(point)){
					latitude = point.substring(0, point.indexOf(","));
			    	// 截取逗号后的字符串
			    	longitude = point.substring(latitude.length()+1, point.length());
				}
				
				mc = null;
			}
			
			// 以经纬度为第一优先级， 没有经纬度就根据收货地址去调接口查
			String distanceSql = " SELECT a.organizationno  AS SHOPID ,  F_CRM_GetDistance( "+latitude+" , "+longitude+",a.latitude,a.longitude ) as distance "
					+ " FROM DCP_ORG a WHERE EID  = '"+eId+"' AND organizationNo = '"+shopId+"' ";
			
			List<Map<String, Object>> distanceDatas = this.doQueryData(distanceSql, null);
			String distance = null;
			if(distanceDatas != null && !distanceDatas.isEmpty()){
				distance = distanceDatas.get(0).get("DISTANCE").toString();
			} 
			
			if(Check.Null(distance)){ //如果获取门店经纬度和距离失败，给提示信息
				res.setSuccess(false);
				res.setServiceStatus("200");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取"+shopId+"和配送地址经纬度失败");
			}
			
			String baseSql = "";
			StringBuffer sqlbuf = new StringBuffer();
			sqlbuf.append("SELECT a.basesetno , a.freightway , a.freight  AS comFreight  , b.item , b.maxDistance , b.freight AS difFreight "
					+ " FROM Dcp_Takeout_Baseset a "
					+ " LEFT JOIN dcp_takeout_baseset_freight b ON a.EID = b.EID AND a.basesetno = b.basesetno "
					+ " WHERE a.EID = '"+eId+"'  AND  a.baseSetNo = '"+baseSetNo+"' "
					+ " order by a.freightway , b.item ");
			
			baseSql = sqlbuf.toString();
			List<Map<String, Object>> freightDatas = this.doQueryData(baseSql, null);
			
			res.setDatas(new ArrayList<DCP_FreightQuery_OpenRes.level1Elm>());
			if(freightDatas != null && !freightDatas.isEmpty()){
				
				DCP_FreightQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
				int num = freightDatas.size();
				Double[] dArr = new Double[num];
				
				String freightWay = freightDatas.get(0).get("FREIGHTWAY").toString();
				String freight = "0";
				if(freightWay.equals("0")){ //统一运费
					freight = freightDatas.get(0).get("COMFREIGHT").toString();
				}
				else{
					
					for (int i = 0; i < freightDatas.size(); i++) {
						
						String maxDistanceStr = freightDatas.get(i).get("MAXDISTANCE").toString();
						dArr[i] = new Double(Double.parseDouble(maxDistanceStr));
						
					}
					
					// 排序
					//冒泡
			        for (int i = 0; i < dArr.length; i++) {
			            //外层循环，遍历次数
			            for (int j = 0; j < dArr.length - i - 1; j++) {
			                //内层循环，升序（如果前一个值比后一个值大，则交换）
			                //内层循环一次，获取一个最大值
			                if (dArr[j] > dArr[j + 1]) {
			                    double temp = dArr[j + 1];
			                    dArr[j + 1] = dArr[j];
			                    dArr[j] = temp;
			                }
			            }
			        }
					
					double min = 0; //匹配的配送距离
					double distanceDou = Double.parseDouble(distance);
			        for (int i = 0; i < dArr.length; i++) {
						double d = dArr[i];
						
						if(d - distanceDou > 0){
							min = d ;
							break;
						}
					}
			        
			        for (Map<String, Object> map : freightDatas) {
			        	
			        	if(Check.Null(map.get("MAXDISTANCE").toString())){
			        		continue;
			        	}
			        	
			        	else{
				        	double id = Double.parseDouble(map.get("MAXDISTANCE").toString());
				        	
				        	if(id == min){
//				        		System.out.println("最小配送费："+ map.get("ID").toString() + "    "+ map.get("money").toString());
				        		freight = map.get("DIFFREIGHT").toString();
				        	}
			        	}
			        	
			        }
				}
				
				lv1.setFreight(freight);
				lv1.setLatitude(latitude);
				lv1.setLongitude(longitude);
				lv1.setAddress(address);
				lv1.setDistance(distance);
				res.getDatas().add(lv1);
			}
					
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_FreightQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
