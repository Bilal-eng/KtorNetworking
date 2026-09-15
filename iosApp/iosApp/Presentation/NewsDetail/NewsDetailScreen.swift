import SwiftUI
import SharedLogic

struct NewsDetailScreen: View {
    let state: NewsDetailUiState
    let onRetry: () -> Void

    var body: some View {
        Group {
            switch state {
            case .loading:
                ProgressView("Haber yükleniyor…")
            case .error:
                VStack(spacing: 16) {
                    Text("Haber yüklenemedi. Lütfen tekrar dene.")
                        .multilineTextAlignment(.center)
                    Button("Tekrar dene", action: onRetry)
                        .buttonStyle(.borderedProminent)
                }
                .padding()
            case .success(let news):
                NewsDetailContent(news: news)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .navigationTitle("Haber Detayı")
        .navigationBarTitleDisplayMode(.inline)
    }
}

private struct NewsDetailContent: View {
    let news: News

    private var authorText: String {
        let names = news.authors
            .map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }
            .filter { !$0.isEmpty }
        return names.isEmpty ? "Bilinmiyor" : names.joined(separator: ", ")
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Color(.secondarySystemBackground)
                    .frame(height: 240)
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
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                    .accessibilityHidden(true)

                Text(news.title)
                    .font(.title.bold())
                Text(news.sourceName)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                Text("Yazar: \(authorText)")
                    .font(.subheadline)
                Text("Yayın tarihi: \(news.publishedAt)")
                    .font(.caption)
                    .foregroundStyle(.secondary)
                Text(news.description_)
                    .font(.body)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding()
        }
    }
}

#Preview("Loading") {
    NavigationStack {
        NewsDetailScreen(state: .loading, onRetry: {})
    }
}

#Preview("Error") {
    NavigationStack {
        NewsDetailScreen(state: .error, onRetry: {})
    }
}

#Preview("Success") {
    NavigationStack {
        NewsDetailScreen(state: .success(News(
            id: 42, title: "Yeni uzay görevi başladı",
            description: "Bilim insanları Dünya'yı gözlemlemek için yeni bir uydu gönderdi.",
            imageUrl: "", authors: ["Örnek yazar", "İkinci yazar"],
            publishedAt: "2026-09-15T10:00:00Z",
            sourceName: "Örnek kaynak", sourceUrl: "https://example.com"
        )), onRetry: {})
    }
}
