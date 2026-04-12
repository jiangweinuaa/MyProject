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
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateUpdateReq.levelRange;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateUpdateReq.levelTemplate;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateUpdateRes;
import com.dsc.spos.model.Plu_POS_GoodsPriceRedisUpdate;
import com.dsc.spos.model.Template_POS_ShopGoodsTemplateRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsTemplateUpdate extends SPosAdvanceService<DCP_GoodsTemplateUpdateReq,DCP_GoodsTemplateUpdateRes>
{

	@Override
	protected void processDUID(DCP_GoodsTemplateUpdateReq req, DCP_GoodsTemplateUpdateRes res) throws Exception 
	{

		String eId=req.geteId();

		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		String templateId = req.getRequest().getTemplateId();		
		String templateType=req.getRequest().getTemplateType();
				
		String status=req.getRequest().getStatus();
		String memo=req.getRequest().getMemo();
		List<levelRange> rangeList=req.getRequest().getRangeList();

		List<levelTemplate> templateName_lang=req.getRequest().getTemplateName_lang();		

		String sql = "select templateid from dcp_goodstemplate where eid='"+eId+"' and templateid='"+templateId+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData==null || getData.isEmpty()) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此模板编码不存在！");
			return;
		}


		if (templateName_lang.size()==0) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("模板多语言必须有值！");
			return;
		}
		
		

		
		//删除之前的,再插入
		DelBean db1 = new DelBean("DCP_GOODSTEMPLATE");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));	
		
		db1 = new DelBean("DCP_GOODSTEMPLATE_LANG");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));
		
		
		db1 = new DelBean("DCP_GOODSTEMPLATE_RANGE");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));
		

		String[] columns_DCP_GOODSTEMPLATE_LANG = 
			{ 
					"EID","TEMPLATEID","LANG_TYPE","TEMPLATENAME","LASTMODITIME"
			};
		for (levelTemplate template : templateName_lang) 
		{

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(templateId, Types.VARCHAR),
					new DataValue(template.getLangType(), Types.VARCHAR),
					new DataValue(template.getName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_GOODSTEMPLATE_LANG", columns_DCP_GOODSTEMPLATE_LANG);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增
		}		

		
		if (rangeList!=null && rangeList.size()>0) 
		{
			//
			String[] columns_DCP_GOODSTEMPLATE_RANGE = 
				{ 
						"EID","TEMPLATEID","RANGETYPE","ID","NAME",
						"LASTMODITIME"
				};
			for (levelRange range : rangeList) 
			{
				String rangeType ="";//适用范围：1-公司 2-门店

				if (templateType.equals("COMPANY"))
				{
					rangeType ="1";
				}
				else if (templateType.equals("SHOP")) 
				{
					rangeType ="2";
				}
				else if (templateType.equals("CHANNEL")) 
				{
					rangeType ="3";
				}
				else 
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板适用对象templateType枚举值未定义！");
				}
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR),
						new DataValue(rangeType, Types.VARCHAR),
						new DataValue(range.getId(), Types.VARCHAR),
						new DataValue(range.getName(), Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE)				
				};

				InsBean ib1 = new InsBean("DCP_GOODSTEMPLATE_RANGE", columns_DCP_GOODSTEMPLATE_RANGE);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增			
			}
			
		}



		//
		String[] columns_DCP_GOODSTEMPLATE = 
			{ 
					"EID","TEMPLATEID","TEMPLATETYPE","MEMO","STATUS","CREATEOPID",
					"CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","CREATEDEPTID"
			};
		DataValue[] insValue1 = null;

		insValue1 = new DataValue[]{
				new DataValue(eId, Types.VARCHAR),
				new DataValue(templateId, Types.VARCHAR),
				new DataValue(templateType, Types.VARCHAR),				
							
				new DataValue(memo, Types.VARCHAR),
				new DataValue(status, Types.VARCHAR),
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE),
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE),
				new DataValue(req.getDepartmentNo(), Types.VARCHAR),
		};

		InsBean ib1 = new InsBean("DCP_GOODSTEMPLATE", columns_DCP_GOODSTEMPLATE);
		ib1.addValues(insValue1);
		this.addProcessData(new DataProcessBean(ib1)); // 新增

		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");



        //同步缓存
        List<Template_POS_ShopGoodsTemplateRedisUpdate> templateList=new ArrayList<>();
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
        //
        Template_POS_ShopGoodsTemplateRedisUpdate template_pos_shopGoodsTemplateRedisUpdate=new Template_POS_ShopGoodsTemplateRedisUpdate();
        template_pos_shopGoodsTemplateRedisUpdate.setTemplateId(req.getRequest().getTemplateId());
        templateList.add(template_pos_shopGoodsTemplateRedisUpdate);

        PosPub.POS_ShopGoodsTemplateRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,templateList);

        return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsTemplateUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsTemplateUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsTemplateUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String templateId = req.getRequest().getTemplateId();		
		String templateType=req.getRequest().getTemplateType();
		
		String status=req.getRequest().getStatus();

		List<levelTemplate> templateName_lang=req.getRequest().getTemplateName_lang();
		List<levelRange> rangeList=req.getRequest().getRangeList();

		if(Check.Null(templateId))
		{
			errMsg.append("模板编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(templateType))
		{
			errMsg.append("模板类型不能为空值 ");
			isFail = true;
		}
	
		
		if(Check.Null(status))
		{
			errMsg.append("状态不能为空值 ");
			isFail = true;
		}
		if(templateName_lang==null)
		{
			errMsg.append("模板多语言不能为空值 ");
			isFail = true;
		}
		if(rangeList==null || rangeList.size()==0)
		{
			errMsg.append("适用范围不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsTemplateUpdateReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsTemplateUpdateReq>() {};
	}

	@Override
	protected DCP_GoodsTemplateUpdateRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsTemplateUpdateRes();
	}




}
