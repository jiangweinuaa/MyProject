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
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_DefaultOrgUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DefaultOrgUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_DefaultOrgUpdate extends SPosAdvanceService<DCP_DefaultOrgUpdateReq, DCP_DefaultOrgUpdateRes>{

	@Override
	protected void processDUID(DCP_DefaultOrgUpdateReq req, DCP_DefaultOrgUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String shopId = req.getRequest().getShopId(); //选择的默认门店编号 
		String opNo = req.getOpNO(); 
		String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //org_form=2  登录时会校验管辖门店是否注册
        //  select * from Platform_CregisterDetail where shopid='NJ001' and producttype='1'
        String orgForm="";
        StringBuilder orSb=new StringBuilder();
        orSb.append("select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getRequest().getShopId()+"' ");
        List<Map<String, Object>> orList = this.doQueryData(orSb.toString(), null);
        if(orList.size()>0){
            orgForm = orList.get(0).get("ORG_FORM").toString();
        }
        if("2".equals(orgForm)){
            StringBuilder psSb=new StringBuilder();
            psSb.append("select * from PLATFORM_STAFFS_SHOP where shopid='"+req.getRequest().getShopId()+"' and eid='"+eId+"' and OPNO='"+opNo+"' ");
            List<Map<String, Object>> psList = this.doQueryData(psSb.toString(), null);
            if(psList.size()<=0){
               // throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "门店不在管辖门店范围之内！");
            }
            StringBuilder vaSb=new StringBuilder();
            vaSb.append(" select * from Platform_CregisterDetail where shopid='"+req.getRequest().getShopId()+"' and producttype='1'");//1:门店管理
            List<Map<String, Object>> vaList = this.doQueryData(vaSb.toString(), null);
            if(vaList.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "门店未注册！");
            }

        }

        UptBean ub1 = null;
		ub1 = new UptBean("PLATFORM_STAFFS_SHOP");
		ub1.addUpdateValue("ISDEFAULT", new DataValue("Y", Types.VARCHAR));
		
		ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		ub1.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(ub1));
		
		// 设置默认组织的同时， 设置其他组织 isDefault = "N"
		UptBean ub2 = null;	
		ub2 = new UptBean("PLATFORM_STAFFS_SHOP");
		ub2.addUpdateValue("ISDEFAULT", new DataValue("N", Types.VARCHAR));
		
		ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR ,DataExpression.NE )); // NE 表示 不等于
		ub2.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
		ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(ub2));

        //更改默认组织
        UptBean ub3 = null;
        ub3 = new UptBean("PLATFORM_STAFFS");
        ub3.addUpdateValue("DEFAULTORG", new DataValue(shopId, Types.VARCHAR));
        ub3.addUpdateValue("LASTMODIOPID", new DataValue(employeeNo, Types.VARCHAR));
        ub3.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));


        ub3.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
        ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub3));
		
		this.doExecuteDataToDB();
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DefaultOrgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DefaultOrgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DefaultOrgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DefaultOrgUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		String shopId = req.getRequest().getShopId();
		
		if (Check.Null(shopId)) 
		{
			errMsg.append("默认组织编号不可为空值 ");
			isFail = true;
		} 
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DefaultOrgUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DefaultOrgUpdateReq>(){};
	}

	@Override
	protected DCP_DefaultOrgUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DefaultOrgUpdateRes();
	}

	
}
