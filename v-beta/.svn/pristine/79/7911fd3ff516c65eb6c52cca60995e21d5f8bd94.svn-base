package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DingFuncCreateReq;
import com.dsc.spos.json.cust.req.DCP_DingFuncCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_DingFuncCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_DingFuncCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DingFuncCreateDCP
 * 服务说明：钉钉功能审批新增
 * @author jinzma
 * @since  2019-10-31
 */
public class DCP_DingFuncCreate extends SPosAdvanceService<DCP_DingFuncCreateReq,DCP_DingFuncCreateRes>{

	@Override
	protected void processDUID(DCP_DingFuncCreateReq req, DCP_DingFuncCreateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String funcNO = req.getRequest().getFuncNo();
		String templateNO = req.getRequest().getTemplateNo();
		String status = req.getRequest().getStatus();

		try
		{
			if (checkExist(req) == false)
			{
				// DCP_DING_FUNC
				String[] columns = { "EID", "FUNCNO","TEMPLATENO","STATUS" };
				DataValue[] insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(funcNO, Types.VARCHAR), 
						new DataValue(templateNO, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
				};

				//DCP_DING_FUNC_SHOP
				List<level1Elm> level1Elm = req.getRequest().getDatas();
				for (level1Elm level1 : level1Elm) 
				{
					String shopId = level1.getShopId();
					String defUserID = level1.getDefUserID();
					String defDeptID = level1.getDefDeptID();

					String[] columns_shop = { "EID", "FUNCNO","SHOPID","DEF_USERID","DEF_DEPTID","STATUS" };
					DataValue[] insValue_shop = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(funcNO, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR),
							new DataValue(defUserID, Types.VARCHAR),
							new DataValue(defDeptID, Types.VARCHAR),
							new DataValue("100", Types.VARCHAR),
					};

					//DCP_DING_FUNC_SHOP_APPROVEDBY
					List<level2Elm> level2Elm = level1.getDatas();
					//新增或修改门店时，至少有一笔审批人
					if (level2Elm == null || level2Elm.isEmpty())
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "审批人不能为空！");		
					}

					for (level2Elm level2 : level2Elm) 
					{
						String approvedByid = level2.getApprovedByid();
						String approvedByDeptID = level2.getApprovedByDeptID();

						String[] columns_appr = { "EID", "FUNCNO","SHOPID","APPROVEDBYID","APPROVEDBYDEPTID","STATUS" };
						DataValue[] insValue_appr = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(funcNO, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(approvedByid, Types.VARCHAR),
								new DataValue(approvedByDeptID, Types.VARCHAR),
								new DataValue("100", Types.VARCHAR),
						};

						InsBean ib = new InsBean("DCP_DING_FUNC_SHOP_APPROVEDBY", columns_appr);
						ib.addValues(insValue_appr);
						this.addProcessData(new DataProcessBean(ib)); 
					}
					InsBean ib = new InsBean("DCP_DING_FUNC_SHOP", columns_shop);
					ib.addValues(insValue_shop);
					this.addProcessData(new DataProcessBean(ib)); 
				}
				InsBean ib = new InsBean("DCP_DING_FUNC", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); 
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "功能编号已存在！");		
			}

			this.doExecuteDataToDB();
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
	protected List<InsBean> prepareInsertData(DCP_DingFuncCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DingFuncCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DingFuncCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DingFuncCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String funcNO = req.getRequest().getFuncNo();
		String templateNO = req.getRequest().getTemplateNo();
		String status = req.getRequest().getStatus();

		if (Check.Null(funcNO)) 
		{
			errMsg.append("功能编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(templateNO)) 
		{
			errMsg.append("模板编号不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(status)) 
		{
			errMsg.append("状态不可为空值, ");
			isFail = true;
		} 
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DingFuncCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingFuncCreateReq>(){};
	}

	@Override
	protected DCP_DingFuncCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingFuncCreateRes();
	}

	private boolean checkExist(DCP_DingFuncCreateReq req)  throws Exception {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		String funcNO =req.getRequest().getFuncNo();

		sql = " SELECT * FROM DCP_DING_FUNC  where EID='"+eId+"' and FUNCNO='"+funcNO+"'  " ;
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}

}
