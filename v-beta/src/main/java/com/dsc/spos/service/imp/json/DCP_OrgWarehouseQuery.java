package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OrgWarehouseQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrgWarehouseQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_OrgWarehouseQuery extends SPosBasicService<DCP_OrgWarehouseQueryReq, DCP_OrgWarehouseQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_OrgWarehouseQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_OrgWarehouseQueryReq> getRequestType() {
        return new TypeToken<DCP_OrgWarehouseQueryReq>() {
        };
    }

    @Override
    protected DCP_OrgWarehouseQueryRes getResponseType() {
        return new DCP_OrgWarehouseQueryRes();
    }

    @Override
    protected DCP_OrgWarehouseQueryRes processJson(DCP_OrgWarehouseQueryReq req) throws Exception {
        DCP_OrgWarehouseQueryRes res = this.getResponseType();
        //查詢資料
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        int totalRecords;                                //总笔数
        int totalPages;                                    //总页数
        res.setDatas(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(getQData)) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            for (Map<String, Object> oneData : getQData) {
                DCP_OrgWarehouseQueryRes.Datas oneLv1 = res.new Datas();

                // 取得第一層資料庫搜尋結果
                String warehouse = oneData.get("WAREHOUSE").toString();
                String warehouseName = oneData.get("WAREHOUSE_NAME").toString();
                String is_Cost = oneData.get("IS_COST").toString();

                // 處理調整回傳值
                oneLv1.setWarehouse(warehouse);
                oneLv1.setWarehouseName(warehouseName);
                oneLv1.setIsCost(is_Cost);
                oneLv1.setOrganizationNo(oneData.get("ORGANIZATIONNO").toString());
                oneLv1.setOrganizationName(oneData.get("ORG_NAME").toString());
                oneLv1.setDeductionRate(oneData.get("DEDUCTIONRATE").toString());
                oneLv1.setWareHouseType(oneData.get("WAREHOUSE_TYPE").toString());
                oneLv1.setStockManageType(oneData.get("STOCKMANAGETYPE").toString());
                oneLv1.setStatus(oneData.get("STATUS").toString());
                oneLv1.setIsLocation(oneData.get("ISLOCATION").toString());
                oneLv1.setIsCheckStock(oneData.get("ISCHECKSTOCK").toString());
                res.getDatas().add(oneLv1);
            }
        } else {
            totalRecords = 0;
            totalPages = 0;
        }
        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    public void addChildren(String organizationNo,List<String> orgs,List<Map<String, Object>> list ) throws Exception{
        List<Map<String, Object>> downOrgs = list.stream().filter(x -> x.get("UP_ORG").toString().equals(organizationNo)).distinct().collect(Collectors.toList());
        for (Map<String, Object> oneOrg : downOrgs){
            String thisOrgNo = oneOrg.get("ORGANIZATIONNO").toString();
            if(!orgs.contains(thisOrgNo)){
                orgs.add(thisOrgNo);
            }
            addChildren(thisOrgNo,orgs,list);

        }
    }

    @Override
    protected String getQuerySql(DCP_OrgWarehouseQueryReq req) throws Exception {

        List<DCP_OrgWarehouseQueryReq.OrgList> orgList = req.getRequest().getOrgList();
        String orgStr = "";
        if (Check.Null(req.getRequest().getGetType()) || "0".equals(req.getRequest().getGetType())) {
            String orgSql = "select * from DCP_ORG_LEVEL where eid='" + req.geteId() + "' ";
            List<Map<String, Object>> list = this.doQueryData(orgSql, null);
            List<String> orgs=new ArrayList();
            orgs.add(req.getOrganizationNO());
            addChildren(req.getOrganizationNO(),orgs,list);

            //看看有没有传orgList 过滤
            if (CollectionUtils.isNotEmpty(orgList)) {
                List<String> newOrgs=new ArrayList<>();
                for (DCP_OrgWarehouseQueryReq.OrgList oneOrg : orgList) {
                    if(orgs.contains(oneOrg.getOrgNo())){
                        if(!newOrgs.contains(oneOrg.getOrgNo())){
                            newOrgs.add(oneOrg.getOrgNo());
                        }
                    }
                }

                if(newOrgs.size()<=0){
                    //过滤组织与当前组织没有匹配的
                    newOrgs.add("*****");
                }
                orgStr = PosPub.getArrayStrSQLIn(newOrgs.toArray(new String[newOrgs.size()]));

            }else {
                orgStr = PosPub.getArrayStrSQLIn(orgs.toArray(new String[orgs.size()]));
            }
        }else{
            if (CollectionUtils.isNotEmpty(orgList)) {
                List<String> items = new ArrayList<>();
                for (DCP_OrgWarehouseQueryReq.OrgList oneOrg : orgList) {
                    items.add(oneOrg.getOrgNo());
                }

                orgStr = PosPub.getArrayStrSQLIn(items.toArray(new String[items.size()]));
            }
        }



        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        StringBuilder sqlbuf = new StringBuilder();

        sqlbuf.append(" select * from ( ")
                .append(" select count(*) over() num,row_number() over (order by a.organizationno, a.warehouse ) rn," +
                        " a.*,b.warehouse_name,c.ORG_NAME  ")
                .append(" from dcp_warehouse a ")
                .append(" left join dcp_warehouse_lang b on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.warehouse and b.lang_type='").append(req.getLangType()).append("' ")
                .append(" left join dcp_org_lang c on a.eid=c.eid and a.organizationno=c.ORGANIZATIONNO and c.LANG_TYPE='").append(req.getLangType()).append("' ");

        sqlbuf.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            sqlbuf.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (!orgStr.isEmpty()) {
            sqlbuf.append(" and a.organizationno in (").append(orgStr).append(") ");
        }


        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
            String keyTxt = req.getRequest().getKeyTxt();
            sqlbuf.append(" and (a.warehouse like '%%").append(keyTxt).append("%%' ")
                    .append("  or b.warehouse_name like '%%").append(keyTxt).append("%%'")
                    .append(" )");
        }

        sqlbuf.append("  ) tbl where rn >").append(startRow).append(" and rn <=").append(startRow + pageSize);

        return sqlbuf.toString();
    }
}
