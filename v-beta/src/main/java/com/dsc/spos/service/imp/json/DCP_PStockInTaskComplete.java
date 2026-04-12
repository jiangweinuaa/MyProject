package com.dsc.spos.service.imp.json;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsBomQueryReq;
import com.dsc.spos.json.cust.req.DCP_PStockInCreateReq;
import com.dsc.spos.json.cust.req.DCP_PStockInProcessReq;
import com.dsc.spos.json.cust.req.DCP_PStockInTaskCompleteReq;
import com.dsc.spos.json.cust.res.DCP_GoodsBomQueryRes;
import com.dsc.spos.json.cust.res.DCP_PStockInCreateRes;
import com.dsc.spos.json.cust.res.DCP_PStockInProcessRes;
import com.dsc.spos.json.cust.res.DCP_PStockInTaskCompleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_PStockInTaskComplete extends SPosAdvanceService<DCP_PStockInTaskCompleteReq, DCP_PStockInTaskCompleteRes>
{

    @Override
    protected void processDUID(DCP_PStockInTaskCompleteReq req, DCP_PStockInTaskCompleteRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sysDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sysTime =	new SimpleDateFormat("HHmmss").format(new Date());


        List<DCP_PStockInTaskCompleteReq.level2> datas = req.getRequest().getDatas();

        try
        {

            DCP_PStockInCreateReq dcp_pStockInCreateReq=new DCP_PStockInCreateReq();
            //对象拷贝
            BeanUtil.copyProperties(req, dcp_pStockInCreateReq);
            //赋值
            dcp_pStockInCreateReq.setServiceId("DCP_PStockInCreate");
            DCP_PStockInCreateReq.levelElm request=dcp_pStockInCreateReq.new levelElm();
            request.setDatas(new ArrayList<>());
            request.setbDate(sysDate);
            request.setDocType("0");
            request.setLoadDocNo("");
            request.setLoadDocType("");
            request.setMaterialWarehouseNo(req.getRequest().getMaterialWarehouseNo());
            request.setMemo("");
            request.setOfNo(req.getRequest().getOfNo());
            request.setoType("");
            request.setProcessPlanNo(req.getRequest().getProcessPlanNo());
            request.setpStockInID(req.getRequestId());
            request.setpTemplateNo(req.getRequest().getPTemplateNo());
            request.setStatus("0");
            request.setTask0No(req.getRequest().getTask0No());
            request.setWarehouse(req.getRequest().getWarehouse());

            int detailItem=0;
            BigDecimal bdm_tot_amt=new BigDecimal("0");
            long bdm_tot_cqty=datas.stream().filter(PosPub.distinctByKeys(p->p.getPluNo())).count();
            BigDecimal bdm_tot_distriamt=new BigDecimal("0");

            for (DCP_PStockInTaskCompleteReq.level2 data : datas)
            {
                detailItem+=1;

                bdm_tot_amt=bdm_tot_amt.add(new BigDecimal(data.getAmt()));
                bdm_tot_distriamt=bdm_tot_distriamt.add(new BigDecimal(data.getDistriAmt()));

                DCP_PStockInCreateReq.level1Elm lv1=dcp_pStockInCreateReq.new level1Elm();
                lv1.setAmt(data.getAmt());
                lv1.setBaseQty(data.getBaseQty());
                lv1.setBaseUnit(data.getBaseUnit());
                lv1.setBatchNo(data.getBatchNo());
                lv1.setBsNo(data.getBsNo());
                lv1.setDistriAmt(data.getDistriAmt());
                lv1.setDistriPrice(data.getDistriPrice());
                lv1.setFeatureNo(data.getFeatureNo());
                lv1.setgDate("");
                lv1.setgTime("");
                lv1.setItem(detailItem+"");
                lv1.setMemo(data.getMemo());
                lv1.setMulQty(data.getMulQty());
                lv1.setoItem(data.getOItem());
                lv1.setPluNo(data.getPluNo());
                lv1.setPqty(data.getPqty());
                lv1.setPrice(data.getPrice());
                lv1.setProdDate(data.getProdDate());
                lv1.setPunit(data.getPunit());
                lv1.setScrapQty(data.getScrapQty());
                lv1.setTaskQty(data.getTaskQty());
                lv1.setUnitRatio(data.getUnitRatio());
                lv1.setWarehouse(data.getWarehouse());
                lv1.setMaterial(new ArrayList<>());

                try
                {
                    //查询原料
                    DCP_GoodsBomQueryReq dcp_goodsBomQueryReq=new DCP_GoodsBomQueryReq();
                    //对象拷贝
                    BeanUtil.copyProperties(req, dcp_goodsBomQueryReq);
                    dcp_goodsBomQueryReq.setServiceId("DCP_GoodsBomQuery");
                    DCP_GoodsBomQueryReq.levelElm bom_request=dcp_goodsBomQueryReq.new levelElm();
                    bom_request.setBomType("0");//bom类型 0-完工入库 1-转换合并 2-组合拆解
                    bom_request.setShopId(req.getShopId());
                    List<DCP_GoodsBomQueryReq.level1Elm> bomPlus=new ArrayList<>();
                    DCP_GoodsBomQueryReq.level1Elm bom_lv1=dcp_goodsBomQueryReq.new level1Elm();
                    bom_lv1.setPluNo(data.getPluNo());
                    bom_lv1.setProdUnit(data.getPunit());
                    bomPlus.add(bom_lv1);
                    bom_request.setPluList(bomPlus);
                    dcp_goodsBomQueryReq.setRequest(bom_request);
                    DCP_GoodsBomQuery dcp_goodsBomQuery=new DCP_GoodsBomQuery();
                    dcp_goodsBomQuery.setDao(this.dao);
                    DCP_GoodsBomQueryRes dcp_goodsBomQueryRes=dcp_goodsBomQuery.processJson(dcp_goodsBomQueryReq);
                    if (dcp_goodsBomQueryRes.isSuccess())
                    {
                        List<DCP_PStockInCreateReq.level2Elm> material=new ArrayList<>();
                        int materialItem=0;
                        for (DCP_GoodsBomQueryRes.level1Elm ResData : dcp_goodsBomQueryRes.getDatas())
                        {
                            for (DCP_GoodsBomQueryRes.level2Elm ResDataLV2 : ResData.getMaterialList())
                            {
                                materialItem+=1;
                                DCP_PStockInCreateReq.level2Elm lv2=dcp_pStockInCreateReq.new level2Elm();

                                //重算原料数量
                                BigDecimal bdm_ma_pqty=new BigDecimal(lv1.getPqty()).multiply(new BigDecimal(ResDataLV2.getMaterial_rawMaterialBaseQty())).multiply(new BigDecimal("1").add(new BigDecimal(ResDataLV2.getLossRate()).multiply(new BigDecimal("0.01")))).divide(new BigDecimal(ResDataLV2.getMaterial_finalProdBaseQty()), Convert.toInt(ResDataLV2.getMaterial_unitUdLength(),2), BigDecimal.ROUND_HALF_UP);
                                lv2.setMaterial_pqty(bdm_ma_pqty.toPlainString());
                                lv2.setIsBuckle(ResDataLV2.getIsBuckle());
                                lv2.setMaterial_prodDate("");
                                lv2.setMaterial_batchNo("");
                                lv2.setMaterial_distriPrice(ResDataLV2.getMaterial_distriPrice());
                                lv2.setMaterial_featureNo(" ");
                                lv2.setMaterial_finalProdBaseQty(ResDataLV2.getMaterial_finalProdBaseQty());
                                lv2.setMaterial_pluNo(ResDataLV2.getMaterial_pluNo());
                                lv2.setMaterial_price(ResDataLV2.getMaterial_price());
                                lv2.setMaterial_rawMaterialBaseQty(ResDataLV2.getMaterial_rawMaterialBaseQty());
                                lv2.setMaterial_unitRatio(ResDataLV2.getMaterial_unitRatio());
                                lv2.setmItem(lv1.getItem());
                                lv2.setMaterial_item(materialItem+"");
                                lv2.setMaterial_warehouse(req.getRequest().getMaterialWarehouseNo());
                                lv2.setMaterial_punit(ResDataLV2.getMaterial_unit());
                                lv2.setMaterial_baseUnit(ResDataLV2.getMaterial_baseUnit());
                                lv2.setMaterial_amt(new BigDecimal(lv2.getMaterial_pqty()).multiply(new BigDecimal(lv2.getMaterial_price())).setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString());
                                lv2.setMaterial_distriAmt(new BigDecimal(lv2.getMaterial_pqty()).multiply(new BigDecimal(lv2.getMaterial_distriPrice())).setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString());

                                //换算
                                Map<String, Object> map_base=PosPub.getBaseQty(this.dao, req.geteId(), lv2.getMaterial_pluNo(), lv2.getMaterial_punit(), lv2.getMaterial_pqty());
                                String v_baseQty=map_base.get("baseQty").toString();
                                lv2.setMaterial_baseQty(new BigDecimal(v_baseQty).setScale(Convert.toInt(ResDataLV2.getMaterial_baseUnitUdLength(),2),BigDecimal.ROUND_HALF_UP).toPlainString());

                                material.add(lv2);
                            }
                        }
                        //查询返回的原料列表
                        lv1.setMaterial(material);
                    }
                }
                catch (Exception e)
                {
                    //暂不处理
                }
                request.getDatas().add(lv1);
            }
            request.setTotAmt(bdm_tot_amt.toPlainString());
            request.setTotCqty(bdm_tot_cqty+"");
            request.setTotDistriAmt(bdm_tot_distriamt.toPlainString());

            dcp_pStockInCreateReq.setRequest(request);
            DCP_PStockInCreateRes dcp_pStockInCreateRes=new DCP_PStockInCreateRes();
            DCP_PStockInCreate dcp_pStockInCreate=new DCP_PStockInCreate();
            dcp_pStockInCreate.setDao(this.dao);
            dcp_pStockInCreate.processDUID(dcp_pStockInCreateReq,dcp_pStockInCreateRes);
            if (dcp_pStockInCreateRes.isSuccess())
            {
                try
                {
                    //
                    DCP_PStockInProcessReq dcp_pStockInProcessReq=new DCP_PStockInProcessReq();
                    //对象拷贝
                    BeanUtil.copyProperties(req, dcp_pStockInProcessReq);
                    dcp_pStockInProcessReq.setServiceId("DCP_PStockInProcess");

                    DCP_PStockInProcessReq.levelElm process_request=dcp_pStockInProcessReq.new levelElm();
                    process_request.setPStockInNo(dcp_pStockInCreateRes.getPStockInNo());
                    dcp_pStockInProcessReq.setRequest(process_request);

                    DCP_PStockInProcessRes dcp_pStockInProcessRes=new DCP_PStockInProcessRes();
                    DCP_PStockInProcess dcp_pStockInProcess=new DCP_PStockInProcess();
                    dcp_pStockInProcess.setDao(this.dao);
                    dcp_pStockInProcess.processDUID(dcp_pStockInProcessReq,dcp_pStockInProcessRes);
                    if (dcp_pStockInProcessRes.isSuccess())
                    {
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("服务执行成功");
                    }
                    else
                    {
                        res.setSuccess(false);
                        res.setServiceStatus("100");
                        res.setServiceDescription(dcp_pStockInProcessRes.getServiceDescription());
                    }
                }
                catch (Exception e)
                {
                    res.setSuccess(false);
                    res.setServiceStatus("100");
                    res.setServiceDescription(e.getMessage());
                }
            }
            else
            {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription(dcp_pStockInCreateRes.getServiceDescription());
            }
        }
        catch (Exception e)
        {
            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            pw=null;
            errors=null;

            res.setSuccess(false);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行失败"+ e.getMessage()+"\r\n" + errors.toString());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PStockInTaskCompleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PStockInTaskCompleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PStockInTaskCompleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PStockInTaskCompleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(req.getRequest().getDocType()))
        {
            errMsg.append("docType不能为空 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getOfNo()))
        {
            errMsg.append("ofNo不能为空 ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getPTemplateNo()))
        {
            errMsg.append("pTemplateNo不能为空 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getWarehouse()))
        {
            errMsg.append("warehouse不能为空 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getMaterialWarehouseNo()))
        {
            errMsg.append("materialWarehouseNo不能为空 ");
            isFail = true;
        }


        List<DCP_PStockInTaskCompleteReq.level2> datas = req.getRequest().getDatas();

        if(datas==null || datas.size()==0)
        {
            errMsg.append("dataList必须有记录 ");
            isFail = true;
        }
        else
        {
            for (DCP_PStockInTaskCompleteReq.level2 level2 : datas)
            {
                if (Check.Null(level2.getOItem()))
                {
                    errMsg.append("oItem不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getPluNo()))
                {
                    errMsg.append("pluNo不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getPrice()))
                {
                    errMsg.append("price不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getDistriPrice()))
                {
                    errMsg.append("distriPrice不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getMulQty()))
                {
                    errMsg.append("mulQty不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getBaseUnit()))
                {
                    errMsg.append("baseUnit不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getBaseQty()))
                {
                    errMsg.append("baseQty不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getUnitRatio()))
                {
                    errMsg.append("unitRatio不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getPqty()))
                {
                    errMsg.append("pqty不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getAmt()))
                {
                    errMsg.append("amt不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getDistriAmt()))
                {
                    errMsg.append("distriAmt不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getScrapQty()))
                {
                    errMsg.append("scrapQty不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getTaskQty()))
                {
                    errMsg.append("taskQty不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getPunit()))
                {
                    errMsg.append("punit不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getWarehouse()))
                {
                    errMsg.append("warehouse不能为空 ");
                    isFail = true;
                }
                if (Check.Null(level2.getFeatureNo()))
                {
                    level2.setFeatureNo(" ");
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
    protected TypeToken<DCP_PStockInTaskCompleteReq> getRequestType()
    {
        return new TypeToken<DCP_PStockInTaskCompleteReq>(){};
    }

    @Override
    protected DCP_PStockInTaskCompleteRes getResponseType()
    {
        return new DCP_PStockInTaskCompleteRes();
    }



}
