package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SortingAssignPendingQueryReq;
import com.dsc.spos.json.cust.res.DCP_SortingAssignPendingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_SortingAssignPendingQuery extends SPosBasicService<DCP_SortingAssignPendingQueryReq, DCP_SortingAssignPendingQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_SortingAssignPendingQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SortingAssignPendingQueryReq> getRequestType() {
        return new TypeToken<DCP_SortingAssignPendingQueryReq>() {
        };
    }

    @Override
    protected DCP_SortingAssignPendingQueryRes getResponseType() {
        return new DCP_SortingAssignPendingQueryRes();
    }

    @Override
    protected DCP_SortingAssignPendingQueryRes processJson(DCP_SortingAssignPendingQueryReq req) throws Exception {
        DCP_SortingAssignPendingQueryRes res = this.getResponseType();

        int totalRecords = 0;                //总笔数
        int totalPages = 0;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        List<Map<String, Object>> routeData = this.doQueryData(getQueryRouteSql(req), conditionValues1);

        res.setDatas(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();


            Map<String, Boolean> headCondition = new HashMap<>();
            headCondition.put("EID", true);
            headCondition.put("BILLNO", true);
            headCondition.put("SOURCEBILLNO", true);
            headCondition.put("DOBJECTID", true);
            headCondition.put("TEMPLATENO", true);

            List<Map<String, Object>> headData = MapDistinct.getMap(getData, headCondition);

            for (Map<String, Object> data : headData) {
                DCP_SortingAssignPendingQueryRes.Datas oneData = res.new Datas();

                totalRecords++;

                oneData.setNoticeNo(data.get("BILLNO").toString());
                oneData.setOType(data.get("SOURCETYPE").toString());
                oneData.setOfNo(data.get("SOURCEBILLNO").toString());
                oneData.setObjectType(data.get("OBJECTTYPE").toString());
                oneData.setObjectId(data.get("DOBJECTID").toString());
                oneData.setObjectName(data.get("DOBJECTNAME").toString());
                oneData.setDeliveryDate(data.get("DELIVERYDATE").toString());
                oneData.setRDate(data.get("RDATE").toString());
                oneData.setWarehouse(data.get("WAREHOUSE").toString());
                oneData.setWarehouseName(data.get("WAREHOUSE_NAME").toString());
                oneData.setStatus(data.get("STATUS").toString());
                oneData.setTotCqty(data.get("TOTCQTY").toString());
                oneData.setTotPqty(data.get("TOTPQTY").toString());
                oneData.setTemplateNo(data.get("DTEMPLATENO").toString());
                oneData.setTemplateName(data.get("TEMPLATENAME").toString());

                Map<String, Object> detailCondition = new HashMap<>();
                detailCondition.put("EID", data.get("EID").toString());
                detailCondition.put("BILLNO", data.get("BILLNO").toString());
                detailCondition.put("SOURCEBILLNO", data.get("SOURCEBILLNO").toString());
                detailCondition.put("DOBJECTID", data.get("DOBJECTID").toString());
                detailCondition.put("DTEMPLATENO", data.get("DTEMPLATENO").toString());
                List<Map<String, Object>> detailData = MapDistinct.getWhereMap(getData, detailCondition, true);

                oneData.setDetail(new ArrayList<>());
                res.getDatas().add(oneData);
                for (Map<String, Object> detail : detailData) {
                    DCP_SortingAssignPendingQueryRes.Detail oneDetail = res.new Detail();

                    oneDetail.setRowNum(detail.get("RN").toString());
                    oneDetail.setItem(detail.get("ITEM").toString());
                    oneDetail.setSourceType(detail.get("DSOURCETYPE").toString());
                    oneDetail.setSourceBillNo(detail.get("DSOURCEBILLNO").toString());
                    oneDetail.setOItem(detail.get("OITEM").toString());
                    oneDetail.setPluNo(detail.get("PLUNO").toString());
                    oneDetail.setPluName(detail.get("PLU_NAME").toString());
                    oneDetail.setSpec(detail.get("SPEC").toString());
                    oneDetail.setFeatureNo(detail.get("FEATURENO").toString());
                    oneDetail.setFeatureName(detail.get("FEATURENAME").toString());
                    oneDetail.setCategory(detail.get("CATEGORY").toString());
                    oneDetail.setCategoryName(detail.get("CATEGORY_NAME").toString());
                    oneDetail.setPUnit(detail.get("PUNIT").toString());
                    oneDetail.setPUnitName(detail.get("PUNITNAME").toString());
                    oneDetail.setPQty(detail.get("PQTY").toString());
                    oneDetail.setPoQty(detail.get("POQTY").toString());
                    oneDetail.setStockOutQty(detail.get("STOCKOUTQTY").toString());
                    oneDetail.setTemplateNo(detail.get("DTEMPLATENO").toString());
                    oneDetail.setTemplateName(detail.get("TEMPLATENAME").toString());
                    oneDetail.setBaseUnit(detail.get("BASEUNIT").toString());
                    oneDetail.setBaseQty(detail.get("BASEQTY").toString());
                    oneDetail.setUnitRatio(detail.get("UNITRATIO").toString());
                    oneDetail.setStatus(detail.get("DSTATUS").toString());

                    oneData.getDetail().add(oneDetail);
                }

                oneData.setRouteList(new ArrayList<>());

                Map<String, Object> routeCondition = new HashMap<>();
                routeCondition.put("BILLNO", data.get("BILLNO").toString());
                List<Map<String, Object>> conditionRouteData = MapDistinct.getWhereMap(routeData, routeCondition, true);
                Map<String, Boolean> routeDistinct = new HashMap<>();
                routeDistinct.put("ROUTENO", true);
                List<Map<String, Object>> distinctRouteData = MapDistinct.getMap(conditionRouteData, routeDistinct);

                for (Map<String, Object> route : distinctRouteData) {
                    DCP_SortingAssignPendingQueryRes.RouteList oneRoute = res.new RouteList();
                    oneRoute.setRouteNo(route.get("ROUTENO").toString());
                    oneRoute.setRouteName(route.get("ROUTENAME").toString());

                    oneData.getRouteList().add(oneRoute);
                }

            }
        }

        //算總頁數
        totalPages = totalRecords / req.getPageSize();
        totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    protected String getQueryMasterSql(DCP_SortingAssignPendingQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ");

        sb.append("SELECT row_number( ) OVER (PARTITION  BY BILLNO,OBJECTID,SOURCEBILLNO,TEMPLATENO order by BILLNO,OBJECTID,SOURCEBILLNO,TEMPLATENO) AS RN," +
                        " COUNT(*) OVER ( ) NUM,a.* FROM (" +
                        " SELECT DISTINCT b.EID,b.BILLNO,b.OBJECTID,b.SOURCEBILLNO,b.TEMPLATENO ")
                .append(" FROM DCP_STOCKOUTNOTICE a ")
                .append(" LEFT JOIN DCP_STOCKOUTNOTICE_DETAIL b on a.EID = b.EID and a.BILLNO = b.BILLNO")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        sb.append(" AND (a.BILLTYPE='2' OR  a.BILLTYPE='3') ");
        sb.append(" AND a.DELIVERORGNO='").append(req.getOrganizationNO()).append("'");
        sb.append(" AND (a.STATUS<>'0' AND a.STATUS<>'2' AND a.STATUS<>'3' ) ");
        sb.append(" AND b.STATUS='1' ");
        sb.append(" AND b.PQTY-b.STOCKOUTQTY >0 ");

        if (StringUtils.isNotEmpty(req.getRequest().getWarehouse())) {
            sb.append(" AND a.WAREHOUSE='").append(req.getRequest().getWarehouse()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
            sb.append(" AND ( b.SOURCEBILLNO like '%%").append(req.getRequest().getKeyTxt()).append("'")
                    .append(" OR b.BILLNO like '%%").append(req.getRequest().getKeyTxt()).append("'")
                    .append(")");
        }

//        rDate需求日期，deliveryDate:预计出货日
        if ("rDate".equals(req.getRequest().getDateType())) {
            if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
                sb.append(" AND a.RDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
            }
            if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
                sb.append(" AND a.RDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
            }

        } else {
            if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
                sb.append(" AND a.DELIVERYDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
            }
            if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
                sb.append(" AND a.DELIVERYDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
            }
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getPTemplateNo())) {
            sb.append(" AND ( 1=2 ");
            for (String pTemplate : req.getRequest().getPTemplateNo()) {
                sb.append(" OR b.TEMPLATENO='").append(pTemplate).append("'");
            }
            sb.append(")");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getRouteNo())) {
            sb.append(" AND ( EXISTS ( SELECT * FROM (")
                    .append(getQueryRouteSql(req))
                    .append(" ) t WHERE a.OBJECTID=t.CODE ) ) ");
        }

        sb.append(" AND NOT EXISTS ( SELECT * FROM DCP_SORTINGASSIGN_DETAIL sd WHERE sd.OFNO=b.BILLNO and sd.OITEM=b.ITEM ) ");

        sb.append("  ) a ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + "  ");
        return sb.toString();
    }

    protected String getQueryRouteSql(DCP_SortingAssignPendingQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT DISTINCT a.ROUTENO,a.ROUTENAME,b.CODE,c.BILLNO  " +
                " FROM DCP_STOCKOUTNOTICE c " +
                " LEFT JOIN MES_ROUTE_DETAIL b ON c.eid=b.eid AND c.OBJECTID=b.CODE " +
                " LEFT JOIN MES_ROUTE a ON a.eid=b.eid and a.ROUTENO=b.ROUTENO  ");

        sb.append(" WHERE a.eid='").append(req.geteId()).append("'");
        sb.append(" AND a.STATUS='100' ");
        sb.append(" AND (c.BILLTYPE='2' OR  c.BILLTYPE='3') ");
        sb.append(" AND c.DELIVERORGNO='").append(req.getOrganizationNO()).append("'");
        sb.append(" AND (c.STATUS<>'0' AND c.STATUS<>'2' AND c.STATUS<>'3' ) ");
//        sb.append(" AND b.STATUS='1' ");
//        sb.append(" AND b.PQTY-b.STOCKOUTQTY >0 ");

        if (StringUtils.isNotEmpty(req.getRequest().getWarehouse())) {
            sb.append(" AND c.WAREHOUSE='").append(req.getRequest().getWarehouse()).append("'");
        }

//        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
//            sb.append(" AND ( b.SOURCEBILLNO like '%%").append(req.getRequest().getKeyTxt()).append("'")
//                    .append(" OR b.BILLNO like '%%").append(req.getRequest().getKeyTxt()).append("'")
//                    .append(")");
//        }

//        rDate需求日期，deliveryDate:预计出货日
        if ("rDate".equals(req.getRequest().getDateType())) {
            if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
                sb.append(" AND c.RDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
            }
            if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
                sb.append(" AND c.RDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
            }

        } else {
            if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
                sb.append(" AND c.DELIVERYDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
            }
            if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
                sb.append(" AND c.DELIVERYDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
            }
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getRouteNo())) {
            sb.append(" AND ( 1=2 ");
            for (String s : req.getRequest().getRouteNo()) {
                sb.append(" OR a.ROUTENO='").append(s).append("'");
            }
            sb.append(" ) ");
        }

        return sb.toString();
    }

    @Override
    protected String getQuerySql(DCP_SortingAssignPendingQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
//        int pageNumber = req.getPageNumber();
//        int pageSize = req.getPageSize();
//        //計算起啟位置
//        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ");

        sb.append("SELECT row_number( ) OVER (PARTITION  BY b.BILLNO,b.OBJECTID,b.SOURCEBILLNO,b.TEMPLATENO order by b.BILLNO,b.OBJECTID,b.SOURCEBILLNO,b.TEMPLATENO,b.ITEM) AS RN," +
                        " COUNT(*) OVER ( ) NUM," +
                        " A.*  ")
                .append(" ,b.ITEM,b.SOURCETYPE DSOURCETYPE,b.SOURCEBILLNO DSOURCEBILLNO,b.OITEM,b.PLUNO ")
                .append(" ,b.FEATURENO,b.PUNIT,b.PQTY,b.TEMPLATENO DTEMPLATENO,b.POQTY ")
                .append(" ,b.BASEQTY,b.BASEUNIT,b.UNITRATIO,b.STOCKOUTQTY ")
                .append(" ,b.STATUS DSTATUS,b.OBJECTTYPE DOBJECTTYPE,b.OBJECTID DOBJECTID,b.TEMPLATETYPE ")
                .append(" ,c.SPEC,c.CATEGORY ")
                .append(" ,wl1.WAREHOUSE_NAME,ul1.UNAME as PUNITNAME,gl1.PLU_NAME ")
                .append(" ,cl1.CATEGORY_NAME,fl1.FEATURENAME ")
                .append(" ,CASE WHEN b.TEMPLATETYPE='1' THEN ptl1.NAME " +
                        "       WHEN b.TEMPLATETYPE='3' THEN gtl1.TEMPLATENAME " +
                        "       ELSE pt1.PTEMPLATE_NAME " +
                        "       END AS TEMPLATENAME ")
                .append(" ,CASE WHEN a.OBJECTTYPE='1' THEN bp1.SNAME " +
                        "       WHEN a.OBJECTTYPE='2' THEN cl1.CUSTOMER_NAME" +
                        "       ELSE ol1.ORG_NAME " +
                        "       END AS OBJECTNAME ")
                .append(" ,CASE WHEN b.OBJECTTYPE='1' THEN bp2.SNAME " +
                        "       WHEN b.OBJECTTYPE='2' THEN cl2.CUSTOMER_NAME" +
                        "       ELSE ol2.ORG_NAME " +
                        "       END AS DOBJECTNAME ")
                .append(" FROM DCP_STOCKOUTNOTICE a ")
                .append(" LEFT JOIN DCP_STOCKOUTNOTICE_DETAIL b on a.EID = b.EID and a.BILLNO = b.BILLNO")
                .append(" INNER JOIN ( ").append(getQueryMasterSql(req)).append(") master on b.eid=master.eid " +
                        " AND b.BILLNO=master.BILLNO and b.OBJECTID=master.OBJECTID AND NVL(b.SOURCEBILLNO,' ') = NVL(master.SOURCEBILLNO,' ') and NVL(b.TEMPLATENO,' ')=NVL(master.TEMPLATENO,' ') ")
                .append(" LEFT JOIN DCP_GOODS c ON b.eid=c.eid and b.PLUNO=c.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=b.eid and gl1.PLUNO=b.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 on ul1.eid=b.eid and ul1.UNIT=b.PUNIT and ul1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_WAREHOUSE_LANG wl1 on wl1.eid=a.eid and wl1.WAREHOUSE=a.WAREHOUSE and wl1.LANG_TYPE='").append(req.getLangType()).append("' ")
                .append(" LEFT JOIN DCP_CATEGORY_LANG cl1 on cl1.eid=c.eid and cl1.CATEGORY=c.CATEGORY and cl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_GOODSTEMPLATE_LANG gtl1 on gtl1.eid=b.eid and b.TEMPLATETYPE='3' and gtl1.TEMPLATEID=b.TEMPLATENO and gtl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_PURCHASETEMPLATE_LANG ptl1 on ptl1.eid=b.eid and b.TEMPLATETYPE='1' and ptl1.PURTEMPLATENO=b.TEMPLATENO and ptl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_PTEMPLATE pt1 on pt1.eid=b.eid and pt1.PTEMPLATENO=b.TEMPLATENO")
                .append(" LEFT JOIN DCP_GOODS_FEATURE_LANG fl1 on fl1.eid=b.eid and fl1.PLUNO=b.PLUNO and fl1.FEATURENO=b.FEATURENO and fl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_BIZPARTNER bp1 on bp1.eid=a.eid and bp1.BIZPARTNERNO=a.OBJECTID and a.OBJECTTYPE='1' ")
                .append(" LEFT JOIN DCP_BIZPARTNER bp2 on bp2.eid=b.eid and bp2.BIZPARTNERNO=b.OBJECTID and b.OBJECTTYPE='1' ")
                .append(" LEFT JOIN DCP_CUSTOMER_LANG cl1 on cl1.eid=a.eid and cl1.CUSTOMERNO=a.OBJECTID and a.OBJECTTYPE='2' ")
                .append(" LEFT JOIN DCP_CUSTOMER_LANG cl2 on cl2.eid=b.eid and cl2.CUSTOMERNO=b.OBJECTID and b.OBJECTTYPE='2' ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=a.eid and ol1.ORGANIZATIONNO=a.OBJECTID and a.OBJECTTYPE='3' ")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.eid=b.eid and ol2.ORGANIZATIONNO=b.OBJECTID and b.OBJECTTYPE='3' ")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        sb.append(" AND (a.BILLTYPE='2' OR  a.BILLTYPE='3') ");
        sb.append(" AND a.DELIVERORGNO='").append(req.getOrganizationNO()).append("'");
        sb.append(" AND (a.STATUS<>'0' AND a.STATUS<>'2' AND a.STATUS<>'3' ) ");
        sb.append(" AND b.STATUS='1' ");
        sb.append(" AND b.PQTY-b.STOCKOUTQTY >0 ");

        if (StringUtils.isNotEmpty(req.getRequest().getWarehouse())) {
            sb.append(" AND a.WAREHOUSE='").append(req.getRequest().getWarehouse()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
            sb.append(" AND ( b.SOURCEBILLNO like '%%").append(req.getRequest().getKeyTxt()).append("'")
                    .append(" OR b.BILLNO like '%%").append(req.getRequest().getKeyTxt()).append("'")
                    .append(")");
        }

//        rDate需求日期，deliveryDate:预计出货日
        if ("rDate".equals(req.getRequest().getDateType())) {
            if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
                sb.append(" AND a.RDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
            }
            if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
                sb.append(" AND a.RDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
            }

        } else {
            if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
                sb.append(" AND a.DELIVERYDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
            }
            if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
                sb.append(" AND a.DELIVERYDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
            }
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getPTemplateNo())) {
            sb.append(" AND ( 1=2 ");
            for (String pTemplate : req.getRequest().getPTemplateNo()) {
                sb.append(" OR b.TEMPLATENO='").append(pTemplate).append("'");
            }
            sb.append(")");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getObjectList())) {
            sb.append(" AND ( 1=2 ");
            for (DCP_SortingAssignPendingQueryReq.ObjectList objectList : req.getRequest().getObjectList()) {
                if (StringUtils.isNotEmpty(objectList.getObjectType())) {
                    sb.append(" OR b.OBJECTTYPE='").append(objectList.getObjectType()).append("'");
                }
                if (CollectionUtils.isNotEmpty(objectList.getObjectId())) {
                    sb.append(" AND ( 1=2 ");
                    for (String s : objectList.getObjectId()) {
                        sb.append(" OR b.OBJECTID='").append(s).append("'");
                    }
                    sb.append(")");
                }
            }
            sb.append(")");

        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getRouteNo())) {
            sb.append(" AND ( EXISTS ( SELECT * FROM (")
                    .append(getQueryRouteSql(req))
                    .append(" ) t WHERE a.OBJECTID=t.CODE ) ) ");
        }

        sb.append(" AND NOT EXISTS ( SELECT * FROM DCP_SORTINGASSIGN_DETAIL sd WHERE sd.OFNO=b.BILLNO and sd.OITEM=b.ITEM ) ");

        sb.append("  ) a "
//                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " order by BILLNO,DOBJECTID,SOURCEBILLNO,TEMPLATENO,ITEM ");
        return sb.toString();
    }
}
