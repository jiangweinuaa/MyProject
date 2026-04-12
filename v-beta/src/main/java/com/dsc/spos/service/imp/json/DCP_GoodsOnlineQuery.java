package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsOnlineQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsOnlineQuery extends SPosBasicService<DCP_GoodsOnlineQueryReq, DCP_GoodsOnlineQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_GoodsOnlineQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_GoodsOnlineQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsOnlineQueryReq>() {};
    }

    @Override
    protected DCP_GoodsOnlineQueryRes getResponseType() {
        return new DCP_GoodsOnlineQueryRes();
    }

    @Override
    protected DCP_GoodsOnlineQueryRes processJson(DCP_GoodsOnlineQueryReq req) throws Exception {
        try {
            // 查詢資料
            DCP_GoodsOnlineQueryRes res = this.getResponse();
            // 单头总数
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            int totalRecords = 0;     // 总笔数
            int totalPages = 0;       // 总页数
            res.setDatas(new ArrayList<>());
            if (getQData != null && !getQData.isEmpty()) {
                //图片地址
                String ISHTTPS = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";
                String DomainName = PosPub.getPARA_SMS(this.dao, req.geteId(), "", "DomainName");
                String imagePath = httpStr + DomainName + "/resource/image/";
                if (DomainName.endsWith("/")) {
                    imagePath = httpStr + DomainName + "resource/image/";
                }

                // 算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                Map<String, Boolean> condition = new HashMap<>(); // 查詢條件
                condition.put("PLUNO", true);
                // 调用过滤函数
                List<Map<String, Object>> getHead = MapDistinct.getMap(getQData, condition);

                condition.clear();
                condition.put("PLUNO", true);
                condition.put("CLASSNO", true);
                // 调用过滤函数
                List<Map<String, Object>> getClassList = MapDistinct.getMap(getQData, condition);

                condition.clear();
                condition.put("PLUNO", true);
                condition.put("CHANNELID", true);
                // 调用过滤函数
                List<Map<String, Object>> getChannelList = MapDistinct.getMap(getQData, condition);

                /*
                 * condition.clear(); condition.put("PLUNO", true);
                 * condition.put("RANGETYPE", true); condition.put(" ID", true);
                 * //调用过滤函数 List<Map<String, Object>>
                 * getOffShelfRangeList=MapDistinct.getMap(getQData, condition);
                 */

                for (Map<String, Object> oneData : getHead) {
                    DCP_GoodsOnlineQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    String pluNo = oneData.get("PLUNO").toString();
                    oneLv1.setPluNo(pluNo);
                    oneLv1.setPluName(oneData.get("PLU_NAME").toString());
                    oneLv1.setPluType(oneData.get("PLUTYPE").toString());
                    oneLv1.setListImage(oneData.get("LISTIMAGE").toString());
                    oneLv1.setListImageUrl(imagePath + oneData.get("LISTIMAGE").toString());
                    oneLv1.setMaxPrice(oneData.get("MAXPRICE").toString());
                    oneLv1.setMinPrice(oneData.get("MINPRICE").toString());
                    oneLv1.setSortId(oneData.get("SORTID").toString());
                    /*
                     * String offShelfStatus = "N"; String status_offShelf =
                     * oneData.get("STATUS_OFFSHELF").toString();
                     * if(status_offShelf.equals("0")) { offShelfStatus = "N"; }
                     * else { offShelfStatus = "Y"; }
                     */
                    oneLv1.setStatus(oneData.get("STATUS").toString());

                    oneLv1.setClassList(new ArrayList<>());
                    for (Map<String, Object> map : getClassList) {
                        String classNo = map.get("CLASSNO").toString();
                        if(pluNo.equals(map.get("PLUNO").toString()) && !Check.Null(classNo)) {
                            String className = map.get("CLASSNAME").toString();
                            DCP_GoodsOnlineQueryRes.classMemu mapModel = res.new classMemu();
                            mapModel.setClassNo(classNo);
                            mapModel.setClassName(className);
                            oneLv1.getClassList().add(mapModel);
                        }
                    }

                    //【ID1027353】 [荷家3.0]商城商品查询界面增加渠道展示-DCP服务 by jinzma 20220719
                    oneLv1.setChannelList(new ArrayList<>());
                    for (Map<String, Object> map : getChannelList) {
                        String channelId = map.get("CHANNELID").toString();
                        if(pluNo.equals(map.get("PLUNO").toString()) && !Check.Null(channelId)) {
                            String channelName = map.get("CHANNELNAME").toString();
                            String shelfStatus = map.get("SHELFSTATUS").toString();

                            DCP_GoodsOnlineQueryRes.channel mapChannel = res.new channel();
                            mapChannel.setChannelId(channelId);
                            mapChannel.setChannelName(channelName);
                            mapChannel.setStatus(shelfStatus);
                            oneLv1.getChannelList().add(mapChannel);
                        }
                    }


                    /*
                     * oneLv1.setOffShelfRangeList(new
                     * ArrayList<DCP_GoodsOnlineQueryRes.offShelfRange>()); for
                     * (Map<String, Object> map : getOffShelfRangeList) { String
                     * pluNo_offShelf = map.get("PLUNO").toString(); String
                     * rangeType = map.get("RANGETYPE").toString(); String id =
                     * map.get("ID").toString(); String name =
                     * map.get("NAME").toString();
                     * if(rangeType==null||rangeType.isEmpty()) { continue; }
                     * if(pluNo_offShelf.equals(pluNo)==false) { continue; }
                     * DCP_GoodsOnlineQueryRes.offShelfRange mapModel = res.new
                     * offShelfRange(); mapModel.setRangeType(rangeType);
                     * mapModel.setId(id); mapModel.setName(name);
                     * oneLv1.getOffShelfRangeList().add(mapModel);
                     *
                     * }
                     */

                    res.getDatas().add(oneLv1);


                }

            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_GoodsOnlineQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String channelId = req.getRequest().getChannelId();
        String shopId = req.getRequest().getShopId();
        String status = req.getRequest().getStatus();

        // 分页起始位置
        int pageSize = req.getPageSize();
        int startRow = (req.getPageNumber() - 1) * pageSize;

        String keyTxt = req.getRequest().getKeyTxt();
        String companyId = req.getRequest().getCompanyId();
        String classNo = req.getRequest().getClassNo();
        String minPrice = "0";
        String maxPrice = "99999";

        if (!Check.Null(req.getRequest().getMinPrice())) {
            minPrice = req.getRequest().getMinPrice();
        }

        if (!Check.Null(req.getRequest().getMaxPrice())) {
            maxPrice = req.getRequest().getMaxPrice();
        }

        String classType = "ONLINE";
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" select * from ( ");
        sqlbuf.append(" select count(Distinct a.pluno) over() num,dense_rank() over(ORDER BY a.pluno) rn,A.*,"
                              + " b.channelid," );

        if (channelId != null && !channelId.isEmpty() && shopId != null && !shopId.isEmpty())
        {
            //传入渠道上下架状态已经查出来了
            sqlbuf.append("case WHEN B.CHANNELID='"+channelId+"' THEN " +
                                  "                    CASE WHEN A.R2_STATUS = '0'  THEN 0 " +
                                  "                    WHEN  A.R2_STATUS is null AND A.R2_STATUS = '0'  THEN 0 ELSE 100 END " +
                                  "                ELSE b.shelfstatus                " +
                                  "                END shelfstatus," );
        }
        else
        {
            //不改之前逻辑，前端传全部或空的
            sqlbuf.append("b.shelfstatus," );
        }

        sqlbuf.append("b.channelname from (");
        sqlbuf.append(" select A.*,B.PRICE MINPRICE,B.PRICE MAXPRICE,BL.DISPLAYNAME as PLU_NAME,C.CLASSNO,CL2.CLASSNAME,CL.DISPLAYNAME,P.LISTIMAGE ");

        if (shopId != null && !shopId.isEmpty())
        {
            sqlbuf.append(",R2.STATUS R2_STATUS ");
        }

        if (channelId != null && !channelId.isEmpty())
        {
            sqlbuf.append(",R1.STATUS R1_STATUS ");
        }

        sqlbuf.append(" from DCP_GOODS_ONLINE A ");
        sqlbuf.append(" left join DCP_GOODS B on A.EID=B.EID and A.PLUNO=B.PLUNO and A.PLUTYPE=B.PLUTYPE ");
        // sqlbuf.append(" left join DCP_GOODS_LANG BL on A.EID=BL.EID and
        // B.PLUNO=BL.PLUNO and BL.LANG_TYPE='"+langType+"' ");
        sqlbuf.append(" left join DCP_GOODS_ONLINE_LANG BL on A.EID=BL.EID and B.PLUNO=BL.PLUNO and BL.LANG_TYPE='"+langType+"'");
        sqlbuf.append(" left join DCP_CLASS_GOODS C ON A.EID=C.EID and A.PLUNO=C.PLUNO AND C.CLASSTYPE='"+classType+"' ");
        sqlbuf.append(" left join DCP_CLASS_GOODS_LANG CL ON A.EID=CL.EID and C.CLASSNO=CL.CLASSNO and C.PLUNO=CL.PLUNO AND CL.CLASSTYPE='"+classType+"' ");
        sqlbuf.append(" LEFT JOIN Dcp_Class_Lang CL2 ON C.EID=CL2.EID AND C.CLASSNO=CL2.CLASSNO AND C.CLASSTYPE=CL2.CLASSTYPE AND CL2.Lang_Type='"+langType+"'" );
        // sqlbuf.append(" left join DCP_GOODS_OFFSHELF D ON A.EID=D.EID and
        // A.PLUNO=D.PLUNO and A.PLUTYPE=D.PLUTYPE ");
        // sqlbuf.append(" inner join goodstemplate on A.EID=goodstemplate.EID
        // and A.PLUNO=goodstemplate.PLUNO");
        // sqlbuf.append(" left join DCP_GOODS_OFFSHELF_RANGE R ON A.EID=R.EID
        // and A.PLUNO=R.PLUNO and A.PLUTYPE=R.PLUTYPE");
        sqlbuf.append(" left join Dcp_Goodsimage P ON  A.EID=P.EID and A.PLUNO=P.PLUNO and A.PLUTYPE=P.PLUTYPE AND P.APPTYPE='ALL'");
        if (channelId != null && !channelId.isEmpty()) {
            // 如果传入channelId不为空，则
            // 显示的商品必须是在DCP_GOODS_SHELF_RANGE中渠道=当前传入渠道、SHOP=ALL且状态为上架【status=100】的商品。
            sqlbuf.append(" inner join DCP_GOODS_SHELF_RANGE R1 ON A.EID=R1.EID and A.PLUNO=R1.PLUNO and A.PLUTYPE=R1.PLUTYPE and "
                                  + " R1.SHOPID='ALL'" );

            sqlbuf.append(" and R1.CHANNELID='" + channelId + "' ");
        }
        if (shopId != null && !shopId.isEmpty()) {
            // 不存在与 DCP_GOODS_SHELF_RANGE中 传入渠道 传入门店 且状态为已下架商品中【staus=0】
            sqlbuf.append(" left join DCP_GOODS_SHELF_RANGE R2 ON A.EID=R2.EID and A.PLUNO=R2.PLUNO and A.PLUTYPE=R2.PLUTYPE ");

            sqlbuf.append(" and R2.SHOPID='" + shopId + "' ");

            if (channelId != null && !channelId.isEmpty()) {
                sqlbuf.append(" and R2.CHANNELID='" + channelId + "' ");
            }
        }

        // 这TM瞎搞
        if (!Check.Null(companyId)) {
            // 不存在与 DCP_GOODS_SHELF_RANGE中 传入渠道 传入门店 且状态为已下架商品中【staus=0】
            sqlbuf.append(" left join DCP_GOODS_SHELF_RANGE R3 ON A.EID=R3.EID and A.PLUNO=R3.PLUNO and A.PLUTYPE=R3.PLUTYPE and R3.STATUS='0' "
                                  + " and R3.ORGTYPE='1' and R3.ORGID = '" + companyId + "' ");
        }

        sqlbuf.append(" where A.EID='" + eId + "'  and a.status = '100'  and A.PLUTYPE in ('NORMAL','FEATURE','PACKAGE') ");

        if (shopId != null && !shopId.isEmpty()) {
            // 不存在与 DCP_GOODS_SHELF_RANGE中 传入渠道 传入门店 且状态为已下架商品中【staus=0】
            if (status!=null && status.length()>0)
            {
                if (status.equals("100"))
                {
                    sqlbuf.append(" and (R2.status='100' or  (R2.status is null and R1.status='100') )");
                }
                else
                {
                    //下架状态
                    sqlbuf.append(" and (R2.status='0' or  (R2.status is null and R1.status='0') )");
                }
            }
        }


        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (A.pluNO='" + keyTxt + "' OR BL.DISPLAYNAME like '%" + keyTxt + "%' "
                                  + " or c.classNO = '" + keyTxt + "' or cl.DISPLAYNAME like '%" + keyTxt + "%' ) ");
        }

        if (!Check.Null(classNo)) {
            sqlbuf.append(" and c.classNo = '" + classNo + "' ");
        }

        sqlbuf.append(" and B.PRICE >= " + minPrice + "  and  B.price <= " + maxPrice + " ");

        sqlbuf.append(" Union All");

        //【ID1026399】//【半桶水3.0】商城商品设置 不显示销售分组 by jinzma CL.CLASSNO ---> C.CLASSNO
        sqlbuf.append(" select A.*,B.MINPRICE,B.MAXPRICE,BL.MASTERPLUNAME as PLU_NAME,C.CLASSNO,CL2.CLASSNAME,CL.DISPLAYNAME,P.LISTIMAGE ");
        if (shopId != null && !shopId.isEmpty())
        {
            sqlbuf.append(",R2.STATUS R2_STATUS ");
        }

        if (channelId != null && !channelId.isEmpty())
        {
            sqlbuf.append(",R1.STATUS R1_STATUS ");
        }

        sqlbuf.append(" from DCP_GOODS_ONLINE A");
        sqlbuf.append(" left join  DCP_MSPECGOODS B on A.EID=B.EID and A.PLUNO=B.MASTERPLUNO ");
        sqlbuf.append(" left join  DCP_MSPECGOODS_LANG BL on A.EID=BL.EID and B.MASTERPLUNO=BL.MASTERPLUNO and BL.LANG_TYPE='"+langType+ "'");
        sqlbuf.append(" left join  DCP_CLASS_GOODS C ON A.EID=C.EID and A.PLUNO=C.PLUNO AND C.CLASSTYPE='"+classType+"' ");
        sqlbuf.append(" left join  DCP_CLASS_GOODS_LANG CL ON A.EID=CL.EID and C.CLASSNO=CL.CLASSNO and C.PLUNO=CL.PLUNO and CL.CLASSTYPE='"+classType+"' ");
        sqlbuf.append(" LEFT JOIN Dcp_Class_Lang CL2 ON C.EID = CL2.EID AND C.CLASSNO = CL2.CLASSNO AND C.CLASSTYPE=CL2.CLASSTYPE AND CL2.Lang_Type= '"+langType+"' " );

        //【ID1026399】//【半桶水3.0】商城商品设置 不显示销售分组
        //DCP_MSPECGOODS_SUBGOODS 这个表关联无意义，导致资料冗余 by jinzma 20220621
        //sqlbuf.append(" left join  DCP_MSPECGOODS_SUBGOODS sub on A.EID=sub.EID and A.PLUNO=sub.MASTERPLUNO ");


        // sqlbuf.append(" left join DCP_GOODS_OFFSHELF D ON A.EID=D.EID and
        // A.PLUNO=D.PLUNO and A.PLUTYPE=D.PLUTYPE ");
        // sqlbuf.append(" left join DCP_GOODS_OFFSHELF_RANGE R ON A.EID=R.EID
        // and A.PLUNO=R.PLUNO and A.PLUTYPE=R.PLUTYPE");
        // sqlbuf.append(" inner join goodstemplate on A.EID=goodstemplate.EID
        // and sub.PLUNO=goodstemplate.PLUNO ");
        sqlbuf.append(" left join Dcp_Goodsimage P ON  A.EID=P.EID and A.PLUNO=P.PLUNO and A.PLUTYPE=P.PLUTYPE AND P.APPTYPE='ALL'");

        if (channelId != null && !channelId.isEmpty()) {
            // 如果传入channelId不为空，则
            // 显示的商品必须是在DCP_GOODS_SHELF_RANGE中渠道=当前传入渠道、SHOP=ALL且状态为上架【status=100】的商品。
            sqlbuf.append(" inner join DCP_GOODS_SHELF_RANGE R1 ON A.EID=R1.EID and A.PLUNO=R1.PLUNO and A.PLUTYPE=R1.PLUTYPE "
                                  + " and R1.SHOPID='ALL' ");

            sqlbuf.append(" and R1.CHANNELID='" + channelId + "' ");

        }

        if (shopId != null && !shopId.isEmpty()) {
            // 不存在与 DCP_GOODS_SHELF_RANGE中 传入渠道 传入门店 且状态为已下架商品中【staus=0】
            sqlbuf.append(" left join DCP_GOODS_SHELF_RANGE R2 ON A.EID=R2.EID and A.PLUNO=R2.PLUNO and A.PLUTYPE=R2.PLUTYPE ");

            sqlbuf.append(" and R2.SHOPID='" + shopId + "' ");

            if (channelId != null && !channelId.isEmpty()) {
                sqlbuf.append(" and R2.CHANNELID='" + channelId + "' ");
            }
        }

        sqlbuf.append(" where A.EID='" + eId + "' and a.status = '100'  and A.PLUTYPE='MULTISPEC' ");

        if (shopId != null && !shopId.isEmpty()) {
            // 不存在与 DCP_GOODS_SHELF_RANGE中 传入渠道 传入门店 且状态为已下架商品中【staus=0】
            if (status!=null && status.length()>0)
            {
                if (status.equals("100"))
                {
                    sqlbuf.append(" and (R2.status='100' or  (R2.status is null and R1.status='100') )");
                }
                else
                {
                    //下架状态
                    sqlbuf.append(" and (R2.status='0' or  (R2.status is null and R1.status='0') )");
                }
            }
        }

        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (A.pluNO='" + keyTxt + "' OR BL.MASTERPLUNAME like '%" + keyTxt + "%' "
                                  + " or c.classNO='" + keyTxt + "' or cl.DISPLAYNAME like '%" + keyTxt + "%' ) ");
        }

        if (!Check.Null(classNo)) {
            sqlbuf.append(" and c.classNo = '" + classNo + "' ");
        }

        sqlbuf.append(" and ((b.MINPRICE >=" + minPrice + " and " + minPrice + "<= b.MaxPrice) "
                              + " or (b.minPrice <=" + maxPrice + " and " + maxPrice + "<= b.MaxPrice )  ) ");

        sqlbuf.append(") A");

        //【ID1027353】 [荷家3.0]商城商品查询界面增加渠道展示-DCP服务  by jinzma 20220720
        //sqlbuf.append(" left join dcp_goods_shelf_range b on a.eid=b.eid and a.pluno=b.pluno and b.shopid='ALL'");
        //sqlbuf.append(" left join crm_channel c on a.eid=c.eid and b.channelid=c.channelid");
        sqlbuf.append(""
                              + " left join ("
                              + " select a.eid,a.channelid,a.channelname,nvl(c.status,'0') as shelfstatus,c.pluno from crm_channel a"
                              + " inner join platform_app b on a.appno=b.appno and b.isonline='Y' and b.isthird='N' and b.status='100'"
                              + " left join dcp_goods_shelf_range c on a.eid=c.eid and c.shopid='ALL' and a.channelid=c.channelid"
                              + " ) b on a.eid=b.eid and a.pluno=b.pluno"
                              + " ");
        sqlbuf.append(" ) where rn>" + startRow + " and rn<=" + (startRow + pageSize)
                              + " order by rn,channelid");

        return sqlbuf.toString();

    }

}
