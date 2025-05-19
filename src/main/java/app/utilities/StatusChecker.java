package app.utilities;

public class StatusChecker {
    public static String getStatusText(int status) {
        return switch (status) {
            case 1 -> "Behandler";
            case 2 -> "Afventer betaling";
            case 3 -> "Forespørgsel afsluttet";
            default -> "Ukendt";
        };
    }
}
