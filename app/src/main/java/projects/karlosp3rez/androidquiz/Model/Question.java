package projects.karlosp3rez.androidquiz.Model;

public class Question {
    private int id;
    private String questionText, questionImage, answerA, answerB, asnwerC, answerD, correctAnswer;
    private int categoryId, isImageQuestion;

    public Question() {
    }

    public Question(int id, String questionText, String questionImage, String answerA, String answerB, String asnwerC, String answerD, String correctAnswer, int isImageQuestion, int categoryId) {
        this.id = id;
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.answerA = answerA;
        this.answerB = answerB;
        this.asnwerC = asnwerC;
        this.answerD = answerD;
        this.correctAnswer = correctAnswer;
        this.isImageQuestion = isImageQuestion;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAsnwerC() {
        return asnwerC;
    }

    public void setAsnwerC(String asnwerC) {
        this.asnwerC = asnwerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getIsImageQuestion() {
        return isImageQuestion;
    }

    public void setIsImageQuestion(int isImageQuestion) {
        this.isImageQuestion = isImageQuestion;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
