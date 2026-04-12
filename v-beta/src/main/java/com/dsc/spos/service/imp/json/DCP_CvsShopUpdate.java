package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CvsShopUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CvsShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_CvsShopUpdate extends SPosAdvanceService<DCP_CvsShopUpdateReq, DCP_CvsShopUpdateRes> {

    @Override
    protected boolean isVerifyFail(DCP_CvsShopUpdateReq req) throws Exception {
        StringBuffer errMsg = new StringBuffer();

        if (Check.Null(req.getRequest().getOrgNo()))
            errMsg.append("組織代號不可为空值, ");

        if (Check.Null(req.getRequest().getFamiShopId()))
            errMsg.append("全家寄件店不可为空值, ");

        if (Check.Null(req.getRequest().getHiLifeShopId()))
            errMsg.append("萊爾富寄件店不可为空值, ");

        if (Check.Null(req.getRequest().getUniMartShopId()))
            errMsg.append("711寄件店不可为空值, ");

        if (Check.Null(req.getRequest().getOkShopId()))
            errMsg.append("OK寄件店不可为空值, ");

        if (!errMsg.toString().isEmpty())
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());

        return false;
    }


    @Override
    protected void processDUID(DCP_CvsShopUpdateReq req, DCP_CvsShopUpdateRes res) throws Exception {
        if(isExistOrgNo(req.getRequest().getOrgNo()))
            updateDCP_ORG_CVS(req);
        else
            createDCP_ORG_CVS(req);

        this.doExecuteDataToDB();


        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    private boolean isExistOrgNo(String orgNo) throws Exception {
        String sql =
                "SELECT\n" +
                "    ORGANIZATIONNO \"orgNo\"\n" +
                "FROM DCP_ORG_CVS\n" +
                "WHERE ORGANIZATIONNO = ':OrgNo'";

        sql = sql.replace(":OrgNo", orgNo);

        List<Map<String, Object>> searchResult = this.doQueryData(sql, null);

        return searchResult.size() > 0;
    }

    private void createDCP_ORG_CVS(DCP_CvsShopUpdateReq req) {
        String[] columns ={
                "EID","ORGANIZATIONNO","FAMISHOPID","HILIFESHOPID","UNIMARTSHOPID","OKSHOPID"
        };
        DataValue[] dataValues = new DataValue[]
                {
                        new DataValue(req.geteId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getOrgNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getFamiShopId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getHiLifeShopId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getUniMartShopId() ,Types.VARCHAR),
                        new DataValue(req.getRequest().getOkShopId(), Types.VARCHAR),
                };

        InsBean insBean = new InsBean("DCP_ORG_CVS", columns);
        insBean.addValues(dataValues);
        this.addProcessData(new DataProcessBean(insBean));
    }

    private void updateDCP_ORG_CVS(DCP_CvsShopUpdateReq req) {
        UptBean uptBean = new UptBean("DCP_ORG_CVS");
        uptBean.addUpdateValue("FAMISHOPID", new DataValue(req.getRequest().getFamiShopId(), Types.VARCHAR));
        uptBean.addUpdateValue("HILIFESHOPID", new DataValue(req.getRequest().getHiLifeShopId(), Types.VARCHAR));
        uptBean.addUpdateValue("UNIMARTSHOPID", new DataValue(req.getRequest().getUniMartShopId(), Types.VARCHAR));
        uptBean.addUpdateValue("OKSHOPID", new DataValue(req.getRequest().getOkShopId(), Types.VARCHAR));

        uptBean.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        uptBean.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrgNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(uptBean));
    }

    @Override
    protected TypeToken<DCP_CvsShopUpdateReq> getRequestType() {
        return new TypeToken<DCP_CvsShopUpdateReq>(){};
    }

    @Override
    protected DCP_CvsShopUpdateRes getResponseType() {
        return new DCP_CvsShopUpdateRes();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CvsShopUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CvsShopUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CvsShopUpdateReq req) throws Exception {
        return null;
    }
}
