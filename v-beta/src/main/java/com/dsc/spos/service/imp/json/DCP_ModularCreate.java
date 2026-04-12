package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_ModularCreateReq;
import com.dsc.spos.json.cust.req.DCP_ModularCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ModularCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_ModularCreate extends SPosAdvanceService<DCP_ModularCreateReq,DCP_ModularCreateRes>
{

	@Override
	protected void processDUID(DCP_ModularCreateReq req, DCP_ModularCreateRes res) throws Exception {
		// TODO Auto-generated method stub

		String shopId = req.getShopId();
		String eId = req.geteId();
		String sql = null;
		sql = this.getModularGuid_SQL();
		level1Elm request = req.getRequest();

		String[] conditionValues_checkGuid = {eId,request.getModularID().toUpperCase() }; //查詢條件
		List<Map<String, Object>> getQData_checkGuid = this.doQueryData(sql,conditionValues_checkGuid);
		//GUID存在否判断
		if(getQData_checkGuid==null || getQData_checkGuid.isEmpty())
		{			
			List<DCP_ModularCreateReq.level2Elm> jsonDatas = request.getDatas();
			//前台页面不是一次新增多笔菜单编码，
			//request规格设计成多笔其实是不合理的
			for (DCP_ModularCreateReq.level2Elm level2Elm : jsonDatas) 
			{				
				String sqlModularNo=getModularNO_SQL();

				String[] conditionValues_checkNO = {eId,level2Elm.getModularNo()}; //查詢條件
				List<Map<String, Object>> getQData_checkNO = this.doQueryData(sqlModularNo,conditionValues_checkNO);
				if(getQData_checkNO==null || getQData_checkNO.isEmpty())
				{					
					//2018-11-07 yyy 新增菜单时priority 顺序处理逻辑
					String modularLevel = level2Elm.getModularLevel();
					String upperModular = level2Elm.getUpperModular();
					String getMaxPrioritySql = this.getMaxPriority(modularLevel, eId, upperModular);
					String[] conditions = {}; //查詢條件
					List<Map<String, Object>> maxPriorityList = this.doQueryData(getMaxPrioritySql,conditions);
					String maxPriority = "1";
					if(maxPriorityList !=null &&  !maxPriorityList.isEmpty())
					{					
						maxPriority = maxPriorityList.get(0).get("PRIORITY").toString();
					}
					else{
						//不会出现modularLevel 为空的情况
					}

					UptBean ub1 = null;	
					ub1 = new UptBean("DCP_MODULAR");
					//add Value
					ub1.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.UpdateSelf));
					//condition
					ub1.addCondition("PRIORITY", new DataValue(maxPriority, Types.VARCHAR,DataExpression.GreaterEQ));
					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
					ub1.addCondition("MODULARLEVEL", new DataValue(modularLevel, Types.VARCHAR));		
					ub1.addCondition("UPPERMODULAR", new DataValue(upperModular, Types.VARCHAR));	
					this.addProcessData(new DataProcessBean(ub1));

					//DCP_MODULAR
					String[] columnsModular = {
							"MODULARNO",
							"MODULARLEVEL",
							"UPPERMODULAR",
							"CHSMSG",
							"CHTMSG",
							"ENGMSG",
							"FTYPE",
							"PRONAME",
							"PARAMETER",
							"STATUS",
							"EID",
							"STYPE",
							"MODULAR_ID",
							"PRIORITY",
							"RFUNCNO",
							"RPATTERN",
							"REGEDISTREX",
							"ONSALE",
							"UPDATE_TIME"
					};
					DataValue[] insValue1 = null;

					//单身明细DCP_MODULAR_FUNCTION
					List<DCP_ModularCreateReq.level3Elm> level3=level2Elm.getDatas();
					for (DCP_ModularCreateReq.level3Elm level3Elm : level3) 
					{
						String sqlExist = null;
						sqlExist = this.getModular_FunctionExist();
						String[] conditionValues_sqlExist = {eId,level3Elm.getFuncNo()}; //查詢條件
						List<Map<String, Object>> getQData_sqlExist = this.doQueryData(sqlExist,conditionValues_sqlExist);
						//Function功能编码不能重复
						if(getQData_sqlExist==null || getQData_sqlExist.isEmpty())
						{
							int insColCt = 0;
							String[] columnsModularDetail ={"FUNCNO","MODULARNO","FTYPE","PRONAME","PARAMETER","CHSMSG","CHTMSG","ENGMSG","STATUS","EID",
									"ONSALE","UPDATE_TIME"};
							DataValue[] columnsVal = new DataValue[columnsModularDetail.length];
							for (int i = 0; i < columnsVal.length; i++)
							{
								String keyVal = null;
								switch (i) 
								{
								case 0:
									keyVal=level3Elm.getFuncNo();
									break;
								case 1:
									keyVal=level2Elm.getModularNo();
									break;
								case 2:
									keyVal=level3Elm.getFuncFType();
									break;
								case 3:
									keyVal=level3Elm.getFuncProName();
									break;
								case 4:
									keyVal=level3Elm.getFuncParameter();
									break;
								case 5:
									keyVal=level3Elm.getFuncName();
									break;
								case 6:
									keyVal=level3Elm.getFuncNameCht();
									break;
								case 7:
									keyVal=level3Elm.getFuncNameEng();
									break;
								case 8:
									keyVal=level3Elm.getFuncStatus();
									break;
								case 9:
									keyVal=eId;
									break;
								case 10:
									keyVal=level3Elm.getOnSale();
									break;
								case 11:
									keyVal=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
									break;
								}

								if (keyVal != null) 
								{
									insColCt++;
									if (i == 2) 
									{
										columnsVal[i] = new DataValue(keyVal, Types.DECIMAL);
									} 
									else 
									{
										columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
									}
								} 
								else 
								{
									columnsVal[i] = null;
								}

							}

							String[] columns2 = new String[insColCt];
							DataValue[] insValue2 = new DataValue[insColCt];

							insColCt = 0;

							for (int i = 0; i < columnsVal.length; i++) 
							{
								if (columnsVal[i] != null) 
								{
									columns2[insColCt] = columnsModularDetail[i];
									insValue2[insColCt] = columnsVal[i];
									insColCt++;
									if (insColCt >= insValue2.length)
										break;
								}
							}

							InsBean ib2 = new InsBean("DCP_MODULAR_FUNCTION", columns2);
							ib2.addValues(insValue2);
							this.addProcessData(new DataProcessBean(ib2));	

						}
						else
						{
							res.setSuccess(false);
							res.setServiceDescription("功能编码"+level3Elm.getFuncNo()+"不能重复");

							res.setDatas(new ArrayList<DCP_ModularCreateRes.level2Elm>());

							DCP_ModularCreateRes.level2Elm oneLv2 =  res.new level2Elm();
							oneLv2.setMODULARNO("");
							res.getDatas().add(oneLv2);
							return;
						}

					}


					String REGEDISTREX = PosPub.encodeMD5(level2Elm.getModularNo() + level2Elm.getRFUNCNO() + "DIGIWIN");

					insValue1 = new DataValue[] 
							{ 
									new DataValue(level2Elm.getModularNo(), Types.VARCHAR),
									new DataValue(level2Elm.getModularLevel(), Types.DECIMAL), 
									new DataValue(level2Elm.getUpperModular(), Types.VARCHAR),
									new DataValue(level2Elm.getModularName(), Types.VARCHAR), 
									new DataValue(level2Elm.getModularNameCht(), Types.VARCHAR),
									new DataValue(level2Elm.getModularNameEng(), Types.VARCHAR),
									new DataValue(level2Elm.getfType(), Types.VARCHAR), 
									new DataValue(level2Elm.getProName(), Types.VARCHAR),//STATUS
									new DataValue(level2Elm.getParameter(), Types.VARCHAR), 
									new DataValue(level2Elm.getStatus(), Types.VARCHAR), 
									new DataValue(eId, Types.VARCHAR),
									new DataValue(level2Elm.getsType(), Types.VARCHAR), 
									new DataValue(request.getModularID(), Types.VARCHAR),
									new DataValue(maxPriority, Types.VARCHAR),
									new DataValue(level2Elm.getRFUNCNO(), Types.VARCHAR),
									new DataValue(level2Elm.getRPATTERN(), Types.VARCHAR),
									new DataValue(REGEDISTREX, Types.VARCHAR),
									new DataValue(level2Elm.getOnSale(), Types.VARCHAR),
									new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
							};

					InsBean ib1 = new InsBean("DCP_MODULAR", columnsModular);
					ib1.addValues(insValue1);
					this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

					this.doExecuteDataToDB();

					if (res.isSuccess()) 
					{
						res.setServiceStatus("000");
						res.setServiceDescription("服务执行成功");

						res.setDatas(new ArrayList<DCP_ModularCreateRes.level2Elm>());

						DCP_ModularCreateRes.level2Elm oneLv2 =  res.new level2Elm();
						oneLv2.setMODULARNO(level2Elm.getModularNo());
						res.getDatas().add(oneLv2);
					} 
					else 
					{
						res.setDatas(new ArrayList<DCP_ModularCreateRes.level2Elm>());

						DCP_ModularCreateRes.level2Elm oneLv2 =  res.new level2Elm();
						oneLv2.setMODULARNO("");
						res.getDatas().add(oneLv2);
					}		
				}
				else
				{
					res.setSuccess(false);
					res.setServiceDescription("菜单编码不能重复");

					res.setDatas(new ArrayList<DCP_ModularCreateRes.level2Elm>());

					DCP_ModularCreateRes.level2Elm oneLv2 =  res.new level2Elm();
					oneLv2.setMODULARNO("");
					res.getDatas().add(oneLv2);
					break;
				}

			}			
		}
		else 
		{
			res.setSuccess(false);
			res.setServiceDescription("页面GUID不能重复");

			res.setDatas(new ArrayList<DCP_ModularCreateRes.level2Elm>());

			DCP_ModularCreateRes.level2Elm oneLv2 =  res.new level2Elm();
			oneLv2.setMODULARNO("");
			res.getDatas().add(oneLv2);			
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ModularCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ModularCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ModularCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean isVerifyFail(DCP_ModularCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		level1Elm request = req.getRequest();
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		List<DCP_ModularCreateReq.level2Elm> jsonDatas = request.getDatas();


		if (Check.Null(request.getModularID())) 
		{
			errMsg.append("请求页面GUID不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (DCP_ModularCreateReq.level2Elm level2Elm : jsonDatas) 
		{		
			if (Check.Null(level2Elm.getModularNo())) 
			{
				errMsg.append("菜单编码不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(level2Elm.getModularName())) 
			{
				errMsg.append("菜单名称不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(level2Elm.getModularLevel())) 
			{
				errMsg.append("菜单等级不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(level2Elm.getsType())) 
			{
				errMsg.append("所属系统不可为空值, ");
				isFail = true;
			} 

			if (Check.Null(level2Elm.getStatus())) 
			{
				errMsg.append("状态不可为空值, ");
				isFail = true;
			} 

			List<DCP_ModularCreateReq.level3Elm> level3Elm=level2Elm.getDatas();
			for (DCP_ModularCreateReq.level3Elm level3 : level3Elm) 
			{		
				if (Check.Null(level3.getFuncNo())) 
				{
					errMsg.append("功能编码不可为空值, ");
					isFail = true;
				} 
				if (Check.Null(level3.getFuncName())) 
				{
					errMsg.append("功能名称不可为空值, ");
					isFail = true;
				} 

			}

		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}


		return isFail;
	}

	@Override
	protected TypeToken<DCP_ModularCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ModularCreateReq>(){};
	}

	@Override
	protected DCP_ModularCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ModularCreateRes();
	}

	protected String getModularGuid_SQL()
	{
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("select MODULAR_ID from DCP_MODULAR "
				+ "WHERE EID=? "
				+ "AND MODULAR_ID=? ");

		sql = sqlbuf.toString();

		return sql;	
	}

	protected String getModularNO_SQL()
	{
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("select MODULARNO from DCP_MODULAR "
				+ "WHERE EID=? "
				+ "AND MODULARNO=? ");

		sql = sqlbuf.toString();

		return sql;	
	}

	protected String getModular_FunctionExist()
	{
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("select FUNCTION_ID from DCP_MODULAR_FUNCTION "
				+ "WHERE EID=? "
				+ "AND FUNCNO=? ");

		sql = sqlbuf.toString();

		return sql;	
	}


	/**
	 * 获取最大序号，priority 做排序用，在此不是优先级的概念
	 * @param modularLevel
	 * @param eId
	 * @param upperModular
	 * @return
	 */
	protected String getMaxPriority(String modularLevel,String eId, String upperModular )
	{
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select NVL (MAX(priority)+1, 1  ) AS priority  from DCP_MODULAR "
				+ "WHERE EID= '"+eId+"' and modularLevel = '"+modularLevel+"' " );
		if(!Check.Null(modularLevel) && modularLevel.equals("1")){
			sqlbuf.append(" and upperModular IS NULL ");
		}
		else{
			sqlbuf.append(" and upperModular = '"+upperModular+"'");
		}

		sql = sqlbuf.toString();

		return sql;	
	}




}
