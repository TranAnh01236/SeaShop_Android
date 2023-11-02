package org.trananh.shoppingapp.controller;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.trananh.shoppingapp.api.ApiService;
import org.trananh.shoppingapp.model.BaseUnitOfMeasure;
import org.trananh.shoppingapp.model.PriceDetail;
import org.trananh.shoppingapp.model.Product;
import org.trananh.shoppingapp.model.StructureValue;
import org.trananh.shoppingapp.model.UnitOfMeasure;
import org.trananh.shoppingapp.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductController {
    public static final String TAG = ProductController.class.getName();
    public ProductController(){

    }

    public Map<String, Object> getAll() {
        Map<String, Object> map;
        try {
            map = (Map<String, Object>)ApiService.apiService.getAllProducts().execute().body();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }

        return map;

//        Map<String, Object> map;
//        try {
//            map = (Map<String, Object>) ApiService.apiService.getAllCategory().execute().body();
//        } catch (IOException e) {
//            Log.e("error", e.getMessage());
//            return null;
//        }
//        return map;
    }

    public Map<String, Object> getByCategory(String categoryId){
        Map<String, Object> map;
        try {
            map = ApiService.apiService.getProductByCategory(categoryId).execute().body();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }
        return map;
    }

    public Map<String, Object> getDetail(String productId){
        Map<String, Object> map;
        try {
            map = ApiService.apiService.getProductDetail(productId).execute().body();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }



        return map;
    }

    public Map<String, Object> convertProduct(Map<String, Object> map){

        Map<String, Object> rsMap = new HashMap<>();

        Product product = new Product();
        product.setId(map.get("id").toString());
        product.setName(map.get("name").toString());
        product.setDescription(map.get("description").toString());
        product.setCategory(new StructureValue(map.get("categoryId").toString()));
        product.setImageUrl(map.get("baseUnitOfMeasureImageUrl").toString());

        BaseUnitOfMeasure baseUnitOfMeasure = new BaseUnitOfMeasure();
        baseUnitOfMeasure.setId(map.get("baseUnitOfMeasureId").toString());
        baseUnitOfMeasure.setValue(map.get("baseOfUnitMeasureName").toString());



        rsMap.put("product", product);
        rsMap.put("baseUnitOfMeasure", baseUnitOfMeasure);
        rsMap.put("price", Double.parseDouble(map.get("price").toString()));

        return rsMap;
    }

    public Map<String, Object> convertProductDetail(Map<String, Object> map){

        Map<String, Object> rsMap = new HashMap<>();

        Product product = new Product();
        product.setId(map.get("id").toString());
        product.setName(map.get("name").toString());
        product.setDescription(map.get("description").toString());
        product.setImageUrl(map.get("imageUrl").toString());

        Map<String, Object> mapCategory = Constants.gson.fromJson(Constants.gson.toJson(map.get("category")), Map.class);
        StructureValue structureValue = new StructureValue();
        structureValue.setId(mapCategory.get("id").toString());
        structureValue.setValue(mapCategory.get("value").toString());
        product.setCategory(structureValue);

        List<Map<String, Object>> lstMapUnit = Constants.gson.fromJson(Constants.gson.toJson(map.get("unitOfMeasures")), new TypeToken<List<Map<String, Object>>>(){}.getType());
        List<UnitOfMeasure> unitOfMeasures = new ArrayList<>();
        List<PriceDetail> priceDetails = new ArrayList<>();
        if (lstMapUnit != null){
            for (Map<String, Object> m : lstMapUnit){
                UnitOfMeasure u = new UnitOfMeasure();

                u.setId((int)Double.parseDouble(m.get("id").toString()));
                u.setValue(m.get("value").toString());
                u.setQuantity((int)Double.parseDouble(m.get("quantity").toString()));
                u.setImageUrl(m.get("imageUrl").toString());
                u.setBaseUnitOfMeasure(new BaseUnitOfMeasure(m.get("baseOfUnitMeasure").toString()));

                unitOfMeasures.add(u);
                PriceDetail priceDetail = new PriceDetail();
                try{
                    priceDetail.setPrice(Double.parseDouble(m.get("price").toString()));
                    priceDetail.setUnitOfMeasure(new UnitOfMeasure((int)Double.parseDouble(m.get("id").toString())));
                }catch (Exception e){
                    e.printStackTrace();
                }
                priceDetails.add(priceDetail);
            }
        }

        rsMap.put("product", product);
        rsMap.put("category", structureValue);
        rsMap.put("unitOfMeasures", unitOfMeasures);
        rsMap.put("priceDetails", priceDetails);

        return rsMap;
    }
}
