package com.dsc.spos.service.imp.json;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dsc.spos.json.cust.req.DCP_PTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_PTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：PTemplateGet
 *   說明：要货模板查询
 * 服务说明：要货模板查询
 * @author Jinzma
 * @since  2017-03-09
 */
public class DCP_PTemplateQuery extends SPosBasicService<DCP_PTemplateQueryReq,DCP_PTemplateQueryRes>  {
    @Override
    protected boolean isVerifyFail(DCP_PTemplateQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (Check.Null(req.getRequest().getDocType()))
        {  
            errMsg.append("查询类型不可为空值! ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_PTemplateQueryReq> getRequestType() {
        return new TypeToken<DCP_PTemplateQueryReq>(){};
    }

    @Override
    protected DCP_PTemplateQueryRes getResponseType() {
        return new DCP_PTemplateQueryRes();
    }

    @Override
    protected DCP_PTemplateQueryRes processJson(DCP_PTemplateQueryReq req) throws Exception {
        DCP_PTemplateQueryRes res = this.getResponse();
        try {
            int totalRecords;		//总笔数
            int totalPages;
            //单头查询
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            res.setDatas(new ArrayList<>());
            if (getQData != null && !getQData.isEmpty())
            {
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                for (Map<String, Object> oneData : getQData)
                {
                    DCP_PTemplateQueryRes.level1Elm oneLv1 = res.new level1Elm();

                    //【ID1039808】【金贝儿3403】盘点单审核增加处理不异动库存情况 by jinzma 20240327
                    String isAdjustStock = oneData.get("IS_ADJUST_STOCK").toString();  //是否调整库存Y/N/X Y转库存 N转销售 X不异动
                    if (Check.Null(isAdjustStock)) {
                        isAdjustStock = "Y";
                    }


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
                    oneLv1.setReceiptOrgName(oneData.get("RECEIPTORGNAME").toString());
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
                    oneLv1.setSupplierType( oneData.get("SUPPLIERTYPE").toString()); ////20250217 01029  DCP_PTemplateQuery：增加返参：supplierType
                    oneLv1.setDocType(oneData.get("DOC_TYPE").toString());
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
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_PTemplateQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String docType = req.getRequest().getDocType();  //0.要货模板  1.盘点模板  2.加工模板  3.自采模板
        String eId = req.geteId();
        String shopId= req.getShopId();
        String langType = req.getLangType();
        String keyTxt=req.getRequest().getKeyTxt();
        String supplierId = req.getRequest().getSupplierId();

        String queryType = req.getRequest().getQueryType();
        String isCheckUser = req.getRequest().getIsCheckUser();

        String weekOfDay = this.getWeekDay(req);
        String day = this.getDay(req);
        String doubleDay = "1";    //单日
        if(Integer.parseInt(day) % 2==0)
            doubleDay = "2";//双日

        //計算起啟位置
        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String sysDate = df.format(cal.getTime());
        //要货次数
        String pOrderSelectCount = PosPub.getPARA_SMS(dao, eId, shopId, "POrder_Select_Count");
        if (Check.Null(pOrderSelectCount))
            pOrderSelectCount="99";
        int pOrderSelectCount_i = Integer.parseInt(pOrderSelectCount);

        //盘点模板是否只选一次 Is_StockPTemplate_SelectOne  BY JZMA 20180822 陶大爷今天去办买房手续了
        String Is_StockPTemplate_SelectOne = PosPub.getPARA_SMS(dao, eId, shopId, "Is_StockPTemplate_SelectOne");
        if (Check.Null(Is_StockPTemplate_SelectOne))
            Is_StockPTemplate_SelectOne="Y";  //盘点模板默认只能选1次

        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,ac.* from (");
        sqlbuf.append(" select ptemplate.* from (");
        sqlbuf.append(" "
                + " select distinct a.*,z.org_name as receiptorgname from DCP_ptemplate a"
                + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type"
                + " inner join dcp_ptemplate_shop ps on a.eid=ps.eid and a.ptemplateno=ps.ptemplateno and a.doc_type=ps.doc_type and ps.status='100' "//and ps.shopid='"+shopId+"'
                + " left join DCP_PTEMPLATE_EMPLOYEE pe on pe.eid=a.eid and pe.ptemplateno=a.ptemplateno and pe.status='100' and pe.doc_type=a.doc_type "
                + " left join dcp_goods_lang c  on b.pluno=c.pluno and b.eid=c.eid  and c.lang_type = '"+langType+"' "
                + " left join dcp_org_lang z on a.eid = z.eid and a.receipt_org = z.organizationno and z.lang_type = '"+langType+"'"
                + " where a.status='100' and (a.hqporder='N' or a.hqporder is null) and a.eid= '"+ eId +"' and a.doc_type= '"+ docType +"'"
                + " and (a.shoptype is null or a.shoptype='2')"
                + " ");
        if (!Check.Null(keyTxt))
            sqlbuf.append(" and (a.ptemplateno like '%%"+ keyTxt +"%%' or a.ptemplate_name like '%%"+ keyTxt +"%%'  or c.plu_name like '%%"+ keyTxt +"%%')");

        if(!"2".equals(queryType)) {
            sqlbuf.append(" "
                    + " and ((a.time_type='0' ) "
                    + " or (a.time_type='1' and a.time_value like '%" + doubleDay + "%')"
                    + " or (a.time_type='2' and a.time_value like '%" + weekOfDay + "%')"
                    + " or (a.time_type='3' and ';'||a.time_value||';' like '%%;" + String.valueOf(Integer.valueOf(day)) + ";%%')"
                    + " or (a.time_type='3' and a.time_value like '%%" + day + "%%'))");
        }

        //【ID1018233】【3.0货郎】门店自采获取外部供应商采购价-模板查询服务（服务端） by jinzma 20210607
        if (!Check.Null(supplierId) && docType.equals("3")){
            sqlbuf.append(" and a.supplier='"+supplierId+"' ");
        }

        //【ID1017676】【3.0】要货模板查询服务排除当前请求机构，本机构不能向本机构自己要货/要货单提交校验不能向本机构自己要货 by jinzma 20210517
        if(docType.equals("0")&&!"2".equals(queryType)) {
            //sqlbuf.append(" and a.receipt_org <>'"+shopId+"' "); //丹丹要求改成下面的条件
        	 sqlbuf.append(" and not ( SUPPLIERTYPE='FACTORY'  and a.receipt_org ='"+shopId+"' ) ");  
        }
        if(Check.Null(queryType)|| "0".equals(queryType)){
            sqlbuf.append(" and ps.SHOPID='"+req.getOrganizationNO()+"' ");
        }

        if("0".equals(docType)&&"2".equals(queryType)){
            sqlbuf.append(" and a.RECEIPT_ORG='"+req.getOrganizationNO()+"' ");
            if("Y".equals(isCheckUser)){
                sqlbuf.append(" and (nvl(pe.EMPLOYEEID,'')='"+req.getEmployeeNo()+"' or nvl(pe.EMPLOYEEID,'')=''  or pe.EMPLOYEEID is null ) ");
            }
        }

        sqlbuf.append(" union all");
        sqlbuf.append(" "
                + " select distinct a.*,z.org_name as receiptorgname from dcp_ptemplate a"
                + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type"
                + " left join dcp_goods_lang c  on b.pluno=c.pluno and b.eid=c.eid  and c.lang_type = '"+langType+"'"
                + " left join dcp_org_lang z on a.eid = z.eid and a.receipt_org = z.organizationno and z.lang_type = '"+langType+"'"
                + " left join DCP_PTEMPLATE_EMPLOYEE pe on pe.eid=a.eid and pe.ptemplateno=a.ptemplateno and pe.status='100' and pe.doc_type=a.doc_type "
                + " where a.status='100' and (a.hqporder='N' or a.hqporder is null) and a.eid= '"+ eId +"' and a.doc_type= '"+ docType +"'"
                + " and a.shoptype='1'"
                + "");

        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.ptemplateno like '%%" + keyTxt + "%%' or a.ptemplate_name like '%%" + keyTxt + "%%' or c.plu_name like '%%" + keyTxt + "%%' )");
        }

        if("0".equals(docType)&&"2".equals(queryType)){
            sqlbuf.append(" and a.RECEIPT_ORG='"+req.getOrganizationNO()+"' ");
            if("Y".equals(isCheckUser)){
                sqlbuf.append(" and (nvl(pe.EMPLOYEEID,'')='"+req.getEmployeeNo()+"' or nvl(pe.EMPLOYEEID,'')='' or   pe.EMPLOYEEID is null  ) ");
            }
        }

        if(!"2".equals(queryType)) {
            sqlbuf.append(" "
                    + " and ((a.time_type='0' )"
                    + " or (a.time_type='1' and a.time_value like '%" + doubleDay + "%')"
                    + " or (a.time_type='2' and a.time_value like '%" + weekOfDay + "%')"
                    + " or (a.time_type='3' and ';'||a.time_value||';' like '%%;" + String.valueOf(Integer.valueOf(day)) + ";%%')"
                    + " or (a.time_type='3' and a.time_value like '%%" + day + "%%'))");
        }

        //【ID1018233】【3.0货郎】门店自采获取外部供应商采购价-模板查询服务（服务端） by jinzma 20210607
        if (!Check.Null(supplierId) && docType.equals("3")){
            sqlbuf.append(" and a.supplier='"+supplierId+"' ");
        }

        if(docType.equals("0")&&!"2".equals(queryType)) {
            //sqlbuf.append(" and a.receipt_org <>'"+shopId+"' ");  //丹丹要求改成下面的条件
            sqlbuf.append(" and not ( SUPPLIERTYPE='FACTORY'  and a.receipt_org ='"+shopId+"' ) ");  
           
        }
        sqlbuf.append(" ) ptemplate");

        //0.要货模板  1.盘点模板传  2.加工模板  3.自采模板
        if(docType.equals("0")) {
            sqlbuf.append(" "
                    + " left join ("
                    + " select ptemplateno from dcp_porder"
                    + " where eid= '"+ eId +"'  and  shopid='"+shopId+"' and bdate="+sysDate+""
                    + " group by ptemplateno having count(*)>='"+pOrderSelectCount_i+"'"
                    + " ) p on ptemplate.ptemplateno=p.ptemplateno "
                    + " where p.ptemplateno is null"
                    + " ");
        }

        if(docType.equals("1") && Is_StockPTemplate_SelectOne.equals("Y")) {
            sqlbuf.append(" "
                    + " left join ("
                    + " select ptemplateno from dcp_stocktake"
                    + " where eid= '"+ eId +"'  and  shopid='"+shopId+"' and bdate="+sysDate+""
                    + " ) p on ptemplate.ptemplateno=p.ptemplateno "
                    + " where p.ptemplateno is null"
                    + " ");
        }
        sqlbuf.append(" "
                + " order by ptemplate.ptemplateno"
                + " )ac"
                + " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  "
                + " ");

        return sqlbuf.toString();
    }

    protected String getWeekDay(DCP_PTemplateQueryReq req) throws Exception{
        String weekOfDay="";
        String sql = "select to_char(sysdate,'D') as week from dual";
        List<Map<String, Object>> getWeekDay = this.doQueryData(sql, null);
        if (getWeekDay != null && !getWeekDay.isEmpty())
        {
            Map<String, Object> strWeekDay = getWeekDay.get(0);
            String weekDay = strWeekDay.get("WEEK").toString();
            if(!Check.Null(req.getRequest().getbDate())){
                Date date = new SimpleDateFormat("yyyyMMdd").parse(req.getRequest().getbDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                weekDay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
            }
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

    protected String getDay(DCP_PTemplateQueryReq req) throws Exception{
        String day="";
        String sql = "select to_char(sysdate,'dd') as day from dual";
        List<Map<String, Object>> getDay = this.doQueryData(sql, null);
        if (getDay != null && !getDay.isEmpty())
        {
            day=getDay.get(0).get("DAY").toString();

            if(!Check.Null(req.getRequest().getbDate())){
                Date date = new SimpleDateFormat("yyyyMMdd").parse(req.getRequest().getbDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            }
        }
        return day;
    }

}


