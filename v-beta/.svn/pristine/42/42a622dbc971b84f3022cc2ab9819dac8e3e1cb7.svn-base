package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWMClientQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWMClientQueryRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.util.*;

public class DCP_ISVWMClientQuery extends SPosBasicService<DCP_ISVWMClientQueryReq, DCP_ISVWMClientQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWMClientQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ISVWMClientQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWMClientQueryReq>(){};
    }

    @Override
    protected DCP_ISVWMClientQueryRes getResponseType() {
        return new DCP_ISVWMClientQueryRes();
    }

    @Override
    protected DCP_ISVWMClientQueryRes processJson(DCP_ISVWMClientQueryReq req) throws Exception {
        DCP_ISVWMClientQueryRes res = this.getResponseType();
        DCP_ISVWMClientQueryRes.responseDatas datas = res.new responseDatas();
        //res.setDatas(datas);
        res.setSuccess(true);
        res.setServiceDescription("000");
        res.setServiceDescription("服务执行成功");
        String eId = req.geteId();
        //先查询有没有，客户唯一标识 这个不区分 企业ID
        String clientNoSql =" SELECT * from DCP_ISVWM_CLIENT WHERE eid = '"+eId+"'";
        List<Map<String, Object>> getQDatas = this.doQueryData(clientNoSql, null);
        if (getQDatas==null||getQDatas.isEmpty())
        {
            //没有的话，需要进行应用申请
            return res;
        }
        Map<String,Object> map = getQDatas.get(0);
        String clientNo = map.get("CLIENTNO").toString();
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
        DCP_ISVWMClientQueryRes.regType regType = res.new regType();
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
        /*
        //请求下服务端
        JSONObject req_isv = new JSONObject();
        req_isv.put("token","1234567890");
        req_isv.put("serviceId","ISV_WMClientQuery");

        JSONObject request_isv = new JSONObject();
        request_isv.put("clientNo",clientNo);
        req_isv.put("request",request_isv);
        String reqStr = req_isv.toString();
        String isv_Url = "http://eliutong2.digiwin.com.cn/dcpService/DCP/services/invoke";//暂时写死157的3.0
        String resStr= HttpSend.doPost(isv_Url,reqStr,null);
        ParseJson pj  = new ParseJson();
        DCP_ISVWMClientQueryRes res_isv_obj = pj.jsonToBean(resStr,new TypeToken<DCP_ISVWMClientQueryRes>(){});
        */

      /*  res.setDatas(datas);
        res.setSuccess(true);
        res.setServiceDescription("000");
        res.setServiceDescription("服务执行成功");*/
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVWMClientQueryReq req) throws Exception {
        return null;
    }
}
