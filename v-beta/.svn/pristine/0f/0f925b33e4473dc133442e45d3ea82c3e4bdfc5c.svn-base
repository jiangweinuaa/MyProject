package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PGoodsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_PGoodsUpdate extends SPosAdvanceService<DCP_PGoodsUpdateReq,DCP_PGoodsUpdateRes>
{
	
	@Override
	protected void processDUID(DCP_PGoodsUpdateReq req, DCP_PGoodsUpdateRes res) throws Exception
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();
		
		String pluno =req.getRequest().getPluNo();
		String price =req.getRequest().getPrice();
		String splitType =req.getRequest().getPackageShareType();
		String unit =req.getRequest().getsUnit();
		List<DCP_PGoodsUpdateReq.GoodsLang> pluname_lang=req.getRequest().getPluName_lang();
		List<DCP_PGoodsUpdateReq.ClassGroup> classGroups=req.getRequest().getpClassList();
		
		//【ID1035247】【华东好利来3.3.0.6】套餐活动内容优化---服务端  by jinzma 20230901
		List<DCP_PGoodsUpdateReq.Org> orgList=req.getRequest().getOrgList();
		List<DCP_PGoodsUpdateReq.Shop> shopList=req.getRequest().getShopList();
		List<DCP_PGoodsUpdateReq.PayLimit> payLimitList=req.getRequest().getPayLimitList();
		
		
		
		String sql = "select pluno from dcp_goods where eid='"+eId+"' and pluno='"+pluno+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData==null || getData.isEmpty())
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此商品编码不存在！");
			return;
		}

		/*
		if (pluname_lang.size()==0) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("商品多语言必须有值！");
			return;
		}
		*/
		
		//删除之前的,再插入
		DelBean db = new DelBean("DCP_GOODS_LANG");
		db.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db.addCondition("PLUNO", new DataValue(pluno,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db));
		
		if (pluname_lang!=null&& !pluname_lang.isEmpty())
		{
			String[] columns_DCP_GOODS_LANG =
					{
							"EID","PLUNO","LANG_TYPE","PLU_NAME","LASTMODITIME"
					};
			for (DCP_PGoodsUpdateReq.GoodsLang lang : pluname_lang)
			{
				
				DataValue[] insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(pluno, Types.VARCHAR),
						new DataValue(lang.getLang_type(), Types.VARCHAR),
						new DataValue(lang.getName(), Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE)
				};
				
				InsBean ib1 = new InsBean("DCP_GOODS_LANG", columns_DCP_GOODS_LANG);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增
			}
		}
		
		if (classGroups!=null)
		{
			//删除之前的,再插入
			db = new DelBean("DCP_PGOODSCLASS");
			db.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db.addCondition("PLUNO", new DataValue(pluno,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db));
			
			//删除之前的,再插入
			db = new DelBean("DCP_PGOODSCLASS_DETAIL");
			db.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db.addCondition("PLUNO", new DataValue(pluno,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db));
			
			String[] columns_DCP_PGOODSCLASS =
					{
							"EID","PLUNO","PCLASSNO","INVOWAY","CONDCOUNT","PRIORITY","MINCOUNT"
					};
			// 套餐商品的显示顺序
			Integer prority = null;
			for (DCP_PGoodsUpdateReq.ClassGroup group : classGroups)
			{
				if(prority==null){
					prority = 0;
				}else {
					prority++;
				}
				
				DataValue[] insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(pluno, Types.VARCHAR),
						new DataValue(group.getPclassNo(), Types.VARCHAR),
						new DataValue(group.getInvoWay(), Types.INTEGER),
						new DataValue(group.getCondCount(), Types.VARCHAR),
						new DataValue(prority, Types.INTEGER),
						new DataValue(Check.Null(group.getMinCount())?"0":group.getMinCount(), Types.VARCHAR),
				};
				
				InsBean ib1 = new InsBean("DCP_PGOODSCLASS", columns_DCP_PGOODSCLASS);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增
				
				
				
				String[] columns_DCP_PGOODSCLASS_DETAIL =
						{
								"EID","PLUNO","PCLASSNO","DPLUNO","INVOWAY","QTY","ISSEL"
								,"EXTRAAMT","DUNIT","PRIORITY","SPLITPRICE"
								,"SPLIT","SPLITPERCENT"
						};
				for (DCP_PGoodsUpdateReq.subGoods sub : group.getSubGoodsList())
				{
					
					insValue1 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(pluno, Types.VARCHAR),
							new DataValue(group.getPclassNo(), Types.VARCHAR),
							new DataValue(sub.getDetaiPluNo(), Types.VARCHAR),
							new DataValue(sub.getInvoWay(), Types.INTEGER),
							new DataValue(sub.getQty(), Types.DECIMAL),
							new DataValue(sub.getIsSel(), Types.VARCHAR),
							new DataValue(sub.getExtraAmt(), Types.DECIMAL),
							new DataValue(sub.getDetailUnit(), Types.VARCHAR),
							new DataValue(sub.getPriority(), Types.INTEGER),
							new DataValue(sub.getSplitPrice(), Types.DECIMAL),
							new DataValue(sub.getSplit(), Types.INTEGER),
							new DataValue(sub.getSplitPercent(), Types.DECIMAL)
						
					};
					
					ib1 = new InsBean("DCP_PGOODSCLASS_DETAIL", columns_DCP_PGOODSCLASS_DETAIL);
					ib1.addValues(insValue1);
					this.addProcessData(new DataProcessBean(ib1)); // 新增
				}
				
			}
		}
		
		UptBean ub1 = new UptBean("DCP_GOODS");
		ub1.addUpdateValue("SPLITTYPE", new DataValue(splitType,Types.VARCHAR));
		ub1.addUpdateValue("SUNIT", new DataValue(unit,Types.VARCHAR));
		ub1.addUpdateValue("PRICE", new DataValue(price,Types.DECIMAL));
		ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));
		
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("PLUNO", new DataValue(pluno, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(ub1));
		
		
		//【ID1035247】【华东好利来3.3.0.6】套餐活动内容优化---服务端 by jinzma 20230901
		{
			//删除 DCP_PACKAGEGOODS
			DelBean db1 = new DelBean("DCP_PACKAGEGOODS");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(pluno, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			//删除 DCP_PACKAGEGOODS_ORG
			DelBean db2 = new DelBean("DCP_PACKAGEGOODS_ORG");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("PLUNO", new DataValue(pluno, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			//删除 DCP_PACKAGEGOODS_SHOP
			DelBean db3 = new DelBean("DCP_PACKAGEGOODS_SHOP");
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("PLUNO", new DataValue(pluno, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3));
			
			//删除 DCP_PACKAGEGOODS_PAYLIMIT
			DelBean db4 = new DelBean("DCP_PACKAGEGOODS_PAYLIMIT");
			db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db4.addCondition("PLUNO", new DataValue(pluno, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db4));
			
			//新增 DCP_PACKAGEGOODS
			{
				String[] columns = {"EID","PLUNO","PAYLIMIT","SHOPRANGE"};
				DataValue[] insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(pluno, Types.VARCHAR),
						new DataValue(req.getRequest().getPayLimit(), Types.VARCHAR),
						new DataValue(req.getRequest().getShopRange(), Types.VARCHAR),
				};
				
				InsBean ib1 = new InsBean("DCP_PACKAGEGOODS", columns);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));
			}
			
			// 新增 DCP_PACKAGEGOODS_ORG
			{
				if (req.getRequest().getOrgList() != null) {
					String[] columns = {"EID","PLUNO","ORGNO","ORGNAME"};
					for (DCP_PGoodsUpdateReq.Org org : req.getRequest().getOrgList()) {
						DataValue[] insValue1 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(pluno, Types.VARCHAR),
								new DataValue(org.getOrgNo(), Types.VARCHAR),
								new DataValue(org.getOrgName(), Types.VARCHAR),
						};
						
						InsBean ib1 = new InsBean("DCP_PACKAGEGOODS_ORG", columns);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1));
					}
				}
			}
			
			// 新增 DCP_PACKAGEGOODS_SHOP
			{
				if (req.getRequest().getShopList() != null) {
					String[] columns = {"EID","PLUNO","SHOPNO","SHOPNAME","BEGINDDATE","ENDDATE"};
					for (DCP_PGoodsUpdateReq.Shop shop : req.getRequest().getShopList()) {
						DataValue[] insValue1 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(pluno, Types.VARCHAR),
								new DataValue(shop.getShopNo(), Types.VARCHAR),
								new DataValue(shop.getShopName(), Types.VARCHAR),
								new DataValue(shop.getBeginDate(), Types.VARCHAR),
								new DataValue(shop.getEndDate(), Types.VARCHAR),
						};
						
						InsBean ib1 = new InsBean("DCP_PACKAGEGOODS_SHOP", columns);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1));
					}
				}
			}
			
			// 新增 DCP_PACKAGEGOODS_PAYLIMIT
			{
				if (req.getRequest().getPayLimitList() != null) {
					String[] columns = {"EID","PLUNO","PAYCODE","PAYNAME","PAYTYPE"};
					for (DCP_PGoodsUpdateReq.PayLimit payLimit : req.getRequest().getPayLimitList()) {
						DataValue[] insValue1 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(pluno, Types.VARCHAR),
								new DataValue(payLimit.getPayCode(), Types.VARCHAR),
								new DataValue(payLimit.getPayName(), Types.VARCHAR),
								new DataValue(payLimit.getPayType(), Types.VARCHAR),
						};
						InsBean ib1 = new InsBean("DCP_PACKAGEGOODS_PAYLIMIT", columns);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1));
					}
				}
			}
			
		}
		
		this.doExecuteDataToDB();
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_PGoodsUpdateReq req) throws Exception
	{
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_PGoodsUpdateReq req) throws Exception
	{
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_PGoodsUpdateReq req) throws Exception
	{
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_PGoodsUpdateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if(req.getRequest()==null)
		{
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String pluno =req.getRequest().getPluNo();
		String price =req.getRequest().getPrice();
		String splitType =req.getRequest().getPackageShareType();
		String unit =req.getRequest().getsUnit();
		List<DCP_PGoodsUpdateReq.GoodsLang> pluname_lang=req.getRequest().getPluName_lang();
		List<DCP_PGoodsUpdateReq.ClassGroup> classGroups=req.getRequest().getpClassList();
		
		if (Check.Null(pluno))
		{
			errMsg.append("套餐商品编码不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(price))
		{
			errMsg.append("组合套餐售价不可为空值, ");
			isFail = true;
		}
		
		if (!PosPub.isNumericType(price))
		{
			errMsg.append("组合套餐售价必须为数字, ");
			isFail = true;
		}
		
		if (Check.Null(splitType))
		{
			errMsg.append("分摊方式不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(unit))
		{
			errMsg.append("销售单位不可为空值, ");
			isFail = true;
		} 

		/*
		if (pluname_lang==null)
		{
			errMsg.append("商品名称多语言不可为空值, ");
			isFail = true;
		}
		*/
		
		if (classGroups==null)
		{
			errMsg.append("套餐类别分组不可为空值, ");
			isFail = true;
		}else {
			for (DCP_PGoodsUpdateReq.ClassGroup classGroup : classGroups) {
				List<DCP_PGoodsUpdateReq.subGoods> subGoodsList = classGroup.getSubGoodsList();
				if(!CollectionUtils.isEmpty(subGoodsList)){
					for (DCP_PGoodsUpdateReq.subGoods subGoods : subGoodsList) {
						String qty = subGoods.getQty();
						//【ID1027358】 //霸王3.0 营销管理，套餐设置，将数量修改成小数点，保存报错。  by jinzma 20220714
						//if(Integer.parseInt(qty)<=0){
						if (!PosPub.isNumericType(qty)){
							errMsg.append("分类编码："+classGroup.getPclassNo()+"的套餐子商品编码为:"+subGoods.getDetaiPluNo()+"的数量必须大于0, ");
							isFail = true;
						}
					}
				}
			}
		}
		
		//【ID1035247】【华东好利来3.3.0.6】套餐活动内容优化---服务端  by jinzma 20230904
		if (req.getRequest().getOrgList() == null || req.getRequest().getOrgList().isEmpty()){
			errMsg.append("公司列表不可为空值, ");
			isFail = true;
		}else{
			for (DCP_PGoodsUpdateReq.Org org : req.getRequest().getOrgList()){
				if (Check.Null(org.getOrgNo())){
					errMsg.append("公司编码不可为空值, ");
					isFail = true;
				}
				if (Check.Null(org.getOrgName())){
					errMsg.append("公司名称不可为空值, ");
					isFail = true;
				}
				
				if (isFail) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				}
			}
		}
		
		if (Check.Null(req.getRequest().getShopRange())){
			errMsg.append("适用门店不可为空值, ");
			isFail = true;
		}else {
			if (req.getRequest().getShopList()!=null) {
				for (DCP_PGoodsUpdateReq.Shop shop : req.getRequest().getShopList()) {
					if (Check.Null(shop.getShopNo())) {
						errMsg.append("门店编码不可为空值, ");
						isFail = true;
					}
					if (Check.Null(shop.getShopName())) {
						errMsg.append("门店名称不可为空值, ");
						isFail = true;
					}
					if (!PosPub.isNumeric(shop.getBeginDate())) {
						errMsg.append("套餐适用开始日期不可为空值, ");
						isFail = true;
					}
					if (!PosPub.isNumeric(shop.getEndDate())) {
						errMsg.append("套餐适用结束日期不可为空值, ");
						isFail = true;
					}
					
					if (isFail) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
					}
				}
			}
		}
		
		if (Check.Null(req.getRequest().getPayLimit())){
			errMsg.append("促销款别规则不可为空值, ");
			isFail = true;
		}else {
			if (req.getRequest().getPayLimitList()!=null){
				for (DCP_PGoodsUpdateReq.PayLimit payLimit : req.getRequest().getPayLimitList()) {
					//paycode这个字段存在空的情形，此处拿掉检核  by jinzma 20230908
					/*if (Check.Null(payLimit.getPayCode())) {
						errMsg.append("支付编码不可为空值, ");
						isFail = true;
					}*/
					if (Check.Null(payLimit.getPayName())) {
						errMsg.append("款别名称不可为空值, ");
						isFail = true;
					}
					if (Check.Null(payLimit.getPayType())) {
						errMsg.append("款别编码不可为空值, ");
						isFail = true;
					}
					
					if (isFail) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
					}
				}
			}
		}
		
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_PGoodsUpdateReq> getRequestType()
	{
		return new TypeToken<DCP_PGoodsUpdateReq>(){};
	}
	
	@Override
	protected DCP_PGoodsUpdateRes getResponseType()
	{
		return new DCP_PGoodsUpdateRes();
	}
	
	
	
	
}
