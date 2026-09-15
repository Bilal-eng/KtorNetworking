import SwiftUI
import SharedLogic

struct NewsListScreen: View {
    let state: NewsListUiState
    let onRetry: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Group {
                switch state {
                case .loading:
                    ProgressView("Haberler yükleniyor…")
                case .error:
                    VStack(spacing: 16) {
                        Text("Haberler yüklenemedi. Lütfen tekrar dene.")
                            .multilineTextAlignment(.center)
                        Button("Tekrar dene", action: onRetry)
                            .buttonStyle(.borderedProminent)
                    }
                    .padding()
                case .success(let news):
                    if news.isEmpty {
                        Text("Henüz haber bulunmuyor.")
                            .foregroundStyle(.secondary)
                    } else {
                        ScrollView {
                            LazyVStack(spacing: 16) {
                                ForEach(news, id: \.id) { article in
                                    NavigationLink(value: NewsDetailDestination(newsId: article.id)) {
                                        NewsCard(news: article)
                                    }
                                    .buttonStyle(.plain)
                                }
                            }
                            .padding()
                        }
                    }
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

private struct NewsCard: View {
    let news: News

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Images use SwiftUI's loader; article JSON still comes through Ktor.
            Color(.secondarySystemBackground)
                .frame(height: 200)
                .overlay {
                    AsyncImage(url: URL(string: news.imageUrl)) { phase in
                        switch phase {
                        case .empty:
                            ProgressView()
                        case .success(let image):
                            image.resizable().scaledToFill()
                        case .failure:
                            Image(systemName: "photo")
                                .font(.largeTitle)
                                .foregroundStyle(.secondary)
                        @unknown default:
                            Image(systemName: "photo")
                        }
                    }
                }
                .clipped()
                .accessibilityHidden(true)

            VStack(alignment: .leading, spacing: 8) {
                Text(news.title)
                    .font(.headline)
                    .lineLimit(3)
                Text(news.description_)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .lineLimit(3)
                Text(news.sourceName)
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
            .padding()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 16))
    }
}

#Preview("Loading") {
    NewsListScreen(state: .loading, onRetry: {})
}

#Preview("Error") {
    NewsListScreen(state: .error, onRetry: {})
}

#Preview("Empty") {
    NewsListScreen(state: .success([]), onRetry: {})
}

#Preview("Success") {
    NavigationStack {
        NewsListScreen(state: .success([
            News(id: 42, title: "Yeni uzay görevi başladı",
                 description: "Bilim insanları Dünya'yı gözlemlemek için yeni bir uydu gönderdi.",
                 imageUrl: "", authors: ["Örnek yazar"], publishedAt: "2026-09-15T10:00:00Z",
                 sourceName: "Örnek kaynak", sourceUrl: "https://example.com")
        ]), onRetry: {})
        .navigationTitle("Uzay Haberleri")
        .navigationDestination(for: NewsDetailDestination.self) { destination in
            NewsDetailScreen(newsId: destination.newsId)
        }
    }
}
