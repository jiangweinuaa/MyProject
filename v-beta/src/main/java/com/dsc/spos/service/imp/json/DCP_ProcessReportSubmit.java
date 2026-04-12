package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.dao.impl.TableEntityDao;
import com.dsc.spos.json.cust.req.DCP_MStockOutProcessReq;
import com.dsc.spos.json.cust.req.DCP_ProcessReportSubmitReq;
import com.dsc.spos.json.cust.res.DCP_MStockOutProcessRes;
import com.dsc.spos.json.cust.res.DCP_ProcessReportSubmitRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.DcpGood;
import com.dsc.spos.model.DcpGoodsUnit;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_ProcessReportSubmit extends SPosAdvanceService<DCP_ProcessReportSubmitReq, DCP_ProcessReportSubmitRes> {

    @Override
    protected void processDUID(DCP_ProcessReportSubmitReq req, DCP_ProcessReportSubmitRes res) throws Exception {
        ColumnDataValue condition = new ColumnDataValue();

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String bDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        String mStockoutNo ="";

        String querySql = String.format(" SELECT a.* "+
                " ,mm.MATERIAL_TYPE,mm.ZITEM,mm.SITEM,mm.ITEM,mm.MATERIAL_PLUNO" +
                " ,mm.MATERIAL_UNIT,mm.MATERIAL_QTY,mm.ISBUCKLE,mm.KWAREHOUSE" +
                " ,mm.BASEUNIT,mm.BASEQTY,mm.STANDARD_QTY,mm.BATCHNO as MBATCHNO " +
                " ,c.UNITRATIO,b.PRICE,b.PRICE*mm.MATERIAL_QTY as AMT " +
                " ,mm.LOCATION,b.PURPRICE,b.SUPPRICE,b.PURPRICE*mm.mm.MATERIAL_QTY as PURAMT,g.DEPARTID,to_char(d.PRODUCTDATE,'yyyyMMdd') as PRODDATE,to_char(d.LOSEDATE,'yyyyMMdd') as EXPDATE," +
                " TO_CHAR(a.CREATETIME,'HH24MISS') as CREATE_TIME,TO_CHAR(a.CREATETIME,'yyyyMMdd') as CREATE_DATE,TO_CHAR(a.LASTMODITIME,'HH24MISS') as modify_time,TO_CHAR(a.ACCOUNTTIME,'YYYYMMDD') ACCOUNT_DATE,TO_CHAR(a.LASTMODITIME,'YYYYMMDD') MODIFY_DATE," +
                " g.BOMNO,h.baseunit as m_base_unit,h.supprice as m_supprice,i.unitratio as m_unit_ratio " +
                " FROM MES_PROCESS_REPORT a " +
                " LEFT JOIN MES_PROCESS_REPORT_MATERIAL mm ON a.eid =mm.eid and a.ORGANIZATIONNO=mm.ORGANIZATIONNO and a.REPORTNO=mm.REPORTNO " +
                " LEFT JOIN DCP_GOODS b ON mm.EID=b.EID and mm.MATERIAL_PLUNO=b.PLUNO   " +
                " LEFT JOIN DCP_GOODS_UNIT c on mm.EID = c.EID AND mm.MATERIAL_PLUNO=C.PLUNO AND mm.MATERIAL_UNIT=C.OUNIT AND mm.BASEUNIT=C.UNIT " +
                " LEFT JOIN MES_BATCH d ON mm.EID=d.EID and d.PLUNO=mm.MATERIAL_PLUNO and d.BATCHNO=mm.BATCHNO " +
                " left join MES_BATCHTASK g on g.eid=a.eid and g.organizationno=a.organizationno and g.batchTaskNo=a.batchTaskNo " +
                " LEFT JOIN DCP_GOODS h ON a.EID=h.EID and a.ARTIFACTNO=h.PLUNO   " +
                " LEFT JOIN DCP_GOODS_UNIT i on a.EID = i.EID AND a.ARTIFACTNO=I.PLUNO AND a.PPUNIT=I.OUNIT AND h.BASEUNIT=I.UNIT " +
                " WHERE a.EID='%s' and a.REPORTNO='%s' ", req.geteId(), req.getRequest().getReportNo());
        List<Map<String, Object>> qData = doQueryData(querySql, null);
        if (qData != null && !qData.isEmpty()) {
            ColumnDataValue mes_process_report = new ColumnDataValue();
            ColumnDataValue mes_process_reportCondition = new ColumnDataValue();

            Map<String, Object> qOne = qData.get(0);
            mes_process_reportCondition.add("EID", DataValues.newString(qOne.get("EID")));
            mes_process_reportCondition.add("ORGANIZATIONNO", DataValues.newString(qOne.get("ORGANIZATIONNO")));
            mes_process_reportCondition.add("REPORTNO", DataValues.newString(qOne.get("REPORTNO")));

            mes_process_report.add("STATUS", DataValues.newString("1"));
            mes_process_report.add("COMPLETETIME", DataValues.newString(DateFormatUtils.getNowDateTime()));
            mes_process_report.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            mes_process_report.add("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("MES_PROCESS_REPORT", mes_process_reportCondition, mes_process_report)));

            String artifactType = qOne.get("ARTIFACTTYPE").toString();
            String pItem = qOne.get("PITEM").toString();
            String batchNo = qOne.get("BATCHNO").toString();

            //取零售价
            List<Map<String, Object>> plus = qData.stream().map(x->{
                Map<String, Object> plu = new HashMap<String, Object>();
                plu.put("PLUNO", x.get("MATERIAL_PLUNO").toString());
                plu.put("PUNIT", x.get("MATERIAL_UNIT").toString());
                plu.put("BASEUNIT", x.get("BASEUNIT").toString());
                plu.put("UNITRATIO", x.get("UNITRATIO").toString());
                plu.put("SUPPLIERID", "");
                return plu;
            }).distinct().collect(Collectors.toList());

            Map<String, Object> mplu = new HashMap<String, Object>();
            mplu.put("PLUNO", qOne.get("ARTIFACTNO").toString());
            mplu.put("PUNIT", qOne.get("PPUNIT").toString());
            mplu.put("BASEUNIT", qOne.get("M_BASE_UNIT").toString());
            mplu.put("UNITRATIO", qOne.get("M_UNIT_RATIO").toString());
            mplu.put("SUPPLIERID", "");
            plus.add(mplu);

            MyCommon mc = new MyCommon();
            List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, req.geteId(), req.getBELFIRM(), req.getOrganizationNO(), plus,"");


            if ("1".equals(pItem)) {

                ColumnDataValue mes_batchtask_batch = new ColumnDataValue();

                mes_batchtask_batch.add("EID", DataValues.newString(qOne.get("EID")));
                mes_batchtask_batch.add("ORGANIZATIONNO", DataValues.newString(qOne.get("ORGANIZATIONNO")));
                mes_batchtask_batch.add("BATCHTASKNO", DataValues.newString(qOne.get("BATCHTASKNO")));
                mes_batchtask_batch.add("PLUNO", DataValues.newString(qOne.get("MPLUNO")));

                if (StringUtils.isEmpty(batchNo)) {
//                    batchNo = PosPub.getBatchNo(dao, req.geteId(), req.getShopId(), "", qOne.get("MPLUNO").toString(), " ", DateFormatUtils.getPlainDate(qOne.get("COMPLETETIME").toString()), "", req.getEmployeeNo(), req.getEmployeeName(), "");
                    batchNo = PosPub.getBatchNoWithOutRule(req.geteId(),DateFormatUtils.getPlainDate(qOne.get("COMPLETETIME").toString()));
                }

                if (StringUtils.isEmpty(batchNo)){
                    batchNo=" ";

                }
                mes_batchtask_batch.add("BATCHNO", DataValues.newString(batchNo));

                if (" ".equals(batchNo)){
                    ColumnDataValue mes_batchtask_batch_condition = new ColumnDataValue();

                    mes_batchtask_batch_condition.add("EID", DataValues.newString(qOne.get("EID")));
                    mes_batchtask_batch_condition.add("ORGANIZATIONNO", DataValues.newString(qOne.get("ORGANIZATIONNO")));
                    mes_batchtask_batch_condition.add("BATCHTASKNO", DataValues.newString(qOne.get("BATCHTASKNO")));
                    mes_batchtask_batch_condition.add("PLUNO", DataValues.newString(qOne.get("MPLUNO")));
                    mes_batchtask_batch_condition.add("BATCHNO", DataValues.newString(batchNo));

                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_BATCHTASK_BATCH", mes_batchtask_batch_condition)));
                }

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_BATCHTASK_BATCH", mes_batchtask_batch)));
            }

            ColumnDataValue mes_batchtask_report = new ColumnDataValue();
            mes_batchtask_report.add("EID", DataValues.newString(qOne.get("EID")));
            mes_batchtask_report.add("ORGANIZATIONNO", DataValues.newString(qOne.get("ORGANIZATIONNO")));
            mes_batchtask_report.add("BATCHTASKNO", DataValues.newString(qOne.get("BATCHTASKNO")));
            if ("1".equals(pItem)) {
                mes_batchtask_report.add("BATCHNO", DataValues.newString(batchNo));
            } else {
                mes_batchtask_report.add("BATCHNO", DataValues.newString(qOne.get("BATCHNO")));
            }

            mes_batchtask_report.add("PITEM", DataValues.newString(qOne.get("PITEM")));
            mes_batchtask_report.add("REPORTNO", DataValues.newString(qOne.get("REPORTNO")));

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_BATCHTASK_REPORT", mes_batchtask_report)));
            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_BATCHTASK_REPORT", mes_batchtask_report)));

            String warehouse = "";
            querySql = " SELECT a.* FROM MES_BATCHTASK_PROCESS a WHERE a.BATCHTASKNO='" + qOne.get("BATCHTASKNO").toString() + "' and a.PITEM='" + qOne.get("PITEM").toString() + "'";
            List<Map<String, Object>> qDataBatch = doQueryData(querySql, null);
            if (qDataBatch != null && !qDataBatch.isEmpty()) {

                Map<String, Object> qBatchOne = qDataBatch.get(0);
                warehouse = qBatchOne.get("WAREHOUSE").toString();
                ColumnDataValue mes_batchtask_process = new ColumnDataValue();
                ColumnDataValue mes_batchtask_processCondition = new ColumnDataValue();

                mes_batchtask_processCondition.append("EID", DataValues.newString(qBatchOne.get("EID")));
                mes_batchtask_processCondition.append("ORGANIZATIONNO", DataValues.newString(qBatchOne.get("ORGANIZATIONNO")));
                mes_batchtask_processCondition.append("BATCHTASKNO", DataValues.newString(qBatchOne.get("BATCHTASKNO")));
                mes_batchtask_processCondition.append("PITEM", DataValues.newString(qBatchOne.get("PITEM")));

                double producedQty = BigDecimalUtils.add(Double.parseDouble(qBatchOne.get("PRODUCEDQTY").toString()), Double.parseDouble(qOne.get("PPQTY").toString()));
                double actualProducedQty = BigDecimalUtils.add(Double.parseDouble(qBatchOne.get("PRODUCEDQTY").toString()), Double.parseDouble(qOne.get("PASSQTY").toString()));

                mes_batchtask_process.add("PRODUCEDQTY", DataValues.newDecimal(producedQty));
                mes_batchtask_process.add("ACTUAL_PRODUCEDQTY", DataValues.newDecimal(actualProducedQty));
                if (producedQty > 0) {
                    mes_batchtask_process.add("REPORTSTATUS", DataValues.newString("Y"));
                }

                addProcessData(new DataProcessBean(DataBeans.getUptBean("MES_BATCHTASK_PROCESS", mes_batchtask_processCondition, mes_batchtask_process)));

                if (actualProducedQty > 0) {
                    ColumnDataValue mes_batchtask_process2 = new ColumnDataValue();
                    ColumnDataValue mes_batchtask_processCondition2 = new ColumnDataValue();

                    mes_batchtask_processCondition2.append("EID", DataValues.newString(qBatchOne.get("EID")));
                    mes_batchtask_processCondition2.append("ORGANIZATIONNO", DataValues.newString(qBatchOne.get("ORGANIZATIONNO")));
                    mes_batchtask_processCondition2.append("BATCHTASKNO", DataValues.newString(qBatchOne.get("BATCHTASKNO")));
                    mes_batchtask_processCondition2.append("PITEM", DataValues.newInteger(Integer.parseInt(qBatchOne.get("PITEM").toString()) + 1));

                    mes_batchtask_process2.add("ISEXECUTABLE", DataValues.newString("Y"));

                    addProcessData(new DataProcessBean(DataBeans.getUptBean("MES_BATCHTASK_PROCESS", mes_batchtask_processCondition2, mes_batchtask_process2)));

                }
            }

            //String productionno = getNormalNO(req, "SCRK");
            if ("2".equals(artifactType)) {

                /**
                ColumnDataValue mes_productin = new ColumnDataValue();
                ColumnDataValue mes_productin_detail = new ColumnDataValue();

                mes_productin.add("EID", DataValues.newString(qOne.get("EID")));
                mes_productin.add("ORGANIZATIONNO", DataValues.newString(qOne.get("ORGANIZATIONNO")));
                mes_productin.add("PRODUCTIONNO", DataValues.newString(productionno));

                mes_productin.add("PGROUPNO", DataValues.newString(qOne.get("PGROUPNO")));
                mes_productin.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
                mes_productin.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                mes_productin.add("CREATEOPNAME", DataValues.newString(req.getEmployeeName()));
                mes_productin.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                mes_productin.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));

                mes_productin_detail.add("EID", DataValues.newString(qOne.get("EID")));
                mes_productin_detail.add("ORGANIZATIONNO", DataValues.newString(qOne.get("ORGANIZATIONNO")));
                mes_productin_detail.add("PRODUCTIONNO", DataValues.newString(productionno));
                mes_productin_detail.add("ITEM", DataValues.newString(1));
                mes_productin_detail.add("PLUNO", DataValues.newString(qOne.get("ARTIFACTNO")));
                mes_productin_detail.add("PUNIT", DataValues.newString(qOne.get("PPUNIT")));
                mes_productin_detail.add("PQTY", DataValues.newString(qOne.get("PASSQTY")));
                mes_productin_detail.add("SCRAPQTY", DataValues.newString(qOne.get("SCRAPQTY")));
                mes_productin_detail.add("REASON", DataValues.newString(qOne.get("REASON")));
                mes_productin_detail.add("WAREHOUSE", DataValues.newString(warehouse));

                TableEntityDao entityDao = MySpringContext.getBean("entityDao");
                String entitySQL = "SELECT * FROM DCP_GOODS WHERE EID='"+qOne.get("EID")+"' AND PLUNO='"+qOne.get("ARTIFACTNO")+"' ";
                DcpGood dcpGood = entityDao.queryOne(entitySQL,DcpGood.class);
                entitySQL = " SELECT * FROM DCP_GOODS_UNIT WHERE EID='"+qOne.get("EID")+"' AND " +
                        " PLUNO='"+qOne.get("ARTIFACTNO")+"' AND UNIT='"+dcpGood.getBaseunit()+"' " +
                        " AND OUNIT='"+ qOne.get("PPUNIT") +"' ";
                DcpGoodsUnit dcpGoodsUnit = entityDao.queryOne(entitySQL,DcpGoodsUnit.class);
                BigDecimal unitRatio = BigDecimal.valueOf(1);
                if (null!=dcpGoodsUnit){
                    unitRatio = dcpGoodsUnit.getUnitratio();
                }

                double baseQty = BigDecimalUtils.mul(unitRatio.doubleValue(),Double.parseDouble(qOne.get("PASSQTY").toString()));

                mes_productin_detail.add("BASEUNIT", DataValues.newString(dcpGood.getBaseunit()));
                mes_productin_detail.add("BASEQTY", DataValues.newString( baseQty));
                mes_productin_detail.add("SOURCENO", DataValues.newString( qOne.get("SOURCENO")));
                mes_productin_detail.add("MITEM", DataValues.newString("1"));
                mes_productin_detail.add("BATCHNO", DataValues.newString(batchNo));

                addProcessData(new DataProcessBean( DataBeans.getInsBean("mes_productin",mes_productin) ));
                addProcessData(new DataProcessBean( DataBeans.getInsBean("mes_productin_detail",mes_productin_detail) ));

                 **/

                //生成pstockin
                BigDecimal pstTotAmt=new BigDecimal(0);
                BigDecimal pstTotDistriAmt=new BigDecimal(0);

                String pStockInNo = this.getOrderNO(req, "WGRK");

                String price="0";
                Map<String, Object> condiV = new HashMap<String, Object>();
                condiV.put("PLUNO", qOne.get("ARTIFACTNO").toString());
                condiV.put("PUNIT", qOne.get("PPUNIT").toString());
                List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                if (priceList != null && priceList.size() > 0) {
                    price = priceList.get(0).get("PRICE").toString();
                }
                BigDecimal mAmt=new BigDecimal(qOne.get("PPQTY").toString()).multiply(new BigDecimal(price));

                Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao,req.geteId(), qOne.get("ARTIFACTNO").toString(), qOne.get("M_BASE_UNIT").toString(),qOne.get("PPUNIT").toString(), qOne.get("M_SUPPRICE").toString());
                String distriPrice = unitCalculate.get("afterDecimal").toString();
                BigDecimal distriAmt = new BigDecimal(distriPrice).multiply(new BigDecimal(qOne.get("PPQTY").toString()));

                BigDecimal mBaseQty= new BigDecimal(qOne.get("PASSQTY").toString()).multiply(new BigDecimal(qOne.get("M_UNIT_RATIO").toString()));



                ColumnDataValue pstdColumns=new ColumnDataValue();
                pstdColumns.add("EID", DataValues.newString(req.geteId()));
                pstdColumns.add("SHOPID", DataValues.newString(req.getShopId()));
                pstdColumns.add("PSTOCKINNO", DataValues.newString(pStockInNo));
                pstdColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                pstdColumns.add("ITEM", DataValues.newString("1"));

                pstdColumns.add("WAREHOUSE", DataValues.newString(warehouse));
                pstdColumns.add("PQTY", DataValues.newString(qOne.get("PASSQTY").toString()));
                pstdColumns.add("UNIT_RATIO", DataValues.newString(qOne.get("M_UNIT_RATIO").toString()));
                pstdColumns.add("PLUNO", DataValues.newString(qOne.get("ARTIFACTNO").toString()));
                pstdColumns.add("AMT", DataValues.newString(mAmt));
                pstdColumns.add("BASEQTY", DataValues.newString(mBaseQty));
                pstdColumns.add("PUNIT", DataValues.newString(qOne.get("PPUNIT").toString()));
                pstdColumns.add("PRICE", DataValues.newString(price));
                pstdColumns.add("BASEUNIT", DataValues.newString(qOne.get("M_BASE_UNIT")));
                pstdColumns.add("TASK_QTY", DataValues.newString(qOne.get("PPQTY").toString()));

                pstdColumns.add("TRAN_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
                pstdColumns.add("MEMO", DataValues.newString("工序："));
                pstdColumns.add("BATCH_NO", DataValues.newString(qOne.get("BATCHNO").toString()));
                pstdColumns.add("PROD_DATE", DataValues.newString(qOne.get("BDATE").toString()));
                pstdColumns.add("DISTRIPRICE", DataValues.newString(distriPrice));
                pstdColumns.add("DISTRIAMT", DataValues.newString(distriAmt));

                pstdColumns.add("ACCOUNT_DATE", DataValues.newString(qOne.get("ACCOUNT_DATE").toString()));
                pstdColumns.add("FEATURENO", DataValues.newString(" "));
                pstdColumns.add("OFNO", DataValues.newString(req.getRequest().getReportNo()));
                pstdColumns.add("PARTITION_DATE", DataValues.newString(qOne.get("BDATE").toString()));
                pstdColumns.add("PRODTYPE", DataValues.newString("0"));

                pstdColumns.add("BOMNO", DataValues.newString(qOne.get("BOMNO").toString()));
                pstdColumns.add("VERSIONNUM", DataValues.newString(""));
                pstdColumns.add("DISPTYPE", DataValues.newString("1"));
                pstdColumns.add("OOTYPE", DataValues.newString("1"));
                pstdColumns.add("OOFNO", DataValues.newString(qOne.get("BATCHTASKNO")));

                String[] pstdColumnNames = pstdColumns.getColumns().toArray(new String[0]);
                DataValue[] pstdDataValues = pstdColumns.getDataValues().toArray(new DataValue[0]);
                InsBean pstdib=new InsBean("DCP_PSTOCKIN_DETAIL",pstdColumnNames);
                pstdib.addValues(pstdDataValues);
                this.addProcessData(new DataProcessBean(pstdib));

                //加上报废表的数据
                String scrapSql="select * from MES_PROCESS_REPORT_SCRAP a where a.eid='"+req.geteId()+"' " +
                        "and a.organizationno='"+req.getOrganizationNO()+"' and a.reportno='"+req.getRequest().getReportNo()+"' ";
                List<Map<String, Object>> scrapList = this.doQueryData(scrapSql, null);
                if(CollUtil.isNotEmpty(scrapList)){
                    int scarpItem=1;
                    for (Map<String, Object> scrapOne : scrapList){
                        scarpItem++;
                        ColumnDataValue pstdColumns2=new ColumnDataValue();
                        pstdColumns2.add("EID", DataValues.newString(req.geteId()));
                        pstdColumns2.add("SHOPID", DataValues.newString(req.getShopId()));
                        pstdColumns2.add("STOCKINNO", DataValues.newString(pStockInNo));
                        pstdColumns2.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                        pstdColumns2.add("ITEM", DataValues.newString(scarpItem));

                        pstdColumns2.add("WAREHOUSE", DataValues.newString(warehouse));
                        pstdColumns2.add("PQTY", DataValues.newString(0));
                        pstdColumns2.add("UNIT_RATIO", DataValues.newString(qOne.get("M_UNIT_RATIO").toString()));
                        pstdColumns2.add("PLUNO", DataValues.newString(qOne.get("ARTIFACTNO").toString()));
                        pstdColumns2.add("AMT", DataValues.newString(0));
                        pstdColumns2.add("BASEQTY", DataValues.newString(0));
                        pstdColumns2.add("PUNIT", DataValues.newString(qOne.get("PPUNIT").toString()));
                        pstdColumns2.add("PRICE", DataValues.newString(price));
                        pstdColumns2.add("BASEUNIT", DataValues.newString(qOne.get("M_BASE_UNIT")));
                        pstdColumns2.add("TASK_QTY", DataValues.newString(0));
                        pstdColumns2.add("SCRAP_QTY", DataValues.newString(scrapOne.get("SCRAP_QTY").toString()));

                        pstdColumns2.add("TRAN_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
                        pstdColumns2.add("MEMO", DataValues.newString("工序："));
                        pstdColumns2.add("BATCH_NO", DataValues.newString(qOne.get("BATCHNO").toString()));
                        pstdColumns2.add("PROD_DATE", DataValues.newString(qOne.get("BDATE").toString()));
                        pstdColumns2.add("DISTRIPRICE", DataValues.newString(distriPrice));
                        pstdColumns2.add("DISTRIAMT", DataValues.newString(distriAmt));

                        pstdColumns2.add("ACCOUNT_DATE", DataValues.newString(qOne.get("ACCOUNT_DATE").toString()));
                        pstdColumns2.add("FEATURENO", DataValues.newString(" "));
                        pstdColumns2.add("OFNO", DataValues.newString(req.getRequest().getReportNo()));
                        pstdColumns2.add("PARTITION_DATE", DataValues.newString(qOne.get("BDATE").toString()));
                        pstdColumns2.add("PRODTYPE", DataValues.newString("0"));

                        pstdColumns2.add("BOMNO", DataValues.newString(qOne.get("BOMNO").toString()));
                        pstdColumns2.add("VERSIONNUM", DataValues.newString(""));
                        pstdColumns2.add("DISPTYPE", DataValues.newString("1"));
                        pstdColumns2.add("OOTYPE", DataValues.newString("1"));
                        pstdColumns2.add("OOFNO", DataValues.newString(qOne.get("BATCHTASKNO")));

                        String[] pstdColumnNames2 = pstdColumns2.getColumns().toArray(new String[0]);
                        DataValue[] pstdDataValues2 = pstdColumns2.getDataValues().toArray(new DataValue[0]);
                        InsBean pstdib2=new InsBean("DCP_PSTOCKIN_DETAIL",pstdColumnNames2);
                        pstdib2.addValues(pstdDataValues2);
                        this.addProcessData(new DataProcessBean(pstdib2));
                    }

                }


                ColumnDataValue pstColumns=new ColumnDataValue();
                pstColumns.add("EID", DataValues.newString(req.geteId()));
                pstColumns.add("SHOPID", DataValues.newString(req.getShopId()));
                pstColumns.add("PSTOCKINNO", DataValues.newString(pStockInNo));
                pstColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                pstColumns.add("BDATE", DataValues.newString(bDate));
                pstColumns.add("OTYPE", DataValues.newString("4"));
                pstColumns.add("CREATE_DATE", DataValues.newString(qOne.get("CREATE_DATE").toString()));
                pstColumns.add("CANCEL_TIME", DataValues.newString(qOne.get("CREATE_TIME").toString()));
                pstColumns.add("PROCESS_STATUS", DataValues.newString("N"));
                pstColumns.add("TOT_CQTY", DataValues.newString(1));
                pstColumns.add("PTEMPLATENO", DataValues.newString(""));
                pstColumns.add("ACCOUNTBY", DataValues.newString(qOne.get("ACCOUNTOPID").toString()));
                pstColumns.add("TRAN_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
                //pstColumns.add("ACCOUNT_TIME", DataValues.newString(""));
                pstColumns.add("LOAD_DOCNO", DataValues.newString(""));
                //pstColumns.add("CONFIRM_DATE", DataValues.newString(""));
                pstColumns.add("TOT_PQTY", DataValues.newString(qOne.get("PASSQTY").toString()));

                pstColumns.add("WAREHOUSE", DataValues.newString(""));
                pstColumns.add("DOC_TYPE", DataValues.newString("0"));
                //pstColumns.add("CONFIRM_TIME", DataValues.newString(""));
                pstColumns.add("CREATEBY", DataValues.newString(qOne.get("CREATEOPID")));
                pstColumns.add("CANCELBY", DataValues.newString(""));
                pstColumns.add("MODIFYBY", DataValues.newString(qOne.get("LASTMODIOPID").toString()));
                pstColumns.add("LOAD_DOCTYPE", DataValues.newString(""));
                pstColumns.add("MODIFY_TIME", DataValues.newString(qOne.get("MODIFY_TIME")));
                pstColumns.add("ACCOUNT_DATE", DataValues.newString(qOne.get("ACCOUNT_DATE")));
                pstColumns.add("OFNO", DataValues.newString(req.getRequest().getReportNo()));

                pstColumns.add("TOT_AMT", DataValues.newString(pstTotAmt));
                pstColumns.add("MODIFY_DATE", DataValues.newString(qOne.get("MODIFY_DATE")));
                //pstColumns.add("PSTOCKIN_ID", DataValues.newString(req.getRequest().getReportNo()));
                pstColumns.add("MEMO", DataValues.newString("报工单生成完工入库"));
                pstColumns.add("UPDATE_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));

                pstColumns.add("SUBMITBY", DataValues.newString(qOne.get("CREATEOPID").toString()));
                pstColumns.add("SUBMIT_DATE", DataValues.newString(qOne.get("CREATE_DATE").toString()));
                pstColumns.add("SUBMIT_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
                pstColumns.add("TOT_DISTRIAMT", DataValues.newString(pstTotDistriAmt));
                pstColumns.add("STATUS", DataValues.newString("2"));
                pstColumns.add("PARTITION_DATE", DataValues.newString(bDate));
                pstColumns.add("BDATE_DATE", DataValues.newDate(DateFormatUtils.getDate(qOne.get("BDATE").toString(),"yyyy-MM-dd HH:mm:ss")));

                pstColumns.add("CREATEOPID", DataValues.newString(qOne.get("CREATEOPID")));
                pstColumns.add("CREATETIME", DataValues.newDate(qOne.get("CREATETIME").toString()));
                pstColumns.add("LASTMODIOPID", DataValues.newString(qOne.get("LASTMODIOPID").toString()));
                pstColumns.add("LASTMODITIME", DataValues.newDate(qOne.get("LASTMODITIME")));
                pstColumns.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
                pstColumns.add("DEPARTID", DataValues.newString(qOne.get("DEPARTID").toString()));
                pstColumns.add("CREATEDEPTID", DataValues.newString(qOne.get("DEPARTID").toString()));
                pstColumns.add("OOTYPE", DataValues.newString("1"));
                pstColumns.add("OOFNO", DataValues.newString(qOne.get("BATCHTASKNO")));

                String[] pstColumnNames = pstColumns.getColumns().toArray(new String[0]);
                DataValue[] pstDataValues = pstColumns.getDataValues().toArray(new DataValue[0]);
                InsBean pstib=new InsBean("DCP_PSTOCKIN",pstColumnNames);
                pstib.addValues(pstDataValues);
                this.addProcessData(new DataProcessBean(pstib));


                List<Object> inputParameter08 = Lists.newArrayList();

                inputParameter08.add(qOne.get("EID").toString());                                                     //P_EID		IN	VARCHAR2,	--企业ID
                inputParameter08.add(qOne.get("ORGANIZATIONNO").toString());                                          //P_OrgID		IN	VARCHAR2,	--组织
                inputParameter08.add("08");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                inputParameter08.add(pStockInNo);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                inputParameter08.add("1");                                                   //P_Item		IN	INTEGER,	--单据行号
                inputParameter08.add(1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                inputParameter08.add(DateFormatUtils.getNowPlainDate());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                inputParameter08.add(qOne.get("ARTIFACTNO").toString());                                          //P_PluNo		IN	VARCHAR2,	--品号
                inputParameter08.add(" ");          //P_FeatureNo	IN	VARCHAR2,	--特征码
                inputParameter08.add(warehouse);                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                inputParameter08.add(batchNo);                                                 //P_BATCHNO	IN	VARCHAR2,	--批号
                inputParameter08.add(" ");                                                //P_LOCATION IN VARCHAR2,	--库位
                inputParameter08.add(qOne.get("PPUNIT").toString());                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                inputParameter08.add(qOne.get("PASSQTY").toString());                                            //P_Qty		IN	NUMBER,		--交易数量
                inputParameter08.add(qOne.get("M_BASE_UNIT").toString());                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                inputParameter08.add(mBaseQty);                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                inputParameter08.add(qOne.get("M_UNIT_RATIO").toString());                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                inputParameter08.add("0");                                                   //P_Price		IN	NUMBER,		--零售价
                inputParameter08.add("0");                                                     //P_Amt		IN	NUMBER,		--零售金额
                inputParameter08.add("0");                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                inputParameter08.add("0");                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
                inputParameter08.add(DateFormatUtils.getDate(qOne.get("BDATE").toString()));               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                inputParameter08.add(DateFormatUtils.getDate(qOne.get("COMPLETETIME").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                inputParameter08.add(DateFormatUtils.getDate(qOne.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                inputParameter08.add("MES_ReportReceiptSubmit生成");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                inputParameter08.add("");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                inputParameter08.add(req.getEmployeeNo());                                                    //P_UserID	IN	VARCHAR2	--操作员

                this.addProcessData(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_V3", inputParameter08)));

            }

            //生成扣料单
            BigDecimal mtotCqty=new BigDecimal(qData.stream().map(x->x.get("MATERIAL_PLUNO").toString()).distinct().collect(Collectors.toList()).size());
            BigDecimal mtotPqty=BigDecimal.ZERO;
            BigDecimal mtotAmt=BigDecimal.ZERO;
            BigDecimal mtotDistriAmt=BigDecimal.ZERO;

            mStockoutNo = this.getOrderNO(req, "SCKL");
            Integer mstockItem=0;

            for (Map<String, Object> oneData : qData) {

                String materialType = oneData.get("MATERIAL_TYPE").toString();
                String isBuckle = oneData.get("ISBUCKLE").toString();

                if ("0".equals(materialType) && "Y".equals(isBuckle)) {
                    List<Object> inputParameter = Lists.newArrayList();

                    inputParameter.add(oneData.get("EID").toString());                                                     //P_EID		IN	VARCHAR2,	--企业ID
                    inputParameter.add(oneData.get("ORGANIZATIONNO").toString());                                          //P_OrgID		IN	VARCHAR2,	--组织
                    inputParameter.add("11");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                    inputParameter.add(oneData.get("REPORTNO").toString());                                                //P_BillNo	IN	VARCHAR2,	--单据号
                    inputParameter.add(oneData.get("ZITEM").toString());                                                   //P_Item		IN	INTEGER,	--单据行号
                    inputParameter.add(-1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                    inputParameter.add(oneData.get("BDATE").toString());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                    inputParameter.add(oneData.get("MATERIAL_PLUNO").toString());                                          //P_PluNo		IN	VARCHAR2,	--品号
                    inputParameter.add(StringUtils.toString(oneData.get("FEATURENO"), " "));          //P_FeatureNo	IN	VARCHAR2,	--特征码
                    inputParameter.add(oneData.get("KWAREHOUSE").toString());                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                    inputParameter.add(oneData.get("MBATCHNO").toString());                                                 //P_BATCHNO	IN	VARCHAR2,	--批号
                    inputParameter.add(oneData.get("LOCATION").toString());                                                //P_LOCATION IN VARCHAR2,	--库位
                    inputParameter.add(oneData.get("MATERIAL_UNIT").toString());                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                    inputParameter.add(oneData.get("MATERIAL_QTY").toString());                                            //P_Qty		IN	NUMBER,		--交易数量
                    inputParameter.add(oneData.get("BASEUNIT").toString());                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                    inputParameter.add(oneData.get("BASEQTY").toString());                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                    inputParameter.add(oneData.get("UNITRATIO").toString());                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                    inputParameter.add("0");                                                   //P_Price		IN	NUMBER,		--零售价
                    inputParameter.add("0");                                                     //P_Amt		IN	NUMBER,		--零售金额
                    inputParameter.add("0");                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                    inputParameter.add("0");                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
                    inputParameter.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                    inputParameter.add(DateFormatUtils.getDate(oneData.get("COMPLETETIME").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                    inputParameter.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                    inputParameter.add("MES_ReportReceiptSubmit生成");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                    inputParameter.add("");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                    inputParameter.add(req.getEmployeeNo());                                                    //P_UserID	IN	VARCHAR2	--操作员

                    this.addProcessData(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_V3", inputParameter)));

                }
                String reportNo = oneData.get("REPORTNO").toString();
                String item = oneData.get("ITEM").toString();

                ColumnDataValue mes_process_report_detail = new ColumnDataValue();
                ColumnDataValue mes_process_report_detailCondition = new ColumnDataValue();

                mes_process_report_detailCondition.add("EID", DataValues.newString(oneData.get("EID")));
                mes_process_report_detailCondition.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO")));
                mes_process_report_detailCondition.add("REPORTNO", DataValues.newString(oneData.get("REPORTNO")));
                mes_process_report_detailCondition.add("ITEM", DataValues.newString(oneData.get("ITEM")));

                mes_process_report_detail.append("REPORTSTATUS", DataValues.newString("Y"));
                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("MES_PROCESS_REPORT_DETAIL", mes_process_report_detailCondition, mes_process_report_detail)));


                //生成扣料单明细

                String price="0";
                Map<String, Object> condiV = new HashMap<String, Object>();
                condiV.put("PLUNO", oneData.get("MATERIAL_PLUNO").toString());
                condiV.put("PUNIT", oneData.get("MATERIAL_UNIT").toString());
                List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                if (priceList != null && priceList.size() > 0) {
                    price = priceList.get(0).get("PRICE").toString();
                }
                BigDecimal amt = new BigDecimal(price).multiply(new BigDecimal(oneData.get("MATERIAL_QTY").toString()));
                Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao,req.geteId(), oneData.get("MATERIAL_PLUNO").toString(), oneData.get("BASEUNIT").toString(),oneData.get("MATERIAL_UNIT").toString(), oneData.get("SUPPRICE").toString());
                String distriPrice = unitCalculate.get("afterDecimal").toString();
                BigDecimal distriAmt = new BigDecimal(distriPrice).multiply(new BigDecimal(oneData.get("MATERIAL_QTY").toString()));

                 mtotPqty=mtotPqty.add(new BigDecimal(oneData.get("MATERIAL_QTY").toString()));
                 mtotAmt=mtotAmt.add(amt);
                 mtotDistriAmt=mtotDistriAmt.add(distriAmt);

                mstockItem++;
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(req.geteId()));
                detailColumns.add("SHOPID", DataValues.newString(req.getShopId()));
                detailColumns.add("MSTOCKOUTNO", DataValues.newString(mStockoutNo));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                detailColumns.add("WAREHOUSE", DataValues.newString(oneData.get("KWAREHOUSE").toString()));
                detailColumns.add("ITEM", DataValues.newString(mstockItem));
                detailColumns.add("PLUNO", DataValues.newString(oneData.get("MATERIAL_PLUNO").toString()));
                detailColumns.add("PUNIT", DataValues.newString(oneData.get("MATERIAL_UNIT").toString()));
                detailColumns.add("PQTY", DataValues.newString(oneData.get("MATERIAL_QTY").toString()));
                detailColumns.add("BASEUNIT", DataValues.newString(oneData.get("BASEUNIT").toString()));
                detailColumns.add("BASEQTY", DataValues.newString(oneData.get("BASEQTY").toString()));
                detailColumns.add("UNIT_RATIO", DataValues.newString(oneData.get("UNITRATIO").toString()));
                detailColumns.add("PRICE", DataValues.newString(price));
                detailColumns.add("AMT", DataValues.newString(amt));
                detailColumns.add("DISTRIPRICE", DataValues.newString(distriPrice));//供货价
                detailColumns.add("DISTRIAMT", DataValues.newString(distriAmt));
                detailColumns.add("BATCHNO", DataValues.newString(qOne.get("BATCHNO").toString()));
                detailColumns.add("PRODDATE", DataValues.newString(qOne.get("PRODDATE").toString()));
                detailColumns.add("EXPDATE", DataValues.newString(qOne.get("EXPDATE").toString()));
                detailColumns.add("ISBUCKLE", DataValues.newString("Y"));
                detailColumns.add("FEATURENO", DataValues.newString(" "));
                detailColumns.add("LOCATION", DataValues.newString(oneData.get("LOCATION").toString()));
                detailColumns.add("OTYPE", DataValues.newString("1"));
                detailColumns.add("OFNO", DataValues.newString(req.getRequest().getReportNo()));
                detailColumns.add("OITEM", DataValues.newString(oneData.get("ITEM").toString()));
                detailColumns.add("OOTYPE", DataValues.newString("1"));
                detailColumns.add("OOFNO", DataValues.newString(oneData.get("BATCHTASKNO").toString()));
                detailColumns.add("OOITEM", DataValues.newString(""));
                detailColumns.add("LOAD_DOCITEM", DataValues.newString(""));
                detailColumns.add("PITEM", DataValues.newString(oneData.get("PITEM").toString()));
                detailColumns.add("PROCESSNO", DataValues.newString(oneData.get("PROCESSNO").toString()));
                detailColumns.add("SITEM", DataValues.newString(oneData.get("SITEM").toString()));
                detailColumns.add("ZITEM", DataValues.newString(oneData.get("ZITEM").toString()));
                detailColumns.add("OPQTY", DataValues.newString(0));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_MSTOCKOUT_DETAIL",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));

            }

            ColumnDataValue mstColumns=new ColumnDataValue();
            mstColumns.add("EID", DataValues.newString(req.geteId()));
            mstColumns.add("SHOPID", DataValues.newString(req.getShopId()));
            mstColumns.add("MSTOCKOUTNO", DataValues.newString(mStockoutNo));
            mstColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
            //mainColumns.add("WAREHOUSE", DataValues.newString(""));
            mstColumns.add("DOC_TYPE", DataValues.newString("0"));//入库提交为0，报工红冲为1
            mstColumns.add("ACCOUNT_DATE", DataValues.newString(bDate));

            mstColumns.add("BDATE", DataValues.newString(bDate));
            mstColumns.add("SDATE", DataValues.newString(bDate));
            mstColumns.add("OTYPE", DataValues.newString("1"));
            mstColumns.add("OFNO", DataValues.newString(req.getRequest().getReportNo()));
            mstColumns.add("OOTYPE", DataValues.newString("1"));
            mstColumns.add("OOFNO", DataValues.newString(qOne.get("BATCHTASKNO").toString()));
            mstColumns.add("MEMO", DataValues.newString(qOne.get("PROCESSNO").toString()));
            mstColumns.add("STATUS", DataValues.newInteger(0));
            mstColumns.add("ADJUSTSTATUS", DataValues.newString("0"));
            mstColumns.add("OMSTOCKOUTNO", DataValues.newString(""));
            mstColumns.add("TOT_CQTY", DataValues.newDecimal(mtotCqty));
            mstColumns.add("TOT_PQTY", DataValues.newDecimal(mtotPqty));
            mstColumns.add("TOT_AMT", DataValues.newDecimal(mtotAmt));
            mstColumns.add("TOT_DISTRIAMT", DataValues.newDecimal(mtotDistriAmt));

            mstColumns.add("AUTOPROCESS", DataValues.newString("Y"));
            mstColumns.add("PROCESS_STATUS", DataValues.newString("N"));

            mstColumns.add("LOAD_DOCTYPE", DataValues.newString(""));
            mstColumns.add("LOAD_DOCNO", DataValues.newString(""));
            mstColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
            mstColumns.add("CREATETIME", DataValues.newDate(lastmoditime));
            mstColumns.add("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

            mstColumns.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
            mstColumns.add("DEPARTID", DataValues.newString(qOne.get("DEPARTID").toString()));

            String[] mstColumnNames = mstColumns.getColumns().toArray(new String[0]);
            DataValue[] mstDataValues = mstColumns.getDataValues().toArray(new DataValue[0]);
            InsBean mstib=new InsBean("DCP_MSTOCKOUT",mstColumnNames);
            mstib.addValues(mstDataValues);
            this.addProcessData(new DataProcessBean(mstib));

        }

        //将MES_PROCESS_REPORT和MES_PROCESS_REPORT_DETAIL表数据插入表DCP_WOREPORT_TRAN;
        //OOTYPE=1,
        //OOFNO=BATCHTASKNO,
        //OOITEM=空
        //EQTY=COUNT(MES_PROCESS_REPORT_USER)
        //LABORTIME=PREPORTTIME(hhmmss转换为分钟保留2位小数)
        //MACHINETIME=EREPORTTIME(hhmmss转换为分钟保留2位小数)


        String prDetailSql="select a.*,b.BATCHTASKNO,b.ARTIFACTNO AS PLUNO,b.PPUNIT AS PUNIT,b.PPQTY AS PQTY,B.EQUIPNO,b.PITEM,b.PROCESSNO " +
                " from MES_PROCESS_REPORT_DETAIL a " +
                " inner join MES_PROCESS_REPORT b on a.eid=b.eid and a.organizationno=b.organizationno and a.REPORTNO=b.REPORTNO " +
                " inner join MES_BATCHTASK c on c.eid=a.eid and c.organizationno=a.organizationno and c.batchtaskno=b.batchtaskno " +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.reportno='"+req.getRequest().getReportNo()+"' ";
        List<Map<String, Object>> reportDetailList = this.doQueryData( prDetailSql, null);

        if(reportDetailList.size()>0){

            String uSql="select * from MES_PROCESS_REPORT_USER a  " +
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                    " and a.reportno='"+req.getRequest().getReportNo()+"' ";
            List<Map<String, Object>> reportUserList = this.doQueryData( uSql, null);

            String reportNo = this.getOrderNO(req, "GDBG");
            int item=0;
            for (Map<String, Object> reportDetail : reportDetailList) {

                item++;

                //将MES_PROCESS_REPORT和MES_PROCESS_REPORT_DETAIL表数据插入表DCP_WOREPORT_TRAN;
                //OOTYPE=1,
                //OOFNO=BATCHTASKNO,
                //OOITEM=空
                //EQTY=COUNT(MES_PROCESS_REPORT_USER)
                //LABORTIME=PREPORTTIME(hhmmss转换为分钟保留2位小数)
                //MACHINETIME=EREPORTTIME(hhmmss转换为分钟保留2位小数)

                Double preportTime = this.convertToMinutes(reportDetail.get("PREPORTTIME").toString());
                Double ereportTime = this.convertToMinutes(reportDetail.get("EREPORTTIME").toString());
                String pluNo = reportDetail.get("PLUNO").toString();
                String pQty = reportDetail.get("PQTY").toString();
                String pUnit = reportDetail.get("PUNIT").toString();
                String equipNo = reportDetail.get("EQUIPNO").toString();
                String pItem = reportDetail.get("PITEM").toString();
                String processNo = reportDetail.get("PROCESSNO").toString();
                //String artifactNo = reportDetail.get("ARTIFACTNO").toString();
                //PREPORTTIME(hhmmss转换为分钟保留2位小数)


                ColumnDataValue trainColumns=new ColumnDataValue();
                trainColumns.add("EID", DataValues.newString(req.geteId()));
                trainColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                trainColumns.add("SHOPID", DataValues.newString(req.getOrganizationNO()));

                trainColumns.add("REPORTNO", DataValues.newString(reportNo));
                trainColumns.add("ITEM", new DataValue(item, Types.INTEGER));

                // 上级来源
                trainColumns.add("OOTYPE", DataValues.newInteger(1)); // 固定为0
                trainColumns.add("OOFNO", new DataValue(reportDetail.get("BATCHTASKNO").toString(), Types.VARCHAR));
                trainColumns.add("OOITEM", new DataValue("", Types.INTEGER));

                // 商品与生产信息
                trainColumns.add("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                trainColumns.add("FEATURENO", new DataValue("", Types.VARCHAR));//todo
                trainColumns.add("PQTY", new DataValue(pQty, Types.DECIMAL));//todo
                trainColumns.add("PUNIT", new DataValue(pUnit, Types.VARCHAR));//todo

                // 工序信息（可能为空）
                trainColumns.add("PITEM", new DataValue(pItem, Types.INTEGER));
                trainColumns.add("PROCESSNO", new DataValue(processNo, Types.VARCHAR));
                trainColumns.add("SITEM", new DataValue("", Types.INTEGER));
                trainColumns.add("EQUIPNO", new DataValue(equipNo, Types.VARCHAR));
                trainColumns.add("EQTY", new DataValue(reportUserList.size(), Types.DECIMAL));
                trainColumns.add("LABORTIME", new DataValue(preportTime, Types.DECIMAL));
                trainColumns.add("MACHINETIME", new DataValue(ereportTime, Types.DECIMAL));

                // === 时间与操作人字段 ===
                trainColumns.add("ACCOUNTDATE", DataValues.newString(bDate)); // 记账日期
                trainColumns.add("BDATE", new DataValue(bDate, Types.VARCHAR)); // 单据日期
                trainColumns.add("SDATE", DataValues.newString(bDate)); // 系统日期

                trainColumns.add("CREATEOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
                trainColumns.add("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
                trainColumns.add("CREATETIME", new DataValue(lastmoditime, Types.DATE));
                trainColumns.add("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
                trainColumns.add("ACCOUNTOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
                trainColumns.add("ACCOUNTTIME", DataValues.newDate(lastmoditime));


                String[] trainColumnNames = trainColumns.getColumns().toArray(new String[0]);
                DataValue[] trainDataValues = trainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean trainib=new InsBean("DCP_WOREPORT_TRAN",trainColumnNames);
                trainib.addValues(trainDataValues);
                this.addProcessData(new DataProcessBean(trainib));

            }



        }


        this.doExecuteDataToDB();

        //DCP_MStockOutProcess
        if(Check.NotNull(mStockoutNo)) {
            ParseJson pj = new ParseJson();
            DCP_MStockOutProcessReq mstockReq = new DCP_MStockOutProcessReq();
            mstockReq.setServiceId("DCP_MStockOutProcess");
            mstockReq.setToken(req.getToken());
            DCP_MStockOutProcessReq.LevelRequest request = mstockReq.new LevelRequest();
            request.setMStockOutNo(mStockoutNo);
            request.setAccountDate(bDate);
            mstockReq.setRequest(request);

            String jsontemp = pj.beanToJson(mstockReq);

            //直接调用CRegisterDCP服务
            DispatchService ds = DispatchService.getInstance();
            String resXml = ds.callService(jsontemp, StaticInfo.dao);
            DCP_MStockOutProcessRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_MStockOutProcessRes>() {
            });
            if (resserver.isSuccess() == false) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "扣料单审核失败！");
            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessReportSubmitReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessReportSubmitReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessReportSubmitReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessReportSubmitReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ProcessReportSubmitReq> getRequestType() {
        return new TypeToken<DCP_ProcessReportSubmitReq>() {
        };
    }

    @Override
    protected DCP_ProcessReportSubmitRes getResponseType() {
        return new DCP_ProcessReportSubmitRes();
    }

    public  double convertToMinutes(String hhmmss) {

        if(Check.Null(hhmmss)){
            return 0;
        }

        // 解析 hhmmss
        int hours = Integer.parseInt(hhmmss.substring(0, 2));
        int minutes = Integer.parseInt(hhmmss.substring(2, 4));
        int seconds = Integer.parseInt(hhmmss.substring(4, 6));

        // 计算总分钟（含小数）
        double totalMinutes = hours * 60 + minutes + seconds / 60.0;

        // 保留两位小数
        BigDecimal bd = new BigDecimal(totalMinutes).setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }



}
