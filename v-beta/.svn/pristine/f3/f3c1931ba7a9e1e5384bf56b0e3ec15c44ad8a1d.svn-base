package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PTemplateCalculateReq;
import com.dsc.spos.json.cust.res.DCP_PTemplateCalculateRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DCP_PTemplateCalculate extends SPosBasicService<DCP_PTemplateCalculateReq, DCP_PTemplateCalculateRes>
{

    @Override
    protected boolean isVerifyFail(DCP_PTemplateCalculateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_PTemplateCalculateReq> getRequestType()
    {
        return new TypeToken<DCP_PTemplateCalculateReq>(){};
    }

    @Override
    protected DCP_PTemplateCalculateRes getResponseType()
    {
        return new DCP_PTemplateCalculateRes();
    }

    @Override
    protected DCP_PTemplateCalculateRes processJson(DCP_PTemplateCalculateReq req) throws Exception
    {
        DCP_PTemplateCalculateRes res=this.getResponseType();
        //查詢資料
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        String eid = req.geteId();
        String employeeNo = req.getEmployeeNo();
        //String rDate = req.getRequest().getRDate();
        String isTemplateControl = req.getRequest().getIsTemplateControl();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        StringBuilder orgSb=new StringBuilder("select a.phone,b.organizationno,b.org_name from dcp_org a " +
                " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+eid+"' and a.status='100' "); //and a.org_form='2'
        List<Map<String, Object>> getOrgData = this.doQueryData(orgSb.toString(), null);

        StringBuilder tsSb=new StringBuilder("select a.PTEMPLATENO,b.shopid,c.org_name,b.shopid as organizationno,d.phone " +
                " from DCP_PTEMPLATE a " +
                " inner join DCP_PTEMPLATE_SHOP b on a.eid=b.eid and a.PTEMPLATENO=b.PTEMPLATENO " +
                " left join dcp_org_lang c on a.eid=c.eid and b.shopid=c.organizationno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org d on a.eid=d.eid and b.shopid=d.organizationno " +
                " where a.eid='"+eid+"' and a.receipt_org='"+req.getRequest().getReceiptOrgNo()+"' " +
                " and b.status='100'");
        List<Map<String, Object>> getTsData = this.doQueryData(tsSb.toString(), null);

        StringBuilder boSb=new StringBuilder("select a.PTEMPLATENO,a.shopid " +
                " from DCP_PORDER a " +
                " where a.eid='"+eid+"' " +
                " and a.rdate>='"+req.getRequest().getBeginDate()+"' " +
                " and a.rdate<='"+req.getRequest().getEndDate()+"' and a.status='2' ");
        List<Map<String, Object>> getBoData = this.doQueryData(boSb.toString(), null);

        StringBuilder sb=new StringBuilder("select * from " +
                " DCP_PTEMPLATE_EMPLOYEE a " +
                " where a.eid='"+eid+"' ");
        List<Map<String, Object>> getQData2 = this.doQueryData(sb.toString(), null);


        //if (getQData != null && getQData.isEmpty() == false) // 有資料，取得詳細內容
        //{
            //String num = getQData.get(0).get("NUM").toString();
            //totalRecords=Integer.parseInt(num);
            //算總頁數
            //totalPages = totalRecords / req.getPageSize();
            //totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            String beginDate = req.getRequest().getBeginDate();
            String endDate = req.getRequest().getEndDate();
            //从beginDate 到endDate 取出每一天

            List<String> dateList = getDateBetween(beginDate, endDate);
            for (String currentDate : dateList) {
                DCP_PTemplateCalculateRes.RDates rDates = res.new RDates();
                rDates.setRDate(currentDate);
                rDates.setDatas(new ArrayList<>());
                if (getQData != null && getQData.isEmpty() == false) // 有資料，取得詳細內容
                {
                    for (Map<String, Object> row : getQData) {
                        String time_type = row.get("TIME_TYPE").toString();
                        //String time_value = row.get("TIME_VALUE").toString();
                        String ptemplateno = row.get("PTEMPLATENO").toString();
                        String shop_type = row.get("SHOPTYPE").toString();


                        if ("Y".equals(isTemplateControl)) {
                            List<Map<String, Object>> pemployees = getQData2.stream().filter(x -> x.get("PTEMPLATENO").toString().equals(ptemplateno)).collect(Collectors.toList());
                            if (pemployees != null && pemployees.isEmpty() == false) {
                                List<String> employeeids = pemployees.stream().map(x -> x.get("EMPLOYEEID").toString()).distinct().collect(Collectors.toList());
                                if (!employeeids.contains(employeeNo)) {
                                    continue;
                                }
                            }
                        }

                        DCP_PTemplateCalculateRes.Level1Elm level1Elm = res.new Level1Elm();

                        //应提交门店
                        //适用门店类型：1-全部门店2-指定门店

                        level1Elm.setTemplateNo(ptemplateno);
                        level1Elm.setTemplateName(row.get("PTEMPLATENAME").toString());
                        level1Elm.setOptionalTime(row.get("OPTIONAL_TIME").toString());
                        level1Elm.setReceiptOrgNo(row.get("RECEIPTORGNO").toString());
                        level1Elm.setReceiptOrgName(row.get("RECEIPTORGNAME").toString());


                        level1Elm.setOrgCqty("0");
                        level1Elm.setSumbitOrgCqty("0");
                        level1Elm.setUnSumbitCqty("0");
                        List<Map<String, Object>> cOrgList = new ArrayList<>();

                        //将周期类型=无且发货组织=当前登录组织的要货模板纳入统计范围，此类型要货模板的【应提交数】固定为0，
                        // if(!"0".equals(time_type)) {
                        if ("1".equals(shop_type)) {
                            level1Elm.setOrgCqty(String.valueOf(getOrgData.size()));
                            cOrgList.addAll(getOrgData);
                        } else {
                            List<Map<String, Object>> pShopIds = getTsData.stream().filter(x -> x.get("PTEMPLATENO").toString().equals(ptemplateno)).collect(Collectors.toList());
                            level1Elm.setOrgCqty(String.valueOf(pShopIds.size()));
                            cOrgList.addAll(pShopIds);
                        }
                        // }


                        List<String> shopIds = getBoData.stream().filter(x -> x.get("PTEMPLATENO").toString().equals(ptemplateno) && x.get("RDATE").toString().equals(currentDate)).map(x -> x.get("SHOPID").toString()).distinct().collect(Collectors.toList());
                        level1Elm.setSumbitOrgCqty(String.valueOf(shopIds.size()));
                        if (!"0".equals(time_type)) {
                            int unOrg = Integer.parseInt(level1Elm.getOrgCqty()) - Integer.parseInt(level1Elm.getSumbitOrgCqty());
                            level1Elm.setUnSumbitCqty(String.valueOf(unOrg));
                            level1Elm.setUnSumbitOrgList(new ArrayList<>());
                            for (Map<String, Object> singOrg : cOrgList) {
                                String organizationno = singOrg.get("ORGANIZATIONNO").toString();
                                if (!shopIds.contains(organizationno)) {
                                    DCP_PTemplateCalculateRes.UnSumbitOrgList unSumbitOrg = res.new UnSumbitOrgList();
                                    unSumbitOrg.setOrgNo(organizationno);
                                    unSumbitOrg.setOrgName(singOrg.get("ORG_NAME").toString());
                                    unSumbitOrg.setPhone(singOrg.get("PHONE").toString());
                                    level1Elm.getUnSumbitOrgList().add(unSumbitOrg);
                                }
                            }
                        }
                        rDates.getDatas().add(level1Elm);
                    }
                }

                res.getDatas().add(rDates);

            }



        //}




        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {


    }

    @Override
    protected String getQuerySql(DCP_PTemplateCalculateReq req) throws Exception
    {
        String receiptOrgNo = req.getRequest().getReceiptOrgNo();
        //String isTemplateControl = req.getRequest().getIsTemplateControl();
        //String rDate = req.getRequest().getRDate();
        String eid = req.geteId();

        //int pageSize=req.getPageSize();
        //int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        //startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        //startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        StringBuffer sb=new StringBuffer();
        sb.append(" select * from (");
        sb.append(" select count(*) over () num,rownum rn,ac.* from (");
        sb.append("select a.SHOPTYPE,a.ptemplateno,a.PTEMPLATE_NAME as ptemplatename,a.OPTIONAL_TIME,b.ORGANIZATIONNO as receiptorgno,b.org_name as receiptOrgName,a.TIME_TYPE,a.TIME_VALUE" +
                " from DCP_PTEMPLATE a " +
                " left join DCP_ORG_LANG b on a.eid=b.eid and a.receipt_org=b.ORGANIZATIONNO and b.lang_type='"+req.getLangType()+"'" +
                " where a.eid='"+eid+"' and a.receipt_org='"+receiptOrgNo+"' ");
        //sb.append(" and a.time_type!='0' ");//剔除不等于0的

        Date beginDate = new Date();
        String weekOfDay = this.getWeekDay(beginDate);
        String day = this.getDay(beginDate);
        String doubleDay = "1";    //单日
        if(Integer.parseInt(day) % 2==0) {
            doubleDay = "2";//双日
        }
        sb.append(" and  ((a.time_type='0' and a.RECEIPT_ORG='"+req.getOrganizationNO()+"')"
                + " or (a.time_type='1' and a.time_value like '%"+doubleDay+"%')"
                + " or (a.time_type='2' and a.time_value like '%"+weekOfDay+"%')"
                + " or (a.time_type='3' and ';'||a.time_value||';' like '%%;"+String.valueOf(Integer.valueOf(day))+";%%')"
                + " or (a.time_type='3' and a.time_value like '%%"+day+"%%')" +

                ")");


        sb.append(" "
                + " ) ac"
                + " )   "
                + " ");

        return sb.toString();
    }


    protected String getWeekDay(Date date) throws Exception{
        String weekOfDay="";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String weekDay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
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
        return weekOfDay;

    }
    public  Date addOneDay(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    protected String getDay(Date date) throws Exception{
        String day="";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        return day;

    }

    private List<String> getDateBetween(String beginDate, String endDate) throws Exception {
        List<String> dateList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date start = sdf.parse(beginDate);
        Date end = sdf.parse(endDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);

        while (!calendar.getTime().after(end)) {
            dateList.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
        }

        return dateList;
    }








}
