package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECExpFormatCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderECExpFormatCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderECExpFormatCreateRes;
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
public class DCP_OrderECExpFormatCreate extends SPosAdvanceService<DCP_OrderECExpFormatCreateReq, DCP_OrderECExpFormatCreateRes> {

	@Override
	protected void processDUID(DCP_OrderECExpFormatCreateReq req, DCP_OrderECExpFormatCreateRes res) throws Exception {
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
			
			sql = this.isRepeat(expOrderFormatNo, eId);
			List<Map<String, Object>> ofDatas = this.doQueryData(sql, null);
			if(ofDatas.isEmpty()){
				String[] columns1 = {
						 "EID","EXPFORMATNO","EXPFORMATNAME","ECPLATFORMNO", 
						 "ECPLATFORMNAME","PICKUPWAY","EXPORTTYPE","FTP_UID",
						 "FTP_PWD","FILEPATH","MEMO","STATUS"
				};
				DataValue[] insValue1 = null;
				
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
				
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(expOrderFormatNo, Types.VARCHAR),
						new DataValue(expOrderFormatName, Types.VARCHAR),
						new DataValue(ecplatformNo, Types.VARCHAR),
						new DataValue(ecplatformName, Types.VARCHAR),
						new DataValue(pickupWay, Types.VARCHAR),
						
						new DataValue(exportType, Types.VARCHAR), 
						new DataValue(ftp_uid, Types.VARCHAR),
						new DataValue(ftp_pwd, Types.VARCHAR),
						new DataValue(filePath, Types.VARCHAR),
						new DataValue(memo, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR)
				};
				InsBean ib1 = new InsBean("OC_ECEXPORDERFORMAT", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
				
				this.doExecuteDataToDB();
				if (res.isSuccess()) 
				{
					res.setServiceStatus("000");
					res.setServiceDescription("服务执行成功");						
				}
				
			}
			
			
		} catch (Exception e) {
	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECExpFormatCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECExpFormatCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECExpFormatCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECExpFormatCreateReq req) throws Exception {
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
	protected TypeToken<DCP_OrderECExpFormatCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECExpFormatCreateReq>(){};
	}

	@Override
	protected DCP_OrderECExpFormatCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECExpFormatCreateRes();
	}
	
	private String isRepeat( String orderFormatNo , String eId ){
		String sql = null;
		sql = "select EXPFORMATNO from OC_ECEXPORDERFORMAT "
			+ " where  EXPFORMATNO = '"+orderFormatNo +"' "
			+ " and EID = '"+eId+"'";
		return sql;
	}


}
