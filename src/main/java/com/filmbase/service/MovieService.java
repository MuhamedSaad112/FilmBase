package com.filmbase.service;



import com.filmbase.dto.MovieDTO;
import com.filmbase.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {

    MovieDTO addMovie(MovieDTO movieDTO, MultipartFile file) throws IOException;

    MovieDTO getMovie(Long movieId);

    List<MovieDTO> getAllMovies();

    MovieDTO updateMovie(Long movieId, MovieDTO movieDTO, MultipartFile file) throws IOException;

    String deleteMovie(Long movieId) throws IOException;


    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);

    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String direction);

//    List<MovieDTO> getMoviesByDirector(String directorName);
//
//    List<MovieDTO> getMoviesByGenre(String genre);
//
//    long getMoviesCount();

}
