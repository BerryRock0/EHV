package EtherHack;

import EtherHack.Main;
import EtherHack.utils.Logger;
import EtherHack.utils.Patch;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Comparator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.objectweb.asm.tree.*;

public class GamePatcher
{
    private final String[] patchFiles = new String[]{"GameWindow.class", "inventory/ItemContainer.class", "Lua/LuaEventManager.class", "Lua/LuaManager.class"};
    private final String gameClassFolder = "zombie";
    private final String whiteListPathEtherFiles = "EtherHack";

    public void extractEtherHack()
    {
        try {
            String jarFilePath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            Path currentDirectory = Paths.get(System.getProperty("user.dir"), new String[0]);
            try (JarFile jarFile = new JarFile(jarFilePath);){
                jarFile.stream().filter(entry -> entry.getName().startsWith("EtherHack")).forEach(entry -> {
                    block9: {
                        try {
                            Path extractPath = currentDirectory.resolve(entry.getName());
                            if (entry.isDirectory()) {
                                Files.createDirectories(extractPath, new FileAttribute[0]);
                                break block9;
                            }
                            Files.createDirectories(extractPath.getParent(), new FileAttribute[0]);
                            try (InputStream inputStream = jarFile.getInputStream((ZipEntry)entry);){
                                Files.copy(inputStream, extractPath, StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Logger.print("Extraction completed successfully");
            }
        }
        catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void uninstallEtherHackFiles()
    {
        Logger.print("Deleting all EtherHack files...");
        try {
            Path currentDirectory = Paths.get(System.getProperty("user.dir"), new String[0]);
            Path targetPath = currentDirectory.resolve("EtherHack");
            if (Files.exists(targetPath, new LinkOption[0])) {
                Files.walk(targetPath, new FileVisitOption[0]).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
            Logger.print("Deletion EtherHack files completed successfully");
        }
        catch (IOException except) {
            except.printStackTrace();
        }
    }

    public void backupGameFiles()
    {
        Path currentPath = Paths.get("", new String[0]).toAbsolutePath();
        for (int i = 0; i < this.patchFiles.length; ++i) {
            String iteration = "[" + (i + 1) + "/" + this.patchFiles.length + "]";
            Logger.print("Creating a backup file '" + this.patchFiles[i] + "' " + iteration);
            Path originalFilePath = Paths.get(currentPath.toString(), "zombie", this.patchFiles[i]);
            if (Files.exists(originalFilePath, new LinkOption[0])) {
                try {
                    Path backupFilePath = Paths.get(originalFilePath + ".bkup", new String[0]);
                    if (Files.exists(backupFilePath, new LinkOption[0])) {
                        Logger.print("Backup of the file already exists. Skipping backup.");
                        continue;
                    }
                    Files.copy(originalFilePath, backupFilePath, new CopyOption[0]);
                }
                catch (IOException e) {
                    Logger.print("Error while creating backup file: " + e.getMessage());
                }
                continue;
            }
            Logger.print(this.patchFiles[i] + " file not found.");
        }
        Logger.print("Backups of game files have been completed!");
    }

    public void patchGameWindow()
    {
        Patch.injectIntoClass("zombie/GameWindow", "init", true, method -> {
            AbstractInsnNode insertionPoint = null;
            for (AbstractInsnNode insn : method.instructions.toArray()) {
                MethodInsnNode methodInsn;
                if (!(insn instanceof MethodInsnNode) || (methodInsn = (MethodInsnNode)insn).getOpcode() != 184 || !methodInsn.owner.equals("zombie/Lua/LuaManager") || !methodInsn.name.equals("init")) continue;
                insertionPoint = insn;
                break;
            }
            if (insertionPoint == null) {
                throw new IllegalStateException("Cannot find LuaManager.init() invocation in the method when patching the Game window");
            }
            InsnList initEtherLuaInstructions = new InsnList();
            initEtherLuaInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherLuaCompiler", "getInstance", "()LEtherHack/Ether/EtherLuaCompiler;", false));
            initEtherLuaInstructions.add(new MethodInsnNode(182, "EtherHack/Ether/EtherLuaCompiler", "init", "()V", false));
            method.instructions.insert(insertionPoint, initEtherLuaInstructions);
            AbstractInsnNode lastInsn = method.instructions.getLast();
            if (lastInsn == null) {
                throw new IllegalStateException("Could not find the end of the method when patching the Game window");
            }
            InsnList initEtherInstructions = new InsnList();
            initEtherInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            initEtherInstructions.add(new MethodInsnNode(182, "EtherHack/Ether/EtherMain", "init", "()V", false));
            method.instructions.insertBefore(lastInsn, initEtherInstructions);
        });
    }

    public void patchItemContainer()
    {
        Patch.injectIntoClass("zombie/inventory/ItemContainer", "getWeight", false, method -> {
            InsnList newInstructions = new InsnList();
            LabelNode carryOnLabel = new LabelNode();
            newInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            newInstructions.add(new JumpInsnNode(198, carryOnLabel));
            newInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            newInstructions.add(new FieldInsnNode(180, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
            newInstructions.add(new JumpInsnNode(198, carryOnLabel));
            newInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            newInstructions.add(new FieldInsnNode(180, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
            newInstructions.add(new FieldInsnNode(180, "EtherHack/Ether/EtherAPI", "isUnlimitedCarry", "Z"));
            newInstructions.add(new JumpInsnNode(153, carryOnLabel));
            newInstructions.add(new InsnNode(3));
            newInstructions.add(new InsnNode(172));
            newInstructions.add(carryOnLabel);
            method.instructions.insert(newInstructions);
        });
        Patch.injectIntoClass("zombie/inventory/ItemContainer", "getCapacityWeight", false, method -> {
            InsnList newInstructions = new InsnList();
            LabelNode carryOnLabel = new LabelNode();
            newInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            newInstructions.add(new JumpInsnNode(198, carryOnLabel));
            newInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            newInstructions.add(new FieldInsnNode(180, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
            newInstructions.add(new JumpInsnNode(198, carryOnLabel));
            newInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            newInstructions.add(new FieldInsnNode(180, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
            newInstructions.add(new FieldInsnNode(180, "EtherHack/Ether/EtherAPI", "isUnlimitedCarry", "Z"));
            newInstructions.add(new JumpInsnNode(153, carryOnLabel));
            newInstructions.add(new InsnNode(11));
            newInstructions.add(new InsnNode(174));
            newInstructions.add(carryOnLabel);
            method.instructions.insert(newInstructions);
        });
        Patch.injectIntoClass("zombie/inventory/ItemContainer", "getContentsWeight", false, method -> {
            InsnList newInstructions = new InsnList();
            LabelNode carryOnLabel = new LabelNode();
            newInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            newInstructions.add(new JumpInsnNode(198, carryOnLabel));
            newInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            newInstructions.add(new FieldInsnNode(180, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
            newInstructions.add(new JumpInsnNode(198, carryOnLabel));
            newInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            newInstructions.add(new FieldInsnNode(180, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
            newInstructions.add(new FieldInsnNode(180, "EtherHack/Ether/EtherAPI", "isUnlimitedCarry", "Z"));
            newInstructions.add(new JumpInsnNode(153, carryOnLabel));
            newInstructions.add(new InsnNode(11));
            newInstructions.add(new InsnNode(174));
            newInstructions.add(carryOnLabel);
            method.instructions.insert(newInstructions);
        });
    }

    public void patchLuaEventManager()
    {
        Patch.injectIntoClass("zombie/Lua/LuaEventManager", "triggerEvent", true, method -> {
            InsnList toInject = new InsnList();
            toInject.add(new VarInsnNode(25, 0));
            toInject.add(new MethodInsnNode(184, "EtherHack/utils/EventSubscriber", "invokeSubscriber", "(Ljava/lang/String;)V", false));
            method.instructions.insertBefore(method.instructions.get(0), toInject);
        });
    }

    public void patchLuaManager()
    {
        Patch.injectIntoClass("zombie/Lua/LuaManager", "RunLua", true, method -> {
            if (!method.desc.equals("(Ljava/lang/String;Z)Ljava/lang/Object;")) {
                return;
            }
            InsnList newInstructions = new InsnList();
            LabelNode endOfMethodLabel = new LabelNode();
            newInstructions.add(new MethodInsnNode(184, "EtherHack/Ether/EtherLuaCompiler", "getInstance", "()LEtherHack/Ether/EtherLuaCompiler;", false));
            newInstructions.add(new VarInsnNode(25, 0));
            newInstructions.add(new MethodInsnNode(182, "EtherHack/Ether/EtherLuaCompiler", "isShouldLuaCompile", "(Ljava/lang/String;)Z", false));
            newInstructions.add(new JumpInsnNode(154, endOfMethodLabel));
            newInstructions.add(new InsnNode(1));
            newInstructions.add(new InsnNode(176));
            newInstructions.add(endOfMethodLabel);
            method.instructions.insert(newInstructions);
        });
    }

    public boolean checkInjectedAnnotations()
    {
        return Arrays.stream(this.patchFiles).anyMatch(filePath -> Patch.isInjectedAnnotationPresent(filePath, "zombie"));
    }

    public boolean isGameFolder()
    {
        Path gameFolderPath = Paths.get("zombie", new String[0]);
        if (Files.exists(gameFolderPath, new LinkOption[0]) && Files.isDirectory(gameFolderPath, new LinkOption[0])) {
            return Arrays.stream(this.patchFiles).allMatch(fileName -> Files.exists(gameFolderPath.resolve((String)fileName), new LinkOption[0]));
        }
        return false;
    }

    public void patchGame() {
        Logger.printCredits();
        Logger.print("Preparing to install the EtherHack...");
        if (!this.isGameFolder()) {
            Logger.print("No game files were found in this directory. Place the cheat in the root folder of the game");
            return;
        }
        Logger.print("Checking for injections in game files");
        if (this.checkInjectedAnnotations()) {
            Logger.print("Signs of interference were found in the game files. If you have installed this cheat before, run it with the '--uninstall' flag. Otherwise, check the integrity of the game files via Steam");
            return;
        }
        Logger.print("No signs of injections were found. Preparing for backup...");
        this.backupGameFiles();
        Logger.print("Preparation for injection into game file...");
        this.patchGameWindow();
        this.patchItemContainer();
        this.patchLuaEventManager();
        this.patchLuaManager();
        Patch.saveModifiedClasses();
        Logger.print("The injections were completed!");
        Logger.print("Extracting EtherHack files to the current directory...");
        this.extractEtherHack();
        Logger.print("The cheat installation is complete, you can enter the game!");
    }

    public void restoreFiles()
    {
        Logger.printCredits();
        Logger.print("Restoring files...");
        Path currentPath = Paths.get("", new String[0]).toAbsolutePath();
        for (int i = 0; i < this.patchFiles.length; ++i) {
            String fileName = this.patchFiles[i];
            String iteration = "[" + (i + 1) + "/" + this.patchFiles.length + "]";
            Logger.print("Restoring the file '" + fileName + "' " + iteration);
            Path originalFilePath = Paths.get(currentPath.toString(), "zombie", this.patchFiles[i]);
            Path backupFilePath = Paths.get(originalFilePath.toString() + ".bkup", new String[0]);
            if (Files.exists(backupFilePath, new LinkOption[0])) {
                try {
                    if (Files.exists(originalFilePath, new LinkOption[0])) {
                        Files.delete(originalFilePath);
                    }
                    Files.move(backupFilePath, originalFilePath, new CopyOption[0]);
                }
                catch (IOException e) {
                    Logger.print("Error when restoring the game file '" + fileName + "': " + e.getMessage());
                }
                continue;
            }
            Logger.print("Backup file '" + fileName + ".bkup' not found. Skipping restore");
        }
        Logger.print("Files restoration completed!");
        this.uninstallEtherHackFiles();
    }
}
