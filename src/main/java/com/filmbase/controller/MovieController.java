package com.filmbase.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmbase.configuration.AppConstants;
import com.filmbase.dto.MovieDTO;
import com.filmbase.dto.MoviePageResponse;
import com.filmbase.exception.EmptyFileException;
import com.filmbase.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping("/add-movie")
    public ResponseEntity<MovieDTO> addMovieHandler(@RequestPart String movieDTO, @RequestPart MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new EmptyFileException("File is empty! Please select a file!");
        }
        MovieDTO dto = convertToMovieDTO(movieDTO);

        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    private MovieDTO convertToMovieDTO(String movieDTOObj) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDTOObj, MovieDTO.class);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDTO> getMovieHandler(@PathVariable Long movieId) {
        return new ResponseEntity<>(movieService.getMovie(movieId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDTO>> getAllMoviesHandler() {
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }


    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDTO> updateMovieHandler(@PathVariable Long movieId, @RequestPart String movieDTOObj, @RequestPart MultipartFile file) throws IOException {

        if (file.isEmpty()) file = null;

        MovieDTO movieDTO = convertToMovieDTO(movieDTOObj);
        return ResponseEntity.ok(movieService.updateMovie(movieId, movieDTO, file));
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Long movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
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


}
