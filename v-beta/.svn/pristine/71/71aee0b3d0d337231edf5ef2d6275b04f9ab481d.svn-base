package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_POrderCustomerUpdateReq;
import com.dsc.spos.json.cust.res.DCP_POrderCustomerUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.internal.objects.NativeUint8Array;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DCP_POrderCustomerUpdate extends SPosAdvanceService<DCP_POrderCustomerUpdateReq, DCP_POrderCustomerUpdateRes> {
    @Override
    protected void processDUID(DCP_POrderCustomerUpdateReq req, DCP_POrderCustomerUpdateRes res) throws Exception {

        String eId = req.geteId();
        String templateNo = req.getRequest().getTemplateNo();
        String docType = "0";//要货模板
        String sql = "select * from DCP_PTEMPLATE where EID='"+eId+"' and DOC_TYPE='"+docType+"' and PTEMPLATENO='"+templateNo+"'";
        List<Map<String, Object>> getQData_checkNO = this.doQueryData(sql, null);
        if (getQData_checkNO==null||getQData_checkNO.isEmpty())
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("模板不存在！");
            return;
        }
        List<DCP_POrderCustomerUpdateReq.customer> customerList = req.getRequest().getCustomerList();
        if (customerList!=null)
        {
            DelBean db1 = new DelBean("DCP_TEMCUSTOMER");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("TEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            String[] columnsName = {"EID","TEMPLATENO","CUSTOMERNO"};
            InsBean ib1 = new InsBean("DCP_TEMCUSTOMER", columnsName);
            DataValue[] columnsVal = null;
            boolean isUpdate = false;
            for (DCP_POrderCustomerUpdateReq.customer item : customerList)
            {
                columnsVal = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(templateNo, Types.VARCHAR),
                        new DataValue(item.getCustomerNo(), Types.VARCHAR)
                };
                ib1.addValues(columnsVal);
                isUpdate = true;
            }
            if (isUpdate)
            {
                this.addProcessData(new DataProcessBean(ib1));
            }

            //更新单头
            String modifyBy = req.getOpNO();
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String modifyDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String modifyTime = df.format(cal.getTime());
            UptBean ub1 = null;
            ub1 = new UptBean("DCP_PTEMPLATE");
            ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

            // condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("PTEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
            ub1.addCondition("DOC_TYPE", new DataValue(docType, Types.VARCHAR));//要货模板
            this.addProcessData(new DataProcessBean(ub1));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_POrderCustomerUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_POrderCustomerUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_POrderCustomerUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_POrderCustomerUpdateReq req) throws Exception {
        if (req.getRequest()==null)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (Check.Null(req.getRequest().getTemplateNo()))
        {
            errMsg.append("节点templateNo不可为空值，");
            isFail = true;
        }
        List<DCP_POrderCustomerUpdateReq.customer> customerList = req.getRequest().getCustomerList();
        if (customerList==null)
        {
            errMsg.append("节点customerList不存在，");
            isFail = true;
        }

        if (customerList!=null)
        {
            for (DCP_POrderCustomerUpdateReq.customer item : customerList)
            {
                if (Check.Null(item.getCustomerNo()))
                {
                    errMsg.append("节点customerNo不可为空值，");
                    isFail = true;
                }

            }

        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_POrderCustomerUpdateReq> getRequestType() {
        return new TypeToken<DCP_POrderCustomerUpdateReq>(){};
    }

    @Override
    protected DCP_POrderCustomerUpdateRes getResponseType() {
        return new DCP_POrderCustomerUpdateRes();
    }
}
