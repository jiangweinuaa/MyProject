package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_GoodsSetCreateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsSetCreateReq.*;
import com.dsc.spos.json.cust.res.DCP_GoodsSetCreateRes;
import com.dsc.spos.json.cust.res.DCP_GoodsSetCreateRes.level1Elm;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_GoodsSetCreate extends SPosAdvanceService<DCP_GoodsSetCreateReq, DCP_GoodsSetCreateRes> {
    
    @Override
    protected void processDUID(DCP_GoodsSetCreateReq req, DCP_GoodsSetCreateRes res) throws Exception {

        String pluCodeRule=PosPub.getPARA_SMS(dao, req.geteId(), "", "PluCodeRule");
        String pluBarCodeRule=PosPub.getPARA_SMS(dao, req.geteId(), "", "PluBarCodeRule");


        //清缓存
        String posUrl = PosPub.getPOS_INNER_URL(req.geteId());
        String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
                " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
        List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
        String apiUserCode = "";
        String apiUserKey = "";
        if (result != null && result.size() == 2) {
            for (Map<String, Object> map : result) {
                if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
                    apiUserCode = map.get("ITEMVALUE").toString();
                }else{
                    apiUserKey = map.get("ITEMVALUE").toString();
                }
            }
        }
        PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,req.geteId());
        levelRequest requestModel = req.getRequest();
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String curLangType = req.getLangType();
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();

        String pluNo = requestModel.getPluNo();
        String pluId = requestModel.getPluId();
        String selfBuiltShopId = requestModel.getSelfBuiltShopId();


        
        if (Check.Null(pluNo)){
            pluNo = getPluNoNew(req.geteId(),req.getRequest().getCategory(),pluCodeRule);
        }
        //商品资料档里面的商品编号必须小写处理 by jinzma 20220427  参阅商品查询服务 ///要货单商品导入 商品编号要小写处理，有客户商品编号带字母
        //pluNo = pluNo.toLowerCase();
        
        //检查DCP_GOODS
        String sql = " select pluno,pluid from dcp_goods where eid='"+req.geteId()+"' and (pluno='"+pluNo+"' or pluid='"+pluId+"') ";
        List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);
        if(getQData_check!=null && !getQData_check.isEmpty()) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编码或商品ID已存在!");
        }
        
        //多语言档在插入前直接删除，此次判断没必要了    by jinzma 20220422
		/*	sql = " select pluno from DCP_goods_lang where EID='"+req.geteId()+"' "
				+ " and pluno ='"+pluNo+"' ";
		getQData_check = this.doQueryData(sql,null);
		if(getQData_check!=null && getQData_check.isEmpty()==false) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品多语言信息已存在记录!");
		}*/
        
        
        
        
        
        
        
        String pluType = requestModel.getPluType();
        String attrGroupId = requestModel.getAttrGroupId();
        List<attr> attrList = requestModel.getAttrList();
        List<barcode> barcodeList = requestModel.getBarcodeList();
        
        String shortcutCode = requestModel.getShortcutCode();
        String warmType = requestModel.getWarmType();
        String category = requestModel.getCategory();
        String brandNo = requestModel.getBrandNo();
        String seriesNo = requestModel.getSeriesNo();
        String taxCode = requestModel.getTaxCode();
        String inputTaxCode = requestModel.getInputTaxCode();
        String virtual = requestModel.getVirtual();
        String openPrice = requestModel.getOpenPrice();
        String isWeight = requestModel.getIsWeight();
        String stockManageType = requestModel.getStockManageType();
        String memo = requestModel.getMemo();
        String status = requestModel.getStatus();
        String price = requestModel.getPrice();
        String supPrice = requestModel.getSupPrice();
        String baseUnit = requestModel.getBaseUnit();//基准单位编码
        String pUnit = requestModel.getpUnit();//要货单位编码
        String purUnit = requestModel.getPurUnit();//采购单位编码
        String sUnit = requestModel.getsUnit();//默认销售单位编码
        String cUnit = requestModel.getcUnit();//盘点单位编码
        String wUnit = requestModel.getwUnit();//库存单位编码
        String bomUnit = requestModel.getBomUnit();//配方单位编码
        String prodUnit = requestModel.getProdUnit();//成品的生产单位
        String isHoliday = requestModel.getIsHoliday();
        String isHotGoods = requestModel.getIsHotGoods();
        String isOwnGoods = requestModel.getIsOwnGoods();
        String isDoubleGoods = requestModel.getIsDoubleGoods();
        String rUnit=requestModel.getrUnit();

        //①基础信息：splitType, shortCutCode, mainBarcode, sourceType, spec,
        String splitType = requestModel.getSplitType();
        String shortCutCode = requestModel.getShortCutCode();
        String mainBarcode = requestModel.getMainBarcode();
        String sourceType = requestModel.getSourceType();
        String mainSpec = requestModel.getSpec();
        //②单位信息：pickUnit
        String pickUnit = requestModel.getPickUnit();
        //③采购设置：supplier, procRate, isQualityCheck
        String supplier = requestModel.getSupplier();
        String procRate = requestModel.getProcRate();
        String isQualityCheck = requestModel.getIsQualityCheck();
        //④销售设置：testMarketSaleQty, testMarketSaleAmt, testMarketGrossAmt, goodsTemplateList[],barcodeList[]:增加传参barcodeType,取消pluBarcode必传
        String testMarketSaleQty = requestModel.getTestMarketSaleQty();
        String testMarketSaleAmt = requestModel.getTestMarketSaleAmt();
        String testMarketGrossAmt = requestModel.getTestMarketGrossAmt();
        List<GoodsTemplateList> goodsTemplateList = requestModel.getGoodsTemplateList();
        //⑤要货设置：minQty, maxQty, mulQty,  pOrderTemplateList[],
        String minQty = requestModel.getMinQty();
        String maxQty = requestModel.getMaxQty();
        String mulQty = requestModel.getMulQty();
        List<POrderTemplateList> pOrderTemplateList = requestModel.getpOrderTemplateList();
        //⑥批号管理：batchRules, batchSortType,
        String batchRules = requestModel.getBatchRules();
        String batchSortType = requestModel.getBatchSortType();
        //⑦效期管理：isShelfLifeCheck,
        String isShelfLifeCheck = requestModel.getIsShelfLifeCheck();
        //⑧生管设置：prodShop, prodHQ, prodOEM, isSensitive, isCombineBatch, materialProperties, productTime,
        String prodShop = requestModel.getProdShop();
        String prodHQ = requestModel.getProdHQ();
        String prodOEM = requestModel.getProdOEM();
        String isSensitive = requestModel.getIsSensitive();
        String isCombineBatch = requestModel.getIsCombineBatch();
        String materialProperties = requestModel.getMaterialProperties();
        String productTime = requestModel.getProductTime();
        //⑨溯源信息：producer, storageCon, manufacturer, hotLine, ingreTable, netContent, foodProLicNum, eatingMethod, foodProLicNum, exStandard, prodAddress,
        String producer = requestModel.getProducer();
        String storageCon = requestModel.getStorageCon();
        String manufacturer = requestModel.getManufacturer();
        String hotLine = requestModel.getHotLine();
        String ingreTable = requestModel.getIngreTable();
        String netContent = requestModel.getNetContent();
        String foodProLicNum = requestModel.getFoodProLicNum();
        String eatingMethod = requestModel.getEatingMethod();
        String exStandard = requestModel.getExStandard();
        String propAddress = requestModel.getPropAddress();
        String purPrice = requestModel.getPurPrice();
        //⑩商品标签：tagList[]
        List<TagList> tagList = requestModel.getTagList();


        //【ID1023729】货郎3.0加盟店可以自己采购商品，定价，做促销，管理自己的会员（后端云中台服务） by jinzma 20220216
        //自建商品逻辑判断
        if (!Check.Null(selfBuiltShopId)){
            if (!selfBuiltShopId.equals(req.getShopId())){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "自建商品所属门店非当前门店,不允许修改");
            }else{
                sql = " select distinct b.templateid from ("
                        + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                        + " from dcp_goodstemplate a"
                        + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+req.getShopId()+"'"
                        + " where a.eid='"+req.geteId()+"' and a.status='100' and (a.templatetype='SHOP' and c2.id is not null)"
                        + " ) a"
                        + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                        + " where a.rn=1 "
                        + " ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);  //添加自建商品ERP必须先下传门店级的生效商品模板
                if (getQData==null || getQData.isEmpty()){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "商品模板适用范围不是门店级，请联系总部重新下发");
                }else{
                    String templateId = getQData.get(0).get("TEMPLATEID").toString();
                    
                    sql=" select isallowselfbuilt,category from dcp_category"
                            + " where eid='"+req.geteId()+"' and (up_category='"+category+"' or category='"+category+"')";
                    getQData.clear();
                    getQData = this.doQueryData(sql, null);
                    
                    if (getQData!=null && !getQData.isEmpty()){
                        if (getQData.size()==1){
                            if (!category.equals(getQData.get(0).get("CATEGORY"))){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "商品所属分类非末级分类,请重新选择正确分类");
                            }else{
                                String isAllowSelfBuilt = getQData.get(0).get("ISALLOWSELFBUILT").toString();
                                if (Check.Null(isAllowSelfBuilt) || isAllowSelfBuilt.equals("0")){
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "商品所属分类不允许自建商品,请重新选择正确分类");
                                }
                            }
                        }else{
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "商品所属分类非末级分类,请重新选择正确分类");
                        }
                    }else{
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "商品分类在dcp_category表中不存在!");
                    }
                    
                    
                    //自建商品基础资料删除后未删除对应的门店商品模板，所以此处判断商品模板是否存在，存在就不再插入  by jinzma 20220222
                    sql = " select * from dcp_goodstemplate_goods where eid='"+req.geteId()+"' and templateid='"+templateId+"' and pluno='"+pluNo+"' ";
                    getQData.clear();
                    getQData = this.doQueryData(sql, null);
                    if (getQData==null || getQData.isEmpty()){
                        //新增商品模板
                        String[] columns ={
                                "EID","TEMPLATEID","PLUNO","WARNINGQTY","SAFEQTY",
                                "CANSALE","CANFREE","CANRETURN","CANORDER","CANPURCHASE","CANREQUIRE","CANREQUIREBACK","CANESTIMATE",
                                "MINQTY","MAXQTY","MULQTY",
                                "IS_AUTO_SUBTRACT","CLEARTYPE","STATUS","LASTMODITIME",
                                "ITEM","REDISUPDATESUCCESS","ISNEWGOODS","ISALLOT"
                        };
                        
                        DataValue[] insValue = new DataValue[]{
                                new DataValue(req.geteId(), Types.VARCHAR),   //企业编号
                                new DataValue(templateId, Types.VARCHAR),     //模板编号
                                new DataValue(pluNo, Types.VARCHAR),          //商品编号
                                new DataValue("0", Types.VARCHAR),     //警戒库存量
                                new DataValue("0", Types.VARCHAR),     //安全库存量
                                new DataValue("Y", Types.VARCHAR),     //可销售
                                new DataValue("N", Types.VARCHAR),     //可免单
                                new DataValue("Y", Types.VARCHAR),     //可销退
                                new DataValue("N", Types.VARCHAR),     //可预订
                                new DataValue("Y", Types.VARCHAR),     //可采购
                                new DataValue("N", Types.VARCHAR),     //可要货
                                new DataValue("N", Types.VARCHAR),     //可退仓
                                new DataValue("N", Types.VARCHAR),     //可预估
                                new DataValue("0", Types.VARCHAR),     //最小量
                                new DataValue("0", Types.VARCHAR),     //最大量
                                new DataValue("0", Types.VARCHAR),     //倍量
                                new DataValue("N", Types.VARCHAR),     //是否自动倒扣料
                                new DataValue("DAY", Types.VARCHAR),   //估清方式：N-不估清PERIOD-当餐DAY-当天 默认DAY
                                new DataValue("100", Types.VARCHAR),   //状态
                                new DataValue(lastmoditime,Types.DATE),      //最后修改时间
                                new DataValue("0", Types.VARCHAR),    //项次
                                new DataValue("N", Types.VARCHAR),    //同步缓存是否成功
                                new DataValue("N", Types.VARCHAR),    //是否新品
                                new DataValue("N", Types.VARCHAR)     //是否可调拨
                        };
                        InsBean ib = new InsBean("DCP_GOODSTEMPLATE_GOODS", columns);
                        ib.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib));
                        
                    }
                }
            }
        }
        
        
        List<unit> unitList = requestModel.getUnitList();
        String isBatch = requestModel.getIsBatch();
        int shelfLife = 0;
        if (PosPub.isNumeric(requestModel.getShelfLife())){
            shelfLife = Integer.parseInt(requestModel.getShelfLife());
        }
        
        int stockInValidDay = 0;
        if (PosPub.isNumeric(requestModel.getStockInValidDay())){
            stockInValidDay = Integer.parseInt(requestModel.getStockInValidDay());
        }
        
        int stockOutValidDay = 0;
        if (PosPub.isNumeric(requestModel.getStockOutValidDay())){
            stockOutValidDay = Integer.parseInt(requestModel.getStockOutValidDay());
        }
        
        int checkValidDay = 0;
        if (PosPub.isNumeric(requestModel.getCheckValidDay())){
            checkValidDay = Integer.parseInt(requestModel.getCheckValidDay());
        }
        
        
        //删除商品多语言档
        DelBean db1 = new DelBean("DCP_GOODS_LANG");
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db1.addCondition("PLUNO", new DataValue(pluNo,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        
        List<pluName_lang> pluNameLangs = req.getRequest().getPluName_lang();
        String[] columns_GoodsLang = {
                "EID",
                "PLUNO",
                "LANG_TYPE",
                "PLU_NAME",
                "LASTMODITIME",
        };
        for (pluName_lang pluName_lang : pluNameLangs) {
            String langType = pluName_lang.getLangType();
            String spec = "";
			/*List<spec_lang> specLangs = req.getRequest().getSpec_lang();
			if(specLangs!=null&&specLangs.isEmpty()==false)
			{
				for (spec_lang spec_lang : specLangs)
				{
			     if(langType.equals(spec_lang.getLang_type()))
			     {
			    	 spec = spec_lang.getName();
			    	 break;
			     }

				}

			}*/
            
            DataValue[] insValue1 = new DataValue[] {
                    new DataValue(req.geteId(), Types.VARCHAR),
                    new DataValue(pluNo, Types.VARCHAR),
                    new DataValue(langType, Types.VARCHAR),
                    new DataValue(pluName_lang.getName(), Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE)
            };
            
            InsBean ib1 = new InsBean("DCP_GOODS_LANG", columns_GoodsLang);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
        }
        
        //DCP_GOODS_BARCODE
        String[] columns_barcode =
                {
                        "EID",
                        "PLUNO",
                        "PLUBARCODE",
                        "UNIT",
                        "FEATURENO",
                        "UNITRATIO",
                        "SORTID",
                        "STATUS",
                        "LASTMODITIME",
                        "LEN",
                        "WIDTH",
                        "HEIGHT",
                        "VOLUMEUNIT",
                        "WEIGHT",
                        "WEIGHTUNIT",
                        "BARCODETYPE"
                };
        
        int i = 1;
        String oldPluBarCode="";
        List allBarCode=new ArrayList();
        for (barcode par : barcodeList) {
            if(Check.Null(par.getFeatureNo())){
                par.setFeatureNo(" ");
            }
            String  pluBarcode = par.getPluBarcode();

            if("2".equals(par.getBarcodeType())&&Check.Null(pluBarcode)){
                //自编码
                pluBarcode=PosPub.getPluBarCode(dao,eId,"",pluNo,oldPluBarCode);
            }
            oldPluBarCode=pluBarcode;
            if(!allBarCode.contains(pluBarcode)){
                allBarCode.add(pluBarcode);
            }

            String featureNo =" ";//特征码 普通商品和套餐商品为一个空格，FEATURE商品：示例“size-xl”，使用短线连接规格的编码
            String sortId = i+"";
            String status_barcode = par.getStatus();
            if(pluType.equalsIgnoreCase("FEATURE"))
            {
                featureNo = par.getFeatureNo();
            }
            String curUnit = par.getUnit();
            BigDecimal unitRatio = new BigDecimal("1");
            boolean isExistUnitRatio = false;
            for(unit item_unit : unitList) {
                String oUnit = item_unit.getoUnit();
                if(oUnit==null||oUnit.isEmpty()) {
                    continue;
                }
                if(curUnit.equals(oUnit)==false) {
                    continue;
                }
                try {
                    
                    String jizhunUnit = item_unit.getUnit();
                    if(curUnit.equals(jizhunUnit)) {
                        unitRatio = new BigDecimal("1");
                    } else {
                        BigDecimal oQty_b = new BigDecimal(item_unit.getoQty());
                        BigDecimal qty_b = new BigDecimal(item_unit.getQty());
                        unitRatio = qty_b.divide(oQty_b,8, RoundingMode.HALF_UP);
                    }
                    isExistUnitRatio = true;
                    break;
                } catch (Exception e) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品条码pluBarcode=" + pluBarcode +" 特征码featureNo="+featureNo+" 计算单位换算异常："+e.getMessage());
                }
                
            }
            
            String len = "0";
            try {
                len = par.getLen();
                Double.parseDouble(len);
                
            } catch (Exception e) {
                len = "0";
            }
            String width = "0";
            try {
                width = par.getWidth();
                Double.parseDouble(width);
                
            } catch (Exception e) {
                width = "0";
            }
            String height = "0";
            try {
                height = par.getHeight();
                Double.parseDouble(height);
                
            } catch (Exception e) {
                height = "0";
            }
            String weight = "0";
            try {
                weight = par.getWeight();
                Double.parseDouble(weight);
            } catch (Exception e) {
                weight = "0";
                
            }
            
            if(!isExistUnitRatio) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品条码pluBarcode=" + pluBarcode +" 特征码featureNo="+featureNo+" 找不到对应的换算关系");
            }
            
            DataValue[] insValue1 = null;
            insValue1 = new DataValue[]
                    {
                            new DataValue(req.geteId(), Types.VARCHAR),
                            new DataValue(pluNo, Types.VARCHAR),
                            new DataValue(pluBarcode, Types.VARCHAR),
                            new DataValue(par.getUnit(), Types.VARCHAR),
                            new DataValue(featureNo, Types.VARCHAR),
                            new DataValue(unitRatio, Types.VARCHAR),
                            new DataValue(sortId, Types.VARCHAR),
                            new DataValue(status_barcode, Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE),
                            new DataValue(len, Types.VARCHAR),
                            new DataValue(width, Types.VARCHAR),
                            new DataValue(height, Types.VARCHAR),
                            new DataValue(par.getVolumeUnit(), Types.VARCHAR),
                            new DataValue(weight, Types.VARCHAR),
                            new DataValue(par.getWeightUnit(), Types.VARCHAR),
                            new DataValue(par.getBarcodeType(), Types.VARCHAR)
                        
                    };
            
            InsBean ib1 = new InsBean("DCP_GOODS_BARCODE", columns_barcode);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
            i++;
        }

        List<String> barcodeFeatureList=new ArrayList();//记录属性特征码值

        //属性，特征码
        if(attrList!=null&&attrList.isEmpty()==false){
            i = 1;
            String[] columns_attr = {
                    "EID",
                    "PLUNO",
                    "ATTRID",
                    "SORTID",
                    "LASTMODITIME"
                
            };
            
            
            
            for (attr item_attr : attrList) //最多3条
            {
                String attrId = item_attr.getAttrId();
                String attrName = item_attr.getAttrName();
                String sortId_attr = i+"";
                
                DataValue[] insValue1 = null;
                
                insValue1 = new DataValue[]
                        {
                                new DataValue(req.geteId(), Types.VARCHAR),
                                new DataValue(pluNo, Types.VARCHAR),
                                new DataValue(attrId, Types.VARCHAR),
                                new DataValue(sortId_attr, Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };
                
                InsBean ib1 = new InsBean("DCP_GOODS_ATTR", columns_attr);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));
                i++;
                
                List<attrValue> attrValueList = item_attr.getAttrValueList();
                if(attrValueList!=null&&attrValueList.isEmpty()==false) {
                    String[] columns_attrValue = {
                            "EID",
                            "PLUNO",
                            "ATTRID",
                            "ATTRVALUEID",
                            "LASTMODITIME"
                        
                    };
                    
                    String[] columns_feature = {
                            "EID",
                            "PLUNO",
                            "FEATURENO",
                            "FEATURENAME",
                            "STATUS",
                            "MEMO",
                            "LASTMODITIME"
                        
                    };
                    
                    for(attrValue item_attrValue : attrValueList) {
                        String attrValueId = item_attrValue.getAttrValueId();
                        String attrValueName = item_attrValue.getAttrValueName();
                        
                        DataValue[] insValue_attrValue = null;
                        
                        insValue_attrValue = new DataValue[]{
                                new DataValue(req.geteId(), Types.VARCHAR),
                                new DataValue(pluNo, Types.VARCHAR),
                                new DataValue(attrId, Types.VARCHAR),
                                new DataValue(attrValueId, Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };
                        
                        InsBean ib_attrValue = new InsBean("DCP_GOODS_ATTR_VALUE", columns_attrValue);
                        ib_attrValue.addValues(insValue_attrValue);
                        this.addProcessData(new DataProcessBean(ib_attrValue));


					 /*String featureNo = attrId +"-"+attrValueId;//size-XL
					 String featureName = attrName +"-"+attrValueName;//颜色-黑色
					 String memo_feature = "";
					 String status_feature = "100";


					 DataValue[] insValue_feature = null;

					 insValue_feature = new DataValue[]
								{
										new DataValue(req.geteId(), Types.VARCHAR),
										new DataValue(pluNo, Types.VARCHAR),
										new DataValue(featureNo, Types.VARCHAR),
										new DataValue(featureName, Types.VARCHAR),
										new DataValue(status_feature, Types.VARCHAR),
										new DataValue(memo_feature, Types.VARCHAR),
										new DataValue(lastmoditime, Types.DATE)
								};

						InsBean ib_feature = new InsBean("DCP_GOODS_FEATURE", columns_feature);
						ib_feature.addValues(insValue_feature);
						this.addProcessData(new DataProcessBean(ib_feature));*/
                    
                    }
                }
            }
            
            //属性值排列组合
            attr attr1 = attrList.get(0);
            attr attr2 = null;
            if(attrList.size()>1)
            {
                attr2 = attrList.get(1);
            }
            attr attr3 = null;
            if(attrList.size()>2)
            {
                attr2 = attrList.get(2);
            }
            String attr1Id = attr1.getAttrId();
            String attr1Name = attr1.getAttrName();
            List<attrValue> attr1ValueList = attr1.getAttrValueList();
            if(attr1ValueList!=null&&attr1ValueList.isEmpty()==false) {
                String[] columns_feature = {
                        "EID",
                        "PLUNO",
                        "FEATURENO",
                        //"FEATURENAME",
                        "ATTRID1",
                        "ATTRVALUEID1",
                        "ATTRID2",
                        "ATTRVALUEID2",
                        "ATTRID3",
                        "ATTRVALUEID3",
                        "STATUS",
                        "MEMO",
                        "LASTMODITIME"
                    
                };
                
                String[] columns_feature_lang =
                        {
                                "EID",
                                "PLUNO",
                                "FEATURENO",
                                "LANG_TYPE",
                                "FEATURENAME",
                                "LASTMODITIME"
                            
                        };
                
                
                String joinString="_";//连接符
                for (attrValue attr1Value : attr1ValueList) {
                    String attr1ValueId = attr1Value.getAttrValueId();
                    String attr1ValueName = attr1Value.getAttrValueName();
                    String featureNo = attr1ValueId;//size-XL
                    String featureName = attr1ValueName;//颜色-黑色
                    String memo_feature = "";
                    String status_feature = "100";
                    
                    if(attr2!=null&&attr2.getAttrValueList()!=null&&attr2.getAttrValueList().isEmpty()==false) {
                        String attr2Id = attr2.getAttrId();
                        String attr2Name = attr2.getAttrName();
                        List<attrValue> attr2ValueList = attr2.getAttrValueList();
                        for (attrValue attr2Value : attr2ValueList) {
                            
                            String attr2ValueId = attr2Value.getAttrValueId();
                            String attr2ValueName = attr2Value.getAttrValueName();
                            featureNo = attr1ValueId+ joinString+attr2ValueId;
                            featureName = attr1ValueName+ joinString+ attr2ValueName;
                            
                            if(attr3!=null&&attr3.getAttrValueList()!=null&&attr3.getAttrValueList().isEmpty()==false) {
                                String attr3Id = attr3.getAttrId();
                                String attr3Name = attr3.getAttrName();
                                List<attrValue> attr3ValueList = attr3.getAttrValueList();
                                for (attrValue attr3Value : attr3ValueList) {
                                    String attr3ValueId = attr3Value.getAttrValueId();
                                    String attr3ValueName = attr3Value.getAttrValueName();
                                    featureNo = attr1ValueId+ joinString+attr2ValueId+joinString+attr3ValueId;
                                    featureName = attr1ValueName+ joinString+ attr2ValueName+joinString+ attr3ValueName;
                                    
                                    DataValue[] insValue_feature = null;
                                    insValue_feature = new DataValue[]
                                            {
                                                    new DataValue(req.geteId(), Types.VARCHAR),
                                                    new DataValue(pluNo, Types.VARCHAR),
                                                    new DataValue(featureNo, Types.VARCHAR),
                                                    //new DataValue(featureName, Types.VARCHAR),
                                                    new DataValue(attr1Id, Types.VARCHAR),
                                                    new DataValue(attr1ValueId, Types.VARCHAR),
                                                    new DataValue(attr2Id, Types.VARCHAR),
                                                    new DataValue(attr2ValueId, Types.VARCHAR),
                                                    new DataValue(attr3Id, Types.VARCHAR),
                                                    new DataValue(attr3ValueId, Types.VARCHAR),
                                                    new DataValue(status_feature, Types.VARCHAR),
                                                    new DataValue(memo_feature, Types.VARCHAR),
                                                    new DataValue(lastmoditime, Types.DATE)
                                            };
                                    
                                    InsBean ib_feature = new InsBean("DCP_GOODS_FEATURE", columns_feature);
                                    ib_feature.addValues(insValue_feature);
                                    this.addProcessData(new DataProcessBean(ib_feature));
                                    
                                    
                                    DataValue[] insValue_feature_lang = null;
                                    insValue_feature_lang = new DataValue[]
                                            {
                                                    new DataValue(req.geteId(), Types.VARCHAR),
                                                    new DataValue(pluNo, Types.VARCHAR),
                                                    new DataValue(featureNo, Types.VARCHAR),
                                                    new DataValue(curLangType, Types.VARCHAR),
                                                    new DataValue(featureName, Types.VARCHAR),
                                                    new DataValue(lastmoditime, Types.DATE)
                                            };
                                    
                                    InsBean ib_feature_lang = new InsBean("DCP_GOODS_FEATURE_LANG", columns_feature_lang);
                                    ib_feature_lang.addValues(insValue_feature_lang);
                                    this.addProcessData(new DataProcessBean(ib_feature_lang));
                                    if(!barcodeFeatureList.contains(attr1ValueId+"_"+attr2ValueId+"_"+attr3ValueId)){
                                        barcodeFeatureList.add(attr1ValueId+"_"+attr2ValueId+"_"+attr3ValueId);
                                    }
                                    
                                }
                            } else {
                                //只有二层
                                DataValue[] insValue_feature = null;
                                
                                insValue_feature = new DataValue[]
                                        {
                                                new DataValue(req.geteId(), Types.VARCHAR),
                                                new DataValue(pluNo, Types.VARCHAR),
                                                new DataValue(featureNo, Types.VARCHAR),
                                                //new DataValue(featureName, Types.VARCHAR),
                                                new DataValue(attr1Id, Types.VARCHAR),
                                                new DataValue(attr1ValueId, Types.VARCHAR),
                                                new DataValue(attr2Id, Types.VARCHAR),
                                                new DataValue(attr2ValueId, Types.VARCHAR),
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue(status_feature, Types.VARCHAR),
                                                new DataValue(memo_feature, Types.VARCHAR),
                                                new DataValue(lastmoditime, Types.DATE)
                                        };
                                
                                InsBean ib_feature = new InsBean("DCP_GOODS_FEATURE", columns_feature);
                                ib_feature.addValues(insValue_feature);
                                this.addProcessData(new DataProcessBean(ib_feature));
                                
                                
                                
                                DataValue[] insValue_feature_lang = null;
                                
                                insValue_feature_lang = new DataValue[]
                                        {
                                                new DataValue(req.geteId(), Types.VARCHAR),
                                                new DataValue(pluNo, Types.VARCHAR),
                                                new DataValue(featureNo, Types.VARCHAR),
                                                new DataValue(curLangType, Types.VARCHAR),
                                                new DataValue(featureName, Types.VARCHAR),
                                                new DataValue(lastmoditime, Types.DATE)
                                        };
                                
                                InsBean ib_feature_lang = new InsBean("DCP_GOODS_FEATURE_LANG", columns_feature_lang);
                                ib_feature_lang.addValues(insValue_feature_lang);
                                this.addProcessData(new DataProcessBean(ib_feature_lang));

                                if(!barcodeFeatureList.contains(attr1ValueId+"_"+attr2ValueId)){
                                    barcodeFeatureList.add(attr1ValueId+"_"+attr2ValueId);
                                }
                            }
                            
                        }
                    } else {
                        //只有一层
                        DataValue[] insValue_feature = null;
                        
                        insValue_feature = new DataValue[]
                                {
                                        new DataValue(req.geteId(), Types.VARCHAR),
                                        new DataValue(pluNo, Types.VARCHAR),
                                        new DataValue(featureNo, Types.VARCHAR),
                                        //new DataValue(featureName, Types.VARCHAR),
                                        new DataValue(attr1Id, Types.VARCHAR),
                                        new DataValue(attr1ValueId, Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(status_feature, Types.VARCHAR),
                                        new DataValue(memo_feature, Types.VARCHAR),
                                        new DataValue(lastmoditime, Types.DATE)
                                };
                        
                        InsBean ib_feature = new InsBean("DCP_GOODS_FEATURE", columns_feature);
                        ib_feature.addValues(insValue_feature);
                        this.addProcessData(new DataProcessBean(ib_feature));
                        
                        
                        DataValue[] insValue_feature_lang = null;
                        
                        insValue_feature_lang = new DataValue[]
                                {
                                        new DataValue(req.geteId(), Types.VARCHAR),
                                        new DataValue(pluNo, Types.VARCHAR),
                                        new DataValue(featureNo, Types.VARCHAR),
                                        new DataValue(curLangType, Types.VARCHAR),
                                        new DataValue(featureName, Types.VARCHAR),
                                        new DataValue(lastmoditime, Types.DATE)
                                };
                        
                        InsBean ib_feature_lang = new InsBean("DCP_GOODS_FEATURE_LANG", columns_feature_lang);
                        ib_feature_lang.addValues(insValue_feature_lang);
                        this.addProcessData(new DataProcessBean(ib_feature_lang));

                        if(!barcodeFeatureList.contains(attr1ValueId)){
                            barcodeFeatureList.add(attr1ValueId);
                        }
                    }
                    
                }
                
            }
            
            
            
        }

        //先生成一条pluno的
        if(CollUtil.isEmpty(barcodeList)){

            int barCodeSortId=1;

            //按照品号生成一笔条码数据写入DCP_GOODS_BARCODE;
            if(!allBarCode.contains(pluNo)){
                allBarCode.add(pluNo);
            }
            DataValue[] insValue1 = null;
            insValue1 = new DataValue[]
                    {
                            new DataValue(req.geteId(), Types.VARCHAR),
                            new DataValue(pluNo, Types.VARCHAR),
                            new DataValue(pluNo, Types.VARCHAR),
                            new DataValue(baseUnit, Types.VARCHAR),
                            new DataValue(" ", Types.VARCHAR),
                            new DataValue("1", Types.VARCHAR),
                            new DataValue(barCodeSortId, Types.VARCHAR),
                            new DataValue("100", Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("2", Types.VARCHAR)

                    };

            InsBean ib1 = new InsBean("DCP_GOODS_BARCODE", columns_barcode);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));

            if(pluType.equalsIgnoreCase("FEATURE")){

                //如果只生成一条 则用pluno作为barcode  否则编码
                String oldBarCode="";
                for (String bf :barcodeFeatureList){
                    String pluBarcode=PosPub.getPluBarCode(dao,eId,"",pluNo,oldBarCode);
                    if(!allBarCode.contains(pluBarcode)){
                        allBarCode.add(pluBarcode);
                    }
                    barCodeSortId++;
                    DataValue[] insValue2 = null;
                    insValue2 = new DataValue[]
                            {
                                    new DataValue(req.geteId(), Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(pluBarcode, Types.VARCHAR),
                                    new DataValue(baseUnit, Types.VARCHAR),
                                    new DataValue(bf, Types.VARCHAR),
                                    new DataValue("1", Types.VARCHAR),
                                    new DataValue(barCodeSortId, Types.VARCHAR),
                                    new DataValue("100", Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE),
                                    new DataValue("0", Types.VARCHAR),
                                    new DataValue("0", Types.VARCHAR),
                                    new DataValue("0", Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue("0", Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue("2", Types.VARCHAR)

                            };

                    InsBean ib2 = new InsBean("DCP_GOODS_BARCODE", columns_barcode);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));
                    oldBarCode=pluBarcode;
                }

            }

        }

        if(Check.Null(mainBarcode)){
            if(allBarCode.contains(pluNo)){
                mainBarcode=pluNo;
            }else if(allBarCode.size()>0){
                mainBarcode=allBarCode.get(0).toString();
            }
        }

        //单位
        String[] columns_unit =
                {
                        "EID",
                        "PLUNO",
                        "OQTY",
                        "OUNIT",
                        "QTY",
                        "UNIT",
                        "SUNIT_USE",
                        "PUNIT_USE",
                        "BOM_UNIT_USE",
                        "PROD_UNIT_USE",
                        "PURUNIT_USE",
                        "CUNIT_USE",
                        "RUNIT_USE",
                        "WEIGHT",
                        "VOLUME",
                        "UNITRATIO",
                        "LASTMODITIME"
                    
                };
        
        for(unit item_unit : unitList) {
            String oUnit = item_unit.getoUnit();
            DataValue[] insValue1 = null;
            BigDecimal unitRatio = new BigDecimal("0");
            try {
                BigDecimal oQty_b = new BigDecimal(item_unit.getoQty());
                BigDecimal qty_b = new BigDecimal(item_unit.getQty());
                unitRatio = qty_b.divide(oQty_b,8, RoundingMode.HALF_UP);
                
            } catch (Exception ignored) {
            }
            
            insValue1 = new DataValue[]
                    {
                            new DataValue(req.geteId(), Types.VARCHAR),
                            new DataValue(pluNo, Types.VARCHAR),
                            new DataValue(item_unit.getoQty(), Types.VARCHAR),
                            new DataValue(oUnit, Types.VARCHAR),
                            new DataValue(item_unit.getQty(), Types.VARCHAR),
                            new DataValue(item_unit.getUnit(), Types.VARCHAR),
                            new DataValue(item_unit.getsUnitUse(), Types.VARCHAR),
                            new DataValue(item_unit.getpUnitUse(), Types.VARCHAR),
                            new DataValue(item_unit.getBomUnitUse(), Types.VARCHAR),
                            new DataValue(item_unit.getProdUnitUse(), Types.VARCHAR),
                            new DataValue(item_unit.getPurUnitUse(), Types.VARCHAR),
                            new DataValue(item_unit.getcUnitUse(), Types.VARCHAR),
                            new DataValue(item_unit.getrUnitUse(), Types.VARCHAR),
                            new DataValue(item_unit.getWeight(), Types.VARCHAR),
                            new DataValue(item_unit.getVolume(), Types.VARCHAR),
                            new DataValue(unitRatio, Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE)
                    };
            
            InsBean ib1 = new InsBean("DCP_GOODS_UNIT", columns_unit);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
            
            List<spec_lang> specList = item_unit.getSpec_lang();//规格名称在 单位
            if(specList!=null&&specList.isEmpty()==false) {
                //单位
                String[] columns_unit_spec = {
                        "EID",
                        "PLUNO",
                        "OUNIT",
                        "LANG_TYPE",
                        "SPEC",
                        "LASTMODITIME"
                    
                };
                
                for (spec_lang item_spec : specList) {
                    DataValue[] insValue_sepc = null;
                    
                    insValue_sepc = new DataValue[]
                            {
                                    new DataValue(req.geteId(), Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(oUnit, Types.VARCHAR),
                                    new DataValue(item_spec.getLangType(), Types.VARCHAR),
                                    new DataValue(item_spec.getName(), Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE)
                            };
                    
                    InsBean ib_spec = new InsBean("DCP_GOODS_UNIT_LANG", columns_unit_spec);
                    ib_spec.addValues(insValue_sepc);
                    this.addProcessData(new DataProcessBean(ib_spec));
                }
            }
            
            
        }
        
        //DCP_GOODS
        String[] columnsGoods =
                {
                        "EID","PLUNO","PLUTYPE","ATTRGROUPID","CATEGORY","BRAND","SERIES","SHORTCUT_CODE",
                        "WARMTYPE","VIRTUAL","OPENPRICE","ISWEIGHT","STOCKMANAGETYPE","TAXCODE","INPUTTAXCODE","MEMO","STATUS",
                        "BASEUNIT","SUNIT","PRICE","SUPPRICE","BOM_UNIT","PROD_UNIT","PUNIT","PURUNIT","CUNIT",
                        "WUNIT","RUNIT","ISBATCH","SHELFLIFE","STOCKINVALIDDAY","STOCKOUTVALIDDAY","CHECKVALIDDAY",
                        "CREATETIME","ISHOLIDAY","ISHOTGOODS","OWN_GOODS","ISDOUBLEGOODS","SELFBUILTSHOPID","PLUID","SPLITTYPE",
                        "MAINBARCODE","SOURCETYPE","SPEC","PICKUNIT","SUPPLIER","PROCRATE","ISQUALITYCHECK","TESTMARKETSALEQTY","TESTMARKETSALEAMT","TESTMARKETGROSSAMT",
                        "MINQTY","MAXQTY","MULQTY","BATCHRULES","BATCHSORTTYPE","ISSHELFLIFECHECK","PROD_SHOP","PROD_HQ","PROD_OEM","ISSENSITIVE","ISCOMBINEBATCH","MATERIALPROPERTIES","PRODUCTTIME",
                        "PRODUCER","STORAGECON","MANUFACTURER","HOTLINE","INGRETABLE","NETCONTENT","FOODPROLICNUM","EATINGMETHOD","EXSTANDARD","PROPADDRESS","PURPRICE","CREATEOPID","CREATEDEPTID"
                };
        
        //DCP_GOODS
        DataValue[] insValueGoods = new DataValue[]
                {
                        new DataValue(req.geteId(), Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(pluType, Types.VARCHAR),
                        new DataValue(attrGroupId, Types.VARCHAR),
                        new DataValue(category, Types.VARCHAR),
                        new DataValue(brandNo, Types.VARCHAR),
                        new DataValue(seriesNo, Types.VARCHAR),
                        new DataValue(shortcutCode, Types.VARCHAR),
                        new DataValue(warmType, Types.VARCHAR),
                        new DataValue(virtual, Types.VARCHAR),
                        new DataValue(openPrice, Types.VARCHAR),
                        new DataValue(isWeight, Types.VARCHAR),
                        new DataValue(stockManageType, Types.VARCHAR),
                        new DataValue(taxCode, Types.VARCHAR),
                        new DataValue(inputTaxCode, Types.VARCHAR),
                        new DataValue(memo, Types.VARCHAR),
                        new DataValue(status, Types.VARCHAR),
                        new DataValue(baseUnit, Types.VARCHAR),
                        new DataValue(sUnit, Types.VARCHAR),
                        new DataValue(price, Types.VARCHAR),
                        new DataValue(supPrice, Types.VARCHAR),
                        new DataValue(bomUnit, Types.VARCHAR),
                        new DataValue(prodUnit, Types.VARCHAR),
                        new DataValue(pUnit, Types.VARCHAR),
                        new DataValue(purUnit, Types.VARCHAR),
                        new DataValue(cUnit, Types.VARCHAR),
                        new DataValue(wUnit, Types.VARCHAR),
                        new DataValue(rUnit, Types.VARCHAR),
                        new DataValue(isBatch, Types.VARCHAR),
                        new DataValue(shelfLife, Types.VARCHAR),
                        new DataValue(stockInValidDay, Types.VARCHAR),
                        new DataValue(stockOutValidDay, Types.VARCHAR),
                        new DataValue(checkValidDay, Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE) ,
                        new DataValue(isHoliday, Types.VARCHAR),
                        new DataValue(isHotGoods, Types.VARCHAR),
                        new DataValue(isOwnGoods, Types.VARCHAR),
                        new DataValue(isDoubleGoods, Types.VARCHAR),
                        new DataValue(selfBuiltShopId, Types.VARCHAR),
                        new DataValue(pluId, Types.VARCHAR),
                        new DataValue(splitType, Types.VARCHAR),
                        new DataValue(mainBarcode, Types.VARCHAR),
                        new DataValue(sourceType, Types.VARCHAR),
                        new DataValue(mainSpec, Types.VARCHAR),
                        new DataValue(pickUnit, Types.VARCHAR),
                        new DataValue(supplier, Types.VARCHAR),
                        new DataValue(procRate, Types.VARCHAR),
                        new DataValue(isQualityCheck, Types.VARCHAR),
                        new DataValue(testMarketSaleQty, Types.VARCHAR),
                        new DataValue(testMarketSaleAmt, Types.VARCHAR),
                        new DataValue(testMarketGrossAmt, Types.VARCHAR),
                        new DataValue(minQty, Types.VARCHAR),
                        new DataValue(maxQty, Types.VARCHAR),
                        new DataValue(mulQty, Types.VARCHAR),
                        new DataValue(batchRules, Types.VARCHAR),
                        new DataValue(batchSortType, Types.VARCHAR),
                        new DataValue(isShelfLifeCheck, Types.VARCHAR),
                        new DataValue(prodShop, Types.VARCHAR),
                        new DataValue(prodHQ, Types.VARCHAR),
                        new DataValue(prodOEM, Types.VARCHAR),
                        new DataValue(isSensitive, Types.VARCHAR),
                        new DataValue(isCombineBatch, Types.VARCHAR),
                        new DataValue(materialProperties, Types.VARCHAR),
                        new DataValue(productTime, Types.VARCHAR),
                        new DataValue(producer, Types.VARCHAR),
                        new DataValue(storageCon, Types.VARCHAR),
                        new DataValue(manufacturer, Types.VARCHAR),
                        new DataValue(hotLine, Types.VARCHAR),
                        new DataValue(ingreTable, Types.VARCHAR),
                        new DataValue(netContent, Types.VARCHAR),
                        new DataValue(foodProLicNum, Types.VARCHAR),
                        new DataValue(eatingMethod, Types.VARCHAR),
                        new DataValue(exStandard, Types.VARCHAR),
                        new DataValue(propAddress, Types.VARCHAR),
                        new DataValue(purPrice, Types.VARCHAR),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(departmentNo, Types.VARCHAR),

                };
        InsBean ib1 = new InsBean("DCP_GOODS", columnsGoods);
        ib1.addValues(insValueGoods);
        this.addProcessData(new DataProcessBean(ib1));
        
        // 添加商品简介
        String[] columns_hm ={"EID","PLUNO","DESCRIPTION","LASTMODITIME"};
        DataValue[] insValue_hm = new DataValue[]{
                new DataValue(req.geteId(), Types.VARCHAR),
                new DataValue(pluNo, Types.VARCHAR),
                new DataValue(req.getRequest().getDescription(), Types.VARCHAR),
                new DataValue(lastmoditime, Types.DATE)
        };
        InsBean ib_hm = new InsBean("DCP_GOODS_EXT", columns_hm);
        ib_hm.addValues(insValue_hm);
        this.addProcessData(new DataProcessBean(ib_hm));

        if(CollUtil.isNotEmpty(goodsTemplateList)){
            String categorySql="select * from DCP_CATEGORY_CONTROL where eid='"+eId+"' and category='"+category+"'";
            List<Map<String, Object>> categoryList = this.doQueryData(categorySql, null);
            String canSale="Y";
            String canFree="Y";
            String canReturn="Y";
            String canOrder="Y";
            String canPurchase="Y";
            String canRequire="Y";
            String canRequireBack="Y";
            String canEstimate="Y";
            String clearType="N";
            if(CollUtil.isNotEmpty(categoryList)){
                Map<String, Object> categoryMap = categoryList.get(0);
                canSale=categoryMap.get("CANSALE").toString();
                canFree=categoryMap.get("CANFREE").toString();
                canReturn=categoryMap.get("CANRETURN").toString();
                canOrder=categoryMap.get("CANORDER").toString();
                canPurchase=categoryMap.get("CANPURCHASE").toString();
                canRequire=categoryMap.get("CANREQUIRE").toString();
                canRequireBack=categoryMap.get("CANREQUIREBACK").toString();
                canEstimate=categoryMap.get("CANESTIMATE").toString();
                clearType=categoryMap.get("CLEARTYPE").toString();
            }
            int templateItem=0;
            for(GoodsTemplateList goodsTemplate : goodsTemplateList){
                templateItem++;
                String templateId = goodsTemplate.getTemplateId();
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("TEMPLATEID", DataValues.newString(templateId));
                detailColumns.add("PLUNO", DataValues.newString(pluNo));
                detailColumns.add("SUPPLIERTYPE", DataValues.newString(""));
                detailColumns.add("SUPPLIERID", DataValues.newString(""));

                detailColumns.add("WARNINGQTY", DataValues.newString(0));
                detailColumns.add("SAFEQTY", DataValues.newString("0"));
                detailColumns.add("CANSALE", DataValues.newString(canSale));
                detailColumns.add("CANFREE", DataValues.newString(canFree));
                detailColumns.add("CANRETURN", DataValues.newString(canReturn));
                detailColumns.add("CANORDER", DataValues.newString(canOrder));
                detailColumns.add("CANPURCHASE", DataValues.newString(canPurchase));
                detailColumns.add("CANREQUIRE", DataValues.newString(canRequire));
                detailColumns.add("MINQTY", DataValues.newString(minQty));
                detailColumns.add("MAXQTY", DataValues.newString(maxQty));
                detailColumns.add("MULQTY", DataValues.newString(mulQty));
                detailColumns.add("CANREQUIREBACK", DataValues.newString(canRequireBack));
                detailColumns.add("IS_AUTO_SUBTRACT", DataValues.newString("N"));
                detailColumns.add("CANESTIMATE", DataValues.newString(canEstimate));
                detailColumns.add("CLEARTYPE", DataValues.newString(clearType));
                detailColumns.add("STATUS", DataValues.newString("100"));

                //detailColumns.add("TRAN_TIME", DataValues.newString(lastmoditime));
                detailColumns.add("LASTMODITIME", new DataValue(lastmoditime, Types.DATE)	);
                detailColumns.add("ITEM", DataValues.newString(templateItem));
                detailColumns.add("ISNEWGOODS", DataValues.newString("N"));
                detailColumns.add("ISALLOT", DataValues.newString("N"));


                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_GOODSTEMPLATE_GOODS",detailColumnNames);
                ib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(ib));
            }
        }

        if(CollUtil.isNotEmpty(pOrderTemplateList)){
            int templateItem=0;
            for(POrderTemplateList goodsTemplate : pOrderTemplateList){
               String templateNo = goodsTemplate.getTemplateNo();
               String templateSql = "select * from DCP_PTEMPLATE where eid='"+eId+"' and ptemplateNo='"+templateNo+"'";
               List<Map<String, Object>> templateList = this.doQueryData(templateSql, null);
               String docType="";
               if(CollUtil.isNotEmpty(templateList)){
                   Map<String, Object> templateMap = templateList.get(0);
                   docType=templateMap.get("DOC_TYPE").toString();
               }
               templateItem++;
               ColumnDataValue detailColumns=new ColumnDataValue();
               detailColumns.add("EID", DataValues.newString(eId));
               detailColumns.add("PTEMPLATENO", DataValues.newString(templateNo));
               detailColumns.add("DOC_TYPE", DataValues.newString(docType));
               detailColumns.add("ITEM", DataValues.newString(templateItem));
               detailColumns.add("PLUNO", DataValues.newString(pluNo));

               detailColumns.add("PUNIT", DataValues.newString(pUnit));
               detailColumns.add("MIN_QTY", DataValues.newString(minQty));
               detailColumns.add("MAX_QTY", DataValues.newString(maxQty));
               detailColumns.add("MUL_QTY", DataValues.newString(mulQty));
               detailColumns.add("DEFAULT_QTY", DataValues.newString("0"));
               detailColumns.add("TRAN_TIME", DataValues.newString(lastmoditime));
               detailColumns.add("CATEGORYNO", DataValues.newString(category));
               detailColumns.add("STATUS", DataValues.newString("100"));
               detailColumns.add("SORTID", DataValues.newString(templateItem));
               detailColumns.add("SUPPLIER", DataValues.newString(supplier));


               String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
               DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
               InsBean ib=new InsBean("DCP_PTEMPLATE_DETAIL",detailColumnNames);
               ib.addValues(detailDataValues);
               this.addProcessData(new DataProcessBean(ib));
            }
        }

        if(CollUtil.isNotEmpty(tagList)){
            int tagItem=0;
            String pluName="";
            if(CollUtil.isNotEmpty(pluNameLangs)){
                List<pluName_lang> collect = pluNameLangs.stream().filter(x -> x.getLangType().equals(req.getLangType())).distinct().collect(Collectors.toList());
                if(collect.size()>0){
                    pluName=collect.get(0).getName();
                }
            }
            for (TagList tag : tagList){
                tagItem++;
                String tagNo = tag.getTagNo();
                String tagSql="SELECT tagGroupType,tagGroupNo FROM dcp_tagType WHERE tagNo='"+tagNo+"'";
                List<Map<String, Object>> tagTypeList = this.doQueryData(tagSql, null);
                String tagGroupType="";
                String tagGroupNo="";
                if(CollUtil.isNotEmpty(tagTypeList)){
                    Map<String, Object> tagTypeMap = tagTypeList.get(0);
                    tagGroupType=tagTypeMap.get("TAGGROUPTYPE").toString();
                    tagGroupNo=tagTypeMap.get("TAGGROUPNO").toString();
                }
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("TAGGROUPTYPE", DataValues.newString(tagGroupType));
                detailColumns.add("TAGNO", DataValues.newString(tagNo));
                detailColumns.add("TAGGROUPNO", DataValues.newString(tagGroupNo));
                detailColumns.add("ID", DataValues.newString(pluNo));

                detailColumns.add("NAME", DataValues.newString(pluName));
                detailColumns.add("LASTMODITIME", DataValues.newDate(lastmoditime));
                //detailColumns.add("TRAN_TIME", DataValues.newString(lastmoditime));
                detailColumns.add("ONLILASORTING", DataValues.newString(tagItem));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_TAGTYPE_DETAIL",detailColumnNames);
                ib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(ib));
            }
        }
        this.doExecuteDataToDB();
        
        //【ID1025723】货郎加盟店，门店视角添加商品，调缓存报错 by jinzma 20220512
        level1Elm datas = res.new level1Elm();
        datas.setPluNo(pluNo);
        res.setDatas(datas);
        
        //【ID1035449】【货郎3.0】自建商品（所有）DCP_GOODS_BARCODE_MAIN表没有存条码 by jinzma 20230816
        {
            String procedure = "SP_ETL_GOODS_BARCODE_MAIN";
            Map<Integer, Object> inputParameter = new HashMap<>();
            inputParameter.put(1, req.geteId());       //--企业ID
            inputParameter.put(2, pluNo);              //--品号
            ProcedureBean pb = new ProcedureBean(procedure, inputParameter);
            this.addProcessData(new DataProcessBean(pb));
            
            this.doExecuteDataToDB();
        }
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_GoodsSetCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_GoodsSetCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_GoodsSetCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_GoodsSetCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        //必传字段
        String pluNo = req.getRequest().getPluNo();
        String pluId = req.getRequest().getPluId();
        String pluType = req.getRequest().getPluType();
        String price = req.getRequest().getPrice();
        String category = req.getRequest().getCategory();
        String virtual = req.getRequest().getVirtual();
        String openPrice = req.getRequest().getOpenPrice();
        String isWeight = req.getRequest().getIsWeight();
        String stockManageType = req.getRequest().getStockManageType();
        String status = req.getRequest().getStatus();
        String baseUnit = req.getRequest().getBaseUnit();
        String sUnit = req.getRequest().getsUnit();
        String bomUnit = req.getRequest().getBomUnit();
        String prodUnit = req.getRequest().getProdUnit();
        String pUnit = req.getRequest().getpUnit();
        String purUnit = req.getRequest().getpUnit();
        String cUnit = req.getRequest().getcUnit();
        String wUnit = req.getRequest().getwUnit();
        String isBatch = req.getRequest().getIsBatch();
        String isHotGoods = req.getRequest().getIsHotGoods();
        String isOwnGoods = req.getRequest().getIsOwnGoods();
        String isDoubleGoods = req.getRequest().getIsDoubleGoods();
        String selfBuiltShopId = req.getRequest().getSelfBuiltShopId();
        
        List<attr> attrList = req.getRequest().getAttrList();
        List<barcode> barcodeList = req.getRequest().getBarcodeList();
        List<unit> unitList = req.getRequest().getUnitList();//
        List<pluName_lang> pluName_lang = req.getRequest().getPluName_lang();
        
        if(pluName_lang==null||pluName_lang.isEmpty()) {
            errMsg.append("商品名称多语言信息不可为空, ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        if(barcodeList==null||barcodeList.isEmpty()) {
            //errMsg.append("商品条码信息不可为空, ");
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }else{
            for (barcode barcode:barcodeList){
                if(Check.Null(barcode.getUnit())){
                    //errMsg.append("商品条码单位不可为空, ");
                    //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                if(Check.Null(barcode.getFeatureNo())){
                    //errMsg.append("商品条码特征码不可为空, ");
                    //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                if(Check.Null(barcode.getStatus())){
                   // errMsg.append("商品条码状态不可为空, ");
                    //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                if(Check.Null(barcode.getBarcodeType())){
                    errMsg.append("商品条码分类不可为空, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }else{
                    if(barcode.getBarcodeType().equals("1")){
                        if(Check.Null(barcode.getPluBarcode())){
                            errMsg.append("商品条码不可为空, ");
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                        }
                    }
                }
            }
        }
        
        if(unitList==null||unitList.isEmpty()) {
            errMsg.append("商品单位信息不可为空, ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }else{
            for (unit u:unitList){
                if (!PosPub.isNumericType(u.getoQty())){
                    errMsg.append("换算单位数量必须为数值, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }else {
                    if (BigDecimal.ZERO.compareTo(new BigDecimal(u.getoQty()))==0){
                        errMsg.append("换算单位数量不能为零, ");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }
                
                if (!PosPub.isNumericType(u.getQty())){
                    errMsg.append("基准单位数量必须为数值, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }else {
                    if (BigDecimal.ZERO.compareTo(new BigDecimal(u.getQty()))==0){
                        errMsg.append("基准单位数量不能为零, ");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }
                
                if (Check.Null(u.getoUnit())){
                    errMsg.append("换算单位编码不能为空, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                
                if (Check.Null(u.getUnit())){
                    errMsg.append("基准单位编码不能为空, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                
                if (Check.Null(u.getsUnitUse())){
                    errMsg.append("是否用于销售不能为空, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                
                if (Check.Null(u.getpUnitUse())){
                    errMsg.append("是否用于要货不能为空, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                
                if (Check.Null(u.getBomUnitUse())){
                    errMsg.append("是否用于配方不能为空, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                
                if (Check.Null(u.getProdUnitUse())){
                    errMsg.append("是否用于生产不能为空, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                
                if (Check.Null(u.getPurUnitUse())){
                    errMsg.append("是否用于采购不能为空, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                
                if (Check.Null(u.getcUnitUse())){
                    errMsg.append("是否用于盘点不能为空, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                
            }
        }
        
        if(pluType!=null&& pluType.equalsIgnoreCase("FEATURE")) {
            if(attrList==null||attrList.isEmpty()) {
                errMsg.append("特征码商品属性不可为空, ");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
            for (attr par : attrList) {
                if(par.getAttrValueList()==null||par.getAttrValueList().isEmpty()) {
                    errMsg.append("特征码商品属性值不可为空, ");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
            }
        }
        
        //商品编码非必填，如果入参没有商品编码传入，则自动生成， by jinzma 20220421
        if (Check.Null(pluNo)) {
            if (Check.Null(pluId)){
                errMsg.append("商品ID不可为空值, ");
                isFail = true;
            }
            if (Check.Null(selfBuiltShopId)){
                //errMsg.append("自建门店不可为空值, ");
                //isFail = true;
            }
        }
        
        if (Check.Null(category)) {
            errMsg.append("商品分类不可为空值, ");
            isFail = true;
        }
        if (Check.Null(pluType)) {
            errMsg.append("商品类型pluType不可为空值, ");
            isFail = true;
        }
        if (Check.Null(virtual)) {
            errMsg.append("是否虚拟商品virtual不可为空值, ");
            isFail = true;
        }
        if (Check.Null(openPrice)) {
            errMsg.append("是否开价商品openPrice不可为空值, ");
            isFail = true;
        }
        if (Check.Null(isWeight)) {
            errMsg.append("是否称重商品isWeight不可为空值, ");
            isFail = true;
        }
        if (Check.Null(stockManageType)) {
            errMsg.append("库存管控方式stockManageType不可为空值, ");
            isFail = true;
        }
        if (Check.Null(status)) {
            errMsg.append("商品状态status不可为空值, ");
            isFail = true;
        }
        if (Check.Null(baseUnit)) {
            errMsg.append("基准单位编码baseUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(sUnit)) {
            errMsg.append("默认销售单位编码sUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(bomUnit)) {
            errMsg.append("配方单位编码bomUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(prodUnit)) {
            errMsg.append("生产单位编码prodUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(pUnit)) {
            errMsg.append("要货单位编码pUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(purUnit)) {
            errMsg.append("采购单位编码purUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(cUnit)) {
            errMsg.append("盘点单位编码cUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(wUnit)) {
            errMsg.append("库存单位编码wUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(price)) {
            errMsg.append("建议零售价price不可为空值, ");
            isFail = true;
        }
        if (Check.Null(isHotGoods)) {
            errMsg.append("是否爆款商品不可为空值, ");
            isFail = true;
        }
        if (Check.Null(isOwnGoods)) {
            errMsg.append("是否自有商品不可为空值, ");
            isFail = true;
        }
        if (Check.Null(isDoubleGoods)) {
            errMsg.append("是否双拼商品不可为空值, ");
            isFail = true;
        }

        List<GoodsTemplateList> goodsTemplateList = req.getRequest().getGoodsTemplateList();
        List<POrderTemplateList> pOrderTemplateLists = req.getRequest().getpOrderTemplateList();
        List<TagList> tagList = req.getRequest().getTagList();
        if(CollUtil.isNotEmpty(goodsTemplateList)){
            for (GoodsTemplateList par : goodsTemplateList){
                if(Check.Null(par.getTemplateId())){
                    errMsg.append("模板编号不可为空值, ");
                    isFail = true;
                }
            }
        }

        if(CollUtil.isNotEmpty(pOrderTemplateLists)){
            for (POrderTemplateList par : pOrderTemplateLists){
                if(Check.Null(par.getTemplateNo())){
                    errMsg.append("模板编号不可为空值, ");
                    isFail = true;
                }
            }
        }

        if(CollUtil.isNotEmpty(tagList)){
            for (TagList par : tagList){
                if(Check.Null(par.getTagNo())){
                    errMsg.append("标签编号不可为空值, ");
                    isFail = true;
                }
            }
        }


        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return isFail;
        
    }
    
    @Override
    protected TypeToken<DCP_GoodsSetCreateReq> getRequestType() {
        return new TypeToken<DCP_GoodsSetCreateReq>(){};
    }
    
    @Override
    protected DCP_GoodsSetCreateRes getResponseType() {
        return new DCP_GoodsSetCreateRes();
    }
    
    private String getPluNo(String eId,String selfBuiltShopId) throws Exception {
        String accountDate = PosPub.getAccountDate_SMS(dao,eId,selfBuiltShopId);
        //生成规则：门店编码+日期+四位流水号 比如C100001202204200001
        String pluNo = selfBuiltShopId + accountDate;
        String sql = " select max(pluno) as pluno from dcp_goods "
                + " where eid = '"+eId+"' and pluno like '"+pluNo.toLowerCase()+"%' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        pluNo = getQData.get(0).get("PLUNO").toString();
        if (!Check.Null(pluNo)) {
            pluNo = pluNo.substring(selfBuiltShopId.length());
            long i = Long.parseLong(pluNo) + 1;
            pluNo = i + "";
            pluNo = selfBuiltShopId + pluNo;
        } else {
            pluNo = selfBuiltShopId + accountDate+"00001";
        }
        
        return pluNo.toLowerCase();
    }

    private String getPluNoNew(String eId,String category,String pluCodeRule) throws Exception {
        if(Check.Null(pluCodeRule)){
            pluCodeRule="1";
        }
        //1.品类编码+3位流水码
        //2.品类编码+4位流水码
        //3.品类编码+5位流水码

        String preFixCode=category;
        String cSql="select * from dcp_category where eid='"+eId+"' and category='"+category+"'";
        List<Map<String, Object>> cList = this.doQueryData(cSql, null);
        if(cList.size()>0){
            String thisPreFixCode = cList.get(0).get("PREFIXCODE").toString();
            if(!Check.Null(thisPreFixCode)){
                preFixCode=thisPreFixCode;
            }
        }

        int idx=0;
        String newPluNo="";
        String pluNo = "";
        String sql = " select  pluno from dcp_goods "
                + " where eid = '"+eId+"' and pluno like '"+preFixCode.toLowerCase()+"%' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        //pluNo = getQData.get(0).get("PLUNO").toString();
        //if (!Check.Null(pluNo)) {
        //    String substring = pluNo.substring(catetory.length());
        //    if (!Check.Null(substring)) {
        //        idx = Integer.parseInt(substring);
        //    }
        //}
        //查寻最大的id
        if(getQData.size()>0) {
            String finalPreFixCode = preFixCode;
            List<Integer> subIds = getQData.stream().map(x -> {
                int thisId = 0;
                String thisPluno = x.get("PLUNO").toString();
                String substring = thisPluno.substring(finalPreFixCode.length());
                if (!Check.Null(substring)) {
                    thisId = Integer.parseInt(substring);
                }
                return thisId;
            }).distinct().collect(Collectors.toList());
            Optional<Integer> maxOp = subIds.stream().max(Integer::compareTo);
            if (maxOp.isPresent()) {
                idx = maxOp.get();
            }
        }
        idx+=1;
        int left=3;
        if(pluCodeRule.equals("2")){
            left=4;
        }else if(pluCodeRule.equals("3")){
            left=5;
        }
        newPluNo=preFixCode+PosPub.FillStr(idx+"",left,"0",true);


        return newPluNo;
    }

}
