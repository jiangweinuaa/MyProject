package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockOutRefundDeleteReq;
import com.dsc.spos.json.cust.res.DCP_StockOutRefundDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_StockOutRefundDelete extends SPosAdvanceService<DCP_StockOutRefundDeleteReq, DCP_StockOutRefundDeleteRes>
{


    @Override
    protected void processDUID(DCP_StockOutRefundDeleteReq req, DCP_StockOutRefundDeleteRes res) throws Exception
    {
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        DCP_StockOutRefundDeleteReq.levelElm request = req.getRequest();
        String stockOutNO = request.getStockOutNo();
        String stockOutNo_origin = request.getStockOutNo_origin();

        try
        {
            //查詢單據狀態
            String sql = "select status "
                    + " from DCP_Stockout "
                    + " where SHOPID = '"+shopId+"' AND OrganizationNO = '"+organizationNO+"' "
                    + " AND EID = '"+eId+"' and stockOutNO = '"+stockOutNO+"' ";

            List<Map<String, Object>> getQData = this.doQueryData(sql, null);

            if (getQData != null && getQData.isEmpty() == false)
            {
                String status=getQData.get(0).get("STATUS").toString();
                if (status.equals("-1"))
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉审批中，不能删除！");
                }

                if (status.equals("0"))
                {
                    //樣板 刪除行為
                    DelBean db1 = new DelBean("DCP_StockOut");
                    db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    DelBean db2 = new DelBean("DCP_StockOut_Detail");
                    db2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db2.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));



                    //原单单头字段 恢复原状
                    UptBean ub1 = new UptBean("DCP_StockOut");
                    ub1.addUpdateValue("STOCKOUTNO_REFUND", new DataValue("", Types.VARCHAR));

                    // condition
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("STOCKOUTNO", new DataValue(stockOutNo_origin, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));


                    //原单单身字段 恢复原状
                    ub1 = new UptBean("DCP_StockOut_Detail");
                    ub1.addUpdateValue("PQTY_REFUND", new DataValue("0", Types.VARCHAR));

                    // condition
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("STOCKOUTNO", new DataValue(stockOutNo_origin, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));

                    //删除内部结算
                    ColumnDataValue condition = new ColumnDataValue();
                    condition.add("EID", req.geteId());
                    condition.add("BILLNO", req.getRequest().getStockOutNo());

                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition)));
                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition)));


                    this.doExecuteDataToDB();

                    res.setSuccess(true);
                    res.setServiceStatus("000");
                    res.setServiceDescription("服务执行成功");

                }
                else
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已确认，不能删除！");
                }
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在，请重新确认！");
            }
        }
        catch (Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutRefundDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutRefundDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutRefundDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockOutRefundDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_StockOutRefundDeleteReq.levelElm request = req.getRequest();

        String stockOutNO = request.getStockOutNo();
        String stockOutNo_origin = request.getStockOutNo_origin();

        if (Check.Null(stockOutNO)) {
            isFail = true;
            errMsg.append("stockOutNo不可为空值, ");
        }
        if (Check.Null(stockOutNo_origin)) {
            isFail = true;
            errMsg.append("stockOutNo_origin不可为空值, ");
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockOutRefundDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_StockOutRefundDeleteReq>(){};
    }

    @Override
    protected DCP_StockOutRefundDeleteRes getResponseType()
    {
        return new DCP_StockOutRefundDeleteRes();
    }
}
