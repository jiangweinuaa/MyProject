package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECFormatCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderECFormatCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderECFormatCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单导入格式设定
 * @author yuanyy 2019-03-08
 *
 */
public class DCP_OrderECFormatCreate extends SPosAdvanceService<DCP_OrderECFormatCreateReq, DCP_OrderECFormatCreateRes> {

	@Override
	protected void processDUID(DCP_OrderECFormatCreateReq req, DCP_OrderECFormatCreateRes res) throws Exception {
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
			
			sql = this.isRepeat(orderFormatNo, eId);
			List<Map<String, Object>> ofDatas = this.doQueryData(sql, null);
			if(ofDatas.isEmpty()){
				String[] columns1 = {
						 "EID","ORDERFORMATNO","ORDERFORMATNAME","ECPLATFORMNO", "ECPLATFORMNAME",
						 "PICKUPWAY","PICKUPWAYNAME","SHOPID","WAREHOUSE","CURRENCYNO",
						 "STARTLINE","FILEFROM","FTP_UID","FTP_PWD","FILEPATH","MEMBERGET",
						 "STATUS","ECCUSTOMERNO","ISINVOICE"
				};
				DataValue[] insValue1 = null;
				
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
				
				
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(orderFormatNo, Types.VARCHAR),
						new DataValue(orderFormatName, Types.VARCHAR),
						new DataValue(ecplatformNo, Types.VARCHAR),
						new DataValue(ecplatformName, Types.VARCHAR),
						new DataValue(pickupWay, Types.VARCHAR),
						new DataValue(pickupWayName, Types.VARCHAR),
						
						new DataValue(orderShop, Types.VARCHAR), 
						new DataValue(orderWarehouse, Types.VARCHAR),
						new DataValue(currencyNo, Types.VARCHAR),
						new DataValue(startLine, Types.VARCHAR),
						new DataValue(fileFrom, Types.VARCHAR),
						new DataValue(ftp_uid, Types.VARCHAR),
						new DataValue(ftp_pwd, Types.VARCHAR),
						new DataValue(filePath, Types.VARCHAR),
						new DataValue(memberGet, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(customerNo, Types.VARCHAR),
						new DataValue(canInvoice, Types.VARCHAR)
				};
				InsBean ib1 = new InsBean("OC_ECORDERFORMAT", columns1);
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
	protected List<InsBean> prepareInsertData(DCP_OrderECFormatCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECFormatCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECFormatCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECFormatCreateReq req) throws Exception {
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
		
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECFormatCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECFormatCreateReq>(){};
	}

	@Override
	protected DCP_OrderECFormatCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECFormatCreateRes();
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
