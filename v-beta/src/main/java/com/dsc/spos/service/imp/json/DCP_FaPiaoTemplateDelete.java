package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_FaPiaoTemplateDeleteReq;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_FaPiaoTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

/**
 * 服务函数：DCP_FaPiaoTemplateDelete
 * 服务说明：发票模板删除
 *
 * @author wangzyc
 * @since 2021-2-03
 */
public class DCP_FaPiaoTemplateDelete extends SPosAdvanceService<DCP_FaPiaoTemplateDeleteReq, DCP_FaPiaoTemplateDeleteRes> {
    @Override
    protected void processDUID(DCP_FaPiaoTemplateDeleteReq req, DCP_FaPiaoTemplateDeleteRes res) throws Exception {
        DCP_FaPiaoTemplateDeleteReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String templateId = request.getTemplateId(); // 模板编号

        try {
            // 1.delete DCP_FAPIAO_TEMPLATE 发票模板
            DelBean db1 = new DelBean("DCP_FAPIAO_TEMPLATE");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            // 2.delete DCP_FAPIAO_TEMPLATE_SHOP 发票模板适用门店
            DelBean db2 = new DelBean("DCP_FAPIAO_TEMPLATE_SHOP");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            this.doExecuteDataToDB();
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常: "+e.getMessage());
        }
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！: ");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FaPiaoTemplateDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FaPiaoTemplateDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FaPiaoTemplateDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FaPiaoTemplateDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_FaPiaoTemplateDeleteReq.level1Elm request = req.getRequest();

        if (Check.Null(request.getTemplateId())) {
            errMsg.append("模板编码不能为空值 ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_FaPiaoTemplateDeleteReq> getRequestType() {
        return new TypeToken<DCP_FaPiaoTemplateDeleteReq>() {
        };
    }

    @Override
    protected DCP_FaPiaoTemplateDeleteRes getResponseType() {
        return new DCP_FaPiaoTemplateDeleteRes();
    }
}
