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
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateUpdateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateUpdateReq.level3Elm;
import com.dsc.spos.json.cust.res.DCP_MinQtyTemplateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_MinQtyTemplateUpdate 
 * 服务说明：起售量模板修改
 * @author wangzyc
 * @since 2020-11-11 引用SPosAdvanceService
 */
public class DCP_MinQtyTemplateUpdateServiceImp extends SPosAdvanceService<DCP_MinQtyTemplateUpdateReq, DCP_MinQtyTemplateUpdateRes> {

	@Override
	protected boolean isVerifyFail(DCP_MinQtyTemplateUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考虑当错误很多时则直接提示格式错误；
		String templateId = req.getRequest().getTemplateId();
		String templateName = req.getRequest().getTemplateName();
		String status = req.getRequest().getStatus();

		if (PosPub.isNumeric(req.getRequest().getRestrictShop())==false)  req.getRequest().setRestrictShop("0");
		if (PosPub.isNumeric(req.getRequest().getRestrictChannel())==false)  req.getRequest().setRestrictChannel("0");
		if (PosPub.isNumeric(req.getRequest().getRestrictPeriod())==false)  req.getRequest().setRestrictPeriod("0");

		String restrictShop = req.getRequest().getRestrictShop();
		String restrictChannel = req.getRequest().getRestrictChannel(); //适用渠道：0-所有渠道1-指定渠道2-排除渠道
		String restrictPeriod = req.getRequest().getRestrictPeriod(); //适用时段：0-所有时段1-指定时段

		List<level3Elm> pluList = req.getRequest().getPluList();
		List<level2Elm> shopList = req.getRequest().getShopList();

		if (Check.Null(templateId)) {
			errCt++;
			errMsg.append("模板编码不可为空值, ");
			isFail = true;
		}
		if (Check.Null(templateName)) {
			errCt++;
			errMsg.append("模板名称不可为空值, ");
			isFail = true;
		}
		if (Check.Null(status)) {
			errCt++;
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}
		if (Check.Null(restrictShop)) {
			errCt++;
			errMsg.append("适用门店不可为空值, ");
			isFail = true;
		}

		if (pluList != null && pluList.isEmpty() == false) {
			for (level3Elm level3Elm : pluList) {
				String pluNo = level3Elm.getPluNo();
				String minQty = level3Elm.getMinQty();

				if (Check.Null(pluNo)) {
					errCt++;
					errMsg.append("商品编码不可为空值, ");
					isFail = true;
				}
				if (Check.Null(minQty)) {
					errCt++;
					errMsg.append("起售量不可为空值, ");
					isFail = true;
				}
			}
		} else {
			errCt++;
			errMsg.append("商品列表不可为空值, ");
			isFail = true;
		}

		if (restrictShop.equals("1")) {
			if (shopList != null && shopList.isEmpty() == false) {
				for (level2Elm level2Elm : shopList) {
					String id = level2Elm.getId();
					String name = level2Elm.getName();

					if (Check.Null(id)) {
						errCt++;
						errMsg.append("门店门店编号不可为空值, ");
						isFail = true;
					}
					if (Check.Null(name)) {
						errCt++;
						errMsg.append("门店名称不可为空值, ");
						isFail = true;
					}
				}
			} else {
				errCt++;
				errMsg.append("适用门店列表不可为空值, ");
				isFail = true;
			}
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected void processDUID(DCP_MinQtyTemplateUpdateReq req, DCP_MinQtyTemplateUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根

		String templateId = req.getRequest().getTemplateId();
		String templateName = req.getRequest().getTemplateName();
		String memo = req.getRequest().getMemo();
		String status = req.getRequest().getStatus();
		String restrictShop = req.getRequest().getRestrictShop();
		List<level2Elm> shopList = req.getRequest().getShopList();
		List<level3Elm> pluList = req.getRequest().getPluList();

		String eId = req.geteId();
		String opNO = req.getOpNO();
		String opName = req.getOpName();

		// 获取当前时间
		String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String updateDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

		try
		{
			if (checkExist(req) == true)
			{
				DataValue[] insValue = null;
				// 删除适用门店
				DelBean db = new DelBean("DCP_MINQTYTEMPLATE_RANGE");
				db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db));

				if (restrictShop.equals("1"))
				{
					// 模板适用门店 DCP_MINQTYTEMPLATE_RANGE 保存资料
					for (level2Elm level2Elm : shopList) {
						String[] columnsDcpMinQtyTemplateRange = { "EID", "TEMPLATEID", "TEMPLATENAME","RANGETYPE","ID", "NAME", "LASTMODITIME" };
						insValue = null;
						insValue = new DataValue[] { new DataValue(eId, Types.VARCHAR),
								new DataValue(templateId, Types.VARCHAR),
								new DataValue(templateName, Types.VARCHAR),
								new DataValue(2, Types.INTEGER),
								new DataValue(level2Elm.getId(), Types.VARCHAR),
								new DataValue(level2Elm.getName(), Types.VARCHAR), new DataValue(createDate, Types.DATE) };
						InsBean ib3 = new InsBean("DCP_MINQTYTEMPLATE_RANGE", columnsDcpMinQtyTemplateRange);
						ib3.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib3));
					}
				}

				// 删除单据单身
				DelBean db1 = new DelBean("DCP_MINQTYTEMPLATE_DETAIL");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				// DCP_MINQTYTEMPLATE_DETAIL 单身 (多条) 保存资料
				String[] columnsDcpMinQtyTemplateDetail = { "EID", "TEMPLATEID", "PLUNO", "MOQ", "LASTMODIOPID",
						"LASTMODIOPNAME", "UPDATE_TIME" };
				for (level3Elm level3Elm : pluList) {
					insValue = new DataValue[] { new DataValue(eId, Types.VARCHAR),
							new DataValue(templateId, Types.VARCHAR),
							new DataValue(level3Elm.getPluNo(), Types.VARCHAR),
							new DataValue(level3Elm.getMinQty(), Types.VARCHAR), new DataValue(opNO, Types.VARCHAR),
							new DataValue(opName, Types.VARCHAR), new DataValue(updateDate, Types.VARCHAR) };
					InsBean ib1 = new InsBean("DCP_MINQTYTEMPLATE_DETAIL", columnsDcpMinQtyTemplateDetail);
					ib1.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib1));
				}

				// 修改 DCP_MINQTYTEMPLATE_LANG 模板多语言信息
				UptBean ub1 = new UptBean("DCP_MINQTYTEMPLATE_LANG");
				ub1.addUpdateValue("LANG_TYPE", new DataValue(req.getLangType(), Types.VARCHAR));
				ub1.addUpdateValue("TEMPLATENAME", new DataValue(templateName, Types.VARCHAR));
				ub1.addUpdateValue("UPDATE_TIME", new DataValue(updateDate, Types.VARCHAR));
				// condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));

				// 修改单头 DCP_MINQTYTEMPLATE
				UptBean ub2 = new UptBean("DCP_MINQTYTEMPLATE");
				ub2.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
				ub2.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				ub2.addUpdateValue("LASTMODIOPID", new DataValue(opNO, Types.VARCHAR));
				ub2.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
				ub2.addUpdateValue("LASTMODITIME", new DataValue(createDate, Types.DATE));
				ub2.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.INTEGER));
				// condition
				ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub2.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub2));

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}else{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板不存在，请重新输入");
			}
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MinQtyTemplateUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MinQtyTemplateUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MinQtyTemplateUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected TypeToken<DCP_MinQtyTemplateUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MinQtyTemplateUpdateReq>() {
		};
	}

	@Override
	protected DCP_MinQtyTemplateUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MinQtyTemplateUpdateRes();
	}

	private boolean checkExist(DCP_MinQtyTemplateUpdateReq req) throws Exception {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		String templateId = req.getRequest().getTemplateId();

		String[] conditionValues = { eId, templateId };
		sql = " SELECT * FROM DCP_MINQTYTEMPLATE WHERE EID = ?  AND TEMPLATEID = ? ";

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}

}
