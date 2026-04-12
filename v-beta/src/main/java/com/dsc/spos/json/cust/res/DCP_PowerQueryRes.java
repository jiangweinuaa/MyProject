package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 权限查询服务 2018-09-21	
 * @author yuanyy
 *
 */
public class DCP_PowerQueryRes extends JsonRes {
	/**
	 * {		
		 "success": true,	成功否	
		 "serviceStatus": "000",	服務狀態代碼	
		 "serviceDescription": "服務執行成功",	服務狀態說明	
		 "powerDatas": [{		
		   "modularNO": "12",	模块编码	
		   "modularName": "门店管理",	模块名称	
		   "isPower": "Y",	是否授权	
		   "sType": "1",	所属系统	0表示pos菜单  1表示门店菜单 2 表示区域菜单 3表示总部菜单
		 "functionPower": [],		
		   "children": [{		
		      "modularNO": "1201",	模块编码	
		      "modularName": "物流管理",	模块名称	
		      "isPower": "Y",	是否授权	
		    "sType": "1",	所属系统	
		    "functionPower": [],		
		      "children": [{		
		         "modularNO": "120101",	模块编码	
		         "modularName": "要货申请",	模块名称	
		         "isPower": "Y",	是否授权	
		         "sType": "1",	所属系统	
		       "functionPower": [{		
		            "funcNO": "120101001",	功能编码	
		            "funcName": "新增",	功能名称	
		            "isPower": "Y",	是否授权	
		            "powerType": "1"	权限类型	
		         }]		
		 }, {		
		         "modularNO": "120102",	模块编码	
		         "modularName": "配送收货",	模块名称	
		         "isPower": "Y",	是否授权	
		         "sType": "1",	所属系统	
		         "functionPower": []		
		    }]		
		 }]		
		 }]		
		}		
	 */

	private List<level1Elm> powerDatas;

	public List<level1Elm> getPowerDatas() {
		return powerDatas;
	}

	public void setPowerDatas(List<level1Elm> powerDatas) {
		this.powerDatas = powerDatas;
	}

	public class level1Elm {
		private String modularNo;
		private String modularName;
		private String isPower;
		private String sType;

		private List<functionPower> functionPower;
		private List<level1Elm> children;

		public String getModularNo() {
			return modularNo;
		}

		public void setModularNo(String modularNo) {
			this.modularNo = modularNo;
		}

		public String getModularName() {
			return modularName;
		}

		public void setModularName(String modularName) {
			this.modularName = modularName;
		}

		public String getIsPower() {
			return isPower;
		}

		public void setIsPower(String isPower) {
			this.isPower = isPower;
		}

		public String getsType() {
			return sType;
		}

		public void setsType(String sType) {
			this.sType = sType;
		}

		public List<functionPower> getFunctionPower() {
			return functionPower;
		}

		public void setFunctionPower(List<functionPower> functionPower) {
			this.functionPower = functionPower;
		}

		public List<level1Elm> getChildren() {
			return children;
		}

		public void setChildren(List<level1Elm> children) {
			this.children = children;
		}


	}

	public class functionPower {
		private String funcNo;
		private String funcName;
		private String isPower;
		private String powerType;

        private String proName;

		public String getFuncNo() {
			return funcNo;
		}
		public void setFuncNo(String funcNo) {
			this.funcNo = funcNo;
		}
		public String getFuncName() {
			return funcName;
		}
		public void setFuncName(String funcName) {
			this.funcName = funcName;
		}
		public String getIsPower() {
			return isPower;
		}
		public void setIsPower(String isPower) {
			this.isPower = isPower;
		}
		public String getPowerType() {
			return powerType;
		}
		public void setPowerType(String powerType) {
			this.powerType = powerType;
		}

        public String getProName() {
            return proName;
        }

        public void setProName(String proName) {
            this.proName = proName;
        }
    }

}
