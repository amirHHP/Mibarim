package com.mibarim.main.models.Plus;

import java.io.Serializable;

/**
 * Created by Hamed on 4/6/2016.
 */
public class PasPayModel implements Serializable {
    public String BankLink;
    public String MerchantCode;
    public String TerminalCode;
    public String InvoiceNumber;
    public String InvoiceDate;
    public String Amount;
    public String RedirectAddress;
    public String Action;
    public String TimeStamp;
    public String Sign;
    public long ReqId;
}
