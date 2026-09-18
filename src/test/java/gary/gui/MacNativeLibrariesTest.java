package gary.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MacNativeLibrariesTest {
    @Test
    void resourceDirectory_supportedArchitectures_selectsMatchingLibraries() {
        assertEquals("/natives/mac-aarch64/", MacNativeLibraries.resourceDirectory("aarch64"));
        assertEquals("/natives/mac-aarch64/", MacNativeLibraries.resourceDirectory("arm64"));
        assertEquals("/natives/mac-x86_64/", MacNativeLibraries.resourceDirectory("x86_64"));
        assertEquals("/natives/mac-x86_64/", MacNativeLibraries.resourceDirectory("AMD64"));
    }

    @Test
    void resourceDirectory_unsupportedArchitecture_rejectsInsteadOfLoadingWrongLibraries() {
        assertThrows(IllegalArgumentException.class, () -> MacNativeLibraries.resourceDirectory("riscv64"));
    }
}
