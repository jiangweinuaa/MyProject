package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ElementCreateReq;
import com.dsc.spos.json.cust.res.DCP_ElementCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 影响因素新增
 * @author yuanyy 
 *
 */
public class DCP_ElementCreate extends SPosAdvanceService<DCP_ElementCreateReq, DCP_ElementCreateRes> {

	@Override
	protected void processDUID(DCP_ElementCreateReq req, DCP_ElementCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		try {
			String eId = req.geteId();
			String eType = req.getRequest().gete_Type();
			String eNo = req.getRequest().geteNo();
			String eName = req.getRequest().geteName();
			String eRatio = req.getRequest().geteRatio();
			String status = req.getRequest().getStatus();
			sql = "select * from DCP_ELEMENT where EID = '"+eId+"' and e_type = '"+eType+"' and e_No = '"+eNo+"' "
					+ " and SHOPID = '"+req.getShopId()+"' ";

			List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
			if(getDatas.size() == 0){

				String[] columns1 = {"EID", "E_TYPE","E_NO","E_NAME", "E_RATIO","STATUS","SHOPID" };
				DataValue[] insValue1 = null;	

				//添加原因吗信息
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(eType, Types.VARCHAR),
						new DataValue(eNo, Types.VARCHAR),
						new DataValue(eName, Types.VARCHAR),
						new DataValue(eRatio, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(req.getShopId(), Types.VARCHAR)
				};

				InsBean ib1 = new InsBean("DCP_ELEMENT", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭				
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "因素类型 编码为" + eNo +" 已存在！");	
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
	protected List<InsBean> prepareInsertData(DCP_ElementCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ElementCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ElementCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ElementCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		//必传值不为空
		String eType = req.getRequest().gete_Type();

		if(Check.Null(eType)){
			errMsg.append("因素类型不能为空值 ");
			isFail = true;
		}
		if(Check.Null(req.getRequest().geteNo())){
			errMsg.append("因素编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(req.getRequest().geteRatio())){
			errMsg.append("影响比例不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ElementCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ElementCreateReq>(){};
	}

	@Override
	protected DCP_ElementCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ElementCreateRes();
	}


}
