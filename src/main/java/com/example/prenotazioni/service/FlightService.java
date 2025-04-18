package com.example.prenotazioni.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.prenotazioni.model.Volo;
import com.example.prenotazioni.repository.VoloRepository;

@Service
public class FlightService {

    @Autowired
    private VoloRepository voloRepository;

    /**
     * Cerca i voli disponibili a partire dalla data fornita, 
     * per un aeroporto di partenza e un aeroporto di arrivo specificati, 
     * e verificando la disponibilità di posti e capacità per il bagaglio.
     * 
     * Il metodo utilizza il repository per eseguire una query nativa che:
     *   - Seleziona i voli con data >= dataPartenza;
     *   - Filtra per città di partenza e città di arrivo;
     *   - Verifica che il numero attuale di passeggeri sia inferiore al massimo consentito;
     *   - Controlla che il carico merci attuale, sommato al peso del bagaglio richiesto, 
     *     non superi il massimo consentito.
     * 
     * Inoltre, il metodo rimuove dalla lista i voli che non hanno posti disponibili 
     * o che hanno capacità di bagaglio insufficiente per il peso richiesto.
     * 
     * @param cittaPartenza  La città di partenza
     * @param cittaArrivo    La città di arrivo
     * @param dataPartenza   La data minima di partenza (inclusi voli futuri)
     * @param pesoBagaglio   Il peso del bagaglio a stiva richiesto dall'utente
     * @return Una lista di voli che soddisfano i criteri di disponibilità.
     */

    public List<Volo> searchAvailableFlights(String cittaPartenza, 
                                            String cittaArrivo,
                                            LocalDate dataPartenza, 
                                            Integer pesoBagaglio) {
        
        // Eseguo la ricerca dei voli disponibili dal repository                                        
        List<Volo> voli = voloRepository.searchAvailableFlightsNative(
                cittaPartenza, cittaArrivo, dataPartenza, pesoBagaglio
        );

        // Rimuovo dalla lista voli i voli che non hanno posti o bagagli disponibili
        for (Volo volo : voli) {
            if(volo.getPostiDisponibili() <= 0 && volo.getBagagliDisponibili() <= 0) {
                voli.remove(volo);
            }
        }
        
        return voli;
    }
}
