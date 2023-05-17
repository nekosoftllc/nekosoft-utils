@file:JvmName("NekosoftJpaPaginationUtils")

package org.nekosoft.utils.jpa.pagination

import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import javax.persistence.TypedQuery

fun <X> paginateResult(query: TypedQuery<X>, countQuery: TypedQuery<Long>, pageable: Pageable?): PageImpl<X> {
    if (pageable != null && pageable.isPaged) {
        query.firstResult = pageable.pageNumber * pageable.pageSize
        query.maxResults = pageable.pageSize
    }

    val queryResults = query.resultList

    val countResult = countQuery.singleResult as Long

    // Return the paged result

    return PageImpl(queryResults, pageable ?: Pageable.unpaged(), countResult)
}