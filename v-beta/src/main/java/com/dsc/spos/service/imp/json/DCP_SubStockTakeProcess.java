package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SubStockTakeProcessReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeProcessRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeProcessRes.level1Elm;
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
 * 服务函数：DCP_SubStockTakeProcess
 * 服务说明：盘点子任务确认
 * @author jinzma
 * @since  2021-02-26
 */
public class DCP_SubStockTakeProcess extends SPosAdvanceService<DCP_SubStockTakeProcessReq, DCP_SubStockTakeProcessRes> {
    @Override
    protected void processDUID(DCP_SubStockTakeProcessReq req, DCP_SubStockTakeProcessRes res) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        level1Elm datas = res.new level1Elm();
        String lastModiOpId = req.getOpNO();
        String lastModiOpName = req.getOpName();
        String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try{
            String subStockTakeNo = req.getRequest().getSubStockTakeNo();
            String sql = " select a.status,b.item from dcp_substocktake a"
                    + " left join dcp_substocktake_detail b on a.eid=b.eid and a.shopid=b.shopid and a.substocktakeno=b.substocktakeno"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.substocktakeno='"+subStockTakeNo+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                String status = getQData.get(0).get("STATUS").toString();
                String item = getQData.get(0).get("ITEM").toString();
                if (status.equals("2")){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务已确认!");
                }
                if (Check.Null(item)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务商品明细不能为空!");
                }

                UptBean ub = new UptBean("DCP_SUBSTOCKTAKE");
                ub.addUpdateValue("STATUS",new DataValue("2", Types.VARCHAR));
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

        }catch(Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SubStockTakeProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SubStockTakeProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SubStockTakeProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeProcessReq req) throws Exception {
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
    protected TypeToken<DCP_SubStockTakeProcessReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeProcessReq>(){};
    }

    @Override
    protected DCP_SubStockTakeProcessRes getResponseType() {
        return new DCP_SubStockTakeProcessRes();
    }
}
