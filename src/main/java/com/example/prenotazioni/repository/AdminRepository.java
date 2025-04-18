// Definizione del package in cui si trova questa interfaccia
package com.example.prenotazioni.repository;

// Importazione di Optional, utile per evitare NullPointerException e gestire assenza di risultati
import java.util.Optional;

// Importazione dell'interfaccia JpaRepository fornita da Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;

// Importazione della classe Admin, che è l'entità da gestire in questo repository
import com.example.prenotazioni.model.Admin;

/**
 * Repository per l'entità {@link Admin}.
 * <p>
 * Estende {@link JpaRepository} per fornire operazioni CRUD di base,
 * oltre a metodi personalizzati di interrogazione sul database.
 * </p>
 *
 * <p>
 * Questa interfaccia utilizza Spring Data JPA per generare automaticamente
 * le implementazioni basate sulla firma dei metodi.
 * </p>
 *
 * @author [Rosario Mirabella]
 * @since 1.0
 */
public interface AdminRepository extends JpaRepository<Admin, String> {
    

    /**
     * Recupera un oggetto {@link Admin} dal database in base al suo username.
     *
     * @param username lo username dell'amministratore da cercare
     * @return un {@link Optional} contenente l'oggetto Admin se trovato, altrimenti vuoto
     */
    Optional<Admin> findByUsername(String username);
}

