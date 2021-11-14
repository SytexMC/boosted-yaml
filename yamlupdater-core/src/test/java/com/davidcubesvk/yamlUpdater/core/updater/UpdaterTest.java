package com.davidcubesvk.yamlUpdater.core.updater;

import com.davidcubesvk.yamlUpdater.core.YamlFile;
import com.davidcubesvk.yamlUpdater.core.path.Path;
import com.davidcubesvk.yamlUpdater.core.settings.dumper.DumperSettings;
import com.davidcubesvk.yamlUpdater.core.settings.general.GeneralSettings;
import com.davidcubesvk.yamlUpdater.core.settings.loader.LoaderSettings;
import com.davidcubesvk.yamlUpdater.core.settings.updater.UpdaterSettings;
import com.davidcubesvk.yamlUpdater.core.versioning.Pattern;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UpdaterTest {

    // Pattern
    private static final Pattern PATTERN = new Pattern(new Pattern.Part(1, 100), new Pattern.Part("."), new Pattern.Part(0, 10));
    // Settings
    private static final UpdaterSettings UPDATER_SETTINGS = UpdaterSettings.builder().setRelocations(new HashMap<String, Map<Path, Path>>(){{
        put("1.3", new HashMap<Path, Path>(){{
            put(Path.from("z", "a"), Path.from("r"));
        }});
        put("2.3", new HashMap<Path, Path>(){{
            put(Path.fromSingleKey("o"), Path.fromSingleKey("m"));
            put(Path.fromSingleKey("z"), Path.fromSingleKey("s"));
        }});
    }}).setVersioning(PATTERN, "a").build();

    @Test
    void update() {
        // File
        YamlFile file = new YamlFile(
                new ByteArrayInputStream("a: 1.2\ny: true\nz:\n  a: 1\n  b: 15\no: \"a: b\"\np: 50".getBytes(StandardCharsets.UTF_8)),
                new ByteArrayInputStream("a: 2.3\ny: false\ns:\n  a: 5\n  b: 10\nm: \"a: c\"\nr: 20\nt: 100".getBytes(StandardCharsets.UTF_8)),
                GeneralSettings.DEFAULT, LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UPDATER_SETTINGS);
        // Update
        Updater.update(file, file.getDefaults(), file.getUpdaterSettings(), file.getGeneralSettings());
        // Assert
        assertEquals("2.3", file.getString("a", null));
        assertEquals(true, file.get("y", null));
        assertEquals(5, file.get("s.a", null));
        assertEquals(15, file.get("s.b", null));
        assertEquals("a: b", file.get("m", null));
        assertEquals(1, file.get("r", null));
        assertEquals(100, file.get("t", null));
        assertEquals(6, file.getKeys().size());
        assertEquals(2, file.getSection("s").getKeys().size());
    }
}