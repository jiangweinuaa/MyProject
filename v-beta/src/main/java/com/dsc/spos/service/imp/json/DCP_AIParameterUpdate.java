package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AIParameterUpdateReq;
import com.dsc.spos.json.cust.res.DCP_AIParameterUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * 服务函数：DCP_AIParameterUpdate
 * 服务说明：AI应用信息修改
 * @author jinzma
 * @since  2025-10-23
 */
public class DCP_AIParameterUpdate extends SPosAdvanceService<DCP_AIParameterUpdateReq, DCP_AIParameterUpdateRes> {
    @Override
    protected void processDUID(DCP_AIParameterUpdateReq req, DCP_AIParameterUpdateRes res) throws Exception {
        
        String eId=req.geteId();
        
        
        //删除 DCP_AIPARAMETER
        DelBean db1 = new DelBean("DCP_AIPARAMETER");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        
        
        //插入 DCP_AIPARAMETER
        ColumnDataValue columnDataValue = new ColumnDataValue();
        columnDataValue.add("EID", eId, Types.VARCHAR);
        columnDataValue.add("TENANTID", req.getRequest().getTenantId(), Types.VARCHAR);
        columnDataValue.add("APPID", req.getRequest().getAppId(), Types.VARCHAR);
        columnDataValue.add("APPNAME", req.getRequest().getAppName(), Types.VARCHAR);
        columnDataValue.add("APPTOKEN", req.getRequest().getAppToken(), Types.VARCHAR);
        columnDataValue.add("APPSECRET", req.getRequest().getAppSecret(), Types.VARCHAR);
        columnDataValue.add("SKCAPPTOKEN", req.getRequest().getSkcAppToken(), Types.VARCHAR);
        columnDataValue.add("AUTHCODE", req.getRequest().getAuthCode(), Types.VARCHAR);
        columnDataValue.add("NNAAUTHCODE", req.getRequest().getNnaAuthCode(), Types.VARCHAR);
        columnDataValue.add("TRAN_TIME", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR);
        
        //ID：20251030009 [产品3.0]中台NNA娜娜入口登入-服务端  by jinzma 20251030
        columnDataValue.add("IAMURL", req.getRequest().getIamUrl(), Types.VARCHAR);
        columnDataValue.add("NNAURL", req.getRequest().getNnaUrl(), Types.VARCHAR);
        
        //ID：20251031020【产品3.0】AI应用信息配置-增加NNA访问地址-服务端 by jinzma 20251031
        columnDataValue.add("TENANTNAME", req.getRequest().getTenantName(), Types.VARCHAR);
        
        
        String[] columns = columnDataValue.getColumns().toArray(new String[0]);
        DataValue[] dataValue = columnDataValue.getDataValues().toArray(new DataValue[0]);
        
        
        InsBean insBean = new InsBean("DCP_AIPARAMETER", columns);
        insBean.addValues(dataValue);
        
        this.addProcessData(new DataProcessBean(insBean));
        
        
        this.doExecuteDataToDB();
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_AIParameterUpdateReq req) throws Exception {
        return Collections.emptyList();
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_AIParameterUpdateReq req) throws Exception {
        return Collections.emptyList();
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_AIParameterUpdateReq req) throws Exception {
        return Collections.emptyList();
    }
    
    @Override
    protected boolean isVerifyFail(DCP_AIParameterUpdateReq req) throws Exception {
        
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if (req.getRequest() == null) {
            errMsg.append("request 不可为空值 ");
            isFail = true;
        }else {
            
            if (Check.Null(req.getRequest().getTenantId())) {
                errMsg.append(" tenantId 不可为空值 ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getAppId())) {
                errMsg.append(" appId 不可为空值 ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getAppName())) {
                errMsg.append(" appName 不可为空值 ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getAppToken())) {
                errMsg.append(" appToken 不可为空值 ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getAppSecret())) {
                errMsg.append(" appSecret 不可为空值 ");
                isFail = true;
            }
            
            //ID：20251030009 [产品3.0]中台NNA娜娜入口登入-服务端  by jinzma 20251030
            if (Check.Null(req.getRequest().getIamUrl())) {
                errMsg.append(" iamUrl 不可为空值 ");
                isFail = true;
            }
            
            
            //智能体中心（kai-skc）
            if (!Check.Null(req.getRequest().getAuthCode())) {
                if (Check.Null(req.getRequest().getSkcAppToken())) {
                    errMsg.append(" skcAppToken 不可为空值 ");
                    isFail = true;
                }
                if (Check.Null(req.getRequest().getAuthCode())) {
                    errMsg.append(" authCode 不可为空值 ");
                    isFail = true;
                }
            }
            
            //娜娜入口(NNA)
            if (!Check.Null(req.getRequest().getNnaAuthCode())) {
                if (Check.Null(req.getRequest().getNnaAuthCode())) {
                    errMsg.append(" nnaAuthCode 不可为空值 ");
                    isFail = true;
                }
                
                if (Check.Null(req.getRequest().getNnaUrl())) {
                    errMsg.append(" nnaUrl 不可为空值 ");
                    isFail = true;
                }
            }
            
            
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_AIParameterUpdateReq> getRequestType() {
        return new TypeToken<DCP_AIParameterUpdateReq>(){};
    }
    
    @Override
    protected DCP_AIParameterUpdateRes getResponseType() {
        return new DCP_AIParameterUpdateRes();
    }
}

