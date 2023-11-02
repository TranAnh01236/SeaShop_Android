package org.trananh.shoppingapp.controller;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.trananh.shoppingapp.api.ApiService;
import org.trananh.shoppingapp.model.StructureValue;
import org.trananh.shoppingapp.util.Constants;
import org.trananh.shoppingapp.util.MyHttpResponse;
import org.trananh.shoppingapp.util.MyHttpResponseArray;

import java.io.IOException;
import java.util.Map;

public class CategoryController {

    public static final String TAG = CategoryController.class.getName();

    public CategoryController(){

    }
    public Map<String, Object> getAll() {
        Map<String, Object> map;
        try {
            map = (Map<String, Object>) ApiService.apiService.getAllCategory().execute().body();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }
        return map;
    }
    public Map<String, Object> getByLevel(int level) {
        Map<String, Object> map;
        try {
            map = (Map<String, Object>) ApiService.apiService.getCategoryByLevel(level).execute().body();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }
        return map;
    }
    public Map<String, Object> getByParentId(String parentId){
        Map<String, Object> map;
        try {
            map = (Map<String, Object>) ApiService.apiService.getCategoryByParentId(parentId).execute().body();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }
        return map;
    }
    public StructureValue convertStructureValue(Map map){
        StructureValue structureValue = new StructureValue();
        structureValue.setId(map.get("id").toString());
        structureValue.setValue(map.get("value").toString());
        structureValue.setDescription(map.get("description").toString());
        structureValue.setImageUrl(map.get("imageUrl").toString());
        structureValue.setLevel((int)Double.parseDouble(map.get("level").toString()));
        Map<String, Object> parent = Constants.gson.fromJson(Constants.gson.toJson(map.get("parent")), Map.class);
        if (parent != null)
            structureValue.setParentId(parent.get("id").toString());
        return structureValue;
    }
}
