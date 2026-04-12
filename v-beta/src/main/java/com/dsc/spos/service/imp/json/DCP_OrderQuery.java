package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.json.cust.req.DCP_OrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.reflect.TypeToken;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 查询订单
 *
 */
public class DCP_OrderQuery extends SPosBasicService<DCP_OrderQueryReq, DCP_OrderQueryRes>
{

    @Override
    protected boolean isVerifyFail(DCP_OrderQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        /*if (Check.Null(req.getRequest().geteId()))
        {
            errCt++;
            errMsg.append("企业编号eId不可为空值, ");
            isFail = true;
        }*/

        //默认干成Y，不管你是不是有参数，不传N 就干成Y
        if (Check.Null(req.getRequest().getSortStatus()) || "N".equals(req.getRequest().getSortStatus())==false)
        {
            req.getRequest().setSortStatus("Y");
        }

        //如果没传这个节点,走之前逻辑
        boolean isReqOrderNo = false;
        if (!Check.Null(req.getRequest().getOrderNo()))
        {
            isReqOrderNo = true;
        }
        if (req.getRequest().getOrderList()!=null&&req.getRequest().getOrderList().size()>0)
        {
            isReqOrderNo = true;
            if (req.getRequest().getOrderList().size()>20)
            {
                errCt++;
                //errMsg.append("组织类型orgType不可为空值, ");
                isFail = true;
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单号已超过最大数量20个,请减少请求的单号数量");
            }
        }


        if (!isReqOrderNo)
        {
            if (Check.Null(req.getRequest().getOrgType()))
            {
                req.getRequest().setOrgType("");//防止后面为null，报错
               /* errCt++;
                errMsg.append("组织类型orgType不可为空值, ");
                isFail = true;*/
            }

            if (Check.Null(req.getRequest().getOrgNo()))
            {
                req.getRequest().setOrgNo("");//防止后面为null，报错
                /*errCt++;
                errMsg.append("组织编号orgNo不可为空值, ");
                isFail = true;*/
            }

            if (Check.Null(req.getRequest().getSortMode()))
            {
                errCt++;
                errMsg.append("时间查询分类sortMode不可为空值, ");
                isFail = true;
            }

            if (Check.Null(req.getRequest().getSortType()))
            {
                errCt++;
                errMsg.append("时间查询类型sortType不可为空值, ");
                isFail = true;
            }

            if (Check.Null(req.getRequest().getBeginDate()))
            {
                errCt++;
                errMsg.append("开始日期beginDate不可为空值, ");
                isFail = true;
            }

            if (Check.Null(req.getRequest().getEndDate()))
            {
                errCt++;
                errMsg.append("截止日期endDate不可为空值, ");
                isFail = true;
            }

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderQueryReq> getRequestType()
    {
        return new TypeToken<DCP_OrderQueryReq>(){};
    }

    @Override
    protected DCP_OrderQueryRes getResponseType()
    {
        return new DCP_OrderQueryRes();
    }

    @Override
    protected DCP_OrderQueryRes processJson(DCP_OrderQueryReq req) throws Exception
    {
        long d_s = System.currentTimeMillis();
        //HelpTools.writelog_fileName("查询【开始】时间:"+d_s,"DCP_OrderQuery");
        String eId=req.getRequest().geteId();
        if (Check.Null(eId))
        {
            eId = req.geteId();
            if (Check.Null(req.geteId()))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业编码eId不能为空值！");
            }
            else
            {
                req.getRequest().seteId(req.geteId());
            }
        }

        DCP_OrderQueryRes res=this.getResponse();

        int totalRecords=0;								//总笔数
        int totalPages=0;

        try
        {
            //处理==绑定变量SQL的写法
            List<DataValue> lstDV=new ArrayList<>();
            DataValue dv=null;

            //先查单头
            String sqlOrder=getQuerySql_Header(req,lstDV);
            List<String> lstDV_list = new ArrayList<String>();
            for (int i = 0; i < lstDV.size(); i++)
            {
                lstDV_list.add(lstDV.get(i).getValue().toString());
            }
            String[] conditionValues = new String[lstDV_list.size()];
            lstDV_list.toArray(conditionValues);
            try {
                HelpTools.writelog_fileName("订单查询请求sql:"+sqlOrder+"\r\n入参:"+lstDV_list.toString(),"DCP_OrderQuery");
            }
            catch (Exception e)
            {

            }
            List<Map<String, Object>> getOrder = this.doQueryData(sqlOrder, conditionValues);
            //List<Map<String, Object>> getOrder = this.executeQuerySQL_BindSQL(sqlOrder, lstDV);

            res.setDatas(res.new level1Elm());
            res.getDatas().setOrderList(new ArrayList<DCP_OrderQueryRes.orders>());

            if(getOrder!=null && getOrder.isEmpty()==false)
            {
                String num = getOrder.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);

                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                StringBuffer sJoinOrderno=new StringBuffer("");
                StringBuffer sJoinEid=new StringBuffer("");
                StringBuffer sJoinMachshop=new StringBuffer("");
                for (Map<String, Object> tempmap : getOrder)
                {
                    sJoinOrderno.append(tempmap.get("ORDERNO").toString()+",");
                    sJoinEid.append(tempmap.get("EID").toString()+",");
                    sJoinMachshop.append(tempmap.get("MACHSHOP").toString()+",");
                }

                //
                Map<String, String> mapOrder=new HashMap<String, String>();
                mapOrder.put("MACHSHOP", sJoinMachshop.toString());
                mapOrder.put("ORDERNO", sJoinOrderno.toString());
                mapOrder.put("EID", sJoinEid.toString());
                //
                MyCommon cm=new MyCommon();
                String withasSql_Orderno=cm.getFormatSourceMultiColWith(mapOrder);
                mapOrder=null;
                mapOrder=null;

                if (withasSql_Orderno.equals(""))
                {
                    res.setSuccess(false);
                    res.setServiceDescription("订单查询失败--单号转换成临时表的方法处理失败！");
                    return res;
                }

                //********这里拆开查询，因为存在商品300笔*折扣300笔*付款300笔这种单据，一次性返回10W笔数据就要很久，超时*******

                //商品明细、备注
                String sql_detail=getQuerySql_Detail(withasSql_Orderno,req);
                List<Map<String, Object>> getMulti = this.doQueryData(sql_detail, null);

                //折扣
                String sql_detail_agio=getQuerySql_Detail_Agio(withasSql_Orderno,req);
                List<Map<String, Object>> getMulti_agio = this.doQueryData(sql_detail_agio, null);

                //付款
                String sql_detail_pay=getQuerySql_Detail_Pay(withasSql_Orderno,req);
                List<Map<String, Object>> getMulti_pay = this.doQueryData(sql_detail_pay, null);

                res.getDatas().getOrderList().addAll(DealOrders(req,getOrder,getMulti,getMulti_agio,getMulti_pay));

            }


        }
        catch (Exception ex)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                ex.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                PosPub.WriteETLJOBLog("订单查询异常DCP_OrderQuery： "+ errors.toString());

                pw=null;
                errors=null;
            }
            catch (Exception e)
            {

            }
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalPages(totalPages);
        res.setTotalRecords(totalRecords);
        long d_e = System.currentTimeMillis();
        long d_swpn = d_e-d_s;
        HelpTools.writelog_fileName("查询【结束】总耗时:"+d_swpn,"DCP_OrderQuery");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {

    }

    @Override
    protected String getQuerySql(DCP_OrderQueryReq req) throws Exception
    {
        return  null;
    }
    /**
     * 只查询单头
     */
    protected String getQuerySql_Header(DCP_OrderQueryReq req,List<DataValue> lstDV) throws Exception
    {
        String eId=req.getRequest().geteId();
        String ketTxt=req.getRequest().getKeyTxt();
        String orgNo=req.getRequest().getOrgNo();
        String orgType=req.getRequest().getOrgType();
        String shopNo=req.getRequest().getShopNo();
        String machShopNo=req.getRequest().getMachShopNo();
        String shippingShopNo=req.getRequest().getShippingShopNo();

        String sortMode=req.getRequest().getSortMode();
        String sortType=req.getRequest().getSortType();
        String beginDate=req.getRequest().getBeginDate();
        String endDate=req.getRequest().getEndDate();
        String isInvoice=req.getRequest().getIsInvoice();
        String invOperateType=req.getRequest().getInvOperateType();
        String proName=req.getRequest().getProName();
        String pickUpDocPrint=req.getRequest().getPickUpDocPrint();
        String orderPrint=req.getRequest().getOrderPrint();
        String sortStatus=req.getRequest().getSortStatus();
        String[] loadDocType=req.getRequest().getLoadDocType();
        String[] channelId=req.getRequest().getChannelId();
        String[] shipType=req.getRequest().getShipType();
        String[] status=req.getRequest().getStatus();
        String[] refundStatus=req.getRequest().getRefundStatus();
        String[] deliveryStatus=req.getRequest().getDeliveryStatus();
        String[] deliveryType=req.getRequest().getDeliveryType();
        String[] payStatus=req.getRequest().getPayStatus();
        String[] productStatus=req.getRequest().getProductStatus();

        String delId = req.getRequest().getDelId();
        String address = req.getRequest().getAddress();
        String orderNo = req.getRequest().getOrderNo();
        String sn = req.getRequest().getSn();
        String county = req.getRequest().getCounty();
        String memberId = req.getRequest().getMemberId();
        String localStoreProduction = req.getRequest().getLocalStoreProduction();
        String lineNo = req.getRequest().getLineNo();
        String opNo = req.getRequest().getOpNo();

        String beginTime=req.getRequest().getBeginTime();
        if (beginTime!=null&&!beginTime.isEmpty())
        {
            beginTime = beginTime.replace(":","");//防止误传
            if (beginTime.length()>=4)
            {
                beginTime = beginTime.substring(0,4);
                if ("0000".equals(beginTime))
                {
                    beginTime = "";//这种无需增加条件
                }
            }
            else
            {
                beginTime = "";
            }
        }
        String endTime=req.getRequest().getEndTime();
        if (endTime!=null&&!endTime.isEmpty())
        {
            endTime = endTime.replace(":","");//防止误传
            if (endTime.length()>=4)
            {
                endTime = endTime.substring(0,4);
                if ("2359".equals(endTime))
                {
                    endTime = "";////这种无需增加条件
                }
            }
            else
            {
                endTime = "";
            }
        }

        boolean isPosRequest = false;//判断是不是 POS接口账号请求
        if (req.getApiUser()!=null)
        {
            if ("POS".equals(req.getApiUser().getAppType())||"POSANDROID".equals((req.getApiUser().getAppType())))
            {
                isPosRequest = true;
            }

        }
        String sql = null;

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow=(pageNumber-1) * pageSize;

        //处理==绑定变量SQL的写法
        if (lstDV == null)
        {
            lstDV=new ArrayList<>();
        }
        DataValue dv=null;

        //如果传了单号，精确查询
        if (orderNo!=null&&orderNo.trim().isEmpty()==false)
        {
            //给个默认，兼容下，否则报错
            req.setPageNumber(1);
            req.setPageSize(10);
            StringBuffer sqlbuf=new StringBuffer("select * from ( "
                    + "select count(*) over() num, rownum rn,a.* from ( "
                    + "select a.*,b.APPNAME,c.CHANNELNAME,d.SUPPORTINVOICE,g1.BELFIRM as BELFIRM_SHOP,g2.BELFIRM as BELFIRM_SHIPPINGSHOP,g3.BELFIRM as BELFIRM_MACHSHOP,g4.OP_NAME,nvl(credit.returnamt,0) CREDIT_RETURNAMT from "
                    + "dcp_order a "
                    + "left join PLATFORM_APP b on a.loaddoctype=b.appno and b.status='100' "
                    + "left join CRM_CHANNEL c on a.channelid=c.channelid and a.eid=c.eid and c.status='100' "
                    + "left join Dcp_Ecommerce d on a.eid=d.eid and a.loaddoctype=d.loaddoctype and a.channelid=d.channelid "
                    + "left join dcp_org g1 on g1.eid=a.eid and g1.ORGANIZATIONNO=a.shop "
                    + "left join dcp_org g2 on g2.eid=a.eid and g2.ORGANIZATIONNO=a.shippingshop "
                    + "left join dcp_org g3 on g3.eid=a.eid and g3.ORGANIZATIONNO=a.machshop "
                    + "left join platform_staffs_lang g4 on g4.eid=a.eid and g4.OPNO=a.OPNO and g4.LANG_TYPE='zh_CN' "
                    + "left join DCP_CUSTOMER_CREDIT_DETAIL credit on a.eid=credit.eid and a.shop=credit.shopid and a.orderno=credit.sourceno and (credit.sourcetype=3 or credit.sourcetype=4) "
                    + "where a.eid=? ");
            sqlbuf.append( "and a.orderno=? ");
            sqlbuf.append(" ) a ) where rn>0 and rn<=10 ");

            //?问号参数赋值处理
            dv=new DataValue(eId, Types.VARCHAR);
            lstDV.add(dv);
            dv=new DataValue(orderNo, Types.VARCHAR);
            lstDV.add(dv);
            sql = sqlbuf.toString();
            return sql;
        }
        List<DCP_OrderQueryReq.levelOrder> orderList = req.getRequest().getOrderList();
        if (orderList!=null&&!orderList.isEmpty())
        {
            String orderList_sqlCondition = "";
            for (DCP_OrderQueryReq.levelOrder par_order : orderList)
            {
                String orderId = par_order.getOrderNo();
                if (orderId==null||orderId.trim().isEmpty())
                {
                    continue;
                }
                orderList_sqlCondition += "'"+orderId+"'"+",";
            }
            if (!orderList_sqlCondition.isEmpty())
            {
                orderList_sqlCondition = orderList_sqlCondition.substring(0,orderList_sqlCondition.length()-1);
                //给个默认，兼容下，否则报错
                req.setPageNumber(1);
                req.setPageSize(20);
                StringBuffer sqlbuf=new StringBuffer("select * from ( "
                        + "select count(*) over() num, rownum rn,a.* from ( "
                        + "select a.*,b.APPNAME,c.CHANNELNAME,d.SUPPORTINVOICE,g1.BELFIRM as BELFIRM_SHOP,g2.BELFIRM as BELFIRM_SHIPPINGSHOP,g3.BELFIRM as BELFIRM_MACHSHOP,g4.OP_NAME,nvl(credit.returnamt,0) CREDIT_RETURNAMT from "
                        + "dcp_order a "
                        + "left join PLATFORM_APP b on a.loaddoctype=b.appno and b.status='100' "
                        + "left join CRM_CHANNEL c on a.channelid=c.channelid and a.eid=c.eid and c.status='100' "
                        + "left join Dcp_Ecommerce d on a.eid=d.eid and a.loaddoctype=d.loaddoctype and a.channelid=d.channelid "
                        + "left join dcp_org g1 on g1.eid=a.eid and g1.ORGANIZATIONNO=a.shop "
                        + "left join dcp_org g2 on g2.eid=a.eid and g2.ORGANIZATIONNO=a.shippingshop "
                        + "left join dcp_org g3 on g3.eid=a.eid and g3.ORGANIZATIONNO=a.machshop "
                        + "left join platform_staffs_lang g4 on g4.eid=a.eid and g4.OPNO=a.OPNO and g4.LANG_TYPE='zh_CN' "
                        + "left join DCP_CUSTOMER_CREDIT_DETAIL credit on a.eid=credit.eid and a.shop=credit.shopid and a.orderno=credit.sourceno and (credit.sourcetype=3 or credit.sourcetype=4) "
                        + "where a.eid=? ");
                sqlbuf.append( "and a.orderno in ("+orderList_sqlCondition+")");
                sqlbuf.append(" ) a ) where rn>0 and rn<=20 ");

                //?问号参数赋值处理
                dv=new DataValue(eId, Types.VARCHAR);
                lstDV.add(dv);
                sql = sqlbuf.toString();
                return sql;
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的单号列表OrderList都是空值！");
            }
        }

        boolean isJoinGoodsDetail = false;//是不是关联商品明细
        String category_sqlCondition = "";
        List<DCP_OrderQueryReq.levelCategory> categoryList = req.getRequest().getCategoryList();
        if (categoryList!=null&&!categoryList.isEmpty())
        {
            for (DCP_OrderQueryReq.levelCategory par_category : categoryList)
            {
                String categoryId = par_category.getCategoryId();
                if (categoryId==null||categoryId.trim().isEmpty())
                {
                    continue;
                }
                category_sqlCondition += "'"+categoryId+"'"+",";
            }
            if (!category_sqlCondition.isEmpty())
            {
                category_sqlCondition = category_sqlCondition.substring(0,category_sqlCondition.length()-1);
            }
        }

        if (!category_sqlCondition.trim().isEmpty())
        {
            isJoinGoodsDetail = true;
        }


        //没有传单号，走之前逻辑
        StringBuffer sqlbuf=new StringBuffer("select * from ( "
                + "select count(*) over() num, rownum rn,a.* from ( ");
        if (isJoinGoodsDetail)
        {
            sqlbuf.append("select distinct eid,orderno,status,create_datetime,shipdate2,shipstarttime2 from ( ");
        }
        sqlbuf.append("select a.*,"
                + "NVL(a.shipdate,'19700101') shipdate2,NVL(a.shipstarttime,'000000') shipstarttime2,"
                + "b.APPNAME,c.CHANNELNAME,d.SUPPORTINVOICE,g1.BELFIRM as BELFIRM_SHOP,g2.BELFIRM as BELFIRM_SHIPPINGSHOP,g3.BELFIRM as BELFIRM_MACHSHOP,g4.OP_NAME,nvl(credit.returnamt,0) CREDIT_RETURNAMT from "
                + "dcp_order a "
                + "left join PLATFORM_APP b on a.loaddoctype=b.appno and b.status='100' "
                + "left join CRM_CHANNEL c on a.channelid=c.channelid and a.eid=c.eid and c.status='100' "
                + "left join Dcp_Ecommerce d on a.eid=d.eid and a.loaddoctype=d.loaddoctype and a.channelid=d.channelid "
                + "left join dcp_org g1 on g1.eid=a.eid and g1.ORGANIZATIONNO=a.shop "
                + "left join dcp_org g2 on g2.eid=a.eid and g2.ORGANIZATIONNO=a.shippingshop "
                + "left join dcp_org g3 on g3.eid=a.eid and g3.ORGANIZATIONNO=a.machshop "
                + "left join platform_staffs_lang g4 on g4.eid=a.eid and g4.OPNO=a.OPNO and g4.LANG_TYPE='zh_CN' "
                + "left join DCP_CUSTOMER_CREDIT_DETAIL credit on a.eid=credit.eid and a.shop=credit.shopid and a.orderno=credit.sourceno and (credit.sourcetype=3 or credit.sourcetype=4) "
                );
        if (isJoinGoodsDetail)
        {
            sqlbuf.append("left join dcp_order_detail dd on a.eid=dd.eid and a.orderno=dd.orderno ");
            sqlbuf.append("left join dcp_goods gg on gg.eid=dd.eid and gg.pluno=dd.pluno ");
        }

        sqlbuf.append("where a.eid=? ");
        //?问号参数赋值处理
        dv=new DataValue(eId, Types.VARCHAR);
        lstDV.add(dv);
        sqlbuf.append("and a.billtype='1' and ((nvl(a.headorderno,'null')='null' or a.headorderno='') or (a.loaddoctype='"+orderLoadDocType.MINI+"')) ");

        if (isJoinGoodsDetail)
        {
            sqlbuf.append("and gg.category in ("+category_sqlCondition+") " );
        }


        if (orgType.equals("0"))//公司
        {
            //sqlbuf.append("and (a.BELFIRM='"+orgNo+"' or a.BELFIRM is null or a.BELFIRM='') ");
            sqlbuf.append("and (a.BELFIRM=? "
                    +" or a.shop=? or a.MACHSHOP=? or a.SHIPPINGSHOP=? "
                    +" or g1.BELFIRM=? or g2.BELFIRM=? or g3.BELFIRM=? "
                    +" or a.BELFIRM is null or a.BELFIRM='') ");

            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);

        }
        else if (orgType.equals("1"))//组织
        {
            sqlbuf.append("and (a.shop=? or a.MACHSHOP=? or a.SHIPPINGSHOP=? ) ");
            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);
        }
        else if (orgType.equals("2"))//门店
        {
            ////在门店视角订单中心,如果这个入参为Y，则只查询，本门店是生产门店的订单
            if ("Y".equals(localStoreProduction))
            {
                sqlbuf.append("and  a.MACHSHOP=? ");
                //?问号参数赋值处理
                dv=new DataValue(orgNo, Types.VARCHAR);
                lstDV.add(dv);
            }
            else
            {
                sqlbuf.append("and (a.shop=? or a.MACHSHOP=? or a.SHIPPINGSHOP=? ) ");
                //?问号参数赋值处理
                dv=new DataValue(orgNo, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue(orgNo, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue(orgNo, Types.VARCHAR);
                lstDV.add(dv);
            }

        }
        else if (orgType.equals("3"))//配送中心
        {
            sqlbuf.append("and a.SHIPPINGSHOP=? ");
            //?问号参数赋值处理
            dv=new DataValue(orgNo, Types.VARCHAR);
            lstDV.add(dv);
        }


        //【ID1036816】【甜星3310】自营外卖小程序未支付的不要传中台、门店
        //如果是POS请求，过滤下自营外卖未支付的订单
        if (isPosRequest)
        {
            sqlbuf.append(" and ( a.paystatus='3' or a.loaddoctype<>'WAIMAI') ");
        }

        //下单人编码和名称
        if (opNo != null && opNo.trim().isEmpty() == false)
        {
            sqlbuf.append("and (a.opNo=? or g4.op_name=?) ");
            //?问号参数赋值处理
            dv=new DataValue(opNo, Types.VARCHAR);
            lstDV.add(dv);

            dv=new DataValue(opNo, Types.VARCHAR);
            lstDV.add(dv);
        }

        //配送员id
        if (memberId != null && memberId.trim().isEmpty() == false)
        {
            sqlbuf.append("and a.MEMBERID=? ");
            //?问号参数赋值处理
            dv=new DataValue(memberId, Types.VARCHAR);
            lstDV.add(dv);
        }

        //配送员id
        if (sn != null && sn.trim().isEmpty() == false)
        {
            sqlbuf.append("and a.ORDER_SN=? ");
            //?问号参数赋值处理
            dv=new DataValue(sn, Types.VARCHAR);
            lstDV.add(dv);
        }

        //配送员id
        if (delId != null && delId.trim().isEmpty() == false)
        {
            sqlbuf.append("and a.DELID=? ");
            //?问号参数赋值处理
            dv=new DataValue(delId, Types.VARCHAR);
            lstDV.add(dv);
        }

        //address配送地址模糊查询（支持PROVINCE、CITY、COUNTY、STREET、ADDRESS）
        if (address != null && address.trim().isEmpty() == false)
        {
            sqlbuf.append("and (a.ADDRESS like ? or a.PROVINCE like ? or a.CITY like ? or a.COUNTY like ? or a.STREET like ?) ");
            //?问号参数赋值处理
            dv=new DataValue("%"+address+"%", Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue("%"+address+"%", Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue("%"+address+"%", Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue("%"+address+"%", Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue("%"+address+"%", Types.VARCHAR);
            lstDV.add(dv);

        }

        //增加区县筛选条件，【ID1034409】黛慕食品 3.0
        if (county != null && county.trim().isEmpty() == false)
        {
            sqlbuf.append("and a.COUNTY=? ");
            //?问号参数赋值处理
            dv=new DataValue(county.trim(), Types.VARCHAR);
            lstDV.add(dv);
        }

        //订单号、手工单号、电话、会员编号、会员名称、会员卡号、门店
        if(ketTxt != null && ketTxt.length() >0)
        {
            String newKeyTxt = ketTxt.trim().replace("，",",");
            String[] ss = newKeyTxt.split(",");
            if (ss.length>1)
            {
                String sqlCon_key = "";
                for (int i=0; i<ss.length;i++)
                {
                    String keyStr = ss[i].trim();
                    if (keyStr.isEmpty())
                    {
                        continue;
                    }

                    if (sqlCon_key.isEmpty())
                    {
                        sqlCon_key = " a.orderno like ? or a.MANUALNO like ? ";
                    }
                    else
                    {
                        sqlCon_key += " or a.orderno like ? or a.MANUALNO like ? ";
                    }
                    //?问号参数赋值处理
                    dv=new DataValue("%"+keyStr+"%", Types.VARCHAR);
                    lstDV.add(dv);
                    //?问号参数赋值处理
                    dv=new DataValue("%"+keyStr+"%", Types.VARCHAR);
                    lstDV.add(dv);
                }
                sqlbuf.append(" and ("+sqlCon_key+")");

            }
            else
            {
                sqlbuf.append("and (a.orderno like ? or a.MANUALNO like ? or a.CONTTEL like ? or a.GETMANTEL like ? or a.MEMBERID like ? or a.MEMBERNAME like ? or a.CARDNO like ? or a.SHOP like ? or a.SHOPNAME like ? or a.MACHSHOP like ? or a.MACHSHOPNAME like ? or a.SHIPPINGSHOP like ? or a.SHIPPINGSHOPNAME like ? ) ");

                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue("%"+ketTxt+"%", Types.VARCHAR);
                lstDV.add(dv);
            }

        }

        if(shopNo != null && shopNo.length() >0)
        {
            sqlbuf.append("and a.shop=? ");
            //?问号参数赋值处理
            dv=new DataValue(shopNo, Types.VARCHAR);
            lstDV.add(dv);
        }

        if(machShopNo != null && machShopNo.length() >0)
        {
            sqlbuf.append("and a.MACHSHOP=? ");
            //?问号参数赋值处理
            dv=new DataValue(machShopNo, Types.VARCHAR);
            lstDV.add(dv);
        }

        if(shippingShopNo != null && shippingShopNo.length() >0)
        {
            sqlbuf.append("and a.SHIPPINGSHOP=? ");
            //?问号参数赋值处理
            dv=new DataValue(shippingShopNo, Types.VARCHAR);
            lstDV.add(dv);
        }

        if(isInvoice != null && isInvoice.length() >0)
        {
            sqlbuf.append("and a.isinvoice=? ");
            //?问号参数赋值处理
            dv=new DataValue(isInvoice, Types.VARCHAR);
            lstDV.add(dv);
        }

        if(invOperateType != null && invOperateType.length() >0)
        {
            sqlbuf.append("and a.invoperatetype=? ");
            //?问号参数赋值处理
            dv=new DataValue(invOperateType, Types.VARCHAR);
            lstDV.add(dv);
        }
        if(pickUpDocPrint != null && pickUpDocPrint.length() >0)
        {
            sqlbuf.append("and a.PICKUPDOCPRINT=? ");
            //?问号参数赋值处理
            dv=new DataValue(pickUpDocPrint, Types.VARCHAR);
            lstDV.add(dv);
        }

        if(orderPrint != null && orderPrint.length() >0)
        {
            if (orderPrint.equals("Y"))
            {
                //sqlbuf.append("and (a.PRINTCOUNT is null or a.PRINTCOUNT<>0) ");
                sqlbuf.append("and a.PRINTCOUNT>0 ");
            }
            else
            {
                sqlbuf.append("and a.PRINTCOUNT=0 ");
            }
        }

        if(proName != null && proName.length() >0)
        {
            if (proName.equals("schedulingCount"))//待审核
            {
                sqlbuf.append(" and a.status='0' ");
            }
            else if (proName.equals("receiptCount"))//订单开立
            {
                sqlbuf.append(" and a.status='1' ");
            }
            else if (proName.equals("applyRefundCount"))//申请退单
            {
                sqlbuf.append(" and (A.Refundstatus='2' or A.Refundstatus='7' or A.Refundstatus='21' or A.Refundstatus='22') and  A.status<>'12' and A.status<>'3' ");
            }
            else if (proName.equals("proRejectCount"))//生产拒单
            {
                sqlbuf.append(" and a.refundstatus='5' ");
            }
            else if (proName.equals("errorCount"))
            {
                sqlbuf.append(" and a.exceptionstatus='Y' ");//异常订单
            }
            else if (proName.equals("rejectStockOut"))
            {
                sqlbuf.append(" and a.deliverystatus='-2' ");//拒绝出货
            }
            else if (proName.equals("unPrintCount"))//未打印
            {
                sqlbuf.append(" and a.PRINTCOUNT=0  ");
            }
            else if (proName.equals("deliveredCount"))//待配送,暂不支持
            {

            }
            else if (proName.equals("cancelCount"))//已取消,暂不支持
            {

            }
            else if (proName.equals("completeCount"))//已完成,暂不支持
            {

            }
            else if (proName.equals("dschedulingCount"))//已完成,暂不支持
            {

            }
            else if ("orderTransferCount".equalsIgnoreCase(proName))//门店视角 内部调拨
            {
                //订单状态  status =1 订单开立 或  status = 2 已接单且生产状态productStatus = 6
                //且生产门店=当前门店且配送门店不等于当前门店
                sqlbuf.append(" and (A.status='1' or A.status='2') and  A.productStatus='6' ");
                sqlbuf.append(" and A.MACHSHOP=?  and  a.SHIPPINGSHOP<>? ");
                //?问号参数赋值处理
                dv=new DataValue(orgNo, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue(orgNo, Types.VARCHAR);
                lstDV.add(dv);
            }
            else if ("orderProductionFinishedCount".equalsIgnoreCase(proName))//门店视角 待完工入库
            {
                //订单状态  status =1 订单开立 或  status = 2 已接单且生产状态productStatus = 6
                //且生产门店=当前门店
                sqlbuf.append(" and (A.status='1' or A.status='2') and  A.productStatus='4' ");
                sqlbuf.append(" and A.MACHSHOP=? ");
                //?问号参数赋值处理
                dv=new DataValue(orgNo, Types.VARCHAR);
                lstDV.add(dv);
            }
            else if ("orderNeedProductionCount".equalsIgnoreCase(proName))//门店视角 待生产
            {
                //订单状态  status =1 订单开立 或  status = 2 已接单且生产状态productStatus 为空
                //且生产门店=当前门店且配送日期=当前日期
                String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
                sqlbuf.append(" and (A.status='1' or A.status='2') and  A.productStatus is null  and  A.shipdate='"+sdate+"' ");
                sqlbuf.append(" and A.MACHSHOP=?  ");
                //?问号参数赋值处理
                dv=new DataValue(orgNo, Types.VARCHAR);
                lstDV.add(dv);
            }
        }

        if(loadDocType != null && loadDocType.length >0)
        {
            String temp_loadDocType=PosPub.getArrayStrSQLIn(loadDocType);
            sqlbuf.append("and a.loaddoctype in ("+temp_loadDocType+") ");
        }

        if(channelId != null && channelId.length >0)
        {
            String temp_channelId=PosPub.getArrayStrSQLIn(channelId);
            sqlbuf.append("and a.channelid in ("+temp_channelId+") ");
        }

        if(shipType != null && shipType.length >0)
        {
            String temp_shipType=PosPub.getArrayStrSQLIn(shipType);
            sqlbuf.append("and a.shiptype in ("+temp_shipType+") ");
        }

        if(status != null && status.length >0)
        {
            String temp_status=PosPub.getArrayStrSQLIn(status);
            sqlbuf.append("and a.status in ("+temp_status+") ");
        }

        if(refundStatus != null && refundStatus.length >0)
        {
            String temp_refundStatus=PosPub.getArrayStrSQLIn(refundStatus);
            sqlbuf.append("and a.refundstatus in ("+temp_refundStatus+") ");
        }

        if(deliveryStatus != null && deliveryStatus.length >0)
        {
            String temp_deliveryStatus=PosPub.getArrayStrSQLIn(deliveryStatus);

            boolean contains = Arrays.asList(deliveryType).contains("0");
            if(contains){
                sqlbuf.append("and (a.deliveryStatus in ("+temp_deliveryStatus+") OR a.deliveryStatus IS NULL or a.deliveryStatus = '' )");
            }else{
                sqlbuf.append("and a.deliveryStatus in ("+temp_deliveryStatus+") ");
            }

            //sqlbuf.append("and a.deliverystatus in ("+temp_deliveryStatus+") ");
        }

        if(deliveryType != null && deliveryType.length >0)
        {
            String temp_deliveryType=PosPub.getArrayStrSQLIn(deliveryType);
            // Add 若传入物流类型 deliveryType 包含 0 ， 增加条件 物流类型为空。     ( A.DELIVERYTYPE IN ( '0', '1' ) OR A.deliveryType IS NULL  or A.deliveryType = '' )
            boolean contains = Arrays.asList(deliveryType).contains("0");
            if(contains){
                sqlbuf.append("and (a.deliverytype in ("+temp_deliveryType+") OR a.deliverytype IS NULL or a.deliveryType = '' )");
            }else{
                sqlbuf.append("and a.deliverytype in ("+temp_deliveryType+") ");
            }

        }

        if(payStatus != null && payStatus.length >0)
        {
            String temp_payStatus=PosPub.getArrayStrSQLIn(payStatus);
            sqlbuf.append("and a.paystatus in ("+temp_payStatus+") ");
        }

        if(productStatus != null && productStatus.length >0)
        {
            //入参productStatus  增加枚举值:0 表示未生产;
            //当入参productStatus 传0 的时候,只查询,生产状态为空的订单;
            boolean isHashNullProductStatus = false;//是否包含 0
            for (int i =0 ; i<productStatus.length;i++)
            {
                if ("0".equals(productStatus[i]))
                {
                    isHashNullProductStatus = true;
                    break;
                }
            }
            String temp_productStatus=PosPub.getArrayStrSQLIn(productStatus);
            if (isHashNullProductStatus)
            {
                if (productStatus.length==1)
                {
                    //如果只有一个，并且是0，不需要in
                    sqlbuf.append(" and  a.PRODUCTSTATUS is null ");
                }
                else
                {
                    sqlbuf.append(" and (a.PRODUCTSTATUS in ("+temp_productStatus+") or a.PRODUCTSTATUS is null) ");
                }

            }
            else
            {
                sqlbuf.append(" and a.PRODUCTSTATUS in ("+temp_productStatus+") ");
            }

        }

        //【ID1037555】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单打印贺卡及配送路线选择---中台修改 by jinzma 20231222
        if (!Check.Null(lineNo)) {
            sqlbuf.append(" and a.lineno='"+lineNo+"' ");
        }


        String isJoinGoodsDetail_orderbySql = "";//关联单身之后的排序

        if(Check.Null(req.getRequest().getSortStatus())||req.getRequest().getSortStatus().isEmpty()
                || req.getRequest().getSortStatus().equals("N"))
        {
            if(sortMode.equals("0"))
            {
                if(beginDate != null && beginDate.length() >0)
                {
                    //sqlbuf.append("and a.create_datetime>=? ");
                    //sqlbuf.append("and substr(a.create_datetime,1,8)>=? ");
                    sqlbuf.append("and a.bdate>=? ");
                    //?问号参数赋值处理
                    dv=new DataValue(beginDate, Types.VARCHAR);
                    lstDV.add(dv);
                }
                if (beginTime!=null&&!beginTime.isEmpty())
                {
                    sqlbuf.append("and substr(a.create_datetime,9,4)>=? ");
                    dv=new DataValue(beginTime, Types.VARCHAR);
                    lstDV.add(dv);
                }

                if(endDate != null && endDate.length() >0)
                {
                    //sqlbuf.append("and substr(a.create_datetime,1,8)<=? ");
                    sqlbuf.append("and a.bdate<=? ");
                    //?问号参数赋值处理
                    dv=new DataValue(endDate, Types.VARCHAR);
                    lstDV.add(dv);
                }
                if (endTime!=null&&!endTime.isEmpty())
                {
                    sqlbuf.append("and substr(a.create_datetime,9,4)<=? ");
                    dv=new DataValue(endTime, Types.VARCHAR);
                    lstDV.add(dv);
                }


                if(sortType.equals("0"))
                {
                    if (isJoinGoodsDetail)
                    {
                        isJoinGoodsDetail_orderbySql = " order by a.create_datetime ";//后面再排序
                    }
                    else
                    {
                        sqlbuf.append("order by a.create_datetime ");
                    }


                }
                else
                {
                    if (isJoinGoodsDetail)
                    {
                        isJoinGoodsDetail_orderbySql = " order by a.create_datetime desc";
                    }
                    else
                    {
                        sqlbuf.append("order by a.create_datetime desc ");
                    }

                }
            }
            else{
                if(beginDate != null && beginDate.length() >0)
                {
                    sqlbuf.append("and a.shipdate>=? ");

                    //?问号参数赋值处理
                    dv=new DataValue(beginDate, Types.VARCHAR);
                    lstDV.add(dv);
                }

                if (beginTime!=null&&!beginTime.isEmpty())
                {
                    sqlbuf.append("and NVL(a.shipstarttime,'000000') >=? ");
                    dv=new DataValue(beginTime+"00", Types.VARCHAR);
                    lstDV.add(dv);
                }

                if(endDate != null && endDate.length() >0)
                {
                    sqlbuf.append("and a.shipdate<=? ");

                    //?问号参数赋值处理
                    dv=new DataValue(endDate, Types.VARCHAR);
                    lstDV.add(dv);
                }
                if (endTime!=null&&!endTime.isEmpty())
                {
                    sqlbuf.append("and NVL(a.shipstarttime,'000000') <=? ");
                    dv=new DataValue(endTime+"59", Types.VARCHAR);
                    lstDV.add(dv);
                }
                if(sortType.equals("0"))
                {
                    if (isJoinGoodsDetail)
                    {
                        isJoinGoodsDetail_orderbySql = " order by shipdate2,shipstarttime2 ";
                    }
                    else
                    {
                        sqlbuf.append("order by shipdate2,shipstarttime2 ");
                    }

                }
                else
                {
                    if (isJoinGoodsDetail)
                    {
                        isJoinGoodsDetail_orderbySql = " order by shipdate2 desc,shipstarttime2 desc ";
                    }
                    else
                    {
                        sqlbuf.append("order by shipdate2 desc,shipstarttime2 desc ");
                    }

                }
            }
        }else
        {
            if(sortMode.equals("0"))
            {
                if(beginDate != null && beginDate.length() >0)
                {
                    //sqlbuf.append("and substr(a.create_datetime,1,8)>=? ");
                    sqlbuf.append("and a.bdate>=? ");
                    //?问号参数赋值处理
                    dv=new DataValue(beginDate, Types.VARCHAR);
                    lstDV.add(dv);
                }
                if (beginTime!=null&&!beginTime.isEmpty())
                {
                    sqlbuf.append("and substr(a.create_datetime,9,4)>=? ");
                    dv=new DataValue(beginTime, Types.VARCHAR);
                    lstDV.add(dv);
                }

                if(endDate != null && endDate.length() >0)
                {
                    //sqlbuf.append("and substr(a.create_datetime,1,8)<=? ");
                    sqlbuf.append("and a.bdate<=? ");
                    //?问号参数赋值处理
                    dv=new DataValue(endDate, Types.VARCHAR);
                    lstDV.add(dv);
                }

                if (endTime!=null&&!endTime.isEmpty())
                {
                    sqlbuf.append("and substr(a.create_datetime,9,4)<=? ");
                    dv=new DataValue(endTime, Types.VARCHAR);
                    lstDV.add(dv);
                }

                if(sortType.equals("0"))
                {
                    if (isJoinGoodsDetail)
                    {
                        isJoinGoodsDetail_orderbySql = "order by cast(a.status as int) asc,"
                                + " a.create_datetime ";
                    }
                    else
                    {
                        sqlbuf.append("order by cast(a.status as int) asc,"
                                + " a.create_datetime ");
                    }

                }
                else
                {
                    if (isJoinGoodsDetail)
                    {
                        isJoinGoodsDetail_orderbySql = "order by cast(a.status as int) asc,"
                                + " a.create_datetime desc ";
                    }
                    else
                    {
                        sqlbuf.append("order by cast(a.status as int) asc,"
                                + " a.create_datetime desc ");
                    }

                }
            }
            else{
                if(beginDate != null && beginDate.length() >0)
                {
                    sqlbuf.append("and a.shipdate>=? ");

                    //?问号参数赋值处理
                    dv=new DataValue(beginDate, Types.VARCHAR);
                    lstDV.add(dv);
                }
                if (beginTime!=null&&!beginTime.isEmpty())
                {
                    sqlbuf.append("and NVL(a.shipstarttime,'000000') >=? ");
                    dv=new DataValue(beginTime+"00", Types.VARCHAR);
                    lstDV.add(dv);
                }
                if(endDate != null && endDate.length() >0)
                {
                    sqlbuf.append("and a.shipdate<=? ");

                    //?问号参数赋值处理
                    dv=new DataValue(endDate, Types.VARCHAR);
                    lstDV.add(dv);
                }
                if (endTime!=null&&!endTime.isEmpty())
                {
                    sqlbuf.append("and NVL(a.shipstarttime,'000000') <=? ");
                    dv=new DataValue(endTime+"59", Types.VARCHAR);
                    lstDV.add(dv);
                }
                if(sortType.equals("0"))
                {
                    if (isJoinGoodsDetail)
                    {
                        isJoinGoodsDetail_orderbySql = "order by cast(a.status as int) asc,"
                                + "shipdate2,shipstarttime2 ";
                    }
                    else
                    {
                        sqlbuf.append("order by cast(a.status as int) asc,"
                                + "shipdate2,shipstarttime2 ");
                    }

                }
                else
                {
                    if (isJoinGoodsDetail)
                    {
                        isJoinGoodsDetail_orderbySql = "order by cast(a.status as int) asc,"
                                + " shipdate2 desc,shipstarttime2 desc ";
                    }
                    else
                    {
                        sqlbuf.append("order by cast(a.status as int) asc,"
                                + " shipdate2 desc,shipstarttime2 desc ");
                    }

                }
            }
        }

        if (isJoinGoodsDetail)
        {
            sqlbuf.append(" ) a ");
            sqlbuf.append(isJoinGoodsDetail_orderbySql);
        }
        sqlbuf.append(" ) a ) where rn>? and rn<=? ");

        //?问号参数赋值处理
        dv=new DataValue(startRow, Types.INTEGER);
        lstDV.add(dv);

        //?问号参数赋值处理
        dv=new DataValue(startRow+pageSize, Types.INTEGER);
        lstDV.add(dv);

        sql = sqlbuf.toString();

        if (isJoinGoodsDetail)
        {
            sqlbuf = new StringBuffer("");
            sqlbuf.append("with p as ( ");
            sqlbuf.append(sql);
            sqlbuf.append(" ) ");
            sqlbuf.append(" select p.num, a.*,b.APPNAME,c.CHANNELNAME,d.SUPPORTINVOICE,g1.BELFIRM as BELFIRM_SHOP,g2.BELFIRM as BELFIRM_SHIPPINGSHOP,g3.BELFIRM as BELFIRM_MACHSHOP,g4.OP_NAME,nvl(credit.returnamt,0) CREDIT_RETURNAMT from ");
            sqlbuf.append(" p ");
            sqlbuf.append(" inner join dcp_order a on a.eid=p.eid and a.orderno=p.orderno ");
            sqlbuf.append("left join PLATFORM_APP b on a.loaddoctype=b.appno and b.status='100' "
                    + "left join CRM_CHANNEL c on a.channelid=c.channelid and a.eid=c.eid and c.status='100' "
                    + "left join Dcp_Ecommerce d on a.eid=d.eid and a.loaddoctype=d.loaddoctype and a.channelid=d.channelid "
                    + "left join dcp_org g1 on g1.eid=a.eid and g1.ORGANIZATIONNO=a.shop "
                    + "left join dcp_org g2 on g2.eid=a.eid and g2.ORGANIZATIONNO=a.shippingshop "
                    + "left join dcp_org g3 on g3.eid=a.eid and g3.ORGANIZATIONNO=a.machshop "
                    + "left join platform_staffs_lang g4 on g4.eid=a.eid and g4.OPNO=a.OPNO and g4.LANG_TYPE='zh_CN' "
                    + "left join DCP_CUSTOMER_CREDIT_DETAIL credit on a.eid=credit.eid and a.shop=credit.shopid and a.orderno=credit.sourceno and (credit.sourcetype=3 or credit.sourcetype=4) "
                    );
            sqlbuf.append(isJoinGoodsDetail_orderbySql);
            sql = sqlbuf.toString();
        }

        return sql;
    }

    /**
     * 查询多笔订单的明细、折扣、付款、备注
     * @param withasSql 多笔订单
     * @return
     * @throws Exception
     */
    private String getQuerySql_Detail(String withasSql,DCP_OrderQueryReq req) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf=new StringBuffer(" with a AS ( "
                + withasSql + " ) "
                + "select b.*, ");

        if(req.getRequest().getIsQueryKitchenPrintSet() != null && req.getRequest().getIsQueryKitchenPrintSet().equals("Y"))
        {
            sqlbuf.append("NVL(H1.ISLIQUOR, H2.ISLIQUOR) ISLIQUOR, "
                    + "NVL(H1.isKdsShow, H2.isKdsShow) isKdsShow, "
                    + "NVL(H1.ISKDS_CATERING_SHOW, H2.ISKDS_CATERING_SHOW) ISKDSCATERINGSHOW, "
                    + "NVL(H1.KDS_MAX_MAKE_QTY, H2.KDS_MAX_MAKE_QTY) KDSMAXMAKEQTY, "
                    + "NVL(H1.ISQTYPRINT, H2.ISQTYPRINT) ISQTYPRINT, "
                    + "NVL(H1.isPrintReturn, H2.isPrintReturn) isPrintReturn, "
                    + "NVL(H1.isPrintCrossMenu, H2.isPrintCrossMenu) isPrintCrossMenu, "
                    + "NVL(H1.CROSSPRINTERNAME, H2.CROSSPRINTERNAME) CROSSPRINTER, "
                    + "NVL(H1.PRINTERNAME, H2.PRINTERNAME) kitchenPrinter, ");
        }
        sqlbuf.append("e.oitem E_OITEM,e.item E_ITEM,e.memotype E_MEMOTYPE,e.memoname E_MEMONAME,e.memo E_MEMO,"
                //【ID1037876】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单中心裱花间打印格式调整---服务
                //【ID1037588】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单中心裱花间打印格式调整---服务  by jinzma 20231213
                + " a1.shelflife,a1.netcontent,a1.goodstype,a1.memo as goodsmemo,a1.shelflifen, "
                + " a1.PRODUCER,a1.STORAGECON,a1.MANUFACTURER,a1.HOTLINE,a1.INGRETABLE,a1.FOODPROLICNUM,a1.EATINGMETHOD,a1.EXSTANDARD,a1.PROPADDRESS, "
                + " a2.NUTRITIONNAME,a2.NUTRITIONCONTENT,a2.REFERENCEVALUE "
                + " from a "
                + " inner join dcp_order_detail b on a.eid=b.eid and a.orderno=b.orderno "
                + " LEFT JOIN DCP_GOODS A1 ON b.eid=A1.Eid and b.pluno=A1.Pluno"
                + " LEFT JOIN DCP_GOODS_NUTRITION A2 ON b.eid=A2.Eid and b.pluno=A2.Pluno"
                 );

        if(req.getRequest().getIsQueryKitchenPrintSet() != null && req.getRequest().getIsQueryKitchenPrintSet().equals("Y"))
        {
            sqlbuf.append(" LEFT JOIN DCP_KITCHENPRINTSET H1 ON A1.EID=H1.EID AND A1.PLUNO=H1.ID AND H1.TYPE='GOODS' AND H1.SHOPID=a.MACHSHOP "
                    + " LEFT JOIN DCP_KITCHENPRINTSET H2 ON A1.EID=H2.EID AND A1.Category=H2.ID AND H2.TYPE='CATEGORY' AND H2.SHOPID=a.MACHSHOP ");
        }
        sqlbuf.append(" left join dcp_order_detail_memo e on a.eid=e.eid and a.orderno=e.orderno and b.item=e.oitem "
                + " order by b.orderno,b.item "
                + "");

        sql = sqlbuf.toString();
        return sql;
    }

    private String getQuerySql_Detail_Agio(String withasSql,DCP_OrderQueryReq req) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf=new StringBuffer(" with a AS ( "
                + withasSql + " ) "
                + "select a.eid,a.orderno, ");

        sqlbuf.append("c.MITEM C_MITEM,c.ITEM C_ITEM,c.QTY C_QTY,c.AMT C_AMT,c.INPUTDISC C_INPUTDISC, "
                + "c.REALDISC C_REALDISC,c.DISC C_DISC,c.DCTYPE C_DCTYPE,c.DCTYPENAME C_DCTYPENAME, "
                + "c.PMTNO C_PMTNO,c.GIFTCTF C_GIFTCTF,c.GIFTCTFNO C_GIFTCTFNO,c.BSNO C_BSNO, "
                + "c.DISC_MERRECEIVE C_DISC_MERRECEIVE,c.DISC_CUSTPAYREAL C_DISC_CUSTPAYREAL "
                + "from a ");
        sqlbuf.append("left join dcp_order_detail_agio c on a.eid=c.eid and a.orderno=c.orderno  order by a.orderno,c.MITEM ");

        sql = sqlbuf.toString();
        return sql;
    }

    private String getQuerySql_Detail_Pay(String withasSql,DCP_OrderQueryReq req) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf=new StringBuffer(" with a AS ( "
                + withasSql + " ) "
                + "select a.eid,a.orderno, ");

        sqlbuf.append("d.BILLNO D_BILLNO,d.ITEM D_ITEM,d.BILLDATE D_BILLDATE,d.BDATE D_BDATE, "
                + "d.SOURCEBILLTYPE D_SOURCEBILLTYPE,d.SOURCEBILLNO D_SOURCEBILLNO, "
                + "d.LOADDOCTYPE D_LOADDOCTYPE,d.CHANNELID D_CHANNELID,d.PAYCODE D_PAYCODE, "
                + "d.PAYCODEERP D_PAYCODEERP,d.PAYNAME D_PAYNAME,d.ORDER_PAYCODE D_ORDER_PAYCODE, "
                + "d.ISONLINEPAY D_ISONLINEPAY,d.PAY D_PAY,d.PAYDISCAMT D_PAYDISCAMT, "
                + "d.PAYAMT1 D_PAYAMT1,d.PAYAMT2 D_PAYAMT2,d.DESCORE D_DESCORE,d.CTTYPE D_CTTYPE, "
                + "d.CARDNO D_CARDNO,d.CARDBEFOREAMT D_CARDBEFOREAMT,d.CARDREMAINAMT D_CARDREMAINAMT, "
                + "d.COUPONQTY D_COUPONQTY,d.ISVERIFICATION D_ISVERIFICATION, "
                + "d.EXTRA D_EXTRA,d.CHANGED D_CHANGED,d.PAYSERNUM D_PAYSERNUM,d.SERIALNO D_SERIALNO, "
                + "d.REFNO D_REFNO,d.TERIMINALNO D_TERIMINALNO,d.CANINVOICE D_CANINVOICE, "
                + "d.WRITEOFFAMT D_WRITEOFFAMT,d.AUTHCODE D_AUTHCODE, "
                + "d.LASTMODIOPID D_LASTMODIOPID,d.LASTMODIOPNAME D_LASTMODIOPNAME, "
                + "d.LASTMODITIME D_LASTMODITIME, d.FUNCNO  D_FUNCNO,d.PAYDOCTYPE D_PAYDOCTYPE,d.SENDPAY D_SENDPAY, "
                + "d.PAYTYPE D_PAYTYPE, d.MERDISCOUNT D_MERDISCOUNT,d.MERRECEIVE D_MERRECEIVE,"
                + "d.THIRDDISCOUNT D_THIRDDISCOUNT,d.CUSTPAYREAL D_CUSTPAYREAL,d.COUPONMARKETPRICE D_COUPONMARKETPRICE,"
                + "d.COUPONPRICE D_COUPONPRICE,d.MOBILE D_MOBILE,d.PAYCHANNELCODE AS D_PAYCHANNELCODE,e.coupontypename,"
                + "d.GAINCHANNEL D_GAINCHANNEL,d.GAINCHANNELNAME D_GAINCHANNELNAME "
                + "from a ");
        sqlbuf.append("left join dcp_order_pay_detail d on a.eid=d.eid and a.orderno=d.sourcebillno and d.sourcebilltype='Order' "
                + " left join crm_coupontype e on a.eid=e.eid and d.cttype=e.coupontypeid "
                + "order by a.orderno,d.ITEM ");

        sql = sqlbuf.toString();
        return sql;
    }



    private List<DCP_OrderQueryRes.orders> DealOrders(DCP_OrderQueryReq req,List<Map<String, Object>> getOrder,List<Map<String, Object>> getMulti,List<Map<String, Object>> getMulti_agio,List<Map<String, Object>> getMulti_pay) throws Exception
    {
        List<DCP_OrderQueryRes.orders> orders=new ArrayList<>();
        try
        {
            for (Map<String, Object> map : getOrder)
            {
                DCP_OrderQueryRes.orders order=new DCP_OrderQueryRes().new orders();

                String billType = map.getOrDefault("BILLTYPE","").toString();
                order.setAddress(map.get("ADDRESS").toString());
                order.setbDate(map.get("BDATE").toString());
                order.setBelfirm(map.get("BELFIRM").toString());
                order.setCardNo(map.get("CARDNO").toString());
                order.setChannelId(map.get("CHANNELID").toString());
                order.setChannelIdName(map.get("CHANNELNAME").toString());
                order.setCity(map.get("CITY").toString());
                order.setContMan(map.get("CONTMAN").toString());
                order.setContTel(map.get("CONTTEL").toString());
                order.setCounty(map.get("COUNTY").toString());
                order.setCreateDatetime(map.get("CREATE_DATETIME").toString());
                order.setCustomer(map.get("CUSTOMER").toString());
                order.setCustomerName(map.get("CUSTOMERNAME").toString());
                order.setDeliveryNo(map.get("DELIVERYNO").toString());
                order.setDeliveryStatus(map.get("DELIVERYSTATUS").toString());
                order.setDeliveryType(map.get("DELIVERYTYPE").toString());
                order.setSubDeliveryCompanyNo(map.get("SUBDELIVERYCOMPANYNO").toString());
                order.setSubDeliveryCompanyName(map.get("SUBDELIVERYCOMPANYNAME").toString());
                order.setDeliveryHandinput(map.get("DELIVERYHANDINPUT").toString());

                order.setDelMemo(map.get("DELMEMO").toString());
                order.setDelName(map.get("DELNAME").toString());
                order.setDelTelephone(map.get("DELTELEPHONE").toString());
                order.setDetailType(map.get("DETAILTYPE").toString());
                order.setDeliveryBusinessType(map.get("DELIVERYBUSINESSTYPE").toString());
                order.setEraseAmt(map.get("ERASE_AMT").toString());
                order.setExceptionMemo(map.get("EXCEPTIONMEMO").toString());
                order.setExceptionStatus(map.get("EXCEPTIONSTATUS").toString());

                order.setGetMan(map.get("GETMAN").toString());
                order.setGetManTel(map.get("GETMANTEL").toString());
                order.setHeadOrderNo(map.get("HEADORDERNO").toString());
                order.setIncomeAmt(map.get("INCOMEAMT").toString());
                order.setTotQty(map.get("TOT_QTY").toString());
                order.setIsBook(map.get("ISBOOK").toString());
                order.setIsChargeOrder(map.get("ISCHARGEORDER").toString());
                order.setIsOrgOrder(map.get("ISORGORDER").toString());
                order.setIsShipCompany(map.get("ISSHIPCOMPANY").toString());
                order.setIsUrgentOrder(map.get("ISURGENTORDER").toString());
                order.setLatitude(map.get("LATITUDE").toString());
                order.setLoadDocBillType(map.get("LOADDOCBILLTYPE").toString());
                order.setLoadDocOrderNo(map.get("LOADDOCORDERNO").toString());
                order.setLoadDocType(map.get("LOADDOCTYPE").toString());
                order.setLoadDocTypeName(map.get("APPNAME").toString());
                order.setLongitude(map.get("LONGITUDE").toString());
                order.setMachineNo(map.get("MACHINE").toString());
                order.setMachShopName(map.get("MACHSHOPNAME").toString());
                order.setManualNo(map.get("MANUALNO").toString());
                order.setMachShopNo(map.get("MACHSHOP").toString());
                order.setMealNumber(map.get("MEALNUMBER").toString());
                order.setMemberId(map.get("MEMBERID").toString());
                order.setMemberName(map.get("MEMBERNAME").toString());
                order.setMemberPayNo(map.get("MEMBERPAYNO").toString());
                order.setMemo(map.get("MEMO").toString());
                order.setOpNo(map.get("OPNO").toString());
                order.setOrderNo(map.get("ORDERNO").toString());
                order.setOrderShop(map.get("ORDERSHOP").toString());
                order.setOrderShopName(map.get("ORDERSHOPNAME").toString());
                order.setOutDocType(map.get("OUTDOCTYPE").toString());
                order.setOutDocTypeName(map.get("OUTDOCTYPENAME").toString());
                order.setPackageFee(map.get("PACKAGEFEE").toString());
                order.setPrintCount(map.get("PRINTCOUNT").toString());
                order.setMealStatus(map.get("MEALSTATUS").toString());

                String sPayamt=map.get("PAYAMT").toString();
                if (PosPub.isNumericType(sPayamt)==false)
                {
                    sPayamt="0";
                }
                order.setPayAmt(sPayamt);
                order.setPayStatus(map.get("PAYSTATUS").toString());
                order.setPlatformDisc(map.get("PLATFORM_DISC").toString());
                order.setPointQty(map.get("POINTQTY").toString());
                order.setProMemo(map.get("PROMEMO").toString());
                order.setProvince(map.get("PROVINCE").toString());
                order.setRefundAmt(map.get("REFUNDAMT").toString());
                order.setRefundReason(map.get("REFUNDREASON").toString());
                order.setRefundReasonName(map.get("REFUNDREASONNAME").toString());
                order.setRefundReasonNo(map.get("REFUNDREASONNO").toString());
                order.setRefundSourceBillNo(map.get("REFUNDSOURCEBILLNO").toString());
                order.setRefundStatus(map.get("REFUNDSTATUS").toString());
                order.setRequestId(map.get("REQUESTID").toString());
                order.setRshipFee(map.get("RSHIPFEE").toString());
                order.setSellCredit(map.get("SELLCREDIT").toString());
                order.setSellerDisc(map.get("SELLER_DISC").toString());
                order.setSellNo(map.get("SELLNO").toString());
                order.setServiceCharge(map.get("SERVICECHARGE").toString());
                order.setShipDate(map.get("SHIPDATE").toString());
                order.setShipEndTime(map.get("SHIPENDTIME").toString());
                order.setShipFee(map.get("SHIPFEE").toString());
                order.setShippingShopName(map.get("SHIPPINGSHOPNAME").toString());
                order.setShippingShopNo(map.get("SHIPPINGSHOP").toString());
                order.setShipStartTime(map.get("SHIPSTARTTIME").toString());
                order.setShipType(map.get("SHIPTYPE").toString());
                order.setShopName(map.get("SHOPNAME").toString());
                order.setShopNo(map.get("SHOP").toString());
                order.setSn(map.get("ORDER_SN").toString());
                order.setSquadNo(map.get("SQUADNO").toString());
                order.setStatus(map.get("STATUS").toString());
                order.setsTime(map.get("STIME").toString());
                order.setStreet(map.get("STREET").toString());

                String sTot_amt=map.get("TOT_AMT").toString();
                if (PosPub.isNumericType(sTot_amt)==false)
                {
                    sTot_amt="0";
                }
                order.setTot_Amt(sTot_amt);
                order.setTot_oldAmt(map.get("TOT_OLDAMT").toString());
                order.setTot_shipFee(map.get("TOTSHIPFEE").toString());
                order.setTot_uAmt(map.get("TOT_UAMT").toString());
                order.setTotDisc(map.get("TOT_DISC").toString());
                order.setVerNum(map.get("VER_NUM").toString());
                order.setWorkNo(map.get("WORKNO").toString());
                order.setWriteOffAmt(map.get("WRITEOFFAMT").toString());
                order.setYaoHuoNo(map.get("YAOHUONO").toString());
                order.setIsApportion(map.get("ISAPPORTION").toString());
                order.setOrderToSaleDateTime(map.get("ORDERTOSALE_DATETIME").toString());
                order.setTot_Amt_merReceive(map.get("TOT_AMT_MERRECEIVE").toString());
                order.setTot_Amt_custPayReal(map.get("TOT_AMT_CUSTPAYREAL").toString());
                order.setTotDisc_merReceive(map.get("TOT_DISC_MERRECEIVE").toString());
                order.setTotDisc_custPayReal(map.get("TOT_DISC_CUSTPAYREAL").toString());
                order.setIsSystemInvoice(map.get("SUPPORTINVOICE").toString());
                order.setProductStatus(map.get("PRODUCTSTATUS").toString());
                order.setDelId(map.getOrDefault("DELID","").toString());
                order.setPackerId(map.getOrDefault("PACKERID","").toString());
                order.setPackerName(map.getOrDefault("PACKERNAME","").toString());
                order.setPackerTelephone(map.getOrDefault("PACKERTELEPHONE","").toString());

                order.setTablewareQty(map.getOrDefault("TABLEWAREQTY","0").toString());
                order.setIsEvaluate(map.getOrDefault("ISEVALUATE","").toString());
                order.setIsDelete(map.getOrDefault("ISDELETE","").toString());

                String deliveryTime = map.getOrDefault("DELIVERYTIME","").toString();//yyyy-mm-dd hh:mm:ss
                if(deliveryTime!=null&&deliveryTime.length()==19)
                {
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deliveryTime);
                    deliveryTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date1);
                }
                order.setDeliveryTime(deliveryTime);
                order.setPreparationStatus(map.getOrDefault("PREPARATIONSTATUS","").toString());
                order.setOrderCodeView(map.getOrDefault("ORDERCODEVIEW","").toString());
                order.setDowngraded(map.getOrDefault("DOWNGRADED","N").toString());
                order.setWaiMaiMerReceiveMode(map.getOrDefault("WAIMAIMERRECEIVEMODE","0").toString());
                order.setSaleAmt(map.getOrDefault("SALEAMT","0").toString());
                order.setSaleDisc(map.getOrDefault("SALEDISC","0").toString());
                order.setCanModify(map.getOrDefault("CANMODIFY","").toString());
                order.setOperationType(map.getOrDefault("OPERATIONTYPE","").toString());
                //【ID1034999】【嘉华3.0】团购打折功能-dcp服务 by jinzma 20230804
                order.setGroupBuying(map.getOrDefault("GROUPBUYING","N").toString());
                order.setPartnerMember(Check.Null(map.getOrDefault("PARTNERMEMBER","digiwin").toString())?"digiwin":map.getOrDefault("PARTNERMEMBER","digiwin").toString());
                //【ID1037561】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单下订增加配送费，超区费，加急费同时支持费用补录---服务 by jinzma 20231206
                order.setDeliveryMoney(map.getOrDefault("DELIVERYMONEY","0").toString());
                order.setSuperZoneMoney(map.getOrDefault("SUPERZONEMONEY","0").toString());
                order.setUrgentMoney(map.getOrDefault("URGENTMONEY","0").toString());
                //快递员的上门取件码
                order.setPickUpCode(map.getOrDefault("PICKUPCODE","").toString());

                //【ID1037555】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单打印贺卡及配送路线选择---中台修改 by jinzma 20231222
                order.setIsHaveCard(map.get("ISHAVECARD").toString());
                order.setIsCardPrint(map.get("ISCARDPRINT").toString());
                order.setLineNo(map.get("LINENO").toString());
                order.setLineName(map.get("LINENAME").toString());
                order.setpOrderNo(map.getOrDefault("PORDERNO","").toString());
                String isIntention = map.getOrDefault("ISINTENTION","").toString();
                if (!"Y".equals(isIntention))
                {
                    isIntention = "N";
                }
                order.setIsIntention(isIntention);
                order.setOpName(map.getOrDefault("OP_NAME","").toString());
                order.setAddOrderOriginNo(map.getOrDefault("ADDORDERORIGINNO","").toString());
                order.setAddOrderchildNo(map.getOrDefault("ADDORDERCHILDNO","").toString());
                //赊销标记
                order.setIsCreditSell(new BigDecimal(map.getOrDefault("CREDIT_RETURNAMT","0").toString()).compareTo(BigDecimal.ZERO)>0?"Y":"N");


                //SALEDISC
                DCP_OrderQueryRes.levelShipDetail ship=new DCP_OrderQueryRes(). new levelShipDetail();
                //tot_Amt-payAmt
                BigDecimal bdm_tot_Amt=new BigDecimal(sTot_amt);
                BigDecimal bdm_payAmt=new BigDecimal(sPayamt);
                BigDecimal bdm_collectamt=bdm_tot_Amt.subtract(bdm_payAmt);
                ship.setCollectAmt(bdm_collectamt.toPlainString());

                ship.setPickUpDocPrint(map.get("PICKUPDOCPRINT").toString());
                ship.setDistanceName(map.get("DISTANCENAME").toString());
                ship.setDistanceNo(map.get("DISTANCENO").toString());
                ship.setGreenworld_logisticsId(map.get("GREENWORLD_LOGISTICSID").toString());
                ship.setGreenworld_merchantTradeNo(map.get("GREENWORLD_MERCHANTTRADENO").toString());
                ship.setGreenworld_rtnLogisticsId(map.get("GREENWORLD_RTNLOGISTICSID").toString());
                ship.setGreenworld_rtnMerchantTradeNo(map.get("GREENWORLD_RTNMERCHANTTRADENO").toString());
                ship.setGreenworld_rtnOrderNo(map.get("GREENWORLD_RTNORDERNO").toString());
                ship.setGreenworld_rtnValidNo(map.get("GREENWORLD_RTNVALIDNO").toString());
                ship.setGreenworld_validNo(map.get("GREENWORLD_VALIDNO").toString());
                ship.setMeasureName(map.get("MEASURENAME").toString());
                ship.setMeasureNo(map.get("MEASURENO").toString());
                ship.setPackageName(map.get("PACKAGENAME").toString());
                ship.setPackageNo(map.get("PACKAGENO").toString());
                ship.setReceiverFivecode(map.get("RECEIVER_FIVECODE").toString());
                ship.setReceiverSevencode(map.get("RECEIVER_SEVENCODE").toString());
                ship.setShopeeAddressId(map.get("SHOPEE_ADDRESS_ID").toString());
                ship.setShopeeBranchId(map.get("SHOPEE_BRANCH_ID").toString());
                ship.setShopeeMode(map.get("SHOPEE_MODE").toString());
                ship.setShopeePickuptimeId(map.get("SHOPEE_PICKUP_TIME_ID").toString());
                ship.setShopeeSenderRealName(map.get("SHOPEE_SENDER_REAL_NAME").toString());
                ship.setTemperateName(map.get("TEMPERATELAYERNAME").toString());
                ship.setTemperateNo(map.get("TEMPERATELAYERNO").toString());
                ship.setWeight(map.get("WEIGHT").toString());
                order.setShipDetail(ship);

                //
                DCP_OrderQueryRes.levelInvoice inv=new DCP_OrderQueryRes(). new levelInvoice();

                inv.setBuyerGuiNo(map.get("BUYERGUINO").toString());
                inv.setCarrierCode(map.get("CARRIERCODE").toString());
                inv.setCarrierHiddenId(map.get("CARRIERHIDDENID").toString());
                inv.setCarrierShowId(map.get("CARRIERSHOWID").toString());
                inv.setFreeCode(map.get("FREECODE").toString());
                inv.setInvNo(map.get("INVNO").toString());
                inv.setInvMemo(map.get("INVMEMO").toString());
                inv.setInvoiceDate(map.get("INVOICEDATE").toString());
                inv.setInvoiceTitle(map.get("INVOICETITLE").toString());
                inv.setPeopleType(map.get("PEOPLETYPE").toString());
                inv.setInvoiceType(map.get("INVOICETYPE").toString());
                inv.setInvOperateType(map.get("INVOPERATETYPE").toString());
                inv.setInvSplitType(map.get("INVSPLITTYPE").toString());
                inv.setIsInvoice(map.get("ISINVOICE").toString());
                inv.setLoveCode(map.get("LOVECODE").toString());
                inv.setPassPort(map.get("PASSPORT").toString());
                inv.setTaxRegNumber(map.get("TAXREGNUMBER").toString());


                order.setInvoiceDetail(inv);

                //指定订单号商品、折扣、付款、备注列表
                Map<String, Object> condDetail=new HashMap<>();
                condDetail.put("EID", map.get("EID").toString());
                condDetail.put("ORDERNO", map.get("ORDERNO").toString());
                List<Map<String, Object>> getDetailMulti=MapDistinct.getWhereMap(getMulti, condDetail, true);

                //ITEM过滤商品
                Map<String, Boolean> condDetail_Item=new HashMap<>();
                condDetail_Item.put("ITEM", true);
                List<Map<String, Object>> getDetail=MapDistinct.getMap(getDetailMulti, condDetail_Item);
                order.setGoodsList(new ArrayList<DCP_OrderQueryRes.levelGoods>());
                boolean machShopIsEquShopFlag = true;//生产门店是否和下单门店一致
                if (order.getShopNo()!=null&&!order.getMachShopNo().equals(order.getShopNo()))
                {
                    machShopIsEquShopFlag  = false;
                }
                for (Map<String, Object> detail : getDetail)
                {
                    DCP_OrderQueryRes.levelGoods goods=new DCP_OrderQueryRes(). new levelGoods();

                    goods.setAccNo(detail.get("ACCNO").toString());
                    goods.setAmt(detail.get("AMT").toString());
                    goods.setAttrName(detail.get("ATTRNAME").toString());
                    goods.setAttrName_origin(detail.getOrDefault("ATTRNAME_ORIGIN","").toString());
                    goods.setBoxNum(detail.get("BOXNUM").toString());
                    goods.setBoxPrice(detail.get("BOXPRICE").toString());
                    goods.setCounterNo(detail.get("COUNTERNO").toString());
                    goods.setCouponCode(detail.get("COUPONCODE").toString());
                    goods.setCouponType(detail.get("COUPONTYPE").toString());
                    goods.setDisc(detail.get("DISC").toString());
                    goods.setFeatureName(detail.get("FEATURENAME").toString());
                    goods.setFeatureNo(detail.get("FEATURENO").toString());
                    goods.setGift(detail.get("GIFT").toString());
                    goods.setGiftReason(detail.get("GIFTREASON").toString());
                    goods.setGiftSourceSerialNo(detail.get("GIFTSOURCESERIALNO").toString());
                    goods.setGoodsGroup(detail.get("GOODSGROUP").toString());
                    goods.setGoodsUrl(detail.get("GOODSURL").toString());
                    goods.setInclTax(detail.get("INCLTAX").toString());
                    goods.setInvNo(detail.get("INVNO").toString());
                    goods.setInvSplitType(detail.get("INVSPLITTYPE").toString());
                    goods.setIsMemo(detail.get("ISMEMO").toString());
                    goods.setItem(detail.get("ITEM").toString());
                    goods.setoItem(detail.get("OITEM").toString());
                    goods.setoReItem(detail.get("OREITEM").toString());
                    goods.setOldAmt(detail.get("OLDAMT").toString());
                    goods.setOldPrice(detail.get("OLDPRICE").toString());
                    goods.setPackageMitem(detail.get("PACKAGEMITEM").toString());
                    goods.setPackageType(detail.get("PACKAGETYPE").toString());
                    goods.setPickQty(detail.get("PICKQTY").toString());
                    goods.setPluBarcode(detail.get("PLUBARCODE").toString());
                    goods.setPluName(detail.get("PLUNAME").toString());
                    goods.setPluNo(detail.get("PLUNO").toString());
                    goods.setPrice(detail.get("PRICE").toString());
                    goods.setQty(detail.get("QTY").toString());
                    goods.setrQty(detail.get("RQTY").toString());
                    goods.setrUnpickQty(detail.get("RUNPICKQTY").toString());
                    goods.setSellerName(detail.get("SELLERNAME").toString());
                    goods.setSellerNo(detail.get("SELLERNO").toString());
                    goods.setShopQty(detail.get("SHOPQTY").toString());
                    goods.setSpecName(detail.get("SPECNAME").toString());
                    goods.setSpecName_origin(detail.getOrDefault("SPECNAME_ORIGIN","").toString());
                    goods.setsTime(detail.get("STIME").toString());
                    goods.setsUnit(detail.get("SUNIT").toString());
                    goods.setsUnitName(detail.get("SUNITNAME").toString());
                    goods.setTaxCode(detail.get("TAXCODE").toString());
                    goods.setTaxType(detail.get("TAXTYPE").toString());
                    goods.setToppingMitem(detail.get("TOPPINGMITEM").toString());
                    goods.setToppingType(detail.get("TOPPINGTYPE").toString());
                    goods.setWarehouse(detail.get("WAREHOUSE").toString());
                    goods.setWarehouseName(detail.get("WAREHOUSENAME").toString());
                    goods.setInvItem(detail.get("INVITEM").toString());
                    goods.setVirtual(detail.get("VIRTUAL").toString());
                    goods.setAmt_merReceive(detail.get("AMT_MERRECEIVE").toString());
                    goods.setAmt_custPayReal(detail.get("AMT_CUSTPAYREAL").toString());
                    goods.setDisc_merReceive(detail.get("DISC_MERRECEIVE").toString());
                    goods.setDisc_custPayReal(detail.get("DISC_CUSTPAYREAL").toString());
                    goods.setPreparationStatus(detail.getOrDefault("PREPARATIONSTATUS","").toString());

                    //这些SQL字段未必查询
                    goods.setIsLiquor(detail.getOrDefault("ISLIQUOR","N").toString());
                    goods.setKdsMaxMakeQty(Convert.toBigDecimal(detail.getOrDefault("KDSMAXMAKEQTY",0),BigDecimal.ZERO));
                    goods.setIsQtyPrint(detail.getOrDefault("ISQTYPRINT","N").toString());
                    goods.setIsPrintReturn(detail.getOrDefault("ISPRINTRETURN","N").toString());
                    goods.setIsPrintCrossMenu(detail.getOrDefault("ISPRINTCROSSMENU","N").toString());
                    goods.setCrossPrinter(detail.getOrDefault("CROSSPRINTER","").toString());
                    goods.setKitchenPrinter(detail.getOrDefault("KITCHENPRINTER","").toString());
                    goods.setFlavorStuffDetail(detail.getOrDefault("FLAVORSTUFFDETAIL","").toString());

                    //生产门店需要生产数量赋值 下单门店和生产门店不一致:qty-shopqty 否则取qty
                    goods.setMachShopQty(goods.getQty());
                    if (!machShopIsEquShopFlag)
                    {
                        try
                        {
                            BigDecimal qty_b = new BigDecimal(goods.getQty());
                            BigDecimal shopQty_b = new BigDecimal(goods.getShopQty());
                            BigDecimal machShopQty_b = qty_b.subtract(shopQty_b);
                            if (machShopQty_b.compareTo(BigDecimal.ZERO)==-1)
                            {
                                machShopQty_b = new BigDecimal("0");
                            }
                            goods.setMachShopQty(machShopQty_b.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                        }
                        catch (Exception e)
                        {

                        }
                        goods.setMachShopQty(goods.getQty());
                    }

                    goods.setAgioInfo(new ArrayList<DCP_OrderQueryRes.levelAgio>());
                    goods.setMessages(new ArrayList<DCP_OrderQueryRes.levelMemo>());
                    goods.setNutritions(new ArrayList<DCP_OrderQueryRes.levelNutrition>());

                    //【ID1037876】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单中心裱花间打印格式调整---服务
                    //【ID1037588】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单中心裱花间打印格式调整---服务  by jinzma 20231213
                    goods.setGoodsType(detail.get("GOODSTYPE").toString());
                    goods.setShelfLife(detail.get("SHELFLIFE").toString());
                    goods.setNetContent(detail.get("NETCONTENT").toString());
                    goods.setAmountRefund((detail.get("AMOUNT_REFUND")!=null&&detail.get("AMOUNT_REFUND").toString().trim().length()>0)?detail.get("AMOUNT_REFUND").toString():"0");
                    //【ID1038965】【热点3402】打印标价签需求---服务 by jinzma 20240219
                    goods.setGoodsMemo(detail.get("GOODSMEMO").toString());
                    //【ID1039170】【热点3402】打印标价签需求---易成---POS服务  by jinzma 20240229
                    goods.setShelfLifen(detail.get("SHELFLIFEN").toString());
                    //【20240506020】【热点食品3.0】订单中心打印相关需求
                    goods.setProducer(detail.get("PRODUCER").toString());
                    goods.setStoragecon(detail.get("STORAGECON").toString());
                    goods.setManuFacturer(detail.get("MANUFACTURER").toString());
                    goods.setHotline(detail.get("HOTLINE").toString());
                    goods.setIngretable(detail.get("INGRETABLE").toString());
                    goods.setFoodProlicnum(detail.get("FOODPROLICNUM").toString());
                    goods.setEatingMethod(detail.get("EATINGMETHOD").toString());
                    goods.setExstandard(detail.get("EXSTANDARD").toString());
                    goods.setPropAddress(detail.get("PROPADDRESS").toString());

                    //指定商品的折扣
                    Map<String, Object> condDetailAgio=new HashMap<>();
                    condDetailAgio.put("EID", map.get("EID").toString());
                    condDetailAgio.put("ORDERNO", map.get("ORDERNO").toString());
                    condDetailAgio.put("C_MITEM", detail.get("ITEM").toString());
                    List<Map<String, Object>> getDetailAgioMulti=MapDistinct.getWhereMap(getMulti_agio, condDetailAgio, true);

                    for (Map<String, Object> detailAgio : getDetailAgioMulti)
                    {
                        DCP_OrderQueryRes.levelAgio agio=new DCP_OrderQueryRes().new levelAgio();
                        agio.setAmt(detailAgio.get("C_AMT").toString());
                        agio.setBsNo(detailAgio.get("C_BSNO").toString());
                        agio.setDcType(detailAgio.get("C_DCTYPE").toString());
                        agio.setDcTypeName(detailAgio.get("C_DCTYPENAME").toString());
                        agio.setDisc(detailAgio.get("C_DISC").toString());
                        agio.setGiftCtf(detailAgio.get("C_GIFTCTF").toString());
                        agio.setGiftCtfNo(detailAgio.get("C_GIFTCTFNO").toString());
                        agio.setInputDisc(detailAgio.get("C_INPUTDISC").toString());
                        agio.setItem(detailAgio.get("C_ITEM").toString());
                        agio.setPmtNo(detailAgio.get("C_PMTNO").toString());
                        agio.setQty(detailAgio.get("C_QTY").toString());
                        agio.setRealDisc(detailAgio.get("C_REALDISC").toString());
                        agio.setDisc_merReceive(detailAgio.get("C_DISC_MERRECEIVE").toString());
                        agio.setDisc_custPayReal(detailAgio.get("C_DISC_CUSTPAYREAL").toString());

                        goods.getAgioInfo().add(agio);
                        agio=null;
                    }

                    //指定商品的备注
                    Map<String, Object> condDetailMemo=new HashMap<>();
                    condDetailMemo.put("EID", map.get("EID").toString());
                    condDetailMemo.put("ORDERNO", map.get("ORDERNO").toString());
                    condDetailMemo.put("E_OITEM", detail.get("ITEM").toString());
                    List<Map<String, Object>> getDetailMemoMulti=MapDistinct.getWhereMap(getDetailMulti, condDetailMemo, true);

                    //E_ITEM过滤指定商品的备注
                    Map<String, Boolean> condDetail_Eitem=new HashMap<>();
                    condDetail_Eitem.put("E_ITEM", true);
                    List<Map<String, Object>> getDetailMemo=MapDistinct.getMap(getDetailMemoMulti, condDetail_Eitem);
                    for (Map<String, Object> detail_Memo : getDetailMemo)
                    {
                        DCP_OrderQueryRes.levelMemo memos=new DCP_OrderQueryRes().new levelMemo();
                        memos.setMessage(detail_Memo.get("E_MEMO").toString());
                        memos.setMsgName(detail_Memo.get("E_MEMONAME").toString());
                        memos.setMsgType(detail_Memo.get("E_MEMOTYPE").toString());

                        goods.getMessages().add(memos);
                        memos=null;
                    }


                    //指定商品的能量
                    Map<String, Object> condDetail_nutrition=new HashMap<>();
                    condDetail_nutrition.put("EID", map.get("EID").toString());
                    condDetail_nutrition.put("ORDERNO", map.get("ORDERNO").toString());
                    condDetail_nutrition.put("ITEM", detail.get("ITEM").toString());
                    List<Map<String, Object>> getDetailNutritionMulti=MapDistinct.getWhereMap(getDetailMulti, condDetail_nutrition, true);
                    //name过滤重复
                    Map<String, Boolean> condDetail_nutritionName=new HashMap<>();
                    condDetail_nutritionName.put("NUTRITIONNAME", true);
                    List<Map<String, Object>> getDetailNutritions =MapDistinct.getMap(getDetailNutritionMulti, condDetail_nutritionName);
                    getDetailNutritions.sort(Comparator.comparing(o->String.valueOf(o.getOrDefault("NUTRITIONNAME",""))));
                    for (Map<String, Object> detail_image : getDetailNutritions)
                    {
                        String nutritionname = detail_image.get("NUTRITIONNAME").toString();
                        if (nutritionname.isEmpty())
                        {
                            continue;
                        }
                        DCP_OrderQueryRes.levelNutrition nutrition=new DCP_OrderQueryRes().new levelNutrition();
                        nutrition.setNutritionName(nutritionname);
                        nutrition.setNutritionContent(detail_image.get("NUTRITIONCONTENT").toString());
                        nutrition.setReferenceValue(detail_image.get("REFERENCEVALUE").toString());
                        goods.getNutritions().add(nutrition);
                        nutrition=null;
                    }


                    order.getGoodsList().add(goods);
                    goods=null;
                }


                order.setPay(new ArrayList<DCP_OrderQueryRes.levelPay>());

                //D_ITEM过滤付款明细
                Map<String, Object> condPayDetail_DItem=new HashMap<>();
                condPayDetail_DItem.put("ORDERNO", map.get("ORDERNO").toString());
                List<Map<String, Object>> getPayDetail=MapDistinct.getWhereMap(getMulti_pay, condPayDetail_DItem,true);

                for (Map<String, Object> payDetail : getPayDetail)
                {
                    DCP_OrderQueryRes.levelPay paylist=new DCP_OrderQueryRes().new levelPay();
                    paylist.setAuthCode(payDetail.get("D_AUTHCODE").toString());
                    paylist.setbDate(payDetail.get("D_BDATE").toString());
                    paylist.setCanInvoice(payDetail.get("D_CANINVOICE").toString());
                    paylist.setCardBeforeAmt(payDetail.get("D_CARDBEFOREAMT").toString());
                    paylist.setCardNo(payDetail.get("D_CARDNO").toString());
                    paylist.setCardRemainAmt(payDetail.get("D_CARDREMAINAMT").toString());
                    paylist.setChanged(payDetail.get("D_CHANGED").toString());
                    paylist.setCouponQty(payDetail.get("D_COUPONQTY").toString());
                    paylist.setCtType(payDetail.get("D_CTTYPE").toString());
                    paylist.setDescore(payDetail.get("D_DESCORE").toString());
                    paylist.setExtra(payDetail.get("D_EXTRA").toString());
                    paylist.setIsOnlinePay(payDetail.get("D_ISONLINEPAY").toString());
                    //paylist.setIsOrderpay(payDetail.get("D_ISORDERPAY").toString());
                    paylist.setIsVerification(payDetail.get("D_ISVERIFICATION").toString());
                    paylist.setItem(payDetail.get("D_ITEM").toString());
                    paylist.setLoadDocType(payDetail.get("D_LOADDOCTYPE").toString());
                    paylist.setOrder_payCode(payDetail.get("D_ORDER_PAYCODE").toString());
                    paylist.setPay(payDetail.get("D_PAY").toString());
                    paylist.setPayAmt1(payDetail.get("D_PAYAMT1").toString());
                    paylist.setPayAmt2(payDetail.get("D_PAYAMT2").toString());
                    paylist.setPayCode(payDetail.get("D_PAYCODE").toString());
                    paylist.setPayCodeErp(payDetail.get("D_PAYCODEERP").toString());
                    paylist.setPayDiscAmt(payDetail.get("D_PAYDISCAMT").toString());
                    paylist.setPayName(payDetail.get("D_PAYNAME").toString());
                    paylist.setPaySerNum(payDetail.get("D_PAYSERNUM").toString());
                    paylist.setRefNo(payDetail.get("D_REFNO").toString());
                    paylist.setSerialNo(payDetail.get("D_SERIALNO").toString());
                    paylist.setTeriminalNo(payDetail.get("D_TERIMINALNO").toString());
                    paylist.setPaydoctype(payDetail.get("D_PAYDOCTYPE").toString());
                    paylist.setFuncNo(payDetail.get("D_FUNCNO").toString());
                    paylist.setSendPay(payDetail.get("D_SENDPAY").toString());
                    paylist.setPayType(payDetail.get("D_PAYTYPE").toString());
                    paylist.setMerDiscount(payDetail.get("D_MERDISCOUNT").toString());
                    paylist.setMerReceive(payDetail.get("D_MERRECEIVE").toString());
                    paylist.setThirdDiscount(payDetail.get("D_THIRDDISCOUNT").toString());
                    paylist.setCustPayReal(payDetail.get("D_CUSTPAYREAL").toString());
                    paylist.setCouponMarketPrice(payDetail.get("D_COUPONMARKETPRICE").toString());
                    paylist.setCouponPrice(payDetail.get("D_COUPONPRICE").toString());
                    paylist.setMobile(payDetail.get("D_MOBILE").toString());
                    paylist.setPayChannelCode(payDetail.get("D_PAYCHANNELCODE").toString());

                    //【ID1037810】【阿哆诺斯3311】订单用大量券付款，订单小票修改需汇总记录，不要一行一张券信息---服务 by jinzma 20231220
                    String funcNo = payDetail.get("D_FUNCNO").toString();
                    if ("304".equals(funcNo) || "307".equals(funcNo) || "308".equals(funcNo) || "609".equals(funcNo) || "302".equals(funcNo)) {
                        paylist.setCouponTypeName(payDetail.get("COUPONTYPENAME").toString());
                    }else {
                        paylist.setCouponTypeName("");
                    }
                    paylist.setGainChannel(payDetail.get("D_GAINCHANNEL").toString());
                    paylist.setGainChannelName(payDetail.get("D_GAINCHANNELNAME").toString());
                    order.getPay().add(paylist);
                    paylist=null;
                }


                order.setRefundOrderList(new ArrayList<DCP_OrderQueryRes.orders>());
                order.setChildOrderList(new ArrayList<DCP_OrderQueryRes.orders>());

                //***************处理订单子单
                //1无拆单 2 主单   3子单
                if ("1".equals(billType)&&order.getDetailType().equals("2"))
                {
                    String sqlOrder_Child=getOrderSql_Child(req,order.getOrderNo());
                    List<Map<String, Object>> getOrder_Child = this.doQueryData(sqlOrder_Child, null);
                    String sJoinOrderno="";
                    String sJoinEid="";
                    String sJoinMachshop="";
                    for (Map<String, Object> tempmap : getOrder_Child)
                    {
                        sJoinOrderno+=tempmap.get("ORDERNO").toString()+",";
                        sJoinEid+=tempmap.get("EID").toString()+",";
                        sJoinMachshop+=tempmap.get("MACHSHOP").toString()+",";
                    }

                    //
                    Map<String, String> mapOrder=new HashMap<String, String>();
                    mapOrder.put("MACHSHOP", sJoinMachshop);
                    mapOrder.put("ORDERNO", sJoinOrderno);
                    mapOrder.put("EID", sJoinEid);
                    //
                    MyCommon cm=new MyCommon();
                    String withasSql_Orderno=cm.getFormatSourceMultiColWith(mapOrder);
                    mapOrder=null;
                    mapOrder=null;

                    if (withasSql_Orderno.equals("")==false)
                    {
                        //商品明细、备注
                        String sql_detail=getQuerySql_Detail(withasSql_Orderno,req);
                        List<Map<String, Object>> getMulti_Child = this.doQueryData(sql_detail, null);

                        //折扣
                        String sql_detail_agio=getQuerySql_Detail_Agio(withasSql_Orderno,req);
                        List<Map<String, Object>> getMulti_Child_agio = this.doQueryData(sql_detail_agio, null);

                        //付款
                        String sql_detail_pay=getQuerySql_Detail_Pay(withasSql_Orderno,req);
                        List<Map<String, Object>> getMulti_Child_pay = this.doQueryData(sql_detail_pay, null);

                        order.getChildOrderList().addAll(DealOrders(req,getOrder_Child,getMulti_Child,getMulti_Child_agio,getMulti_Child_pay));
                    }
                }

                //***************处理订单退单
                //6.退单成功  10.部分退款成功
                if ("1".equals(billType)&&(order.getRefundStatus().equals("6")||order.getRefundStatus().equals("10")))
                {
                    String sqlOrder_Refund=getOrderSql_Refund(req,order.getOrderNo());
                    List<Map<String, Object>> getOrder_Refund = this.doQueryData(sqlOrder_Refund, null);
                    String sJoinOrderno="";
                    String sJoinEid="";
                    String sJoinMachshop="";
                    for (Map<String, Object> tempmap : getOrder_Refund)
                    {
                        sJoinOrderno+=tempmap.get("ORDERNO").toString()+",";
                        sJoinEid+=tempmap.get("EID").toString()+",";
                        sJoinMachshop+=tempmap.get("MACHSHOP").toString()+",";
                    }

                    //
                    Map<String, String> mapOrder=new HashMap<String, String>();
                    mapOrder.put("MACHSHOP", sJoinMachshop);
                    mapOrder.put("ORDERNO", sJoinOrderno);
                    mapOrder.put("EID", sJoinEid);
                    //
                    MyCommon cm=new MyCommon();
                    String withasSql_Orderno=cm.getFormatSourceMultiColWith(mapOrder);
                    mapOrder=null;
                    mapOrder=null;

                    if (withasSql_Orderno.equals("")==false)
                    {
                        //商品明细、备注
                        String sql_detail=getQuerySql_Detail(withasSql_Orderno,req);
                        List<Map<String, Object>> getMulti_Refund = this.doQueryData(sql_detail, null);

                        //折扣
                        String sql_detail_agio=getQuerySql_Detail_Agio(withasSql_Orderno,req);
                        List<Map<String, Object>> getMulti_Refund_agio = this.doQueryData(sql_detail_agio, null);

                        //付款
                        String sql_detail_pay=getQuerySql_Detail_Pay(withasSql_Orderno,req);
                        List<Map<String, Object>> getMulti_Refund_pay = this.doQueryData(sql_detail_pay, null);

                        order.getRefundOrderList().addAll(DealOrders(req,getOrder_Refund,getMulti_Refund,getMulti_Refund_agio,getMulti_Refund_pay));
                    }
                }

                orders.add(order);
            }
        }
        catch (Exception ex)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                ex.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                PosPub.WriteETLJOBLog("DealOrders订单查询异常DCP_OrderQuery： "+ errors.toString());

                pw=null;
                errors=null;
            }
            catch (Exception e)
            {

            }
        }

        return orders;
    }


    /**
     * 根据订单号*查询子单单头
     * @param orderno
     * @return
     */
    private String getOrderSql_Child(DCP_OrderQueryReq req,String orderno)
    {
        String sql = null;
        StringBuffer sqlbuf=new StringBuffer("select a.*,b.APPNAME,c.CHANNELNAME,d.SUPPORTINVOICE from dcp_order a "
                + "left join PLATFORM_APP b on a.loaddoctype=b.appno and b.status='100' "
                + "left join CRM_CHANNEL c on a.channelid=c.channelid and a.eid=c.eid and c.status='100' "
                + "left join Dcp_Ecommerce d on a.eid=d.eid and a.loaddoctype=d.loaddoctype and a.channelid=d.channelid "
                + "where a.eid='"+req.getRequest().geteId()+"' and  a.headorderno='"+orderno+"' and a.detailtype='3' ");

        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 根据订单号*查询退单单头
     * @param orderno
     * @return
     */
    private String getOrderSql_Refund(DCP_OrderQueryReq req,String orderno)
    {
        String sql = null;
        StringBuffer sqlbuf=new StringBuffer("select a.*,b.APPNAME,c.CHANNELNAME,d.SUPPORTINVOICE from dcp_order a "
                + "left join PLATFORM_APP b on a.loaddoctype=b.appno and b.status='100' "
                + "left join CRM_CHANNEL c on a.channelid=c.channelid and a.eid=c.eid and c.status='100' "
                + "left join Dcp_Ecommerce d on a.eid=d.eid and a.loaddoctype=d.loaddoctype and a.channelid=d.channelid "
                + "where a.eid='"+req.getRequest().geteId()+"' and a.refundsourcebillno='"+orderno+"' and a.billtype='-1' ");

        sql = sqlbuf.toString();
        return sql;
    }




}
