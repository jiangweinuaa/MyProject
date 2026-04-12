package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComStaffQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComStaffQueryRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComStaffQueryRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ISVWeComStaffQueryRes.Shop;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_ISVWeComStaffQuery
 * 服务说明：查询企微员工列表
 * @author jinzma
 * @since  2023-09-13
 */
public class DCP_ISVWeComStaffQuery extends SPosBasicService<DCP_ISVWeComStaffQueryReq, DCP_ISVWeComStaffQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComStaffQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ISVWeComStaffQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComStaffQueryReq>(){};
    }
    
    @Override
    protected DCP_ISVWeComStaffQueryRes getResponseType() {
        return new DCP_ISVWeComStaffQueryRes();
    }
    
    @Override
    protected DCP_ISVWeComStaffQueryRes processJson(DCP_ISVWeComStaffQueryReq req) throws Exception {
        DCP_ISVWeComStaffQueryRes res = this.getResponse();
        List<level1Elm> datas = new ArrayList<>();
        try{
            String eId = req.geteId();
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            getQData = getQData.stream().distinct().collect(Collectors.toList());
            int totalRecords=0;								//总笔数
            int totalPages=0;									//总页数
            if (!CollectionUtils.isEmpty(getQData)){
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                for (Map<String, Object> oneData : getQData) {
                    level1Elm oneLv1 = res.new level1Elm();
                    String userId = oneData.get("USERID").toString();
                    oneLv1.setOpNo(oneData.get("OPNO").toString());
                    oneLv1.setOpName(oneData.get("OPNAME").toString());
                    oneLv1.setUserId(userId);
                    oneLv1.setTelephone(oneData.get("TELEPHONE").toString());
                    oneLv1.setAccountType(oneData.get("ACCOUNTTYPE").toString());
                    oneLv1.setActiveTime(oneData.get("ACTIVETIME").toString());
                    oneLv1.setExpireTime(oneData.get("EXPIRETIME").toString());

                    oneLv1.setShopList(new ArrayList<>());
                    sql = " select a.shopid,b.org_name from dcp_isvwecom_staffs_shop a"
                            + " left join dcp_org_lang b on a.eid=b.eid and a.shopid=b.organizationno and b.lang_type='zh_CN'"
                            + " where a.eid='"+eId+"' and a.userid='"+userId+"'"
                            + " order by a.shopid";
                    List<Map<String, Object>> getUserQData=this.doQueryData(sql, null);
                    for (Map<String, Object> userData : getUserQData) {
                        Shop shop = res.new Shop();
                        shop.setShopId(userData.get("SHOPID").toString());
                        shop.setShopName(userData.get("ORG_NAME").toString());

                        oneLv1.getShopList().add(shop);
                    }

                    datas.add(oneLv1);
                }
            }
            
            res.setDatas(datas);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            return res;
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_ISVWeComStaffQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String accountType = req.getRequest().getAccountType();
        
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * req.getPageSize();
        
        sqlbuf.append(" select * from ("
                + " select count(distinct a.opno) over() num,"
                + " dense_rank() over (order by a.opno asc) rn,"
                + " a.opno,a.opname,a.telephone,a.userid,a.status,a.accounttype,"
                + " to_char(a.activetime,'yyyy-mm-dd hh24:mi:ss') as activetime,"
                + " to_char(a.expiretime,'yyyy-mm-dd hh24:mi:ss') as expiretime"
                + " from dcp_isvwecom_staffs a"
                + " left join dcp_isvwecom_staffs_shop b on a.eid=b.eid and a.userid=b.userid"
                + " left join dcp_org_lang b1 on a.eid=b1.eid and b.shopid=b1.organizationno and b1.lang_type='zh_CN'"
                + " where a.eid='"+eId+"'");

        if (!Check.Null(keyTxt)){
            sqlbuf.append(" and (a.opno like '%"+keyTxt+"%' or a.opname like '%"+keyTxt+"%' "
                    + " or a.telephone like '%"+keyTxt+"%' or b.shopid like '%"+keyTxt+"%' "
                    + " or b1.org_name like '%"+keyTxt+"%' ) ");
        }
        if (!Check.Null(accountType)){
            sqlbuf.append(" and a.accounttype='"+accountType+"'");
        }
        sqlbuf.append(" )a ");
        sqlbuf.append(" where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize));
        sqlbuf.append(" order by a.rn ");
        
        return sqlbuf.toString();
    }
}
