package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_InterSettleDataQueryReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_InterSettleDataQuery extends SPosBasicService<DCP_InterSettleDataQueryReq, DCP_InterSettleDataQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_InterSettleDataQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettleDataQueryReq> getRequestType() {
        return new TypeToken<DCP_InterSettleDataQueryReq>() {
        };
    }

    @Override
    protected DCP_InterSettleDataQueryRes getResponseType() {
        return new DCP_InterSettleDataQueryRes();
    }

    @Override
    protected DCP_InterSettleDataQueryRes processJson(DCP_InterSettleDataQueryReq req) throws Exception {
        DCP_InterSettleDataQueryRes res = new DCP_InterSettleDataQueryRes();
        res.setDatas(res.new Datas());

        res.getDatas().setProcess(new ArrayList<>());
        List<Map<String, Object>> detailData = doQueryData(getQueryInterSettleDetailSql(req), null);
        List<Map<String, Object>> routeData = doQueryData(getQueryInterSettleRouteSql(req), null);

        if (CollectionUtils.isNotEmpty(detailData)) {
            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("BILLNO", true);
            List<Map<String, Object>> distinctData = MapDistinct.getMap(detailData, distinct);

            for (Map<String, Object> map : distinctData) {

                DCP_InterSettleDataQueryRes.Process oneProcess = res.new Process();
                res.getDatas().getProcess().add(oneProcess);

                Map<String, Object> condition = new HashMap<>();
                condition.put("EID", map.get("EID").toString());
                condition.put("BILLNO", map.get("BILLNO").toString());

                List<Map<String, Object>> processDetail = MapDistinct.getWhereMap(detailData, condition, true);

                oneProcess.setPluDetail(new ArrayList<>());

                List<String> totCQty = new ArrayList<>();
                double totPqty = 0;
                double totReceiveAmt = 0;
                double totSupplyAmt = 0;
                for (Map<String, Object> map2 : processDetail) {

                    DCP_InterSettleDataQueryRes.PluDetail oneDetail = res.new PluDetail();
                    oneProcess.getPluDetail().add(oneDetail);

                    oneDetail.setPluNo(map2.get("PLUNO").toString());
                    oneDetail.setPluName(map2.get("PLU_NAME").toString());
                    oneDetail.setFeatureNo(map2.get("FEATURENO").toString());
                    oneDetail.setFeatureName(map2.get("FEATURENAME").toString());
                    oneDetail.setSpec(map2.get("SPEC").toString());
                    oneDetail.setPUnit(map2.get("PUNIT").toString());
                    oneDetail.setPUnitName(map2.get("PUNITNAME").toString());


                    double pQty = Double.parseDouble(map2.get("PQTY").toString());
                    oneDetail.setPQty(pQty);
                    double receivePrice = Double.parseDouble(map2.get("RECEIVEPRICE").toString());
                    oneDetail.setReceivePrice(receivePrice);
                    double receiveAmt = Double.parseDouble(map2.get("RECEIVEAMT").toString());
                    oneDetail.setReceiveAmt(receiveAmt);
                    double supplyPrice = Double.parseDouble(map2.get("SUPPLYPRICE").toString());
                    oneDetail.setSupplyPrice(supplyPrice);
                    double supplyAmt = Double.parseDouble(map2.get("SUPPLYAMT").toString());

                    if (!totCQty.contains(map2.get("PLUNO").toString())){
                        totCQty.add(map2.get("PLUNO").toString());
                    }
                    totPqty+= pQty;
                    totReceiveAmt+= receiveAmt;
                    totSupplyAmt+= supplyAmt;
                    oneDetail.setSupplyAmt(supplyAmt);
                    oneDetail.setTaxCode(map2.get("TAXCODE").toString());
                    oneDetail.setTaxName(map2.get("TAXNAME").toString());
                    oneDetail.setTaxRate(Double.parseDouble(map2.get("TAXRATE").toString()));
                    oneDetail.setReceiveOrgNo(map2.get("RECEIVEORGNO").toString());
                    oneDetail.setReceiveOrgName(map2.get("RECEIVEORGNAME").toString());
                    oneDetail.setReceiveCorp(map2.get("RECEIVECORP").toString());
                    oneDetail.setReceiveCorpName(map2.get("RECEIVECORPNAME").toString());
                    oneDetail.setSupplyOrgNo(map2.get("SUPPLYORGNO").toString());
                    oneDetail.setSupplyOrgName(map2.get("SUPPLYORGNAME").toString());
                    oneDetail.setSupplyCorp(map2.get("SUPPLYCORP").toString());
                    oneDetail.setSupplyCorpName(map2.get("SUPPLYCORPNAME").toString());
                    oneDetail.setProcessNo(map2.get("PROCESSNO").toString());
                    oneDetail.setVersionNum(map2.get("VERSIONNUM").toString());
                    oneDetail.setDirection(map2.get("DIRECTION").toString());
                    oneDetail.setBType(map2.get("BTYPE").toString());
                    oneDetail.setSettleCorp(map2.get("SETTLECORP").toString());
                    oneDetail.setSettleCorpName(map2.get("SETTLECORPNAME").toString());
                    oneDetail.setTaxPayerType(map2.get("TAXPAYER_TYPE").toString());
                    oneDetail.setPreTaxAmt(Double.valueOf(map2.get("PRETAXAMT").toString()));
                    oneDetail.setTaxAmt(Double.valueOf(map2.get("TAXAMT").toString()));
                    oneDetail.setInputTaxCode(map2.get("INPUT_TAXCODE").toString());
                    oneDetail.setInputTaxRate(map2.get("INPUT_TAXRATE").toString());
                    oneDetail.setOutputTaxCode(map2.get("OUTPUT_TAXCODE").toString());
                    oneDetail.setOutputTaxRate(map2.get("OUTPUT_TAXRATE").toString());

                }
                oneProcess.setTotCqty(totCQty.size());
                oneProcess.setTotPqty(totPqty);
                oneProcess.setTotReceiveAmt(totReceiveAmt);
                oneProcess.setTotSupplyAmt(totSupplyAmt);

                List<Map<String, Object>> processRoute = MapDistinct.getWhereMap(routeData, condition, true);
                oneProcess.setRoute(new ArrayList<>());
                for (Map<String, Object> map2 : processRoute) {

                    if (Integer.parseInt(map2.get("SORTID").toString()) <= 1) {
                        DCP_InterSettleDataQueryRes.Route zeroRoute = res.new Route();
                        oneProcess.getRoute().add(zeroRoute);

                        zeroRoute.setBType(map2.get("BTYPE").toString());
                        zeroRoute.setDirection(map2.get("DIRECTION").toString());
                        zeroRoute.setProcessNo(map2.get("PROCESSNO").toString());
                        zeroRoute.setVersionNum(map2.get("VERSIONNUM").toString());
                        zeroRoute.setPSortId(map2.get("PSORTID").toString());
                        zeroRoute.setStation(map2.get("STARTPOINT").toString());
                        zeroRoute.setStationName(map2.get("STARTPOINTNAME").toString());
                        zeroRoute.setRowNum("0");
                    }

                    DCP_InterSettleDataQueryRes.Route oneRoute = res.new Route();
                    oneProcess.getRoute().add(oneRoute);

                    oneRoute.setBType(map2.get("BTYPE").toString());
                    oneRoute.setDirection(map2.get("DIRECTION").toString());
                    oneRoute.setProcessNo(map2.get("PROCESSNO").toString());
                    oneRoute.setVersionNum(map2.get("VERSIONNUM").toString());
                    oneRoute.setPSortId(map2.get("PSORTID").toString());
                    oneRoute.setStation(map2.get("ENDPOINT").toString());
                    oneRoute.setStationName(map2.get("ENDPOINTNAME").toString());
                    oneRoute.setRowNum(map2.get("SORTID").toString());

                }
            }

        }
        res.setSuccess(true);
        res.setPageSize(req.getPageSize());
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    private String getQueryInterSettleDetailSql(DCP_InterSettleDataQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT a.* ")
                .append(" ,g.SPEC,gl1.PLU_NAME,fl1.FEATURENAME,ul1.UNAME PUNITNAME ")
                .append(" ,tl1.TAXNAME,ol1.ORG_NAME RECEIVEORGNAME,ol2.ORG_NAME RECEIVECORPNAME ")
                .append(" ,ol3.ORG_NAME SUPPLYORGNAME,ol4.ORG_NAME SUPPLYCORPNAME,ol5.ORG_NAME SETTLECORPNAME ")
                .append(" FROM DCP_INTERSETTLE_DETAIL a")
                .append(" LEFT JOIN DCP_GOODS g on a.EID=g.EID and a.PLUNO=g.PLUNO ")
                .append(" LEFT JOIN DCP_TAXCATEGORY_LANG tl1 on tl1.eid = a.eid and tl1.TAXCODE=a.TAXCODE AND tl1.TAXAREA='CN' and tl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 on ul1.eid = a.eid and ul1.UNIT=a.PUNIT and ul1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid = a.eid and gl1.PLUNO=a.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_GOODS_FEATURE_LANG fl1 on fl1.eid = a.eid and fl1.PLUNO=a.PLUNO and fl1.FEATURENO=a.FEATURENO and fl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid = a.eid and ol1.ORGANIZATIONNO=a.RECEIVEORGNO and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.eid = a.eid and ol2.ORGANIZATIONNO=a.RECEIVECORP and ol2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol3 on ol3.eid = a.eid and ol3.ORGANIZATIONNO=a.SUPPLYORGNO and ol3.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol4 on ol4.eid = a.eid and ol4.ORGANIZATIONNO=a.SUPPLYCORP and ol4.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol5 on ol5.eid = a.eid and ol5.ORGANIZATIONNO=a.SETTLECORP and ol5.LANG_TYPE='").append(req.getLangType()).append("'")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }

        return sb.toString();
    }

    private String getQueryInterSettleRouteSql(DCP_InterSettleDataQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT a.* ")
                .append(" ,ol1.ORG_NAME STARTPOINTNAME,ol2.ORG_NAME ENDPOINTNAME ")
                .append(" FROM  DCP_INTERSETTLE_ROUTE a ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid = a.eid and ol1.ORGANIZATIONNO=a.STARTPOINT and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.eid = a.eid and ol2.ORGANIZATIONNO=a.ENDPOINT and ol2.LANG_TYPE='").append(req.getLangType()).append("'")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }
        sb.append(" ORDER BY SORTID ");
        return sb.toString();
    }

    @Override
    protected String getQuerySql(DCP_InterSettleDataQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();


        return sb.toString();
    }
}
