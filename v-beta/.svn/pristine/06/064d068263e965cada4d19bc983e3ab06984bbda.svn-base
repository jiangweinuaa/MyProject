package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dsc.spos.json.cust.req.DCP_GoodsSetCategoryTreeQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetCategoryTreeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetCategoryTreeQuery extends SPosBasicService<DCP_GoodsSetCategoryTreeQueryReq,DCP_GoodsSetCategoryTreeQueryRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_GoodsSetCategoryTreeQueryReq req) throws Exception {
        return false;
    }
    
    @Override
    protected DCP_GoodsSetCategoryTreeQueryRes processJson(DCP_GoodsSetCategoryTreeQueryReq req) throws Exception {
        DCP_GoodsSetCategoryTreeQueryRes res = this.getResponse();
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        res.setDatas(new ArrayList<DCP_GoodsSetCategoryTreeQueryRes.level1Elm>());
        if(getQData!=null && !getQData.isEmpty()) {
            Map<String, Object> map_condition=new HashMap<String, Object>();
            map_condition.put("UP_CATEGORY", "");//顶层
            List<Map<String, Object>> lstmapOne=MapDistinct.getWhereMap(getQData, map_condition, true);
            if(lstmapOne!=null && !lstmapOne.isEmpty()) {
                for (Map<String, Object> oneData : lstmapOne) {
                    DCP_GoodsSetCategoryTreeQueryRes.level1Elm lv1=res.new level1Elm();
                    
                    lv1.setCATEGORY(oneData.get("CATEGORY").toString());
                    lv1.setCATEGORY_NAME(oneData.get("CATEGORY_NAME").toString());
                    lv1.setUP_CATEGORY(oneData.get("UP_CATEGORY").toString());
                    lv1.setCATEGORYLEVEL(oneData.get("CATEGORYLEVEL").toString());
                    lv1.seteId(oneData.get("EID").toString());
                    lv1.setStatus(oneData.get("STATUS").toString());
                    lv1.setIsAllowSelfBuilt(oneData.get("ISALLOWSELFBUILT").toString());
                    lv1.setTaxCode(oneData.get("TAXCODE").toString());
                    lv1.setTaxName(oneData.get("TAXNAME").toString());
                    lv1.setTaxRate(oneData.get("TAXRATE").toString());
                    CategoryFun(getQData,oneData.get("CATEGORY").toString(),lv1);
                    
                    res.getDatas().add(lv1);
                }
            }
        }
        return res;
    }
    
    @Override
    protected TypeToken<DCP_GoodsSetCategoryTreeQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsSetCategoryTreeQueryReq>(){};
    }
    
    @Override
    protected DCP_GoodsSetCategoryTreeQueryRes getResponseType() {
        return new DCP_GoodsSetCategoryTreeQueryRes();
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_GoodsSetCategoryTreeQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        
        String selfBuiltSearch = "0"; //0查询全部，1查询不允许自建 2查询允许自建；
        
        if (req.getRequest()!=null) {
            selfBuiltSearch = req.getRequest().getSelfBuiltSearch();
            if (Check.Null(selfBuiltSearch)){
                selfBuiltSearch = "0";
            }
        }
        
        sqlbuf.append(""
                + " select a.category,a.categorylevel,a.eid,a.status,a.isallowselfbuilt,"
                + " case when a.category=a.up_category then N'' else trim(a.up_category) end up_category,"
                + " b.category_name,d.taxcode,e.taxrate,f.taxname "
                + " from dcp_category a"
                + " left join dcp_category_lang b on a.eid=b.eid and a.category=b.category and b.lang_type='"+req.getLangType()+"' "
                + " left join dcp_taxgroup_detail c on c.eid=a.eid and c.attrtype='1' and c.attrid=a.category "
                + " left join dcp_taxgroup d on d.eid=c.eid and d.TAXGROUPNO=c.TAXGROUPNO "
                + " left join dcp_taxcategory e on e.eid=d.eid and d.taxcode=e.taxcode "
                + " left join dcp_taxcategory_lang f on f.eid=e.eid and f.taxcode=e.taxcode and f.lang_type='"+req.getLangType()+"' "
                + " where a.eid='"+req.geteId()+"' and a.status='100'"
                + "");
        
        if (selfBuiltSearch.equals("1")){
            sqlbuf.append(" and (a.isallowselfbuilt is null or a.isallowselfbuilt='0') ");
        }else if (selfBuiltSearch.equals("2")){
            sqlbuf.append(" and a.isallowselfbuilt='1' ");
        }
        
        sqlbuf.append(" order by a.categorylevel,a.up_category,a.category");
        
        return sqlbuf.toString();
    }
    
    private void CategoryFun(List<Map<String, Object>> getQData, String CATEGORY,DCP_GoodsSetCategoryTreeQueryRes.level1Elm lv) {
        Map<String, Object> map_condition = new HashMap<String, Object>(); //查詢條件
        map_condition.put("UP_CATEGORY", CATEGORY);
        
        List<Map<String, Object>> lstmap=MapDistinct.getWhereMap(getQData, map_condition, true);
        List<Map<String, String>> collect = lstmap.stream().map(x -> {
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("CATEGORY", x.get("CATEGORY").toString());
            hashMap.put("CATEGORY_NAME", x.get("CATEGORY_NAME").toString());
            hashMap.put("UP_CATEGORY", x.get("UP_CATEGORY").toString());
            hashMap.put("CATEGORYLEVEL", x.get("CATEGORYLEVEL").toString());
            hashMap.put("EID", x.get("EID").toString());
            hashMap.put("STATUS", x.get("STATUS").toString());
            hashMap.put("ISALLOWSELFBUILT", x.get("ISALLOWSELFBUILT").toString());
            return hashMap;
        }).distinct().collect(Collectors.toList());
        if(collect!=null && collect.isEmpty()==false) {
            lv.setChildren(new ArrayList<DCP_GoodsSetCategoryTreeQueryRes.level1Elm>());
            for (Map<String, String> oneData : collect) {
                DCP_GoodsSetCategoryTreeQueryRes.level1Elm lv1=new DCP_GoodsSetCategoryTreeQueryRes().new level1Elm();
                
                lv1.setCATEGORY(oneData.get("CATEGORY").toString());
                lv1.setCATEGORY_NAME(oneData.get("CATEGORY_NAME").toString());
                lv1.setUP_CATEGORY(oneData.get("UP_CATEGORY").toString());
                lv1.setCATEGORYLEVEL(oneData.get("CATEGORYLEVEL").toString());
                lv1.seteId(oneData.get("EID").toString());
                lv1.setStatus(oneData.get("STATUS").toString());
                lv1.setIsAllowSelfBuilt(oneData.get("ISALLOWSELFBUILT").toString());
                lv1.setChildren(new ArrayList<DCP_GoodsSetCategoryTreeQueryRes.level1Elm>());


                CategoryFun(getQData,oneData.get("CATEGORY").toString(),lv1);
                lv.getChildren().add(lv1);
            }
        }
    }
    
    
    
}
