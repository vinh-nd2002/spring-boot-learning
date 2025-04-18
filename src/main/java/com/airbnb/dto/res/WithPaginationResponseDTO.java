package com.airbnb.dto.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithPaginationResponseDTO<T> {

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("size")
    private int size;

    @JsonProperty("total_items")
    private long totalItems;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("items")
    private List<T> items;
}
