package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ShopGoodsUnitQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopGoodsUnitQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DCP_ShopGoodsUnitQuery
 * 服务说明：【门店用】商品单位查询
 * @author jinzma
 * @since 2020-08-17
 */
public class DCP_ShopGoodsUnitQuery extends SPosBasicService<DCP_ShopGoodsUnitQueryReq,DCP_ShopGoodsUnitQueryRes>{
    
    @Override
    protected boolean isVerifyFail(DCP_ShopGoodsUnitQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String pluNo = req.getRequest().getPluNo();
        
        if(Check.Null(pluNo)){
            errMsg.append("商品编码不可为空值, ");
            isFail = true;
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ShopGoodsUnitQueryReq> getRequestType() {
        return new TypeToken<DCP_ShopGoodsUnitQueryReq>(){};
    }
    
    @Override
    protected DCP_ShopGoodsUnitQueryRes getResponseType() {
        return new DCP_ShopGoodsUnitQueryRes();
    }
    
    @Override
    protected DCP_ShopGoodsUnitQueryRes processJson(DCP_ShopGoodsUnitQueryReq req) throws Exception {
        //try {
            DCP_ShopGoodsUnitQueryRes res = this.getResponse();
            String eId=req.geteId();
            String shopId=req.getShopId();
            String companyId = req.getBELFIRM();
            String supplier = req.getRequest().getSupplier();
            //单位类型：SUNIT-销售单位；PUNIT-要货单位；PURUNIT-采购单位；BOMUNIT-配方单位；PRODUNIT-生产单位 ;CUNIT-盘点单位
            String unitType = req.getRequest().getUnitType();

            String priceUdLength= PosPub.getPARA_SMS(dao, req.geteId(), "", "PRICEROUND");
            if(Check.Null(priceUdLength)){
                priceUdLength="0";
            }

            //查询资料
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            int totalRecords;								//总笔数
            int totalPages;									//总页数
            res.setDatas(new ArrayList<>());
            if (getQData != null && !getQData.isEmpty()) {
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                
                if (Check.Null(companyId)) {
                    sql=" select belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
                    List<Map<String, Object>> getQCompanyId = this.doQueryData(sql, null);
                    companyId = getQCompanyId.get(0).get("BELFIRM").toString();
                }
                //商品取价计算
                MyCommon mc = new MyCommon();
                List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, getQData,  companyId);

                List<Map<String, Object>> getPurTemplate=new ArrayList<>();
                if("PURUNIT".equals(unitType)){
                    if(Check.NotNull(req.getRequest().getSupplier())&&Check.NotNull(req.getRequest().getTemplateNo())){
                        String purTemplateSql="select a.*,c.pluno " +
                                " from DCP_PURCHASETEMPLATE_PRICE a " +
                                " left join DCP_PURCHASETEMPLATE b on a.eid=b.eid and a.purtemplateno=b.purtemplateno " +
                                " left join DCP_PURCHASETEMPLATE_GOODS c on c.eid=a.eid and c.purtemplateno=a.purtemplateno and c.item=a.item" +
                                " where a.eid='"+req.geteId()+"' and b.purtemplateno='"+req.getRequest().getTemplateNo()+"' " +
                                " and b.SUPPLIERNO='"+req.getRequest().getSupplier()+"' ";
                         getPurTemplate = this.doQueryData(purTemplateSql, null);
                    }
                }

                for (Map<String, Object> oneData:getQData) {
                    String pluNo = oneData.get("PLUNO").toString();
                    String unit = oneData.get("PUNIT").toString();
                    String unitName = oneData.get("PUNITNAME").toString();
                    String udLength = oneData.get("UDLENGTH").toString();
                    String purUdLength = oneData.get("PURUDLENGTH").toString();
                    String baseUnit = oneData.get("BASEUNIT").toString();
                    String baseUnitName = oneData.get("BASEUNITNAME").toString();
                    String unitRatio = oneData.get("UNITRATIO").toString();

                    //pt.purunit,pt.PURBASEPRICE ,pt.MINRATE,pt.MAXRATE,pt.MULPQTY,pt.MINPQTY,gu1.UNITRATIO as pur_unitratio
                    String purbaseprice = oneData.get("PURBASEPRICE").toString();
                    String purunit = oneData.get("PURUNIT").toString();
                    String minrate = oneData.get("MINRATE").toString();
                    String maxrate = oneData.get("MAXRATE").toString();
                    String mulpqty = oneData.get("MULPQTY").toString();
                    String minpqty = oneData.get("MINPQTY").toString();
                    String pur_unitratio = oneData.get("PUR_UNITRATIO").toString();//采购单位和基准单位的换算率

                    if(Check.Null(pur_unitratio)||"0".equals(pur_unitratio)){
                        pur_unitratio="1";
                    }

                    //pur_unitratio  采购->基准单位 1
                    //unitRatio  单位->基准单位  6
                    //先算兑换比例
                    //BigDecimal unitRatioPrice = new BigDecimal(pur_unitratio).compareTo(BigDecimal.ZERO)==0?BigDecimal.ZERO:new BigDecimal(unitRatio).divide(new BigDecimal(pur_unitratio), 4, BigDecimal.ROUND_HALF_UP);

                    //BigDecimal unitRatio1 = new BigDecimal(pur_unitratio).compareTo(BigDecimal.ZERO)==0?BigDecimal.ZERO:new BigDecimal(unitRatio).divide(new BigDecimal(pur_unitratio), Integer.parseInt(udLength), BigDecimal.ROUND_HALF_UP);

                    BigDecimal purPrice = BigDecimal.ZERO;
                    BigDecimal minrateNew =BigDecimal.ZERO;
                    BigDecimal maxrateNew = BigDecimal.ZERO;
                    BigDecimal mulpqtyNew = BigDecimal.ZERO;
                    BigDecimal minpqtyNew = BigDecimal.ZERO;
                    if(new BigDecimal(pur_unitratio).compareTo(BigDecimal.ZERO)!=0){
                         purPrice = new BigDecimal(purbaseprice).multiply(new BigDecimal(unitRatio)).divide(new BigDecimal(pur_unitratio),Integer.parseInt(priceUdLength), BigDecimal.ROUND_HALF_UP);
                         minrateNew = new BigDecimal(minrate).multiply(new BigDecimal(pur_unitratio)).divide(new BigDecimal(unitRatio),Integer.parseInt(priceUdLength), BigDecimal.ROUND_HALF_UP);
                         maxrateNew = new BigDecimal(maxrate).multiply(new BigDecimal(pur_unitratio)).divide(new BigDecimal(unitRatio),Integer.parseInt(priceUdLength), BigDecimal.ROUND_HALF_UP);
                         mulpqtyNew = new BigDecimal(mulpqty).multiply(new BigDecimal(pur_unitratio)).divide(new BigDecimal(unitRatio),Integer.parseInt(purUdLength), BigDecimal.ROUND_HALF_UP);
                         minpqtyNew = new BigDecimal(minpqty).multiply(new BigDecimal(pur_unitratio)).divide(new BigDecimal(unitRatio),Integer.parseInt(purUdLength), BigDecimal.ROUND_HALF_UP);

                    }

                    //商品取价
                    Map<String, Object> condiV= new HashMap<>();
                    condiV.put("PLUNO",pluNo);
                    condiV.put("PUNIT",unit);

                    List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPrice, condiV, false);
                    condiV.clear();
                    String price="0";
                    String distriPrice="0";
                    if(priceList!=null && priceList.size()>0 ) {
                        price=priceList.get(0).get("PRICE").toString();
                        distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                    }
                    
                    //【ID1018410】【3.0货郎】门店自采获取外部供应商采购价-单位查询服务 by jinzma 20210617
                    if (!Check.Null(supplier) && !Check.Null(unitType) && unitType.equals("PURUNIT")){
                        String suptemplatePluno = oneData.get("SUPTEMPLATE_PLUNO").toString();
                        String suptemplatePrice = oneData.get("SUPTEMPLATE_PRICE").toString();
                        distriPrice="0";
                        if (!Check.Null(suptemplatePluno)) {
                            distriPrice = suptemplatePrice;
                        }
                    }
                    
                    DCP_ShopGoodsUnitQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    oneLv1.setUnit(unit);
                    oneLv1.setUnitName(unitName);
                    oneLv1.setBaseUnit(baseUnit);
                    oneLv1.setBaseUnitName(baseUnitName);
                    oneLv1.setUdLength(udLength);
                    oneLv1.setUnitRatio(unitRatio);
                    oneLv1.setPrice(price);
                    oneLv1.setDistriPrice(distriPrice);
                    oneLv1.setBaseUnitUdLength(oneData.get("BASEUNITUDLENGTH").toString());
                    oneLv1.setPurPrice(purPrice.toString());
                    oneLv1.setMinRate(minrateNew.toString());
                    oneLv1.setMaxRate(maxrateNew.toString());
                    oneLv1.setMulQty(mulpqtyNew.toString());
                    oneLv1.setMinQty(minpqtyNew.toString());
                    oneLv1.setCustPrice("0");
                    oneLv1.setCustCategoryDiscRate("");

                    oneLv1.setPurPriceList(new ArrayList<>());
                    if("PURUNIT".equals(unitType)) {
                        if (Check.NotNull(req.getRequest().getSupplier()) && Check.NotNull(req.getRequest().getTemplateNo())) {
                            List<Map<String, Object>> purPriceList = getPurTemplate.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                            if (CollUtil.isNotEmpty(purPriceList)) {
                                for (Map<String, Object> onePurPrice : purPriceList) {
                                    BigDecimal bQty = new BigDecimal(onePurPrice.get("BQTY").toString());
                                    BigDecimal eQty = new BigDecimal(onePurPrice.get("EQTY").toString());
                                    BigDecimal singlePurPrice = new BigDecimal(onePurPrice.get("PURPRICE").toString());
                                    DCP_ShopGoodsUnitQueryRes.PurPriceList onePurPriceElm = res.new PurPriceList();

                                    BigDecimal singlePurPriceNew = singlePurPrice.multiply(new BigDecimal(unitRatio)).divide(new BigDecimal(pur_unitratio),Integer.parseInt(priceUdLength), BigDecimal.ROUND_HALF_UP);
                                    BigDecimal bQtyNew= bQty.multiply(new BigDecimal(pur_unitratio)).divide(new BigDecimal(unitRatio),Integer.parseInt(purUdLength), BigDecimal.ROUND_HALF_UP);
                                    BigDecimal eQtyNew= eQty.multiply(new BigDecimal(pur_unitratio)).divide(new BigDecimal(unitRatio),Integer.parseInt(purUdLength), BigDecimal.ROUND_HALF_UP);

                                    onePurPriceElm.setBeginQty(bQtyNew.toString());
                                    onePurPriceElm.setEndQty(eQtyNew.toString());
                                    onePurPriceElm.setPurPrice(singlePurPriceNew.toString());
                                    oneLv1.getPurPriceList().add(onePurPriceElm);

                                }
                            }
                        }
                     }

                    res.getDatas().add(oneLv1);
                }

                if(Check.NotNull(req.getRequest().getCustomer())){
                    //取商品所处分类
                    String goodsSql="select * from dcp_goods a where a.eid='"+eId+"' " +
                            " and a.pluno='"+req.getRequest().getPluNo()+"'";
                    List<Map<String, Object>> goodList = this.doQueryData(goodsSql, null);
                    if(CollUtil.isNotEmpty(goodList)){
                        String category = goodList.get(0).get("CATEGORY").toString();

                        String cRateSql="" +
                                "SELECT DISTINCT EID, CUSTOMERNO, CATEGORYID, DISCRATE " +
                                "FROM (SELECT a.EID,d.ID as CUSTOMERNO, CATEGORYID, DISCRATE " +
                                "      FROM DCP_CUSTOMER_CATE_DISC a " +
                                "               INNER JOIN DCP_CUSTGROUP b " +
                                "                          ON a.CUSTOMERTYPE = '1' and a.EID = b.EID and a.CUSTOMERNO = b.CUSTGROUPNO " +
                                "               INNER JOIN DCP_CUSTGROUP_DETAIL c " +
                                "                          on b.EID = c.EID and b.CUSTGROUPNO = c.CUSTGROUPNO and c.ATTRTYPE = '1' " +
                                "               INNER JOIN DCP_TAGTYPE_DETAIL d " +
                                "                          ON d.EID = c.EID and TAGNO = c.ATTRID and d.TAGGROUPTYPE = 'CUST' " +
                                "      UNION all " +
                                "      SELECT a.EID, c.ATTRID as CUSTOMERNO, CATEGORYID, DISCRATE " +
                                "      FROM DCP_CUSTOMER_CATE_DISC a " +
                                "               INNER JOIN DCP_CUSTGROUP b " +
                                "                          ON a.CUSTOMERTYPE = '1' and a.EID = b.EID and a.CUSTOMERNO = b.CUSTGROUPNO " +
                                "               INNER JOIN DCP_CUSTGROUP_DETAIL c " +
                                "                          on b.EID = c.EID and b.CUSTGROUPNO = c.CUSTGROUPNO and c.ATTRTYPE = '2' " +
                                "      UNION all " +
                                "      SELECT a.EID, CUSTOMERNO, CATEGORYID, DISCRATE " +
                                "      FROM DCP_CUSTOMER_CATE_DISC a " +
                                "      WHERE a.CUSTOMERTYPE = '2') a " +
                                "WHERE  CATEGORYID='"+category+"' and eid='"+eId+"' and CUSTOMERNO='"+req.getRequest().getCustomer()+"'" +
                                " ";

                        List<Map<String, Object>> rateList = this.doQueryData(cRateSql, null);
                        String custCategoryDiscRate="";
                        if(CollUtil.isNotEmpty(rateList)){
                            custCategoryDiscRate=rateList.get(0).get("DISCRATE").toString();
                        }
                        String nowDate =  new SimpleDateFormat("yyyyMMdd").format(new Date());

                        String priceSql="select a.unit,a.price from DCP_CUSTOMER_PRICE a " +
                                " where a.eid='"+eId+"' and a.pluno='"+req.getRequest().getPluNo()+"' " +
                                " and a.customerno='"+req.getRequest().getCustomer()+"' " +
                                " and a.begindate<='"+nowDate+"' " +
                                " and (a.enddate>='"+nowDate+"' or a.enddate is null)" +
                                " order by a.item desc ";
                        List<Map<String, Object>> priceList = this.doQueryData(priceSql, null);
                        String finalCustCategoryDiscRate = custCategoryDiscRate;
                        res.getDatas().forEach(x->{
                            String unit = x.getUnit();
                            List<Map<String, Object>> unitFilterRows = priceList.stream().filter(y -> y.get("UNIT").toString().equals(unit)).collect(Collectors.toList());
                            if(unitFilterRows.size()>0)
                            {
                                x.setCustPrice(unitFilterRows.get(0).get("PRICE").toString());
                            }
                            x.setCustCategoryDiscRate(finalCustCategoryDiscRate);
                        });


                    }
                }

            }else {
                totalRecords = 0;
                totalPages = 0;
            }
            
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            
            return res;
            
       // } catch (Exception e) {
         //   throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
       // }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_ShopGoodsUnitQueryReq req) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        String pluNo = req.getRequest().getPluNo();
        String supplier = req.getRequest().getSupplier();
        String templateNo = req.getRequest().getTemplateNo();
        String weekOfDay = this.getWeekDay();
        String day = this.getDay();
        String doubleDay = "1";    //单日
        if(Integer.parseInt(day) % 2==0) {
            doubleDay = "2";//双日
        }
        //单位类型：SUNIT-销售单位；PUNIT-要货单位；PURUNIT-采购单位；BOMUNIT-配方单位；PRODUNIT-生产单位 ;CUNIT-盘点单位
        String unitType = req.getRequest().getUnitType();
        if (!Check.Null(unitType)){
            switch (unitType) {
                case "SUNIT":
                    unitType="sunit_use";
                    break;
                case "PUNIT":
                    unitType="punit_use";
                    break;
                case "PURUNIT":
                    unitType="purunit_use";
                    break;
                case "BOMUNIT":
                    unitType="bom_unit_use";
                    break;
                case "PRODUNIT":
                    unitType="prod_unit_use";
                    break;
                case "CUNIT":
                    unitType="cunit_use";
                    break;
                case "RUNIT":
                    unitType="runit_use";
                    break;
                default:
                    unitType = "";
                    break;
            }
        }
        
        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;

        String purtemplateNo="";
        if(Check.NotNull(supplier)){
            purtemplateNo=templateNo;
            templateNo="";
        }

        
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                + " with supTemplate as ("
                + " select b.pluno as supTemplate_pluno,b.price as supTemplate_price,b.punit as supTemplate_punit "
                + " from ("
                + " select a.*,row_number() over (order by a.tran_time desc) as rn from dcp_ptemplate a"
                + " left join dcp_ptemplate_shop b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type and b.shopid='"+shopId+"' "
                + " where a.eid='"+eId+"' and a.doc_type='3' and a.status='100' and a.supplier='"+supplier+"' "
                + " ");
        if (!Check.Null(templateNo)){
            sqlbuf.append(" and a.ptemplateno='"+templateNo+"' ");
        }else {
            sqlbuf.append(""
                    + " and (a.shoptype='1' or (a.shoptype='2' and b.shopid is not null ))"
                    + " and ((a.time_type='0' ) or (a.time_type='1' and a.time_value like '%" + doubleDay + "%')"
                    + " or (a.time_type='2' and a.time_value like '%" + weekOfDay + "%')"
                    + " or (a.time_type='3' and ';'||a.time_value||';' like '%%;" + Integer.valueOf(day) + ";%%')"
                    + " or (a.time_type='3' and a.time_value like '%%" + day + "%%'))"
                    + " ");
        }



        sqlbuf.append(""
                + " )a"
                + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type"
                + " where a.rn='1' and b.pluno='"+pluNo+"'"
                + " )"
                + " ");

        StringBuffer purTemplateSqlSb=new StringBuffer();
        purTemplateSqlSb.append(" (" +
                " select distinct b.PURTEMPLATENO,a.supplierno,b.pluno,b.purunit,b.PURBASEPRICE ,b.MINRATE,b.MAXRATE,b.MULPQTY,b.MINPQTY  from DCP_PURCHASETEMPLATE a" +
                " left join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                " left join DCP_PURCHASETEMPLATE_ORG c on a.eid=c.eid and a.PURTEMPLATENO=c.PURTEMPLATENO " +
                " where a.eid='"+req.geteId()+"' and b.status='100' and a.status='100' and c.status='100' and a.supplierno='"+supplier+"' ");

        if(Check.Null(purtemplateNo)) {
            if (!Check.Null(req.getRequest().getOrgNo())) {
                purTemplateSqlSb.append(" and c.ORGANIZATIONNO='" + req.getRequest().getOrgNo() + "' ");
            } else {
                purTemplateSqlSb.append(" and c.ORGANIZATIONNO='" + req.getOrganizationNO() + "' ");

            }
        }
        if("PURUNIT".equals(unitType)){
            if(!Check.Null(req.getRequest().getTemplateNo())){
                purTemplateSqlSb.append(" and a.PURTEMPLATENO='"+req.getRequest().getTemplateNo()+"' ");
            }
        }
        purTemplateSqlSb.append(") ");
        
        sqlbuf.append(""
                + " select * from ("
                + " select count(*) over() num,row_number() over (order by a.ounit) rn,"
                + " a.pluno,a.ounit as punit,a.unit as baseUnit,a.unitratio,pul.uname as punitname,bul.uname as baseUnitName,u.udlength,nvl(u1.udlength,0) as purudlength,"
                + " supTemplate.*,buludlength.udlength as baseunitudlength ,pt.purunit,nvl(pt.PURBASEPRICE,0) PURBASEPRICE,nvl(pt.MINRATE,0) minrate,nvl(pt.MAXRATE,0) maxrate,nvl(pt.MULPQTY,0) as MULPQTY,nvl(pt.MINPQTY,0) as minpqty,nvl(gu1.UNITRATIO,0) as pur_unitratio,'"+req.getRequest().getSupplierId()+"' as supplierid  "
                + " from dcp_goods_unit a"
                + " inner join dcp_unit u on u.eid=a.eid and u.unit=a.ounit and u.status='100'"
                + " left  join dcp_unit_lang pul on pul.eid=a.eid and pul.unit=a.ounit and pul.lang_type='"+langType+"'"
                + " left  join dcp_unit_lang bul on bul.eid=a.eid and bul.unit=a.unit and bul.lang_type='"+langType+"'"
                + " inner join dcp_goods dg on dg.eid=a.eid and dg.pluno=a.pluno and dg.baseunit=a.unit and dg.status='100' "
                + " left  join supTemplate on a.pluno=supTemplate.suptemplate_pluno and a.ounit=supTemplate.suptemplate_punit"
                + " left  join dcp_unit buludlength on a.eid=buludlength.eid and a.unit = buludlength.unit"

                + " ");
        sqlbuf.append(" left join "+purTemplateSqlSb+"  pt on pt.pluno=a.pluno " +
                " left join dcp_goods_unit gu1 on gu1.ounit=pt.purunit and gu1.unit=a.unit and a.eid=gu1.eid and gu1.pluno=a.pluno  " +
                " left join dcp_unit u1 on u1.eid=a.eid and u1.unit=pt.purunit and u.status='100'" +
                "                 ");

        sqlbuf.append(" where a.eid='"+eId+"' and a.pluno='"+pluNo+"' ");
        
        if (!Check.Null(unitType)){
            //【ID1027055】【安吉双门石3.0】商品单位“片”禁止用于要货，要货模板里面还是可以选择"片"为要货单位 by jinzma 20220704 以下注释
            //sqlbuf.append(" and (a."+unitType+"='Y' or a.ounit=dg.baseunit)");
            sqlbuf.append(" and a."+unitType+"='Y' ");
            
        }
        sqlbuf.append( " ) where rn>"+startRow+" and rn<="+(startRow+pageSize) );
        return sqlbuf.toString();
    }
    
    protected String getWeekDay() throws Exception{
        String weekOfDay="";
        String sql = "select to_char(sysdate,'D') as week from dual";
        List<Map<String, Object>> getWeekDay = this.doQueryData(sql, null);
        if (getWeekDay != null && !getWeekDay.isEmpty())
        {
            Map<String, Object> strWeekDay = getWeekDay.get(0);
            String weekDay = strWeekDay.get("WEEK").toString();
            switch (weekDay) {
                case "1":
                    weekOfDay="周日";
                    break;
                case "2":
                    weekOfDay="周一";
                    break;
                case "3":
                    weekOfDay="周二";
                    break;
                case "4":
                    weekOfDay="周三";
                    break;
                case "5":
                    weekOfDay="周四";
                    break;
                case "6":
                    weekOfDay="周五";
                    break;
                case "7":
                    weekOfDay="周六";
                    break;
                default:
                    break;
            }
        }
        return weekOfDay;
    }
    
    protected String getDay() throws Exception{
        String day="";
        String sql = "select to_char(sysdate,'dd') as day from dual";
        List<Map<String, Object>> getDay = this.doQueryData(sql, null);
        if (getDay != null && !getDay.isEmpty())
        {
            day=getDay.get(0).get("DAY").toString();
        }
        return day;
    }
    
    
    
}
