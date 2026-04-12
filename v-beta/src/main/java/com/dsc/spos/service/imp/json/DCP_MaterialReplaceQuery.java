package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_MaterialReplaceQueryReq;
import com.dsc.spos.json.cust.res.DCP_MaterialReplaceQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_MaterialReplaceQuery extends SPosBasicService<DCP_MaterialReplaceQueryReq, DCP_MaterialReplaceQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_MaterialReplaceQueryReq req) throws Exception {
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
    protected TypeToken<DCP_MaterialReplaceQueryReq> getRequestType() {
        return new TypeToken<DCP_MaterialReplaceQueryReq>(){};
    }

    @Override
    protected DCP_MaterialReplaceQueryRes getResponseType() {
        return new DCP_MaterialReplaceQueryRes();
    }

    @Override
    protected DCP_MaterialReplaceQueryRes processJson(DCP_MaterialReplaceQueryReq req) throws Exception {
        DCP_MaterialReplaceQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> row : getQData){
                DCP_MaterialReplaceQueryRes.Level1Elm level1Elm = res.new Level1Elm();

                level1Elm.setReplaceType(row.get("REPLACETYPE").toString());
                level1Elm.setOrganizationNo(row.get("ORGANIZATIONNO").toString());
                level1Elm.setOrganizationName(row.get("ORGANIZATIONNAME").toString());
                level1Elm.setMaterialPluNo(row.get("MATERIALPLUNO").toString());
                level1Elm.setMaterialPluName(row.get("MATERIALPLUNAME").toString());
                level1Elm.setMaterialQty(row.get("MATERIALQTY").toString());
                level1Elm.setMaterialUnit(row.get("MATERIALUNIT").toString());
                level1Elm.setMaterialUName(row.get("MATERIALUNAME").toString());
                level1Elm.setReplacePluNo(row.get("REPLACEPLUNO").toString());
                level1Elm.setReplacePluName(row.get("REPLACEPLUNAME").toString());
                level1Elm.setReplaceQty(row.get("REPLACEQTY").toString());
                level1Elm.setReplaceUnit(row.get("REPLACEUNIT").toString());
                level1Elm.setReplaceUName(row.get("REPLACEUNAME").toString());
                level1Elm.setReplaceBDate(row.get("REPLACEBDATE").toString());
                level1Elm.setReplaceEDate(row.get("REPLACEEDATE").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setPriority(row.get("PRIORITY").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                res.getDatas().add(level1Elm);
            }
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_MaterialReplaceQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        String anotherWhere=" and 1=1 ";
        if (!Check.Null(status)) {
            anotherWhere += " and a.status='" + status + "' ";
        }
        if (!Check.Null(req.getRequest().getOrganizationNo())) {
            anotherWhere += " and a.organizationno='" + req.getRequest().getOrganizationNo() + "' ";
        }
        if (!Check.Null(req.getRequest().getCategory())) {
            anotherWhere += " and b.category='" + req.getRequest().getCategory() + "' ";
        }
        if (!Check.Null(req.getRequest().getMaterialPluNo())) {
            anotherWhere += " and a.material_pluno='" + req.getRequest().getMaterialPluNo() + "' ";
        }
        if (!Check.Null(req.getRequest().getMaterialUnit())) {
            anotherWhere += " and a.material_unit='" + req.getRequest().getMaterialUnit() + "' ";
        }
        if (!Check.Null(req.getRequest().getReplaceType())) {
            anotherWhere += " and a.replacetype='" + req.getRequest().getReplaceType() + "' ";
        }
        if(Check.NotNull(req.getRequest().getKeyTxt())){
            anotherWhere += " and (a.material_pluno like '%%" + req.getRequest().getKeyTxt()
                    + "%%' or c.plu_name like '%%" + req.getRequest().getKeyTxt() + "%%' " +
                    " or d.plu_name like '%%" + req.getRequest().getKeyTxt() + "%%'" +
                    " or a.replace_pluno like '%%"+req.getRequest().getKeyTxt()+"%%') ";
        }


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.replaceType,a.organizationno,g.org_name as organizationname,a.status,a.priority,a.memo,a.REPLACE_BDATE as replacebdate,a.REPLACE_eDATE as replaceedate," +
                "   a.material_pluno as materialpluno,c.plu_name as materialpluname,a.material_qty as materialqty, a.material_unit as materialunit,e.uname as materialuname,a.replace_pluno as replacepluno,d.plu_name as replacepluname," +
                "   a.replace_qty as replaceqty,a.replace_unit as replaceunit,f.uname as replaceuname"
                + " from MES_MATERIAL_REPLACE a " +
                " left join dcp_goods b on a.eid=b.eid and a.material_pluno=b.pluno " +
                " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.material_pluno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.REPLACE_PLUNO and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.material_unit and e.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang f on f.eid=a.eid and f.unit=a.REPLACE_UNIT and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang g on g.eid=a.eid and g.organizationno=a.organizationno and g.lang_type='"+req.getLangType()+"' "
                + " where a.eid='"+eId+"' "+anotherWhere
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


