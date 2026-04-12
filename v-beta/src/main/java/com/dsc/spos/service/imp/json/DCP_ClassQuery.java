package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ClassQueryReq;
import com.dsc.spos.json.cust.res.DCP_ClassQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_ClassQuery extends SPosBasicService<DCP_ClassQueryReq, DCP_ClassQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ClassQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (req.getRequest() == null) {
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		/*
		 * if(Check.Null(req.getRequest().getClassType())) { isFail = true;
		 * errMsg.append("菜单类型不能为空 ");
		 * 
		 * }
		 */
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ClassQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ClassQueryReq>() {
		};
	}

	@Override
	protected DCP_ClassQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ClassQueryRes();
	}

	@Override
	protected DCP_ClassQueryRes processJson(DCP_ClassQueryReq req) throws Exception {
		DCP_ClassQueryRes res = this.getResponse();
		res.setDatas(new ArrayList<DCP_ClassQueryRes.level1Elm>());
		String sql = this.getQuerySql(req);
		String curLangType = req.getLangType();
        String keyTxt= req.getRequest().getKeyTxt();

        boolean isKeyTxt = false;
        if(!Check.Null(keyTxt)){
            isKeyTxt = true;
        }

        if (curLangType == null || curLangType.isEmpty()) {
			curLangType = "zh_CN";
		}

		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查询条件
			condition.put("CLASSTYPE", true);
			condition.put("CLASSNO", true);
			// 调用过滤函数
			List<Map<String, Object>> catDatas = MapDistinct.getMap(getQDataDetail, condition);

			String ISHTTPS= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
			String httpStr=ISHTTPS.equals("1")?"https://":"http://";

			String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
			for (Map<String, Object> map : catDatas) {
				DCP_ClassQueryRes.level1Elm oneLv1 = res.new level1Elm();

				String classType = map.get("CLASSTYPE").toString();
				String classNo = map.get("CLASSNO").toString();
				String upClassNo = map.get("UPCLASSNO").toString();
				String upClassName = map.get("UPCLASSNAME").toString();
				String levelId = map.get("LEVELID").toString();
				String sortId = map.get("SORTID").toString();
				String memo = map.get("MEMO").toString();
				String status = map.get("STATUS").toString();
				String beginDate = map.get("BEGINDATE").toString();
				String endDate = map.get("ENDDATE").toString();

				if (beginDate != null && beginDate.length() == 8) {
					StringBuffer stringBuilder1 = new StringBuffer(beginDate);
					stringBuilder1.insert(6, "-");
					stringBuilder1.insert(4, "-");
					beginDate = stringBuilder1.toString();
				}

				if (endDate != null && endDate.length() == 8) {
					StringBuffer stringBuilder1 = new StringBuffer(endDate);
					stringBuilder1.insert(6, "-");
					stringBuilder1.insert(4, "-");
					endDate = stringBuilder1.toString();
				}

				if (upClassNo == null) {
					upClassNo = "";
				}
				if (levelId == null) {
					levelId = "";
				}
				if (levelId.isEmpty() || upClassNo.isEmpty() || levelId.equals("2") == false||isKeyTxt) {
					oneLv1.setClassType(classType);
					oneLv1.setClassNo(classNo);
					oneLv1.setUpClassNo(upClassNo);
					oneLv1.setUpClassName(upClassName);
					oneLv1.setLevelId(levelId);
					oneLv1.setSortId(sortId);
					oneLv1.setMemo(memo);
					oneLv1.setStatus(status);
					oneLv1.setBeginDate(beginDate);
					oneLv1.setEndDate(endDate);
					oneLv1.setGoodsSortType(map.get("GOODSSORTTYPE").toString());
					oneLv1.setSubClassCount(map.get("SUBCLASSCOUNT").toString());
					oneLv1.setClassName(map.get("CLASSNAME").toString());
					oneLv1.setDisplayName(map.get("DISPLAYNAME").toString());

					String remind = map.get("REMIND").toString();
					if(Check.Null(remind)){
						remind = "N";
					}
					oneLv1.setRemind(remind);
					oneLv1.setRemindType(map.get("REMINDTYPE").toString());
					String label = map.get("LABEL").toString();
					if(Check.Null(label)){
						label = "N";
					}
					oneLv1.setLabel(label);
					oneLv1.setLabelName(map.get("LABELNAME").toString());


                    String isshare = map.get("ISSHARE").toString();
                    if(Check.Null(isshare)){
                        isshare = "0";
                    }
					oneLv1.setIsShare(isshare);

                    String classimage = map.get("CLASSIMAGE").toString();
                    // 拼接图片地址
					if(!Check.Null(classimage)){
						if (DomainName.endsWith("/"))
						{
							oneLv1.setClassImageUrl(httpStr+DomainName+"resource/image/" +classimage);
						}
						else
						{
							oneLv1.setClassImageUrl(httpStr+DomainName+"/resource/image/" +classimage);
						}
					}else{
						oneLv1.setClassImageUrl("");
					}


                    oneLv1.setSubClassList(new ArrayList<DCP_ClassQueryRes.level1Elm>());
					/*
					 * oneLv1.setClassName_lang(new
					 * ArrayList<DCP_ClassQueryRes.className>());
					 * oneLv1.setDisplayName_lang(new
					 * ArrayList<DCP_ClassQueryRes.displayName>());
					 */

					/*
					 * for (Map<String, Object> item : getQDataDetail) { String
					 * classType_detail = item.get("CLASSTYPE").toString();
					 * String classNo_detail = item.get("CLASSNO").toString();
					 * String langType = item.get("LANG_TYPE").toString();
					 * if(langType==null||langType.isEmpty()||classType_detail==
					 * null||classNo_detail==null) { continue; }
					 * 
					 * if(classType.equals(classType_detail)&&classNo.equals(
					 * classNo_detail)) { String className =
					 * item.get("CLASSNAME").toString(); String displayName =
					 * item.get("DISPLAYNAME").toString();
					 * DCP_ClassQueryRes.className class_lang = res.new
					 * className();
					 * 
					 * class_lang.setLangType(langType);
					 * class_lang.setName(className);
					 * if(curLangType.equals(langType)) {
					 * oneLv1.setClassName(className); }
					 * oneLv1.getClassName_lang().add(class_lang);
					 * 
					 * DCP_ClassQueryRes.displayName display_lang = res.new
					 * displayName(); display_lang.setLangType(langType);
					 * display_lang.setName(displayName);
					 * if(curLangType.equals(langType)) {
					 * oneLv1.setDisplaysName(displayName); }
					 * oneLv1.getDisplayName_lang().add(display_lang); }
					 * 
					 * }
					 */
                    if(!isKeyTxt) {
                        setChildrenDatas(req, oneLv1, catDatas);
                    }
					res.getDatas().add(oneLv1);

				}

			}

		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_ClassQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String status = null; // 0 查询全部 ,1 查询当前有效
		String keyTxt = null;
		String shopId = null;// 适用门店编号
		String channelId = null;// 适用渠道编号
		String appType = null;// 适用应用类型
		String levelId = null;
		String appNo = null; // 渠道类型编码
        Set<String> channelIdList= null;

		// 获取当前时间
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		String thisDate = dfDate.format(cal.getTime());

		if (req.getRequest() != null) {
			status = req.getRequest().getStatus();
			keyTxt = req.getRequest().getKeyTxt();
			shopId = req.getRequest().getShopId();// 适用门店编号
			channelId = req.getRequest().getChannelId();// 适用渠道编号
			appType = req.getRequest().getAppType();// 适用应用类型
			levelId = req.getRequest().getLevelId();
             appNo = req.getRequest().getAppNo(); // / 渠道类型编码
        }
		String classType = req.getRequest().getClassType();

		String langType = req.getLangType();
		if (langType == null || langType.isEmpty()) {
			langType = "zh_CN";
		}



		String sql = "";
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("SELECT DISTINCT a.CLASSTYPE,a.CLASSNO,b.LANG_TYPE,b.CLASSNAME,b.DISPLAYNAME,a.LEVELID,a.UPCLASSNO,UL.CLASSNAME as UPCLASSNAME,UL.DISPLAYNAME AS UPDISPLAYNAME,a.SORTID,a.MEMO,a.STATUS,"
				+ "a.BEGINDATE,a.ENDDATE,a.SUBCLASSCOUNT,a.SORTID PRIORITY,a.GOODSSORTTYPE,a.REMIND,a.REMINDTYPE,a.LABEL,a.LABELNAME,a.ISSHARE,i.CLASSIMAGE FROM DCP_CLASS a "
				+ "LEFT JOIN DCP_CLASS_LANG b ON a.EID = b.EID AND a.CLASSNO = b.CLASSNO AND a.CLASSTYPE = b.CLASSTYPE AND b.LANG_TYPE = '"+langType+"' "
				+ "left join DCP_CLASS_LANG UL on A.EID = UL.EID AND A.CLASSTYPE = UL.CLASSTYPE AND A.UPCLASSNO = UL.CLASSNO and UL.LANG_TYPE = '"+langType+"' "
				+ "LEFT JOIN DCP_CLASS_RANGE e ON a.EID = e.EID AND a.CLASSNO = e.CLASSNO AND a.CLASSTYPE = e.CLASSTYPE AND e.RANGETYPE = 2 "
				+ "LEFT JOIN DCP_CLASS_RANGE f ON a.EID = f.EID AND a.CLASSNO = f.CLASSNO AND a.CLASSTYPE = f.CLASSTYPE AND f.RANGETYPE = 1 "
				+ "LEFT JOIN DCP_CLASS_RANGE h ON a.EID = h.EID AND a.CLASSNO = h.CLASSNO AND a.CLASSTYPE = h.CLASSTYPE AND h.RANGETYPE = 3 "
				+ "LEFT JOIN DCP_CLASS_IMAGE i ON i.EID = a.EID AND a.CLASSNO = i.CLASSNO " +
                " ");

		sqlBuff.append(" where a.eid='" + eId + "' ");

		if (classType != null && classType.isEmpty() == false) 
		{
			sqlBuff.append(" and a.classtype='" + classType + "' ");
		}
		
		if (shopId != null && shopId.isEmpty() == false) 
		{
			sqlBuff.append(" AND (a.RESTRICTSHOP = 0 OR (a.RESTRICTSHOP = 1 AND e.ID = '"+shopId+"') OR a.RESTRICTSHOP = 2 AND not exists(SELECT *  FROM DCP_CLASS_RANGE WHERE a.EID = EID AND a.CLASSNO = CLASSNO  AND a.CLASSTYPE = CLASSTYPE AND ID = '"+shopId+"')) "
					+ " AND f.ID = (SELECT BELFIRM FROM DCP_ORG WHERE ORGANIZATIONNO = '"+shopId+"' AND EID = '"+eId+"') ");
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
                    " a.RESTRICTCHANNEL = 1 AND h.ID IN("+channelIdSql+") " +
					" OR a.RESTRICTCHANNEL = 2 AND not exists( " +
					" SELECT * FROM DCP_CLASS_RANGE WHERE a.EID = EID AND a.CLASSNO = CLASSNO AND a.CLASSTYPE = CLASSTYPE AND ID IN("+channelIdSql+")))");
        }
		
//		if (channelId != null && channelId.isEmpty() == false)
//		{
//			sqlBuff.append(" AND (a.RESTRICTCHANNEL = 0 OR (a.RESTRICTCHANNEL = 1 AND h.ID = '"+channelId+"') ");
//			sqlBuff.append(" OR a.RESTRICTCHANNEL = 2 AND not exists(SELECT * FROM DCP_CLASS_RANGE WHERE a.EID = EID AND a.CLASSNO = CLASSNO AND a.CLASSTYPE = CLASSTYPE AND ID = '\"+channelId+\"')) ") ;
//		}

		// 0-全部状态,1-生效中【在生效日期内】 2-未生效【未到生效日期】3-已失效【截止日期已过】 
		if (!Check.Null(status) && status.equals("1")) 
		{
			sqlBuff.append(" and '"+thisDate+"' between a.BEGINDATE and a.ENDDATE ");
		}

		else if (!Check.Null(status) && status.equals("2")) 
		{
			sqlBuff.append(" and '"+thisDate+"' < a.BEGINDATE  ");
		}

		else if (!Check.Null(status) && status.equals("3")) 
		{
			sqlBuff.append(" and '"+thisDate+"' > a.ENDDATE  ");
		}

		if (levelId != null && levelId.isEmpty() == false) 
		{
			sqlBuff.append(" and a.levelId='" + levelId + "' ");
		}

		if (keyTxt != null && keyTxt.isEmpty() == false) 
		{
			sqlBuff.append(" and (a.CLASSNO like '%%" + keyTxt + "%%' or b.CLASSNAME like '%%" + keyTxt
					+ "%%' or b.DISPLAYNAME like '%%" + keyTxt + "%%' )");
		}


		sqlBuff.append(" ORDER BY a.SORTID, a.CLASSNO ");



		sql = sqlBuff.toString();

		return sql;

	}

	protected void setChildrenDatas(DCP_ClassQueryReq req, DCP_ClassQueryRes.level1Elm oneLv2,
			List<Map<String, Object>> allDatas) throws Exception {
		String curLangType = req.getLangType();
		if (curLangType == null || curLangType.isEmpty()) {
			curLangType = "zh_CN";
		}

		try {
			List<Map<String, Object>> nextDatas = getChildDatas(allDatas, oneLv2.getClassType(), oneLv2.getClassNo());
			if (nextDatas != null && !nextDatas.isEmpty()) {
				String ISHTTPS= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
				String httpStr=ISHTTPS.equals("1")?"https://":"http://";

				String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
				for (Map<String, Object> datas : nextDatas) {
					DCP_ClassQueryRes.level1Elm oneLv1 = new DCP_ClassQueryRes().new level1Elm();

					String classType = datas.get("CLASSTYPE").toString();
					String classNo = datas.get("CLASSNO").toString();
					String upClassNo = datas.get("UPCLASSNO").toString();
					String upClassName = datas.get("UPCLASSNAME").toString();
					String levelId = datas.get("LEVELID").toString();
					String sortId = datas.get("SORTID").toString();
					String memo = datas.get("MEMO").toString();
					String status = datas.get("STATUS").toString();
					String beginDate = datas.get("BEGINDATE").toString();
					if (beginDate != null && beginDate.length() == 8) {
						StringBuffer stringBuilder1 = new StringBuffer(beginDate);
						stringBuilder1.insert(6, "-");
						stringBuilder1.insert(4, "-");
						beginDate = stringBuilder1.toString();
					}
					String endDate = datas.get("ENDDATE").toString();

					if (endDate != null && endDate.length() == 8) {
						StringBuffer stringBuilder1 = new StringBuffer(endDate);
						stringBuilder1.insert(6, "-");
						stringBuilder1.insert(4, "-");
						endDate = stringBuilder1.toString();
					}
					if (upClassNo == null) {
						upClassNo = "";
					}
					if (levelId == null) {
						levelId = "";
					}

					oneLv1.setClassType(classType);
					oneLv1.setClassNo(classNo);
					oneLv1.setUpClassNo(upClassNo);
					oneLv1.setUpClassName(upClassName);
					oneLv1.setLevelId(levelId);
					oneLv1.setSortId(sortId);
					oneLv1.setMemo(memo);
					oneLv1.setStatus(status);
					oneLv1.setBeginDate(beginDate);
					oneLv1.setEndDate(endDate);
					oneLv1.setGoodsSortType(datas.get("GOODSSORTTYPE").toString());
					oneLv1.setSubClassCount(datas.get("SUBCLASSCOUNT").toString());
					oneLv1.setClassName(datas.get("CLASSNAME").toString());
					oneLv1.setDisplayName(datas.get("DISPLAYNAME").toString());
					String remind = datas.get("REMIND").toString();
					if(Check.Null(remind)){
						remind = "N";
					}
					oneLv1.setRemind(remind);
					oneLv1.setRemindType(datas.get("REMINDTYPE").toString());
					String label = datas.get("LABEL").toString();
					if(Check.Null(label)){
						label = "N";
					}
					oneLv1.setLabel(label);
					oneLv1.setLabelName(datas.get("LABELNAME").toString());

                    String isshare = datas.get("ISSHARE").toString();
                    if(Check.Null(isshare)){
                        isshare = "0";
                    }
                    oneLv1.setIsShare(isshare);

					String classimage = datas.get("CLASSIMAGE").toString();
					// 拼接图片地址
					if(!Check.Null(classimage)){
						if (DomainName.endsWith("/"))
						{
							oneLv1.setClassImageUrl(httpStr+DomainName+"resource/image/" +classimage);
						}
						else
						{
							oneLv1.setClassImageUrl(httpStr+DomainName+"/resource/image/" +classimage);
						}
					}else{
						oneLv1.setClassImageUrl("");
					}


					oneLv1.setSubClassList(new ArrayList<DCP_ClassQueryRes.level1Elm>());
					/*
					 * oneLv1.setClassName_lang(new
					 * ArrayList<DCP_ClassQueryRes.className>());
					 * oneLv1.setDisplayName_lang(new
					 * ArrayList<DCP_ClassQueryRes.displayName>());
					 * 
					 * for (Map<String, Object> item : allDatas) { String
					 * classType_detail = item.get("CLASSTYPE").toString();
					 * String classNo_detail = item.get("CLASSNO").toString();
					 * String langType = item.get("LANG_TYPE").toString();
					 * if(langType==null||langType.isEmpty()||classType_detail==
					 * null||classNo_detail==null) { continue; }
					 * 
					 * if(classType.equals(classType_detail)&&classNo.equals(
					 * classNo_detail)) { String className =
					 * item.get("CLASSNAME").toString(); String displayName =
					 * item.get("DISPLAYNAME").toString();
					 * DCP_ClassQueryRes.className class_lang = new
					 * DCP_ClassQueryRes().new className();
					 * 
					 * class_lang.setLangType(langType);
					 * class_lang.setName(className);
					 * if(curLangType.equals(langType)) {
					 * oneLv1.setClassName(className); }
					 * oneLv1.getClassName_lang().add(class_lang);
					 * 
					 * DCP_ClassQueryRes.displayName display_lang = new
					 * DCP_ClassQueryRes().new displayName();
					 * display_lang.setLangType(langType);
					 * display_lang.setName(displayName);
					 * if(curLangType.equals(langType)) {
					 * oneLv1.setDisplaysName(displayName); }
					 * oneLv1.getDisplayName_lang().add(display_lang); }
					 * 
					 * }
					 */

					setChildrenDatas(req, oneLv1, allDatas);
					oneLv2.getSubClassList().add(oneLv1);

					oneLv1 = null;

				}
			}

		} catch (Exception e) {

		}

	}

	/**
	 * 获取下一层级信息
	 * 
	 * @param allDatas
	 * @param
	 * @return
	 */
	protected List<Map<String, Object>> getChildDatas(List<Map<String, Object>> allDatas, String classType,
			String classNo) {
		List<Map<String, Object>> datas = new ArrayList<>();
		for (Map<String, Object> map : allDatas) {
			if (map.get("CLASSTYPE").toString().equals(classType) && map.get("UPCLASSNO").toString().equals(classNo)) {
				datas.add(map);
			}
		}
		return datas;
	}

}
