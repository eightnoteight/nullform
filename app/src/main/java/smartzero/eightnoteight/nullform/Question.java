package smartzero.eightnoteight.nullform;

import android.widget.EditText;

/**
 * Created by eightnoteight on 3/31/16.
 */
public class Question {
    private String questionText;

    private String questionHint;

    public Question() {
    }

    public String getQuestionHint() {
        return questionHint;
    }

    public void setQuestionHint(String questionHint) {
        this.questionHint = questionHint;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
