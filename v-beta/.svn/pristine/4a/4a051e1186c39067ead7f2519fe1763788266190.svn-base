package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProdScheduleDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProdScheduleDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ProdScheduleDetailQuery  extends SPosBasicService<DCP_ProdScheduleDetailQueryReq, DCP_ProdScheduleDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProdScheduleDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ProdScheduleDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ProdScheduleDetailQueryReq>(){};
    }

    @Override
    protected DCP_ProdScheduleDetailQueryRes getResponseType() {
        return new DCP_ProdScheduleDetailQueryRes();
    }

    @Override
    protected DCP_ProdScheduleDetailQueryRes processJson(DCP_ProdScheduleDetailQueryReq req) throws Exception {
        DCP_ProdScheduleDetailQueryRes res = this.getResponse();
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String detailSql = this.getDetailSql(req);
            List<Map<String, Object>> getDetailData = this.doQueryData(detailSql, null);
            String sourceSql = this.getSourceSql(req);
            List<Map<String, Object>> getSourceData = this.doQueryData(sourceSql, null);
            String genSql = this.getGenSql(req);
            List<Map<String, Object>> getGenData = this.doQueryData(genSql, null);
//            String moSql = this.getMoSql(req);
            String moSql = this.getProcessTaskSql(req); //工单取值表变更
            List<Map<String, Object>> getMoData = this.doQueryData(moSql, null);

            for (Map<String, Object> row : getQData){
                DCP_ProdScheduleDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setBillNo(row.get("BILLNO").toString());
                level1Elm.setBeginDate(row.get("BEGINDATE").toString());
                level1Elm.setEndDate(row.get("ENDATE").toString());
                level1Elm.setSemiWOGenType(row.get("SEMIWOGENTYPE").toString());
                level1Elm.setEmployeeId(row.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(row.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setTotCqty(row.get("TOTCQTY").toString());
                level1Elm.setTotPqty(row.get("TOTPQTY").toString());
                level1Elm.setTotWOQty(row.get("TOTWOQTY").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setCreateBy(row.get("CREATEBY").toString());
                level1Elm.setCreateByName(row.get("CREATEBYNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setCreateDeptId(row.get("CREATEDEPTID").toString());
                level1Elm.setCreateDeptName(row.get("CREATEDEPTNAME").toString());
                level1Elm.setModifyBy(row.get("MODIFYBY").toString());
                level1Elm.setModifyByName(row.get("MODIFYBYNAME").toString());
                level1Elm.setModifyTime(row.get("MODIFYTIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmTime(row.get("CONFIRMTIME").toString());
                level1Elm.setCancelBy(row.get("CANCELBY").toString());
                level1Elm.setCancelByName(row.get("CANCELBYNAME").toString());
                level1Elm.setCancelTime(row.get("CANCELTIME").toString());
                level1Elm.setCloseBy(row.get("CLOSEBY").toString());
                level1Elm.setCloseByName(row.get("CLOSEBYNAME").toString());
                level1Elm.setCloseTime(row.get("CLOSETIME").toString());

                level1Elm.setDetail(new ArrayList<>());
                level1Elm.setGenList(new ArrayList<>());

                for (Map<String, Object> detailRow : getDetailData){
                    DCP_ProdScheduleDetailQueryRes.Detail detail = res.new Detail();
                    detail.setItem(detailRow.get("ITEM").toString());
                    detail.setPluBarcode(detailRow.get("PLUBARCODE").toString());
                    detail.setPluNo(detailRow.get("PLUNO").toString());
                    detail.setPluName(detailRow.get("PLUNAME").toString());
                    detail.setFeatureNo(detailRow.get("FEATURENO").toString());
                    detail.setFeatureName(detailRow.get("FEATURENAME").toString());
                    detail.setSpec(detailRow.get("SPEC").toString());
                    detail.setUpPluNo(detailRow.get("UPPLUNO").toString());
                    detail.setUpPluName(detailRow.get("UPPLUNAME").toString());
                    detail.setRDate(detailRow.get("RDATE").toString());
                    detail.setPGroupNo(detailRow.get("PGROUPNO").toString());
                    detail.setPGroupName(detailRow.get("PGROUPNAME").toString());
                    detail.setPUnit(detailRow.get("PUNIT").toString());
                    detail.setPUnitName(detailRow.get("PUNITNAME").toString());
                    detail.setPQty(detailRow.get("PQTY").toString());
                    detail.setBaseUnit(detailRow.get("BASEUNIT").toString());
                    detail.setBaseQty(detailRow.get("BASEQTY").toString());
                    detail.setUnitRatio(detailRow.get("UNITRATIO").toString());
                    detail.setPoQty(detailRow.get("POQTY").toString());
                    detail.setStockQty(detailRow.get("STOCKQTY").toString());
                    detail.setShortQty(detailRow.get("SHORTQTY").toString());
                    detail.setAdviceQty(detailRow.get("ADVICEQTY").toString());
                    detail.setMinQty(detailRow.get("MINQTY").toString());
                    detail.setMulQty(detailRow.get("MULQTY").toString());
                    detail.setRemainType(detailRow.get("REMAINTYPE").toString());
                    detail.setPreDays(detailRow.get("PREDAYS").toString());
                    detail.setBomNo(detailRow.get("BOMNO").toString());
                    detail.setVersionNum(detailRow.get("VERSIONNUM").toString());
                    detail.setGItem(detailRow.get("GITEM").toString());
                    detail.setSourceType(detailRow.get("SOURCETYPE").toString());
                    detail.setMemo(detailRow.get("MEMO").toString());
                    detail.setOddValue(detailRow.get("ODDVALUE").toString());

                    detail.setSourceList(new ArrayList<>());
                    List<Map<String, Object>> sourceFilterRows = getSourceData.stream().filter(x -> x.get("OITEM").toString().equals(detail.getItem())).collect(Collectors.toList());
                    for (Map<String, Object> sourceRow : sourceFilterRows){
                        DCP_ProdScheduleDetailQueryRes.SourceList sourceList = res.new SourceList();
                        sourceList.setItem(sourceRow.get("ITEM").toString());
                        sourceList.setOItem(sourceRow.get("OITEM").toString());
                        sourceList.setOrderType(sourceRow.get("ORDERTYPE").toString());
                        sourceList.setOrderNo(sourceRow.get("ORDERNO").toString());
                        sourceList.setOrderItem(sourceRow.get("ORDERITEM").toString());
                        sourceList.setObjectType(sourceRow.get("OBJECTTYPE").toString());
                        sourceList.setObjectId(sourceRow.get("OBJECTID").toString());
                        sourceList.setObjectName(sourceRow.get("OBJECTNAME").toString());
                        sourceList.setRUnit(sourceRow.get("RUNIT").toString());
                        sourceList.setRUnitName(sourceRow.get("RUNITNAME").toString());
                        sourceList.setRQty(sourceRow.get("RQTY").toString());
                        sourceList.setPoQty(sourceRow.get("POQTY").toString());
                        sourceList.setPTemplateNo(sourceRow.get("PTEMPLATENO").toString());
                        sourceList.setPTemplateName(sourceRow.get("PTEMPLATENAME").toString());
                        detail.getSourceList().add(sourceList);
                    }

                    level1Elm.getDetail().add(detail);
                }

                for(Map<String, Object> genRow : getGenData){
                    DCP_ProdScheduleDetailQueryRes.GenList genList = res.new GenList();
                    genList.setItem(genRow.get("ITEM").toString());
                    genList.setPluBarcode(genRow.get("PLUBARCODE").toString());
                    genList.setPluNo(genRow.get("PLUNO").toString());
                    genList.setPluName(genRow.get("PLUNAME").toString());
                    genList.setFeatureNo(genRow.get("FEATURENO").toString());
                    genList.setFeatureName(genRow.get("FEATURENAME").toString());
                    genList.setSpec(genRow.get("SPEC").toString());
                    genList.setPGroupNo(genRow.get("PGROUPNO").toString());
                    genList.setPGroupName(genRow.get("PGROUPNAME").toString());
                    genList.setPUnit(genRow.get("PUNIT").toString());
                    genList.setPUnitName(genRow.get("PUNITNAME").toString());
                    genList.setPQty(genRow.get("PQTY").toString());
                    genList.setBaseUnit(genRow.get("BASEUNIT").toString());
                    genList.setBaseUnitName(genRow.get("BASEUNITNAME").toString());
                    genList.setBaseQty(genRow.get("BASEQTY").toString());
                    genList.setUnitRatio(genRow.get("UNITRATIO").toString());
                    genList.setRDate(genRow.get("RDATE").toString());
                    genList.setPreDays(genRow.get("PREDAYS").toString());
                    genList.setBeginDate(genRow.get("BEGINDATE").toString());
                    genList.setEndDate(genRow.get("ENDDATE").toString());
                    genList.setToWOQty(genRow.get("TOWOQTY").toString());
                    genList.setBomNo(genRow.get("BOMNO").toString());
                    genList.setVerisonNum(genRow.get("VERSIONNUM").toString());
                    genList.setProdType(genRow.get("PRODTYPE").toString());
                    genList.setUpPluNo(genRow.get("UPPLUNO").toString());
                    genList.setUpPluName(genRow.get("UPPLUNAME").toString());

                    genList.setUpPluList(new ArrayList<>());
                    List<Map<String, Object>> upPluFilterRows = getDetailData.stream().filter(x -> x.get("PLUNO").toString().equals(genList.getUpPluNo())).collect(Collectors.toList());
                    List<Map<String, Object>> pluFilterRows = getDetailData.stream().filter(x -> x.get("PLUNO").toString().equals(genList.getPluNo())).collect(Collectors.toList());

                    //找到detail 对应的pluno 可能有多个
                    for (Map<String, Object> pluRow : pluFilterRows){
                        for (Map<String, Object> upPluRow : upPluFilterRows){
                            DCP_ProdScheduleDetailQueryRes.UpPluList upPluList = res.new UpPluList();
                            upPluList.setOItem(upPluRow.get("ITEM").toString());
                            upPluList.setUpPluNo(upPluRow.get("PLUNO").toString());
                            upPluList.setUpPluName(upPluRow.get("PLUNAME").toString());
                            upPluList.setRDate(upPluRow.get("RDATE").toString());
                            upPluList.setUpPUnit(upPluRow.get("PUNIT").toString());
                            upPluList.setUpPUnitName(upPluRow.get("PUNITNAME").toString());
                            upPluList.setUpPQty(upPluRow.get("PQTY").toString());
                            upPluList.setPQty(pluRow.get("PQTY").toString());
                            genList.getUpPluList().add(upPluList);
                        }
                    }

                    genList.setWoList(new ArrayList<>());
                    List<Map<String, Object>> wList = getMoData.stream().filter(x -> x.get("OITEM").toString().equals(genList.getItem())).collect(Collectors.toList());
                    for (Map<String, Object> wRow : wList){
                        DCP_ProdScheduleDetailQueryRes.WoList woList = res.new WoList();
                        woList.setMONo(wRow.get("MONO").toString());
                        woList.setMOItem(wRow.get("MOITEM").toString());
                        woList.setMOBeginDate(wRow.get("MBEGINDATE").toString());
                        woList.setMOEndDate(wRow.get("MENDDATE").toString());
                        woList.setWOQty(wRow.get("MPQTY").toString());
                        woList.setDisPatchQty(wRow.get("DISPATCHQTY").toString());
                        woList.setDisPatchStatus(wRow.get("DISPATCHSTATUS").toString());
                        genList.getWoList().add(woList);
                    }

                    level1Elm.getGenList().add(genList);
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
    protected String getQuerySql(DCP_ProdScheduleDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();

        String organizationNO = req.getOrganizationNO();
        StringBuffer sqlbuf=new StringBuffer();
        String billNo = req.getRequest().getBillNo();

        sqlbuf.append(" "

                + " select  "
                + " a.billno,a.bdate,a.begindate,a.endate,a.status,a.semiWOGenType,a.totCqty,a.totpqty,a.totWOQty,a.memo,"
                + " a.CREATEBY ,e1.name as CREATEBYNAME,a.MODIFYBY,e2.name as MODIFYBYNAME,TO_CHAR(a.CREATETIME,'yyyy-MM-dd HH:mm:ss') as createtime,to_char(a.MODIFYTIME,'yyyy-MM-dd HH:mm:ss') as MODIFYTIME,a.CREATEDEPTID,d1.departname as createdeptname,  "
                + " a.confirmby,e3.name as confirmbyname,a.cancelby,e4.name as cancelbyname,a.closeby,e5.name as closebyname,to_char(a.confirmtime,'yyyy-MM-dd HH:mm:ss') as confirmtime,to_char(a.canceltime,'yyyy-MM-dd HH:mm:ss') as canceltime,to_char(a.closetime,'yyyy-MM-dd HH:mm:ss') as closetime, "
                + " a.employeeid,e.name as employeename,a.departid,d.departname "
                + " from DCP_PRODSCHEDULE a"
                + " left join dcp_employee e on e.eid=a.eid and e.employeeno=a.employeeid "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.CREATEBY "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.MODIFYBY "
                + " left join dcp_employee e3 on e3.eid=a.eid and e3.employeeno=a.CONFIRMBY "
                + " left join dcp_employee e4 on e4.eid=a.eid and e4.employeeno=a.CANCELBY "
                + " left join dcp_employee e5 on e5.eid=a.eid and e5.employeeno=a.CLOSEBY "
                + " left join dcp_department_lang d on d.eid=a.eid and d.departno=a.departid and d.lang_type='"+langType+"'"
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.CREATEDEPTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"'  and a.organizationno='"+organizationNO+"' and a.billno='"+billNo+"'"
                + " ");

        return sqlbuf.toString();
    }

    private String getDetailSql(DCP_ProdScheduleDetailQueryReq req) throws Exception{
        String eId = req.geteId();
        String langType = req.getLangType();
        String organizationNO = req.getOrganizationNO();
        String billNo = req.getRequest().getBillNo();
        StringBuffer sqlbuf=new StringBuffer();

        sqlbuf.append("select  a.item,a.pluno,c.plu_name as pluname,b.spec,a.featureno,d.featurename,a.UPPLUNO,e.plu_name as uppluname,a.rdate,a.pgroupno,f.pgroupname,a.punit,g.uname as punitname," +
                " a.pqty,a.baseunit,h.uname as baseunitname,a.POQTY,a.STOCKQTY,a.SHORTQTY,a.ADVICEQTY,a.MINQTY,a.MULQTY,a.REMAINTYPE,a.PREDAYS,a.unitratio,a.bomno,a.versionnum,a.gitem,a.sourcetype,a.memo   " +
                " ,b.MAINBARCODE PLUBARCODE,a.BASEQTY ,a.ODDVALUE " +
                "  from  DCP_PRODSCHEDULE_DETAIL a " +
                " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+langType+"'" +
                " left join dcp_goods_feature_lang d on d.eid=a.eid and d.pluno=a.pluno and d.featureno=a.featureno and d.lang_type='"+langType+"' " +
                " left join dcp_goods_lang e on e.eid=a.eid and e.pluno=a.uppluno and e.lang_type='"+langType+"'" +
                " left join MES_PRODUCT_GROUP f on f.eid=a.eid and f.pgroupno=a.pgroupno " +
                " left join dcp_unit_lang g on g.eid=a.eid and g.unit=a.punit and g.lang_type='"+langType+"' " +
                " left join dcp_unit_lang h on h.eid=a.eid and h.unit=a.baseunit and h.lang_type='"+langType+"' " +
                " " +
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"'" +
                "  " +
                " and a.billno='"+billNo+"'");
        sqlbuf.append(" ORDER BY a.ITEM ");


        return sqlbuf.toString();
    }

    private String getSourceSql(DCP_ProdScheduleDetailQueryReq req) throws Exception{
        String eId = req.geteId();
        String langType = req.getLangType();
        String organizationNO = req.getOrganizationNO();
        String billNo = req.getRequest().getBillNo();
        StringBuffer sqlbuf=new StringBuffer();

        sqlbuf.append("select a.*,b.uname as runitname,c.PTEMPLATE_NAME ptemplatename," +
                " case when a.objecttype='1' then d.org_name else e.sname end as objectname    " +
                " from " +
                " DCP_PRODSCHEDULE_SOURCE a " +
                " LEFT JOIN dcp_unit_lang b on a.eid=b.eid and a.runit=b.unit and b.lang_type='"+langType+"'" +
                " left join DCP_PTEMPLATE c on c.eid=a.eid and c.ptemplateno=a.ptemplateno " +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.objectid and d.lang_type='"+langType+"' " +
                " left join dcp_bizpartner e on e.eid=a.eid and e.BIZPARTNERNO=a.objectid " +

                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"'" +
                "  " +
                " and a.billno='"+billNo+"'");


        return sqlbuf.toString();
    }

    private String getGenSql(DCP_ProdScheduleDetailQueryReq req) throws Exception{
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();

        sqlbuf.append(" select a.ITEM,a.PLUNO,b.plu_name as pluname,c.spec,a.FEATURENO,d.featurename,a.UPPLUNO,e.plu_name as uppluname," +
                " a.PGROUPNO,h.pgroupname,a.DEPARTID,c.PUNIT,f.uname as punitname,a.PQTY," +
                " a.RDATE,a.PREDAYS,a.BEGINDATE,a.ENDDATE,a.TOWOQTY,a.BASEUNIT,g.uname as baseunitname,a.BASEQTY,a.UNITRATIO,a.BOMNO,a.VERSIONNUM " +
                " ,c.MAINBARCODE PLUBARCODE,case when bv.bomno is null then bv2.prodtype else bv.PRODTYPE end as prodtype "+
                " from DCP_PRODSCHEDULE_GEN a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno " +
                " left join dcp_goods c on a.eid=c.eid and c.pluno=a.pluno " +
                " left join dcp_goods_feature_lang d on d.eid=a.eid and d.pluno=a.pluno and d.featureno=a.featureno and d.lang_type='"+langType+"'" +
                " left join dcp_goods_lang e on e.eid=a.eid and e.pluno=a.uppluno and e.lang_type='"+langType+"' " +
                " left join dcp_unit_lang f on f.eid=a.eid and f.unit=a.punit and f.lang_type='"+langType+"' " +
                " left join dcp_unit_lang g on g.eid=a.eid and g.unit =a.baseunit and g.lang_type='"+langType+"'" +
                " left join MES_PRODUCT_GROUP h on h.eid=a.eid and h.pgroupno=a.pgroupno " +
                " left join DCP_BOM bv on bv.eid=a.eid and bv.BOMNO=a.BOMNO and a.VERSIONNUM=bv.VERSIONNUM "+
                " left join DCP_BOM bv2 on bv2.eid=a.eid and bv2.BOMNO=a.BOMNO and a.VERSIONNUM=bv2.VERSIONNUM "+
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.billno='"+req.getRequest().getBillNo()+"'");

        return sqlbuf.toString();
    }

    private String getMoSql(DCP_ProdScheduleDetailQueryReq req) throws Exception{
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append("select b.mono,b.item as moitem ,b.begindate as mbegindate,b.enddate as menddate,b.pqty as mpqty ,b.DISPATCHQTY,b.DISPATCHstatus " +
                " ,b.oitem from mes_mo a " +
                " left join mes_mo_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.mono=b.mono " +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.ofno='"+req.getRequest().getBillNo()+"'");
        return sqlbuf.toString();
    }
    private String getProcessTaskSql(DCP_ProdScheduleDetailQueryReq req) throws Exception{
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append("select b.PROCESSTASKNO mono,b.item as moitem ,b.begindate as mbegindate,b.enddate as menddate,b.pqty as mpqty ,b.DISPATCHQTY,b.DISPATCHstatus " +
                " ,b.oitem from DCP_PROCESSTASK a " +
                " left join DCP_PROCESSTASK_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.PROCESSTASKNO=b.PROCESSTASKNO " +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.ofno='"+req.getRequest().getBillNo()+"'");
        return sqlbuf.toString();
    }

}


