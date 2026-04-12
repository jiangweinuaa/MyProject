package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SubStockTakeProcessCancelReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeProcessCancelRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_SubStockTakeProcessCancel extends SPosAdvanceService<DCP_SubStockTakeProcessCancelReq, DCP_SubStockTakeProcessCancelRes> {
    @Override
    protected void processDUID(DCP_SubStockTakeProcessCancelReq req, DCP_SubStockTakeProcessCancelRes res) throws Exception {

        String eId=req.geteId();
        String shopId=req.getShopId();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        String lastModiOpId = req.getOpNO();
        String lastModiOpName = req.getOpName();
        String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        DCP_SubStockTakeProcessCancelRes.level1Elm datas = res.new level1Elm();
        try {
            String sql=""
                    + " select a.status,a.importstatus from dcp_substocktake a"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.substocktakeno='"+subStockTakeNo+"' "
                    + " and a.status='2' and a.importstatus='0' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData==null || getQData.isEmpty()){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务单已导入或未确认,无法撤销!");
            }

            UptBean ub = new UptBean("DCP_SUBSTOCKTAKE");
            ub.addUpdateValue("STATUS",new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("LASTMODIOPID",new DataValue(lastModiOpId, Types.VARCHAR));
            ub.addUpdateValue("LASTMODIOPNAME",new DataValue(lastModiOpName, Types.VARCHAR));
            ub.addUpdateValue("LASTMODITIME",new DataValue(lastModiTime, Types.DATE));
            // condition
            ub.addCondition("EID",new DataValue(eId, Types.VARCHAR));
            ub.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
            ub.addCondition("SUBSTOCKTAKENO",new DataValue(subStockTakeNo, Types.VARCHAR));
            ub.addCondition("IMPORTSTATUS",new DataValue("0", Types.VARCHAR));

            this.addProcessData(new DataProcessBean(ub));


            this.doExecuteDataToDB();

            res.setDatas(datas);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SubStockTakeProcessCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SubStockTakeProcessCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SubStockTakeProcessCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeProcessCancelReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        if (Check.Null(subStockTakeNo)) {
            errMsg.append("盘点子任务单号不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_SubStockTakeProcessCancelReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeProcessCancelReq>(){};
    }

    @Override
    protected DCP_SubStockTakeProcessCancelRes getResponseType() {
        return new DCP_SubStockTakeProcessCancelRes();
    }
}
