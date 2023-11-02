package org.trananh.shoppingapp.main.cart;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.google.gson.reflect.TypeToken;

import org.trananh.shoppingapp.R;
import org.trananh.shoppingapp.controller.CartController;
import org.trananh.shoppingapp.controller.CategoryController;
import org.trananh.shoppingapp.main.MainActivity;
import org.trananh.shoppingapp.main.SearchActivity;
import org.trananh.shoppingapp.model.Cart;
import org.trananh.shoppingapp.model.StructureValue;
import org.trananh.shoppingapp.model.User;
import org.trananh.shoppingapp.util.Constants;
import org.trananh.shoppingapp.util.MyHttpResponseArray;
import org.trananh.shoppingapp.util.data_local.DataLocalManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CartFragment extends Fragment {
    public static final String TAG = CartFragment.class.getName();
    private View rootView;

    private MainActivity mMainActivity;
    private RelativeLayout mRelativeLayoutSearch, relativeLayoutShopping, relativeLayoutDeleteAll;
    private CategoryController mCategoryController;
    private RecyclerView mRecyclerViewCategory, mRecycleViewCard;
    private CartCategoryAdapter mCartCategoryAdapter;
    private RelativeLayout rl_first, rl_second;
    private List<StructureValue> mCategoriesList;
    private TextView tvViewAll, tvTotalPrice;
    private RelativeLayout btnCategory;
    private CartController mCartController;
    private List<Map<String, Object>> mMaps;
    private CartAdapter mCartAdapter;
    private double totalPrice;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        mMainActivity = (MainActivity) getActivity();
        initialize();

        return rootView;
    }

    private void initialize(){

        mRelativeLayoutSearch = rootView.findViewById(R.id.relativeLayout_search);
        tvViewAll = rootView.findViewById(R.id.tv_view_all);
        rl_first = rootView.findViewById(R.id.relative_layout_first);
        rl_second = rootView.findViewById(R.id.relative_layout_second);
        mCartController = new CartController();
        mRecycleViewCard = rootView.findViewById(R.id.recycle_view_product);
        relativeLayoutShopping = rootView.findViewById(R.id.relative_layout_product);
        relativeLayoutDeleteAll = rootView.findViewById(R.id.relative_layout_delete_all_cart);
        tvTotalPrice = rootView.findViewById(R.id.tv_total_price);

        mMaps = new ArrayList<>();

        mRelativeLayoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mMainActivity, SearchActivity.class);
                startActivity(intent);
            }
        });

        mCategoryController = new CategoryController();
        mRecyclerViewCategory = rootView.findViewById(R.id.recycle_view_category);
        btnCategory = rootView.findViewById(R.id.relative_layout_category);

        getListCategories();
        loadCartList();

        mCartAdapter = new CartAdapter(mMainActivity, new CartAdapter.CartListener() {

            @Override
            public void onClickAdd(int id) {
                addCart(id);
            }

            @Override
            public void onClickSub(int id) {
                subCart(id);
            }

            @Override
            public void onClickImage(Map<String, Object> map) {

            }
        });

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mMainActivity, RecyclerView.VERTICAL, false);
        mRecycleViewCard.setLayoutManager(linearLayoutManager1);

        mRecycleViewCard.setAdapter(mCartAdapter);
        mCartAdapter.setData(mMaps);

        mCartCategoryAdapter = new CartCategoryAdapter(mMainActivity, new CartCategoryAdapter.CategoryItemListener() {
            @Override
            public void onClick(StructureValue category) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);
        mRecyclerViewCategory.setLayoutManager(linearLayoutManager);

        mRecyclerViewCategory.setAdapter(mCartCategoryAdapter);
        mCartCategoryAdapter.setData(mCategoriesList);

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.chooseCategoryFragment();
            }
        });

        relativeLayoutShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.chooseCategoryFragment();
            }
        });

        relativeLayoutDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDeleteCartDialog();
            }
        });

        tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.selectProduct();
            }
        });

        calculateTotalPrice();

    }

    private void getListCategories(){
        mCategoriesList = new ArrayList<>();
        Map<String, Object> map = mCategoryController.getByLevel(1);
        if (map != null && Double.parseDouble(map.get("statusCode").toString()) == 0){
            List<Map<String, Object>> lstMap = Constants.gson.fromJson(Constants.gson.toJson(map.get("payload")), new TypeToken<List<Map<String, Object>>>(){}.getType());
            if (lstMap != null){
                for (Map<String, Object> m : lstMap){
                    mCategoriesList.add(mCategoryController.convertStructureValue(m));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        loadCartList();

        if (mMaps != null && mMaps.size() > 0){
            rl_first.setVisibility(View.GONE);
            rl_second.setVisibility(View.VISIBLE);

        }else {
            rl_first.setVisibility(View.VISIBLE);
            rl_second.setVisibility(View.GONE);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "pause");
    }

    private void loadCartList(){
        Map<String, Object> map = mCartController.getAll();
        mMaps = new ArrayList<>();
        if (map != null && Double.parseDouble(map.get("statusCode").toString()) == 0){
            List<Map<String, Object>> lstMap = Constants.gson.fromJson(Constants.gson.toJson(map.get("payload")), new TypeToken<List<Map<String, Object>>>(){}.getType());
            if (lstMap != null){
                for (Map<String, Object> m : lstMap){
                    mMaps.add(mCartController.convertCart(m));
                }
            }
        }
    }

    private void addCart(int id){
        Map<String, Object> map = mCartController.addCart(id);
        mMaps = new ArrayList<>();
        if (map != null && Double.parseDouble(map.get("statusCode").toString()) == 0){
            List<Map<String, Object>> lstMap = Constants.gson.fromJson(Constants.gson.toJson(map.get("payload")), new TypeToken<List<Map<String, Object>>>(){}.getType());
            if (lstMap != null){
                for (Map<String, Object> m : lstMap){
                    mMaps.add(mCartController.convertCart(m));
                }
            }
        }
        mCartAdapter.setData(mMaps);
        calculateTotalPrice();
    }

    private void subCart(int id){
        Map<String, Object> map = mCartController.subCart(id);
        mMaps = new ArrayList<>();
        if (map != null && Double.parseDouble(map.get("statusCode").toString()) == 0){
            List<Map<String, Object>> lstMap = Constants.gson.fromJson(Constants.gson.toJson(map.get("payload")), new TypeToken<List<Map<String, Object>>>(){}.getType());
            if (lstMap != null){
                for (Map<String, Object> m : lstMap){
                    mMaps.add(mCartController.convertCart(m));
                }
            }
        }
        mCartAdapter.setData(mMaps);
        calculateTotalPrice();
    }

    private void calculateTotalPrice(){
        totalPrice = 0;

        for (Map<String, Object> map : mMaps){
            Cart cart = (Cart) map.get("cart");
            int quantity = cart.getQuantity();
            double price = Double.parseDouble(map.get("price").toString());
            double total = quantity * price;
            totalPrice += total;
        }
        tvTotalPrice.setText(Constants.numberFormat.format(totalPrice) + "đ");
    }

    private void openDeleteCartDialog(){
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_all_card);

        Window window = dialog.getWindow();
        if (window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(false);

        RelativeLayout rlYes = dialog.findViewById(R.id.relative_layout_yes);
        RelativeLayout rlNo = dialog.findViewById(R.id.relative_layout_no);

        rlNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        rlYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent resultIntent = new Intent();
//                setResult(Activity.RESULT_OK, resultIntent);
//                finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}