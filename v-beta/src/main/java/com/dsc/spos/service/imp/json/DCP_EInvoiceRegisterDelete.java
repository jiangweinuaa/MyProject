package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_EInvoiceRegisterDeleteReq;
import com.dsc.spos.json.cust.res.DCP_EInvoiceRegisterDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * @description: 开票登记删除
 * @author: wangzyc
 * @create: 2022-03-15
 */
public class DCP_EInvoiceRegisterDelete extends SPosAdvanceService<DCP_EInvoiceRegisterDeleteReq, DCP_EInvoiceRegisterDeleteRes> {

    @Override
    protected void processDUID(DCP_EInvoiceRegisterDeleteReq req, DCP_EInvoiceRegisterDeleteRes res) throws Exception {
        String invoiceBillNo = req.getRequest().getInvoiceBillNo();
        String eId = req.geteId();

        // 先查询下是否手工开票
        String sql = "select ISMANUAL from DCP_EINVOICE where eid = ? and INVOICEBILLNO =  ?";
        List<Map<String, Object>> maps = this.doQueryData(sql, new String[]{eId, invoiceBillNo});
        if(!CollectionUtils.isEmpty(maps)){
            String ismanual = maps.get(0).get("ISMANUAL").toString();
            if("Y".equals(ismanual)){
                DelBean db1 = new DelBean("DCP_EINVOICE");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("INVOICEBILLNO", new DataValue(invoiceBillNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                DelBean db2 = new DelBean("DCP_EINVOICE_BUSINESS");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("INVOICEBILLNO", new DataValue(invoiceBillNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                DelBean db3 = new DelBean("DCP_EINVOICE_DETAIL");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("INVOICEBILLNO", new DataValue(invoiceBillNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));

                this.doExecuteDataToDB();
            }else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "非手工开票的单据，暂不可删除！");
            }
        }else{
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在，请检查后重试！");
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_EInvoiceRegisterDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_EInvoiceRegisterDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_EInvoiceRegisterDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_EInvoiceRegisterDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_EInvoiceRegisterDeleteReq.level1Elm request = req.getRequest();

        if (Check.Null(request.getInvoiceBillNo())) {
            errMsg.append("开票单号不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_EInvoiceRegisterDeleteReq> getRequestType() {
        return new TypeToken<DCP_EInvoiceRegisterDeleteReq>(){};
    }

    @Override
    protected DCP_EInvoiceRegisterDeleteRes getResponseType() {
        return new DCP_EInvoiceRegisterDeleteRes();
    }
}
