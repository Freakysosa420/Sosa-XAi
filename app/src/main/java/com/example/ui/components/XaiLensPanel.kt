package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.xai.XaiActionStatus
import com.example.xai.XaiContextKind
import com.example.xai.XaiExplanation
import com.example.xai.displayLabel
import com.example.xai.provenanceStatus

/**
 * Expandable, user-safe explanation panel. Callers must pass provenance emitted
 * for this answer by a trusted backend; do not build this payload from guesses.
 */
@Composable
fun XaiLensPanel(
    explanation: XaiExplanation,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable(explanation.answerId) { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Hide why this answer" else "Why this answer?")
            }

            if (expanded) {
                Text(
                    text = explanation.provenanceStatus(),
                    style = MaterialTheme.typography.labelMedium
                )
                HorizontalDivider()

                SectionTitle("Evidence")
                if (explanation.evidence.isEmpty()) {
                    DetailText("No verifiable source details are available for this answer.")
                } else {
                    explanation.evidence.forEach { item ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(item.title, style = MaterialTheme.typography.bodyMedium)
                            item.uri?.let { DetailText(it) }
                            item.retrievedAt?.let { DetailText("Retrieved: $it") }
                            item.supportsAnswer?.let {
                                DetailText(if (it) "Linked to a supported claim" else "Support not confirmed")
                            }
                        }
                    }
                }

                HorizontalDivider()
                SectionTitle("Context")
                if (explanation.context.isEmpty()) {
                    DetailText("Context contribution was not recorded.")
                } else {
                    explanation.context.forEach { context ->
                        val kind = when (context.kind) {
                            XaiContextKind.CURRENT_CONVERSATION -> "Current conversation"
                            XaiContextKind.SAVED_MEMORY -> "Saved memory"
                            XaiContextKind.EXTERNAL_EVIDENCE -> "External evidence"
                            XaiContextKind.NONE -> "No context recorded"
                        }
                        DetailText("$kind: ${context.displayLabel()}")
                    }
                }

                HorizontalDivider()
                SectionTitle("Actions")
                if (explanation.actions.isEmpty()) {
                    DetailText("No tool actions are recorded for this answer.")
                } else {
                    explanation.actions.forEach { action ->
                        val status = when (action.status) {
                            XaiActionStatus.SUCCESS -> "Succeeded"
                            XaiActionStatus.FAILED -> "Failed"
                            XaiActionStatus.BLOCKED -> "Blocked"
                            XaiActionStatus.SKIPPED -> "Skipped"
                            XaiActionStatus.UNKNOWN -> "Unknown"
                        }
                        DetailText("${action.name} · $status")
                        DetailText(action.summary)
                    }
                }

                HorizontalDivider()
                SectionTitle("Uncertainty and limitations")
                if (explanation.limitations.isEmpty()) {
                    DetailText(
                        if (explanation.provenanceAvailable) {
                            "No additional limitations were recorded. This does not guarantee the answer is correct."
                        } else {
                            "Explanation details are unavailable. No confidence score is provided."
                        }
                    )
                } else {
                    explanation.limitations.forEach { DetailText(it) }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleSmall)
}

@Composable
private fun DetailText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
