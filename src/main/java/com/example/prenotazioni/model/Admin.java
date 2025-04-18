// Package in cui si trova la classe
package com.example.prenotazioni.model;

// Importazioni di annotazioni per la persistenza (JPA) e la validazione
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "UTENTI")
public class Admin {

    // Identifico la chiave primaria
    @Id
    @Column(name = "USERNAME", length = 50)
    private String username;

    // Stringa che rappresenta la password dell'admin
    @Column(name = "PASSW", length = 255, nullable = false)
    private String passw;
}
