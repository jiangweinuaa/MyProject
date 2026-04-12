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
import com.dsc.spos.json.cust.req.DCP_StaffCreateReq;
import com.dsc.spos.json.cust.req.DCP_StaffCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_StaffUpdateReq;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes.paras;
import com.dsc.spos.json.cust.res.DCP_StaffCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_StaffCreate extends SPosAdvanceService<DCP_StaffCreateReq, DCP_StaffCreateRes>{

	@Override
	protected void processDUID(DCP_StaffCreateReq req, DCP_StaffCreateRes res) throws Exception {
		// TODO Auto-generated method stub

		String sql = null;
		try {
            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            String eId = req.geteId();
			String staffNO = req.getRequest().getStaffNo();
			String password = req.getRequest().getPassword();
			String status = req.getRequest().getStatus();
			String staffName = req.getRequest().getStaffName();
			String lang = req.getRequest().getLang();
			String en_cash  = req.getRequest().getEn_cash();
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
            if(discPowerType.equals("USER"))
            {
            	disc = req.getRequest().getDisc();
                maxFreeAmt = req.getRequest().getMaxFreeAmt();
            }

			String departNo = req.getRequest().getDepartNo();
			
			String staffID = req.getRequest().getStaffID();
            List<DCP_StaffCreateReq.levelWarehouseElm> warehouseList=req.getRequest().getWarehouseList();


            sql = this.isRepeat(req, staffNO);
			List<Map<String, Object>> staffDatas = this.doQueryData(sql, null);
			
			if(staffDatas.isEmpty()){
				String[] opGroupList = req.getRequest().getOpGroup();
				//String[] orgList = req.getRequest().getOrg();
                List<DCP_StaffCreateReq.Org> orgList = req.getRequest().getOrg();
                StringBuffer opGroup = new StringBuffer();
				StringBuffer org = new StringBuffer();
				for(int i = 0; i < opGroupList.length;i++){
					opGroup.append(opGroupList[i]);
				}
				for(int i = 0; i < orgList.size();i++){
					org.append(orgList.get(i).getOrganizationNo());
				}
				
				List<level1Elm> datas = req.getRequest().getDatas();	
				
				String[] columns1 = {"OPNO", "PASSWORD","STATUS","LANG_TYPE","EID" ,"DEPARTNO","OPNAME","EN_CASH","VIEWABLEDAY","TELEPHONE","DISCPOWERTYPE","DISC","MAXFREEAMT",
                "DISTRIBUTOR","EMPLOYEENO","DEFAULTORG","ORGRANGE","CREATEOPID","CREATEDEPTID","CREATETIME","LASTMODIOPID","LASTMODITIME"};
				DataValue[] insValue1 = null;
				
				//添加多语言信息 
				if(datas!=null&&!datas.isEmpty()){
					for (level1Elm par : datas) {
					 	int insColCt = 0;  
					  	String[] columnsName = {"OPNO", "LANG_TYPE", "OP_NAME", "STATUS", "EID"};
					  	String langType = par.getLangType();
					  	String lOPName = par.getOpName();
					  	String lstatus = par.getStatus();
					  	   
					  	//验证多语言是否重复或已存在
					  	sql = this.isRepeatLang(staffNO, langType, eId);
					  	List<Map<String, Object>> staffLangDatas = this.doQueryData(sql, null);
					  	if(staffLangDatas.isEmpty()){
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
					  	}
					  	else{
					  		this.pData.clear();
					  		res.setSuccess(false);
							res.setServiceDescription("多语言类型 "+langType+"不能重复");
							break;					
					  	}
					}// 添加多语言信息结束
				}
				
				//添加到 platform_staffs_role 
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
					
					InsBean ib3 = new InsBean("PLATFORM_STAFFS_ROLE", columns3);
					ib3.addValues(insValue3);
					this.addProcessData(new DataProcessBean(ib3));
				}// 添加platForm_staff_role  到此结束
				
				
				//添加到 platform_staffs_shop 
				for(int i = 0; i < orgList.size() ; i++){
					int insColCt3 = 0;
					String[] columnsName3 = {"OPNO", "SHOPID","EID","ISDEFAULT","STATUS","ISEXPAND"};
					String orgNO =orgList.get(i).getOrganizationNo();
                    String isExpand = orgList.get(i).getIsExpand();

                    if(orgNO.trim().isEmpty()){
						continue;
					}
					
					DataValue[] orgColumnsVal = new DataValue[columnsName3.length];
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
				       		insColCt3++;
				       		orgColumnsVal[j] = new DataValue(keyVal, Types.VARCHAR);
				       	} 
				       	else {
				       		orgColumnsVal[j] = null;
				       	}
				        
				 	} 	
				 	String[] columns3  = new String[insColCt3];
					DataValue[] insValue3 = new DataValue[insColCt3];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt3 = 0;
					
					for (int k=0;k<orgColumnsVal.length;k++){
						if (orgColumnsVal[k] != null){
							columns3[insColCt3] = columnsName3[k];
							insValue3[insColCt3] = orgColumnsVal[k];
							insColCt3 ++;
							if (insColCt3 >= insValue3.length) 
								break;
						}
					}
					
					InsBean ib3 = new InsBean("PLATFORM_STAFFS_SHOP", columns3);
					ib3.addValues(insValue3);
					this.addProcessData(new DataProcessBean(ib3));
				}// 添加platForm_staff_role  到此结束


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
                        for (DCP_StaffCreateReq.levelWarehouseElm levelWarehouseElm : warehouseList)
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



				
				//添加原因吗信息
				insValue1 = new DataValue[]{
						new DataValue(staffNO, Types.VARCHAR), 
						new DataValue(password, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(lang, Types.VARCHAR),
						//new DataValue(staffID, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR),
						new DataValue(departNo, Types.VARCHAR),
						new DataValue(staffName, Types.VARCHAR),
						new DataValue(en_cash, Types.VARCHAR),
						new DataValue(viewAbleDay, Types.VARCHAR),
                        new DataValue(telephone, Types.VARCHAR),
                        new DataValue(discPowerType, Types.VARCHAR),
                        new DataValue(disc, Types.VARCHAR),
                        new DataValue(maxFreeAmt, Types.VARCHAR),
                        new DataValue(req.getRequest().getDistributor(), Types.VARCHAR),
                        new DataValue(req.getRequest().getEmployeeNo(), Types.VARCHAR),
                        new DataValue(Check.Null(req.getRequest().getDefaultOrg())?req.getOrganizationNO():req.getRequest().getDefaultOrg(), Types.VARCHAR),
                        new DataValue(req.getRequest().getOrgRange(), Types.VARCHAR),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(req.getDepartmentNo(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),

                };
				
				InsBean ib1 = new InsBean("PLATFORM_STAFFS", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
				
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");	
			}
			else{
				this.pData.clear();
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行失败: 用户编号："+staffNO+" 已存在,请重新输入");	
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.pData.clear();
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	

		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StaffCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StaffCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StaffCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StaffCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    if(req.getRequest()==null)
	    {
	    	errMsg.append("requset不能为空值 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }
		String staffNO = req.getRequest().getStaffNo();
		String password = req.getRequest().getPassword();
		String status = req.getRequest().getStatus();
		String lang = req.getRequest().getLang();

        String discPowerType = req.getRequest().getDiscPowerType();
        String disc = req.getRequest().getDisc();
        String maxFreeAmt = req.getRequest().getMaxFreeAmt();

		
		String staffID = req.getRequest().getStaffID();
		List<level1Elm> datas = req.getRequest().getDatas();	
		if(Check.Null(staffNO)){
			errMsg.append("用户编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(password)){
			errMsg.append("密码不能为空值 ");
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
		if(Check.Null(staffID)){
			errMsg.append("GUID不能为空值 ");
			isFail = true;
		}

        if(Check.Null(discPowerType) || (!discPowerType.equals("ROLE") && !discPowerType.equals("USER")))
        {
            errMsg.append("折扣/免单使用权限类型：值必须是ROLE或者USER");
            isFail = true;
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
		
		if(datas != null&&!datas.isEmpty()){
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
	protected TypeToken<DCP_StaffCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StaffCreateReq>(){};
	}

	@Override
	protected DCP_StaffCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StaffCreateRes();
	}
	
	/**
	 * 验证新增的用户基本信息是否重复
	 * @param req
	 * @param OPNO
	 * @return
	 */
	private String isRepeat(DCP_StaffCreateReq req , String OPNO){
		
		String sql = "SELECT OPNO FROM PLATFORM_STAFFS "
				+ " WHERE OPNO = '"+OPNO+"' "
				+ " AND EID = '"+req.geteId()+"'";
		return sql;
	}
	
	/**
	 * 验证新增的用户多语言信息是否重复或已存在
	 * @param
	 * @param OPNO
	 * @return
	 */
	private String isRepeatLang(String OPNO, String langType , String eId){
		String sql = "SELECT OPNO FROM PLATFORM_STAFFS_LANG "
				+ " WHERE OPNO = '"+OPNO+"' "
				+ " and lang_Type = '"+langType+"'"
				+ " AND EID = '"+eId+"'";
		return sql;
	}
	
	
}
