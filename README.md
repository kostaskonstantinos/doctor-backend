
# Doctor Backend

Java backend application for a clinic, built using Jakarta EE, Hibernate, and PostgreSQL.

##  Technologies

- Java 17+
- Jakarta EE (JAX-RS, JPA)
- Hibernate (JPA provider)
- PostgreSQL
- Payara Server (Deployment)
- Maven
- JWT (Authentication)
- BCrypt (Password hashing)
- H2 (In-memory DB for testing)

##  Project Structure

```
doctor-backend/
├── src/
│   ├── main/
│   │   ├── java/           # Application source code (entities, services)
│   │   └── resources/      # Configuration files (e.g., persistence.xml)
│   └── test/               # Unit tests
├── pom.xml                 # Maven configuration
└── README.md
```

##  Configuration

### `persistence.xml`

```xml
<persistence-unit name="ClinicPU" transaction-type="JTA">
  <jta-data-source>jdbc/clinicdb</jta-data-source>
  <class>com.clinic.entity.Doctor</class>
  ...
  <properties>
    <property name="hibernate.show_sql" value="true"/>
    <property name="hibernate.format_sql" value="true"/>
  </properties>
</persistence-unit>
```

For testing, a separate `ClinicTestPU` configuration using H2 is provided.

---

## Run Tests

```bash
mvn test
```



##  Build & Deploy

1. **Build the `.war` file**:
```bash
mvn clean package
```

2. **Deploy** to Payara:
   - Access the Payara Admin Console
   - Deploy `target/doctor-backend.war`



##  Authentication

JWT token-based login is used.  
The token is stored in the frontend and sent via the `Authorization: Bearer <token>` header.




