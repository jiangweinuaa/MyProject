package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_GoodsFlavorUpdateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsFlavorUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 更新商品口味信息
 * @author yuanyy
 *
 */
public class DCP_GoodsFlavorUpdate extends SPosAdvanceService<DCP_GoodsFlavorUpdateReq, DCP_GoodsFlavorUpdateRes> {

	@Override
	protected void processDUID(DCP_GoodsFlavorUpdateReq req, DCP_GoodsFlavorUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			String flavorNO = req.getRequest().getFlavorNo();
			String flavorName = req.getRequest().getFlavorName();
			String priority = req.getRequest().getPriority();
			String status = req.getRequest().getStatus();
			String eId = req.geteId();
			String toPriority = req.getRequest().getToPriority();

			if(!Check.Null(toPriority)){
				int toPt = Integer.parseInt(toPriority);
				int pt = Integer.parseInt(priority);

				if(toPt != pt ){
					UptBean ub2 = null;	
					ub2 = new UptBean("DCP_FLAVOR");
					///// 如果当前优先级序号 priority = toPriority ,前端需要给出提示信息：“ 已处于第N行 ”
					if(toPt > pt){  //相当 于   下移

						String[] priorityList = new String[toPt-pt];
						for(int i=0; i < toPt-pt ; i++) {
							priorityList[i] = String.valueOf(pt +i+ 1);
						}
						String str1 = StringUtils.join(priorityList,",");
						//add Value
						ub2.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.SubSelf));
						//condition
						ub2.addCondition("PRIORITY", new DataValue(str1, Types.VARCHAR,DataExpression.IN));
					}
					else{  //  上移

						String[] priorityList = new String[pt-toPt];
						for(int i=0; i < pt- toPt ; i++) {
							priorityList[i] = String.valueOf(toPt + i);
						}
						String str1 = StringUtils.join(priorityList,",");

						//add Value
						ub2.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.UpdateSelf));
						//condition
						ub2.addCondition("PRIORITY", new DataValue(str1, Types.VARCHAR,DataExpression.IN));
					}
					ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
					this.addProcessData(new DataProcessBean(ub2));
				}

				UptBean ub3 = null;	
				ub3 = new UptBean("DCP_FLAVOR");
				//add Value
				ub3.addUpdateValue("PRIORITY", new DataValue(toPriority, Types.VARCHAR));
				ub3.addUpdateValue("FLAVORNAME", new DataValue(flavorName, Types.VARCHAR));
				ub3.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));

				//condition
				ub3.addCondition("FLAVORNO", new DataValue(flavorNO, Types.VARCHAR));
				ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));		

				this.addProcessData(new DataProcessBean(ub3));
			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsFlavorUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsFlavorUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsFlavorUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsFlavorUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String flavorNO = req.getRequest().getFlavorNo();

		if(Check.Null(flavorNO)){
			errMsg.append("口味编码不能为空值 ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsFlavorUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsFlavorUpdateReq>(){};
	}

	@Override
	protected DCP_GoodsFlavorUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsFlavorUpdateRes();
	}

}
