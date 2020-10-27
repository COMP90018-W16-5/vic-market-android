package group.unimelb.vicmarket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.activity.CategoryDetailActivity;
import group.unimelb.vicmarket.retrofit.bean.CategoriesBean;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    private List<CategoriesBean.DataBean> data = new ArrayList<>();
    private Context context;

    public CategoryListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<CategoriesBean.DataBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoriesBean.DataBean dataBean = data.get(position);

        holder.textName.setText(dataBean.getName());
        holder.relativeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryDetailActivity.class);
            intent.putExtra("category_id", dataBean.getCid());
            intent.putExtra("category_name", dataBean.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textName;
        private RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.item_category_name);
            relativeLayout = itemView.findViewById(R.id.item_category_holder);
        }
    }
}
