package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dsc.spos.json.cust.req.DCP_WarehouseQueryReq;
import com.dsc.spos.json.cust.res.DCP_WarehouseQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：WarehouseGet
 * 說明：仓库查询
 * 服务说明：仓库查询
 *
 * @author luoln
 * @since 2017-11-30
 */
public class DCP_WarehouseQuery extends SPosBasicService<DCP_WarehouseQueryReq, DCP_WarehouseQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_WarehouseQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        String getType = req.getRequest().getGetType();

        if (Check.Null(getType)) {
            errMsg.append("查询分类不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_WarehouseQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_WarehouseQueryReq>() {
        };
    }

    @Override
    protected DCP_WarehouseQueryRes getResponseType() {

        return new DCP_WarehouseQueryRes();
    }

    @Override
    protected DCP_WarehouseQueryRes processJson(DCP_WarehouseQueryReq req) throws Exception {

        try {
            DCP_WarehouseQueryRes res = this.getResponseType();
            //查詢資料
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            int totalRecords;                                //总笔数
            int totalPages;                                    //总页数
            res.setDatas(new ArrayList<DCP_WarehouseQueryRes.level1Elm>());
            if (getQData != null && getQData.isEmpty() == false) {

                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                for (Map<String, Object> oneData : getQData) {
                    DCP_WarehouseQueryRes.level1Elm oneLv1 = res.new level1Elm();
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
                    oneLv1.setIsLocation(oneData.get("ISLOCATION").toString());
                    oneLv1.setStockManageType(oneData.get("STOCKMANAGETYPE").toString());
                    oneLv1.setStatus(oneData.get("STATUS").toString());
                    oneLv1.setWareHouseType(oneData.get("WAREHOUSE_TYPE").toString());
                    oneLv1.setIsCheckStock(oneData.get("ISCHECKSTOCK").toString());
                    res.getDatas().add(oneLv1);
                    oneLv1 = null;
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
        } catch (Exception e) {

            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {


    }

    @Override
    protected String getQuerySql(DCP_WarehouseQueryReq req) throws Exception {

        //查詢條件
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        String getType = req.getRequest().getGetType();             //仓库开窗查询时传1 后端要做分页处理 ,无分页时传2 则后端不做分页处理
        String warehouseType = req.getRequest().getWarehouseType();  // null或0：查询所有仓库，1：不包含在途仓
        String oShopId = req.getRequest().getShopId();
        if (!Check.Null(oShopId))
            shopId = oShopId;    //取前端传入的门店编号
        List<DCP_WarehouseQueryReq.levelorgElm> orgList = req.getRequest().getOrgList();

        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String isCheckRestrictGroup = req.getRequest().getIsCheckRestrictGroup();

        String orgStr="";
        if (orgList != null && orgList.size() > 0) {
            List<String> items = new ArrayList<>();
            for (DCP_WarehouseQueryReq.levelorgElm levelorgElm : orgList) {
                items.add(levelorgElm.getOrgNo());
            }

            orgStr = PosPub.getArrayStrSQLIn(items.toArray(new String[items.size()]));
        }


        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over() num,row_number() over (order by a.warehouse ) rn,a.*,b.warehouse_name,c.ORG_NAME from dcp_warehouse a"
                + " left join dcp_warehouse_lang b "
                + "  on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.warehouse and b.lang_type='" + langType + "' "
                + " left join dcp_org_lang c on a.eid=c.eid and a.organizationno=c.ORGANIZATIONNO and c.LANG_TYPE='" + langType + "' "
                + " where a.eid='" + eId + "' and a.status='100'"
                + " ");

        if (orgStr.length()>0) {
            //List<String> items = new ArrayList<>();
            //for (DCP_WarehouseQueryReq.levelorgElm levelorgElm : orgList) {
            //    items.add(levelorgElm.getOrgNo());
            //}

             //orgStr = PosPub.getArrayStrSQLIn(items.toArray(new String[items.size()]));

            sqlbuf.append(" and a.organizationno in (" + orgStr + ") ");
        } else {
            sqlbuf.append(" and a.organizationno = '" + shopId + "' ");
        }

        if("1".equals(isCheckRestrictGroup)) {
            //查询采购控制组
            StringBuffer cSqlSb=new StringBuffer("select a.WAREHOUSE from DCP_RESTRICTGROUP_WAREHOUSE a " +
                    " inner join DCP_RESTRICTGROUP_EMP b on a.eid=b.eid and a.GROUPNO=b.GROUPNO and a.grouptype=b.grouptype " +
                    " inner join DCP_RESTRICTGROUP c on a.eid=c.eid and a.GROUPNO=c.GROUPNO and a.grouptype=c.grouptype " +
                    " where a.status='100' and b.status='100' and c.status='100' and b.EMPLOYEENO='"+employeeNo+"' and a.eid='"+eId+"' ");

            if(Check.Null(orgStr)){
                cSqlSb.append(" and a.organizationno ='"+shopId+"' ");
            }else{
                cSqlSb.append(" and a.organizationno in ("+orgStr+")");
            }

            cSqlSb.append("union all " +
                    " select a.WAREHOUSE from DCP_RESTRICTGROUP_WAREHOUSE a " +
                    " inner join DCP_RESTRICTGROUP_DEPT b on a.eid=b.eid and a.GROUPNO=b.GROUPNO and a.grouptype=b.grouptype " +
                    " inner join DCP_RESTRICTGROUP c on a.eid=c.eid and a.GROUPNO=c.GROUPNO and a.grouptype=c.grouptype " +
                    " where a.status='100' and b.status='100' and c.status='100' and b.DEPARTMENTNO='"+departmentNo+"' and a.eid='"+eId+"' ");
            if(Check.Null(orgStr)){
                cSqlSb.append(" and a.organizationno ='"+shopId+"' ");
            }else{
                cSqlSb.append(" and a.organizationno in ("+orgStr+")");
            }

            List<Map<String, Object>> cgList = this.doQueryData(cSqlSb.toString(), null);

            if (cgList.size() > 0) {
                List<String> warehouseList = cgList.stream().map(x -> x.get("WAREHOUSE").toString()).distinct().collect(Collectors.toList());
                String warehouseStr = PosPub.getArrayStrSQLIn(warehouseList.toArray(new String[warehouseList.size()]));

                sqlbuf.append(" and a.warehouse in (" + warehouseStr + ") ");
            }
        }

        if (StringUtils.equals(warehouseType, "1"))  //1：不包含在途仓
        {
            sqlbuf.append(" and a.warehouse_type<>'3'");
        }
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.warehouse like '%%" + keyTxt + "%%' or b.warehouse_name like '%%" + keyTxt + "%%')");
        }

        String[] whTypes = req.getRequest().getWhType();
        if(whTypes!=null&&whTypes.length>0){
            String whTypeStr = "";
            for(String s:whTypes){
                whTypeStr+="'"+s+"',";
            }
            whTypeStr=whTypeStr.substring(0,whTypeStr.length()-1);
            sqlbuf.append(" and a.WAREHOUSE_TYPE in ("+whTypeStr+") ");
        }

        if (StringUtils.equals(getType, "1"))    //1.后端要做分页  2.后端不做分页
        {
            sqlbuf.append(" ) tbl where rn >" + startRow + " and rn <=" + (startRow + pageSize));
        } else {
            sqlbuf.append(" ) tbl where 1=1 ");
        }

        sql = sqlbuf.toString();
        return sql;
    }
}
