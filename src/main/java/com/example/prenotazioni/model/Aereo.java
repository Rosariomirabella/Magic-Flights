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
@Table(name = "AEREO")
public class Aereo {

    @Id
    @Column(name = "TIPO_AEREO", length = 5)
    private String tipoAereo;

    @Column(name = "NUM_PASS")
    private Integer numPass;

    @Column(name = "QTA_MERCI")
    private Integer qtaMerci;
}

