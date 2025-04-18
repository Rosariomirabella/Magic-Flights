package com.example.prenotazioni.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.prenotazioni.model.Aereo;


/**
 * Repository per la gestione dell'entità {@link Aereo}.
 * <p>
 * Estende {@link JpaRepository} per fornire metodi CRUD e query dinamiche
 * tramite Spring Data JPA. Le operazioni predefinite includono salvataggio,
 * aggiornamento, cancellazione e ricerca per chiave primaria.
 * </p>
 *
 * <p>
 * È possibile definire query personalizzate aggiungendo metodi con firma specifica.
 * </p>
 *
 * @author [Rosario Mirabella]
 * @since 1.0
 */

public interface AereoRepository extends JpaRepository<Aereo, String> {
    // Query custom se servono
}

