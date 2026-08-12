package com.schoolfinance.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditDatabaseProtectionInitializer
        implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public void run(
            ApplicationArguments args
    ) {

        jdbcTemplate.execute("""
                CREATE OR REPLACE FUNCTION school_finance.prevent_audit_log_mutation()
                RETURNS trigger
                LANGUAGE plpgsql
                AS $$
                BEGIN
                    RAISE EXCEPTION
                    'audit_logs is immutable: UPDATE and DELETE are forbidden';
                END;
                $$;
                """);


        jdbcTemplate.execute("""
                DO $$
                BEGIN

                    IF NOT EXISTS (
                        SELECT 1
                        FROM pg_trigger
                        WHERE tgname = 'trg_audit_logs_immutable'
                    )
                    THEN

                        CREATE TRIGGER trg_audit_logs_immutable
                        BEFORE UPDATE OR DELETE
                        ON school_finance.audit_logs
                        FOR EACH ROW
                        EXECUTE FUNCTION school_finance.prevent_audit_log_mutation();

                    END IF;

                END;
                $$;
                """);
    }
}