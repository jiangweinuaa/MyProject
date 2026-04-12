package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FinalCategoryQueryReq;
import com.dsc.spos.json.cust.res.DCP_FinalCategoryQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 末级商品分类查询
 * @author: wangzyc
 * @create: 2021-06-16
 */
public class DCP_FinalCategoryQuery extends SPosBasicService<DCP_FinalCategoryQueryReq, DCP_FinalCategoryQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_FinalCategoryQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_FinalCategoryQueryReq> getRequestType() {
        return new TypeToken<DCP_FinalCategoryQueryReq>(){};
    }

    @Override
    protected DCP_FinalCategoryQueryRes getResponseType() {
        return new DCP_FinalCategoryQueryRes();
    }

    @Override
    protected DCP_FinalCategoryQueryRes processJson(DCP_FinalCategoryQueryReq req) throws Exception {
        DCP_FinalCategoryQueryRes res = null;
        res = this.getResponse();
        res.setDatas(new ArrayList<DCP_FinalCategoryQueryRes.level1Elm>());

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> data = this.doQueryData(sql, null);

        // 过滤
        Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
        condition.put("CATEGORY", true);
        // 调用过滤函数
        List<Map<String, Object>> getHeader = MapDistinct.getMap(data, condition);

        // 过滤

        if(!CollectionUtils.isEmpty(getHeader)){
            for (Map<String, Object> oneData : getHeader) {
                DCP_FinalCategoryQueryRes.level1Elm level1Elm = res.new level1Elm();
                String zh_category_name = oneData.get("ZH_CATEGORY_NAME").toString();
                String category = oneData.get("CATEGORY").toString();

                level1Elm.setCategory(category);
                level1Elm.setCategoryName(zh_category_name);

                level1Elm.setCategoryName_lang(new ArrayList<DCP_FinalCategoryQueryRes.level2Elm>());
                for (Map<String, Object> oneData2 : data) {
                    DCP_FinalCategoryQueryRes.level2Elm level2Elm = res.new level2Elm();
                    String lang_type = oneData2.get("LANG_TYPE").toString();
                    String category2 = oneData2.get("CATEGORY").toString();
                    String category_name = oneData2.get("CATEGORY_NAME").toString();
                    if(!category.equals(category2)){
                        continue;
                    }

                    level2Elm.setLangType(lang_type);
                    level2Elm.setName(category_name);
                    level1Elm.getCategoryName_lang().add(level2Elm);
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
    protected String getQuerySql(DCP_FinalCategoryQueryReq req) throws Exception {
        String sql = "";
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append("SELECT DISTINCT a.CATEGORY, b.CATEGORY_NAME AS zh_CATEGORY_NAME, c.LANG_TYPE, c.CATEGORY_NAME " +
                " FROM DCP_GOODS a " +
                " LEFT JOIN DCP_CATEGORY_LANG b ON a.EID = b.EID AND a.CATEGORY = b.CATEGORY AND b.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_CATEGORY_LANG c ON a.EID = c.EID AND a.CATEGORY = c.CATEGORY " +
                " WHERE a.EID = '"+req.geteId()+"'");
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" AND (a.CATEGORY LIKE '%%"+keyTxt+"%%' OR b.CATEGORY_NAME LIKE '%%"+keyTxt+"') ");
        }
        sqlbuf.append(" ORDER BY a.CATEGORY");
        sql = sqlbuf.toString();
        return sql;
    }
}
