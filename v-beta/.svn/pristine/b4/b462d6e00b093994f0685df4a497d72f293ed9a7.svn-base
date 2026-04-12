package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_MaterialSetsCalculateReq;
import com.dsc.spos.json.cust.res.DCP_MaterialSetsCalculateRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_MaterialSetsCalculate extends SPosBasicService<DCP_MaterialSetsCalculateReq, DCP_MaterialSetsCalculateRes> {


    @Override
    protected boolean isVerifyFail(DCP_MaterialSetsCalculateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_MaterialSetsCalculateReq> getRequestType() {
        return new TypeToken<DCP_MaterialSetsCalculateReq>() {
        };

    }

    @Override
    protected DCP_MaterialSetsCalculateRes getResponseType() {
        return new DCP_MaterialSetsCalculateRes();
    }

//    private String getQueryPluStockSql(DCP_MaterialSetsCalculateReq req) throws Exception {
//        StringBuilder querySql = new StringBuilder();
//
//        querySql.append(" SELECT a.EID,a.ORGANIZATIONNO,PLUNO,FEATURENO, ")
//                .append(" SUM(QTY-LOCKQTY-ONLINEQTY) SQTY ")
//                .append(" FROM DCP_STOCK a ")
//                .append(" LEFT JOIN DCP_WAREHOUSE b ON a.EID=b.EID and a.WAREHOUSE=b.WAREHOUSE ")
//                .append(" WHERE a.EID='").append(req.geteId()).append("' ")
//                .append("   and b.WAREHOUSE_TYPE <>'3' and b.WAREHOUSE_TYPE<>'5' ");
//
//        for (DCP_MaterialSetsCalculateReq.PluList onePlu:req.getRequest().getPluList()){
//            querySql.append(" ( 1=2 ");
//            if (StringUtils.isEmpty(onePlu.getUpPluNo()) && CollectionUtils.isEmpty(onePlu.getUpPluList())){
//               querySql.append(" or a.UPPLNO=' ").append(onePlu.getPluNo()).append("' ");
//            }
//            querySql.append(")");
//        }
//
//        querySql.append(" group by a.ORGANIZATIONNO, PLUNO, FEATURENO, a.EID ");
//
//        return querySql.toString();
//
//
//    }

    private String getQueryStockSql(DCP_MaterialSetsCalculateReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.EID,a.ORGANIZATIONNO,PLUNO,FEATURENO, ")
                .append(" SUM(QTY-LOCKQTY-ONLINEQTY) SQTY ")
                .append(" FROM DCP_STOCK a ")
                .append(" LEFT JOIN DCP_WAREHOUSE b ON a.EID=b.EID and a.WAREHOUSE=b.WAREHOUSE ")
                .append(" WHERE a.EID='").append(req.geteId()).append("' ")
                .append("   and b.WAREHOUSE_TYPE <>'3' and b.WAREHOUSE_TYPE<>'5' ");

        querySql.append(" group by a.ORGANIZATIONNO, PLUNO, FEATURENO, a.EID ");

        return querySql.toString();
    }

    private String getVersionBomQuerySql(DCP_MaterialSetsCalculateReq req) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(" SELECT a.BOMNOBOMNO,a.VERSIONNUM,a.PLUNO,a.UNIT,a.MULQTY,b.MATERIAL_PLUNO ")
                .append(" ,b.MATERIAL_UNIT,b.MATERIAL_QTY,b.QTY,c.UNIT_RATIO,stock.SQTY ")
                .append(" FROM DCP_BOM_V a ")
                .append(" LEFT JOIN DCP_BOM_MATERIAL_V b ON a.eid=b.eid and a.BOMNO=b.BOMNO  ")
                .append(" LEFT JOIN DCP_UNITCONVERT c on c.eid=b.eid and c.OUNIT=b.MATERIAL_UNIT and c.unit=b.baseunit ")
                .append(" left join( ").append(getQueryStockSql(req)).append(" ) stock on stock.eid=b.eid and stock.PLUNO=b.MATERIAL_PLUNO  ")
                .append(" WHERE a.EID='").append(req.geteId());
        ;

        return stringBuilder.toString();
    }

    private String getBomQuerySql(DCP_MaterialSetsCalculateReq req) throws Exception {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(" SELECT a.BOMNO BOMNO,a.VERSIONNUM,a.PLUNO,a.UNIT,a.BATCHQTY,a.MULQTY,b.MATERIAL_PLUNO ")
                .append(" ,b.MATERIAL_UNIT,b.MATERIAL_QTY,b.QTY,c.UNIT_RATIO,stock.SQTY SMQTY,gl1.PLU_NAME as PLUNAME ")
                .append(" ,gl2.PLU_NAME as MATERIALPLUNAME,stock1.SQTY as SPQTY ")
                .append(" FROM DCP_BOM a ")
                .append(" LEFT JOIN DCP_BOM_MATERIAL b ON a.eid=b.eid and a.BOMNO=b.BOMNO  " +
                        " left join dcp_goods d on d.eid=b.eid and d.pluno=b.material_pluno ")
                .append(" LEFT JOIN DCP_UNITCONVERT c on c.eid=b.eid and c.OUNIT=b.MATERIAL_UNIT and c.unit=d.baseunit ")
                .append(" left join DCP_GOODS_LANG gl1 on gl1.eid=a.eid and gl1.PLUNO=a.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" left join DCP_GOODS_LANG gl2 on gl2.eid=b.eid and gl2.PLUNO=b.MATERIAL_PLUNO and gl2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" left join( ").append(getQueryStockSql(req)).append(" ) stock on stock.eid=b.eid and stock.PLUNO=b.MATERIAL_PLUNO  ")
                .append(" left join( ").append(getQueryStockSql(req)).append(" ) stock1 on stock1.eid=b.eid and stock1.PLUNO=b.material_pluno  ")
                .append(" WHERE a.EID='").append(req.geteId()).append("' and a.STATUS<>'-1' and EFFDATE<=sysdate ")
        ;

        return stringBuilder.toString();
    }

    @Override
    protected DCP_MaterialSetsCalculateRes processJson(DCP_MaterialSetsCalculateReq req) throws Exception {
        DCP_MaterialSetsCalculateRes res = getResponseType();

        //查询bom信息
        List<Map<String, Object>> bomList = doQueryData(getBomQuerySql(req), null);

        DCP_MaterialSetsCalculateReq.Request request = req.getRequest();

        List<DCP_MaterialSetsCalculateReq.PluList> pluList = request.getPluList();

        res.setDatas(new ArrayList<>());
        for (DCP_MaterialSetsCalculateReq.PluList onePlu : pluList) {
            DCP_MaterialSetsCalculateRes.Datas oneData = res.new Datas();

            BigDecimal pQty = BigDecimal.valueOf(Double.parseDouble(onePlu.getPQty()));
            //创建节点
            BomNode bomNode = new BomNode();
            bomNode.setPoQty(new BigDecimal(0));
            bomNode.setToWOQty(new BigDecimal(0));
            bomNode.setWoQty(new BigDecimal(0));

            if (StringUtils.isNotEmpty(onePlu.getUpPluNo())) {
                Map<String, Object> condition = new HashMap<>();
                condition.put("PLUNO", onePlu.getUpPluNo());
                condition.put("MATERIAL_PLUNO", onePlu.getPluNo());
                List<Map<String, Object>> bom = MapDistinct.getWhereMap(bomList, condition, true);
                Map<String, Object> bomInfo = bom.get(0);

                setBomInfoToNode(bomNode, bomInfo, pQty, false); //数量不重算

            } else if (CollectionUtils.isNotEmpty(onePlu.getUpPluList())) {
                //多上阶品号则需要取第一笔
                DCP_MaterialSetsCalculateReq.UpPluList oneUp = onePlu.getUpPluList().get(0);

                Map<String, Object> condition = new HashMap<>();
                condition.put("PLUNO", oneUp.getUpPluNo());
                condition.put("MATERIAL_PLUNO", onePlu.getPluNo());
                List<Map<String, Object>> bom = MapDistinct.getWhereMap(bomList, condition, true);
                Map<String, Object> bomInfo = bom.get(0);

                setBomInfoToNode(bomNode, bomInfo, pQty, false); //数量不重算

            } else { //无上阶品号则给根据自身配方给值
                Map<String, Object> condition = new HashMap<>();
                condition.put("PLUNO", onePlu.getPluNo());
                List<Map<String, Object>> bom = MapDistinct.getWhereMap(bomList, condition, true);
                if(!bom.isEmpty()){
                    Map<String, Object> bomInfo = bom.get(0);
                    bomNode.setPluNo(String.valueOf(bomInfo.get("PLUNO")));
                    bomNode.setPluName(String.valueOf(bomInfo.get("NAME")));
                    bomNode.setPoQty(BigDecimal.valueOf(Double.parseDouble(onePlu.getPQty()))); //需求量

                    double stock = Double.parseDouble(bomInfo.get("SPQTY").toString());
                    bomNode.setStock(BigDecimal.valueOf(stock));

                    if (bomNode.getPoQty().compareTo(bomNode.getStock()) > 0) {
                        bomNode.setAdQty(bomNode.getStock());
                    } else {
                        bomNode.setAdQty(bomNode.getPoQty());
                    }
                }
            }

            //递归展料
            expandBomNode(bomNode, bomList, onePlu.getPluNo(), pQty);
            //todo 合并配料需求量并根据实时库存反算下发量

            oneData.setPluNo(bomNode.getPluNo());
            oneData.setPluName(bomNode.getPluName());
            oneData.setUpPluNo(bomNode.getUpPluNo());
            oneData.setUpPluName(bomNode.getUpPluName());
            oneData.setRDate(onePlu.getRDate());
            oneData.setPUnit(bomNode.getPUnit());
            oneData.setPQty(String.valueOf(bomNode.getPoQty().doubleValue()));
            oneData.setToWOQty(String.valueOf(bomNode.getToWOQty().doubleValue()));
            oneData.setSets(String.valueOf(bomNode.getWoQty().doubleValue()));

            res.getDatas().add(oneData);
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    private void setBomInfoToNode(BomNode node, Map<String, Object> bomInfo, BigDecimal pQty, boolean recalculate) {
        if (node == null) return;
        if (null == bomInfo || bomInfo.isEmpty()) return;
        node.setPluNo(String.valueOf(bomInfo.get("MATERIAL_PLUNO")));
        node.setPluName(String.valueOf(bomInfo.get("MATERIAL_NAME")));

        node.setUpPluNo(String.valueOf(bomInfo.get("PLUNO")));
        node.setUpPluName(String.valueOf(bomInfo.get("PLUNAME")));

        double stock = Double.parseDouble(StringUtils.toString(bomInfo.get("SMQTY"),"0"));
        node.setStock(BigDecimal.valueOf(stock));

        double materialQty = Double.parseDouble(bomInfo.get("MATERIAL_QTY").toString()); //配方组成用量
        double baseFactor = Double.parseDouble(bomInfo.get("BATCHQTY").toString()); //配方基数
        double parentBase = Double.parseDouble(bomInfo.get("QTY").toString()); //主件底数

        //-应备料量：BOM标准应发量=主件产量*子件组成用量/（主件底数*配方基数）
        BigDecimal materialPQty;
        if (!recalculate) {
            materialPQty = pQty;
        } else {
            materialPQty = pQty.multiply(BigDecimal.valueOf(materialQty / (parentBase * baseFactor)));
        }

        //需求量
        node.setPoQty(materialPQty);
        //-可供给量：备料现有可用库存量（统计剔除在途仓和坏货仓的仓库库存）
        if (materialPQty.compareTo(node.getStock()) > 0) {
            node.setAdQty(node.getStock());
        } else {
            node.setAdQty(materialPQty);
        }
//                -理论分配：根据现有库存对备料需求进行分配，汇总所有备料的【应备料量】再进行判断
        //这里先不给分配，需要全部汇总后再进行统计

    }


    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_MaterialSetsCalculateReq req) throws Exception {
        return "";
    }

    private void expandBomNode(BomNode node, List<Map<String, Object>> bomInfo, String pluNo, BigDecimal pQty) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("PLUNO", pluNo);
        List<Map<String, Object>> childMat = MapDistinct.getWhereMap(bomInfo, condition, true);
        BomNode bomNode = node;
        if (null == bomNode) { //表示新增第一笔，
            return;
        }
        if (null == bomNode.getBomMaterial()) {
            bomNode.setBomMaterial(new ArrayList<>());
        }

        if (null != childMat && !childMat.isEmpty()) {
            for (Map<String, Object> oneMaterial : childMat) {
                BomNode materialNode = new BomNode();

                setBomInfoToNode(materialNode, oneMaterial, pQty, true); //重算子件需求量

                bomNode.getBomMaterial().add(materialNode);
                expandBomNode(materialNode, bomInfo, String.valueOf(oneMaterial.get("MATERIAL_PLUNO")), materialNode.getPoQty());
            }
        }
    }

    @Data
    @EqualsAndHashCode(of = {"pluNo"})
    private class BomNode {
        private String pluNo;
        private String pluName;
        private String upPluNo;
        private String upPluName;

        private String pUnit;

        //实时库存
        private BigDecimal stock;
        //需求量
        private BigDecimal poQty;
        //供给量
        private BigDecimal adQty;
        //最小齐料套数
        private BigDecimal woQty;
        //建议下发量
        private BigDecimal toWOQty;

        List<BomNode> bomMaterial;

        public boolean hasMaterial() {
            return null == bomMaterial || bomMaterial.isEmpty();
        }
    }


}
