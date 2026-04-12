package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_WarningMonitoringExportRes extends JsonBasicRes {
	
	private level1Elm  datas;
	
	public level1Elm getDatas() {
		return datas;
	}

	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String filePath;//导出路径
		
		private String excelFileName; // 导出文件名

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getExcelFileName() {
		return excelFileName;
	}

	public void setExcelFileName(String excelFileName) {
		this.excelFileName = excelFileName;
	}
		
		
		
	}

}
