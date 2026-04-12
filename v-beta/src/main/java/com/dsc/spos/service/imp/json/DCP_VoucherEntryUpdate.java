package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_VoucherEntryUpdateReq;
import com.dsc.spos.json.cust.res.DCP_VoucherEntryUpdateRes;
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

public class DCP_VoucherEntryUpdate extends SPosAdvanceService<DCP_VoucherEntryUpdateReq, DCP_VoucherEntryUpdateRes>
{

    @Override
    protected void processDUID(DCP_VoucherEntryUpdateReq req, DCP_VoucherEntryUpdateRes res) throws Exception
    {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        List<DCP_VoucherEntryUpdateReq.level1Elm> voucherEntryList=req.getRequest().getVoucherEntryList();

        String[] columns_DCP_VOUCHERENTRY =
                {
                        "EID","VOUCHERTYPE","ENTRYTYPE","ENTRYID","SUBJECTID","MEMO","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
                };

        for (DCP_VoucherEntryUpdateReq.level1Elm level1Elm : voucherEntryList)
        {
            String sql = "select * from DCP_VOUCHERENTRY where EID='"+req.geteId()+"' and VOUCHERTYPE='"+level1Elm.getVoucherType()+"' and ENTRYTYPE='"+level1Elm.getEntryType()+"' and ENTRYID='"+level1Elm.getEntryId()+"' ";
            List<Map<String , Object>> getData=this.doQueryData(sql, null);

            if (getData == null || getData.size()==0)
            {
                DataValue[] insValue1 = null;

                insValue1 = new DataValue[]{
                        new DataValue(req.geteId(), Types.VARCHAR),
                        new DataValue(level1Elm.getVoucherType(), Types.VARCHAR),
                        new DataValue(level1Elm.getEntryType(), Types.VARCHAR),
                        new DataValue(level1Elm.getEntryId(), Types.VARCHAR),
                        new DataValue(level1Elm.getSubjectId(), Types.VARCHAR),
                        new DataValue(level1Elm.getMemo(), Types.VARCHAR),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(req.getOpName(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };

                InsBean ib1 = new InsBean("DCP_VOUCHERENTRY", columns_DCP_VOUCHERENTRY);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1)); // 新增
            }
            else
            {
                UptBean ub1 = new UptBean("DCP_VOUCHERENTRY");
                ub1.addUpdateValue("SUBJECTID", new DataValue(level1Elm.getSubjectId(), Types.VARCHAR));
                ub1.addUpdateValue("MEMO", new DataValue(level1Elm.getMemo(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

                //condition
                ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                ub1.addCondition("VOUCHERTYPE", new DataValue(level1Elm.getVoucherType(), Types.VARCHAR));
                ub1.addCondition("ENTRYTYPE", new DataValue(level1Elm.getEntryType(), Types.VARCHAR));
                ub1.addCondition("ENTRYID", new DataValue(level1Elm.getEntryId(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }

        }
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_VoucherEntryUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_VoucherEntryUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_VoucherEntryUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_VoucherEntryUpdateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        List<DCP_VoucherEntryUpdateReq.level1Elm> voucherEntryList=req.getRequest().getVoucherEntryList();
        if(voucherEntryList==null || voucherEntryList.size()==0)
        {
            errMsg.append("凭证列表不可为空, ");
            isFail = true;
        }

        for (DCP_VoucherEntryUpdateReq.level1Elm level1Elm : voucherEntryList)
        {
            if (Check.Null(level1Elm.getVoucherType()))
            {
                errMsg.append("凭证类型不可为空, ");
                isFail = true;
            }
            if (Check.Null(level1Elm.getEntryType()))
            {
                errMsg.append("分录类型不可为空, ");
                isFail = true;
            }
            if (Check.Null(level1Elm.getEntryId()))
            {
                errMsg.append("分录项目不可为空, ");
                isFail = true;
            }
            if (Check.Null(level1Elm.getDebitOrCredit()))
            {
                errMsg.append("借贷不可为空, ");
                isFail = true;
            }
        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_VoucherEntryUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_VoucherEntryUpdateReq>(){};
    }

    @Override
    protected DCP_VoucherEntryUpdateRes getResponseType()
    {
        return new DCP_VoucherEntryUpdateRes();
    }


}
