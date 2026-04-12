package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_GetAllGroup_Open
 * 服务说明：获取全部线上商品分组
 * @author jinzma 
 * @since  2020-09-25
 */
public class DCP_GetAllGroup_OpenRes extends JsonRes{
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
 
	public class level1Elm{
		private String groupId;
		private String groupName;
		private String isShare;
		private List<level2Elm> subGroup;
		
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		public List<level2Elm> getSubGroup() {
			return subGroup;
		}
		public void setSubGroup(List<level2Elm> subGroup) {
			this.subGroup = subGroup;
		}
		public String getIsShare() {
			return isShare;
		}
		public void setIsShare(String isShare) {
			this.isShare = isShare;
		}
	}
	public class level2Elm{
		private String groupId;
		private String groupName;
		private String isShare;
		
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		public String getIsShare() {
			return isShare;
		}
		public void setIsShare(String isShare) {
			this.isShare = isShare;
		}
	}
}
