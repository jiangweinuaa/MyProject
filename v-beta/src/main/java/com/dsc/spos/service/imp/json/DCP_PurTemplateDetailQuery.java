package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurTemplateDetailQueryReq;
import com.dsc.spos.json.cust.req.DCP_PurTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurTemplateDetailQueryRes;
import com.dsc.spos.json.cust.res.DCP_PurTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PurTemplateDetailQuery extends SPosBasicService<DCP_PurTemplateDetailQueryReq, DCP_PurTemplateDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurTemplateDetailQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PurTemplateDetailQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PurTemplateDetailQueryReq>(){};
    }

    @Override
    protected DCP_PurTemplateDetailQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PurTemplateDetailQueryRes();
    }

    @Override
    protected DCP_PurTemplateDetailQueryRes processJson(DCP_PurTemplateDetailQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_PurTemplateDetailQueryRes res = this.getResponse();
        //try
       // {

            //单头查询
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            res.setDatas(new ArrayList<DCP_PurTemplateDetailQueryRes.DataList>());
            if (getQData != null && getQData.isEmpty() == false)
            {
                Map<String, Object> oneData =getQData.get(0);
                DCP_PurTemplateDetailQueryRes.DataList oneLv1 = res.new DataList();
                oneLv1.setPurTemplateNo(oneData.get("PURTEMPLATENO").toString());
                oneLv1.setSupplier(oneData.get("SUPPLIERNO").toString());
                oneLv1.setSupplierName(oneData.get("SUPPLIER_NAME").toString());
                oneLv1.setPurType(oneData.get("PURTYPE").toString());
                oneLv1.setPurCenter(oneData.get("PURCENTER").toString());
                oneLv1.setOrgName(oneData.get("ORG_NAME").toString());
                oneLv1.setTimeType(oneData.get("TIME_TYPE").toString());
                oneLv1.setTimeValue(oneData.get("TIME_VALUE").toString());
                oneLv1.setPreDays(oneData.get("PRE_DAY").toString());
                oneLv1.setMemo(oneData.get("MEMO").toString());
                oneLv1.setStatus(oneData.get("STATUS").toString());
                oneLv1.setCreatorID(oneData.get("CREATEOPID").toString());
                oneLv1.setCreatorName(oneData.get("CREATEOPNAME").toString());
                oneLv1.setCreatorDeptID(oneData.get("CREATEDEPTID").toString());
                oneLv1.setCreatorDeptName(oneData.get("CREATEDEPTNAME").toString());
                oneLv1.setCreate_datetime(oneData.get("CREATETIME").toString());
                oneLv1.setLastmodifyID(oneData.get("LASTMODIOPID").toString());
                oneLv1.setLastmodifyName(oneData.get("LASTMODIOPNAME").toString());
                oneLv1.setLastmodify_datetime(oneData.get("LASTMODITIME").toString());
                oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
                oneLv1.setTaxName(oneData.get("TAXNAME").toString());
                oneLv1.setTaxRate(oneData.get("TAXRATE").toString());
                oneLv1.setInclTax(oneData.get("INCLTAX").toString());
                oneLv1.setBeginDate(oneData.get("BEGINDATE").toString());
                oneLv1.setEndDate(oneData.get("ENDDATE").toString());
                res.getDatas().add(oneLv1);

                oneLv1.setLang_list(new ArrayList<>());
                oneLv1.setPluList(new ArrayList<>());
                oneLv1.setOrgList(new ArrayList<>());
                String langSql=this.getLangSql(req.getRequest().getPurTemplateNo(),req.geteId());
                List<Map<String, Object>> getLangData=this.doQueryData(langSql, null);
                for (Map<String, Object> langData : getLangData)
                {
                    DCP_PurTemplateDetailQueryRes.Name_lang oneLang=res.new Name_lang();
                    oneLang.setLangType(langData.get("LANG_TYPE").toString());
                    oneLang.setName(langData.get("NAME").toString());
                    oneLv1.getLang_list().add(oneLang);
                }

                String goodSql = this.getGoodSql(req);
                List<Map<String, Object>> getGoodData=this.doQueryData(goodSql, null);
                String priceSql=this.getPriceSql(req);
                List<Map<String, Object>> getPriceData=this.doQueryData(priceSql, null);
                for (Map<String, Object> goodData : getGoodData)
                {
                    String item = goodData.get("ITEM").toString();
                    DCP_PurTemplateDetailQueryRes.Plu onePlu=res.new Plu();
                    onePlu.setItem(item);
                    onePlu.setPluno(goodData.get("PLUNO").toString());
                    onePlu.setPluName(goodData.get("PLU_NAME").toString());
                    onePlu.setPluBarcode(goodData.get("PLUBARCODE").toString());
                    onePlu.setSpec(goodData.get("SPEC").toString());
                    onePlu.setCategory(goodData.get("CATEGORY").toString());
                    onePlu.setCategoryName(goodData.get("CATEGORY_NAME").toString());
                    onePlu.setBaseUnit(goodData.get("BASEUNIT").toString());
                    onePlu.setBaseUnitName(goodData.get("BASEUNITNAME").toString());
                    onePlu.setWUnit(goodData.get("WUNIT").toString());
                    onePlu.setWUnitName(goodData.get("WUNITNAME").toString());
                    onePlu.setTaxCode(goodData.get("TAXCODE").toString());
                    onePlu.setTaxName(goodData.get("TAXNAME").toString());
                    onePlu.setTaxRate(goodData.get("TAXRATE").toString());
                    onePlu.setInclTax(goodData.get("INCLTAX").toString());
                    onePlu.setPurUnit(goodData.get("PURUNIT").toString());
                    onePlu.setPurUnitName(goodData.get("PUNITNAME").toString());

                    onePlu.setPriceType(goodData.get("PRICETYPE").toString());
                    onePlu.setBasePrice(goodData.get("PURBASEPRICE").toString());
                    onePlu.setMinRate(goodData.get("MINRATE").toString());

                    onePlu.setMaxRate(goodData.get("MAXRATE").toString());
                    onePlu.setMulPqty(goodData.get("MULPQTY").toString());
                    onePlu.setMinPqty(goodData.get("MINPQTY").toString());
                    onePlu.setStatus(goodData.get("STATUS").toString());
                    onePlu.setPriceList(new ArrayList<>());
                    for (Map<String, Object> priceData : getPriceData)
                    {
                        String priceItem = priceData.get("ITEM").toString();
                        if(item.equals(priceItem)){
                            DCP_PurTemplateDetailQueryRes.Price onePrice=res.new Price();
                            onePrice.setSeq(priceData.get("SEQ").toString());
                            onePrice.setBQty(priceData.get("BQTY").toString());
                            onePrice.setEQty(priceData.get("EQTY").toString());
                            onePrice.setPrice(priceData.get("PURPRICE").toString());
                            onePlu.getPriceList().add(onePrice);
                        }

                    }

                    oneLv1.getPluList().add(onePlu);
                }

                String orgSql = this.getOrgSql(req);
                List<Map<String, Object>> getOrgData=this.doQueryData(orgSql, null);
                for (Map<String, Object> orgData : getOrgData)
                {
                    DCP_PurTemplateDetailQueryRes.Org oneOrg=res.new Org();
                    oneOrg.setItem(orgData.get("ITEM").toString());
                    oneOrg.setOrgNo(orgData.get("ORGANIZATIONNO").toString());
                    oneOrg.setOrgName(orgData.get("ORG_NAME").toString());
                    oneOrg.setStatus(orgData.get("STATUS").toString());
                    oneLv1.getOrgList().add(oneOrg);
                }
                oneLv1.setOrgCnt(String.valueOf(oneLv1.getOrgList().size()));

            }


            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());

            return res;
        //}
        //catch (Exception e)
        //{
            // TODO: handle exception
          //  throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
       // }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_PurTemplateDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String langType = req.getLangType();
        DCP_PurTemplateDetailQueryReq.levelElm request = req.getRequest();
        String no = request.getPurTemplateNo();

        sqlbuf.append(" "
                + " select distinct a.*,c.plu_name,d.fname as supplier_name,e.org_name,f.taxcode,g.taxname,f.taxrate,f.INCLTAX,dd.departname as CREATEDEPTNAME,em1.op_name as CREATEOPNAME,em2.op_name as LASTMODIOPNAME," +
                " to_char(d.begindate,'yyyy-MM-dd') beginDate,to_char(d.enddate,'yyyy-MM-dd') as enddate " +
                "  from DCP_PURCHASETEMPLATE a"
                + " inner join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO "
                + " left join dcp_goods_lang c  on b.pluno=c.pluno and b.eid=c.eid  and c.lang_type = '"+langType+"' "
                + " left join DCP_BIZPARTNER d  on d.BIZPARTNERNO=a.SUPPLIERNO and d.eid=a.eid  "
                + " left join dcp_org_lang e on e.eid=a.eid and a.purcenter=e.organizationno  and e.lang_type = '"+langType+"' "
                + " left join PLATFORM_STAFFS_LANG em1 on em1.eid=a.eid and em1.opno=a.CREATEOPID and em1.lang_type='"+langType+"'"
                + " left join PLATFORM_STAFFS_LANG em2 on em2.eid=a.eid and em2.opno=a.LASTMODIOPID and em2.lang_type='"+langType+"'"
                + " left join dcp_department_lang dd on dd.eid=a.eid and dd.departno=a.createdeptid and dd.lang_type='"+req.getLangType()+"'  "
                + " left join DCP_TAXCATEGORY f on f.eid=d.eid and f.taxcode=d.taxcode "
                + " left join DCP_TAXCATEGORY_lang g on g.eid=d.eid and g.taxcode=d.taxcode and g.lang_type='"+req.getLangType()+"' "
                + " where  a.eid= '"+ eId +"' and a.PURTEMPLATENO='"+no+"' "
                + " "
                + " ");

        return sqlbuf.toString();
    }

    private String getLangSql(String no,String eid){
        String sql="select * from DCP_PURCHASETEMPLATE_LANG where eid='"+eid+"'" +
                " and PURTEMPLATENO = '"+no+"'";
        return sql;
    }


    private String getGoodSql(DCP_PurTemplateDetailQueryReq req){
        String sql="select distinct a.*,c.plu_name,b.spec,b.category,b.MAINBARCODE as plubarcode,e.category_name,b.BASEUNIT,base.uname as baseunitname," +
                " b.wunit,wunit.uname as wunitname,a.taxcode,m.TAXNAME,l.taxrate,l.INCLTAX,punit.uname as punitname " +
                " from DCP_PURCHASETEMPLATE_GOODS a" +
                " left join dcp_goods b on b.eid=a.eid and  b.pluno=a.pluno  " +
                " left join dcp_goods_lang c on b.eid=c.eid and  b.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' " +
                " left join DCP_category d on d.eid=b.eid and d.category=b.category "+
                " LEFT JOIN DCP_CATEGORY_LANG e ON a.EID=e.EID AND d.CATEGORY=e.CATEGORY  AND e.LANG_TYPE='"+req.getLangType()+"' "+
                " LEFT JOIN DCP_UNIT_LANG  base ON b.EID=base.EID AND b.BASEUNIT=base.UNIT  AND base.LANG_TYPE='"+req.getLangType()+"' "+
                " LEFT JOIN DCP_UNIT_LANG  punit ON b.EID=punit.EID AND a.purunit=punit.UNIT AND punit.LANG_TYPE='"+req.getLangType()+"' "+
                " LEFT JOIN DCP_UNIT_LANG  wunit ON b.EID=wunit.EID AND b.WUNIT=wunit.UNIT AND wunit.LANG_TYPE='"+req.getLangType()+"' "+
                " left join DCP_TAXCATEGORY l on l.eid=a.eid and a.taxcode=l.taxcode "+
                " LEFT JOIN DCP_TaxCategory_LANG m on a.EID=m.EID  and a.taxCode=m.taxCode  AND m.LANG_TYPE='"+req.getLangType()+"' "+
                "  where a.eid='"+req.geteId()+"'" +
                " and a.PURTEMPLATENO ='"+req.getRequest().getPurTemplateNo()+"' " +
                " order by a.item ";
        return sql;
    }

    private String getPriceSql(DCP_PurTemplateDetailQueryReq req){
        String sql="select * from DCP_PURCHASETEMPLATE_PRICE where eid='"+req.geteId()+"'" +
                " and PURTEMPLATENO='"+req.getRequest().getPurTemplateNo()+"'" +
                " order by item ";
        return sql;
    }
    private String getOrgSql(DCP_PurTemplateDetailQueryReq req){
        String sql="select a.*,b.org_name from DCP_PURCHASETEMPLATE_ORG a " +
                " left join dcp_org_lang b on b.eid=a.eid and a.ORGANIZATIONNO=b.organizationno  and b.lang_type = '"+req.getLangType()+"' "+
                " where a.eid='"+req.geteId()+"'" +
                " and a.PURTEMPLATENO ='"+req.getRequest().getPurTemplateNo()+"'  " +
                " order by a.item ";
        return sql;
    }


}
