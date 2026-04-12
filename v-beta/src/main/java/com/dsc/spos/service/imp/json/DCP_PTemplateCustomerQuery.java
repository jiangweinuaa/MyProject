package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PTemplateCustomerQueryReq;
import com.dsc.spos.json.cust.res.DCP_PTemplateCustomerQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：PTemplateGet
 *   說明：大客户要货模板查询
 * 服务说明：要货模板查询
 * @author Jinzma
 * @since  2017-03-09
 */
public class DCP_PTemplateCustomerQuery extends SPosBasicService<DCP_PTemplateCustomerQueryReq,DCP_PTemplateCustomerQueryRes>  {
    @Override
    protected boolean isVerifyFail(DCP_PTemplateCustomerQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (Check.Null(req.getRequest().getCustomerNo()))
        {
            errMsg.append("大客户编码customerNo不可为空值! ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PTemplateCustomerQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PTemplateCustomerQueryReq>(){};
    }

    @Override
    protected DCP_PTemplateCustomerQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PTemplateCustomerQueryRes();
    }

    @Override
    protected DCP_PTemplateCustomerQueryRes processJson(DCP_PTemplateCustomerQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_PTemplateCustomerQueryRes res = this.getResponse();
        try
        {
            int totalRecords;		//总笔数
            int totalPages;
            //单头查询
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            res.setDatas(new ArrayList<DCP_PTemplateCustomerQueryRes.level1Elm>());
            if (getQData != null && getQData.isEmpty() == false)
            {
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                for (Map<String, Object> oneData : getQData)
                {
                    DCP_PTemplateCustomerQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    // 取出第一层


                    //是否调整库存
                    String isAdjustStock = oneData.get("IS_ADJUST_STOCK").toString();
                    if (Check.Null(isAdjustStock)||!isAdjustStock.equals("N"))
                        isAdjustStock="Y";
                    // 新增按商品分类盘点  BY JZMA 20200115
                    String rangeWay = oneData.get("RANGEWAY").toString();   //0.按商品盘点 1.按分类盘点
                    if (Check.Null(rangeWay))
                        rangeWay="0";
                    // 新增盘点是否显示库存为零   BY JZMA 20200205
                    String isShowZStock =oneData.getOrDefault("ISSHOWZSTOCK", "").toString();
                    if (Check.Null(isShowZStock) || !isShowZStock.equals("0"))
                        isShowZStock="1" ;
                    // 允许新增商品   BY JZMA 20200205
                    String isAddGoods =oneData.getOrDefault("ISADDGOODS", "").toString();
                    if (Check.Null(isAddGoods) || !isAddGoods.equals("Y"))
                        isAddGoods="N" ;
                    //是否显示总部库存数量
                    String isShowHeadStockQty =oneData.getOrDefault("ISSHOWHEADSTOCKQTY", "").toString();
                    if (Check.Null(isShowHeadStockQty) || !isShowHeadStockQty.equals("Y"))
                        isShowHeadStockQty="N" ;

                    //设置响应
                    oneLv1.setpTemplateNo(oneData.get("PTEMPLATENO").toString());
                    oneLv1.setpTemplateName(oneData.get("PTEMPLATE_NAME").toString());
                    oneLv1.setReceiptOrg(oneData.get("RECEIPT_ORG").toString());
                    oneLv1.setPreDay(oneData.get("PRE_DAY").toString());
                    oneLv1.setReceiptOrgName(oneData.getOrDefault("RECEIPTORGNAME","").toString());
                    oneLv1.setIsBTake(oneData.get("IS_BTAKE").toString());
                    oneLv1.setOptionalTime(oneData.get("OPTIONAL_TIME").toString());
                    oneLv1.setTaskWay(oneData.get("TASKWAY").toString());
                    oneLv1.setIsAdjustStock(isAdjustStock);
                    oneLv1.setRangeWay(rangeWay);
                    oneLv1.setRdate_Type(oneData.get("RDATE_TYPE").toString());
                    oneLv1.setRdate_Add(oneData.get("RDATE_ADD").toString());
                    oneLv1.setRdate_Values(oneData.get("RDATE_VALUES").toString());
                    oneLv1.setRevoke_Day(oneData.get("REVOKE_DAY").toString());
                    oneLv1.setRevoke_Time(oneData.get("REVOKE_TIME").toString());
                    oneLv1.setRdate_Times(oneData.get("RDATE_TIMES").toString());
                    oneLv1.setIsShowZStock(isShowZStock);
                    oneLv1.setIsAddGoods(isAddGoods);
                    oneLv1.setIsShowHeadStockQty(isShowHeadStockQty);
                    oneLv1.setSupplier(oneData.get("SUPPLIER").toString());
                    oneLv1.setStockTakeCheck(oneData.get("STOCKTAKECHECK").toString());
                    oneLv1.setCalType(oneData.get("CAL_TYPE").toString());
                    oneLv1.setMateralType(oneData.get("MATERAL_TYPE").toString());
                    oneLv1.setoType( oneData.get("OTYPE").toString());
                    res.getDatas().add(oneLv1);
                    oneLv1=null;
                }
            }
            else
            {
                totalRecords = 0;
                totalPages = 0;
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;
        }
        catch (Exception e)
        {
            // TODO: handle exception
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_PTemplateCustomerQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String docType = "0";  //0.要货模板  1.盘点模板  2.加工模板  3.自采模板
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt=req.getRequest().getKeyTxt();
        String customerNo = req.getRequest().getCustomerNo();

        //計算起啟位置
        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String sysDate = df.format(cal.getTime());

        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,ac.* from (");
        sqlbuf.append(" select ptemplate.* from (");
        sqlbuf.append(" "
                + " select distinct a.* from DCP_ptemplate a"
                + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type"
                + " inner join DCP_TEMCUSTOMER ps on a.eid=ps.eid and a.ptemplateno=ps.TEMPLATENO  and ps.CUSTOMERNO='"+customerNo+"'"
                + " left join dcp_goods_lang c  on b.pluno=c.pluno and b.eid=c.eid  and c.lang_type = '"+langType+"' "
                + " where a.status='100'  and a.eid= '"+ eId +"' and a.doc_type= '"+ docType +"'"
                + " "
                + " ");
        if (!Check.Null(keyTxt))
            sqlbuf.append(" and (a.ptemplateno like '%%"+ keyTxt +"%%' or a.ptemplate_name like '%%"+ keyTxt +"%%'  or c.plu_name like '%%"+ keyTxt +"%%')");

        sqlbuf.append(" ) ptemplate");


        sqlbuf.append(" "
                + " order by ptemplate.ptemplateno"
                + " )ac"
                + " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  "
                + " ");

        return sqlbuf.toString();
    }

    protected String getWeekDay() throws Exception{
        String weekOfDay="";
        String sql = "select to_char(sysdate,'D') as week from dual";
        List<Map<String, Object>> getWeekDay = this.doQueryData(sql, null);
        if (getWeekDay != null && getWeekDay.isEmpty() == false)
        {
            Map<String, Object> strWeekDay = getWeekDay.get(0);
            String weekDay = strWeekDay.get("WEEK").toString();
            switch (weekDay) {
                case "1":
                    weekOfDay="周日";
                    break;
                case "2":
                    weekOfDay="周一";
                    break;
                case "3":
                    weekOfDay="周二";
                    break;
                case "4":
                    weekOfDay="周三";
                    break;
                case "5":
                    weekOfDay="周四";
                    break;
                case "6":
                    weekOfDay="周五";
                    break;
                case "7":
                    weekOfDay="周六";
                    break;
                default:
                    break;
            }
        }
        return weekOfDay;
    }

    protected String getDay() throws Exception{
        String day="";
        String sql = "select to_char(sysdate,'dd') as day from dual";
        List<Map<String, Object>> getDay = this.doQueryData(sql, null);
        if (getDay != null && getDay.isEmpty() == false)
        {
            day=getDay.get(0).get("DAY").toString();
        }
        return day;
    }

}


