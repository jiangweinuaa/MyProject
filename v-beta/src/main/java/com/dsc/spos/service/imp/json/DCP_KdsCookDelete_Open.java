package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_KdsCookDelete_OpenReq;
import com.dsc.spos.json.cust.res.DCP_KdsCookDelete_OpenRes;
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
 * @description: KDS机器人删除
 * @author: wangzyc
 * @create: 2021-09-16
 */
public class DCP_KdsCookDelete_Open extends SPosAdvanceService<DCP_KdsCookDelete_OpenReq, DCP_KdsCookDelete_OpenRes> {
    @Override
    protected void processDUID(DCP_KdsCookDelete_OpenReq req, DCP_KdsCookDelete_OpenRes res) throws Exception {
        DCP_KdsCookDelete_OpenReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String shopId = request.getShopId();
        String machineId = request.getMachineId();
        String cookId = request.getCookId();

        try
        {
            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from DCP_KDSCOOKSET where eid = ? and SHOPID = ? and COOKID = ?");
            List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), new String[]{eId, shopId, cookId});
            if(!CollectionUtils.isEmpty(data))
            {
                String sortId = data.get(0).get("SORTID").toString();

                //删除
                DelBean db1 = new DelBean("DCP_KDSCOOKSET");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("COOKID", new DataValue(cookId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //更新其他机器人顺序
                UptBean ub1 = new UptBean("DCP_KDSCOOKSET");
                ub1.addCondition("SORTID",new DataValue( sortId, Types.VARCHAR, DataValue.DataExpression.GreaterEQ));
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addUpdateValue("SORTID",new DataValue(1,Types.INTEGER, DataValue.DataExpression.SubSelf));
                this.addProcessData(new DataProcessBean(ub1));

                //删除机器人要清空加工任务中，此机器人已分配的菜品（待制作状态1的）
                UptBean ub_processTaskDetail = new UptBean("dcp_processtask_detail");
                ub_processTaskDetail.addUpdateValue("COOKNAME",new DataValue("", Types.VARCHAR));
                ub_processTaskDetail.addUpdateValue("COOKID",new DataValue("", Types.VARCHAR));

                ub_processTaskDetail.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                ub_processTaskDetail.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
                ub_processTaskDetail.addCondition("COOKID",new DataValue(cookId, Types.VARCHAR));
                ub_processTaskDetail.addCondition("goodsstatus",new DataValue("1", Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub_processTaskDetail));

                this.doExecuteDataToDB();
            }

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_KdsCookDelete_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_KdsCookDelete_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_KdsCookDelete_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_KdsCookDelete_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_KdsCookDelete_OpenReq.level1Elm request = req.getRequest();
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

        if (Check.Null(request.getCookId())) {
            errMsg.append("机器人编号不能为空");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KdsCookDelete_OpenReq> getRequestType() {
        return new TypeToken<DCP_KdsCookDelete_OpenReq>(){};
    }

    @Override
    protected DCP_KdsCookDelete_OpenRes getResponseType() {
        return new DCP_KdsCookDelete_OpenRes();
    }
}
