package com.example.baith3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * Màn hình 2: Danh sách sản phẩm & thống kê kho
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvProductCount, tvTotalValue, tvLowStockCount, tvEmpty;
    private ListView listProducts;
    private Button btnAddProduct;

    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        tvProductCount = findViewById(R.id.tvProductCount);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        tvEmpty = findViewById(R.id.tvEmpty);
        listProducts = findViewById(R.id.listProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        adapter = new ProductAdapter(this);
        listProducts.setAdapter(adapter);
        // Khi danh sach rong thi hien dong "Chua co san pham nao"
        listProducts.setEmptyView(tvEmpty);

        // Nhan vao mot dong -> mo chi tiet, chi truyen id qua Intent
        listProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product product = adapter.getItem(position);
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
            startActivity(intent);
        });

        btnAddProduct.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddProductActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Product> products = dbHelper.getAllProducts();
        adapter.setProducts(products);

        tvProductCount.setText(String.valueOf(products.size()));
        tvTotalValue.setText(MoneyFormatter.formatVnd(dbHelper.getTotalValue()));
        tvLowStockCount.setText(String.valueOf(dbHelper.countLowStock()));
    }
}
