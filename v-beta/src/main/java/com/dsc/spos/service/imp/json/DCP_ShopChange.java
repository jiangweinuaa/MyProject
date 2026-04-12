package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CRegisterCheckReq;
import com.dsc.spos.json.cust.req.DCP_LoginRetailReq;
import com.dsc.spos.json.cust.req.DCP_ShopChangeReq;
import com.dsc.spos.json.cust.req.DCP_ShopChangeReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_CRegisterCheckRes;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes;
import com.dsc.spos.json.cust.res.DCP_RegisterRes;
import com.dsc.spos.json.cust.res.DCP_StaffQueryRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.TokenManagerRetail;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.Login_ShopChange;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.Register;
import com.google.gson.reflect.TypeToken;
import com.spreada.utils.chinese.ZHConverter;

public class DCP_ShopChange extends SPosAdvanceService<DCP_ShopChangeReq, DCP_LoginRetailRes> {
	
	String sysReportURL = "";
	String statementURL = "";
	
	@Override
	protected void processDUID(DCP_ShopChangeReq req, DCP_LoginRetailRes res) throws Exception {
		
		String token = "";
		// 获取原始登录信息
		TokenManagerRetail tmr=new TokenManagerRetail();
		DCP_LoginRetailRes resLogin = tmr.getLoginData(req.getToken());
		res.setDatas(new ArrayList<DCP_LoginRetailRes.level1Elm>());
		Login_ShopChange LSC = new Login_ShopChange();
		
		level1Elm request = req.getRequest();
		String loginType = request.getLoginType();
        //if(Check.Null(loginType)){
         //   loginType="1";
        //}
		// 取得 SQL
		String sql = null;
		
		String sORG_FORM = "";
		res.setDatas(new ArrayList<DCP_LoginRetailRes.level1Elm>());
		
		// 管辖门店
		// 添加门店层级，查找组织表
        String orgRange = req.getOrgRange();
        sql = LSC.getQueryStaffShopSql(req.geteId(), req.getOpNO(), req.getLangType());
        if("0".equals(orgRange)){
            sql=LSC.getQueryStaffShopAllSql(req.geteId(), req.getOpNO(), req.getLangType());
        }
        List<Map<String, Object>> getQData2 = this.doQueryData(sql,null);
		
		//		sysReportURL = "";// 报表路径:http://172.16.100.153:8080/WebReport/ReportServer?reportlet=myWebReports
		//		String sqlPara = "select ITEMVALUE from platform_basesettemp where item='sysReportURL' and EID='"
		//				+ req.geteId() + "'";
		//		List<Map<String, Object>> getPara = this.doQueryData(sqlPara, null);
		//		if (getPara == null || getPara.isEmpty() == true) {
		//			// throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
		//			// "报表参数sysReportURL未设置！");
		//		} else {
		//			sysReportURL = getPara.get(0).get("ITEMVALUE").toString();
		//		}
		
		///参数改取公用方法  BY JZMA 20200820 
		sysReportURL=PosPub.getPARA_SMS(dao, req.geteId(), "", "sysReportURL");
		statementURL=PosPub.getPARA_SMS(dao, req.geteId(), "", "statementURL");
		
		//		String BaseDataSourceERP = "Y";// 报表路径:http://172.16.100.153:8080/WebReport/ReportServer?reportlet=myWebReports
		//		sqlPara = "select ITEMVALUE from platform_basesettemp where item='BaseDataSourceERP' and EID='"
		//				+ req.geteId() + "'";
		//		getPara = this.doQueryData(sqlPara, null);
		//		if (getPara == null || getPara.isEmpty() == true) {
		//			;
		//		} else {
		//			BaseDataSourceERP = getPara.get(0).get("ITEMVALUE") == null || getPara.get(0).get("ITEMVALUE").toString().isEmpty()
		//					? " " : getPara.get(0).get("ITEMVALUE").toString();
		//		}
		
		///参数改取公用方法  BY JZMA 20200820 
		String BaseDataSourceERP=PosPub.getPARA_SMS(dao, req.geteId(), "", "BaseDataSourceERP");
		if (Check.Null(BaseDataSourceERP))
			BaseDataSourceERP = "Y";
		
		DCP_LoginRetailRes.level1Elm oneLv1 = new DCP_LoginRetailRes().new level1Elm();
		
		String eId = req.geteId();
		String langType = req.getLangType();
		String bDate = this.getLoginData().getDatas().get(0).getbDate();
		String staffNO = req.getOpNO();
		String staffName = req.getOpName();
		String viewAbleDay = req.getViewAbleDay();
		String defDepartNo = req.getDefDepartNo();
		String defDepartName = req.getDefDepartName();
		String chatUserId = req.getChatUserId();

		String employeeNo = req.getEmployeeNo();
        String employeeName = req.getEmployeeName();
        String departmentNo = req.getDepartmentNo();
        String departmentName = req.getDepartmentName();
        String defaultOrg = req.getDefaultOrg();
        String belOrgNo = req.getBelOrgNo();
        String belOrgName = req.getBelOrgName();
        String upDepartNo = req.getUpDepartNo();

        if (Check.Null(chatUserId)) {
			chatUserId = "";
		}
		// 處理調整回傳值
		// 是否存在当前员工默认门店
		boolean bExitDefaultShop = false;
		oneLv1.setMyShops(new ArrayList<DCP_LoginRetailRes.level2Shops>());
		oneLv1.setShopId("");
		oneLv1.setShopName("");
		oneLv1.setOrg_Form("");
		
		Map<String, Object> mapChangeShop = new HashMap<String, Object>();
		mapChangeShop.put("SHOPID", request.getoShopId());
		List<Map<String, Object>> getChangeShop = MapDistinct.getWhereMap(getQData2, mapChangeShop, false);

		//sORG_FORM 应该取默认门店的
		if (getChangeShop != null && getChangeShop.isEmpty() == false) {
			bExitDefaultShop = true;
			sORG_FORM = getChangeShop.get(0).get("ORG_FORM").toString();
			oneLv1.setShopId(getChangeShop.get(0).get("SHOPID").toString());
			oneLv1.setShopName(getChangeShop.get(0).get("SHOPNAME").toString());
			oneLv1.setOrganizationNo(getChangeShop.get(0).get("SHOPID").toString());
			oneLv1.setOrg_Form(sORG_FORM);
			oneLv1.setDisCentre(getChangeShop.get(0).get("DISCENTRE").toString());
			oneLv1.setOrg_type(getChangeShop.get(0).get("ORG_TYPE").toString());
			
			oneLv1.setIn_cost_warehouse(getChangeShop.get(0).get("IN_COST_WAREHOUSE").toString());
			oneLv1.setIn_non_cost_warehouse(getChangeShop.get(0).get("IN_NON_COST_WAREHOUSE").toString());
			oneLv1.setOut_cost_warehouse(getChangeShop.get(0).get("OUT_COST_WAREHOUSE").toString());
			oneLv1.setOut_non_cost_warehouse(getChangeShop.get(0).get("OUT_NON_COST_WAREHOUSE").toString());
			oneLv1.setInv_cost_warehouse(getChangeShop.get(0).get("INV_COST_WAREHOUSE").toString());
			oneLv1.setInv_non_cost_warehouse(getChangeShop.get(0).get("INV_NON_COST_WAREHOUSE").toString());
            oneLv1.setReturn_cost_warehouse(getChangeShop.get(0).get("RETURN_COST_WAREHOUSE").toString());
            oneLv1.setReturn_cost_warehouse_name(getChangeShop.get(0).get("RETURN_COST_WAREHOUSENAME").toString());
			oneLv1.setIn_cost_warehouse_name(getChangeShop.get(0).get("IN_COST_WAREHOUSENAME").toString());
			oneLv1.setIn_non_cost_warehouse_name(getChangeShop.get(0).get("IN_NON_COST_WAREHOUSENAME").toString());
			oneLv1.setOut_cost_warehouse_name(getChangeShop.get(0).get("OUT_COST_WAREHOUSENAME").toString());
			oneLv1.setOut_non_cost_warehouse_name(getChangeShop.get(0).get("OUT_NON_COST_WAREHOUSENAME").toString());
			oneLv1.setInv_cost_warehouse_name(getChangeShop.get(0).get("INV_COST_WAREHOUSENAME").toString());
			oneLv1.setInv_non_cost_warehouse_name(getChangeShop.get(0).get("INV_NON_COST_WAREHOUSENAME").toString());
			
			oneLv1.setCITY(getChangeShop.get(0).get("CITY").toString());
			oneLv1.setDISTRICT(getChangeShop.get(0).get("DISTRICT").toString());
			oneLv1.setENABLECREDIT(getChangeShop.get(0).get("ENABLECREDIT").toString());
			
			
			///if Org_Form==0 公司时，所属公司BELFIRM 就等于组织自己  BY JZMA 20200720 
			String belfirm = getChangeShop.get(0).get("BELFIRM").toString();
			String belfirmName = getChangeShop.get(0).get("BELFIRM_NAME").toString();
			if (sORG_FORM.equals("0") && Check.Null(belfirm)) {
				oneLv1.setBELFIRM(getChangeShop.get(0).get("SHOPID").toString());
				oneLv1.setBELFIRM_NAME(getChangeShop.get(0).get("SHOPNAME").toString());
			} else {
				oneLv1.setBELFIRM(belfirm);
				oneLv1.setBELFIRM_NAME(belfirmName);
			}
			
			//			List<Map<String, Object>> para = this
			//					.doQueryData("select ITEMVALUE from platform_basesettemp where item='MultiWarehouse' and EID='"
			//							+ req.geteId() + "'", null);
			//			if (para != null && para.size() > 0) {
			//				oneLv1.setMultiWarehouse(para.get(0).get("ITEMVALUE").toString());
			//			} else {
			//				oneLv1.setMultiWarehouse("N");
			//			}
			
			
			///参数改取公用方法  BY JZMA 20200820 
			String MultiWarehouse = PosPub.getPARA_SMS(dao, req.geteId(), "", "MultiWarehouse");
			if (!Check.Null(MultiWarehouse)) {
				oneLv1.setMultiWarehouse(MultiWarehouse);
			} else {
				oneLv1.setMultiWarehouse("N");
			}
			
			
			
			// 启用多语言
			//			para = this.doQueryData("select ITEMVALUE from platform_basesettemp where item='EnableMultiLang' and EID='"
			//					+ req.geteId() + "'", null);
			//			if (para != null && para.size() > 0) {
			//				oneLv1.setEnableMultiLang(para.get(0).get("ITEMVALUE").toString());
			//			} else {
			//				oneLv1.setEnableMultiLang("N");
			//			}
			
			
			///参数改取公用方法  BY JZMA 20200820 
			String EnableMultiLang=PosPub.getPARA_SMS(dao, req.geteId(), "", "EnableMultiLang");
			if (!Check.Null(EnableMultiLang)) {
				oneLv1.setEnableMultiLang(EnableMultiLang);
			} else {
				oneLv1.setEnableMultiLang("N");
			}
			
			
			
			// 单身分页大小
			String PageSizeDetail = PosPub.getPARA_SMS(dao, req.geteId(), "", "PageSizeDetail");
			if (Check.Null(PageSizeDetail)) {
				PageSizeDetail = "50";
			}
			oneLv1.setPageSizeDetail(PageSizeDetail);
			// tempWarehouse=null;
			oneLv1.setToken(token);
		}
		
		oneLv1.seteId(eId);
		oneLv1.setbDate(bDate);
		oneLv1.setLangType(langType);
		oneLv1.setOpNo(staffNO);
		oneLv1.setOpName(staffName);
		oneLv1.setDayType("0");
		oneLv1.setViewAbleDay(viewAbleDay);
		oneLv1.setDefDepartName(defDepartName);
		oneLv1.setDefDepartNo(defDepartNo);
		oneLv1.setChatUserId(chatUserId);
		oneLv1.setOrgRange(orgRange);
		oneLv1.setEmployeeNo(employeeNo);
        oneLv1.setEmployeeName(employeeName);
        oneLv1.setDepartmentName(departmentName);
		oneLv1.setDefaultOrg(defaultOrg);
		oneLv1.setDepartmentNo(departmentNo);
        oneLv1.setBelOrgNo(belOrgNo);
        oneLv1.setBelOrgName(belOrgName);
        oneLv1.setUpDepartNo(upDepartNo);

		String orgSql=getOrgSql(req);
		List<Map<String, Object>> allOrgList = this.doQueryData(orgSql, null);

		// 管辖门店加入
		for (Map<String, Object> oneData2 : getQData2) {
			String shopId = oneData2.get("SHOPID").toString();
			String pshopName = oneData2.get("SHOPNAME").toString();
			String porg_Form = oneData2.get("ORG_FORM").toString();
			String org_type = oneData2.get("ORG_TYPE").toString();
			String isDefault = oneData2.get("ISDEFAULT").toString();
			String DISCENTRE= oneData2.get("DISCENTRE").toString();

			
			///if Org_Form==0 公司时，所属公司BELFIRM 就等于组织自己  BY JZMA 20200720 
			String bELFIRM = oneData2.get("BELFIRM").toString();
			String bELFIRM_NAME= oneData2.get("BELFIRM_NAME").toString();
			if (porg_Form.equals("0") && Check.Null(bELFIRM)) {
				bELFIRM = shopId;
				bELFIRM_NAME = pshopName;
			}
			

			DCP_LoginRetailRes.level2Shops shops = new DCP_LoginRetailRes().new level2Shops();
            isDefault=shopId.equals(oneLv1.getDefaultOrg())?"Y":"N";
			shops.setIsDefault(isDefault);
			shops.setOrgNo(shopId);
			shops.setOrgName(pshopName);
			shops.setOrg_Form(porg_Form);
			shops.setDisCentre(DISCENTRE);

			//用户管辖组织范围='0'（全部组织），则返回营运组织树所有组织；若='1'（指定组织），则判断1-2-2；
			//1-2-2.用户管辖组织 isexpand='1'（全部下展），则返回营运组织树中该组织的所有下级组织；否则chilren[]节点返回空
			List<DCP_LoginRetailRes.level2Shops> filterRows = oneLv1.getMyShops().stream().filter(x -> x.getOrgNo().equals(shops.getOrgNo())).collect(Collectors.toList());
			if(filterRows.size()<=0){
				//有嵌套的就会多次添加，所以需要过滤一下
				oneLv1.getMyShops().add(shops);
			}
			if(orgRange.equals("0")){

				expendChildren(oneLv1,shops,allOrgList,req);

			}
			else{
				Object isexpand = oneData2.get("ISEXPAND");
				if (isexpand != null)
				{
					shops.setIsExpend(isexpand.toString());
					if(isexpand.toString().equals("1")){
						//下展
						expendChildren(oneLv1,shops,allOrgList,req);
					}

				}
			}

			//sORG_FORM 取到切换门店的org_form
			if(!Check.Null(oneLv1.getShopId())){
				sORG_FORM=oneLv1.getOrg_Form();
                //获取组织信息
                String queryDefaultShopSql = this.getQueryDefaultShopSql(req.geteId(), oneLv1.getShopId(), req.getLangType());
                List<Map<String, Object>> list = this.doQueryData(queryDefaultShopSql, null);

                if(list.size()>0){
					Map<String, Object> singleMap = list.get(0);
				    oneLv1.setDisCentre(singleMap.get("DISCENTRE").toString());
					oneLv1.setOrg_type(singleMap.get("ORG_TYPE").toString());

					oneLv1.setIn_cost_warehouse(singleMap.get("IN_COST_WAREHOUSE").toString());
					oneLv1.setIn_non_cost_warehouse(singleMap.get("IN_NON_COST_WAREHOUSE").toString());
					oneLv1.setOut_cost_warehouse(singleMap.get("OUT_COST_WAREHOUSE").toString());
					oneLv1.setOut_non_cost_warehouse(singleMap.get("OUT_NON_COST_WAREHOUSE").toString());
					oneLv1.setInv_cost_warehouse(singleMap.get("INV_COST_WAREHOUSE").toString());
					oneLv1.setInv_non_cost_warehouse(singleMap.get("INV_NON_COST_WAREHOUSE").toString());
                    oneLv1.setReturn_cost_warehouse(singleMap.get("RETURN_COST_WAREHOUSE").toString());
                    oneLv1.setReturn_cost_warehouse_name(singleMap.get("RETURN_COST_WAREHOUSENAME").toString());
					oneLv1.setIn_cost_warehouse_name(singleMap.get("IN_COST_WAREHOUSENAME").toString());
					oneLv1.setIn_non_cost_warehouse_name(singleMap.get("IN_NON_COST_WAREHOUSENAME").toString());
					oneLv1.setOut_cost_warehouse_name(singleMap.get("OUT_COST_WAREHOUSENAME").toString());
					oneLv1.setOut_non_cost_warehouse_name(singleMap.get("OUT_NON_COST_WAREHOUSENAME").toString());
					oneLv1.setInv_cost_warehouse_name(singleMap.get("INV_COST_WAREHOUSENAME").toString());
					oneLv1.setInv_non_cost_warehouse_name(singleMap.get("INV_NON_COST_WAREHOUSENAME").toString());

					oneLv1.setCITY(singleMap.get("CITY").toString());
					oneLv1.setDISTRICT(singleMap.get("DISTRICT").toString());
					oneLv1.setENABLECREDIT(singleMap.get("ENABLECREDIT").toString());


					///if Org_Form==0 公司时，所属公司BELFIRM 就等于组织自己  BY JZMA 20200720
					String belfirm = singleMap.get("BELFIRM").toString();
					String belfirmName = singleMap.get("BELFIRM_NAME").toString();
					if (sORG_FORM.equals("0") && Check.Null(belfirm)) {
						oneLv1.setBELFIRM(singleMap.get("SHOPID").toString());
						oneLv1.setBELFIRM_NAME(singleMap.get("SHOPNAME").toString());
					} else {
						oneLv1.setBELFIRM(belfirm);
						oneLv1.setBELFIRM_NAME(belfirmName);
					}

					String MultiWarehouse = PosPub.getPARA_SMS(dao, req.geteId(), "", "MultiWarehouse");
					if (!Check.Null(MultiWarehouse)) {
						oneLv1.setMultiWarehouse(MultiWarehouse);
					} else {
						oneLv1.setMultiWarehouse("N");
					}

					String EnableMultiLang=PosPub.getPARA_SMS(dao, req.geteId(), "", "EnableMultiLang");
					if (!Check.Null(EnableMultiLang)) {
						oneLv1.setEnableMultiLang(EnableMultiLang);
					} else {
						oneLv1.setEnableMultiLang("N");
					}
				}

            }



			// 加入
			//oneLv1.getMyShops().add(shops);
			
		}

		for (Map<String, Object> oneData2 : getQData2) {
			String shopId = oneData2.get("SHOPID").toString();
			String pshopName = oneData2.get("SHOPNAME").toString();
			String porg_Form = oneData2.get("ORG_FORM").toString();
			String org_type = oneData2.get("ORG_TYPE").toString();
			String isDefault = oneData2.get("ISDEFAULT").toString();
			String DISCENTRE= oneData2.get("DISCENTRE").toString();


			///if Org_Form==0 公司时，所属公司BELFIRM 就等于组织自己  BY JZMA 20200720
			String bELFIRM = oneData2.get("BELFIRM").toString();
			String bELFIRM_NAME= oneData2.get("BELFIRM_NAME").toString();
			if (porg_Form.equals("0") && Check.Null(bELFIRM)) {
				bELFIRM = shopId;
				bELFIRM_NAME = pshopName;
			}
			// 默认第一条门店管辖门店信息
			if (bExitDefaultShop == false && oneLv1.getShopId().equals("")) {
				oneLv1.setShopId(shopId);
				oneLv1.setShopName(pshopName);
				oneLv1.setOrganizationNo(shopId);
				oneLv1.setOrg_Form(porg_Form);
				oneLv1.setBELFIRM(bELFIRM);
				oneLv1.setBELFIRM_NAME(bELFIRM_NAME);
				oneLv1.setOrg_type(org_type);
			}

		}
		
		String orgformtemp = "";
		
		// 系统参数SQL
		String sqlParas = "";
		//处理==绑定变量SQL的写法
		List<DataValue> lstDV=new ArrayList<>();
		DataValue dv=null;
		
		// 菜单权限
		switch (sORG_FORM) {
			case "2":
				// 门店
				orgformtemp = "1";
				//原来查门店参数
				//			sqlParas = "select a.item as PARANAME, nvl(b.itemvalue, a.ITEMVALUE) as PARAVALUE "
				//					+ "from platform_basesettemp a " + "left join platform_baseset b on a.EID = b.EID "
				//					+ "and a.item = b.item " + "and b.status='100' " + "and b.SHOPID = '" + oneLv1.getShopId() + "' "
				//					+ "where a.EID = '" + oneLv1.geteId() + "' " + "and a.status='100' ";
				//			sqlParas = "select item as PARANAME,ITEMVALUE as PARAVALUE " + "from platform_basesettemp "
				//					+ "where EID = '" + oneLv1.geteId() + "' " + "and status='100' ";
				

				
				break;
			case "0":
				orgformtemp = "3";
				
				sqlParas = "select item as PARANAME,ITEMVALUE as PARAVALUE " + "from platform_basesettemp "
						+ "where EID = ? " + "and status='100' ";
				
				//?问号参数赋值处理
				//dv = new DataValue(oneLv1.geteId(), Types.VARCHAR);
				//lstDV.add(dv);
				
				break;
			case "1":
				orgformtemp = "2";
				break;
			default:
				orgformtemp = "1";
				break;
		}

        sqlParas = ""
                + " SELECT nvl(t.item , e.item ) AS PARANAME , NVL(t.itemvalue , e.itemvalue) AS PARAVALUE "
                + " FROM PLATFORM_BASESETTEMP e "
                + " LEFT JOIN ( "
                + " SELECT a.eId, a.templateId , a.restrictshop , a.restrictmachine, b.item , b.itemvalue "
                + " FROM PLATFORM_PARATEMPLATE a  "
                + " LEFT JOIN PLATFORM_PARATEMPLATE_ITEM b ON a.eId = b.eId AND a.templateid = b.templateid"
                + " LEFT JOIN PLATFORM_PARATEMPLATE_SHOP c ON a.eId = c.Eid AND a.templateid = c.templateid"
                + " LEFT JOIN PLATFORM_PARATEMPLATE_MACHINE d ON a.eid = d.eid AND a.templateid = d.templateid"
                + " WHERE a.eid = ? "
                + " and ((a.restrictshop = '1' AND c.shopid = ?) or a.restrictshop = '0') "
                //【ID1034132】【潮品3.0】要货上限控制，门店切换服务，返回的参数重复，导致前端取值不对，影响了用户使用  by jinzma 20230619
                + " and a.templateid not in (select a.templateId from PLATFORM_PARATEMPLATE a"
                + " INNER JOIN PLATFORM_PARATEMPLATE_SHOP b1 ON a.eId = b1.Eid AND a.templateid = b1.templateid"
                + " where a.eid=? AND a.restrictshop = '2' and b1.shopid=?) "
                + " ) t ON e.eId = t.eId AND e.item = t.item "
                + " WHERE e.eid = ? "
                + " AND e.status = '100' "
                //+ " AND  upper(e.item) = '"+upperPara+"'"
                + " ORDER BY e.item "
                + "";


        //?问号参数赋值处理
        dv = new DataValue(eId, Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv = new DataValue(oneLv1.getShopId(), Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv = new DataValue(eId, Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv = new DataValue(oneLv1.getShopId(), Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv = new DataValue(eId, Types.VARCHAR);
        lstDV.add(dv);

		
		// 处理系统参数
		oneLv1.setDataParas(new ArrayList<DCP_LoginRetailRes.paras>());
		if (sqlParas.equals("") == false) {
			List<Map<String, Object>> getQParas = this.executeQuerySQL_BindSQL(sqlParas,lstDV);
			if (getQParas != null && getQParas.size() > 0) {
				for (Map<String, Object> oneParas : getQParas) {
					DCP_LoginRetailRes.paras lParas = new DCP_LoginRetailRes().new paras();
					lParas.setParaName(oneParas.get("PARANAME").toString());
					lParas.setParaValue(oneParas.get("PARAVALUE").toString());
					
					oneLv1.getDataParas().add(lParas);
				}
			}
			getQParas = null;
		}
        //移动门店
        if ( "2".equals(loginType))
        {
            orgformtemp="7";
        }
		//登录应用类型=云中台，全部统一返回菜单所属系统Stype=[3-总部后台]的菜单目录
		if ( "1".equals(loginType))
		{
			orgformtemp="3";
		}
        //门店要显示门店的权限
        if(!"2".equals(sORG_FORM)) {
            orgformtemp = "3";
        }
		//
		String	sqlModular="";
		if(oneLv1.getDisCentre()!=null&&oneLv1.getDisCentre().equals("Y")) {
			sqlModular = LSC.getQueryModularFunctionSql(req.geteId(), req.getOpNO(), orgformtemp,//"4"
					BaseDataSourceERP, req.getLangType());
		} else {
			sqlModular = LSC.getQueryModularFunctionSql(req.geteId(), req.getOpNO(), orgformtemp,
					BaseDataSourceERP, req.getLangType());
		}
		
		//		String sqlModular = LSC.getQueryModularFunctionSql(req.geteId(), req.getOpNO(), orgformtemp,
		//				BaseDataSourceERP, req.getLangType());
		
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sqlModular, null);
		oneLv1.setMyPower(new ArrayList<DCP_LoginRetailRes.level2Powers>());
		
		// 新增是否可显示配送价 BY JZMA 2019/7/16
		String isShowDistriPrice = "N";
		
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
			// 新增是否可显示配送价 BY JZMA 2019/7/16
			isShowDistriPrice = getQDataDetail.get(0).get("ISSHOWDISTRIPRICE").toString();
			if (Check.Null(isShowDistriPrice) || !isShowDistriPrice.equals("Y")) {
				isShowDistriPrice = "N";
			}
			
			// 单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
			condition.put("MODULARNO", true);
			
			// 调用过滤函数
			List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);
			
			oneLv1.setMyPower(new ArrayList<DCP_LoginRetailRes.level2Powers>());
			
			// **********************
			// **DCP_MODULAR要分3级处理**
			// **********************
			
			/////////////////////////////////////////////////////////////////////////////
			// 一级菜单
			Map<String, Object> map_condition = new HashMap<String, Object>(); // 查詢條件
			map_condition.put("MODULARLEVEL", "1");
			List<Map<String, Object>> getQHeader1 = MapDistinct.getWhereMap(getQHeader, map_condition, true);
			
			for (Map<String, Object> oneData3 : getQHeader1) {
				DCP_LoginRetailRes.level2Powers oneLv2Power = new DCP_LoginRetailRes().new level2Powers();
				
				String modularNO = oneData3.get("MODULARNO").toString();
				String modularName = oneData3.get("MODULARNAME").toString();
				String modularLevel = oneData3.get("MODULARLEVEL").toString();
				String upModularNO = oneData3.get("UPMODULARNO").toString();
				String isCollection = oneData3.get("ISCOLLECTION").toString();
				String mProName = oneData3.get("MPRONAME").toString();
				if(req.getLangType().equals("zh_TW")) {
					mProName =ZHConverter.convert(mProName,0);
				}
				String mftype = oneData3.get("MFTYPE").toString();
				String mparameter = oneData3.get("MPARAMETER").toString();
				String isMask = oneData3.get("ISMASK").toString();
				// 模块编码
				Map<String, Object> modularno_condition = new HashMap<String, Object>(); // 查詢條件
				
				oneLv2Power.setModularNo(modularNO);
				oneLv2Power.setModularName(modularName);
				oneLv2Power.setModularLevel(modularLevel);
				oneLv2Power.setUpModularNo(upModularNO);
				oneLv2Power.setIsCollection(isCollection);
				if("4".equals(mftype)) {
					oneLv2Power.setSysReportURL(statementURL); //自建报表地址
				}else {
					oneLv2Power.setSysReportURL(sysReportURL); //帆软报表地址
				}
				oneLv2Power.setProName(mProName);
				oneLv2Power.setMftype(mftype);
				oneLv2Power.setMparameter(mparameter);
				oneLv2Power.setIsMask(isMask);
				oneLv2Power.setDatas(new ArrayList<DCP_LoginRetailRes.lv_Func>());
				
				oneLv2Power.setChildren(new ArrayList<DCP_LoginRetailRes.level2Powers>());
				
				// 添加一级菜单的func
				for (Map<String, Object> fstFuncDatas : getQHeader) {
					// 过滤属于此单头的明细
					if (modularNO.equals(fstFuncDatas.get("MODULARNO")) == false)
						continue;
					// 在这里过滤除属于第一级的func
					// ModularGetDCPRes.function fstFunc = new
					// ModularGetDCPRes.function();
					DCP_LoginRetailRes.lv_Func fstFunc = new DCP_LoginRetailRes().new lv_Func();
					
					String funcNO = fstFuncDatas.get("FUNCNO").toString();
					if (funcNO.trim().equals(""))
						continue;// 过滤掉空值
					
					String funName = fstFuncDatas.get("FUNNAME").toString();
					String PowerType = fstFuncDatas.get("POWERTYPE").toString();
					String fProName = fstFuncDatas.get("FPRONAME").toString();
					
					fstFunc.setFunctionNo(funcNO);
					fstFunc.setFunctionName(funName);
					fstFunc.setPowerType(PowerType);
					fstFunc.setProName(fProName);
					
					oneLv2Power.getDatas().add(fstFunc);
					// oneLv2Power.getFunction().add(fstFunc);
					
				} // 添加一级菜单func结束
				
				setChildrenDatas(oneLv2Power, getQDataDetail,langType);
				oneLv1.getMyPower().add(oneLv2Power);
				
			}
			map_condition = null;
			getQHeader1 = null;
			getQHeader = null;
			/////////////////////////////////////////////////////////////////////////////
			
		}
		
		getQDataDetail = null;
		// 新增是否可显示配送价 BY JZMA 2019/7/16
		oneLv1.setIsShowDistriPrice(isShowDistriPrice);
		oneLv1.setViewAbleDay(viewAbleDay);

        if(Check.NotNull(oneLv1.getOrganizationNo())){
            StringBuffer sb=new StringBuffer("select a.corp,b.org_name as corpname,b1.TAXPAYER_TYPE,b1.OUTPUTTAX as outPutTaxCode,b1.INPUT_TAXCODE as inputTaxCode," +
                    " c.taxrate as outputtaxrate,d.taxrate as inputtaxrate,e.taxname as outputtaxname,f.taxname as inputtaxname,d.taxcaltype as inputtaxcaltype,d.incltax as inputtaxincltax,c.taxcaltype as outputtaxcaltype ,c.incltax as outputtaxincltax " +
                    " from dcp_org a " +
                    " left join dcp_org_lang b on a.eid=b.eid and a.corp=b.organizationno and b.lang_type='"+req.getLangType()+"'" +
                    " left join dcp_org b1 on a.eid=b1.eid and a.corp=b1.organizationno" +
                    " left join DCP_TAXCATEGORY c on c.eid=a.eid and c.taxcode=b1.outputtax and c.taxarea='CN' " +
                    " left join DCP_TAXCATEGORY d on d.eid=a.eid and d.taxcode=b1.input_taxcode and d.taxarea='CN' " +

                    " left join DCP_TAXCATEGORY_lang e on e.eid=a.eid and e.taxcode=b1.outputtax and e.taxarea='CN' and e.lang_type='"+langType+"' " +
                    " left join DCP_TAXCATEGORY_lang f on f.eid=a.eid and f.taxcode=b1.input_taxcode and f.taxarea='CN' and f.lang_type='"+langType+"' " +

                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+oneLv1.getOrganizationNo()+"' ");
            List<Map<String, Object>> getData_corp = this.doQueryData(sb.toString(), null);
            if(getData_corp!=null && getData_corp.size()>0)
            {
                oneLv1.setCorp(getData_corp.get(0).get("CORP").toString());
                oneLv1.setCorpName(getData_corp.get(0).get("CORPNAME").toString());
                oneLv1.setTaxPayerType(getData_corp.get(0).get("TAXPAYER_TYPE").toString());
                oneLv1.setOutputTaxCode(getData_corp.get(0).get("OUTPUTTAXCODE").toString());
                oneLv1.setInputTaxCode(getData_corp.get(0).get("INPUTTAXCODE").toString());
                oneLv1.setOutputTaxRate(getData_corp.get(0).get("OUTPUTTAXRATE").toString());
                oneLv1.setInputTaxRate(getData_corp.get(0).get("INPUTTAXRATE").toString());
                oneLv1.setInputTaxName(getData_corp.get(0).get("INPUTTAXNAME").toString());
                oneLv1.setOutputTaxName(getData_corp.get(0).get("OUTPUTTAXNAME").toString());
				oneLv1.setInputTaxCalType(getData_corp.get(0).get("INPUTTAXCALTYPE").toString());
				oneLv1.setInputTaxInclTax(getData_corp.get(0).get("INPUTTAXINCLTAX").toString());
				oneLv1.setOutputTaxCalType(getData_corp.get(0).get("OUTPUTTAXCALTYPE").toString());
				oneLv1.setOutputTaxInclTax(getData_corp.get(0).get("OUTPUTTAXINCLTAX").toString());
            }
        }




        res.getDatas().add(oneLv1);
		
		String path = System.getProperty("user.dir") + "\\Register\\" + "Register.txt";
		File file = new File(path);
		ParseJson pj = new ParseJson();
		
		int rcount = 0;
		if (file.exists()) {
			long filelength = file.length();
			byte[] filecontent = new byte[(int) filelength];
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
			String outfile = new String(filecontent, StandardCharsets.UTF_8);
			// JSON转OBJ,先解密一下
			EncryptUtils eu = new EncryptUtils();
			String AES_Key_Register = "DigiwinPosmpcfx5";
			outfile = eu.decodeAES256(AES_Key_Register, outfile);
			eu=null;
			
			DCP_RegisterRes regreq = pj.jsonToBean(outfile, new TypeToken<DCP_RegisterRes>() {
			});
			// 正式区数量
			rcount = Integer.parseInt(regreq.getTOT_Count());
			for (DCP_RegisterRes.level1 detailtemp : regreq.getDatas()) {
				if (detailtemp.getRegisterType().equals("1")) {
					String bedate = detailtemp.getBDate();
					String edate = detailtemp.getEDate();
					String scount = detailtemp.getSCount();
					// 判断一下日期,日期可以直接用当前日期转换成int去比较
					String sdate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
					int isdate = Integer.parseInt(sdate);
					int ibddate = Integer.parseInt(bedate);
					int sedate = Integer.parseInt(edate);
					if (isdate >= ibddate && ibddate <= sedate) {
						rcount += Integer.parseInt(scount);
					}
				}
			}
		}
		
		if (res.getDatas().get(0).getShopId().isEmpty() || res.getDatas().get(0).getShopId().equals("")) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E401_1, "管辖门店未设置！");
		}
		
		// 开始启用新注册验证
		if ( (sORG_FORM.equals("A")||(res.getDatas().get(0).getDisCentre()!=null&&res.getDatas().get(0).getDisCentre().equals("Y")))  && !req.getOpNO().equalsIgnoreCase("admin")) {
			// 公司的，需要验证云中台是否注册
			Preferences pre = Preferences.systemRoot().node("digiwin");
			Preferences test = pre.node("digiwinsoft");
			String machinecode = test.get("digiwincode", "");
			
			// 取得硬件信息
			String scpu = Register.getCPUSerial();
			String sHardDisk = Register.getHardDiskSN("c");
			String scouputername = Register.getcomputername();
			String sMotherboard = Register.getMotherboardSN();
			String smac = Register.getMac();
			// 取得安装目录
			File f = new File(this.getClass().getResource("/").getPath());
			String spath = f.toString();
			
			DCP_CRegisterCheckReq reqregister = new DCP_CRegisterCheckReq();
			DCP_CRegisterCheckReq.levelElm level = new DCP_CRegisterCheckReq().new levelElm();
			reqregister.setServiceId("DCP_CRegisterCheck");
			level.setMachineName(scouputername);
			level.setMotherBoardSn(sMotherboard);
			level.setHardDiskSn(sHardDisk);
			level.setCpuSerial(scpu);
			level.setSmac(smac);
			level.setInstallpath(spath);
			level.setMachincode(machinecode);
			level.setProducttype("90101");
			reqregister.setRequest(level);
			
			String jsontemp = pj.beanToJson(reqregister);
			
			// 直接调用CRegisterDCP服务
			DispatchService ds = DispatchService.getInstance();
			String resXml = ds.callService(jsontemp, StaticInfo.dao);
			DCP_CRegisterCheckRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_CRegisterCheckRes>() {
			});
			if (resserver.isSuccess() == false) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "智慧平台总部基础模块未注册！");
			}
		}
		
		if (sORG_FORM.equals("2")&&!"Y".equals(res.getDatas().get(0).getDisCentre()) )
		{
			DCP_CRegisterCheckReq reqregister = new DCP_CRegisterCheckReq();
			DCP_CRegisterCheckReq.levelElm level = new DCP_CRegisterCheckReq().new levelElm();
			reqregister.setServiceId("DCP_CRegisterCheck");
			
			level.setProducttype("1");
			if (request.getLoginType() != null && request.getLoginType().equals("2")) {
				// 移动门店注册验证使用
				level.setProducttype("7");
			}
			
			level.setrEId(res.getDatas().get(0).geteId());
			level.setrShopId(res.getDatas().get(0).getShopId());
			reqregister.setRequest(level);
			String jsontemp = pj.beanToJson(reqregister);
			
			// 直接调用CRegisterDCP服务
			DispatchService ds = DispatchService.getInstance();
			String resXml = ds.callService(jsontemp, StaticInfo.dao);
			DCP_CRegisterCheckRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_CRegisterCheckRes>() {
			});
			if (resserver.isSuccess() == false) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E401_1,"门店号:" + res.getDatas().get(0).getShopId() + "未注册!");
			}
			
		}
		
		// 重新产生token
		token = tmr.produce(res);
		
		//将原来的token干掉
		tmr.deleteTokenAndDB(req.getToken());
		tmr=null;
		
		// System.out.println("新token值:"+token);
		resLogin.setToken(token);
		res.setToken(token);
		res.setLangType(langType);
		LSC = null;
		pj=null;
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_ShopChangeReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_ShopChangeReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_ShopChangeReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_ShopChangeReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		level1Elm request = req.getRequest();
		if (Check.Null(request.getoShopId())) {
			errCt++;
			errMsg.append("门店编码不可为空值, ");
			isFail = true;
		}
		
		/*
		 * if (Check.Null(req.getcShopName())) { errCt++;
		 * errMsg.append("门店名称不可为空值, "); isFail = true; }
		 */
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_ShopChangeReq> getRequestType() {
		return new TypeToken<DCP_ShopChangeReq>() {};
	}
	
	@Override
	protected DCP_LoginRetailRes getResponseType() {
		return new DCP_LoginRetailRes();
	}
	
	// 这里写一个递归的调用当前的方法
	protected void setChildrenDatas(DCP_LoginRetailRes.level2Powers oneLv2, List<Map<String, Object>> allMenuDatas,String langType) throws Exception {
		
		try {
			List<Map<String, Object>> upModularList = getChildDatas(allMenuDatas, oneLv2.getModularNo());
			// 主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
			condition.put("MODULARNO", true);
			// 调用过滤函数
			List<Map<String, Object>> upModularList2 = MapDistinct.getMap(upModularList, condition);
			
			if (upModularList2 != null && !upModularList2.isEmpty()) {
				for (Map<String, Object> menuDatas : upModularList2) {
					DCP_LoginRetailRes.level2Powers lv1 = new DCP_LoginRetailRes().new level2Powers();
					lv1.setChildren(new ArrayList<DCP_LoginRetailRes.level2Powers>());
					lv1.setDatas(new ArrayList<DCP_LoginRetailRes.lv_Func>());
					
					String modularNO = menuDatas.get("MODULARNO").toString();
					String modularName = menuDatas.get("MODULARNAME").toString();
					String modularLevel = menuDatas.get("MODULARLEVEL").toString();
					String upModularNO = menuDatas.get("UPMODULARNO").toString();
					String isCollection = menuDatas.get("ISCOLLECTION").toString();
					String mProName = menuDatas.get("MPRONAME").toString();
					if(langType.equals("zh_TW")) {
						mProName =ZHConverter.convert(mProName,0);
					}
					String mftype = menuDatas.get("MFTYPE").toString();
					String mparameter = menuDatas.get("MPARAMETER").toString();
					String isMask = menuDatas.get("ISMASK").toString();
					
					// 模块编码
					lv1.setModularNo(modularNO);
					lv1.setModularName(modularName);
					lv1.setModularLevel(modularLevel);
					lv1.setUpModularNo(upModularNO);
					lv1.setIsCollection(isCollection);
					if("4".equals(mftype)) {
						lv1.setSysReportURL(statementURL); //自建报表地址
					}else {
						lv1.setSysReportURL(sysReportURL); //帆软报表地址
					}
					lv1.setProName(mProName);
					lv1.setMftype(mftype);
					lv1.setMparameter(mparameter);
					lv1.setIsMask(isMask);
					
					lv1.setDatas(new ArrayList<DCP_LoginRetailRes.lv_Func>());
					// lv1.setChildren(new
					// ArrayList<LoginRetailRes.lv2_modular>());
					
					lv1.setChildren(new ArrayList<DCP_LoginRetailRes.level2Powers>());
					
					for (Map<String, Object> fstFuncDatas : upModularList) {
						if (modularNO.equals(fstFuncDatas.get("MODULARNO")) == false)
							continue;
						// 在这里过滤除属于第一级的func
						DCP_LoginRetailRes.lv_Func fstFunc = new DCP_LoginRetailRes().new lv_Func();
						
						String funcNO = fstFuncDatas.get("FUNCNO").toString();
						if (funcNO.trim().equals(""))
							continue;// 过滤掉空值
						
						String funName = fstFuncDatas.get("FUNNAME").toString();
						String PowerType = fstFuncDatas.get("POWERTYPE").toString();
						String fProName = fstFuncDatas.get("FPRONAME").toString();
						
						fstFunc.setFunctionNo(funcNO);
						fstFunc.setFunctionName(funName);
						fstFunc.setPowerType(PowerType);
						fstFunc.setProName(fProName);
						
						lv1.getDatas().add(fstFunc);
						
					} // 添加菜单func结束
					
					setChildrenDatas(lv1, allMenuDatas,langType);
					oneLv2.getChildren().add(lv1);
				}
				
			}
		} catch (Exception ignored) {
		
		}
		
	}
	
	protected List<Map<String, Object>> getChildDatas(List<Map<String, Object>> allMenuDatas, String modularNO) {
		List<Map<String, Object>> menuDataTemp = new ArrayList<>();
		for (Map<String, Object> map : allMenuDatas) {
			if (map.get("UPMODULARNO").toString().equals(modularNO)) {
				menuDataTemp.add(map);
			}
		}
		return menuDataTemp;
	}



	public String getOrgSql(DCP_ShopChangeReq req){
		String sql="";
		sql="select DISTINCT a.ORGANIZATIONNO,a.SNAME,b.UP_ORG,c.ORG_NAME,a.ORG_FORM,a.DISCENTRE,nvl(d.ISDEFAULT,'N') isdefault ,a.BELFIRM,a.ORG_TYPE " +
				" from DCP_ORG a " +
				" left join DCP_ORG_LEVEL b on a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO " +
				" LEFT JOIN DCP_ORG_LANG c ON a.EID=c.EID AND a.ORGANIZATIONNO=c.ORGANIZATIONNO AND c.LANG_TYPE='"+req.getLangType()+"'"+
				" LEFT JOIN platform_staffs_shop d on d.shopid=a.organizationno and a.eid=d.eid " +
				" where a.eid='"+req.geteId()+"' and a.status='100'" ;

		return sql;
	}

	public void expendChildren(DCP_LoginRetailRes.level1Elm level1Elm, DCP_LoginRetailRes.level2Shops org,List<Map<String, Object>> allOrgList,DCP_ShopChangeReq req){
		String forg = org.getOrgNo();
		DCP_StaffQueryRes res =new DCP_StaffQueryRes();

		for(Map<String, Object> map : allOrgList){
			Object up_org = map.get("UP_ORG");
			if(up_org!=null&&forg.equals(up_org.toString())){
				DCP_LoginRetailRes.level2Shops child =new DCP_LoginRetailRes().new level2Shops();
				child.setOrgNo((String)map.get("ORGANIZATIONNO"));
				child.setOrgName((String)map.get("ORG_NAME"));

				child.setOrg_Form((String)map.get("ORG_FORM"));
				child.setIsDefault((String)map.get("ISDEFAULT"));
				child.setDisCentre((String)map.get("DISCENTRE"));

				List<DCP_LoginRetailRes.level2Shops> filterRows = level1Elm.getMyShops().stream().filter(x -> x.getOrgNo().equals(child.getOrgNo())).collect(Collectors.toList());
				if(filterRows.size()<=0){
					//有嵌套的就会多次添加，所以需要过滤一下
					level1Elm.getMyShops().add(child);
				}

				if(Check.Null(level1Elm.getShopId())&&!Check.Null(req.getRequest().getoShopId())){
					// 设置门店显示
					if(child.getOrgNo().equals(req.getRequest().getoShopId())){
						level1Elm.setShopId(child.getOrgNo());
						level1Elm.setShopName(child.getOrgName());
						level1Elm.setOrganizationNo(child.getOrgNo());
						level1Elm.setOrg_Form(child.getOrg_Form());
						level1Elm.setBELFIRM((String)map.get("BELFIRM"));
						level1Elm.setBELFIRM_NAME(child.getOrgName());
						level1Elm.setOrg_type((String)map.get("ORG_TYPE"));


                    }
				}

				expendChildren(level1Elm,child,allOrgList,req);

				//org.getChildren().add(child);
			}
		}

	}

	public String getQueryDefaultShopSql(String eId,String shopId,String langType)  {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(""
				+ " select SHOPNAME,in_cost_warehouse,in_non_cost_warehouse,"
				+ " out_cost_warehouse,out_non_cost_warehouse,inv_cost_warehouse,inv_non_cost_warehouse,CITY,DISTRICT,"
				+ " in_cost_warehouseName,in_non_cost_warehouseName, out_cost_warehouseName,"
				+ " out_non_cost_warehouseName,inv_cost_warehouseName,inv_non_cost_warehouseName ,ORG_FORM,ENABLECREDIT"
				+ ",case when ORG_FORM='0'  THEN SHOPID ELSE BELFIRM END AS BELFIRM "
				+ ",case when ORG_FORM='0'  THEN SHOPNAME ELSE BELFIRM_NAME END AS BELFIRM_NAME "
				+ " ,isDefault ,DISCENTRE,org_type,ISEXPAND,return_cost_warehouse,return_cost_warehousename  "
				+ " from ("
				+ "select  a.organizationno as shopid,c.org_name  SHOPNAME,"
				+ " c.in_cost_warehouse,c.in_non_cost_warehouse,c.out_cost_warehouse,"
				+ " c.out_non_cost_warehouse,c.inv_cost_warehouse,c.inv_non_cost_warehouse,c.CITY,c.DISTRICT,"
				+ " d.warehouse_name as in_cost_warehouseName, e.warehouse_name as in_non_cost_warehouseName,f.warehouse_name as out_cost_warehouseName,"
				+ " g.warehouse_name as out_non_cost_warehouseName,h.warehouse_name as inv_cost_warehouseName,i.warehouse_name as inv_non_cost_warehouseName,"
                + " j.warehouse_name as return_cost_warehouseName,c.return_cost_warehouse,"
                + " c.ORG_FORM as ORG_FORM,c.ENABLECREDIT ,c.BELFIRM,c.BELFIRM_NAME,C.DISCENTRE,'Y' AS isDefault,c.org_type,'N' AS ISEXPAND"

				+ " from dcp_org a "
				+ " left join dcp_org_lang b ON a.organizationno=b.organizationno and a.eid=b.eid  and b.LANG_TYPE='"+langType+"' "

				+ " inner join "
				+ " (SELECT A.eid,A.ORGANIZATIONNO SHOPID,NVL(B.ORG_NAME,A.SNAME) AS ORG_NAME ,"
				+ " A.in_cost_warehouse,A.in_non_cost_warehouse,A.out_cost_warehouse,A.out_non_cost_warehouse,A.inv_cost_warehouse,"
				+ " A.inv_non_cost_warehouse,a.RETURN_COST_WAREHOUSE,"
				+ " A.STATUS,A.ENABLECREDIT,A.CITY,A.COUNTY DISTRICT,A.ORG_FORM,A.BELFIRM,BB.ORG_NAME AS BELFIRM_NAME,A.DISCENTRE,a.org_type"
				+ " FROM dcp_ORG A "
				+ " LEFT JOIN DCP_ORG_LANG B ON A.eid=B.eid AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langType+"' "
				+ " AND B.status='100' "
				+ " LEFT JOIN DCP_ORG_LANG BB ON A.eid=BB.eid AND A.BELFIRM =BB.ORGANIZATIONNO AND BB.LANG_TYPE = '"+langType+"' "
				+ " AND BB.status='100' "
				+ " WHERE A.eid='"+eId+"' AND (A.ORG_FORM='0' OR A.ORG_FORM='2') AND A.status='100' "
				+ " ) c on a.ORGANIZATIONNO=c.SHOPID and a.eid=c.eid "
				+ " left join DCP_warehouse_lang d on c.in_cost_warehouse=d.warehouse and a.eid=d.eid AND c.SHOPID=d.ORGANIZATIONNO AND d.lang_type=b.lang_type "
				+ " left join DCP_warehouse_lang e on c.in_non_cost_warehouse=e.warehouse and a.eid=e.eid AND c.SHOPID=e.ORGANIZATIONNO AND e.lang_type=b.lang_type "
				+ " left join DCP_warehouse_lang f on c.out_cost_warehouse=f.warehouse and a.eid=f.eid AND c.SHOPID=f.ORGANIZATIONNO AND f.lang_type=b.lang_type "
				+ " left join DCP_warehouse_lang g on c.out_non_cost_warehouse=g.warehouse and a.eid=g.eid AND c.SHOPID=g.ORGANIZATIONNO AND g.lang_type=b.lang_type "
				+ " left join DCP_warehouse_lang h on c.inv_cost_warehouse=h.warehouse and a.eid=h.eid AND c.SHOPID=h.ORGANIZATIONNO AND h.lang_type=b.lang_type "
				+ " left join DCP_warehouse_lang i on c.inv_non_cost_warehouse=i.warehouse and a.eid=i.eid AND c.SHOPID=i.ORGANIZATIONNO AND i.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang j on c.return_cost_warehouse=j.warehouse and a.eid=j.eid AND c.SHOPID=j.ORGANIZATIONNO AND j.lang_type=b.lang_type "
                + " where a.STATUS='100' and (c.STATUS='100' or c.STATUS is null )  "
				+ " AND A.eid='"+eId+"' "
				+ " AND A.ORGANIZATIONNO='"+shopId+"' "
				+ " ) "
		);

		sql = sqlbuf.toString();

		sqlbuf=null;

		return sql;
	}

}
