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

import com.dsc.spos.json.cust.req.DCP_ClassGoodsDisplayNameUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ClassGoodsDisplayNameUpdateReq.displayName;
import com.dsc.spos.json.cust.res.DCP_ClassGoodsDisplayNameUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_ClassGoodsDisplayNameUpdate  extends SPosAdvanceService<DCP_ClassGoodsDisplayNameUpdateReq, DCP_ClassGoodsDisplayNameUpdateRes>{

	@Override
	protected void processDUID(DCP_ClassGoodsDisplayNameUpdateReq req, DCP_ClassGoodsDisplayNameUpdateRes res) throws Exception 
	{

		 String eId = req.geteId();
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

       
		String classType = req.getRequest().getClassType();
		String classNo = req.getRequest().getClassNo();		
		String pluNo = req.getRequest().getPluNo();
		List<displayName> displayName_lang = req.getRequest().getDisplayName_lang();
		
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
				
		
		DelBean db1 = new UptBean("DCP_CLASS_GOODS_LANG");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
		db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(db1));
	
		String[] columns_class_lang =
			{
					"EID",
					"CLASSTYPE",
					"CLASSNO",
					"PLUNO",
					"LANG_TYPE" ,
					"DISPLAYNAME",					
				  "LASTMODITIME"
		
			};
		
		if (displayName_lang!=null)
		{
			for (displayName lang : displayName_lang) 
			{
				String langType = lang.getLangType();
				String displayName = lang.getName();
				
				
				DataValue[] insValue1 = null;			
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(classType, Types.VARCHAR),
						new DataValue(classNo, Types.VARCHAR),
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(langType, Types.VARCHAR),					
						new DataValue(displayName, Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE)
					};
				
				InsBean ib1 = new InsBean("DCP_CLASS_GOODS_LANG", columns_class_lang);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); 
				
			}
		}	
		
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ClassGoodsDisplayNameUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ClassGoodsDisplayNameUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ClassGoodsDisplayNameUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ClassGoodsDisplayNameUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
	  StringBuffer errMsg = new StringBuffer("");
	
	  if(req.getRequest()==null)
	  {
	  	errMsg.append("request不能为空值 ");
	  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }	  
	      
	  if(Check.Null(req.getRequest().getClassType())){
	   	errMsg.append("菜单类型不能为空值， ");
	   	isFail = true;
	
	  }
	  if(Check.Null(req.getRequest().getClassNo())){
	   	errMsg.append("分类编码不能为空值， ");
	   	isFail = true;
	
	  }
	  if(Check.Null(req.getRequest().getPluNo())){
	   	errMsg.append("商品编码不能为空值，");
	   	isFail = true;
	
	  }
	 
	  if (isFail)
	  {
	 	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
	  
	  return isFail;
	
	}

	@Override
	protected TypeToken<DCP_ClassGoodsDisplayNameUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ClassGoodsDisplayNameUpdateReq>(){};
	}

	@Override
	protected DCP_ClassGoodsDisplayNameUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ClassGoodsDisplayNameUpdateRes();
	}

}
