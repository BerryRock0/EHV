<h1 align="center">Project-Zomboid-Ether</h1>

This is a utility client written in Java(API) and LUA(GUI) for Project Zomboid. It is aimed at providing the game with additional functionality that allows users to get some benefits. Please use responsibly and understand the consequences that may arise as a result of improper use.
The performance of the client was tested on the latest version of the game `41.78.16 (Steam)` (July 09, 2023).


### Installation

1. Download and install Java on your computer
2. Make sure that the path to Java is set in your environment variables

(For Windows: `WIN + X` -> `"System"` -> `"Advanced System Parameters"` -> `"Environment Variables"` -> `Double click on "Path"` -> `Insert path to java, for example "C:\Program Files\Java\jdk-17\bin"`)

3. Clone the repository
4. Open the project in the IDE and build the executable via Gradle `.jar` file
5. Move the created `.jar` to the root folder of the game 

(For example, `c:\Steam\steamapps\common\ProjectZomboid`)

6. Open the console in the root folder and run the following command: 

```
java -jar ./Ether-{yourVersion}.jar --install
```

`{yourVersion}` - Specify your version of the client

### Uninstallation
Open the console in the root folder and run the following command:
```
java -jar ./Ether-{yourVersion}.jar --uninstall
```

`{yourVersion}` - Specify your version of the client

## Usage

After successfully installing the client, you need to log in to the game. When loading, the cheat logo will appear in front of the main logo, in the game itself (menu and in the game session) in the lower left corner there will be information about the client. The name of the game window will also change.

To open the client menu, press `Insert`

To reload the LuaGUI, press `Home`, but first make sure that all the client windows are closed, otherwise an error will appear.

## For developer
If you are a developer and want to expand the functionality, you can do it as follows:

In the project folder located on the path `src/main/resources/EtherHack/lua` there is a single file responsible for rendering the entire user interface of the cheat. You can edit it using examples from game files.

Also, Ether provides the ability to add custom methods from Java to lua, for this in the `src/main/java/Ether/Ether/EtherAPI.java` needs to find the `public static class GlobalEtherAPI` and add it according to the available examples:

```java
@LuaMethod(
         name = "yourMethodName",
        global = true
)
public static String yourMethodName() {
    return "Test!";
}
```
After adding and reassembling the client, you will be able to call this method in lua:

```lua
print(yourMethodName());
--Output log: Test!
```
In addition, you can load third-party Lua in any other Lua through the `EtherRequire` method:

```lua
EtherRequire "path/to/your.lua"
```

The path to Lua must be specified relative to the root folder of the game
