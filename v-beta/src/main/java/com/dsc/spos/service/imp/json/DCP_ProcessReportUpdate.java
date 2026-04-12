package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.impl.TableEntityDao;
import com.dsc.spos.json.cust.req.DCP_ProcessReportCreateReq;
import com.dsc.spos.json.cust.req.DCP_ProcessReportUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ProcessReportUpdateRes;
import com.dsc.spos.model.DcpGood;
import com.dsc.spos.model.DcpGoodsUnit;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ProcessReportUpdate extends SPosAdvanceService<DCP_ProcessReportUpdateReq,DCP_ProcessReportUpdateRes> {
    @Override
    protected void processDUID(DCP_ProcessReportUpdateReq req, DCP_ProcessReportUpdateRes res) throws Exception {
        ColumnDataValue mes_process_report = new ColumnDataValue();

        String reportNo = req.getRequest().getReportNo();

        ColumnDataValue condition = new ColumnDataValue();

        condition.append("EID", DataValues.newString(req.geteId()));
        condition.append("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        condition.append("REPORTNO", DataValues.newString(reportNo));

        mes_process_report.append("MPLUNO", DataValues.newString(req.getRequest().getPluNo()));
        mes_process_report.append("MACHINE", DataValues.newString(req.getRequest().getMachine()));
        mes_process_report.append("STATION", DataValues.newString(req.getRequest().getStation()));
        mes_process_report.append("PITEM", DataValues.newString(req.getRequest().getPItem()));
        mes_process_report.append("PROCESSNO", DataValues.newString(req.getRequest().getProcessNo()));
        mes_process_report.append("ARTIFACTTYPE", DataValues.newString(req.getRequest().getArtifactType()));
        mes_process_report.append("ARTIFACTNO", DataValues.newString(req.getRequest().getArtifactNo()));
        mes_process_report.append("ARTIFACTNAME", DataValues.newString(req.getRequest().getArtifactName()));
        mes_process_report.append("PPUNIT", DataValues.newString(req.getRequest().getPPUnit()));
        mes_process_report.append("PPQTY", DataValues.newString(req.getRequest().getPPQty()));
        mes_process_report.append("TOTPPQTY", DataValues.newString(req.getRequest().getTotPPQty()));
        mes_process_report.append("PASSQTY", DataValues.newString(req.getRequest().getPassQty()));
        mes_process_report.append("SCRAPQTY", DataValues.newString(req.getRequest().getScrapQty()));
        mes_process_report.append("STATUS", DataValues.newString(req.getRequest().getStatus()));

        mes_process_report.append("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        mes_process_report.append("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));
        mes_process_report.append("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

        mes_process_report.append("BATCHNO", DataValues.newString(req.getRequest().getBatchNo()));
        mes_process_report.append("BATCHTASKNO", DataValues.newString(req.getRequest().getBatchTaskNo()));
        mes_process_report.append("STARTTIME", DataValues.newString(DateFormatUtils.getNowPlainDatetime()));
        mes_process_report.append("BDATE", DataValues.newString(req.getRequest().getBDate()));

        //修改
        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("MES_PROCESS_REPORT",condition,mes_process_report)));

        //清空
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT_DETAIL",condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT_MATERIAL",condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT_SCRAP",condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT_USER",condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_PROCESS_REPORT_MO",condition)));

        //写入
        int item = 1;
        for (DCP_ProcessReportUpdateReq.Datas oneData : req.getRequest().getDatas()) {

            ColumnDataValue mes_process_report_detail = new ColumnDataValue();
            mes_process_report_detail.add("EID", DataValues.newString(req.geteId()));
            mes_process_report_detail.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
            mes_process_report_detail.add("REPORTNO", DataValues.newString(reportNo));
            mes_process_report_detail.add("ITEM", DataValues.newString(item++));
            mes_process_report_detail.add("SITEM", DataValues.newString(oneData.getSItem()));
            mes_process_report_detail.add("PREPORTTIME", DataValues.newString(oneData.getPReportTime()));
            mes_process_report_detail.add("EREPORTTIME", DataValues.newString(oneData.getEReportTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_PROCESS_REPORT_DETAIL", mes_process_report_detail)));

            TableEntityDao entityDao = MySpringContext.getBean("entityDao");

            for (DCP_ProcessReportUpdateReq.MaterialList oneMaterial : oneData.getMaterialList()) {

                ColumnDataValue mes_process_report_material = new ColumnDataValue();
                mes_process_report_material.add("EID", DataValues.newString(req.geteId()));
                mes_process_report_material.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                mes_process_report_material.add("REPORTNO", DataValues.newString(reportNo));
                mes_process_report_material.add("ITEM", DataValues.newString(item));
                mes_process_report_material.add("SITEM", DataValues.newString(oneData.getSItem()));
                mes_process_report_material.add("ZITEM", DataValues.newString(oneMaterial.getZItem()));
                mes_process_report_material.add("MATERIAL_TYPE", DataValues.newString(oneMaterial.getMaterialType()));
                mes_process_report_material.add("MATERIAL_PLUNO", DataValues.newString(oneMaterial.getMaterialPluNo()));
                mes_process_report_material.add("MATERIAL_NAME", DataValues.newString(oneMaterial.getMaterialPluName()));
                mes_process_report_material.add("MATERIAL_UNIT", DataValues.newString(oneMaterial.getMaterialUnit()));
                mes_process_report_material.add("MATERIAL_QTY", DataValues.newString(oneMaterial.getMaterialQty()));
                mes_process_report_material.add("ISBUCKLE", DataValues.newString(oneMaterial.getIsBuckle()));
                mes_process_report_material.add("KWAREHOUSE", DataValues.newString(oneMaterial.getKWarehouse()));

                DcpGood dcpGood = entityDao.queryOne(String.format(" SELECT * FROM DCP_GOODS WHERE EID='%s' and PLUNO='%s' ", req.geteId(), oneMaterial.getMaterialPluNo()), DcpGood.class);
                if (null != dcpGood) {

                    mes_process_report_material.add("BASEUNIT",DataValues.newString(dcpGood.getBaseunit()));

                    DcpGoodsUnit dcpGoodsUnit = entityDao.queryOne(String.format(" SELECT * FROM DCP_GOODS_UNIT WHERE EID='%s' and PLUNO='%s' and OUNIT='%s' and UNIT='%s' ", req.geteId(), oneMaterial.getMaterialPluNo(), oneMaterial.getMaterialUnit(), dcpGood.getBaseunit()), DcpGoodsUnit.class);

                    double unitRatio = 1;
                    if (null != dcpGoodsUnit) {
                        unitRatio = dcpGoodsUnit.getUnitratio().doubleValue();
                    }
                    double baseQty = BigDecimalUtils.mul(unitRatio,Double.parseDouble(oneMaterial.getMaterialQty()));
                    mes_process_report_material.add("BASEQTY",DataValues.newDecimal(baseQty));
                }

//                mes_process_report_material.add("BASEUNIT",DataValues.newString(oneMaterial.getMaterialUnit()));
//                mes_process_report_material.add("BASEQTY",DataValues.newString(oneMaterial.getMaterialUnit()));
                mes_process_report_material.add("STANDARD_QTY", DataValues.newString(oneMaterial.getStandardQty()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_PROCESS_REPORT_MATERIAL", mes_process_report_material)));

            }
        }

        item = 1;
        for (DCP_ProcessReportUpdateReq.ScrapList oneScrap : req.getRequest().getScrapList()) {
            ColumnDataValue mes_process_report_scrap = new ColumnDataValue();
            mes_process_report_scrap.add("EID", DataValues.newString(req.geteId()));
            mes_process_report_scrap.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
            mes_process_report_scrap.add("REPORTNO", DataValues.newString(reportNo));
            mes_process_report_scrap.add("ITEM", DataValues.newString(item++));
            mes_process_report_scrap.add("REASON", DataValues.newString(oneScrap.getReason()));
            mes_process_report_scrap.add("SCRAPQTY", DataValues.newInteger(oneScrap.getScrapQty()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_PROCESS_REPORT_SCRAP", mes_process_report_scrap)));
        }

        item = 1;
        for (DCP_ProcessReportUpdateReq.Users oneUser : req.getRequest().getUsers()) {
            ColumnDataValue mes_process_report_user = new ColumnDataValue();

            mes_process_report_user.add("EID", DataValues.newString(req.geteId()));
            mes_process_report_user.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
            mes_process_report_user.add("REPORTNO", DataValues.newString(reportNo));
            mes_process_report_user.add("ITEM", DataValues.newString(item++));
            mes_process_report_user.add("OPNO", DataValues.newString(oneUser.getOpNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_PROCESS_REPORT_USER", mes_process_report_user)));
        }

        String querySql = String.format(" SELECT * FROM MES_BATCHTASK_DETAIL WHERE EID='%s' and BATCHTASKNO='%s' ",
                req.geteId(),
                req.getRequest().getBatchTaskNo());
        List<Map<String, Object>> queryData = doQueryData(querySql, null);
        if (null != queryData && !queryData.isEmpty()) {
            for (Map<String, Object> oneData : queryData) {
                ColumnDataValue mes_process_report_mo = new ColumnDataValue();

                mes_process_report_mo.add("EID", DataValues.newString(req.geteId()));
                mes_process_report_mo.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                mes_process_report_mo.add("REPORTNO", DataValues.newString(reportNo));
                mes_process_report_mo.add("SOURCENO", DataValues.newString(oneData.get("MONO")));
                mes_process_report_mo.add("MITEM", DataValues.newString(oneData.get("MITEM")));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_PROCESS_REPORT_MO", mes_process_report_mo)));
            }
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessReportUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessReportUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessReportUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessReportUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ProcessReportUpdateReq> getRequestType() {
        return new TypeToken<DCP_ProcessReportUpdateReq>(){};
    }

    @Override
    protected DCP_ProcessReportUpdateRes getResponseType() {
        return new DCP_ProcessReportUpdateRes();
    }
}
