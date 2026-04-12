package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

/**
 * 平台支付方式映射查询
 * @author yuanyy 2019-04-24
 *
 */
public class DCP_PayMappingQueryRes extends JsonRes
{
	private List<level1Elm> datas;

	public List<level1Elm> getDatas()
	{
		return datas;
	}

	public void setDatas(List<level1Elm> datas)
	{
		this.datas = datas;
	}


	public class level1Elm
	{
		private String channelType;
		private String channelTypeName;
		private String channelId;
		private String channelIdName;
		private String payType;
		private String payName;
		private String order_paycode;
		private String order_payname;
        private String createBy;
        private String create_date;
        private String modifyBy;
        private String modify_date;


		public String getChannelType() {
			return channelType;
		}

		public void setChannelType(String channelType) {
			this.channelType = channelType;
		}

		public String getChannelTypeName() {
			return channelTypeName;
		}

		public void setChannelTypeName(String channelTypeName) {
			this.channelTypeName = channelTypeName;
		}

		public String getChannelId() {
			return channelId;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}

		public String getChannelIdName() {
			return channelIdName;
		}

		public void setChannelIdName(String channelIdName) {
			this.channelIdName = channelIdName;
		}

		public String getPayType() {
			return payType;
		}

		public void setPayType(String payType) {
			this.payType = payType;
		}

		public String getPayName() {
			return payName;
		}

		public void setPayName(String payName) {
			this.payName = payName;
		}

		public String getOrder_paycode() {
			return order_paycode;
		}

		public void setOrder_paycode(String order_paycode) {
			this.order_paycode = order_paycode;
		}

		public String getOrder_payname() {
			return order_payname;
		}

		public void setOrder_payname(String order_payname) {
			this.order_payname = order_payname;
		}

        public String getCreateBy() {
            return createBy;
        }

        public void setCreateBy(String createBy) {
            this.createBy = createBy;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public String getModifyBy() {
            return modifyBy;
        }

        public void setModifyBy(String modifyBy) {
            this.modifyBy = modifyBy;
        }

        public String getModify_date() {
            return modify_date;
        }

        public void setModify_date(String modify_date) {
            this.modify_date = modify_date;
        }
    }

}	
