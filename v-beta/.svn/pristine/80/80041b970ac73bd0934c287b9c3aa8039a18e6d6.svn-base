package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SubStockTakeCancelReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeCancelRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeCancelRes.level1Elm;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;

/*
 * 服务函数：DCP_SubStockTakeCancel
 * 服务说明：盘点子任务初盘导入撤销
 * @author jinzma
 * @since  2021-03-11
 */
public class DCP_SubStockTakeCancel extends SPosAdvanceService<DCP_SubStockTakeCancelReq, DCP_SubStockTakeCancelRes> {
    @Override
    protected void processDUID(DCP_SubStockTakeCancelReq req, DCP_SubStockTakeCancelRes res) throws Exception {
        String eId=req.geteId();
        String shopId=req.getShopId();
        String stockTakeNo = req.getRequest().getStockTakeNo();

        String lastModiOpId = req.getOpNO();
        String lastModiOpName = req.getOpName();
        String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String modifyBy = req.getOpNO();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
        String modifyDate = dfDate.format(cal.getTime());
        SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
        String modifyTime = dfTime.format(cal.getTime());
        level1Elm datas = res.new level1Elm();

        try {
            String sql=""
                    + " select a.stocktakeno,a.doc_type,b.substocktakeno,b.stocktype,b.status,b.importstatus from dcp_stocktake a"
                    + " left join dcp_substocktake b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.stocktakeno='"+stockTakeNo+"' and a.status='0' and a.substockimport='1' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData==null || getQData.isEmpty()){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点单已提交或导入状态非初盘导入!");
            }
            String docType = getQData.get(0).get("DOC_TYPE").toString(); //0-全盘  1-抽盘  2-模板
            for (Map<String, Object> oneData:getQData){
                String subStockTakeNo = oneData.get("SUBSTOCKTAKENO").toString();
                String stockType = oneData.get("STOCKTYPE").toString();
                String status = oneData.get("STATUS").toString();
                String importStatus = oneData.get("IMPORTSTATUS").toString();
                if (stockType.equals("2")){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务单:"+subStockTakeNo+" 已产生复盘资料，无法进行初盘撤销!");
                }
                if (status.equals("0")){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务单:"+subStockTakeNo+" 未确认，无法进行初盘撤销!");
                }
                if (importStatus.equals("0")){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务单:"+subStockTakeNo+" 未导入，无法进行初盘撤销!");
                }

                //关联盘点单对应的所有“已导入”的初盘类型的盘点子任务，这些子任务的导入状态IMPORTSTATUS改为0 且 单据状态STATUS改为0
                UptBean ub = new UptBean("DCP_SUBSTOCKTAKE");
                ub.addUpdateValue("STATUS",new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("IMPORTSTATUS",new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPID",new DataValue(lastModiOpId, Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPNAME",new DataValue(lastModiOpName, Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME",new DataValue(lastModiTime, Types.DATE));
                // condition
                ub.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                ub.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("SUBSTOCKTAKENO",new DataValue(subStockTakeNo, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub));
            }

            //清空盘点明细表DCP_STOCKTAKE_DETAIL的数据（如果是按模板盘仅清空数量，明细保留）
            if (docType.equals("2")){
                //修改 DCP_STOCKTAKE_DETAIL
                UptBean ub = new UptBean("DCP_STOCKTAKE_DETAIL");
                //add Value
                ub.addUpdateValue("PQTY",new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("BASEQTY",new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("AMT",new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("DISTRIAMT",new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("FQTY",new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("FBASEQTY",new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("RQTY",new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("RBASEQTY",new DataValue("0", Types.VARCHAR));
                //condition
                ub.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                ub.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("STOCKTAKENO",new DataValue(stockTakeNo, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub));
            }else{
                DelBean db = new DelBean("DCP_STOCKTAKE_DETAIL");
                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                db.addCondition("STOCKTAKENO", new DataValue(stockTakeNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db));
            }

            //盘点单的导入状态SUBSTOCKIMPORT改为0
            UptBean ub = new UptBean("DCP_STOCKTAKE");
            ub.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
            ub.addUpdateValue("SUBSTOCKIMPORT", new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("TOT_CQTY", new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("TOT_PQTY", new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("TOT_AMT", new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("TOT_DISTRIAMT", new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
            ub.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
            ub.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
            // condition
            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
            ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub.addCondition("STOCKTAKENO", new DataValue(stockTakeNo, Types.VARCHAR));
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
    protected List<InsBean> prepareInsertData(DCP_SubStockTakeCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SubStockTakeCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SubStockTakeCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeCancelReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        if (Check.Null(stockTakeNo)) {
            errMsg.append("盘点单号不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_SubStockTakeCancelReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeCancelReq>(){};
    }

    @Override
    protected DCP_SubStockTakeCancelRes getResponseType() {
        return new DCP_SubStockTakeCancelRes();
    }
}
