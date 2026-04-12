package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockTakeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_StockTakeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_StockTakeDelete extends SPosAdvanceService<DCP_StockTakeDeleteReq, DCP_StockTakeDeleteRes>
{
    @Override
    protected boolean isVerifyFail(DCP_StockTakeDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String stockTakeNO = req.getRequest().getStockTakeNo();
        if (Check.Null(stockTakeNO)) {
            isFail = true;
            errMsg.append("盘点单单号不可为空值, ");
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected void processDUID(DCP_StockTakeDeleteReq req, DCP_StockTakeDeleteRes res) throws Exception {
        // TODO Auto-generated method stub
        String eId = req.geteId();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String stockTakeNO = req.getRequest().getStockTakeNo();
        try
        {
            String sql = "select ofno,substocktakeno from DCP_stocktake a"
                    + " left join dcp_substocktake b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno"
                    + " where a.EID='"+eId+"' and a.organizationno='"+organizationNO+"' "
                    + " and a.stocktakeno='"+stockTakeNO+"' and a.status='0' ";


            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && getQData.isEmpty() == false)
            {
                String ofno = getQData.get(0).get("OFNO").toString();
                //更新DCP_STOCKTASK //有盘点计划单
                if (ofno != null && ofno.length() > 0)
                {
                    //更新计划单状态==6
                    UptBean	ub1 = new UptBean("DCP_STOCKTASK");
                    ub1.addUpdateValue("Status", new DataValue("6", Types.VARCHAR));
                    ub1.addCondition("Status", new DataValue("7", Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("StockTaskNO", new DataValue(ofno, Types.VARCHAR));
                    ub1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }
                String subStockTakeNo = getQData.get(0).get("SUBSTOCKTAKENO").toString();
                if (!Check.Null(subStockTakeNo)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务:"+ subStockTakeNo + " 已创建, 无法删除!");
                }

                //DCP_STOCKTAKE_DETAIL
                DelBean db2 = new DelBean("DCP_STOCKTAKE_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("StockTakeNO", new DataValue(stockTakeNO, Types.VARCHAR));
                db2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //DCP_STOCKTAKE
                DelBean db1 = new DelBean("DCP_STOCKTAKE");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("StockTakeNO", new DataValue(stockTakeNO, Types.VARCHAR));
                db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //DCP_STOCKTAKE_DETAIL_UNIT
                DelBean db3 = new DelBean("DCP_STOCKTAKE_DETAIL_UNIT");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db3.addCondition("StockTakeNO", new DataValue(stockTakeNO, Types.VARCHAR));
                db3.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));



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
    protected List<InsBean> prepareInsertData(DCP_StockTakeDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTakeDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTakeDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected TypeToken<DCP_StockTakeDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_StockTakeDeleteReq>(){};
    }

    @Override
    protected DCP_StockTakeDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_StockTakeDeleteRes();
    }

}
