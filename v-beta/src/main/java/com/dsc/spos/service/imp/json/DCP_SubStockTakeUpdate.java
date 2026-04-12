package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SubStockTakeUpdateReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeUpdateRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeUpdateRes.level1Elm;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_SubStockTakeUpdate
 * 服务说明：盘点子任务修改
 * @author jinzma
 * @since  2021-02-25
 */
public class DCP_SubStockTakeUpdate extends SPosAdvanceService<DCP_SubStockTakeUpdateReq, DCP_SubStockTakeUpdateRes> {
    @Override
    protected void processDUID(DCP_SubStockTakeUpdateReq req, DCP_SubStockTakeUpdateRes res) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        level1Elm datas = res.new level1Elm();
        String lastModiOpId = req.getOpNO();
        String lastModiOpName = req.getOpName();
        String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try{
            String subStockTakeNo = req.getRequest().getSubStockTakeNo();
            String memo = req.getRequest().getMemo();
            String sql = " select * from dcp_substocktake"
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and substocktakeno='"+subStockTakeNo+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {

                String status = getQData.get(0).get("STATUS").toString();
                if (status.equals("2")){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务已确认,不能修改!");
                }

                UptBean ub = new UptBean("DCP_SUBSTOCKTAKE");
                ub.addUpdateValue("MEMO",new DataValue(memo, Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPID",new DataValue(lastModiOpId, Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPNAME",new DataValue(lastModiOpName, Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME",new DataValue(lastModiTime, Types.DATE));

                // condition
                ub.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                ub.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("SUBSTOCKTAKENO",new DataValue(subStockTakeNo, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub));
                this.doExecuteDataToDB();

                res.setDatas(datas);
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

            }else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务不存在!");
            }

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SubStockTakeUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SubStockTakeUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SubStockTakeUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        String memo = req.getRequest().getMemo();

        if (Check.Null(subStockTakeNo)) {
            errMsg.append("盘点子任务单号不能为空,");
            isFail = true;
        }
        if (Check.Null(memo)) {
            errMsg.append("备注不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;

    }

    @Override
    protected TypeToken<DCP_SubStockTakeUpdateReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeUpdateReq>(){};
    }

    @Override
    protected DCP_SubStockTakeUpdateRes getResponseType() {
        return new DCP_SubStockTakeUpdateRes();
    }
}
