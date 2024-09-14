package malilib.util.text;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Properties;

import malilib.MaLiLib;
import malilib.MaLiLibReference;
import malilib.util.FileUtils;

public class LanguageManager
{
    public static final LanguageManager INSTANCE = new LanguageManager();

    protected final HashSet<String> modIds = new HashSet<>();
    protected final Properties translations = new Properties();

    protected LanguageManager()
    {
        this.registerMod(MaLiLibReference.MOD_ID);
    }

    public void registerMod(String modId)
    {
        if (this.modIds.add(modId))
        {
            this.loadTranslation(modId);
        }
    }

    protected void loadTranslation(String modId)
    {
        try
        {
            this.translations.load(new InputStreamReader(FileUtils.openModResource(modId, "/lang/en_us.lang"), StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            MaLiLib.LOGGER.error("Exception trying to load translations for mod {}: ", modId, e);
        }
    }

    public void reload()
    {
        this.translations.clear();

        for (String modId : this.modIds)
        {
            this.loadTranslation(modId);
        }
    }

    public boolean hasTranslation(String key)
    {
        return this.translations.containsKey(key);
    }

    public String translate(String key)
    {
        return this.translations.getProperty(key, key);
    }

    public String translate(String key, Object... args)
    {
        String translated = this.translations.getProperty(key, key);
        return String.format(translated, args);
    }
}
