package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.ISV_WMClientRegisterReq;
import com.dsc.spos.json.cust.res.ISV_WMClientRegisterRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.isv.ISV_HelpTools;
import com.dsc.spos.waimai.isv.ISV_WMUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ISV_WMClientRegister extends SPosAdvanceService<ISV_WMClientRegisterReq, ISV_WMClientRegisterRes> {
    @Override
    protected void processDUID(ISV_WMClientRegisterReq req, ISV_WMClientRegisterRes res) throws Exception {

        try
        {
            String clientNo = ISV_HelpTools.generateShortUuid();
            String mainURL = req.getRequest().getMainURL();
            if(mainURL.toLowerCase().contains("localhost")||mainURL.contains("127.0.0.1"))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "不能使用本地主机地址:"+mainURL);
            }
            String url_dcpService = "";
            if ("retaildev.digiwin.com.cn".equals(mainURL))
            {
                url_dcpService = ISV_WMUtils.dcpService_url.replace("[doMain]",mainURL).replace("dcpService","dcpService_3.0");
            }
            else
            {
                url_dcpService = ISV_WMUtils.dcpService_url.replace("[doMain]",mainURL);
            }
            if (!ISV_HelpTools.doGetUrlConnect(url_dcpService))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "域名对应的DCP中台地址无法访问:"+url_dcpService);
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
                for (ISV_WMClientRegisterReq.regType par : req.getRequest().getRegLoadDocTypeList() )
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

            String lastModiTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String[] columns1 = { "CLIENTNO","CLIENTMAINURL","CUSTOMERNO","CUSTOMERNAME","MEITUAN_REGISTER","ELEME_REGISTER",
                    "MEMO","CREATEOPID","CREATEOPNAME","CREATETIME"};
            DataValue[] insValue1 = null;

            insValue1 = new DataValue[]{
                    new DataValue(clientNo, Types.VARCHAR),
                    new DataValue(mainURL, Types.VARCHAR),
                    new DataValue(req.getRequest().getCustNo(), Types.VARCHAR),
                    new DataValue(req.getRequest().getCustName(), Types.VARCHAR),
                    new DataValue(MEITUAN_REGISTER, Types.VARCHAR),
                    new DataValue(ELEME_REGISTER, Types.VARCHAR),
                    new DataValue(memo, Types.VARCHAR),
                    new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                    new DataValue(req.getRequest().getOpName(), Types.VARCHAR),
                    new DataValue(lastModiTimeStr, Types.DATE)
            };

            InsBean ib1 = new InsBean("ISV_WM_CLIENT", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

            this.doExecuteDataToDB();
            ISV_WMClientRegisterRes.responseDatas datas = res.new responseDatas();
            datas.setClientNo(clientNo);
            res.setDatas(datas);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        catch (Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(ISV_WMClientRegisterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(ISV_WMClientRegisterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(ISV_WMClientRegisterReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(ISV_WMClientRegisterReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；

        if (req.getRequest() == null)
        {
            errMsg.append("request不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
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
    protected TypeToken<ISV_WMClientRegisterReq> getRequestType() {
        return new TypeToken<ISV_WMClientRegisterReq>(){};
    }

    @Override
    protected ISV_WMClientRegisterRes getResponseType() {
        return new ISV_WMClientRegisterRes();
    }
}
