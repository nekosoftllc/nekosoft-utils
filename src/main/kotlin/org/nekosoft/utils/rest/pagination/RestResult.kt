package org.nekosoft.utils.rest.pagination

import org.springframework.data.domain.Page

data class RestResult<T>(
    var results: List<T>? = null,
    var pagination: PaginationData? = null,
) {

    companion object {

        fun <T> fromPage(page: Page<T>): RestResult<T> = RestResult(
            results = page.content,
            pagination = PaginationData.fromPage(page)
        )

    }

}