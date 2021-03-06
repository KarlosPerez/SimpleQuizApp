package projects.karlosp3rez.androidquiz.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import projects.karlosp3rez.androidquiz.Adapter.AnswerSheetAdapter;
import projects.karlosp3rez.androidquiz.Adapter.AnswerSheetHelperAdapter;
import projects.karlosp3rez.androidquiz.Adapter.QuestionFragmentAdapter;
import projects.karlosp3rez.androidquiz.Common.Common;
import projects.karlosp3rez.androidquiz.Common.SpaceDecoration;
import projects.karlosp3rez.androidquiz.DBHelper.DBHelper;
import projects.karlosp3rez.androidquiz.Model.CurrentQuestion;
import projects.karlosp3rez.androidquiz.Fragments.QuestionFragment;
import projects.karlosp3rez.androidquiz.R;

public class QuestionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int GET_CODE_RESULT = 9999;
    int time_play = Common.TOTAL_TIME;
    boolean isAnswerModeView = false;

    AnswerSheetAdapter answerSheetAdapter;
    AnswerSheetHelperAdapter answerSheetHelperAdapter;
    TextView txt_right_answer, txt_timer, txt_wrong_answer;
    RecyclerView answer_sheet_view_recycler, answer_sheet_helper;
    Button btn_done;
    NavigationView navigationView;

    ViewPager viewPager;
    TabLayout tabLayout;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    BroadcastReceiver goToQuestionNum = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Common.KEY_GO_TO_QUESTION)) {
                int question = intent.getIntExtra(Common.KEY_GO_TO_QUESTION, -1);
                if(question != -1)
                    viewPager.setCurrentItem(question);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        initUI();

        //register BroadCast
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(goToQuestionNum, new IntentFilter(Common.KEY_GO_TO_QUESTION));

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishGameDialog();
            }
        });

        //Get questions from DB
        takeQuestion();

        if(Common.questionList.size() > 0) {
            //Show TextView right answer and text view timer
            txt_timer.setVisibility(View.VISIBLE);
            txt_right_answer.setVisibility(View.VISIBLE);
            txt_right_answer.setText(new StringBuilder(
                    String.format("%d/%d",Common.right_answer_count,Common.questionList.size())));

            answer_sheet_view_recycler = (RecyclerView) findViewById(R.id.grid_answer_recycler);
            answer_sheet_view_recycler.setHasFixedSize(true);
            if (Common.questionList.size() > 5) { //if question List have size > 5, we will separate 2 rows
                answer_sheet_view_recycler.setLayoutManager(new GridLayoutManager(this, Common.questionList.size() / 2));
            }

            setCounDownTimer();
            loadAnswerSheetPanel();
            genFragmentList();

            QuestionFragmentAdapter questionFragmentAdapter = new QuestionFragmentAdapter(getSupportFragmentManager(),
                    this,Common.fragmentList);

            viewPager.setAdapter(questionFragmentAdapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                int SCROLLING_RIGHT = 0;
                int SCROLLING_LEFT = 1;
                int SCROLLING_UNDETERMINED = 2;

                int currentScrollDirection = 2;

                private void setScrollingDirection(float positionOffset) {
                    if((1-positionOffset) >= 0.5) {
                        this.currentScrollDirection = SCROLLING_RIGHT;
                    } else if((1-positionOffset) <= 0.5){
                        this.currentScrollDirection = SCROLLING_LEFT;
                    }
                }

                private boolean isScrollDirectionUndetermined() {
                    return currentScrollDirection == SCROLLING_UNDETERMINED;
                }

                private boolean isScrollingRight() {
                    return currentScrollDirection == SCROLLING_RIGHT;
                }

                private boolean isScrollingLeft() {
                    return currentScrollDirection == SCROLLING_LEFT;
                }

                @Override
                public void onPageScrolled(int i, float v, int i1) {
                    if(isScrollDirectionUndetermined()) {
                        setScrollingDirection(v);
                    }
                }

                @Override
                public void onPageSelected(int i) {
                    QuestionFragment questionFragment;
                    int position = 0;
                    if(i>0) {
                        if(isScrollingRight()) {
                            //if user scroll to right, get preview fragment to calculate result
                            questionFragment = Common.fragmentList.get(i-1);
                            position = i-1;
                        } else if(isScrollingLeft()) {
                            //if user scroll to left, get next fragment to calculate result
                            questionFragment = Common.fragmentList.get(i+1);
                            position = i+1;
                        } else {
                            questionFragment = Common.fragmentList.get(position);
                        }
                    } else {
                        questionFragment = Common.fragmentList.get(0);
                        position = 0;
                    }

                    //if you want to show correct answer, just call function here
                    showCorrectAnswer(position, questionFragment);
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                    if(i == viewPager.SCROLL_STATE_IDLE) {
                        this.currentScrollDirection = SCROLLING_UNDETERMINED;
                    }
                }
            });
            viewPager.setOffscreenPageLimit(Common.questionList.size()); //fixed ViewPager size
        }
    }


    private void initUI() {
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Common.selectedCategory.getName());
        setSupportActionBar(toolbar);

        //Navigation Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        answer_sheet_helper = (RecyclerView) hView.findViewById(R.id.answer_sheet_recycler_helper);
        answer_sheet_helper.setHasFixedSize(true);
        answer_sheet_helper.setLayoutManager(new GridLayoutManager(this, 3));
        answer_sheet_helper.addItemDecoration(new SpaceDecoration(2));

        btn_done = (Button) hView.findViewById(R.id.btn_done);

        txt_right_answer = (TextView) findViewById(R.id.txt_question_right);
        txt_timer = (TextView) findViewById(R.id.txt_timer);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

    }

    private void showCorrectAnswer(int position, QuestionFragment questionFragment) {
        if(Common.answerSheetList.get(position).getType() == Common.ANSWER_TYPE.NO_ANSWER) {
            CurrentQuestion question_state = questionFragment.getSelectedAnswer();
            Common.answerSheetList.set(position, question_state); //set question answer for answerSheet
            answerSheetAdapter.notifyDataSetChanged(); //change color in answer sheet

            countCorrectAnswer();
            txt_right_answer.setText(new StringBuilder(String.format("%d", Common.right_answer_count))
                    .append("/")
                    .append(String.format("%d", Common.questionList.size())).toString());
            txt_wrong_answer.setText(String.valueOf(Common.wrong_answer_count));

            if (question_state.getType() != Common.ANSWER_TYPE.NO_ANSWER) {
                questionFragment.showCorrectAnswer();
                questionFragment.disableAnswer();
            }
        }
    }

    private void countCorrectAnswer() {
        //Reset variable
        Common.right_answer_count = Common.wrong_answer_count = 0;
        for (CurrentQuestion item: Common.answerSheetList) {
            if(item.getType() == Common.ANSWER_TYPE.RIGHT_ANSWER) {
                Common.right_answer_count++;
            } else if(item.getType() == Common.ANSWER_TYPE.WRONG_ANSWER) {
                Common.wrong_answer_count++;
            }
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
    private void setCounDownTimer() {
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
                finishGame();
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

    /**
     *
     */
    private void takeQuestion() {
        Common.questionList = DBHelper.getInstance(this).getQuestionByCategory(Common.selectedCategory.getId());
        if(Common.questionList.size() == 0) {
            noQuestionForSelectedCategoryDialog();
        } else {
            if(Common.answerSheetList.size() > 0)
                Common.answerSheetList.clear();

            /* Gen answerSheet item from question: 1 question => 1 answer sheet item */
            for(int i=0;i <Common.questionList.size();i++)
                Common.answerSheetList.add(new CurrentQuestion(i,Common.ANSWER_TYPE.NO_ANSWER)); //default all answer is no answer
        }
    }

    private void noQuestionForSelectedCategoryDialog() {
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
    }

    private void loadAnswerSheetPanel() {
        answerSheetAdapter = new AnswerSheetAdapter(this,Common.answerSheetList);
        answer_sheet_view_recycler.setAdapter(answerSheetAdapter);
        answerSheetHelperAdapter = new AnswerSheetHelperAdapter(this,Common.answerSheetList);
        answer_sheet_helper.setAdapter(answerSheetHelperAdapter);
    }

    private void viewQuizAnswer() {
        viewPager.setCurrentItem(0);

        isAnswerModeView = true;
        Common.countDownTimer.cancel();

        txt_wrong_answer.setVisibility(View.GONE);
        txt_right_answer.setVisibility(View.GONE);
        txt_timer.setVisibility(View.GONE);

        for(int i=0;i<Common.fragmentList.size();i++) {
            Common.fragmentList.get(i).showCorrectAnswer();
            Common.fragmentList.get(i).disableAnswer();
        }
    }

    private void resetValues() {
        viewPager.setCurrentItem(0);

        isAnswerModeView = false;
        countTimer(1);

        txt_wrong_answer.setVisibility(View.VISIBLE);
        txt_right_answer.setVisibility(View.VISIBLE);
        txt_timer.setVisibility(View.VISIBLE);

        for(CurrentQuestion item : Common.answerSheetList)
            item.setType(Common.ANSWER_TYPE.NO_ANSWER); //reset all question
        answerSheetAdapter.notifyDataSetChanged();
        answerSheetHelperAdapter.notifyDataSetChanged();

        for(int i=0; i<Common.fragmentList.size();i++)
            Common.fragmentList.get(i).resetQuestion();
    }

    /**
     * FINISH GAME
     */
    private void finishGameDialog() {
        if(!isAnswerModeView) {
            new MaterialStyledDialog.Builder(QuestionActivity.this)
                    .setTitle(getString(R.string.fninish_game_prompt))
                    .setIcon(R.drawable.ic_mood_black_24dp)
                    .setDescription(getString(R.string.finish_game_confirm))
                    .setNegativeText(getString(R.string.negative_answer))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveText(getString(R.string.positive_answer))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            finishGame();
                            drawerLayout.closeDrawer(Gravity.LEFT);
                        }
                    }).show();
        } else {
            finishGame();
        }
    }

    private void finishGame() {
        int position = viewPager.getCurrentItem();
        QuestionFragment questionFragment = Common.fragmentList.get(position);
        CurrentQuestion question_state = questionFragment.getSelectedAnswer();
        Common.answerSheetList.set(position,question_state); //set question answer for answerSheet
        answerSheetAdapter.notifyDataSetChanged(); //change color in answer sheet
        answerSheetHelperAdapter.notifyDataSetChanged();

        countCorrectAnswer();

        txt_right_answer.setText(new StringBuilder(String.format("%d",Common.right_answer_count))
                .append("/")
                .append(String.format("%d", Common.questionList.size())).toString());
        txt_wrong_answer.setText(String.valueOf(Common.wrong_answer_count));

        if(question_state.getType() != Common.ANSWER_TYPE.NO_ANSWER) {
            questionFragment.showCorrectAnswer();
            questionFragment.disableAnswer();
        }

        //We will navigate to new Result Activity here
        Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
        Common.timer = Common.TOTAL_TIME - time_play;
        Common.no_answer_count = Common.questionList.size() - (Common.wrong_answer_count+Common.right_answer_count);
        Common.data_question = new StringBuilder(new Gson().toJson(Common.answerSheetList));

        startActivityForResult(intent,GET_CODE_RESULT);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_wrong_answer);
        ConstraintLayout constraintLayout = (ConstraintLayout) item.getActionView();
        txt_wrong_answer = (TextView) constraintLayout.findViewById(R.id.txt_wrong_answer);
        txt_wrong_answer.setText(String.valueOf(0));

        return true;
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
        if (id == R.id.menu_finish_game) {
            if(!isAnswerModeView) {
                new MaterialStyledDialog.Builder(this)
                        .setTitle(getString(R.string.fninish_game_prompt))
                        .setIcon(R.drawable.ic_mood_black_24dp)
                        .setDescription(getString(R.string.finish_game_confirm))
                        .setNegativeText(getString(R.string.negative_answer))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText(getString(R.string.positive_answer))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                finishGame();
                            }
                        }).show();
            } else {
                finishGame();
            }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_CODE_RESULT) {
            if(resultCode == Activity.RESULT_OK) {
                String action = data.getStringExtra("action");
                if(action == null || TextUtils.isEmpty(action)) {
                    int questionNum = data.getIntExtra(Common.KEY_BACK_FROM_RESULT, -1);
                    viewPager.setCurrentItem(questionNum);

                    isAnswerModeView = true;
                    Common.countDownTimer.cancel();

                    txt_wrong_answer.setVisibility(View.GONE);
                    txt_right_answer.setVisibility(View.GONE);
                    txt_timer.setVisibility(View.GONE);
                } else {
                    if(action.equals("viewquizanswer")) {
                        viewQuizAnswer();
                    } else {
                        if(action.equals("doitagain")) {
                            resetValues();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(Common.countDownTimer != null)
            Common.countDownTimer.cancel();
        super.onDestroy();
    }

}
