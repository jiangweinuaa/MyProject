package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSONArray;
import com.dsc.spos.json.cust.req.DCP_AdvisorQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_AdvisorQuery_OpenRes;
import com.dsc.spos.scheduler.job.AbnormalOrderBatchProcess;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 顾问查询
 *
 * 总结：
 * 1、门店是必传的
 * 2、查询门店所有顾问相关信息(有顾问评分、排班情况等)
 * 3、根据评价指标标签查询门店顾问相关信息(有顾问评分、排班情况等)
 * 4、修改预约时，只是查询预约单的那个顾问相关信息(无顾问评分、排班情况，所以这个要简单化，加2个参数)
 * 5、增加参数isOnlyAdvisor是否只查顾问，不查评分、排班情况  Y/N
 * 6、增加参数advisorNo顾问编码，不传是门店所有顾问(POS上有只选顾问)
 *
 * @author: wangzyc
 * @create: 2021-08-02
 */
public class DCP_AdvisorQuery_Open extends SPosBasicService<DCP_AdvisorQuery_OpenReq, DCP_AdvisorQuery_OpenRes>
{
    Logger logger = LogManager.getLogger(DCP_AdvisorQuery_Open.class.getName());

    @Override
    protected boolean isVerifyFail(DCP_AdvisorQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_AdvisorQuery_OpenReq.level1Elm request = req.getRequest();

        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String shopId = req.getRequest().getShopId();

        if (Check.Null(shopId)) {
            errMsg.append("门店编号不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AdvisorQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_AdvisorQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_AdvisorQuery_OpenRes getResponseType() {
        return new DCP_AdvisorQuery_OpenRes();
    }

    @Override
    protected DCP_AdvisorQuery_OpenRes processJson(DCP_AdvisorQuery_OpenReq req) throws Exception {
        DCP_AdvisorQuery_OpenRes res = this.getResponseType();
        String eId = req.geteId();
        DCP_AdvisorQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String date = request.getDate();
        String beginTime = request.getBeginTime();
        String endTime = request.getEndTime();
        String itemsNo = request.getItemsNo();
        String labelId = request.getLabelId();
        //是否只查顾问，不查评分、排班情况  Y/N
        String isOnlyAdvisor = request.getIsOnlyAdvisor();
        if (Check.Null(isOnlyAdvisor))
        {
            isOnlyAdvisor="N";
        }
        //顾问编码，不传是查门店所有顾问
        String advisorNo = request.getAdvisorNo();
        if (Check.Null(advisorNo))
        {
            advisorNo="";
        }

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        int totalRecords = 0; // 总笔数
        int totalPages = 0;
        DCP_AdvisorQuery_OpenRes.level11Elm level11Elm = res.new level11Elm();
        res.setDatas(level11Elm);
        try
        {
            level11Elm.setOpList(new ArrayList<>());
            level11Elm.setLabelList(new ArrayList<>());
            /****************************************** 查询顾问信息 Begin *******************************/

            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记1");
            List<Map<String, Object>> opNos = getOpNos(eId, shopId, itemsNo,advisorNo);
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记2");

            if (!CollectionUtils.isEmpty(opNos))
            {
                //*************只查顾问标记处理**************
                if (!isOnlyAdvisor.equals("Y"))
                {
                    // 如果有传时间 则根据时间进行查看该时间段 该顾问是否有排班
                    if (!Check.Null(date) && !Check.Null(beginTime) && !Check.Null(endTime))
                    {
                        //过滤只返回顾问在工作时间段且非顾问休息日的
                        opNos = getOpNos(opNos, date, beginTime, endTime,req);
                    }
                }

                if (!CollectionUtils.isEmpty(opNos))
                {

                    String isHttps = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                    String httpStr = isHttps.equals("1") ? "https://" : "http://";
                    String domainName = PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                    if (domainName.endsWith("/"))
                    {
                        domainName = httpStr + domainName + "resource/image/";
                    }
                    else
                    {
                        domainName = httpStr + domainName + "/resource/image/";
                    }

                    //建立剩余的多顾问编号的with高效查询语句
                    StringBuffer sJoinOpno=new StringBuffer("");
                    for (Map<String, Object> opNo : opNos)
                    {
                        sJoinOpno.append(opNo.get("OPNO").toString()+",");
                    }
                    //
                    Map<String, String> mapOpno=new HashMap<String, String>();
                    mapOpno.put("OPNO", sJoinOpno.toString());
                    //
                    MyCommon cm=new MyCommon();
                    String withasSql_Opno=cm.getFormatSourceMultiColWith(mapOpno);
                    cm=null;
                    mapOpno=null;

                    StringBuffer sqlbuf=new StringBuffer();

                    List<Map<String, Object>> getQdata_HighPraise =null;
                    List<Map<String, Object>> getQdata_LabelHighPraise =null;

                    //如果传指定顾问，就不查指标评分，这个时候是修改预约，只是要顾问名称及头像资料
                    if (advisorNo.equals(""))
                    {
                        //1、这里是要所有标签指标平均分>3的
                        sqlbuf.setLength(0);
                        sqlbuf.append(" with a AS ( "
                                              + withasSql_Opno + " ) ");
                        sqlbuf.append("SELECT count(*) NUM ,NVL(sum(b.GRADE),0) GRADE,d.STAFFID OPNO FROM a " +
                                              " INNER JOIN CRM_COMMENTMAIN d ON a.OPNO=d.STAFFID " +
                                              " LEFT JOIN CRM_COMMENTTAGSHOP b ON  d.eid = b.EID  AND d.BILLNO  = b.BILLNO " +
                                              " INNER JOIN CRM_COMMENTTAGTYPE c on  d.EID  = c.eid  AND c.ATTR = '3' AND c.STATUS = '100' AND b.TAGTYPEID  = c.TAGTYPEID " +
                                              " WHERE d.eid = '" + eId + "' AND d.shopid = '" + shopId + "'  " +
                                              " group by d.STAFFID ");
                        getQdata_HighPraise = this.doQueryData(sqlbuf.toString(), null);

                        logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记8");

                    }

                    //2、这里是要单标签指标平均分>3的
                    sqlbuf.setLength(0);
                    if (!Check.Null(labelId))
                    {
                        sqlbuf.append(" with a AS ( "
                                              + withasSql_Opno + " ) ");
                        sqlbuf.append("SELECT count(*) NUM ,NVL(sum(b.GRADE),0) GRADE,d.STAFFID OPNO FROM a " +
                                              " INNER JOIN CRM_COMMENTMAIN d ON a.OPNO=d.STAFFID " +
                                              " LEFT JOIN CRM_COMMENTTAGSHOP b ON  d.eid = b.EID  AND d.BILLNO  = b.BILLNO " +
                                              " INNER JOIN CRM_COMMENTTAGTYPE c on  d.EID  = c.eid  AND c.ATTR = '3' AND c.STATUS = '100' AND b.TAGTYPEID  = c.TAGTYPEID " +
                                              " WHERE d.eid = '" + eId + "' AND d.shopid = '" + shopId + "'  " +
                                              " AND c.TAGTYPEID = '" + labelId + "' " +
                                              " group by d.STAFFID ");

                        getQdata_LabelHighPraise=this.doQueryData(sqlbuf.toString(), null);

                        logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记9");

                    }

                    //3、这里是要单标签指标每条评分>3的，跟上面的不一样
                    sqlbuf.setLength(0);
                    sqlbuf.append(" with a AS ( "
                                          + withasSql_Opno + " ) ");
                    sqlbuf.append(" SELECT d.STAFFID OPNO,c.TAGTYPEID ,c.TAGTYPENAME FROM a " +
                                          " INNER JOIN CRM_COMMENTMAIN d ON a.OPNO=d.STAFFID " +
                                          " LEFT JOIN CRM_COMMENTTAGSHOP b ON  d.eid = b.EID  AND d.BILLNO  = b.BILLNO " +
                                          " INNER JOIN CRM_COMMENTTAGTYPE c on  d.EID  = c.eid  AND c.ATTR = '3' AND c.STATUS = '100' AND b.TAGTYPEID  = c.TAGTYPEID " +
                                          " WHERE d.eid = '"+eId+"' AND b.GRADE >3  AND d.SHOPID  = '"+shopId+"' " +
                                          " group by d.STAFFID ,c.TAGTYPEID ,c.TAGTYPENAME " +
                                          " ");
                    List<Map<String, Object>> getQdata_OPLabel = this.doQueryData(sqlbuf.toString(), null);

                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记10");

                    // 查找用户的好评率
                    for (Map<String, Object> opNo : opNos)
                    {
                        String highPraise = "5";
                        String opno = opNo.get("OPNO").toString();
                        String opname = opNo.get("OPNAME").toString();
                        String ability = opNo.get("ABILITY").toString();
                        String professionalname = opNo.get("PROFESSIONALNAME").toString();
                        String professional = opNo.get("PROFESSIONAL").toString();
                        String headimage = opNo.get("HEADIMAGE").toString();

                        if (!Check.Null(headimage))
                        {
                            headimage = domainName + headimage;
                        }

                        //1、这里是要单标签指标平均分>3的
                        if (!Check.Null(labelId))
                        {
                            //如果传指定顾问，就不查指标评分，这个时候是修改预约，只是要顾问名称及头像资料
                            //所以这里不能跳出
                            if (advisorNo.equals(""))
                            {
                                //单标签指标平均分
                                String grade="";
                                if (!CollectionUtils.isEmpty(getQdata_LabelHighPraise))
                                {
                                    //过滤一下
                                    List<Map<String, Object>> op_LabelHighPraise_list = getQdata_LabelHighPraise.stream().filter(p->p.get("OPNO").toString().equals(opno)).collect(Collectors.toList());
                                    if (!CollectionUtils.isEmpty(op_LabelHighPraise_list))
                                    {
                                        // 总条数
                                        int num = Integer.parseInt(op_LabelHighPraise_list.get(0).get("NUM").toString());
                                        int num2 = Integer.parseInt(op_LabelHighPraise_list.get(0).get("GRADE").toString());

                                        if (num > 0)
                                        {
                                            // 创建一个数值格式化对象
                                            NumberFormat numberFormat = NumberFormat.getInstance();
                                            // 设置精确到小数点后2位,可以写0不带小数位
                                            numberFormat.setMaximumFractionDigits(2);
                                            grade = numberFormat.format((float) num2 / (float) num);
                                            BigDecimal bigDecimalpQty = new BigDecimal(grade);
                                            bigDecimalpQty = bigDecimalpQty.setScale(0, BigDecimal.ROUND_HALF_UP);
                                            int number = bigDecimalpQty.intValue();
                                            grade = number + "";
                                        }
                                    }

                                }

                                if (Check.Null(grade))
                                {
                                    continue;
                                }
                                else if (Integer.parseInt(grade) <= 3)
                                {
                                    continue;
                                }

                            }

                        }

                        //2、这里是要所有标签指标平均分>3的
                        String grade_total="";
                        if (!CollectionUtils.isEmpty(getQdata_HighPraise))
                        {
                            //过滤一下
                            List<Map<String, Object>> op_HighPraise_list = getQdata_HighPraise.stream().filter(p->p.get("OPNO").toString().equals(opno)).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(op_HighPraise_list))
                            {
                                // 总条数
                                int num = Integer.parseInt(op_HighPraise_list.get(0).get("NUM").toString());
                                int num2 = Integer.parseInt(op_HighPraise_list.get(0).get("GRADE").toString());

                                if (num > 0)
                                {
                                    // 创建一个数值格式化对象
                                    NumberFormat numberFormat = NumberFormat.getInstance();
                                    // 设置精确到小数点后2位,可以写0不带小数位
                                    numberFormat.setMaximumFractionDigits(2);
                                    grade_total = numberFormat.format((float) num2 / (float) num);
                                    BigDecimal bigDecimalpQty = new BigDecimal(grade_total);
                                    bigDecimalpQty = bigDecimalpQty.setScale(0, BigDecimal.ROUND_HALF_UP);
                                    int number = bigDecimalpQty.intValue();
                                    grade_total = number + "";
                                }
                            }

                        }
                        if (!Check.Null(grade_total))
                        {
                            highPraise = grade_total;
                        }

                        DCP_AdvisorQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                        lv1.setOpNo(opno);
                        lv1.setOpName(opname);
                        lv1.setAbility(ability);
                        lv1.setProfessionalName(professionalname);
                        lv1.setProfessionalId(professional);
                        lv1.setHeadImage(headimage);
                        lv1.setHighPraise(highPraise);

                        //3、这里是要单标签指标每条评分>3的，跟上面的不一样
                        lv1.setLabelList(new ArrayList<>());
                        if (!CollectionUtils.isEmpty(getQdata_OPLabel))
                        {
                            //过滤一下
                            List<Map<String, Object>> op_OPLabel_list = getQdata_OPLabel.stream().filter(p->p.get("OPNO").toString().equals(opno)).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(op_OPLabel_list))
                            {
                                for (Map<String, Object> oneData : op_OPLabel_list)
                                {
                                    DCP_AdvisorQuery_OpenRes.level3Elm lv3 = res.new level3Elm();
                                    String tagtypeid = oneData.get("TAGTYPEID").toString();
                                    if (Check.Null(tagtypeid))
                                    {
                                        continue;
                                    }
                                    lv3.setLabelId(tagtypeid);
                                    lv3.setLabelName(oneData.get("TAGTYPENAME").toString());
                                    lv1.getLabelList().add(lv3);
                                }
                            }
                        }
                        level11Elm.getOpList().add(lv1);
                    }

                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记11");

                }

            }
            /****************************************** 查询顾问信息 End *******************************/

            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记12");

            totalRecords = level11Elm.getOpList().size();
            // 算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            // 分页处理：skip从第几条记录开始，limit是取几条记录
            List<DCP_AdvisorQuery_OpenRes.level1Elm> collect = level11Elm.getOpList().stream().skip((pageNumber - 1) * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            //按opno排序
            Collections.sort(collect, new Comparator<DCP_AdvisorQuery_OpenRes.level1Elm>() {
                public int compare(DCP_AdvisorQuery_OpenRes.level1Elm o1, DCP_AdvisorQuery_OpenRes.level1Elm o2) {
                    String id1 = o1.getOpNo();
                    String id2 = o2.getOpNo();
                    return id1.compareTo(id2);
                }
            });
            //map转换选取字段
            List<String> opNoIds = level11Elm.getOpList().stream().map(DCP_AdvisorQuery_OpenRes.level1Elm::getOpNo).collect(Collectors.toList());

            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记13");

            if(!CollectionUtils.isEmpty(opNoIds))
            {
                //*************只查顾问标记处理**************
                if (!isOnlyAdvisor.equals("Y"))
                {
                    /****************************************** 查询顶部标签列表 Begin *******************************/

                    //建立剩余的多顾问编号的with高效查询语句
                    StringBuffer sJoinOpno=new StringBuffer("");
                    for (String opno : opNoIds)
                    {
                        sJoinOpno.append(opno +",");
                    }
                    //
                    Map<String, String> mapOpno=new HashMap<String, String>();
                    mapOpno.put("OPNO", sJoinOpno.toString());
                    //
                    MyCommon cm=new MyCommon();
                    String withasSql_Opno=cm.getFormatSourceMultiColWith(mapOpno);
                    cm=null;
                    mapOpno=null;

                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记14");


                    StringBuffer sqlbuf=new StringBuffer();
                    sqlbuf.append(" with a AS ( "
                                          + withasSql_Opno + " ) ");
                    sqlbuf.append(" SELECT c.TAGTYPEID ,c.TAGTYPENAME,count(*) NUM FROM a " +
                                          " INNER JOIN CRM_COMMENTMAIN d ON a.OPNO=d.STAFFID " +
                                          " LEFT JOIN CRM_COMMENTTAGSHOP b ON  d.eid = b.EID  AND d.BILLNO  = b.BILLNO " +
                                          " INNER JOIN CRM_COMMENTTAGTYPE c on  d.EID  = c.eid  AND c.ATTR = '3' AND c.STATUS = '100' AND b.TAGTYPEID  = c.TAGTYPEID " +
                                          " WHERE d.eid = '"+eId+"' AND b.GRADE >3  AND d.SHOPID  = '"+shopId+"' " +
                                          " group by c.TAGTYPEID ,c.TAGTYPENAME " +
                                          " order by NUM ");
                    List<Map<String, Object>> getQdata_TOPLabel = this.doQueryData(sqlbuf.toString(), null);

                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记15");

                    if (!CollectionUtils.isEmpty(getQdata_TOPLabel))
                    {
                        for (Map<String, Object> oneData : getQdata_TOPLabel)
                        {
                            DCP_AdvisorQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                            lv2.setLabelId(oneData.get("TAGTYPEID").toString()).
                                    setLabelName(oneData.get("TAGTYPENAME").toString()).
                                    setNumber(oneData.get("NUM").toString());
                            level11Elm.getLabelList().add(lv2);
                        }
                    }
                    sqlbuf.setLength(0);

                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记16");

                    /****************************************** 查询顶部标签列表 End *******************************/

                }

            }
            res.setDatas(level11Elm);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        catch (ParseException e)
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }

        logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记17");

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_AdvisorQuery_OpenReq req) throws Exception {
        return null;
    }

    /**
     * 查询门店所有顾问
     *
     * @param eId
     * @param shopId
     * @param itemsNo
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> getOpNos(String eId, String shopId, String itemsNo,String advisorNo ) throws Exception
    {
        List<Map<String, Object>> OpNos = null;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT DISTINCT a.Eid,a.shopId,a.OPNO ,b.OPNAME ,b.HEADIMAGE ,b.PROFESSIONAL ,d.PROFESSIONALNAME ,b.ABILITY " +
                              "FROM DCP_RESERVEADVISOR a " +
                              "INNER JOIN  DCP_ADVISORSET b ON a.EID  = b.EID  AND a.OPNO  = b.OPNO  AND b.STATUS = '100'" +
                              "LEFT JOIN DCP_PROFESSIONALGRADE d ON b.PROFESSIONAL  = d.PROFESSIONALID  " +
                              "where a.eid = '" + eId + "' and a.SHOPID = '" + shopId + "'");
        if (!Check.Null(itemsNo))
        {
            sqlbuf.append(" and a.ITEMSNO = '" + itemsNo + "'");
        }
        if (!Check.Null(advisorNo))
        {
            sqlbuf.append(" and a.OPNO = '" + advisorNo + "'");
        }
        OpNos = this.doQueryData(sqlbuf.toString(), null);
        return OpNos;
    }

    /**
     * 根据这3个条件过滤不满足条件的顾问:预约量 小于 设定量 且非顾问休息日期
     *
     * @param opNos
     * @return
     */
    private List<Map<String, Object>> getOpNos(List<Map<String, Object>> opNos, String date, String beginTime, String endTime,DCP_AdvisorQuery_OpenReq req) throws Exception
    {
        List<Map<String, Object>> list = null;
        if (!CollectionUtils.isEmpty(opNos))
        {
            list = new ArrayList<>();

            //建立多顾问编号的with高效查询语句
            StringBuffer sJoinOpno=new StringBuffer("");
            for (Map<String, Object> opNo : opNos)
            {
                sJoinOpno.append(opNo.get("OPNO").toString()+",");
            }
            //
            Map<String, String> mapOpno=new HashMap<String, String>();
            mapOpno.put("OPNO", sJoinOpno.toString());
            //
            MyCommon cm=new MyCommon();
            String withasSql_Opno=cm.getFormatSourceMultiColWith(mapOpno);
            mapOpno=null;

            //状态值
            Map<String, String> mapStatus=new HashMap<String, String>();
            mapStatus.put("STATUS", "0,1,");
            String withasSql_Status=cm.getFormatSourceMultiColWith(mapStatus);
            cm=null;
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记3");



            //1、这是已预约的数量
            StringBuffer sqlbuf=new StringBuffer(" with a AS ( "
                                                         + withasSql_Opno + " ) ");

            sqlbuf.append("SELECT count(*) NUM,b.OPNO from a " +
                                  "inner join DCP_RESERVE b  on a.OPNO=b.OPNO " +
                                  "inner join ("+withasSql_Status+") c on b.STATUS=c.STATUS " +
                                  "where  b.eid='"+req.geteId()+"' and b.shopid='"+req.getRequest().getShopId()+"' " +
                                  "and b.BDATE = to_date('" + date + "','yyyy-MM-dd') AND b.\"TIME\" = '" + beginTime + "-" + endTime + "' " +
                                  "group by b.OPNO ");
            List<Map<String, Object>> getQdata_reserve = this.doQueryData(sqlbuf.toString(), null);
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记4");


            //2、这是设定的最大预约量
            sqlbuf.setLength(0);
            sqlbuf.append(" with a AS ( "
                                  + withasSql_Opno + " ) ");
            sqlbuf.append("SELECT b.TIMEINTERVAL,b.CYCLE,b.APPOINTMENTS,b.OPNO FROM  DCP_ADVISORSET_SCHEDULING b " +
                                  "inner join a on a.OPNO=b.OPNO " +
                                  "WHERE b.eid = '"+req.geteId()+"' AND b.SHOPID = '"+req.getRequest().getShopId()+"' ");
            List<Map<String, Object>> getQdata_scheduling = this.doQueryData(sqlbuf.toString(), null);
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记5");

            //3、顾问休息日的设定(设定到日期)
            sqlbuf.setLength(0);
            sqlbuf.append(" with a AS ( "
                                  + withasSql_Opno + " ) ");
            sqlbuf.append("SELECT b.* FROM DCP_ADVISORSET_RESTTIME b " +
                                  "inner join a on a.OPNO=b.OPNO " +
                                  "WHERE b.eid = '"+req.geteId()+"' AND b.SHOPID = '"+req.getRequest().getShopId()+"' and status = '100' ");
            List<Map<String, Object>> getQdata_resttime = this.doQueryData(sqlbuf.toString(), null);
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记6");

            for (Map<String, Object> opNo : opNos)
            {
                //1、已预约量
                String opno = opNo.get("OPNO").toString();

                String num = "0";
                if (!CollectionUtils.isEmpty(getQdata_reserve))
                {
                    List<Map<String, Object>> op_reserve_list = getQdata_reserve.stream().filter(p->p.get("OPNO").toString().equals(opno)).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(op_reserve_list))
                    {
                        num = op_reserve_list.get(0).get("NUM").toString();
                    }
                }

                //此顾问时间段内是否还能预约标记
                boolean b_canReserve = false;

                //2、设定量与预约量对比
                if (!CollectionUtils.isEmpty(getQdata_scheduling))
                {
                    List<Map<String, Object>> op_scheduling_list = getQdata_scheduling.stream().filter(p->p.get("OPNO").toString().equals(opno)).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(op_scheduling_list))
                    {
                        for (Map<String, Object> data : op_scheduling_list)
                        {
                            String timeinterval = data.get("TIMEINTERVAL").toString();
                            String cycle = data.get("CYCLE").toString();
                            String appointments = "";
                            appointments = data.get("APPOINTMENTS").toString();
                            List<String> cycles = JSONArray.parseArray(cycle, String.class);
                            if (!CollectionUtils.isEmpty(cycles))
                            {
                                int week = 0;
                                if (!Check.Null(date))
                                {
                                    week = getWeek(date);
                                }
                                // 如果当前排班周期 含有当前预约日期
                                if (cycles.contains(week + ""))
                                {
                                    // 检查时间段是否 一致
                                    if (timeinterval.equals(beginTime + "-" + endTime))
                                    {
                                        if (!Check.Null(appointments))
                                        {
                                            // 如果当前顾问  当前时间段越满了 也排除掉
                                            if (Integer.parseInt(num) >= Integer.parseInt(appointments))
                                            {
                                                b_canReserve = false;
                                            }
                                            else
                                            {
                                                b_canReserve = true;
                                                break;//能约就行
                                            }
                                        }
                                        else
                                        {
                                            // 如果最大预约数为空 代表无限制
                                            b_canReserve = true;
                                            break;//能约就行
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

                //如果还能预约
                if (b_canReserve)
                {
                    //顾问此日期是休息日标记
                    boolean b_restTime=false;

                    //3、顾问休息日的设定(设定到日期)
                    if (!CollectionUtils.isEmpty(getQdata_resttime))
                    {
                        List<Map<String, Object>> op_resttime_list = getQdata_resttime.stream().filter(p->p.get("OPNO").toString().equals(opno)).collect(Collectors.toList());

                        if (!CollectionUtils.isEmpty(op_resttime_list))
                        {
                            for (Map<String, Object> data : op_resttime_list)
                            {
                                String cycleType = data.get("CYCLETYPE").toString();
                                String restTime = data.get("RESTTIME").toString();
                                if (!Check.Null(cycleType))
                                {
                                    if (cycleType.equals("month"))
                                    {
                                        // 获取当月的某天
                                        Calendar calendar1 = Calendar.getInstance();
                                        calendar1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(restTime));
                                        calendar1.add(Calendar.MONTH, 0);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        Date currentDate = calendar1.getTime();
                                        String format = sdf.format(currentDate);
                                        if (date.equals(format))
                                        {
                                            b_restTime = true;
                                            break;//是休息日就行
                                        }
                                    }
                                    else if (cycleType.equals("custom"))
                                    {
                                        // 自定义日期 yyyy-MM-dd
                                        if (date.equals(restTime))
                                        {
                                            b_restTime = true;
                                            break;//是休息日就行
                                        }
                                    }
                                }
                            }
                        }

                    }

                    //如果不是顾问休息日才算ok
                    if (!b_restTime)
                    {
                        list.add(opNo);
                    }

                }
            }
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_AdvisorQuery_Open时间标记7");
        }
        return list;
    }

    /**
     * 查看当前员工 在当前时间段是否预约满
     *
     * @param conditionValues
     * @param date
     * @param beginTime
     * @param endTime
     * @return
     * @throws Exception
     */
    private Boolean checkWorkDay(String[] conditionValues, String date, String beginTime, String endTime) throws Exception
    {
        boolean bool = false;

        //这是已预约数量
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT COUNT(a.RESERVENO) as num FROM DCP_RESERVE a " +
                              " WHERE a.EID  = '" + conditionValues[0] + "' AND a.SHOPID  = '" + conditionValues[1] + "' AND a.OPNO  = '" + conditionValues[2] + "' " +
                              " AND a.BDATE = to_date('" + date + "','yyyy-MM-dd') AND a.\"TIME\" = '" + beginTime + "-" + endTime + "' AND a.STATUS in(0,1)");
        List<Map<String, Object>> getQdata = this.doQueryData(sqlbuf.toString(), null);
        String num = "";
        if (!CollectionUtils.isEmpty(getQdata))
        {
            num = getQdata.get(0).get("NUM").toString();
        }

        sqlbuf.setLength(0);

        //这是设定最大预约量
        sqlbuf.append("SELECT TIMEINTERVAL,CYCLE,APPOINTMENTS FROM  DCP_ADVISORSET_SCHEDULING WHERE eid = ? AND SHOPID = ? AND OPNO = ?  ");
        List<Map<String, Object>> datas = this.doQueryData(sqlbuf.toString(), conditionValues);
        if (!CollectionUtils.isEmpty(datas))
        {
            for (Map<String, Object> data : datas)
            {
                String timeinterval = data.get("TIMEINTERVAL").toString();
                String cycle = data.get("CYCLE").toString();
                String appointments = "";
                appointments = data.get("APPOINTMENTS").toString();
                List<String> cycles = JSONArray.parseArray(cycle, String.class);
                if (!CollectionUtils.isEmpty(cycles))
                {
                    int week = 0;
                    if (!Check.Null(date))
                    {
                        week = getWeek(date);
                    }
                    // 如果当前排班周期 含有当前预约日期
                    if (cycles.contains(week + ""))
                    {
                        // 检查时间段是否 一致
                        if (timeinterval.equals(beginTime + "-" + endTime))
                        {
                            if (!Check.Null(appointments))
                            {
                                // 如果当前顾问  当前时间段越满了 也排除掉
                                if (Integer.parseInt(num) >= Integer.parseInt(appointments))
                                {
                                    bool = false;
                                }
                                else
                                {
                                    bool = true;
                                }
                            }
                            else
                            {
                                // 如果最大预约数为空 代表无限制
                                bool = true;
                            }
                        }
                    }
                }
            }
        }
        return bool;
    }

    /**
     * 查询下当前时间段 该员工有无约满
     *
     * @param eid
     * @param shopId
     * @param opNo
     * @param time
     * @return
     */
    private boolean checkCurrentTime(String eid, String shopId, String opNo, String time, String date, String week) throws Exception {
        boolean bool = false;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT COUNT(a.RESERVENO) as num FROM DCP_RESERVE a " +
                              " WHERE a.EID  = '" + eid + "' AND a.SHOPID  = '" + shopId + "' AND a.OPNO  = '" + opNo + "' " +
                              " AND a.BDATE  = '" + date + "' AND a.TIME = '" + time + "' AND a.STATUS in(0,1)");
        List<Map<String, Object>> getQdata = this.doQueryData(sqlbuf.toString(), null);
        String num = "";
        if (!CollectionUtils.isEmpty(getQdata)) {
            num = getQdata.get(0).get("NUM").toString();
        }
        // 查询下该日期该时间段 员工的最大预约数
        sqlbuf.setLength(0);
        sqlbuf.append(" SELECT * FROM DCP_ADVISORSET_SCHEDULING WHERE eid= '" + eid + "' AND OPNO = '" + opNo + "' AND SHOPID = '" + shopId + "'  AND TIMEINTERVAL = '" + time + "'");
        List<Map<String, Object>> getdata = this.doQueryData(sqlbuf.toString(), null);
        String appointments = "";

        if (!CollectionUtils.isEmpty(getdata)) {
            for (Map<String, Object> data : getdata) {
                String cycle = data.get("CYCLE").toString();
                List<String> cycles = JSONArray.parseArray(cycle, String.class);
                if (cycles.contains(week)) {
                    appointments = data.get("APPOINTMENTS").toString();
                }
            }
        }
        // 20210824 add 顾问 某一时间段最大预约数 如果为空 则不限制最大预约数
        if (!Check.Null(appointments)) {
            // 如果员工该时间段 预约单待服务人数 等于 员工该时间段最大预约数 则返回
            if (Integer.parseInt(num) >= Integer.parseInt(appointments)) {
                bool = true;
            }
        }

        return bool;
    }

    /**
     * 排除掉员工的休息日
     *
     * @param conditionValues
     * @param date
     * @return
     * @throws Exception
     */
    private Boolean excludeOffOpNoDay(String[] conditionValues, String date) throws Exception
    {
        boolean bool = false;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_ADVISORSET_RESTTIME WHERE eid = ? AND SHOPID = ? AND OPNO = ? and status = '100' ");
        List<Map<String, Object>> getQdata = this.doQueryData(sqlbuf.toString(), conditionValues);
        if (!CollectionUtils.isEmpty(getQdata))
        {
            for (Map<String, Object> data : getQdata)
            {
                String cycleType = data.get("CYCLETYPE").toString();
                String restTime = data.get("RESTTIME").toString();
                if (!Check.Null(cycleType))
                {
                    if (cycleType.equals("month"))
                    {
                        // 获取当月的某天
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(restTime));
                        calendar1.add(Calendar.MONTH, 0);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date currentDate = calendar1.getTime();
                        String format = sdf.format(currentDate);
                        if (date.equals(format))
                        {
                            bool = true;
                        }
                    }
                    else if (cycleType.equals("custom"))
                    {
                        // 自定义日期 yyyy-MM-dd
                        if (date.equals(restTime))
                        {
                            bool = true;
                        }
                    }
                }
            }
        }
        return bool;
    }

    /**
     * 查找当前员工的星级分数
     *
     * @param eid
     * @param shopId
     * @param opNo   highPraise计算逻辑：
     *               1）根据opNo匹配CRM_COMMENTMAIN.STAFFID，获取单号BILLNO；
     *               2）查询CRM_COMMENTTAGTYPE.ATTR==3的TAGTYPEID；
     *               3）根据上述2个条件，查询所有CRM_COMMENTTAGSHOP.GRADE，3分及以上的几位好评；
     *               4）统计所有3分及以上的条数与总条数的百分比并返回，若为空则默认返回100%，例：100%
     *               2022/01/19 修改 原返回百分比，现返回分数(1~5)
     * @return
     */
    private String getHighPraise(String eid, String shopId, String opNo, String labelId) throws Exception {
        // 默认为满分
        //AND a.billdate between trunc(sysdate)-90 and trunc(sysdate)
        String highPraise = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT count(1) num ,NVL(sum(b.GRADE),0) GRADE FROM CRM_COMMENTMAIN a " +
                              " LEFT JOIN CRM_COMMENTTAGSHOP b ON  a.eid = b.EID  AND a.BILLNO  = b.BILLNO " +
                              " INNER JOIN CRM_COMMENTTAGTYPE c on  a.EID  = c.eid  AND c.ATTR = '3' AND c.STATUS = '100' AND b.TAGTYPEID  = c.TAGTYPEID " +
                              " WHERE a.eid = '" + eid + "' AND shopid = '" + shopId + "' AND a.STAFFID = '" + opNo + "' ");
        if (!Check.Null(labelId))
        {
            sqlbuf.append(" AND c.TAGTYPEID = '" + labelId + "'");
        }
        List<Map<String, Object>> getQdata = this.doQueryData(sqlbuf.toString(), null);
        if (!CollectionUtils.isEmpty(getQdata)) {
            // 总条数
            int num = Integer.parseInt(getQdata.get(0).get("NUM").toString());
            int num2 = Integer.parseInt(getQdata.get(0).get("GRADE").toString());

            if (num > 0)
            {
                // 创建一个数值格式化对象
                NumberFormat numberFormat = NumberFormat.getInstance();
                // 设置精确到小数点后2位,可以写0不带小数位
                numberFormat.setMaximumFractionDigits(2);
                highPraise = numberFormat.format((float) num2 / (float) num);
                BigDecimal bigDecimalpQty = new BigDecimal(highPraise);
                bigDecimalpQty = bigDecimalpQty.setScale(0, BigDecimal.ROUND_HALF_UP);
                int number = bigDecimalpQty.intValue();
                highPraise = number + "";
            }

        }
        return highPraise;
    }

    /**
     * 查询顾问的评价标签列表,评分>3
     *
     * @return
     */
    private String getOplabelList() {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT DISTINCT a.STAFFID ,c.TAGTYPEID ,c.TAGTYPENAME FROM CRM_COMMENTMAIN a " +
                              " LEFT JOIN CRM_COMMENTTAGSHOP b ON  a.eid = b.EID  AND a.BILLNO  = b.BILLNO " +
                              " INNER JOIN CRM_COMMENTTAGTYPE c on  a.EID  = c.eid  AND c.ATTR = '3' AND c.STATUS = '100' AND b.TAGTYPEID  = c.TAGTYPEID " +
                              " WHERE a.eid = ? AND b.GRADE >3  AND a.SHOPID  = ? AND a.STAFFID = ? ");
        return sqlbuf.toString();
    }


    /**
     * 查询顶部标签列表
     *
     * @return
     */
    private String getlabelList(List<String> opNos)
    {
        String opNostr = "";
        StringBuffer sqlbuf = new StringBuffer();
        if (!CollectionUtils.isEmpty(opNos))
        {
            opNostr = PosPub.getArrayStrSQLIn(opNos.toArray(new String[opNos.size()]));
        }
        sqlbuf.append(" SELECT TAGTYPEID, TAGTYPENAME, count(1) AS num FROM (" +
                              " SELECT  DISTINCT a.STAFFID ,b.TAGTYPEID ,c.TAGTYPENAME  FROM CRM_COMMENTMAIN a " +
                              " LEFT JOIN CRM_COMMENTTAGSHOP b ON  a.eid = b.EID  AND a.BILLNO  = b.BILLNO " +
                              " INNER JOIN CRM_COMMENTTAGTYPE c on  a.EID  = c.eid  AND c.ATTR = '3' AND c.STATUS = '100' AND b.TAGTYPEID  = c.TAGTYPEID " +
                              " WHERE a.eid = ?  AND b.GRADE >3 AND a.STAFFID IS not NULL AND a.SHOPID = ? ");
        if(!Check.Null(opNostr))
        {
            sqlbuf.append(" AND a.STAFFID in ("+opNostr+")");
        }
        sqlbuf.append(") GROUP BY TAGTYPEID ,TAGTYPENAME " +
                              " ORDER BY num");
        return sqlbuf.toString();
    }

    /**
     * 获取当前日期 是周几
     *
     * @param date yyyy-MM-dd
     * @return [1, 2, 3, 4, 5, 6, 7]
     */
    public static int getWeek(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = sdf.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index <= 0) {
            week_index = 7;
        }
        return week_index;
    }

}
