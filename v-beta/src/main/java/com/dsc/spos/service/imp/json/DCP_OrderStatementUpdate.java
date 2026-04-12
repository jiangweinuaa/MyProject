package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderStatementUpdateReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatementUpdateReq.Data;
import com.dsc.spos.json.cust.res.DCP_OrderStatementUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 服务OrderStatementUpdate ,对账更新
 * @author 08546
 */
public class DCP_OrderStatementUpdate extends SPosAdvanceService<DCP_OrderStatementUpdateReq,DCP_OrderStatementUpdateRes> 
{

	@Override
	protected void processDUID(DCP_OrderStatementUpdateReq req, DCP_OrderStatementUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		Logger logger = LogManager.getLogger(DCP_OrderStatementUpdate.class.getName());

		try{
			List<Data> datas=req.getRequest().getDatas();
			List<String> orderNoList=new ArrayList<String>();
			for(Data data:datas){
				Object diversityReason=data.getDiversityReason();
				if(diversityReason==null||diversityReason.toString().trim().isEmpty()){
					orderNoList.add(data.getOrderNo());
				}else{
					Date date = new Date();
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					String nowTime=sdf1.format(date);
					String modifiedBy=req.getOpNO();

					UptBean ub1 = null;	
					ub1 = new UptBean("OC_STATEMENT");
					//差异原因		DIVERSITYREASON
					ub1.addUpdateValue("DIVERSITYREASON", new DataValue(data.getDiversityReason(), Types.VARCHAR));
					//资料修改者		DATAMODIFIEDBY
					ub1.addUpdateValue("THIRDACCOUNTDATE", new DataValue(modifiedBy, Types.VARCHAR));
					//最近修改日		LASTMODIFIEDDATE
					ub1.addUpdateValue("LASTMODIFIEDDATE", new DataValue(nowTime, Types.VARCHAR));


					//条件部分
					ub1.addCondition("EID", new DataValue(data.geteId(), Types.VARCHAR));
					ub1.addCondition("SHOPID", new DataValue(data.getShopId(), Types.VARCHAR));
					ub1.addCondition("THIRDSHOP", new DataValue(data.getThirdShop(), Types.VARCHAR));
					ub1.addCondition("ORDERNO", new DataValue(data.getOrderNo(), Types.VARCHAR));
					ub1.addCondition("ORDERTYPE", new DataValue(data.getOrderType(), Types.VARCHAR));
					ub1.addCondition("THIRDTYPE", new DataValue(data.getThirdType(), Types.VARCHAR));
					ub1.addCondition("ACCOUNTSTATUS", new DataValue("1", Types.VARCHAR));


					//列表SQL  需要执行的sql列表
					List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
					lstData.add(new DataProcessBean(ub1));
					Boolean isSuccess=this.dao.useTransactionProcessData(lstData);
				}

				//				String des="";
				//				if(isSuccess){
				//					
				//				}else{
				//					des="单据:"+data.getOrderNo()+"保存失败";
				//				}
			}

			String description="";
			if(orderNoList!=null&&orderNoList.size()>0){
				description="单据:"+getStringList2Str(orderNoList)+"原因未填写";
			}else{
				description="保存成功";
			}
			
			this.doExecuteDataToDB();	
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription(description);
		}catch(Exception e){
			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"对账更新执行失败",e);
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败：" + e.getMessage());
		}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderStatementUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderStatementUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderStatementUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderStatementUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<Data> datas=req.getRequest().getDatas();
		if(datas==null){
			isFail=true;
			errMsg.append("datas不可为空值, ");
		}else{
			//校验data部分
			for(Data data:datas){
				String orderNo=data.getOrderNo();
				if(orderNo==null||orderNo.trim().isEmpty()){
					errMsg.append("datas中orderNo不可为空值, ");
				}
			}
		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderStatementUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderStatementUpdateReq>(){};
	}

	@Override
	protected DCP_OrderStatementUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderStatementUpdateRes();
	}

	public static String getStringList2Str(List<String> strList) throws Exception{
		StringBuffer idsStr = new StringBuffer("");
		for (int i = 0; i < strList.size(); i++) {
			if (i > 0) {
				idsStr.append(",");
			}
			String str=strList.get(i);
			idsStr.append(str);
		}
		return idsStr.toString();
	}


}
