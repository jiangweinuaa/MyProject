package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DualPlayTimeUpdateReq;
import com.dsc.spos.json.cust.req.DCP_DualPlayTimeUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DualPlayTimeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DualPlayTimeUpdateDCP
 *    說明：双屏播放时间修改
 * 服务说明：双屏播放时间修改
 * @author panjing 
 * @since  2016-09-20
 */
public class DCP_DualPlayTimeUpdate extends SPosAdvanceService<DCP_DualPlayTimeUpdateReq, DCP_DualPlayTimeUpdateRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_DualPlayTimeUpdateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String dualPlayID=req.getRequest().getDualPlayID();	
	
		if (Check.Null(dualPlayID)) {
			errCt++;
			errMsg.append("双屏播放ID不可为空值, ");
			isFail = true;
		} 

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DualPlayTimeUpdateReq> getRequestType() {
		return new TypeToken<DCP_DualPlayTimeUpdateReq>(){};
	}

	@Override
	protected DCP_DualPlayTimeUpdateRes getResponseType() {
		return new DCP_DualPlayTimeUpdateRes();
	}

	@Override
	protected void processDUID(DCP_DualPlayTimeUpdateReq req,DCP_DualPlayTimeUpdateRes res) throws Exception {
		StringBuffer errMsg = new StringBuffer("");	   
		String eId = req.geteId();
		String dualPlayID = req.getRequest().getDualPlayID();

		try 
		{
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {eId,dualPlayID}; //查詢條件
			List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
			if (getQData != null && getQData.isEmpty() == false) {
				
			//删除原来单身
				DelBean db1 = new DelBean("DCP_DUALPLAY_TIME");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("DUALPLAYID", new DataValue(dualPlayID, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				DataValue[] insValue = null;
				//新增單身 (多筆)				
				if (req.getRequest().getDatas() != null && req.getRequest().getDatas().isEmpty() == false && req.getRequest().getDatas().size() > 0)	
				{

					for (level1Elm par : req.getRequest().getDatas()) {
						String[] columns = {"EID", "DUALPLAYID", "ITEM","LBDATE","LEDATE","LBTIME","LETIME","STATUS" };
						insValue = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(dualPlayID, Types.VARCHAR), 
								new DataValue(par.getItem(), Types.INTEGER),								
								new DataValue(par.getLbDate(), Types.VARCHAR),
								new DataValue(par.getLeDate(), Types.VARCHAR),
								new DataValue(par.getLbTime(), Types.VARCHAR),
								new DataValue(par.getLeTime(), Types.VARCHAR),
								new DataValue("100", Types.VARCHAR), 
						};				
						InsBean ib1 = new InsBean("DCP_DUALPLAY_TIME", columns);
						ib1.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib1)); 

					}	
				}
				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");				
			}
			else
			{
				errMsg.append("双屏播放记录不存在，请重新输入！");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}	
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}	

	@Override
	protected List<InsBean> prepareInsertData(DCP_DualPlayTimeUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DualPlayTimeUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DualPlayTimeUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected String getQuerySql(DCP_DualPlayTimeUpdateReq req) throws Exception {
		String sql = null;
		sql= " select *  from DCP_DUALPLAY  where EID= ? and dualplayid = ?  ";
		return sql;
	}

}
