package com.filmbase.service;


import com.filmbase.dto.MovieDTO;
import com.filmbase.dto.MoviePageResponse;
import com.filmbase.entity.Movie;
import com.filmbase.exception.FileExistsException;
import com.filmbase.exception.MovieNotFoundException;
import com.filmbase.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {


    private final MovieRepository movieRepository;


    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;


    @Override
    public MovieDTO addMovie(MovieDTO movieDTO, MultipartFile file) throws IOException {

        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File already exists! Please enter another filename");
        }

        if (movieRepository.existsByTitle(movieDTO.getTitle())) {
            throw new FileExistsException("Movie with title  " + movieDTO.getTitle() + "already exists.");
        }

        String uploadedFileName = fileService.uploadFile(path, file);

        movieDTO.setPoster(uploadedFileName);

        Movie movie = new Movie();

        movie.setTitle(movieDTO.getTitle());
        movie.setDirector(movieDTO.getDirector());
        movie.setGenre(movieDTO.getGenre());
        movie.setDescription(movieDTO.getDescription());
        movie.setLanguage(movieDTO.getLanguage());
        movie.setDuration(movieDTO.getDuration());
        movie.setStudio(movieDTO.getStudio());
        movie.setRating(movieDTO.getRating());
        movie.setMovieCast(movieDTO.getMovieCast());
        movie.setCountryOfOrigin(movieDTO.getCountryOfOrigin());
        movie.setAvailableLanguages(movieDTO.getAvailableLanguages());
        movie.setSubtitleLanguages(movieDTO.getSubtitleLanguages());
        movie.setReleaseYear(movieDTO.getReleaseYear());
        movie.setPoster(movieDTO.getPoster());
        movie.setBoxOfficeRevenue(movieDTO.getBoxOfficeRevenue());
        movie.setProductionCompany(movieDTO.getProductionCompany());
        movie.setAgeRating(movieDTO.getAgeRating());
        movie.setKeywords(movieDTO.getKeywords());
        movie.setTrailerUrl(movieDTO.getTrailerUrl());
        Movie savedMovie = movieRepository.save(movie);
        String postrUrl = baseUrl + "/file/" + uploadedFileName;


        MovieDTO response = new MovieDTO();

        response.setMovieId(savedMovie.getMovieId());
        response.setTitle(savedMovie.getTitle());
        response.setDirector(savedMovie.getDirector());
        response.setGenre(savedMovie.getGenre());
        response.setDescription(savedMovie.getDescription());
        response.setLanguage(savedMovie.getLanguage());
        response.setDuration(savedMovie.getDuration());
        response.setStudio(savedMovie.getStudio());
        response.setRating(savedMovie.getRating());
        response.setMovieCast(savedMovie.getMovieCast());
        response.setCountryOfOrigin(savedMovie.getCountryOfOrigin());
        response.setAvailableLanguages(savedMovie.getAvailableLanguages());
        response.setSubtitleLanguages(savedMovie.getSubtitleLanguages());
        response.setReleaseYear(savedMovie.getReleaseYear());
        response.setPoster(savedMovie.getPoster());
        response.setBoxOfficeRevenue(savedMovie.getBoxOfficeRevenue());
        response.setProductionCompany(savedMovie.getProductionCompany());
        response.setAgeRating(savedMovie.getAgeRating());
        response.setKeywords(savedMovie.getKeywords());
        response.setTrailerUrl(savedMovie.getTrailerUrl());
        response.setReleaseYear(savedMovie.getReleaseYear());

        response.setPosterUrl(postrUrl);


        return response;
    }

    @Override
    public MovieDTO getMovie(Long movieId) {

        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + movieId));

        String postrUrl = baseUrl + "/file/" + movie.getPoster();

        MovieDTO response = new MovieDTO();

        response.setMovieId(movie.getMovieId());
        response.setTitle(movie.getTitle());
        response.setGenre(movie.getGenre());
        response.setDescription(movie.getDescription());
        response.setLanguage(movie.getLanguage());
        response.setDuration(movie.getDuration());
        response.setStudio(movie.getStudio());
        response.setRating(movie.getRating());
        response.setMovieCast(movie.getMovieCast());
        response.setDirector(movie.getDirector());
        response.setCountryOfOrigin(movie.getCountryOfOrigin());
        response.setAvailableLanguages(movie.getAvailableLanguages());
        response.setSubtitleLanguages(movie.getSubtitleLanguages());
        response.setReleaseYear(movie.getReleaseYear());
        response.setPoster(movie.getPoster());
        response.setBoxOfficeRevenue(movie.getBoxOfficeRevenue());
        response.setProductionCompany(movie.getProductionCompany());
        response.setAgeRating(movie.getAgeRating());
        response.setKeywords(movie.getKeywords());
        response.setTrailerUrl(movie.getTrailerUrl());
        response.setReleaseYear(movie.getReleaseYear());

        response.setPosterUrl(postrUrl);


        return response;
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();

        List<MovieDTO> movieDTOList = new ArrayList<>();

        for (Movie movie : movies) {
            MovieDTO movieDTO = new MovieDTO();
            String postrUrl = baseUrl + "/file/" + movie.getPoster();

            movieDTO.setMovieId(movie.getMovieId());
            movieDTO.setTitle(movie.getTitle());
            movieDTO.setGenre(movie.getGenre());
            movieDTO.setDescription(movie.getDescription());
            movieDTO.setLanguage(movie.getLanguage());
            movieDTO.setDuration(movie.getDuration());
            movieDTO.setStudio(movie.getStudio());
            movieDTO.setRating(movie.getRating());
            movieDTO.setMovieCast(movie.getMovieCast());
            movieDTO.setDirector(movie.getDirector());
            movieDTO.setCountryOfOrigin(movie.getCountryOfOrigin());
            movieDTO.setAvailableLanguages(movie.getAvailableLanguages());
            movieDTO.setSubtitleLanguages(movie.getSubtitleLanguages());
            movieDTO.setReleaseYear(movie.getReleaseYear());
            movieDTO.setPoster(movie.getPoster());
            movieDTO.setBoxOfficeRevenue(movie.getBoxOfficeRevenue());
            movieDTO.setProductionCompany(movie.getProductionCompany());
            movieDTO.setAgeRating(movie.getAgeRating());
            movieDTO.setKeywords(movie.getKeywords());
            movieDTO.setTrailerUrl(movie.getTrailerUrl());
            movieDTO.setReleaseYear(movie.getReleaseYear());
            movieDTO.setPosterUrl(postrUrl);
            movieDTOList.add(movieDTO);
        }


        return movieDTOList;
    }

    @Override
    public MovieDTO updateMovie(Long movieId, MovieDTO movieDTO, MultipartFile file) throws IOException {

        Movie mv = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found! Movie id: " + movieId));

        String fileName = mv.getPoster();

        if (file != null && !file.isEmpty() && !fileName.equals(movieDTO.getPoster()) ) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }


        movieDTO.setPoster(fileName);

        if (movieRepository.existsByTitle(movieDTO.getTitle())) {
            throw new FileExistsException("Movie with title  " + movieDTO.getTitle() + "already exists.");
        }
        Movie movie = new Movie();



        movie.setTitle(movieDTO.getTitle());
        movie.setGenre(movieDTO.getGenre());
        movie.setDescription(movieDTO.getDescription());
        movie.setLanguage(movieDTO.getLanguage());
        movie.setDuration(movieDTO.getDuration());
        movie.setStudio(movieDTO.getStudio());
        movie.setRating(movieDTO.getRating());
        movie.setMovieCast(movieDTO.getMovieCast());
        movie.setDirector(movieDTO.getDirector());
        movie.setCountryOfOrigin(movieDTO.getCountryOfOrigin());
        movie.setAvailableLanguages(movieDTO.getAvailableLanguages());
        movie.setSubtitleLanguages(movieDTO.getSubtitleLanguages());
        movie.setReleaseYear(movieDTO.getReleaseYear());
        movie.setPoster(fileName);
        movie.setBoxOfficeRevenue(movieDTO.getBoxOfficeRevenue());
        movie.setProductionCompany(movieDTO.getProductionCompany());
        movie.setAgeRating(movieDTO.getAgeRating());
        movie.setKeywords(movieDTO.getKeywords());
        movie.setTrailerUrl(movieDTO.getTrailerUrl());

        Movie updatedMovie = movieRepository.save(movie);

        String posterUrl = baseUrl + "/file/" + fileName;

        MovieDTO response = new MovieDTO();
        response.setMovieId(updatedMovie.getMovieId());
        response.setTitle(updatedMovie.getTitle());
        response.setGenre(updatedMovie.getGenre());
        response.setDescription(updatedMovie.getDescription());
        response.setLanguage(updatedMovie.getLanguage());
        response.setDuration(updatedMovie.getDuration());
        response.setStudio(updatedMovie.getStudio());
        response.setRating(updatedMovie.getRating());
        response.setMovieCast(updatedMovie.getMovieCast());
        response.setDirector(updatedMovie.getDirector());
        response.setCountryOfOrigin(updatedMovie.getCountryOfOrigin());
        response.setAvailableLanguages(updatedMovie.getAvailableLanguages());
        response.setSubtitleLanguages(updatedMovie.getSubtitleLanguages());
        response.setReleaseYear(updatedMovie.getReleaseYear());
        response.setPoster(updatedMovie.getPoster());
        response.setBoxOfficeRevenue(updatedMovie.getBoxOfficeRevenue());
        response.setProductionCompany(updatedMovie.getProductionCompany());
        response.setAgeRating(updatedMovie.getAgeRating());
        response.setKeywords(updatedMovie.getKeywords());
        response.setTrailerUrl(updatedMovie.getTrailerUrl());
        response.setPosterUrl(posterUrl);

        return response;
    }


    @Override
    public String deleteMovie(Long movieId) throws IOException {

        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found! Movie id: " + movieId));

        Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));

        movieRepository.delete(mv);
        return "Movie deleted with id: " + movieId;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Movie> moviePages = movieRepository.findAll(pageable);

        List<Movie> movies = moviePages.getContent();

        List<MovieDTO> movieDTOList = new ArrayList<>();

        for (Movie movie : movies) {
            MovieDTO movieDTO = new MovieDTO();
            String postrUrl = baseUrl + "/file/" + movie.getPoster();

            movieDTO.setMovieId(movie.getMovieId());
            movieDTO.setTitle(movie.getTitle());
            movieDTO.setGenre(movie.getGenre());
            movieDTO.setDescription(movie.getDescription());
            movieDTO.setLanguage(movie.getLanguage());
            movieDTO.setDuration(movie.getDuration());
            movieDTO.setStudio(movie.getStudio());
            movieDTO.setRating(movie.getRating());
            movieDTO.setMovieCast(movie.getMovieCast());
            movieDTO.setDirector(movie.getDirector());
            movieDTO.setCountryOfOrigin(movie.getCountryOfOrigin());
            movieDTO.setAvailableLanguages(movie.getAvailableLanguages());
            movieDTO.setSubtitleLanguages(movie.getSubtitleLanguages());
            movieDTO.setReleaseYear(movie.getReleaseYear());
            movieDTO.setPoster(movie.getPoster());
            movieDTO.setBoxOfficeRevenue(movie.getBoxOfficeRevenue());
            movieDTO.setProductionCompany(movie.getProductionCompany());
            movieDTO.setAgeRating(movie.getAgeRating());
            movieDTO.setKeywords(movie.getKeywords());
            movieDTO.setTrailerUrl(movie.getTrailerUrl());
            movieDTO.setReleaseYear(movie.getReleaseYear());

            movieDTO.setPosterUrl(postrUrl);

            movieDTOList.add(movieDTO);
        }

        return new MoviePageResponse(movieDTOList, pageNumber, pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Movie> moviePages = movieRepository.findAll(pageable);

        List<Movie> movies = moviePages.getContent();

        List<MovieDTO> movieDTOList = new ArrayList<>();

        for (Movie movie : movies) {
            MovieDTO movieDTO = new MovieDTO();
            String postrUrl = baseUrl + "/file/" + movie.getPoster();
            movieDTO.setMovieId(movie.getMovieId());
            movieDTO.setTitle(movie.getTitle());
            movieDTO.setGenre(movie.getGenre());
            movieDTO.setDescription(movie.getDescription());
            movieDTO.setLanguage(movie.getLanguage());
            movieDTO.setDuration(movie.getDuration());
            movieDTO.setStudio(movie.getStudio());
            movieDTO.setRating(movie.getRating());
            movieDTO.setMovieCast(movie.getMovieCast());
            movieDTO.setDirector(movie.getDirector());
            movieDTO.setCountryOfOrigin(movie.getCountryOfOrigin());
            movieDTO.setAvailableLanguages(movie.getAvailableLanguages());
            movieDTO.setSubtitleLanguages(movie.getSubtitleLanguages());
            movieDTO.setReleaseYear(movie.getReleaseYear());
            movieDTO.setPoster(movie.getPoster());
            movieDTO.setBoxOfficeRevenue(movie.getBoxOfficeRevenue());
            movieDTO.setProductionCompany(movie.getProductionCompany());
            movieDTO.setAgeRating(movie.getAgeRating());
            movieDTO.setKeywords(movie.getKeywords());
            movieDTO.setTrailerUrl(movie.getTrailerUrl());
            movieDTO.setReleaseYear(movie.getReleaseYear());

            movieDTO.setPosterUrl(postrUrl);
            movieDTOList.add(movieDTO);
        }

        return new MoviePageResponse(movieDTOList, pageNumber, pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }

    @Override
    public Optional<Movie> findByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

    @Override
    public List<Movie> findByDirector(String director) {
        return movieRepository.findByDirector(director);
    }

    @Override
    public List<Movie> findByStudio(String studio) {
        return movieRepository.findByStudio(studio);
    }

    @Override
    public List<Movie> findByTitleContainingIgnoreCase(String keyword) {
        return movieRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Override
    public List<Movie> findByDirectorAndStudio(String director, String studio) {
        return movieRepository.findByDirectorAndStudio(director, studio);
    }

    @Override
    public List<Movie> findMoviesByActor(String actor) {
        return movieRepository.findMoviesByActor(actor);
    }

    @Override
    public List<Movie> findLatestMovies() {
        return movieRepository.findLatestMovies();
    }

    @Override
    public boolean existsByTitle(String title) {
        return movieRepository.existsByTitle(title);
    }


}
