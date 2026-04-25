package EtherHack.Ether;

import EtherHack.utils.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EtherLuaCompiler {
    private static EtherLuaCompiler instance;
    public boolean isBlockCompileDefaultLua = false;
    public boolean isBlockCompileLuaAboutEtherHack = false;
    public boolean isBlockCompileLuaWithBadWords = false;
    public ArrayList<String> whiteListPathCompiler = new ArrayList();
    public ArrayList<String> blackListWordsEtherUICompiler = new ArrayList();
    public String[] blackListWordsCompiler = new String[]{"logExploit", "LogExtender", "ISLogSystem", "writeLog", "sendLog", "PARP", "Bikinitools", "AVCS", "BTSE", "AntiCheat", "ISPerkLog", "getCore():quitToDesktop()", "KickPlayer", "kickPlayer", "playerKick", "PlayerKick", "banPlayer", "PlayerBan", "playerBan", "AnTiCheat"};
    public String[] stopDefaultLuaCompile = new String[]{"ISPerkLog"};

    public boolean isShouldLuaCompile(String filePath) {
        for (String pathElement : this.whiteListPathCompiler) {
            if (!pathElement.equals(filePath)) continue;
            return true;
        }
        String content = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));){
            String line;
            StringBuilder contentBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            content = contentBuilder.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (this.isBlockCompileLuaAboutEtherHack) {
            for (String blackListWord : this.blackListWordsEtherUICompiler) {
                if (!content.contains(blackListWord) || !filePath.toLowerCase().contains("mod")) continue;
                Logger.printLog("File '" + filePath + "' is not allowed to compile. Contains the word: '" + blackListWord + "'");
                return false;
            }
        }
        if (this.isBlockCompileLuaWithBadWords) {
            for (String word : this.blackListWordsCompiler) {
                if (content.contains(word) && filePath.toLowerCase().contains("mod")) {
                    Logger.printLog("File '" + filePath + "' is not allowed to compile. Contains the word: '" + word + "'");
                    return false;
                }
                if (!filePath.toLowerCase().contains("mod") || !filePath.toLowerCase().contains(word.toLowerCase())) continue;
                Logger.printLog("File '" + filePath + "' is not allowed to compile. Contains the word in the file name: '" + word + "'");
                return false;
            }
        }
        if (this.isBlockCompileDefaultLua) {
            for (String word : this.stopDefaultLuaCompile) {
                if (!filePath.toLowerCase().contains(word.toLowerCase())) continue;
                Logger.printLog("File '" + filePath + "' is not allowed to compile. This is a standard logger - disable it!");
                return false;
            }
        }
        return true;
    }

    public void addWordToBlacklistLuaCompiler(String element) {
        if (this.blackListWordsEtherUICompiler.contains(element)) {
            return;
        }
        this.blackListWordsEtherUICompiler.add(element);
    }

    public void addPathToWhiteListLuaCompiler(String element) {
        if (this.whiteListPathCompiler.contains(element)) {
            return;
        }
        this.whiteListPathCompiler.add(element);
    }

    public void init() {
        Logger.printLog("Initializing EtherLuaCompiler...");
    }

    public static EtherLuaCompiler getInstance() {
        if (instance == null) {
            instance = new EtherLuaCompiler();
        }
        return instance;
    }
}
