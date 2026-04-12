package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DualPlayShopUpdateReq;
import com.dsc.spos.json.cust.req.DCP_DualPlayShopUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DualPlayShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：StockTakeUpdate
 *    說明：库存盘点修改
 * 服务说明：库存盘点修改
 * @author panjing
 * @since  2016-09-20
 */
public class DCP_DualPlayShopUpdate extends SPosAdvanceService<DCP_DualPlayShopUpdateReq, DCP_DualPlayShopUpdateRes> {
	@Override
	protected boolean isVerifyFail(DCP_DualPlayShopUpdateReq req) throws Exception {
		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		if (req.getRequest()==null){
			errMsg.append("request不可为空值, ");
			isFail = true;
		}else {
			if (Check.Null(req.getRequest().getDualPlayID())) {
				errMsg.append("双屏播放ID不可为空值, ");
				isFail = true;
			}
		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
		
	}
	
	@Override
	protected TypeToken<DCP_DualPlayShopUpdateReq> getRequestType() {
		return new TypeToken<DCP_DualPlayShopUpdateReq>(){};
	}
	
	@Override
	protected DCP_DualPlayShopUpdateRes getResponseType() {
		return new DCP_DualPlayShopUpdateRes();
	}
	
	@Override
	protected void processDUID(DCP_DualPlayShopUpdateReq req,DCP_DualPlayShopUpdateRes res) throws Exception {
		String eId = req.geteId();
		String dualPlayID =req.getRequest().getDualPlayID();
		try {
			String sql = " select * from DCP_DUALPLAY  where EID= '"+eId+"' and dualplayid = '"+dualPlayID+"'  ";
			List<Map<String, Object>> getQData = this.doQueryData(sql,null);
			if (getQData != null && !getQData.isEmpty()) {
				//删除原来单身
				DelBean db1 = new DelBean("DCP_DUALPLAY_SHOP");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("DUALPLAYID", new DataValue(dualPlayID, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//新增單身 (多筆)				
				if (req.getRequest().getDatas() != null && !req.getRequest().getDatas().isEmpty()) {
					for (level1Elm par : req.getRequest().getDatas()) {
						String[] columns = {"EID", "DUALPLAYID", "SHOPID", "STATUS" };
						DataValue[] insValue = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(dualPlayID, Types.VARCHAR),
								new DataValue(par.getShopId(), Types.VARCHAR),
								new DataValue("100", Types.VARCHAR),
						};
						InsBean ib1 = new InsBean("DCP_DUALPLAY_SHOP", columns);
						ib1.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
					}
				}
				
				this.doExecuteDataToDB();
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
				
			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "双屏播放记录不存在，请重新输入！");
			}
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_DualPlayShopUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_DualPlayShopUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_DualPlayShopUpdateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected String getQuerySql(DCP_DualPlayShopUpdateReq req) throws Exception {
		return null;
	}
	
}
