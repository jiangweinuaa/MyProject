package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_FinancialAuditExportGet_u8cRes extends JsonBasicRes
{

    private level1Elm datas;


    @Data
    public class level1Elm
    {
        private String prop_roottag;
        private String prop_billtype;
        private String prop_replace;
        private String prop_receiver;
        private String prop_sender;
        private String prop_isexchange;
        private String prop_filename;
        private String prop_proc;
        private String prop_operation;
        private levelVoucher voucher;
    }

    @Data
    public class levelVoucher
    {
        private String prop_id;
        private levelVoucher_Head voucher_head;
        private levelVoucher_Body voucher_body;
    }

    @Data
    public class levelVoucher_Head
    {
        private String company;
        private String voucher_type;
        private String fiscal_year;
        private String accounting_period;
        private String voucher_id;
        private String attachment_number;
        private String date;
        private String enter;
        private String signature;
        private String voucher_making_system;
    }

    @Data
    public class levelVoucher_Body
    {
        private List<levelVoucher_Entry> entry;

    }

    @Data
    public class levelVoucher_Entry
    {
        private String entry_id;
        private String shopId;
        private String bDate;
        private String orgId_out;
        private String orgName_out;
        private String compId_out;
        private String compName_out;
        private String account_code;
        private String Abstract;
        private String currency;
        private String primary_debit_amount;
        private String secondary_debit_amount;
        private String natural_debit_currency;
        private String credit_quantity;
        private String primary_credit_amount;
        private String secondary_credit_amount;
        private String natural_credit_currency;
        private levelAuxiliary auxiliary_accounting;
    }

    @Data
    public class levelAuxiliary
    {
        private levelAuxiliary_Item item;
    }

    @Data
    public class levelAuxiliary_Item
    {
        private String prop_name;
        private String value_text;
    }


}
