package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComCustomTagUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComCustomTagUpdateReq.ExternalUserId;
import com.dsc.spos.json.cust.req.DCP_ISVWeComCustomTagUpdateReq.Tag;
import com.dsc.spos.json.cust.req.DCP_ISVWeComCustomTagUpdateReq.Chat;
import com.dsc.spos.json.cust.res.DCP_ISVWeComCustomTagUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务函数：DCP_ISVWeComCustomTagUpdate
 * 服务说明：批量企微客户打标签
 * @author jinzma
 * @since  2024-01-25
 */
public class DCP_ISVWeComCustomTagUpdate extends SPosAdvanceService<DCP_ISVWeComCustomTagUpdateReq, DCP_ISVWeComCustomTagUpdateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComCustomTagUpdateReq req, DCP_ISVWeComCustomTagUpdateRes res) throws Exception {
        try{
            String eId = req.geteId();
            String type = req.getRequest().getType();  //add添加 remove删除
            List<ExternalUserId> externalUserIdList = req.getRequest().getExternalUserIdList();
            if (CollectionUtil.isEmpty(externalUserIdList)){
                externalUserIdList = new ArrayList<>();
            }
            List<Tag> tagList = req.getRequest().getTagList();
            //支持群成员打标签
            List<Chat> chatIdList = req.getRequest().getChatIdList();
            if (CollectionUtil.isNotEmpty(chatIdList)){
                for (Chat chat:chatIdList){
                    String sql = " select b.externaluserid from dcp_isvwecom_groupchat_de a"
                            + " inner join dcp_isvwecom_custom b on a.eid=b.eid and a.userid=b.externaluserid"
                            + " where a.eid='"+eId+"' and a.chatid='"+chat.getChatId()+"' and a.userid is not null ";
                    List<Map<String,Object>> getQData = this.doQueryData(sql, null);
                    if (CollectionUtil.isNotEmpty(getQData)){
                        for (Map<String,Object>oneQData:getQData){
                            ExternalUserId externalUserId = req.new ExternalUserId();
                            externalUserId.setExternalUserId(oneQData.get("EXTERNALUSERID").toString());

                            externalUserIdList.add(externalUserId);
                        }
                    }
                }
            }

            //检查标签资料是否正确
            String sql = " select tagid from dcp_isvwecom_tag where eid='"+eId+"' ";
            List<Map<String,Object>> getTagQData = this.doQueryData(sql, null);
            if (CollectionUtil.isEmpty(getTagQData)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "标签资料表为空!");
            }
            {
                for (Tag tag : tagList){
                    if (getTagQData.stream().noneMatch(a->a.get("TAGID").toString().equals(tag.getTagId()))){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "标签ID "+tag.getTagId()+"在标签资料表中不存在!");
                    }
                }
            }

            DCPWeComService dCPWeComService = new DCPWeComService();
            DCP_ISVWeComCustomTagUpdateRes.Datas datas = res.new Datas();
            datas.setExternalUserIdList(new ArrayList<>());

            for (ExternalUserId par:externalUserIdList) {
                String externalUserId = par.getExternalUserId();

                //获取企微客户详情（资料同步）
                if (!dCPWeComService.ExternalContactSync(dao, eId, externalUserId)) {
                    DCP_ISVWeComCustomTagUpdateRes.ExternalUserId externalUserIdRes = res.new ExternalUserId();
                    externalUserIdRes.setExternalUserId(externalUserId);
                    externalUserIdRes.setExternalUserName("未知");
                    externalUserIdRes.setErrorMsg("指定的成员或部门或标签不在应用的可见范围之内");   //企微客户详情同步失败，查询日志
                    datas.getExternalUserIdList().add(externalUserIdRes);
                    continue;
                }

                sql = " select a.name,userid from dcp_isvwecom_custom a"
                        + " inner join dcp_isvwecom_custom_follow b on a.eid=b.eid and a.externaluserid=b.externaluserid"
                        + " where a.eid='" + eId + "' and a.externaluserid='" + externalUserId + "' ";
                List<Map<String, Object>> getExternalUserQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getExternalUserQData)) {
                    DCP_ISVWeComCustomTagUpdateRes.ExternalUserId externalUserIdRes = res.new ExternalUserId();
                    externalUserIdRes.setExternalUserId(externalUserId);
                    externalUserIdRes.setExternalUserName("未知");
                    externalUserIdRes.setErrorMsg("客户资料表为空");
                    datas.getExternalUserIdList().add(externalUserIdRes);
                    continue;
                }

                String name = getExternalUserQData.get(0).get("NAME").toString();

                sql = " select a.tagid,a.userid from dcp_isvwecom_custom_tag a"
                        + " where a.eid='" + eId + "' and a.externaluserid='" + externalUserId + "' ";
                List<Map<String, Object>> getTagsQData = this.doQueryData(sql, null);
                //标签资料校验
                {
                    boolean isError = false;
                    for (Tag tag : tagList) {
                        Map<String, Object> getTag = getTagsQData.stream().filter(x -> x.get("TAGID").toString().equals(tag.getTagId())).findFirst().orElse(null);
                        if ("add".equals(type)) {
                            //添加
                            if (getTag != null && !getTag.isEmpty()) {
                                DCP_ISVWeComCustomTagUpdateRes.ExternalUserId externalUserIdRes = res.new ExternalUserId();
                                externalUserIdRes.setExternalUserId(externalUserId);
                                externalUserIdRes.setExternalUserName(name);
                                externalUserIdRes.setErrorMsg("标签资料已经存在,无法添加 ");
                                datas.getExternalUserIdList().add(externalUserIdRes);
                                isError = true;
                                break;
                            }
                        } else {
                            //删除
                            if (getTag == null || getTag.isEmpty()) {
                                DCP_ISVWeComCustomTagUpdateRes.ExternalUserId externalUserIdRes = res.new ExternalUserId();
                                externalUserIdRes.setExternalUserId(externalUserId);
                                externalUserIdRes.setExternalUserName(name);
                                externalUserIdRes.setErrorMsg("标签资料不存在,无法删除 ");
                                datas.getExternalUserIdList().add(externalUserIdRes);
                                isError = true;
                                break;
                            }
                        }
                    }

                    if (isError) {
                        continue;
                    }

                }

                DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();


                if ("add".equals(type)){
                    //添加从表里面查找一个用户
                    sql = " select a.userid from dcp_isvwecom_custom_follow a"
                            + " where a.eid='"+eId+"' and a.externaluserid='"+externalUserId+"' ";
                    List<Map<String,Object>> getUserQData = this.doQueryData(sql, null);
                    if (CollectionUtil.isEmpty(getUserQData)){
                        DCP_ISVWeComCustomTagUpdateRes.ExternalUserId externalUserIdRes = res.new ExternalUserId();
                        externalUserIdRes.setExternalUserId(externalUserId);
                        externalUserIdRes.setExternalUserName(name);
                        externalUserIdRes.setErrorMsg("客户与员工对应关系表为空");
                        datas.getExternalUserIdList().add(externalUserIdRes);
                        continue;
                    }
                    String userid = getUserQData.get(0).get("USERID").toString();

                    //list -- > string[]
                    List<String> tagToList = new ArrayList<>();
                    for (Tag tag : tagList) {
                        tagToList.add(tag.getTagId());
                    }
                    String[] add_tag = tagToList.toArray(new String[0]);

                    String error = dcpWeComUtils.markTag(dao,userid,externalUserId,add_tag,null);
                    if (!Check.Null(error)){
                        DCP_ISVWeComCustomTagUpdateRes.ExternalUserId externalUserIdRes = res.new ExternalUserId();
                        externalUserIdRes.setExternalUserId(externalUserId);
                        externalUserIdRes.setExternalUserName(name);
                        externalUserIdRes.setErrorMsg(error);
                        datas.getExternalUserIdList().add(externalUserIdRes);
                        continue;
                    }else {

                        //新增 DCP_ISVWECOM_CUSTOM_TAG
                        String[] columns = {"EID","EXTERNALUSERID","TAGID","USERID"};
                        for (Tag tag : tagList) {
                            DataValue[] insValue = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(externalUserId, Types.VARCHAR),
                                    new DataValue(tag.getTagId(), Types.VARCHAR),
                                    new DataValue(userid, Types.VARCHAR),
                            };
                            InsBean ib = new InsBean("DCP_ISVWECOM_CUSTOM_TAG", columns);
                            ib.addValues(insValue);
                            this.addProcessData(new DataProcessBean(ib));
                        }

                        this.doExecuteDataToDB();  //保证和企微一致，直接提交

                    }


                }else {
                    //删除
                    for (Tag tag : tagList) {
                        boolean isError = false;
                        List<Map<String, Object>> getTag = getTagsQData.stream().filter(x -> x.get("TAGID").toString().equals(tag.getTagId())).collect(Collectors.toList());
                        for (Map<String, Object> oneTag:getTag){
                            String userid = oneTag.get("USERID").toString();
                            String[] remove_tag = {tag.getTagId()};
                            String error = dcpWeComUtils.markTag(dao,userid,externalUserId,null,remove_tag);
                            if (!Check.Null(error)){
                                DCP_ISVWeComCustomTagUpdateRes.ExternalUserId externalUserIdRes = res.new ExternalUserId();
                                externalUserIdRes.setExternalUserId(externalUserId);
                                externalUserIdRes.setExternalUserName(name);
                                externalUserIdRes.setErrorMsg(error);
                                datas.getExternalUserIdList().add(externalUserIdRes);
                                isError = true;
                                break;
                            }else {


                                //删除 DCP_ISVWECOM_CUSTOM_TAG
                                DelBean db = new DelBean("DCP_ISVWECOM_CUSTOM_TAG");
                                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                db.addCondition("EXTERNALUSERID", new DataValue(externalUserId, Types.VARCHAR));
                                db.addCondition("TAGID", new DataValue(tag.getTagId(), Types.VARCHAR));
                                db.addCondition("USERID", new DataValue(userid, Types.VARCHAR));

                                this.addProcessData(new DataProcessBean(db));

                                this.doExecuteDataToDB();   //保证和企微一致，直接提交


                            }
                        }
                        if (isError){
                            break;
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComCustomTagUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComCustomTagUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComCustomTagUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComCustomTagUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            if (Check.Null(req.getRequest().getType())){
                errMsg.append("类型不能为空,");
                isFail = true;
            }
            List<ExternalUserId> externalUserIdList = req.getRequest().getExternalUserIdList();
            List<Tag> tagList = req.getRequest().getTagList();

            if (CollectionUtil.isNotEmpty(externalUserIdList)){
                for (ExternalUserId par:externalUserIdList){
                    if (Check.Null(par.getExternalUserId())){
                        errMsg.append("外部客户ID不能为空,");
                        isFail = true;
                    }
                    if (isFail) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }
            }

            if (CollectionUtil.isEmpty(tagList)){
                errMsg.append("标签列表不能为空,");
                isFail = true;
            }else{
                for (Tag par:tagList){
                    if (Check.Null(par.getTagId())){
                        errMsg.append("标签ID不能为空,");
                        isFail = true;
                    }
                    if (isFail) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
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
    protected TypeToken<DCP_ISVWeComCustomTagUpdateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComCustomTagUpdateReq>(){};
    }

    @Override
    protected DCP_ISVWeComCustomTagUpdateRes getResponseType() {
        return new DCP_ISVWeComCustomTagUpdateRes();
    }
}
