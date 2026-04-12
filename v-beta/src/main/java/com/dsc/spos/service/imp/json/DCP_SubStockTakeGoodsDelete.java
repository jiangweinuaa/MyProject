package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SubStockTakeGoodsDeleteReq;
import com.dsc.spos.json.cust.req.DCP_SubStockTakeGoodsDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeGoodsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/*
 * 服务函数：DCP_SubStockTakeGoodsDelete
 * 服务说明：盘点子任务商品删除
 * @author jinzma
 * @since  2021-03-05
 */
public class DCP_SubStockTakeGoodsDelete extends SPosAdvanceService<DCP_SubStockTakeGoodsDeleteReq, DCP_SubStockTakeGoodsDeleteRes> {
    @Override
    protected void processDUID(DCP_SubStockTakeGoodsDeleteReq req, DCP_SubStockTakeGoodsDeleteRes res) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        DCP_SubStockTakeGoodsDeleteRes.level1Elm datas = res.new level1Elm();
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
                List<level1Elm> pluList = req.getRequest().getPluList();
                for (level1Elm par : pluList) {
                    String pluNo = par.getPluNo();
                    String featureNo = par.getFeatureNo();

                    //DCP_SUBSTOCKTAKE_DETAILTRACK
                    DelBean db1 = new DelBean("DCP_SUBSTOCKTAKE_DETAILTRACK");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("SUBSTOCKTAKENO", new DataValue(subStockTakeNo, Types.VARCHAR));
                    db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                    db1.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    //DCP_SUBSTOCKTAKE_DETAIL
                    DelBean db2 = new DelBean("DCP_SUBSTOCKTAKE_DETAIL");
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db2.addCondition("SUBSTOCKTAKENO", new DataValue(subStockTakeNo, Types.VARCHAR));
                    db2.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                    db2.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));

                }

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
    protected List<InsBean> prepareInsertData(DCP_SubStockTakeGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SubStockTakeGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SubStockTakeGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeGoodsDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        if (Check.Null(subStockTakeNo)) {
            errMsg.append("盘点子任务单号不能为空,");
            isFail = true;
        }else
        {
            List<level1Elm> pluList = req.getRequest().getPluList();
            for (level1Elm par : pluList){
                String pluNo = par.getPluNo();
                String featureNo = par.getFeatureNo();

                if (Check.Null(pluNo)) {
                    errMsg.append("商品编码不能为空,");
                    isFail = true;
                }
                if (Check.Null(featureNo)) {
                    errMsg.append("特征码不能为空,");
                    isFail = true;
                }
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_SubStockTakeGoodsDeleteReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeGoodsDeleteReq>(){};
    }

    @Override
    protected DCP_SubStockTakeGoodsDeleteRes getResponseType() {
        return new DCP_SubStockTakeGoodsDeleteRes();
    }
}
