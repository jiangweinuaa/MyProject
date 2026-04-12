package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_N_GoodsOnlineLogisticsInfoUpdateReq;
import com.dsc.spos.json.cust.req.DCP_N_GoodsOnlineLogisticsInfoUpdateReq.LogisticsInfo;
import com.dsc.spos.json.cust.req.DCP_N_GoodsOnlineLogisticsInfoUpdateReq.Plu;
import com.dsc.spos.json.cust.res.DCP_N_GoodsOnlineLogisticsInfoUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 服务函数：DCP_N_GoodsOnlineLogisticsInfoUpdate
 * 服务说明：N-商城商品修改物流与运费
 * @author jinzma
 * @since  2024-05-08
 */
public class DCP_N_GoodsOnlineLogisticsInfoUpdate extends SPosAdvanceService<DCP_N_GoodsOnlineLogisticsInfoUpdateReq, DCP_N_GoodsOnlineLogisticsInfoUpdateRes> {
    @Override
    protected void processDUID(DCP_N_GoodsOnlineLogisticsInfoUpdateReq req, DCP_N_GoodsOnlineLogisticsInfoUpdateRes res) throws Exception {

        try{
            String eId = req.geteId();
            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            LogisticsInfo logisticsInfo = req.getRequest().getLogisticsInfo();
            List<Plu> pluList = req.getRequest().getPluList();

            for (Plu plu:pluList){
                UptBean ub1 = new UptBean("DCP_GOODS_ONLINE");

                ub1.addUpdateValue("SHOPPICKUP",new DataValue(logisticsInfo.getShopPickUp(), Types.VARCHAR));
                ub1.addUpdateValue("CITYDELIVER",new DataValue(logisticsInfo.getCityDeliver(), Types.VARCHAR));
                ub1.addUpdateValue("EXPRESSDELIVER",new DataValue(logisticsInfo.getExpressDeliver(), Types.VARCHAR));
                ub1.addUpdateValue("FREIGHTFREE",new DataValue(logisticsInfo.getFreightFree(), Types.VARCHAR));
                ub1.addUpdateValue("FREIGHTTEMPLEID",new DataValue(logisticsInfo.getFreightTemplateId(), Types.VARCHAR));
                ub1.addUpdateValue("ISRESTAURANT",new DataValue(logisticsInfo.getIsRestaurant(), Types.VARCHAR));

                ub1.addUpdateValue("LASTMODIOPID",new DataValue(req.getOpNO(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPNAME",new DataValue(req.getOpName(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME",new DataValue(sDate, Types.DATE));

                ub1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("PLUNO", new DataValue(plu.getPluNo(), Types.VARCHAR));

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
    protected List<InsBean> prepareInsertData(DCP_N_GoodsOnlineLogisticsInfoUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_N_GoodsOnlineLogisticsInfoUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_N_GoodsOnlineLogisticsInfoUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_N_GoodsOnlineLogisticsInfoUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (req.getRequest() == null) {
            errMsg.append("request 不能为空 ");
            isFail = true;
        }else {
            LogisticsInfo logisticsInfo = req.getRequest().getLogisticsInfo();
            List<Plu> pluList = req.getRequest().getPluList();
            if (CollectionUtil.isEmpty(pluList)){
                errMsg.append("pluList 不能为空 ");
                isFail = true;
            }else {
                for (Plu plu:pluList) {
                    if (Check.Null(plu.getPluNo())){
                        errMsg.append("pluNo 不能为空 ");
                        isFail = true;
                    }

                    if (isFail) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }
            }

            if (logisticsInfo==null){
                errMsg.append("logisticsInfo 不能为空 ");
                isFail = true;
            }else {
                if (Check.Null(logisticsInfo.getCityDeliver())){
                    errMsg.append("cityDeliver 不能为空 ");
                    isFail = true;
                }
                if (Check.Null(logisticsInfo.getExpressDeliver())){
                    errMsg.append("expressDeliver 不能为空 ");
                    isFail = true;
                }
                if (Check.Null(logisticsInfo.getFreightFree())){
                    errMsg.append("freightFree 不能为空 ");
                    isFail = true;
                }
                /*if (Check.Null(logisticsInfo.getFreightTemplateId())){
                    errMsg.append("freightTemplateId 不能为空 ");
                    isFail = true;
                }*/
                if (Check.Null(logisticsInfo.getIsRestaurant())){
                    errMsg.append("isRestaurant 不能为空 ");
                    isFail = true;
                }
                if (Check.Null(logisticsInfo.getShopPickUp())){
                    errMsg.append("shopPickUp 不能为空 ");
                    isFail = true;
                }
            }

        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_N_GoodsOnlineLogisticsInfoUpdateReq> getRequestType() {
        return new TypeToken<DCP_N_GoodsOnlineLogisticsInfoUpdateReq>(){};
    }

    @Override
    protected DCP_N_GoodsOnlineLogisticsInfoUpdateRes getResponseType() {
        return new DCP_N_GoodsOnlineLogisticsInfoUpdateRes();
    }
}
