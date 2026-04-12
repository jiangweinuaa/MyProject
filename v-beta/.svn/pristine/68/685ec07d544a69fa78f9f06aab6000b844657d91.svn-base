package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MainErrorInfoDeleteReq;
import com.dsc.spos.json.cust.res.DCP_MainErrorInfoDeleteRes;
import com.dsc.spos.scheduler.job.InsertWSLOG;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_MainErrorInfoDelete extends SPosAdvanceService<DCP_MainErrorInfoDeleteReq,DCP_MainErrorInfoDeleteRes>{

	@Override
	protected void processDUID(DCP_MainErrorInfoDeleteReq req, DCP_MainErrorInfoDeleteRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String jobName = req.getRequest().getJobName();
		String dataType = req.getRequest().getDataType();
		String oShopId = req.getRequest().getoShopId();
		if (Check.Null(oShopId))
			oShopId = " ";
		String docNO = req.getRequest().getDocNo();
		String docType=req.getRequest().getDocType();   ///删除类型	0.云中台-->ERP  1.ERP-->云中台
		String serviceName = req.getRequest().getServiceName(); //服务名称	

		try
		{
			if (Check.Null(docType)||docType.equals("0"))
			{
				wsDelete(eId,oShopId,docNO,jobName,dataType); //删除类型	0.云中台-->ERP
			}
			else
			{
				serviceDelete(eId,oShopId,docNO,serviceName); //删除类型  1.ERP-->云中台
			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MainErrorInfoDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MainErrorInfoDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MainErrorInfoDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MainErrorInfoDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String dataType = req.getRequest().getDataType();
		String oShopId = req.getRequest().getoShopId();
		String docNO = req.getRequest().getDocNo();

		if (Check.Null(dataType)) {
			errMsg.append("数据分类不可为空值, ");
			isFail = true;
		}
		if (Check.Null(oShopId)) {
			errMsg.append("门店编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(docNO)) {
			errMsg.append("单据编号不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_MainErrorInfoDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return  new TypeToken<DCP_MainErrorInfoDeleteReq>(){};
	}

	@Override
	protected DCP_MainErrorInfoDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MainErrorInfoDeleteRes();
	}

	private void wsDelete(String eId,String oShopId,String docNO,String jobName,String dataType) throws Exception
	{
		String sql="";
		String tableName="";
		String docName="";
		if (Check.Null(jobName))
		{
			sql =" select JOB_NAME from job_quartz where job_discretion='"+dataType+"' " ;
			List<Map<String, Object>> getQData_JOB_NAME = this.doQueryData(sql, null);
			if (getQData_JOB_NAME !=null && getQData_JOB_NAME.isEmpty()==false)
			{
				jobName=getQData_JOB_NAME.get(0).get("JOB_NAME").toString();
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "在job_quartz表中未查询到"+dataType+"对应的JOB_NAME");
			}
		}

		switch(jobName){
			case "RequisitionCreate" :
				//serviceName="requisition.create";
				tableName = "DCP_PORDER" ;
				docName="PORDERNO";
				break;
			case "DifferenceCreate" :
				//serviceName="difference.create";
				tableName = "DCP_DIFFERENCE";
				docName="DIFFERENCENO";
				break;
			case "ReturnCreate" :
				//serviceName="return.create";
				tableName = "DCP_STOCKOUT";
				docName="STOCKOUTNO";
				break;
			case "TransferCreate" :
				//serviceName="transfer.create";
				tableName = "DCP_STOCKOUT";
				docName="STOCKOUTNO";
				break;
			case "TransferUpdate" :
				//serviceName="transfer.update";
				tableName = "DCP_STOCKIN";
				docName="STOCKINNO";
				break;
			case "IntegrateCountingCreate" :
				//serviceName="integrate.counting.create";
				tableName = "DCP_STOCKTAKE";
				docName="STOCKTAKENO";
				break;
			case "CompletionProcess" :
				//serviceName="completion.process";
				tableName = "DCP_PSTOCKIN";
				docName="PSTOCKINNO";
				break;
			case "DisassemblyProcess" :
				//serviceName="disassembly.process";
				tableName = "DCP_PSTOCKIN";
				docName="PSTOCKINNO";
				break;
			case "InventoryAdjustCreateLoss" :
				//serviceName="inventory.adjust.create";
				tableName = "DCP_LSTOCKOUT";
				docName="LSTOCKOUTNO";
				break;
			case "InventoryAdjustCreateOtherIn" :
				//serviceName="inventory.adjust.create";
				tableName = "DCP_STOCKIN";
				docName="STOCKINNO";
				break;
			case "InventoryAdjustCreateOtherOut" :
				//serviceName="inventory.adjust.create";
				tableName = "DCP_STOCKOUT";
				docName="STOCKOUTNO";
				break;
			case "ReceiptUpdate" :
				//serviceName="receipt.update";
				tableName = "DCP_SSTOCKIN";
				docName="SSTOCKINNO";
				break;
			case "PurchaseReturnCreate" :
				//serviceName="purchase.return.create";
				tableName = "DCP_SSTOCKOUT";
				docName="SSTOCKOUTNO";
				break;
			case "FeeCreate" :
				//serviceName="fee.create";
				tableName = "DCP_BFEE";
				docName="BFEENO";
				break;
			case "ReceiptEcsflg" :
				//serviceName="receipt.ecsflg";
				tableName = "DCP_RECEIVING";
				docName="RECEIVINGNO";
				break;
			case "HolidayShoporderCreate_V3" :
				//serviceName="holidayorder.create";
				tableName = "DCP_ORDER";
				docName="ORDERNO";
				break;
			case "OrderPayRefundCreate_V3" :
				tableName = "DCP_ORDER_PAY";
				docName="BILLNO";
				break;
		}

		if (tableName.equals("DCP_ORDER"))
		{
			sql = "select bdate from DCP_ORDER "
					+ " where EID='"+eId+"' and SHOP='"+oShopId+"' and orderno='"+docNO+"' ";
		}
		else if(tableName.equals("DCP_ORDER_PAY"))
		{
			sql = "select BILLDATE as bdate from DCP_ORDER_PAY "
					+ " where EID='"+eId+"' and SHOPID='"+oShopId+"' and (BILLNO='"+docNO+"' or SOURCEBILLNO='"+docNO+"') ";
		}
		else
		{
			sql = "select bdate from "+tableName+" "
					+ " where EID='"+eId+"' and SHOPID='"+oShopId+"' and "+docName+"='"+docNO+"' ";
		}

		List<Map<String, Object>> getQData_date = this.doQueryData(sql, null);

		if (getQData_date!=null && getQData_date.isEmpty()==false)
		{
			String bdate = getQData_date.get(0).getOrDefault("BDATE", "").toString();

			Calendar cal = Calendar.getInstance();//获得当前时间
			cal.add(Calendar.DAY_OF_MONTH, -3);   //只能变更三天前的单据状态 
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String sDate = df.format(cal.getTime());

			if (PosPub.compare_date(sDate, bdate) >0)
			{
				UptBean ub =  new UptBean(tableName);
				//add Value
				ub.addUpdateValue("PROCESS_STATUS", new DataValue("E", Types.VARCHAR));
				//condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));

				if (tableName.equals("DCP_ORDER"))
				{
					ub.addCondition("SHOP", new DataValue(oShopId, Types.VARCHAR));
				}
				else{
					ub.addCondition("SHOPID", new DataValue(oShopId, Types.VARCHAR));
				}

				ub.addCondition(docName, new DataValue(docNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub));



				//删除WS日志
				InsertWSLOG.delete_WSLOG(eId, oShopId,"1",docNO);
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "只能终止三天前的单据，此单据日期为："+bdate);
			}

		}
		else
		{
			//删除单据不存在的WS日志
			InsertWSLOG.delete_WSLOG(eId, oShopId,"1",docNO);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单号："+docNO+" 不存在，请重新确认");
		}

	}


	private void serviceDelete(String eId,String oShopId,String docNO,String serviceName ) throws Exception
	{
		if (Check.Null(eId)) eId=" ";
		if (Check.Null(oShopId)) oShopId=" ";
		if (Check.Null(docNO)) docNO=" ";
		if (Check.Null(serviceName)) serviceName=" ";

		//删除异常的WS日志
		InsertWSLOG.delete_WSLOG(eId, oShopId,"2",docNO);
		InsertWSLOG.delete_WSLOG(" ", oShopId,"2",docNO);

		if (oShopId.equals(" ") && docNO.equals(" ") && serviceName.equals(" ") )  //删除JSON解析失败的资料，无门店，无服务名
		{
			InsertWSLOG.delete_WSLOG(" ", oShopId,"2",docNO);
		}

	}



}
