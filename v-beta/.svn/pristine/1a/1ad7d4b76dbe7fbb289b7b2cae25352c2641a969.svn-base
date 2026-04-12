package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.json.cust.req.DCP_KitchenDishQuery_OpenReq;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_KitchenDishQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: KDS画面待分配/待制作查询
 * @author: wangzyc
 * @create: 2021-09-16
 */
public class DCP_KitchenDishQuery_Open extends SPosBasicService<DCP_KitchenDishQuery_OpenReq, DCP_KitchenDishQuery_OpenRes>
{
    Logger logger = LogManager.getLogger(DCP_KitchenDishQuery_Open.class.getName());


    @Override
    protected boolean isVerifyFail(DCP_KitchenDishQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_KitchenDishQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("机台编号不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KitchenDishQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_KitchenDishQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_KitchenDishQuery_OpenRes getResponseType() {
        return new DCP_KitchenDishQuery_OpenRes();
    }

    @Override
    protected DCP_KitchenDishQuery_OpenRes processJson(DCP_KitchenDishQuery_OpenReq req) throws Exception {
        DCP_KitchenDishQuery_OpenRes res = this.getResponseType();
        DCP_KitchenDishQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String machineId = request.getMachineId();
        String queryType = request.getQueryType();
        if(Check.Null(queryType)){
            queryType = "0";
        }
        String eId = req.geteId();
        int pageSize = req.getPageSize();
        int pageNumber = req.getPageNumber();

        int totalRecords = 0; // 总笔数
        int totalPages = 0;

        res.setDatas(new ArrayList<>());
        try
        {
            // 查询系统参数 KdsProduceQty(最大可制作份数)
            String strKdsProduceQty = PosPub.getPARA_SMS(dao, eId, shopId, "KdsProduceQty");

            if (PosPub.isNumericType(strKdsProduceQty)==false)
            {
                strKdsProduceQty="4";
            }

            String sql = this.getIsOverTime(req);
            List<Map<String, Object>> getIsOverTime = this.doQueryData(sql, null);
            String IsOverTime = ""; // 超时预警时间
            if (!CollectionUtils.isEmpty(getIsOverTime)) {
                IsOverTime = getIsOverTime.get(0).get("OVERTIME").toString();
            }

            String cook_sql = this.getCook();
            List<Map<String, Object>> getCooks = this.doQueryData(cook_sql, new String[]{eId, shopId});

            //举例：预制菜品A,分3次加工任务单(2+2+3)，拆分到DCP_PROCESSTASK_DETAIL是7条数量为1的记录
            //strKdsProduceQty单次生产量为4，则要拆成2条记录(4+3),加工任务单号不同
            sql = this.getQuerySql(req);
            List<Map<String, Object>> getPlunoDetails = this.doQueryData(sql, null);


            // 这里就是查出7个A商品，通过品号信息过滤去重为1个A商品，下面再挂多单据
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("PLUNO", true);
            condition.put("PUNIT", true);
            condition.put("FLAVORSTUFFDETAIL", true);
            condition.put("PLUMEMO", true);
            condition.put("PLUNAME", true);
            condition.put("PLUBARCODE", true);
            condition.put("SPECATTRDETAIL", true);
            condition.put("COOKID", true);//相同2个商品，一个分配机器人了，一个没分配，是不能汇总的，否则机器人字段有值，就不分配了

            // 调用过滤函数
            List<Map<String, Object>> getHeader = MapDistinct.getMap(getPlunoDetails, condition);

            if (!CollectionUtils.isEmpty(getHeader))
            {

                for (Map<String, Object> header : getHeader)
                {
                    List<DCP_KitchenDishQuery_OpenRes.level1Elm> lists = new ArrayList<>();
                    String pluno = header.get("PLUNO").toString();
                    String punit = header.get("PUNIT").toString();
                    String flavorstuffdetail = header.get("FLAVORSTUFFDETAIL").toString();
                    String pluMemo = header.get("PLUMEMO").toString();
                    String plubarcode = header.get("PLUBARCODE").toString();
                    String cookid = header.get("COOKID").toString();


                    for (Map<String, Object> plunoDetail : getPlunoDetails)
                    {
                        String pluno2 = plunoDetail.get("PLUNO").toString();
                        String billno = plunoDetail.get("BILLNO").toString();
                        String punit2 = plunoDetail.get("PUNIT").toString();
                        String flavorstuffdetail2 = plunoDetail.get("FLAVORSTUFFDETAIL").toString();
                        String pluMemo2 = plunoDetail.get("PLUMEMO").toString();
                        String plubarcode2 = plunoDetail.get("PLUBARCODE").toString();
                        String cookid2 = plunoDetail.get("COOKID").toString();

                        if (pluno2.equals(pluno) && punit2.equals(punit) && flavorstuffdetail2.equals(flavorstuffdetail) && pluMemo2.equals(pluMemo) && plubarcode.equals(plubarcode2) && cookid.equals(cookid2))
                        {
                            String strPqty = plunoDetail.get("PQTY").toString(); // 大改  之前是多份  全部改成了单份
                            //将小数位设置为0，且设置四舍五入
                            BigDecimal bigDecimalpQty = new BigDecimal(strPqty);

                            //向上取整处理，这里取整是为了前端显示整份做，剩余的数量在制作完成服务扣减后自动产生预制单
                            bigDecimalpQty=bigDecimalpQty.setScale(0,BigDecimal.ROUND_UP);

                            if (CollectionUtils.isEmpty(lists))
                            {
                                DCP_KitchenDishQuery_OpenRes.level1Elm level1Elm = res.new level1Elm();
                                level1Elm = fillData(res, level1Elm, plunoDetail, bigDecimalpQty.toPlainString(), strKdsProduceQty, IsOverTime);
                                lists.add(level1Elm);
                            }
                            else
                            {
                                DCP_KitchenDishQuery_OpenRes.level1Elm lv1 = lists.get(lists.size() - 1);
                                String qty = lv1.getQty();
                                //大于等于最大制作量就单独切分一笔商品汇总，qty都是被拆成数量1或0.5的记录,strKdsProduceQty是单次制作4份
                                if (new BigDecimal(qty).add(bigDecimalpQty).compareTo(new BigDecimal(strKdsProduceQty))>0)
                                {
                                    DCP_KitchenDishQuery_OpenRes.level1Elm level1Elm = res.new level1Elm();
                                    fillData(res, level1Elm, plunoDetail, bigDecimalpQty.toPlainString(), strKdsProduceQty, IsOverTime);
                                    lists.add(level1Elm);
                                }
                                else
                                {
                                    //累加数量sumQty
                                    BigDecimal sumQty = new BigDecimal(qty).add(bigDecimalpQty);
                                    lv1.setQty(sumQty + "");
                                    DCP_KitchenDishQuery_OpenRes.level2Elm level2Elm1 = fillOrderData(res, plunoDetail, bigDecimalpQty.toPlainString(), IsOverTime);
                                    lv1.getOrderList().add(level2Elm1);
                                }
                            }
                        }
                    }

                    res.getDatas().addAll(lists);
                }
            }

            //1001 20230913154030
            //1002 20230913154130
            //1001 20230913154230
            //上面的商品显示顺序是有问题的，因为虽然SQL是按照来单时间排序了，
            //但是相同商品，根据商品去重后，后来的那个相同商品也因为去重跑前面了
            if (!CollectionUtils.isEmpty(res.getDatas()))
            {
                //按指定字段排序，默认升序
                Collections.sort(res.getDatas(), Comparator.comparing((h)->{return h.getSorttime();}));
            }

            //上面还是只填充到1条商品1个orderlist记录，下面根据加工任务单号+原单号进行汇总重算
            int item = -1;
            //取出没在炒菜中的机器，进行分配
            List<Map<String, Object>> getCook_noCookstatus=getCooks.stream().filter(c->Convert.toStr(c.get("COOKSTATUS"),"0").equals("0")).collect(Collectors.toList());
            for (DCP_KitchenDishQuery_OpenRes.level1Elm data : res.getDatas())
            {
                List<Integer> someTimes = new ArrayList<>();
                List<DCP_KitchenDishQuery_OpenRes.level2Elm> orders = new ArrayList<>();
                Set<String> billNos = new HashSet<>();

                if ("1".equals(queryType) && !CollectionUtils.isEmpty(getCook_noCookstatus))
                {
                    //加工任务明细表里的商品没分配机器人的，再进行分配，分配好的上面会有值的，不用重新分配,同时会打上已分配的商品标签
                    if (Check.Null(data.getCookId()))
                    {
                        //3、机器人状态为1：炒菜中，不能分配
                        //1、分满了就给下一个机器人，不能总是从第一个开始,找下一个没满的，都满了就从头来
                        //2、虽然机器人未满，但是跟机器人已存在的品号不同，也找下个没满的机器人
                        boolean b_find_noFull=false;
                        //从未满中找出1个+品号不同，取出索引值
                        List<Map<String, Object>> tempCook_noFull=getCook_noCookstatus.stream().filter(p-> Convert.toStr(p.get("FULL_CST"),"N").equals("N") && (Convert.toStr(p.get("PLUNO_CST"),"").equals(data.getPluNo()) ||Convert.toStr(p.get("PLUNO_CST"),"").equals("")  )).collect(Collectors.toList());
                        if (tempCook_noFull != null && tempCook_noFull.size()>0)
                        {
                            for (int i = 0; i < getCook_noCookstatus.size(); i++)
                            {
                                if(getCook_noCookstatus.get(i).get("COOKID").toString().equals(tempCook_noFull.get(0).get("COOKID").toString()))
                                {
                                    //未满的机器人
                                    item=i;
                                    b_find_noFull=true;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            item = -1;//没有未满且相同菜品的，先不分配
                        }
                    }
                    else
                    {
                        //有机器人的，给机器人打个品号标签字段
                        for (int i = 0; i < getCook_noCookstatus.size(); i++)
                        {
                            if(getCook_noCookstatus.get(i).get("COOKID").toString().equals(data.getCookId()))
                            {
                                //置品号标签
                                getCook_noCookstatus.get(i).put("PLUNO_CST",data.getPluNo());

                                //返回机器人状态
                                data.setCookStatus(Convert.toStr(getCook_noCookstatus.get(i).get("COOKSTATUS"),"0"));

                                break;
                            }
                        }
                    }

                    //分配满，要打标记
                    if (new BigDecimal(data.getSingleMakeQty()).compareTo(new BigDecimal(data.getQty()))==0)
                    {
                        for (int i = 0; i < getCook_noCookstatus.size(); i++)
                        {
                            if(getCook_noCookstatus.get(i).get("COOKID").toString().equals(data.getCookId()))
                            {
                                //置满
                                getCook_noCookstatus.get(i).put("FULL_CST","Y");
                                break;
                            }
                        }
                    }

                }

                StringBuffer trNoSum = new StringBuffer(""); // 机器人模式下需拼接
                Integer type0 = 0; // 堂食数量
                Integer type1 = 0; // 打包数量
                Integer type2 = 0; // 外卖数量
                Integer type3 = 0; // 预制数量
                for (DCP_KitchenDishQuery_OpenRes.level2Elm level2Elm : data.getOrderList())
                {
                    String someTime = level2Elm.getTimeInfo().getSomeTime();
                    String trNo = level2Elm.getTrNo();
                    String repastType = level2Elm.getRepastType();
                    String isBefore = level2Elm.getIsBefore();
                    if ("Y".equals(isBefore)) {
                        type3++;
                    } else if ("0".equals(repastType)) {
                        type0++;
                    } else if ("1".equals(repastType)) {
                        type1++;
                    } else if ("2".equals(repastType)) {
                        type2++;
                    }
                    if (!Check.Null(trNo))
                    {
                        trNoSum.append(trNo + "/");
                    }

                    someTimes.add(Integer.parseInt(someTime));
                    String billNo = level2Elm.getBillNo();
                    String processTaskNo = level2Elm.getProcessTaskNo();


                    //分配机器人后，要更新加工任务明细中的此菜品(待制作状态1的)
                    if ("1".equals(queryType) && !CollectionUtils.isEmpty(getCook_noCookstatus))
                    {
                        //加工任务明细表里的商品没分配机器人的，再进行分配，分配好的上面会有值的，不用重新分配
                        if (Check.Null(data.getCookId()) && item!=-1)
                        {
                            for (DCP_KitchenDishQuery_OpenRes.level4Elm level4Elm : level2Elm.getItemList())
                            {
                                String cookid = getCook_noCookstatus.get(item).get("COOKID").toString();
                                String cookName = getCook_noCookstatus.get(item).get("COOKNAME").toString();
                                // values
                                Map<String, DataValue> values = new HashMap<String, DataValue>();
                                values.put("COOKID",  new DataValue(cookid, Types.VARCHAR));
                                values.put("COOKNAME",  new DataValue(cookName, Types.VARCHAR));

                                // condition
                                Map<String, DataValue> conditions = new HashMap<String, DataValue>();
                                conditions.put("EID", new DataValue(eId, Types.VARCHAR));
                                conditions.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                conditions.put("PROCESSTASKNO", new DataValue(processTaskNo, Types.VARCHAR));
                                if (!Check.Null(billNo))
                                {
                                    conditions.put("OFNO", new DataValue(billNo, Types.VARCHAR));
                                }
                                conditions.put("PLUNO", new DataValue(data.getPluNo(), Types.VARCHAR));
                                conditions.put("ITEM", new DataValue(level4Elm.getItem(), Types.VARCHAR));

                                //
                                StaticInfo.dao.update("dcp_processtask_detail",values,conditions);
                            }

                        }

                    }

                    //相同加工任务单和原单的合并到1个orderlist里
                    if (CollectionUtils.isEmpty(orders))
                    {
                        orders.add(level2Elm);
                        billNos.add(processTaskNo+"_"+billNo);
                    }
                    else
                    {
                        if (billNos.contains(processTaskNo+"_"+billNo))
                        {
                            for (DCP_KitchenDishQuery_OpenRes.level2Elm order : orders)
                            {
                                if (order.getProcessTaskNo().equals(processTaskNo) && order.getBillNo().equals(billNo))
                                {
                                    BigDecimal sumQty = new BigDecimal(order.getQty()).add(new BigDecimal(level2Elm.getQty()));
                                    order.setQty(sumQty.toPlainString());
                                    order.getItemList().add(level2Elm.getItemList().get(0));
                                }
                            }
                        }
                        else
                        {
                            orders.add(level2Elm);
                            billNos.add(processTaskNo+"_"+billNo);
                        }
                    }
                }

                //分配机器人后，要更新加工任务明细中的此菜品(待制作状态1的)
                if ("1".equals(queryType) && !CollectionUtils.isEmpty(getCook_noCookstatus))
                {
                    //加工任务明细表里的商品没分配机器人的，再进行分配，分配好的上面会有值的，不用重新分配
                    if (Check.Null(data.getCookId()) && item!=-1)
                    {
                        String cookid = getCook_noCookstatus.get(item).get("COOKID").toString();
                        String cookName = getCook_noCookstatus.get(item).get("COOKNAME").toString();
                        data.setCookId(cookid);
                        data.setCookName(cookName);
                        //置品号标签
                        getCook_noCookstatus.get(item).put("PLUNO_CST",data.getPluNo());

                        //返回机器人状态
                        data.setCookStatus(Convert.toStr(getCook_noCookstatus.get(item).get("COOKSTATUS"),"0"));
                    }
                }


                //相同加工任务单和原单的合并到1个orderlist里
                data.setOrderList(orders);
                someTimes.sort((a, b) -> b.compareTo(a));
                data.setSomeTime(someTimes.get(0) + "");
                if (trNoSum.length() != 0)
                {
                    trNoSum.deleteCharAt(trNoSum.length() - 1).toString();
                }
                data.setTrNo(trNoSum.toString());
                String repastNum = "";
                repastNum = getRepastNum(type0, type1, type2, type3);
                data.setRepastNum(repastNum);

            }

            if (!CollectionUtils.isEmpty(res.getDatas()))
            {
                totalRecords = res.getDatas().size();
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            }


            if ("0".equals(queryType))
            {
                // 分页
                List<DCP_KitchenDishQuery_OpenRes.level1Elm> collect = res.getDatas().stream().skip((pageNumber - 1) * pageSize)
                        .limit(pageSize)
                        .collect(Collectors.toList());
                res.setDatas(collect);
            }
            else if ("1".equals(queryType))
            {
                // 机器人模式下 按照机器人数量返回，每个机器人只返回一条，还没分配菜的机器人不不返回
                List<DCP_KitchenDishQuery_OpenRes.level1Elm> cooksResults=new ArrayList<>();
                for (int i = 0; i < getCooks.size(); i++)
                {
                    String v_Cookid=getCooks.get(i).get("COOKID").toString();
                    Optional<DCP_KitchenDishQuery_OpenRes.level1Elm> v_First=res.getDatas().stream().filter(c->v_Cookid.equals(c.getCookId())).findFirst();
                    //存在才取
                    if (v_First.isPresent())
                    {
                        //状态加进去
                        v_First.get().setCookStatus(getCooks.get(i).get("COOKSTATUS").toString());
                        cooksResults.add(v_First.get());
                    }
                }
                //重新赋值根据机器人返回结果
                res.setDatas(cooksResults);
            }

            if (!CollectionUtils.isEmpty(res.getDatas()))
            {
                totalRecords = res.getDatas().size();
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            }


            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
        }
        catch (Exception e)
        {
            String sqlerr= "";

            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                sqlerr= e.getCause()==null || e.getCause().getMessage()==null?"":e.getCause().getMessage();
                if (Check.Null(sqlerr))  sqlerr=errors.toString();


                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******DCP_KitchenDishQuery_Open报错信息1:" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******DCP_KitchenDishQuery_Open报错信息2:" + e1.getMessage() + "******\r\n");
            }

            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + sqlerr);
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    /**
     * 查询所有商品的信息
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Override
    protected String getQuerySql(DCP_KitchenDishQuery_OpenReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        DCP_KitchenDishQuery_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        List<String> goodsStatus = request.getGoodsStatus();
        String categoryQuery = request.getCategoryQuery();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.* , CASE  WHEN DINNERDATETime IS NOT NULL THEN DINNERDATETime WHEN REPASTTYPE = 2 THEN SHIPENDTIME WHEN a.otype = 'BEFORE' " +
                              " THEN CREATEDATETIME WHEN (REPASTTYPE = 1 OR REPASTTYPE = 0) AND a.otype != 'BEFORE' THEN ORDERTIME END AS sorttime  FROM (" +
                              " SELECT b.PROCESSTASKNO, b.PLUNO, b.PLUNAME, b.PQTY ,NVL(e.MAKETIME ,b.MAKETIME) MAKETIME, b.PLUBARCODE , b.FLAVORSTUFFDETAIL, b.MEMO AS pluMEMo, " +
                              " CASE WHEN b.SPECNAME IS NOT NULL AND b.ATTRNAME IS NULL THEN '(' || b.SPECNAME  || ')'  " +
                              "  WHEN b.SPECNAME IS  NULL AND b.ATTRNAME IS NOT NULL  THEN '('  || b.ATTRNAME || ')'  " +
                              "  WHEN b.SPECNAME IS not NULL AND b.ATTRNAME IS NOT NULL THEN '(' || b.SPECNAME || ',' || b.ATTRNAME || ')' END  AS specAttrDetail ," +
                              "  b.PUNIT, d.UNAME , b.ISPACKAGE, b.PGOODSDETAIL, b.GOODSSTATUS, b.BDATE, e.ORDERTIME , NVL(e.LOADDOCTYPE,a.LOADDOCTYPE) LOADDOCTYPE,e.CHANNELID ," +
                              " f.CHANNELNAME AS CHANNELNAME, e.APPTYPE, e.BILLNO, e.TRNO , e.TABLENO, e.GUESTNUM , CASE  WHEN e.REPASTTYPE IS NULL AND " +
                              " a.OTYPE != 'BEFORE' AND (e.LOADDOCTYPE = 'WAIMAI' OR e.LOADDOCTYPE = 'MEITUAN' OR e.LOADDOCTYPE = 'ELEME') THEN 2 ELSE  " +
                              " TO_NUMBER(e.REPASTTYPE) END AS REPASTTYPE, e.MEMO, b.ISURGE, e.SHIPENDTIME, a.otype , NVL(e.ORDERTIME,SUBSTR(a.CREATEDATETIME, 1, 14))  CREATEDATETIME," +
                              " b.ITEM,e.DINNERDATE ||e.DINNERTIME  DINNERDATETime,h.ISPRINTCROSSMENU hISPRINTCROSSMENU,h.CROSSPRINTERNAME hCROSSPRINTERNAME,i.ISPRINTCROSSMENU iISPRINTCROSSMENU,i.CROSSPRINTERNAME iCROSSPRINTERNAME," +
                              " b.COOKID,b.COOKNAME,nvl(e.isbook,'N') isbook " +
                              " FROM DCP_PROCESSTASK a " +
                              " LEFT JOIN DCP_PROCESSTASK_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO " +
                              " LEFT JOIN DCP_UNIT_LANG d ON a.EID = d.EID AND b.PUNIT = d.UNIT AND d.LANG_TYPE = '" + langType + "' " +
                              " LEFT JOIN DCP_PRODUCT_SALE e ON a.EID = e.EID AND b.OFNO = e.BILLNO AND b.SHOPID = e.SHOPID " +
                              " LEFT JOIN CRM_CHANNEL f ON e.CHANNELID = f.CHANNELID and a.Eid = f.Eid " +
                              " LEFT JOIN DCP_KITCHENPRINTSET h  ON h.EID  = a.EID  AND h.SHOPID  = a.SHOPID  AND h.ID = b.PLUNO AND h.\"TYPE\" = 'GOODS' " +
                              " LEFT JOIN DCP_KITCHENPRINTSET i  ON i.EID  = a.EID  AND i.SHOPID  = a.SHOPID  AND i.ID = b.PLUNO AND i.\"TYPE\" = 'CATEGORY'" +
                              " WHERE a.EID = '" + eId + "' AND a.SHOPID = '" + shopId + "' AND a.OTYPE IS NOT NULL  and (e.ISREFUND = 'N' OR e.ISREFUND is null)  ");
        if (!CollectionUtils.isEmpty(goodsStatus)) {
            sqlbuf.append("AND b.GOODSSTATUS IN (");
            for (String status : goodsStatus) {
                sqlbuf.append("'" + status + "',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.append(")");
        } else {
            sqlbuf.append("AND b.GOODSSTATUS IN ('0', '1')");
        }
        if (!Check.Null(categoryQuery)) {
            sqlbuf.append(" AND b.FINALCATEGORY = '" + categoryQuery + "'");
        }

        sqlbuf.append(") a");

        sqlbuf.append(
                " WHERE ((REPASTTYPE = 2 and ISBOOK='Y' AND OTYPE != 'BEFORE' AND substr(SHIPENDTIME,0,8) = to_char(sysdate, 'yyyyMMdd'))  " +//外卖+预定单，取配送日期当天
                        " OR (REPASTTYPE = 2 and ISBOOK='N' and OTYPE != 'BEFORE' AND substr(CREATEDATETIME,0,8) = to_char(sysdate, 'yyyyMMdd')) " +//外卖+非预定，取下单日期
                        " OR ((OTYPE='BEFORE' AND BILLNO IS NULL) OR (OTYPE!='BEFORE' AND BILLNO IS NOT NULL)  AND substr(CREATEDATETIME,0,8) = to_char(sysdate, 'yyyyMMdd')))  " +//非外卖，取下单日期
                        " ORDER BY NVL(sorttime, 0) ASC ,PROCESSTASKNO,PLUNO,PLUBARCODE,PQTY ");

        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 填充数据
     * @param res
     * @param level1Elm
     * @param plunoDetail
     * @param pluQty
     * @param strKdsProduceQty
     * @param isOverTime
     * @return
     * @throws ParseException
     */
    protected DCP_KitchenDishQuery_OpenRes.level1Elm fillData(DCP_KitchenDishQuery_OpenRes res, DCP_KitchenDishQuery_OpenRes.level1Elm level1Elm, Map<String, Object> plunoDetail, String pluQty, String strKdsProduceQty, String isOverTime) throws ParseException
    {
        level1Elm.setPluNo(plunoDetail.get("PLUNO").toString());
        level1Elm.setPluName(plunoDetail.get("PLUNAME").toString());
        level1Elm.setPluBarcode(plunoDetail.get("PLUBARCODE").toString());
        level1Elm.setSingleMakeQty(strKdsProduceQty);
        level1Elm.setQty(pluQty);
        level1Elm.setSpecAttrDetail(plunoDetail.get("SPECATTRDETAIL").toString());
        level1Elm.setUnitId(plunoDetail.get("PUNIT").toString());
        level1Elm.setUnitName(plunoDetail.get("UNAME").toString());
        level1Elm.setFlavorstuffDetail(plunoDetail.get("FLAVORSTUFFDETAIL").toString());
        level1Elm.setIsPackage(plunoDetail.get("ISPACKAGE").toString());
        level1Elm.setPGoodsDetail(plunoDetail.get("PGOODSDETAIL").toString());
        level1Elm.setGoodsStatus(plunoDetail.get("GOODSSTATUS").toString());
        level1Elm.setMemo(plunoDetail.get("PLUMEMO").toString());
        level1Elm.setSomeTime("");
        level1Elm.setCookId(plunoDetail.get("COOKID").toString());
        level1Elm.setCookName(plunoDetail.get("COOKNAME").toString());
        String isPrintCrossMenu = "";
        // 是否打印划菜单Y/N
        String isprintcrossmenu1 = plunoDetail.get("HISPRINTCROSSMENU").toString();
        String isprintcrossmenu2 = plunoDetail.get("IISPRINTCROSSMENU").toString();
        if (!Check.Null(isprintcrossmenu1)) {
            isPrintCrossMenu = isprintcrossmenu1;
        } else if (!Check.Null(isprintcrossmenu2)) {
            isPrintCrossMenu = isprintcrossmenu2;
        }
        level1Elm.setIsPrintCrossMenu(isPrintCrossMenu);

        // 划菜打印机
        String crossPrinterName = "";
        String crossPrinterName1 = plunoDetail.get("HCROSSPRINTERNAME").toString();
        String crossPrinterName2 = plunoDetail.get("ICROSSPRINTERNAME").toString();
        if (!Check.Null(crossPrinterName1)) {
            crossPrinterName = crossPrinterName1;
        } else if (!Check.Null(crossPrinterName2)) {
            crossPrinterName = crossPrinterName2;
        }
        level1Elm.setCrossPrinter(crossPrinterName);
        level1Elm.setSorttime(plunoDetail.get("SORTTIME").toString());

        level1Elm.setOrderList(new ArrayList<>());
        //填充orderlist
        DCP_KitchenDishQuery_OpenRes.level2Elm level2Elm = fillOrderData(res, plunoDetail, pluQty, isOverTime);
        level1Elm.getOrderList().add(level2Elm);
        return level1Elm;
    }

    /**
     * 填充orderlist
     * @param res
     * @param plunoDetail
     * @param pluQty
     * @param isOverTime
     * @return
     * @throws ParseException
     */
    protected DCP_KitchenDishQuery_OpenRes.level2Elm fillOrderData(DCP_KitchenDishQuery_OpenRes res, Map<String, Object> plunoDetail, String pluQty, String isOverTime) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String oType = plunoDetail.get("OTYPE").toString();
        DCP_KitchenDishQuery_OpenRes.level2Elm level2Elm = res.new level2Elm();
        String billno = plunoDetail.get("BILLNO").toString();
        if ("BEFORE".equals(oType))
        {
            level2Elm.setIsBefore("Y");
            level2Elm.setProcessTaskNo(plunoDetail.get("PROCESSTASKNO").toString());
            level2Elm.setBillNo("");
            level2Elm.setQty(pluQty);
            level2Elm.setIsUrge(plunoDetail.get("ISURGE").toString());
            level2Elm.setItemList(new ArrayList<>());
            String item = plunoDetail.get("ITEM").toString();
            DCP_KitchenDishQuery_OpenRes.level4Elm level4Elm = res.new level4Elm();
            level4Elm.setItem(item);
            level2Elm.getItemList().add(level4Elm);
            String createdatetime = plunoDetail.get("CREATEDATETIME").toString();
            String overTime = "";
            Long min = getMin(createdatetime);
            DCP_KitchenDishQuery_OpenRes.level3Elm level3Elm = res.new level3Elm();
            String sorttime = plunoDetail.get("SORTTIME").toString();
            Long overMin = getMin(sorttime);
            if (Check.Null(isOverTime) || "0".equals(isOverTime)) {
                overTime = "N";
            } else {
                int i = Integer.parseInt(isOverTime);
                if (overMin >= i) {
                    overTime = "Y";
                }
            }
            level2Elm.setIsOverTime(overTime);
            String maketime = plunoDetail.get("MAKETIME").toString();
            String format = "";
            if (!Check.Null(sorttime)) {
                format = sds.format(sdf.parse(sorttime));
            }
            level3Elm.setOrderTime(format);
            level3Elm.setSomeTime(min + "");
            if (!Check.Null(maketime)) {
                maketime = sds.format(sdf.parse(maketime));
            }
            level3Elm.setMadeTime(maketime);
            level2Elm.setTimeInfo(level3Elm);
        }
        else if (!"BEFORE".equals(oType) && !Check.Null(billno))
        {
            String loaddoctype = plunoDetail.get("LOADDOCTYPE").toString();
            String channelId = plunoDetail.get("CHANNELID").toString();
            level2Elm.setOChannelId(channelId);
            level2Elm.setProcessTaskNo(plunoDetail.get("PROCESSTASKNO").toString());
            level2Elm.setBillNo(billno);
            level2Elm.setOChannelName(plunoDetail.get("CHANNELNAME").toString());
            level2Elm.setLoadDocType(loaddoctype);
            String appType = plunoDetail.get("APPTYPE").toString();
            String appName = "";
            if ("POS".equals(appType) || "POSANDROID".equals(appType)) {
                appName = "POS";
            } else if ("SCAN".equals(appType)) {
                appName = "扫码点单";
            } else if ("WAIMAI".equals(appType)) {
                appName = "自营外卖";
            } else if ("ELEME".equals(appType)) {
                appName = "饿了么";
            } else if ("MEITUAN".equals(appType)) {
                appName = "美团";
            }
            level2Elm.setAppName(appName);
            level2Elm.setBillNo(plunoDetail.get("BILLNO").toString());
            level2Elm.setTrNo(plunoDetail.get("TRNO").toString());
            level2Elm.setQty(pluQty);
            level2Elm.setTableNo(plunoDetail.get("TABLENO").toString());
            level2Elm.setAdultCount(plunoDetail.get("GUESTNUM").toString());
            level2Elm.setRepastType(plunoDetail.get("REPASTTYPE").toString());
            level2Elm.setMemo(plunoDetail.get("MEMO").toString());
            level2Elm.setIsUrge(plunoDetail.get("ISURGE").toString());
            String createdatetime = plunoDetail.get("CREATEDATETIME").toString();
            String overTime = "";
            Long min = getMin(createdatetime);
            level2Elm.setIsBefore("N");
            DCP_KitchenDishQuery_OpenRes.level3Elm level3Elm = res.new level3Elm();
            String sorttime = plunoDetail.get("SORTTIME").toString();
            String maketime = plunoDetail.get("MAKETIME").toString();
            String format = "";
            Long overMin = getMin(sorttime);
            if (Check.Null(isOverTime) || "0".equals(isOverTime)) {
                overTime = "N";
            } else {
                int i = Integer.parseInt(isOverTime);
                if (overMin >= i) {
                    overTime = "Y";
                }
            }
            level2Elm.setIsOverTime(overTime);
            if (!Check.Null(sorttime)) {
                format = sds.format(sdf.parse(sorttime));
            }
            level3Elm.setOrderTime(format);
            level3Elm.setSomeTime(min + "");
            level3Elm.setMadeTime(maketime);
            level2Elm.setTimeInfo(level3Elm);

            level2Elm.setItemList(new ArrayList<>());
            String item = plunoDetail.get("ITEM").toString();
            DCP_KitchenDishQuery_OpenRes.level4Elm level4Elm = res.new level4Elm();
            level4Elm.setItem(item);
            level2Elm.getItemList().add(level4Elm);
        }
        if (Check.Null(level2Elm.getBillNo()))
        {
            System.out.println(billno);
        }
        return level2Elm;
    }

    /**
     * 查询当前门店该机台超时预警时间
     *
     * @param req
     * @return
     */
    protected String getIsOverTime(DCP_KitchenDishQuery_OpenReq req) {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_KDSBASICSET WHERE eid = '" + req.geteId() + "' AND SHOPID = '" + req.getRequest().getShopId() + "' AND MACHINEID = '" + req.getRequest().getMachineId() + "' ");
        sql = sqlbuf.toString();
        return sql;
    }


    /**
     * 计算下单时间和当前时间差 分钟
     *
     * @param sdate
     * @return
     * @throws ParseException
     */
    public Long getMin(String sdate) throws ParseException {
        long min = 0;
        if (!Check.Null(sdate)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date start = simpleDateFormat.parse(sdate);
            Date end = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            long between = (end.getTime() - start.getTime()) / 1000;
            min = between / 60;
        }
        return min;
    }

    /**
     * 查询该门店该机台的所有机器人信息
     *
     * @param
     * @return
     */
    protected String getCook() {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_KDSCOOKSET WHERE EID = ? AND SHOPID  = ?  AND STATUS='Y' ORDER BY SORTID ");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 拼接就餐类型
     *
     * @param type0 堂食数量
     * @param type1 打包数量
     * @param type2 外卖数量
     * @param type3 预制数量
     * @return
     */
    protected String getRepastNum(Integer type0, Integer type1, Integer type2, Integer type3) {
        StringBuffer repast = new StringBuffer();
        if (type0 > 0 && type1 == 0 && type2 == 0 && type3 == 0) {
            repast.append("堂食");
        } else if (type0 == 0 && type1 > 0 && type2 == 0 && type3 == 0) {
            repast.append("打包");
        } else if (type0 == 0 && type1 == 0 && type2 > 0 && type3 == 0) {
            repast.append("外卖");
        } else if (type0 == 0 && type1 == 0 && type2 == 0 && type3 > 0) {
            repast.append("预制");
        } else {
            if (type0 > 0) {
                repast.append("堂食×" + type0 + ",");
            }
            if (type1 > 0) {
                repast.append("打包×" + type1 + ",");
            }
            if (type2 > 0) {
                repast.append("外卖×" + type2 + ",");
            }
            if (type3 > 0) {
                repast.append("预制×" + type3 + ",");
            }
            if (repast.length() > 0) {
                repast.deleteCharAt(repast.length() - 1);
            }
        }

        return repast.toString();
    }

}

