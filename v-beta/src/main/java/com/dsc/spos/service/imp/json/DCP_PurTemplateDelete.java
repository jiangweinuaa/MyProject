package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PurTemplateDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PurTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_PurTemplateDelete extends SPosAdvanceService<DCP_PurTemplateDeleteReq, DCP_PurTemplateDeleteRes> {

    @Override
    protected boolean isVerifyFail(DCP_PurTemplateDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        //必传值不为空
        String no = req.getRequest().getPurTemplateNo();
        String eid = req.geteId();


        if(Check.Null(no)){
            errMsg.append("模板编号不能为空值 ");
            isFail = true;
        }


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    private boolean CheckTemplate(String eid, String no) throws Exception {
        String sql="select * from DCP_PURCHASETEMPLATE a" +
                 " where a.PURTEMPLATENO='"+no+"' and a.status='-1' and a.eid='"+eid+"' ";
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);

        return getQData.size()>0;
    }

    /**
     * 查询多语言信息
     */
    @Override
    protected String getQuerySql(DCP_PurTemplateDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    protected void processDUID(DCP_PurTemplateDeleteReq req, DCP_PurTemplateDeleteRes res) throws Exception {
        // TODO Auto-generated method stub
        String sql = "";
        try
        {
            String no = req.getRequest().getPurTemplateNo();
            String eId = req.geteId();

            if(!CheckTemplate(eId,no)){
                res.setSuccess(false);
                res.setServiceStatus("000");
                res.setServiceDescription("非【未启用】状态模板数据不可删除！");
                return;
            }


            DelBean db1 = new DelBean("DCP_PURCHASETEMPLATE");
            db1.addCondition("PURTEMPLATENO", new DataValue(no, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("DCP_PURCHASETEMPLATE_LANG");
            db2.addCondition("PURTEMPLATENO", new DataValue(no, Types.VARCHAR));
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            DelBean db3 = new DelBean("DCP_PURCHASETEMPLATE_GOODS");
            db3.addCondition("PURTEMPLATENO", new DataValue(no, Types.VARCHAR));
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));

            DelBean db4 = new DelBean("DCP_PURCHASETEMPLATE_PRICE");
            db4.addCondition("PURTEMPLATENO", new DataValue(no, Types.VARCHAR));
            db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db4));

            DelBean db5 = new DelBean("DCP_PURCHASETEMPLATE_ORG");
            db5.addCondition("PURTEMPLATENO", new DataValue(no, Types.VARCHAR));
            db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db5));


            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        catch (Exception e)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurTemplateDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurTemplateDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurTemplateDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    protected TypeToken<DCP_PurTemplateDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_PurTemplateDeleteReq>(){};
    }
    @Override
    protected DCP_PurTemplateDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_PurTemplateDeleteRes();
    }

}
