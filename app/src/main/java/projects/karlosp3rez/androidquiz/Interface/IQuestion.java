package projects.karlosp3rez.androidquiz.Interface;

import projects.karlosp3rez.androidquiz.Model.CurrentQuestion;

public interface IQuestion {
    CurrentQuestion getSelectedAnswer(); //get selected answer from user select
    void showCorrectAnswer(); //Bold correct answer
    void disableAnswer();; //Disable all check box
    void resetQuestion(); //reset all function on question
}
