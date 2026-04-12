package com.dsc.spos.service.webhook;

import org.apache.log4j.Logger;

public class WebHookThread implements Runnable {

    static Logger logger = Logger.getLogger(WebHookThread.class);

    //当启用了业务消息推送地址，当特定的业务发生改变时，会推送给指定的服务器
    //消息推送采用后台线程处理

    public WebHookThread()
    {

    }

    private WebHookBaseMessage message;
    /*
     * 最基础的，由外部构建好Message传进来,NeedFillInThread 表示是否需要在线程中从新取数据
     */
    public WebHookThread(WebHookBaseMessage WebHookMessage) {

        message = WebHookMessage;
    }


    //Method method = null;
    //String[] params = null;


    String eid = "";
    String cardtypeid="";
    String coupontypeid = "";
    String memberid = "";
    String billtype = "";
    String consumeBillNo = "";
    String cardConsumeBillNo = "";
    String reverse = "0";
    String shopid="";
    String goodsid = "";
    String categoryid = "";
    String bizNo = "";
    String billno = "";
    String warehouse = "";//仓库
    Integer type = 0;
    String couponSendByMemberId = "N";

    WebHookEventEnum WebHookEvent = null;
    String orderno = "";

    public void Goods(String EID,String GoodsID)
    {
        try {
            eid = EID;
            goodsid = GoodsID;
            WebHookEvent = WebHookEventEnum.GOODS;
        }
        catch(Exception ex)
        {
            logger.info("\r\nGoods:" + ex.toString());
        }

    }
    public void AllGoods(String EID)
    {
        try {
            eid = EID;
            WebHookEvent = WebHookEventEnum.ALLGOODS;
        }
        catch(Exception ex)
        {
            logger.info("\r\nAllGoods:" + ex.toString());
        }

    }

    /**
     * 同步库存给三方
     * @param EID 企业编码
     * @param shopId 门店编码
     * @param billNo 异动单号
     */
    public void stockSync(String EID,String shopId,String billNo )
    {
        try {
            eid = EID;
            shopid = shopId;
            billno = billNo;//异动单号
            WebHookEvent = WebHookEventEnum.STOCKSYNC;
        }
        catch(Exception ex)
        {
            logger.info("\r\nstockSync:" + ex.toString());
        }

    }

    public void run() {

        try
        {
            execute();
        }
        catch(Exception ex)
        {
            String eventid ="";
            if (WebHookEvent != null)
            {
                eventid = WebHookEvent.name();
            }
            logger.error("\r\nWebHookThread:eventid:"+eventid+":" + ex.toString());

        }
    }

    public void execute() throws Exception
    {
        if(WebHookEvent.equals(WebHookEventEnum.GOODS))
        {
            WebHookGoodsService service = new WebHookGoodsService();
            service.execute(eid,goodsid);
        }
        if(WebHookEvent.equals(WebHookEventEnum.ALLGOODS))
        {
            WebHookGoodsService service = new WebHookGoodsService();
            service.executeAll(eid);
        }
        if(WebHookEvent.equals(WebHookEventEnum.STOCKSYNC))
        {
            WebHookStockSyncService service = new WebHookStockSyncService();
            service.execute(eid,shopid,billno);
        }
    }


}
