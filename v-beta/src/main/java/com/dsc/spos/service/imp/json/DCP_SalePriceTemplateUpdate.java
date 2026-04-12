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
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateUpdateReq.levelGoods;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateUpdateReq.levelRange;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateUpdateReq.levelTemplate;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateCreateRes;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateUpdateRes;
import com.dsc.spos.model.Template_POS_GoodsChannelPriceRedisUpdate;
import com.dsc.spos.model.Template_POS_ShopGoodsTemplateRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_SalePriceTemplateUpdate extends SPosAdvanceService<DCP_SalePriceTemplateUpdateReq, DCP_SalePriceTemplateUpdateRes>
{

	@Override
	protected void processDUID(DCP_SalePriceTemplateUpdateReq req, DCP_SalePriceTemplateUpdateRes res)
			throws Exception 
	{

        //同步缓存
        List<Template_POS_GoodsChannelPriceRedisUpdate> templateList=new ArrayList<>();


		String eId=req.geteId();


		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		String templateId = req.getRequest().getTemplateId();		
		String templateType=req.getRequest().getTemplateType();

		String restrictchannel=req.getRequest().getRestrictChannel();
		String channelId=req.getRequest().getChannelId();
		String status=req.getRequest().getStatus();
		String memo=req.getRequest().getMemo();
		List<levelRange> rangeList=req.getRequest().getRangeList();

		List<levelTemplate> templateName_lang=req.getRequest().getTemplateName_lang();

		String sql = "select templateid from DCP_SALEPRICETEMPLATE where eid='"+eId+"' and templateid='"+templateId+"' ";
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
		DelBean db1 = new DelBean("DCP_SALEPRICETEMPLATE_LANG");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));

		db1 = new DelBean("DCP_SALEPRICETEMPLATE_RANGE");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));


		String[] columns_DCP_SALEPRICETEMPLATE_LANG = 
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

			InsBean ib1 = new InsBean("DCP_SALEPRICETEMPLATE_LANG", columns_DCP_SALEPRICETEMPLATE_LANG);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增
		}	

		if (rangeList!=null && rangeList.size()>0) 
		{
			//
			String[] columns_DCP_SALEPRICETEMPLATE_RANGE = 
				{ 
						"EID","TEMPLATEID","RANGETYPE","ID","NAME",
						"LASTMODITIME"
				};
			for (levelRange range : rangeList) 
			{

				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR),
						new DataValue(templateType.equals("COMPANY")?1:2, Types.VARCHAR),
						new DataValue(range.getId(), Types.VARCHAR),
						new DataValue(range.getName(), Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE)				
				};

				InsBean ib1 = new InsBean("DCP_SALEPRICETEMPLATE_RANGE", columns_DCP_SALEPRICETEMPLATE_RANGE);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增			
			}
		}

		UptBean ub1 = new UptBean("DCP_SALEPRICETEMPLATE");
		ub1.addUpdateValue("TEMPLATETYPE", new DataValue(templateType,Types.VARCHAR));
		ub1.addUpdateValue("RESTRICTCHANNEL", new DataValue(restrictchannel,Types.VARCHAR));
		ub1.addUpdateValue("CHANNELID", new DataValue(channelId,Types.VARCHAR));
		ub1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(),Types.VARCHAR));
		ub1.addUpdateValue("STATUS", new DataValue(status,Types.INTEGER));
		ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));
        ub1.addUpdateValue("REDISUPDATESUCCESS", new DataValue("N",Types.VARCHAR));

		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(ub1));
		//
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
        //
        Template_POS_GoodsChannelPriceRedisUpdate pos_goodsChannelPriceRedisUpdate=new Template_POS_GoodsChannelPriceRedisUpdate();
        pos_goodsChannelPriceRedisUpdate.setTemplateId(req.getRequest().getTemplateId());
        templateList.add(pos_goodsChannelPriceRedisUpdate);

        PosPub.POS_GoodsChannelPriceRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,templateList);


		return;




	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SalePriceTemplateUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SalePriceTemplateUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SalePriceTemplateUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SalePriceTemplateUpdateReq req) throws Exception 
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

		String restrictchannel=req.getRequest().getRestrictChannel();
		String channelId=req.getRequest().getChannelId();
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

		if(Check.Null(restrictchannel))
		{
			errMsg.append("适用渠道不能为空值 ");
			isFail = true;
		}

		//指定渠道
		if (restrictchannel.equals("1"))
		{
			if(Check.Null(channelId))
			{
				errMsg.append("渠道编号不能为空值 ");
				isFail = true;
			}
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

		if (rangeList==null || rangeList.size()==0)
		{
			errMsg.append("适用公司或门店不能为空值 ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_SalePriceTemplateUpdateReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SalePriceTemplateUpdateReq>() {};
	}

	@Override
	protected DCP_SalePriceTemplateUpdateRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_SalePriceTemplateUpdateRes();
	}




}
