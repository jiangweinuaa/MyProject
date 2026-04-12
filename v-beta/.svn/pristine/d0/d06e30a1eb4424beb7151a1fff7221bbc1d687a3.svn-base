package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ShopRecProcessReq;
import com.dsc.spos.json.cust.res.DCP_ShopRecProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.dataaux.ShopRecData;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ShopRecProcess extends SPosAdvanceService<DCP_ShopRecProcessReq, DCP_ShopRecProcessRes> {
    @Override
    protected void processDUID(DCP_ShopRecProcessReq req, DCP_ShopRecProcessRes res) throws Exception {

        ShopRecData shopRecData = new ShopRecData();

        List<Map<String, Object>> dcpSaleData = doQueryData(getQuerySaleSql(req), null);

        if (CollectionUtils.isNotEmpty(dcpSaleData)) {
            shopRecData.insertShopSettBill(this.pData, ShopRecData.TicketType.Sale, dcpSaleData);
        }



    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ShopRecProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ShopRecProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ShopRecProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ShopRecProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ShopRecProcessReq> getRequestType() {
        return new TypeToken<DCP_ShopRecProcessReq>() {
        };
    }

    @Override
    protected DCP_ShopRecProcessRes getResponseType() {
        return new DCP_ShopRecProcessRes();
    }


    private String getQuerySalePaySql(DCP_ShopRecProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT c.*,o1.CORP ")
                .append(" FROM DCP_SALE a ")
                .append(" LEFT JOIN DCP_SALE_DETAIL b ON a.eid = b.eid and a.SHOPID=b.SHOPID and a.SALENO=b.SALENO  ")
                .append(" LEFT JOIN DCP_SALE_DETAIL_PAY c ON b.eid = c.eid and b.SHOPID=c.SHOPID and b.SALENO=c.SALENO and b.ITEM=c.ITEM_DETAIL  ")
                .append(" LEFT JOIN DCP_ORG o1 on o1.eid=a.eid and o1.ORGANAZATIONNO=a.SHOPID  ")
        ;

        if (Check.isNotEmpty(req.getRequest().getEid())) {
            querySql.append(" WHERE a.EID='").append(req.getRequest().getEid()).append("' ");
        } else {
            querySql.append(" WHERE a.EID='").append(req.geteId()).append("' ");
        }

        querySql.append(" AND a.FNO is null ");

        if (Check.isNotEmpty(req.getRequest().getShopId())) {
            querySql.append(" AND a.SHOPID='").append(req.getRequest().getShopId()).append("' ");
        }

        if (Check.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" AND o1.CORP='").append(req.getRequest().getCorp()).append("' ");
        }

        if (Check.isNotEmpty(req.getRequest().getBeginDate())) {
            querySql.append(" AND a.BDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("' ");
        }

        if (Check.isNotEmpty(req.getRequest().getEndDate())) {
            querySql.append(" AND a.BDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("' ");
        }

        return querySql.toString();
    }

    private String getQueryCteSql(DCP_ShopRecProcessReq req, ShopRecData.TicketType ticketType) throws Exception {
        StringBuilder querySql = new StringBuilder();



        switch (ticketType) {
            case Sale:
                querySql.append(" WITH CTE AS(")
                        .append(" SELECT a.EID, " +
                                "         a.SHOPID, " +
                                "         a.TYPE, " +
                                "         a.CUSTOMERNO, " +
                                "         a.BDATE, " +
                                "         o1.CORP, " +
                                "         a.TOT_AMT, " +
                                "         a.PAY_AMT " +
                                "         FROM DCP_SALE a " +
                                "         LEFT JOIN DCP_ORG o1 on o1.EID = a.EID and o1.ORGANIZATIONNO = a.SHOPID ");
                if (Check.isNotEmpty(req.getRequest().getEid())) {
                    querySql.append(" WHERE a.EID='").append(req.getRequest().getEid()).append("' ");
                } else {
                    querySql.append(" WHERE a.EID='").append(req.geteId()).append("' ");
                }
                querySql.append(" AND a.FNO is null ");
                break;
            case CardSale:
                querySql.append(" WITH CTE AS(")
                        .append(" SELECT a.EID, " +
                                "         a.SHOPID, " +
                                "         a.BILLTYPE, " +
                                "         a.CHANNELID, " +
                                "         a.BILLDATE, " +
                                "         o1.CORP, " +
                                "         a.TOTALAMOUNT, " +
                                "         a.TOTALAMOUNT " +
                                "         FROM CRM_CARDSALE a " +
                                "         LEFT JOIN DCP_ORG o1 on o1.EID = a.EID and o1.ORGANIZATIONNO = a.SHOPID ");
                if (Check.isNotEmpty(req.getRequest().getEid())) {
                    querySql.append(" WHERE a.EID='").append(req.getRequest().getEid()).append("' ");
                } else {
                    querySql.append(" WHERE a.EID='").append(req.geteId()).append("' ");
                }
                break;
            case CouponSale:
                break;
            case EquityCardSale:
                break;
        }

        if (Check.isNotEmpty(req.getRequest().getShopId())) {
            querySql.append(" AND a.SHOPID='").append(req.getRequest().getShopId()).append("' ");
        }
        if (Check.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" AND o1.CORP='").append(req.getRequest().getCorp()).append("' ");
        }

        if (ticketType == ShopRecData.TicketType.CardSale){
            if (Check.isNotEmpty(req.getRequest().getBeginDate())) {
                querySql.append(" AND to_char(a.BILLDATE,'yyyymmdd')>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("' ");
            }

            if (Check.isNotEmpty(req.getRequest().getEndDate())) {
                querySql.append(" AND to_char(a.BILLDATE,'yyyymmdd')<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("' ");
            }
        }else {
            if (Check.isNotEmpty(req.getRequest().getBeginDate())) {
                querySql.append(" AND a.BDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("' ");
            }

            if (Check.isNotEmpty(req.getRequest().getEndDate())) {
                querySql.append(" AND a.BDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("' ");
            }
        }

        querySql.append(")");

        return querySql.toString();

    }

    private String getQueryCardSaleSql(DCP_ShopRecProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(getQueryCteSql(req,ShopRecData.TicketType.Sale));

        querySql.append(" SELECT tb.* " +
                        " FROM ( ")
                .append(" SELECT EID, " +
                        "             SHOPID, " +
                        "             TYPE, " +
                        "             CUSTOMERNO, " +
                        "             BDATE, " +
                        "             CORP, " +
                        "             SUM(TOT_AMT) as TOT_AMT, " +
                        "             SUM(PAY_AMT) as PAY_AMT " +
                        "      FROM  CTE ")
                .append("  group by EID, SHOPID, TYPE, CUSTOMERNO, BDATE, CORP ")
                .append(" ) ta " +
                        "   LEFT JOIN (" +
                        "             SELECT a.TYPE, " +
                        "                    a.CUSTOMERNO, " +
                        "                    o1.CORP, " +
                        "                    b.* " +
                        "             FROM CRM_CARDSALE a " +
                        "                             LEFT JOIN DCP_ORG o1 on o1.EID = a.EID and o1.ORGANIZATIONNO = a.SHOPID " +
                        "                             LEFT JOIN CRM_CARDSALEITEM b " +
                        "                                       on a.EID = b.EID and a.SHOPID = b.SHOPID and a.BILLNO = b.BILLNO) tb " +
                        "                   on ta.EID = tb.EID and ta.SHOPID = tb.SHOPID and ta.CUSTOMERNO = tb.CUSTOMERNO and " +
                        "                      ta.BDATE = tb.BDATE and ta.CORP = tb.CORP ")
        ;


        return querySql.toString();
    }

    private String getQuerySaleSql(DCP_ShopRecProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(getQueryCteSql(req,ShopRecData.TicketType.Sale));

        querySql.append(" SELECT tb.* " +
                        " FROM ( ")
                .append(" SELECT EID, " +
                        "             SHOPID, " +
                        "             TYPE, " +
                        "             CUSTOMERNO, " +
                        "             BDATE, " +
                        "             CORP, " +
                        "             SUM(TOT_AMT) as TOT_AMT, " +
                        "             SUM(PAY_AMT) as PAY_AMT " +
                        "      FROM  CTE ")
                .append("  group by EID, SHOPID, TYPE, CUSTOMERNO, BDATE, CORP ")
                .append(" ) ta " +
                        "   LEFT JOIN (" +
                        "             SELECT a.TYPE, " +
                        "                    a.CUSTOMERNO, " +
                        "                    o1.CORP, " +
                        "                    b.* " +
                        "             FROM DCP_SALE a " +
                        "                             LEFT JOIN DCP_ORG o1 on o1.EID = a.EID and o1.ORGANIZATIONNO = a.SHOPID " +
                        "                             LEFT JOIN DCP_SALE_DETAil b " +
                        "                                       on a.EID = b.EID and a.SHOPID = b.SHOPID and a.SALENO = b.SALENO) tb " +
                        "                   on ta.EID = tb.EID and ta.SHOPID = tb.SHOPID and ta.CUSTOMERNO = tb.CUSTOMERNO and " +
                        "                      ta.BDATE = tb.BDATE and ta.CORP = tb.CORP ")
        ;


        return querySql.toString();
    }

    //
//    private String getQueryCardSaleSql(DCP_ShopRecProcessReq req) throws Exception {
//
//    }
//
//    private String getQueryCouponSaleSql(DCP_ShopRecProcessReq req) throws Exception {
//
//    }


}
