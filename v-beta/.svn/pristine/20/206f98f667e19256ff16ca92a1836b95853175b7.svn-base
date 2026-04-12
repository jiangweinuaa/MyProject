package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.prefs.Preferences;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.context.annotation.EnableLoadTimeWeaving;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CRegisterCheckReq;
import com.dsc.spos.json.cust.res.DCP_CRegisterCheckRes;
import com.dsc.spos.json.cust.res.DCP_SRegisterRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.PLATFORM_SREGISTERDETAIL;
import com.dsc.spos.model.PLATFORM_SREGISTERHEAD;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.RSAUtil;
import com.dsc.spos.utils.Register;
import com.google.gson.reflect.TypeToken;

public class DCP_CRegisterCheck extends SPosAdvanceService<DCP_CRegisterCheckReq, DCP_CRegisterCheckRes>
{

    @Override
    protected void processDUID(DCP_CRegisterCheckReq req, DCP_CRegisterCheckRes res) throws Exception {
        // TODO Auto-generated method stub
        //POS是必须验证机器码，门店，以及机台,这种是通过注册码去验证
        String sdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

        //POS第二种验证方式，必须要先通过作业去维护对应关系，POS才能注册,POS上来注册，必须要传门店号，机台号等
        //查询一下DCP_REGEDISTMODULAR表的RTYPEINFO
        String regsql="select * from DCP_REGEDISTMODULAR where RFUNCNO='"+req.getRequest().getProducttype()+"' ";
        String RTYPEINFO="";
        List<Map<String, Object>> reglist=this.doQueryData(regsql, null);
        if(reglist!=null&&!reglist.isEmpty())
        {
            RTYPEINFO=reglist.get(0).get("RTYPEINFO").toString();
        }

        if(req.getRequest().getProducttype().equals("3") ||RTYPEINFO.equals("3"))
        {
            if(req.getRequest().getrEId().isEmpty()||req.getRequest().getrShopId().isEmpty()||req.getRequest().getRmachine().isEmpty())
            {
                res.setSuccess(false);
                res.setServiceDescription("POS传入信息缺失!");
                return;
            }
            String sql="select * from Platform_CregisterDetail where Producttype='"+req.getRequest().getProducttype()+"'  and EID='"+req.getRequest().getrEId()+"' and SHOPID='"+req.getRequest().getrShopId()+"' "
                    + " and machine='"+req.getRequest().getRmachine()+"'   order by RegisterType desc ";
            List<Map<String, Object>> listdcp=this.doQueryData(sql, null);
            if(listdcp!=null&&!listdcp.isEmpty())
            {
                String bdate=listdcp.get(0).get("BDATE").toString();
                String edate=listdcp.get(0).get("EDATE").toString();
                if(Integer.parseInt(sdate)>=Integer.parseInt(bdate)&&Integer.parseInt(sdate)<=Integer.parseInt(edate))
                {
                    if(listdcp.get(0).get("ISREGISTER").toString().equals("Y"))
                    {
                        res.setSuccess(false);
                        res.setServiceDescription("该机台已被注册!");
                        return;
                    }
                    //这里相当于注册成功，还需要加一次验签的过程
                    if(checktlinfo(listdcp.get(0))==false)
                    {
                        res.setSuccess(false);
                        res.setServiceDescription("注册验签失败!");
                        return;
                    }

                    UptBean up=new UptBean("Platform_CregisterDetail");

                    up.addUpdateValue("IsRegister", new DataValue("Y", Types.VARCHAR));

                    up.addCondition("MachineCode", new DataValue(listdcp.get(0).get("MACHINECODE").toString(), Types.VARCHAR));
                    up.addCondition("Producttype", new DataValue(req.getRequest().getProducttype(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(up));

                    this.doExecuteDataToDB();

                    res.setMachinecode(listdcp.get(0).get("MACHINECODE").toString());
                    res.setTlinfo(listdcp.get(0).get("TLINFO").toString());
                    res.setCustomerNo(listdcp.get(0).get("CUSTOMERNO").toString());
                    res.setEdate(listdcp.get(0).get("EDATE").toString());
                    res.setBdate(listdcp.get(0).get("BDATE").toString());

                    res.setCompanyno(listdcp.get(0).get("COMPANYNO").toString());
                    res.setEid(listdcp.get(0).get("EID").toString());
                    res.setIsRegister("Y");
                    res.setMachine(listdcp.get(0).get("MACHINE").toString());
                    res.setProductType(listdcp.get(0).get("PRODUCTTYPE").toString());
                    res.setsDatetime(listdcp.get(0).get("SDATETIME").toString());
                    res.setsDetailModular(listdcp.get(0).get("SDETAILMODULAR").toString());
                    res.setShop(listdcp.get(0).get("SHOP").toString());
                    res.setShopid(listdcp.get(0).get("SHOPID").toString());
                    res.setTerminalLicence(listdcp.get(0).get("TERMINALLICENCE").toString());
                    res.setRegisterType(listdcp.get(0).get("REGISTERTYPE").toString());

                    //机台信息也返回
                    String sqlMachine="select * from platform_machine where EID='"+req.getRequest().getrEId()+"' and SHOPID='"+req.getRequest().getrShopId()+"' "
                            + " and machine='"+req.getRequest().getRmachine()+"' ";
                    List<Map<String, Object>> listMachine=this.doQueryData(sqlMachine, null);
                    if (listMachine != null && listMachine.size()>0)
                    {
                        DCP_CRegisterCheckRes.level1Elm mInfo=res.new level1Elm();
                        mInfo.setApiusercode(listMachine.get(0).get("APIUSERCODE").toString());
                        mInfo.setApptype(listMachine.get(0).get("APPTYPE").toString());
                        mInfo.setBusinessType(listMachine.get(0).get("BUSINESSTYPE").toString());
                        mInfo.setChannelid(listMachine.get(0).get("CHANNELID").toString());
                        mInfo.setCnfflg(listMachine.get(0).get("CNFFLG").toString());
                        mInfo.setEid(listMachine.get(0).get("EID").toString());
                        mInfo.setHardwareInfo(listMachine.get(0).get("HARDWAREINFO").toString());
                        mInfo.setMachine(listMachine.get(0).get("MACHINE").toString());
                        mInfo.setMachineName(listMachine.get(0).get("MACHINENAME").toString());
                        mInfo.setRdate(listMachine.get(0).get("RDATE").toString());
                        mInfo.setRegflg(listMachine.get(0).get("REGFLG").toString());
                        mInfo.setShopid(listMachine.get(0).get("SHOPID").toString());
                        mInfo.setSnumber(listMachine.get(0).get("SNUMBER").toString());
                        mInfo.setStatus(listMachine.get(0).get("STATUS").toString());
                        mInfo.setTrans_time(listMachine.get(0).get("TRAN_TIME").toString());
                        mInfo.setUpdate_time(listMachine.get(0).get("UPDATE_TIME").toString());
                        mInfo.setVernum(listMachine.get(0).get("VERNUM").toString());

                        res.setMachineInfo(mInfo);
                    }

                    res.setSuccess(true);
                    return;

                }
                else
                {
                    res.setSuccess(false);
                    res.setServiceDescription("该机台没有添加注册信息已过期!");
                    return;
                }
            }
            else
            {
                res.setSuccess(false);
                res.setServiceDescription("该机台没有添加注册信息!");
                return;
            }

        }



        //云中台、CRM注册验证
        if(req.getRequest().getProducttype().equals("2")||req.getRequest().getProducttype().equals("0")||req.getRequest().getProducttype().equals("4")||req.getRequest().getProducttype().equals("5") ||RTYPEINFO.equals("1") )
        {
            //验证会员以及云中台没有注册过
            String sql="select * from Platform_CregisterDetail where Producttype='"+req.getRequest().getProducttype()+"' and bdate<='"+sdate+"' and edate>='"+sdate+"' order by RegisterType desc ";
            List<Map<String, Object>> listdcp=this.doQueryData(sql, null);
            if(listdcp!=null&&!listdcp.isEmpty())
            {
                //这里需要验签
                //注册成功
                //这里相当于注册成功，还需要加一次验签的过程
                if(checktlinfo(listdcp.get(0))==false)
                {
                    res.setSuccess(false);
                    res.setServiceDescription("注册验签失败!");
                    return;
                }

                res.setSuccess(true);
                return;
            }
            else
            {
                //注册失败
                res.setSuccess(false);
                res.setServiceDescription("产品未注册!");
                return;
            }
        }

        //门店管理验证
        if(req.getRequest().getProducttype().equals("1")||req.getRequest().getProducttype().equals("6")||req.getRequest().getProducttype().equals("7")||req.getRequest().getProducttype().equals("8") ||RTYPEINFO.equals("2") )
        {
            {
                String sql="select * from Platform_CregisterDetail where Producttype='"+req.getRequest().getProducttype()+"' and bdate<='"+sdate+"' and edate>='"+sdate+"' and EID='"+req.getRequest().getrEId()+"' and SHOPID='"+req.getRequest().getrShopId()+"'   order by RegisterType desc ";
                List<Map<String, Object>> listdcp=this.doQueryData(sql, null);
                if(listdcp!=null&&!listdcp.isEmpty())
                {
                    //这里还要加一步验签的过程
                    //这里相当于注册成功，还需要加一次验签的过程
                    if(checktlinfo(listdcp.get(0))==false)
                    {
                        res.setSuccess(false);
                        res.setServiceDescription("注册验签失败!");
                        return;
                    }

                    res.setSuccess(true);
                    return;
                }
                else
                {
                    res.setSuccess(false);
                    res.setServiceDescription("该门店没有注册!");
                    return;
                }
            }

        }
    }

    public boolean checktlinfo(Map<String, Object> tlinfo) throws Exception
    {

        //解密tlinfo的TLINFO信息
        try
        {
            String stlinfo=tlinfo.get("TLINFO").toString();
            stlinfo=RSAUtil.RSADecrypt(stlinfo);
            String checkstring=tlinfo.get("MACHINECODE").toString()+tlinfo.get("BDATE").toString()+tlinfo.get("EDATE").toString()+tlinfo.get("CUSTOMERNO").toString();
            if(stlinfo.equals(checkstring))
            {
                return true;
            }
            return false;
        }
        catch(Exception ex)
        {
            return false;
        }
    }



    @Override
    protected List<InsBean> prepareInsertData(DCP_CRegisterCheckReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CRegisterCheckReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CRegisterCheckReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CRegisterCheckReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_CRegisterCheckReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_CRegisterCheckReq>(){};
    }

    @Override
    protected DCP_CRegisterCheckRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_CRegisterCheckRes();
    }
}


