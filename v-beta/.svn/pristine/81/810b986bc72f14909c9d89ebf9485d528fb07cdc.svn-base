package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TaxGroupDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TaxGroupDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;

public class DCP_TaxGroupDelete extends SPosAdvanceService<DCP_TaxGroupDeleteReq, DCP_TaxGroupDeleteRes> {

    @Override
    protected void processDUID(DCP_TaxGroupDeleteReq req, DCP_TaxGroupDeleteRes res) throws Exception {

        try {
            String querySql = " SELECT * FROM DCP_TAXGROUP WHERE EID='"+req.geteId()+"' AND TAXGROUPNO='"+req.getRequest().getTaxGroupNo() + "'";
            List<Map<String,Object>> data = doQueryData(querySql,null);
            if (null != data && !data.isEmpty()) {

                if ("-1".equals(data.get(0).get("STATUS").toString())){
                    ColumnDataValue condition = new ColumnDataValue();
                    condition.add("EID", DataValues.newString(req.geteId()));
                    condition.add("TAXGROUPNO",DataValues.newString(req.getRequest().getTaxGroupNo()));

                    addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_TAXGROUP",condition)));
                    addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_TAXGROUP_DETAIL",condition)));

                    this.doExecuteDataToDB();
                }else {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前状态不是'-1:未启用'，不可删除");
                }
            }

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TaxGroupDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TaxGroupDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TaxGroupDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TaxGroupDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_TaxGroupDeleteReq> getRequestType() {
        return new TypeToken<DCP_TaxGroupDeleteReq>(){

        };
    }

    @Override
    protected DCP_TaxGroupDeleteRes getResponseType() {
        return new DCP_TaxGroupDeleteRes();
    }
}
