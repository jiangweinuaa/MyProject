package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_MachineQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_MachineQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_MachineQuery_Open extends SPosBasicService<DCP_MachineQuery_OpenReq, DCP_MachineQuery_OpenRes> {

	@Override
	protected boolean isVerifyFail(DCP_MachineQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_MachineQuery_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MachineQuery_OpenReq>(){};
	}

	@Override
	protected DCP_MachineQuery_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MachineQuery_OpenRes();
	}

	@Override
	protected DCP_MachineQuery_OpenRes processJson(DCP_MachineQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_MachineQuery_OpenRes res = null;
		res = this.getResponse();
		String sql = "";
		try {
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_MachineQuery_OpenRes.level1Elm>());
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("EID", true);	
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
				
				//单头主键字段
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
				condition2.put("EID", true);	
				condition2.put("SHOPID", true);	
				//调用过滤函数
				List<Map<String, Object>> shopDatas = MapDistinct.getMap(getQDataDetail, condition2);
				
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_MachineQuery_OpenRes.level1Elm oneLv1 = new DCP_MachineQuery_OpenRes().new level1Elm();
					String eId = oneData.get("EID").toString();
					oneLv1.seteId(eId);
					oneLv1.setShopList(new ArrayList<DCP_MachineQuery_OpenRes.level2Elm>());
					
					if(shopDatas != null && shopDatas.isEmpty() == false){
						for (Map<String, Object> map : shopDatas) {
							
							if(eId.equals(map.get("EID").toString())){
								DCP_MachineQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
								String shopId = map.get("SHOPID").toString();
								String shopName = map.get("SHOPNAME").toString();
								String orgForm = map.get("ORGFORM").toString();
								
								lv2.setShopId(shopId);
								lv2.setShopName(shopName);
								lv2.setOrgForm(orgForm);
								
								lv2.setMachineList(new ArrayList<DCP_MachineQuery_OpenRes.level3Elm>());
								
								for (Map<String, Object> macDatas : getQDataDetail) {
									if(eId.equals(macDatas.get("EID").toString()) && shopId.equals(macDatas.get("SHOPID").toString())){
										
										DCP_MachineQuery_OpenRes.level3Elm lv3 = res.new level3Elm();
										String machineId = macDatas.get("MACHINEID").toString();
										String machineName = macDatas.get("MACHINENAME").toString();
//										String machineCode = macDatas.get("MACHINECODE").toString();
										
										lv3.setMachineId(machineId);
//										lv3.setMachineCode(machineCode);
										lv3.setMachineName(machineName);
										
										lv2.getMachineList().add(lv3);
									}
									
								}
								
								oneLv1.getShopList().add(lv2);
							}
							
						}
					}

					res.getDatas().add(oneLv1);			
				}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败");
			res.setServiceStatus("200");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_MachineQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;			
		
		String isRegister = req.getRequest().getIsRegister();
		String producttype = req.getRequest().getProducttype();
		
		String langType=req.getLangType();
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( " "
				+ " select EID,SHOPID,shopName, orgFOrm ,machineId,machineName,machinecode,sNumber,isRegister,status from ( "
				+ " select  a.EID,  "
				+ " a.SHOPID,b.org_name as shopName , d.org_form as orgForm , a.machine as machineId, machineName,c.machinecode  , c.customerno as sNumber, c.isregister  , "
				+ " a.status from platform_machine a "
				+ " left join DCP_ORG_lang b on a.EID=b.EID and b.organizationno=a.SHOPID and b.status='100' "
				+ " and b.lang_type='"+ langType +"' "
				+ " left join platform_cregisterdetail c on c.EID=a.EID and c.SHOPID=a.SHOPID and c.machine=a.machine "
				+ " LEFT JOIN DCP_ORG d on a.EID=d.EID and d.organizationno = a.SHOPID and d.status='100' "
				+ " where 1=1 " );
		
		if (isRegister != null && isRegister.length()>0)
		{
			sqlbuf.append(" and c.isregister='"+ isRegister +"' ");
		}
		
		if (producttype != null && producttype.length()>0)
		{
			sqlbuf.append(" and c.producttype='"+ producttype +"' ");
		}
		
		sqlbuf.append( " )  order by SHOPID,machineId   ");
		sql = sqlbuf.toString();
		return sql;
	}

}
