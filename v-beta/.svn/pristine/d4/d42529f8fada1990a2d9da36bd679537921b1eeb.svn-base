package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_PrintRPQueryReq;
import com.dsc.spos.json.cust.res.DCP_PrintRPQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PrintRPQuery  extends SPosBasicService<DCP_PrintRPQueryReq, DCP_PrintRPQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_PrintRPQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_PrintRPQueryReq> getRequestType() {
        return new TypeToken<DCP_PrintRPQueryReq>(){};
    }

    @Override
    protected DCP_PrintRPQueryRes getResponseType() {
        return new DCP_PrintRPQueryRes();
    }

    @Override
    protected DCP_PrintRPQueryRes processJson(DCP_PrintRPQueryReq req) throws Exception {
        DCP_PrintRPQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        String eId = req.geteId();
        String modularNo = req.getRequest().getModularNo();
        String queryType = req.getRequest().getQueryType();
        List<String> customer = req.getRequest().getCustomer();

        List<String> customerMap =CollUtil.isEmpty( customer)?new ArrayList<>(): customer.stream().map(x -> "'" + x + "'").distinct().collect(Collectors.toList());
        String customerSql = CollUtil.isEmpty(customerMap) ? " and 1=2 " : " and (d.customer in (" + CollUtil.join(customerMap, ",") + "))";
        String sql="";
        if("0".equals(queryType)){
            sql="select distinct a.* from DCP_MODULAR_PRINT a " +
                    " left join DCP_MODULAR_PRINT_USER  b on a.eid=b.eid and a.modularno=b.modularno and a.printno=b.printno " +
                    " left join DCP_MODULAR_PRINT_org c on c.eid=a.eid and c.modularno=a.modularno and c.printno=a.printno " +
                    " left join DCP_MODULAR_PRINT_customer d on d.eid=a.eid and d.modularno=a.modularno and d.printno=a.printno " +
                    " " +
                    " where a.eid='"+eId+"' and a.modularno='"+modularNo+"' " +
                    " and (a.RESTRICTOP='0'  or (a.RESTRICTOP='1' and b.USERID='"+req.getOpNO()+"'))" +
                    " and (a.RESTRICTORG='0' or (a.RESTRICTORG='1' and c.organizationno='"+req.getOrganizationNO()+"')) " +
                    " and (a.RESTRICTCUST='0' or (a.RESTRICTCUST='1' "+customerSql+" ) )" +
                    " ";
            sql+=" and a.status='100' ";
            sql+=" order by a.PRINTNO asc ";

        }
        else if ("1".equals(queryType)){
            sql="select * from DCP_MODULAR_PRINT a where a.eid='"+eId+"' and a.modularno='"+modularNo+"'" +
                    " AND a.ISdefault='Y'";
            sql+=" and a.status='100' ";
            sql+=" order by a.printno asc ";
        }else if ("2".equals(queryType)){
            sql="select * from DCP_MODULAR_PRINT a where a.eid='"+eId+"' and a.modularno='"+modularNo+"'";
            sql+=" and a.status='100' ";
            sql+=" order by a.printno asc ";
        }

        if(sql.length()>0){
            List<Map<String, Object>> list = this.doQueryData(sql,null);
            DCP_PrintRPQueryRes.Level1Elm level1Elm = res.new Level1Elm();
            level1Elm.setModularNo(modularNo);
            level1Elm.setDetail(new ArrayList<>());
            if(list.size()>0){
                for (Map<String, Object> map : list){
                    DCP_PrintRPQueryRes.Detail detail = res.new Detail();
                    detail.setPrintNo(map.get("PRINTNO").toString());
                    detail.setPrintName(map.get("PRINTNAME").toString());
                    detail.setProName(map.get("PRONAME").toString());
                    detail.setParameter(map.get("PARAMETER").toString());
                    detail.setIsStandard(map.get("ISSTANDARD").toString());
                    detail.setIsDefault(map.get("ISDEFAULT").toString());
                    detail.setPrintType(map.get("PRINTTYPE").toString());
                    level1Elm.getDetail().add(detail);

                }
            }

            res.getDatas().add(level1Elm);
        }


        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_PrintRPQueryReq req) throws Exception {
        String modularNo = req.getRequest().getModularNo();
        StringBuffer sqlbuf=new StringBuffer();


        return sqlbuf.toString();
    }

}


