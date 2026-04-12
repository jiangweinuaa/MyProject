package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_StockAllocationRuleDetailReq;
import com.dsc.spos.json.cust.res.DCP_StockAllocationRuleDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_StockAllocationRuleDetail
 * 服务说明：分配规则设置查询
 *
 * @author wangzyc 2021-03-15
 */
public class DCP_StockAllocationRuleDetail extends SPosBasicService<DCP_StockAllocationRuleDetailReq, DCP_StockAllocationRuleDetailRes> {

    @Override
    protected boolean isVerifyFail(DCP_StockAllocationRuleDetailReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_StockAllocationRuleDetailReq> getRequestType() {
        return new TypeToken<DCP_StockAllocationRuleDetailReq>() {
        };
    }

    @Override
    protected DCP_StockAllocationRuleDetailRes getResponseType() {
        return new DCP_StockAllocationRuleDetailRes();
    }

    @Override
    protected DCP_StockAllocationRuleDetailRes processJson(DCP_StockAllocationRuleDetailReq req) throws Exception {
        DCP_StockAllocationRuleDetailRes res = null;
        res = this.getResponse();
        String langType = req.getLangType();

        int totalRecords = 0; //总笔数
        int totalPages = 0;

        if (req.getPageNumber() == 0) {
            req.setPageNumber(1);
        }

        if (req.getPageSize() == 0) {
            req.setPageSize(1000);
        }
        try {

            String sql = this.getQuerySql(req);
            String[] conditionValues = {langType,langType,langType,langType};
            List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

            Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查詢條件
            condition1.put("EID", true);
            condition1.put("ORGANIZATIONNO", true);
            condition1.put("PLUNO", true);
            condition1.put("FEATURENO", true);
            condition1.put("SUNIT", true);
            //调用过滤函数
            List<Map<String, Object>> pluMainDatas = MapDistinct.getMap(getQData,condition1);

            // 获取数量
            sql = this.getCount(req);
            List<Map<String, Object>> getCount = this.doQueryData(sql, null);

            res.setDatas(new DCP_StockAllocationRuleDetailRes().new level1Elm());
            // 商品总数
//            Integer num = 0;
            if (!CollectionUtils.isEmpty(getQData)) {

                String num = getCount.get(0).get("NUM").toString();
                if(req.getPageSize() != 0 && req.getPageNumber() != 0){
                    totalRecords=Integer.parseInt(num);
                    totalPages = totalRecords / req.getPageSize();
                    totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                }

                DCP_StockAllocationRuleDetailRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setPluList(new ArrayList<DCP_StockAllocationRuleDetailRes.level2Elm>());
                //
                String ISHTTPS= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=ISHTTPS.equals("1")?"https://":"http://";

                String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                for (Map<String, Object> map : pluMainDatas) {
                    DCP_StockAllocationRuleDetailRes.level2Elm level2Elm = res.new level2Elm();
                    String organizationNo = map.get("ORGANIZATIONNO").toString();
                    String org_name = map.get("ORG_NAME").toString();
                    String pluno = map.get("PLUNO").toString();
                    String plu_name = map.get("PLU_NAME").toString();
                    String featureno = map.get("FEATURENO").toString();
                    String featurename = map.get("FEATURENAME").toString();
                    String sunit = map.get("SUNIT").toString();
                    String uname = map.get("UNAME").toString();
                    String channelId = map.get("CHANNELID").toString();
                    String listimage =  map.get("LISTIMAGE").toString();
                    // 拼接URL 返回
                    if(!Check.Null(listimage)){
                        //
                        if (DomainName.endsWith("/"))
                        {
                            level2Elm.setListImage(httpStr+DomainName+"resource/image/" +listimage);
                        }
                        else
                        {
                            level2Elm.setListImage(httpStr+DomainName+"/resource/image/"+listimage);
                        }
                    }else{
                        level2Elm.setListImage(listimage);
                    }
                    String ruletype = map.get("RULETYPE").toString();

                    level2Elm.setOrganizationNo(organizationNo);
                    level2Elm.setOrganizationName(org_name);
                    level2Elm.setPluNo(pluno);
                    level2Elm.setPluName(plu_name);
                    level2Elm.setFeatureNo(featureno.equals(" ")?"":featureno);
                    level2Elm.setFeatureName(featurename);
                    level2Elm.setSUnit(sunit);
                    level2Elm.setSUnitName(uname);
                    level2Elm.setRuleType(ruletype);


                    level2Elm.setChannelList(new ArrayList<DCP_StockAllocationRuleDetailRes.level3Elm>());
                    for (Map<String, Object> getQDatum : getQData) {
                        String channelid = getQDatum.get("CHANNELID").toString();
                        if(Check.Null(channelid)){
                            continue;
                        }
                        String sorganizationNo = getQDatum.get("ORGANIZATIONNO").toString();
                        String spluno = getQDatum.get("PLUNO").toString();
                        String sfeatureno = getQDatum.get("FEATURENO").toString();
                        String sunit2 = getQDatum.get("SUNIT").toString();
                        if(organizationNo.equals(sorganizationNo)&&pluno.equals(spluno)&&featureno.equals(sfeatureno)&&sunit.equals(sunit2)){
                            DCP_StockAllocationRuleDetailRes.level3Elm level3Elm = res.new level3Elm();
                            String channelname = getQDatum.get("CHANNELNAME").toString();
                            String allocationvalue = getQDatum.get("ALLOCATIONVALUE").toString();
                            level3Elm.setChannelId(channelid);
                            level3Elm.setChannelName(channelname);
                            level3Elm.setAllocationValue(allocationvalue);
                            level2Elm.getChannelList().add(level3Elm);
                        }
                    }
                    level1Elm.getPluList().add(level2Elm);
                }
                res.setDatas(level1Elm);
//                totalRecords = num;
//
//                //算總頁數
//                totalPages = totalRecords / req.getPageSize();
//                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败！" + e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_StockAllocationRuleDetailReq req) throws Exception {
        String sql = "";
        StringBuffer sqlBuffer = new StringBuffer("");
        DCP_StockAllocationRuleDetailReq.level1ELm request = req.getRequest();
        List<DCP_StockAllocationRuleDetailReq.level2ELm> organizationList = request.getOrganizationList();
        String keyTxt = request.getKeyTxt();
        String ruleType = request.getRuleType();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sqlBuffer.append("SELECT * FROM ( " +
                " SELECT COUNT(*) OVER () AS num, DENSE_RANK() OVER (ORDER BY a.ORGANIZATIONNO, a.PLUNO, a.FEATURENO) AS rn , " +
                " a.ORGANIZATIONNO, b.ORG_NAME, a.PLUNO, c.PLU_NAME, a.FEATURENO , d.FEATURENAME, a.SUNIT, e.UNAME, f.LISTIMAGE," +
                " a.RULETYPE , CASE WHEN a.CHANNELID = ' ' OR a.CHANNELID IS NULL THEN NULL ELSE a.CHANNELID END CHANNELID , g.CHANNELNAME,a.ALLOCATIONVALUE" +
                " FROM DCP_STOCK_ALLOCATION_RULE a" +
                " LEFT JOIN DCP_ORG_LANG b ON a.EID = b.EID AND a.ORGANIZATIONNO = b.ORGANIZATIONNO AND b.LANG_TYPE = ? " +
                " LEFT JOIN DCP_GOODS_LANG c ON a.EID = c.EID AND a.PLUNO = c.PLUNO AND c.LANG_TYPE = ? " +
                " LEFT JOIN DCP_GOODS_FEATURE_LANG d ON a.EID = d.EID and a.pluno = d.pluno AND a.FEATURENO = d.FEATURENO AND d.LANG_TYPE = ? " +
                " LEFT JOIN DCP_UNIT_LANG e ON a.EID = e.EID AND a.SUNIT = e.UNIT AND e.LANG_TYPE = ? " +
                " LEFT JOIN DCP_GOODSIMAGE f ON a.EID = f.EID AND f.APPTYPE = 'ALL' AND f.PLUNO = a.PLUNO " +
                " LEFT JOIN CRM_CHANNEL g ON a.EID = g.EID  AND a.CHANNELID = g.CHANNELID" +
                " WHERE a.EID = '" + req.geteId() + "' ");
        if (!CollectionUtils.isEmpty(organizationList)) {
            sqlBuffer.append("AND a.ORGANIZATIONNO IN (");
            organizationList.forEach(organization -> {
                        sqlBuffer.append("'" + organization.getOrganizationNo() + "',");
                    }
            );
            sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
            sqlBuffer.append(" ) ");
        }

        if (!Check.Null(keyTxt)) {
            sqlBuffer.append("AND (a.PLUNO  like  '%%" + keyTxt + "%%' OR  c.PLU_NAME like '%%" + keyTxt + "%%')");
        }

        if (!Check.Null(ruleType)) {
            sqlBuffer.append("AND a.RULETYPE = '" + ruleType + "'");
        }
        sqlBuffer.append(" ORDER BY a.CREATETIME desc) WHERE rn > " + startRow + " AND rn<= " + (startRow + pageSize) + "");
        sql = sqlBuffer.toString();
        return sql;
    }

    /**
     * 查询一共多少条数据
     * @return
     */
    private String getCount(DCP_StockAllocationRuleDetailReq req){
        String sql = "";
        StringBuffer sqlBuffer = new StringBuffer("");
        DCP_StockAllocationRuleDetailReq.level1ELm request = req.getRequest();
        List<DCP_StockAllocationRuleDetailReq.level2ELm> organizationList = request.getOrganizationList();
        String keyTxt = request.getKeyTxt();
        String ruleType = request.getRuleType();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sqlBuffer.append("SELECT COUNT(*) AS num FROM ( " +
                " SELECT DISTINCT ORGANIZATIONNO, PLUNO, FEATURENO FROM ( " +
                " SELECT a.ORGANIZATIONNO, a.PLUNO, a.FEATURENO " +
                " FROM DCP_STOCK_ALLOCATION_RULE a" +
                " LEFT JOIN DCP_GOODS_LANG c ON a.EID = c.EID AND a.PLUNO = c.PLUNO AND c.LANG_TYPE = ? " +
                " WHERE a.EID = '" + req.geteId() + "' ");
        if (!CollectionUtils.isEmpty(organizationList)) {
            sqlBuffer.append("AND a.ORGANIZATIONNO IN (");
            organizationList.forEach(organization -> {
                        sqlBuffer.append("'" + organization.getOrganizationNo() + "',");
                    }
            );
            sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
            sqlBuffer.append(" ) ");
        }

        if (!Check.Null(keyTxt)) {
            sqlBuffer.append("AND (a.PLUNO  like  '%%" + keyTxt + "%%' OR  c.PLU_NAME like '%%" + keyTxt + "%%')");
        }

        if (!Check.Null(ruleType)) {
            sqlBuffer.append("AND a.RULETYPE = '" + ruleType + "'");
        }
        sqlBuffer.append(")) ");
        sql = sqlBuffer.toString();
        return sql;
    }


}
