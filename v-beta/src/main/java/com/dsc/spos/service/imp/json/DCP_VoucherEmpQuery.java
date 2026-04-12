package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_VoucherEmpQueryReq;
import com.dsc.spos.json.cust.res.DCP_VoucherEmpQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_VoucherEmpQuery extends SPosBasicService<DCP_VoucherEmpQueryReq, DCP_VoucherEmpQueryRes>
{


    @Override
    protected boolean isVerifyFail(DCP_VoucherEmpQueryReq req) throws Exception
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
    protected TypeToken<DCP_VoucherEmpQueryReq> getRequestType()
    {
        return new TypeToken<DCP_VoucherEmpQueryReq>(){};
    }

    @Override
    protected DCP_VoucherEmpQueryRes getResponseType()
    {
        return new DCP_VoucherEmpQueryRes();
    }

    @Override
    protected DCP_VoucherEmpQueryRes processJson(DCP_VoucherEmpQueryReq req) throws Exception
    {
        DCP_VoucherEmpQueryRes res=getResponseType();

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        res.setDatas(res.new level1Elm());
        res.getDatas().setEmpList(new ArrayList<>());

        if(getQData!=null && getQData.isEmpty()==false)
        {
            for (Map<String, Object> map : getQData)
            {
                DCP_VoucherEmpQueryRes.level2Elm lv2=res.new level2Elm();

                lv2.setOpNo(map.get("OPNO").toString());
                lv2.setOpName(map.get("OP_NAME").toString());
                lv2.setOpNoOut(map.get("OPNO_OUT").toString());
                res.getDatas().getEmpList().add(lv2);
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
    protected String getQuerySql(DCP_VoucherEmpQueryReq req) throws Exception
    {
        String keyTxt=req.getRequest().getKeyTxt();

        StringBuffer sb=new StringBuffer("select a.opno,c.op_name,a.opno_out from DCP_VOUCHER_EMP a " +
                                                 "left join PLATFORM_STAFFS b on a.eid=b.eid and a.opno=b.opno " +
                                                 "left join PLATFORM_STAFFS_LANG c on b.eid=c.eid and b.opno=c.opno and c.lang_type='"+req.getLangType()+"'  " );

        if (Check.Null(keyTxt)==false)
        {
            sb.append("where a.opno like '%%"+keyTxt+"%%' or c.op_name like '%%"+keyTxt+"%%' or a.opno_out like '%%"+keyTxt+"%%' ");
        }
        return sb.toString();
    }


}
