package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_DeliveryManDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryManDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.util.List;

/**
 * 服務函數：DCP_DeliveryManDelete
 *    說明：配送员删除
 * 服务说明：配送员删除
 * @author wangzyc
 * @since  2021/4/23
 */
public class DCP_DeliveryManDelete extends SPosAdvanceService<DCP_DeliveryManDeleteReq, DCP_DeliveryManDeleteRes> {
    @Override
    protected void processDUID(DCP_DeliveryManDeleteReq req, DCP_DeliveryManDeleteRes res) throws Exception {
        List<String> opNos = req.getRequest().getOpNo();
        String eId = req.geteId();
        if(!CollectionUtils.isEmpty(opNos)){
            for (String opNo : opNos) {
                DelBean del=new DelBean("DCP_DELIVERYMAN");
                del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                del.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
                this.pData.add(new DataProcessBean(del));

                DelBean del2=new DelBean("DCP_DELIVERYMAN_ORG");
                del2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                del2.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
                this.pData.add(new DataProcessBean(del2));
            }
        }
        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DeliveryManDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DeliveryManDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DeliveryManDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DeliveryManDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        DCP_DeliveryManDeleteReq.level1Elm request = req.getRequest();

        if (CollectionUtils.isEmpty(request.getOpNo())) {
            errCt++;
            errMsg.append("配送员编号不能为Null ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DeliveryManDeleteReq> getRequestType() {
        return new TypeToken<DCP_DeliveryManDeleteReq>(){};
    }

    @Override
    protected DCP_DeliveryManDeleteRes getResponseType() {
        return new DCP_DeliveryManDeleteRes();
    }
}
