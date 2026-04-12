package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_MoDetailQueryReq;
import com.dsc.spos.json.cust.req.DCP_MoQueryReq;
import com.dsc.spos.json.cust.res.DCP_MoDetailQueryRes;
import com.dsc.spos.json.cust.res.DCP_MoQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_MoDetailQuery extends SPosBasicService<DCP_MoDetailQueryReq, DCP_MoDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_MoDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_MoDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_MoDetailQueryReq>(){};
    }

    @Override
    protected DCP_MoDetailQueryRes getResponseType() {
        return new DCP_MoDetailQueryRes();
    }

    @Override
    protected DCP_MoDetailQueryRes processJson(DCP_MoDetailQueryReq req) throws Exception {
        DCP_MoDetailQueryRes res = this.getResponse();

        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());
        String detailSql = this.getDetailSql(req);
        List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);

        if (getQData != null && !getQData.isEmpty()) {
            for (Map<String, Object> row : getQData){
                DCP_MoDetailQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setPDate(row.get("PDATE").toString());
                level1Elm.setMoNo(row.get("MONO").toString());
                level1Elm.setPGroupNo(row.get("PGROUPNO").toString());
                level1Elm.setPGroupName(row.get("PGROUPNAME").toString());
                level1Elm.setLoadDocNo(row.get("LOADDOCNO").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setCreateBy(row.get("CREATEBY").toString());
                level1Elm.setCreateByName(row.get("CREATEBYNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setModifyBy(row.get("MODIFYBY").toString());
                level1Elm.setModifyByName(row.get("MODIFYBYNAME").toString());
                level1Elm.setModifyTime(row.get("MODIFYTIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmTime(row.get("CONFIRMTIME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setOType(row.get("OTYPE").toString());
                level1Elm.setOfNo(row.get("OFNO").toString());
                level1Elm.setSourceMoNo(row.get("SOURCEMONO").toString());
                level1Elm.setProdType(row.get("PRODTYPE").toString());

                level1Elm.setDatas(new ArrayList<>());
                if(CollUtil.isNotEmpty(getDetailData)){
                    for (Map<String, Object> rowDetail : getDetailData){
                        DCP_MoDetailQueryRes.Datas detail = res.new Datas();
                        detail.setItem(rowDetail.get("ITEM").toString());
                        detail.setSourceMoItem(rowDetail.get("SOURCEMOITEM").toString());
                        detail.setPluNo(rowDetail.get("PLUNO").toString());
                        detail.setPluName(rowDetail.get("PLUNAME").toString());
                        detail.setFeatureNo(rowDetail.get("FEATURENO").toString());
                        detail.setFeatureName(rowDetail.get("FEATURENAME").toString());
                        detail.setPUnit(rowDetail.get("PUNIT").toString());
                        detail.setPUName(rowDetail.get("PUNITNAME").toString());
                        detail.setPQty(rowDetail.get("PQTY").toString());
                        detail.setBeginDate(rowDetail.get("BEGINDATE").toString());
                        detail.setEndDate(rowDetail.get("ENDDATE").toString());
                        detail.setBomNo(rowDetail.get("BOMNO").toString());
                        detail.setVersionNum(rowDetail.get("VERSIONNUM").toString());
                        detail.setPickStatus(rowDetail.get("PICKSTATUS").toString());
                        detail.setDispatchQty(rowDetail.get("DISPATCHQTY").toString());
                        detail.setDispatchStatus(rowDetail.get("DISPATCHSTATUS").toString());
                        detail.setMulQty(rowDetail.get("MULQTY").toString());
                        detail.setMinQty(rowDetail.get("MINQTY").toString());
                        level1Elm.getDatas().add(detail);
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
    protected String getQuerySql(DCP_MoDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String mono = req.getRequest().getMoNo();
        StringBuffer sqlbuf=new StringBuffer();

        sqlbuf.append(" "
                + " select "
                + " a.bdate,a.status,a.pdate,a.mono,a.memo,a.PGROUPNo,c.PGROUPNAME,a.LOAD_DOCNO as loadDocNo,a.otype,a.ofno," +
                " a.sourceMoNo,a.prodType,a.CREATEOPID as createby ,e1.name as createbyname,a.LASTMODIOPID as modifyby,e2.name as modifybyname,a.CREATETIME as createTime,a.LASTMODITIME as modifyTime," +
                " a.departId,d1.departname as departNAME,a.ACCOUNTOPID as confirmby,a.ACCOUNTTIME as confirmtime,e3.name as confirmbyname  "
                + " from mes_mo a"
                + " left join MES_PRODUCT_GROUP c on c.eid=a.eid and a.pgroupno=c.PGROUPNO "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.CREATEOPID "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.LASTMODIOPID "
                + " left join dcp_employee e3 on e3.eid=a.eid and e3.employeeno=a.ACCOUNTOPID "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                + " and a.mono='"+mono+"'"
                + " ");

        return sqlbuf.toString();
    }

    private String getDetailSql(DCP_MoDetailQueryReq req) throws Exception {

        StringBuffer sb = new StringBuffer();
        sb.append("select a.item,a.oitem,a.mono,a.sourcemoitem,a.pluno,b.plu_name as pluname,a.featureno,c.featurename,a.punit,a.pqty,d.uname as punitname," +
                " a.begindate,a.enddate,a.bomno,a.versionnum,a.pickstatus,a.dispatchqty,a.dispatchstatus,a.mulqty,a.minqty  " +
                " from mes_mo_detail a " +
                " left join dcp_goods_lang b on a.pluno=b.pluno and a.eid=b.eid and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods_feature_lang c on a.eid=c.eid and a.pluno=c.pluno and a.featureno=c.featureno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang d on d.eid=a.eid and d.unit=a.punit and d.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.mono='"+req.getRequest().getMoNo()+"'");

        return sb.toString();

    }
}


