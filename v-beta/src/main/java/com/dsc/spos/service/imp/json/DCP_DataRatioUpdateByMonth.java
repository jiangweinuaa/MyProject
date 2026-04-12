package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DataRatioUpdateByMonthReq;
import com.dsc.spos.json.cust.res.DCP_DataRatioUpdateByMonthRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_DataRatioUpdateByMonth extends SPosAdvanceService<DCP_DataRatioUpdateByMonthReq, DCP_DataRatioUpdateByMonthRes> {
    @Override
    protected void processDUID(DCP_DataRatioUpdateByMonthReq req, DCP_DataRatioUpdateByMonthRes res) throws Exception {

        String eId = req.geteId();
        String opNo = req.getOpNO();
        String opName = req.getOpName();
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String d_year = req.getRequest().getD_year();
        String d_month = req.getRequest().getD_month();
        String dataRatio = req.getRequest().getDataRatio();
        String rangeType = req.getRequest().getRangeType();
        String[] columnsName = {"EID","SHOPID","D_YEAR","D_MONTH","DATARATIO","CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};

        if ("1".equals(rangeType))
        {
            DelBean db1 = new DelBean("DCP_DATARATIO");
            db1.addCondition("D_YEAR", new DataValue(d_year, Types.VARCHAR));
            db1.addCondition("d_month", new DataValue(d_month, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            String sql = " select * from DCP_ORG  where ORG_FORM='2' and EID='"+eId+"' ";
            List<Map<String,Object>> getShopList = this.doQueryData(sql,null);
            if (getShopList==null||getShopList.isEmpty())
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "范围类型为全部门店时，查询门店为空！");
            }
            for (Map<String,Object> par : getShopList)
            {
                String shopId = par.get("ORGANIZATIONNO").toString();

                DataValue[] columnsVal = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(d_year, Types.VARCHAR),
                        new DataValue(d_month, Types.VARCHAR),
                        new DataValue(dataRatio, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };

                InsBean ib1 = new InsBean("DCP_DATARATIO", columnsName);
                ib1.addValues(columnsVal);
                this.addProcessData(new DataProcessBean(ib1));
            }
        }
        else if ("2".equals(rangeType))
        {
            List<DataProcessBean> DPBList = new ArrayList<>();//先删后插，插入的列表放在删除列表之后
            for (DCP_DataRatioUpdateByMonthReq.LevelDataShop par : req.getRequest().getShopList())
            {
                String shopId = par.getShopId();
                DelBean db1 = new DelBean("DCP_DATARATIO");
                db1.addCondition("D_YEAR", new DataValue(d_year, Types.VARCHAR));
                db1.addCondition("d_month", new DataValue(d_month, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                DataValue[] columnsVal = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(d_year, Types.VARCHAR),
                        new DataValue(d_month, Types.VARCHAR),
                        new DataValue(dataRatio, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };

                InsBean ib1 = new InsBean("DCP_DATARATIO", columnsName);
                ib1.addValues(columnsVal);
                DPBList.add(new DataProcessBean(ib1));
            }

            for (DataProcessBean dpb : DPBList)
            {
                this.addProcessData(dpb);
            }

        }
        else
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "范围类型rangeType="+rangeType+"枚举值不对！");
        }

        this.doExecuteDataToDB();
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DataRatioUpdateByMonthReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DataRatioUpdateByMonthReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DataRatioUpdateByMonthReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DataRatioUpdateByMonthReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        /*if (Check.Null(req.getRequest().getShopId()))
        {
            errCt++;
            errMsg.append("shopId门店编码不能为空值，");
            isFail = true;
        }*/
        if (Check.Null(req.getRequest().getD_year()))
        {
            errCt++;
            errMsg.append("d_year年份不能为空值，");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getD_month()))
        {
            errCt++;
            errMsg.append("d_month月份不能为空值，");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getRangeType()))
        {
            errCt++;
            errMsg.append("rangeType范围类型不能为空值，");
            isFail = true;
        }
        if (!Check.Null(req.getRequest().getRangeType()))
        {
            if ("1".equals(req.getRequest().getRangeType()))
            {

            }
            else if ("2".equals(req.getRequest().getRangeType()))
            {
                if (req.getRequest().getShopList()==null||req.getRequest().getShopList().isEmpty())
                {
                    errCt++;
                    errMsg.append("范围类型是指定门店时，shopList门店列表不能为空");
                    isFail = true;
                }
                else
                {
                    for (DCP_DataRatioUpdateByMonthReq.LevelDataShop par : req.getRequest().getShopList())
                    {
                        if (Check.Null(par.getShopId()))
                        {
                            errCt++;
                            errMsg.append("shopId门店编码不能为空值，");
                            isFail = true;
                        }
                    }
                }

            }
            else
            {
                errCt++;
                errMsg.append("rangeType范围值不对，正确值为[1,2]，");
                isFail = true;
            }
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DataRatioUpdateByMonthReq> getRequestType() {
        return new TypeToken<DCP_DataRatioUpdateByMonthReq>(){};
    }

    @Override
    protected DCP_DataRatioUpdateByMonthRes getResponseType() {
        return new DCP_DataRatioUpdateByMonthRes();
    }
}
