import SwiftUI

// Temporary destination for verifying ID transfer before adding detail loading.
struct NewsDetailScreen: View {
    let newsId: Int32

    var body: some View {
        Text("Haber #\(newsId)")
            .font(.title2)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .navigationTitle("Haber Detayı")
            .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    NavigationStack {
        NewsDetailScreen(newsId: 42)
    }
}
