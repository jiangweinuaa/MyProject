package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_VoucherEntryQueryReq;
import com.dsc.spos.json.cust.res.DCP_VoucherEntryQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_VoucherEntryQuery extends SPosBasicService<DCP_VoucherEntryQueryReq, DCP_VoucherEntryQueryRes>
{

    @Override
    protected boolean isVerifyFail(DCP_VoucherEntryQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String voucherType=req.getRequest().getVoucherType();
        if(Check.Null(voucherType))
        {
            errMsg.append("凭证类型不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_VoucherEntryQueryReq> getRequestType()
    {
        return new TypeToken<DCP_VoucherEntryQueryReq>(){};
    }

    @Override
    protected DCP_VoucherEntryQueryRes getResponseType()
    {
        return new DCP_VoucherEntryQueryRes();
    }

    @Override
    protected DCP_VoucherEntryQueryRes processJson(DCP_VoucherEntryQueryReq req) throws Exception
    {
        DCP_VoucherEntryQueryRes res=getResponseType();

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        res.setDatas(res.new level1Elm());
        res.getDatas().setVoucherEntryList(new ArrayList<>());

        if(getQData!=null && getQData.isEmpty()==false)
        {
            for (Map<String, Object> map : getQData)
            {
                DCP_VoucherEntryQueryRes.level2Elm lv2=res.new level2Elm();
                lv2.setVoucherType(map.get("VOUCHERTYPE").toString());
                lv2.setDebitOrCredit(map.get("DEBITORCREDIT").toString().equals("")?"1":map.get("DEBITORCREDIT").toString());
                lv2.setEntryId(map.get("ENTRYID").toString());
                lv2.setEntryName(map.get("ENTRYNAME").toString());
                lv2.setEntryType(map.get("ENTRYTYPE").toString());
                lv2.setSubjectId(map.get("SUBJECTID").toString());
                lv2.setSubjectName(map.get("SUBJECTNAME").toString());
                lv2.setMemo(map.get("MEMO").toString());
                res.getDatas().getVoucherEntryList().add(lv2);
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
    protected String getQuerySql(DCP_VoucherEntryQueryReq req) throws Exception
    {
        StringBuffer sb=new StringBuffer("select '"+req.getRequest().getVoucherType()+"' voucherType,a.ENTRYTYPE,a.ENTRYID,a.ENTRYNAME,b.DEBITORCREDIT,b.SUBJECTID,c.SUBJECTNAME,b.MEMO from " +
                                                 "( " +
                                                 "SELECT TO_CHAR(A.EID) EID,TO_CHAR('PAYTYPE') AS ENTRYTYPE,TO_CHAR(A.PAYTYPE) ENTRYID,TO_CHAR(B.PAYNAME) ENTRYNAME " +
                                                 "FROM DCP_PAYTYPE A " +
                                                 "LEFT JOIN DCP_PAYTYPE_LANG B ON A.EID=B.EID AND A.PAYTYPE=B.PAYTYPE " +
                                                 "WHERE A.EID='"+req.geteId()+"' AND B.LANG_TYPE='"+req.getLangType()+"' " +
                                                 "UNION ALL " +
                                                 "SELECT TO_CHAR(EID) EID,TO_CHAR('PAYCHANNEL'),TO_CHAR(PAYCODE),TO_CHAR(PAYTYPENAME) FROM CRM_ACCESSPAYTYPE " +
                                                 "WHERE EID='"+req.geteId()+"' AND PAYCODE<>'##' " +
                                                 ") a " +
                                                 "left join DCP_VOUCHERENTRY b on a.eid=b.eid and a.ENTRYTYPE=b.ENTRYTYPE and a.ENTRYID=b.ENTRYID and b.VOUCHERTYPE='"+req.getRequest().getVoucherType()+"' " +
                                                 "left join DCP_ACCOUNTINGSUBJECT c on a.eid=c.eid and b.SUBJECTID=c.SUBJECTID " +
                                                 "order by a.ENTRYTYPE desc,a.ENTRYID ");

        return sb.toString();
    }




}
