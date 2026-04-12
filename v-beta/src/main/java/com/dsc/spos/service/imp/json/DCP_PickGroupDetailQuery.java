package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PickGroupDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PickGroupDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PickGroupDetailQuery extends SPosBasicService<DCP_PickGroupDetailQueryReq, DCP_PickGroupDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PickGroupDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PickGroupDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_PickGroupDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_PickGroupDetailQueryRes getResponseType() {
        return new DCP_PickGroupDetailQueryRes();
    }

    @Override
    protected DCP_PickGroupDetailQueryRes processJson(DCP_PickGroupDetailQueryReq req) throws Exception {
        DCP_PickGroupDetailQueryRes res = this.getResponseType();

        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        List<Map<String, Object>> rangeList = this.doQueryData(getRangeQuerySql(req), conditionValues1);
        List<Map<String, Object>> objectList = this.doQueryData(getObjectQuerySql(req), conditionValues1);
        res.setDatas(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {


            for (Map<String, Object> data : getData) {
                DCP_PickGroupDetailQueryRes.Datas oneData = res.new Datas();

                oneData.setPickGroupNo(StringUtils.toString(data.get("PICKGROUPNO"), ""));
                oneData.setPickGroupName(StringUtils.toString(data.get("PICKGROUPNAME"), ""));
                oneData.setWarehouse(StringUtils.toString(data.get("WAREHOUSE"), ""));
                oneData.setWarehouseName(StringUtils.toString(data.get("WAREHOUSE_NAME"), ""));
                oneData.setWareRegionNo(StringUtils.toString(data.get("WAREREGIONNO"), ""));
                oneData.setWareRegionName(StringUtils.toString(data.get("WAREREGIONNAME"), ""));
                oneData.setPickType(StringUtils.toString(data.get("PICKTYPE"), ""));
                oneData.setRangeType(StringUtils.toString(data.get("PICKRANGETYPE"), ""));
                oneData.setObjectRange(StringUtils.toString(data.get("OBJECTRANGE"), ""));
                oneData.setStatus(StringUtils.toString(data.get("STATUS"), ""));
                oneData.setMemo(StringUtils.toString(data.get("MEMO"), ""));
                oneData.setCreateOpId(StringUtils.toString(data.get("CREATEOPID"), ""));
                oneData.setCreateOpName(StringUtils.toString(data.get("CREATEOPNAME"), ""));
                oneData.setCreateTime(StringUtils.toString(data.get("CREATETIME"), ""));
                oneData.setLastModiOpId(StringUtils.toString(data.get("LASTMODIOPID"), ""));
                oneData.setLastModiOpName(StringUtils.toString(data.get("LASTMODIOPNAME"), ""));
                oneData.setLastModiTime(StringUtils.toString(data.get("LASTMODITIME"), ""));

                oneData.setObjectList(new ArrayList<>());
                oneData.setRangeList(new ArrayList<>());

                if (null != rangeList && !rangeList.isEmpty()) {
                    for (Map<String, Object> rangeData : rangeList) {
                        DCP_PickGroupDetailQueryRes.RangeList oneRange = res.new RangeList();

                        oneRange.setCode(StringUtils.toString(rangeData.get("CODE"), ""));
                        oneRange.setType(StringUtils.toString(rangeData.get("TYPE"), ""));
                        oneRange.setName(StringUtils.toString(rangeData.get("NAME"), ""));

                        if (null != rangeData.get("SORTID")) {
                            oneRange.setSortId(StringUtils.toString(rangeData.get("SORTID"), ""));
                        }

                        oneData.getRangeList().add(oneRange);
                    }
                }

                if (null != objectList && !objectList.isEmpty()) {
                    for (Map<String, Object> objectData : objectList) {
                        DCP_PickGroupDetailQueryRes.ObjectList oneObject = res.new ObjectList();

                        oneObject.setCode(StringUtils.toString(objectData.get("CODE"), ""));
                        oneObject.setType(StringUtils.toString(objectData.get("TYPE"), ""));
                        oneObject.setName(StringUtils.toString(objectData.get("NAME"), ""));
                        oneObject.setSortId(StringUtils.toString(objectData.get("SORTID"), ""));

                        oneData.getObjectList().add(oneObject);
                    }
                }

                res.getDatas().add(oneData);

            }

        }

        res.setSuccess(true);
        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    private String getRangeQuerySql(DCP_PickGroupDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        //1.品类 2.商品 3.库位
        sb.append("select b.*, CASE WHEN b.TYPE='1' THEN cl.CATEGORY_NAME WHEN TYPE='2' THEN gl.PLU_NAME ELSE ml.LOCATIONNAME END AS NAME ")
                .append(" ,ml.SORTID,mw.WAREREGIONNAME ")
                .append(" FROM DCP_PICKGROUP a ")
                .append(" INNER JOIN DCP_PICKGROUP_RANGE b ON a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.PICKGROUPNO=b.PICKGROUPNO ")
                .append(" LEFT JOIN DCP_CATEGORY_LANG cl on cl.EID=b.EID and cl.CATEGORY=b.CODE and b.TYPE='1' and cl.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_GOODS_LANG gl on gl.EID=b.EID and gl.PLUNO=b.CODE and b.TYPE='2' and gl.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_LOCATION ml on ml.EID=b.EID and ml.LOCATION=b.CODE and a.WAREHOUSE=ml.WAREHOUSE  and b.TYPE='3'")
                .append(" LEFT JOIN MES_WAREREGION mw on mw.eid=a.eid and mw.WAREREGIONNO=a.WAREREGIONNO and a.WAREHOUSE=mw.WAREHOUSENO ")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getPickGroupNo())) {
            sb.append(" AND a.PICKGROUPNO='").append(req.getRequest().getPickGroupNo()).append("'");
        }


        return sb.toString();
    }

    private String getObjectQuerySql(DCP_PickGroupDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        //1.路线 2.组织 3.客户
        sb.append("select b.*,CASE WHEN b.TYPE='1' THEN mr.ROUTENAME WHEN TYPE='2' THEN gl.ORG_NAME ELSE cl.CUSTOMER_NAME END AS NAME ")
                .append(" FROM DCP_PICKGROUP a ")
                .append(" INNER JOIN DCP_PICKGROUP_OBJECT b ON a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.PICKGROUPNO=b.PICKGROUPNO ")
                .append(" LEFT JOIN MES_ROUTE mr on mr.EID=b.EID and mr.ROUTENO=b.CODE and b.TYPE='1' ")
                .append(" LEFT JOIN DCP_ORG_LANG gl on gl.EID=b.EID and gl.ORGANIZATIONNO=b.CODE and b.TYPE='2' and gl.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CUSTOMER cl on cl.EID=b.EID and cl.CUSTOMERNO=b.CODE and b.TYPE='3'")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getPickGroupNo())) {
            sb.append(" AND a.PICKGROUPNO='").append(req.getRequest().getPickGroupNo()).append("'");
        }
        return sb.toString();
    }

    @Override
    protected String getQuerySql(DCP_PickGroupDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.PICKGROUPNO DESC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.*, ")
                .append(" em1.name AS CREATEOPNAME,em2.name AS LASTMODIOPNAME,dd0.DEPARTNAME AS CREATEDEPTNAME ")
                .append(",wl1.WAREHOUSE_NAME,mw.WAREREGIONNAME  ")
                .append(" FROM DCP_PICKGROUP a ")
                .append(" left join dcp_warehouse_lang wl1 on wl1.eid=a.eid and wl1.warehouse=a.warehouse and wl1.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_employee em1 ON em1.eid = a.eid AND em1.employeeno = a.CREATEOPID ")
                .append(" LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.LASTMODIOPID ")
                .append(" LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.CREATEDEPTID AND dd0.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN MES_WAREREGION mw on mw.eid=a.eid and mw.WAREREGIONNO=a.WAREREGIONNO and a.WAREHOUSE=mw.WAREHOUSENO ")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getPickGroupNo())) {
            sb.append(" AND a.PICKGROUPNO='").append(req.getRequest().getPickGroupNo()).append("'");
        }

        sb.append("  )  a ORDER BY PICKGROUPNO ");

        return sb.toString();
    }
}
