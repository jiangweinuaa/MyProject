package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_StuffQueryReq;
import com.dsc.spos.json.cust.res.DCP_StuffQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_StuffQuery extends SPosBasicService<DCP_StuffQueryReq, DCP_StuffQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_StuffQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest() == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        //String groupId = req.getRequest().getGroupId();

		/*if (Check.Null(groupId)) 
		{
			isFail = true;
			errMsg.append("口味分组编码不能为空 ");			
		}*/

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_StuffQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_StuffQueryReq>() {
        };
    }

    @Override
    protected DCP_StuffQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_StuffQueryRes();
    }

    @Override
    protected DCP_StuffQueryRes processJson(DCP_StuffQueryReq req) throws Exception {
        String eId = req.geteId();
        String curLangType = req.getLangType();
        if (curLangType == null || curLangType.isEmpty()) {
            curLangType = "zh_CN";
        }
        DCP_StuffQueryRes res = this.getResponse();

        String sql = getQuerySql(req);
        List<Map<String, Object>> getData = this.doQueryData(sql, null);

        res.setDatas(new ArrayList<DCP_StuffQueryRes.level1Elm>());


        for (Map<String, Object> oneData : getData) {
            DCP_StuffQueryRes.level1Elm lv1 = res.new level1Elm();
            String stuffId = oneData.get("STUFFID").toString();
            lv1.setStuffId(stuffId);
            lv1.setStuffName(oneData.get("PLU_NAME").toString());
            lv1.setSortId(oneData.get("SORTID").toString());
            lv1.setStatus(oneData.get("STATUS").toString());
            res.getDatas().add(lv1);
            lv1 = null;
        }


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_StuffQueryReq req) throws Exception {
        String eId = req.geteId();
        String langtype = req.getLangType();
        String ketTxt = req.getRequest().getKeyTxt();
        String status = req.getRequest().getStatus();


        String sql = null;


        StringBuffer sqlbuf = new StringBuffer("select * from ( "
                + "select A.*,B.LANG_TYPE,B.PLU_NAME,c.STATUS "
                + "from DCP_STUFF a "
                + "left join dcp_goods_lang b on a.eid=b.eid and a.STUFFID=b.PLUNO and B.lang_type='" + langtype + "' "
                + "left join dcp_goods c on a.eid=c.eid and a.STUFFID=c.PLUNO  "
                + "where a.eid='" + eId + "' ");

        if (status != null && status.length() > 0) {
            sqlbuf.append("and c.status='" + status + "' ");
        }

        if (ketTxt != null && ketTxt.length() > 0) {
            sqlbuf.append("and (a.STUFFID like '%%" + ketTxt + "%%' or b.PLU_NAME like '%%" + ketTxt + "%%') ");
        }

        sqlbuf.append(" order by a.sortid )");

        sql = sqlbuf.toString();
        return sql;
    }

}
