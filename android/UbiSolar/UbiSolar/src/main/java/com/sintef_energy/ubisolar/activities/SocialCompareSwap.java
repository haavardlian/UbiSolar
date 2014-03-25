package com.sintef_energy.ubisolar.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sintef_energy.ubisolar.R;

/**
 * Created by baier on 3/24/14.
 */
public class SocialCompareSwap extends Activity implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }

    public class SingleListItem extends Activity{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(R.layout.fragment_social_friends_row);

            Intent i = getIntent();
            // getting attached intent data


        }
    }

}
