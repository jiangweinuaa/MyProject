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
import com.dsc.spos.json.cust.req.DCP_UndoCreateReq;
import com.dsc.spos.json.cust.req.DCP_UndoCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_UndoCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：UndoCreateDCP
 * 服务说明：门店单据撤销
 * @author jinzma 
 * @since  2019-06-11
 */

public class DCP_UndoCreate extends SPosAdvanceService<DCP_UndoCreateReq,DCP_UndoCreateRes > {

	@Override
	protected void processDUID(DCP_UndoCreateReq req, DCP_UndoCreateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String docType = req.getDocType();   // 0.配送收货 1.采购收货
		List<level1Elm> datas = req.getDatas();
		StringBuilder errorMessage = new StringBuilder();
		boolean isSuccess = false;
		try
		{
			switch (docType) {
			case "0":
				isSuccess=stockinUndo(eId,docType,datas,errorMessage);
				break;
			case "1":
				isSuccess=sstockinUndo(eId,docType,datas,errorMessage);
				break;
			}

			///处理ERP调用中台接口保存WS  BY JZMA 20200221
			String shopId="";
			String loadDocNO="";
			if (datas!=null && datas.isEmpty()==false)
			{
				shopId=datas.get(0).getShopId();
				loadDocNO=datas.get(0).getLoadDocNO();
			}	
			
			if (!isSuccess)
			{
				res.setDoc_no(loadDocNO);
				res.setOrg_no(shopId);
				res.setSuccess(false);
				res.setServiceStatus("000");
				res.setServiceDescription("撤销失败: "+ errorMessage.toString());			
			}
			else
			{
				this.doExecuteDataToDB();
				res.setDoc_no(loadDocNO);
				res.setOrg_no(shopId);
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}

		}
		catch (Exception e)
		{
			res.setSuccess(false);
			res.setServiceStatus("000");
			res.setServiceDescription(e.getMessage());			
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_UndoCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_UndoCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_UndoCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_UndoCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String docType = req.getDocType();
		if (Check.Null(docType)) {
			errMsg.append("单据类型不可为空值, ");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		List<level1Elm> datas = req.getDatas();
		for (level1Elm par:datas )
		{
			String shopId = par.getShopId();
			String loadDocNO=par.getLoadDocNO();

			if (Check.Null(shopId)) {
				errMsg.append("门店编号不可为空值, ");
				isFail = true;
			}
			if (Check.Null(loadDocNO)) {
				errMsg.append("来源单号不可为空值, ");
				isFail = true;
			}

			if (isFail) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;

	}

	@Override
	protected TypeToken<DCP_UndoCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_UndoCreateReq>(){};
	}

	@Override
	protected DCP_UndoCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_UndoCreateRes();
	}

	private boolean stockinUndo(String eId,String docType,List<level1Elm> datas,StringBuilder errorMessage) throws Exception{
		boolean isSuccess=true;
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		String sDate = sdf.format(cal.getTime());
		sdf = new SimpleDateFormat("HHmmss");
		String sTime = sdf.format(cal.getTime());	

		try
		{
			for (level1Elm par:datas )
			{
				String shopId=par.getShopId();
				String loadDocNO=par.getLoadDocNO();
				String sql=" select a.receivingno,a.status,a.load_docno,a.load_receiptno,b.ofno  from DCP_receiving a  "
						+ " left join DCP_stockin b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.receivingno=b.ofno "
						+ " where a.status<>'8' and a.doc_type='0' and a.EID='"+eId+"' and a.SHOPID='"+shopId+"'  "
						+ " and (a.load_docno='"+loadDocNO+"' or a.LOAD_RECEIPTNO='"+loadDocNO+"')  ";

				List<Map<String, Object>> getQData = this.doQueryData(sql, null);
				if (getQData != null && getQData.isEmpty() == false)
				{					
					String status = getQData.get(0).get("STATUS").toString();
					String ofno = getQData.get(0).get("OFNO").toString();
					if (status.equals("6") && Check.Null(ofno))
					{					
						String receivingNO = getQData.get(0).get("RECEIVINGNO").toString();
						//收货通知单状态改成 8.作废
						UptBean ub = new UptBean("DCP_RECEIVING");
						ub.addUpdateValue("STATUS", new DataValue("8", Types.VARCHAR));
						ub.addUpdateValue("MODIFY_DATE", new DataValue(sDate, Types.VARCHAR));
						ub.addUpdateValue("MODIFY_TIME", new DataValue(sTime, Types.VARCHAR));
						ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
						ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

						
						//更新条件
						ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
						ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
						ub.addCondition("RECEIVINGNO", new DataValue(receivingNO, Types.VARCHAR));
						ub.addCondition("DOC_TYPE", new DataValue("0", Types.VARCHAR));
						ub.addCondition("STATUS", new DataValue("6", Types.VARCHAR));
						this.addProcessData(new DataProcessBean(ub));
						
						//删除 dcp_platform_billcheck
						DelBean db = new DelBean("DCP_PLATFORM_BILLCHECK");
						db.addCondition("EID",  new DataValue(eId, Types.VARCHAR));
						db.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR) );
						db.addCondition("BILLTYPE",  new DataValue("receivingCreate", Types.VARCHAR));
						db.addCondition("BILLNO", new DataValue(loadDocNO , Types.VARCHAR));      
						this.addProcessData(new DataProcessBean(db));
						
					}
					else
					{
						isSuccess=false;						
						errorMessage =errorMessage.append("门店："+shopId +" ERP来源单号："+loadDocNO + " 已产生配送收货单，无法删除！"  );
						return isSuccess;		
					}
				}
			}		
		}
		catch (Exception e)
		{
			isSuccess=false;
			errorMessage =errorMessage.append(e.getMessage());
			return isSuccess;
		}
		return isSuccess;
	}

	private boolean sstockinUndo(String eId,String docType,List<level1Elm> datas,StringBuilder errorMessage) throws Exception{
		boolean isSuccess=true;
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		String sDate = sdf.format(cal.getTime());
		sdf = new SimpleDateFormat("HHmmss");
		String sTime = sdf.format(cal.getTime());	
		try
		{
			for (level1Elm par:datas )
			{
				String shopId=par.getShopId();
				String loadDocNO=par.getLoadDocNO();
				String sql=" select a.receivingno,a.status,a.load_docno,a.load_receiptno,b.ofno  from DCP_receiving a  "
						+ " left join DCP_sstockin b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.receivingno=b.ofno "
						+ " where a.status<>'8' and a.doc_type='2' and a.EID='"+eId+"' and a.SHOPID='"+shopId+"'  "
						+ " and (a.load_docno='"+loadDocNO+"' or a.LOAD_RECEIPTNO='"+loadDocNO+"')  ";

				List<Map<String, Object>> getQData = this.doQueryData(sql, null);
				if (getQData != null && getQData.isEmpty() == false)
				{					
					String status = getQData.get(0).get("STATUS").toString();
					String ofno = getQData.get(0).get("OFNO").toString();
					if (status.equals("6") && Check.Null(ofno))
					{					
						String receivingNO = getQData.get(0).get("RECEIVINGNO").toString();
						//收货通知单状态改成 8.作废
						UptBean ub = new UptBean("DCP_RECEIVING");
						ub.addUpdateValue("STATUS", new DataValue("8", Types.VARCHAR));
						ub.addUpdateValue("MODIFY_DATE", new DataValue(sDate, Types.VARCHAR));
						ub.addUpdateValue("MODIFY_TIME", new DataValue(sTime, Types.VARCHAR));
						ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
						ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

						//更新条件
						ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
						ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
						ub.addCondition("RECEIVINGNO", new DataValue(receivingNO, Types.VARCHAR));
						ub.addCondition("DOC_TYPE", new DataValue("2", Types.VARCHAR));
						ub.addCondition("STATUS", new DataValue("6", Types.VARCHAR));
						this.addProcessData(new DataProcessBean(ub));
						
						//删除 dcp_platform_billcheck
						DelBean db = new DelBean("DCP_PLATFORM_BILLCHECK");
						db.addCondition("EID",  new DataValue(eId, Types.VARCHAR));
						db.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR) );
						db.addCondition("BILLTYPE",  new DataValue("receivingCreate", Types.VARCHAR));
						db.addCondition("BILLNO", new DataValue(loadDocNO , Types.VARCHAR));      
						this.addProcessData(new DataProcessBean(db));
						
					}
					else
					{
						isSuccess=false;						
						errorMessage =errorMessage.append("门店："+shopId +" ERP来源单号："+loadDocNO + " 已产生采购入库单，无法删除！"  );
						return isSuccess;		
					}
				}
			}		
		}
		catch (Exception e)
		{
			isSuccess=false;
			errorMessage =errorMessage.append(e.getMessage());
			return isSuccess;
		}
		return isSuccess;

	}

}
