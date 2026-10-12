package com.example.xai

import java.time.Instant

/**
 * User-safe provenance for one answer. This model deliberately excludes prompts,
 * chain-of-thought, credentials, and raw tool payloads.
 */
data class XaiEvidence(
    val id: String,
    val title: String,
    val uri: String? = null,
    val retrievedAt: Instant? = null,
    val supportsAnswer: Boolean? = null
)

enum class XaiContextKind { CURRENT_CONVERSATION, SAVED_MEMORY, EXTERNAL_EVIDENCE, NONE }

data class XaiContextItem(
    val kind: XaiContextKind,
    val label: String,
    val selectedMemoryId: String? = null
)

enum class XaiActionStatus { SUCCESS, FAILED, BLOCKED, SKIPPED, UNKNOWN }

data class XaiAction(
    val name: String,
    val summary: String,
    val status: XaiActionStatus
)

data class XaiExplanation(
    val answerId: String,
    val evidence: List<XaiEvidence> = emptyList(),
    val context: List<XaiContextItem> = emptyList(),
    val actions: List<XaiAction> = emptyList(),
    val limitations: List<String> = emptyList(),
    /** True only when backend provenance exists for this answer. */
    val provenanceAvailable: Boolean = false
)

/** Avoids implying that missing provenance is proof that no evidence was used. */
fun XaiExplanation.provenanceStatus(): String =
    if (provenanceAvailable) "Provenance recorded" else "Details unavailable"

/** A saved-memory label is valid only when the selected memory has an auditable ID. */
fun XaiContextItem.displayLabel(): String =
    if (kind == XaiContextKind.SAVED_MEMORY && selectedMemoryId.isNullOrBlank()) {
        "Saved memory (not verified)"
    } else {
        label
    }
