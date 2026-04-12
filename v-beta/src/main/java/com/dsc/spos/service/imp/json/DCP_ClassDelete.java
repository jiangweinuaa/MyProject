package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ClassDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ClassDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ClassDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_ClassDelete extends SPosAdvanceService<DCP_ClassDeleteReq, DCP_ClassDeleteRes> {

	@Override
	protected void processDUID(DCP_ClassDeleteReq req, DCP_ClassDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{

			String eId= req.geteId();		
            //清缓存
            String posUrl =  PosPub.getPOS_INNER_URL(eId);
            String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
                    " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
            List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
            String apiUserCode = "";
            String apiUserKey = "";
            if (result != null && result.size() == 2) {
                for (Map<String, Object> map : result) {
                    if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
                        apiUserCode = map.get("ITEMVALUE").toString();
                    } else {
                        apiUserKey = map.get("ITEMVALUE").toString();
                    }
                }
            }
            PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,eId);

		
			String sql = "";
			for (level1Elm par : req.getRequest().getClassList())
			{
				String classType = par.getClassType();
				String classNo = par.getClassNo();	
				sql = "";
				sql = "select status from DCP_CLASS "
					+ "where  eid='"+eId+"' and CLASSTYPE='"+classType+"' and CLASSNO='"+classNo+"' ";
				List<Map<String, Object>> getData = this.doQueryData(sql, null);
				if(getData==null||getData.isEmpty())
				{
					continue;
				}
				
				
				DelBean db1 = new DelBean("DCP_CLASS_LANG");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
				db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
				db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

                db1 = new DelBean("DCP_CLASS_IMAGE");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
				
				db1 = new DelBean("DCP_CLASS_RANGE");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
				db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
				db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				db1 = new UptBean("DCP_CLASS");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));	
				db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
				
				this.addProcessData(new DataProcessBean(db1));
			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("服务执行异常:"+e.getMessage());
		}
		
		
		
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ClassDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ClassDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ClassDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ClassDeleteReq req) throws Exception {
	// TODO Auto-generated method stub

	boolean isFail = false;
  StringBuffer errMsg = new StringBuffer("");

  if(req.getRequest()==null)
  {
  	errMsg.append("request不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
  
  if (req.getRequest().getClassList()==null) 
  {
  	errMsg.append("编码不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}
  
  
  for (level1Elm par : req.getRequest().getClassList())
	{
  	String classType = par.getClassType(); 
  	String classNo = par.getClassNo();
    if(Check.Null(classType)){
     	errMsg.append("类型不能为空值 ，");
     	isFail = true;
    }
    if(Check.Null(classNo)){
     	errMsg.append("编码不能为空值 ，");
     	isFail = true;
    }
	}
  
 
  
	if (isFail)
	{
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}
	
	return isFail;
	}

	@Override
	protected TypeToken<DCP_ClassDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ClassDeleteReq>(){};
	}

	@Override
	protected DCP_ClassDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ClassDeleteRes();
	}

}
