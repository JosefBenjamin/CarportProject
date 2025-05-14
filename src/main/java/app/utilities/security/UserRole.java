package app.utilities.security;

import io.javalin.security.RouteRole;

public enum UserRole implements RouteRole {

    VISITOR,
    LOGGED_IN,
    ADMIN

}
