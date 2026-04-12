package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECFormatUpdateReq;
import com.dsc.spos.json.cust.req.DCP_OrderECFormatUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderECFormatUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单导入格式修改
 * @author yuanyy 2019-03-08
 *
 */
public class DCP_OrderECFormatUpdate extends SPosAdvanceService<DCP_OrderECFormatUpdateReq, DCP_OrderECFormatUpdateRes> {

	@Override
	protected void processDUID(DCP_OrderECFormatUpdateReq req, DCP_OrderECFormatUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String sql = null;
			String eId = req.geteId();
			String orderFormatNo = req.getOrderFormatNo();
			String orderFormatName = req.getOrderFormatName();

			String ecplatformNo = req.getEcplatformNo();
			String ecplatformName = req.getEcplatformName();
			String pickupWay = req.getPickupWay();
			String pickupWayName = req.getPickupWayName();
			String startLine = req.getStartLine();
			String fileFrom = req.getFileFrom();
			String ftp_uid = req.getFtp_uid();
			String ftp_pwd = req.getFtp_pwd();
			String filePath = req.getFilePath();
			String memberGet = req.getMemberGet();
			String orderShop = req.getOrderShop();
			String orderWarehouse = req.getOrderWarehouse();
			String currencyNo = req.getCurrencyNo();
			String status = req.getStatus();
			String customerNo = req.getCustomerNO();
			String canInvoice = req.getCanInvoice();
			
			UptBean ub1 = null;	
			ub1 = new UptBean("OC_ECORDERFORMAT");
			ub1.addUpdateValue("ORDERFORMATNAME", new DataValue(orderFormatName, Types.VARCHAR));
			
			ub1.addUpdateValue("ECPLATFORMNO", new DataValue(ecplatformNo, Types.VARCHAR));
			ub1.addUpdateValue("ECPLATFORMNAME", new DataValue(ecplatformName, Types.VARCHAR));
			
			ub1.addUpdateValue("PICKUPWAY", new DataValue(pickupWay, Types.VARCHAR));
			ub1.addUpdateValue("PICKUPWAYNAME", new DataValue(pickupWayName,Types.VARCHAR));
			ub1.addUpdateValue("STARTLINE", new DataValue(startLine,Types.VARCHAR));
			ub1.addUpdateValue("FILEFROM", new DataValue(fileFrom,Types.VARCHAR));
			ub1.addUpdateValue("FTP_UID", new DataValue(ftp_uid,Types.VARCHAR));
			ub1.addUpdateValue("FTP_PWD", new DataValue(ftp_pwd, Types.VARCHAR));
			
			ub1.addUpdateValue("FILEPATH", new DataValue(filePath, Types.VARCHAR));
			ub1.addUpdateValue("MEMBERGET", new DataValue(memberGet,Types.VARCHAR));
			ub1.addUpdateValue("SHOPID", new DataValue(orderShop,Types.VARCHAR));
			ub1.addUpdateValue("WAREHOUSE", new DataValue(orderWarehouse,Types.VARCHAR));
			ub1.addUpdateValue("CURRENCYNO", new DataValue(currencyNo,Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
			ub1.addUpdateValue("ECCUSTOMERNO", new DataValue(customerNo,Types.VARCHAR));
			ub1.addUpdateValue("ISINVOICE", new DataValue(canInvoice,Types.VARCHAR));
			
			ub1.addCondition("ORDERFORMATNO", new DataValue(orderFormatNo, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
						
			DelBean db1 = new DelBean("OC_ECORDERFORMAT_DETAIL");
			db1.addCondition("ORDERFORMATNO", new DataValue(orderFormatNo, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));		
			this.doExecuteDataToDB();
				
			List<level1Elm> datas = req.getDatas();
			if(!datas.isEmpty()){

				for (level1Elm par : datas) {
					int insColCt = 0;  
					String[] columnsName = {
							 "EID","ORDERFORMATNO","ORDERFORMATNAME","ECPLATFORMNO", 
							 "PICKUPWAY","ITEM","TABLENAME","FIELDNAME","FIELDMEMO","FROMTYPE",
							 "FROMVALUE","SPLITCOLUMN","STATUS"
					};

					
					DataValue[] columnsVal = new DataValue[columnsName.length];
					for (int i = 0; i < columnsVal.length; i++) { 
						String keyVal = null;
						switch (i) { 
						case 0:
							keyVal = eId;
							break;
						case 1:
							keyVal = orderFormatNo;
							break;
						case 2:
							keyVal = orderFormatName;
							break;
						case 3:
							keyVal = ecplatformNo;
							break;
						case 4:
							keyVal = pickupWay;
							break;
						case 5:
							keyVal = par.getItem();
							break;
						case 6:
							keyVal = par.getTableName();
							break;
						case 7:
							keyVal = par.getFieldName();
							break;
						case 8:
							keyVal = par.getFieldMemo();
							break;
						case 9:
							keyVal = par.getFromType();
							break;
						case 10:
							keyVal = par.getFromValue();
							break;
						case 11:
							keyVal = par.getSplitColumn();
							break;
						case 12:
							keyVal = par.getStatus();
							break;
						default:
							break;
						}
						if (keyVal != null) 
			          	{
			        	  	insColCt++;
			        	  	columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
			          	} 
					  	else 
					  	{
						  	columnsVal[i] = null;
					  	}
						
					}
					String[] columns2  = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt = 0;

					for (int i=0;i<columnsVal.length;i++){
						if (columnsVal[i] != null){
							columns2[insColCt] = columnsName[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue2.length) 
								break;
						}
					}
					InsBean ib2 = new InsBean("OC_ECORDERFORMAT_DETAIL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
					
				}
			}
			
			this.doExecuteDataToDB();
			if (res.isSuccess()) 
			{
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");						
			} 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECFormatUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECFormatUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECFormatUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECFormatUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	    String orderFormatNo = req.getOrderFormatNo();
	    if(Check.Null(orderFormatNo))
    	{
	      errCt++;
	      errMsg.append("格式编号不可为空值, ");
	      isFail = true;
	    }
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECFormatUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECFormatUpdateReq>(){};
	}

	@Override
	protected DCP_OrderECFormatUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECFormatUpdateRes();
	}
	
	/**
	 * 验证当前单号是否已经存在    ？（如果当前单据自动 生成的单号，已有正在保存的单据生成。。建议创建最大单据号表，每次打开单据时，查询该表最大单据号）
	 * @param orderFormatNo
	 * @param eId
	 * @return
	 */
	private String isRepeat( String orderFormatNo , String eId ){
		String sql = null;
		sql = "select orderFormatNo from OC_ECORDERFORMAT "
			+ " where  orderFormatNo = '"+orderFormatNo +"' "
			+ " and EID = '"+eId+"'";
		return sql;
	}
	
	
	
}	
