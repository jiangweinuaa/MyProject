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

import com.dsc.spos.json.cust.req.DCP_ClassGoodsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ClassGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_ClassGoodsUpdate  extends SPosAdvanceService<DCP_ClassGoodsUpdateReq, DCP_ClassGoodsUpdateRes>{

	@Override
	protected void processDUID(DCP_ClassGoodsUpdateReq req, DCP_ClassGoodsUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();

        //清缓存
        String posUrl = PosPub.getPOS_INNER_URL(eId);
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

		String curLangType = req.getLangType();
		String classType = req.getRequest().getClassType();
		String classNo = req.getRequest().getClassNo();
		String sortId = req.getRequest().getSortId();
		//String displayName = req.getRequest().getDisplayName();
		String pluNo = req.getRequest().getPluNo();

        String remind = req.getRequest().getRemind();
        String remindType = req.getRequest().getRemindType();

        String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		boolean isUpdateDB = false;
		
		UptBean up1 = new UptBean("DCP_CLASS_GOODS");
		up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		up1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
		up1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
		up1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
		
		if(sortId!=null&&sortId.isEmpty()==false)
		{
			isUpdateDB = true;
			up1.addUpdateValue("SORTID", new DataValue(sortId, Types.VARCHAR));
			up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			up1.addUpdateValue("REMIND", new DataValue(remind, Types.VARCHAR));
			up1.addUpdateValue("REMINDTYPE", new DataValue(remindType, Types.VARCHAR));

			this.addProcessData(new DataProcessBean(up1));
		}
	
		
		/*if(displayName!=null&&displayName.isEmpty()==false)
		{
			isUpdateDB = true;
			UptBean up2 = new UptBean("DCP_CLASS_GOODS_LANG");
			up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			up2.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
			up2.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
			up2.addCondition("LANG_TYPE", new DataValue(curLangType, Types.VARCHAR));		
			
			up2.addUpdateValue("DISPLAYNAME", new DataValue(displayName, Types.VARCHAR));			
			up2.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			
			this.addProcessData(new DataProcessBean(up2));
		}*/
		
		if(!isUpdateDB)
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("请传入需要更新的内容！");
			return;
		}
		
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ClassGoodsUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ClassGoodsUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ClassGoodsUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ClassGoodsUpdateReq req) throws Exception {
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
	protected TypeToken<DCP_ClassGoodsUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ClassGoodsUpdateReq>(){};
	}

	@Override
	protected DCP_ClassGoodsUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ClassGoodsUpdateRes();
	}

}
