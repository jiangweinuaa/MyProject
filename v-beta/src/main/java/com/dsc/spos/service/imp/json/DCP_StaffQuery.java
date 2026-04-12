package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dsc.spos.json.cust.req.DCP_StaffQueryReq;
import com.dsc.spos.json.cust.res.DCP_StaffQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_StaffQuery extends SPosBasicService<DCP_StaffQueryReq, DCP_StaffQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_StaffQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StaffQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StaffQueryReq>(){};
	}

	@Override
	protected DCP_StaffQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StaffQueryRes();
	}

	@Override
	protected DCP_StaffQueryRes processJson(DCP_StaffQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		DCP_StaffQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		int totalRecords = 0;
		int totalPages = 0;
		sql = this.getQueryCount(req);
		List<Map<String, Object>> getNum = this.doQueryData(sql, null);

		if (getNum != null && getNum.isEmpty() == false)
		{ 
			Map<String, Object> oneData_Count = getNum.get(0);
			String num = oneData_Count.get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
		}
		else
		{
			totalRecords = 0;
			totalPages = 0;
		}

		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);	

		//算總頁數
		totalPages = totalRecords / req.getPageSize();
		totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

		sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_StaffQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("STAFFNO", true);	
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);


			//
			String sql_mes_platformstaff_warehouse="select a.*,b.warehouse_name,c.ORG_NAME from MES_PLATFORM_STAFFS_WAREHOUSE a " +
					"left join dcp_warehouse_lang b on a.eid=b.eid and a.ORGANIZATION=b.ORGANIZATIONNO  and a.warehouseno=b.warehouse and b.lang_type='"+req.getLangType()+"' " +
                    "left join dcp_org_lang c on a.eid=c.eid and a.ORGANIZATION=c.ORGANIZATIONNO and c.LANG_TYPE='"+req.getLangType()+"' " +
					"where a.eid='"+req.geteId()+"' ";
			List<Map<String, Object>> getData_meswarehouse = this.doQueryData(sql_mes_platformstaff_warehouse, null);


			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_StaffQueryRes.level1Elm oneLv1 = res.new level1Elm();

				String staffNO = oneData.get("STAFFNO").toString() ;
				oneLv1.setStaffNo(staffNO);

				oneLv1.setOpName(oneData.get("OPNAME").toString());
				oneLv1.setEn_cash(oneData.get("EN_CASH").toString());
				oneLv1.setOrganizationNo(oneData.get("ORG").toString());
				oneLv1.setOrganizationName(oneData.get("ORGANIZATIONNAME").toString());
				
				oneLv1.setDepartNo(oneData.get("DEPARTNO").toString());
				oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());
				oneLv1.setViewAbleDay(oneData.get("VIEWABLEDAY").toString()); //业务单据可查看天数
				
				oneLv1.setLangType(oneData.get("STAFFLANGTYPE").toString());
				oneLv1.setStatus(oneData.get("STAFFSTATUS").toString());

                oneLv1.setTelephone(oneData.get("TELEPHONE").toString());
                oneLv1.setDiscPowerType(oneData.get("DISCPOWERTYPE").toString());
                oneLv1.setDisc(oneData.get("DISC").toString());
                oneLv1.setMaxFreeAmt(oneData.get("MAXFREEAMT").toString());

                oneLv1.setDistributor(oneData.get("DISTRIBUTOR").toString());
                oneLv1.setEmployeeNo(oneData.get("EMPLOYEENO").toString());
                oneLv1.setDefaultOrg(oneData.get("DEFAULTORG").toString());
                oneLv1.setDefaultOrgName(oneData.get("DEFAULTORGNAME").toString());
                oneLv1.setOrgRange(oneData.get("ORGRANGE").toString());
                oneLv1.setCreateOpId(oneData.get("CREATEOPID").toString());
                oneLv1.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
                oneLv1.setCreateTime(oneData.get("CREATETIME").toString());
                oneLv1.setLastModiOpId(oneData.get("LASTMODIOPID").toString());
                oneLv1.setLastModiTime(oneData.get("LASTMODITIME").toString());
                oneLv1.setLastModiOpName(oneData.get("LASTMODIOPNAME").toString());
                oneLv1.setCreateOpName(oneData.get("CREATEOPNAME").toString());
                oneLv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());
				
				oneLv1.setRoleList(new ArrayList<DCP_StaffQueryRes.level2Elm>());
				oneLv1.setOrgList(new ArrayList<DCP_StaffQueryRes.level3Elm>());
				oneLv1.setDatas(new ArrayList<DCP_StaffQueryRes.level4Elm>());
                oneLv1.setWarehouseList(new ArrayList<>());

				//Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
				//condition2.put("STAFFNO", true);	
				//condition2.put("OPGROUP", true);	
				//调用过滤函数
				//List<Map<String, Object>> roleDatas=MapDistinct.getMap(getQData, condition2);

				sql = this.getStaffRole(req,staffNO);
				List<Map<String, Object>> roleData = this.doQueryData(sql, null);

				for (Map<String, Object> oneData2 : roleData) 
				{
					//过滤属于此单头的明细
					//					if(staffNO.equals(oneData2.get("STAFFNO")))
					//					{
					DCP_StaffQueryRes.level2Elm oneLv2 = res.new level2Elm();
					oneLv2.setOpGroup(oneData2.get("OPGROUP").toString());
					oneLv2.setOpgName(oneData2.get("OPGNAME").toString());
					oneLv1.getRoleList().add(oneLv2);
					//					}
				}


				//Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查詢條件
				//condition3.put("STAFFNO", true);	
				//condition3.put("ORG", true);	
				//调用过滤函数
				//List<Map<String, Object>> orgDatas=MapDistinct.getMap(getQData, condition3);
				sql = this.getStaffShop(req, staffNO);
				List<Map<String, Object>> shopData = this.doQueryData(sql, null);

				String orgSql=getOrgSql(req);
				List<Map<String, Object>> allOrgList = this.doQueryData(orgSql, null);

				for (Map<String, Object> oneData2 : shopData)
				{
					//					//过滤属于此单头的明细
					//					if(staffNO.equals(oneData2.get("STAFFNO")))
					//					{
					DCP_StaffQueryRes.level3Elm oneLv3 = res.new level3Elm();
					oneLv3.setOrg(oneData2.get("ORG").toString());
					oneLv3.setOrgName(oneData2.get("ORGNAME").toString());
					Object isexpand = oneData2.get("ISEXPAND");
                    oneLv3.setChildren(new ArrayList<>());
					if (isexpand != null)
					{
						oneLv3.setExpand(isexpand.toString());

						if(isexpand.toString().equals("1")){
							//下展
							expendChildren(oneLv3,allOrgList);
						}

					}


					oneLv1.getOrgList().add(oneLv3);
					//					}
				}


				Map<String, Boolean> condition4 = new HashMap<String, Boolean>(); //查詢條件
				condition4.put("STAFFNO", true);	
				condition4.put("LANGTYPE", true);	
				//调用过滤函数
				List<Map<String, Object>> datas=MapDistinct.getMap(getQData, condition4);

				for (Map<String, Object> oneData2 : datas) 
				{
					//过滤属于此单头的明细
					if(staffNO.equals(oneData2.get("STAFFNO")) )
					{
						DCP_StaffQueryRes.level4Elm oneLv4 = res.new level4Elm();
						oneLv4.setLangType(oneData2.get("LANGTYPE").toString());
						oneLv4.setStatus(oneData2.get("STATUS").toString());
						oneLv4.setOpName(oneData2.get("OPNAME").toString());
						oneLv1.getDatas().add(oneLv4);
					}
				}

				//mes仓库
				if (getData_meswarehouse != null && getData_meswarehouse.size()>0)
				{
					List<Map<String, Object>> mesWarehouseList=getData_meswarehouse.stream().filter(p->p.get("OPNO").toString().equals(oneLv1.getStaffNo())).collect(Collectors.toList());
					if (mesWarehouseList != null && mesWarehouseList.size()>0)
					{
						for (Map<String, Object> meswhMap : mesWarehouseList)
						{
							DCP_StaffQueryRes.levelwarehouseElm lvmesWh=res.new levelwarehouseElm();
							lvmesWh.setWarehouseNo(meswhMap.get("WAREHOUSENO").toString());
							lvmesWh.setIsDefault(meswhMap.get("ISDEFAULT").toString());
							lvmesWh.setWarehouseName(meswhMap.get("WAREHOUSE_NAME").toString());
							lvmesWh.setOrganizationNo(meswhMap.get("ORGANIZATION").toString());
							lvmesWh.setOrganizationName(meswhMap.get("ORG_NAME").toString());
							oneLv1.getWarehouseList().add(lvmesWh);
						}
					}
				}

				res.getDatas().add(oneLv1);
			}
		}
		else{
			res.setDatas(new ArrayList<DCP_StaffQueryRes.level1Elm>());
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_StaffQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		String opGroup = req.getRequest().getOpGroup();
		String org = req.getRequest().getOrg();
		String getType = req.getRequest().getGetType();
		String langType = req.getLangType();

		int pageSize = req.getPageSize();
		int pageNum = req.getPageNumber();

		int startRow = pageSize * (pageNum-1);

		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append( "select * from ( select COUNT(DISTINCT staffNO  ) OVER() NUM ,dense_rank() over(ORDER BY staffNO ) rn , "
				+ " staffNO , staffOPName  ,  org ,organizationName , staffLangType ,"
				+ " staffstatus , EID ,"
				+ " langType , opName , status , opgroup , departNO , departName,en_cash,viewAbleDay ,"
                + " TELEPHONE,DISCPOWERTYPE,DISC,MAXFREEAMT,EMPLOYEENO,DEFAULTORG,ORGRANGE,CREATEOPID,CREATEDEPTID,CREATETIME,LASTMODIOPID,LASTMODITIME,DISTRIBUTOR,lastModiOpName,defaultorgname,createopname ,createdeptname  "
				+ " from ( " 
				+ " SELECT COUNT(DISTINCT a.opNO ) OVER() NUM ,dense_rank() over(ORDER BY a.opNO ) rn ,"
				+ " a.opNO AS staffNO ,a.opName as staffOPName , f.SHOPID as org , d.org_name AS organizationName ,"
				+ " a.lang_type AS staffLangType , a.status  AS staffstatus,  a.EID ,"
				+ " b.lang_Type  AS langType, b.op_name AS opName , b.status , e.opgroup , g.departNO, g.departName ,a.en_cash,a.viewAbleDay, "
                + " a.TELEPHONE,a.DISCPOWERTYPE,a.DISC,a.MAXFREEAMT,a.EMPLOYEENO,a.DEFAULTORG,a.ORGRANGE,a.CREATEOPID,a.CREATEDEPTID,a.CREATETIME,a.LASTMODIOPID,a.LASTMODITIME,a.DISTRIBUTOR,e1.op_name as  lastModiOpName,h.ORG_NAME as defaultorgname ,"
                +" e2.op_name as createopname ,g1.departname as createdeptname"
				+ "  FROM  platform_staffs  a "
				+ " LEFT JOIN platform_staffs_lang  b  ON a.OPNO = b.OPNO AND a.EID = b.EID  "
				+ " LEFT JOIN DCP_ORG_Lang d ON a.EID = d.EID "
				+ " AND a.organizationNO = d.organizationNO AND d.lang_Type = '"+langType+"' "
				+ " LEFT JOIN platform_staffs_role e ON a.EID = e.EID AND a.opNO = e.opNO  "
                + " LEFT JOIN platform_staffs_SHOP F ON A.EID = F.EID AND A.OPNO = F.OPNO  "
              //2018-12-19 新增部门信息关联查询 
				+ " LEFT JOIN DCP_DEPARTMENT_LANG G on A.EID = G.EID AND A.DEPARTNO = G.DEPARTNO AND  G.lang_Type = '"+langType+"' "
                + " left join platform_staffs_lang e1 on e1.eid=a.eid and e1.opno=a.LASTMODIOPID and e1.lang_type='"+req.getLangType()+"'  "
                + " left join DCP_ORG_LANG h on h.eid=a.eid and h.ORGANIZATIONNO=a.defaultorg and h.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e2 on e2.eid=a.eid and e2.opno=a.createopid and e2.lang_type='"+req.getLangType()+"'  "
                + " LEFT JOIN DCP_DEPARTMENT_LANG g1 on A.EID = g1.EID AND A.createDeptId = g1.DEPARTNO AND  g1.lang_Type = '"+langType+"' "

                + ") "
				+ " where 1=1 and EID = '"+eId+"' ");
		if(status != null && status.length() > 0){
			sqlbuf.append(" and staffstatus = '"+status+"'");
		}
		if(opGroup != null && opGroup.length() > 0){
			sqlbuf.append(" and opGroup = '"+opGroup+"'");
		}
		if(org != null && org.length() > 0){
			sqlbuf.append(" and org = '"+org+"'");
		}

		if(keyTxt != null && keyTxt.length() > 0){
			sqlbuf.append(" and (staffNO like  '%%"+keyTxt+"%%' or staffOPName like '%%"+keyTxt+"%%' "
					+ " or org like '%%"+keyTxt+"%%' or organizationName like '%%"+keyTxt+"%%' "
					//					+ " or opgName like '%%"+keyTxt+"%%' or orgName like   '%%"+keyTxt+"%%' "
					//					+ " or opName like  '%%"+keyTxt+"%%'  "
					+ ")  ");
		}

		if(getType != null && getType.equals("0")){
			sqlbuf.append(" ) where  rn > "+startRow+" AND rn < = "+(startRow+pageSize)+" ");
		}else{
			sqlbuf.append(" ) ");
		}

		sqlbuf.append(" order by staffNO,org ");

		sql = sqlbuf.toString();
		return sql;
	}


	protected String getQueryCount(DCP_StaffQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		String opGroup = req.getRequest().getOpGroup();
		String org = req.getRequest().getOrg();
		String langType = req.getLangType();

		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append( " select count(*) as num from (" 
				+ "  select distinct staffno, EID  from ( " 
				+ " SELECT  a.opNO AS staffNO ,a.opName as staffOPName ,  f.SHOPID AS org  , d.org_name AS organizationName ,"
				+ " a.lang_type AS staffLangType , a.status  AS staffstatus, a.EID , "
				+ " b.lang_Type  AS langType, b.op_name AS opName , b.status , e.opgroup "
				+ "  FROM  platform_staffs  a "
				+ " LEFT JOIN platform_staffs_lang  b  ON a.OPNO = b.OPNO AND a.EID = b.EID  "
				+ " LEFT JOIN DCP_ORG_Lang d ON a.EID = d.EID "
				+ " AND a.organizationNO = d.organizationNO AND d.lang_Type = '"+langType+"' "
				+ " LEFT JOIN platform_staffs_role e ON a.EID = e.EID AND a.opNO = e.opNO  "
                + " LEFT JOIN platform_staffs_SHOP F ON A.EID = F.EID AND A.OPNO = F.OPNO  "
				//2018-12-19 新增部门信息关联查询
				+ " LEFT JOIN DCP_DEPARTMENT_LANG G on A.EID = G.EID AND A.DEPARTNO = G.DEPARTNO AND  G.lang_Type = '"+langType+"' "
                + " ) "
				+ " where 1=1 and EID = '"+eId+"' ");
		if(status != null && status.length() > 0){
			sqlbuf.append(" and staffstatus = '"+status+"'");
		}
		if(opGroup != null && opGroup.length() > 0){
			sqlbuf.append(" and opGroup = '"+opGroup+"'");
		}
		if(org != null && org.length() > 0){
			sqlbuf.append(" and org = '"+org+"'");
		}

		if(keyTxt != null && keyTxt.length() > 0){
			sqlbuf.append(" and (staffNO like  '%%"+keyTxt+"%%' or staffOPName like '%%"+keyTxt+"%%' "
					+ " or org like '%%"+keyTxt+"%%' or organizationName like '%%"+keyTxt+"%%' "
					//					+ " or opgName like '%"+keyTxt+"%' or orgName like   '%"+keyTxt+"%' "
					//					+ " or opName like  '%"+keyTxt+"%'  "
					+ ")");
		}

		sqlbuf.append(" order by staffNO,org )");

		sql = sqlbuf.toString();
		return sql;
	}




	/**
	 * 获取角色信息
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getStaffRole(DCP_StaffQueryReq req, String opNO) throws Exception {
		String sql = null;

		sql = " select * from  ( select e.opGroup , c.opgName  from platform_staffs_role e "
				+ " INNER JOIN platform_role  c ON e.EID = c.EID AND e.opGroup = c.opGroup "
				+ " and e.status='100' and c.status='100' "
				+ " where e.opNO = '"+opNO+"' and e.EID = '"+req.geteId()+"')";

		return sql;
	}

	/**
	 * 获取组织信息
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getStaffShop(DCP_StaffQueryReq req, String opNO ) throws Exception {
		String sql = null;

		sql = " select * from  ( select  f.SHOPID AS org , g.SHOPNAME AS orgName ,f.ISEXPAND  from platform_staffs_shop f "
				+ " LEFT JOIN "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"'"

					//	+ " AND B.status='100' "
				+ " WHERE A.EID='"+req.geteId()+"'  "
				//+ "AND A.status='100' "
				+ ") g ON f.EID = g.EID AND f.SHOPID = g.SHOPID AND g.lang_type = '"+req.getLangType()+"'  "
				+ " where f.opNO = '"+opNO+"' and f.EID = '"+req.geteId()+"') order by org";

		return sql;
	}

	public String getOrgSql(DCP_StaffQueryReq req){
		String sql="";
		sql="select a.ORGANIZATIONNO,a.SNAME,b.UP_ORG,c.ORG_NAME " +
				" from DCP_ORG a " +
				" left join DCP_ORG_LEVEL b on a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO " +
				" LEFT JOIN DCP_ORG_LANG c ON a.EID=c.EID AND a.ORGANIZATIONNO=c.ORGANIZATIONNO AND c.LANG_TYPE='"+req.getLangType()+"'";


		return sql;
	}

	public void expendChildren(DCP_StaffQueryRes.level3Elm org,List<Map<String, Object>> allOrgList){
		String forg = org.getOrg();
		DCP_StaffQueryRes res =new DCP_StaffQueryRes();

		for(Map<String, Object> map : allOrgList){
			Object up_org = map.get("UP_ORG");
			if(up_org!=null&&forg.equals(up_org.toString())){
				DCP_StaffQueryRes.level3Elm child = res.new level3Elm();
				child.setOrg((String)map.get("ORGANIZATIONNO"));
				child.setOrgName((String)map.get("ORG_NAME"));
                child.setChildren(new ArrayList<>());
				expendChildren(child,allOrgList);

				org.getChildren().add(child);
			}
		}

	}



}
