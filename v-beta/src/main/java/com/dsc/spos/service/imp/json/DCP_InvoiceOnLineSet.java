package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InvoiceOnLineSetReq;
import com.dsc.spos.json.cust.res.DCP_InvoiceOnLineSetRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_InvoiceOnLineSet extends SPosAdvanceService<DCP_InvoiceOnLineSetReq, DCP_InvoiceOnLineSetRes> {

    @Override
    protected boolean isVerifyFail(DCP_InvoiceOnLineSetReq req) throws Exception {
        StringBuffer errMsg = new StringBuffer();

        if (req.getRequest() == null)
            errMsg.append("Request不可为空值, ");

        if (req.getRequest().getMemberCarrier() == null)
            errMsg.append("会员载具不可为空值, ");

        if (Check.Null(req.getRequest().getRestrictChannel()))
            errMsg.append("限定渠道不可为空值, ");

        if (req.getRequest().getChannelList() == null)
            errMsg.append("渠道列表不可为空值, ");

        if (req.getRequest().getAppList() == null)
            errMsg.append("适用应用不可为空值, ");

        if (!errMsg.toString().isEmpty())
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());

        return false;
    }


    @Override
    protected void processDUID(DCP_InvoiceOnLineSetReq req, DCP_InvoiceOnLineSetRes res) throws Exception {
        updateDCP_INVOICESET(req);
        updateDCP_INVOICESET_CHANNEL(req);
        updateDCP_INVOICESET_APP(req);

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    private void updateDCP_INVOICESET(DCP_InvoiceOnLineSetReq req) throws Exception {
//      刪除目前資料
        DelBean delBean = new DelBean("DCP_INVOICESET");
        delBean.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(delBean));

//      新增資料
        String[] columns = {
                "EID","TEMPLATEID","TEMPLATENAME","ENABLEINVOICE","INVDISTRICT",
                "MEMBERCARRIER","RESTRICTCHANNEL","LASTMODIOPID","LASTMODIOPNAME",
                "LASTMODITIME"
        };

        DataValue[] dataValues = new DataValue[]
                {
                        new DataValue(req.geteId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getTemplateId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getTemplateName(), Types.VARCHAR),
                        new DataValue(req.getRequest().getEnableInvoice(), Types.VARCHAR),
                        new DataValue(req.getRequest().getInvDistrict() ,Types.VARCHAR),
                        new DataValue(req.getRequest().getMemberCarrier(), Types.INTEGER),
                        new DataValue(req.getRequest().getRestrictChannel(), Types.VARCHAR),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(req.getOpName(), Types.VARCHAR),
                        new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.VARCHAR),
                };

        InsBean insBean = new InsBean("DCP_INVOICESET", columns);
        insBean.addValues(dataValues);
        this.addProcessData(new DataProcessBean(insBean));
    }

    private void updateDCP_INVOICESET_CHANNEL(DCP_InvoiceOnLineSetReq req) {
//      刪除目前資料
        DelBean delBean = new DelBean("DCP_INVOICESET_CHANNEL");
        delBean.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(delBean));

//      新增資料
        String[] columns = {
                "EID","TEMPLATEID","CHANNELID"
        };

        for(DCP_InvoiceOnLineSetReq.Channel channel : req.getRequest().getChannelList()){
            DataValue[] dataValues = new DataValue[]
                    {
                            new DataValue(req.geteId(), Types.VARCHAR),
                            new DataValue(req.getRequest().getTemplateId(), Types.VARCHAR),
                            new DataValue(channel.getChannelId(), Types.VARCHAR),
                    };

            InsBean insBean = new InsBean("DCP_INVOICESET_CHANNEL", columns);
            insBean.addValues(dataValues);
            this.addProcessData(new DataProcessBean(insBean));
        }
    }

    private void updateDCP_INVOICESET_APP(DCP_InvoiceOnLineSetReq req) {
//      刪除目前資料
        DelBean delBean = new DelBean("DCP_INVOICESET_APP ");
        delBean.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(delBean));

        String[] columns = {
                "EID","TEMPLATEID","APPNO"
        };
        for(DCP_InvoiceOnLineSetReq.App app : req.getRequest().getAppList()) {
            DataValue[] dataValues = new DataValue[]
                    {
                            new DataValue(req.geteId(), Types.VARCHAR),
                            new DataValue(req.getRequest().getTemplateId(), Types.VARCHAR),
                            new DataValue(app.getAppNo(), Types.VARCHAR),
                    };

            InsBean insBean = new InsBean("DCP_INVOICESET_APP", columns);
            insBean.addValues(dataValues);
            this.addProcessData(new DataProcessBean(insBean));
        }
    }



    @Override
    protected TypeToken<DCP_InvoiceOnLineSetReq> getRequestType() {
        return new TypeToken<DCP_InvoiceOnLineSetReq>(){};
    }

    @Override
    protected DCP_InvoiceOnLineSetRes getResponseType() {
        return new DCP_InvoiceOnLineSetRes();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InvoiceOnLineSetReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InvoiceOnLineSetReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InvoiceOnLineSetReq req) throws Exception {
        return null;
    }
}
