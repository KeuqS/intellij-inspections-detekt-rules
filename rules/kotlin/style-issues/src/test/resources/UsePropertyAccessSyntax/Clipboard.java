import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Clipboard {
    private String clip;
    public @Nullable String getClip() {
        return clip;
    }
    public void setClip(@NotNull String clip) {
        this.clip = clip;
    }
}
