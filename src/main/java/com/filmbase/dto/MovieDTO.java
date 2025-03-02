package com.filmbase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

/**
 * Data Transfer Object for the Movie entity.
 * Provides a simplified view of the Movie entity to be used in API requests and responses.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MovieDTO {

    /**
     * Unique identifier for the movie.
     */
    private Long movieId;

    /**
     * The title of the movie. Must be unique.
     */
    @NotBlank(message = "Movie title is required")
    private String title;

    /**
     * Name of the movie's director.
     */
    @NotBlank(message = "Director name is required")
    private String director;

    /**
     * The genre of the movie (e.g., Action, Comedy, Drama).
     */
    @NotBlank(message = "Genre is required")
    private String genre;

    /**
     * A brief description or summary of the movie.
     */
    private String description;

    /**
     * Language of the movie (e.g., English, French).
     */
    @NotBlank(message = "Language is required")
    private String language;

    /**
     * The duration of the movie in minutes.
     */
    @NotNull(message = "Duration is required")
    private Integer duration;

    /**
     * Studio of the movie's director.
     */
    @NotBlank(message = "Studio is required")
    private String studio;

    /**
     * The rating of the movie (e.g., IMDB rating out of 10).
     */
    @NotNull(message = "Rating is required")
    private Double rating;

    /**
     * Set of actors/actresses who performed in the movie.
     */
    private Set<String> movieCast;

    /**
     * The country where the movie was produced.
     */
    @NotBlank(message = "Country of origin is required")
    private String countryOfOrigin;

    /**
     * A set of available languages (dubbing) for the movie.
     */
    private Set<String> availableLanguages;

    /**
     * A set of subtitle languages available for the movie.
     */
    private Set<String> subtitleLanguages;

    /**
     * The official release year of the movie.
     */
    @NotNull(message = "Release year is required")
    private Integer releaseYear;

    /**
     * Path to the movie's poster image.
     */
    @NotBlank(message = "Movie poster is required")
    private String poster;

    /**
     * The box office revenue of the movie in dollars.
     */
    private Long boxOfficeRevenue;

    /**
     * The production company responsible for the movie.
     */
    @NotBlank(message = "Production company is required")
    private String productionCompany;

    /**
     * The age rating of the movie (e.g., PG, PG-13, R).
     */
    @NotBlank(message = "Age rating is required")
    private String ageRating;

    /**
     * A set of keywords associated with the movie for search and categorization.
     */
    private Set<String> keywords;

    /**
     * URL for the movie's trailer.
     */
    private String trailerUrl;



    @NotBlank(message = "Movie posterUrl is required")
    private String posterUrl;
}
