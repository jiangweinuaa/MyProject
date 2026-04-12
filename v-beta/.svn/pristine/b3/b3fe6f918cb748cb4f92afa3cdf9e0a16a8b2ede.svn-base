package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComGpMsgQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpMsgQueryRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpMsgQueryRes.*;
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
 * 服务函数：DCP_ISVWeComGpMsgQuery
 * 服务说明：群发消息查询
 * @author jinzma
 * @since  2024-03-01
 */
public class DCP_ISVWeComGpMsgQuery extends SPosBasicService<DCP_ISVWeComGpMsgQueryReq, DCP_ISVWeComGpMsgQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGpMsgQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ISVWeComGpMsgQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGpMsgQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComGpMsgQueryRes getResponseType() {
        return new DCP_ISVWeComGpMsgQueryRes();
    }

    @Override
    protected DCP_ISVWeComGpMsgQueryRes processJson(DCP_ISVWeComGpMsgQueryReq req) throws Exception {
        DCP_ISVWeComGpMsgQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        try{
            String eId = req.geteId();
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            int totalRecords=0;								//总笔数
            int totalPages=0;									//总页数
            if (!CollectionUtils.isEmpty(getQData)) {
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                //图片地址
                String ISHTTPS = PosPub.getPARA_SMS(dao,eId,"","ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";
                String DomainName = PosPub.getPARA_SMS(dao,eId,"","DomainName");
                String imagePath = httpStr + DomainName + "/resource/image/";
                if (DomainName.endsWith("/")) {
                    imagePath = httpStr + DomainName + "resource/image/";
                }

                //distinct 单头资料
                getQData = getQData.stream().distinct().collect(Collectors.toList());

                for (Map<String, Object> oneData : getQData) {
                    Datas datas = res.new Datas();
                    String gpMsgId = oneData.get("GPMSGID").toString();

                    datas.setGpMsgId(gpMsgId);
                    datas.setName(oneData.get("NAME").toString());
                    datas.setMsg(oneData.get("MSG").toString());
                    datas.setRemark(oneData.get("REMARK").toString());
                    datas.setType(oneData.get("TYPE").toString());
                    datas.setSendType(oneData.get("SENDTYPE").toString());
                    datas.setStatus(oneData.get("STATUS").toString());
                    datas.setCreateTime(oneData.get("CREATETIME").toString());
                    datas.setLastModiTime(oneData.get("LASTMODITIME").toString());

                    datas.setUserList(new ArrayList<>());
                    datas.setChatList(new ArrayList<>());
                    datas.setTagList(new ArrayList<>());
                    datas.setAnnexList(new ArrayList<>());

                    sql = " select a.userid,b.opname from dcp_isvwecom_gpmsg_user a"
                            + " left join dcp_isvwecom_staffs b on a.eid=b.eid and a.userid=b.userid"
                            + " where a.eid='"+eId+"' and a.gpmsgid='"+gpMsgId+"' "
                            + " order by a.userid ";
                    List<Map<String, Object>> getUserQData=this.doQueryData(sql, null);
                    for (Map<String, Object> userData : getUserQData) {
                        User user = res.new User();
                        user.setUserId(userData.get("USERID").toString());
                        user.setUserName(userData.get("OPNAME").toString());

                        datas.getUserList().add(user);
                    }

                    sql = " select a.chatid,b.name from dcp_isvwecom_gpmsg_gp a"
                            + " left join dcp_isvwecom_groupchat b on a.eid=b.eid and a.chatid=b.chatid"
                            + " where a.eid='"+eId+"' and a.gpmsgid='"+gpMsgId+"' "
                            + " order by a.chatid ";
                    List<Map<String, Object>> getChatQData=this.doQueryData(sql, null);
                    for (Map<String, Object> chatData : getChatQData) {
                        Chat chat = res.new Chat();
                        chat.setChatId(chatData.get("CHATID").toString());
                        chat.setChatName(chatData.get("NAME").toString());

                        datas.getChatList().add(chat);
                    }

                    sql = " select a.item,a.tagid,b.tagname from dcp_isvwecom_gpmsg_tag a"
                            + " left join dcp_isvwecom_tag b on a.eid=b.eid and a.tagid=b.tagid"
                            + " where a.eid='"+eId+"' and a.gpmsgid='"+gpMsgId+"'"
                            + " order by a.item,a.tagid";
                    List<Map<String, Object>> getTagQData=this.doQueryData(sql, null);
                    //distinct 资料
                    getTagQData = getTagQData.stream().distinct().collect(Collectors.toList());
                    for (Map<String, Object> tagData : getTagQData) {
                        Tag tag = res.new Tag();
                        tag.setItem(tagData.get("ITEM").toString());
                        tag.setTagId(tagData.get("TAGID").toString());
                        tag.setTagName(tagData.get("TAGNAME").toString());

                        datas.getTagList().add(tag);
                    }

                    sql = " select a.* from dcp_isvwecom_gpmsg_annex a"
                            + " where a.eid='"+eId+"' and a.gpmsgid='"+gpMsgId+"'"
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
    protected String getQuerySql(DCP_ISVWeComGpMsgQueryReq req) throws Exception {
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String userId = req.getRequest().getUserId();
        String type = req.getRequest().getType();
        String status = req.getRequest().getStatus();
        String chatId = req.getRequest().getChatId();
        String tagId = req.getRequest().getTagId();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();

        StringBuffer sqlBuf = new StringBuffer();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * req.getPageSize();

        sqlBuf.append(" select a.* from ("
                + " select count(distinct a.gpmsgid) over() num,"
                + " dense_rank() over(order by a.createtime desc,a.gpmsgid) as rn,"
                + " a.gpmsgid,a.name,a.remark,a.msg,a.type,a.status,a.sendtype,"
                + " to_char(a.createtime,'yyyy-mm-dd hh24:mi:ss') as createtime,"
                + " to_char(a.lastmoditime,'yyyy-mm-dd hh24:mi:ss') as lastmoditime"
                + " from dcp_isvwecom_gpmsg a"
                + " left join dcp_isvwecom_gpmsg_user b on a.eid=b.eid and a.gpmsgid=b.gpmsgid"
                + " left join dcp_isvwecom_staffs b1 on a.eid=b1.eid and b.userid=b1.userid"
                + " left join dcp_isvwecom_gpmsg_gp c on a.eid=c.eid and a.gpmsgid=c.gpmsgid"
                + " left join dcp_isvwecom_groupchat c1 on a.eid=c1.eid and c.chatid=c1.chatid"
                + " left join dcp_isvwecom_gpmsg_tag d on a.eid=d.eid and a.gpmsgid=d.gpmsgid"
                + " left join dcp_isvwecom_tag d1 on a.eid=d1.eid and d.tagid=d1.tagid"
                + " where a.eid='"+eId+"' ");

        if (!Check.Null(userId)){
            sqlBuf.append(" and b.userid='"+userId+"' ");
        }
        if (!Check.Null(type)){
            sqlBuf.append(" and a.type='"+type+"' ");
        }
        if (!Check.Null(status)){
            sqlBuf.append(" and a.status='"+status+"' ");
        }
        if (!Check.Null(chatId)){
            sqlBuf.append(" and c.chatid='"+chatId+"' ");
        }
        if (!Check.Null(tagId)){
            sqlBuf.append(" and d.tagid='"+tagId+"' ");
        }
        if (!Check.Null(beginDate) && !Check.Null(endDate)){
            sqlBuf.append(" and a.createtime>=to_date('"+beginDate+" 00:00:01','yyyy-MM-dd HH24:mi:ss') ");
            sqlBuf.append(" and a.createtime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd HH24:mi:ss') ");
        }
        if (!Check.Null(keyTxt)){
            sqlBuf.append(" and (a.name like '%"+keyTxt+"%' or b1.opname like '%"+keyTxt+"%' "
                    + "or c1.name like '%"+keyTxt+"%' or d1.tagname like '%"+keyTxt+"%') ");
        }

        sqlBuf.append(" ) a");
        sqlBuf.append(" where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" ");
        sqlBuf.append(" order by a.rn ");

        return sqlBuf.toString();

    }
}
