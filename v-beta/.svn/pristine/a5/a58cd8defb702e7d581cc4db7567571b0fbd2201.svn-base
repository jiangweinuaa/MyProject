package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_VoucherOrgQueryReq;
import com.dsc.spos.json.cust.res.DCP_VoucherOrgQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_VoucherOrgQuery extends SPosBasicService<DCP_VoucherOrgQueryReq, DCP_VoucherOrgQueryRes>
{


    @Override
    protected boolean isVerifyFail(DCP_VoucherOrgQueryReq req) throws Exception
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
    protected TypeToken<DCP_VoucherOrgQueryReq> getRequestType()
    {
        return new TypeToken<DCP_VoucherOrgQueryReq>(){};
    }

    @Override
    protected DCP_VoucherOrgQueryRes getResponseType()
    {
        return new DCP_VoucherOrgQueryRes();
    }

    @Override
    protected DCP_VoucherOrgQueryRes processJson(DCP_VoucherOrgQueryReq req) throws Exception
    {
        DCP_VoucherOrgQueryRes res=getResponseType();

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        res.setDatas(res.new level1Elm());
        res.getDatas().setOrgList(new ArrayList<>());

        if(getQData!=null && getQData.isEmpty()==false)
        {
            for (Map<String, Object> map : getQData)
            {
                DCP_VoucherOrgQueryRes.level2Elm lv2=res.new level2Elm();

                lv2.setOrgId(map.get("ORGID").toString());
                lv2.setOrgName(map.get("ORG_NAME").toString());
                lv2.setOrgIdOut(map.get("ORGID_OUT").toString());
                lv2.setOrgNameOut(map.get("ORGNAME_OUT").toString());
                lv2.setCompIdOut(map.get("COMPID_OUT").toString());
                lv2.setCompNameOut(map.get("COMPNAME_OUT").toString());
                res.getDatas().getOrgList().add(lv2);
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
    protected String getQuerySql(DCP_VoucherOrgQueryReq req) throws Exception
    {
        String keyTxt=req.getRequest().getKeyTxt();

        StringBuffer sb=new StringBuffer("select b.organizationno orgid,c.org_name,a.orgid_out,a.orgname_out,a.compid_out,a.compname_out from " +
                                                 "DCP_ORG b " +
                                                 "left join DCP_VOUCHER_ORG a on a.eid=b.eid and a.orgid=b.organizationno " +
                                                 "left join  DCP_ORG_lang c on b.eid=c.eid and b.organizationno=c.organizationno and c.lang_type='"+req.getLangType()+"' " +
                                                 "where b.eid='"+req.geteId()+"' " );
        if (Check.Null(keyTxt)==false)
        {
            sb.append(" and b.organizationno like '%%"+keyTxt+"%%' or c.org_name like '%%"+keyTxt+"%%' or a.orgid_out like '%%"+keyTxt+"%%' ");
        }
        sb.append("order by b.organizationno ");

        return  sb.toString();
    }


}
