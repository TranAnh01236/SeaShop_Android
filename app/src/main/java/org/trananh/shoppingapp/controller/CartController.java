package org.trananh.shoppingapp.controller;

import android.util.Log;

import org.trananh.shoppingapp.api.ApiService;
import org.trananh.shoppingapp.model.BaseUnitOfMeasure;
import org.trananh.shoppingapp.model.Cart;
import org.trananh.shoppingapp.model.Product;
import org.trananh.shoppingapp.model.UnitOfMeasure;
import org.trananh.shoppingapp.util.Constants;
import org.trananh.shoppingapp.util.MyHttpResponseArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CartController {
    public CartController(){

    }

    public Map<String, Object> getAll() {
        Map<String, Object> map;
        try {
            map = ApiService.apiService.getAllCart(Constants.token).execute().body();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }
        return map;
    }

    public Map<String, Object> addCart(int id) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        Map<String, Object> map;
        try {
            map = ApiService.apiService.addCart(Constants.token, m).execute().body();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }
        return map;
    }

    public Map<String, Object> delete(int id){
        Map<String, Object> map;
        try {
            map = ApiService.apiService.deleteCart(Constants.token, id).execute().body();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }
        return map;
    }

    public Map<String, Object> subCart(int id) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        Map<String, Object> map;
        try {
            map = ApiService.apiService.subCart(Constants.token, m).execute().body();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }
        return map;
    }

    public Map<String, Object> convertCart(Map<String, Object> map){

        Map<String, Object> rsMap = new HashMap<>();

        Cart cart = new Cart();
        cart.setId((int)Double.parseDouble(map.get("cartId").toString()));
        cart.setQuantity((int)Double.parseDouble(map.get("quantity").toString()));

        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setId((int)Double.parseDouble(map.get("unitOfMeasureId").toString()));
        unitOfMeasure.setValue(map.get("unitOfMeasureName").toString());
        unitOfMeasure.setImageUrl(map.get("unitOfMeasureImageUrl").toString());

        cart.setUnitOfMeasure(unitOfMeasure);

        BaseUnitOfMeasure baseUnitOfMeasure = new BaseUnitOfMeasure(map.get("baseUnitOfMeasureId").toString());
        Product product = new Product(map.get("productId").toString());
        product.setName(map.get("productName").toString());

        rsMap.put("cart", cart);
        rsMap.put("price", Double.parseDouble(map.get("price").toString()));
        rsMap.put("product", product);
        rsMap.put("baseUnitOfMeasure", baseUnitOfMeasure);

        return rsMap;

    }
}
