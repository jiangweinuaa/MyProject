package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_ShopGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_ShopGroupQuery extends SPosBasicService<DCP_ShopGroupQueryReq, DCP_ShopGroupQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_ShopGroupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ShopGroupQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ShopGroupQueryReq>(){};
	}

	@Override
	protected DCP_ShopGroupQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ShopGroupQueryRes();
	}

	@Override
	protected DCP_ShopGroupQueryRes processJson(DCP_ShopGroupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		try {

			DCP_ShopGroupQueryRes res=this.getResponse();
			//判断keyTxt，如果是TEXT的要显示TEXT的一个上级，以及TEXT的所有上级
			String skeytxt="";
			if(req.getRequest().getKeyTxt()!=null&&!req.getRequest().getKeyTxt().isEmpty())
			{
				skeytxt=" and (C.OrganizationNo like '%"+req.getRequest().getKeyTxt()+"%' or C.Org_Name  like '%"+req.getRequest().getKeyTxt()+"%' )  ";
			}
			String sTypeString="";
			if(req.getRequest().getShopGroupType()!=null &&!req.getRequest().getShopGroupType().isEmpty())
			{
				sTypeString=" and A.ShopGroupType='"+req.getRequest().getShopGroupType()+"' ";
			}

			String sql="select A.shopGroupNo,A.shopGroupName,A.status Astatus,B.SHOPID,B.status Bstatus,C.Org_Name,nvl(A.SHOPGROUPTYPE,'1') SHOPGROUPTYPE ,"
					+ " a.priority , b.priority AS childpriority "
					+ "  from DCP_SHOPGHEAD A "
					+ "left join DCP_SHOPGROUP B on A.EID=B.EID and A.ShopGroupNo=b.ShopGroupNo and A.ShopGroupType=B.ShopGroupType "
					+ " left join DCP_ORG_Lang C on B.EID=C.EID and B.SHOPID=C.OrganizationNo and C.Lang_Type='"+req.getLangType()+"' "
					+ " where A.EID='"+req.geteId()+"' " +skeytxt +sTypeString  
					+ " ORDER BY  a.priority , b.priority ";
			List<Map<String, Object>> listgroup=this.doQueryData(sql, null);
			if(listgroup!=null&&!listgroup.isEmpty())
			{
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("SHOPGROUPNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(listgroup, condition);
				res.setDatas(new ArrayList<DCP_ShopGroupQueryRes.level1Elm>());
				for (Map<String, Object> maphead : getQHeader) 
				{
					DCP_ShopGroupQueryRes.level1Elm lev1=new DCP_ShopGroupQueryRes().new level1Elm();
					lev1.setShopGroupNo(maphead.get("SHOPGROUPNO").toString());
					lev1.setShopGroupName(maphead.get("SHOPGROUPNAME").toString());
					lev1.setStatus(maphead.get("ASTATUS").toString());
					lev1.setPriority(maphead.getOrDefault("PRIORITY", "1").toString());
					lev1.setShopGroupType(maphead.get("SHOPGROUPTYPE").toString() );
					//设置children
					List<Map<String, Object>> getQDetailtemp=getcurlist(listgroup,maphead.get("SHOPGROUPNO").toString(),maphead.get("SHOPGROUPTYPE").toString());
					if(getQDetailtemp!=null&&!getQDetailtemp.isEmpty())
					{
						lev1.setChildren(new ArrayList<DCP_ShopGroupQueryRes.level2Elm>());
						for (Map<String, Object> mapdetail : getQDetailtemp)
						{
							DCP_ShopGroupQueryRes.level2Elm lev2=new DCP_ShopGroupQueryRes().new level2Elm();
							lev2.setShopId(mapdetail.get("SHOPID").toString());
							lev2.setShopName(mapdetail.get("ORG_NAME").toString());
							lev2.setStatus(mapdetail.get("BSTATUS").toString());
							lev2.setPriority(mapdetail.getOrDefault("CHILDPRIORITY", "1").toString());
							lev2.setShopGroupType(mapdetail.get("SHOPGROUPTYPE").toString() );

							lev1.getChildren().add(lev2);
						}
					}
					res.getDatas().add(lev1);
				}

			}

			return res;
		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	private List<Map<String, Object>> getcurlist(List<Map<String, Object>> listgroup,String groupno,String grouptype )
	{
		List<Map<String, Object>> listtemp=new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : listgroup) 
		{
			if(map.get("SHOPGROUPNO").toString().equals(groupno)&&!map.get("SHOPID").toString().isEmpty()&&map.get("SHOPGROUPTYPE").toString().equals(grouptype))
			{
				listtemp.add(map);
			}
		}
		return listtemp;

	}


	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_ShopGroupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
