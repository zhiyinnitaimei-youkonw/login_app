package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

/**
 * 商城Tab — 商品网格展示
 */
public class ShopFragment extends Fragment {

    private GridView gridView;
    private ProductAdapter adapter;
    private List<Product> products = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        gridView = view.findViewById(R.id.grid_products);
        initProducts();
        adapter = new ProductAdapter(requireContext(), products);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, v, position, id) -> {
            Product p = products.get(position);
            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
            intent.putExtra("product", p);
            startActivity(intent);
        });

        return view;
    }

    private void initProducts() {
        products.add(new Product(1, "无线蓝牙耳机", "降噪长续航 高品质音质", 199.00, R.drawable.ic_product));
        products.add(new Product(2, "运动跑鞋", "透气减震 轻便舒适", 299.00, R.drawable.ic_product));
        products.add(new Product(3, "双肩背包", "大容量 防水耐磨", 159.00, R.drawable.ic_product));
        products.add(new Product(4, "保温杯", "316不锈钢 500ml", 89.00, R.drawable.ic_product));
        products.add(new Product(5, "机械键盘", "青轴 RGB背光 87键", 349.00, R.drawable.ic_product));
        products.add(new Product(6, "充电宝", "20000mAh 快充", 129.00, R.drawable.ic_product));
        products.add(new Product(7, "遮阳帽", "防晒透气 可折叠", 49.00, R.drawable.ic_product));
        products.add(new Product(8, "T恤", "纯棉 宽松版型 多色可选", 79.00, R.drawable.ic_product));
    }
}
