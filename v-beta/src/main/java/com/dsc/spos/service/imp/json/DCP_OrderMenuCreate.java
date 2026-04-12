package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderMenuCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderMenuCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderMenuCreate extends SPosAdvanceService<DCP_OrderMenuCreateReq,DCP_OrderMenuCreateRes> {

	@Override
	protected void processDUID(DCP_OrderMenuCreateReq req, DCP_OrderMenuCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String menuName = req.getMenuName();
		String operateType = req.getOperateType();//0-新建空白菜单、1-复制已有菜单
		if(IsExistMenuName(req))
		{
			res.setSuccess(false);
			res.setMenuID("");
			res.setServiceStatus("100");
			res.setServiceDescription(menuName+"该菜单名称已经存在！");
			return;
		}
		
		if(operateType.equals("0"))
		{
			createNewMenu(req,res);
		}
		else if (operateType.equals("1")) 
		{
			createNewMenuByOriginMenu(req, res);		
	  }
		else 
		{
			res.setSuccess(false);
			res.setMenuID("");
			res.setServiceStatus("100");
			res.setServiceDescription("operateType="+operateType+" 该操作类型暂时不存在！");
		
	  }
				
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderMenuCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderMenuCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderMenuCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderMenuCreateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String menuName = req.getMenuName();
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
	protected TypeToken<DCP_OrderMenuCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderMenuCreateReq>(){};
	}

	@Override
	protected DCP_OrderMenuCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderMenuCreateRes();
	}
	
	/**
	 * 是否已经存在
	 * @param req
	 * @return
	 * @throws Exception
	 */
	private boolean IsExistMenuName(DCP_OrderMenuCreateReq req) throws Exception
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

	private void createNewMenu(DCP_OrderMenuCreateReq req, DCP_OrderMenuCreateRes res) throws Exception
	{
		try 
		{
			String eId = req.geteId();
			String menuName = req.getMenuName();
			String menuDescription = req.getMenuDescription();
			String menuMemo = req.getMenuMemo();
			String menuID= UUID.randomUUID().toString();
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
			
			
			String[] menu_columns = {
					"EID","MENUID","BELFIRM","MENUNAME","DESCRIPTION","MEMO","STATUS" 
			};		
			DataValue[]	insValue = new DataValue[]{
					new DataValue(eId, Types.VARCHAR), 					
					new DataValue(menuID, Types.VARCHAR), 
					new DataValue(belFirm, Types.VARCHAR),
					new DataValue(menuName, Types.VARCHAR),					
					new DataValue(menuDescription, Types.VARCHAR),
					new DataValue(menuMemo,Types.VARCHAR),			
					new DataValue("100", Types.VARCHAR)
			};
			InsBean menu_ib = new InsBean("OC_MENU", menu_columns);
			menu_ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(menu_ib)); 
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			res.setMenuID(menuID);
		
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setMenuID("");
			res.setServiceStatus("100");
			res.setServiceDescription(e.getMessage());
	
		}	
	}
	
	
	private void createNewMenuByOriginMenu(DCP_OrderMenuCreateReq req, DCP_OrderMenuCreateRes res) throws Exception
	{
		res.setSuccess(false);
		res.setMenuID("");
		res.setServiceStatus("100");
		res.setServiceDescription("代码还没开始写！");
		
	}
	
}
