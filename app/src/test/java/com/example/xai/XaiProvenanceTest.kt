package com.example.xai

import org.junit.Assert.assertEquals
import org.junit.Test

class XaiProvenanceTest {
    @Test
    fun missingProvenanceIsNeverPresentedAsConfirmed() {
        val explanation = XaiExplanation(answerId = "answer-1")
        assertEquals("Details unavailable", explanation.provenanceStatus())
    }

    @Test
    fun recordedProvenanceHasExplicitStatus() {
        val explanation = XaiExplanation(
            answerId = "answer-2",
            provenanceAvailable = true
        )
        assertEquals("Provenance recorded", explanation.provenanceStatus())
    }

    @Test
    fun savedMemoryWithoutAuditableIdIsMarkedUnverified() {
        val context = XaiContextItem(
            kind = XaiContextKind.SAVED_MEMORY,
            label = "Preferred writing style"
        )
        assertEquals("Saved memory (not verified)", context.displayLabel())
    }

    @Test
    fun savedMemoryWithAuditableIdCanUseItsRecordedLabel() {
        val context = XaiContextItem(
            kind = XaiContextKind.SAVED_MEMORY,
            label = "Saved preference",
            selectedMemoryId = "memory-123"
        )
        assertEquals("Saved preference", context.displayLabel())
    }
}
