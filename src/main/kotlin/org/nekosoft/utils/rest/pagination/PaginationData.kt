package org.nekosoft.utils.rest.pagination

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

data class PaginationData(
    var currentPage: Int,
    var pageSize: Int,
    var currentPageSize: Int,
    var totalPages: Int,
    var totalSize: Long,
) {

    companion object {

        @JvmStatic
        fun fromPage(page: Page<*>): PaginationData = PaginationData(
            currentPage = page.number + 1,
            pageSize = page.size,
            currentPageSize = page.numberOfElements,
            totalPages = page.totalPages,
            totalSize = page.totalElements,
        )

        @JvmStatic
        fun paginationToPageable(pagination: PaginationOptions): Pageable = if (pagination.page == null && pagination.pageSize == null) {
            Pageable.unpaged()
        } else {
            PageRequest.of((pagination.page ?: 1) - 1, pagination.pageSize ?: 100)
        }

    }

}
