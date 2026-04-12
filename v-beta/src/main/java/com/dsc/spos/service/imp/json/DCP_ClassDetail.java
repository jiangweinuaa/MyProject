package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ClassDetailReq;
import com.dsc.spos.json.cust.res.DCP_ClassDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ClassDetail  extends SPosBasicService<DCP_ClassDetailReq, DCP_ClassDetailRes>{

	@Override
	protected boolean isVerifyFail(DCP_ClassDetailReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
	  StringBuffer errMsg = new StringBuffer("");

	  if(req.getRequest()==null)
	  {
	  	isFail = true;
	  	errMsg.append("request不能为空 ");
	  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
	  
	  if(Check.Null(req.getRequest().getClassType()))
	  {
	  	isFail = true;
	  	errMsg.append("菜单类型不能为空 ");
	  	
	  }
	  if (isFail)
	  {
	 	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
	
	  return isFail;
	}

	@Override
	protected TypeToken<DCP_ClassDetailReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ClassDetailReq>(){};
	}

	@Override
	protected DCP_ClassDetailRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ClassDetailRes();
	}

	@Override
	protected DCP_ClassDetailRes processJson(DCP_ClassDetailReq req) throws Exception 
	{	
		DCP_ClassDetailRes res = this.getResponse();
		res.setDatas(new ArrayList<DCP_ClassDetailRes.level1Elm>());
		String sql = this.getQuerySql(req);
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
		if(getQDataDetail!=null&&getQDataDetail.isEmpty()==false)
		{
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
			//condition.put("CLASSTYPE", true);
			condition.put("CLASSNO", true);
			//调用过滤函数
			List<Map<String, Object>> catDatas = MapDistinct.getMap(getQDataDetail, condition);
			condition.clear();
			condition.put("CLASSNO", true);
			condition.put("LANG_TYPE", true);
			List<Map<String, Object>> langDatas = MapDistinct.getMap(getQDataDetail, condition);
			
			condition.clear();
			condition.put("CLASSNO", true);
			condition.put("RANGETYPE", true);
			condition.put("ID", true);
			List<Map<String, Object>> rangeDatas = MapDistinct.getMap(getQDataDetail, condition);

			String ISHTTPS= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
			String httpStr=ISHTTPS.equals("1")?"https://":"http://";

			String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
			for (Map<String, Object> map : catDatas) 
			{
				DCP_ClassDetailRes.level1Elm oneLv1 = res.new level1Elm();
				
				String classType = map.get("CLASSTYPE").toString();
				String classNo = map.get("CLASSNO").toString();
				String upClassNo = map.get("UPCLASSNO").toString();
				String levelId = map.get("LEVELID").toString();
				String sortId = map.get("SORTID").toString();
				String memo = map.get("MEMO").toString();
				String status = map.get("STATUS").toString();				
				String beginDate = map.get("BEGINDATE").toString();
                String classimage = map.get("CLASSIMAGE").toString();
                String remind = map.get("REMIND").toString();
                String remindtype = map.get("REMINDTYPE").toString();
				String label = map.get("LABEL").toString();
				String labelname = map.get("LABELNAME").toString();
				String isshare = map.get("ISSHARE").toString();
				if(beginDate!=null&&beginDate.length()==8)
				{
					StringBuffer stringBuilder1=new StringBuffer(beginDate);
	        stringBuilder1.insert(6,"-");
	        stringBuilder1.insert(4,"-");
	        beginDate = stringBuilder1.toString();
				}
				String endDate = map.get("ENDDATE").toString();
				
				if(endDate!=null&&endDate.length()==8)
				{
					StringBuffer stringBuilder1=new StringBuffer(endDate);
	        stringBuilder1.insert(6,"-");
	        stringBuilder1.insert(4,"-");
	        endDate = stringBuilder1.toString();
				}
				
				String restrictPeriod = map.get("RESTRICTPERIOD").toString();//适用时段：0-所有时段 1-指定时段 2-排除时段
				String restrictShop = map.get("RESTRICTSHOP").toString();//适用门店：0-所有门店1-指定门店2-排除门店
				String restrictChannel = map.get("RESTRICTCHANNEL").toString();//适用渠道：0-所有渠道1-指定渠道2-排除渠道
				String restrictAppType = map.get("RESTRICTAPPTYPE").toString();//适用应用：0-所有应用1-指定应用
				
				
				if (upClassNo==null) 
				{
					upClassNo ="";				
				}
				if(levelId==null)
				{
					levelId = "";					
				}

				oneLv1.setClassType(classType);
				oneLv1.setClassNo(classNo);
				oneLv1.setUpClassNo(upClassNo);
				oneLv1.setLevelId(levelId);
				oneLv1.setSortId(sortId);
				oneLv1.setMemo(memo);
				oneLv1.setStatus(status);
				oneLv1.setBeginDate(beginDate);
				oneLv1.setEndDate(endDate);
				oneLv1.setGoodsSortType(map.get("GOODSSORTTYPE").toString());
				oneLv1.setSubClassCount(map.get("SUBCLASSCOUNT").toString());
				oneLv1.setRestrictAppType(restrictAppType);
				oneLv1.setRestrictChannel(restrictChannel);
				oneLv1.setRestrictShop(restrictShop);
				oneLv1.setRestrictPeriod(restrictPeriod);
				oneLv1.setLabel(label);
				oneLv1.setLabelName(labelname);

				oneLv1.setIsShare(Check.Null(isshare)?"0":isshare);

                if(!Check.Null(classimage)){
                    if (DomainName.endsWith("/"))
                    {
                        oneLv1.setClassImageUrl(httpStr+DomainName+"resource/image/" +classimage);
                    }
                    else
                    {
                        oneLv1.setClassImageUrl(httpStr+DomainName+"/resource/image/" +classimage);
                    }
                }else {
                    oneLv1.setClassImageUrl("");
                }


                oneLv1.setClassImage(classimage);
                oneLv1.setRemind(remind);
                oneLv1.setRemindType("");
				//  是否开启点单提醒Y/N，提醒类型，0.必须 1.提醒  仅限classType=POS
				if(classType.equals("POS")){
					oneLv1.setRemind(remind);
					oneLv1.setRemindType(remindtype);
				}
				
				oneLv1.setCreateopid(map.get("CREATEOPID").toString());
				oneLv1.setCreateopname(map.get("CREATEOPNAME").toString());
				oneLv1.setCreatetime(map.get("CREATETIME").toString());
				oneLv1.setLastmodiopid(map.get("LASTMODIOPID").toString());
				oneLv1.setLastmodiname(map.get("LASTMODIOPNAME").toString());
				oneLv1.setLastmoditime(map.get("LASTMODITIME").toString());
				oneLv1.setUpClassName(map.get("UPCLASSNAME").toString());
			
				oneLv1.setRangeList(new ArrayList<DCP_ClassDetailRes.range>());
				oneLv1.setClassName_lang(new ArrayList<DCP_ClassDetailRes.className>());
				oneLv1.setDisplayName_lang(new ArrayList<DCP_ClassDetailRes.displayName>());		
				
				for (Map<String, Object> item : langDatas) 
				{
					String classType_detail = item.get("CLASSTYPE").toString();
					String classNo_detail = item.get("CLASSNO").toString();
					String langType =  item.get("LANG_TYPE").toString();
					if(langType==null||langType.isEmpty()||classType_detail==null||classNo_detail==null)
					{
						continue;
					}
					
					if(classType.equals(classType_detail)&&classNo.equals(classNo_detail))
					{
						String className = item.get("CLASSNAME").toString();
						String displayName = item.get("DISPLAYNAME").toString();
						DCP_ClassDetailRes.className class_lang = res.new className();
												
						class_lang.setLangType(langType);
						class_lang.setName(className);
						if(curLangType.equals(langType))
						{
							oneLv1.setClassName(className);						
						}
						oneLv1.getClassName_lang().add(class_lang);
						
						DCP_ClassDetailRes.displayName display_lang = res.new displayName();
						display_lang.setLangType(langType);
						display_lang.setName(displayName);
						if(curLangType.equals(langType))
						{
							oneLv1.setDisplayName(displayName);						
						}
						oneLv1.getDisplayName_lang().add(display_lang);												
					}
										
				}
				for (Map<String, Object> item : rangeDatas) 					
				{
					String classType_detail = item.get("CLASSTYPE").toString();
					String classNo_detail = item.get("CLASSNO").toString();
					String rangeType =  item.get("RANGETYPE").toString();
					if(rangeType==null||rangeType.isEmpty()||classType_detail==null||classNo_detail==null)
					{
						continue;
					}
					if(classType.equals(classType_detail)&&classNo.equals(classNo_detail))
					{
						DCP_ClassDetailRes.range class_range = res.new range();
						class_range.setRangeType(rangeType);
						class_range.setId(item.get("ID").toString());
						class_range.setName(item.get("NAME").toString());
						
						oneLv1.getRangeList().add(class_range);
					}
												
				}
				
				
				
				res.getDatas().add(oneLv1);																		
			}
			
			
		}
			
	
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_ClassDetailReq req) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		/*String status = null;
		String keyTxt = null;
		String shopId = null;//适用门店编号
		String channelId = null;//适用渠道编号
		String appType = null;//适用应用类型
		String levelId = null;
		if(req.getRequest()!=null)
		{
			
			status = req.getRequest().getStatus();
			keyTxt = req.getRequest().getKeyTxt();
			shopId = req.getRequest().getShopId();//适用门店编号
			channelId = req.getRequest().getChannelId();//适用渠道编号
			appType = req.getRequest().getAppType();//适用应用类型
			levelId = req.getRequest().getLevelId();
		}*/
		String classType = req.getRequest().getClassType();
		String classNo = req.getRequest().getClassNo();
		
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
			
	String sql = "";
	StringBuffer sqlBuff = new StringBuffer();
	sqlBuff.append(" SELECT * from ( ");
	sqlBuff.append(" select A.*, "		
		+ " AL.LANG_TYPE,AL.CLASSNAME,AL.DISPLAYNAME,UPL.CLASSNAME AS UPCLASSNAME,B.RANGETYPE,B.ID,B.NAME,c.CLASSIMAGE "
		+ "  from dcp_class A ");
	sqlBuff.append(" left join dcp_class_lang AL on  A.EID=AL.EID AND A.CLASSTYPE=AL.CLASSTYPE AND A.CLASSNO=AL.CLASSNO  ");
	sqlBuff.append(" left join dcp_class_lang UPL on  A.EID=UPL.EID AND A.CLASSTYPE=UPL.CLASSTYPE AND A.UPCLASSNO=UPL.CLASSNO and UPL.LANG_TYPE='"+langType+"' ");
	sqlBuff.append(" left join DCP_CLASS_RANGE B on  A.EID=B.EID AND A.CLASSTYPE=B.CLASSTYPE AND A.CLASSNO=B.CLASSNO ");
	sqlBuff.append(" LEFT JOIN DCP_CLASS_IMAGE c ON a.EID = c.EID AND a.CLASSNO = c.CLASSNO ");
	sqlBuff.append(" where A.eid='"+eId+"' ");
	sqlBuff.append(" and A.classtype='"+classType+"' ");
	sqlBuff.append(" and A.classno='"+classNo+"' ");
	
	/*if(status!=null&&status.isEmpty()==false)
	{
		sqlBuff.append(" and A.status='"+status+"' ");
	}
	if(levelId!=null&&levelId.isEmpty()==false)
	{
		sqlBuff.append(" and A.levelId='"+levelId+"' ");
	}
	if(keyTxt!=null&&keyTxt.isEmpty()==false)
	{
		sqlBuff.append(" and (A.CLASSNO like '%%"+keyTxt+"%%' or AL.CLASSNAME like '%%"+keyTxt+"%%' or AL.DISPLAYNAME like '%%"+keyTxt+"%%' )");
	}*/
	
	sqlBuff.append(" order by A.SORTID");
	sqlBuff.append(" )");
	
	sql = sqlBuff.toString();

	return sql;
	
	
	
	}
	
	
	
	

}
