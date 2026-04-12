package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PlanDetailUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PlanDetailUpdateRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

/**
 * 生产计划预估更新
 * @author yuanyy 2019-10-28
 *
 */
public class DCP_PlanDetailUpdate extends SPosAdvanceService<DCP_PlanDetailUpdateReq, DCP_PlanDetailUpdateRes> {

	@Override
	protected void processDUID(DCP_PlanDetailUpdateReq req, DCP_PlanDetailUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			
			UptBean ub1 = new UptBean("DCP_PLAN_DETAIL");		
			
			ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
			ub1.addCondition("SHOPID",new DataValue(req.getShopId(), Types.VARCHAR));
			ub1.addCondition("PLANNO",new DataValue(req.getPlanNo(), Types.VARCHAR));
			ub1.addCondition("FTYPE",new DataValue(req.getfType(), Types.VARCHAR));
			ub1.addCondition("FNO",new DataValue(req.getfNo(), Types.VARCHAR));
			
//			ub1.addUpdateValue("AVGAMT",new DataValue(req.getAvgAmt(), Types.VARCHAR));
			ub1.addUpdateValue("PREDICTAMT",new DataValue(req.getPredictAmt(), Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(ub1));		
			
			// 需要重新计算班次 / 批次预估数量
			// 根据比例计算更新即可，不用重新再查询数量后再计算      平均金额/修改后的金额  == 平均数量 /修改后的数量 
			BigDecimal predictAmt = new BigDecimal(req.getPredictAmt());
			BigDecimal avgAmt = new BigDecimal(req.getAvgAmt());
			
			String predictAmtOldStr = req.getPredictAmt(); // 默认给预估额
			if(req.getPredictAmtOld() != null && req.getPredictAmtOld().length() > 0){
				predictAmtOldStr = req.getPredictAmtOld();
			}
			
			BigDecimal predictAmtOld = new BigDecimal(predictAmtOldStr); 
			
			BigDecimal ratio = new BigDecimal("0");
			if(predictAmt.compareTo(new BigDecimal("0")) == 0 || predictAmtOld.compareTo(new BigDecimal("0")) == 0){
				ratio = new BigDecimal("0");
			}
			
			else{
				ratio = predictAmt.divide(predictAmtOld,2, BigDecimal.ROUND_HALF_UP);
			}
			
			ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
			
			String execsql1 = " update DCP_PLAN_MATERIAL set predictQty = ceil( predictQty * "+ratio+" ), "
							+ " actQty =  ceil( predictQty *"+ratio + " ), "
							+ " distriAmt = ceil( distriAmt * "+ratio +" ) , "
							+ " totAmt = ceil(  totAmt * "+ratio + " ) "
							+ " where EID = '"+req.geteId()+"' and SHOPID = '"+req.getShopId()+"' "
							+ " and planNo = '"+req.getPlanNo()+"' and ftype = '"+req.getfType()+"' "
							+ " and fNo = '"+req.getfNo()+"' ";
			ExecBean exc1 = new ExecBean(execsql1);
			DPB.add(new DataProcessBean(exc1));
			
			this.doExecuteDataToDB(DPB);
					
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PlanDetailUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PlanDetailUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PlanDetailUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PlanDetailUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;
	    
	    if (Check.Null(req.getBeginTime())) {
	    	isFail = true;
	    	errCt++;
	    	errMsg.append("开始时间不可为空值, ");
	    }
	    if (Check.Null(req.getEndTime())) {
	    	isFail = true;
	    	errCt++;
	    	errMsg.append("结束时间不可为空值, ");
	    }
	    
	    if (Check.Null(req.getfNo())) {
	    	isFail = true;
	    	errCt++;
	    	errMsg.append("班次/批次编码不可为空值, ");
	    }
	    
	    if (Check.Null(req.getPlanNo())) {
	    	isFail = true;
	    	errCt++;
	    	errMsg.append("单据编号不可为空值, ");
	    }
	    
	    if (Check.Null(req.getbDate())) {
	    	isFail = true;
	    	errCt++;
	    	errMsg.append("单据日期不可为空值, ");
	    }
	    
	    if (Check.Null(req.getAvgAmt())) {
	    	isFail = true;
	    	errCt++;
	    	errMsg.append("参考营业额不可为空值, ");
	    }
	    
	    if (Check.Null(req.getPredictAmt())) {
	    	isFail = true;
	    	errCt++;
	    	errMsg.append("预估营业额不可为空值, ");
	    }
	    
	    if (isFail){
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		    
	    return isFail;
	}

	@Override
	protected TypeToken<DCP_PlanDetailUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanDetailUpdateReq>(){};
	}

	@Override
	protected DCP_PlanDetailUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanDetailUpdateRes();
	}

	
	protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
		if (pData == null || pData.size() == 0) {
			return;
		}
		StaticInfo.dao.useTransactionProcessData(pData);
		
	}
	
	
}
