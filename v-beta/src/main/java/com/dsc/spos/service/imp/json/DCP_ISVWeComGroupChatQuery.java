package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComGroupChatQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGroupChatQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.springframework.util.CollectionUtils;

/**
 * 服务函数：DCP_ISVWeComGroupChatQuery
 * 服务说明：查询企微客户群列表
 * @author jinzma
 * @since  2024-01-15
 */
public class DCP_ISVWeComGroupChatQuery extends SPosBasicService<DCP_ISVWeComGroupChatQueryReq, DCP_ISVWeComGroupChatQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGroupChatQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ISVWeComGroupChatQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGroupChatQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComGroupChatQueryRes getResponseType() {
        return new DCP_ISVWeComGroupChatQueryRes();
    }

    @Override
    protected DCP_ISVWeComGroupChatQueryRes processJson(DCP_ISVWeComGroupChatQueryReq req) throws Exception {
        DCP_ISVWeComGroupChatQueryRes res = this.getResponse();
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
                for (Map<String, Object> oneData : getQData) {
                    DCP_ISVWeComGroupChatQueryRes.Datas datas = res.new Datas();
                    datas.setChatId(oneData.get("CHATID").toString());
                    datas.setName(oneData.get("NAME").toString());
                    datas.setStatus(oneData.get("STATUS").toString());
                    datas.setUserId(oneData.get("OWNER").toString());
                    datas.setUserName(oneData.get("OPNAME").toString());
                    datas.setMemberCnt(oneData.get("MEMBERCNT").toString());
                    datas.setCreateTime(oneData.get("CHATCREATETIME").toString());
                    datas.setNotice(oneData.get("NOTICE").toString());

                    res.getDatas().add(datas);
                }
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;


        }catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVWeComGroupChatQueryReq req) throws Exception {
        StringBuffer sqlBuf=new StringBuffer();
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String userId = req.getRequest().getUserId();
        String status = req.getRequest().getStatus();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * req.getPageSize();

        sqlBuf.append(" select * from ("
                + " select count(*) over() num,row_number() over (order by a.chatcreatetime desc) rn,"
                + " a.chatid,a.name,a.status,a.owner,a.notice,"
                + " to_char(a.chatcreatetime,'yyyy-MM-dd HH24:mi:ss') as chatcreatetime,"
                + " b.opname,c.membercnt"
                + " from dcp_isvwecom_groupchat a"
                + " left join dcp_isvwecom_staffs b on a.eid=b.eid and a.owner=b.userid"
                + " left join (select eid,chatid,count(*) as membercnt from dcp_isvwecom_groupchat_de group by eid,chatid) c on a.eid=c.eid and a.chatid=c.chatid"
                + " where a.eid='"+eId+"'"
                + " ");
        if (!Check.Null(keyTxt)){
            sqlBuf.append(" and (a.chatid like '%"+keyTxt+"%' or a.name like '%"+keyTxt+"%') ");
        }
        if (!Check.Null(userId)){
            sqlBuf.append(" and a.owner='"+userId+"' ");
        }
        if (!Check.Null(status)){
            sqlBuf.append(" and a.status='"+status+"'");
        }
        if (!Check.Null(beginDate) && !Check.Null(endDate)){
            sqlBuf.append(" and a.chatcreatetime>=to_date('"+beginDate+" 00:00:01','yyyy-MM-dd HH24:mi:ss')");
            sqlBuf.append(" and a.chatcreatetime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd HH24:mi:ss')");
        }
        sqlBuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize)+" order by rn ");

        return sqlBuf.toString();
    }
}
