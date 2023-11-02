package org.trananh.shoppingapp.main.category.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.trananh.shoppingapp.R;
import org.trananh.shoppingapp.controller.CategoryController;
import org.trananh.shoppingapp.controller.ProductController;
import org.trananh.shoppingapp.main.category.CategoryAdapter3;
import org.trananh.shoppingapp.model.BaseUnitOfMeasure;
import org.trananh.shoppingapp.model.HierarchyStructure;
import org.trananh.shoppingapp.model.StructureValue;
import org.trananh.shoppingapp.model.Product;
import org.trananh.shoppingapp.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    public static String TAG = ProductActivity.class.getName();

    private RecyclerView mRecyclerView, mRecycleViewProduct;
    private CategoryAdapter3 mCategoryAdapter3;
    private ProductAdapter productAdapter;
    private StructureValue mainCategory;
    private List<StructureValue> mListCategories;
    private CategoryController categoryController;
    private ImageButton btnBack;
    private ProductController mProductController;
    private TextView tvSearch;
    private RelativeLayout layoutNull;
    private View layoutView;
    private List<Map<String, Object>> mapProducts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        initialize();
    }

    private void initialize(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (getIntent().getExtras() != null){
            mainCategory = (StructureValue) getIntent().getExtras().get("object_category");
            if (mainCategory!=null){
                mProductController = new ProductController();
                btnBack = findViewById(R.id.img_btn_back);
                mRecyclerView = findViewById(R.id.recycle_view);
                mRecycleViewProduct = findViewById(R.id.recycle_view_product);
                tvSearch = findViewById(R.id.tv_search);
                layoutNull = findViewById(R.id.layout_null);
                layoutView = findViewById(R.id.view_layout);

                categoryController = new CategoryController();
                getListCategories();

                tvSearch.setText(mainCategory.getValue());

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
                mRecyclerView.setLayoutManager(linearLayoutManager);

                productAdapter = new ProductAdapter(this, new ProductAdapter.ProductItemListener() {
                    @Override
                    public void onClick(Map<String, Object> map) {
                        Intent intent = new Intent(ProductActivity.this, ProductDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("object_map", (Serializable) map);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onClickCart(Map<String, Object> map) {
                        Toast.makeText(ProductActivity.this, "Click cart", Toast.LENGTH_SHORT).show();
                    }
                });
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                mRecycleViewProduct.setLayoutManager(gridLayoutManager);

                getListProducts(new StructureValue("TATCA"));
                productAdapter.setData(mapProducts);
                showListProduct();
                mRecycleViewProduct.setAdapter(productAdapter);

                mCategoryAdapter3 = new CategoryAdapter3(this, new CategoryAdapter3.CategoryItemListener() {
                    @Override
                    public void onClick(StructureValue category) {
                        getListProducts(category);
                        productAdapter.setData(mapProducts);
                        showListProduct();
                    }
                });
                mCategoryAdapter3.setData(mListCategories);
                mCategoryAdapter3.setSingleSelection(0);
                showListCategory();
                mRecyclerView.setAdapter(mCategoryAdapter3);

                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });

            }
        }
    }
    private void getListCategories(){

        Map<String, Object> map = categoryController.getByParentId(mainCategory.getId());
        mListCategories = new ArrayList<>();
        if (map != null && Double.parseDouble(map.get("statusCode").toString()) == 0) {
            List<Map<String, Object>> lstMap = Constants.gson.fromJson(Constants.gson.toJson(map.get("payload")), new TypeToken<List<Map<String, Object>>>() {}.getType());
            for (Map<String, Object> m : lstMap){
                mListCategories.add(categoryController.convertStructureValue(m));
            }
        }

        if (mListCategories == null)
            mListCategories = new ArrayList<>();
        else {
            if (mListCategories.size() > 1){
                StructureValue category = new StructureValue("TATCA", "", "", 0, "", "", new HierarchyStructure(2));
                mListCategories.add(0, category);
            }
        }
    }
    private void getListProducts(StructureValue category){
        mapProducts = new ArrayList<>();
        Map<String, Object> map;
        if (category.getId().trim().equals("TATCA")){
            map = mProductController.getByCategory(mainCategory.getId().trim());
        } else{
            map = mProductController.getByCategory(category.getId().trim());
        }
        if (map != null && Double.parseDouble(map.get("statusCode").toString()) == 0){
            List<Map<String, Object>> lstMap = Constants.gson.fromJson(Constants.gson.toJson(map.get("payload")), new TypeToken<List<Map<String, Object>>>(){}.getType());
            for (Map<String, Object> m : lstMap){
                mapProducts.add(mProductController.convertProduct(m));
            }
            for (Map<String, Object> m : mapProducts) {
                Product pro = (Product) m.get("product");
                BaseUnitOfMeasure b = (BaseUnitOfMeasure) m.get("baseUnitOfMeasure");
                Log.e(TAG, pro.getName() + " , " + b.getValue());

            }
        }
        
    }

    private void showListProduct(){
        if (mapProducts == null || mapProducts.size() == 0){
            layoutNull.setVisibility(View.VISIBLE);
            mRecycleViewProduct.setVisibility(View.GONE);
        }else{
            layoutNull.setVisibility(View.GONE);
            mRecycleViewProduct.setVisibility(View.VISIBLE);
        }
    }

    private void showListCategory(){
        if (mListCategories == null || mListCategories.size() == 0){
            layoutView.setVisibility(View.GONE);
        }else
            layoutView.setVisibility(View.VISIBLE);
    }
    public static void Finish1(){

    }
}