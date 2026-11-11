package com.example.baith3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.CompoundButton;

/**
 * Màn hình 3: Thêm sản phẩm mới vào ko.
 */
public class AddProductActivity extends AppCompatActivity {

    private EditText edtName, edtPrice, edtQuantity, edtDescription;
    private Spinner spinnerCategory;
    // Layout dung the <Switch> nhung voi theme AppCompat, Android se nap
    // thanh SwitchCompat nen bien phai khai bao dung kieu nay
    private CompoundButton switchActive;
    private Button btnSaveProduct;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        switchActive = findViewById(R.id.switchActive);
        edtDescription = findViewById(R.id.edtDescription);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);

        // spinnerCategory da khai bao android:entries="@array/product_categories"
        // ngay trong XML nen khong can set adapter bang code

        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceText = edtPrice.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        // --- Kiem tra ten ---
        if (name.isEmpty()) {
            edtName.setError("Tên sản phẩm không được để trống");
            edtName.requestFocus();
            return;
        }

        // --- Kiem tra gia: bat buoc nhap, phai la so va > 0 ---
        if (priceText.isEmpty()) {
            edtPrice.setError("Giá bán không được để trống");
            edtPrice.requestFocus();
            return;
        }
        int price;
        try {
            price = Integer.parseInt(priceText);
        } catch (NumberFormatException e) {
            edtPrice.setError("Giá bán không hợp lệ");
            edtPrice.requestFocus();
            return;
        }
        if (price <= 0) {
            edtPrice.setError("Giá bán phải lớn hơn 0");
            edtPrice.requestFocus();
            return;
        }

        // --- Kiem tra so luong: bat buoc nhap, phai la so nguyen >= 0 ---
        if (quantityText.isEmpty()) {
            edtQuantity.setError("Số lượng không được để trống");
            edtQuantity.requestFocus();
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            edtQuantity.setError("Số lượng không hợp lệ");
            edtQuantity.requestFocus();
            return;
        }
        if (quantity < 0) {
            edtQuantity.setError("Số lượng không được âm");
            edtQuantity.requestFocus();
            return;
        }

        String category = spinnerCategory.getSelectedItem().toString();
        boolean active = switchActive.isChecked();

        Product product = new Product(name, price, quantity, category, active, description);
        long newId = dbHelper.insertProduct(product);

        if (newId > 0) {
            Toast.makeText(this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
            // Ve man danh sach; MainActivity.onResume() se tu tai lai du lieu
            finish();
        } else {
            Toast.makeText(this, "Lưu sản phẩm thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
