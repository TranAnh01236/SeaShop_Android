package org.trananh.shoppingapp.main.category.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;

import org.trananh.shoppingapp.R;
import org.trananh.shoppingapp.controller.ProductController;
import org.trananh.shoppingapp.controller.UnitOfMeasureController;
import org.trananh.shoppingapp.main.MainActivity;
import org.trananh.shoppingapp.model.BaseUnitOfMeasure;
import org.trananh.shoppingapp.model.PriceDetail;
import org.trananh.shoppingapp.model.Product;
import org.trananh.shoppingapp.model.StructureValue;
import org.trananh.shoppingapp.model.UnitOfMeasure;
import org.trananh.shoppingapp.util.Constants;
import org.trananh.shoppingapp.util.MyHttpResponseArray;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String TAG = ProductDetailActivity.class.getName();

    private ImageButton btnBack;
    private Map<String, Object> mainMap, mainDetailProductMap;
    private BaseUnitOfMeasure mainBaseUnitOfMeasure;
    private Product mainProduct;
    private UnitOfMeasureController mUnitOfMeasureController;
    private UnitAdapter mUnitAdapter;
    private RecyclerView mRecyclerViewUnit;
    private TextView tvName, tvDescription, tvViewAll, tvCart, tvPrice;
    private ImageView image;

    private RecyclerView recyclerViewProduct;
    private ProductController productController;
    private List<Product> mProducts;
    private ProductAdapter1 mProductAdapter;
    private RelativeLayout relativeLayoutCart;

    private double mainPrice = 0;
    private UnitOfMeasure mainUnit = new UnitOfMeasure();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        initialize();
    }

    private void initialize(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (getIntent().getExtras() != null){
            mainMap = (Map<String, Object>) getIntent().getExtras().get("object_map");
            if (mainMap != null){
                productController = new ProductController();
                Product mainProduct1 = (Product) mainMap.get("product");
                mainBaseUnitOfMeasure = (BaseUnitOfMeasure) mainMap.get("baseUnitOfMeasure");
                Map<String, Object> map = productController.getDetail(mainProduct1.getId().trim());
                if (map != null && Double.parseDouble(map.get("statusCode").toString()) == 0){
                    Map<String, Object> payload = Constants.gson.fromJson(Constants.gson.toJson(map.get("payload")), Map.class);
                    mainDetailProductMap = productController.convertProductDetail(payload);
                    if (mainDetailProductMap != null){
                        mainProduct = (Product) mainDetailProductMap.get("product");
                        StructureValue s = (StructureValue) mainDetailProductMap.get("category");
                        List<UnitOfMeasure> lstUnitOfMeasures = (List<UnitOfMeasure>) mainDetailProductMap.get("unitOfMeasures");
                        List<PriceDetail> lstPriceDetails = (List<PriceDetail>) mainDetailProductMap.get("priceDetails");

                        for (PriceDetail pri : lstPriceDetails){
                            Log.e(TAG, pri.toString());
                        }

                        mUnitOfMeasureController = new UnitOfMeasureController();
                        btnBack = findViewById(R.id.img_btn_back);
                        mRecyclerViewUnit = findViewById(R.id.recycle_view_unit);
                        tvName = findViewById(R.id.text_view_name);
                        image = findViewById(R.id.image_view_product);
                        tvDescription = findViewById(R.id.text_view_description);
                        recyclerViewProduct = findViewById(R.id.recycle_view_product);
                        tvViewAll = findViewById(R.id.tv_view_all);
                        relativeLayoutCart = findViewById(R.id.relative_layout_button_cart);
                        tvPrice = findViewById(R.id.text_view_price);
                        tvCart = findViewById(R.id.tv_cart);

                        relativeLayoutCart.setClickable(false);

                        tvName.setText(mainProduct.getName().trim());
                        Glide.with(this).load(mainProduct.getImageUrl()).into(image);
                        tvDescription.setText(mainProduct.getDescription().trim());

                        getListProduct();

                        mUnitAdapter = new UnitAdapter(this, lstPriceDetails, new UnitAdapter.UnitMeasureListener() {
                            @Override
                            public void onClick(UnitOfMeasure unitOfMeasure, double price) {
                                relativeLayoutCart.setClickable(true);
                                relativeLayoutCart.setBackgroundResource(R.drawable.custom_btn_addcart_hightlight);
                                tvCart.setTextColor(getResources().getColor(R.color.white));
                                Glide.with(ProductDetailActivity.this).load(unitOfMeasure.getImageUrl()).into(image);

                                mainUnit = unitOfMeasure;
                                mainPrice = price;
                                tvPrice.setText(Constants.numberFormat.format(price) + " vnđ");

                            }
                        });

                        relativeLayoutCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openAddCartDialog();
                            }
                        });

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                        mRecyclerViewUnit.setLayoutManager(linearLayoutManager);
                        mRecyclerViewUnit.setAdapter(mUnitAdapter);

                        List<UnitOfMeasure> lst = new ArrayList<>();
                        for (UnitOfMeasure unit : lstUnitOfMeasures){
                            if (unit.getBaseUnitOfMeasure().getId().trim().equals(mainBaseUnitOfMeasure.getId().trim())){
                                lst.add(unit);
                            }
                        }

                        mUnitAdapter.setData(lst);

                        btnBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onBackPressed();
                            }
                        });

                        mProductAdapter = new ProductAdapter1(this, new ProductAdapter1.ProductItemListener() {
                            @Override
                            public void onClick(Product product) {
                                Intent intent = new Intent(ProductDetailActivity.this, ProductDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("object_product", product);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }

                            @Override
                            public void onClickCart(Product product) {

                            }
                        });

                        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
                        recyclerViewProduct.setLayoutManager(linearLayoutManager1);
                        recyclerViewProduct.setAdapter(mProductAdapter);

                        mProductAdapter.setData(mProducts);

                        tvViewAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("object_category", mainProduct.getCategory());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                    }
                }
            }
        }
    }
    private void getListProduct(){
        mProducts = new ArrayList<>();
        Map<String, Object> map = productController.getByCategory(mainProduct.getCategory().getId());
        if (map != null && Double.parseDouble(map.get("statusCode").toString()) == 0){
            List<Map<String, Object>> lstMap = Constants.gson.fromJson(Constants.gson.toJson(map.get("payload")), new TypeToken<List<Map<String, Object>>>(){}.getType());
            for (Map<String, Object> m : lstMap){
                Map<String, Object> mapProduct = productController.convertProduct(m);
                Product pro = (Product) mapProduct.get("product");
                mProducts.add(pro);
            }
        }
        if (mProducts == null)
            mProducts = new ArrayList<>();
    }

    private void openAddCartDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_add_cart);

        Window window = dialog.getWindow();
        if (window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(false);

        ImageView img =dialog.findViewById(R.id.img_product);
        TextView tvName = dialog.findViewById(R.id.tv_name);
        TextView tvPrice = dialog.findViewById(R.id.tv_price);
        TextView tvUnit = dialog.findViewById(R.id.tv_unit);
        RelativeLayout btnViewCart = dialog.findViewById(R.id.relative_layout_button_cart);
        ImageButton btnBack = dialog.findViewById(R.id.img_btn_back);

        Glide.with(ProductDetailActivity.this).load(mainUnit.getImageUrl()).into(img);
        tvName.setText(mainProduct.getName());
        tvPrice.setText(Constants.numberFormat.format(mainPrice) + "đ");
        tvUnit.setText(mainUnit.getValue());

        btnViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.selectCart();
                dialog.dismiss();
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

//        rlNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        rlYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent resultIntent = new Intent();
//                setResult(Activity.RESULT_OK, resultIntent);
//                finish();
//            }
//        });

        dialog.show();
    }

}