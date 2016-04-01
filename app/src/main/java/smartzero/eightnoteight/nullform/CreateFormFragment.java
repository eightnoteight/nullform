package smartzero.eightnoteight.nullform;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;


/*
    public static class CreateFormFragment extends Fragment {
        public CreateFormFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_createform, container, false);
            ListView questions = (ListView) rootView.findViewById(R.id.listView);

            return  rootView;
        }
    }
*/
public class CreateFormFragment extends Fragment {

    private Firebase fbref = new Firebase("https://nullform.firebaseio.com/forms");

    public CreateFormFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_form, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button create_form_btn = (Button) getView().findViewById(R.id.create_form_button);
        create_form_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createForm(v);
            }
        });
    }


    public void createForm(View view) {
        EditText questionsEditText = (EditText) getView().findViewById(R.id.questions_et);
        String questionsString = questionsEditText.getText().toString();
        String[] questions = questionsString.split("\n");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("uid", getArguments().getString("uid"));
        data.put("questions", questions);
        data.put("responses", null);
        Firebase elem = fbref.push();
        elem.setValue(data);
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("nullform", elem.getKey()));
        Toast.makeText(getContext(), "form_id copied to the clipboard", Toast.LENGTH_LONG).show();
        questionsEditText.setText("");
    }
}
