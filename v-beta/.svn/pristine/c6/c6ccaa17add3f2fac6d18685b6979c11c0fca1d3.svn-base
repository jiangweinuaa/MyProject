package com.dsc.spos.service.imp.json;

import java.util.*;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_GoodsSetDetailReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetDetailRes;
import com.dsc.spos.json.cust.res.DCP_GoodsSetDetailRes.goodsSetDetail;
import com.dsc.spos.json.cust.res.DCP_GoodsSetDetailRes.spec_lang;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetDetail extends SPosBasicService<DCP_GoodsSetDetailReq,DCP_GoodsSetDetailRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_GoodsSetDetailReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		//必传字段
		String pluNo = req.getRequest().getPluNo();
		if (Check.Null(pluNo))
		{
			errMsg.append("商品编码不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
		
	}
	
	@Override
	protected TypeToken<DCP_GoodsSetDetailReq> getRequestType() {
		return new TypeToken<DCP_GoodsSetDetailReq>(){};
	}
	
	@Override
	protected DCP_GoodsSetDetailRes getResponseType() {
		return new DCP_GoodsSetDetailRes();
	}
	
	@Override
	protected DCP_GoodsSetDetailRes processJson(DCP_GoodsSetDetailReq req) throws Exception {
		String sql = null;
		DCP_GoodsSetDetailRes res = this.getResponse();
		String curLangtype=req.getLangType();
		if(curLangtype==null||curLangtype.isEmpty())
		{
			curLangtype="zh_CN";
		}
		
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		goodsSetDetail data  = res.new goodsSetDetail();
		if (getQData != null && getQData.isEmpty() == false)
		{
			Map<String, Object> singleMap = getQData.get(0);
			String pluNo = 	getQData.get(0).get("PLUNO").toString();
			String pluType = getQData.get(0).get("PLUTYPE").toString();
			String attrGroupId = getQData.get(0).get("ATTRGROUPID").toString();
			String attrGroupName = getQData.get(0).get("ATTRGROUPNAME").toString();
			//List<attr> attrList = requestModel.getAttrList();
			//List<barcode> barcodeList = requestModel.getBarcodeList();
			
			String shortcutCode = getQData.get(0).get("SHORTCUT_CODE").toString();
			/*String weight = getQData.get(0).get("WEIGHT").toString();
			String volume = getQData.get(0).get("VOLUME").toString();*/
			String warmType = getQData.get(0).get("WARMTYPE").toString();
			String category = getQData.get(0).get("CATEGORY").toString();
			String categoryName = getQData.get(0).get("CATEGORY_NAME").toString();
			String brandNo = getQData.get(0).get("BRAND").toString();
			String brandName = getQData.get(0).get("BRAND_NAME").toString();
			String seriesNo = getQData.get(0).get("SERIES").toString();
			String seriesName = getQData.get(0).get("SERIES_NAME").toString();
			String taxCode = getQData.get(0).get("TAXCODE").toString();
			String taxName = getQData.get(0).get("TAXNAME").toString();
			String virtual = getQData.get(0).get("VIRTUAL").toString();
			String openPrice = getQData.get(0).get("OPENPRICE").toString();
			String isWeight = getQData.get(0).get("ISWEIGHT").toString();
			String stockManageType = getQData.get(0).get("STOCKMANAGETYPE").toString();
			String memo = getQData.get(0).get("MEMO").toString();
			String status = getQData.get(0).get("STATUS").toString();
			String price = getQData.get(0).get("PRICE").toString();
			String supPrice = getQData.get(0).get("SUPPRICE").toString();
			
			String baseUnit = getQData.get(0).get("BASEUNIT").toString();//基准单位编码
			String pUnit = getQData.get(0).get("PUNIT").toString();//要货单位编码
			String purUnit = getQData.get(0).get("PURUNIT").toString();//采购单位编码
			String sUnit = getQData.get(0).get("SUNIT").toString();//默认销售单位编码
			String cUnit = getQData.get(0).get("CUNIT").toString();//盘点单位编码
			String wUnit = getQData.get(0).get("WUNIT").toString();//库存单位编码
			String bomUnit = getQData.get(0).get("BOM_UNIT").toString();//配方单位编码
			String prodUnit = getQData.get(0).get("PROD_UNIT").toString();//成品的生产单位
			
			String baseUnitName = getQData.get(0).get("BASE_UNIT_NAME").toString();
			String sUnitName = getQData.get(0).get("SUNIT_NAME").toString();
			String pUnitName = getQData.get(0).get("PUNIT_NAME").toString();
			String wUnitName = getQData.get(0).get("WUNIT_NAME").toString();
			String prodUnitName = getQData.get(0).get("PROD_UNIT_NAME").toString();
			String bomUnitName = getQData.get(0).get("BOM_UNIT_NAME").toString();
			String cUnitName = getQData.get(0).get("CUNIT_NAME").toString();
			String purUnitName = getQData.get(0).get("PUR_UNIT_NAME").toString();
			String rUnit=getQData.get(0).get("RUNIT").toString();
			String rUnitName=getQData.get(0).get("RUNIT_NAME").toString();
			
			String isBatch = getQData.get(0).get("ISBATCH").toString();
			String shelfLife = getQData.get(0).get("SHELFLIFE").toString();
			String stockInValidDay = getQData.get(0).get("STOCKINVALIDDAY").toString();
			String stockOutValidDay = getQData.get(0).get("STOCKOUTVALIDDAY").toString();
			String checkValidDay = getQData.get(0).get("CHECKVALIDDAY").toString();
			String splitType = singleMap.get("SPLITTYPE").toString();
			String mainBarCode = singleMap.get("MAINBARCODE").toString();
			String sourceType = singleMap.get("SOURCETYPE").toString();
			String mainSpec = singleMap.get("MAINSPEC").toString();
			String pickUnit = singleMap.get("PICKUNIT").toString();
			String pickUnitName = singleMap.get("PICKUNITNAME").toString();
			String supplier = singleMap.get("SUPPLIER").toString();
			String supplierName = singleMap.get("SUPPLIERNAME").toString();
			String procRate = singleMap.get("PROCRATE").toString();
			String isQualityCheck = singleMap.get("ISQUALITYCHECK").toString();
			String testMarketSaleQty = singleMap.get("TESTMARKETSALEQTY").toString();
			String testMarketSaleAmt = singleMap.get("TESTMARKETSALEAMT").toString();
			String testMarketGrossAmt = singleMap.get("TESTMARKETGROSSAMT").toString();
			String minqty = singleMap.get("MINQTY").toString();
			String maxqty = singleMap.get("MAXQTY").toString();
			String mulqty = singleMap.get("MULQTY").toString();
			String batchRules = singleMap.get("BATCHRULES").toString();
			String batchSortType = singleMap.get("BATCHSORTTYPE").toString();
			String isShelfLifeCheck = singleMap.get("ISSHELFLIFECHECK").toString();
			String prodShop = singleMap.get("PROD_SHOP").toString();
			String prodHQ = singleMap.get("PROD_HQ").toString();
			String prodOEM = singleMap.get("PROD_OEM").toString();
			String isSensitive = singleMap.get("ISSENSITIVE").toString();
			String isCombineBatch = singleMap.get("ISCOMBINEBATCH").toString();
			String materialProperties = singleMap.get("MATERIALPROPERTIES").toString();
			String productTime = singleMap.get("PRODUCTTIME").toString();
			String producer = singleMap.get("PRODUCER").toString();
			String storageCon = singleMap.get("STORAGECON").toString();
			String manufacturer = singleMap.get("MANUFACTURER").toString();
			String hotLine = singleMap.get("HOTLINE").toString();
			String ingreTable = singleMap.get("INGRETABLE").toString();
			String netContent = singleMap.get("NETCONTENT").toString();
			String foodProLicNum = singleMap.get("FOODPROLICNUM").toString();
			String eatingMethod = singleMap.get("EATINGMETHOD").toString();
			String exStandard = singleMap.get("EXSTANDARD").toString();
			String propAddress = singleMap.get("PROPADDRESS").toString();
			String createOpId = singleMap.get("CREATEOPID").toString();
			String createOpName = singleMap.get("CREATEOPNAME").toString();
			String createDeptId = singleMap.get("CREATEDEPTID").toString();
			String createDeptName = singleMap.get("CREATEDEPTNAME").toString();
			String createTime = singleMap.get("CREATETIME").toString();
			String lastModiOpId = singleMap.get("LASTMODIOPID").toString();
			String lastModiOpName = singleMap.get("LASTMODIOPNAME").toString();
			String lastModiTime = singleMap.get("LASTMODITIME").toString();
            String purprice = singleMap.get("PURPRICE").toString();

            String inputTaxCode = singleMap.get("INPUTTAXCODE").toString();
            String inputTaxName = singleMap.get("INPUTTAXNAME").toString();
            String inputTaxRate = singleMap.get("INPUTTAXRATE").toString();

            data.setPluNo(pluNo);
			data.setPluType(pluType);
			data.setAttrGroupId(attrGroupId);
			data.setAttrGroupName(attrGroupName);
			data.setCategory(category);
			data.setCategoryName(categoryName);
			data.setBrandNo(brandNo);
			data.setBrandName(brandName);
			data.setSeriesNo(seriesNo);
			data.setSeriesName(seriesName);
			data.setTaxCode(taxCode);
			data.setTaxName(taxName);
			data.setShortcutCode(shortcutCode);
			/*data.setWeight(weight);
			data.setVolume(volume);*/
			data.setWarmType(warmType);
			data.setVirtual(virtual);
			data.setOpenPrice(openPrice);
			data.setIsWeight(isWeight);
			data.setStockManageType(stockManageType);
			data.setMemo(memo);
			data.setStatus(status);
			data.setBaseUnit(baseUnit);
			data.setBaseUnitName(baseUnitName);
			data.setsUnit(sUnit);
			data.setsUnitName(sUnitName);
			data.setPrice(price);
			data.setSupPrice(supPrice);
			data.setBomUnit(bomUnit);
			data.setBomUnitName(bomUnitName);
			data.setProdUnit(prodUnit);
			data.setProdUnitName(prodUnitName);
			data.setpUnit(pUnit);
			data.setpUnitName(pUnitName);
			data.setPurUnit(purUnit);
			data.setPurUnitName(purUnitName);
			data.setcUnit(cUnit);
			data.setcUnitName(cUnitName);
			data.setwUnit(wUnit);
			data.setwUnitName(wUnitName);
			data.setIsBatch(isBatch);
			data.setShelfLife(shelfLife);
			data.setStockInValidDay(stockInValidDay);
			data.setStockOutValidDay(stockOutValidDay);
			data.setCheckValidDay(checkValidDay);
			data.setPluName(getQData.get(0).get("PLU_NAME").toString());
			data.setrUnit(rUnit);
			data.setrUnitName(rUnitName);

			data.setSplitType(splitType);
			data.setShortCutCode(shortcutCode);
			data.setMainBarcode(mainBarCode);
			data.setSourceType(sourceType);
			data.setSpec(mainSpec);
			data.setPickUnit(pickUnit);
			data.setPickUnitName(pickUnitName);
			data.setSupplier(supplier);
			data.setSupplierName(supplierName);
			data.setProcRate(procRate);
			data.setIsQualityCheck(isQualityCheck);
			data.setTestMarketSaleQty(testMarketSaleQty);
			data.setTestMarketSaleAmt(testMarketSaleAmt);
			data.setTestMarketGrossAmt(testMarketGrossAmt);
			data.setMinQty(minqty);
			data.setMaxQty(maxqty);
			data.setMulQty(mulqty);
			data.setBatchRules(batchRules);
			data.setBatchSortType(batchSortType);
			data.setIsShelfLifeCheck(isShelfLifeCheck);
			data.setProdShop(prodShop);
			data.setProdHQ(prodHQ);
			data.setProdOEM(prodOEM);
			data.setIsSensitive(isSensitive);
			data.setIsCombineBatch(isCombineBatch);
			data.setMaterialProperties(materialProperties);
			data.setProductTime(productTime);
			data.setProducer(producer);
			data.setStorageCon(storageCon);
			data.setManufacturer(manufacturer);
			data.setHotLine(hotLine);
			data.setIngreTable(ingreTable);
			data.setNetContent(netContent);
			data.setFoodProLicNum(foodProLicNum);
			data.setEatingMethod(eatingMethod);
			data.setExStandard(exStandard);
			data.setPropAddress(propAddress);
			data.setCreateOpId(createOpId);
			data.setCreateOpName(createOpName);
			data.setCreateDeptId(createDeptId);
			data.setCreateDeptName(createDeptName);
			data.setCreateTime(createTime);
			data.setLastModiOpId(lastModiOpId);
			data.setLastModiOpName(lastModiOpName);
			data.setLastModiTime(lastModiTime);
            data.setPurPrice(purprice);
            data.setInputTaxCode(inputTaxCode);
            data.setInputTaxRate(inputTaxRate);
            data.setInputTaxName(inputTaxName);

			data.setPluName_lang(new ArrayList<DCP_GoodsSetDetailRes.pluName_lang>());
			//data.setSpec_lang(new ArrayList<DCP_GoodsSetDetailRes.spec_lang>());
			data.setAttrList(new ArrayList<DCP_GoodsSetDetailRes.attr>());
			data.setBarcodeList(new ArrayList<DCP_GoodsSetDetailRes.barcode>());
			data.setUnitList(new ArrayList<DCP_GoodsSetDetailRes.unit>());

			data.setGoodsTemplateList(new ArrayList<DCP_GoodsSetDetailRes.GoodsTemplateList>());
			data.setpOrderTemplateList(new ArrayList<DCP_GoodsSetDetailRes.POrderTemplateList>());
			data.setTagList(new ArrayList<DCP_GoodsSetDetailRes.TagList>());
			//商品多语言
			Map<String, Boolean> condition_pluName_Lang = new HashMap<String, Boolean>(); //查詢條件		
			condition_pluName_Lang.put("LANGTYPE", true);
			//调用过滤函数
			List<Map<String, Object>> pluNameLangDatas=MapDistinct.getMap(getQData, condition_pluName_Lang);
			if(pluNameLangDatas!=null)
			{
				for (Map<String, Object> map : pluNameLangDatas)
				{
					DCP_GoodsSetDetailRes.pluName_lang pluNameLang = res.new pluName_lang();
					pluNameLang.setLangType(map.get("LANGTYPE").toString());
					pluNameLang.setName(map.get("PLU_NAME").toString());
					if(map.get("LANGTYPE").toString().equals(curLangtype))
					{
						data.setPluName(map.get("PLU_NAME").toString());
					}
					
					data.getPluName_lang().add(pluNameLang);
					
				/*	DCP_GoodsSetDetailRes.spec_lang specNameLang = res.new spec_lang();				
					specNameLang.setLang_type(map.get("LANGTYPE").toString());
					specNameLang.setName(map.get("SPEC_NAME").toString());
					data.getSpec_lang().add(specNameLang);*/
					
				}
				
			}
			
			//条码
			Map<String, Boolean> condition_barcode = new HashMap<String, Boolean>(); //查詢條件		
			condition_barcode.put("PLUBARCODE", true);
			condition_barcode.put("UNIT_BARCODE", true);
			condition_barcode.put("FEATURENO", true);
			
			//调用过滤函数
			List<Map<String, Object>> barcodeDatas=MapDistinct.getMap(getQData, condition_barcode);
			if(barcodeDatas!=null)
			{
				for (Map<String, Object> map : barcodeDatas)
				{
					DCP_GoodsSetDetailRes.barcode barcode = res.new barcode();
					barcode.setPluBarcode(map.get("PLUBARCODE").toString());
					barcode.setFeatureNo(map.get("FEATURENO").toString());
					barcode.setUnit(map.get("UNIT_BARCODE").toString());
					barcode.setUnitRatio(map.get("UNITRATIO").toString());
					barcode.setStatus(map.get("BARCODE_STATUS").toString());
					barcode.setLen(map.getOrDefault("LEN", "").toString());
					barcode.setWidth(map.getOrDefault("WIDTH", "").toString());
					barcode.setHeight(map.getOrDefault("HEIGHT", "").toString());
					barcode.setVolumeUnit(map.getOrDefault("VOLUMEUNIT_BARCODE", "").toString());
					barcode.setVolumeUnitName(map.getOrDefault("VOLUMEUNITNAME_BARCODE", "").toString());
					barcode.setWeight(map.getOrDefault("WEIGHT_BARCODE", "").toString());
					barcode.setWeightUnit(map.getOrDefault("WEIGHTUNIT_BARCODE", "").toString());
					barcode.setWeightUnitName(map.getOrDefault("WEIGHTUNITNAME_BARCODE", "").toString());
					barcode.setBarcodeType(map.getOrDefault("BARCODETYPE","").toString());
					data.getBarcodeList().add(barcode);
					
				}
				
			}
			
			
			//属性
			Map<String, Boolean> condition_attr = new HashMap<String, Boolean>(); //查詢條件		
			condition_attr.put("ATTRID", true);
			//调用过滤函数
			List<Map<String, Object>> attrDatas=MapDistinct.getMap(getQData, condition_attr);
			if(attrDatas!=null)
			{
				//属性
				Map<String, Boolean> condition_attrValue = new HashMap<String, Boolean>(); //查詢條件		
				condition_attrValue.put("ATTRID", true);
				condition_attrValue.put("ATTRVALUEID", true);
				//调用过滤函数
				List<Map<String, Object>> attrValueDatas=MapDistinct.getMap(getQData, condition_attrValue);
				for (Map<String, Object> map : attrDatas)
				{
					if(map.get("ATTRID")==null||map.get("ATTRID").toString().isEmpty())
					{
						continue;
					}
					String attrId = map.get("ATTRID").toString();
					DCP_GoodsSetDetailRes.attr attr = res.new attr();
					attr.setAttrValueList(new ArrayList<DCP_GoodsSetDetailRes.attrValue>());
					attr.setAttrId(map.get("ATTRID").toString());
					attr.setAttrName(map.get("ATTRNAME").toString());
					if(attrValueDatas!=null)
					{
						for (Map<String, Object> mapValue : attrValueDatas)
						{
							if(mapValue.get("ATTRVALUEID")==null||mapValue.get("ATTRVALUEID").toString().isEmpty())
							{
								continue;
							}
							if(attrId.equals(mapValue.get("ATTRID").toString())==false)
							{
								continue;
							}
							
							DCP_GoodsSetDetailRes.attrValue attrValue = res.new attrValue();
							attrValue.setAttrValueId(mapValue.get("ATTRVALUEID").toString());
							attrValue.setAttrValueName(mapValue.get("ATTRVALUENAME").toString());
							attr.getAttrValueList().add(attrValue);
						}
					}
					
					data.getAttrList().add(attr);
					
				}
				
			}
			
			//属性
			Map<String, Boolean> condition_unit = new HashMap<String, Boolean>(); //查詢條件		
			condition_unit.put("OUNIT", true);
			condition_unit.put("UNIT", true);
			//调用过滤函数
			List<Map<String, Object>> unitDatas=MapDistinct.getMap(getQData, condition_unit);
			
			if(unitDatas!=null)
			{
				//spec多语言
				Map<String, Boolean> condition_unit_spec = new HashMap<String, Boolean>(); //查詢條件		
				condition_unit_spec.put("OUNIT", true);
				condition_unit_spec.put("LANGTYPE_SPEC", true);
				//调用过滤函数
				List<Map<String, Object>> unitSpecDatas=MapDistinct.getMap(getQData, condition_unit_spec);
				
				
				for (Map<String, Object> map : unitDatas)
				{
					DCP_GoodsSetDetailRes.unit unit = res.new unit();
					
					String oUnit = map.get("OUNIT").toString();
					unit.setoUnit(map.get("OUNIT").toString());
					unit.setoUnitName(map.get("OUNIT_NAME").toString());
					unit.setUnitName(map.get("UNIT_NAME").toString());
					unit.setUnit(map.get("UNIT").toString());
					unit.setoQty(map.get("OQTY").toString());
					unit.setQty(map.get("QTY").toString());
					unit.setBomUnitUse(map.get("BOM_UNIT_USE").toString());
					unit.setcUnitUse(map.get("CUNIT_USE").toString());
					unit.setProdUnitUse(map.get("PROD_UNIT_USE").toString());
					unit.setpUnitUse(map.get("PUNIT_USE").toString());
					unit.setPurUnitUse(map.get("PURUNIT_USE").toString());
					unit.setsUnitUse(map.get("SUNIT_USE").toString());
					unit.setrUnitUse(map.get("RUNIT_USE").toString());
					unit.setWeight(map.get("WEIGHT").toString());
					unit.setVolume(map.get("VOLUME").toString());
					unit.setSpec_lang(new ArrayList<spec_lang>());
					if(unitSpecDatas!=null&&unitSpecDatas.isEmpty()==false)
					{
						
						for (Map<String, Object> mapSpec : unitSpecDatas)
						{
							String oUnit_spec = mapSpec.get("OUNIT").toString();
							String langType_spec = mapSpec.get("LANGTYPE_SPEC").toString();
							if(langType_spec==null||langType_spec.isEmpty()||oUnit_spec==null||oUnit_spec.isEmpty())
							{
								continue;
							}
							if(oUnit_spec.equals(oUnit))
							{
								spec_lang spec = res.new spec_lang();
								spec.setLangType(langType_spec);
								spec.setName(mapSpec.get("SPEC").toString());
								if (langType_spec.equals(curLangtype))
								{
									unit.setSpec(mapSpec.get("SPEC").toString());
								}
								
								unit.getSpec_lang().add(spec);
								
							}
							
						}
						
						
					}
					
					data.getUnitList().add(unit);
					
				}
				
			}

            Map<String, Boolean> condition_tempGoods = new HashMap<String, Boolean>(); //查詢條件
            condition_tempGoods.put("GOODSTEMPLATEID", true);
            List<Map<String, Object>> goodsTemplates=MapDistinct.getMap(getQData, condition_tempGoods);
            if(CollUtil.isNotEmpty(goodsTemplates)){
                for (Map<String, Object> goodsTemplate : goodsTemplates){
					if(Check.Null(goodsTemplate.get("GOODSTEMPLATEID").toString())){
						continue;
					}
                    DCP_GoodsSetDetailRes.GoodsTemplateList goodsTemplateList = res.new GoodsTemplateList();
                    goodsTemplateList.setTemplateId(goodsTemplate.get("GOODSTEMPLATEID").toString());
                    goodsTemplateList.setTemplateName(goodsTemplate.get("GOODSTEMPLATENAME").toString());
                    data.getGoodsTemplateList().add(goodsTemplateList);
                }
            }

            Map<String, Boolean> condition_tempPorder = new HashMap<String, Boolean>(); //查詢條件
            condition_tempPorder.put("PTEMPLATENO", true);
            List<Map<String, Object>> pOrderTemplates=MapDistinct.getMap(getQData, condition_tempPorder);
            if(CollUtil.isNotEmpty(pOrderTemplates)){
                for (Map<String, Object> pOrderTemplate : pOrderTemplates){
					if(Check.Null(pOrderTemplate.get("PTEMPLATENO").toString())){
						continue;
					}
                    DCP_GoodsSetDetailRes.POrderTemplateList pOrderTemplateList = res.new POrderTemplateList();
                    pOrderTemplateList.setTemplateNo(pOrderTemplate.get("PTEMPLATENO").toString());
                    pOrderTemplateList.setTemplateName(pOrderTemplate.get("PTEMPLATENAME").toString());
                    data.getpOrderTemplateList().add(pOrderTemplateList);
                }
            }


            Map<String, Boolean> condition_tag = new HashMap<String, Boolean>(); //查詢條件
            condition_tag.put("TAGNO", true);
            List<Map<String, Object>> tags=MapDistinct.getMap(getQData, condition_tag);
            if(CollUtil.isNotEmpty(tags)){
                for (Map<String, Object> tag : tags){
					if(Check.Null(tag.get("TAGNO").toString())){
						continue;
					}
                    DCP_GoodsSetDetailRes.TagList singleTag = res.new TagList();
                    singleTag.setTagNo(tag.get("TAGNO").toString());
                    singleTag.setTagName(tag.get("TAGNAME").toString());
                    data.getTagList().add(singleTag);
                }
            }


        }
		
		res.setDatas(data);
		
		return res ;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	
	}
	
	@Override
	protected String getQuerySql(DCP_GoodsSetDetailReq req) throws Exception {
		String langtype=req.getLangType();
		String eId = req.geteId();
		StringBuffer sqlbuf=new StringBuffer();
		sqlbuf.append(" select "
				+ " a.eid,a.pluno,a.plutype,a.splittype,a.attrgroupid,a.category,a.brand,a.series,a.warmtype,"
				+ " a.shortcut_code,a.virtual,a.openprice,a.isweight,a.stockmanagetype,a.taxcode,a.memo,a.status,"
				+ " a.baseunit,a.price,a.supprice,a.sunit,a.bom_unit,a.prod_unit,a.punit,a.purunit,a.cunit,a.wunit,"
				+ " a.runit ,"
				+ " a.isbatch,"
				+ " a.shelflife,a.stockinvalidday,a.stockoutvalidday,a.checkvalidday,a.createopid,a.createopname,a.createtime,"
				+ " a.lastmodiopid,a.lastmodiopname,a.lastmoditime,a.tran_time,a.isholiday,a.restrictshop,a.packagetype,"
				+ " a.mainbarcode,a.maxorderspec,a.redisupdatesuccess,a.holidayenddate,a.holidaybegindate,a.holidaybillno,"
				+ " a.ishotgoods,a.own_goods,a.isdoublegoods,a.percentage_comm,a.specprice,a.specprom,a.weighplu,a.weighplutype,"
				+ " a.selfbuiltshopid,a.supplier,a.prod_shop,a.prod_hq,a.prod_oem,a.pluid,"  ///a.spec
				+ " B.LANG_TYPE LANGTYPE,B.Plu_Name,V.PLUBARCODE,V.FEATURENO,V.UNIT UNIT_BARCODE,V.UNITRATIO,V.STATUS BARCODE_STATUS,"
				+ " V.LEN,V.WIDTH,V.HEIGHT,V.VOLUMEUNIT VOLUMEUNIT_BARCODE,V1.UNAME VOLUMEUNITNAME_BARCODE,V.WEIGHT WEIGHT_BARCODE,V.WEIGHTUNIT WEIGHTUNIT_BARCODE,V2.UNAME WEIGHTUNITNAME_BARCODE, ");
		sqlbuf.append(" C.UNAME PUNIT_NAME,D.UNAME CUNIT_NAME,E.UNAME SUNIT_NAME,F.UNAME WUNIT_NAME,G.UNAME BOM_UNIT_NAME,BASE.UNAME BASE_UNIT_NAME,PROD.UNAME PROD_UNIT_NAME,PUR.UNAME PUR_UNIT_NAME,"
				+ "RUN.UNAME RUNIT_NAME ,");
		sqlbuf.append(" H.CATEGORY_NAME,I.BRAND_NAME,J.SERIES_NAME,M.TAXNAME,m2.taxname as inputTaxName,m3.taxrate as inputTaxRate,N.ATTRGROUPNAME,");
		sqlbuf.append(" P.ATTRID,PL.ATTRNAME,Q.ATTRVALUEID,QL.ATTRVALUENAME,");
		sqlbuf.append(" U.OUNIT,U.OQTY,U.QTY,U.UNIT,U.SUNIT_USE,U.PUNIT_USE,U.BOM_UNIT_USE,U.PROD_UNIT_USE,U.PURUNIT_USE,U.CUNIT_USE,U.WEIGHT,U.VOLUME,UL.LANG_TYPE LANGTYPE_SPEC,UL.SPEC, "
					+ " NVL(U.RUNIT_USE,'N') AS RUNIT_USE,");
		sqlbuf.append(" OU1.UNAME OUNIT_NAME,OU2.UNAME UNIT_NAME, ");
        sqlbuf.append(" A.SPLITTYPE,A.SHORTCUT_CODE," +
                "   A.MAINBARCODE,A.SOURCETYPE,A.SPEC as MAINSPEC,A.PICKUNIT,U3.UNAME AS PICKUNITNAME,A.SUPPLIER,A.PURPRICE,S.SNAME AS SUPPLIERNAME,A.PROCRATE,A.ISQUALITYCHECK,A.TESTMARKETSALEQTY,A.TESTMARKETSALEAMT,A.TESTMARKETGROSSAMT," +
                "   A.MINQTY,A.MAXQTY,A.MULQTY,A.BATCHRULES,A.BATCHSORTTYPE,A.ISSHELFLIFECHECK,A.PROD_SHOP,A.PROD_HQ,A.PROD_OEM,A.ISSENSITIVE,A.ISCOMBINEBATCH,A.MATERIALPROPERTIES,A.PRODUCTTIME," +
                "   A.PRODUCER,A.STORAGECON,A.MANUFACTURER,A.HOTLINE,A.INGRETABLE,A.NETCONTENT,A.FOODPROLICNUM,A.EATINGMETHOD,A.EXSTANDARD,A.PROPADDRESS, " +
                " ");
		sqlbuf.append(" a.CREATEOPID,EM1.op_NAME AS CREATEOPNAME,DD1.DEPARTNO AS CREATEDEPTID,DD1.DEPARTNAME AS CREATEDEPTNAME,A.CREATETIME,a.LASTMODIOPID,EM2.op_NAME AS LASTMODIOPNAME,A.LASTMODITIME, ");
		sqlbuf.append(" GTL.TEMPLATEID AS GOODSTEMPLATEID,GTL.TEMPLATENAME AS GOODSTEMPLATENAME,PT.PTEMPLATENO,PTM.PTEMPLATE_NAME AS PTEMPLATENAME,TDL.TAGNO,TDL.TAGNAME,V.BARCODETYPE,P.SORTID AS ATTRSORTID,a.INPUTTAXCODE  ");
		sqlbuf.append(" from DCP_GOODS A");
		sqlbuf.append(" LEFT JOIN DCP_GOODS_LANG B ON A.EID=B.EID AND A.PLUNO=B.PLUNO ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  C ON A.EID=C.EID AND A.PUNIT=C.UNIT AND C.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  D ON A.EID=D.EID AND A.CUNIT=D.UNIT AND D.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  E ON A.EID=E.EID AND A.SUNIT=E.UNIT AND E.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  F ON A.EID=F.EID AND A.WUNIT=F.UNIT AND F.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  G ON A.EID=G.EID AND A.BOM_UNIT=G.UNIT AND G.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  BASE ON A.EID=BASE.EID AND A.BASEUNIT=BASE.UNIT  AND BASE.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  PROD ON A.EID=PROD.EID AND A.PROD_UNIT=PROD.UNIT  AND PROD.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  PUR ON A.EID=PUR.EID AND A.PURUNIT=PUR.UNIT  AND PUR.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  RUN ON A.EID=RUN.EID AND A.RUNIT=RUN.UNIT  AND RUN.LANG_TYPE='"+langtype+"' ");		
		sqlbuf.append(" LEFT JOIN DCP_CATEGORY_LANG H ON A.EID=H.EID AND A.CATEGORY=H.CATEGORY  AND H.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_BRAND_LANG I ON A.EID=I.EID AND A.BRAND=I.BRANDNO  AND I.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_SERIES_LANG J ON A.EID=J.EID AND A.SERIES=J.SERIESNO AND J.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_TaxCategory_LANG M on A.EID=M.EID  and A.taxCode=M.taxCode  AND M.LANG_TYPE='"+langtype+"' ");
        sqlbuf.append(" LEFT JOIN DCP_TaxCategory_LANG M2 on A.EID=M2.EID  and A.inputTaxCode=M2.taxCode  AND M.LANG_TYPE='"+langtype+"' ");
        sqlbuf.append(" LEFT JOIN DCP_TaxCategory M3 on A.EID=M3.EID  and A.inputTaxCode=M3.taxCode   ");
		sqlbuf.append(" LEFT JOIN DCP_ATTRGROUP_LANG N on A.EID=N.EID  and A.ATTRGROUPID=N.ATTRGROUPID  AND N.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" Left join DCP_GOODS_ATTR P on A.EID=P.EID  and A.PLUNO= P.pluno ");
		sqlbuf.append(" LEFT JOIN DCP_ATTRIBUTION_LANG PL on A.EID=PL.EID  and P.ATTRID=PL.ATTRID  AND PL.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" left join DCP_GOODS_ATTR_VALUE Q on A.EID=Q.EID  and A.PLUNO= Q.pluno  AND P.ATTRID=Q.ATTRID");
		sqlbuf.append(" LEFT JOIN DCP_ATTRIBUTION_VALUE_LANG QL on A.EID=QL.EID AND Q.ATTRID=QL.ATTRID  and Q.ATTRVALUEID=QL.ATTRVALUEID AND QL.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_GOODS_UNIT U on A.EID=U.EID and A.PLUNO = U.PLUNO ");
		sqlbuf.append(" LEFT JOIN DCP_GOODS_UNIT_LANG UL on A.EID=UL.EID and A.PLUNO = UL.PLUNO and U.OUNIT=UL.OUNIT ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  OU1 ON A.EID=OU1.EID AND U.OUNIT=OU1.UNIT AND OU1.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  OU2 ON A.EID=OU2.EID AND U.UNIT=OU2.UNIT AND OU2.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" Left JOIN Dcp_Goods_Barcode V on A.EID=V.EID and A.PLUNO = V.PLUNO");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  V1 ON V.EID=V1.EID AND V.VOLUMEUNIT=V1.UNIT AND V1.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  V2 ON V.EID=V2.EID AND V.WEIGHTUNIT=V2.UNIT AND V2.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG  U3 ON A.EID=U3.EID AND A.PICKUNIT=U3.UNIT AND U3.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" left join DCP_BIZPARTNER S ON A.EID=S.EID  AND A.SUPPLIER=S.BIZPARTNERNO ");
        sqlbuf.append(" LEFT JOIN PLATFORM_STAFFS_LANG EM1 ON EM1.EID=A.EID AND A.CREATEOPID=EM1.opno and em1.lang_type='"+req.getLangType()+"'");
		sqlbuf.append(" LEFT JOIN PLATFORM_STAFFS_LANG EM2 ON EM2.EID=A.EID AND A.LASTMODIOPID=EM2.opno anD EM2.LANG_TYPE='"+req.getLangType()+"' ");
		sqlbuf.append(" left join DCP_DEPARTMENT_LANG DD1 ON DD1.EID=A.EID AND DD1.DEPARTNO=A.CREATEDEPTID AND DD1.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" left join DCP_GOODSTEMPLATE_GOODS GT ON GT.EID=A.EID AND GT.PLUNO=A.PLUNO " +
				" left join DCP_GOODSTEMPLATE_LANG GTL ON GTL.EID=GT.EID AND GTL.TEMPLATEID=GT.TEMPLATEID AND GTL.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append("left join DCP_PTEMPLATE_DETAIL PT ON PT.EID=A.EID AND PT.PLUNO=A.PLUNO and PT.DOC_TYPE='0'" +
				" LEFT JOIN DCP_PTEMPLATE PTM ON PTM.EID=A.EID AND PTM.PTEMPLATENO=PT.PTEMPLATENO and PTM.DOC_TYPE='0' ");
		sqlbuf.append(" left join DCP_TAGTYPE_DETAIL TD ON TD.EID=A.EID AND TD.ID=A.PLUNO" +
				" LEFT JOIN DCP_TAGTYPE_LANG TDL ON TDL.EID=TD.EID AND TDL.TAGNO=TD.TAGNO AND TDL.LANG_TYPE='"+langtype+"' ");
		sqlbuf.append(" where a.eid='"+eId+"' and A.pluno='"+req.getRequest().getPluNo()+"'  ");
		sqlbuf.append(" order by P.SORTID,v.sortid ");
		
		return sqlbuf.toString();
		
	}
	
}
