package com.example.prenotazioni.repository;
import com.example.prenotazioni.model.Volo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository per l'entità {@link Volo}, che consente l'accesso ai dati relativi ai voli.
 * Estende {@link JpaRepository} per fornire operazioni CRUD e metodi personalizzati.
 */

@Repository
public interface VoloRepository extends JpaRepository<Volo, Integer> {

     /**
     * Restituisce tutti i voli a partire dalla data indicata, ordinati in ordine crescente per giorno.
     *
     * @param oggi La data di riferimento (tipicamente la data corrente)
     * @return Lista di voli dal giorno indicato in poi ordinati per giorno
     */
     List<Volo> findByGiornoGreaterThanEqualOrderByGiornoAsc(LocalDate oggi);

     /**
     * Restituisce tutti i voli disponibili che soddisfano i seguenti criteri:
     * <ul>
     *     <li>La città di partenza corrisponde a {@code cittaPartenza}</li>
     *     <li>La città di arrivo corrisponde a {@code cittaArrivo}</li>
     *     <li>La data del volo è maggiore o uguale a {@code dataPartenza}</li>
     *     <li>Ci sono ancora posti disponibili</li>
     *     <li>Il peso residuo disponibile nel vano merci è sufficiente per trasportare {@code pesoBagaglio}</li>
     * </ul>
     *
     * @param cittaPartenza città di partenza desiderata
     * @param cittaArrivo città di arrivo desiderata
     * @param dataPartenza data minima del volo
     * @param pesoBagaglio peso in kg del bagaglio da stiva richiesto
     * @return lista di voli disponibili secondo i criteri indicati
     */

     @Query("SELECT v FROM Volo v " +
       "WHERE v.cittaPartenza = :cittaPartenza " +
       "AND v.cittaArrivo = :cittaArrivo " +
       "AND v.giorno >= :dataPartenza " +
       "AND (v.tipoAereo.numPass - v.passeggeri) > 0 " +
       "AND (v.tipoAereo.qtaMerci - v.merci) >= :pesoBagaglio")

     List<Volo> searchAvailableFlightsNative(
          @Param("cittaPartenza") String cittaPartenza,
          @Param("cittaArrivo") String cittaArrivo,
          @Param("dataPartenza") LocalDate dataPartenza,
          @Param("pesoBagaglio") Integer pesoBagaglio
     );
}


