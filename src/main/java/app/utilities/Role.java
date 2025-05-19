package app.utilities;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    ADMIN,
    USER
}