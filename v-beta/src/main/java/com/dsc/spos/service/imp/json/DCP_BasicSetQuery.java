package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_BasicSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_BasicSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 基础设置列表查询
 * @author yuanyy 2020-03-04
 *
 */
public class DCP_BasicSetQuery extends SPosBasicService<DCP_BasicSetQueryReq, DCP_BasicSetQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_BasicSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_BasicSetQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BasicSetQueryReq>(){};
	}

	@Override
	protected DCP_BasicSetQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BasicSetQueryRes();
	}

	@Override
	protected DCP_BasicSetQueryRes processJson(DCP_BasicSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_BasicSetQueryRes res = null;
		res = this.getResponse();
		
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			int totalRecords = 0;								//总笔数
			int totalPages = 0;			
			res.setDatas(new ArrayList<DCP_BasicSetQueryRes.level1Elm>());
			
			if(queryDatas != null && queryDatas.size() > 0){
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("TEMPLATENO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(queryDatas, condition);
				
				for (Map<String, Object> map : getQHeader) {
					DCP_BasicSetQueryRes.level1Elm lv1 = res.new level1Elm();
					String templateNo = map.get("TEMPLATENO").toString();
					String updateServiceAddress = map.get("UPDATESERVICEADDRESS").toString();
					String posServiceAddress = map.get("POSSERVICEADDRESS").toString();
					String memberServiceAddress = map.get("MEMBERSERVICEADDRESS").toString();
					
					String userCode = map.get("USERCODE").toString();
//					String userName = map.get("USERNAME").toString();
					String userKey = map.get("USERKEY").toString();
					String spreadAppNo = map.get("SPREADAPPNO").toString();
					String spreadAppName = map.getOrDefault("SPREADAPPNAME","").toString();
					String restrictShop = map.get("RESTRICTSHOP").toString();
					String createopid = map.get("CREATEOPID").toString();
					String createTime = map.get("CREATETIME").toString();
					String lastmodiopid = map.get("LASTMODIOPID").toString();
					String lastmoditime = map.get("LASTMODITIME").toString();
					String pictureGetAddress = map.get("PICTUREGETADDRESS").toString();
					String payServiceAddress = map.getOrDefault("PAYSERVICEADDRESS", "").toString();
					
					if(!Check.Null(lastmoditime)){
						SimpleDateFormat format22 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
						Date modiDate = format22.parse(lastmoditime);
						lastmoditime = format22.format(modiDate);
					}
					
					if(!Check.Null(createTime)){
						SimpleDateFormat format22 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
						Date modiDate = format22.parse(createTime);
						createTime = format22.format(modiDate);
					}
					
					lv1.setTemplateNo(templateNo);
					lv1.setUpdateServiceAddress(updateServiceAddress);
					lv1.setPosServiceAddress(posServiceAddress);
					lv1.setMemberServiceAddress(memberServiceAddress);
					lv1.setUserCode(userCode);
//					lv1.setUserName(userName);
					lv1.setUserKey(userKey);
					lv1.setSpreadAppNo(spreadAppNo);
					lv1.setSpreadAppName(spreadAppName);
					lv1.setRestrictShop(restrictShop);
					lv1.setCreateopid(createopid);
					lv1.setCreateTime(createTime);
					lv1.setLastmodiopid(lastmodiopid);
					lv1.setLastmoditime(lastmoditime);
					lv1.setPictureGetAddress(pictureGetAddress);
					lv1.setPayServiceAddress(payServiceAddress);
					
					res.getDatas().add(lv1);
					
				}
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}
		
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_BasicSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		String eId = req.getRequest().getoEId();
		String oShopId = req.getRequest().getoShopId();
		String shopId = req.getRequest().getShopId();
		String keyTxt = req.getRequest().getSearchString();// 注: 根据模板编号模糊搜索
		String terminalType = req.getRequest().getTerminalType();
		
		sqlbuf.append("  SELECT * FROM ( "
				+ " SELECT count(distinct a.templateNo  ) OVER() AS NUM,  dense_rank() over (order BY  a.templateNo ) rn,"
				+ " a.eid, a.templateNo , a.updateServiceAddress , a.posServiceAddress , a.memberServiceAddress , a.userCode , a.pictureGetAddress , "
				+ " a.userKey , a.spreadAppNo , a.restrictShop , a.createopid, a.createopName , a.createTime ,a.lastmodiopid , a.lastmodiopName , a.lastmodiTime,"
				+ " b.shopId, b.serialNo , a.pad_use , a.mobileCash_Use , a.payServiceAddress " 
				+ " FROM DCP_PADGUIDE_BASESET  a "
				+ " LEFT JOIN DCP_PADGUIDE_BASESET_PICKSHOP b ON a.eId = b.eid AND a.templateNo = b.templateNo "
				+ " WHERE a.eid = '"+eId+"'  " );
		
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and ( a.templateNo like '%%"+keyTxt+"%%' ) ");
		}
		
		if (!Check.Null(terminalType) && terminalType.equals("pad"))
		{
			sqlbuf.append(" and   a.pad_use = '1' ");
		}
		
		if (!Check.Null(terminalType) && terminalType.equals("mobileCash"))
		{
			sqlbuf.append(" and   a.mobilecash_use = '1' ");
		}
		
		
		if (!Check.Null(shopId))
		{
			sqlbuf.append(" and ( ( b.shopId = '"+shopId+"' and a.restrictShop = 'limit' ) or a.restrictShop = 'noLimit' ) ");
		}
		
		sqlbuf.append(" order by a.templateNo "
				+ " ) t where t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)
				+ " order BY templateNo, serialNo ");
		sql = sqlbuf.toString();
		return sql;
	}
	
}
