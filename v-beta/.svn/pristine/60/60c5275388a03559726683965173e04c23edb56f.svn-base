package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

public class DCP_SalePriceTemplateQuery extends SPosBasicService<DCP_SalePriceTemplateQueryReq,DCP_SalePriceTemplateQueryRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_SalePriceTemplateQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_SalePriceTemplateQueryReq> getRequestType() {
        return new TypeToken<DCP_SalePriceTemplateQueryReq>() {};
    }
    
    @Override
    protected DCP_SalePriceTemplateQueryRes getResponseType() {
        return new DCP_SalePriceTemplateQueryRes();
    }
    
    @Override
    protected DCP_SalePriceTemplateQueryRes processJson(DCP_SalePriceTemplateQueryReq req) throws Exception {
        DCP_SalePriceTemplateQueryRes res=this.getResponse();
        String sql="";
        String companyId="";
        String shopId = req.getRequest().getShopId();
        String isSelfBuiltTemplate="0";  //是否存在门店自建模板(0没有，1有)
        
        if (!Check.Null(shopId)) {
            sql = " select a.belfirm,b.selfbuiltshopid from dcp_org a"
                    + " left  join dcp_salepricetemplate b on a.eid=b.eid and a.organizationno=b.selfbuiltshopid"
                    + " where a.eid='"+req.geteId()+"' and a.organizationno='"+shopId+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData!=null && !getQData.isEmpty()){
                companyId = getQData.get(0).get("BELFIRM").toString();
                if (!Check.Null(getQData.get(0).get("SELFBUILTSHOPID").toString())){
                    isSelfBuiltTemplate="1";
                }
            }
        }
        
        int totalRecords = 0; //总笔数
        int totalPages = 0;
        
        sql=getQueryTemplateSql(req,companyId);
        List<Map<String , Object>> getData=this.doQueryData(sql, null);
        
        res.setDatas(new ArrayList<DCP_SalePriceTemplateQueryRes.level1Elm>());
        
        if (getData!=null && getData.isEmpty()==false) {
            String num = getData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            
            
            String sJoinA="";
            String sJoinB="";
            for (Map<String, Object> mapAll : getData) {
                sJoinA+=mapAll.get("EID").toString()+",";
                sJoinB+=mapAll.get("TEMPLATEID").toString()+",";
            }
            
            Map<String, String> map=new HashMap<String, String>();
            
            map.put("EID", sJoinA);
            map.put("TEMPLATEID", sJoinB);
            
            MyCommon cm=new MyCommon();
            String withasSql_Pluno=cm.getFormatSourceMultiColWith(map);
            map.clear();
            map=null;
            
            
            String sqlRange="with p1 as ("+ withasSql_Pluno +") "
                    + "select A.*,NVL(C.ORG_NAME,a.name) AS ORGNAME from DCP_SALEPRICETEMPLATE_RANGE a inner join p1 "
                    + " on a.eid=p1.eid and a.templateid=p1.templateid "
                    + " LEFT JOIN DCP_ORG_LANG C ON A.EID=C.EID AND A.ID=C.ORGANIZATIONNO AND C.LANG_TYPE='"+req.getLangType()+"' ";
            
            List<Map<String , Object>> getDataRange=this.doQueryData(sqlRange, null);
            
            String sqlLang="with p1 as ("+ withasSql_Pluno +") "
                    + "select * from DCP_SALEPRICETEMPLATE_LANG a inner join p1 "
                    + "on a.eid=p1.eid and a.templateid=p1.templateid ";
            
            List<Map<String , Object>> getDataLang=this.doQueryData(sqlLang, null);
            
            for (Map<String, Object> oneData : getData) {
                DCP_SalePriceTemplateQueryRes.level1Elm lv1=res.new level1Elm();
                lv1.setCreateopid(oneData.get("CREATEOPID").toString());
                lv1.setCreateopname(oneData.get("CREATEOPNAME").toString());
                lv1.setCreatetime(oneData.get("CREATETIME").toString());
                lv1.setLastmodiname(oneData.get("LASTMODIOPNAME").toString());
                lv1.setLastmodiopid(oneData.get("LASTMODIOPID").toString());
                lv1.setLastmoditime(oneData.get("LASTMODITIME").toString());
                lv1.setMemo(oneData.get("MEMO").toString());
                lv1.setRestrictChannel(oneData.get("RESTRICTCHANNEL").toString());
                lv1.setStatus(oneData.get("STATUS").toString());
                lv1.setTemplateId(oneData.get("TEMPLATEID").toString());
                lv1.setTemplateName(oneData.get("TEMPLATENAME").toString());
                lv1.setTemplateType(oneData.get("TEMPLATETYPE").toString());
                lv1.setChannelId(oneData.get("CHANNELID").toString());
                lv1.setChannelName(oneData.get("CHANNELNAME").toString());
                lv1.setRedisUpdateSuccess(oneData.get("REDISUPDATESUCCESS").toString());
                lv1.setSelfBuiltShopId(oneData.get("SELFBUILTSHOPID").toString());
                
                Map<String, Object> condiV=new HashMap<String, Object>();
                condiV.put("TEMPLATEID", oneData.get("TEMPLATEID").toString());
                List<Map<String, Object>> rangeList= MapDistinct.getWhereMap(getDataRange, condiV, true);
                condiV=null;
                
                lv1.setRangeList(new ArrayList<DCP_SalePriceTemplateQueryRes.range>());
                for (Map<String, Object> map2 : rangeList) {
                    DCP_SalePriceTemplateQueryRes.range rg=res.new range();
                    rg.setId(map2.get("ID").toString());
                    rg.setName(map2.get("ORGNAME").toString());
                    
                    lv1.getRangeList().add(rg);
                    rg=null;
                }
                
                
                condiV=new HashMap<String, Object>();
                condiV.put("TEMPLATEID", oneData.get("TEMPLATEID").toString());
                List<Map<String, Object>> langList= MapDistinct.getWhereMap(getDataLang, condiV, true);
                condiV=null;
                lv1.setTemplateName_lang(new ArrayList<DCP_SalePriceTemplateQueryRes.levelTemplate>());
                for (Map<String, Object> map2 : langList) {
                    DCP_SalePriceTemplateQueryRes.levelTemplate lang=res.new levelTemplate();
                    lang.setLangType(map2.get("LANG_TYPE").toString());
                    lang.setName(map2.get("TEMPLATENAME").toString());
                    
                    lv1.getTemplateName_lang().add(lang);
                    lang=null;
                }
                
                
                res.getDatas().add(lv1);
                lv1=null;
            }
        }
        
        res.setIsSelfBuiltTemplate(isSelfBuiltTemplate);
        
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
        
        return res;
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_SalePriceTemplateQueryReq req) throws Exception {
        return null;
    }
    
    private String getQueryTemplateSql(DCP_SalePriceTemplateQueryReq req,String companyId) throws Exception {
        String eId = req.geteId();
        String langtype = req.getLangType();
        String ketTxt = req.getRequest().getKeyTxt();
        String channelId = req.getRequest().getChannelId();
        String shopId = req.getRequest().getShopId();
        String status = req.getRequest().getStatus();
        String redisUpdateSuccess = req.getRequest().getRedisUpdateSuccess();
        //searchScope：0、全部 1、总部和当前自建门店 2、仅总部 3、全部自建门店 4、仅当前自建门店  by jinzma 20220310
        String searchScope = req.getRequest().getSearchScope();
        if (Check.Null(searchScope)){
            searchScope="0";
        }
        String selfBuiltShopId =req.getRequest().getSelfBuiltShopId();
        
        StringBuffer sqlbuf=new StringBuffer();
        
        //計算起啟位置
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;
        
        sqlbuf.append(""
                + " select * from ("
                + " select count(*) over() num,row_number() over (order by a.selfbuiltshopid desc,a.templateid) as rn,"
                + " a.eid,a.templateid,b.templatename,a.templatetype,a.restrictchannel,"
                + " a.channelid,c.channelname,a.memo,a.status,a.redisupdatesuccess,a.selfBuiltShopId,"
                + " to_char(a.createtime,'yyyy-MM-dd HH24:mi:ss') createtime,a.createopid,a.createopname,"
                + " to_char(a.lastmoditime,'yyyy-MM-dd HH24:mi:ss') lastmoditime,a.lastmodiopid,a.lastmodiopname "
                + " from dcp_salepricetemplate a "
                + " left join dcp_salepricetemplate_lang b on a.eid=b.eid and a.templateid=b.templateid and b.lang_type='"+langtype+"' "
                + " left join crm_channel c on a.eid=c.eid and a.channelid=c.channelid "
                + " left join dcp_salepricetemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.rangetype='1' and c1.id='"+companyId+"'"
                + " left join dcp_salepricetemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.rangetype='2' and c2.id='"+shopId+"'"
                + " where a.eid='"+eId+"' ");
    
        //searchScope by jinzma 20220310
        switch (searchScope){
            case "0":    //0、全部
                break;
            case "1":    //1、总部和当前自建门店
                sqlbuf.append(" and (a.selfbuiltshopid is null or a.selfbuiltshopid = '"+selfBuiltShopId+"') ");
                break;
            case "2":    //2、仅总部
                sqlbuf.append(" and a.selfbuiltshopid is null");
                break;
            case "3":    //3、全部自建门店
                sqlbuf.append(" and a.selfbuiltshopid is not null");
                break;
            case "4":    //4、仅当前自建门店
                sqlbuf.append(" and a.selfbuiltshopid = '"+selfBuiltShopId+"'");
                break;
        }
        
        if(status != null && status.length() >0) {
            sqlbuf.append(" and a.status="+status +" ");
        }
        
        if(redisUpdateSuccess != null && redisUpdateSuccess.length() >0) {
            sqlbuf.append(" and a.redisupdatesuccess='"+redisUpdateSuccess +"' ");
        }
        
        if(ketTxt != null && ketTxt.length() >0) {
            sqlbuf.append(" and (b.templateid like '%%"+ketTxt+"%%' or b.templatename like '%%"+ketTxt+"%%') ");
        }
        
        if(channelId != null && channelId.length() >0) {
            sqlbuf.append(" and (a.channelid="+channelId +" or a.restrictchannel=0) ");
        }
        
        //适用门店
        if(shopId != null && shopId.length() >0) {
            sqlbuf.append( " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null)) ");
        }
        
        sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
        
        return sqlbuf.toString();
    }
    
}
