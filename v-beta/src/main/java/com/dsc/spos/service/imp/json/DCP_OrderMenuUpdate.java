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
import com.dsc.spos.json.cust.req.DCP_OrderMenuUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderMenuUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderMenuUpdate extends SPosAdvanceService<DCP_OrderMenuUpdateReq,DCP_OrderMenuUpdateRes> {

	@Override
	protected void processDUID(DCP_OrderMenuUpdateReq req, DCP_OrderMenuUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String menuName = req.getMenuName();
		if(IsExistMenuName(req))
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription(menuName+"该菜单名称已经存在，请重新修改！");
			return;
		}
		try 
		{
			String menuDescription = req.getMenuDescription();
			String menuMemo = req.getMenuMemo();
			String menuID= req.getMenuID();
			String belFirm ="";
			
			if(req.getOrg_Form().equals("0"))
			{
				belFirm = req.getOrganizationNO();
			}
			else if(req.getOrg_Form().equals("2"))
			{
				belFirm = req.getBELFIRM();
			}
			
			if(belFirm==null||belFirm.isEmpty())
			{
				belFirm=" ";//默认空格
			}
			
			
			UptBean ub1 = new UptBean("OC_MENU");

			ub1.addUpdateValue("MENUNAME", new DataValue(menuName, Types.VARCHAR));
			ub1.addUpdateValue("DESCRIPTION", new DataValue(menuDescription, Types.VARCHAR));
			ub1.addUpdateValue("MEMO", new DataValue(menuMemo, Types.VARCHAR));
			ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			
			ub1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			ub1.addCondition("MENUID", new DataValue(menuID,Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(ub1));
			
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription(e.getMessage());
	
		}
		
		
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderMenuUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderMenuUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderMenuUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderMenuUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String menuName = req.getMenuName();
		if(Check.Null(req.getMenuID()))
		{
			errMsg.append("菜单ID不可为空值, ");
			isFail = true;
		}
		if(Check.Null(menuName))
		{
			errMsg.append("菜单名称不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	  return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderMenuUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderMenuUpdateReq>(){};
	}

	@Override
	protected DCP_OrderMenuUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderMenuUpdateRes();
	}
	
	/**
	 * 是否已经存在
	 * @param req
	 * @return
	 * @throws Exception
	 */
	private boolean IsExistMenuName(DCP_OrderMenuUpdateReq req) throws Exception
	{
		String eId = req.geteId();
		String menuName = req.getMenuName();
		String sql = "select * from OC_menu where EID='"+eId+"' and  menuname='"+menuName+"'";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if(getQData!=null&&getQData.isEmpty()==false)
		{
			return true;
		}
		
		return false;
	}

}
