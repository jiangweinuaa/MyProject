package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsMappingCreateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsMappingDeleteReq;
import com.dsc.spos.json.cust.req.DCP_GoodsMappingDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsMappingDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsMappingDelete extends SPosAdvanceService<DCP_GoodsMappingDeleteReq,DCP_GoodsMappingDeleteRes> 
{

	@Override
	protected void processDUID(DCP_GoodsMappingDeleteReq req, DCP_GoodsMappingDeleteRes res) throws Exception 
	{	
	  // TODO Auto-generated method stub	
		String eId = req.geteId();
		String platformType = req.getRequest().getPlatformType();
		for(level1Elm par : req.getRequest().getDatas())
		{
			DelBean db1 = new DelBean("DCP_GOODS_MAPPING");			
			db1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
			db1.addCondition("PLATFORMTYPE", new DataValue(platformType, Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(par.getPluNo(), Types.VARCHAR));
            db1.addCondition("UNITID", new DataValue(par.getUnitId(), Types.VARCHAR));
            db1.addCondition("PLATFORMPLUNO", new DataValue(par.getPlatformPluNo(), Types.VARCHAR));
				
			this.addProcessData(new DataProcessBean(db1));
		}
		 
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsMappingDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsMappingDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsMappingDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsMappingDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;
        if (req.getRequest().getPlatformType() == null) {
            errCt++;
            errMsg.append("平台类型不能为空, ");
            isFail = true;
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        List<DCP_GoodsMappingDeleteReq.level1Elm> josnDatas = req.getRequest().getDatas();
        if (josnDatas == null || josnDatas.isEmpty()) {
            errCt++;
            errMsg.append("没有映射的数据, ");
            isFail = true;
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_GoodsMappingDeleteReq.level1Elm par : josnDatas) {
            if (Check.Null(par.getPluNo())) {
                errCt++;
                errMsg.append("ERP商品编码不可为空值, ");
                isFail = true;
            }
            if (Check.Null(par.getUnitId())) {
                errCt++;
                errMsg.append("ERP商品单位编码不可为空值, ");
                isFail = true;
            }
            if (Check.Null(par.getPlatformPluNo())) {
                errCt++;
                errMsg.append("第三方品号不可为空值, ");
                isFail = true;
            } else {
                par.setPlatformPluNo(par.getPlatformPluNo().trim());
                if (Check.Null(par.getPlatformPluNo())) {
                    errCt++;
                    errMsg.append("第三方品号不可为空值, ");
                    isFail = true;
                }
            }
            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }


        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

	@Override
	protected TypeToken<DCP_GoodsMappingDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsMappingDeleteReq>(){};
	}

	@Override
	protected DCP_GoodsMappingDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsMappingDeleteRes();
	}

}
