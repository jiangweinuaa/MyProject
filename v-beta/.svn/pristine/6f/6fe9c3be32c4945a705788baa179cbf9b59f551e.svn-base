package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SortingAssignDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_SortingAssignDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_SortingAssignDetailQuery extends SPosBasicService<DCP_SortingAssignDetailQueryReq, DCP_SortingAssignDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_SortingAssignDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SortingAssignDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_SortingAssignDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_SortingAssignDetailQueryRes getResponseType() {
        return new DCP_SortingAssignDetailQueryRes();
    }

    @Override
    protected DCP_SortingAssignDetailQueryRes processJson(DCP_SortingAssignDetailQueryReq req) throws Exception {
        DCP_SortingAssignDetailQueryRes res = getResponseType();
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setDatas(new ArrayList<>());
        if (null != getData && !getData.isEmpty()) {

            Map<String, Boolean> masterCondition = Maps.newHashMap();
            masterCondition.put("EID", true);
            masterCondition.put("BILLNO", true);
            List<Map<String, Object>> datas = MapDistinct.getMap(getData, masterCondition);
            for (Map<String, Object> oneData : datas) {
                DCP_SortingAssignDetailQueryRes.Datas masterData = res.new Datas();

                masterData.setBillNo(StringUtils.toString(oneData.get("BILLNO"), ""));
                masterData.setOType(StringUtils.toString(oneData.get("OTYPE"), ""));
                masterData.setBDate(StringUtils.toString(oneData.get("BDATE"), ""));
                masterData.setWarehouse(StringUtils.toString(oneData.get("WAREHOUSE"), ""));
                masterData.setWarehouseName(StringUtils.toString(oneData.get("WAREHOUSE_NAME"), ""));
                masterData.setEmployeeId(StringUtils.toString(oneData.get("EMPLOYEEID"), ""));
                masterData.setEmployeeName(StringUtils.toString(oneData.get("EMPLOYEENAME"), ""));
                masterData.setDepartId(StringUtils.toString(oneData.get("DEPARTID"), ""));
                masterData.setDepartName(StringUtils.toString(oneData.get("DEPARTNAME"), ""));
                masterData.setStatus(StringUtils.toString(oneData.get("STATUS"), ""));
                masterData.setTotOrgCnt(StringUtils.toString(oneData.get("TOTORGCNT"), ""));
                masterData.setTotCqty(StringUtils.toString(oneData.get("TOTCQTY"), ""));
                masterData.setTotPqty(StringUtils.toString(oneData.get("TOTPQTY"), ""));
                masterData.setCreateOpId(StringUtils.toString(oneData.get("CREATEOPID"), ""));
                masterData.setCreateOpName(StringUtils.toString(oneData.get("CREATEOPNAME"), ""));
                masterData.setCreateDeptId(StringUtils.toString(oneData.get("CREATEDEPTID"), ""));
                masterData.setCreateDeptName(StringUtils.toString(oneData.get("CREATEDEPTNAME"), ""));
                masterData.setCreateTime(StringUtils.toString(oneData.get("CREATETIME"), ""));
                masterData.setLastModiOpId(StringUtils.toString(oneData.get("LASTMODIOPID"), ""));
                masterData.setLastModiOpName(StringUtils.toString(oneData.get("LASTMODIOPNAME"), ""));
                masterData.setLastModiTime(StringUtils.toString(oneData.get("LASTMODITIME"), ""));
                masterData.setConfirmBy(StringUtils.toString(oneData.get("CONFIRMBY"), ""));
                masterData.setConfirmByName(StringUtils.toString(oneData.get("CONFIRMBYNAME"), ""));
                masterData.setConfirmTime(StringUtils.toString(oneData.get("CONFIRMTIME"), ""));
                masterData.setCloseBy(StringUtils.toString(oneData.get("CLOSEBY"), ""));
                masterData.setCloseByName(StringUtils.toString(oneData.get("CLOSEBYNAME"), ""));
                masterData.setCloseTime(StringUtils.toString(oneData.get("CLOSETIME"), ""));
                masterData.setCancelBy(StringUtils.toString(oneData.get("CANCELBY"), ""));
                masterData.setCancelByName(StringUtils.toString(oneData.get("CANCELBYNAME"), ""));
                masterData.setCancelTime(StringUtils.toString(oneData.get("CANCELTIME"), ""));

                res.getDatas().add(masterData);

                masterData.setDetail(new ArrayList<>());

                Map<String, Object> detailCondition = Maps.newHashMap();
                detailCondition.put("EID", oneData.get("EID"));
                detailCondition.put("BILLNO", oneData.get("BILLNO"));
                List<Map<String, Object>> detailDatas = MapDistinct.getWhereMap(getData, detailCondition, true);

                // 派工列表：按[机构+通知单号+需求单号+要货模板+需求日期+预计交货日]汇总
                List<Map<String, Object>> sumList = getSumList(detailDatas);
                for (Map<String, Object> oneDetail : sumList) {

                    DCP_SortingAssignDetailQueryRes.Detail detailData = res.new Detail();

                    detailData.setObjectType(StringUtils.toString(oneDetail.get("OBJECTTYPE"), ""));
                    detailData.setObjectId(StringUtils.toString(oneDetail.get("OBJECTID"), ""));
                    detailData.setObjectName(StringUtils.toString(oneDetail.get("OBJECTNAME"), ""));
                    detailData.setRDate(StringUtils.toString(oneDetail.get("RDATE"), ""));
                    detailData.setDeliveryDate(StringUtils.toString(oneDetail.get("DELIVERYDATE"), ""));
                    detailData.setOfNo(StringUtils.toString(oneDetail.get("OFNO"), ""));
                    detailData.setOrderNo(StringUtils.toString(oneDetail.get("ORDERNO"), ""));
                    detailData.setRouteNo(StringUtils.toString(oneDetail.get("ROUTENO"), ""));
                    detailData.setRouteName(StringUtils.toString(oneDetail.get("ROUTENAME"), ""));
                    detailData.setPTemplateNo(StringUtils.toString(oneDetail.get("PTEMPLATENO"), ""));
                    detailData.setPTemplateName(StringUtils.toString(oneDetail.get("PTEMPLATENAME"), ""));


                    detailData.setIsAdditional(StringUtils.toString(oneDetail.get("ISADDITIONAL"), ""));
                    detailData.setAddTime(StringUtils.toString(oneDetail.get("ADDTIME"), ""));
                    detailData.setAddBy(StringUtils.toString(oneDetail.get("ADDBY"), ""));
                    detailData.setAddByName(StringUtils.toString(oneDetail.get("ADDBYNAME"), ""));
                    detailData.setAddIsDispatch(StringUtils.toString(oneDetail.get("ADDISDISPATCH"), ""));

                    detailData.setDataList(new ArrayList<>());

                    Map<String, Object> dataCondition = Maps.newHashMap();
                    dataCondition.put("EID", oneDetail.get("EID"));
                    dataCondition.put("BILLNO", oneDetail.get("BILLNO"));
                    dataCondition.put("OBJECTID", oneDetail.get("OBJECTID"));
                    dataCondition.put("OBJECTTYPE", oneDetail.get("OBJECTTYPE"));
                    dataCondition.put("PTEMPLATENO", oneDetail.get("PTEMPLATENO"));
                    dataCondition.put("ORDERNO", oneDetail.get("ORDERNO"));
                    dataCondition.put("OFNO", oneDetail.get("OFNO"));
                    dataCondition.put("RDATE", oneDetail.get("RDATE"));
                    dataCondition.put("DELIVERYDATE", oneDetail.get("DELIVERYDATE"));

                    List<Map<String, Object>> data = MapDistinct.getWhereMap(getData, dataCondition, true);

                    detailData.setTotRecords(StringUtils.toString(data.size(), ""));

                    List<String> totCQty = new ArrayList<>();
                    double totPQty = 0;
                    int i = 0;
                    for (Map<String, Object> oneDetailData : data) {
                        DCP_SortingAssignDetailQueryRes.DataList oneList = res.new DataList();
                        oneList.setRowNum(String.valueOf(++i));

                        String c = oneDetailData.get("PLUNO").toString() + oneDetailData.get("FEATURENO").toString();
                        if (!totCQty.contains(c)) {
                            totCQty.add(c);
                        }
                        oneList.setOItem(oneDetailData.get("OITEM").toString());
                        oneList.setOrderItem(oneDetailData.get("ORDERITEM").toString());
                        oneList.setPluNo(oneDetailData.get("PLUNO").toString());
                        oneList.setPluName(oneDetailData.get("PLUNAME").toString());
                        oneList.setFeatureNo(oneDetailData.get("FEATURENO").toString());
                        oneList.setFeatureName(oneDetailData.get("FEATURENAME").toString());
                        oneList.setSpec(oneDetailData.get("SPEC").toString());
                        oneList.setCategory(oneDetailData.get("CATEGORY").toString());
                        oneList.setCategoryName(oneDetailData.get("CATEGORYNAME").toString());
                        oneList.setPUnit(oneDetailData.get("PUNIT").toString());
                        oneList.setPUnitName(oneDetailData.get("PUNITNAME").toString());
                        oneList.setPQty(oneDetailData.get("PQTY").toString());
                        totPQty += Double.parseDouble(oneDetailData.get("PQTY").toString());
                        oneList.setNoQty(oneDetailData.get("NOQTY").toString());
                        oneList.setPoQty(oneDetailData.get("POQTY").toString());
                        oneList.setBaseUnit(oneDetailData.get("BASEUNIT").toString());
                        oneList.setUnitRatio(oneDetailData.get("UNITRATIO").toString());

                        detailData.getDataList().add(oneList);
                    }
                    detailData.setTotCqty(StringUtils.toString(totCQty.size(), ""));
                    detailData.setTotPqty(StringUtils.toString(totPQty, ""));


                    masterData.getDetail().add(detailData);
                }


            }

        }


        res.setSuccess(true);
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    private List<Map<String, Object>> getSumList(List<Map<String, Object>> qList) throws Exception {

        // 派工列表：按[机构+通知单号+需求单号+要货模板+需求日期+预计交货日]汇总

        Map<String, Boolean> condition = Maps.newHashMap();
        condition.put("OBJECTID", true);
        condition.put("OFNO", true);
        condition.put("ORDERNO", true);
        condition.put("PTEMPLATENO", true);
        condition.put("RDATE", true);
        condition.put("DELIVERYDATE", true);

        List<Map<String, Object>> sumList = MapDistinct.getMap(qList, condition);

        for (Map<String, Object> map : sumList) {
            Map<String, Object> mulCondition = Maps.newHashMap();
            mulCondition.put("OBJECTID", map.get("OBJECTID"));
            mulCondition.put("OFNO", map.get("OFNO"));
            mulCondition.put("ORDERNO", map.get("ORDERNO"));
            mulCondition.put("PTEMPLATENO", map.get("PTEMPLATENO"));
            mulCondition.put("RDATE", map.get("RDATE"));
            mulCondition.put("DELIVERYDATE", map.get("DELIVERYDATE"));

            List<Map<String, Object>> mul = MapDistinct.getWhereMap(qList, mulCondition, true);
            double totPQty = 0;
            List<String> totCQty = new ArrayList();
            for (Map<String, Object> map2 : mul) {
                totPQty += Double.parseDouble(map2.get("PQTY").toString());

                String pluNo = map2.get("PLUNO").toString() + map2.get("FEATURENO").toString();

                if (!totCQty.contains(pluNo)) {
                    totCQty.add(pluNo);
                }

            }

            map.put("TOTRECORDS", mul.size());
            map.put("TOTCQTY", totCQty.size());
            map.put("TOTPQTY", totPQty);


        }


        return sumList;
    }


    @Override
    protected String getQuerySql(DCP_SortingAssignDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT * FROM( ")
                .append(" SELECT ")
                .append(" a.* ")
                .append(" ,b.ITEM,b.OFNO,b.OITEM,b.ORDERTYPE,b.ORDERNO,b.ORDERITEM ")
                .append(" ,b.RDATE,b.DELIVERYDATE,b.OBJECTTYPE,b.OBJECTID,b.PLUNO ")
                .append(" ,b.FEATURENO,b.CATEGORY,b.PUNIT,b.POQTY,b.WAREHOUSE DETAILWAREHOUSE ")
                .append(" ,b.NOQTY,b.PQTY,b.BASEUNIT,b.BASEQTY,b.UNITRATIO,b.PTEMPLATENO,b.ROUTENO ")
                .append(" ,b.ISADDITIONAL,b.ADDTIME,b.ADDBY,b.ADDISDISPATCH ")
                .append(" ,em1.name AS CREATEOPNAME,em2.name AS LASTMODIOPNAME,em2.name AS EMPLOYEENAME ")
                .append(" ,em4.name AS CONFIRMBYNAME,em5.name AS CANCELBYNAME,em6.name AS CLOSEBYNAME ")
                .append(" ,dd0.DEPARTNAME AS DEPARTNAME,dd1.DEPARTNAME AS CREATEDEPTNAME,wl1.WAREHOUSE_NAME  ")
                .append(" ,em7.name as ADDBYNAME,dp.PTEMPLATE_NAME PTEMPLATENAME ,g.SPEC")
                .append(" ,ul1.UNAME PUNITNAME,ul2.UNAME BASEUNITNAME,cl.CATEGORY_NAME CATEGORYNAME ")
                .append(" ,mr.ROUTENAME,gl.PLU_NAME PLUNAME,fl.FEATURENAME ")
                .append(" ,CASE WHEN b.OBJECTTYPE='2' THEN cl1.CUSTOMER_NAME" +
                        "       ELSE ol1.ORG_NAME " +
                        "       END AS OBJECTNAME ")
                .append(" FROM DCP_SORTINGASSIGN a")
                .append(" INNER JOIN DCP_SORTINGASSIGN_DETAIL b ON a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.BILLNO=b.BILLNO ")
                .append(" left join dcp_warehouse_lang wl1 on wl1.eid=a.eid and wl1.warehouse=a.warehouse and wl1.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_employee em1 ON em1.eid = a.eid AND em1.employeeno = a.CREATEOPID ")
                .append(" LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.LASTMODIOPID ")
                .append(" LEFT JOIN DCP_employee em3 ON em3.eid = a.eid AND em3.employeeno = a.EMPLOYEEID ")
                .append(" LEFT JOIN DCP_employee em4 ON em4.eid = a.eid AND em4.employeeno = a.CONFIRMBY ")
                .append(" LEFT JOIN DCP_employee em5 ON em5.eid = a.eid AND em5.employeeno = a.CANCELBY ")
                .append(" LEFT JOIN DCP_employee em6 ON em6.eid = a.eid AND em6.employeeno = a.CLOSEBY ")
                .append(" LEFT JOIN DCP_employee em7 ON em7.eid = b.eid AND em7.employeeno = b.ADDBY ")
                .append(" LEFT JOIN DCP_PTEMPLATE dp on b.eid=dp.eid AND b.PTEMPLATENO=dp.PTEMPLATENO ")
                .append(" LEFT JOIN MES_ROUTE mr on mr.eid=b.eid and mr.ROUTENO=b.ROUTENO ")
                .append(" LEFT JOIN DCP_GOODS g on g.eid=b.eid and g.PLUNO=b.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl on gl.eid=b.eid and gl.PLUNO=b.PLUNO AND gl.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.DEPARTID AND dd0.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN dcp_department_lang dd1 ON dd0.eid = a.eid AND dd1.departno = a.CREATEDEPTID AND dd1.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 on ul1.eid=b.eid and ul1.UNIT=b.PUNIT and ul1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul2 on ul2.eid=b.eid and ul2.UNIT=b.BASEUNIT and ul2.LANG_TYPE='").append(req.getLangType()).append("'")
//                .append(" LEFT JOIN DCP_BIZPARTNER bp1 on bp1.eid=a.eid and bp1.BIZPARTNERNO=a.OBJECTID and a.OBJECTTYPE='1' ")
                .append(" LEFT JOIN DCP_CUSTOMER_LANG cl1 on cl1.eid=b.eid and cl1.CUSTOMERNO=b.OBJECTID and b.OBJECTTYPE='2' ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=b.eid and ol1.ORGANIZATIONNO=b.OBJECTID and b.OBJECTTYPE='3' ")
                .append(" LEFT JOIN dcp_category_lang cl ON cl.eid = b.eid AND cl.category = b.category AND cl.lang_type = '").append(req.getLangType()).append("'")
                .append(" left join dcp_goods_feature_lang fl on fl.eid=b.eid and fl.pluno=b.pluno and fl.featureno=b.featureno and fl.lang_type='").append(req.getLangType()).append("'")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }
        sb.append("  ) a "
                + " ORDER BY BILLNO,ITEM ASC ");
        return sb.toString();
    }
}
