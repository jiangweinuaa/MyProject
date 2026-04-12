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
import com.dsc.spos.json.cust.req.DCP_SaleRecordCreateReq;
import com.dsc.spos.json.cust.req.DCP_SaleRecordCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_SaleRecordCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_SaleRecordCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 面销记录
 * @author yuanyy 2019-12-31
 *
 */
public class DCP_SaleRecordCreate extends SPosAdvanceService<DCP_SaleRecordCreateReq, DCP_SaleRecordCreateRes> {

	@Override
	protected void processDUID(DCP_SaleRecordCreateReq req, DCP_SaleRecordCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String opNo = req.getOpNO();
			String eId = req.geteId();
			String shopId = req.getShopId();
			String salesRecordNo = this.queryMaxNo(req);
			String customName = req.getRequest().getCustomName();
			String sex = req.getRequest().getSex();
			String mobile = req.getRequest().getMobile();
			String receptionTime = req.getRequest().getReceptionTime();
			String customType = req.getRequest().getCustomType();
			String labels = req.getRequest().getLabels();
			String remark = req.getRequest().getRemark();
			
			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime =  matter.format(dt);
			
			SimpleDateFormat matter2 = new SimpleDateFormat("yyyy-MM-dd");
			String billDate =  matter2.format(dt);
			
			List<level2Elm> intentionalProduct = req.getRequest().getIntentionalProduct();
			if(intentionalProduct != null && intentionalProduct.size() > 0){
				
				DataValue[] insValue = null;
				
				for (level2Elm par : intentionalProduct) {
					int insColCt = 0;	
					String[] columnsName = {
							"EID", "SHOPID","SALESRECORDNO","PLUNO", 
							"PLUNAME", "ITEM", "CATEGORYNAME", "WUNIT", "PRICE" ,"ORIGINALPRICE","FILENAME"
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

			String[] columns_hm ={"EID","SHOPID","SALESRECORDNO","CUSTOMNAME","SEX","MOBILE","CUSTOMTYPE","RECEPTIONTIME",
					"REMARK","LABELS","CREATETIME","LASTMODITIME","OPNO","BILLDATE"
			};
			DataValue[] insValue_hm = new DataValue[] 
					{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(salesRecordNo, Types.VARCHAR),
							new DataValue(customName, Types.VARCHAR),
							new DataValue(sex ,Types.VARCHAR),
							new DataValue(mobile, Types.VARCHAR),
							new DataValue(customType, Types.VARCHAR),
							new DataValue(receptionTime, Types.VARCHAR),
							new DataValue(remark , Types.VARCHAR),
							new DataValue(labels, Types.VARCHAR),
							new DataValue(createTime, Types.DATE),
							new DataValue(createTime, Types.DATE),
							new DataValue(opNo, Types.VARCHAR),
							new DataValue(billDate, Types.DATE),
					};
			
			InsBean ib_hm = new InsBean("DCP_SALESRECORD", columns_hm);
			ib_hm.addValues(insValue_hm);
			this.addProcessData(new DataProcessBean(ib_hm)); 

			this.doExecuteDataToDB();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SaleRecordCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SaleRecordCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SaleRecordCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SaleRecordCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
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
	protected TypeToken<DCP_SaleRecordCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleRecordCreateReq>(){};
	}

	@Override
	protected DCP_SaleRecordCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleRecordCreateRes();
	}
	
	
	/**
	 * 查询面销单号
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	private String queryMaxNo(DCP_SaleRecordCreateReq req) throws Exception{
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		String eId = req.geteId();
		String shopId = req.getShopId();
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		sqlbuf.append("select * from DCP_SalesRecord where EID = '"+eId+"' and shopID = '"+shopId+"' "
				+ " and SalesRecordNo like 'MXJL"+bDate+"%%' order by SalesRecordNo desc ");
		sql = sqlbuf.toString();
		
		String salesRecordNo = "MXJL"+bDate;
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		if (getQData != null && getQData.isEmpty() == false) {
			salesRecordNo = (String) getQData.get(0).get("SALESRECORDNO");
			if (salesRecordNo != null && salesRecordNo.length() > 0) {
				long i;
				salesRecordNo = salesRecordNo.substring(4, salesRecordNo.length());
				i = Long.parseLong(salesRecordNo) + 1;
				salesRecordNo = i + "";
				salesRecordNo = "MXJL" + salesRecordNo;
			} else {
				salesRecordNo = "MXJL" + bDate + "00001";
			}
			
		} else {
			salesRecordNo = "MXJL" + bDate + "00001";
		}
		
		return salesRecordNo;
	}
	
}
