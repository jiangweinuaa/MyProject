package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BizPartnerCreateReq;
import com.dsc.spos.json.cust.res.DCP_BizPartnerRes;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.SUtil;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 交易对象
 *
 * @author 01029
 * .
 * @date 2024-10-23
 */
public class DCP_BizPartnerUpdate extends SPosAdvanceService<DCP_BizPartnerCreateReq, DCP_BizPartnerRes> {

    @Override
    protected void processDUID(DCP_BizPartnerCreateReq req, DCP_BizPartnerRes res) throws Exception {

        try {
            //String oprType = req.getRequest().getOprType();//I insert U update

            processOnCreate(req, res);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());

        }
    }

    private void processOnCreate(DCP_BizPartnerCreateReq req, DCP_BizPartnerRes res) throws Exception {
        String eId = req.geteId();
        String bizPartnerNo = req.getRequest().getBizPartnerNo();
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = null;
        sql = this.isRepeat(eId, bizPartnerNo);

        List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
        if (!mDatas.isEmpty()) {
            ColumnDataValue columns = new ColumnDataValue();
            String[] columns1 = null;
            DataValue[] insValue1 = null;
            UptBean ub1 = null;
            ub1 = new UptBean("DCP_BIZPARTNER");
            ub1.addCondition("EID", DataValues.newString(eId));
            ub1.addCondition("BIZPARTNERNO", DataValues.newString(bizPartnerNo));

            ub1.addUpdateValue("BIZTYPE", new DataValue(req.getRequest().getBizType(), Types.VARCHAR));
            ub1.addUpdateValue("SNAME", new DataValue(req.getRequest().getSName(), Types.VARCHAR));
            ub1.addUpdateValue("FNAME", new DataValue(req.getRequest().getFName(), Types.VARCHAR));
            ub1.addUpdateValue("CORPTYPE", new DataValue(req.getRequest().getCorpType(), Types.VARCHAR));
            ub1.addUpdateValue("REGISTERNO", new DataValue(req.getRequest().getRegisterNo(), Types.VARCHAR));
            ub1.addUpdateValue("LEGALPERSON", new DataValue(req.getRequest().getLegalPerson(), Types.VARCHAR));
            ub1.addUpdateValue("TAXPAYER_NO", new DataValue(req.getRequest().getTaxPayerNo(), Types.VARCHAR));
            ub1.addUpdateValue("MAINCATEGORY", new DataValue(req.getRequest().getMainCatgegory(), Types.VARCHAR));
            ub1.addUpdateValue("MAINBRAND", new DataValue(req.getRequest().getMainBrand(), Types.VARCHAR));
            ub1.addUpdateValue("PUREMPNO", new DataValue(req.getRequest().getPurEmpNo(), Types.VARCHAR));
            ub1.addUpdateValue("PUREDEPTNO", new DataValue(req.getRequest().getPurDeptNo(), Types.VARCHAR));
            ub1.addUpdateValue("GRADE", DataValues.newString(req.getRequest().getGrade()));
            ub1.addUpdateValue("ROLE", new DataValue(req.getRequest().getRole(), Types.VARCHAR));
            ub1.addUpdateValue("ENABLECONTRACT", new DataValue(req.getRequest().getEnableContract(), Types.VARCHAR));
            ub1.addUpdateValue("PAYTYPE", new DataValue(req.getRequest().getPayType(), Types.VARCHAR));
            ub1.addUpdateValue("PAYCENTER", new DataValue(req.getRequest().getPayCenter(), Types.VARCHAR));
            ub1.addUpdateValue("BILLDATENO", new DataValue(req.getRequest().getBillDateNo(), Types.VARCHAR));
            ub1.addUpdateValue("TAXCODE", new DataValue(req.getRequest().getTaxCode(), Types.VARCHAR));
            ub1.addUpdateValue("INVOICECODE", new DataValue(req.getRequest().getInvoiceCode(), Types.VARCHAR));
            ub1.addUpdateValue("PAYDATENO", new DataValue(req.getRequest().getPayDateNo(), Types.VARCHAR));
            ub1.addUpdateValue("MAINCURRENCY", new DataValue(req.getRequest().getMainCurrency(), Types.VARCHAR));
            ub1.addUpdateValue("BEGINDATE", new DataValue(req.getRequest().getBeginDate(), Types.DATE));
            ub1.addUpdateValue("ENDDATE", new DataValue(req.getRequest().getEndDate(), Types.DATE));
            ub1.addUpdateValue("STATUS", new DataValue(Integer.valueOf(req.getRequest().getStatus()), Types.INTEGER));
            ub1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(), Types.VARCHAR));

            ub1.addUpdateValue("OFFICE_ADDRESS", DataValues.newString(req.getRequest().getOfficeAddress()));
            ub1.addUpdateValue("DELIVERY_ADDRESS", DataValues.newString(req.getRequest().getDeliveryAddress()));
            ub1.addUpdateValue("INVOICE_ADDRESS", DataValues.newString(req.getRequest().getInvoiceAddress()));
            ub1.addUpdateValue("ADDRESS", DataValues.newString(req.getRequest().getAddress()));
            ub1.addUpdateValue("IS_CREDIT", new DataValue(req.getRequest().getIsCredit(), Types.VARCHAR));
            ub1.addUpdateValue("CREDIT_AMT", DataValues.newDecimal(req.getRequest().getCreditAmt()));
            ub1.addUpdateValue("CREDIT_TYPE", DataValues.newString(req.getRequest().getCreditType()));
            ub1.addUpdateValue("RESTRICTSHOP", DataValues.newDecimal(req.getRequest().getRestrictShop()));
            ub1.addUpdateValue("COLLECT_OBJECT", DataValues.newString(req.getRequest().getCollectObject()));
            ub1.addUpdateValue("COLLECT_SHOP", DataValues.newString(req.getRequest().getCollectShop()));
            
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

            ub1.addUpdateValue("SALEEMPNO", new DataValue(req.getRequest().getSaleEmpNo(), Types.VARCHAR));
            ub1.addUpdateValue("SALEDEPTNO ", new DataValue(req.getRequest().getSaleDeptNo(), Types.VARCHAR));
            ub1.addUpdateValue("CUSTGRADE", DataValues.newString(req.getRequest().getCustGrade()));
            ub1.addUpdateValue("CUSTPAYTYPE", DataValues.newString(req.getRequest().getCustPayType()));
            ub1.addUpdateValue("CUSTPAYCENTER", new DataValue(req.getRequest().getCustPayCenter(), Types.VARCHAR));
            ub1.addUpdateValue("CUSTBILLDATENO ", new DataValue(req.getRequest().getCustBillDateNo(), Types.VARCHAR));
            ub1.addUpdateValue("CUSTPAYDATENO", new DataValue(req.getRequest().getCustPayDateNo(), Types.VARCHAR));
            ub1.addUpdateValue("SALEINVOICECODE", new DataValue(req.getRequest().getSaleInvoiceCode(), Types.VARCHAR));

            ub1.addUpdateValue("COLLECT_SHOP", new DataValue(req.getRequest().getCollectShop(), Types.VARCHAR));

            String payer = req.getRequest().getPayer();
            if (StringUtils.isEmpty(payer)) {
                payer = req.getRequest().getBizPartnerNo();
            }
            String payee = req.getRequest().getPayee();
            if (StringUtils.isEmpty(payee)) {
                payee = req.getRequest().getBizPartnerNo();
            }
            ub1.addUpdateValue("PAYER", new DataValue(payer, Types.VARCHAR));
            ub1.addUpdateValue("PAYEE", new DataValue(payee, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(ub1)); // update

            // 交易对象主数据档_组织范围
            List<DCP_BizPartnerCreateReq.OrgList> orgLists = req.getRequest().getOrgList();
            DelBean db2 = new DelBean("DCP_BIZPARTNER_ORG");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("BIZPARTNERNO", new DataValue(bizPartnerNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));
            if (orgLists != null && orgLists.size() > 0) {
                for (DCP_BizPartnerCreateReq.OrgList par : orgLists) {
                    columns.Columns.clear();
                    columns.DataValues.clear();
                    columns.Add("EID", eId, Types.VARCHAR);
                    columns.Add("BIZPARTNERNO", bizPartnerNo, Types.VARCHAR);
                    columns.Add("ORGANIZATIONNO", par.getOrgNo(), Types.VARCHAR);
                    columns.Add("STATUS", Integer.valueOf(par.getStatus()), Types.INTEGER);

                    columns1 = columns.Columns.toArray(new String[0]);
                    insValue1 = columns.DataValues.toArray(new DataValue[0]);
                    // 交易对象主数据档_组织范围
                    InsBean ib2 = new InsBean("DCP_BIZPARTNER_ORG", columns1);
                    ib2.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }

            DelBean dbRSL = new DelBean("DCP_CUSTOMER_SHOP");
            dbRSL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            dbRSL.addCondition("CUSTOMERNO", new DataValue(bizPartnerNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(dbRSL));

            List<DCP_BizPartnerCreateReq.RestrictShopList> restrictShopLists = req.getRequest().getRestrictShopList();
            if (restrictShopLists != null && !restrictShopLists.isEmpty()) {
                for (DCP_BizPartnerCreateReq.RestrictShopList restrictShop : restrictShopLists) {

                    ColumnDataValue columnDataValue = new ColumnDataValue();

                    columnDataValue.Add("EID", eId, Types.VARCHAR);
                    columnDataValue.Add("CUSTOMERNO", req.getRequest().getBizPartnerNo(), Types.VARCHAR);
                    columnDataValue.Add("SHOPID", restrictShop.getShopId(), Types.VARCHAR);
                    columnDataValue.Add("LASTMODITIME", lastmoditime, Types.DATE);

                    InsBean ib2 = new InsBean("DCP_CUSTOMER_SHOP", columnDataValue.Columns.toArray(new String[0]));
                    ib2.addValues(columnDataValue.DataValues.toArray(new DataValue[0]));
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }

            //tag
            List<DCP_BizPartnerCreateReq.CustTagList> custTagLists = req.getRequest().getCustTagList();
            DelBean dbt = new DelBean("DCP_TAGTYPE_DETAIL");
            dbt.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            dbt.addCondition("ID", new DataValue(bizPartnerNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(dbt));
            if (custTagLists != null && custTagLists.size() > 0) {
                for (DCP_BizPartnerCreateReq.CustTagList par : custTagLists) {
                    sql = " SELECT * FROM DCP_TAGTYPE WHERE EID='%s'   AND TAGNO='%s' ";
                    sql = String.format(sql, eId, par.getTagNo());
                    List<Map<String, Object>> mDatas3 = this.doQueryData(sql, null);
                    if (!SUtil.EmptyList(mDatas3)) {
                        columns.Columns.clear();
                        columns.DataValues.clear();
                        columns.Add("EID", eId, Types.VARCHAR);
                        columns.Add("TAGGROUPTYPE", mDatas3.get(0).get("TAGGROUPTYPE").toString(), Types.VARCHAR);
                        columns.Add("TAGNO", par.getTagNo(), Types.VARCHAR);
                        columns.Add("TAGGROUPNO", mDatas3.get(0).get("TAGGROUPNO").toString(), Types.VARCHAR);
                        columns.Add("ID", bizPartnerNo, Types.VARCHAR);
                        columns.Add("NAME", req.getRequest().getSName(), Types.VARCHAR);
                        columns.Add("LASTMODITIME", lastmoditime, Types.DATE);
                        columns1 = columns.Columns.toArray(new String[0]);
                        insValue1 = columns.DataValues.toArray(new DataValue[0]);
                        //
                        InsBean ib2 = new InsBean("DCP_TAGTYPE_DETAIL", columns1);
                        ib2.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }
            }

            // 交易对象主数据档_联络方式
            List<DCP_BizPartnerCreateReq.ContactList> contactLists = req.getRequest().getContactList();
            DelBean db3 = new DelBean("DCP_BIZPARTNER_CONTACT");
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db3.addCondition("BIZPARTNERNO", new DataValue(bizPartnerNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));
            if (contactLists != null && contactLists.size() > 0) {
                for (DCP_BizPartnerCreateReq.ContactList par : contactLists) {
                    columns.Columns.clear();
                    columns.DataValues.clear();
                    columns.Add("EID", eId, Types.VARCHAR);
                    columns.Add("BIZPARTNERNO", bizPartnerNo, Types.VARCHAR);
                    columns.Add("ITEM ", par.getItem(), Types.VARCHAR);
                    columns.Add("CONTYPE", par.getConType(), Types.VARCHAR);
                    columns.Add("CONTENT", par.getContent(), Types.VARCHAR);
                    columns.Add("ISMAINCONTACT", par.getIsMainContact(), Types.VARCHAR);
                    columns.Add("CONTACT", par.getContact(), Types.VARCHAR);
                    columns.Add("STATUS", Integer.valueOf(par.getStatus()), Types.INTEGER);
                    columns.Add("MEMO ", par.getMemo(), Types.VARCHAR);

                    columns1 = columns.Columns.toArray(new String[0]);
                    insValue1 = columns.DataValues.toArray(new DataValue[0]);
                    // 交易对象主数据档_联络方式
                    InsBean ib2 = new InsBean("DCP_BIZPARTNER_CONTACT", columns1);
                    ib2.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }

            // 交易对象主数据档_结算账期
            //1 供应商 2 客户 3 全部
            sql = " SELECT * FROM DCP_BIZPARTNER WHERE EID='%s'   AND BIZPARTNERNO='%s' ";
            sql = String.format(sql, req.geteId(), bizPartnerNo);
            List<Map<String, Object>> mDatas2 = this.doQueryData(sql, null);
            String xBizType = req.getRequest().getBizType();
            String xBDate = req.getRequest().getBeginDate();
            String xEDate = req.getRequest().getEndDate();
            String billDateNo = req.getRequest().getBillDateNo();
            String billCustDateNo = req.getRequest().getCustBillDateNo();
            if ((!xBizType.equals(mDatas2.get(0).get("BIZTYPE").toString()))
                    || (!xBDate.equals(mDatas2.get(0).get("BEGINDATE").toString()))
                    || (!xEDate.equals(mDatas2.get(0).get("ENDDATE").toString()))
                    || (!billDateNo.equals(mDatas2.get(0).get("BILLDATENO").toString()))
                    || (!billCustDateNo.equals(mDatas2.get(0).get("CUSTBILLDATENO").toString()))
            ) {
                DelBean db5 = new DelBean("DCP_BIZPARTNER_BILL");
                db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db5.addCondition("BIZPARTNERNO", new DataValue(bizPartnerNo, Types.VARCHAR));
                db5.addCondition("ISCHECK", new DataValue("N", Types.VARCHAR));

                this.addProcessData(new DataProcessBean(db5));

                // 交易对象主数据档_结算账期
                //1 供应商 2 客户 3 全部

                String payType = req.getRequest().getPayType();  //供应商结算方式
                String custPayType = req.getRequest().getCustPayType(); //客户结算方式

                if ("3".equals(xBizType)) {
                    //这里 供应商结算方式 统结 和 分结
                    if ("1".equals(payType)) {
                        setBillList(req, res, bizPartnerNo, "1", "");
                    } else if ("2".equals(payType)) {  //分结 循环传入 多个结算中心
                        if (orgLists != null && orgLists.size() > 0) {
                            for (DCP_BizPartnerCreateReq.OrgList par : orgLists) {
                                setBillList(req, res, bizPartnerNo, "1", par.getOrgNo());
                            }
                        }

                    }
                    //这里 客户结算方式 统结 和 分结
                    if ("1".equals(custPayType)) {
                        setBillList(req, res, bizPartnerNo, "2", "");
                    } else if ("2".equals(custPayType)) {   //分结 循环传入 多个结算中心
                        if (orgLists != null && orgLists.size() > 0) {
                            for (DCP_BizPartnerCreateReq.OrgList par : orgLists) {
                                setBillList(req, res, bizPartnerNo, "2", par.getOrgNo());
                            }
                        }
                    }

                } else {
                    if ("2".equals(xBizType)) {  //如果是客户 结算类型用客户的
                        payType = custPayType;
                    }
                    //这里  结算方式 统结 和 分结
                    if ("1".equals(payType)) {
                        setBillList(req, res, bizPartnerNo, xBizType, "");
                    } else if ("2".equals(payType)) { //分结 循环传入 多个结算中心
                        if (orgLists != null && orgLists.size() > 0) {
                            for (DCP_BizPartnerCreateReq.OrgList par : orgLists) {
                                setBillList(req, res, bizPartnerNo, xBizType, par.getOrgNo());
                            }
                        }
                    }
                }

            }
            //供应商证照表
            DelBean db4 = new DelBean("DCP_SUPPLIER_LICENSE");
            db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db4.addCondition("SUPPLIER", new DataValue(bizPartnerNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db4));
            List<DCP_BizPartnerCreateReq.LicenseList> licenseLists = req.getRequest().getLicenseList();

            if (licenseLists != null && licenseLists.size() > 0) {
                for (DCP_BizPartnerCreateReq.LicenseList par : licenseLists) {
                    columns.Columns.clear();
                    columns.DataValues.clear();
                    columns.Add("EID", eId, Types.VARCHAR);
                    columns.Add("SUPPLIER", bizPartnerNo, Types.VARCHAR);
                    columns.Add("IMGTYPE", par.getImgType(), Types.VARCHAR);
                    columns.Add("ITEM", par.getItem(), Types.VARCHAR);
                    columns.Add("LICENSEIMG", par.getLicenseImg(), Types.VARCHAR);
                    String licen = "";
                    if (StringUtils.isEmpty(par.getLicenseNo()))
                        licen = " ";
                    columns.Add("LICENSENO", par.getLicenseNo() + licen, Types.VARCHAR);
                    columns.Add("BEGINDATE", par.getBeginDate(), Types.DATE);
                    columns.Add("ENDDATE", par.getEndDate(), Types.DATE);
                    columns.Add("STATUS", Integer.valueOf(par.getStatus()), Types.INTEGER);
                    columns.Add("CREATEOPID", req.getEmployeeNo(), Types.VARCHAR);
                    columns.Add("CREATEDEPTID", req.getDepartmentNo(), Types.VARCHAR);
                    columns.Add("CREATETIME", lastmoditime, Types.DATE);


                    columns1 = columns.Columns.toArray(new String[0]);
                    insValue1 = columns.DataValues.toArray(new DataValue[0]);
                    // 交易对象主数据档_结算账期
                    InsBean ib2 = new InsBean("DCP_SUPPLIER_LICENSE", columns1);
                    ib2.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib2));
                }

            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } else {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 对象信息：" + bizPartnerNo + "不存在 ");
            return;
        }
    }

    private void setBillList(DCP_BizPartnerCreateReq req, DCP_BizPartnerRes res, String bizPartnerNo, String xBizType, String xPayCenter) throws Exception {

        // TODO Auto-generated method stub
        ColumnDataValue columns = new ColumnDataValue();
        String[] columns1 = null;
        DataValue[] insValue1 = null;
        String sql = " SELECT   * FROM DCP_BILLDATE a "
                //+" inner join DCP_BILLDATE b on a.eid=b.eid and  a.BILLDATENO = b.BILLDATENO "
                + "  WHERE EID='%s'  AND BILLDATENO='%s'   ";
        String billDateNo = null;
        String payCenter = null;
        //1 供应商 2 客户 3 全部

        if ("1".equals(xBizType)) {
            billDateNo = req.getRequest().getBillDateNo();
            payCenter = req.getRequest().getPayCenter();
        } else if ("2".equals(xBizType)) {
            billDateNo = req.getRequest().getCustBillDateNo();
            payCenter = req.getRequest().getCustPayCenter();
        } else if ("3".equals(xBizType)) {  //不会有此 ，上面做了判断
            billDateNo = req.getRequest().getBillDateNo();
            payCenter = req.getRequest().getPayCenter();
        }
        //如果传入的结算中心不为空 ，以传入为准，表示是 分结
        if (!"".equals(xPayCenter)) {
            payCenter = xPayCenter;
        }
        sql = String.format(sql, req.geteId(), billDateNo);
        List<Map<String, Object>> mDatas2 = this.doQueryData(sql, null);
        if (!SUtil.EmptyList(mDatas2)) {
            String xBDate = req.getRequest().getBeginDate();
            String xEDate = req.getRequest().getEndDate();
            String xBillType = mDatas2.get(0).get("BILLTYPE").toString();

            String xFDate = mDatas2.get(0).get("FDATE").toString();
            int incMonth = Integer.valueOf(mDatas2.get(0).get("ADDMONTHS").toString());
            int incDay = Integer.valueOf(mDatas2.get(0).get("ADDDAYS").toString());
            if (!DCP_BillDateCalCulate.checkCond(xBillType, xBDate, xEDate, xFDate)) {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败: 账期信息异常，请检查 ");
                return;
            }
            List<String> listInsDay = new ArrayList<String>();
            if ("1".equals(xBillType)) {

                listInsDay = DCP_BillDateCalCulate.getDay(xBillType, xBDate, xEDate, xFDate);
            } else if ("2".equals(xBillType)) {
                listInsDay = DCP_BillDateCalCulate.getDay(xBDate, xEDate, xFDate, incMonth);
            } else if ("3".equals(xBillType)) {
                listInsDay = DCP_BillDateCalCulate.getDay(xBDate, xEDate, incDay);
            } else if ("4".equals(xBillType)) {
                listInsDay = DCP_BillDateCalCulate.getDay(xBDate, xEDate);
            }
            String[] day1 = null;
            listInsDay = DCP_BillDateCalCulate.oprList(xBDate, xEDate, listInsDay);
            if (!SUtil.EmptyList(listInsDay))
                for (int i = 0; i < listInsDay.size(); i++) {
                    day1 = listInsDay.get(i).split(",");
                    columns.Columns.clear();
                    columns.DataValues.clear();
                    columns.Add("EID", req.geteId(), Types.VARCHAR);
                    columns.Add("BIZPARTNERNO", bizPartnerNo, Types.VARCHAR);
                    if ("3".equals(xBizType)) {
                        columns.Add("BILLTYPE", 1, Types.VARCHAR);
                    } else {
                        columns.Add("BILLTYPE", xBizType, Types.VARCHAR);
                    }
                    columns.Add("CONTRACTNO", "1", Types.VARCHAR);
                    columns.Add("ORGANIZATIONNO", payCenter, Types.VARCHAR);
                    columns.Add("ITEM", String.valueOf(i + 1), Types.VARCHAR);
                    columns.Add("BDATE", day1[0], Types.DATE);
                    columns.Add("EDATE", day1[1], Types.DATE);
                    columns.Add("BILLDATE", day1[1], Types.DATE);
                    columns.Add("ISCHECK", "N", Types.VARCHAR);
                    columns.Add("BILLNO", "", Types.VARCHAR);

                    columns1 = columns.Columns.toArray(new String[0]);
                    insValue1 = columns.DataValues.toArray(new DataValue[0]);
                    // 交易对象主数据档_结算账期
                    InsBean ib2 = new InsBean("DCP_BIZPARTNER_BILL", columns1);
                    ib2.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib2));


                }
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BizPartnerCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BizPartnerCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BizPartnerCreateReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_BizPartnerCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        // 必传值不为空
        String PayDate = req.getRequest().getBizType();

        if (Check.Null(PayDate)) {
            errMsg.append("交易对象类型不能为空值 ");
            isFail = true;
        }


        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BizPartnerCreateReq> getRequestType() {
        return new TypeToken<DCP_BizPartnerCreateReq>() {
        };
    }

    @Override
    protected DCP_BizPartnerRes getResponseType() {
        return new DCP_BizPartnerRes();
    }


    /**
     * 判断 信息时候已存在或重复
     */
    private String isRepeat(String... key) {
        String sql = null;
        sql = " SELECT * FROM DCP_BIZPARTNER WHERE EID='%s'   AND BIZPARTNERNO='%s' ";
        sql = String.format(sql, key);
        return sql;
    }

}
	
