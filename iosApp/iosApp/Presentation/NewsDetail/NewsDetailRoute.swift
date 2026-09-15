import SwiftUI
import SharedLogic

struct NewsDetailRoute: View {
    @StateObject private var model: NewsDetailModel

    init(newsId: Int32) {
        _model = StateObject(wrappedValue: NewsDetailModel(
            newsId: newsId,
            client: IosNewsClient()
        ))
    }

    var body: some View {
        NewsDetailScreen(state: model.state, onRetry: model.loadNews)
            .onAppear { model.loadIfNeeded() }
            .onDisappear { model.cancelLoading() }
    }
}
