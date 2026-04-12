package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformOnshelfUpdateReq;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformOnshelfUpdateReq.level1goodsElm;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformOnshelfUpdateReq.level2specElm;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformOnshelfUpdateRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMELMProductService;
import com.dsc.spos.waimai.WMJBPProductService;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.dsc.spos.waimai.jddj.JDDJProductService;
import com.dsc.spos.waimai.jddj.OPriceInfo;
import com.dsc.spos.waimai.model.WMJBPGoodsUpdate;
import com.google.gson.reflect.TypeToken;

import eleme.openapi.sdk.api.entity.product.OBatchModifiedFailure;
import eleme.openapi.sdk.api.entity.product.OBatchModifiedResult;
import eleme.openapi.sdk.api.enumeration.product.IdType;


/**
 * 服务函数：OrderPlatformOnshelfUpdate
 * 服务说明：第三方商品上下架更新
 * @author jinzma	 
 * @since  2019-03-14
 */
public class DCP_OrderPlatformOnshelfUpdate extends SPosAdvanceService<DCP_OrderPlatformOnshelfUpdateReq,DCP_OrderPlatformOnshelfUpdateRes> {

	static String goodsLogFileName = "OnshelfUpdate";
	@Override
	protected void processDUID(DCP_OrderPlatformOnshelfUpdateReq req, DCP_OrderPlatformOnshelfUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String loadDocType=req.getLoadDocType();
		String isOnshelf=req.getIsOnshelf();	
		///饿了么上下架
		if (loadDocType.equals("1")) ELMOnshelfUpdate(req,res,loadDocType);//此接口调用上下架修改

		if (loadDocType.equals("1") && isOnshelf.equals("N") && res.getResult().equals("N")  )
		{	
			ELMStockUpdate(req,res,loadDocType);  //此接口调用库存修改,用于商户搞活动时无法下架商品
		}

		if (loadDocType.equals("1") && isOnshelf.equals("Y")&& res.getResult().equals("Y"))
		{	
			ELMStockUpdate(req,res,loadDocType);  //下架失败清库存，上架成功增库存
		}

		///美团上下架
		if (loadDocType.equals("2")) JBPOnshelfUpdate(req,res,loadDocType); //此接口调用商品修改

		if (loadDocType.equals("2") && res.getResult().equals("N"))
		{	
			JBPStockUpdate(req,res,loadDocType); //商品上下架且批量修改接口失败时，修改库存  这个接口有效能问题
		}

		///京东到家上下架
		if (loadDocType.equals("3")) JDDJOnshelfUpdateBySkuId(req,res,loadDocType);//JDDJOnshelfUpdate(req,res,loadDocType);

		//官网上下架接口
		if (loadDocType.equals("4"))
		{
			//从味多美官网上查询信息，然后保存到数据库,直接从这里取
			String method="";
			method="salesDeliver";
			JSONObject reqJsonObject=new JSONObject();
			reqJsonObject.put("cmd", "wdmwaimai_change_product_status");
			reqJsonObject.put("channel", "mall");
			
			String sheftempString="";
			if(isOnshelf.equals("N"))
			{
				sheftempString="0";
			}
			else
			{
				sheftempString="1";
			}
			
			JSONArray jsarrArray=new JSONArray();
			for (DCP_OrderPlatformOnshelfUpdateReq.level1goodsElm map : req.getGoodsdatas()) 
			{
				JSONObject jsmap=new JSONObject();
				jsmap.put("erp_code", req.getShopId());
				//jsmap.put("erp_code", "BJC0001");
				jsmap.put("sn", map.getPluNO() );
				jsmap.put("status", sheftempString );
				jsarrArray.put(jsmap);
				
				UptBean up1=new UptBean("OC_MAPPINGGOODS_SPEC");
				up1.addUpdateValue("ISONSHELF", new DataValue(isOnshelf, Types.VARCHAR));					
				up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				up1.addCondition("LOAD_DOCTYPE", new DataValue("4", Types.VARCHAR));
				up1.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
				up1.addCondition("PLUNO", new DataValue(map.getPluNO(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(up1));	
				
			}
			reqJsonObject.put("prducts", jsarrArray);
			
			reqJsonObject.put("msg", "");
			reqJsonObject.put("opno", "");
			String resbody=HttpSend.SendWuXiang(method, reqJsonObject.toString(), "http://www.wdmcake.cn/api/erp-wdmwaimai_change_product_status.html");
			JSONObject resJsonObject=new JSONObject(resbody);
			String code= resJsonObject.getString("code");
			String message= resJsonObject.getString("msg");
			String memoStr = message;
			if(code.equals("0"))
			{
				res.setFailures(new ArrayList<DCP_OrderPlatformOnshelfUpdateRes.level1Elm>());
			}
			else
			{
				this.pData.clear();
				res.setSuccess(false);
				res.setServiceStatus("000");
				res.setServiceDescription(message);
				return;
			}
			
		}
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPlatformOnshelfUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPlatformOnshelfUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPlatformOnshelfUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformOnshelfUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		//查询第三方门店
		GetOrderShopNO(req);		
		List<level1goodsElm> goodsdatas = req.getGoodsdatas();				
		if(Check.Null(req.getLoadDocType()))
		{
			errMsg.append("外卖平台不可为空值, ");
			isFail = true;
		}

		if(Check.Null(req.getIsOnshelf()))
		{
			errMsg.append("上架否不可为空值, ");
			isFail = true;
		}

//		if(Check.Null(req.getOrderShopNO()))
//		{
//			errMsg.append("平台门店编号不可为空值, ");
//			isFail = true;
//		}

		if(Check.Null(req.getShopId()))
		{
			errMsg.append("门店编号不可为空值, ");
			isFail = true;
		}

		for (level1goodsElm par : goodsdatas) 
		{	
			if (Check.Null(par.getPluNO())) 
			{
				errMsg.append("商品编号不可为空值, ");
				isFail = true;
			}	
			else 
			{
				if (par.getPluNO().equals(" ")) 
				{
					errMsg.append("商品编号不可为空值, ");
					isFail = true;
				}	
			}
			
			if (Check.Null(par.getOrderPluNO())) 
			{
				errMsg.append("平台商品编号不可为空值, ");
				isFail = true;
			}

			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderPlatformOnshelfUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderPlatformOnshelfUpdateReq>(){};
	}

	@Override
	protected DCP_OrderPlatformOnshelfUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderPlatformOnshelfUpdateRes();
	}

	private void ELMOnshelfUpdate(DCP_OrderPlatformOnshelfUpdateReq req,DCP_OrderPlatformOnshelfUpdateRes res,String loadDocType )throws Exception {

		//查询下当前门店的对应的饿了么APPKEY

		String shopId = req.getShopId();
		String eId=req.geteId();
		String isOnshelf=req.getIsOnshelf();	
		Boolean isGoNewFunction = false;//是否走新的接口
		String elmAPPKey = "";
		String elmAPPSecret = "";
		String elmAPPName = "";			
		boolean elmIsSandbox = false;
		StringBuilder errorMessage = new StringBuilder();
		List<Long> goodslong = new ArrayList<Long>();
		try
		{
			Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, "1","");
			if (map != null)
			{
				elmAPPKey = map.get("APPKEY").toString();
				elmAPPSecret = map.get("APPSECRET").toString();
				elmAPPName = map.get("APPNAME").toString();
				String	elmIsTest = map.get("ISTEST").toString();					
				if (elmIsTest != null && elmIsTest.equals("Y"))
				{
					elmIsSandbox = true;
				}
				isGoNewFunction = true;
			}

			List<level1goodsElm> goodsdatas = req.getGoodsdatas();		
			for (level1goodsElm par : goodsdatas) 
			{
				goodslong.add(Long.valueOf(par.getOrderPluNO()));
			}
			HelpTools.writelog_fileName("【批量上架商品】开始, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+shopId+" 上架商品总数："+goodsdatas.size(), goodsLogFileName);

			OBatchModifiedResult nRet= new OBatchModifiedResult();
			if(isGoNewFunction)  //新的接口
			{
				if (isOnshelf.equals("Y")) //上架
				{
					nRet = WMELMProductService.batchListItems(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName,goodslong,errorMessage  );
				}
				else  //下架
				{
					nRet = WMELMProductService.batchDelistItems(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName,goodslong,errorMessage  );	
				}
			}
			else   //饿了么旧的接口
			{
				if (isOnshelf.equals("Y")) //上架
				{
					nRet = WMELMProductService.batchListItems(goodslong, errorMessage);
				}
				else //下架
				{
					nRet = WMELMProductService.batchDelistItems(goodslong, errorMessage);
				}
			}
			if (nRet.getFailures()==null || nRet.getFailures().isEmpty()) 
			{
				HelpTools.writelog_fileName("【批量上架商品】结束,上架成功   平台类型LoadDocType:"+loadDocType+" 上架门店shopId："+shopId+" 上架商品总数："+goodsdatas.size(), goodsLogFileName);
			}	
			else
			{
				HelpTools.writelog_fileName("【批量上架商品】结束,上架失败  "+errorMessage.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			}

			//回写商品映射资料档状态
			List<Long> modifications = nRet.getModifications();
			IdType iDType = nRet.getType();   // ITEM_ID: 商品ID   SPEC_ID:规格ID   CATEGORY_ID:分类ID
			for (Long par : modifications) {										
				UptBean up1=new UptBean("OC_MAPPINGGOODS_SPEC");
				up1.addUpdateValue("ISONSHELF", new DataValue(isOnshelf, Types.VARCHAR));					
				up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				up1.addCondition("LOAD_DOCTYPE", new DataValue("1", Types.VARCHAR));
				up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));

				if (iDType ==IdType.ITEM_ID)
				{
					up1.addCondition("ORDER_PLUNO", new DataValue(par, Types.VARCHAR));
				}
				if (iDType ==IdType.SPEC_ID)
				{
					up1.addCondition("ORDER_SPECNO", new DataValue(par, Types.VARCHAR));
				}
				this.addProcessData(new DataProcessBean(up1));
			}

			//返回
			res.setFailures(new ArrayList<DCP_OrderPlatformOnshelfUpdateRes.level1Elm>());
			List<OBatchModifiedFailure> failures = nRet.getFailures();
			if (failures==null || failures.isEmpty()) 
			{
				res.setResult("Y");
			}
			else
			{
				res.setResult("N");	
				for (OBatchModifiedFailure par : failures) {	
					DCP_OrderPlatformOnshelfUpdateRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setOrderPluNO(String.valueOf(par.getId()));
					oneLv1.setCode(par.getCode().name());
					oneLv1.setDescription(par.getDescription());
					res.getFailures().add(oneLv1);
				}
			}				
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【批量上架商品】失败:"+e.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}	

	}

	private void ELMStockUpdate(DCP_OrderPlatformOnshelfUpdateReq req,DCP_OrderPlatformOnshelfUpdateRes res,String loadDocType )throws Exception {

		//查询下当前门店的对应的饿了么APPKEY
		String shopId = req.getShopId();
		String eId=req.geteId();
		String isOnshelf=req.getIsOnshelf();	
		Boolean isGoNewFunction = false;//是否走新的接口
		String elmAPPKey = "";
		String elmAPPSecret = "";
		String elmAPPName = "";			
		boolean elmIsSandbox = false;
		StringBuilder errorMessage = new StringBuilder();
		Map<Long,Integer> stockMap = new HashMap<Long,Integer>()  ;
		int stockNum = 0 ;
		if (isOnshelf.equals("Y")) stockNum=9999;

		try
		{
			Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(StaticInfo.dao, eId, shopId, "1","");
			if (map != null)
			{
				elmAPPKey = map.get("APPKEY").toString();
				elmAPPSecret = map.get("APPSECRET").toString();
				elmAPPName = map.get("APPNAME").toString();
				String	elmIsTest = map.get("ISTEST").toString();					
				if (elmIsTest != null && elmIsTest.equals("Y"))
				{
					elmIsSandbox = true;
				}
				isGoNewFunction = true;
			}

			List<level1goodsElm> goodsdatas = req.getGoodsdatas();		
			for (level1goodsElm par : goodsdatas) 
			{				
				List<level2specElm> specsDatas = par.getSpecDatas();	
				for (level2specElm specPar : specsDatas )
				{
					stockMap.put(Long.valueOf(specPar.getOrderSpecNO()), stockNum);
				}
			}

			HelpTools.writelog_fileName("【批量更新商品库存】开始, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+shopId+" 商品总数："+goodsdatas.size(), goodsLogFileName);

			OBatchModifiedResult nRet= new OBatchModifiedResult();
			if(isGoNewFunction)  //新的接口
			{
				nRet = WMELMProductService.batchUpdateStock(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName,stockMap,errorMessage  );
			}
			else   //饿了么旧的接口
			{
				nRet = WMELMProductService.batchUpdateStock(stockMap, errorMessage);
			}

			if (nRet.getFailures()==null || nRet.getFailures().isEmpty()) 
			{
				HelpTools.writelog_fileName("【批量更新商品库存】结束,更新成功   平台类型LoadDocType:"+loadDocType+" 更新门店shopId："+shopId+" 更新商品总数："+goodsdatas.size(), goodsLogFileName);
			}	
			else
			{
				HelpTools.writelog_fileName("【批量更新商品库存】结束,更新失败  "+errorMessage.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			}

			//回写商品映射资料档状态
			List<Long> modifications = nRet.getModifications();
			IdType iDType = nRet.getType();   // ITEM_ID: 商品ID   SPEC_ID:规格ID   CATEGORY_ID:分类ID
			for (Long par : modifications) {										
				UptBean up1=new UptBean("OC_MAPPINGGOODS_SPEC");
				up1.addUpdateValue("STOCKQTY", new DataValue(stockNum, Types.VARCHAR));					
				up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				up1.addCondition("LOAD_DOCTYPE", new DataValue("1", Types.VARCHAR));
				up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));

				if (iDType ==IdType.ITEM_ID)
				{
					up1.addCondition("ORDER_PLUNO", new DataValue(par, Types.VARCHAR));
				}
				if (iDType ==IdType.SPEC_ID)
				{
					up1.addCondition("ORDER_SPECNO", new DataValue(par, Types.VARCHAR));
				}
				this.addProcessData(new DataProcessBean(up1));
			}

			//返回

			res.setFailures(new ArrayList<DCP_OrderPlatformOnshelfUpdateRes.level1Elm>());
			List<OBatchModifiedFailure> failures = nRet.getFailures();
			if (failures==null || failures.isEmpty()) 
			{
				res.setResult("Y");
			}
			else
			{
				res.setResult("N");	
				for (OBatchModifiedFailure par : failures) {	
					DCP_OrderPlatformOnshelfUpdateRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setOrderPluNO(String.valueOf(par.getId()));
					oneLv1.setCode(par.getCode().name());
					oneLv1.setDescription(par.getDescription());
					res.getFailures().add(oneLv1);
				}
			}	

		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【批量更新商品库存】失败:"+e.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}	

	}

	private void JBPOnshelfUpdate(DCP_OrderPlatformOnshelfUpdateReq req,DCP_OrderPlatformOnshelfUpdateRes res,String loadDocType )throws Exception {
		String shopId = req.getShopId();
		String eId=req.geteId();
		String isOnshelf=req.getIsOnshelf();
		String goods = toFormatGoods(req) ;
		boolean nRet;
		StringBuilder errorMessage = new StringBuilder();
		List<WMJBPGoodsUpdate>  disheslist = new ArrayList<WMJBPGoodsUpdate>();
		List<level1goodsElm> goodsdatas = req.getGoodsdatas();

		try 
		{				
			String sql=" select  a.order_categoryname,a.pluno,a.order_pluname,a.unit,b.specno,b.specname,b.price,b.order_specno from OC_MAPPINGGOODS a  "
					+ " inner join OC_MAPPINGGOODS_SPEC b on a.EID=b.EID and  a.SHOPID=b.SHOPID and a.LOAD_DOCTYPE=b.LOAD_DOCTYPE  and a.pluno=b.pluno "
					+ " where A.SHOPID='"+shopId+"' and A.LOAD_DOCTYPE='2'  and a.pluno in (" +goods + ")  " ;

			List<Map<String, Object>> getGoodsQData =this.doQueryData(sql, null);
			for (level1goodsElm par : goodsdatas) 
			{							
				WMJBPGoodsUpdate dishes = new WMJBPGoodsUpdate();
				dishes.setePoiId(eId+"_"+shopId);
				String pluNO=par.getPluNO() ;				
				for (Map<String, Object> oneGoodsQData :getGoodsQData )
				{
					String Q_pluNO = oneGoodsQData.get("PLUNO").toString();
					if (pluNO.equals(Q_pluNO))
					{
						String orderCategoryName =oneGoodsQData.get("ORDER_CATEGORYNAME").toString();
						String orderPluName = oneGoodsQData.get("ORDER_PLUNAME").toString();
						String unit = oneGoodsQData.get("UNIT").toString();
						if(unit==null||unit.isEmpty()) unit = "份";
						
						if (Check.Null(orderCategoryName) || Check.Null(orderPluName))
						{
							res.setSuccess(false);
							HelpTools.writelog_fileName("【批量上架商品】失败: 分类名称或平台商品名称不存在, PLUNO: "+Q_pluNO + "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "分类名称或平台商品名称不存在 ");
						}
						dishes.setEDishCode(pluNO);
						dishes.setCategoryName(orderCategoryName);  
						dishes.setDishName(orderPluName);				
						dishes.setUnit(unit);
						break;
					}
				}

				if (isOnshelf.equals("Y")) //上架
				{
					dishes.setIsSoldOut(0);
				}
				else  //下架
				{
					dishes.setIsSoldOut(1);
				}	

				dishes.setSkus(new ArrayList<WMJBPGoodsUpdate.Skus>());
				List<level2specElm> specDatas = par.getSpecDatas();			
				for (level2specElm parspec : specDatas) 
				{
					WMJBPGoodsUpdate.Skus dishesSkus = new WMJBPGoodsUpdate.Skus();
					String specNO=parspec.getSpecNO();
					String orderSpecNO=parspec.getOrderSpecNO();
					for (Map<String, Object> oneGoodsQData :getGoodsQData )
					{
						String Q_pluNO = oneGoodsQData.get("PLUNO").toString();
						String Q_specNO = oneGoodsQData.get("SPECNO").toString();
						String Q_orderSpecNO = oneGoodsQData.get("ORDER_SPECNO").toString();
						if (pluNO.equals(Q_pluNO)&&specNO.equals(Q_specNO)&&orderSpecNO.equals(Q_orderSpecNO) )
						{
							String specName = oneGoodsQData.get("SPECNAME").toString();
							String price = oneGoodsQData.get("PRICE").toString();
							if(specName==null||specName.isEmpty()||specName.trim().length()==0)
							{
								specName="常规";
							}
							dishesSkus.setSkuId(specNO);
							dishesSkus.setSpec(specName);						
							dishesSkus.setStock(Integer.valueOf("9999"));
							dishesSkus.setPrice(Float.valueOf(price));
						/*	dishesSkus.setBoxNum(Float.valueOf("1"));
							dishesSkus.setBoxPrice(Float.valueOf("0"));*/
							dishes.getSkus().add(dishesSkus);
						}
					}				
				}

				disheslist.add(dishes);
			}

			HelpTools.writelog_fileName("【批量上架商品】开始, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+shopId+" 上架商品总数："+goodsdatas.size(), goodsLogFileName);
			nRet = WMJBPProductService.batchUpload(eId,shopId,disheslist,errorMessage  );

			if (nRet) 
			{
				HelpTools.writelog_fileName("【批量上架商品】结束,上架成功   平台类型LoadDocType:"+loadDocType+" 上架门店shopId："+shopId+" 上架商品总数："+goodsdatas.size(), goodsLogFileName);
			}	
			else
			{
				HelpTools.writelog_fileName("【批量上架商品】结束,上架失败  "+errorMessage.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			}

			//回写商品映射资料档状态
			if (nRet) 
			{
				for (level1goodsElm par : goodsdatas) {	
					UptBean up1=new UptBean("OC_MAPPINGGOODS_SPEC");
					up1.addUpdateValue("ISONSHELF", new DataValue(isOnshelf, Types.VARCHAR));					
					up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					up1.addCondition("LOAD_DOCTYPE", new DataValue("2", Types.VARCHAR));
					up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					up1.addCondition("PLUNO", new DataValue(par.getPluNO(), Types.VARCHAR));
					this.addProcessData(new DataProcessBean(up1));					
				}				
			}
			//返回
			res.setFailures(new ArrayList<DCP_OrderPlatformOnshelfUpdateRes.level1Elm>());
			if (nRet) 
			{
				res.setResult("Y");
			}
			else
			{
				res.setResult("N");	
				for (level1goodsElm par : goodsdatas) {	
					DCP_OrderPlatformOnshelfUpdateRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setOrderPluNO(String.valueOf(par.getOrderPluNO()));
					oneLv1.setCode("");
					oneLv1.setDescription("");
					res.getFailures().add(oneLv1);
				}
			}				
		}
		catch (Exception e) 
		{
			// TODO: handle exception			
			HelpTools.writelog_fileName("【批量上架商品】失败:"+e.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}	
	}

	private void JBPStockUpdate(DCP_OrderPlatformOnshelfUpdateReq req,DCP_OrderPlatformOnshelfUpdateRes res,String loadDocType )throws Exception {

		String shopId = req.getShopId();
		String eId=req.geteId();
		String isOnshelf=req.getIsOnshelf();
		boolean nRet;
		StringBuilder errorMessage = new StringBuilder();
		List<WMJBPGoodsUpdate>  dishSkuStockslist = new ArrayList<WMJBPGoodsUpdate>();
		List<level1goodsElm> goodsdatas = req.getGoodsdatas();
		int stockNum = 0 ;
		if (isOnshelf.equals("Y")) stockNum=9999;
		try 
		{	
			for (level1goodsElm par : goodsdatas) 
			{			
				WMJBPGoodsUpdate dishSkuStocks = new WMJBPGoodsUpdate();
				dishSkuStocks.setEDishCode(par.getPluNO());
				dishSkuStocks.setePoiId(eId+"_"+shopId);
				List<level2specElm> specDatas = par.getSpecDatas();	
				dishSkuStocks.setSkus(new ArrayList<WMJBPGoodsUpdate.Skus>());
				
				for (level2specElm parspec : specDatas) 
				{					
				  String specno = parspec.getSpecNO();
					WMJBPGoodsUpdate.Skus dishSkuStocksSkus = new WMJBPGoodsUpdate.Skus();
					dishSkuStocksSkus.setSkuId(specno);				
					dishSkuStocksSkus.setStock(stockNum);
					dishSkuStocks.getSkus().add(dishSkuStocksSkus);
				}

				dishSkuStockslist.add(dishSkuStocks);
			}

			HelpTools.writelog_fileName("【更新菜品库存】开始, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+shopId+" 更新商品总数："+goodsdatas.size(), goodsLogFileName);
			nRet = WMJBPProductService.updateStock(eId,shopId,dishSkuStockslist,errorMessage);

			if (nRet) 
			{
				HelpTools.writelog_fileName("【更新菜品库存】结束,更新成功   平台类型LoadDocType:"+loadDocType+" 更新门店shopId："+shopId+" 更新商品总数："+goodsdatas.size(), goodsLogFileName);
			}	
			else
			{
				HelpTools.writelog_fileName("【更新菜品库存】结束,更新失败  "+errorMessage.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			}

			//回写商品映射资料档状态
			if (nRet) 
			{
				for (level1goodsElm par : goodsdatas) {	
					UptBean up1=new UptBean("OC_MAPPINGGOODS_SPEC");
					up1.addUpdateValue("STOCKQTY", new DataValue(stockNum, Types.VARCHAR));					
					up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					up1.addCondition("LOAD_DOCTYPE", new DataValue("2", Types.VARCHAR));
					up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					up1.addCondition("PLUNO", new DataValue(par.getPluNO(), Types.VARCHAR));
					this.addProcessData(new DataProcessBean(up1));					
				}				
			}
			//返回
			res.setFailures(new ArrayList<DCP_OrderPlatformOnshelfUpdateRes.level1Elm>());
			if (nRet) 
			{
				res.setResult("Y");
			}
			else
			{
				res.setResult("N");	
				for (level1goodsElm par : goodsdatas) {	
					DCP_OrderPlatformOnshelfUpdateRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setOrderPluNO(String.valueOf(par.getOrderPluNO()));
					oneLv1.setCode("");
					oneLv1.setDescription("");
					res.getFailures().add(oneLv1);
				}
			}				
		}
		catch (Exception e) 
		{
			// TODO: handle exception			
			HelpTools.writelog_fileName("【更新菜品库存】失败:"+e.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}	

	}


	private void JDDJOnshelfUpdate(DCP_OrderPlatformOnshelfUpdateReq req,DCP_OrderPlatformOnshelfUpdateRes res,String loadDocType )throws Exception {
		String shopId = req.getShopId();
		String isOnshelf=req.getIsOnshelf();
		String tyep ;
		boolean nRet;
		StringBuilder errorMessage = new StringBuilder();
		List<String> goods = new ArrayList<String>();
		List<level1goodsElm> goodsdatas = req.getGoodsdatas();
		try
		{
			for (level1goodsElm par : goodsdatas) 
			{
				List<level2specElm> specdatas = par.getSpecDatas();			
				for (level2specElm parspec : specdatas)
				{
					goods.add(parspec.getSpecNO());
				}
			}

			if (isOnshelf.equals("Y")) //上架
			{
				tyep="1";
			}
			else  //下架
			{
				tyep="2";
			}

			HelpTools.writelog_fileName("【批量上架商品】开始, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+shopId+" 上架商品总数："+goodsdatas.size(), goodsLogFileName);
			nRet = JDDJProductService.batchUpdateVendibility(shopId, "pos", goods,tyep,errorMessage  );

			if (nRet) 
			{
				HelpTools.writelog_fileName("【批量上架商品】结束,上架成功   平台类型LoadDocType:"+loadDocType+" 上架门店shopId："+shopId+" 上架商品总数："+goodsdatas.size(), goodsLogFileName);
			}	
			else
			{
				HelpTools.writelog_fileName("【批量上架商品】结束,上架失败  "+errorMessage.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			}

			//回写商品映射资料档状态
			if (nRet) 
			{
				for (String  par : goods) {					
					UptBean up1=new UptBean("OC_MAPPINGGOODS_SPEC");
					up1.addUpdateValue("ISONSHELF", new DataValue(isOnshelf, Types.VARCHAR));					
					up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					up1.addCondition("LOAD_DOCTYPE", new DataValue("3", Types.VARCHAR));
					up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					up1.addCondition("PLUNO", new DataValue(par, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(up1));					
				}
				this.doExecuteDataToDB();				
			}
			//返回
			res.setFailures(new ArrayList<DCP_OrderPlatformOnshelfUpdateRes.level1Elm>());
			if (nRet) 
			{
				res.setResult("Y");
			}
			else
			{
				res.setResult("N");	
				for (level1goodsElm par : goodsdatas) {	
					DCP_OrderPlatformOnshelfUpdateRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setOrderPluNO(String.valueOf(par.getOrderPluNO()));
					oneLv1.setCode("");
					oneLv1.setDescription("");
					res.getFailures().add(oneLv1);
				}
			}				
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【批量上架商品】失败:"+e.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}	
	}

	private void JDDJOnshelfUpdateBySkuId(DCP_OrderPlatformOnshelfUpdateReq req,DCP_OrderPlatformOnshelfUpdateRes res,String loadDocType )throws Exception 
	{
		String shopId = req.getShopId();
		String orderShopNO = req.getOrderShopNO();
		String isOnshelf=req.getIsOnshelf();
		int doSale = 0 ;//可售状态（0:可售，1:不可售）

		//StringBuilder errorMessage = new StringBuilder();
		List<Long> goods = new ArrayList<Long>();
		List<level1goodsElm> goodsdatas = req.getGoodsdatas();
		try
		{
			for (level1goodsElm par : goodsdatas) 
			{
				List<level2specElm> specdatas = par.getSpecDatas();			
				for (level2specElm parspec : specdatas)
				{					
					try 
					{
						goods.add(Long.parseLong(parspec.getOrderSpecNO()));	
					} 
					catch (Exception e) 
					{

					}					
				}
			}

			if (isOnshelf.equals("Y")) //上架
			{
				doSale = 0;
			}
			else  //下架
			{
				doSale = 1;
			}

			int totalCount = goods.size();
			if(totalCount==0)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "没有选择商品！");
			}
			int totalPage = totalCount/50+1;//调用接口次数
			List<Long> goodsFailure = new ArrayList<Long>();
			HelpTools.writelog_fileName("【批量上下架商品】开始！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+shopId+" 上下架商品总个数："+totalCount+" 需要调用上下架商品总次数："+totalPage, goodsLogFileName);
			for (int i = 0; i < totalPage; i++)
			{
				boolean nRet = false;
				ArrayList<Long> reqSkuIds = new ArrayList<Long>();
				int j_start = i*50;//0
				int j_end = j_start +50;//50
				if (j_end > totalCount)
				{
					j_end = totalCount;
				}
				for (int j = j_start; j < j_end; j++)
				{
					reqSkuIds.add(goods.get(j));
				}
				HelpTools.writelog_fileName("【批量上下架商品】开始！当前调用接口第【"+i+1+"】次， 上下架商品总个数："+totalCount+" 需要调用上下架商品接口总次数："+totalPage, goodsLogFileName);
				StringBuilder errorMessage = new StringBuilder();
				nRet = JDDJProductService.updateVendibility(orderShopNO, reqSkuIds,doSale,errorMessage);				
				HelpTools.writelog_fileName("【批量上下架商品】完成！当前调用接口第【"+i+1+"】次， 上下架商品总个数："+totalCount+" 需要调用上下架商品接口总次数："+totalPage+" 返回结果："+errorMessage.toString(), goodsLogFileName);

				if (nRet) 
				{					
					HelpTools.writelog_fileName("【批量上下架商品】结束,上下架成功   平台类型LoadDocType:"+loadDocType+" 上下架门店shopId："+shopId+" 上下架商品总数："+goodsdatas.size(), goodsLogFileName);
				}	
				else
				{					
					HelpTools.writelog_fileName("【批量上下架商品】结束,上下架失败  "+errorMessage.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
					goodsFailure.addAll(reqSkuIds);

				}


				//回写商品映射资料档状态
				if (nRet) 
				{
					for (long  par : reqSkuIds) 
					{					
						UptBean up1=new UptBean("OC_MAPPINGGOODS_SPEC");
						up1.addUpdateValue("ISONSHELF", new DataValue(isOnshelf, Types.VARCHAR));					
						up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
						up1.addCondition("LOAD_DOCTYPE", new DataValue("3", Types.VARCHAR));
						up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
						up1.addCondition("ORDER_SPECNO", new DataValue(par, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(up1));					
					}
					try 
					{
						this.doExecuteDataToDB();				
					} 
					catch (Exception e) 
					{

					}

				}

			}


			//返回
			res.setFailures(new ArrayList<DCP_OrderPlatformOnshelfUpdateRes.level1Elm>());
			if (goodsFailure == null || goodsFailure.size() == 0) 
			{
				res.setResult("Y");
			}
			else
			{
				res.setResult("N");	
				for (Long par : goodsFailure) {	
					DCP_OrderPlatformOnshelfUpdateRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setOrderPluNO(String.valueOf(par));
					oneLv1.setCode("");
					oneLv1.setDescription("");
					res.getFailures().add(oneLv1);
				}
			}				
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【批量上架商品】失败:"+e.toString()+ "平台类型LoadDocType:" + loadDocType  , goodsLogFileName);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}	
	}

	private String toFormatGoods(DCP_OrderPlatformOnshelfUpdateReq req)throws Exception {
		String goods="";
		List<level1goodsElm> goodsData = req.getGoodsdatas();
		for (level1goodsElm par :goodsData )
		{
			goods="'" + par.getPluNO() + "'," + goods;			
		}
		goods=goods.substring(0, goods.length()-1);
		return goods;
	}

	private void GetOrderShopNO(DCP_OrderPlatformOnshelfUpdateReq req)
	{
		try 
		{
			String loadDocType = req.getLoadDocType();
			String erpShopNO = req.getShopId();
			String sql = " select * from OC_MAPPINGSHOP where businessid='2' and LOAD_DOCTYPE='"+loadDocType+"'";		
			sql += " and SHOPID='"+erpShopNO+"'";
			
			HelpTools.writelog_fileName("【批量上下架商品】, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO+" 获取第三方门店sql:"+sql, "OnshelfUpdate_GetOrderShopNO");

			List<Map<String, Object>> mappingShops = this.doQueryData(sql, null);
			if (mappingShops != null && mappingShops.isEmpty() == false)
			{
				String orderShopNO = mappingShops.get(0).get("ORDERSHOPNO").toString();
				HelpTools.writelog_fileName("【批量上下架商品】, 平台类型LoadDocType:"+loadDocType+" 当前门店shopId："+erpShopNO+" 获取第三方门店OrderShopNO:"+orderShopNO, "OnshelfUpdate_GetOrderShopNO");
				req.setOrderShopNO(orderShopNO);
			}

		} 
		catch (Exception e) 
		{

		}	
	}

}
