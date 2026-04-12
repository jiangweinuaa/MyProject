package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ProcessTaskDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTaskDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ProcessTaskDelete extends SPosAdvanceService<DCP_ProcessTaskDeleteReq, DCP_ProcessTaskDeleteRes>{

	@Override
	protected void processDUID(DCP_ProcessTaskDeleteReq req, DCP_ProcessTaskDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String shopId=req.getShopId();
        List<DCP_ProcessTaskDeleteReq.ProcessTaskList> processTaskList = req.getRequest().getProcessTaskList();

        for (DCP_ProcessTaskDeleteReq.ProcessTaskList pt : processTaskList) {
            String sql = "select status from DCP_PROCESSTASK "
                    + " where EID='" + eId + "' and organizationno='" + organizationNO + "' and PROCESSTASKNO='" + pt.getProcessTaskNo() + "'"
                    + " and status='5' ";
            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
                //DCP_PROCESSTASK
                DelBean db1 = new DelBean("DCP_PROCESSTASK");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("ProcessTaskNO", new DataValue(pt.getProcessTaskNo(), Types.VARCHAR));
                db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //DCP_PROCESSTASK_DETAIL
                DelBean db2 = new DelBean("DCP_PROCESSTASK_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("ProcessTaskNO", new DataValue(pt.getProcessTaskNo(), Types.VARCHAR));
                db2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));


            } else {
                //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
            }
        }
        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ProcessTaskDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ProcessTaskDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ProcessTaskDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ProcessTaskDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		//String processTaskNO = req.getRequest().getProcessTaskNo();
		//if (Check.Null(processTaskNO)) {
		//	isFail = true;
		//	errMsg.append("加工任务单单号不可为空值, ");
		//}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_ProcessTaskDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ProcessTaskDeleteReq>(){};
	}

	@Override
	protected DCP_ProcessTaskDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ProcessTaskDeleteRes();
	}

}
