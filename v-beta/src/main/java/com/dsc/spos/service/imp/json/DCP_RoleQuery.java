package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.dsc.spos.json.cust.req.DCP_PowerQueryReq;
import com.dsc.spos.json.cust.req.DCP_RoleQueryReq;
import com.dsc.spos.json.cust.res.DCP_PowerQueryRes;
import com.dsc.spos.json.cust.res.DCP_RoleQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_RoleQuery extends SPosBasicService<DCP_RoleQueryReq, DCP_RoleQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_RoleQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
		{
		  	errMsg.append("request不能为空 ");
		  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if (Check.Null(req.getRequest().getGetType())) 
		{
			errMsg.append("查询类型不可为空值, ");
			isFail = true;
		} 
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	  return isFail;
		
	}

	@Override
	protected TypeToken<DCP_RoleQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_RoleQueryReq>(){};
	}

	@Override
	protected DCP_RoleQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_RoleQueryRes();
	}


	@Override
	protected DCP_RoleQueryRes processJson(DCP_RoleQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;
        String powerSql = null;
        String funcSql =null;
        String userSql=null;
		
		//查询条件
		String eId = req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		String status=req.getRequest().getStatus();
		
		String getType = req.getRequest().getGetType();
		
		DCP_RoleQueryRes res=null;
		res=this.getResponse();
		
		int startRow = 1;
		int pageSize = 10;
		
		if(getType != null && getType.equals("0")){
			//给分页字段赋值
			sql = this.getQuerySql_Count(req);			//查询总笔数
	    
			String[] conditionValues_Count = {eId};			//查詢條件
			List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQData_Count != null && getQData_Count.isEmpty() == false)
			{ 
				Map<String, Object> oneData2 = getQData_Count.get(0);
				String num = oneData2.get("NUM").toString();
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
		  
			sql=null;
			
			int pageNumber = req.getPageNumber();
			pageSize = req.getPageSize();
			
			//計算起啟位置
			startRow = ((pageNumber - 1) * pageSize);
			startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
			startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		}
		
		
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select a.*,dd.departname as CREATEDEPTNAME,em1.op_name as CREATEOPNAME,em2.op_name as LASTMODIOPNAME from Platform_Role a"
                        + " left join platform_staffs_lang em1 on em1.eid=a.eid and em1.opno=a.CREATEOPID and em1.lang_type='"+req.getLangType()+"' "
                        + " left join platform_staffs_lang em2 on em2.eid=a.eid and em2.opno=a.LASTMODIOPID and em2.lang_type='"+req.getLangType()+"' "
                        + " left join dcp_department_lang dd on dd.eid=a.eid and dd.departno=a.createdeptid and dd.lang_type='"+req.getLangType()+"'  "+

                " where a.EID='"+eId+"'");
		if (status != null && status.length()>0)
		{
			sqlbuf.append(" and a.status='"+status+"'");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" and(a.OPGROUP like '%%"+ keyTxt +"%%' or a.OPGNAME like '%%"+ keyTxt +"%%')");
		}
		
		sqlbuf.append(" and a.OPGROUP in ("
		    +"SELECT OPGROUP FROM ("
			+"SELECT ROWNUM rn, OPGROUP FROM("
		    +"SELECT OPGROUP,STATUS FROM PLATFORM_ROLE WHERE EID=?"
		);
		
		if (status != null && status.length()>0)
		{
			sqlbuf.append(" and STATUS='"+status+"'");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" and(OPGROUP like '%%"+ keyTxt +"%%' or OPGNAME like '%%"+ keyTxt +"%%')");
		}
		sqlbuf.append(" order by STATUS desc, OPGROUP asc) ) ");
		
		//getType ==0 有分页， ==1 无分页， 查询全部
		if(getType.equals("0")){
			sqlbuf.append("where rn>" + startRow + " AND rn <= " + (startRow+pageSize) + "");
		}
		sqlbuf.append(")");
				
		sql=sqlbuf.toString();
		
		String[] condCountValues={eId};
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,condCountValues);

        powerSql = this.getQuerySql1(req);
        //String[] condCountValues1 = {};
        List<Map<String, Object>> getFirstDatas = this.doQueryData(powerSql,null);

        funcSql=getQuerySql2(req);
        List<Map<String, Object>> allFuncDatas = this.doQueryData(funcSql, null);

        userSql=getUserSql(req);

        List<Map<String, Object>> allUserDatas = this.doQueryData(userSql, null);


        if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("OPGROUP", true);		
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
			
			res.setDatas(new ArrayList<DCP_RoleQueryRes.level1Elm>());
			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_RoleQueryRes.level1Elm oneLv1 = res.new level1Elm();
				// 取出第一层
				String opGroup = oneData.get("OPGROUP").toString();
				String opgName = oneData.get("OPGNAME").toString();
				String disc = oneData.get("DISC").toString();
				String isMask = oneData.get("ISMASK").toString();
				String isShowDistriPrice = oneData.get("ISSHOWDISTRIPRICE").toString();
				String status1 = oneData.get("STATUS").toString();
				String maxFreeAmt = oneData.get("MAXFREEAMT").toString();
                String createdeptname = oneData.get("CREATEDEPTNAME").toString();
                String createopname = oneData.get("CREATEOPNAME").toString();
                String lastmodiopname = oneData.get("LASTMODIOPNAME").toString();
                if (Check.Null(maxFreeAmt))
					maxFreeAmt="0";
				
				int i_disc;
				if (disc == null || disc.length()==0 )
				{
					i_disc=0;
				} 
				else 	
				{
					i_disc=Integer.parseInt(disc);
					float f= Float.parseFloat(disc); 
					i_disc=(int) Math.floor(f);
				}
				//设置响应
				oneLv1.setOpGroup(opGroup);
				oneLv1.setOpgName(opgName);
				oneLv1.setDisc(i_disc);
				oneLv1.setIsMask(isMask);
				oneLv1.setIsShowDistriPrice(isShowDistriPrice);
				oneLv1.setStatus(status1);
				oneLv1.setMaxFreeAmt(maxFreeAmt);
                oneLv1.setCreatorName(createopname);
                oneLv1.setCreatorDeptName(createdeptname);
                oneLv1.setLastmodifyName(lastmodiopname);

                //添加modularPower
                if (getFirstDatas != null && getFirstDatas.isEmpty() == false)
                {
                    //List<Map<String, Object>> opGroupList1 = getFirstDatas.stream().filter(var -> var.get("OPGROUP").equals(opGroup)).collect(Collectors.toList());

                    //Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
                    //condition2.put("MODULARNO", true);
                    //调用过滤函数
                    //List<Map<String, Object>> getQHeader2=MapDistinct.getMap(getFirstDatas, condition2);

                    List<Map<String, Object>> opGroupList = getFirstDatas.stream().filter(var -> var.get("OPGROUP").equals(opGroup)).collect(Collectors.toList());

                    //List modularPowerList=new ArrayList();
                    oneLv1.setModularPower(new ArrayList<>());
                    for (Map<String, Object> modularData : opGroupList)
                    {
                        String opgroup = modularData.get("OPGROUP").toString();
                        //if(!opgroup.equals(opGroup)){
                        //    continue;
                        //}
                        String modularNo = modularData.get("MODULARNO").toString();
                        String powertype = modularData.get("POWERTYPE").toString();
                        String queryRange = modularData.get("QUERY_RANGE").toString();
                        String editRange = modularData.get("EDIT_RANGE").toString();
                        String deleteRange = modularData.get("DELETE_RANGE").toString();

                        DCP_RoleQueryRes.ModularPower mp=res.new ModularPower();
                        mp.setModularNo(modularNo);
                        mp.setQueryRange(queryRange);
                        mp.setPowerType(powertype);
                        mp.setEditRange(editRange);
                        mp.setDeleteRange(deleteRange);
                        oneLv1.getModularPower().add(mp);
                       // modularPowerList.add(mp);

                    }

                    //oneLv1.setModularPower(modularPowerList);

                }

                //添加functionPower
                List funcList=new ArrayList();
                for (Map<String, Object> funcData : allFuncDatas)
                {
                    String opgroup = funcData.get("OPGROUP").toString();
                    if(!opgroup.equals(opGroup)){
                        continue;
                    }
                    String funcNo = funcData.get("FUNCNO").toString();
                    String powerType = funcData.get("POWERTYPE").toString();
                    DCP_RoleQueryRes.FunctionPower fp=res.new FunctionPower();
                    fp.setFunctionNo(funcNo);
                    fp.setPowerType(powerType);
                    funcList.add(fp);
                }
                oneLv1.setFunctionPower(funcList);

                //添加userList
                List userList=new ArrayList();
                for(Map<String, Object> userData : allUserDatas){
                    String opgroup = userData.get("OPGROUP").toString();
                    if(!opgroup.equals(opGroup)){
                        continue;
                    }
                    String opno = userData.get("OPNO").toString();
                    String opname = userData.get("OPNAME").toString();
                    userData.get("STATUS").toString();
                    DCP_RoleQueryRes.User user=res.new User();
                    user.setOpNo(opno);
                    user.setOpName(opname);
                    user.setStatus(status);
                    userList.add(user);
                }
                oneLv1.setUserList(userList);
                oneLv1.setUserCount(String.valueOf(userList.size()));

                //添加create
                Object role = oneData.get("ROLE");
                if (role != null)
                {
                    oneLv1.setRole(role.toString());
                }
                Object createopid = oneData.get("CREATEOPID");
                if (createopid != null)
                {
                    oneLv1.setCreatorID(createopid.toString());
                }
                Object createdeptid = oneData.get("CREATEDEPTID");
                if (createdeptid != null)
                {
                    oneLv1.setCreatorDeptID(createdeptid.toString());
                }
                Object createtime = oneData.get("CREATETIME");
                if (createtime != null)
                {
                    oneLv1.setCreateDatetime(createtime.toString());
                }
                Object lastmodiopid = oneData.get("LASTMODIOPID");
                if (lastmodiopid != null)
                {
                    oneLv1.setLastmodifyID(lastmodiopid.toString());
                }
                Object lastmoditime = oneData.get("LASTMODITIME");
                if (lastmoditime != null)
                {
                    oneLv1.setLastmodifyDatetime(lastmoditime.toString());
                }


                //添加
				res.getDatas().add(oneLv1);
				oneLv1=null;
			}			
		}
		else
		{
			res.setDatas(new ArrayList<DCP_RoleQueryRes.level1Elm>());
		}

        return res;
	}



    @Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_RoleQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected String getQuerySql_Count(DCP_RoleQueryReq req) throws Exception {
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		
		sqlbuf.append("SELECT NUM FROM ("
				+"SELECT COUNT(*) NUM FROM ("
				+"SELECT OPGROUP FROM PLATFORM_ROLE WHERE EID=?");
		
		String keyTxt = req.getRequest().getKeyTxt();
		String status=req.getRequest().getStatus();
		if (status != null && status.length()>0)
		{
			sqlbuf.append(" and status='"+status+"'");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" and(opGroup like '%%"+ keyTxt +"%%' or opgName like '%%"+ keyTxt +"%%')");
		}
		sqlbuf.append(")");
		sqlbuf.append(")");
		
		sql=sqlbuf.toString();
		
		return sql;
	}

    /**
     *
     * @param req
     * @return
     * @throws Exception
     */
    protected String getQuerySql1(DCP_RoleQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String eId = req.geteId();
        String langType=req.getLangType();
        String opGroup="";
        if (req.getRequest().getOpGroup()!=null&&req.getRequest().getOpGroup().toString() != null)
        {
            String[] opGroupList= req.getRequest().getOpGroup();

            for (String str : opGroupList)
            {
                opGroup = opGroup + "'" +str + "',";
            }
            if(opGroup.length()>0){
                opGroup = opGroup.substring(0,opGroup.length()-1);
            }

        }
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT distinct modularNO , modularName , uppermodular, modularlevel,sType,  powerType, isPower,isFPower,OPGROUP,QUERY_RANGE,EDIT_RANGE,DELETE_RANGE  FROM ("// funcNO, funcName ,
                + " SELECT a.priority,a.modularNO ,c.QUERY_RANGE,c.EDIT_RANGE,c.DELETE_RANGE ,c.OPGROUP,");
        if(langType.equals("zh_TW"))
        {
            sqlbuf.append("a.chtmsg  AS modularName," );//b.chtmsg AS funcname,
        }else if(langType.equals("zh_EN"))
            sqlbuf.append("a.engmsg  as modularName,");//b.engmsg AS funcname,
        else
            sqlbuf.append("a.chsmsg  modularName,");//b.chsmsg AS funcname,

        sqlbuf.append( " a.uppermodular,  a.sType , a.modularLevel , "
                + "  d.Powertype , "//b.funcNO,
                + " ( CASE  WHEN c.modularNO IS NULL   THEN 'N' ELSE 'Y' END) AS  isPower ,"
                + " ( CASE  WHEN d.funcNO  IS NULL   THEN 'N' ELSE 'Y' END) AS  isFPower   "
                + " FROM  DCP_MODULAR a "
                + " left join DCP_MODULAR_function b on a.modularno=b.modularno "
                + "	and a.EID=b.EID and b.status='100' "
                + " left join platform_billpower c on a.modularno=c.modularno and a.EID=c.EID "
                + " and c.status='100' %s "
                + " left join Platform_Power d on b.funcno=d.funcno and b.EID=d.EID "
                + " and d.status='100'  %s "
                + " where a.EID='"+eId+"' and a.status='100' "
                + "  order by a.priority,a.modularNO,b.funcno ) ");
        sql = sqlbuf.toString();

        if(opGroup.length()>0){
            sql=String.format(sql,"and c.opgroup  in ("+opGroup+")","and d.opgroup  in ("+opGroup+") ");
        }else{
            sql=String.format(sql," "," ");
        }


        return sql;
    }


    protected String getQuerySql2(DCP_RoleQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType=req.getLangType();
        String opGroup="";
        if (req.getRequest().getOpGroup()!=null&&req.getRequest().getOpGroup().toString() != null)
        {
            String[] opGroupList= req.getRequest().getOpGroup();

            for (String str : opGroupList)
            {
                opGroup = opGroup + "'" +str + "',";
            }
            if(opGroup.length()>0){
                opGroup = opGroup.substring(0,opGroup.length()-1);
            }

        }


        StringBuffer funcSql=new StringBuffer("");

        funcSql.append(" select * from ( SELECT  b.modularNO,b.funcno,");
        if(langType.equals("zh_TW"))
        {
            funcSql .append("chtmsg as chsmsg, ");
        }else if(langType.equals("zh_EN"))
        {
            funcSql .append("engmsg as chsmsg, ");
        }
        else
        {
            funcSql .append("chsmsg as chsmsg, ");
        }
        funcSql.append(" b.EID ,d.OPGROUP,"
                + "( CASE  WHEN d.funcNO  IS NULL   THEN 'N' ELSE 'Y' END) AS  isFPower, "
                + "d.powerType   FROM DCP_MODULAR_function  b "
                + "LEFT JOIN Platform_Power d on b.funcno=d.funcno and b.EID=d.EID  "
                + "and d.status='100' "
                + " %s ) where EID = '"+eId+"' ");

        String sql= funcSql.toString();

        if(opGroup.length()>0){
            sql=String.format(sql,"and d.opgroup  in ("+opGroup+") ");
        }else{
            sql=String.format(sql," "," ");
        }

        return sql;

    }

    private String getUserSql(DCP_RoleQueryReq req) {

        String eId = req.geteId();
        String langType=req.getLangType();
        String opGroup="";
        if (req.getRequest().getOpGroup()!=null&&req.getRequest().getOpGroup().toString() != null)
        {
            String[] opGroupList= req.getRequest().getOpGroup();

            for (String str : opGroupList)
            {
                opGroup = opGroup + "'" +str + "',";
            }
            if(opGroup.length()>0){
                opGroup = opGroup.substring(0,opGroup.length()-1);
            }

        }


        String sql=String.format(" select a.OPGROUP,b.OPNO,b.OPNAME,b.STATUS from PLATFORM_STAFFS_ROLE a " +
                " left join PLATFORM_STAFFS b on a.opno=b.opno" +
                " where a.opgroup in (%s) and a.eid='%s' and b.eid='%s' ",opGroup,
                eId,eId);
        if(opGroup.length()<=0){
            sql=String.format(" select a.OPGROUP,b.OPNO,b.OPNAME,b.STATUS from PLATFORM_STAFFS_ROLE a " +
                            " left join PLATFORM_STAFFS b on a.opno=b.opno" +
                            " where  a.eid='%s' and b.eid='%s' ",
                    eId,eId);
        }

        return sql;

    }

}
