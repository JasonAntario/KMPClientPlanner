import SwiftUI
import PhotosUI

/// Picks images, persists them to the app's documents directory, and returns their
/// file URLs as strings — matching the shared model's `List<String>` image-URL contract.
struct PhotoPickerButton: View {
    let label: String
    let onPicked: ([String]) -> Void

    @State private var selection: [PhotosPickerItem] = []

    var body: some View {
        PhotosPicker(selection: $selection, maxSelectionCount: 0, matching: .images) {
            Label(label, systemImage: "photo.badge.plus")
        }
        .onChange(of: selection) { _, items in
            guard !items.isEmpty else { return }
            Task {
                var paths: [String] = []
                for item in items {
                    if let data = try? await item.loadTransferable(type: Data.self),
                       let url = Self.persist(data) {
                        paths.append(url)
                    }
                }
                selection = []
                if !paths.isEmpty { onPicked(paths) }
            }
        }
    }

    private static func persist(_ data: Data) -> String? {
        let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let url = dir.appendingPathComponent(UUID().uuidString + ".jpg")
        do {
            try data.write(to: url)
            return url.absoluteString
        } catch {
            return nil
        }
    }
}

/// Horizontal grid of image thumbnails with optional delete buttons.
struct ImageRowView: View {
    let urls: [String]
    var onDelete: ((Int) -> Void)? = nil

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(Array(urls.enumerated()), id: \.offset) { index, urlString in
                    ZStack(alignment: .topTrailing) {
                        AsyncImage(url: URL(string: urlString)) { image in
                            image.resizable().scaledToFill()
                        } placeholder: {
                            Color.secondary.opacity(0.2)
                        }
                        .frame(width: 100, height: 100)
                        .clipShape(RoundedRectangle(cornerRadius: 8))

                        if let onDelete {
                            Button { onDelete(index) } label: {
                                Image(systemName: "xmark.circle.fill")
                                    .foregroundStyle(.white, .black.opacity(0.6))
                            }
                            .padding(4)
                        }
                    }
                }
            }
        }
    }
}
