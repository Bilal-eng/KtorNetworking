package com.bilal.ktornetworking.presentation.news_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bilal.ktornetworking.R

// Navigation exercise: the next stage will load the article using newsId.
@Composable
fun NewsDetailScreen(newsId: Int, onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.news_back))
            }
            Text(
                stringResource(R.string.news_detail_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(stringResource(R.string.news_selected_id, newsId))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NewsDetailPreview() {
    MaterialTheme { NewsDetailScreen(newsId = 42, onBack = {}) }
}
