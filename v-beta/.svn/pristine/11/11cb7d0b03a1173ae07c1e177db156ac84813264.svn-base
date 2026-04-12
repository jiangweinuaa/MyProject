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
import com.dsc.spos.json.cust.req.DCP_SeriesCreateReq;
import com.dsc.spos.json.cust.req.DCP_SeriesCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SeriesCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SeriesCreate extends SPosAdvanceService<DCP_SeriesCreateReq, DCP_SeriesCreateRes> {

	@Override
	protected void processDUID(DCP_SeriesCreateReq req, DCP_SeriesCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try {
			String seriesNO = req.getRequest().getSeriesNo();
			String seriesName = req.getRequest().getSeriesName();
			String eId = req.geteId();
			String status = req.getRequest().getStatus();
			List<level1Elm> datas = req.getRequest().getSeriesName_lang();
			String lastmoditime = null;//req.getRequest().getLastmoditime();
			if(lastmoditime==null||lastmoditime.isEmpty())
			{
				lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			sql = this.isRepeat(seriesNO, eId);
			List<Map<String, Object>> seriesDatas = this.doQueryData(sql, null);
			if(seriesDatas.isEmpty()){
				
				String[] columns1 = {"SERIESNO","STATUS","EID","CREATETIME" };
				DataValue[] insValue1 = null;
				if(datas.size() > 0) {
					for (level1Elm par : datas) {
					  	String[] columnsName = {"SERIESNO", "SERIES_NAME", "LANG_TYPE","EID"};				  	
					  	//获取
					  	
					  	String lSeriesName = par.getName();
					  	String langType = par.getLangType();					  			  	
					  	DataValue[] insValueDetail = new DataValue[] 
									{
										new DataValue(seriesNO, Types.VARCHAR),
										new DataValue(lSeriesName, Types.VARCHAR),
										new DataValue(langType, Types.VARCHAR),										
										new DataValue(eId, Types.VARCHAR)																						
									};
					  	
						//添加原因码多语言信息
						InsBean ib2 = new InsBean("DCP_SERIES_LANG", columnsName);
						ib2.addValues(insValueDetail);
						this.addProcessData(new DataProcessBean(ib2));
					  	
					}
				}
				
				insValue1 = new DataValue[]{
						new DataValue(seriesNO, Types.VARCHAR), 
						new DataValue(status, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE)
						};
				
				InsBean ib1 = new InsBean("DCP_SERIES", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
				
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功！");	
			}
			else{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行失败: 系列编码  "+seriesNO+" 已存在");	
				return;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常:"+e.getMessage());	
			return;
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SeriesCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SeriesCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SeriesCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SeriesCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空值 ");
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
	protected TypeToken<DCP_SeriesCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SeriesCreateReq>(){};
	}

	@Override
	protected DCP_SeriesCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SeriesCreateRes();
	}
	
	private String isRepeat(String seriesNO, String eId){
		String sql = null;
		sql = "select * from DCP_SERIES "
				+ " where seriesNO = '"+seriesNO+"' "
				+ " and EID = '"+eId+"'";
		return sql;
	}
	
}
