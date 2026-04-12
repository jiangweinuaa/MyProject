package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BeforeDishQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_BeforeDishQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * @description: KDS预制菜品查询
 * @author: wangzyc
 * @create: 2021-10-15
 */
public class DCP_BeforeDishQuery_Open extends SPosBasicService<DCP_BeforeDishQuery_OpenReq, DCP_BeforeDishQuery_OpenRes>
{
    Logger logger = LogManager.getLogger(DCP_BeforeDishQuery_Open.class.getName());

    @Override
    protected boolean isVerifyFail(DCP_BeforeDishQuery_OpenReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_BeforeDishQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null)
        {
            errMsg.append("request不可为空值, ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String shopId = request.getShopId();
        if(Check.Null(shopId))
        {
            errMsg.append("shopId不可为空值, ");
            isFail = true;
        }


        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BeforeDishQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_BeforeDishQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_BeforeDishQuery_OpenRes getResponseType() {
        return new DCP_BeforeDishQuery_OpenRes();
    }

    @Override
    protected DCP_BeforeDishQuery_OpenRes processJson(DCP_BeforeDishQuery_OpenReq req) throws Exception {
        DCP_BeforeDishQuery_OpenRes res = this.getResponseType();
        String eId = req.geteId();
        String shopId = req.getRequest().getShopId();
        String companyId = req.getBELFIRM();
        String orgForm = req.getOrg_Form();
        String keyTxt = req.getRequest().getKeyTxt();

        res.setDatas(new ArrayList<>());

        int totalRecords = 0; // 总笔数
        int totalPages = 0;
        try {
            String sql = "";
            ///if Org_Form==0 公司时，所属公司BELFIRM 就等于组织自己
            if (Check.Null(companyId)) {
                ///组织类型 0-公司  1-组织  2-门店 3-其它
                if ("0".equals(orgForm)) {
                    companyId = shopId;
                } else {
                    sql = " select belfirm from dcp_org where eid='" + eId + "' and organizationno='" + shopId + "' ";
                    List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                    companyId = getQData.get(0).get("BELFIRM").toString();
                }
            }

            sql = this.getQuerySql(req, companyId);
            List<Map<String, Object>> getGoodsDetails = this.doQueryData(sql, null);


            //商品取价计算
            Map<String, Boolean> getPriceCondition = new HashMap<String, Boolean>(); //查詢條件
            getPriceCondition.put("PLUNO", true);
            getPriceCondition.put("PUNIT", true);
            getPriceCondition.put("BASEUNIT", true);

            //调用过滤函数
            List<Map<String, Object>> getPriceQHeader = MapDistinct.getMap(getGoodsDetails, getPriceCondition);
            MyCommon mc = new MyCommon();
            List<Map<String, Object>> getPrice = new ArrayList<Map<String, Object>>();
            getPrice = mc.getSalePrice_distriPrice(dao,eId,companyId,shopId,getPriceQHeader,companyId);

            if (!CollectionUtils.isEmpty(getGoodsDetails))
            {
                String num = getGoodsDetails.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                //算總頁數
                if (req.getPageSize() != 0 && req.getPageNumber() != 0)
                {
                    totalPages = totalRecords / req.getPageSize();
                    totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                }

                DCP_BeforeDishQuery_OpenRes.level1Elm lv1 = res.new level1Elm();

                //这个返回结构尼玛谁订的，严重不符，严重歧义误导，纯属瞎搞
                //按照你的逻辑干吧：关键字查询固定返回一个类搜索;有分类查询固定返回第一个分类的;无分类也固定返回搜索的
                String category = "";
                String category_name = "";
                if (!Check.Null(keyTxt))
                {
                    category = "query";
                    category_name = "搜索";
                }
                else
                {
                    if (!Check.Null(req.getRequest().getCategory()))
                    {
                        category = getGoodsDetails.get(0).get("CATEGORY").toString();
                        category_name = getGoodsDetails.get(0).get("CATEGORY_NAME").toString();
                    }
                    else
                    {
                        category = "query";
                        category_name = "搜索";
                    }
                }

                lv1.setCategoryId(category);
                lv1.setCategoryName(category_name);
                lv1.setGoodsList(new ArrayList<>());

                Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                condition.put("CATEGORY", true);
                // 调用过滤函数
                List<Map<String, Object>> getHeader = MapDistinct.getMap(getGoodsDetails, condition);
                if (!org.apache.cxf.common.util.CollectionUtils.isEmpty(getHeader))
                {
                    for (Map<String, Object> header : getHeader)
                    {
                        String v_category = header.get("CATEGORY").toString();

                        for (Map<String, Object> getGoodsDetail : getGoodsDetails)
                        {
                            String category2 = getGoodsDetail.get("CATEGORY").toString();
                            if (!v_category.equals(category2))
                            {
                                continue;
                            }
                            DCP_BeforeDishQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                            String pluno = getGoodsDetail.get("PLUNO").toString();
                            String plu_name = getGoodsDetail.get("PLU_NAME").toString();
                            String plubarCode = getGoodsDetail.get("PLUBARCODE").toString();
                            String remainQty = getGoodsDetail.get("REMAINQTY").toString();
                            String punit = getGoodsDetail.get("PUNIT").toString();
                            String baseUnit = getGoodsDetail.get("BASEUNIT").toString();
                            String unitratio = getGoodsDetail.get("UNITRATIO").toString();
                            String featureNo = getGoodsDetail.get("FEATURENO").toString();
                            String featureName = getGoodsDetail.get("FEATURENAME").toString();
                            String pUName = getGoodsDetail.get("PUNAME").toString();
                            String baseQty = getGoodsDetail.get("OQTY").toString();
                            //商品取价
                            Map<String, Object> condiV = new HashMap<String, Object>();
                            condiV.put("PLUNO", pluno);
                            condiV.put("PUNIT", punit);
                            List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                            condiV.clear();
                            String price = "0";
                            String distriPrice = "0";
                            if (priceList != null && priceList.size() > 0)
                            {
                                price = priceList.get(0).get("PRICE").toString();
                                distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                            }

                            lv2.setCategoryId(category2);
                            lv2.setPluNo(pluno);
                            lv2.setPluName(plu_name);
                            lv2.setPluBarCode(plubarCode);
                            lv2.setRemainQty(remainQty);
                            lv2.setDistriPrice(distriPrice);
                            lv2.setPrice(price);
                            lv2.setBaseUnit(baseUnit);
                            lv2.setBaseQty(baseQty);
                            lv2.setUnitRatio(unitratio);
                            lv2.setFeatureNo(featureNo);
                            lv2.setFeatureName(featureName);
                            lv2.setUnitId(punit);
                            lv2.setUnitName(pUName);
                            lv1.getGoodsList().add(lv2);
                        }

                    }
                }

                res.getDatas().add(lv1);

            }

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
        }
        catch (Exception e)
        {
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
    protected String getQuerySql(DCP_BeforeDishQuery_OpenReq req) throws Exception {
        return null;
    }

    protected String getQuerySql(DCP_BeforeDishQuery_OpenReq req, String companyId) throws Exception {
        String sql = "";
        String eId = req.geteId();
        DCP_BeforeDishQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        StringBuffer sqlbuf = new StringBuffer("");
        String langType = req.getLangType();
        String isStock = request.getIsStock();
        if (Check.Null(isStock))
        {
            isStock="";
        }
        String terminalType = request.getTerminalType();
        String category = request.getCategory();
        String keyTxt = request.getKeyTxt();


        //当日
        String bDate=new SimpleDateFormat("yyyyMMdd").format(new Date());

        int pageSize = req.getPageSize();
        int pageNumber = req.getPageNumber();
        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        // 商品模板表
        sqlbuf.append(" "
                              + " with goodstemplate as ("
                              + " select a.templatetype,b.*,c.CATEGORY,c.ISDOUBLEGOODS ,c.PUNIT , c.BASEUNIT from ( " +
                              "select * " +
                              "from ( " +
                              "select temp.*, ROWNUM rn from " +
                              "( "
                              + " select a.* "
                              + " from dcp_goodstemplate a"
                              + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='" + companyId + "'"
                              + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='" + shopId + "'"
                              //and ((a.restrictshop='1' and c2.id is not null) or a.restrictshop='0' or c1.id is not null) 20200701 小凤通知拿掉全部门店
                              + " where a.eid='" + eId + "' and a.status='100' "
                              + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null)) "
                              + "ORDER BY a.TEMPLATETYPE DESC, a.CREATETIME DESC " +
                              ")temp " +
                              ") " +
                              "where rn = 1 " +
                              ") a"
                              + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100' " +
                              " LEFT JOIN DCP_GOODS c ON  a.EID  = b.EID  AND b.PLUNO  = c.PLUNO "
                              + " )"
                              + " ");

        sqlbuf.append("SELECT * FROM ( " +
                              "  SELECT count(*) over() num, row_number() OVER (ORDER BY c.pluno) AS rn," +
                              "  a.CATEGORY,  b.CATEGORY_NAME, c.templatetype,c.pluno, e.PLU_NAME, d.UNSIDE , d.UNCOOK, d.UNCALL, f.FEATURENO, g.FEATURENAME, h.PLUBARCODE ," +
                              " c.PUNIT, i.UNAME AS pUName, c.BASEUNIT, j.uname AS baseName, k.UNITRATIO,NVl(l.remainQty,0) REMAINQTY,k.oqty  " +
                              " FROM DCP_CATEGORY a " +
                              " LEFT JOIN DCP_CATEGORY_LANG b ON a.EID = b.EID AND a.CATEGORY = b.CATEGORY AND b.LANG_TYPE = '" + langType + "' " +
                              " LEFT JOIN goodstemplate c ON a.EID = b.EID AND a.CATEGORY = c.CATEGORY AND c.CANSALE = 'Y' " +
                              " LEFT JOIN DCP_KDSDISHES_CONTROL d ON a.EID = d.EID AND d.CATEGORY = a.CATEGORY AND d.PLUNO = c.pluno AND d.SHOPID = '" + shopId + "' " +
                              " LEFT JOIN DCP_GOODS_LANG e ON a.EID = e.EID AND c.pluno = e.PLUNO AND e.LANG_TYPE = '" + langType + "'  " +
                              " LEFT JOIN DCP_GOODS_FEATURE f ON a.eid = f.EID AND c.PLUNO = f.PLUNO  " +
                              " LEFT JOIN DCP_GOODS_FEATURE_LANG g ON a.EID = g.EID AND f.FEATURENO = g.FEATURENO AND c.PLUNO = g.PLUNO AND g.LANG_TYPE = '" + langType + "' " +
                              " LEFT JOIN DCP_GOODS_BARCODE h ON a.EID = h.EID AND c.pluno = h.PLUNO AND c.punit = h.UNIT AND (f.FEATURENO = h.FEATURENO or h.FEATURENO is null or h.FEATURENO=' ') AND h.STATUS = '100' " +
                              " LEFT JOIN DCP_UNIT_LANG i ON a.EID = i.EID AND c.punit = i.UNIT AND i.LANG_TYPE = '" + langType + "' " +
                              " LEFT JOIN DCP_UNIT_LANG j ON a.EID = j.EID AND c.baseUnit = j.UNIT AND j.LANG_TYPE = '" + langType + "'  " +
                              " LEFT JOIN DCP_GOODS_UNIT k ON a.EID = k.EID AND c.pluno = k.PLUNO AND c.punit = k.unit AND c.baseunit = k.OUNIT " +
                              " LEFT JOIN ( " +
                              " SELECT SUM(nvl(a.AVAILQTY,0))  remainQty,a.PLUNO ,trim(a.FEATURENO) FEATURENO,a.BASEUNIT from DCP_PROCESSTASK_DETAIL a " +
                              " inner JOIN DCP_PROCESSTASK b on a.eid=b.eid and a.shopid=b.shopid and a.processtaskno=b.processtaskno " +
                              " WHERE a.eid = '" + eId + "' AND a.shopid  = '" + shopId + "' " +
                              " AND a.BDATE='"+bDate+"' AND B.OTYPE='BEFORE' " + //当日预制菜剩余数量
                              " GROUP BY a.pluno, trim(a.FEATURENO), a.BASEUNIT ) l" +
                              " ON c.pluno = l.pluno  AND c.punit = l.BASEUNIT  AND NVL(f.FEATURENO,0) = NVL( l.FEATURENO,0) " +
                              " WHERE a.EID = '" + eId + "' AND a.DOWN_CATEGORYQTY = 0   ");
        if ("Y".equals(isStock))
        {
            sqlbuf.append(" and l.remainQty >0 ");
        }
        if ("N".equals(isStock))
        {
            sqlbuf.append(" and NVl(l.remainQty, 0)  =0 ");
        }
        if ("0".equals(terminalType)) {
            sqlbuf.append(" AND (d.UNSIDE IS NULL OR d.UNSIDE = 'N') ");
        } else if ("1".equals(terminalType)) {
            sqlbuf.append(" AND (d.UNCOOK IS NULL OR d.UNCOOK = 'N') ");
        } else if ("2".equals(terminalType)) {
            sqlbuf.append(" AND (d.UNCALL IS NULL OR d.UNCALL = 'N') ");
        }

        if (!Check.Null(keyTxt))
        {
            sqlbuf.append(" and (c.pluno like '%%"+keyTxt+"%%' or e.PLU_NAME like '%%"+keyTxt+"%%') ");
        }
        else
        {
            //分类改为非必传
            if (!Check.Null(category))
            {
                sqlbuf.append(" and a.CATEGORY='"+category+"' ");
            }
        }

        sqlbuf.append(" and not EXISTS(select 1 from goodstemplate where pluno = c.pluno and PUNIT=c.PUNIT and BASEUNIT=c.BASEUNIT and templatetype > c.templatetype) " + //相同商品先取门店记录，没有再取通用模板
                              ") WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }
}
