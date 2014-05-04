package com.sintef_energy.ubisolar.IView;

import com.sintef_energy.ubisolar.database.energy.UserModel;

/**
 * Created by Lars Erik on 04.05.2014.
 */
public interface IUserView  {
    public void addUser(UserModel model);
    public void deleteUser(UserModel model);
}
