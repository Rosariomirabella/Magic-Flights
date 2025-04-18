
# ✈️ Magic Flights - Sistema di Prenotazione Voli

Magic Flights è un'applicazione web per la prenotazione di voli e la gestione amministrativa di una compagnia aerea, sviluppata con **Spring Boot**, **Thymeleaf**, **Spring Data JPA** e **MySQL**.

---

## 📁 Struttura del Progetto

```
prenotazioni/
├── src/
│   └── main/
│       ├── java/com/example/prenotazioni/
│       │   ├── config/
│       │   ├── controller/
│       │   │   ├── AdminController.java
│       │   │   └── HomeController.java
│       │   ├── model/
│       │   │   ├── Admin.java
│       │   │   ├── Aereo.java
│       │   │   ├── Aeroporto.java
│       │   │   └── Volo.java
│       │   ├── repository/
│       │   │   ├── AdminRepository.java
│       │   │   ├── AereoRepository.java
│       │   │   ├── AeroportoRepository.java
│       │   │   └── VoloRepository.java
│       │   ├── service/
│       │   │   ├── AdminService.java
│       │   │   └── FlightService.java
│       │   ├── PrenotazioniApplication.java
│       │   └── ServletInitializer.java
│       ├── resources/
│       │   ├── static/css/
│       │   │   └── styles.css
│       │   ├── templates/
│       │   │   ├── admin/
│       │   │   │   ├── dashboard.html
│       │   │   │   ├── login.html
│       │   │   │   ├── modificaVoloForm.html
│       │   │   │   ├── selectVoloById.html
│       │   │   │   ├── selectVoloToDelete.html
│       │   │   │   ├── voliList.html
│       │   │   │   └── voloForm.html
│       │   │   ├── home.html
│       │   │   ├── NoFlights.html
│       │   │   └── searchResults.html
│       │   └── application.properties
```

---

## ⚙️ Funzionalità Principali

### Utente
- Ricerca voli tra città selezionate
- Visualizzazione risultati disponibili
- Verifica posti e bagagli rimanenti

### Amministratore
- Login con username/password
- Inserimento nuovo volo (con validazioni su date/orari)
- Modifica tipo di aereo per un volo esistente
- Cancellazione volo tramite ID
- Visualizzazione report voli futuri
- Gestione flash message (successo/errore)

---

## 🛢️ Configurazione `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nomedb
spring.datasource.username=utente
spring.datasource.password=********
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.thymeleaf.cache=false
```

---

## ▶️ Avvio Applicazione

```bash
mvn spring-boot:run
```

- 🌍 Frontend pubblico: http://localhost:8080/
- 🔐 Login admin: http://localhost:8080/admin/login

---

## 🗃️ Dipendenze principali
- Spring Boot Starter Web
- Spring Boot Starter Thymeleaf
- Spring Boot Starter Data JPA
- MySQL Driver
- Lombok (per @Data, @NoArgsConstructor, ecc.)

---

## 🔐 Note Sicurezza
- Login semplificato senza Spring Security
- Gestione sessione manuale via `HttpSession`


