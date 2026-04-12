package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECExpFormatUpdateReq;
import com.dsc.spos.json.cust.req.DCP_OrderECExpFormatUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderECExpFormatUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单导出格式查询
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECExpFormatUpdate extends SPosAdvanceService<DCP_OrderECExpFormatUpdateReq, DCP_OrderECExpFormatUpdateRes> {

	@Override
	protected void processDUID(DCP_OrderECExpFormatUpdateReq req, DCP_OrderECExpFormatUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String sql = null;
			String eId = req.geteId();
			String expOrderFormatNo = req.getExpOrderFormatNo();
			String expOrderFormatName = req.getExpOrderFormatName();
			
			String ecplatformNo = req.getEcplatformNo();
			String ecplatformName = req.getEcplatformName();
			String pickupWay = req.getPickupWay();
			String exportType = req.getExportType();
			String ftp_uid = req.getFtp_uid();
			String ftp_pwd = req.getFtp_pwd();
			String filePath = req.getFilePath();
			String memo = req.getMemo();
			String status = req.getStatus();
			
			UptBean ub1 = null;	
			ub1 = new UptBean("OC_ECEXPORDERFORMAT");
			ub1.addUpdateValue("EXPFORMATNAME", new DataValue(expOrderFormatName, Types.VARCHAR));
			ub1.addUpdateValue("ECPLATFORMNO", new DataValue(ecplatformNo, Types.VARCHAR));
			ub1.addUpdateValue("ECPLATFORMNAME", new DataValue(ecplatformName, Types.VARCHAR));
			
			ub1.addUpdateValue("PICKUPWAY", new DataValue(pickupWay, Types.VARCHAR));
			ub1.addUpdateValue("EXPORTTYPE", new DataValue(exportType, Types.VARCHAR));
			ub1.addUpdateValue("FTP_UID", new DataValue(ftp_uid,Types.VARCHAR));
			ub1.addUpdateValue("FTP_PWD", new DataValue(ftp_pwd, Types.VARCHAR));
			ub1.addUpdateValue("FILEPATH", new DataValue(filePath, Types.VARCHAR));
			ub1.addUpdateValue("MEMO", new DataValue(memo,Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
			
			ub1.addCondition("EXPFORMATNO", new DataValue(expOrderFormatNo, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
						
			DelBean db1 = new DelBean("OC_ECEXPORDERFORMAT_DETAIL");
			db1.addCondition("EXPFORMATNO", new DataValue(expOrderFormatNo, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));		
			this.doExecuteDataToDB();
			
				
			List<level1Elm> datas = req.getDatas();
			if(!datas.isEmpty()){

				for (level1Elm par : datas) {
					int insColCt = 0;  
					String[] columnsName = {
							 "EID","EXPFORMATNO","EXPFORMATNAME","ECPLATFORMNO", 
							 "ITEM","COLUMNNO","COLUMNNAME","DATAFROM",
							 "TABLENAME", "FIELDNAME","FIELDMEMO","FIXVALUE",
							 "STATUS"
					};
					
					DataValue[] columnsVal = new DataValue[columnsName.length];
					for (int i = 0; i < columnsVal.length; i++) { 
						String keyVal = null;
						switch (i) { 
						case 0:
							keyVal = eId;
							break;
						case 1:
							keyVal = expOrderFormatNo;
							break;
						case 2:
							keyVal = expOrderFormatName;
							break;
						case 3:
							keyVal = ecplatformNo;
							break;
						case 4:
							keyVal = par.getItem();
							break;
						case 5:
							keyVal = par.getExcelColumnNo();
							break;
						case 6:
							keyVal = par.getExcelColumnTitle();
							break;
						case 7:
							keyVal = par.getDataType();
							break;
						case 8:
							keyVal = par.getTableName();
							break;
						case 9:
							keyVal = par.getFieldName();
							break;
						case 10:
							keyVal = par.getFieldMemo();
							break;
						case 11:
							keyVal = par.getFixValue();
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
					InsBean ib2 = new InsBean("OC_ECEXPORDERFORMAT_DETAIL", columns2);
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
	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECExpFormatUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECExpFormatUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECExpFormatUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECExpFormatUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	    String expOrderFormatNo = req.getExpOrderFormatNo();
	    if(Check.Null(expOrderFormatNo))
    	{
	      errCt++;
	      errMsg.append("格式编号不可为空值, ");
	      isFail = true;
	    }
		
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECExpFormatUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECExpFormatUpdateReq>(){};
	}

	@Override
	protected DCP_OrderECExpFormatUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECExpFormatUpdateRes();
	}


}
