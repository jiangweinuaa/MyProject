package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ClassCreateReq;
import com.dsc.spos.json.cust.req.DCP_ClassCreateReq.className;
import com.dsc.spos.json.cust.req.DCP_ClassCreateReq.displayName;
import com.dsc.spos.json.cust.req.DCP_ClassCreateReq.range;
import com.dsc.spos.json.cust.res.DCP_ClassCreateRes;
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

public class DCP_ClassCreate extends SPosAdvanceService<DCP_ClassCreateReq, DCP_ClassCreateRes> {

	@Override
	protected void processDUID(DCP_ClassCreateReq req, DCP_ClassCreateRes res) throws Exception {
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
		List<DCP_ClassCreateReq.className> className_lang = req.getRequest().getClassName_lang();
		List<DCP_ClassCreateReq.displayName> displayName_lang = req.getRequest().getDisplayName_lang();
		List<DCP_ClassCreateReq.range> rangeList = req.getRequest().getRangeList();	
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
		String classImage = req.getRequest().getClassImage();
		String remindType = req.getRequest().getRemindType();
		String labelName = req.getRequest().getLabelName();
        String isShare = req.getRequest().getIsShare();

        // 是否可分享：0.否 1.是 默认否
        if(Check.Null(isShare)){
            isShare = "0";
        }

        //String restrictCompany = req.getRequest().getRestrictCompany();
		String restrictShop = req.getRequest().getRestrictShop();
		if(restrictShop==null||restrictShop.isEmpty())
		{
			restrictShop ="0";
		}
		String restrictChannel = req.getRequest().getRestrictChannel();
		if(restrictChannel==null||restrictChannel.isEmpty())
		{
			restrictChannel ="0";
		}
		String restrictAppType = req.getRequest().getRestrictAppType();
		if(restrictAppType==null||restrictAppType.isEmpty())
		{
			restrictAppType ="0";
		}
		String restrictPeriod = req.getRequest().getRestrictPeriod();
		if(restrictPeriod==null||restrictPeriod.isEmpty())
		{
			restrictPeriod ="0";
		}

		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}

		if (this.isExist(req)) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "该菜单类型："+classType+" 菜单编码："+classNo+"已存在！");
		}

		String[] columns_class ={"EID","CLASSTYPE","CLASSNO","LEVELID" ,"UPCLASSNO","SUBCLASSCOUNT","SORTID",
				"MEMO","STATUS","BEGINDATE", "ENDDATE","GOODSSORTTYPE","RESTRICTSHOP","RESTRICTCHANNEL",
				"RESTRICTAPPTYPE","RESTRICTPERIOD","CREATETIME","REMIND","REMINDTYPE","LABEL","LABELNAME","ISSHARE"
		};

		DataValue[] insValue_class = new DataValue[] 
				{
						new DataValue(eId, Types.VARCHAR),						
						new DataValue(classType, Types.VARCHAR),
						new DataValue(classNo, Types.VARCHAR),
						new DataValue(levelId,Types.VARCHAR),
						new DataValue(upClassNo, Types.VARCHAR),
						new DataValue(subClassCount, Types.VARCHAR),
						new DataValue(sorId ,Types.VARCHAR),
						new DataValue(memo ,Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(beginDate, Types.VARCHAR),
						new DataValue(endDate,Types.VARCHAR),
						new DataValue(goodsSortType,Types.VARCHAR),					
						new DataValue(restrictShop, Types.VARCHAR), //修改人、时间等信息
						new DataValue(restrictChannel ,Types.VARCHAR),
						new DataValue(restrictAppType ,Types.VARCHAR),
						new DataValue(restrictPeriod ,Types.VARCHAR),
						new DataValue(lastmoditime , Types.DATE),
						new DataValue(remind ,Types.VARCHAR),
						new DataValue(remindType ,Types.VARCHAR),
						new DataValue(label ,Types.VARCHAR),
						new DataValue(labelName,Types.VARCHAR),
						new DataValue(isShare,Types.VARCHAR)

				};

		InsBean ib_class = new InsBean("DCP_CLASS", columns_class);
		ib_class.addValues(insValue_class);
		this.addProcessData(new DataProcessBean(ib_class)); 

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
	protected List<InsBean> prepareInsertData(DCP_ClassCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ClassCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ClassCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ClassCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		List<DCP_ClassCreateReq.range> rangeList = req.getRequest().getRangeList();	
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
	protected TypeToken<DCP_ClassCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ClassCreateReq>(){};
	}

	@Override
	protected DCP_ClassCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ClassCreateRes();
	}

	private Boolean isExist(DCP_ClassCreateReq req) throws Exception
	{
		boolean nRet = false;

//		String sql = " select * from DCP_CLASS where eid='"+req.geteId()+"' "
//				+ "and CLASSTYPE='"+req.getRequest().getClassType()+"' and CLASSNO='"+req.getRequest().getClassNo()+"' ";
		String sql = " select * from DCP_CLASS where eid='"+req.geteId()+"' "
				+ " and CLASSNO='"+req.getRequest().getClassNo()+"' ";

		List<Map<String, Object>> mapList = this.doQueryData(sql, null);
		if (mapList!=null&&mapList.isEmpty()==false)
		{
			nRet = true;	
		}

		return nRet;


	}

}
