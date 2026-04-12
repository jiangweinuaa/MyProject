package com.dsc.spos.service.imp.json;


import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_LocationQueryReq;
import com.dsc.spos.json.cust.res.DCP_LocationQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DCP_LocationQuery extends SPosBasicService<DCP_LocationQueryReq, DCP_LocationQueryRes> {

    public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();

    @Override
    protected boolean isVerifyFail(DCP_LocationQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_LocationQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_LocationQueryReq>() {
        };
    }

    @Override
    protected DCP_LocationQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_LocationQueryRes();
    }

    @Override
    protected DCP_LocationQueryRes processJson(DCP_LocationQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_LocationQueryRes res = null;
        res = this.getResponse();
        String sql = null;
        //try {

        sql = this.getCountSql(req);
        String[] condCountValues = {}; //查詢條件
        List<Map<String, Object>> getReasonCount = this.doQueryData(sql, condCountValues);
        int totalRecords;                                //总笔数
        int totalPages;                                    //总页数
        if (getReasonCount != null && getReasonCount.isEmpty() == false) {
            Map<String, Object> total = getReasonCount.get(0);
            String num = total.get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
        } else {
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        sql = this.getQuerySql(req);

        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);
        sql = this.getLocationQuerySql(req);
        List<Map<String, Object>> getQLangDataDetail = this.doQueryData(sql, conditionValues1);
        String eID = req.geteId();
        String billNo = "";
        String payType = "";
        res.setDatas(new ArrayList<DCP_LocationQueryRes.DataDetail>());
        if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
            List<Map<String, String>> returnCollection = getQDataDetail.stream().map(x -> {
                Map<String, String> map = new HashMap<>();
                map.put("ORGANIZATIONNO", x.get("ORGANIZATIONNO").toString());
                map.put("ORG_NAME", x.get("ORG_NAME").toString());
                map.put("WAREHOUSE", x.get("WAREHOUSE").toString());
                map.put("WAREHOUSE_NAME", x.get("WAREHOUSE_NAME").toString());
                return map;

            }).distinct().collect(Collectors.toList());

            for (Map<String, String> oneData : returnCollection) {
                DCP_LocationQueryRes.DataDetail oneLv1 = res.new DataDetail();


                oneLv1.setOrgNo(oneData.get("ORGANIZATIONNO").toString());
                oneLv1.setOrgName(oneData.get("ORG_NAME").toString());
                oneLv1.setWareHouse(oneData.get("WAREHOUSE").toString());
                oneLv1.setWareHouseName(oneData.get("WAREHOUSE_NAME").toString());
                String id = eID;
                String bill = oneData.get("WAREHOUSE").toString();
                String pType = oneData.get("ORGANIZATIONNO").toString();
                //组织先筛选出符合当前条件的数据
                List<Map<String, Object>> filterRows = getQLangDataDetail.stream()
                        .filter(LangData -> id.equals(LangData.get("EID").toString())
                                && bill.equals(LangData.get("WAREHOUSE").toString()))
                        .collect(Collectors.toList());

                oneLv1.setLocationList(new ArrayList<DCP_LocationQueryRes.LocationList>());
                for (Map<String, Object> oneData2 : filterRows) {
                    DCP_LocationQueryRes.LocationList oneLv2 = res.new LocationList();
                    oneLv2.setLocation(oneData2.get("LOCATION").toString());
                    oneLv2.setLocationName(oneData2.get("LOCATIONNAME").toString());
                    oneLv2.setContent(oneData2.get("CONTENT").toString());
                    oneLv2.setStatus(oneData2.get("STATUS").toString());
                    oneLv2.setSortId(oneData2.get("SORTID").toString());
                    oneLv2.setLocationType(oneData2.get("LOCATIONTYPE").toString());
                    oneLv2.setWareRegionNo(oneData2.get("WAREREGIONNO").toString());
                    oneLv2.setWareRegionName(oneData2.get("WAREREGIONNAME").toString());
                    oneLv2.setCreatorID(oneData2.get("CREATEOPID").toString());
                    oneLv2.setCreatorName(oneData2.get("CREATEOPNAME").toString());
                    oneLv2.setCreateDeptID(oneData2.get("CREATEDEPTID").toString());
                    oneLv2.setCreateDateTime(oneData2.get("CREATETIME").toString());
                    oneLv2.setLastModifyID(oneData2.get("LASTMODIOPID").toString());
                    oneLv2.setLastModifyName(oneData2.get("LASTMODIOPNAME").toString());
                    oneLv2.setLastModifyDateTime(oneData2.get("LASTMODITIME").toString());
                    ;
                    oneLv1.getLocationList().add(oneLv2);
                    oneLv2 = null;
                }


                res.getDatas().add(oneLv1);
                oneLv1 = null;
            }
        }


        //} catch (Exception e) {
        // TODO Auto-generated catch block
        //	res.setDatas(new ArrayList<DCP_LocationQueryRes.DataDetail>());
        //	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
        //}

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_LocationQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        String sql = null;
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        String langType = req.getLangType();
        if (langType == null || langType.isEmpty()) {
            langType = "zh_CN";
        }

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String key = null;
        String type = null;
        String status = null;
        String isBillQuery = null;
        String eId = req.geteId();
        String ware = null;
        if (req.getRequest() != null) {
            key = req.getRequest().getOrgNo();
            ware = req.getRequest().getWareHouse();

        }

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT distinct * FROM ("
                + " SELECT  row_number() over (order by a.SORTID, a.ORGANIZATIONNO, a.WAREHOUSE )  AS rn ,  a.ORGANIZATIONNO,f.org_name,G.WAREHOUSE_NAME,a.warehouse,a.sortid,a.LOCATIONTYPE,a.WAREREGIONNO,h.WAREREGIONNAME  "
                + " FROM DCP_LOCATION a"
                + " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
                + " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
                + " left join DCP_ORG_LANG f on f.eid=a.eid and f.ORGANIZATIONNO=a.ORGANIZATIONNO and f.lang_type='" + langType + "' "
                + " left join DCP_WAREHOUSE_LANG  g on G.eid=a.eid and G.ORGANIZATIONNO=a.ORGANIZATIONNO AND G.WAREHOUSE=A.WAREHOUSE and g.lang_type='" + langType + "' "
                + "  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='" + langType + "' "
                + "  left join MES_WAREREGION h on h.eid=a.eid and h.WAREREGIONNO=a.WAREREGIONNO and a.warehouse=h.WAREHOUSENO "
                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and   a.ORGANIZATIONNO  like  " + SUtil.RetLikeStr(key));
        }
        if (ware != null && ware.length() > 0)
            sqlbuf.append(" and a.WAREHOUSE like " + SUtil.RetLikeStr(ware));


        sqlbuf.append("  ) DBL  WHERE rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" order by SORTID,ORGANIZATIONNO,WAREHOUSE ");


        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }


    private String getLocationQuerySql(DCP_LocationQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        String sql = null;
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        String langType = req.getLangType();
        if (langType == null || langType.isEmpty()) {
            langType = "zh_CN";
        }

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String key = null;
        String type = null;
        String status = null;
        String isBillQuery = null;
        String eId = req.geteId();
        String ware = null;
        if (req.getRequest() != null) {
            key = req.getRequest().getOrgNo();
            ware = req.getRequest().getWareHouse();

        }

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM ("
                + " SELECT  row_number() over (order by a.SORTID, a.ORGANIZATIONNO, a.WAREHOUSE )  AS rn ,  a.*  "
                + "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName,h.WAREREGIONNAME "
                + " FROM DCP_LOCATION a"
                + " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
                + " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
                + " left join MES_WAREREGION h on h.eid=a.eid and h.WAREREGIONNO=a.WAREREGIONNO and a.warehouse=h.WAREHOUSENO "
                + "  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='" + langType + "' "
                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and   a.ORGANIZATIONNO  like  " + SUtil.RetLikeStr(key));
        }
        if (ware != null && ware.length() > 0)
            sqlbuf.append(" and a.WAREHOUSE like " + SUtil.RetLikeStr(ware));


        sqlbuf.append(" ) DBL  WHERE rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" order by SORTID,ORGANIZATIONNO,WAREHOUSE ");


        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }


    /**
     * 新增要處理的資料(先加入的, 先處理)
     *
     * @param row
     */
    protected final void addProcessData(DataProcessBean row) {
        this.pData.add(row);
    }

    /**
     * 计算总数
     *
     * @param req
     * @return
     * @throws Exception
     */
    protected String getCountSql(DCP_LocationQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String key = null;
        String status = null;
        String eId = req.geteId();
        String ware = null;
        if (req.getRequest() != null) {
            key = req.getRequest().getOrgNo();
            ware = req.getRequest().getWareHouse();
        }

        StringBuffer sqlbuf = new StringBuffer("");
        String sql = "";

        sqlbuf.append("select num from( select count(*) AS num from ( select distinct a.EID,a.ORGANIZATIONNO,a.warehouse,a.LOCATION  from DCP_LOCATION a "
                + " where a.EID='" + eId + "'  ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and   a.ORGANIZATIONNO  like  " + SUtil.RetLikeStr(key));

        }
        if (ware != null && ware.length() > 0)
            sqlbuf.append(" and  a.WAREHOUSE like " + SUtil.RetLikeStr(ware));
        sqlbuf.append(")  )");

        sql = sqlbuf.toString();
        return sql;
    }


}
