package projects.karlosp3rez.androidquiz.Activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.util.concurrent.TimeUnit;

import projects.karlosp3rez.androidquiz.Adapter.AnswerSheetAdapter;
import projects.karlosp3rez.androidquiz.Adapter.QuestionFragmentAdapter;
import projects.karlosp3rez.androidquiz.Common.Common;
import projects.karlosp3rez.androidquiz.DBHelper.DBHelper;
import projects.karlosp3rez.androidquiz.Model.CurrentQuestion;
import projects.karlosp3rez.androidquiz.Fragments.QuestionFragment;
import projects.karlosp3rez.androidquiz.R;

public class QuestionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int time_play = Common.TOTAL_TIME;
    boolean isAnswerModeView = false;

    AnswerSheetAdapter answerSheetAdapter;
    TextView txt_right_answer, txt_timer;
    RecyclerView answer_sheet_view_recycler;

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Common.selectedCategory.getName());
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Get questions from DB
        takeQuestion();

        if(Common.questionList.size() > 0) {
            //Show TextView right answer and text view timer
            txt_right_answer = (TextView) findViewById(R.id.txt_question_right);
            txt_timer = (TextView) findViewById(R.id.txt_timer);

            txt_timer.setVisibility(View.VISIBLE);
            txt_right_answer.setVisibility(View.VISIBLE);

            txt_right_answer.setText(new StringBuilder(String.format("%d/%d",Common.right_answer_count,Common.questionList.size())));
            //Set count down timer
            setTimer();

            answer_sheet_view_recycler = (RecyclerView) findViewById(R.id.grid_answer_recycler);
            answer_sheet_view_recycler.setHasFixedSize(true);
            if (Common.questionList.size() > 5) { //if question List have size > 5, we will separate 2 rows
                answer_sheet_view_recycler.setLayoutManager(new GridLayoutManager(this, Common.questionList.size() / 2));
            }
            //Load Answer Sheet Panel
            loadAnswerSheet();

            viewPager = (ViewPager) findViewById(R.id.viewPager);
            tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

            genFragmentList();

            QuestionFragmentAdapter questionFragmentAdapter = new QuestionFragmentAdapter(getSupportFragmentManager(),
                    this,Common.fragmentList);
            viewPager.setAdapter(questionFragmentAdapter);

            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void genFragmentList() {
        for (int i=0;i<Common.questionList.size();i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            QuestionFragment fragment = new QuestionFragment();
            fragment.setArguments(bundle);

            Common.fragmentList.add(fragment);
        }
    }

    /**
     * SETUP TIMER
     */
    private void setTimer() {
        if(Common.countDownTimer == null) {
            startTimer();
        } else {
            Common.countDownTimer.cancel();
            startTimer();
        }
    }

    private void startTimer() {
        Common.countDownTimer = new CountDownTimer(Common.TOTAL_TIME,1000) {
            @Override
            public void onTick(long duration) {
                countTimer(duration);
            }
            @Override
            public void onFinish() {
                //Finish game
            }
        }.start();
    }

    private void countTimer(long duration) {
        txt_timer.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
        time_play -= 1000;
    }


    private void takeQuestion() {
        Common.questionList = DBHelper.getInstance(this).getQuestionByCategory(Common.selectedCategory.getId());
        if(Common.questionList.size() == 0) {
            //If no question
            new MaterialStyledDialog.Builder(this)
                    .setTitle("Opssss!!")
                    .setIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                    .setDescription("We don't have any question in this "+Common.selectedCategory.getName()+" category")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            finish();
                        }
            }).show();
        } else {
            if(Common.answerSheetList.size() > 0)
                Common.answerSheetList.clear();

            /* Gen answerSheet item from question
            30 question = 30 answer sheet item
            1 question = 1 answer sheet item */
            for(int i=0;i <Common.questionList.size();i++) {
                Common.answerSheetList.add(new CurrentQuestion(i,Common.ANSWER_TYPE.NO_ANSWER)); //default all answer is no answer
            }
        }
    }

    private void loadAnswerSheet() {
        answerSheetAdapter = new AnswerSheetAdapter(this,Common.answerSheetList);
        answer_sheet_view_recycler.setAdapter(answerSheetAdapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        if(Common.countDownTimer != null)
            Common.countDownTimer.cancel();
        super.onDestroy();
    }

}
