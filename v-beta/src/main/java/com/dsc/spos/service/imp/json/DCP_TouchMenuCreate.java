package com.dsc.spos.service.imp.json;

import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TouchMenuCreateReq;
import com.dsc.spos.json.cust.req.DCP_TouchMenuCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_TouchMenuCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_TouchMenuCreateRes;

public class DCP_TouchMenuCreate extends SPosAdvanceService <DCP_TouchMenuCreateReq,DCP_TouchMenuCreateRes> {

	@Override
	protected void processDUID(DCP_TouchMenuCreateReq req, DCP_TouchMenuCreateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String menuNO= "";
		String menuID= req.getMenuID();
		String menuName= req.getMenuName();
		String workNO=req.getWorkNO();
		String posUse= req.getPosUse();
		String appUse= req.getAppUse();
		String padUse = req.getPadUse();
		String appletUse= req.getAppletUse();
		String lbDate= req.getLbDate();
		String leDate= req.getLeDate();
		String shopType= req.getShopType();	
		String status = req.getStatus();
		String prioprity=req.getPriority();
		String takeOutUse = req.getTakeOutUse();
		String mobileCashUse = req.getMobileCashUse();
		String touchMenu = req.getTouchMenu();
		
		if (Check.Null(prioprity) || PosPub.isNumeric(prioprity)==false) 
		{
			prioprity="1";
		}

		if (!Check.Null(touchMenu) && touchMenu.equals("Y"))
			touchMenu="1";
		else
			touchMenu="0";
		
		if (!Check.Null(posUse) && posUse.equals("Y"))
			posUse="1";
		else
			posUse="0";

		if (!Check.Null(padUse) && padUse.equals("Y"))
			padUse="1";
		else
			padUse="0";
		
		if (!Check.Null(appUse) && appUse.equals("Y"))
			appUse="1";
		else
			appUse="0";

		if ( !Check.Null(appletUse) && appletUse.equals("Y"))
			appletUse="1";
		else
			appletUse="0";

		if (!Check.Null(takeOutUse) &&  takeOutUse.equals("Y"))
			takeOutUse="1";
		else
			takeOutUse="0";
		
		if 	(Check.Null(mobileCashUse) || mobileCashUse.equals("N"))
			mobileCashUse="0";
		else
			mobileCashUse="1";

		try 
		{
			if (checkGuid(req) == false)
			{
				menuNO = getmenuNO(req);
				DataValue[] insValue = null;
				String[] columns1 = {
						"EID", "MENUNO","MENU_ID","MENUNAME","WORKNO", "POS_USE", 
						"APP_USE","APPLET_USE", "LBDATE", "LEDATE", "SHOPTYPE", "STATUS","PAD_USE","PRIORITY",
						"TAKEOUT_USE","MOBILECASH_USE","TOUCHMENU_USE"};
				
				//新增单身
				List<level1Elm> jsonDatas = req.getDatas();
				for (level1Elm par : jsonDatas) {
					String[] columns2 = {
							"EID", "MENUNO","CLASSNO","CLASSNAME","PRIORITY", "LBTIME", 
							"LETIME","STATUS","PICRATIO" };
					String classNO= par.getClassNO();
					String className= par.getClassName();
					String priority = par.getPriority();
					String lbTime=par.getLbTime();
					String leTime = par.getLeTime();
					String d_status = par.getStatus();
					String picRatio = par.getPicRatio();
					if(Check.Null(picRatio) || picRatio.isEmpty())
					{
						picRatio="1";
					}
					//新增子单身
					List<level2Elm> jsonDetailDatas = par.getGoods() ;
					for (level2Elm pardetail : jsonDetailDatas) {
						String[] columns3 = {
								"EID", "MENUNO","CLASSNO","PLUNO","TYPE","DISPNAME","UNIT","PRIORITY","STATUS"};
						String pluNO=pardetail.getPluNO();
						String dispName=pardetail.getDispName();
						String unitNO=pardetail.getUnitNO();
						String type=pardetail.getType();
						String d_priority = pardetail.getPriority();
						insValue = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(menuNO, Types.VARCHAR), 
								new DataValue(classNO, Types.VARCHAR),
								new DataValue(pluNO, Types.VARCHAR),	
								new DataValue(type, Types.VARCHAR),								
								new DataValue(dispName, Types.VARCHAR),
								new DataValue(unitNO, Types.VARCHAR),
								new DataValue(d_priority, Types.INTEGER),
								new DataValue("100", Types.VARCHAR)
						};
						InsBean ib3 = new InsBean("DCP_TOUCHMENU_CLASS_GOODS", columns3);
						ib3.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib3)); //			
					}

					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(menuNO, Types.VARCHAR), 
							new DataValue(classNO, Types.VARCHAR),
							new DataValue(className, Types.VARCHAR),								
							new DataValue(priority, Types.INTEGER),
							new DataValue(lbTime, Types.VARCHAR),
							new DataValue(leTime, Types.VARCHAR),
							new DataValue(d_status, Types.VARCHAR),
							new DataValue(picRatio,Types.INTEGER)
					};
					InsBean ib2 = new InsBean("DCP_TOUCHMENU_CLASS", columns2);
					ib2.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib2)); //			
				}

				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(menuNO, Types.VARCHAR), 
						new DataValue(menuID, Types.VARCHAR),
						new DataValue(menuName, Types.VARCHAR),								
						new DataValue(workNO, Types.VARCHAR),
						new DataValue(posUse, Types.INTEGER),
						new DataValue(appUse, Types.INTEGER),
						new DataValue(appletUse, Types.INTEGER),
						new DataValue(lbDate, Types.VARCHAR),
						new DataValue(leDate, Types.VARCHAR),
						new DataValue(shopType, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(padUse, Types.INTEGER),
						new DataValue(prioprity, Types.INTEGER),
						new DataValue(takeOutUse, Types.INTEGER),
						new DataValue(mobileCashUse, Types.INTEGER),
						new DataValue(touchMenu, Types.INTEGER)
				};
				InsBean ib1 = new InsBean("DCP_TOUCHMENU", columns1);
				ib1.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib1)); //	
				this.doExecuteDataToDB();
			}

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
	protected List<InsBean> prepareInsertData(DCP_TouchMenuCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TouchMenuCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TouchMenuCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TouchMenuCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String menuID =req.getMenuID();	
		String menuName =req.getMenuName();	
		String posUse =req.getPosUse();
		String appUse =req.getAppUse();
		String appletUse =req.getAppletUse();
		String lbDate =req.getLbDate();
		String leDate =req.getLeDate();
		String shopType =req.getShopType();
		String status =req.getStatus();
		List<level1Elm> datas = req.getDatas();

		if (Check.Null(menuID)) 
		{
			errMsg.append("菜单ID不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(menuName)) 
		{
			errMsg.append("菜单名称不可为空值, ");
			isFail = true;
		} 
//		if (Check.Null(posUse) || Check.Null(appUse) || Check.Null(appletUse) ) 
//		{
//			errMsg.append("菜单适用范围不可为空值, ");
//			isFail = true;
//		} 

		if (Check.Null(lbDate) || Check.Null(leDate) ) 
		{
			errMsg.append("日期不可为空值, ");
			isFail = true;
		}

		if (Check.Null(shopType) ) 
		{
			errMsg.append("门店类型不可为空值, ");
			isFail = true;
		}

		if (Check.Null(status) ) 
		{
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (level1Elm par : datas) 
		{			
			if (Check.Null(par.getClassNO())) 
			{
				errMsg.append("类别编号不可为空值, ");
				isFail = true;
			}	

			if (Check.Null(par.getClassName())) 
			{
				errMsg.append("类别名称不可为空值, ");
				isFail = true;
			}	
			else 
			{
        if (par.getClassName().length()>2000)
        {
        	errMsg.append("类别名称不能超过2000个字符, ");
  				isFail = true;
        }
			}

			if (Check.Null(par.getPriority()) || !PosPub.isNumeric(par.getPriority()) ) 
			{
				errMsg.append("类别优先级不可为空值或非数值, ");
				isFail = true;
			}	

			if (Check.Null(par.getLbTime()) || Check.Null(par.getLeTime())) 
			{
				errMsg.append("时间不可为空值, ");
				isFail = true;
			}

			if (Check.Null(par.getStatus()) ) 
			{
				errMsg.append("状态不可为空值, ");
				isFail = true;
			}

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}		

			List<level2Elm> goods = par.getGoods();
			for (level2Elm par2 : goods) 
			{
				if (Check.Null(par2.getPluNO())) 
				{
					errMsg.append("商品编号不可为空值, ");
					isFail = true;
				}

				if (Check.Null(par2.getDispName())) 
				{
					errMsg.append("商品简称不可为空值, ");
					isFail = true;
				}
				else
				{
					 if (par2.getDispName().length()>2000)
		        {
		        	errMsg.append("商品简称不能超过2000个字符, ");
		  				isFail = true;
		        }
				}

				if (Check.Null(par2.getType())) 
				{
					errMsg.append("类型不可为空值, ");
					isFail = true;
				}

				//				if (Check.Null(par2.getUnitNO())) 
				//				{
				//					errMsg.append("单位不可为空值, ");
				//					isFail = true;
				//				}

				if (Check.Null(par2.getPriority()) || !PosPub.isNumeric(par2.getPriority()) ) 
				{
					errMsg.append("商品优先级不可为空值或非数值, ");
					isFail = true;
				}


				if (isFail)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				}	
			}
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_TouchMenuCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TouchMenuCreateReq>(){} ;
	}

	@Override
	protected DCP_TouchMenuCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TouchMenuCreateRes() ;
	}

	private boolean checkGuid(DCP_TouchMenuCreateReq req) throws Exception {
		String sql = null;
		String guid = req.getMenuID();
		boolean existGuid;
		String[] conditionValues = { guid }; 		
		sql = "select *  from DCP_TOUCHMENU  where MENU_ID = ? ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}
		return existGuid;
	}

	private String getmenuNO(DCP_TouchMenuCreateReq req) throws Exception {

		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
		String sql = null;
		String menuNO = null;
		String eId = req.geteId();
		StringBuffer sqlbuf = new StringBuffer("");
		String[] conditionValues = {eId}; // 

		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd");
		menuNO = "CPCD" + matter.format(dt);

		sqlbuf.append("" + "select MENUNO  from ( " + "select max(MENUNO) as  MENUNO "
				+ "  from DCP_TOUCHMENU " + " where  EID = ? "
				+ " and MENUNO like '%%" + menuNO + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {

			menuNO = (String) getQData.get(0).get("MENUNO");

			if (menuNO != null && menuNO.length() > 0) {
				long i;
				menuNO = menuNO.substring(4, menuNO.length());
				i = Long.parseLong(menuNO) + 1;
				menuNO = i + "";
				menuNO = "CPCD" + menuNO;
			} else {
				menuNO = "CPCD" + matter.format(dt) + "00001";
			}
		} else {
			menuNO = "CPCD" + matter.format(dt) + "00001";
		}

		return menuNO;


	}






}
