package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PackChargeUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PackChargeUpdateReq.Goods;
import com.dsc.spos.json.cust.res.DCP_PackChargeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.collection.CollectionUtil;

public class DCP_PackChargeUpdate extends SPosAdvanceService<DCP_PackChargeUpdateReq, DCP_PackChargeUpdateRes> {
	@Override
	protected void processDUID(DCP_PackChargeUpdateReq req, DCP_PackChargeUpdateRes res) throws Exception {
		
		String eId = req.geteId();
		String packPluNo = req.getRequest().getPackPluNo();
		
		UptBean uptBean = new UptBean("DCP_PACKCHARGE");
		uptBean.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		uptBean.addCondition("PACKPLUNO", new DataValue(packPluNo, Types.VARCHAR));
		uptBean.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(), Types.VARCHAR));
//		uptBean.addUpdateValue("REDISUPDATESUCCESS", req.getRequest());
		uptBean.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
		uptBean.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
		uptBean.addUpdateValue("LASTMODITIME", new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));
		uptBean.addUpdateValue("PACKBAGNUM", new DataValue(req.getRequest().getPackBagNum(), Types.VARCHAR));
		uptBean.addUpdateValue("PACKPLUTYPE", new DataValue(req.getRequest().getPackPluType(), Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(uptBean));
		
		if(CollectionUtil.isNotEmpty(req.getRequest().getGoodsList())) {
			
			DelBean delBean = new DelBean("DCP_PACKCHARGE_GOODS");
			delBean.addCondition("PACKPLUNO", new DataValue(packPluNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(delBean));
			
			for (Goods goods : req.getRequest().getGoodsList()) {
				String[] columns = {"EID", "PLUNO", "UNITID", "PACKPLUNO"};
				DataValue[] insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(goods.getPluNo(), Types.VARCHAR),
						new DataValue(goods.getUnitId(), Types.VARCHAR),
						new DataValue(packPluNo, Types.VARCHAR),
				};
				InsBean insBean = new InsBean("DCP_PACKCHARGE_GOODS", columns);
				insBean.addValues(insValue);
				this.addProcessData(new DataProcessBean(insBean));
			}
		}
		
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PackChargeUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PackChargeUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PackChargeUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PackChargeUpdateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String packPluNo = req.getRequest().getPackPluNo();
		if (Check.Null(packPluNo)) {
			errMsg.append("打包商品编码packPluNo不能为空值， ");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PackChargeUpdateReq> getRequestType() {
		return new TypeToken<DCP_PackChargeUpdateReq>() {};
	}

	@Override
	protected DCP_PackChargeUpdateRes getResponseType() {
		return new DCP_PackChargeUpdateRes();
	}
}
