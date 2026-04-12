package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_HolidayGoodsUpdateReq;
import com.dsc.spos.json.cust.req.DCP_HolidayGoodsUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_HolidayGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import javax.servlet.jsp.tagext.TryCatchFinally;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public  class DCP_HolidayGoodsUpdate extends SPosAdvanceService<DCP_HolidayGoodsUpdateReq, DCP_HolidayGoodsUpdateRes> {
    @Override
    protected void processDUID(DCP_HolidayGoodsUpdateReq req, DCP_HolidayGoodsUpdateRes res) throws Exception {

        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId = req.geteId();
        String oprType = req.getRequest().getOprType();//操作类型：1-创建 2-修改
        String companyId=req.getRequest().getCompanyId();//制单公司id
        String employeeId=req.getRequest().getEmployeeId();//经办员工id
        String billNo=req.getRequest().getBillNo();//活动编号，修改时不可改
        String billDate=req.getRequest().getBillDate();//规划日期YYYY-MM-DD
        String title=req.getRequest().getTitle();//活动主题
        String beginDate=req.getRequest().getBeginDate();//活动开始日期YYYY-MM-DD
        String endDate=req.getRequest().getEndDate();//活动截止日期YYYY-MM-DD
        String status=req.getRequest().getStatus();//状态：-1启用 100-已启用 0-已禁用
        List<DCP_HolidayGoodsUpdateReq.level2Elm> goodsList = req.getRequest().getGoodsList();

        StringBuffer goodsSyncBuffer = new StringBuffer();

        if (oprType.equals("1"))
        {
            if (CheckIsExist(eId,billNo,goodsSyncBuffer))
            {
                throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")已存在！");
            }
            status = "-1";//新增默认-1
            String[] columns_DCP_HOLIDAYGOODS =
                    {
                            "EID","BILLNO","BILLDATE","COMPANYID","EMPLOYEEID","DEPARTID","TITLE",
                            "BEGINDATE","ENDDATE","REMARK","STATUS","CREATEOPID","CREATEOPNAME","CREATETIME"
                    };
            DataValue[] insValue1 = null;

            insValue1 = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(billNo, Types.VARCHAR),
                    new DataValue(billDate, Types.DATE),
                    new DataValue(companyId, Types.VARCHAR),
                    new DataValue(employeeId, Types.VARCHAR),
                    new DataValue(req.getDefDepartNo(), Types.VARCHAR),
                    new DataValue(title, Types.VARCHAR),
                    new DataValue(beginDate, Types.DATE),
                    new DataValue(endDate, Types.DATE),
                    new DataValue(req.getRequest().getRemark(), Types.VARCHAR),
                    new DataValue(status, Types.VARCHAR),
                    new DataValue(req.getOpNO(), Types.VARCHAR),
                    new DataValue(req.getOpName(), Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE)
            };

            InsBean ib1 = new InsBean("DCP_HOLIDAYGOODS", columns_DCP_HOLIDAYGOODS);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增


            DelBean db1 = new DelBean("DCP_HOLIDAYGOODS_DETAIL");
            db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
            db1.addCondition("BILLNO", new DataValue(billNo,Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
            String[] columns_DCP_HOLIDAYGOODS_DETAIL =
                    {
                            "EID","BILLNO","PLUNO","SORTID","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
                    };
            for (level2Elm goods : goodsList)
            {
                String pluNo = goods.getPluNo();
                int sortId = goods.getSortId();
                DataValue[] insValue2 = null;

                insValue2 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(billNo, Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(sortId, Types.INTEGER),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(req.getOpName(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };

                InsBean ib2 = new InsBean("DCP_HOLIDAYGOODS_DETAIL", columns_DCP_HOLIDAYGOODS_DETAIL);
                ib2.addValues(insValue2);
                this.addProcessData(new DataProcessBean(ib2)); // 新增

            }


        }
        else if (oprType.equals("2"))
        {
            if (!CheckIsExist(eId,billNo,goodsSyncBuffer))
            {
                throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")不存在,无法修改！");
            }
            String GOODSSYNC = goodsSyncBuffer.toString();

            if (!GOODSSYNC.equals("N"))
            {
                throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")商品同步状态("+GOODSSYNC+")不等于N,不可修改！");
            }

            UptBean up1 = new UptBean("DCP_HOLIDAYGOODS");
            up1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
            up1.addCondition("BILLNO", new DataValue(billNo,Types.VARCHAR));


            up1.addUpdateValue("BILLDATE", new DataValue(billDate,Types.DATE));
            up1.addUpdateValue("COMPANYID", new DataValue(companyId,Types.VARCHAR));
            up1.addUpdateValue("EMPLOYEEID", new DataValue(employeeId,Types.VARCHAR));
            up1.addUpdateValue("DEPARTID", new DataValue(req.getDefDepartNo(),Types.VARCHAR));
            up1.addUpdateValue("TITLE", new DataValue(title,Types.VARCHAR));
            up1.addUpdateValue("BEGINDATE", new DataValue(beginDate,Types.DATE));
            up1.addUpdateValue("ENDDATE", new DataValue(endDate,Types.DATE));
            up1.addUpdateValue("REMARK", new DataValue(req.getRequest().getRemark(),Types.VARCHAR));
            //up1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
            up1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
            up1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
            up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));
            this.addProcessData(new DataProcessBean(up1));

            DelBean db1 = new DelBean("DCP_HOLIDAYGOODS_DETAIL");
            db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
            db1.addCondition("BILLNO", new DataValue(billNo,Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            String[] columns_DCP_HOLIDAYGOODS_DETAIL =
                    {
                            "EID","BILLNO","PLUNO","SORTID","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
                    };
            for (level2Elm goods : goodsList)
            {
                String pluNo = goods.getPluNo();
                int sortId = goods.getSortId();
                DataValue[] insValue2 = null;

                insValue2 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(billNo, Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(sortId, Types.INTEGER),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(req.getOpName(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };

                InsBean ib2 = new InsBean("DCP_HOLIDAYGOODS_DETAIL", columns_DCP_HOLIDAYGOODS_DETAIL);
                ib2.addValues(insValue2);
                this.addProcessData(new DataProcessBean(ib2)); // 新增

            }
        }
        else
        {
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, "操作类型oprType="+oprType+"暂未实现！");
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_HolidayGoodsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_HolidayGoodsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_HolidayGoodsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_HolidayGoodsUpdateReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String oprType = req.getRequest().getOprType();//操作类型：1-创建 2-修改
        String companyId=req.getRequest().getCompanyId();//制单公司id
        String employeeId=req.getRequest().getEmployeeId();//经办员工id
        String billNo=req.getRequest().getBillNo();//活动编号，修改时不可改
        String billDate=req.getRequest().getBillDate();//规划日期YYYY-MM-DD
        String title=req.getRequest().getTitle();//活动主题
        String beginDate=req.getRequest().getBeginDate();//活动开始日期YYYY-MM-DD
        String endDate=req.getRequest().getEndDate();//活动截止日期YYYY-MM-DD
        String status=req.getRequest().getStatus();//状态：-1启用 100-已启用 0-已禁用

        List<DCP_HolidayGoodsUpdateReq.level2Elm> goodsList = req.getRequest().getGoodsList();

        if(Check.Null(oprType))
        {
            errMsg.append("操作类型oprType不能为空值 ");
            isFail = true;
        }
        if(Check.Null(companyId))
        {
            errMsg.append("制单公司companyId不能为空值 ");
            isFail = true;
        }

        if(Check.Null(employeeId))
        {
            errMsg.append("经办员工employeeId不能为空值 ");
            isFail = true;
        }

        if(Check.Null(billNo))
        {
            errMsg.append("活动编号billNo不能为空值 ");
            isFail = true;
        }
        if(Check.Null(billDate))
        {
            errMsg.append("制单日期billDate不能为空值 ");
            isFail = true;
        }
        if(Check.Null(title))
        {
            errMsg.append("活动主题title不能为空值 ");
            isFail = true;
        }
        if(Check.Null(beginDate))
        {
            errMsg.append("活动开始日期beginDate不能为空值 ");
            isFail = true;
        }
        if(Check.Null(endDate))
        {
            errMsg.append("活动结束日期endDate不能为空值 ");
            isFail = true;
        }

        if(Check.Null(status))
        {
            errMsg.append("状态status不能为空值 ");
            isFail = true;
        }
        if(goodsList==null)
        {
            errMsg.append("商品列表goodsList不能为空值 ");
            isFail = true;
        }
        if (goodsList!=null&&goodsList.size()>0)
        {
            for (level2Elm goods :  goodsList)
            {
                String pluNo = goods.getPluNo();
                int sortId = goods.getSortId();
                if(Check.Null(pluNo))
                {
                    errMsg.append("商品编码pluNo不能为空值 ");
                    isFail = true;
                }

            }
        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (CheckDateFormat(billDate)==false)
        {
            errMsg.append("制单日期billDate格式不对， ");
            isFail = true;
        }
        if (CheckDateFormat(beginDate)==false)
        {
            errMsg.append("活动开始日期beginDate格式不对， ");
            isFail = true;
        }
        if (CheckDateFormat(endDate)==false)
        {
            errMsg.append("活动结束日期endDate格式不对， ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;

    }

    @Override
    protected TypeToken<DCP_HolidayGoodsUpdateReq> getRequestType() {
        return new TypeToken<DCP_HolidayGoodsUpdateReq>(){};
    }

    @Override
    protected DCP_HolidayGoodsUpdateRes getResponseType() {
        return new DCP_HolidayGoodsUpdateRes();
    }

    private boolean CheckIsExist (String eId,String billNo,StringBuffer goodsSync) throws  Exception
    {
        String sql = "select * from dcp_holidaygoods where EID='"+eId+"' and BILLNO='"+billNo+"'";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData!=null&&getQData.isEmpty()==false)
        {
            goodsSync.append(getQData.get(0).getOrDefault("GOODSSYNC","").toString());
            return  true;
        }
        return  false;
    }

    private boolean CheckDateFormat(String dateStr) throws  Exception
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date d1 = simpleDateFormat.parse(dateStr);
            if (d1!=null)
            {
                return true;
            }
        }
        catch (Exception e)
        {

        }
        return  false;
    }
}
