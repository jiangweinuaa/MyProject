package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsFlavorQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsFlavorQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 统一加料信息查询
 * @author yuanyy
 *
 */
public class DCP_GoodsFlavorQuery extends SPosBasicService<DCP_GoodsFlavorQueryReq, DCP_GoodsFlavorQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_GoodsFlavorQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsFlavorQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsFlavorQueryReq>(){};
	}

	@Override
	protected DCP_GoodsFlavorQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsFlavorQueryRes();
	}

	@Override
	protected DCP_GoodsFlavorQueryRes processJson(DCP_GoodsFlavorQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		DCP_GoodsFlavorQueryRes res = null;
		res = this.getResponse();
		
		try 
		{
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			//查询原因码信息
			sql = this.getQuerySql(req);
			
			String[] conditionValues1 = { }; //查詢條件
			
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				
				String num = getQDataDetail.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			
				res.setPageNumber(req.getPageNumber());
				res.setPageSize(req.getPageSize());
				res.setTotalRecords(totalRecords);
				res.setTotalPages(totalPages);
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("FLAVORNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
				res.setDatas(new ArrayList<DCP_GoodsFlavorQueryRes.level1Elm>());
				
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_GoodsFlavorQueryRes.level1Elm oneLv1 = res.new level1Elm();
					// 取出第一层
					String flavorNO = oneData.get("FLAVORNO").toString();
					String flavorName = oneData.get("FLAVORNAME").toString();
					String priority = oneData.get("PRIORITY").toString();
					String status = oneData.get("STATUS").toString();
					oneLv1.setFlavorNo(flavorNO);
					oneLv1.setFlavorName(flavorName);
					oneLv1.setPriority(priority);
					oneLv1.setStatus(status);
					res.getDatas().add(oneLv1);
					oneLv1 = null;
				}
			}
			else
			{
				res.setDatas(new ArrayList<DCP_GoodsFlavorQueryRes.level1Elm>());
			}
			return res;
		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
			
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_GoodsFlavorQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		String sql=null;
		String eId = req.geteId();
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
    
		String flavorNO = req.getRequest().getFlavorNo();
		String flavorName = req.getRequest().getFlavorName();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		StringBuffer sqlbuf=new StringBuffer("");
		
		sqlbuf.append("select * from (SELECT COUNT(*) OVER() NUM , row_number() OVER(ORDER BY flavorNo) rn , "
				+ "flavorNO , flavorName , status , priority , EID  FROM DCP_FLAVOR "
				+ " WHERE EID = '"+eId+"' ");
	
		if(keyTxt !=null && keyTxt.length()>0)
  		{
  			sqlbuf.append("and (flavorNO like '%%" + keyTxt + "%%' or flavorName like '%%"+keyTxt+"%%' )");
  		}
		
		if(flavorNO!=null && flavorNO.length()>0)
  		{
  			sqlbuf.append("and flavorNO='" + flavorNO + "' ");
  		}
		
		if(flavorName!=null && flavorName.length()>0)
  		{
  			sqlbuf.append("and flavorName like '%%" + flavorName + "%%' ");
  		}
		if(status!=null && status.length()>0)
  		{
  			sqlbuf.append("and status='" + status + "' ");
  		}
//		if(priority != null && priority.length() > 0){
//			sqlbuf.append(" and priority = '"+priority+"'");
//		}
		sqlbuf.append( "  order by priority ) ");
		sqlbuf.append( " where  rn>"+startRow+" and rn<="+(startRow+pageSize)+" "
					+ " order by priority "  
					);
		sql = sqlbuf.toString();
		return sql;
	}
	
}
