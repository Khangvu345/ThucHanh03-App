package com.example.baith3;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter do du lieu san pham len ListView cua MainActivity.
 * Moi dong dung layout item_product.xml.
 *
 * Diem phai chu y: ListView TAI SU DUNG View cua cac dong da cuon qua
 * (convertView != null). Vi vay khi bind du lieu phai dat LAI ca hai
 * trang thai: san pham sap het thi HIEN nhan canh bao + doi so luong
 * sang mau do; san pham binh thuong thi AN nhan + tra so luong ve mau
 * mac dinh. Thieu nhanh "else" thi nhan/mau se bi "dinh" sang dong khac.
 */
public class ProductAdapter extends BaseAdapter {

    private static final int COLOR_NORMAL = Color.parseColor("#381B25");
    private static final int COLOR_LOW_STOCK = Color.parseColor("#BC1231");

    private final LayoutInflater inflater;
    private List<Product> products = new ArrayList<>();

    public ProductAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    /** Cap nhat lai toan bo danh sach va ve lai ListView. */
    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.tvRowName = convertView.findViewById(R.id.tvRowName);
            holder.tvRowCategory = convertView.findViewById(R.id.tvRowCategory);
            holder.tvRowPrice = convertView.findViewById(R.id.tvRowPrice);
            holder.tvRowQuantity = convertView.findViewById(R.id.tvRowQuantity);
            holder.tvRowLowStock = convertView.findViewById(R.id.tvRowLowStock);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = getItem(position);
        holder.tvRowName.setText(product.getName());
        holder.tvRowCategory.setText(product.getCategory());
        holder.tvRowPrice.setText(MoneyFormatter.formatVnd(product.getPrice()));
        holder.tvRowQuantity.setText("Tồn: " + product.getQuantity());

        if (product.getQuantity() < DatabaseHelper.LOW_STOCK_THRESHOLD) {
            holder.tvRowLowStock.setVisibility(View.VISIBLE);
            holder.tvRowQuantity.setTextColor(COLOR_LOW_STOCK);
        } else {
            holder.tvRowLowStock.setVisibility(View.GONE);
            holder.tvRowQuantity.setTextColor(COLOR_NORMAL);
        }

        return convertView;
    }

    // Giu tham chieu cac View cua mot dong de khong phai findViewById lai moi lan cuon
    private static class ViewHolder {
        TextView tvRowName, tvRowCategory, tvRowPrice, tvRowQuantity, tvRowLowStock;
    }
}
