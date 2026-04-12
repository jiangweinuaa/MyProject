package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OTakeClassShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_OTakeClassShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_OTakeClassShopQuery extends SPosBasicService<DCP_OTakeClassShopQueryReq, DCP_OTakeClassShopQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_OTakeClassShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_OTakeClassShopQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OTakeClassShopQueryReq>(){};
	}

	@Override
	protected DCP_OTakeClassShopQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OTakeClassShopQueryRes();
	}

	@Override
	protected DCP_OTakeClassShopQueryRes processJson(DCP_OTakeClassShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String textsql="";
//		if(req.getKeyTxt()!=null&&!req.getKeyTxt().isEmpty())
//		{
//			textsql =" and (ORDER_CATEGORYNAME like '%"+req.getKeyTxt()+"%' ) ";
//		}
		String belFirm = null;
		if(req.getOrg_Form()!=null&&req.getOrg_Form().equals("0"))
		{
			belFirm = req.getOrganizationNO();
		}
		else
		{
			belFirm = req.getBELFIRM();
		}
		DCP_OTakeClassShopQueryRes res=new DCP_OTakeClassShopQueryRes();
		res.setDatas(new ArrayList<DCP_OTakeClassShopQueryRes.level1Elm>());
		
		String sql="select a.SHOPID,a.order_categoryno,a.order_categoryname,b.CATEGORYNO,b.CATEGORYNAME,a.priority  "
			+ " from OC_MAPPINGCATEGORY a "
			+ " left join OC_category b on a.EID=b.EID and a.order_categoryname=b.categoryname"
			+ " and b.Belfirm='"+belFirm+"'"
			+ " where a.EID='"+req.geteId()+"' "
			
			+ " and a.LOAD_DOCTYPE='"+req.getDocType()+"' and  a.SHOPID='"+req.getErpShopNO()+"' order by a.PRIORITY  ";

		List<Map<String, Object>> listslq=this.doQueryData(sql, null);
		if(listslq!=null&&!listslq.isEmpty())
		{
			for (Map<String, Object> map : listslq) {
				DCP_OTakeClassShopQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setClassNO(map.get("CATEGORYNO").toString());
				lv1.setClassName(map.get("CATEGORYNAME").toString());
				lv1.setErpShopNO(map.get("SHOPID").toString());
				lv1.setOrderClassNO(map.get("ORDER_CATEGORYNO").toString());
				lv1.setOrderClassName(map.get("ORDER_CATEGORYNAME").toString());
				lv1.setPriority(map.get("PRIORITY").toString());
				res.getDatas().add(lv1);
				lv1 = null;
		  }
			
		}
		
	return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_OTakeClassShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
