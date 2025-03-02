package com.filmbase.service;



import com.filmbase.dto.MovieDTO;
import com.filmbase.dto.MoviePageResponse;
import com.filmbase.entity.Movie;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MovieService {

    MovieDTO addMovie(MovieDTO movieDTO, MultipartFile file) throws IOException;

    MovieDTO getMovie(Long movieId);

    List<MovieDTO> getAllMovies();

    MovieDTO updateMovie(Long movieId, MovieDTO movieDTO, MultipartFile file) throws IOException;

    String deleteMovie(Long movieId) throws IOException;


    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);

    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String direction);

    Optional<Movie> findByTitle(String title);

    List<Movie> findByDirector(String director);

    List<Movie> findByStudio(String studio);

    List<Movie> findByTitleContainingIgnoreCase(String keyword);

    List<Movie> findByDirectorAndStudio(String director, String studio);

    List<Movie> findMoviesByActor(String actor);

    List<Movie> findLatestMovies();

    boolean existsByTitle(String title);

}
