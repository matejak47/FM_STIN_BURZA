package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class StockResponseGrancek {
    private String name;
    private Date from;
    private Date to;
}