package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_BomTreeQueryReq;
import com.dsc.spos.json.cust.res.DCP_BomTreeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BomTreeQuery extends SPosBasicService<DCP_BomTreeQueryReq, DCP_BomTreeQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_BomTreeQueryReq req) throws Exception
    {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null)
        {
            errMsg.append("requset不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BomTreeQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_BomTreeQueryReq>(){};
    }

    @Override
    protected DCP_BomTreeQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BomTreeQueryRes();
    }

    @Override
    protected DCP_BomTreeQueryRes processJson(DCP_BomTreeQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        //查詢資料未加生产单位
        DCP_BomTreeQueryRes res = this.getResponse();
        String eId = req.geteId();
        int totalRecords = 0; //总笔数
        int totalPages = 0;
        //查詢資料
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        res.setDatas(new ArrayList<DCP_BomTreeQueryRes.level1Elm>());
        if (getQData != null && getQData.isEmpty() == false) // 有資料，取得詳細內容
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            String allBomSql = this.getAllBomSql(req);
            List<Map<String, Object>> getAllBomData = this.doQueryData(allBomSql, null);
            String allMaterialSql = this.getAllMaterialSql(req);
            List<Map<String, Object>> getAllMaterialData = this.doQueryData(allMaterialSql, null);


            for(Map<String, Object> singleMap:getQData){
                DCP_BomTreeQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setBomNo(singleMap.get("BOMNO").toString());
                level1Elm.setBomType(singleMap.get("BOMTYPE").toString());
                level1Elm.setPluNo(singleMap.get("PLUNO").toString());
                level1Elm.setPluName(singleMap.get("PLUNAME").toString());
                level1Elm.setSpec(singleMap.get("SPEC").toString());
                level1Elm.setUnit(singleMap.get("UNIT").toString());
                level1Elm.setUnitName(singleMap.get("UNITNAME").toString());
                level1Elm.setStatus(singleMap.get("STATUS").toString());

                level1Elm.setMaterialList(new ArrayList<>());
                List<Map<String, Object>> materials = getAllMaterialData.stream().filter(x -> x.get("BOMNO").equals(level1Elm.getBomNo())).distinct().collect(Collectors.toList());

                for (Map<String, Object> material : materials){
                    //第一层
                    DCP_BomTreeQueryRes.MaterialList materialList = res.new MaterialList();
                    materialList.setMaterialPluNo(material.get("MATERIALPLUNO").toString());
                    materialList.setMaterialPluName(material.get("MATERIALPLUNAME").toString());
                    materialList.setMaterialQty(material.get("MATERIALQTY").toString());
                    materialList.setQty(material.get("QTY").toString());
                    materialList.setMaterialUnit(material.get("MATERIALUNIT").toString());
                    materialList.setMaterialUnitName(material.get("MATERIALUNITNAME").toString());
                    materialList.setSortId(material.get("SORTID").toString());

                    materialList.setChildren(new ArrayList<>());
                    List bomNos=new ArrayList();
                    bomNos.add(level1Elm.getBomNo());//不能把自己嵌套进去  否则就乱套了
                    expandMaterials( getAllBomData,  getAllMaterialData,  materialList,level1Elm.getBomNo(),bomNos, res);
                    level1Elm.getMaterialList().add(materialList);
                }

                res.getDatas().add(level1Elm);

            }

        }

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
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_BomTreeQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        String eId= req.geteId();
        String bomType = req.getRequest().getBomType();
        String restrictShop = req.getRequest().getRestrictShop();

        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        String langType=req.getLangType();

        if(Check.Null(bomType)|| bomType.isEmpty())
        {
            bomType="0";
        }
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        sqlbuf.append(" SELECT * FROM ( "
                + " select count(distinct a.bomno) over() num, dense_rank() over( order by a.bomno,a.pluno) rn, "
                + " a.bomno,a.bomtype,a.pluno,a.unit,G.PLU_NAME pluname,u.uname unitname,b.spec,a.status "
                + " from dcp_bom a"
                + " left join dcp_goods_lang g on a.eid=g.eid and a.pluno=g.pluno and g.lang_type='"+langType+"' "
                + " left join dcp_unit_lang u on a.eid = u.eid and a.unit=u.unit and u.lang_type='"+langType+"' "
                + " left join dcp_goods g1 on g1.eid=a.eid and g1.pluno=a.pluno " +
                " left join dcp_goods_unit_lang b on b.eid=a.eid and b.pluno=a.pluno and b.ounit=a.unit and b.lang_type='"+langType+"' "
        );

        sqlbuf.append(" where a.eid='"+eId+"' and a.bomType='"+bomType+"' and a.restrictShop='"+restrictShop+"' ");

        if(status!=null&&status.length()>0)
        {
            sqlbuf.append(" and a.status='"+status+"' ");
        }
        if(keyTxt!=null&&keyTxt.length()>0)
        {
            sqlbuf.append(" and (a.bomno like '%%"+keyTxt+"%%' or  a.pluno like '%%"+keyTxt+"%%' or g.plu_name like '%%"+keyTxt+"%%')");
        }
        sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));


        sql = sqlbuf.toString();
        return sql;
    }

    private String getAllMaterialSql(DCP_BomTreeQueryReq req) throws Exception{
        String eId= req.geteId();
        StringBuffer sb=new StringBuffer();
        sb.append("select a.bomno,a.material_pluno as materialPluNo,b.plu_name as materialPluName,a.MATERIAL_QTY as materialqty,a.qty,a.material_unit as materialunit,c.uname as materialunitname," +
                " a.sortid from " +
                " DCP_BOM_MATERIAL a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.material_pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang c on c.eid=a.eid and c.unit=a.material_unit and c.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+eId+"' ");

        return sb.toString();
    }

    private String getAllBomSql(DCP_BomTreeQueryReq req) throws Exception{
        String eId= req.geteId();
        StringBuffer sb=new StringBuffer();
        sb.append("select * from dcp_bom where eid='"+eId+"' and status='100'");
        return sb.toString();
    }

    private void expandMaterials(List<Map<String, Object>> getAllBomData, List<Map<String, Object>> getAllMaterialData, DCP_BomTreeQueryRes.MaterialList materialList,String oldBomNo,List bomNos,DCP_BomTreeQueryRes res){

        List<Map<String, Object>> pluBoms = getAllBomData.stream().filter(x -> x.get("PLUNO").toString().equals(materialList.getMaterialPluNo())&&!x.get("BOMNO").toString().equals(oldBomNo)).distinct().collect(Collectors.toList());
        if(CollUtil.isNotEmpty(pluBoms)){
            for(Map<String, Object> pluBom:pluBoms){
                String bomNo = pluBom.get("BOMNO").toString();
                if(bomNos.contains(bomNo)){
                    continue;
                }else{
                    bomNos.add(bomNo);
                }
                String status = pluBom.get("STATUS").toString();
                List<Map<String, Object>> materials = getAllMaterialData.stream().filter(x -> x.get("BOMNO").toString().equals(bomNo)).distinct().collect(Collectors.toList());
                if(CollUtil.isNotEmpty(materials)){
                    for(Map<String, Object> material:materials){
                        DCP_BomTreeQueryRes.MaterialList singleMaterial = res.new MaterialList();

                        singleMaterial.setMaterialPluNo(material.get("MATERIALPLUNO").toString());
                        singleMaterial.setMaterialPluName(material.get("MATERIALPLUNAME").toString());
                        singleMaterial.setMaterialQty(material.get("MATERIALQTY").toString());
                        singleMaterial.setQty(material.get("QTY").toString());
                        singleMaterial.setMaterialUnit(material.get("MATERIALUNIT").toString());
                        singleMaterial.setMaterialUnitName(material.get("MATERIALUNITNAME").toString());
                        singleMaterial.setSortId(material.get("SORTID").toString());

                        singleMaterial.setChildren(new ArrayList<>());
                        expandMaterials( getAllBomData,  getAllMaterialData,  singleMaterial,bomNo,bomNos, res);
                        materialList.getChildren().add(singleMaterial);
                    }
                }
                materialList.setBomNo(bomNo);
                materialList.setStatus(status);
            }
        }
    }

}
