package com.example

import com.example.data.content.Class5to8Data
import com.example.data.content.Class9to12Data
import com.example.data.content.SampurnaHistoryMasterBank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun `verify 500 questions bank size and uniqueness`() {
    val questions = SampurnaHistoryMasterBank.all500Questions
    assertEquals("Should have exactly 500 questions", 500, questions.size)
    val uniqueIds = questions.map { it.id }.toSet()
    assertEquals("All question IDs must be unique (Zero Repeats)", 500, uniqueIds.size)
  }

  @Test
  fun `verify question structure and detailed explanations`() {
    val questions = SampurnaHistoryMasterBank.all500Questions
    questions.forEach { q ->
      assertTrue("Question text should not be empty", q.text.isNotBlank())
      assertEquals("Each question must have 4 options", 4, q.options.size)
      assertTrue("Correct option index must be 0 to 3", q.correctOptionIndex in 0..3)
      assertTrue("Detailed explanation must be present", q.explanation.isNotBlank())
      assertTrue("Era must be valid", q.era in listOf("Ancient", "Medieval", "Modern", "World"))
    }
  }

  @Test
  fun `verify class 5 to 12 history content`() {
    for (lvl in 5..8) {
      val chapters = Class5to8Data.getChaptersForClass(lvl)
      assertTrue("Class $lvl should have chapters", chapters.isNotEmpty())
      chapters.forEach { ch ->
        assertTrue("Chapter title hindi must be present", ch.titleHindi.isNotBlank())
        assertTrue("Notes must be present", ch.fullNotes.isNotBlank())
      }
    }

    for (lvl in 9..12) {
      val chapters = Class9to12Data.getChaptersForClass(lvl)
      assertTrue("Class $lvl should have chapters", chapters.isNotEmpty())
      chapters.forEach { ch ->
        assertTrue("Chapter title hindi must be present", ch.titleHindi.isNotBlank())
        assertTrue("Notes must be present", ch.fullNotes.isNotBlank())
      }
    }
  }
}
