package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateEnableReq;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateEnableReq.levelTemplate;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateEnableRes;
import com.dsc.spos.model.Template_POS_ShopGoodsTemplateRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsTemplateEnable extends SPosAdvanceService<DCP_GoodsTemplateEnableReq,DCP_GoodsTemplateEnableRes>
{

	@Override
	protected void processDUID(DCP_GoodsTemplateEnableReq req, DCP_GoodsTemplateEnableRes res) throws Exception 
	{

        //同步缓存
        List<Template_POS_ShopGoodsTemplateRedisUpdate> templateList=new ArrayList<>();

		String eId=req.geteId();

		List<levelTemplate> templatList = req.getRequest().getTemplateList();
		String  oprType=req.getRequest().getOprType();//操作类型：1-启用2-禁用
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String status="";
		if (oprType.equals("1")) 
		{
			status="100";
		}
		else if (oprType.equals("2")) 
		{
			status="0";
		}
		else 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("操作类型的值不在规格中！");
			return;
		}	

		for (levelTemplate Template : templatList) 
		{
            //
            Template_POS_ShopGoodsTemplateRedisUpdate template_pos_shopGoodsTemplateRedisUpdate=new Template_POS_ShopGoodsTemplateRedisUpdate();
            template_pos_shopGoodsTemplateRedisUpdate.setTemplateId(Template.getTemplateId());
            templateList.add(template_pos_shopGoodsTemplateRedisUpdate);

			UptBean ub1 = new UptBean("DCP_GOODSTEMPLATE");
			//add Value
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub1.addUpdateValue("REDISUPDATESUCCESS", new DataValue("N", Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getEmployeeName(), Types.VARCHAR));

            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			//condition		
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
			ub1.addCondition("TEMPLATEID", new DataValue(Template.getTemplateId(), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
		}	    


		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");


        //同步缓存
        String posUrl = PosPub.getPOS_INNER_URL(req.geteId());
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
        PosPub.POS_ShopGoodsTemplateRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,templateList);

		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsTemplateEnableReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsTemplateEnableReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsTemplateEnableReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateEnableReq req) throws Exception 
	{		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		List<levelTemplate> templatList = req.getRequest().getTemplateList();

		String  oprType=req.getRequest().getOprType();//操作类型：1-启用2-禁用

		if(Check.Null(oprType))
		{
			errMsg.append("操作类型不能为空值 ");
			isFail = true;
		}

		if (templatList==null || templatList.size()==0) 
		{
			errMsg.append("模板对象不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (levelTemplate levelTemplate : templatList) 
		{
			if(Check.Null(levelTemplate.getTemplateId()))
			{
				errMsg.append("模板编码不能为空值 ");
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
	protected TypeToken<DCP_GoodsTemplateEnableReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsTemplateEnableReq>() {};
	}

	@Override
	protected DCP_GoodsTemplateEnableRes getResponseType() 
	{		
		return new DCP_GoodsTemplateEnableRes();
	}



}
