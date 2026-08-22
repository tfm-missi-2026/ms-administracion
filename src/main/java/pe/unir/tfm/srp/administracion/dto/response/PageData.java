package pe.unir.tfm.srp.administracion.dto.response;

import java.util.List;

public record PageData<T>(
    List<T> items,
    long total,
    int page,
    int pageSize,
    int totalPages
) {
    public static <T> PageData<T> of(List<T> items, long total, int page, int pageSize) {
        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        return new PageData<>(items, total, page, pageSize, totalPages);
    }
}