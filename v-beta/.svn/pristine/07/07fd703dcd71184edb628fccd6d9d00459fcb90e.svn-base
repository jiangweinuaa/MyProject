package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CurInvCostStatQueryReq;
import com.dsc.spos.json.cust.res.DCP_CurInvCostStatQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CurInvCostStatQuery extends SPosBasicService<DCP_CurInvCostStatQueryReq, DCP_CurInvCostStatQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CurInvCostStatQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CurInvCostStatQueryReq> getRequestType() {
        return new TypeToken<DCP_CurInvCostStatQueryReq>() {
        };
    }

    @Override
    protected DCP_CurInvCostStatQueryRes getResponseType() {
        return new DCP_CurInvCostStatQueryRes();
    }

    @Override
    protected DCP_CurInvCostStatQueryRes processJson(DCP_CurInvCostStatQueryReq req) throws Exception {
        DCP_CurInvCostStatQueryRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setChkList(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            for (Map<String, Object> data : getData) {
                DCP_CurInvCostStatQueryRes.ChkList chkList = res.new ChkList();
                res.getChkList().add(chkList);

                chkList.setCorp(data.get("CORP").toString());
                chkList.setCorpName(data.get("CORPNAME").toString());
                chkList.setYear(data.get("YEAR").toString());
                chkList.setPeriod(data.get("PERIOD").toString());
                chkList.setAccountID(data.get("ACCOUNTID").toString());
                chkList.setAccount(data.get("ACCOUNT").toString());
                chkList.setPluNo(data.get("PLUNO").toString());
                chkList.setPluName(data.get("PLUNAME").toString());
                chkList.setFeatureNo(data.get("FEATURENO").toString());
                chkList.setBaseUnit(data.get("BASEUNIT").toString());
                chkList.setBaseUnitName(data.get("BASEUNITNAME").toString());
                chkList.setCostDomainID(data.get("COSTDOMAINID").toString());
                chkList.setCostDomainName(data.get("COSTDOMAINNAME").toString());
                chkList.setCurAvgPrice(data.get("CURAVGPRICE").toString());
                chkList.setLastBalQty(data.get("LASTBALQTY").toString());
                chkList.setLastBalAmt(data.get("LASTBALAMT").toString());
                chkList.setCurInQty(data.get("CURINQTY").toString());
                chkList.setCurInAmt(data.get("CURINAMT").toString());
                chkList.setCurOutQty(data.get("CUROUTQTY").toString());
                chkList.setCurOutAmt(data.get("CUROUTAMT").toString());
                chkList.setEndingBalQty(data.get("ENDINGBALQTY").toString());
                chkList.setEndingBalAmt(data.get("ENDINGBALAMT").toString());


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
    protected String getQuerySql(DCP_CurInvCostStatQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.YEAR,a.PERIOD ) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                //此处一致  begin
                .append(" a.EID,a.CORP,a.ACCOUNTID,a.COSTDOMAINID,a.YEAR,a.PERIOD " +
                        " ,a.PLUNO,a.FEATURENO " +
                        " ,ol1.ORG_NAME CORPNAME,cs.ACCOUNT,gl1.PLU_NAME PLUNAME" +
                        " ,ol2.ORG_NAME COSTDOMAINNAME,g.BASEUNIT,ul1.UNAME BASEUNITNAME "
                )
                //此处一致 end
                .append(" ,SUM(a.CURAVGPRICE) CURAVGPRICE ")
                .append(" ,SUM(a.LASTBALQTY) LASTBALQTY ")
                .append(" ,SUM(a.LASTBALAMT) LASTBALAMT ")
                .append(" ,SUM(a.CURPURINQTY + a.CUROUTSOURCINQTY + a.CURWOINQTY + a.CURREWOINQTY + a.CURMISCINQTY + a.CURADJINQTY + a.CURCANCELINQTY + a.CURTRANSINQTY) CURINQTY ")
                .append(" ,SUM(a.CURPURINAMT + a.CUROUTSOURCINAMT + a.CURWOINAMT + a.CURREWOINAMT + a.CURMISCINAMT + a.CURADJINAMT + a.CURCANCELINAMT + a.CURTRANSINAMT) CURINAMT ")
                .append(" ,SUM(a.CURREWOOUTQTY + a.CURWOOUTQTY + a.CURMISCOUTQTY) CUROUTQTY ")
                .append(" ,SUM(a.CURREWOOUTAMT + a.CURWOOUTAMT + a.CURMISCOUTAMT) CUROUTAMT ")
                .append(" ,SUM(a.ENDINGBALQTY) ENDINGBALQTY ")
                .append(" ,SUM(a.ENDINGBALAMT) ENDINGBALAMT ")
                .append(" FROM DCP_CURINVCOSTSTAT a ")
                .append(" LEFT JOIN DCP_ORG c on a.eid=c.eid and a.CORP=c.ORGANIZATIONNO ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=a.eid and ol1.ORGANIZATIONNO=a.CORP AND ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.eid=a.eid and ol2.ORGANIZATIONNO=a.COSTDOMAINID AND ol2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING cs on cs.eid=a.eid and cs.ACCOUNTID=a.ACCOUNTID ")
                .append(" LEFT JOIN DCP_GOODS g on g.EID=a.EID and g.PLUNO=a.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=a.eid AND gl1.PLUNO=a.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 on ul1.eid=a.eid and ul1.UNIT=g.BASEUNIT and ul1.LANG_TYPE='").append(req.getLangType()).append("'")
//                .append(" LEFT JOIN DCP_COSTDOMAIN cd on cd.eid=a.eid and cd.COSTDOMAINID=a.COSTDOMAINID ")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            sb.append(" and a.YEAR='").append(req.getRequest().getYear()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            sb.append(" and a.PERIOD='").append(req.getRequest().getPeriod()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            sb.append(" and a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getAccount())) {
            sb.append(" and cs.ACCOUNT like '%%").append(req.getRequest().getAccount()).append("%%'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" and a.CORP='").append(req.getRequest().getCorp()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCorp_Name())) {
            sb.append(" and ol1.ORG_NAME like '%%").append(req.getRequest().getCorp_Name()).append("%%'");
        }


        if (StringUtils.isNotEmpty(req.getRequest().getPluNo())) {
            sb.append(" and a.PLUNO='").append(req.getRequest().getPluNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCostDomainID())) {
            sb.append(" and c.COST_DOMAIN='").append(req.getRequest().getCostDomainID()).append("'");
        }

        sb.append(" GROUP BY  ")
                //此处一致 begin 去掉别名
                .append(" a.EID,a.CORP,a.ACCOUNTID,a.COSTDOMAINID,a.YEAR,a.PERIOD " +
                        " ,a.PLUNO,a.FEATURENO " +
                        " ,ol1.ORG_NAME ,ol2.ORG_NAME ,cs.ACCOUNT,gl1.PLU_NAME " +
                        " ,g.BASEUNIT,ul1.UNAME  ");
        //此处一致 end

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY rn ");

        return sb.toString();
    }
}
