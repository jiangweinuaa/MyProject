package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PlanWorkCreateReq;
import com.dsc.spos.json.cust.res.DCP_PlanWorkCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 班次新增 2018-10-10	
 * @author yuanyy
 *
 */
public class DCP_PlanWorkCreate extends SPosAdvanceService<DCP_PlanWorkCreateReq, DCP_PlanWorkCreateRes> {

	@Override
	protected void processDUID(DCP_PlanWorkCreateReq req, DCP_PlanWorkCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try {
			String workNO = req.getRequest().getWorkNo();
			String workName = req.getRequest().getWorkName();
			String bTime = req.getRequest().getbTime();
			String eTime = req.getRequest().geteTime();
			String status = req.getRequest().getStatus();
			String eId = req.geteId();

			sql = this.isRepeat(workNO, eId);
			List<Map<String, Object>> workDatas = this.doQueryData(sql, null);
			if(workDatas.isEmpty()){
				String[] columns1 = { "WORKNO","WORKNAME","BTIME","ETIME","STATUS","EID" };
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(workNO, Types.VARCHAR),
						new DataValue(workName, Types.VARCHAR),
						new DataValue(bTime, Types.VARCHAR),
						new DataValue(eTime, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR)
				};

				InsBean ib1 = new InsBean("DCP_PLANWORK", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"服务执行失败:编码为" +workNO+" 的班次信息已存在");
			}

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PlanWorkCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PlanWorkCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PlanWorkCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PlanWorkCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		//必传值不为空
		String workNO = req.getRequest().getWorkNo();
		if(Check.Null(workNO))
		{
			errCt++;
			errMsg.append("班次编码不能为空值！！ ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400 , errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PlanWorkCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanWorkCreateReq>(){};
	}

	@Override
	protected DCP_PlanWorkCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanWorkCreateRes();
	}

	/**
	 * 验证班次信息是否重复
	 * @param workNO
	 * @param eId
	 * @return
	 */
	private String isRepeat(String workNO , String eId ){
		String sql = null;
		sql = "select * from dcp_PLANWORK "
				+ " where workNO = '"+workNO +"' "
				+ " and EID = '"+eId+"'";
		return sql;
	}
}
