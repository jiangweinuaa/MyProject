package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AccountingSubjectQueryReq;
import com.dsc.spos.json.cust.res.DCP_AccountingSubjectQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_AccountingSubjectQuery extends SPosBasicService<DCP_AccountingSubjectQueryReq, DCP_AccountingSubjectQueryRes>
{


    @Override
    protected boolean isVerifyFail(DCP_AccountingSubjectQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AccountingSubjectQueryReq> getRequestType()
    {
        return new TypeToken<DCP_AccountingSubjectQueryReq>(){};
    }

    @Override
    protected DCP_AccountingSubjectQueryRes getResponseType()
    {
        return new DCP_AccountingSubjectQueryRes();
    }

    @Override
    protected DCP_AccountingSubjectQueryRes processJson(DCP_AccountingSubjectQueryReq req) throws Exception
    {

        DCP_AccountingSubjectQueryRes res=getResponseType();

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        res.setDatas(res.new level1Elm());
        res.getDatas().setSubjectList(new ArrayList<>());

        if(getQData!=null && getQData.isEmpty()==false)
        {
            for (Map<String, Object> map : getQData)
            {
                DCP_AccountingSubjectQueryRes.level2Elm lv2=res.new level2Elm();
                lv2.setSubjectId(map.get("SUBJECTID").toString());
                lv2.setSubjectName(map.get("SUBJECTNAME").toString());
                lv2.setAuxiliaryType(map.get("AUXILIARYTYPE").toString());
                lv2.setDirection(map.get("DIRECTION").toString());
                lv2.setMemo(map.get("MEMO").toString());
                res.getDatas().getSubjectList().add(lv2);
                lv2=null;
            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {

    }

    @Override
    protected String getQuerySql(DCP_AccountingSubjectQueryReq req) throws Exception
    {
        String keyTxt = req.getRequest().getKeyTxt();

        StringBuffer sb=new StringBuffer("select * from DCP_ACCOUNTINGSUBJECT WHERE EID='"+req.geteId()+"' ");

        if (Check.Null(keyTxt)==false)
        {
            sb.append("AND (SUBJECTID LIKE '%%"+keyTxt+"%%'  OR SUBJECTNAME LIKE '%%"+keyTxt+"%%') ");
        }

        return sb.toString();
    }



}
