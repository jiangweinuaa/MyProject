package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_VendorAdjCreateReq;
import com.dsc.spos.json.cust.res.DCP_VendorAdjCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_VendorAdjCreate extends SPosAdvanceService<DCP_VendorAdjCreateReq, DCP_VendorAdjCreateRes>
{


    @Override
    protected void processDUID(DCP_VendorAdjCreateReq req, DCP_VendorAdjCreateRes res) throws Exception
    {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        //新增单据日期不得小于发货组织对应法人账套的关账日期
        String accountSettingSql="select * from DCP_ACOUNT_SETTING a where a.eid='"+req.geteId()+"' " +
                " and a.corp='"+req.getRequest().getOrganizationNo()+"' and to_char(a.APCLOSINGDATE,'yyyyMMdd')>'"+createDate+"' ";
        List<Map<String, Object>> accountSettingList = this.doQueryData(accountSettingSql,null);
        if(accountSettingList.size()>0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "新增单据日期不得小于发货组织对应法人账套的关账日期!");
        }

        String oType = req.getRequest().getOtype();//1.收货入库 sstockin  2.退厂单 sstockout
        String sStockInNo = req.getRequest().getSStockInNo();
        String mainSql="";
        String detailSql="";
        List<Map<String, Object>> mainList =new ArrayList<>();
        List<Map<String, Object>> detailList = new ArrayList<>();
        List<DCP_VendorAdjCreateReq.AdjList> adjList = req.getRequest().getAdjList();

        if("1".equals(oType)){
            mainSql="select * from dcp_sstockin a where" +
                    " a.eid='"+req.geteId()+"' " +
                    " and a.sstockinno='"+sStockInNo+"' " +
                    " and a.organizationno='"+req.getRequest().getOrganizationNo()+"'" +
                    " and a.status='2' ";

            detailSql="select a.*,b.incltax,b.amt,b.taxamt,b.pretaxamt " +//,b.incltax,b.amt,b.taxamt,b.pretaxamt
                    " from dcp_sstockin_detail a " +
                    " left join DCP_TRANSACTION b on a.eid=b.eid and a.organizationno=b.organizationno and a.sstockinno=b.billno and a.item=b.item " +
                    " where  " +
                    " a.eid='"+req.geteId()+"' " +
                    " and a.sstockinno='"+sStockInNo+"' " +
                    " and a.organizationno='"+req.getRequest().getOrganizationNo()+"' ";
            mainList = this.doQueryData(mainSql, null);
            detailList = this.doQueryData(detailSql, null);
        }
        else if("2".equals(oType)){
            mainSql="select a.* " +
                    " from dcp_sstockout a where a.eid='"+req.geteId()+"' " +
                    " and a.sstockoutno='"+sStockInNo+"' " +
                    " and a.organizationno='"+req.getRequest().getOrganizationNo()+"'" +
                    " and a.status='2' ";

            detailSql="select a.*,b.incltax,b.amt,b.taxamt,b.pretaxamt " +
                    " from dcp_sstockout_detail a " +
                    " left join DCP_TRANSACTION b on a.eid=b.eid and a.organizationno=b.organizationno and a.sstockoutno=b.billno and a.item=b.item " +
                    " where  " +
                    " a.eid='"+req.geteId()+"' " +
                    " and a.sstockoutno='"+sStockInNo+"' " +
                    " and a.organizationno='"+req.getRequest().getOrganizationNo()+"' ";
            mainList = this.doQueryData(mainSql, null);
            detailList = this.doQueryData(detailSql, null);
        }
        else if("0".equals(oType)){
            List<BigDecimal> amtColl = adjList.stream().map(x -> {
                String amt = x.getAmt();
                if (Check.Null(amt)) {
                    return new BigDecimal(0);
                }
                return new BigDecimal(amt);
            }).collect(Collectors.toList());
            BigDecimal totAmt = amtColl.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            if(totAmt.compareTo(BigDecimal.ZERO)<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "调整金额不能小于0！");
            }
        }

        //if(mainList.size()<=0){
        //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "来源单据不存在！");
        //}

        String adjustNo=this.getOrderNO(req,"GYTZ");
        BigDecimal totAmt=new BigDecimal(0);

        for(DCP_VendorAdjCreateReq.AdjList adj:adjList){
            if(Check.Null(adj.getAdjAmt())){
                continue;
            }
            BigDecimal amtDecimal = new BigDecimal(adj.getAdjAmt());
            //if(amtDecimal.compareTo(BigDecimal.ZERO)<=0){
            //    continue;
            //}
            List<Map<String, Object>> filterList = detailList.stream().filter(x -> x.get("ITEM").toString().equals(adj.getItem())).collect(Collectors.toList());

            if(filterList.size()>0){
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(req.geteId()));
                detailColumns.add("ADJUSTNO", DataValues.newString(adjustNo));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));

                detailColumns.add("SSTOCKINNO", DataValues.newString(sStockInNo));
                detailColumns.add("ITEM", DataValues.newString(adj.getItem()));
                detailColumns.add("PQTY", DataValues.newString(filterList.get(0).get("PQTY").toString()));
                detailColumns.add("PUNIT", DataValues.newString(filterList.get(0).get("PUNIT").toString()));
                detailColumns.add("PROC_RATE", DataValues.newString(adj.getProc_Rate()));
                detailColumns.add("BASEUNIT", DataValues.newString(filterList.get(0).get("BASEUNIT").toString()));
                detailColumns.add("BASEQTY", DataValues.newString(filterList.get(0).get("BASEQTY").toString()));
                detailColumns.add("UNIT_RATIO", DataValues.newString(filterList.get(0).get("UNIT_RATIO").toString()));
                detailColumns.add("POQTY", DataValues.newString(adj.getPoQty()));
                detailColumns.add("STOCKIN_QTY", DataValues.newString(adj.getStockIn_Qty()));
                detailColumns.add("RETWQTY", DataValues.newString(adj.getRetwQty()));
                detailColumns.add("PURORDERNO", DataValues.newString(filterList.get(0).get("OOFNO").toString()));
                detailColumns.add("POITEM", DataValues.newString(filterList.get(0).get("OOITEM").toString()));
                detailColumns.add("PLUNO", DataValues.newString(filterList.get(0).get("PLUNO").toString()));
                detailColumns.add("FEATURENO", DataValues.newString(filterList.get(0).get("FEATURENO").toString()));
                detailColumns.add("LOCATION", DataValues.newString(filterList.get(0).get("LOCATION").toString()));
                detailColumns.add("BATCH_NO", DataValues.newString(filterList.get(0).get("BATCH_NO").toString()));
                detailColumns.add("DISTRIPRICE", DataValues.newString(filterList.get(0).get("DISTRIPRICE").toString()));
                detailColumns.add("DISTRIAMT", DataValues.newString(filterList.get(0).get("DISTRIAMT").toString()));
                detailColumns.add("TAXCODE", DataValues.newString(filterList.get(0).get("TAXCODE").toString()));
                detailColumns.add("TAXRATE", DataValues.newString(filterList.get(0).get("TAXRATE").toString()));
                detailColumns.add("INCLTAX", DataValues.newString(filterList.get(0).get("INCLTAX").toString()));
                detailColumns.add("AMT", DataValues.newString(filterList.get(0).get("AMT").toString()));
                detailColumns.add("PRETAXAMT", DataValues.newString(filterList.get(0).get("PRETAXAMT").toString()));
                detailColumns.add("TAXAMT", DataValues.newString(filterList.get(0).get("TAXAMT").toString()));

                detailColumns.add("ADJTAXAMT", DataValues.newString(adj.getAdjTaxAmt()));
                detailColumns.add("ADJAMTPRETAX", DataValues.newString(adj.getAdjAmtPreTax()));
                detailColumns.add("ADJAMT", DataValues.newString(adj.getAdjAmt()));

                detailColumns.add("ADJPRICE", DataValues.newString(adj.getAdjPrice()));
                detailColumns.add("ADJTAXAMTED", DataValues.newString(adj.getAdjTaxAmt()));
                detailColumns.add("ADJAMTPRETAXED", DataValues.newString(adj.getAdjAmtPreTaxed()));
                detailColumns.add("ADJAMTTAXED", DataValues.newString(adj.getAdjAmtTaxed()));
                detailColumns.add("MEMO", DataValues.newString(adj.getMemo()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailIb=new InsBean("DCP_VENDORADJ_DETAIL",detailColumnNames);
                detailIb.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailIb));
            }
            else{
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(req.geteId()));
                detailColumns.add("ADJUSTNO", DataValues.newString(adjustNo));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));

                detailColumns.add("SSTOCKINNO", DataValues.newString(sStockInNo));
                detailColumns.add("ITEM", DataValues.newString(adj.getItem()));
                detailColumns.add("PQTY", DataValues.newString(adj.getPqty()));
                detailColumns.add("PUNIT", DataValues.newString(adj.getPunit()));
                detailColumns.add("PROC_RATE", DataValues.newString(adj.getProc_Rate()));
                detailColumns.add("BASEUNIT", DataValues.newString(adj.getBaseUnit()));
                detailColumns.add("BASEQTY", DataValues.newString(adj.getBaseQty()));
                detailColumns.add("UNIT_RATIO", DataValues.newString(adj.getUnit_Ratio()));
                detailColumns.add("POQTY", DataValues.newString(adj.getPoQty()));
                detailColumns.add("STOCKIN_QTY", DataValues.newString(adj.getStockIn_Qty()));
                detailColumns.add("RETWQTY", DataValues.newString(adj.getRetwQty()));
                detailColumns.add("PURORDERNO", DataValues.newString(adj.getPurOrderNo()));
                detailColumns.add("POITEM", DataValues.newString(adj.getPoItem()));
                detailColumns.add("PLUNO", DataValues.newString(adj.getPluNo()));
                detailColumns.add("FEATURENO", DataValues.newString(adj.getFeatureNo()));
                detailColumns.add("LOCATION", DataValues.newString(adj.getLocation()));
                detailColumns.add("BATCH_NO", DataValues.newString(adj.getBatch_No()));
                detailColumns.add("DISTRIPRICE", DataValues.newString(adj.getDistriPrice()));
                detailColumns.add("DISTRIAMT", DataValues.newString(adj.getDistriAmt()));
                detailColumns.add("TAXCODE", DataValues.newString(adj.getTaxCode()));
                detailColumns.add("TAXRATE", DataValues.newString(adj.getTaxRate()));
                detailColumns.add("INCLTAX", DataValues.newString(adj.getInclTax()));
                detailColumns.add("AMT", DataValues.newString(adj.getAmt()));
                detailColumns.add("PRETAXAMT", DataValues.newString(adj.getPreTaxAmt()));
                detailColumns.add("TAXAMT", DataValues.newString(adj.getTaxAmt()));


                detailColumns.add("ADJTAXAMT", DataValues.newString(adj.getAdjTaxAmt()));
                detailColumns.add("ADJAMTPRETAX", DataValues.newString(adj.getAdjAmtPreTax()));
                detailColumns.add("ADJAMT", DataValues.newString(adj.getAdjAmt()));

                detailColumns.add("ADJPRICE", DataValues.newString(adj.getAdjPrice()));
                detailColumns.add("ADJTAXAMTED", DataValues.newString(adj.getAdjTaxAmted()));
                detailColumns.add("ADJAMTPRETAXED", DataValues.newString(adj.getAdjAmtPreTaxed()));
                detailColumns.add("ADJAMTTAXED", DataValues.newString(adj.getAdjAmtTaxed()));
                detailColumns.add("MEMO", DataValues.newString(adj.getMemo()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailIb=new InsBean("DCP_VENDORADJ_DETAIL",detailColumnNames);
                detailIb.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailIb));
            }

            totAmt=totAmt.add(amtDecimal);


        }

        String supplierNo = req.getRequest().getSupplierNo();
        String payDateNo = req.getRequest().getPayDateNo();
        String billDateNo = req.getRequest().getBillDateNo();
        String taxCode = req.getRequest().getTaxCode();
        if(mainList.size()>0){
             supplierNo = mainList.get(0).get("SUPPLIER").toString();
             payDateNo = mainList.get(0).get("PAYDATENO").toString();
             billDateNo = mainList.get(0).get("BILLDATENO").toString();
             taxCode = mainList.get(0).get("TAXCODE").toString();
        }

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(req.geteId()));
        mainColumns.add("STATUS", DataValues.newString(req.getRequest().getStatus()));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
        mainColumns.add("ADJUSTNO", DataValues.newString(adjustNo));
        mainColumns.add("BDATE", DataValues.newString(createDate));
        mainColumns.add("OTYPE", DataValues.newString(oType));
        mainColumns.add("SSTOCKINNO", DataValues.newString(sStockInNo));
        mainColumns.add("SUPPLIER", DataValues.newString(supplierNo));
        mainColumns.add("PAYDATENO", DataValues.newString(payDateNo));
        mainColumns.add("BILLDATENO", DataValues.newString(billDateNo));
        mainColumns.add("TAXCODE", DataValues.newString(taxCode));
        mainColumns.add("TAXRATE", DataValues.newString(req.getRequest().getTaxRate()));
        mainColumns.add("CURRENCY", DataValues.newString(req.getRequest().getCurrency()));
        mainColumns.add("CURRENCYNAME", DataValues.newString(req.getRequest().getCurrencyName()));
        mainColumns.add("EXRATE", DataValues.newString(req.getRequest().getExRate()));
        mainColumns.add("MEMO", DataValues.newString(req.getRequest().getMeno()));
        mainColumns.add("TOT_AMT", DataValues.newString(totAmt.toString()));

        mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
        mainColumns.add("CREATE_TIME", DataValues.newString(createTime));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_VENDORADJ",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        this.doExecuteDataToDB();

        res.setAdjustNo(adjustNo);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_VendorAdjCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_VendorAdjCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_VendorAdjCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_VendorAdjCreateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_VendorAdjCreateReq> getRequestType()
    {
        return new TypeToken<DCP_VendorAdjCreateReq>(){};
    }

    @Override
    protected DCP_VendorAdjCreateRes getResponseType()
    {
        return new DCP_VendorAdjCreateRes();
    }
}
