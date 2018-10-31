package projects.karlosp3rez.androidquiz.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import projects.karlosp3rez.androidquiz.Common.Common;
import projects.karlosp3rez.androidquiz.Model.Category;
import projects.karlosp3rez.androidquiz.R;

public class DBHelper extends SQLiteAssetHelper {

    private static DBHelper instance;

    public static synchronized DBHelper getInstance (Context context) {
        if(instance == null)
            instance = new DBHelper(context);
        return  instance;
    }

    private DBHelper(Context context) {
        super(context, Common.DB_NAME, null, Common.DB_VER);
    }

    /**
     * GET ALL CATEGORY FROM DB
     */
    public List<Category> getAllCategories() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Category;",null);
        List<Category> categories = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Category category = new Category(cursor.getInt(cursor.getColumnIndex("ID")),
                        cursor.getString(cursor.getColumnIndex("Name")),
                        cursor.getString(cursor.getColumnIndex("Image")));
                categories.add(category);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return categories;
    }
}
