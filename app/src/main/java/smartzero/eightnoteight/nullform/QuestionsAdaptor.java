package smartzero.eightnoteight.nullform;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by eightnoteight on 3/31/16.
 */
public class QuestionsAdaptor extends ArrayAdapter<Question> {
    private List<Question> list = Collections.synchronizedList(new ArrayList<Question>());
    public QuestionsAdaptor(Context context, int resource) {
        super(context, resource);
        // TODO: do something
    }

    @Override
    public void add(Question question) {
        list.add(question);
        super.add(question);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final Question question = getItem(position);
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.create_question_layout, parent, false);
        }
        EditText questionView = (EditText)row.findViewById(R.id.question_n);
        questionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                question.setQuestionText(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        questionView.setHint(question.getQuestionHint());
        questionView.setText(question.getQuestionText());
        return row;
    }
}
