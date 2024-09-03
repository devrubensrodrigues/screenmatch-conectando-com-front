package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SerieService {
    private Integer anoAtual = LocalDate.now().getYear();
    @Autowired
    private Repository repository;

    public List<SerieDTO> obterTodasAsSeries() {
        return converteDados(repository.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados(repository.buscarTop5SeriesQuery());
    }

    public List<SerieDTO> obterLancamentosDTO() {
        return converteDados(repository.buscarSeriesLancamentos(anoAtual));
        /*repository.findAll().stream()
                .filter(s -> s.getEpisodios().stream()
                        .filter(e -> e.getDate() != null)
                        .anyMatch(e -> e.getDate().getYear() == LocalDate.now().getYear()))
                .collect(Collectors.toList())*/
    }

    public Serie obterSeriePeloId(String id) {
        return repository.buscarSeriePeloId(id);
    }

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getAtores(), s.getSinopse(), s.getGenero(), s.getPoster()))
                .collect(Collectors.toList());
    }
}
