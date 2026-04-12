package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ReceivingDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReceivingDetailQueryRes;
import com.dsc.spos.json.cust.res.DCP_StockInDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ReceivingDetailQuery extends SPosBasicService<DCP_ReceivingDetailQueryReq, DCP_ReceivingDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ReceivingDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ReceivingDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ReceivingDetailQueryReq>() {

        };
    }

    @Override
    protected DCP_ReceivingDetailQueryRes getResponseType() {
        return new DCP_ReceivingDetailQueryRes();
    }

    @Override
    protected DCP_ReceivingDetailQueryRes processJson(DCP_ReceivingDetailQueryReq req) throws Exception {
        DCP_ReceivingDetailQueryRes res = getResponseType();
        int totalRecords = 0;        //总笔数
        int totalPages = 0;

        res.setDatas(new ArrayList<>());

        List<Map<String, Object>> queryData = this.doQueryData(getQuerySql(req), null);
        List<Map<String, Object>> countData = this.doQueryData(getQueryReceiptCountSql(req),null);
        if (CollectionUtils.isNotEmpty(queryData)) {

            for (Map<String, Object> oneData : queryData) {
                DCP_ReceivingDetailQueryRes.Data data = res.new Data();

                data.setReceivingNo(oneData.get("RECEIVINGNO").toString());
                data.setStatus(oneData.get("STATUS").toString());
                data.setOrgNo(oneData.get("ORGNO").toString());
                data.setOrgName(oneData.get("ORGNAME").toString());
                data.setDocType(oneData.get("DOC_TYPE").toString());

                data.setReceiptOrgNo(oneData.get("RECEIPTORGNO").toString());
                data.setReceiptOrgName(oneData.get("RECEIPTORGNAME").toString());
                data.setBDate(DateFormatUtils.getDate(StringUtils.toString(oneData.get("BDATE"), "")));
                data.setSupplierNo(oneData.get("SUPPLIERNO").toString());
                data.setSupplierName(oneData.get("SUPPLIERNAME").toString());
                data.setReceiptDate(DateFormatUtils.getDate(StringUtils.toString(oneData.get("RECEIPTDATE"), "")));
                data.setLoadDocNo(StringUtils.toString(oneData.get("LOAD_DOCNO"), ""));
                data.setLoadDocType(StringUtils.toString(oneData.get("LOAD_DOCTYPE"), ""));

                data.setExpireDate(DateFormatUtils.getDate(StringUtils.toString(oneData.get("EXPIREDATE"), "")));
                data.setWareHouse(oneData.get("WAREHOUSE").toString());
                data.setWareHouseName(oneData.get("WAREHOUSENAME").toString());
                data.setTotCqty(oneData.get("TOT_CQTY").toString());
                data.setTotPqty(oneData.get("TOT_PQTY").toString());
                data.setTotDistriAmt(oneData.get("TOT_DISTRIAMT").toString());
                data.setTotAmt(oneData.get("TOT_AMT").toString());

                if (CollectionUtils.isNotEmpty(countData)){
                    data.setTotReceiptCqty(countData.get(0).get("CNT").toString());
                }else {
                    data.setTotReceiptCqty(oneData.get("TOT_RECEIPTCQTY").toString());
                }


                data.setTotReceiptPqty(oneData.get("TOT_RECEIPTPQTY").toString());

                data.setEmployeeID(oneData.get("EMPLOYEEID").toString());
                data.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
                data.setDepartID(oneData.get("DEPARTID").toString());
                data.setDepartName(oneData.get("DEPARTNAME").toString());
                data.setCreateDate(StringUtils.toString(oneData.get("CREATEDATE"), ""));
                data.setCreateTime(StringUtils.toString(oneData.get("CREATETIME"), ""));

                data.setCreateBy(StringUtils.toString(oneData.get("CREATEBY"), ""));
                data.setCancelBy(StringUtils.toString(oneData.get("CANCELBY"), ""));
                data.setModifyBy(StringUtils.toString(oneData.get("MODIFYBY"), ""));
                data.setConfirmBy(StringUtils.toString(oneData.get("CONFIRMBY"), ""));

                data.setCreateByName(StringUtils.toString(oneData.get("CREATEBYNAME"), ""));
                data.setCancelByName(StringUtils.toString(oneData.get("CANCELBYNAME"), ""));
                data.setModifyByName(StringUtils.toString(oneData.get("MODIFYBYNAME"), ""));
                data.setConfirmByName(StringUtils.toString(oneData.get("CONFIRMBYNAME"), ""));

                data.setModifyDate(StringUtils.toString(oneData.get("MODIFYDATE"), ""));
                data.setModifyTime(StringUtils.toString(oneData.get("MODIFYTIME"), ""));
                data.setConfirmDate(StringUtils.toString(oneData.get("CONFIRMDATE"), ""));

                data.setCancelDate(StringUtils.toString(oneData.get("CANCELDATE"), ""));
                data.setCancelTime(StringUtils.toString(oneData.get("CANCELTIME"), ""));

                data.setOwnOpID(StringUtils.toString(oneData.get("OWNOPID"), ""));
                data.setOwnOpName(StringUtils.toString(oneData.get("OWNOPNAME"), ""));
                data.setOwnDeptID(StringUtils.toString(oneData.get("OWNDEPTID"), ""));
                data.setOwnDeptName(StringUtils.toString(oneData.get("OWNDEPTNAME"), ""));

                data.setReceiptAddress(oneData.get("RECEIPTADDRESS").toString());
                data.setCloseOpId(oneData.get("CLOSEOPID").toString());
                data.setCloseOpName(oneData.get("CLOSEOPNAME").toString());
                data.setCloseTime(oneData.get("CLOSETIMES").toString());

                data.setPurType(StringUtils.toString(oneData.get("PURTYPE"), ""));
                data.setOfNo(StringUtils.toString(oneData.get("OFNO"), ""));
                data.setOType(StringUtils.toString(oneData.get("OTYPE"), ""));
                data.setCustomer(StringUtils.toString(oneData.get("CUSTOMER"), ""));
                data.setCustomerName(StringUtils.toString(oneData.get("CUSTOMERNAME"), ""));
                data.setMemo(StringUtils.toString(oneData.get("MEMO"), ""));
                data.setDeliveryNo(StringUtils.toString(oneData.get("DELIVERY_NO"), ""));
                data.setIsLocation(oneData.get("ISLOCATION").toString());
                data.setTransferShop(StringUtils.toString(oneData.get("TRANSFERSHOP"), ""));
                data.setTransferShopName(StringUtils.toString(oneData.get("TRANSFERSHOPNAME"), ""));
                data.setTransferWarehouse(StringUtils.toString(oneData.get("TRANSFERWAREHOUSE"), ""));
                data.setTransferWarehouseName(StringUtils.toString(oneData.get("TRANSFERWAREHOUSENAME"), ""));
                data.setInvWarehouse(StringUtils.toString(oneData.get("INVWAREHOUSE"), ""));
                data.setInvWarehouseName(StringUtils.toString(oneData.get("INVWAREHOUSENAME"), ""));

                data.setPackingNo(oneData.get("PACKINGNO").toString());
                data.setPTemplateNo(oneData.get("PTEMPLATENO").toString());
                data.setPTemplateName(oneData.get("PTEMPLATENAME").toString());

                data.setReason(StringUtils.toString(oneData.get("REASON"), ""));

                data.setPayType(oneData.get("PAYTYPE").toString());
                data.setPayOrgNo(oneData.get("PAYORGNO").toString());
                data.setPayOrgName(oneData.get("PAYORGNAME").toString());
                data.setPayDateNo(oneData.get("PAYDATENO").toString());
                data.setPayDateName(oneData.get("PAYDATENAME").toString());
                data.setBillDateNo(oneData.get("BILLDATENO").toString());
                data.setBillDateName(oneData.get("BILLDATENAME").toString());
                data.setInvoiceCode(oneData.get("INVOICECODE").toString());
                data.setInvoiceName(oneData.get("INVOICENAME").toString());
                data.setCurrency(oneData.get("CURRENCY").toString());
                data.setCurrencyName(oneData.get("CURRENCYNAME").toString());
                data.setPayee(oneData.get("PAYEE").toString());
                data.setPayeeName(oneData.get("PAYEENAME").toString());
                data.setPayer(oneData.get("PAYER").toString());
                data.setPayerName(oneData.get("PAYERNAME").toString());
                data.setCorp(oneData.get("CORP").toString());
                data.setCorpName(oneData.get("CORPNAME").toString());
                data.setReceiptCorp(oneData.get("RECEIPTCORP").toString());
                data.setReceiptCorpName(oneData.get("RECEIPTCORPNAME").toString());
                data.setTotPurAmt(oneData.get("TOTPURAMT").toString());
                data.setConfirmTime(oneData.get("CONFIRMTIME").toString());
                List<Map<String, Object>> detailData = this.doQueryData(getDetailQuerySql(req), null);
                //if ( !detailData.isEmpty()){
                //    String num = detailData.get(0).get("NUM").toString();
                //    totalRecords = Integer.parseInt(num);
                //算總頁數
                //    totalPages = totalRecords / req.getPageSize();
                //    totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                //}

                data.setDataList(new ArrayList<>());

                for (Map<String, Object> detail : detailData) {
                    DCP_ReceivingDetailQueryRes.Detail oneDetail = res.new Detail();

                    oneDetail.setItem(detail.get("ITEM").toString());
                    oneDetail.setListImage(detail.get("LISTIMAGE").toString());
                    oneDetail.setPluBarcode(detail.get("PLU_BARCODE").toString());
                    oneDetail.setPluNo(detail.get("PLUNO").toString());
                    oneDetail.setPluName(detail.get("PLU_NAME").toString());
                    oneDetail.setSpec(StringUtils.toString(detail.get("SPEC"), ""));
                    oneDetail.setFeatureNo(StringUtils.toString(detail.get("FEATURENO"), ""));
                    oneDetail.setFeatureName(StringUtils.toString(detail.get("FEATURENAME"), ""));
                    oneDetail.setPUnit(StringUtils.toString(detail.get("PUNIT"), ""));
                    oneDetail.setPUnitName(StringUtils.toString(detail.get("UNAME"), ""));
                    oneDetail.setPQty(StringUtils.toString(detail.get("PQTY"), ""));
                    oneDetail.setDistriPrice(StringUtils.toString(detail.get("DISTRIPRICE"), ""));
                    oneDetail.setDistriAmt(StringUtils.toString(detail.get("DISTRIAMT"), ""));
                    oneDetail.setPurQty(StringUtils.toString(detail.get("PURQTY"), ""));
                    oneDetail.setBookQty(StringUtils.toString(detail.get("BOOKQTY"), ""));
                    oneDetail.setCanBookQty(StringUtils.toString(detail.get("CANBOOKQTY"), ""));
                    oneDetail.setStockInQty(StringUtils.toString(detail.get("STOCKIN_QTY"), ""));
//                    canReceiveQty=pQty-StockInQty
                    double canReceiveQty = Double.parseDouble(oneDetail.getPQty()) - Double.parseDouble(Check.Null(oneDetail.getStockInQty())?"0":oneDetail.getStockInQty());
                    oneDetail.setCanReceiveQty(StringUtils.toString(canReceiveQty, ""));

                    oneDetail.setProcRate(StringUtils.toString(detail.get("PROC_RATE"), ""));
                    oneDetail.setIsQualityCheck(StringUtils.toString(detail.get("IS_QUALITYCHECK"), ""));
                    oneDetail.setShelfLife(StringUtils.toString(detail.get("SHELFLIFE"), ""));
                    oneDetail.setOfNo(StringUtils.toString(detail.get("OFNO"), ""));
                    oneDetail.setOType(StringUtils.toString(detail.get("OTYPE"), ""));
                    oneDetail.setOItem(StringUtils.toString(detail.get("OITEM"), ""));
                    oneDetail.setOItem2(StringUtils.toString(detail.get("OITEM2"), ""));
                    oneDetail.setMemo(StringUtils.toString(detail.get("PLU_MEMO"), ""));
                    oneDetail.setStatus(StringUtils.toString(detail.get("STATUS"), ""));
                    oneDetail.setIsGift(StringUtils.toString(detail.get("ISGIFT"), ""));
                    oneDetail.setBaseUnit(StringUtils.toString(detail.get("BASEUNIT"), ""));
                    oneDetail.setBaseUnitName(StringUtils.toString(detail.get("BASEUNITNAME"), ""));
                    oneDetail.setBaseQty(StringUtils.toString(detail.get("BASEQTY"), ""));
                    oneDetail.setUnitRatio(StringUtils.toString(detail.get("UNITRATIO"), ""));
                    oneDetail.setPoQty(StringUtils.toString(detail.get("POQTY"), ""));
                    oneDetail.setPrice(StringUtils.toString(detail.get("PRICE"), ""));
                    oneDetail.setAmt(StringUtils.toString(detail.get("AMT"), ""));
                    oneDetail.setBatchNo(StringUtils.toString(detail.get("BATCHNO"), ""));
                    oneDetail.setProdDate(StringUtils.toString(detail.get("PRODDATE"), ""));
                    oneDetail.setExpDate(StringUtils.toString(detail.get("EXPDATE"), ""));
                    oneDetail.setIsBatch(StringUtils.toString(detail.get("ISBATCH"), ""));
                    oneDetail.setInclTax(StringUtils.toString(detail.get("INCLTAX"), ""));
                    oneDetail.setTaxCode(StringUtils.toString(detail.get("TAXCODE"), ""));
                    oneDetail.setTaxRate(StringUtils.toString(detail.get("TAXRATE"), ""));
                    oneDetail.setTaxCalType(StringUtils.toString(detail.get("TAXCALTYPE"), ""));

                    oneDetail.setBsNo(detail.get("BSNO").toString());
                    oneDetail.setBsName(detail.get("BSNAME").toString());
                    oneDetail.setPackingNo(detail.get("PACKINGNO").toString());
                    oneDetail.setPurPrice(detail.get("PURPRICE").toString());
                    oneDetail.setPurAmt(detail.get("PURAMT").toString());
                    oneDetail.setCategory(detail.get("CATEGORY").toString());
                    oneDetail.setCategoryName(detail.get("CATEGORYNAME1").toString());

                    oneDetail.setPassQty(detail.get("PASSQTY").toString());
                    oneDetail.setRejectQty(detail.get("REJECTQTY").toString());
                    oneDetail.setSupPrice(detail.get("SUPPRICE").toString());
                    oneDetail.setSupAmt(detail.get("SUPAMT").toString());

                    data.getDataList().add(oneDetail);
                }

                if("5".equals(data.getDocType())){
                    String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                    String httpStr=isHttps.equals("1")?"https://":"http://";
                    String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                    if (domainName.endsWith("/")) {
                        domainName = httpStr + domainName + "resource/image/";
                    }else{
                        domainName = httpStr + domainName + "/resource/image/";
                    }
                    String imageSql="select a.* from DCP_STOCKOUT_DETAIL_IMAGE a where a.eid='"+req.geteId()+"' and a.stockoutno='"+data.getOfNo()+"'";
                    List<Map<String, Object>> imageList = this.doQueryData(imageSql,null);
                    if(CollUtil.isNotEmpty(imageList)){
                        String finalDomainName = domainName;
                        data.getDataList().forEach(x->{
                            x.setImageList(new ArrayList<>());
                            List<Map<String, Object>> detailImageList = imageList.stream().filter(y -> y.get("OITEM").toString().equals(x.getOItem())).collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(detailImageList)){
                                DCP_ReceivingDetailQueryRes.ImageList singleImage = res.new ImageList();

                                String image = detailImageList.get(0).get("IMAGE").toString();
                                String item = detailImageList.get(0).get("ITEM").toString();
                                if(Check.NotNull(image)){
                                    image= finalDomainName +image;
                                }

                                singleImage.setImage(image);
                                singleImage.setItem(item);
                                x.getImageList().add(singleImage);
                            }
                        });
                    }
                }

                res.getDatas().add(data);
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

    private String getDetailQuerySql(DCP_ReceivingDetailQueryReq req) {
        StringBuilder querySql = new StringBuilder();

        // querySql.append(" select * from ( ");

        querySql.append(
                // " SELECT COUNT(DISTINCT ITEM) OVER() NUM ,dense_rank() over(ORDER BY ITEM) rn, " +
                //  " temp.* FROM(" +
                "  SELECT a.*,b.LISTIMAGE,c.PLU_NAME, " +
                        "  j.SPEC,e.FEATURENAME,f.UNAME," +
                        "  i.PURQTY,a.PQTY BOOKQTY,i.PURQTY -k.SPQTY + a.PQTY CANBOOKQTY," +
                        "  i.IS_QUALITYCHECK,d.SHELFLIFE," +
                        "  f1.uname as baseunitname,a.UNIT_RATIO as unitratio," +
                        "  a.BATCH_NO as batchno,a.PROD_DATE as proddate,d.ISBATCH,l.bsno,l.reason_name as bsname,m.category_name as categoryname1,a.category " +
                        "  FROM DCP_RECEIVING_DETAIL a " +
                        "  LEFT JOIN DCP_GOODSIMAGE b ON a.EID=b.EID AND a.PLUNO=b.PLUNO " +
                        "  LEFT JOIN DCP_GOODS_LANG c ON c.EID=a.EID and c.PLUNO=a.PLUNO AND c.LANG_TYPE='" + req.getLangType() + "'" +
                        "  LEFT JOIN DCP_GOODS d ON d.EID=a.EID AND d.PLUNO=a.PLUNO " +
                        "  LEFT JOIN DCP_GOODS_FEATURE_LANG e ON e.EID=a.EID and e.PLUNO=a.PLUNO AND a.FEATURENO=e.FEATURENO AND e.LANG_TYPE='" + req.getLangType() + "'" +
                        "  LEFT JOIN DCP_UNIT_LANG f ON f.EID=a.EID and f.UNIT=a.PUNIT AND f.LANG_TYPE='" + req.getLangType() + "'" +
                        "  LEFT JOIN DCP_UNIT_LANG f1 ON f1.EID=a.EID and f1.UNIT=a.baseunit AND f1.LANG_TYPE='" + req.getLangType() + "'" +

                        "  LEFT JOIN DCP_RECEIVING g on g.eid=a.eid and g.ORGANIZATIONNO=a.ORGANIZATIONNO and g.RECEIVINGNO=a.RECEIVINGNO " +
                        "  LEFT JOIN DCP_PURORDER_DETAIL i ON i.EID=a.EID AND a.ofNo=i.purorderno and a.OITEM=i.ITEM " +
                        "  LEFT JOIN DCP_GOODS_UNIT_LANG j ON j.EID=a.EID AND a.PLUNO=j.PLUNO AND j.OUNIT=a.PUNIT AND j.LANG_TYPE='" + req.getLangType() + "'" +
                        "  LEFT JOIN( SELECT EID,OFNO,OITEM,SUM(PQTY) SPQTY FROM DCP_RECEIVING_DETAIL " +
                        "             GROUP BY EID,OFNO,OITEM) k ON k.EID=a.EID and k.OFNO=a.OFNO and k.OITEM=a.OITEM  " +
                        " left join dcp_reason_lang l on l.eid=a.eid and l.bsno=a.bsno and l.bstype='2' and l.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_category_lang m on m.eid=a.eid and m.category=a.category and m.lang_type='"+req.getLangType()+"' " +
                        "  where a.eid='" + req.geteId() + "' "
        );
        if (StringUtils.isNotEmpty(req.getRequest().getReceivingNo())) {
            querySql.append(" AND a.receivingno='" + req.getRequest().getReceivingNo() + "'");
        }

        querySql.append(" ORDER BY a.ITEM ASC ");

        //if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
        //querySql.append(" AND a.status='" + req.getRequest().getStatus() + "'");
        //}

        //  querySql.append(") temp  ");

        //querySql.append("  "
        //        + " ) a");

        //querySql.append("  "
        //        + " where  rn>" + startRow + " and rn<=" + (startRow + pageSize) + "  "
        //       + " ");

        return querySql.toString();
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    private String getQueryReceiptCountSql(DCP_ReceivingDetailQueryReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();

        querySql.append(" select count(distinct pluno||featureno) CNT " +
                " from( " +
                " select pluno,featureno,pQty,poQTY,STOCKIN_QTY " +
                " from DCP_RECEIVING_DETAIL a ")
                .append(" where a.eid='").append(req.geteId()).append("' ")
        ;
        if (StringUtils.isNotEmpty(req.getRequest().getReceivingNo())) {
            querySql.append(" AND a.receivingno='").append(req.getRequest().getReceivingNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append(" AND a.status='").append(req.getRequest().getStatus()).append("'");
        }
//        querySql.append(" ) where poQTY-STOCKIN_QTY<=0 and STOCKIN_QTY>0 ");
        querySql.append(" ) where STOCKIN_QTY>0 ");

        return querySql.toString();

    }

    @Override
    protected String getQuerySql(DCP_ReceivingDetailQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        querySql.append(" select * from (");

        querySql.append(
                " SELECT COUNT(DISTINCT RECEIVINGNO ) OVER() NUM ,dense_rank() over(ORDER BY RECEIVINGNO) rn, " +
                        " temp.* FROM(" +
                        "   SELECT a.DOC_TYPE, a.status, c.organizationno as orgno,c.org_name as orgname,a.receivingno,d.organizationno as receiptOrgNo,d.org_name as receiptOrgName,a.bdate,a.supplier as supplierno,e.sname as suppliername,a.LOAD_DOCNO,a.LOAD_DOCTYPE,a.receiptDate,a.warehouse,f.warehouse_name as warehousename," +
                        "   em2.employeeno as employeeid,em2.name as employeename ,dd2.DEPARTNAME as departName,dd2.DEPARTNO as departID,a.createby,em1.op_name as createbyname,a.create_date as createdate,a.create_time createtime,a.MODIFYBY,a.modify_date as modifydate,a.modify_time as modifytime,em6.op_name as modifybyname," +
                        "   a.confirmby,em3.op_name as confirmbyname,a.confirm_date as confirmdate,a.confirm_time as confirmtime,a.cancelby ,em4.op_name as cancelbyname ,a.cancel_date as canceldate,a.cancel_time as canceltime, " +
                        "   a.ownopid,em0.op_name as ownopname,a.OWNDEPTID,dd0.DEPARTNAME as ownDeptName,b.EXPIREDATE, " +
                        "   a.OFNO,a.OTYPE,a.CUSTOMER,g.sname CUSTOMERNAME,a.MEMO,a.DELIVERY_NO,h.ISLOCATION,a.TOT_PQTY,a.TOT_CQTY, " +
                        "   a.TOT_DISTRIAMT,a.TOT_AMT,b.PURTYPE,a.TRANSFER_SHOP as transfershop,i.org_name as transfershopname,a.TRANSFER_WAREHOUSE as transferwarehouse,j.warehouse_name as transferwarehousename,a.INVWAREHOUSE,k.warehouse_name as invwarehousename,a.reason  " +
                        "   ,sb.TOT_RECEIPTCQTY,sb.TOT_RECEIPTPQTY,a.packingno,a.ptemplateno,l.ptemplate_name as ptemplatename," +
                        " m.org_name as payorgname,n.name as paydatename,o.name as billdatename,p.INVOICE_NAME as invoicename,q.name as currencyname," +
                        " r.sname as payeename,s.sname as payername,to_char(a.closetime,'yyyy-MM-dd HH:mm:ss') as closetimes,A.CLOSEOPID,A.RECEIPTADDRESS,em7.op_name as closeopname,a.corp,a.receiptcorp,t.org_name as corpname,u.org_name as receiptcorpname,a.totpuramt,a.payee,a.payer,a.paytype,a.payorgno,a.paydateno,a.billdateno,a.invoicecode,a.currency  " +
                        "   FROM DCP_RECEIVING a " +
                        "   LEFT JOIN DCP_PURORDER b ON a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.otype='2' and a.ofNo=b.purorderno " +
                        "   left join dcp_org_lang c on c.eid=a.eid and c.ORGANIZATIONNO=a.ORGANIZATIONNO and c.lang_type='" + req.getLangType() + "'   " +
                        "   left join dcp_org_lang d on d.eid=a.eid and d.ORGANIZATIONNO=a.RECEIPTORGNO and d.lang_type='" + req.getLangType() + "'   " +
                        "   left join DCP_BIZPARTNER e on e.eid=a.eid and e.BIZPARTNERNO=a.supplier " +
                        "   left join dcp_warehouse_lang f on f.eid=a.eid and f.warehouse=a.warehouse and f.lang_type='" + req.getLangType() + "' " +
                        "   left join DCP_BIZPARTNER g on g.eid=a.eid and g.BIZPARTNERNO=a.CUSTOMER " +
                        "   left join dcp_warehouse h on h.eid=a.eid and h.warehouse=a.warehouse " +
                        "   left join platform_staffs_lang em0 on em0.eid=a.eid and em0.opno=a.OWNOPID and em0.lang_type='"+req.getLangType()+"' " +
                        "   left join platform_staffs_lang em1 on em1.eid=a.eid and em1.opno=a.CREATEBY and em1.lang_type='"+req.getLangType()+"' " +
                        "   left join DCP_employee em2 on em2.eid=a.eid and em2.employeeno=a.EMPLOYEEID " +
                        "   left join platform_staffs_lang em3 on em3.eid=a.eid and em3.opno=a.CONFIRMBY and em3.lang_type='"+req.getLangType()+"' " +
                        "   left join platform_staffs_lang em4 on em4.eid=a.eid and em4.opno=a.CANCELBY and em4.lang_type='"+req.getLangType()+"' " +
                        "   left join platform_staffs_lang em6 on em6.eid=a.eid and em6.opno=a.MODIFYBY and em6.lang_type='"+req.getLangType()+"' " +
                        "   left join platform_staffs_lang em7 on em7.eid=a.eid and em7.opno=a.CLOSEOPID and em7.lang_type='"+req.getLangType()+"' " +
                        "   left join dcp_department_lang dd0 on dd0.eid=a.eid and dd0.departno=a.OWNDEPTID and dd0.lang_type='" + req.getLangType() + "'  " +
                        "   left join dcp_department_lang dd2 on dd2.eid=a.eid and dd2.departno=a.DEPARTID and dd2.lang_type='" + req.getLangType() + "' " +
                        "   left join dcp_org_lang i on i.eid=a.eid and i.ORGANIZATIONNO=a.transfer_shop and i.lang_type='" + req.getLangType() + "'   " +
                        "   left join dcp_warehouse_lang j on j.eid=a.eid and j.warehouse=a.transfer_warehouse and j.lang_type='" + req.getLangType() + "'  " +
                        "   left join dcp_warehouse_lang k on k.eid=a.eid and k.warehouse=a.invwarehouse and k.lang_type='" + req.getLangType() + "'  " +
                        "   left join ( " +
                        "       SELECT SHOPID,RECEIVINGNO,ORGANIZATIONNO,EID, " +
                        "       COUNT(CASE WHEN POQTY-STOCKIN_QTY <= 0 THEN 1 END) AS TOT_RECEIPTCQTY, " +
                        "       SUM(NVL(STOCKIN_QTY,0)) TOT_RECEIPTPQTY " +
                        "       FROM DCP_RECEIVING_DETAIL " +
                        "       GROUP BY SHOPID,RECEIVINGNO,ORGANIZATIONNO,EID) sb on sb.eid=a.eid and sb.SHOPID=a.SHOPID and a.RECEIVINGNO=sb.RECEIVINGNO and a.ORGANIZATIONNO=sb.ORGANIZATIONNO " +
                        "   left join DCP_PTEMPLATE l on l.eid=a.eid and l.ptemplateno=a.ptemplateno " +
                        " left join dcp_org_lang m on m.eid=a.eid and m.organizationno=a.payorgno and m.lang_type='"+req.getLangType()+"' " +
                        " left join DCP_PAYDATE_LANG n on n.eid=a.eid and n.paydateno=a.paydateno and n.lang_type='"+req.getLangType()+"' " +
                        " left join DCP_BILLDATE_LANG o on o.eid=a.eid and o.billdateno=a.billdateno and o.lang_type='"+req.getLangType()+"' " +
                        " left join DCP_INVOICETYPE_LANG p on p.eid=a.eid and p.invoicecode=a.invoicecode " +
                        " left join DCP_CURRENCY_LANG q on q.eid=a.eid and q.currency=a.currency " +
                        " left join dcp_bizpartner r on r.eid=a.eid and r.bizpartnerno=a.payee " +
                        " left join dcp_bizpartner s on s.eid=a.eid and s.bizpartnerno=a.payer " +
                        " left join dcp_org_lang t on t.eid=a.eid and t.organizationno=a.corp and t.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_org_lang u on u.eid=a.eid and u.organizationno=a.receiptcorp and u.lang_type='"+req.getLangType()+"' " +
                        "   where a.eid='" + req.geteId() + "' "
        );

        if (StringUtils.isNotEmpty(req.getRequest().getReceivingNo())) {
            querySql.append(" AND a.receivingno='").append(req.getRequest().getReceivingNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append(" AND a.status='").append(req.getRequest().getStatus()).append("'");
        }

        querySql.append(") temp  ");

        querySql.append("  "
                + " ) a");

        return querySql.toString();
    }
}
