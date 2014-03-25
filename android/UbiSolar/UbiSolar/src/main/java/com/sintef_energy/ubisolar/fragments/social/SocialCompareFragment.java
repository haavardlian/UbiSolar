package com.sintef_energy.ubisolar.fragments.social;

        import android.app.Activity;
        import android.app.Fragment;
        import android.app.LoaderManager;
        import android.content.CursorLoader;
        import android.content.Loader;
        import android.database.Cursor;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ListView;
        import android.widget.SimpleCursorAdapter;

        import com.sintef_energy.ubisolar.R;
        import com.sintef_energy.ubisolar.activities.DrawerActivity;
        import com.sintef_energy.ubisolar.adapter.FriendAdapter;
        import com.sintef_energy.ubisolar.fragments.DefaultTabFragment;
        import com.sintef_energy.ubisolar.model.User;

        import java.util.ArrayList;

/**
 * Created by baier on 3/21/14.
 */
public class SocialCompareFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = SocialFragment.class.getName();

    private SimpleCursorAdapter adapter;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_compare, container, false);

        return view;
    }

}