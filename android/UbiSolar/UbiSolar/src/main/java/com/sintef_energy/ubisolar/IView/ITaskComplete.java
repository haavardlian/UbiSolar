package com.sintef_energy.ubisolar.IView;

/**
 * Created by perok on 05.05.14.
 */
public interface ITaskComplete <T> {
    public void onComplete(T t);
    public void updateProgress(int progress);
}
