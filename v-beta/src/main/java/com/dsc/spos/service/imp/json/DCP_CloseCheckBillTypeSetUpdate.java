package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CloseCheckBillTypeSetUpdateReq;
import com.dsc.spos.json.cust.req.DCP_CloseCheckBillTypeSetUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_CloseCheckBillTypeSetUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
/**
 * @apiNote 闭店检查单据类型设置适用类型修改
 * @since 2021-05-18
 * @author jinzma
 */
public class DCP_CloseCheckBillTypeSetUpdate extends SPosAdvanceService<DCP_CloseCheckBillTypeSetUpdateReq, DCP_CloseCheckBillTypeSetUpdateRes> {

    @Override
    protected void processDUID(DCP_CloseCheckBillTypeSetUpdateReq req, DCP_CloseCheckBillTypeSetUpdateRes res) throws Exception {
        String eId=req.geteId();
        String lastModiOpId = req.getOpNO();
        String lastModiOpName = req.getOpName();
        String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try {
            String billType = req.getRequest().getBillType();
            String status = req.getRequest().getStatus();
            String restrictShop = req.getRequest().getRestrictShop();
            List<level1Elm> shopList = req.getRequest().getShopList();

            //修改单据单头
            UptBean ub = new UptBean("DCP_CLOSEBILLTYPE");
            ub.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
            ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub.addUpdateValue("LASTMODIOPID", new DataValue(lastModiOpId, Types.VARCHAR));
            ub.addUpdateValue("LASTMODIOPNAME", new DataValue(lastModiOpName, Types.VARCHAR));
            ub.addUpdateValue("LASTMODITIME", new DataValue(lastModiTime, Types.DATE));
            // condition
            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub.addCondition("BILLTYPE", new DataValue(billType, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub));

            //删除单身
            DelBean db = new DelBean("DCP_CLOSEBILLTYPE_PICKSHOP");
            db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db.addCondition("BILLTYPE", new DataValue(billType, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db));

            String[] columns = {
                    "EID","BILLTYPE","SHOPID"
            };

            if (!restrictShop.equals("0")){
                for (level1Elm par : shopList) {
                    String shopId = par.getShopId();
                    DataValue[]	insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(billType, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                    };
                    InsBean ib = new InsBean("DCP_CLOSEBILLTYPE_PICKSHOP", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CloseCheckBillTypeSetUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CloseCheckBillTypeSetUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CloseCheckBillTypeSetUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CloseCheckBillTypeSetUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String billType = req.getRequest().getBillType();
        String status = req.getRequest().getStatus();
        String restrictShop = req.getRequest().getRestrictShop();
        List<level1Elm> shopList = req.getRequest().getShopList();

        if (Check.Null(billType)) {
            errMsg.append("单据类型不能为空,");
            isFail = true;
        }
        if (Check.Null(status)) {
            errMsg.append("状态不能为空,");
            isFail = true;
        }
        if (Check.Null(restrictShop)) {
            errMsg.append("是否限用门店不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (!restrictShop.equals("0")){
            if (shopList==null) {
                errMsg.append("门店shopList不能为空,");
                isFail = true;
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
            else {
                for (level1Elm par:shopList) {
                    String shopId=par.getShopId();
                    if (Check.Null(shopId)) {
                        errMsg.append("门店ID不能为空,");
                        isFail = true;
                    }
                    if (isFail) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }
            }
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_CloseCheckBillTypeSetUpdateReq> getRequestType() {
        return new TypeToken<DCP_CloseCheckBillTypeSetUpdateReq>(){};
    }

    @Override
    protected DCP_CloseCheckBillTypeSetUpdateRes getResponseType() {
        return new DCP_CloseCheckBillTypeSetUpdateRes();
    }
}
