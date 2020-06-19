package com.wgu.c196.data;

import com.wgu.c196.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            if (!username.equals("test") || !password.equals("capstone")) {
                throw new Exception("User name or password is incorrect");
            }
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Test User");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException(e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}