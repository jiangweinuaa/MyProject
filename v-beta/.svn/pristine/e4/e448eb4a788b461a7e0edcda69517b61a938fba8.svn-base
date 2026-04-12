package com.dsc.spos.scheduler.job;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SStockOutCreateReq;
import com.dsc.spos.json.cust.res.DCP_SStockOutCreateRes;
import com.dsc.spos.redis.DocSubmitStop;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.batchLocation.WarehouseLocationPlu;
import com.dsc.spos.utils.tax.TaxAmount2;
import com.dsc.spos.utils.tax.TaxAmountCalculation;
import com.dsc.spos.utils.transitStock.TransitStockAdjust;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MES_StockoutAutoProcess  extends InitJob
{
    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    List<String> pBillNoList=new ArrayList<>();

    Logger logger = LogManager.getLogger(MES_StockoutAutoProcess.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public MES_StockoutAutoProcess()
    {

    }

    public MES_StockoutAutoProcess(String eId,String shopId,String organizationNO, String billNo,List<String> billNoList)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
        pBillNoList=billNoList;
    }


    public String doExe()
    {
        //返回信息
        String sReturnInfo="";
        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.info("\r\n*********分拣确认正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-分拣确认正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r\n*********分拣确认定时调用Start:************\r\n");

        try
        {

            String bDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
            String stime =	new SimpleDateFormat("HHmmss").format(new Date());
            String uptime =	new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

            String sql="";
            StringBuffer sqlbuf = new StringBuffer("");
            if(pBillNoList.size()<=0) {
                sqlbuf.append("select * from ( select row_number() over(order by a.CREATETIME) rn,b.warehouseno as warehouse, a.* " +
                        " from MES_STOCKOUT A " +
                        " INNER JOIN MES_SORTINGTASK B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND A.TASKNO=B.TASKNO " +
                        " where A.STATUS='1' and NVL(A.ISDOWN,'N')='N' )" +
                        " where  rn>0 and rn<=100  ");
            }else{
                List<String> collect = pBillNoList.stream().map(x -> "'" + x + "'").collect(Collectors.toList());
                sqlbuf.append(" select row_number() over(order by a.CREATETIME) rn,b.warehouseno as warehouse, a.* " +
                        " from MES_STOCKOUT A " +
                        " INNER JOIN MES_SORTINGTASK B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND A.TASKNO=B.TASKNO " +
                        " where 1=1  and A.STATUS='1' and NVL(A.ISDOWN,'N')='N' " +
                        " and a.eid='"+pEId+"' and a.organizationno='"+pOrganizationNO+"' " +
                        " and A.stockoutno in (" + String.join(",", collect) + " )" +
                        "   ");
            }

            sql = sqlbuf.toString();

            List<Map<String, Object>> stockoutList=this.doQueryData(sql, null);
            if (CollUtil.isNotEmpty(stockoutList))
            {
                //一张发货单报错不能影响其他的发货单下发  成功的该状态  不成功的不管
                List<String> noList = stockoutList.stream().map(x -> "'" + x.get("STOCKOUTNO").toString() + "'").collect(Collectors.toList());
                String noStr = String.join(",", noList);

                String mesStockDSql="select a.*,to_char(a.PRODUCTDATE,'yyyyMMdd') as proddate,b.shareqty,b.sourceno,b.oitem,d.ofno as sofno,c.SOURCENO as sSOURCENO,c.oitem as soitem,e.TAXCODE,e.INCLTAX,e.TAXCALTYPE,f.CURRENCY " +
                        " from MES_STOCKOUT_detail a" +
                        " inner join MES_STOCKOUT_SHARE b on a.eid=b.eid and a.stockoutno=b.stockoutno and a.item=b.initem " +
                        " LEFT JOIN MES_SORTDATADETAIL C on c.eid=b.eid and c.organizationno=a.organizationno and c.docno=b.sourceno and b.oitem=c.ERPITEM " +
                        " left join MES_SORTINGDATA d on d.eid=a.eid and d.organizationno=c.organizationno and d.docno=c.docno " +
                        " left join DCP_STOCKOUTNOTICE_DETAIL e on e.eid=a.eid and e.billno=d.ofno and e.item=b.oitem " +
                        " left join dcp_stockoutnotice f on f.eid=e.eid and f.billno=e.billno " +
                        " where 1=1 " +
                        " and a.STOCKOUTNO in (" + noStr + ")";
                List<Map<String, Object>> mesStockoutDetailData = this.doQueryData(mesStockDSql, null);

                for (Map<String, Object> oneData : stockoutList)
                {
                    List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                    String eid = oneData.get("EID").toString();
                    String stockoutNo = oneData.get("STOCKOUTNO").toString();
                    String organizationNo = oneData.get("ORGANIZATIONNO").toString();
                    String warehouseNo = oneData.get("WAREHOUSE").toString();
                    String confirmOpId = oneData.get("CONFIRMOPID").toString();
                    String employeeNo="";
                    String departmentNo="";
                    if(Check.NotNull(confirmOpId)){
                        String opSql="select * from PLATFORM_STAFFS where eid='"+eid+"' and OPNO='"+confirmOpId+"' ";
                        List<Map<String, Object>> opData = this.doQueryData( opSql, null);
                        if(opData.size()>0){
                            employeeNo=opData.get(0).get("EMPLOYEENO").toString();
                            departmentNo=opData.get(0).get("DEPARTNO").toString();
                        }
                    }

                    String invWarehouse="";
                    String orgSql="select * from dcp_org where eid='"+eid+"' and organizationno='"+organizationNo+"' ";
                    List<Map<String, Object>> orgData = this.doQueryData( orgSql, null);
                    if(orgData.size()>0){
                        invWarehouse=orgData.get(0).get("INV_COST_WAREHOUSE").toString();
                    }

                    List<Map<String, Object>> stockOutInfos = stockoutList
                            .stream().filter(x -> x.get("STOCKOUTNO").toString().equals(stockoutNo)
                            &&x.get("EID").toString().equals(eid)&&x.get("ORGANIZATIONNO").toString().equals(organizationNo))
                            .collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(stockOutInfos)){
                        Map<String, Object> stockOutInfo = stockOutInfos.get(0);
                        List<Map<String, Object>> stockOutDetails = mesStockoutDetailData.stream().filter(x -> x.get("STOCKOUTNO").toString().equals(stockoutNo)).collect(Collectors.toList());
                        String department = stockOutInfo.get("DEPARTMENT").toString();
                        String stockoutBDate = stockOutInfo.get("BDATE").toString();
                        String requireNo = stockOutInfo.get("REQUIRENO").toString();

                        String stockoutMemo = stockOutInfo.get("MEMO").toString();
                        List<String> dStockoutNoList=new ArrayList<String>();
                        List<String> dsStockoutNoList=new ArrayList<String>();
                        String receivingNo="";
                        if("0".equals(department)){
                            if(!organizationNo.equals(requireNo)&&Check.NotNull(invWarehouse)){
                                receivingNo=PosPub.getBillNo(StaticInfo.dao,eid,organizationNo,"SHTZ");
                            }

                            String billNo= PosPub.getBillNo(StaticInfo.dao,eid,organizationNo,"PSCK");
                            dStockoutNoList.add(billNo);
                            String[] columnsName_DCP_STOCKOUT_DETAIL = {
                                    "EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO",
                                    "ITEM","BASEQTY","OITEM","PUNIT","WAREHOUSE","PLUNO","BATCH_NO","PROD_DATE","PRICE"
                                    ,"DISTRIPRICE","UNIT_RATIO","PQTY","PLU_BARCODE","BASEUNIT","AMT","RQTY","BSNO"
                                    ,"PLU_MEMO","DISTRIAMT","BDATE","FEATURENO","STOCKQTY","PARTITION_DATE","MES_LOCATION","POQTY","OTYPE","OFNO","OOTYPE","OOFNO","OOITEM"
                            };
                            int detailitem=0;
                            BigDecimal totQty=new BigDecimal(0);
                            String sOfno="";
                            String sSourceNo="";
                            for(Map<String, Object> stockOutDetail : stockOutDetails){
                                BigDecimal shareQty = new BigDecimal(stockOutDetail.get("SHAREQTY").toString());
                                Map<String, Object> map =PosPub.getBaseQty(StaticInfo.dao,eid,stockOutDetail.get("PLUNO").toString(),stockOutDetail.get("UNIT").toString(),shareQty.toString());
                                String baseUnit=map.get("baseUnit").toString();
                                String baseQty=map.get("baseQty").toString();//用这个基准数量去扣
                                String unitRatio=map.get("unitRatio").toString();
                                sOfno=stockOutDetail.get("SOFNO").toString();
                                sSourceNo=stockOutDetail.get("SSOURCENO").toString();
                                String oitem = stockOutDetail.get("OITEM").toString();
                                String soitem = stockOutDetail.get("SOITEM").toString();
                                BigDecimal dPrice = new BigDecimal(stockOutDetail.get("DPRICE").toString());
                                BigDecimal dAmt = dPrice.multiply(shareQty);

                                totQty=totQty.add(shareQty);
                                detailitem++;
                                DataValue[] insValue_DCP_STOCKOUT_DETAIL = new DataValue[]
                                        {
                                                new DataValue(eid, Types.VARCHAR),
                                                new DataValue(organizationNo, Types.VARCHAR),
                                                new DataValue(organizationNo, Types.VARCHAR),
                                                new DataValue(billNo, Types.VARCHAR),
                                                new DataValue(detailitem, Types.VARCHAR),//ITEM
                                                new DataValue(baseQty, Types.VARCHAR),//BASEQTY
                                                new DataValue(oitem, Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("UNIT").toString(), Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("TRANSFEROUT").toString(), Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("PLUNO").toString(), Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("BATCHNO").toString(), Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("PRODDATE").toString(), Types.VARCHAR),
                                                new DataValue(0, Types.FLOAT),
                                                new DataValue(dPrice.toString(), Types.FLOAT),
                                                new DataValue(unitRatio, Types.VARCHAR),//UNIT_RATIO
                                                new DataValue(shareQty, Types.VARCHAR),
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue(baseUnit, Types.VARCHAR),//BASEUNIT
                                                new DataValue("0", Types.FLOAT),//AMT
                                                new DataValue(0, Types.FLOAT),//RQTY
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue("", Types.VARCHAR),//PLU_MEMO
                                                new DataValue(dAmt, Types.FLOAT),//DISTRIAMT
                                                new DataValue(stockoutBDate, Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("FEATURENO").toString(), Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),//STOCKQTY
                                                new DataValue(stockoutBDate, Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("LOCATION").toString(), Types.VARCHAR),
                                                new DataValue(shareQty, Types.VARCHAR),//POQTY
                                                new DataValue("3", Types.VARCHAR),
                                                new DataValue(sOfno, Types.VARCHAR),
                                                new DataValue("3", Types.VARCHAR),
                                                new DataValue(sSourceNo, Types.VARCHAR),
                                                new DataValue(soitem, Types.VARCHAR),//ooitem
                                        };
                                InsBean ib_DCP_STOCKOUT_DETAIL = new InsBean("DCP_STOCKOUT_DETAIL", columnsName_DCP_STOCKOUT_DETAIL);
                                ib_DCP_STOCKOUT_DETAIL.addValues(insValue_DCP_STOCKOUT_DETAIL);
                                lstData.add(new DataProcessBean(ib_DCP_STOCKOUT_DETAIL));

                                ColumnDataValue batchColumns = new ColumnDataValue();
                                batchColumns.add("EID", eid, Types.VARCHAR);
                                batchColumns.add("ORGANIZATIONNO",organizationNo, Types.VARCHAR);
                                batchColumns.add("SHOPID",organizationNo, Types.VARCHAR);
                                batchColumns.add("STOCKOUTNO", billNo, Types.VARCHAR);
                                batchColumns.add("ITEM",detailitem, Types.VARCHAR);
                                batchColumns.add("ITEM2",detailitem, Types.VARCHAR);
                                batchColumns.add("PLUNO", stockOutDetail.get("PLUNO").toString(), Types.VARCHAR);
                                batchColumns.add("FEATURENO", stockOutDetail.get("FEATURENO").toString(), Types.VARCHAR);
                                batchColumns.add("WAREHOUSE",stockOutDetail.get("TRANSFEROUT").toString(), Types.VARCHAR);
                                batchColumns.add("LOCATION",stockOutDetail.get("LOCATION").toString(), Types.VARCHAR);
                                batchColumns.add("BATCHNO",stockOutDetail.get("BATCHNO").toString(), Types.VARCHAR);
                                batchColumns.add("PRODDATE", stockOutDetail.get("PRODDATE").toString(), Types.VARCHAR);
                                batchColumns.add("EXPDATE",stockOutDetail.get("LOSEDATE").toString(), Types.VARCHAR);
                                batchColumns.add("PUNIT",stockOutDetail.get("UNIT").toString(), Types.VARCHAR);
                                batchColumns.add("PQTY", shareQty, Types.VARCHAR);
                                batchColumns.add("BASEUNIT",baseUnit, Types.VARCHAR);
                                batchColumns.add("BASEQTY",baseQty, Types.VARCHAR);
                                batchColumns.add("UNITRATIO",unitRatio, Types.VARCHAR);

                                String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                                DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibb = new InsBean("DCP_STOCKOUT_BATCH", batchColumnNames);
                                ibb.addValues(batchDataValues);
                                lstData.add(new DataProcessBean(ibb));
                            }

                            List<Map> pfs = stockOutDetails.stream().map(x -> {
                                Map map = new HashMap();

                                map.put("PLUNO", x.get("PLUNO").toString());
                                map.put("FEATURENO", x.get("FEATURENO").toString());

                                return map;
                            }).distinct().collect(Collectors.toList());


                            String[] columnsName_DCP_STOCKOUT = {
                                    "EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO",
                                    "BDATE","TRANSFER_SHOP","CONFIRM_DATE","CONFIRMBY","MODIFY_DATE","TOT_AMT","CREATE_TIME"
                                    ,"MODIFY_TIME","CANCEL_DATE","CREATE_DATE","DOC_TYPE","OFNO","CREATEBY","BSNO","ACCOUNTBY",
                                    "DELIVERY_NO","TRANSFER_WAREHOUSE","TOT_PQTY","TOT_DISTRIAMT","OTYPE","CANCELBY","CANCEL_TIME"
                                    ,"PROCESS_STATUS","WAREHOUSE","TOT_CQTY","ACCOUNT_TIME","RECEIPT_ORG","LOAD_DOCNO","MEMO"
                                    ,"CONFIRM_TIME","LOAD_DOCTYPE","MODIFYBY","ACCOUNT_DATE","UPDATE_TIME","SUBMITBY","SUBMIT_DATE"
                                    ,"SUBMIT_TIME","PROCESS_ERP_NO","PROCESS_ERP_ORG","LOAD_DOCSHOP","PTEMPLATENO","STATUS","SOURCEMENU"
                                    ,"CREATE_CHATUSERID","MODIFY_CHATUSERID","ACCOUNT_CHATUSERID","PARTITION_DATE","OOTYPE","OOFNO","INVWAREHOUSE","EMPLOYEEID","DEPARTID"
                            };

                            DataValue[] insValue_DCP_STOCKOUT = new DataValue[]
                                    {
                                            new DataValue(eid, Types.VARCHAR),
                                            new DataValue(organizationNo, Types.VARCHAR),
                                            new DataValue(organizationNo, Types.VARCHAR),
                                            new DataValue(billNo, Types.VARCHAR),
                                            new DataValue(stockoutBDate, Types.VARCHAR),
                                            new DataValue(requireNo, Types.VARCHAR),
                                            new DataValue(bDate, Types.VARCHAR),
                                            new DataValue(confirmOpId, Types.VARCHAR),
                                            new DataValue(bDate, Types.VARCHAR),
                                            new DataValue(0, Types.FLOAT),
                                            new DataValue(stime, Types.VARCHAR),
                                            new DataValue(stime, Types.VARCHAR),
                                            new DataValue(bDate, Types.VARCHAR),
                                            new DataValue(bDate, Types.VARCHAR),
                                            new DataValue("5", Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),//ofno
                                            new DataValue(confirmOpId, Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue(confirmOpId, Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue(totQty, Types.VARCHAR),//TOT_PQTY
                                            new DataValue(0, Types.VARCHAR),
                                            new DataValue("3", Types.VARCHAR),//otype
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue("N", Types.VARCHAR),
                                            new DataValue(warehouseNo, Types.VARCHAR),
                                            new DataValue(pfs.size(), Types.VARCHAR),//TOT_CQTY
                                            new DataValue(stime, Types.VARCHAR),
                                            new DataValue(requireNo, Types.VARCHAR),
                                            new DataValue(stockoutNo, Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue(stime, Types.VARCHAR),
                                            new DataValue("0", Types.VARCHAR),
                                            new DataValue(confirmOpId, Types.VARCHAR),
                                            new DataValue(bDate, Types.VARCHAR),
                                            new DataValue(uptime, Types.VARCHAR),
                                            new DataValue(confirmOpId, Types.VARCHAR),
                                            new DataValue(bDate, Types.VARCHAR),
                                            new DataValue(stime, Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue(organizationNo, Types.VARCHAR),
                                            new DataValue(organizationNo, Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue(0,Types.INTEGER),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue(bDate, Types.VARCHAR),
                                            new DataValue("3", Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue(invWarehouse, Types.VARCHAR),
                                            new DataValue(employeeNo, Types.VARCHAR),
                                            new DataValue(departmentNo, Types.VARCHAR),

                                    };
                            InsBean ib_DCP_STOCKOUT = new InsBean("DCP_STOCKOUT", columnsName_DCP_STOCKOUT);
                            ib_DCP_STOCKOUT.addValues(insValue_DCP_STOCKOUT);
                            lstData.add(new DataProcessBean(ib_DCP_STOCKOUT));

                        }
                        if("1".equals(department)){
                            // 如果MES_STOCKOUT表DEPARTMENT为1，则生成DCP_SSTOCKOUT表STOCKOUTTYPE为2数据
                            String billNo= PosPub.getBillNo(StaticInfo.dao,eid,organizationNo,"XHCK");
                            dsStockoutNoList.add(billNo);
                            String accountDate = PosPub.getAccountDate_SMS(StaticInfo.dao, eid, organizationNo);
                            String[] columnsDetail = {
                                    "SSTOCKOUTNO","SHOPID","ITEM","OITEM","PLUNO",
                                    "PUNIT","PQTY","BASEUNIT","BASEQTY","UNIT_RATIO",
                                    "PRICE","AMT","EID","ORGANIZATIONNO","WAREHOUSE",
                                    "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO","OTYPE","OFNO","OOTYPE","OOFNO","OOITEM","TAXRATE","TAXCODE","INCLTAX","TAXCALTYPE"
                            };

                            String currency=stockOutDetails.get(0).get("CURRENCY").toString();
                            int amountDigit=2;
                            if(Check.NotNull(currency)) {
                                String currencySql = "select * from DCP_CURRENCY where eid='" + eid + "' and" +
                                        " CURRENCY='" + currency + "' ";
                                List<Map<String, Object>> currencies = this.doQueryData(currencySql, null);
                                if (currencies.size() <= 0) {

                                } else {
                                    amountDigit = Integer.parseInt(currencies.get(0).get("AMOUNTDIGIT").toString());
                                }
                            }

                            int detailitem=0;
                            BigDecimal totQty=new BigDecimal(0);


                            BigDecimal totDistriAmt=new BigDecimal(0);
                            BigDecimal totDistriTaxAmt=new BigDecimal(0);
                            BigDecimal totDistriPreTaxAmt=new BigDecimal(0);
                            BigDecimal totAmt=new BigDecimal(0);

                            for(Map<String, Object> stockOutDetail : stockOutDetails){
                                BigDecimal shareQty = new BigDecimal(stockOutDetail.get("SHAREQTY").toString());
                                Map<String, Object> map =PosPub.getBaseQty(StaticInfo.dao,eid,stockOutDetail.get("PLUNO").toString(),stockOutDetail.get("UNIT").toString(),shareQty.toString());
                                String baseUnit=map.get("baseUnit").toString();
                                String baseQty=map.get("baseQty").toString();//用这个基准数量去扣
                                String unitRatio=map.get("unitRatio").toString();
                                String sOfno=stockOutDetail.get("SOFNO").toString();
                                String sSourceNo=stockOutDetail.get("SSOURCENO").toString();
                                String oitem = stockOutDetail.get("OITEM").toString();
                                String soitem = stockOutDetail.get("SOITEM").toString();
                                BigDecimal dPrice = new BigDecimal(stockOutDetail.get("DPRICE").toString());
                                BigDecimal dAmt = dPrice.multiply(shareQty);
                                String taxRate = stockOutDetail.get("TAXRATE").toString();
                                String taxCode = stockOutDetail.get("TAXCODE").toString();
                                String inclTax = stockOutDetail.get("INCLTAX").toString();
                                String taxCaltype = stockOutDetail.get("TAXCALTYPE").toString();

                                totQty=totQty.add(shareQty);
                                detailitem++;
                                DataValue[] insValue_DCP_SSTOCKOUT_DETAIL = new DataValue[]
                                        {
                                                new DataValue(billNo, Types.VARCHAR),
                                                new DataValue(organizationNo, Types.VARCHAR),
                                                new DataValue(detailitem, Types.VARCHAR),
                                                new DataValue(oitem, Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("PLUNO").toString(), Types.VARCHAR),

                                                new DataValue(stockOutDetail.get("UNIT").toString(), Types.VARCHAR),
                                                new DataValue(shareQty, Types.VARCHAR),
                                                new DataValue(baseUnit, Types.VARCHAR),
                                                new DataValue(baseQty, Types.VARCHAR),
                                                new DataValue(unitRatio, Types.VARCHAR),//UNIT_RATIO
                                                new DataValue(0, Types.FLOAT),
                                                new DataValue(0, Types.FLOAT),
                                                new DataValue(eid, Types.VARCHAR),
                                                new DataValue(organizationNo, Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("TRANSFEROUT").toString(), Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("BATCHNO").toString(), Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("PRODDATE").toString(), Types.VARCHAR),
                                                new DataValue(dPrice.toString(), Types.FLOAT),
                                                new DataValue(dAmt, Types.FLOAT),
                                                new DataValue(stockoutBDate, Types.VARCHAR),
                                                new DataValue(stockOutDetail.get("FEATURENO").toString(), Types.VARCHAR),
                                                new DataValue("1", Types.VARCHAR),
                                                new DataValue(sOfno, Types.VARCHAR),
                                                new DataValue("4", Types.VARCHAR),
                                                new DataValue(sSourceNo, Types.VARCHAR),
                                                new DataValue(soitem, Types.VARCHAR),//ooitem
                                                new DataValue(taxRate, Types.VARCHAR),
                                                new DataValue(taxCode, Types.VARCHAR),
                                                new DataValue(inclTax, Types.VARCHAR),
                                                new DataValue(taxCaltype, Types.VARCHAR),

                                        };
                                InsBean ib_DCP_SSTOCKOUT_DETAIL = new InsBean("DCP_SSTOCKOUT_DETAIL", columnsDetail);
                                ib_DCP_SSTOCKOUT_DETAIL.addValues(insValue_DCP_SSTOCKOUT_DETAIL);
                                lstData.add(new DataProcessBean(ib_DCP_SSTOCKOUT_DETAIL));


                                ColumnDataValue batchColumns = new ColumnDataValue();
                                batchColumns.add("EID", DataValues.newString(eid));
                                batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                                batchColumns.add("SSTOCKOUTNO", DataValues.newString(billNo));
                                batchColumns.add("ITEM", DataValues.newString(detailitem));
                                batchColumns.add("ITEM2", DataValues.newString(detailitem));
                                batchColumns.add("PLUNO", DataValues.newString(stockOutDetail.get("PLUNO").toString()));
                                batchColumns.add("FEATURENO", DataValues.newString(stockOutDetail.get("FEATURENO").toString()));
                                batchColumns.add("WAREHOUSE", DataValues.newString(stockOutDetail.get("TRANSFEROUT").toString()));
                                batchColumns.add("LOCATION", DataValues.newString(stockOutDetail.get("LOCATION").toString()));
                                batchColumns.add("BATCHNO", DataValues.newString(stockOutDetail.get("BATCHNO").toString()));
                                batchColumns.add("PRODDATE", DataValues.newString(stockOutDetail.get("PRODDATE").toString()));
                                batchColumns.add("EXPDATE", DataValues.newString(stockOutDetail.get("LOSEDATE").toString()));
                                batchColumns.add("PUNIT", DataValues.newString(stockOutDetail.get("UNIT").toString()));
                                batchColumns.add("PQTY", DataValues.newString(shareQty));
                                batchColumns.add("BASEUNIT", DataValues.newString(baseUnit));
                                batchColumns.add("BASEQTY", DataValues.newString(baseQty));
                                batchColumns.add("UNITRATIO", DataValues.newString(unitRatio));

                                String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                                DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibb = new InsBean("DCP_SSTOCKOUT_BATCH", batchColumnNames);
                                ibb.addValues(batchDataValues);
                                lstData.add(new DataProcessBean(ibb));
                            }

                            List<Map<String, String>> pfpList = stockOutDetails.stream().map(x -> {
                                Map<String, String> rMap = new HashMap<String, String>();

                                rMap.put("PLUNO", x.get("PLUNO").toString());
                                rMap.put("FEATURENO", x.get("FEATURENO").toString());
                                rMap.put("PUNIT", x.get("UNIT").toString());
                                rMap.put("TAXCODE", x.get("TAXCODE").toString());
                                rMap.put("TAXRATE", x.get("TAXRATE").toString());
                                rMap.put("DISTRIPRICE", x.get("DPRICE").toString());
                                rMap.put("TAXCALTYPE", x.get("TAXCALTYPE").toString());
                                rMap.put("INCLTAX", x.get("INCLTAX").toString());
                                rMap.put("ISGIFT", "0");
                               // rMap.put("CURRENCY",x.get("CURRENCY").toString())

                                return rMap;
                            }).distinct().collect(Collectors.toList());
                            int transDetailItem=0;
                            for (Map<String, String> pfp : pfpList) {
                                //品号特征码单位合计
                                String[] columnsTrans = {
                                        "EID","ORGANIZATIONNO","COMPANY","BTYPE","BILLNO",
                                        "ITEM","PLUNO","FEATURENO","TAXCODE","TAXRATE",
                                        "INCLTAX","PUNIT","PQTY","PRICE","AMT",
                                        "PRETAXAMT","TAXAMT","TAXCALTYPE"
                                };
                                InsBean ibTrans = new InsBean("DCP_TRANSACTION", columnsTrans);


                                List<Map<String, Object>> pfpDetails = stockOutDetails.stream().filter(x -> x.get("PLUNO").toString().equals(pfp.get("PLUNO"))
                                        && x.get("FEATURENO").toString().equals(pfp.get("FEATURENO"))
                                        && x.get("UNIT").toString().equals(pfp.get("PUNIT"))
                                ).collect(Collectors.toList());

                                BigDecimal totPqty=new BigDecimal(0);
                                BigDecimal price=new BigDecimal(pfp.get("DISTRIPRICE"));
                                BigDecimal distriPrice=new BigDecimal(pfp.get("DISTRIPRICE"));


                                BigDecimal taxAmt=BigDecimal.ZERO;
                                BigDecimal preTaxAmt=BigDecimal.ZERO;
                                BigDecimal disTaxAmt=BigDecimal.ZERO;
                                BigDecimal preDisTaxAmt=BigDecimal.ZERO;
                                String taxRate=pfp.get("TAXRATE");
                                String inclTax=pfp.get("INCLTAX");
                                String taxCalType=pfp.get("TAXCALTYPE");
                                for (Map<String, Object> pfpDetail : pfpDetails){
                                    totPqty=totPqty.add(new BigDecimal(pfpDetail.get("SHAREQTY").toString()));
                                }
                                BigDecimal amt=totPqty.multiply(distriPrice).setScale(amountDigit, BigDecimal.ROUND_HALF_UP);
                                BigDecimal distriAmt=totPqty.multiply(distriPrice).setScale(amountDigit, BigDecimal.ROUND_HALF_UP);

                                TaxAmount2 taxAmount2 = TaxAmountCalculation.calculateAmount(inclTax, amt, new BigDecimal(taxRate), taxCalType, 2);
                                taxAmt=taxAmount2.getTaxAmount();
                                preTaxAmt=taxAmount2.getPreAmount();
                                amt=taxAmount2.getAmount();

                                TaxAmount2 taxAmount3 = TaxAmountCalculation.calculateAmount(inclTax, distriAmt, new BigDecimal(taxRate), taxCalType, 2);
                                disTaxAmt=taxAmount3.getTaxAmount();
                                preDisTaxAmt=taxAmount3.getPreAmount();
                                distriAmt=taxAmount3.getAmount();

                                totAmt=totAmt.add(amt);
                                totDistriAmt=totDistriAmt.add(distriAmt);
                                totDistriTaxAmt=totDistriTaxAmt.add(disTaxAmt);
                                totDistriPreTaxAmt=totDistriPreTaxAmt.add(preDisTaxAmt);

                                transDetailItem++;
                                DataValue[] insValueTrans = new DataValue[]{
                                        new DataValue(eid, Types.VARCHAR),
                                        new DataValue(organizationNo, Types.VARCHAR),
                                        new DataValue(organizationNo, Types.VARCHAR),
                                        new DataValue("3", Types.VARCHAR),//1.采购入库 2.采退出库 3.销货出库 4.销退入库
                                        new DataValue(billNo, Types.VARCHAR),
                                        new DataValue(String.valueOf(transDetailItem), Types.VARCHAR),
                                        new DataValue(pfp.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(pfp.get("FEATURENO").toString(), Types.VARCHAR),
                                        new DataValue(pfp.get("TAXCODE").toString(), Types.VARCHAR),
                                        new DataValue(taxRate, Types.VARCHAR),
                                        new DataValue(inclTax, Types.VARCHAR),
                                        new DataValue(pfp.get("PUNIT"), Types.VARCHAR),
                                        new DataValue(totPqty, Types.VARCHAR),
                                        new DataValue(price, Types.VARCHAR),
                                        new DataValue(amt, Types.VARCHAR),
                                        new DataValue(preTaxAmt, Types.VARCHAR),
                                        new DataValue(taxAmt, Types.VARCHAR),
                                        new DataValue(taxCalType, Types.VARCHAR),
                                };

                                ibTrans.addValues(insValueTrans);
                                lstData.add(new DataProcessBean(ibTrans));
                            }


                            List<Map> pfs = stockOutDetails.stream().map(x -> {
                                Map map = new HashMap();

                                map.put("PLUNO", x.get("PLUNO").toString());
                                map.put("FEATURENO", x.get("FEATURENO").toString());

                                return map;
                            }).distinct().collect(Collectors.toList());

                            String[] columns = {
                                    "SHOPID","ORGANIZATIONNO","BDATE","SSTOCKOUT_ID",
                                    "CREATEBY","CREATE_DATE","CREATE_TIME",
                                    "TOT_PQTY","TOT_AMT","TOT_CQTY","EID",
                                    "SSTOCKOUTNO","MEMO","STATUS","SUPPLIER","WAREHOUSE",
                                    "OFNO","TOT_DISTRIAMT","TAXCODE","CREATE_CHATUSERID",
                                    "UPDATE_TIME","TRAN_TIME","LOAD_DOCNO","EMPLOYEEID","DEPARTID","STOCKOUTTYPE","CUSTOMER","TOTDISTRIPRETAXAMT","TOTDISTRITAXAMT"
                            };

                            DataValue[] insValue = new DataValue[]{
                                    new DataValue(organizationNo, Types.VARCHAR),
                                    new DataValue(organizationNo, Types.VARCHAR),
                                    new DataValue(stockoutBDate, Types.VARCHAR),
                                    new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                                    new DataValue(confirmOpId, Types.VARCHAR),
                                    new DataValue(bDate, Types.VARCHAR),
                                    new DataValue(stime, Types.VARCHAR),
                                    new DataValue(totQty, Types.VARCHAR),
                                    new DataValue(totAmt, Types.VARCHAR),
                                    new DataValue(pfs.size(), Types.VARCHAR),
                                    new DataValue(eid, Types.VARCHAR),
                                    new DataValue(billNo, Types.VARCHAR),
                                    new DataValue(stockoutMemo, Types.VARCHAR),
                                    new DataValue("0", Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue(warehouseNo, Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue(totDistriAmt, Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                    new DataValue(stockoutNo, Types.VARCHAR),
                                    new DataValue(employeeNo, Types.VARCHAR),
                                    new DataValue(departmentNo, Types.VARCHAR),
                                    new DataValue("2", Types.VARCHAR),
                                    new DataValue(requireNo, Types.VARCHAR),
                                    new DataValue(totDistriPreTaxAmt, Types.VARCHAR),
                                    new DataValue(totDistriTaxAmt, Types.VARCHAR),

                            };

                            InsBean ib = new InsBean("DCP_SSTOCKOUT", columns);
                            ib.addValues(insValue);
                            lstData.add(new DataProcessBean(ib));

                        }

                        //先生成单据
                        StaticInfo.dao.useTransactionProcessData(lstData);
                        lstData=new ArrayList<>();

                        //生成的单据审核
                        try {
                        String accountDate = PosPub.getAccountDate_SMS(StaticInfo.dao, eid, organizationNo);
                        if(CollUtil.isNotEmpty(dStockoutNoList)){
                            for (String dStockoutNo : dStockoutNoList){

                                //出库逻辑
                                String sBatchSql = "select a.* ,NVL(uc1.UNIT_RATIO,1) UNIT_RATIO,d.price,d.DISTRIPRICE from DCP_STOCKOUT_BATCH a " +
                                        " LEFT JOIN DCP_UNITCONVERT uc1 on uc1.eid=a.eid and uc1.OUNIT=a.PUNIT AND uc1.UNIT=a.BASEUNIT " +
                                        " left join DCP_STOCKOUT_DETAIL d on d.eid=a.eid and d.organizationno=a.organizationno and d.stockoutno=a.stockoutno and d.item=a.item2 " +
                                        " where a.eid='" + eid + "' and a.organizationno='" + organizationNo + "' " +
                                        " and a.stockoutno='" + dStockoutNo + "' ";
                                List<Map<String, Object>> sBatchList = this.doQueryData(sBatchSql, null);
                                for (Map<String, Object> row : sBatchList) {
                                    String thisItem = row.get("ITEM").toString();
                                    String thisItem2 = row.get("ITEM2").toString();
                                    String thisPluNo = row.get("PLUNO").toString();
                                    String thisFeatureNo = row.get("FEATURENO").toString();
                                    String thisWarehouse = row.get("WAREHOUSE").toString();
                                    String thisLocation = row.get("LOCATION").toString();
                                    String thisBatchNo = row.get("BATCHNO").toString();
                                    String thisProdDate = row.get("PRODDATE").toString();
                                    String thisExpDate = row.get("EXPDATE").toString();
                                    String thisPUnit = row.get("PUNIT").toString();
                                    String thisPQty = row.get("PQTY").toString();
                                    String thisBaseUnit = row.get("BASEUNIT").toString();
                                    String thisBaseQty = row.get("BASEQTY").toString();
                                    String thisUnitRatio = row.get("UNIT_RATIO").toString();
                                    double amt = BigDecimalUtils.mul(Double.parseDouble(row.get("PRICE").toString()), Double.parseDouble(row.get("PQTY").toString()), 2);
                                    double distriAmt = BigDecimalUtils.mul(Double.parseDouble(row.get("DISTRIPRICE").toString()), Double.parseDouble(row.get("PQTY").toString()), 2);

                                    String procedure = "SP_DCP_StockChange_v3";
                                    Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                    inputParameter.put(1, eid);                                            //--企业ID
                                    inputParameter.put(2, organizationNo);                                         //--组织
                                    inputParameter.put(3, "41");                                //--单据类型
                                    inputParameter.put(4, dStockoutNo);                                        //--单据号
                                    inputParameter.put(5, thisItem2);
                                    inputParameter.put(6, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                    inputParameter.put(7, stockoutBDate);             //--营业日期 yyyy-MM-dd
                                    inputParameter.put(8, thisPluNo);         //--品号
                                    inputParameter.put(9, thisFeatureNo);                                   //--特征码
                                    inputParameter.put(10, thisWarehouse);    //--仓库
                                    inputParameter.put(11, thisBatchNo);     //批号
                                    inputParameter.put(12, thisLocation);     //--location
                                    inputParameter.put(13, thisPUnit);        //--交易单位
                                    inputParameter.put(14, thisPQty);         //--交易数量
                                    inputParameter.put(15, thisBaseUnit);     //--基准单位
                                    inputParameter.put(16, thisBaseQty);      //--基准数量
                                    inputParameter.put(17, thisUnitRatio);   //--换算比例
                                    inputParameter.put(18, row.get("PRICE").toString());        //--零售价
                                    inputParameter.put(19, amt);          //--零售金额
                                    inputParameter.put(20, row.get("DISTRIPRICE").toString());  //--进货价
                                    inputParameter.put(21, distriAmt);    //--进货金额
                                    inputParameter.put(22, accountDate);                                   //--入账日期 yyyy-MM-dd
                                    inputParameter.put(23, thisProdDate);    //--批号的生产日期 yyyy-MM-dd
                                    inputParameter.put(24, stockoutBDate);            //--单据日期
                                    inputParameter.put(25, "配货出库扣库存");             //--异动原因
                                    inputParameter.put(26, "");             //--异动描述
                                    inputParameter.put(27, "");          //--操作员

                                    ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                    lstData.add(new DataProcessBean(pdb));

                                }

                                //在途仓逻辑
                                List<WarehouseLocationPlu> allocList = Collections.emptyList();
                                List<DataProcessBean> newDatabeans = new ArrayList<>();
                                newDatabeans = new TransitStockAdjust().stockOutWarehouse(eid,
                                        dStockoutNo,
                                        organizationNo,
                                        organizationNo,
                                        confirmOpId,
                                        "",
                                        receivingNo,
                                        allocList
                                );
                                if (!newDatabeans.isEmpty()) {
                                    for (DataProcessBean bean : newDatabeans) {
                                        lstData.add(bean);
                                    }
                                }

                                //更新出货通知单的明细和状态
                                String detailSql = "select distinct a.ofno from dcp_stockout_detail a where a.eid='" + eid + "'" +
                                        " and a.organizationno='" + organizationNo + "' " +
                                        " and a.stockoutno='" + dStockoutNo + "'";
                                List<Map<String, Object>> ofList = this.doQueryData(detailSql, null);
                                for (Map<String, Object> ofData : ofList) {
                                    String ofNoD = ofData.get("OFNO").toString();

                                    String upSql = "select nvl(b.stockoutno,'') as stockoutno, a.billno,a.item,nvl(a.STOCKOUTQTY,0) as STOCKOUTQTY," +
                                            " nvl(c.pqty,0) as pqty,nvl(a.pqty,0) as npqty,a.status,b.status as stockoutstatus " +
                                            " from DCP_STOCKOUTNOTICE_DETAIL a " +
                                            " left join dcp_stockout_detail c on a.eid=c.eid  and a.item=c.oitem and a.billno=c.ofno " +
                                            " left join dcp_stockout b on a.eid=b.eid and c.organizationno=b.organizationno and c.stockoutno=b.stockoutno " +
                                            " where a.eid='" + eid + "'  and a.billno='" + ofNoD + "' ";
                                    List<Map<String, Object>> getUpDetail = this.doQueryData(upSql, null);

                                    String thisStockOutSql = "select a.oitem,a.ofno,sum(a.pqty) pqty from dcp_stockout_detail a where a.stockoutno='" + dStockoutNo + "'" +
                                            " and a.eid='" + eid + "' and a.organizationno='" + organizationNo + "' " +
                                            " group by a.ofno,a.oitem ";
                                    List<Map<String, Object>> thisStockDList = this.doQueryData(thisStockOutSql, null);

                                    List detailStatusList = new ArrayList();
                                    //getUpDetail 这边会重复 没关系  数量重新
                                    for (Map<String, Object> oneData1 : getUpDetail) {
                                        String billNo = oneData1.get("BILLNO").toString();
                                        String nStatus = oneData1.get("STATUS").toString();
                                        String item = oneData1.get("ITEM").toString();
                                        String checkStatus = oneData1.get("STOCKOUTSTATUS").toString();
                                        String nowStockoutNo = oneData1.get("STOCKOUTNO").toString();
                                        BigDecimal stockoutQty = new BigDecimal(oneData1.get("STOCKOUTQTY").toString());
                                        BigDecimal pQty = new BigDecimal(oneData1.get("PQTY").toString());
                                        BigDecimal npQty = new BigDecimal(oneData1.get("NPQTY").toString());
                                        String detailStatus = "";
                                        if (Check.Null(billNo) || Check.Null(item)) {
                                            continue;
                                        }
                                        if ("2".equals(checkStatus)) {
                                            continue;
                                        }
                                        //分段的时候同一条明细有两条出库数据  状态都是4
                                        //需要排除掉已经审核过的出库单
                                        if (!nowStockoutNo.equals(dStockoutNo)) {
                                            //该条不是这次出库单的
                                            if (!detailStatusList.contains(nStatus)) {
                                                detailStatusList.add(nStatus);
                                            }
                                            continue;
                                        }

                                        List<Map<String, Object>> collect = thisStockDList.stream().filter(x -> x.get("OFNO").toString().equals(billNo) && x.get("OITEM").toString().equals(item)).collect(Collectors.toList());
                                        if (collect.size() > 0) {
                                            pQty = new BigDecimal(collect.get(0).get("PQTY").toString());
                                        }

                                        stockoutQty = stockoutQty.add(pQty);
                                        if (stockoutQty.compareTo(npQty) < 0) {
                                            detailStatus = "1";
                                        } else {
                                            detailStatus = "2";
                                        }
                                        if (!detailStatusList.contains(detailStatus)) {
                                            detailStatusList.add(detailStatus);
                                        }

                                        UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE_DETAIL");
                                        upNotice.addUpdateValue("STATUS", new DataValue(Float.parseFloat(detailStatus), Types.VARCHAR));
                                        upNotice.addUpdateValue("STOCKOUTQTY", new DataValue(stockoutQty, Types.VARCHAR));
                                        // condition
                                        //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                        upNotice.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                        upNotice.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                                        upNotice.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                                        //this.addProcessData(new DataProcessBean(upNotice));
                                        lstData.add(new DataProcessBean(upNotice));

                                    }

                                    if (detailStatusList.size() > 1) {

                                        // 存在行状态=【4-出货中】/【3-已派工】，则单据状态=【4-出货中】
                                        if (detailStatusList.contains("3") || detailStatusList.contains("4")) {

                                            UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE");
                                            upNotice.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR));
                                            // condition
                                            //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                            upNotice.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                            upNotice.addCondition("BILLNO", new DataValue(ofNoD, Types.VARCHAR));
                                            lstData.add(new DataProcessBean(upNotice));
                                            //this.addProcessData(new DataProcessBean(upNotice));
                                        } else {

                                            UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE");
                                            upNotice.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
                                            // condition
                                            //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                            upNotice.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                            upNotice.addCondition("BILLNO", new DataValue(ofNoD, Types.VARCHAR));
                                            lstData.add(new DataProcessBean(upNotice));
                                            //this.addProcessData(new DataProcessBean(upNotice));
                                        }

                                    } else {
                                        if (detailStatusList.size() == 1) {
                                            if (detailStatusList.get(0).equals("1")) {
                                                UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE");
                                                upNotice.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
                                                // condition
                                                //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                                upNotice.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                                upNotice.addCondition("BILLNO", new DataValue(ofNoD, Types.VARCHAR));
                                                lstData.add(new DataProcessBean(upNotice));
                                                //this.addProcessData(new DataProcessBean(upNotice));
                                            } else if (detailStatusList.get(0).equals("2")) {
                                                UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE");
                                                upNotice.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                                                // condition
                                                //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                                upNotice.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                                upNotice.addCondition("BILLNO", new DataValue(ofNoD, Types.VARCHAR));
                                                lstData.add(new DataProcessBean(upNotice));
                                                //this.addProcessData(new DataProcessBean(upNotice));
                                            }
                                        }
                                    }

                                }


                                //更新出库单状态
                                UptBean ub1 = new UptBean("DCP_StockOut");
                                ub1.addUpdateValue("Status", new DataValue("2", Types.VARCHAR));
                                ub1.addUpdateValue("ConfirmBy", new DataValue(confirmOpId, Types.VARCHAR));
                                ub1.addUpdateValue("Confirm_Date", new DataValue(bDate, Types.VARCHAR));
                                ub1.addUpdateValue("Confirm_Time", new DataValue(stime, Types.VARCHAR));
                                ub1.addUpdateValue("accountBy", new DataValue(confirmOpId, Types.VARCHAR));
                                ub1.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
                                ub1.addUpdateValue("account_Time", new DataValue(stime, Types.VARCHAR));
                                ub1.addUpdateValue("SUBMITBY", new DataValue(confirmOpId, Types.VARCHAR));
                                ub1.addUpdateValue("SUBMIT_DATE", new DataValue(bDate, Types.VARCHAR));
                                ub1.addUpdateValue("SUBMIT_TIME", new DataValue(stime, Types.VARCHAR));
                                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                                // condition
                                ub1.addCondition("OrganizationNO", new DataValue(organizationNo, Types.VARCHAR));
                                ub1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                ub1.addCondition("StockOutNO", new DataValue(dStockoutNo, Types.VARCHAR));

                                lstData.add(new DataProcessBean(ub1));
                            }
                        }
                        if(CollUtil.isNotEmpty(dsStockoutNoList)){
                            for (String dsStockoutNo : dsStockoutNoList){
                                String sBatchSql = "select a.* ,NVL(uc1.UNIT_RATIO,1) UNIT_RATIO,d.price,d.DISTRIPRICE from DCP_SSTOCKOUT_BATCH a " +
                                        " LEFT JOIN DCP_UNITCONVERT uc1 on uc1.eid=a.eid and uc1.OUNIT=a.PUNIT AND uc1.UNIT=a.BASEUNIT " +
                                        " left join DCP_SSTOCKOUT_DETAIL d on d.eid=a.eid and d.organizationno=a.organizationno and d.sstockoutno=a.sstockoutno and d.item=a.item2 " +
                                        " where a.eid='" + eid + "' and a.organizationno='" + organizationNo + "' " +
                                        " and a.sStockoutno='" + dsStockoutNo + "' ";
                                List<Map<String, Object>> batchDataList=this.doQueryData(sBatchSql, null);
                                for (Map<String, Object> row : batchDataList){
                                    String thisItem = row.get("ITEM").toString();
                                    String thisItem2 = row.get("ITEM2").toString();
                                    String thisPluNo = row.get("PLUNO").toString();
                                    String thisFeatureNo = row.get("FEATURENO").toString();
                                    String thisWarehouse = row.get("WAREHOUSE").toString();
                                    String thisLocation = row.get("LOCATION").toString();
                                    String thisBatchNo = row.get("BATCHNO").toString();
                                    String thisProdDate = row.get("PRODDATE").toString();
                                    String thisExpDate = row.get("EXPDATE").toString();
                                    String thisPUnit = row.get("PUNIT").toString();
                                    String thisPQty = row.get("PQTY").toString();
                                    String thisBaseUnit = row.get("BASEUNIT").toString();
                                    String thisBaseQty = row.get("BASEQTY").toString();
                                    String thisUnitRatio = row.get("UNIT_RATIO").toString();
                                    double amt = BigDecimalUtils.mul(Double.parseDouble(row.get("PRICE").toString()), Double.parseDouble(row.get("PQTY").toString()), 2);
                                    double distriAmt = BigDecimalUtils.mul(Double.parseDouble(row.get("DISTRIPRICE").toString()), Double.parseDouble(row.get("PQTY").toString()), 2);

                                    String procedure = "SP_DCP_StockChange_v3";
                                    Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                    inputParameter.put(1, eid);                                            //--企业ID
                                    inputParameter.put(2, organizationNo);                                         //--组织
                                    inputParameter.put(3, "1");                                //--单据类型
                                    inputParameter.put(4, dsStockoutNo);                                        //--单据号
                                    inputParameter.put(5, thisItem2);
                                    inputParameter.put(6, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                    inputParameter.put(7, bDate);             //--营业日期 yyyy-MM-dd
                                    inputParameter.put(8, thisPluNo);         //--品号
                                    inputParameter.put(9, thisFeatureNo);                                   //--特征码
                                    inputParameter.put(10, thisWarehouse);    //--仓库
                                    inputParameter.put(11, thisBatchNo);     //批号
                                    inputParameter.put(12, thisLocation);     //--location
                                    inputParameter.put(13, thisPUnit);        //--交易单位
                                    inputParameter.put(14, thisPQty);         //--交易数量
                                    inputParameter.put(15, thisBaseUnit);     //--基准单位
                                    inputParameter.put(16, thisBaseQty);      //--基准数量
                                    inputParameter.put(17, thisUnitRatio);   //--换算比例
                                    inputParameter.put(18, row.get("PRICE").toString());        //--零售价
                                    inputParameter.put(19, amt);          //--零售金额
                                    inputParameter.put(20, row.get("DISTRIPRICE").toString());  //--进货价
                                    inputParameter.put(21, distriAmt);    //--进货金额
                                    inputParameter.put(22, accountDate);                                   //--入账日期 yyyy-MM-dd
                                    inputParameter.put(23, thisProdDate);    //--批号的生产日期 yyyy-MM-dd
                                    inputParameter.put(24, bDate);            //--单据日期
                                    inputParameter.put(25, "销货/退货出库扣库存");             //--异动原因
                                    inputParameter.put(26, "");             //--异动描述
                                    inputParameter.put(27, confirmOpId);          //--操作员

                                    ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                    lstData.add(new DataProcessBean(pdb));

                                }

                                //更新状态
                                UptBean ub1 = new UptBean("DCP_SSTOCKOUT");
                                //add Value
                                ub1.addUpdateValue("status", new DataValue("2", Types.VARCHAR));
                                ub1.addUpdateValue("ConfirmBy", new DataValue(confirmOpId, Types.VARCHAR));
                                ub1.addUpdateValue("Confirm_Date", new DataValue(bDate, Types.VARCHAR));
                                ub1.addUpdateValue("Confirm_Time", new DataValue(stime, Types.VARCHAR));
                                ub1.addUpdateValue("accountBy", new DataValue(confirmOpId, Types.VARCHAR));
                                ub1.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
                                ub1.addUpdateValue("account_Time", new DataValue(stime, Types.VARCHAR));
                                ub1.addUpdateValue("SUBMITBY", new DataValue(confirmOpId, Types.VARCHAR));
                                ub1.addUpdateValue("SUBMIT_DATE", new DataValue(bDate, Types.VARCHAR));
                                ub1.addUpdateValue("SUBMIT_TIME", new DataValue(stime, Types.VARCHAR));
                                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                                //condition
                                ub1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                ub1.addCondition("sStockOutNO", new DataValue(dsStockoutNo, Types.VARCHAR));
                                ub1.addCondition("organizationNO", new DataValue(organizationNo, Types.VARCHAR));
                                lstData.add(new DataProcessBean(ub1));

                            }
                        }

                        //更新mes_stockout 状态
                        UptBean ub1 = null;
                        ub1 = new UptBean("MES_StockOut");
                        ub1.addUpdateValue("ISDOWN", new DataValue("Y", Types.VARCHAR));
                        ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                        ub1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                        ub1.addCondition("STOCKOUTNO", new DataValue(stockoutNo, Types.VARCHAR));
                        lstData.add(new DataProcessBean(ub1));
                        //执行sql
                        StaticInfo.dao.useTransactionProcessData(lstData);
                        lstData=new ArrayList<>();
                        }
                        catch (Exception e){
                            logger.error("\r\n******分拣确认报错信息" + e.getMessage() + "******\r\n");

                            //删除生成的单据

                            if(CollUtil.isNotEmpty(dStockoutNoList)) {
                                for (String dStockoutNo : dStockoutNoList) {

                                    DelBean db1 = new DelBean("DCP_STOCKOUT");
                                    db1.addCondition("OrganizationNO", new DataValue(organizationNo, Types.VARCHAR));
                                    db1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                    db1.addCondition("STOCKOUTNO", new DataValue(dStockoutNo, Types.VARCHAR));
                                    lstData.add(new DataProcessBean(db1));

                                    DelBean db2 = new DelBean("DCP_STOCKOUT_DETAIL");
                                    db2.addCondition("OrganizationNO", new DataValue(organizationNo, Types.VARCHAR));
                                    db2.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                    db2.addCondition("STOCKOUTNO", new DataValue(dStockoutNo, Types.VARCHAR));
                                    lstData.add(new DataProcessBean(db2));

                                    DelBean db3 = new DelBean("DCP_STOCKOUT_BATCH");
                                    db3.addCondition("OrganizationNO", new DataValue(organizationNo, Types.VARCHAR));
                                    db3.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                    db3.addCondition("STOCKOUTNO", new DataValue(dStockoutNo, Types.VARCHAR));
                                    lstData.add(new DataProcessBean(db3));
                                }
                            }

                            if(CollUtil.isNotEmpty(dsStockoutNoList)) {
                                for (String dsStockoutNo : dsStockoutNoList) {

                                    DelBean db1 = new DelBean("DCP_SSTOCKOUT");
                                    db1.addCondition("OrganizationNO", new DataValue(organizationNo, Types.VARCHAR));
                                    db1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                    db1.addCondition("SSTOCKOUTNO", new DataValue(dsStockoutNo, Types.VARCHAR));
                                    lstData.add(new DataProcessBean(db1));

                                    DelBean db2 = new DelBean("DCP_SSTOCKOUT_DETAIL");
                                    db2.addCondition("OrganizationNO", new DataValue(organizationNo, Types.VARCHAR));
                                    db2.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                    db2.addCondition("SSTOCKOUTNO", new DataValue(dsStockoutNo, Types.VARCHAR));
                                    lstData.add(new DataProcessBean(db2));

                                    DelBean db3 = new DelBean("DCP_SSTOCKOUT_BATCH");
                                    db3.addCondition("OrganizationNO", new DataValue(organizationNo, Types.VARCHAR));
                                    db3.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                                    db3.addCondition("SSTOCKOUTNO", new DataValue(dsStockoutNo, Types.VARCHAR));
                                    lstData.add(new DataProcessBean(db3));
                                }
                            }
                            StaticInfo.dao.useTransactionProcessData(lstData);

                            continue;
                        }

                    }

                }
            }
            else
            {
                sReturnInfo="错误信息:无单头数据！";
                logger.info("\r\n******分拣确认没有要处理的单据******\r\n");
            }
        }
        catch (Exception e)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                logger.error("\r\n******分拣确认报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******分拣确认报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********分拣确认定时调用End:************\r\n");
        }
        return sReturnInfo;

    }

}
