package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurOrderBookingQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurOrderBookingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PurOrderBookingQuery extends SPosBasicService<DCP_PurOrderBookingQueryReq, DCP_PurOrderBookingQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurOrderBookingQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurOrderBookingQueryReq> getRequestType() {
        return new TypeToken<DCP_PurOrderBookingQueryReq>() {

        };
    }

    @Override
    protected DCP_PurOrderBookingQueryRes getResponseType() {
        return new DCP_PurOrderBookingQueryRes();
    }

    @Override
    protected DCP_PurOrderBookingQueryRes processJson(DCP_PurOrderBookingQueryReq req) throws Exception {
        DCP_PurOrderBookingQueryRes res = this.getResponseType();

        //try {
            String sql = this.getQuerySql(req);
            String[] conditionValues1 = {}; //查詢條件
            int totalRecords;                //总笔数
            int totalPages;
            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);
            res.setDatas(new ArrayList<>());
            if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
                totalRecords = Integer.parseInt(getQDataDetail.get(0).get("NUM").toString());

                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                res.setPageNumber(req.getPageNumber());
                res.setPageSize(req.getPageSize());
                res.setTotalRecords(totalRecords);
                res.setTotalPages(totalPages);

                for (Map<String, Object> oneData : getQDataDetail) {
                    DCP_PurOrderBookingQueryRes.Data data = res.new Data();
                    data.setPurOrderNo(oneData.get("PURORDERNO").toString());

                    data.setBDate(oneData.get("BDATE").toString());
                    data.setArrivalDate(oneData.get("ARRIVALDATE").toString());
                    data.setPurOrgNo(oneData.get("PURORGNO").toString());
                    data.setPurOrgName(oneData.get("PURORGNAME").toString());
                    data.setSupplier(oneData.get("SUPPLIER").toString());
                    data.setSupplierName(oneData.get("SUPPLIERNAME").toString());
                    data.setReceiptOrgNo(oneData.get("RECEIPTORGNO").toString());
                    data.setReceiptOrgName(oneData.get("RECEIPTORGNAME").toString());
                    data.setAddress(oneData.get("ADDRESS").toString());

                    data.setPurCorp(oneData.get("PURCORP").toString());
                    data.setPurCorpName(oneData.get("PURCORPNAME").toString());
                    data.setReceiptCorp(oneData.get("RECEIPTCORP").toString());
                    data.setReceiptCorpName(oneData.get("RECEIPTCORPNAME").toString());

                    res.getDatas().add(data);
                }
            }
            res.setSuccess(true);
            res.setServiceStatus(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E200.getCodeType());
       // } catch (Exception e) {
         //   res.setSuccess(false);
         //   res.setServiceStatus(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400.getCodeType());
         //   res.setServiceDescription(e.getMessage());
        //}
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_PurOrderBookingQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder querySql = new StringBuilder();
        querySql.append(
                " SELECT * FROM (" +
                        " SELECT COUNT(1) over() NUM, dense_rank() over(ORDER BY a.bdate, a.PURORDERNO desc) rn,a.* from ( " +
                        "   SELECT distinct a.ORGANIZATIONNO as purorgno,c.org_name as purorgname, " +
                        "   a.PURORDERNO,a.BDATE,B.ARRIVALDATE,f.org_name as receiptorgname,a.RECEIPTORGNO,a.SUPPLIER,d.sname as suppliername " +
                        "   ,a.address,a.corp as purcorp,a.receiptcorp,g.org_name as purcorpname,h.org_name as receiptcorpname " +
                        "   FROM DCP_PURORDER a " +
                        "   LEFT JOIN DCP_PURORDER_DELIVERY b ON a.EID=b.EID  AND a.PURORDERNO=b.PURORDERNO " +
                        "   left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.organizationno and c.lang_type='"+req.getLangType()+"'" +
                        "   LEFT JOIN DCP_BIZPARTNER d on d.EID=a.EID AND d.BIZPARTNERNO=a.SUPPLIER " +
                        "   LEFT JOIN DCP_ORG_LANG f ON a.EID=f.EID AND a.RECEIPTORGNO=f.ORGANIZATIONNO AND F.LANG_TYPE='"+req.getLangType()+"'" +
                        " left join dcp_org_lang g on g.eid=a.eid and g.organizationno=a.corp and g.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_org_lang h on h.eid=a.eid and h.organizationno=a.receiptcorp and h.lang_type='"+req.getLangType()+"' "+

                        " WHERE  a.EID='" + req.geteId() + "'  and a.organizationno='"+req.getOrganizationNO()+"'" +
                        " and b.PURQTY-nvl(b.BOOKQTY,0)>0 "
        );

        if("arrivalDate".equals(req.getRequest().getDateType())){
            if(Check.NotNull(req.getRequest().getBeginDate())){
                querySql.append(" AND B.ARRIVALDATE>='").append(req.getRequest().getBeginDate()).append("'");
            }
            if(Check.NotNull(req.getRequest().getEndDate())){
                querySql.append(" AND B.ARRIVALDATE<='").append(req.getRequest().getEndDate()).append("'");
            }

        }
        if("bDate".equals(req.getRequest().getDateType())){
            if(Check.NotNull(req.getRequest().getBeginDate())){
                querySql.append(" AND a.BDATE>='").append(req.getRequest().getBeginDate()).append("'");
            }
            if(Check.NotNull(req.getRequest().getEndDate())){
                querySql.append(" AND a.BDATE<='").append(req.getRequest().getEndDate()).append("'");
            }
        }


        if (StringUtils.isNotEmpty(req.getRequest().getPurOrderNo())){
            querySql.append(" AND a.PURORDERNO='").append(req.getRequest().getPurOrderNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getReceiptOrgNo())) {
            querySql.append(" AND a.RECEIPTORGNO='").append(req.getRequest().getReceiptOrgNo()).append("'");
        }
        if(Check.NotNull(req.getRequest().getStatus())){
            querySql.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("' ");
        }
        if(Check.NotNull(req.getRequest().getSupplier())){
            querySql.append(" AND a.SUPPLIER='").append(req.getRequest().getSupplier()).append("' ");
        }

        querySql.append(" and not exists (select re.receivingno from dcp_receiving re where a.eid=re.eid and a.purorderno=re.ofno and re.status='0' )");



        querySql.append(" ) a ) temp ");


        querySql.append(" WHERE rn> ")
                .append(startRow)
                .append(" and rn<= ")
                .append(startRow + pageSize)
                .append(" ORDER BY rn ");

        return querySql.toString();
    }
}
