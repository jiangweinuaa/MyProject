package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComTagDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComTagDeleteRes;
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
 * 服务函数：DCP_ISVWeComTagDelete
 * 服务说明：企微标签删除
 * @author jinzma
 * @since  2023-09-14
 */
public class DCP_ISVWeComTagDelete extends SPosAdvanceService<DCP_ISVWeComTagDeleteReq, DCP_ISVWeComTagDeleteRes> {
    @Override
    protected void processDUID(DCP_ISVWeComTagDeleteReq req, DCP_ISVWeComTagDeleteRes res) throws Exception {
        try{
            String eId = req.geteId();
            String groupId = req.getRequest().getGroupId();
            String tagId = req.getRequest().getTagId();
            String tagId_groupId = "";  //标签对应的标签组
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            JSONObject reqObject = new JSONObject();
            //删除标签组
            if (!Check.Null(groupId)){
                String sql = " select groupid from dcp_isvwecom_taggroup where groupid='" + groupId + "'";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtils.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "标签组ID不存在,无法删除");
                }
               
                reqObject.put("group_id", groupId);
            }
            //删除标签
            if (!Check.Null(tagId)){
                String sql = " select groupid from dcp_isvwecom_tag where tagid='"+tagId+"'";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtils.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "标签ID不存在,无法删除");
                }else{
                    tagId_groupId = getQData.get(0).get("GROUPID").toString();
                }
                reqObject.put("tag_id", tagId);
            }
            
            //调企微接口
            JSONObject resObject = dcpWeComUtils.delCorpTag(dao,reqObject);
            if (resObject==null){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业微信删除客户标签失败,详细原因请查询日志");
            }
            int errcode = resObject.optInt("errcode");
            String errmsg = resObject.optString("errmsg");
            //无权限操作标签
            if (errcode==81011){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业微信管理端创建的标签无法删除");
            }
            if (!"ok".equals(errmsg)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业微信删除客户标签失败,详细原因请查询日志");
            }
         
            //删除标签组资料
            if (!Check.Null(groupId)){
                DelBean db1 = new DelBean("DCP_ISVWECOM_TAGGROUP");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("GROUPID", new DataValue(groupId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
                
                DelBean db2 = new DelBean("DCP_ISVWECOM_TAG");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("GROUPID", new DataValue(groupId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));
            }
            
            //删除标签
            if (!Check.Null(tagId)){
                DelBean db2 = new DelBean("DCP_ISVWECOM_TAG");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("TAGID", new DataValue(tagId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));
                
                //标签如果只剩下一个，需要删除标签对应的标签组
                String sql = " select groupid from dcp_isvwecom_tag where groupid='"+tagId_groupId+"'";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (getQData.size()==1) {
                    //如果一个标签组下所有的标签均被删除，则标签组会被自动删除。
                    DelBean db1 = new DelBean("DCP_ISVWECOM_TAGGROUP");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("GROUPID", new DataValue(tagId_groupId,Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComTagDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComTagDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComTagDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComTagDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            if (!Check.Null(req.getRequest().getGroupId()) && !Check.Null(req.getRequest().getTagId())) {
                errMsg.append("标签组和标签不支持同时删除,");
                isFail = true;
            }
            
            if (Check.Null(req.getRequest().getGroupId()) && Check.Null(req.getRequest().getTagId())) {
                errMsg.append("标签组或标签不能同时为空,");
                isFail = true;
            }
            
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ISVWeComTagDeleteReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComTagDeleteReq>(){};
    }
    
    @Override
    protected DCP_ISVWeComTagDeleteRes getResponseType() {
        return new DCP_ISVWeComTagDeleteRes();
    }
}
