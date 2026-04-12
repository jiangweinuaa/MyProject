package com.dsc.spos.thirdpart.qimai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.thirdpart.ThirdpartConstants;
import com.dsc.spos.waimai.SWaimaiBasicService;
import com.dsc.spos.waimai.entity.orderLoadDocType;

public class QiMaiService
{
	static String LogFileName = "QiMaiPost";//解析记录
	
	public static String langType="zh_CN";

	Logger logger = LogManager.getLogger(SWaimaiBasicService.class.getName());

	public QiMaiService() 
	{
	}
	
	public static QiMaiService getInstance() throws Exception {
		return new QiMaiService();
	}


//	
	public static void main(String[] args) {
		Map<String, Object> basicMap=new HashMap<String, Object>();
//		basicMap.put("grantCode", "1c1e66a18");
//		basicMap.put("openKey", "iIzIuVHhOMbmxQTJ4oPZvuQponfv7yIhxmv8T3dB4QscxKIhi2");
//		basicMap.put("openId", "ea57b5f81c96f8e0a1fb4c727f153014");
		basicMap.put("grantCode", "5b0798878c");
		basicMap.put("openKey", "jGaMscU7j8TyWGat7QNodfNREqqkVMjipXKsFOfc92HSZfm2GM");
		basicMap.put("openId", "ffa652ff99d3639e1bb68740ebd1d15f");
//		basicMap.put("grantCode", "ba67d4fa46");
//		basicMap.put("openKey", "LyvrkvkxRkG2R6aM55bXpPwjYAbkEXTbVnKwfDYvVHjNwNFAmx");
//		basicMap.put("openId", "d14c1559e87b747d577c834b275a4310");
		Map<String, Object> params=new HashMap<String, Object>();
//		params.put("multiMark", "1001");
//		params.put("status", "2");
//		params.put("multiMark", "1001");
//		params.put("orderNo", "24165387426358680569");
		params.put("orderNo", "48165364082473446858");
		params.put("action", "VERFIY");
//		params.put("status", "70");
//		params.put("updateTime", "2022-05-18 10:57:19");
		String serviceId="v3/baking/order/operationOrder";
		try {
			QiMaiUtils.getInstance().PostData(serviceId, basicMap, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, Object> getBasicMap(Map<String, Object> map) throws Exception {
		Map<String, Object> basicMap=null;
		StringBuffer sql=new StringBuffer("SELECT A.*"
				+ " FROM DCP_ECOMMERCE A "
				+ " WHERE A.LOADDOCTYPE='"+orderLoadDocType.QIMAI+"' "
				+ " ");
		if(map==null){
			map=new HashMap<String, Object>();
		}
		String eId=map.get("EID")==null?"":map.get("EID").toString().trim();
		if(eId!=null&&eId.length()>0){
			sql.append(" AND A.EID='" + eId + "' ");
		}
		String channelId=map.get("CHANNELID")==null?"":map.get("CHANNELID").toString().trim();
		if(channelId!=null&&channelId.length()>0){
			sql.append(" AND A.CHANNELID='" + channelId + "' ");
		}
		List<Map<String, Object>> basicList = null;
		try{
			basicList = StaticInfo.dao.executeQuerySQL(sql.toString(), null);
		}catch(Exception e){
			QiMaiUtils.getInstance().ErrorLog("[SQL执行异常]:\r\n"+sql.toString()+"\r\n错误详情:\r\n",e);
		}
		if(basicList==null||basicList.size()==0){
			QiMaiUtils.getInstance().ErrorLog("DCP_ECOMMERCE 资料未配置!",null);
		}else if(basicList.size()>1){
			QiMaiUtils.getInstance().ErrorLog("DCP_ECOMMERCE 资料配置资料异常,生效数量大于一笔!"+"\r\nSQL:"+sql,null);
		}else{
			basicMap=basicList.get(0);
		}
		return basicMap;
	}
	
	/**
	 * 订转销调用
	 */
	public JsonBasicRes OrderToSale(Map<String, Object> orderMap,Map<String, Object> otherMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		//1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
		String shipType=orderMap.get("SHIPTYPE")==null?"":orderMap.get("SHIPTYPE").toString().trim();
		if("3".equals(shipType)){
			//1:到店自提
			thisRes=selfFetchCodeApply(orderMap, otherMap);
		}
		else {
			thisRes.setSuccess(true);
			thisRes.setServiceDescription("非自提单无需对接");
		}
		return thisRes;
	}
	
	/**
	 * 配送
	 */
	public JsonBasicRes localDelivery(Map<String, Object> orderMap,Map<String, Object> otherMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		//订单发货
		String serviceId="v3/baking/order/sendOrder";
		Map<String, Object> params1 = new HashMap<String, Object>();
		String orderNo=orderMap.get("ORDERNO")==null?"":orderMap.get("ORDERNO").toString().trim();
		params1.put("orderNo", orderNo);//
		String memo=orderMap.get("MEMO")==null?"":orderMap.get("MEMO").toString().trim();
		params1.put("sellerRemarks", memo);//商家备注内容字段、商家备注时必传
		Map<String, Object> expressMap = new HashMap<String, Object>();
		
		String deliveryNo=otherMap.get("deliveryNo")==null?"":otherMap.get("deliveryNo").toString().trim();
		String deliveryType=otherMap.get("deliveryType")==null?"":otherMap.get("deliveryType").toString().trim();
		String docType=otherMap.get("docType")==null?"":otherMap.get("docType").toString().trim();
		if("ERP".equals(docType)){
			expressMap.put("expressNumber", deliveryNo);//物流单号
			expressMap.put("type", "3");//管易云
			expressMap.put("code", deliveryType);
		}else{
			QiMaiExpressData expressData=deliveryTypeToData(deliveryType);
			expressMap.put("expressNumber", deliveryNo);//物流单号
			expressMap.put("type", expressData.getType());
			expressMap.put("code", expressData.getCode());
			expressMap.put("name", expressData.getName());//
		}
		
		params1.put("expressInfoDtoRequest", expressMap);
		
		
		String resStr1=QiMaiUtils.getInstance().PostData(serviceId,orderMap, params1);
		
		QiMaiBasicRes res1=new QiMaiBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, QiMaiBasicRes.class);
		if(res1!=null){
			if("true".equalsIgnoreCase(res1.getStatus())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("操作成功");
				return thisRes;
			}else{
				if(res1.getMessage()!=null){
					thisRes.setServiceDescription(res1.getMessage());
//							String message="重复";
//							if(res1.getErrorMsg().indexOf(message)>0){
//								thisRes.setSuccess(true);
//								thisRes.setServiceDescription(res1.getErrorMsg());
//								return thisRes;
//							}else{
//								thisRes.setServiceDescription(res1.getErrorMsg());
//								return thisRes;
//							}
					return thisRes;
				}else{
					thisRes.setServiceDescription("操作失败");
					return thisRes;
				}
			}
		}else{
			thisRes.setServiceDescription("操作失败");
		}
		return thisRes;
	}
	
	public QiMaiExpressData deliveryTypeToData(String deliveryType)throws Exception{
		//企迈物流信息
		//物流公司ID。更新物流信息是，必传。
		//1=顺丰快递；2=圆通快递；3=韵达快递；4=中通快递；5=天天快递；
		//6=中国邮政；7=EMS；8=百世汇通；9=申通快递；10=德邦快递；
		//13=中铁快运；15=安能物流；16=优速快递；17=国通快递；19=京东物流；20=全一快递；
		//21=远成；22=信丰；23=速尔快递；24=品骏快递；25=龙邦速运；
		//26=极兔物流；27=特急送；28=宅急送；29=丹鸟物流；30=耀飞快递；
		//31=众邮快递
		QiMaiExpressData data=new QiMaiExpressData();
		//圆通
		if(ThirdpartConstants.yto_deliveryType.equals(deliveryType)){
			data.setCode("YTO");
//			data.setId("2");
			data.setName("圆通快递");
		}else{
			List<Map<String, Object>> basicList = null;
			String sql="SELECT * FROM DCP_FIXEDVALUE WHERE KEY='DELIVERYTYPE' AND STATUS='100'";
			Map<String,Object> deliveryMap=null;
			try{
				basicList = StaticInfo.dao.executeQuerySQL(sql, null);
				if(basicList!=null&&basicList.size()>0){
					deliveryMap=basicList.stream().filter(g->g.get("VALUEID").toString().equals(deliveryType)).findFirst().orElse(null);
				}
			}catch(Exception e){
				QiMaiUtils.getInstance().ErrorLog("[SQL执行异常]:\r\n"+sql.toString()+"\r\n错误详情:\r\n",e);
			}
			if(deliveryMap==null||deliveryMap.isEmpty()){
				data.setCode(deliveryType);
				data.setName("其他物流");
				QiMaiUtils.getInstance().ErrorLog("物流类型["+deliveryType+"]未对接,需调整QiMaiService代码或相关配置!\r\nsql:"+sql, null);
			}else{
				data.setCode(deliveryMap.get("VALUEID")==null?"":deliveryMap.get("VALUEID").toString());
				data.setName(deliveryMap.get("VALUENAME")==null?"":deliveryMap.get("VALUENAME").toString());
			}
		}
		return data;
	}
	
	/**
	 * 自提核销
	 */
	public JsonBasicRes selfFetchCodeApply(Map<String, Object> orderMap,Map<String, Object> otherMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		//订单操作
		String serviceId="v3/baking/order/operationOrder";
		Map<String, Object> params1 = new HashMap<String, Object>();
		String orderNo=orderMap.get("ORDERNO")==null?"":orderMap.get("ORDERNO").toString().trim();
		params1.put("orderNo", orderNo);//订单号
		params1.put("action", "VERFIY");//操作动作。CANCEL=取消；VERFIY=核销；RECEIVE=接单
		String resStr1=QiMaiUtils.getInstance().PostData(serviceId,orderMap, params1);
		
		QiMaiBasicRes res1=new QiMaiBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, QiMaiBasicRes.class);
		if(res1!=null){
			if("true".equalsIgnoreCase(res1.getStatus())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("操作成功");
				return thisRes;
			}else{
				if(res1.getMessage()!=null){
					thisRes.setServiceDescription(res1.getMessage());
//					String message="重复";
//					if(res1.getErrorMsg().indexOf(message)>0){
//						thisRes.setSuccess(true);
//						thisRes.setServiceDescription(res1.getErrorMsg());
//						return thisRes;
//					}else{
//						thisRes.setServiceDescription(res1.getErrorMsg());
//						return thisRes;
//					}
					return thisRes;
				}else{
					thisRes.setServiceDescription("操作失败");
					return thisRes;
				}
			}
		}else{
			thisRes.setServiceDescription("操作失败");
		}
		return thisRes;
	}

	/**
	 * 同步商品库存
	 * @param setMap
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public JsonBasicRes updateStock(Map<String, Object> setMap,Map<String, Object> paramMap)throws Exception{
		JsonBasicRes thisRes=new JsonBasicRes();
		thisRes.setSuccess(false);
		//订单操作
		String serviceId="v3/baking/goods/out/updateStock";
		String resStr1=QiMaiUtils.getInstance().PostData(serviceId,setMap, paramMap);

		QiMaiBasicRes res1=new QiMaiBasicRes();
		res1=com.alibaba.fastjson.JSON.parseObject(resStr1, QiMaiBasicRes.class);
		if(res1!=null){
			if("true".equalsIgnoreCase(res1.getStatus())){
				thisRes.setSuccess(true);
				thisRes.setServiceDescription("操作成功");
				return thisRes;
			}else{
				if(res1.getMessage()!=null){
					thisRes.setServiceDescription(res1.getMessage());
//					String message="重复";
//					if(res1.getErrorMsg().indexOf(message)>0){
//						thisRes.setSuccess(true);
//						thisRes.setServiceDescription(res1.getErrorMsg());
//						return thisRes;
//					}else{
//						thisRes.setServiceDescription(res1.getErrorMsg());
//						return thisRes;
//					}
					return thisRes;
				}else{
					thisRes.setServiceDescription("操作失败");
					return thisRes;
				}
			}
		}else{
			thisRes.setServiceDescription("操作失败");
		}
		return thisRes;
	}

}
