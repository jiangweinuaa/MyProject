package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ClassGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_ClassGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class DCP_ClassGoodsQuery extends SPosBasicService<DCP_ClassGoodsQueryReq, DCP_ClassGoodsQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ClassGoodsQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
	  StringBuffer errMsg = new StringBuffer("");
	
	  if(req.getRequest()==null)
	  {
	  	errMsg.append("request不能为空值 ");
	  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
	  if(req.getRequest().getClassList()==null)
	  {
	  	errMsg.append("菜单不能为空值 ");
	  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
	      
	  if(Check.Null(req.getRequest().getClassList().getClassType())){
	   	errMsg.append("菜单类型不能为空值， ");
	   	isFail = true;
	
	  }
	  /*if(Check.Null(req.getRequest().getClassList().getClassNo())){
	   	errMsg.append("分类编码不能为空值， ");
	   	isFail = true;
	
	  }*/
	 
	  if (isFail)
	  {
	 	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
	  
	  return isFail;
	
	}

	@Override
	protected TypeToken<DCP_ClassGoodsQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ClassGoodsQueryReq>(){};
	}

	@Override
	protected DCP_ClassGoodsQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ClassGoodsQueryRes();
	}

	@Override
	protected DCP_ClassGoodsQueryRes processJson(DCP_ClassGoodsQueryReq req) throws Exception 
	{		
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		String sql = null;
	  DCP_ClassGoodsQueryRes res = this.getResponse();
	  int totalRecords;								//总笔数
	  int totalPages;									//总页数
		
		sql = this.getQuerySql(req);	
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_ClassGoodsQueryRes.level1Elm>());
		
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			String num = getQDataDetail.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			//
			String ISHTTPS=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
			String httpStr=ISHTTPS.equals("1")?"https://":"http://";
			
			String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("CLASSNO", true);	
			condition.put("PLUNO", true);			
			//调用过滤函数
			List<Map<String, Object>> headerDatas=MapDistinct.getMap(getQDataDetail, condition);

            Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
            condition2.put("CLASSNO", true);
            condition2.put("pluno", true);
            condition2.put("EID", true);
            condition2.put("CLASSTYPE", true);
            condition2.put("SUNIT", true);
            //调用过滤函数
            List<Map<String, Object>> langDetas=MapDistinct.getMap(getQDataDetail, condition2);
			
			for (Map<String, Object> oneData : headerDatas) 
			{
				DCP_ClassGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String pluNo = oneData.get("PLUNO").toString();
				String classNo = oneData.get("CLASSNO").toString();
				
				oneLv1.setClassNo(classNo);
				oneLv1.setClassType(oneData.get("CLASSTYPE").toString());
				oneLv1.setPluNo(pluNo);
				oneLv1.setPluType(oneData.get("PLUTYPE").toString());
				oneLv1.setSortId(oneData.get("SORTID").toString());
				oneLv1.setClassName(oneData.get("CLASSNAME").toString());
				oneLv1.setDisplayName(oneData.get("DISPLAYNAME").toString());
				oneLv1.setPluName(oneData.get("PLU_NAME").toString());
				oneLv1.setsUnit(oneData.get("SUNIT").toString());
				oneLv1.setPrice(oneData.get("PRICE").toString());
				oneLv1.setDescription(oneData.get("DESCRIPTION").toString());
				String remind = oneData.get("REMIND").toString();
				if(Check.Null(remind)){
					remind = "N";
				}
				oneLv1.setRemind(remind);
				oneLv1.setRemindType(oneData.get("REMINDTYPE").toString());
				//		
				String imageName = oneData.get("LISTIMAGE").toString();
				oneLv1.setPicUrl(imageName);
				if(imageName!=null&&imageName.trim().length()>0)
				{
					if (DomainName.endsWith("/")) 
					{
						oneLv1.setPicUrl(httpStr+DomainName+"resource/image/" +imageName);		
					}
					else 
					{
						oneLv1.setPicUrl(httpStr+DomainName+"/resource/image/"+imageName);		
					}
				}
				
				oneLv1.setDisplayName_lang(new ArrayList<DCP_ClassGoodsQueryRes.displayName>());
				for (Map<String, Object> oneData2 : langDetas)
				{
					Object langType = oneData2.get("LANG_TYPE");
					if(langType==null||langType.toString().isEmpty())
					{
						continue;
					}
					String pluNo_lang = oneData2.get("PLUNO").toString();
					String classNo_lang = oneData2.get("CLASSNO").toString();
					String name = oneData2.get("DISPLAYNAME").toString();
					
					if(pluNo.equals(pluNo_lang)&&classNo.equals(classNo_lang))
					{
						DCP_ClassGoodsQueryRes.displayName oneLv2 = res.new displayName();
						oneLv2.setLangType(langType.toString());
						oneLv2.setName(name);
						
						if(curLangType.equals(langType.toString()))
						{
							oneLv1.setDisplayName(name);
						}
						
						oneLv1.getDisplayName_lang().add(oneLv2);
						
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
	protected String getQuerySql(DCP_ClassGoodsQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String sql = "";
		StringBuffer sqlBuff = new StringBuffer("");
		
		String eId = req.geteId();
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
    
		String keyTxt = req.getRequest().getKeyTxt();
		String classType = req.getRequest().getClassList().getClassType();
		String classNo = req.getRequest().getClassList().getClassNo();
		String curLangType = req.getLangType();

        String channelId = req.getRequest().getChannelId();

		String appNo = req.getRequest().getAppNo();
		Set<String> channelIdList= null;
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		
		
		sqlBuff.append(" select * from (");
		sqlBuff.append(" select   aa.* from (");
		sqlBuff.append(" select count(DISTINCT a.pluno||a.EID||a.CLASSTYPE||a.CLASSNO||c.SUNIT) OVER() num,DENSE_RANK () OVER(ORDER BY a.SORTID ,a.pluno,NVL(c.price, 0),a.EID,a.CLASSTYPE,a.CLASSNO,c.SUNIT) rn,a.*,");
		sqlBuff.append(" B.LANG_TYPE,B.DISPLAYNAME,C.SUNIT,NVL(C.PRICE,0) PRICE,F.DESCRIPTION, "
				+ "	CASE WHEN A.PLUTYPE = 'MULTISPEC' THEN CL.MASTERPLUNAME ELSE GL.plu_Name END  AS  plu_name ,"
				+ " D.CLASSNAME,E.LISTIMAGE ");
		sqlBuff.append(" from (");
		sqlBuff.append(" SELECT A.*,B.UPCLASSNO,B.RESTRICTCHANNEL from DCP_CLASS_GOODS  A");
		sqlBuff.append(" inner join (");
		sqlBuff.append(" select EID,classtype, classno,upclassno,RESTRICTCHANNEL from DCP_CLASS  where eid='"+eId+"' and classtype='"+classType+"'");
		if(classNo!=null&&classNo.isEmpty()==false)
		{
			sqlBuff.append(" AND (classno='"+classNo+"'  or (Upclassno='"+classNo+"' and upclassno is not null) ) ");
		}
		
		sqlBuff.append(" ) B  on A.EID=B.EID and A.CLASSTYPE=B.CLASSTYPE AND A.CLASSNO=B.CLASSNO ");
		sqlBuff.append(" where A.eid='"+eId+"' and A.classtype='"+classType+"' ");
		/*if(classNo!=null&&classNo.isEmpty()==false) //不需要了 否则2级菜单不显示
		{
			sqlBuff.append(" and A.classno='"+classNo+"' ");
		}*/
		
		sqlBuff.append(" ) a");
		//sqlBuff.append(" left join  dcp_class_goods_lang B  on  A.EID=B.EID and A.CLASSTYPE=B.CLASSTYPE AND A.CLASSNO=B.CLASSNO and A.pluno=B.PLUNO AND B.LANG_TYPE='"+curLangType+"' ");
		sqlBuff.append(" left join  dcp_class_goods_lang B  on  A.EID=B.EID and A.CLASSTYPE=B.CLASSTYPE AND A.CLASSNO=B.CLASSNO and A.pluno=B.PLUNO  AND B.lang_Type = '"+curLangType+"' ");
		sqlBuff.append(" LEFT JOIN DCP_GOODS c ON  A.EID=c.EID and A.pluno=c.PLUNO");
//		sqlBuff.append(" left join DCP_GOODS_LANG CL ON   A.EID=CL.EID and A.pluno=CL.PLUNO AND CL.LANG_TYPE='"+curLangType+"' ");
		sqlBuff.append(" LEFT JOIN DCP_GOODS_lang GL ON A.eID = GL.eId AND A.PLUNO = GL.pluNO AND GL.lang_Type = '"+curLangType+"'");
		sqlBuff.append(" LEFT join DCP_MSPECGOODS_LANG CL on A.EID = CL.EID and A.PLUNO = CL.MASTERPLUNO  AND CL.LANG_TYPE='"+curLangType+"' ");
		
		sqlBuff.append(" LEFT join DCP_CLASS_LANG D ON  A.EID=D.EID and A.CLASSTYPE=D.CLASSTYPE AND A.CLASSNO=D.CLASSNO AND D.LANG_TYPE='"+curLangType+"' ");
		sqlBuff.append(" LEFT JOIN (select a.* from DCP_GOODSIMAGE a where APPTYPE = (select max(APPTYPE) from DCP_GOODSIMAGE where pluno = a.pluno and eid='"+eId+"' AND (APPTYPE = 'ALL' OR APPTYPE='"+classType+"')) order by a.pluno ) E ON A.pluno = E.PLUNO " +
                "  LEFT JOIN DCP_GOODS_EXT F on a.EID = F.EID AND a.PLUNO = F.PLUNO  " +
                " LEFT JOIN DCP_CLASS_RANGE g ON a.EID  = g.EID  AND a.CLASSTYPE = g.CLASSTYPE AND a.CLASSNO = g.CLASSNO " +
                " WHERE 1=1 ");


		if(keyTxt != null && keyTxt.isEmpty()==false)
		{
			sqlBuff.append("  AND ( A.pluNo = '"+keyTxt+"' OR CASE "
                + " WHEN A.PLUTYPE = 'MULTISPEC' THEN CL.MASTERPLUNAME "
                + " ELSE  GL.PLU_NAME   END LIKE '%%"+keyTxt+"%%'  ) ");
		}
		if(!Check.Null(appNo)){
			if(CollectionUtils.isEmpty(channelIdList)){
				channelIdList = new HashSet<>();
			}

			// 获得 APPNO 关联的 渠道编号 channelid
			StringBuffer sqlbuf2 = new StringBuffer("");
			sqlbuf2.append("select CHANNELID from CRM_CHANNEL where EID = '"+eId+"' and APPNO = '"+appNo+"' and STATUS = '100' ");
			List<Map<String, Object>> getChannelIDs = this.doQueryData(sqlbuf2.toString(), null);
			for (Map<String, Object> getChannelID : getChannelIDs) {
				String channelid = getChannelID.get("CHANNELID").toString();
				channelIdList.add(channelid);
			}
		}

		if(channelId != null && channelId.isEmpty() == false){
			if(CollectionUtils.isEmpty(channelIdList)){
				channelIdList = new HashSet<>();
			}
			channelIdList.add(channelId);
		}

		if(!CollectionUtils.isEmpty(channelIdList)){
			String channelIdSql = PosPub.getArrayStrSQLIn(channelIdList.toArray(new String[channelIdList.size()]));

			sqlBuff.append(" AND (a.RESTRICTCHANNEL = 0 OR " +
					" a.RESTRICTCHANNEL = 1 AND g.ID IN("+channelIdSql+") " +
					" OR a.RESTRICTCHANNEL = 2 AND not exists( " +
					" SELECT * FROM DCP_CLASS_RANGE WHERE a.EID = EID AND a.CLASSNO = CLASSNO AND a.CLASSTYPE = CLASSTYPE AND ID IN("+channelIdSql+")))");
		}

		sqlBuff.append(" order by a.SORTID,a.PLUNO，NVL(c.price, 0))  aa ) where rn>"+startRow+"  and rn <= "+(startRow+pageSize));
		
		sql = sqlBuff.toString();
		
	
		return sql;
	}

}
