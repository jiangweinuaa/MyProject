package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CustGroupDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_CustGroupDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_CustGroupDetailQuery extends SPosBasicService<DCP_CustGroupDetailQueryReq, DCP_CustGroupDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CustGroupDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustGroupDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_CustGroupDetailQueryReq>(){};
    }

    @Override
    protected DCP_CustGroupDetailQueryRes getResponseType() {
        return new DCP_CustGroupDetailQueryRes();
    }

    @Override
    protected DCP_CustGroupDetailQueryRes processJson(DCP_CustGroupDetailQueryReq req) throws Exception {
        DCP_CustGroupDetailQueryRes res = this.getResponseType();

        List<Map<String, Object>> queryData = doQueryData(this.getQuerySql(req), null);
        List<Map<String, Object>> customerData = doQueryData(this.getDetailQuerySql(req), null);

        res.setDatas(new ArrayList<>());
        Map<String, Boolean> condition = new HashMap<>(); //查询条件
        condition.put("CUSTGROUPNO", true);
        //调用过滤函数
        List<Map<String, Object>> headData=MapDistinct.getMap(queryData, condition);

        for (Map<String, Object> oneData : headData) {
            DCP_CustGroupDetailQueryRes.Datas datas = res.new Datas();

            datas.setCustGroupNo(oneData.get("CUSTGROUPNO").toString());
            datas.setCustGroupName(StringUtils.toString(oneData.get("CUSTGROUPNAME"), ""));

            datas.setLastModiOpId(StringUtils.toString(oneData.get("LASTMODIOPID"), ""));
            datas.setLastModiOpName(StringUtils.toString(oneData.get("LASTMODIOPNAME"), ""));
            datas.setCreateOpId(StringUtils.toString(oneData.get("CREATEOPID"), ""));
            datas.setCreateOpName(StringUtils.toString(oneData.get("CREATEOPNAME"), ""));
            datas.setCreateDeptId(StringUtils.toString(oneData.get("CREATEDEPTID"), ""));
            datas.setCreateDeptName(StringUtils.toString(oneData.get("CREATEDEPTNAME"), ""));
            datas.setMemo(StringUtils.toString(oneData.get("MEMO"), ""));
            datas.setLastModiTime(StringUtils.toString(oneData.get("LASTMODITIME"), ""));
            datas.setCreateTime(StringUtils.toString(oneData.get("CREATETIME"), ""));
            datas.setStatus(oneData.get("STATUS").toString());

            Map<String, Object> condiV=new HashMap<String, Object>();
            condiV.put("CUSTGROUPNO", oneData.get("CUSTGROUPNO"));

            List<Map<String, Object>> customer = MapDistinct.getWhereMap(customerData,condiV,true);

            datas.setCustomerList(new ArrayList<>());
            for (Map<String, Object> oneCustomer : customer) {
                DCP_CustGroupDetailQueryRes.CustomerList customerList = res.new CustomerList();

                customerList.setSName(StringUtils.toString(oneCustomer.get("SNAME"), ""));
                customerList.setFName(StringUtils.toString(oneCustomer.get("FNAME"), ""));
                customerList.setCustomerNo(StringUtils.toString(oneCustomer.get("CUTNO"), ""));
                customerList.setBeginDate(StringUtils.toString(oneCustomer.get("BEGINDATE"), ""));
                customerList.setEndDate(StringUtils.toString(oneCustomer.get("ENDDATE"), ""));
                customerList.setStatus(StringUtils.toString(oneCustomer.get("STATUS"), ""));

                datas.getCustomerList().add(customerList);
            }


            datas.setDetail(new ArrayList<>());
            List<Map<String, Object>> detail = MapDistinct.getWhereMap(queryData,condiV,true);
            for (Map<String, Object> oneDetail : detail) {
                DCP_CustGroupDetailQueryRes.Detail detailData = res.new Detail();

                detailData.setStatus(StringUtils.toString(oneDetail.get("DETAILSTATUS"), ""));
                detailData.setItem(StringUtils.toString(oneDetail.get("RN"), ""));
                detailData.setAttrType(StringUtils.toString(oneDetail.get("ATTRTYPE"), ""));
                detailData.setAttrId(StringUtils.toString(oneDetail.get("ATTRID"), ""));
                detailData.setAttrName(StringUtils.toString(oneDetail.get("ATTRNAME"), ""));

                datas.getDetail().add(detailData);
            }
            res.getDatas().add(datas);
        }
        res.setSuccess(true);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    protected String getDetailQuerySql(DCP_CustGroupDetailQueryReq req) throws Exception {
        //分页处理
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
        StringBuilder builder = new StringBuilder();

        builder.append(
                " SELECT a.EID,a.CUSTGROUPNO,a.CUTNO,b.SNAME,b.FNAME,b.BEGINDATE,b.ENDDATE,b.STATUS  FROM( " +
                        "  SELECT DISTINCT a.EID,a.CUSTGROUPNO,CASE WHEN a.ATTRTYPE='1' THEN c.ID ELSE A.ATTRID END CUTNO " +
                        "  FROM DCP_CUSTGROUP_DETAIL a " +
                        "  LEFT JOIN dcp_tagtype_detail c on a.EID=c.EID AND a.ATTRTYPE=1 and c.TAGGROUPTYPE='CUST' AND c.TAGNO=a.ATTRID )a" +
                        "  LEFT JOIN dcp_bizpartner b on a.eid=b.eid and a.CUTNO = b.BIZPARTNERNO " +
                        "  where a.eid='" + req.geteId() + "'"

        );
        if (StringUtils.isNotEmpty(req.getRequest().getCustGroupNo())) {
            builder.append(" AND a.CUSTGROUPNO='").append(req.getRequest().getCustGroupNo()).append("'");
        }

        return builder.toString();
    }

    @Override
    protected String getQuerySql(DCP_CustGroupDetailQueryReq req) throws Exception {
        //分页处理
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        String countCustSql =
                " SELECT a.EID,a.CUSTGROUPNO,COUNT(DISTINCT CUTNO) CustCount  FROM( " +
                        "  SELECT a.EID,a.CUSTGROUPNO,CASE WHEN a.ATTRTYPE='1' THEN c.ID ELSE A.ATTRID END CUTNO " +
                        "  FROM DCP_CUSTGROUP_DETAIL a " +
                        "  LEFT JOIN dcp_tagtype_detail c on a.EID=c.EID AND a.ATTRTYPE=1 and c.TAGGROUPTYPE='CUST' AND c.TAGNO=a.ATTRID )a" +
                        "  GROUP BY  a.EID,a.CUSTGROUPNO";

        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT * FROM( ");

        builder.append(" SELECT count(1) over () as num,row_number() over (order by a.CUSTGROUPNO desc) as rn," +
                "  a.*,em0.name as CREATEOPNAME,em1.name as LASTMODIOPNAME,dd0.DEPARTNAME as CREATEDEPTNAME," +
                "  b.ATTRTYPE,b.ATTRID,b.STATUS as detailStatus,case when b.ATTRTYPE='1' THEN c.TAGNAME else d.sname end as ATTRNAME   " +
                "  FROM DCP_CUSTGROUP a " +
                "  LEFT JOIN DCP_CUSTGROUP_DETAIL b ON a.EID=b.EID AND a.CUSTGROUPNO=b.CUSTGROUPNO " +
                "  LEFT JOIN DCP_TAGTYPE_LANG c on a.EID=b.EID AND b.ATTRTYPE='1' and c.TAGGROUPTYPE='CUST' AND c.TAGNO=b.ATTRID and c.LANG_TYPE='"+req.getLangType()+"'" +
                "  LEFT JOIN dcp_bizpartner d on d.EID=b.EID AND b.ATTRTYPE='2' and b.ATTRID = d.BIZPARTNERNO  " +
                "  left join DCP_employee em0 on em0.eid=a.eid and em0.employeeno=a.CREATEOPID " +
                "  left join DCP_employee em1 on em1.eid=a.eid and em1.employeeno=a.LASTMODIOPID " +
                "  left join dcp_department_lang dd0 on dd0.eid=a.eid and dd0.departno=a.CREATEDEPTID and dd0.lang_type='" + req.getLangType() + "'" +
                "  where a.eid='" + req.geteId() + "'"
        );

        if (StringUtils.isNotEmpty(req.getRequest().getCustGroupNo())) {
            builder.append(" AND a.CUSTGROUPNO='").append(req.getRequest().getCustGroupNo()).append("'");
        }

        builder.append(") temp  ");

        builder.append(" where  rn>").append(startRow).append(" and rn<=").append(startRow + pageSize).append("  ").append(" ");

        return builder.toString();
    }
}
