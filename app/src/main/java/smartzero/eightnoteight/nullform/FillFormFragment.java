package smartzero.eightnoteight.nullform;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;


public class FillFormFragment extends Fragment {
    private EditText form_id_et;
    private EditText form_response_et;
    private Button load_form_btn;
    private Button submit_response_btn;
    private String uid;
    Firebase fbref = new Firebase("https://nullform.firebaseio.com/forms");
    public FillFormFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstaceState) {
        uid = getArguments().getString("uid");
        View root = inflater.inflate(R.layout.fragment_fillform, container, false);
        form_id_et = (EditText) root.findViewById(R.id.form_id);
        form_response_et = (EditText) root.findViewById(R.id.form_response);
        load_form_btn = (Button) root.findViewById(R.id.load_form_button);
        submit_response_btn = (Button) root.findViewById(R.id.submit_response_btn);
        load_form_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadForm(form_id_et.getText().toString());
            }
        });
        submit_response_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResponse(form_response_et.getText().toString());
            }
        });
        return root;
    }

    public void loadForm(final String form_id) {
        Firebase formPoint = fbref.child(form_id).child("questions");
        formPoint.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    System.out.println("NF_ERROR: no such form with form_id=" + form_id);
                }
                else {
                    ArrayList<String> questions = (ArrayList<String>)dataSnapshot.getValue();
                    System.out.println("form_id=" + form_id + ": questions=" + questions.toString());
                    ByteArrayOutputStream bastream = new ByteArrayOutputStream();
                    PrintWriter out = new PrintWriter(bastream);
                    for(String question: questions) {
                        out.println(question);
                        out.println("answer: ");
                    }
                    out.flush();
                    form_response_et.setText(new String(bastream.toByteArray()));
                    form_response_et.setTag(questions);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("NF_ERROR: " + firebaseError.toString());
            }
        });
    }

    public void submitResponse(String response) {
        String resp = form_response_et.getText().toString();
        ArrayList<String> questions = (ArrayList<String>)form_response_et.getTag();
        Scanner scanner = new Scanner(resp);
        ArrayList<String> answers = new ArrayList<String>();
        int i = 0;
        while(scanner.hasNextLine()) {
            if (i > 2 * questions.size()) {
                form_response_et.setError("more lines than expected, please load the form again");
                return;
            }
            if (i % 2 == 0) {
                String q = scanner.nextLine();
                if (q.compareTo(questions.get(i / 2)) != 0) {
                    form_response_et.setError("you are not supposed to edit the questions, please load the form again");
                    return;
                }
            }
            else {
                String answer = scanner.nextLine();
                if (answer.startsWith("answer: ")) {
                    answer = answer.substring(8);
                }
                else if (answer.startsWith("answer:")) {
                    answer = answer.substring(7);
                }
                answers.add(answer);
            }
            i++;
        }
        if (i != 2 * questions.size()) {
            form_response_et.setError("you are supposed to answer all the questions, please load the form again");
            return;
        }
        String form_id = form_id_et.getText().toString();
        fbref.child(form_id).child("responses").push().setValue(answers);
        Toast.makeText(getContext(), "response created", Toast.LENGTH_LONG).show();
        form_response_et.setText("");
        form_id_et.setText("");
    }
}
