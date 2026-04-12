package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_LStockOutRefundDeleteReq;
import com.dsc.spos.json.cust.res.DCP_LStockOutRefundDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_LStockOutRefundDelete extends SPosAdvanceService<DCP_LStockOutRefundDeleteReq, DCP_LStockOutRefundDeleteRes>
{


    @Override
    protected void processDUID(DCP_LStockOutRefundDeleteReq req, DCP_LStockOutRefundDeleteRes res) throws Exception
    {
        String shopId = req.getShopId();
        String eId = req.geteId();;
        String lStockOutNO = req.getRequest().getlStockOutNo();
        String lStockoutNo_origin = req.getRequest().getlStockoutNo_origin();

        try
        {
            String sql ="select status from DCP_lstockout "
                    + "where EID='"+eId+"' and shopid='"+shopId+"' and lstockoutno='"+lStockOutNO+"' and status='0' ";
            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
            {
                //DCP_LSTOCKOUT
                DelBean db1 = new DelBean("DCP_LSTOCKOUT");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("lStockOutNO", new DataValue(lStockOutNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //DCP_LSTOCKOUT_DETAIL
                DelBean db2 = new DelBean("DCP_LSTOCKOUT_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("lStockOutNO", new DataValue(lStockOutNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //原单单头字段 恢复原状
                UptBean ub1 = new UptBean("DCP_LSTOCKOUT");
                ub1.addUpdateValue("LSTOCKOUTNO_REFUND", new DataValue("", Types.VARCHAR));

                // condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("LSTOCKOUTNO", new DataValue(lStockoutNo_origin, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub1));


                //原单单身字段 恢复原状
                ub1 = new UptBean("DCP_LSTOCKOUT_DETAIL");
                ub1.addUpdateValue("PQTY_REFUND", new DataValue("0", Types.VARCHAR));

                // condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("LSTOCKOUTNO", new DataValue(lStockoutNo_origin, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub1));


                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_LStockOutRefundDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_LStockOutRefundDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_LStockOutRefundDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_LStockOutRefundDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        String lStockOutNO = req.getRequest().getlStockOutNo();
        String lStockoutNo_origin = req.getRequest().getlStockoutNo_origin();

        if (Check.Null(lStockOutNO)) {
            isFail = true;
            errMsg.append("lStockOutNo不可为空值, ");
        }
        if (Check.Null(lStockoutNo_origin)) {
            isFail = true;
            errMsg.append("lStockoutNo_origin不可为空值, ");
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_LStockOutRefundDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_LStockOutRefundDeleteReq>(){};
    }

    @Override
    protected DCP_LStockOutRefundDeleteRes getResponseType()
    {
        return new DCP_LStockOutRefundDeleteRes();
    }



}
