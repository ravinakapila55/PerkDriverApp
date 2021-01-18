package com.app.perk99driver.orders;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderHistoryModel implements Serializable
{
   String job_id,order_id,pickup,drop,distance,fare,pickup_lat,pickup_long,drop_lat,drop_long,rest_name,delivery_add,status;

   ArrayList<MenuItemsModel> list;

    public ArrayList<MenuItemsModel> getList() {
        return list;
    }

    public void setList(ArrayList<MenuItemsModel> list) {
        this.list = list;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getPickup_lat() {
        return pickup_lat;
    }

    public void setPickup_lat(String pickup_lat) {
        this.pickup_lat = pickup_lat;
    }

    public String getPickup_long() {
        return pickup_long;
    }

    public void setPickup_long(String pickup_long) {
        this.pickup_long = pickup_long;
    }

    public String getDrop_lat() {
        return drop_lat;
    }

    public void setDrop_lat(String drop_lat) {
        this.drop_lat = drop_lat;
    }

    public String getDrop_long() {
        return drop_long;
    }

    public void setDrop_long(String drop_long) {
        this.drop_long = drop_long;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getDelivery_add() {
        return delivery_add;
    }

    public void setDelivery_add(String delivery_add) {
        this.delivery_add = delivery_add;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
