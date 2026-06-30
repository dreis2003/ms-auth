package br.com.ikonbrasil.auth.seguranca;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class SenhaSeedAdminTest {

    private static final String HASH_ADMIN_MATRIZ =
            "$2a$10$MskdGUVlTAfRZ41skSp8zuhsZHZ5de6YmgJz1T4wkhIzonejnnzGm";

    @Test
    void deveManterSenhaSeedDoAdminMatrizValida() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        assertThat(encoder.matches("admin123", HASH_ADMIN_MATRIZ)).isTrue();
    }
}
