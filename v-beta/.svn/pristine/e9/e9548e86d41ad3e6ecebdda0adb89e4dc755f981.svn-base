package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWMClientUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ISVWMClientRegisterRes;
import com.dsc.spos.json.cust.res.DCP_ISVWMClientUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.isv.ISV_HelpTools;
import com.dsc.spos.waimai.isv.ISV_WMUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_ISVWMClientUpdate extends SPosAdvanceService<DCP_ISVWMClientUpdateReq, DCP_ISVWMClientUpdateRes> {
    @Override
    protected void processDUID(DCP_ISVWMClientUpdateReq req, DCP_ISVWMClientUpdateRes res) throws Exception {

        try
        {
            String eId = req.geteId();
            String clientNo = req.getRequest().getClientNo();
            //先查询有没有，客户唯一标识 这个不区分 企业ID
            String clientNoSql =" SELECT * from DCP_ISVWM_CLIENT WHERE  EID='"+eId+"' and CLIENTNO ='"+clientNo+"'";
            List<Map<String, Object>> getQDatas = this.doQueryData(clientNoSql, null);
            if (getQDatas==null||getQDatas.isEmpty())
            {
                //没有的话，需要进行应用申请
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该应用("+clientNo+")本地查询不到资料，无法修改！");
            }
            String mainURL = req.getRequest().getMainURL().trim();
            if (mainURL.startsWith("http://")||mainURL.startsWith("https://"))
            {
                mainURL = mainURL.replace("http://","").replace("https://","");
                req.getRequest().setMainURL(mainURL);
            }

            String memo = req.getRequest().getMemo();
            if (!Check.Null(memo))
            {
                if (memo.length()>255)
                {
                    memo = memo.substring(0,255);
                }
            }
            String MEITUAN_REGISTER = "N";
            String ELEME_REGISTER = "N";
            if (req.getRequest().getRegLoadDocTypeList()!=null&&!req.getRequest().getRegLoadDocTypeList().isEmpty())
            {
                for (DCP_ISVWMClientUpdateReq.regType par : req.getRequest().getRegLoadDocTypeList() )
                {
                    if (orderLoadDocType.MEITUAN.equals(par.getLoadDocType()))
                    {
                        if ("Y".equals(par.getIsRegister()))
                        {
                            MEITUAN_REGISTER = "Y";
                        }
                    }
                    if (orderLoadDocType.ELEME.equals(par.getLoadDocType()))
                    {
                        if ("Y".equals(par.getIsRegister()))
                        {
                            ELEME_REGISTER = "Y";
                        }
                    }
                }
            }

            String opNo = req.getOpNO();
            String opName = req.getOpName();
            req.getRequest().setOpNo(opNo);
            req.getRequest().setOpName(opName);
            req.setServiceId("ISV_WMClientUpdate");
            ParseJson pj  = new ParseJson();
            String reqStr = pj.beanToJson(req);
            String isv_Url = "http://eliutong2.digiwin.com.cn/dcpService/DCP/services/invoke";//暂时写死157的3.0
            if (!Check.Null(StaticInfo.microMarkHttpPost)&&StaticInfo.microMarkHttpPost.contains("DCP/services/invoke"))
            {
                isv_Url = StaticInfo.microMarkHttpPost;
            }
            String isvTestUrl = isv_Url.replace("/invoke","");
            if(isvTestUrl.toLowerCase().contains("localhost")||isvTestUrl.contains("127.0.0.1"))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务商接口地址配置错误:"+isvTestUrl);
            }
            if (!ISV_HelpTools.doGetUrlConnect(isvTestUrl))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务商接口地址无法访问:"+isvTestUrl);
            }
            String lastModiTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String resStr= HttpSend.doPost(isv_Url,reqStr,null,"");
            DCP_ISVWMClientUpdateRes res_isv = pj.jsonToBean(resStr,new TypeToken<DCP_ISVWMClientUpdateRes>(){});
            res.setSuccess(res_isv.isSuccess());
            res.setServiceDescription(res_isv.getServiceDescription());
            res.setServiceStatus(res_isv.getServiceStatus());
            if (res_isv.isSuccess())
            {
                UptBean up1 = new UptBean("DCP_ISVWM_CLIENT");
                up1.addCondition("CLIENTNO",new DataValue(clientNo, Types.VARCHAR));

                up1.addUpdateValue("CLIENTMAINURL",new DataValue(mainURL, Types.VARCHAR));
                up1.addUpdateValue("CUSTOMERNO",new DataValue(req.getRequest().getCustNo(), Types.VARCHAR));
                up1.addUpdateValue("CUSTOMERNAME",new DataValue(req.getRequest().getCustName(), Types.VARCHAR));
                up1.addUpdateValue("MEITUAN_REGISTER",new DataValue(MEITUAN_REGISTER, Types.VARCHAR));
                up1.addUpdateValue("ELEME_REGISTER",new DataValue(ELEME_REGISTER, Types.VARCHAR));
                up1.addUpdateValue("LASTMODIOPID",new DataValue(opNo, Types.VARCHAR));
                up1.addUpdateValue("LASTMODIOPNAME",new DataValue(opName, Types.VARCHAR));
                up1.addUpdateValue("LASTMODITIME",new DataValue(lastModiTimeStr, Types.DATE));
                if (req.getRequest().getMemo()!=null)
                {
                    up1.addUpdateValue("MEMO",new DataValue(memo, Types.VARCHAR));
                }

                this.addProcessData(new DataProcessBean(up1)); // 新增單頭
                this.doExecuteDataToDB();
            }

        }
        catch (Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWMClientUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWMClientUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWMClientUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWMClientUpdateReq req) throws Exception {
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
            errMsg.append("应用标识clientNo不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getCustNo()))
        {
            errMsg.append("客户编码custNo不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getCustName()))
        {
            errMsg.append("客户名称custName不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getMainURL()))
        {
            errMsg.append("客户中台域名mainURL不可为空值, ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ISVWMClientUpdateReq> getRequestType() {
        return new TypeToken<DCP_ISVWMClientUpdateReq>(){};
    }

    @Override
    protected DCP_ISVWMClientUpdateRes getResponseType() {
        return new DCP_ISVWMClientUpdateRes();
    }
}
