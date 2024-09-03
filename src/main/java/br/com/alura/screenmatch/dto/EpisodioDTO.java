package br.com.alura.screenmatch.dto;

import java.time.LocalDate;

public record EpisodioDTO(Integer season,
                          String title,
                          Integer numberEp,
                          Double assessment,
                          LocalDate date) {
}
