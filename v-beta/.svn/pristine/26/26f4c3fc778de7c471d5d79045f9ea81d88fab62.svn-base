package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DishControlUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_DishControlUpdate_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: KDS菜品流程控制修改
 * @author: wangzyc
 * @create: 2021-09-13
 */
public class DCP_DishControlUpdate_Open extends SPosAdvanceService<DCP_DishControlUpdate_OpenReq, DCP_DishControlUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_DishControlUpdate_OpenReq req, DCP_DishControlUpdate_OpenRes res) throws Exception {
        DCP_DishControlUpdate_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        List<DCP_DishControlUpdate_OpenReq.level2Elm> goodsList = request.getGoodsList();
        String eId = req.geteId();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            String lastmodiopId = req.getApiUser().getUserCode();
            String lastmodiopName = req.getOpName();

            String [] columns = {"EID","SHOPID","CATEGORY","PLUNO","UNSIDE","UNCOOK","UNCALL","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};

            if(!CollectionUtils.isEmpty(goodsList)){
                for (DCP_DishControlUpdate_OpenReq.level2Elm lv2 : goodsList) {

                    // DCP_KDSDISHES_CONTROL
                    DelBean db1 = new DelBean("DCP_KDSDISHES_CONTROL");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("CATEGORY", new DataValue(lv2.getCategoryId(), Types.VARCHAR));
                    db1.addCondition("PLUNO", new DataValue(lv2.getPluNo(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    DataValue[] insValue = {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(lv2.getCategoryId(), Types.VARCHAR),
                            new DataValue(lv2.getPluNo(), Types.VARCHAR),
                            new DataValue(lv2.getUnSide(), Types.VARCHAR),
                            new DataValue(lv2.getUnCook(), Types.VARCHAR),
                            new DataValue(lv2.getUnCall(), Types.VARCHAR),
                            new DataValue(lastmodiopId, Types.VARCHAR),
                            new DataValue(lastmodiopName, Types.VARCHAR),
                            new DataValue(df.format(new Date()), Types.DATE)
                    };
                    InsBean ins = new InsBean("DCP_KDSDISHES_CONTROL", columns);
                    ins.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ins));
                }
            }

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            // TODO: handle exception
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DishControlUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DishControlUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DishControlUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DishControlUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_DishControlUpdate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
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
    protected TypeToken<DCP_DishControlUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_DishControlUpdate_OpenReq>() {
        };
    }

    @Override
    protected DCP_DishControlUpdate_OpenRes getResponseType() {
        return new DCP_DishControlUpdate_OpenRes();
    }
}
