package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewmethodisannotatedwithtilepreviewannotation

internal enum class ForeignPreview(val message: String) {
    DIRECT("In order to preview a Tile, the Preview annotation must be from androidx.wear.tiles.tooling.preview"),
    MULTI_PREVIEW(
        "In order to preview a Tile, any Preview annotations referenced from this annotation must be from " +
            "androidx.wear.tiles.tooling.preview",
    ),
}
