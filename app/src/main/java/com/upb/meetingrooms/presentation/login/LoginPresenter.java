package com.upb.meetingrooms.presentation.login;

public class LoginPresenter implements LoginContract.Presenter{


    private final LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void onLoginClicked() {
        view.executeLoginAction();
    }

    @Override
    public void goToMainActivity() {
        view.startMainActivity();
    }
}
