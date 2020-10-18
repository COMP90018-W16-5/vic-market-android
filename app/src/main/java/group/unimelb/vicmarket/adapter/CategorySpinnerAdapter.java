package group.unimelb.vicmarket.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import group.unimelb.vicmarket.retrofit.bean.CategoriesBean;

public class CategorySpinnerAdapter extends ArrayAdapter<CategoriesBean.DataBean> {
    private List<CategoriesBean.DataBean> data = new ArrayList<>();

    public CategorySpinnerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public CategorySpinnerAdapter(@NonNull Context context, int resource, @NonNull List<CategoriesBean.DataBean> objects) {
        super(context, resource);
        this.data = objects;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Nullable
    @Override
    public CategoriesBean.DataBean getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setText(data.get(position).getName());
        return label;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setText(data.get(position).getName());
        return label;
    }
}
