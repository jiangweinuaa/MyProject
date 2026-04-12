package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformMappingGoodsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformMappingGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.WMELMProductService;
import com.dsc.spos.waimai.WMJBPProductService;
import com.google.gson.reflect.TypeToken;

import eleme.openapi.sdk.api.entity.product.OItemSellingTime;
import eleme.openapi.sdk.api.entity.product.OItemTime;
import eleme.openapi.sdk.api.entity.product.OMaterial;
import eleme.openapi.sdk.api.entity.product.OSpec;
import eleme.openapi.sdk.api.enumeration.product.OItemUpdateProperty;
import eleme.openapi.sdk.api.enumeration.product.OItemWeekEnum;

public class DCP_OrderPlatformMappingGoodsUpdate  extends SPosAdvanceService<DCP_OrderPlatformMappingGoodsUpdateReq,DCP_OrderPlatformMappingGoodsUpdateRes > {

	@Override
	protected void processDUID(DCP_OrderPlatformMappingGoodsUpdateReq req, DCP_OrderPlatformMappingGoodsUpdateRes res)
			throws Exception {
		// TODO 自动生成的方法存根
		String compangyNO= req.geteId();
		String docType = req.getDocType();
		String erpShopNO = req.getErpShopNO();
		String orderPluNO = req.getOrderPluNO();
		String orderSpecNO = req.getOrderSpecNO();

		String oldErpPluNO="";
		String oldErpSpecNO="";
		List<Map<String, Object>> getResult = new ArrayList<>() ;
		try
		{
			String sql =" select pluno,specno from OC_mappinggoods_spec where "
					+ " EID='"+compangyNO+"' and SHOPID='"+erpShopNO+"' and load_doctype='"+docType+"' "
					+ " and order_pluno='"+orderPluNO+"'  and order_specno='"+orderSpecNO+"'   ";
			String[] condCountValues={};
			List<Map<String, Object>> getQData=this.doQueryData(sql,condCountValues);
			if (getQData!=null && getQData.isEmpty()==false)
			{
				oldErpPluNO=getQData.get(0).get("PLUNO").toString().trim();
				oldErpSpecNO=getQData.get(0).get("SPECNO").toString().trim();
				if (Check.Null(oldErpPluNO) || Check.Null(oldErpSpecNO))
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "映射的资料不存在，请重新输入！");
				}
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "映射的资料不存在，请重新输入！");
			}

			// 开始映射所有门店资料

			if (docType.equals("1")) //饿了么
			{
				getResult = ELMMappingGoodsUpdate(compangyNO,docType,oldErpPluNO,oldErpSpecNO,req) ;
			}
			if (docType.equals("2")) //聚宝盆
			{
				getResult = JBPMappingGoodsUpdate(compangyNO,docType,oldErpPluNO,oldErpSpecNO,req) ;
			}
			if (docType.equals("3")) //京东到家
			{
				//JDDJMappingGoodsUpdate(compangyNO,docType,erpPluNO,erpSpecNO,newBarcode) ; //留给青哥自己去干吧
			}

			this.doExecuteDataToDB();
			res.setDatas(new ArrayList<DCP_OrderPlatformMappingGoodsUpdateRes.level1Elm>());
			if (getResult!=null)
			{
				for (Map<String, Object> oneData : getResult) 
				{				
					DCP_OrderPlatformMappingGoodsUpdateRes.level1Elm oneLv1= res.new level1Elm();
					oneLv1.setErpShopNO(oneData.get("erpShopNO").toString());
					oneLv1.setResult(oneData.get("result").toString());
					res.getDatas().add(oneLv1);	
					oneLv1 = null;
				}
			}

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch(Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPlatformMappingGoodsUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPlatformMappingGoodsUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPlatformMappingGoodsUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformMappingGoodsUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String docType = req.getDocType();
		String erpShopNO = req.getErpShopNO();
		String orderPluNO = req.getOrderPluNO();
		String orderSpecNO = req.getOrderSpecNO();
		String newPluNO = req.getNewPluNO();
		String newPluName = req.getNewPluName();
		String newSpecNO = req.getNewSpecNO();
		String newSpecName = req.getNewSpecName();

		if(Check.Null(docType))
		{
			errMsg.append("平台类型不可为空值, ");
			isFail = true;
		}
		if(Check.Null(erpShopNO))
		{
			errMsg.append("ERP门店编号不可为空值, ");
			isFail = true;
		}

		if(Check.Null(orderPluNO))
		{
			errMsg.append("平台商品编号不可为空值, ");
			isFail = true;
		}

		if(Check.Null(orderSpecNO))
		{
			errMsg.append("平台规格编号不可为空值, ");
			isFail = true;
		}

		if(Check.Null(newPluNO))
		{
			errMsg.append("商品编号不可为空值, ");
			isFail = true;
		}
		if(Check.Null(newPluName))
		{
			errMsg.append("商品名称不可为空值, ");
			isFail = true;
		}
		if(Check.Null(newSpecNO))
		{
			errMsg.append("规格编号不可为空值, ");
			isFail = true;
		}
		if(Check.Null(newSpecName))
		{
			errMsg.append("规格名称不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_OrderPlatformMappingGoodsUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderPlatformMappingGoodsUpdateReq>(){};
	}

	@Override
	protected DCP_OrderPlatformMappingGoodsUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderPlatformMappingGoodsUpdateRes() ;
	}

	private List<Map<String, Object>> ELMMappingGoodsUpdate(String compangyNO,String docType,String oldErpPluNO, String oldErpSpecNO, DCP_OrderPlatformMappingGoodsUpdateReq req) throws Exception{

		//取饿了么平台已经映射的所有门店
		String newSpecNO = req.getNewSpecNO();
		String sql="";
		List<Map<String, Object>> ELMMappingGoods = new ArrayList<Map<String, Object>>();
		String[] condCountValues={};
	
		sql=" select a.*,b.specno,b.specname,b.order_specno,b.order_specname,b.price,b.stockqty,b.packagefee,b.isonshelf,b.netweight "
				+ " from OC_mappinggoods a inner join OC_mappinggoods_spec b "
				+ " on a.EID=b.EID and a.load_doctype=b.load_doctype and a.SHOPID=b.SHOPID and a.order_pluno=b.order_pluno "
				+ " left join OC_mappingshop c on a.EID=c.EID  and (c.SHOPID is not null and c.SHOPID<>' ') and c.SHOPID=a.SHOPID and c.load_doctype=a.load_doctype "
				+ " where a.EID='"+compangyNO+"' and a.load_doctype='"+docType+"' and a.PLUNO='"+oldErpPluNO+"'    ";

		List<Map<String, Object>> getMappingQData=this.doQueryData(sql,condCountValues);
		Map<String, Boolean> condition_shop = new HashMap<String, Boolean>(); //查询条件
		condition_shop.put("ORDER_SHOP", true);
		//调用过滤函数
		List<Map<String, Object>> getShopQData=MapDistinct.getMap(getMappingQData, condition_shop);
		for (Map<String, Object> oneShopData : getShopQData) 
		{
			boolean is_Plu = false;
			boolean is_Spec = false;
			String erpShopNO = oneShopData.get("SHOPID").toString();
			String orderShopNO = oneShopData.get("ORDER_SHOP").toString();
			String Q_orderShopNO = "";
			String Q_orderPluNO = ""; 
			String Q_PluNO ="";
			String Q_orderCategoryNO ="";
			StringBuilder errorMessage = new StringBuilder();

			Map<OItemUpdateProperty,Object> properties = new HashMap<OItemUpdateProperty,Object>();

			//查询下当前门店的对应的饿了么APPKEY
			Map<String, Object> elmmap = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, compangyNO, erpShopNO, docType,"");
			Boolean isGoNewFunction = false;//是否走新的接口
			String elmAPPKey = "";
			String elmAPPSecret = "";
			String elmAPPName = "";			
			boolean elmIsSandbox = false;
			if (elmmap != null)
			{
				elmAPPKey = elmmap.get("APPKEY").toString();
				elmAPPSecret = elmmap.get("APPSECRET").toString();
				elmAPPName = elmmap.get("APPNAME").toString();
				String	elmIsTest = elmmap.get("ISTEST").toString();					
				if (elmIsTest != null && elmIsTest.equals("Y"))
				{
					elmIsSandbox = true;
				}
				isGoNewFunction = true;
			}

			Map<String, Boolean> condition_plu = new HashMap<String, Boolean>(); //查询条件
			condition_plu.put("ORDER_SHOP", true);
			condition_plu.put("ORDER_PLUNO", true);
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getMappingQData, condition_plu);
			for (Map<String, Object> oneData : getQHeader) 
			{	
				Q_orderShopNO = oneData.get("ORDER_SHOP").toString(); 
				Q_orderPluNO = oneData.get("ORDER_PLUNO").toString();  
				Q_PluNO = oneData.get("PLUNO").toString();	
				Q_orderCategoryNO=oneData.get("ORDER_CATEGORYNO").toString();
				if (orderShopNO.equals(Q_orderShopNO) && oldErpPluNO.equals(Q_PluNO))
				{
					//String Q_PluName = oneData.get("PLUNAME").toString();
					String Q_OrderPluName = oneData.get("ORDER_PLUNAME").toString();
					String Q_description = oneData.get("DESCRIPTION").toString();
					String Q_isAllTimeSell =oneData.get("ISALLTIMESELL").toString();
					String Q_beginDate =oneData.get("BEGINDATE").toString();
					String Q_endDate =oneData.get("ENDDATE").toString();
					String Q_sellWeek =oneData.get("SELLWEEK").toString();
					String Q_sellTime =oneData.get("SELLTIME").toString();
					String Q_material1 =oneData.get("MATERIAL1").toString();
					String Q_materialID1 =oneData.get("MATERIALID1").toString();
					String Q_material2 =oneData.get("MATERIAL2").toString();
					String Q_materialID2 =oneData.get("MATERIALID2").toString();
					String Q_material3 =oneData.get("MATERIAL3").toString();
					String Q_materialID3 =oneData.get("MATERIALID3").toString();
					String Q_material4 =oneData.get("MATERIAL4").toString();
					String Q_materialID4 =oneData.get("MATERIALID4").toString();
					String Q_material5 =oneData.get("MATERIAL5").toString();
					String Q_materialID5 =oneData.get("MATERIALID5").toString();
					String Q_material6 =oneData.get("MATERIAL6").toString();
					String Q_materialID6 =oneData.get("MATERIALID6").toString();
					String Q_material7 =oneData.get("MATERIAL7").toString();
					String Q_materialID7 =oneData.get("MATERIALID7").toString();
					String Q_material8 =oneData.get("MATERIAL8").toString();
					String Q_materialID8 =oneData.get("MATERIALID8").toString();
					String Q_material9 =oneData.get("MATERIAL9").toString();
					String Q_materialID9 =oneData.get("MATERIALID9").toString();
					String Q_material10 =oneData.get("MATERIAL10").toString();
					String Q_materialID10 =oneData.get("MATERIALID10").toString();

					//properties.put(OItemUpdateProperty.)
					properties.put(OItemUpdateProperty.name,Q_OrderPluName);
					properties.put(OItemUpdateProperty.description,Q_description);
					OItemSellingTime sellingtime = new OItemSellingTime();
					if (Q_isAllTimeSell.equals("N"))
					{
						sellingtime.setBeginDate(Q_beginDate);
						sellingtime.setEndDate(Q_endDate);
						if(Q_sellWeek!=null&&Q_sellWeek.isEmpty()==false)
						{
							List <OItemWeekEnum > weekEnum = new ArrayList<>();							
							for (String week: Q_sellWeek.split(",")){
								if (week.equals("1")) weekEnum.add(OItemWeekEnum.MONDAY);
								else if (week.equals("2")) weekEnum.add(OItemWeekEnum.TUESDAY);
								else if (week.equals("3")) weekEnum.add(OItemWeekEnum.WEDNESDAY);
								else if (week.equals("4")) weekEnum.add(OItemWeekEnum.THURSDAY);
								else if (week.equals("5")) weekEnum.add(OItemWeekEnum.FRIDAY);
								else if (week.equals("6")) weekEnum.add(OItemWeekEnum.SATURDAY);
								else if (week.equals("7")) weekEnum.add(OItemWeekEnum.SUNDAY);		           
							}
							sellingtime.setWeeks(weekEnum);
						}
						if(Q_sellTime!=null&&Q_sellTime.isEmpty()==false)
						{
							List<OItemTime> times = new ArrayList<OItemTime>();
							String[] ssTime = Q_sellTime.split(",");//16:00-19:00,20:00-23:00
							for (String timeStr : ssTime) 
							{
								String[] ss1 = timeStr.split("-");//16:00-19:00						  			
								OItemTime oItemTime = new OItemTime();
								oItemTime.setBeginTime(ss1[0]);
								oItemTime.setEndTime(ss1[1]);
								times.add(oItemTime);	
							}					  		
							sellingtime.setTimes(times);
						}
						properties.put(OItemUpdateProperty.sellingTime, sellingtime);
					}

					List<OMaterial> oMaterials = new ArrayList<>();
					if  (!Check.Null(Q_material1)&&!Check.Null(Q_materialID1)) 
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf(Q_materialID1) );
						omaterial.setName(Q_material1);
						oMaterials.add(omaterial);
					}
					if  (!Check.Null(Q_material2)&&!Check.Null(Q_materialID2)) 
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf(Q_materialID2) );
						omaterial.setName(Q_material2);
						oMaterials.add(omaterial);
					}									
					if  (!Check.Null(Q_material3)&&!Check.Null(Q_materialID3)) 
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf(Q_materialID3) );
						omaterial.setName(Q_material3);
						oMaterials.add(omaterial);
					}
					if  (!Check.Null(Q_material4)&&!Check.Null(Q_materialID4)) 
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf(Q_materialID4) );
						omaterial.setName(Q_material4);
						oMaterials.add(omaterial);
					}
					if  (!Check.Null(Q_material5)&&!Check.Null(Q_materialID5)) 
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf(Q_materialID5) );
						omaterial.setName(Q_material5);
						oMaterials.add(omaterial);
					}
					if  (!Check.Null(Q_material6)&&!Check.Null(Q_materialID6)) 
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf(Q_materialID6) );
						omaterial.setName(Q_material6);
						oMaterials.add(omaterial);
					}
					if  (!Check.Null(Q_material7)&&!Check.Null(Q_materialID7)) 
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf(Q_materialID7) );
						omaterial.setName(Q_material7);
						oMaterials.add(omaterial);
					}
					if  (!Check.Null(Q_material8)&&!Check.Null(Q_materialID8)) 
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf(Q_materialID8) );
						omaterial.setName(Q_material8);
						oMaterials.add(omaterial);
					}
					if  (!Check.Null(Q_material9)&&!Check.Null(Q_materialID9)) 
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf(Q_materialID9) );
						omaterial.setName(Q_material9);
						oMaterials.add(omaterial);
					}
					if  (!Check.Null(Q_material10)&&!Check.Null(Q_materialID10)) 
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf(Q_materialID10) );
						omaterial.setName(Q_material10);
						oMaterials.add(omaterial);
					}

					if ( oMaterials ==null || oMaterials.isEmpty()||oMaterials.size()==0  )
					{
						OMaterial omaterial = new OMaterial();
						omaterial.setId(Long.valueOf("10042") );
						omaterial.setName("小麦");
						oMaterials.add(omaterial);
					}

					properties.put(OItemUpdateProperty.materials, oMaterials);
					is_Plu = true;
					break;
				}
			}

			List<OSpec> oSpecs = new ArrayList<OSpec>();
			for (Map<String, Object> oneDataQSpec : getMappingQData) 								
			{
				String Qspec_orderShopNO = oneDataQSpec.get("ORDER_SHOP").toString(); 
				String Qspec_PluNO = oneDataQSpec.get("PLUNO").toString();						
				if (orderShopNO.equals(Qspec_orderShopNO) && oldErpPluNO.equals(Qspec_PluNO))
				{
					OSpec oSpec = new OSpec();
					String QSpec_orderSpecNO = oneDataQSpec.get("ORDER_SPECNO").toString();
					String QSpec_price =  oneDataQSpec.get("PRICE").toString();
					String QSpec_stockQty =  oneDataQSpec.get("STOCKQTY").toString();
					String QSpec_isonshelf =  oneDataQSpec.get("ISONSHELF").toString();
					String QSpec_netWeight =  oneDataQSpec.get("NETWEIGHT").toString();
					String QSpec_packageFee =  oneDataQSpec.get("PACKAGEFEE").toString();
					String QSpec_specNO =  oneDataQSpec.get("SPECNO").toString();
					//String QSpec_specName =  oneDataQSpec.get("SPECNAME").toString();
					String QSpec_orderSpecName =  oneDataQSpec.get("ORDER_SPECNAME").toString();

					int QSpec_onshelf =0 ;
					if (QSpec_isonshelf.equals("Y")) QSpec_onshelf=1;			
					if (Check.Null(QSpec_price )) QSpec_price="0";
					if (Check.Null(QSpec_stockQty )) QSpec_stockQty="9999";
					if (Check.Null(QSpec_netWeight )) QSpec_netWeight="0";
					if (Check.Null(QSpec_packageFee )) QSpec_packageFee="0";
					oSpec.setSpecId(new Long(QSpec_orderSpecNO));

					if (oldErpSpecNO.equals(QSpec_specNO))
					{
						oSpec.setExtendCode(newSpecNO);
					}
					else
					{
						oSpec.setExtendCode(QSpec_specNO);
					}
					oSpec.setName(QSpec_orderSpecName);
					oSpec.setPrice(Double.valueOf(QSpec_price));
					oSpec.setMaxStock(10000);
					oSpec.setStock(Integer.valueOf(QSpec_stockQty));
					oSpec.setOnShelf(QSpec_onshelf);
					oSpec.setStockStatus(1);
					oSpec.setWeight(Integer.valueOf(QSpec_netWeight));
					oSpec.setPackingFee(Double.valueOf(QSpec_packageFee));
					oSpecs.add(oSpec);
					is_Spec = true;
				}		
			}
			properties.put(OItemUpdateProperty.specs,oSpecs);	

			try 
			{	
				boolean nRet = false;
				if (is_Plu && is_Spec ) 
				{
					if(isGoNewFunction)
					{
						nRet = WMELMProductService.updateItem(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,new Long(Q_orderPluNO), new Long(Q_orderCategoryNO), properties, errorMessage);
					}
					else
					{
						nRet = WMELMProductService.updateItem(new Long(Q_orderPluNO), new Long(Q_orderCategoryNO), properties, errorMessage);
					}
				}

				String result = "N";
				if (nRet) 
				{
					result = "Y";
				}	

				Map<String, Object> mappingMap = new HashMap<>();
				mappingMap.put("erpShopNO",erpShopNO);
				mappingMap.put("result", result);
				ELMMappingGoods.add(mappingMap);
			} 
			catch (Exception e) 
			{
				Map<String, Object> mappingMap = new HashMap<>();
				mappingMap.put("erpShopNO",erpShopNO);
				mappingMap.put("result", "N");
				ELMMappingGoods.add(mappingMap);
				continue;		
			}
		}

		return ELMMappingGoods;
	}

	private List<Map<String, Object>> JBPMappingGoodsUpdate(String eId,String docType,String oldErpPluNO, String oldErpSpecNO, DCP_OrderPlatformMappingGoodsUpdateReq req ) throws Exception{

		//取聚宝盆平台已经映射的所有门店
		String newSpecNO = req.getNewSpecNO();
		String sql="";
		List<Map<String, Object>> JBPMappingGoods = new ArrayList<Map<String, Object>>();
		String[] condCountValues={};

		//从数据库里面取平台资料
		sql=" select a.*,b.specno,b.specname,b.order_specno,b.order_specname,b.price,b.stockqty,b.packagefee,b.isonshelf,b.netweight "
				+ " from OC_mappinggoods a inner join OC_mappinggoods_spec b "
				+ " on a.EID=b.EID and a.load_doctype=b.load_doctype and a.SHOPID=b.SHOPID and a.order_pluno=b.order_pluno "
				+ " left join OC_mappingshop c on a.EID=c.EID  and (c.SHOPID is not null and c.SHOPID<>' ') and c.SHOPID=a.SHOPID and c.load_doctype=a.load_doctype "
				+ " where a.EID='"+eId+"' and a.load_doctype='"+docType+"' and a.PLUNO='"+oldErpPluNO+"'    ";

		List<Map<String, Object>> getMappingQData=this.doQueryData(sql,condCountValues);

		Map<String, Boolean> condition_shop = new HashMap<String, Boolean>(); //查询条件
		condition_shop.put("ORDER_SHOP", true);
		//调用过滤函数
		List<Map<String, Object>> getShopQData=MapDistinct.getMap(getMappingQData, condition_shop);
		for (Map<String, Object> oneShopData : getShopQData) 
		{
			boolean is_Plu = false;
			boolean is_Spec = false;
			String erpShopNO = oneShopData.get("SHOPID").toString();
			String orderShopNO = oneShopData.get("ORDER_SHOP").toString();
			String Q_orderShopNO = "";
			String Q_orderPluNO = ""; 
			String Q_PluNO ="";
			StringBuilder errorMessage = new StringBuilder();
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();
			JSONArray array_sku = new JSONArray();

			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
			condition.put("ORDER_SHOP", true);
			condition.put("ORDER_PLUNO", true);
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getMappingQData, condition);
			for (Map<String, Object> oneData : getQHeader) 
			{	
				Q_orderShopNO = oneData.get("ORDER_SHOP").toString(); 
				Q_orderPluNO = oneData.get("ORDER_PLUNO").toString();  
				Q_PluNO = oneData.get("PLUNO").toString();	
				if (orderShopNO.equals(Q_orderShopNO) && oldErpPluNO.equals(Q_PluNO))
				{
					object.put("dishId", new Long(Q_orderPluNO));
					object.put("eDishCode", Q_PluNO);
					is_Plu = true;
					break;
				}
			}

			for (Map<String, Object> oneDataQSpec : getMappingQData) 								
			{
				Q_orderShopNO = oneDataQSpec.get("ORDER_SHOP").toString(); 
				Q_orderPluNO = oneDataQSpec.get("ORDER_PLUNO").toString();  
				Q_PluNO = oneDataQSpec.get("PLUNO").toString();						
				if (orderShopNO.equals(Q_orderShopNO) && oldErpPluNO.equals(Q_PluNO))
				{
					String QSpec_orderSpecNO = oneDataQSpec.get("ORDER_SPECNO").toString();		
					String QSpec_SpecNO = oneDataQSpec.get("SPECNO").toString();		

					JSONObject object_sku = new JSONObject();
					object_sku.put("dishSkuId", new Long(QSpec_orderSpecNO));
					if (oldErpSpecNO.equals(QSpec_SpecNO))
					{
						object_sku.put("eDishSkuCode", newSpecNO);
					}
					else
					{
						object_sku.put("eDishSkuCode", QSpec_SpecNO);
					}
					array_sku.put(object_sku);
					is_Spec=true;
				}
			}

			try 
			{	
				String result = "N";
				boolean nRet=false;
				if (is_Plu&&is_Spec)
				{
					object.put("waiMaiDishSkuMappings", array_sku);
					array.put(object);
					String dishMappings = array.toString();
					nRet = WMJBPProductService.dishMapping(eId, erpShopNO, dishMappings, errorMessage);
				}

				if (nRet) 
				{
					result = "Y";
				}		

				Map<String, Object> mappingMap = new HashMap<>();
				mappingMap.put("erpShopNO",erpShopNO);
				mappingMap.put("result", result);
				JBPMappingGoods.add(mappingMap);
			} 
			catch (Exception e) 
			{
				Map<String, Object> mappingMap = new HashMap<>();
				mappingMap.put("erpShopNO",erpShopNO);
				mappingMap.put("result", "N");
				JBPMappingGoods.add(mappingMap);
				continue;		
			}
		}

		return JBPMappingGoods;

	}


}
