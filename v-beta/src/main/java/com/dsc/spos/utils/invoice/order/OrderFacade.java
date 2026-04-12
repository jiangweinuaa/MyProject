package com.dsc.spos.utils.invoice.order;


import com.dsc.spos.utils.invoice.order.dto.InvoiceResult;
import com.dsc.spos.utils.invoice.order.dto.Order;
import com.dsc.spos.utils.invoice.order.dto.OrderBase;
import com.dsc.spos.utils.invoice.order.dto.Result;

/**
 * 订单接口
 */
public interface OrderFacade {
    /**
     * (异步)开具蓝字发票异步方法
     */
    Result kpAsync(Order order) throws Exception;

    /**
     * (异步)开具红字发票异步方法
     */
    Result chAsync(Order order) throws Exception;

    /**
     * (异步)对开票失败的订单重新开票异步方法
     */
    Result retryKpAsync(OrderBase orderBase) throws Exception;

    /**
     * (同步)取消订单
     */
    Result cancel(OrderBase orderBase) throws Exception;

    /**
     * (同步)根据订单编号查询发票
     */
    InvoiceResult cxByOrderNo(OrderBase orderBase) throws Exception;
}
