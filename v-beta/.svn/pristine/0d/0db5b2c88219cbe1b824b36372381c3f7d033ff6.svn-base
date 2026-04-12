package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsDetailReq;
import com.dsc.spos.json.cust.res.DCP_MultiSpecGoodsDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_MultiSpecGoodsDetail extends SPosBasicService<DCP_MultiSpecGoodsDetailReq,DCP_MultiSpecGoodsDetailRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_MultiSpecGoodsDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        //必传字段
        String pluNo = req.getRequest().getMasterPluNo();
        if (Check.Null(pluNo))
        {
            errMsg.append("商品编码不可为空值, ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
        
    }
    
    @Override
    protected TypeToken<DCP_MultiSpecGoodsDetailReq> getRequestType() {
        return new TypeToken<DCP_MultiSpecGoodsDetailReq>(){};
    }
    
    @Override
    protected DCP_MultiSpecGoodsDetailRes getResponseType() {
        return new DCP_MultiSpecGoodsDetailRes();
    }
    
    @Override
    protected DCP_MultiSpecGoodsDetailRes processJson(DCP_MultiSpecGoodsDetailReq req) throws Exception {
        try {
            DCP_MultiSpecGoodsDetailRes res = this.getResponse();
            DCP_MultiSpecGoodsDetailRes.goodsSetDetail data = res.new goodsSetDetail();
            data.setMasterPluName_lang(new ArrayList<>());
            data.setAttrList(new ArrayList<>());
            data.setSubGoodsList(new ArrayList<>());
            
            String eid = req.geteId();
            String masterPluNo = req.getRequest().getMasterPluNo();
            
            String sql = " select a.MEMO,a.STATUS,a.ATTRGROUPID,a.MINPRICE,a.MAXPRICE,b.ATTRGROUPNAME,C.MASTERPLUNAME"
                    + " from DCP_MSPECGOODS a"
                    + " LEFT JOIN DCP_ATTRGROUP_LANG b on A.EID=b.EID  and A.ATTRGROUPID=b.ATTRGROUPID AND b.LANG_TYPE='" + req.getLangType() + "'"
                    + " LEFT JOIN DCP_MSPECGOODS_LANG C ON A.EID=C.EID AND A.MASTERPLUNO=C.MASTERPLUNO AND c.LANG_TYPE='zh_CN' "
                    + " where a.eid='" + eid + "' and a.masterpluno='" + masterPluNo + "' ";
            
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                data.setMasterPluNo(masterPluNo);
                data.setMemo(getQData.get(0).get("MEMO").toString());
                data.setStatus(getQData.get(0).get("STATUS").toString());
                data.setAttrGroupId(getQData.get(0).get("ATTRGROUPID").toString());
                data.setAttrGroupName(getQData.get(0).get("ATTRGROUPNAME").toString());
                data.setMinPrice(getQData.get(0).get("MINPRICE").toString());
                data.setMaxPrice(getQData.get(0).get("MAXPRICE").toString());
                
                //商品多语言
                DCP_MultiSpecGoodsDetailRes.masterPluName_lang pluNameLang = res.new masterPluName_lang();
                pluNameLang.setLangType("zh_CN");
                pluNameLang.setName(getQData.get(0).get("MASTERPLUNAME").toString());
                
                data.getMasterPluName_lang().add(pluNameLang);
                
                
                //条码
                sql = " select a.FEATURENO,a.attrid1,a.attrvalueid1,a.attrid2,a.attrvalueid2,a.attrid3,a.attrvalueid3,a.pluno,a.unit,a.price,"
                        + " B.FEATURENAME,c.plu_name,d.uname"
                        + " from DCP_MSPECGOODS_SUBGOODS a"
                        + " left join DCP_MSPECGOODS_SUBGOODS_LANG b ON A.EID=b.EID AND A.MASTERPLUNO=b.MASTERPLUNO AND A.FEATURENO=B.FEATURENO AND B.LANG_TYPE='zh_CN'"
                        + " LEFT JOIN DCP_GOODS_LANG c ON A.EID=c.EID AND a.PLUNO=c.PLUNO  AND c.LANG_TYPE='zh_CN'"
                        + " LEFT JOIN DCP_UNIT_LANG d on A.EID=d.EID  and a.UNIT=d.UNIT  AND d.LANG_TYPE='zh_CN'"
                        + " LEFT JOIN DCP_ATTRIBUTION_VALUE d1 on a.EID=d1.EID AND a.ATTRID1=d1.ATTRID AND a.ATTRVALUEID1=d1.ATTRVALUEID"
                        + " LEFT JOIN DCP_ATTRIBUTION_VALUE d2 on a.EID=d2.EID AND a.ATTRID2=d2.ATTRID AND a.ATTRVALUEID2=d2.ATTRVALUEID"
                        + " LEFT JOIN DCP_ATTRIBUTION_VALUE d3 on a.EID=d3.EID AND a.ATTRID3=d3.ATTRID AND a.ATTRVALUEID3=d3.ATTRVALUEID"
                        + " where a.eid='" + req.geteId() + "' and a.MASTERPLUNO='" + masterPluNo + "'"
                        + " ORDER BY d1.SORTID,d2.SORTID,d3.SORTID ";
                List<Map<String, Object>> barcodeDatas = this.doQueryData(sql, null);
                if (barcodeDatas != null) {
                    for (Map<String, Object> map : barcodeDatas) {
                        DCP_MultiSpecGoodsDetailRes.subGoods barcode = res.new subGoods();
                        barcode.setFeatureNo(map.get("FEATURENO").toString());
                        barcode.setFeatureName(map.get("FEATURENAME").toString());
                        barcode.setAttrId1(map.get("ATTRID1").toString());
                        barcode.setAttrValueId1(map.get("ATTRVALUEID1").toString());
                        barcode.setAttrId2(map.get("ATTRID2").toString());
                        barcode.setAttrValueId2(map.get("ATTRVALUEID2").toString());
                        barcode.setAttrId3(map.get("ATTRID3").toString());
                        barcode.setAttrValueId3(map.get("ATTRVALUEID3").toString());
                        barcode.setPluNo(map.get("PLUNO").toString());
                        barcode.setPluName(map.get("PLU_NAME").toString());
                        barcode.setUnit(map.get("UNIT").toString());
                        barcode.setUnitName(map.get("UNAME").toString());
                        barcode.setPrice(map.get("PRICE").toString());
                        data.getSubGoodsList().add(barcode);
                    }
                }
                
                
                //属性
                sql = " select a.attrid,b.sortid,c.attrname from DCP_MSPECGOODS_ATTR a"
                        + " inner join DCP_ATTRIBUTION b on a.eid=b.eid and a.attrid=b.attrid"
                        + " left  join DCP_ATTRIBUTION_LANG c on a.eid=c.eid and a.attrid=c.attrid and c.lang_type='" + req.getLangType() + "'"
                        //【ID1035523】 多规格商品图，原有逻辑是给属性1配图，但把属性2也展示出来了。期望：把属性2的属性值去掉不展示。 by jinzma 20230824
                        + " left  join (select distinct attrid1,attrid2,attrid3 from dcp_mspecgoods_subgoods "
                        + "    where eid='"+eid+"' and masterpluno='"+masterPluNo+"')d on a.attrid=d.attrid1 "
                        + " where a.eid='" + eid + "' and a.masterpluno='" + masterPluNo + "'"
                        + " order by d.attrid1,b.sortid";
                List<Map<String, Object>> attrDatas = this.doQueryData(sql, null);
                if (attrDatas != null) {
                    for (Map<String, Object> map : attrDatas) {
                        DCP_MultiSpecGoodsDetailRes.attr attr = res.new attr();
                        attr.setAttrValueList(new ArrayList<>());
                        
                        String attrId = map.get("ATTRID").toString();
                        attr.setAttrId(attrId);
                        attr.setAttrName(map.get("ATTRNAME").toString());
                        attr.setSortId(map.get("SORTID").toString());
                        
                        //属性
                        sql = " select a.attrvalueid,b.attrvaluename,c.masterpluno from DCP_ATTRIBUTION_VALUE a"
                                + " left join DCP_ATTRIBUTION_VALUE_LANG b on a.eid=b.eid and a.attrid=b.attrid and a.attrvalueid=b.attrvalueid and b.lang_type='zh_CN'"
                                + " LEFT JOIN DCP_MSPECGOODS_ATTR_VALUE c ON A.EID=c.EID AND c.MASTERPLUNO='" + masterPluNo + "' AND a.ATTRID=c.ATTRID and a.ATTRVALUEID=c.ATTRVALUEID"
                                + " where a.eid='" + eid + "' and a.attrid='" + attrId + "'"
                                + " order by a.sortid";
                        List<Map<String, Object>> attrValueDatas = this.doQueryData(sql, null);
                        if (attrValueDatas != null) {
                            for (Map<String, Object> mapValue : attrValueDatas) {
                                DCP_MultiSpecGoodsDetailRes.attrValue attrValue = res.new attrValue();
                                attrValue.setAttrValueId(mapValue.get("ATTRVALUEID").toString());
                                attrValue.setAttrValueName(mapValue.get("ATTRVALUENAME").toString());
                                
                                //【ID1032300】[货郎]多规格商品，商品图片编辑，默认没有展示商品规格，点添加又展示了所有该规格的值，而不是已选规格。 by jinzma 20230419
                                String attrValueStatus = "0"; //是否使用：0未使用 100使用
                                if (!Check.Null(mapValue.get("MASTERPLUNO").toString())) {
                                    attrValueStatus = "100";
                                }
                                attrValue.setStatus(attrValueStatus);
                                
                                attr.getAttrValueList().add(attrValue);
                            }
                        }
                        
                        data.getAttrList().add(attr);
                    }
                }
            }
            
            res.setDatas(data);
            
            return res;
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_MultiSpecGoodsDetailReq req) throws Exception {
        
        String langtype=req.getLangType();
        if(langtype==null||langtype.isEmpty())
        {
            langtype="zh_CN";
        }
        
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append(" select A.*,B.LANG_TYPE LANGTYPE,B.MASTERPLUNAME, ");
        sqlbuf.append(" C.ATTRID,C.SORTID AS SORTID_ATTR,CL.ATTRNAME,D.ATTRVALUEID,DL.ATTRVALUENAME,");
        sqlbuf.append(" G.FEATURENO,G.ATTRID1,G.ATTRVALUEID1,G.ATTRID2,G.ATTRVALUEID2,G.ATTRID3,G.ATTRVALUEID3,"
                + "G.PLUNO,G.UNIT,G.PRICE,GL.FEATURENAME,GLL.PLU_NAME,");
        sqlbuf.append(" U.UNAME,N.ATTRGROUPNAME,E.ATTRVALUEID as ATTRVALUESTATUS");
        sqlbuf.append(" from DCP_MSPECGOODS A");
        sqlbuf.append(" LEFT JOIN DCP_MSPECGOODS_LANG B ON A.EID=B.EID AND A.MASTERPLUNO=B.MASTERPLUNO ");
        sqlbuf.append(" LEFT JOIN DCP_MSPECGOODS_ATTR C ON A.EID=C.EID AND A.MASTERPLUNO=C.MASTERPLUNO ");
        sqlbuf.append(" LEFT JOIN DCP_ATTRIBUTION_LANG CL ON A.EID=CL.EID AND C.ATTRID=CL.ATTRID AND CL.LANG_TYPE='"+langtype+"' ");
        sqlbuf.append(" LEFT JOIN DCP_ATTRIBUTION_VALUE D ON A.EID=D.EID AND C.ATTRID=D.ATTRID and D.STATUS='100' ");
        sqlbuf.append(" LEFT JOIN DCP_ATTRIBUTION_VALUE_LANG DL ON A.EID=DL.EID AND D.ATTRID=DL.ATTRID AND D.ATTRVALUEID=DL.ATTRVALUEID AND DL.LANG_TYPE='"+langtype+"' ");
        
        //【ID1032300】 [货郎]多规格商品，商品图片编辑，默认没有展示商品规格，点添加又展示了所有该规格的值，而不是已选规格。  by jinzma 20230419
        sqlbuf.append(" LEFT JOIN DCP_MSPECGOODS_ATTR_VALUE E ON A.EID=E.EID AND A.MASTERPLUNO=E.MASTERPLUNO AND D.ATTRID=E.ATTRID and D.ATTRVALUEID=E.ATTRVALUEID ");
        
        sqlbuf.append(" LEFT JOIN DCP_MSPECGOODS_SUBGOODS G ON A.EID=G.EID AND A.MASTERPLUNO=G.MASTERPLUNO ");
        sqlbuf.append(" LEFT JOIN DCP_MSPECGOODS_SUBGOODS_LANG GL ON A.EID=GL.EID AND A.MASTERPLUNO=GL.MASTERPLUNO AND G.FEATURENO=GL.FEATURENO AND GL.LANG_TYPE='"+langtype+"' ");
        sqlbuf.append(" LEFT JOIN DCP_GOODS_LANG GLL ON A.EID=GLL.EID AND G.PLUNO=GLL.PLUNO  AND GLL.LANG_TYPE='"+langtype+"' ");
        sqlbuf.append(" LEFT JOIN DCP_UNIT_LANG U on A.EID=U.EID  and G.UNIT=U.UNIT  AND U.LANG_TYPE='"+langtype+"' ");
        sqlbuf.append(" LEFT JOIN DCP_ATTRGROUP_LANG N on A.EID=N.EID  and A.ATTRGROUPID=N.ATTRGROUPID  AND N.LANG_TYPE='"+langtype+"' ");
        
        
        sqlbuf.append(" where a.eid='"+req.geteId()+"' and A.MASTERPLUNO='"+req.getRequest().getMasterPluNo()+"' ");
        sqlbuf.append(" ORDER BY C.SORTID,E.ATTRVALUEID");
        
        return sqlbuf.toString();
        
    }
    
}
