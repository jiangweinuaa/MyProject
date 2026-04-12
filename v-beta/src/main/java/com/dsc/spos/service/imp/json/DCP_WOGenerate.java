package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WOGenerateReq;
import com.dsc.spos.json.cust.res.DCP_WOGenerateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.bc.BaseUnitCalculate;
import com.google.gson.reflect.TypeToken;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_WOGenerate extends SPosAdvanceService<DCP_WOGenerateReq, DCP_WOGenerateRes> {
    @Override
    protected void processDUID(DCP_WOGenerateReq req, DCP_WOGenerateRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String opNo = req.getOpNO();
        String departmentNo = req.getDepartmentNo();
        String createDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());

        String  pGroupSql="select * from MES_PRODUCT_GROUP where eid='"+req.geteId()+"' ";
        List<Map<String, Object>> pGroupList = this.doQueryData(pGroupSql, null);

        List<BaseUnitCalculate> bucList = req.getRequest().getDatas().stream().map(x -> {
            BaseUnitCalculate bc = new BaseUnitCalculate();
            bc.setPluNo(x.getPluNo());
            bc.setUnit(x.getPUnit());
            bc.setQty(x.getPQty());
            bc.setBaseUnit("");
            bc.setBaseQty("");
            bc.setUnitRatio("");
            return bc;
        }).collect(Collectors.toList());
        bucList= PosPub.getBaseQtyList(dao, eId, bucList);

        List<BaseUnitCalculate> finalBucList = bucList;
        List<Map<String, Object>> plus = new ArrayList<Map<String, Object>>();
        StringBuffer sJoinPluno=new StringBuffer("");
        req.getRequest().getDatas().forEach(x->{
            List<BaseUnitCalculate> collect = finalBucList.stream().filter(y -> y.getPluNo().equals(x.getPluNo()) && y.getUnit().equals(x.getPUnit())).collect(Collectors.toList());
            if(collect.size()>0){
                x.setBaseQty(collect.get(0).getBaseQty());
                x.setBaseUnit(collect.get(0).getBaseUnit());
                x.setUnitRatio(collect.get(0).getUnitRatio());
            }

            Map<String, Object> plu = new HashMap<String, Object>();
            plu.put("PLUNO", x.getPluNo());
            plu.put("PUNIT", x.getPUnit());
            plu.put("BASEUNIT", x.getBaseUnit());
            plu.put("UNITRATIO",x.getUnitRatio());
            plu.put("SUPPLIERID", "");//算零售价用不上这个
            plus.add(plu);

            sJoinPluno.append(x.getPluNo()+",");

        });
        MyCommon mc = new MyCommon();
        List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, req.geteId(), req.getBELFIRM(), "", plus,"");

        //生管模板数据 + bom数据
        Map<String, String> mapOrder=new HashMap<String, String>();
        mapOrder.put("PLUNO", sJoinPluno.toString());
        String withasSql_mono=mc.getFormatSourceMultiColWith(mapOrder);
        String templateSql=" with p as ("+withasSql_mono+")" +
                " select a.* " +
                " from DCP_PRODTEMPLATE_GOODS a " +
                " inner join p on p.pluno=a.pluno" +
                " inner join DCP_PRODTEMPLATE b on a.eid=b.eid and a.templateid=b.templateid and b.RESTRICTORG='0' " +
                " where a.status='100' and b.status='100' " +
                " union all(" +
                " select a.* " +
                " from DCP_PRODTEMPLATE_GOODS a " +
                " inner join p on p.pluno=a.pluno" +
                " inner join DCP_PRODTEMPLATE b on a.eid=b.eid and a.templateid=b.templateid and b.RESTRICTORG='1' " +
                " inner join DCP_PRODTEMPLATE_RANGE c on c.eid=a.eid and c.templateid=a.templateid " +
                " where a.status='100' and b.status='100' and c.status='100' and c.organizationno='"+req.getOrganizationNO()+"'  " +
                " ) ";
        List<Map<String, Object>> templateList = this.doQueryData(templateSql, null);

        String bomSql=" with p as ("+withasSql_mono+") " +
                " select a.* from dcp_bom a " +
                " inner join p on p.pluno=a.pluno " +
                " where a.eid='"+eId+"' and a.status='100'" ;
        List<Map<String, Object>> bomList = this.doQueryData(bomSql, null);


        List<DCP_WOGenerateReq.Datas> datas = req.getRequest().getDatas();
        List<MoInfo> moInfos = datas.stream().map(x -> {
            MoInfo moInfo = new MoInfo();
            moInfo.setBillNo(x.getBillNo());
            moInfo.setPGroupNo(x.getPGroupNo());
            moInfo.setProdType(x.getProdType());
            return moInfo;
        }).distinct().collect(Collectors.toList());

        for (MoInfo moInfo : moInfos) {
            //DCP_PROCESSTASK_DETAIL


            List<Map<String, Object>> pGroupInfo = pGroupList.stream().filter(y -> y.get("PGROUPNO").toString().equals(moInfo.getPGroupNo())).collect(Collectors.toList());

            String moNo = this.getOrderNO(req, "JGRW");
            ColumnDataValue mainColumns=new ColumnDataValue();
            mainColumns.add("EID", DataValues.newString(eId));
            mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
            mainColumns.add("SHOPID", DataValues.newString(organizationNO));
            mainColumns.add("PROCESSTASKNO", DataValues.newString(moNo));
            mainColumns.add("PDATE", DataValues.newString(createDate));
            mainColumns.add("BDATE", DataValues.newString(createDate));
            mainColumns.add("STATUS", DataValues.newString("6"));
            mainColumns.add("OTYPE", DataValues.newString("SP"));
            mainColumns.add("OFNO", DataValues.newString(moInfo.getBillNo()));
            mainColumns.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
            if(pGroupInfo.size()>0){
                mainColumns.add("DEPARTID", DataValues.newString(pGroupInfo.get(0).get("DEPARTID").toString()));
            }
            else{
                mainColumns.add("DEPARTID", DataValues.newString(""));
            }
            mainColumns.add("PRODTYPE", DataValues.newString(moInfo.getProdType()));

            mainColumns.add("CREATEOPID", DataValues.newString(opNo));
            mainColumns.add("CREATETIME", DataValues.newDate(createDateTime));
            mainColumns.add("LASTMODIOPID", DataValues.newString(opNo));
            mainColumns.add("LASTMODITIME", DataValues.newDate(createDateTime));

            mainColumns.add("CREATEBY", DataValues.newString(opNo));
            mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
            mainColumns.add("CREATE_TIME", DataValues.newString(createTime));

            mainColumns.add("MODIFYBY", DataValues.newString(opNo));
            mainColumns.add("MODIFY_DATE", DataValues.newString(createDate));
            mainColumns.add("MODIFY_TIME", DataValues.newString(createTime));

            BigDecimal totPqty=new BigDecimal(0);
            BigDecimal totCqty=new BigDecimal(0);
            BigDecimal totAmt=new BigDecimal(0);

            List<DCP_WOGenerateReq.Datas> filterRows = datas.stream().filter(y -> y.getBillNo().equals(moInfo.getBillNo())
                    && y.getPGroupNo().equals(moInfo.getPGroupNo())
                    && y.getProdType().equals(moInfo.getProdType())
            ).collect(Collectors.toList());
            int item=0;

            List<Map> collect = filterRows.stream().map(x -> {
                Map map = new HashMap<>();
                map.put("PLUNO", x.getPluNo());
                map.put("FEATURENO", x.getFeatureNo());
                return map;
            }).distinct().collect(Collectors.toList());
            totCqty=new BigDecimal(collect.size());

            for (DCP_WOGenerateReq.Datas row : filterRows){
                item++;

                //Map<String, Object> mapBase1 = PosPub.getBaseQty(dao, req.geteId(), row.getPluNo(), row.getPUnit(), row.getPQty());
                //if (mapBase1 == null)
                //{
                //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+row.getPluNo()+",OUNIT="+row.getPUnit()+"无记录！");
                //}

                //MINQTY，MULQTY,DISPTYPE，SEMIWOTYPE，ODDVALUE，REMAINTYPE
                String minQty=row.getMinQty();
                String mulQty=row.getMulQty();
                String dispType="";
                String semiWoType="";
                String oddValue="";
                String remainType="";
                List<Map<String, Object>> filterRows1 = templateList.stream().filter(x -> x.get("PLUNO").toString().equals(row.getPluNo())).collect(Collectors.toList());
                if(filterRows1.size()>0){
                    if(Check.Null(minQty)){
                        minQty=filterRows1.get(0).get("PRODMINQTY").toString();
                    }
                    if(Check.Null(mulQty)){
                        mulQty=filterRows1.get(0).get("PRODMULQTY").toString();
                    }
                    dispType=filterRows1.get(0).get("DISPTYPE").toString();
                    semiWoType=filterRows1.get(0).get("SEMIWOTYPE").toString();
                    oddValue=filterRows1.get(0).get("ODDVALUE").toString();
                    remainType=filterRows1.get(0).get("REMAINTYPE").toString();
                }else{
                    List<Map<String, Object>> filterRows2 = bomList.stream().filter(x -> x.get("PLUNO").toString().equals(row.getPluNo())).collect(Collectors.toList());
                    if(filterRows2.size()>0){
                        if(Check.Null(minQty)){
                            minQty=filterRows2.get(0).get("MINQTY").toString();
                        }
                        if(Check.Null(mulQty)){
                            mulQty=filterRows2.get(0).get("MULQTY").toString();
                        }
                        dispType=filterRows2.get(0).get("DISPTYPE").toString();
                        semiWoType=filterRows2.get(0).get("SEMIWOTYPE").toString();
                        oddValue=filterRows2.get(0).get("ODDVALUE").toString();
                        remainType=filterRows2.get(0).get("REMAINTYPE").toString();
                    }
                }


                String price="0";
                Map<String, Object> condiV = new HashMap<String, Object>();
                condiV.put("PLUNO", row.getPluNo());
                condiV.put("PUNIT", row.getPUnit());
                List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                if (priceList != null && priceList.size() > 0) {
                    price = priceList.get(0).get("PRICE").toString();
                }
                BigDecimal amt=new BigDecimal(price).multiply(new BigDecimal(row.getPQty()));
                totAmt=totAmt.add(amt);
                totPqty=totPqty.add(new BigDecimal(row.getPQty()));

                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                detailColumns.add("SHOPID", DataValues.newString(organizationNO));
                detailColumns.add("PROCESSTASKNO", DataValues.newString(moNo));
                detailColumns.add("ITEM", DataValues.newInteger(item));
                detailColumns.add("PLUNO", DataValues.newString(row.getPluNo()));
                detailColumns.add("FEATURENO", DataValues.newString(row.getFeatureNo()));
                detailColumns.add("PUNIT", DataValues.newString(row.getPUnit()));
                detailColumns.add("PQTY", DataValues.newString(row.getPQty()));
                detailColumns.add("BASEUNIT", DataValues.newString(row.getBaseUnit()));
                detailColumns.add("BASEQTY", DataValues.newString(row.getBaseQty()));
                detailColumns.add("UNIT_RATIO", DataValues.newString(row.getUnitRatio()));
                detailColumns.add("GOODSSTATUS", DataValues.newString("0"));
                detailColumns.add("DISPATCHSTATUS", DataValues.newString("0"));
                detailColumns.add("BEGINDATE", DataValues.newString(row.getBeginDate()));
                detailColumns.add("ENDDATE", DataValues.newString(row.getEndDate()));
                detailColumns.add("BOMNO", DataValues.newString(row.getBomNo()));
                detailColumns.add("VERSIONNUM", DataValues.newString(row.getVersionNum()));
                detailColumns.add("PICKSTATUS", DataValues.newString("N"));
                detailColumns.add("MINQTY", DataValues.newString(minQty));
                detailColumns.add("MUL_QTY", DataValues.newString(mulQty));
                detailColumns.add("BDATE", DataValues.newString(createDate));
                detailColumns.add("OITEM", DataValues.newInteger(row.getItem()));

                detailColumns.add("DISTRIPRICE", DataValues.newString("0"));
                detailColumns.add("DISTRIAMT", DataValues.newString("0"));
                detailColumns.add("PRICE", DataValues.newString(price));
                detailColumns.add("AMT", DataValues.newString(amt));

                ////MINQTY，MULQTY，DISPTYPE，SEMIWOTYPE，ODDVALUE，REMAINTYPE
                // 以上字段按 适用组织生管模板→全部组织生管模板→配方 优先级给值
                detailColumns.add("DISPTYPE", DataValues.newString(dispType));
                detailColumns.add("SEMIWOTYPE", DataValues.newString(semiWoType));
                detailColumns.add("ODDVALUE", DataValues.newString(oddValue));
                detailColumns.add("REMAINTYPE", DataValues.newString(remainType));
                detailColumns.add("OFNO", DataValues.newString(moInfo.getBillNo()));


                String[] detailColumnNames =detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ibDetail=new InsBean("DCP_PROCESSTASK_DETAIL",detailColumnNames);
                ibDetail.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(ibDetail));
            }

            mainColumns.add("TOT_PQTY", DataValues.newString(totPqty.toString()));
            mainColumns.add("TOT_CQTY", DataValues.newString(totCqty.toString()));
            mainColumns.add("TOT_AMT", DataValues.newString(totAmt.toString()));



            String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
            DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ib=new InsBean("DCP_PROCESSTASK",mainColumnNames);
            ib.addValues(mainDataValues);
            this.addProcessData(new DataProcessBean(ib));
        }

        //DCP_PRODSCHEDULE_GEN
        List<String> billNoList = datas.stream().map(x -> "'" + x.getBillNo() + "'").distinct().collect(Collectors.toList());
        List<Map<String, Object>> pScheduleList = this.doQueryData("select * from DCP_PRODSCHEDULE_GEN where BILLNO in (" + String.join(",", billNoList) + ")", null);
        if(CollUtil.isNotEmpty(pScheduleList)){
            //2.根据billNo和item更新DCP_PRODSCHEDULE_GEN表TOWOQTY已下发量；
            //更新后检查DCP_PRODSCHEDULE_GEN表是否存在TOWOQTY<PQTY 的数量，如存在则更新DCP_PRODSCHEDULE.STATUS为3.部分下发，不存在则更新为2.全部下发
            List<GenInfo> genList = pScheduleList.stream().map(y -> {
                String billNo = y.get("BILLNO").toString();
                String item = y.get("ITEM").toString();
                BigDecimal pQty = new BigDecimal(y.get("PQTY").toString());
                BigDecimal totWoQty = new BigDecimal(y.get("TOWOQTY").toString());
                GenInfo genInfo = new GenInfo();
                genInfo.setBillNo(billNo);
                genInfo.setItem(item);
                genInfo.setPQty(pQty);
                genInfo.setTotWoQty(totWoQty);
                return genInfo;
            }).distinct().collect(Collectors.toList());

            for (DCP_WOGenerateReq.Datas singleData  : datas){
                String billNo = singleData.getBillNo();
                String item = singleData.getItem();
                String pQty = singleData.getPQty();
                List<Map<String, Object>> filterSchedule = pScheduleList.stream().filter(y -> y.get("BILLNO").toString().equals(billNo)
                        && y.get("ITEM").toString().equals(item)).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(filterSchedule)){
                    BigDecimal toWoQty = new BigDecimal(filterSchedule.get(0).get("TOWOQTY").toString());
                    toWoQty=toWoQty.add(new BigDecimal(pQty));
                    UptBean ub1 = new UptBean("DCP_PRODSCHEDULE_GEN");
                    ub1.addUpdateValue("TOWOQTY", new DataValue(toWoQty, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));

                    BigDecimal finalToWoQty = toWoQty;
                    genList.forEach(genInfo -> {
                        if(genInfo.getBillNo().equals(billNo)&&genInfo.getItem().equals(item)){
                            genInfo.setTotWoQty(finalToWoQty);
                        }
                    });
                }

            }

            List<String> billNoList2 = datas.stream().map(x -> x.getBillNo() ).distinct().collect(Collectors.toList());
            for (String billNo : billNoList2){
                List<GenInfo> filterCollect = genList.stream().filter(x -> x.getBillNo().equals(billNo) && x.getTotWoQty().compareTo(x.getPQty()) < 0).collect(Collectors.toList());
                if(filterCollect.size()>0){
                    UptBean ub1 = new UptBean("DCP_PRODSCHEDULE");
                    ub1.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }else{
                    UptBean ub1 = new UptBean("DCP_PRODSCHEDULE");
                    ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }
            }
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WOGenerateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WOGenerateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WOGenerateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WOGenerateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空值,");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }



        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_WOGenerateReq> getRequestType() {
        return new TypeToken<DCP_WOGenerateReq>(){};
    }

    @Override
    protected DCP_WOGenerateRes getResponseType() {
        return new DCP_WOGenerateRes();
    }

    @Data
    public class MoInfo{
        private String pGroupNo;
        private String billNo;
        private String prodType;
    }

    @Data
    public class GenInfo{
        private String billNo;
        private String item;
        private BigDecimal pQty;
        private BigDecimal totWoQty;
    }

}
