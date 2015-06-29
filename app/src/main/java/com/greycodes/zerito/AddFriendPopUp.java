package com.greycodes.zerito;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.greycodes.zerito.app.AppController;
import com.greycodes.zerito.helper.SendRequestService;

/**
 * Created by ajmal on 3/4/15.
 */
public class AddFriendPopUp extends DialogFragment {
public static TextView  tvname,tvnumber,tvsendrequest;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.addfriendpop, container,false);
        tvname= (TextView) rootView.findViewById(R.id.afp_name);
        tvnumber= (TextView) rootView.findViewById(R.id.afp_number);
        tvsendrequest= (TextView) rootView.findViewById(R.id.afp_send);

        tvname.setText(AppController.afpName);
        tvnumber.setText(AppController.afpNumber);
        if (!AppController.afptype){
            tvsendrequest.setText("Invite");
        }
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvsendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.afptype){
                    Intent intent = new Intent(getActivity(), SendRequestService.class);
                    getActivity().startService(intent);
                    HomeActivity.removepopup();

                }else{
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Hey check out this cool app I found,Zerito.It lets you change your friends wallpapers and more. Link : https://play.google.com/store/apps/details?id=com.greycodes.zerito";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Zerito");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    HomeActivity.removepopup();
                    startActivity(sharingIntent);

                }
            }
        });
// Do something else
        return rootView;
    }


}
