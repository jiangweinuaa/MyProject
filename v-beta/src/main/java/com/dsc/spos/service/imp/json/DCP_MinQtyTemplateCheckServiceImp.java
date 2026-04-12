package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateCheckReq;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateCheckReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_MinQtyTemplateCheckRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_MinQtyTemplateCheck 
 * 服务说明：起售量模板启用/禁用
 * @author wangzyc
 * @since 2020-11-11 引用SPosAdvanceService
 */
public class DCP_MinQtyTemplateCheckServiceImp extends SPosAdvanceService<DCP_MinQtyTemplateCheckReq, DCP_MinQtyTemplateCheckRes> {

	@Override
	protected void processDUID(DCP_MinQtyTemplateCheckReq req, DCP_MinQtyTemplateCheckRes res) throws Exception {
		// TODO 自动生成的方法存根

		if(checkExist(req)==false){
			String oprType = req.getRequest().getOprType(); // 修改状态，1启用 2禁用
			String status = null; // 状态：-1未启用100已启用 0已禁用
			if(oprType.equals("1")){
				status = "100";
			}else if(oprType.equals("2")){
				status = "0";
			}

			List<level2Elm> templateList = req.getRequest().getTemplateList();


			String eId = req.geteId();
			String opNO = req.getOpNO();
			String opName = req.getOpName();

			// 获取当前时间
			String sysDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

			try
			{
				// 如果有状态的情况下 再进行修改
				if(Check.Null(status) == false)
				{
					for (level2Elm level2Elm : templateList)
					{
						// 修改单头 DCP_MINQTYTEMPLATE
						UptBean ub1 = new UptBean("DCP_MINQTYTEMPLATE");
						ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
						ub1.addUpdateValue("LASTMODIOPID", new DataValue(opNO, Types.VARCHAR));
						ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
						ub1.addUpdateValue("LASTMODITIME", new DataValue(sysDate, Types.DATE));
						// condition
						ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub1.addCondition("TEMPLATEID", new DataValue(level2Elm.getTemplateId(), Types.VARCHAR));
						this.addProcessData(new DataProcessBean(ub1));
					}
				}
				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			} catch (Exception e) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
			}
		}
		else{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板不存在，请重新输入");
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MinQtyTemplateCheckReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MinQtyTemplateCheckReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MinQtyTemplateCheckReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MinQtyTemplateCheckReq req) throws Exception {
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level2Elm> templateList = req.getRequest().getTemplateList();
		String oprType = req.getRequest().getOprType();
		if (templateList == null && templateList.isEmpty() == false) {
			errMsg.append("模板编码不可为空值, ");
			isFail = true;
		}
		if (Check.Null(oprType)) {
			errMsg.append("修改状态不可为空值, ");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_MinQtyTemplateCheckReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MinQtyTemplateCheckReq>() {
		};
	}

	@Override
	protected DCP_MinQtyTemplateCheckRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MinQtyTemplateCheckRes();
	}

	private boolean checkExist(DCP_MinQtyTemplateCheckReq req) throws Exception {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		List<level2Elm> templateList = req.getRequest().getTemplateList();

		for (level2Elm level2Elm : templateList) {
			String[] conditionValues = { eId, level2Elm.getTemplateId() };
			sql = " SELECT * FROM DCP_MINQTYTEMPLATE WHERE EID = ?  AND TEMPLATEID = ? ";

			List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
			if (getQData != null && getQData.isEmpty() == false) {
			}else{
				exist = true;
			}
		}

		return exist;
	}



}
