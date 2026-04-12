package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_DemandToWOQueryReq;
import com.dsc.spos.json.cust.res.DCP_DemandToWOQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_DemandToWOQuery extends SPosBasicService<DCP_DemandToWOQueryReq, DCP_DemandToWOQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_DemandToWOQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (req.getRequest() == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        } else {
            //分页查询的服务，必须给值，不能为0
            int pageNumber = req.getPageNumber();
            int pageSize = req.getPageSize();
            if (pageNumber == 0) {
                isFail = true;
                errMsg.append("分页查询pageNumber不能为0,");
            }
            if (pageSize == 0) {
                isFail = true;
                errMsg.append("分页查询pageSize不能为0,");
            }

        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_DemandToWOQueryReq> getRequestType() {
        return new TypeToken<DCP_DemandToWOQueryReq>() {
        };
    }

    @Override
    protected DCP_DemandToWOQueryRes getResponseType() {
        return new DCP_DemandToWOQueryRes();
    }

    @Override
    protected DCP_DemandToWOQueryRes processJson(DCP_DemandToWOQueryReq req) throws Exception {
        DCP_DemandToWOQueryRes res = this.getResponse();
        int totalRecords;        //总笔数
        int totalPages;
        String querSqlAll = this.getQuerSqlAll(req);
        List<Map<String, Object>> getQDataAll = this.doQueryData(querSqlAll, null);
        List<String> pluNos = getQDataAll.stream().map(x -> x.get("PLUNO").toString()).distinct().collect(Collectors.toList());


        if (pluNos.size() <= 0) {
            res.setPageNumber(0);
            res.setPageSize(0);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            return res;
        }
        //查询
        String sql = this.getQuerSqlCount(req, pluNos);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        String prodTemplateSql = getProdTemplateSql(req, pluNos);
        List<Map<String, Object>> getQDataTemplate = this.doQueryData(prodTemplateSql, null);

        String bomSql = getBomSql(req, pluNos);
        List<Map<String, Object>> getQDataBom = this.doQueryData(bomSql, null);
        res.setDatas(new ArrayList<>());
        if (CollUtil.isNotEmpty(getQData)) {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setDatas(new ArrayList<>());
            for (Map<String, Object> map : getQData) {
                DCP_DemandToWOQueryRes.Level1Elm level1Elm = res.new Level1Elm();

                level1Elm.setPluNo(map.get("PLUNO").toString());
                level1Elm.setPluName(map.get("PLUNAME").toString());
                level1Elm.setFeatureNo(map.get("FEATURENO").toString());
                level1Elm.setFeatureName(map.get("FEATURENAME").toString());
                level1Elm.setRDate(map.get("RDATE").toString());
                level1Elm.setProdUnit(map.get("PRODUNIT").toString());
                level1Elm.setProdUnitName(map.get("PRODUNITNAME").toString());
                level1Elm.setBaseUnit(map.get("BASEUNIT").toString());
                level1Elm.setUnitRatio(map.get("UNITRATIO").toString());
                level1Elm.setPGroupNo(map.get("PGROUPNO").toString());
                level1Elm.setDetail(new ArrayList<>());
                BigDecimal prodQtySum = new BigDecimal(0);
                String prodUdLength = "0";
                //明细
                List<Map<String, Object>> detailColl = getQDataAll.stream().filter(x -> x.get("PLUNO").toString().equals(level1Elm.getPluNo()) &&
                        x.get("FEATURENO").toString().equals(level1Elm.getFeatureNo()) &&
                        x.get("RDATE").toString().equals(level1Elm.getRDate())).collect(Collectors.toList());
                for (Map<String, Object> detailMap : detailColl) {
                    DCP_DemandToWOQueryRes.Detail detail = res.new Detail();
                    detail.setOrderNo(detailMap.get("ORDERNO").toString());
                    detail.setOrderItem(detailMap.get("ITEM").toString());
                    detail.setOrderType(detailMap.get("ORDERTYPE").toString());
                    detail.setObjectType(detailMap.get("OBJECTTYPE").toString());
                    detail.setObjectId(detailMap.get("OBJECTID").toString());
                    detail.setPUnit(detailMap.get("PUNIT").toString());
                    detail.setPQty(detailMap.get("PQTY").toString());
                    detail.setProdUnit(detailMap.get("PRODUNIT").toString());

                    String baseQty = detailMap.get("BASEQTY").toString();
                    prodUdLength = detailMap.get("PRODUDLENGTH").toString();
                    String prodUnitRatio = detailMap.get("UNITRATIO").toString();
                    BigDecimal prodQty = new BigDecimal(baseQty).divide(new BigDecimal(prodUnitRatio), Integer.parseInt(prodUdLength), BigDecimal.ROUND_HALF_UP);

                    detail.setProdQty(prodQty.toString());
                    detail.setPTemplateNo(detailMap.get("TEMPLATENO").toString());
                    detail.setPTemplateName(detailMap.get("PTEMPLATENAME").toString());
                    level1Elm.getDetail().add(detail);
                    prodQtySum = prodQtySum.add(prodQty);

                }

                level1Elm.setProdQty(prodQtySum.toString());

                //基础单位的
                BigDecimal stockQty = Check.Null(map.get("STOCKQTY").toString()) ? BigDecimal.ZERO : new BigDecimal(map.get("STOCKQTY").toString());
                BigDecimal prodStockQty = stockQty.divide(new BigDecimal(level1Elm.getUnitRatio()), Integer.parseInt(prodUdLength), BigDecimal.ROUND_HALF_UP);
                level1Elm.setStockQty(prodStockQty.toString());
                BigDecimal subtract = prodQtySum.subtract(prodStockQty);
                if (subtract.compareTo(BigDecimal.ZERO) <= 0) {
                    level1Elm.setShortQty("0");
                } else {
                    if (subtract.compareTo(BigDecimal.ZERO) <= 0) {
                        level1Elm.setShortQty("0");
                    } else {
                        level1Elm.setShortQty(subtract.toString());
                    }
                }

                boolean prodData = false;
                //生管模板数据
                List<Map<String, Object>> prodTList = getQDataTemplate.stream().filter(x -> x.get("PLUNO").toString().equals(level1Elm.getPluNo())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(prodTList)) {
                    level1Elm.setMinQty(prodTList.get(0).get("MINQTY").toString());
                    level1Elm.setMulQty(prodTList.get(0).get("MULQTY").toString());
                    level1Elm.setRemainType(prodTList.get(0).get("REMAINTYPE").toString());
                    level1Elm.setPreDays(prodTList.get(0).get("PREDAYS").toString());
                    level1Elm.setOddValue(prodTList.get(0).get("ODDVALUE").toString());
                    prodData = true;
                }

                //bom
                List<Map<String, Object>> bomCollection1 = getQDataBom.stream().filter(x -> x.get("PLUNO").toString().equals(level1Elm.getPluNo()) &&
                        x.get("RESTRICTSHOP").toString().equals("1")).collect(Collectors.toList());

                Map<String, Object> bomData = null;
                if (!bomCollection1.isEmpty()) {
                    bomData = bomCollection1.get(0);
                } else {
                    bomCollection1 = getQDataBom.stream().filter(x -> x.get("PLUNO").toString().equals(level1Elm.getPluNo()) &&
                            x.get("RESTRICTSHOP").toString().equals("0")).collect(Collectors.toList());
                    if (!bomCollection1.isEmpty()) {
                        bomData = bomCollection1.get(0);

                    }
                }

                if (null == bomData) {
                    level1Elm.setBomNo("");
                    level1Elm.setVersionNum("");
                } else {
                    level1Elm.setBomNo(bomData.get("BOMNO").toString());
                    level1Elm.setVersionNum(bomData.get("VERSIONNUM").toString());
                    if (!prodData) {
                        level1Elm.setMinQty(bomData.get("MINQTY").toString());
                        level1Elm.setMulQty(bomData.get("MULQTY").toString());
                        level1Elm.setRemainType(bomData.get("REMAINTYPE").toString());
                        level1Elm.setPreDays(bomData.get("FIXPREDAYS").toString());
                        level1Elm.setOddValue(bomData.get("ODDVALUE").toString());
                    }

                }

                res.getDatas().add(level1Elm);
            }


        } else {
            totalRecords = 0;
            totalPages = 0;
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_DemandToWOQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        DCP_DemandToWOQueryReq.LevelRequest request = req.getRequest();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String shopId = req.getShopId();

        //在途量 可用库存量  后面集合处理
        String langType = req.getLangType();
        sqlbuf.append(" select * from (");

        sqlbuf.append(" select distinct a.pluno,c.plu_name as pluname,a.featureno,d.featurename,a.rdate,b.PROD_UNIT as PRODUNIT,u1.uname as prodUnitName,b.baseunit,e.UNITRATIO,f.pgroupno,sum(PQTY) pqty " +
                " from dcp_demand a" +
                " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='" + langType + "' " +
                " left join dcp_goods_feature_lang d on d.eid=a.eid and d.pluno=a.pluno and d.featureno=a.featureno and d.lang_type='" + langType + "'" +
                " left join dcp_unit_lang u1 on u1.uname=b.PROD_UNIT and u1.lang_type='" + langType + "'" +
                " left join DCP_GOODS_UNIT e on e.eid=a.eid and e.ounit=b.baseunit and e.unit=b.PROD_UNIT and e.pluno=a.pluno " +
                " left join MES_PRODUCT_GROUP_GOODS f on f.eid=a.eid and f.pluno=a.pluno " +
                " left join DCP_PRODSCHEDULE_SOURCE g on g.orderno=a.orderno and g.orderitem=a.item and a.eid=g.eid and a.organizationno=g.organizationno " +
                " left join DCP_PRODSCHEDULE h on h.eid=g.eid and h.billno=g.billno and a.eid=h.eid and a.organizationno=h.organizationno and h.status!='5'" +

                "  ");

        sqlbuf.append(" WHERE a.eid='").append(eId).append("'  and b.SOURCETYPE='1' ");
        sqlbuf.append(" AND a.DELIVERYORGNO='").append(organizationNO).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
            sqlbuf.append(" AND a.rdate>='").append(req.getRequest().getBeginDate()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            sqlbuf.append(" AND a.rdate<='").append(req.getRequest().getEndDate()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getOrderType())) {
            sqlbuf.append(" and a.ordertype='").append(req.getRequest().getOrderType()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getProdDepartId())) {
            sqlbuf.append(" and f.PGROUPNO='").append(req.getRequest().getProdDepartId()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getOrderList())) {
            sqlbuf.append(" AND (1=2 ");
            for (DCP_DemandToWOQueryReq.OrderList orderList : req.getRequest().getOrderList()) {
                sqlbuf.append(" OR a.ORDERNO='").append(orderList.getOrderNo()).append("' ");
            }
            sqlbuf.append(")");
        }

        sqlbuf.append(" and h.billno is null  ");


        if (Check.NotNull(req.getRequest().getKeyTxt())) {
            sqlbuf.append(" and (a.pluno like '%" + req.getRequest().getKeyTxt() + "%' " +
                    "or c.plu_name like '%" + req.getRequest().getKeyTxt() + "%' " +
                    "or d.featurename like '%" + req.getRequest().getKeyTxt() + "%'" +
                    "or a.orderno like '%" + req.getRequest().getKeyTxt() + "%' ) ");
        }

        sqlbuf.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY purTemplateNo ");


        return sqlbuf.toString();

    }

    protected String getQuerSqlAll(DCP_DemandToWOQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String langType = req.getLangType();

        sqlbuf.append(" select  distinct a.pluno,c.plu_name as pluname,a.featureno,d.featurename,a.rdate,b.PROD_UNIT as PRODUNIT,u1.uname as prodUnitName,b.baseunit,e.UNITRATIO,f.pgroupno,a.pqty,a.baseqty,a.ORDERTYPE,a.orderno,a.item,a.OBJECTTYPE," +
                " a.OBJECTid,a.PUNIT,a.pqty,a.TEMPLATENO,i.PTEMPLATE_NAME as ptemplatename,u2.UDLENGTH as produdlength " +
                " from dcp_demand a " +
                " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='" + langType + "' " +
                " left join dcp_goods_feature_lang d on d.eid=a.eid and d.pluno=a.pluno and d.featureno=a.featureno and d.lang_type='" + langType + "'" +
                " left join dcp_unit_lang u1 on a.eid=u1.eid and  u1.unit=b.PROD_UNIT and u1.lang_type='" + langType + "'" +
                " left join dcp_unit u2 on a.eid=u2.eid and  u2.unit=b.PROD_UNIT " +
                " left join DCP_GOODS_UNIT e on e.eid=a.eid and e.ounit=b.PROD_UNIT and e.unit=b.baseunit and e.pluno=a.pluno " +
                " left join MES_PRODUCT_GROUP_goods f on f.eid=a.eid and f.pluno=a.pluno " +
                " left join DCP_PRODSCHEDULE_SOURCE g on g.orderno=a.orderno and g.orderitem=a.item and a.eid=g.eid and a.organizationno=g.organizationno " +
                " left join DCP_PRODSCHEDULE h on h.eid=g.eid and h.billno=g.billno and a.eid=h.eid and a.organizationno=h.organizationno and h.status!='5' " +
                " left join DCP_PTEMPLATE i on i.eid=a.eid and i.ptemplateno=a.templateno " +

                "  ");

        sqlbuf.append(" WHERE a.eid='").append(eId).append("'  and b.SOURCETYPE='1' ");
        sqlbuf.append(" AND a.DELIVERYORGNO='").append(organizationNO).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
            sqlbuf.append(" AND a.rdate>='").append(req.getRequest().getBeginDate()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            sqlbuf.append(" AND a.rdate<='").append(req.getRequest().getEndDate()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getOrderType())) {
            sqlbuf.append(" and a.ordertype='").append(req.getRequest().getOrderType()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getProdDepartId())) {
            sqlbuf.append(" and f.PGROUPNO='").append(req.getRequest().getProdDepartId()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getOrderList())) {
            sqlbuf.append(" AND (1=2 ");
            for (DCP_DemandToWOQueryReq.OrderList orderList : req.getRequest().getOrderList()) {
                sqlbuf.append(" OR a.ORDERNO='").append(orderList.getOrderNo()).append("' ");
            }
            sqlbuf.append(")");
        }

        sqlbuf.append(" and h.billno is null  ");
        if (Check.NotNull(req.getRequest().getKeyTxt())) {
            sqlbuf.append(" and (a.pluno like '%" + req.getRequest().getKeyTxt() + "%' " +
                    "or c.plu_name like '%" + req.getRequest().getKeyTxt() + "%' " +
                    "or d.featurename like '%" + req.getRequest().getKeyTxt() + "%'" +
                    "or a.orderno like '%" + req.getRequest().getKeyTxt() + "%' ) ");
        }

        //不加库存条件

        return sqlbuf.toString();
    }

    protected String getQuerSqlCount(DCP_DemandToWOQueryReq req, List<String> pluNos) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();

        MyCommon cm = new MyCommon();
        StringBuffer sJoinPluNo = new StringBuffer("");
        for (String pluNo : pluNos) {
            sJoinPluNo.append(pluNo + ",");
        }
        Map<String, String> mapPluNo = new HashMap<String, String>();
        mapPluNo.put("PLUNO", sJoinPluNo.toString());

        String withasSql_pluno = "";
        withasSql_pluno = cm.getFormatSourceMultiColWith(mapPluNo);
        mapPluNo = null;


        //在途量 可用库存量  后面集合处理
        String langType = req.getLangType();
        sqlbuf.append(" with p as (" + withasSql_pluno + ") " +
                " select * from ( " +
                " select count(*) over () num,rownum rn,a.* from (");
        sqlbuf.append(" select a.*,p1.stockqty from (");
        sqlbuf.append(" select  a.pluno,c.plu_name as pluname,a.featureno,d.featurename,a.rdate,b.PROD_UNIT as PRODUNIT,u1.uname as prodUnitName,b.baseunit,e.UNITRATIO,f.pgroupno,sum(a.baseqty) pqty " +
                " from dcp_demand a" +
                " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='" + langType + "' " +
                " left join dcp_goods_feature_lang d on d.eid=a.eid and d.pluno=a.pluno and d.featureno=a.featureno and d.lang_type='" + langType + "'" +
                " left join dcp_unit_lang u1 on u1.eid=a.eid and  u1.unit=b.PROD_UNIT and u1.lang_type='" + langType + "'" +
                " left join DCP_GOODS_UNIT e on e.eid=a.eid and e.ounit=b.PROD_UNIT and e.unit=b.baseunit and e.pluno=a.pluno " +
                " left join MES_PRODUCT_GROUP_goods f on f.eid=a.eid and f.pluno=a.pluno " +
                " left join DCP_PRODSCHEDULE_SOURCE g on g.orderno=a.orderno and g.orderitem=a.item and a.eid=g.eid and a.organizationno=g.organizationno " +
                " left join DCP_PRODSCHEDULE h on h.eid=g.eid and h.billno=g.billno and a.eid=h.eid and a.organizationno=h.organizationno and h.status!='5'" +
                "  ");
        sqlbuf.append(" WHERE a.eid='").append(eId).append("'  and b.SOURCETYPE='1' ");
        sqlbuf.append(" AND a.DELIVERYORGNO='").append(organizationNO).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
            sqlbuf.append(" AND a.rdate>='").append(req.getRequest().getBeginDate()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            sqlbuf.append(" AND a.rdate<='").append(req.getRequest().getEndDate()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getOrderType())) {
            sqlbuf.append(" and a.ordertype='").append(req.getRequest().getOrderType()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getProdDepartId())) {
            sqlbuf.append(" and f.PGROUPNO='").append(req.getRequest().getProdDepartId()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getOrderList())) {
            sqlbuf.append(" AND (1=2 ");
            for (DCP_DemandToWOQueryReq.OrderList orderList : req.getRequest().getOrderList()) {
                sqlbuf.append(" OR a.ORDERNO='").append(orderList.getOrderNo()).append("' ");
            }
            sqlbuf.append(")");
        }

        sqlbuf.append(" and h.billno is null  ");

        if (Check.NotNull(req.getRequest().getKeyTxt())) {
            sqlbuf.append(" and (a.pluno like '%" + req.getRequest().getKeyTxt() + "%' " +
                    "or c.plu_name like '%" + req.getRequest().getKeyTxt() + "%' " +
                    "or d.featurename like '%" + req.getRequest().getKeyTxt() + "%'" +
                    "or a.orderno like '%" + req.getRequest().getKeyTxt() + "%' ) ");
        }
        sqlbuf.append(" group by a.pluno,c.plu_name ,a.featureno,d.featurename,a.rdate,b.PROD_UNIT,u1.uname ,b.baseunit,e.UNITRATIO,f.pgroupno  ");
        sqlbuf.append(" ) a " +
                " left join ( select a.pluno,a.featureno,sum(a.qty-a.lockqty-a.ONLINEQTY ) as stockQty " +
                " from dcp_stock a inner join p on p.pluno=a.pluno " +
                "  left join dcp_warehouse b on a.eid=b.eid and a.warehouse=b.warehouse " +
                " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' " +
                " and b.WAREHOUSE_TYPE in ('3','5')" +
                " group by a.pluno,a.featureno" +
                " ) p1 on p1.pluno=a.pluno and p1.featureno=a.featureno and p1.stockQty<a.pqty " +
                " ");

        sqlbuf.append("  )  a  ) "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + "" +
                //  + " ORDER BY purTemplateNo " +
                "");


        return sqlbuf.toString();
    }

    private String getProdTemplateSql(DCP_DemandToWOQueryReq req, List<String> pluNos) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        MyCommon cm = new MyCommon();
        StringBuffer sJoinPluNo = new StringBuffer("");
        for (String pluNo : pluNos) {
            sJoinPluNo.append(pluNo + ",");
        }
        Map<String, String> mapPluNo = new HashMap<String, String>();
        mapPluNo.put("PLUNO", sJoinPluNo.toString());

        String withasSql_pluno = "";
        withasSql_pluno = cm.getFormatSourceMultiColWith(mapPluNo);
        mapPluNo = null;

        sqlbuf.append(" with p as ( " + withasSql_pluno + " ) " +
                " select a.pluno,a.PRODMINQTY as minqty,a.PRODMULQTY mulqty,a.FIXPREDAYS as predays,a.REMAINTYPE,a.ODDVALUE " +
                " from DCP_PRODTEMPLATE_GOODS a " +
                " inner join p on p.pluno=a.pluno " +
                " inner join DCP_PRODTEMPLATE b on a.eid=b.eid and  a.TEMPLATEID=b.TEMPLATEID " +
                " left join DCP_PRODTEMPLATE_RANGE c on a.eid=c.eid and a.TEMPLATEID=c.TEMPLATEID " +
                " where a.eid='" + req.geteId() + "' " +
                " and a.status='100' and (b.RESTRICTORG='0' or (c.ORGANIZATIONNO='" + req.getOrganizationNO() + "' and c.status='100')) ");

        return sqlbuf.toString();
    }

    private String getBomSql(DCP_DemandToWOQueryReq req, List<String> pluNos) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        MyCommon cm = new MyCommon();
        StringBuffer sJoinPluNo = new StringBuffer("");
        for (String pluNo : pluNos) {
            sJoinPluNo.append(pluNo + ",");
        }
        Map<String, String> mapPluNo = new HashMap<String, String>();
        mapPluNo.put("PLUNO", sJoinPluNo.toString());

        String withasSql_pluno = "";
        withasSql_pluno = cm.getFormatSourceMultiColWith(mapPluNo);
        mapPluNo = null;

        sqlbuf.append(" with p as (" + withasSql_pluno + ") " +
                " select a.pluno,a.bomno,a.RESTRICTSHOP ,a.VERSIONNUM VERSIONNUM " +
                " ,a.MINQTY,a.ODDVALUE,a.MULQTY,a.REMAINTYPE,a.FIXPREDAYS " +
                " from dcp_bom a " +
                " inner join p on p.pluno=a.pluno " +
                " left join DCP_BOM_RANGE b on a.eid=b.eid  and a.bomno=b.bomno " +
                " where a.eid='" + req.geteId() + "' " +
                " and a.status='100' and (a.RESTRICTSHOP='0' or (b.shopid='" + req.getOrganizationNO() + "' )) " +
                " and a.bomtype='0'  and trunc(a.effdate)<=trunc(sysdate) " +
                " order by a.effdate desc");

        return sqlbuf.toString();
    }
}
