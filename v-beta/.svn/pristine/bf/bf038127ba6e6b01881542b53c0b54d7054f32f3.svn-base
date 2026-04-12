package com.dsc.spos.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.List;

@XmlRootElement(name = "PluginServiceConfig")
public class SPosConfig {

	private Value Using_GAODEMAP_key;//高德地图天气预报平台应用key值
	private Value Pos_Type;//1云POS 2其他POS
	private Value DCP_Slave; //1:是 0:否
    private Value OMS_Url; //分销系统接口地址
    private Value OMS_Eid; //分销系统企业编号
    private Value OMS_ERP_Eid; //ERP企业编号
    private Value RuiXiang_Url; //锐翔系统接口地址
    private Value RuiXiang_Secret; //锐翔系统签名秘钥
    private Value RuiXiang_Appid; //锐翔系统Appid
    private Value RuiXiang_Eid; //锐翔系统企业编号

    private Value MES_URL; //MES地址
    private Value DCP_URL; //中台地址
    private Value CRM_URL; //会员地址
    private Value PAY_URL; //支付地址
    private Value PROM_URL; //促销地址
    private Value POS_URL; //POS地址
	private Value PICTURE_URL;//图片服务地址
    private Value MES_INNER_URL; //MES地址
    private Value DCP_INNER_URL; //中台地址
    private Value CRM_INNER_URL; //会员地址
    private Value PAY_INNER_URL; //支付地址
    private Value PROM_INNER_URL; //促销地址
    private Value POS_INNER_URL; //POS地址
	private Value PICTURE_INNER_URL;//图片服务地址

    public Value getMES_INNER_URL()
    {
        return MES_INNER_URL;
    }

    public void setMES_INNER_URL(Value MES_INNER_URL)
    {
        this.MES_INNER_URL = MES_INNER_URL;
    }

    public Value getDCP_INNER_URL() {
		return DCP_INNER_URL;
	}
	public void setDCP_INNER_URL(Value dCP_INNER_URL) {
		DCP_INNER_URL = dCP_INNER_URL;
	}
	public Value getCRM_INNER_URL() {
		return CRM_INNER_URL;
	}
	public void setCRM_INNER_URL(Value cRM_INNER_URL) {
		CRM_INNER_URL = cRM_INNER_URL;
	}
	public Value getPAY_INNER_URL() {
		return PAY_INNER_URL;
	}
	public void setPAY_INNER_URL(Value pAY_INNER_URL) {
		PAY_INNER_URL = pAY_INNER_URL;
	}
	public Value getPROM_INNER_URL() {
		return PROM_INNER_URL;
	}
	public void setPROM_INNER_URL(Value pROM_INNER_URL) {
		PROM_INNER_URL = pROM_INNER_URL;
	}
	public Value getPOS_INNER_URL() {
		return POS_INNER_URL;
	}
	public void setPOS_INNER_URL(Value pOS_INNER_URL) {
		POS_INNER_URL = pOS_INNER_URL;
	}
	public Value getPICTURE_INNER_URL() {
		return PICTURE_INNER_URL;
	}
	public void setPICTURE_INNER_URL(Value pICTURE_INNER_URL) {
		PICTURE_INNER_URL = pICTURE_INNER_URL;
	}
	public Value getPICTURE_URL() {
		return PICTURE_URL;
	}
	public void setPICTURE_URL(Value pICTURE_URL) {
		PICTURE_URL = pICTURE_URL;
	}
	private ServiceInterface serviceInterface;//门店作为服务端提供的服务
	private ZipLogFilePath zipLogFilePath;//压缩文件路径

	private NRCRestfulInterface nrcRestfulInterface;//中台监控服务
	private DataBaseConfig dataBaseConfig; //DB info

	private T100Interface t100Interface; //与ERP集成

	private NewRetailTransferm newretailTransferm; //与新零售集成

	private ScheduleOfDirector scheduleOfDirector; //排程用的 tage.

	//2018-08-29新增微商城信息
	private MicroMarkTransferm microMarkTransferm; // 与微商城集成

    public  Value KeepLogsDay; //保留天数范围内的日志 例:7 保留7天内的日志

    @XmlElement(name = "OMS_Url", required = false)
    public Value getOMS_Url() {
        return OMS_Url;
    }
    public void setOMS_Url(Value oMS_Url) {
        OMS_Url = oMS_Url;
    }

    @XmlElement(name = "OMS_Eid", required = false)
    public Value getOMS_Eid() {
        return OMS_Eid;
    }
    public void setOMS_Eid(Value oMS_Eid) {
        OMS_Eid = oMS_Eid;
    }

    @XmlElement(name = "OMS_ERP_Eid", required = false)
    public Value getOMS_ERP_Eid() {
        return OMS_ERP_Eid;
    }
    public void setOMS_ERP_Eid(Value oMS_ERP_Eid) {
        OMS_ERP_Eid = oMS_ERP_Eid;
    }

    @XmlElement(name = "RuiXiang_Url", required = false)
    public Value getRuiXiang_Url() {
        return RuiXiang_Url;
    }
    public void setRuiXiang_Url(Value ruiXiang_Url) {
        RuiXiang_Url = ruiXiang_Url;
    }

    @XmlElement(name = "RuiXiang_Secret", required = false)
    public Value getRuiXiang_Secret() {
        return RuiXiang_Secret;
    }
    public void setRuiXiang_Secret(Value ruiXiang_Secret) {
        RuiXiang_Secret = ruiXiang_Secret;
    }

    @XmlElement(name = "RuiXiang_Appid", required = false)
    public Value getRuiXiang_Appid() {
        return RuiXiang_Appid;
    }
    public void setRuiXiang_Appid(Value ruiXiang_Appid) {
        RuiXiang_Appid = ruiXiang_Appid;
    }

    @XmlElement(name = "RuiXiang_Eid", required = false)
    public Value getRuiXiang_Eid() {
        return RuiXiang_Eid;
    }
    public void setRuiXiang_Eid(Value ruiXiang_Eid) {
        RuiXiang_Eid = ruiXiang_Eid;
    }

	@XmlElement(name = "DCP_Slave", required = false)
	public Value getDCP_Slave() {
		return DCP_Slave;
	}
	public void setDCP_Slave(Value dCP_Slave) {
		DCP_Slave = dCP_Slave;
	}
	
	@XmlElement(name = "Using_GAODEMAP_key", required = true)
	public Value getUsing_GAODEMAP_key() 
	{
		return Using_GAODEMAP_key;
	}
	public void setUsing_GAODEMAP_key(Value using_GAODEMAP_key) 
	{
		Using_GAODEMAP_key = using_GAODEMAP_key;
	}

    @XmlElement(name = "KeepLogsDay", required = true)
    public Value getKeepLogsDay() {
        return KeepLogsDay;
    }
    public void setKeepLogsDay(Value keepLogsDay) {
        KeepLogsDay = keepLogsDay;
    }


	@XmlElement(name = "ServiceInterface", required = true)
	public ServiceInterface getServiceInterface() 
	{
		return serviceInterface;
	}
	public void setServiceInterface(ServiceInterface serviceInterface) 
	{
		this.serviceInterface = serviceInterface;
	}

	@XmlElement(name = "ZipLogFilePath", required = false)
	public ZipLogFilePath getZipLogFilePath()
	{
		return zipLogFilePath;
	}
	public void setZipLogFilePath(ZipLogFilePath zipLogFilePath)
	{
		this.zipLogFilePath = zipLogFilePath;
	}

	@XmlElement(name = "NewRetailTransferm", required = true)
	public NewRetailTransferm getNewretailTransferm() {
		return newretailTransferm;
	}
	public void setNewretailTransferm(NewRetailTransferm newretailTransferm) {
		this.newretailTransferm = newretailTransferm;
	}

	@XmlElement(name = "MicroMarkTransferm", required = true)
	public MicroMarkTransferm getMicroMarkTransferm() {
		return microMarkTransferm;
	}
	public void setMicroMarkTransferm(MicroMarkTransferm microMarkTransferm) 
	{
		this.microMarkTransferm = microMarkTransferm;
	}


	@XmlElement(name = "DataBaseConfig", required = true)
	public DataBaseConfig getDataBaseConfig() {
		return dataBaseConfig;
	}
	public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
		this.dataBaseConfig = dataBaseConfig;
	}


	@XmlElement(name = "ScheduleOfDirector", required = true)
	public ScheduleOfDirector getScheduleOfDirector() {
		return scheduleOfDirector;
	}
	public void setScheduleOfDirector(ScheduleOfDirector scheduleOfDirector) {
		this.scheduleOfDirector = scheduleOfDirector;
	}


	/**
	 * 基本 存放 Value 的 bean.
	 * @author Xavier
	 *
	 */
	public static class Value {
		private String value;
		private String tagValue;

		public Value() {}
		public Value(String value) {
			this.value = value;
		}

		@XmlAttribute(name = "value")
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@XmlValue
		public String getTagValue() {
			return tagValue;
		}
		public void setTagValue(String tagValue) {
			this.tagValue = tagValue;
		}


	}




	/**
	 * 门店提供的服务接口
	 * @author Administrator
	 *
	 */
	public static class ServiceInterface 
	{
		private List<Value> OPSService;

		@XmlElement(name = "OPSService", required = true)
		public List<Value> getOPSService() 
		{
			return OPSService;
		}

		public void setOPSService(List<Value> oPSService) 
		{
			OPSService = oPSService;
		}		
	}

    public static class ZipLogFilePath
    {
        private List<Value> FilePath;

        @XmlElement(name = "FilePath", required = false)
        public List<Value> getFilePath()
        {
            return FilePath;
        }

        public void setFilePath(List<Value> filePath)
        {
            FilePath = filePath;
        }
    }


	/**
	 * 中台监控服务
	 * @author Administrator
	 *
	 */
	public static class NRCRestfulInterface 
	{
		private List<Value> RestfulService;

		@XmlElement(name = "RestfulService", required = true)
		public List<Value> getRestfulService() 
		{
			return RestfulService;
		}

		public void setRestfulService(List<Value> restfulService) 
		{
			RestfulService = restfulService;
		}		
	}



	/*
	 * 与新零售集成
	 */
	public static class NewRetailTransferm {

		private Value waimaiHttpPost;
		private Value waimaiMTAPPID;
		private Value waimaiMTSignKey;
		private Value waimaiMTIsJBP;
		private Value waimaiELMAPPKey;
		private Value waimaiELMSecret;
		private Value waimaiELMSandbox;

		private Value waimaiJDDJAPPKey;
		private Value waimaiJDDJSecret;
		private Value waimaiJDDJToken;
		private Value waimaiJDDJSandbox;

		@XmlElement(name = "waimaiHttpPost", required = true)
		public Value getWaimaiHttpPost() {
			return waimaiHttpPost;
		}

		public void setWaimaiHttpPost(Value waimaiHttpPost) {
			this.waimaiHttpPost = waimaiHttpPost;
		}
		@XmlElement(name = "waimaiMTAPPID", required = true)
		public Value getWaimaiMTAPPID() {
			return waimaiMTAPPID;
		}

		public void setWaimaiMTAPPID(Value waimaiMTAPPID) {
			this.waimaiMTAPPID = waimaiMTAPPID;
		}
		@XmlElement(name = "waimaiMTSignKey", required = true)
		public Value getWaimaiMTSignKey() {
			return waimaiMTSignKey;
		}

		public void setWaimaiMTSignKey(Value waimaiMTSignKey) {
			this.waimaiMTSignKey = waimaiMTSignKey;
		}

		@XmlElement(name = "waimaiMTIsJBP", required = true)
		public Value getWaimaiMTIsJBP() {
			return waimaiMTIsJBP;
		}

		public void setWaimaiMTIsJBP(Value waimaiMTIsJBP) {
			this.waimaiMTIsJBP = waimaiMTIsJBP;
		}

		@XmlElement(name = "waimaiELMAPPKey", required = true)
		public Value getWaimaiELMAPPKey() {
			return waimaiELMAPPKey;
		}
		public void setWaimaiELMAPPKey(Value waimaiELMAPPKey) {
			this.waimaiELMAPPKey = waimaiELMAPPKey;
		}

		@XmlElement(name = "waimaiELMSecret", required = true)
		public Value getWaimaiELMSecret() {
			return waimaiELMSecret;
		}
		public void setWaimaiELMSecret(Value waimaiELMSecret) {
			this.waimaiELMSecret = waimaiELMSecret;
		}

		@XmlElement(name = "waimaiELMSandbox", required = true)
		public Value getWaimaiELMSandbox() {
			return waimaiELMSandbox;
		}
		public void setWaimaiELMSandbox(Value waimaiELMSandbox) {
			this.waimaiELMSandbox = waimaiELMSandbox;
		}

		@XmlElement(name = "waimaiJDDJAPPKey", required = true)
		public Value getWaimaiJDDJAPPKey() {
			return waimaiJDDJAPPKey;
		}

		public void setWaimaiJDDJAPPKey(Value waimaiJDDJAPPKey) {
			this.waimaiJDDJAPPKey = waimaiJDDJAPPKey;
		}

		@XmlElement(name = "waimaiJDDJSecret", required = true)
		public Value getWaimaiJDDJSecret() {
			return waimaiJDDJSecret;
		}

		public void setWaimaiJDDJSecret(Value waimaiJDDJSecret) {
			this.waimaiJDDJSecret = waimaiJDDJSecret;
		}

		@XmlElement(name = "waimaiJDDJToken", required = true)
		public Value getWaimaiJDDJToken() {
			return waimaiJDDJToken;
		}

		public void setWaimaiJDDJToken(Value waimaiJDDJToken) {
			this.waimaiJDDJToken = waimaiJDDJToken;
		}

		@XmlElement(name = "waimaiJDDJSandbox", required = true)
		public Value getWaimaiJDDJSandbox() {
			return waimaiJDDJSandbox;
		}

		public void setWaimaiJDDJSandbox(Value waimaiJDDJSandbox) {
			this.waimaiJDDJSandbox = waimaiJDDJSandbox;
		}



	}


	/*
	 * 与微商城集成
	 */
	public static class MicroMarkTransferm {

		private Value microMarkHttpPost;
		private Value microMarkKey;
		private Value microMarkSign;

		@XmlElement(name = "MicroMarkHttpPost", required = true)
		public Value getMicroMarkHttpPost() {
			return microMarkHttpPost;
		}
		public void setMicroMarkHttpPost(Value microMarkHttpPost) {
			this.microMarkHttpPost = microMarkHttpPost;
		}

		@XmlElement(name = "MicroMarkKey", required = true)
		public Value getMicroMarkKey() {
			return microMarkKey;
		}
		public void setMicroMarkKey(Value microMarkKey) {
			this.microMarkKey = microMarkKey;
		}

		@XmlElement(name = "MicroMarkSign", required = true)
		public Value getMicroMarkSign() {
			return microMarkSign;
		}
		public void setMicroMarkSign(Value microMarkSign) {
			this.microMarkSign = microMarkSign;
		}

	}


	public static class T100Interface 
	{		
		private List<ProdInterface>  ProdInterface;


		@XmlElement(name = "ProdInterface", required = true)
		public List<ProdInterface> getProdInterface() 
		{
			return ProdInterface;
		}

		public void setProdInterface(List<ProdInterface> prodInterface) 
		{
			ProdInterface = prodInterface;
		}

	}

	public static class ProdInterface 
	{

		private Value hostProd;  
		private Value hostIP;  
		private Value hostLang;  
		private Value hostAcct;  
		private Value hostID;
		private Value serviceProd;  
		private Value serviceIP;  
		private Value serviceID;
		private Value key;
		private Value type;
		private Value httpPost;
		private Value ETLHttpPost;

		private Value eId;
		private Value WS_To_Cross;  //服务方式   1：经中台      0：不经中台


		@XmlElement(name = "HostProd", required = true)
		public Value getHostProd() {
			return hostProd;
		}
		public void setHostProd(Value hostProd) {
			this.hostProd = hostProd;
		}
		@XmlElement(name = "HostIP", required = true)
		public Value getHostIP() {
			return hostIP;
		}
		public void setHostIP(Value hostIP) {
			this.hostIP = hostIP;
		}
		@XmlElement(name = "HostLang", required = true)
		public Value getHostLang() {
			return hostLang;
		}
		public void setHostLang(Value hostLang) {
			this.hostLang = hostLang;
		}
		@XmlElement(name = "HostAcct", required = true)
		public Value getHostAcct() {
			return hostAcct;
		}
		public void setHostAcct(Value hostAcct) {
			this.hostAcct = hostAcct;
		}

		@XmlElement(name = "HostID", required = true)
		public Value getHostID() {
			return hostID;
		}
		public void setHostID(Value hostID) {
			this.hostID = hostID;
		}  

		@XmlElement(name = "ServiceProd", required = true)
		public Value getServiceProd() 
		{
			return serviceProd;
		}
		public void setServiceProd(Value serviceProd) 
		{
			this.serviceProd = serviceProd;
		}

		@XmlElement(name = "ServiceIP", required = true)
		public Value getServiceIP() 
		{
			return serviceIP;
		}
		public void setServiceIP(Value serviceIP) 
		{
			this.serviceIP = serviceIP;
		}

		@XmlElement(name = "ServiceID", required = true)
		public Value getServiceID() 
		{
			return serviceID;
		}
		public void setServiceID(Value serviceID) 
		{
			this.serviceID = serviceID;
		}

		@XmlElement(name = "Key", required = true)
		public Value getKey() 
		{
			return key;
		}
		public void setKey(Value key) 
		{
			this.key = key;
		}

		@XmlElement(name = "Type", required = true)
		public Value getType() 
		{
			return type;
		}
		public void setType(Value type)
		{
			this.type = type;
		}

		@XmlElement(name = "HttpPost", required = true)
		public Value getHttpPost() 
		{
			return httpPost;
		}
		public void setHttpPost(Value httpPost)
		{
			this.httpPost = httpPost;
		}

		@XmlElement(name = "eId", required = true)
		public Value geteId() {
			return eId;
		}
		public void seteId(Value eId) {
			this.eId = eId;
		}
			

		@XmlElement(name = "WS_To_Cross", required = true)
		public Value getWS_To_Cross() {
			return WS_To_Cross;
		}
		public void setWS_To_Cross(Value WS_To_Cross) {
			this.WS_To_Cross = WS_To_Cross;
		}

		@XmlElement(name = "ETLHttpPost", required = true)
		public Value getETLHttpPost() {
			return ETLHttpPost;
		}
		public void setETLHttpPost(Value eTLHttpPost) {
			ETLHttpPost = eTLHttpPost;
		}


	}

	/**
	 * 設定外部參考的 lib.
	 * @author Xavier
	 *
	 */
	public static class RefExternalJar {
		private String refDir;
		private List<Value> refJar;

		@XmlAttribute
		public String getRefDir() {
			return refDir;
		}

		public void setRefDir(String refDir) {
			this.refDir = refDir;
		}

		@XmlElement(name = "RefJar", required = true)
		public List<Value> getRefJar() {
			return refJar;
		}

		public void setRefJar(List<Value> refJar) {
			this.refJar = refJar;
		}
	}

	/**
	 * 排程設定檔
	 * @author Xavier
	 *
	 */
	public static class ScheduleOfDirector {
		private String schPath;
		private List<Value> scheduleTask;

		@XmlAttribute(name = "schPath")
		public String getSchPath() {
			return schPath;
		}

		public void setSchPath(String schPath) {
			this.schPath = schPath;
		}

		@XmlElement(name = "ScheduleTask", required = true)
		public List<Value> getScheduleTask() {
			return scheduleTask;
		}

		public void setScheduleTask(List<Value> scheduleTask) {
			this.scheduleTask = scheduleTask;
		}
	}

	public static class DataBaseConfig {
		private Value driverClassName;  //DriverClassName
		private Value connectString;  //連線字串
		private Value loginUser;  //登入 user
		private Value loginPawd;  //登入 password

		@XmlElement(name = "DriverClassName", required = true)
		public Value getDriverClassName() {
			return driverClassName;
		}
		public void setDriverClassName(Value driverClassName) {
			this.driverClassName = driverClassName;
		}

		@XmlElement(name = "ConnectString", required = true)
		public Value getConnectString() {
			return connectString;
		}
		public void setConnectString(Value connectString) {
			this.connectString = connectString;
		}

		@XmlElement(name = "LoginUser", required = true)
		public Value getLoginUser() {
			return loginUser;
		}
		public void setLoginUser(Value loginUser) {
			this.loginUser = loginUser;
		}

		@XmlElement(name = "LoginPawd", required = true)
		public Value getLoginPawd() {
			return loginPawd;
		}
		public void setLoginPawd(Value loginPawd) {
			this.loginPawd = loginPawd;
		}
	}


	public static class MiddleDataBaseConfig {
		private Value driverClassName;  //DriverClassName
		private Value connectString;  //連線字串
		private Value loginUser;  //登入 user
		private Value loginPawd;  //登入 password

		@XmlElement(name = "DriverClassName", required = true)
		public Value getDriverClassName() {
			return driverClassName;
		}
		public void setDriverClassName(Value driverClassName) {
			this.driverClassName = driverClassName;
		}

		@XmlElement(name = "ConnectString", required = true)
		public Value getConnectString() {
			return connectString;
		}
		public void setConnectString(Value connectString) {
			this.connectString = connectString;
		}

		@XmlElement(name = "LoginUser", required = true)
		public Value getLoginUser() {
			return loginUser;
		}
		public void setLoginUser(Value loginUser) {
			this.loginUser = loginUser;
		}

		@XmlElement(name = "LoginPawd", required = true)
		public Value getLoginPawd() {
			return loginPawd;
		}
		public void setLoginPawd(Value loginPawd) {
			this.loginPawd = loginPawd;
		}
	}




	public static class YCERPDataBaseConfig 
	{
		private Value driverClassName;  //DriverClassName
		private Value connectString;  //連線字串
		private Value loginUser;  //登入 user
		private Value loginPawd;  //登入 password

		@XmlElement(name = "DriverClassName", required = true)
		public Value getDriverClassName() {
			return driverClassName;
		}
		public void setDriverClassName(Value driverClassName) {
			this.driverClassName = driverClassName;
		}

		@XmlElement(name = "ConnectString", required = true)
		public Value getConnectString() {
			return connectString;
		}
		public void setConnectString(Value connectString) {
			this.connectString = connectString;
		}

		@XmlElement(name = "LoginUser", required = true)
		public Value getLoginUser() {
			return loginUser;
		}
		public void setLoginUser(Value loginUser) {
			this.loginUser = loginUser;
		}

		@XmlElement(name = "LoginPawd", required = true)
		public Value getLoginPawd() {
			return loginPawd;
		}
		public void setLoginPawd(Value loginPawd) {
			this.loginPawd = loginPawd;
		}
	}


	public static class YCVIPDataBaseConfig 
	{
		private Value driverClassName;  //DriverClassName
		private Value connectString;  //連線字串
		private Value loginUser;  //登入 user
		private Value loginPawd;  //登入 password

		@XmlElement(name = "DriverClassName", required = true)
		public Value getDriverClassName() {
			return driverClassName;
		}
		public void setDriverClassName(Value driverClassName) {
			this.driverClassName = driverClassName;
		}

		@XmlElement(name = "ConnectString", required = true)
		public Value getConnectString() {
			return connectString;
		}
		public void setConnectString(Value connectString) {
			this.connectString = connectString;
		}

		@XmlElement(name = "LoginUser", required = true)
		public Value getLoginUser() {
			return loginUser;
		}
		public void setLoginUser(Value loginUser) {
			this.loginUser = loginUser;
		}

		@XmlElement(name = "LoginPawd", required = true)
		public Value getLoginPawd() {
			return loginPawd;
		}
		public void setLoginPawd(Value loginPawd) {
			this.loginPawd = loginPawd;
		}
	}



	public static void main(String[] args) throws Exception 
	{

	}

	@XmlElement(name = "T100Interface", required = true)
	public T100Interface getT100Interface() 
	{
		return t100Interface;
	}
	public void setT100Interface(T100Interface t100Interface) 
	{
		this.t100Interface = t100Interface;
	}

	@XmlElement(name = "NRCRestfulInterface", required = true)
	public NRCRestfulInterface getNrcRestfulInterface() {
		return nrcRestfulInterface;
	}
	public void setNrcRestfulInterface(NRCRestfulInterface nrcRestfulInterface) {
		this.nrcRestfulInterface = nrcRestfulInterface;
	}
	
	@XmlElement(name = "Pos_Type", required = true)
	public Value getPos_Type() {
		return Pos_Type;
	}
	public void setPos_Type(Value pos_Type) {
		Pos_Type = pos_Type;
	}
	public Value getCRM_URL() {
		return CRM_URL;
	}
	public void setCRM_URL(Value cRM_URL) {
		CRM_URL = cRM_URL;
	}
	public Value getPAY_URL() {
		return PAY_URL;
	}
	public void setPAY_URL(Value pAY_URL) {
		PAY_URL = pAY_URL;
	}
	public Value getPROM_URL() {
		return PROM_URL;
	}
	public void setPROM_URL(Value pROM_URL) {
		PROM_URL = pROM_URL;
	}
	public Value getPOS_URL() {
		return POS_URL;
	}
	public void setPOS_URL(Value pOS_URL) {
		POS_URL = pOS_URL;
	}

    public Value getMES_URL()
    {
        return MES_URL;
    }

    public void setMES_URL(Value MES_URL)
    {
        this.MES_URL = MES_URL;
    }

    public Value getDCP_URL() {
		return DCP_URL;
	}
	public void setDCP_URL(Value dCP_URL) {
		DCP_URL = dCP_URL;
	}

}