package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpCodeDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpCodeDeleteReq.GpCode;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpCodeDeleteRes;
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
 * 服务函数：DCP_ISVWeComGpCodeDelete
 * 服务说明：社群活码删除
 * @author jinzma
 * @since  2024-02-28
 */
public class DCP_ISVWeComGpCodeDelete extends SPosAdvanceService<DCP_ISVWeComGpCodeDeleteReq, DCP_ISVWeComGpCodeDeleteRes> {
    @Override
    protected void processDUID(DCP_ISVWeComGpCodeDeleteReq req, DCP_ISVWeComGpCodeDeleteRes res) throws Exception {
        try {

            String eId = req.geteId();
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            List<GpCode> gpCodeList = req.getRequest().getGpCodeList();
            for (GpCode gpCode : gpCodeList) {
                String gpCodeId = gpCode.getGpCodeId();
                //资料检查
                String sql = "select gpcodeid,configid from dcp_isvwecom_gpcode where eid='"+eId+"' and gpcodeid='"+gpCodeId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "gpCodeId:"+gpCodeId+"不存在,无法删除");
                }
                String config_id = getQData.get(0).get("CONFIGID").toString();
                if (Check.Null(config_id)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "gpCodeId:"+gpCodeId+"对应的config_id为空,无法删除");
                }

                //调企微
                if (!dcpWeComUtils.del_join_way(dao,config_id)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调企微接口失败,请查阅企微日志查找原因");
                }



                //删除 DCP_ISVWECOM_GPCODE
                DelBean db1 = new DelBean("DCP_ISVWECOM_GPCODE");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("GPCODEID", new DataValue(gpCodeId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));


                //删除 DCP_ISVWECOM_GPCODE_GPID
                DelBean db2 = new DelBean("DCP_ISVWECOM_GPCODE_GPID");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("GPCODEID", new DataValue(gpCodeId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //删除 DCP_ISVWECOM_GPCODE_SHOP
                DelBean db3 = new DelBean("DCP_ISVWECOM_GPCODE_SHOP");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("GPCODEID", new DataValue(gpCodeId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));

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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComGpCodeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComGpCodeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComGpCodeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGpCodeDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            List<GpCode> gpCodeList = req.getRequest().getGpCodeList();
            if (CollectionUtil.isEmpty(gpCodeList)) {
                errMsg.append("gpCodeList不能为空,");
                isFail = true;
            } else {
                for (GpCode gpCode : gpCodeList) {
                    if (Check.Null(gpCode.getGpCodeId())) {
                        errMsg.append("gpCodeId不能为空,");
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
    protected TypeToken<DCP_ISVWeComGpCodeDeleteReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGpCodeDeleteReq>(){};
    }

    @Override
    protected DCP_ISVWeComGpCodeDeleteRes getResponseType() {
        return new DCP_ISVWeComGpCodeDeleteRes();
    }
}
