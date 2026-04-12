package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.ISV_WMClientUpdateReq;
import com.dsc.spos.json.cust.res.ISV_WMClientUpdateRes;
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
import java.util.Map;

public class ISV_WMClientUpdate extends SPosAdvanceService<ISV_WMClientUpdateReq, ISV_WMClientUpdateRes> {
    @Override
    protected void processDUID(ISV_WMClientUpdateReq req, ISV_WMClientUpdateRes res) throws Exception {

        try
        {
            String clientNo = req.getRequest().getClientNo();
            //先查询有没有，客户唯一标识 这个不区分 企业ID
            String clientNoSql =" SELECT * from ISV_WM_CLIENT WHERE CLIENTNO = '"+clientNo+"'";
            List<Map<String, Object>> getQDatas = this.doQueryData(clientNoSql, null);
            if (getQDatas==null||getQDatas.isEmpty())
            {
                //没有的话，需要进行应用申请
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该应用("+clientNo+")服务商查询不到资料，无法修改！");
            }
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
                for (ISV_WMClientUpdateReq.regType par : req.getRequest().getRegLoadDocTypeList() )
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

            UptBean up1 = new UptBean("ISV_WM_CLIENT");
            up1.addCondition("CLIENTNO",new DataValue(clientNo, Types.VARCHAR));

            up1.addUpdateValue("CLIENTMAINURL",new DataValue(mainURL, Types.VARCHAR));
            up1.addUpdateValue("CUSTOMERNO",new DataValue(req.getRequest().getCustNo(), Types.VARCHAR));
            up1.addUpdateValue("CUSTOMERNAME",new DataValue(req.getRequest().getCustName(), Types.VARCHAR));
            up1.addUpdateValue("MEITUAN_REGISTER",new DataValue(MEITUAN_REGISTER, Types.VARCHAR));
            up1.addUpdateValue("ELEME_REGISTER",new DataValue(ELEME_REGISTER, Types.VARCHAR));
            up1.addUpdateValue("LASTMODIOPID",new DataValue(req.getRequest().getOpNo(), Types.VARCHAR));
            up1.addUpdateValue("LASTMODIOPNAME",new DataValue(req.getRequest().getOpName(), Types.VARCHAR));
            up1.addUpdateValue("LASTMODITIME",new DataValue(lastModiTimeStr, Types.DATE));
            if (req.getRequest().getMemo()!=null)
            {
                up1.addUpdateValue("MEMO",new DataValue(memo, Types.VARCHAR));
            }

            this.addProcessData(new DataProcessBean(up1)); // 新增單頭

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            if (ISV_WMUtils.clientAccounts!=null)
            {
                ISV_WMUtils.clientAccounts.put(clientNo,mainURL);
            }

        }
        catch (Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(ISV_WMClientUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(ISV_WMClientUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(ISV_WMClientUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(ISV_WMClientUpdateReq req) throws Exception {
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
    protected TypeToken<ISV_WMClientUpdateReq> getRequestType() {
        return new TypeToken<ISV_WMClientUpdateReq>(){};
    }

    @Override
    protected ISV_WMClientUpdateRes getResponseType() {
        return new ISV_WMClientUpdateRes();
    }
}
