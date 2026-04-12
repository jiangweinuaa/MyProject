package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_PrintTemplateDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PrintTemplateDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PrintTemplateDetailQuery extends SPosBasicService<DCP_PrintTemplateDetailQueryReq, DCP_PrintTemplateDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_PrintTemplateDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_PrintTemplateDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_PrintTemplateDetailQueryReq>(){};
    }

    @Override
    protected DCP_PrintTemplateDetailQueryRes getResponseType() {
        return new DCP_PrintTemplateDetailQueryRes();
    }

    @Override
    protected DCP_PrintTemplateDetailQueryRes processJson(DCP_PrintTemplateDetailQueryReq req) throws Exception {
        DCP_PrintTemplateDetailQueryRes res = this.getResponse();

        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            for (Map<String, Object> row : getQData){
                DCP_PrintTemplateDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setModularNo(row.get("MODULARNO").toString());
                level1Elm.setModularName(row.get("CHSMSG").toString());
                level1Elm.setProName(row.get("PRONAME").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setOnSale(row.get("ONSALEY").toString());
                level1Elm.setTotCqty("0");
                level1Elm.setDetail(new ArrayList<>());

                StringBuffer detailSb=new StringBuffer();
                detailSb.append("select * from DCP_MODULAR_PRINT a " +
                        " where a.eid='"+req.geteId()+"' and a.modularno='"+req.getRequest().getModularNo()+"'");

                List<Map<String, Object>> detailList = this.doQueryData(detailSb.toString(), null);

                String opSql="select a.*,case when a.USERTYPE='OPNO' THEN b.op_name ELSE OPGNAME END AS OP_NAME  from DCP_MODULAR_PRINT_USER a " +
                        " left join platform_staffs_lang b on a.eid=b.eid and a.USERID=b.opno and b.lang_type='"+req.getLangType()+"' " +
                        " left join PLATFORM_ROLE c on c.eid=a.eid and a.userid=c.OPGROUP " +
                        " where a.eid='"+req.geteId()+"' and a.modularno='"+req.getRequest().getModularNo()+"'";
                List<Map<String, Object>> opList = this.doQueryData(opSql, null);

                String orgSql="select a.*,b.org_name from DCP_MODULAR_PRINT_ORG a" +
                        " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+req.getLangType()+"' " +
                        " where a.eid='"+req.geteId()+"' and a.modularno='"+req.getRequest().getModularNo()+"' ";
                List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);

                String custSql="select a.*,b.sname as customername from DCP_MODULAR_PRINT_CUSTOMER a " +
                        " left join dcp_bizpartner b on a.eid=b.eid and a.customerno=b.bizpartnerno " +
                        " where a.eid='"+req.geteId()+"' and a.modularno='"+req.getRequest().getModularNo()+"' ";
                List<Map<String, Object>> custList = this.doQueryData(custSql, null);

                if (CollUtil.isNotEmpty(detailList)) {
                    for (Map<String, Object> detailRow : detailList){
                        DCP_PrintTemplateDetailQueryRes.Detail level2Elm = res.new Detail();
                        level2Elm.setPrintNo(detailRow.get("PRINTNO").toString());
                        level2Elm.setPrintName(detailRow.get("PRINTNAME").toString());
                        level2Elm.setProName(detailRow.get("PRONAME").toString());
                        level2Elm.setParaMeter(detailRow.get("PARAMETER").toString());
                        level2Elm.setIsStandard(detailRow.get("ISSTANDARD").toString());
                        level2Elm.setIsDefault(detailRow.get("ISDEFAULT").toString());
                        level2Elm.setPrintType(detailRow.get("PRINTTYPE").toString());
                        level2Elm.setRestrictOp(detailRow.get("RESTRICTOP").toString());
                        level2Elm.setRestrictCust(detailRow.get("RESTRICTCUST").toString());
                        level2Elm.setRestrictOrg(detailRow.get("RESTRICTORG").toString());
                        level2Elm.setRestrictOrgList(new ArrayList<>());
                        level2Elm.setRestrictOpList(new ArrayList<>());
                        level2Elm.setRestrictCustList(new ArrayList<>());
                        level2Elm.setStatus(detailRow.get("STATUS").toString());

                        List<Map<String, Object>> filterOps = opList.stream().filter(x -> x.get("PRINTNO").toString().equals(level2Elm.getPrintNo())).collect(Collectors.toList());

                        if(filterOps.size()>0){
                            for (Map<String, Object> opRow : filterOps){
                                DCP_PrintTemplateDetailQueryRes.RestrictOpList restrictOpList = res.new RestrictOpList();
                                restrictOpList.setId(opRow.get("USERID").toString());
                                restrictOpList.setOpType(opRow.get("USERTYPE").toString());
                                restrictOpList.setName(opRow.get("OP_NAME").toString());
                                level2Elm.getRestrictOpList().add(restrictOpList);
                            }
                        }

                        List<Map<String, Object>> filterOrgs = orgList.stream().filter(x -> x.get("PRINTNO").toString().equals(level2Elm.getPrintNo())).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(filterOrgs)) {
                            for (Map<String, Object> orgRow : filterOrgs){
                                DCP_PrintTemplateDetailQueryRes.RestrictOrgList restrictOrgList = res.new RestrictOrgList();
                                restrictOrgList.setOrgNo(orgRow.get("ORGANIZATIONNO").toString());
                                restrictOrgList.setOrgName(orgRow.get("ORG_NAME").toString());
                                level2Elm.getRestrictOrgList().add(restrictOrgList);
                            }
                        }

                        List<Map<String, Object>> filterCusts = custList.stream().filter(x -> x.get("PRINTNO").toString().equals(level2Elm.getPrintNo())).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(filterCusts)) {
                            for (Map<String, Object> custRow : filterCusts){
                                DCP_PrintTemplateDetailQueryRes.RestrictCustList restrictCustList = res.new RestrictCustList();
                                restrictCustList.setCustomerNo(custRow.get("CUSTOMERNO").toString());
                                restrictCustList.setCustomerName(custRow.get("CUSTOMERNAME").toString());
                                level2Elm.getRestrictCustList().add(restrictCustList);
                            }
                        }

                        level1Elm.getDetail().add(level2Elm);

                    }
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
    protected String getQuerySql(DCP_PrintTemplateDetailQueryReq req) throws Exception {
        String modularNo = req.getRequest().getModularNo();
        StringBuffer sqlbuf=new StringBuffer();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append("" +
                "select a.*,nvl(a.onsale,'Y') as onsaley from dcp_modular a " +
                " where a.eid='"+req.geteId()+"' and a.modularno='"+modularNo+"' ");


        return sqlbuf.toString();
    }

}


