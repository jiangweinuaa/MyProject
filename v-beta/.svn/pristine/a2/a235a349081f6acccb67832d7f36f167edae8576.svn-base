package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_StockChannelOrderDetailReq;
import com.dsc.spos.json.cust.res.DCP_StockChannelOrderDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 渠道库存分配单据列表查询
 * @author 2020-06-05
 *
 */
public class DCP_StockChannelOrderDetail extends SPosBasicService<DCP_StockChannelOrderDetailReq, DCP_StockChannelOrderDetailRes> {

    @Override
    protected boolean isVerifyFail(DCP_StockChannelOrderDetailReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_StockChannelOrderDetailReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_StockChannelOrderDetailReq>(){};
    }

    @Override
    protected DCP_StockChannelOrderDetailRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_StockChannelOrderDetailRes();
    }

    @Override
    protected DCP_StockChannelOrderDetailRes processJson(DCP_StockChannelOrderDetailReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_StockChannelOrderDetailRes res = null;
        res = this.getResponse();

        try {

            String sql = this.getQuerySql(req);
            List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
            DCP_StockChannelOrderDetailRes.levelRes lvRes = res.new levelRes();

            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("BILLNO", true);
            condition.put("CHANNELID", true);
            condition.put("ORGANIZATIONNO", true);
            condition.put("PLUNO", true);
            condition.put("SUNIT", true);
            condition.put("WAREHOUSE", true);
            condition.put("FEATURENO", true);
            // 调用过滤函数
            List<Map<String, Object>> getDatas = MapDistinct.getMap(queryDatas, condition);

            lvRes.setPluList( new ArrayList<DCP_StockChannelOrderDetailRes.PluList>());

            int totalRecords = 0;
            int totalPages = 0;

            if(getDatas.size() > 0 && !getDatas.isEmpty()){
                // 拼接返回图片路径  by jinzma 20210705
                String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }

                String num = getDatas.get(0).get("NUM").toString();
                if(req.getPageSize() != 0 && req.getPageNumber() != 0){
                    totalRecords=Integer.parseInt(num);
                    totalPages = totalRecords / req.getPageSize();
                    totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                }

                for (Map<String, Object> map : getDatas) {
                    DCP_StockChannelOrderDetailRes.PluList lvPlu = res.new PluList();
                    String billNo = map.get("BILLNO").toString();
                    String channelId = map.get("CHANNELID").toString();
                    String channelName = map.getOrDefault("CHANNELNAME","").toString();
                    String orgNo = map.getOrDefault("ORGANIZATIONNO","").toString();
                    String orgName = map.getOrDefault("ORGANIZATIONNAME","").toString();
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
                        listImage = domainName+listImage;
                    }
                    String direction = map.get("DIRECTION").toString();
                    String sQty = map.get("SQTY").toString();

                    lvPlu.setBillNo(billNo);
                    lvPlu.setChannelId(channelId);
                    lvPlu.setChannelName(channelName);
                    lvPlu.setOrganizationNo(orgNo);
                    lvPlu.setOrganizationName(orgName);
                    lvPlu.setPluNo(pluNo);
                    lvPlu.setPluName(pluName);
                    lvPlu.setFeatureNo(featureNo);
                    lvPlu.setFeatureName(featureName);
                    lvPlu.setsUnit(sUnit);
                    lvPlu.setsUnitName(sUnitName);
                    lvPlu.setWarehouse(warehouse);
                    lvPlu.setWarehouseName(warehouseName);
//					lvPlu.setFileName(fileName);
                    lvPlu.setListImage(listImage);
                    lvPlu.setDirection(direction);
                    lvPlu.setsQty(sQty);

                    lvRes.getPluList().add(lvPlu);
//					res.getPluList().add(lvPlu);

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
    protected String getQuerySql(DCP_StockChannelOrderDetailReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        if(pageNumber ==0 || pageSize == 0){
            pageNumber = 1;
            pageSize = 99999;
        }

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        String langType = req.getLangType();

        String billNo = req.getRequest().getBillNo();
        String channelId = req.getRequest().getChannelId();

        sqlbuf.append(""
                + " SELECT * from ( "
                + " SELECT"
                + " count(*) OVER() AS NUM,  dense_rank() over (order BY a.billNo, a.channelId , a.organizationno, a.pluNo, a.sUnit, a.warehouse,a.featureNo  ) rn, "
                + " a.eid , a.billNo , a.billType , a.channelId , a.item , a.organizationno , a.pluNo , a.sunit , a.featureNo , a.warehouse , a.direction ,"
                + " a.sqty , a.baseunit , a.bqty , "
                + " b.plu_name AS pluname , c.org_name AS orgName , d.uname AS sunitName , e.warehouse_name AS warehouseName "
                + " , f.listImage , g.featureName , i.channelName "
                + " FROM Dcp_Stock_Channel_Billgoods a "
                + " LEFT JOIN DCP_goods_lang b ON a.eId = b.eid AND a.pluNo = b.pluno AND b.lang_type = '"+langType+"' "
                + " LEFT JOIN DCP_org_lang c  ON a.eId = c.eid AND a.organizationno = c.organizationno AND c.lang_type = '"+langType+"' "
                + " LEFT JOIN DCP_UNIT_LANG d ON a.eId = d.eid AND a.sunit = d.unit AND d.lang_type = '"+langType+"' "
                + " LEFT JOIN DCP_warehouse_LANG e ON a.eId = e.eid AND a.warehouse = e.warehouse  AND e.lang_type = '"+langType+"' "
                + " LEFT JOIN DCP_GOODS_FEATURE_LANG g ON a.eid = g.eid AND a.pluNo = g.pluNo AND a.featureNo = g.featureno and g.lang_type = '"+langType+"' "
                + "	LEFT JOIN  DCP_GOODSIMAGE f ON a.eid = f.eid AND a.pluno = f.pluNo AND f.APPTYPE='ALL' "
                + " LEFT JOIN Crm_Channel i ON a.eid = i.eid AND a.channelid = i.channelid "

//				+ " LEFT JOIN  (  "
//				+ " SELECT a.eId, a.templateId , b.pluNo , b.listImage    FROM Dcp_Imagetemplate  a "
//				+ " INNER JOIN   Dcp_Imagetemplate_Goods b ON a.eid = b.eId AND a.templateId = b.templateId "
//				+ " WHERE a.Eid = '"+eId+"' AND a.templateType = 'COMMON'  AND a.status = '100' "
//				+ " ) f ON a.Eid = f.eId AND a.pluNo = f.pluNo "
                + "	"

                + " WHERE a.eId = '"+eId+"' "  );

        if(!Check.Null(channelId)){
            sqlbuf.append(" AND a.channelid  = '"+channelId+"' ");
        }

        if(!Check.Null(billNo)){
            sqlbuf.append(" AND   a.billNo  = '"+ billNo +"'   ");
        }


        sqlbuf.append(" order by  a.channelId , a.pluNo , a.warehouse  "
                + " )  t WHERE t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize) +""
                + "  order by channelId   , pluNo, warehouse ");

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
