package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ParaClassCreateReq;
import com.dsc.spos.json.cust.req.DCP_ParaClassCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ParaClassCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_ParaClassCreate 說明：参数分类新增
 * @author yuanyy
 * @since 2020-05-29
 */
public class DCP_ParaClassCreate extends SPosAdvanceService<DCP_ParaClassCreateReq, DCP_ParaClassCreateRes> {
	
	@Override
	protected void processDUID(DCP_ParaClassCreateReq req, DCP_ParaClassCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		// PLATFORM_PARAMCLASS
		try {
			level1Elm reqDatas = req.getRequest();
			
			if(reqDatas != null ){
				
				String classNoStr = reqDatas.getClassNo();
				
				// 先验证所传classNo 是否已存在
//				String[] classNoArr = new String[req.getDatas().size()] ;
//				int i=0;
//				for (level1Elm lv1 : reqDatas) 
//				{
//					String classNo="";
//					if(!Check.Null(lv1.getClassNo())){
//						classNo = lv1.getClassNo();
//					}
//					classNoArr[i] = classNo;
//					i++;
//				}
//				String classNoStr = getString(classNoArr);
				
				String sql = "select CLASSNO, CLASSNAME, CLASSNAME_TW, CLASSNAME_EN from PLATFORM_PARAMCLASS where classNo in ( '"+classNoStr+"' )";
				List<Map<String, Object>> repeatDatas = this.doQueryData(sql, null);
				if(repeatDatas != null && !repeatDatas.isEmpty() ){
					
					String existMsg = "";
					for (Map<String, Object> map : repeatDatas) {
						String classNo = map.get("CLASSNO").toString();
						String className = map.get("CLASSNAME").toString();
						existMsg = existMsg + className+"("+classNo+")  " ;
					}
					
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, existMsg +" 已存在，请勿重复添加" );
				}
				else{
					String[] columns1 = {"CLASSNO","CLASSNAME","CLASSNAME_TW","CLASSNAME_EN","LASTMODIOPID"};
					String classNo= reqDatas.getClassNo();
					String className = reqDatas.getClassName();
					String className_TW = reqDatas.getClassName_TW();
					String className_EN = reqDatas.getClassName_EN();
					String opNo = req.getOpNO();
					
					DataValue[] insValue1 = null;
					insValue1 = new DataValue[]{
							new DataValue(classNo, Types.VARCHAR),
							new DataValue(className, Types.VARCHAR),
							new DataValue(className_TW, Types.VARCHAR),
							new DataValue(className_EN, Types.VARCHAR),
							new DataValue(opNo, Types.VARCHAR)
					};
					InsBean ib1 = new InsBean("PLATFORM_PARAMCLASS", columns1);
					ib1.addValues(insValue1);
					this.addProcessData(new DataProcessBean(ib1));
					
					this.doExecuteDataToDB();
					
				}
				
			}
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ParaClassCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ParaClassCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ParaClassCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ParaClassCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ParaClassCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ParaClassCreateReq>(){};
	}

	@Override
	protected DCP_ParaClassCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ParaClassCreateRes();
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
	
	
	
}
