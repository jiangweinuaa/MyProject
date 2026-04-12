package com.dsc.spos.service.webhook;

public enum WebHookEventEnum {

    /*
     * 会员折扣率设置发生改变
     */
    CARDTYPEDISCCHANGED,	//折扣率变更
    CARDTYPE,				//卡类型变更
    COUPONTYPE,				//券类型变更

    MALLORDER,				//商城下单

    CREATEMEMBER,			//新增会员
    UPDATEMEMBER,			//修改会员

    UPDATEPOINT,			//积分变更
    UPDATEAMOUNT,			//余额变更
    UPDATEACCOUNT,			//------------账户变更，不作为事件，拆分到积分、余额变更
    UPDATELEVEL,			//会员等级变更
    MEMBERCONSUME,			//会员消费记录
    SHOP,					//店铺新增或保存
    ALLSHOP,				//同步所有店铺     --不是一个事件
    GOODS,					//商品新增或保存
    ALLGOODS,				//同步所有商品     --不是一个事件
    CATEGORY,				//品类新增或保存
    ALLCATEGORY,			//同步所有品类     --不是一个事件

    COUPONSEND,             //优惠券发券
    COUPONUSED,             //优惠券核销
    COUPONREFUND,           //优惠券退券
    COUPONRECOVER,          //优惠券回收

    STOCKSYNC,              //库存同步
}
