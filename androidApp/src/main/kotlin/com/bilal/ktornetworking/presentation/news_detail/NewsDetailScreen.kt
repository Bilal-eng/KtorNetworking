package com.bilal.ktornetworking.presentation.news_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bilal.ktornetworking.R
import com.bilal.ktornetworking.domain.model.News
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NewsDetailRoute(
    newsId: Int,
    onBack: () -> Unit,
    viewModel: NewsDetailViewModel = koinViewModel(
        key = "news-detail-$newsId",
        parameters = { parametersOf(newsId) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NewsDetailScreen(uiState = uiState, onRetry = viewModel::loadNews, onBack = onBack)
}

@Composable
fun NewsDetailScreen(uiState: NewsDetailUiState, onRetry: () -> Unit, onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            TextButton(onClick = onBack, modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(stringResource(R.string.news_back))
            }
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (uiState) {
                    NewsDetailUiState.Loading -> DetailStatus {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.news_detail_loading))
                    }
                    NewsDetailUiState.Error -> DetailStatus {
                        Text(stringResource(R.string.news_detail_error), textAlign = TextAlign.Center)
                        Button(onClick = onRetry) { Text(stringResource(R.string.news_retry)) }
                    }
                    is NewsDetailUiState.Success -> NewsDetailContent(uiState.news)
                }
            }
        }
    }
}

@Composable
private fun DetailStatus(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) { content() }
}

@Composable
private fun NewsDetailContent(news: News) {
    val authors = news.authors.filter { it.isNotBlank() }.joinToString(", ")
        .ifBlank { stringResource(R.string.news_author_unknown) }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AsyncImage(
            model = news.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(240.dp),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
            error = painterResource(android.R.drawable.ic_menu_report_image),
        )
        Text(news.title, style = MaterialTheme.typography.headlineSmall)
        Text(news.sourceName, style = MaterialTheme.typography.labelLarge)
        Text(stringResource(R.string.news_authors, authors))
        Text(stringResource(R.string.news_published_at, news.publishedAt))
        Text(news.description, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun NewsDetailPreview() {
    val news = News(
        id = 42,
        title = "Yeni uzay görevi",
        description = "Haberin açıklaması burada satır sınırı olmadan gösterilir.",
        imageUrl = "",
        authors = listOf("Örnek Yazar"),
        publishedAt = "2026-09-14T10:00:00Z",
        sourceName = "Örnek Kaynak",
        sourceUrl = "",
    )
    MaterialTheme {
        NewsDetailScreen(NewsDetailUiState.Success(news), onRetry = {}, onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun NewsDetailErrorPreview() {
    MaterialTheme { NewsDetailScreen(NewsDetailUiState.Error, onRetry = {}, onBack = {}) }
}

@Preview(showBackground = true)
@Composable
private fun NewsDetailLoadingPreview() {
    MaterialTheme { NewsDetailScreen(NewsDetailUiState.Loading, onRetry = {}, onBack = {}) }
}
