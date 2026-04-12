package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ServiceChargeQueryReq;
import com.dsc.spos.json.cust.res.DCP_ServiceChargeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_ServiceChargeQuery extends SPosBasicService<DCP_ServiceChargeQueryReq, DCP_ServiceChargeQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ServiceChargeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ServiceChargeQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ServiceChargeQueryReq>(){};
	}

	@Override
	protected DCP_ServiceChargeQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ServiceChargeQueryRes();
	}

	@Override
	protected DCP_ServiceChargeQueryRes processJson(DCP_ServiceChargeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ServiceChargeQueryRes res = null; 
		res = this.getResponse();
		
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {};
			
			List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);
			
			if(getDatas.size() > 0){
				
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("SERVICECHARGENO", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getDatas, condition);
				
				res.setDatas(new ArrayList<DCP_ServiceChargeQueryRes.level1Elm>());
				
				String num = getDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
				
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_ServiceChargeQueryRes.level1Elm lev1 = res.new level1Elm();
					lev1.setDatas(new ArrayList<DCP_ServiceChargeQueryRes.level2Elm>());
					String serviceChargeNO = oneData.get("SERVICECHARGENO").toString();
					String scType = oneData.get("SCTYPE").toString();
					String limitShop = oneData.get("LIMITSHOP").toString();
					
					String createDate = oneData.get("CREATEDATE").toString();
					String createTime = oneData.get("CREATETIME").toString();
					String createBy = oneData.get("CREATEBY").toString();
					String createByName = oneData.get("CREATEBYNAME").toString();
					String modifyDate = oneData.get("MODIFYDATE").toString();
					String modifyTime = oneData.get("MODIFYTIME").toString();
					String modifyBy = oneData.get("MODIFYBY").toString();
					String modifyByName = oneData.get("MODIFYBYNAME").toString();
					String status = oneData.get("STATUS").toString();
					String updateTime = oneData.get("UPDATETIME").toString();
					
					lev1.setServiceChargeNo(serviceChargeNO);
					lev1.setScType(scType);
					lev1.setLimitShop(limitShop);
					lev1.setCreateDate(createDate);
					lev1.setCreateTime(createTime);
					lev1.setCreateBy(createBy);
					lev1.setCreateByName(createByName);
					lev1.setModifyDate(modifyDate);
					lev1.setModifyTime(modifyTime); 
					lev1.setModifyBy(modifyBy);
					lev1.setModifyByName(modifyByName);
					lev1.setStatus(status);
					lev1.setUpdateTime(updateTime);
					
					
					for (Map<String, Object> oneData2 : getDatas) 
					{
						//过滤属于此单头的明细
						if(serviceChargeNO.equals(oneData2.get("SERVICECHARGENO")))
						{	
							DCP_ServiceChargeQueryRes.level2Elm lev2 = res.new level2Elm();
						
							///// 这里有两种类型，  品号类别  和  品号 ，暂时先不查询名称
							String spno = oneData2.get("SPNO").toString();
							String spName = oneData2.get("SPNAME").toString();
							String scrate = oneData2.get("SCRATE").toString();
							lev2.setSpNo(spno);
							lev2.setSpName(spName);
							lev2.setScrate(scrate);
							
							lev1.getDatas().add(lev2);
						}
					}
					
					res.getDatas().add(lev1);
				}
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
	protected String getQuerySql(DCP_ServiceChargeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String eId = req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		String langType = req.getLangType();
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append( "SELECT num,rn , serviceChargeNO ,scType , limitShop , status , "
			+ " createBy , createDate , createTime , modifyBy , modifyDate , modifyTime , createByName, modifyByName ,"
			+ " updateTime , spno ,spname, scrate FROM ( "
			+ " SELECT "
			+ " COUNT(DISTINCT a.serviceChargeNO ) OVER() NUM ,dense_rank() over(ORDER BY a.serviceChargeNO) rn , "
			+ " a.serviceChargeNO , a.scType ,  a.limit_shop as limitShop , a.status , "
			+ " a.createBy, a.create_date AS createDate , a.create_Time  AS createTime ,d.op_name AS createByName , "
			+ " a.modifyBy , a.modify_Date AS modifyDate , a.modify_Time AS modifyTime ,e.op_name AS modifyByName ,  "
			+ " a.Update_Time AS updateTime ,"
			+ " case when a.sctype='1' THEN g.category_Name ELSE f.plu_name  end spName ,   "
			+ " b. spno , b.scrate "
			+ " FROM DCP_SERVICECHARGE a  "
			+ " LEFT JOIN DCP_SERVICECHARGE_detail b ON a.EID = b.EID AND a.serviceChargeNO = b.servicechargeNO "
			
			+ " LEFT JOIN platform_staffs_lang d ON a.EID = d.EID AND a.createby = d.opNO AND d.lang_Type  = '"+langType+"'"
			+ " LEFT JOIN platform_staffs_lang e ON a.EID = e.EID AND a.modifyby = e.opNO  AND e.lang_Type  = '"+langType+"'"
			+ " LEFT JOIN DCP_GOODS_lang f ON b.EID = f.EID AND b.spno = f.pluNo AND b.scType = '2'  "
			+ " AND f.lang_Type  =  '"+langType+"'"
		    + " LEFT JOIN DCP_CATEGORY_lang g ON b.EID = g.EID AND b.spno = g.category AND b.sctype = '1'"
			+ " AND g.lang_Type  =  '"+langType+"'"
			+ " WHERE a.EID = '"+eId+"'  "
			+ " " );
		
		if (keyTxt != null && keyTxt.length()!=0) { 	
			sqlbuf.append( " AND a.serviceChargeNO LIKE '%%"+keyTxt+"%%'  ");
		}
		if (status != null && status.length()!=0) { 	
			sqlbuf.append( " AND a.status = '"+status+"'  ");
		}
		
		sqlbuf.append( " )  "
				+  " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by serviceChargeNO ");
		sql = sqlbuf.toString();
		return sql;
	}

}
