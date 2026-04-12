package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComTagUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComTagUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComTagUpdate
 * 服务说明：企微标签更新
 * @author jinzma
 * @since  2023-09-14
 */
public class DCP_ISVWeComTagUpdate extends SPosAdvanceService<DCP_ISVWeComTagUpdateReq, DCP_ISVWeComTagUpdateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComTagUpdateReq req, DCP_ISVWeComTagUpdateRes res) throws Exception {
        try {
            //String eId = req.geteId();
            String groupId = req.getRequest().getGroupId();
            String groupName = req.getRequest().getGroupName();
            String tagId = req.getRequest().getTagId();
            String tagName = req.getRequest().getTagName();
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            JSONObject reqObject = new JSONObject();

            //资料检查
            {
                String sql = " select tagname from dcp_isvwecom_tag where tagname='" + tagName + "' and tagid<>'"+tagId+"'";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "标签名称:" + tagName + " 已存在,无法修改");
                }
            }

            //修改标签组
            if (!Check.Null(groupName)){
                String sql = " select groupid from dcp_isvwecom_taggroup where groupid='" + groupId + "'";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtils.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "标签组ID不存在,无法修改");
                }
                reqObject.put("id", groupId);
                reqObject.put("name", groupName);
            }
            //修改标签
            if (!Check.Null(tagName)){
                String sql = " select tagid from dcp_isvwecom_tag where tagid='"+tagId+"'";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtils.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "标签ID不存在,无法修改");
                }
                reqObject.put("id", tagId);
                reqObject.put("name", tagName);
            }
            
            //调企微接口
            JSONObject resObject = dcpWeComUtils.editCorpTag(dao,reqObject);
            if (resObject==null){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业微信修改客户标签失败,详细原因请查询日志");
            }
            int errcode = resObject.optInt("errcode");
            String errmsg = resObject.optString("errmsg");
            //无权限操作标签
            if (errcode==81011){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业微信管理端创建的标签无法修改");
            }
            if (!"ok".equals(errmsg)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业微信修改客户标签失败,详细原因请查询日志");
            }
            
            //修改标签组
            if (!Check.Null(groupName)) {
                UptBean ub = new UptBean("DCP_ISVWECOM_TAGGROUP");
                ub.addUpdateValue("GROUPNAME", new DataValue(groupName, Types.VARCHAR));
                //条件
                ub.addCondition("GROUPID", new DataValue(groupId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub));
            }
            
            //修改标签
            if (!Check.Null(tagName)) {
                UptBean ub = new UptBean("DCP_ISVWECOM_TAG");
                ub.addUpdateValue("TAGNAME", new DataValue(tagName, Types.VARCHAR));
                //条件
                ub.addCondition("TAGID", new DataValue(tagId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub));
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComTagUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComTagUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComTagUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComTagUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            if (!Check.Null(req.getRequest().getGroupName()) && !Check.Null(req.getRequest().getTagName())) {
                errMsg.append("标签组和标签不支持同时修改,");
                isFail = true;
            }
            
            if (Check.Null(req.getRequest().getGroupName()) && Check.Null(req.getRequest().getTagName())) {
                errMsg.append("标签组或标签名称不能为空,");
                isFail = true;
            }
            
            if (!Check.Null(req.getRequest().getGroupName())){
                if (Check.Null(req.getRequest().getGroupId())){
                    errMsg.append("标签组ID不能为空,");
                    isFail = true;
                }
            }
            
            if (!Check.Null(req.getRequest().getTagName())){
                if (Check.Null(req.getRequest().getTagId())){
                    errMsg.append("标签ID不能为空,");
                    isFail = true;
                }
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ISVWeComTagUpdateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComTagUpdateReq>(){};
    }
    
    @Override
    protected DCP_ISVWeComTagUpdateRes getResponseType() {
        return new DCP_ISVWeComTagUpdateRes();
    }
}
