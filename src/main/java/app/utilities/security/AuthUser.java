package app.utilities.security;


import app.entities.User;
import io.javalin.http.Context;

public class AuthUser {

        public static UserRole getUserRole(Context ctx) {
            User user = ctx.sessionAttribute("currentUser");
            if (user == null) {
                return UserRole.VISITOR;
            }
            if (user.isAdmin()) {
                return UserRole.ADMIN;
            }
            return UserRole.LOGGED_IN;
        }
    }


