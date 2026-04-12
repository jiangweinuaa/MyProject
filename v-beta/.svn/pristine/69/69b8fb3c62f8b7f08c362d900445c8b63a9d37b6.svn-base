package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PStockOutDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PStockOutDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_PStockOutDelete  extends SPosAdvanceService<DCP_PStockOutDeleteReq, DCP_PStockOutDeleteRes>
{
    @Override
    protected void processDUID(DCP_PStockOutDeleteReq req, DCP_PStockOutDeleteRes res) throws Exception {
        // TODO Auto-generated method stub
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();;
        String pStockInNO = req.getRequest().getPStockInNo();
        String shopId = req.getShopId();
        try
        {
            String sql = "select status from DCP_pstockin "
                    + " where EID='"+eId+"' and organizationno='"+organizationNO+"' "
                    + " and pstockinno='"+pStockInNO+"' and status='0' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && getQData.isEmpty() == false)
            {
                //DCP_PSTOCKIN
                DelBean db1 = new DelBean("DCP_PSTOCKIN");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //DCP_PSTOCKIN_DETAIL
                DelBean db2 = new DelBean("DCP_PSTOCKIN_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                db2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //2018-09-07新增
                //DCP_PSTOCKIN_MATERIAL
                DelBean db3 = new DelBean("DCP_PSTOCKIN_MATERIAL");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db3.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                db3.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));


                DelBean db4 = new DelBean("DCP_PSTOCKOUT_BATCH");
                db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db4.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                db4.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db4));


                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

            }
            else
            {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PStockOutDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PStockOutDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PStockOutDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PStockOutDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        String pStockInNO = req.getRequest().getPStockInNo();
        if (Check.Null(pStockInNO))
        {
            isFail = true;
            errMsg.append("组合拆解单单号不可为空值, ");
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PStockOutDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_PStockOutDeleteReq>(){};
    }

    @Override
    protected DCP_PStockOutDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_PStockOutDeleteRes();
    }

}
