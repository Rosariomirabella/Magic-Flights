// Package in cui si trova la classe
package com.example.prenotazioni.model;

// Importazione delle classi Java per la gestione di date e orari
import java.time.LocalDate;
import java.time.LocalDateTime;

// Importazioni di annotazioni per la persistenza (JPA) e la validazione
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

// Lombok: automatizza la creazione di getter, setter, costruttori, ecc.
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Genera automaticamente getters, setter, equals, hashCode e toString
@Data

// Genera un costruttore senza argomenti
@NoArgsConstructor

// Genera un costruttore con tutti gli argomenti
@AllArgsConstructor

// Indica che questa classe è una entità JPA (mappata su una tabella DB)
@Entity

// Specifica il nome della tabella a cui è associata
@Table(name = "VOLI")
public class Volo {

    // Identifico la chiave primaria con auto-increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VOLO")
    private Integer idVolo;

    // Data del volo (solo giorno, senza ora)
    @Column(name = "GIORNO")
    private LocalDate giorno;

    // Nome città partenza
    @Column(name = "CITTA_PARTENZA", length = 50)
    private String cittaPartenza;

    // Orario preciso di partenza
    @Column(name = "ORA_PARTENZA")
    private LocalDateTime oraPartenza;

    // Nome città arrivo
    @Column(name = "CITTA_ARRIVO", length = 50)
    private String cittaArrivo;

    // Orario preciso di arrivo
    @Column(name = "ORA_ARRIVO")
    private LocalDateTime oraArrivo;

    // Relazione verso Aereo: il campo TIPO_AEREO in VOLI si riferisce alla chiave primaria di AEREO.
    @ManyToOne
    @JoinColumn(name = "TIPO_AEREO", referencedColumnName = "TIPO_AEREO")
    private Aereo tipoAereo;

    // Numero di passeggeri attualmente prenotati
    @Column(name = "PASSEGGERI")
    private Integer passeggeri;

    // Quantità di merce/bagagli in kg attualmente prenotati
    @Column(name = "MERCI")
    private Integer merci;

    // Il transient indica che il metodo non è persistente, cioè non esiste come colonna nel DB
    @Transient
    public int getPostiDisponibili() {
        return tipoAereo.getNumPass() - passeggeri;
    }
   
    @Transient
    public int getBagagliDisponibili() {
        return tipoAereo.getQtaMerci() - merci;
    }
}

