package org.trananh.shoppingapp.main.cart;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.trananh.shoppingapp.R;
import org.trananh.shoppingapp.main.category.product.ProductAdapter;
import org.trananh.shoppingapp.model.Cart;
import org.trananh.shoppingapp.model.Product;
import org.trananh.shoppingapp.util.Constants;

import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public static final String TAG = CartAdapter.class.getName();
    private Context mContext;
    private List<Map<String, Object>> maps;
    private CartListener mCartListener;
    public interface CartListener{
        void onClickAdd(int id);
        void onClickSub(int id);
        void onClickImage(Map<String, Object> map);
    }

    public CartAdapter(Context context, CartListener listener){
        this.mContext = context;
        this.mCartListener = listener;
    }

    public void setData(List<Map<String, Object>> maps){
        this.maps = maps;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Map<String, Object> map = maps.get(position);
        if (map == null)
            return;

        Cart cart = (Cart)map.get("cart");
        Double price = (Double) map.get("price");
        Product product = (Product) map.get("product");

        Glide.with(mContext).load(cart.getUnitOfMeasure().getImageUrl()).into(holder.imgProduct);
        holder.tvName.setText(product.getName().trim());
        holder.tvUnit.setText("Đơn vị: " + cart.getUnitOfMeasure().getValue());
        holder.tvQuantity.setText(String.valueOf(cart.getQuantity()));
        Double totalPrice = price * cart.getQuantity();
        holder.tvPrice.setText(Constants.numberFormat.format(totalPrice) + "đ");

        holder.imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCartListener.onClickImage(map);
            }
        });

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCartListener.onClickAdd(cart.getId());
            }
        });

        holder.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cart.getQuantity() <= 1){
                    openDeleteCartDialog(cart.getId());
                }else{
                    mCartListener.onClickSub(cart.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (maps!=null)
            return maps.size();
        return 0;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgProduct;
        private TextView tvName, tvPrice, tvUnit, tvQuantity;
        private TextView btnSub, btnAdd;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_view);
            tvName = itemView.findViewById(R.id.tv_name);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnSub = itemView.findViewById(R.id.tv_sub);
            btnAdd = itemView.findViewById(R.id.tv_add);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }
    }

    private void openDeleteCartDialog(int id){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_card);

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
                mCartListener.onClickSub(id);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
