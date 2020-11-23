package com.just.justbikev2.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import androidx.core.database.CursorWindowCompat;

import com.just.justbikev2.Model.Article;
import com.just.justbikev2.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "OrderDetails.db";
    private static final int DB_VERSION = 1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public List<Order> getCart(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductId" , "ProductName" ,"Quantity" , "Price" , "Discount","Image"};
        String sqlTableName = "OrderDetails";

        sqLiteQueryBuilder.setTables(sqlTableName);

        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase,sqlSelect,null,null,null,null,null,null);


        final List<Order> result =new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                result.add(new Order(cursor.getString(cursor.getColumnIndex(sqlSelect[0])),
                        cursor.getString(cursor.getColumnIndex(sqlSelect[1])),
                        cursor.getInt(cursor.getColumnIndex(sqlSelect[2])),
                        cursor.getString(cursor.getColumnIndex(sqlSelect[3])),
                        cursor.getString(cursor.getColumnIndex(sqlSelect[4])),
                        cursor.getString(cursor.getColumnIndex(sqlSelect[5]))
                        ));
            }while(cursor.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetails (ProductId , ProductName , Quantity, Price , Discount , Image ) VALUES ('%s','%s',%d,'%s','%s','%s');",
                order.getProductId() , order.getProductName() , order.getQuantity() , order.getPrice() , order.getDiscount(),order.getImage());
        db.execSQL(query);
    }
  public void removeFromCart(String productId){
    SQLiteDatabase db = getReadableDatabase();
    String query = String.format("DELETE FROM OrderDetails WHERE ProductId='%s';",productId);
    db.execSQL(query);
  }

    public void cleanCart(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetails;");
        db.execSQL(query);
    }

    public void addToFavorites(String vehicleId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites (vehicleId) VALUES('%s');",vehicleId);
        db.execSQL(query);
    }
    public void removeFromFavorites(String vehicleId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE vehicleId='%s';",vehicleId);
        db.execSQL(query);
    }
    public boolean isFavorite(String vehicleId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE vehicleId='%s';",vehicleId);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount()<=0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public void addToArticleFavorites(String articleId,String articleTitle,String articleDesc,String articleImage){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO ArticleFavorites (articleId , articleTitle , articleDesc , articleImage) VALUES('%s' , '%s' , '%s' , '%s' );",articleId,articleTitle,articleDesc,articleImage);
        db.execSQL(query);
    }
    public void removeFromoArticleFavorites(String articleId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM ArticleFavorites WHERE articleId='%s';",articleId);
        db.execSQL(query);
    }
    public boolean isArticleFavorite(String articleId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM ArticleFavorites WHERE articleId='%s';",articleId);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount()<=0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<Article> getFavoriteArticles(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"articleId" , "articleTitle" ,"articleDesc" , "articleImage"};
        String sqlTableName = "ArticleFavorites";

        sqLiteQueryBuilder.setTables(sqlTableName);

        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase,sqlSelect,null,null,null,null,null,null);

        final List<Article> result =new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                result.add(new Article(cursor.getString(cursor.getColumnIndex(sqlSelect[0])),
                        cursor.getString(cursor.getColumnIndex(sqlSelect[1])),
                        cursor.getString(cursor.getColumnIndex(sqlSelect[2])),
                        cursor.getString(cursor.getColumnIndex(sqlSelect[3]))
                ));
            }while(cursor.moveToNext());
        }
        return result;
    }

    public int getCartCount() {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetails;");
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do{
                count = cursor.getInt(0);
            }while (cursor.moveToNext());

        }
        return count;
    }
}
