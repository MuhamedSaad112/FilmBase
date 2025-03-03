package com.filmbase.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmbase.dto.MovieDTO;
import com.filmbase.exception.EmptyFileException;
import com.filmbase.security.AuthoritiesConstants;
import com.filmbase.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin/movie")
@RequiredArgsConstructor
public class MovieAdminController {

    private final MovieService movieService;

    @PostMapping("/add-movie")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
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


    @PutMapping("/update/{movieId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<MovieDTO> updateMovieHandler(@PathVariable Long movieId, @RequestPart String movieDTO, @RequestPart MultipartFile file) throws IOException {

        MovieDTO movieDTO1 = convertToMovieDTO(movieDTO);
        return ResponseEntity.ok(movieService.updateMovie(movieId, movieDTO1, file));
    }

    @DeleteMapping("/delete/{movieId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Long movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }


}
