package org.trananh.shoppingapp.model;

import java.io.Serializable;

public class Cart implements Serializable {
    private int id;
    private UnitOfMeasure unitOfMeasure;
    private int quantity;
    private User user;

    public Cart() {
    }

    public Cart(int id) {
        this.id = id;
    }

    public Cart(int id, UnitOfMeasure unitOfMeasure, int quantity, User user) {
        this.id = id;
        this.unitOfMeasure = unitOfMeasure;
        this.quantity = quantity;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", unitOfMeasure=" + unitOfMeasure +
                ", quantity=" + quantity +
                ", user=" + user +
                '}';
    }
}
