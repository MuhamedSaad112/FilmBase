package com.filmbase.repository;


import com.filmbase.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Movie entity operations.
 * Provides standard CRUD operations and custom queries for movie-related data access.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Find a movie by its title
     *
     * @param title the movie title
     * @return Optional containing the movie if found
     */
    Optional<Movie> findByTitle(String title);

    /**
     * Find movies by director name
     *
     * @param director the director's name
     * @return List of movies by the director
     */
    List<Movie> findByDirector(String director);

    /**
     * Find movies by studio
     *
     * @param studio the studio name
     * @return List of movies from the studio
     */
    List<Movie> findByStudio(String studio);

    /**
     * Search movies by title containing the given keyword
     *
     * @param keyword search term
     * @return List of movies matching the search term
     */
    List<Movie> findByTitleContainingIgnoreCase(String keyword);

    /**
     * Find movies by director and studio
     *
     * @param director the director's name
     * @param studio   the studio name
     * @return List of movies matching both criteria
     */
    List<Movie> findByDirectorAndStudio(String director, String studio);

    /**
     * Find movies where a specific actor is in the cast
     *
     * @param actor name of the actor
     * @return List of movies featuring the actor
     */
    @Query("SELECT m FROM Movie m JOIN m.movieCast cast WHERE cast = :actor")
    List<Movie> findMoviesByActor(@Param("actor") String actor);

    /**
     * Find the latest movies by release date
     *
     * @param limit number of movies to return
     * @return List of most recent movies
     */
    @Query("SELECT m FROM Movie m ORDER BY m.releaseYear DESC")
    List<Movie> findLatestMovies(int limit);

    /**
     * Check if a movie title already exists
     *
     * @param title the movie title to check
     * @return true if the title exists, false otherwise
     */
    boolean existsByTitle(String title);
}