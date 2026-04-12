package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PGoodsRetrieveReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsRetrieveRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_PGoodsRetrieve extends SPosBasicService<DCP_PGoodsRetrieveReq,DCP_PGoodsRetrieveRes>
{
	
	@Override
	protected boolean isVerifyFail(DCP_PGoodsRetrieveReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}
		
		String pluno=req.getRequest().getPluNo();
		if(Check.Null(pluno))
		{
			isFail = true;
			errMsg.append("商品编码不能为空 ");
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_PGoodsRetrieveReq> getRequestType()
	{
		return new TypeToken<DCP_PGoodsRetrieveReq>() {};
	}
	
	@Override
	protected DCP_PGoodsRetrieveRes getResponseType()
	{
		return new DCP_PGoodsRetrieveRes();
	}
	
	@Override
	protected DCP_PGoodsRetrieveRes processJson(DCP_PGoodsRetrieveReq req) throws Exception
	{
		DCP_PGoodsRetrieveRes res=this.getResponse();
		
		String eId=req.geteId();
		String pluno=req.getRequest().getPluNo();
		
		String sql=getQuerySql(req);
		List<Map<String,Object>> getData=this.doQueryData(sql, null);
		
		res.setDatas(new ArrayList<>());
		
		if (getData!=null && !getData.isEmpty())
		{
			DCP_PGoodsRetrieveRes.level1Elm lv1=res.new level1Elm();
			lv1.setPackageShareType(getData.get(0).get("SPLITTYPE").toString());
			lv1.setPluName(getData.get(0).get("PLU_NAME").toString());
			lv1.setPluNo(getData.get(0).get("PLUNO").toString());
			lv1.setPrice(getData.get(0).get("PRICE").toString());
			lv1.setsUnit(getData.get(0).get("SUNIT").toString());
			lv1.setsUnitName(getData.get(0).get("UNAME").toString());
			lv1.setStatus(getData.get(0).get("STATUS").toString());
			
			//【ID1035247】【华东好利来3.3.0.6】套餐活动内容优化---服务端  by jinzma 20230904
			if (Check.Null(getData.get(0).get("SHOPRANGE").toString())){
				lv1.setShopRange("0");
			}else {
				lv1.setShopRange(getData.get(0).get("SHOPRANGE").toString());
			}
			if (Check.Null(getData.get(0).get("PAYLIMIT").toString())){
				lv1.setPayLimit("0");
			}else {
				lv1.setPayLimit(getData.get(0).get("PAYLIMIT").toString());
			}
			
			lv1.setPluName_lang(new ArrayList<>());
			String sql_Lang="select * from dcp_goods_lang a where a.eid = '"+eId+"'  and a.pluno = '"+pluno+"'";
			List<Map<String,Object>> getDataLang=this.doQueryData(sql_Lang, null);
			if (getDataLang!=null && !getDataLang.isEmpty())
			{
				for (Map<String, Object> map : getDataLang)
				{
					DCP_PGoodsRetrieveRes.GoodsLang gLang=res.new GoodsLang();
					gLang.setLang_type(map.get("LANG_TYPE").toString());
					gLang.setName(map.get("PLU_NAME").toString());
					lv1.getPluName_lang().add(gLang);
				}
			}
			
			String sqlPackage="select a.pclassno A_PCLASSNO,a.invoway A_INVOWAY,a.condcount A_CONDCOUNT,a.MINCOUNT A_MINCOUNT,b.*,c.plu_name,d.uname,e.price "
					+ "from dcp_pgoodsclass a "
					+ "left join dcp_pgoodsclass_detail b on a.eid=b.eid and a.pluno=b.pluno and a.pclassno=b.pclassno "
					+ "left join dcp_goods_lang c on a.eid=c.eid and b.dpluno=c.pluno and c.lang_type='"+req.getLangType()+"' "
					+ "left join dcp_unit_lang d on b.eid=d.eid and b.dunit=d.unit and d.lang_type='"+req.getLangType()+"' "
					+ "left join dcp_goods e on a.eid=e.eid and b.dpluno=e.pluno "
					+ "where a.eid='"+eId+"' and a.pluno='"+pluno+"' order by a.PRIORITY ,b.PRIORITY";
			List<Map<String,Object>> getPackage=this.doQueryData(sqlPackage, null);
			
			lv1.setpClassList(new ArrayList<>());
			if (getPackage!=null && !getPackage.isEmpty())
			{
				//按类别去重
				Map<String, Boolean> condV = new HashMap<>(); //查詢條件
				condV.put("A_PCLASSNO", true);
				List<Map<String, Object>> HeaderPackage= MapDistinct.getMap(getPackage, condV);
				
				for (Map<String, Object> map : HeaderPackage)
				{
					DCP_PGoodsRetrieveRes.ClassGroup group=res.new ClassGroup();
					group.setCondCount(map.get("A_CONDCOUNT").toString());
					group.setInvoWay(map.get("A_INVOWAY").toString());
					group.setMinCount(Check.Null(map.get("A_MINCOUNT").toString())?"0":map.get("A_MINCOUNT").toString());
					group.setPclassNo(map.get("A_PCLASSNO").toString());
					
					group.setSubGoodsList(new ArrayList<>());
					
					Map<String, Object> condi=new HashMap<>();
					condi.put("A_PCLASSNO", map.get("A_PCLASSNO").toString());
					List<Map<String, Object>> subPackage=MapDistinct.getWhereMap(getPackage, condi, true);
					for (Map<String, Object> map2 : subPackage)
					{
						//明细商品没设置跳过
						if (map2.get("DPLUNO").toString().isEmpty())
						{
							continue;
						}
						DCP_PGoodsRetrieveRes.subGoods sub=res.new subGoods();
						sub.setDetailUnit(map2.get("DUNIT").toString());
						sub.setDetailUnitName(map2.get("UNAME").toString());
						sub.setPrice(map2.get("PRICE").toString());
						sub.setDetaiPluNo(map2.get("DPLUNO").toString());
						sub.setDetailPluName(map2.get("PLU_NAME").toString());
						sub.setExtraAmt(map2.get("EXTRAAMT").toString());
						sub.setInvoWay(map2.get("INVOWAY").toString());
						sub.setIsSel(map2.get("ISSEL").toString());
						sub.setQty(map2.get("QTY").toString());
						sub.setSplit(map2.get("SPLIT").toString());
						sub.setSplitPercent(map2.get("SPLITPERCENT").toString());
						sub.setSplitPrice(map2.get("SPLITPRICE").toString());
						sub.setPriority(map2.get("PRIORITY").toString());
						group.getSubGoodsList().add(sub);
					}
					lv1.getpClassList().add(group);
				}
			}
			
			//【ID1035247】【华东好利来3.3.0.6】套餐活动内容优化---服务端  by jinzma 20230904
			{
				lv1.setOrgList(new ArrayList<>());
				lv1.setShopList(new ArrayList<>());
				lv1.setPayLimitList(new ArrayList<>());
				List<Map<String, Object>> getPackData;
				
				sql = " select * from dcp_packagegoods_org where eid='"+eId+"' and pluno='"+pluno+"' ";
				getPackData = this.doQueryData(sql, null);
				if (!CollectionUtils.isEmpty(getPackData)) {
					for (Map<String, Object> onePackData : getPackData) {
						DCP_PGoodsRetrieveRes.Org org = res.new Org();
						org.setOrgNo(onePackData.get("ORGNO").toString());
						org.setOrgName(onePackData.get("ORGNAME").toString());
						
						lv1.getOrgList().add(org);
					}
				}
				
				sql = " select * from dcp_packagegoods_shop where eid='"+eId+"' and pluno='"+pluno+"' ";
				getPackData = this.doQueryData(sql, null);
				if (!CollectionUtils.isEmpty(getPackData)) {
					for (Map<String, Object> onePackData : getPackData) {
						DCP_PGoodsRetrieveRes.Shop shop = res.new Shop();
						shop.setShopNo(onePackData.get("SHOPNO").toString());
						shop.setShopName(onePackData.get("SHOPNAME").toString());
						shop.setBeginDate(onePackData.get("BEGINDDATE").toString());
						shop.setEndDate(onePackData.get("ENDDATE").toString());
						
						lv1.getShopList().add(shop);
					}
				}
				
				sql = " select * from dcp_packagegoods_paylimit where eid='"+eId+"' and pluno='"+pluno+"' ";
				getPackData = this.doQueryData(sql, null);
				if (!CollectionUtils.isEmpty(getPackData)) {
					for (Map<String, Object> onePackData : getPackData) {
						DCP_PGoodsRetrieveRes.PayLimit payLimit = res.new PayLimit();
						payLimit.setPayCode(onePackData.get("PAYCODE").toString());
						payLimit.setPayName(onePackData.get("PAYNAME").toString());
						payLimit.setPayType(onePackData.get("PAYTYPE").toString());
						
						lv1.getPayLimitList().add(payLimit);
					}
				}
			}
			
			
			
			res.getDatas().add(lv1);
			
		}
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
		return res;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
	
	
	}
	
	@Override
	protected String getQuerySql(DCP_PGoodsRetrieveReq req) throws Exception
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String pluno = req.getRequest().getPluNo();
		
		StringBuffer sqlbuf=new StringBuffer(" select a.PLUNO,a.SPLITTYPE,"
				+ " b.PLU_NAME,a.PRICE,a.SUNIT,c.UNAME,a.STATUS,"
				+ " d.paylimit,d.shoprange "
				+ " from dcp_goods a   "
				+ " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langtype+"' "
				+ " left join dcp_unit_lang c on a.eid=c.eid and a.sunit=c.unit and c.lang_type='"+langtype+"' "
				+ " left join dcp_packagegoods d on a.eid=d.eid and a.pluno=d.pluno"
				+ " where a.eid='"+eId+"' and a.pluno='"+pluno+"' and a.plutype='PACKAGE' ");
		
		return sqlbuf.toString();
	}
	
	
}
