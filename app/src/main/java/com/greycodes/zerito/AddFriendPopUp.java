package com.greycodes.zerito;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by ajmal on 3/4/15.
 */
public class AddFriendPopUp extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.addfriendpop, container,false);
        getDialog().setTitle("DialogFragment Tutorial");
// Do something else
        return rootView;
    }
}
