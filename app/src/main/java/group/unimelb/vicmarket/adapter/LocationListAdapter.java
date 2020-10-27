package group.unimelb.vicmarket.adapter;

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

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {
    private List<String> data = new ArrayList<>();
    private OnItemClickedListener onItemClickedListener;

    public LocationListAdapter(List<String> data) {
        this.data = data;
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
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
        holder.textName.setText(data.get(position));
        holder.relativeLayout.setOnClickListener(v -> {
            if (onItemClickedListener != null) {
                onItemClickedListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickedListener {
        void onClick(int position);
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
