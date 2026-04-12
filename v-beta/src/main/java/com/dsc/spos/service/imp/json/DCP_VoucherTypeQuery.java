package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_VoucherTypeQueryReq;
import com.dsc.spos.json.cust.res.DCP_VoucherTypeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_VoucherTypeQuery extends SPosBasicService<DCP_VoucherTypeQueryReq, DCP_VoucherTypeQueryRes>
{


    @Override
    protected boolean isVerifyFail(DCP_VoucherTypeQueryReq req) throws Exception
    {
        boolean isFail = false;
//        StringBuffer errMsg = new StringBuffer("");
//
//        if(req.getRequest()==null)
//        {
//            errMsg.append("requset不能为空值 ");
//            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
//        }
//
//        if (isFail)
//        {
//            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
//        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_VoucherTypeQueryReq> getRequestType()
    {
        return new TypeToken<DCP_VoucherTypeQueryReq>(){};
    }

    @Override
    protected DCP_VoucherTypeQueryRes getResponseType()
    {
        return new DCP_VoucherTypeQueryRes();
    }

    @Override
    protected DCP_VoucherTypeQueryRes processJson(DCP_VoucherTypeQueryReq req) throws Exception
    {
        DCP_VoucherTypeQueryRes res=getResponseType();

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        res.setDatas(res.new level1Elm());
        res.getDatas().setVoucherTypeList(new ArrayList<>());

        if(getQData!=null && getQData.isEmpty()==false)
        {
            for (Map<String, Object> map : getQData)
            {
                DCP_VoucherTypeQueryRes.level2Elm lv2=res.new level2Elm();
                lv2.setVoucherType(map.get("VOUCHERTYPE").toString());
                lv2.setVoucherName(map.get("VOUCHERNAME").toString());
                lv2.setMemo(map.get("MEMO").toString());
                res.getDatas().getVoucherTypeList().add(lv2);
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
    protected String getQuerySql(DCP_VoucherTypeQueryReq req) throws Exception
    {

        StringBuffer sb=new StringBuffer("select * from DCP_VOUCHERTYPE ");

        return sb.toString();
    }


}
