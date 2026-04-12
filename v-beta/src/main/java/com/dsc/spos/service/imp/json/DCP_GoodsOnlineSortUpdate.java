package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineSortUpdateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsOnlineSortUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

/**
 * @description: 商城商品序号修改
 * @author: wangzyc
 * @create: 2021-06-29
 */
public class DCP_GoodsOnlineSortUpdate extends SPosAdvanceService<DCP_GoodsOnlineSortUpdateReq, DCP_GoodsOnlineSortUpdateRes> {
    @Override
    protected void processDUID(DCP_GoodsOnlineSortUpdateReq req, DCP_GoodsOnlineSortUpdateRes res) throws Exception {
        try {
            DCP_GoodsOnlineSortUpdateReq.level1Elm request = req.getRequest();
            UptBean ub1 = new UptBean("DCP_GOODS_ONLINE");
            ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
            ub1.addCondition("PLUNO", new DataValue(request.getPluNo(), Types.VARCHAR));

            ub1.addUpdateValue("SORTID",new DataValue(request.getSortId(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:"+e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_GoodsOnlineSortUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_GoodsOnlineSortUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_GoodsOnlineSortUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_GoodsOnlineSortUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_GoodsOnlineSortUpdateReq.level1Elm request = req.getRequest();

        if (request == null)
        {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if(Check.Null(request.getPluNo())){
            errMsg.append("商品编码不能为空 ,");
            isFail = true;
        }

        if(Check.Null(request.getSortId())){
            errMsg.append("显示序号不能为空 ,");
            isFail = true;
        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_GoodsOnlineSortUpdateReq> getRequestType() {
        return new TypeToken<DCP_GoodsOnlineSortUpdateReq>(){};
    }

    @Override
    protected DCP_GoodsOnlineSortUpdateRes getResponseType() {
        return new DCP_GoodsOnlineSortUpdateRes();
    }
}
