package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_CRegisterCheckRes extends JsonRes 
{
	private String machinecode;
	private String tlinfo;
	private String customerNo;
	private String bdate;
	private String edate;

    private String terminalLicence;
    private String registerType;
    private String productType;
    private String sDatetime;
    private String shopid;
    private String shop;
    private String eid;
    private String machine;
    private String isRegister;
    private String sDetailModular;
    private String companyno;

    private level1Elm machineInfo;


    public class level1Elm
    {
        private String eid;
        private String shopid;
        private String machine;
        private String machineName;
        private String hardwareInfo;
        private String snumber;
        private String regflg;
        private String trans_time;
        private String update_time;
        private String vernum;
        private String rdate;
        private String businessType;
        private String status;
        private String apptype;
        private String apiusercode;
        private String channelid;
        private String cnfflg;

        public String getEid()
        {
            return eid;
        }

        public void setEid(String eid)
        {
            this.eid = eid;
        }

        public String getShopid()
        {
            return shopid;
        }

        public void setShopid(String shopid)
        {
            this.shopid = shopid;
        }

        public String getMachine()
        {
            return machine;
        }

        public void setMachine(String machine)
        {
            this.machine = machine;
        }

        public String getMachineName()
        {
            return machineName;
        }

        public void setMachineName(String machineName)
        {
            this.machineName = machineName;
        }

        public String getHardwareInfo()
        {
            return hardwareInfo;
        }

        public void setHardwareInfo(String hardwareInfo)
        {
            this.hardwareInfo = hardwareInfo;
        }

        public String getSnumber()
        {
            return snumber;
        }

        public void setSnumber(String snumber)
        {
            this.snumber = snumber;
        }

        public String getRegflg()
        {
            return regflg;
        }

        public void setRegflg(String regflg)
        {
            this.regflg = regflg;
        }

        public String getTrans_time()
        {
            return trans_time;
        }

        public void setTrans_time(String trans_time)
        {
            this.trans_time = trans_time;
        }

        public String getUpdate_time()
        {
            return update_time;
        }

        public void setUpdate_time(String update_time)
        {
            this.update_time = update_time;
        }

        public String getVernum()
        {
            return vernum;
        }

        public void setVernum(String vernum)
        {
            this.vernum = vernum;
        }

        public String getRdate()
        {
            return rdate;
        }

        public void setRdate(String rdate)
        {
            this.rdate = rdate;
        }

        public String getBusinessType()
        {
            return businessType;
        }

        public void setBusinessType(String businessType)
        {
            this.businessType = businessType;
        }

        public String getStatus()
        {
            return status;
        }

        public void setStatus(String status)
        {
            this.status = status;
        }

        public String getApptype()
        {
            return apptype;
        }

        public void setApptype(String apptype)
        {
            this.apptype = apptype;
        }

        public String getApiusercode()
        {
            return apiusercode;
        }

        public void setApiusercode(String apiusercode)
        {
            this.apiusercode = apiusercode;
        }

        public String getChannelid()
        {
            return channelid;
        }

        public void setChannelid(String channelid)
        {
            this.channelid = channelid;
        }

        public String getCnfflg()
        {
            return cnfflg;
        }

        public void setCnfflg(String cnfflg)
        {
            this.cnfflg = cnfflg;
        }
    }


    public level1Elm getMachineInfo()
    {
        return machineInfo;
    }

    public void setMachineInfo(level1Elm machineInfo)
    {
        this.machineInfo = machineInfo;
    }

    public String getTerminalLicence()
    {
        return terminalLicence;
    }

    public void setTerminalLicence(String terminalLicence)
    {
        this.terminalLicence = terminalLicence;
    }

    public String getRegisterType()
    {
        return registerType;
    }

    public void setRegisterType(String registerType)
    {
        this.registerType = registerType;
    }

    public String getProductType()
    {
        return productType;
    }

    public void setProductType(String productType)
    {
        this.productType = productType;
    }

    public String getsDatetime()
    {
        return sDatetime;
    }

    public void setsDatetime(String sDatetime)
    {
        this.sDatetime = sDatetime;
    }

    public String getShopid()
    {
        return shopid;
    }

    public void setShopid(String shopid)
    {
        this.shopid = shopid;
    }

    public String getShop()
    {
        return shop;
    }

    public void setShop(String shop)
    {
        this.shop = shop;
    }

    public String getEid()
    {
        return eid;
    }

    public void setEid(String eid)
    {
        this.eid = eid;
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine(String machine)
    {
        this.machine = machine;
    }

    public String getIsRegister()
    {
        return isRegister;
    }

    public void setIsRegister(String isRegister)
    {
        this.isRegister = isRegister;
    }

    public String getsDetailModular()
    {
        return sDetailModular;
    }

    public void setsDetailModular(String sDetailModular)
    {
        this.sDetailModular = sDetailModular;
    }

    public String getCompanyno()
    {
        return companyno;
    }

    public void setCompanyno(String companyno)
    {
        this.companyno = companyno;
    }

    public String getCustomerNo() {
		return customerNo;
	}
	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}
	public String getBdate() {
		return bdate;
	}
	public void setBdate(String bdate) {
		this.bdate = bdate;
	}
	public String getEdate() {
		return edate;
	}
	public void setEdate(String edate) {
		this.edate = edate;
	}

	public String getTlinfo() {
		return tlinfo;
	}
	public void setTlinfo(String tlinfo) {
		this.tlinfo = tlinfo;
	}
	public String getMachinecode() {
	return machinecode;
	}
	public void setMachinecode(String machinecode) {
	this.machinecode = machinecode;
	}
	
	
}
