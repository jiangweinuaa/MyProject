package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ShopDateSaleQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopDateSaleQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ShopDateSaleQuery extends SPosBasicService<DCP_ShopDateSaleQueryReq, DCP_ShopDateSaleQueryRes>
{

    @Override
    protected boolean isVerifyFail(DCP_ShopDateSaleQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
        }
        else
        {
            if(req.getRequest().getDataList()==null || req.getRequest().getDataList().size()==0)
            {
                isFail = true;
                errMsg.append("dataList不能为空 ");
            }
            else
            {
                for (DCP_ShopDateSaleQueryReq.level2Elm level2Elm : req.getRequest().getDataList())
                {
                    if (Check.Null(level2Elm.getSDate()))
                    {
                        isFail = true;
                        errMsg.append("sDate不能为空 ");
                    }
                }
            }

            if(req.getRequest().getPluList()==null || req.getRequest().getPluList().size()==0)
            {
                isFail = true;
                errMsg.append("pluList不能为空 ");
            }
            else
            {
                for (DCP_ShopDateSaleQueryReq.levelPluElm level2Elm : req.getRequest().getPluList())
                {
                    if (Check.Null(level2Elm.getItem()))
                    {
                        isFail = true;
                        errMsg.append("item不能为空 ");
                    }
                    if (Check.Null(level2Elm.getPluNo()))
                    {
                        isFail = true;
                        errMsg.append("pluNo不能为空 ");
                    }
                    if (Check.Null(level2Elm.getBaseUnit()))
                    {
                        isFail = true;
                        errMsg.append("baseUnit不能为空 ");
                    }
                    if (Check.Null(level2Elm.getPUnit()))
                    {
                        isFail = true;
                        errMsg.append("pUnit不能为空 ");
                    }
                    if (Check.Null(level2Elm.getMulQty()))
                    {
                        isFail = true;
                        errMsg.append("mulQty不能为空 ");
                    }
                    if (Check.Null(level2Elm.getPUnitUdLength()))
                    {
                        isFail = true;
                        errMsg.append("pUnitUdLength不能为空 ");
                    }
                    if (Check.Null(level2Elm.getUnitRatio()))
                    {
                        isFail = true;
                        errMsg.append("unitRatio不能为空 ");
                    }
                }
            }

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ShopDateSaleQueryReq> getRequestType()
    {
        return new TypeToken<DCP_ShopDateSaleQueryReq>(){};
    }

    @Override
    protected DCP_ShopDateSaleQueryRes getResponseType()
    {
        return new DCP_ShopDateSaleQueryRes();
    }

    @Override
    protected DCP_ShopDateSaleQueryRes processJson(DCP_ShopDateSaleQueryReq req) throws Exception
    {
        DCP_ShopDateSaleQueryRes res=this.getResponseType();

        res.setDatas(res.new level1Elm());
        res.getDatas().setDataList(new ArrayList<>());

        //
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);

        //以入参商品和日期纬度返回
        for (int i = 0; i < req.getRequest().getPluList().size(); i++)
        {
            DCP_ShopDateSaleQueryRes.level2Elm level2Elm=res.new level2Elm();

            level2Elm.setBaseUnit(req.getRequest().getPluList().get(i).getBaseUnit());
            level2Elm.setBaseUnitName(req.getRequest().getPluList().get(i).getBaseUnitName());
            level2Elm.setFeatureName(req.getRequest().getPluList().get(i).getFeatureName());
            level2Elm.setFeatureNo(req.getRequest().getPluList().get(i).getFeatureNo());
            level2Elm.setItem(req.getRequest().getPluList().get(i).getItem());
            level2Elm.setPluName(req.getRequest().getPluList().get(i).getPluName());
            level2Elm.setPluNo(req.getRequest().getPluList().get(i).getPluNo());
            level2Elm.setPUnit(req.getRequest().getPluList().get(i).getPUnit());
            level2Elm.setPUnitName(req.getRequest().getPluList().get(i).getPUnitName());
            level2Elm.setSaleList(new ArrayList<>());

            int pUnitUdLength=Integer.valueOf(req.getRequest().getPluList().get(i).getPUnitUdLength());
            int mulQty=Integer.valueOf(req.getRequest().getPluList().get(i).getMulQty());
            BigDecimal unitRatio=new BigDecimal(req.getRequest().getPluList().get(i).getUnitRatio());

            String earliestSaleTime="";
            String lastestSaleTime="";
            BigDecimal adviceQty=new BigDecimal("0");
            String averageQty="0";

            BigDecimal bdm_sum_qty=new BigDecimal("0");

            for (int i1 = 0; i1 < req.getRequest().getDataList().size(); i1++)
            {
                DCP_ShopDateSaleQueryRes.level3Elm level3Elm=res.new level3Elm();
                level3Elm.setSDate(req.getRequest().getDataList().get(i1).getSDate());
                //给默认值
                level3Elm.setDayEarliestSaleTime("");
                level3Elm.setDayLastestSaleTime("");
                level3Elm.setSaleQty("0");

                //
                if (getQData != null && getQData.size()>0)
                {
                    //如果有销量，就给值
                    List<Map<String, Object>> getQData_plu_sdate=getQData.stream().filter(p->p.get("PLUNO").toString().equals(level2Elm.getPluNo()) && p.get("BDATE").toString().equals(level3Elm.getSDate())).collect(Collectors.toList());
                    if (getQData_plu_sdate != null && getQData_plu_sdate.size()>0)
                    {
                        level3Elm.setDayEarliestSaleTime(getQData_plu_sdate.get(0).get("MINTIME").toString());
                        level3Elm.setDayLastestSaleTime(getQData_plu_sdate.get(0).get("MAXTIME").toString());
                        level3Elm.setSaleQty(getQData_plu_sdate.get(0).get("SUM_BASEQTY").toString());

                        //取最小
                        if (earliestSaleTime.equals(""))
                        {
                            earliestSaleTime=level3Elm.getDayEarliestSaleTime();
                        }
                        else
                        {
                            earliestSaleTime=PosPub.FillStr(Math.min(Integer.valueOf(earliestSaleTime),Integer.valueOf(level3Elm.getDayEarliestSaleTime()))+"",6,"0",true);
                        }
                        //取最大
                        if (lastestSaleTime.equals(""))
                        {
                            lastestSaleTime=level3Elm.getDayLastestSaleTime();
                        }
                        else
                        {
                            lastestSaleTime=PosPub.FillStr(Math.max(Integer.valueOf(lastestSaleTime),Integer.valueOf(level3Elm.getDayLastestSaleTime()))+"",6,"0",true);
                        }
                    }
                }

                //累加
                bdm_sum_qty=bdm_sum_qty.add(new BigDecimal(level3Elm.getSaleQty()));

                level2Elm.getSaleList().add(level3Elm);
            }

            level2Elm.setEarliestSaleTime(earliestSaleTime);
            level2Elm.setLastestSaleTime(lastestSaleTime);
            //多日期，取平均
            averageQty=bdm_sum_qty.divide(new BigDecimal(req.getRequest().getDataList().size()),pUnitUdLength,BigDecimal.ROUND_HALF_UP).toPlainString();
            level2Elm.setAverageQty(averageQty);


            //需要生产100个，倍量为12
            //100/12=8.333   往上进为9
            //建议生产量=9*12=108，需按小数位数保留
            //倍量为0不需要考虑倍量生产
            if (unitRatio.compareTo(BigDecimal.ZERO)==0)
            {
                unitRatio=new BigDecimal("1");
            }
            if (mulQty == 0)
            {
                mulQty=1;
            }

            //向上取整：平均量/转换率/倍量
            int i_over=(int) Math.ceil(new BigDecimal(averageQty).divide(unitRatio,8,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(mulQty),8,BigDecimal.ROUND_HALF_UP).doubleValue());

            //建议量
            adviceQty=new BigDecimal(mulQty).multiply(new BigDecimal(i_over)).setScale(pUnitUdLength,BigDecimal.ROUND_HALF_UP);

            level2Elm.setAdviceQty(adviceQty.toPlainString());
            res.getDatas().getDataList().add(level2Elm);
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {


    }

    @Override
    protected String getQuerySql(DCP_ShopDateSaleQueryReq req) throws Exception
    {
        StringBuffer sqlbuf=new StringBuffer();

        //日期
        String[] strs=new String[req.getRequest().getDataList().size()];
        for (int i = 0; i < req.getRequest().getDataList().size(); i++)
        {
            strs[i]=req.getRequest().getDataList().get(i).getSDate();
        }
        String sDate_Str = PosPub.getArrayStrSQLIn(strs);


        //品号
        String[] strs_plu=new String[req.getRequest().getPluList().size()];
        for (int i = 0; i < req.getRequest().getPluList().size(); i++)
        {
            strs_plu[i]=req.getRequest().getPluList().get(i).getPluNo();
        }
        String sPlu_Str = PosPub.getArrayStrSQLIn(strs_plu);

        sqlbuf.append("select distinct a.eid,a.shopid,a.pluno,a.bdate,min(a.stime) over(partition by a.eid,a.shopid,a.pluno,a.bdate) MINTIME ,max(a.stime) over(partition by a.eid,a.shopid,a.pluno,a.bdate) MAXTIME ,sum(a.baseqty) over (partition by a.eid,a.shopid,a.pluno,a.bdate) SUM_BASEQTY " +
                              "from DCP_SALE_DETAIL a " +
                              "where a.eid='"+req.geteId()+"' " +
                              "and a.shopid='"+req.getShopId()+"' " +
                              "and pluno in ("+sPlu_Str+") " +
                              "and bdate in ("+sDate_Str+") " +
                              "order by a.eid,a.shopid,a.pluno,a.bdate ");


        return sqlbuf.toString();

    }


}
