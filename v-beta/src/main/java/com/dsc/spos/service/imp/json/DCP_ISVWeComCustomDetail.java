package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.json.cust.req.DCP_ISVWeComCustomDetailReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComCustomDetailRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComCustomDetailRes.*;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComCustomDetail
 * 服务说明：查询企微客户详情
 * @author jinzma
 * @since  2024-01-25
 */
public class DCP_ISVWeComCustomDetail extends SPosBasicService<DCP_ISVWeComCustomDetailReq, DCP_ISVWeComCustomDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComCustomDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            if (Check.Null(req.getRequest().getExternalUserId())){
                errMsg.append("外部userid 不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComCustomDetailReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComCustomDetailReq>(){};
    }

    @Override
    protected DCP_ISVWeComCustomDetailRes getResponseType() {
        return new DCP_ISVWeComCustomDetailRes();
    }

    @Override
    protected DCP_ISVWeComCustomDetailRes processJson(DCP_ISVWeComCustomDetailReq req) throws Exception {
        DCP_ISVWeComCustomDetailRes res = this.getResponse();
        Datas datas = res.new Datas();
        try{
            String eid = req.geteId();
            String externalUserId = req.getRequest().getExternalUserId();
            String sql = " select a.externaluserid,a.name,a.avatar,a.customtype,a.gender,a.status,b.memberid"
                    + " from dcp_isvwecom_custom a"
                    + " left join dcp_isvwecom_member b on a.eid=b.eid and a.externaluserid=b.externaluserid"
                    + " where a.eid='"+eid+"' and a.externaluserid='"+externalUserId+"' ";
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (CollectionUtil.isNotEmpty(getQData)){
                String memberId = getQData.get(0).get("MEMBERID").toString();

                datas.setExternalUserId(getQData.get(0).get("EXTERNALUSERID").toString());
                datas.setName(getQData.get(0).get("NAME").toString());
                datas.setMemberId(memberId);
                datas.setAvatar(getQData.get(0).get("AVATAR").toString());
                datas.setCustomType(getQData.get(0).get("CUSTOMTYPE").toString());
                datas.setGender(getQData.get(0).get("GENDER").toString());
                datas.setStatus(getQData.get(0).get("STATUS").toString());

                datas.setFollowList(new ArrayList<>());
                datas.setMemberTagList(new ArrayList<>());
                datas.setTagList(new ArrayList<>());
                datas.setRemarkMobiles(new ArrayList<>());

                //FollowList
                sql = " select a.userid,a.addway,a.followremark,a.followdescrip,"
                        + " to_char(a.followtime,'yyyy-MM-dd HH24:mi:ss') as followtime,"
                        + " b.opname"
                        + " from dcp_isvwecom_custom_follow a"
                        + " left join dcp_isvwecom_staffs b on a.eid=b.eid and a.userid=b.userid"
                        + " where a.eid='"+eid+"' and a.externaluserid='"+externalUserId+"' "
                        + " order by a.userid";
                List<Map<String, Object>> getFollowQData=this.doQueryData(sql, null);
                if (CollectionUtil.isNotEmpty(getFollowQData)){
                    getFollowQData.forEach(
                            oneFollow->{
                                Follow follow = res.new Follow();
                                follow.setUserId(oneFollow.get("USERID").toString());
                                follow.setUserName(oneFollow.get("OPNAME").toString());
                                follow.setAddWay(oneFollow.get("ADDWAY").toString());
                                follow.setFollowRemark(oneFollow.get("FOLLOWREMARK").toString());
                                follow.setFollowDescrip(oneFollow.get("FOLLOWDESCRIP").toString());
                                follow.setFollwTime(oneFollow.get("FOLLOWTIME").toString());

                                datas.getFollowList().add(follow);
                            }
                    );
                }

                //MemberTagList
                if (!Check.Null(memberId)) {
                    sql = " select a.tagid,b.tagname from crm_membertag a "
                            + " left join crm_membertagtype b on a.eid=b.eid and a.tagid=b.tagid"
                            + " where a.eid='" + eid + "' and a.memberid='" + memberId + "'"
                            + " order by a.tagid";
                    List<Map<String, Object>> getMemberTagQData=this.doQueryData(sql, null);
                    if (CollectionUtil.isNotEmpty(getMemberTagQData)){
                        getMemberTagQData.forEach(
                                oneMemberTag->{
                                    MemberTag memberTag = res.new MemberTag();
                                    memberTag.setTagId(oneMemberTag.get("TAGID").toString());
                                    memberTag.setTagName(oneMemberTag.get("TAGNAME").toString());

                                    datas.getMemberTagList().add(memberTag);
                                }
                        );
                    }
                }

                //TagList
                sql = " select a.tagid,a.userid,b.tagname from dcp_isvwecom_custom_tag a"
                        + " left join dcp_isvwecom_tag b on a.eid=b.eid and a.tagid=b.tagid"
                        + " where a.eid='"+eid+"' and a.externaluserid='"+externalUserId+"'"
                        + " order by a.tagid ";
                List<Map<String, Object>> getTagQData=this.doQueryData(sql, null);
                if (CollectionUtil.isNotEmpty(getTagQData)){
                    getTagQData.forEach(
                            oneTag->{
                                Tag tag = res.new Tag();
                                tag.setTagId(oneTag.get("TAGID").toString());
                                tag.setTagName(oneTag.get("TAGNAME").toString());
                                tag.setUserId(oneTag.get("USERID").toString());

                                datas.getTagList().add(tag);
                            }
                    );
                }

                //RemarkMobile
                sql = " select a.remarkmobile,a.userid from dcp_isvwecom_custom_mobile a"
                        + " where a.eid='"+eid+"' and a.externaluserid='"+externalUserId+"' "
                        + " order by a.remarkmobile ";
                List<Map<String, Object>> getRemarkMobileQData=this.doQueryData(sql, null);
                if (CollectionUtil.isNotEmpty(getRemarkMobileQData)){
                    getRemarkMobileQData.forEach(
                            oneRemarkMobile->{
                                RemarkMobile remarkMobile = res.new RemarkMobile();
                                remarkMobile.setMobile(oneRemarkMobile.get("REMARKMOBILE").toString());
                                remarkMobile.setUserId(oneRemarkMobile.get("USERID").toString());

                                datas.getRemarkMobiles().add(remarkMobile);
                            }
                    );
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
    protected String getQuerySql(DCP_ISVWeComCustomDetailReq req) throws Exception {
        return null;
    }
}
