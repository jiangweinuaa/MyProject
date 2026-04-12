package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_OrderMenuDetialQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderMenuDetialQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderMenuDetialQuery extends SPosBasicService<DCP_OrderMenuDetialQueryReq,DCP_OrderMenuDetialQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderMenuDetialQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  

		if (Check.Null(req.getMenuID())) {
			errCt++;
			errMsg.append("菜单ID不可为空值, ");
			isFail = true;
		} 

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderMenuDetialQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderMenuDetialQueryReq>(){} ;
	}

	@Override
	protected DCP_OrderMenuDetialQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderMenuDetialQueryRes();
	}

	@Override
	protected DCP_OrderMenuDetialQueryRes processJson(DCP_OrderMenuDetialQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		
		DCP_OrderMenuDetialQueryRes res = this.getResponse();
		res.setCategoryDatas(new ArrayList<DCP_OrderMenuDetialQueryRes.level1Category>());
		String sql=null;	
		sql = this.getQueryDataSql(req);
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{			
			String menuID = getQDataDetail.get(0).get("MENUID").toString();
			String menuName = getQDataDetail.get(0).get("MENUNAME").toString();
			String menuDescription = getQDataDetail.get(0).get("DESCRIPTION").toString();
			String menuMemo = getQDataDetail.get(0).get("MEMO").toString();
			res.setMenuID(menuID);
			res.setMenuName(menuName);
			res.setMenuDescription(menuDescription);
			res.setMenuMemo(menuMemo);
			
			//单头主键字段
			Map<String, Boolean> condition_menu_category = new HashMap<String, Boolean>(); //查詢條件
			condition_menu_category.put("CATEGORYNO_MENU", true);	
			// 菜单分类
			List<Map<String, Object>> getMenuCategory=MapDistinct.getMap(getQDataDetail, condition_menu_category);
			
			//菜单分类下商品明细
			Map<String, Boolean> condition_menu_pluno = new HashMap<String, Boolean>(); //查詢條件
			condition_menu_pluno.put("PLUNO_MENU_GOOD", true);

			List<Map<String, Object>> getMenuGoodsDetail=MapDistinct.getMap(getQDataDetail, condition_menu_pluno);

			//商品的规格明细
			Map<String, Boolean> condition_specno = new HashMap<String, Boolean>(); //查詢條件

			condition_specno.put("PLUNO_MENU_GOOD", true);	
			condition_specno.put("SPECNO", true);
			List<Map<String, Object>> getMenuGoodsSPecDetail=MapDistinct.getMap(getQDataDetail, condition_specno);

			//商品的属性明细
			Map<String, Boolean> condition_attr = new HashMap<String, Boolean>(); //查詢條件
			condition_attr.put("PLUNO_MENU_GOOD", true);	
			condition_attr.put("ATTRNAME", true);
			List<Map<String, Object>> getMenuGoodsAttrDetail=MapDistinct.getMap(getQDataDetail, condition_attr);

		
			for (Map<String, Object> oneData : getMenuCategory) 
			{

				DCP_OrderMenuDetialQueryRes.level1Category oneLv1 = res.new level1Category();
				oneLv1.setGoodDatas(new ArrayList<DCP_OrderMenuDetialQueryRes.level2Good>());
				String orderNO = oneData.get("ORDERNO").toString();
				String shopId = oneData.get("SHOPID").toString();
				
				String	loadDocType	= oneData.get("LOAD_DOCTYPE").toString();
				String shipTime = oneData.get("SHIPTIME").toString();

				for (Map<String, Object> oneData_detail : getMenuGoodsDetail) 
				{
					DCP_OrderMenuDetialQueryRes.level2Good oneLv2 = res.new level2Good();
					oneLv2.setSpecDatas(new ArrayList<DCP_OrderMenuDetialQueryRes.level3Spec>());
					oneLv2.setAttrDatas(new ArrayList<DCP_OrderMenuDetialQueryRes.level3Attr>());
					String orderNO_detail = oneData_detail.get("ORDERNO").toString();
					String shopNO_detail = oneData_detail.get("SHOPID").toString();
					String item = oneData_detail.get("ITEM").toString();
					String LOAD_DOCTYPE=oneData_detail.get("LOAD_DOCTYPE").toString();
					String detailmemo="";

					if(orderNO.equals(orderNO_detail)&&shopId.equals(shopNO_detail)&&loadDocType.equals(LOAD_DOCTYPE))
					{											
						
						for (Map<String, Object> map : getMenuGoodsSPecDetail) 
						{
							String ORDERNO=map.get("ORDERNO").toString();
							String LOADDOCTYPE=map.get("LOAD_DOCTYPE").toString();

							if(orderNO.equals(ORDERNO)&&shopId.equals(map.get("SHOPID").toString())&&loadDocType.equals(LOADDOCTYPE)  )
							{
								DCP_OrderMenuDetialQueryRes.level3Spec lv3=res.new level3Spec();
								/*lv3.setMsgType(map.get("DMEMOTYPE").toString());
								lv3.setMsgName(map.get("DMEMONAME").toString());
								lv3.setMessage(map.get("DMEMO").toString());*/
								oneLv2.getSpecDatas().add(lv3);
								if(map.get("DMEMOTYPE").toString().equals("1"))
								{
									detailmemo+=map.get("DMEMO").toString();
								}

							}
						}
						
						for (Map<String, Object> map : getMenuGoodsAttrDetail) 
						{
							String ORDERNO=map.get("ORDERNO").toString();
							String LOADDOCTYPE=map.get("LOAD_DOCTYPE").toString();

							if(orderNO.equals(ORDERNO)&&shopId.equals(map.get("SHOPID").toString())&&loadDocType.equals(LOADDOCTYPE)  )
							{
								DCP_OrderMenuDetialQueryRes.level3Attr lv3=res.new level3Attr();
								/*lv3.setMsgType(map.get("DMEMOTYPE").toString());
								lv3.setMsgName(map.get("DMEMONAME").toString());
								lv3.setMessage(map.get("DMEMO").toString());*/
								oneLv2.getAttrDatas().add(lv3);
								if(map.get("DMEMOTYPE").toString().equals("1"))
								{
									detailmemo+=map.get("DMEMO").toString();
								}
								lv3 = null;

							}
						}
						
						
														
					}		
					oneLv1.getGoodDatas().add(oneLv2);
					oneLv2 = null;
				}
				

				res.getCategoryDatas().add(oneLv1);
				
				oneLv1 = null;
			}

		}
		
	 return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_OrderMenuDetialQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}
	
	public String getQueryDataSql(DCP_OrderMenuDetialQueryReq req) throws Exception
	{
		String eId = req.geteId();
		String menuID = req.getMenuID();
		String keyText = req.getKeyTxt();
		String belfirm = "";
		
		if(req.getOrg_Form().equals("0"))//总部视角
		{
			belfirm = req.getOrganizationNO();
		}
		else if(req.getOrg_Form().equals("2"))//门店视角
		{
			belfirm = req.getBELFIRM();
		}
		
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from (");
		
		sqlbuf.append(" select A.*,B.CATEGORYNO as CATEGORYNO_menu ,B.Categoryname as Categoryname_menu,B.Description as categoryDescription,B.Priority as categorypriority,C.CATEGORYNAME, ");
		sqlbuf.append(" D.PLUNO as PLUNO_menu_good, D.CATEGORYNO as CATEGORYNO_menu_good,D.Priority as priority_menu_good,D.status as status_menu_good,");
		sqlbuf.append(" E.Pluno,E.pluname,E.description  as description_good,E.filename,E.unit,E.priority,E.ISALLTIMESELL,  E.BEGINDATE,E.ENDDATE,E.SELLWEEK,E.SELLTIME,E.MEMO as MEMO_good,E.status as status_good,");
		sqlbuf.append(" E.MATERIALID1,E.MATERIALID2,E.MATERIALID3,E.MATERIALID4,E.MATERIALID5,E.MATERIALID6,E.MATERIALID7,E.MATERIALID8,E.MATERIALID9,E.MATERIALID10, ");
		sqlbuf.append(" E.MATERIAL1,E.MATERIAL2,E.MATERIAL3,E.MATERIAL4,E.MATERIAL5,E.MATERIAL6,E.MATERIAL7,E.MATERIAL8,E.MATERIAL9,E.MATERIAL10,");
		sqlbuf.append(" F.specno,F.specname,F.price,F.stockqty,F.packagefee,F.isOnshelf,F.NETWEIGHT,H.attrname,H.attrvalue");
		sqlbuf.append(" from OC_menu A left  join OC_menu_category B on A.EID=B.EID and A.MENUID=B.MENUID");//菜单的分类表
		sqlbuf.append(" left join OC_category C on B.EID=C.EID and B.CATEGORYNO=C.CATEGORYNO ");//菜品池的分类表
		sqlbuf.append(" left join OC_menu_goods D on A.EID=D.EID and A.MENUID=D.MENUID ");//菜单的菜品表
		sqlbuf.append(" left join OC_goods E on D.EID=E.EID and D.PLUNO=E.PLUNO   AND D.BELFIRM=E.BELFIRM");//菜品池的菜品表
		sqlbuf.append(" left join OC_goods_spec F on E.EID=F.EID and E.PLUNO=F.PLUNO and E.Belfirm=F.Belfirm");//菜品池的规格表
		sqlbuf.append(" left join OC_goods_attr H on E.EID=H.EID and E.PLUNO=H.PLUNO and E.Belfirm=H.Belfirm");//菜品池的属性表
		
		sqlbuf.append(" where A.EID='"+eId+"' and A.MENUID='"+menuID+"' order by B.Priority,D.Priority");
		
		sqlbuf.append(")");
		
		
		
		return sqlbuf.toString();
	}
	

}
