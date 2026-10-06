import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.ohdsi.webapi.util.GenericExceptionMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.support.ErrorMessage;

import javax.ws.rs.core.Response;
import java.sql.SQLException;

/**
 * Drives GenericExceptionMapper with the DataIntegrityViolationException shapes seen in
 * production logs and reports, per scenario, whether the client response is safe:
 * no exception escapes the mapper, the status is 409 and no raw database text is returned.
 */
public class ErrorMapperRepro {

    private static final String RAW_DB_TEXT =
            "ERROR: duplicate key value violates unique constraint \"uq_cs_name\"";

    public static void main(String[] args) {
        // The mapper logs every stack trace at ERROR; keep the console to the scenario table.
        Configurator.setLevel(GenericExceptionMapper.class.getName(), Level.OFF);
        int failures = 0;
        failures += run("no cause", new DataIntegrityViolationException("could not execute statement"));
        failures += run("single cause", new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("constraint violation")));
        failures += run("nested cause without Detail", new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("constraint violation", new SQLException(RAW_DB_TEXT))));
        failures += run("nested cause with Detail", new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("constraint violation",
                        new SQLException(RAW_DB_TEXT + "\n  Detail: Key (name)=(Diabetes) already exists."))));
        System.out.println();
        if (failures > 0) {
            System.out.println("REPRODUCED: " + failures + " of 4 scenarios return an unsafe response");
            System.exit(1);
        }
        System.out.println("ok: all 4 scenarios return a safe 409");
    }

    private static int run(String name, Throwable ex) {
        String outcome;
        boolean safe;
        try {
            Response response = new GenericExceptionMapper().toResponse(ex);
            String message = ((ErrorMessage) response.getEntity()).getPayload().getMessage();
            boolean leaks = message != null && (message.contains("duplicate key") || message.contains("constraint \"")
                    || message.contains("Key ("));
            safe = response.getStatus() == 409 && !leaks;
            outcome = response.getStatus() + " \"" + message + "\"" + (leaks ? "  <- raw database text" : "");
        } catch (RuntimeException e) {
            safe = false;
            outcome = "mapper threw " + e.getClass().getName();
        }
        System.out.printf("%-6s %-30s %s%n", safe ? "ok" : "FAIL", name, outcome);
        return safe ? 0 : 1;
    }
}
