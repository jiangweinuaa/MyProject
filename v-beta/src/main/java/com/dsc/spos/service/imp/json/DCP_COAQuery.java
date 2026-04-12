package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_COAQueryReq;
import com.dsc.spos.json.cust.res.DCP_COAQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_COAQuery extends SPosBasicService<DCP_COAQueryReq, DCP_COAQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_COAQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_COAQueryReq> getRequestType() {
        return new TypeToken<DCP_COAQueryReq>(){};
    }

    @Override
    protected DCP_COAQueryRes getResponseType() {
        return new DCP_COAQueryRes();
    }

    @Override
    protected DCP_COAQueryRes processJson(DCP_COAQueryReq req) throws Exception {
        DCP_COAQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
        //分两段sql 查
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        String sqld=this.getQueryDetailSql(req);
        List<Map<String, Object>> getQDataDetail=this.doQueryData(sqld, null);


        res.setDatas(new ArrayList<>());
        //and a.accountid='ALLCOA'

        if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
            //List<Map<String, Object>> getQData = getQDataDetail.stream().filter(z -> z.get("ACCOUNTID").toString().equals("ALLCOA")).collect(Collectors.toList());

            List<DCP_COAQueryRes.SubjectInfo> subjectList = getQData.stream().map(x -> {
                DCP_COAQueryRes.SubjectInfo subjectInfo = res.new SubjectInfo();
                subjectInfo.setSubjectId(x.get("SUBJECTID").toString());
                subjectInfo.setSubjectName(x.get("SUBJECTNAME").toString());
                subjectInfo.setUpSubjectId(x.get("UPSUBJECTID").toString());
                subjectInfo.setLevelID(x.get("LEVELID").toString());
                subjectInfo.setCoaRefId(x.get("COAREFID").toString());
                subjectInfo.setSubjectCat(x.get("SUBJECTCAT").toString());
                subjectInfo.setFirstSubjectId(x.get("FIRSTSUBJECTID")==null?"":x.get("FIRSTSUBJECTID").toString());
                return subjectInfo;
            }).distinct().collect(Collectors.toList());

            List<String> subjectIdList = subjectList.stream().map(x -> x.getSubjectId()).collect(Collectors.toList());

            //找到父节点 UPSUBJECTID 一级科目的所属科目就是自己  或者不存在父级科目的
            List<DCP_COAQueryRes.SubjectInfo> fatherSubjectList = subjectList.stream().filter(map ->
                    map.getUpSubjectId().equals(map.getSubjectId())||!subjectIdList.contains(map.getUpSubjectId())
            ).collect(Collectors.toList());
            for (DCP_COAQueryRes.SubjectInfo fatherSubject : fatherSubjectList){
                DCP_COAQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setCoaRefID(fatherSubject.getCoaRefId());
                level1Elm.setSubjectId(fatherSubject.getSubjectId());
                level1Elm.setSubjectName(fatherSubject.getSubjectName());
                level1Elm.setSubjectCat(fatherSubject.getSubjectCat());
                level1Elm.setLevelID(fatherSubject.getLevelID());
                level1Elm.setUpSubjectId(fatherSubject.getUpSubjectId());
                level1Elm.setFirstSubjectId(fatherSubject.getFirstSubjectId());

                List<Map<String, Object>> collect = getQData.stream().filter(x -> x.get("COAREFID").toString().equals(fatherSubject.getCoaRefId())
                        && x.get("SUBJECTID").toString().equals(fatherSubject.getSubjectId())
                ).collect(Collectors.toList());//这个是ALLCOA 
                if(collect.size()>0){
                    List<Map<String, Object>> accounts = getQDataDetail.stream().filter(x -> x.get("COAREFID").toString().equals(fatherSubject.getCoaRefId())
                            && x.get("SUBJECTID").toString().equals(fatherSubject.getSubjectId())
                            &&!x.get("ACCOUNTID").toString().equals("ALLCOA")
                    ).collect(Collectors.toList());
                    level1Elm.setAccountList(accounts.stream().map(x -> {
                        DCP_COAQueryRes.AccountList accountList = res.new AccountList();
                        accountList.setAccountId(x.get("ACCOUNTID").toString());
                        accountList.setAccountName(x.get("ACCOUNTNAME").toString());
                        accountList.setStatus(x.get("STATUS").toString());
                        return accountList;
                    }).collect(Collectors.toList()));

                    Map<String, Object> stringObjectMap = collect.get(0);
                    level1Elm.setAuxiliaryType(stringObjectMap.get("AUXILIARYTYPE").toString());
                    level1Elm.setMemo(stringObjectMap.get("MEMO").toString());
                    level1Elm.setSubjectProperty(stringObjectMap.get("SUBJECTPROPERTY").toString());
                    level1Elm.setSubjectType(stringObjectMap.get("SUBJECTTYPE").toString());
                    level1Elm.setDirection(stringObjectMap.get("DIRECTION").toString());
                    level1Elm.setIsDirection(stringObjectMap.get("ISDIRECTION").toString());
                    level1Elm.setExpType(stringObjectMap.get("EXPTYPE").toString());
                    level1Elm.setFinAnalSource(stringObjectMap.get("FINANALSOURCE").toString());
                    level1Elm.setIsCashSubject(stringObjectMap.get("ISCASHSUBJECT").toString());
                    level1Elm.setIsEnableDptMng(stringObjectMap.get("ISENABLEDPTMNG").toString());
                    level1Elm.setIsEnableTradObjMng(stringObjectMap.get("ISENABLETRADOBJMNG").toString());
                    level1Elm.setIsEnableProdCatMng(stringObjectMap.get("ISENABLEPRODCATMNG").toString());
                    level1Elm.setIsEnableManMng(stringObjectMap.get("ISENABLEMANMNG").toString());
                    level1Elm.setIsMultiCurMng(stringObjectMap.get("ISMULTICURMNG").toString());
                    level1Elm.setIsSubsysSubject(stringObjectMap.get("ISSUBSYSSUBJECT").toString());
                    level1Elm.setDrCashChgCode(stringObjectMap.get("DRCASHCHGCODE").toString());
                    level1Elm.setCrCashChgCode(stringObjectMap.get("CRCASHCHGCODE").toString());
                    level1Elm.setIsFreeChars1(stringObjectMap.get("ISFREECHARS1").toString());
                    level1Elm.setFreeChars1TypeId(stringObjectMap.get("FREECHARS1_TYPEID").toString());
                    level1Elm.setFreeChars1CtrlMode(stringObjectMap.get("FREECHARS1_CTRLMODE").toString());
                    level1Elm.setIsFreeChars2(stringObjectMap.get("ISFREECHARS2").toString());
                    level1Elm.setFreeChars2TypeId(stringObjectMap.get("FREECHARS2_TYPEID").toString());
                    level1Elm.setFreeChars2CtrlMode(stringObjectMap.get("FREECHARS2_CTRLMODE").toString());
                    level1Elm.setIsFreeChars3(stringObjectMap.get("ISFREECHARS3").toString());
                    level1Elm.setFreeChars3TypeId(stringObjectMap.get("FREECHARS3_TYPEID").toString());
                    level1Elm.setFreeChars3CtrlMode(stringObjectMap.get("FREECHARS3_CTRLMODE").toString());
                    level1Elm.setStatus(stringObjectMap.get("STATUS").toString());

                    level1Elm.setChildren(new ArrayList<>());
                    fetchChildren(res,subjectList,getQData,getQDataDetail,level1Elm);

                }
                res.getDatas().add(level1Elm);

            }


        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_COAQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        String coaRefID = req.getRequest().getCoaRefID();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        String anotherCondition=" and 1=1 ";
        if(Check.NotNull(coaRefID)){
            anotherCondition=" and a.coaRefID='"+coaRefID+"' ";
        }
        if(Check.NotNull(status)){
            anotherCondition=" and a.status='"+status+"' ";
        }

        if(Check.NotNull(keyTxt)){
            anotherCondition=" and (a.subjectid like '%%"+keyTxt+"%%' or a.subjectname like '%%"+keyTxt+"%%' )";
        }

        sqlbuf.append(" "
               // + " select * from ("
                //+ " select count(*) over () num,row_number() over (order by a.coarefid desc) as rn, a.* from ("
                + " select distinct a.SUBJECTID,a.SUBJECTNAME,a.AUXILIARYTYPE,a.COAREFID,a.ACCOUNTID,a.SUBJECTCAT,a.UPSUBJECTID,a.LEVELID,a.SUBJECTPROPERTY,a.SUBJECTTYPE,a.DIRECTION,a.ISDIRECTION," +
                " a.EXPTYPE,a.FINANALSOURCE,a.ISCASHSUBJECT,a.ISENABLEDPTMNG,a.ISENABLETRADOBJMNG,a.ISENABLEPRODCATMNG,a.ISENABLEMANMNG,a.ISMULTICURMNG,a.ISSUBSYSSUBJECT,a.DRCASHCHGCODE,a.CRCASHCHGCODE," +
                " a.ISFREECHARS1,a.FREECHARS1_TYPEID,a.FREECHARS1_CTRLMODE,a.ISFREECHARS2,a.FREECHARS2_TYPEID,a.FREECHARS2_CTRLMODE,a.ISFREECHARS3,a.FREECHARS3_TYPEID,a.FREECHARS3_CTRLMODE," +
                " b.account as accountname,a.memo,a.status "
                + " from DCP_COA  a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid "
                + " where a.eid='"+eId+"' and a.accountid='ALLCOA'  " +anotherCondition+
                " order by a.subjectid asc "
              //  ")a"
               // + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }

    protected String getQueryDetailSql(DCP_COAQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        String coaRefID = req.getRequest().getCoaRefID();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        String anotherCondition=" and 1=1 ";
        if(Check.NotNull(coaRefID)){
            anotherCondition=" and a.coaRefID='"+coaRefID+"' ";
        }


        if(Check.NotNull(keyTxt)){
            anotherCondition=" and (a.subjectid like '%%"+keyTxt+"%%' or a.subjectname like '%%"+keyTxt+"%%' )";
        }

        sqlbuf.append(" "
                // + " select * from ("
                //+ " select count(*) over () num,row_number() over (order by a.coarefid desc) as rn, a.* from ("
                + " select distinct a.SUBJECTID,a.SUBJECTNAME,a.AUXILIARYTYPE,a.COAREFID,a.ACCOUNTID,a.SUBJECTCAT,a.UPSUBJECTID,a.LEVELID,a.SUBJECTPROPERTY,a.SUBJECTTYPE,a.DIRECTION,a.ISDIRECTION," +
                " a.EXPTYPE,a.FINANALSOURCE,a.ISCASHSUBJECT,a.ISENABLEDPTMNG,a.ISENABLETRADOBJMNG,a.ISENABLEPRODCATMNG,a.ISENABLEMANMNG,a.ISMULTICURMNG,a.ISSUBSYSSUBJECT,a.DRCASHCHGCODE,a.CRCASHCHGCODE," +
                " a.ISFREECHARS1,a.FREECHARS1_TYPEID,a.FREECHARS1_CTRLMODE,a.ISFREECHARS2,a.FREECHARS2_TYPEID,a.FREECHARS2_CTRLMODE,a.ISFREECHARS3,a.FREECHARS3_TYPEID,a.FREECHARS3_CTRLMODE," +
                " b.account as accountname,a.memo,a.status "
                + " from DCP_COA  a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid "
                + " where a.eid='"+eId+"'  " +anotherCondition+
                " order by a.subjectid asc "
                //  ")a"
                // + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }

    public void fetchChildren(DCP_COAQueryRes res,List<DCP_COAQueryRes.SubjectInfo> subjectList,List<Map<String, Object>> getQData,List<Map<String, Object>> getQDataDetail,DCP_COAQueryRes.Level1Elm level1Elm){
        List<DCP_COAQueryRes.SubjectInfo> childrenSubjectList = subjectList.stream().filter(x -> x.getUpSubjectId().equals(level1Elm.getSubjectId())&&!x.getSubjectId().equals(level1Elm.getSubjectId())).collect(Collectors.toList());
        if(childrenSubjectList.size()>0){
            for (DCP_COAQueryRes.SubjectInfo childrenSubject : childrenSubjectList){
                DCP_COAQueryRes.Level1Elm childrenLevel = res.new Level1Elm();
                childrenLevel.setCoaRefID(childrenSubject.getCoaRefId());
                childrenLevel.setSubjectId(childrenSubject.getSubjectId());
                childrenLevel.setSubjectName(childrenSubject.getSubjectName());
                childrenLevel.setSubjectCat(childrenSubject.getSubjectCat());
                childrenLevel.setLevelID(childrenSubject.getLevelID());
                childrenLevel.setUpSubjectId(childrenSubject.getUpSubjectId());
                childrenLevel.setFirstSubjectId(childrenSubject.getFirstSubjectId());

                List<Map<String, Object>> collect = getQData.stream().filter(x -> x.get("COAREFID").toString().equals(childrenSubject.getCoaRefId())
                        && x.get("SUBJECTID").toString().equals(childrenSubject.getSubjectId())
                ).collect(Collectors.toList());
                if(collect.size()>0){
                    List<Map<String, Object>> accounts = getQDataDetail.stream().filter(x -> x.get("COAREFID").toString().equals(childrenSubject.getCoaRefId())
                            && x.get("SUBJECTID").toString().equals(childrenSubject.getSubjectId())
                            &&!x.get("ACCOUNTID").toString().equals("ALLCOA")
                    ).collect(Collectors.toList());
                    childrenLevel.setAccountList(accounts.stream().map(x -> {
                        DCP_COAQueryRes.AccountList accountList = res.new AccountList();
                        accountList.setAccountId(x.get("ACCOUNTID").toString());
                        accountList.setAccountName(x.get("ACCOUNTNAME").toString());
                        accountList.setStatus(x.get("STATUS").toString());
                        return accountList;
                    }).collect(Collectors.toList()));

                    Map<String, Object> stringObjectMap = collect.get(0);
                    childrenLevel.setAuxiliaryType(stringObjectMap.get("AUXILIARYTYPE").toString());
                    childrenLevel.setMemo(stringObjectMap.get("MEMO").toString());
                    childrenLevel.setSubjectProperty(stringObjectMap.get("SUBJECTPROPERTY").toString());
                    childrenLevel.setSubjectType(stringObjectMap.get("SUBJECTTYPE").toString());
                    childrenLevel.setDirection(stringObjectMap.get("DIRECTION").toString());
                    childrenLevel.setIsDirection(stringObjectMap.get("ISDIRECTION").toString());
                    childrenLevel.setExpType(stringObjectMap.get("EXPTYPE").toString());
                    childrenLevel.setFinAnalSource(stringObjectMap.get("FINANALSOURCE").toString());
                    childrenLevel.setIsCashSubject(stringObjectMap.get("ISCASHSUBJECT").toString());
                    childrenLevel.setIsEnableDptMng(stringObjectMap.get("ISENABLEDPTMNG").toString());
                    childrenLevel.setIsEnableTradObjMng(stringObjectMap.get("ISENABLETRADOBJMNG").toString());
                    childrenLevel.setIsEnableProdCatMng(stringObjectMap.get("ISENABLEPRODCATMNG").toString());
                    childrenLevel.setIsEnableManMng(stringObjectMap.get("ISENABLEMANMNG").toString());
                    childrenLevel.setIsMultiCurMng(stringObjectMap.get("ISMULTICURMNG").toString());
                    childrenLevel.setIsSubsysSubject(stringObjectMap.get("ISSUBSYSSUBJECT").toString());
                    childrenLevel.setDrCashChgCode(stringObjectMap.get("DRCASHCHGCODE").toString());
                    childrenLevel.setCrCashChgCode(stringObjectMap.get("CRCASHCHGCODE").toString());
                    childrenLevel.setIsFreeChars1(stringObjectMap.get("ISFREECHARS1").toString());
                    childrenLevel.setFreeChars1TypeId(stringObjectMap.get("FREECHARS1_TYPEID").toString());
                    childrenLevel.setFreeChars1CtrlMode(stringObjectMap.get("FREECHARS1_CTRLMODE").toString());
                    childrenLevel.setIsFreeChars2(stringObjectMap.get("ISFREECHARS2").toString());
                    childrenLevel.setFreeChars2TypeId(stringObjectMap.get("FREECHARS2_TYPEID").toString());
                    childrenLevel.setFreeChars2CtrlMode(stringObjectMap.get("FREECHARS2_CTRLMODE").toString());
                    childrenLevel.setIsFreeChars3(stringObjectMap.get("ISFREECHARS3").toString());
                    childrenLevel.setFreeChars3TypeId(stringObjectMap.get("FREECHARS3_TYPEID").toString());
                    childrenLevel.setFreeChars3CtrlMode(stringObjectMap.get("FREECHARS3_CTRLMODE").toString());
                    childrenLevel.setStatus(stringObjectMap.get("STATUS").toString());

                    childrenLevel.setChildren(new ArrayList<>());
                    fetchChildren(res,subjectList,getQData,getQDataDetail,childrenLevel);
                }

                level1Elm.getChildren().add(childrenLevel);
            }

        }

    }
}


