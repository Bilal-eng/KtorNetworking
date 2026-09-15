import Foundation
import Combine
import SharedLogic

enum NewsDetailUiState {
    case loading
    case success(News)
    case error
}

@MainActor
final class NewsDetailModel: ObservableObject {
    @Published private(set) var state: NewsDetailUiState = .loading

    private let newsId: Int32
    private let client: IosNewsClient
    private var request: NewsRequest?
    private var activeRequestID: UUID?

    init(newsId: Int32, client: IosNewsClient) {
        self.newsId = newsId
        self.client = client
    }

    func loadIfNeeded() {
        guard case .loading = state else { return }
        loadNews()
    }

    func loadNews() {
        guard activeRequestID == nil else { return }
        let requestID = UUID()
        activeRequestID = requestID
        state = .loading

        request = client.getNewsById(
            newsId: newsId,
            onSuccess: { [weak self] news in
                Task { @MainActor [weak self] in
                    self?.finish(.success(news), requestID: requestID)
                }
            },
            onError: { [weak self] in
                Task { @MainActor [weak self] in
                    self?.finish(.error, requestID: requestID)
                }
            }
        )
    }

    func cancelLoading() {
        activeRequestID = nil
        request?.cancel()
        request = nil
    }

    private func finish(_ newState: NewsDetailUiState, requestID: UUID) {
        // Ignore callbacks queued before cancellation or a newer request.
        guard activeRequestID == requestID else { return }
        activeRequestID = nil
        request = nil
        state = newState
    }

    deinit {
        Task { @MainActor [client] in
            client.close()
        }
    }
}
