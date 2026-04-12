package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ReconDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReconDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ReconDetailQuery extends SPosBasicService<DCP_ReconDetailQueryReq, DCP_ReconDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ReconDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ReconDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ReconDetailQueryReq>(){};
    }

    @Override
    protected DCP_ReconDetailQueryRes getResponseType() {
        return new DCP_ReconDetailQueryRes();
    }

    @Override
    protected DCP_ReconDetailQueryRes processJson(DCP_ReconDetailQueryReq req) throws Exception {
        DCP_ReconDetailQueryRes res = this.getResponse();

        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            for (Map<String, Object> row : getQData){
                DCP_ReconDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setStatus(row.get("STATUS").toString());

                level1Elm.setOrganizationNo(row.get("ORGANIZATIONNO").toString());
                level1Elm.setOrganizationName(row.get("ORGANIZATIONNAME").toString());
                level1Elm.setBdate(row.get("BDATE").toString());
                level1Elm.setBillNo(row.get("RECONNO").toString());
                level1Elm.setCorp(row.get("CORP").toString());
                level1Elm.setCorpName(row.get("CORPNAME").toString());
                level1Elm.setDataType(row.get("DATATYPE").toString());
                level1Elm.setBizPartnerNo(row.get("BIZPARTNERNO").toString());
                level1Elm.setBizPartnerName(row.get("BIZPARTNERNAME").toString());
                level1Elm.setYear(row.get("YEAR").toString());
                level1Elm.setMonth(row.get("MONTH").toString());
                level1Elm.setEstReceExpDay(row.get("ESTRECEEXPDAY").toString());
                level1Elm.setCurrReconAmt(row.get("CURRRECONAMT").toString());
                level1Elm.setCurrReconTaxAmt(row.get("CURRRECONTAXAMT").toString());
                level1Elm.setCurrReconPretaxAmt(row.get("CURRRECONPRETAXAMT").toString());
                level1Elm.setPaidReceAmt(row.get("PAIDRECEAMT").toString());
                level1Elm.setNotPaidReceAmt(row.get("NOTPAIDRECEAMT").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStartDate(row.get("STARTDATE").toString());
                level1Elm.setEndDate(row.get("ENDDATE").toString());

                level1Elm.setCurrency(row.get("CURRENCY").toString());
                level1Elm.setCurrencyName(row.get("CURRENCYNAME").toString());
                level1Elm.setPayDateNo(row.get("PAYDATENO").toString());
                level1Elm.setPayDateDesc(row.get("PAYDATEDESC").toString());

                level1Elm.setCreateBy(row.get("CREATEBY").toString());
                level1Elm.setCreateByName(row.get("CREATEBYNAME").toString());
                level1Elm.setCreate_Date(row.get("CREATE_DATE").toString());
                level1Elm.setCreate_Time(row.get("CREATE_TIME").toString());
                level1Elm.setModifyBy(row.get("MODIFYBY").toString());
                level1Elm.setModifyByName(row.get("MODIFYBYNAME").toString());
                level1Elm.setModify_Date(row.get("MODIFY_DATE").toString());
                level1Elm.setModify_Time(row.get("MODIFY_TIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirm_Date(row.get("CONFIRM_DATE").toString());
                level1Elm.setConfirm_Time(row.get("CONFIRM_TIME").toString());
                level1Elm.setCancelBy(row.get("CANCELBY").toString());
                level1Elm.setCancelByName(row.get("CANCELBYNAME").toString());
                level1Elm.setCancel_Date(row.get("CANCEL_DATE").toString());
                level1Elm.setCancel_Time(row.get("CANCEL_TIME").toString());

                level1Elm.setReconList(new ArrayList<>());


                String detailSql = this.getReconDetailSql(req);
                List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);
                if(CollUtil.isNotEmpty(getDetailData)){
                    for (Map<String, Object> detailRow : getDetailData){
                        DCP_ReconDetailQueryRes.Detail detail = res.new Detail();
                        detail.setOrganizationNo(level1Elm.getOrganizationNo());
                        detail.setOrganizationName(level1Elm.getOrganizationName());
                        detail.setCorp(level1Elm.getCorp());
                        detail.setCorpName(level1Elm.getCorpName());
                        detail.setBillNo(detailRow.get("RECONNO").toString());
                        detail.setItem(detailRow.get("ITEM").toString());
                        detail.setSourceType(detailRow.get("SOURCETYPE").toString());
                        detail.setSourceNo(detailRow.get("SOURCENO").toString());
                        detail.setSourceNoSeq(detailRow.get("SOURCENOSEQ").toString());
                        detail.setRDate(detailRow.get("RDATE").toString());
                        detail.setFee(detailRow.get("FEE").toString());
                        detail.setFeeName(detailRow.get("FEENAME").toString());
                        detail.setPluNo(detailRow.get("PLUNO").toString());
                        detail.setPluName(detailRow.get("PLUNAME").toString());
                        detail.setCurrency(detailRow.get("CURRENCY").toString());
                        detail.setTaxRate(detailRow.get("TAXRATE").toString());
                        detail.setDirection(detailRow.get("DIRECTION").toString());
                        detail.setBillQty(detailRow.get("BILLQTY").toString());
                        detail.setReconQty(detailRow.get("RECONQTY").toString());
                        detail.setBillPrice(detailRow.get("BILLPRICE").toString());
                        detail.setPreTaxAmt(detailRow.get("PRETAXAMT").toString());
                        detail.setAmt(detailRow.get("AMT").toString());
                        detail.setReconAmt(detailRow.get("RECONAMT").toString());
                        detail.setUnPaidAmt(detailRow.get("UNPAIDAMT").toString());
                        detail.setCurrReconAmt(detailRow.get("CURRRECONAMT").toString());
                        detail.setDepartId(detailRow.get("DEPARTID").toString());
                        detail.setCateGory(detailRow.get("CATEGORY").toString());
                        detail.setDepartName(detailRow.get("DEPARTNAME").toString());
                        detail.setCateGoryName(detailRow.get("CATEGORYNAME").toString());
                        detail.setTaxCode(detailRow.get("TAXCODE").toString());
                        detail.setTaxName(detailRow.get("TAXNAME").toString());
                        level1Elm.getReconList().add(detail);
                    }
                }



                res.getDatas().add(level1Elm);

            }

        }

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_ReconDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String dataType = req.getRequest().getDataType();
        String reconNo = req.getRequest().getReconNo();
        StringBuffer sqlbuf=new StringBuffer();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;


        sqlbuf.append(" "
                + " select  "
                + " a.*,b.org_name as organizationname,c.org_name as corpname,d.sname as bizpartnername,e.name as currencyname,f.name as paydatedesc," +
                " g0.op_name as createbyname,g1.op_name as modifybyname,g2.op_name as confirmbyname,g3.op_name as cancelbyname " +
                "  "
                + " from DCP_RECONLIATION a"
                + " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+langType+"' "
                + " left join dcp_org_lang c on a.eid=c.eid and a.corp=c.organizationno and c.lang_type='"+langType+"' " +
                " left join dcp_bizpartner d on d.eid=a.eid and d.BIZPARTNERNO=a.bizpartnerno" +
                " left join dcp_currency_lang e on e.eid=a.eid and e.currency=a.currency and e.lang_type='"+langType+"' " +
                " left join DCP_PAYDATE_LANG f on f.eid=a.eid and f.paydateno=a.paydateno and f.lang_type='"+langType+"'" +
                " left join platform_staffs_lang g0 on g0.eid=a.eid and g0.opno=a.createby and g0.lang_type='"+langType+"' " +
                " left join platform_staffs_lang g1 on g1.eid=a.eid and g0.opno=a.modifyBy and g1.lang_type='"+langType+"' " +
                " left join platform_staffs_lang g2 on g2.eid=a.eid and g0.opno=a.confirmby and g2.lang_type='"+langType+"' " +
                " left join platform_staffs_lang g3 on g3.eid=a.eid and g0.opno=a.cancelby and g3.lang_type='"+langType+"' " +
                " " +
                ""
                + " where a.eid='"+eId+"' and a.reconno='"+reconNo+"' and a.datatype='"+dataType+"' "

                + " ");

        return sqlbuf.toString();
    }

    private String getReconDetailSql(DCP_ReconDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf =  new StringBuffer();

        sqlbuf.append("select distinct a.*,b.plu_name as pluname,c.fee_name as feename,d.departname,f.category_name as categoryname,g.taxname " +
                " from DCP_RECONDETAIL a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_fee_lang c on c.eid=a.eid and c.fee=a.fee and c.lang_type='"+req.getLangType()+"'" +
                " left join dcp_department_lang d on d.eid=a.eid and d.departno=a.departid and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_category_lang f on f.eid=a.eid and f.category=a.category and f.lang_type='"+req.getLangType()+"' " +
                " left join DCP_TAXCATEGORY_LANG g on g.eid=a.eid and g.taxcode=a.taxcode and g.lang_type='"+req.getLangType()+"'" +
                " where a.reconNo='"+req.getRequest().getReconNo()+"' " +
                " and a.eid='"+req.geteId()+"'");

        return sqlbuf.toString();
    }

}


