package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_PurOrderCreateReq;
import com.dsc.spos.json.cust.req.DCP_SStockOutCreateReq;
import com.dsc.spos.json.cust.req.DCP_SStockOutCreateReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_SStockOutCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_SStockOutCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_SStockOutCreateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.DocSubmitStop;
import com.dsc.spos.redis.IRedisCacheName;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.tax.TaxAmount;
import com.dsc.spos.utils.tax.TaxAmount2;
import com.dsc.spos.utils.tax.TaxAmountCalculation;
import com.dsc.spos.utils.tax.TaxUtils;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：SStockOutCreateDCP
 *    說明：自采出库单新增
 * 服务说明：自采出库单新增
 * @author JZMA
 * @since  2018-11-20
 */
public class DCP_SStockOutCreate extends SPosAdvanceService<DCP_SStockOutCreateReq, DCP_SStockOutCreateRes>{
    
    @Override
    protected boolean isVerifyFail(DCP_SStockOutCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        
        //必传值不为空
        String status = request.getStatus();
        String supplier = request.getSupplier();
        String sStockOutID = request.getSStockOutID();
        String warehouse = request.getWarehouse();
        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt=request.getTotDistriAmt();
        String totCqty = request.getTotCqty();
        String employeeId = request.getEmployeeId();
        String departId = request.getDepartId();
        String accountDate = request.getAccountDate();
        List<level1Elm> datas = request.getDatas();
        
        if(Check.Null(status)){
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        
        if(Check.Null(supplier)){
            errMsg.append("供应商不可为空值, ");
            isFail = true;
        }
        
        if(Check.Null(sStockOutID)){
            errMsg.append("自采出库guid不可为空值, ");
            isFail = true;
        }
        
        if(Check.Null(warehouse)){
            errMsg.append("仓库不可为空值, ");
            isFail = true;
        }
        
        if (Check.Null(totPqty)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totAmt)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totDistriAmt)) {
            errMsg.append("合计进货金额可为空值, ");
            isFail = true;
        }
        if (Check.Null(totCqty)) {
            errMsg.append("合计品种数量不可为空值, ");
            isFail = true;
        }

        if(Check.Null(employeeId)){
            errMsg.append("申请人员不可为空值, ");
            isFail = true;
        }
        if(Check.Null(departId)){
            errMsg.append("申请部门不可为空值, ");
            isFail = true;
        }
        if(Check.Null(accountDate)){
            //errMsg.append("扣账日期不可为空值, ");
            //isFail = true;
        }

        if(CollUtil.isNotEmpty(datas)) {
            for (level1Elm par : datas) {
                String pluNo = par.getPluNo();
                String baseUnit = par.getBaseUnit();
                String baseQty = par.getBaseQty();
                String unitRatio = par.getUnitRatio();

                if (Check.Null(par.getItem())) {
                    errMsg.append("项次不可为空值, ");
                    isFail = true;
                }

                if (Check.Null(par.getPluNo())) {
                    errMsg.append("商品编码不可为空值, ");
                    isFail = true;
                }

                if (Check.Null(par.getPunit())) {
                    errMsg.append("商品" + pluNo + "单位不可为空值, ");
                    isFail = true;
                }

                if (Check.Null(par.getPqty())) {
                    errMsg.append("商品" + pluNo + "数量不可为空值, ");
                    isFail = true;
                }

                //if (Check.Null(par.getWarehouse())) {
                    //errMsg.append("商品" + pluNo + "仓库不可为空值, ");
                    //isFail = true;
                //}
                //if (baseUnit == null) {//后面自己算
                    //errMsg.append("商品" + pluNo + "基本单位不可为空值, ");
                    //isFail = true;
                //}
                //if (baseQty == null) {
                    //errMsg.append("商品" + pluNo + "基本数量不可为空值, ");
                    //isFail = true;
                //}
                //if (unitRatio == null) {
                    //errMsg.append("商品" + pluNo + "单位转换率不可为空值, ");
                    //isFail = true;
                //}

                if (isFail) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
            }
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }
    
    @Override
    protected void processDUID(DCP_SStockOutCreateReq req, DCP_SStockOutCreateRes res) throws Exception {
        
        //try {
            if (checkGuid(req) == false) {
                levelElm request = req.getRequest();
                String eId = req.geteId();
                String shopId = req.getShopId();
                //String createBy = req.getOpNO();
                String organizationNO = req.getOrganizationNO();
                //String employeeNo = req.getEmployeeNo();
                //String departmentNo = req.getDepartmentNo();
                String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String createTime = new SimpleDateFormat("HHmmss").format(new Date());
                String bDate = request.getBDate();
                //前端未给值时，默认为当天  BY JZMA 20200427
                if (Check.Null(bDate)) {
                    bDate = createDate;
                }

                //校验
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

                String stockOutType = request.getStockOutType();
                String supplierNo = request.getSupplier();
                String oType = request.getOType();//1.采购订单 2.采购收货 3.采购入库 4.采购收货入库 5.自采收货入6.退货申请
                String ofNo = request.getOfNo();

                //组织法人的进项税 和销项税
                req.getRequest().setTaxPayerType(req.getTaxPayerType());
                req.getRequest().setInputTaxCode(req.getInputTaxCode());
                req.getRequest().setInputTaxRate(req.getInputTaxRate());
                req.getRequest().setOutputTaxCode(req.getOutputTaxCode());
                req.getRequest().setOutputTaxRate(req.getOutputTaxRate());

                //2 小规模纳税人 进项税率为0
                if(("2").equals(req.getRequest().getTaxPayerType())){
                    if(new BigDecimal(req.getRequest().getInputTaxRate()).compareTo(BigDecimal.ZERO)!=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "当前组织所属法人为「小规模纳税人」，进项税率需设置为0%，请检查【组织信息】设置".toString());
                    }
                }


                //交易对象
                StringBuilder bizSb=new StringBuilder("" +
                        "select * from DCP_BIZPARTNER where eid='"+eId+"' and (BIZPARTNERNO='"+request.getCustomer()+"' or BIZPARTNERNO='"+request.getSupplier()+"' )");
                List<Map<String, Object>> allBizList=this.doQueryData(bizSb.toString(), null);

                if(allBizList.size()<=0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "交易对象不存在!");
                }

                StringBuilder noticeSb=new StringBuilder("select * from DCP_STOCKOUTNOTICE " +
                        " where eid='"+eId+"' and organizationno='"+organizationNO+"' and billno='"+request.getOfNo()+"'");
                List<Map<String,Object>> noticeList=this.doQueryData(noticeSb.toString(), null);

                //入库
                StringBuilder sssb = new StringBuilder("select a.*,b.bizorgno,b.BIZCORP from DCP_SSTOCKIN_DETAIL a " +
                        " inner join dcp_sstockin b on a.eid=b.eid and a.organizationno=b.organizationno and a.sstockinno=b.sstockinno " +
                        " where a.eid='" + eId + "'  and a.SSTOCKINNO='" + ofNo + "'");
                List<Map<String, Object>> sstockinDList = this.doQueryData(sssb.toString(), null);

                //大客订单
                String crSql = "select * from DCP_CUSTOMERPORDER a where a.eid='" + req.geteId() + "' and a.PORDERNO='" + req.getRequest().getOriginNo() + "'";
                List<Map<String, Object>> custlist = this.doQueryData(crSql, null);


                List<DCP_SStockOutCreateReq.level1Elm> dataList = request.getDatas();
                if(dataList==null||dataList.size()<=0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无商品明细，请检查!");
                }
                List<String> plunos = dataList.stream().map(x -> x.getPluNo()).distinct().collect(Collectors.toList());
                //totCqty=new BigDecimal(plunos.size());

                MyCommon cm=new MyCommon();
                StringBuffer sJoinPluNo=new StringBuffer("");
                for(String plu:plunos){
                    sJoinPluNo.append(plu+",");
                }
                Map<String, String> mapPlu=new HashMap<String, String>();
                mapPlu.put("PLUNO", sJoinPluNo.toString());

                String withasSql_plu="";
                withasSql_plu=cm.getFormatSourceMultiColWith(mapPlu);
                mapPlu=null;

                if (withasSql_plu.equals("")) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找品号失败!");
                }
                StringBuilder sb = new StringBuilder();

                sb.append("with p AS ( " + withasSql_plu + ") " +
                        " select * from dcp_goods a " +
                        " inner join p on p.pluno=a.pluno " +
                        "  ")
                ;
                List<Map<String, Object>> getPluData=this.doQueryData(sb.toString(), null);

                if(getPluData.size()==0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找品号失败!");
                }

                StringBuilder noticedSb=new StringBuilder("select * from DCP_STOCKOUTNOTICE_DETAIL " +
                        " where eid='"+eId+"' and organizationno='"+organizationNO+"' and billno='"+request.getOfNo()+"'");
                List<Map<String,Object>> noticeDList=this.doQueryData(noticedSb.toString(), null);

                String customerOrderSql="select * from DCP_CUSTOMERPORDER_detail a where " +
                        " a.eid='"+eId+"' and a.PORDERNO='"+request.getOfNo()+"'";
                List<Map<String,Object>> customerDList = this.doQueryData(customerOrderSql, null);

                //获取采购模板
                StringBuilder ptSb=new StringBuilder("SELECT * FROM DCP_PURCHASETEMPLATE_GOODS a" +
                        " INNER JOIN DCP_PURCHASETEMPLATE b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO where b.SUPPLIERNO='"+supplierNo+"'" );
                List<Map<String, Object>> ptList=this.doQueryData(ptSb.toString(), null);


                //结算法人payOrgNo 通知单退货会从通知单带
                if(Check.Null(request.getPayOrgNo())){
                    request.setPayOrgNo(organizationNO);
                }

                //ofno 不为空才会存reddis 后面直接删就行
                String id=req.geteId()+"_"+req.getOrganizationNO()+"_"+req.getRequest().getOfNo();
                String key_redis= IRedisCacheName.sStockOutCreateKey+id;

                if(Check.NotNull(req.getRequest().getOriginNo())) {
                    if (Check.Null(req.getRequest().getPayee())) {
                        //采购订单
                        String prSql = "select * from dcp_purorder a where a.eid='" + req.geteId() + "' and a.PURORDERNO='" + req.getRequest().getOriginNo() + "'";
                        List<Map<String, Object>> list = this.doQueryData(prSql, null);
                        if(CollUtil.isNotEmpty(list)){
                            req.getRequest().setPayee(list.get(0).get("PAYEE").toString());
                        }
                    }
                    if (Check.Null(req.getRequest().getPayer())) {
                        if(CollUtil.isNotEmpty(custlist)){
                            req.getRequest().setPayee(custlist.get(0).get("PAYER").toString());
                        }
                    }

                    //2销退  1 采退
                    if("2".equals(req.getRequest().getStockOutType())){
                        String prdSql="select * from DCP_CUSTOMERPORDER_DETAIL a " +
                                " where a.eid='"+eId+"' and a.PORDERNO='"+req.getRequest().getOriginNo()+"' ";
                        List<Map<String, Object>> list = this.doQueryData(prdSql, null);
                        if (CollUtil.isNotEmpty(list)) {
                            req.getRequest().getDatas().forEach(x->{
                                if(Check.NotNull(x.getOriginNo())&&Check.NotNull(x.getOriginItem())){
                                    List<Map<String, Object>> filterRows = list.stream().filter(y -> y.get("ITEM").toString().equals(x.getOriginItem())).collect(Collectors.toList());
                                    if(!filterRows.isEmpty()){
                                        String receivePrice = filterRows.get(0).get("RECEIVEPRICE").toString();
                                        if (Check.isNotEmpty(receivePrice)){
                                            x.setDistriPrice(receivePrice);
                                            double d = BigDecimalUtils.mul(Double.parseDouble(x.getDistriPrice()), Double.parseDouble(x.getPqty()));
                                            x.setDistriAmt(String.valueOf(d));
                                        }

                                    }
                                }

                            });
                        }
                    }
                    else{
                        String prdSql="select * from dcp_purorder_delivery a " +
                                " where a.eid='"+eId+"' and a.purorderno='"+req.getRequest().getOriginNo()+"' ";
                        List<Map<String, Object>> list = this.doQueryData(prdSql, null);
                        if (CollUtil.isNotEmpty(list)) {
                            req.getRequest().getDatas().forEach(x->{
                                if(Check.NotNull(x.getOriginNo())&&Check.NotNull(x.getOriginItem())){
                                    List<Map<String, Object>> filterRows = list.stream().filter(y -> y.get("ITEM").toString().equals(x.getOriginItem())).collect(Collectors.toList());
                                    if(filterRows.size()>0){
                                        x.setDistriPrice(filterRows.get(0).get("RECEIVEPRICE").toString());
                                        BigDecimal multiply = new BigDecimal(x.getDistriPrice()).multiply(new BigDecimal(x.getPqty()));
                                        x.setDistriAmt(multiply.toString());
                                    }
                                }

                            });
                        }
                    }


                }

                if(Check.Null(req.getRequest().getPayee())){
                    //String bizSql="select * from DCP_BIZPARTNER where eid='"+eId+"' and bizpartnerno='"+req.getRequest().getSupplier()+"'  ";
                    List<Map<String, Object>> bizList = allBizList.stream().filter(x->x.get("BIZPARTNERNO").toString().equals(req.getRequest().getSupplier())).collect(Collectors.toList());
                    if(bizList.size()>0){
                        req.getRequest().setPayee(bizList.get(0).get("PAYEE").toString());
                    }
                }
                if(Check.Null(req.getRequest().getPayer())){
                    //String bizSql="select * from DCP_BIZPARTNER where eid='"+req.geteId()+"' and bizpartnerno='"+req.getRequest().getCustomer()+"'  ";
                    List<Map<String, Object>> bizList =allBizList.stream().filter(x->x.get("BIZPARTNERNO").toString().equals(req.getRequest().getCustomer())).collect(Collectors.toList()); //this.doQueryData(bizSql, null);
                    if(bizList.size()>0){
                        req.getRequest().setPayer(bizList.get(0).get("PAYER").toString());
                    }
                }

                if(Check.Null(req.getRequest().getCorp())){
                    req.getRequest().setCorp(req.getCorp());
                }
                //bizOrgNo给值
                if(Check.Null(req.getRequest().getBizOrgNo())){
                    if("1".equals(req.getRequest().getStockOutType())){
                        if(Check.Null(req.getRequest().getOfNo())){
                            req.getRequest().setBizOrgNo(req.getOrganizationNO());
                            req.getRequest().setBizOrgCorp(req.getCorp());
                        }
                        else {
                            if ("1".equals(oType)) {
                                //bizOrgNo=「采退通知」归属组织organizationNo；
                                if(noticeList.size()>0){
                                    req.getRequest().setBizOrgNo(noticeList.get(0).get("ORGANIZATIONNO").toString());
                                    req.getRequest().setBizOrgCorp(noticeList.get(0).get("CORP").toString());
                                }
                            }
                            else if ("2".equals(oType) || "3".equals(oType) || "4".equals(oType)) {
                                if(sstockinDList.size()>0){
                                    req.getRequest().setBizOrgNo(sstockinDList.get(0).get("BIZORGNO").toString());
                                    req.getRequest().setBizOrgCorp(sstockinDList.get(0).get("BIZCORP").toString());
                                }
                            }
                        }
                    }
                    else if("2".equals(req.getRequest().getStockOutType())){
                        if(Check.Null(req.getRequest().getOfNo())){
                            req.getRequest().setBizOrgNo(req.getOrganizationNO());
                            req.getRequest().setBizOrgCorp(req.getCorp());
                        }
                        else {
                            if ("1".equals(oType)) {
                                //bizOrgNo=「采退通知」归属组织organizationNo；
                                if(noticeList.size()>0){
                                    req.getRequest().setBizOrgNo(noticeList.get(0).get("ORGANIZATIONNO").toString());
                                    req.getRequest().setBizOrgCorp(noticeList.get(0).get("CORP").toString());
                                }
                            }
                            else if ("5".equals(oType) ) {
                                if(custlist.size()>0){
                                    req.getRequest().setBizOrgNo(custlist.get(0).get("ORGANIZATIONNO").toString());
                                    req.getRequest().setBizOrgCorp(custlist.get(0).get("CORP").toString());
                                }
                            }
                        }
                    }
                }

                //refPrice给值

                //取客户价格
                StringBuffer querySql=new StringBuffer();
                querySql.append(" SELECT DISTINCT ORDERID,a.CUSTOMERNO,CUSTGROUPNO,a.PLUNO,a.UNIT,PRICE,BEGINDATE,ENDDATE,a.LASTMODITIME,ul.UNAME FROM (")
                        .append(
//                "--取出客户组中客户标签定价 " +
                                "       SELECT 3 ORDERID, d.ID as CUSTOMERNO,b.CUSTGROUPNO,a.PLUNO,a.UNIT,a.PRICE,a.BEGINDATE,a.ENDDATE,a.LASTMODITIME FROM DCP_CUSTOMER_PRICE a" +
                                        " INNER JOIN DCP_CUSTGROUP b ON a.CUSTOMERTYPE='1' and a.EID=b.EID and a.CUSTOMERNO=b.CUSTGROUPNO" +
                                        " INNER JOIN DCP_CUSTGROUP_DETAIL c on b.EID=c.EID and b.CUSTGROUPNO=c.CUSTGROUPNO and c.ATTRTYPE='1'" +
                                        " INNER JOIN DCP_TAGTYPE_DETAIL d ON d.EID=c.EID and TAGNO=c.ATTRID and d.TAGGROUPTYPE='CUST'" +
                                        " UNION all " +
//                "--取出客户组中客户定价" +
                                        " SELECT 2 ORDERID,c.ATTRID as CUSTOMERNO,b.CUSTGROUPNO,a.PLUNO,a.UNIT,a.PRICE,a.BEGINDATE,a.ENDDATE,a.LASTMODITIME FROM DCP_CUSTOMER_PRICE a" +
                                        " INNER JOIN DCP_CUSTGROUP b ON a.CUSTOMERTYPE='1' and a.EID=b.EID and a.CUSTOMERNO=b.CUSTGROUPNO" +
                                        " INNER JOIN DCP_CUSTGROUP_DETAIL c on b.EID=c.EID and b.CUSTGROUPNO=c.CUSTGROUPNO and c.ATTRTYPE='2'" +
                                        " UNION all " +
//                "--取出客户定价" +
                                        " SELECT 1 ORDERID,a.CUSTOMERNO,CAST('' as NVARCHAR2(32)) CUSTGROUPNO, a.PLUNO,a.UNIT,a.PRICE,a.BEGINDATE,a.ENDDATE,a.LASTMODITIME FROM DCP_CUSTOMER_PRICE a" +
                                        " WHERE a.CUSTOMERTYPE='2'")
                        .append(" ) a ")
                        .append(" LEFT JOIN DCP_UNIT_LANG ul on ul.UNIT=a.UNIT AND ul.LANG_TYPE='").append(req.getLangType()).append("'")
                ;

                if (StringUtils.isNotEmpty(req.getRequest().getCustomer())) {
                    querySql.append(" WHERE a.CUSTOMERNO = '").append(req.getRequest().getCustomer()).append("'");
                }
                List<Map<String, Object>> customerPriceList = this.doQueryData(querySql.toString(), null);

                request.getDatas().forEach(x->{
                    if(Check.Null(x.getRefPurPrice())){
                        if("1".equals(oType)){
                            List<Map<String, Object>> filterRows = noticeDList.stream().filter(y -> y.get("ITEM").toString().equals(x.getOItem())).collect(Collectors.toList());
                            if(filterRows.size()>0){
                                String price = filterRows.get(0).get("PRICE").toString();
                                String pUnit = filterRows.get(0).get("PUNIT").toString();
                                Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao,req.geteId(), x.getPluNo(), pUnit, x.getPunit(), price);
                                String afterDecimal = unitCalculate.get("afterDecimal").toString();
                                x.setRefPurPrice(afterDecimal);
                            }
                        }
                        else if("2".equals(oType)||"3".equals(oType)||"4".equals(oType)){
                            List<Map<String, Object>> filterRows = sstockinDList.stream().filter(y -> y.get("ITEM").toString().equals(x.getOItem())).collect(Collectors.toList());
                            if(filterRows.size()>0){
                                String purPriceBefore = filterRows.get(0).get("PURPRICE").toString();
                                String stockInPunit = filterRows.get(0).get("PUNIT").toString();
                                Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao,req.geteId(), x.getPluNo(), stockInPunit, x.getPunit(), purPriceBefore);
                                String afterDecimal = unitCalculate.get("afterDecimal").toString();
                                x.setRefPurPrice(afterDecimal);

                            }
                        }
                        else if(Check.Null(oType)){

                            //先看采购模板有没有
                            List<Map<String, Object>> ptFillter = ptList.stream().filter(pt -> pt.get("PLUNO").equals(x.getPluNo()) && pt.get("PURTEMPLATENO").equals(x.getPTemplateNo())).distinct().collect(Collectors.toList());
                            if(ptFillter.size()>0){
                                Map<String, Object> pt = ptFillter.get(0);

                                String purPriceBefore = pt.get("PURBASEPRICE").toString();
                                String stockInPunit = pt.get("PURUNIT").toString();
                                Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao,req.geteId(), x.getPluNo(), stockInPunit, x.getPunit(), purPriceBefore);
                                String afterDecimal = unitCalculate.get("afterDecimal").toString();
                                x.setRefPurPrice(afterDecimal);
                            }
                            else {
                                List<Map<String, Object>> pluFilter = getPluData.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())).collect(Collectors.toList());
                                if (pluFilter.size() > 0) {
                                    String purPriceBefore = pluFilter.get(0).get("PURPRICE").toString();
                                    String stockInPunit = pluFilter.get(0).get("PURUNIT").toString();
                                    Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao, req.geteId(), x.getPluNo(), stockInPunit, x.getPunit(), purPriceBefore);
                                    String afterDecimal = unitCalculate.get("afterDecimal").toString();
                                    x.setRefPurPrice(afterDecimal);
                                }
                            }
                        }
                    }
                    if(Check.Null(x.getRefCustPrice())){
                        if("1".equals(oType)){
                            List<Map<String, Object>> filterRows = noticeDList.stream().filter(y -> y.get("ITEM").toString().equals(x.getOItem())).collect(Collectors.toList());
                            if(filterRows.size()>0){
                                String price = filterRows.get(0).get("PRICE").toString();
                                String pUnit = filterRows.get(0).get("PUNIT").toString();
                                Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao,req.geteId(), x.getPluNo(), pUnit, x.getPunit(), price);
                                String afterDecimal = unitCalculate.get("afterDecimal").toString();
                                x.setRefCustPrice(afterDecimal);
                            }
                        }
                        else if("5".equals(oType)){
                            List<Map<String, Object>> filterRows = customerDList.stream().filter(y -> y.get("ITEM").toString().equals(x.getOItem())).collect(Collectors.toList());
                            if(filterRows.size()>0){
                                String price = filterRows.get(0).get("PRICE").toString();
                                String pUnit = filterRows.get(0).get("UNIT").toString();
                                Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao,req.geteId(), x.getPluNo(), pUnit, x.getPunit(), price);
                                String afterDecimal = unitCalculate.get("afterDecimal").toString();
                                x.setRefCustPrice(afterDecimal);
                            }
                        }
                        else if(Check.Null(oType)){
                            List<Map<String, Object>> pluFilters = customerPriceList.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())).collect(Collectors.toList());
                            if(pluFilters.size()>0){
                                String price = pluFilters.get(0).get("PRICE").toString();
                                String pUnit = pluFilters.get(0).get("UNIT").toString();
                                Map<String, Object> unitCalculate = PosPub.getUnitCalculate(dao,req.geteId(), x.getPluNo(), pUnit, x.getPunit(), price);
                                String afterDecimal = unitCalculate.get("afterDecimal").toString();
                                x.setRefCustPrice(afterDecimal);
                            }
                        }
                    }

                    if(Check.Null(x.getPurPrice())){
                        x.setPurPrice(x.getRefPurPrice());
                    }
                    if(Check.Null(x.getCustPrice())){
                        x.setCustPrice(x.getRefCustPrice());
                    }

                    if(Check.Null(x.getPurAmt()) && Check.NotNull(x.getPurPrice())){
                        BigDecimal multiply = new BigDecimal(x.getPurPrice()).multiply(new BigDecimal(x.getPqty()));
                        x.setPurAmt(multiply.toString());
                    }
                    if(Check.Null(x.getCustAmt())&&Check.NotNull(x.getCustPrice())){
                        BigDecimal multiply = new BigDecimal(x.getCustPrice()).multiply(new BigDecimal(x.getPqty()));
                        x.setCustAmt(multiply.toString());
                    }


                });

                //校验
                if("1".equals(oType)||"5".equals(oType)){
                    //添加一个缓存防止别人使用
                    if(DocSubmitStop.isStop(key_redis)){
                        //throw new SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "该通知单正在使用中，请稍后重试");
                    }
                }
                if("1".equals(oType)){

                    if(noticeList.size()>0){
                        String status = noticeList.get(0).get("STATUS").toString();
                        if(status.equals("4")){
                            DocSubmitStop.endStop(key_redis);
                            throw new SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "该通知单正在出库中，请重新选择");
                        }
                    }

                }

                //otype 1.出货通知 2.采购入库 3.采购收货入库 4.无源采购入库 5大客订单
                //1.采退出库
                if("1".equals(stockOutType)){

                    //StringBuilder bizSb=new StringBuilder("" +
                    //        "select * from DCP_BIZPARTNER where eid='"+eId+"' and BIZPARTNERNO='"+supplierNo+"'");
                    List<Map<String, Object>> bizList=allBizList.stream().filter(x->x.get("BIZPARTNERNO").toString().equals(supplierNo)).collect(Collectors.toList());
                    if(bizList.size()<=0){
                        DocSubmitStop.endStop(key_redis);
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "供应商已失效，请检查!");
                    }
                    else {
                        Map<String, Object> stringObjectMap = bizList.get(0);
                        String begindate = stringObjectMap.get("BEGINDATE").toString();
                        String enddate = stringObjectMap.get("ENDDATE").toString();
                        Date beginDatef = formatter2.parse(begindate);
                        Date endDatef = formatter2.parse(enddate);
                        Date bdatef = formatter.parse(request.getBDate());
                        if(bdatef.before(beginDatef)||bdatef.after(endDatef)){
                            DocSubmitStop.endStop(key_redis);
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "供应商已失效，请检查!");
                        }
                    }



                    //otype ofno 关联的其他采退出库
                    StringBuilder outSb=new StringBuilder("SELECT a.* from DCP_sstockout_DETAIL a" +
                            " inner join DCP_sstockout b on a.eid=b.eid and a.organizationno=b.organizationno and a.SSTOCKOUTNO=b.SSTOCKOUTNO " +
                            " where b.ofno='"+ofNo+"' and a.otype='"+oType+"' and a.eid='"+eId+"' and b.status='0' ");
                    List<Map<String,Object>> otherRList=this.doQueryData(outSb.toString(), null);


                    if("1".equals(oType)){
                        //通知单退货：本次退货量(pUnit)> 通知出货量 - 已转出货量-已登打量（排除本单录入量）-->单位一致无需转换
                        if(CollUtil.isEmpty(noticeDList)){
                            DocSubmitStop.endStop(key_redis);
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "通知单不存在!");
                        }


                        for (DCP_SStockOutCreateReq.level1Elm detail : dataList){
                            List<Map<String, Object>> noticeItem = noticeDList.stream().filter(x -> x.get("ITEM").toString().equals(detail.getOItem())).collect(Collectors.toList());
                            if(noticeItem.size()==0){
                                DocSubmitStop.endStop(key_redis);
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"找不到对应的通知单，请检查！");
                            }else{
                                Map<String, Object> notice = noticeItem.get(0);
                                String pqty = notice.get("PQTY").toString();//交易数量
                                String stockoutqty = notice.get("STOCKOUTQTY").toString();//已退货量
                                String pQtynow = detail.getPqty();//本次退货量
                                BigDecimal ddqty=new BigDecimal(0);//登打量
                                List<Map<String, Object>> others = otherRList.stream().filter(x -> x.get("OITEM").equals(detail.getOItem())).collect(Collectors.toList());
                                //收集其他通知单的退货数量
                                if(others.size()>0){
                                    for (Map<String, Object> other : others){
                                        String pqtyOther = other.get("PQTY").toString();
                                        ddqty=ddqty.add(new BigDecimal(pqtyOther));
                                    }
                                }
                                int compare = new BigDecimal(stockoutqty).add(new BigDecimal(pQtynow)).add(ddqty).compareTo(new BigDecimal(pqty));
                                if(compare>0){
                                    //超了
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"本次退货数量不能大于通知单退货数量，请检查！");
                                }
                            }
                        }


                    }

                    //采购入库234 dcp_sstockin
                    if("2".equals(oType)||"3".equals(oType)||"4".equals(oType)) {

                        for (DCP_SStockOutCreateReq.level1Elm detail : dataList) {
                            List<Map<String, Object>> pluno = getPluData.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).distinct().collect(Collectors.toList());
                            if (pluno.size() <= 0) {
                                DocSubmitStop.endStop(key_redis);
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号" + detail.getPluNo() + "不存在!");
                            }

                            BigDecimal pQty = new BigDecimal(detail.getPqty());
                            if (pQty.compareTo(BigDecimal.ZERO) <= 0) {
                                DocSubmitStop.endStop(key_redis);
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次" + detail.getItem() + "退货数量异常，请检查！");
                            }

                            //② 检查退货单价是否在允许价格范围，低于最低价格或超出最大允许价格则提示”#项次 退货价不在允许价格范围（minprice~maxprice），请检查！“，退货价格允许范围分以下情况判断：
                            //价格下调比例从采购模板获取，转换为使用退货单位对应价格范围再来判断
                            //● 入库单退货：采购基准价 *（1-下调比例）~ 源单入库单价
                            //● 无源退货：采购基准价 *（1-下调比例）~ 采购基准价

                            //有采购模板  校验下调比例  无采购模板只校验入库单的purPrice
                            List<Map<String, Object>> ptFillter = ptList.stream().filter(pt -> pt.get("PLUNO").equals(detail.getPluNo()) && pt.get("PURTEMPLATENO").equals(detail.getPTemplateNo())).distinct().collect(Collectors.toList());
                            if (ptFillter.size() == 0) {//先不限制
                                //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"找不到对应的模板，请检查！");
                            } else {
                                Map<String, Object> pt = ptFillter.get(0);
                                String purbaseprice = pt.get("PURBASEPRICE").toString();
                                String minrate = pt.get("MINRATE").toString();
                                String maxrate = pt.get("MAXRATE").toString();
                                String purunit = pt.get("PURUNIT").toString();

                                //比较价格先转换成基准单位的
                                Map<String, Object> mapBase1 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), purunit, purbaseprice);
                                if (mapBase1 == null) {
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno=" + detail.getPluNo() + ",OUNIT=" + purunit + "无记录！");
                                }
                                Map<String, Object> mapBase2 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), detail.getPunit(), detail.getPurPrice());
                                if (mapBase2 == null) {
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno=" + detail.getPluNo() + ",OUNIT=" + detail.getPunit() + "无记录！");
                                }

                                BigDecimal purbasepriceBase = new BigDecimal(mapBase1.get("baseQty").toString());//基准价格
                                BigDecimal purPriceBase = new BigDecimal(mapBase2.get("baseQty").toString());//比较价格

                                //下限比较
                                int compareLess = purbasepriceBase.multiply(new BigDecimal("1").subtract(new BigDecimal(minrate))).compareTo(purPriceBase);
                                if (compareLess > 0) {
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次" + detail.getItem() + "退货单价不能低于" + purbaseprice + "，请检查！");
                                }

                                //上限比较
                                //取原单 放在下面了


                            }


                            //找入库单  数量比较  价格比较
                            List<Map<String, Object>> psInList = sstockinDList.stream().filter(x -> x.get("ITEM").toString().equals(detail.getOItem())).collect(Collectors.toList());
                            if (psInList.size() == 0) {
                                DocSubmitStop.endStop(key_redis);
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次" + detail.getItem() + "找不到对应的通知单，请检查！");
                            }
                            else {
                                Map<String, Object> ps = psInList.get(0);
                                String punit = ps.get("PUNIT").toString();//入库单位
                                String pqty = ps.get("PQTY").toString();//入库数量

                                Map<String, Object> mapBase = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), punit, pqty);
                                if (mapBase == null) {
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno=" + detail.getPluNo() + ",OUNIT=" + punit + "无记录！");
                                }
                                BigDecimal basepQty = new BigDecimal(mapBase.get("baseQty").toString());


                                String pQtynow = detail.getPqty();//本次退货量
                                String pUnitnow = detail.getPunit();//本次退货单位
                                Map<String, Object> mapBase2 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), pUnitnow, pQtynow);
                                if (mapBase2 == null) {
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno=" + detail.getPluNo() + ",OUNIT=" + pUnitnow + "无记录！");
                                }
                                BigDecimal basepQtynow = new BigDecimal(mapBase2.get("baseQty").toString());


                                BigDecimal ddqty = new BigDecimal(0);//登打量

                                List<Map<String, Object>> others = otherRList.stream().filter(x -> x.get("OITEM").equals(detail.getOItem())).collect(Collectors.toList());
                                //收集其他通知单的退货数量
                                if (others.size() > 0) {
                                    for (Map<String, Object> other : others) {
                                        String punitOther = other.get("PUNIT").toString();
                                        String pqtyOther = other.get("PQTY").toString();
                                        Map<String, Object> mapBase3 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), punitOther, pqtyOther);
                                        if (mapBase2 == null) {
                                            DocSubmitStop.endStop(key_redis);
                                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno=" + detail.getPluNo() + ",OUNIT=" + punitOther + "无记录！");
                                        }
                                        BigDecimal basepqtyOther = new BigDecimal(mapBase3.get("baseQty").toString());
                                        ddqty = ddqty.add(basepqtyOther);
                                    }
                                }
                                int compare = basepQtynow.add(ddqty).compareTo(basepQty);

                                if (compare > 0) {
                                    //超了
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次" + detail.getItem() + "本次退货数量不能大于通知单退货数量，请检查！");
                                }


                                //比较出库单的purPrice  不能大于入库单的purPrice
                                String inPurPrice = ps.get("PURPRICE").toString();
                                if(new BigDecimal(inPurPrice).compareTo(new BigDecimal(detail.getPurPrice()))<0){
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次" + detail.getItem() + "采退单采购价格不能大于入库单采购价，请检查！");
                                }

                            }

                        }
                    }

                    //无源退货：采购基准价 *（1-下调比例）~ 采购基准价
                    if(Check.Null(oType)){
                        for (DCP_SStockOutCreateReq.level1Elm detail : dataList) {

                            //无源的 没有purprice 的  不校验
                            if(Check.Null(detail.getPurPrice())){
                                continue;
                            }
                            List<Map<String, Object>> ptFillter = ptList.stream().filter(pt -> pt.get("PLUNO").equals(detail.getPluNo()) && pt.get("PURTEMPLATENO").equals(detail.getPTemplateNo())).distinct().collect(Collectors.toList());
                            if (ptFillter.size() == 0) {//先不限制
                                DocSubmitStop.endStop(key_redis);
                                //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"找不到对应的模板，请检查！");
                            } else {
                                Map<String, Object> pt = ptFillter.get(0);
                                String purbaseprice = pt.get("PURBASEPRICE").toString();
                                String minrate = pt.get("MINRATE").toString();
                                String maxrate = pt.get("MAXRATE").toString();
                                String purunit = pt.get("PURUNIT").toString();

                                //比较价格先转换成基准单位的
                                Map<String, Object> mapBase1 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), purunit, purbaseprice);
                                if (mapBase1 == null) {
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno=" + detail.getPluNo() + ",OUNIT=" + purunit + "无记录！");
                                }
                                Map<String, Object> mapBase2 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), detail.getPunit(), detail.getPurPrice());
                                if (mapBase2 == null) {
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno=" + detail.getPluNo() + ",OUNIT=" + detail.getPunit() + "无记录！");
                                }

                                BigDecimal purbasepriceBase = new BigDecimal(mapBase1.get("baseQty").toString());//基准价格
                                BigDecimal purPriceBase = new BigDecimal(mapBase2.get("baseQty").toString());//比较价格

                                //下限比较
                                int compareLess = purbasepriceBase.multiply(new BigDecimal("1").subtract(new BigDecimal(minrate))).compareTo(purPriceBase);
                                if (compareLess > 0) {
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次" + detail.getItem() + "退货单价不能低于" + purbaseprice + "，请检查！");
                                }

                                //上限比较
                                int compareMore = purbasepriceBase.compareTo(purPriceBase);
                                if (compareMore < 0) {
                                    DocSubmitStop.endStop(key_redis);
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次" + detail.getItem() + "退货单价不能高于" + purbaseprice + "，请检查！");
                                }

                            }
                        }
                    }
                }

                if("2".equals(stockOutType)){
                    //1. 通知单号不为空时，检查通知单状态非“1-待出货”不可创建！提示“销货通知已出货，请检查！”
                    //todo  测到了再改
                    //2. 【单据日期】不在客户有效期范围内，则提示”客户已失效，请检查！“ 关联表：DCP_BIZPARTNER
                    //StringBuilder bizSb=new StringBuilder("" +
                    //        "select * from DCP_BIZPARTNER where eid='"+eId+"' and BIZPARTNERNO='"+request.getCustomer()+"'");
                    //List<Map<String, Object>> bizList=this.doQueryData(bizSb.toString(), null);
                    List<Map<String, Object>> bizList=allBizList.stream().filter(x->x.get("BIZPARTNERNO").toString().equals(request.getCustomer())).collect(Collectors.toList());
                    if(bizList.size()<=0){
                        DocSubmitStop.endStop(key_redis);
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "客户已失效，请检查!");
                    }

                    //查询出货通知单
                    //List<Map<String, Object>> noticeDList=new ArrayList<>();
                    List<Map<String, Object>> otherSStockOutDList=new ArrayList<>();
                    //List<Map<String, Object>> customerDList=new ArrayList<>();

                    if(Check.NotNull(request.getOfNo())){
                        String otherSStockOutDSql="select * from DCP_SSTOCKOUT_DETAIL a" +
                                " inner join dcp_sstockout b on a.eid=b.eid and a.organizationno=b.organizationno and a.sstockoutno=b.sstockoutno" +
                                "  where " +
                                " a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.OFNO='"+request.getOfNo()+"' " +
                                " and b.status='0'";
                        otherSStockOutDList=this.doQueryData(otherSStockOutDSql, null);


                    }

                    //3. 单身明细不可为空！判断：dataList[]不可为空
                    if(CollUtil.isEmpty(request.getDatas())){
                        DocSubmitStop.endStop(key_redis);
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "明细不能为空!");
                    }else{
                        for (DCP_SStockOutCreateReq.level1Elm detail : request.getDatas()){
                            BigDecimal pqty = new BigDecimal(detail.getPqty());
                            String item = detail.getItem();
                            if(pqty.compareTo(BigDecimal.ZERO)<=0){
                                DocSubmitStop.endStop(key_redis);
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+item+"退货数量异常，请检查!");
                            }
                            if("5".equals(request.getOType())&&Check.NotNull(request.getOfNo())){
                                List<Map<String, Object>> customerDCheckList = customerDList.stream().filter(x -> x.get("ITEM").toString().equals(detail.getOItem())).collect(Collectors.toList());
                                if(CollUtil.isNotEmpty(customerDCheckList)){
                                    BigDecimal customerPqty = new BigDecimal(customerDCheckList.get(0).get("QTY").toString());
                                    BigDecimal otherPqty=new BigDecimal(0);
                                    List<Map<String, Object>> otherSStockoutDFilter = otherSStockOutDList.stream().filter(x -> x.get("OITEM").toString().equals(detail.getOItem())).collect(Collectors.toList());
                                    if(CollUtil.isNotEmpty(otherSStockoutDFilter)){
                                        for (Map<String, Object> map : otherSStockoutDFilter){
                                            otherPqty=otherPqty.add(new BigDecimal(map.get("PQTY").toString()));
                                        }
                                    }
                                    BigDecimal checkPqty = customerPqty.subtract(otherPqty);
                                    if(new BigDecimal(detail.getPqty()).compareTo(checkPqty)>0){
                                        DocSubmitStop.endStop(key_redis);
                                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"本次退货数量不能大于订单数量，请检查！");
                                    }
                                }
                            }

                        }
                    }
                }



                String sStockOutNo =""; //this.getSStockOutNO(req);
                if("1".equals(stockOutType)){
                    sStockOutNo=this.getOrderNO(req,"CTCK");
                }
                if("2".equals(stockOutType)){
                    sStockOutNo=this.getOrderNO(req,"XHCK");
                }
                List<Map<String, Object>> getQData = new ArrayList<>();
                
                //【ID1018458】【3.0货郎】通过采购入库单的退货功能建的采购退货单的来源单号不对，导致无法上传ERP  by jinzma 20210615
                //if (!Check.Null(request.getOfNo())) {
                   // String sql = " "
                    //        + " select b.item,b.pluno from dcp_sstockin a"
                    //        + " inner join dcp_sstockin_detail b on a.eid=b.eid and a.shopid=b.shopid and a.sstockinno=b.sstockinno"
                    //        + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.sstockinno='"+request.getOfNo()+"'"
                    //        + " ";
                    //getQData = this.doQueryData(sql, null);
                    //if (getQData == null || getQData.isEmpty()) {
                       // throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "来源单号ofno:"+request.getOfNo()+"不存在!");
                    //}
                //}

                BigDecimal totDistriAmt=new BigDecimal(0);
                BigDecimal totDistriTaxAmt=new BigDecimal(0);
                BigDecimal totDistriPreTaxAmt=new BigDecimal(0);
                BigDecimal totAmt=new BigDecimal(0);
    
                //新增单身（多笔）
                List<level1Elm> datas = request.getDatas();
                String[] columnsDetail = {
                        "SSTOCKOUTNO","SHOPID","ITEM","OITEM","PLUNO",
                        "PUNIT","PQTY","BASEUNIT","BASEQTY","UNIT_RATIO",
                        "PRICE","AMT","EID","ORGANIZATIONNO","WAREHOUSE",
                        "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO","OTYPE","OFNO","CATEGORY","ISGIFT",
                        "LOCATION","EXPDATE","PTEMPLATENO","ORIGINNO","ORIGINITEM","TAXCODE","TAXRATE","INCLTAX","TAXCALTYPE","BSNO","OOFNO","OOITEM",
                        "TAXAMT","PRETAXAMT","PURPRICE","PURAMT","CUSTPRICE","CUSTAMT","REFPURPRICE","REFCUSTPRICE"
                };

                //获取currency信息
                int amountDigit=2;
                if(Check.NotNull(request.getCurrency())) {
                    String currencySql = "select * from DCP_CURRENCY where eid='" + eId + "' and" +
                            " CURRENCY='" + request.getCurrency() + "' ";
                    List<Map<String, Object>> currencies = this.doQueryData(currencySql, null);
                    if (currencies.size() <= 0) {
                        DocSubmitStop.endStop(key_redis);
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "币种:"+request.getCurrency()+"不存在!");
                    } else {
                        amountDigit = Integer.parseInt(currencies.get(0).get("AMOUNTDIGIT").toString());
                    }
                }

                InsBean ibDetail = new InsBean("DCP_SSTOCKOUT_DETAIL", columnsDetail);
                int transDetailItem=0;
                int batchItem=0;
                TaxUtils taxUtils = new TaxUtils();
                String taxSql = "select * from DCP_TAXCATEGORY a where a.eid='" + req.geteId() + "' ";
                List<Map<String, Object>> getTax = this.doQueryData(taxSql, null);

                for (level1Elm par : datas) {

                    if("1".equals(stockOutType)){

                        //看看能不呢获取到采购模板
                        if(Check.Null(par.getPTemplateNo())){
                            String pTemplateNo=taxUtils.getPurTemplateNo(eId, organizationNO, supplierNo, par.getPluNo());
                            par.setPTemplateNo(pTemplateNo);
                        }

                        if("2".equals(req.getRequest().getTaxPayerType())&&Check.Null(par.getOfNo())){
                            par.setTaxCode(req.getInputTaxCode());
                            List<Map<String, Object>> taxcodes = getTax.stream().filter(y -> y.get("TAXCODE").toString().equals(par.getTaxCode())).collect(Collectors.toList());
                            if (taxcodes.size() > 0) {
                                par.setTaxRate(taxcodes.get(0).get("TAXRATE").toString());
                                par.setTaxCalType(taxcodes.get(0).get("TAXCALTYPE").toString());
                                par.setInclTax(taxcodes.get(0).get("INCLTAX").toString());
                            }
                        }
                        else {
                            if (Check.Null(par.getOfNo())) {
                                TaxUtils.Tax tax = taxUtils.getTaxWithType(eId, organizationNO, supplierNo, par.getPTemplateNo(), par.getPluNo(), "J");
                                par.setTaxCode(tax.getTaxCode());
                                par.setTaxRate(String.valueOf(tax.getTaxRate()));
                                par.setInclTax(tax.getInclTax());
                                par.setTaxCalType(tax.getTaxCalType());
                            }
                        }
                    }
                    else{
                       // TaxUtils.Tax tax = taxUtils.getTaxWithType(eId, organizationNO, supplierNo, "", par.getPluNo(), "X");
                       // par.setTaxCode(tax.getTaxCode());
                      //  par.setTaxRate(String.valueOf(tax.getTaxRate()));
                      //  par.setInclTax(tax.getInclTax());
                      //  par.setTaxCalType(tax.getTaxCalType());
                    }

                    if(Check.Null(par.getLocation())){
                        par.setLocation(" ");
                    }
                    String featureNo = par.getFeatureNo();
                    if (Check.Null(featureNo)){
                        featureNo = " ";
                    }
                    Map<String, Object> baseMap = PosPub.getBaseQty(dao, eId, par.getPluNo(), par.getPunit(), par.getPqty());
                    String baseUnit = baseMap.get("baseUnit").toString();
                    String unitRatio = baseMap.get("unitRatio").toString();
                    String baseQty = baseMap.get("baseQty").toString();
                    if(Check.Null(par.getBaseUnit())){
                        par.setBaseUnit(baseUnit);
                    }
                    if(Check.Null(par.getUnitRatio())){
                        par.setUnitRatio(unitRatio);
                    }
                    if(Check.Null(par.getBaseQty())){
                        par.setBaseQty(baseQty);
                    }
                    if(Check.Null(par.getWarehouse())){
                        par.setWarehouse(request.getWarehouse());
                    }
                    if(Check.Null(par.getAmt())){
                        par.setAmt(PosPub.reCalculateAmt(par.getPrice(), par.getPqty()));
                    }
                    if(Check.Null(par.getDistriAmt())) {
                        par.setDistriAmt(PosPub.reCalculateAmt(par.getDistriPrice(), par.getPqty()));
                    }

                    if(Check.Null(par.getCategory())){
                        String pluSql="select * from dcp_goods a where a.eid='"+eId+"' and a.pluno='"+par.getPluNo()+"'";
                        List<Map<String, Object>> plu = this.doQueryData(pluSql, null);
                        if(plu.size()>0) {
                            par.setCategory(plu.get(0).get("CATEGORY").toString());
                        }
                    }

                    //if (!Check.Null(request.getOfNo())) {
                        //Map<String, Object> condiV = new HashMap<String, Object>();
                        //condiV.put("PLUNO", par.getPluNo());
                        //condiV.put("ITEM", par.getoItem());
                        //List<Map<String, Object>> sStockIn = MapDistinct.getWhereMap(getQData, condiV, false);
                        //if (sStockIn == null || sStockIn.isEmpty()) {
                        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编号:"+par.getPluNo()+"在原单中不存在或来源项次"+par.getoItem()+"不匹配");
                        //}
                    //}

                    BigDecimal taxRate=new BigDecimal(Check.Null(par.getTaxRate())?"0":par.getTaxRate());
                    String inclTax=par.getInclTax();
                    String taxCalType=par.getTaxCalType();//1-一般 2-运费/农产品

                    BigDecimal taxAmt=BigDecimal.ZERO;
                    BigDecimal preTaxAmt=BigDecimal.ZERO;
                    BigDecimal disTaxAmt=BigDecimal.ZERO;
                    BigDecimal preDisTaxAmt=BigDecimal.ZERO;
                    BigDecimal amt = new BigDecimal(par.getAmt());
                    BigDecimal distriAmt = new BigDecimal(par.getDistriAmt());

                    TaxAmount2 taxAmount2 = TaxAmountCalculation.calculateAmount(inclTax, amt, taxRate, taxCalType, 2);
                    taxAmt=taxAmount2.getTaxAmount();
                    preTaxAmt=taxAmount2.getPreAmount();
                    amt=taxAmount2.getAmount();

                    TaxAmount2 taxAmount3 = TaxAmountCalculation.calculateAmount(inclTax, distriAmt, taxRate, taxCalType, 2);
                    disTaxAmt=taxAmount3.getTaxAmount();
                    preDisTaxAmt=taxAmount3.getPreAmount();
                    distriAmt=taxAmount3.getAmount();

                    if(Check.Null(par.getTaxAmt())){
                        par.setTaxAmt(taxAmt.toString());
                    }
                    if(Check.Null(par.getPreTaxAmt())){
                        par.setPreTaxAmt(preTaxAmt.toString());
                    }
                    if(Check.Null(par.getDisTaxAmt())){
                        par.setDisTaxAmt(disTaxAmt.toString());
                    }
                    if(Check.Null(par.getPreDisTaxAmt())){
                        par.setPreDisTaxAmt(preDisTaxAmt.toString());
                    }

                    if(Check.Null(par.getPurPrice())){
                        par.setPurPrice("0");
                    }
                    if(Check.Null(par.getCustPrice())){
                        par.setCustPrice("0");
                    }

                    par.setPurAmt(new BigDecimal(par.getPurPrice()).multiply(new BigDecimal(par.getPqty())).toString());
                    par.setCustAmt(new BigDecimal(par.getCustPrice()).multiply(new BigDecimal(par.getPqty())).toString());



                    DataValue[] insValueDetail = new DataValue[]{
                            new DataValue(sStockOutNo, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(par.getItem(), Types.VARCHAR),
                            new DataValue(par.getOItem(), Types.VARCHAR),
                            new DataValue(par.getPluNo(), Types.VARCHAR),
                            new DataValue(par.getPunit(), Types.VARCHAR),
                            new DataValue(par.getPqty(), Types.VARCHAR),
                            new DataValue(par.getBaseUnit(), Types.VARCHAR),
                            new DataValue(par.getBaseQty(), Types.VARCHAR),
                            new DataValue(par.getUnitRatio(), Types.VARCHAR),
                            new DataValue(par.getPrice(), Types.VARCHAR),
                            new DataValue(par.getAmt(), Types.VARCHAR),
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(par.getWarehouse(), Types.VARCHAR),
                            new DataValue(par.getBatchNo(), Types.VARCHAR),
                            new DataValue(par.getProdDate(), Types.VARCHAR),
                            new DataValue(par.getDistriPrice(), Types.VARCHAR),
                            new DataValue(par.getDistriAmt(), Types.VARCHAR),
                            new DataValue(bDate, Types.VARCHAR),
                            new DataValue(featureNo, Types.VARCHAR),
                            new DataValue(par.getOType(), Types.VARCHAR),
                            new DataValue(par.getOfNo(), Types.VARCHAR),
                            new DataValue(par.getCategory(), Types.VARCHAR),
                            new DataValue(par.getIsGift(), Types.VARCHAR),
                            new DataValue(par.getLocation(), Types.VARCHAR),
                            new DataValue(par.getExpDate(), Types.VARCHAR),
                            new DataValue(par.getPTemplateNo(), Types.VARCHAR),
                            new DataValue(par.getOriginNo(), Types.VARCHAR),
                            new DataValue(par.getOriginItem(), Types.VARCHAR),
                            new DataValue(par.getTaxCode(), Types.VARCHAR),
                            new DataValue(par.getTaxRate(), Types.VARCHAR),
                            new DataValue(par.getInclTax(), Types.VARCHAR),
                            new DataValue(par.getTaxCalType(), Types.VARCHAR),
                            new DataValue(par.getBsNo(), Types.VARCHAR),
                            new DataValue(par.getOofNo(), Types.VARCHAR),
                            new DataValue(par.getOoItem(), Types.VARCHAR),
                            new DataValue("1".equals(stockOutType)?disTaxAmt:taxAmt, Types.VARCHAR),
                            new DataValue("1".equals(stockOutType)?preDisTaxAmt:preTaxAmt, Types.VARCHAR),
                            new DataValue(par.getPurPrice(), Types.VARCHAR),
                            new DataValue(par.getPurAmt(), Types.VARCHAR),
                            new DataValue(par.getCustPrice(), Types.VARCHAR),
                            new DataValue(par.getCustAmt(), Types.VARCHAR),
                            new DataValue(par.getRefPurPrice(), Types.VARCHAR),
                            new DataValue(par.getRefCustPrice(), Types.VARCHAR),
                    };
                    ibDetail.addValues(insValueDetail);

                    batchItem++;
                    ColumnDataValue batchColumns=new ColumnDataValue();
                    batchColumns.add("EID", DataValues.newString(eId));
                    batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                    batchColumns.add("SSTOCKOUTNO", DataValues.newString(sStockOutNo));
                    batchColumns.add("ITEM", DataValues.newString(batchItem));//par.getItem()
                    batchColumns.add("ITEM2", DataValues.newString(par.getItem()));
                    batchColumns.add("PLUNO", DataValues.newString(par.getPluNo()));
                    batchColumns.add("FEATURENO", DataValues.newString(featureNo));
                    batchColumns.add("WAREHOUSE", DataValues.newString(par.getWarehouse()));
                    batchColumns.add("LOCATION", DataValues.newString(par.getLocation()));
                    batchColumns.add("BATCHNO", DataValues.newString(par.getBatchNo()));
                    batchColumns.add("PRODDATE", DataValues.newString(par.getProdDate()));
                    batchColumns.add("EXPDATE", DataValues.newString(par.getExpDate()));
                    batchColumns.add("PUNIT", DataValues.newString(par.getPunit()));
                    batchColumns.add("PQTY", DataValues.newString(par.getPqty()));
                    batchColumns.add("BASEUNIT", DataValues.newString(par.getBaseUnit()));
                    batchColumns.add("BASEQTY", DataValues.newString(par.getBaseQty()));

                    String batchPrice=stockOutType.equals("1") ? par.getPurPrice():par.getCustPrice();
                    BigDecimal batchAmount = new BigDecimal(batchPrice).multiply(new BigDecimal(par.getPqty()));

                    TaxAmount taxAmount = TaxAmountCalculation.calculateAmount(
                            "Y".equals(inclTax),
                            Double.parseDouble(batchAmount.toString()),
                            Double.parseDouble(par.getTaxRate()),
                            taxCalType,
                            amountDigit
                    );


                    batchColumns.add("PRICE", DataValues.newString(batchPrice));
                    batchColumns.add("AMOUNT", DataValues.newString(batchAmount));

                    batchColumns.add("TAXCODE", DataValues.newString(par.getTaxCode()));
                    batchColumns.add("TAXRATE", DataValues.newString(par.getTaxRate()));
                    batchColumns.add("TAXAMT", DataValues.newString(taxAmount.getTaxAmount()));
                    batchColumns.add("PRETAXAMT", DataValues.newString(taxAmount.getPreAmount()));


                    String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                    DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ibBatch=new InsBean("DCP_SSTOCKOUT_BATCH",batchColumnNames);
                    ibBatch.addValues(batchDataValues);
                    this.addProcessData(new DataProcessBean(ibBatch));
                }
                this.addProcessData(new DataProcessBean(ibDetail));

                //品号特征码单位合计
                String[] columnsTrans = {
                        "EID","ORGANIZATIONNO","COMPANY","BTYPE","BILLNO",
                        "ITEM","PLUNO","FEATURENO","TAXCODE","TAXRATE",
                        "INCLTAX","PUNIT","PQTY","PRICE","AMT",
                        "PRETAXAMT","TAXAMT","TAXCALTYPE"
                };

                List<DCP_SStockOutCreateRes.pfp> pfps = datas.stream().map(x -> {
                    DCP_SStockOutCreateRes.pfp pfp = res.new pfp();
                    if(Check.Null(x.getIsGift())){
                        x.setIsGift("0");
                    }
                    pfp.setPluNo(x.getPluNo());
                    pfp.setFeatureNo(x.getFeatureNo());
                    pfp.setPUnit(x.getPunit());
                    pfp.setTaxCode(x.getTaxCode());
                    pfp.setTaxRate(x.getTaxRate());
                    pfp.setDistriPirce(x.getDistriPrice());
                    if("1".equals(stockOutType)){
                        pfp.setPrice(x.getPurPrice());
                    }
                    else if("2".equals(stockOutType)){
                        pfp.setPrice(x.getCustPrice());
                    }
                    //pfp.setDistriPirce("2".equals(stockOutType)?x.getPrice():x.getDistriPrice());
                    pfp.setTaxCalType(x.getTaxCalType());
                    pfp.setInclTax(x.getInclTax());
                    pfp.setIsGift(x.getIsGift());
                    return pfp;
                }).distinct().collect(Collectors.toList());

                InsBean ibTrans = new InsBean("DCP_TRANSACTION", columnsTrans);
                for (DCP_SStockOutCreateRes.pfp pfp : pfps){

                    List<level1Elm> pfpDetails = datas.stream().filter(x -> x.getPluNo().equals(pfp.getPluNo()) && x.getFeatureNo().equals(pfp.getFeatureNo())
                            && x.getPunit().equals(pfp.getPUnit())&&x.getIsGift().equals(pfp.getIsGift())
                    ).collect(Collectors.toList());

                    BigDecimal totPqty=new BigDecimal(0);
                    BigDecimal price=new BigDecimal(pfp.getPrice());
                    BigDecimal distriPrice=new BigDecimal(pfp.getDistriPirce());


                    BigDecimal taxAmt=BigDecimal.ZERO;
                    BigDecimal preTaxAmt=BigDecimal.ZERO;
                    BigDecimal disTaxAmt=BigDecimal.ZERO;
                    BigDecimal preDisTaxAmt=BigDecimal.ZERO;
                    String taxRate=pfp.getTaxRate();
                    String inclTax=pfp.getInclTax();
                    String taxCalType=pfp.getTaxCalType();
                    for (DCP_SStockOutCreateReq.level1Elm pfpDetail : pfpDetails){
                        totPqty=totPqty.add(new BigDecimal(pfpDetail.getPqty()));
                    }
                    BigDecimal amt=totPqty.multiply(price).setScale(amountDigit, BigDecimal.ROUND_HALF_UP);
                    BigDecimal distriAmt=totPqty.multiply(distriPrice).setScale(amountDigit, BigDecimal.ROUND_HALF_UP);

                    TaxAmount2 taxAmount2 = TaxAmountCalculation.calculateAmount(inclTax, amt, new BigDecimal(taxRate), taxCalType, 2);
                    taxAmt=taxAmount2.getTaxAmount();
                    preTaxAmt=taxAmount2.getPreAmount();
                    amt=taxAmount2.getAmount();

                    TaxAmount2 taxAmount3 = TaxAmountCalculation.calculateAmount(inclTax, distriAmt, new BigDecimal(taxRate), taxCalType, 2);
                    disTaxAmt=taxAmount3.getTaxAmount();
                    preDisTaxAmt=taxAmount3.getPreAmount();
                    distriAmt=taxAmount3.getAmount();

                    totAmt=totAmt.add(amt);
                    totDistriAmt=totDistriAmt.add(distriAmt);
                    totDistriTaxAmt=totDistriTaxAmt.add(disTaxAmt);
                    totDistriPreTaxAmt=totDistriPreTaxAmt.add(preDisTaxAmt);

                    transDetailItem++;
                    DataValue[] insValueTrans = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(organizationNO, Types.VARCHAR),
                            new DataValue(request.getPayOrgNo(), Types.VARCHAR),
                            new DataValue("2", Types.VARCHAR),//1.采购入库 2.采退出库 3.销货出库 4.销退入库
                            new DataValue(sStockOutNo, Types.VARCHAR),
                            new DataValue(String.valueOf(transDetailItem), Types.VARCHAR),
                            new DataValue(pfp.getPluNo(), Types.VARCHAR),
                            new DataValue(pfp.getFeatureNo(), Types.VARCHAR),
                            new DataValue(pfp.getTaxCode(), Types.VARCHAR),
                            new DataValue(taxRate, Types.VARCHAR),
                            new DataValue(inclTax, Types.VARCHAR),
                            new DataValue(pfp.getPUnit(), Types.VARCHAR),
                            new DataValue(totPqty, Types.VARCHAR),
                            new DataValue(price, Types.VARCHAR),
                            new DataValue(amt, Types.VARCHAR),
                            new DataValue(preTaxAmt, Types.VARCHAR),
                            new DataValue(taxAmt, Types.VARCHAR),
                            new DataValue(taxCalType, Types.VARCHAR),
                    };

                    ibTrans.addValues(insValueTrans);
                }
                this.addProcessData(new DataProcessBean(ibTrans));

                //新增图片单身(多笔)
                List<level2Elm> imageList = request.getImageList();
                if (imageList != null && !imageList.isEmpty()) {
                    int item=1;
                    String[] columnsImage = {
                            "EID", "ORGANIZATIONNO", "SHOPID", "SSTOCKOUTNO", "ITEM", "IMAGE"
                    };
                    InsBean ibImage = new InsBean("DCP_SSTOCKOUT_IMAGE", columnsImage);
                    for (level2Elm par : imageList) {
                        DataValue[] insValueImage = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(sStockOutNo, Types.VARCHAR),
                                new DataValue(item, Types.VARCHAR),
                                new DataValue(par.getImage(), Types.VARCHAR),
                        };
                        ibImage.addValues(insValueImage);
                        item++;
                    }
                    this.addProcessData(new DataProcessBean(ibImage));
                }

                String[] columns = {
                        "SHOPID","ORGANIZATIONNO","BDATE","SSTOCKOUT_ID",
                        "CREATEBY","CREATE_DATE","CREATE_TIME",
                        "TOT_PQTY","TOT_AMT","TOT_CQTY","EID",
                        "SSTOCKOUTNO","MEMO","STATUS","SUPPLIER","WAREHOUSE",
                        "OFNO","TOT_DISTRIAMT","TAXCODE","CREATE_CHATUSERID",
                        "UPDATE_TIME","TRAN_TIME","EMPLOYEEID","DEPARTID","ORDERORGNO",
                        "TOTDISTRIPRETAXAMT","TOTDISTRITAXAMT","PAYTYPE","PAYORGNO","BILLDATENO",
                        "PAYDATENO","INVOICECODE","CURRENCY","STOCKOUTTYPE","CUSTOMER","OTYPE","ORIGINNO","PAYEE","PAYER",
                        "TAXPAYER_TYPE","INPUT_TAXCODE","INPUT_TAXRATE","OUTPUT_TAXCODE","OUTPUT_TAXRATE","BIZORGNO","CORP","BIZCORP"
                };
                
                DataValue[] insValue = new DataValue[]{
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(bDate, Types.VARCHAR),
                        new DataValue(request.getSStockOutID(), Types.VARCHAR),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(createDate, Types.VARCHAR),
                        new DataValue(createTime, Types.VARCHAR),
                        new DataValue(request.getTotPqty(), Types.VARCHAR),
                        new DataValue(Check.Null(request.getTotAmt())?totAmt:request.getTotAmt(), Types.VARCHAR),
                        new DataValue(request.getTotCqty(), Types.VARCHAR),
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(sStockOutNo, Types.VARCHAR),
                        new DataValue(request.getMemo(), Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),//新建状态为0request.getStatus()
                        new DataValue(request.getSupplier(), Types.VARCHAR),
                        new DataValue(request.getWarehouse(), Types.VARCHAR),
                        new DataValue(request.getOfNo(), Types.VARCHAR),
                        new DataValue(totDistriAmt.toString(), Types.VARCHAR),
                        new DataValue(request.getTaxCode(), Types.VARCHAR),
                        new DataValue(req.getChatUserId(), Types.VARCHAR),
                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
						new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        new DataValue(request.getEmployeeId(), Types.VARCHAR),
                        new DataValue(request.getDepartId(), Types.VARCHAR),
                        new DataValue(request.getOrderOrgNo(), Types.VARCHAR),
                        new DataValue(totDistriPreTaxAmt, Types.VARCHAR),
                        new DataValue(totDistriTaxAmt, Types.VARCHAR),
                        new DataValue(request.getPayType(), Types.VARCHAR),
                        new DataValue(request.getPayOrgNo(), Types.VARCHAR),
                        new DataValue(request.getBillDateNo(), Types.VARCHAR),
                        new DataValue(request.getPayDateNo(), Types.VARCHAR),
                        new DataValue(request.getInvoiceCode(), Types.VARCHAR),
                        new DataValue(request.getCurrency(), Types.VARCHAR),
                        new DataValue(request.getStockOutType(), Types.VARCHAR),
                        new DataValue(request.getCustomer(), Types.VARCHAR),
                        new DataValue(request.getOType(), Types.VARCHAR),
                        new DataValue(request.getOriginNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getPayee(), Types.VARCHAR),
                        new DataValue(req.getRequest().getPayer(), Types.VARCHAR),

                        new DataValue(req.getRequest().getTaxPayerType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getInputTaxCode(), Types.VARCHAR),
                        new DataValue(req.getRequest().getInputTaxRate(), Types.VARCHAR),
                        new DataValue(req.getRequest().getOutputTaxCode(), Types.VARCHAR),
                        new DataValue(req.getRequest().getOutputTaxRate(), Types.VARCHAR),
                        new DataValue(req.getRequest().getBizOrgNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getCorp(), Types.VARCHAR),
                        new DataValue(req.getRequest().getBizOrgCorp(), Types.VARCHAR),
                };
                
                InsBean ib = new InsBean("DCP_SSTOCKOUT", columns);
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));

                if("1".equals(oType)&&Check.NotNull(ofNo)){
                    UptBean ub1 = new UptBean("DCP_STOCKOUTNOTICE");
                    //add Value
                    ub1.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR));

                    //condition
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("BILLNO", new DataValue(ofNo, Types.VARCHAR));
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));
                }

                this.doExecuteDataToDB();

                DocSubmitStop.endStop(key_redis);

                res.setSstockoutNo(sStockOutNo);
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

                //内部结算
                //Map<String, String> corpData = PosPub.getCorpByOrgNo(req.getOrganizationNO(), req.getRequest().ge());
                // (!StringUtils.equals(corpData.get(req.getOrganizationNO()),
                //        corpData.get(req.getRequest().get()))) {

                //}

                if(!req.getRequest().getCorp().equals(req.getRequest().getBizOrgCorp())){
                    if("1".equals(stockOutType)){
                        //采退
                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        //DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(sStockOutNo);
                        request1.setSupplyOrgNo(req.getRequest().getBizOrgNo());
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType_11002.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_SStockOutCreateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getOrganizationNO());
                            detail.setSourceBillNo(par.getOfNo());
                            detail.setSourceItem(par.getOoItem());

                            detail.setItem(String.valueOf(par.getItem()));
                            detail.setPluNo(par.getPluNo());
                            detail.setFeatureNo(par.getFeatureNo());
                            detail.setPUnit(par.getPunit());
                            detail.setPQty(String.valueOf(par.getPqty()));
                            detail.setReceivePrice(par.getDistriPrice());
                            detail.setReceiveAmt(par.getDistriAmt());
                            detail.setSupplyPrice("");
                            detail.setSupplyAmt("");
                            request1.getDetail().add(detail);
                        }
                        inReq.setRequest(request1);
                        ParseJson pj = new ParseJson();
                        String jsontemp = pj.beanToJson(inReq);

                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                        });
                        if (resserver.isSuccess() == false) {
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("内部结算失败！");
                            return;
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                        }
                    }
                    else if("2".equals(stockOutType)){
                        //销退

                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        //DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(sStockOutNo);
                        request1.setSupplyOrgNo(req.getOrganizationNO());
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType12002.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_SStockOutCreateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getRequest().getBizOrgNo());
                            detail.setSourceBillNo(par.getOfNo());
                            detail.setSourceItem(par.getOoItem());

                            detail.setItem(String.valueOf(par.getItem()));
                            detail.setPluNo(par.getPluNo());
                            detail.setFeatureNo(par.getFeatureNo());
                            detail.setPUnit(par.getPunit());
                            detail.setPQty(String.valueOf(par.getPqty()));
                            detail.setReceivePrice(par.getDistriPrice());
                            detail.setReceiveAmt(par.getDistriAmt());
                            detail.setSupplyPrice("");
                            detail.setSupplyAmt("");
                            request1.getDetail().add(detail);
                        }
                        inReq.setRequest(request1);
                        ParseJson pj = new ParseJson();
                        String jsontemp = pj.beanToJson(inReq);

                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                        });
                        if (resserver.isSuccess() == false) {
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("内部结算失败！");
                            return;
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                        }

                    }

                }


                } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
            }
            
        //} catch (Exception e) {
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        //}
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_SStockOutCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_SStockOutCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_SStockOutCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected TypeToken<DCP_SStockOutCreateReq> getRequestType() {
        return new TypeToken<DCP_SStockOutCreateReq>(){};
    }
    
    @Override
    protected DCP_SStockOutCreateRes getResponseType() {
        return new DCP_SStockOutCreateRes();
    }
    
    @Override
    protected String getQuerySql(DCP_SStockOutCreateReq req) throws Exception {
        return null;
    }
    
    private String getSStockOutNO(DCP_SStockOutCreateReq req) throws Exception  {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sql = null;
        String sStockOutNO = null;
        String shopId = req.getShopId();
        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        
        String[] conditionValues = { eId, shopId }; // 查询要货单号
        sStockOutNO = "ZCTH" + bDate;
        sqlbuf.append("" + "select sStockOutNO  from ( " + "select max(sStockOutNO) as sStockOutNO "
                + "  from DCP_SSTOCKOUT " + " where EID = ? " + " and SHOPID = ? "
                + " and sStockOutNO like '%%" + sStockOutNO + "%%' "); // 假資料
        sqlbuf.append(" ) TBL ");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
        
        if (getQData != null && getQData.isEmpty() == false) {
            sStockOutNO = (String) getQData.get(0).get("SSTOCKOUTNO");
            if (sStockOutNO != null && sStockOutNO.length() > 0) {
                long i;
                sStockOutNO = sStockOutNO.substring(4);
                i = Long.parseLong(sStockOutNO) + 1;
                sStockOutNO = i + "";
                sStockOutNO = "ZCTH" + sStockOutNO;
            }
            else {
                sStockOutNO = "ZCTH" + bDate + "00001";
            }
        }
        else {
            sStockOutNO = "ZCTH" + bDate + "00001";
        }
        
        return sStockOutNO;
    }
    
    private boolean checkGuid(DCP_SStockOutCreateReq req) throws Exception {
        levelElm request = req.getRequest();
        String guid = request.getSStockOutID();
        boolean existGuid;
        String sql = "select SSTOCKOUT_ID from DCP_SSTOCKOUT where SSTOCKOUT_ID = '"+guid+"' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        
        if (getQData != null && getQData.isEmpty() == false) {
            existGuid = true;
        } else {
            existGuid =  false;
        }
        return existGuid;
    }
    
}

