package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_PowerQueryReq;
import com.dsc.spos.json.cust.res.DCP_PowerQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PowerQuery extends SPosBasicService<DCP_PowerQueryReq , DCP_PowerQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_PowerQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
//		StringBuffer errMsg = new StringBuffer("");
//
//		String[] opGroup = req.getOpGroup();
//		if(opGroup.length < 1){
//			errMsg.append("角色不可为空值, ");
//			isFail = true;
//		}
//		if (isFail) {
//			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
//		}
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_PowerQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PowerQueryReq>(){};
	}

	@Override
	protected DCP_PowerQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PowerQueryRes();
	}

	List<Map<String, Object>> allFuncDatas = new ArrayList<Map<String, Object>>();
	
	@Override
	protected DCP_PowerQueryRes processJson(DCP_PowerQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PowerQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		String eId = req.geteId();
		String langType  = req.getLangType();
		try {
			sql = this.getQuerySql1(req);
			String[] condCountValues = {};
			List<Map<String, Object>> getFirstDatas = this.doQueryData(sql,condCountValues);
			
			String opGroup="''";
			if (req.getRequest().getOpGroup().toString() != null) 
			{
				String[] opGroupList= req.getRequest().getOpGroup();
				
				for (String str : opGroupList)
				{	
					opGroup = opGroup + ",'" +str + "'";
				}
				
			}	
	
			//基础资料来源ERP
			String BaseDataSourceERP=PosPub.getPARA_SMS(dao, req.geteId(), "","BaseDataSourceERP");
			if(Check.Null(BaseDataSourceERP))
				BaseDataSourceERP="Y";
			StringBuffer funcSql=new StringBuffer("");
			if(BaseDataSourceERP.equals("Y")) //基本资料来源ERP,把基本资料维护的新增。修改、删除等权限去掉
			{
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
				funcSql.append(" b.EID ,"
					+ "( CASE  WHEN d.funcNO  IS NULL   THEN 'N' ELSE 'Y' END) AS  isFPower, "
					+ "d.powerType   FROM DCP_MODULAR_function  b "
					+ "LEFT JOIN Platform_Power d on b.funcno=d.funcno and b.EID=d.EID  "
					+ "and d.status='100' "
					+ "and d.opgroup  in ("+opGroup+") ) where EID = '"+eId+"' "
					+ " and FUNCNO NOT IN ("
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
					+ "'250201'  ,'250203')");
			}
			else
			{
				funcSql.append(" select * from ( SELECT  b.modularNO,b.funcno,b.PRONAME,");
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
				funcSql.append(" b.EID ,"
						+ "( CASE  WHEN d.funcNO  IS NULL   THEN 'N' ELSE 'Y' END) AS  isFPower, "
						+ "d.powerType   FROM DCP_MODULAR_function  b "
						+ "LEFT JOIN Platform_Power d on b.funcno=d.funcno and b.EID=d.EID  "
						+ "and d.status='100' "
						+ "and d.opgroup  in ("+opGroup+") ) where EID = '"+eId+"' ");
			}
			if(!req.getOpNO().equals("NRC"))
			{
				funcSql.append(" and FUNCNO NOT IN ('250101')"); //320401
			}
			
			//这里开始加入菜单注册管控
			StringBuffer regtypeString=new StringBuffer("");
			if(langType.equals("zh_CN"))
			{
				regtypeString.append(" and ( A.Rpattern='1' or A.Rpattern='3' )  ");
			}
			else
			{
				regtypeString.append(" and  ( A.Rpattern='2' or A.Rpattern='3' ) ");
			}
			
			regtypeString.append(" or A.MODULARNO='26' or A.MODULARNO='2602' or A.MODULARNO='2604'  " );
			String curdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
			
			StringBuffer sqlregister=new StringBuffer(" select distinct A.MODULARNO  from DCP_MODULAR A left join platform_cregisterdetail B on A.rfuncno=B.producttype "
					+ " and B.BDATE<='"+curdate+"' AND '"+curdate+"'<=B.EDATE " 
					+ " where  A.EID='"+req.geteId()+"' and B.producttype is not null   " + regtypeString.toString() );
			
			if(!req.getOpNO().equals("NRC"))
			{
                funcSql.insert(0," select A.* from ( ");
			    funcSql.append(" ) A inner join ( "+sqlregister.toString()+" ) B on A.MODULARNO=B.MODULARNO ");
			}
			
			allFuncDatas = this.doQueryData(funcSql.toString(), null);
			
			//添加一级信息
			if (getFirstDatas != null && getFirstDatas.isEmpty() == false)
			{
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("MODULARNO", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getFirstDatas, condition);
				res.setPowerDatas(new ArrayList<DCP_PowerQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQHeader) 
				{
					String modularLevel = oneData.get("MODULARLEVEL").toString();
					if(modularLevel != null && modularLevel.equals("1")){
						
						DCP_PowerQueryRes.level1Elm oneLv1 = res.new level1Elm();
						oneLv1.setChildren(new ArrayList<DCP_PowerQueryRes.level1Elm>());
						oneLv1.setFunctionPower(new ArrayList<DCP_PowerQueryRes.functionPower>());
						String modularNO = oneData.get("MODULARNO").toString();
						String modularName = oneData.get("MODULARNAME").toString();
						String sType = oneData.get("STYPE").toString();
						String isPower = oneData.get("ISPOWER").toString();
						oneLv1.setModularNo(modularNO);
						oneLv1.setModularName(modularName);
						oneLv1.setsType(sType);
						oneLv1.setIsPower(isPower);
						
						for (Map<String, Object> fstFuncDatas : getQHeader) 
						{
							if(modularNO.equals(fstFuncDatas.get("MODULARNO")) == false)
								continue;
							//在这里过滤除属于第一级的func
							DCP_PowerQueryRes.functionPower fstFunc = res.new functionPower();
							
							String funcNO = fstFuncDatas.get("FUNCNO").toString();
							if(funcNO.trim().equals("")) continue;//过滤掉空值
							
							String funcName = fstFuncDatas.get("FUNCNAME").toString();
							String isFPower = fstFuncDatas.get("ISFPOWER").toString();
							String powerType = fstFuncDatas.get("POWERTYPE").toString();
                            String proname = fstFuncDatas.get("PRONAME").toString();
                            fstFunc.setFuncName(funcName);
							fstFunc.setFuncNo(funcNO);
							fstFunc.setIsPower(isFPower);
							fstFunc.setPowerType(powerType);
                            fstFunc.setProName(proname);
							oneLv1.getFunctionPower().add(fstFunc);
							fstFunc=null;
						}
						
						setNextDatas(oneLv1,getQHeader);
						res.getPowerDatas().add(oneLv1);
						oneLv1=null;
					
					}
				}
			} //一级信息添加结束if 
			
		} catch (Exception e) {

			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");
			res.setPowerDatas(new ArrayList<DCP_PowerQueryRes.level1Elm>());
		}
			
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected String getQuerySql(DCP_PowerQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 查询第一级信息
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getQuerySql1(DCP_PowerQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType=req.getLangType();
		String opGroup="''";
		if (req.getRequest().getOpGroup().toString() != null) 
		{
			String[] opGroupList= req.getRequest().getOpGroup();
			
			for (String str : opGroupList)
			{	
				opGroup = opGroup + ",'" +str + "'";
			}
			
		}	
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("SELECT modularNO , modularName , uppermodular, modularlevel,sType,  funcNO,PRONAME, funcName , powerType, isPower,isFPower  FROM ("
					+ " SELECT a.priority,a.modularNO ,");
		if(langType.equals("zh_TW"))
		{
			sqlbuf.append("a.chtmsg  AS modularName,b.chtmsg AS funcname," );
		}else if(langType.equals("zh_EN"))	
			sqlbuf.append("a.engmsg  as modularName,b.engmsg AS funcname,");
			else
				sqlbuf.append("a.chsmsg  modularName,b.chsmsg AS funcname,");
		
		sqlbuf.append( " a.uppermodular,  a.sType , a.modularLevel , "
					+ " b.funcNO,b.PRONAME,  d.Powertype , "
					+ " ( CASE  WHEN c.modularNO IS NULL   THEN 'N' ELSE 'Y' END) AS  isPower ,"
					+ " ( CASE  WHEN d.funcNO  IS NULL   THEN 'N' ELSE 'Y' END) AS  isFPower   "
					+ " FROM  DCP_MODULAR a "
					+ " left join DCP_MODULAR_function b on a.modularno=b.modularno "
					+ "	and a.EID=b.EID and b.status='100' "
					+ " left join platform_billpower c on a.modularno=c.modularno and a.EID=c.EID "
					+ " and c.status='100' and c.opgroup  in ("+opGroup+")"
					+ " left join Platform_Power d on b.funcno=d.funcno and b.EID=d.EID "
					+ " and d.status='100' and d.opgroup  in ("+opGroup+")"
					+ " where a.EID='"+eId+"' and a.status='100' "
					+ "  order by a.priority,a.modularNO,b.funcno ) ");
		sql = sqlbuf.toString();
		
		//这里开始加入菜单注册管控
		StringBuffer regtypeString=new StringBuffer("");
		if(langType.equals("zh_CN"))
		{
			regtypeString.append(" and ( A.Rpattern='1' or A.Rpattern='3' )  ");
		}
		else
		{
			regtypeString.append(" and  ( A.Rpattern='2' or A.Rpattern='3' ) ");
		}
		
		regtypeString.append( " or A.MODULARNO='26' or A.MODULARNO='2602' or A.MODULARNO='2604'  " );
		String curdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		
		StringBuffer sqlregister=new StringBuffer(" select distinct A.MODULARNO  from DCP_MODULAR A left join platform_cregisterdetail B on A.rfuncno=B.producttype "
				+ " and B.BDATE<='"+curdate+"' AND '"+curdate+"'<=B.EDATE " 
				+ " where  A.EID='"+req.geteId()+"' and B.producttype is not null   " + regtypeString.toString() );
		
		if(!req.getOpNO().equals("NRC"))
		{
			sql= " select A.* from ( "+sql+" ) A inner join ( "+sqlregister.toString()+" ) B on A.MODULARNO=B.MODULARNO ";
		}
		
		return sql;
	}
	
	/**
	 * 查询func 信息
	 * @param allFuncDatas
	 * @param
	 * @return
	 */
	protected List<Map<String, Object>> getFuncDatas(List<Map<String, Object>> allFuncDatas,String modularNO)
	{
		List<Map<String, Object>> funcDataTemp =new ArrayList<>();
		for (Map<String, Object> map : allFuncDatas) 
		{
			if(map.get("MODULARNO").toString().equals(modularNO))
			{
				funcDataTemp.add(map);
			}
		}
		return funcDataTemp;
	}
	
	
	protected List<Map<String, Object>> getChildDatas(List<Map<String, Object>> allMenuDatas,String modularNO)
	{
		List<Map<String, Object>> menuDataTemp =new ArrayList<>();
		for (Map<String, Object> map : allMenuDatas) 
		{
			if(map.get("UPPERMODULAR").toString().equals(modularNO))
			{
				menuDataTemp.add(map);
			}
		}
		return menuDataTemp;
	}
	
	//这里写一个递归的调用当前的方法
	protected void setNextDatas(DCP_PowerQueryRes.level1Elm oneLv2,List<Map<String, Object>> getQHeader) throws Exception
	{
		try {
			List<Map<String, Object>> upModularList = getChildDatas(getQHeader,oneLv2.getModularNo());
			if(upModularList != null && !upModularList.isEmpty())
			{
				for (Map<String, Object> menuDatas : upModularList) 
				{
					DCP_PowerQueryRes res = new DCP_PowerQueryRes();
					DCP_PowerQueryRes.level1Elm lv1=res.new level1Elm();
					lv1.setChildren(new ArrayList<DCP_PowerQueryRes.level1Elm>());
					lv1.setFunctionPower(new ArrayList<DCP_PowerQueryRes.functionPower>());
					
					//设置响应
					String modularNO = menuDatas.get("MODULARNO").toString();
					String modularName = menuDatas.get("MODULARNAME").toString();
					String sType = menuDatas.get("STYPE").toString();
					String isPower = menuDatas.get("ISPOWER").toString();
					lv1.setModularNo(modularNO);
					lv1.setModularName(modularName);
					lv1.setsType(sType);
					lv1.setIsPower(isPower);
					
					List<Map<String, Object>> funcList = getFuncDatas(allFuncDatas,modularNO);
					if(funcList != null && !funcList.isEmpty())
					{
						for (Map<String, Object> fstFuncDatas : funcList) 
						{
							//过滤属于此单头的明细
							if(modularNO.equals(fstFuncDatas.get("MODULARNO")) == false)
								continue;
							//在这里过滤除属于第一级的function
							DCP_PowerQueryRes.functionPower fstFunc = res.new functionPower();
							
							String funcNO = fstFuncDatas.get("FUNCNO").toString();
							if(funcNO.trim().equals("")) continue;//过滤掉空值
							
							String funcName = fstFuncDatas.get("CHSMSG").toString();
							String isFPower = fstFuncDatas.get("ISFPOWER").toString();
							String powerType = fstFuncDatas.get("POWERTYPE").toString();
							String proname = fstFuncDatas.get("PRONAME").toString();
							fstFunc.setFuncName(funcName);
							fstFunc.setFuncNo(funcNO);
							fstFunc.setIsPower(isFPower);
							fstFunc.setPowerType(powerType);
							fstFunc.setProName(proname);
							lv1.getFunctionPower().add(fstFunc);
							fstFunc=null;
						}
					}
					setNextDatas(lv1,getQHeader);
					oneLv2.getChildren().add(lv1);
					lv1=null;
			  }
				
			}
		} catch (Exception e) {

		}
		
	}
	
	
	
}
