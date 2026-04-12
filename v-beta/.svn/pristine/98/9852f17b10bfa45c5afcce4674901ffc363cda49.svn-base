package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_StockOutNoticePendingDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockOutNoticePendingDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_StockOutNoticePendingDetailQuery extends SPosBasicService<DCP_StockOutNoticePendingDetailQueryReq, DCP_StockOutNoticePendingDetailQueryRes>
{
    @Override
    protected boolean isVerifyFail(DCP_StockOutNoticePendingDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockOutNoticePendingDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_StockOutNoticePendingDetailQueryReq>(){};
    }

    @Override
    protected DCP_StockOutNoticePendingDetailQueryRes getResponseType() {
        return new DCP_StockOutNoticePendingDetailQueryRes();
    }

    @Override
    protected DCP_StockOutNoticePendingDetailQueryRes processJson(DCP_StockOutNoticePendingDetailQueryReq req) throws Exception {

        //查詢資料
        DCP_StockOutNoticePendingDetailQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        int totalRecords=0;
        int totalPages=0;

        String sql = this.getQuerySql(req);				  												//查询总笔数
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && getQData.isEmpty() == false) // 有資料，取得詳細內容
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> oneData : getQData) {
                DCP_StockOutNoticePendingDetailQueryRes.DataDetail dataDetail = res.new DataDetail();

                dataDetail.setNoticeNo(oneData.get("NOTICENO").toString());
                dataDetail.setNoticeItem(oneData.get("NOTICEITEM").toString());
                dataDetail.setObjectId(oneData.get("OBJECTID").toString());
                dataDetail.setObjectName(oneData.get("OBJECTNAME").toString());
                dataDetail.setTemplateNo(oneData.get("TEMPLATENO").toString());
                dataDetail.setTemplateName(oneData.get("TEMPLATENAME").toString());
                dataDetail.setPluNo(oneData.get("PLUNO").toString());
                dataDetail.setPluName(oneData.get("PLUNAME").toString());
                dataDetail.setSpec(oneData.get("SPEC").toString());
                dataDetail.setFeatureNo(oneData.get("FEATURENO").toString());
                dataDetail.setFeatureName(oneData.get("FEATURENAME").toString());
                dataDetail.setPluBarcode(oneData.get("PLUBARCODE").toString());
                dataDetail.setWarehouse(oneData.get("WAREHOUSE").toString());
                dataDetail.setWarehouseName(oneData.get("WAREHOUSENAME").toString());
                dataDetail.setPUnit(oneData.get("PUNIT").toString());
                dataDetail.setPUnitName(oneData.get("PUNITNAME").toString());
                dataDetail.setPQty(oneData.get("PQTY").toString());
                dataDetail.setStockOutQty(oneData.get("STOCKOUTQTY").toString());
                BigDecimal subtract = new BigDecimal(dataDetail.getPQty()).subtract(new BigDecimal(dataDetail.getStockOutQty()));
                dataDetail.setCanStockOutQty(subtract.toString());
                dataDetail.setTaxCode(oneData.get("TAXCODE").toString());
                dataDetail.setTaxRate(oneData.get("TAXRATE").toString());
                dataDetail.setTaxCalType(oneData.get("TAXCALTYPE").toString());
                dataDetail.setInclTax(oneData.get("INCLTAX").toString());
                dataDetail.setBaseUnitName(oneData.get("BASEUNITNAME").toString());
                dataDetail.setBaseUnit(oneData.get("BASEUNIT").toString());
                dataDetail.setBaseQty(oneData.get("BASEQTY").toString());
                dataDetail.setUnitRatio(oneData.get("UNITRATIO").toString());
                dataDetail.setOfNo(oneData.get("OFNO").toString());
                dataDetail.setOItem(oneData.get("OITEM").toString());
                dataDetail.setPrice(oneData.get("PRICE").toString());
                dataDetail.setAmt(oneData.get("AMT").toString());
                dataDetail.setRetailPrice(oneData.get("RETAILPRICE").toString());
                dataDetail.setRetailAmt(oneData.get("RETAILAMT").toString());

                res.getDatas().add(dataDetail);

            }
        }
        else
        {
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        //調整查出來的資料
    }

    @Override
    protected String getQuerySql(DCP_StockOutNoticePendingDetailQueryReq req) throws Exception {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        String langType = req.getLangType();
        String eid = req.geteId();
        String organizationNO = req.getOrganizationNO();

        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        sqlbuf.append("select * from ( ");
        sqlbuf.append("select count(*) over() as num,ROWNUM rn, a.billno as noticeNo,b.item as noticeItem,b.objectid,(case when b.OBJECTTYPE='3' then d.org_name else c.sname end) as objectName," +
                " (case when b.TEMPLATETYPE='1' then b.templateno else f.PTEMPLATE_NAME end ) as templateName,b.templateno,b.pluno,g.plu_name as pluname,g1.spec, h.featureno,h.featurename," +
                " b.pluBarcode,i.warehouse,i.warehouse_name as warehousename,b.punit ,j.uname as punitname,b.pqty,b.stockOutQty,b.taxCode, b.taxRate,b.inclTax, b.taxCalType ," +
                " b.baseUnit,b.baseQty,b.unitRatio,b.SOURCEBILLNO as ofNo,b.oItem,b.price,b.AMOUNT amt,b.retailPrice,b.retailAmt,k.uname as baseunitname   " +
                " from DCP_STOCKOUTNOTICE a " +
                " left join DCP_STOCKOUTNOTICE_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.BILLNO=b.BILLNO " +
                " left join DCP_BIZPARTNER c on a.eid=c.eid and a.organizationno=c.organizationno and b.objectid=c.BIZPARTNERNO " +
                " left join DCP_ORG_LANG d on a.eid=d.eid and b.objectid=d.organizationno and d.lang_type='"+langType+"' " +
                " left join DCP_PURCHASETEMPLATE e on e.eid=a.eid and e.PURTEMPLATENO=b.templateno " +
                " left join DCP_PTEMPLATE f on f.eid=a.eid and b.templateno=f.PTEMPLATENO " +
                " left join dcp_goods_lang g on g.eid=a.eid and g.pluno=b.pluno and g.lang_type='"+langType+"' " +
                " left join dcp_goods g1 on g1.eid=a.eid and g1.pluno=b.pluno " +
                " left join DCP_GOODS_FEATURE_LANG h on h.eid=b.eid and h.featureno=b.featureno and h.lang_type='"+langType+"' and h.pluno=b.pluno " +
                " left join DCP_WAREHOUSE_LANG i on i.eid=b.eid and i.organizationno=a.DELIVERORGNO and i.warehouse=b.warehouse and i.lang_type='"+langType+"' " +
                " left join dcp_unit_lang j on j.eid=b.eid and j.unit=b.punit and j.lang_type='"+langType+"'" +
                " left join dcp_unit_lang k on k.eid=b.eid and k.unit=b.baseunit and k.lang_type='"+langType+"'" +
                " " +
                " where a.eid='"+eid+"'  and nvl(b.pqty,0)>nvl(b.stockoutqty,0)  and b.status='1' " +//只能查待出货的明细
                " ");

        if(!Check.Null(req.getRequest().getObjectType())){
            sqlbuf.append(" and  a.objecttype='"+req.getRequest().getObjectType()+"' ");
        }
        if(!Check.Null(req.getRequest().getStockOutOrgNo())){
            sqlbuf.append(" and a.DELIVERORGNO='"+req.getRequest().getStockOutOrgNo()+"' ");
        }else{
            sqlbuf.append(" and a.DELIVERORGNO='"+organizationNO+"' ");
        }
        if(!Check.Null(req.getRequest().getTemplateNo())){
            sqlbuf.append(" and b.templateno='"+req.getRequest().getTemplateNo()+"' ");
        }
        if(!Check.Null(req.getRequest().getBillType())){
            sqlbuf.append(" and a.BILLTYPE='"+req.getRequest().getBillType()+"' ");
        }
        if(req.getRequest().getObjectID()!=null&&req.getRequest().getObjectID().length>0){
            if(req.getRequest().getObjectID().length==1&&req.getRequest().getObjectID()[0].toString().equals("")){

            }else {
                sqlbuf.append(" and b.objectid in ('" + getString(req.getRequest().getObjectID()) + "') ");
            }
        }

        if(Check.NotNull(req.getRequest().getNoticeNo())){
            sqlbuf.append(" and a.billno='"+req.getRequest().getNoticeNo()+"' ");
        }

        if(Check.NotNull(req.getRequest().getKeyTxt())){
            sqlbuf.append(""
                    + "AND (a.billNo like '%%"+ req.getRequest().getKeyTxt() +"%%'  " +
                    " or a.SOURCEBILLNO like '%%"+ req.getRequest().getKeyTxt() +"%%' " +
                    " or b.pluno like '%%"+ req.getRequest().getKeyTxt() +"%%' " +
                    " or g.plu_name like '%%"+ req.getRequest().getKeyTxt() +"%%'  )");
        }

        sqlbuf.append("order by b.objectId asc, a.billno asc,b.item asc ");

        sqlbuf.append(") a where rn > " + startRow + " AND rn <= " + (startRow+req.getPageSize()) );

        sql = sqlbuf.toString();

        return sql;

    }

    protected String getString(String[] str){
        String str2 = "";
        if (str!=null&&str.length>0)
        {
            for (String s:str){
                str2 = str2 + s + "','";
            }
        }
        if (str2.length()>0){
            str2=str2.substring(0,str2.length()-3);
        }
        return str2;
    }




}
