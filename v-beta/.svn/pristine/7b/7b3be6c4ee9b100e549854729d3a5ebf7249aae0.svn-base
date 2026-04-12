package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_CustomerQueryReq;
import com.dsc.spos.json.cust.res.DCP_CustomerQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_CustomerQuery extends SPosBasicService<DCP_CustomerQueryReq,DCP_CustomerQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_CustomerQueryReq req) throws Exception {
		if(req.getRequest()==null) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request不能为空 ");
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_CustomerQueryReq> getRequestType() {
		return new TypeToken<DCP_CustomerQueryReq>(){};
	}
	
	@Override
	protected DCP_CustomerQueryRes getResponseType() {
		return new DCP_CustomerQueryRes();
	}
	
	@Override
	protected DCP_CustomerQueryRes processJson(DCP_CustomerQueryReq req) throws Exception {
		try {
			DCP_CustomerQueryRes res = this.getResponse();
			res.setDatas(new ArrayList<>());
			//给分页字段赋值
			String sql = this.getQuerySql(req);            //查询总笔数
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			int totalRecords = 0;                                //总笔数
			int totalPages = 0;                                    //总页数
			if (getQData != null && !getQData.isEmpty()) {
				String num = getQData.get(0).get("NUM").toString();
				totalRecords = Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<>(); //查詢條件
				condition.put("CUSTOMERNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQData, condition);
				for (Map<String, Object> oneData : getQHeader) {
					DCP_CustomerQueryRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setRangeList(new ArrayList<>());
					oneLv1.setServiceStaffList(new ArrayList<>());

					String customerNO = oneData.get("CUSTOMERNO").toString();
					
					oneLv1.setCustomerNo(customerNO);
					oneLv1.setCustomerName(oneData.get("CUSTOMERNAME").toString());
					oneLv1.setAbbr(oneData.get("ABBR").toString());
					oneLv1.setAddress(oneData.get("ADDRESS").toString());
					oneLv1.setLinkPerson(oneData.get("LINK_PERSON").toString());//联系人
					oneLv1.setTelephone(oneData.get("TELEPHONE").toString());
					oneLv1.setTaxNO(oneData.get("TAXNO").toString());
					oneLv1.setDistrict(oneData.get("DISTRICT").toString());
					oneLv1.setLegalPerson(oneData.get("LEGAL_PERSON").toString());//法人代表
					oneLv1.setMemo(oneData.get("MEMO").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setRestrictShop(oneData.get("RESTRICTSHOP").toString());
					oneLv1.setDeliveryAddress(oneData.get("DELIVERY_ADDRESS").toString());

					//【ID1037530】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---大客户文案信息---服务 by jinzma 20231227
					oneLv1.setCopyWriting(oneData.get("COPYWRITING").toString());
					
					Map<String, Object> condV = new HashMap<>();
					condV.put("CUSTOMERNO", customerNO);
					List<Map<String, Object>> curCust = MapDistinct.getWhereMap(getQData, condV, true);
					for (Map<String, Object> oneData2 : curCust) {
						DCP_CustomerQueryRes.range oneLv2 = res.new range();
						if (!Check.Null(oneData2.get("SHOPID").toString())) {
							oneLv2.setShopId(oneData2.get("SHOPID").toString());
							oneLv2.setShopName(oneData2.get("SHOPNAME").toString());
							//添加单身
							oneLv1.getRangeList().add(oneLv2);
						}
					}


					//【ID1037503】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---大客户员工会员--服务  by jinzma 20231227
					sql = " select a.opno,b.opname from dcp_customer_servicestaff a"
							+ " left join platform_staffs b on a.eid=b.eid and a.opno=b.opno"
							+ " where a.eid='"+req.geteId()+"' and a.customerno='"+customerNO+"' ";
					List<Map<String, Object>> getStaffQData = this.doQueryData(sql, null);
					for (Map<String, Object> oneData3 : getStaffQData) {
						DCP_CustomerQueryRes.ServiceStaff serviceStaff = res.new ServiceStaff();
						serviceStaff.setOpNo(oneData3.get("OPNO").toString());
						serviceStaff.setOpName(oneData3.get("OPNAME").toString());

						oneLv1.getServiceStaffList().add(serviceStaff);
					}

					//添加单头
					res.getDatas().add(oneLv1);
				}
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			return res;
			
		}catch (Exception e){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	
	}
	
	@Override
	protected String getQuerySql(DCP_CustomerQueryReq req) throws Exception {
		String eId = req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		String shopId = req.getRequest().getShopId(); //【ID1036911】【3.0 嘉华】 大客户要货优化--大客户查询限制门店-服务端  by jinzma 20231031
		String langType = req.getLangType();
		//分页处理
		int pageSize=req.getPageSize();
		int startRow=(req.getPageNumber()-1) * pageSize;
		
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" select a.*,b.shopid,c.org_name as shopname from ("
				+ " select count(*)over() num ,row_number() over(order by a.customerno) as rn,"
				+ " a.eid,a.customerno,a.abbrno,a.taxno,a.district,a.legal_person,a.link_person,a.email,a.fax,"
				+ " a.url,a.telephone,a.address,a.memo,a.status,a.restrictshop,a.delivery_address,a.copywriting,"
                + " b.customer_name as customername,b.abbr"
				+ " from dcp_customer a"
				+ " left join dcp_customer_lang b on a.eid=b.eid and a.customerno=b.customerno and b.lang_type='"+langType+"'"
				+ " ");
		if (!Check.Null(shopId)){
			sqlbuf.append(" left join dcp_customer_shop c on a.eid=c.eid and a.customerno=c.customerno and c.shopid='"+shopId+"'");
		}
		
		sqlbuf.append(" where a.eid='"+eId+"'");
		
		if(!Check.Null(keyTxt)){
			sqlbuf.append(" and (a.customerno like '%"+keyTxt+"%' or b.customer_name like '%"+keyTxt+"%')");
		}
		
		if(!Check.Null(status)) {
			sqlbuf.append(" and a.status="+status+" ");
		}
		
		if (!Check.Null(shopId)){
			sqlbuf.append(" and (a.restrictshop='0' or c.shopid='"+shopId+"')");
		}
		
		sqlbuf.append(" )a");
		
		if (!Check.Null(shopId)){
			//和龙海确认，如果前端传入门店编号，返回的门店列表需要过滤  by jinzma 20231031
			sqlbuf.append(" left join dcp_customer_shop b on a.eid=b.eid and a.customerno=b.customerno and b.shopid='"+shopId+"'");
		}else {
			sqlbuf.append(" left join dcp_customer_shop b on a.eid=b.eid and a.customerno=b.customerno");
		}
		
		sqlbuf.append(" left join dcp_org_lang c on a.eid=c.eid and b.shopid=c.organizationno and c.lang_type='"+langType+"'");
		sqlbuf.append(" where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize));
		sqlbuf.append(" order by a.rn,b.shopid");
		
		
		return sqlbuf.toString();
		
	}
	
}
