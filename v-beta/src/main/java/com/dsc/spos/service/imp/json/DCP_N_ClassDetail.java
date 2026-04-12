package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_N_ClassDetailReq;
import com.dsc.spos.json.cust.res.DCP_N_ClassDetailRes;
import com.dsc.spos.json.cust.res.DCP_N_ClassDetailRes.*;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_N_ClassDetail
 * 服务说明：N-销售分组详情
 * @author jinzma
 * @since  2024-04-18
 */
public class DCP_N_ClassDetail extends SPosBasicService<DCP_N_ClassDetailReq, DCP_N_ClassDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_N_ClassDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }else {
            if (Check.Null(req.getRequest().getClassNo())) {
                isFail = true;
                errMsg.append("classNo不能为空 ");
            }
            if (Check.Null(req.getRequest().getClassType())) {
                isFail = true;
                errMsg.append("classType不能为空 ");
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_N_ClassDetailReq> getRequestType() {
        return new TypeToken<DCP_N_ClassDetailReq>(){};
    }

    @Override
    protected DCP_N_ClassDetailRes getResponseType() {
        return new DCP_N_ClassDetailRes();
    }

    @Override
    protected DCP_N_ClassDetailRes processJson(DCP_N_ClassDetailReq req) throws Exception {

        DCP_N_ClassDetailRes res = this.getResponse();
        res.setDatas(new ArrayList<Datas>());

        try {
            String sql = this.getQuerySql(req);
            String curLangType = req.getLangType();
            if (curLangType == null || curLangType.isEmpty()) {
                curLangType = "zh_CN";
            }

            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
            if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
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

                String ISHTTPS = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";

                String DomainName = PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                for (Map<String, Object> map : catDatas) {
                    DCP_N_ClassDetailRes.Datas oneLv1 = res.new Datas();

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

                    //特殊处理，前端传入空的，后端数据库存-1，此处还要返回给前端空，这个会员合并代码复制原来的，又加上新的内容，表字段亦有废除亦有添加全混在一起，最后肯定是一锅粥
                    String remindType = "";
                    if (!"-1".equals(map.get("REMINDTYPE").toString())){
                        remindType = map.get("REMINDTYPE").toString();
                    }


                    String label = map.get("LABEL").toString();
                    String labelname = map.get("LABELNAME").toString();
                    String isshare = map.get("ISSHARE").toString();
                    if (beginDate != null && beginDate.length() == 8) {
                        StringBuffer stringBuilder1 = new StringBuffer(beginDate);
                        stringBuilder1.insert(6, "-");
                        stringBuilder1.insert(4, "-");
                        beginDate = stringBuilder1.toString();
                    }
                    String endDate = map.get("ENDDATE").toString();

                    if (endDate != null && endDate.length() == 8) {
                        StringBuffer stringBuilder1 = new StringBuffer(endDate);
                        stringBuilder1.insert(6, "-");
                        stringBuilder1.insert(4, "-");
                        endDate = stringBuilder1.toString();
                    }

                    String restrictPeriod = map.get("RESTRICTPERIOD").toString();//适用时段：0-所有时段 1-指定时段 2-排除时段
                    String restrictShop = map.get("RESTRICTSHOP").toString();//适用门店：0-所有门店1-指定门店2-排除门店
                    String restrictChannel = map.get("RESTRICTCHANNEL").toString();//适用渠道：0-所有渠道1-指定渠道2-排除渠道
                    String restrictAppType = map.get("RESTRICTAPPTYPE").toString();//适用应用：0-所有应用1-指定应用


                    if (upClassNo == null) {
                        upClassNo = "";
                    }
                    if (levelId == null) {
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

                    oneLv1.setIsShare(Check.Null(isshare) ? "0" : isshare);

                    if (!Check.Null(classimage)) {
                        if (DomainName.endsWith("/")) {
                            oneLv1.setClassImageUrl(httpStr + DomainName + "resource/image/" + classimage);
                        } else {
                            oneLv1.setClassImageUrl(httpStr + DomainName + "/resource/image/" + classimage);
                        }
                    } else {
                        oneLv1.setClassImageUrl("");
                    }


                    oneLv1.setClassImage(classimage);
                    oneLv1.setRemind(remind);


                    //这个在原本服务里面是有这个判断的，现在新的服务里面，搞不清楚了，拿掉这个"POS"判断，全部返回 by jinzma 20240422
                    /*oneLv1.setRemindType("");
                    //  是否开启点单提醒Y/N，提醒类型，0.必须 1.提醒  仅限classType=POS
                    if (classType.equals("POS")) {
                        oneLv1.setRemind(remind);
                        oneLv1.setRemindType(remindType);
                    }*/
                    oneLv1.setRemindType(remindType);


                    oneLv1.setCreateopid(map.get("CREATEOPID").toString());
                    oneLv1.setCreateopname(map.get("CREATEOPNAME").toString());
                    oneLv1.setCreatetime(map.get("CREATETIME").toString());
                    oneLv1.setLastmodiopid(map.get("LASTMODIOPID").toString());
                    oneLv1.setLastmodiname(map.get("LASTMODIOPNAME").toString());
                    oneLv1.setLastmoditime(map.get("LASTMODITIME").toString());
                    oneLv1.setUpClassName(map.get("UPCLASSNAME").toString());
                    oneLv1.setIsPublic(map.get("ISPUBLIC").toString());


                    oneLv1.setRangeList(new ArrayList<range>());
                    oneLv1.setClassName_lang(new ArrayList<className>());
                    oneLv1.setDisplayName_lang(new ArrayList<displayName>());



                    for (Map<String, Object> item : langDatas) {
                        String classType_detail = item.get("CLASSTYPE").toString();
                        String classNo_detail = item.get("CLASSNO").toString();
                        String langType = item.get("LANG_TYPE").toString();
                        if (langType == null || langType.isEmpty() || classType_detail == null || classNo_detail == null) {
                            continue;
                        }

                        if (classType.equals(classType_detail) && classNo.equals(classNo_detail)) {
                            String className = item.get("CLASSNAME").toString();
                            String displayName = item.get("DISPLAYNAME").toString();
                            className class_lang = res.new className();

                            class_lang.setLangType(langType);
                            class_lang.setName(className);
                            if (curLangType.equals(langType)) {
                                oneLv1.setClassName(className);
                            }
                            oneLv1.getClassName_lang().add(class_lang);

                            displayName display_lang = res.new displayName();
                            display_lang.setLangType(langType);
                            display_lang.setName(displayName);
                            if (curLangType.equals(langType)) {
                                oneLv1.setDisplayName(displayName);
                            }
                            oneLv1.getDisplayName_lang().add(display_lang);
                        }

                    }
                    for (Map<String, Object> item : rangeDatas) {
                        String classType_detail = item.get("CLASSTYPE").toString();
                        String classNo_detail = item.get("CLASSNO").toString();
                        String rangeType = item.get("RANGETYPE").toString();
                        if (rangeType == null || rangeType.isEmpty() || classType_detail == null || classNo_detail == null) {
                            continue;
                        }

                        if (classType.equals(classType_detail) && classNo.equals(classNo_detail)) {
                            range class_range = res.new range();
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

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }


    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_N_ClassDetailReq req) throws Exception {

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
        if(langType==null||langType.isEmpty()) {
            langType = "zh_CN";
        }

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


        return sqlBuff.toString();


    }
}
