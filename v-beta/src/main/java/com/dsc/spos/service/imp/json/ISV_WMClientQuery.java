package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.ISV_WMClientQueryReq;
import com.dsc.spos.json.cust.res.ISV_WMClientQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ISV_WMClientQuery extends SPosBasicService<ISV_WMClientQueryReq, ISV_WMClientQueryRes> {
    @Override
    protected boolean isVerifyFail(ISV_WMClientQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；

        if (req.getRequest() == null)
        {
            errMsg.append("request不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(req.getRequest().getClientNo()))
        {
            errMsg.append("应用唯一标识clientNo不可为空值, ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<ISV_WMClientQueryReq> getRequestType() {
        return new TypeToken<ISV_WMClientQueryReq>(){};
    }

    @Override
    protected ISV_WMClientQueryRes getResponseType() {
        return new ISV_WMClientQueryRes();
    }

    @Override
    protected ISV_WMClientQueryRes processJson(ISV_WMClientQueryReq req) throws Exception {
        ISV_WMClientQueryRes res = this.getResponseType();
        String clientNo = req.getRequest().getClientNo();
        String sql = " select * from ISV_WM_CLIENT where clientno='"+clientNo+"'";
        List<Map<String,Object>> getQDatas = this.doQueryData(sql,null);
        if (getQDatas==null||getQDatas.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务端查询该应用("+clientNo+")失败！");
        }
        ISV_WMClientQueryRes.responseDatas datas = res.new responseDatas();
        Map<String,Object> map = getQDatas.get(0);
        String custNo = map.get("CUSTOMERNO").toString();
        String custName = map.get("CUSTOMERNAME").toString();
        String mainURL = map.get("CLIENTMAINURL").toString();
        String MEITUAN_REGISTER = map.get("MEITUAN_REGISTER").toString();
        String ELEME_REGISTER = map.get("ELEME_REGISTER").toString();
        String createTime = map.get("CREATETIME").toString();
        String createOpId = map.get("CREATEOPID").toString();
        String createOpName = map.get("CREATEOPNAME").toString();
        String lastModiTime = map.get("LASTMODITIME").toString();
        String lastModiOpId = map.get("LASTMODIOPID").toString();
        String lastModiOpName = map.get("LASTMODIOPNAME").toString();
        String memo = map.get("MEMO").toString();
        String status = map.get("STATUS").toString();//(0-待审核，100-审核通过，-1-审核失败)
        datas.setClientNo(clientNo);
        datas.setCustNo(custNo);
        datas.setCustName(custName);
        datas.setMainURL(mainURL);
        datas.setCreateTime(createTime);
        datas.setCreateOpId(createOpId);
        datas.setCreateOpName(createOpName);
        datas.setLastModiTime(lastModiTime);
        datas.setLastModiOpId(lastModiOpId);
        datas.setLastModiOpName(lastModiOpName);
        datas.setMemo(memo);
        datas.setStatus(status);

        datas.setRegLoadDocTypeList(new ArrayList<>());
        ISV_WMClientQueryRes.regType regType = res.new regType();
        regType.setLoadDocType(orderLoadDocType.MEITUAN);
        regType.setLoadDocTypeName("美团外卖");
        regType.setIsRegister("N");
        datas.getRegLoadDocTypeList().add(regType);
        if ("Y".equals(MEITUAN_REGISTER))
        {
            regType.setIsRegister("Y");
        }
        regType = res.new regType();
        regType.setLoadDocType(orderLoadDocType.ELEME);
        regType.setLoadDocTypeName("饿了么外卖");
        regType.setIsRegister("N");
        datas.getRegLoadDocTypeList().add(regType);
        if ("Y".equals(ELEME_REGISTER))
        {
            regType.setIsRegister("Y");
        }

        res.setDatas(datas);
        res.setSuccess(true);
        res.setServiceDescription("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(ISV_WMClientQueryReq req) throws Exception {
        return null;
    }
}
