# SecondChat
A Minecraft mod that adds a second chat window with regex filtering for messages.

## Features
- Filters chat messages using REGEX or CONTAINS rules, configured in `config/secondchat.json`.
- Displays filtered messages in a separate HUD, with customizable position and appearance via `config/secondchat_hud.json`.
- Supports Minecraft 1.19.4 with Fabric.

## Installation
1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.19.4.
2. Download the latest `SecondChat` mod JAR from the [Releases](https://github.com/zeadn/Secondchat/releases) page (or build from source using `./gradlew build`).
3. Place the JAR in your Minecraft `mods` folder.
4. Configure filter rules in `config/secondchat.json` and HUD settings in `config/secondchat_hud.json`.

## Configuration
The mod uses two configuration files in the Minecraft `config` directory:

### `secondchat.json`
- Defines filter rules to determine which chat messages appear in the second chat HUD.
- Format: JSON array of rule objects with `type` (`REGEX` or `CONTAINS`) and `value` (the pattern or string to match).
- Example:
  ```json
  [
    {"type": "REGEX", "value": "hello.*"},
    {"type": "CONTAINS", "value": "welcome"}
  ]
  ```
- Edit this file to add or modify rules, then reload the game or use the in-game config screen (via ModMenu).

### `secondchat_hud.json`
- Configures the HUD’s position and appearance.
- Fields:
  - `xOffset`: Horizontal offset from the right side of the window (negative to move left, e.g., `-360`).
  - `y`: Vertical position from the top (e.g., `490`).
  - `width`: Width of the HUD background (e.g., `200`).
  - `backgroundColor`: Background color as a signed 32-bit integer in ARGB format (Alpha, Red, Green, Blue).
- Example:
  ```json
  {
    "xOffset": -360,
    "y": 490,
    "width": 200,
    "backgroundColor": -2147483648
  }
  ```
- **Formatting `backgroundColor`**:
  - The `backgroundColor` is a signed integer representing an ARGB color (8 bits each for Alpha, Red, Green, Blue).
  - **Alpha**: Controls transparency (0 = fully transparent, 255 = fully opaque).
  - **Red, Green, Blue**: Color components (0–255 each).
  - To set a color, use a hexadecimal ARGB value (e.g., `#AARRGGBB`) and convert it to a signed integer:
    - `#80000000` (semi-transparent black, alpha=128) → `-2147483648`
    - `#FF000000` (opaque black, alpha=255) → `-16777216`
    - `#80FF0000` (semi-transparent red, alpha=128) → `-2139095040`
    - `#80FFFFFF` (semi-transparent white, alpha=128) → `-2130706433`
  - Conversion tools:
    - Use a programming language (e.g., Python: `int("0x80000000", 16) - 2**32`).
    - Online hex-to-decimal converters (ensure 32-bit signed output).
    - Example: For `#80FFFFFF`, enter `-2130706433` in `secondchat_hud.json`.
  - Default: `-2147483648` (`#80000000`, semi-transparent black).

## In-Game Configuration
- Requires the [ModMenu](https://modrinth.com/mod/modmenu) mod (optional, version >=6.3.1).
- Access the config screen via the ModMenu in the game’s main menu to manage filter rules interactively.
- Note: HUD settings (`secondchat_hud.json`) must be edited manually in the file.

## Building from Source
1. Clone the repository:
   ```bash
   git clone https://github.com/zeadn/SecondChat.git
   ```
2. Navigate to the project directory and build:
   ```bash
   ./gradlew build
   ```
3. Find the compiled JAR in `build/libs/`.

## License
MIT License
