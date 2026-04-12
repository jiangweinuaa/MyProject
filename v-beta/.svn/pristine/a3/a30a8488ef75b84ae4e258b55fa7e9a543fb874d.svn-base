package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AdvisorCreateReq;
import com.dsc.spos.json.cust.req.DCP_AdvisorProfessionalCreateReq;
import com.dsc.spos.json.cust.res.DCP_AdvisorProfessionalCreateRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: 职称等级创建
 * @author: wangzyc
 * @create: 2021-07-14
 */
public class DCP_AdvisorProfessionalCreate extends SPosAdvanceService<DCP_AdvisorProfessionalCreateReq, DCP_AdvisorProfessionalCreateRes> {
    @Override
    protected void processDUID(DCP_AdvisorProfessionalCreateReq req, DCP_AdvisorProfessionalCreateRes res) throws Exception {
        DCP_AdvisorProfessionalCreateReq.level1Elm request = req.getRequest();

        String opNO = req.getOpNO();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date time = new Date();
        String lastmoditime = df.format(time);
        Date lastmodiTime = df.parse(lastmoditime);

        String[] columnsName = {
                "PROFESSIONALID","PROFESSIONALNAME","LASTMODIOPID","LASTMODITIME"
        };

        try {
            DataValue[] insValueDetail = new DataValue[]
                    {
                            new DataValue(request.getProfessionalId(), Types.VARCHAR),
                            new DataValue(request.getProfessionalName(), Types.VARCHAR),
                            new DataValue(opNO, Types.VARCHAR),
                            new DataValue(lastmodiTime, Types.DATE)
                    };
            InsBean ib2 = new InsBean("DCP_PROFESSIONALGRADE", columnsName);
            ib2.addValues(insValueDetail);
            this.addProcessData(new DataProcessBean(ib2));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:"+e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AdvisorProfessionalCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AdvisorProfessionalCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AdvisorProfessionalCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AdvisorProfessionalCreateReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_AdvisorProfessionalCreateReq.level1Elm request = req.getRequest();

        if(request==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if(Check.Null(request.getProfessionalId())){
            errMsg.append("职称代号不能为空值 ");
            isFail = true;

        }
        if(Check.Null(request.getProfessionalName())){
            errMsg.append("职称名称不能为空值 ");
            isFail = true;

        }
        return false;
    }

    @Override
    protected TypeToken<DCP_AdvisorProfessionalCreateReq> getRequestType() {
        return new TypeToken<DCP_AdvisorProfessionalCreateReq>(){};
    }

    @Override
    protected DCP_AdvisorProfessionalCreateRes getResponseType() {
        return new DCP_AdvisorProfessionalCreateRes();
    }
}
