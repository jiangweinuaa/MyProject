package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_SalePriceTemplateGoodsQuery extends SPosBasicService<DCP_SalePriceTemplateGoodsQueryReq, DCP_SalePriceTemplateGoodsQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_SalePriceTemplateGoodsQueryReq req) throws Exception {
        boolean isFail = false;

        return false;
    }

    @Override
    protected TypeToken<DCP_SalePriceTemplateGoodsQueryReq> getRequestType() {
        return new TypeToken<DCP_SalePriceTemplateGoodsQueryReq>() {
        };
    }

    @Override
    protected DCP_SalePriceTemplateGoodsQueryRes getResponseType() {
        return new DCP_SalePriceTemplateGoodsQueryRes();
    }

    @Override
    protected DCP_SalePriceTemplateGoodsQueryRes processJson(DCP_SalePriceTemplateGoodsQueryReq req) throws Exception {
        DCP_SalePriceTemplateGoodsQueryRes res = this.getResponse();

        int totalRecords = 0;
        int totalPages = 0;
        try {
            String sql = getQuerySql(req);
            List<Map<String, Object>> getData = this.doQueryData(sql, null);
            res.setDatas(new ArrayList<>());
            if (getData != null && !getData.isEmpty()) {
                //算總頁數
                String num = getData.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> oneData : getData) {
                    DCP_SalePriceTemplateGoodsQueryRes.Datas lv1 = res.new Datas();
                    lv1.setTemplateId(oneData.get("TEMPLATEID").toString());
                    lv1.setBeginDate(oneData.get("BEGINDATE").toString());
                    lv1.setEndDate(oneData.get("ENDDATE").toString());
                    lv1.setFeatureNo(oneData.get("FEATURENO").toString());
                    lv1.setIsDiscount(oneData.get("ISDISCOUNT").toString());
                    lv1.setIsProm(oneData.get("ISPROM").toString());
                    lv1.setItem(oneData.get("ITEM").toString());
                    lv1.setMinPrice(oneData.get("MINPRICE").toString());
                    lv1.setPluName(oneData.get("PLU_NAME").toString());
                    lv1.setPluNo(oneData.get("PLUNO").toString());
                    lv1.setPrice(Double.parseDouble(oneData.get("PRICE").toString()));
                    lv1.setStandardPrice(Double.parseDouble(oneData.get("STANDARDPRICE").toString()));
                    lv1.setStatus(oneData.get("STATUS").toString());
                    lv1.setUnit(oneData.get("UNIT").toString());
                    lv1.setUnitName(oneData.get("UNAME").toString());
                    lv1.setRedisUpdateSuccess(oneData.get("REDISUPDATESUCCESS").toString());

                    lv1.setCreateTime(StringUtils.toString(oneData.get("CONFIRMTIME"), ""));
                    lv1.setLastModiTime(StringUtils.toString(oneData.get("LASTMODIOPID"), ""));
                    lv1.setConfirmTime(StringUtils.toString(oneData.get("CONFIRMTIME"), ""));
                    lv1.setCreateOpId(StringUtils.toString(oneData.get("CREATEOPID"), ""));
                    lv1.setCreateOpName(StringUtils.toString(oneData.get("CREATEOPNAME"), ""));
                    lv1.setLastModiOpId(StringUtils.toString(oneData.get("LASTMODIOPID"), ""));
                    lv1.setLastModiOpName(StringUtils.toString(oneData.get("LASTMODIOPNAME"), ""));
                    lv1.setEmployeeId(StringUtils.toString(oneData.get("EMPLOYEEID"), ""));
                    lv1.setEmployeeName(StringUtils.toString(oneData.get("EMPLOYEENAME"), ""));
                    lv1.setOfNo(StringUtils.toString(oneData.get("OFNO"), ""));
                    lv1.setOItem(StringUtils.toString(oneData.get("OITEM"), ""));
                    lv1.setDepartId(StringUtils.toString(oneData.get("DEPARTID"), ""));
                    lv1.setDepartName(StringUtils.toString(oneData.get("DEPARTNAME"), ""));

                    res.getDatas().add(lv1);

                }

            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_SalePriceTemplateGoodsQueryReq req) throws Exception {
        String eId = req.geteId();
        String langtype = req.getLangType();
        String ketTxt = req.getRequest().getKeyTxt();
        String templateId = req.getRequest().getTemplateId();
        String sortType = req.getRequest().getSortType();
        String category = req.getRequest().getCategory();
        String redisUpdateSuccess = req.getRequest().getRedisUpdateSuccess();

        //ID1024837】【潮品3.0】零售价模板04006底下商品没有显示，数据表DCP_SALEPRICETEMPLATE_PRICE中有
        String effectDate = req.getRequest().getEffectDate();
        String effectStatus = req.getRequest().getEffectStatus();
        if (Check.Null(effectStatus)) {
            effectStatus = "0";
        }

        //計算起啟位置
        int pageSize = req.getPageSize();
        int startRow = (req.getPageNumber() - 1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                + " select * from ( "
                + " select count(*) over() num, ");
        if (sortType != null && sortType.length() > 0) {
            if (sortType.equals("1")) {
                sqlbuf.append(" row_number() over(order by a.item desc) rn, ");
            } else {
                sqlbuf.append(" row_number() over(order by a.pluno asc) rn, ");
            }
        }
        sqlbuf.append(""
                + " a.*,b.plu_name,c.uname,d.category,d.price STANDARDPRICE "
                + " ,em1.name CREATEOPNAME,em2.name AS LASTMODIOPNAME "
                + " ,em3.name EMPLOYEENAME,dd0.DEPARTNAME AS DEPARTNAME "
                + " from DCP_SALEPRICETEMPLATE_PRICE a "
                + " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='" + langtype + "' "
                + " left join dcp_unit_lang c on a.eid=c.eid and a.unit=c.unit and c.lang_type='" + langtype + "' "
                + " left join dcp_goods d on a.eid=d.eid and a.pluno=d.pluno "
                + " LEFT JOIN DCP_employee em1 ON em1.eid = a.eid AND em1.employeeno = a.CREATEOPID"
                + " LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.LASTMODIOPID "
                + " LEFT JOIN DCP_employee em3 ON em3.eid = a.eid AND em3.employeeno = a.EMPLOYEEID "
                + " LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.DEPARTID AND dd0.lang_type='" + req.getLangType() + "'"
                + " where a.eid='" + eId + "' ");

        if (templateId != null && !templateId.isEmpty()) {
            sqlbuf.append(" and a.templateid='" + templateId + "' ");
        }
        if (redisUpdateSuccess != null && !redisUpdateSuccess.isEmpty()) {
            sqlbuf.append(" and a.REDISUPDATESUCCESS='" + redisUpdateSuccess + "' ");
        }
        if (ketTxt != null && !ketTxt.isEmpty()) {
            sqlbuf.append(" and (b.pluno like '%%" + ketTxt + "%%' or b.plu_name like '%%" + ketTxt + "%%') ");
        }
        if (category != null && !category.isEmpty()) {
            sqlbuf.append(" and d.category='" + category + "' ");
        }

        if (!effectStatus.equals("0")) {
			/*增加两个字段：effectDate：日期YYYYMMDD，effectStatus：0全部，1已生效，2未生效，3已失效
		      当effectStatus 值为0的时候，就不要关联effectDate值，直接查询模板下的全部商品
		      当effectStatus 值为1的时候，就需要查effectDate大于等于商品生效开始日期，且effectDate小于等于商品生效结束日期；
		      当effectStatus 值为2的时候，就需要查effectDate小于商品生效开始日期
		      当effectStatus 值为3的时候，就需要查effectDate大于商品生效结束日期；*/
            switch (effectStatus) {
                case "1":
                    sqlbuf.append(" and to_date('" + effectDate + "','yyyy-MM-dd')>=a.begindate "
                            + " and to_date('" + effectDate + "','yyyy-MM-dd')<=a.enddate ");
                    break;
                case "2":
                    sqlbuf.append(" and to_date('" + effectDate + "','yyyy-MM-dd')<a.begindate");
                    break;
                case "3":
                    sqlbuf.append(" and to_date('" + effectDate + "','yyyy-MM-dd')>a.enddate");
                    break;
            }
        }

        sqlbuf.append(" ) where rn>" + startRow + " and rn<=" + (startRow + pageSize));
        return sqlbuf.toString();
    }

}
