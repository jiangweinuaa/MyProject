package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_RejectCreateReq;
import com.dsc.spos.json.cust.req.DCP_RejectCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_RejectCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * 服務函數：RejectCreateDCP
 * 服务说明：门店单据驳回
 * @author jinzma 
 * @since  2019-05-29
 */
public class DCP_RejectCreate  extends SPosAdvanceService<DCP_RejectCreateReq,DCP_RejectCreateRes> {

	@Override
	protected void processDUID(DCP_RejectCreateReq req, DCP_RejectCreateRes res) throws Exception {
		// TODO 自动生成的方法存根		
		String eId = req.geteId();
		String docType = req.getDocType();   // 0.要货
		String rejectShopNO="";
		String rejectDocNO="";

		List<level1Elm> datas = req.getDatas();
		try 
		{
			if (docType.equals("0")) //要货单驳回
			{
				String sql="";
				for (level1Elm par:datas )
				{
					rejectShopNO = par.getShopId();
					rejectDocNO=par.getDocNO();
					String reason = par.getReason();

					//查询要货单是否存在
					sql = " select status from DCP_porder "
							+ " where EID='"+eId+"' and SHOPID='"+rejectShopNO+"' and porderno='"+rejectDocNO+"'  "
							+ " and status<>'0' ";
					List<Map<String, Object>> getQData = this.doQueryData(sql, null);
					if (getQData != null && getQData.isEmpty() == false)
					{
						// 0-新建 1-待确定 2-待收货  3-已完成  5-已驳回  6-已审核
						String status = getQData.get(0).get("STATUS").toString();
						if (!status.equals("5"))
						{
							//修改要货单状态和驳回理由
							UptBean ub = new UptBean("DCP_PORDER");
							ub.addUpdateValue("STATUS", new DataValue("5", Types.VARCHAR));
							ub.addUpdateValue("REASON", new DataValue(reason, Types.VARCHAR));
			                ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			                ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));


							//更新条件
							ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							ub.addCondition("ORGANIZATIONNO", new DataValue(rejectShopNO, Types.VARCHAR));
							ub.addCondition("SHOPID", new DataValue(rejectShopNO, Types.VARCHAR));
							ub.addCondition("PORDERNO", new DataValue(rejectDocNO, Types.VARCHAR));
							this.addProcessData(new DataProcessBean(ub));
						}
					}
					else
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "门店 "+rejectShopNO + "前端要货单号"+rejectDocNO+" 不存在或未确认！");
					}
				}
			}		
			this.doExecuteDataToDB();

			res.setDoc_no(rejectDocNO);
			res.setOrg_no(rejectShopNO);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RejectCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RejectCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RejectCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RejectCreateReq req) throws Exception {
		// TODO 自动生成的方法存根		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String docType = req.getDocType();
		if (Check.Null(docType)) {
			errMsg.append("单据类型不可为空值, ");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		List<level1Elm> datas = req.getDatas();
		for (level1Elm par:datas )
		{
			String shopId = par.getShopId();
			String docNO=par.getDocNO();

			if (Check.Null(shopId)) {
				errMsg.append("门店编号(shop_no)不可为空值, ");
				isFail = true;
			}
			if (Check.Null(docNO)) {
				errMsg.append("前端单号(front_no)不可为空值, ");
				isFail = true;
			}

			if (isFail) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_RejectCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_RejectCreateReq>(){};
	}

	@Override
	protected DCP_RejectCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_RejectCreateRes();
	}

}
