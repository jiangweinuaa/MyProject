package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ScutKeyUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ScutKeyUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：ParaDefineUpdate
 *   說明：参数定义修改
 * 服务说明：参数定义修改
 * @author Jinzma 
 * @since  2017-03-03
 */
public class DCP_ScutKeyUpdate extends SPosAdvanceService<DCP_ScutKeyUpdateReq, DCP_ScutKeyUpdateRes> 
{
	@Override
	protected void processDUID(DCP_ScutKeyUpdateReq req, DCP_ScutKeyUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		try 
		{
			String eId=req.geteId();
			//新增新的单身（DCP_SHORTCUTKEY）
			if (req.getDatas() != null && req.getDatas().isEmpty() == false && req.getDatas().size() > 0)	
			{
				for (DCP_ScutKeyUpdateReq.level1Elm par : req.getDatas()) 
				{										
					
					//删除原有单身
					DelBean db1 = new DelBean("DCP_SHORTCUTKEY");
					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db1.addCondition("FUNCNO", new DataValue(par.getFuncNO(), Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1));
					
					//新增新的单身
					String[] columns = {"EID","FUNCNO","KEYSCAN","STATUS"};
					DataValue[] insValue = null ;
					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(par.getFuncNO(), Types.VARCHAR),
							new DataValue(par.getKeyScan(), Types.VARCHAR),								
							new DataValue("100", Types.VARCHAR)
					};
					InsBean ib = new InsBean("DCP_SHORTCUTKEY", columns);
					ib.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib));
				}
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
	protected List<InsBean> prepareInsertData(DCP_ScutKeyUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ScutKeyUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ScutKeyUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ScutKeyUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
//		StringBuffer errMsg = new StringBuffer("");
//		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
//		List<level1Elm> jsonDatas = req.getDatas();
//
//		for (level1Elm par : jsonDatas){ 
//			//keyName必须为数值型
//			String keyScan = par.getKeyScan();
//			if (!Check.Null(keyScan))
//			{
//				if (!PosPub.isNumeric(keyScan)) {
//					errCt++;
//					errMsg.append("快捷键码必须为数字, ");
//					isFail = true;
//				}
//			}
//			if (isFail){
//				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
//			}
//		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_ScutKeyUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ScutKeyUpdateReq>(){};
	}

	@Override
	protected DCP_ScutKeyUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ScutKeyUpdateRes();
	}




}
