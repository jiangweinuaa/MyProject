package com.dsc.spos.scheduler.job;

import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.dao.DsmDAO;

public class StaticInfo {
	
	public static SPosConfig psc;


	public static String Using_Redis;//POS门店开启Redis   1:开启Redis  0:不开启Redis
	/**
	 * 数据库类型 0: oracle数据库  1:postgresql数据库
	 */
	public static int dbType;
	
	public static String Using_GAODEMAP_key;//高德地图天气预报平台应用key值
	public static String DCP_Slave="0"; //DCP从服务 1:是 N:否
	public static String waimaiMTAPPID;//美团外卖APPID
	public static String waimaiMTSignKey;//美团外卖Key
	public static String waimaiMTIsJBP;//美团外卖是否使用聚宝盆
	public static String waimaiELMAPPKey;//饿了么外卖KEY
	public static String waimaiELMSecret;//饿了么外卖Secret
	public static String waimaiELMSandbox;//饿了么外卖是否沙箱测试环境
	public static boolean waimaiELMIsSandbox;//饿了么外卖是否沙箱测试环境
	
	public static String waimaiJDDJAPPKey = "19d2e2b16a3e411ca123746f2e3ff98a";//京东到家KEY
	public static String waimaiJDDJSecret = "5e0f3785bcb3475c9b5015f344c74cc7";//京东到家Secret
	public static String waimaiJDDJToken = "f76aa439-869a-467b-b5e8-412fd5e01b5c";//京东到家授权Token
	public static String waimaiJDDJSandbox ="N";//京东到家是否沙箱环境
	
	
	//2018-08-29新增微商城
	public static String microMarkKey;//微商城key
	public static String microMarkSign;//微商城sign
	public static String microMarkHttpPost;//微商城请求地址
	
	public static DsmDAO dao;//POS门店

    public static DsmDAO dao_pos2;//DCP2.0

    public static DsmDAO dao_crm2;//CRM2.0

    public static String KeepLogsDay; //保留天数范围内的日志  默认:如果不设置 则保留45天内的日志


	public static long lUsercount=0L;//并发用户数
	
	public static String sOrgTopName="";//取组织信息中集团公司级5条记录组织名称

	public static String OMS_Url; //分销系统接口地址
	public static String OMS_Eid; //分销系统企业编号
    public static String OMS_ERP_Eid; //ERP企业编号

    public static String RuiXiang_Url; //锐翔系统接口地址
    public static String RuiXiang_Secret; //锐翔系统签名秘钥
    public static String RuiXiang_Appid; //锐翔系统Appid
    public static String RuiXiang_Eid; //锐翔系统企业编号
    public static String MES_URL; //MES地址
    public static String DCP_URL; //DCP地址
    public static String CRM_URL; //CRM地址
    public static String PAY_URL; //PAY地址
    public static String POS_URL; //POS地址
    public static String PROM_URL; //PROM地址
    public static String PICTURE_URL;//图片地址
    public static String MES_INNER_URL; //MES内网地址
    public static String DCP_INNER_URL; //DCP内网地址
    public static String CRM_INNER_URL; //CRM内网地址
    public static String PAY_INNER_URL; //PAY内网地址
    public static String POS_INNER_URL; //POS内网地址
    public static String PROM_INNER_URL; //PROM内网地址
    public static String PICTURE_INNER_URL;//图片内网地址

    public static String waimai_digiwin_ISV_MT_developerId = "100146";//美团外卖鼎捷服务商开发者id
    public static String waimai_digiwin_ISV_MT_signKey = "jevw2dkj37mb8pun";//美美团外卖鼎捷服务商signKey

    public static String waimai_digiwin_ISV_ELM_appKey = "";//饿了么外卖鼎捷服务商应用ID
    public static String waimai_digiwin_ISV_ELM_appSecret = "";//美美团外卖鼎捷服务商应用密钥

    public static String waimai_digiwin_ISV_ELM_appKey_test = "";//饿了么外卖鼎捷服务商测试环境应用ID
    public static String waimai_digiwin_ISV_ELM_appSecret_test = "";//美美团外卖鼎捷服务商测试环境应用密钥
	
}
