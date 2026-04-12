package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSONArray;
import com.dsc.spos.json.cust.req.DCP_ReserveOrderCreate_OpenReq;
import com.dsc.spos.json.cust.req.DCP_ReserveTimeQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ReserveTimeQuery_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 预约时间查询
 * @author: wangzyc
 * @create: 2021-08-02
 */
public class DCP_ReserveTimeQuery_Open extends SPosBasicService<DCP_ReserveTimeQuery_OpenReq, DCP_ReserveTimeQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_ReserveTimeQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveTimeQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空 ");
            isFail = true;
        }
        if (Check.Null(request.getItemsNo())) {
            errMsg.append("项目编号不能为空 ");
            isFail = true;
        }
        if (Check.Null(request.getDate())) {
            errMsg.append("所选日期不能为空 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveTimeQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_ReserveTimeQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_ReserveTimeQuery_OpenRes getResponseType() {
        return new DCP_ReserveTimeQuery_OpenRes();
    }

    @Override
    protected DCP_ReserveTimeQuery_OpenRes processJson(DCP_ReserveTimeQuery_OpenReq req) throws Exception {
        DCP_ReserveTimeQuery_OpenRes res = this.getResponseType();
        String eId = req.geteId();
        DCP_ReserveTimeQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String date = request.getDate();
        String itemsNo = request.getItemsNo();
        String opNo = request.getOpNo();


        res.setDatas(res.new level1Elm());

        try {
            // 获取当前门店可预约几天内时间
            String[] conditionValues = {eId, shopId};
            int appointmentDays = getAppointmentDays(eId, conditionValues);
            List<String> dates = null;
            if (appointmentDays != 0) {
                dates = getDate(appointmentDays);
            }
            // 排除门店休息日
            dates = excludeOffDay(dates, conditionValues);

            if (!Check.Null(opNo)) {
                // 如果 员工编号不为空则排除员工休息日
                String[] conditionValues2 = {eId, shopId, opNo};
                dates = excludeOffOpNoDay(dates, conditionValues2);
            }

            res.getDatas().setDateList(new ArrayList<DCP_ReserveTimeQuery_OpenRes.level2Elm>());

            Collections.sort(dates);
            if (!CollectionUtils.isEmpty(dates)) {
                for (String day : dates) {
                    DCP_ReserveTimeQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                    int week = DCP_AdvisorQuery_Open.getWeek(day);
                    lv2.setDate(day);
                    lv2.setWeek(getWeek(week));
                    res.getDatas().getDateList().add(lv2);
                }
            }

            // 判断 所传日期为周几
            String currentDateWeek = "";
            if (!Check.Null(date)) {
                currentDateWeek = DCP_AdvisorQuery_Open.getWeek(date) + "";
            }

            res.getDatas().setTimeList(new ArrayList<DCP_ReserveTimeQuery_OpenRes.level3Elm>());
            // 先查询所有的时间段
            List<Map<String, String>> times = getTimes(conditionValues, currentDateWeek);
            if (!CollectionUtils.isEmpty(times)&&dates.contains(date)) {
                SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
                SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                String currentDate = sdr.format(cal.getTime());
                String currentTime = sd.format(cal.getTime());
                Date parse = sd.parse(currentTime);
                times = sortTimes(times,"1");
                for (Map<String, String> time : times) {
                    String begintime = time.get("BEGINTIME").toString();
                    String endtime = time.get("ENDTIME").toString();
                    Date parseBegin = sd.parse(begintime);
                    Date parseEnd = sd.parse(endtime);
                    DCP_ReserveTimeQuery_OpenRes.level3Elm lv3 = res.new level3Elm();
                    lv3.setBeginTime(begintime);
                    lv3.setEndTime(endtime);

                    String disabled = "";
                    // 1. 判断当前时间针对之前的时间段返回disabled==1；
                    int i1 = parse.compareTo(parseEnd);

                    // 默认用户所传日期 都是大于等于当天的
                    if(currentDate.equals(date)){
                        // 如果等于当天则判断当前时间是否超时
                        if (i1>=0) {
                            // 1.超过当前时间，（前端需置灰）
                            disabled = "1";
                        } else {
                            // 2. 根据itemsNo查询DCP_SERVICEITEMS.SERVICETIME，判断当前时间与结束时间差，若小于则disabled==2；
                            String itemsServiceTime = getItemsServiceTime(eId, itemsNo);
                            Calendar cal2 = Calendar.getInstance();
                            cal2.add(Calendar.MINUTE, Integer.parseInt(itemsServiceTime));
                            Date ttime = sd.parse(sd.format(cal2.getTime()));
                            int i = ttime.compareTo(parseEnd);
                            if(i>0){
                                disabled = "2";
                            }
                        }
                    }

                    // 4. 查询一下有无超过门店的最大预约人数
                    if(Check.Null(disabled)){
                        boolean b = checkMaxReserve(req, begintime + "-" + endtime);
                        if(b){
                            disabled = "4";
                        }
                    }

                    // 3.若opNo有传值时，查询【预约记录档】待opNo对应的消费总数，与DCP_ADVISORSET_SCHEDULING对应时间段内的APPOINTMENTS，
                    // 若消费总数≥APPOINTMENTS，则disabled==3；
                    if(!Check.Null(opNo)&&Check.Null(disabled)){
                        boolean b = checkCurrentTime(eId, shopId, opNo, begintime + "-" + endtime, date, currentDateWeek);
                        if(b){
                            disabled = "3";
                        }
                    }
                    lv3.setDisabled(disabled);
                    res.getDatas().getTimeList().add(lv3);
                }
            }

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ReserveTimeQuery_OpenReq req) throws Exception {
        return null;
    }

    /**
     * 查询当前门店最大可预约天数
     *
     * @param eId
     * @param conditionValues
     * @return
     */
    private int getAppointmentDays(String eId, String[] conditionValues) throws Exception {
        int appointmentdays = 7; // 默认7天
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_RESERVEPARAMETER WHERE eid = ? AND SHOPID = ? ");
        List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), conditionValues);
        if (!CollectionUtils.isEmpty(data)) {
            appointmentdays = Integer.parseInt(data.get(0).get("APPOINTMENTDAYS").toString());
        }
        return appointmentdays;
    }

    /**
     * 排除门店休息日
     *
     * @param dates
     * @param conditionValues
     * @return
     */
    private List<String> excludeOffDay(List<String> dates, String[] conditionValues) throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_RESERVEHOLIDAY WHERE eid = ? AND SHOPID  = ? ");
        List<Map<String, Object>> getQdata = this.doQueryData(sqlbuf.toString(), conditionValues);
        if (!CollectionUtils.isEmpty(getQdata)) {
            for (Map<String, Object> data : getQdata) {
                String beginDate = data.get("BEGINDATE").toString();
                String endDate = data.get("ENDDATE").toString();
                List<String> days = getDays(beginDate, endDate);
                dates = listrem(dates, days);
            }
        }
        return dates;
    }

    /**
     * 排除员工的休息日
     *
     * @param dates
     * @param conditionValues
     * @return
     */
    public  List<String>  excludeOffOpNoDay(List<String> dates, String[] conditionValues) throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_ADVISORSET_RESTTIME WHERE eid = ? AND SHOPID = ? AND OPNO = ? and status = '100' ");
        List<Map<String, Object>> getQdata = this.doQueryData(sqlbuf.toString(), conditionValues);
        List<String> days = new ArrayList<>();
        if (!CollectionUtils.isEmpty(getQdata)) {
            for (Map<String, Object> data : getQdata) {
                String cycleType = data.get("CYCLETYPE").toString();
                String restTime = data.get("RESTTIME").toString();
                if (!Check.Null(cycleType)) {
                    if (cycleType.equals("month")) {
                        // 获取当月的某天
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(restTime));
                        calendar1.add(Calendar.MONTH, 0);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = calendar1.getTime();
                        String format = sdf.format(date);
                        days.add(format);
                    } else if (cycleType.equals("custom")) {
                        // 自定义日期 yyyy-MM-dd
                        days.add(restTime);
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(days)) {
            dates = listrem(dates, days);
        }
        return dates;
    }


    /**
     * 查询当前周期的所有时间段
     *
     * @param conditionValues
     * @param currentDateWeek
     * @return
     */
    private List<Map<String, String>> getTimes(String[] conditionValues, String currentDateWeek) throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        List<Map<String, String>> times = null;
        sqlbuf.append("SELECT * FROM DCP_RESERVETIME WHERE eid = ? AND shopid = ?  and status = '100' ");
        List<Map<String, Object>> getQdata = this.doQueryData(sqlbuf.toString(), conditionValues);
        if (!CollectionUtils.isEmpty(getQdata)) {
            times = new ArrayList<>();
            for (Map<String, Object> data : getQdata) {
                String cycle = data.get("CYCLE").toString();
                List<String> cycles = JSONArray.parseArray(cycle, String.class);
                if (!CollectionUtils.isEmpty(cycles)) {
                    if (cycles.contains(currentDateWeek)) {
                        Map<String, String> map = new HashMap<>();
                        map.put("BEGINTIME", data.get("BEGINTIME").toString());
                        map.put("ENDTIME", data.get("ENDTIME").toString());
                        times.add(map);
                    }
                }
            }
        }
        return times;
    }

    /**
     * 获取当前项目的需要服务时长
     * @param eid
     * @param itemsno
     * @return
     */
    private String getItemsServiceTime(String eid,String itemsno) throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        String serviceTiem = "";
        List<Map<String, String>> times = null;
        sqlbuf.append("SELECT SERVICETIME FROM DCP_SERVICEITEMS WHERE eid = '"+eid+"' AND ITEMSNO = '"+itemsno+"' ");
        List<Map<String, Object>> getQdata = this.doQueryData(sqlbuf.toString(), null);
        if(!CollectionUtils.isEmpty(getQdata)){
            serviceTiem =  getQdata.get(0).get("SERVICETIME").toString();
        }
        return serviceTiem;
    }

    /**
     * 查询下当前时间段 该员工有无约满
     * @param eid
     * @param shopId
     * @param opNo
     * @param time
     * @return
     */
    private boolean checkCurrentTime(String eid,String shopId,String opNo,String time,String date,String week) throws Exception {
        boolean bool = false;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT COUNT(a.RESERVENO) as num FROM DCP_RESERVE a " +
                " WHERE a.EID  = '"+eid+"' AND a.SHOPID  = '"+shopId+"' AND a.OPNO  = '"+opNo+"' " +
                " AND a.BDATE  = to_date('"+date+"','yyyy-mm-dd' ) AND a.TIME = '"+time+"' AND a.STATUS in(0,1)");
        List<Map<String, Object>> getQdata = this.doQueryData(sqlbuf.toString(), null);
        String num = "";
        if(!CollectionUtils.isEmpty(getQdata)){
             num = getQdata.get(0).get("NUM").toString();
        }
        // 查询下该日期该时间段 员工的最大预约数
        sqlbuf.setLength(0);
        sqlbuf.append(" SELECT * FROM DCP_ADVISORSET_SCHEDULING WHERE eid= '"+eid+"' AND OPNO = '"+opNo+"' AND SHOPID = '"+shopId+"'  AND TIMEINTERVAL = '"+time+"'");
        List<Map<String, Object>> getdata = this.doQueryData(sqlbuf.toString(), null);
        String appointments = "";
        if(!CollectionUtils.isEmpty(getdata)){
            for (Map<String, Object> data : getdata) {
                String cycle = data.get("CYCLE").toString();
                List<String> cycles = JSONArray.parseArray(cycle, String.class);
                if (cycles.contains(week)) {
                     appointments = data.get("APPOINTMENTS").toString();
                }
            }
        }
        // 如果员工该时间段 预约单待服务人数 等于 员工该时间段最大预约数 则返回
        if(!Check.Null(appointments)){
            if(Integer.parseInt(num)>=Integer.parseInt(appointments)){
                bool = true;
            }
        }

        return bool;
    }

    /**
     * 获取过去或者未来 任意天内的日期数组
     *
     * @param intervals intervals天内
     * @return 日期数组
     */
    public static List<String> getDate(int intervals) {
        List<String> fetureDaysList = new ArrayList<>();
        for (int i = 0; i < intervals; i++) {
            fetureDaysList.add(getFetureDate(i));
        }
        return fetureDaysList;
    }

    /**
     * 获取未来 第 past 天的日期
     *
     * @param past
     * @return
     */
    public static String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取两个日期区间的日期
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getDays(String startTime, String endTime) {

        // 返回的日期集合
        List<String> days = new ArrayList<String>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }

    /**
     * 去掉另一个List 中的重复数据
     *
     * @param listA
     * @param listB
     * @return
     */
    public static List<String> listrem(List<String> listA, List<String> listB) {
        HashSet hs1 = new HashSet(listA);
        HashSet hs2 = new HashSet(listB);
        hs1.removeAll(hs2);
        List<String> listC = new ArrayList<String>();
        listC.addAll(hs1);
        return listC;
    }

    /**
     * 获取今天是周几
     *
     * @param week
     * @return
     */
    public String getWeek(int week) {
        String sweek = "";
        switch (week) {
            case 1:
                sweek = "周一";
                break;
            case 2:
                sweek = "周二";
                break;
            case 3:
                sweek = "周三";
                break;
            case 4:
                sweek = "周四";
                break;
            case 5:
                sweek = "周五";
                break;
            case 6:
                sweek = "周六";
                break;
            case 7:
                sweek = "周日";
                break;
        }
        return sweek;
    }

    /**
     * 对预约的时间段 进行排序返回
     * @param times
     * @param sortType
     * @return
     */
    private List<Map<String,String>> sortTimes(List<Map<String, String>> times ,String sortType){
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
            /// 时间排序方式 1. 按时开始时间升序排序 2.按照开始时间降序排序
            switch (sortType){
                case "1":  // 1. 开始时间升序排序
                    times.sort(new Comparator<Map<String, String>>() {
                        @SneakyThrows
                        @Override
                        public int compare(Map<String, String> o1, Map<String, String> o2) {
                            // 解决：Comparison method violates its general contract todo 需要测下
                            String d1 = o1.get("BEGINTIME").toString();
                            String d2 = o2.get("BEGINTIME").toString();
                            if (d1 == null && d2 == null) {
                                return 0;
                            }
                            return sd.parse(d1).compareTo(sd.parse(d2));
                        }
                    });
                    break;
                case "2":  // 2. 开始时间降序排序
                    times.sort(new Comparator<Map<String, String>>() {
                        @SneakyThrows
                        @Override
                        public int compare(Map<String, String> o1, Map<String, String> o2) {
                            // 解决：Comparison method violates its general contract todo 需要测下
                            String d1 = o1.get("BEGINTIME").toString();
                            String d2 = o2.get("BEGINTIME").toString();
                            if (d1 == null && d2 == null) {
                                return 0;
                            }
                            return sd.parse(d2).compareTo(sd.parse(d1));
                        }
                    });
                    break;
                default:
                    break;
            }

            return times;

    }

    /**
     * 判断同一时段 到店分配是否到最大值
     * @param req
     * @return
     */
    private boolean checkMaxReserve(DCP_ReserveTimeQuery_OpenReq req,String time) throws Exception {
        boolean bool = false;
        String eId = req.geteId();
        DCP_ReserveTimeQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String date = request.getDate();
        String itemsNo = request.getItemsNo();
        // 1. 查询下参数中有无控制 时段预约单有无限制
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_RESERVEPARAMETER WHERE eid  = '"+eId+"' AND shopid = '"+shopId+"'");
        List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), null);
        String appointments = "1"; // 默认到店最大分配预约数为1
        if(!CollectionUtils.isEmpty(data)){
            appointments = data.get(0).get("APPOINTMENTS").toString();
        }

        // 2.查询下当前预约单的时间段 预约人数
        sqlbuf.setLength(0);
        sqlbuf.append("select count(1) NUM from DCP_RESERVE where eid= '"+eId+"' and SHOPID = '"+shopId+"' and \"TIME\" = '"+time+"' and \"BDATE\" = to_date('"+date+"','YYYY-MM-DD') AND itemsNo = '"+itemsNo+"' AND status IN (0,1)");
        List<Map<String, Object>> getCount = this.doQueryData(sqlbuf.toString(), null);
        if(!CollectionUtils.isEmpty(getCount)){
            String num = getCount.get(0).get("NUM").toString();
            if(Integer.parseInt(num)>=Integer.parseInt(appointments)){
                bool = true;
            }
        }
        return bool;
    }



}
