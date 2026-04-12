package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ClassUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ClassUpdateReq.className;
import com.dsc.spos.json.cust.req.DCP_ClassUpdateReq.displayName;
import com.dsc.spos.json.cust.req.DCP_ClassUpdateReq.range;
import com.dsc.spos.json.cust.res.DCP_ClassUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_ClassUpdate extends SPosAdvanceService<DCP_ClassUpdateReq, DCP_ClassUpdateRes> {

	@Override
	protected void processDUID(DCP_ClassUpdateReq req, DCP_ClassUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
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

		//必传字段
		String classType = req.getRequest().getClassType();
		String classNo = req.getRequest().getClassNo();		
		String status = req.getRequest().getStatus();
		String remind = req.getRequest().getRemind();
		String label = req.getRequest().getLabel();
		List<DCP_ClassUpdateReq.className> className_lang = req.getRequest().getClassName_lang();
		List<DCP_ClassUpdateReq.displayName> displayName_lang = req.getRequest().getDisplayName_lang();
		List<DCP_ClassUpdateReq.range> rangeList = req.getRequest().getRangeList();	
		String sorId = req.getRequest().getSortId();
		String beginDate = req.getRequest().getBeginDate();//yyyy-mm-dd
		if(beginDate!=null)
		{
			beginDate = beginDate.replace("-", "");
		}
		String endDate = req.getRequest().getEndDate();//yyyy-mm-dd
		if(endDate!=null)
		{
			endDate = endDate.replace("-", "");
		}

		int subClassCount = 0;

		//非必须
		String levelId = req.getRequest().getLevelId();		
		String upClassNo = req.getRequest().getUpClassNo();
		String memo = req.getRequest().getMemo();
		String goodsSortType = req.getRequest().getGoodsSortType();
		//String restrictCompany = req.getRequest().getRestrictCompany();
		String restrictShop = req.getRequest().getRestrictShop();		
		String restrictChannel = req.getRequest().getRestrictChannel();		
		String restrictAppType = req.getRequest().getRestrictAppType();		
		String restrictPeriod = req.getRequest().getRestrictPeriod();
		String classImage = req.getRequest().getClassImage();
		String remindType = req.getRequest().getRemindType();
		String labelName = req.getRequest().getLabelName();
        String isShare = req.getRequest().getIsShare();

        // 是否可分享：0.否 1.是 默认否
        if(Check.Null(isShare)){
            isShare = "0";
        }


        String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}

		String sql = " select COUNT(*) AS SUBCLASSCOUNT from DCP_CLASS "
				+ "where eid='"+eId+"' and CLASSTYPE='"+classType+"' and CLASSNO='"+classNo+"' and UPCLASSNO='"+upClassNo+"' ";
		List<Map<String, Object>> downQty = this.doQueryData(sql, null);
		if (downQty != null && downQty.isEmpty() == false)
		{
			try 
			{
				subClassCount =  Integer.parseInt(downQty.get(0).get("SUBCLASSCOUNT").toString());				
			} catch (Exception e) {
				// TODO: handle exception

			}


		}



		DelBean db1 = new DelBean("DCP_CLASS_LANG");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
		db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
		db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));

		db1 = new DelBean("DCP_CLASS_RANGE");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
		db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
		db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_CLASS_IMAGE");
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db2.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

		UptBean up1 = new UptBean("DCP_CLASS");
		up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		up1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));	
		up1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));

		up1.addUpdateValue("LEVELID", new DataValue(levelId, Types.VARCHAR));
		up1.addUpdateValue("UPCLASSNO", new DataValue(upClassNo, Types.VARCHAR));
		up1.addUpdateValue("SUBCLASSCOUNT", new DataValue(subClassCount, Types.VARCHAR));
		//up1.addUpdateValue("CLASSGOODSCOUNT", new DataValue(levelId, Types.VARCHAR));
		up1.addUpdateValue("SORTID", new DataValue(sorId, Types.VARCHAR));
		up1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
		up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
		up1.addUpdateValue("BEGINDATE", new DataValue(beginDate, Types.VARCHAR));
		up1.addUpdateValue("ENDDATE", new DataValue(endDate, Types.VARCHAR));
		
		if(!Check.Null( req.getRequest().getGoodsSortType() )){
			up1.addUpdateValue("GOODSSORTTYPE", new DataValue(goodsSortType, Types.VARCHAR));
		}
		
		//up1.addUpdateValue("RESTRICTCOMPANY", new DataValue(restrictCompany, Types.VARCHAR));
		if(restrictShop!=null&&restrictShop.trim().isEmpty()==false)
		{
			up1.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
		}
		if(restrictChannel!=null&&restrictChannel.trim().isEmpty()==false)
		{
			up1.addUpdateValue("RESTRICTCHANNEL", new DataValue(restrictChannel, Types.VARCHAR));
		}
		if(restrictAppType!=null&&restrictAppType.trim().isEmpty()==false)
		{
			up1.addUpdateValue("RESTRICTAPPTYPE", new DataValue(restrictAppType, Types.VARCHAR));
		}
		if(restrictPeriod!=null&&restrictPeriod.trim().isEmpty()==false)
		{
			up1.addUpdateValue("RESTRICTPERIOD", new DataValue(restrictPeriod, Types.VARCHAR));			
		}

		up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
		up1.addUpdateValue("REMIND", new DataValue(remind, Types.VARCHAR));
		up1.addUpdateValue("REMINDTYPE", new DataValue(remindType, Types.VARCHAR));
		up1.addUpdateValue("LABEL", new DataValue(label, Types.VARCHAR));
		up1.addUpdateValue("LABELNAME", new DataValue(labelName, Types.VARCHAR));
		up1.addUpdateValue("ISSHARE", new DataValue(isShare, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(up1));
		String[] columns_class_lang =
			{
					"EID",
					"CLASSTYPE",
					"CLASSNO",
					"LANG_TYPE" ,
					"CLASSNAME",
					"DISPLAYNAME",
					"LASTMODITIME"

			};

		for (className lang : className_lang) 
		{		
			String langType = lang.getLangType();
			String className = lang.getName();
			String displayName = "";

			if (displayName_lang!=null)
			{
				for (displayName item : displayName_lang) 
				{
					if(langType.equals(item.getLangType()))
					{
						displayName = item.getName();
						break;
					}			
				}
			}


			DataValue[] insValue1 = null;			
			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(classType, Types.VARCHAR),
					new DataValue(classNo, Types.VARCHAR),
					new DataValue(langType, Types.VARCHAR),
					new DataValue(className, Types.VARCHAR),
					new DataValue(displayName, Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)
			};

			InsBean ib1 = new InsBean("DCP_CLASS_LANG", columns_class_lang);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); 

		}

		String[] columns_class_range =
			{
					"EID",
					"CLASSTYPE",
					"CLASSNO",
					"RANGETYPE" ,
					"ID",
					"NAME",
					"LASTMODITIME"

			};

		int sortId_range = 1;

		for (range par : rangeList) 
		{	

			DataValue[] insValue1 = null;			
			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(classType, Types.VARCHAR),
					new DataValue(classNo, Types.VARCHAR),
					new DataValue(par.getRangeType(), Types.VARCHAR),
					new DataValue(par.getId(), Types.VARCHAR),
					new DataValue(par.getName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)
			};

			InsBean ib1 = new InsBean("DCP_CLASS_RANGE", columns_class_range);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); 
			sortId_range++;
		}

       if(!Check.Null(classImage)){
		   // 新增 分组图片
		   String[] columns_class_image =
				   {
						   "EID",
						   "CLASSNO",
						   "CLASSIMAGE"
				   };
		   DataValue[] insValue_class_image = null;
		   insValue_class_image = new DataValue[]{
				   new DataValue(eId, Types.VARCHAR),
				   new DataValue(classNo, Types.VARCHAR),
				   new DataValue(classImage, Types.VARCHAR)
		   };

		   InsBean ib2 = new InsBean("DCP_CLASS_IMAGE", columns_class_image);
		   ib2.addValues(insValue_class_image);
		   this.addProcessData(new DataProcessBean(ib2));
	   }

		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ClassUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ClassUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ClassUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ClassUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		List<DCP_ClassUpdateReq.range> rangeList = req.getRequest().getRangeList();	
		String classType = req.getRequest().getClassType(); 
		String classNo = req.getRequest().getClassNo();

		String restrictShop = req.getRequest().getRestrictShop();
		String restrictChannel = req.getRequest().getRestrictChannel();
		String restrictPeriod = req.getRequest().getRestrictPeriod();

		if (req.getRequest().getClassName_lang()==null) 
		{
			errMsg.append("分类名称多语言不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if(Check.Null(classType)){
			errMsg.append("类型不能为空值 ，");
			isFail = true;
		}
		if(Check.Null(classNo)){
			errMsg.append("编码不能为空值 ，");
			isFail = true;
		}

		if(Check.Null(restrictShop))
		{
			errMsg.append("适用门店不能为空值 ，");
			isFail = true;
		}
		if(Check.Null(restrictChannel))
		{
			errMsg.append("适用渠道不能为空值 ，");
			isFail = true;
		}
		if(Check.Null(restrictPeriod))
		{
			errMsg.append("适用时段不能为空值 ，");
			isFail = true;
		}

		if (rangeList==null || rangeList.size()==0)
		{
			errMsg.append("适用范围列表不能为空值 ，");
			isFail = true;
		}


		boolean bCompany=false;
		boolean bShop=false;
		boolean bChannel=false;
		boolean bPeriod=false;

		if (rangeList!=null && rangeList.size()>0)
		{
			for (range range : rangeList)
			{
				//适用范围：1-公司2-门店3-渠道4-应用 5-时段
				if(Check.Null(range.getRangeType()))
				{
					errMsg.append("适用范围列表-类型rangeType不能为空值 ，");
					isFail = true;

					break;
				}

				if (range.getRangeType().equals("1"))
				{
					bCompany=true;
				}		

				if (restrictShop.equals("0") || ( restrictShop.equals("0")==false && range.getRangeType().equals("2")))
				{
					bShop=true;
				}
			
				if (restrictChannel.equals("0") ||(restrictChannel.equals("0")==false && range.getRangeType().equals("3")))
				{
					bChannel=true;
				}
				
				if (restrictPeriod.equals("0")|| (restrictPeriod.equals("0")==false && range.getRangeType().equals("5")))
				{
					bPeriod=true;
				}			
			}
		}
		else 
		{
			if (restrictShop.equals("0") )
			{
				bShop=true;
			}
			if (restrictChannel.equals("0") )
			{
				bChannel=true;
			}
			if (restrictPeriod.equals("0") )
			{
				bPeriod=true;
			}			
		}

		if (bCompany==false)
		{
			errMsg.append("未设置适用公司 ，");
			isFail = true;
		}

		if (bShop==false)
		{
			errMsg.append("未设置适用门店 ，");
			isFail = true;
		}


		if (bChannel==false)
		{
			errMsg.append("未设置适用渠道 ，");
			isFail = true;
		}

		if (bPeriod==false)
		{
			errMsg.append("未设置适用时段 ，");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ClassUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ClassUpdateReq>(){};
	}

	@Override
	protected DCP_ClassUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ClassUpdateRes();
	}

}
