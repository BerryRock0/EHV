package EtherHack.Ether;

import EtherHack.utils.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.Translator;

public class EtherTranslator {
    private static final String TRANSLATIONS_PATH = "EtherHack/translations";
    private Map<String, Map<String, String>> translations;

    public EtherTranslator() {
        Logger.printLog((String)"Initializing EtherTranslator...");
        this.translations = new HashMap<String, Map<String, String>>();
    }

    public void loadTranslations() {
        File translationsDir = new File(TRANSLATIONS_PATH);
        File[] translationFiles = translationsDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (translationFiles == null) {
            Logger.printLog((String)"Failed to load translations: no files found.");
            return;
        }
        for (File file : translationFiles) {
            String languageCode = file.getName().replace(".txt", "");
            HashMap<String, String> languageTranslations = new HashMap<String, String>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file));){
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts;
                    if (line.trim().isEmpty() || !line.contains("=") || (parts = line.split("=", 2)).length < 2) continue;
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (value.endsWith(",")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    value = value.replaceAll("\"", "");
                    languageTranslations.put(key, value);
                }
            }
            catch (Exception e) {
                Logger.printLog((String)("Failed to load translation file: " + file.getName()));
                e.printStackTrace();
            }
            this.translations.put(languageCode, languageTranslations);
        }
    }

    public String getTranslate(String translationKey) {
        return this.getTranslate(translationKey, null);
    }

    public String getTranslate(String translationKey, KahluaTable args) {
        String translation;
        if (translationKey == null) {
            Logger.printLog((String)"The translation key value was not obtained!");
            return "???";
        }
        String languageCode = Translator.getLanguage().name();
        Map<String, String> languageTranslations = this.translations.get(languageCode);
        if (languageTranslations == null) {
            Logger.printLog((String)("No translations for language code: " + languageCode));
            languageTranslations = this.translations.get("EN");
            if (languageTranslations == null) {
                return translationKey;
            }
        }
        if ((translation = languageTranslations.get(translationKey)) == null) {
            Logger.printLog((String)("No translation for key: " + translationKey + " for language: " + languageCode));
            return translationKey;
        }
        if (args != null && !args.isEmpty()) {
            KahluaTableIterator iterator = args.iterator();
            while (iterator.advance()) {
                String key = iterator.getKey().toString();
                String value = iterator.getValue().toString();
                translation = translation.replace("{" + key + "}", value);
            }
        }
        translation = translation.replace("<br>", "\n");
        return translation;
    }
}
