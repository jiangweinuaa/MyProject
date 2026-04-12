package com.dsc.spos.thirdpart.duandian;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class duandianCallBackService {
    /**
     * 端点OMS主动查询订单
     * @param reqJsonStr
     * @return
     * @throws Exception
     */
    public String queryOrder(String reqJsonStr) throws Exception
    {
        String logFileName = "duandianQueryOrder";
        String result = "";
        queryOrderRes orderRes = new queryOrderRes();
        orderRes.setCode(200);
        ParseJson pj = new ParseJson();
        try
        {
            HelpTools.writelog_fileName("端点主动查询请求Req:"+reqJsonStr,logFileName);

            //queryOrderReq req = pj.jsonToBean(reqJsonStr, new TypeToken<queryOrderReq>(){});
            com.alibaba.fastjson.JSONObject reqJson = JSONObject.parseObject(reqJsonStr);
            String companyCode = reqJson.getOrDefault("companyCode","").toString();
            String orderCode = reqJson.getOrDefault("orderCode","").toString();
            String beginTime = reqJson.getOrDefault("beginTime","").toString();
            String endTime = reqJson.getOrDefault("endTime","").toString();
            String queryType = reqJson.getOrDefault("queryType","").toString();
            String pageNo = reqJson.getOrDefault("pageNo","1").toString();
            String pageSize = reqJson.getOrDefault("pageSize","10").toString();
            String beginTime_date = "";
            String beginTime_time = "";
            String endTime_date = "";
            String endTime_time = "";
            int pageNo_i = 0;
            int pageSize_i = 0;
            boolean isReqOrderNo = false;
            boolean isFail = false;
            StringBuffer errMsg = new StringBuffer("");
            if (queryType.trim().isEmpty())
            {
                isFail = true;
                errMsg.append("queryType节点不能为空，");
            }
            else
            {
                if ("ORDER".equals(queryType)||"REVERSE_ORDER".equals(queryType))
                {

                }
                else
                {
                    isFail = true;
                    errMsg.append("queryType节点枚举值不正确只能是[ORDER、REVERSE_ORDER]，");
                }
            }
            if (pageNo.trim().isEmpty())
            {
                isFail = true;
                errMsg.append("pageNo节点不能为空，");
            }
            else
            {
                try {
                    pageNo_i = Integer.parseInt(pageNo.trim());
                    if (pageNo_i<=0)
                    {
                        errMsg.append("pageNo节点传值必须大于0，");
                    }
                }
                catch (Exception e)
                {
                    isFail = true;
                    errMsg.append("pageNo节点传值非数字，");
                }
            }
            if (pageSize.trim().isEmpty())
            {
                isFail = true;
                errMsg.append("pageSize节点不能为空，");
            }
            else
            {
                try {
                    pageSize_i = Integer.parseInt(pageSize.trim());

                    if (pageSize_i<=0)
                    {
                        errMsg.append("pageSize节点传值必须大于0，");
                    }
                }
                catch (Exception e)
                {
                    isFail = true;
                    errMsg.append("pageSize节点传值非数字，");
                }
            }
            //没有传具体单号
            if (orderCode.trim().isEmpty())
            {
                SimpleDateFormat sdfDate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (beginTime.trim().isEmpty())
                {
                    isFail = true;
                    errMsg.append("beginTime节点不能为空，");
                }
                else
                {
                    try
                    {
                      Date date1 = sdfDate1.parse(beginTime);
                      beginTime_date = new SimpleDateFormat("yyyyMMdd").format(date1);
                      beginTime_time = new SimpleDateFormat("HHmmss").format(date1);

                    }
                    catch (Exception e)
                    {
                        isFail = true;
                        errMsg.append("beginTime传值非日期格式(yyyy-MM-dd HH:mm:ss)，");
                    }
                }

                if (endTime.trim().isEmpty())
                {
                    isFail = true;
                    errMsg.append("endTime节点不能为空，");
                }
                else
                {
                    try
                    {
                        Date date2 = sdfDate1.parse(endTime);
                        endTime_date = new SimpleDateFormat("yyyyMMdd").format(date2);
                        endTime_time = new SimpleDateFormat("HHmmss").format(date2);

                    }
                    catch (Exception e)
                    {
                        isFail = true;
                        errMsg.append("endTime传值非日期格式(yyyy-MM-dd HH:mm:ss)，");
                    }
                }

            }
            else
            {
                isReqOrderNo = true;
            }
            if (isFail)
            {
                orderRes.setErrorCode("400");
                orderRes.setErrorMsg("传参异常:"+errMsg.toString());
                orderRes.setSuccess(false);
                result = pj.beanToJson(orderRes);
                return result ;
            }

            int totalRecords=0;
            int totalPages=0;
            StringBuffer sqlBuffer = new StringBuffer("");
            int saleType = 0 ;//正向
            if ("REVERSE_ORDER".equals(queryType))
            {
                saleType = 1;//逆向
            }
            //計算起啟位置
            int startRow = (pageNo_i-1) * pageSize_i;
            int endRow = startRow + pageSize_i;
            //sqlBuffer.append("");
            sqlBuffer.append(" select * from (");
            sqlBuffer.append(" select count(*) over() num, rownum rn, A.EID,A.SHOPID,A.SALENO from dcp_Sale A");
            sqlBuffer.append(" where A.TYPE="+saleType);
            if (!companyCode.isEmpty())
            {
                sqlBuffer.append(" and A.EID="+companyCode);
            }
            if (isReqOrderNo)
            {
                sqlBuffer.append(" and A.SALENO='"+orderCode+"'");
            }
            else
            {
                sqlBuffer.append(" and A.SDATE>='"+beginTime_date+"'");
                sqlBuffer.append(" and A.STIME>='"+beginTime_time+"'");
                sqlBuffer.append(" and A.SDATE<='"+endTime_date+"'");
                sqlBuffer.append(" and A.STIME<='"+endTime_time+"'");
            }
            sqlBuffer.append(" order by A.SDATE,STIME ) where rn>"+startRow+" and rn<="+endRow);
            orderRes.setSuccess(true);
            orderRes.setPageNo(pageNo_i);
            orderRes.setPageSize(pageSize_i);
            orderRes.setTotalSize(totalRecords);
            orderRes.setLastPage("0");
            orderRes.setRes(new ArrayList<>());
            String sql_header = sqlBuffer.toString();
            HelpTools.writelog_fileName("端点主动查询，查询单头sql:"+sql_header,logFileName);
            List<Map<String, Object>> getOrder = StaticInfo.dao.executeQuerySQL(sql_header, null);
            if (getOrder!=null&&!getOrder.isEmpty())
            {
                String num = getOrder.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);

                //总页数
                totalPages = totalRecords / pageSize_i;
                totalPages = (totalRecords % pageSize_i > 0) ? totalPages + 1 : totalPages;
                if (totalPages==pageNo_i)
                {
                    orderRes.setLastPage("1");//是否最后一页
                }

                orderRes.setTotalSize(totalRecords);

                StringBuffer sJoinOrderno=new StringBuffer("");
                StringBuffer sJoinEid=new StringBuffer("");
                StringBuffer sJoinMachshop=new StringBuffer("");
                String sql_item = "";
                duandianService ddService = new duandianService();
                for (Map<String, Object> tempmap : getOrder)
                {
                    try
                    {
                         /* sJoinOrderno.append(tempmap.get("SALENO").toString()+",");
                    sJoinEid.append(tempmap.get("EID").toString()+",");
                    sJoinMachshop.append(tempmap.get("SHOPID").toString()+",");*/

                        String eId = tempmap.get("EID").toString();
                        String shopId = tempmap.get("SHOPID").toString();
                        String saleNo = tempmap.get("SALENO").toString();
                        sql_item = " SELECT A.*,B.ITEM,B.PLUNO,B.PNAME,B.PLUBARCODE,B.UNIT,B.QTY,B.PRICE,B.AMT "
                                + " FROM DCP_SALE A "
                                + " LEFT join DCP_SALE_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SALENO=B.SALENO "
                                + " where  A.EID='"+eId+"' and A.SHOPID='"+shopId+"' and A.SALENO='"+saleNo+"'";
                        List<Map<String, Object>> getQDataDetail = StaticInfo.dao.executeQuerySQL(sql_item, null);
                        if (getQDataDetail==null||getQDataDetail.isEmpty())
                        {
                            continue;
                        }
                        channelOrderDTO  orderDTO = ddService.convertSaleMapToOrder(getQDataDetail);
                        orderRes.getRes().add(orderDTO);

                    }
                    catch (Exception e)
                    {

                    }

                }

               /* //
                Map<String, String> mapOrder=new HashMap<String, String>();
                mapOrder.put("SHOPID", sJoinMachshop.toString());
                mapOrder.put("SALENO", sJoinOrderno.toString());
                mapOrder.put("EID", sJoinEid.toString());
                //
                MyCommon cm=new MyCommon();
                String withasSql_Orderno=cm.getFormatSourceMultiColWith(mapOrder);
                mapOrder=null;
                mapOrder=null;

                if (withasSql_Orderno.equals(""))
                {
                    orderRes.setErrorCode("400");
                    orderRes.setErrorMsg("订单查询失败--单号转换成临时表的方法处理失败！");
                    orderRes.setSuccess(false);
                    result = pj.beanToJson(orderRes);
                    return result ;
                }

                String sql_detail = " with P as ("+withasSql_Orderno+")"
                                  + " select A.*,B.ITEM,B.PLUNO,B.PNAME,B.PLUBARCODE,B.UNIT,B.QTY,B.PRICE,B.AMT "
                                  + " FROM P "
                                  + " inner join DCP_SALE A  on P.EID=A.EID AND P.SHOPID=A.SHOPID AND P.SALENO=A.SALENO "
                                  + " left join DCP_SALE_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SALENO=B.SALENO";
                HelpTools.writelog_fileName("端点主动查询，查询明细sql:"+sql_detail,logFileName);
                List<Map<String, Object>> getMulti = StaticInfo.dao.executeQuerySQL(sql_detail, null);

*/

            }


        }
        catch (Exception e)
        {
            orderRes.setErrorMsg("查询异常:"+e.getMessage());
            orderRes.setSuccess(false);
        }
        result = pj.beanToJson(orderRes);
        return result;
    }

    /**
     * 端点订单接收后异步回调
     * @param reqJsonStr
     * @return
     * @throws Exception
     */
    public String callBackOrder(String reqJsonStr) throws Exception
    {
        String logFileName = "duandianCallBackOrder";
        String result = "";
        responseDTO orderRes = new responseDTO();
        orderRes.setCode(200);
        orderRes.setSuccess(false);
        ParseJson pj = new ParseJson();
        try
        {
            HelpTools.writelog_fileName("端点订单接收后异步回调请求Req:"+reqJsonStr,logFileName);

            //queryOrderReq req = pj.jsonToBean(reqJsonStr, new TypeToken<queryOrderReq>(){});
            com.alibaba.fastjson.JSONObject reqJson = JSONObject.parseObject(reqJsonStr);
            String callbackCode = reqJson.getOrDefault("callbackCode","").toString();
            String callbackType = reqJson.getOrDefault("callbackType","").toString();
            String resultFlag = reqJson.getOrDefault("resultFlag","").toString();
            String failedReason = reqJson.getOrDefault("failedReason","").toString();
            boolean isFail = false;
            StringBuffer errMsg = new StringBuffer("");
            if (callbackCode.trim().isEmpty())
            {
                isFail = true;
                errMsg.append("callbackCode节点不能为空，");
            }
            if (callbackType.trim().isEmpty())
            {
                isFail = true;
                errMsg.append("callbackType节点不能为空，");
            }
            if (resultFlag.trim().isEmpty())
            {
                isFail = true;
                errMsg.append("resultFlag节点不能为空，");
            }
            else
            {
                if ("SUCCESS".equals(resultFlag)||"FAILED".equals(resultFlag))
                {

                }
                else
                {
                    isFail = true;
                    errMsg.append("resultFlag节点枚举值不正确只能是[SUCCESS、FAILED]，");
                }
            }
            if (isFail)
            {
                orderRes.setErrorCode("400");
                orderRes.setErrorMsg("传参异常:"+errMsg.toString());
                orderRes.setSuccess(false);
                result = pj.beanToJson(orderRes);
                HelpTools.writelog_fileName("端点订单接收后异步回调处理传参异常:"+errMsg.toString()+",单号saleNo="+callbackCode,logFileName);
                return result ;
            }
            String STATUSTOOMS = "3";
            if ("SUCCESS".equals(resultFlag))
            {
                STATUSTOOMS = "2";
            }
            else
            {
                STATUSTOOMS = "3";
                if ("REPEAT_PUSH".equals(failedReason))//重复推送
                {
                    STATUSTOOMS = "2";
                }
            }

            List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
            UptBean ubecOrder=new UptBean("dcp_sale");
           /* ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ubecOrder.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));*/
            ubecOrder.addCondition("SALENO", new DataValue(callbackCode, Types.VARCHAR));
            ubecOrder.addUpdateValue("STATUSTOOMS", new DataValue(STATUSTOOMS, Types.VARCHAR));//1已上传，2单据成功，3单据处理失败
            lstData.add(new DataProcessBean(ubecOrder));
            StaticInfo.dao.useTransactionProcessData(lstData);
            orderRes.setSuccess(true);
            HelpTools.writelog_fileName("端点订单接收后异步回调处理成功，单号saleNo="+callbackCode,logFileName);

        }
        catch (Exception e)
        {
            orderRes.setSuccess(false);
            orderRes.setErrorMsg("处理异常:"+e.getMessage());
        }
        result = pj.beanToJson(orderRes);
        HelpTools.writelog_fileName("端点订单接收后异步回调返回Res:"+result,logFileName);
        return result;

    }
}
