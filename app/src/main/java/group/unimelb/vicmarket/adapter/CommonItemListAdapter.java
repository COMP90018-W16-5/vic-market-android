package group.unimelb.vicmarket.adapter;

import android.content.Context;
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

public class CommonItemListAdapter extends RecyclerView.Adapter<CommonItemListAdapter.ViewHolder> {
    List<MainItemListBean.DataBean> data = new ArrayList<>();
    private Context context;
    private OnListItemClickListener onListItemClickListener;

    public CommonItemListAdapter(Context context) {
        this.context = context;
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
        View view = inflater.inflate(R.layout.item_post_common, parent, false);
        return new ViewHolder(view);
    }

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

        if (onListItemClickListener != null) {
            holder.holderLayout.setOnClickListener(v -> onListItemClickListener.onListItemClick(position));
        }
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePicture = itemView.findViewById(R.id.common_item_picture);
            textTitle = itemView.findViewById(R.id.common_item_title);
            textPrice = itemView.findViewById(R.id.common_item_price);
            holderLayout = itemView.findViewById(R.id.common_item_holder);
        }
    }
}
