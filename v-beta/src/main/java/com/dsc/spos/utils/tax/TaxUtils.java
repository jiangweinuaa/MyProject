package com.dsc.spos.utils.tax;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.StringUtils;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class TaxUtils {

    @Getter
    public enum TaxPayerType {

        TYPE_GENERAL("1"),  //一般纳税人
        TYPE_SMALL("2");  //小规模纳税人

        TaxPayerType(String value) {
            this.type = value;
        }

        final String type;
    }


    public Tax getTaxFromTemplate(String eid, String organization, String supplier, String pTemplate, String pluNo) throws Exception {
        String taxCode = "";
        DsmDAO dao = StaticInfo.dao;

        if (StringUtils.isNotEmpty(pTemplate)) {
            String purTempSql = "select b.TAXCODE from DCP_PURCHASETEMPLATE a" +
                    " left join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                    " left join DCP_PURCHASETEMPLATE_ORG c on c.eid=a.eid and c.PURTEMPLATENO=a.PURTEMPLATENO " +
                    " where a.EID='" + eid + "' and c.organizationno='" + organization + "' and a.SUPPLIERNO='" + supplier + "' and a.PURTEMPLATENO='" + pTemplate + "' and a.status='100' and b.pluno='" + pluNo + "'";
            List<Map<String, Object>> getPurTempData = dao.executeQuerySQL(purTempSql, null);

            if (null != getPurTempData && !getPurTempData.isEmpty()) {
                Map<String, Object> oneData = getPurTempData.get(0);
                taxCode = oneData.get("TAXCODE").toString();
                if (StringUtils.isEmpty(taxCode)) {
                    String getGoodsSql = " SELECT a.INPUTTAXCODE FROM DCP_GOODS a " +
                            " WHERE EID='" + eid + "'and PLUNO='" + pluNo + "'";
                    List<Map<String, Object>> getGoodsInfo = dao.executeQuerySQL(getGoodsSql, null);

                    if (null != getGoodsInfo && !getGoodsInfo.isEmpty()) {
                        oneData = getGoodsInfo.get(0);
                        taxCode = oneData.get("INPUTTAXCODE").toString();
                    }
                }
            }
        }

        return getTax(eid, taxCode);

    }

    public Tax getTax(String eid, String organization, String pluNo) throws Exception {

//        根据【供货法人】纳税人性质TAXPAYER_TYPE判断赋值：
//        1.一般纳税人：按以下优先序
//        1️⃣取商品默认销项税：DCP_GOODS.TAXCODE；
//        2️⃣取商品分类默认税别：按商品分类找对应税别DCP_TAXGROUP.TAXCODE
//        3️⃣取法人销项税别：DCP_ORG.OUTPUTTAX;
//        2.小规模纳税人：取法人销项税别，取值DCP_ORG.OUTPUTTAX;

        DsmDAO dao = StaticInfo.dao;
        String taxpayer_type = "";
        String taxCode = "";
        String querySql = " SELECT TAXPAYER_TYPE,OUTPUTTAX FROM DCP_ORG WHERE EID='" + eid + "' AND ORGANIZATIONNO='" + organization + "'";
        List<Map<String, Object>> getData = dao.executeQuerySQL(querySql, null);
        if (CollectionUtils.isNotEmpty(getData)) {
            taxpayer_type = getData.get(0).get("TAXPAYER_TYPE").toString();
        }
        if (StringUtils.isEmpty(taxpayer_type)) {
            taxpayer_type = "1";
        }

        if ("2".equals(taxpayer_type)) {
            taxCode = getData.get(0).get("OUTPUTTAX").toString();
        } else {
            querySql = " SELECT TAXCODE,CATEGORY FROM DCP_GOODS WHERE eid='" + eid + "' AND PLUNO='" + pluNo + "'";
            getData = dao.executeQuerySQL(querySql, null);
            if (CollectionUtils.isNotEmpty(getData)) {
                taxCode = getData.get(0).get("TAXCODE").toString();
                if (StringUtils.isEmpty(taxCode)) {
                    String category = getData.get(0).get("CATEGORY").toString();
                    querySql = " SELECT TAXCODE FROM DCP_TAXGROUP a " +
                            " INNER JOIN DCP_TAXGROUP_DETAIL b on a.eid=b.eid and a.TAXGROUPNO=b.TAXGROUPNO and b.ATTRTYPE='2'  " +
                            " WHERE a.EID='" + eid + "' AND b.ATTRID='" + category + "'";
                    getData = dao.executeQuerySQL(querySql, null);
                    if (CollectionUtils.isNotEmpty(getData)) {
                        taxCode = getData.get(0).get("TAXCODE").toString();
                    }
                }

            }

        }

        return getTax(eid, taxCode);
    }

    public Tax getTax(String eid, String taxCode) throws Exception {
        Tax tax = new Tax();
        if (StringUtils.isNotEmpty(taxCode) && StringUtils.isNotEmpty(eid)) {
            DsmDAO dao = StaticInfo.dao;

            String querySql = "  SELECT TAXCODE,TAXRATE,INCLTAX,TAXCALTYPE FROM DCP_TAXCATEGORY WHERE eid='" + eid + "' AND TAXCODE='" + taxCode + "'";
            List<Map<String, Object>> getData = dao.executeQuerySQL(querySql, null);
            if (CollectionUtils.isNotEmpty(getData)) {
                tax = new Tax();
                tax.setTaxCode(getData.get(0).get("TAXCODE").toString());
                tax.setTaxRate(Double.parseDouble(getData.get(0).get("TAXRATE").toString()));
                tax.setTaxCalType(getData.get(0).get("TAXCALTYPE").toString());
                tax.setInclTax(getData.get(0).get("INCLTAX").toString());
            }
        }

        return tax;
    }


    /**
     * @param eid
     * @param organization
     * @param supplier
     * @param pTemplate
     * @param pluNo
     * @param taxType      进项，销项  J X
     * @return
     * @throws Exception
     */
    public Tax getTaxWithType(String eid, String organization, String supplier, String pTemplate, String pluNo, String taxType) throws Exception {
        String taxCode = "";
        DsmDAO dao = StaticInfo.dao;

        if ("J".equals(taxType)) {
            if (StringUtils.isNotEmpty(pTemplate)) {
                String purTempSql = "select b.TAXCODE from DCP_PURCHASETEMPLATE a" +
                        " left join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                        " left join DCP_PURCHASETEMPLATE_ORG c on c.eid=a.eid and c.PURTEMPLATENO=a.PURTEMPLATENO " +
                        " where a.EID='" + eid + "' and c.organizationno='" + organization + "' and a.SUPPLIERNO='" + supplier + "' and a.PURTEMPLATENO='" + pTemplate + "' and a.status='100' and b.pluno='" + pluNo + "'";
                List<Map<String, Object>> getPurTempData = dao.executeQuerySQL(purTempSql, null);

                if (null != getPurTempData && !getPurTempData.isEmpty()) {
                    Map<String, Object> oneData = getPurTempData.get(0);
                    taxCode = oneData.get("TAXCODE").toString();

                }
            }

            if (StringUtils.isEmpty(taxCode) && StringUtils.isNotEmpty(supplier)) {
                String bizSql = "select * from dcp_bizpartner a where a.eid='" + eid + "' and a.bizpartnerno='" + supplier + "' ";
                List<Map<String, Object>> getBizData = dao.executeQuerySQL(bizSql, null);
                if (null != getBizData && !getBizData.isEmpty()) {
                    Map<String, Object> oneData = getBizData.get(0);
                    taxCode = oneData.get("TAXCODE").toString();
                }
            }

            if (StringUtils.isEmpty(taxCode) && StringUtils.isNotEmpty(pluNo)) {
                String getGoodsSql = " SELECT a.INPUTTAXCODE FROM DCP_GOODS a " +
                        " WHERE EID='" + eid + "'and PLUNO='" + pluNo + "'";
                List<Map<String, Object>> getGoodsInfo = dao.executeQuerySQL(getGoodsSql, null);

                if (null != getGoodsInfo && !getGoodsInfo.isEmpty()) {
                    Map<String, Object> stringObjectMap = getGoodsInfo.get(0);
                    taxCode = stringObjectMap.get("INPUTTAXCODE").toString();
                }
            }


        } else if ("X".equals(taxType)) {
            //直接取商品的销项税
            if (StringUtils.isEmpty(taxCode)) {
                String getGoodsSql = " SELECT a.TAXCODE FROM DCP_GOODS a " +
                        " WHERE EID='" + eid + "'and PLUNO='" + pluNo + "'";
                List<Map<String, Object>> getGoodsInfo = dao.executeQuerySQL(getGoodsSql, null);

                if (null != getGoodsInfo && !getGoodsInfo.isEmpty()) {
                    Map<String, Object> stringObjectMap = getGoodsInfo.get(0);
                    taxCode = stringObjectMap.get("TAXCODE").toString();
                }
            }
        }

        return getTax(eid, taxCode);

    }


    public String getPurTemplateNo(String eid, String organizationNo, String supplier, String pluNo) throws Exception {
        String purTemplateNo = "";
        DsmDAO dao = StaticInfo.dao;

        String purTempSql = "select a.PURTEMPLATENO from DCP_PURCHASETEMPLATE a" +
                " left join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                " left join DCP_PURCHASETEMPLATE_ORG c on c.eid=a.eid and c.PURTEMPLATENO=a.PURTEMPLATENO " +
                " where a.EID='" + eid + "' and c.organizationno='" + organizationNo + "' and a.SUPPLIERNO='" + supplier + "' and a.status='100' and b.pluno='" + pluNo + "'";
        List<Map<String, Object>> getPurTempData = dao.executeQuerySQL(purTempSql, null);

        if (CollUtil.isNotEmpty(getPurTempData)) {
            purTemplateNo = getPurTempData.get(0).get("PURTEMPLATENO").toString();
        }

        return purTemplateNo;
    }

    public InterTax getInterTax(String eid, String orgNo) throws Exception {

        InterTax interTax = new InterTax();

        interTax.setOrgNo(orgNo);
        DsmDAO dao = StaticInfo.dao;

        String querySql = " SELECT TAXPAYER_TYPE,OUTPUTTAX,INPUT_TAXCODE " +
                " ,tc1.TAXCODE OUTPUTTAXCODE,tc1.TAXRATE OUTPUTTAXRATE,tc1.INCLTAX OUTPUTINCLTAX,tc1.TAXCALTYPE OUTPUTTAXCALTYPE " +
                " ,tc2.TAXCODE INPUTTAXCODE,tc2.TAXRATE INPUTTAXRATE,tc2.INCLTAX INPUTINCLTAX,tc2.TAXCALTYPE INPUTTAXCALTYPE " +
                " FROM DCP_ORG o " +
                " LEFT JOIN DCP_TAXCATEGORY tc1 on tc1.eid=o.eid and tc1.TAXCODE=o.OUTPUTTAX " +
                " LEFT JOIN DCP_TAXCATEGORY tc2 on tc2.eid=o.eid and tc2.TAXCODE=o.INPUT_TAXCODE " +
                " WHERE o.EID='" + eid + "' AND o.ORGANIZATIONNO='" + orgNo + "'";
        List<Map<String, Object>> getData = dao.executeQuerySQL(querySql, null);

        if (CollectionUtils.isNotEmpty(getData)) {

            interTax.setTaxPayerType(getData.get(0).get("TAXPAYER_TYPE").toString());
            Tax inPutTax = new Tax();
            Tax outPutTax = new Tax();

            interTax.setInPutTax(inPutTax);
            interTax.setOutPutTax(outPutTax);

            inPutTax.setTaxCode(getData.get(0).get("INPUTTAXCODE").toString());
            inPutTax.setTaxRate(Double.parseDouble(StringUtils.toString(getData.get(0).get("INPUTTAXRATE").toString(), "0")));
            inPutTax.setInclTax(getData.get(0).get("INPUTINCLTAX").toString());
            inPutTax.setTaxCalType(getData.get(0).get("INPUTTAXCALTYPE").toString());

            outPutTax.setTaxCode(getData.get(0).get("OUTPUTTAXCODE").toString());
            outPutTax.setTaxRate(Double.parseDouble(getData.get(0).get("OUTPUTTAXRATE").toString()));
            outPutTax.setInclTax(getData.get(0).get("OUTPUTINCLTAX").toString());
            outPutTax.setTaxCalType(getData.get(0).get("OUTPUTTAXCALTYPE").toString());
        }

        return interTax;
    }

    @Data
    public class Tax {
        String taxCode;
        double taxRate;
        String inclTax;
        String taxCalType;

        //采购模板编号
        private String pTemplateNo;
    }


    /*
     * 用于小规模纳税人内部交易结算时取内部的进项税和销项税
     */
    @Data
    public class InterTax {
        String orgNo;
        String taxPayerType;

        Tax inPutTax;
        Tax outPutTax;
    }

}
