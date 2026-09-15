import Foundation
import Combine
import SharedLogic

enum NewsListUiState {
    case loading
    case success([News])
    case error
}

@MainActor
final class NewsListModel: ObservableObject {
    @Published private(set) var state: NewsListUiState = .loading

    private let client: IosNewsClient
    private var request: NewsRequest?
    private var activeRequestID: UUID?

    init(client: IosNewsClient) {
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

        request = client.getNews(
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

    private func finish(_ newState: NewsListUiState, requestID: UUID) {
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
