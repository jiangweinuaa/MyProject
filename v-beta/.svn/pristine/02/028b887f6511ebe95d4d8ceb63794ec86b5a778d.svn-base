package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.util.AddClosureArrayList;

import com.dsc.spos.json.cust.req.DCP_RCustomerQueryReq;
import com.dsc.spos.json.cust.res.DCP_RCustomerQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_RCustomerQuery extends SPosBasicService<DCP_RCustomerQueryReq, DCP_RCustomerQueryRes>
{

    @Override
    protected boolean isVerifyFail(DCP_RCustomerQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_RCustomerQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_RCustomerQueryReq>(){};
    }

    @Override
    protected DCP_RCustomerQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_RCustomerQueryRes();
    }

    @Override
    protected DCP_RCustomerQueryRes processJson(DCP_RCustomerQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        //查找Platform_RCustomer,Platform_SregisterHead,Platform_SregisterDetail三张表联合查询
        String sdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

        StringBuffer sb=new StringBuffer("");
        sb.append("select A.CustomerNo,A.CustomerName,A.MEMO,B.TerminalLicence,B.IsRegister,B.SDateTime BSDATETIME,B.MEMO BMEMO,C.RegisterType,C.Producttype,C.Scount,C.Bdate,C.Edate,C.SDateTime CSDATETIME,C.MEMO CMEMO,"
                          + " D.SDetailType DSDETAILTYPE,D.SDetailmodular DSDETAILMODULAR,D.Scount DSCOUNT "
                          + "  from Platform_RCustomer A left join Platform_SregisterHead B on A.CustomerNo=B.CustomerNo "
                          + " left join Platform_SregisterDetail C on B.CustomerNo=C.CustomerNo and B.TerminalLicence=C.TerminalLicence "
                          + " left join Platform_SDetailinfo D on C.CustomerNo=D.CustomerNo and C.TerminalLicence=D.TerminalLicence and C.PRODUCTTYPE=D.PRODUCTTYPE   ");
        if(req.getKeyTxt()!=null&&!req.getKeyTxt().isEmpty())
        {
            sb.append(" where A.CustomerNo like '%"+req.getKeyTxt()+"%' or A.CustomerName like '%"+req.getKeyTxt()+"%'  ");
        }

        String sql=sb.toString();
        List<Map<String, Object>> listcustomer=this.doQueryData(sql, null);
        DCP_RCustomerQueryRes res=new DCP_RCustomerQueryRes();
        if(listcustomer!=null&&!listcustomer.isEmpty())
        {
            res.setDatas(new ArrayList<DCP_RCustomerQueryRes.level1Elm>());
            //单头主键字段
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("CUSTOMERNO", true);
            //调用过滤函数
            List<Map<String, Object>> getQHeader=MapDistinct.getMap(listcustomer, condition);

            //单头主键字段
            Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查詢條件
            condition1.put("CUSTOMERNO", true);
            condition1.put("TERMINALLICENCE", true);
            //调用过滤函数
            List<Map<String, Object>> getcurstomhead=MapDistinct.getMap(listcustomer, condition1);

            //单头主键字段
            Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
            condition2.put("CUSTOMERNO", true);
            condition2.put("TERMINALLICENCE", true);
            condition2.put("PRODUCTTYPE", true);
            //调用过滤函数
            List<Map<String, Object>> getcurstomDetail=MapDistinct.getMap(listcustomer, condition2);

            for (Map<String, Object> maphed : getQHeader)
            {
                int dcpCount=0;//云中台数量
                int crmCount=0;//会员数量
                int posCount=0;//POS数量
                int smCount=0;//门店管理数量

                DCP_RCustomerQueryRes.level1Elm lv1=new DCP_RCustomerQueryRes.level1Elm();
                lv1.setCustomerNo(maphed.get("CUSTOMERNO").toString());
                lv1.setCustomerName(maphed.get("CUSTOMERNAME").toString());
                lv1.setMemo(maphed.get("MEMO").toString());
                lv1.setRegisterHead(new ArrayList<DCP_RCustomerQueryRes.level2Elm>());
                lv1.setCountdatas(new ArrayList<DCP_RCustomerQueryRes.level4Elm>());

                //这里可以直接先添加一下所有的注册内容，数量都可以给成0
                lv1.setCountdatas(GetAllSdetial());

                for (Map<String, Object> mapcurstomhed : getcurstomhead)
                {

                    if(maphed.get("CUSTOMERNO").toString().equals(mapcurstomhed.get("CUSTOMERNO").toString()))
                    {
                        DCP_RCustomerQueryRes.level2Elm lv2=new DCP_RCustomerQueryRes.level2Elm();
                        lv2.setIsRegister(mapcurstomhed.get("ISREGISTER").toString());
                        lv2.setMemo(mapcurstomhed.get("BMEMO").toString());

                        if(mapcurstomhed.get("TERMINALLICENCE")==null||mapcurstomhed.get("TERMINALLICENCE").toString().isEmpty())
                        {
                            continue;
                        }

                        //这里时间要格式化一下
                        String sdatatimetemp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(mapcurstomhed.get("BSDATETIME").toString()));
                        //lv2.setsDateTime(mapcurstomhed.get("BSDATETIME").toString());
                        lv2.setsDateTime(sdatatimetemp);

                        lv2.setTerminalLicence(mapcurstomhed.get("TERMINALLICENCE").toString());
                        lv2.setRegisterDetail(new ArrayList<DCP_RCustomerQueryRes.level3Elm>());
                        for (Map<String, Object> mapdetail : getcurstomDetail)
                        {
                            if(mapcurstomhed.get("CUSTOMERNO").toString().equals(mapdetail.get("CUSTOMERNO").toString())&&mapcurstomhed.get("TERMINALLICENCE").toString().equals(mapdetail.get("TERMINALLICENCE").toString()))
                            {
                                DCP_RCustomerQueryRes.level3Elm lv3=new DCP_RCustomerQueryRes.level3Elm();
                                lv3.setBdate(mapdetail.get("BDATE").toString());
                                lv3.setEdate(mapdetail.get("EDATE").toString());
                                lv3.setMemo(mapdetail.get("MEMO").toString());
                                lv3.setProducttype(mapdetail.get("PRODUCTTYPE").toString());
                                lv3.setRegisterType(mapdetail.get("REGISTERTYPE").toString());
                                lv3.setScount(mapdetail.get("SCOUNT").toString());
                                lv2.getRegisterDetail().add(lv3);
                                //这里要加上日期的判断
                                String bdate=mapdetail.get("BDATE").toString();
                                String edate=mapdetail.get("EDATE").toString();
                                if(bdate.isEmpty()||edate.isEmpty())
                                {
                                    continue;
                                }

                                boolean isindate=false;
                                if(Integer.parseInt(sdate)>=Integer.parseInt(bdate)&&Integer.parseInt(sdate)<=Integer.parseInt(edate))
                                {
                                    isindate=true;
                                    //这里要循环一下，如果没有就加入进去，如果有就把数量加上去
                                    boolean isexist=false;
                                    for (DCP_RCustomerQueryRes.level4Elm map : lv1.getCountdatas())
                                    {
                                        if(map.getProducttype().equals(mapdetail.get("PRODUCTTYPE").toString()))
                                        {
                                            isexist=true;
                                            String scount1=map.getScount();
                                            String scount2=mapdetail.get("SCOUNT").toString();
                                            map.setScount(Integer.parseInt(scount1)+Integer.parseInt(scount2)+"");
                                        }
                                    }

                                    if(isexist==false)
                                    {
                                        DCP_RCustomerQueryRes.level4Elm lv4=new DCP_RCustomerQueryRes.level4Elm();
                                        lv4.setProducttype(mapdetail.get("PRODUCTTYPE").toString());
                                        lv4.setScount(mapdetail.get("SCOUNT").toString());
                                        lv1.getCountdatas().add(lv4);
                                    }

                                }

                                lv3.setSDetailinfo(new ArrayList<DCP_RCustomerQueryRes.level5Elm>());
                                for (Map<String, Object> mapsdtail : listcustomer)
                                {
                                    if(mapsdtail.get("CUSTOMERNO").toString().equals(mapdetail.get("CUSTOMERNO").toString())&&mapsdtail.get("TERMINALLICENCE").toString().equals(mapdetail.get("TERMINALLICENCE").toString())&&mapsdtail.get("PRODUCTTYPE").toString().equals(mapdetail.get("PRODUCTTYPE").toString()))
                                    {
                                        String DSDETAILMODULAR=mapdetail.get("DSDETAILMODULAR").toString();

                                        DCP_RCustomerQueryRes.level5Elm lv5=new DCP_RCustomerQueryRes.level5Elm();
                                        lv5.setSDetailType(mapdetail.get("DSDETAILTYPE").toString());
                                        lv5.setSDetailmodular(mapdetail.get("DSDETAILMODULAR").toString());
                                        lv5.setScount(mapdetail.get("DSCOUNT").toString());
                                        if(DSDETAILMODULAR==null||DSDETAILMODULAR.isEmpty())
                                        {
                                            continue;
                                        }
                                        lv3.getSDetailinfo().add(lv5);
                                        if(isindate==true)
                                        {
                                            for (DCP_RCustomerQueryRes.level4Elm map : lv1.getCountdatas())
                                            {
                                                if(map.getSDetailinfo()==null)
                                                {
                                                    continue;
                                                }
                                                for (DCP_RCustomerQueryRes.level5Elm map2 : map.getSDetailinfo())
                                                {
                                                    if(map.getProducttype().equals(mapsdtail.get("PRODUCTTYPE").toString())&&map2.getSDetailmodular().equals(mapdetail.get("DSDETAILMODULAR").toString()) )
                                                    {
                                                        String scount1=map2.getScount();
                                                        String scount2=mapsdtail.get("SCOUNT").toString();
                                                        map2.setScount(Integer.parseInt(scount1)+Integer.parseInt(scount2)+"");
                                                    }
                                                }

                                            }
                                        }

                                    }
                                }

                            }

                        }

                        lv1.getRegisterHead().add(lv2);
                    }

                }


                res.getDatas().add(lv1);

            }

        }

        return res;
    }

    protected ArrayList<DCP_RCustomerQueryRes.level4Elm>  GetAllSdetial()
    {
        ArrayList<DCP_RCustomerQueryRes.level4Elm> listlv4=new ArrayList<DCP_RCustomerQueryRes.level4Elm>();
        DCP_RCustomerQueryRes.level4Elm lv4=new DCP_RCustomerQueryRes.level4Elm();
        lv4.setSDetailinfo(new ArrayList<DCP_RCustomerQueryRes.level5Elm>());
        lv4.setProducttype("11");
        lv4.setScount("0");
        lv4.setIscount("0");
        DCP_RCustomerQueryRes.level5Elm lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1101");
        lv5.setSDetailmodular("110101");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1101");
        lv5.setSDetailmodular("110102");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1101");
        lv5.setSDetailmodular("110103");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1101");
        lv5.setSDetailmodular("110104");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1101");
        lv5.setSDetailmodular("110105");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1101");
        lv5.setSDetailmodular("110106");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1101");
        lv5.setSDetailmodular("110107");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110201");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110202");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110203");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110204");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110205");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110206");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110207");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110208");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110209");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110210");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1102");
        lv5.setSDetailmodular("110211");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1103");
        lv5.setSDetailmodular("110301");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        listlv4.add(lv4);

        lv4=new DCP_RCustomerQueryRes.level4Elm();
        lv4.setSDetailinfo(new ArrayList<DCP_RCustomerQueryRes.level5Elm>());
        lv4.setProducttype("12");
        lv4.setScount("0");
        lv4.setIscount("0");
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1201");
        lv5.setSDetailmodular("120101");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1202");
        lv5.setSDetailmodular("120201");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1202");
        lv5.setSDetailmodular("120202");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        listlv4.add(lv4);

        lv4=new DCP_RCustomerQueryRes.level4Elm();
        lv4.setSDetailinfo(new ArrayList<DCP_RCustomerQueryRes.level5Elm>());
        lv4.setProducttype("13");
        lv4.setScount("0");
        lv4.setIscount("0");
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1301");
        lv5.setSDetailmodular("130101");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        listlv4.add(lv4);

        lv4=new DCP_RCustomerQueryRes.level4Elm();
        lv4.setSDetailinfo(new ArrayList<DCP_RCustomerQueryRes.level5Elm>());
        lv4.setProducttype("14");
        lv4.setScount("0");
        lv4.setIscount("0");
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1401");
        lv5.setSDetailmodular("140101");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1401");
        lv5.setSDetailmodular("140102");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1402");
        lv5.setSDetailmodular("140201");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1402");
        lv5.setSDetailmodular("140202");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1402");
        lv5.setSDetailmodular("140203");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1403");
        lv5.setSDetailmodular("140301");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1403");
        lv5.setSDetailmodular("140302");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        listlv4.add(lv4);

        lv4=new DCP_RCustomerQueryRes.level4Elm();
        lv4.setSDetailinfo(new ArrayList<DCP_RCustomerQueryRes.level5Elm>());
        lv4.setProducttype("15");
        lv4.setScount("0");
        lv4.setIscount("0");
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1501");
        lv5.setSDetailmodular("150101");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        listlv4.add(lv4);

        lv4=new DCP_RCustomerQueryRes.level4Elm();
        lv4.setSDetailinfo(new ArrayList<DCP_RCustomerQueryRes.level5Elm>());
        lv4.setProducttype("16");
        lv4.setScount("0");
        lv4.setIscount("0");
        lv5=new DCP_RCustomerQueryRes.level5Elm();
        lv5.setSDetailType("1601");
        lv5.setSDetailmodular("160101");
        lv5.setScount("0");
        lv5.setIscount("0");
        lv4.getSDetailinfo().add(lv5);
        listlv4.add(lv4);

        return listlv4;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_RCustomerQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
