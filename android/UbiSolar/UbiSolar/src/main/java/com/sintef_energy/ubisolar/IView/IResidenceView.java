package com.sintef_energy.ubisolar.IView;
import com.sintef_energy.ubisolar.database.energy.ResidenceModel;

/**
 * Created by Lars Erik on 03.05.2014.
 */

public interface IResidenceView {
    public void addResidence(ResidenceModel model);
    public void deleteResidence(ResidenceModel model);
    public void editResidence(ResidenceModel model, String name, String description);
}
