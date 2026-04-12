package com.dsc.spos.model;

import java.util.List;

import com.dsc.spos.orm.ORMField;

public class PLATFORM_SREGISTERHEAD 
{
	@ORMField(ColumnLength = 20,ColumnType = "NVARCHAR2",ColumnNullable ="N",ColumnPrimarykey = "Y",Comment="客户编号")
	public String CUSTOMERNO;
//注册号码
	@ORMField(ColumnLength = 40,ColumnType = "NVARCHAR2",ColumnNullable ="N",ColumnPrimarykey = "Y",Comment="注册号")
	private String TERMINALLICENCE;
	@ORMField(ColumnLength = 40,ColumnType = "NVARCHAR2",Comment="机器码")
	private String MACHINECODE;
//是否已注册
	@ORMField(ColumnLength = 10,ColumnType = "NVARCHAR2",Comment="是否已注册")
	private String ISREGISTER;
//客户端注册URL
	@ORMField(ColumnLength = 100,ColumnType = "NVARCHAR2",Comment="客户端注册URL")
	private String REGISTERURL;
//是否首次注册
	@ORMField(ColumnLength = 10,ColumnType = "NVARCHAR2",Comment="是否首次注册")
	private String ISFIRST;
//机器名称
	@ORMField(ColumnLength = 40,ColumnType = "NVARCHAR2",Comment="机器名称")
	private String MACHINENAME;
//主板序列号
	@ORMField(ColumnLength = 100,ColumnType = "NVARCHAR2",Comment="主板序列号")
	private String MOTHERBOARDSN;
	
	//硬盘序列号
	@ORMField(ColumnLength = 100,ColumnType = "NVARCHAR2",Comment="硬盘序列号")
	private String HARDDISKSN;
	
	//CPU序列号
	@ORMField(ColumnLength = 100,ColumnType = "NVARCHAR2",Comment="CPU序列号")
	private String CPUSERIAL;
	
	//网卡MAC地址
	@ORMField(ColumnLength = 100,ColumnType = "NVARCHAR2",Comment="网卡MAC地址")
	private String SMAC;
//网卡MAC地址
	@ORMField(ColumnLength = 200,ColumnType = "NVARCHAR2",Comment="网卡MAC地址")
	private String INSTALLPATH;
	@ORMField(ColumnLength = 20,ColumnType = "NVARCHAR2",ColumnDefaultValue="to_char(systimestamp, 'yyyymmddhh24missff3')",Comment="系统时间")
	private String SDATETIME;
	@ORMField(ColumnLength = 200,ColumnType = "NVARCHAR2",Comment="备注")
	private String MEMO;
	
	
	public String getCUSTOMERNO() {
		return CUSTOMERNO;
	}
	public void setCUSTOMERNO(String cUSTOMERNO) {
		CUSTOMERNO = cUSTOMERNO;
	}
	public String getTERMINALLICENCE() {
		return TERMINALLICENCE;
	}
	public void setTERMINALLICENCE(String tERMINALLICENCE) {
		TERMINALLICENCE = tERMINALLICENCE;
	}
	public String getMACHINECODE() {
		return MACHINECODE;
	}
	public void setMACHINECODE(String mACHINECODE) {
		MACHINECODE = mACHINECODE;
	}
	public String getISREGISTER() {
		return ISREGISTER;
	}
	public void setISREGISTER(String iSREGISTER) {
		ISREGISTER = iSREGISTER;
	}
	public String getREGISTERURL() {
		return REGISTERURL;
	}
	public void setREGISTERURL(String rEGISTERURL) {
		REGISTERURL = rEGISTERURL;
	}
	public String getISFIRST() {
		return ISFIRST;
	}
	public void setISFIRST(String iSFIRST) {
		ISFIRST = iSFIRST;
	}
	public String getMACHINENAME() {
		return MACHINENAME;
	}
	public void setMACHINENAME(String mACHINENAME) {
		MACHINENAME = mACHINENAME;
	}
	public String getMOTHERBOARDSN() {
		return MOTHERBOARDSN;
	}
	public void setMOTHERBOARDSN(String mOTHERBOARDSN) {
		MOTHERBOARDSN = mOTHERBOARDSN;
	}
	public String getHARDDISKSN() {
		return HARDDISKSN;
	}
	public void setHARDDISKSN(String hARDDISKSN) {
		HARDDISKSN = hARDDISKSN;
	}
	public String getCPUSERIAL() {
		return CPUSERIAL;
	}
	public void setCPUSERIAL(String cPUSERIAL) {
		CPUSERIAL = cPUSERIAL;
	}
	public String getSMAC() {
		return SMAC;
	}
	public void setSMAC(String sMAC) {
		SMAC = sMAC;
	}
	public String getINSTALLPATH() {
		return INSTALLPATH;
	}
	public void setINSTALLPATH(String iNSTALLPATH) {
		INSTALLPATH = iNSTALLPATH;
	}
	public String getSDATETIME() {
		return SDATETIME;
	}
	public void setSDATETIME(String sDATETIME) {
		SDATETIME = sDATETIME;
	}
	public String getMEMO() {
		return MEMO;
	}
	public void setMEMO(String mEMO) {
		MEMO = mEMO;
	}
	
	
}
