package com.filmbase.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmbase.configuration.AppConstants;
import com.filmbase.dto.MovieDTO;
import com.filmbase.dto.MoviePageResponse;
import com.filmbase.entity.Movie;
import com.filmbase.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/movie")
@RequiredArgsConstructor
public class MovieUserController {

    private final MovieService movieService;

    private MovieDTO convertToMovieDTO(String movieDTOObj) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDTOObj, MovieDTO.class);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDTO> getMovieHandler(@PathVariable Long movieId) {
        return new ResponseEntity<>(movieService.getMovie(movieId), HttpStatus.OK);
    }

    @GetMapping("/all-list")
    public ResponseEntity<List<MovieDTO>> getAllMoviesHandler() {
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }


    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse> getMovieWithPagination(@RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                    @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize) {

        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber, pageSize));
    }


    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getMovieWithPaginationAndSorting(@RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                              @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                              @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
                                                                              @RequestParam(defaultValue = AppConstants.SORT_DER, required = false) String direction) {

        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber, pageSize, sortBy, direction));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Optional<Movie>> findByTitle(@PathVariable String title) {
        return ResponseEntity.ok(movieService.findByTitle(title));
    }


    @GetMapping("/director/{director}")
    public ResponseEntity<List<Movie>> findByDirector(@PathVariable String director) {
        return ResponseEntity.ok(movieService.findByDirector(director));
    }

    @GetMapping("/studio/{studio}")
    public ResponseEntity<List<Movie>> findByStudio(@PathVariable String studio) {
        return ResponseEntity.ok(movieService.findByStudio(studio));
    }

    @GetMapping("/keyword-title/{keyword}")
    public ResponseEntity<List<Movie>> findByTitleContainingIgnoreCase(@PathVariable String keyword) {
        return ResponseEntity.ok(movieService.findByTitleContainingIgnoreCase(keyword));
    }

    @GetMapping("/subdirectories")
    public ResponseEntity<List<Movie>> findByDirectorAndStudio(@RequestParam String director, @RequestParam String studio) {
        return ResponseEntity.ok(movieService.findByDirectorAndStudio(director, studio));
    }

    @GetMapping("/subdirectories/{actor}")
    public ResponseEntity<List<Movie>> findMoviesByActor(@PathVariable String actor) {
        return ResponseEntity.ok(movieService.findMoviesByActor(actor));
    }

    @GetMapping("/releaseYear")
    public ResponseEntity<List<Movie>> findLatestMovies() {
        return ResponseEntity.ok(movieService.findLatestMovies());
    }

    @GetMapping("/title/exists/{title}")
    public ResponseEntity<Boolean> existsByTitle(@PathVariable String title) {
        return ResponseEntity.ok(movieService.existsByTitle(title));
    }
}
