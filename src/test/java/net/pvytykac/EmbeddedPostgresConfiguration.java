package net.pvytykac;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.io.IOException;

@Profile("test")
@Configuration
@EnableJpaRepositories(basePackages = {"net.pvytykac.db"})
@EntityScan(basePackages = {"net.pvytykac.db"})
public class EmbeddedPostgresConfiguration {

    @Bean
    public DataSource dataSource() throws IOException {
        //todo: find a way to close this after the entire test suite finishes - hopefully nothing's leaking
        return EmbeddedPostgres.builder()
            .setImage(DockerImageName.parse("postgres:17.2"))
            .start()
            .getPostgresDatabase();
    }

}