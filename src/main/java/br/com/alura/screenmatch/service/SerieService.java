package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    public SerieDTO obterSeriePeloId(Long id) {
        Optional<Serie> optionalSerie = repository.findById(id);
        if (optionalSerie.isPresent()){
            Serie s = optionalSerie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getAtores(), s.getSinopse(), s.getGenero(), s.getPoster());
        }else {
            return null;
        }
    }

    public List<EpisodioDTO> obterTemporadas(Long id) {
        Optional<Serie> optionalSerie = repository.findById(id);

        if (optionalSerie.isPresent()) {
            Serie s = optionalSerie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                    .collect(Collectors.toList());
        }else {
            return null;
        }
    }

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getAtores(), s.getSinopse(), s.getGenero(), s.getPoster()))
                .collect(Collectors.toList());
    }
}
