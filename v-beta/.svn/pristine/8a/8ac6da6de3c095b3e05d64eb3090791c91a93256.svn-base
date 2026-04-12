package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_LangLabelCreateReq;
import com.dsc.spos.json.cust.res.DCP_LangLabelCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 多语言标签新增 
 * @author yuanyy 2019-01-17 
 *
 */
public class DCP_LangLabelCreate extends SPosAdvanceService<DCP_LangLabelCreateReq, DCP_LangLabelCreateRes> {

	@Override
	protected void processDUID(DCP_LangLabelCreateReq req, DCP_LangLabelCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null; 
		try 
		{			
			String labelID = req.getRequest().getLabelID();
			String label_cn = req.getRequest().getLabel_cn();
			String label_tw = req.getRequest().getLabel_tw();
			String label_en = req.getRequest().getLabel_en();

			String opNO = req.getOpNO();

			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = df.format(cal.getTime());

			sql = this.isRepeat(labelID);
			List<Map<String, Object>> langLabelDatas = this.doQueryData(sql, null);
			if(langLabelDatas.isEmpty()){
				String[] columns1 = { "LABELID", "LABEL_CN", "LABEL_TW", "LABEL_EN", "CREATEOPID" ,"CREATETIME"};
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[] { 
						new DataValue(labelID, Types.VARCHAR), 
						new DataValue(label_cn, Types.VARCHAR),
						new DataValue(label_tw, Types.VARCHAR), 
						new DataValue(label_en, Types.VARCHAR),
						new DataValue(opNO, Types.VARCHAR) ,
						new DataValue(createTime, Types.VARCHAR) 
				};

				InsBean ib1 = new InsBean("PLATFORM_LANG_LABEL", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

				this.doExecuteDataToDB();	

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"编码 为 '"+labelID+" 的 多语言标签 已存在");
			}

		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_LangLabelCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_LangLabelCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_LangLabelCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_LangLabelCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String labelID = req.getRequest().getLabelID();
		String label_cn = req.getRequest().getLabel_cn();
		String label_tw = req.getRequest().getLabel_tw();
		String label_en = req.getRequest().getLabel_en();

		if (Check.Null(labelID)) 
		{
			errMsg.append("多语言标签编码不能为空值 ");
			isFail = true;
		}

		if( Check.Null(label_cn) && Check.Null(label_tw) && Check.Null(label_en)){
			errMsg.append("多语言标签不能同时为空值 ，请至少添加一条多语言信息");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_LangLabelCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_LangLabelCreateReq>(){};
	}

	@Override
	protected DCP_LangLabelCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_LangLabelCreateRes();
	}

	/**
	 * 验证多语言标签是否已经存在
	 * @param labelID
	 * @return
	 */
	private String isRepeat(String labelID ){
		String sql = null;
		sql = "select * from PLATFORM_LANG_LABEL where LABELID = '"+labelID+"'" ;
		return sql;
	}

}
