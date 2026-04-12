package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComQrCodeDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComQrCodeDeleteReq.QrCode;
import com.dsc.spos.json.cust.res.DCP_ISVWeComQrCodeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComQrCodeDelete
 * 服务说明：个个人活码删除
 * @author jinzma
 * @since  2024-02-26
 */
public class DCP_ISVWeComQrCodeDelete extends SPosAdvanceService<DCP_ISVWeComQrCodeDeleteReq, DCP_ISVWeComQrCodeDeleteRes> {
    @Override
    protected void processDUID(DCP_ISVWeComQrCodeDeleteReq req, DCP_ISVWeComQrCodeDeleteRes res) throws Exception {
        try{

            String eId = req.geteId();
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            List<QrCode> qrCodeList = req.getRequest().getQrCodeList();
            for (QrCode qrCode : qrCodeList) {
                String qrCodeId = qrCode.getQrCodeId();
                //资料检查
                String sql = "select configid from dcp_isvwecom_qrcode where eid='"+eId+"' and qrcodeid='"+qrCodeId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "qrCodeId:"+qrCodeId+"不存在,无法删除");
                }
                String config_id = getQData.get(0).get("CONFIGID").toString();
                if (Check.Null(config_id)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "qrCodeId:"+qrCodeId+"对应的config_id为空,无法删除");
                }

                //调企微
                if (!dcpWeComUtils.del_contact_way(dao,config_id)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调企微接口失败,请查阅企微日志查找原因");
                }

                //删除 DCP_ISVWECOM_QRCODE
                DelBean db1 = new DelBean("DCP_ISVWECOM_QRCODE");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("QRCODEID", new DataValue(qrCodeId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //删除 DCP_ISVWECOM_QRCODE_USER
                DelBean db2 = new DelBean("DCP_ISVWECOM_QRCODE_USER");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("QRCODEID", new DataValue(qrCodeId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                this.doExecuteDataToDB();
            }



            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComQrCodeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComQrCodeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComQrCodeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComQrCodeDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            List<QrCode> qrCodeList = req.getRequest().getQrCodeList();
            if (CollectionUtil.isEmpty(qrCodeList)) {
                errMsg.append("qrCodeList不能为空,");
                isFail = true;
            } else {
                for (QrCode qrCode : qrCodeList) {
                    if (Check.Null(qrCode.getQrCodeId())) {
                        errMsg.append("qrCodeId不能为空,");
                        isFail = true;
                    }
                }
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComQrCodeDeleteReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComQrCodeDeleteReq>(){};
    }

    @Override
    protected DCP_ISVWeComQrCodeDeleteRes getResponseType() {
        return new DCP_ISVWeComQrCodeDeleteRes();
    }
}
