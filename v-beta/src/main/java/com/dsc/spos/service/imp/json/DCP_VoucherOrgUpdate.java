package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_VoucherOrgUpdateReq;
import com.dsc.spos.json.cust.res.DCP_VoucherOrgUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_VoucherOrgUpdate extends SPosAdvanceService<DCP_VoucherOrgUpdateReq, DCP_VoucherOrgUpdateRes>
{

    @Override
    protected void processDUID(DCP_VoucherOrgUpdateReq req, DCP_VoucherOrgUpdateRes res) throws Exception
    {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        List<DCP_VoucherOrgUpdateReq.level1Elm> empList=req.getRequest().getOrgList();

        String[] columns_DCP_VOUCHER_ORG =
                {
                        "EID","ORGID","ORGID_OUT","ORGNAME_OUT","COMPID_OUT","COMPNAME_OUT","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
                };

        for (DCP_VoucherOrgUpdateReq.level1Elm level1Elm : empList)
        {

            String sql = "select * from DCP_VOUCHER_ORG where EID='"+req.geteId()+"' and ORGID='"+level1Elm.getOrgId()+"' ";
            List<Map<String , Object>> getData=this.doQueryData(sql, null);

            if (getData == null || getData.size()==0)
            {
                DataValue[] insValue1 = null;

                insValue1 = new DataValue[]{
                        new DataValue(req.geteId(), Types.VARCHAR),
                        new DataValue(level1Elm.getOrgId(), Types.VARCHAR),
                        new DataValue(level1Elm.getOrgIdOut(), Types.VARCHAR),
                        new DataValue(level1Elm.getOrgNameOut(), Types.VARCHAR),
                        new DataValue(level1Elm.getCompIdOut(), Types.VARCHAR),
                        new DataValue(level1Elm.getCompNameOut(), Types.VARCHAR),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(req.getOpName(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };

                InsBean ib1 = new InsBean("DCP_VOUCHER_ORG", columns_DCP_VOUCHER_ORG);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1)); // 新增
            }
            else
            {
                UptBean ub1 = new UptBean("DCP_VOUCHER_ORG");
                ub1.addUpdateValue("ORGID_OUT", new DataValue(level1Elm.getOrgIdOut(), Types.VARCHAR));
                ub1.addUpdateValue("ORGNAME_OUT", new DataValue(level1Elm.getOrgNameOut(), Types.VARCHAR));
                ub1.addUpdateValue("COMPID_OUT", new DataValue(level1Elm.getCompIdOut(), Types.VARCHAR));
                ub1.addUpdateValue("COMPNAME_OUT", new DataValue(level1Elm.getCompNameOut(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

                //condition
                ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                ub1.addCondition("ORGID", new DataValue(level1Elm.getOrgId(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_VoucherOrgUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_VoucherOrgUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_VoucherOrgUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_VoucherOrgUpdateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        List<DCP_VoucherOrgUpdateReq.level1Elm> empList=req.getRequest().getOrgList();
        if(empList==null || empList.size()==0)
        {
            errMsg.append("列表不可为空, ");
            isFail = true;
        }

        for (DCP_VoucherOrgUpdateReq.level1Elm level1Elm : empList)
        {
            if (Check.Null(level1Elm.getOrgId()))
            {
                errMsg.append("机构不可为空, ");
                isFail = true;
            }
//            if (Check.Null(level1Elm.getOrgIdOut()))
//            {
//                errMsg.append("外部机构不可为空, ");
//                isFail = true;
//            }

        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_VoucherOrgUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_VoucherOrgUpdateReq>(){};
    }

    @Override
    protected DCP_VoucherOrgUpdateRes getResponseType()
    {
        return new DCP_VoucherOrgUpdateRes();
    }


}
