package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComGroupChatDetailReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGroupChatDetailRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGroupChatDetailRes.Admin;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGroupChatDetailRes.Member;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComGroupChatDetail
 * 服务说明：查询企微客户群详情
 * @author jinzma
 * @since  2024-01-15
 */
public class DCP_ISVWeComGroupChatDetail extends SPosBasicService<DCP_ISVWeComGroupChatDetailReq, DCP_ISVWeComGroupChatDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGroupChatDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getChatId())){
                errMsg.append("群ID 不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComGroupChatDetailReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGroupChatDetailReq>(){};
    }

    @Override
    protected DCP_ISVWeComGroupChatDetailRes getResponseType() {
        return new DCP_ISVWeComGroupChatDetailRes();
    }

    @Override
    protected DCP_ISVWeComGroupChatDetailRes processJson(DCP_ISVWeComGroupChatDetailReq req) throws Exception {
        DCP_ISVWeComGroupChatDetailRes res = this.getResponse();
        DCP_ISVWeComGroupChatDetailRes.Datas datas = res.new Datas();
        try{
            String eId = req.geteId();
            String chatId = req.getRequest().getChatId();
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getQData)){
                datas.setChatId(getQData.get(0).get("CHATID").toString());
                datas.setName(getQData.get(0).get("NAME").toString());
                datas.setStatus(getQData.get(0).get("STATUS").toString());
                datas.setUserId(getQData.get(0).get("OWNER").toString());
                datas.setUserName(getQData.get(0).get("OPNAME").toString());

                datas.setCreateTime(getQData.get(0).get("CHATCREATETIME").toString());
                datas.setNotice(getQData.get(0).get("NOTICE").toString());

                datas.setAdminList(new ArrayList<>());
                datas.setMemberList(new ArrayList<>());
                //adminList
                sql = " select * from dcp_isvwecom_groupchat_de where eid='"+eId+"' and chatid='"+chatId+"' order by userid";
                List<Map<String, Object>> getUserQData=this.doQueryData(sql, null);
                for (Map<String, Object> user:getUserQData){
                    String isAdmin = user.get("ISADMIN").toString();  //是否是管理员0否1是
                    if ("1".equals(isAdmin)){
                        Admin admin = res.new Admin();
                        admin.setUserId(user.get("USERID").toString());
                        admin.setUserName(user.get("NAME").toString());

                        datas.getAdminList().add(admin);
                    }else {
                        Member member = res.new Member();
                        member.setUserId(user.get("USERID").toString());
                        member.setUserName(user.get("NAME").toString());
                        member.setGroupNickName(user.get("GROUPNICKNAME").toString());
                        member.setUserType(user.get("TYPE").toString());
                        member.setJoinScene(user.get("JOINSCENE").toString());
                        member.setJoinTime(user.get("JOINTIME").toString());

                        datas.getMemberList().add(member);
                    }
                }

                //成员数统计
                sql = " select count(*) as num from dcp_isvwecom_groupchat_de"
                        + " where eid='"+eId+"' and chatid='"+chatId+"' ";
                List<Map<String, Object>> getMemberCntQData=this.doQueryData(sql, null);
                datas.setMemberCnt(getMemberCntQData.get(0).get("NUM").toString());


            }else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400," 详情资料不存在! ");
            }

            res.setDatas(datas);

            return res;


        }catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVWeComGroupChatDetailReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String chatId = req.getRequest().getChatId();

        sqlbuf.append(" select a.chatid,a.name,a.status,a.owner,a.notice,b.opname,"
                + " to_char(a.chatcreatetime,'yyyy-MM-dd HH24:mi:ss') as chatcreatetime"
                + " from dcp_isvwecom_groupchat a"
                + " left join dcp_isvwecom_staffs b on a.eid=b.eid and a.owner=b.userid"
                + " where a.eid='"+eId+"' and a.chatid='"+chatId+"' ");

        return sqlbuf.toString();
    }
}
