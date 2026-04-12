package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComCodeQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComCodeQuery_OpenRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComCodeQuery_OpenRes.Datas;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.ContactWay;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONObject;


import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComCodeQuery_Open
 * 服务说明：商城查询二维码
 * @author jinzma
 * @since  2024-03-14
 */
public class DCP_ISVWeComCodeQuery_Open extends SPosAdvanceService<DCP_ISVWeComCodeQuery_OpenReq, DCP_ISVWeComCodeQuery_OpenRes> {

    @Override
    protected void processDUID(DCP_ISVWeComCodeQuery_OpenReq req, DCP_ISVWeComCodeQuery_OpenRes res) throws Exception {

        Datas datas = res.new Datas();
        try {
            String eId = req.geteId();
            String codeType = req.getRequest().getCodeType();  //qrCode 个人活码
            datas.setCodeType(codeType);
            String shopId = req.getRequest().getShopId();
            String memberId = req.getRequest().getMemberId();
            String unionId = req.getRequest().getUnionId();
            String openId = req.getRequest().getOpenId();
            //以下这个是裂变券的，需要记录一下裂变券活动编号，便于后续统计，这个裂变券的设计就是垃圾 因为很难看懂，不好理解
            String isFissionCoupon = req.getRequest().getIsFissionCoupon();
            String fissionBillNo = req.getRequest().getFissionBillNo();

            String channelId = req.getApiUser().getChannelId();

            //系统日期和时间
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String dateTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();

            //个人活码，安驰说后续还会增加社群活码，其实还不如后续加了再考虑方案，现在预留意义不大
            if (codeType.equals("qrCode")) {

                //判断当前会员是否已经是企微会员，是企微会员直接就返回给前端了，如果当前会员不存在，就新增一笔会员
                {
                    String sql = " select * from dcp_isvwecom_member where eid='" + eId + "' and memberid='" + memberId + "' ";
                    List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                    if (CollectionUtils.isNotEmpty(getQData)) {
                        String externalUserId = getQData.get(0).get("EXTERNALUSERID").toString();
                        if (!Check.Null(externalUserId)) {
                            datas.setExternalUserId(externalUserId);

                            res.setDatas(datas);
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("服务执行成功");
                            return ;
                        }
                    }

                    //会员不存在，会新增一笔会员
                    if (CollectionUtils.isEmpty(getQData)) {
                        String[] columns = {"EID", "MEMBERID", "UNIONID", "EXTERNALUSERID", "PENDINGID", "VALIDDATE", "OPENID"};
                        DataValue[] insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(memberId, Types.VARCHAR),
                                new DataValue(unionId, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(openId, Types.VARCHAR),
                        };
                        // 插入DCP_ISVWECOM_MEMBER
                        InsBean ib = new InsBean("DCP_ISVWECOM_MEMBER", columns);
                        ib.addValues(insValue);

                        this.addProcessData(new DataProcessBean(ib));

                        this.doExecuteDataToDB();

                    }

                }


                //调企微unionid与external_userid的关联，用unionid去尝试获取外部客户，解决可能客户之前其他途径已经绑定了外部客户
                {
                    String sql = " select * from dcp_isvwecom_member where eid='" + eId + "' and memberid='" + memberId + "' ";
                    List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                    if (CollectionUtils.isNotEmpty(getQData)) {
                        String union_Id = getQData.get(0).get("UNIONID").toString();
                        String open_Id = getQData.get(0).get("OPENID").toString();
                        String pendingId = getQData.get(0).get("PENDINGID").toString();
                        String validDate = getQData.get(0).get("VALIDDATE").toString();

                        //此处必须使用表中的数据进行查询，避免前端给值错误，上面一段新增用的是前端传入的memberId和unionId写入，但是如果不为空，取前端就可能出现给值错误，必须从表里面去取
                        JSONObject jsonObject = dcpWeComUtils.unionid_to_external_userid(StaticInfo.dao, union_Id, open_Id);
                        if (jsonObject != null) {
                            String external_userid = jsonObject.optString("external_userid","");
                            String pending_id = jsonObject.optString("pending_id","");

                            if (!Check.Null(external_userid) || (!Check.Null(pending_id))) {
                                //更新 DCP_ISVWECOM_MEMBER
                                UptBean ub = new UptBean("DCP_ISVWECOM_MEMBER");
                                ub.addUpdateValue("EXTERNALUSERID", new DataValue(external_userid, Types.VARCHAR));

                                //pending_id 有效期是90天，如果企微返回变了，重新记录有效期
                                if (!Check.Null(pending_id) && !pending_id.equals(pendingId)) {
                                    ub.addUpdateValue("PENDINGID", new DataValue(pending_id, Types.VARCHAR));
                                    ub.addUpdateValue("VALIDDATE", new DataValue(PosPub.GetStringDate(sDate, 90), Types.VARCHAR));
                                }

                                //Condition
                                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub.addCondition("MEMBERID", new DataValue(memberId, Types.VARCHAR));

                                this.addProcessData(new DataProcessBean(ub));

                                this.doExecuteDataToDB();

                            }

                            //之前绑定过了，直接返回给前端
                            if (!Check.Null(external_userid)){
                                datas.setExternalUserId(external_userid);

                                res.setDatas(datas);
                                res.setSuccess(true);
                                res.setServiceStatus("000");
                                res.setServiceDescription("服务执行成功");

                                return ;
                            }

                        }

                    }else {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "异常: 当前会员在dcp_isvwecom_member表中不存在");
                    }

                }


                //查询传入门店对应的客服userId
                String userId = "";
                {
                    String sql = " select userid from dcp_isvwecom_staffs_shop where eid='" + eId + "' and shopid='" + shopId + "' ";
                    List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                    if (!CollectionUtils.isEmpty(getQData)) {
                        userId = getQData.get(0).get("USERID").toString();
                    }
                }

                //裂变营销: 获取营销单号和对应的userid （客服ID）
                if ("Y".equals(isFissionCoupon)){
                    //未知营销活动单号，需要去表里面查询，逻辑参考CRM服务
                    if (Check.Null(fissionBillNo)){
                        String sql = " select billno,userid from crm_fissioncoupon "
                                + " where eid='"+eId+"' and selectchannelid='"+channelId+"' and status='100' and wecom='1' "
                                + " and begintime <=trunc(sysdate) and endtime >=trunc(sysdate) ";
                        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                        if (CollectionUtils.isEmpty(getQData)) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "异常: 找不到渠道:"+channelId+"对应的裂变券活动");
                        }
                        fissionBillNo = getQData.get(0).get("BILLNO").toString();
                        //找不到所属门店对应的客服，用这个来兜底
                        if (Check.Null(userId)) {
                            userId = getQData.get(0).get("USERID").toString();
                        }

                    }else {
                        String sql = " select userid from crm_fissioncoupon where eid='"+eId+"' and billno='"+fissionBillNo+"' and wecom='1' ";
                        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                        if (CollectionUtils.isEmpty(getQData)) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "异常: 活动单号("+fissionBillNo+")不存在或非企微活动");
                        }
                        //找不到所属门店对应的客服，用这个来兜底
                        if (Check.Null(userId)) {
                            userId = getQData.get(0).get("USERID").toString();
                        }
                    }
                }

                //非裂变营销，取dcp_isvwecom_lbspage对应的userid
                if (!"Y".equals(isFissionCoupon)){
                    if (Check.Null(userId)) {
                        String sql = " select userid from dcp_isvwecom_lbspage where eid='"+eId+"' and pagetype='qrCode' and userid is not null ";
                        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                        if (CollectionUtils.isEmpty(getQData)) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "异常: dcp_isvwecom_lbspage表资料缺失");
                        }
                        //找不到所属门店对应的客服，用这个来兜底
                        userId = getQData.get(0).get("USERID").toString();
                    }
                }


                //查询会员永久二维码，如果不存在则调企微创建
                {
                    String sql = " select * from dcp_isvwecom_tempqrcode where eid='"+eId+"' and memberid='"+memberId+"' ";
                    List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                    if (CollectionUtils.isEmpty(getQData)) {
                        //调企微产生永久码
                        String[] userArray = {userId};

                        ContactWay contactWay = new ContactWay();
                        contactWay.setType("1");                              // 是	联系方式类型,1-单人, 2-多人
                        contactWay.setScene("2");                             // 是	场景，1-在小程序中联系，2-通过二维码联系
                        contactWay.setRemark("");                             // 否	联系方式的备注信息，用于助记，不超过30个字符
                        contactWay.setSkip_verify(true);                      // 否	外部客户添加时是否无需验证，默认为true
                        contactWay.setState(memberId);                        // 否	企业自定义的state参数，用于区分不同的添加渠道，在调用“获取外部联系人详情”时会返回该参数值，不超过30个字符
                        contactWay.setUser(userArray);                        // 否	使用该联系方式的用户userID列表，在type为1时为必填，且只能有一个

                        JSONObject add_contact_way = dcpWeComUtils.add_contact_way(dao,contactWay);
                        if (add_contact_way == null){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调企微接口失败,请查阅企微日志查找原因");
                        }

                        String config_id = add_contact_way.optString("config_id");
                        String qr_code = add_contact_way.optString("qr_code");
                        if(Check.Null(config_id) || Check.Null(qr_code)){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企微返回异常,请查阅企微日志查找原因");
                        }

                        //新增 DCP_ISVWECOM_TEMPQRCODE
                        String[] columns = {"EID", "MEMBERID", "QRCODEURL", "CONFIGID", "STATE", "BILLNO", "WECOMDEL", "CREATETIME"};

                        DataValue[] insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(memberId, Types.VARCHAR),
                                new DataValue(qr_code, Types.VARCHAR),
                                new DataValue(config_id, Types.VARCHAR),
                                new DataValue(memberId, Types.VARCHAR),
                                new DataValue(fissionBillNo, Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue(dateTime, Types.DATE),
                        };
                        // DCP_ISVWECOM_TEMPQRCODE
                        InsBean ib = new InsBean("DCP_ISVWECOM_TEMPQRCODE", columns);
                        ib.addValues(insValue);

                        this.addProcessData(new DataProcessBean(ib));

                        datas.setCodeUrl(qr_code);


                    }else {
                        String qrCodeUrl = getQData.get(0).get("QRCODEURL").toString();
                        String wecomDel = getQData.get(0).get("WECOMDEL").toString();

                        //已经删除了，需要重新申请
                        if ("1".equals(wecomDel)){
                            //调企微产生永久码
                            String[] userArray = {userId};

                            ContactWay contactWay = new ContactWay();
                            contactWay.setType("1");                              // 是	联系方式类型,1-单人, 2-多人
                            contactWay.setScene("2");                             // 是	场景，1-在小程序中联系，2-通过二维码联系
                            contactWay.setRemark("");                             // 否	联系方式的备注信息，用于助记，不超过30个字符
                            contactWay.setSkip_verify(true);                      // 否	外部客户添加时是否无需验证，默认为true
                            contactWay.setState(memberId);                        // 否	企业自定义的state参数，用于区分不同的添加渠道，在调用“获取外部联系人详情”时会返回该参数值，不超过30个字符
                            contactWay.setUser(userArray);                        // 否	使用该联系方式的用户userID列表，在type为1时为必填，且只能有一个

                            JSONObject add_contact_way = dcpWeComUtils.add_contact_way(dao,contactWay);
                            if (add_contact_way == null){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调企微接口失败,请查阅企微日志查找原因");
                            }

                            String config_id = add_contact_way.optString("config_id");
                            String qr_code = add_contact_way.optString("qr_code");
                            if(Check.Null(config_id) || Check.Null(qrCodeUrl)){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企微返回异常,请查阅企微日志查找原因");
                            }

                            datas.setCodeUrl(qr_code);

                            //修改 DCP_ISVWECOM_TEMPQRCODE
                            UptBean ub = new UptBean("DCP_ISVWECOM_TEMPQRCODE");


                            ub.addUpdateValue("QRCODEURL", new DataValue(qr_code, Types.VARCHAR));
                            ub.addUpdateValue("CONFIGID", new DataValue(config_id, Types.VARCHAR));
                            ub.addUpdateValue("WECOMDEL", new DataValue("0", Types.VARCHAR));
                            ub.addUpdateValue("STATE", new DataValue(memberId, Types.VARCHAR));
                            ub.addUpdateValue("LASTMODITIME", new DataValue(dateTime, Types.DATE));
                            if (Check.Null(fissionBillNo)) {
                                ub.addUpdateValue("BILLNO", new DataValue("", Types.VARCHAR));
                            }else {
                                ub.addUpdateValue("BILLNO", new DataValue(fissionBillNo, Types.VARCHAR));
                            }

                            //Condition
                            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub.addCondition("MEMBERID", new DataValue(memberId, Types.VARCHAR));

                            this.addProcessData(new DataProcessBean(ub));

                        } else {

                            datas.setCodeUrl(qrCodeUrl);

                            //修改 DCP_ISVWECOM_TEMPQRCODE
                            UptBean ub = new UptBean("DCP_ISVWECOM_TEMPQRCODE");
                            ub.addUpdateValue("LASTMODITIME", new DataValue(dateTime, Types.DATE));
                            if (Check.Null(fissionBillNo)) {
                                ub.addUpdateValue("BILLNO", new DataValue("", Types.VARCHAR));
                            }else {
                                ub.addUpdateValue("BILLNO", new DataValue(fissionBillNo, Types.VARCHAR));
                            }

                            //Condition
                            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub.addCondition("MEMBERID", new DataValue(memberId, Types.VARCHAR));

                            this.addProcessData(new DataProcessBean(ub));
                        }
                    }
                }

            }

            this.doExecuteDataToDB();

            res.setDatas(datas);

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComCodeQuery_OpenReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComCodeQuery_OpenReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComCodeQuery_OpenReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComCodeQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            if (Check.Null(req.getRequest().getCodeType())){
                errMsg.append("codeType 不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getShopId())){
                errMsg.append("shopId 不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getMemberId())){
                errMsg.append("memberId 不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getUnionId())){
                errMsg.append("unionId 不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getOpenId())){
                errMsg.append("openId 不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getIsFissionCoupon())){
                errMsg.append("isFissionCoupon 不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComCodeQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComCodeQuery_OpenReq>(){};
    }

    @Override
    protected DCP_ISVWeComCodeQuery_OpenRes getResponseType() {
        return new DCP_ISVWeComCodeQuery_OpenRes();
    }
}
