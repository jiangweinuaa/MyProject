package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.json.cust.req.DCP_ISVWeComCustomQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComCustomQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_ISVWeComCustomQuery
 * 服务说明：查询企微客户列表
 * @author jinzma
 * @since  2024-01-24
 */
public class DCP_ISVWeComCustomQuery extends SPosBasicService<DCP_ISVWeComCustomQueryReq, DCP_ISVWeComCustomQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComCustomQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            List<DCP_ISVWeComCustomQueryReq.Tag> tagList = req.getRequest().getTagList();
            if (CollectionUtil.isNotEmpty(tagList)){
                for (DCP_ISVWeComCustomQueryReq.Tag tag:tagList){
                    if (Check.Null(tag.getTagId())){
                        errMsg.append("tagId 不能为空,");
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
    protected TypeToken<DCP_ISVWeComCustomQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComCustomQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComCustomQueryRes getResponseType() {
        return new DCP_ISVWeComCustomQueryRes();
    }

    @Override
    protected DCP_ISVWeComCustomQueryRes processJson(DCP_ISVWeComCustomQueryReq req) throws Exception {
        DCP_ISVWeComCustomQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        try{
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            int totalRecords=0;								//总笔数
            int totalPages=0;								//总页数
            if (!CollectionUtils.isEmpty(getQData)){
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                //EXTERNALUSERID 去重
                List<Map<String, Object>> externalUserList = getQData.stream().collect(
                        Collectors.collectingAndThen(
                                Collectors.toCollection(()-> new TreeSet<>(
                                        Comparator.comparing(p->p.get("EXTERNALUSERID").toString()))
                                ),ArrayList::new));
                //去重以后需要重新排序
                externalUserList = externalUserList.stream().sorted(
                        Comparator.comparing(p->p.get("RN").toString())
                ).collect(Collectors.toList());

                for (Map<String, Object> oneData : externalUserList) {
                    DCP_ISVWeComCustomQueryRes.Datas datas = res.new Datas();
                    datas.setExternalUserId(oneData.get("EXTERNALUSERID").toString());
                    datas.setName(oneData.get("NAME").toString());
                    datas.setStatus(oneData.get("STATUS").toString());
                    datas.setLossTime(oneData.get("LOSSTIME").toString());
                    datas.setTagList(new ArrayList<>());

                    //过滤所属标签组的标签
                    List<Map<String, Object>> tagList = getQData.stream().filter(
                            p->p.get("EXTERNALUSERID").toString().equals(oneData.get("EXTERNALUSERID").toString())
                    ).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(tagList)){
                        for (Map<String, Object> oneTag : tagList) {
                            DCP_ISVWeComCustomQueryRes.Tag tag = res.new Tag();
                            tag.setTagId(oneTag.get("TAGID").toString());
                            tag.setTagName(oneTag.get("TAGNAME").toString());

                            datas.getTagList().add(tag);
                        }
                    }

                    res.getDatas().add(datas);
                }
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVWeComCustomQueryReq req) throws Exception {
        StringBuffer sqlBuf=new StringBuffer();
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String mobile = req.getRequest().getMobile();
        String userId = req.getRequest().getUserId();
        String status = req.getRequest().getStatus();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        List<DCP_ISVWeComCustomQueryReq.Tag> tagList =req.getRequest().getTagList();
        String tags = "";   //'etONs8DQAAxrL6hg5_l6pEXHwBNJSG-A','etONs8DQAAcTKe6ej42KreYHKwDLjp2A'
        if (CollectionUtil.isNotEmpty(tagList)){
            for (DCP_ISVWeComCustomQueryReq.Tag tag:tagList){
                tags = tags + "'"+tag.getTagId()+"'," ;
            }
            tags = tags.substring(0,tags.length()-1);
        }

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * req.getPageSize();

        sqlBuf.append(" select a.*,b.tagid,c.tagname"
                + " from ("
                + " select row_number() over (order by a.followtime desc) as rn,"
                + " to_char(a.followtime,'yyyy-MM-dd HH24:mi:ss') as followtime,"
                + " to_char(a.losstime,'yyyy-MM-dd HH24:mi:ss') as losstime,"
                + " a.num,a.eid,a.externaluserid,a.name,a.status,a.userid"
                + " from ("
                + " select count(distinct a.externaluserid) over() num,"
                + " row_number() over (partition by a.externaluserid order by a.externaluserid) as rn,"
                + " a.eid,a.externaluserid,a.name,a.status,a.losstime,b.userid,d.tagid,b.followtime"
                + " from dcp_isvwecom_custom a"
                + " left join dcp_isvwecom_custom_follow b on a.eid=b.eid and a.externaluserid=b.externaluserid"
                + " left join dcp_isvwecom_custom_tag d on a.eid=d.eid and a.externaluserid=d.externaluserid");
        if (!Check.Null(mobile)){
            sqlBuf.append(" inner join dcp_isvwecom_custom_mobile f on a.eid=f.eid and a.externaluserid=f.externaluserid and f.remarkmobile='"+mobile+"' ");
        }

        sqlBuf.append(" where a.eid='"+eId+"' ");

        if (!Check.Null(keyTxt)){
            sqlBuf.append(" and (a.externaluserid like '%"+keyTxt+"%' or a.name like '%"+keyTxt+"%')");
        }
        if (!Check.Null(tags)){
            sqlBuf.append(" and d.tagid in ("+tags+") ");
        }
        if (!Check.Null(userId)){
            sqlBuf.append(" and b.userid='"+userId+"' ");
        }
        if (!Check.Null(status)){
            sqlBuf.append(" and a.status='"+status+"' ");
        }

        if (!Check.Null(beginDate) && !Check.Null(endDate)) {
            if ("0".equals(status)) {
                sqlBuf.append(" and a.losstime>=to_date('" + beginDate + " 00:00:01','yyyy-MM-dd HH24:mi:ss') and a.losstime<=to_date('" + endDate + " 23:59:59','yyyy-MM-dd HH24:mi:ss')  ");
            } else {
                sqlBuf.append(" and b.followtime>=to_date('" + beginDate + " 00:00:01','yyyy-MM-dd HH24:mi:ss') and b.followtime<=to_date('" + endDate + " 23:59:59','yyyy-MM-dd HH24:mi:ss')  ");
            }
        }
        sqlBuf.append(" ) a where a.rn=1 ");
        sqlBuf.append(" ) a ");
        sqlBuf.append(" left join dcp_isvwecom_custom_tag b on a.eid=b.eid and a.externaluserid=b.externaluserid");
        sqlBuf.append(" left join (select distinct tagid,tagname from dcp_isvwecom_tag where eid='"+eId+"')c on b.tagid=c.tagid");
        sqlBuf.append(" where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" ");
        sqlBuf.append(" order by a.rn ");

        return sqlBuf.toString();

    }
}
