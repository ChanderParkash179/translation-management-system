package com.tms.app.dtos.wrapper;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> {

    private Integer pageNo;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalItems;
    private Boolean isFirstPage;
    private Boolean isLastPage;
    private List<T> content;

    public static <T, R> PaginationResponse<R> makeResponse(Page<T> page, Function<T, R> mapper) {

        List<R> responseDTOList = page.getContent()
                .stream()
                .map(mapper)
                .toList();

        PaginationResponse<R> pageResponseDTO = new PaginationResponse<>();
        pageResponseDTO.setPageNo(page.getNumber());
        pageResponseDTO.setPageSize(page.getSize());
        pageResponseDTO.setTotalPages(page.getTotalPages());
        pageResponseDTO.setTotalItems(page.getTotalElements());
        pageResponseDTO.setIsFirstPage(page.isFirst());
        pageResponseDTO.setIsLastPage(page.isLast());
        pageResponseDTO.setContent(responseDTOList);

        return pageResponseDTO;
    }

    public static <T> PaginationResponse<T> makeEmptyResponse() {

        PaginationResponse<T> emptyPaginationResponse = new PaginationResponse<>();
        emptyPaginationResponse.setPageNo(0);
        emptyPaginationResponse.setPageSize(0);
        emptyPaginationResponse.setTotalItems(0L);
        emptyPaginationResponse.setIsFirstPage(true);
        emptyPaginationResponse.setIsLastPage(true);
        emptyPaginationResponse.setContent(Collections.emptyList());

        return emptyPaginationResponse;
    }
}
