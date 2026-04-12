package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_WoProdRepStatQueryReq;
import com.dsc.spos.json.cust.res.DCP_WoProdRepStatQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_WoProdRepStatQuery extends SPosBasicService<DCP_WoProdRepStatQueryReq, DCP_WoProdRepStatQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_WoProdRepStatQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_WoProdRepStatQueryReq> getRequestType() {
        return new TypeToken<DCP_WoProdRepStatQueryReq>() {
        };
    }

    @Override
    protected DCP_WoProdRepStatQueryRes getResponseType() {
        return new DCP_WoProdRepStatQueryRes();
    }

    @Override
    protected DCP_WoProdRepStatQueryRes processJson(DCP_WoProdRepStatQueryReq req) throws Exception {
        DCP_WoProdRepStatQueryRes res = this.getResponseType();
        int totalRecords = 0; //总笔数
        int totalPages = 0;
        List<Map<String, Object>> queryData = this.doQueryData(getQuerySql(req), null);
        res.setDatas(new ArrayList<>());

        if (CollectionUtils.isNotEmpty(queryData)) {
            String num = queryData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            // 算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;


            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("CORP", true);
            distinct.put("PORDERNO", true);

            List<Map<String, Object>> masterData = MapDistinct.getMap(queryData, distinct);

            for (Map<String, Object> master : masterData) {
                DCP_WoProdRepStatQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);
                oneData.setWorkList(new ArrayList<>());

                oneData.setStatus(master.get("STATUS").toString());
                oneData.setBDate(master.get("WDATE").toString());
                oneData.setPorderNo(master.get("PORDERNO").toString());
                oneData.setCorp(master.get("CORP").toString());

                Map<String, Object> condition = new HashMap<>();
                condition.put("CORP", master.get("CORP").toString());
                condition.put("PORDERNO", master.get("PORDERNO").toString());
                List<Map<String, Object>> detailData = MapDistinct.getWhereMap(queryData, condition, true);
                for (Map<String, Object> detail : detailData) {
                    DCP_WoProdRepStatQueryRes.WorkList oneWork = res.new WorkList();
                    oneData.getWorkList().add(oneWork);

                    oneWork.setItem(detail.get("ITEM").toString());
                    oneWork.setCostCenter(detail.get("COSTCENTER").toString());
                    oneWork.setCostCenterNo(detail.get("COSTCENTERNO").toString());
                    oneWork.setTaskId(detail.get("TASKID").toString());
                    oneWork.setPstockInNo(detail.get("PSTOCKINNO").toString());
                    oneWork.setPstockInItem(detail.get("PSTOCKINITEM").toString());
                    oneWork.setPluNo(detail.get("PLUNO").toString());
                    oneWork.setPluName(detail.get("PLU_NAME").toString());
                    oneWork.setSpec(detail.get("PSPEC").toString());
                    oneWork.setInvQty(detail.get("INVQTY").toString());
                    oneWork.setInvAmount(detail.get("INVAMOUNT").toString());
                    oneWork.setEndWipQty(detail.get("ENDWIPQTY").toString());
                    oneWork.setEndWipEqQty(detail.get("ENDWIPEQQTY").toString());
                    oneWork.setEndWipEqRate(detail.get("ENDWIPEQRATE").toString());
                    oneWork.setReportedQty(detail.get("REPORTEDQUY").toString());
                    oneWork.setActHours(detail.get("ACTHOURS").toString());
                    oneWork.setActMachineHrs(detail.get("ACTMACHINEHRS").toString());
                    oneWork.setStdHours(detail.get("STDHOURS").toString());
                    oneWork.setStdMachineHrs(detail.get("STDMACHINEHRS").toString());
                    oneWork.setRemarks(detail.get("REMARKS").toString());

                }

            }

        }


        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    private String getQueryRecordSql(DCP_WoProdRepStatQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM ( ");
        sb.append(" SELECT row_number() OVER (ORDER BY a.PORDERNO DESC) AS RN,COUNT(*) OVER ( ) NUM " +
                " ,a.* "+
                " FROM( ");
        sb.append("SELECT " +
                        " DISTINCT a.EID,a.CORP,a.PORDERNO "+
                        " FROM DCP_WOPRODREPSTAT a "
                )
                .append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" and a.CORP = '").append(req.getRequest().getCorp()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
            sb.append(" and to_char(a.WDATE,'YYYYMMDD') >= '").append(req.getRequest().getBeginDate()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            sb.append(" and to_char(a.WDATE,'YYYYMMDD') <= '").append(req.getRequest().getEndDate()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
            sb.append(" AND ( a.PORDERNO like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(" or a.TASKID like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(")");
        }

        sb.append("  )a )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY rn ");

        return sb.toString();
    }

    @Override
    protected String getQuerySql(DCP_WoProdRepStatQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
//
//        int pageNumber = req.getPageNumber();
//        int pageSize = req.getPageSize();
//
//        //計算起啟位置
//        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ");
        sb.append("SELECT tp.rn,tp.num," +
                        " A.*,b.SPEC PSPEC,c.PLU_NAME  " +
                        " FROM DCP_WOPRODREPSTAT a "
                )
                .append(" LEFT JOIN DCP_GOODS b ON a.eid=b.eid and a.PLUNO=b.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG c on a.eid=c.eid and a.PLUNO=c.PLUNO and c.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" INNER JOIN ( ").append(getQueryRecordSql(req)).append(") TP ON TP.EID=a.EID and tp.CORP=a.CORP and tp.PORDERNO=a.PORDERNO ");
//                .append(" WHERE a.EID='").append(req.geteId()).append("'");
//        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
//            sb.append(" and a.CORP = '").append(req.getRequest().getCorp()).append("'");
//        }
//        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
//            sb.append(" and to_char(a.WDATE,'YYYYMMDD') >= '").append(req.getRequest().getBeginDate()).append("'");
//        }
//        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
//            sb.append(" and to_char(a.WDATE,'YYYYMMDD') <= '").append(req.getRequest().getEndDate()).append("'");
//        }
//        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
//            sb.append(" AND ( a.PORDERNO like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
//                    .append(" or a.TASKID like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
//                    .append(")");
//        }


        sb.append("  )  a ");
//                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize)
                sb.append(" ORDER BY rn,a.ITEM ");

        return sb.toString();
    }
}
