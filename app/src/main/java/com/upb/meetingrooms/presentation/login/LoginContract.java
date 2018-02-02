package com.upb.meetingrooms.presentation.login;

import com.upb.meetingrooms.presentation.BasePresenter;
import com.upb.meetingrooms.presentation.BaseView;

public interface LoginContract {


    interface View extends BaseView<Presenter> {

        void executeLoginAction();

        void startMainActivity();
    }


    interface Presenter extends BasePresenter {

        void onLoginClicked();

        void goToMainActivity();
    }
}
