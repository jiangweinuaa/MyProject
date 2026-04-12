package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ProductGroupGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProductGroupGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ProductGroupGoodsQuery extends SPosBasicService<DCP_ProductGroupGoodsQueryReq, DCP_ProductGroupGoodsQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProductGroupGoodsQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ProductGroupGoodsQueryReq> getRequestType() {
        return new TypeToken<DCP_ProductGroupGoodsQueryReq>(){};
    }

    @Override
    protected DCP_ProductGroupGoodsQueryRes getResponseType() {
        return new DCP_ProductGroupGoodsQueryRes();
    }

    @Override
    protected DCP_ProductGroupGoodsQueryRes processJson(DCP_ProductGroupGoodsQueryReq req) throws Exception {
        DCP_ProductGroupGoodsQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            for (Map<String, Object> row : getQData){
                DCP_ProductGroupGoodsQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setPGroupNo(row.get("PGROUPNO").toString());
                level1Elm.setPGroupName(row.get("PGROUPNAME").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setCreatorID(row.get("CREATORID").toString());
                level1Elm.setCreatorName(row.get("CREATORNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setLastModifyID(row.get("LASTMODIFYID").toString());
                level1Elm.setLastModifyName(row.get("LASTMODIFYNAME").toString());
                level1Elm.setLastModifyTime(row.get("LASTMODIFYTIME").toString());
                level1Elm.setDatas(new ArrayList<>());

                String goodSql="select a.pluno,c.plu_name as pluname,b.spec from MES_PRODUCT_GROUP_GOODS a " +
                        " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                        " left join dcp_goods_lang c on a.eid=c.eid and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"'" +
                        " where a.eid='"+req.geteId()+"' and a.pgroupno='"+req.getRequest().getPGroupNo()+"' ";
                List<Map<String, Object>> getGoodData=this.doQueryData(goodSql, null);
                if(CollUtil.isNotEmpty(getGoodData)){
                    for (Map<String, Object> goodRow : getGoodData){
                        DCP_ProductGroupGoodsQueryRes.Datas datas = res.new Datas();
                        datas.setPluNo(goodRow.get("PLUNO").toString());
                        datas.setPluName(goodRow.get("PLUNAME").toString());
                        datas.setSpec(goodRow.get("SPEC").toString());
                        level1Elm.getDatas().add(datas);
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
    protected String getQuerySql(DCP_ProductGroupGoodsQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();

        sqlbuf.append(" "
                + " select  "
                + " a.pgroupno,a.pgroupname,a.memo,a.status,a.createOpId as creatorID ,e1.name as creatorName,a.lastModiOpId as lastModifyID,e2.name as lastModifyNAME,to_char(a.createTime,'yyyy-MM-dd HH:mm:ss') as createtime ,to_char(a.lastModiTime,'yyyy-MM-dd HH:mm:ss') as lastModifyTime"
                + ",a.departid,d1.departname as departname  "
                + " from MES_PRODUCT_GROUP a"
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.createOpId "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.lastModiOpId "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.pgroupno='"+req.getRequest().getPGroupNo()+"'"
                + " ");

        return sqlbuf.toString();
    }
}


