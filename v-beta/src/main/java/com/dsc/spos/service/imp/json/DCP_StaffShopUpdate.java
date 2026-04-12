package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StaffShopUpdateReq;
import com.dsc.spos.json.cust.req.DCP_StaffShopUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_StaffShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_StaffShopUpdate extends SPosAdvanceService<DCP_StaffShopUpdateReq, DCP_StaffShopUpdateRes>
{
	@Override
	protected void processDUID(DCP_StaffShopUpdateReq req, DCP_StaffShopUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();;
		String staffNO = req.getRequest().getStaffNo();
		
		//先删除后新增
		DelBean db1 = new DelBean("PLATFORM_STAFFS_SHOP");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("opNO", new DataValue(staffNO, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));
		
		List<level1Elm> jsonSelectedShops = req.getRequest().getSelectedShops();
		for (level1Elm par : jsonSelectedShops) {
			int insColCt = 0;
			String[] columns1 = {"EID","OPNO","SHOPID","ISDEFAULT","STATUS"};
			DataValue[] insValue1 = new DataValue[columns1.length];
			for (int i = 0; i < insValue1.length; i++) { 
				String keyVal = null;
				switch (i) {
					case 0:
						keyVal = eId;
						break;
					case 1:
						keyVal = staffNO;
						break;
					case 2:
						keyVal = par.getShopId();
						break;
					case 3:
						keyVal = "N";
						break;
					case 4:
						keyVal = "100";
						break;
					default:
						break;
				}
				
				if (keyVal != null) {
					insColCt++;
					if (i == 0 || i == 1 || i == 2){
						insValue1[i] = new DataValue(keyVal, Types.VARCHAR);
					}else{
						insValue1[i] = new DataValue(keyVal, Types.VARCHAR);
					}
				} else {
					insValue1[i] = null;
				}
			}

			InsBean ib1 = new InsBean("PLATFORM_STAFFS_SHOP", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1));
		}
		
		this.doExecuteDataToDB();
		if (res.isSuccess()) 
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StaffShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StaffShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StaffShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StaffShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; 
		List<level1Elm> jsonSelectedShops = req.getRequest().getSelectedShops();
		
		//必传值不为空
		String staffNO = req.getRequest().getStaffNo();
		
		if(Check.Null(staffNO)){
			errCt++;
			errMsg.append("用户编码不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for(level1Elm par : jsonSelectedShops){
			if (Check.Null(par.getShopId())) 
			{
				errCt++;
				errMsg.append("门店编码不可为空值, ");
				isFail = true;
			}
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StaffShopUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StaffShopUpdateReq>(){};
	}

	@Override
	protected DCP_StaffShopUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StaffShopUpdateRes();
	}
}
