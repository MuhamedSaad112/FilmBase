package com.filmbase.dto;

import java.util.List;

public record MoviePageResponse(List<MovieDTO> movieDTOS, Integer pageNumber, Integer pageSize,
                                Long totalElements, Integer totalPages, boolean isLastPage) {
}
