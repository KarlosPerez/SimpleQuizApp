package projects.karlosp3rez.androidquiz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import projects.karlosp3rez.androidquiz.Common.Common;
import projects.karlosp3rez.androidquiz.Interface.IRecyclerHelperClick;
import projects.karlosp3rez.androidquiz.Model.CurrentQuestion;
import projects.karlosp3rez.androidquiz.R;

public class AnswerSheetHelperAdapter extends RecyclerView.Adapter<AnswerSheetHelperAdapter.HelperViewHolder> {

    Context context;
    List<CurrentQuestion> currentQuestionList;

    public AnswerSheetHelperAdapter(Context context, List<CurrentQuestion> currentQuestionList) {
        this.context = context;
        this.currentQuestionList = currentQuestionList;
    }

    @NonNull
    @Override
    public HelperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_answer_sheet_helper, viewGroup, false);
        return new HelperViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HelperViewHolder helperViewHolder, int i) {
        helperViewHolder.txt_question_num.setText(String.valueOf(i+1)); // Show question number
        if(currentQuestionList.get(i).getType() == Common.ANSWER_TYPE.RIGHT_ANSWER)
            helperViewHolder.layout_wrapper.setBackgroundResource(R.drawable.grid_question_right_answer);
        else if(currentQuestionList.get(i).getType() == Common.ANSWER_TYPE.WRONG_ANSWER)
            helperViewHolder.layout_wrapper.setBackgroundResource(R.drawable.grid_question_wrong_answer);
        else
            helperViewHolder.layout_wrapper.setBackgroundResource(R.drawable.grid_question_no_answer);
        helperViewHolder.setiRecyclerHelperClick(new IRecyclerHelperClick() {
            @Override
            public void onClick(View view, int position) {
                //When user click to item , navigate to this question on Question Activity
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(new Intent(
                                Common.KEY_GO_TO_QUESTION
                        ).putExtra(Common.KEY_GO_TO_QUESTION,position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return currentQuestionList.size();
    }

    public class HelperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_question_num;
        LinearLayout layout_wrapper;
        IRecyclerHelperClick iRecyclerHelperClick;

        public void setiRecyclerHelperClick(IRecyclerHelperClick iRecyclerHelperClick) {
            this.iRecyclerHelperClick = iRecyclerHelperClick;
        }

        public HelperViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_question_num = (TextView) itemView.findViewById(R.id.txt_question_num);
            layout_wrapper = (LinearLayout) itemView.findViewById(R.id.layout_wrapper);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerHelperClick.onClick(view, getAdapterPosition());
        }
    }
}
