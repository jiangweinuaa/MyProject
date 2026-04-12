package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComMomentQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMomentQueryRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMomentQueryRes.*;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_ISVWeComMomentQuery
 * 服务说明：朋友圈任务查询
 * @author jinzma
 * @since  2024-03-06
 */
public class DCP_ISVWeComMomentQuery extends SPosBasicService<DCP_ISVWeComMomentQueryReq, DCP_ISVWeComMomentQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMomentQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ISVWeComMomentQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMomentQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComMomentQueryRes getResponseType() {
        return new DCP_ISVWeComMomentQueryRes();
    }

    @Override
    protected DCP_ISVWeComMomentQueryRes processJson(DCP_ISVWeComMomentQueryReq req) throws Exception {
        DCP_ISVWeComMomentQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        try {
            String eId = req.geteId();
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            int totalRecords=0;								//总笔数
            int totalPages=0;									//总页数
            if (!CollectionUtils.isEmpty(getQData)){
                //图片地址
                String ISHTTPS = PosPub.getPARA_SMS(dao,eId,"","ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";
                String DomainName = PosPub.getPARA_SMS(dao,eId,"","DomainName");
                String imagePath = httpStr + DomainName + "/resource/image/";
                if (DomainName.endsWith("/")) {
                    imagePath = httpStr + DomainName + "resource/image/";
                }

                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                getQData = getQData.stream().distinct().collect(Collectors.toList());

                for (Map<String, Object> oneQData : getQData) {
                    Datas datas = res.new Datas();
                    String momentMsgId = oneQData.get("MOMENTMSGID").toString();

                    datas.setMomentMsgId(momentMsgId);
                    datas.setName(oneQData.get("NAME").toString());
                    datas.setRemark(oneQData.get("REMARK").toString());
                    datas.setMsg(oneQData.get("MSG").toString());
                    datas.setType(oneQData.get("TYPE").toString());
                    datas.setMomentId(oneQData.get("MOMENTID").toString());
                    datas.setJobId(oneQData.get("JOBID").toString());
                    datas.setStatus(oneQData.get("STATUS").toString());
                    datas.setCreateTime(oneQData.get("CREATETIME").toString());
                    datas.setLastModiTime(oneQData.get("LASTMODITIME").toString());

                    datas.setTagList(new ArrayList<>());
                    sql = " select a.tagid,b.tagname from dcp_isvwecom_moment_tag a"
                            + " left join dcp_isvwecom_tag b on a.eid=b.eid and a.tagid = b.tagid"
                            + " where a.eid='"+eId+"' and a.momentmsgid='"+momentMsgId+"' "
                            + " order by a.tagid ";
                    List<Map<String, Object>> getTagQData=this.doQueryData(sql, null);
                    getTagQData = getTagQData.stream().distinct().collect(Collectors.toList());
                    for (Map<String, Object> tagData : getTagQData) {
                        Tag tag = res.new Tag();
                        tag.setTagId(tagData.get("TAGID").toString());
                        tag.setTagName(tagData.get("TAGNAME").toString());

                        datas.getTagList().add(tag);
                    }

                    datas.setUserList(new ArrayList<>());
                    sql = " select a.userid,b.opname from dcp_isvwecom_moment_user a"
                            + " left join dcp_isvwecom_staffs b on a.eid=b.eid and a.userid=b.userid"
                            + " where a.eid='"+eId+"' and a.momentmsgid='"+momentMsgId+"' "
                            + " order by a.userid ";
                    List<Map<String, Object>> getUserQData=this.doQueryData(sql, null);
                    for (Map<String, Object> userData : getUserQData) {
                        User user = res.new User();
                        user.setUserId(userData.get("USERID").toString());
                        user.setUserName(userData.get("OPNAME").toString());

                        datas.getUserList().add(user);
                    }

                    datas.setAnnexList(new ArrayList<>());
                    sql = " select a.* from dcp_isvwecom_moment_annex a "
                            + " where a.eid='"+eId+"' and a.momentmsgid='"+momentMsgId+"' "
                            + " order by a.serialno";
                    List<Map<String, Object>> getAnnexQData=this.doQueryData(sql, null);
                    for (Map<String, Object> annexData : getAnnexQData) {
                        Annex annex = res.new Annex();
                        annex.setSerialNo(annexData.get("SERIALNO").toString());
                        annex.setMsgType(annexData.get("MSGTYPE").toString());
                        annex.setMsgId(annexData.get("MSGID").toString());

                        annex.setLinkDescription(annexData.get("LINKDESCRIPTION").toString());       // 链接摘要
                        annex.setLinkUrl(annexData.get("LINKURL").toString());                       // 链接地址
                        annex.setLinkMediaId(annexData.get("LINKMEDIAID").toString());               // 链接封面图片文件名
                        annex.setLinkMediaUrl(annexData.get("LINKMEDIAURL").toString());             // 链接封面图片url地址
                        annex.setLinkTitle(annexData.get("LINKTITLE").toString());                   // 链接标题
                        annex.setLinkPicMediaId(annexData.get("LINKPICMEDIAID").toString());         // 链接封面图片上传后获取的唯一标识3天内有效

                        annex.setMiniAppId(annexData.get("MINIAPPID").toString());                   // 小程序appid
                        annex.setMiniUrl(annexData.get("MINIURL").toString());                       // 小程序url路径
                        annex.setMiniMediaId(annexData.get("MINIMEDIAID").toString());               // 小程序封面图片文件名
                        annex.setMiniMediaUrl(imagePath+annexData.get("MINIMEDIAID").toString());    // 小程序封面图片URL (取系统参数拼接)
                        annex.setMiniPicMediaId(annexData.get("MINIPICMEDIAID").toString());         // 小程序封面图片上传后获取的唯一标识3天内有效
                        annex.setMiniTitle(annexData.get("MINITITLE").toString());                   // 小程序标题

                        annex.setImageMediaId(annexData.get("IMAGEMEDIAID").toString());             // 图片文件名
                        annex.setImageMediaUrl(imagePath+annexData.get("IMAGEMEDIAID").toString());  // 图片URL (取系统参数拼接)
                        annex.setImageWeComPicUrl(annexData.get("IMAGEWECOMPICURL").toString());     // 图片永久链接
                        annex.setImagePicMediaId(annexData.get("IMAGEPICMEDIAID").toString());       // 图片上传后获取的唯一标识3天内有效


                        datas.getAnnexList().add(annex);
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
    protected String getQuerySql(DCP_ISVWeComMomentQueryReq req) throws Exception {

        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String userId = req.getRequest().getUserId();
        String tagId = req.getRequest().getTagId();
        String status = req.getRequest().getStatus();
        String type = req.getRequest().getType();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();

        StringBuffer sqlBuf = new StringBuffer();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * req.getPageSize();

        sqlBuf.append(" select * from ("
                + " select count(distinct a.momentmsgid) over() num,"
                + " dense_rank() over(order by a.createtime desc,a.momentmsgid) as rn,"
                + " a.momentmsgid,a.name,a.msg,a.remark,a.type,a.momentid,a.jobid,a.status,"
                + " to_char(a.createtime,'yyyy-mm-dd hh24:mi:ss') as createtime,"
                + " to_char(a.lastmoditime,'yyyy-mm-dd hh24:mi:ss') as lastmoditime"
                + " from dcp_isvwecom_moment a"
                + " left join dcp_isvwecom_moment_user b on a.eid=b.eid and a.momentmsgid=b.momentmsgid"
                + " left join dcp_isvwecom_staffs b1 on a.eid=b1.eid and b.userid=b1.userid"
                + " left join dcp_isvwecom_moment_tag c on a.eid=b1.eid and a.momentmsgid=c.momentmsgid"
                + " left join dcp_isvwecom_tag c1 on a.eid=c1.eid and c.tagid=c1.tagid"
                + " where a.eid='"+eId+"'"
                + " ");

        if (!Check.Null(userId)){
            sqlBuf.append(" and b.userid='"+userId+"'");
        }

        if (!Check.Null(tagId)){
            sqlBuf.append(" and c.tagid='"+tagId+"'");
        }

        if (!Check.Null(status)){
            sqlBuf.append(" and a.status='"+status+"'");
        }

        if (!Check.Null(type)){
            sqlBuf.append(" and a.type='"+type+"'");
        }

        if (!Check.Null(keyTxt)){
            sqlBuf.append(" and (a.name like '%"+keyTxt+"%' or b1.opname like '%"+keyTxt+"%' or c1.tagname like '%"+keyTxt+"%')");
        }

        if (!Check.Null(beginDate) && !Check.Null(endDate)){
            sqlBuf.append(" and a.createtime>=to_date('"+beginDate+" 00:00:01','yyyy-MM-dd HH24:mi:ss') ");
            sqlBuf.append(" and a.createtime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd HH24:mi:ss') ");
        }

        sqlBuf.append(" ) a");
        sqlBuf.append(" where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" ");
        sqlBuf.append(" order by a.rn ");

        return sqlBuf.toString();


    }
}
