package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.model.enums.Categoria;
import br.com.alura.screenmatch.repository.Repository;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=53fd5cbd";
    private String json;
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private List<Serie> series = new ArrayList<>();
    private Repository repository;

    public Principal(Repository repository) {
        this.repository = repository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0) {
            var menu = """
                     1- Buscar séries
                     2- Buscar episódios
                     3- Listar séries buscadas
                     4- Buscar série por título
                     5- Buscar séries por ator
                     6- Buscar Top5 series
                     7- Buscar séries por categoria
                     8- Buscar séries por total de temporada
                     9- Buscar episódios a partir de um trecho
                     10- Buscar Top5 episódios
                     11- Buscando episódios a partir de uma data
                     
                     0- Sair
                    """;
            System.out.println(menu);
            var opcaoBusca = leitura.nextInt();
            leitura.nextLine();

            switch (opcaoBusca) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    listarEpisodiosBuscado();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePeloTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    buscarSeriePeloTotalDeTemporada();
                    break;
                case 9:
                    buscarEpisodiosPeloTrecho();
                    break;
                case 10:
                    buscarTop5Episodios();
                    break;
                case 11:
                    buscarEpisodiosPelaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    opcao = 0;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serieParaSalvar = new Serie(dados);

        List<Episodio> episodios;

        for (int i = 1; i <= serieParaSalvar.getTotalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + serieParaSalvar.getTitulo()
                    .replace(" ", "+") + "&season=" + i + API_KEY);
            System.out.println(json);
            var dadosTemporada = conversor.obterDados(json, DadosTemporada.class);

            episodios = dadosTemporada.episodios().stream()
                    .map(e -> new Episodio(dadosTemporada.numeroTemp(), e))
                    .collect(Collectors.toList());
            serieParaSalvar.setEpisodios(episodios);
            repository.save(serieParaSalvar);
        }
        System.out.println(serieParaSalvar);
    }

    private DadosSerie getDadosSerie() {
        System.out.print("Digite o nome da série para busca: ");
        var serieEscolhida = leitura.nextLine();

        var json = consumo.obterDados(ENDERECO + serieEscolhida.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void listarEpisodiosBuscado() {
        listarSeriesBuscadas();
        System.out.print("Digite o nome da série para busca: ");
        var serieBuscada = leitura.nextLine();

        repository.buscarSeriePeloTitulo(serieBuscada).get().getEpisodios().forEach(System.out::println);
    }

    private void listarSeriesBuscadas() {

        series = repository.findAll().stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .collect(Collectors.toList());
        series.forEach(System.out::println);
    }

    private void buscarSeriePeloTitulo() {
        System.out.print("Escolha uma série pelo nome: ");
        var serieEscolhida = leitura.nextLine();

        var serieBuscada = repository.buscarSeriePeloTitulo(serieEscolhida);

        if (serieBuscada.isPresent()) {
            System.out.println("Dados da série: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.print("Escreva o nome do ator para busca: ");
        var nomeAtor = leitura.nextLine();
        System.out.print("A partir de qual avaliação você quer pesquisar as séries? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

        var serieBuscada = repository.buscarSeriepeloAtor(nomeAtor, avaliacao);

        String nomeAtorEscolhido = serieBuscada.stream()
                .flatMap(s -> Arrays.stream(s.getAtores().split(","))
                        .filter(n -> n.toLowerCase().contains(nomeAtor.toLowerCase())))
                .findFirst().orElse("Ator não encontrado!");

        if (!serieBuscada.isEmpty()) {
            System.out.println("Dados da série em que " + nomeAtorEscolhido
                    + " trabalhou e que a avaliação é igual ou maior que " + avaliacao + ": ");
            serieBuscada.forEach(s ->
                    System.out.println("Nome " + s.getTitulo() + " Avaliação: " + s.getAvaliacao()));
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarTop5Series() {
        List<Serie> top5Series = repository.buscarTop5SeriesQuery();

        System.out.println("As top 5 Série atualmente são:");
        top5Series.forEach(s ->
                System.out.println("Nome " + s.getTitulo() + " Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria() {
        System.out.print("Digite a categoria/gênero para busca: ");
        var categoriaBuscada = leitura.nextLine();

        List<Serie> seriesPorGenero = repository.buscarSeriePorGenero(Categoria.fromStringPortugues(categoriaBuscada));
        System.out.println("As séries com o genero " + seriesPorGenero.get(0).getGenero() + " são:");
        seriesPorGenero.forEach(s ->
                System.out.println("Nome " + s.getTitulo() + " Avaliação: " + s.getAvaliacao() + " Categoria: " + s.getGenero()));
    }

    private void buscarSeriePeloTotalDeTemporada() {
        System.out.print("Dgite o total de temporadas: ");
        var totalTemporadaBuscado = leitura.nextInt();
        System.out.print("Qual o valor da avaliação? ");
        var totalAvaliacao = leitura.nextDouble();

        List<Serie> seriesPorTotalTemporada = repository.buscarSeriePorTotalDeTemporada(totalTemporadaBuscado, totalAvaliacao);
        System.out.println("As séries com total de temporadas " + totalTemporadaBuscado
                + " e com avaliação maior ou igual a " + totalAvaliacao + " são:");
        seriesPorTotalTemporada.stream()
                .sorted(Comparator.comparing(Serie::getAvaliacao).reversed())
                .forEach(s ->
                System.out.println("Nome " + s.getTitulo() + " Avaliação: " + s.getAvaliacao() + " Total temporada: " + s.getTotalTemporadas()));
    }

    private void buscarEpisodiosPeloTrecho() {
        System.out.println("Escreva o nome da série: ");
        var nomeSerie = leitura.nextLine();
        System.out.print("Escreva o nome ou um trecho do episódio para busca: ");
        var nomeOuTrecho = leitura.nextLine();

        List<Episodio> episodiosBuscadosPorNome = repository.buscarEpisodioPorTrecho(nomeSerie, nomeOuTrecho);
        System.out.println("Episódios encontrados: ");
        episodiosBuscadosPorNome.forEach(e ->
                System.out.println("Nome " + e.getTitulo() + " Temporada: " + e.getTemporada() + " Avaliação: " + e.getAvaliacao()));
    }

    private void buscarTop5Episodios() {
        System.out.println("Escreva o nome da série: ");
        var nomeSerie = leitura.nextLine();

        List<Episodio> top5Episodios = repository.buscarTop5EpisodiosQuery(nomeSerie);
        top5Episodios.forEach(e ->
                System.out.println("Nome " + e.getTitulo() + " Episódio: " + e.getNumeroEpisodio() + " Temporada: " + e.getTemporada() + " Avaliação: " + e.getAvaliacao()));
    }

    private void buscarEpisodiosPelaData() {
        System.out.println("Escreva o nome da série: ");
        var nomeSerie = leitura.nextLine();
        System.out.println("Qual o ano para busca?");
        var data = leitura.nextLine();

        List<Episodio> episodiosPorData = repository.buscarEpisodiosPorData(nomeSerie, data);
        episodiosPorData.forEach(e ->
                System.out.println("Nome " + e.getTitulo() + " Episódio: " + e.getNumeroEpisodio() + " Temporada: " + e.getTemporada() + " Avaliação: " + e.getAvaliacao() + " Data: " + e.getDataLancamento()));
    }
}
