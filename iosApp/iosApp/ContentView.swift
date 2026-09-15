import SwiftUI
import SharedLogic

struct ContentView: View {
    @StateObject private var model = NewsListModel(client: IosNewsClient())

    var body: some View {
        NavigationStack {
            NewsListScreen(state: model.state, onRetry: model.loadNews)
                .navigationTitle("Uzay Haberleri")
                .navigationDestination(for: NewsDetailDestination.self) { destination in
                    NewsDetailScreen(newsId: destination.newsId)
                }
                .onAppear { model.loadIfNeeded() }
                .onDisappear { model.cancelLoading() }
        }
    }
}
