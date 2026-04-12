package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_HolidayGoodsEnableReq;
import com.dsc.spos.json.cust.res.DCP_HolidayGoodsEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class DCP_HolidayGoodsEnable extends SPosAdvanceService<DCP_HolidayGoodsEnableReq, DCP_HolidayGoodsEnableRes> {
    @Override
    protected void processDUID(DCP_HolidayGoodsEnableReq req, DCP_HolidayGoodsEnableRes res) throws Exception {

        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId = req.geteId();
        String billNo=req.getRequest().getBillNo();//活动编号，修改时不可改
        String oprType = req.getRequest().getOprType();//操作类型：1-启用 2-禁用
        String langType = req.getLangType();
        if (Check.Null(langType))
        {
            langType = "zh_CN";
        }

        String sql = "select * from dcp_holidaygoods where EID='"+eId+"' and BILLNO='"+billNo+"'";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData==null||getQData.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")不存在！");
        }

        String GOODSSYNC = getQData.get(0).getOrDefault("GOODSSYNC","").toString();
        String status = getQData.get(0).getOrDefault("STATUS","").toString();
        String updateStatus = "";

        String beginDate_DB = getQData.get(0).getOrDefault("BEGINDATE","").toString();//yyyy-mm-dd hh:mm:ss
        String beginDate = "";
        if(beginDate_DB!=null&&beginDate_DB.length()==19)
        {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(beginDate_DB);
            beginDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);
        }

        String endDate_DB = getQData.get(0).getOrDefault("ENDDATE","").toString();//yyyy-mm-dd hh:mm:ss
        String endDate = "";
        if(endDate_DB!=null&&endDate_DB.length()==19)
        {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate_DB);
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);
        }

        /*if (!GOODSSYNC.equals("N"))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")商品同步状态("+GOODSSYNC+")不等于N,不可操作！");
        }*/

        if ("1".equals(oprType))
        {
            if (status.equals("100"))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")已启用状态("+status+")无须再操作启用！");
            }
            if (Check.Null(beginDate)||Check.Null(endDate))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")生效日期为空！");
            }
            updateStatus = "100";
            //同一商品在已生效的节日活动规划单中，生效日期范围不可交叉；
            //返回 商品编码 商品名称  节日活动规划单号 提醒前端。

            StringBuffer sqlBuffer = new StringBuffer("");
            sql = "";

            sqlBuffer.append(" with p as (");
            sqlBuffer.append(" select a.eid,a.billno,b.pluno,bl.plu_name from dcp_holidaygoods a");
            sqlBuffer.append(" inner join dcp_holidaygoods_detail b on a.eid=b.eid and a.billno = b.billno");
            sqlBuffer.append(" left join dcp_goods_lang bl on b.eid = bl.eid and b.pluno=bl.pluno and bl.lang_type='"+langType+"' ");
            sqlBuffer.append(" where a.eid='"+eId+"' and a.billno='"+billNo+"' ");
            sqlBuffer.append(" )");

            sqlBuffer.append(" select a.eid,a.billno,b.pluno,bl.plu_name from dcp_holidaygoods a ");
            sqlBuffer.append(" inner join dcp_holidaygoods_detail b on a.eid=b.eid and a.billno = b.billno ");
            sqlBuffer.append(" left join dcp_goods_lang bl on b.eid = bl.eid and b.pluno=bl.pluno and bl.lang_type='zh_CN' ");
            sqlBuffer.append(" inner join p on b.eid=p.eid and b.pluno = p.pluno");

            //查询不交叉的活动编号(开始日期大于传入的结束日期，或者结束日期小于传入的开始日期)，
            sqlBuffer.append(" where a.eid='"+eId+"' and a.status='100' "
                    + " and  a.billno not in "
                    + " (select billno from dcp_holidaygoods where begindate >to_date('"+endDate+"','YYYY-MM-DD') "
                    + " or enddate<to_date('"+beginDate+"','YYYY-MM-DD'))");

            sql = sqlBuffer.toString();
            List<Map<String,Object>> getQData_PluNo = this.doQueryData(sql,null);
            if (getQData_PluNo!=null&&getQData_PluNo.isEmpty()==false)
            {
                Map<String,Boolean> condition = new HashMap<>();
                condition.put("PLUNO",true);

                List<Map<String,Object>> pluNoList = MapDistinct.getMap(getQData_PluNo,condition);
                StringBuffer errorMessBuffer = new StringBuffer("");
                for (Map<String,Object> oneData : pluNoList)
                {
                    String pluNo = oneData.getOrDefault("PLUNO","").toString();
                    String pluName = oneData.getOrDefault("PLU_NAME","").toString();
                    String error ="商品"+pluNo+"("+pluName+"),已存在于以下节日活动规划单号:";
                    for (Map<String,Object> oneData2 : getQData_PluNo)
                    {
                        String pluNo_detail = oneData2.getOrDefault("PLUNO","").toString();
                        if (pluNo_detail.isEmpty())
                        {
                            continue;
                        }
                        if (!pluNo_detail.equals(pluNo))
                        {
                            continue;
                        }
                        String billNo_detail = oneData2.getOrDefault("BILLNO","").toString();
                        error = error+billNo_detail+",";
                    }

                    errorMessBuffer.append(error+"<br>");

                }

                res.setSuccess(false);
                res.setServiceStatus("000");
                res.setServiceDescription(errorMessBuffer.toString());
                return;
            }



        }
        else  if ("2".equals(oprType))
        {
            if (status.equals("0"))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该活动编号("+billNo+")已禁用状态("+status+")无须再操作禁用！");
            }
            updateStatus = "0";
        }
        else
        {
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, "操作类型oprType="+oprType+"暂未实现！");
        }

        UptBean up1 = new UptBean("DCP_HOLIDAYGOODS");
        up1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        up1.addCondition("BILLNO", new DataValue(billNo,Types.VARCHAR));

        up1.addUpdateValue("STATUS", new DataValue(updateStatus,Types.VARCHAR));
        up1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
        up1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
        up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));
        this.addProcessData(new DataProcessBean(up1));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_HolidayGoodsEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_HolidayGoodsEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_HolidayGoodsEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_HolidayGoodsEnableReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String billNo=req.getRequest().getBillNo();//活动编号，修改时不可改


        if(Check.Null(billNo))
        {
            errMsg.append("活动编号billNo不能为空值 ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getOprType()))
        {
            errMsg.append("操作类型oprType不能为空值 ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        return isFail;

    }

    @Override
    protected TypeToken<DCP_HolidayGoodsEnableReq> getRequestType() {
        return new TypeToken<DCP_HolidayGoodsEnableReq>(){};
    }

    @Override
    protected DCP_HolidayGoodsEnableRes getResponseType() {
        return new DCP_HolidayGoodsEnableRes();
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
