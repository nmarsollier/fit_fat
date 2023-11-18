package com.nmarsollier.fitfat.common

import com.nmarsollier.fitfat.common.converters.formatDate
import com.nmarsollier.fitfat.common.converters.formatDateTime
import com.nmarsollier.fitfat.common.converters.formatShortDate
import com.nmarsollier.fitfat.common.converters.parseIso8601
import com.nmarsollier.fitfat.common.converters.toIso8601
import com.nmarsollier.fitfat.common.converters.truncateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DateUtilsTest {
    val timeMillsSample = 1384540245000

    @Test
    fun toIso8601Test() {
        assertEquals("2013-11-15T18:30:45Z", Date(timeMillsSample).toIso8601())
        assertNull((null as Date?).toIso8601())
    }

    @Test
    fun parseIso8601Test() {
        assertEquals(Date(timeMillsSample), "2013-11-15T18:30:45Z".parseIso8601())
        assertNull((null as String?).parseIso8601())
    }

    @Test
    fun truncateTimeTest() {
        assertEquals(1384484400000, Date(timeMillsSample).truncateTime())
    }

    @Test
    fun formatDateTest() {
        assertEquals("Nov 15, 2013", Date(timeMillsSample).formatDate())
    }

    @Test
    fun formatShortDateTest() {
        assertEquals("11/15/13", Date(timeMillsSample).formatShortDate())
    }

    @Test
    fun formatDateTimeTest() {
        assertEquals("Nov 15, 2013, 3:30 PM", Date(timeMillsSample).formatDateTime())
    }

    @Test
    fun dateOfTest() {
        assertEquals(1697338800000, com.nmarsollier.fitfat.common.converters.dateOf(2023, 10, 15).time)
    }
}