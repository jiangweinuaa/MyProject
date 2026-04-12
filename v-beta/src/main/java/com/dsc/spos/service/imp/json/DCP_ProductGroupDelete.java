package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProductGroupDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ProductGroupDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_ProductGroupDelete extends SPosAdvanceService<DCP_ProductGroupDeleteReq, DCP_ProductGroupDeleteRes> {

    @Override
    protected void processDUID(DCP_ProductGroupDeleteReq req, DCP_ProductGroupDeleteRes res) throws Exception {

        String eId = req.geteId();
        List<DCP_ProductGroupDeleteReq.Datas> datas = req.getRequest().getDatas();
        for (DCP_ProductGroupDeleteReq.Datas data : datas){
            String pGroupNo = data.getPGroupNo();

            String validSql="select * from MES_PRODUCT_GROUP a where a.eid='"+eId+"'" +
                    " and a.pgroupno='"+pGroupNo+"' and a.status='-1'";
            List<Map<String, Object>> validData = this.doQueryData(validSql, null);
            if(CollUtil.isEmpty(validData)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "生产班组"+pGroupNo+"不可删除!");
            }

            //TEMPLATEID
            DelBean db1 = new DelBean("MES_PRODUCT_GROUP");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("PGROUPNO", new DataValue(pGroupNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("MES_PRODUCT_GROUP_GOODS");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("PGROUPNO", new DataValue(pGroupNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProductGroupDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProductGroupDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProductGroupDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProductGroupDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ProductGroupDeleteReq> getRequestType() {
        return new TypeToken<DCP_ProductGroupDeleteReq>() {
        };
    }

    @Override
    protected DCP_ProductGroupDeleteRes getResponseType() {
        return new DCP_ProductGroupDeleteRes();
    }
}

