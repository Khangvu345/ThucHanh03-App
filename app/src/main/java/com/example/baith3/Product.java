package com.example.baith3;

public class Product {

    private int id;
    private String name;
    private int price;       // Gia ban (VND), > 0
    private int quantity;    // So luong ton kho, >= 0
    private String category; // Loai san pham
    private boolean active;  // true = dang kinh doanh, false = ngung kinh doanh
    private String description;

    // Constructor khong tham so
    public Product() {
    }

    // Constructor khi tao san pham moi (chua co id, DB se tu sinh id)
    public Product(String name, int price, int quantity, String category,
                   boolean active, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    // Constructor day du (dung khi doc mot dong tu DB len)
    public Product(int id, String name, int price, int quantity, String category,
                   boolean active, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
