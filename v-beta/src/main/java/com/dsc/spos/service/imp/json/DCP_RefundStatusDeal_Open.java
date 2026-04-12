package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_RefundStatusDeal_OpenReq;
import com.dsc.spos.json.cust.res.DCP_RefundStatusDeal_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * @description: KDS退单商品待确认处理
 * @author: wangzyc
 * @create: 2021-09-18
 */
public class DCP_RefundStatusDeal_Open extends SPosAdvanceService<DCP_RefundStatusDeal_OpenReq, DCP_RefundStatusDeal_OpenRes> {
    @Override
    protected void processDUID(DCP_RefundStatusDeal_OpenReq req, DCP_RefundStatusDeal_OpenRes res) throws Exception {
        String eId = req.geteId();
        DCP_RefundStatusDeal_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String terminalType = request.getTerminalType();
        // 根据设备模式区分 0，1 同步加工任务表及临时表 ，2模式下 只变更临时表
        try
        {
            String sql = this.getOrderDetail(req);
            List<Map<String, Object>> orderDetails = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(orderDetails))
            {
                for (Map<String, Object> order : orderDetails)
                {
                    String billno = order.get("BILLNO").toString();

                    UptBean ub = null;
                    ub = new UptBean("DCP_PROCESSTASK");
                    ub.addUpdateValue("ISREFUND", new DataValue("Y", Types.VARCHAR));
                    // condition
                    ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub.addCondition("OFNO", new DataValue(billno, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub));

                    UptBean ub2 = null;
                    ub2 = new UptBean("DCP_PRODUCT_SALE");
                    ub2.addUpdateValue("ISREFUND", new DataValue("Y", Types.VARCHAR));
                    // condition
                    ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub2.addCondition("BILLNO", new DataValue(billno, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub2));
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
    protected List<InsBean> prepareInsertData(DCP_RefundStatusDeal_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_RefundStatusDeal_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_RefundStatusDeal_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_RefundStatusDeal_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_RefundStatusDeal_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (CollectionUtils.isEmpty(request.getOrderList())) {
            errMsg.append("订单列表不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_RefundStatusDeal_OpenReq> getRequestType() {
        return new TypeToken<DCP_RefundStatusDeal_OpenReq>(){};
    }

    @Override
    protected DCP_RefundStatusDeal_OpenRes getResponseType() {
        return new DCP_RefundStatusDeal_OpenRes();
    }

    /**
     * 查询 符合要求的订单
     * @param req
     * @return
     */
    private String getOrderDetail(DCP_RefundStatusDeal_OpenReq req){
        DCP_RefundStatusDeal_OpenReq.level1Elm request = req.getRequest();
        List<String> orderList = request.getOrderList();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * from DCP_PRODUCT_SALE where eid = '"+req.geteId()+"' and shopId = '"+request.getShopId()+"' ");
        if(!CollectionUtils.isEmpty(orderList)){
            sqlbuf.append(" AND BILLNO IN (");
            for (String orderNo : orderList) {
                sqlbuf.append("'"+orderNo+"',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length()-1);
            sqlbuf.append(" )");
        }
        sqlbuf.append(" AND (ISREFUND ='N' OR ISREFUND IS null)");
        sql = sqlbuf.toString();
        return sql;
    }
}
