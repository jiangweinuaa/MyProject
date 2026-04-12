package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CategoryGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_CategoryGoodsQueryRes;
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
 * @description: 分类下属商品
 * @author: wangzyc
 * @create: 2021-06-16
 */
public class DCP_CategoryGoodsQuery extends SPosBasicService<DCP_CategoryGoodsQueryReq, DCP_CategoryGoodsQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CategoryGoodsQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        List<DCP_CategoryGoodsQueryReq.level2Elm> categoryList = req.getRequest().getCategoryList();

        if(CollectionUtils.isEmpty(categoryList)){
            errMsg.append("商品分类列表不能为空值 ");
            isFail = true;

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CategoryGoodsQueryReq> getRequestType() {
        return new TypeToken<DCP_CategoryGoodsQueryReq>(){};
    }

    @Override
    protected DCP_CategoryGoodsQueryRes getResponseType() {
        return new DCP_CategoryGoodsQueryRes();
    }

    @Override
    protected DCP_CategoryGoodsQueryRes processJson(DCP_CategoryGoodsQueryReq req) throws Exception {
        DCP_CategoryGoodsQueryRes res = null;
        res = this.getResponse();
        String langType = req.getLangType();

        List<DCP_CategoryGoodsQueryReq.level2Elm> categoryList = req.getRequest().getCategoryList();

        res.setDatas(new ArrayList<DCP_CategoryGoodsQueryRes.level1Elm>());
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getData = this.doQueryData(sql, null);

        // 过滤
        Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
        condition.put("PLUNO", true);
        condition.put("CATEGORY", true);
        // 调用过滤函数
        List<Map<String, Object>> getHeader = MapDistinct.getMap(getData, condition);

        if(!CollectionUtils.isEmpty(getHeader)){
            for (DCP_CategoryGoodsQueryReq.level2Elm level2Elm : categoryList) {
                DCP_CategoryGoodsQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setCategory(level2Elm.getCategory());
                level1Elm.setCategoryName(level2Elm.getCategoryName());

                level1Elm.setGoodsList(new ArrayList<DCP_CategoryGoodsQueryRes.level2Elm>());
                for (Map<String, Object> oneData : getHeader) {
                    DCP_CategoryGoodsQueryRes.level2Elm level2Elm1 = res.new level2Elm();
                    String category = oneData.get("CATEGORY").toString();
                    if(!category.equals(level1Elm.getCategory())){
                        continue;
                    }
                    String plutype = oneData.get("PLUTYPE").toString();
                    String pluno = oneData.get("PLUNO").toString();
                    String sunit = oneData.get("SUNIT").toString();
                    String uname = oneData.get("UNAME").toString();
                    String price = oneData.get("PRICE").toString();

                    level2Elm1.setPluNo(pluno);
                    level2Elm1.setPluType(plutype);
                    level2Elm1.setSUnit(sunit);
                    level2Elm1.setSUnitName(uname);
                    level2Elm1.setPrice(price);
                    level2Elm1.setPluName("");

                    level2Elm1.setPluName_lang(new ArrayList<DCP_CategoryGoodsQueryRes.level3Elm>());
                    for (Map<String, Object> oneData2 : getData) {
                        String pluno2 = oneData2.get("PLUNO").toString();

                        if(!pluno.equals(pluno2)){
                            continue;
                        }

                        String lang_type = oneData2.get("LANG_TYPE").toString();
                        String plu_name = oneData2.get("PLU_NAME").toString();

                        if(Check.Null(plu_name)){
                            continue;
                        }

                        if(lang_type.equals(langType)){
                            level2Elm1.setPluName(plu_name);
                        }
                        DCP_CategoryGoodsQueryRes.level3Elm level3Elm = res.new level3Elm();
                        level3Elm.setLangType(langType);
                        level3Elm.setName(plu_name);
                        level2Elm1.getPluName_lang().add(level3Elm);
                    }

                    level1Elm.getGoodsList().add(level2Elm1);
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
    protected String getQuerySql(DCP_CategoryGoodsQueryReq req) throws Exception {
        String sql = "";
        String langType = req.getLangType();
        StringBuffer sqlbuf = new StringBuffer("");
        List<DCP_CategoryGoodsQueryReq.level2Elm> categoryList = req.getRequest().getCategoryList();

        sqlbuf.append(" SELECT a.CATEGORY,a.PLUTYPE ,a.PLUNO ,b.LANG_TYPE ,b.PLU_NAME,a.PRICE,a.SUNIT ,c.UNAME " +
                " FROM DCP_GOODS a  " +
                " LEFT JOIN DCP_GOODS_LANG b ON a.EID  = b.EID  AND a.PLUNO  = b.PLUNO " +
                " LEFT JOIN DCP_UNIT_LANG c ON a.EID = c.EID  AND a.SUNIT  = c.UNIT AND c.LANG_TYPE  = '"+langType+"' " +
                " WHERE a.EID  = '"+req.geteId()+"' ");

        if(!CollectionUtils.isEmpty(categoryList)){
            sqlbuf.append(" AND a.CATEGORY in (");
            for (DCP_CategoryGoodsQueryReq.level2Elm level2Elm : categoryList) {
                sqlbuf.append(" '"+level2Elm.getCategory()+"',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length()-1);
            sqlbuf.append(")");
        }
        sql = sqlbuf.toString();
        return sql;
    }
}
