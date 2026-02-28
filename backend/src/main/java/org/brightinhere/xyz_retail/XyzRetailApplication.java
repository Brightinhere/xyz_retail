package org.brightinhere.xyz_retail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class XyzRetailApplication {

    public static void main(String[] args) {
        SpringApplication.run(XyzRetailApplication.class, args);
    }

}
