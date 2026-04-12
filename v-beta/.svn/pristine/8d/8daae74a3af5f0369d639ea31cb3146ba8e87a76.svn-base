package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.temporal.IsoFields;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.format.ISOPeriodFormat;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineClassUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineClassUpdateReq.level1Plu;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineUpdateReq.classMemu;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineClassUpdateReq.level1Class;
import com.dsc.spos.json.cust.res.DCP_GoodsOnlineClassUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsOnlineClassUpdate extends SPosAdvanceService<DCP_GoodsOnlineClassUpdateReq, DCP_GoodsOnlineClassUpdateRes> {

	@Override
	protected void processDUID(DCP_GoodsOnlineClassUpdateReq req, DCP_GoodsOnlineClassUpdateRes res) throws Exception {
	// TODO Auto-generated method stub			
		String lastmoditime = null;//req.getRequset().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}

		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		
		
		String classType = "ONLINE";
		
		List<level1Plu> pluList = req.getRequest().getPluList();
		List<level1Class> classList = req.getRequest().getClassList();
		
		for (level1Plu par : pluList) 
		{
			String pluNo = par.getPluNo();
			String pluType = par.getPluType();
		 //先删除原来的
			DelBean	db1 = new DelBean("DCP_CLASS_GOODS");
			db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(db1));
			

			String[] columns_class ={"EID","CLASSTYPE","CLASSNO","PLUNO","PLUTYPE","SORTID","LASTMODITIME"
			};
			int sortId = 1;
			for (level1Class par_class : classList) 
			{
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[] 
						{ 
								new DataValue(req.geteId(), Types.VARCHAR),								
								new DataValue(classType, Types.VARCHAR),
								new DataValue(par_class.getClassNo(), Types.VARCHAR),		
								new DataValue(pluNo, Types.VARCHAR),
								new DataValue(pluType, Types.VARCHAR),
								new DataValue(sortId, Types.VARCHAR),
								new DataValue(lastmoditime, Types.DATE) 
						};

				InsBean ib1 = new InsBean("DCP_CLASS_GOODS", columns_class);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));
				sortId ++;
		
		
			}
		
		}

		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsOnlineClassUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsOnlineClassUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsOnlineClassUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsOnlineClassUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_GoodsOnlineClassUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsOnlineClassUpdateReq>(){};
	}

	@Override
	protected DCP_GoodsOnlineClassUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsOnlineClassUpdateRes();
	}
	
}
