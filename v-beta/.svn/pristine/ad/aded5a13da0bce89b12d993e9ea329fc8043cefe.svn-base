package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BizPartnerCreateReq;
import com.dsc.spos.json.cust.res.DCP_BizPartnerRes;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
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
 * @date 2024-10-23
 */
public class DCP_BizPartnerCreate extends SPosAdvanceService<DCP_BizPartnerCreateReq, DCP_BizPartnerRes> {

    @Override
    protected void processDUID(DCP_BizPartnerCreateReq req, DCP_BizPartnerRes res) throws Exception {
        processOnCreate(req, res);
    }

    protected String getBizPNO(String eId, String shopId, String orgId) throws Exception {
        String sql = null;
        String templateNo = null;
        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBIZPNO('" + eId + "','" + shopId + "','DCP_BizPartnerCreate') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false) {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        } else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取单号失败！");
        }
        return templateNo;
    }

    private void processOnCreate(DCP_BizPartnerCreateReq req, DCP_BizPartnerRes res) throws Exception {
        String PayDateNo = req.getRequest().getPayDateNo();
        String eId = req.geteId();
        String bizPartnerNo = req.getRequest().getBizPartnerNo();
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = null;
        sql = " SELECT * FROM DCP_BIZPARTNER WHERE EID='%s'   AND FNAME='%s' ";
        sql = String.format(sql, req.geteId(), req.getRequest().getFName());
        List<Map<String, Object>> mDatas1 = this.doQueryData(sql, null);
        if (!SUtil.EmptyList(mDatas1)) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 交易对象全称已经存在 ");
            return;
        }
        List<Map<String, Object>> mDatas = null;
        if (StringUtils.isEmpty(bizPartnerNo)) {
            bizPartnerNo = getBizPNO(eId, "001", "");
        } else {
            sql = this.isRepeat(eId, bizPartnerNo);
            mDatas = this.doQueryData(sql, null);
        }

        if (SUtil.EmptyList(mDatas)) {
            ColumnDataValue columns = new ColumnDataValue();
            String[] columns1 = null;
            DataValue[] insValue1 = null;

            columns.Columns.clear();
            columns.DataValues.clear();
            columns.Add("EID", eId, Types.VARCHAR);
            columns.Add("ORGANIZATIONNO", req.getOrganizationNO(), Types.VARCHAR);
            columns.Add("BIZTYPE", req.getRequest().getBizType(), Types.VARCHAR);
            columns.Add("BIZPARTNERNO", bizPartnerNo, Types.VARCHAR);
            columns.Add("SNAME", req.getRequest().getSName(), Types.VARCHAR);
            columns.Add("FNAME", req.getRequest().getFName(), Types.VARCHAR);
            columns.Add("CORPTYPE", req.getRequest().getCorpType(), Types.VARCHAR);
            columns.Add("REGISTERNO", req.getRequest().getRegisterNo(), Types.VARCHAR);
            columns.Add("LEGALPERSON", req.getRequest().getLegalPerson(), Types.VARCHAR);
            columns.Add("TAXPAYER_NO", req.getRequest().getTaxPayerNo(), Types.VARCHAR);
            columns.Add("MAINCATEGORY", req.getRequest().getMainCatgegory(), Types.VARCHAR);
            columns.Add("MAINBRAND", req.getRequest().getMainBrand(), Types.VARCHAR);
            columns.Add("PUREMPNO", req.getRequest().getPurEmpNo(), Types.VARCHAR);
            columns.Add("PUREDEPTNO", req.getRequest().getPurDeptNo(), Types.VARCHAR);
            columns.Add("GRADE", req.getRequest().getGrade(), Types.VARCHAR);
            columns.Add("ROLE", req.getRequest().getRole(), Types.VARCHAR);
            //columns.Add("ENABLECONTRACT", req.getRequest().getEnableContract()., Types.VARCHAR);
            columns.Add("PAYTYPE", req.getRequest().getPayType(), Types.VARCHAR);
            columns.Add("PAYCENTER", req.getRequest().getPayCenter(), Types.VARCHAR);
            columns.Add("BILLDATENO", req.getRequest().getBillDateNo(), Types.VARCHAR);
            columns.Add("TAXCODE", req.getRequest().getTaxCode(), Types.VARCHAR);
            columns.Add("INVOICECODE", req.getRequest().getInvoiceCode(), Types.VARCHAR);
            columns.Add("PAYDATENO", req.getRequest().getPayDateNo(), Types.VARCHAR);
            columns.Add("MAINCURRENCY ", req.getRequest().getMainCurrency(), Types.VARCHAR);

            columns.Add("BEGINDATE", DateFormatUtils.getDateTime(req.getRequest().getBeginDate()), Types.DATE);
            columns.Add("ENDDATE", DateFormatUtils.getDateTime(req.getRequest().getEndDate()), Types.DATE);
            columns.Add("STATUS", Integer.valueOf(req.getRequest().getStatus()), Types.INTEGER);
            columns.Add("MEMO ", req.getRequest().getMemo(), Types.VARCHAR);

            columns.Add("CREATEOPID", req.getEmployeeNo(), Types.VARCHAR);
            columns.Add("CREATEDEPTID", req.getDepartmentNo(), Types.VARCHAR);
            columns.Add("CREATETIME", lastmoditime, Types.DATE);
            columns.Add("LASTMODIOPID", req.getDepartmentNo(), Types.VARCHAR);
            columns.Add("LASTMODITIME", lastmoditime, Types.DATE);

            columns.Add("SALEEMPNO", req.getRequest().getSaleEmpNo(), Types.VARCHAR);
            columns.Add("SALEDEPTNO ", req.getRequest().getSaleDeptNo(), Types.VARCHAR);
            columns.Add("CUSTGRADE", req.getRequest().getCustGrade(), Types.VARCHAR);
            columns.Add("CUSTPAYTYPE", req.getRequest().getCustPayType(), Types.VARCHAR);
            columns.Add("CUSTPAYCENTER", req.getRequest().getCustPayCenter(), Types.VARCHAR);
            columns.Add("CUSTBILLDATENO ", req.getRequest().getCustBillDateNo(), Types.VARCHAR);
            columns.Add("CUSTPAYDATENO", req.getRequest().getCustPayDateNo(), Types.VARCHAR);
            columns.Add("SALEINVOICECODE", req.getRequest().getSaleInvoiceCode(), Types.VARCHAR);

            columns.Add("OFFICE_ADDRESS", req.getRequest().getOfficeAddress(), Types.VARCHAR);
            columns.Add("DELIVERY_ADDRESS", req.getRequest().getDeliveryAddress(), Types.VARCHAR);
            columns.Add("INVOICE_ADDRESS", req.getRequest().getInvoiceAddress(), Types.VARCHAR);
            columns.Add("ADDRESS", req.getRequest().getAddress(), Types.VARCHAR);
            columns.Add("IS_CREDIT", req.getRequest().getIsCredit(), Types.VARCHAR);

            if (StringUtils.isNotEmpty(req.getRequest().getCreditAmt())) {
                columns.Add("CREDIT_AMT", req.getRequest().getCreditAmt(), Types.NUMERIC);
            }
            columns.Add("CREDIT_TYPE", req.getRequest().getCreditType(), Types.VARCHAR);

            if (StringUtils.isNotEmpty(req.getRequest().getRestrictShop())) {
                columns.Add("RESTRICTSHOP", req.getRequest().getRestrictShop(), Types.NUMERIC);
            }

            columns.Add("COLLECT_OBJECT", req.getRequest().getCollectObject(), Types.VARCHAR);
            columns.Add("COLLECT_SHOP", req.getRequest().getCollectShop(), Types.VARCHAR);

            String payer = req.getRequest().getPayer();
            if (StringUtils.isEmpty(payer)) {
                payer = bizPartnerNo;
            }
            String payee = req.getRequest().getPayee();
            if (StringUtils.isEmpty(payee)) {
                payee = bizPartnerNo;
            }
            columns.Add("PAYER", payer, Types.VARCHAR);
            columns.Add("PAYEE", payee, Types.VARCHAR);


            columns1 = columns.Columns.toArray(new String[0]);
            insValue1 = columns.DataValues.toArray(new DataValue[0]);
            InsBean ib1 = new InsBean("DCP_BIZPARTNER", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增

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


            // 交易对象主数据档_组织范围
            List<DCP_BizPartnerCreateReq.OrgList> orgLists = req.getRequest().getOrgList();

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
            //taglist
            List<DCP_BizPartnerCreateReq.CustTagList> custCustTagLists = req.getRequest().getCustTagList();

            if (custCustTagLists != null && !custCustTagLists.isEmpty()) {
                for (DCP_BizPartnerCreateReq.CustTagList par : custCustTagLists) {
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

                        InsBean ib2 = new InsBean("DCP_TAGTYPE_DETAIL", columns.Columns.toArray(new String[0]));
                        ib2.addValues(columns.DataValues.toArray(new DataValue[0]));
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }
            }


            // 交易对象主数据档_联络方式
            List<DCP_BizPartnerCreateReq.ContactList> contactLists = req.getRequest().getContactList();

            if (contactLists != null && !contactLists.isEmpty()) {
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


                    // 交易对象主数据档_联络方式
                    InsBean ib2 = new InsBean("DCP_BIZPARTNER_CONTACT", columns.Columns.toArray(new String[0]));
                    ib2.addValues(columns.DataValues.toArray(new DataValue[0]));
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }

            // 交易对象主数据档_结算账期
            //1 供应商 2 客户 3 全部
            String xBizType = req.getRequest().getBizType();
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

            //供应商证照表
            List<DCP_BizPartnerCreateReq.LicenseList> licenseLists = req.getRequest().getLicenseList();

            if (licenseLists != null && !licenseLists.isEmpty()) {
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

                    // 交易对象主数据档_结算账期
                    InsBean ib2 = new InsBean("DCP_SUPPLIER_LICENSE", columns.Columns.toArray(new String[0]));
                    ib2.addValues(columns.DataValues.toArray(new DataValue[0]));
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setBizPartnerNo(bizPartnerNo);
            res.setServiceDescription("服务执行成功 ");
        } else {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 交易对象编号：" + bizPartnerNo + "已存在 ");
            return;
        }
    }

    private void setBillList(DCP_BizPartnerCreateReq req, DCP_BizPartnerRes res, String bizPartnerNo, String xBizType, String xPayCenter) throws Exception {

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


        List<DCP_BizPartnerCreateReq.OrgList> orgLists = req.getRequest().getOrgList();
        for (DCP_BizPartnerCreateReq.OrgList par : orgLists) {
            if (Check.Null(par.getOrgNo())) {
                errMsg.append("组织编号不可为空值, ");
                isFail = true;
            }
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
	
