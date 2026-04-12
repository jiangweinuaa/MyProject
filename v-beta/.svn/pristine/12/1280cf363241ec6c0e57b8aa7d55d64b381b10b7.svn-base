package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsMappingCreateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsMappingCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsMappingCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsMappingCreate extends SPosAdvanceService<DCP_GoodsMappingCreateReq,DCP_GoodsMappingCreateRes> {

	@Override
	protected void processDUID(DCP_GoodsMappingCreateReq req, DCP_GoodsMappingCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		
		String eId = req.geteId();;
		String platformType = req.getRequest().getPlatformType();
		for(level1Elm par : req.getRequest().getDatas())
		{
			String sql="SELECT * FROM DCP_GOODS_MAPPING WHERE EID='"+eId+"' AND PLATFORMTYPE='"+platformType+"' "
					+ " AND PLUNO='"+par.getPluNo()+"' AND UNITID='"+par.getUnitId()+"' AND PLATFORMPLUNO='"+par.getPlatformPluNo()+"'";
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if(getQData!=null && getQData.isEmpty()==false)
			{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编码:"+par.getPluNo()+",单位编码:"+par.getUnitId()+",第三方品号:"+par.getPlatformPluNo()+"已存在，请检查录入的资料");
			}
			String[] columns2 = {
	  			"EID", "PLATFORMTYPE","PLUNO","PLUNAME", "PLATFORMPLUNO", "SHORTCUT_CODE","STATUS","UNITID"
			    };
			DataValue[] insValue2 = null;
			
			insValue2 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(platformType, Types.VARCHAR),
					new DataValue(par.getPluNo(), Types.VARCHAR),
					new DataValue(par.getPluName(), Types.VARCHAR),
					new DataValue(par.getPlatformPluNo(), Types.VARCHAR),
					new DataValue(par.getShortCutCode(), Types.VARCHAR),					
					new DataValue("100", Types.VARCHAR),
					new DataValue(par.getUnitId(), Types.VARCHAR)
				};
			
			InsBean ib2 = new InsBean("DCP_GOODS_MAPPING", columns2);
			ib2.addValues(insValue2);
			this.addProcessData(new DataProcessBean(ib2));
		}
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsMappingCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsMappingCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsMappingCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsMappingCreateReq req) throws Exception {

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

        List<level1Elm> josnDatas = req.getRequest().getDatas();
        if (josnDatas == null || josnDatas.isEmpty()) {
            errCt++;
            errMsg.append("没有映射的数据, ");
            isFail = true;
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (level1Elm par : josnDatas) {
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
	protected TypeToken<DCP_GoodsMappingCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsMappingCreateReq>(){};
	}

	@Override
	protected DCP_GoodsMappingCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsMappingCreateRes();
	}

}
