package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_StockOrderLockDetailReq;
import com.dsc.spos.json.cust.req.DCP_StockOrderLockDetailReq.OrganizationList;
import com.dsc.spos.json.cust.res.DCP_StockOrderLockDetailRes;
import com.dsc.spos.json.cust.res.DCP_StockOrderLockDetailRes.levelRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 库存结存查询
 * @author 2020-06-08
 *
 */
public class DCP_StockOrderLockDetail extends SPosBasicService<DCP_StockOrderLockDetailReq, DCP_StockOrderLockDetailRes> {

    @Override
    protected boolean isVerifyFail(DCP_StockOrderLockDetailReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_StockOrderLockDetailReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_StockOrderLockDetailReq>(){};
    }

    @Override
    protected DCP_StockOrderLockDetailRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_StockOrderLockDetailRes();
    }

    @Override
    protected DCP_StockOrderLockDetailRes processJson(DCP_StockOrderLockDetailReq req) throws Exception {
        // TODO Auto-generated method stub

        DCP_StockOrderLockDetailRes res = null;
        res = this.getResponse();

        try {

            String sql = "";
            sql = this.getQuerySql(req);
            List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
            levelRes lvRes = res.new levelRes();

            int totalRecords = 0;	//总笔数
            int totalPages = 0;		//总页数
            lvRes.setBillList(new ArrayList<DCP_StockOrderLockDetailRes.BillList>());

            if(queryDatas.size() > 0 && !queryDatas.isEmpty()){

                Map<String, Object> oneData2 = queryDatas.get(0);
                String num = oneData2.get("NUM").toString();

                if(req.getPageSize() != 0 && req.getPageNumber() != 0){
                    totalRecords=Integer.parseInt(num);
                    totalPages = totalRecords / req.getPageSize();
                    totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                }

                // 拼接返回图片路径  by jinzma 20210705
                String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }

                for (Map<String, Object> map : queryDatas) {
                    DCP_StockOrderLockDetailRes.BillList lvPlu = res.new BillList();

                    String billNo = map.getOrDefault("BILLNO","").toString();
                    String item = map.getOrDefault("ITEM","1").toString();

                    String channelId = map.getOrDefault("CHANNELID","").toString();
                    String channelName = map.getOrDefault("CHANNELNAME","").toString();
                    String organizationNo = map.getOrDefault("ORGANIZATIONNO","").toString();
                    String organizationName = map.getOrDefault("ORGANIZATIONNAME","").toString();
                    String pluNo = map.getOrDefault("PLUNO","").toString();
                    String pluName = map.getOrDefault("PLUNAME","").toString();
                    String featureNo = map.getOrDefault("FEATURENO","").toString();
                    String featureName = map.getOrDefault("FEATURENAME","").toString();
                    String sUnit = map.getOrDefault("SUNIT","").toString();
                    String sUnitName = map.getOrDefault("SUNITNAME","").toString();
                    String warehouse = map.getOrDefault("WAREHOUSE","").toString();
                    String warehouseName = map.getOrDefault("WAREHOUSENAME","").toString();
                    String listImage = map.getOrDefault("LISTIMAGE","").toString();
                    if (!Check.Null(listImage)){
                        listImage = domainName + listImage;
                    }
//					String oQty = map.getOrDefault("OQTY","0").toString();
                    String sQty = map.getOrDefault("SQTY","0").toString();
//					String tQty = map.getOrDefault("TQTY","0").toString();
//					String saleType = map.getOrDefault("SALETYPE","0").toString();
                    String memo = map.getOrDefault("MEMO","").toString();

                    String lockTime = map.getOrDefault("LOCKTIME","").toString();
                    String createOpId = map.getOrDefault("CREATEOPID","").toString();
                    String createOpName = map.getOrDefault("CREATEOPNAME","").toString();
                    String createTime = map.getOrDefault("CREATETIME","").toString();

                    lvPlu.setBillNo(billNo);
                    lvPlu.setItem(item);
                    lvPlu.setChannelId(channelId);
                    lvPlu.setChannelName(channelName);
                    lvPlu.setOrganizationNo(organizationNo);
                    lvPlu.setOrganizationName(organizationName);
                    lvPlu.setPluNo(pluNo);
                    lvPlu.setPluName(pluName);
                    lvPlu.setFeatureNo(featureNo);
                    lvPlu.setFeatureName(featureName);
                    lvPlu.setsUnit(sUnit);
                    lvPlu.setsUnitName(sUnitName);
                    lvPlu.setWarehouse(warehouse);
                    lvPlu.setWarehouseName(warehouseName);
                    lvPlu.setListImage(listImage);

//					lvPlu.setoQty(oQty);
//					lvPlu.settQty(tQty);
                    lvPlu.setsQty(sQty);
//					lvPlu.setSaleType(saleType);
                    lvPlu.setMemo(memo);
//					lvPlu.setLockDate(lockDate);
                    lvPlu.setLockTime(lockTime);
                    lvPlu.setCreateOpId(createOpId);
                    lvPlu.setCreateOpName(createOpName);
                    lvPlu.setCreateTime(createTime);

                    lvRes.getBillList().add(lvPlu);

                }
            }

            res.setDatas(lvRes);

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            // TODO: handle exception
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败："+e.getMessage());
        }


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_StockOrderLockDetailReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;

        String eId = req.geteId();
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        String langType = req.getLangType();
        StringBuffer sqlbuf = new StringBuffer("");
        List<OrganizationList> orgDatas = req.getRequest().getOrganizationList();

        sqlbuf.append(""
                + " SELECT * from ( "
                + " SELECT "
                + " COUNT (*) OVER () AS NUM , dense_rank() over (order BY  a.billno, a.item , a.pluNo , a.organizationNO , a.channelId , a.featureNo , a.warehouse  ) rn , "
                + " a.eId  , a.channelId ,  i.channelName ,  a.billNo , a.billType , a.item , a.sunit ,  a.organizationNO , a.pluNo , a.featureNo , a.warehouse , a.sQty , a.baseUnit ,  a.bQty , "
                + " a.lockTime ,  a.memo ,  a.createOpId , a.createOPName , a.createTime , a.lastmodiOpId, a.lastModiOpName , a.lastModiTime , "
                + " b.org_name AS organizationName , c.plu_name AS pluName , d.featurename , e.uname AS sUnitName ,  e2.uname AS baseUnitName , "
                + " f.warehouse_name AS warehouseName , h.Listimage "
                + " FROM DCP_STOCK_LOCK a "
                + " LEFT JOIN DCP_org_lang b ON a.eId = b.eId AND a.organizationNo = b.organizationNo AND b.lang_type = '"+langType+"' "
                + " LEFT JOIN Dcp_Goods_Lang c ON a.eId = c.eId AND a.pluNo = c.pluNo AND c.lang_type = '"+langType+"'"
                + " LEFT JOIN Dcp_Goods_Feature_lang d ON a.eid = d.eid AND a.pluNo = d.pluNo AND a.featureNo = d.featureno  AND d.lang_type = '"+langType+"'  "
                + " LEFT JOIN dcp_unit_lang e ON a.eId = e.eid AND a.sUnit = e.unit AND e.lang_type = '"+langType+"' "
                + " LEFT JOIN dcp_unit_lang e2 ON a.eId = e2.eid AND a.baseUnit = e2.unit AND e2.lang_type = '"+langType+"' "
                + " LEFT JOIN dcp_warehouse_lang f ON a.eId = f.eId AND a.warehouse = f.warehouse AND  f.lang_type = '"+langType+"' "
                + "	LEFT JOIN DCP_GOODSIMAGE h ON a.eid = h.eid AND a.pluno = h.pluNo AND h.APPTYPE='ALL'  "
                + " LEFT JOIN Crm_Channel i ON a.eid = i.eid AND a.channelid = i.channelid   "
                + "	"
                + " WHERE a.eId = '"+eId+"' "  );

        if(!Check.Null(req.getRequest().getPluNo())){
            String pluNo = req.getRequest().getPluNo();
            sqlbuf.append(" AND a.pluNo = '"+ pluNo +"' ");
        }
        if(!Check.Null(req.getRequest().getBillNo())){
            sqlbuf.append(" AND a.billNo = '"+ req.getRequest().getBillNo() +"' ");
        }
        if(!Check.Null(req.getRequest().getBeginDate()) && !Check.Null(req.getRequest().getEndDate())){
            String beginDate = req.getRequest().getBeginDate();
            String endDate = req.getRequest().getEndDate();

            sqlbuf.append(" AND to_date( to_char(a.lockTime ,'yyyy-MM-dd' ), 'YYYY-MM-dd' )  BETWEEN   to_Date('"+beginDate+"', 'yyyy-MM-dd')  AND  to_Date('"+endDate+"', 'yyyy-MM-dd')  ");
        }

        if(orgDatas !=null && !orgDatas.isEmpty() && orgDatas.size() > 0  ){

            String[] orgArr = new String[orgDatas.size()] ;
            int i=0;
            for (OrganizationList lvOrg : orgDatas)
            {
                String org = "";
                if(!Check.Null(lvOrg.getOrganizationNo())){
                    org = lvOrg.getOrganizationNo();
                }
                orgArr[i] = org;
                i++;
            }
            String orgStr = getString(orgArr);

            sqlbuf.append(" AND a.organizationno in( "+ orgStr +" ) ");
        }

        sqlbuf.append(" order by a.pluNO , a.organizationno , a.warehouse   "
                + " )  where  rn > "+startRow+" AND rn < = "+(startRow+pageSize)+"  "
                + " order by rn , pluNO , organizationno ,  warehouse  ");

        sql = sqlbuf.toString();
        return sql;
    }

    protected String getString(String[] str)
    {
        String str2 = "";
        for (String s:str)
        {
            str2 = str2 + "'" + s + "'"+ ",";
        }
        if (str2.length()>0)
        {
            str2=str2.substring(0,str2.length()-1);
        }

        return str2;
    }

}
