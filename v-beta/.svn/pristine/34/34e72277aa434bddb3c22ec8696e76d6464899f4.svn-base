package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TouchMenuDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TouchMenuDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.util.List;

public class DCP_TouchMenuDelete extends SPosAdvanceService<DCP_TouchMenuDeleteReq,DCP_TouchMenuDeleteRes >{

	@Override
	protected void processDUID(DCP_TouchMenuDeleteReq req, DCP_TouchMenuDeleteRes res) throws Exception {
	// TODO 自动生成的方法存根
		String eId = req.geteId();
        List<DCP_TouchMenuDeleteReq.level2Elm> menuList = req.getRequest().getMenuList();

        try
		{
            for (DCP_TouchMenuDeleteReq.level2Elm level2Elm : menuList) {
                String menuNO = level2Elm.getMenuNo();

                //DCP_TOUCHMENU
                DelBean db1 = new DelBean("DCP_TOUCHMENU");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("MENUNO", new DataValue(menuNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //DCP_TOUCHMENU_CLASS
                DelBean db2 = new DelBean("DCP_TOUCHMENU_CLASS");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("MENUNO", new DataValue(menuNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //DCP_TOUCHMENU_CLASS_GOODS
                DelBean db3 = new DelBean("DCP_TOUCHMENU_CLASS_GOODS");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("MENUNO", new DataValue(menuNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));

                //DCP_TOUCHMENU_RANGE
                DelBean db4 = new DelBean("DCP_TOUCHMENU_RANGE");
                db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db4.addCondition("MENUNO", new DataValue(menuNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db4));

                //DCP_TOUCHMENU_LANG
                DelBean db5 = new DelBean("DCP_TOUCHMENU_LANG");
                db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db5.addCondition("MENUNO", new DataValue(menuNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db5));

                //DCP_TOUCHMENU_CLASS_GOODS_LANG
                DelBean db6 = new DelBean("DCP_TOUCHMENU_CLASS_GOODS_LANG");
                db6.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db6.addCondition("MENUNO", new DataValue(menuNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db6));

                //DCP_TOUCHMENU_CLASS_LANG
                DelBean db7 = new DelBean("DCP_TOUCHMENU_CLASS_LANG");
                db7.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db7.addCondition("MENUNO", new DataValue(menuNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db7));
            }
			
			this.doExecuteDataToDB();	
			
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TouchMenuDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TouchMenuDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TouchMenuDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TouchMenuDeleteReq req) throws Exception {
	// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
        List<DCP_TouchMenuDeleteReq.level2Elm> menuList = req.getRequest().getMenuList();
        if (CollectionUtils.isEmpty(menuList)) {
			isFail = true;
			errMsg.append("菜单编码不可为空值, ");
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_TouchMenuDeleteReq> getRequestType() {
	// TODO 自动生成的方法存根
		return new TypeToken<DCP_TouchMenuDeleteReq>(){};
	}

	@Override
	protected DCP_TouchMenuDeleteRes getResponseType() {
	// TODO 自动生成的方法存根
		return new DCP_TouchMenuDeleteRes();
	}

}
