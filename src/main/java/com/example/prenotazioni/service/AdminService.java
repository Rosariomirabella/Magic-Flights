package com.example.prenotazioni.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.prenotazioni.model.Aereo;
import com.example.prenotazioni.model.Volo;
import com.example.prenotazioni.repository.AereoRepository;
import com.example.prenotazioni.repository.AeroportoRepository;
import com.example.prenotazioni.repository.VoloRepository;

@Service
public class AdminService {

    @Autowired
    private VoloRepository voloRepository;
    
    @Autowired
    private AereoRepository aereoRepository;

    @Autowired
    private AeroportoRepository aeroportoRepository;

   /**
     * Inserisce un nuovo volo nel database dopo aver effettuato le seguenti verifiche:
     * <ul>
     *   <li>La città di partenza e di arrivo devono corrispondere a un aeroporto esistente.</li>
     *   <li>La data del volo non può essere nel passato.</li>
     *   <li>L'orario di partenza non può essere nel passato.</li>
     *   <li>L'orario di arrivo deve essere successivo all'orario di partenza.</li>
     * </ul>
     * Se tutte le verifiche sono superate, il volo viene salvato nel database.
     *
     * @param volo L'oggetto Volo da inserire nel sistema.
     * @return Il volo appena salvato nel database.
     * @throws IllegalArgumentException Se la città di partenza o arrivo non esiste, 
     *                                  se la data del volo è nel passato, 
     *                                  o se l'orario di partenza è nel passato 
     *                                  o l'orario di arrivo è prima dell'orario di partenza.
     */
    public Volo createFlight(Volo volo) {
        // Verifica che la città di partenza corrisponda a un aeroporto esistente
        if (!aeroportoRepository.existsByCitta(volo.getCittaPartenza())  || !aeroportoRepository.existsByCitta(volo.getCittaArrivo())) {
           throw new IllegalArgumentException("Città di partenza o arrivo non valida!");
       }
    
       // prendo la data odierna senza includere l'ora (solo giorno, mese, anno).
       LocalDate oggi = LocalDate.now();

       // Se la data del volo è prima di oggi viene lanciata un'eccezione di tipo IllegalArgumentException
       if (volo.getGiorno().isBefore(oggi)) {
           throw new IllegalArgumentException(
               "La data del volo non può essere precedente a " + oggi);
       }

        // - Verifico che l'orario di partenza non sia nel passato - 

        // prendo la data e l'ora correnti. 
        LocalDateTime now = LocalDateTime.now();

        //verifico se l'ora di partenza è precedente all'ora attuale. Se sì lancio l'eccezione
        if (volo.getOraPartenza().isBefore(now)) {
            throw new IllegalArgumentException(
                "L'orario di partenza non può essere precedente ad ora: " +
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

        // - Verifico che l'orario di arrivo non sia precdente all'ora di partenza -
        // in caso lancio l'eccezione
        if (!volo.getOraArrivo().isAfter(volo.getOraPartenza())) {
            throw new IllegalArgumentException(
                "L'orario di arrivo (" +
                volo.getOraArrivo().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                ") deve essere successivo a quello di partenza (" +
                volo.getOraPartenza().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                ").");
        }

        // Imposto i campi di prenot. e merci a 0 perchè un nuovo volo non ha passeggeri e bagagli
        volo.setPasseggeri(0);
        volo.setMerci(0);
        
        // Salva il volo
        return voloRepository.save(volo);
    }
    
     /**
     * Recupera un volo dato il suo ID.
     * Se il volo non esiste, viene lanciata un'eccezione.
     *
     * @param idVolo L'ID del volo da recuperare.
     * @return Il volo con l'ID specificato.
     * @throws RuntimeException Se il volo con l'ID fornito non esiste nel database.
     */
    public Volo getFlightById(Integer idVolo) {
        return voloRepository.findById(idVolo)
                .orElseThrow(() -> new RuntimeException("Volo con ID " + idVolo + " non trovato!"));
    }

       /**
     * Modifica il tipo di aereo associato a un volo esistente.
     * Viene verificato che il nuovo tipo di aereo esista nel database.
     *
     * @param idVolo L'ID del volo da aggiornare.
     * @param nuovoTipoAereo Il codice del nuovo tipo di aereo.
     * @return Il volo aggiornato con il nuovo tipo di aereo.
     * @throws RuntimeException Se il volo con l'ID fornito non esiste o se il tipo di aereo non esiste.
     */
    public Volo updateFlightAircraft(Integer idVolo, String nuovoTipoAereo) {
        Volo volo = getFlightById(idVolo);
        
        // Recupera l'entità Aereo dal repository con il findByID
        Aereo nuovoAereo = aereoRepository.findById(nuovoTipoAereo)
                .orElseThrow(() -> new RuntimeException("Aereo non trovato con tipo " + nuovoTipoAereo));

     
        // 3) Verifica passeggeri
        if (volo.getPasseggeri() > nuovoAereo.getNumPass()) {
            throw new IllegalArgumentException(
                "Impossibile cambiare tipo aereo: " +
                "ci sono già " + volo.getPasseggeri() + 
                " passeggeri, ma il nuovo aeromobile ne contiene max " +
                nuovoAereo.getNumPass());
        }

        // 4) Verifica merci
        if (volo.getMerci() > nuovoAereo.getQtaMerci()) {
            throw new IllegalArgumentException(
                "Impossibile cambiare tipo aereo: " +
                "ci sono già " + volo.getMerci() + 
                " Kg di merci, ma il nuovo aeromobile ne contiene max " +
                nuovoAereo.getQtaMerci() + " Kg");
        }

        // setto il nuovo tipo e ritorno l'oggetto aggiornato
        volo.setTipoAereo(nuovoAereo);
        return voloRepository.save(volo);
    }
    
     /**
     * Cancella un volo dal database dato il suo ID.
     * Se il volo non esiste, viene lanciata un'eccezione.
     *
     * @param idVolo L'ID del volo da cancellare.
     * @throws IllegalArgumentException Se il volo con l'ID fornito non esiste nel database.
     */
    public void deleteFlight(Integer idVolo) {

        // Verifico l'esistenza del volo
        if (!voloRepository.existsById(idVolo)) {
            throw new IllegalArgumentException("Impossibile cancellare: il volo con ID " 
                                               + idVolo + " non esiste.");
        }
        // Se esiste lo tolgo
        voloRepository.deleteById(idVolo);
    }
    
     /**
     * Restituisce la lista dei voli futuri a partire dalla data corrente.
     * I voli sono ordinati per data in ordine ascendente.
     *
     * @param oggi La data corrente (tipicamente LocalDate.now()).
     * @return Una lista di voli con data maggiore o uguale a quella corrente, ordinata per giorno.
     */
    public List<Volo> getFutureFlights(LocalDate oggi) {
        return voloRepository.findByGiornoGreaterThanEqualOrderByGiornoAsc(oggi);
    }

    /**
     * Restituisce una lista di tutti i voli presenti nel database.
     * 
     * @return Una lista di tutti i voli.
     */
    public List<Volo> getAllFlights() {
        return voloRepository.findAll();
    }
}





