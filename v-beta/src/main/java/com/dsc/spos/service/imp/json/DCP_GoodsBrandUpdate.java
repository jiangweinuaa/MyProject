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
import com.dsc.spos.json.cust.req.DCP_GoodsBrandUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsBrandUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsBrandUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 修改商品品牌 2018-10-18 
 * @author yuanyy 
 *
 */
public class DCP_GoodsBrandUpdate extends SPosAdvanceService<DCP_GoodsBrandUpdateReq, DCP_GoodsBrandUpdateRes> {

	@Override
	protected void processDUID(DCP_GoodsBrandUpdateReq req, DCP_GoodsBrandUpdateRes res) throws Exception {
		// TODO Auto-generated method stub

		String eId = req.geteId();
		String brandNo = req.getRequest().getBrandNo();
		String status = req.getRequest().getStatus();
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}

		List<level1Elm> getLangDatas = req.getRequest().getBrandName_lang();

		DelBean db2 = new DelBean("DCP_BRAND_LANG");
		db2.addCondition("BRANDNO", new DataValue(brandNo, Types.VARCHAR));
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2));


		if(getLangDatas != null && !getLangDatas.isEmpty()){
			for(level1Elm oneLv1: getLangDatas){

				String[] columnsName = {
						"BRANDNO","LANG_TYPE","BRAND_NAME","EID","LASTMODITIME"
				};

				String langType = oneLv1.getLangType();
				String brandName = oneLv1.getName();


				DataValue[] insValueDetail = new DataValue[] 
						{
								new DataValue(brandNo, Types.VARCHAR),
								new DataValue(langType, Types.VARCHAR),
								new DataValue(brandName, Types.VARCHAR),								
								new DataValue(eId, Types.VARCHAR),								
								new DataValue(lastmoditime, Types.DATE)

						};

				InsBean ib2 = new InsBean("DCP_BRAND_LANG", columnsName);
				ib2.addValues(insValueDetail);
				this.addProcessData(new DataProcessBean(ib2));	


			}
		}


		UptBean ub1 = null;	
		ub1 = new UptBean("DCP_BRAND");
		//add Value
		ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
		//condition
		ub1.addCondition("BRANDNO", new DataValue(brandNo, Types.VARCHAR));
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
		this.addProcessData(new DataProcessBean(ub1));
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsBrandUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsBrandUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsBrandUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsBrandUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String brandNO = req.getRequest().getBrandNo();

		if(Check.Null(brandNO)){
			errMsg.append("编码不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_GoodsBrandUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsBrandUpdateReq>(){};
	}

	@Override
	protected DCP_GoodsBrandUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsBrandUpdateRes();
	}

	/**
	 * 验证多语言信息是否重复 
	 * @param brandNO
	 * @param eId
	 * @param langType
	 * @return
	 */
	private String isRepeatLang(String brandNO, String eId, String langType ){
		String sql = null;
		sql = "SELECT * FROM DCP_BRAND_LANG WHERE "
				+ " BRANDNO = '"+brandNO+"' "
				+ " and EID = '"+eId+"' "
				+ " and lang_Type = '"+langType+"'" ;
		return sql;
	}


}
