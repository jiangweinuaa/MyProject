package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_DeliveryManUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryManUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 服務函數：DCP_DeliveryManUpdate
 *    說明：配送员修改
 * 服务说明：配送员修改
 * @author wangzyc
 * @since  2021/4/23
 */
public class DCP_DeliveryManUpdate extends SPosAdvanceService<DCP_DeliveryManUpdateReq, DCP_DeliveryManUpdateRes> {
    @Override
    protected void processDUID(DCP_DeliveryManUpdateReq req, DCP_DeliveryManUpdateRes res) throws Exception {
        String eId = req.geteId();
        DCP_DeliveryManUpdateReq.level1Elm request = req.getRequest();
        String opNo = request.getOpNo();
        String opName = request.getOpName();
        String viewAbleDay = request.getViewAbleDay();
        String phone = request.getPhone();
        List<DCP_DeliveryManUpdateReq.level2Elm> orgList = request.getOrgList();
        String status = request.getStatus();

        String createId = req.getOpNO();
        String createName = req.getOpName();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try {
            String[] columns_org = {"EID", "OPNO", "ORG ", "ORGNAME", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME"};

            UptBean ub1 = null;
            ub1 = new UptBean("DCP_DELIVERYMAN");
            //Value
            ub1.addUpdateValue("OPNAME", new DataValue(opName, Types.VARCHAR));
            ub1.addUpdateValue("PHONE", new DataValue(phone, Types.VARCHAR));
            ub1.addUpdateValue("VIEWABLEDAY", new DataValue(viewAbleDay, Types.VARCHAR));
            ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(createId, Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(createName, Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
            // condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            if(!CollectionUtils.isEmpty(orgList)){
                DelBean del = new DelBean("DCP_DELIVERYMAN_ORG");
                del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                del.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(del));

                for (DCP_DeliveryManUpdateReq.level2Elm level2Elm : orgList) {
                    String org = level2Elm.getOrg();
                    String orgName = level2Elm.getOrgName();
                    DataValue[] insName = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(opNo, Types.VARCHAR),
                            new DataValue(org, Types.VARCHAR),
                            new DataValue(orgName, Types.VARCHAR),
                            new DataValue(createId, Types.VARCHAR),
                            new DataValue(createName, Types.VARCHAR),
                            new DataValue(createTime, Types.DATE),
                    };
                    InsBean ib2 = new InsBean("DCP_DELIVERYMAN_ORG", columns_org);
                    ib2.addValues(insName);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DeliveryManUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DeliveryManUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DeliveryManUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DeliveryManUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        DCP_DeliveryManUpdateReq.level1Elm request = req.getRequest();

        if (Check.Null(request.getOpNo())) {
            errCt++;
            errMsg.append("配送员编号不能为Null, ");
            isFail = true;
        }
        if (Check.Null(request.getOpName())) {
            errCt++;
            errMsg.append("配送员名称不能为Null, ");
            isFail = true;
        }
        if (Check.Null(request.getPhone())) {
            errCt++;
            errMsg.append("手机号码不能为Null, ");
            isFail = true;
        }
        if (Check.Null(request.getStatus())) {
            errCt++;
            errMsg.append("状态不能为Null, ");
            isFail = true;
        }
        if(CollectionUtils.isEmpty(request.getOrgList())){
            errCt++;
            errMsg.append("所属组织不能为Null, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DeliveryManUpdateReq> getRequestType() {
        return new TypeToken<DCP_DeliveryManUpdateReq>(){};
    }

    @Override
    protected DCP_DeliveryManUpdateRes getResponseType() {
        return new DCP_DeliveryManUpdateRes();
    }
}
