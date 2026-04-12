package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_InvoiceOnLineQueryReq;
import com.dsc.spos.json.cust.res.DCP_InvoiceOnLineQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.ConvertUtils;
import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_InvoiceOnLineQuery extends SPosBasicService<DCP_InvoiceOnLineQueryReq, DCP_InvoiceOnLineQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_InvoiceOnLineQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected DCP_InvoiceOnLineQueryRes processJson(DCP_InvoiceOnLineQueryReq req) throws Exception {
        DCP_InvoiceOnLineQueryRes.Level1Elm data = getData(req);
        List<DCP_InvoiceOnLineQueryRes.Channel> channel = getChannel(data.getTemplateId(), req);
        List<DCP_InvoiceOnLineQueryRes.App> app = getApp(data.getTemplateId(), req);
        data.setChannelList(channel);
        data.setAppList(app);

        DCP_InvoiceOnLineQueryRes response = getResponse();
        response.setDatas(data);
        return response;
    }

    private DCP_InvoiceOnLineQueryRes.Level1Elm getData(DCP_InvoiceOnLineQueryReq req) throws Exception {
        String sql =
                "SELECT\n" +
                "TEMPLATEID,\n" +
                "TEMPLATENAME,\n" +
                "ENABLEINVOICE,\n" +
                "INVDISTRICT,\n" +
                "MEMBERCARRIER,\n" +
                "RESTRICTCHANNEL \n" +
                "FROM DCP_INVOICESET\n" +
                "WHERE EID = " + req.geteId();

        List<Map<String, Object>> data = this.doQueryData(sql, null);

        return data.size() > 0?
                ConvertUtils.convertValue(data.get(0), DCP_InvoiceOnLineQueryRes.Level1Elm.class):
                new DCP_InvoiceOnLineQueryRes.Level1Elm();
    }

    private List<DCP_InvoiceOnLineQueryRes.Channel> getChannel(String templateId, DCP_InvoiceOnLineQueryReq req) throws Exception {
        if(templateId == null || templateId.isEmpty())
            return new ArrayList<>();

        String sql =
                "SELECT\n" +
                "       DCP_INVOICESET_CHANNEL.CHANNELID,\n" +
                "       CRM_CHANNEL.CHANNELNAME\n" +
                "FROM DCP_INVOICESET_CHANNEL\n" +
                "LEFT JOIN CRM_CHANNEL ON DCP_INVOICESET_CHANNEL.EID = CRM_CHANNEL.EID AND DCP_INVOICESET_CHANNEL.CHANNELID = CRM_CHANNEL.CHANNELID\n" +
                "WHERE DCP_INVOICESET_CHANNEL.EID = ':EID'\n" +
                "AND DCP_INVOICESET_CHANNEL.TEMPLATEID = ':TEMPLATEID'";

        sql = sql.replace(":EID", req.geteId());
        sql = sql.replace(":TEMPLATEID", templateId);

        List<Map<String, Object>> queryDataResult = this.doQueryData(sql, null);

        return queryDataResult.size() > 0?
                ConvertUtils.convertValue(queryDataResult, DCP_InvoiceOnLineQueryRes.Channel.class):
                new ArrayList<DCP_InvoiceOnLineQueryRes.Channel>();
    }

    private List<DCP_InvoiceOnLineQueryRes.App> getApp(String templateId, DCP_InvoiceOnLineQueryReq req) throws Exception{
        if(templateId == null || templateId.isEmpty())
            return new ArrayList<>();

        String sql =
                "SELECT\n" +
                "       DCP_INVOICESET_APP.APPNO,\n" +
                "       PLATFORM_APP.APPNAME\n" +
                "FROM DCP_INVOICESET_APP\n" +
                "LEFT JOIN PLATFORM_APP ON DCP_INVOICESET_APP.APPNO = PLATFORM_APP.APPNO\n" +
                "WHERE DCP_INVOICESET_APP.EID = ':EID'\n" +
                "AND DCP_INVOICESET_APP.TEMPLATEID = ':TEMPLATEID'";

        sql = sql.replace(":EID", req.geteId());
        sql = sql.replace(":TEMPLATEID", templateId);

        List<Map<String, Object>> queryDataResult = this.doQueryData(sql, null);

        return queryDataResult.size() > 0?
                ConvertUtils.convertValue(queryDataResult, DCP_InvoiceOnLineQueryRes.App.class):
                new ArrayList<DCP_InvoiceOnLineQueryRes.App>();
    }

    @Override
    protected TypeToken<DCP_InvoiceOnLineQueryReq> getRequestType() {
        return new TypeToken<DCP_InvoiceOnLineQueryReq>(){};
    }

    @Override
    protected DCP_InvoiceOnLineQueryRes getResponseType() {
        return new DCP_InvoiceOnLineQueryRes();
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_InvoiceOnLineQueryReq req) throws Exception {
        return null;
    }
}
