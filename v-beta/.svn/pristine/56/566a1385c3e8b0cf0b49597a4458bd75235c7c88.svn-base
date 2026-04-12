package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CardReaderTypeUpdateReq;
import com.dsc.spos.json.cust.req.DCP_TicketStyleUpdateReq;
import com.dsc.spos.json.cust.req.DCP_CardReaderTypeUpdateReq.level2ELm;
import com.dsc.spos.json.cust.res.DCP_CardReaderTypeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_CardReaderTypeUpdate
 * 服务说明：读卡器类型修改
 * @author wangzyc 
 * @since  2020-12-8
 */
public class DCP_CardReaderTypeUpdate extends SPosAdvanceService<DCP_CardReaderTypeUpdateReq, DCP_CardReaderTypeUpdateRes>{

	@Override
	protected void processDUID(DCP_CardReaderTypeUpdateReq req, DCP_CardReaderTypeUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		List<level2ELm> readerTypeList = req.getRequest().getReaderTypeList();
		
		try{
			if(readerTypeList.isEmpty()==false && readerTypeList.size()>0){
				for (level2ELm level2eLm : readerTypeList) {
					
					// 修改DCP_CARDREADERTYPE 读卡器类型
					UptBean ub1 = null;	
					ub1 = new UptBean("DCP_CARDREADERTYPE");
					
					ub1.addUpdateValue("READERTYPE", new DataValue(level2eLm.getReaderType(), Types.VARCHAR));
					ub1.addUpdateValue("READERNAME", new DataValue(level2eLm.getReaderName(), Types.VARCHAR));
					ub1.addUpdateValue("MEDIATYPE", new DataValue(level2eLm.getMediaType(), Types.VARCHAR));
					ub1.addUpdateValue("BAUD", new DataValue(level2eLm.getBaud(), Types.VARCHAR));
					ub1.addUpdateValue("PORT", new DataValue(level2eLm.getPort(), Types.VARCHAR));
					ub1.addUpdateValue("SORTID", new DataValue(level2eLm.getSortId(), Types.VARCHAR));
					ub1.addUpdateValue("MEMO", new DataValue(level2eLm.getMemo(), Types.VARCHAR));
					ub1.addUpdateValue("STATUS", new DataValue(level2eLm.getStatus(), Types.VARCHAR));
				
					// condition
					ub1.addCondition("READERTYPE", new DataValue(level2eLm.getReaderType(), Types.VARCHAR));

					this.addProcessData(new DataProcessBean(ub1));
				}
			}
			this.doExecuteDataToDB();
		}
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());	
		}
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CardReaderTypeUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CardReaderTypeUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CardReaderTypeUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CardReaderTypeUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		//必传值不为空
		List<level2ELm> readerTypeList = req.getRequest().getReaderTypeList();
		if(readerTypeList.size()>0&&readerTypeList.isEmpty() == false){
			for (level2ELm level2eLm : readerTypeList) {
				if(Check.Null(level2eLm.getReaderType())){
					errMsg.append("读卡器编号不可为空值, ");
					isFail = true;
				}
				if(Check.Null(level2eLm.getReaderName())){
					errMsg.append("读卡器名称不可为空值, ");
					isFail = true;
				}
				if(Check.Null(level2eLm.getMediaType())){
					errMsg.append("介质类型不可为空值, ");
					isFail = true;
				}
				if(Check.Null(level2eLm.getSortId())){
					errMsg.append("显示序号不可为空值, ");
					isFail = true;
				}
				if(Check.Null(level2eLm.getStatus())){
					errMsg.append("是否有效不可为空值, ");
					isFail = true;
				}
			}
		}
		else{
			errMsg.append("读卡器不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_CardReaderTypeUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_CardReaderTypeUpdateReq>(){};
	}

	@Override
	protected DCP_CardReaderTypeUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_CardReaderTypeUpdateRes();
	}
	
	
}
