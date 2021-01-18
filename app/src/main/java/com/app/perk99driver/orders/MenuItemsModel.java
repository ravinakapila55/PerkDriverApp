package com.app.perk99driver.orders;

import java.io.Serializable;

public class MenuItemsModel implements Serializable {

    String item_name,qty;

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }


}
