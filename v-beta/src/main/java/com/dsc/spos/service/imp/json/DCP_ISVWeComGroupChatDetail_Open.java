package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComGroupChatDetail_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGroupChatDetail_OpenRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGroupChatDetail_OpenRes.Datas;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGroupChatDetail_OpenRes.Admin;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGroupChatDetail_OpenRes.Member;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.springframework.util.CollectionUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComGroupChatDetail_Open
 * 服务说明：查询企微客户群详情_外部
 * @author jinzma
 * @since  2024-03-18
 */
public class DCP_ISVWeComGroupChatDetail_Open extends SPosBasicService<DCP_ISVWeComGroupChatDetail_OpenReq, DCP_ISVWeComGroupChatDetail_OpenRes> {

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGroupChatDetail_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getChatId())){
                errMsg.append("chatId 不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComGroupChatDetail_OpenReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGroupChatDetail_OpenReq>(){};
    }

    @Override
    protected DCP_ISVWeComGroupChatDetail_OpenRes getResponseType() {
        return new DCP_ISVWeComGroupChatDetail_OpenRes();
    }

    @Override
    protected DCP_ISVWeComGroupChatDetail_OpenRes processJson(DCP_ISVWeComGroupChatDetail_OpenReq req) throws Exception {
        DCP_ISVWeComGroupChatDetail_OpenRes res = this.getResponse();
        Datas datas = res.new Datas();
        try{
            String eId = req.geteId();
            String chatId = req.getRequest().getChatId();
            String sql = " select a.name,a.status,a.owner,a.notice,b.opname,"
                    + " to_char(a.chatcreatetime,'yyyy-MM-dd HH24:mi:ss') as chatcreatetime"
                    + " from dcp_isvwecom_groupchat a"
                    + " left join dcp_isvwecom_staffs b on a.eid=b.eid and a.owner=b.userid "
                    + " where a.eid='"+eId+"' and a.chatid='"+chatId+"' ";

            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getQData)) {
                datas.setChatId(chatId);
                datas.setName(getQData.get(0).get("NAME").toString());
                datas.setStatus(getQData.get(0).get("STATUS").toString());
                datas.setUserId(getQData.get(0).get("OWNER").toString());
                datas.setUserName(getQData.get(0).get("OPNAME").toString());
                datas.setNotice(getQData.get(0).get("NOTICE").toString());
                datas.setCreateTime(getQData.get(0).get("CHATCREATETIME").toString());

                //成员数 (外部客户 + 内部员工)
                sql = " select count(*) as num from dcp_isvwecom_groupchat_de"
                        + " where eid='"+eId+"' and chatid='"+chatId+"' ";
                List<Map<String, Object>> getMemberCntQData=this.doQueryData(sql, null);
                String memberCnt = getMemberCntQData.get(0).get("NUM").toString();
                if (!PosPub.isNumericType(memberCnt)) {
                    memberCnt = "0";
                }
                datas.setMemberCnt(memberCnt);

                //今日入群数
                sql = " select sum(memchangecnt) as memchangecnt from dcp_isvwecom_callback_gp"
                        + " where eid='"+eId+"' and chatid='"+chatId+"'"
                        + " and changetype='update' and updatedetail='add_member' and createtime >= trunc(sysdate)";
                List<Map<String, Object>> getInCntQData=this.doQueryData(sql, null);
                String inCnt = getInCntQData.get(0).get("MEMCHANGECNT").toString();
                if (!PosPub.isNumericType(inCnt)) {
                    inCnt = "0";
                }
                datas.setInCnt(inCnt);

                //今日退群数
                sql = " select sum(memchangecnt) as memchangecnt from dcp_isvwecom_callback_gp"
                        + " where eid='"+eId+"' and chatid='"+chatId+"'"
                        + " and changetype='update' and updatedetail='del_member' and createtime >= trunc(sysdate)";
                List<Map<String, Object>> getOutCntQData=this.doQueryData(sql, null);
                String outCnt = getOutCntQData.get(0).get("MEMCHANGECNT").toString();
                if (!PosPub.isNumericType(outCnt)) {
                    outCnt = "0";
                }
                datas.setOutCnt(outCnt);

                //客人总数
                sql = " select count(*) as num from dcp_isvwecom_groupchat_de"
                        + " where eid='"+eId+"' and chatid='"+chatId+"' and type='2' ";
                List<Map<String, Object>> getExternalUserQData=this.doQueryData(sql, null);
                String customerNum = getExternalUserQData.get(0).get("NUM").toString();
                if (!PosPub.isNumericType(customerNum)) {
                    customerNum = "0";
                }


                //会员人数
                sql = " select count(*) as membernum from dcp_isvwecom_groupchat_de a"
                        + " left join dcp_isvwecom_member b on a.eid=b.eid and a.userid =b.externaluserid"
                        + " where a.eid='"+eId+"' and a.chatid='"+chatId+"' and a.type='2' and b.memberid is not null";
                List<Map<String, Object>> getMemberNumQData=this.doQueryData(sql, null);
                String memberNum = getMemberNumQData.get(0).get("MEMBERNUM").toString();
                if (!PosPub.isNumericType(memberNum)) {
                    memberNum = "0";
                }
                datas.setMemberNum(memberNum);

                //会员比例
                BigDecimal memberRatio = new BigDecimal("0");
                if (!"0".equals(customerNum)){
                    memberRatio = new BigDecimal(memberNum).divide(new BigDecimal(customerNum),2, RoundingMode.HALF_UP);
                }
                datas.setMemberRatio(String.valueOf(memberRatio));

                //非会员人数
                BigDecimal nonMemberNum = new BigDecimal(customerNum).subtract(new BigDecimal(memberNum));
                datas.setNonMemberNum(String.valueOf(nonMemberNum));

                //非会员比例
                BigDecimal nonMemberRatio = new BigDecimal("0");
                if (!"0".equals(customerNum)){
                    nonMemberRatio = nonMemberNum.divide(new BigDecimal(customerNum),2, RoundingMode.HALF_UP);
                }
                datas.setNonMemberRatio(String.valueOf(nonMemberRatio));

                //员工总数
                sql = " select count(*) as num from dcp_isvwecom_groupchat_de"
                        + " where eid='"+eId+"' and chatid='"+chatId+"' and type='1' ";
                List<Map<String, Object>> getStaffNumQData=this.doQueryData(sql, null);
                String staffNum = getStaffNumQData.get(0).get("NUM").toString();
                if (!PosPub.isNumericType(staffNum)) {
                    staffNum = "0";
                }
                datas.setStaffNum(staffNum);

                //员工比例
                BigDecimal staffRatio = new BigDecimal("0");
                if (!"0".equals(memberCnt)){
                    staffRatio = new BigDecimal(staffNum).divide(new BigDecimal(memberCnt),2, RoundingMode.HALF_UP);
                }
                datas.setStaffRatio(String.valueOf(staffRatio));

                //客人总数
                datas.setCustomerNum(customerNum);

                //客人比例
                BigDecimal customerRatio = new BigDecimal("0");
                if (!"0".equals(memberCnt)){
                    customerRatio = new BigDecimal(customerNum).divide(new BigDecimal(memberCnt),2, RoundingMode.HALF_UP);
                }

                datas.setCustomerRatio(String.valueOf(customerRatio));



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
    protected String getQuerySql(DCP_ISVWeComGroupChatDetail_OpenReq req) throws Exception {
        return null;
    }
}
