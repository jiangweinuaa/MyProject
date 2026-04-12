package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ReconliationDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReconliationDetailQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class DCP_ReconliationDetailQuery extends SPosAdvanceService<DCP_ReconliationDetailQueryReq, DCP_ReconliationDetailQueryRes> {
    @Override
    protected void processDUID(DCP_ReconliationDetailQueryReq req, DCP_ReconliationDetailQueryRes res) throws Exception {

        List<Map<String, Object>> qData = doQueryData(getReconDetailSql(req), null);

        res.setDatas(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(qData)) {

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("RECONNO", true);
            List<Map<String, Object>> master = MapDistinct.getMap(qData, distinct);

            for (Map<String, Object> oneMaster : master) {
                DCP_ReconliationDetailQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setOrganizationNo(oneMaster.get("ORGANIZATIONNO").toString());
                oneData.setOrganizationName(oneMaster.get("ORGANIZATIONNAME").toString());
                oneData.setBdate(oneMaster.get("BDATE").toString());
                oneData.setReconNo(oneMaster.get("RECONNO").toString());
                oneData.setCorp(oneMaster.get("CORP").toString());
                oneData.setCorpName(oneMaster.get("CORPNAME").toString());
                oneData.setDataType(oneMaster.get("DATATYPE").toString());
                oneData.setBizPartnerNo(oneMaster.get("BIZPARTNERNO").toString());
                oneData.setYear(oneMaster.get("YEAR").toString());
                oneData.setMonth(oneMaster.get("MONTH").toString());
                oneData.setEstReceExpAmt(oneMaster.get("ESTRECEEXPDAY").toString());
                oneData.setCurrReconAmt(oneMaster.get("CURRRECONAMT").toString());
                oneData.setCurrReconTaxAmt(oneMaster.get("CURRRECONTAXAMT").toString());
                oneData.setCurrReconPretaxAmt(oneMaster.get("CURRRECONPRETAXAMT").toString());
                oneData.setPaidReceAmt(oneMaster.get("PAIDRECEAMT").toString());
                oneData.setNotPaidReceAmt(oneMaster.get("NOTPAIDRECEAMT").toString());
                oneData.setMemo(oneMaster.get("MEMO").toString());
                oneData.setStatus(oneMaster.get("STATUS").toString());

                oneData.setCreateBy(oneMaster.get("CREATEBY").toString());
                oneData.setCreate_Date(oneMaster.get("CREATE_DATE").toString());
                oneData.setCreate_Time(oneMaster.get("CREATE_TIME").toString());

                oneData.setModifyBy(oneMaster.get("MODIFYBY").toString());
                oneData.setModify_Date(oneMaster.get("MODIFY_DATE").toString());
                oneData.setModify_Time(oneMaster.get("MODIFY_TIME").toString());

                oneData.setConfirmBy(oneMaster.get("CONFIRMBY").toString());
                oneData.setConfirm_Date(oneMaster.get("CONFIRM_DATE").toString());
                oneData.setConfirm_Time(oneMaster.get("CONFIRM_TIME").toString());

                oneData.setCancelBy(oneMaster.get("CANCELBY").toString());
                oneData.setCancel_Date(oneMaster.get("CANCEL_DATE").toString());
                oneData.setCancel_Time(oneMaster.get("CANCEL_TIME").toString());

                Map<String, Object> condition = new HashMap<>();
                condition.put("EID", oneMaster.get("EID").toString());
                condition.put("RECONNO", oneMaster.get("RECONNO").toString());
                List<Map<String, Object>> detail = MapDistinct.getWhereMap(qData, condition, true);

                oneData.setReconList(new ArrayList<>());
                for (Map<String, Object> oneDetail : detail) {
                    DCP_ReconliationDetailQueryRes.ReconList oneRecon = res.new ReconList();
                    oneData.getReconList().add(oneRecon);

                    oneRecon.setOrganizationNo(oneDetail.get("ORGANIZATIONNO").toString());
                    oneRecon.setOrganizationName(oneDetail.get("ORGANIZATIONNAME").toString());
                    oneRecon.setCorp(oneDetail.get("CORP").toString());
                    oneRecon.setCorpName(oneDetail.get("CORPNAME").toString());
                    oneRecon.setReconNo(oneDetail.get("RECONNO").toString());
                    oneRecon.setItem(oneDetail.get("ITEM").toString());
                    oneRecon.setSourceType(oneDetail.get("SOURCETYPE").toString());
                    oneRecon.setSourceNo(oneDetail.get("SOURCENO").toString());
                    oneRecon.setSourceNoSeq(oneDetail.get("SOURCENOSEQ").toString());
                    oneRecon.setRDate(oneDetail.get("RDATE").toString());
                    oneRecon.setFee(oneDetail.get("FEE").toString());
                    oneRecon.setFeeName(oneDetail.get("FEENAME").toString());
                    oneRecon.setPluNo(oneDetail.get("PLUNO").toString());
                    oneRecon.setPluName(oneDetail.get("PLUNAME").toString());
                    oneRecon.setCurrency(oneDetail.get("CURRENCY").toString());
                    oneRecon.setTaxRate(oneDetail.get("TAXRATE").toString());
                    oneRecon.setTaxCode(oneDetail.get("TAXCODE").toString());
                    oneRecon.setDirection(oneDetail.get("DIRECTION").toString());
                    oneRecon.setBillQty(oneDetail.get("BILLQTY").toString());
                    oneRecon.setReconQty(oneDetail.get("RECONQTY").toString());
                    oneRecon.setBillPrice(oneDetail.get("BILLPRICE").toString());
                    oneRecon.setPreTaxAmt(oneDetail.get("PRETAXAMT").toString());
                    oneRecon.setAmt(oneDetail.get("AMT").toString());
                    oneRecon.setReconAmt(oneDetail.get("RECONAMT").toString());
                    oneRecon.setUnPaidAmt(oneDetail.get("UNPAIDAMT").toString());
                    oneRecon.setCurrReconAmt(oneDetail.get("CURRRECONAMT").toString());
                    oneRecon.setDepartId(oneDetail.get("DEPARTID").toString());
                    oneRecon.setCateGory(oneDetail.get("CATEGORY").toString());

                }
            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReconliationDetailQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReconliationDetailQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReconliationDetailQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ReconliationDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ReconliationDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ReconliationDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_ReconliationDetailQueryRes getResponseType() {
        return new DCP_ReconliationDetailQueryRes();
    }

    @Override
    protected String getQuerySql(DCP_ReconliationDetailQueryReq req) throws Exception {
        return super.getQuerySql(req);
    }

    private String getReconDetailSql(DCP_ReconliationDetailQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append("select e.*,a.ITEM,a.SOURCETYPE,a.SOURCENO,a.SOURCENOSEQ,a.RDATE " +
                " ,a.FEE,a.PLUNO,a.CURRENCY,a.TAXRATE,a.TAXCODE,a.DIRECTION,a.BILLQTY,a.RECONQTY,a.BILLPRICE " +
                " ,a.PRETAXAMT,a.AMT,a.RECONAMT,a.UNPAIDAMT,a.CURRRECONAMT,a.CATEGORY,a.TAXCODE " +
                " ,b.plu_name as pluname,c.fee_name as feename,d.departname,f.category_name as categoryname,g.taxname " +
                " ,ol1.ORG_NAME ORGANIZATIONNAME,ol2.ORG_NAME CORPNAME,a.DEPARTID,a.CATEGORY " +
                " from DCP_RECONDETAIL  a " +
                " LEFT JOIN DCP_RECONLIATION e on a.EID=e.EID and a.RECONNO=e.RECONNO " +
                " left join dcp_org_lang ol1 on a.eid=ol1.eid and a.organizationno=ol1.organizationno and ol1.lang_type='" + req.getLangType() + "' " +
                " left join dcp_org_lang ol2 on a.eid=ol2.eid and a.corp=ol2.organizationno and ol2.lang_type='" + req.getLangType() + "' " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='" + req.getLangType() + "' " +
                " left join dcp_fee_lang c on c.eid=a.eid and c.fee=a.fee and c.lang_type='" + req.getLangType() + "'" +
                " left join dcp_department_lang d on d.eid=a.eid and d.departno=a.departid and d.lang_type='" + req.getLangType() + "' " +
                " left join dcp_category_lang f on f.eid=a.eid and f.category=a.category and f.lang_type='" + req.getLangType() + "' " +
                " left join DCP_TAXCATEGORY_LANG g on g.eid=a.eid and g.taxcode=a.taxcode and g.lang_type='" + req.getLangType() + "'"
        );
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (CollectionUtils.isNotEmpty(req.getRequest().getReconList())) {
            querySql.append(" AND (1=2 ");
            for (DCP_ReconliationDetailQueryReq.ReconList reconList : req.getRequest().getReconList()) {
                querySql.append(" OR a.RECONNO='").append(reconList.getReconNo()).append("'");
            }
            querySql.append(")");
        }

        return querySql.toString();
    }

}
