package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.res.DCP_CreditQueryRes;
import com.dsc.spos.utils.Check;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dsc.spos.json.cust.req.DCP_CreditQueryReq;
import com.dsc.spos.scheduler.job.InsertWSLOG;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_CreditQuery extends SPosBasicService<DCP_CreditQueryReq, DCP_CreditQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_CreditQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_CreditQueryReq> getRequestType() {
        return new TypeToken<DCP_CreditQueryReq>() {
        };
    }

    @Override
    protected DCP_CreditQueryRes getResponseType() {
        return new DCP_CreditQueryRes();
    }

    @Override
    protected DCP_CreditQueryRes processJson(DCP_CreditQueryReq req) throws Exception {
        Logger logger = LogManager.getLogger(DCP_CreditQuery.class.getName());
        //查询资料
        DCP_CreditQueryRes res = null;
        res = this.getResponse();
        DCP_CreditQueryReq.level1Elm request = req.getRequest();

        String sCredit = "0";//信用总额
        String sUsed = "0";//已使用额
        String sRest = "0";//剩余额
        String sRecharge = "0";//充值额度
        String isPorder = ""; // 是否超出赊销额度，0-否，1-是
        String creditContrl = ""; // 赊销额度管控方式，0.不管控  1:超额警告 2.超额禁止交易

        res.setDatas(res.new level1Elm());

        DCP_CreditQueryRes.level1Elm level1Elm = res.new level1Elm();
        List<DCP_CreditQueryRes.level2Elm> specialList = new ArrayList<>();
        List<DCP_CreditQueryRes.level3Elm> pOrderList = new ArrayList<>();

        try {

            JSONObject payload = new JSONObject();
            JSONObject std_data = new JSONObject();
            JSONObject parameter = new JSONObject();


            JSONObject header = new JSONObject();

            // 给单头赋值
            header.put("customer", req.getShopId());//门店编号
            if (request != null) {
//                if (!Check.Null(request.getTotPqty())) {
//                    header.put("totPqty", request.getTotPqty());// 合计录入数量
//                }
//                if (!Check.Null(request.getTotAmt())) {
//                    header.put("totAmt", request.getTotAmt());// 合计零售金额
//                }
//                if (!Check.Null(request.getTotDistriAmt())) {
//                    header.put("totDistriAmt", request.getTotDistriAmt());// 合计进货金额
//                }
//                if (!Check.Null(request.getUtotDistriAmt())) {
//                    header.put("utotDistriAmt", request.getUtotDistriAmt());// 原合计进货金额；若为空，说明未参与促销
//                }

                if (!CollectionUtils.isEmpty(request.getGoodsList())) {
                    // 给单身赋值
                    header.put("requisition_detail", request.getGoodsList());// 要货商品明细
                }
            }

            parameter.put("transfer", header);
            std_data.put("parameter", parameter);
            payload.put("std_data", std_data);

            String str = payload.toString();// 将json对象转换为字符串

            String resbody = "";
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******信用额度查询服务" + "credit.query" + "请求T100参数：  " + "\r\n" + "门店=" + req.getShopId() + "\r\n" + str + "******\r\n");
            resbody = HttpSend.Send(str, "credit.query", req.geteId(), req.getShopId(), req.getOrganizationNO(), "门店=" + req.getShopId());
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******信用额度查询服务" + "credit.query" + "请求T100返回参数：  " + "\r\n" + "门店=" + req.getShopId() + "\r\n" + resbody + "******\r\n");
            JSONObject jsonres = new JSONObject(resbody);
            JSONObject std_data_res = jsonres.getJSONObject("std_data");
            JSONObject execution_res = std_data_res.getJSONObject("execution");

            String code = execution_res.getString("code");
            //String sqlcode = execution_res.getString("sqlcode");

            String description = "";
            if (!execution_res.isNull("description")) {
                description = execution_res.getString("description");
            }
            if (code.equals("0")) {
                JSONObject para_res = std_data_res.getJSONObject("parameter");

                //格式化
                java.text.DecimalFormat decfm = new java.text.DecimalFormat("#.###");

                sCredit = decfm.format(para_res.getDouble("credit_atm"));
                sUsed = decfm.format(para_res.getDouble("used_amount"));
                sRest = decfm.format(para_res.getDouble("remaining_amount"));
                if (para_res.has("recharge_amount"))
                {
                    sRecharge = decfm.format(para_res.getDouble("recharge_amount"));
                }


                // 专款额度
                if (para_res.has("speciallist")) {
                    JSONArray specialListArray = para_res.getJSONArray("speciallist");
                    for (int i = 0; i < specialListArray.length(); i++) {
                        JSONObject jsonObject = specialListArray.getJSONObject(i);
                        DCP_CreditQueryRes.level2Elm level2Elm = res.new level2Elm();
                        level2Elm.setSpecialNo(jsonObject.getString("specialno"));
                        int special = jsonObject.getInt("special");
                        level2Elm.setSpecial(special + "");
                        level2Elm.setRules(jsonObject.getString("rules"));
                        specialList.add(level2Elm);
                    }
                }
                // 是否超出剩余赊销额度，0-否，1-是
                try {
                    String isporder = para_res.get("isporder").toString();
                    if (!isporder.equals("null") && !isporder.equals("")) {
                        isPorder = isporder;
                    } else {
                        // 若入参有goodsList&ERP-credit. Query接口未返回isPorder，则根据入参sum(distriAmt )与剩余额度rest比较，返回isPorder；
                        Float sum = 0.0f;
                        if (req.getRequest() != null) {
                            List<DCP_CreditQueryReq.level2ELm> goodsList = req.getRequest().getGoodsList();
                            if (!CollectionUtils.isEmpty(goodsList)) {
                                for (DCP_CreditQueryReq.level2ELm level2ELm : goodsList) {
                                    sum += Float.parseFloat(level2ELm.getDistriAmt());
                                }
                            }
                            if (sum > Float.parseFloat(sRest)) {
                                isPorder = "1";
                            } else {
                                isPorder = "0";
                            }
                        }
                    }
                } catch (Exception e) {
                    // 若入参有goodsList&ERP-credit. Query接口未返回isPorder，则根据入参sum(distriAmt )与剩余额度rest比较，返回isPorder；
                    Float sum = 0.0f;
                    if (req.getRequest() != null) {
                        List<DCP_CreditQueryReq.level2ELm> goodsList = req.getRequest().getGoodsList();
                        if (!CollectionUtils.isEmpty(goodsList)) {
                            for (DCP_CreditQueryReq.level2ELm level2ELm : goodsList) {
                                sum += Float.parseFloat(level2ELm.getDistriAmt());
                            }
                        }
                        if (sum > Float.parseFloat(sRest)) {
                            isPorder = "1";
                        } else {
                            isPorder = "0";
                        }
                    }

                }


                // 超出额度信息
                // 若ERP-credit. Query接口未返回isPorder&isPorder=1，pOrderList返回creditType=0；
                try {
                    JSONArray pOrderListArray = para_res.getJSONArray("porderlist");
                    for (int i = 0; i < pOrderListArray.length(); i++) {
                        JSONObject jsonObject = pOrderListArray.getJSONObject(i);
                        DCP_CreditQueryRes.level3Elm level3Elm = res.new level3Elm();
                        String specialno = jsonObject.getString("specialno").trim();
                        if (!Check.Null(specialno)) {
                            level3Elm.setSpecialNo(specialno);
                        }
                        level3Elm.setCreditType(jsonObject.getString("creditType"));
                        pOrderList.add(level3Elm);
                    }
                } catch (JSONException e) {
                    if (isPorder.equals("1") && req.getRequest() != null) {
                        DCP_CreditQueryRes.level3Elm level3Elm = res.new level3Elm();
                        level3Elm.setCreditType("0");
                        pOrderList.add(level3Elm);
                    }

                }

                //查询本门店已确认未上传ERP的要货单金额
                String sBeforeAMT = "0";
                String sqlBefore = this.getQuerySql_AMT(req);
                List<Map<String, Object>> getQDataBefore = this.doQueryData(sqlBefore, null);
                if (getQDataBefore != null && getQDataBefore.isEmpty() == false) {
                    sBeforeAMT = getQDataBefore.get(0).get("AMT").toString();
                    if (PosPub.isNumericType(sBeforeAMT) == false) {
                        sBeforeAMT = "0";
                    }
                }

                level1Elm.setPBeforeOrderAMT(sBeforeAMT);

                // creditContrl 赊销额度管控 从 ERP 获取
                creditContrl = para_res.get("creditType").toString();
//                String sqlDef = this.getQuerySqlDef(req);    //加盟商信用超支管控程度  0:严格  1:提醒
//                String[] conditionCredit_Contrl = {"Credit_Contrl", req.geteId()};
//                List<Map<String, Object>> getQDataconditionCredit_Contrl = this.doQueryData(sqlDef, conditionCredit_Contrl);
//
//                String sCredit_Contrl = "1";
//                if (getQDataconditionCredit_Contrl != null && getQDataconditionCredit_Contrl.isEmpty() == false) {
//                    sCredit_Contrl = getQDataconditionCredit_Contrl.get(0).get("ITEMVALUE").toString();
//                }
//                level1Elm.setCreditContrl(sCredit_Contrl);
                level1Elm.setCreditContrl(creditContrl);
                //完成***********************************************************************

                level1Elm.setCredit(sCredit);
                level1Elm.setUsed(sUsed);
                level1Elm.setRest(sRest);
                level1Elm.setRecharge(sRecharge);

                if (!CollectionUtils.isEmpty(specialList)) {
                    level1Elm.setSpecialList(specialList);
                }
                if (!Check.Null(isPorder)) {
                    level1Elm.setIsPorder(isPorder);
                }
                if (!CollectionUtils.isEmpty(pOrderList)) {
                    level1Elm.setPOrderList(pOrderList);
                }

                res.setDatas(level1Elm);

                res.setSuccess(true);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行成功!");

            } else {

                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*********" + "信用额度查询服务" + "credit.query" + ">>>ERP返回错误信息:" + code + "," + description + "************\r\n");

                //写数据库
                InsertWSLOG.insert_WSLOG("credit.query", "门店=" + req.getShopId(), req.geteId(), req.getShopId(), "1", str, resbody, code, description);


                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "查询信用额度失败，单据不能提交!ERP返回错误信息:" + code + "," + description);

            }
        } catch (Exception e) {
            ////System.out.println(e.toString());
            logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******信用额度查询服务" + "credit.query" + "：门店=" + req.getShopId() + ",组织编码=" + req.getOrganizationNO() + ",公司编码=" + req.geteId() + "\r\n报错信息：" + e.getMessage() + "******\r\n");

            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("查询信用额度失败:"+e.getMessage());
        }


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_CreditQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * 查询本门店已确认未上传ERP的要货单金额
     *
     * @param req
     * @return
     * @throws Exception
     */
    protected String getQuerySql_AMT(DCP_CreditQueryReq req) throws Exception {
        String sql = null;

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select sum(a.tot_distriamt) AMT from DCP_porder a "
                + "where a.EID='" + req.geteId() + "' "
                + "and a.SHOPID='" + req.getShopId() + "' "
                + "and a.organizationno='" + req.getShopId() + "' "
                + "and a.process_status='N' "
                + "and a.status='2'");

        sql = sqlbuf.toString();
        return sql;
    }


//    protected String getQuerySqlDef(DCP_CreditQueryReq req) throws Exception {
//        String sql = null;
//        StringBuffer sqlbuf = new StringBuffer("");
//        sqlbuf.append("select ITEMVALUE from Platform_BaseSetTemp "
//                + " where Item=? and EID=?"
//        );
//        sql = sqlbuf.toString();
//        return sql;
//    }


}
