package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.MES_SortDataAddReq;
import com.dsc.spos.json.cust.res.MES_SortDataAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * MES分拣底稿单
 */
public class MES_SortDataAdd extends SPosAdvanceService<MES_SortDataAddReq, MES_SortDataAddRes>
{
    Logger logger = LogManager.getLogger(MES_SortDataAdd.class.getName());

    @Override
    protected void processDUID(MES_SortDataAddReq req, MES_SortDataAddRes res) throws Exception
    {

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String bDate=req.getRequest().getBDate();
        if (bDate == null || bDate.length()!=8)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "bDate必须是8位的日期格式！");
        }

        //数据库入参：带杠的Date格式解析
        bDate=bDate.substring(0,4)+"-"+bDate.substring(4,6) +"-"+bDate.substring(6,8);


        String sql="select * from MES_SORTINGDATA where EID='"+req.getRequest().getEId()+"' " +
                "and ORGANIZATIONNO='"+req.getRequest().getOrganizationNo()+"' and DOCNO='"+req.getRequest().getDocNo()+"' " ;

        List<Map<String, Object>>  data=this.doQueryData(sql, null);

        if (data != null && data.size()>0)
        {
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已经存在！");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setDoc_no(data.get(0).get("DOCNO").toString());
            res.setOrg_no(data.get(0).get("ORGANIZATIONNO").toString());
            return;
        }


        //
        List<MES_SortDataAddReq.level2Elm> dataList=req.getRequest().getDataList();
        int detail_item=0;
        for (MES_SortDataAddReq.level2Elm level2Elm : dataList)
        {
            detail_item+=1;

            //
            String rDate=level2Elm.getRDate();
            if (rDate == null || rDate.length()!=8)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "rDate必须是8位的日期格式！");
            }
            rDate=level2Elm.getRDate().substring(0,4)+"-"+level2Elm.getRDate().substring(4,6) +"-"+level2Elm.getRDate().substring(6,8);


            //MES_SORTDATADETAIL
            String[] columnsName_MES_SORTDATADETAIL = {
                    "EID","ORGANIZATIONNO","DOCNO","ITEM",
                    "PLUNO","FEATURENO","UNIT","QTY","RDATE","SHIPPEDQTY","RESIDUEQTY","TRANSFEROUT",
                    "TRANSFERIN","ACTUALALLOCATION","PRICE","AMT","TAXRATE","TAXAMOUNT",
                    "SOURCETYPE","SOURCENO","OITEM","OCOMPANY","ERPITEM"
            };

            DataValue[] insValue_MES_SORTDATADETAIL = new DataValue[]
                    {
                            new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getDocNo(), Types.VARCHAR),
                            new DataValue(detail_item, Types.INTEGER),
                            new DataValue(level2Elm.getPluNo(), Types.VARCHAR),
                            new DataValue(level2Elm.getFeatureNo(), Types.VARCHAR),
                            new DataValue(level2Elm.getUnit(), Types.VARCHAR),
                            new DataValue(level2Elm.getAQty(), Types.VARCHAR),
                            new DataValue(rDate, Types.DATE),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue(level2Elm.getTransferOut(), Types.VARCHAR),
                            new DataValue(level2Elm.getTransferIn(), Types.VARCHAR),
                            new DataValue(level2Elm.getAQty(), Types.VARCHAR),
                            new DataValue(level2Elm.getPrice(), Types.VARCHAR),
                            new DataValue(level2Elm.getAmt(), Types.VARCHAR),
                            new DataValue(level2Elm.getTaxRate(), Types.VARCHAR),
                            new DataValue(level2Elm.getTaxAmount(), Types.VARCHAR),
                            new DataValue(level2Elm.getSourceType(), Types.VARCHAR),
                            new DataValue(level2Elm.getSourceNo(), Types.VARCHAR),
                            new DataValue(level2Elm.getOItem(), Types.VARCHAR),
                            new DataValue(level2Elm.getOCompany(), Types.VARCHAR),
                            new DataValue(level2Elm.getItem(), Types.VARCHAR)
                    };
            InsBean ib_MES_SORTDATADETAIL = new InsBean("MES_SORTDATADETAIL", columnsName_MES_SORTDATADETAIL);
            ib_MES_SORTDATADETAIL.addValues(insValue_MES_SORTDATADETAIL);
            this.addProcessData(new DataProcessBean(ib_MES_SORTDATADETAIL));
        }

        //单头MES_SORTINGDATA
        String[] columnsName_MES_SORTINGDATA = {
                "EID","ORGANIZATIONNO","DOCNO","REQUIRENO",
                "BDATE","RTAMPLATENO","DEPARTMENT","NAME","PHONE",
                "ADDRESS","SDATE","STIME"
        };

        DataValue[] insValue_MES_SORTINGDATA = new DataValue[]
                {
                        new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getDocNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getRequireNo(), Types.VARCHAR),
                        new DataValue(bDate, Types.DATE),
                        new DataValue(req.getRequest().getRTamplateNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getDepartment(), Types.VARCHAR),
                        new DataValue(req.getRequest().getName(), Types.VARCHAR),
                        new DataValue(req.getRequest().getPhone(), Types.VARCHAR),
                        new DataValue(req.getRequest().getAddress(), Types.VARCHAR),
                        new DataValue(req.getRequest().getSDate(), Types.VARCHAR),
                        new DataValue(req.getRequest().getSTime(), Types.VARCHAR)
                };
        InsBean ib_MES_SORTINGDATA = new InsBean("MES_SORTINGDATA", columnsName_MES_SORTINGDATA);
        ib_MES_SORTINGDATA.addValues(insValue_MES_SORTINGDATA);
        this.addProcessData(new DataProcessBean(ib_MES_SORTINGDATA));

        this.doExecuteDataToDB();

        logger.info("\n*********MES_SortDataAdd MES ERP 下发分拣底稿单成功erpno="+req.getRequest().getDocNo()+"************\n");

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        res.setDoc_no(req.getRequest().getDocNo());
        res.setOrg_no(req.getRequest().getOrganizationNo());
    }

    @Override
    protected List<InsBean> prepareInsertData(MES_SortDataAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(MES_SortDataAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(MES_SortDataAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(MES_SortDataAddReq req) throws Exception
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
            if (Check.Null(req.getRequest().getDocNo()))
            {
                isFail = true;
                errMsg.append("docNo不能为空,");
            }
            if (Check.Null(req.getRequest().getBDate()))
            {
                isFail = true;
                errMsg.append("bDate不能为空,");
            }
            if (Check.Null(req.getRequest().getRequireNo()))
            {
                isFail = true;
                errMsg.append("requireNo不能为空,");
            }
            if (Check.Null(req.getRequest().getRTamplateNo()))
            {
                isFail = true;
                errMsg.append("rTamplateNo不能为空,");
            }
            if (Check.Null(req.getRequest().getDepartment()))
            {
                isFail = true;
                errMsg.append("department不能为空,");
            }

            List<MES_SortDataAddReq.level2Elm> dataList=req.getRequest().getDataList();

            if (dataList == null || dataList.size()==0)
            {
                isFail = true;
                errMsg.append("dataList不能为空,");
            }
            else
            {
                for (MES_SortDataAddReq.level2Elm detailData : dataList)
                {
                    if (Check.Null(detailData.getItem()))
                    {
                        isFail = true;
                        errMsg.append("item不能为空,");
                    }

                    if (Check.Null(detailData.getPluNo()))
                    {
                        isFail = true;
                        errMsg.append("pluNo不能为空,");
                    }
                    if (Check.Null(detailData.getAQty()))
                    {
                        isFail = true;
                        errMsg.append("aQty不能为空,");
                    }
                    if (Check.Null(detailData.getAmt()))
                    {
                        isFail = true;
                        errMsg.append("amt不能为空,");
                    }
                    if (Check.Null(detailData.getFeatureNo()))
                    {
                        detailData.setFeatureNo(" ");
                    }
                    if (Check.Null(detailData.getUnit()))
                    {
                        isFail = true;
                        errMsg.append("unit不能为空,");
                    }
//                    if (Check.Null(detailData.getOCompany()))
//                    {
//                        isFail = true;
//                        errMsg.append("oCompany不能为空,");
//                    }
//                    if (Check.Null(detailData.getOItem()))
//                    {
//                        isFail = true;
//                        errMsg.append("oItem不能为空,");
//                    }
//                    if (Check.Null(detailData.getSourceType()))
//                    {
//                        isFail = true;
//                        errMsg.append("sourceType不能为空,");
//                    }
//                    if (Check.Null(detailData.getSourceNo()))
//                    {
//                        isFail = true;
//                        errMsg.append("sourceNo不能为空,");
//                    }
                    if (Check.Null(detailData.getPrice()))
                    {
                        isFail = true;
                        errMsg.append("price不能为空,");
                    }
                    if (Check.Null(detailData.getRDate()))
                    {
                        isFail = true;
                        errMsg.append("rDate不能为空,");
                    }
                    if (Check.Null(detailData.getTaxAmount()))
                    {
                        isFail = true;
                        errMsg.append("taxAmount不能为空,");
                    }
                    if (Check.Null(detailData.getTaxRate()))
                    {
                        isFail = true;
                        errMsg.append("taxRate不能为空,");
                    }
                    if (Check.Null(detailData.getTransferIn()))
                    {
                        isFail = true;
                        errMsg.append("transferIn不能为空,");
                    }
                    if (Check.Null(detailData.getTransferOut()))
                    {
                        isFail = true;
                        errMsg.append("transferOut不能为空,");
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
    protected TypeToken<MES_SortDataAddReq> getRequestType()
    {
        return new TypeToken<MES_SortDataAddReq>(){};
    }

    @Override
    protected MES_SortDataAddRes getResponseType()
    {
        return new MES_SortDataAddRes();
    }




}
