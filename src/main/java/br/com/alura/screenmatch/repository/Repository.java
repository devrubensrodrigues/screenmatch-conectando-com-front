package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.model.enums.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface Repository extends JpaRepository<Serie, Long> {

    @Query("SELECT s FROM Serie s WHERE s.titulo ILIKE %:titulo%")
    Optional<Serie> buscarSeriePeloTitulo(String titulo);

    @Query("SELECT s FROM Serie s WHERE s.atores ILIKE %:nomeAtor% AND s.avaliacao >= :avaliacao")
    List<Serie> buscarSeriepeloAtor(String nomeAtor, Double avaliacao);

    @Query("SELECT s FROM Serie s ORDER BY s.avaliacao DESC LIMIT 5")
    List<Serie> buscarTop5SeriesQuery();

    @Query("SELECT s FROM Serie s WHERE s.genero = :categoriaBuscada")
    List<Serie> buscarSeriePorGenero(Categoria categoriaBuscada);

    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadaBuscado AND s.avaliacao >= :totalAvaliacao")
    List<Serie> buscarSeriePorTotalDeTemporada(int totalTemporadaBuscado, double totalAvaliacao);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.titulo ILIKE %:nomeSerie% AND e.titulo ILIKE %:nomeOuTrecho%")
    List<Episodio> buscarEpisodioPorTrecho(String nomeSerie, String nomeOuTrecho);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.titulo ILIKE %:nomeSerie% ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> buscarTop5EpisodiosQuery(String nomeSerie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.titulo ILIKE %:nomeSerie% AND YEAR(e.dataLancamento) >= :data")
    List<Episodio> buscarEpisodiosPorData(String nomeSerie, String data);

    @Query("SELECT s FROM Serie s JOIN s.episodios e WHERE YEAR(e.dataLancamento) = :data GROUP BY s")
    List<Serie> buscarSeriesLancamentos(Integer data);

}
