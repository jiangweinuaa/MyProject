package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BatchingTaskMaterialReq;
import com.dsc.spos.json.cust.res.DCP_BatchingTaskMaterialRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_BatchingTaskMaterial extends SPosBasicService<DCP_BatchingTaskMaterialReq, DCP_BatchingTaskMaterialRes> {
    @Override
    protected boolean isVerifyFail(DCP_BatchingTaskMaterialReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BatchingTaskMaterialReq> getRequestType() {
        return new TypeToken<DCP_BatchingTaskMaterialReq>() {
        };
    }

    @Override
    protected DCP_BatchingTaskMaterialRes getResponseType() {
        return new DCP_BatchingTaskMaterialRes();
    }

    @Override
    protected DCP_BatchingTaskMaterialRes processJson(DCP_BatchingTaskMaterialReq req) throws Exception {
        DCP_BatchingTaskMaterialRes res = this.getResponseType();
        res.setDatas(new ArrayList<DCP_BatchingTaskMaterialRes.Datas>());
        String querySql = this.getQuerySql(req);
        List<Map<String, Object>> queryData = this.doQueryData(querySql, null);

        if(queryData.size()>0){
            for (Map<String, Object> row : queryData){
                DCP_BatchingTaskMaterialRes.Datas data = res.new Datas();
                data.setBatchTaskNo(row.get("BATCHTASKNO").toString());
                data.setPluNo(row.get("PLUNO").toString());
                data.setPluName(row.get("PLUNAME").toString());
                data.setPQty(req.getRequest().getPQty());
                data.setPUnit(row.get("PUNIT").toString());
                data.setPUName(row.get("PUNITNAME").toString());
                data.setPItem(row.get("PITEM").toString());
                data.setProcessNo(row.get("PROCESSNO").toString());
                data.setProcessName(row.get("PROCESSNAME").toString());
                data.setSItem(row.get("SITEM").toString());
                data.setZItem(row.get("ZITEM").toString());
                data.setMaterialPluNo(row.get("MATERIALPLUNO").toString());
                data.setMaterialPluName(row.get("MATERIALPLUNAME").toString());
                data.setMaterialPUnit(row.get("MATERIALPUNIT").toString());
                data.setMaterialPUName(row.get("MATERIALPUNITNAME").toString());
                data.setPWarehouse(row.get("PWAREHOUSE").toString());
                data.setKWarehouse(row.get("KWAREHOUSE").toString());
                data.setCopies(row.get("COPIES").toString());
                data.setBatchCopies(row.get("BATCHCOPIES").toString());
                data.setIsBuckle(row.get("ISBUCKLE").toString());
                data.setDetailList(new ArrayList<>());
                BigDecimal materialPqty = new BigDecimal(row.get("MATERIAL_PQTY").toString());

                BigDecimal singleppqty = new BigDecimal(row.get("SINGLEPPQTY").toString());
                BigDecimal tailmaterialpqty = new BigDecimal(row.get("TAILMATERIALPQTY").toString());
                BigDecimal sectailmaterialpqty = new BigDecimal(row.get("SECTAILMATERIALPQTY").toString());
                int copies = Integer.valueOf(data.getCopies());
                if(copies==1){
                    DCP_BatchingTaskMaterialRes.DetailList detailList = res.new DetailList();
                    detailList.setBenCopies("1");
                    detailList.setMaterialPQty(singleppqty.toString());
                    data.getDetailList().add(detailList);
                }
                else if(copies==2){
                    DCP_BatchingTaskMaterialRes.DetailList detailList1 = res.new DetailList();
                    detailList1.setBenCopies("1");
                    detailList1.setMaterialPQty(singleppqty.toString());
                    data.getDetailList().add(detailList1);

                    DCP_BatchingTaskMaterialRes.DetailList detailList2 = res.new DetailList();
                    detailList2.setBenCopies("2");
                    BigDecimal subtract = materialPqty.subtract(singleppqty);
                    detailList2.setMaterialPQty(subtract.toString());
                    data.getDetailList().add(detailList2);
                }
                else if(copies>2){
                    int benCopies=1;
                    int subtract = copies-1;
                    while(benCopies<=copies){
                        if(benCopies<subtract){
                            DCP_BatchingTaskMaterialRes.DetailList detailList1 = res.new DetailList();
                            detailList1.setBenCopies(String.valueOf(benCopies));
                            detailList1.setMaterialPQty(singleppqty.toString());
                            data.getDetailList().add(detailList1);
                        }
                        else if(benCopies==subtract){
                            if(sectailmaterialpqty.compareTo(BigDecimal.ONE)<=0){
                                DCP_BatchingTaskMaterialRes.DetailList detailList1 = res.new DetailList();
                                detailList1.setBenCopies(String.valueOf(benCopies));
                                detailList1.setMaterialPQty(singleppqty.toString());
                                data.getDetailList().add(detailList1);
                            }else{
                                DCP_BatchingTaskMaterialRes.DetailList detailList1 = res.new DetailList();
                                detailList1.setBenCopies(String.valueOf(benCopies));
                                detailList1.setMaterialPQty(sectailmaterialpqty.toString());
                                data.getDetailList().add(detailList1);
                            }
                        }
                        else if(benCopies==copies){
                            if(sectailmaterialpqty.compareTo(BigDecimal.ZERO)>0){
                                DCP_BatchingTaskMaterialRes.DetailList detailList1 = res.new DetailList();
                                detailList1.setBenCopies(String.valueOf(benCopies));
                                int sc=copies-2;
                                BigDecimal subtract1 = materialPqty.subtract(singleppqty.multiply(new BigDecimal(sc))).subtract(sectailmaterialpqty);
                                detailList1.setMaterialPQty(subtract1.toString());
                                data.getDetailList().add(detailList1);
                            }
                            else if(sectailmaterialpqty.compareTo(BigDecimal.ZERO)==0){
                                DCP_BatchingTaskMaterialRes.DetailList detailList1 = res.new DetailList();
                                detailList1.setBenCopies(String.valueOf(benCopies));
                                int sc=copies-1;
                                BigDecimal subtract1 = materialPqty.subtract(singleppqty.multiply(new BigDecimal(sc)));
                                detailList1.setMaterialPQty(subtract1.toString());
                                data.getDetailList().add(detailList1);
                            }
                            //SECTAILMATERIALPQTY不为0则materialPQty=MATERIAL_PQTY-(COPIES-2)*SINGLEMATERIALPQTY-SECTAILMATERIALPQTY，
//为0则materialPQty=MATERIAL_PQTY-(COPIES-1)*SINGLEMATERIALPQTY；
                        }

                        benCopies++;
                    }
                }

                res.getDatas().add(data);
//当COPIES>2，返回COPIES笔数据，
//当1<=benCopies<COPIES-1，materialPQty=SINGLEMATERIALPQTY；
//当benCopies=COPIES-1，SECTAILMATERIALPQTY不为0则materialPQty=SECTAILMATERIALPQTY，为0则materialPQty=SINGLEMATERIALPQTY；
//当benCopies=COPIES，
//SECTAILMATERIALPQTY不为0则materialPQty=MATERIAL_PQTY-(COPIES-2)*SINGLEMATERIALPQTY-SECTAILMATERIALPQTY，
//为0则materialPQty=MATERIAL_PQTY-(COPIES-1)*SINGLEMATERIALPQTY；

            }
        }

        res.setSuccess(true);
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_BatchingTaskMaterialReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("" +
                "select a.batchtaskno,a.pluno,d.plu_name as pluname,a.punit,e.uname as punitname,b.pitem,b.sitem,b.zitem,b.MATERIAL_PLUNO as materialpluno,f.plu_name as materialpluname," +
                " b.material_unit as materialPunit,g.uname as materialpunitname,b.pwarehouse,b.kwarehouse,c.PROCESSNO,h.processname,b.batchcopies,c.copies,b.SINGLEMATERIALPQTY as SINGLEPPQTY,b.TAILMATERIALPQTY,b.SECTAILMATERIALPQTY,b.material_pqty," +
                " b.isbuckle  " +
                " from MES_BATCHTASK a " +
                " left join MES_BATCHTASK_MATERIAL b on a.eid=b.eid and a.organizationno=b.organizationno and a.batchtaskno=b.batchtaskno " +
                " left join mes_batchtask_process c on a.eid=c.eid and a.organizationno=c.organizationno and a.batchtaskno=c.batchtaskno and b.pitem=c.pitem " +
                " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.punit and e.lang_type='"+req.getLangType()+"'" +
                " left join dcp_goods_lang f on f.eid=a.eid and f.pluno=b.MATERIAL_PLUNO and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang g on g.eid=a.eid and g.unit=b.material_unit and g.lang_type='"+req.getLangType()+"' " +
                " left join MES_PROCESS h on h.eid=a.eid and h.PROCESSNO=c.PROCESSNO  " +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.batchtaskno='"+req.getRequest().getBatchTaskNo()+"' " +
                " and a.pluno='"+req.getRequest().getPluNo()+"' " +
                " and a.punit='"+req.getRequest().getPUnit()+"' " +
                " and b.MATERIAL_TYPE='0' and b.ISBATCH='Y' and c.COPIES-b.BATCHCOPIES>0 " +
                " ");

        if(Check.NotNull(req.getRequest().getMaterialPluNo())){
            sb.append(" and b.MATERIAL_PLUNO='"+req.getRequest().getMaterialPluNo()+"' ");
        }
        if(Check.NotNull(req.getRequest().getProcessNo())){
            sb.append(" and c.PROCESSNO='"+req.getRequest().getProcessNo()+"' ");
        }


        return sb.toString();
    }
}
