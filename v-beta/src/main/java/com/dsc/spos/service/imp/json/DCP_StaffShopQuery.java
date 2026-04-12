package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_StaffShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_StaffShopQueryRes;
import com.dsc.spos.json.cust.res.DCP_StaffShopQueryRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_StaffShopQuery extends SPosBasicService<DCP_StaffShopQueryReq,DCP_StaffShopQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_StaffShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StaffShopQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StaffShopQueryReq>(){};
	}

	@Override
	protected DCP_StaffShopQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StaffShopQueryRes();
	}

	@Override
	protected DCP_StaffShopQueryRes processJson(DCP_StaffShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql1 = null;
		String sql2 = null;
		//查询条件
		String eId = req.geteId();;
		String langType = req.getLangType();
		String staffNO = req.getRequest().getStaffNo();
		
		//查询资料
		DCP_StaffShopQueryRes res = null;
		res = this.getResponse();
		
		String keytxt="";
		if(req.getRequest()!=null)
		{
			if(req.getRequest().getKeyTxt()!=null&&!req.getRequest().getKeyTxt().isEmpty())
			{
				keytxt=" and ( b.organizationno like '%%"+req.getRequest().getKeyTxt()+"%%' or b.org_name like '%%"+req.getRequest().getKeyTxt()+"%%' ) ";
			}
		}
		
		
		
		//查询用户未选管辖门店
		StringBuffer sqlbuf1 = new StringBuffer("");
		String[] condCountValues1 = {eId,eId,staffNO,langType};
		sqlbuf1.append("select SHOPID,shopName from (select a.organizationno as SHOPID,b.org_name as shopName from DCP_ORG a "
					+" inner join DCP_ORG_lang b on a.EID=b.EID and a.organizationno=b.organizationno "
					+" where a.EID=? and a.status='100' and b.status='100'  " + keytxt
					+" and a.organizationno not in (select SHOPID from platform_staffs_shop  where EID=? and opno=?  and status='100') and b.lang_Type=?) tbl "
				);
		
		sql1 = sqlbuf1.toString();
		List<Map<String, Object>> unselectShops = this.doQueryData(sql1,condCountValues1);
		if (unselectShops != null && unselectShops.isEmpty() == false)
		{
			res.setUnselectShops(new ArrayList<level1Elm>());
			for (Map<String, Object> unselectShop : unselectShops) 
			{
				level1Elm oneLv1 = res. new level1Elm();
				
				String shopId = unselectShop.get("SHOPID").toString();
				String shopName = unselectShop.get("SHOPNAME").toString();
				
				oneLv1.setShopId(shopId);
				oneLv1.setShopName(shopName);
				
				res.getUnselectShops().add(oneLv1);
			}
		}
		
		//查询用户已选管辖门店
		StringBuffer sqlbuf2 = new StringBuffer("");
		String[] condCountValues2 = {eId,staffNO,langType};
		sqlbuf2.append("select SHOPID,shopName from (select a.SHOPID as SHOPID,b.org_name as shopName from platform_staffs_shop a "
					+" inner join DCP_ORG_lang b on a.EID=b.EID and a.SHOPID=b.organizationno "
					+" where a.EID=? and a.opno=? and b.lang_Type=? and a.status='100' and b.status='100'  " + keytxt
					+ " ) tbl "
				);
		
		sql2 = sqlbuf2.toString();
		List<Map<String, Object>> selectedShops = this.doQueryData(sql2,condCountValues2);
		if (selectedShops != null && selectedShops.isEmpty() == false)
		{
			res.setSelectedShops(new ArrayList<level1Elm>());
			for (Map<String, Object> selectedShop : selectedShops)
			{
				level1Elm oneLv1 = res.new level1Elm();
				
				String shopId = selectedShop.get("SHOPID").toString();
				String shopName = selectedShop.get("SHOPNAME").toString();
				
				oneLv1.setShopId(shopId);
				oneLv1.setShopName(shopName);
				
				res.getSelectedShops().add(oneLv1);
			}
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_StaffShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
