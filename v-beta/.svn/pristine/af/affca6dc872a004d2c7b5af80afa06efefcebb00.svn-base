package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SubStockTakeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeDeleteRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeDeleteRes.level1Elm;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_SubStockTakeDelete
 * 服务说明：盘点子任务删除
 * @author jinzma
 * @since  2021-02-25
 */
public class DCP_SubStockTakeDelete extends SPosAdvanceService<DCP_SubStockTakeDeleteReq, DCP_SubStockTakeDeleteRes> {
    @Override
    protected void processDUID(DCP_SubStockTakeDeleteReq req, DCP_SubStockTakeDeleteRes res) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        level1Elm datas = res.new level1Elm();
        try{
            String subStockTakeNo = req.getRequest().getSubStockTakeNo();
            String sql = " select status,importstatus from dcp_substocktake"
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and substocktakeno='"+subStockTakeNo+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                String status = getQData.get(0).get("STATUS").toString();  //0：新建（待盘点）； 2：已确定
                String importStatus = getQData.get(0).get("IMPORTSTATUS").toString(); //0：未导入；100：已导入
                if ( !status.equals("0") || !importStatus.equals("0")){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务已确认,不能删除!");
                }

                //DCP_SUBSTOCKTAKE
                DelBean db1 = new DelBean("DCP_SUBSTOCKTAKE");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("SUBSTOCKTAKENO", new DataValue(subStockTakeNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //DCP_SUBSTOCKTAKE_DETAIL
                DelBean db2 = new DelBean("DCP_SUBSTOCKTAKE_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("SUBSTOCKTAKENO", new DataValue(subStockTakeNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //DCP_SUBSTOCKTAKE_DETAILTRACK
                DelBean db3 = new DelBean("DCP_SUBSTOCKTAKE_DETAILTRACK");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db3.addCondition("SUBSTOCKTAKENO", new DataValue(subStockTakeNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));

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
    protected List<InsBean> prepareInsertData(DCP_SubStockTakeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SubStockTakeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SubStockTakeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_SubStockTakeDeleteReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeDeleteReq>(){};
    }

    @Override
    protected DCP_SubStockTakeDeleteRes getResponseType() {
        return new DCP_SubStockTakeDeleteRes();
    }
}
