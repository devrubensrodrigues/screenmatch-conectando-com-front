package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.enums.Categoria;

public record SerieDTO(Long id,
                       String titulo,
                       Integer totalTemporadas,
                       Double avaliacao,
                       String atores,
                       String sinopse,
                       Categoria genero,
                       String poster) {
}
