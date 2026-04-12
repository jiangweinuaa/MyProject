package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.dsc.spos.json.cust.req.DCP_GoodsUnitQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsUnitQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsUnitQuery extends SPosBasicService<DCP_GoodsUnitQueryReq, DCP_GoodsUnitQueryRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_GoodsUnitQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    protected TypeToken<DCP_GoodsUnitQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_GoodsUnitQueryReq>(){};
    }
    
    @Override
    protected DCP_GoodsUnitQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_GoodsUnitQueryRes();
    }
    
    @Override
    protected DCP_GoodsUnitQueryRes processJson(DCP_GoodsUnitQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_GoodsUnitQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<DCP_GoodsUnitQueryRes.level1Elm>());
        String eId = req.geteId();
        String pluNo = req.getRequest().getPluNo();
        int totalRecords;		//总笔数
        int totalPages;
        
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if(getQData!=null&&getQData.isEmpty()==false)
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            String baseUnit = getQData.get(0).get("BASEUNIT").toString();
            String baseUnitName = getQData.get(0).get("BASEUNIT_NAME").toString();
            String sUnit = getQData.get(0).get("SUNIT").toString();
            String sPrice = getQData.get(0).get("PRICE").toString();
            
            BigDecimal sPrice_b = new BigDecimal("0");
            try
            {
                sPrice_b = new BigDecimal(sPrice);
                
            }
            catch (Exception e)
            {
                // TODO: handle exception
            }
            BigDecimal baseUnitRito = new BigDecimal("0");
            if(baseUnit.equals(sUnit))//销售单位=基准单位
            {
                baseUnitRito = new BigDecimal("1");
            }
            else
            {
                for (Map<String, Object> map : getQData)
                {
                    if(sUnit!=null&&sUnit.isEmpty()==false&&sUnit.equals("OUNIT"))
                    {
                        String oQty = map.get("OQTY").toString();
                        String qty = map.get("QTY").toString();
                        
                        BigDecimal oQty_b = new BigDecimal(oQty);
                        BigDecimal qty_b = new BigDecimal(qty);
                        if(qty_b.compareTo(BigDecimal.ZERO)==0)
                        {
                            break;
                        }
                        baseUnitRito = oQty_b.divide(qty_b,6,BigDecimal.ROUND_HALF_UP);
                        break;
                    }
                }
            }
            
            
            String pUnit = getQData.get(0).get("PUNIT").toString();//默认要货单位
            String supPrice = getQData.get(0).get("SUPPRICE").toString();//标准进货价(默认要货单位)
            
            BigDecimal supPrice_b = new BigDecimal("0");
            try
            {
                supPrice_b = new BigDecimal(supPrice);
                
            }
            catch (Exception e)
            {
                // TODO: handle exception
            }
            BigDecimal baseUnitRito_punit = new BigDecimal("0");
            
            if(baseUnit.equals(pUnit))//要货单位==基准单位
            {
                baseUnitRito_punit = new BigDecimal("1");
            }
            else
            {
                for (Map<String, Object> map : getQData)
                {
                    if(pUnit!=null&&pUnit.isEmpty()==false&&pUnit.equals("OUNIT"))
                    {
                        String oQty = map.get("OQTY").toString();
                        String qty = map.get("QTY").toString();
                        
                        BigDecimal oQty_b = new BigDecimal(oQty);
                        BigDecimal qty_b = new BigDecimal(qty);
                        if(qty_b.compareTo(BigDecimal.ZERO)==0)
                        {
                            break;
                        }
                        baseUnitRito_punit = oQty_b.divide(qty_b,6,BigDecimal.ROUND_HALF_UP);
                        break;
                    }
                }
                
            }
            
            
            
            for (Map<String, Object> map : getQData)
            {
                DCP_GoodsUnitQueryRes.level1Elm oneLv1 = res.new level1Elm();
                
                
                oneLv1.setBaseUnit(baseUnit);
                oneLv1.setBaseUnitName(baseUnitName);
                oneLv1.setUnit(map.get("OUNIT").toString());
                oneLv1.setUnitName(map.get("OUNIT_NAME").toString());
                oneLv1.setUdLength(map.get("UDLENGTH").toString());
                oneLv1.setUnitRatio(map.get("UNITRATIO").toString());
                oneLv1.setBaseUnitUdLength(map.get("BASEUNITUDLENGTH").toString());
                oneLv1.setSpec(map.get("SPEC").toString());
                
                BigDecimal oPrice = sPrice_b.multiply(baseUnitRito);
                oneLv1.setPrice(oPrice.toString());
                
                BigDecimal oPrice_sup = supPrice_b.multiply(baseUnitRito_punit);
                oneLv1.setSupPrice(oPrice_sup.toString());
                
                res.getDatas().add(oneLv1);
                
            }
            
            
        }
        else{
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        return res;
        
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected String getQuerySql(DCP_GoodsUnitQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String langType = req.getLangType();
        if(langType==null||langType.isEmpty())
        {
            langType = "zh_CN";
        }
        String sUnitUse = null;//req.getRequest().getsUnitUse();//是否用于销售N-否Y-是
        String pUnitUse=  null;//req.getRequest().getpUnitUse();//是否用于要货N-否Y-是
        String bomUnitUse=  null;//req.getRequest().getBomUnitUse();//是否用于配方N-否Y-是
        String prodUnitUse=  null;//req.getRequest().getProdUnitUse();//是否用于生产N-否Y-是
        String purUnitUse=  null;//req.getRequest().getPurUnitUse();//是否用于采购N-否Y-是
        String cUnitUse=  null;//req.getRequest().getcUnitUse();//是否用于盘点N-否Y-是
        String pluNo =  req.getRequest().getPluNo();
        String unitType = req.getRequest().getUnitType();

        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料


        if(unitType!=null)//单位类型：SUNIT-销售单位；PUNIT-要货单位；PURUNIT-采购单位；BOMUNIT-配方单位；PRODUNIT-生产单位 ;CUNIT-盘点单位
        {
            switch (unitType)
            {
                case "SUNIT":
                    sUnitUse = "Y";
                    break;
                case "PUNIT":
                    pUnitUse = "Y";
                    break;
                case "PURUNIT":
                    purUnitUse = "Y";
                    break;
                case "BOMUNIT":
                    bomUnitUse = "Y";
                    break;
                case "PRODUNIT":
                    prodUnitUse = "Y";
                    break;
                case "CUNIT":
                    cUnitUse = "Y";
                    break;
                default:
                    break;
                
            }
        }
        
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" select * from (");
        sqlbuf.append("select count(*) over () num,rownum rn,a.* from ( ");
        
        sqlbuf.append(" select a.*,");
        sqlbuf.append(" b.baseunit,b.sunit,b.price,b.punit,b.SUPPRICE,al.uname as OUNIT_NAME,bl.uname as BASEUNIT_NAME,u.UDLENGTH, ");
        sqlbuf.append(" bul.udlength as baseunitudlength,gul.spec ");
        sqlbuf.append(" from dcp_goods_unit a ");
        sqlbuf.append(" left join dcp_goods b on  a.eid = b.eid and a.pluno=B.Pluno");
        sqlbuf.append(" left join dcp_unit_lang al on a.eid=al.eid and a.ounit=al.unit and al.lang_type='"+langType+"' ");
        sqlbuf.append(" left join dcp_unit_lang bl on a.eid=bl.eid and b.baseunit=bl.unit and bl.lang_type='"+langType+"' ");
        sqlbuf.append(" left join dcp_unit u on a.eid=u.eid and a.ounit=u.unit ");
        //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
        sqlbuf.append(" left join dcp_unit bul on a.eid = bul.eid and b.baseunit = bul.unit");
        sqlbuf.append(" left join DCP_GOODS_UNIT_LANG gul on gul.eid=a.eid and gul.pluno=a.pluno and gul.ounit=a.ounit and gul.lang_type='"+langType+"' ");
        sqlbuf.append(" where a.eid='"+req.geteId()+"' and a.pluno='"+pluNo+"' and a.unit=b.baseunit");
        if(sUnitUse!=null && sUnitUse.isEmpty()==false)
        {
            sqlbuf.append(" and SUNIT_USE='"+sUnitUse+"' ");
        }
        
        if(pUnitUse!=null && pUnitUse.isEmpty()==false)
        {
            sqlbuf.append(" and PUNIT_USE='"+pUnitUse+"' ");
        }
        
        if(bomUnitUse!=null && bomUnitUse.isEmpty()==false)
        {
            sqlbuf.append(" and BOM_UNIT_USE='"+bomUnitUse+"' ");
        }
        
        if(prodUnitUse!=null&&prodUnitUse.isEmpty()==false)
        {
            sqlbuf.append(" and PROD_UNIT_USE='"+prodUnitUse+"' ");
        }
        
        if(purUnitUse!=null&&purUnitUse.isEmpty()==false)
        {
            sqlbuf.append(" and PURUNIT_USE='"+purUnitUse+"' ");
        }
        
        if(cUnitUse!=null&&cUnitUse.isEmpty()==false)
        {
            sqlbuf.append(" and CUNIT_USE='"+cUnitUse+"' ");
        }
        
        
        sqlbuf.append(") a ");
        sqlbuf.append(    " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  ");
        sql = sqlbuf.toString();
        
        return sql;
        
        
        
    }
    
    
    
}
