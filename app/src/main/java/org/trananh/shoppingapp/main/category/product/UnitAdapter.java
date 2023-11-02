package org.trananh.shoppingapp.main.category.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;

import org.trananh.shoppingapp.R;
import org.trananh.shoppingapp.controller.PriceDetailController;
import org.trananh.shoppingapp.controller.UnitOfMeasureController;
import org.trananh.shoppingapp.main.category.CategoryAdapter1;
import org.trananh.shoppingapp.model.PriceDetail;
import org.trananh.shoppingapp.model.UnitOfMeasure;
import org.trananh.shoppingapp.util.Constants;
import org.trananh.shoppingapp.util.MyHttpResponseArray;

import java.util.ArrayList;
import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.UnitMeasureViewHolder>{

    private Context mContext;
    private List<UnitOfMeasure> mUnitOfMeasures;
    private List<PriceDetail> mPriceDetails;
    private int single_selection_position = -1;

    private UnitMeasureListener mUnitMeasureListener;
    public interface UnitMeasureListener{
        void onClick(UnitOfMeasure unitOfMeasure, double price);
    }

    public UnitAdapter(Context context, List<PriceDetail> priceDetails, UnitMeasureListener listener){
        this.mContext = context;
        this.mUnitMeasureListener = listener;
        this.mPriceDetails = priceDetails;
    }

    public void setData(List<UnitOfMeasure> list){
        this.mUnitOfMeasures = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UnitMeasureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unit_measure, parent, false);
        return new UnitMeasureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitMeasureViewHolder holder, int position) {
        UnitOfMeasure unit = mUnitOfMeasures.get(position);
        if (unit == null)
            return;
        holder.tvName.setText(unit.getValue().trim());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double price = 0;
                for (PriceDetail p : mPriceDetails){
                    if (unit.getId() == p.getUnitOfMeasure().getId()){
                        price = p.getPrice();
                    }
                }
                mUnitMeasureListener.onClick(unit, price);
                setSingleSelection(holder.getAdapterPosition());
            }
        });

        if (single_selection_position == position){
            holder.relativeLayout.setBackgroundResource(R.drawable.custom_unit_item_hightlight);
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.white));
        }else{
            holder.relativeLayout.setBackgroundResource(R.drawable.custom_unit_item);
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
        }

    }

    @Override
    public int getItemCount() {
        if (mUnitOfMeasures!=null)
            return mUnitOfMeasures.size();
        return 0;
    }

    public class UnitMeasureViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private RelativeLayout relativeLayout;


        public UnitMeasureViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.text_view_unit);
            relativeLayout = itemView.findViewById(R.id.relative_layout_unit);
        }
    }
    public void setSingleSelection(int adapterPosition){
        if (adapterPosition != RecyclerView.NO_POSITION){
            notifyItemChanged(single_selection_position);
            single_selection_position = adapterPosition;
            notifyItemChanged(single_selection_position);
        }
    }
}
