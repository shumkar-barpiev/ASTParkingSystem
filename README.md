# Parking Management System

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=shumkar-barpiev_ASTParkingSystem&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=shumkar-barpiev_ASTParkingSystem)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=shumkar-barpiev_ASTParkingSystem&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=shumkar-barpiev_ASTParkingSystem)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=shumkar-barpiev_ASTParkingSystem&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=shumkar-barpiev_ASTParkingSystem)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=shumkar-barpiev_ASTParkingSystem&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=shumkar-barpiev_ASTParkingSystem)


[![Coverage Status](https://coveralls.io/repos/github/shumkar-barpiev/ASTParkingSystem/badge.svg?branch=main)](https://coveralls.io/github/shumkar-barpiev/ASTParkingSystem?branch=main)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=shumkar-barpiev_ASTParkingSystem&metric=coverage)](https://sonarcloud.io/summary/new_code?id=shumkar-barpiev_ASTParkingSystem)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=shumkar-barpiev_ASTParkingSystem&metric=bugs)](https://sonarcloud.io/summary/new_code?id=shumkar-barpiev_ASTParkingSystem)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=shumkar-barpiev_ASTParkingSystem&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=shumkar-barpiev_ASTParkingSystem)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=shumkar-barpiev_ASTParkingSystem&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=shumkar-barpiev_ASTParkingSystem)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=shumkar-barpiev_ASTParkingSystem&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=shumkar-barpiev_ASTParkingSystem)

---

## About The Project
A desktop application built for managing parking zones and tracking active vehicle tickets.

* Java 8, Java Swing
* MongoDB, Docker, Maven
* JUnit 4, AssertJ Swing, Testcontainers, Mockito

---

### 1. Clone the Repository
To clone the repository:

```bash
git clone https://github.com/shumkar-barpiev/ASTParkingSystem.git
```
### Run Tests

Execute the full test (Unit, Integration, and End-to-End):

```bash
mvn verify
```

### Coverage (JaCoCo)

```bash
mvn clean test jacoco:report
```

### Mutation Testing

```bash
mvn pitest:mutationCoverage
```
