#  Doctor Backend

Java backend εφαρμογή για κλινική, υλοποιημένη με Jakarta EE, Hibernate και PostgreSQL.

##  Τεχνολογίες

- Java 17+
- Jakarta EE (JAX-RS, JPA)
- Hibernate (JPA provider)
- PostgreSQL
- Payara Server (Deployment)
- Maven
- JWT (Authentication)
- BCrypt (Password hashing)
- H2 (In-memory DB για testing)

##  Δομή

```
doctor-backend/
├── src/
│   ├── main/
│   │   ├── java/           # Κώδικας εφαρμογής ( entities, services)
│   │   └── resources/      # persistence.xml, config
│   └── test/               # Unit tests
├── pom.xml                 # Maven dependencies και build settings
└── README.md
```

## ⚙️ Ρυθμίσεις

### 📄 `persistence.xml`

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

Για testing υπάρχει `ClinicTestPU` που χρησιμοποιεί H2.

---

##  Τρέξιμο Tests

```bash
mvn test
```

---

##  Build & Deploy

1. **Build** το αρχείο `.war`:
```bash
mvn clean package
```

2. **Deploy** σε Payara:
   - Πήγαινε στο Payara admin console
   - Κάνε deploy το αρχείο `target/doctor-backend.war`

---

##  Authentication

Χρησιμοποιείται JWT token-based login.
- Το token αποθηκεύεται στο frontend και αποστέλλεται ως `Authorization: Bearer <token>` σε κάθε αίτημα.

---

