package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TouchMenuEnableReq;
import com.dsc.spos.json.cust.res.DCP_TouchMenuEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.util.List;

/**
 * @description: 门店触屏菜单启用禁用
 * @author: wangzyc
 * @create: 2021-06-15
 */
public class DCP_TouchMenuEnable extends SPosAdvanceService<DCP_TouchMenuEnableReq, DCP_TouchMenuEnableRes> {
    @Override
    protected void processDUID(DCP_TouchMenuEnableReq req, DCP_TouchMenuEnableRes res) throws Exception {

        try {

            DCP_TouchMenuEnableReq.level1Elm request = req.getRequest();
            List<DCP_TouchMenuEnableReq.level2Elm> menuList = request.getMenuList();
            String eId = req.geteId();

            String status = "100";//状态：-1未启用100已启用 0已禁用

            if (request.getOprType().equals("1"))//操作类型：1-启用2-禁用
            {
                status = "100";
            } else {
                status = "0";
            }

            for (DCP_TouchMenuEnableReq.level2Elm level2Elm : menuList) {
                String menuNo = level2Elm.getMenuNo();

                UptBean up1 = new UptBean("DCP_TOUCHMENU");

                up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                up1.addCondition("MENUNO", new DataValue(menuNo, Types.VARCHAR));

                up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(up1));
            }


            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常：" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TouchMenuEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TouchMenuEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TouchMenuEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TouchMenuEnableReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_TouchMenuEnableReq.level1Elm request = req.getRequest();
        String oprType = request.getOprType();
        List<DCP_TouchMenuEnableReq.level2Elm> menuList = request.getMenuList();

        if (Check.Null(oprType)) {
            isFail = true;
            errMsg.append("操作类型不可为空值, ");
        }
        if (CollectionUtils.isEmpty(menuList)) {
            isFail = true;
            errMsg.append("菜单编码不可为空值, ");
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_TouchMenuEnableReq> getRequestType() {
        return new TypeToken<DCP_TouchMenuEnableReq>() {
        };
    }

    @Override
    protected DCP_TouchMenuEnableRes getResponseType() {
        return new DCP_TouchMenuEnableRes();
    }
}
