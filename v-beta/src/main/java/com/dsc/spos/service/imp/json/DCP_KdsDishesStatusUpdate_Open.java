package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_KdsDishesStatusUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_KdsDishesStatusUpdate_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.util.List;

/**
 * @description: 菜品状态修改
 * @author: wangzyc
 * @create: 2021-09-18
 */
public class DCP_KdsDishesStatusUpdate_Open extends SPosAdvanceService<DCP_KdsDishesStatusUpdate_OpenReq, DCP_KdsDishesStatusUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_KdsDishesStatusUpdate_OpenReq req, DCP_KdsDishesStatusUpdate_OpenRes res) throws Exception {
        DCP_KdsDishesStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String oprType = request.getOprType(); // 若oprType=1，操作变更DCP_PRODUCT_DETAIL.ISURGE=Y
        String billNo = request.getBillNo();
        String machineId = request.getMachineId();
        String shopId = request.getShopId();
        List<DCP_KdsDishesStatusUpdate_OpenReq.level2Elm> goodsList = request.getGoodsList();
        try {
            if("1".equals(oprType))
            {
               if(!CollectionUtils.isEmpty(goodsList)){
                   for (DCP_KdsDishesStatusUpdate_OpenReq.level2Elm goods : goodsList) {
                       UptBean ub1 = null;
                       ub1 = new UptBean("DCP_PRODUCT_DETAIL");
                       //add Value
                       ub1.addUpdateValue("ISURGE", new DataValue("Y", Types.VARCHAR));
                       //condition
                       ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                       ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                       ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                       ub1.addCondition("OITEM", new DataValue(goods.getOItem(), Types.VARCHAR));
                       ub1.addCondition("PLUNO", new DataValue(goods.getPluNo(), Types.VARCHAR));
                       this.addProcessData(new DataProcessBean(ub1));

                       UptBean ub2 = null;
                       ub2 = new UptBean("DCP_PROCESSTASK_DETAIL");
                       //add Value
                       ub2.addUpdateValue("ISURGE", new DataValue("Y", Types.VARCHAR));
                       //condition
                       ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                       ub2.addCondition("OFNO", new DataValue(billNo, Types.VARCHAR));
                       ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                       ub2.addCondition("OITEM", new DataValue(goods.getOItem(), Types.VARCHAR));
                       ub2.addCondition("PLUNO", new DataValue(goods.getPluNo(), Types.VARCHAR));
                       this.addProcessData(new DataProcessBean(ub2));
                   }
               }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_KdsDishesStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_KdsDishesStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_KdsDishesStatusUpdate_OpenReq req) throws Exception {
        return null;
    }
    @Override
    protected boolean isVerifyFail(DCP_KdsDishesStatusUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_KdsDishesStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("机台编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getOprType())) {
            errMsg.append("操作类型不能为空,");
            isFail = true;
        }
        if (Check.Null(request.getBillNo())) {
            errMsg.append("单号不能为空,");
            isFail = true;
        }

        if (CollectionUtils.isEmpty(request.getGoodsList())) {
            errMsg.append("商品不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KdsDishesStatusUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_KdsDishesStatusUpdate_OpenReq>(){};
    }

    @Override
    protected DCP_KdsDishesStatusUpdate_OpenRes getResponseType() {
        return new DCP_KdsDishesStatusUpdate_OpenRes();
    }

}
