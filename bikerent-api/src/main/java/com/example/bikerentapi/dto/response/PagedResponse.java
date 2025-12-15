package com.example.bikerentapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Стандартизированный ответ с пагинацией")
public record PagedResponse<T>(
        @Schema(description = "Содержимое текущей страницы")
        List<T> content,

        @Schema(description = "Номер текущей страницы (начиная с 0)")
        int page,

        @Schema(description = "Размер страницы")
        int size,

        @Schema(description = "Общее количество элементов во всех страницах")
        long totalElements,

        @Schema(description = "Общее количество страниц")
        int totalPages,

        @Schema(description = "Является ли текущая страница последней")
        boolean last
) {}
