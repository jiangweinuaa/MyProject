package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReconliationDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ReconliationDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_ReconliationDelete extends SPosAdvanceService<DCP_ReconliationDeleteReq, DCP_ReconliationDeleteRes> {

    @Override
    protected void processDUID(DCP_ReconliationDeleteReq req, DCP_ReconliationDeleteRes res) throws Exception {

        String eId = req.geteId();
        String corp = req.getRequest().getCorp();
        String reconNo = req.getRequest().getReconNo();

        String validSql="select * from DCP_RECONLIATION a where a.eid='"+eId+"'" +
                " and a.corp='"+corp+"' and a.reconNo='"+reconNo+"' ";
        List<Map<String, Object>> validData = this.doQueryData(validSql, null);

        String detailSql="select a.*,b.SETTLEAMT,b.billamt,b.billno,b.item as setitem " +
                " from DCP_RECONDETAIL a" +
                " left join DCP_SETTLEDATA b on a.eid=b.eid and a.sourceno=b.billno and a.sourcenoseq=b.item " +
                " where a.eid='"+eId+"' " +
                " and a.reconno='"+reconNo+"' and a.corp='"+corp+"' ";
        List<Map<String, Object>> detailData = this.doQueryData(detailSql, null);

        if(validData.size()>0){
            String status = validData.get(0).get("STATUS").toString();
            if(Check.Null(status)||"0".equals(status)){//新建可删
                DelBean db1 = new DelBean("DCP_RECONLIATION");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("CORP", new DataValue(corp, Types.VARCHAR));
                db1.addCondition("RECONNO", new DataValue(reconNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                DelBean db2 = new DelBean("DCP_RECONDETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("CORP", new DataValue(corp, Types.VARCHAR));
                db2.addCondition("RECONNO", new DataValue(reconNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                if(detailData.size()>0){
                    for(Map<String, Object> detail:detailData){
                        //String sourceNo = detail.get("SOURCENO").toString();
                        //String sourceNoSeq = detail.get("SOURCENOSEQ").toString();
                        //SETTLEAMT 减掉当前对账金额
                        String currentReconAmt = detail.get("CURRRECONAMT").toString();//当前对账金额
                        String billNo = detail.get("BILLNO").toString();
                        String setItem = detail.get("SETITEM").toString();
                        if(Check.NotNull(billNo)&&Check.NotNull(setItem)){
                            BigDecimal settleAmt = new BigDecimal(detail.get("SETTLEAMT").toString());//已对账金额
                            BigDecimal billAmt = new BigDecimal(detail.get("BILLAMT").toString());
                            settleAmt=settleAmt.subtract(new BigDecimal(currentReconAmt));
                            BigDecimal unSettleAmt=billAmt.subtract(settleAmt);
                            //更新状态  对账中
                            UptBean ub1 = new UptBean("DCP_SETTLEDATA");
                            ub1.addUpdateValue("status", new DataValue("0", Types.VARCHAR));
                            ub1.addUpdateValue("SETTLEAMT", new DataValue(settleAmt.toString(), Types.VARCHAR));
                            ub1.addUpdateValue("UNSETTLEAMT", new DataValue(unSettleAmt.toString(), Types.VARCHAR));
                            //condition
                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                            ub1.addCondition("ITEM", new DataValue(setItem, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub1));
                        }

                    }
                }

            }
        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReconliationDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReconliationDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReconliationDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReconliationDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReconliationDeleteReq> getRequestType() {
        return new TypeToken<DCP_ReconliationDeleteReq>() {
        };
    }

    @Override
    protected DCP_ReconliationDeleteRes getResponseType() {
        return new DCP_ReconliationDeleteRes();
    }
}

