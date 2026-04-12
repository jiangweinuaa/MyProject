package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_N_MinQtyTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_N_MinQtyTemplateQueryRes;
import com.dsc.spos.json.cust.res.DCP_N_MinQtyTemplateQueryRes.*;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务函数：DCP_N_MinQtyTemplateQuery
 * 服务说明：N-起售量模板查询
 * @author jinzma
 * @since  2024-04-18
 */
public class DCP_N_MinQtyTemplateQuery extends SPosBasicService<DCP_N_MinQtyTemplateQueryReq, DCP_N_MinQtyTemplateQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_N_MinQtyTemplateQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_N_MinQtyTemplateQueryReq> getRequestType() {
        return new TypeToken<DCP_N_MinQtyTemplateQueryReq>(){};
    }

    @Override
    protected DCP_N_MinQtyTemplateQueryRes getResponseType() {
        return new DCP_N_MinQtyTemplateQueryRes();
    }

    @Override
    protected DCP_N_MinQtyTemplateQueryRes processJson(DCP_N_MinQtyTemplateQueryReq req) throws Exception {

        // 查詢資料
        DCP_N_MinQtyTemplateQueryRes res = this.getResponse();

        try {
            // 单头查询
            String sql = this.getQuerySql(req);

            List<Map<String, Object>> getQData_Count = this.doQueryData(sql, null);
            int totalRecords; // 总笔数
            int totalPages; // 总页数

            if (getQData_Count != null && !getQData_Count.isEmpty()) {
                Map<String, Object> oneData_Count = getQData_Count.get(0);
                String num = oneData_Count.get("NUM").toString();
                totalRecords = Integer.parseInt(num);

                // 算总页数
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                String sJoinTemplateId="";
                String sJoinEid="";
                for (Map<String, Object> tempmap : getQData_Count) {
                    sJoinTemplateId+=tempmap.get("TEMPLATEID").toString()+",";
                    sJoinEid+=tempmap.get("EID").toString()+",";
                }

                //
                Map<String, String> mapOrder=new HashMap<String, String>();
                mapOrder.put("TEMPLATEID", sJoinTemplateId);
                mapOrder.put("EID", sJoinEid);
                //
                MyCommon cm=new MyCommon();
                String withasSql_Orderno=cm.getFormatSourceMultiColWith(mapOrder);

                if (withasSql_Orderno.isEmpty()) {
                    res.setSuccess(false);
                    res.setServiceDescription("查询失败--模板编号转换成临时表的方法处理失败！");
                    return res;
                }

                sql = this.getQuery_detailSql(req,withasSql_Orderno);
                List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);

                if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
                    // 单头主键字段
                    Map<String, Boolean> condition = new HashMap<>(); // 查詢條件
                    condition.put("TEMPLATEID", true);
                    // 调用过滤函数
                    List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);
                    res.setDatas(new ArrayList<>());

                    // 时间格式化
                    SimpleDateFormat simptemp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 创建时间格式化

                    for (Map<String, Object> oneData : getQHeader) {
                        Datas oneLv1 = res.new Datas();

                        // 取出第一层

                        String templateId = oneData.get("TEMPLATEID").toString();
                        String templateName = oneData.get("TEMPLATENAME").toString();
                        String memo = oneData.get("MEMO").toString();
                        String status1 = oneData.get("STATUS").toString();
                        String restrictShop = oneData.get("RESTRICTSHOP").toString();
                        Date CREATETIME = simptemp.parse(oneData.get("CREATETIME").toString());
                        String createtime = simptemp.format(CREATETIME);
                        String createopid = oneData.get("CREATEOPID").toString();
                        String createopname = oneData.get("CREATEOPNAME").toString();
                        Date LASTTIME = simptemp.parse(oneData.get("LASTMODITIME").toString());
                        String updateTime = simptemp.format(LASTTIME);
                        String lastmodiopid = oneData.get("LASTMODIOPID").toString();
                        String lastmodiname = oneData.get("LASTMODINAME").toString();

                        // 设置响应
                        oneLv1.setTemplateId(templateId);
                        oneLv1.setTemplateName(templateName);
                        oneLv1.setMemo(memo);
                        oneLv1.setStatus(status1);
                        oneLv1.setRestrictShop(restrictShop);
                        oneLv1.setCreatetime(createtime);
                        oneLv1.setCreateopid(createopid);
                        oneLv1.setCreateopname(createopname);
                        oneLv1.setUpdateTime(updateTime);
                        oneLv1.setLastmodiopid(lastmodiopid);
                        oneLv1.setLastmodiname(lastmodiname);

                        oneLv1.setShopList(new ArrayList<>());

                        Map<String, Object> cond=new HashMap<>();
                        cond.put("TEMPLATEID",templateId);
                        List<Map<String, Object>>  tempList=MapDistinct.getWhereMap(getQDataDetail,cond,true);

                        //去重ID
                        Map<String, Boolean> condB=new HashMap<>();
                        condB.put("ID",true);
                        List<Map<String, Object>>  ids=MapDistinct.getMap(tempList,condB);

                        // 判断适用门店范围
                        for (Map<String, Object> oneData2 : ids) {
                            if (!Check.Null(oneData.get("ID").toString())) {
                                Shop oneLv2 = res.new Shop();
                                String id = oneData2.get("ID").toString();
                                String name = oneData2.get("NAME").toString();

                                oneLv2.setId(id);
                                oneLv2.setName(name);
                                // 添加
                                oneLv1.getShopList().add(oneLv2);
                            }
                        }
                        oneLv1.setPluList(new ArrayList<>());

                        //去重PLUNO
                        condB=new HashMap<>();
                        condB.put("PLUNO",true);
                        List<Map<String, Object>>  plunos=MapDistinct.getMap(tempList,condB);
                        //
                        for (Map<String, Object> oneData3 : plunos) {
                            Plu oneLv3 = res.new Plu();
                            String pluNo = oneData3.get("PLUNO").toString();
                            String minQty = oneData3.get("MOQ").toString();
                            String maxQty = oneData3.get("MAXQTY").toString();
                            String pluName = oneData3.get("PLU_NAME").toString();
                            String punitName = oneData3.get("UNAME").toString();
                            String price = oneData3.get("PRICE").toString();
                            oneLv3.setPrice(price);
                            oneLv3.setPluNo(pluNo);
                            oneLv3.setMinQty(minQty);
                            oneLv3.setMaxQty(maxQty);
                            oneLv3.setPluName(pluName);
                            oneLv3.setPunitName(punitName);
                            // 添加
                            oneLv1.getPluList().add(oneLv3);
                        }
                        // 添加
                        res.getDatas().add(oneLv1);
                    }

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

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }


    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_N_MinQtyTemplateQueryReq req) throws Exception {
        String keyTxt = req.getRequest().getKeyTxt();
        String status = req.getRequest().getStatus();
        List<DCP_N_MinQtyTemplateQueryReq.Shop> shop = req.getRequest().getShop();
        if (keyTxt == null)
            keyTxt = "";

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow=(pageNumber-1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from (select count(*) over() num, rownum rn,a.* "
                + " from ( " + "SELECT DISTINCT a.EID, a.TEMPLATEID FROM DCP_MINQTYTEMPLATE a "
                + "left join  DCP_MINQTYTEMPLATE_LANG b on a.eid = b.eid and a.templateid = b.templateid  and b.lang_type = '"+req.getLangType()+"'");
        if (shop != null && !shop.isEmpty())
        {
            sqlbuf.append(" LEFT JOIN DCP_MINQTYTEMPLATE_RANGE c ON a.eid = c.eid  AND a.templateid = c.templateid  ");
        }
        sqlbuf.append(" where a.eId = '"+req.geteId()+"'");
        if (status != null && !status.isEmpty())
        {
            sqlbuf.append(" AND a.STATUS = '" + status + "' ");
        }
        if (shop != null && !shop.isEmpty())
        {
            sqlbuf.append(" AND ( c.ID in ( ");
            for (DCP_N_MinQtyTemplateQueryReq.Shop level2Elm : shop)
            {
                sqlbuf.append("'" + level2Elm.getShopId() + "',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.append("  )  OR a.RESTRICTSHOP = 0)");
        }

        if (!keyTxt.isEmpty())
        {
            sqlbuf.append(" AND (a.templateid LIKE '%%" + keyTxt + "%%' or b.templatename LIKE '%%" + keyTxt + "%%') ");
        }

        sqlbuf.append(" ) a ) where rn>"+startRow+" and rn<="+(startRow+pageSize));

        return sqlbuf.toString();
    }

    private String getQuery_detailSql(DCP_N_MinQtyTemplateQueryReq req,String withasSql) {
        String sql;
        String langType = req.getLangType();

        StringBuffer sqlbuf = new StringBuffer("with p AS ("+ withasSql + " ) "
                + "SELECT a.templateId , b.templateName, c.ID, c.NAME, a.memo , a.status, a.RESTRICTSHOP, d.PLUNO, d.MOQ,d.maxqty, a.createtime, a.createopid ,"
                + " a.createopname, a.lastmoditime, a.lastmodiopid, a.lastmodiopname AS lastmodiname,f.plu_name,g.uname,e.WUNIT,e.PUNIT,"
                + " e.baseunit,e.price  "
                + "FROM p "
                + "INNER JOIN DCP_MINQTYTEMPLATE a ON p.EID=a.EID AND p.templateid=a.templateid "
                + "LEFT JOIN DCP_MINQTYTEMPLATE_LANG b ON a.eid = b.eid AND a.templateid = b.templateid AND b.lang_type = '"
                + langType + "'"
                + "LEFT JOIN DCP_MINQTYTEMPLATE_RANGE c ON a.eid = c.eid  AND a.templateId = c.templateId "
                + "LEFT JOIN DCP_MINQTYTEMPLATE_DETAIL d ON a.eid = d.eid AND a.templateid = d.templateid "
                + "LEFT JOIN DCP_GOODS e ON a.eid = e.eid AND d.PLUNO = e.PLUNO "
                + "LEFT JOIN DCP_GOODS_LANG f ON e.PLUNO = f.PLUNO AND a.eid = f.eid AND f.LANG_TYPE ='"
                + langType + "' "
                + "LEFT JOIN DCP_UNIT_LANG g ON e.PUNIT = g.UNIT AND a.eid = g.eid AND g.LANG_TYPE ='"
                + langType + "' ");

        sqlbuf.append(" ORDER BY a.createtime desc ");
        sql = sqlbuf.toString();
        return sql;
    }

}
