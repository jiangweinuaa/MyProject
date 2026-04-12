package com.dsc.spos.utils.invoice.order.dto;

import java.util.List;

/**
 * “根据订单编号查询发票”方法响应和回调数据
 */
public class InvoiceResult extends Result {
    /**
     * 发票列表
     */
    private List<Invoice> invoices;

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}
