package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_EInvoiceRegisterCreateReq;
import com.dsc.spos.json.cust.res.DCP_EInvoiceRegisterCreateRes;
import com.dsc.spos.model.EInvoiceResponse;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 开票登记新增
 * @author: wangzyc
 * @create: 2022-03-15
 */
public class DCP_EInvoiceRegisterCreate extends SPosAdvanceService<DCP_EInvoiceRegisterCreateReq, DCP_EInvoiceRegisterCreateRes> {
    @Override
    protected void processDUID(DCP_EInvoiceRegisterCreateReq req, DCP_EInvoiceRegisterCreateRes res) throws Exception {

        DCP_EInvoiceRegisterCreateReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String billType = request.getBillType();
        String orderno = request.getOrderno();
        String taxRate = request.getTaxRate();
        try {
            if(checkOrderNo(req)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"当前单号："+orderno+" 已登记，请勿重复开票登记！");
            }


            SimpleDateFormat sdfh = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<String> orderNos = new ArrayList<>();
            orderNos.add(orderno);
            // 查询订单是否已退
           String sql = this.CheckOrderIsReturn(eId, orderNos, billType);
            List<Map<String, Object>> getOrderIsReturn = dao.executeQuerySQL(sql, null);
            boolean isQuery = true;
            StringBuffer message = new StringBuffer("");
            if (!CollectionUtils.isEmpty(getOrderIsReturn)) {
                for (Map<String, Object> map : getOrderIsReturn) {
                    String isreturn = map.get("ISRETURN").toString();
                    String orderno1 = map.get("ORDERNO").toString();
                    String status = map.getOrDefault("STATUS","100").toString();
                    String quotesign = map.getOrDefault("QUOTESIGN","0").toString();
                    if("Y".equals(isreturn)||"0".equals(status)||"-1".equals(status)||"90".equals(status)||"1".equals(quotesign)){
                        isQuery = false;
                        message.append(orderno1+",");
                    }
                }
            }
            if(!isQuery){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, message.toString()+"订单已退订，请重新选择开票单据。");
            }

            BigDecimal bigtaxRate = new BigDecimal(taxRate).multiply(new BigDecimal("0.01"));
            // 计算价格
            sql  = getOrderPayInfoByOrderNos(eId,orderNos,billType,bigtaxRate.doubleValue());
            List<Map<String, Object>> orderPayInfoList = dao.executeQuerySQL(sql, null);
            String[] columns_EINVOICE = {"EID","INVOICEBILLNO","SOURCEBILLTYPE","PLATFORMTYPE","INVOICETYPE","TAXRATE","AMT","TAXAMT","EXTAXAMT","DRAWER","RECEIVER","REVIEWER"
                    ,"SALETAXNUM","SALETEL","SALEADDRESS","SALEACCOUNT","QRCODETIME","QRCODE","CREATETIME","UPDATETIME","SALEBANK","BUYERBANK","TEMPLATEID","ISAPPLY",
                    "BUYERNAME","EMAIL","BUYERPHONE","BUYERTAXNUM","BUYERTEL","BUYERADDRESS","BUYERACCOUNT","STATUS","ISMANUAL","OPNO","OPNAME","APPLYDATE","INVOICEDATE"};
            String[] columns_EINVOICE_BUSINESS = {"EID","INVOICEBILLNO","SOURCEBILLNO","SOURCESHOPID"};
            String[] columns_EINVOICE_DETAIL = {"EID", "INVOICEBILLNO", "ITEM", "GOODSNAME", "NUM", "HSBZ", "PRICE", "TAXRATE", "SPBM", "FPHXZ", "YHZCBS", "ZZSTSGL", "LSLBS", "AMT", "TAXAMT", "EXTAXAMT"};


            if (!CollectionUtils.isEmpty(orderPayInfoList)) {
                BigDecimal ordertotal = new BigDecimal(0);
                BigDecimal taxtotal = new BigDecimal(0);
                BigDecimal bhtaxtotal = new BigDecimal(0);
                EInvoiceResponse invoiceResponse = new EInvoiceResponse();
                invoiceResponse.setDetail(new ArrayList<>());
                for (Map<String, Object> orderPayInfo : orderPayInfoList) {
                    String taxamt = orderPayInfo.get("TAXAMT").toString();
                    String tax = orderPayInfo.get("TAX").toString();
                    String taxfreeamt = orderPayInfo.get("TAXFREEAMT").toString();

                    ordertotal = ordertotal.add(new BigDecimal(taxamt));
                    taxtotal = taxtotal.add(new BigDecimal(tax));
                    bhtaxtotal = bhtaxtotal.add(new BigDecimal(taxfreeamt));

                    EInvoiceResponse.level1Elm lv1 = invoiceResponse.new level1Elm();
                    lv1.setPrice(taxamt);
                    lv1.setTaxrate(taxRate + "");
                    lv1.setSpbm(request.getTaxCode());
                    lv1.setTaxamt(taxamt);
                    lv1.setTax(tax);
                    lv1.setTaxfreeamt(taxfreeamt);
                    invoiceResponse.getDetail().add(lv1);
                }
                String invoiceBillNo = sdfh.format(new Date());
                String dateTime = sdf.format(new Date());
                DataValue[] insValue = new DataValue[] {
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(invoiceBillNo, Types.VARCHAR),
                        new DataValue(billType, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("1", Types.VARCHAR),
                        new DataValue(taxRate, Types.VARCHAR), // 税率
                        new DataValue(ordertotal.toString(), Types.VARCHAR), // 价税合计
                        new DataValue(taxtotal.toString(), Types.VARCHAR), // 税额
                        new DataValue(bhtaxtotal.toString(), Types.VARCHAR), // 未税金额
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(dateTime, Types.DATE),
                        new DataValue("", Types.VARCHAR), // 二维码链接地址
                        new DataValue(dateTime, Types.DATE),
                        new DataValue(dateTime, Types.DATE),
                        new DataValue("", Types.VARCHAR), // 销方银行开户行地址
                        new DataValue("", Types.VARCHAR), // 购方银行开户行地址
                        new DataValue("", Types.VARCHAR), // 模版ID
                        new DataValue("N", Types.VARCHAR), // 是否申请开票
                        new DataValue("", Types.VARCHAR), // 购方名称
                        new DataValue("", Types.VARCHAR), // 购方邮箱
                        new DataValue("", Types.VARCHAR), // 购方电话
                        new DataValue("", Types.VARCHAR), // 购方税号
                        new DataValue("", Types.VARCHAR), // 电话
                        new DataValue("", Types.VARCHAR), // 地址
                        new DataValue("", Types.VARCHAR), // 银行账户
                        new DataValue("Y", Types.VARCHAR), // 开票状态
                        new DataValue("Y", Types.VARCHAR), // 是否手工开票
                        new DataValue(request.getOpNo(), Types.VARCHAR), // 操作人
                        new DataValue(request.getOpName(), Types.VARCHAR), // 操作人名称
                        new DataValue(dateTime, Types.DATE),
                        new DataValue(dateTime, Types.DATE),
                };

                InsBean ib1 = new InsBean("DCP_EINVOICE", columns_EINVOICE);
                ib1.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib1));


                DataValue[] insValue2 = new DataValue[] {
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(invoiceBillNo, Types.VARCHAR),
                        new DataValue(orderno, Types.VARCHAR),
                        new DataValue(request.getShopId(), Types.VARCHAR)
                };

                InsBean ib2 = new InsBean("DCP_EINVOICE_BUSINESS", columns_EINVOICE_BUSINESS);
                ib2.addValues(insValue2);
                this.addProcessData(new DataProcessBean(ib2));

                int item = 0;
                for (EInvoiceResponse.level1Elm lv1 : invoiceResponse.getDetail()) {
                    item++;
                    DataValue[] insValue3 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(invoiceBillNo, Types.VARCHAR),
                            new DataValue(item, Types.VARCHAR),
                            new DataValue(request.getProjectName(), Types.VARCHAR),
                            new DataValue(1, Types.VARCHAR),
                            new DataValue(1, Types.VARCHAR),
                            new DataValue(lv1.getPrice(), Types.VARCHAR),
                            new DataValue(lv1.getTaxrate(), Types.VARCHAR),
                            new DataValue(lv1.getSpbm(), Types.VARCHAR),
                            new DataValue(0, Types.VARCHAR),
                            new DataValue(0, Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue(lv1.getTaxamt(), Types.VARCHAR),
                            new DataValue(lv1.getTax(), Types.VARCHAR),
                            new DataValue(lv1.getTaxfreeamt(), Types.VARCHAR)
                    };
                    InsBean ib3 = new InsBean("DCP_EINVOICE_DETAIL", columns_EINVOICE_DETAIL);
                    ib3.addValues(insValue3);
                    this.addProcessData(new DataProcessBean(ib3));
                }

                this.doExecuteDataToDB();
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_EInvoiceRegisterCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_EInvoiceRegisterCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_EInvoiceRegisterCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_EInvoiceRegisterCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(Check.Null(req.getRequest().getBillType()))
        {
            errMsg.append("业务单据类型不可为空值, ");
            isFail = true;
        }

        if(Check.Null(req.getRequest().getOrderno()))
        {
            errMsg.append("原始业务单号不可为空值, ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getTaxCode()))
        {
            errMsg.append("税别不可为空值, ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getTaxRate()))
        {
            errMsg.append("税率不可为空值, ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getProjectName()))
        {
            errMsg.append("开票项目名称不可为空值, ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getIsCheckLimitation()))
        {
            errMsg.append("是否校验发票限额不可为空值, ");
            isFail = true;
        }

        if(isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_EInvoiceRegisterCreateReq> getRequestType() {
        return new TypeToken<DCP_EInvoiceRegisterCreateReq>(){};
    }

    @Override
    protected DCP_EInvoiceRegisterCreateRes getResponseType() {
        return new DCP_EInvoiceRegisterCreateRes();
    }

    /**
     * 查询各种类型单据是否已退
     * @param eId
     * @param orderNos
     * @param billtype Sale-销售单  Card-售卡 Coupon-售券 Recharge-充值
     * @return
     */
    public String CheckOrderIsReturn(String eId,List<String> orderNos,String billtype){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        String ordrNos_Str = PosPub.getArrayStrSQLIn(orderNos.toArray(new String[orderNos.size()]));
        if (!Check.Null(ordrNos_Str)) {
            if ("Sale".equals(billtype)) {
                sqlbuf.append("SELECT SALENO orderNo,ISRETURN,SHOPID，'100' STATUS ,'0' QUOTESIGN FROM dcp_sale WHERE eid = '" + eId + "' AND  SALENO in( " + ordrNos_Str + ")");
            } else if ("Card".equals(billtype)) {
                sqlbuf.append("SELECT BILLNO orderNo,DECODE(REFUND,'1','Y','N') ISRETURN,SHOPID,STATUS,QUOTESIGN FROM CRM_CARDSALE WHERE eid = '" + eId + "' AND  BILLNO in( " + ordrNos_Str + ")");
            } else if ("Coupon".equals(billtype)) {
                sqlbuf.append("SELECT BILLNO orderNo ,DECODE(REFUND,'1','Y','N') ISRETURN, SHOPID,STATUS,QUOTESIGN FROM CRM_COUPONSALE WHERE eid = '" + eId + "' AND  BILLNO in( " + ordrNos_Str + ")");
            } else if ("Recharge".equals(billtype)) {
                sqlbuf.append("SELECT BILLNO orderNo ,DECODE(REFUND,'1','Y','N') ISRETURN,SHOPID ,STATUS,QUOTESIGN FROM CRM_CARDRECHARGE WHERE eid = '" + eId + "' AND  BILLNO in( " + ordrNos_Str + ")");
            }
        }
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 根据不同的单据类型进行查询金额
     * @param eId
     * @param orderNos
     * @param billtype  Sale-销售单  Card-售卡 Coupon-售券 Recharge-充值
     * @param taxrate
     * @return
     */
    public String getOrderPayInfoByOrderNos(String eId,List<String> orderNos,String billtype,Double taxrate){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        String ordrNos_Str = PosPub.getArrayStrSQLIn(orderNos.toArray(new String[orderNos.size()]));
        if (!Check.Null(ordrNos_Str)) {
            if ("Sale".equals(billtype)) {
                sqlbuf.append("  SELECT SALENO orderNo, ROUND(sum(PAY-CHANGED-EXTRA), 2) AS taxamt , ROUND(SUM(PAY-CHANGED-EXTRA) * " + taxrate + " / (1 + " + taxrate + "), 2) AS tax , ROUND(sum(PAY-CHANGED-EXTRA) - ROUND((SUM(PAY-CHANGED-EXTRA) * " + taxrate + ") / (1 + " + taxrate + "), 2), 2) AS taxfreeamt " +
                        " FROM DCP_SALE_PAY a LEFT JOIN DCP_PAYTYPE b ON a.EID = b.EID  AND a.PAYCODE = b.PAYCODE  AND a.PAYTYPE  = b.PAYTYPE " +
                        " WHERE a.EID = '" + eId + "'  AND a.SALENO in(" + ordrNos_Str + ") ");
                sqlbuf.append(" AND b.CANOPENINVOICE  = '1' ");
                sqlbuf.append(" GROUP BY a.SALENO ,b.CANOPENINVOICE");
            } else if ("Card".equals(billtype)) {
                sqlbuf.append("SELECT BILLNO orderNo, ROUND(sum(WORTH), 2) AS taxamt , ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax , ROUND(sum(WORTH) - ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2), 2) AS taxfreeamt " +
                        " FROM CRM_CARDSALEPAY a  " +
                        " LEFT JOIN DCP_PAYTYPE b ON a.EID = b.EID AND a.PAYTYPEID = b.PAYTYPE WHERE a.EID = '"+eId+"' AND a.BILLNO IN ("+ordrNos_Str+") " +
                        " AND b.CANOPENINVOICE = '1' " +
                        " GROUP BY a.BILLNO, b.CANOPENINVOICE ");
            } else if ("Coupon".equals(billtype)) {
                sqlbuf.append("SELECT BILLNO orderNo, ROUND(sum(WORTH), 2) AS taxamt , ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax , ROUND(sum(WORTH) - ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2), 2) AS taxfreeamt " +
                        " FROM CRM_COUPONSALEPAY a  " +
                        " LEFT JOIN DCP_PAYTYPE b ON a.EID = b.EID AND a.PAYTYPEID = b.PAYTYPE WHERE a.EID = '"+eId+"' AND a.BILLNO IN ("+ordrNos_Str+") " +
                        " AND b.CANOPENINVOICE = '1' " +
                        " GROUP BY a.BILLNO, b.CANOPENINVOICE ");
            } else if ("Recharge".equals(billtype)) {
                sqlbuf.append("SELECT BILLNO orderNo, ROUND(sum(WORTH), 2) AS taxamt , ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax , ROUND(sum(WORTH) - ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2), 2) AS taxfreeamt " +
                        " FROM CRM_CARDRECHARGEPAY a  " +
                        " LEFT JOIN DCP_PAYTYPE b ON a.EID = b.EID AND a.PAYTYPEID = b.PAYTYPE WHERE a.EID = '"+eId+"' AND a.BILLNO IN ("+ordrNos_Str+") " +
                        " AND b.CANOPENINVOICE = '1' " +
                        " GROUP BY a.BILLNO, b.CANOPENINVOICE ");
            }
        }
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 判断当前业务单号是否登记过开票
     * @param req
     * @return
     * @throws Exception
     */
    private boolean checkOrderNo(DCP_EInvoiceRegisterCreateReq req) throws Exception{
        boolean bool = false;
        String sql = "select * from DCP_EINVOICE_BUSINESS where eid = '"+req.geteId()+"' and SOURCEBILLNO = '"+req.getRequest().getOrderno()+"' and SOURCESHOPID = '"+req.getRequest().getShopId()+"'";
        List<Map<String, Object>> maps = this.doQueryData(sql, null);
        if(!CollectionUtils.isEmpty(maps)){
            bool = true;
        }
        return bool;
    }
}
