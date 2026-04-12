package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_TopupSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_TopupSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_TopupSetQuery extends SPosBasicService<DCP_TopupSetQueryReq, DCP_TopupSetQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_TopupSetQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_TopupSetQueryReq> getRequestType() {
        return new TypeToken<DCP_TopupSetQueryReq>() {
        };
    }

    @Override
    protected DCP_TopupSetQueryRes getResponseType() {
        return new DCP_TopupSetQueryRes();
    }

    @Override
    protected DCP_TopupSetQueryRes processJson(DCP_TopupSetQueryReq req) throws Exception {
        DCP_TopupSetQueryRes res = this.getResponseType();


        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setRequest(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("CORP", true);
            distinct.put("TOPUPORG", true);
            List<Map<String, Object>> distinctData = MapDistinct.getMap(getData, distinct);

            for (Map<String, Object> data : distinctData) {
                DCP_TopupSetQueryRes.Request oneData = res.new Request();
                res.getRequest().add(oneData);
                oneData.setCorp(data.get("CORP").toString());
                oneData.setCorpName(data.get("CORPNAME").toString());
                oneData.setStatus(data.get("STATUS").toString());
                oneData.setTopupOrg(data.get("TOPUPORG").toString());
                oneData.setTopupOrgName(data.get("TOPUPORGNAME").toString());

                if ("1".equals(req.getRequest().getTopupType())) {
                    Map<String, Object> condition = new HashMap<>();
                    condition.put("EID", data.get("EID").toString());
                    condition.put("CORP", data.get("CORP").toString());
                    condition.put("TOPUPORG", data.get("TOPUPORG").toString());

                    List<Map<String, Object>> detail = MapDistinct.getWhereMap(getData, condition, true);
                    oneData.setSetList(new ArrayList<>());
                    for (Map<String, Object> data2 : detail) {
                        DCP_TopupSetQueryRes.SetList oneSet = res.new SetList();
                        oneData.getSetList().add(oneSet);

                        oneSet.setTopupProdID(data2.get("TOPUPPRODID").toString());
                        oneSet.setTopupProdName(data2.get("TOPUPPRODNAME").toString());
                        oneSet.setTopupPayType(data2.get("TOPUPPAYTYPE").toString());
                        oneSet.setTopupPayName(data2.get("TOPUPPAYNAME").toString());
                        oneSet.setConsPayType(data2.get("CONSPAYTYPE").toString());
                        oneSet.setConsPayName(data2.get("CONSPAYNAME").toString());
                        oneSet.setConsProdID(data2.get("CONSRPODID").toString());
                        oneSet.setConsProdName(data2.get("CONSRPODNAME").toString());

                    }
                }
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
    protected String getQuerySql(DCP_TopupSetQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
//        1：详情；2：简查
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.CORP ) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* " +
                        " ,ol1.ORG_NAME CORPNAME,ol2.ORG_NAME TOPUPORGNAME  ");
        if ("1".equals(req.getRequest().getTopupType())) {
            sb.append(" ,gl1.PLU_NAME TOPUPPRODNAME " +
                    "   ,gl2.PLU_NAME CONSRPODNAME" +
                    "   ,pl1.FUNCNAME TOPUPPAYNAME " +
                    "   ,pl2.FUNCNAME CONSPAYNAME " +
                    " ,ee0.name as CREATEBYNAME,ee1.name as MODIFYBYNAME,ee2.name as CONFIRMBYNAME" +
                    " ,ee3.name as CANCELBYNAME  "
            );
            sb.append(" FROM DCP_TOPUPSET a ");
            sb.append(" LEFT JOIN DCP_GOODS_LANG gl1 ON gl1.eid=a.EID and gl1.PLUNO=a.TOPUPPRODID AND gl1.LANG_TYPE='").append(req.getLangType()).append("'");
            sb.append(" LEFT JOIN DCP_GOODS_LANG gl2 ON gl2.eid=a.EID and gl2.PLUNO=a.CONSRPODID AND gl2.LANG_TYPE='").append(req.getLangType()).append("'");
            sb.append(" LEFT JOIN DCP_PAYFUNC_LANG pl1 ON pl1.eid=a.EID and pl1.FUNCNO=a.TOPUPPAYTYPE AND pl1.LANG_TYPE='").append(req.getLangType()).append("'");
            sb.append(" LEFT JOIN DCP_PAYFUNC_LANG pl2 ON pl2.eid=a.EID and pl2.FUNCNO=a.CONSPAYTYPE AND pl2.LANG_TYPE='").append(req.getLangType()).append("'");
            sb.append(" LEFT JOIN DCP_EMPLOYEE ee0 ON ee0.EID=a.EID and ee0.EMPLOYEENO=a.CREATEBY   "
                    + " LEFT JOIN DCP_EMPLOYEE ee1 ON ee1.EID=a.EID and ee1.EMPLOYEENO=a.MODIFYBY "
                    + " LEFT JOIN DCP_EMPLOYEE ee2 ON ee2.EID=a.EID and ee2.EMPLOYEENO=a.CONFIRMBY "
                    + " LEFT JOIN DCP_EMPLOYEE ee3 ON ee3.EID=a.EID and ee3.EMPLOYEENO=a.CANCELBY ");

        } else {
            sb.append(" FROM (SELECT DISTINCT a.EID,a.CORP,a.TOPUPORG,a.STATUS " +
                    " FROM DCP_TOPUPSET a) a ")
            ;
        }
        sb.append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.EID=a.EID and ol1.ORGANIZATIONNO=a.CORP AND ol1.LANG_TYPE='").append(req.getLangType()).append("'");
        sb.append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.EID=a.EID and ol2.ORGANIZATIONNO=a.TOPUPORG AND ol2.LANG_TYPE='").append(req.getLangType()).append("'");

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" AND a.CORP='").append(req.getRequest().getCorp()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getTopupOrg())) {
            sb.append(" AND a.TOPUPORG='").append(req.getRequest().getTopupOrg()).append("'");
        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY CORP ");

        return sb.toString();
    }
}
