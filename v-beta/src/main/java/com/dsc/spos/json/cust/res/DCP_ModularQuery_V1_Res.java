package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 菜单维护
 * @author yuanyy
 *
 */
public class DCP_ModularQuery_V1_Res extends JsonRes {
	
	private List<level1Elm> memuDatas ;
	
	public List<level1Elm> getMemuDatas() {
		return memuDatas;
	}

	public void setMemuDatas(List<level1Elm> memuDatas) {
		this.memuDatas = memuDatas;
	}

	public class level1Elm{
		
		private String modularNO;
		private String modularName;
		private String modularLevel;
		private String upperModular;
		private String upModularName;
		private String sType;
		private String fType;
		private String proName;
		private String parameter;
		private String status;
		
		private List<function> function ;
		
		private List<level2Elm> childMenuDatas;
		
		
		public List<function> getFunction() {
			return function;
		}

		public void setFunction(List<function> function) {
			this.function = function;
		}

		public List<level2Elm> getChildMenuDatas() {
			return childMenuDatas;
		}
		
		public void setChildMenuDatas(List<level2Elm> childMenuDatas) {
			this.childMenuDatas = childMenuDatas;
		}

		public String getModularNO() {
			return modularNO;
		}

		public void setModularNO(String modularNO) {
			this.modularNO = modularNO;
		}

		public String getModularName() {
			return modularName;
		}

		public void setModularName(String modularName) {
			this.modularName = modularName;
		}

		public String getModularLevel() {
			return modularLevel;
		}

		public void setModularLevel(String modularLevel) {
			this.modularLevel = modularLevel;
		}

		public String getUpperModular() {
			return upperModular;
		}

		public void setUpperModular(String upperModular) {
			this.upperModular = upperModular;
		}

		public String getUpModularName() {
			return upModularName;
		}

		public void setUpModularName(String upModularName) {
			this.upModularName = upModularName;
		}

		public String getsType() {
			return sType;
		}

		public void setsType(String sType) {
			this.sType = sType;
		}

		public String getfType() {
			return fType;
		}

		public void setfType(String fType) {
			this.fType = fType;
		}

		public String getProName() {
			return proName;
		}

		public void setProName(String proName) {
			this.proName = proName;
		}

		public String getParameter() {
			return parameter;
		}

		public void setParameter(String parameter) {
			this.parameter = parameter;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}


	}
	
	public class level2Elm{
		
		private String modularNO;
		private String modularName;
		private String modularLevel;
		private String upperModular;
		private String upModularName;
		private String sType;
		private String fType;
		private String proName;
		private String parameter;
		private String status;
		
		private List<level3Elm> childMenuDatas;
		private List<function> function;
		
		
		public List<level3Elm> getChildMenuDatas() {
			return childMenuDatas;
		}
		public void setChildMenuDatas(List<level3Elm> childMenuDatas) {
			this.childMenuDatas = childMenuDatas;
		}
		public List<function> getFunction() {
			return function;
		}
		public void setFunction(List<function> function) {
			this.function = function;
		}
		public String getModularNO() {
			return modularNO;
		}
		public void setModularNO(String modularNO) {
			this.modularNO = modularNO;
		}
		public String getModularName() {
			return modularName;
		}
		public void setModularName(String modularName) {
			this.modularName = modularName;
		}
		public String getModularLevel() {
			return modularLevel;
		}
		public void setModularLevel(String modularLevel) {
			this.modularLevel = modularLevel;
		}
		public String getUpperModular() {
			return upperModular;
		}
		public void setUpperModular(String upperModular) {
			this.upperModular = upperModular;
		}
		public String getUpModularName() {
			return upModularName;
		}
		public void setUpModularName(String upModularName) {
			this.upModularName = upModularName;
		}
		public String getsType() {
			return sType;
		}
		public void setsType(String sType) {
			this.sType = sType;
		}
		public String getfType() {
			return fType;
		}
		public void setfType(String fType) {
			this.fType = fType;
		}
		public String getProName() {
			return proName;
		}
		public void setProName(String proName) {
			this.proName = proName;
		}
		public String getParameter() {
			return parameter;
		}
		public void setParameter(String parameter) {
			this.parameter = parameter;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
		
	}
	
	public class function{
	
		private String funcNO;
		private String funcName;
		private String funcFType;
		private String funcProName;
		private String funcParameter;
		private String funcStatus;
		public String getFuncNO() {
			return funcNO;
		}
		public void setFuncNO(String funcNO) {
			this.funcNO = funcNO;
		}
		public String getFuncName() {
			return funcName;
		}
		public void setFuncName(String funcName) {
			this.funcName = funcName;
		}
		public String getFuncFType() {
			return funcFType;
		}
		public void setFuncFType(String funcFType) {
			this.funcFType = funcFType;
		}
		public String getFuncProName() {
			return funcProName;
		}
		public void setFuncProName(String funcProName) {
			this.funcProName = funcProName;
		}
		public String getFuncParameter() {
			return funcParameter;
		}
		public void setFuncParameter(String funcParameter) {
			this.funcParameter = funcParameter;
		}
		public String getFuncStatus() {
			return funcStatus;
		}
		public void setFuncStatus(String funcStatus) {
			this.funcStatus = funcStatus;
		}
		
	}
	
	public class level3Elm{
			
			private String modularNO;
			private String modularName;
			private String modularLevel;
			private String upperModular;
			private String upModularName;
			private String sType;
			private String fType;
			private String proName;
			private String parameter;
			private String status;
			
			private List<function> thirdFunc ; 
			
			public List<function> getThirdFunc() {
				return thirdFunc;
			}
			public void setThirdFunc(List<function> thirdFunc) {
				this.thirdFunc = thirdFunc;
			}
			public String getModularNO() {
				return modularNO;
			}
			public void setModularNO(String modularNO) {
				this.modularNO = modularNO;
			}
			public String getModularName() {
				return modularName;
			}
			public void setModularName(String modularName) {
				this.modularName = modularName;
			}
			public String getModularLevel() {
				return modularLevel;
			}
			public void setModularLevel(String modularLevel) {
				this.modularLevel = modularLevel;
			}
			public String getUpperModular() {
				return upperModular;
			}
			public void setUpperModular(String upperModular) {
				this.upperModular = upperModular;
			}
			public String getUpModularName() {
				return upModularName;
			}
			public void setUpModularName(String upModularName) {
				this.upModularName = upModularName;
			}
			public String getsType() {
				return sType;
			}
			public void setsType(String sType) {
				this.sType = sType;
			}
			public String getfType() {
				return fType;
			}
			public void setfType(String fType) {
				this.fType = fType;
			}
			public String getProName() {
				return proName;
			}
			public void setProName(String proName) {
				this.proName = proName;
			}
			public String getParameter() {
				return parameter;
			}
			public void setParameter(String parameter) {
				this.parameter = parameter;
			}
			public String getStatus() {
				return status;
			}
			public void setStatus(String status) {
				this.status = status;
			}
			
			
	}
	
}
