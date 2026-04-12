package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_TagDetailQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_TagDetailQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_TagDetailQuery_Open
 *   說明：生产标签商品查询
 * 服务说明：生产标签商品查询
 * @author wangzyc
 * @since  2021-5-10
 */
public class DCP_TagDetailQuery_Open extends SPosBasicService<DCP_TagDetailQuery_OpenReq, DCP_TagDetailQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_TagDetailQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_TagDetailQuery_OpenReq.level1Elm request = req.getRequest();

        if (Check.Null(request.getMachShopNo()))
        {
            errMsg.append("生产机构编码不可为空值, ");
            isFail = true;
        }
        if(Check.Null(request.getStallId())){
            errMsg.append("档口编号不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_TagDetailQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_TagDetailQuery_OpenReq>(){};
    }

    @Override
    protected DCP_TagDetailQuery_OpenRes getResponseType() {
        return new DCP_TagDetailQuery_OpenRes();
    }

    @Override
    protected DCP_TagDetailQuery_OpenRes processJson(DCP_TagDetailQuery_OpenReq req) throws Exception {
        DCP_TagDetailQuery_OpenRes res = null;
        res = this.getResponseType();

        res.setDatas(new ArrayList<DCP_TagDetailQuery_OpenRes.level1Elm>());

        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> datas = this.doQueryData(sql, null);

            // 商品
            Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查詢條件
            condition1.put("ID", true);
            //调用过滤函数
            List<Map<String, Object>> plunoList= MapDistinct.getMap(datas, condition1);

            // 规格
            Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
            condition2.put("ID", true);
            condition2.put("SPEC", true);
            //调用过滤函数
            List<Map<String, Object>> specList= MapDistinct.getMap(datas, condition2);

            // 规格
            Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查詢條件
            condition3.put("ID", true);
            condition3.put("FEATURENO", true);
            //调用过滤函数
            List<Map<String, Object>>featureList= MapDistinct.getMap(datas, condition3);

            if(!CollectionUtils.isEmpty(plunoList)){
                for (Map<String, Object> pluno : plunoList) {
                    DCP_TagDetailQuery_OpenRes.level1Elm level1Elm = res.new level1Elm();
//                    String tagNo = pluno.get("TAGNO").toString();
//                    String tagName = pluno.get("TAGNAME").toString();
                    String pluNo = pluno.get("ID").toString();
                    String pluName = pluno.get("PLU_NAME").toString();

//                    level1Elm.setTagNo(tagNo);
//                    level1Elm.setTagName(tagName);
                    level1Elm.setPluNo(pluNo);
                    level1Elm.setPluName(pluName);

                    level1Elm.setSpecList(new ArrayList<DCP_TagDetailQuery_OpenRes.level2Elm>());
                    level1Elm.setFeatureList(new ArrayList<DCP_TagDetailQuery_OpenRes.level3Elm>());
                    if(!CollectionUtils.isEmpty(specList)){
                        for (Map<String, Object> spec : specList) {
//                            String tagNo2 = spec.get("TAGNO").toString();
                            String pluNo2 = spec.get("ID").toString();

                            // 过滤属于当前商品的信息
                            if( pluNo.equals(pluNo2)){
                                DCP_TagDetailQuery_OpenRes.level2Elm level2Elm = res.new level2Elm();
                                String specName = spec.get("SPEC").toString();

                                if(Check.Null(specName)){
                                    continue;
                                }
                                level2Elm.setSpecName(specName);
                                level1Elm.getSpecList().add(level2Elm);
                            }
                        }
                    }

                    if(!CollectionUtils.isEmpty(featureList)){
                        for (Map<String, Object> feature : featureList) {
                            String pluNo3 = feature.get("ID").toString();

                            // 过滤属于当前商品的信息
                            if( pluNo.equals(pluNo3)){
                                DCP_TagDetailQuery_OpenRes.level3Elm level3Elm = res.new level3Elm();
                                String featureNo = feature.get("FEATURENO").toString();
                                String featureName = feature.get("FEATURENAME").toString();

                                if(Check.Null(featureNo)){
                                    continue;
                                }
                                level3Elm.setFeatureNo(featureNo);
                                level3Elm.setFeatureName(featureName);
                                level1Elm.getFeatureList().add(level3Elm);
                            }
                        }
                    }
                    res.getDatas().add(level1Elm);
                }
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功!" );
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_TagDetailQuery_OpenReq req) throws Exception {
        String getTagSql = getTag(req);
        List<Map<String, Object>> data = this.doQueryData(getTagSql, null);

        String langType = req.getLangType();
        String eId = req.geteId();

        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.TAGNO, c.TAGNAME, a.ID, b.PLU_NAME, d.SPEC,e.FEATURENO,f.FEATURENAME " +
                " FROM DCP_TAGTYPE_DETAIL a " +
                " LEFT JOIN DCP_GOODS_LANG b ON a.EID = b.EID AND a.ID = b.PLUNO AND b.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_TAGTYPE_LANG c ON a.EID = b.EID AND a.TAGGROUPTYPE = c.TAGGROUPTYPE AND a.TAGNO = c.TAGNO AND a.TAGGROUPNO = c.TAGGROUPNO AND c.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_GOODS_UNIT_LANG d ON a.EID = b.EID AND a.EID = b.EID AND a.ID = d.PLUNO AND d.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_GOODS_FEATURE e ON a.EID = e.EID AND a.ID = e.PLUNO AND e.STATUS = '100' " +
                " LEFT JOIN DCP_GOODS_FEATURE_LANG f ON a.EID = f.EID AND e.FEATURENO = f.FEATURENO AND a.ID = f.PLUNO AND f.LANG_TYPE = '"+langType+"' " +
                " WHERE a.EID = '"+eId+"' AND a.TAGGROUPTYPE = 'GOODS_PROD'");


        if(!CollectionUtils.isEmpty(data)){
            sqlbuf.append(" AND a.TAGNO in (");
            for (Map<String, Object> tag : data) {
                sqlbuf.append("'"+tag.get("TAGNO").toString()+"',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length()-1);
            sqlbuf.append(")");
        }

        sql =sqlbuf.toString();
        return sql;
    }

    /**
     * 查询档口下生产标签范围
     * @param req
     * @return
     */
    private String getTag(DCP_TagDetailQuery_OpenReq req){
        DCP_TagDetailQuery_OpenReq.level1Elm request = req.getRequest();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT TAGNO FROM DCP_ORG_ORDERSET_KDS_TAG WHERE STALLID = '"+request.getStallId()+"' AND ORGANIZATIONNO = '"+request.getMachShopNo()+"' AND EID = '"+req.geteId()+"'");
        sql =sqlbuf.toString();
        return sql;
    }
}
