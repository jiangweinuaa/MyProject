package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_BasicSetDetailReq;
import com.dsc.spos.json.cust.res.DCP_BasicSetDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 基础设置列表查询
 * @author yuanyy 2020-03-04
 *
 */
public class DCP_BasicSetDetail extends SPosBasicService<DCP_BasicSetDetailReq, DCP_BasicSetDetailRes> {

	@Override
	protected boolean isVerifyFail(DCP_BasicSetDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_BasicSetDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BasicSetDetailReq>(){};
	}

	@Override
	protected DCP_BasicSetDetailRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BasicSetDetailRes();
	}

	@Override
	protected DCP_BasicSetDetailRes processJson(DCP_BasicSetDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_BasicSetDetailRes res = null;
		res = this.getResponse();
		
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			res.setDatas(res.new level1Elm());
			DCP_BasicSetDetailRes.level1Elm lv1 = res.new level1Elm() ;
			
			if(queryDatas != null && queryDatas.size() > 0){
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("TEMPLATENO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(queryDatas, condition);
				
				for (Map<String, Object> map : getQHeader) {
					String templateNo = map.get("TEMPLATENO").toString();
					if(Check.Null(templateNo)){
						continue;
					}
					
					String updateServiceAddress = map.get("UPDATESERVICEADDRESS").toString();
					String posServiceAddress = map.get("POSSERVICEADDRESS").toString();
					String memberServiceAddress = map.get("MEMBERSERVICEADDRESS").toString();
					String payServiceAddress = map.get("PAYSERVICEADDRESS").toString();
					
					String userCode = map.get("USERCODE").toString();
					String userKey = map.get("USERKEY").toString();
					String spreadAppNo = map.get("SPREADAPPNO").toString();
					String spreadAppName = map.getOrDefault("SPREADAPPNAME","").toString(); // 数据库里没有 SPREADAPPNAME 字段
					String restrictShop = map.get("RESTRICTSHOP").toString();
					String pictureGetAddress = map.get("PICTUREGETADDRESS").toString();
					String createopid = map.get("CREATEOPID").toString();
					String createTime = map.get("CREATETIME").toString();
					String lastmodiopid = map.get("LASTMODIOPID").toString();
					String lastmoditime = map.get("LASTMODITIME").toString();
					
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
					
					lv1.setShopList(new ArrayList<DCP_BasicSetDetailRes.level2Elm>());
					for (Map<String, Object> map2 : queryDatas) {
						if(templateNo.equals(map2.get("TEMPLATENO").toString())){
							DCP_BasicSetDetailRes.level2Elm lv2 = res.new level2Elm();
							String shopId = map2.get("SHOPID").toString();
							String shopName = map2.get("SHOPNAME").toString();
							if(Check.Null(shopId)){
								continue;
							}
							
							lv2.setShopId(shopId);
							lv2.setShopName(shopName);
							lv1.getShopList().add(lv2);
						}
						
					}
					
//					res.getDatas().add(lv1);
				}
				res.setDatas(lv1);
			}
			
			
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
	protected String getQuerySql(DCP_BasicSetDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理		
		String templateNo = req.getRequest().getTemplateNo();
		String eId = req.getRequest().getoEId();
		if(Check.Null(eId))
		{
			eId = req.geteId();
		}
		
		String langType = req.getLangType();
		
		if(Check.Null(langType)){
			langType = "zh_CN";
		}
		
		sqlbuf.append("  SELECT * FROM ( "
				+ " SELECT a.eid, a.templateNo , a.updateServiceAddress , a.posServiceAddress , a.memberServiceAddress , a.userCode , "
				+ " a.userKey , a.spreadAppNo , a.restrictShop , a.createopid, a.createopName , a.createTime ,a.lastmodiopid , a.lastmodiopName , a.lastmodiTime,"
				+ " b.SERIALNO , b.shopId , c.org_name as shopName ,a.pictureGetAddress, a.payServiceAddress " 
				+ " FROM DCP_PADGUIDE_BASESET  a "
				+ " LEFT JOIN DCP_PADGUIDE_BASESET_PICKSHOP b ON a.eId = b.eid AND a.templateNo = b.templateNo "
				+ " LEFT JOIN DCP_ORG_LANG c on b.eid = c.EID and c.organizationNo = b.shopid and c.lang_type = '"+langType+"' and c.status = '100'  "
				+ " WHERE a.eid = '"+eId+"'  and a.templateNo = '"+templateNo+"'  " );
		
		sqlbuf.append(" order by a.templateNo, b.SERIALNO ) " );
		sql = sqlbuf.toString();
		return sql;
	}
	
}
