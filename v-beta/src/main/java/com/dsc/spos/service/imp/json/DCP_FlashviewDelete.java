package com.dsc.spos.service.imp.json;

import java.io.File;
import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FlashviewDeleteReq;
import com.dsc.spos.json.cust.res.DCP_FlashviewDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 轮播广告
 * @author yuanyy
 *
 */
public class DCP_FlashviewDelete extends SPosAdvanceService<DCP_FlashviewDeleteReq, DCP_FlashviewDeleteRes> {

	@Override
	protected void processDUID(DCP_FlashviewDeleteReq req, DCP_FlashviewDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			
			String flashviewId = req.getRequest().getFlashviewId();
			String eId = req.geteId();
			String priority = req.getRequest().getPriority();
			String fileName = req.getRequest().getFileName();
			
			DelBean db2 = new DelBean("DCP_FLASHVIEW_SHOP");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("FLASHVIEWID", new DataValue(flashviewId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			DelBean db1 = new DelBean("DCP_FLASHVIEW");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("FLASHVIEWID", new DataValue(flashviewId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			//删除后 ， 需要把大于当前行优先级的其他广告， 优先级全部 减一。
			UptBean ub = new UptBean("DCP_FLASHVIEW");
			ub.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.SubSelf));
			// condition
			ub.addCondition("PRIORITY", new DataValue(priority, Types.VARCHAR,DataExpression.GreaterEQ));
			ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub));
			
			try
			{
				if(Check.Null(fileName)==false)
				{
					String dirpath= System.getProperty("catalina.home")+"\\webapps\\flashviewImg";
					
					File file =new File(dirpath +"\\" + fileName);    
					if(file.exists())
					{
						file.delete();
					}				
					file=null;
				}				
			}
			catch (Exception ex)
			{
				
			}
			
			
			this.doExecuteDataToDB();
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常！");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_FlashviewDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FlashviewDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FlashviewDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FlashviewDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		String flashviewId = req.getRequest().getFlashviewId();
		
		if (Check.Null(flashviewId)) {
			errCt++;
			errMsg.append("广告编号不可为空值, ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_FlashviewDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlashviewDeleteReq>(){};
	}

	@Override
	protected DCP_FlashviewDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_FlashviewDeleteRes();
	}
	
}
