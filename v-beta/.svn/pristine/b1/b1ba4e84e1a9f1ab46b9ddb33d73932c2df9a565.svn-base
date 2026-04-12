package com.dsc.spos.service.imp.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.config.SPosConfig.Value;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_NRCRestfulStatusQueryReq;
import com.dsc.spos.json.cust.res.DCP_NRCRestfulStatusQueryRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_NRCRestfulStatusQuery extends SPosAdvanceService<DCP_NRCRestfulStatusQueryReq, DCP_NRCRestfulStatusQueryRes>
{

	@Override
	protected void processDUID(DCP_NRCRestfulStatusQueryReq req, DCP_NRCRestfulStatusQueryRes res) throws Exception 
	{
		res.setDatas(new ArrayList<DCP_NRCRestfulStatusQueryRes.level1Elm>());//

		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		String mySysDate=dfDate.format(cal.getTime());
		//时间
		SimpleDateFormat dfTime=new SimpleDateFormat("HHmmss");
		String mySysTime = dfTime.format(cal.getTime());			

		List<Map<String, Object>> getQData=null;

		boolean bDisHistory=false;//定时器调用不需要回传历史记录		
		if(req.getDisHistory()!=null && req.getDisHistory().equals("Y"))
		{
			bDisHistory=true;        	

			String sql="select * from DCP_nrcreststatus where sdate>='"+PosPub.GetStringDate(mySysDate, -1)+"' and sdate<='"+mySysDate+"' order by restfuladdress,sdate DESC,stime DESC ";
			getQData = this.doQueryData(sql, null);
		}

		//查找中台监控服务节点
		List<Value> listRest=StaticInfo.psc.getNrcRestfulInterface().getRestfulService();
		for (int i = 0; i < listRest.size(); i++) 
		{			
			DCP_NRCRestfulStatusQueryRes.level1Elm lv=res.new level1Elm();

			String invokeUrl=listRest.get(i).getValue();
			String description= listRest.get(i).getTagValue();				
			if (bDisHistory) 
			{
				Map<String, Object> map_condition = new HashMap<String, Object>(); //查詢條件
				map_condition.put("RESTFULADDRESS", invokeUrl);		
				List<Map<String, Object>> getQHeader=MapDistinct.getWhereMap(getQData,map_condition,true);

				lv.setDatas(new ArrayList<DCP_NRCRestfulStatusQueryRes.level2Elm>());
				for (Map<String, Object> oneData : getQHeader)
				{
					DCP_NRCRestfulStatusQueryRes.level2Elm lv2=res.new level2Elm();
					lv2.setsDescription(oneData.get("DESCRIPTION").toString());
					lv2.setsDate(oneData.get("SDATE").toString());
					lv2.setsTime(oneData.get("STIME").toString());
					lv2.setsStatus(oneData.get("STATUS").toString());		
					
					lv.getDatas().add(lv2);
					lv2 = null;
				}
			}			

			String responseStr="";
			try 
			{			
				//检测
				responseStr=HttpSend.SendNCRRestfulStatus(invokeUrl, description);	        
			} 
			catch (Exception e) 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);	
				
				pw.flush();
				pw.close();			
				
				errors.flush();
				errors.close();

				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("执行异常:" + e.getMessage()+"\r\n" +errors.toString()+"\r\n" );
				
				pw=null;
				errors=null;
			}		
			finally 
			{
				//
				lv.setServiceDescription(description);
				lv.setRestfulUrl(invokeUrl);
				if (responseStr.equals("")) 
				{
					lv.setStatus("0");
				}
				else
				{
					lv.setStatus("1");
				}



				res.getDatas().add(lv);     
				//
				String[] columns1 = {"RESTFULADDRESS","DESCRIPTION","STATUS","SDATE","STIME"};

				DataValue[] insValue = new DataValue[]{
						new DataValue(invokeUrl, Types.VARCHAR), 
						new DataValue(description, Types.VARCHAR), 
						new DataValue(lv.getStatus(), Types.VARCHAR),
						new DataValue(mySysDate, Types.VARCHAR),				
						new DataValue(mySysTime, Types.VARCHAR)			
				};
				InsBean ib1 = new InsBean("DCP_NRCRESTSTATUS", columns1);
				ib1.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib1)); //		
				
				lv = null;
			}

		}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_NRCRestfulStatusQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_NRCRestfulStatusQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_NRCRestfulStatusQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_NRCRestfulStatusQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_NRCRestfulStatusQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_NRCRestfulStatusQueryReq>(){};
	}

	@Override
	protected DCP_NRCRestfulStatusQueryRes getResponseType() 
	{
		return new DCP_NRCRestfulStatusQueryRes();
	}



}
