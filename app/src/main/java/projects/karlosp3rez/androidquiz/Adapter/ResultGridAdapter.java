package projects.karlosp3rez.androidquiz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import projects.karlosp3rez.androidquiz.Common.Common;
import projects.karlosp3rez.androidquiz.Model.CurrentQuestion;
import projects.karlosp3rez.androidquiz.R;

public class ResultGridAdapter extends RecyclerView.Adapter<ResultGridAdapter.ResultGridViewHolder> {

    Context context;
    List<CurrentQuestion> currentQuestionList;

    public ResultGridAdapter(Context context, List<CurrentQuestion> currentQuestionList) {
        this.context = context;
        this.currentQuestionList = currentQuestionList;
    }

    @NonNull
    @Override
    public ResultGridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_result_item, viewGroup, false);
        return new ResultGridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultGridViewHolder resultGridViewHolder, int i) {

        Drawable img;

        //Change color based on result
        resultGridViewHolder.btn_question.setText(new StringBuilder("Question").append(
                currentQuestionList.get(i).getQuestionIndex()+1));
        if(currentQuestionList.get(i).getType() == Common.ANSWER_TYPE.RIGHT_ANSWER) {
            resultGridViewHolder.btn_question.setBackgroundColor(Color.parseColor("#ff99cc00"));
            img = context.getResources().getDrawable(R.drawable.ic_check_white_24dp);
            resultGridViewHolder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null,null,null,img);
        } else if(currentQuestionList.get(i).getType() == Common.ANSWER_TYPE.WRONG_ANSWER) {
            resultGridViewHolder.btn_question.setBackgroundColor(Color.parseColor("#FFCC0000"));
            img = context.getResources().getDrawable(R.drawable.ic_clear_white_24dp);
            resultGridViewHolder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null,null,null,img);
        } else {
            img = context.getResources().getDrawable(R.drawable.ic_error_outline_white_24dp);
            resultGridViewHolder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null,null,null,img);
        }
    }

    @Override
    public int getItemCount() {
        return currentQuestionList.size();
    }

    public class ResultGridViewHolder extends RecyclerView.ViewHolder {

        Button btn_question;

        public ResultGridViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_question = (Button) itemView.findViewById(R.id.btn_question);
            btn_question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //When user click to question button, we willget backto questionActivity to show question
                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(new Intent(Common.KEY_BACK_FROM_RESULT).putExtra(Common.KEY_GO_TO_QUESTION
                                    ,currentQuestionList.get(getAdapterPosition()).getQuestionIndex()));
                }
            });
        }
    }
}
