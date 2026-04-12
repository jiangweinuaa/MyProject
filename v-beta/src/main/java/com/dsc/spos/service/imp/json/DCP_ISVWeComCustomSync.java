package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComCustomSyncReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComCustomSyncRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.ExternaContactBatch;
import com.dsc.spos.thirdpart.wecom.entity.ExternaContactBatch.*;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务函数：DCP_ISVWeComCustomSync
 * 服务说明：企微客户同步
 * @author jinzma
 * @since  2024-01-23
 */
public class DCP_ISVWeComCustomSync extends SPosAdvanceService<DCP_ISVWeComCustomSyncReq, DCP_ISVWeComCustomSyncRes> {
    @Override
    protected void processDUID(DCP_ISVWeComCustomSyncReq req, DCP_ISVWeComCustomSyncRes res) throws Exception {
        try{
            String eId = req.geteId();
            List<String> userid_list = new ArrayList<>();
            List<String> userid_add = new ArrayList<>();   //新增客户

            //查找客服ID
            String sql = " select userid from dcp_isvwecom_staffs where eid='"+eId+"' and accounttype='2' and userid is not null  ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (CollectionUtil.isNotEmpty(getQData)){
                for (Map<String, Object> oneQDate:getQData){
                    userid_list.add(oneQDate.get("USERID").toString());
                }
                String [] userid = userid_list.toArray(new String[0]);
                //调企微（批量获取客户详情）
                DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                ExternaContactBatch externalContactBatch = dcpWeComUtils.getExternalContactBatch(dao,userid);
                if (externalContactBatch != null){
                    List<External_contact_list> external_contact_list = externalContactBatch.getExternal_contact_list();
                    if (CollectionUtil.isNotEmpty(external_contact_list)) {

                        //所有生效的status统一调整为-1
                        String sql1 = " update dcp_isvwecom_custom set status='-1'  where eid='"+eId+"' and status='100'";
                        ExecBean execBean1 = new ExecBean(sql1);
                        this.addProcessData(new DataProcessBean(execBean1));


                        for (External_contact_list par:external_contact_list){
                            External_contact external_contact = par.getExternal_contact();
                            Follow_info follow_info = par.getFollow_info();
                            String external_userid = external_contact.getExternal_userid();

                            if (userid_add.stream().noneMatch(x->x.equals(external_userid))){
                                userid_add.add(external_userid);
                                externalDelete(eId,external_userid);              //同步企微的资料，本地全部删除
                                externalAdd(eId,external_contact,follow_info);    //新增客户
                            } else {
                                //之前已经新增过了，只新增单身资料
                                externalUpdate(eId,external_contact,follow_info);
                            }
                        }

                        this.doExecuteDataToDB();

                        //原本是生效的但本次未同步到企微的统一调整为0
                        String sql3 = " update dcp_isvwecom_custom set status='0',losstime=sysdate  where eid='"+eId+"' and status='-1'";
                        ExecBean execBean3 = new ExecBean(sql3);
                        this.addProcessData(new DataProcessBean(execBean3));


                        this.doExecuteDataToDB();

                    }
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComCustomSyncReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComCustomSyncReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComCustomSyncReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComCustomSyncReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComCustomSyncReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComCustomSyncReq>(){};
    }

    @Override
    protected DCP_ISVWeComCustomSyncRes getResponseType() {
        return new DCP_ISVWeComCustomSyncRes();
    }

    private void externalAdd(String eId,External_contact external_contact,Follow_info follow_info) {

        String[] columns1 = {"EID","EXTERNALUSERID","NAME","AVATAR","CUSTOMTYPE","GENDER","POSITION","STATUS"};
        String[] columns2 = {"EID","EXTERNALUSERID","TAGID","USERID"};
        String[] columns3 = {"EID","EXTERNALUSERID","REMARKMOBILE","USERID"};
        String[] columns4 = {"EID","EXTERNALUSERID","USERID","FOLLOWREMARK","FOLLOWDESCRIP","FOLLOWTIME","ADDWAY","OPERUSERID","STATE"};

        //新增 DCP_ISVWECOM_CUSTOM
        {
            DataValue[] insValue1 = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(external_contact.getExternal_userid(), Types.VARCHAR),
                    new DataValue(external_contact.getName(), Types.VARCHAR),
                    new DataValue(external_contact.getAvatar(), Types.VARCHAR),
                    new DataValue(external_contact.getType(), Types.VARCHAR),
                    new DataValue(external_contact.getGender(), Types.VARCHAR),
                    new DataValue(external_contact.getPosition(), Types.VARCHAR),
                    new DataValue("100", Types.VARCHAR),     //status 查到一定是100
            };
            InsBean ib1 = new InsBean("DCP_ISVWECOM_CUSTOM", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
        }

        //新增 DCP_ISVWECOM_CUSTOM_TAG
        {
            if (follow_info !=null) {
                String [] tagIds = follow_info.getTag_id();
                if(tagIds != null){
                    for (String tagId:tagIds) {
                        DataValue[] insValue2 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(external_contact.getExternal_userid(), Types.VARCHAR),
                                new DataValue(tagId, Types.VARCHAR),
                                new DataValue(follow_info.getUserid(), Types.VARCHAR),
                        };
                        InsBean ib2 = new InsBean("DCP_ISVWECOM_CUSTOM_TAG", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }
            }
        }

        //新增 DCP_ISVWECOM_CUSTOM_MOBILE
        {
            if (follow_info !=null) {
                String [] remark_mobiles = follow_info.getRemark_mobiles();
                if(remark_mobiles != null){
                    for (String remark_mobile:remark_mobiles) {
                        DataValue[] insValue3 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(external_contact.getExternal_userid(), Types.VARCHAR),
                                new DataValue(remark_mobile, Types.VARCHAR),
                                new DataValue(follow_info.getUserid(), Types.VARCHAR),
                        };
                        InsBean ib3 = new InsBean("DCP_ISVWECOM_CUSTOM_MOBILE", columns3);
                        ib3.addValues(insValue3);
                        this.addProcessData(new DataProcessBean(ib3));
                    }
                }
            }
        }

        //新增 DCP_ISVWECOM_CUSTOM_FOLLOW
        {
            if (follow_info !=null) {
                DataValue[] insValue4 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(external_contact.getExternal_userid(), Types.VARCHAR),
                        new DataValue(follow_info.getUserid(), Types.VARCHAR),
                        new DataValue(follow_info.getRemark(), Types.VARCHAR),
                        new DataValue(follow_info.getDescription(), Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(follow_info.getCreatetime()), Types.DATE),
                        new DataValue(follow_info.getAdd_way(), Types.VARCHAR),
                        new DataValue(follow_info.getOper_userid(), Types.VARCHAR),
                        new DataValue(follow_info.getState(), Types.VARCHAR),
                };
                InsBean ib4 = new InsBean("DCP_ISVWECOM_CUSTOM_FOLLOW", columns4);
                ib4.addValues(insValue4);
                this.addProcessData(new DataProcessBean(ib4));
            }
        }

    }

    private void externalDelete(String eId,String external_userid){

        //删除 DCP_ISVWECOM_CUSTOM
        DelBean db1 = new DelBean("DCP_ISVWECOM_CUSTOM");
        db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db1.addCondition("EXTERNALUSERID", new DataValue(external_userid,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        //删除 DCP_ISVWECOM_CUSTOM_TAG
        DelBean db2 = new DelBean("DCP_ISVWECOM_CUSTOM_TAG");
        db2.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db2.addCondition("EXTERNALUSERID", new DataValue(external_userid,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        //删除 DCP_ISVWECOM_CUSTOM_MOBILE
        DelBean db3 = new DelBean("DCP_ISVWECOM_CUSTOM_MOBILE");
        db3.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db3.addCondition("EXTERNALUSERID", new DataValue(external_userid,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        //删除 DCP_ISVWECOM_CUSTOM_FOLLOW
        DelBean db4 = new DelBean("DCP_ISVWECOM_CUSTOM_FOLLOW");
        db4.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db4.addCondition("EXTERNALUSERID", new DataValue(external_userid,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db4));

    }

    private void externalUpdate(String eId,External_contact external_contact,Follow_info follow_info){

        //String[] columns1 = {"EID","EXTERNALUSERID","NAME","AVATAR","CUSTOMTYPE","GENDER","UNIONID","POSITION","STATUS"};
        String[] columns2 = {"EID","EXTERNALUSERID","TAGID","USERID"};
        String[] columns3 = {"EID","EXTERNALUSERID","REMARKMOBILE","USERID"};
        String[] columns4 = {"EID","EXTERNALUSERID","USERID","FOLLOWREMARK","FOLLOWDESCRIP","FOLLOWTIME","ADDWAY","OPERUSERID","STATE"};


        //新增 DCP_ISVWECOM_CUSTOM
        /*{
            DataValue[] insValue1 = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(external_contact.getExternal_userid(), Types.VARCHAR),
                    new DataValue(external_contact.getName(), Types.VARCHAR),
                    new DataValue(external_contact.getAvatar(), Types.VARCHAR),
                    new DataValue(external_contact.getType(), Types.VARCHAR),
                    new DataValue(external_contact.getGender(), Types.VARCHAR),
                    new DataValue(external_contact.getUnionid(), Types.VARCHAR),
                    new DataValue(external_contact.getPosition(), Types.VARCHAR),
                    new DataValue("100", Types.VARCHAR),     //status 查到一定是100
            };
            InsBean ib1 = new InsBean("DCP_ISVWECOM_CUSTOM", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
        }*/

        //新增 DCP_ISVWECOM_CUSTOM_TAG
        {
            if (follow_info !=null) {
                String [] tagIds = follow_info.getTag_id();
                if(tagIds != null){
                    for (String tagId:tagIds) {
                        DataValue[] insValue2 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(external_contact.getExternal_userid(), Types.VARCHAR),
                                new DataValue(tagId, Types.VARCHAR),
                                new DataValue(follow_info.getUserid(), Types.VARCHAR),
                        };
                        InsBean ib2 = new InsBean("DCP_ISVWECOM_CUSTOM_TAG", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }
            }
        }

        //新增 DCP_ISVWECOM_CUSTOM_MOBILE
        {
            if (follow_info !=null) {
                String [] remark_mobiles = follow_info.getRemark_mobiles();
                if(remark_mobiles != null){
                    for (String  remark_mobile:remark_mobiles) {
                        DataValue[] insValue3 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(external_contact.getExternal_userid(), Types.VARCHAR),
                                new DataValue(remark_mobile, Types.VARCHAR),
                                new DataValue(follow_info.getUserid(), Types.VARCHAR),
                        };
                        InsBean ib3 = new InsBean("DCP_ISVWECOM_CUSTOM_MOBILE", columns3);
                        ib3.addValues(insValue3);
                        this.addProcessData(new DataProcessBean(ib3));
                    }
                }
            }
        }

        //新增 DCP_ISVWECOM_CUSTOM_FOLLOW
        {
            if (follow_info !=null) {
                DataValue[] insValue4 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(external_contact.getExternal_userid(), Types.VARCHAR),
                        new DataValue(follow_info.getUserid(), Types.VARCHAR),
                        new DataValue(follow_info.getRemark(), Types.VARCHAR),
                        new DataValue(follow_info.getDescription(), Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(follow_info.getCreatetime()), Types.DATE),
                        new DataValue(follow_info.getAdd_way(), Types.VARCHAR),
                        new DataValue(follow_info.getOper_userid(), Types.VARCHAR),
                        new DataValue(follow_info.getState(), Types.VARCHAR),
                };
                InsBean ib4 = new InsBean("DCP_ISVWECOM_CUSTOM_FOLLOW", columns4);
                ib4.addValues(insValue4);
                this.addProcessData(new DataProcessBean(ib4));
            }
        }


    }

}
