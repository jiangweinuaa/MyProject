package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateCheckReq;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateDeleteReq;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateDeleteReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_MinQtyTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_MinQtyTemplateDelete 
 * 服务说明：起售量模板删除
 * @author wangzyc
 * @since 2020-11-11 引用SPosAdvanceService
 */
public class DCP_MinQtyTemplateDeleteServiceImp extends SPosAdvanceService<DCP_MinQtyTemplateDeleteReq, DCP_MinQtyTemplateDeleteRes> {

	@Override
	protected void processDUID(DCP_MinQtyTemplateDeleteReq req, DCP_MinQtyTemplateDeleteRes res) throws Exception {
		// TODO 自动生成的方法存根
		if (checkExist(req) == false)
		{
			String eId = req.geteId();
			List<level2Elm> templateList = req.getRequest().getTemplateList();
			try
			{
				for (level2Elm level2Elm : templateList)
				{
					// 删除适用门店 DCP_MINQTYTEMPLATE_RANGE
					DelBean db = new DelBean("DCP_MINQTYTEMPLATE_RANGE");
					db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db.addCondition("TEMPLATEID", new DataValue(level2Elm.getTemplateId(), Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db));

					// 删除单身 DCP_MINQTYTEMPLATE_DETAIL
					db = new DelBean("DCP_MINQTYTEMPLATE_DETAIL");
					db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db.addCondition("TEMPLATEID", new DataValue(level2Elm.getTemplateId(), Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db));

					// 删除多语言 DCP_MINQTYTEMPLATE_LANG
					db = new DelBean("DCP_MINQTYTEMPLATE_LANG");
					db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db.addCondition("TEMPLATEID", new DataValue(level2Elm.getTemplateId(), Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db));

					// 删除单头 DCP_MINQTYTEMPLATE
					db = new DelBean("DCP_MINQTYTEMPLATE");
					db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db.addCondition("TEMPLATEID", new DataValue(level2Elm.getTemplateId(), Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db));
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
		}else{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板不存在，请重新输入");
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MinQtyTemplateDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MinQtyTemplateDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MinQtyTemplateDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MinQtyTemplateDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level2Elm> templateList = req.getRequest().getTemplateList();
		if (templateList == null && templateList.isEmpty() == false) {
			errMsg.append("模板编码不可为空值, ");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_MinQtyTemplateDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MinQtyTemplateDeleteReq>() {
		};
	}

	@Override
	protected DCP_MinQtyTemplateDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MinQtyTemplateDeleteRes();
	}

	private boolean checkExist(DCP_MinQtyTemplateDeleteReq req) throws Exception {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		List<level2Elm> templateList = req.getRequest().getTemplateList();

		for (level2Elm level2Elm : templateList) {
			String[] conditionValues = { eId, level2Elm.getTemplateId() };
			sql = " SELECT * FROM DCP_MINQTYTEMPLATE WHERE EID = ?  AND TEMPLATEID = ? ";

			List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
			if (getQData != null && getQData.isEmpty() == false) {
			} else {
				exist = true;
			}
		}

		return exist;
	}

}
