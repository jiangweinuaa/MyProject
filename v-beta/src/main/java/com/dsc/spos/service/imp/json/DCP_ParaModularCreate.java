package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ParaModularCreateReq;
import com.dsc.spos.json.cust.req.DCP_ParaModularCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ParaModularCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_ParaModularCreate 說明：参数分类新增
 * @author yuanyy
 * @since 2020-05-29
 */
public class DCP_ParaModularCreate extends SPosAdvanceService<DCP_ParaModularCreateReq, DCP_ParaModularCreateRes> {
	
	@Override
	protected void processDUID(DCP_ParaModularCreateReq req, DCP_ParaModularCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		// PLATFORM_MODULAR_FIXED
		try {
			level1Elm reqDatas = req.getRequest();
			
			if(reqDatas != null ){
				
				String modularStr = reqDatas.getModularId(); 
//				// 先验证所传classNo 是否已存在
//				String[] modularArr = new String[req.getDatas().size()] ;
//				int i=0;
//				for (level1Elm lv1 : reqDatas) 
//				{
//					String modularId = "";
//					if(!Check.Null(lv1.getModularId())){
//						modularId = lv1.getModularId();
//					}
//					modularArr[i] = modularId;
//					i++;
//				}
//				String modularStr = getString(modularArr);
				
				String sql = "select MODULARID, MODULARNAME from PLATFORM_MODULAR_FIXED where modularId in ( '"+modularStr+"' )";
				List<Map<String, Object>> repeatDatas = this.doQueryData(sql, null);
				if(repeatDatas != null && !repeatDatas.isEmpty() ){
					
					String existMsg = "";
					for (Map<String, Object> map : repeatDatas) {
						String modularId = map.get("MODULARID").toString();
						String modularName = map.get("MODULARNAME").toString();
						existMsg = existMsg + modularName+"("+modularId+")  " ;
					}
					
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, existMsg +" 已存在，请勿重复添加" );
				}
				else{
					String[] columns1 = {"MODULARID","MODULARNAME","MODIFYER"};
					String modularId= reqDatas.getModularId();
					String modularName = reqDatas.getModularName();
					String opNo = req.getOpNO();
					
					DataValue[] insValue1 = null;
					insValue1 = new DataValue[]{
							new DataValue(modularId, Types.VARCHAR),
							new DataValue(modularName, Types.VARCHAR),
							new DataValue(opNo, Types.VARCHAR)
					};
					InsBean ib1 = new InsBean("PLATFORM_MODULAR_FIXED", columns1);
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
	protected List<InsBean> prepareInsertData(DCP_ParaModularCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ParaModularCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ParaModularCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ParaModularCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ParaModularCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ParaModularCreateReq>(){};
	}

	@Override
	protected DCP_ParaModularCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ParaModularCreateRes();
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
