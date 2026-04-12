package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DishControlQuery_OpenReq;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_DishControlQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: KDS菜品流程控制查询
 * @author: wangzyc
 * @create: 2021-09-13
 */
public class DCP_DishControlQuery_Open extends SPosBasicService<DCP_DishControlQuery_OpenReq, DCP_DishControlQuery_OpenRes>
{
    Logger logger = LogManager.getLogger(DCP_DishControlQuery_Open.class.getName());

    @Override
    protected boolean isVerifyFail(DCP_DishControlQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_DishControlQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_DishControlQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_DishControlQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_DishControlQuery_OpenRes getResponseType() {
        return new DCP_DishControlQuery_OpenRes();
    }

    @Override
    protected DCP_DishControlQuery_OpenRes processJson(DCP_DishControlQuery_OpenReq req) throws Exception {
        DCP_DishControlQuery_OpenRes res = this.getResponseType();
        try {
            int totalRecords = 0; // 总笔数
            int totalPages = 0;

            res.setDatas(new ArrayList<>());
            String sql = getQuerySql(req);
            List<Map<String, Object>> getPlunos = this.doQueryData(sql, null);


            // 过滤
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("CATEGORY", true);
            // 调用过滤函数
            List<Map<String, Object>> categorys = MapDistinct.getMap(getPlunos, condition);


            condition.clear();
            condition.put("CATEGORY", true);
            condition.put("PLUNO", true);
            List<Map<String, Object>> plunos = MapDistinct.getMap(getPlunos, condition);

            if (!CollectionUtils.isEmpty(categorys))
            {
                String num = categorys.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> categoryOne : categorys) {
                    DCP_DishControlQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                    String category = categoryOne.get("CATEGORY").toString();
                    String categoryName = categoryOne.get("CATEGORY_NAME").toString();
                    lv1.setCategoryId(category);
                    lv1.setCategoryName(categoryName);
                    lv1.setGoodsList(new ArrayList<>());
                    if (!CollectionUtils.isEmpty(plunos)) {
                        for (Map<String, Object> plunoOne : plunos) {
                            String category1 = plunoOne.get("CATEGORY").toString();
                            String pluno = plunoOne.get("PLUNO").toString();
                            String plu_name = plunoOne.get("PLU_NAME").toString();
                            String unside = plunoOne.get("UNSIDE").toString();
                            String uncook = plunoOne.get("UNCOOK").toString();
                            String uncall = plunoOne.get("UNCALL").toString();
                            // 过滤不属于当前分类的商品
                            if (!category1.equals(category)) {
                                continue;
                            }
                            if(Check.Null(pluno)){
                                continue;
                            }
                            DCP_DishControlQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                            lv2.setPluNo(pluno);
                            lv2.setPluName(plu_name);
                            lv2.setUnSide(unside);
                            lv2.setUnCook(uncook);
                            lv2.setUnCall(uncall);
                            lv1.getGoodsList().add(lv2);
                        }
                    }
                    res.getDatas().add(lv1);
                }
            }
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            // TODO: handle exception
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_DishControlQuery_OpenReq req) throws Exception {
        String sql = "";
        String eId = req.geteId();
        String companyId = req.getBELFIRM();
        DCP_DishControlQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        StringBuffer sqlbuf = new StringBuffer("");
        String keyTxt = request.getKeyTxt();
        String langType = req.getLangType();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        if (Check.Null(companyId) || companyId.equals("null")) {
            // 查询下组织表 在服务器上回出现 Token 中的 request 的 belfirm 为null 的情况 本地有值
            sql = "select BELFIRM from DCP_ORG where EID = '" + eId + "' and ORG_FORM = '2' and ORGANIZATIONNO = '" + shopId + "'";
            List<Map<String, Object>> getBelfirm = this.doQueryData(sql, null);
            if (!org.apache.cxf.common.util.CollectionUtils.isEmpty(getBelfirm)) {
                companyId = getBelfirm.get(0).get("BELFIRM").toString();
            }
        }

        // 商品模板表
        sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.*,c.CATEGORY  from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='" + companyId + "'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='" + shopId + "'"
                //and ((a.restrictshop='1' and c2.id is not null) or a.restrictshop='0' or c1.id is not null) 20200701 小凤通知拿掉全部门店
                + " where a.eid='" + eId + "' and a.status='100' "
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100' " +
                " LEFT JOIN DCP_GOODS c ON  a.EID  = c.EID  AND b.PLUNO  = c.PLUNO "
                + " where a.rn=1 "
                + " )"
                + " ");
        sqlbuf.append("select * from ( " +
                " SELECT  count(DISTINCT a.CATEGORY) OVER () AS num, DENSE_RANK() OVER (ORDER BY a.CATEGORY) AS rn ," +
                " a.CATEGORY, b.CATEGORY_NAME, c.pluno, e.PLU_NAME, d.UNSIDE , d.UNCOOK, d.UNCALL " +
                " FROM DCP_CATEGORY a " +
                " LEFT JOIN DCP_CATEGORY_LANG b ON a.EID = b.EID AND a.CATEGORY = b.CATEGORY AND b.LANG_TYPE = '" + langType + "' " +
                " LEFT JOIN goodstemplate c ON a.EID = b.EID AND a.CATEGORY = c.CATEGORY AND c.CANSALE = 'Y' " +
                " LEFT JOIN DCP_KDSDISHES_CONTROL d ON a.EID = d.EID AND d.CATEGORY = a.CATEGORY AND d.PLUNO = c.pluno AND d.SHOPID = '" + shopId + "' " +
                " LEFT JOIN DCP_GOODS_LANG e ON a.EID = e.EID AND c.pluno = e.PLUNO AND e.LANG_TYPE = '" + langType + "' " +
                " WHERE a.EID = '"+eId+"' AND a.DOWN_CATEGORYQTY = 0 ");
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" AND  (c.pluno LIKE '%%" + keyTxt + "%%' OR e.PLU_NAME LIKE '%%" + keyTxt + "%%')");
        }
        sqlbuf.append(" ORDER BY a.CATEGORY,c.pluno");
        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        return sqlbuf.toString();
    }
}
