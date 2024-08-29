package com.ml.oilpricechecker.models;
public class NichollOilsFuelModel {

    public NichollOilsFuelModel(final int orderQty) {
        this.OrderQty = orderQty;
    }

    public String DeliveryOptionWeighting = "Standard Delivery";
    public int OrderQty;
    public String Postcode = "BT47 4HR";
    public String CountryCode = "UK";
    public String ProductCode = "0002";
    public String DeliveryScheduleWeighting = "Web 5 Day";

}
