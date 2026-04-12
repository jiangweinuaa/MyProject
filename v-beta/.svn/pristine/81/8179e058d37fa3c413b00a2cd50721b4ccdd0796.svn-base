package com.dsc.spos.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.ResultDatas;
import com.dsc.spos.model.ApiUser;
import com.dsc.spos.scheduler.job.InsertWSLOG;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//公用
public class MyCommon
{
    Logger logger = LogManager.getLogger(MyCommon.class.getName());
    public MyCommon()
    {
    
    }
    
    /**
     * SELECT REGEXP_SUBSTR('1,2,3,','[^,]+',1,rownum) pluno,REGEXP_SUBSTR('ab,aa,acc,','[^,]+',1,rownum) punit
     from dual connect by rownum<=LENGTH ('1,2,3,') - LENGTH (regexp_replace('1,2,3,',',',''))
     union all
     SELECT REGEXP_SUBSTR('1,2,3,','[^,]+',1,rownum) pluno,REGEXP_SUBSTR('ab,aa,acc,','[^,]+',1,rownum) punit
     from dual connect by rownum<=LENGTH ('1,2,3,') - LENGTH (regexp_replace('1,2,3,',',',''))
     * 多with临时表更高效(单with是采用loop循环关联表慢)，支持多列(ORA-01704: 字符串文字太长，使用union all 每100个商品 分段处理)
     * @param map
     * key=PLUNO value=1001,1002,
     * key=PUNIT value=kg,个,
     * @return
     * @throws Exception
     */
    public String getFormatSourceMultiColWith(Map<String, String> map) throws Exception
    {
        StringBuffer sqlJoinPluno=new StringBuffer("");
        
        int iCount=0;
        List<String> lstStr=new ArrayList<String>();
        for (Map.Entry<String, String> entry1 : map.entrySet())
        {
            iCount+=1;
            
            //
            StringBuffer tempStr=new StringBuffer("");
            String[] strSplit=entry1.getValue().split(",",-1);
            
            for (int i = 0; i < strSplit.length-1; i++)
            {
                //取余,每次100个商品
                int iMod=(i+1) % 100;
                
                if (strSplit[i].equals(""))
                {
                    tempStr.append(" " + ",");
                }
                else
                {
                    tempStr.append(strSplit[i] + ",");
                }
                
                if (iMod==0)
                {
                    lstStr.add("'" +tempStr.toString()+"','[^,]+',1,rownum) " + entry1.getKey() + ",");
                    tempStr.setLength(0);
                }
            }
            
            if (tempStr.toString().equals("")==false)
            {
                lstStr.add("'" +tempStr.toString()+"','[^,]+',1,rownum) " + entry1.getKey() + ",");
            }
            
        }
        
        //
        int iMultiple=lstStr.size()/iCount;
        for (int i = 0; i < iMultiple; i++)
        {
            StringBuffer sp=new StringBuffer(" SELECT ");
            String spluno="";
            for (int j = 0; j < iCount; j++) {
                sp.append( "REGEXP_SUBSTR(" +lstStr.get(j*iMultiple+i));
                
                int ipos=lstStr.get(0).indexOf(",'[^,]+'");
                
                spluno=lstStr.get(0).substring(0,ipos);
            }
            
            sp.deleteCharAt(sp.length()-1);//去除右边逗号,
            sp.append(" , rownum rn FROM DUAL connect by rownum<=LENGTH (" + spluno +") - LENGTH (regexp_replace(" +spluno + ",',',''))" +" union all");
            sqlJoinPluno.append(sp.toString());
        }
        
        if (sqlJoinPluno.length()>0)
        {
            sqlJoinPluno.delete(sqlJoinPluno.length()-9,sqlJoinPluno.length());//union all
        }
        
        return sqlJoinPluno.toString();
    }
    
    
    /**
     * 获取零售价和进货价（供应商为空时只返回零售价）
     * @param dao
     * @param eId
     * @param companyId
     * @param shopId 收货组织
     * @param pluList 服务查出来的商品明细，必须包含此4个字段(PLUNO,PUNIT,BASEUNIT,UNITRATIO)
     * @param supplierId 供应商或公司编号  供货组织
     * @return 返回的记录：pluno、punit、price(对应punit)、distriprice(对应punit)
     */
    public List<Map<String, Object>> getSalePrice_distriPrice(DsmDAO dao,String eId,String companyId,String shopId,List<Map<String, Object>> pluList,String supplierId){
        List<Map<String, Object>> getPrice=new ArrayList<Map<String, Object>>();
        StringBuffer sqlbuf = new StringBuffer("");
        try
        {
            // 避免SQL执行异常，为空就直接返回  by jinzma 20210527
            if (pluList==null || pluList.isEmpty()){
                return getPrice ;
            }

            //供货法人  收货法人 收货公司
            String orgSql="select * from dcp_org a where a.eid='"+eId+"' ";
            List<Map<String, Object>> orgList=dao.executeQuerySQL(orgSql,null);
            //String gCorp="";
            String sCorp="";
            String sCompany="";
            //List<Map<String, Object>> gOrgs = orgList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(supplierId)).collect(Collectors.toList());
            //if (gOrgs.size()>0){
                //gCorp=gOrgs.get(0).get("CORP").toString();
            //}

            List<Map<String, Object>> sOrgs = orgList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(shopId)).collect(Collectors.toList());
            if (sOrgs.size()>0){
                sCorp=sOrgs.get(0).get("CORP").toString();
                sCompany=sOrgs.get(0).get("BELFIRM").toString();
            }


            ///处理单价和金额小数位数  BY JZMA 20200401
            String priceLength = PosPub.getPARA_SMS(StaticInfo.dao,eId,shopId,"priceLength");
            if (Check.Null(priceLength)||!PosPub.isNumeric(priceLength)){
                priceLength="2";
            }
            int priceLengthInt = Integer.valueOf(priceLength);
            
            String distriPriceLength = PosPub.getPARA_SMS(StaticInfo.dao,eId,shopId,"distriPriceLength");
            if (Check.Null(distriPriceLength)||!PosPub.isNumeric(distriPriceLength)){
                distriPriceLength="2";
            }
            int distriPriceLengthInt = Integer.valueOf(distriPriceLength);
            
            //成品
            StringBuffer sJoinPluno=new StringBuffer("");
            StringBuffer sJoinPunit=new StringBuffer("");
            for (Map<String, Object> mapAll : pluList){
                sJoinPluno.append(mapAll.get("PLUNO").toString()+",");
                sJoinPunit.append(mapAll.get("PUNIT").toString()+",");
            }
            Map<String, String> map=new HashMap<String, String>();
            map.put("PLUNO", sJoinPluno.toString());
            map.put("PUNIT", sJoinPunit.toString());
            
            String withasSql_Pluno= getFormatSourceMultiColWith(map);
            map.clear();
            map=null;
            
            //---商品列表
            sqlbuf.append("with plu as ("+withasSql_Pluno+")");
            
            //【ID1033148】 SQL效率优化 dcp_suppricetemplate_range by jinzma 20230627
            //---零售价模板ID表
            sqlbuf.append(" ,salepriceid as ("
                    + " select a.templateid from dcp_salepricetemplate a"
                    + " inner join dcp_salepricetemplate_range b on a.eid=b.eid and a.templateid=b.templateid and b.id='"+companyId+"'"
                    + " where a.eid='"+eId+"' and a.status='100' and a.templatetype='COMPANY'"
                    + " union all"
                    + " select a.templateid from dcp_salepricetemplate a"
                    + " inner join dcp_salepricetemplate_range b on a.eid=b.eid and a.templateid=b.templateid and b.id='"+shopId+"'"
                    + " where a.eid='"+eId+"' and a.status='100' and a.templatetype='SHOP'"
                    + " )");
            
            //【ID1033148】 SQL效率优化 dcp_suppricetemplate_range by jinzma 20230627
            if (!Check.Null(supplierId)) {
                //供货组织每个商品可能不一样

                //获取供货价模板id
                //1、供货条件：供货组织匹配优先序分别：1️⃣供货法人 > 2️⃣全部法人（需新增供货机构类型）@YYY🐨
                //2、需方条件：收货组织匹配优先序分别：1️⃣收货组织 > 2️⃣组织所属法人(公司)>3️⃣所有组织



                //---供货价模板ID表
                sqlbuf.append(" ,suppriceid as ("
                        + " select a.templateid,1 as sort from dcp_suppricetemplate a"
                        + " inner join dcp_suppricetemplate_range b on a.eid=b.eid and a.templateid=b.templateid and b.id='"+shopId+"'"
                        + " where a.eid='"+eId+"' and a.status='100' "
                        + "   and a.receivertype='2' and a.receiveridrange ='1' "//and a.supplierid='"+gCorp+"'
                        + " union all"
                        + " select a.templateid,2 as sort from dcp_suppricetemplate a"
                        + " inner join dcp_suppricetemplate_range b on a.eid=b.eid and a.templateid=b.templateid and b.id='"+sCorp+"'"
                        + " where a.eid='"+eId+"' and a.status='100' "
                        + "   and a.receivertype='1'  "
                        + " union all"
                        + " select a.templateid,3 as sort from dcp_suppricetemplate a "
                        + " where a.eid='"+eId+"' and a.status='100' "
                        + "   and a.receivertype='2' and a.receiveridrange ='0' "  //receiveridrange收货机构范围：0-全部机构 1指定机构
                        //suppliertype供应机构类型 0-无 1-公司 2-供应商 3-公司或供应商   receivertype收货机构类型：1-公司，2-门店 3-配送中心
                        + " )");
            }
            
            //---零售价模板表
            sqlbuf.append(" ,saleprice as ("
                    + " select pluno,unit as priceunit,featureno,price,priceUnitRatio from ("
                    + " select b.*,"
                    + " u.unitratio as priceUnitRatio,"
                    + " row_number() over (partition by b.pluno,b.unit order by templatetype desc,a.createtime desc,b.item desc) as rn "
                    + " from dcp_salepricetemplate a"
                    + " inner join dcp_salepricetemplate_price b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                    + " inner join plu on b.pluno=plu.pluno"
                    //+ " left  join dcp_salepricetemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.rangetype='1' and c1.id='"+companyId+"'"
                    //+ " left  join dcp_salepricetemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.rangetype='2' and c2.id='"+shopId+"'"
                    + " inner join salepriceid c on c.templateid=a.templateid"
                    + " inner join dcp_goods g on g.eid=b.eid and b.pluno=g.pluno and g.status='100' "
                    + " inner join dcp_goods_unit u on u.eid=a.eid and u.pluno=b.pluno and u.ounit=b.unit and u.unit=g.baseunit "
                    + " where a.eid='"+eId+"' "
                    //+ " and a.status='100' and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null)) "
                    //+ " and ((a.restrictshop='1' and c2.id is not null) or a.restrictshop='0' or c1.id is not null)" //20200701  小凤通知拿掉全部门店
                    + " and trunc(b.begindate)<=trunc(sysdate) and trunc(b.enddate)>=trunc(sysdate)"
                    //+ " order by a.templatetype desc,a.createtime desc"
                    + " ) where rn=1 )"
                    + " ");
            
            if (!Check.Null(supplierId)) {
                //---供货价模板表
                sqlbuf.append(",supprice as ("
                        + " select pluno,featureno,unit as distriunit,price as distriprice,distriUnitRatio,supplierid from ("
                        + " select b.*,"
                        + " u.unitratio as distriUnitRatio,a.supplierid,"
                        //【ID1022591】【霸王】3.0 门店要货的时候，显示的要货价格，不是最新的 add b.item desc  by jinzma 20211209
                        + " row_number() over (partition by b.pluno,b.unit,a.supplierid order by c.sort asc,a.createtime desc,b.item desc) as rn "
                        + " from dcp_suppricetemplate a"
                        + " inner join dcp_suppricetemplate_price b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                        + " inner join plu on b.pluno=plu.pluno"
                        //+ " left  join dcp_suppricetemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.id='"+companyId+"'"
                        //+ " left  join dcp_suppricetemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.id='"+shopId+"'"
                        + " inner join suppriceid c on c.templateid=a.templateid"
                        + " inner join dcp_goods g on g.eid=b.eid and b.pluno=g.pluno and g.status='100' "
                        + " inner join dcp_goods_unit u on u.eid=a.eid and u.pluno=b.pluno and u.ounit=b.unit and u.unit=g.baseunit"
                        + " where a.eid='"+eId+"' ");  // and a.status='100'
                
                
                sqlbuf.append(""
                        //+ " and a.supplierid='"+supplierId+"'"
                        //+ " and a.receivertype='1' and (a.receiveridrange ='0' or c1.id is not null)"  ///收货机构为公司
                        //+ " and a.receivertype='2' and (a.receiveridrange ='0' or c2.id is not null)"  ///收货机构固定为门店，不支持非门店查询供货价
                        + " and trunc(b.begindate)<=trunc(sysdate) and trunc(b.enddate)>=trunc(sysdate)"
                        + " ) where rn=1 )"
                        + " ");
            }
            
            //只查询零售价
            if (Check.Null(supplierId)) {
                sqlbuf.append(" "
                        + " select plu.pluno,"
                        + " saleprice.priceunit,saleprice.price,saleprice.priceUnitRatio,"
                        + " u.unitratio,"
                        + " g.sunit as defpriceunit,g.price as defprice,gu.unitratio as defpriceunitratio"          /// 标准零售价(默认销售单位)
                        + " from plu"
                        + " inner join dcp_goods g on g.pluno = plu.pluno and g.eid='"+eId+"' and g.status='100' "
                        + " left  join saleprice on saleprice.pluno=plu.pluno"
                        + " left  join dcp_goods_unit u on u.pluno=plu.pluno and u.ounit=plu.punit and u.eid='"+eId+"' and u.unit=g.baseunit"
                        + " left  join dcp_goods_unit gu on gu.pluno=plu.pluno and gu.ounit=g.sunit and gu.eid='"+eId+"' and gu.unit=g.baseunit "
                        + " where plu.pluno is not null "
                        + " ");
            }
            else {
                sqlbuf.append(" "
                        + " select plu.pluno,plu.punit,"
                        + " saleprice.priceunit,saleprice.price,saleprice.priceUnitRatio,"
                        + " supprice.distriunit,supprice.distriprice,supprice.distriUnitRatio,"
                        + " u.unitratio,supprice.supplierid,"
                        + " g.punit as defsuppriceunit,g.supprice as defsupprice,gu.unitratio as defsuppriceunitratio,"  /// 标准进货价(默认要货单位)
                        + " g.sunit as defpriceunit,g.price as defprice,guprice.unitratio as defpriceunitratio"          /// 标准零售价(默认销售单位)
                        + " from plu"
                        + " left  join saleprice on saleprice.pluno=plu.pluno " /// and saleprice.priceunit = plu.punit"
                        + " left  join supprice on supprice.pluno=plu.pluno "   /// and supprice.distriunit = plu.punit"
                        + " inner join dcp_goods g on g.pluno = plu.pluno and g.eid='"+eId+"' and g.status='100' "
                        + " left  join dcp_goods_unit u on u.pluno=plu.pluno and u.ounit=plu.punit and u.eid='"+eId+"' and u.unit=g.baseunit"
                        + " left  join dcp_goods_unit gu on gu.pluno=plu.pluno and gu.ounit=g.punit and gu.eid='"+eId+"' and gu.unit=g.baseunit"
                        + " left  join dcp_goods_unit guprice on guprice.pluno=plu.pluno and guprice.ounit=g.sunit and guprice.eid='"+eId+"' and guprice.unit=g.baseunit"
                        + " where plu.pluno is not null "
                        + " ");
            }
            
            PosPub.WritePriceLog("公用取价SQL："+sqlbuf.toString());
            PosPub.WritePriceLog("公用取价SQL执行开始");
            List<Map<String, Object>> getQData=dao.executeQuerySQL(sqlbuf.toString(), null);
            PosPub.WritePriceLog("公用取价SQL执行结束");

            //查询一下dcp_goods_unit 防止没传
            String dgSql="with plu as ("+withasSql_Pluno+")" +
                    "select a.* from DCP_GOODS_UNIT a" +
                    " inner join plu b on a.pluno=b.pluno and a.ounit=b.punit " +
                    " where a.eid='"+eId+"'";
            List<Map<String, Object>> dgList = dao.executeQuerySQL(dgSql, null);


            for (Map<String, Object> onePlu : pluList) {
                String pluNo = onePlu.get("PLUNO").toString();
                String punit = onePlu.get("PUNIT").toString();
                String baseUnit="";
                String unitRatio="";
                List<Map<String, Object>> collect = dgList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo) && x.get("OUNIT").toString().equals(punit)).collect(Collectors.toList());
                if(onePlu.get("BASEUNIT")==null||onePlu.get("BASEUNIT").toString().equals("")){
                    if(collect.size()>0){
                        baseUnit=collect.get(0).get("UNIT").toString();
                    }
                }else{
                    baseUnit = onePlu.get("BASEUNIT").toString();
                }
                if(onePlu.get("UNITRATIO")==null||onePlu.get("UNITRATIO").toString().equals("")){
                    if(collect.size()>0){
                        unitRatio=collect.get(0).get("UNITRATIO").toString();
                    }
                }else{
                    unitRatio = onePlu.get("UNITRATIO").toString();
                }


                //String baseUnit = onePlu.get("BASEUNIT").toString();
                //String unitRatio = onePlu.get("UNITRATIO").toString();
                String ghSupplierId =onePlu.get("SUPPLIERID")==null?"": onePlu.get("SUPPLIERID").toString();//供货组织
                String ghCorp="";
                List<Map<String, Object>> gOrgs1 = orgList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(ghSupplierId)).collect(Collectors.toList());
                if (gOrgs1.size()>0){
                    ghCorp=gOrgs1.get(0).get("CORP").toString();
                }
                String price="0";
                String distriPrice="0";
                
                Map<String, Object> condiV=new HashMap<String, Object>();
                condiV.put("PLUNO", pluNo);
                List<Map<String, Object>> QData = MapDistinct.getWhereMap(getQData, condiV, true);
                
                //计算零售价
                price = getPrice(pluNo,baseUnit,punit,unitRatio,QData,priceLengthInt );
                
                if (!Check.Null(supplierId))  // 非空查询配送价
                {
                    //计算配送价
                    distriPrice=getDistriPrice(pluNo,baseUnit,punit,unitRatio,QData,distriPriceLengthInt,ghCorp );
                }
                
                BigDecimal price_b = new BigDecimal(price);
                price_b=price_b.setScale(priceLengthInt, RoundingMode.HALF_UP);
                price_b = price_b.stripTrailingZeros();  ////去除多余的零，康大爷坚决要求的
                
                BigDecimal distriPrice_b = new BigDecimal(distriPrice);
                distriPrice_b=distriPrice_b.setScale(distriPriceLengthInt, RoundingMode.HALF_UP);
                distriPrice_b=distriPrice_b.stripTrailingZeros();   ////去除多余的零，康大爷坚决要求的
                
                Map<String, Object> mapPrice=new HashMap<String, Object>();
                mapPrice.put("PLUNO", pluNo);
                mapPrice.put("PUNIT", punit);
                mapPrice.put("PRICE", price_b.toPlainString());
                mapPrice.put("DISTRIPRICE", distriPrice_b.toPlainString());
                getPrice.add(mapPrice);
                
                QData = null;
            }
            
            return getPrice;
            
        } catch (Exception e) {
            getPrice.clear();
            return getPrice;
        }
    }

    public List<Map<String, Object>> getPrice(DsmDAO dao,String eId,String companyId,String shopId,List<Map<String, Object>> pluList,int sort) throws Exception{
        List<Map<String, Object>> getPrice=new ArrayList<Map<String, Object>>();
        //优先取收货组织零售价模板，其次取收货组织所属公司零售价模板，最后取DCP_GOODS标准零售价
        // 避免SQL执行异常，为空就直接返回  by jinzma 20210527
        if (pluList==null || pluList.isEmpty()){
            return getPrice ;
        }

        try {
            ///处理单价和金额小数位数  BY JZMA 20200401
            String priceLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "priceLength");
            if (Check.Null(priceLength) || !PosPub.isNumeric(priceLength)) {
                priceLength = "2";
            }
            int priceLengthInt = Integer.valueOf(priceLength);

            StringBuffer sJoinPluno = new StringBuffer("");
            StringBuffer sJoinPunit = new StringBuffer("");
            for (Map<String, Object> mapAll : pluList) {
                sJoinPluno.append(mapAll.get("PLUNO").toString() + ",");
                sJoinPunit.append(mapAll.get("PUNIT").toString() + ",");
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("PLUNO", sJoinPluno.toString());
            map.put("PUNIT", sJoinPunit.toString());

            String withasSql_Pluno = getFormatSourceMultiColWith(map);
            map.clear();
            map = null;
            StringBuffer sb = new StringBuffer();
            sb.append("with plu as (" + withasSql_Pluno + ")");
            if (1 == sort) {
                sb.append(" ,salepriceid as ("
                        + " select a.templateid from dcp_salepricetemplate a"
                        + " inner join dcp_salepricetemplate_range b on a.eid=b.eid and a.templateid=b.templateid and b.id='" + companyId + "'"
                        + " where a.eid='" + eId + "' and a.status='100' and a.templatetype='COMPANY'"
                        + " union all"
                        + " select a.templateid from dcp_salepricetemplate a"
                        + " inner join dcp_salepricetemplate_range b on a.eid=b.eid and a.templateid=b.templateid and b.id='" + shopId + "'"
                        + " where a.eid='" + eId + "' and a.status='100' and a.templatetype='SHOP'"
                        + " )");
            } else {
                sb.append(" ,salepriceid as ("
                        + " select a.templateid from dcp_salepricetemplate a"
                        + " inner join dcp_salepricetemplate_range b on a.eid=b.eid and a.templateid=b.templateid and b.id='" + shopId + "'"
                        + " where a.eid='" + eId + "' and a.status='100' and a.templatetype='SHOP'"
                        + " union all"
                        + " select a.templateid from dcp_salepricetemplate a"
                        + " inner join dcp_salepricetemplate_range b on a.eid=b.eid and a.templateid=b.templateid and b.id='" + companyId + "'"
                        + " where a.eid='" + eId + "' and a.status='100' and a.templatetype='COMPANY'"
                        + " )");
            }

            sb.append(" ,saleprice as ("
                    + " select pluno,unit as priceunit,featureno,price,priceUnitRatio from ("
                    + " select b.*,"
                    + " u.unitratio as priceUnitRatio,"
                    + " row_number() over (partition by b.pluno,b.unit order by templatetype desc,a.createtime desc,b.item desc) as rn "
                    + " from dcp_salepricetemplate a"
                    + " inner join dcp_salepricetemplate_price b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                    + " inner join plu on b.pluno=plu.pluno"
                    + " inner join salepriceid c on c.templateid=a.templateid"
                    + " inner join dcp_goods g on g.eid=b.eid and b.pluno=g.pluno and g.status='100' "
                    + " inner join dcp_goods_unit u on u.eid=a.eid and u.pluno=b.pluno and u.ounit=b.unit and u.unit=g.baseunit "
                    + " where a.eid='" + eId + "' "
                    + " and trunc(b.begindate)<=trunc(sysdate) and trunc(b.enddate)>=trunc(sysdate)"
                    + " ) where rn=1 )"
                    + " ");

            sb.append(" "
                    + " select plu.pluno,"
                    + " saleprice.priceunit,saleprice.price,saleprice.priceUnitRatio,"
                    + " u.unitratio,"
                    + " g.sunit as defpriceunit,g.price as defprice,gu.unitratio as defpriceunitratio"          /// 标准零售价(默认销售单位)
                    + " from plu"
                    + " inner join dcp_goods g on g.pluno = plu.pluno and g.eid='" + eId + "' and g.status='100' "
                    + " left  join saleprice on saleprice.pluno=plu.pluno"
                    + " left  join dcp_goods_unit u on u.pluno=plu.pluno and u.ounit=plu.punit and u.eid='" + eId + "' and u.unit=g.baseunit"
                    + " left  join dcp_goods_unit gu on gu.pluno=plu.pluno and gu.ounit=g.sunit and gu.eid='" + eId + "' and gu.unit=g.baseunit "
                    + " where plu.pluno is not null "
                    + " ");

            List<Map<String, Object>> getQData = dao.executeQuerySQL(sb.toString(), null);

            for (Map<String, Object> onePlu : pluList) {
                String pluNo = onePlu.get("PLUNO").toString();
                String baseUnit = onePlu.get("BASEUNIT").toString();
                String punit = onePlu.get("PUNIT").toString();
                String unitRatio = onePlu.get("UNITRATIO").toString();
                String price = "0";
                String distriPrice = "0";

                Map<String, Object> condiV = new HashMap<String, Object>();
                condiV.put("PLUNO", pluNo);
                List<Map<String, Object>> QData = MapDistinct.getWhereMap(getQData, condiV, true);

                //计算零售价
                price = getPrice(pluNo, baseUnit, punit, unitRatio, QData, priceLengthInt);
                if(price.equals("0")){
                    List<Map<String, Object>> priceFilter = QData.stream().filter(x -> Check.NotNull(x.get("PRICEUNIT").toString())).collect(Collectors.toList());
                    if(priceFilter.size()<=0){
                        //没模板
                        List<Map<String, Object>> priceFilter2 = QData.stream().filter(x -> Check.NotNull(x.get("DEFPRICE").toString())).collect(Collectors.toList());
                        if(priceFilter2.size()>0){
                            price=priceFilter2.get(0).get("DEFPRICE").toString();
                        }
                    }
                }

                BigDecimal price_b = new BigDecimal(price);
                price_b = price_b.setScale(priceLengthInt, RoundingMode.HALF_UP);
                price_b = price_b.stripTrailingZeros();  ////去除多余的零，康大爷坚决要求的


                Map<String, Object> mapPrice = new HashMap<String, Object>();
                mapPrice.put("PLUNO", pluNo);
                mapPrice.put("PUNIT", punit);
                mapPrice.put("PRICE", price_b.toPlainString());
                getPrice.add(mapPrice);

                QData = null;
            }

        }
        catch (Exception e){
            getPrice.clear();
            return getPrice;
        }

        return getPrice;
    }


    /**
     * todo
     * @param dao
     * @param eId
     * @param demandObjectId 收货组织
     * @param pluList 供货组织在这里面
     * @return
     */
    public List<Map<String, Object>> getDistriPrice(DsmDAO dao,String eId,String demandObjectId,List<Map<String, Object>> pluList ){
        List<Map<String, Object>> getPrice=new ArrayList<Map<String, Object>>();
        StringBuffer sqlbuf = new StringBuffer("");
        try
        {
            // 避免SQL执行异常，为空就直接返回  by jinzma 20210527
            if (pluList==null || pluList.isEmpty()){
                return getPrice ;
            }

            //供货法人  收货法人 收货公司
            String orgSql="select * from dcp_org a where a.eid='"+eId+"' ";
            List<Map<String, Object>> orgList=dao.executeQuerySQL(orgSql,null);
            String sCorp="";

            List<Map<String, Object>> sOrgs = orgList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(demandObjectId)).collect(Collectors.toList());
            if (sOrgs.size()>0){
                sCorp=sOrgs.get(0).get("CORP").toString();
            }


            String distriPriceLength = PosPub.getPARA_SMS(StaticInfo.dao,eId,demandObjectId,"distriPriceLength");
            if (Check.Null(distriPriceLength)||!PosPub.isNumeric(distriPriceLength)){
                distriPriceLength="2";
            }
            int distriPriceLengthInt = Integer.valueOf(distriPriceLength);

            //成品
            StringBuffer sJoinPluno=new StringBuffer("");
            StringBuffer sJoinPunit=new StringBuffer("");
            for (Map<String, Object> mapAll : pluList){
                sJoinPluno.append(mapAll.get("PLUNO").toString()+",");
                sJoinPunit.append(mapAll.get("PUNIT").toString()+",");
            }
            Map<String, String> map=new HashMap<String, String>();
            map.put("PLUNO", sJoinPluno.toString());
            map.put("PUNIT", sJoinPunit.toString());

            String withasSql_Pluno= getFormatSourceMultiColWith(map);
            map.clear();
            map=null;

            //---商品列表
            sqlbuf.append("with plu as ("+withasSql_Pluno+")");

            {
                //供货组织每个商品可能不一样

                //获取供货价模板id
                //1、供货条件：供货组织匹配优先序分别：1️⃣供货法人 > 2️⃣全部法人（需新增供货机构类型）@YYY🐨
                //2、需方条件：收货组织匹配优先序分别：1️⃣收货组织 > 2️⃣组织所属法人(公司)>3️⃣所有组织



                //---供货价模板ID表
                sqlbuf.append(" ,suppriceid as ("
                        + " select a.templateid,1 as sort from dcp_suppricetemplate a"
                        + " inner join dcp_suppricetemplate_range b on a.eid=b.eid and a.templateid=b.templateid and b.id='"+demandObjectId+"'"
                        + " where a.eid='"+eId+"' and a.status='100' "
                        + "   and a.receivertype='2' and a.receiveridrange ='1' "//and a.supplierid='"+gCorp+"'
                        + " union all"
                        + " select a.templateid,2 as sort from dcp_suppricetemplate a"
                        + " inner join dcp_suppricetemplate_range b on a.eid=b.eid and a.templateid=b.templateid and b.id='"+sCorp+"'"
                        + " where a.eid='"+eId+"' and a.status='100' "
                        + "   and a.receivertype='1'  "
                        + " union all"
                        + " select a.templateid,3 as sort from dcp_suppricetemplate a "
                        + " where a.eid='"+eId+"' and a.status='100' "
                        + "   and a.receivertype='2' and a.receiveridrange ='0' "  //receiveridrange收货机构范围：0-全部机构 1指定机构
                        //suppliertype供应机构类型 0-无 1-公司 2-供应商 3-公司或供应商   receivertype收货机构类型：1-公司，2-门店 3-配送中心
                        + " )");
            }


            {
                //---供货价模板表
                sqlbuf.append(",supprice as ("
                        + " select pluno,featureno,unit as distriunit,price as distriprice,distriUnitRatio,supplierid from ("
                        + " select b.*,"
                        + " u.unitratio as distriUnitRatio,a.supplierid,"
                        //【ID1022591】【霸王】3.0 门店要货的时候，显示的要货价格，不是最新的 add b.item desc  by jinzma 20211209
                        + " row_number() over (partition by b.pluno,b.unit,a.supplierid order by c.sort asc,a.createtime desc,b.item desc) as rn "
                        + " from dcp_suppricetemplate a"
                        + " inner join dcp_suppricetemplate_price b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                        + " inner join plu on b.pluno=plu.pluno"
                        //+ " left  join dcp_suppricetemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.id='"+companyId+"'"
                        //+ " left  join dcp_suppricetemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.id='"+shopId+"'"
                        + " inner join suppriceid c on c.templateid=a.templateid"
                        + " inner join dcp_goods g on g.eid=b.eid and b.pluno=g.pluno and g.status='100' "
                        + " inner join dcp_goods_unit u on u.eid=a.eid and u.pluno=b.pluno and u.ounit=b.unit and u.unit=g.baseunit"
                        + " where a.eid='"+eId+"' ");  // and a.status='100'


                sqlbuf.append(""
                        //+ " and a.supplierid='"+supplierId+"'"
                        //+ " and a.receivertype='1' and (a.receiveridrange ='0' or c1.id is not null)"  ///收货机构为公司
                        //+ " and a.receivertype='2' and (a.receiveridrange ='0' or c2.id is not null)"  ///收货机构固定为门店，不支持非门店查询供货价
                        + " and trunc(b.begindate)<=trunc(sysdate) and trunc(b.enddate)>=trunc(sysdate)"
                        + " ) where rn=1 )"
                        + " ");
            }
{
                sqlbuf.append(" "
                        + " select plu.pluno,plu.punit,"
                        + " saleprice.priceunit,saleprice.price,saleprice.priceUnitRatio,"
                        + " supprice.distriunit,supprice.distriprice,supprice.distriUnitRatio,"
                        + " u.unitratio,supprice.supplierid,"
                        + " g.punit as defsuppriceunit,g.supprice as defsupprice,gu.unitratio as defsuppriceunitratio,"  /// 标准进货价(默认要货单位)
                        + " g.sunit as defpriceunit,g.price as defprice,guprice.unitratio as defpriceunitratio"          /// 标准零售价(默认销售单位)
                        + " from plu"
                        + " left  join saleprice on saleprice.pluno=plu.pluno " /// and saleprice.priceunit = plu.punit"
                        + " left  join supprice on supprice.pluno=plu.pluno "   /// and supprice.distriunit = plu.punit"
                        + " inner join dcp_goods g on g.pluno = plu.pluno and g.eid='"+eId+"' and g.status='100' "
                        + " left  join dcp_goods_unit u on u.pluno=plu.pluno and u.ounit=plu.punit and u.eid='"+eId+"' and u.unit=g.baseunit"
                        + " left  join dcp_goods_unit gu on gu.pluno=plu.pluno and gu.ounit=g.punit and gu.eid='"+eId+"' and gu.unit=g.baseunit"
                        + " left  join dcp_goods_unit guprice on guprice.pluno=plu.pluno and guprice.ounit=g.sunit and guprice.eid='"+eId+"' and guprice.unit=g.baseunit"
                        + " where plu.pluno is not null "
                        + " ");
            }

            PosPub.WritePriceLog("公用取价SQL："+sqlbuf.toString());
            PosPub.WritePriceLog("公用取价SQL执行开始");
            List<Map<String, Object>> getQData=dao.executeQuerySQL(sqlbuf.toString(), null);
            PosPub.WritePriceLog("公用取价SQL执行结束");

            //查询一下dcp_goods_unit 防止没传
            String dgSql="with plu as ("+withasSql_Pluno+")" +
                    "select a.* from DCP_GOODS_UNIT a" +
                    " inner join plu b on a.pluno=b.pluno and a.ounit=b.punit " +
                    " where a.eid='"+eId+"'";
            List<Map<String, Object>> dgList = dao.executeQuerySQL(dgSql, null);


            for (Map<String, Object> onePlu : pluList) {
                String pluNo = onePlu.get("PLUNO").toString();
                String punit = onePlu.get("PUNIT").toString();
                String baseUnit="";
                String unitRatio="";
                List<Map<String, Object>> collect = dgList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo) && x.get("OUNIT").toString().equals(punit)).collect(Collectors.toList());
                if(onePlu.get("BASEUNIT")==null||onePlu.get("BASEUNIT").toString().equals("")){
                    if(collect.size()>0){
                        baseUnit=collect.get(0).get("UNIT").toString();
                    }
                }else{
                    baseUnit = onePlu.get("BASEUNIT").toString();
                }
                if(onePlu.get("UNITRATIO")==null||onePlu.get("UNITRATIO").toString().equals("")){
                    if(collect.size()>0){
                        unitRatio=collect.get(0).get("UNITRATIO").toString();
                    }
                }else{
                    unitRatio = onePlu.get("UNITRATIO").toString();
                }


                String ghSupplierId =onePlu.get("SUPPLIERID")==null?"": onePlu.get("SUPPLIERID").toString();//供货组织
                String ghCorp="";
                List<Map<String, Object>> gOrgs1 = orgList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(ghSupplierId)).collect(Collectors.toList());
                if (gOrgs1.size()>0){
                    ghCorp=gOrgs1.get(0).get("CORP").toString();
                }

                String distriPrice="0";

                Map<String, Object> condiV=new HashMap<String, Object>();
                condiV.put("PLUNO", pluNo);
                List<Map<String, Object>> QData = MapDistinct.getWhereMap(getQData, condiV, true);

                distriPrice=getDistriPrice(pluNo,baseUnit,punit,unitRatio,QData,distriPriceLengthInt,ghCorp );



                BigDecimal distriPrice_b = new BigDecimal(distriPrice);
                distriPrice_b=distriPrice_b.setScale(distriPriceLengthInt, RoundingMode.HALF_UP);
                distriPrice_b=distriPrice_b.stripTrailingZeros();   ////去除多余的零，康大爷坚决要求的

                Map<String, Object> mapPrice=new HashMap<String, Object>();
                mapPrice.put("PLUNO", pluNo);
                mapPrice.put("PUNIT", punit);
                mapPrice.put("DISTRIPRICE", distriPrice_b.toPlainString());
                getPrice.add(mapPrice);

                QData = null;
            }

            return getPrice;

        } catch (Exception e) {
            getPrice.clear();
            return getPrice;
        }
    }


    /**
     * 动态往字符串数组中添加元素
     * @param arr
     * @param str
     * @return
     */
    public String[] insert(String[] arr, String str)
    {
        int size = arr.length;
        String[] tmp = new String[size + 1];
        System.arraycopy(arr, 0, tmp, 0, size);
        tmp[size] = str;
        return tmp;
    }
    
    public Boolean isEmpty(String s)
    {
        if(s == null)
        {
            return true;
        }
        if(s.length() <= 0)
        {
            return true;
        }
        
        return false;
    }
    
    public String GetRequestString(String json) throws JSONException
    {
        json = json.trim();
        if(json.startsWith("{"))
        {
            json = json.substring(1);
        }
        if(json.endsWith("}"))
        {
            json = json.substring(0,json.length() - 1);
        }
        json = json.trim();
        
        String[] nodes = new String[]{"\"serviceId\"","\"langType\"","\"request\"","\"sign\""};
        
        String request = "\"request\"";
        int request_index = json.indexOf(request);
        int beginindex = request_index+9;
        
        int endindex = json.length();
        for(int i = 0;i<nodes.length;i++)
        {
            int index = json.indexOf(nodes[i]);
            if(index > request_index)
            {
                if(index < endindex)
                {
                    endindex = index;
                }
            }
        }
        
        String requeststr = "";
        if(endindex > beginindex)
        {
            requeststr = json.substring(beginindex,endindex).trim();
        }
        else
        {
            requeststr = json.substring(beginindex).trim();
        }
        if(requeststr.startsWith(":"))
        {
            requeststr = requeststr.substring(1);
        }
        if(requeststr.endsWith(","))
        {
            requeststr = requeststr.substring(0,requeststr.length() - 1);
        }
        requeststr = requeststr.trim();
        return requeststr;
    }
    
    public String GetRequestString_old(String json) throws JSONException
    {
        json = json.trim();
        if(json.startsWith("{"))
        {
            json = json.substring(1);
        }
        if(json.endsWith("}"))
        {
            json = json.substring(0,json.length() - 1);
        }
        json = json.trim();
        
        String serviceId = "\"serviceId\"";
        String request = "\"request\"";
        String sign = "\"sign\"";
        
        int serviceId_index = json.indexOf(serviceId);
        int request_index = json.indexOf(request);
        int sign_index = json.indexOf(sign);
        
        int beginindex = request_index+9;
        int endindex = beginindex;
        
        if(request_index < serviceId_index && request_index < sign_index)
        {
            if(serviceId_index < sign_index)
            {
                endindex = serviceId_index;
            }
            else
            {
                endindex = sign_index;
            }
        }
        
        if(request_index > serviceId_index && request_index > sign_index)
        {
            endindex = -1;
        }
        
        if(request_index > serviceId_index && request_index < sign_index )
        {
            endindex = sign_index;
        }
        
        if(request_index > sign_index && request_index < serviceId_index )
        {
            endindex = serviceId_index;
        }
        
        String requeststr = "";
        if(endindex > beginindex)
        {
            requeststr = json.substring(beginindex,endindex).trim();
        }
        else
        {
            requeststr = json.substring(beginindex).trim();
        }
        if(requeststr.startsWith(":"))
        {
            requeststr = requeststr.substring(1);
        }
        if(requeststr.endsWith(","))
        {
            requeststr = requeststr.substring(0,requeststr.length() - 1);
        }
        requeststr = requeststr.trim();
        return requeststr;
    }
    
    public Boolean CheckSign(String json,String key,String sign) throws JSONException
    {
        //先去掉签名校验
        //return true;
        
        if(isEmpty(key))
        {
            return true;
        }
        
        JSONObject jsonObj = new JSONObject(json);
        String a = jsonObj.getJSONObject("request").toString();
        
        if(CheckRequestSign(a,key,sign))
        {
            return true;
        }

		/*这个多余
    	a = GetRequestString(json);
		if(CheckRequestSign(a,key,sign))
		{
			return true;
		}
		 */
        
        return false;
    }
    
    public Boolean CheckRequestSign(String request,String key,String sign)
    {
        if(isEmpty(key))
        {
            return true;
        }
        if(isEmpty(request))
        {
            return true;
        }
        String str = request+key;
        
        String signed = DigestUtils.md5Hex(str);
        
        if(sign.equals(signed))
        {
            return true;
        }
        return false;
        
    }
    
    /**
     * 获取集合中最接近某数的值。
     * @param intarray
     * @param number
     * @return
     */
    public Double getNumberThree(Double[] intarray,Double number){
        double index = Math.abs(number-intarray[0]);
        double result = intarray[0];
        for (double i : intarray) {
            double abs = Math.abs(number-i);
            if(abs <= index){
                index = abs;
                result = i;
            }
        }
        return result;
    }
    
    public String getTableFormatContent(String title,List<Map<String, String>> rows)
    {
        String htmlcode="";
        StringBuffer sb = new StringBuffer("");
        sb.append("<html><head></head><body>");
        if (title!=null && title.length()>0)
        {
            sb.append("<h1>"+title+"</h1>");
        }
        
        sb.append("<table border=\"5\" style=\"border:solid 1px #E8F2F9;font-size=14px;font-size:18px;\">");
        sb.append("<tr style=\"background-color: #428BCA; color:#ffffff\">");
        
        if (rows==null || rows.size()==0)
        {
            htmlcode="";
        }
        else
        {
            for(Map.Entry<String, String> entry : rows.get(0).entrySet())
            {
                //表头
                sb.append("<th>"+entry.getKey()+"</th>");
            }
            sb.append("</tr>");
            
            //
            StringBuffer sb_Row = new StringBuffer("");
            
            for (Map<String, String> map : rows)
            {
                sb_Row.append("<tr>");
                for(Map.Entry<String, String> entry : map.entrySet())
                {
                    //表行
                    sb_Row.append("<td>"+entry.getValue()+"</td>");
                }
                sb_Row.append("</tr>");
            }
            
            
            sb.append(sb_Row.toString());
            sb_Row.setLength(0);
            sb_Row=null;
            
            sb.append("</table></body></html>");
            
            htmlcode=sb.toString();
        }
        
        sb.setLength(0);
        sb=null;
        
        return htmlcode;
    }
    
    /**
     * 根据指定日期获取周几
     * @param date
     * @return
     */
    public String getWeekOfDate(Date date)
    {
        String[] weekDays = {"7","1", "2", "3", "4", "5", "6" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        int w = cal.get(Calendar.DAY_OF_WEEK)-1;
        if (w < 0)
        {
            w = 0;
        }
        return weekDays[w];
    }
    
    //做成单例 ,避免Gson内存泄漏
    private static final Gson gson=new Gson();
    /**
     * 保存ERP调用中台的WS记录
     * @param serviceName   ERP调用服务名
     * @param erpJson       ERP请求JSON
     * @param nrcJson       云中台转换JSON
     * @param resJson       云中台返回JSON
     * @param errMsg        异常说明
     * @return
     */
    public void erpWSSave(String serviceName,String erpJson,String nrcJson,String resJson,String errMsg) {
        ///服务名为空，ERPJSON解析异常
        ///errMsg有值的两种情况
        ///1.解析nrcJson发生异常，nrcJson为空值
        ///2.解析resJson发生异常，resJson可能有值
        
        String eId=" ";
        String organizationNo=" ";
        List<String> shops = new ArrayList<>();
        String docNo=" ";
        String code="-1";                 //if CODE=0  WSLOG.processStatus==Y 保存为成功
        boolean resSuccess=false;
        String resServiceDescription="";
        
        try
        {
            //服务名为空，JSON解析失败，只保存ERP原始请求，企业编号和门店编号都为空
            if (Check.Null(serviceName))
            {
                InsertWSLOG.insert_WSLOG(" ",docNo,eId, organizationNo, "2", erpJson, resJson, "-1", errMsg);
                return;
            }
            
            //ERP JSON转换为云中台JSON出现异常，只保存ERP原始请求，企业编号和门店编号都为空
            if (Check.Null(nrcJson))
            {
                InsertWSLOG.insert_WSLOG(serviceName,docNo, eId, organizationNo, "2", erpJson, resJson, "-1", errMsg);
                return;
            }
            else
            {
                //解析nrcJson 获取企业编号
                JSONObject jsonObj = new JSONObject(nrcJson);
                eId=jsonObj.optString("eId", " ");
                
                //解析nrcJson 获取ERP单据编号和门店编号
                switch (serviceName) {
                    case "pos.receiving.create":  //收货通知单新增
                        organizationNo=jsonObj.optString("shopId", " ");
                        docNo=jsonObj.optString("loadDocNO", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.counting.create":  //门店盘点计划新增
                        JSONArray jsonArray = jsonObj.optJSONArray("stocktaskshop");
                        for(int i=0 ; i<jsonArray.length(); i++)
                        {
                            organizationNo= jsonArray.getJSONObject(i).optString("shopId", "").toString();
                            if (!Check.Null(organizationNo))
                            {
                                shops.add(organizationNo);
                            }
                        }
                        docNo=jsonObj.optString("loadDocNO", " ");
                        break;
                    case "pos.adjust.create":
                        organizationNo=jsonObj.optString("shopId", " ");
                        docNo=jsonObj.optString("loadDocNO", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.return.update":
                        organizationNo=jsonObj.optString("shopId", " ");
                        docNo=jsonObj.optString("stockOutNO", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.scrap.update":
                        organizationNo=jsonObj.optString("shopId", " ");
                        docNo=jsonObj.optString("lstockOutNO", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.orgorder.update":
                        organizationNo=jsonObj.optString("shop_no", " ");
                        docNo=jsonObj.optString("front_no", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.reject.create":
                        organizationNo=jsonObj.optString("shopId", " ");
                        docNo=jsonObj.optString("docNO", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.undo.create":
                        organizationNo=jsonObj.optString("shopId", " ");
                        docNo=jsonObj.optString("loadDocNO", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.orderstatus.update":
                        organizationNo=jsonObj.optString("oShopId", " ");
                        docNo=jsonObj.optString("orderNO", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.requisition.update":
                        organizationNo=jsonObj.optString("shopId", " ");
                        docNo=jsonObj.optString("pOrderno", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.fee.update":     //费用单状态更新
                        organizationNo=jsonObj.optString("shop_no", " ");
                        docNo=jsonObj.optString("front_no", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.transfer.create":     //调拨出库单新增
                        organizationNo=jsonObj.optString("shopId", " ");
                        docNo=jsonObj.optString("loadDocNo", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.requisition.create":     //要货单新增
                        organizationNo=jsonObj.optString("shopId", " ");
                        docNo=jsonObj.optString("loadDocNo", " ");
                        shops.add(organizationNo);
                        break;
                    case "pos.requisition.ecsflg":     //要货单结案
                        organizationNo=jsonObj.optString("shopId", " ");
                        docNo=jsonObj.optString("loadDocNo", " ");
                        shops.add(organizationNo);
                        break;
                    default:
                        //未知的服务名，直接返回
                        return;
                }
                
                jsonObj=null;
            }
            
            if (!Check.Null(resJson))   //解析返回JSON，获取成功否等信息
            {
                @SuppressWarnings("unchecked")
                Map<String,Object> jsonMap = gson.fromJson(resJson, Map.class);
                resSuccess = (boolean)jsonMap.getOrDefault("success",false);
                resServiceDescription=jsonMap.getOrDefault("serviceDescription", "").toString();
                
                //			resServiceStatus=jsonMap.getOrDefault("serviceStatus", "").toString();
                //			docNo=jsonMap.getOrDefault("doc_no", " ").toString();
                //			organizationNo=jsonMap.getOrDefault("org_no", " ").toString();
                //			if (Check.Null(docNo)) docNo=" ";
                //			if (Check.Null(organizationNo)) organizationNo=" ";
                
                jsonMap.clear();
                jsonMap=null;
            }
            if (resSuccess)
            {
                code="0";
            }
            
            for (String shopId : shops)
            {
                organizationNo = shopId;
                InsertWSLOG.insert_WSLOG(serviceName,docNo, eId, organizationNo, "2", erpJson, resJson, code, resServiceDescription);
            }
            
            return;
        }
        catch(Exception e)
        {
            return;
        }
        
        
    }
    
    /**
     * 根据输入的地址获取经纬度
     * @param address
     * @return
     * @throws JSONException
     */
    public String getLngLat(String address) throws JSONException
    {
        StringBuffer json = new StringBuffer();
        try
        {
            URL u = new URL("http://restapi.amap.com/v3/geocode/geo?address="+address+"&output=JSON&key=66b4b07702ff29ecc1ee9fbbf6980675");
            URLConnection yc = u.openConnection();
            
            InputStreamReader is=new InputStreamReader(yc.getInputStream(),"UTF-8");
            
            //读取返回的数据
            BufferedReader in = new BufferedReader(is);
            String inputline = null;
            while((inputline=in.readLine())!=null)
            {
                json.append(inputline);
            }
            in.close();
            in=null;
            
            is.close();
            is=null;
            
            yc=null;
            u=null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        String jsonStr=json.toString();
        JSONObject jsonObject = new JSONObject(jsonStr);
        
        //
        String res="";
        
        if(jsonObject.getJSONArray("geocodes").length()>0)
            res=jsonObject.getJSONArray("geocodes").getJSONObject(0).get("location").toString();
        else
            res= null;
        
        //
        json.setLength(0);
        json=null;
        
        jsonObject=null;
        
        return res;
    }
    
    private String getPrice(String pluNo,String baseUnit,String punit,String unitRatio,List<Map<String, Object>> getQData,int priceLengthInt )
    {
        String price="0";
        try
        {
            Map<String, Object> condiV=new HashMap<String, Object>();
            condiV.put("PLUNO", pluNo);
            condiV.put("PRICEUNIT",punit);
            List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getQData, condiV, false);
            condiV.clear();
            if(priceList!=null && priceList.size()>0 )  //取价格档对应的单位
            {
                price=priceList.get(0).get("PRICE").toString();
                if (Check.Null(price) || !PosPub.isNumericType(price))
                {
                    price="0";
                }
            }
            else
            {
                condiV.put("PLUNO", pluNo);
                condiV.put("PRICEUNIT",baseUnit);
                priceList= MapDistinct.getWhereMap(getQData, condiV, false);
                condiV.clear();
                if (priceList!=null && priceList.size()>0)   //取价格档对应的基准单位
                {
                    price=priceList.get(0).get("PRICE").toString();
                    if (Check.Null(price) || !PosPub.isNumericType(price))
                    {
                        price="0";
                    }
                    BigDecimal unitRatio_b = new BigDecimal("0");
                    if (!Check.Null(unitRatio) && PosPub.isNumericType(unitRatio))
                    {
                        unitRatio_b=new BigDecimal(unitRatio);
                    }
                    price=unitRatio_b.multiply(new BigDecimal(price)).setScale(priceLengthInt, BigDecimal.ROUND_HALF_UP).toString();
                }
                else
                {
                    condiV.put("PLUNO", pluNo);
                    condiV.put("PUNIT", punit);
                    priceList= MapDistinct.getWhereMap(getQData, condiV, false);
                    condiV.clear();
                    if (priceList!=null && priceList.size()>0)   //用价格档对应的单位和单位转换率，转换成punit对应的单价
                    {
                        price=priceList.get(0).get("PRICE").toString();
                        if (Check.Null(price) || !PosPub.isNumericType(price))
                        {
                            String defPriceUnit = priceList.get(0).get("DEFPRICEUNIT").toString();
                            String defPrice =priceList.get(0).get("DEFPRICE").toString();
                            String defPriceUnitRatio =priceList.get(0).get("DEFPRICEUNITRATIO").toString();
                            
                            ///取商品资料档的标准零售价  by 20200828 jinzma 小凤新增规格
                            if ( !Check.Null(defPriceUnit)&&!Check.Null(defPrice)&&!Check.Null(defPriceUnitRatio))
                            {
                                BigDecimal unitRatio_b = new BigDecimal("0");
                                if (!Check.Null(unitRatio) && PosPub.isNumericType(unitRatio))
                                {
                                    unitRatio_b=new BigDecimal(unitRatio);
                                }
                                BigDecimal defPriceUnitRatio_b = new BigDecimal(defPriceUnitRatio);
                                //精度用8避免计算出现精度丢失 by jinzma 20210331
                                BigDecimal price_b = unitRatio_b.multiply(new BigDecimal(defPrice)).divide(defPriceUnitRatio_b,8,BigDecimal.ROUND_HALF_UP); // 1大箱  10元   转换率 5 , 1小箱  ? 元   转换率 2   ?=2*10/5
                                price=price_b.setScale(priceLengthInt,BigDecimal.ROUND_HALF_UP).toPlainString();
                            }
                            else
                            {
                                price="0";
                            }
                        }
                        else
                        {
                            String priceUnitRatio = priceList.get(0).get("PRICEUNITRATIO").toString();
                            BigDecimal priceUnitRatio_b = new BigDecimal("0");
                            if (!Check.Null(priceUnitRatio) && PosPub.isNumericType(priceUnitRatio))
                            {
                                priceUnitRatio_b=new BigDecimal(priceUnitRatio);
                            }
                            
                            BigDecimal unitRatio_b = new BigDecimal("0");
                            if (!Check.Null(unitRatio) && PosPub.isNumericType(unitRatio))
                            {
                                unitRatio_b=new BigDecimal(unitRatio);
                            }
                            //精度用8避免计算出现精度丢失 by jinzma 20210331
                            BigDecimal price_b = unitRatio_b.multiply(new BigDecimal(price)).divide(priceUnitRatio_b,8,BigDecimal.ROUND_HALF_UP); // 1大箱  10元   转换率 5 , 1小箱  ? 元   转换率 2   ?=2*10/5
                            price=price_b.setScale(priceLengthInt, BigDecimal.ROUND_HALF_UP).toPlainString();
                        }
                    }
                    else
                    {
                        price="0";
                    }
                }
            }
            return price;
        }
        catch (Exception e)
        {
            return "0";
        }
    }
    
    private String getDistriPrice(String pluNo,String baseUnit,String punit,String unitRatio,List<Map<String, Object>> getQData,int distriPriceLengthInt ,String ghCorp){
        String distriPrice="0";
        try
        {
            //计算配送价
            Map<String, Object> condiV=new HashMap<String, Object>();
            condiV.put("PLUNO", pluNo);
            condiV.put("DISTRIUNIT",punit);
            condiV.put("SUPPLIERID",ghCorp);
            List<Map<String, Object>>distriList= MapDistinct.getWhereMap(getQData, condiV, false);
            condiV.clear();
            if(distriList!=null && distriList.size()>0 )  //取价格档对应的单位
            {
                distriPrice=distriList.get(0).get("DISTRIPRICE").toString();
                if (Check.Null(distriPrice) || !PosPub.isNumericType(distriPrice))
                {
                    distriPrice="0";
                }
            }
            else
            {
                condiV.put("PLUNO", pluNo);
                condiV.put("DISTRIUNIT",baseUnit);
                condiV.put("SUPPLIERID",ghCorp);
                distriList= MapDistinct.getWhereMap(getQData, condiV, false);
                condiV.clear();
                if (distriList!=null && distriList.size()>0)   //取价格档对应的基准单位
                {
                    distriPrice=distriList.get(0).get("DISTRIPRICE").toString();
                    if (Check.Null(distriPrice) || !PosPub.isNumericType(distriPrice))
                    {
                        distriPrice="0";
                    }
                    BigDecimal unitRatio_b = new BigDecimal("0");
                    if (!Check.Null(unitRatio) && PosPub.isNumericType(unitRatio))
                    {
                        unitRatio_b=new BigDecimal(unitRatio);
                    }
                    distriPrice=unitRatio_b.multiply(new BigDecimal(distriPrice)).setScale(distriPriceLengthInt, BigDecimal.ROUND_HALF_UP).toString();
                }
                else
                {
                    //原来外面就加了供应商条件，现在加在里面  要是查不到直接取商品上的
                    condiV.put("PLUNO", pluNo);
                    condiV.put("PUNIT", punit);
                    condiV.put("SUPPLIERID",ghCorp);
                    distriList= MapDistinct.getWhereMap(getQData, condiV, false);
                    condiV.clear();
                    if (distriList!=null && distriList.size()>0)   //用价格档对应的单位和单位转换率，转换成punit对应的单价
                    {
                        distriPrice=distriList.get(0).get("DISTRIPRICE").toString();
                        if (Check.Null(distriPrice) || !PosPub.isNumericType(distriPrice))
                        {
                            String defSupPriceUnit = distriList.get(0).get("DEFSUPPRICEUNIT").toString();
                            String defSupPrice =distriList.get(0).get("DEFSUPPRICE").toString();
                            String defSupPriceUnitRatio =distriList.get(0).get("DEFSUPPRICEUNITRATIO").toString();
                            
                            ///取商品资料档的供货价  by 20200828 jinzma 小凤新增规格
                            if ( !Check.Null(defSupPriceUnit)&&!Check.Null(defSupPrice)&&!Check.Null(defSupPriceUnitRatio))
                            {
                                BigDecimal unitRatio_b = new BigDecimal("0");
                                if (!Check.Null(unitRatio) && PosPub.isNumericType(unitRatio))
                                {
                                    unitRatio_b=new BigDecimal(unitRatio);
                                }
                                BigDecimal defSupPriceUnitRatio_b = new BigDecimal(defSupPriceUnitRatio);
                                //精度用8避免计算出现精度丢失 by jinzma 20210331
                                BigDecimal distriPrice_b = unitRatio_b.multiply(new BigDecimal(defSupPrice)).divide(defSupPriceUnitRatio_b,8,BigDecimal.ROUND_HALF_UP); // 1大箱  10元   转换率 5 , 1小箱  ? 元   转换率 2   ?=2*10/5
                                distriPrice=distriPrice_b.setScale(distriPriceLengthInt, BigDecimal.ROUND_HALF_UP).toPlainString();
                            }
                            else
                            {
                                distriPrice="0";
                            }
                        }
                        else
                        {
                            String distriUnitRatio = distriList.get(0).get("DISTRIUNITRATIO").toString();
                            BigDecimal distriUnitRatio_b = new BigDecimal("0");
                            if (!Check.Null(distriUnitRatio) && PosPub.isNumericType(distriUnitRatio))
                            {
                                distriUnitRatio_b=new BigDecimal(distriUnitRatio);
                            }
                            
                            BigDecimal unitRatio_b = new BigDecimal("0");
                            if (!Check.Null(unitRatio) && PosPub.isNumericType(unitRatio))
                            {
                                unitRatio_b=new BigDecimal(unitRatio);
                            }
                            //精度用8避免计算出现精度丢失 by jinzma 20210331
                            BigDecimal distriPrice_b = unitRatio_b.multiply(new BigDecimal(distriPrice)).divide(distriUnitRatio_b,8,BigDecimal.ROUND_HALF_UP); // 1大箱  10元   转换率 5 , 1小箱  ? 元   转换率 2   ?=2*10/5
                            distriPrice=distriPrice_b.setScale(distriPriceLengthInt, BigDecimal.ROUND_HALF_UP).toPlainString();
                        }
                    }
                    else
                    {
                        condiV.clear();
                        condiV.put("PLUNO", pluNo);
                        condiV.put("PUNIT", punit);
                        distriList= MapDistinct.getWhereMap(getQData, condiV, false);
                        condiV.clear();
                        if (distriList!=null && distriList.size()>0)   //用价格档对应的单位和单位转换率，转换成punit对应的单价
                        {

                            String defSupPriceUnit = distriList.get(0).get("DEFSUPPRICEUNIT").toString();
                            String defSupPrice =distriList.get(0).get("DEFSUPPRICE").toString();
                            String defSupPriceUnitRatio =distriList.get(0).get("DEFSUPPRICEUNITRATIO").toString();

                            ///取商品资料档的供货价  by 20200828 jinzma 小凤新增规格
                            if ( !Check.Null(defSupPriceUnit)&&!Check.Null(defSupPrice)&&!Check.Null(defSupPriceUnitRatio))
                            {
                                BigDecimal unitRatio_b = new BigDecimal("0");
                                if (!Check.Null(unitRatio) && PosPub.isNumericType(unitRatio))
                                {
                                    unitRatio_b=new BigDecimal(unitRatio);
                                }
                                BigDecimal defSupPriceUnitRatio_b = new BigDecimal(defSupPriceUnitRatio);
                                //精度用8避免计算出现精度丢失 by jinzma 20210331
                                BigDecimal distriPrice_b = unitRatio_b.multiply(new BigDecimal(defSupPrice)).divide(defSupPriceUnitRatio_b,8,BigDecimal.ROUND_HALF_UP); // 1大箱  10元   转换率 5 , 1小箱  ? 元   转换率 2   ?=2*10/5
                                distriPrice=distriPrice_b.setScale(distriPriceLengthInt, BigDecimal.ROUND_HALF_UP).toPlainString();
                            }
                            else
                            {
                                distriPrice="0";
                            }


                        }
                    }
                }
            }
            return distriPrice;
        }
        catch (Exception e) {
            return "0";
        }
        
    }
    
    
    
    /**
     * 3.0 校验外部接口
     * @param dao
     * @param reqStr    请求JSON
     * @param signStr   请求sign
     * @return
     * @throws Exception
     */
    public ResultDatas checkSign(DsmDAO dao, String reqStr, String signStr) throws Exception{
        
        ResultDatas rd = new ResultDatas();
        
        JSONObject signJson = new JSONObject(signStr);
        
        String userCode = signJson.getString("key");
        String sign = signJson.getString("sign");
        
        String eId = "";
        String userSignKey = "";
        StringBuffer sql =new StringBuffer( "select usercode, userName ,userkey , EID , COMPANYID , shopId,channelId, departId, "
                + " msgChannelId,  appType , useDate , endDate, useWhite , remark   "
                + " from  CRM_APIUSER "
                + " where usercode = '"+userCode+"' and STATUS='100' ");
        
        ApiUser apiUser = new ApiUser();
        String appType_def = "";
        List<Map<String, Object>> getUserDatas = dao.executeQuerySQL(sql.toString(),null);
        if(getUserDatas!= null && !getUserDatas.isEmpty())
        {
            for (Map<String, Object> map : getUserDatas)
            {
                userSignKey = map.get("USERKEY").toString();
                userCode = map.get("USERCODE").toString();
                eId = map.get("EID").toString();
                
                String userName =  map.get("USERNAME").toString();
                String companyId = map.get("COMPANYID").toString();
                String shopId = map.get("SHOPID").toString();
                String channelId = map.get("CHANNELID").toString();
                String departId = map.get("DEPARTID").toString();
                String msgChannelId = map.get("MSGCHANNELID").toString();
                String appType = map.get("APPTYPE").toString();
                String useDate = map.get("USEDATE").toString();
                String endDate = map.get("ENDDATE").toString();
                String useWhite = map.get("USEWHITE").toString();
                String remark = map.get("REMARK").toString();
                
                apiUser.setUserCode(userCode);
                apiUser.setUserName(userName);
                apiUser.setUserKey(userSignKey);
                apiUser.setCompanyId(companyId);
                apiUser.setShopId(shopId);
                apiUser.setChannelId(channelId);
                apiUser.setDepartId(departId);
                apiUser.setMsgChannelId(msgChannelId);
                apiUser.setAppType(appType);
                apiUser.setUseDate(useDate);
                apiUser.setEndDate(endDate);
                apiUser.setUseWhite(useWhite);
                apiUser.setRemark(remark);
                appType_def = appType;
                
            }
        }
        
        getUserDatas.clear();
        getUserDatas=null;
        
        // 例如会员名字等字段从微信带过来后是这样 ，编码格式为 unicode 前面处理会把这个给转义掉，这里需要把转义去下，否则会签名不一致
        if ("MINI".equals(appType_def))
        {
            if (reqStr.contains("\\u"))
            {
                reqStr = convertUnicodeToCh(reqStr);
                //reqStr =   StringEscapeUtils.unescapeJava(reqStr);
            }
        }
        //reqStr =   StringEscapeUtils.unescapeJava(reqStr);
        
        
        
        String result = PosPub.encodeMD5(reqStr + userSignKey);
        
        if(result.equals(sign))
        {
            /**
             * 根据key 去查找signKey、公司别等信息  生成之后和 sign签名 对比
             */
            rd.setApiUser(apiUser);
            rd.seteId(eId);
            rd.setStatus("SUCCESS");
            rd.setDesctiption("签名校验成功！");
        }
        else
        {
            
            //签名校验失败处理：可以拿reqStr内容跟请求的内容对比，因为这里reqStr会被URL编码，才是真正的web
            PosPub.WriteETLJOBLog("reqStr： "+ reqStr);
            PosPub.WriteETLJOBLog("result： "+ result +",sign:" +sign);
            
            rd.setApiUser(apiUser);
            rd.seteId(eId);
            rd.setStatus("FAILED");
            rd.setDesctiption("签名校验失败！");
            logger.info("\r\n*********************** 正确签名应该是：  "+result+"*****\r\n");
        }
        
        apiUser=null;
        signJson=null;
        
        
        return rd;
    }
    
    /**
     * 3.0 调用会员基础促销价计算  PROM_BasicPromotionCalc_Open
     * @param apiUserCode
     * @param userKey
     * @param langType
     * @param requestId
     * @param companyId
     * @param shopId
     * @param memberId
     * @param cardNo
     * @param crmUrl
     * @param getQPlu (必须包含PLUNO和PLUTYPE、UNITID三个字段 ，ISEXCHANGE不给值默认为0)
     * @return List<Map<String, Object>> (返回笔数与会员接口返回一致)
     *         (PLUNO,PLUTYPE,UNITID,ORIGINALPRICE,PRICE,BASICPRICE,PROMLABEL,ISOPENPRICE,
     *          ISEXCHANGE,MEMBERPRICE,ISVIPDISC,DICTYPE)
     * @throws Exception
     */
    public List<Map<String, Object>> getBasicProm(String apiUserCode,String userKey,String langType,String requestId,String companyId,String shopId,String memberId,String cardNo,String crmUrl,List<Map<String, Object>> getQPlu) throws Exception
    {
        try {
            List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
            
            //转成请求接口格式
            for (Map<String, Object> par : getQPlu) {
                String pluNo= par.get("PLUNO").toString();
                if (!Check.Null(pluNo)) {
                    Map<String, Object> promMap = new HashMap<>();
                    promMap.put("pluNo", par.get("PLUNO").toString());
                    promMap.put("pluType", par.get("PLUTYPE").toString());
                    promMap.put("unitId", par.get("UNITID").toString());
                    
                    if (par.get("ISEXCHANGE")==null) {
                        promMap.put("isExchange", "N");
                    }else {
                        promMap.put("isExchange", par.get("ISEXCHANGE").toString());
                    }
                    goodsList.add(promMap);
                    promMap=null;
                }
            }
            
            JSONObject promJson = new JSONObject();
            //门店为空时给公司     20200915 和玲霞确认
            if (!Check.Null(shopId)) {
                promJson.put("orgType", "2");
                promJson.put("orgId", shopId);
            }else{
                promJson.put("orgType", "1");
                promJson.put("orgId", companyId);
            }
            promJson.put("memberId", memberId);
            promJson.put("cardNo", cardNo);
            promJson.put("goodsList",goodsList);
            
            Map<String, Object> map = new HashMap<>();
            map.put("serviceId", "PROM_BasicPromotionCalc_Open");
            map.put("apiUserCode",apiUserCode);
            map.put("sign",PosPub.encodeMD5(promJson.toString() + userKey));
            map.put("langType",langType);
            map.put("requestId",requestId);
            map.put("timestamp",timestamp);
            map.put("version","3.0");

//            logger.info("\r\n******请求外部服务 PROM_BasicPromotionCalc_Open 调用Start******\r\n");
//            logger.info("\r\n******apiUserCode："+apiUserCode+"\r\n");
//            logger.info("\r\n******sign："+PosPub.encodeMD5(promJson.toString() + userKey)+"\r\n");
//            logger.info("\r\n******langType："+langType+"\r\n");
//            logger.info("\r\n******requestId："+requestId+"\r\n");
//            logger.info("\r\n******timestamp："+timestamp+"\r\n");
//            logger.info("\r\n******version："+"3.0"+"\r\n");
//            logger.info("\r\n******request："+promJson.toString()+"\r\n");
            
            logger.info("\r\nrequestId："+requestId+" ******PROM_BasicPromotionCalc_Open服务调用开始 ****************");
            
            
            String res = HttpSend.doPost(crmUrl,promJson.toString(),map,requestId);
            promJson=null;
            map=null;
            
            logger.info("\r\nrequestId："+requestId+" ******PROM_BasicPromotionCalc_Open服务调用返回： "+res+" ****************");
            if (res != null){
                JSONObject resJson = new JSONObject(res);
                boolean success = resJson.getBoolean("success");
                //String success = resJson.getString("success").toLowerCase(); // true/false
                if (success) {
                    JSONArray datas = resJson.getJSONArray("datas");
                    for (int i = 0; i < datas.length(); i++) {
                        Map<String, Object> resMap = new HashMap<>();
                        JSONObject obj = datas.getJSONObject(i);
                        String pluNo = obj.get("pluNo").toString();
                        String pluType = obj.get("pluType").toString();
                        String unitId = obj.get("unitId").toString();
                        String originalPrice = obj.get("originalPrice").toString();
                        String price = obj.get("price").toString();
                        String basicPrice = obj.get("basicPrice").toString();
                        String promLable = obj.get("promLable").toString();
                        String isOpenPrice = obj.get("isOpenPrice").toString();
                        String isExchange = obj.get("isExchange").toString();
                        //String memberPrice = obj.get("memberPrice").toString();
                        //String isVipDisc = obj.get("isVipDisc").toString();
                        String dicType = obj.get("dicType").toString();
                        //权益卡新增  by jinzma 20210722
                        String price_vip = obj.get("price_vip").toString();
                        String price_novip = obj.get("price_novip").toString();
                        
                        
                        resMap.put("PLUNO",pluNo);
                        resMap.put("PLUTYPE",pluType);
                        resMap.put("UNITID",unitId);
                        resMap.put("ORIGINALPRICE",originalPrice);
                        resMap.put("PRICE",price);
                        resMap.put("BASICPRICE",basicPrice);
                        resMap.put("PROMLABLE",promLable);
                        resMap.put("ISOPENPRICE",isOpenPrice);
                        resMap.put("ISEXCHANGE",isExchange);
                        //resMap.put("MEMBERPRICE",memberPrice);
                        //resMap.put("ISVIPDISC",isVipDisc);
                        resMap.put("DICTYPE",dicType);
                        resMap.put("PRICE_VIP",price_vip);
                        resMap.put("PRICE_NOVIP",price_novip);
                        
                        returnList.add(resMap);
                        
                        resMap=null;
                        obj=null;
                    }
                    datas=null;
                    resJson=null;
                    
                    return returnList;
                }else {
                    resJson=null;
                    return null;
                }
                
            }else{
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }
    
    /**
     * 发票参数Json串转Map
     * @param params
     * @return
     */
    public Map<String,String> invoiceParmsStringToMap(String params){
        Map<String,String> invoiceParmsMap = new HashMap<>();
        
        try {
            List<Map<String, Object>> paramMaps = toListMap(params);
            for (Map<String, Object> paramMap : paramMaps) {
                String param = paramMap.get("param").toString();
                String value = paramMap.get("value").toString();
                invoiceParmsMap.put(param,value);
            }
        } catch (Exception e) {
            return null;
        }
        return invoiceParmsMap;
    }
    
    /**
     * @param data 要加密的字符串
     * @param key  私钥:
     * @param iv   初始化向量参数(偏移量)
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            byte[] dataBytes = data.getBytes();
            
            //两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "DES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(dataBytes);
            
            return byteToHex(encrypted);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * byte数组转hex
     *
     * @param bytes
     * @return
     */
    public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }
    
    public static List<Map<String, Object>> toListMap(String json) {
        List<Object> list = JSON.parseArray(json);
        
        List<Map<String, Object>> listw = new ArrayList<Map<String, Object>>();
        for (Object object : list) {
            Map<String, Object> ageMap = new HashMap<String, Object>();
            Map<String, Object> ret = (Map<String, Object>) object;//取出list里面的值转为map
            listw.add(ret);
        }
        return listw;
    }
    
    /**
     * 3.0 调用商品可参与促销列表  PROM_GoodsPromQuery_Open
     * @param apiUserCode
     * @param userKey
     * @param langType
     * @param requestId
     * @param companyId
     * @param shopId
     * @param memberId
     * @param crmUrl
     * @param pluNo
     * @param pluType
     * @param unitId
     * @param promCategory
     * @param promNo
     * @return List<Map<String, Object>>
     * @throws Exception
     */
    public List<Map<String, Object>> getActivity(String apiUserCode,String userKey,String langType,String requestId,String companyId,String shopId,String memberId,String crmUrl,String pluNo,String pluType,String unitId,String promCategory,String promNo,String cardTypeId) throws Exception
    {
        try
        {
            List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
            JSONObject activityJson = new JSONObject();
            //门店为空时给公司     20200915 和玲霞确认
            if (!Check.Null(shopId))
            {
                activityJson.put("orgType", "2");
                activityJson.put("orgId", shopId);
            }
            else
            {
                activityJson.put("orgType", "1");
                activityJson.put("orgId", companyId);
            }
            activityJson.put("memberId", memberId);
            activityJson.put("queryType", "2");
            activityJson.put("pluNo",pluNo);
            activityJson.put("pluType",pluType);
            activityJson.put("unitId",unitId);
            activityJson.put("promCategory",promCategory);
            activityJson.put("promNo",promNo);
            activityJson.put("cardTypeId",cardTypeId);
            
            Map<String, Object> map = new HashMap<>();
            map.put("serviceId", "PROM_GoodsPromQuery_Open");
            map.put("apiUserCode",apiUserCode);
            map.put("sign",PosPub.encodeMD5(activityJson.toString() + userKey));
            map.put("langType",langType);
            map.put("requestId",requestId);
            map.put("timestamp",timestamp);
            map.put("version","3.0");

//            logger.info("\r\n******请求外部服务 PROM_GoodsPromQuery_Open 调用Start******\r\n");
//            logger.info("\r\n******apiUserCode："+apiUserCode+"\r\n");
//            logger.info("\r\n******sign："+PosPub.encodeMD5(activityJson.toString() + userKey)+"\r\n");
//            logger.info("\r\n******langType："+langType+"\r\n");
//            logger.info("\r\n******requestId："+requestId+"\r\n");
//            logger.info("\r\n******timestamp："+timestamp+"\r\n");
//            logger.info("\r\n******version："+"3.0"+"\r\n");
//            logger.info("\r\n******request："+activityJson.toString()+"\r\n");
            
            logger.info("\r\nrequestId："+requestId+" ******PROM_GoodsPromQuery_Open服务调用开始：  ****************");
            String res = HttpSend.doPost(crmUrl,activityJson.toString(),map,requestId);
            activityJson=null;
            map=null;
            logger.info("\r\nrequestId："+requestId+" ******PROM_GoodsPromQuery_Open服务调用返回： "+res+" ****************");
            
            if (res != null)
            {
                JSONObject resJson = new JSONObject(res);
                boolean success = resJson.getBoolean("success"); // true/false
                if (success)
                {
                    JSONArray datas = resJson.getJSONArray("datas");
                    for (int i = 0; i < datas.length(); i++)
                    {
                        Map<String,Object> returnMap = new HashMap<>();
                        JSONObject obj = datas.getJSONObject(i);
                        Iterator<String> iterator = obj.keys();
                        while(iterator.hasNext())
                        {
                            String key = (String)iterator.next();
                            Object value = obj.get(key);
                            returnMap.put(key, value);
                        }
                        returnList.add(returnMap);
                        
                        obj=null;
                        returnMap=null;
                    }
                    
                    datas=null;
                    resJson=null;
                    
                    return returnList;
                }
                else
                {
                    resJson=null;
                    
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            return null;
        }
    }
    
    public String arrayToString(String[] str)
    {
        StringBuffer str2 = new StringBuffer("");
        for (String s:str)
        {
            str2.append("'" + s + "'"+ ",");
        }
        if (str2.length()>0)
        {
            str2.deleteCharAt(str2.length()-1);
        }
        
        return str2.toString();
    }
    
    /**
     * 获取客显图片地址
     * @param eId
     * @return
     * @throws Exception
     * @return http://101.37.33.19/dcpService_3.0/dualplay/digiwin300.jpg?1611128455104
     */
    public String getDualplayImgBaseUrl(String eId) throws Exception
    {
        return getBaseUrl(eId) + "/dualplay";
    }
    public String getBaseUrl(String eId) throws Exception
    {
        String platformCentreURL = PosPub.getDCP_URL(eId);
        URI uri = URLUtil.toURI(platformCentreURL);
        String scheme = uri.getScheme();
        String domainName = uri.getHost();
        String path = StrUtil.sub(uri.getPath(), 0, uri.getPath().indexOf("/", 1));
        if (uri.getScheme().equals("http"))
        {
            String isHttps = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "ISHTTPS");
            if ("1".equals(isHttps))
            {
                scheme = "https";
            }
        }
        uri=null;
        if (Check.isIP(domainName))
        {
            String value = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "DomainName");
            if (StrUtil.isNotEmpty(value))
            {
                if (value.endsWith("/")) value = value.substring(-1);
                domainName = value;
            }
        }
        
        return scheme + "://" + domainName + path;
    }
    
    public String convertUnicodeToCh (String str) throws  Exception
    {
        try {
            Pattern pattern = Pattern.compile("(\\\\u(\\w{4}))");
            Matcher matcher = pattern.matcher(str);
            while (matcher.find())
            {
                String unicodeFull = matcher.group(1);//匹配出的每个字unicode,比如\u67e5
                String unicodeNum = matcher.group(2);//匹配出的每个字的数字部分,比如\u67e5，会匹配出67e5
                //讲16进制转10进制，转为char,就是正常的字符
                char singleChar = (char)Integer.parseInt(unicodeNum,16);
                //替换原始字符集中unicode码
                str = str.replace(unicodeFull,singleChar+"");
            }
            
        }
        catch (Exception e)
        {
            return str;
        }
        return str;
    }
    
    public String getPluno_Sale_Goods_Price(String companyNO,String shop,String withasSql_Pluno,String sDate)
    {
        String sql="";
        
        try
        {
//			sql="with p0 as ( " + withasSql_Pluno +" ), " +
//					"p1 as ( "+
//					getPluno_UnitRatio(companyNO) +" ), " +
//					"p2 as ( " +
//					getPluno_GoodsPrice(companyNO, shop,  sDate) + " ), " +
//					"p3 as ( " +
//					getPluno_SalePrice(companyNO, shop, sDate) +" ) " +
//					"select p0.pluno,p1.COMPANYNO,p1.punit,p1.wunit,p1.UNIT_RATIO,p1.sunit,p2.*,p3.* from p0 " + 
//					"inner join p1 on  p0.pluno=p1.pluno    " + 
//					"left  join p2 on  p0.pluno = p2.pluno  " + 
//					"left  join p3 on  p0.pluno = p3.pluno  " +
//					"where p0.pluno is not null ";
            
            return sql;
        }
        catch (Exception e)
        {
            return "";
        }
        finally
        {
            sql=null;
        }
    }
}
