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
import com.dsc.spos.json.cust.req.DCP_RoleUpdateReq;
import com.dsc.spos.json.cust.res.DCP_RoleUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_RoleUpdate extends SPosAdvanceService<DCP_RoleUpdateReq, DCP_RoleUpdateRes> 
{
	@Override
	protected void processDUID(DCP_RoleUpdateReq req, DCP_RoleUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String employeeNo = req.getEmployeeNo();
		String departmentNo = req.getDepartmentNo();
		String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		String opGroup = req.getRequest().getOpGroup();
		String opgName=req.getRequest().getOpgName();
		String disc=req.getRequest().getDisc();
		String isMask=req.getRequest().getIsMask();
		String status = req.getRequest().getStatus();
		String isShowDistriPrice = req.getRequest().getIsShowDistriPrice();
		if (Check.Null(isShowDistriPrice) || !isShowDistriPrice.equals("Y"))
		{
			isShowDistriPrice="N";
		}
		String maxFreeAmt=req.getRequest().getMaxFreeAmt();
		if (Check.Null(maxFreeAmt))
			maxFreeAmt="0";
		
		int i_disc=100;
		if(disc==null||disc.length()==0)
		{
		  i_disc=100;
		}
		else 
		{
			i_disc=Integer.parseInt(disc);
			/*
			float f= Float.parseFloat(disc); 
			i_disc=(int) Math.floor(f);
			*/
	  }
		UptBean ub1 = null;	
		ub1 = new UptBean("PLATFORM_Role");
		ub1.addUpdateValue("OPGNAME", new DataValue(opgName,Types.VARCHAR));
		ub1.addUpdateValue("DISC", new DataValue(i_disc, Types.INTEGER));
		ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
		ub1.addUpdateValue("ISMASK", new DataValue(isMask, Types.VARCHAR));
		ub1.addUpdateValue("ISSHOWDISTRIPRICE", new DataValue(isShowDistriPrice, Types.VARCHAR));
		ub1.addUpdateValue("MAXFREEAMT", new DataValue(maxFreeAmt, Types.VARCHAR));

		ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

		ub1.addCondition("OPGROUP", new DataValue(opGroup, Types.VARCHAR));
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

    //修改权限

        //角色权限
        List<DCP_RoleCreateReq.FunctionPower> functionPower = req.getRequest().getFunctionPower();
        List<DCP_RoleCreateReq.ModularPower> modularPower = req.getRequest().getModularPower();

        //先删除
        DelBean db1 = new DelBean("PLATFORM_BILLPOWER");
        db1.addCondition("OPGROUP", new DataValue(opGroup, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("PLATFORM_POWER");
        db2.addCondition("OPGROUP", new DataValue(opGroup, Types.VARCHAR));
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
                        new DataValue(opGroup, Types.VARCHAR),
                        new DataValue(modular, Types.VARCHAR),
                        new DataValue(i_powerType, Types.INTEGER),
                        new DataValue("100", Types.VARCHAR),
                        new DataValue(eId, Types.VARCHAR),
						new DataValue(queryRange, Types.VARCHAR),
						new DataValue(editRange, Types.VARCHAR),
						new DataValue(deleteRange, Types.VARCHAR),
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
                        new DataValue(opGroup, Types.VARCHAR),
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

	@Override
	protected List<InsBean> prepareInsertData(DCP_RoleUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RoleUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RoleUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RoleUpdateReq req) throws Exception {
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
        String opGroup = req.getRequest().getOpGroup();
        if(req.getRequest().getStatus().equals("0")&&opGroup.length()>0){
            //禁用
            if(isRoleBindUser(opGroup)){
                errCt++;
                errMsg.append("该角色已绑定用户,不可禁用, ");
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
	protected TypeToken<DCP_RoleUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_RoleUpdateReq>(){};
	}

	@Override
	protected DCP_RoleUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_RoleUpdateRes();
	}

    public boolean isRoleBindUser(String opGroup) throws Exception{
        String sql=String.format("select * from PLATFORM_STAFFS_ROLE where OPGROUP='%s'",opGroup);
        List<Map<String, Object>> list = this.doQueryData(sql, null);

        return list.size()>0;
    }

}
