package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StaffUpdateReq;
import com.dsc.spos.json.cust.req.DCP_StaffUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_StaffUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_StaffUpdate extends SPosAdvanceService<DCP_StaffUpdateReq, DCP_StaffUpdateRes> {

	@Override
	protected boolean isVerifyFail(DCP_StaffUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String staffNO = req.getRequest().getStaffNo();
		String status = req.getRequest().getStatus();
		String lang = req.getRequest().getLang();

        String discPowerType = req.getRequest().getDiscPowerType();
        String disc = req.getRequest().getDisc();
        String maxFreeAmt = req.getRequest().getMaxFreeAmt();


        List<level1Elm> datas = req.getRequest().getDatas();
		if(Check.Null(staffNO)){
			errMsg.append("用户编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(status)){
			errMsg.append("状态不能为空值 ");
			isFail = true;
		}
		if(Check.Null(lang)){
			errMsg.append("语言类别不能为空值 ");
			isFail = true;
		}
        if(Check.Null(discPowerType) || (!discPowerType.equals("ROLE") && !discPowerType.equals("USER")))
        {
            //errMsg.append("折扣/免单使用权限类型：值必须是ROLE或者USER");
            //isFail = true;
        }else if (discPowerType.equals("USER"))
        {
        	if(Check.Null(disc) || !PosPub.isNumeric(disc))
        	{
        		errMsg.append("最低折扣率，必须是0-100整数 ");
        		isFail = true;
        	}
        	if(Check.Null(maxFreeAmt) || !PosPub.isNumericType(maxFreeAmt))
        	{
        		errMsg.append("最大免单金额,必须是数字 ");
        		isFail = true;
        	}
        }
		if(datas!=null){
			for(level1Elm par : datas){

				if (Check.Null(par.getOpName()))
				{
					errMsg.append("用户名称不可为空值, ");
					isFail = true;
				}
				if (Check.Null(par.getLangType()))
				{
					errMsg.append("多语言类型不可为空值, ");
					isFail = true;
				}
				if (Check.Null(par.getStatus()))
				{
					errMsg.append("多语言状态不可为空值, ");
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
	protected TypeToken<DCP_StaffUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StaffUpdateReq>(){};
	}

	@Override
	protected DCP_StaffUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StaffUpdateRes();
	}

	@Override
	protected void processDUID(DCP_StaffUpdateReq req, DCP_StaffUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

			String eId = req.geteId();
			String staffNO = req.getRequest().getStaffNo();
			String status = req.getRequest().getStatus();
			String lang = req.getRequest().getLang();

			String staffName = req.getRequest().getStaffName();
			String departNo = req.getRequest().getDepartNo();
			String en_cash  = req.getRequest().getEn_cash();

            String distributor = req.getRequest().getDistributor();
            String employeeNo = req.getRequest().getEmployeeNo();
            String organizationNO = req.getOrganizationNO();
            String orgRange = req.getRequest().getOrgRange();
            String createOpId = req.getOpNO();
            String createDeptId = req.getDepartmentNo();


            String viewAbleDay = req.getRequest().getViewAbleDay();
			if(Check.Null(en_cash))
			{
				en_cash="Y";
			}
			if(Check.Null(viewAbleDay)){
				viewAbleDay = "999999"; // 如果单据可查看天数为空值的情况下 默认是999999天
			}

			String telephone = req.getRequest().getTelephone();
			String discPowerType = req.getRequest().getDiscPowerType();
            String disc="100";
            String maxFreeAmt="0";
            if(!Check.Null(discPowerType) && discPowerType.equals("USER"))
            {
            	disc = req.getRequest().getDisc();
                maxFreeAmt = req.getRequest().getMaxFreeAmt();
            }

			String[] opGroupList = req.getRequest().getOpGroup();
			//String[] orgList = req.getRequest().getOrg();
            List<DCP_StaffUpdateReq.Org> orgList = req.getRequest().getOrg();
            StringBuffer opGroup = new StringBuffer();
			StringBuffer org = new StringBuffer();
			for(int i = 0; i < opGroupList.length;i++){
				opGroup.append(opGroupList[i]);
			}
			for(int i = 0; i < orgList.size();i++){
				org.append(orgList.get(i).getOrganizationNo());
			}

			List<level1Elm> datas = req.getRequest().getDatas();
            List<DCP_StaffUpdateReq.levelWarehouseElm> warehouseList=req.getRequest().getWarehouseList();

			//添加多语言信息
			DelBean db2 = new DelBean("PLATFORM_STAFFS_LANG");
			db2.addCondition("OPNO", new DataValue(staffNO, Types.VARCHAR));
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			//this.doExecuteDataToDB();
			if(datas != null && datas.size() > 0){
				for (level1Elm par : datas) {
					int insColCt = 0;  
					String[] columnsName = {"OPNO", "LANG_TYPE", "OP_NAME", "STATUS", "EID"};
					String langType = par.getLangType();
					String lOPName = par.getOpName();
					String lstatus = par.getStatus();

					DataValue[] columnsVal = new DataValue[columnsName.length];
					for (int i = 0; i < columnsVal.length; i++) { 
						String keyVal = null;
						switch (i) { 
						case 0:
							keyVal = staffNO;
							break;
						case 1:
							keyVal = langType;
							break;
						case 2:
							keyVal = lOPName;
							break;
						case 3:
							keyVal = lstatus;
							break;
						case 4:
							keyVal = eId;
							break;
						default:
							break;	
						}  //switch 结束

						if (keyVal != null) {
							insColCt++;
							columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
						} 
						else {
							columnsVal[i] = null;
						}

					} 	
					String[] columns2  = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt = 0;

					for (int i=0;i<columnsVal.length;i++){
						if (columnsVal[i] != null){
							columns2[insColCt] = columnsName[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue2.length) 
								break;
						}
					}
					//添加原因码多语言信息
					InsBean ib2 = new InsBean("PLATFORM_STAFFS_LANG", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
					//					}
					//					else
					//					{
					//						res.setSuccess(false);
					//						res.setServiceDescription("多语言类型 "+langType+"不能重复");
					//					}
				}// 添加多语言信息到此结束
			}

			//添加到 platform_staffs_role 

			DelBean db3 = new DelBean("PLATFORM_STAFFS_ROLE");
			db3.addCondition("OPNO", new DataValue(staffNO, Types.VARCHAR));
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3));


			for(int i = 0; i<opGroupList.length ; i++){
				int insColCt3 = 0;
				String[] columnsName3 = {"OPNO", "OPGROUP","EID","STATUS"};
				String roleOpGroup = opGroupList[i].toString();

				if(roleOpGroup.trim().isEmpty()){
					continue;
				}

				DataValue[] roleColumnsVal = new DataValue[columnsName3.length];
				for (int j = 0; j < roleColumnsVal.length; j++) { 
					String keyVal = null;
					switch (j) { 
					case 0:
						keyVal = staffNO;
						break;
					case 1:
						keyVal = roleOpGroup;
						break;
					case 2:
						keyVal = eId;
						break;
					case 3:
						keyVal = "100";
						break;
					default:
						break;	
					}  //switch 结束

					if (keyVal != null) {
						insColCt3++;
						roleColumnsVal[j] = new DataValue(keyVal, Types.VARCHAR);
					} 
					else {
						roleColumnsVal[j] = null;
					}

				} 	
				String[] columns3  = new String[insColCt3];
				DataValue[] insValue3 = new DataValue[insColCt3];
				// 依照傳入參數組譯要insert的欄位與數值；
				insColCt3 = 0;

				for (int k=0;k<roleColumnsVal.length;k++){
					if (roleColumnsVal[k] != null){
						columns3[insColCt3] = columnsName3[k];
						insValue3[insColCt3] = roleColumnsVal[k];
						insColCt3 ++;
						if (insColCt3 >= insValue3.length) 
							break;
					}
				}
				//添加原因码多语言信息
				InsBean ib3 = new InsBean("PLATFORM_STAFFS_ROLE", columns3);
				ib3.addValues(insValue3);
				this.addProcessData(new DataProcessBean(ib3));

				// 添加platForm_staff_role
			}

			//添加到 platform_staffs_shop 
			//orgList 虽说规格上写的名字是组织，实际代表的含义是门店 

			DelBean db4 = new DelBean("PLATFORM_STAFFS_SHOP");
			db4.addCondition("OPNO", new DataValue(staffNO, Types.VARCHAR));
			db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db4));

			for(int i = 0; i<orgList.size() ; i++){
				int insColCt4 = 0;
				String[] columnsName4 = {"OPNO", "SHOPID","EID","ISDEFAULT","STATUS","ISEXPAND"};
				String orgNO =orgList.get(i).getOrganizationNo();
                String isExpand = orgList.get(i).getIsExpand();

                if(orgNO.trim().isEmpty()){
					continue;
				}

				DataValue[] orgColumnsVal = new DataValue[columnsName4.length];
				for (int j = 0; j < orgColumnsVal.length; j++) { 
					String keyVal = null;
					switch (j) { 
					case 0:
						keyVal = staffNO;
						break;
					case 1:
						keyVal = orgNO;
						break;
					case 2:
						keyVal = eId;
						break;
					case 3:
						keyVal = "N";
						break;
					case 4:
						keyVal = "100";
						break;
                    case 5:
                        keyVal = isExpand;
                        break;
					default:
						break;	
					}  //switch 结束

					if (keyVal != null) {
						insColCt4++;
						orgColumnsVal[j] = new DataValue(keyVal, Types.VARCHAR);
					} 
					else {
						orgColumnsVal[j] = null;
					}

				} 	
				String[] columns4  = new String[insColCt4];
				DataValue[] insValue4 = new DataValue[insColCt4];
				// 依照傳入參數組譯要insert的欄位與數值；
				insColCt4 = 0;

				for (int k=0;k < orgColumnsVal.length;k++){
					if (orgColumnsVal[k] != null){
						columns4[insColCt4] = columnsName4[k];
						insValue4[insColCt4] = orgColumnsVal[k];
						insColCt4 ++;
						if (insColCt4 >= insValue4.length) 
							break;
					}
				}
				//添加原因码多语言信息
				InsBean ib4 = new InsBean("PLATFORM_STAFFS_SHOP", columns4);
				ib4.addValues(insValue4);
				this.addProcessData(new DataProcessBean(ib4));

			}// 添加platForm_staff_SHOP 结束

            //mes仓库处理
            /**
            if (warehouseList != null )
            {
                DelBean db5 = new DelBean("MES_PLATFORM_STAFFS_WAREHOUSE");
                db5.addCondition("OPNO", new DataValue(staffNO, Types.VARCHAR));
                db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db5));
                if (warehouseList.size()>0)
                {
                    String[] columnsName5 = {"EID", "OPNO","ORGANIZATION","WAREHOUSENO","ISDEFAULT","LASTMODIOPID","LASTMODIOPNAME","LASTMODTIME"};
                    for (DCP_StaffUpdateReq.levelWarehouseElm levelWarehouseElm : warehouseList)
                    {
                        DataValue[] insValue5 = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(staffNO, Types.VARCHAR),
                                new DataValue(levelWarehouseElm.getOrganizationNo(), Types.VARCHAR),
                                new DataValue(levelWarehouseElm.getWarehouseNo(), Types.VARCHAR),
                                new DataValue(levelWarehouseElm.getIsDefault(), Types.VARCHAR),
                                new DataValue(req.getOpNO(), Types.VARCHAR),
                                new DataValue(req.getOpName(), Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };

						InsBean ib5 = new InsBean("MES_PLATFORM_STAFFS_WAREHOUSE", columnsName5);
						ib5.addValues(insValue5);
						this.addProcessData(new DataProcessBean(ib5));
                    }
                }
            }
             **/

			//更新单头
			UptBean ub1 = null;	
			ub1 = new UptBean("PLATFORM_STAFFS");
			//add Value
			if(!Check.Null(status))
			{
				ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			}
			if(!Check.Null(lang))
			{
				ub1.addUpdateValue("LANG_TYPE", new DataValue(lang, Types.VARCHAR));
			}
			//ub1.addUpdateValue("OPNO", new DataValue(staffNO, Types.VARCHAR));
			if(!Check.Null(departNo))
			{
				ub1.addUpdateValue("DEPARTNO", new DataValue(departNo, Types.VARCHAR));
			}
			if(!Check.Null(staffName))
			{
				ub1.addUpdateValue("OPNAME", new DataValue(staffName, Types.VARCHAR));
			}
			if(!Check.Null(en_cash))
			{
				ub1.addUpdateValue("EN_CASH",new DataValue(en_cash,Types.VARCHAR));
			}
			if(!Check.Null(viewAbleDay))
			{
				ub1.addUpdateValue("VIEWABLEDAY", new DataValue(viewAbleDay,Types.VARCHAR));
			}
			if(!Check.Null(telephone))
			{
				ub1.addUpdateValue("TELEPHONE", new DataValue(telephone,Types.VARCHAR));
			}
			if(!Check.Null(discPowerType))
			{
				ub1.addUpdateValue("DISCPOWERTYPE", new DataValue(discPowerType,Types.VARCHAR));
			}
			if(!Check.Null(discPowerType) && discPowerType.equals("USER"))
			{
				ub1.addUpdateValue("DISC", new DataValue(disc,Types.VARCHAR));
				ub1.addUpdateValue("MAXFREEAMT", new DataValue(maxFreeAmt,Types.VARCHAR));
			}

            if(!Check.Null(distributor))
            {
                ub1.addUpdateValue("DISTRIBUTOR", new DataValue(distributor,Types.VARCHAR));
            }

            if(!Check.Null(employeeNo))
            {
                ub1.addUpdateValue("EMPLOYEENO", new DataValue(employeeNo,Types.VARCHAR));
            }

            if(!Check.Null(req.getRequest().getDefaultOrg()))
            {
                ub1.addUpdateValue("DEFAULTORG", new DataValue(req.getRequest().getDefaultOrg(),Types.VARCHAR));
            }

            if(!Check.Null(orgRange))
            {
                ub1.addUpdateValue("ORGRANGE", new DataValue(orgRange,Types.VARCHAR));
            }
            if(!Check.Null(createOpId))
            {
                ub1.addUpdateValue("LASTMODIOPID", new DataValue(createOpId,Types.VARCHAR));
            }
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));


            //condition
			ub1.addCondition("OPNO", new DataValue(staffNO, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));

			this.doExecuteDataToDB();


		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StaffUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StaffUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StaffUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;

	}

}
