// Package in cui si trova la classe
package com.example.prenotazioni.model;

// Importazioni di annotazioni per la persistenza (JPA) e la validazione
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Lombok: automatizza la creazione di getter, setter, costruttori, ecc.
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Genera automaticamente getter, setter, equals, hashCode e toString
@Data

// Genera un costruttore senza argomenti
@NoArgsConstructor

// Genera un costruttore con tutti gli argomenti
@AllArgsConstructor

// Indica che questa classe è una entità JPA (mappata su una tabella DB)
@Entity

// Specifica il nome della tabella a cui è associata
@Table(name = "AEROPORTI")
public class Aeroporto {

    // Identifico la chiave primaria con auto-increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AEROPORTO")
    private Integer idAeroporto;

    // Città in cui si trova l'aeroporto
    @Column(name = "CITTA", length = 30)
    private String citta;

    // Nazione in cui si trova l'aeroporto
    @Column(name = "NAZIONE", length = 30)
    private String nazione;

    // Numero di piste che ha l'aeroporto
    @Column(name = "NUM_PISTE")
    private Integer numPiste;
}
