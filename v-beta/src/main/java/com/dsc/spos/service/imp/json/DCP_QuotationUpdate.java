package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_QuotationUpdateReq;
import com.dsc.spos.json.cust.req.DCP_QuotationUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_QuotationUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 报价单
 * @author yuanyy
 *
 */
public class DCP_QuotationUpdate extends SPosAdvanceService<DCP_QuotationUpdateReq, DCP_QuotationUpdateRes> {

	@Override
	protected void processDUID(DCP_QuotationUpdateReq req, DCP_QuotationUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId();
			String shopId = req.getRequest().getShopId();
			String shopName = req.getRequest().getShopName();
			String telephone = req.getRequest().getTelephone();
			String shopEmail = req.getRequest().getShopEmail();
			String address = req.getRequest().getAddress();
			String customName = req.getRequest().getCustomName();
			String mobile = req.getRequest().getMobile();
			String customEmail = req.getRequest().getCustomEmail();
			String referees = req.getRequest().getReferees();
			String remark = req.getRequest().getRemark();
			String tot_Amt = req.getRequest().getTot_Amt();	
			String opNo = req.getOpNO();
			
			String quotationRecordNo = req.getRequest().getQuotationRecordNo();
			
			List<level2Elm> quotationGoods = req.getRequest().getQuotationGoods();
		
			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime =  matter.format(dt);
			
			DelBean db1 = new DelBean("DCP_QUOTATIONRECORD_DETAIL");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("QUOTATIONRECORDNO", new DataValue(quotationRecordNo, Types.VARCHAR));
			db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			if(quotationGoods != null && quotationGoods.size() > 0){
				String[] columns_hms ={"EID","QUOTATIONRECORDNO","SHOPID","ITEM","PLUNO","WUNIT",
						"PRICE" , "QTY" , "REMARK" , "OPNO", "LASTMODITIME","CREATETIME","AMT","FILENAME",
						"PLUBARCODE","UNIT","SPECNAME","ATTRNAME","GOODSGROUP","ISPACKAGE","PCLASSNO","PACKAGETYPE",
						"PACKAGEMITEM","DISC" };
				
				for (level2Elm lv2 : quotationGoods) {
					String pluNo = lv2.getPluNo();
					String pluName = lv2.getPluName();
					String item = lv2.getItem();
					String wunit = lv2.getWunit();
					String detailRemark = lv2.getRemark();
					String price = lv2.getPrice()==null?"0":lv2.getPrice();
					String qty = lv2.getQty() == null?"0":lv2.getQty();
					String amt = lv2.getAmt() == null?"0":lv2.getAmt();
					String fileName = lv2.getImageFileName();
					
					String pluBarcode = lv2.getPluBarcode();
					String unit = lv2.getUnit();
					String specName = lv2.getSpecName();
					String attrName = lv2.getAttrName();
					String goodsGroup = lv2.getGoodsGroup();
					String isPackage = lv2.getIsPackage();
					String pClassNo = lv2.getpClassNo();
					String packageType = lv2.getPackageType();
					String packageMitem = lv2.getPackageMitem();
					String disc = lv2.getDisc();
					
					List<level2Elm> childDatas = lv2.getChildGoodsList();
					if(childDatas != null && !childDatas.isEmpty()){
						for (level2Elm cdMap : childDatas) {
							String cdpluNo = cdMap.getPluNo();
							String cdpluName = cdMap.getPluName();
							String cditem = cdMap.getItem();
							String cdwunit = cdMap.getWunit();
							String cddetailRemark = cdMap.getRemark();
							String cdprice = cdMap.getPrice()==null?"0":cdMap.getPrice();
							String cdqty = cdMap.getQty() == null?"0":cdMap.getQty();
							String cdamt = cdMap.getAmt() == null?"0":cdMap.getAmt();
							String cdfileName = cdMap.getImageFileName();
							
							String cdpluBarcode = cdMap.getPluBarcode();
							String cdunit = cdMap.getUnit();
							String cdspecName = cdMap.getSpecName()==null?"":cdMap.getSpecName();
							String cdattrName = cdMap.getAttrName()==null?"":cdMap.getAttrName();
							String cdgoodsGroup = cdMap.getGoodsGroup()==null?"":cdMap.getGoodsGroup();
							String cdisPackage = cdMap.getIsPackage()==null?"Y":cdMap.getIsPackage();
							String cdpClassNo = cdMap.getpClassNo()==null?"":cdMap.getpClassNo();
							String cdpackageType = cdMap.getPackageType()==null?"3":cdMap.getPackageType();
							String cddisc = cdMap.getDisc()==null?"0":cdMap.getDisc();
							
							DataValue[] insValue_hms = new DataValue[] 
							{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(quotationRecordNo, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(item, Types.VARCHAR),
								new DataValue(cdpluNo, Types.VARCHAR),
								new DataValue(cdwunit, Types.VARCHAR),
								new DataValue(cdprice, Types.VARCHAR),
								new DataValue(cdqty, Types.VARCHAR),
								new DataValue(cddetailRemark, Types.VARCHAR),
								new DataValue(opNo, Types.VARCHAR),
								new DataValue(createTime, Types.DATE),
								new DataValue(createTime, Types.DATE),
								new DataValue(cdamt, Types.VARCHAR),
								new DataValue(cdfileName, Types.VARCHAR),
								new DataValue(cdpluBarcode, Types.VARCHAR),
								new DataValue(cdunit, Types.VARCHAR),
								
								new DataValue(cdspecName, Types.VARCHAR),
								new DataValue(cdattrName, Types.VARCHAR),
								new DataValue(cdgoodsGroup, Types.VARCHAR),
								new DataValue(cdisPackage, Types.VARCHAR),
								new DataValue(cdpClassNo, Types.VARCHAR),
								new DataValue(cdpackageType, Types.VARCHAR),
								new DataValue(item, Types.VARCHAR), // packageMitem 主商品项次，存item的值，不以前端为准
								new DataValue(cddisc, Types.VARCHAR)
								
							};
							
							InsBean ib_hms = new InsBean("DCP_QUOTATIONRECORD_DETAIL", columns_hms);
							ib_hms.addValues(insValue_hms);
							this.addProcessData(new DataProcessBean(ib_hms));
							
						}
						
					}
					
					DataValue[] insValue_hms = new DataValue[] 
					{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(quotationRecordNo, Types.VARCHAR), 
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(item, Types.VARCHAR),
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(wunit, Types.VARCHAR),
						new DataValue(price, Types.VARCHAR),
						new DataValue(qty, Types.VARCHAR),
						new DataValue(detailRemark, Types.VARCHAR),
						new DataValue(opNo, Types.VARCHAR),
						new DataValue(createTime, Types.DATE),
						new DataValue(createTime, Types.DATE),
						new DataValue(amt, Types.VARCHAR),
						new DataValue(fileName, Types.VARCHAR),
						new DataValue(pluBarcode, Types.VARCHAR),
						new DataValue(unit, Types.VARCHAR),
						new DataValue(specName, Types.VARCHAR),
						new DataValue(attrName, Types.VARCHAR),
						new DataValue(goodsGroup, Types.VARCHAR),
						new DataValue(isPackage, Types.VARCHAR),
						new DataValue(pClassNo, Types.VARCHAR),
						new DataValue(packageType, Types.VARCHAR),
						new DataValue(packageMitem, Types.VARCHAR),
						new DataValue(disc, Types.VARCHAR)
						
					};
					
					InsBean ib_hms = new InsBean("DCP_QUOTATIONRECORD_DETAIL", columns_hms);
					ib_hms.addValues(insValue_hms);
					this.addProcessData(new DataProcessBean(ib_hms));
					
				}
				
				
			}
			
			UptBean ub2 = new UptBean("DCP_QUOTATIONRECORD");
			
			ub2.addUpdateValue("TELEPHONE", new DataValue(telephone, Types.VARCHAR));
			ub2.addUpdateValue("SHOPEMAIL", new DataValue(shopEmail, Types.VARCHAR));
			ub2.addUpdateValue("ADDRESS", new DataValue(address, Types.VARCHAR));
			ub2.addUpdateValue("CUSTOMNAME", new DataValue(customName, Types.VARCHAR));
			ub2.addUpdateValue("MOBILE", new DataValue(mobile, Types.VARCHAR));
			ub2.addUpdateValue("CUSTOMEMAIL", new DataValue(customEmail, Types.VARCHAR));
			
			ub2.addUpdateValue("REFEREES", new DataValue(referees, Types.VARCHAR));
			ub2.addUpdateValue("REMARK", new DataValue(remark, Types.VARCHAR));
			ub2.addUpdateValue("TOT_AMT", new DataValue(tot_Amt, Types.VARCHAR));
			ub2.addUpdateValue("OPNO", new DataValue(opNo, Types.VARCHAR));
			ub2.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
			ub2.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
			
			// condition
			ub2.addCondition("QUOTATIONRECORDNO", new DataValue(quotationRecordNo, Types.VARCHAR));
			ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub2));
			
			this.doExecuteDataToDB();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceStatus("200");
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败！");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_QuotationUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_QuotationUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_QuotationUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_QuotationUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		String quotationRecordNo = req.getRequest().getQuotationRecordNo();

		if (Check.Null(quotationRecordNo)) {
			errCt++;
			errMsg.append("报价单号不可为空值, ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_QuotationUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_QuotationUpdateReq>(){};
	}

	@Override
	protected DCP_QuotationUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_QuotationUpdateRes();
	}
	
}
