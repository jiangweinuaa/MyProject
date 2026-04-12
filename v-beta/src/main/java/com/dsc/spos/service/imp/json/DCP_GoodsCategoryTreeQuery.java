package com.dsc.spos.service.imp.json;

import java.util.*;
import java.util.stream.Collectors;
import com.dsc.spos.json.cust.req.DCP_GoodsCategoryTreeQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsCategoryTreeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.commons.collections4.CollectionUtils;

public class DCP_GoodsCategoryTreeQuery extends SPosBasicService<DCP_GoodsCategoryTreeQueryReq, DCP_GoodsCategoryTreeQueryRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_GoodsCategoryTreeQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_GoodsCategoryTreeQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsCategoryTreeQueryReq>(){};
    }
    
    @Override
    protected DCP_GoodsCategoryTreeQueryRes getResponseType() {
        return new DCP_GoodsCategoryTreeQueryRes();
    }
    
    @Override
    protected DCP_GoodsCategoryTreeQueryRes processJson(DCP_GoodsCategoryTreeQueryReq req) throws Exception {
        //取得 SQL
        String sql;
        
        //查詢資料
        DCP_GoodsCategoryTreeQueryRes res = this.getResponse();
        DCP_GoodsCategoryTreeQueryRes.data datas = res.new data();
        datas.setBrand(new ArrayList<>());
        datas.setSeries(new ArrayList<>());
        datas.setCategory(new ArrayList<>());
        datas.setAttr(new ArrayList<>());
        datas.setAttrGroup(new ArrayList<>());
        datas.setTags(new ArrayList<>());
        
        try {
            
            String ISHTTPS = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
            String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";
            String DomainName = PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
            
            //根据条件
            if (req.getRequest().getBrand() != null && req.getRequest().getBrand().equals("Y")) {
                sql = this.getQuerySql(req);
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (getQData != null && !getQData.isEmpty()) {
                    for (Map<String, Object> oneData : getQData) {
                        DCP_GoodsCategoryTreeQueryRes.levelbrand oneLv1 = res.new levelbrand();
                        
                        String brand = oneData.get("BRAND").toString();
                        String brandName = oneData.get("BRANDNAME").toString();
                        oneLv1.setBrandNo(brand);
                        oneLv1.setBrandName(brandName);
                        
                        datas.getBrand().add(oneLv1);
                    }
                }
            }
            
            //根据条件
            if (req.getRequest().getSeries() != null && req.getRequest().getSeries().equals("Y")) {
                //查询系列
                sql = this.getQuerySql2(req);
                List<Map<String, Object>> getQData2 = this.doQueryData(sql, null);
                if (getQData2 != null && !getQData2.isEmpty()) {
                    for (Map<String, Object> oneData : getQData2) {
                        DCP_GoodsCategoryTreeQueryRes.levelseries oneLv2 = res.new levelseries();
                        String series = oneData.get("SERIES").toString();
                        String seriesName = oneData.get("SERIESNAME").toString();
                        
                        // 處理調整回傳值；
                        oneLv2.setSeriesNo(series);
                        oneLv2.setSeriesName(seriesName);
                        
                        datas.getSeries().add(oneLv2);
                    }
                }
            }
            
            //【ID1030278】 //【3.0】货郎 门店管理，添加商品，商品加载很慢； by jinzma 20221213
            String isOneLevel = req.getRequest().getIsOneLevel();
            if (Check.Null(isOneLevel)) {
                isOneLevel = "N";
            }
            if (req.getRequest().getCategory() != null) {
                sql = this.getQueryCatSql(req, isOneLevel);
                List<Map<String, Object>> allDatas = this.doQueryData(sql, null);
                //返回所有的分类层级
                if (req.getRequest().getCategory().equals("Y") && isOneLevel.equals("N")) {
                    if (allDatas != null && !allDatas.isEmpty()) {
                        //过滤只要1级分类
                        Map<String, Object> condV = new HashMap<>();
                        condV.put("CATEGORYLEVEL", "1");
                        List<Map<String, Object>> LV1Category = MapDistinct.getWhereMap(allDatas, condV, true);
                        
                        //非1级分类
                        List<Map<String, Object>> listCategory = allDatas.stream().filter(p -> !p.get("CATEGORYLEVEL").toString().equals("1")).collect(Collectors.toList());
                        
                        for (Map<String, Object> oneData : LV1Category) {
                            DCP_GoodsCategoryTreeQueryRes.levelcategory oneLv1 = res.new levelcategory();
                            
                            String category = oneData.get("CATEGORY").toString();
                            String categoryName = oneData.get("CATEGORYNAME").toString();
                            String upCategory = oneData.get("UPCATEGORY").toString();
                            String upCategoryName = oneData.get("UPCATEGORYNAME").toString();
                            String categoryLevel = oneData.get("CATEGORYLEVEL").toString();
                            // 新增商品分类图片
                            String categoryImage = oneData.get("CATEGORYIMAGE").toString();
                            
                            oneLv1.setCategory(category);
                            oneLv1.setCategoryName(categoryName);
                            oneLv1.setUpCategory(upCategory);
                            oneLv1.setCategoryLevel(categoryLevel);
                            oneLv1.setIsAllowSelfBuilt(oneData.get("ISALLOWSELFBUILT").toString());
                            
                            if (!Check.Null(categoryImage)) {
                                // 拼接返回图片路径
                                if (DomainName.endsWith("/")) {
                                    oneLv1.setCategoryImageUrl(httpStr + DomainName + "resource/image/" + categoryImage);
                                } else {
                                    oneLv1.setCategoryImageUrl(httpStr + DomainName + "/resource/image/" + categoryImage);
                                }
                            } else {
                                oneLv1.setCategoryImageUrl("");
                            }
                            
                            oneLv1.setChild(new ArrayList<>());
                            setChildrenDatas(oneLv1, listCategory, req, ISHTTPS, httpStr, DomainName);
                            
                            datas.getCategory().add(oneLv1);
                        }
                    }
                }
                //只返回1级分类
                if (req.getRequest().getCategory().equals("Y") && isOneLevel.equals("Y")) {
                    if (allDatas != null && !allDatas.isEmpty()) {
                        //过滤只要1级分类
                        Map<String, Object> condV = new HashMap<>();
                        condV.put("CATEGORYLEVEL", "1");
                        List<Map<String, Object>> LV1Category = MapDistinct.getWhereMap(allDatas, condV, true);
                        
                        //非1级分类
                        //List<Map<String, Object>> listCategory = allDatas.stream().filter(p -> p.get("CATEGORYLEVEL").toString().equals("1") == false).collect(Collectors.toList());
                        
                        for (Map<String, Object> oneData : LV1Category) {
                            DCP_GoodsCategoryTreeQueryRes.levelcategory oneLv1 = res.new levelcategory();
                            
                            String category = oneData.get("CATEGORY").toString();
                            String categoryName = oneData.get("CATEGORYNAME").toString();
                            String upCategory = oneData.get("UPCATEGORY").toString();
                            String upCategoryName = oneData.get("UPCATEGORYNAME").toString();
                            String categoryLevel = oneData.get("CATEGORYLEVEL").toString();
                            // 新增商品分类图片
                            String categoryImage = oneData.get("CATEGORYIMAGE").toString();
                            
                            oneLv1.setCategory(category);
                            oneLv1.setCategoryName(categoryName);
                            oneLv1.setUpCategory(upCategory);
                            oneLv1.setCategoryLevel(categoryLevel);
                            oneLv1.setIsAllowSelfBuilt(oneData.get("ISALLOWSELFBUILT").toString());
                            
                            if (!Check.Null(categoryImage)) {
                                // 拼接返回图片路径
                                if (DomainName.endsWith("/")) {
                                    oneLv1.setCategoryImageUrl(httpStr + DomainName + "resource/image/" + categoryImage);
                                } else {
                                    oneLv1.setCategoryImageUrl(httpStr + DomainName + "/resource/image/" + categoryImage);
                                }
                            } else {
                                oneLv1.setCategoryImageUrl("");
                            }
                            
                            oneLv1.setChild(new ArrayList<>());
                            datas.getCategory().add(oneLv1);
                        }
                    }
                }
                //只返回当前查询分类和下级第一层分类
                if (!req.getRequest().getCategory().equals("Y") && isOneLevel.equals("Y")) {
                    if (allDatas != null && !allDatas.isEmpty()) {
                        //过滤当前分类
                        Map<String, Object> condV = new HashMap<>();
                        condV.put("CATEGORY", req.getRequest().getCategory());
                        List<Map<String, Object>> LVCategory = MapDistinct.getWhereMap(allDatas, condV, true);
                        
                        //非当前分类
                        List<Map<String, Object>> listCategory = allDatas.stream().filter(p -> !p.get("CATEGORY").toString().equals(req.getRequest().getCategory())).collect(Collectors.toList());
                        
                        for (Map<String, Object> oneData : LVCategory) {
                            DCP_GoodsCategoryTreeQueryRes.levelcategory oneLv1 = res.new levelcategory();
                            
                            String category = oneData.get("CATEGORY").toString();
                            String categoryName = oneData.get("CATEGORYNAME").toString();
                            String upCategory = oneData.get("UPCATEGORY").toString();
                            String upCategoryName = oneData.get("UPCATEGORYNAME").toString();
                            String categoryLevel = oneData.get("CATEGORYLEVEL").toString();
                            // 新增商品分类图片
                            String categoryImage = oneData.get("CATEGORYIMAGE").toString();
                            
                            oneLv1.setCategory(category);
                            oneLv1.setCategoryName(categoryName);
                            oneLv1.setUpCategory(upCategory);
                            oneLv1.setCategoryLevel(categoryLevel);
                            oneLv1.setIsAllowSelfBuilt(oneData.get("ISALLOWSELFBUILT").toString());
                            
                            if (!Check.Null(categoryImage)) {
                                // 拼接返回图片路径
                                if (DomainName.endsWith("/")) {
                                    oneLv1.setCategoryImageUrl(httpStr + DomainName + "resource/image/" + categoryImage);
                                } else {
                                    oneLv1.setCategoryImageUrl(httpStr + DomainName + "/resource/image/" + categoryImage);
                                }
                            } else {
                                oneLv1.setCategoryImageUrl("");
                            }
                            
                            oneLv1.setChild(new ArrayList<>());
                            setChildrenDatas(oneLv1, listCategory, req, ISHTTPS, httpStr, DomainName);
                            
                            datas.getCategory().add(oneLv1);
                        }
                    }
                }
                //只返回当前查询分类和下级所有分类
                if (!req.getRequest().getCategory().equals("Y") && isOneLevel.equals("N")) {
                    if (allDatas != null && !allDatas.isEmpty()) {
                        //过滤当前分类
                        Map<String, Object> condV = new HashMap<>();
                        condV.put("CATEGORY", req.getRequest().getCategory());
                        List<Map<String, Object>> LVCategory = MapDistinct.getWhereMap(allDatas, condV, true);
                        
                        //非当前分类
                        List<Map<String, Object>> listCategory = allDatas.stream().filter(p -> !p.get("CATEGORY").toString().equals(req.getRequest().getCategory())).collect(Collectors.toList());
                        
                        for (Map<String, Object> oneData : LVCategory) {
                            DCP_GoodsCategoryTreeQueryRes.levelcategory oneLv1 = res.new levelcategory();
                            
                            String category = oneData.get("CATEGORY").toString();
                            String categoryName = oneData.get("CATEGORYNAME").toString();
                            String upCategory = oneData.get("UPCATEGORY").toString();
                            String upCategoryName = oneData.get("UPCATEGORYNAME").toString();
                            String categoryLevel = oneData.get("CATEGORYLEVEL").toString();
                            // 新增商品分类图片
                            String categoryImage = oneData.get("CATEGORYIMAGE").toString();
                            
                            oneLv1.setCategory(category);
                            oneLv1.setCategoryName(categoryName);
                            oneLv1.setUpCategory(upCategory);
                            oneLv1.setCategoryLevel(categoryLevel);
                            oneLv1.setIsAllowSelfBuilt(oneData.get("ISALLOWSELFBUILT").toString());
                            
                            if (!Check.Null(categoryImage)) {
                                // 拼接返回图片路径
                                if (DomainName.endsWith("/")) {
                                    oneLv1.setCategoryImageUrl(httpStr + DomainName + "resource/image/" + categoryImage);
                                } else {
                                    oneLv1.setCategoryImageUrl(httpStr + DomainName + "/resource/image/" + categoryImage);
                                }
                            } else {
                                oneLv1.setCategoryImageUrl("");
                            }
                            
                            oneLv1.setChild(new ArrayList<>());
                            setChildrenDatas(oneLv1, listCategory, req, ISHTTPS, httpStr, DomainName);
                            
                            datas.getCategory().add(oneLv1);
                        }
                    }
                }
                
            }
            
            
            //根据条件
            if (req.getRequest().getAttr() != null && req.getRequest().getAttr().equals("Y")) {
                //属性
                sql = this.getQuerySql4(req);
                List<Map<String, Object>> getQData4_1 = this.doQueryData(sql, null);
                
                if (getQData4_1 != null && !getQData4_1.isEmpty()) {
                    //属性
                    Map<String, Boolean> condition_attr = new HashMap<>(); //查詢條件
                    condition_attr.put("ATTRID", true);
                    //调用过滤函数
                    List<Map<String, Object>> attrDatas = MapDistinct.getMap(getQData4_1, condition_attr);
                    
                    for (Map<String, Object> oneData : attrDatas) {
                        DCP_GoodsCategoryTreeQueryRes.levelattr oneLv1 = res.new levelattr();
                        String attrId = oneData.get("ATTRID").toString();
                        String attrName = oneData.get("ATTRNAME").toString();
                        oneLv1.setAttrId(attrId);
                        oneLv1.setAttrName(attrName);
                        oneLv1.setAttrValue(new ArrayList<>());
                        
                        Map<String, Object> condition_attrValue = new HashMap<>(); //查詢條件
                        condition_attrValue.put("ATTRID", attrId);
                        //调用过滤函数
                        List<Map<String, Object>> attrValueDatas = MapDistinct.getWhereMap(getQData4_1, condition_attrValue, true);
                        for (Map<String, Object> attrValueData : attrValueDatas) {
                            String attrValueId = attrValueData.get("ATTRVALUEID").toString();
                            if (attrValueId == null || attrValueId.isEmpty()) {
                                continue;
                            }
                            DCP_GoodsCategoryTreeQueryRes.levelattrValue value = res.new levelattrValue();
                            value.setAttrValueId(attrValueId);
                            value.setAttrValueName(attrValueData.get("ATTRVALUENAME").toString());
                            
                            oneLv1.getAttrValue().add(value);
                        }
                        datas.getAttr().add(oneLv1);
                    }
                }
            }
            
            //根据条件
            if (req.getRequest().getAttrGroup() != null && req.getRequest().getAttrGroup().equals("Y")) {
                //属性分组
                sql = this.getQuerySql5(req);
                List<Map<String, Object>> getQData_attrGroup = this.doQueryData(sql, null);
                
                if (getQData_attrGroup != null && !getQData_attrGroup.isEmpty()) {
                    for (Map<String, Object> oneData : getQData_attrGroup) {
                        
                        DCP_GoodsCategoryTreeQueryRes.levelattrGroup oneLv2 = res.new levelattrGroup();
                        String attrGroupId = oneData.get("ATTRGROUPID").toString();
                        String attrGroupName = oneData.get("ATTRGROUPNAME").toString();
                        
                        // 處理調整回傳值；
                        oneLv2.setAttrGroupId(attrGroupId);
                        oneLv2.setAttrGroupName(attrGroupName);
                        
                        datas.getAttrGroup().add(oneLv2);
                    }
                }
            }
            
            //【ID1033123】【标准产品3.0】商品标签及商城副标题展示---中台服务 by jinzma 20230530
            if ("Y".equals(req.getRequest().getTag())){
                sql = " select tagno,tagname from dcp_tagtype_lang "
                        + " where eid='"+req.geteId()+"' and taggrouptype='GOODS' and lang_type='"+req.getLangType()+"' order by tagno ";
                List<Map<String, Object>> getQData_tag = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(getQData_tag)){
                    for (Map<String, Object> oneData : getQData_tag){
                        DCP_GoodsCategoryTreeQueryRes.leveltags oneLv1 = res.new leveltags();
                        oneLv1.setTagNo(oneData.get("TAGNO").toString());
                        oneLv1.setTagName(oneData.get("TAGNAME").toString());
            
                        datas.getTags().add(oneLv1);
                    }
                }
            }
            
            res.setDatas(datas);
            
            return res;
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
        
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_GoodsCategoryTreeQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        
        //查询品牌
        sqlbuf.append("select brand, brandName from ( ");
        sqlbuf.append("select a.brandno as brand,b.BRAND_NAME as brandName "
                + " FROM DCP_BRAND a "
                + " INNER JOIN DCP_BRAND_LANG b on a.eid=b.eid  and a.brandno=b.brandno "
                + " where a.eid ='"+req.geteId()+"' "
                + " and a.status='100'"
                + " and b.LANG_TYPE ='"+req.getLangType()+"' "
        );
        
        sqlbuf.append(" ) TBL ");
        
        return sqlbuf.toString();
    }
    
    protected String getQuerySql2(DCP_GoodsCategoryTreeQueryReq req) {
        StringBuffer sqlbuf = new StringBuffer();
 		/*
 		 *SELECT a.seriesno as series,b.series_NAME as seriesName
				FROM TB_series a
				INNER JOIN TB_series_LANG b on a.eid=b.eid  and a.seriesno=b.seriesno
				where a.company=传入的eid
				AND a.CNFFLT='Y'
				AND b.CNFFLT='Y'
				AND b.LANT_TYPE='传入的langType'
 		 **/
        
        //查询系列
        sqlbuf.append("select series, seriesName from ( ");
        sqlbuf.append("SELECT a.seriesno as series,b.series_NAME as seriesName"
                + " FROM DCP_SERIES a "
                + " INNER JOIN DCP_SERIES_LANG b on a.eid=b.eid  and a.seriesno=b.seriesno"
                + " where a.eid ='"+req.geteId()+"' "
                + " and a.status='100' "
                + " and b.LANG_TYPE ='"+req.getLangType()+"' "
        );
        
        sqlbuf.append(" ) TBL ");
        
        return sqlbuf.toString();
    }
    
    private String getQuerySql4(DCP_GoodsCategoryTreeQueryReq req) {
        String eId = req.geteId();
        String curLangType = req.getLangType();
        
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" select *  from (");
        sqlbuf.append(" SELECT a.attrid,b.ATTRNAME,");
        sqlbuf.append(" C.ATTRVALUEID,CL.ATTRVALUENAME");
        sqlbuf.append(" FROM  DCP_Attribution a ");
        sqlbuf.append(" LEFT JOIN DCP_Attribution_LANG b ON a.EID = b.EID AND a.ATTRID = b.ATTRID and b.LANG_TYPE='"+curLangType+"' ");
        sqlbuf.append(" left join DCP_ATTRIBUTION_VALUE C on a.eid = C.eid and a.ATTRID = c.ATTRID and C.Status='100' ");
        sqlbuf.append(" left join DCP_ATTRIBUTION_VALUE_LANG CL on a.eid = CL.eid and C.ATTRID = CL.ATTRID and C.ATTRVALUEID=CL.ATTRVALUEID and CL.LANG_TYPE='"+curLangType+"' ");
        
        sqlbuf.append(" where a.eid ='"+eId+"' and a.status='100' ");
        sqlbuf.append(")");
        
        return sqlbuf.toString();
    }
    
    protected String getQuerySql5(DCP_GoodsCategoryTreeQueryReq req) {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from ( ");
        sqlbuf.append("select a.ATTRGROUPID,b.ATTRGROUPNAME "
                + " FROM DCP_ATTRGROUP a "
                + " INNER JOIN DCP_ATTRGROUP_LANG b on a.eid=b.eid  and a.ATTRGROUPID=b.ATTRGROUPID "
                + " where a.eid ='"+req.geteId()+"' "
                + " and a.status='100'"
                + " and b.LANG_TYPE ='"+req.getLangType()+"' "
        );
        
        sqlbuf.append(" ) TBL ");
        
        return sqlbuf.toString();
    }
    
    protected String getQueryCatSql(DCP_GoodsCategoryTreeQueryReq req,String isOneLevel) {
        StringBuffer sqlbuf = new StringBuffer();
        String langType = req.getLangType();
        String eid = req.geteId();
        
        
        sqlbuf.append(" SELECT a.CATEGORY, b.category_Name AS CATEGORYName,up_CateGory AS upCategory,"
                + " c.category_name as upCategoryname,"
                + " a.categorylevel,a.status,a.top_category AS topCategory,a.down_categoryQty AS downCategoryQty,"
                + " d.category_name AS topCategoryName,"
                + " b.lang_type AS langType,b.category_Name AS LCategoryname,a.eid,e.categoryImage,a.isallowselfbuilt"
                + " FROM DCP_CATEGORY a "
                + " LEFT JOIN dcp_category_lang b on a.eid=b.eid and a.category=b.category and b.lang_Type='"+langType+"'"
                + " LEFT JOIN dcp_category_lang c on a.eid=c.eid and a.up_CateGory=c.category and c.lang_Type='"+langType+"'"
                + " LEFT JOIN dcp_category_lang d on a.eid=d.eid and a.top_category=d.category and d.lang_Type='"+langType+"'"
                + " LEFT JOIN dcp_category_image e on a.eid=e.eid and a.category=e.category "
                + " where a.eid='"+eid+"' and a.status='100' ");
        
        //【ID1030278】 //【3.0】货郎 门店管理，添加商品，商品加载很慢； by jinzma 20221213
        String category = req.getRequest().getCategory();
        if (!category.equals("Y") && isOneLevel.equals("Y") ){
            sqlbuf.append(" and (a.category='"+category+"' or a.up_category ='"+category+"')");
        }
        
        sqlbuf.append(" order by a.category ");
        return sqlbuf.toString();
    }
    
    /*
     * 获取下一层级信息
     */
    protected List<Map<String, Object>> getChildDatas (List<Map<String, Object>> allDatas,String category) {
        List<Map<String, Object>> datas =new ArrayList<>();
        
        Iterator<Map<String, Object>> iterator = allDatas.iterator();
        while (iterator.hasNext())
        {
            Map<String, Object> map=iterator.next();
            
            //如果上级组织是自己的不要新加
            if(map.get("UPCATEGORY").toString().equals(map.get("CATEGORY").toString()))
            {
                continue;
            }
            
            if(map.get("UPCATEGORY").toString().equals(category))
            {
                datas.add(map);
                
                //上面找到了，这里就移除，a*b*c*d的递归量就小很多
                iterator.remove();
            }
        }
        
        return datas;
    }
    
    /*
     * 循环添加层级
     */
    protected void setChildrenDatas(DCP_GoodsCategoryTreeQueryRes.levelcategory oneLv2,List<Map<String, Object>> allDatas,DCP_GoodsCategoryTreeQueryReq req,String ISHTTPS,String httpStr,String DomainName) {
        try {
            List<Map<String, Object>> nextDatas  = getChildDatas(allDatas,oneLv2.getCategory());
            if(nextDatas != null && !nextDatas.isEmpty()) {
                for (Map<String, Object> datas : nextDatas) {
                    DCP_GoodsCategoryTreeQueryRes.levelcategory oneLv1 = new DCP_GoodsCategoryTreeQueryRes().new levelcategory();
                    
                    String category = datas.get("CATEGORY").toString();
                    String categoryName = datas.get("CATEGORYNAME").toString();
                    String upCategory = datas.get("UPCATEGORY").toString();
                    String categoryLevel = datas.get("CATEGORYLEVEL").toString();
                    // 新增商品分类图片
                    String categoryImage = datas.get("CATEGORYIMAGE").toString();
                    
                    oneLv1.setCategory(category);
                    oneLv1.setCategoryName(categoryName);
                    oneLv1.setUpCategory(upCategory);
                    oneLv1.setCategoryLevel(categoryLevel);
                    oneLv1.setIsAllowSelfBuilt(datas.get("ISALLOWSELFBUILT").toString());
                    
                    if(!Check.Null(categoryImage)){
                        // 拼接返回图片路径
                        if (DomainName.endsWith("/")) {
                            oneLv1.setCategoryImageUrl(httpStr+DomainName+"resource/image/" +categoryImage);
                        } else {
                            oneLv1.setCategoryImageUrl(httpStr+DomainName+"/resource/image/" +categoryImage);
                        }
                    }else{
                        oneLv1.setCategoryImageUrl("");
                    }
                    
                    oneLv1.setChild(new ArrayList<>());
                    
                    setChildrenDatas(oneLv1,allDatas,req,ISHTTPS,httpStr,DomainName);
                    oneLv2.getChild().add(oneLv1);
                    
                }
            }
            
        } catch (Exception ignored) {
        }
        
        
    }
    
    
    
    
    
}
