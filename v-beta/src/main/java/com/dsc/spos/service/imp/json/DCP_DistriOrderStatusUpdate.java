package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DistriOrderStatusUpdateReq;
import com.dsc.spos.json.cust.req.DCP_POrderStatusUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ReceivingStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DistriOrderStatusUpdateRes;
import com.dsc.spos.json.cust.res.DCP_ReceivingStatusUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DCP_DistriOrderStatusUpdate extends SPosAdvanceService<DCP_DistriOrderStatusUpdateReq, DCP_DistriOrderStatusUpdateRes> {

    private static final String TYPE_CONFIRM = "confirm";
    private static final String TYPE_UNCONFIRM = "unconfirm";
    private static final String TYPE_CANCEL = "cancel";

    @Override
    protected boolean isVerifyFail(DCP_DistriOrderStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_DistriOrderStatusUpdateReq.LevelElm request = req.getRequest();

        if (Check.Null(request.getBillNo())){
            errMsg.append("铺货单号不可为空值, ");
            isFail = true;
        }

        if (Check.Null(request.getOpType())){
            errMsg.append("操作类型不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_DistriOrderStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_DistriOrderStatusUpdateReq>(){};
    }

    @Override
    protected DCP_DistriOrderStatusUpdateRes getResponseType() {
        return new DCP_DistriOrderStatusUpdateRes();
    }

    @Override
    public void processDUID(DCP_DistriOrderStatusUpdateReq req,DCP_DistriOrderStatusUpdateRes res) throws Exception {
        //枚举: confirm：审核,unconfirm：取消审核,cancel：作废
        String billNo = req.getRequest().getBillNo();
        String opType = req.getRequest().getOpType();
        String eid = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String companyId = req.getBELFIRM();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String nowDate = new SimpleDateFormat("yyyyMMdd").format(new Date());


        String querySql = this.getQuerySql(req);
        List<Map<String, Object>> mainData = dao.executeQuerySQL(querySql, null);

        if(mainData.size()==0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在");
        }
        String status = mainData.get(0).get("STATUS").toString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String bdate = mainData.get(0).get("BDATE").toString();
        if(TYPE_CANCEL.equals(opType)){
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非【新建】不可作废！");
            }
            UptBean ub2 = new UptBean("DCP_DITRIORDER");
            ub2.addUpdateValue("STATUS", DataValues.newString("2"));//2-已作废
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CANCELBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CANCELTIME", DataValues.newDate(lastmoditime));


            ub2.addCondition("EID", DataValues.newString(eid));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        if(TYPE_UNCONFIRM.equals(opType)){
            if(!"1".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非【审核】不可取消审核！");
            }

            String existPOrderSql = getExistPOrderSql(req);
            List<Map<String, Object>> existPOrderData = dao.executeQuerySQL(existPOrderSql, null);
            if(existPOrderData!=null&&existPOrderData.size()>0){
                List<Map<String, Object>> validList = existPOrderData.stream().filter(x -> x.get("STATUS").toString().equals("1")).collect(Collectors.toList());
                if(validList!=null&&validList.size()>0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "要货申请单状态存在已审核,不可取消审核！");
                }
                for (Map<String, Object> item : existPOrderData){
                    String deleteNo=item.get("PORDERNO").toString();
                    DelBean db1 = new DelBean("DCP_PORDER");
                    db1.addCondition("PORDERNO", new DataValue(deleteNo,Types.VARCHAR));
                    db1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                    db1.addCondition("ORGANIZATIONNO",new DataValue(item.get("ORGANIZATIONNO").toString(),Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    DelBean db2 = new DelBean("DCP_PORDER_DETAIL");
                    db2.addCondition("PORDERNO", new DataValue(deleteNo,Types.VARCHAR));
                    db2.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                    db2.addCondition("ORGANIZATIONNO",new DataValue(item.get("ORGANIZATIONNO").toString(),Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));

                    DelBean db3 = new DelBean("DCP_DEMAND");
                    db3.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                    db3.addCondition("ORGANIZATIONNO", new DataValue(item.get("ORGANIZATIONNO").toString(), Types.VARCHAR));
                    db3.addCondition("ORDERNO", new DataValue(deleteNo, Types.VARCHAR));
                    db3.addCondition("ORDERTYPE", new DataValue("1", Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db3));

                }
            }

            UptBean ub2 = new UptBean("DCP_DITRIORDER");
            ub2.addUpdateValue("STATUS", DataValues.newString("0"));//0-新建
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(null));

            ub2.addCondition("EID", DataValues.newString(eid));
            ub2.addCondition("ORGANIZATIONNO",DataValues.newString(organizationNO));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        List<String> porderNos=new ArrayList();
        if(TYPE_CONFIRM.equals(opType)){
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String bDate = df.format(cal.getTime());
            String createDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String createTime = df.format(cal.getTime());

            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非【新建】不可审核！");
            }

            String employeeid = mainData.get(0).get("EMPLOYEEID").toString();
            String departid = mainData.get(0).get("DEPARTID").toString();
            String ownopid = mainData.get(0).get("OWNOPID").toString();
            String owndeptid = mainData.get(0).get("OWNDEPTID").toString();
            String createdeptid = mainData.get(0).get("CREATEDEPTID").toString();

            String detailSql = this.getDetailSql(req);
            List<Map<String, Object>> detailData = dao.executeQuerySQL(detailSql, null);
            if(detailData.size()==0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据明细不存在");
            }
            List<Map<String, Object>> va1 = detailData.stream().filter(x -> Check.Null(x.get("SUPPLIERTYPE").toString()) || Check.Null(x.get("SUPPLIERID").toString())).collect(Collectors.toList());
            if(va1!=null&&va1.size()>0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "  单身明细【供货方式】或【供货对象】不可为空！");
            }
            List<Map<String, Object>> va2 = detailData.stream().filter(x -> {
                int com = new BigDecimal(x.get("PQTY").toString()).compareTo(BigDecimal.ZERO);
                return com <= 0;
            }).collect(Collectors.toList());
            if(va2!=null&&va2.size()>0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "  单身明细【采购数量】不可小于0！");
            }

            //取价格
            String pluSql="select distinct a.pluno,goodsunit.unitratio,a.PUNIT,a.baseunit,a.SUPPRICE,a.price from dcp_goods a " +
                    " inner join DCP_DITRIORDER_DETAIL b on a.eid=b.eid and a.pluno=b.pluno   " +
                    " inner join dcp_goods_unit goodsunit " +
                    " on goodsunit.eid=a.eid and goodsunit.pluno=b.pluno and goodsunit.ounit=b.punit  " +//and prod_unit_use='Y'
                    " and goodsunit.unit=b.baseunit ";
            pluSql+=" where a.eid='"+eid+"' and b.BILLNO='"+billNo+"' ";
            List<Map<String, Object>> getQPlu = dao.executeQuerySQL(pluSql, null);



            //产生要货申请单：按【需求组织+需求日期+供货方式+供货对象】拆分多张要货申请单，并逐笔执行提交要货申请单
            List<Map> mapList = detailData.stream().map(x -> {
                String supplierType = x.get("SUPPLIERTYPE").toString();
                String supplierId = x.get("SUPPLIERID").toString();
                String demandorgno = x.get("DEMANDORGNO").toString();
                String rdate = x.get("RDATE").toString();
                Map map = new HashMap();
                map.put("supplierType", supplierType);
                map.put("supplierId", supplierId);
                map.put("demandorgno", demandorgno);
                map.put("rdate", rdate);
                return map;
            }).distinct().collect(Collectors.toList());
            for (Map item : mapList){

                String supplierType = item.get("supplierType").toString();
                String supplierId = item.get("supplierId").toString();
                String demandorgno = item.get("demandorgno").toString();
                String rdate = item.get("rdate").toString();
                String orderNO = this.getOrderNO(req,demandorgno, "YHSQ");
                porderNos.add(orderNO);

                StringBuilder orgSqlSb=new StringBuilder("select * from dcp_org WHERE eid='"+eid+"'" +
                        " and organizationno='"+demandorgno+"' ");
                List<Map<String, Object>> orgList = dao.executeQuerySQL(orgSqlSb.toString(), null);
                if(orgList.size()==0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据明细中的需求组织不存在！");
                }
                String warehouse = orgList.get(0).get("IN_COST_WAREHOUSE").toString();

                List<Map<String, Object>> detailCollect = detailData.stream().filter(x -> x.get("SUPPLIERTYPE").toString().equals(supplierType) && x.get("SUPPLIERID").toString().equals(supplierId)
                        && x.get("DEMANDORGNO").toString().equals(demandorgno) && x.get("RDATE").toString().equals(rdate)).collect(Collectors.toList());

                BigDecimal totAmt=BigDecimal.ZERO;
                BigDecimal totPqty=BigDecimal.ZERO;
                BigDecimal totDistriAmt=BigDecimal.ZERO;
                int detailItem=0;
                for (Map<String, Object> detailMap : detailCollect){
                    detailItem++;

                    String pluno = detailMap.get("PLUNO").toString();
                    String punit = detailMap.get("PUNIT").toString();

                    List<Map<String, Object>> plus = new ArrayList<Map<String, Object>>();
                    for (Map<String, Object> onePlu :getQPlu) {
                        Map<String, Object> plu = new HashMap<String, Object>();
                        plu.put("PLUNO", onePlu.get("PLUNO").toString());
                        plu.put("PUNIT", onePlu.get("PUNIT").toString());
                        plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
                        plu.put("UNITRATIO", onePlu.get("UNITRATIO").toString());
                        plus.add(plu);
                    }

                    Map<String, Boolean> getPriceCondition = new HashMap<>(); //查詢條件
                    getPriceCondition.put("PLUNO", true);
                    getPriceCondition.put("PUNIT", true);
                    getPriceCondition.put("BASEUNIT", true);

                    //调用过滤函数
                    MyCommon mc = new MyCommon();
                    List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, eid, companyId, organizationNO, plus,companyId);
                    String price="0";
                    String distriPrice="0";

                    Map<String, Object> condiV = new HashMap<String, Object>();
                    condiV.put("PLUNO", pluno);
                    condiV.put("PUNIT", punit);
                    List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                    if (priceList != null && priceList.size() > 0) {
                        price = priceList.get(0).get("PRICE").toString();
                        distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                    }
                    List<Map<String, Object>> yetPluno = getQPlu.stream().filter(x -> x.get("PLUNO").toString().equals(pluno)).collect(Collectors.toList());
                    if(distriPrice.equals("0")){
                        if(yetPluno!=null&&yetPluno.size()>0){
                            distriPrice=yetPluno.get(0).get("SUPPRICE").toString();
                        }
                    }
                    if(price.equals("0")){
                        if(yetPluno!=null&&yetPluno.size()>0){
                            price=yetPluno.get(0).get("PRICE").toString();
                        }
                    }
                    String unitRatio="0";
                    if(yetPluno!=null&&yetPluno.size()>0){
                        unitRatio=yetPluno.get(0).get("UNITRATIO").toString();
                    }
                    BigDecimal amt = new BigDecimal(price).multiply(new BigDecimal(detailMap.get("PQTY").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal distriAmt = new BigDecimal(distriPrice).multiply(new BigDecimal(detailMap.get("PQTY").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);

                    totAmt=totAmt.add(amt);
                    totDistriAmt=totDistriAmt.add(distriAmt);
                    totPqty=totPqty.add(new BigDecimal(detailMap.get("PQTY").toString()));

                    ColumnDataValue pOrderDetailColumns=new ColumnDataValue();
                    pOrderDetailColumns.add("SHOPID",demandorgno,Types.VARCHAR);
                    pOrderDetailColumns.add("PORDERNO",orderNO,Types.VARCHAR);
                    pOrderDetailColumns.add("ITEM",String.valueOf(detailItem),Types.VARCHAR);
                    pOrderDetailColumns.add("EID",eid,Types.VARCHAR);
                    pOrderDetailColumns.add("ORGANIZATIONNO",demandorgno,Types.VARCHAR);
                    pOrderDetailColumns.add("PUNIT",detailMap.get("PUNIT").toString(),Types.VARCHAR);
                    pOrderDetailColumns.add("WAREHOUSE",warehouse,Types.VARCHAR);
                    pOrderDetailColumns.add("PRICE",price,Types.VARCHAR);
                    pOrderDetailColumns.add("BASEQTY",detailMap.get("BASEQTY").toString(),Types.VARCHAR);
                    pOrderDetailColumns.add("PQTY",detailMap.get("PQTY").toString(),Types.VARCHAR);
                    pOrderDetailColumns.add("BASEUNIT",detailMap.get("BASEUNIT").toString(),Types.VARCHAR);
                    pOrderDetailColumns.add("PLUNO",pluno,Types.VARCHAR);
                    pOrderDetailColumns.add("UNIT_RATIO",unitRatio,Types.VARCHAR);
                    pOrderDetailColumns.add("AMT",amt,Types.VARCHAR);
                    pOrderDetailColumns.add("DISTRIPRICE",distriPrice,Types.VARCHAR);
                    pOrderDetailColumns.add("ISNEW","N",Types.VARCHAR);
                    pOrderDetailColumns.add("DISTRIAMT",distriAmt,Types.VARCHAR);
                    pOrderDetailColumns.add("BDATE",bDate,Types.VARCHAR);
                    pOrderDetailColumns.add("HEADSTOCKQTY","0",Types.VARCHAR);//todo
                    pOrderDetailColumns.add("FEATURENO",Check.Null(detailMap.get("FEATURENO").toString())?" ":detailMap.get("FEATURENO").toString(),Types.VARCHAR);

                    pOrderDetailColumns.add("SUPPLIERTYPE",detailMap.get("SUPPLIERTYPE").toString(),Types.VARCHAR);
                    pOrderDetailColumns.add("SUPPLIERID",detailMap.get("SUPPLIERID").toString(),Types.VARCHAR);
                    pOrderDetailColumns.add("PLUBARCODE",detailMap.get("PLUBARCODE").toString(),Types.VARCHAR);
                    pOrderDetailColumns.add("OFNO",detailMap.get("BILLNO").toString(),Types.VARCHAR);
                    pOrderDetailColumns.add("OITEM",detailMap.get("ITEM").toString(),Types.VARCHAR);

                    String[] pOrderDetailColumnNames = pOrderDetailColumns.getColumns().toArray(new String[0]);
                    DataValue[] pOrderDetailDataValues = pOrderDetailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib1=new InsBean("DCP_PORDER_DETAIL",pOrderDetailColumnNames);
                    ib1.addValues(pOrderDetailDataValues);
                    this.addProcessData(new DataProcessBean(ib1));

                }

                List<Map> collect = detailCollect.stream().map(z -> {
                    Map map = new HashMap<>();
                    map.put("pluNo", z.get("PLUNO").toString());
                    map.put("featureNo", z.get("FEATURENO").toString());
                    return map;
                }).distinct().collect(Collectors.toList());
                BigDecimal totCqty=new BigDecimal(collect.size());


                ColumnDataValue pOrderColumns=new ColumnDataValue();
                pOrderColumns.add("SHOPID",demandorgno,Types.VARCHAR);
                pOrderColumns.add("PORDERNO",orderNO,Types.VARCHAR);
                pOrderColumns.add("EID",eid,Types.VARCHAR);
                pOrderColumns.add("ORGANIZATIONNO",demandorgno,Types.VARCHAR);
                pOrderColumns.add("BDATE",nowDate,Types.VARCHAR);
                pOrderColumns.add("IS_ADD","N",Types.VARCHAR);
                pOrderColumns.add("TOT_AMT",totAmt.toString(),Types.VARCHAR);
                pOrderColumns.add("RDATE",bdate,Types.VARCHAR);
                pOrderColumns.add("TOT_PQTY",totPqty.toString(),Types.VARCHAR);
                pOrderColumns.add("TOT_CQTY",totCqty.toString(),Types.VARCHAR);
                pOrderColumns.add("CREATEBY",req.getOpNO(),Types.VARCHAR);
                pOrderColumns.add("CREATE_DATE",createDate,Types.VARCHAR);
                pOrderColumns.add("CREATE_TIME",createTime,Types.VARCHAR);
                pOrderColumns.add("ISURGENTORDER","N",Types.VARCHAR);
                pOrderColumns.add("RECEIPT_ORG",supplierId,Types.VARCHAR);
                pOrderColumns.add("OFNO",billNo,Types.VARCHAR);
                pOrderColumns.add("OTYPE","7",Types.VARCHAR);
                pOrderColumns.add("TOT_DISTRIAMT",totDistriAmt.toString(),Types.VARCHAR);
                pOrderColumns.add("ISFORECAST","N",Types.VARCHAR);
                pOrderColumns.add("STATUS","0",Types.VARCHAR);//新建
                pOrderColumns.add("SUPPLIERTYPE",supplierType,Types.VARCHAR);
                pOrderColumns.add("EMPLOYEEID",employeeid,Types.VARCHAR);
                pOrderColumns.add("DEPARTID",departid,Types.VARCHAR);
                pOrderColumns.add("OWNOPID",ownopid,Types.VARCHAR);
                pOrderColumns.add("OWNDEPTID",owndeptid,Types.VARCHAR);
                pOrderColumns.add("CREATEDEPTID",createdeptid,Types.VARCHAR);

                String[] pOrderColumnNames = pOrderColumns.getColumns().toArray(new String[0]);
                DataValue[] pOrderDataValues = pOrderColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_PORDER",pOrderColumnNames);
                ib1.addValues(pOrderDataValues);
                this.addProcessData(new DataProcessBean(ib1));
            }

            UptBean ub2 = new UptBean("DCP_DITRIORDER");
            ub2.addUpdateValue("STATUS", DataValues.newString("1"));
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(lastmoditime));

            ub2.addCondition("EID", DataValues.newString(eid));
            ub2.addCondition("ORGANIZATIONNO",DataValues.newString(organizationNO));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        this.doExecuteDataToDB();

        if(porderNos.size()>0){
            ParseJson pj = new ParseJson();
            for (String thisOrderNo:porderNos){
                this.confirmOPorder(req,thisOrderNo);
            }
        }

    }

    private void confirmOPorder(DCP_DistriOrderStatusUpdateReq req,String pOrderNo) throws Exception{

        String sql = " SELECT a.STATUS ,a.PTEMPLATENO ,b.OPTIONAL_TIME ,a.organizationno,a.EMPLOYEEID,a.departid,a.rdate,a.BDATE," +
                " a.ISURGENTORDER,a.SUBMIT_TIME,a.PTEMPLATENO,a.SUPPLIERTYPE,a.RECEIPT_ORG  from  DCP_PORDER a left join DCP_PTEMPLATE b on a.eid=b.eid "
                +" and a.PTEMPLATENO=b.PTEMPLATENO and b.DOC_TYPE ='0' WHERE a.EID='%s'   AND a.PORDERNO='%s'   ";
        sql = String.format(sql, req.geteId(), pOrderNo);
        List<Map<String, Object>> list = this.doQueryData(sql, null);

        String eId=req.geteId();
        String billNo = pOrderNo;
        Map<String, Object> singleMap = list.get(0);
        String organizationNo = singleMap.get("ORGANIZATIONNO").toString();
        String employeeID = singleMap.get("EMPLOYEEID").toString();
        String departID = singleMap.get("DEPARTID").toString();
        String rDate = singleMap.get("RDATE").toString();
        String receipt_org = singleMap.get("RECEIPT_ORG").toString();
        String pTemplateNo = singleMap.get("PTEMPLATENO").toString();


        //查询所有组织
        String orgSql="select * from dcp_org where eid='"+req.geteId()+"'";
        List<Map<String, Object>> allOrgDatas = this.doQueryData(orgSql, null);
        //查询明细
        String detailSql="select a.*,u1.UNITRATIO as punitratio,u2.UNITRATIO as wunitratio,u3.udlength as pudlength,u4.udlength as wudlength," +
                " o1.IN_COST_WAREHOUSE,o1.OUT_COST_WAREHOUSE,g.wunit  from " +
                " dcp_porder_detail a" +
                " left join dcp_porder b on a.eid=b.eid and a.organizationno=b.organizationno and a.porderno=b.porderno " +
                " inner join dcp_goods g on a.pluno=g.pluno and a.eid=g.eid " +
                " left join DCP_GOODS_UNIT u1 on u1.eid=a.eid and u1.pluno=a.pluno and u1.ounit=a.punit and u1.unit=a.baseunit" +
                " left join DCP_GOODS_UNIT u2 on u2.eid=a.eid and u2.pluno=a.pluno and u2.ounit=g.wunit and u2.unit=a.baseunit" +
                " left join DCP_UNIT u3 on u3.eid=a.eid and   u3.unit=a.punit" +
                " left join DCP_UNIT u4 on u4.eid=a.eid and   u4.unit=g.wunit" +
                " left join dcp_org o1 on o1.eid=a.eid and o1.organizationno=b.organizationno "+
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNo+"' and a.porderno='"+billNo+"' ";
        List<Map<String, Object>> detailDatas = this.doQueryData(detailSql, null);

        String bDate = singleMap.get("BDATE").toString();
        String isUrgent = singleMap.get("ISURGENTORDER").toString();
        String submitTime = singleMap.get("SUBMIT_TIME").toString();
        String isMustAllot="";
        String rank="";
        String templateShopSql="select * from DCP_PTEMPLATE_SHOP where eid='"+eId+"' and PTEMPLATENO='"+pTemplateNo+"'";
        List<Map<String, Object>> templateShopDatas = this.doQueryData(templateShopSql, null);
        List<Map<String, Object>> ps = templateShopDatas.stream().filter(x -> x.get("SHOPID").toString().equals(organizationNo)).collect(Collectors.toList());
        if(CollUtil.isNotEmpty(ps)){
            isMustAllot=ps.get(0).get("ISMUSTALLOT").toString();
            rank=ps.get(0).get("SORTID").toString();
        }

        String purTemplateSql="select b.item,b.pluno,b.SUPPLIER,c.PURTYPE,c.PURCENTER,a.RECEIPT_ORG " +
                "from DCP_PTEMPLATE a " +
                "inner join DCP_PTEMPLATE_detail b on a.eid = b.eid and a.PTEMPLATENO = b.PTEMPLATENO " +
                "left join DCP_PURCHASETEMPLATE c on b.eid=c.EID and b.SUPPLIER=c.SUPPLIERNO and a.RECEIPT_ORG=c.PURCENTER " +
                "left join DCP_PURCHASETEMPLATE_goods d on c.eid=d.eid and c.PURTEMPLATENO=d.PURTEMPLATENO and d.PLUNO=b.PLUNO " +
                "where a.eid = '"+eId+"' " +
                "  and a.DOC_TYPE = '0' " +
                "  and a.SUPPLIERTYPE = 'SUPPLIER'" +
                "and b.STATUS='100' " ;
        if(Check.NotNull(pTemplateNo)){
            purTemplateSql+=" and a.PTEMPLATENO='"+pTemplateNo+"'";
        }
        purTemplateSql+=" order by item ";

        List<Map<String, Object>> purTemplateDatas = this.doQueryData(purTemplateSql, null);

        for (Map<String, Object> detailData : detailDatas) {
            String detailItem = detailData.get("ITEM").toString();
            String detailPluNo = detailData.get("PLUNO").toString();
            String detailPluBarCode = detailData.get("PLUBARCODE").toString();
            String detailFeatureNo = detailData.get("FEATURENO").toString();
            String detailPUnit = detailData.get("PUNIT").toString();
            String detailBaseUnit = detailData.get("BASEUNIT").toString();
            String detailOrgNo = detailData.get("ORGANIZATIONNO").toString();
            BigDecimal detailPQty = new BigDecimal(detailData.get("PQTY").toString());
            String detailWUnit = detailData.get("WUNIT").toString();
            String detailSupplierType = detailData.get("SUPPLIERTYPE").toString();
            String detailSupplierID = detailData.get("SUPPLIERID").toString();//统配的时候这边是组织
            BigDecimal pUnitRatio = new BigDecimal(detailData.get("PUNITRATIO").toString());
            BigDecimal wUnitRatio = new BigDecimal(detailData.get("WUNITRATIO").toString());
            int pUdLength = Integer.parseInt(detailData.get("PUDLENGTH").toString());
            int wUdLength = Integer.parseInt(detailData.get("WUDLENGTH").toString());
            String inCostWarehouse = detailData.get("IN_COST_WAREHOUSE").toString();
            String outCostWarehouse = detailData.get("OUT_COST_WAREHOUSE").toString();
            //生成需求底稿
            com.dsc.spos.utils.ColumnDataValue demandColumns=new com.dsc.spos.utils.ColumnDataValue();
            demandColumns.add("EID",eId,Types.VARCHAR);
            demandColumns.add("ORGANIZATIONNO",organizationNo,Types.VARCHAR);
            demandColumns.add("BDATE",bDate,Types.VARCHAR);
            demandColumns.add("ORDERTYPE","1",Types.VARCHAR);
            demandColumns.add("ORDERNO",billNo,Types.VARCHAR);
            demandColumns.add("ITEM",detailItem,Types.VARCHAR);
            demandColumns.add("PLUBARCODE",detailPluBarCode,Types.VARCHAR);
            demandColumns.add("PLUNO",detailPluNo,Types.VARCHAR);
            demandColumns.add("FEATURENO",detailFeatureNo,Types.VARCHAR);
            demandColumns.add("PUNIT",detailPUnit,Types.VARCHAR);
            demandColumns.add("POQTY",detailPQty,Types.VARCHAR);
            BigDecimal pQty=detailPQty;
            BigDecimal baseQty=BigDecimal.ZERO;
            BigDecimal wQty=BigDecimal.ZERO;
            baseQty=pQty.multiply(pUnitRatio).setScale(pUdLength, RoundingMode.HALF_UP);
            wQty=baseQty.divide(wUnitRatio, wUdLength, RoundingMode.HALF_UP);

            demandColumns.add("PQTY",pQty,Types.VARCHAR);
            demandColumns.add("BASEUNIT",detailBaseUnit,Types.VARCHAR);
            demandColumns.add("BASEQTY",baseQty.toString(),Types.VARCHAR);
            demandColumns.add("WUNIT",detailWUnit,Types.VARCHAR);
            demandColumns.add("WQTY",wQty.toString(),Types.VARCHAR);
            demandColumns.add("RDATE",rDate,Types.VARCHAR);
            demandColumns.add("OBJECTTYPE","1",Types.VARCHAR);
            demandColumns.add("OBJECTID",detailOrgNo,Types.VARCHAR);
            demandColumns.add("EMPLOYEEID",employeeID,Types.VARCHAR);
            demandColumns.add("DEPARTID",departID,Types.VARCHAR);
            demandColumns.add("RECEIPTWAREHOUSE",inCostWarehouse,Types.VARCHAR);
            demandColumns.add("SUPPLIERTYPE",detailSupplierType,Types.VARCHAR);
            if(detailSupplierType.equals("SUPPLIER")){
                demandColumns.add("SUPPLIER",detailSupplierID,Types.VARCHAR);
                Stream<Map<String, Object>> mapStream = purTemplateDatas.stream().filter(x -> x.get("PLUNO").toString().equals(detailPluNo) &&
                        x.get("RECEIPT_ORG").toString().equals(receipt_org) &&
                        x.get("SUPPLIER").toString().equals(detailSupplierID));
                List<Map<String, Object>> templateColl = mapStream.collect(Collectors.toList());
                if(CollUtil.isNotEmpty(templateColl)){
                    String purCenter = templateColl.get(0).get("PURCENTER").toString();
                    String purType = templateColl.get(0).get("PURTYPE").toString().toString();

                    demandColumns.add("PURCENTER",purCenter,Types.VARCHAR);
                    demandColumns.add("PURTYPE",purType,Types.VARCHAR);
                    demandColumns.add("DELIVERYORGNO",purCenter,Types.VARCHAR);

                    if("0".equals(purType)||"1".equals(purType)){
                        demandColumns.add("DELIVERYWAREHOUSE","",Types.VARCHAR);
                    }else{
                        List<Map<String, Object>> deliveryOrgS = allOrgDatas.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(purCenter)).collect(Collectors.toList());
                        if(deliveryOrgS.size()>0){
                            demandColumns.add("DELIVERYWAREHOUSE",deliveryOrgS.get(0).get("OUT_COST_WAREHOUSE").toString(),Types.VARCHAR);
                        }
                    }
                }
            }
            else if(detailSupplierType.equals("FACTORY")){
                demandColumns.add("DELIVERYORGNO",detailSupplierID,Types.VARCHAR);
                List<Map<String, Object>> deliveryOrgS = allOrgDatas.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(detailSupplierID)).collect(Collectors.toList());
                if(deliveryOrgS.size()>0){
                    demandColumns.add("DELIVERYWAREHOUSE",deliveryOrgS.get(0).get("OUT_COST_WAREHOUSE").toString(),Types.VARCHAR);
                }
            }

            demandColumns.add("STOCKOUTNOQTY","0",Types.VARCHAR);
            demandColumns.add("PURQTY","0",Types.VARCHAR);
            demandColumns.add("STOCKINQTY","0",Types.VARCHAR);
            demandColumns.add("STOCKOUTQTY","0",Types.VARCHAR);
            demandColumns.add("CLOSESTATUS","0",Types.VARCHAR);
            demandColumns.add("DISTRISTATUS","00",Types.VARCHAR);
            demandColumns.add("ISURGENT",isUrgent,Types.VARCHAR);
            demandColumns.add("SUBMITTIME",submitTime,Types.VARCHAR);
            demandColumns.add("TEMPLATENO",pTemplateNo,Types.VARCHAR);
            demandColumns.add("ISMUSTALLOT",isMustAllot,Types.VARCHAR);
            demandColumns.add("RANK",rank,Types.VARCHAR);

            String[] mainColumnNames = demandColumns.getColumns().toArray(new String[0]);
            DataValue[] mainDataValues = demandColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ib1=new InsBean("DCP_DEMAND",mainColumnNames);
            ib1.addValues(mainDataValues);
            this.addProcessData(new DataProcessBean(ib1));
        }

        String mDate =new SimpleDateFormat("yyyyMMdd").format(new Date());
        String mTime = new SimpleDateFormat("HHmmss").format(new Date());

        UptBean ub1 = null;
        ub1 = new UptBean("DCP_PORDER");

        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("PORDERNO", new DataValue(billNo, Types.VARCHAR));
        ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
        ub1.addUpdateValue("SUBMITBY", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("SUBMIT_DATE", new DataValue(mDate, Types.VARCHAR));
        ub1.addUpdateValue("SUBMIT_TIME", new DataValue(mTime, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        this.doExecuteDataToDB();

    }



    @Override
    protected List<InsBean> prepareInsertData(DCP_DistriOrderStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DistriOrderStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DistriOrderStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_DistriOrderStatusUpdateReq req) throws Exception {
        StringBuilder sb=new StringBuilder();
        sb.append("select * from DCP_DITRIORDER a where a.eid='"+req.geteId()+"' " +
                " and a.ORGANIZATIONNO='"+req.getOrganizationNO()+"' " +
                " and a.BILLNO='"+req.getRequest().getBillNo()+"' ");

        return sb.toString();
    }

    private String getDetailSql(DCP_DistriOrderStatusUpdateReq req) throws Exception {
        StringBuilder sb=new StringBuilder();

        sb.append("select * from DCP_DITRIORDER_DETAIL a where a.eid='"+req.geteId()+"' " +
                " and a.ORGANIZATIONNO='"+req.getOrganizationNO()+"' " +
                " and a.BILLNO='"+req.getRequest().getBillNo()+"' ");

        return sb.toString();
    }

    private String getExistPOrderSql(DCP_DistriOrderStatusUpdateReq req) throws Exception {
        StringBuilder sb=new StringBuilder();
        sb.append("select distinct a.PORDERNO,a.organizationno,b.status from DCP_PORDER_DETAIL a" +
                " inner join DCP_PORDER b on a.eid=b.eid and a.organizationno=b.organizationno and a.PORDERNO=b.PORDERNO" +
                " inner join DCP_DITRIORDER_DETAIL c on c.eid=a.eid and a.organizationno=c.DEMANDORGNO and b.ofno=c.BILLNO " +
                " where a.eid='"+req.geteId()+"' " +
                " and c.ORGANIZATIONNO='"+req.getOrganizationNO()+"' " +
                " and a.OFNO='"+req.getRequest().getBillNo()+"' ");

        return sb.toString();
    }



}

