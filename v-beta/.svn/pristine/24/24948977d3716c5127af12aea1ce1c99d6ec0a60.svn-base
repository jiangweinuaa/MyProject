package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MaterialMsgUpdateReq;
import com.dsc.spos.json.cust.req.DCP_MaterialMsgUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MaterialMsgUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 物料报单修改
 * @author yuanyy 2019-10-28
 *
 */
public class DCP_MaterialMsgUpdate extends SPosAdvanceService<DCP_MaterialMsgUpdateReq, DCP_MaterialMsgUpdateRes> {

	@Override
	protected void processDUID(DCP_MaterialMsgUpdateReq req, DCP_MaterialMsgUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
//			String bDate = req.getbDate();
			List<DCP_MaterialMsgUpdateReq.level1Elm> detailDatas = req.getRequest().getDatas();
			if(detailDatas.size() > 0){
				for (level1Elm map : detailDatas) {
					String status = "0";
					UptBean ub1 = null;	
					ub1 = new UptBean("DCP_PORDER_FORECAST_MATERIAL");
					//add Value
					if(!Check.Null(map.getrQty())){ //实收数量
						ub1.addUpdateValue("RQTY", new DataValue(map.getrQty(), Types.VARCHAR));
					}
					 
					if(!Check.Null(map.getuQty())){ //修改量
						ub1.addUpdateValue("UQTY", new DataValue(map.getuQty(), Types.VARCHAR));
					}
					
					if(!Check.Null(map.getdQty())){ //差异量
						ub1.addUpdateValue("DQTY", new DataValue(map.getdQty(), Types.VARCHAR));
					}
					
					if(!Check.Null(map.gettQty())){ //今日底货
						ub1.addUpdateValue("TQTY", new DataValue(map.gettQty(), Types.VARCHAR));
					}
					
					if(!Check.Null(map.getpQty())){ //实报数量
						ub1.addUpdateValue("PQTY", new DataValue(map.getpQty(), Types.VARCHAR));
					}
					
					if(!Check.Null(req.getRequest().getStatus())){ //要货单状态
						ub1.addUpdateValue("STATUS", new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
					}
					
					ub1.addUpdateValue("PFNO", new DataValue(req.getRequest().getPfNo(), Types.VARCHAR));
					ub1.addUpdateValue("PORDERNO", new DataValue(req.getRequest().getPorderNO(), Types.VARCHAR));
					
					//condition
					ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					ub1.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
					ub1.addCondition("PFNO", new DataValue(req.getRequest().getPfNo(), Types.VARCHAR));
					ub1.addCondition("MATERIAL_PLUNO", new DataValue(map.getMaterialPluNo(), Types.VARCHAR));
					
					this.addProcessData(new DataProcessBean(ub1));
					
				}
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MaterialMsgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MaterialMsgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MaterialMsgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MaterialMsgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_MaterialMsgUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MaterialMsgUpdateReq>(){};
	}

	@Override
	protected DCP_MaterialMsgUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MaterialMsgUpdateRes();
	}
	
	
	
	
}
