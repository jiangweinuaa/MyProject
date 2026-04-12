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
import com.dsc.spos.json.cust.req.DCP_SeriesUpdateReq;
import com.dsc.spos.json.cust.req.DCP_SeriesUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SeriesUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SeriesUpdate extends SPosAdvanceService<DCP_SeriesUpdateReq, DCP_SeriesUpdateRes> {

	@Override
	protected void processDUID(DCP_SeriesUpdateReq req, DCP_SeriesUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String seriesNO = req.getRequest().getSeriesNo();
			String seriesName = req.getRequest().getSeriesName();
			String eId = req.geteId();
			String status = req.getRequest().getStatus();
			String lastmoditime = null;//req.getRequest().getLastmoditime();
			if(lastmoditime==null||lastmoditime.isEmpty())
			{
				lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			
			DelBean db2 = new DelBean("DCP_SERIES_LANG");
			db2.addCondition("SERIESNO", new DataValue(seriesNO, Types.VARCHAR));					
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			List<level1Elm> datas = req.getRequest().getSeriesName_lang();
			
			if(datas != null && datas.size() > 0 ){
				for (level1Elm par : datas) {
					
				 	int insColCt = 0;  
				  	String[] columnsName = {"SERIESNO", "LANG_TYPE", "SERIES_NAME", "EID","LASTMODITIME"};
				  	//获取				  	
				  	String lSeriesName = par.getName();
				  	String langType = par.getLangType();
				  				  					  								  					  			  	
				  	DataValue[] insValueDetail = new DataValue[] 
								{
									new DataValue(seriesNO, Types.VARCHAR),
									new DataValue(langType, Types.VARCHAR),	
									new DataValue(lSeriesName, Types.VARCHAR),																		
									new DataValue(eId, Types.VARCHAR),
									new DataValue(lastmoditime, Types.DATE)
								};
				  	
					//添加原因码多语言信息
					InsBean ib2 = new InsBean("DCP_SERIES_LANG", columnsName);
					ib2.addValues(insValueDetail);
					this.addProcessData(new DataProcessBean(ib2));
				  	
				}
			}
			
			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_SERIES");
			//add Value
			
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			//condition
			ub1.addCondition("SERIESNO", new DataValue(seriesNO, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常:"+e.getMessage());	
			this.pData.clear();
			
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SeriesUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SeriesUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SeriesUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SeriesUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空 ");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
		String seriesNO = req.getRequest().getSeriesNo();
		List<level1Elm> datas = req.getRequest().getSeriesName_lang();
		
		if(datas==null)
		{
			errMsg.append("多语言资料不能为空");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
	    if(Check.Null(seriesNO)){
		   	errMsg.append("系列编码不能为空值 ");
		   	isFail = true;
		}
		for(level1Elm par : datas){
		      
			if (Check.Null(par.getLangType())) 
		  	{
		        errMsg.append("多语言类型不可为空值, ");
		        isFail = true;
		  	}
		}
		
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_SeriesUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SeriesUpdateReq>(){};
	}

	@Override
	protected DCP_SeriesUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SeriesUpdateRes();
	}
	
}
