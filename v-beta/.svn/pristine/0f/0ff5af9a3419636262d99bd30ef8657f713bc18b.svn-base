package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ParaTemplateEditReq;
import com.dsc.spos.json.cust.req.DCP_ParaTemplateEditReq.ClassList;
import com.dsc.spos.json.cust.req.DCP_ParaTemplateEditReq.ItemList;
import com.dsc.spos.json.cust.req.DCP_ParaTemplateEditReq.MachineList;
import com.dsc.spos.json.cust.req.DCP_ParaTemplateEditReq.ShopList;
import com.dsc.spos.json.cust.res.DCP_ParaTemplateEditRes;
import com.dsc.spos.json.cust.res.DCP_ParaTemplateEditRes.level1Elm;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * V3模版参数设置#add
 * @author 2020-06-02
 *
 */
public class DCP_ParaTemplateEdit extends SPosAdvanceService<DCP_ParaTemplateEditReq, DCP_ParaTemplateEditRes> {

	@Override
	protected void processDUID(DCP_ParaTemplateEditReq req, DCP_ParaTemplateEditRes res) throws Exception {
		// TODO Auto-generated method stub
				
		try {
			
			res.setDatas(new ArrayList<DCP_ParaTemplateEditRes.level1Elm>());
			
			String opNo = req.getOpNO();
			String opName = req.getOpName();
			
			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String modiDate =  matter.format(dt);
			
			String eId = req.geteId();
			
			String templateId =  req.getRequest().getTemplateId();
			String templateName = req.getRequest().getTemplateName();
			String templateType = req.getRequest().getTemplateType();
			String restrictShop = req.getRequest().getRestrictShop();
			String restrictMachine = req.getRequest().getRestrictMachine();
			
			List<ShopList> shopDatas = req.getRequest().getShopList();
			List<MachineList> machineDatas = req.getRequest().getMachineList();
			List<ClassList> classDatas = req.getRequest().getClassList();
			
			//****************** 验证模板上的参数是否已存在于其他模板，是否已适用于当前所传门店，是否已适用于所传机台 *********************
			
		
			// 新增时验证所有模板上有没有该参数
			String[] itemArr = {} ;
//			int i = 0;
			for (ClassList classList : classDatas) {
				String item = "";
				List<ItemList> itemList = classList.getItemList();
				if(itemList != null && itemList.size() > 0 && !itemList.isEmpty()){
					for (ItemList itemMap : itemList) {
						if(!Check.Null(itemMap.getItem())){
							item = itemMap.getItem();
						}
						itemArr = insert(itemArr, item);
//						i++;
					}
				}
				
			}
			
			itemArr = insert(itemArr, "");
			
			String itemStr = getString(itemArr);
			
			// 获取本次修改的适用门店
			String[] shopArr = new String[shopDatas.size()] ;
			// 新增时验证所有模板上有没有该参数
			int j = 0;
			for (ShopList shopList : shopDatas) {
				String shopId = "";
				
				if(!Check.Null(shopList.getShopId())){
					shopId = shopList.getShopId();
				}
				shopArr[j] = shopId;
				j++;
				
			}
			String shopStr = getString(shopArr);
			
			// 获取本次修改的适用门店
			String[] machineArr = new String[machineDatas.size()] ;
			// 新增时验证所有模板上有没有该参数
			int k = 0;
			for (MachineList machineList : machineDatas) {
				String machineId = "";
				String machineShopId = "";
				
				if(!Check.Null(machineList.getMachineId())){
					machineId = machineList.getMachineId();
				}
				if(!Check.Null(machineList.getShopId())){
					machineShopId = machineList.getShopId();
				}
				machineArr[k] = machineShopId+"_"+machineId;
				k++;
				
			}
			String machineStr = getString(machineArr);
			
			StringBuffer sqlbuf = new StringBuffer();
			String repeatSql = "";
//			sqlbuf.append(" SELECT * "
//					+ " FROM platform_paratemplate pt "
//					+ " LEFT JOIN platform_paratemplate_item pti  ON pt.eId = pti.eid AND pt.templateid = pti.templateid "
//	 
//					+ " LEFT JOIN platform_paratemplate_shop pts1 ON pt.eId = pts1.eId AND pt.templateid = pts1.templateid AND pt.restrictshop = '1' AND pts1.shopid = '01' "
//	 				+ " LEFT JOIN platform_paratemplate_shop pts2 ON pt.eId = pts1.eId AND pt.templateid = pts1.templateid AND pt.restrictshop = '0' "
//	 
//	 				+ " LEFT JOIN platform_paratemplate_machine ptm1 ON pt.eId = ptm1.eId AND pt.templateid = ptm1.templateid AND pt.restrictmachine = '1' AND ptm1.machineid = 'csMachineId'  "
//	 				+ " LEFT JOIN platform_paratemplate_machine ptm2 ON pt.eId = ptm2.eId AND pt.templateid = ptm2.templateid AND pt.restrictmachine = '0'   "
//	 
//	 				+ " WHERE pt.eid = '99' ");
			
			
			
			sqlbuf.append(" SELECT pt.templateId , pt.templatetype , pt.templatename , pt.restrictshop , pt.restrictMachine , "
					+ " pti.item , pti.itemvalue , pts.shopid , ptm.machineid "
					+ " FROM platform_paratemplate pt "
					+ " LEFT JOIN platform_paratemplate_shop pts ON pt.eid = pts.eId AND pt.templateid = pts.templateid "
	 
					+ " LEFT JOIN platform_paratemplate_machine ptm ON pt.eid = ptm.eId AND pt.templateid = ptm.templateid "
					+ " LEFT JOIN platform_paratemplate_item pti ON pt.eid = pti.eid AND pt.templateid = pti.templateid "
	 
					+ " WHERE pt.eid = '"+eId+"' " );
			// 如果请求中的模板编号为空， 表示该模板为新增; 不为空， 表示修改
			if(!Check.Null(req.getRequest().getTemplateId() ) ){ 
				sqlbuf.append(" and pt.templateId != '"+templateId+"' " );
			}
			
		//	sqlbuf.append(" and ( pt.templateType = '"+templateType+"' or  pti.item IN ("+itemStr+")   )");
			// 参数模板适用门店
			sqlbuf.append(" and ( PT.EID = '"+eId+"' AND PT.TEMPLATETYPE = '"+templateType+"' AND pti.item IN ( "+itemStr+"  )"
					+ " and ( pt.restrictshop = '0' " );	
			
			
			//指定门店  (指定门店可以共存，指定和排除互斥可共存各一个)
			if(restrictShop.equals("1") && shopDatas !=null && !shopDatas.isEmpty() && shopDatas.size() > 0){
				sqlbuf.append(" "
						+ " OR ( pt.restrictshop = '1' AND pts.shopid IN ( "+shopStr+")  ) "
						//+ " OR ( pt.restrictShop = '2' AND pts.shopId not in ( "+shopStr+" ))"
						);
			}
			
			//排除门店    (排除门店不可共存 )
			if(restrictShop.equals("2") && shopDatas !=null && !shopDatas.isEmpty() && shopDatas.size() > 0){
				sqlbuf.append(" "
						+ " OR ( pt.restrictshop = '1' AND pts.shopid NOT IN ( "+shopStr+")  ) "
						+ " OR ( pt.restrictshop = '2' )  "
						);
			}
			
			sqlbuf.append(" ) ");
			
			
			
			
			
//			sqlbuf.append(" or ( PT.EID = '"+eId+"' AND PT.TEMPLATETYPE = 'MACHINEPARA' AND pti.item IN ( "+itemStr+"  )"
//					+ " and ( pt.restrictMachine = '0' " );	
//			
//			if(!restrictShop.equals("0") && machineDatas !=null && !machineDatas.isEmpty() && machineDatas.size() > 0){
//				sqlbuf.append(" "
//						+ " OR ( pt.restrictMachine = '1' AND ptm.shopid || ptm.machineid IN( "+machineStr+" )  )  "
//						+ " OR ( pt.restrictMachine = '2' AND ptm.shopid || ptm.machineid NOT IN (  "+machineStr+"  )  )  "
//						);
//			}
//			sqlbuf.append(" ) ) " );
			sqlbuf.append(") " );
			
			
			sqlbuf.append(" order by pt.restrictshop , pt.restrictMachine ,  pt.templateId , pt.templatetype , pt.templatename  ");
			
			// 参数模板适用门店
			repeatSql = sqlbuf.toString();
			
			List<Map<String, Object>> repeatDatas = this.doQueryData(repeatSql, null);
			
			if( classDatas.size() > 0 && classDatas !=null && repeatDatas !=null && !repeatDatas.isEmpty() && repeatDatas.size() > 0 ){
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("TEMPLATEID", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(repeatDatas, condition);
				
				String templateIdStr = "";
				String repeatItemStr = "";
				for (Map<String, Object> map : getQHeader) {
					String repeatShopId = map.get("SHOPID").toString();
					String repeatMachineId = map.get("MACHINEID").toString();
					String repeatTemplateId = map.get("TEMPLATEID").toString();
					String item = map.get("ITEM").toString();
					
					repeatItemStr = item + "/"+repeatItemStr;
					templateIdStr = repeatTemplateId+ "/" + templateIdStr;
				}
				
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,repeatItemStr +"参数已存在于"+templateIdStr+"模板");
			}
			
			//验证相关信息的完整度
			
			//****************** 验证结束  ********************** 
			
			if(Check.Null(req.getRequest().getTemplateId() ) ){ //如果模板编号为空， 说明是新增。不为空就是修改
				//模板编号后端生成， 无特殊规则
				SimpleDateFormat matter2 = new SimpleDateFormat("yyyyMMddHHmmss");
				String itemNo =  matter2.format(new Date());
				
				templateId = "PARA"+itemNo;
				
				String[] tempColumns = {"EID","TEMPLATEID","TEMPLATETYPE","TEMPLATENAME","RESTRICTSHOP","RESTRICTMACHINE","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME" };
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR),
						new DataValue(templateType, Types.VARCHAR) ,
						new DataValue(templateName, Types.VARCHAR) ,
						new DataValue(restrictShop, Types.VARCHAR),
						new DataValue(restrictMachine, Types.VARCHAR), 
						new DataValue(opNo, Types.VARCHAR) ,
						new DataValue(opName, Types.VARCHAR) ,
						new DataValue(modiDate, Types.VARCHAR) 
				};
				InsBean ib1 = new InsBean("PLATFORM_PARATEMPLATE", tempColumns);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));
			}
			else{
				
				//"LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
				UptBean	ub_code = new UptBean("PLATFORM_PARATEMPLATE");		
				ub_code.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));	
				ub_code.addUpdateValue("RESTRICTMACHINE", new DataValue(restrictMachine, Types.VARCHAR));	
				ub_code.addUpdateValue("TEMPLATETYPE", new DataValue(templateType, Types.VARCHAR));	
				ub_code.addUpdateValue("TEMPLATENAME", new DataValue(templateName, Types.VARCHAR));	
				
				ub_code.addUpdateValue("LASTMODIOPID", new DataValue(opNo, Types.VARCHAR));	
				ub_code.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));	
				ub_code.addUpdateValue("LASTMODITIME", new DataValue(modiDate, Types.VARCHAR));	
				
				ub_code.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub_code.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub_code));
				
				DelBean db1 = new DelBean("PLATFORM_PARATEMPLATE_ITEM");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				DelBean db2 = new DelBean("PLATFORM_PARATEMPLATE_SHOP");
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));
				
				DelBean db3 = new DelBean("PLATFORM_PARATEMPLATE_MACHINE");
				db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db3.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db3));
				
				
			}
			
			
//			DelBean db4 = new DelBean("PLATFORM_PARATEMPLATE");
//			db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
//			db4.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
//			this.addProcessData(new DataProcessBean(db4));
			
			
			if(shopDatas != null && !shopDatas.isEmpty()){
				if(restrictShop.equals("1") || restrictShop.equals("2")  ){ //0-全部 1-指定 2-排除
					String[] columns1 = {"EID","TEMPLATEID","SHOPID" };
					for (ShopList lvShop : shopDatas) {
						String shopId = lvShop.getShopId();
						String shopName = lvShop.getShopName();
						DataValue[] insValue1 = null;
						insValue1 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(templateId, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR) 
						};
						InsBean ib1 = new InsBean("PLATFORM_PARATEMPLATE_SHOP", columns1);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1));
					}
				}
				
			}
			
			
			// machineList 节点
			if(machineDatas != null && !machineDatas.isEmpty()){
				if(restrictMachine.equals("1") || restrictMachine.equals("2")  ){ //0-全部 1-指定 2-排除
					String[] columns1 = {"EID","TEMPLATEID","SHOPID","MACHINEID" };
					for (MachineList lvMachine : machineDatas) {
						String machineId = lvMachine.getMachineId();
						String shopId = lvMachine.getShopId();
						DataValue[] insValue1 = null;
						insValue1 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(templateId, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR) ,
								new DataValue(machineId, Types.VARCHAR) 
						};
						InsBean ib1 = new InsBean("PLATFORM_PARATEMPLATE_MACHINE", columns1);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1));
					}
				}
				
			}
			
			// classList 节点
			if(classDatas != null && !classDatas.isEmpty()){
				String[] columns1 = {"EID","TEMPLATEID","ITEM","ITEMVALUE" };
				for (ClassList lvClass : classDatas) {
					
					List<ItemList> itemDatas = lvClass.getItemList();
					
					if(itemDatas != null && !itemDatas.isEmpty()){
						for (ItemList lvItem : itemDatas) {
							String item = lvItem.getItem();
							String itemValue = lvItem.getItemValue();
							
							DataValue[] insValue1 = null;
							insValue1 = new DataValue[]{
									new DataValue(eId, Types.VARCHAR),
									new DataValue(templateId, Types.VARCHAR),
									new DataValue(item, Types.VARCHAR) ,
									new DataValue(itemValue, Types.VARCHAR) 
							};
							InsBean ib1 = new InsBean("PLATFORM_PARATEMPLATE_ITEM", columns1);
							ib1.addValues(insValue1);
							this.addProcessData(new DataProcessBean(ib1));
						}
					}
					
				}
				
			}
						
			this.doExecuteDataToDB();
			

//			List<DCP_ParaTemplateEditRes.level1Elm> resLv1 = new ArrayList<DCP_ParaTemplateEditRes.level1Elm>();
			DCP_ParaTemplateEditRes.level1Elm datasLv1 = res.new level1Elm();
			datasLv1.setTemplateId(templateId);
			datasLv1.setTemplateName(templateName);
			datasLv1.setRestrictShop(restrictShop);
			datasLv1.setRestrictMachine(restrictMachine);
//			datasLv1.setShopList(new ArrayList<DCP_ParaTemplateEditReq.ShopList>());
//			datasLv1.setMachineList(new ArrayList<DCP_ParaTemplateEditReq.MachineList>() );
//			datasLv1.setClassList(new ArrayList<DCP_ParaTemplateEditReq.ClassList>());
			
			datasLv1.setShopList(shopDatas);
			datasLv1.setMachineList(machineDatas);
			datasLv1.setClassList(classDatas);
//			resLv1.add(datasLv1);
			res.getDatas().add(datasLv1);
			
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ParaTemplateEditReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ParaTemplateEditReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ParaTemplateEditReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ParaTemplateEditReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；	
		if (Check.Null(req.getRequest().getTemplateName())) 
		{
			errCt++;
			errMsg.append("templateName模板名称不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(req.getRequest().getTemplateType())) 
		{
			errCt++;
			errMsg.append("templateType模板参数类型不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(req.getRequest().getRestrictShop())) 
		{
			errCt++;
			errMsg.append("restrictShop限定门店不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(req.getRequest().getRestrictMachine())) 
		{
			errCt++;
			errMsg.append("restrictMachine限定机台不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ParaTemplateEditReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ParaTemplateEditReq>(){};
	}

	@Override
	protected DCP_ParaTemplateEditRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ParaTemplateEditRes();
	}

	
	private String queryMaxTempId (DCP_ParaTemplateEditReq req){
		
		return null;
	}
	
	
	protected String getString(String[] str)
	{
		String str2 = "";
		for (String s:str)
		{
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		return str2;
	}
	
	
	private static String[] insert(String[] arr, String str)
	{
		int size = arr.length;
		String[] tmp = new String[size + 1];
		System.arraycopy(arr, 0, tmp, 0, size);
		tmp[size] = str;
		return tmp;
	}
	
	
	
}
