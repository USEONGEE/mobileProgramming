package com.example.ucanhealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FirstFragment extends Fragment {

    Button button_Routine1,button_Routine2,button_Routine3;
    public FirstFragment(){
        //Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first,container,false);

        button_Routine1 = (Button)view.findViewById(R.id.button_Routine1);
        button_Routine2 = (Button)view.findViewById(R.id.button_Routine2);
        button_Routine3 = (Button)view.findViewById(R.id.button_Routine3);

        button_Routine1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),Routine1.class);
                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);

    }

}
