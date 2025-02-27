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

        String uploadedFileName = fileService.uploadFile(path, file);

        movieDTO.setPoster(uploadedFileName);

        Movie movie = new Movie();

        movie.setTitle(movieDTO.getTitle());
        movie.setDirector(movieDTO.getDirector());
        movie.setStudio(movieDTO.getStudio());
        movie.setMovieCast(movieDTO.getMovieCast());
        movie.setReleaseYear(movieDTO.getReleaseYear());
        movie.setPoster(movieDTO.getPoster());
        Movie savedMovie = movieRepository.save(movie);

        String postrUrl = baseUrl + "/file/" + uploadedFileName;

        MovieDTO response = new MovieDTO();

        response.setMovieId(savedMovie.getMovieId());
        response.setTitle(savedMovie.getTitle());
        response.setDirector(savedMovie.getDirector());
        response.setStudio(savedMovie.getStudio());
        response.setMovieCast(savedMovie.getMovieCast());
        response.setReleaseYear(savedMovie.getReleaseYear());
        response.setPoster(savedMovie.getPoster());
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
        response.setDirector(movie.getDirector());
        response.setStudio(movie.getStudio());
        response.setMovieCast(movie.getMovieCast());
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
            movieDTO.setDirector(movie.getDirector());
            movieDTO.setStudio(movie.getStudio());
            movieDTO.setMovieCast(movie.getMovieCast());
            movieDTO.setReleaseYear(movie.getReleaseYear());
            movieDTO.setPoster(movie.getPoster());
            movieDTO.setPosterUrl(postrUrl);
            movieDTOList.add(movieDTO);
        }


        return movieDTOList;
    }

    @Override
    public MovieDTO updateMovie(Long movieId, MovieDTO movieDTO, MultipartFile file) throws IOException {

        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found! Movie id: " + movieId));

        String fileName = mv.getPoster();

        if (file != null && !fileName.equals(mv.getPoster())) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        movieDTO.setPoster(fileName);

        Movie movie = new Movie();
        movie.setMovieId(mv.getMovieId());
        movie.setTitle(movieDTO.getTitle());
        movie.setDirector(movieDTO.getDirector());
        movie.setStudio(movieDTO.getStudio());
        movie.setMovieCast(movieDTO.getMovieCast());
        movie.setReleaseYear(movieDTO.getReleaseYear());
        movie.setPoster(fileName);

        Movie updateMovie = movieRepository.save(movie);

        String postrUrl = baseUrl + "/file/" + fileName;

        MovieDTO response = new MovieDTO();
        response.setMovieId(updateMovie.getMovieId());
        response.setTitle(updateMovie.getTitle());
        response.setDirector(updateMovie.getDirector());
        response.setStudio(updateMovie.getStudio());
        response.setMovieCast(updateMovie.getMovieCast());
        response.setReleaseYear(updateMovie.getReleaseYear());
        response.setPoster(updateMovie.getPoster());
        response.setPosterUrl(postrUrl);


        return response;
    }

    @Override
    public String deleteMovie(Long movieId) throws IOException {

        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found! Movie id: " + movieId));

        Long id = mv.getMovieId();
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
            movieDTO.setDirector(movie.getDirector());
            movieDTO.setStudio(movie.getStudio());
            movieDTO.setMovieCast(movie.getMovieCast());
            movieDTO.setReleaseYear(movie.getReleaseYear());
            movieDTO.setPoster(movie.getPoster());
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
            movieDTO.setDirector(movie.getDirector());
            movieDTO.setStudio(movie.getStudio());
            movieDTO.setMovieCast(movie.getMovieCast());
            movieDTO.setReleaseYear(movie.getReleaseYear());
            movieDTO.setPoster(movie.getPoster());
            movieDTO.setPosterUrl(postrUrl);
            movieDTOList.add(movieDTO);
        }

        return new MoviePageResponse(movieDTOList, pageNumber, pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }
}
