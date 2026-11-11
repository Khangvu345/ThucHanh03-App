package com.example.baith3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLLite của ứng dụng.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Bang users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Bang products
    private static final String TABLE_PRODUCTS = "products";
    private static final String COL_PRODUCT_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PRICE = "price";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_CATEGORY = "category";
    private static final String COL_ACTIVE = "active";
    private static final String COL_DESCRIPTION = "description";

    // Nguong canh bao sap het hang
    public static final int LOW_STOCK_THRESHOLD = 5;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tao bang users
        String createUsers = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createUsers);

        // Tao bang products
        String createProducts = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_PRICE + " INTEGER NOT NULL, "
                + COL_QUANTITY + " INTEGER NOT NULL, "
                + COL_CATEGORY + " TEXT NOT NULL, "
                + COL_ACTIVE + " INTEGER NOT NULL, "
                + COL_DESCRIPTION + " TEXT)";
        db.execSQL(createProducts);

        // Du lieu mau: tai khoan admin / 123456
        insertUser(db, "admin", "123456");

        // Du lieu mau: 3 san pham theo de bai (active = 1)
        insertProduct(db, new Product("Chuột không dây", 150000, 20, "Điện tử", true, ""));
        insertProduct(db, new Product("Bàn phím cơ", 450000, 3, "Điện tử", true, ""));
        insertProduct(db, new Product("Sổ tay A5", 25000, 40, "Văn phòng phẩm", true, ""));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Ban 1 nen chi can xoa bang cu va tao lai khi nang phien ban
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ------------------------------------------------------------------
    // Cac ham chen du lieu Nhan SQLiteDatabase tu ben ngoai truyen vao.
    // onCreate() dung cac ham nay vi luc do khong the goi getWritableDatabase()
    // (DB dang trong qua trinh duoc tao). Cac method cong khai o duoi se
    // tu mo DB roi goi lai cac ham nay.
    // ------------------------------------------------------------------

    private long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    private long insertProduct(SQLiteDatabase db, Product p) {
        ContentValues values = productToValues(p);
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    // Chuyen mot Product thanh ContentValues de insert/update
    private ContentValues productToValues(Product p) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, p.getName());
        values.put(COL_PRICE, p.getPrice());
        values.put(COL_QUANTITY, p.getQuantity());
        values.put(COL_CATEGORY, p.getCategory());
        values.put(COL_ACTIVE, p.isActive() ? 1 : 0);
        values.put(COL_DESCRIPTION, p.getDescription() == null ? "" : p.getDescription());
        return values;
    }

    // Doc dong hien tai cua Cursor thanh mot Product
    private Product cursorToProduct(Cursor cursor) {
        Product p = new Product();
        p.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID)));
        p.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        p.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRICE)));
        p.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
        p.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
        p.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVE)) == 1);
        p.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)));
        return p;
    }

    // ------------------------------------------------------------------
    // USERS
    // ------------------------------------------------------------------

    /** Kiem tra dang nhap: true neu co dong khop username + password trong bang users. */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    /** Them tai khoan moi vao bang users. Tra ve id vua them (-1 neu trung username). */
    public long insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        return insertUser(db, username, password);
    }

    // ------------------------------------------------------------------
    // PRODUCTS
    // ------------------------------------------------------------------

    /** Them san pham, tra ve id vua them. */
    public long insertProduct(Product p) {
        SQLiteDatabase db = getWritableDatabase();
        return insertProduct(db, p);
    }

    /** Lay toan bo san pham de hien thi danh sach (sap xep theo id tang dan). */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null,
                null, null, COL_PRODUCT_ID + " ASC");
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToProduct(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /** Lay mot san pham theo id; tra ve null neu khong tim thay. */
    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null,
                COL_PRODUCT_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
        Product product = null;
        if (cursor.moveToFirst()) {
            product = cursorToProduct(cursor);
        }
        cursor.close();
        return product;
    }

    /** Cap nhat so luong ton cua mot san pham (dung cho Nhap them / Ban). */
    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY, quantity);
        return db.update(TABLE_PRODUCTS, values,
                COL_PRODUCT_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    /** Xoa san pham theo id. */
    public int deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PRODUCTS,
                COL_PRODUCT_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    /** Tong gia tri kho: SUM(price * quantity). Dung long vi tong tien co the rat lon. */
    public long getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_PRICE + " * " + COL_QUANTITY + ") FROM " + TABLE_PRODUCTS,
                null);
        long total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    /** So san pham sap het hang: quantity < 5. */
    public int countLowStock() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COL_QUANTITY + " < ?",
                new String[]{String.valueOf(LOW_STOCK_THRESHOLD)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}
