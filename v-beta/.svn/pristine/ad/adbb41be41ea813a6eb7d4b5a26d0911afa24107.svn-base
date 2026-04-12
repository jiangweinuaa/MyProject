package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_ModularUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ModularUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ModularUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_ModularUpdate extends SPosAdvanceService<DCP_ModularUpdateReq,DCP_ModularUpdateRes>
{
	@Override
	protected void processDUID(DCP_ModularUpdateReq req, DCP_ModularUpdateRes res) throws Exception 
	{		
		String eId = req.geteId();
		String sql = null;
		sql = this.getModular_FunctionGuid_SQL();
		level1Elm request = req.getRequest();

		String[] conditionValues_checkGuid = {eId,request.getFunctionID().toUpperCase() }; //查詢條件
		List<Map<String, Object>> getQData_checkGuid = this.doQueryData(sql,conditionValues_checkGuid);

		boolean bOK=false;

		List<DataProcessBean> lstIns=new ArrayList<DataProcessBean>();

		//GUID存在否判断
		if(getQData_checkGuid==null || getQData_checkGuid.isEmpty())
		{
			List<DCP_ModularUpdateReq.level2Elm> jsonDatas = request.getDatas();
			for (DCP_ModularUpdateReq.level2Elm level2Elm : jsonDatas) 
			{				
				bOK=true;

				String modularNO = level2Elm.getModularNo();
				String modularLevel = level2Elm.getModularLevel();
				String upperModular = level2Elm.getUpperModular();
				//2018-11-07 yyy 新增上下移动时处理逻辑
				String toPriority = level2Elm.getToPriority();
				String priority = level2Elm.getPriority();
				if(!Check.Null(toPriority)){
					int toPt = Integer.parseInt(toPriority);
					int pt = Integer.parseInt(priority);

					UptBean ub2 = null;	
					ub2 = new UptBean("DCP_MODULAR");
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
					ub2.addCondition("MODULARLEVEL", new DataValue(modularLevel, Types.VARCHAR));

					// 当前菜单等级 不是一级菜单时， 加上upperModular ，一级菜单 不能加 upperModular，因为上级菜单为空
					if(!modularLevel.equals("1")){
						ub2.addCondition("UPPERMODULAR", new DataValue(upperModular, Types.VARCHAR));
					}

					UptBean ub3 = null;	
					ub3 = new UptBean("DCP_MODULAR");
					//add Value
					ub3.addUpdateValue("PRIORITY", new DataValue(toPriority, Types.VARCHAR));
					//condition
					ub3.addCondition("MODULARNO", new DataValue(modularNO, Types.VARCHAR));
					ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
					ub2.addCondition("MODULARLEVEL", new DataValue(modularLevel, Types.VARCHAR));

					this.addProcessData(new DataProcessBean(ub2));
					this.addProcessData(new DataProcessBean(ub3));
					this.doExecuteDataToDB(); //將資料寫到 DB
				}
				else{

					//单身明细DCP_MODULAR_FUNCTION
					List<DCP_ModularUpdateReq.level3Elm> level3=level2Elm.getDatas();
					for (DCP_ModularUpdateReq.level3Elm level3Elm : level3) 
					{
						String sqlExist = null;
						sqlExist = this.getModular_FunctionExist();

						String[] conditionValues_sqlExist = {eId,level3Elm.getFuncNo(),level2Elm.getModularNo()}; //查詢條件
						List<Map<String, Object>> getQData_sqlExist = this.doQueryData(sqlExist,conditionValues_sqlExist);
						//Function功能编码不能重复
						if(getQData_sqlExist==null || getQData_sqlExist.isEmpty())
						{						
							//新增SQL
							int insColCt = 0;
							String[] columnsModularDetail ={"FUNCNO","MODULARNO","FTYPE","PRONAME","PARAMETER","CHSMSG","CHTMSG","ENGMSG","STATUS","EID"
									,"ONSALE","UPDATE_TIME"};
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
							//this.addProcessData(new DataProcessBean(ib2));

							lstIns.add(new DataProcessBean(ib2));	
						}
						else
						{
							bOK=false;

							res.setSuccess(false);
							res.setServiceDescription("功能编码"+level3Elm.getFuncNo()+"不能重复");

							res.setDatas(new ArrayList<DCP_ModularUpdateRes.level2Elm>());

							DCP_ModularUpdateRes.level2Elm oneLv2 = res.new level2Elm();
							oneLv2.setFuncNO("");
							res.getDatas().add(oneLv2);
							oneLv2 = null;
							break;						
						}
					}		

					if(bOK==true)
					{
						//更新单头
						UptBean ub1 = new UptBean("DCP_MODULAR");			
						ub1.addCondition("MODULARNO",new DataValue(level2Elm.getModularNo(), Types.VARCHAR));
						ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));

						ub1.addUpdateValue("MODULARLEVEL",new DataValue(level2Elm.getModularLevel(), Types.DECIMAL)); 
						ub1.addUpdateValue("UPPERMODULAR",new DataValue(level2Elm.getUpperModular(), Types.VARCHAR));
						ub1.addUpdateValue("CHSMSG",new DataValue(level2Elm.getModularName(), Types.VARCHAR) );
						ub1.addUpdateValue("CHTMSG",new DataValue(level2Elm.getModularNameCht(), Types.VARCHAR) );
						ub1.addUpdateValue("ENGMSG",new DataValue(level2Elm.getModularNameEng(), Types.VARCHAR) );
						ub1.addUpdateValue("FTYPE",new DataValue(level2Elm.getfType(), Types.VARCHAR));
						ub1.addUpdateValue("PRONAME",new DataValue(level2Elm.getProName(), Types.VARCHAR));
						ub1.addUpdateValue("PARAMETER",new DataValue(level2Elm.getParameter(), Types.VARCHAR));
						ub1.addUpdateValue("STATUS",new DataValue(level2Elm.getStatus(), Types.VARCHAR));
						ub1.addUpdateValue("EID",new DataValue(eId, Types.VARCHAR));
						ub1.addUpdateValue("STYPE",new DataValue(level2Elm.getsType(), Types.VARCHAR)); 
						ub1.addUpdateValue("PRIORITY", new DataValue(level2Elm.getPriority(), Types.VARCHAR));
						ub1.addUpdateValue("RFUNCNO", new DataValue(level2Elm.getRFUNCNO(), Types.VARCHAR));
						ub1.addUpdateValue("RPATTERN", new DataValue(level2Elm.getRPATTERN(), Types.VARCHAR));

						String REGEDISTREX = PosPub.encodeMD5(level2Elm.getModularNo() + level2Elm.getRFUNCNO() + "DIGIWIN");
						ub1.addUpdateValue("REGEDISTREX", new DataValue(REGEDISTREX, Types.VARCHAR));
						ub1.addUpdateValue("ONSALE", new DataValue(level2Elm.getOnSale(), Types.VARCHAR));
						ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

						this.addProcessData(new DataProcessBean(ub1));

						//先删除原来单身
						DelBean db1 = new DelBean("DCP_MODULAR_FUNCTION");
						db1.addCondition("MODULARNO", new DataValue(level2Elm.getModularNo(), Types.VARCHAR));
						db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(db1));

						//再加入新增SQL
						for (DataProcessBean ins : lstIns) 
						{				
							this.addProcessData(ins);
						}

						this.doExecuteDataToDB();
						if (res.isSuccess()) 			
						{			
							res.setServiceStatus("000");
							res.setServiceDescription("服务执行成功");
							res.setDatas(new ArrayList<DCP_ModularUpdateRes.level2Elm>());				
							DCP_ModularUpdateRes.level2Elm oneLv2 = res.new level2Elm();
							oneLv2.setFuncNO(oneLv2.getFuncNO());
							res.getDatas().add(oneLv2);		
							oneLv2 =null;
						} 			
						else 			
						{				
							res.setDatas(new ArrayList<DCP_ModularUpdateRes.level2Elm>());				
							DCP_ModularUpdateRes.level2Elm oneLv2 = res.new level2Elm();
							oneLv2.setFuncNO("");
							res.getDatas().add(oneLv2);	
							oneLv2 = null;
						}					
					}
				}

			}					
		}
		else
		{
			res.setSuccess(false);
			res.setServiceDescription("页面GUID不能重复");
			res.setDatas(new ArrayList<DCP_ModularUpdateRes.level2Elm>());
			DCP_ModularUpdateRes.level2Elm oneLv2 = res.new level2Elm();
			oneLv2.setFuncNO("");;
			res.getDatas().add(oneLv2);		
			oneLv2 = null;
		}	
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ModularUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ModularUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ModularUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ModularUpdateReq req) throws Exception 
	{	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
		List<DCP_ModularUpdateReq.level2Elm> jsonDatas = request.getDatas();

		if (Check.Null(request.getFunctionID())) 
		{
			errMsg.append("请求页面GUID不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (DCP_ModularUpdateReq.level2Elm level2Elm : jsonDatas) 
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
			//			if (Check.Null(level2Elm.getCanPrint())) 
			//			{
			//				errCt++;
			//				errMsg.append("打印否不可为空值, ");
			//				isFail = true;
			//			} 
			//			if (Check.Null(level2Elm.getCanLineDisp())) 
			//			{
			//				errCt++;
			//				errMsg.append("客显否不可为空值, ");
			//				isFail = true;
			//			} 
			if (Check.Null(level2Elm.getStatus())) 
			{
				errMsg.append("状态不可为空值, ");
				isFail = true;
			} 

			List<DCP_ModularUpdateReq.level3Elm> level3Elm=level2Elm.getDatas();
			for (DCP_ModularUpdateReq.level3Elm level3 : level3Elm) 
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
	protected TypeToken<DCP_ModularUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ModularUpdateReq>(){};
	}

	@Override
	protected DCP_ModularUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ModularUpdateRes();
	}

	protected String getModular_FunctionGuid_SQL()
	{
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("select FUNCTION_ID from DCP_MODULAR_FUNCTION "
				+ "WHERE EID=? "
				+ "AND FUNCTION_ID=? ");

		sql = sqlbuf.toString();

		return sql;	
	}

	protected String getModular_FunctionExist()
	{
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("select FUNCTION_ID from DCP_MODULAR_FUNCTION "
				+ "WHERE EID=? "
				+ "AND FUNCNO=? "
				+ "AND MODULARNO<>?");

		sql = sqlbuf.toString();

		return sql;	
	}
}
