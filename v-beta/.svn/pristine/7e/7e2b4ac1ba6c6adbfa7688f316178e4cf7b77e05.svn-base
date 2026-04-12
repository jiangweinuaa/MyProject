package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DeliveryManCreateReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryManCreateRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_DeliveryManCreate
 *    說明：配送员新增
 * 服务说明：配送员新增
 * @author wangzyc
 * @since  2021/4/23
 */
public class DCP_DeliveryManCreate extends SPosAdvanceService<DCP_DeliveryManCreateReq, DCP_DeliveryManCreateRes> {
    @Override
    protected void processDUID(DCP_DeliveryManCreateReq req, DCP_DeliveryManCreateRes res) throws Exception {
        String eId = req.geteId();
        DCP_DeliveryManCreateReq.level1Elm request = req.getRequest();
        String opNo = request.getOpNo();
        String opName = request.getOpName();
        String password = request.getPassword();
        String viewAbleDay = request.getViewAbleDay();
        String phone = request.getPhone();
        List<DCP_DeliveryManCreateReq.level2Elm> orgList = request.getOrgList();
        String status = request.getStatus();

        String createId = req.getOpNO();
        String createName = req.getOpName();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try {
            if (checkExist(req)) {
                // 不存在
                String[] columns = {"EID", "OPNO", "OPNAME", "PHONE", "PASSWORD", "VIEWABLEDAY", "STATUS", "CREATEOPID",
                        "CREATEOPNAME", "CREATETIME", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME"};

                String[] columns_org = {"EID", "OPNO", "ORG ", "ORGNAME", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME"};

                DataValue[] insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(phone, Types.VARCHAR),
                        new DataValue(password, Types.VARCHAR),
                        new DataValue(viewAbleDay, Types.VARCHAR),
                        new DataValue(status, Types.VARCHAR),
                        new DataValue(createId, Types.VARCHAR),
                        new DataValue(createName, Types.VARCHAR),
                        new DataValue(createTime, Types.DATE),
                        new DataValue(createId, Types.VARCHAR),
                        new DataValue(createName, Types.VARCHAR),
                        new DataValue(createTime, Types.DATE),
                };
                InsBean ib = new InsBean("DCP_DELIVERYMAN", columns);
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));

                if(!CollectionUtils.isEmpty(orgList)){
                    for (DCP_DeliveryManCreateReq.level2Elm level2Elm : orgList) {
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
                    this.doExecuteDataToDB();
                }
            } else {
                // 存在
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配送员编号重复,配送员已存在！");
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DeliveryManCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DeliveryManCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DeliveryManCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DeliveryManCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        DCP_DeliveryManCreateReq.level1Elm request = req.getRequest();

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
        if (Check.Null(request.getPassword())) {
            errCt++;
            errMsg.append("登录密码不能为Null, ");
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
    protected TypeToken<DCP_DeliveryManCreateReq> getRequestType() {
        return new TypeToken<DCP_DeliveryManCreateReq>(){};
    }

    @Override
    protected DCP_DeliveryManCreateRes getResponseType() {
        return new DCP_DeliveryManCreateRes();
    }

    private boolean checkExist(DCP_DeliveryManCreateReq req)  throws Exception {
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
