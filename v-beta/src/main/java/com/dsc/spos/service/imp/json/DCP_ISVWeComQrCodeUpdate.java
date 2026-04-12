package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComQrCodeUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComQrCodeUpdateReq.User;
import com.dsc.spos.json.cust.res.DCP_ISVWeComQrCodeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.ContactWay;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务函数：DCP_ISVWeComQrCodeUpdate
 * 服务说明：个人活码更新
 * @author jinzma
 * @since  2024-02-27
 */
public class DCP_ISVWeComQrCodeUpdate extends SPosAdvanceService<DCP_ISVWeComQrCodeUpdateReq, DCP_ISVWeComQrCodeUpdateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComQrCodeUpdateReq req, DCP_ISVWeComQrCodeUpdateRes res) throws Exception {
        try{
            String eId = req.geteId();
            String qrCodeId = req.getRequest().getQrCodeId();
            String name = req.getRequest().getName();
            String autoPass = req.getRequest().getAutoPass();   //是否自动通过添加好友  0否1是
            List<User> userList = req.getRequest().getUserList();
            String[] userArray = new String[userList.size()];
            for (int i=0; i<userList.size(); i++){
                userArray[i] = userList.get(i).getUserId();
            }

            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String configId;
            String state;
            //资料检查
            {
                String sql = "select qrcodeid,configid,state from dcp_isvwecom_qrcode where eid='"+eId+"' and qrcodeid='"+qrCodeId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "qrCodeId不存在,无法修改");
                }
                configId = getQData.get(0).get("CONFIGID").toString();
                state = getQData.get(0).get("STATE").toString();
            }
            {
                String sql = "select name from dcp_isvwecom_qrcode where eid='"+eId+"' and name='"+name+"' and qrcodeid<>'"+qrCodeId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isNotEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "名称已存在,无法修改");
                }
            }


            //调企微
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            ContactWay contactWay = new ContactWay();
            contactWay.setConfig_id(configId);               // 是	企业联系方式的配置id
            contactWay.setType("2");                         // 是	联系方式类型,1-单人, 2-多人
            contactWay.setScene("2");                        // 是	场景，1-在小程序中联系，2-通过二维码联系
            contactWay.setRemark(req.getRequest().getRemark());   // 否	联系方式的备注信息，用于助记，不超过30个字符
            contactWay.setSkip_verify(!"0".equals(autoPass));   // 否	外部客户添加时是否无需验证，默认为true
            contactWay.setState(state);                      // 否	企业自定义的state参数，用于区分不同的添加渠道，在调用“获取外部联系人详情”时会返回该参数值，不超过30个字符
            contactWay.setUser(userArray);                      // 否	使用该联系方式的用户userID列表，在type为1时为必填，且只能有一个

            if (!dcpWeComUtils.update_contact_way(dao,contactWay)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调企微接口失败,请查阅企微日志查找原因");
            }

            //修改 DCP_ISVWECOM_QRCODE
            {
                UptBean ub = new UptBean("DCP_ISVWECOM_QRCODE");
                ub.addUpdateValue("NAME", new DataValue(name, Types.VARCHAR));
                ub.addUpdateValue("REMARK", new DataValue(req.getRequest().getRemark(), Types.VARCHAR));
                ub.addUpdateValue("AUTOPASS", new DataValue(autoPass, Types.VARCHAR));
                ub.addUpdateValue("LOGO", new DataValue(req.getRequest().getLogo(), Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME", new DataValue(sDate, Types.DATE));

                //条件
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("QRCODEID", new DataValue(qrCodeId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub));
            }

            //删除 DCP_ISVWECOM_QRCODE_USER
            {
                DelBean db1 = new DelBean("DCP_ISVWECOM_QRCODE_USER");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("QRCODEID", new DataValue(qrCodeId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
            }

            //新增 DCP_ISVWECOM_QRCODE_USER
            {
                String[] columns = {"EID", "QRCODEID", "USERID"};
                for (User user : userList) {
                    InsBean ib = new InsBean("DCP_ISVWECOM_QRCODE_USER", columns);
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(qrCodeId, Types.VARCHAR),
                            new DataValue(user.getUserId(), Types.VARCHAR),
                    };
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComQrCodeUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComQrCodeUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComQrCodeUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComQrCodeUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getQrCodeId())) {
                errMsg.append("qrCodeId不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getName())) {
                errMsg.append("name不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getAutoPass())) {
                errMsg.append("autoPass不能为空,");
                isFail = true;
            }

            if (!Check.Null(req.getRequest().getRemark())) {
                if (req.getRequest().getRemark().length()>30) {
                    errMsg.append("remark不能超过30个字符,");
                    isFail = true;
                }
            }

            List<User> userList = req.getRequest().getUserList();
            if (CollectionUtil.isEmpty(userList)) {
                errMsg.append("userList不能为空,");
                isFail = true;
            } else {
                for (User user : userList) {
                    if (Check.Null(user.getUserId())) {
                        errMsg.append("userId不能为空,");
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
    protected TypeToken<DCP_ISVWeComQrCodeUpdateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComQrCodeUpdateReq>(){};
    }

    @Override
    protected DCP_ISVWeComQrCodeUpdateRes getResponseType() {
        return new DCP_ISVWeComQrCodeUpdateRes();
    }
}
