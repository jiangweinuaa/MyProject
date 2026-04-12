package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dsc.spos.json.cust.req.DCP_DosageOldQueryReq;
import com.dsc.spos.json.cust.res.DCP_DosageOldQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 计算千元用量 
 * @author yuanyy 2019-08-13
 *
 */
public class DCP_DosageOldQuery extends SPosBasicService<DCP_DosageOldQueryReq, DCP_DosageOldQueryRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_DosageOldQueryReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_DosageOldQueryReq> getRequestType() {
        return new TypeToken<DCP_DosageOldQueryReq>(){};
    }
    
    @Override
    protected DCP_DosageOldQueryRes getResponseType() {
        return new DCP_DosageOldQueryRes();
    }
    
    @Override
    protected DCP_DosageOldQueryRes processJson(DCP_DosageOldQueryReq req) throws Exception {
        String sql;
        DCP_DosageOldQueryRes res = this.getResponse();
        try {
            String eId = req.geteId();
            String shopId = req.getShopId();
            String avgSaleAmt = req.getAvgSaleAmt();  // 平均营业额
            String saleAmt = req.getSaleAmt(); // 预估(参考)营业额
            String modifRatio = req.getModifRatio();
            String totSaleAmt = req.getTotSaleAmt() == null ? "0":req.getTotSaleAmt();
            String companyId = req.getBELFIRM();
            String pfWeatherRatio = req.getPfWeatherRatio();
            String pfHolidayRatio = req.getPfHolidayRatio();
            String pfMatterRatio = req.getPfMatterRatio();
            
            if(req.getPfWeatherRatio() == null){
                pfWeatherRatio = "0";
            }
            if(req.getPfHolidayRatio() == null){
                pfHolidayRatio = "0";
            }
            if(req.getPfMatterRatio() == null){
                pfMatterRatio = "0";
            }
            double ratio = Double.parseDouble(modifRatio) + (Double.parseDouble(pfWeatherRatio)
                    + Double.parseDouble(pfHolidayRatio) + Double.parseDouble(pfMatterRatio))/100;
            
            
            // 平均营业额
            double avgAmt = Double.parseDouble(avgSaleAmt);
            // 预估营业额
            double csaleAMT = Double.parseDouble(saleAmt);
            // 五周总营业额
            double totSaleAmtDou = Double.parseDouble(totSaleAmt);
            
            // 2营业额算一个比例
            double rrate;
            if (avgAmt == 0) {
                rrate = 0;
            } else {
                rrate = (csaleAMT / avgAmt) * ratio;
            }
            
            double trate = csaleAMT/1000; // 比例系数
            
            sql = this.getQuerySql2(req);
            List<Map<String , Object>> getDatas = this.doQueryData(sql, null);
            res.setDatas(new ArrayList<>());
            if(getDatas.size() > 0){
                
                //单头主键字段
                Map<String, Boolean> condition = new HashMap<>(); //查询条件
                condition.put("PLUNO", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader = MapDistinct.getMap(getDatas, condition);
                //商品取价计算

                MyCommon mc = new MyCommon();
                List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, getQHeader,companyId);
             
                
                
                
                for (Map<String, Object> map : getQHeader) {
                    DCP_DosageOldQueryRes.level1Elm lv1 = res.new level1Elm();
                    
                    String pluNo = map.get("PLUNO").toString();
                    String pluName = map.get("PLUNAME").toString();
                    String unit = map.get("UNIT").toString(); //销售单位 // 销售单位 == 要货单位？？？？
                    String unitName = map.get("UNITNAME").toString(); //销售单位
                    String amt = map.getOrDefault("AMT", "0").toString(); //总销售额
                    String qty = map.getOrDefault("QTY", "0").toString();
                   // String price = map.getOrDefault("PRICE", "0").toString(); // 零售价 ( 取自DCP_PRICE 或 DCP_PRICE_shop )
                    
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
                    String isClear = map.get("ISCLEAR").toString(); // 是否沽清 （有些为空）
                    String lastSaleTime = map.getOrDefault("LASTSALETIME", "").toString(); // 需要截取出时间
                    String dateDiff = map.getOrDefault("DATEDIFF", "1").toString(); // 用于计算千元用量
                    
                    String punitUdLength = map.getOrDefault("PUNITUDLENGTH", "2").toString();
                    double preSaleQty = Double.parseDouble(map.getOrDefault("PRESALEQTY","0").toString()); //前日销量
                    
                    //*************** 2020-04-16 营业预估成品显示的数量都是销售单位，此处不必做单位换算。*****************
//					BigDecimal unitRatio2;
//					double punitRatio = 0; // 要货单位
//					
//					List<Map<String, Object>> getQData_Ratio2 = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
//							pluNo, unit);
//					
//					if (getQData_Ratio2 == null || getQData_Ratio2.isEmpty()) {
//						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + pluNo + "  单位："+ unit +" 找不到对应的换算关系");
//					}
//
//					unitRatio2 = (BigDecimal) getQData_Ratio2.get(0).get("UNIT_RATIO");
//
//					punitRatio = unitRatio2.doubleValue(); // 要货单位
//					
//					preSaleQty = preSaleQty / punitRatio;
                    //*************** 2020-04-16 结束 *****************
                    
                    // 单品千元用量 = 单品售卖量 / 当天总业绩 * 1000
                    // 单品千元用量 = 单品售卖量 / (当天总业绩 / 1000)
                    // 单品预估量 = 预估营业额 / 1000 * ratio * 千元用量
                    if(dateDiff.equals("0")){
                        dateDiff = "1";
                    }
                    double saleQty = Double.parseDouble(qty) / Double.parseDouble(dateDiff) * rrate ;
                    
                    //2020-04-23  根据SA武小凤要求，增加 商品平均销量  和  均价
                    // 商品平均销量 = 总销量/ 天数差 ；            1：均价 = 同期五周平均营业额 / 平均销量     2：均价 = 同期五周总营业额 / 总销量
                    double avgQty = 0;
                    double avgPrice = 0;
                    double evAmt = 0;
                    
                    if(!Check.Null(amt)){
                        evAmt = Double.parseDouble(amt) ;
                    }
                    
                    if(Double.parseDouble(qty) != 0){
                        avgQty = Double.parseDouble(qty) / Double.parseDouble(dateDiff); //平均销量
//						double avgPrice = avgAmt / avgQty;  // 1：均价 = 同期五周平均营业额 / 平均销量 
//						avgPrice = totSaleAmtDou / Double.parseDouble(qty); //  2：均价 = 同期五周总营业额 / 总销量
                        avgPrice = evAmt / Double.parseDouble(qty); // 3: 均价 = 期间内商品总实际销售额 / 总销量
                    }
                    
                    //-------- 计算每个商品的千元用量 ----------
                    String kQty;
                    if(trate == 0){
                        kQty = "0";
                    }
                    else{
                        kQty = saleQty + "";
                    }
                    
                    if(StringUtils.isBlank(price)){
                        price = "0";
                    }
                    
                    String kAmt = PosPub.GetdoubleScale((Double.parseDouble(kQty) * Double.parseDouble(price) ), 3); //参考销售额
                    
                    // lastSaleTime 设置为 19:00 这种格式
                    if(lastSaleTime.length() == 14){
                        lastSaleTime = lastSaleTime.substring(8, 12);
                        StringBuilder sb = new StringBuilder(lastSaleTime);
                        sb.insert(2, ":");//在指定的位置, 插入指定的字符串
                        lastSaleTime = sb.toString();
                    }
                    
                    lv1.setPluNo(pluNo);
                    lv1.setPluName(pluName);
                    lv1.setpUnit(unit);
                    lv1.setpUnitName(unitName);
                    lv1.setPrice(Double.parseDouble(price)+ "");
                    lv1.setIsClear(isClear);
                    lv1.setLastSaleTime(lastSaleTime);
                    
                    //2019-12-19 根据鑫铭鲜的要求，菜品固定四舍五入取整
//					lv1.setkQty(String.format("%."+punitUdLength+"f", Double.parseDouble(kQty))+"");
//					lv1.setPreSaleQty(String.format("%."+punitUdLength+"f", preSaleQty));//昨日销量
                    
                    String priceStr = String.format("%.2f", avgPrice)  ;
                    String qtyStr = String.format("%.2f", avgQty)  ;
                    
                    lv1.setAvgPrice(priceStr);
                    lv1.setAvgQty(qtyStr);
                    
                    BigDecimal b1 = new BigDecimal(priceStr );
                    BigDecimal b2 = new BigDecimal(qtyStr);
                    
                    String avgAmtStr = String.format("%.2f", b1.multiply(b2).doubleValue());
                    lv1.setAvgAmt(avgAmtStr );
                    
                    lv1.setkQty(String.format("%."+punitUdLength+"f", Double.parseDouble(kQty))+"");
                    lv1.setPreSaleQty(String.format("%."+punitUdLength+"f", preSaleQty));//昨日销量
                    
                    lv1.setkAmt(String.format("%.2f", Double.parseDouble(kAmt))+"");
                    
                    lv1.setDatas(new ArrayList<>());
                    
                    res.getDatas().add(lv1);
                    
                }
                
            }
            
            
        } catch (Exception e) {
            
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败："+e.getMessage());
        }
        
        return res;
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_DosageOldQueryReq req) throws Exception {
        return null;
    }
    
    protected String getQuerySql2(DCP_DosageOldQueryReq req) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        String beginDate = req.getBeginDate();
        String endDate = req.getEndDate();
        
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) );
        Date day = calendar.getTime();
        String sysDate = format.format(day);
        
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        day = calendar.getTime();
        String preDate = format.format(day);  // 昨天（取前日库存）
        
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append( "SELECT a.PLUNO , a.pluname "//, g1.price1 as price  "
        		+ ",  a.unit AS PUNIT,a.unit , a.amt ,  a.qty , a.saleTime  as lastSaleTime, a.beginDate ,a.endDate ,   "
                + " ( to_date(a.endDate ,'yyyymmdd')-to_date(a.beginDate ,'yyyymmdd') ) + 1 AS  dateDiff , "
                + " nvl(sum(f.sale_qty), 0) AS preSaleQty , nvl( SUM(f.sale_amt) , 0 ) AS preSaleAmt ,  a.baseunit AS BASEUNIT "
//			+ " , h.state AS isClear "
                + " , nvl(u.udlength ,2 ) AS punitudlength "
                + " , nvl(ph.out_stock , 'N' )AS isClear "
                + " , uz.uname AS unitName ,NVL(AAA.UNITRATIO,1) AS UNITRATIO  "
                + " " );
        
        sqlbuf.append( "   FROM ( "
                + " SELECT EID , SHOPID  , pluNo , unit , baseunit , pluName ,   SUM(amt ) AS amt  , SUM(qty)  AS qty , "
                + " max(a.bDate) AS endDate ,   MIN(a.bDate) AS beginDate , MAX(a.sdate || a.sTime) AS saleTime  "
                + " FROM "
                + " (  SELECT a.EID , a.SHOPID , a.type  , b.pluno,  b.unit ,b.baseunit ,  e.pluName AS pluName , "
                + " CASE WHEN a.type = '0' THEN b.amt ELSE -b.amt END  AS amt , CASE WHEN a.type = '0' THEN b.qty ELSE -b.qty END AS qty   , "
                + " a.bDate ,  a.sDate , a.stime "
                + " FROM DCP_SALE a "
                + " LEFT JOIN DCP_SALE_detail b ON a.EID = b.EID AND a.saleno = b.saleNo AND a.SHOPID = b.SHOPID  "
                + " INNER JOIN ("
                + " SELECT b.EID , b.pluNo ,c.plu_name AS pluName from  dcp_goodstemplate a "
                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100' "
                + " INNER JOIN DCP_GOODS_lang c on b.eid=c.eid and b.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' "
                + " where a.eid='" +eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"') and b.CANSALE='Y' "              
//                + "  SELECT a.EID , a.pluNo , b.plu_name AS pluName "
//                + "  FROM DCP_GOODS a "
//                + " INNER JOIN DCP_GOODS_lang b  ON a.EID = b.EID AND a.pluNo = b.pluNo"
//                + " Inner Join DCP_GOODS_shop c on a.EID = c.EID and a.pluNo = c.pluNo "
//                + " WHERE a.EID = '"+eId+"'  AND a.status='100'  AND b.status='100'  and c.status='100'  "
//                + " AND b.lang_type = '"+langType+"' and c.organizationNO = '"+shopId+"' "
//                + " AND c.fsal = 'Y'  "
                + " ) e  ON b.EID = e.EID AND b.pluNo = e.pluNo   "
                + " WHERE a.EID = '"+eId+"' AND a.SHOPID = '"+shopId+"'  AND a.sDate BETWEEN '"+beginDate+"'  AND '"+endDate+"'   "
                + " and a.type != 16   and (b.packagemaster != 'Y' or b.packagemaster is null)"
                + " AND B.DISHESSTATUS != '3' AND B.DISHESSTATUS !='4'    "
                + " GROUP BY   a.EID , a.SHOPID , b.pluno ,  b.unit , b.baseunit , a.type  , b.amt , b.qty  ,  "
                + " a.bDate ,  a.sDate , a.stime , e.pluName  "
                + " ORDER BY pluNo  "
                + " )  a " );
        
        sqlbuf.append( " GROUP BY EID , SHOPID ,  pluNo  ,pluName , unit , baseunit  "
                + " ORDER BY pluNo "
                + " )  a  " );
        
        
        sqlbuf.append(" LEFT JOIN "
                + " ("
                + " select a.EID, a.SHOPID,a.bdate ,b.PLUNO,c.PLU_NAME AS PluName, b.unit , "
                + " sum(case when a.type='1' or a.type='2' or a.type='4' then -b.Qty else b.Qty end ) as sale_qty ,"
                + " sum(case when a.type='1' or a.type='2' or a.type='4' then -b.AMT else b.Amt end ) as sale_Amt  "
                + " from  DCP_SALE a "
                + " inner join DCP_SALE_detail b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.saleno=b.saleno"
                + " inner join DCP_GOODS_lang c on b.PLUNO=c.PLUNO and  b.EID=c.EID and c.lang_TYPE='"+req.getLangType()+"' "
                + " where a.EID='"+eId+"'"
                + " and a.bdate = '"+preDate+"'  "
                + " and a.type!=16 and (b.packagemaster!='Y' or b.packagemaster is null ) "
                + " group by a.EID, a.SHOPID,   a.bdate ,b.PLUNO,c.Plu_Name , b.unit "
                + " order by a.SHOPID   "
                + ") f ON a.EID = f.EID and a.SHOPID = f.SHOPID    and a.pluno = f.pluno "
//                + " LEFT JOIN  (select * "
//                + " from (  select EID, pluno, unit, price1, idx, row_number() over(partition by EID, pluno, unit order by idx) rn "
//                + " from (select EID, pluno, unit, price1, 1 as idx "
//                + " from DCP_PRICE_shop "
//                + " where EID = '"+eId+"' "
//                + " and organizationno = '"+shopId+"' "
//                + " and effdate <= '"+sysDate+"' "
//                + " and (ledate >= '"+sysDate+"' or "
//                + " ledate is null) "
//                + " and status='100' "
//                + " union "
//                + " select EID, pluno, unit, price1, 2 as idx "
//                + " from DCP_PRICE "
//                + " where EID = '"+eId+"' "
//                + " and effdate <= '"+sysDate+"' "
//                + " and (ledate >= '"+sysDate+"' or "
//                + " ledate is null) "
//                + " and status='100' )  ) "
//                + " where rn = 1) g1  "
//                + " ON a.EID = g1.EID AND a.pluNo = g1.pluNo AND g1.unit = a.unit  "
                // 2019-11-21 增加查单位保留位数
                + "   LEFT JOIN DCP_UNIT u ON a.EID = u.EID AND a.unit = u.unit "
                + "   LEFT JOIN DCP_UNIT_lang  uz on a.EID = uz.EID and a.unit = uz.unit AND uz.lang_type = '"+langType+"' "
                + "   LEFT JOIN ( "
                + " SELECT DISTINCT EID , pluNo , out_stock  FROM DCP_GOODS_SET_HISTORY "
                + " WHERE EID = '"+eId+"' AND SHOPID = '"+shopId+"'  AND out_stock = 'Y' "
                + " AND tran_time LIKE '"+preDate+"%%' "
                + " ) ph  "
                + " ON a.EID = ph.EID AND a.pluNo = ph.pluNo "
                + " left join DCP_GOODS_UNIT AAA ON a.eid=aaa.eid and a.PLUNO=AAA.PLUNO AND A.UNIT=AAA.OUNIT AND A.BASEUNIT=AAA.UNIT "
                + " GROUP BY a.pluNo , a.pluname  , "// g1.price1, "
                + "a.unit , a.amt , a.qty , a.saleTime , a.beginDate , "
                + " a.endDate , a.baseunit , u.udlength ,ph.out_stock ,uz.uname,NVL(AAA.UNITRATIO,1)  " );
        
        sqlbuf.append(" ORDER BY a.pluNO  " );
        return sqlbuf.toString();
    }
    
    
    
}
