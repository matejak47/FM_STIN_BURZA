package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyData {

    private String date;    // Datum
    private double open;    // Otevírací cena
    private double high;    // Nejvyšší cena
    private double low;     // Nejnižší cena
    private double close;   // Zavírací cena
    private long volume;    // Objem

}
