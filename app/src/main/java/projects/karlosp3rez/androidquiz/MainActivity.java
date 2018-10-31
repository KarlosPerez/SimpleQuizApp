package projects.karlosp3rez.androidquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import projects.karlosp3rez.androidquiz.Adapter.CategoryAdapter;
import projects.karlosp3rez.androidquiz.Common.SpaceDecoration;
import projects.karlosp3rez.androidquiz.DBHelper.DBHelper;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView category_recycler;
    final int spaceInPixel = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        category_recycler = (RecyclerView) findViewById(R.id.recycler_category);
        category_recycler.setHasFixedSize(true);
        category_recycler.setLayoutManager(new GridLayoutManager(this, 2));

        category_recycler.addItemDecoration(new SpaceDecoration(spaceInPixel));

        loadCategories();
    }

    private void loadCategories() {
        CategoryAdapter adapter = new CategoryAdapter(MainActivity.this, DBHelper.getInstance(this).getAllCategories());
        category_recycler.setAdapter(adapter);
    }
}
