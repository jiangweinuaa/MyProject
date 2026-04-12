package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SpecCreateReq;
import com.dsc.spos.json.cust.res.DCP_SpecCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 新建规格信息  2018-10-22
 * @author yuanyy
 *
 */
public class DCP_SpecCreate extends SPosAdvanceService<DCP_SpecCreateReq, DCP_SpecCreateRes> {

	@Override
	protected void processDUID(DCP_SpecCreateReq req, DCP_SpecCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try {
			String specNO = req.getRequest().getSpecNo();
			String specName = req.getRequest().getSpecName();
			String status = req.getRequest().getStatus();
			String eId = req.geteId();

			sql = this.isRepeat(specNO, eId);
			List<Map<String, Object>> flavorDatas = this.doQueryData(sql, null);
			if(flavorDatas.isEmpty()){

				String[] columns1 = { "SPEC_NO","SPEC_NAME","STATUS","EID" };
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(specNO, Types.VARCHAR),
						new DataValue(specName, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR)
				};

				InsBean ib1 = new InsBean("DCP_SPEC", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"服务执行失败:编码为" +specNO+" 的规格信息已存在");
			}

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SpecCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SpecCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SpecCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SpecCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String specNO = req.getRequest().getSpecNo();

		if(Check.Null(specNO)){
			errMsg.append("规格编码不能为空值 ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_SpecCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SpecCreateReq>(){};
	}

	@Override
	protected DCP_SpecCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SpecCreateRes();
	}

	/**
	 * 验证新增的规格信息是否已经存在
	 * @param specNO
	 * @param eId
	 * @return
	 */
	private String isRepeat(String specNO , String eId ){
		String sql = null;
		sql = "select * from DCP_SPEC "
				+ " where SPEC_NO = '"+specNO +"' "
				+ " and EID = '"+eId+"'";
		return sql;
	}

}
