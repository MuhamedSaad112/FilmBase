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
     * Studio of the movie's director.
     */
    @NotBlank(message = "Studio is required")
    private String studio;

    /**
     * Set of actors/actresses who performed in the movie.
     */
    private Set<String> movieCast;

    /**
     * The official release date of the movie.
     */
    @NotNull(message = "Release date is required")
    private Integer releaseYear;

    /**
     * path to the movie's poster image.
     */
    @NotBlank(message = "Movie poster is required")
    private String poster;

    @NotBlank(message = "Movie posterUrl is required")
    private String posterUrl;
}
