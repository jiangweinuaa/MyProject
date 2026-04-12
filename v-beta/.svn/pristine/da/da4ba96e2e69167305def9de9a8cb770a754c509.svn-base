package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TaxGroupUpdateReq;
import com.dsc.spos.json.cust.res.DCP_TaxGroupUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_TaxGroupUpdate extends SPosAdvanceService<DCP_TaxGroupUpdateReq, DCP_TaxGroupUpdateRes> {

    @Override
    protected void processDUID(DCP_TaxGroupUpdateReq req, DCP_TaxGroupUpdateRes res) throws Exception {
        try {

            if (!verifyGoodsList(req)) {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("商品校验不通过!");
                return;
            }

            ColumnDataValue dcp_taxGroup = new ColumnDataValue();
            ColumnDataValue condition = new ColumnDataValue();

            condition.add("EID",DataValues.newString(req.geteId()));
            condition.add("TAXGROUPNO", DataValues.newString(req.getRequest().getTaxGroupNo()));

            dcp_taxGroup.add("TAXGROUPNAME", DataValues.newString(req.getRequest().getTaxGroupName()));
            dcp_taxGroup.add("TAXCODE", DataValues.newString(req.getRequest().getTaxCode()));
            dcp_taxGroup.add("MEMO", DataValues.newString(req.getRequest().getMemo()));
            dcp_taxGroup.add("STATUS", DataValues.newString(req.getRequest().getStatus()));

            dcp_taxGroup.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_taxGroup.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_TAXGROUP", condition,dcp_taxGroup)));

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_TAXGROUP_DETAIL",condition)));
            for (DCP_TaxGroupUpdateReq.GoodsList goods : req.getRequest().getGoodsList()) {
                ColumnDataValue dcp_taxGroup_detail = new ColumnDataValue();

                dcp_taxGroup_detail.add("EID", DataValues.newString(req.geteId()));
                dcp_taxGroup_detail.add("TAXGROUPNO", DataValues.newString(req.getRequest().getTaxGroupNo()));
                dcp_taxGroup_detail.add("ATTRTYPE", DataValues.newString(goods.getAttrType()));
                dcp_taxGroup_detail.add("ATTRID", DataValues.newString(goods.getAttrId()));
                dcp_taxGroup_detail.add("STATUS", DataValues.newString(goods.getStatus()));

                dcp_taxGroup_detail.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_taxGroup_detail.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dcp_taxGroup_detail.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                dcp_taxGroup_detail.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_taxGroup_detail.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_TAXGROUP_DETAIL", dcp_taxGroup_detail)));
            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());

        }
    }

    /**
     * 商品校验:
     * ● 品类/商品状态码需有效！
     * ○ 品类：DCP_CATEGORY
     * ○ 商品：DCP_GOODS
     * ●  重复性检查
     * ○ 同分类检查：同一类型（商品或品类）不可重复添加
     * ○ 跨分类检查：同一种类型（商品或品类）且状态有效，不可对应多个税种！
     */
    private boolean verifyGoodsList(DCP_TaxGroupUpdateReq req) throws Exception {
        StringBuilder errorMsg = new StringBuilder();
        List<String> categoryList = Lists.newArrayList();
        List<String> goodsList = Lists.newArrayList();

        for (DCP_TaxGroupUpdateReq.GoodsList goods : req.getRequest().getGoodsList()) {
//            枚举: 1-品类 2-商品
            if ("1".equals(goods.getAttrType())) {
                categoryList.add(goods.getAttrId());
            } else if ("2".equals(goods.getAttrType())) {
                goodsList.add(goods.getAttrId());
            }
        }
        StringBuilder querySql = new StringBuilder();

        if (!categoryList.isEmpty()) {
            querySql.append(" SELECT CATEGORY,STATUS FROM DCP_CATEGORY WHERE EID='").append(req.geteId()).append("'").append(" AND STATUS<>100 ").append(" AND CATEGORY IN(");
            for (String category : categoryList) {
                querySql.append("'").append(category).append("',");
            }
            querySql.deleteCharAt(querySql.length() - 1);
            querySql.append(")");

            List<Map<String, Object>> check = this.doQueryData(querySql.toString(), null);
            if (check != null && !check.isEmpty()) {
                for (Map<String, Object> map : check) {
                    errorMsg.append("分类:").append(map.get("TAXCODE")).append("未生效！");
                }
            }
        }
        //检查商品或分类是否有效
        if (!goodsList.isEmpty()) {
            querySql = new StringBuilder();
            querySql.append(" SELECT PLUNO,STATUS FROM DCP_GOODS WHERE EID='").append(req.geteId()).append("'").append(" AND STATUS<>100 ").append(" AND PLUNO in(");
            for (String category : goodsList) {
                querySql.append("'").append(category).append("',");
            }
            querySql.deleteCharAt(querySql.length() - 1);
            querySql.append(")");
            List<Map<String, Object>> check = this.doQueryData(querySql.toString(), null);
            if (check != null && !check.isEmpty()) {
                for (Map<String, Object> map : check) {
                    errorMsg.append("商品:").append(map.get("PLUNO")).append("未生效！");
                }
            }

        }

        //检查商品或分类是否重复
        List<String> collect = categoryList
                .stream()
                .filter(i -> !"".equals(i))
                .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            errorMsg.append("品类重复:").append(collect).append("\n");
        }

        collect = goodsList
                .stream()
                .filter(i -> !"".equals(i))
                .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            errorMsg.append("商品重复:").append(collect).append("\n");
        }

        querySql = new StringBuilder();
        querySql.append("  SELECT a.TAXGROUPNO,a.TAXCODE,b.ATTRTYPE,b.ATTRID,b.STATUS " +
                        "  FROM DCP_TAXGROUP a " +
                        "  LEFT JOIN DCP_TAXGROUP_DETAIL b ON a.EID=b.EID AND a.TAXGROUPNO=b.TAXGROUPNO")
                .append(" WHERE a.EID='").append(req.geteId()).append("'")
                .append( " AND a.TAXCODE='").append(req.getRequest().getTaxCode()).append("'")
                .append( " AND a.TAXGROUPNO<>'").append(req.getRequest().getTaxGroupNo()).append("'");
        //检查当前品类是否有多个税种
        if (!categoryList.isEmpty()) {
            querySql.append(" AND (ATTRTYPE='1' AND ATTRID IN(");
            for (String category : categoryList) {
                querySql.append("'").append(category).append("',");
            }
            querySql.deleteCharAt(querySql.length() - 1);
            querySql.append(")").append(")");
        }

        //检查当前商品是否有多个税种
        if (!goodsList.isEmpty()) {
            querySql.append(" AND (ATTRTYPE='2' AND ATTRID IN(");
            for (String category : goodsList) {
                querySql.append("'").append(category).append("',");
            }
            querySql.deleteCharAt(querySql.length() - 1);
            querySql.append(")").append(")");
        }
        List<Map<String, Object>> check = this.doQueryData(querySql.toString(), null);
        if (check != null && !check.isEmpty()) {
            for (Map<String, Object> map : check) {
                if ("1".equals(map.get("ATTRTYPE"))) {
                    errorMsg.append("分类").append(map.get("ATTRID")).append("已有对应分组").append(map.get("TAXGROUPNO"));
                } else {
                    errorMsg.append("商品").append(map.get("ATTRID")).append("已有对应分组").append(map.get("TAXGROUPNO"));
                }

            }
        }

        if (StringUtils.isNotEmpty(errorMsg)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errorMsg.toString());
        }
        return true;
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TaxGroupUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TaxGroupUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TaxGroupUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TaxGroupUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_TaxGroupUpdateReq> getRequestType() {
        return new TypeToken<DCP_TaxGroupUpdateReq>(){

        };
    }

    @Override
    protected DCP_TaxGroupUpdateRes getResponseType() {
        return new DCP_TaxGroupUpdateRes();
    }
}
