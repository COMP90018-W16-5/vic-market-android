package group.unimelb.vicmarket.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.bean.MainItemListBean;
import group.unimelb.vicmarket.util.LocationUtil;

public class MainItemListAdapter extends RecyclerView.Adapter<MainItemListAdapter.ViewHolder> {
    List<MainItemListBean.DataBean> data = new ArrayList<>();
    private final Context context;
    private OnListItemClickListener onListItemClickListener;

    private final double longitude;
    private final double latitude;

    public MainItemListAdapter(Context context, double longitude, double latitude) {
        this.context = context;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }

    public void setData(List<MainItemListBean.DataBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_post_main, parent, false);


        ViewHolder myViewHolder = new ViewHolder(view);
        //view.setOnClickListener(this);


        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainItemListBean.DataBean dataBean = data.get(position);
        if (dataBean.getUrls() != null && !dataBean.getUrls().isEmpty()) {
            Glide.with(context)
                    .load(dataBean.getUrls().get(0).getUrl())
                    .into(holder.imagePicture);
        }
        holder.textTitle.setText(dataBean.getTitle());
        holder.textPrice.setText("$" + dataBean.getPrice());
        double distance = LocationUtil.getInstance().getDistance(dataBean.getLongitude(),
                dataBean.getLatitude(), longitude, latitude);
        String distanceDisplay;
        if (distance < 1) {
            distance *= 1000;
            distanceDisplay = String.format("%sm", String.format("%.2f", distance));
        } else {
            distanceDisplay = String.format("%skm", String.format("%.2f", distance));
        }
        holder.textDistance.setText( distanceDisplay);

        if (onListItemClickListener != null) {
            holder.holderLayout.setOnClickListener(v -> onListItemClickListener.onListItemClick(position));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListItemClickListener != null) {
                    onListItemClickListener.onListItemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnListItemClickListener {
        void onListItemClick(int index);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePicture;
        private TextView textTitle;
        private TextView textPrice;
        private LinearLayout holderLayout;
        private TextView textDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePicture = itemView.findViewById(R.id.main_item_picture);
            textTitle = itemView.findViewById(R.id.main_item_title);
            textPrice = itemView.findViewById(R.id.main_item_price);
            holderLayout = itemView.findViewById(R.id.main_item_holder);
            textDistance = itemView.findViewById(R.id.main_item_distance);
        }
    }
}
