package com.example.baith3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Màn hình 4: Chi tiết sản phẩm.
 */
public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";

    private TextView tvDetailName, tvDetailCategory, tvDetailActive, tvDetailPrice,
            tvDetailQuantity, tvDetailStockValue, tvDetailLowStock, tvDetailDescription;
    private Button btnRestock, btnSell, btnBack, btnDelete;

    private DatabaseHelper dbHelper;
    private int productId = -1;
    private Product product; // San pham dang hien thi (doc tu DB)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailActive = findViewById(R.id.tvDetailActive);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvDetailStockValue = findViewById(R.id.tvDetailStockValue);
        tvDetailLowStock = findViewById(R.id.tvDetailLowStock);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        btnRestock = findViewById(R.id.btnRestock);
        btnSell = findViewById(R.id.btnSell);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnRestock.setOnClickListener(v -> {
            int newQuantity = product.getQuantity() + 1;
            dbHelper.updateQuantity(productId, newQuantity);
            loadProduct();
        });

        btnSell.setOnClickListener(v -> {
            if (product.getQuantity() == 0) {
                Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            int newQuantity = product.getQuantity() - 1;
            dbHelper.updateQuantity(productId, newQuantity);
            loadProduct();
        });

        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteProduct(productId);
            Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
            // Ve man danh sach; onResume() cua MainActivity se cap nhat lai
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (productId != -1) {
            loadProduct();
        }
    }

    private void loadProduct() {
        product = dbHelper.getProductById(productId);
        if (product == null) {
            // San pham da bi xoa o noi khac
            Toast.makeText(this, "Sản phẩm không còn tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailName.setText(product.getName());
        tvDetailCategory.setText(product.getCategory());
        tvDetailActive.setText(product.isActive() ? "Đang kinh doanh" : "Ngừng kinh doanh");
        tvDetailPrice.setText(MoneyFormatter.formatVnd(product.getPrice()));
        tvDetailQuantity.setText(String.valueOf(product.getQuantity()));

        // Gia tri ton kho cua rieng san pham nay: gia x so luong (dung long)
        long stockValue = (long) product.getPrice() * product.getQuantity();
        tvDetailStockValue.setText(MoneyFormatter.formatVnd(stockValue));

        tvDetailLowStock.setVisibility(
                product.getQuantity() < DatabaseHelper.LOW_STOCK_THRESHOLD
                        ? View.VISIBLE : View.GONE);

        String description = product.getDescription();
        tvDetailDescription.setText(
                description == null || description.trim().isEmpty()
                        ? "Chưa có mô tả" : description);
    }
}
