package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECExpShippingQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECExpShippingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单导出Excel 查询
 * @author yuanyy 2019-03-19
 *
 */
public class DCP_OrderECExpShippingOldQuery extends SPosBasicService<DCP_OrderECExpShippingQueryReq, DCP_OrderECExpShippingQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_OrderECExpShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected TypeToken<DCP_OrderECExpShippingQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECExpShippingQueryReq>(){};
	}

	@Override
	protected DCP_OrderECExpShippingQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECExpShippingQueryRes();
	}

	@Override
	protected DCP_OrderECExpShippingQueryRes processJson(DCP_OrderECExpShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECExpShippingQueryRes res = null;
		res = this.getResponse();
		String eId = req.geteId();
		String ecPlatformNo = req.getEcPlatformNo();
		String exportDoc = req.getExportDoc(); //是否导出， Y已导出，N未导出 ， 空代表全部状态
		
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {};
			List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);
			if(!getDatas.isEmpty()){
				
				String tableName = null; 
				//过滤出表明，
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
				condition2.put("TABLENAME", true);	
				List<Map<String, Object>> getTable=MapDistinct.getMap(getDatas, condition2);

				ArrayList<String> columnsFName = new ArrayList<String>();
				String fieldNameStr = ""; // fieldNameStr 用于记录每个表的列名, 插入到查询语句中
				
				for(Map<String, Object> tableDatas : getTable){
					tableName = tableDatas.get("TABLENAME").toString();
					for (Map<String, Object> oneData : getDatas) 
					{
						if(tableName.equals(oneData.get("TABLENAME").toString()) ){
							String fieldName = oneData.get("FIELDNAME").toString();
							//定义查询字段名： 给每个字段都取 别名 ： tableName$fieldName 的形式
							fieldNameStr += ","+tableName+"."+fieldName+ " as "+ tableName+"$" +fieldName;
							//定义返回参数名: 用 tableName$fieldName 组合的形式作返回参数名
							String excelColumn = tableName+"$"+fieldName;
							columnsFName.add(excelColumn);
						}
					}
				}
				
				sql = this.getExcelDatas(exportDoc,eId, ecPlatformNo, fieldNameStr);
				List<Map<String , Object>> getDetailDatas = this.doQueryData(sql, null);
				
				if(!getDetailDatas.isEmpty()){
					
					ArrayList<String> shipmentNo = new ArrayList<String>();
					
					for (int i = 0; i < columnsFName.size(); i++) {
						String excelColumnName = columnsFName.get(i).toString();
						
						Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
						condition.put("SHIPMENTNO", true);// 这里有疑问， 是按照货运单号还是按照订单号来过滤
						//调用过滤函数
						List<Map<String, Object>> getQHeader=MapDistinct.getMap(getDetailDatas, condition);
						for(Map<String, Object> getMetaDatas : getQHeader){
							String evShipmentNo = getMetaDatas.get("SHIPMENTNO").toString();
							shipmentNo.add(evShipmentNo);
							
							Map<String, Object> columnDatas = new HashMap<String, Object>();
							// 列中必须有item 来标识行号， 前端根据item 填充excel
							String excelColumnValue = getMetaDatas.get(excelColumnName).toString();
							columnDatas.put(excelColumnName, excelColumnValue);
						}
						
					}
					
					String[] sids = shipmentNo.toArray(new String[shipmentNo.size()]);    
					res.setShipmentNo(sids);
					
				}
				else{
				}
				
			}
			else{ //当查询导出格式列为空的时候
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("当前选择的导出格式存在异常！");
			}
			
		}
		catch (Exception e) {
		
			
		}
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderECExpShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String expOrderFormatNo = req.getExpOrderFormatNo(); // 导出格式编码
		String ecPlatformNo = req.getEcPlatformNo(); //电商平台编码
		
		sqlbuf.append(" SELECT a.expformatno , a. ecplatformno , b.item , b.columnno, b.columnName , b.tablename , b.fieldname "
			+ " FROM OC_ecexporderFormat a  "
			+ " LEFT JOIN OC_ecexporderFormat_detail b ON a.EID = b.EID AND a.EXPFORMATNO = b.EXPFORMATNO "
			+ " WHERE a.EID = '"+eId+"' AND a.EXPFORMATNO = '"+expOrderFormatNo+"' "
			);
		
		if (ecPlatformNo != null && ecPlatformNo.length()!=0) { 	
			sqlbuf.append( " AND  a.ecPlatformNo = '"+ecPlatformNo+"' ");
		}
		
		sqlbuf.append(" order by a.expformatno , b.item , b.columnNo , b.tableName , b.fieldName "	);
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	/**
	 * 查找每一列的数据
	 * @param eId
	 * @param ecPlatformNo
	 * @param fieldNameStr
	 * @return
	 */
	private String getExcelDatas(String exportDoc, String eId, String ecPlatformNo, String fieldNameStr){
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		// 下面的sql 要查询的列是动态变化的，根据导出格式的中的列变化
		sqlbuf.append(" SELECT a.shipmentNo , a.EXPORTDOC , a.ECPLATFORMNO ,  a.ORIGINALTYPE , a.ORIGINALNO "+fieldNameStr+"  FROM DCP_shipment  a "
			+ " LEFT JOIN OC_Order OC_Order  ON a.EID = OC_ORDER.EID AND a.SHOPID = OC_ORDER.SHOPID  AND a.originalno = OC_ORDER.orderNo "
			+ " LEFT JOIN OC_ORDER_DETAIL OC_ORDER_DETAIL on OC_ORDER.EID = OC_ORDER_DETAIL.EID and OC_ORDER.SHOPID = OC_ORDER_DETAIL.SHOPID and OC_ORDER.orderNo = OC_ORDER_DETAIL.orderNO "
			+ " where a.EID = '"+eId+"' and a.ecPlatformNo = '"+ecPlatformNo+"' "
			+ " and a.exportDoc = '"+exportDoc+"' "
			+ " order by a.shipmentNO, OC_ORDER.orderNO , OC_ORDER_DETAIL.item "
			);
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	

}
