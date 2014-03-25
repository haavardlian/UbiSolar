package com.sintef_energy.ubisolar.fragments.social;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sintef_energy.ubisolar.R;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * Created by baier on 3/21/14.
 */
public class SocialCard extends Card {

    protected TextView socialListItemLabel;
    protected ImageView icon;

    public SocialCard(Context context) {
        super(context);
    }

}
