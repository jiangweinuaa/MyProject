package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_WOReportDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_WOReportDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_WOReportDetailQuery extends SPosBasicService<DCP_WOReportDetailQueryReq, DCP_WOReportDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_WOReportDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_WOReportDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_WOReportDetailQueryReq>(){};
    }

    @Override
    protected DCP_WOReportDetailQueryRes getResponseType() {
        return new DCP_WOReportDetailQueryRes();
    }

    @Override
    protected DCP_WOReportDetailQueryRes processJson(DCP_WOReportDetailQueryReq req) throws Exception {
        DCP_WOReportDetailQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> row : getQData){

                DCP_WOReportDetailQueryRes.LevelElm datasDTO = res.new LevelElm();
                datasDTO.setReportNo(row.get("REPORTNO").toString());
                datasDTO.setBDate(row.get("BDATE").toString());
                datasDTO.setAccountDate(row.get("ACCOUNTDATE").toString());
                datasDTO.setMemo(row.get("MEMO").toString());
                datasDTO.setStatus(row.get("STATUS").toString());
                datasDTO.setCreateBy(row.get("CREATEBY").toString());
                datasDTO.setCreateByName(row.get("CREATEBYNAME").toString());
                datasDTO.setCreateTime(row.get("CREATETIME").toString());
                datasDTO.setModifyBy(row.get("MODIFYBY").toString());
                datasDTO.setModifyByName(row.get("MODIFYBYNAME").toString());
                datasDTO.setModifyTime(row.get("MODIFYTIME").toString());
                datasDTO.setAccountBy(row.get("ACCOUNTBY").toString());
                datasDTO.setAccountByName(row.get("ACCOUNTBYNAME").toString());
                datasDTO.setAccountTime(row.get("ACCOUNTTIME").toString());
                datasDTO.setCancelBy(row.get("CANCELBY").toString());
                datasDTO.setCancelByName(row.get("CANCELBYNAME").toString());
                datasDTO.setCancelTime(row.get("CANCELTIME").toString());
                datasDTO.setProcessStatus(row.get("PROCESSSTATUS").toString());
                datasDTO.setProcessErpNo(row.get("PROCESSERPNO").toString());
                datasDTO.setProcessErpOrg(row.get("PROCESSERPORG").toString());
                datasDTO.setEmployeeId(row.get("EMPLOYEEID").toString());
                datasDTO.setEmployeeName(row.get("EMPLOYEENAME").toString());
                datasDTO.setDepartId(row.get("DEPARTID").toString());
                datasDTO.setDepartName(row.get("DEPARTNAME").toString());

                datasDTO.setDatas(new ArrayList<>());

                String detailDSql="select a.*,b.plu_name as pluname,c.featurename,d.uname as punitname,e.equipname  " +
                        " from DCP_WOREPORT_DETAIL a " +
                        " left join dcp_gooods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_goods_feature_lang c on a.eid=c.eid and a.pluno=c.pluno and a.featureno=c.featureno and c.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_unit_lang d on d.eid=a.eid and d.unit=a.punit and d.lang_type='"+req.getLangType()+"' " +
                        " left join MES_EQUIPMENT e on e.eid=a.eid and e.equipno=a.equipno " +
                        " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                        " and a.reportno='"+req.getRequest().getReportNo()+"' ";
                List<Map<String, Object>> getQDataD=this.doQueryData(detailDSql, null);
                if(CollUtil.isNotEmpty(getQDataD)){
                    for (Map<String, Object> rowD : getQDataD){

                        DCP_WOReportDetailQueryRes.Datas datas = res.new Datas();

                        datas.setItem(rowD.get("ITEM").toString());
                        datas.setOfNo(rowD.get("OFNO").toString());
                        datas.setOItem(rowD.get("OITEM").toString());
                        datas.setPluNo(rowD.get("PLUNO").toString());
                        datas.setPluName(rowD.get("PLUNAME").toString());
                        datas.setFeatureNo(rowD.get("FEATURENO").toString());
                        datas.setFeatureName(rowD.get("FEATURENAME").toString());
                        datas.setPQty(rowD.get("PQTY").toString());
                        datas.setPUnit(rowD.get("PUNIT").toString());
                        datas.setPUName(rowD.get("PUNITNAME").toString());
                        datas.setEquipNo(rowD.get("EQUIPNO").toString());
                        datas.setEquipName(rowD.get("EQUIPNAME").toString());
                        datas.setEQty(rowD.get("EQTY").toString());
                        datas.setLaborTime(rowD.get("LABORTIME").toString());
                        datas.setMachineTime(rowD.get("MACHINETIME").toString());
                        datas.setMemo(rowD.get("MEMO").toString());
                        datasDTO.getDatas().add(datas);
                    }
                }



                res.getDatas().add(datasDTO);
            }

        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_WOReportDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();

        StringBuffer sqlbuf=new StringBuffer();


        sqlbuf.append(" "
                + " select "
                + " a.*,e1.opno as createby,e2.opno as modifyby,e3.opno as accountby,e4.opno as cancelby,e1.op_name as createbyname,e2.op_name as modifybyname,e3.op_name as accountbyname,e4.op_name as cancelbyname,d1.departname," +
                "e5.name as employeename  "
                + " from DCP_WOREPORT a"
                + " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.CREATEOPID and e1.lang_type='"+req.getLangType()+"'"
                + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.LASTMODIOPID and e2.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e3 on e3.eid=a.eid and e3.opno=a.ACCOUNTOPID and e1.lang_type='"+req.getLangType()+"'"
                + " left join PLATFORM_STAFFS_LANG e4 on e4.eid=a.eid and e4.opno=a.CANCELOPID and e2.lang_type='"+req.getLangType()+"' " +
                " left join dcp_employee e5 on e5.eid=a.eid and e5.employeeno =a.employeeid "

                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.DEPARTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.reportno='"+req.getRequest().getReportNo()+"' ");


        return sqlbuf.toString();
    }
}


