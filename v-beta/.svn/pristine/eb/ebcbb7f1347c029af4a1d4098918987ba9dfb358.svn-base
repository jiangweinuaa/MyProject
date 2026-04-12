package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CollectionUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CollectionUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_CollectionUpdate extends SPosAdvanceService<DCP_CollectionUpdateReq,DCP_CollectionUpdateRes>
{
	@Override
	protected void processDUID(DCP_CollectionUpdateReq req, DCP_CollectionUpdateRes res) throws Exception 
	{	
		if(req.getRequest().getType().equals("1"))
		{
			//新增SQL
			int insColCt = 0;
			String[] columnsCOLLECTION ={"EID","OPNO","MODULARNO"};
			DataValue[] columnsVal = new DataValue[columnsCOLLECTION.length];

			for (int i = 0; i < columnsVal.length; i++)
			{				
				String keyVal = null;
				switch (i) 
				{
				case 0:
					keyVal=req.geteId();
					break;
				case 1:
					keyVal=req.getOpNO();
					break;
				case 2:
					keyVal=req.getRequest().getModularNo();
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

			String[] columns2 = new String[insColCt];
			DataValue[] insValue2 = new DataValue[insColCt];

			insColCt = 0;

			for (int i = 0; i < columnsVal.length; i++) 
			{
				if (columnsVal[i] != null) 
				{
					columns2[insColCt] = columnsCOLLECTION[i];
					insValue2[insColCt] = columnsVal[i];
					insColCt++;
					if (insColCt >= insValue2.length)
						break;
				}
			}

			//收藏
			InsBean ib2 = new InsBean("DCP_COLLECTION", columns2);
			ib2.addValues(insValue2);
			this.addProcessData(new DataProcessBean(ib2));				
		}
		else
		{
			//取消收藏
			DelBean db1 = new DelBean("DCP_COLLECTION");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("OPNO", new DataValue(req.getOpNO(), Types.VARCHAR));
			db1.addCondition("MODULARNO", new DataValue(req.getRequest().getModularNo(), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
		}

		this.doExecuteDataToDB();
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CollectionUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CollectionUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CollectionUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CollectionUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		
		if (Check.Null(req.getRequest().getModularNo())) 
		{
			errCt++;
			errMsg.append("菜单编码不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(req.getRequest().getType())) 
		{
			errCt++;
			errMsg.append("操作类型不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_CollectionUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_CollectionUpdateReq>(){};
	}

	@Override
	protected DCP_CollectionUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_CollectionUpdateRes();
	}

}
