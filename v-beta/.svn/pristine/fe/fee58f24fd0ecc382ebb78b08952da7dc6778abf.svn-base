package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ShopInfoExQueryReq;
import com.dsc.spos.json.cust.req.DCP_ShopInfoQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopInfoExQueryRes;
import com.dsc.spos.json.cust.res.DCP_ShopInfoQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_ShopInfoExQuery extends SPosBasicService<DCP_ShopInfoExQueryReq, DCP_ShopInfoExQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_ShopInfoExQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_ShopInfoExQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ShopInfoExQueryReq>(){};
	}

	@Override
	protected DCP_ShopInfoExQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ShopInfoExQueryRes();
	}

	@Override
	protected DCP_ShopInfoExQueryRes processJson(DCP_ShopInfoExQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_ShopInfoExQueryRes res=new DCP_ShopInfoExQueryRes();
		String textsql="";
		if(req.getKeyText()!=null&&!req.getKeyText().isEmpty())
		{
			textsql=" where CITY like '%"+req.getKeyText()+"%' ";
		}
			
		String sql="select A.*,B.Org_Name,D.OPNo,D.op_Name,E.EID EEID from DCP_ORG A left join DCP_ORG_Lang B on A.EID=B.EID and A.OrganizationNo=B.OrganizationNo and B.Lang_Type='zh_CN'  "
			+ " left join Platform_Staffs_Shop C on A.EID=C.EID and A.OrganizationNo=C.SHOPID left join Platform_Staffs_Lang D on C.EID=D.EID and C.OPNO=D.OPNO "
			+ " and D.Lang_Type='zh_CN' left join DCP_SHOP_ORDERSet E on A.EID=E.EID and A.OrganizationNo=E.SHOPID and E.OrdersetType='3' and E.OrdersetValue='1' " +textsql;
		List<Map<String, Object>> listsql=this.doQueryData(sql, null);
		if(listsql!=null&&!listsql.isEmpty())
		{
		//单头主键字段
			res.setDatas(new ArrayList<DCP_ShopInfoExQueryRes.level1Elm>());
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
			condition.put("EID", true);
			condition.put("ORGANIZATIONNO", true);
		  //调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(listsql, condition);
			for (Map<String, Object> map : getQHeader) 
			{
				DCP_ShopInfoExQueryRes.level1Elm lv1=new DCP_ShopInfoExQueryRes().new level1Elm();
				lv1.setShopId(map.get("ORGANIZATIONNO").toString());
				lv1.setShopName(map.get("ORG_NAME").toString());
				lv1.setPhone(map.get("PHONE").toString());
				lv1.setAddress(map.get("ADDRESS").toString());
				lv1.setCity(map.get("CITY").toString());
				lv1.setOrgType(map.get("ORG_TYPE").toString());
				if(map.get("EEID").toString()!=null&&!map.get("EEID").toString().isEmpty())
				{
					lv1.setIsFCake("Y");
				}
				else
				{
				  lv1.setIsFCake("N");
				}
				lv1.setIsSMention("Y");
				lv1.setStatus(map.get("STATUS").toString());
				lv1.setLongitude(map.get("LONGITUDE").toString());
				lv1.setLatitude(map.get("LATITUDE").toString());
				lv1.setBelfirm(map.get("BELFIRM").toString());
				lv1.setOpnoInfo(new ArrayList<DCP_ShopInfoExQueryRes.level2Elm>());
				
				for (Map<String, Object> map2 : listsql) 
				{
					if(map.get("ORGANIZATIONNO").toString().equals(map2.get("ORGANIZATIONNO").toString()))
					{
						DCP_ShopInfoExQueryRes.level2Elm lv2=new DCP_ShopInfoExQueryRes().new level2Elm();
						lv2.setOpNO(map2.get("OPNO").toString());
						lv2.setOpName(map2.get("OP_NAME").toString());
						lv1.getOpnoInfo().add(lv2);
					}
		    }
				res.getDatas().add(lv1);
		  }
			return res;
			
		}
			
	return null;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_ShopInfoExQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
