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
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateCreateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_MinQtyTemplateCreateReq.level3Elm;
import com.dsc.spos.json.cust.res.DCP_MinQtyTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_MinQtyTemplateCreate 
 * 服务说明：商品起售量模板添加
 * @author wangzyc
 * @since 2020-11-10 引用SPosAdvanceService
 */
public class DCP_MinQtyTemplateCreateServiceImp extends SPosAdvanceService<DCP_MinQtyTemplateCreateReq, DCP_MinQtyTemplateCreateRes> {
	@Override
	protected boolean isVerifyFail(DCP_MinQtyTemplateCreateReq req) throws Exception {
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考虑当错误很多时则直接提示格式错误；
		String templateName = req.getRequest().getTemplateName();
		String status = req.getRequest().getStatus();

		if (PosPub.isNumeric(req.getRequest().getRestrictShop())==false)  req.getRequest().setRestrictShop("0");
		if (PosPub.isNumeric(req.getRequest().getRestrictChannel())==false)  req.getRequest().setRestrictChannel("0");
		if (PosPub.isNumeric(req.getRequest().getRestrictPeriod())==false)  req.getRequest().setRestrictPeriod("0");

		String restrictShop = req.getRequest().getRestrictShop(); // 适用门店：0-所有门店1-指定门店2-排除门店
		String restrictChannel = req.getRequest().getRestrictChannel(); //适用渠道：0-所有渠道1-指定渠道2-排除渠道
		String restrictPeriod = req.getRequest().getRestrictPeriod(); //适用时段：0-所有时段1-指定时段


		List<level3Elm> pluList = req.getRequest().getPluList();
		List<level2Elm> shopList = req.getRequest().getShopList();

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
	protected void processDUID(DCP_MinQtyTemplateCreateReq req, DCP_MinQtyTemplateCreateRes res) throws Exception {
		// TODO 自动生成的方法存根

		String templateId = getTemplateNO(req);
		String templateName = req.getRequest().getTemplateName();
		String memo = req.getRequest().getMemo();
		String status = req.getRequest().getStatus();

		String eId = req.geteId();
		String opNO = req.getOpNO();
		String opName = req.getOpName();
		// 获取当前时间
		String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String updateDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

		try {
			if (checkGuid(req,templateId) == false) {
				// 生成模板编码


				String[] columnsDcpMinQtyTemplateDetail = { "EID", "TEMPLATEID", "PLUNO", "MOQ", "LASTMODIOPID",
						"LASTMODIOPNAME", "UPDATE_TIME" };

				DataValue[] insValue = null;
				// DCP_MINQTYTEMPLATE_DETAIL 单身 (多条) 保存资料
				List<level3Elm> pluList = req.getRequest().getPluList();
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

				// DCP_MINQTYTEMPLATE 单头 保存资料
				List<level2Elm> shopList = req.getRequest().getShopList();
				String restrictShop = req.getRequest().getRestrictShop(); // 适用门店：0-所有门店1-指定门店2-排除门店
				String restrictChannel = req.getRequest().getRestrictChannel(); //适用渠道：0-所有渠道1-指定渠道2-排除渠道
				String restrictPeriod = req.getRequest().getRestrictPeriod(); //适用时段：0-所有时段1-指定时段

				String[] columnsDcpMinQtyTemplate = { "EID", "TEMPLATEID", "MEMO", "STATUS", "CREATEOPID",
						"CREATEOPNAME", "CREATETIME", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME",
						"RESTRICTSHOP","RESTRICTCHANNEL","RESTRICTPERIOD" };
				insValue = null;
				insValue = new DataValue[] { new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR), new DataValue(memo, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR), new DataValue(opNO, Types.VARCHAR),
						new DataValue(opName, Types.VARCHAR), new DataValue(createDate, Types.DATE),
						new DataValue(opNO, Types.VARCHAR), new DataValue(opName, Types.VARCHAR),
						new DataValue(createDate, Types.DATE), new DataValue(restrictShop, Types.INTEGER),
						new DataValue(restrictChannel, Types.INTEGER),
						new DataValue(restrictPeriod, Types.INTEGER)
				};
				InsBean ib2 = new InsBean("DCP_MINQTYTEMPLATE", columnsDcpMinQtyTemplate);
				ib2.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib2));

				// 如果指定门店适用 则添加 适用门店相关的资料
				if (restrictShop.equals("1")) {
					// 模板适用门店 DCP_MINQTYTEMPLATE_RANGE 保存资料
					String[] columnsDcpMinQtyTemplateRange = { "EID", "TEMPLATEID", "TEMPLATENAME","RANGETYPE","ID", "NAME", "LASTMODITIME" };
					insValue = null;
					for (level2Elm level2Elm : shopList) {
						insValue = new DataValue[] { new DataValue(eId, Types.VARCHAR),
								new DataValue(templateId, Types.VARCHAR),
								new DataValue(templateName, Types.VARCHAR),
								new DataValue(restrictShop, Types.INTEGER),
								new DataValue(level2Elm.getId(), Types.VARCHAR),
								new DataValue(level2Elm.getName(), Types.VARCHAR), new DataValue(createDate, Types.DATE) };
						InsBean ib3 = new InsBean("DCP_MINQTYTEMPLATE_RANGE", columnsDcpMinQtyTemplateRange);
						ib3.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib3));
					}

				}

				// 增加 DCP_MINQTYTEMPLATE_LANG 多语言表资料
				String[] columnsDcpMinQtyTemplateLang = { "EID", "TEMPLATEID", "LANG_TYPE", "TEMPLATENAME",
						"UPDATE_TIME" };
				insValue = null;
				insValue = new DataValue[] { new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR), new DataValue(req.getLangType(), Types.VARCHAR),
						new DataValue(templateName, Types.VARCHAR), new DataValue(updateDate, Types.VARCHAR) };
				InsBean ib4 = new InsBean("DCP_MINQTYTEMPLATE_LANG", columnsDcpMinQtyTemplateLang);
				ib4.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib4));

				this.doExecuteDataToDB();

			}else{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板已存在，请重新输入");
			}
			if (res.isSuccess()) {
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MinQtyTemplateCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MinQtyTemplateCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MinQtyTemplateCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected TypeToken<DCP_MinQtyTemplateCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MinQtyTemplateCreateReq>() {
		};
	}

	@Override
	protected DCP_MinQtyTemplateCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MinQtyTemplateCreateRes();
	}

	private boolean checkGuid(DCP_MinQtyTemplateCreateReq req,String templateId) throws Exception {
		String sql = null;

		boolean existGuid;
		String[] conditionValues = { templateId };
		sql = "select *  from DCP_MINQTYTEMPLATE  where TEMPLATEID = ? ";

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid = false;
		}
		return existGuid;
	}

	private String getTemplateNO(DCP_MinQtyTemplateCreateReq req){
		/*
		 * 模板编号在后台按规格生成QSL+日期时分秒（例:QSL20201113143806）
		 *
		 */
		String templateNo = "";
		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyyMMddHHmmss");
		templateNo = "QSL" + matter.format(dt);
		return templateNo;
	}

}
