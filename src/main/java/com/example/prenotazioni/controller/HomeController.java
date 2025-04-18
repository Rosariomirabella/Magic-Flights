package com.example.prenotazioni.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.prenotazioni.model.Volo;
import com.example.prenotazioni.service.FlightService;

@Controller
public class HomeController {

    @Autowired
    private FlightService flightService;
    
    /**
     * Mostra la home page con il form per la ricerca dei voli.
     */
    @GetMapping("/")
    public String home(Model model) {
        //mostriamo semplicemente il form di ricerca.
        return "home";  // visualizza il template home.html
    }
    
    /**
     * Elabora la ricerca dei voli.
     *
     * Riceve i dati inseriti dall'utente:
     * - cittaPartenza: la città da cui parte il volo
     * - cittaArrivo: la città di destinazione
     * - dataPartenza: la data minima del volo (può essere anche una data futura)
     * - pesoBagaglio: il peso del bagaglio a stiva che l'utente intende trasportare
     *
     * Invoca il servizio per cercare i voli disponibili e, in base al risultato:
     * - Se non ci sono voli compatibili, aggiunge un messaggio informativo al model e restituisce la vista "noFlights"
     * - Se vengono trovati voli, li aggiunge al model e restituisce la vista "searchResults"
     */

     // Mappa le richieste POST all'URL "/search" a questo metodo
    @PostMapping("/search")
    public String searchFlights(
            @RequestParam("cittaPartenza") String cittaPartenza,
            @RequestParam("cittaArrivo") String cittaArrivo,
            @RequestParam("dataPartenza") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPartenza,
            @RequestParam("pesoBagaglio") Integer pesoBagaglio,
            Model model) {
        
        // Chiama il servizio per cercare voli disponibili in base ai criteri dell'utente
        List<Volo> voli = flightService.searchAvailableFlights(cittaPartenza, cittaArrivo, dataPartenza, pesoBagaglio);

        // aggiungo anche le città al model per passarle al template
        model.addAttribute("cittaPartenza", cittaPartenza);
        model.addAttribute("cittaArrivo", cittaArrivo);
        
        // Se non sono stati trovati voli disponibili aggiungo al model il msg d'errore
        if (voli.isEmpty()) {
            model.addAttribute("message", "Non ci sono voli disponibili per i criteri ricercati. Riprova con altri parametri.");
            return "noFlights";  // template che mostra il messaggio e un tasto per tornare alla home
        }
        
        // voli non è empty, aggiungo la lista al model 
        model.addAttribute("voli", voli);
        return "searchResults";  // template che visualizza l'elenco dei voli disponibili
    }
}
