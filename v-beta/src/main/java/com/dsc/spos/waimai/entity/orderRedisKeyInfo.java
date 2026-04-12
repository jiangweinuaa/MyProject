package com.dsc.spos.waimai.entity;

/**
 * 用来记录订单redis缓存主键的枚举值
 * @author 86187
 *
 */
public class orderRedisKeyInfo
{
	/**
	 * 写订单缓存开头主键名字，比如WMORDER:EID:SHOP中WMORDER
	 */
	public static String redis_OrderTableName = "WMORDER";

	/**
	 * 写订单通知消息缓存，比如ORDERNOTIFY:EID:SHOP中ORDERNOTIFY
	 */
	public static String redis_OrderNotify = "ORDERNOTIFY";

	/**
	 * 饿了么门店映射缓存主键key
	 */
	public static String redisKey_elemeMappingshop = "ELM_MappingShop";

	/**
	 * 美团外卖（连锁店）门店映射缓存主键key
	 */
	public static String redisKey_mtMappingshop = "MT_MappingShop";

	/**
	 * 聚宝盆门店映射缓存主键key
	 */
	public static String redisKey_jbpMappingshop = "JBP_MappingShop";

	/**
	 * 美团闪购（连锁店）门店映射缓存主键key
	 */
	public static String redisKey_sgmtMappingshop = "SGMT_MappingShop";

	/**
	 * 抖音外卖（自营商家）门店映射缓存主键key
	 */
	public static String redisKey_dywmMappingshop = "DYSM_MappingShop";

	/**
	 * 京东到家门店映射缓存主键key
	 */
	public static String redisKey_jddjMappingshop = "JDDJ_MappingShop";

	/**
	 * 商有云管家门店映射缓存主键key
	 */
	public static String redisKey_syooMappingshop = "SYOO_MappingShop";

	/**
	 * 写存储打印错误缓存开头主键名字，比如WMORDER_PrintError:EID:SHOP中WMORDER_PrintError
	 */
	public static String redis_OrderPrintError = "WMORDER_PrintError";


	/**
	 * 外卖单推送取消或者退款消息时加锁主键表，WMORDER_ToRefund:EID:ORDERNO中WMORDER_ToRefund
	 */
	public static String redis_OrderRefund = "WMORDER_ToRefund";

	/**
	 * 外卖单订销时加锁主键表，WMORDER_ToSale:EID:ORDERNO中WMORDER_ToSale
	 */
	public static String redis_OrderToSale = "WMORDER_ToSale";


	/**
	 * 批量处理异常订单起讫日期
	 */
	public static String redis_AbnormalOrderProcessToSaleTime = "AbnormalOrderProcess_ToSaleTime";


	/**
	 * 订单暂存
     */
	public static String redis_OrderTemp = "ORDER_TEMP";

}
