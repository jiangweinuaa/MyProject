package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComTagCreateReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComTagCreateReq.Tag;
import com.dsc.spos.json.cust.res.DCP_ISVWeComTagCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComTagCreate
 * 服务说明：企微标签创建
 * @author jinzma
 * @since  2023-09-14
 */
public class DCP_ISVWeComTagCreate extends SPosAdvanceService<DCP_ISVWeComTagCreateReq, DCP_ISVWeComTagCreateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComTagCreateReq req, DCP_ISVWeComTagCreateRes res) throws Exception {
        try{
            String eId = req.geteId();
            String groupId = req.getRequest().getGroupId();
            String groupName = req.getRequest().getGroupName();
            List<Tag> tagList = req.getRequest().getTagList();
            DCPWeComUtils dcpIsvWeComUtils = new DCPWeComUtils();
            //资料检查
            for (Tag tag:tagList){
                String sql = " select tagname from dcp_isvwecom_tag where tagname='"+tag.getTagName()+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "标签名称:"+tag.getTagName()+" 已存在,无法新增");
                }
            }
            
            //标签处理
            JSONObject reqObject = new JSONObject();
            JSONArray reqArray = new JSONArray();
            if (Check.Null(groupId)){
                //新增标签组和标签
                String sql = " select groupName from dcp_isvwecom_taggroup where groupname='"+groupName+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "标签组名称:"+groupName+" 已存在,无法新增");
                }
                
                for (Tag tag:tagList){
                    JSONObject jsonTag = new JSONObject();
                    jsonTag.put("name", tag.getTagName());
                    reqArray.put(jsonTag);
                }
                reqObject.put("group_name", groupName);
                reqObject.put("tag", reqArray);
                
            }else{
                //标签组存在仅新增标签
                String sql = " select groupid from dcp_isvwecom_taggroup where groupid='"+groupId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtils.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "标签组ID:"+groupId+" 不存在,无法新增");
                }
                for (Tag tag:tagList){
                    JSONObject jsonTag = new JSONObject();
                    jsonTag.put("name", tag.getTagName());
                    reqArray.put(jsonTag);
                }
                reqObject.put("group_id", groupId);
                reqObject.put("tag", reqArray);
            }


            JSONObject resObject = dcpIsvWeComUtils.addCorpTag(dao,reqObject);
            if (resObject == null) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业微信添加客户标签失败,详细原因请查询日志");
            }
            
            JSONObject tag_group = resObject.optJSONObject("tag_group");
            
            //新增 DCP_ISVWECOM_TAGGROUP;
            {
                //此处优化： 如果企微返回的ID已经存在，则不再新增
                String sql = " select groupid from dcp_isvwecom_taggroup where groupid='"+ tag_group.opt("group_id") +"' ";
                List<Map<String, Object>> getQGroup = this.doQueryData(sql, null);
                if (CollectionUtils.isEmpty(getQGroup)) {
                    String[] columns = {"EID", "GROUPID", "GROUPNAME", "GROUPORDER"};
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(tag_group.opt("group_id").toString(), Types.VARCHAR),
                            new DataValue(tag_group.opt("group_name").toString(), Types.VARCHAR),
                            new DataValue(tag_group.opt("order").toString(), Types.VARCHAR),
                    };
                    InsBean ib = new InsBean("DCP_ISVWECOM_TAGGROUP", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }
            
            //新增 DCP_ISVWECOM_TAG;
            {
                String[] columns = {"EID", "GROUPID", "TAGID", "TAGNAME","TAGORDER"};
                JSONArray tagArray = tag_group.optJSONArray("tag");
                for (int i = 0; i < tagArray.length(); i++) {
                    //此处优化： 如果企微返回的ID已经存在，则不再新增
                    String sql = " select tagid from dcp_isvwecom_tag "
                            + "where tagid='"+tagArray.getJSONObject(i).opt("id").toString()+"' ";
                    List<Map<String, Object>> getQTag = this.doQueryData(sql, null);
                    if (CollectionUtils.isEmpty(getQTag)) {
                        InsBean ib = new InsBean("DCP_ISVWECOM_TAG", columns);
                        DataValue[] insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(tag_group.opt("group_id").toString(), Types.VARCHAR),
                                new DataValue(tagArray.getJSONObject(i).opt("id").toString(), Types.VARCHAR),
                                new DataValue(tagArray.getJSONObject(i).opt("name").toString(), Types.VARCHAR),
                                new DataValue(tagArray.getJSONObject(i).opt("order").toString(), Types.VARCHAR),
                        };
                        ib.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib));
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComTagCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComTagCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComTagCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComTagCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            List<Tag> tagList = req.getRequest().getTagList();
            if (Check.Null(req.getRequest().getGroupName())){
                errMsg.append("标签组名称不能为空,");
                isFail = true;
            }
            if (CollectionUtils.isEmpty(tagList)){
                errMsg.append("标签列表不能为空,");
                isFail = true;
            }else{
                for (Tag tag:tagList){
                    if (Check.Null(tag.getTagName())){
                        errMsg.append("标签名称不能为空,");
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
    protected TypeToken<DCP_ISVWeComTagCreateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComTagCreateReq>(){};
    }
    
    @Override
    protected DCP_ISVWeComTagCreateRes getResponseType() {
        return new DCP_ISVWeComTagCreateRes();
    }
}
