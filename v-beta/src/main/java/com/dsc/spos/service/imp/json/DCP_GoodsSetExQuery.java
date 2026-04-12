package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsSetExQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetExQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetExQuery extends SPosBasicService<DCP_GoodsSetExQueryReq, DCP_GoodsSetExQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetExQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsSetExQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsSetExQueryReq>(){};
	}

	@Override
	protected DCP_GoodsSetExQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsSetExQueryRes(); 
	}

	@Override
	protected DCP_GoodsSetExQueryRes processJson(DCP_GoodsSetExQueryReq req) throws Exception 
	{
		String sdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		String sqlpluno="";
		DCP_GoodsSetExQueryRes res=new DCP_GoodsSetExQueryRes();

		if(req.getPluno()!=null&&!req.getPluno().isEmpty())
		{
			sqlpluno=" and A.pluno ='"+req.getPluno()+"' ";
		}
		else
		{
			//			res.setSuccess(false);
			//			res.setServiceDescription("请传入商品编码！");
			//			return res;
		}

		String sql="select A1.status  A1status,A.PLUNO APLUNO,A1.WUNIT,B.PLU_NAME BPLU_NAME,B.SHORTCUT_CODE ,A1.SPEC SPEC,C.UNIT CUNIT,C.PRICE1 CPRICE1,C.PRICE2 CPRICE2 ,C.PRICE3 CPRICE3 ,D.MATERIAL_PLUNO DMATERIAL_PLUNO ,E.PLU_NAME EPLU_NAME,F.Category_Name SNONAME "
				+ " ,G.Category GSNO,G.Up_Category GUPSNO,I.Category ISNO,I.Up_Category IUPSNO,J.Category_Name JSNONAME  "
				+ " from  DCP_GOODS A1 left join DCP_GOODS_Shop A on A1.EID=A.EID and A1.pluno=A.pluno  and A.OrganizationNo='"+req.getoShopId()+"'  "
				+ " left join DCP_GOODS_Lang B on A.EID=B.EID and A.pluno=B.PLUNO and B.lang_type='zh_CN'   "
				+ " left join "
				+ "(select * from " 
				+ "(" 
				+ "select A.*,ROW_NUMBER() OVER(PARTITION BY A.EID,A.organizationno,A.pluno ORDER BY A.EID,A.organizationno,A.pluno,A.Item DESC ) ST "  
				+ "from DCP_PRICE_shop A " 
				+ "where a.organizationno='"+req.getoShopId()+"'  and a.EFFDate<='"+sdate+"' and '"+sdate+"'<= a.LEDate  and a.status='100'  " 
				+ ") where  ST=1 ) "
				+ " C on A.EID=C.EID and A.OrganizationNo=C.OrganizationNo and A.PLUNO=C.PLUNO and C.EFFDate<='"+sdate+"' and '"+sdate+"'<= C.LEDate  and C.status='100'  "
				+ " left join DCP_BOM_SHOP D on A.EID=D.EID and A.ORGANIZATIONNO=D.ORGANIZATIONNO and A.PLUNO=D.pluno and D.MATERIAL_BDATE<='"+sdate+"' and '"+sdate+"'<=D.MATERIAL_EDATE "
				+ " left join  DCP_GOODS_Lang E on D.EID=E.EID and D.MATERIAL_PLUNO=E.pluno and E.lang_type='zh_CN' "
				+ " left join DCP_CATEGORY_Lang F on A1.EID=F.EID and A1.sno=F.Category and F.lang_type='zh_CN' "
				+ " left join DCP_CATEGORY G on A1.EID=G.EID and A1.sno=G.Category "
				+ " left join DCP_GOODS H on D.EID=H.EID and D.MATERIAL_PLUNO=H.pluno "
				+ " left join DCP_CATEGORY I on H.EID=I.EID and H.sno=I.Category  "
				+ " left join DCP_CATEGORY_Lang J on I.EID=J.EID and I.Category=J.Category and J.lang_type='zh_CN'  "
				+ " where A.OrganizationNo='"+req.getoShopId()+"'  and A.status='100' and A1.status='100'   ";
		sql+=sqlpluno;
		List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
		if(sqllist!=null&&!sqllist.isEmpty())
		{
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("APLUNO", true);	
			//调用过滤函数,过滤DCP_GOODS_Shop
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(sqllist, condition);

			//过滤DCP_PRICE_Shop
			condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("APLUNO", true);	
			condition.put("CUNIT", true);	
			List<Map<String, Object>> getprcer=MapDistinct.getMap(sqllist, condition);
			//过滤DCP_BOM_SHOP
			condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("APLUNO", true);	
			condition.put("DMATERIAL_PLUNO", true);
			List<Map<String, Object>> getboom=MapDistinct.getMap(sqllist, condition);

			res.setDatas(new ArrayList<DCP_GoodsSetExQueryRes.level1Elm>());
			for (Map<String, Object> map : getQHeader) 
			{
				String APLUNO=map.get("APLUNO").toString();
				DCP_GoodsSetExQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setPLUNO(map.get("APLUNO").toString());
				lv1.setPLUNAME(map.get("BPLU_NAME").toString());
				lv1.setSPEC(map.get("SPEC").toString());
				lv1.setSNONAME(map.get("SNONAME").toString());
				lv1.setSNO(map.get("GSNO").toString());
				lv1.setUPSNO(map.get("GUPSNO").toString());
				lv1.setSHORTCUT_CODE(map.get("SHORTCUT_CODE").toString());

				lv1.setStatus(map.get("A1STATUS").toString());
				lv1.setWUNIT(map.get("WUNIT").toString());

				lv1.setTB_PRICE_SHOP(new ArrayList<DCP_GoodsSetExQueryRes.TB_PRICE_SHOP>());

				Map<String, Object> condition1=new HashMap<String, Object>();
				condition1.put("APLUNO", map.get("APLUNO").toString());
				condition1.put("CUNIT", map.get("CUNIT").toString());
				List<Map<String, Object>> getprcer1 =MapDistinct.getWhereMap(getprcer, condition1, true);
				condition1.clear();
				condition1=null;

				for (Map<String, Object> map2 : getprcer1) 
				{					
					if(map2.get("CUNIT").toString().equals("")==false)
					{
						DCP_GoodsSetExQueryRes.TB_PRICE_SHOP priceshop=res.new TB_PRICE_SHOP();
						priceshop.setUNIT(map2.get("CUNIT").toString());
						priceshop.setPRICE1(map2.get("CPRICE1").toString());
						lv1.getTB_PRICE_SHOP().add(priceshop);

						priceshop = null;
						break;
					}
				}
				
				if (getprcer1!=null) 
				{
					getprcer1.clear();
					getprcer1=null;
				}
				
				lv1.setBoomList(new ArrayList<DCP_GoodsSetExQueryRes.level2Elm>());

				Map<String, Object> condition2=new HashMap<String, Object>();
				condition2.put("APLUNO", map.get("APLUNO").toString());					
				List<Map<String, Object>> getboom1 =MapDistinct.getWhereMap(getboom, condition2, true);
				condition2.clear();
				condition2=null;
				
				for (Map<String, Object> map3 : getboom1) 
				{
					if(map3.get("DMATERIAL_PLUNO").toString().equals("")==false)
					{
						DCP_GoodsSetExQueryRes.level2Elm lv2=res.new level2Elm();
						lv2.setMaterial_pluno(map3.get("DMATERIAL_PLUNO").toString());
						lv2.setMaterial_pluName(map3.get("EPLU_NAME").toString());
						lv2.setSNONAME(map3.get("JSNONAME").toString());
						lv2.setSNO(map3.get("ISNO").toString());
						lv2.setUPSNO(map3.get("IUPSNO").toString());

						lv1.getBoomList().add(lv2);

						lv2 = null;
					}
				}
				
				if (getboom1!=null) 
				{
					getboom1.clear();
					getboom1=null;
				}
				
				res.getDatas().add(lv1);

				lv1 = null;

			}
			
			if (getQHeader!=null) 
			{
				getQHeader.clear();
				getQHeader=null;
			}
			if (getprcer!=null) 
			{
				getprcer.clear();
				getprcer=null;
			}
			if (getboom!=null) 
			{
				getboom.clear();
				getboom=null;
			}

		}
		
		if (sqllist!=null) 
		{
			sqllist.clear();
			sqllist=null;
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_GoodsSetExQueryReq req) throws Exception 
	{
		return "";
	}

}
