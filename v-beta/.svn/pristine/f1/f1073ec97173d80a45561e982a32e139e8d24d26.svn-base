package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComQrCodeCreateReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComQrCodeCreateReq.User;
import com.dsc.spos.json.cust.res.DCP_ISVWeComQrCodeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.ContactWay;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.json.JSONObject;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComQrCodeCreate
 * 服务说明：个人活码创建
 * @author jinzma
 * @since  2024-02-26
 */
public class DCP_ISVWeComQrCodeCreate extends SPosAdvanceService<DCP_ISVWeComQrCodeCreateReq, DCP_ISVWeComQrCodeCreateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComQrCodeCreateReq req, DCP_ISVWeComQrCodeCreateRes res) throws Exception {

        try{
            String eId = req.geteId();
            String name = req.getRequest().getName();
            String autoPass = req.getRequest().getAutoPass();   //是否自动通过添加好友  0否1是
            List<User> userList = req.getRequest().getUserList();
            String[] userArray = new String[userList.size()];
            for (int i=0;i<userList.size();i++){
                userArray[i] = userList.get(i).getUserId();
            }


            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String qrCodeId = PosPub.getGUID(false);
            String state = qrCodeId.substring(0,30);    //截取前30位作为state传企微


            //资料检查
            {
                String sql = "select name from dcp_isvwecom_qrcode where eid='"+eId+"' and name='"+name+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isNotEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "名称已存在,无法新增");
                }
            }

            //调企微
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            ContactWay contactWay = new ContactWay();
            contactWay.setType("2");                         // 是	联系方式类型,1-单人, 2-多人
            contactWay.setScene("2");                        // 是	场景，1-在小程序中联系，2-通过二维码联系
            contactWay.setRemark(req.getRequest().getRemark());    // 否	联系方式的备注信息，用于助记，不超过30个字符
            contactWay.setSkip_verify(!"0".equals(autoPass));   // 否	外部客户添加时是否无需验证，默认为true
            contactWay.setState(state);                      // 否	企业自定义的state参数，用于区分不同的添加渠道，在调用“获取外部联系人详情”时会返回该参数值，不超过30个字符
            contactWay.setUser(userArray);                      // 否	使用该联系方式的用户userID列表，在type为1时为必填，且只能有一个

            JSONObject add_contact_way = dcpWeComUtils.add_contact_way(dao,contactWay);
            if (add_contact_way == null){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调企微接口失败,请查阅企微日志查找原因");
            }

            String config_id = add_contact_way.optString("config_id");
            String qr_code = add_contact_way.optString("qr_code");
            if(Check.Null(config_id) || Check.Null(qr_code)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企微返回异常,请查阅企微日志查找原因");
            }

            //新增 DCP_ISVWECOM_QRCODE
            {
                String[] columns = {"EID","QRCODEID","NAME","REMARK","AUTOPASS","LOGO","CONFIGID","QRCODEURL","CREATETIME","STATE"};
                InsBean ib = new InsBean("DCP_ISVWECOM_QRCODE", columns);
                DataValue[] insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(qrCodeId, Types.VARCHAR),
                        new DataValue(name, Types.VARCHAR),
                        new DataValue(req.getRequest().getRemark(), Types.VARCHAR),
                        new DataValue(autoPass, Types.VARCHAR),
                        new DataValue(req.getRequest().getLogo(), Types.VARCHAR),
                        new DataValue(config_id, Types.VARCHAR),
                        new DataValue(qr_code, Types.VARCHAR),
                        new DataValue(sDate, Types.DATE),
                        new DataValue(state, Types.VARCHAR),
                };
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComQrCodeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComQrCodeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComQrCodeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComQrCodeCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
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
    protected TypeToken<DCP_ISVWeComQrCodeCreateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComQrCodeCreateReq>(){};
    }

    @Override
    protected DCP_ISVWeComQrCodeCreateRes getResponseType() {
        return new DCP_ISVWeComQrCodeCreateRes();
    }
}
