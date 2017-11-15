package com.mindcoders.phial.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mindcoders.phial.PhialScope;

/**
 * Created by rost on 11/8/17.
 */

public class AutoFillFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auto_fill, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final Button button = view.findViewById(R.id.button_login);
        ((ShareElementManager) getActivity()).addSharedElement(button, R.string.transition_button);

        view.findViewById(R.id.go_auto_fill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AutoFillActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        PhialScope.enterScope("Login");
    }

    @Override
    public void onPause() {
        super.onPause();
        PhialScope.exitScope("Login");
    }
}
