package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SortingTaskDetailReq;
import com.dsc.spos.json.cust.res.DCP_SortingTaskDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_SortingTaskDetail extends SPosBasicService<DCP_SortingTaskDetailReq, DCP_SortingTaskDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_SortingTaskDetailReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SortingTaskDetailReq> getRequestType() {
        return new TypeToken<DCP_SortingTaskDetailReq>() {
        };
    }

    @Override
    protected DCP_SortingTaskDetailRes getResponseType() {
        return new DCP_SortingTaskDetailRes();
    }

    @Override
    protected DCP_SortingTaskDetailRes processJson(DCP_SortingTaskDetailReq req) throws Exception {
        DCP_SortingTaskDetailRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setDatas(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            Map<String, Boolean> masterCondition = new HashMap<>();
            masterCondition.put("EID", true);
            masterCondition.put("TASKNO", true);
            masterCondition.put("REQUIRETYPE", true);
            masterCondition.put("REQUIRENO", true);
            List<Map<String, Object>> master = MapDistinct.getMap(getData, masterCondition);

            for (Map<String, Object> data : master) {
                Map<String, Object> detailCondition = new HashMap<>();
                detailCondition.put("EID", data.get("EID"));
                detailCondition.put("TASKNO", data.get("TASKNO"));
                detailCondition.put("REQUIRETYPE", data.get("REQUIRETYPE"));
                detailCondition.put("REQUIRENO", data.get("REQUIRENO"));

                DCP_SortingTaskDetailRes.Data oneData = res.new Data();
                oneData.setDetail(new ArrayList<>());
                res.getDatas().add(oneData);

                oneData.setTaskNo(data.get("TASKNO").toString());
                oneData.setWarehouse(data.get("WAREHOUSENO").toString());
                oneData.setWarehouseName(data.get("WAREHOUSE_NAME").toString());
                oneData.setRequireType(data.get("REQUIRETYPE").toString());
                oneData.setRequireNo(data.get("REQUIRENO").toString());
                oneData.setRequireName(data.get("REQUIRENAME").toString());


                oneData.setTotRQty(data.get("TOTRQTY").toString());
                oneData.setTotQty(data.get("MTOTQTY").toString());
                oneData.setTotAQty(data.get("MTOTAQTY").toString());
                oneData.setTotDQty(data.get("TOTDQTY").toString());

                List<Map<String, Object>> detail = MapDistinct.getWhereMap(getData, detailCondition, true);
                List<String> totCqty = new ArrayList<>();
                for (Map<String, Object> d : detail) {
                    DCP_SortingTaskDetailRes.Detail oneDetail = res.new Detail();
                    oneData.getDetail().add(oneDetail);

                    String c = d.get("PLUNO").toString() + d.get("FEATURENO").toString();
                    if (!totCqty.contains(c)){
                        totCqty.add(c);
                    }
                    oneDetail.setItem(d.get("ITEM").toString());
                    oneDetail.setOfNo(d.get("OFNO").toString());
                    oneDetail.setOItem(d.get("OITEM").toString());
                    oneDetail.setPluNo(d.get("PLUNO").toString());
                    oneDetail.setPluName(d.get("PLU_NAME").toString());
                    oneDetail.setSpec(d.get("SPEC").toString());
                    oneDetail.setFeatureNo(d.get("FEATURENO").toString());
                    oneDetail.setFeatureName(d.get("FEATURENAME").toString());
                    oneDetail.setCategory(d.get("CATEGORY").toString());
                    oneDetail.setCategoryName(d.get("CATEGORY_NAME").toString());
                    oneDetail.setUnit(d.get("UNIT").toString());
                    oneDetail.setUnitName(d.get("UNAME").toString());
                    oneDetail.setRQty(d.get("TOTAQTY").toString());
                    oneDetail.setQty(d.get("QTY").toString());
                    oneDetail.setAQty(d.get("AQTY").toString());
                    oneDetail.setDiffQty(d.get("DQTY").toString());

                    oneDetail.setOrderNo(d.get("ORDERNO").toString());

                }
                oneData.setTotCqty(StringUtils.toString(totCqty.size(),"0"));
            }
        }

        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_SortingTaskDetailReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.TASKNO DESC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" b.*,a.WAREHOUSENO,wl1.WAREHOUSE_NAME,gl1.PLU_NAME,g.SPEC,fl1.FEATURENAME ")
                .append(" ,cl1.CATEGORY,cl1.CATEGORY_NAME,ul1.UNAME,CASE WHEN b.REQUIRETYPE='0' THEN ol1.ORG_NAME ELSE co1.CUSTOMER_NAME END  REQUIRENAME " +
                        " ,a.TOTCQTY,a.TOTQTY,c.TOTQTY MTOTQTY, c.TOTAQTY MTOTAQTY,c.TOTRQTY,c.TOTQTY-c.TOTAQTY TOTDQTY,b.QTY-b.AQTY DQTY" +
                        " ,f.SOURCENO,e.ORDERNO  ")
                .append(" FROM MES_SORTINGTASK a  ")
                .append(" LEFT JOIN MES_SORTINGTASK_DETAIL b on a.eid=b.eid and a.TASKNO = b.TASKNO   ")
                .append(" LEFT JOIN ( SELECT ORGANIZATIONNO,EID,TASKNO,REQUIRENO,REQUIRETYPE,SUM(QTY) TOTQTY,SUM(AQTY) TOTAQTY,SUM(TOTAQTY) TOTRQTY FROM MES_SORTINGTASK_DETAIL GROUP BY EID,ORGANIZATIONNO,TASKNO,REQUIRENO,REQUIRETYPE ) c " +
                        "  ON b.eid=c.eid and b.TASKNO = c.TASKNO and b.REQUIRENO=c.REQUIRENO and b.REQUIRETYPE=c.REQUIRETYPE ")
                .append(" LEFT JOIN DCP_SORTINGASSIGN_DETAIL e on e.eid=b.eid and e.billno=b.ofno and e.item=b.oitem ")
                .append(" LEFT JOIN MES_SORTDATADETAIL f on f.eid=b.eid and f.docno=b.docno and f.item=b.docitem " )
                .append(" LEFT JOIN DCP_GOODS g on b.eid=g.eid and b.PLUNO=g.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=b.eid and gl1.PLUNO=b.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_GOODS_FEATURE_LANG fl1 on fl1.eid=b.eid and fl1.PLUNO=b.PLUNO AND fl1.FEATURENO=b.FEATURENO and fl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=b.eid and ol1.ORGANIZATIONNO=b.REQUIRENO AND b.REQUIRETYPE='0' and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CUSTOMER co1 on co1.eid=b.eid and co1.CUSTOMERNO=b.REQUIRENO AND b.REQUIRETYPE='1' ")
                .append(" LEFT JOIN DCP_WAREHOUSE_LANG wl1 on wl1.eid=a.eid and wl1.WAREHOUSE=a.WAREHOUSENO and wl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CATEGORY_LANG cl1 on cl1.eid=g.eid and cl1.CATEGORY=g.CATEGORY and cl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 on ul1.eid=b.eid and ul1.UNIT=b.UNIT and ul1.LANG_TYPE='").append(req.getLangType()).append("'")

        ;
        sb.append(" WHERE a.eid='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getTaskNo())) {
            sb.append("  and a.TASKNO='").append(req.getRequest().getTaskNo()).append("'");
        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY TASKNO ");

        return sb.toString();
    }

}
