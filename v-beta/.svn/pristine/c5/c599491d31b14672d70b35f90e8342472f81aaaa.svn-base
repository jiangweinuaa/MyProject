package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MStockOutDeleteReq;
import com.dsc.spos.json.cust.res.DCP_MStockOutDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_MStockOutDelete extends SPosAdvanceService<DCP_MStockOutDeleteReq, DCP_MStockOutDeleteRes> {

    @Override
    protected void processDUID(DCP_MStockOutDeleteReq req, DCP_MStockOutDeleteRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        List<DCP_MStockOutDeleteReq.Datas> datas = req.getRequest().getDatas();
        if(datas.size()>0){
            StringBuffer sJoinno=new StringBuffer("");
            for (DCP_MStockOutDeleteReq.Datas data : datas){
                sJoinno.append(data.getMStockOutNo()+",");
            }
            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("NO", sJoinno.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

            String sql="with p as ("+withasSql_mono+") " +
                    " select * from DCP_MSTOCKOUT a" +
                    " inner join p on p.no=a.mstockoutno " +
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                    " and a.status !='0'" ;
            List<Map<String, Object>> list=this.doQueryData(sql,null);
            if(list.size()>0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "不能删除！".toString());
            }

            for (DCP_MStockOutDeleteReq.Datas data : datas){
                DelBean db1 = new DelBean("DCP_MSTOCKOUT");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                db1.addCondition("MSTOCKOUTNO", new DataValue(data.getMStockOutNo(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                DelBean db2 = new DelBean("DCP_MSTOCKOUT_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                db2.addCondition("MSTOCKOUTNO", new DataValue(data.getMStockOutNo(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

            }
        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MStockOutDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MStockOutDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MStockOutDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MStockOutDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_MStockOutDeleteReq> getRequestType() {
        return new TypeToken<DCP_MStockOutDeleteReq>() {
        };
    }

    @Override
    protected DCP_MStockOutDeleteRes getResponseType() {
        return new DCP_MStockOutDeleteRes();
    }
}

