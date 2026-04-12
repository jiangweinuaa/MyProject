package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AccCOAQueryReq;
import com.dsc.spos.json.cust.res.DCP_AccCOAQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_AccCOAQuery extends SPosBasicService<DCP_AccCOAQueryReq, DCP_AccCOAQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_AccCOAQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_AccCOAQueryReq> getRequestType() {
        return new TypeToken<DCP_AccCOAQueryReq>() {
        };
    }

    @Override
    protected DCP_AccCOAQueryRes getResponseType() {
        return new DCP_AccCOAQueryRes();
    }

    @Override
    protected DCP_AccCOAQueryRes processJson(DCP_AccCOAQueryReq req) throws Exception {
        DCP_AccCOAQueryRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);

        res.setDatas(new ArrayList<>());

        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
//
//            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
//
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setPageSize(req.getPageSize());
            res.setPageNumber(req.getPageNumber());

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("COAREFID", true);

            List<Map<String, Object>> distinctData = MapDistinct.getMap(getData, distinct);

            for (Map<String, Object> data : distinctData) {
                DCP_AccCOAQueryRes.Request oneReq = res.new Request();
                res.getDatas().add(oneReq);

                oneReq.setCoaRefID(data.get("COAREFID").toString());
                oneReq.setAccountList(new ArrayList<>());

                oneReq.setCreateBy(data.get("CREATEBY").toString());
                oneReq.setCreate_Date(data.get("CREATE_DATE").toString());
                oneReq.setCreate_Time(data.get("CREATE_TIME").toString());

                oneReq.setModifyBy(data.get("MODIFYBY").toString());
                oneReq.setModify_Date(data.get("MODIFY_DATE").toString());
                oneReq.setModify_Time(data.get("MODIFY_TIME").toString());

                oneReq.setConfirmBy(data.get("CONFIRMBY").toString());
                oneReq.setConfirm_Date(data.get("CONFIRM_DATE").toString());
                oneReq.setConfirm_Time(data.get("CONFIRM_TIME").toString());

                oneReq.setCancelBy(data.get("CANCELBY").toString());
                oneReq.setCancel_Date(data.get("CANCEL_DATE").toString());
                oneReq.setCancel_Time(data.get("CANCEL_TIME").toString());

                Map<String, Object> condition = new HashMap<>();
                condition.put("EID", data.get("EID").toString());
                condition.put("COAREFID", data.get("COAREFID").toString());

                List<Map<String, Object>> accountData = MapDistinct.getWhereMap(getData, condition, true);
                distinct = new HashMap<>();
                distinct.put("EID", true);
                distinct.put("COAREFID", true);
                distinct.put("ACCOUNTID", true);
                List<Map<String, Object>> distinctAccountData = MapDistinct.getMap(accountData, distinct);

                for (Map<String, Object> data2 : distinctAccountData) {
                    DCP_AccCOAQueryRes.AccountList oneAccount = res.new AccountList();
                    oneReq.getAccountList().add(oneAccount);

                    oneAccount.setCoaList(new ArrayList<>());

                    oneAccount.setAccountId(data2.get("ACCOUNTID").toString());
                    oneAccount.setAccountName(data2.get("ACCOUNTNAME").toString());

                    condition = new HashMap<>();
                    condition.put("EID", data2.get("EID").toString());
                    condition.put("COAREFID", data2.get("COAREFID").toString());
                    condition.put("ACCOUNTID", data2.get("ACCOUNTID").toString());

                    List<Map<String, Object>> coaData = MapDistinct.getWhereMap(getData, condition, true);

                    for (Map<String, Object> data3 : coaData) {
                        DCP_AccCOAQueryRes.CoaList oneCoa = res.new CoaList();
                        oneAccount.getCoaList().add(oneCoa);

                        oneCoa.setAccountId(data3.get("ACCOUNTID").toString());
                        oneCoa.setAccountName(data3.get("ACCOUNTNAME").toString());
                        oneCoa.setSubjectId(data3.get("SUBJECTID").toString());
                        oneCoa.setSubjectName(data3.get("SUBJECTNAME").toString());
                        oneCoa.setAuxiliaryType(data3.get("AUXILIARYTYPE").toString());
                        oneCoa.setMemo(data3.get("MEMO").toString());
                        oneCoa.setCoaRefID(data3.get("COAREFID").toString());
                        oneCoa.setSubjectType(data3.get("SUBJECTTYPE").toString());
                        oneCoa.setUpSubjectCat(data3.get("UPSUBJECTID").toString());
                        oneCoa.setSubjectCat(data3.get("SUBJECTCAT").toString());
                        oneCoa.setLevelID(data3.get("LEVELID").toString());
                        oneCoa.setSubjectProperty(data3.get("SUBJECTPROPERTY").toString());
                        oneCoa.setDirection(data3.get("DIRECTION").toString());
                        oneCoa.setIsDirection(data3.get("ISDIRECTION").toString());
                        oneCoa.setExpType(data3.get("EXPTYPE").toString());
                        oneCoa.setFinAnalSource(data3.get("FINANALSOURCE").toString());
                        oneCoa.setIsCashSubject(data3.get("ISCASHSUBJECT").toString());
                        oneCoa.setIsEnableDptMng(data3.get("ISENABLEDPTMNG").toString());
                        oneCoa.setIsEnableTradObjMng(data3.get("ISENABLETRADOBJMNG").toString());
                        oneCoa.setIsEnableProdCatMng(data3.get("ISENABLEPRODCATMNG").toString());
                        oneCoa.setIsEnableManMng(data3.get("ISENABLEMANMNG").toString());
                        oneCoa.setIsMultiCurMng(data3.get("ISMULTICURMNG").toString());
                        oneCoa.setIsSubsysSubject(data3.get("ISSUBSYSSUBJECT").toString());
                        oneCoa.setDrCashChgCode(data3.get("DRCASHCHGCODE").toString());
                        oneCoa.setCrCashChgCode(data3.get("CRCASHCHGCODE").toString());

                        oneCoa.setIsFreeChars1(data3.get("ISFREECHARS1").toString());
                        oneCoa.setIsFreeChars2(data3.get("ISFREECHARS2").toString());
                        oneCoa.setIsFreeChars3(data3.get("ISFREECHARS3").toString());

                        oneCoa.setFreeChars1TypeID(data3.get("FREECHARS1_TYPEID").toString());
                        oneCoa.setFreeChars1CtrlMode(data3.get("FREECHARS1_CTRLMODE").toString());

                        oneCoa.setFreeChars2TypeID(data3.get("FREECHARS2_TYPEID").toString());
                        oneCoa.setFreeChars2CtrlMode(data3.get("FREECHARS2_CTRLMODE").toString());

                        oneCoa.setFreeChars3TypeID(data3.get("FREECHARS3_TYPEID").toString());
                        oneCoa.setFreeChars3CtrlMode(data3.get("FREECHARS3_CTRLMODE").toString());

                        oneCoa.setStatus(data3.get("STATUS").toString());
                        
                    }
                }
            }

        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_AccCOAQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        querySql.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.COAREFID ) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* from ( ");
//                .append(" FROM DCP_COA a ")
//                .append(" LEFT JOIN DCP_ACOUNT_SETTING b ON a.ACCOUNTID=b.ACCOUNTID ")
//        ;
        querySql.append(" SELECT " +
                        " a.* ")
                .append(" ,b.ACCOUNT ACCOUNTNAME ")
                .append(" FROM DCP_COA a ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID ")
        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getCoaRefID())) {
            querySql.append(" AND a.COAREFID='").append(req.getRequest().getCoaRefID()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            querySql.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("' ");
        }

        querySql.append("  ) a  ) "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY rn ");
        return querySql.toString();
    }
}
