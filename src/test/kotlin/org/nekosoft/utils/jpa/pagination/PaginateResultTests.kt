package org.nekosoft.utils.jpa.pagination

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.nekosoft.utils.rest.pagination.PaginationData.Companion.paginationToPageable
import org.nekosoft.utils.rest.pagination.PaginationOptions

class PaginateResultTests {

    @Test
    fun `no values means no pagination`() {
        val options = PaginationOptions(null, null)
        val pageable = paginationToPageable(options)
        assertFalse(pageable.isPaged)
    }

    @Test
    fun `any value means pagination`() {
        val options1 = PaginationOptions(1, null)
        val pageable1 = paginationToPageable(options1)
        assertTrue(pageable1.isPaged)
        val options2 = PaginationOptions(null, 25)
        val pageable2 = paginationToPageable(options2)
        assertTrue(pageable2.isPaged)
        val options3 = PaginationOptions(5, 25)
        val pageable3 = paginationToPageable(options3)
        assertTrue(pageable3.isPaged)
    }

    @Test
    fun `Pageable page is zero-based, options is one-based`() {
        val options1 = PaginationOptions(1, null)
        val pageable1 = paginationToPageable(options1)
        assertEquals(0, pageable1.pageNumber)
        val options2 = PaginationOptions(23, null)
        val pageable2 = paginationToPageable(options2)
        assertEquals(22, pageable2.pageNumber)
    }

    @Test
    fun `Default page is 1`() {
        val options = PaginationOptions(null, 25)
        val pageable = paginationToPageable(options)
        assertEquals(0, pageable.pageNumber)
    }

    @Test
    fun `Default size is 100`() {
        val options = PaginationOptions(1, null)
        val pageable = paginationToPageable(options)
        assertEquals(100, pageable.pageSize)
    }
}