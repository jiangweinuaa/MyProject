package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.MES_StockDetailCreateReq;
import com.dsc.spos.json.cust.res.MES_StockDetailCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ERP下发库存异动
 */
public class MES_StockDetailCreate extends SPosAdvanceService<MES_StockDetailCreateReq, MES_StockDetailCreateRes>
{

    Logger logger = LogManager.getLogger(MES_StockDetailCreate.class.getName());


    @Override
    protected void processDUID(MES_StockDetailCreateReq req, MES_StockDetailCreateRes res) throws Exception
    {

        res.setErrorList(new ArrayList<>());


        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        List<MES_StockDetailCreateReq.level1Elm> mesStockDetail=req.getRequest().getMesStockDetail();

        for (MES_StockDetailCreateReq.level1Elm lv : mesStockDetail)
        {
            //记录下来
            MES_StockDetailCreateRes.level1Elm elm=res.new level1Elm();
            elm.setEId(lv.getEId());
            elm.setOrganizationNo(lv.getOrganizationNo());
            elm.setBillType(lv.getBillType());
            elm.setDirect(lv.getDirect());
            elm.setErpBillNo(lv.getErpBillNo());
            elm.setErpSeq(lv.getErpSeq());
            elm.setErpItem(lv.getErpItem());
            try
            {
                String procedure="SP_DCP_STOCKCHANGE_V4";
                Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                inputParameter.put(1,lv.getEId());                                       //--企业ID
                inputParameter.put(2,lv.getOrganizationNo());                                    //--组织
                inputParameter.put(3,lv.getBillType());                  //--单据类型11
                inputParameter.put(4,lv.getErpBillNo());//--单据号
                inputParameter.put(5,lv.getErpItem());            //--单据行号
                inputParameter.put(6,lv.getErpSeq());            //--单据序号
                inputParameter.put(7,lv.getDirect());            //--异动方向 1=加库存 -1=减库存
                inputParameter.put(8,lv.getBDate());           //--营业日期 yyyyMMdd
                inputParameter.put(9,lv.getPluNo());//--品号
                inputParameter.put(10,lv.getFeatureNo());       //--特征码
                inputParameter.put(11,lv.getWarehouse());   //--仓库
                inputParameter.put(12,lv.getBatchNo());       //--批号
                inputParameter.put(13,lv.getLocation());       //--库位
                inputParameter.put(14,lv.getBaseUnit()); //--交易单位
                inputParameter.put(15,lv.getBaseQty());  //--交易数量
                inputParameter.put(16,lv.getBaseUnit()); //--基准单位
                inputParameter.put(17,lv.getBaseQty());        //--基准数量
                inputParameter.put(18,"1");     //--换算比例
                inputParameter.put(19,lv.getPrice());          //--零售价
                inputParameter.put(20,lv.getAmt());            //--零售金额
                inputParameter.put(21,lv.getPrice());    //--进货价
                inputParameter.put(22,lv.getAmt());      //--进货金额
                inputParameter.put(23,lv.getBDate());     //--入账日期 yyyy-MM-dd
                inputParameter.put(24,lv.getProdDate());      //--批号的生产日期 yyyy-MM-dd
                inputParameter.put(25,lv.getBDate());    //--单据日期yyyy-MM-dd
                inputParameter.put(26,"ERP直接下发异动");   //--异动原因
                inputParameter.put(27,"ERP直接下发异动");   //--异动描述
                inputParameter.put(28,lv.getUserId());                                //--操作员

                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                this.addProcessData(new DataProcessBean(pdb));

                this.doExecuteDataToDB();
            }
            catch (Exception ex)
            {
                //加入失败列表
                res.getErrorList().add(elm);
            }
        }

        //错误处理
        if (res.getErrorList()!=null && res.getErrorList().size()>0)
        {
            logger.info("\n*********MES_StockDetailCreate MES ERP 下发库存异动失败erpno="+mesStockDetail.get(0).getErpBillNo()+"_"+mesStockDetail.get(0).getErpSeq()+"************\n");

            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行失败");
            res.setDoc_no("");
            res.setOrg_no("");
        }
        else
        {
            logger.info("\n*********MES_StockDetailCreate MES ERP 下发库存异动成功erpno="+mesStockDetail.get(0).getErpBillNo()+"_"+mesStockDetail.get(0).getErpSeq()+"************\n");

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setDoc_no("");
            res.setOrg_no(mesStockDetail.get(0).getOrganizationNo());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(MES_StockDetailCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(MES_StockDetailCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(MES_StockDetailCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(MES_StockDetailCreateReq req) throws Exception
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

            List<MES_StockDetailCreateReq.level1Elm> mesStockDetail=req.getRequest().getMesStockDetail();

            if (mesStockDetail == null || mesStockDetail.size()==0)
            {
                isFail = true;
                errMsg.append("request不能为空 ");
            }
            else
            {
                for (MES_StockDetailCreateReq.level1Elm lv : mesStockDetail)
                {
                    if (Check.Null(lv.getEId()))
                    {
                        isFail = true;
                        errMsg.append("eId不能为空,");
                    }
                    if (Check.Null(lv.getOrganizationNo()))
                    {
                        isFail = true;
                        errMsg.append("organizationNo不能为空,");
                    }
                    if (Check.Null(lv.getBillType()))
                    {
                        isFail = true;
                        errMsg.append("billType不能为空,");
                    }
                    if (Check.Null(lv.getErpBillNo()))
                    {
                        isFail = true;
                        errMsg.append("erpBillNo不能为空,");
                    }
                    if (Check.Null(lv.getErpItem()))
                    {
                        isFail = true;
                        errMsg.append("erpItem不能为空,");
                    }
                    if (Check.Null(lv.getErpSeq()))
                    {
                      lv.setErpSeq("");
                    }

                    if (Check.Null(lv.getDirect()))
                    {
                        isFail = true;
                        errMsg.append("direct不能为空,");
                    }
                    if (Check.Null(lv.getBDate()))
                    {
                        isFail = true;
                        errMsg.append("bDate不能为空,");
                    }
                    if (Check.Null(lv.getAccountDate()))
                    {
                        isFail = true;
                        errMsg.append("accountDate不能为空,");
                    }
                    if (Check.Null(lv.getPluNo()))
                    {
                        isFail = true;
                        errMsg.append("pluNo不能为空,");
                    }
                    if (Check.Null(lv.getFeatureNo()))
                    {
                        lv.setFeatureNo(" ");
                    }
                    if (Check.Null(lv.getWarehouse()))
                    {
                        isFail = true;
                        errMsg.append("warehouse不能为空,");
                    }
                    if (Check.Null(lv.getBaseUnit()))
                    {
                        isFail = true;
                        errMsg.append("baseUnit不能为空,");
                    }
                    if (Check.Null(lv.getBaseQty()))
                    {
                        isFail = true;
                        errMsg.append("baseQty不能为空,");
                    }
                    if (Check.Null(lv.getPrice()))
                    {
                        lv.setPrice("0");
                    }
                    if (Check.Null(lv.getAmt()))
                    {
                        lv.setAmt("0");
                    }
                    if (Check.Null(lv.getUserId()))
                    {
                        isFail = true;
                        errMsg.append("userId不能为空,");
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
    protected TypeToken<MES_StockDetailCreateReq> getRequestType()
    {
        return new TypeToken<MES_StockDetailCreateReq>(){};
    }

    @Override
    protected MES_StockDetailCreateRes getResponseType()
    {
        return new MES_StockDetailCreateRes();
    }



}
