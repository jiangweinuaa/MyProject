package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dsc.spos.json.cust.req.DCP_BatchPStockInDetailReq;
import com.dsc.spos.json.cust.res.DCP_BatchPStockInDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_BatchPStockInQuery
 * 說明：分批出数单身查询
 * @author jinzma
 * @since  2020-07-07
 */
public class DCP_BatchPStockInDetail extends SPosBasicService<DCP_BatchPStockInDetailReq,DCP_BatchPStockInDetailRes> {
	Logger logger = LogManager.getLogger(DCP_BatchPStockInDetail.class.getName());
	@Override
	protected boolean isVerifyFail(DCP_BatchPStockInDetailReq req) throws Exception {
		boolean isFail = false ;
		StringBuffer errMsg = new StringBuffer();
		String pTemplateNo = req.getRequest().getpTemplateNo();
		String ofNo = req.getRequest().getOfNo();
		
		if (Check.Null(pTemplateNo) && Check.Null(ofNo) ) {
			errMsg.append("来源单号或模板编号不可都为空值, ");
			isFail = true;
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
		
	}
	
	@Override
	protected TypeToken<DCP_BatchPStockInDetailReq> getRequestType() {
		return new TypeToken<DCP_BatchPStockInDetailReq>(){} ;
	}
	
	@Override
	protected DCP_BatchPStockInDetailRes getResponseType() {
		return new DCP_BatchPStockInDetailRes();
	}
	
	@Override
	protected DCP_BatchPStockInDetailRes processJson(DCP_BatchPStockInDetailReq req) throws Exception {
		
		//查询资料
		String eId =req.geteId();
		String companyId = req.getBELFIRM();
		String orgForm = req.getOrg_Form();
		String shopId=req.getShopId();
		DCP_BatchPStockInDetailRes res =this.getResponse();
		try {
			if (Check.Null(companyId)) {
				///组织类型 0-公司  1-组织  2-门店 3-其它
				if (orgForm.equals("0")) {
					companyId=shopId;
				} else {
					String sql=" select belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
					List<Map<String, Object>> getQData = this.doQueryData(sql, null);
					companyId = getQData.get(0).get("BELFIRM").toString();
				}
			}
			
			//单身明细查询
			String sql;
			if (!Check.Null(req.getRequest().getOfNo())){
				//【ID1023852】【詹记】门店分批出数作业调整 by jinzma 20220408
				sql = this.getQuerySql_ofNo(req);
			}else{
				sql = this.getQuerySql_pTemplateNo(req);
			}
			
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_BatchPStockInDetailRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false) {
				// 拼接返回图片路径  by jinzma 20210705
				String isHttps=PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
				String httpStr=isHttps.equals("1")?"https://":"http://";
				String domainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
				if (domainName.endsWith("/")) {
					domainName = httpStr + domainName + "resource/image/";
				}else{
					domainName = httpStr + domainName + "/resource/image/";
				}
				
				///处理单价和金额小数位数  BY JZMA 20200401
				String amtLength = PosPub.getPARA_SMS(dao, eId, shopId, "amtLength");
				String distriAmtLength = PosPub.getPARA_SMS(dao, eId, shopId, "distriAmtLength");
				if (Check.Null(amtLength)||!PosPub.isNumeric(amtLength)) {
					amtLength="2";
				}
				if (Check.Null(distriAmtLength)||!PosPub.isNumeric(distriAmtLength)) {
					distriAmtLength="2";
				}
				int amtLengthInt = Integer.parseInt(amtLength);
				int distriAmtLengthInt = Integer.parseInt(distriAmtLength);
				
				
				List<Map<String, Object>> getPluPrice = new ArrayList<>();
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				MyCommon mc = new MyCommon();
				
				if (Check.Null(req.getRequest().getOfNo())){
					//商品取价计算
					List<Map<String, Object>> plus = new ArrayList<Map<String, Object>>();
					condition.put("PLUNO", true);
					//调用过滤函数
					List<Map<String, Object>> getQPlu=MapDistinct.getMap(getQData, condition);
					for (Map<String, Object> onePlu :getQPlu) {
						Map<String, Object> plu = new HashMap<String, Object>();
						plu.put("PLUNO", onePlu.get("PLUNO").toString());
						plu.put("PUNIT", onePlu.get("PUNIT").toString());
						plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
						plu.put("UNITRATIO", onePlu.get("UNITRATIO").toString());
						plus.add(plu);
					}
					getPluPrice = mc.getSalePrice_distriPrice(dao,eId,companyId, shopId,plus,companyId);
				}
				
				//原料取价计算
				List<Map<String, Object>> materialPlus = new ArrayList<Map<String, Object>>();
				condition.clear(); //查詢條件
				condition.put("MATERIAL_PLUNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQmaterialPlu=MapDistinct.getMap(getQData, condition);
				for (Map<String, Object> oneMaterialPlu :getQmaterialPlu ) {
					Map<String, Object> materialPlu = new HashMap<String, Object>();
					materialPlu.put("PLUNO", oneMaterialPlu.get("MATERIAL_PLUNO").toString());
					materialPlu.put("PUNIT", oneMaterialPlu.get("MATERIAL_PUNIT").toString());
					materialPlu.put("BASEUNIT", oneMaterialPlu.get("MATERIAL_BASEUNIT").toString());
					materialPlu.put("UNITRATIO", oneMaterialPlu.get("MATERIAL_UNITRATIO").toString());
					materialPlus.add(materialPlu);
				}
				List<Map<String, Object>> getMaterialPluPrice = mc.getSalePrice_distriPrice(dao,eId,companyId,shopId,materialPlus,companyId);
				
				//过滤商品重复，一个商品对应多个原料
				condition.clear(); //查詢條件
				condition.put("PLUNO", true);
				condition.put("FEATURENO", true);
				condition.put("PUNIT", true);
				List<Map<String, Object>> getPluDetail=MapDistinct.getMap(getQData, condition);
				int item=1;
				for (Map<String, Object> onePlu : getPluDetail) {
					DCP_BatchPStockInDetailRes.level1Elm oneLv1 = res.new level1Elm();
					String pluNo = onePlu.get("PLUNO").toString();
					String pluName = onePlu.get("PLU_NAME").toString();
					String pqty = onePlu.get("PQTY").toString();
					String punit = onePlu.get("PUNIT").toString();
					String punitName = onePlu.get("PUNITNAME").toString();
					String taskQty = onePlu.get("TASKQTY").toString();
					String listImage = onePlu.get("LISTIMAGE").toString();
					if (!Check.Null(listImage)){
						listImage=domainName+listImage;
					}
					String spec = onePlu.get("SPEC").toString();
					String scrapQty = onePlu.get("SCRAPQTY").toString();
					String mulQty = onePlu.get("MULQTY").toString();
					String bsNO = "";              // onePlu.get("BSNO").toString();
					String bsName = "";            // onePlu.get("BSNAME").toString();
					
					
					String unitRatio = onePlu.get("UNITRATIO").toString();
					String featureNo = onePlu.get("FEATURENO").toString();
					String featureName = onePlu.get("FEATURENAME").toString();
					String baseQty = onePlu.get("BASEQTY").toString();
					String baseUnit=onePlu.get("BASEUNIT").toString();
					String baseUnitName = onePlu.get("BASEUNITNAME").toString();
					
					String price="0";
					String distriPrice="0";
					String amt="0";
					String distriAmt="0";
					
					//商品取价
					Map<String, Object> condiV = new HashMap<String, Object>();
					if (Check.Null(req.getRequest().getOfNo())) {
						condiV.put("PLUNO", pluNo);
						condiV.put("PUNIT", punit);
						List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPluPrice, condiV, false);
						if (priceList != null && priceList.size() > 0) {
							price = priceList.get(0).get("PRICE").toString();
							distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
							BigDecimal price_b = new BigDecimal(price);
							amt = new BigDecimal(pqty).multiply(price_b).setScale(amtLengthInt, RoundingMode.HALF_UP).toString();
							BigDecimal distriPrice_b = new BigDecimal(distriPrice);
							distriAmt = new BigDecimal(pqty).multiply(distriPrice_b).setScale(distriAmtLengthInt, RoundingMode.HALF_UP).toString();
						}
					}else{
						price = onePlu.get("PRICE").toString();
						distriPrice = onePlu.get("DISTRIPRICE").toString();
						amt = onePlu.get("AMT").toString();
						distriAmt = onePlu.get("DISTRIAMT").toString();
					}
					//单身赋值
					oneLv1.setItem(String.valueOf(item));
					oneLv1.setPluNo(pluNo);
					oneLv1.setPluName(pluName);
					oneLv1.setPqty(pqty);
					oneLv1.setPunit(punit);
					oneLv1.setPunitName(punitName);
					oneLv1.setPrice(price);
					oneLv1.setAmt(amt);
					oneLv1.setDistriPrice(distriPrice);
					oneLv1.setDistriAmt(distriAmt);
					oneLv1.setTaskQty(taskQty);
					oneLv1.setScrapQty(scrapQty);
					oneLv1.setMulQty(mulQty);
					oneLv1.setBsNo(bsNO);
					oneLv1.setBsName(bsName);
					oneLv1.setListImage(listImage);
					oneLv1.setSpec(spec);
					oneLv1.setUnitRatio(unitRatio);
					oneLv1.setFeatureNo(featureNo);
					oneLv1.setFeatureName(featureName);
					oneLv1.setBaseQty(baseQty);
					oneLv1.setBaseUnit(baseUnit);
					oneLv1.setBaseUnitName(baseUnitName);
					
					//增加原料单身
					condiV.clear();
					condiV.put("PLUNO", pluNo);
					//condiV.put("BSNO", bsNO);
					//调用过滤函数			
					List<Map<String, Object>> getMaterialDetail=MapDistinct.getWhereMap(getQData, condiV, true);
					oneLv1.setMaterial(new ArrayList<DCP_BatchPStockInDetailRes.level2Elm>());
					for (Map<String, Object> oneMaterial : getMaterialDetail) {
						DCP_BatchPStockInDetailRes.level2Elm oneLv2 = res.new level2Elm();
						String mItem = String.valueOf(item);
						String material_pluNO = oneMaterial.get("MATERIAL_PLUNO").toString();
						String material_pluName = oneMaterial.get("MATERIAL_PLUNAME").toString();
						String material_pqty = oneMaterial.get("MATERIAL_PQTY").toString();
						String material_punit = oneMaterial.get("MATERIAL_PUNIT").toString();
						String material_punitName = oneMaterial.get("MATERIAL_PUNITNAME").toString();
						String material_baseQty = oneMaterial.get("MATERIAL_BASEQTY").toString();
						String material_baseUnit = oneMaterial.get("MATERIAL_BASEUNIT").toString();
						String material_baseUnitName = oneMaterial.get("MATERIAL_BASEUNITNAME").toString();
						String isBuckle=oneMaterial.get("ISBUCKLE").toString();
						String material_finalProdBaseQty = oneMaterial.get("MATERIAL_FINALPRODBASEQTY").toString();
						String material_rawMaterialBaseQty = oneMaterial.get("MATERIAL_RAWMATERIALBASEQTY").toString();
						String material_unitRatio = oneMaterial.get("MATERIAL_UNITRATIO").toString();
						String material_spec = oneMaterial.get("MATERIAL_SPEC").toString();
						String material_listImage = oneMaterial.get("MATERIAL_LISTIMAGE").toString();
						if (!Check.Null(material_listImage)){
							material_listImage = domainName+material_listImage;
						}
						//原料取价
						String material_price="0";
						String material_distriPrice="0";
						String material_amt="0";
						String material_distriAmt="0";
						condiV.clear();
						condiV.put("PLUNO",material_pluNO);
						condiV.put("PUNIT",material_punit);
						List<Map<String, Object>> materialPriceList= MapDistinct.getWhereMap(getMaterialPluPrice, condiV, false);
						if(materialPriceList!=null && materialPriceList.size()>0 ) {
							material_price=materialPriceList.get(0).get("PRICE").toString();
							material_distriPrice=materialPriceList.get(0).get("DISTRIPRICE").toString();
							BigDecimal material_price_b=new BigDecimal(material_price);
							material_amt = new BigDecimal(material_pqty).multiply(material_price_b).setScale(amtLengthInt, RoundingMode.HALF_UP).toString();
							BigDecimal material_distriPrice_b=new BigDecimal(material_distriPrice);
							material_distriAmt = new BigDecimal(material_pqty).multiply(material_distriPrice_b).setScale(distriAmtLengthInt, RoundingMode.HALF_UP).toString();
						}
						
						oneLv2.setmItem(mItem);
						oneLv2.setMaterial_pluNo(material_pluNO);
						oneLv2.setMaterial_pluName(material_pluName);
						oneLv2.setMaterial_pqty(material_pqty);
						oneLv2.setMaterial_punit(material_punit);
						oneLv2.setMaterial_punitName(material_punitName);
						oneLv2.setMaterial_price(material_price);
						oneLv2.setMaterial_distriPrice(material_distriPrice);
						oneLv2.setMaterial_amt(material_amt);
						oneLv2.setMaterial_distriAmt(material_distriAmt);
						oneLv2.setMaterial_baseQty(material_baseQty);
						oneLv2.setMaterial_baseUnit(material_baseUnit);
						oneLv2.setMaterial_baseUnitName(material_baseUnitName);
						oneLv2.setMaterial_finalProdBaseQty(material_finalProdBaseQty);
						oneLv2.setMaterial_rawMaterialBaseQty(material_rawMaterialBaseQty);
						oneLv2.setMaterial_spec(material_spec);
						oneLv2.setMaterial_listImage(material_listImage);
						oneLv2.setMaterial_isFeature("N");
						oneLv2.setIsBuckle(isBuckle);
						oneLv2.setMaterial_unitRatio(material_unitRatio);
						
						oneLv1.getMaterial().add(oneLv2);
					}
					res.getDatas().add(oneLv1);
					item++;
					
				}
			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"未查询到明细资料，请重新查询！");
			}
			
			return res;
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
		
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_BatchPStockInDetailReq req) throws Exception {
		return null;
	}
	
	private String getQuerySql_pTemplateNo(DCP_BatchPStockInDetailReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();
		String pTemplateNo = req.getRequest().getpTemplateNo();  //加工任务或者模板
		
		sqlbuf.append(" "
				+ " select glang.plu_name,fn.featurename,gul.spec,image.listimage,bom.mulqty,rlang.reason_name as bsname,"
				+ " ulang1.uname as punitName,ulang2.uname as baseUnitName,bom.material_pluno,bom.material_isFeature,"
				+ " bom.material_punit,bom.qty as material_finalProdBaseQty,bom.material_pqty as material_rawMaterialBaseQty,"
				+ " bom.material_pqty * nvl(c.pqty,0) AS material_pqty,"
				+ " mgoodsunit.unitratio as material_unitRatio,"
				+ " bom.isbuckle,bom.sortid,"
				+ " mimage.listImage as material_listImage,mgul.spec as material_spec,mgb.baseunit as material_baseUnit,"
				+ " bom.material_pqty * nvl(c.pqty,0) * mgoodsunit.unitratio as material_baseqty,"
				+ " mglang.plu_name as material_pluName,mulang1.uname as material_punitname,mulang2.uname as material_baseUnitName,"
		);
		
		//无任务单出数，有模板的按模板汇总显示  没有模板的汇总在一起显示       不包含今天的数据
		sqlbuf.append(" a.ptemplateno as docno,b.item,b.pluno,f.featureno,b.punit,gb.baseunit,c.bsno,nvl(c.scrapqty,0) as scrapqty,"
				+ " nvl(c.pqty,0) as pqty,nvl(c.baseqty,0) as baseqty,"
				+ " goodsunit.unitratio,"
				+ " 0 as taskqty"
				+ " from dcp_ptemplate a"
				+ " inner join dcp_ptemplate_detail b on a.ptemplateno=b.ptemplateno and a.eid=b.eid and a.doc_type=b.doc_type and b.ptemplateno= '"+pTemplateNo+"'"
				+ " left join dcp_ptemplate_shop c on a.eid=c.eid and a.ptemplateno=c.ptemplateno and a.doc_type=c.doc_type and c.shopid='"+shopId+"'"
				+ " inner join dcp_goods gb on gb.eid=b.eid and gb.pluno=b.pluno"
				+ " left join dcp_goods_feature f on b.eid=f.eid and b.pluno=f.pluno and f.status='100'"
				+ " inner join dcp_goods_unit goodsunit "
				+ " on goodsunit.eid=a.eid and goodsunit.pluno=b.pluno and goodsunit.ounit=b.punit and prod_unit_use='Y' "
				+ " and goodsunit.unit=gb.baseunit"
				+ " left join ("
				+ " select a.pluno,a.featureno,a.bsno,sum(a.pqty) as pqty,sum(a.baseqty) as baseqty,sum(a.scrap_qty) as scrapqty from dcp_pstockin_detail a"
				+ " inner join dcp_pstockin b on a.eid=b.eid and a.shopid=b.shopid and a.pstockinno=b.pstockinno and a.account_date=b.account_date"
				+ " where a.eid='"+ eId +"' and a.shopid='"+ shopId +"' and b.ptemplateno = '"+pTemplateNo+"'"
				+ " group by a.pluno,a.featureno,a.bsno)c on b.pluno=c.pluno and (f.featureno=c.featureno or c.featureno is null)"
		);
		
		sqlbuf.append(" inner join ("
				+ " select distinct a.pluno,a.unit,b.qty,a.mulqty,b.material_pluno,b.material_unit as material_punit,b.material_qty as material_pqty,"
				+ " b.loss_rate,b.isbuckle,b.isreplace,nvl2(f.featureno,'Y','N') as material_isFeature,b.sortid from ("
				+ " select * from ("
				+ " select a.*,row_number() over (partition by pluno order by effdate desc) as rn from dcp_bom a"
				+ " left join dcp_bom_range c on a.eid=c.eid and a.bomno=c.bomno and c.shopid ='"+shopId+"'"
				+ " where a.eId='"+eId+"' and trunc(a.effdate)<=trunc(sysdate) and a.status='100' and a.bomtype = '0'"
				+ " and (a.restrictshop=0 or (a.restrictshop=1 and c.shopid is not null))"
				+ " ) bom where bom.rn=1 ) a"
				+ " inner join dcp_bom_material b on a.eid=b.eid and a.bomno=b.bomno and trunc(b.material_bdate) <=trunc(sysdate) "
				+ " and trunc(sysdate)<=trunc(b.material_edate)"
				+ " left join dcp_goods_feature f on b.eid=f.eid and b.material_pluno=f.pluno and f.status='100'"
				+ " ) bom on b.pluno =bom.pluno and b.punit=bom.unit"
				+ " left join dcp_goods_lang glang on b.eid=glang.eid and b.pluno=glang.pluno and glang.lang_type='"+langType+"'"
				+ " left join dcp_goods_feature_lang fn on a.eid=fn.eid and b.pluno=fn.pluno and c.featureno=fn.featureno  and fn.lang_type='"+langType+"'"
				+ " left join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
				+ " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' "
				+ " left join dcp_reason_lang rlang on a.eid=rlang.eid and c.bsno=rlang.bsno and rlang.lang_type='"+langType+"' and rlang.bstype='3'"
				+ " left join dcp_unit_lang ulang1 on ulang1.eid=a.eid and ulang1.unit=b.punit and ulang1.lang_type='"+langType+"'"
		);
		
		sqlbuf.append(" left join dcp_unit_lang ulang2 on ulang2.eid=a.eid and ulang2.unit=gb.baseunit and ulang2.lang_type='"+langType+"'");
		sqlbuf.append(" "
				+ " inner join dcp_goods mgb on mgb.eid=b.eid and mgb.pluno=bom.material_pluno"
				+ " inner join dcp_goods_unit mgoodsunit "
				+ " on mgoodsunit.eid=a.eid and mgoodsunit.pluno=bom.material_pluno and mgoodsunit.ounit=bom.material_punit "
				+ " and mgoodsunit.bom_unit_use='Y' and mgoodsunit.unit=mgb.baseunit"
				+ " left join dcp_goodsimage mimage on mimage.eid=a.eid and mimage.pluno=bom.material_pluno and mimage.apptype='ALL'"
				+ " left join dcp_goods_unit_lang mgul on a.eid=mgul.eid and bom.material_pluno=mgul.pluno and bom.material_punit=mgul.ounit and mgul.lang_type='"+langType+"'"
				+ " left join dcp_goods_lang mglang on b.eid=mglang.eid and bom.material_pluno=mglang.pluno and mglang.lang_type='"+langType+"'"
				+ " left join dcp_unit_lang mulang1 on mulang1.eid=a.eid and mulang1.unit=bom.material_punit and mulang1.lang_type='"+langType+"'"
				+ " left join dcp_unit_lang mulang2 on mulang2.eid=a.eid and mulang2.unit=mgb.baseunit and mulang2.lang_type='"+langType+"'"
				+ " ");
		sqlbuf.append(" where a.eid='"+ eId +"' and a.doc_type='2' and a.ptemplateno= '"+pTemplateNo+"' and (c.shopid is not null or a.shoptype='1') ");
		sqlbuf.append(" order by item,pluno,sortid");
		sql = sqlbuf.toString();
		return sql;
	}
	
	private String getQuerySql_ofNo(DCP_BatchPStockInDetailReq req) throws Exception {
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();
		String ofNo = req.getRequest().getOfNo();  //加工任务
		
		sqlbuf.append(""
				+ " select a.processtasksumno as docno,b.pluno,glang.plu_name,b.featureno,fn.featurename,gul.spec,image.listimage,"
				+ " b.pqty as taskqty,b.punit,b.baseunit,ulang1.uname as punitName,ulang2.uname as baseUnitName,"
				+ " b.unit_ratio as unitratio,"
				+ " bom.material_pluno,mglang.plu_name as material_pluName,mimage.listImage as material_listImage,mgul.spec as material_spec,"
				+ " bom.mulqty,bom.qty as material_finalProdBaseQty,bom.material_pqty as material_rawMaterialBaseQty,"
				+ " mgoodsunit.unitratio as material_unitRatio,"
				+ " bom.material_punit,mulang1.uname as material_punitname,"
				+ " mgb.baseunit as material_baseUnit,mulang2.uname as material_baseUnitName,"
				+ " bom.isbuckle,bom.sortid,"
				+ " bom.material_pqty*nvl(b.stockin_qty,0) as material_pqty,"
				+ " bom.material_pqty*nvl(b.stockin_qty,0) * mgoodsunit.unitratio as material_baseqty,"
				+ " b.scrap_qty as scrapqty,b.stockin_qty as pqty,b.stockin_qty*b.unit_ratio as baseqty,"
				+ " b.price,b.distriprice,b.amt,b.distriamt"
				+ " from dcp_processtasksum a"
				+ " inner join dcp_processtasksum_detail b on a.eid=b.eid and a.shopid=b.shopid and a.processtasksumno=b.processtasksumno"
				+ " inner join ("
				+ " select distinct a.pluno,a.unit,a.mulqty,b.material_pluno,b.qty,b.material_unit as material_punit,"
				+ " b.material_qty as material_pqty,b.loss_rate,b.isbuckle,b.isreplace,b.sortid"
				+ " from ("
				+ " select * from ("
				+ " select a.*,row_number() over (partition by pluno order by effdate desc) as rn from dcp_bom a"
				+ " left join dcp_bom_range c on a.eid=c.eid and a.bomno=c.bomno and c.shopid ='"+shopId+"'"
				+ " where a.eId='"+eId+"' and trunc(sysdate)>=trunc(a.effdate) and a.status='100' and a.bomtype = '0'"
				+ " and (a.restrictshop=0 or (a.restrictshop=1 and c.shopid is not null))"
				+ " ) bom where bom.rn=1"
				+ " ) a"
				+ " inner join dcp_bom_material b on a.eid=b.eid and a.bomno=b.bomno"
				+ " and trunc(sysdate)>=trunc(b.material_bdate) and trunc(sysdate)<=trunc(b.material_edate)"
				+ " ) bom on b.pluno =bom.pluno and b.punit=bom.unit"
				+ " left join dcp_goods_lang glang on b.eid=glang.eid and b.pluno=glang.pluno and glang.lang_type='"+langType+"'"
				+ " left join dcp_goods_feature_lang fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno and fn.lang_type='"+langType+"'"
				+ " left join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
				+ " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL'"
				+ " left join dcp_unit_lang ulang1 on ulang1.eid=a.eid and ulang1.unit=b.punit and ulang1.lang_type='"+langType+"'"
				+ " left join dcp_unit_lang ulang2 on ulang2.eid=a.eid and ulang2.unit=b.baseunit and ulang2.lang_type='"+langType+"'"
				+ " inner join dcp_goods mgb on mgb.eid=b.eid and mgb.pluno=bom.material_pluno"
				+ " inner join dcp_goods_unit mgoodsunit on mgoodsunit.eid=a.eid and mgoodsunit.pluno=bom.material_pluno"
				+ " and mgoodsunit.ounit=bom.material_punit and mgoodsunit.bom_unit_use='Y' and mgoodsunit.unit=mgb.baseunit"
				+ " left join dcp_goodsimage mimage on mimage.eid=a.eid and mimage.pluno=bom.material_pluno and mimage.apptype='ALL'"
				+ " left join dcp_goods_unit_lang mgul on a.eid=mgul.eid and bom.material_pluno=mgul.pluno"
				+ " and bom.material_punit=mgul.ounit and mgul.lang_type='"+langType+"'"
				+ " left join dcp_goods_lang mglang on b.eid=mglang.eid and bom.material_pluno=mglang.pluno and mglang.lang_type='"+langType+"'"
				+ " left join dcp_unit_lang mulang1 on mulang1.eid=a.eid and mulang1.unit=bom.material_punit and mulang1.lang_type='"+langType+"'"
				+ " left join dcp_unit_lang mulang2 on mulang2.eid=a.eid and mulang2.unit=mgb.baseunit and mulang2.lang_type='"+langType+"'"
				+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.processtasksumno='"+ofNo+"'"
				+ " order by pluno,sortid"
				+ " ");

		
		 
		
		
		
		
		

		
		/*
		sqlbuf.append(" "
				+ " select glang.plu_name,fn.featurename,gul.spec,image.listimage,bom.mulqty,rlang.reason_name as bsname,"
				+ " ulang1.uname as punitName,ulang2.uname as baseUnitName,bom.material_pluno,bom.material_isFeature,"
				+ " bom.material_punit,bom.qty as material_finalProdBaseQty,bom.material_pqty as material_rawMaterialBaseQty,"
				+ " bom.material_pqty * nvl(c.pqty,0) AS material_pqty,"
				+ " mgoodsunit.unitratio as material_unitRatio,"
				+ " bom.isbuckle,bom.sortid,"
				+ " mimage.listImage as material_listImage,mgul.spec as material_spec,mgb.baseunit as material_baseUnit,"
				+ " bom.material_pqty * nvl(c.pqty,0) * mgoodsunit.unitratio as material_baseqty,"
				+ " mglang.plu_name as material_pluName,mulang1.uname as material_punitname,mulang2.uname as material_baseUnitName,"
				+ " a.processtaskno as docno,b.item,b.pluno,b.featureno,b.punit,b.baseunit,c.bsno,nvl(c.scrapqty,0) as scrapqty,"
				+ " nvl(c.pqty,0) as pqty,nvl(c.baseqty,0) as baseqty,b.unit_ratio as unitratio,b.pqty as taskqty from dcp_processtask a"
				+ " inner join dcp_processtask_detail b on a.eid=b.eid and a.shopid=b.shopid and a.processtaskno=b.processtaskno" //and a.bdate=b.bdate"
				+ " left join ("
				+ " select a.pluno,a.featureno,a.bsno,sum(a.pqty) as pqty,sum(a.baseqty) as baseqty,sum(a.scrap_qty) as scrapqty from dcp_pstockin_detail a"
				+ " inner join dcp_pstockin b on a.eid=b.eid and a.shopid=b.shopid and a.pstockinno=b.pstockinno" // and a.account_date=b.account_date"
				+ " where a.eid='"+eId+"' and a.shopid='"+ shopId +"' and b.ofno = '"+ofNo+"' group by a.pluno,a.featureno,a.bsno )c"
				+ " on b.pluno=c.pluno and (b.featureno=c.featureno or c.featureno is null)"
		        + " inner join ("
				+ " select distinct a.pluno,a.unit,b.qty,a.mulqty,b.material_pluno,b.material_unit as material_punit,b.material_qty as material_pqty,"
				+ " b.loss_rate,b.isbuckle,b.isreplace,nvl2(f.featureno,'Y','N') as material_isFeature,b.sortid from ("
				+ " select * from ("
				+ " select a.*,row_number() over (partition by pluno order by effdate desc) as rn from dcp_bom a"
				+ " left join dcp_bom_range c on a.eid=c.eid and a.bomno=c.bomno and c.shopid ='"+shopId+"'"
				+ " where a.eId='"+eId+"' and trunc(a.effdate)<=trunc(sysdate) and a.status='100' and a.bomtype = '0'"
				+ " and (a.restrictshop=0 or (a.restrictshop=1 and c.shopid is not null))"
				+ " ) bom where bom.rn=1 ) a"
				+ " inner join dcp_bom_material b on a.eid=b.eid and a.bomno=b.bomno and trunc(b.material_bdate) <=trunc(sysdate) "
				+ " and trunc(sysdate)<=trunc(b.material_edate)"
				+ " left join dcp_goods_feature f on b.eid=f.eid and b.material_pluno=f.pluno and f.status='100'"
				+ " ) bom on b.pluno =bom.pluno and b.punit=bom.unit"
				+ " left join dcp_goods_lang glang on b.eid=glang.eid and b.pluno=glang.pluno and glang.lang_type='"+langType+"'"
				+ " left join dcp_goods_feature_lang fn on a.eid=fn.eid and b.pluno=fn.pluno and c.featureno=fn.featureno  and fn.lang_type='"+langType+"'"
				+ " left join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
				+ " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' "
				+ " left join dcp_reason_lang rlang on a.eid=rlang.eid and c.bsno=rlang.bsno and rlang.lang_type='"+langType+"' and rlang.bstype='3'"
				+ " left join dcp_unit_lang ulang1 on ulang1.eid=a.eid and ulang1.unit=b.punit and ulang1.lang_type='"+langType+"'"
		);
		sqlbuf.append(" left join dcp_unit_lang ulang2 on ulang2.eid=a.eid and ulang2.unit=b.baseunit and ulang2.lang_type='"+langType+"'");
		sqlbuf.append(" "
				+ " inner join dcp_goods mgb on mgb.eid=b.eid and mgb.pluno=bom.material_pluno"
				+ " inner join dcp_goods_unit mgoodsunit "
				+ "  on mgoodsunit.eid=a.eid and mgoodsunit.pluno=bom.material_pluno and mgoodsunit.ounit=bom.material_punit "
				+ "  and mgoodsunit.bom_unit_use='Y' and mgoodsunit.unit=mgb.baseunit"
				+ " left join dcp_goodsimage mimage on mimage.eid=a.eid and mimage.pluno=bom.material_pluno and mimage.apptype='ALL'"
				+ " left join dcp_goods_unit_lang mgul on a.eid=mgul.eid and bom.material_pluno=mgul.pluno and bom.material_punit=mgul.ounit and mgul.lang_type='"+langType+"'"
				+ " left join dcp_goods_lang mglang on b.eid=mglang.eid and bom.material_pluno=mglang.pluno and mglang.lang_type='"+langType+"'"
				+ " left join dcp_unit_lang mulang1 on mulang1.eid=a.eid and mulang1.unit=bom.material_punit and mulang1.lang_type='"+langType+"'"
				+ " left join dcp_unit_lang mulang2 on mulang2.eid=a.eid and mulang2.unit=mgb.baseunit and mulang2.lang_type='"+langType+"'"
				+ " ");
		
		sqlbuf.append(" where a.eid='"+ eId +"' and a.shopid='"+ shopId +"' and a.processtaskno='"+ofNo+"'");
		sqlbuf.append(" order by item,pluno,sortid");
		*/
		
		
		
		
		return sqlbuf.toString();
	}
	
	
	
}
