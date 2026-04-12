package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_N_GoodsMustOrderReq;
import com.dsc.spos.json.cust.req.DCP_N_GoodsMustOrderReq.Plu;
import com.dsc.spos.json.cust.res.DCP_N_GoodsMustOrderRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_N_GoodsMustOrder
 * 服务说明：N-商品必点设置
 * @author jinzma
 * @since  2024-04-17
 */
public class DCP_N_GoodsMustOrder extends SPosAdvanceService<DCP_N_GoodsMustOrderReq, DCP_N_GoodsMustOrderRes> {
    @Override
    protected void processDUID(DCP_N_GoodsMustOrderReq req, DCP_N_GoodsMustOrderRes res) throws Exception {

        try{

            String eId = req.geteId();
            List<Plu> pluList = req.getRequest().getPluList();

            for (Plu plu:pluList){
                String pluNo = plu.getPluNo();
                String pluType = plu.getPluType();
                String classNo = plu.getClassNo();
                String remindType = plu.getRemindType();  //提醒类型，0.必须 1.提醒，空值为不提醒
                if (Check.Null(remindType)){
                    remindType = "-1";
                }

                String sql = " select * from dcp_class_goods "
                        + "where eid='"+eId+"' and classno='"+classNo+"' and pluno='"+pluNo+"' and plutype='"+pluType+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtils.isEmpty(getQData)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品:"+pluNo+" 在dcp_class_goods表中不存在 ");
                }

                UptBean ub = new UptBean("DCP_CLASS_GOODS");
                ub.addUpdateValue("REMINDTYPE", new DataValue(remindType, Types.VARCHAR));

                //条件
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
                ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                ub.addCondition("PLUTYPE", new DataValue(pluType, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub));

            }


            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_N_GoodsMustOrderReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_N_GoodsMustOrderReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_N_GoodsMustOrderReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_N_GoodsMustOrderReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            List<Plu> pluList = req.getRequest().getPluList();
            if (CollectionUtils.isEmpty(pluList)){
                errMsg.append("pluList不能为空,");
                isFail = true;
            }else {
                for (Plu plu:pluList){
                    if (Check.Null(plu.getPluNo())){
                        errMsg.append("pluNo不能为空,");
                        isFail = true;
                    }
                    if (Check.Null(plu.getPluType())){
                        errMsg.append("pluType不能为空,");
                        isFail = true;
                    }
                    if (Check.Null(plu.getClassNo())){
                        errMsg.append("classNo不能为空,");
                        isFail = true;
                    }
                   /* if (Check.Null(plu.getRemindType())){
                        errMsg.append("remindType不能为空,");
                        isFail = true;
                    }*/

                    if (isFail) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_N_GoodsMustOrderReq> getRequestType() {
        return new TypeToken<DCP_N_GoodsMustOrderReq>(){};
    }

    @Override
    protected DCP_N_GoodsMustOrderRes getResponseType() {
        return new DCP_N_GoodsMustOrderRes();
    }
}
