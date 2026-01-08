package com.gravity.ourmoments.presentation.component.post

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gravity.ourmoments.presentation.component.base.HandButton
import com.gravity.ourmoments.ui.theme.InkLight

@Composable
fun FilterBar(
    selectedVisibility: String?,
    onFilterChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "筛选:",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = InkLight
        )

        Spacer(modifier = Modifier.width(12.dp))

        FilterChip(
            text = "全部",
            selected = selectedVisibility == null,
            onClick = { if (selectedVisibility != null) onFilterChange(null) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        FilterChip(
            text = "公开",
            selected = selectedVisibility == "PUBLIC",
            onClick = { onFilterChange("PUBLIC") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        FilterChip(
            text = "私密",
            selected = selectedVisibility == "PRIVATE",
            onClick = { onFilterChange("PRIVATE") }
        )
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    HandButton(
        text = text,
        onClick = onClick,
        variant = if (selected) {
            HandButton.HandButtonVariant.Filled
        } else {
            HandButton.HandButtonVariant.Outline
        },
        modifier = Modifier.width(60.dp)
    )
}