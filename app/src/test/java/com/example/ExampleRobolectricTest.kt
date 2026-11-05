package com.example

import com.example.data.content.SampurnaHistoryMasterBank
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun `verify question bank loading on JVM`() {
    val count = SampurnaHistoryMasterBank.all500Questions.size
    assertEquals(500, count)
  }
}
