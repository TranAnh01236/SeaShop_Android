package org.trananh.shoppingapp.main.category.product;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;

import org.trananh.shoppingapp.R;
import org.trananh.shoppingapp.controller.PriceDetailController;
import org.trananh.shoppingapp.controller.UnitOfMeasureController;
import org.trananh.shoppingapp.model.BaseUnitOfMeasure;
import org.trananh.shoppingapp.model.PriceDetail;
import org.trananh.shoppingapp.model.Product;
import org.trananh.shoppingapp.model.UnitOfMeasure;
import org.trananh.shoppingapp.util.Constants;
import org.trananh.shoppingapp.util.MyHttpResponseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    public static final String TAG = ProductAdapter.class.getName();
    private Context mContext;
    private ProductItemListener mProductItemListener;
    private List<Map<String,Object>> lst;
    public interface ProductItemListener{
        void onClick(Map<String, Object> map);
        void onClickCart(Map<String, Object> map);
    }
    public ProductAdapter(Context context, ProductItemListener listener){
        this.mContext = context;
        this.mProductItemListener = listener;
    }

    public void setData(List<Map<String, Object>> lst){
        this.lst = lst;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Map<String, Object> map = lst.get(position);
        if (map == null)
            return;

        Product product = (Product) map.get("product");
        BaseUnitOfMeasure baseUnitOfMeasure = (BaseUnitOfMeasure) map.get("baseUnitOfMeasure");

        if (product == null || baseUnitOfMeasure == null)
            return;
        Glide.with(mContext).load(product.getImageUrl()).into(holder.imgProduct);
        holder.tvName.setText(product.getName().trim());
        holder.tvUnit.setText("Đơn vị:");
        holder.tvUnit.setText("Đơn vị: " + baseUnitOfMeasure.getValue());
        holder.tvPrice.setText(Constants.numberFormat.format(Double.parseDouble(map.get("price").toString())) + "đ");
        holder.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductItemListener.onClickCart(map);
            }
        });
        holder.layout_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductItemListener.onClick(map);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (lst!=null)
            return lst.size();
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgProduct;
        private TextView tvName, tvPrice, tvUnit;
        private LinearLayout btnCart;
        private RelativeLayout layout_product;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvUnit = itemView.findViewById(R.id.tv_unit_product);
            tvPrice = itemView.findViewById(R.id.tv_price_product);
            btnCart = itemView.findViewById(R.id.linear_layout_cart);
            layout_product = itemView.findViewById(R.id.relative_layout_product);
        }
    }
}
