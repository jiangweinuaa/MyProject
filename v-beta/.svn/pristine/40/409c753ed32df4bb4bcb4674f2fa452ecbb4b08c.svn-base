package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_DeliveryPasswordUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryPasswordUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_DeliveryPasswordUpdate
 *    說明：配送员密码修改
 * 服务说明：配送员密码修改
 * @author wangzyc
 * @since  2021/4/23
 */
public class DCP_DeliveryPasswordUpdate extends SPosAdvanceService<DCP_DeliveryPasswordUpdateReq, DCP_DeliveryPasswordUpdateRes> {
    @Override
    protected void processDUID(DCP_DeliveryPasswordUpdateReq req, DCP_DeliveryPasswordUpdateRes res) throws Exception {

        String createId = req.getOpNO();
        String createName = req.getOpName();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try {
            if(!checkExist(req)){
                UptBean ub1 = null;
                ub1 = new UptBean("DCP_DELIVERYMAN");
                //Value
                ub1.addUpdateValue("PASSWORD", new DataValue(req.getRequest().getNewPassword(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPID", new DataValue(createId, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(createName, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
                // condition
                ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                ub1.addCondition("OPNO", new DataValue(req.getRequest().getOpNo(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));

                this.doExecuteDataToDB();
            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "用户不存在");
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DeliveryPasswordUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DeliveryPasswordUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DeliveryPasswordUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DeliveryPasswordUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_DeliveryPasswordUpdateReq> getRequestType() {
        return new TypeToken<DCP_DeliveryPasswordUpdateReq>(){};
    }

    @Override
    protected DCP_DeliveryPasswordUpdateRes getResponseType() {
        return new DCP_DeliveryPasswordUpdateRes();
    }

    private boolean checkExist(DCP_DeliveryPasswordUpdateReq req)  throws Exception {
        String sql = null;
        boolean exist = false;
        String eId = req.geteId();
        String opNo =req.getRequest().getOpNo();

        sql = " select * from DCP_DELIVERYMAN  where EID='"+eId+"' and OPNO='"+opNo+"' " ;
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (CollectionUtils.isEmpty(getQData)) {
            exist = true;
        }
        return exist;
    }
}
