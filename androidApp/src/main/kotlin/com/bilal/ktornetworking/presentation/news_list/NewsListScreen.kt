package com.bilal.ktornetworking.presentation.news_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bilal.ktornetworking.R
import com.bilal.ktornetworking.domain.model.News
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsListRoute(
    onNewsClick: (Int) -> Unit,
    viewModel: NewsListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NewsListScreen(uiState = uiState, onRetry = viewModel::loadNews, onNewsClick = onNewsClick)
}

@Composable
fun NewsListScreen(uiState: NewsListUiState, onRetry: () -> Unit, onNewsClick: (Int) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            Text(
                text = stringResource(R.string.news_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
            )
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (uiState) {
                    NewsListUiState.Loading -> StatusContent {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.news_loading))
                    }
                    NewsListUiState.Error -> StatusContent {
                        Text(stringResource(R.string.news_error), textAlign = TextAlign.Center)
                        Button(onClick = onRetry) {
                            Text(stringResource(R.string.news_retry))
                        }
                    }
                    is NewsListUiState.Success -> {
                        if (uiState.news.isEmpty()) {
                            StatusContent {
                                Text(stringResource(R.string.news_empty), textAlign = TextAlign.Center)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                items(items = uiState.news, key = { it.id }) { news ->
                                    NewsCard(news, onClick = { onNewsClick(news.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusContent(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        content()
    }
}

@Composable
private fun NewsCard(news: News, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = news.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
            error = painterResource(android.R.drawable.ic_menu_report_image),
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = news.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = news.sourceName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    MaterialTheme { NewsListScreen(NewsListUiState.Loading, onRetry = {}, onNewsClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    MaterialTheme { NewsListScreen(NewsListUiState.Error, onRetry = {}, onNewsClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() {
    MaterialTheme { NewsListScreen(NewsListUiState.Success(emptyList()), onRetry = {}, onNewsClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun NewsPreview() {
    val news = News(
        id = 1,
        title = "Yeni uzay görevi için hazırlıklar sürüyor",
        description = "Araştırma ekibi, yeni görevle uzay hakkında daha fazla veri toplamayı hedefliyor.",
        imageUrl = "",
        authors = emptyList(),
        publishedAt = "2026-09-12T10:00:00Z",
        sourceName = "Örnek Haber Kaynağı",
        sourceUrl = "",
    )
    MaterialTheme { NewsListScreen(NewsListUiState.Success(listOf(news)), onRetry = {}, onNewsClick = {}) }
}
