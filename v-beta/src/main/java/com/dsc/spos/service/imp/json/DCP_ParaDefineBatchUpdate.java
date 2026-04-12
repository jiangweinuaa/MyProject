package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ParaDefineBatchUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ParaDefineBatchUpdateReq.ParaList;
import com.dsc.spos.json.cust.res.DCP_ParaDefineBatchUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * V3参数定义批次更新
 * @author 2020-06-02
 *
 */
public class DCP_ParaDefineBatchUpdate extends SPosAdvanceService<DCP_ParaDefineBatchUpdateReq, DCP_ParaDefineBatchUpdateRes> {

	@Override
	protected void processDUID(DCP_ParaDefineBatchUpdateReq req, DCP_ParaDefineBatchUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String modularId = req.getRequest().getModularId();
			String modularName = req.getRequest().getModularName();
			
			List<ParaList> paraDatas = req.getRequest().getItemList();
			if(paraDatas != null && !paraDatas.isEmpty()){
				
				String[] paraArr = new String[paraDatas.size()] ;
				int i=0;
				for (ParaList lvPara : paraDatas) 
				{
					String item = "";
					if(!Check.Null(lvPara.getItem())){
						item = lvPara.getItem().trim();
					}
					paraArr[i] = item;
					i++;
				}
				String itemStr = getString(paraArr);
				
				DelBean db1 = new DelBean("PLATFORM_BASESETTEMP_MODULAR");
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				db1.addCondition("ITEM", new DataValue(itemStr, Types.VARCHAR,DataExpression.IN));
				this.addProcessData(new DataProcessBean(db1)); // 
				
				String[] columns2 = {"EID","ITEM","MODULARID","MODULARNAME"};
				for (ParaList lvPara : paraDatas) {
					
					String item = lvPara.getItem();
					DataValue[] insValue2 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(item, Types.VARCHAR),
							new DataValue(modularId, Types.VARCHAR),
							new DataValue(modularName, Types.VARCHAR)
					};
					InsBean ib2 = new InsBean("PLATFORM_BASESETTEMP_MODULAR", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
				}
				
				//更新参数模板
				UptBean ub1 = null;	
				ub1 = new UptBean("PLATFORM_BASESETTEMP");
				ub1.addUpdateValue("EID", new DataValue(eId, Types.VARCHAR));
				//add Value
				
				if(!Check.Null( req.getRequest().getClassNO() )){
					ub1.addUpdateValue("CLASSNO", new DataValue(req.getRequest().getClassNO() , Types.VARCHAR));
				}
				
				if(!Check.Null( req.getRequest().getParaType() )){
					ub1.addUpdateValue("TYPE", new DataValue(req.getRequest().getParaType() , Types.VARCHAR));
				}
				
				//condition
				ub1.addCondition("ITEM", new DataValue(itemStr, Types.VARCHAR,DataExpression.IN));
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));				
				
				this.doExecuteDataToDB();
				
			}
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ParaDefineBatchUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ParaDefineBatchUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ParaDefineBatchUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ParaDefineBatchUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ParaDefineBatchUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ParaDefineBatchUpdateReq>(){};
	}

	@Override
	protected DCP_ParaDefineBatchUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ParaDefineBatchUpdateRes();
	}

	protected String getString(String[] str)
	{
		String str2 = "";
		for (String s:str)
		{
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		return str2;
	}
	
	
}
