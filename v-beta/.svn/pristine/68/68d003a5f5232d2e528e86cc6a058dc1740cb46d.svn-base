package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.impl.TableEntityDao;
import com.dsc.spos.json.cust.req.DCP_WOPreGenerateReq;
import com.dsc.spos.json.cust.res.DCP_WOPreGenerateRes;
import com.dsc.spos.model.DcpProdscheduleDetail;
import com.dsc.spos.model.DcpProdscheduleDetailId;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_WOPreGenerate extends SPosAdvanceService<DCP_WOPreGenerateReq, DCP_WOPreGenerateRes> {

    @Override
    protected void processDUID(DCP_WOPreGenerateReq req, DCP_WOPreGenerateRes res) throws Exception {

        List<Map<String, Object>> dcpProdscheduleDetailList = doQueryData(getQuerySql(req), null);

        if (null == dcpProdscheduleDetailList || dcpProdscheduleDetailList.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未查询到对应的排产计划单！");
        }
        //开始前先检查传入单据状态，若不等于<>"0新建"状态不可执行！
        if (!"0".equals(dcpProdscheduleDetailList.get(0).get("STATUS").toString())) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非\"0新建\"，不可执行！");
        }

        String orgNo = dcpProdscheduleDetailList.get(0).get("ORGANIZATIONNO").toString();

        //获取当前所有的BOM信息
        List<Map<String, Object>> bomInfo = doQueryData(getBomQuerySql(req, orgNo, ""), null);
        //获取最大序号
        List<Map<String, Object>> maxItemInfo = doQueryData(getQueryMaxDetailItem(req), null);
        List<Map<String, Object>> groupInfo = doQueryData(getGroupSql(req), null);
        List<Map<String, Object>> prodTemplateInfo = doQueryData(getQueryTemplateSql(req), null);

        //开始处理：清空当前排产计划单号系统生成的相关数据
        //包括：
        //2.DCP_PRODSCHEDULE_GEN
        ColumnDataValue condition = new ColumnDataValue();
        condition.append("EID", DataValues.newString(req.geteId()));
        condition.append("BILLNO", DataValues.newString(req.getRequest().getBillNo()));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PRODSCHEDULE_GEN", condition)));

        //1.DCP_PRODSCHEDULE_DETAIL.SOURCE来源="2.生产指令展开"的明细
        condition.append("SOURCETYPE", DataValues.newString("2")); //全删除吧，由后面再插入回去
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PRODSCHEDULE_DETAIL", condition)));

        Map<String, Boolean> distinct = new HashMap<>();
        distinct.put("EID", true);
        distinct.put("BILLNO", true);
        distinct.put("ITEM", true);
        //回写模板编号
        for (Map<String, Object> oneSource : MapDistinct.getMap(dcpProdscheduleDetailList, distinct)) {
            ColumnDataValue pcondition = new ColumnDataValue();
            ColumnDataValue dcp_prodSchedule_source = new ColumnDataValue();
            pcondition.append("EID", DataValues.newString(oneSource.get("EID").toString()));
            pcondition.append("BILLNO", DataValues.newString(oneSource.get("BILLNO").toString()));
            pcondition.append("ITEM", DataValues.newString(oneSource.get("SITEM").toString()));

            String templateId = StringUtils.toString(
                    getTemplate(prodTemplateInfo,
                            oneSource.get("PLUNO").toString(),
                            oneSource.get("ORGANIZATIONNO").toString()).get("TEMPLATEID")
                    , ""
            );
            dcp_prodSchedule_source.add("PTEMPLATENO", DataValues.newString(templateId));
            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PRODSCHEDULE_SOURCE", pcondition, dcp_prodSchedule_source)));
        }
        int item = 0;
        if (null != maxItemInfo && !maxItemInfo.isEmpty()) {
            item = Integer.parseInt(maxItemInfo.get(0).get("ITEM").toString()); //获取最大序号
        }
        TableEntityDao entityDao = MySpringContext.getBean("entityDao");
        List<DcpProdscheduleDetail> entityList = entityDao.queryList(" SELECT * FROM DCP_PRODSCHEDULE_DETAIL WHERE EID='" + req.geteId() + "' and BILLNO='" + req.getRequest().getBillNo() + "' AND SOURCETYPE<>'2'", DcpProdscheduleDetail.class);

        for (int i = entityList.size() - 1; i >= 0; i--) {
            DcpProdscheduleDetail detail = entityList.get(i);
            entityList.remove(detail); //清除这笔产成品

            //获取对应的其它信息
            Map<String, Object> detailCondition = new HashMap<>();
            detailCondition.put("ITEM", detail.getId().getItem());
            Map<String, Object> moreInfo = MapDistinct.getWhereMap(dcpProdscheduleDetailList, detailCondition, true).get(0);

            //半成品展料并回写entityList
            expandDetail(detail,
                    item,
                    getTemplate(prodTemplateInfo, detail.getPluno(), detail.getOrganizationno()),
                    moreInfo,
                    entityList,
                    bomInfo,
                    groupInfo);

        }


        String semiwogentype = dcpProdscheduleDetailList.get(0).get("SEMIWOGENTYPE").toString();
        List<DcpProdscheduleDetail> genTemp = null;
        int gItem = 0;
        if ("1".equals(semiwogentype)) { //合并配料
            //根据品号去重数据
            genTemp = entityList.stream()
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(()
                            -> new TreeSet<>(Comparator.comparing(DcpProdscheduleDetail::getPluno))), ArrayList::new));

            //遍历数据，合并工单信息
            for (DcpProdscheduleDetail detail : genTemp) {
                //过滤信息
                List<DcpProdscheduleDetail> filter = entityList.stream().filter(s -> s.getPluno().equals(detail.getPluno())).collect(Collectors.toList());
                if (filter.size() > 1) {  //数量大于1则需要合并信息
                    BigDecimal baseQty = new BigDecimal(0);
                    BigDecimal pQty = new BigDecimal(0);
                    BigDecimal poQty = new BigDecimal(0);

                    for (DcpProdscheduleDetail oneData : filter) {
                        baseQty = baseQty.add(oneData.getBaseqty());
                        pQty = pQty.add(oneData.getPqty());
                        poQty = poQty.add(oneData.getPoqty());
                    }
                    detail.setBaseqty(baseQty);
                    detail.setPqty(pQty);
                    detail.setPoqty(poQty);

                }
            }

        } else {
            genTemp = new ArrayList<>(entityList);
        }


        for (DcpProdscheduleDetail detail : genTemp) {
            ColumnDataValue dcp_prodSchedule_gen = new ColumnDataValue();

            dcp_prodSchedule_gen.add("EID", DataValues.newString(req.geteId()));
            dcp_prodSchedule_gen.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
            dcp_prodSchedule_gen.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));
            dcp_prodSchedule_gen.add("ITEM", DataValues.newString(++item));
            dcp_prodSchedule_gen.add("PLUNO", DataValues.newString(detail.getPluno()));
            dcp_prodSchedule_gen.add("FEATURENO", DataValues.newString(detail.getFeatureno()));
            dcp_prodSchedule_gen.add("UPPLUNO", DataValues.newString(detail.getUppluno()));
            dcp_prodSchedule_gen.add("PGROUPNO", DataValues.newString(detail.getPgroupno()));

            dcp_prodSchedule_gen.add("DEPARTID", DataValues.newString(detail.getPgroupno()));
            dcp_prodSchedule_gen.add("PUNIT", DataValues.newString(detail.getPunit()));
            dcp_prodSchedule_gen.add("PQTY", DataValues.newString(detail.getPqty()));
            dcp_prodSchedule_gen.add("RDATE", DataValues.newString(DateFormatUtils.getPlainDate(detail.getRdate())));
            dcp_prodSchedule_gen.add("PREDAYS", DataValues.newString(detail.getPredays()));

            dcp_prodSchedule_gen.add("BEGINDATE", DataValues.newString(DateFormatUtils.getPlainDate(DateFormatUtils.addDay(detail.getRdate(), detail.getPredays() * -1))));
            dcp_prodSchedule_gen.add("ENDDATE", DataValues.newString(DateFormatUtils.getPlainDate(detail.getRdate())));

            dcp_prodSchedule_gen.add("TOWOQTY", DataValues.newString("0"));
            dcp_prodSchedule_gen.add("BASEUNIT", DataValues.newString(detail.getBaseunit()));
            dcp_prodSchedule_gen.add("BASEQTY", DataValues.newString(detail.getBaseqty()));
            dcp_prodSchedule_gen.add("UNITRATIO", DataValues.newString(detail.getUnitratio()));
            dcp_prodSchedule_gen.add("BOMNO", DataValues.newString(detail.getBomno()));
            dcp_prodSchedule_gen.add("VERSIONNUM", DataValues.newString(detail.getVersionnum()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_prodSchedule_gen", dcp_prodSchedule_gen)));

            //遍历回写gItem
            for (DcpProdscheduleDetail d : entityList) {
                if (d.getPluno().equals(detail.getPluno())) {
                    d.setGitem(gItem);
                }
            }
        }

        //遍历写入detail
        for (DcpProdscheduleDetail detail : entityList) {

            if ("2".equals(detail.getSourcetype())) {
                ColumnDataValue dcp_prodSchedule_detail = new ColumnDataValue();

                dcp_prodSchedule_detail.add("EID", DataValues.newString(detail.getId().getEid()));
                dcp_prodSchedule_detail.add("BILLNO", DataValues.newString(detail.getId().getBillno()));
                dcp_prodSchedule_detail.add("ITEM", DataValues.newString(detail.getId().getItem()));
                dcp_prodSchedule_detail.add("ORGANIZATIONNO", DataValues.newString(detail.getOrganizationno()));
                dcp_prodSchedule_detail.add("PLUNO", DataValues.newString(detail.getPluno()));
                dcp_prodSchedule_detail.add("FEATURENO", DataValues.newString(detail.getFeatureno()));
                dcp_prodSchedule_detail.add("UPPLUNO", DataValues.newString(detail.getUppluno()));
                dcp_prodSchedule_detail.add("RDATE", DataValues.newString(DateFormatUtils.getPlainDate(detail.getRdate())));
                dcp_prodSchedule_detail.add("PGROUPNO", DataValues.newString(detail.getPgroupno()));
                dcp_prodSchedule_detail.add("PUNIT", DataValues.newString(detail.getPunit()));
                dcp_prodSchedule_detail.add("PQTY", DataValues.newString(detail.getPqty()));
                dcp_prodSchedule_detail.add("POQTY", DataValues.newString(detail.getPoqty()));
                dcp_prodSchedule_detail.add("STOCKQTY", DataValues.newString(detail.getStockqty()));
                dcp_prodSchedule_detail.add("SHORTQTY", DataValues.newString(detail.getShortqty()));
                dcp_prodSchedule_detail.add("ADVICEQTY", DataValues.newString(detail.getAdviceqty()));
                dcp_prodSchedule_detail.add("MINQTY", DataValues.newString(detail.getMinqty()));
                dcp_prodSchedule_detail.add("MULQTY", DataValues.newString(detail.getMulqty()));
                dcp_prodSchedule_detail.add("REMAINTYPE", DataValues.newString(detail.getRemaintype()));
                dcp_prodSchedule_detail.add("PREDAYS", DataValues.newString(detail.getPredays()));
                dcp_prodSchedule_detail.add("BASEUNIT", DataValues.newString(detail.getBaseunit()));
                dcp_prodSchedule_detail.add("BASEQTY", DataValues.newString(detail.getBaseqty()));
                dcp_prodSchedule_detail.add("UNITRATIO", DataValues.newString(detail.getUnitratio()));
                dcp_prodSchedule_detail.add("MEMO", DataValues.newString(detail.getMemo()));
                dcp_prodSchedule_detail.add("BOMNO", DataValues.newString(detail.getBomno()));
                dcp_prodSchedule_detail.add("VERSIONNUM", DataValues.newString(detail.getVersionnum()));
                dcp_prodSchedule_detail.add("GITEM", DataValues.newString(detail.getGitem()));
                dcp_prodSchedule_detail.add("SOURCETYPE", DataValues.newString(detail.getSourcetype()));
                dcp_prodSchedule_detail.add("ODDVALUE", DataValues.newString(detail.getOddvalue()));
                dcp_prodSchedule_detail.add("SEMIWOTYPE", DataValues.newString(detail.getSemiwotype()));
                dcp_prodSchedule_detail.add("SEMIWODEPTTYPE", DataValues.newString(detail.getSemiwodepttype()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PRODSCHEDULE_DETAIL", dcp_prodSchedule_detail)));

            }

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    private void expandDetail(DcpProdscheduleDetail detail, int item, Map<String, Object> template, Map<String, Object> detailInfo, List<DcpProdscheduleDetail> detailList, List<Map<String, Object>> bomList, List<Map<String, Object>> groupInfo) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("PLUNO", detail.getPluno());

        List<Map<String, Object>> bom = MapDistinct.getWhereMap(bomList, condition, true);

//        String semiWoType = detailInfo.get("SEMIWOTYPE").toString();
        String semiWoType = StringUtils.toString(template.get("SEMIWOTYPE"), "");
        String semiWoDeptType = "";
        if (StringUtils.isEmpty(semiWoType)
                && CollectionUtils.isNotEmpty(bom)) {
            semiWoType = bom.get(0).get("SEMIWOTYPE").toString();
            semiWoDeptType = bom.get(0).get("SEMIWODEPTTYPE").toString();
        }
        if (StringUtils.isEmpty(detail.getSemiwotype())) {
            detail.setSemiwotype(semiWoType);
        }
        if (StringUtils.isEmpty(detail.getSemiwodepttype())) {
            detail.setSemiwodepttype(semiWoDeptType);
        }
        if (null == detail.getBaseqty()) {
            detail.setBaseqty(detail.getPqty().multiply(detail.getUnitratio()));
        }
//        if (!"2".equals(detail.getSourcetype()) && CollectionUtils.isNotEmpty(bom)) {
//            detail.setVersionnum(Integer.parseInt(bom.get(0).get("VERSIONNUM").toString()));
//        }

        detailList.add(detail);//写入信息
        if ("2".equals(semiWoType)) { //判断是否自动开立子工单
            if (CollectionUtils.isNotEmpty(bom)) {

                for (Map<String, Object> material : bom) { //循环物料判断是否半成品

                    condition = new HashMap<>();
                    condition.put("PLUNO", material.get("MATERIAL_PLUNO"));
                    List<Map<String, Object>> materialBom = MapDistinct.getWhereMap(bomList, condition, true);

                    if (CollectionUtils.isNotEmpty(materialBom)) { //半成品
                        condition = new HashMap<>();
                        condition.put("PLUNO", material.get("MATERIAL_PLUNO"));
                        List<Map<String, Object>> group = MapDistinct.getWhereMap(groupInfo, condition, false);

                        double unitRatio = Double.parseDouble(material.get("UNIT_RATIO").toString()); //这个比例是生产单位换算成基础单位的
                        double prodUnitRatio = Double.parseDouble(material.get("PRODUNITRATIO").toString()); //这个比例是生产单位换算成基础单位的

                        DcpProdscheduleDetail newDetail = new DcpProdscheduleDetail();
                        newDetail.setId(new DcpProdscheduleDetailId());
                        newDetail.getId().setEid(detail.getId().getEid());
                        newDetail.getId().setBillno(detail.getId().getBillno());
                        newDetail.getId().setItem(++item); //这里需要更改序号
                        newDetail.setOrganizationno(detail.getOrganizationno());
                        newDetail.setPluno(material.get("MATERIAL_PLUNO").toString());
                        newDetail.setFeatureno(" ");
                        newDetail.setUppluno(detail.getPluno());
                        newDetail.setRdate(DateFormatUtils.addDay(detail.getRdate(), detail.getPredays() * -1));
//                        newDetail.setRdate(detail.getRdate());

                        if (CollectionUtils.isNotEmpty(group)) {
                            newDetail.setPgroupno(group.get(0).get("PGROUPNO").toString());
                        } else {
                            newDetail.setPgroupno(detail.getPgroupno());
                        }

                        newDetail.setPunit(material.get("PROD_UNIT").toString());

                        double materialQty = Double.parseDouble(material.get("MATERIAL_QTY").toString()); //配方组成用量
                        double baseFactor = Double.parseDouble(material.get("BATCHQTY").toString()); //配方基数
                        double parentBase = Double.parseDouble(material.get("QTY").toString()); //主件底数

                        double pQty = detail.getPqty().doubleValue();

                        double poQty = pQty * materialQty / (parentBase * baseFactor) * unitRatio / prodUnitRatio;
                        //半成品产量=上阶品号所需产量*所在配方标准组成用量=主件生产量*配方组成用量/主件底数*配方基数
//                       上阶品号所需产量*所在配方标准组成用量=主件生产量PQTY*配方组成用量DCP_BOM_MATERIAL.MATERIAL_QTY/主件底数DCP_BOM_MATERIAL.QTY*配方基数DCP_BOM.BATCHQTY
                        newDetail.setPoqty(BigDecimal.valueOf(poQty));

                        double sQty = Double.parseDouble(material.get("SQTY").toString());

                        //基础单位转为生产单位用unitRatio的倒数
                        BigDecimal stockQty = new BigDecimal(sQty).divide(BigDecimal.valueOf(prodUnitRatio), 2, RoundingMode.HALF_UP);

                        newDetail.setStockqty(stockQty);

                        double minQty = Double.parseDouble(detailInfo.get("MINQTY").toString());
                        double mulQty = Double.parseDouble(detailInfo.get("MULQTY").toString());

                        minQty = minQty * unitRatio / prodUnitRatio;
                        mulQty = mulQty * unitRatio / prodUnitRatio;

                        newDetail.setMinqty(BigDecimal.valueOf(minQty));
                        newDetail.setMulqty(BigDecimal.valueOf(mulQty));
                        newDetail.setRemaintype(detailInfo.get("REMAINTYPE").toString());

                        if (newDetail.getPoqty().compareTo(newDetail.getMinqty()) < 0) {
                            //按最小生产量
                            newDetail.setAdviceqty(newDetail.getMinqty());
                        } else {
//                            1.抹除 2.无条件进一 3.四舍五入 4.保留
                            BigDecimal mul = newDetail.getPoqty();
//                            String remainType = detailInfo.get("REMAINTYPE").toString();
                            String remainType = StringUtils.toString(template.get("REMAINTYPE"), "4");

                            if ("4".equals(remainType)) {
                                newDetail.setAdviceqty(newDetail.getPoqty());
                            } else {

                                switch (remainType) {
                                    case "1":
                                        mul = mul.divide(newDetail.getMulqty(), 0, RoundingMode.CEILING);
                                        break;
                                    case "2":
                                        mul = mul.divide(newDetail.getMulqty(), 0, RoundingMode.UP);
                                        break;
                                    case "3":
                                        mul = mul.divide(newDetail.getMulqty(), 0, RoundingMode.HALF_UP);
                                        break;

                                }
                                newDetail.setAdviceqty(mul.multiply(newDetail.getMulqty()));
                            }

                        }
                        newDetail.setPqty(newDetail.getAdviceqty());

                        if (newDetail.getPoqty().compareTo(newDetail.getStockqty()) > 0) {
                            newDetail.setShortqty(newDetail.getPoqty().subtract(newDetail.getStockqty()));
                        } else {
                            newDetail.setShortqty(BigDecimal.ZERO);
                        }

                        newDetail.setBaseqty(newDetail.getPqty().multiply(BigDecimal.valueOf(prodUnitRatio)));

                        newDetail.setPredays(Integer.valueOf(detailInfo.get("PREDAYS").toString()));
                        newDetail.setBaseunit(material.get("BASEUNIT").toString());

                        newDetail.setUnitratio(BigDecimal.valueOf(prodUnitRatio));
                        newDetail.setMemo("工单预生成创建");
                        newDetail.setBomno(materialBom.get(0).get("BOMNO").toString());
                        newDetail.setVersionnum(Integer.valueOf(materialBom.get(0).get("VERSIONNUM").toString()));
//                        newDetail.setVersionnum(Integer.valueOf(material.get("VERSIONNUM").toString()));
                        newDetail.setSourcetype("2");
                        newDetail.setOddvalue(detail.getOddvalue());
                        newDetail.setSemiwotype(material.get("SEMIWOTYPE").toString());
                        newDetail.setSemiwodepttype(material.get("SEMIWODEPTTYPE").toString());

                        //半成品继续展料
                        expandDetail(newDetail, item, template, detailInfo, detailList, materialBom, groupInfo);
                    }
                }
            }
        }
    }


    //获取模板信息
    private Map<String, Object> getTemplate(List<Map<String, Object>> templateList, String pluNo, String orgNo) {
        if (CollectionUtils.isEmpty(templateList)) {
            return new HashMap<>();
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("PLUNO", pluNo);
        condition.put("ORGANIZATIONNO", orgNo);
        List<Map<String, Object>> getData = MapDistinct.getWhereMap(templateList, condition, true);
        if (CollectionUtils.isEmpty(getData)) {
            condition.clear();
            condition.put("PLUNO", pluNo);
            getData = MapDistinct.getWhereMap(templateList, condition, false);
        }
        if (CollectionUtils.isEmpty(getData)) {
            return new HashMap<>();
        }

        return getData.get(0);

    }


    private String getQueryMaxDetailItem(DCP_WOPreGenerateReq req) throws Exception {
        return " SELECT MAX(ITEM) ITEM FROM DCP_PRODSCHEDULE_DETAIL WHERE" +
                " EID='" + req.geteId() + "' and BILLNO='" + req.getRequest().getBillNo() + "'" +
                " AND SOURCETYPE<>'2' "
                ;
    }

    private String getBomQuerySql(DCP_WOPreGenerateReq req, String orgNo, String versionNum) throws Exception {

        StringBuilder stringBuilder = new StringBuilder();

        if (StringUtils.isEmpty(versionNum)) {
            stringBuilder.append(" SELECT a.BOMNO,a.VERSIONNUM,a.PLUNO,a.UNIT,a.MULQTY,b.MATERIAL_PLUNO,a.BATCHQTY ")
                    .append(" ,b.MATERIAL_UNIT,b.MATERIAL_QTY,b.QTY,c.UNITRATIO UNIT_RATIO,stock.SQTY,a.SEMIWOTYPE,a.SEMIWODEPTTYPE ")
                    .append(" ,d.BASEUNIT,d.PROD_UNIT,e.UNITRATIO PRODUNITRATIO ")
                    .append(" FROM DCP_BOM a ")
                    .append(" LEFT JOIN DCP_BOM_MATERIAL b ON a.eid=b.eid and a.BOMNO=b.BOMNO  ")
                    .append(" LEFT JOIN DCP_GOODS d on b.EID=d.EID and d.PLUNO=b.MATERIAL_PLUNO ")
                    .append(" LEFT JOIN DCP_GOODS_UNIT c on c.eid=b.eid and c.PLUNO=b.MATERIAL_PLUNO and c.OUNIT=b.MATERIAL_UNIT and c.UNIT=d.baseunit ")
                    .append(" LEFT JOIN DCP_GOODS_UNIT e on e.eid=b.eid and e.PLUNO=b.MATERIAL_PLUNO and e.OUNIT=d.PROD_UNIT and e.UNIT=d.baseunit ")
                    .append(" left join( ").append(getQueryStockSql(req, orgNo)).append(" ) stock on stock.eid=b.eid and stock.PLUNO=b.MATERIAL_PLUNO  ")
                    .append(" WHERE a.EID='").append(req.geteId()).append("' and a.STATUS='100' and EFFDATE<=sysdate ")
            ;
        } else {
            stringBuilder.append(" SELECT a.BOMNO,a.VERSIONNUM,a.PLUNO,a.UNIT,a.MULQTY,b.MATERIAL_PLUNO,a.BATCHQTY ")
                    .append(" ,b.MATERIAL_UNIT,b.MATERIAL_QTY,b.QTY,c.UNITRATIO UNIT_RATIO,stock.SQTY,a.SEMIWOTYPE,a.SEMIWODEPTTYPE ")
                    .append(" ,d.BASEUNIT,d.PROD_UNIT,e.UNITRATIO PRODUNITRATIO ")
                    .append(" FROM DCP_BOM_V a ")
                    .append(" LEFT JOIN DCP_BOM_MATERIAL_V b ON a.eid=b.eid and a.BOMNO=b.BOMNO  ")
                    .append(" LEFT JOIN DCP_GOODS d on b.EID=d.EID and d.PLUNO=b.MATERIAL_PLUNO ")
                    .append(" LEFT JOIN DCP_GOODS_UNIT c on c.eid=b.eid and c.PLUNO=b.MATERIAL_PLUNO and c.OUNIT=b.MATERIAL_UNIT and c.unit=d.baseunit ")
                    .append(" LEFT JOIN DCP_GOODS_UNIT e on e.eid=b.eid and e.PLUNO=b.MATERIAL_PLUNO and e.OUNIT=d.PROD_UNIT and e.UNIT=d.baseunit ")
                    .append(" left join( ").append(getQueryStockSql(req, orgNo)).append(" ) stock on stock.eid=b.eid and stock.PLUNO=b.MATERIAL_PLUNO  ")
                    .append(" WHERE a.EID='").append(req.geteId()).append("' and VERSIONNUM='").append(versionNum).append("'")
            ;
        }

        return stringBuilder.toString();
    }

    private String getGroupSql(DCP_WOPreGenerateReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT b.* FROM MES_PRODUCT_GROUP a" +
                        " INNER JOIN  MES_PRODUCT_GROUP_GOODS b ON a.eid=b.eid and a.PGROUPNO=b.PGROUPNO" +
                        " WHERE a.eid='").append(req.geteId()).append("'")
                .append(" AND a.STATUS ='100' ");

        return querySql.toString();
    }

    private String getQueryStockSql(DCP_WOPreGenerateReq req, String orgNo) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.EID,a.ORGANIZATIONNO,PLUNO, ")
                .append(" SUM(QTY-LOCKQTY-ONLINEQTY) SQTY ")
                .append(" FROM DCP_STOCK a ")
                .append(" LEFT JOIN DCP_WAREHOUSE b ON a.EID=b.EID and a.WAREHOUSE=b.WAREHOUSE ")
                .append(" WHERE a.EID='").append(req.geteId()).append("' ")
                .append(" AND a.ORGANIZATIONNO='").append(orgNo).append("' ")
                .append("  and b.WAREHOUSE_TYPE <>'3' and b.WAREHOUSE_TYPE<>'5'  ")
                .append(" group by a.ORGANIZATIONNO, PLUNO, a.EID ");

        return querySql.toString();
    }

    @Override
    protected String getQuerySql(DCP_WOPreGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ")
                .append(" a.STATUS,b.*,c.PTEMPLATENO,b.PREDAYS ")
                .append(" ,a.SEMIWOGENTYPE,c.ITEM SITEM ")
//                .append(" ,d.SEMIWOTYPE,d.TEMPLATEID,d.PRODMINQTY,d.PRODMULQTY,d.REMAINTYPE ")
                .append(" FROM DCP_PRODSCHEDULE a ")
                .append(" INNER JOIN DCP_PRODSCHEDULE_DETAIL b on a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.billno=b.billno ")
//                .append(" left join( ").append(getQueryStockSql(req)).append(" ) stock on stock.eid=b.eid and stock.ORGANIZATIONNO=b.ORGANIZATIONNO and stock.PLUNO=b.PLUNO and stock.FEATURENO=b.FEATURENO ")
                .append(" left join DCP_PRODSCHEDULE_SOURCE c on c.eid=b.eid and c.BILLNO=b.BILLNO and c.ORGANIZATIONNO=b.ORGANIZATIONNO and c.OITEM=b.ITEM  ")
//                .append(" left join DCP_PRODTEMPLATE_GOODS d on d.eid=c.eid and d.PLUNO=c.PLUNO ")

        ;
        sb.append(" WHERE a.eid='").append(req.geteId()).append("'");
        sb.append(" and a.billno='").append(req.getRequest().getBillNo()).append("'");
        sb.append(" AND b.SOURCETYPE<>'2' ");

        return sb.toString();
    }

    private String getQueryTemplateSql(DCP_WOPreGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT b.SEMIWOTYPE,b.TEMPLATEID,b.PRODMINQTY,b.PRODMULQTY,b.REMAINTYPE,b.PLUNO,c.ORGANIZATIONNO ")
                .append(" FROM DCP_PRODTEMPLATE a ")
                .append(" LEFT JOIN DCP_PRODTEMPLATE_GOODS b on a.eid=b.eid and a.TEMPLATEID=b.TEMPLATEID ")
                .append(" LEFT JOIN DCP_PRODTEMPLATE_RANGE c on a.eid=c.eid and a.TEMPLATEID=c.TEMPLATEID ")
        ;
        sb.append(" WHERE a.eid='").append(req.geteId()).append("'");
//        sb.append(" and d.billno='").append(req.getRequest().getBillNo()).append("'");
        sb.append(" and exists( SELECT EID,PLUNO FROM DCP_PRODSCHEDULE_SOURCE d "
                        + " WHERE b.PLUNO=d.PLUNO and b.eid=d.eid "
                        + " AND d.BILLNO='").append(req.getRequest().getBillNo()).append("'")
                .append(" ) ");

        return sb.toString();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WOPreGenerateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WOPreGenerateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WOPreGenerateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_WOPreGenerateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_WOPreGenerateReq> getRequestType() {
        return new TypeToken<DCP_WOPreGenerateReq>() {
        };
    }

    @Override
    protected DCP_WOPreGenerateRes getResponseType() {
        return new DCP_WOPreGenerateRes();
    }
}