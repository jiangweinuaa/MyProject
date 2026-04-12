package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ShoppingCartDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ShoppingCartDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * @description: 购物车商品删除
 * @author: wangzyc
 * @create: 2021-05-27
 */
public class DCP_ShoppingCartDelete extends SPosAdvanceService<DCP_ShoppingCartDeleteReq, DCP_ShoppingCartDeleteRes> {
    @Override
    protected void processDUID(DCP_ShoppingCartDeleteReq req, DCP_ShoppingCartDeleteRes res) throws Exception {
        try {
            String eId = req.geteId();
            String shopId = req.getShopId();
            String opNO = req.getOpNO();


            List<DCP_ShoppingCartDeleteReq.level2Elm> pluList = req.getRequest().getPluList();
            if (!CollectionUtils.isEmpty(pluList)) {
                for (DCP_ShoppingCartDeleteReq.level2Elm level2Elm : pluList) {

                    DelBean db1 = new DelBean("DCP_SHOPPINGCART");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("ITEM", new DataValue(level2Elm.getItem(), Types.VARCHAR));
                    db1.addCondition("OPNO", new DataValue(opNO, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));
                }
            }
            this.doExecuteDataToDB();


            DCP_ShoppingCartDeleteRes.level1Elm level1Elm = res.new level1Elm();
            res.setDatas(level1Elm);
            DCP_ShoppingCartUpdate dcp_shoppingCartUpdate = new DCP_ShoppingCartUpdate();
            String getShoppingInfoSQL = dcp_shoppingCartUpdate.getShoppingInfoByItems(req, null);
            List<Map<String, Object>> getShoppingInfo = this.doQueryData(getShoppingInfoSQL, null);
            String totCqty ="0";
            String totDistriAmt = "0";
            if(!CollectionUtils.isEmpty(getShoppingInfo)) {
                Map<String, Object> getInfo = getShoppingInfo.get(0);
                 totCqty = getInfo.get("TOTCQTY").toString();
                 totDistriAmt = getInfo.get("TOTDISTRIAMT").toString();
            }
            level1Elm.setTotCqty(totCqty);
            level1Elm.setTotDistriAmt(totDistriAmt);


            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            // TODO: handle exception
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ShoppingCartDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ShoppingCartDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ShoppingCartDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ShoppingCartDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ShoppingCartDeleteReq.level1Elm request = req.getRequest();
        List<DCP_ShoppingCartDeleteReq.level2Elm> pluList = request.getPluList();
        if (CollectionUtils.isEmpty(pluList)) {
            errMsg.append("商品列表不可为空值, ");
            isFail = true;
        } else {
            for (DCP_ShoppingCartDeleteReq.level2Elm level2Elm : pluList) {
                if (Check.Null(level2Elm.getItem())) {
                    errMsg.append("项次不可为空值, ");
                    isFail = true;
                }
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ShoppingCartDeleteReq> getRequestType() {
        return new TypeToken<DCP_ShoppingCartDeleteReq>() {
        };
    }

    @Override
    protected DCP_ShoppingCartDeleteRes getResponseType() {
        return new DCP_ShoppingCartDeleteRes();
    }
}
