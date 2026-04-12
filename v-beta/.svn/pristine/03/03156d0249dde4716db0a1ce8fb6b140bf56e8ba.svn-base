package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.MES_MoCreateReq;
import com.dsc.spos.json.cust.res.MES_MoCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MES_MoCreate extends SPosAdvanceService<MES_MoCreateReq, MES_MoCreateRes>
{
    Logger logger = LogManager.getLogger(MES_MoCreate.class.getName());

    @Override
    protected void processDUID(MES_MoCreateReq req, MES_MoCreateRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


        String sql="select * from MES_MO where eid='"+req.getRequest().getEId()+"' " +
                "and LOAD_DOCNO='"+req.getRequest().getLoadDocNo()+"' " ;

        List<Map<String, Object>>  data=this.doQueryData(sql, null);

        if (data != null && data.size()>0)
        {
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "docNo单号已经存在！");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setDoc_no(data.get(0).get("MONO").toString());
            res.setOrg_no(data.get(0).get("ORGANIZATIONNO").toString());
            return;
        }


        //获取单号
        String mono=PosPub.getBillNo(this.dao,req.getRequest().getEId(),req.getRequest().getOrganizationNo(),"MONO");

        //产生项次
        //int mo_detail_Item=0;
        //产生项次
        int mo_process_Item=0;
        //产生项次
        int mo_subprocess_Item=0;
        //产生项次
        int mo_subprocessMaterial_Item=0;

        //缓存当前单据的单位对应的小数位数
        Map<String,String> map_unit=new HashMap<>();


        //
        List<MES_MoCreateReq.level1> detailDatas=req.getRequest().getDetailDatas();
        for (MES_MoCreateReq.level1 detail : detailDatas)
        {
            //mo_detail_Item+=1;

            //查询生效的BOM,根据创建日期取最大的
            String sql_plu_bom="select a.*,b.organizationno from MES_BOM a " +
                    "left join MES_BOM_RANGE b on a.eid=b.eid and a.bomno=b.bomno " +
                    "where a.eid='"+req.getRequest().getEId()+"' " +
                    "and a.pluno='"+detail.getPluNo()+"' " +
                    "and a.unit='"+detail.getPUnit()+"' " +
                    "and a.effdate<SYSDATE+1 " +
                    "and ((a.restrictorg=0) or (a.restrictorg=1 and b.organizationno='"+req.getRequest().getOrganizationNo()+"')) " +
                    "order by a.CREATETIME desc ";
            List<Map<String, Object>> BomDatas=this.doQueryData(sql_plu_bom, null);
            if (BomDatas == null || BomDatas.size()==0)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "pluNo="+detail.getPluNo() +",pUnit="+detail.getPUnit()+",未维护生效配方");
            }

            //根据BOM单号，查工序
            String bomno=BomDatas.get(0).get("BOMNO").toString();
            String mulqty=BomDatas.get(0).get("MULQTY").toString();//配方基数
            String versionnum=BomDatas.get(0).get("VERSIONNUM").toString();

            if (!PosPub.isNumericType(mulqty))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "bomno="+bomno +",MES_BOM.MULQTY字段不是数字类型");
            }
            if (new BigDecimal(mulqty).compareTo(BigDecimal.ZERO)==0)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "bomno="+bomno +",MES_BOM.MULQTY字段不能为0");
            }

            //
            String sql_BOM_PROCESS_info="select a.*,b.sitem,c.item,c.material_type,c.material_pluno,c.MATERIAL_NAME,c.material_unit,c.material_qty,c.qty," +
                    "c.isbatch,c.ismix,c.mixgroup,c.isbuckle,c.sortid as MATERIAL_SORTID,d.WAREHOUSE,e.WAREHOUSE AS WAREHOUSE_PW,f.WAREHOUSE AS WAREHOUSE_KW " +
                    "from MES_BOM_PROCESS a " +
                    "left join MES_BOM_SUBPROCESS b on a.eid=b.eid and a.bomno=b.bomno and a.pitem=b.pitem " +
                    "left join MES_BOM_SUBPROCESS_MATERIAL c on a.eid=c.eid and a.bomno=c.bomno and b.pitem=c.pitem and b.sitem=c.sitem " +
                    "left join MES_WAREHOUSE_GROUP_DETAIL d on a.eid=d.eid and a.WGROUPNO=d.WGROUPNO and d.ORGANIZATIONNO='"+req.getRequest().getOrganizationNo()+"' " +
                    "left join MES_WAREHOUSE_GROUP_DETAIL e on a.eid=e.eid and c.PWGROUPNO=e.WGROUPNO and e.ORGANIZATIONNO='"+req.getRequest().getOrganizationNo()+"' " +
                    "left join MES_WAREHOUSE_GROUP_DETAIL f on a.eid=f.eid and c.KWGROUPNO=f.WGROUPNO and f.ORGANIZATIONNO='"+req.getRequest().getOrganizationNo()+"' " +
                    "where a.eid='"+req.getRequest().getEId()+"' " +
                    "and a.bomno='"+bomno+"'  " +
                    "order by a.sortid,b.sitem,c.sortid ";
            List<Map<String, Object>> bom_process_Datas=this.doQueryData(sql_BOM_PROCESS_info, null);
            if (bom_process_Datas == null || bom_process_Datas.size()==0)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "bomno="+bomno +",工序未设置");
            }


            //取最小pitem
            OptionalInt optional=bom_process_Datas.stream().mapToInt(m -> Integer.parseInt(m.get("PITEM").toString())).min();
            //没值给0
            int min_PITEM=optional.orElse(0);

            //去重PITEM
            List<Map<String, Object>> bom_processList=bom_process_Datas.stream().filter(PosPub.distinctByKeys(p->p.get("PITEM").toString())).collect(Collectors.toList());
            for (Map<String, Object> bom_process_data : bom_processList)
            {
                //MES_MO_DETAIL.PQTY*MES_BOM_PROCESS.PQTY/MES_BOM.MULQTY
                String bom_processQty=bom_process_data.get("PQTY").toString();
                if (!PosPub.isNumericType(bom_processQty))
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "bomno="+bomno +",MES_BOM_PROCESS.PQTY字段不是数字类型");
                }
                if (new BigDecimal(bom_processQty).compareTo(BigDecimal.ZERO)==0)
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "bomno="+bomno +",MES_BOM_PROCESS.PQTY字段不能为0");
                }

                String warehouse_wgroup=bom_process_data.get("WAREHOUSE").toString();



                //
                mo_process_Item+=1;


                //最小工序给Y
                String pitem=bom_process_data.get("PITEM").toString();
                String isExecute="N";
                if (pitem.equals(min_PITEM+""))
                {
                    isExecute="Y";
                }

                String punit=bom_process_data.get("PUNIT").toString();
                if (!map_unit.containsKey(req.getRequest().getEId()+"_"+ punit))
                {
                    String sql_unit="select * from dcp_unit where eid='"+req.getRequest().getEId()+"' and unit='"+punit+"' ";
                    List<Map<String, Object>> getQ_unit=this.doQueryData(sql_unit, null);
                    if (getQ_unit == null || getQ_unit.size()==0)
                    {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "unit="+punit+",在dcp_unit表找不到数据");
                    }
                    //
                    map_unit.put(req.getRequest().getEId()+"_"+punit,getQ_unit.get(0).get("UDLENGTH").toString());
                }


                int udlength=Integer.parseInt(map_unit.get(req.getRequest().getEId()+"_"+punit).toString());

                BigDecimal process_pqty=new BigDecimal(detail.getPQty()).multiply(new BigDecimal(bom_processQty)).divide(new BigDecimal(mulqty),udlength,BigDecimal.ROUND_HALF_UP);

                //MES_MO_PROCESS
                String[] columnsName_MES_MO_PROCESS = {
                        "EID","ORGANIZATIONNO","MONO","ITEM",
                        "MITEM","MPLUNO","PITEM","SORTID","PROCESSNO","ARTIFACTTYPE","ARTIFACTNO","ARTIFACTNAME",
                        "PPQTY","PUNIT","ISEXECUTABLE","WAREHOUSE"
                };

                DataValue[] insValue_MES_MO_PROCESS = new DataValue[]
                        {
                                new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                new DataValue(mono, Types.VARCHAR),
                                new DataValue(mo_process_Item, Types.INTEGER),
                                new DataValue(detail.getItem(), Types.INTEGER),
                                new DataValue(detail.getPluNo(), Types.VARCHAR),
                                new DataValue(bom_process_data.get("PITEM").toString(), Types.INTEGER),
                                new DataValue(bom_process_data.get("SORTID").toString(), Types.INTEGER),
                                new DataValue(bom_process_data.get("PROCESSNO").toString(), Types.VARCHAR),
                                new DataValue(bom_process_data.get("ARTIFACTTYPE").toString(), Types.VARCHAR),
                                new DataValue(bom_process_data.get("ARTIFACTNO").toString(), Types.VARCHAR),
                                new DataValue(bom_process_data.get("ARTIFACTNAME").toString(), Types.VARCHAR),
                                new DataValue(process_pqty, Types.VARCHAR),//MES_MO_DETAIL.PQTY*MES_BOM_PROCESS.PQTY/MES_BOM.MULQTY
                                new DataValue(punit, Types.VARCHAR),
                                new DataValue(isExecute, Types.VARCHAR),
                                new DataValue(warehouse_wgroup, Types.VARCHAR)
                        };
                InsBean ib_MES_MO_PROCESS = new InsBean("MES_MO_PROCESS", columnsName_MES_MO_PROCESS);
                ib_MES_MO_PROCESS.addValues(insValue_MES_MO_PROCESS);
                this.addProcessData(new DataProcessBean(ib_MES_MO_PROCESS));


                //子工序
                List<Map<String, Object>> bom_subprocessList=bom_process_Datas.stream().filter(p->p.get("PITEM").toString().equals(bom_process_data.get("PITEM").toString())).filter(PosPub.distinctByKeys(p->p.get("SITEM").toString())).collect(Collectors.toList());
                if (bom_subprocessList != null && bom_subprocessList.size()>0)
                {

                    for (Map<String, Object> bom_subprocess : bom_subprocessList)
                    {
                        //跳过
                        if (Check.Null(bom_subprocess.get("SITEM").toString())) continue;

                        mo_subprocess_Item+=1;

                        //MES_MO_SUBPROCESS
                        String[] columnsName_MES_MO_SUBPROCESS = {
                                "EID","ORGANIZATIONNO","MONO","ITEM",
                                "MITEM","SITEM","PITEM"
                        };

                        DataValue[] insValue_MES_MO_SUBPROCESS = new DataValue[]
                                {
                                        new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(mono, Types.VARCHAR),
                                        new DataValue(mo_subprocess_Item, Types.INTEGER),
                                        new DataValue(new BigDecimal(detail.getItem()).intValue(), Types.INTEGER),
                                        new DataValue(bom_subprocess.get("SITEM").toString(), Types.INTEGER),
                                        new DataValue(bom_process_data.get("PITEM").toString(), Types.INTEGER)
                                };
                        InsBean ib_MES_MO_SUBPROCESS = new InsBean("MES_MO_SUBPROCESS", columnsName_MES_MO_SUBPROCESS);
                        ib_MES_MO_SUBPROCESS.addValues(insValue_MES_MO_SUBPROCESS);
                        this.addProcessData(new DataProcessBean(ib_MES_MO_SUBPROCESS));

                        //子工序-原料
                        List<Map<String, Object>> bom_subprocessMateriaList=bom_process_Datas.stream().filter(p->p.get("PITEM").toString().equals(bom_process_data.get("PITEM").toString()) && p.get("SITEM").toString().equals(bom_subprocess.get("SITEM").toString())).filter(PosPub.distinctByKeys(p->p.get("ITEM").toString())).collect(Collectors.toList());
                        if (bom_subprocessMateriaList != null && bom_subprocessMateriaList.size()>0)
                        {

                            for (Map<String, Object> bom_subprocessMateria : bom_subprocessMateriaList)
                            {

                                //跳过,这要在下面判断的签名，否在空值没过滤
                                if (Check.Null(bom_subprocessMateria.get("ITEM").toString())) continue;

                                String warehouse_pw=bom_subprocessMateria.get("WAREHOUSE_PW").toString();
                                String warehouse_kw=bom_subprocessMateria.get("WAREHOUSE_KW").toString();

                                //MES_MO_DETAIL.PQTY*MES_BOM_SUBPROCESS_MATERIAL.MATERIAL_QTY/(MES_BOM.MULQTY*MES_BOM_SUBPROCESS_MATERIAL.QTY)

                                String  bom_subprocessMateriaQty=bom_subprocessMateria.get("QTY").toString();
                                if (!PosPub.isNumericType(bom_subprocessMateriaQty))
                                {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "bomno="+bomno +",MES_BOM_SUBPROCESS_MATERIAL.QTY字段不是数字类型");
                                }
                                if (new BigDecimal(bom_subprocessMateriaQty).compareTo(BigDecimal.ZERO)==0)
                                {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "bomno="+bomno +",MES_BOM_SUBPROCESS_MATERIAL.QTY字段不能为0");
                                }



                                mo_subprocessMaterial_Item+=1;


                                //单位精度获取
                                punit=bom_subprocessMateria.get("MATERIAL_UNIT").toString();
                                if (!map_unit.containsKey(req.getRequest().getEId()+"_"+ punit))
                                {
                                    String sql_unit="select * from dcp_unit where eid='"+req.getRequest().getEId()+"' and unit='"+punit+"' ";
                                    List<Map<String, Object>> getQ_unit=this.doQueryData(sql_unit, null);
                                    if (getQ_unit == null || getQ_unit.size()==0)
                                    {
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "unit="+punit+",在dcp_unit表找不到数据");
                                    }
                                    //
                                    map_unit.put(req.getRequest().getEId()+"_"+punit,getQ_unit.get(0).get("UDLENGTH").toString());
                                }
                                udlength=Integer.parseInt(map_unit.get(req.getRequest().getEId()+"_"+punit).toString());

                                String bmMaterialSql="select nvl(a.LOSS_RATE,0) as loss_rate from MES_BOM_MATERIAL a where a.eid='"+req.getRequest().getEId()+"' and a.bomno='"+bomno+"' and a.MATERIAL_PLUNO='"+bom_subprocessMateria.get("MATERIAL_PLUNO").toString()+"'";
                                List<Map<String, Object>> list = this.doQueryData(bmMaterialSql, null);
                                BigDecimal lossRate=new BigDecimal(0);
                                if(CollUtil.isNotEmpty(list)){
                                    lossRate=new BigDecimal(list.get(0).get("LOSS_RATE").toString());
                                }
                                //
                                BigDecimal bgm_div=new BigDecimal(mulqty).multiply(new BigDecimal(bom_subprocessMateria.get("QTY").toString()));
                                BigDecimal moRate=new BigDecimal(1).add(lossRate.multiply(new BigDecimal("0.01")));
                                BigDecimal bgm_subprocess_materialQty=new BigDecimal(detail.getPQty()).multiply(new BigDecimal(bom_subprocessMateria.get("MATERIAL_QTY").toString())).multiply(moRate).divide(bgm_div,udlength,BigDecimal.ROUND_HALF_UP);

                                //MES_MO_SUBPROCESS_MATERIAL
                                String[] columnsName_MES_MO_SUBPROCESS_MATERIAL = {
                                        "EID","ORGANIZATIONNO","MONO","ITEM",
                                        "MITEM","ZITEM","MATERIAL_TYPE","MATERIAL_PLUNO","MATERIAL_UNIT","MATERIAL_PQTY",
                                        "ISBATCH","ISMIX","MIXGROUP","ISBUCKLE","SORTID","PITEM","SITEM","MATERIAL_NAME",
                                        "PWAREHOUSE","KWAREHOUSE"
                                };

                                DataValue[] insValue_MES_MO_SUBPROCESS_MATERIAL = new DataValue[]
                                        {
                                                new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                                new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                                new DataValue(mono, Types.VARCHAR),
                                                new DataValue(mo_subprocessMaterial_Item, Types.INTEGER),
                                                new DataValue(new BigDecimal(detail.getItem()).intValue(), Types.INTEGER),
                                                new DataValue(bom_subprocessMateria.get("ITEM").toString(), Types.INTEGER),
                                                new DataValue(bom_subprocessMateria.get("MATERIAL_TYPE").toString(), Types.VARCHAR),
                                                new DataValue(bom_subprocessMateria.get("MATERIAL_PLUNO").toString(), Types.VARCHAR),
                                                new DataValue(bom_subprocessMateria.get("MATERIAL_UNIT").toString(), Types.VARCHAR),
                                                new DataValue(bgm_subprocess_materialQty, Types.VARCHAR), //MES_MO_DETAIL.PQTY*MES_BOM_SUBPROCESS_MATERIAL.MATERIAL_QTY/(MES_BOM.MULQTY*MES_BOM_SUBPROCESS_MATERIAL.QTY)
                                                new DataValue(bom_subprocessMateria.get("ISBATCH").toString(), Types.VARCHAR),
                                                new DataValue(bom_subprocessMateria.get("ISMIX").toString(), Types.VARCHAR),
                                                new DataValue(bom_subprocessMateria.get("MIXGROUP").toString(), Types.VARCHAR),
                                                new DataValue(bom_subprocessMateria.get("ISBUCKLE").toString(), Types.VARCHAR),
                                                new DataValue(bom_subprocessMateria.get("MATERIAL_SORTID").toString(), Types.INTEGER),
                                                new DataValue(bom_process_data.get("PITEM").toString(), Types.INTEGER),
                                                new DataValue(bom_subprocess.get("SITEM").toString(), Types.INTEGER),
                                                new DataValue(bom_subprocessMateria.get("MATERIAL_NAME").toString(), Types.VARCHAR),
                                                new DataValue(warehouse_pw, Types.VARCHAR),
                                                new DataValue(warehouse_kw, Types.VARCHAR)

                                        };
                                InsBean ib_MES_MO_SUBPROCESS_MATERIAL = new InsBean("MES_MO_SUBPROCESS_MATERIAL", columnsName_MES_MO_SUBPROCESS_MATERIAL);
                                ib_MES_MO_SUBPROCESS_MATERIAL.addValues(insValue_MES_MO_SUBPROCESS_MATERIAL);
                                this.addProcessData(new DataProcessBean(ib_MES_MO_SUBPROCESS_MATERIAL));
                            }
                        }
                    }
                }

            }


            //MES_MO_DETAIL
            String[] columnsName_MES_MO_DETAIL = {
                    "EID","ORGANIZATIONNO","MONO","ITEM",
                    "PLUNO","PUNIT","PQTY","BEGINDATE","ENDDATE","BOMNO","VERSIONNUM"
            };

            DataValue[] insValue_MES_MO_DETAIL = new DataValue[]
                    {
                            new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(mono, Types.VARCHAR),
                            new DataValue(detail.getItem(), Types.INTEGER),
                            new DataValue(detail.getPluNo(), Types.VARCHAR),
                            new DataValue(detail.getPUnit(), Types.VARCHAR),
                            new DataValue(detail.getPQty(), Types.FLOAT),
                            new DataValue(detail.getBeginDate(), Types.VARCHAR),
                            new DataValue(detail.getEndDate(), Types.VARCHAR),
                            new DataValue(bomno, Types.VARCHAR),
                            new DataValue(versionnum, Types.VARCHAR),
                    };
            InsBean ib_MES_MO_DETAIL = new InsBean("MES_MO_DETAIL", columnsName_MES_MO_DETAIL);
            ib_MES_MO_DETAIL.addValues(insValue_MES_MO_DETAIL);
            this.addProcessData(new DataProcessBean(ib_MES_MO_DETAIL));
        }


        //单头MES_MO
        String[] columnsName_MES_MO = {
                "EID","ORGANIZATIONNO","MONO","PGROUPNO",
                "BDATE","PDATE","LOAD_DOCNO","MEMO","CREATEOPID",
                "CREATEOPNAME","CREATETIME",
                "LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME",
                "ACCOUNTOPID","ACCOUNTOPNAME","ACCOUNTTIME","STATUS"
        };

        DataValue[] insValue_MES_MO = new DataValue[]
                {
                        new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                        new DataValue(mono, Types.VARCHAR),
                        new DataValue(req.getRequest().getPGroupNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getBDate(), Types.VARCHAR),
                        new DataValue(req.getRequest().getPDate(), Types.VARCHAR),
                        new DataValue(req.getRequest().getLoadDocNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getMemo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getCreatByNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getCreatByName(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue(req.getRequest().getCreatByNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getCreatByName(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue(req.getRequest().getCreatByNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getCreatByName(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue(new BigDecimal(req.getRequest().getStatus()).intValue(), Types.INTEGER)//状态 0新建 1确认
                };
        InsBean ib_MES_MO = new InsBean("MES_MO", columnsName_MES_MO);
        ib_MES_MO.addValues(insValue_MES_MO);
        this.addProcessData(new DataProcessBean(ib_MES_MO));

        this.doExecuteDataToDB();

        logger.info("\n*********MES_MoCreate MES ERP 下发工单成功erpno="+req.getRequest().getLoadDocNo()+"************\n");

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        res.setDoc_no(mono);
        res.setOrg_no(req.getRequest().getOrganizationNo());

    }

    @Override
    protected List<InsBean> prepareInsertData(MES_MoCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(MES_MoCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(MES_MoCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(MES_MoCreateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
        }
        else
        {
            if (Check.Null(req.getRequest().getEId()))
            {
                isFail = true;
                errMsg.append("eId不能为空,");
            }
            if (Check.Null(req.getRequest().getOrganizationNo()))
            {
                isFail = true;
                errMsg.append("organizationNo不能为空,");
            }
            if (Check.Null(req.getRequest().getLoadDocNo()))
            {
                isFail = true;
                errMsg.append("loadDocNo不能为空,");
            }
            if (Check.Null(req.getRequest().getPGroupNo()))
            {
                isFail = true;
                errMsg.append("pGroupNo不能为空,");
            }
            if (Check.Null(req.getRequest().getPDate()))
            {
                isFail = true;
                errMsg.append("pDate不能为空,");
            }
            if (Check.Null(req.getRequest().getBDate()))
            {
                isFail = true;
                errMsg.append("bDate不能为空,");
            }

            List<MES_MoCreateReq.level1> detailDatas=req.getRequest().getDetailDatas();

            if (detailDatas == null || detailDatas.size()==0)
            {
                isFail = true;
                errMsg.append("detailDatas不能为空,");
            }
            else
            {
                for (MES_MoCreateReq.level1 detailData : detailDatas)
                {

                    if (Check.Null(detailData.getPluNo()))
                    {
                        isFail = true;
                        errMsg.append("pluNo不能为空,");
                    }
                    if (Check.Null(detailData.getPUnit()))
                    {
                        isFail = true;
                        errMsg.append("pUnit不能为空,");
                    }
                    if (Check.Null(detailData.getPQty()))
                    {
                        isFail = true;
                        errMsg.append("pQty不能为空,");
                    }
                    if (Check.Null(detailData.getBeginDate()))
                    {
                        isFail = true;
                        errMsg.append("beginDate不能为空,");
                    }
                    if (Check.Null(detailData.getEndDate()))
                    {
                        isFail = true;
                        errMsg.append("endDate不能为空,");
                    }
                }
            }

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<MES_MoCreateReq> getRequestType()
    {
        return new TypeToken<MES_MoCreateReq>(){};
    }

    @Override
    protected MES_MoCreateRes getResponseType()
    {
        return new MES_MoCreateRes();
    }



}
