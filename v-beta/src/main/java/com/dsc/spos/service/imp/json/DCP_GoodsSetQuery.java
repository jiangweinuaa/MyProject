package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_GoodsSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_GoodsSetQuery extends SPosBasicService<DCP_GoodsSetQueryReq, DCP_GoodsSetQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_GoodsSetQueryReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        //必传值不为空
        if (req.getRequest() == null) {
            errMsg.append("request不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_GoodsSetQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsSetQueryReq>() {
        };
    }

    @Override
    protected DCP_GoodsSetQueryRes getResponseType() {
        return new DCP_GoodsSetQueryRes();
    }

    @Override
    protected DCP_GoodsSetQueryRes processJson(DCP_GoodsSetQueryReq req) throws Exception {

        DCP_GoodsSetQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<DCP_GoodsSetQueryRes.level1Elm>());

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        int totalRecords;                                //总笔数
        int totalPages;                                    //总页数
        if (getQData != null && !getQData.isEmpty()) {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            Map<String, Boolean> condition_pluno = new HashMap<String, Boolean>(); //查詢條件
            condition_pluno.put("PLUNO", true);
            //调用过滤函数
            List<Map<String, Object>> getHead = MapDistinct.getMap(getQData, condition_pluno);

            for (Map<String, Object> oneData : getHead) {
                DCP_GoodsSetQueryRes.level1Elm oneLv1 = res.new level1Elm();

                oneLv1.setPluNo(oneData.get("PLUNO").toString());
                oneLv1.setPluName(oneData.get("PLUNAME").toString());
                oneLv1.setBrandNo(oneData.get("BRAND").toString());
                oneLv1.setBrandName(oneData.get("BRAND_NAME").toString());
                oneLv1.setSeries(oneData.get("SERIES").toString());
                oneLv1.setSeriesName(oneData.get("SERIES_NAME").toString());
                oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
                oneLv1.setTaxName(oneData.get("TAXNAME").toString());
                oneLv1.setShortCutCode(oneData.get("SHORTCUT_CODE").toString());
                oneLv1.setWarmType(oneData.get("WARMTYPE").toString());
                oneLv1.setVirtual(oneData.get("VIRTUAL").toString());
                oneLv1.setOpenPrice(oneData.get("OPENPRICE").toString());
                oneLv1.setIsWeight(oneData.get("ISWEIGHT").toString());
                oneLv1.setStockManageType(oneData.get("STOCKMANAGETYPE").toString());
                oneLv1.setMemo(oneData.get("MEMO").toString());
                oneLv1.setStatus(oneData.get("STATUS").toString());
                oneLv1.setPrice(oneData.get("PRICE").toString());
                oneLv1.setSupPrice(oneData.get("SUPPRICE").toString());//标准进货价(默认要货单位)
                oneLv1.setIsBatch(oneData.get("ISBATCH").toString());
                oneLv1.setShelfLife(oneData.get("SHELFLIFE").toString());
                oneLv1.setStockInValidDay(oneData.get("STOCKINVALIDDAY").toString());
                oneLv1.setStockOutValidDay(oneData.get("STOCKOUTVALIDDAY").toString());
                oneLv1.setCheckValidDay(oneData.get("CHECKVALIDDAY").toString());
                oneLv1.setIsHoliday(oneData.get("ISHOLIDAY").toString());
                oneLv1.setBaseUnit(oneData.get("BASEUNIT").toString());
                oneLv1.setsUnit(oneData.get("SUNIT").toString());
                oneLv1.setpUnit(oneData.get("PUNIT").toString());
                oneLv1.setwUnit(oneData.get("WUNIT").toString());
                oneLv1.setProdUnit(oneData.get("PROD_UNIT").toString());
                oneLv1.setBomUnit(oneData.get("BOM_UNIT").toString());
                oneLv1.setcUnit(oneData.get("CUNIT").toString());
                oneLv1.setPurUnit(oneData.get("PURUNIT").toString());
                oneLv1.setBaseUnitName(oneData.get("BASE_UNIT_NAME").toString());
                oneLv1.setsUnitName(oneData.get("SUNIT_NAME").toString());
                oneLv1.setpUnitName(oneData.get("PUNIT_NAME").toString());
                oneLv1.setwUnitName(oneData.get("WUNIT_NAME").toString());
                oneLv1.setProdUnitName(oneData.get("PROD_UNIT_NAME").toString());
                oneLv1.setBomUnitName(oneData.get("BOM_UNIT_NAME").toString());
                oneLv1.setcUnitName(oneData.get("CUNIT_NAME").toString());
                oneLv1.setPurUnitName(oneData.get("PUR_UNIT_NAME").toString());
                oneLv1.setCategory(oneData.get("CATEGORY").toString());
                oneLv1.setCategoryName(oneData.get("CATEGORY_NAME").toString());
                oneLv1.setPluType(oneData.get("PLUTYPE").toString());
                oneLv1.setDescription(oneData.get("DESCRIPTION").toString());
                oneLv1.setRedisUpdateSuccess(oneData.get("REDISUPDATESUCCESS").toString());
                oneLv1.setIsHotGoods(oneData.get("ISHOTGOODS").toString());
                oneLv1.setIsOwnGoods(oneData.get("OWN_GOODS").toString());
                oneLv1.setIsDoubleGoods(oneData.get("ISDOUBLEGOODS").toString());
                oneLv1.setSelfBuiltShopId(oneData.get("SELFBUILTSHOPID").toString());

                res.getDatas().add(oneLv1);
            }

        } else {
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;


    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_GoodsSetQueryReq req) throws Exception {

        String eId = req.geteId();
        String langType = req.getLangType();

        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
        String brandno = req.getRequest().getBrandNo();
        String seriesno = req.getRequest().getSeriesNo();
        String categoryno = req.getRequest().getCategory();
        String pluType = req.getRequest().getPluType();
        String virtual = req.getRequest().getVirtual();
        String openPrice = req.getRequest().getOpenPrice();
        String isWeight = req.getRequest().getIsWeight();
        String isBatch = req.getRequest().getIsBatch();
        String redisUpdateSuccess = req.getRequest().getRedisUpdateSuccess();
        //searchScope：0、全部 1、总部和当前自建门店 2、仅总部 3、全部自建门店 4、仅当前自建门店  by jinzma 20220310
        String searchScope = req.getRequest().getSearchScope();
        if (Check.Null(searchScope)) {
            searchScope = "0";
        }
        String selfBuiltShopId = req.getRequest().getSelfBuiltShopId();   //自建门店

        //分页处理
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow = (pageNumber - 1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer();
        //先注释之前的，商品规格 重量 体积 放到unit表
		/*sqlbuf.append("A.num,A.rn,A.GP,A.PLUNO,A.PLUNAME,A.SPEC,A.SHORTCUT_CODE,A.PUNIT,C.UNAME PUNIT_NAME,D.UNAME CUNIT_NAME,E.UNAME SUNIT_NAME,F.UNAME WUNIT_NAME,G.UNAME BOM_UNIT_NAME,A.SUNIT,A.CUNIT,A.WUNIT,A.BOM_UNIT,A.BASEUNIT,BASE.UNAME BASE_UNIT_NAME,A.PROD_UNIT,PROD.UNAME PROD_UNIT_NAME,A.PURUNIT,PUR.UNAME PUR_UNIT_NAME,A.CATEGORY,A.BRAND,A.SERIES,H.CATEGORY_NAME,I.BRAND_NAME,J.SERIES_NAME,A.ISBATCH, A.SHELFLIFE,A.EID,A.status,A.TAXCODE,M.TAXNAME,A.STOCKINVALIDDAY,A.STOCKOUTVALIDDAY,A.CHECKVALIDDAY,"
				+ " A.PRICE,A.WEIGHT,A.VOLUME,A.WARMTYPE,A.PLUTYPE,A.VIRTUAL,A.OPENPRICE,A.ISWEIGHT,A.STOCKMANAGETYPE,A.MEMO     "
				+ "FROM "
				+ "(SELECT * "
				+ "FROM "
				+ "(SELECT count(*) over() num,row_number() over (order by A.PLUNO) rn,A.*  "
				+ "FROM "
				+ "(SELECT row_number() over (partition by A.PLUNO ORDER BY A.PLUNO)   GP,A.PLUNO,B.PLU_NAME AS PLUNAME,B.SPEC,A.SHORTCUT_CODE,A.BASEUNIT,A.PROD_UNIT,A.PURUNIT,A.PUNIT,A.SUNIT,A.CUNIT,A.WUNIT,A.BOM_UNIT,A.CATEGORY,A.BRAND,A.SERIES,A.ISBATCH, A.SHELFLIFE , A.EID,A.STATUS,A.TAXCODE,A.STOCKINVALIDDAY,A.STOCKOUTVALIDDAY,A.CHECKVALIDDAY,A.PRICE,A.WEIGHT,A.VOLUME,A.WARMTYPE,A.PLUTYPE,A.VIRTUAL,A.OPENPRICE,A.ISWEIGHT,A.STOCKMANAGETYPE,A.MEMO    "
				+ " 	 "
				+ " FROM DCP_GOODS A "
				);*/

        sqlbuf.append(""
                + " SELECT A.num,A.rn,A.GP,A.PLUNO,A.PLUNAME,A.SHORTCUT_CODE,A.PUNIT,C.UNAME PUNIT_NAME,D.UNAME CUNIT_NAME,"
                + " E.UNAME SUNIT_NAME,F.UNAME WUNIT_NAME,G.UNAME BOM_UNIT_NAME,A.SUNIT,A.CUNIT,A.WUNIT,A.BOM_UNIT,A.BASEUNIT,"
                + " BASE.UNAME BASE_UNIT_NAME,A.PROD_UNIT,PROD.UNAME PROD_UNIT_NAME,A.PURUNIT,PUR.UNAME PUR_UNIT_NAME,A.CATEGORY,"
                + " A.BRAND,A.SERIES,H.CATEGORY_NAME,I.BRAND_NAME,J.SERIES_NAME,A.ISBATCH, A.SHELFLIFE,A.EID,A.status,A.TAXCODE,"
                + " M.TAXNAME,A.STOCKINVALIDDAY,A.STOCKOUTVALIDDAY,A.CHECKVALIDDAY,A.ISHOLIDAY,"
                + " A.PRICE,A.WARMTYPE,A.PLUTYPE,A.VIRTUAL,A.OPENPRICE,A.ISWEIGHT,A.STOCKMANAGETYPE,A.MEMO,A.SUPPRICE,"
                + " DGE.DESCRIPTION,A.REDISUPDATESUCCESS,A.ISHOTGOODS,A.OWN_GOODS,A.ISDOUBLEGOODS,A.SELFBUILTSHOPID    "
                + " FROM "
                + " (SELECT * "
                + " FROM "
                + " (SELECT count(*) over() num,row_number() over (order by A.SELFBUILTSHOPID desc,A.PLUNO) rn,A.*  "
                + " FROM "
                + " (SELECT row_number() over (partition by A.PLUNO ORDER BY A.PLUNO)   GP,A.PLUNO,B.PLU_NAME AS PLUNAME,"
                + " A.SHORTCUT_CODE,A.BASEUNIT,A.PROD_UNIT,A.PURUNIT,A.PUNIT,A.SUNIT,A.CUNIT,A.WUNIT,A.BOM_UNIT,A.CATEGORY,"
                + " A.BRAND,A.SERIES,A.ISBATCH, A.SHELFLIFE , A.EID,A.STATUS,A.TAXCODE,A.STOCKINVALIDDAY,A.STOCKOUTVALIDDAY,"
                + " A.CHECKVALIDDAY,A.ISHOLIDAY,A.PRICE,A.WARMTYPE,A.PLUTYPE,A.VIRTUAL,A.OPENPRICE,A.ISWEIGHT,A.STOCKMANAGETYPE,"
                + " A.MEMO,A.SUPPRICE,A.REDISUPDATESUCCESS,A.ISHOTGOODS,A.OWN_GOODS,A.ISDOUBLEGOODS,A.SELFBUILTSHOPID "
                + " FROM DCP_GOODS A ");

        sqlbuf.append(" LEFT JOIN DCP_GOODS_LANG B ON A.EID=B.EID AND A.PLUNO=B.PLUNO  AND B.LANG_TYPE='" + langType + "'  ");
        sqlbuf.append(" LEFT JOIN DCP_GOODS_BARCODE B1 ON A.EID=B1.EID AND A.PLUNO=B1.PLUNO  ");

        if (categoryno != null && categoryno.length() > 0) {
            sqlbuf.append(" "
                    + " INNER JOIN "
                    + " ( "
                    + " select * from ( "
                    + " select K.EID,K.Category,K.categorylevel,"
                    + " case when K.Category=K.up_category then N'' else K.up_category end up_category  "
                    + " from DCP_category K "
                    + " where K.EID='" + eId + "' "      //小风说商品表CATEGORYTYPE默认 0-普通分类
                    + " and K.status='100' ) K  "
                    + " start with K.category='" + categoryno + "' "
                    + " connect by prior K.category=K.up_category "
                    + " order by K.categorylevel,K.category "
                    + " ) K  ON A.EID=K.EID AND A.CATEGORY=K.CATEGORY ");
        }

        sqlbuf.append(" where A.EID='" + eId + "' ");

        //searchScope by jinzma 20220310
        switch (searchScope) {
            case "0":    //0、全部
                break;
            case "1":    //1、总部和当前自建门店
                sqlbuf.append(" and (a.selfbuiltshopid is null or a.selfbuiltshopid='" + selfBuiltShopId + "')");
                break;
            case "2":    //2、仅总部
                sqlbuf.append(" and a.selfbuiltshopid is null");
                break;
            case "3":    //3、全部自建门店
                sqlbuf.append(" and a.selfbuiltshopid is not null");
                break;
            case "4":    //4、仅当前自建门店
                sqlbuf.append(" and a.selfbuiltshopid='" + selfBuiltShopId + "'");
                break;
        }

        if (status != null && status.length() > 0) {
            sqlbuf.append(" and A.status='" + status + "' ");
        }

        if (redisUpdateSuccess != null && redisUpdateSuccess.length() > 0) {
            sqlbuf.append(" and A.REDISUPDATESUCCESS='" + redisUpdateSuccess + "' ");
        }

        if (keyTxt != null && !keyTxt.isEmpty()) {
            String upperKeyTxt = keyTxt.toUpperCase();

            sqlbuf.append(" AND (upper(A.PLUNO) LIKE '%%" + upperKeyTxt + "%%' OR B.PLU_NAME LIKE '%%" + keyTxt + "%%' "
                    + " OR B1.PLUBARCODE LIKE '%%" + keyTxt + "%%' OR A.SHORTCUT_CODE LIKE '%%" + keyTxt + "%%' ) ");
        }

        if (brandno != null && !brandno.isEmpty()) {
            sqlbuf.append(" and A.BRAND='" + brandno + "' ");
        }

        if (seriesno != null && !seriesno.isEmpty()) {
            sqlbuf.append(" and A.SERIES='" + seriesno + "' ");
        }

        if (pluType != null && !pluType.isEmpty()) {
            sqlbuf.append(" and A.PLUTYPE='" + pluType + "' ");
        }

        if (virtual != null && !virtual.isEmpty()) {
            sqlbuf.append(" and A.VIRTUAL='" + virtual + "' ");
        }

        if (openPrice != null && !openPrice.isEmpty()) {
            sqlbuf.append(" and A.OPENPRICE='" + openPrice + "' ");
        }

        if (isWeight != null && !isWeight.isEmpty()) {
            sqlbuf.append(" and A.ISWEIGHT='" + isWeight + "' ");
        }

        if (isBatch != null && !isBatch.isEmpty()) {
            sqlbuf.append(" and A.ISBATCH='" + isBatch + "' ");
        }

        sqlbuf.append(" ) A ");
        sqlbuf.append(" WHERE GP=1 ");

        sqlbuf.append(" ) ");
        sqlbuf.append(" where rn>" + startRow + " and rn<=" + (startRow + pageSize));
        sqlbuf.append(" ) A "
                //+ " LEFT JOIN DCP_GOODS_LANG B ON A.EID=B.EID AND A.PLUNO=B.PLUNO AND B.LANG_TYPE='"+langtype+"' "
                + " LEFT JOIN DCP_GOODS_EXT DGE ON A.EID=DGE.EID AND A.PLUNO=DGE.PLUNO  "
                + " LEFT JOIN DCP_UNIT_LANG  C ON A.EID=C.EID AND A.PUNIT=C.UNIT AND C.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_UNIT_LANG  D ON A.EID=D.EID AND A.CUNIT=D.UNIT AND D.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_UNIT_LANG  E ON A.EID=E.EID AND A.SUNIT=E.UNIT AND E.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_UNIT_LANG  F ON A.EID=F.EID AND A.WUNIT=F.UNIT AND F.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_UNIT_LANG  G ON A.EID=G.EID AND A.BOM_UNIT=G.UNIT AND G.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_UNIT_LANG  BASE ON A.EID=BASE.EID AND A.BASEUNIT=BASE.UNIT  AND BASE.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_UNIT_LANG  PROD ON A.EID=PROD.EID AND A.PROD_UNIT=PROD.UNIT  AND PROD.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_UNIT_LANG  PUR ON A.EID=PUR.EID AND A.PURUNIT=PUR.UNIT  AND PUR.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_CATEGORY_LANG H ON A.EID=H.EID AND A.CATEGORY=H.CATEGORY  AND H.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_BRAND_LANG I ON A.EID=I.EID AND A.BRAND=I.BRANDNO  AND I.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_SERIES_LANG J ON A.EID=J.EID AND A.SERIES=J.SERIESNO AND J.LANG_TYPE='" + langType + "' "
                //+ " LEFT JOIN DCP_TAGGROUP_DETAIL N on A.EID=N.EID and N.status='100' and N.TagGroupType='3' and N.TagGroupNo||N.TAGNO=A.ProTagNo"   //3生产标签
                //+ " LEFT JOIN DCP_TAGGROUP_DETAIL O on A.EID=O.EID and O.status='100' and O.TagGroupType='4' and O.TagGroupNo||O.TAGNO=A.DelTagNo"   //4 物流标签
                + " LEFT JOIN DCP_TaxCategory_LANG M on A.EID=M.EID  and A.taxCode=M.taxCode  AND M.LANG_TYPE='" + langType + "' "
        );


        return sqlbuf.toString();

    }


}
