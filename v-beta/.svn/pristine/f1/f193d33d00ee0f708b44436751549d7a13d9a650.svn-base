package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveItemsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ReserveItemsUpdateRes;
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
 * @description: 预约项目修改
 * @author: wangzyc
 * @create: 2021-07-21
 */
public class DCP_ReserveItemsUpdate extends SPosAdvanceService<DCP_ReserveItemsUpdateReq, DCP_ReserveItemsUpdateRes> {
    @Override
    protected void processDUID(DCP_ReserveItemsUpdateReq req, DCP_ReserveItemsUpdateRes res) throws Exception {
        String eId = req.geteId();
        DCP_ReserveItemsUpdateReq.level1Elm request = req.getRequest();

        try {
            String itemsNo = request.getItemsNo();
            String status = request.getStatus();
            String shopId = request.getShopId();
            List<DCP_ReserveItemsUpdateReq.level2Elm> opList = request.getOpList();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastmodiopId = req.getOpNO();
            String lastmodiopName = req.getOpName();
            Date time = new Date();
            String lastmoditime = df.format(time);


            DelBean db1 = new DelBean("DCP_RESERVEADVISOR");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("ITEMSNO", new DataValue(itemsNo, Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            String[] columnsTwo = {
                    "EID", "ITEMSNO", "SHOPID", "OPNO"
            };

            if(!CollectionUtils.isEmpty(opList)){
                for (DCP_ReserveItemsUpdateReq.level2Elm lv2 : opList) {
                    DataValue[] insValuelev = {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(itemsNo, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(lv2.getOpNo(), Types.VARCHAR)
                    };
                    InsBean ins = new InsBean("DCP_RESERVEADVISOR", columnsTwo);
                    ins.addValues(insValuelev);
                    this.addProcessData(new DataProcessBean(ins));
                }
            }

            UptBean ub1 = null;
            ub1 = new UptBean("DCP_RESERVEITEMS");
            ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(lastmodiopId,Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(lastmodiopName,Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));

            ub1.addCondition("ITEMSNO", new DataValue(itemsNo, Types.VARCHAR));
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));


            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReserveItemsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveItemsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveItemsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveItemsUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveItemsUpdateReq.level1Elm request = req.getRequest();
        if(request==null)
        {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if(Check.Null(request.getShopId())){
            errMsg.append("所属门店不能为空 ");
            isFail = true;
        }

        if(Check.Null(request.getItemsNo())){
            errMsg.append("项目编号不能为空");
            isFail = true;
        }
        if(Check.Null(request.getStatus())){
            errMsg.append("状态不能为空");
            isFail = true;
        }
        if(CollectionUtils.isEmpty(request.getOpList())){
            errMsg.append("分配顾问不能为空");
            isFail = true;
        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveItemsUpdateReq> getRequestType() {
        return new TypeToken<DCP_ReserveItemsUpdateReq>(){};
    }

    @Override
    protected DCP_ReserveItemsUpdateRes getResponseType() {
        return new DCP_ReserveItemsUpdateRes();
    }
}
