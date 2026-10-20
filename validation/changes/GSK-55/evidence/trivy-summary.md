# Trivy scan: GSK-55

Trivy 0.56.2, `rootfs --scanners vuln` over `WebAPI.war`, all severities, run locally on 2026-10-20 with the same vulnerability DB for both WARs. Counts are unique CVE ids. The CI `scan` check re-runs this on the PR.

| WAR | critical | high | medium | low | total |
|---|---|---|---|---|---|
| `main` 37e8066 (Boot 1.5, JDK 8) | 17 | 81 | 79 | 10 | 187 |
| branch 8cbf811 (Boot 2.7, JDK 17) | 13 | 69 | 71 | 12 | 165 |

## Closed by this change (38)

| CVE | severity | package (version on main) |
|---|---|---|
| CVE-2020-1938 | CRITICAL | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2020-5413 | CRITICAL | org.springframework.integration:spring-integration-core 4.3.21.RELEASE |
| CVE-2022-22965 | CRITICAL | org.springframework.boot:spring-boot-starter-web 1.5.22.RELEASE |
| CVE-2023-46604 | CRITICAL | org.apache.activemq:activemq-client 5.14.5 |
| CVE-2014-0114 | HIGH | commons-beanutils:commons-beanutils 1.9.3 |
| CVE-2018-11775 | HIGH | org.apache.activemq:activemq-client 5.14.5 |
| CVE-2019-0222 | HIGH | org.apache.activemq:activemq-client 5.14.5 |
| CVE-2019-10086 | HIGH | commons-beanutils:commons-beanutils 1.9.3 |
| CVE-2019-12418 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2019-17563 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2020-11996 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2020-13935 | HIGH | org.apache.tomcat.embed:tomcat-embed-websocket 8.5.43 |
| CVE-2020-9484 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2021-25122 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2021-25329 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2022-22968 | HIGH | org.springframework:spring-context 4.3.25.RELEASE |
| CVE-2022-22970 | HIGH | org.springframework:spring-beans 4.3.25.RELEASE |
| CVE-2022-25647 | HIGH | com.google.code.gson:gson 2.8.5 |
| CVE-2022-27772 | HIGH | org.springframework.boot:spring-boot 1.5.22.RELEASE |
| CVE-2022-42252 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2023-1370 | HIGH | net.minidev:json-smart 2.2.1 |
| CVE-2023-20863 | HIGH | org.springframework:spring-expression 4.3.25.RELEASE |
| CVE-2023-20883 | HIGH | org.springframework.boot:spring-boot-autoconfigure 1.5.22.RELEASE |
| CVE-2023-46589 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2018-1000873 | MEDIUM | com.fasterxml.jackson.datatype:jackson-datatype-jsr310 2.8.11 |
| CVE-2020-1935 | MEDIUM | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2021-24122 | MEDIUM | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2021-27568 | MEDIUM | net.minidev:json-smart 2.2.1 |
| CVE-2021-37533 | MEDIUM | commons-net:commons-net 3.5 |
| CVE-2022-22950 | MEDIUM | org.springframework:spring-expression 4.3.25.RELEASE |
| CVE-2022-22971 | MEDIUM | org.springframework:spring-messaging 4.3.25.RELEASE |
| CVE-2023-20861 | MEDIUM | org.springframework:spring-expression 4.3.25.RELEASE |
| CVE-2023-34036 | MEDIUM | org.springframework.hateoas:spring-hateoas 0.23.0.RELEASE |
| CVE-2023-41080 | MEDIUM | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2023-42795 | MEDIUM | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2023-44487 | MEDIUM | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2023-45648 | MEDIUM | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |
| CVE-2024-21733 | MEDIUM | org.apache.tomcat.embed:tomcat-embed-core 8.5.43 |

## New with this change (16)

All are in the Boot 2.7.18 managed versions (Tomcat 9.0.83, Spring 5.3.31, Micrometer 1.9.17, Boot 2.7.18). Boot 2.7 open-source support has ended; fixes are in later Tomcat 9.0.x or Spring 6 / Boot 3. Follow-up per ADR: one PR per library family.

| CVE | severity | package (version) | fixed in |
|---|---|---|---|
| CVE-2024-38816 | HIGH | org.springframework:spring-webmvc 5.3.31 | 6.1.13 |
| CVE-2024-56337 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 9.0.83 | 11.0.2, 10.1.34, 9.0.98 |
| CVE-2025-41249 | HIGH | org.springframework:spring-core 5.3.31 | 6.2.11 |
| CVE-2025-48989 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 9.0.83 | 11.0.10, 10.1.44, 9.0.108 |
| CVE-2026-24734 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 9.0.83 | 11.0.18, 10.1.52, 9.0.115 |
| CVE-2026-34483 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 9.0.83 | 9.0.116, 10.1.54, 11.0.21 |
| CVE-2026-34487 | HIGH | org.apache.tomcat.embed:tomcat-embed-core 9.0.83 | 9.0.117, 10.1.54, 11.0.21 |
| CVE-2026-40984 | HIGH | io.micrometer:micrometer-core 1.9.17 | 1.16.6, 1.15.12 |
| CVE-2024-38828 | MEDIUM | org.springframework:spring-webmvc 5.3.31 | 5.3.42 |
| CVE-2025-41242 | MEDIUM | org.springframework:spring-webmvc 5.3.31 | 6.2.10 |
| CVE-2025-49124 | MEDIUM | org.apache.tomcat.embed:tomcat-embed-core 9.0.83 | 11.0.8, 10.1.42, 9.0.106 |
| CVE-2025-55668 | MEDIUM | org.apache.tomcat.embed:tomcat-embed-core 9.0.83 | 11.0.8, 10.1.42, 9.0.106 |
| CVE-2026-22737 | MEDIUM | org.springframework:spring-webmvc 5.3.31 | 7.0.6, 6.2.17 |
| CVE-2026-41001 | MEDIUM | org.springframework.boot:spring-boot-autoconfigure 2.7.18 | 4.0.7, 3.5.15 |
| CVE-2025-55754 | LOW | org.apache.tomcat.embed:tomcat-embed-core 9.0.83 | 11.0.11, 10.1.45, 9.0.109 |
| CVE-2026-22735 | LOW | org.springframework:spring-webmvc 5.3.31 | 7.0.6, 6.2.17 |
