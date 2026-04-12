package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_GoodsSetUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsSetUpdateReq.*;
import com.dsc.spos.json.cust.res.DCP_GoodsSetUpdateRes;
import com.dsc.spos.model.Plu_POS_GoodsPriceRedisUpdate;
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

public class DCP_GoodsSetUpdate extends SPosAdvanceService<DCP_GoodsSetUpdateReq,DCP_GoodsSetUpdateRes> {
    @Override
    protected void processDUID(DCP_GoodsSetUpdateReq req, DCP_GoodsSetUpdateRes res) throws Exception {


        //同步缓存
        List<Plu_POS_GoodsPriceRedisUpdate> pluList=new ArrayList<>();
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
                } else {
                    apiUserKey = map.get("ITEMVALUE").toString();
                }
            }
        }
        //
        Plu_POS_GoodsPriceRedisUpdate plu_pos_goodsPriceRedisUpdate=new Plu_POS_GoodsPriceRedisUpdate();
        plu_pos_goodsPriceRedisUpdate.setPluNo(req.getRequest().getPluNo());
        pluList.add(plu_pos_goodsPriceRedisUpdate);
        PosPub.POS_GoodsPriceRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,pluList);
        String eId = req.geteId();
        levelRequest requestModel = req.getRequest();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String pluNo = requestModel.getPluNo();
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
        String rUnit    = requestModel.getrUnit();
        List<unit> unitList = requestModel.getUnitList();//
        String isBatch = requestModel.getIsBatch();
        String shelfLife = requestModel.getShelfLife();
        String stockInValidDay = requestModel.getStockInValidDay();
        String stockOutValidDay = requestModel.getStockOutValidDay();
        String checkValidDay = requestModel.getCheckValidDay();
        String isHoliday = requestModel.getIsHoliday();
        String isHotGoods = requestModel.getIsHotGoods();
        String isOwnGoods = requestModel.getIsOwnGoods();
        String isDoubleGoods = requestModel.getIsDoubleGoods();

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
        List<DCP_GoodsSetUpdateReq.GoodsTemplateList> goodsTemplateList = requestModel.getGoodsTemplateList();
        //⑤要货设置：minQty, maxQty, mulQty,  pOrderTemplateList[],
        String minQty = requestModel.getMinQty();
        String maxQty = requestModel.getMaxQty();
        String mulQty = requestModel.getMulQty();
        List<DCP_GoodsSetUpdateReq.POrderTemplateList> pOrderTemplateList = requestModel.getpOrderTemplateList();
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
        List<DCP_GoodsSetUpdateReq.TagList> tagList = requestModel.getTagList();


        //【ID1023729】货郎3.0加盟店可以自己采购商品，定价，做促销，管理自己的会员（后端云中台服务） by jinzma 20220216
        String sql = " select selfbuiltshopid from dcp_goods where eid='"+req.geteId()+"' and pluno='"+pluNo+"' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData==null || getQData.isEmpty()){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "dcp_goods表中不存在此商品");
        }else{
            String selfBuiltShopId = getQData.get(0).get("SELFBUILTSHOPID").toString();
            //自建商品逻辑判断
            if (!Check.Null(selfBuiltShopId)){
                if (!selfBuiltShopId.equals(req.getShopId())){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "自建商品所属门店非当前门店,不允许修改");
                }else{
                    sql = " select b.templateid from ("
                            + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                            + " from dcp_goodstemplate a"
                            + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+req.getShopId()+"'"
                            + " where a.eid='"+req.geteId()+"' and a.status='100' and (a.templatetype='SHOP' and c2.id is not null)"
                            + " ) a"
                            + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                            + " where a.rn=1 and b.pluno='"+pluNo+"'"
                            + " ";
                    getQData.clear();
                    getQData = this.doQueryData(sql, null);
                    if (getQData==null || getQData.isEmpty()){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "商品模板适用范围不是门店级，请联系总部重新下发");
                    }else{
                        
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
                    }
                }
            }
        }

        //barcode 先删 否则影响排序
        DelBean	dbbarcode = new DelBean("DCP_GOODS_LANG");
        dbbarcode = new DelBean("DCP_GOODS_BARCODE");
        dbbarcode.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        dbbarcode.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(dbbarcode));
        this.doExecuteDataToDB();
        
        //先删除原来的
        DelBean	db1 = new DelBean("DCP_GOODS_LANG");
        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        //先删除原来的
        db1 = new DelBean("DCP_GOODS_UNIT");
        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        //先删除原来的
        db1 = new DelBean("DCP_GOODS_UNIT_LANG");
        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        //先删除原来的

        
        //先删除原来的
        db1 = new DelBean("DCP_GOODS_FEATURE");
        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        //先删除原来的
        db1 = new DelBean("DCP_GOODS_FEATURE_LANG");
        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        //先删除原来的
        db1 = new DelBean("DCP_GOODS_ATTR");
        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        //先删除原来的
        db1 = new DelBean("DCP_GOODS_ATTR_VALUE");
        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        //先删除原来的
        db1 = new DelBean("DCP_GOODS_EXT");
        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        db1 = new DelBean("DCP_GOODSTEMPLATE_GOODS");
        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        db1 = new DelBean("DCP_PTEMPLATE_DETAIL");
        db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        db1 = new DelBean("DCP_TAGTYPE_DETAIL");
        db1.addCondition("ID", new DataValue(pluNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        //DCP_GOODS_LANG
        List<pluName_lang> pluNameLangs = req.getRequest().getPluName_lang();
        String[] columns_GoodsLang = {
                "EID",
                "PLUNO",
                "LANG_TYPE",
                "PLU_NAME",
                "LASTMODITIME",
        };
        for (pluName_lang pluName_lang : pluNameLangs) {
            DataValue[] insValue1 = new DataValue[] {
                    new DataValue(req.geteId(), Types.VARCHAR),
                    new DataValue(pluNo, Types.VARCHAR),
                    new DataValue(pluName_lang.getLangType(), Types.VARCHAR),
                    new DataValue(pluName_lang.getName(), Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE)
            };
            
            InsBean ib1 = new InsBean("DCP_GOODS_LANG", columns_GoodsLang);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
        }
        
        //DCP_GOODS_BARCODE
        String[] columns_barcode = {
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

        //pluBarCode 排一下序取最大的
        String maxPluBarCode="";
        for (barcode par1 : barcodeList) {
            if(!Check.Null(par1.getPluBarcode())){
                if(par1.getPluBarcode().startsWith(pluNo)){
                    if(maxPluBarCode.compareTo(par1.getPluBarcode())<=0){
                        maxPluBarCode=par1.getPluBarcode();
                    }
                }
            }
        }

        for (barcode par : barcodeList) {
            if(Check.Null(par.getFeatureNo())){
                par.setFeatureNo(" ");
            }
            String  pluBarcode = par.getPluBarcode();
            if("2".equals(par.getBarcodeType())&&Check.Null(pluBarcode)){
                //自编码
                pluBarcode=PosPub.getPluBarCode(dao,eId,"",pluNo,maxPluBarCode);
                //最大的传进去编码 传出来的比较
                if(maxPluBarCode.compareTo(pluBarcode)<=0){
                    maxPluBarCode=pluBarcode;
                }
            }
            //oldPluBarCode=pluBarcode;
            if(!allBarCode.contains(pluBarcode)){
                allBarCode.add(pluBarcode);
            }
            String featureNo =" ";//特征码 普通商品和套餐商品为一个空格，FEATURE商品：示例“size-xl”，使用短线连接规格的编码
            String sortId = i+"";
            String status_barcode = par.getStatus();
            if(pluType.equalsIgnoreCase("FEATURE")) {
                featureNo = par.getFeatureNo();
            }
            
            String curUnit = par.getUnit();
            BigDecimal unitRatio = new BigDecimal("1");
            boolean isExistUnitRatio = false;
            
            for(unit item_unit : unitList) {
                String oUnit = item_unit.getoUnit();
                if(oUnit==null || oUnit.isEmpty()) {
                    continue;
                }
                if(curUnit.equals(oUnit)==false) {
                    continue;
                }
                try {
                    String jizhunUnit = item_unit.getUnit();
                    if(curUnit.equals(jizhunUnit)) {
                        unitRatio = new BigDecimal("1");
                    }else{
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
            
            insValue1 = new DataValue[]{
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
        if(attrList!=null && attrList.isEmpty()==false) {
            i = 1;
            String[] columns_attr = {
                    "EID",
                    "PLUNO",
                    "ATTRID",
                    "SORTID",
                    "LASTMODITIME"
            };
            
            for (attr item_attr : attrList) {
                String attrId = item_attr.getAttrId();
                String attrName = item_attr.getAttrName();
                String sortId_attr = i+"";
                
                DataValue[] insValue1 = new DataValue[]{
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
                        
                        DataValue[] insValue_attrValue = new DataValue[]{
                                new DataValue(req.geteId(), Types.VARCHAR),
                                new DataValue(pluNo, Types.VARCHAR),
                                new DataValue(attrId, Types.VARCHAR),
                                new DataValue(attrValueId, Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };
                        
                        InsBean ib_attrValue = new InsBean("DCP_GOODS_ATTR_VALUE", columns_attrValue);
                        ib_attrValue.addValues(insValue_attrValue);
                        this.addProcessData(new DataProcessBean(ib_attrValue));


					/* String featureNo = attrId +"-"+attrValueId;//size-XL
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
            if(attrList.size()>1) {
                attr2 = attrList.get(1);
            }
            attr attr3 = null;
            if(attrList.size()>2) {
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
                
                
                String[] columns_feature_lang = {
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
                                    
                                    DataValue[] insValue_feature = new DataValue[]{
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
                                    
                                    DataValue[] insValue_feature_lang = new DataValue[]{
                                            new DataValue(req.geteId(), Types.VARCHAR),
                                            new DataValue(pluNo, Types.VARCHAR),
                                            new DataValue(featureNo, Types.VARCHAR),
                                            new DataValue(req.getLangType(), Types.VARCHAR),
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
                            }
                            else
                            {
                                //只有二层
                                DataValue[] insValue_feature = new DataValue[]{
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
                                
                                DataValue[] insValue_feature_lang = new DataValue[]{
                                        new DataValue(req.geteId(), Types.VARCHAR),
                                        new DataValue(pluNo, Types.VARCHAR),
                                        new DataValue(featureNo, Types.VARCHAR),
                                        new DataValue(req.getLangType(), Types.VARCHAR),
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
                    }
                    else
                    {
                        //只有一层
                        DataValue[] insValue_feature = new DataValue[]{
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
                        
                        
                        DataValue[] insValue_feature_lang = new DataValue[]{
                                new DataValue(req.geteId(), Types.VARCHAR),
                                new DataValue(pluNo, Types.VARCHAR),
                                new DataValue(featureNo, Types.VARCHAR),
                                new DataValue(req.getLangType(), Types.VARCHAR),
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
        String[] columns_unit = {
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
            BigDecimal unitRatio = new BigDecimal("0");
            try {
                BigDecimal oQty_b = new BigDecimal(item_unit.getoQty());
                BigDecimal qty_b = new BigDecimal(item_unit.getQty());
                unitRatio = qty_b.divide(oQty_b,8, RoundingMode.HALF_UP);
            }catch (Exception ignored) {
            }
            
            DataValue[] insValue1 = new DataValue[]{
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
                    new DataValue(item_unit.getrUnitUse(),Types.VARCHAR),
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
                    DataValue[] insValue_sepc = new DataValue[]{
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
        
        //商品信息-扩展属性
        String[] columns_ext = {
                "EID",
                "PLUNO",
                "DESCRIPTION",
                "LASTMODITIME"
        };
        
        DataValue[] insValue_ext = new DataValue[]{
                new DataValue(req.geteId(), Types.VARCHAR),
                new DataValue(pluNo, Types.VARCHAR),
                new DataValue(req.getRequest().getDescription(), Types.VARCHAR),
                new DataValue(lastmoditime, Types.DATE)
        };
        InsBean ib_ext = new InsBean("DCP_GOODS_EXT", columns_ext);
        ib_ext.addValues(insValue_ext);
        this.addProcessData(new DataProcessBean(ib_ext));
        
        //DCP_GOODS
        UptBean ub1 = new UptBean("DCP_GOODS");
        ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
        ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        
        ub1.addUpdateValue("PLUTYPE",new DataValue(pluType, Types.VARCHAR));
        ub1.addUpdateValue("ATTRGROUPID",new DataValue(attrGroupId, Types.VARCHAR));
        ub1.addUpdateValue("CATEGORY",new DataValue(category, Types.VARCHAR));
        ub1.addUpdateValue("BRAND",new DataValue(brandNo, Types.VARCHAR));
        ub1.addUpdateValue("SERIES",new DataValue(seriesNo, Types.VARCHAR));
        ub1.addUpdateValue("SHORTCUT_CODE",new DataValue(shortcutCode, Types.VARCHAR));
        ub1.addUpdateValue("WARMTYPE",new DataValue(warmType, Types.VARCHAR));
        ub1.addUpdateValue("VIRTUAL",new DataValue(virtual, Types.VARCHAR));
        ub1.addUpdateValue("OPENPRICE",new DataValue(openPrice, Types.VARCHAR));
        ub1.addUpdateValue("ISWEIGHT",new DataValue(isWeight, Types.VARCHAR));
        ub1.addUpdateValue("STOCKMANAGETYPE",new DataValue(stockManageType, Types.VARCHAR));
        ub1.addUpdateValue("TAXCODE",new DataValue(taxCode, Types.VARCHAR));
        ub1.addUpdateValue("INPUTTAXCODE",new DataValue(inputTaxCode, Types.VARCHAR));
        ub1.addUpdateValue("MEMO",new DataValue(memo, Types.VARCHAR));
        ub1.addUpdateValue("STATUS",new DataValue(status, Types.VARCHAR));
        ub1.addUpdateValue("BASEUNIT",new DataValue(baseUnit, Types.VARCHAR));
        ub1.addUpdateValue("SUNIT",new DataValue(sUnit, Types.VARCHAR));
        ub1.addUpdateValue("PRICE",new DataValue(price, Types.VARCHAR));
        ub1.addUpdateValue("SUPPRICE",new DataValue(supPrice, Types.VARCHAR));
        ub1.addUpdateValue("BOM_UNIT",new DataValue(bomUnit, Types.VARCHAR));
        ub1.addUpdateValue("PROD_UNIT",new DataValue(prodUnit, Types.VARCHAR));
        ub1.addUpdateValue("PUNIT",new DataValue(pUnit, Types.VARCHAR));
        ub1.addUpdateValue("PURUNIT",new DataValue(purUnit, Types.VARCHAR));
        ub1.addUpdateValue("CUNIT",new DataValue(cUnit, Types.VARCHAR));
        ub1.addUpdateValue("WUNIT",new DataValue(wUnit, Types.VARCHAR));
        ub1.addUpdateValue("ISBATCH",new DataValue(isBatch, Types.VARCHAR));
        ub1.addUpdateValue("SHELFLIFE",new DataValue(shelfLife, Types.VARCHAR));
        ub1.addUpdateValue("STOCKINVALIDDAY",new DataValue(stockInValidDay, Types.VARCHAR));
        ub1.addUpdateValue("STOCKOUTVALIDDAY",new DataValue(stockOutValidDay, Types.VARCHAR));
        ub1.addUpdateValue("CHECKVALIDDAY",new DataValue(checkValidDay, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime, Types.DATE));
        ub1.addUpdateValue("ISHOLIDAY",new DataValue(isHoliday, Types.VARCHAR));
        ub1.addUpdateValue("REDISUPDATESUCCESS",new DataValue("N", Types.VARCHAR));
        ub1.addUpdateValue("ISHOTGOODS",new DataValue(isHotGoods, Types.VARCHAR));
        ub1.addUpdateValue("OWN_GOODS",new DataValue(isOwnGoods, Types.VARCHAR));
        ub1.addUpdateValue("ISDOUBLEGOODS",new DataValue(isDoubleGoods, Types.VARCHAR));
        ub1.addUpdateValue("RUNIT",new DataValue(rUnit, Types.VARCHAR));

        ub1.addUpdateValue("SPLITTYPE",new DataValue(splitType, Types.VARCHAR));
        ub1.addUpdateValue("SHORTCUT_CODE",new DataValue(shortcutCode, Types.VARCHAR));
        ub1.addUpdateValue("MAINBARCODE",new DataValue(mainBarcode, Types.VARCHAR));
        ub1.addUpdateValue("SOURCETYPE",new DataValue(sourceType, Types.VARCHAR));
        ub1.addUpdateValue("SPEC",new DataValue(mainSpec, Types.VARCHAR));
        ub1.addUpdateValue("PICKUNIT",new DataValue(pickUnit, Types.VARCHAR));
        ub1.addUpdateValue("SUPPLIER",new DataValue(supplier, Types.VARCHAR));
        ub1.addUpdateValue("PROCRATE",new DataValue(procRate, Types.VARCHAR));

        ub1.addUpdateValue("ISQUALITYCHECK",new DataValue(isQualityCheck, Types.VARCHAR));
        ub1.addUpdateValue("TESTMARKETSALEQTY",new DataValue(testMarketSaleQty, Types.VARCHAR));
        ub1.addUpdateValue("TESTMARKETSALEAMT",new DataValue(testMarketSaleAmt, Types.VARCHAR));
        ub1.addUpdateValue("TESTMARKETGROSSAMT",new DataValue(testMarketGrossAmt, Types.VARCHAR));
        ub1.addUpdateValue("MINQTY",new DataValue(minQty, Types.VARCHAR));
        ub1.addUpdateValue("MAXQTY",new DataValue(maxQty, Types.VARCHAR));
        ub1.addUpdateValue("MULQTY",new DataValue(mulQty, Types.VARCHAR));
        ub1.addUpdateValue("BATCHRULES",new DataValue(batchRules, Types.VARCHAR));
        ub1.addUpdateValue("BATCHSORTTYPE",new DataValue(batchSortType, Types.VARCHAR));
        ub1.addUpdateValue("ISSHELFLIFECHECK",new DataValue(isShelfLifeCheck, Types.VARCHAR));
        ub1.addUpdateValue("PROD_SHOP",new DataValue(prodShop, Types.VARCHAR));
        ub1.addUpdateValue("PROD_HQ",new DataValue(prodHQ, Types.VARCHAR));
        ub1.addUpdateValue("PROD_OEM",new DataValue(prodOEM, Types.VARCHAR));
        ub1.addUpdateValue("ISSENSITIVE",new DataValue(isSensitive, Types.VARCHAR));
        ub1.addUpdateValue("ISCOMBINEBATCH",new DataValue(isCombineBatch, Types.VARCHAR));
        ub1.addUpdateValue("MATERIALPROPERTIES",new DataValue(materialProperties, Types.VARCHAR));
        ub1.addUpdateValue("PRODUCTTIME",new DataValue(productTime, Types.VARCHAR));
        ub1.addUpdateValue("PRODUCER",new DataValue(producer, Types.VARCHAR));
        ub1.addUpdateValue("STORAGECON",new DataValue(storageCon, Types.VARCHAR));
        ub1.addUpdateValue("MANUFACTURER",new DataValue(manufacturer, Types.VARCHAR));
        ub1.addUpdateValue("HOTLINE",new DataValue(hotLine, Types.VARCHAR));
        ub1.addUpdateValue("INGRETABLE",new DataValue(ingreTable, Types.VARCHAR));
        ub1.addUpdateValue("NETCONTENT",new DataValue(netContent, Types.VARCHAR));
        ub1.addUpdateValue("FOODPROLICNUM",new DataValue(foodProLicNum, Types.VARCHAR));
        ub1.addUpdateValue("EATINGMETHOD",new DataValue(eatingMethod, Types.VARCHAR));
        ub1.addUpdateValue("EXSTANDARD",new DataValue(exStandard, Types.VARCHAR));
        ub1.addUpdateValue("PROPADDRESS",new DataValue(propAddress, Types.VARCHAR));
        ub1.addUpdateValue("PURPRICE",new DataValue(purPrice, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID",new DataValue(req.getOpNO(),Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));


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
            for(DCP_GoodsSetUpdateReq.GoodsTemplateList goodsTemplate : goodsTemplateList){
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
                detailColumns.add("LASTMODITIME", DataValues.newDate(lastmoditime));
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
            for(DCP_GoodsSetUpdateReq.POrderTemplateList goodsTemplate : pOrderTemplateList){
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
                List<DCP_GoodsSetUpdateReq.pluName_lang> collect = pluNameLangs.stream().filter(x -> x.getLangType().equals(req.getLangType())).distinct().collect(Collectors.toList());
                if(collect.size()>0){
                    pluName=collect.get(0).getName();
                }
            }
            for (DCP_GoodsSetUpdateReq.TagList tag : tagList){
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
    protected List<InsBean> prepareInsertData(DCP_GoodsSetUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_GoodsSetUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_GoodsSetUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_GoodsSetUpdateReq req) throws Exception {
        
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        //必传字段
        String pluNo = req.getRequest().getPluNo();
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
        
        List<attr> attrList = req.getRequest().getAttrList();
        List<barcode> barcodeList = req.getRequest().getBarcodeList();
        List<unit> unitList = req.getRequest().getUnitList();
        List<pluName_lang> pluName_lang = req.getRequest().getPluName_lang();
        
        if(pluName_lang==null||pluName_lang.isEmpty()) {
            errMsg.append("商品名称多语言信息不可为空, ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if(barcodeList==null||barcodeList.isEmpty()) {
           // errMsg.append("商品条码信息不可为空, ");
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }else{
            for (DCP_GoodsSetUpdateReq.barcode barcode:barcodeList){
                if(Check.Null(barcode.getUnit())){
                  //  errMsg.append("商品条码单位不可为空, ");
                   //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                if(Check.Null(barcode.getFeatureNo())){
                  //  errMsg.append("商品条码特征码不可为空, ");
                  //  throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
                if(Check.Null(barcode.getStatus())){
                 //   errMsg.append("商品条码状态不可为空, ");
                  //  throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
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
        
        if(pluType!=null && pluType.equalsIgnoreCase("FEATURE")) {
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
        
        
        if (Check.Null(pluNo))
        {
            errMsg.append("商品编码不可为空值, ");
            isFail = true;
        }
        if (Check.Null(category))
        {
            errMsg.append("商品分类不可为空值, ");
            isFail = true;
        }
        if (Check.Null(wUnit))
        {
            errMsg.append("库存单位wUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(pluType))
        {
            errMsg.append("商品类型pluType不可为空值, ");
            isFail = true;
        }
        if (Check.Null(virtual))
        {
            errMsg.append("是否虚拟商品virtual不可为空值, ");
            isFail = true;
        }
        if (Check.Null(openPrice))
        {
            errMsg.append("是否开价商品openPrice不可为空值, ");
            isFail = true;
        }
        if (Check.Null(isWeight))
        {
            errMsg.append("是否称重商品isWeight不可为空值, ");
            isFail = true;
        }
        if (Check.Null(stockManageType))
        {
            errMsg.append("库存管控方式stockManageType不可为空值, ");
            isFail = true;
        }
        if (Check.Null(status))
        {
            errMsg.append("商品状态status不可为空值, ");
            isFail = true;
        }
        if (Check.Null(baseUnit))
        {
            errMsg.append("基准单位编码baseUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(sUnit))
        {
            errMsg.append("默认销售单位编码sUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(bomUnit))
        {
            errMsg.append("配方单位编码bomUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(prodUnit))
        {
            errMsg.append("生产单位编码prodUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(pUnit))
        {
            errMsg.append("要货单位编码pUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(purUnit))
        {
            errMsg.append("采购单位编码purUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(cUnit))
        {
            errMsg.append("盘点单位编码cUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(wUnit))
        {
            errMsg.append("库存单位编码wUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(price))
        {
            errMsg.append("建议零售价wUnit不可为空值, ");
            isFail = true;
        }
        if (Check.Null(isHotGoods))
        {
            errMsg.append("是否爆款商品不可为空值, ");
            isFail = true;
        }
        if (Check.Null(isOwnGoods))
        {
            errMsg.append("是否自有商品不可为空值, ");
            isFail = true;
        }
        if (Check.Null(isDoubleGoods))
        {
            errMsg.append("是否双拼商品不可为空值, ");
            isFail = true;
        }
        List<DCP_GoodsSetUpdateReq.GoodsTemplateList> goodsTemplateList = req.getRequest().getGoodsTemplateList();
        List<DCP_GoodsSetUpdateReq.POrderTemplateList> pOrderTemplateLists = req.getRequest().getpOrderTemplateList();
        List<DCP_GoodsSetUpdateReq.TagList> tagList = req.getRequest().getTagList();
        if(CollUtil.isNotEmpty(goodsTemplateList)){
            for (DCP_GoodsSetUpdateReq.GoodsTemplateList par : goodsTemplateList){
                if(Check.Null(par.getTemplateId())){
                    errMsg.append("模板编号不可为空值, ");
                    isFail = true;
                }
            }
        }

        if(CollUtil.isNotEmpty(pOrderTemplateLists)){
            for (DCP_GoodsSetUpdateReq.POrderTemplateList par : pOrderTemplateLists){
                if(Check.Null(par.getTemplateNo())){
                    errMsg.append("模板编号不可为空值, ");
                    isFail = true;
                }
            }
        }

        if(CollUtil.isNotEmpty(tagList)){
            for (DCP_GoodsSetUpdateReq.TagList par : tagList){
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
    protected TypeToken<DCP_GoodsSetUpdateReq> getRequestType() {
        return new TypeToken<DCP_GoodsSetUpdateReq>(){};
    }
    
    @Override
    protected DCP_GoodsSetUpdateRes getResponseType() {
        return new DCP_GoodsSetUpdateRes();
    }
    
}
