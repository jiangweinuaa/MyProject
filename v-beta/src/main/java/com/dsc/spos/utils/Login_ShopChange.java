package com.dsc.spos.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Login_ShopChange
{
    /**
     *
     * @param eId
     * @param opNO
     * @param shopId 门店切换时有值
     * @param langType 语言
     * @return
     * @throws Exception
     */
    public String getQueryStaffSql(String eId,String opNO,String shopId,String langType) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select Organizationno, eid,Opgroup, opNO, opName, langType, bDate, password, isNew ,viewAbleDay ,DEPARTNO,DEPARTNAME,orgRange,employeeNo,defaultOrg,DEPARTMENTNO,employeename,departmentname,belorgno,belorgname,UPDEPARTNO  "
                + "from "
                + "("
                + "select A.eid,A.opNO,NVL(B.OP_NAME,A.OPNAME) as opName ,A.LANG_TYPE as langType,to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') as bDate,A.Password, A.Organizationno,A.Opgroup "
                + ", a.isNew  , a.viewAbleDay,A.DEPARTNO,C.DEPARTNAME,A.ORGRANGE AS orgRange,A.EMPLOYEENO as employeeNo,A.DEFAULTORG as defaultOrg,D.DEPARTMENTNO,D.name as employeename,f.DEPARTNAME as departmentname," +
                " g.organizationno as belorgno,h.org_name as belorgname,g.UPDEPARTNO   "
                + "from PLATFORM_STAFFS A  "
                + "LEFT JOIN   PLATFORM_STAFFS_LANG B ON A.OPNO=B.OPNO AND A.eid=B.eid "
                + "AND b.LANG_TYPE="
                + "'"+langType+"' "
                + "LEFT JOIN   DCP_DEPARTMENT_LANG C ON A.EID=C.EID AND A.DEPARTNO=C.DEPARTNO AND C.LANG_TYPE='"+langType+"' "
                + "left join dcp_employee D on D.EID=A.EID AND D.EMPLOYEENO=A.EMPLOYEENO  "
                + "left join DCP_DEPARTMENT_LANG f on f.eid=D.eid and f.DEPARTNO=D.DEPARTMENTNO and f.lang_type='"+langType+"' "
                + "left join DCP_DEPARTMENT g on g.eid=f.eid and g.DEPARTNO=f.DEPARTNO "
                + "left join dcp_org_lang h on g.eid=h.eid and g.ORGANIZATIONNO=h.ORGANIZATIONNO and h.lang_type='"+langType+"'"
                + "WHERE A.eid='"+eId+"' "
                + "AND  A.OPNO='"+opNO+"' "
                + "AND A.status='100' "
                + " ) TBL ");
        sql = sqlbuf.toString();
        
        sqlbuf=null;
        
        return sql;
    }
    
    
    /**
     * 登录和组织切换共用
     * @param eId
     * @param opNO
     * @param langType 语言
     * @return
     * @throws Exception
     */
    public String getQueryStaffShopSql(String eId,String opNO,String langType) throws Exception {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                + " select OPNO,SHOPID,SHOPNAME,in_cost_warehouse,in_non_cost_warehouse,"
                + " out_cost_warehouse,out_non_cost_warehouse,inv_cost_warehouse,inv_non_cost_warehouse,CITY,DISTRICT,"
                + " in_cost_warehouseName,in_non_cost_warehouseName, out_cost_warehouseName,"
                + " out_non_cost_warehouseName,inv_cost_warehouseName,inv_non_cost_warehouseName ,ORG_FORM,ENABLECREDIT"
                + ",case when ORG_FORM='0'  THEN SHOPID ELSE BELFIRM END AS BELFIRM "
                + ",case when ORG_FORM='0'  THEN SHOPNAME ELSE BELFIRM_NAME END AS BELFIRM_NAME "
                //2019-05-15  加上 isDefault 字段， 是否默认组织
                + " ,isDefault ,DISCENTRE,org_type,ISEXPAND,return_cost_warehouse,return_cost_warehousename  "
                + " from ("
                + "select a.SHOPID SHOPID,c.org_name  SHOPNAME,a.opno OPNO,"
                + " c.in_cost_warehouse,c.in_non_cost_warehouse,c.out_cost_warehouse,"
                + " c.out_non_cost_warehouse,c.inv_cost_warehouse,c.inv_non_cost_warehouse,c.CITY,c.DISTRICT,"
                + " d.warehouse_name as in_cost_warehouseName, e.warehouse_name as in_non_cost_warehouseName,f.warehouse_name as out_cost_warehouseName,"
                + " g.warehouse_name as out_non_cost_warehouseName,h.warehouse_name as inv_cost_warehouseName,i.warehouse_name as inv_non_cost_warehouseName,"
                + " j.warehouse_name as return_cost_warehouseName,c.return_cost_warehouse,"
                + " c.ORG_FORM as ORG_FORM,c.ENABLECREDIT ,c.BELFIRM,c.BELFIRM_NAME,C.DISCENTRE,a.isDefault,c.org_type,a.ISEXPAND"
                //2019-05-15  加上 isDefault 字段， 是否默认组织
                + " from platform_staffs_shop a "
                + " left join dcp_org_lang b ON a.SHOPID=b.organizationno and a.eid=b.eid  and b.LANG_TYPE='"+langType+"' "
                
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
                + " ) c on a.SHOPID=c.SHOPID and a.eid=c.eid "
                
                
                + " left join DCP_warehouse_lang d on c.in_cost_warehouse=d.warehouse and a.eid=d.eid AND c.SHOPID=d.ORGANIZATIONNO AND d.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang e on c.in_non_cost_warehouse=e.warehouse and a.eid=e.eid AND c.SHOPID=e.ORGANIZATIONNO AND e.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang f on c.out_cost_warehouse=f.warehouse and a.eid=f.eid AND c.SHOPID=f.ORGANIZATIONNO AND f.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang g on c.out_non_cost_warehouse=g.warehouse and a.eid=g.eid AND c.SHOPID=g.ORGANIZATIONNO AND g.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang h on c.inv_cost_warehouse=h.warehouse and a.eid=h.eid AND c.SHOPID=h.ORGANIZATIONNO AND h.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang i on c.inv_non_cost_warehouse=i.warehouse and a.eid=i.eid AND c.SHOPID=i.ORGANIZATIONNO AND i.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang j on c.return_cost_warehouse=j.warehouse and a.eid=j.eid AND c.SHOPID=j.ORGANIZATIONNO AND j.lang_type=b.lang_type "
                + " where a.STATUS='100' and (c.STATUS='100' or c.STATUS is null )  "
                + " AND A.eid='"+eId+"' "
                + " AND A.OPNO='"+opNO+"' "
                + " ) order by org_form ,SHOPID "
        );
        
        sql = sqlbuf.toString();
        
        sqlbuf=null;
        
        return sql;
    }

    public String getQueryStaffShopAllSql(String eId,String opNO,String langType) throws Exception {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                + " select OPNO,SHOPID,SHOPNAME,in_cost_warehouse,in_non_cost_warehouse,"
                + " out_cost_warehouse,out_non_cost_warehouse,inv_cost_warehouse,inv_non_cost_warehouse,CITY,DISTRICT,"
                + " in_cost_warehouseName,in_non_cost_warehouseName, out_cost_warehouseName,"
                + " out_non_cost_warehouseName,inv_cost_warehouseName,inv_non_cost_warehouseName ,ORG_FORM,ENABLECREDIT"
                + ",case when ORG_FORM='0'  THEN SHOPID ELSE BELFIRM END AS BELFIRM "
                + ",case when ORG_FORM='0'  THEN SHOPNAME ELSE BELFIRM_NAME END AS BELFIRM_NAME "
                + " ,isDefault ,DISCENTRE,org_type,ISEXPAND,return_cost_warehouse,return_cost_warehousename  "
                + " from ("
                + "select a.organizationno SHOPID,c.org_name  SHOPNAME,'"+opNO+"' as  OPNO,"
                + " c.in_cost_warehouse,c.in_non_cost_warehouse,c.out_cost_warehouse,"
                + " c.out_non_cost_warehouse,c.inv_cost_warehouse,c.inv_non_cost_warehouse,c.CITY,c.DISTRICT,"
                + " d.warehouse_name as in_cost_warehouseName, e.warehouse_name as in_non_cost_warehouseName,f.warehouse_name as out_cost_warehouseName,"
                + " g.warehouse_name as out_non_cost_warehouseName,h.warehouse_name as inv_cost_warehouseName,i.warehouse_name as inv_non_cost_warehouseName,"
                + " j.warehouse_name as return_cost_warehouseName,c.return_cost_warehouse,"
                + " c.ORG_FORM as ORG_FORM,c.ENABLECREDIT ,c.BELFIRM,c.BELFIRM_NAME,C.DISCENTRE,'N' as isDefault,c.org_type,'N' as ISEXPAND"
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
                + " WHERE A.eid='"+eId+"'  AND A.status='100' "  //AND (A.ORG_FORM='0' OR A.ORG_FORM='2')
                + " ) c on a.organizationno=c.SHOPID and a.eid=c.eid "


                + " left join DCP_warehouse_lang d on c.in_cost_warehouse=d.warehouse and a.eid=d.eid AND c.SHOPID=d.ORGANIZATIONNO AND d.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang e on c.in_non_cost_warehouse=e.warehouse and a.eid=e.eid AND c.SHOPID=e.ORGANIZATIONNO AND e.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang f on c.out_cost_warehouse=f.warehouse and a.eid=f.eid AND c.SHOPID=f.ORGANIZATIONNO AND f.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang g on c.out_non_cost_warehouse=g.warehouse and a.eid=g.eid AND c.SHOPID=g.ORGANIZATIONNO AND g.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang h on c.inv_cost_warehouse=h.warehouse and a.eid=h.eid AND c.SHOPID=h.ORGANIZATIONNO AND h.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang i on c.inv_non_cost_warehouse=i.warehouse and a.eid=i.eid AND c.SHOPID=i.ORGANIZATIONNO AND i.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang j on c.return_cost_warehouse=j.warehouse and a.eid=j.eid AND c.SHOPID=j.ORGANIZATIONNO AND j.lang_type=b.lang_type "
                + " where a.STATUS='100' and (c.STATUS='100' or c.STATUS is null )  "
                + " AND A.eid='"+eId+"' "
                + " ) order by org_form ,SHOPID "
        );

        sql = sqlbuf.toString();

        sqlbuf=null;

        return sql;
    }


    public String getQueryModularFunctionSql(String eId,String opNO,String sType,String baseDataSourceERP,String langType ) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        if("7".equals(sType))//移动门店
        {
        	sType="7','1','3";
        }
        if(opNO.equals("admin")||opNO.equals("ADMIN")||opNO.equals("crm")||opNO.equals("CRM")||opNO.equals("NRC"))
        {
            sqlbuf.append("select modularNO,modularName,upModularNO,modularLevel,isCollection,funcno,funName,modularNO1,PowerType,MproName,FproName, Mftype,Mparameter,'N' as isMask ,'Y' as ISSHOWDISTRIPRICE, '100' as DISC,PRIORITY  from "
                    + "("
                    + "select  distinct b.priority ,b.modularNO modularNO,b.modularLevel modularLevel,b.uppermodular as upModularNO, ");
            if(langType.equals("zh_TW"))
            {
                sqlbuf.append("b.chtmsg  modularName," );
            }else if(langType.equals("zh_EN"))
                sqlbuf.append("b.engmsg  modularName," );
            else
                sqlbuf.append("b.chsmsg  modularName,");
            sqlbuf.append(""
                    + "c.funcno,c.chsmsg funName,c.modularno modularNO1,d.PowerType ,b.proname as MproName,c.proname as FproName,(case WHEN e.modularno IS NULL  then 'false' ELSE 'true' end) AS isCollection,b.ftype as Mftype,b.parameter as Mparameter  "
                    + "from DCP_modular b  "
                    + "left join DCP_modular_function c on b.eid=c.eid and b.modularno=c.modularno  "
                    + "and c.status='100' "
            );
            if(baseDataSourceERP.equals("Y")) //基本资料来源ERP,把基本资料维护的新增。修改、删除等权限去掉
                sqlbuf.append( "and c.FUNCNO NOT IN ("
//					+ "'10020201','10020202','10020203','10020204',"
//					+ "'10020401','10020403',"
//					+ "'10030101','10030102','10030103',"
//					+ "'10030201','10030202','10030203',"
//					+ "'10030301','10030302','10030303',"
//					+ "'10030401','10030402','10030403',"
//					+ "'10030501','10030502','10030503','10030504',"
//					+ "'10031501','10031502','10031503',"
//					+ "'10031401','10031402','10031403',"
//					+ "'10030801','10030802','10030803',"
//					+ "'10031201','10031202','10031203',"
//					+ "'10031801','10031802','10031803',"
//					+ "'250201'  ,'250203')"
                                + "'10020201','10020202','10020203','10020204',"
                                + "'10020401','10020403',"
                                + "'10030101','10030102','10030103','10030104',"
                                + "'10030201','10030202','10030203','10030204',"
                                + "'10030301','10030302','10030303',"
                                + "'10030401','10030402','10030403','10030404',"
                                + "'10030501','10030502','10030503','10030504',"
                                + "'10031501','10031502','10031503',"
                                + "'10031401','10031402','10031403','10031404',"
                                + "'10030801','10030802','10030803',"
                                + "'10031201','10031202','10031203',"
                                + "'10031801','10031802','10031803','10031804',"
//					+ "'10073301','10073302','10073303','10073304',"
                                + "'250201'  ,'250203')"
                );
            if(!opNO.equals("NRC"))
            {
                sqlbuf.append(	" and c.FUNCNO NOT IN ('250101') " );
            }
            if(opNO.equals("ADMIN")||opNO.equals("NRC"))
            {
                sqlbuf.append("LEFT join platform_power d on c.eid=d.eid and c.funcno=d.funcno "
                        + "LEFT JOIN DCP_COLLECTION e on b.eid=e.eid and b.modularno=e.modularno  "
                        + "and e.opno='"+opNO+"' "
                        + "WHERE b.eid='"+eId+"' "
                        + " and b.status='100'  and b.STYPE in ('"+sType+"')  "
                        + " order by b.priority, b.modularno,funcno "
                        + ")");
                
            }else if(opNO.equals("admin"))
            {
                sqlbuf.append("LEFT join platform_power d on c.eid=d.eid and c.funcno=d.funcno "
                        + "LEFT JOIN DCP_COLLECTION e on b.eid=e.eid and b.modularno=e.modularno  "
                        + "and e.opno='"+opNO+"' "
                        + "WHERE b.eid='"+eId+"' "
                        + " and  b.modularno not in ('100101','100719')"
                        + " and b.status='100'  and b.STYPE in ('"+sType+"')  "
                        + " order by b.priority, b.modularno,funcno "
                        + ")");
            }else
            {
                sqlbuf.append("LEFT join platform_power d on c.eid=d.eid and c.funcno=d.funcno "
                        + "LEFT JOIN DCP_COLLECTION e on b.eid=e.eid and b.modularno=e.modularno  "
                        + "and e.opno='"+opNO+"' "
                        + "WHERE b.eid='"+eId+"' "
                        + " and  b.modularno not in ('2501','100101','100719')"
                        //+ " and  b.parameter<>'ADMIN' "
                        + " and b.status='100'  and b.STYPE in ('"+sType+"')  "
                        + " order by b.priority, b.modularno,funcno "
                        + ")");
            }
        }
        else {
            sqlbuf.append("select modularNO,modularName,upModularNO,modularLevel,isCollection,funcno,funName,modularNO1,PowerType,MproName,FproName ,Mftype,Mparameter,isMask,ISSHOWDISTRIPRICE,DISC,PRIORITY from "
                    + "("
                    + "select  b.priority, a.modularNO modularNO,b.modularLevel modularLevel,b.uppermodular as upModularNO, ");
            if(langType.equals("zh_TW"))
            {
                sqlbuf.append("b.chtmsg  modularName," );
            }else if(langType.equals("zh_EN"))
                sqlbuf.append("b.engmsg  modularName," );
            else
                sqlbuf.append("b.chsmsg  modularName,");
            sqlbuf.append(""
                    + "d.funcno,c.chsmsg funName,c.modularno modularNO1,nvl(d.PowerType,'1') as PowerType ,b.proname as MproName,c.proname as FproName,(case WHEN e.modularno IS NULL  then 'false' ELSE 'true' end) AS isCollection,b.ftype as Mftype"
                    + ",b.parameter as Mparameter,min(aa.ismask) as isMask,max(aa.ISSHOWDISTRIPRICE) as ISSHOWDISTRIPRICE , max(aa.DISC) as DISC  "
                    + "from platform_billpower a "
                    + "inner join platform_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100' "
                    + "inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno "
                    //目录不进行权限管控 begin
                    + " and (b.proname is not null or (b.eid,b.modularno) in ("
                                        + " select distinct  b.eid ,b.uppermodular as modularno "
                    + " from platform_billpower a inner join platform_staffs_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100'  and  a.eid='"+eId+"' and aa.OPNO='"+opNO+"' "
                    + "                           inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno and b.status='100'  and b.modularlevel=5 and b.eid='"+eId+"' "
                    + "                           and b.proname is not null "
                    + " union all "
                    + " select distinct  b.eid ,b.uppermodular as modularno "
                    + " from platform_billpower a inner join platform_staffs_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100'  and  a.eid='"+eId+"' and aa.OPNO='"+opNO+"' "
                    + "                           inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno and b.status='100'  and b.modularlevel=4 and b.eid='"+eId+"' "
                    + "                          and  ( b.proname is not null "
                    + "                          or "
                    + "                         (b.eid,b.modularno) in  "
                    + "                         (select distinct b.eid, b.uppermodular as modularno  "
                    + " from platform_billpower a inner join platform_staffs_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100'  and  a.eid='"+eId+"' and  aa.OPNO='"+opNO+"' "
                    + "                          inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno and b.status='100'  and b.modularlevel=5 and b.eid='"+eId+"' "
                    + "                          and b.proname is not null "
                    + "                          )"
                    + ")"
                    + " union all "
                    + " select distinct  b.eid ,b.uppermodular as modularno "
                    + " from platform_billpower a inner join platform_staffs_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100'  and  a.eid='"+eId+"' and aa.OPNO='"+opNO+"' "
                    + "                          inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno  and b.modularlevel=3 and b.eid='"+eId+"' "
                    + "                          and  ( b.proname is not null "
                    + "                         or "
                    + "                          (b.eid,b.modularno) in   "
                    + "                         (select distinct b.eid, b.uppermodular as modularno  "
                    + " from platform_billpower a inner join platform_staffs_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100'  and  a.eid='"+eId+"' and  aa.OPNO='"+opNO+"' "
                    + "                          inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno and b.status='100'  and b.modularlevel=4 and b.eid='"+eId+"' "
                    + "                          and  ( b.proname is not null "
                    + "                          or "
                    + "                         (b.eid,b.modularno) in  "
                    + "                         (select distinct b.eid, b.uppermodular as modularno  "
                    + " from platform_billpower a inner join platform_staffs_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100'  and  a.eid='"+eId+"' and  aa.OPNO='"+opNO+"' "
                    + "                          inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno and b.status='100'  and b.modularlevel=5 and b.eid='"+eId+"' "
                    + "                          and b.proname is not null "
                    + "                          )"
                    + "))  "
                    + " ) "
                    + " union all "
                    + " select distinct  b.eid ,b.uppermodular as modularno "
                    + " from platform_billpower a inner join platform_staffs_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100'  and  a.eid='"+eId+"'and  aa.OPNO='"+opNO+"' "
                    + "                          inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno  and b.modularlevel=2 and b.eid='"+eId+"' "
                    + "                         and b.status='100' "
                    + "                          and  ( b.proname is not null "
                    + "                          or "
                    + "                          (b.eid,b.modularno) in  "
                    + "                          ( "
                    + "                          select distinct  b.eid ,b.uppermodular as modularno "
                    + " from platform_billpower a inner join platform_staffs_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100'  and  a.eid='"+eId+"' and aa.OPNO='"+opNO+"' "
                    + "                          inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno  and b.modularlevel=3 and b.eid='"+eId+"' "
                    + "                          and b.status='100' "
                    + "                          and  ( b.proname is not null "
                    + "                          or "
                    + "                         (b.eid,b.modularno) in  "
                    + "                         (select distinct b.eid, b.uppermodular as modularno "
                    + " from platform_billpower a inner join platform_staffs_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100'  and  a.eid='"+eId+"' and aa.OPNO='"+opNO+"' "
                    + "                          inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno and b.status='100'  and b.modularlevel=4 and b.eid='"+eId+"' "
                    + "                          and  ( b.proname is not null "
                    + "                          or "
                    + "                         (b.eid,b.modularno) in  "
                    + "                         (select distinct b.eid, b.uppermodular as modularno  "
                    + " from platform_billpower a inner join platform_staffs_role aa  on a.eid=aa.eid and a.opgroup=aa.opgroup and aa.STATUS=a.STATUS and aa.status='100'  and  a.eid='"+eId+"' and  aa.OPNO='"+opNO+"' "
                    + "                          inner join DCP_modular b on a.eid=b.eid and a.modularno=b.modularno and b.status='100'  and b.modularlevel=5 and b.eid='"+eId+"' "
                    + "                          and b.proname is not null "
                    + "                          )"
                    + "))))) "
                    + " ) "
                    + " ) "
                    //目录不进行权限管控 end
                    + "left join DCP_modular_function c on a.eid=c.eid and a.modularno=c.modularno  "
                    + "and c.status='100' ");
            if(baseDataSourceERP.equals("Y"))  //基本资料来源ERP,把基本资料维护的新增。修改、删除等权限去掉
                sqlbuf.append( "and c.FUNCNO NOT IN ("
//						+ "'10020201','10020202','10020203','10020204',"
//						+ "'10020401','10020403',"
//						+ "'10030101','10030102','10030103',"
//						+ "'10030201','10030202','10030203',"
//						+ "'10030301','10030302','10030303',"
//						+ "'10030401','10030402','10030403',"
//						+ "'10030501','10030502','10030503','10030504',"
//						+ "'10031501','10031502','10031503',"
//						+ "'10031401','10031402','10031403',"
//						+ "'10030801','10030802','10030803',"
//						+ "'10031201','10031202','10031203',"
//						+ "'10031801','10031802','10031803',"
//						+ "'250201'  ,'250203')"
                                + "'10020201','10020202','10020203','10020204',"
                                + "'10020401','10020403',"
                                + "'10030101','10030102','10030103','10030104',"
                                + "'10030201','10030202','10030203','10030204',"
                                + "'10030301','10030302','10030303',"
                                + "'10030401','10030402','10030403','10030404',"
                                + "'10030501','10030502','10030503','10030504',"
                                + "'10031501','10031502','10031503',"
                                + "'10031401','10031402','10031403','10031404',"
                                + "'10030801','10030802','10030803',"
                                + "'10031201','10031202','10031203',"
                                + "'10031801','10031802','10031803','10031804',"
//						+ "'10073301','10073302','10073303','10073304',"
                                + "'250201'  ,'250203')"
                );
            sqlbuf.append(	" and c.FUNCNO NOT IN ('250101') " );
            sqlbuf.append( " and c.funcno in "
                    + "("
                    + "select funcno from platform_power "
                    + "where eid='"+eId+"' "
                    + "and opgroup in "
                    + "("
                    + "select opgroup from platform_staffs_role "
                    + "where eid='"+eId+"' "
                    + "and opno='"+opNO+"'"
                    + ")"
                    + ") "
                    + "LEFT join platform_power d on c.eid=d.eid and c.funcno=d.funcno "
                    + "LEFT JOIN DCP_COLLECTION e on a.eid=e.eid and a.modularno=e.modularno  "
                    + "and e.opno='"+opNO+"' "
                    + "WHERE A.eid='"+eId+"'  and b.STYPE in ('"+sType+"')  "
                    + " and  b.modularno not in ('2501','100101','100719') "
                    //+ " and  b.parameter<>'ADMIN' "
                    + "and (a.opgroup in "
                    + "("
                    + "select opgroup from platform_staffs_role "
                    + "where eid='"+eId+"' "
                    + "and opno='"+opNO+"' ) "
                    + " OR b.proname is null )"
                    + "and b.status='100' "
                    + " group by b.priority, a.modularNO ,b.modularLevel ,b.uppermodular , b.chsmsg  ,d.funcno,c.chsmsg ,c.modularno ,nvl(d.PowerType,'1')  ,b.proname ,c.proname ,(case WHEN e.modularno IS NULL  then 'false' ELSE 'true' end) ,b.ftype ,b.parameter  "
                    + "order by b.priority ,a.modularno,funcno "
                    + ")");
        }
        //这里开始加入菜单注册管控
        String regtypeString="";
        if(langType.equals("zh_CN"))
        {
            regtypeString=" and ( A.Rpattern='1' or A.Rpattern='3' )  ";
        }
        else
        {
            regtypeString=" and  ( A.Rpattern='2' or A.Rpattern='3' ) ";
        }
        
        
        regtypeString+= " or A.MODULARNO='26'  or A.MODULARNO='2602' or A.MODULARNO='2604'   " ;
        String curdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        
        String sqlregister=" select distinct A.MODULARNO  from DCP_modular A left join platform_cregisterdetail B on A.rfuncno=B.producttype "
                + " and B.BDATE<='"+curdate+"' AND '"+curdate+"'<=B.EDATE "
                + " where  A.eid='"+eId+"' and B.producttype is not null   " + regtypeString ;
        
        sql = sqlbuf.toString();
        
        if(!opNO.equals("NRC"))
        {
            sql= " select A.* from ( "+sql+" ) A inner join ( "+sqlregister+" ) B on A.MODULARNO=B.MODULARNO  ORDER BY A.PRIORITY,A.MODULARNO";
        }
        
        sqlbuf=null;
        
        return sql;
    }
    
    
    
    
    
    
    
    
}
