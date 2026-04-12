package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CustomerUpdateReq;
import com.dsc.spos.json.cust.req.DCP_CustomerUpdateReq.ServiceStaff;
import com.dsc.spos.json.cust.res.DCP_CustomerUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.springframework.util.CollectionUtils;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_CustomerUpdate
 * 服务说明：大客户修改
 * @author jinzma
 * @since  2023-12-27
 */
public class DCP_CustomerUpdate extends SPosAdvanceService<DCP_CustomerUpdateReq, DCP_CustomerUpdateRes> {
    @Override
    protected void processDUID(DCP_CustomerUpdateReq req, DCP_CustomerUpdateRes res) throws Exception {

        try{
            String eId = req.geteId();
            String customerNo = req.getRequest().getCustomerNo();
            String sql = " select copywriting from dcp_customer  "
                    + " where eid='"+eId+"' and customerno='"+customerNo+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (!CollectionUtils.isEmpty(getQData)){
                String copyWriting = req.getRequest().getCopyWriting();
                List<ServiceStaff> serviceStaffList = req.getRequest().getServiceStaffList();

                //if (!Check.Null(copyWriting)){ }
                //修改DCP_CUSTOMER
                UptBean ub = new UptBean("DCP_CUSTOMER");
                ub.addUpdateValue("COPYWRITING", new DataValue(copyWriting, Types.VARCHAR));
                // condition
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("CUSTOMERNO", new DataValue(customerNo, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub));


                //删除DCP_CUSTOMER_SERVICESTAFF
                DelBean db = new DelBean("DCP_CUSTOMER_SERVICESTAFF");
                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db.addCondition("CUSTOMERNO", new DataValue(customerNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db));

                if (!CollectionUtils.isEmpty(serviceStaffList)){
                    //新增 DCP_CUSTOMER_SERVICESTAFF
                    String[] columns = {"EID","CUSTOMERNO","OPNO"};
                    for (ServiceStaff serviceStaff:serviceStaffList){
                        DataValue[]	insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(customerNo, Types.VARCHAR),
                                new DataValue(serviceStaff.getOpNo(), Types.VARCHAR),
                        };
                        InsBean ib = new InsBean("DCP_CUSTOMER_SERVICESTAFF", columns);
                        ib.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib));
                    }
                }

                this.doExecuteDataToDB();

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

            }else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "大客户不存在,请重新输入 ");
            }

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CustomerUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustomerUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustomerUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CustomerUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (req.getRequest()==null) {
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getCustomerNo())) {
                errMsg.append("大客户编号不可为空值, ");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerUpdateReq> getRequestType() {
        return new TypeToken<DCP_CustomerUpdateReq>(){};
    }

    @Override
    protected DCP_CustomerUpdateRes getResponseType() {
        return new DCP_CustomerUpdateRes();
    }
}
