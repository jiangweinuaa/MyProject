package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComWelcomeMsgQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComWelcomeMsgQueryRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComWelcomeMsgQueryRes.Datas;
import com.dsc.spos.json.cust.res.DCP_ISVWeComWelcomeMsgQueryRes.User;
import com.dsc.spos.json.cust.res.DCP_ISVWeComWelcomeMsgQueryRes.Annex;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.springframework.util.CollectionUtils;

/**
 * 服务函数：DCP_ISVWeComWelcomeMsgQuery
 * 服务说明：个人欢迎语查询
 * @author jinzma
 * @since  2024-02-20
 */
public class DCP_ISVWeComWelcomeMsgQuery extends SPosBasicService<DCP_ISVWeComWelcomeMsgQueryReq, DCP_ISVWeComWelcomeMsgQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComWelcomeMsgQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ISVWeComWelcomeMsgQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComWelcomeMsgQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComWelcomeMsgQueryRes getResponseType() {
        return new DCP_ISVWeComWelcomeMsgQueryRes();
    }

    @Override
    protected DCP_ISVWeComWelcomeMsgQueryRes processJson(DCP_ISVWeComWelcomeMsgQueryReq req) throws Exception {
        DCP_ISVWeComWelcomeMsgQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        try {
            String eId = req.geteId();
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            int totalRecords=0;								//总笔数
            int totalPages=0;									//总页数
            if (!CollectionUtils.isEmpty(getQData)){
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
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

                getQData = getQData.stream().distinct().collect(Collectors.toList());

                for (Map<String, Object> oneData : getQData) {
                    Datas datas = res.new Datas();
                    String welMsgId = oneData.get("WELMSGID").toString();
                    datas.setWelMsgId(welMsgId);
                    datas.setName(oneData.get("NAME").toString());
                    datas.setRemark(oneData.get("REMARK").toString());
                    datas.setMsg(oneData.get("MSG").toString());
                    datas.setRestrictStaff(oneData.get("RESTRICTSTAFF").toString());
                    datas.setCreateTime(oneData.get("CREATETIME").toString());
                    datas.setLastModiTime(oneData.get("LASTMODITIME").toString());

                    datas.setUserList(new ArrayList<>());
                    sql = " select a.userid,b.opname from dcp_isvwecom_welmsg_staff a"
                            + " left join dcp_isvwecom_staffs b on a.eid=b.eid and a.userid=b.userid"
                            + " where a.eid='"+eId+"' and a.welmsgid='"+welMsgId+"' "
                            + " order by a.userid ";
                    List<Map<String, Object>> getUserQData=this.doQueryData(sql, null);
                    for (Map<String, Object> userData : getUserQData) {
                        User user = res.new User();
                        user.setUserId(userData.get("USERID").toString());
                        user.setUserName(userData.get("OPNAME").toString());

                        datas.getUserList().add(user);
                    }

                    datas.setAnnexList(new ArrayList<>());
                    sql = " select * from dcp_isvwecom_welmsg_annex "
                            + " where eid='"+eId+"' and welmsgid='"+welMsgId+"' "
                            + " order by serialno";
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
    protected String getQuerySql(DCP_ISVWeComWelcomeMsgQueryReq req) throws Exception {
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String userId = req.getRequest().getUserId();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();

        StringBuffer sqlBuf = new StringBuffer();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * req.getPageSize();

        sqlBuf.append(" select * from ("
                + " select count(distinct a.welmsgid) over() num,"
                + " dense_rank() over(order by a.createtime desc,a.welmsgid) as rn,"
                + " a.welmsgid,a.name,a.remark,a.msg,a.restrictstaff,"
                + " to_char(a.createtime,'yyyy-mm-dd hh24:mi:ss') as createtime,"
                + " to_char(a.lastmoditime,'yyyy-mm-dd hh24:mi:ss') as lastmoditime"
                + " from dcp_isvwecom_welmsg a"
                + " left join dcp_isvwecom_welmsg_staff b on a.eid=b.eid and a.welmsgid=b.welmsgid"
                + " left join dcp_isvwecom_staffs b1 on a.eid=b1.eid and b.userid=b1.userid"
                + " where a.eid='"+eId+"' ");

        if (!Check.Null(userId)){
            sqlBuf.append(" and (a.restrictstaff=0 or b.userid='"+userId+"')");
        }

        if (!Check.Null(keyTxt)){
            sqlBuf.append(" and (a.name like '%"+keyTxt+"%' or b1.opname like '%"+keyTxt+"%') ");
        }

        if (!Check.Null(beginDate) && !Check.Null(endDate)){
            sqlBuf.append(" and a.createtime>=to_date('"+beginDate+" 00:00:01','yyyy-MM-dd HH24:mi:ss') "
                    + " and a.createtime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd HH24:mi:ss') ");
        }
        sqlBuf.append(" ) a");
        sqlBuf.append(" where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" ");
        sqlBuf.append(" order by a.rn ");

        return sqlBuf.toString();


    }
}
