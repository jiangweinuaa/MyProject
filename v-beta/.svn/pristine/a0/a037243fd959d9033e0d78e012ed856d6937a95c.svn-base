package com.dsc.spos.service.imp.json;

import java.io.File;
import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DualPlayDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DualPlayDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_DualPlayDelete extends SPosAdvanceService<DCP_DualPlayDeleteReq, DCP_DualPlayDeleteRes>
{

	@Override
	protected void processDUID(DCP_DualPlayDeleteReq req, DCP_DualPlayDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String dualPlayID = req.getRequest().getDualPlayID();
		String fileName= req.getRequest().getFileName();
		try 
		{
			//DCP_DUALPLAY
			DelBean db1 = new DelBean("DCP_DUALPLAY");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("DUALPLAYID", new DataValue(dualPlayID, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			//DCP_DUALPLAY_SHOP
			DelBean db2 = new DelBean("DCP_DUALPLAY_SHOP");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("DUALPLAYID", new DataValue(dualPlayID, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			
			//DCP_DUALPLAY_TIME
			DelBean db3 = new DelBean("DCP_DUALPLAY_TIME");
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("DUALPLAYID", new DataValue(dualPlayID, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3));

			this.doExecuteDataToDB();	

		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		
		
		try
		{
		//删除\\webapps\\dualplay 
			if(!Check.Null(fileName))
			{
				String dirpath= System.getProperty("catalina.home")+"\\webapps\\dualplay";
				File file =new File(dirpath +"\\" + fileName);    
				if(file.exists())
				{
					file.delete();
				}				
				file=null;
			}				
		}
		catch (Exception ex)
		{
			
		}
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DualPlayDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DualPlayDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DualPlayDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DualPlayDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;
		String dualPlayID = req.getRequest().getDualPlayID();
		if (Check.Null(dualPlayID)) {
			isFail = true;
			errCt++;
			errMsg.append("双屏播放ID不可为空值, ");
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DualPlayDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DualPlayDeleteReq>(){};
	}

	@Override
	protected DCP_DualPlayDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DualPlayDeleteRes();
	}

}
