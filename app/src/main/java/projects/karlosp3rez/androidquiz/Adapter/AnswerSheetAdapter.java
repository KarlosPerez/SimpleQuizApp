package projects.karlosp3rez.androidquiz.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import projects.karlosp3rez.androidquiz.Common.Common;
import projects.karlosp3rez.androidquiz.Model.CurrentQuestion;
import projects.karlosp3rez.androidquiz.R;

public class AnswerSheetAdapter extends RecyclerView.Adapter<AnswerSheetAdapter.AnswerSheetViewHolder> {

    Context context;
    List<CurrentQuestion> currentQuestionList;

    public AnswerSheetAdapter(Context context, List<CurrentQuestion> currentQuestionList) {
        this.context = context;
        this.currentQuestionList = currentQuestionList;
    }

    @NonNull
    @Override
    public AnswerSheetViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_grid_answer_sheet_item, viewGroup, false);
        return new AnswerSheetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerSheetViewHolder answerSheetViewHolder, int i) {
        if(currentQuestionList.get(i).getType() == Common.ANSWER_TYPE.RIGHT_ANSWER) {
            answerSheetViewHolder.question_item.setBackgroundResource(R.drawable.grid_question_right_answer);
        } else if(currentQuestionList.get(i).getType() == Common.ANSWER_TYPE.WRONG_ANSWER) {
            answerSheetViewHolder.question_item.setBackgroundResource(R.drawable.grid_question_wrong_answer);
        } else {
            answerSheetViewHolder.question_item.setBackgroundResource(R.drawable.grid_question_no_answer);
        }
    }

    @Override
    public int getItemCount() {
        return currentQuestionList.size();
    }

    public class AnswerSheetViewHolder extends RecyclerView.ViewHolder {
        View question_item;

        public AnswerSheetViewHolder(@NonNull View itemView) {
            super(itemView);

            question_item = (View) itemView.findViewById(R.id.question_item);
        }
    }
}
