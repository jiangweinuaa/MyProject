package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_ModularDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ModularDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ModularDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ModularDelete extends SPosAdvanceService<DCP_ModularDeleteReq,DCP_ModularDeleteRes>
{

	@Override
	protected void processDUID(DCP_ModularDeleteReq req, DCP_ModularDeleteRes res) throws Exception 
	{

		String eId = req.geteId();
		level1Elm request = req.getRequest();
		String modularNO = request.getModularNo();

		UptBean ub1 = new UptBean("DCP_MODULAR");			
		ub1.addCondition("PRIORITY",new DataValue( request.getPriority(), Types.VARCHAR,DataExpression.GreaterEQ));
		ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		ub1.addCondition("MODULARLEVEL", new DataValue(request.getModularLevel(), Types.VARCHAR));

		ub1.addUpdateValue("PRIORITY",new DataValue(1,Types.INTEGER,DataExpression.SubSelf)); 
		this.addProcessData(new DataProcessBean(ub1));

		//删除第一级
		DelBean db1 = new DelBean("DCP_MODULAR_FUNCTION");
		db1.addCondition("MODULARNO", new DataValue(modularNO, Types.VARCHAR));
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));  

		DelBean db2 = new DelBean("DCP_MODULAR");
		db2.addCondition("MODULARNO", new DataValue(modularNO, Types.VARCHAR));
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2));  

		//删除第二级
		String sql =  "" ; 
		sql = this.getSqlNext(req,modularNO);

		String[] conditionValues1 = {}; //查詢條件

		List<Map<String, Object>> getNextData=this.doQueryData(sql, conditionValues1);

		if (getNextData != null && getNextData.isEmpty() == false)
		{
			String[] mod = {};

			for (Map<String, Object> oneData1 : getNextData) 
			{
				String nextMpdularNo = oneData1.get("MODULARNO").toString();
				mod = insert(mod, "'"+nextMpdularNo+"'");
			}

			String modularStr=String.join(",",mod);// (Java8) 使用join方法链接字符串
			System.out.println(modularStr);

			DelBean db3 = new DelBean("DCP_MODULAR");
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("MODULARNO", new DataValue(modularStr, Types.VARCHAR,DataExpression.IN));
			this.addProcessData(new DataProcessBean(db3));  

			DelBean db4 = new DelBean("DCP_MODULAR_FUNCTION");
			db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db4.addCondition("MODULARNO", new DataValue(modularStr, Types.VARCHAR,DataExpression.IN));
			this.addProcessData(new DataProcessBean(db4));  

		}
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ModularDeleteReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ModularDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ModularDeleteReq req) throws Exception 
	{		
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ModularDeleteReq req) throws Exception 
	{	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();

		String modularNO = request.getModularNo();

		if (Check.Null(modularNO)) 
		{
			isFail = true;
			errMsg.append("菜单编码不可为空值, ");
		}  

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ModularDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ModularDeleteReq>(){};
	}

	@Override
	protected DCP_ModularDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ModularDeleteRes();
	}


	protected String getSqlNext(DCP_ModularDeleteReq req,String modularNO) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;

		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("SELECT modularNo ,uppermodular , modularlevel , priority "
				+ " FROM DCP_MODULAR "
				+ " WHERE EID = '"+req.geteId()+"' "
				+ " START WITH uppermodular  = '"+modularNO+"' "
				+ " CONNECT BY PRIOR modularNO = uppermodular "
				+ " ORDER BY modularlevel , priority ");

		sql=sqlbuf.toString();
		return sql;
	}

	/**
	 * 动态往字符串数组中添加元素
	 * @param arr
	 * @param str
	 * @return
	 */
	private static String[] insert(String[] arr, String str)
	{
		int size = arr.length;
		String[] tmp = new String[size + 1];
		System.arraycopy(arr, 0, tmp, 0, size);
		tmp[size] = str;
		return tmp;
	}


}
