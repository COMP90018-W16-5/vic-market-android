package group.unimelb.vicmarket.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.baiiu.filter.adapter.MenuAdapter;
import com.baiiu.filter.adapter.SimpleTextAdapter;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.baiiu.filter.interfaces.OnFilterItemClickListener;
import com.baiiu.filter.typeview.DoubleListView;
import com.baiiu.filter.typeview.SingleGridView;
import com.baiiu.filter.typeview.SingleListView;
import com.baiiu.filter.util.CommonUtil;
import com.baiiu.filter.util.UIUtil;
import com.baiiu.filter.view.FilterCheckedTextView;
import com.baiiu.filter.view.FixedTabIndicator;

import java.util.ArrayList;
import java.util.List;

import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.CategoriesBean;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DropMenuAdapter implements MenuAdapter {
    private final Context mContext;
    private OnItemSelectedListener onItemSelectedListener;
    private String[] titles;

    public DropMenuAdapter(Context context, String[] titles) {
        this.mContext = context;
        this.titles = titles;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public int getMenuCount() {
        return titles.length;
    }

    @Override
    public String getMenuTitle(int position) {
        return titles[position];
    }

    @Override
    public int getBottomMargin(int position) {
        if (position == 3) {
            return 0;
        }

        return UIUtil.dp(mContext, 140);
    }

    @Override
    public View getView(int position, FrameLayout parentContainer) {
        return createSingleListView(position);
    }

    private View createSingleListView(int position) {
        SingleListView<DropdownMenuBean> singleListView = new SingleListView<DropdownMenuBean>(mContext)
                .adapter(new SimpleTextAdapter<DropdownMenuBean>(null, mContext) {
                    @Override
                    public String provideText(DropdownMenuBean bean) {
                        return bean.getDisplay();
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        int dp = UIUtil.dp(mContext, 15);
                        checkedTextView.setPadding(dp, dp, 0, dp);
                    }
                }).onItemClick(item -> {
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onSelected(position, item.getCode(), item.getDisplay());
                    }
                });

        List<DropdownMenuBean> list = new ArrayList<>();
        if (position == 0) {
            list.add(new DropdownMenuBean(1, "1 KM"));
            list.add(new DropdownMenuBean(5, "5 KM"));
            list.add(new DropdownMenuBean(10, "10 KM"));
            list.add(new DropdownMenuBean(20, "20 KM"));
            list.add(new DropdownMenuBean(50, "50 KM"));
            list.add(new DropdownMenuBean(100, "100 KM"));
        } else if (position == 1) {
            RetrofitHelper.getInstance().getCategories(new Observer<CategoriesBean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(CategoriesBean categoriesBean) {
                    for (CategoriesBean.DataBean dataBean : categoriesBean.getData()) {
                        list.add(new DropdownMenuBean(dataBean.getCid(), dataBean.getName()));
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });

        }
        singleListView.setList(list, -1);

        return singleListView;
    }

    public interface OnItemSelectedListener {
        void onSelected(int type, int code, String value);
    }

    public static class DropdownMenuBean {
        int code;
        String display;

        public DropdownMenuBean(int code, String display) {
            this.code = code;
            this.display = display;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }
    }
}

