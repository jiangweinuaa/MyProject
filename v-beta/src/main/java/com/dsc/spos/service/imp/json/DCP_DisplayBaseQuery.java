package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DisplayBaseQueryReq;
import com.dsc.spos.json.cust.res.DCP_DisplayBaseQueryRes;
import com.dsc.spos.json.cust.res.DCP_DisplayBaseQueryRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DisplayBaseQueryRes.level2Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务名称：DCP_DisplayBaseQuery
 * 服务说明：客显基础资料查询
 * @author jinzma
 * @since  2022-04-26
 */
public class DCP_DisplayBaseQuery extends SPosBasicService<DCP_DisplayBaseQueryReq,DCP_DisplayBaseQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_DisplayBaseQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if(req.getRequest() == null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_DisplayBaseQueryReq> getRequestType() {
        return new TypeToken<DCP_DisplayBaseQueryReq>(){};
    }
    
    @Override
    protected DCP_DisplayBaseQueryRes getResponseType() {
        return new DCP_DisplayBaseQueryRes();
    }
    
    @Override
    protected DCP_DisplayBaseQueryRes processJson(DCP_DisplayBaseQueryReq req) throws Exception {
        try{
            DCP_DisplayBaseQueryRes res = this.getResponse();
            String sql = getQuerySql(req);
            List<Map<String,Object>> getQData = this.doQueryData(sql,null);
            int totalRecords = 0;	//总笔数
            int totalPages = 0;		//总页数
            res.setDatas(new ArrayList<level1Elm>());
            if (getQData!=null && !getQData.isEmpty()){
                //商品图片
                String ISHTTPS = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String DomainName = PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (DomainName.endsWith("/")) {
                    DomainName = DomainName+"resource/image/";
                }else{
                    DomainName = DomainName+"/resource/image/";
                }
                if (!Check.Null(ISHTTPS) && ISHTTPS.equals("1")){
                    DomainName = "https://" + DomainName;
                }else {
                    DomainName = "http://" + DomainName;
                }
                
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                
                //单头主键字段
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
                condition.put("TEMPLATENO", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader= MapDistinct.getMap(getQData, condition);
                
                for (Map<String, Object> oneData : getQHeader) {
                    level1Elm oneLv1 = res.new level1Elm();
                    String templateNo = oneData.get("TEMPLATENO").toString();
                    oneLv1.setTemplateNo(templateNo);
                    oneLv1.setTemplateName(oneData.get("TEMPLATENAME").toString());
                    oneLv1.setShopType(oneData.get("SHOPTYPE").toString());
                    oneLv1.setStatus(oneData.get("STATUS").toString());
                    oneLv1.setLogoId(oneData.get("LOGOID").toString());
                    if(!Check.Null(oneData.get("LOGOID").toString())){
                        oneLv1.setLogoUrl(DomainName+oneData.get("LOGOID").toString());
                    }else{
                        oneLv1.setLogoUrl("");
                    }
                    oneLv1.setWelcomeWords(oneData.get("WELCOMEWORDS").toString());
                    oneLv1.setBackgroundColor(oneData.get("BACKGROUNDCOLOR").toString());
                    oneLv1.setWelcomeWordColor(oneData.get("WELCOMEWORDCOLOR").toString());
                    oneLv1.setCreateOpId(oneData.get("CREATEOPID").toString());
                    oneLv1.setCreateOpName(oneData.get("CREATEOPNAME").toString());
                    oneLv1.setCreateTime(oneData.get("CREATETIME").toString());
                    oneLv1.setLastModiOpId(oneData.get("LASTMODIOPID").toString());
                    oneLv1.setLastModiOpName(oneData.get("LASTMODIOPNAME").toString());
                    oneLv1.setLastModiTime(oneData.get("LASTMODITIME").toString());
                    oneLv1.setShopList(new ArrayList<level2Elm>());
                    for (Map<String, Object> oneData_shop : getQData){
                        if (templateNo.equals(oneData_shop.get("TEMPLATENO").toString())){
                            if (!Check.Null(oneData_shop.get("SHOPID").toString())) {
                                level2Elm oneLv2 = res.new level2Elm();
                                oneLv2.setShopId(oneData_shop.get("SHOPID").toString());
                                oneLv2.setShopName(oneData_shop.get("ORG_NAME").toString());
                                oneLv1.getShopList().add(oneLv2);
                            }
                        }
                    }
                    res.getDatas().add(oneLv1);
                }
            }
            
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            return res;
            
        }catch(Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_DisplayBaseQueryReq req) throws Exception {
        
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        String status = req.getRequest().getStatus();
        StringBuffer sqlbuf = new StringBuffer();
        
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;
        
        sqlbuf.append(""
                + " select * from ("
                + " select count(distinct a.templateno) over() num,dense_rank() over (order by a.templateno) rn,"
                + " a.templateno,a.templatename,a.shoptype,a.status,"
                + " a.createopid,a.createopname,to_char(a.createtime,'YYYY-MM-DD hh24:mi:ss') as createtime,"
                + " a.lastmodiopid,a.lastmodiopname,to_char(a.lastmoditime,'YYYY-MM-DD hh24:mi:ss') as lastmoditime,"
                + " a.logoid,a.welcomewords,a.welcomewordcolor,a.backgroundcolor,"
                + " b.shopid,c.org_name from dcp_dualplay_baseinfo a"
                + " left join dcp_dualplay_base_shop b on a.eid=b.eid and a.templateno=b.templateno"
                + " left join dcp_org_lang c on b.eid=c.eid and b.shopid=c.organizationno and c.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"'"
                + " ");
        
        if (!Check.Null(keyTxt)){
            sqlbuf.append(""
                    + " and (a.templateno like '%"+keyTxt+"%' or a.templatename like '%"+keyTxt+"%' "
                    + " or a.logoid like '%"+keyTxt+"%' or a.welcomewords like '%"+keyTxt+"%' "
                    + " or a.welcomewordcolor like '%"+keyTxt+"%' or a.backgroundcolor like '%"+keyTxt+"%' "
                    + " or b.shopid like '%"+keyTxt+"%' or c.org_name like '%"+keyTxt+"%')"
                    + " ");
        }
        
        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }
        sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
        
        return sqlbuf.toString();
        
    }
    
}
