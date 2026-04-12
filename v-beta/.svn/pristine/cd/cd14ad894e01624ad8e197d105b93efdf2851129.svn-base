package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SaleRecordUpdateReq;
import com.dsc.spos.json.cust.req.DCP_SaleRecordUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_SaleRecordUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_SaleRecordUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 面销记录
 * @author yuanyy 2019-12-31
 *
 */
public class DCP_SaleRecordUpdate extends SPosAdvanceService<DCP_SaleRecordUpdateReq, DCP_SaleRecordUpdateRes> {

	@Override
	protected void processDUID(DCP_SaleRecordUpdateReq req, DCP_SaleRecordUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String shopId = req.getShopId();
			String salesRecordNo = req.getRequest().getSalesRecordNo();
			String customName = req.getRequest().getCustomName();
			String sex = req.getRequest().getSex();
			String mobile = req.getRequest().getMobile();
			String receptionTime = req.getRequest().getReceptionTime();
			String customType = req.getRequest().getCustomType();
			String labels = req.getRequest().getLabels();
			String remark = req.getRequest().getRemark();
			
			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String lastmodiTime =  matter.format(dt);
			
			SimpleDateFormat matter2 = new SimpleDateFormat("yyyy-MM-dd");
			String billDate =  matter2.format(dt);
			
			DelBean db1 = new DelBean("DCP_SALESRECORD_DETAIL");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			db1.addCondition("SALESRECORDNO", new DataValue(salesRecordNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			this.doExecuteDataToDB();
			
			List<level2Elm> intentionalProduct = req.getRequest().getIntentionalProduct();
			if(intentionalProduct != null && intentionalProduct.size() > 0){
				
				DataValue[] insValue = null;
				
				for (level2Elm par : intentionalProduct) {
					int insColCt = 0;	
					String[] columnsName = {
							"EID", "SHOPID","SALESRECORDNO","PLUNO", 
							"PLUNAME", "ITEM", "CATEGORYNAME", "WUNIT", "PRICE", "ORIGINALPRICE","FILENAME"
					};

					DataValue[] columnsVal = new DataValue[columnsName.length];					
					for (int i = 0; i < columnsVal.length; i++) { 
						String keyVal = null;
						switch (i) {
						case 0:
							keyVal = eId;	
							break;
						case 1:
							keyVal = shopId;
							break;
						case 2:
							keyVal = salesRecordNo;
							break;
						case 3:  
							keyVal = par.getPluNo();
							break;
						case 4:  
							keyVal = par.getPluName();  
							break;	
						case 5:  
							keyVal = par.getItem();
							break;
						case 6:  
							keyVal = par.getCategoryName();
							break;
						case 7:  
							keyVal = par.getWunit();
							break;
						case 8:  
							keyVal = par.getPrice();
							break;
							
						case 9:  
							keyVal = par.getOriginalPrice();
							break;
						case 10:  
							keyVal = par.getImageFileName();
							break;
						default:
							break;
						}

						if (keyVal != null) {
							insColCt++;
							columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
						} else {
							columnsVal[i] = null;
						}
						
						
					}

					String[] columns  = new String[insColCt];
					insValue = new DataValue[insColCt];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt = 0;
					for (int i = 0; i < columnsVal.length; i++){
						if (columnsVal[i] != null){
							columns[insColCt] = columnsName[i];
							insValue[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue.length) break;
						}
					}
					InsBean ib = new InsBean("DCP_SALESRECORD_DETAIL", columns);
					ib.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib));
					
				}
				
			}

			UptBean ub1 = null;
			ub1 = new UptBean("DCP_SALESRECORD");
			ub1.addUpdateValue("CUSTOMNAME", new DataValue(customName, Types.VARCHAR));
			ub1.addUpdateValue("SEX", new DataValue(sex, Types.VARCHAR));
			ub1.addUpdateValue("MOBILE", new DataValue(mobile, Types.VARCHAR));
			ub1.addUpdateValue("CUSTOMTYPE", new DataValue(customType, Types.VARCHAR));
			ub1.addUpdateValue("RECEPTIONTIME", new DataValue(receptionTime, Types.VARCHAR));
			ub1.addUpdateValue("REMARK", new DataValue(remark, Types.VARCHAR));
			ub1.addUpdateValue("LABELS", new DataValue(labels, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmodiTime, Types.DATE));
			ub1.addUpdateValue("BILLDATE", new DataValue(billDate, Types.DATE));
			
			// condition
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			ub1.addCondition("SALESRECORDNO", new DataValue(salesRecordNo, Types.VARCHAR));

			this.addProcessData(new DataProcessBean(ub1));
			
			this.doExecuteDataToDB();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SaleRecordUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SaleRecordUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SaleRecordUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SaleRecordUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
		String salesRecordNo = req.getRequest().getSalesRecordNo();
		
		if (Check.Null(salesRecordNo)) 
		{
			errMsg.append("单号不可为空值, ");
			isFail = true;
		}
		
		if (request!=null && request.getIntentionalProduct() !=null)
		{
			
			List<level2Elm> datas = request.getIntentionalProduct();
			for (level2Elm lv2 : datas) {
				String item = lv2.getItem();
				String pluNo = lv2.getPluNo();
				if (Check.Null(item)) 
				{
					errMsg.append("商品项次不可为空值, ");
					isFail = true;
				}
				if (Check.Null(pluNo)) 
				{
					errMsg.append("商品编码不可为空值, ");
					isFail = true;
				}
				
			}
			
			
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;


	}

	@Override
	protected TypeToken<DCP_SaleRecordUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleRecordUpdateReq>(){};
	}

	@Override
	protected DCP_SaleRecordUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleRecordUpdateRes();
	}
	
	
}
