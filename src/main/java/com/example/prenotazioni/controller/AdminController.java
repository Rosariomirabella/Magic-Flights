package com.example.prenotazioni.controller;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;

import com.example.prenotazioni.repository.AdminRepository;
import com.example.prenotazioni.repository.AereoRepository;
import com.example.prenotazioni.service.AdminService;

import com.example.prenotazioni.model.Admin;
import com.example.prenotazioni.model.Aereo;
import com.example.prenotazioni.model.Volo;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione delle funzionalità amministrative dell'applicazione.
 * 
 * Gestisce:
 * - Login e autenticazione degli amministratori
 * - Visualizzazione dashboard
 * - Inserimento, modifica, cancellazione e report dei voli
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AereoRepository aereoRepository;

    /**
     * Visualizza la pagina di login per l'amministratore.
     *
     * @return nome del template della pagina di login.
     */
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    
    /**
     * Elabora la richiesta di login.
     * Verifica le credenziali dell'amministratore, e in caso di successo, imposta l'attributo in sessione.
     *
     * @param username nome utente inserito nel form
     * @param password password inserita nel form
     * @param session oggetto HttpSession per gestire la sessione utente
     * @param model oggetto Model per passare messaggi alla view
     * @return reindirizzamento alla dashboard se login corretto, altrimenti alla login con errore
     */
 
    @PostMapping("/processLogin")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {

        // Cerca un Admin nel database in base allo username. Se non lo trova, restituisce null.
        Admin admin = adminRepository.findByUsername(username).orElse(null);

        if (admin != null && admin.getPassw().equals(password)) {

            // Se l'admin esiste e la password è corretta salvo l'admin nella sessione
            session.setAttribute("loggedInAdmin", admin);

            // Reindirizza l'admin alla dashboard amministrativa
            return "redirect:/admin/dashboard";
        } else {

            // Se le credenziali non sono valide, aggiunge un messaggio di errore al model
            model.addAttribute("error", "Credenziali non valide. Riprova.");

            // Ritorna nuovamente al form di login
            return "admin/login";
        }
    }

    /**
     * Visualizza la dashboard dell'area amministrativa per l'amministratore loggato.
     * 
     * Se l'amministratore non è autenticato (nessuna sessione attiva), l'utente verrà reindirizzato alla pagina di login.
     * Se l'amministratore è autenticato, il metodo aggiunge le informazioni relative all'amministratore
     * al modello e restituisce il template della dashboard dell'amministratore.
     * 
     * @param session la sessione HTTP per verificare se l'amministratore è loggato
     * @param model il contenitore per i dati da passare al template Thymeleaf
     * @return il nome del template da visualizzare, oppure un redirect alla pagina di login se non autenticato
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
    
        if (admin == null) {
            // L'utente non è loggato: puoi reindirizzarlo alla login page o mostrare un errore
            return "redirect:/admin/login";
        }
        
        // lo aggiungo al Model per prendere lo username dell'admin
        model.addAttribute("loggedInAdmin", admin);

        // reindirizzo l'admin alla dashboard
        return "admin/dashboard";
    }



    /**
     * Visualizza il form per l'inserimento di un nuovo volo da parte dell'amministratore.
     *
     * Verifica se un amministratore è autenticato tramite la sessione. In caso affermativo:
     * - Crea un nuovo oggetto {@link Volo} per il binding con i campi del form.
     * - Imposta la data e l'orario minimo accettabili per la partenza del volo.
     * - Recupera e passa la lista dei tipi di aereo disponibili per la selezione.
     *
     * @param session la sessione HTTP per verificare se l'amministratore è loggato
     * @param model il contenitore per i dati da passare al template Thymeleaf
     * @return il nome del template da visualizzare, oppure un redirect alla pagina di login se non autenticato
     */
    @GetMapping("/nuovoVolo")
    public String nuovoVoloForm(HttpSession session, Model model) {
        // Verifica che l'admin sia loggato

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }
        
        /*Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        */
        
        // Crea un nuovo oggetto Volo per il binding con il form
        Volo nuovoVolo = new Volo();

        model.addAttribute("volo", nuovoVolo);

        // Passa la data odierna come stringa “YYYY-MM-DD”
        model.addAttribute("minDate", LocalDate.now().toString());

        // 4) Timestamp minimo per <input type="datetime-local">
        String minDateTime = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        
        // Aggiunge il timestamp minimo al model per vincolare la data/ora nel form
        model.addAttribute("minDateTime", minDateTime);

       // Recupera la lista di tutti gli aerei presenti nel DB e li aggiunge al model
        model.addAttribute("listaAerei", aereoRepository.findAll());
        
        // Restituisce il template per il form di inserimento del nuovo volo
        return "admin/voloForm";
    }

    /**
     * Elabora la richiesta di salvataggio di un nuovo volo.
     *
     * Verifica se l'amministratore è autenticato; in caso contrario, reindirizza alla pagina di login.
     * Riceve i dati del volo dal form tramite binding con l'oggetto {@link Volo}, insieme al codice del tipo di aereo selezionato.
     * 
     * Recupera dal database l'entità {@link Aereo} corrispondente al codice selezionato e la associa al volo.
     * Imposta i campi iniziali delle prenotazioni (passeggeri e merci) a 0.
     * 
     * Tenta di salvare il volo tramite {@link AdminService}. In caso di successo, reindirizza alla dashboard
     * con un messaggio di conferma; in caso di errore (es. dati non validi), cattura l'eccezione e reindirizza
     * nuovamente al form con un messaggio di errore.
     * 
     * @param volo l'oggetto Volo popolato dai dati del form
     * @param tipoAereoCodice il codice identificativo del tipo di aereo selezionato
     * @param session la sessione HTTP per verificare l'autenticazione dell'amministratore
     * @param model il model per passare attributi alla vista (non utilizzato in questa versione)
     * @param redirectAttributes per aggiungere messaggi flash da visualizzare dopo un redirect
     * @return il redirect alla dashboard in caso di successo, o al form in caso di errore
     */


    @PostMapping("/volo/salva")
    public String salvaVolo(@ModelAttribute("volo") Volo volo,
                            @RequestParam("tipoAereo") String tipoAereoCodice,
                            HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        // Verifica che l'admin sia loggato

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        /*Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        } */
        
        // Recupera l'entità Aereo in base al codice selezionato
        Aereo aereo = aereoRepository.findById(tipoAereoCodice)
                .orElseThrow(() -> new RuntimeException("Tipo aereo non trovato: " + tipoAereoCodice));
        volo.setTipoAereo(aereo);
        
        // Imposta i campi di prenotazione a 0
        volo.setPasseggeri(0);
        volo.setMerci(0);

        try {
            // Salvo il nuovo volo tramite il service
            adminService.createFlight(volo);
            redirectAttributes.addFlashAttribute("successMessage", "Volo inserito correttamente!");
            return "redirect:/admin/dashboard";

        } catch (IllegalArgumentException e) {
            // In caso di errore (ad esempio, città non validi), cattura l'eccezione
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            // Puoi reindirizzare nuovamente al form per correggere l'errore
            return "redirect:/admin/nuovoVolo";
        }
    }
        
        // Salva il nuovo volo tramite il service
        //adminService.createFlight(volo);

        //redirectAttributes.addFlashAttribute("successMessage", "Volo inserito correttamente!");

        //return "redirect:/admin/dashboard";

    /**
     * Mostra una pagina dove l'amministratore può inserire l'ID del volo da modificare.
     * <p>
     * Verifica prima che l'amministratore sia autenticato controllando la sessione;
     * in caso contrario, reindirizza alla pagina di login.
     *
     * @param session la sessione HTTP usata per verificare se l'amministratore è loggato
     * @param model   l'oggetto Model (non usato qui, ma disponibile per eventuali attributi)
     * @return il nome del template Thymeleaf da mostrare, oppure redirect alla login
     */
    @GetMapping("/modificaVolo")
    public String selectFlightToModify(HttpSession session, Model model) {

        // Verifica che l'admin sia loggato
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }
    

        // Se è loggato, mostra la pagina che chiede di inserire l'ID del volo da modificare
        return "admin/selectVoloById";
    }

    /**
     * Recupera i dettagli di un volo specifico a partire dal suo ID e mostra un form per modificarne i dati.
     * <p>
     * Prima controlla che l'amministratore sia autenticato. Se non lo è, reindirizza alla pagina di login.
     * Se il volo viene trovato, lo aggiunge al model insieme alla lista degli aerei disponibili,
     * e mostra il template di modifica. In caso di errore (es. ID non valido), mostra un messaggio e reindirizza.
     *
     * @param idVolo              l'ID del volo da modificare, ricevuto dal form
     * @param session             la sessione HTTP per verificare l'autenticazione
     * @param model               il model per passare oggetti al template
     * @param redirectAttributes  per inviare messaggi flash in caso di errore
     * @return il nome del template per la modifica del volo oppure un redirect
     */
    @PostMapping("/modificaVolo")
    public String loadVoloToModify(@RequestParam("idVolo") Integer idVolo,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        try {
            Volo volo = adminService.getFlightById(idVolo);
            model.addAttribute("volo", volo);
            model.addAttribute("listaAerei", aereoRepository.findAll());
            return "admin/modificaVoloForm";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/modificaVolo";
        }

    }

    /**
     * Aggiorna il tipo di aereo associato a un volo esistente.
     * <p>
     * Verifica che l'amministratore sia autenticato prima di procedere.
     * In caso di successo, aggiunge un messaggio di conferma da mostrare all'utente.
     * In caso di errore (es. volo o aereo non trovati), mostra un messaggio di errore.
     *
     * @param idVolo              l'ID del volo da aggiornare
     * @param tipoAereo           il codice del nuovo tipo di aereo da associare
     * @param session             la sessione HTTP per il controllo autenticazione
     * @param redirectAttributes  oggetto per passare messaggi temporanei (flash) dopo il redirect
     * @return un redirect alla pagina per la modifica del volo
     */

    @PostMapping("/volo/aggiorna")
    public String aggiornaVolo(@RequestParam Integer idVolo,
                               @RequestParam String tipoAereo,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        try {
            // Chiamo il metodo del service per aggiornare il tipo di aereo associato al volo
            adminService.updateFlightAircraft(idVolo, tipoAereo);

            //se ok aggiungo unm messaggio di ok come flash attribute
            redirectAttributes.addFlashAttribute("successMessage", "Tipo aereo aggiornato con successo!");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Reindirizza alla pagina di modifica volo dove si può riprovare o verificare l'esito
        return "redirect:/admin/modificaVolo";
    }

    

    /**
     * Mostra il form per inserire l'ID del volo da cancellare.
     * <p>
     * Verifica che l'amministratore sia autenticato prima di accedere alla pagina.
     * Se non lo è, reindirizza alla pagina di login.
     *
     * @param session la sessione HTTP per controllare se l'admin è autenticato
     * @param model   il model usato per passare dati al template (attualmente non utilizzato)
     * @return il nome del template per selezionare il volo da cancellare oppure un redirect alla login
     */

    @GetMapping("/cancellaVolo")
    public String selectVoloToDeleteForm(HttpSession session,
                                         Model model) {
        // Controllo autenticazione
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        // Se l'admin è loggato, restituisce il template per inserire l'ID del volo da cancellare
        return "admin/selectVoloToDelete";
    }

    /**
     * Elabora la cancellazione di un volo dato il suo ID.
     * <p>
     * Il metodo verifica che l'amministratore sia autenticato. In caso contrario, reindirizza alla pagina di login.
     * Se l'autenticazione è valida, prova a cancellare il volo tramite il servizio.
     * In caso di successo o errore, viene mostrato un messaggio di feedback nella pagina di cancellazione.
     *
     * @param idVolo l'ID del volo da cancellare
     * @param session la sessione HTTP per verificare l'autenticazione dell'admin
     * @param redirectAttributes oggetto usato per passare messaggi temporanei alla view dopo il redirect
     * @return il redirect alla pagina per cancellare un volo o alla login se non autenticato
     */

    @PostMapping("/cancellaVolo")
    public String deleteVolo(@RequestParam("idVolo") Integer idVolo,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        // Controllo autenticazione
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        try {
            adminService.deleteFlight(idVolo);

            // Se la cancellazione ha successo, aggiunge un messaggio flash di conferma
            redirectAttributes.addFlashAttribute("successMessage", 
                "Volo con ID " + idVolo + " cancellato con successo!");

        } catch (IllegalArgumentException e) {
            // Se qualcosa va storto (es. ID non esistente), cattura l'eccezione e aggiunge un messaggio di errore
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Reindirizza di nuovo alla pagina per cancellare un volo
        return "redirect:/admin/cancellaVolo";
    }

    /**
     * Mostra il report dei voli futuri (a partire da oggi).
     * <p>
     * Questo metodo verifica che l'amministratore sia autenticato. Se l'autenticazione fallisce,
     * reindirizza alla pagina di login. Altrimenti, recupera l'elenco dei voli con data di partenza
     * uguale o successiva a quella odierna e lo passa al template per la visualizzazione.
     *
     * @param session la sessione HTTP per verificare l'autenticazione dell'amministratore
     * @param model il model usato per passare i dati al template
     * @return il nome del template che mostra la lista dei voli futuri o il redirect alla login
     */


    @GetMapping("/report")
    public String reportVoli(HttpSession session, Model model) {
        
        //  Controllo autenticazione
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/login";
        }

        // Recupera i voli futuri a partire da oggi
        List<Volo> voli = adminService.getFutureFlights(LocalDate.now());

        //  Passa la lista al template
        model.addAttribute("voli", voli);

        // Ritorna il nome del template 
        return "admin/voliList";
    }

    /**
     * Esegue il logout dell'amministratore invalidando la sessione corrente.
     * <p>
     * Questo metodo rimuove tutte le informazioni legate alla sessione utente,
     * inclusi gli attributi utilizzati per verificare l'autenticazione dell'amministratore.
     * Dopo la disconnessione, l'utente viene reindirizzato alla home page pubblica.
     *
     * @param session la sessione HTTP da invalidare
     * @return il redirect verso la home page pubblica ("/")
     */

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        // Invalida la sessione 
        session.invalidate();

        // Redirect alla home page ("/")
        return "redirect:/";
    }
}




    
    

