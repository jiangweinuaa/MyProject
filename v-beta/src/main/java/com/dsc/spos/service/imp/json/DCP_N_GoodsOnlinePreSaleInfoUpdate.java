package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_N_GoodsOnlinePreSaleInfoUpdateReq;
import com.dsc.spos.json.cust.req.DCP_N_GoodsOnlinePreSaleInfoUpdateReq.*;
import com.dsc.spos.json.cust.res.DCP_N_GoodsOnlinePreSaleInfoUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 服务函数：DCP_N_GoodsOnlinePreSaleInfoUpdate
 * 服务说明：N-商城商品修改预订与发货  (Yapi没有规格)
 * @author jinzma
 * @since  2024-04-18
 */
public class DCP_N_GoodsOnlinePreSaleInfoUpdate extends SPosAdvanceService<DCP_N_GoodsOnlinePreSaleInfoUpdateReq, DCP_N_GoodsOnlinePreSaleInfoUpdateRes> {
    @Override
    protected void processDUID(DCP_N_GoodsOnlinePreSaleInfoUpdateReq req, DCP_N_GoodsOnlinePreSaleInfoUpdateRes res) throws Exception {

        try {
            String lastmoditime = null;//req.getRequset().getLastmoditime();
            lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            String curLangType = req.getLangType();
            if (curLangType == null || curLangType.isEmpty()) {
                curLangType = "zh_CN";
            }

            String preSale = req.getRequest().getPreSaleInfo().getPreSale();//是否预订，需提前预订0-否1-是
            String deliveryDateType = req.getRequest().getPreSaleInfo().getDeliveryDateType();//发货时机类型1：付款成功后发货2：指定日期发货
            String deliveryDateType2 = req.getRequest().getPreSaleInfo().getDeliveryDateType2();//发货时间类型1：小时 2：天
            String deliveryDateValue = req.getRequest().getPreSaleInfo().getDeliveryDateValue();//付款后%S天/小时后发货，发货时机类型为1时必须传入
            String deliveryDate = req.getRequest().getPreSaleInfo().getDeliveryDate();//预计发货日期，发货时机类型为2时必须传入，YYYY-MM-DD
            String deliveryStartDate = req.getRequest().getPreSaleInfo().getDeliveryStartDate(); // 预计发货开始日期  YYYY-MM-DD
            String deliveryEndDate = req.getRequest().getPreSaleInfo().getDeliveryEndDate();     // 预计发货截止日期  YYYY-MM-DD



            String classType = "ONLINE";

            List<level1Plu> pluList = req.getRequest().getPluList();

            for (level1Plu par : pluList) {

                UptBean ub1 = new UptBean("DCP_GOODS_ONLINE");
                ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                ub1.addCondition("PLUNO", new DataValue(par.getPluNo(), Types.VARCHAR));


                ub1.addUpdateValue("PRESALE", new DataValue(preSale, Types.VARCHAR));
                ub1.addUpdateValue("DELIVERYDATETYPE", new DataValue(deliveryDateType, Types.VARCHAR));
                ub1.addUpdateValue("DELIVERYDATETYPE2", new DataValue(deliveryDateType2, Types.VARCHAR));

                if (deliveryDateValue != null && !deliveryDateValue.isEmpty()) {
                    ub1.addUpdateValue("DELIVERYDATEVALUE", new DataValue(deliveryDateValue, Types.VARCHAR));
                }
                if (deliveryDate != null && !deliveryDate.isEmpty()) {
                    ub1.addUpdateValue("DELIVERYDATE", new DataValue(deliveryDate, Types.DATE));
                }
                if (deliveryStartDate != null && !deliveryStartDate.isEmpty()) {
                    ub1.addUpdateValue("DELIVERYSTARTDATE", new DataValue(deliveryStartDate, Types.DATE));
                }
                if (deliveryEndDate != null && !deliveryEndDate.isEmpty()) {
                    ub1.addUpdateValue("DELIVERYENDDATE", new DataValue(deliveryEndDate, Types.DATE));
                }


                ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

                this.addProcessData(new DataProcessBean(ub1));


            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_N_GoodsOnlinePreSaleInfoUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_N_GoodsOnlinePreSaleInfoUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_N_GoodsOnlinePreSaleInfoUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_N_GoodsOnlinePreSaleInfoUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_N_GoodsOnlinePreSaleInfoUpdateReq> getRequestType() {
        return new TypeToken<DCP_N_GoodsOnlinePreSaleInfoUpdateReq>(){};
    }

    @Override
    protected DCP_N_GoodsOnlinePreSaleInfoUpdateRes getResponseType() {
        return new DCP_N_GoodsOnlinePreSaleInfoUpdateRes();
    }
}
