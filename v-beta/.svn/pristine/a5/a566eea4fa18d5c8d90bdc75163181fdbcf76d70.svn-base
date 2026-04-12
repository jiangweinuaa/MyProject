package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.service.imp.json.DCP_ConversionTimeFormat;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 差异申诉单上传锐翔
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RuiXiangDiffOrderUpLoad extends InitJob
{

    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(RuiXiangDiffOrderUpLoad.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中


    public  RuiXiangDiffOrderUpLoad()
    {

    }

    public  RuiXiangDiffOrderUpLoad(String eId,String shopId,String organizationNO, String billNo)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }

    public String doExe()  throws Exception
    {
        //返回信息
        String sReturnInfo="";

        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.info("\r\n*********差异申诉单上传锐翔系统RuiXiangDiffOrderUpLoad正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-差异申诉单上传锐翔系统RuiXiangDiffOrderUpLoad正在执行中！";
            return sReturnInfo;
        }

        //锐翔测试环境
        //地址：https://oms.ebake.net/API/WebApi/zj_v/ApiServers.ashx?action=AddYHD
        //秘钥：20801eebd05b3bf3f8bd
        String RuiXiang_Url=StaticInfo.RuiXiang_Url;
        String RuiXiang_Secret=StaticInfo.RuiXiang_Secret;
        String ruiXiang_appid=StaticInfo.RuiXiang_Appid;
        String ruiXiang_eid=StaticInfo.RuiXiang_Eid;

        if (Check.Null(RuiXiang_Url) || Check.Null(RuiXiang_Secret)|| Check.Null(ruiXiang_appid)|| Check.Null(ruiXiang_eid))
        {
            sReturnInfo="定时传输任务-差异申诉单上传锐翔系统RuiXiangDiffOrderUpLoad配置文件参数RuiXiang_Url、RuiXiang_Secret、ruiXiang_appid、ruiXiang_eid不能为空！";
            logger.error("\r\n*********"+sReturnInfo+"************\r\n");
            return sReturnInfo;
        }

        String action="action";
        String serviceName="AddTH";


        bRun=true;//

        logger.info("\r\n*********差异申诉单上传锐翔系统RuiXiangDiffOrderUpLoad定时调用Start:************\r\n");


        try
        {
            //StockOut抛转
            String sql = this.getQuerySql100_02();

            logger.info("\r\n******差异申诉单RuiXiangDiffOrderUpLoad SQL=" + sql + "******\r\n");

            String[] conditionValues100_02 = {}; // 查詢條件

            List<Map<String, Object>> getQData100_02 = this.doQueryData(sql, conditionValues100_02);

            if (getQData100_02 != null && getQData100_02.isEmpty() == false)
            {
                for (Map<String, Object> oneData100 : getQData100_02)
                {
                    try
                    {
                        String bDate = oneData100.get("BDATE") == null ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) : oneData100.get("BDATE").toString();
                        String transfer_date = "";
                        if (bDate != null && bDate.length() > 0)
                        {
                            transfer_date = DCP_ConversionTimeFormat.converToDate(bDate);
                        }

                        String loadDocType = oneData100.get("LOADDOCTYPE") == null ? ""
                                : oneData100.get("LOADDOCTYPE").toString();

                        if (loadDocType == null || loadDocType.length() <= 0)
                        {
                            loadDocType = "1";
                        }
                        else
                        {
                            loadDocType = "2";
                        }

                        String ruiXiang_warehouse="";
                        String ofno = oneData100.get("OFNO") == null ? "" : oneData100.get("OFNO").toString();
                        String shopId = oneData100.get("SHOPID") == null ? "" : oneData100.get("SHOPID").toString();
                        String outcostWarehouse = oneData100.get("OUT_COST_WAREHOUSE") == null ? "" : oneData100.get("OUT_COST_WAREHOUSE").toString();
                        String eId = oneData100.get("EID") == null ? "": oneData100.get("EID").toString();
                        String organizationNO = oneData100.get("ORGANIZATIONNO") == null ? "": oneData100.get("ORGANIZATIONNO").toString();
                        String differenceno = oneData100.get("DIFFERENCENO") == null ? "": oneData100.get("DIFFERENCENO").toString();
                        String memo = oneData100.get("MEMO") == null ? "" : oneData100.get("MEMO").toString();
                        String docType = oneData100.get("DOCTYPE") == null ? "" : oneData100.get("DOCTYPE").toString();
                        String loadDocNo = oneData100.get("LOADDOCNO") == null ? "": oneData100.get("LOADDOCNO").toString();
                        String createBy = oneData100.get("CREATEBY") == null ? "" : oneData100.get("CREATEBY").toString();
                        String createDate = oneData100.get("CREATEDATE") == null ? "": oneData100.get("CREATEDATE").toString();
                        String createTime = oneData100.get("CREATETIME") == null ? "": oneData100.get("CREATETIME").toString();
                        String create_datetime = "";
                        if (createDate != null && createDate.length() > 0 && createTime != null&& createTime.length() > 0)
                        {
                            create_datetime = DCP_ConversionTimeFormat.converToDatetime(createDate + createTime);
                        }
                        String modifyBy = oneData100.get("MODIFYBY") == null ? "" : oneData100.get("MODIFYBY").toString();
                        String modifyDate = oneData100.get("MODIFYDATE") == null ? "": oneData100.get("MODIFYDATE").toString();
                        String modifyTime = oneData100.get("MODIFYTIME") == null ? "": oneData100.get("MODIFYTIME").toString();
                        String modify_datetime = "";
                        if (modifyDate != null && modifyDate.length() > 0 && modifyTime != null&& modifyTime.length() > 0)
                        {
                            modify_datetime = DCP_ConversionTimeFormat.converToDatetime(modifyDate + modifyTime);
                        }
                        String confirmBy = oneData100.get("CONFIRMBY") == null ? "": oneData100.get("CONFIRMBY").toString();
                        String confirmDate = oneData100.get("CONFIRMDATE") == null ? "": oneData100.get("CONFIRMDATE").toString();
                        String confirmTime = oneData100.get("CONFIRMTIME") == null ? "": oneData100.get("CONFIRMTIME").toString();
                        String approve_datetime = "";
                        if (confirmDate != null && confirmDate.length() > 0 && confirmTime != null&& confirmTime.length() > 0)
                        {
                            approve_datetime = DCP_ConversionTimeFormat.converToDatetime(confirmDate + confirmTime);
                        }
                        String accountBy = oneData100.get("ACCOUNTBY") == null ? "": oneData100.get("ACCOUNTBY").toString();
                        String accountDate = oneData100.get("ACCOUNTDATE") == null ? "": oneData100.get("ACCOUNTDATE").toString();
                        String accountTime = oneData100.get("ACCOUNTTIME") == null ? "": oneData100.get("ACCOUNTTIME").toString();
                        String posted_datetime = "";
                        if (accountDate != null && accountDate.length() > 0 && accountTime != null&& accountTime.length() > 0)
                        {
                            posted_datetime = DCP_ConversionTimeFormat.converToDatetime(accountDate + accountTime);
                        }
                        String transferShop = oneData100.get("TRANSFERSHOP") == null ? "": oneData100.get("TRANSFERSHOP").toString();

                        // 获取单身数据
                        sql = this.getQuerySql101_02(eId, organizationNO, differenceno);
                        String[] conditionValues101_02 = {  }; // 查詢條件
                        List<Map<String, Object>> getQData101_02 = this.doQueryData(sql, conditionValues101_02);
                        if (getQData100_02 != null && getQData100_02.isEmpty() == false)
                        {
                            //这个是按照先进先出顺序的
                            com.alibaba.fastjson.JSONObject  request=new com.alibaba.fastjson.JSONObject (true);
                            //这个是按照先进先出顺序的
                            com.alibaba.fastjson.JSONObject  maininfo=new com.alibaba.fastjson.JSONObject (true);

                            com.alibaba.fastjson.JSONArray request_detail = new com.alibaba.fastjson.JSONArray(); // 存所有单身


                            //标记一下
                            int count_detail=0;
                            for (Map<String, Object> oneData101 : getQData101_02)
                            {
                                // 获取单身数据并赋值
                                com.alibaba.fastjson.JSONObject body = new com.alibaba.fastjson.JSONObject(true); // 存一笔单身
                                String item = oneData101.get("ITEM") == null ? "" : oneData101.get("ITEM").toString();
                                String oItem = oneData101.get("OITEM") == null ? "" : oneData101.get("OITEM").toString();
                                String pluNO = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
                                String goodMemo = oneData101.get("GOODSMEMO") == null ? "" : oneData101.get("GOODSMEMO").toString();
                                String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
                                String baseUnit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
                                String punit = oneData101.get("PUNIT") == null ? "" : oneData101.get("PUNIT").toString();
                                String pqty = oneData101.get("PQTY") == null ? "0" : oneData101.get("PQTY").toString();
                                String baseQty = oneData101.get("BASEQTY") == null ? "0" : oneData101.get("BASEQTY").toString();
                                String unitRatio = oneData101.get("UNITRATIO") == null ? "0" : oneData101.get("UNITRATIO").toString();
                                String price = oneData101.get("PRICE") == null ? "0" : oneData101.get("PRICE").toString();
                                String amt = oneData101.get("AMT") == null ? "0" : oneData101.get("AMT").toString();
                                String WAREHOUSE = oneData101.get("WAREHOUSE") == null ? ""	: oneData101.get("WAREHOUSE").toString();
                                String batchNO = oneData101.get("BATCH_NO") == null ? ""	: oneData101.get("BATCH_NO").toString();
                                String prodDate = oneData101.get("PROD_DATE") == null ? ""	: oneData101.get("PROD_DATE").toString();

                                // 新增进货单价和进货金额  BY JZMA 2019-09-12
                                String distriPrice = oneData101.get("DISTRIPRICE") == null ? "0" : oneData101.get("DISTRIPRICE").toString();
                                String distriAmt = oneData101.get("DISTRIAMT") == null ? "0" : oneData101.get("DISTRIAMT").toString();

                                body.put("code", goodMemo);//商品编码,取商品表的MEMO备注字段
                                body.put("Mqty", new BigDecimal("0").subtract(new BigDecimal(pqty)));//辅单位数量,少收货传正，多收货传负
                                body.put("Munits", PosPub.F_UNIT_DIGIWINTORX(punit));//辅单位
                                body.put("Mcosti", new BigDecimal(distriPrice));//成本价
                                body.put("Mcosto", new BigDecimal(price));//单价
                                body.put("mcosto_y", new BigDecimal(price));//零售价
                                body.put("notes", "");//备注

                                request_detail.add(body);

                                //
                                count_detail+=1;
                                if (count_detail==1)
                                {
                                    //无来源单的退货单，去组织表的备注
                                    if (Check.Null(ofno))
                                    {
                                        String sql_warehouse="select MEMO from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
                                        List<Map<String, Object>> getQDataWarehouse = this.doQueryData(sql_warehouse, null);
                                        if (getQDataWarehouse != null && getQDataWarehouse.size()>0)
                                        {
                                            ruiXiang_warehouse=getQDataWarehouse.get(0).get("MEMO").toString();
                                        }
                                    }
                                    else
                                    {
                                        //有来源单:分几种情况，取不到就还是去门店组织的备注仓位
                                        //1、来源单是配送收货单的:
                                          //load_docno为空的
                                          //load_docno不为空的
                                        //2、来源单是其他入库单的:

                                        //查找配送单的锐翔单号
                                        boolean b_ExistWrhCode=true;
                                        if (Check.Null(loadDocNo))
                                        {
                                            //差异单的ofno是配送收货单，查一下
                                           if (ofno.startsWith("PSSH"))
                                           {
                                               String sql_Stockin="select LOAD_DOCNO from dcp_stockin where eid='"+eId+"' and stockinno='"+ofno+"' and shopid='"+shopId+"' ";
                                               List<Map<String, Object>> getQDataStockin = this.doQueryData(sql_Stockin, null);
                                               if (getQDataStockin != null && getQDataStockin.size()>0)
                                               {
                                                   b_ExistWrhCode=true;
                                                   loadDocNo=getQDataStockin.get(0).get("LOAD_DOCNO").toString();
                                               }
                                               else
                                               {
                                                   b_ExistWrhCode=false;
                                               }
                                           }
                                           else //其他入库单是没有锐翔仓位的，只能取门店组织的备注仓位
                                           {
                                               b_ExistWrhCode=false;
                                           }
                                        }


                                        //有锐翔单号，取收货通知单锐翔仓位
                                        if (b_ExistWrhCode)
                                        {
                                            String sql_warehouse="select RXWRHCODE from dcp_receiving where eid='"+eId+"' and shopid='"+shopId+"' and load_docno='"+loadDocNo+"' ";
                                            List<Map<String, Object>> getQDataWarehouse = this.doQueryData(sql_warehouse, null);
                                            if (getQDataWarehouse != null && getQDataWarehouse.size()>0)
                                            {
                                                ruiXiang_warehouse=getQDataWarehouse.get(0).get("RXWRHCODE").toString();
                                            }
                                        }
                                        else //取门店备注仓位
                                        {
                                            String sql_warehouse="select MEMO from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
                                            List<Map<String, Object>> getQDataWarehouse = this.doQueryData(sql_warehouse, null);
                                            if (getQDataWarehouse != null && getQDataWarehouse.size()>0)
                                            {
                                                ruiXiang_warehouse=getQDataWarehouse.get(0).get("MEMO").toString();
                                            }
                                        }
                                    }
                                }
                            }

                            // 给单头赋值
                            //锐翔测试环境： 01机构对应仓位600      00机构对应仓位400
                            maininfo.put("jgdm", shopId);//退货机构
                            maininfo.put("tdrq", transfer_date+" 00:00:00");//单据日期 格式2021-11-28 00:00:00
                            maininfo.put("JgdmIn", "000");//退入机构,詹记固定000,锐翔测试环境00
                            //maininfo.put("wrhcodeIn", "400");//退入仓位,要改哦 ruiXiang_warehouse
                            maininfo.put("wrhcodeIn", ruiXiang_warehouse);//退入仓位,要改哦
                            maininfo.put("JgdmOut", shopId);//退货机构
                            outcostWarehouse="10";//詹记固定写10 代表营业间
                            maininfo.put("wrhcodeOut", outcostWarehouse);//退货仓位
                            maininfo.put("yrnumber", loadDocNo);//来源单，锐翔的配送单号
                            maininfo.put("fhshhr","999");//发审人，锐翔测试用户编号0001,固定用詹记999
                            maininfo.put("fhshhrq", approve_datetime);//发审日期 格式2021-11-28 00:00:00
                            maininfo.put("notes", memo);//备注
                            request.put("maininfo",maininfo);
                            request.put("details",request_detail);


                            String str = request.toString();// 将json对象转换为字符串

                            //排序拼接
                            StringBuffer sb_Url=new StringBuffer(RuiXiang_Url+"?");
                            sb_Url.append(action).append("=").append(serviceName);

                            //签名秘钥
                            StringBuffer RuiXiang_sign=new StringBuffer(RuiXiang_Secret);

                            //排序字段處理
                            Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
                            maps.put(action, serviceName);

                            //appid
                            String appid=ruiXiang_appid;
                            maps.put("appid", appid);

                            //oderinfo
                            maps.put("oderinfo", str);

                            //时间戳
                            String timestamp=String.valueOf(System.currentTimeMillis()/1000);
                            maps.put("timestamp", timestamp);

                            //这个是按照先进先出顺序的
                            com.alibaba.fastjson.JSONObject  head=new com.alibaba.fastjson.JSONObject (true);

                            Iterator it_M = maps.entrySet().iterator();
                            while (it_M.hasNext())
                            {
                                // entry的输出结果如key0=value0等
                                Map.Entry entry =(Map.Entry) it_M.next();
                                Object key = entry.getKey();
                                Object value=entry.getValue();

                                //签名不转码
                                RuiXiang_sign.append(key).append(value);

                                //POST请求
                                head.put(key.toString(),value.toString());
                            }

                            //计算签名
                            String sign= DigestUtils.md5Hex(RuiXiang_sign.toString());
                            sign=sign.toUpperCase();

                            //签名
                            head.put("sign",sign);

                            String v_RuiXiang_Url=sb_Url.toString();
                            sb_Url.setLength(0);

                            logger.info("\r\n******差异申诉单RuiXiangDiffOrderUpLoad请求锐翔系统传入参数：  " + head.toString() +"地址："+v_RuiXiang_Url+ "\r\n");

                            String resBody="";

                            // 执行请求操作，并拿到结果（同步阻塞）
                            try
                            {
                                resBody= HttpSend.Sendhttp("POST", head.toString(), v_RuiXiang_Url);
                                logger.info("\r\n******差异申诉单RuiXiangDiffOrderUpLoad请求锐翔系统返回参数：  "+ "\r\n单号="+ differenceno + "\r\n" + resBody + "******\r\n");
                                if (Check.Null(resBody))
                                {
                                    logger.error("\r\n*********要货单上传到锐翔系统RuiXiangPOrderUpLoad 请求锐翔系统报错，返回为空************\r\n");
                                    continue;
                                }
                                else
                                {
                                    JSONObject res=new JSONObject(resBody);
                                    String resCode=res.get("code").toString();
                                    if (resCode.equals("100000"))
                                    {
                                        // values
                                        Map<String, DataValue> values = new HashMap<String, DataValue>();
                                        DataValue v = new DataValue("Y", Types.VARCHAR);
                                        values.put("process_status", v);
                                        DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
                                        values.put("UPDATE_TIME", v1);
                                        values.put("TRAN_TIME", v1);
                                        //记录ERP 返回的单号和组织
                                        String docNo=res.get("data").toString();
                                        DataValue docNoVal = new DataValue(docNo, Types.VARCHAR);
                                        values.put("PROCESS_ERP_NO", docNoVal);

                                        // condition
                                        Map<String, DataValue> conditions = new HashMap<String, DataValue>();
                                        DataValue c1 = new DataValue(organizationNO, Types.VARCHAR);
                                        conditions.put("OrganizationNO", c1);
                                        DataValue c2 = new DataValue(eId, Types.VARCHAR);
                                        conditions.put("EID", c2);
                                        DataValue c3 = new DataValue(shopId, Types.VARCHAR);
                                        conditions.put("SHOPID", c3);
                                        DataValue c4 = new DataValue(differenceno, Types.VARCHAR);
                                        conditions.put("DIFFERENCENO", c4);

                                        this.doUpdate("DCP_DIFFERENCE", values, conditions);

                                        //删除WS日志 By jzma 20190524
                                        InsertWSLOG.delete_WSLOG(eId, shopId,"1",differenceno);
                                        sReturnInfo="0";
                                    }
                                    else
                                    {
                                        String description=res.get("message").toString();
                                        //
                                        sReturnInfo="锐翔系统返回错误信息:" + resCode + "," + description;

                                        InsertWSLOG.insert_WSLOG("RuiXiangDiffOrderUpLoad",differenceno,eId,organizationNO,"1",str,resBody,resCode,description) ;
                                    }
                                }

                            }
                            catch (Exception e)
                            {
                                //记录WS日志 By jzma 20190524
                                InsertWSLOG.insert_WSLOG("RuiXiangDiffOrderUpLoad",differenceno,eId,shopId,"1",str,resBody,"-1",e.getMessage());

                                sReturnInfo="错误信息:" + e.getMessage();

                                logger.error("\r\n******差异申诉单RuiXiangDiffOrderUpLoad：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +differenceno + "\r\n报错信息："+e.getMessage()+"******\r\n");
                            }
                        }
                        else
                        {
                            //
                            sReturnInfo="错误信息:无单身数据！";

                            logger.info("\r\n******差异申诉单RuiXiangDiffOrderUpLoad：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +differenceno + "无单身数据！******\r\n");
                        }

                    }
                    catch (Exception e1)
                    {
                        try
                        {
                            StringWriter errors = new StringWriter();
                            PrintWriter pw=new PrintWriter(errors);
                            e1.printStackTrace(pw);

                            pw.flush();
                            pw.close();

                            errors.flush();
                            errors.close();

                            logger.error("\r\n******差异申诉单RuiXiangDiffOrderUpLoad报错信息" + e1.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                            pw=null;
                            errors=null;
                        }
                        catch (IOException e2)
                        {
                            logger.error("\r\n******差异申诉单RuiXiangDiffOrderUpLoad报错信息" + e2.getMessage() + "******\r\n");
                        }

                        //
                        sReturnInfo="错误信息:" + e1.getMessage();
                    }
                }
            }
            else
            {
                //
                sReturnInfo="错误信息:无单头数据！";

                logger.info("\r\n******差异申诉单RuiXiangDiffOrderUpLoad没有要上传的单头数据******\r\n");
            }
        }
        catch (Exception e)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                logger.error("\r\n******差异申诉单上传锐翔系统RuiXiangDiffOrderUpLoad报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******差异申诉单上传锐翔系统RuiXiangDiffOrderUpLoad报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********差异申诉单上传锐翔系统RuiXiangDiffOrderUpLoad定时调用End:************\r\n");
        }

        return sReturnInfo;
    }


    //DCP_STOCKOUT
    protected String getQuerySql100_02() throws Exception
    {
        String sql = null;

        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append(""
                              + "select EID,ORGANIZATIONNO,DIFFERENCENO ,SHOPID,BDATE,MEMO,STATUS,DOCTYPE,OTYPE,OFNO,LOADDOCTYPE,LOADDOCNO,CREATEBY, "
                              + " CREATEDATE,CREATETIME,CONFIRMBY,CONFIRMDATE,CONFIRMTIME,ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME, "
                              + " CANCELBY,CANCELDATE,CANCELTIME,TRANSFERSHOP,TOTPQTY,TOTAMT,TOTCQTY,MODIFYBY,MODIFYDATE,MODIFYTIME, belfirm,OUT_COST_WAREHOUSE "
                              + " from ("
                              + "SELECT a.EID,a.ORGANIZATIONNO,a.DIFFERENCENO,a.SHOPID,a.BDATE,a.MEMO,a.STATUS,a.DOC_TYPE as DOCTYPE, "
                              + " a.OTYPE,a.OFNO,a.LOAD_DOCTYPE as LOADDOCTYPE, a.LOAD_DOCNO as LOADDOCNO,a.CREATEBY, "
                              + " a.CREATE_DATE as CREATEDATE,a.CREATE_TIME as CREATETIME,a.CONFIRMBY,a.CONFIRM_DATE as CONFIRMDATE, "
                              + " a.CONFIRM_TIME as CONFIRMTIME,a.ACCOUNTBY,a.ACCOUNT_DATE as ACCOUNTDATE,a.ACCOUNT_TIME as ACCOUNTTIME, "
                              + " a.CANCELBY,a.CANCEL_DATE as CANCELDATE,a.CANCEL_TIME as CANCELTIME,a.TRANSFER_SHOP as TRANSFERSHOP, "
                              + " a.TOT_PQTY as TOTPQTY,a.TOT_AMT as TOTAMT,a.TOT_CQTY as TOTCQTY,"
                              + " a.MODIFYBY,a.MODIFY_DATE as MODIFYDATE,a.MODIFY_TIME as MODIFYTIME , b.belfirm,b.OUT_COST_WAREHOUSE "
                              + " FROM DCP_DIFFERENCE a "
                              + " LEFT JOIN DCP_ORG b ON a.EID = b.EID AND a.SHOPID = b.organizationno AND b.STATUS = '100' "
                              + " WHERE (a.status = '1') AND a.process_status = 'N' and a.DOC_TYPE='3' "
        );

        //******兼容即时服务的,只查询指定的那张单据******
        if (pEId.equals("")==false)
        {
            sqlbuf.append(" and a.EID='"+pEId+"' "
                                  + " and a.SHOPID='"+pShop+"' "
                                  + " and a.ORGANIZATIONNO='"+pOrganizationNO+"' "
                                  + " and a.DIFFERENCENO='"+pBillNo+"' ");

        }


        sqlbuf.append(" ) TBL ");

        sql = sqlbuf.toString();

        return sql;
    }

    //DCP_STOCKOUT_DETAIL
    protected String getQuerySql101_02(String eId, String organizationNO, String stockOutNO100) throws Exception
    {
        String sql = null;

        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append("select item,oItem,pluNO ,GOODSMEMO,baseunit, punit,pqty,baseqty, "
                              + " unitRatio ,price,amt,warehouse,BATCH_NO,PROD_DATE,DISTRIPRICE,DISTRIAMT,featureno  from ("
                              + " SELECT  a.ITEM,a.OITEM,a.PLUNO ,b.memo GOODSMEMO,a.baseunit, a.PUNIT,a.PQTY,a.baseqty, "
                              + " a.UNIT_RATIO AS UNITRATIO ,a.PRICE,a.AMT,a.warehouse,"
                              + " a.BATCH_NO,a.PROD_DATE,a.DISTRIPRICE,a.DISTRIAMT,a.featureno  "
                              + " FROM DCP_DIFFERENCE_DETAIL a "
                              + " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno "
                              + " WHERE a.EID = '"+eId+"'  " + " AND a.ORGANIZATIONNO = '"+organizationNO+"'  " + " AND a.DIFFERENCENO = '"+stockOutNO100+"' ");
        sqlbuf.append(" ) TBL ");

        sql = sqlbuf.toString();

        return sql;
    }

    //查询模板备注，锐翔的仓位
    protected String getQuerySqlWarehouse(String eId, String organizationNO, String pluno) throws Exception
    {
        String sql = null;

        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append("select a.shoptype,a.ptemplateno,a.memo,b.pluno,c.shopid " +
                              "from dcp_ptemplate a " +
                              "inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno " +
                              "left join dcp_ptemplate_shop c on a.eid=c.eid and a.ptemplateno=c.ptemplateno " +
                              "where a.eid='"+eId+"' " +
                              "and a.doc_type='0' " +
                              "and b.pluno='"+pluno+"' " +
                              "and (a.shoptype='1' or (a.shoptype='2' and c.shopid='"+organizationNO+"' )) " +
                              "and a.memo is not null " +
                              "and a.memo!=' ' ");

        sql = sqlbuf.toString();

        return sql;
    }


}
