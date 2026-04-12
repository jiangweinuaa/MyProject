package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DemandToPOQueryReq;
import com.dsc.spos.json.cust.res.DCP_DemandToPOQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_DemandToPOQuery extends SPosBasicService<DCP_DemandToPOQueryReq, DCP_DemandToPOQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_DemandToPOQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (req.getRequest() == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        } else {
            //分页查询的服务，必须给值，不能为0
            int pageNumber = req.getPageNumber();
            int pageSize = req.getPageSize();
            if (pageNumber == 0) {
                isFail = true;
                errMsg.append("分页查询pageNumber不能为0,");
            }
            if (pageSize == 0) {
                isFail = true;
                errMsg.append("分页查询pageSize不能为0,");
            }

        }


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_DemandToPOQueryReq> getRequestType() {
        return new TypeToken<DCP_DemandToPOQueryReq>(){};
    }

    @Override
    protected DCP_DemandToPOQueryRes getResponseType() {
        return new DCP_DemandToPOQueryRes();
    }

    @Override
    protected DCP_DemandToPOQueryRes processJson(DCP_DemandToPOQueryReq req) throws Exception {
        DCP_DemandToPOQueryRes res = this.getResponse();
        //查询
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        res.setDatas(new ArrayList<>());

        if (CollectionUtils.isNotEmpty(getQData)) {

            StringBuffer sJoinOrderNo=new StringBuffer("");
            StringBuffer sJoinItem=new StringBuffer("");
            for (Map<String, Object> map : getQData){

                DCP_DemandToPOQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setObjectType(map.get("OBJECTTYPE").toString());
                level1Elm.setObjectId(map.get("OBJECTID").toString());
                level1Elm.setObjectName(map.get("OBJECTNAME").toString());
                level1Elm.setOrderNo(map.get("ORDERNO").toString());
                level1Elm.setOrderItem(map.get("ORDERITEM").toString());
                level1Elm.setBDate(map.get("BDATE").toString());
                level1Elm.setPluName(map.get("PLUNAME").toString());
                level1Elm.setPluBarcode(map.get("PLUBARCODE").toString());
                level1Elm.setPluNo(map.get("PLUNO").toString());
                level1Elm.setCategory(map.get("CATEGORY").toString());
                level1Elm.setCategoryName(map.get("CATEGORYNAME").toString());
                level1Elm.setFeatureNo(map.get("FEATURENO").toString());
                level1Elm.setFeatureName(map.get("FEATURENAME").toString());
                level1Elm.setSpec(map.get("SPEC").toString());
                level1Elm.setOUnit(map.get("OUNIT").toString());
                level1Elm.setOUnitName(map.get("OUNITNAME").toString());
                BigDecimal oqty = new BigDecimal(map.get("OQTY").toString());
                BigDecimal purqty = new BigDecimal(map.get("PURQTY").toString());
                BigDecimal subtract = oqty.subtract(purqty);
                level1Elm.setOQty(oqty.toString());

                level1Elm.setToPoQty(purqty.toString());
                level1Elm.setCanPoQty(subtract.toString());//可转采购量（需求单位）  后面计算
                level1Elm.setPurQty("0");//可转采购量（采购单位）  后面计算
                level1Elm.setPurUnit(map.get("PURUNIT").toString());
                level1Elm.setPurUnitName(map.get("PURUNITNAME").toString());
                level1Elm.setPurUnitUdLength(map.get("PURUNITUDLENGTH").toString());
                level1Elm.setPurUnitRoundType(map.get("PURUNITROUNDTYPE").toString());
                level1Elm.setBaseUnit(map.get("BASEUNIT").toString());
                level1Elm.setBaseUnitName(map.get("BASEUNITNAME").toString());
                level1Elm.setPUnitRatio(map.get("PUNITRATIO").toString());
                level1Elm.setOUnitRatio(map.get("OUNITRATIO").toString());
                level1Elm.setMinPurQty(map.get("MINPURQTY").toString());
                level1Elm.setMulPurQty(map.get("MULPURQTY").toString());
                level1Elm.setPurPrice(map.get("PURPRICE").toString());
                level1Elm.setTaxCode(map.get("TAXCODE").toString());
                level1Elm.setTaxName(map.get("TAXNAME").toString());
                level1Elm.setTaxRate(map.get("TAXRATE").toString());
                level1Elm.setInclTax(map.get("INCLTAX").toString());
                level1Elm.setTaxCalType(map.get("TAXCALTYPE").toString());
                level1Elm.setSupplier(map.get("SUPPLIERNO").toString());
                level1Elm.setSupplierName(map.get("SUPPLIERNAME").toString());
                level1Elm.setBeginDate(map.get("BEGINDATE").toString());
                level1Elm.setEndDate(map.get("ENDDATE").toString());
                level1Elm.setPreDays(map.get("PREDAYS").toString());
                level1Elm.setPurTemplateNo(map.get("PURTEMPLATENO").toString());
                level1Elm.setPurType(map.get("PURTYPE").toString());
                level1Elm.setPurCenter(map.get("PURCENTER").toString());
                level1Elm.setPurCenterName(map.get("PURCENTERNAME").toString());
                level1Elm.setPurCenterCorp(map.get("PURCENTERCORP").toString());
                level1Elm.setPurCenterCorpName(map.get("PURCENTERCORPNAME").toString());
                level1Elm.setPayType(map.get("PAYTYPE").toString());
                level1Elm.setPayCenter(map.get("PAYCENTER").toString());
                level1Elm.setPayCenterName(map.get("PAYCENTERNAME").toString());
                level1Elm.setBillDateNo(map.get("BILLDATENO").toString());
                level1Elm.setBillDateDesc(map.get("BILLDATEDESC").toString());
                level1Elm.setPayDateNo(map.get("PAYDATENO").toString());
                level1Elm.setPayDateDesc(map.get("PAYDATEDESC").toString());
                level1Elm.setInvoiceCode(map.get("INVOICECODE").toString());
                level1Elm.setInvoiceName(map.get("INVOICENAME").toString());
                level1Elm.setCurrency(map.get("CURRENCY").toString());
                level1Elm.setCurrencyName(map.get("CURRENCYNAME").toString());
                level1Elm.setDistriOrgNo(map.get("DISTRIORGNO").toString());
                level1Elm.setDistriOrgName(map.get("DISTRIORGNAME").toString());
                level1Elm.setDistriOrgAddress(map.get("DISTRIORGADDRESS").toString());
                level1Elm.setDistriOrgContact(map.get("DISTRIORGCONTACT").toString());
                level1Elm.setDistriOrgTelePhone(map.get("DISTRIORGTELEPHONE").toString());
                level1Elm.setReceiptOrgNo(map.get("RECEIPTORGNO").toString());
                level1Elm.setReceiptAddress(map.get("RECEIPTADDRESS").toString());
                level1Elm.setReceiptContact(map.get("RECEIPTCONTACT").toString());
                level1Elm.setReceiptTelephone(map.get("RECEIPTTELEPHONE").toString());

                level1Elm.setRDate(map.get("RDATE").toString());
                level1Elm.setEmpId(map.get("EMPID").toString());
                level1Elm.setDeptId(map.get("DEPTID").toString());
                level1Elm.setSourcType(map.get("OBJECTTYPE").toString());

                sJoinOrderNo.append(level1Elm.getOrderNo()+",");
                sJoinItem.append(level1Elm.getOrderItem()+",");
                res.getDatas().add(level1Elm);

            }

            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("ORDERNO", sJoinOrderNo.toString());
            mapOrder.put("ITEM", sJoinItem.toString());

            MyCommon cm=new MyCommon();
            String withasSql_oi=cm.getFormatSourceMultiColWith(mapOrder);

            String sourceSql=" with p as ("+withasSql_oi+")" +
                    "select b.baseunit,a.purunit,a.ounit,c.unitratio as punitratio,d.unitratio as ounitratio,e.UDLENGTH as pudlength,f.udlength as oudlength," +
                    " nvl(a.CONVERTPURQTY,0) CONVERTPURQTY,a.SOURCEBILLNO,a.OITEM  " +
                    " from DCP_PURORDER_SOURCE a " +
                    " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                    " left join DCP_GOODS_UNIT c on c.eid=a.eid and c.unit=b.baseunit and a.purunit=c.ounit " +
                    " left join dcp_goods_unit d on d.eid=a.eid and d.unit=b.baseunit and a.ounit=d.ounit " +
                    " left join dcp_unit e on e.eid=a.eid and e.unit=a.purunit " +
                    " left join dcp_unit f on f.eid=a.eid and f.unit=a.ounit " +
                    " inner join p on a.SOURCEBILLNO=p.orderno and a.oitem=p.item " +
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                    " ";
            List<Map<String, Object>> sourceData=this.doQueryData(sourceSql,null);

            for (DCP_DemandToPOQueryRes.Level1Elm level1Elm: res.getDatas()){
                BigDecimal oQty = new BigDecimal(level1Elm.getOQty());
                String orderNo = level1Elm.getOrderNo();
                String orderItem = level1Elm.getOrderItem();
                BigDecimal toPoQty = new BigDecimal(level1Elm.getToPoQty());

                List<Map<String, Object>> filterList = sourceData.stream().filter(map -> map.get("SOURCEBILLNO").toString().equals(orderNo) && map.get("OITEM").toString().equals(orderItem)).collect(Collectors.toList());
                if(filterList.size()>0){
                        BigDecimal sumQty=BigDecimal.ZERO;//需求单位数量  已采购量
                        for (Map<String, Object> map: filterList){
                            BigDecimal convertpurqty =new BigDecimal( map.get("CONVERTPURQTY").toString());
                            BigDecimal ounitratio = new BigDecimal(map.get("OUNITRATIO").toString());
                            BigDecimal punitratio = new BigDecimal(map.get("PUNITRATIO").toString());
                            String oudlength = map.get("OUDLENGTH").toString();
                            String pudlength = map.get("PUDLENGTH").toString();

                            BigDecimal oqty = convertpurqty.multiply(punitratio).divide(ounitratio, Integer.parseInt(oudlength));
                            sumQty=sumQty.add(oqty);
                        }
                        toPoQty=toPoQty.add(sumQty);
                    }

                BigDecimal canPoQty=oQty.subtract(toPoQty);
                level1Elm.setCanPoQty(canPoQty.toString());
                Integer pud = Integer.parseInt(Check.Null(level1Elm.getPurUnitUdLength())?"0":level1Elm.getPurUnitUdLength());
                BigDecimal punitRatio = new BigDecimal(Check.Null(level1Elm.getPUnitRatio())?"1":level1Elm.getPUnitRatio());
                BigDecimal ounitRatio = new BigDecimal(Check.Null(level1Elm.getOUnitRatio())?"1":level1Elm.getOUnitRatio());
                // 1-四舍五入、2-四舍六入五成双、3-无条件舍弃、4-无条件进位
                String purUnitRoundType = level1Elm.getPurUnitRoundType();
                RoundingMode roundMode = RoundingMode.HALF_UP;
                if("1".equals(purUnitRoundType)){
                    roundMode = RoundingMode.HALF_UP;
                }else if("2".equals(purUnitRoundType)){
                    roundMode=RoundingMode.HALF_EVEN;

                }else if("3".equals(purUnitRoundType)){
                    roundMode = RoundingMode.DOWN;
                }else if("4".equals(purUnitRoundType)){
                    roundMode = RoundingMode.UP;
                }
                BigDecimal canPurQty = canPoQty.multiply(ounitRatio).divide(punitRatio, pud,roundMode).setScale(pud, roundMode);
                level1Elm.setPurQty(canPurQty.toString());//折合采购量(采购单位)
            }


        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_DemandToPOQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        DCP_DemandToPOQueryReq.LevelRequest request = req.getRequest();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String eId=req.geteId();
        String shopId = req.getShopId();

        //在途量 可用库存量  后面集合处理
        String langType=req.getLangType();
        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,a.* from (");
        sqlbuf.append("select a.objectType,a.objectId,a.bdate,a.ORDERTYPE as SOURCETYPE,case when a.OBJECTTYPE='1' then b1.org_name else b.sname end as objectName,a.orderno,a.item as orderitem,a.PLUBARCODE,a.pluno,d.plu_name as pluname,c.spec,a.featureno,e.featurename,f.category,f.CATEGORY_NAME as categoryname," +
                " g.unit as ounit ,g.uname as ounitname,nvl(a.poqty,0) as oqty,j.unit as purunit,j.uname as purunitname,k.UDLENGTH as purUnitUdLength,k.ROUNDTYPE as purUnitRoundType,a.baseunit,l.uname as baseunitname ,m.UNITRATIO as pUnitRatio,n.unitratio as ounitratio," +
                " o.MINPQTY as minPurQty,o.MULPQTY as mulPurQty ,a.PURQTY,o.PURBASEPRICE as purPrice ,q.taxcode,q.taxname,p.taxrate,p.inclTax,p.TAXCALTYPE,a.supplier as SUPPLIERNO,o2.sname as suppliername,o2.begindate,o2.enddate,o.PRE_DAY as predays,o.PURTEMPLATENO,o.purtype ," +
                " o.purcenter ,r.org_name as purcentername , r1.corp as purCenterCorp,r3.org_name as purCenterCorpName,b.paytype,r2.organizationno as paycenter ,r2.org_name as payCenterName,s.paydateno,s.name as payDateDesc,t.billdateno,t.name as billdatedesc,u.INVOICECODE,u.INVOICE_NAME as invoiceName,v.currency,v.name as currencyname," +
                " w1.organizationno as distriOrgNo,w.org_name as distriOrgName,w1.address as distriOrgAddress,'' as distriOrgContact,w1.phone as distriOrgTelePhone ,b2.address as receiptAddress,b2.CONTACT as receiptContact,b2.phone as receiptTelephone,a.rdate,i2.EMPLOYEEID as empId ,i2.DEPARTID as deptid,a.OBJECTID as RECEIPTORGNO  " +
                " from DCP_DEMAND a " +
                " left join dcp_bizpartner b on a.eid=b.eid  and a.supplier=b.BIZPARTNERNO " +//and a.DELIVERYORGNO=b.organizationno and a.objecttype=b.biztype
                " left join dcp_org_lang b1 on a.OBJECTID=b1.organizationno and a.eid=b1.eid and b1.lang_type='"+req.getLangType()+"' and a.OBJECTTYPE='1' " +
                " left join dcp_org b2 on a.OBJECTID=b2.organizationno and a.eid=b2.eid  and a.OBJECTTYPE='1' " +
                " left join dcp_goods c on a.eid=b.eid and a.pluno=c.pluno " +
                " left join dcp_goods_lang d on a.eid=d.eid and a.pluno=d.pluno and d.lang_type='"+langType+"' " +
                " left join DCP_GOODS_FEATURE_LANG e on a.eid=e.eid and a.pluno=e.pluno and a.featureno=e.featureno and e.lang_type='"+langType+"' " +
                " left join DCP_CATEGORY_LANG f on f.eid=a.eid and f.category=c.category and f.lang_type='"+langType+"' " +
                " left join dcp_unit_lang g on a.eid=g.eid and g.unit=a.punit and g.lang_type='"+langType+"' " +
                " left join DCP_PORDER_DETAIL h on h.eid=a.eid  and h.porderno=a.orderno and h.item=a.item " +
                " LEFT JOIN DCP_PURORDER_SOURCE i ON i.eid=h.eid and i.organizationno=h.organizationno and i.SOURCEBILLNO=h.porderno and i.oitem=h.item " +
                " left join DCP_PURORDER i1 on i1.eid=i.eid and i1.organizationno=i.organizationno and i1.purorderno=i.purorderno "+
                " left join DCP_PORDER i2 on i2.eid=a.eid and i2.receipt_org=a.deliveryorgno and i2.porderno=a.orderno "+

                " left join dcp_org i3 on i3.eid=i1.eid and i3.organizationno=a.OBJECTID "+
                " left join dcp_unit_lang l on l.unit=a.baseunit and l.eid=a.eid and l.lang_type='"+langType+"' " +
                " left join ( select a.eid,a.supplierno,b.pluno,b.PURUNIT,c.organizationno,a.purtype,a.purcenter,b.taxcode,b.MINPQTY,b.MULPQTY,b.PURBASEPRICE,a.PRE_DAY,a.PURTEMPLATENO from DCP_PURCHASETEMPLATE a \n" +
                " inner join  DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                " inner join DCP_PURCHASETEMPLATE_ORG c on a.eid=c.eid and  a.PURTEMPLATENO=c.PURTEMPLATENO " +
                " where a.eid='"+req.geteId()+"'" +
                " ) o on a.supplier=o.supplierno and o.pluno=a.pluno and o.organizationno=a.ORGANIZATIONNO "+
                " left join dcp_unit_lang j on j.eid=o.eid and j.unit=o.PURUNIT and j.lang_type='"+langType+"'" +
                " left join dcp_unit k on k.eid=j.eid and k.unit=j.unit  " +
                " left join DCP_GOODS_UNIT m on m.eid=a.eid and m.pluno=a.pluno and m.unit=a.baseunit and m.ounit=o.purunit "+
                " left join DCP_GOODS_UNIT n on n.eid=a.eid and n.pluno=a.pluno and n.unit=a.baseunit and n.ounit=a.punit " +

                " left join DCP_TAXCATEGORY p on p.eid=o.eid and p.taxcode=o.taxcode " +
                " left join DCP_TAXCATEGORY_LANG q on q.eid=p.eid and q.taxcode=p.taxcode and q.lang_type='"+langType+"' " +
                " left join dcp_bizpartner o2 on a.eid=o2.eid  and a.supplier=o2.BIZPARTNERNO " +
                " left join dcp_org_lang  r on r.eid=o.eid and r.organizationno=o.purcenter and r.lang_type='"+langType+"'" +
                " left join dcp_org r1 on r1.eid=r.eid and r1.organizationno=r.organizationno  " +
                " left join dcp_org_lang  r2 on o2.eid=r2.eid and r2.organizationno=o2.paycenter and r2.lang_type='"+langType+"'" +
                " left join dcp_org_lang  r3 on r1.eid=r3.eid and r3.organizationno=r1.corp and r3.lang_type='"+langType+"'" +

                " left join DCP_PAYDATE_LANG s on s.eid=o2.eid and s.paydateno=o2.paydateno and s.lang_type='"+langType+"' " +
                " left join DCP_billDATE_LANG t on t.eid=o2.eid and t.billdateno=o2.billdateno and t.lang_type='"+langType+"' " +
                " left join DCP_INVOICETYPE_LANG u on u.eid=o2.eid and u.INVOICECODE=o2.INVOICECODE and u.lang_type='"+langType+"'" +
                " left join DCP_CURRENCY_LANG v on v.eid=o2.eid and v.currency=o2.MAINCURRENCY and v.lang_type='"+langType+"'" +
                " left join dcp_org_lang w on w.eid=a.eid and w.organizationno=a.DELIVERYORGNO and w.lang_type='"+langType+"'" +
                " left join dcp_org w1 on w1.eid=w.eid and w1.organizationno=a.DELIVERYORGNO  " +

                "");

        if("Y".equals(request.getIsCheckRestrictGroup())){
            List<String> bizNos = getBizNos(req);
            if(bizNos.size()>0){
                StringBuffer sJoinNo=new StringBuffer("");
                for (String bizNo: bizNos)
                {
                    sJoinNo.append(bizNo+",");
                }
                Map<String, String> mapOrder=new HashMap<String, String>();
                mapOrder.put("BIZPARTNERNO", sJoinNo.toString());

                MyCommon cm=new MyCommon();
                String withasSql_bizno=cm.getFormatSourceMultiColWith(mapOrder);

                sqlbuf.append(" inner join ("+withasSql_bizno+") biz on biz.BIZPARTNERNO=b.BIZPARTNERNO ");
            }

        }

        sqlbuf.append( " where a.eid='"+eId+"'   " );

        sqlbuf.append(" and a.DISTRISTATUS='00' ");
        if(!Check.Null(request.getOrderType())){
            sqlbuf.append(" and a.ordertype='"+request.getOrderType()+"' ");
        }

        //supplierType="FACTORY", 关联组织条件：DELIVERYORGNO=当前登陆组织；
        //supplierType="SUPPLIER"&purType="0", 关联组织条件：ORGANIZATIONNO=当前登陆组织；
        //supplierType="SUPPLIER"&purType in ("1","2"), 关联组织条件：PURCENTER=当前登陆组织；

        sqlbuf.append(" and (( a.DELIVERYORGNO='"+ req.getOrganizationNO()+"' and a.SUPPLIERTYPE='FACTORY' )" +
                " OR (" +
                "  a.ORGANIZATIONNO='"+ req.getOrganizationNO()+"' and a.SUPPLIERTYPE='SUPPLIER' AND A.PURTYPE='0' " +
                " ) " +
                " OR (" +
                "  a.PURCENTER='"+ req.getOrganizationNO()+"' and a.SUPPLIERTYPE='SUPPLIER' AND A.PURTYPE in ('1','2') " +
                " ) " +
                "  ) ");


        if("bDate".equals(request.getDateType())){
            if(!Check.Null(request.getBeginDate())){
                sqlbuf.append(" and a.bdate>='"+request.getBeginDate()+"' ");
            }
            if(Check.Null(request.getEndDate())){
                sqlbuf.append(" and a.bdate<='"+request.getBeginDate()+"' ");
            }
        }
        if("rDate".equals(request.getDateType())){
            if(!Check.Null(request.getBeginDate())){
                sqlbuf.append(" and a.rdate>='"+request.getBeginDate()+"' ");
            }
            if(Check.Null(request.getEndDate())){
                sqlbuf.append(" and a.rdate<='"+request.getBeginDate()+"' ");
            }
        }

        if(!Check.Null(request.getPurType())){
            sqlbuf.append(" and a.PURTYPE='"+request.getPurType()+"' ");
        }

        if(!Check.Null(request.getPurCenter())){
            sqlbuf.append(" and a.PURCENTER='"+request.getPurCenter()+"' ");
        }

        if(!Check.Null(request.getOrderNo())){
            sqlbuf.append(" and a.orderno='"+request.getOrderNo()+"' ");
        }

        if(!Check.Null(request.getCloseStatus())){
            sqlbuf.append(" and a.CLOSESTATUS='"+request.getCloseStatus()+"' ");
        }

        String[] objectList = request.getObjectList();
        String objects = getString(objectList);
        if (!Check.Null(objects)){
            sqlbuf.append(" and a.OBJECTID in ("+objects+")");
        }




        sqlbuf.append("  ) a  ") ;
        sqlbuf.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY purTemplateNo ");


        return sqlbuf.toString();

    }

    private String getString(String[] str) {
        StringBuffer str2 = new StringBuffer();
        if (str!=null && str.length>0) {
            for (String s:str) {
                str2.append("'").append(s).append("'").append(",");
            }
            if (str2.length()>0) {
                str2 = new StringBuffer(str2.substring(0, str2.length() - 1));
            }
        }
        return str2.toString();
    }

    private List getBizNos(DCP_DemandToPOQueryReq req) throws Exception {

        // //1、根据传参【检查控制组】、【控制组类型】过滤用户在当前据点可使用交易对象范围
        //            //【检查控制组】=Y，【控制组类型】='1-采购'，则
        //            //① 查询用户是否存在有效的采购控制组，不存在代表不设限，直接返回有效交易对象数据（用员工编号以及所属部门条件分别检索符合条件的控制组，允许有多个控制组，多个控制组取并集）
        //            //
        //            //② 检查用户所在控制组范围关联【适用组织】数据，判断：
        //            //- 若【适用组织】为空，则代表该控制组对所有组织据点不设限，控制组编号保留；
        //            //- 若【适用组织】不为空，判断传入组织据点是否存在控制组【限定组织】范围内，不存在则代表当前组织据点不在控制组可用组织范围内，排除该控制组；存在则保留该控制组，继续第③步判断
        //            //
        //            //③ 用户所在控制组不为空，则查询关联【限定交易对象】，多组数据去重合并后返回可用交易对象数据集。用户所在控制组为空，则返回空；
        //            //
        //            //若最后返回数据集为空，则提示用户在当前组织据点下无可操作的交易对象；
        String employeeNo=req.getEmployeeNo();
        String departmentNo=req.getDepartmentNo();
        String eid=req.geteId();
        String organizationNO = req.getOrganizationNO();
        String sql="select a.GROUPNO from DCP_RESTRICTGROUP_EMP a " +
                " inner join DCP_RESTRICTGROUP b on a.eid=b.eid and a.groupno=b.groupno and a.grouptype=b.grouptype " +
                " where a.eid='"+eid+"' and a.EMPLOYEENO='"+employeeNo+"' and a.status='100' and b.status='100' and a.grouptype='1' ";
        sql+=" union all ";
        sql+="select a.GROUPNO from DCP_RESTRICTGROUP_DEPT a " +
                " inner join DCP_RESTRICTGROUP b on a.eid=b.eid and a.groupno=b.groupno and a.grouptype=b.grouptype " +
                " where a.eid='"+eid+"' and a.DEPARTMENTNO='"+departmentNo+"' and b.status='100' and a.status='100' and a.grouptype='1'  ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        List<String> groupno = list.stream().map(var -> var.get("GROUPNO").toString()).collect(Collectors.toList());
        List<String> validGroupNos=new ArrayList();
        if(organizationNO==null||organizationNO.equals("")){
            validGroupNos=groupno;
        }else{
            sql="select a.GROUPNO from DCP_RESTRICTGROUP_ORG a where a.eid='"+eid+"' and a.ORGANIZATIONNO='"+organizationNO+"' and a.status='100' and a.grouptype='1' ";
            List<Map<String, Object>> list2 = this.doQueryData(sql, null);
            List<String> groupno2 = list2.stream()
                    .map(var -> var.get("GROUPNO").toString())
                    .collect(Collectors.toList());

            validGroupNos=groupno.stream()
                    .filter(groupNo -> groupno2.contains(groupNo))
                    .collect(Collectors.toList());
        }

        if(validGroupNos!=null&&validGroupNos.size()>0){
            String sqlNos="";
            for(String validNo:validGroupNos){
                sqlNos+="'"+validNo+"',";
            }
            sqlNos=sqlNos.substring(0,sqlNos.length()-1);
            sql="select a.BIZPARTNERNO from DCP_RESTRICTGROUP_BIZPARTNER a where a.eid='"+eid+"' and a.status='100' and a.grouptype='1' and a.groupno in ("+sqlNos+") ";
            List<Map<String, Object>> list3 = this.doQueryData(sql, null);
            List<String> bizpartnerno = list3.stream().map(var -> var.get("BIZPARTNERNO").toString()).collect(Collectors.toList());
            return bizpartnerno;
        }

        return new ArrayList();
    }



}
