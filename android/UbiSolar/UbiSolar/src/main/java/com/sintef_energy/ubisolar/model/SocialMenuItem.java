package com.sintef_energy.ubisolar.model;

import android.graphics.drawable.Drawable;

/**
 * Created by baier on 3/22/14.
 */
public class SocialMenuItem {

        private String labelId;
        private Drawable socialHomeIcon;
        private String socialHomeLabel;


    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public Drawable getSocialHomeIcon() {
        return socialHomeIcon;
    }

    public void setSocialHomeIcon(Drawable socialHomeIcon) {
        this.socialHomeIcon = socialHomeIcon;
    }

    public String getSocialHomeLabel() {
        return socialHomeLabel;
    }

    public void setSocialHomeLabel(String socialHomeLabel) {
        this.socialHomeLabel = socialHomeLabel;
    }

    public SocialMenuItem (String socialHomeLabel, Drawable socialHomeIcon) {
            this.socialHomeLabel = socialHomeLabel;
            this.socialHomeIcon = socialHomeIcon;
    }

}
