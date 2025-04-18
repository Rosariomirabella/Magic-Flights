package com.example.prenotazioni.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.prenotazioni.model.Aeroporto;

/**
 * Repository per la gestione dell'entità {@link Aeroporto}.
 * <p>
 * Estende {@link JpaRepository} e fornisce metodi CRUD (Create, Read, Update, Delete)
 * e query dinamiche tramite Spring Data JPA.
 * </p>
 *
 * <p>
 * È possibile definire metodi personalizzati sfruttando la convenzione dei nomi
 * o tramite annotazioni {@code @Query}.
 * </p>
 *
 * <h3>Metodi personalizzati:</h3>
 * <ul>
 *   <li>{@code boolean existsByCitta(String citta)} — Verifica l'esistenza di un aeroporto per città.</li>
 * </ul>
 *
 * @author Rosario Mirabella
 * @since 1.0
 */
public interface AeroportoRepository extends JpaRepository<Aeroporto, Integer> {
    
    /**
     * Verifica se esiste almeno un aeroporto registrato con la città specificata.
     *
     * @param citta il nome della città da verificare
     * @return {@code true} se esiste un aeroporto associato alla città specificata, altrimenti {@code false}
     */
    boolean existsByCitta(String citta);
}
