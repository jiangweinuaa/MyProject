package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComTagQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComTagQueryRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComTagQueryRes.Group;
import com.dsc.spos.json.cust.res.DCP_ISVWeComTagQueryRes.Tag;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_ISVWeComTagQuery
 * 服务说明：企微标签查询
 * @author jinzma
 * @since  2023-09-14
 */
public class DCP_ISVWeComTagQuery extends SPosBasicService<DCP_ISVWeComTagQueryReq, DCP_ISVWeComTagQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComTagQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ISVWeComTagQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComTagQueryReq>(){};
    }
    
    @Override
    protected DCP_ISVWeComTagQueryRes getResponseType() {
        return new DCP_ISVWeComTagQueryRes();
    }
    
    @Override
    protected DCP_ISVWeComTagQueryRes processJson(DCP_ISVWeComTagQueryReq req) throws Exception {
        DCP_ISVWeComTagQueryRes res = this.getResponse();
        List<Group> datas = new ArrayList<>();
        try{
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getQData)) {
                //GROUPID 去重
                List<Map<String, Object>> groupList = getQData.stream().collect(
                        Collectors.collectingAndThen(
                                Collectors.toCollection(()-> new TreeSet<>(
                                        Comparator.comparing(p->p.get("GROUPID").toString()))
                                ),ArrayList::new));
                //去重以后需要重新排序
                groupList = groupList.stream().sorted(
                        Comparator.comparing(p->p.get("GROUPORDER").toString())
                ).collect(Collectors.toList());
                
                for (Map<String, Object> oneData : groupList) {
                    Group group = res.new Group();
                    group.setGroupId(oneData.get("GROUPID").toString());
                    group.setGroupName(oneData.get("GROUPNAME").toString());
                    group.setGroupOrder(oneData.get("GROUPORDER").toString());
                    
                    group.setTagList(new ArrayList<>());
                    //过滤所属标签组的标签
                    List<Map<String, Object>> tagList = getQData.stream().filter(
                            p->p.get("GROUPID").toString().equals(oneData.get("GROUPID").toString())
                    ).collect(Collectors.toList());
                    for (Map<String, Object> oneTag : tagList) {
                        Tag tag = res.new Tag();
                        tag.setTagId(oneTag.get("TAGID").toString());
                        tag.setTagName(oneTag.get("TAGNAME").toString());
                        tag.setTagOrder(oneTag.get("TAGORDER").toString());
                        
                        group.getTagList().add(tag);
                    }
                    datas.add(group);
                }
            }
            
            res.setDatas(datas);
            return res;
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_ISVWeComTagQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        sqlbuf.append(" select a.groupid,a.groupname,a.grouporder,b.tagid,b.tagname,b.tagorder"
                + " from dcp_isvwecom_taggroup a"
                + " inner join dcp_isvwecom_tag b on a.eid=b.eid and a.groupid=b.groupid"
                + " where a.eid='"+eId+"'");
        if (!Check.Null(keyTxt)){
            sqlbuf.append(" and (a.groupid like '%"+keyTxt+"%' or a.groupname like '%"+keyTxt+"%' or b.tagid like '%"+keyTxt+"%' or b.tagname like '%"+keyTxt+"%')");
        }
        sqlbuf.append(" order by a.grouporder,b.tagorder");
        
        return sqlbuf.toString();
    }
}
