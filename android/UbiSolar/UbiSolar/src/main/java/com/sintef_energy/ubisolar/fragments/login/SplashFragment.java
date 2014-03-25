package com.sintef_energy.ubisolar.fragments.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import com.sintef_energy.ubisolar.R;

/**
 * Created by Lars Erik on 12.03.14.
 */
public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_splash,
                container, false);
        return view;
    }
}
