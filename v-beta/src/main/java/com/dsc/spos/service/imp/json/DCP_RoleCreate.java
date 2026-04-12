package com.dsc.spos.service.imp.json;

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
import com.dsc.spos.json.cust.req.DCP_RoleCreateReq;
import com.dsc.spos.json.cust.res.DCP_RoleCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_RoleCreate extends SPosAdvanceService<DCP_RoleCreateReq, DCP_RoleCreateRes> 
{

	@Override
	protected void processDUID(DCP_RoleCreateReq req, DCP_RoleCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		if (checkGuid(req) == false)
		{
			if(checkOpGroup(req) == false)
			{
				//查询条件

				String eId = req.geteId();
				String employeeNo = req.getEmployeeNo();
				String departmentNo = req.getDepartmentNo();
				String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				String opgroup = req.getRequest().getOpGroup();
				String opgname=req.getRequest().getOpgName();
				String disc=req.getRequest().getDisc();
				String status = req.getRequest().getStatus();
				String ismask=req.getRequest().getIsMask();
				String roleid=req.getRequest().getRoleID();
				String role = req.getRequest().getRole();
				String isShowDistriPrice = req.getRequest().getIsShowDistriPrice();
				if (Check.Null(isShowDistriPrice) || !isShowDistriPrice.equals("Y"))
				{
					isShowDistriPrice="N";
				}
				String maxFreeAmt = req.getRequest().getMaxFreeAmt();
				if (Check.Null(maxFreeAmt))
					maxFreeAmt="0";

				int i_disc=100;
				if (disc == null || disc.length()==0 )
				{
					i_disc=100;
				} 
				else 	
				{
					i_disc=Integer.parseInt(disc);
				}


				String[] columns1 = {
						"EID","OPGROUP","OPGNAME","DISC","STATUS",
						"ISMASK","ROLE_ID","ISSHOWDISTRIPRICE","MAXFREEAMT","ROLE","CREATEOPID",
						"CREATEDEPTID","CREATETIME","LASTMODIOPID","LASTMODITIME"
				};
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(opgroup, Types.VARCHAR),
						new DataValue(opgname, Types.VARCHAR),		
						new DataValue(i_disc, Types.INTEGER),
						new DataValue(status, Types.VARCHAR),
						new DataValue(ismask, Types.VARCHAR),			
						new DataValue(roleid, Types.VARCHAR),
						new DataValue(isShowDistriPrice, Types.VARCHAR),
						new DataValue(maxFreeAmt, Types.VARCHAR),
						new DataValue(role, Types.VARCHAR),

						new DataValue(req.getOpNO(), Types.VARCHAR),
						new DataValue(departmentNo, Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE),
						new DataValue(req.getOpNO(), Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE),

				};

				InsBean ib1 = new InsBean("PLATFORM_ROLE", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));//增加单头

                //角色权限
                List<DCP_RoleCreateReq.FunctionPower> functionPower = req.getRequest().getFunctionPower();
                List<DCP_RoleCreateReq.ModularPower> modularPower = req.getRequest().getModularPower();

                //先删除
                DelBean db1 = new DelBean("PLATFORM_BILLPOWER");
                db1.addCondition("OPGROUP", new DataValue(opgroup, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                DelBean db2 = new DelBean("PLATFORM_POWER");
                db2.addCondition("OPGROUP", new DataValue(opgroup, Types.VARCHAR));
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                if(modularPower != null && modularPower.size() > 0)
                {
                    for (DCP_RoleCreateReq.ModularPower itemModular:modularPower) {

                        String powerType=itemModular.getPowerType();
                        String modular=itemModular.getModularNo();
						String queryRange = itemModular.getQueryRange();
						String editRange = itemModular.getEditRange();
						String deleteRange = itemModular.getDeleteRange();
						int i_powerType=1;
                        if(powerType==null||powerType.length()==0)
                        {
                            i_powerType=1;
                        }
                        else
                        {
                            i_powerType=Integer.parseInt(powerType);
                        }
                        String[] mpPower = {"OPGROUP","MODULARNO","POWERTYPE","STATUS","EID","QUERY_RANGE","EDIT_RANGE","DELETE_RANGE"};
                        DataValue[] mpinsValue = null;

                        mpinsValue = new DataValue[]{
                                new DataValue(opgroup, Types.VARCHAR),
                                new DataValue(modular, Types.VARCHAR),
                                new DataValue(i_powerType, Types.INTEGER),
                                new DataValue("100", Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
								new DataValue(queryRange, Types.VARCHAR),
								new DataValue(deleteRange, Types.VARCHAR),
								new DataValue(editRange, Types.VARCHAR),

                        };

                        InsBean mpib = new InsBean("PLATFORM_BILLPOWER", mpPower);
                        mpib.addValues(mpinsValue);
                        this.addProcessData(new DataProcessBean(mpib));
                    }


                }

                if(functionPower != null && functionPower.size() > 0)
                {

                    for (DCP_RoleCreateReq.FunctionPower itemFunction :functionPower) {
                        String powerType=itemFunction.getPowerType();
                        String functionno=itemFunction.getFunctionNo();
                        int i_powerType=1;
                        if(powerType==null||powerType.length()==0)
                        {
                            i_powerType=1;
                        }
                        else
                        {
                            i_powerType=Integer.parseInt(powerType);
                        }
                        String[] fpColumn = {"OPGROUP","FUNCNO","POWERTYPE","STATUS","EID"};
                        DataValue[] fpinsValue = null;

                        fpinsValue = new DataValue[]{
                                new DataValue(opgroup, Types.VARCHAR),
                                new DataValue(functionno, Types.VARCHAR),
                                new DataValue(i_powerType, Types.INTEGER),
                                new DataValue("100", Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                        };

                        InsBean fpib = new InsBean("PLATFORM_POWER", fpColumn);
                        fpib.addValues(fpinsValue);
                        this.addProcessData(new DataProcessBean(fpib));

                    }
                }

                this.doExecuteDataToDB();
				if (res.isSuccess()) 
				{
					res.setSuccess(true);
					res.setServiceStatus("000");
					res.setServiceDescription("服务执行成功");
				}
			}
			else
			{
				res.setSuccess(false);
				res.setServiceDescription("此角色编码已存在！");
			}
		}
		else
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RoleCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RoleCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RoleCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RoleCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；	
		if(req.getRequest()==null)
		{
		  	errMsg.append("request不能为空 ");
		  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		if (Check.Null(req.getRequest().getOpGroup())) 
		{
			errCt++;
			errMsg.append("角色编码不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(req.getRequest().getOpgName())) 
		{
			errCt++;
			errMsg.append("角色名称不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(req.getRequest().getIsMask())) 
		{
			errCt++;
			errMsg.append("是否遮罩不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(req.getRequest().getStatus())) 
		{
			errCt++;
			errMsg.append("状态不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(req.getRequest().getRoleID())) 
		{
			errCt++;
			errMsg.append("角色ID不可为空值, ");
			isFail = true;
		} 
		if(Check.Null(req.getRequest().getDisc())==false)
		{
			try 
			{
				Integer.parseInt(req.getRequest().getDisc());
			} 
			catch (Exception e)
			{
				errCt++;
				errMsg.append("折扣必须为数字, ");
				isFail = true;
			}
		}
		//如果没传值默认100
		if(Check.Null(req.getRequest().getDisc()))
		{
			req.getRequest().setDisc("100");
		}


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_RoleCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_RoleCreateReq>(){};
	}

	@Override
	protected DCP_RoleCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_RoleCreateRes();
	}

	private boolean checkGuid(DCP_RoleCreateReq req) throws Exception {
		String sql = null;
		String guid = req.getRequest().getRoleID();
		String eId = req.geteId();
		boolean existGuid;

		String[] conditionValues = { guid, eId }; // 查询用户表
		sql = this.getQuerySql_getGuid();
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {			
			existGuid =  false;
		}

		return existGuid;
	}	

	protected String getQuerySql_getOpGroup() throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("" 
				+ " select * from PLATFORM_ROLE where OPGROUP = ?  and EID = ? "); 

		if (sqlbuf.length() > 0)
			sql = sqlbuf.toString();

		return sql;
	}

	protected String getQuerySql_getGuid() throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(" select * from PLATFORM_ROLE where ROLE_ID = ? and EID = ? "); 

		if (sqlbuf.length() > 0)
			sql = sqlbuf.toString();

		return sql;
	}

	private boolean checkOpGroup(DCP_RoleCreateReq req) throws Exception {
		String sql = null;
		String opGroup = req.getRequest().getOpGroup();
		String eId = req.geteId();
		boolean existOpGroup;

		String[] conditionValues = { opGroup, eId }; // 查询用户表
		sql = this.getQuerySql_getOpGroup();
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

		if (getQData != null && getQData.isEmpty() == false) {
			existOpGroup = true;
		} else {			
			existOpGroup =  false;
		}

		return existOpGroup;
	}

}
