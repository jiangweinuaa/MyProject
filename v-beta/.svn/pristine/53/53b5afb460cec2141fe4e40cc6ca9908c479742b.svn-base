package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DemandQueryReq;
import com.dsc.spos.json.cust.res.DCP_DemandQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_DemandQuery  extends SPosBasicService<DCP_DemandQueryReq, DCP_DemandQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_DemandQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (req.getRequest() == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        } else {
            //分页查询的服务，必须给值，不能为0
            int pageNumber = req.getPageNumber();
            int pageSize = req.getPageSize();
            if (pageNumber == 0) {
                isFail = true;
                errMsg.append("分页查询pageNumber不能为0,");
            }
            if (pageSize == 0) {
                isFail = true;
                errMsg.append("分页查询pageSize不能为0,");
            }

        }


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_DemandQueryReq> getRequestType() {
        return new TypeToken<DCP_DemandQueryReq>(){};
    }

    @Override
    protected DCP_DemandQueryRes getResponseType() {
        return new DCP_DemandQueryRes();
    }

    @Override
    protected DCP_DemandQueryRes processJson(DCP_DemandQueryReq req) throws Exception {
        DCP_DemandQueryRes res = this.getResponse();
        //查询
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        res.setDatas(new ArrayList<>());

        if (CollectionUtils.isNotEmpty(getQData)) {
            for (Map<String, Object> map : getQData){

                DCP_DemandQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setObjectType(map.get("OBJECTTYPE").toString());
                level1Elm.setObjectId(map.get("OBJECTID").toString());
                level1Elm.setObjectName(map.get("OBJECTNAME").toString());
                level1Elm.setOrderNo(map.get("ORDERNO").toString());
                level1Elm.setOrderItem(map.get("ORDERITEM").toString());
                level1Elm.setPluName(map.get("PLUNAME").toString());
                level1Elm.setPluBarcode(map.get("PLUBARCODE").toString());
                level1Elm.setPluNo(map.get("PLUNO").toString());
                level1Elm.setCategory(map.get("CATEGORY").toString());
                level1Elm.setCategoryName(map.get("CATEGORYNAME").toString());
                level1Elm.setFeatureNo(map.get("FEATURENO").toString());
                level1Elm.setFeatureName(map.get("FEATURENAME").toString());
                level1Elm.setSpec(map.get("SPEC").toString());
                level1Elm.setOUnit(map.get("OUNIT").toString());
                level1Elm.setOUnitName(map.get("OUNITNAME").toString());
                level1Elm.setOQty(map.get("OQTY").toString());
                level1Elm.setToPoQty(map.get("TOPOQTY").toString());
                level1Elm.setCanPoQty(map.get("CANPOQTY").toString());
                level1Elm.setPurQty(map.get("PURQTY").toString());
                level1Elm.setPurUnit(map.get("PURUNIT").toString());
                level1Elm.setPurUnitName(map.get("PURUNITNAME").toString());
                level1Elm.setPurUnitUdLength(map.get("PURUNITUDLENGTH").toString());
                level1Elm.setPurUnitRoundType(map.get("PURUNITROUNDTYPE").toString());
                level1Elm.setBaseUnit(map.get("BASEUNIT").toString());
                level1Elm.setBaseUnitName(map.get("BASEUNITNAME").toString());
                level1Elm.setPUnitRatio(map.get("PUNITRATIO").toString());
                level1Elm.setOUnitRatio(map.get("OUNITRATIO").toString());
                level1Elm.setMinPurQty(map.get("MINPURQTY").toString());
                level1Elm.setMulPurQty(map.get("MULPURQTY").toString());
                level1Elm.setPurPrice(map.get("PURPRICE").toString());
                level1Elm.setTaxCode(map.get("TAXCODE").toString());
                level1Elm.setTaxName(map.get("TAXNAME").toString());
                level1Elm.setTaxRate(map.get("TAXRATE").toString());
                level1Elm.setInclTax(map.get("INCLTAX").toString());
                level1Elm.setTaxCalType(map.get("TAXCALTYPE").toString());
                level1Elm.setSupplier(map.get("SUPPLIER").toString());
                level1Elm.setSupplierName(map.get("SUPPLIERNAME").toString());
                level1Elm.setBeginDate(map.get("BEGINDATE").toString());
                level1Elm.setEndDate(map.get("ENDDATE").toString());
                level1Elm.setPreDays(map.get("PREDAYS").toString());
                level1Elm.setPurTemplateNo(map.get("PURTEMPLATENO").toString());
                level1Elm.setPurType(map.get("PURTYPE").toString());
                level1Elm.setPurCenter(map.get("PURCENTER").toString());
                level1Elm.setPurCenterName(map.get("PURCENTERNAME").toString());
                level1Elm.setPurCenterCorp(map.get("PURCENTERCORP").toString());
                level1Elm.setPurCenterCorpName(map.get("PURCENTERCORPNAME").toString());
                level1Elm.setPayType(map.get("PAYTYPE").toString());
                level1Elm.setPayCenter(map.get("PAYCENTER").toString());
                level1Elm.setPayCenterName(map.get("PAYCENTERNAME").toString());
                level1Elm.setBillDateNo(map.get("BILLDATENO").toString());
                level1Elm.setBillDateDesc(map.get("BILLDATEDESC").toString());
                level1Elm.setPayDateNo(map.get("PAYDATENO").toString());
                level1Elm.setPayDateDesc(map.get("PAYDATEDESC").toString());
                level1Elm.setInvoiceCode(map.get("INVOICECODE").toString());
                level1Elm.setInvoiceName(map.get("INVOICENAME").toString());
                level1Elm.setCurrency(map.get("CURRENCY").toString());
                level1Elm.setCurrencyName(map.get("CURRENCYNAME").toString());
                level1Elm.setDistriOrgNo(map.get("DISTRIORGNO").toString());
                level1Elm.setDistriOrgName(map.get("DISTRIORGNAME").toString());
                level1Elm.setDistriOrgAddress(map.get("DISTRIORGADDRESS").toString());
                level1Elm.setDistriOrgContact(map.get("DISTRIORGCONTACT").toString());
                level1Elm.setDistriOrgTelePhone(map.get("DISTRIORGTELEPHONE").toString());
                level1Elm.setReceiptAddress(map.get("RECEIPTADDRESS").toString());
                level1Elm.setReceiptContact(map.get("RECEIPTCONTACT").toString());
                level1Elm.setReceiptTelephone(map.get("RECEIPTTELEPHONE").toString());



                res.getDatas().add(level1Elm);

            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_DemandQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        DCP_DemandQueryReq.LevelRequest request = req.getRequest();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String eId=req.geteId();
        String shopId = req.getShopId();

        //在途量 可用库存量  后面集合处理
        String langType=req.getLangType();

        sqlbuf.append("select a.objectType,a.objectId,b.sname as objectName,a.orderno,a.item as orderitem,a.PLUBARCODE,a.pluno,d.plu_name as pluname,c.spec,a.featureno,e,featurename,f.category,f.CATEGORY_NAME as categoryname," +
                " g.unit as ounit ,g.uname as ounitname,a.pqty as oqty,j.unit as purunit,j.uname as purunitname,k.UDLENGTH as purUnitUdLength,k.ROUNDTYPE as purUnitRoundType,a.baseunit,l.uname as baseunitname ,m.UNITRATIO as pUnitRatio,n.unitratio as ounitratio," +
                " o.MINPQTY as minPurQty,o.MULPQTY as mulPurQty ,o.PURBASEPRICE as purPrice ,q.taxcode,q.taxname,p.taxrate,p.inclTax,p.TAXCALTYPE,o1.SUPPLIERNO,o2.sname as suppliername,o2.begindate,o2.enddate,o1.PRE_DAY as predays,o1.PURTEMPLATENO,o1.purtype ," +
                " o1.purcenter ,r.org_name as purcentername , r1.corp as purCenterCorp,r1.corp_name as purCenterCorpName,r2.organizationno as paycenter ,r2.org_name as payCenterName,s.paydateno,s.name as payDateDesc,t.billdateno,t.name as billdatedesc,u.INVOICECODE,u.INVOICE_NAME as invoiceName,v.currency,v.name as currencyname," +
                " w1.organization as distriOrgNo,w.org_name as distriOrgName,w1.address as distriOrgAddress,'' as distriOrgContact,w1.phone as distriOrgTelePhone ,i3.address as receiptAddress,'' as receiptContact,w1.phone as receiptTelephone from DCP_DEMAND a " +
                " left join dcp_bizpartner b on a.eid=b.eid and a.organizationno and a.objecttype=b.biztype and a.objectid=b.BIZPARTNERNO " +
                " left join dcp_goods c on a.eid=b.eid and a.pluno=c.pluno " +
                " left join dcp_goods_lang d on a.eid=d.eid and a.pluno=d.pluno and d.lang_type='"+langType+"' " +
                " left join DCP_GOODS_FEATURE_LANG e on a.eid=e.eid and a.pluno=e.pluno and a.featureno=e.featureno and e.lang_type='"+langType+"' " +
                " left join DCP_CATEGORY_LANG f on f.eid=a.eid and f.category=c.category and f.lang_type='"+langType+"' " +
                " left join dcp_unit_lang g on g.unit=a.punit and g.lang_type='"+langType+"' " +
                " left join DCP_PORDER_DETAIL h on h.eid=a.eid and h.organizationno=a.organizationno and h.porderno=a.orderno and h.item=a.item " +
                " LEFT JOIN DCP_PURORDER_SOURCE i ON i.eid=h.eid and h.organizationno=h.organizationno and i.SOURCEBILLNO=h.porderno and i.oitem=h.item " +
                " left join DCP_PURORDER i1 on i1.eid=i.eid and i1.organizationno=i.organizationno and i1.purorder=i.purorder "+
                " left join dcp_org i3 on i3.eid=i1.eid and i3.organizationno=i1.RECEIPTORGNO "+
                " left join dcp_unit_lang j on j.eid=i.eid and j.unit=i.PURUNIT and j.lang_type='"+langType+"'" +
                " left join dcp_unit k on k.eid=j.eid and k.unit=j.unit  " +
                " left join dcp_unit_lang l on l.unit=a.baseunit and l.eid=a.eid and l.lang_type='"+langType+"' " +
                " left join DCP_GOODS_UNIT m on m.eid=a.eid and m.pluno=a.pluno and m.unit=a.baseunit and m.ounit=i.purunit "+
                " left join DCP_GOODS_UNIT n on n.eid=a.eid and n.pluno=a.pluno and n.unit=a.baseunit and n.ounit=a.punit " +
                " left join DCP_PURCHASETEMPLATE_GOODS o on o.eid=a.eid and o.PURTEMPLATENO=a.TEMPLATENO and o.pluno=a.pluno " +
                " left join DCP_TAXCATEGORY p on p.eid=o.eid and p.taxcode=o.taxcode " +
                " left join DCP_TAXCATEGORY_LANG q on q.eid=p.eid and q.taxcode=p.taxcode and q.lang_type='"+langType+"' " +
                " left join DCP_PURCHASETEMPLATE o1 on o1.eid=o.eid and o1.PURTEMPLATENO=o.PURTEMPLATENO " +
                " left join dcp_bizpartner o2 on o1.eid=o2.eid and o1.organizationno=o2.organizationno  and o1.SUPPLIERNO=o2.BIZPARTNERNO " +
                " left join dcp_org_lang  r on r.eid=o1.eid and r.organizationno=o1.purcenter and r.lang_type='"+langType+"'" +
                " left join dcp_org r1 on r1.eid=r.eid and r1.organizationno=r.organizationno  " +
                " left join dcp_org_lang  r2 on o2.eid=r2.eid and r2.organizationno=o2.paycenter and r2.lang_type='"+langType+"'" +
                " left join DCP_PAYDATE_LANG s on s.eid=o2.eid and s.paydateno=o2.paydateno and s.lang_type='"+langType+"' " +
                " left join DCP_billDATE_LANG t on t.eid=o2.eid and t.billdateno=o2.billdateno and t.lang_type='"+langType+"' " +
                " left join DCP_INVOICETYPE_LANG u on u.eid=o2.eid and u.INVOICECODE=o2.INVOICECODE and u.lang_type='"+langType+"'" +
                " left join DCP_CURRENCY_LANG v on v.eid=o2.eid and v.currency=o2.currency and v.lang_type='"+langType+"'" +
                " left join dcp_org_lang w on w.eid=a.eid and w.organizationno=a.DELIVERORGNO and w.lang_type='"+langType+"'" +
                " left join dcp_org w1 on w1.eid=w.eid and w1.organization=a.organizationno  " +
                "");


        sqlbuf.append("  ) a  ") ;
        sqlbuf.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY purTemplateNo ");


        return sqlbuf.toString();

    }

}
