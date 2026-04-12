package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockInRefundDeleteReq;
import com.dsc.spos.json.cust.res.DCP_StockInRefundDeleteRes;
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

public class DCP_StockInRefundDelete extends SPosAdvanceService<DCP_StockInRefundDeleteReq, DCP_StockInRefundDeleteRes>
{


    @Override
    protected void processDUID(DCP_StockInRefundDeleteReq req, DCP_StockInRefundDeleteRes res) throws Exception
    {
        DCP_StockInRefundDeleteReq.levelElm request = req.getRequest();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String stockInNO = request.getStockInNo();
        String stockInNo_origin = request.getStockInNo_origin();

        try
        {
            //查詢單據狀態
            String sql = "select status,ofno,doc_type "
                    + " from DCP_Stockin "
                    + " where SHOPID = '"+shopId+"' AND OrganizationNO = '"+organizationNO+"' "
                    + " AND EID = '"+eId+"' and stockinNO = '"+stockInNO+"' ";

            List<Map<String, Object>> getQData = this.doQueryData(sql, null);

            if (getQData != null && getQData.isEmpty() == false)
            {
                String status = getQData.get(0).get("STATUS").toString();
                String docType = getQData.get(0).get("DOC_TYPE").toString();
                String ofNo = getQData.get(0).get("OFNO").toString();

                if (status.equals("0"))
                {
                    //DCP_STOCKIN
                    DelBean db1 = new DelBean("DCP_STOCKIN");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    db1.addCondition("StockInNO", new DataValue(stockInNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    //DCP_STOCKIN_DETAIL
                    DelBean db2 = new DelBean("DCP_STOCKIN_DETAIL");
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    db2.addCondition("StockInNO", new DataValue(stockInNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));



                    //原单单头字段 恢复原状
                    UptBean ub1 = new UptBean("DCP_STOCKIN");
                    ub1.addUpdateValue("STOCKINNO_REFUND", new DataValue("", Types.VARCHAR));

                    // condition
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("STOCKINNO", new DataValue(stockInNo_origin, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));


                    //原单单身字段 恢复原状
                    ub1 = new UptBean("DCP_STOCKIN_DETAIL");
                    ub1.addUpdateValue("PQTY_REFUND", new DataValue("0", Types.VARCHAR));

                    // condition
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("STOCKINNO", new DataValue(stockInNo_origin, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));


                    //删除内部结算
                    ColumnDataValue condition = new ColumnDataValue();
                    condition.add("EID", req.geteId());
                    condition.add("BILLNO", req.getRequest().getStockInNo());

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
        catch(Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockInRefundDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockInRefundDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockInRefundDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockInRefundDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_StockInRefundDeleteReq.levelElm request = req.getRequest();

        String stockInNO = request.getStockInNo();
        String stockInNo_origin = request.getStockInNo_origin();


        if (Check.Null(stockInNO)) {
            isFail = true;
            errMsg.append("stockInNo不可为空值, ");
        }
        if (Check.Null(stockInNo_origin)) {
            isFail = true;
            errMsg.append("stockInNo_origin不可为空值, ");
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockInRefundDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_StockInRefundDeleteReq>(){};
    }

    @Override
    protected DCP_StockInRefundDeleteRes getResponseType()
    {
        return new DCP_StockInRefundDeleteRes();
    }
}
