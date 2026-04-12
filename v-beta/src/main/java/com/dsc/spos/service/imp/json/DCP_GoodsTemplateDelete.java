package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateDeleteReq;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateDeleteReq.levelTemplate;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateDeleteRes;
import com.dsc.spos.model.Template_POS_ShopGoodsTemplateRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsTemplateDelete extends SPosAdvanceService<DCP_GoodsTemplateDeleteReq,DCP_GoodsTemplateDeleteRes>
{

	@Override
	protected void processDUID(DCP_GoodsTemplateDeleteReq req, DCP_GoodsTemplateDeleteRes res) throws Exception 
	{
        //同步缓存
        List<Template_POS_ShopGoodsTemplateRedisUpdate> templateList=new ArrayList<>();

		String eId=req.geteId();

		List<levelTemplate> templatList = req.getRequest().getTemplateList();

		//删除失败标记
		boolean b_Fail=false;
		StringBuffer sb=new StringBuffer();

		for (levelTemplate levelTemplate : templatList) 
		{
            //
            Template_POS_ShopGoodsTemplateRedisUpdate template_pos_shopGoodsTemplateRedisUpdate=new Template_POS_ShopGoodsTemplateRedisUpdate();
            template_pos_shopGoodsTemplateRedisUpdate.setTemplateId(levelTemplate.getTemplateId());
            templateList.add(template_pos_shopGoodsTemplateRedisUpdate);

			String templateId=levelTemplate.getTemplateId();

            String sql = "select STATUS,REDISUPDATESUCCESS from dcp_goodstemplate where eid='"+eId+"' and templateid='"+templateId+"' ";
            List<Map<String , Object>> getData=this.doQueryData(sql, null);
            if (getData!=null && getData.isEmpty()==false)
            {
                String status=getData.get(0).get("STATUS").toString();//状态：-1未启用100已启用 0已禁用
                String redisupdatesuccess=getData.get(0).get("REDISUPDATESUCCESS").toString();

                //启用状态不能删除、禁用状态且未同步缓存也不能删除
                if (status.equals("100")||(redisupdatesuccess.equals("N") && status.equals("0")))
                {
                    b_Fail=true;
                    sb.append("模板编号："+templateId+",启用状态不能删除、禁用状态且未同步缓存也不能删除！<br/>");
                    continue;
                }
                else
				{
					DelBean db1 = new DelBean("DCP_GOODSTEMPLATE");
					db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
					db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1));

					db1 = new DelBean("DCP_GOODSTEMPLATE_LANG");
					db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
					db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1));

					db1 = new DelBean("DCP_GOODSTEMPLATE_GOODS");
					db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
					db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1));

					db1 = new DelBean("DCP_GOODSTEMPLATE_RANGE");
					db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
					db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1));
				}
            }
		}	

		//
		this.doExecuteDataToDB();

		if (b_Fail)
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription(sb.toString());
		}
		else
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
		}



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
	protected List<InsBean> prepareInsertData(DCP_GoodsTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateDeleteReq req) throws Exception 
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
	protected TypeToken<DCP_GoodsTemplateDeleteReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsTemplateDeleteReq>() {};
	}

	@Override
	protected DCP_GoodsTemplateDeleteRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsTemplateDeleteRes();
	}




}
