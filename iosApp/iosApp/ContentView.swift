import SwiftUI
import SharedLogic

struct ContentView: View {
    @StateObject private var model = NewsListModel(client: IosNewsClient())

    var body: some View {
        NewsListScreen(state: model.state, onRetry: model.loadNews)
            .onAppear { model.loadIfNeeded() }
            .onDisappear { model.cancelLoading() }
    }
}
