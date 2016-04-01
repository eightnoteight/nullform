package smartzero.eightnoteight.nullform;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class ResponsesFragment extends Fragment {

    private EditText form_id_et;
    private Button load_responses_btn;
    private EditText responses_et;

    Firebase fbref = new Firebase("https://nullform.firebaseio.com/forms");

    public ResponsesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_responses, container, false);
        form_id_et = (EditText) root.findViewById(R.id.responses_form_id);
        load_responses_btn = (Button) root.findViewById(R.id.load_responses_btn);
        responses_et = (EditText) root.findViewById(R.id.responses);
        load_responses_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadResponses(form_id_et.getText().toString());
            }
        });
        return root;
    }

    public void loadResponses(String form_id) {
        Firebase respref = fbref.child(form_id).child("responses");
        respref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, ArrayList<String>> data = (Map<String, ArrayList<String>>) dataSnapshot.getValue();
                responses_et.setText(data.toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
