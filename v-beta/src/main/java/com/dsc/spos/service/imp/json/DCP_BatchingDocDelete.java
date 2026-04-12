package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BatchingDocDeleteReq;
import com.dsc.spos.json.cust.res.DCP_BatchingDocDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BatchingDocDelete  extends SPosAdvanceService<DCP_BatchingDocDeleteReq, DCP_BatchingDocDeleteRes> {

    @Override
    protected void processDUID(DCP_BatchingDocDeleteReq req, DCP_BatchingDocDeleteRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        List<DCP_BatchingDocDeleteReq.Datas> datas = req.getRequest().getDatas();
        if(datas.size()>0){
            List<String> collect = datas.stream().map(x -> "'" + x.getBatchNo() + "'").distinct().collect(Collectors.toList());
            String join = String.join(",", collect);
            String sql="select * from mes_batching a where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.status!='0' and a.batchno in ("+join+")";
            List<Map<String, Object>> list = this.doQueryData(sql, null);
            if(list.size()>0){
                for (Map<String, Object> map : list){
                    String batchNo = map.get("BATCHNO").toString();
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据"+batchNo+"不能删除！".toString());
                }
            }

            for (DCP_BatchingDocDeleteReq.Datas data : datas){

                DelBean db1 = new DelBean("MES_BATCHING");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("BATCHNO", new DataValue(data.getBatchNo(), Types.VARCHAR));
                db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                DelBean db2 = new DelBean("MES_BATCHING_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("BATCHNO", new DataValue(data.getBatchNo(), Types.VARCHAR));
                db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                DelBean db3 = new DelBean("MES_BATCHING_DETAIL_MO");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("BATCHNO", new DataValue(data.getBatchNo(), Types.VARCHAR));
                db3.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));
            }

        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BatchingDocDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BatchingDocDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BatchingDocDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BatchingDocDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_BatchingDocDeleteReq> getRequestType() {
        return new TypeToken<DCP_BatchingDocDeleteReq>() {
        };
    }

    @Override
    protected DCP_BatchingDocDeleteRes getResponseType() {
        return new DCP_BatchingDocDeleteRes();
    }
}

