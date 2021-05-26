GIF Test

![test image](https://github.com/bubbledrift/myslippycopy/blob/master/READMEgifs/slippyclip.gif?raw=true)

Miru Yang: B01530353

Alexander Ivanov: B01540800


Final Checkpoint:

Playtesting reports are in projects/PlaytestingReports

Added quite a bit of content to the game.
The story is now cohesive, main menu looks significantly better with a proper background, 
and the in game sounds really bring everything together.

Definitely, more can be added as mentioned in the play tests and there are some strange 
bugs that we were able to find through playing the game ourselves but, this is definitely 
a satisfying stopping point. If we had more time, we would've loved to add more features, areas, characters, attacks,
enemies, etc...

A lot of smaller additions to the game/engine can be found in the commit history.
The most significant of these are:
- the Slippy AI and attacks.
- HUD with HP and weapons
- The Skeleton enemy
- Health potions
- proper end screen

Known Bugs/Unintended Features:

There are a couple places in the game where you can clip past walls by being pushed by an object/enemy.
None of them are game breaking.
It also seems like depending on the computer running the game, the tick size differs, so these bugs/clips are
inconsistent. One bug for Alex was not replicable for Miru, perhaps because of the difference in computers.

------------------------------------------

Third Checkpoint:

Playtesting reports are in projects/PlaytestingReports

We currently have a demo with a couple of more decorated areas and natural elements. There's
also a character with some dialogue options and the beginning of a story.

We added dialogue, a bunch of decorative objects, enemies, and are currently in the process
of making multiple attacks with their own collision components. Additionally, we added music/audio
as well as an HP bar for the player. To help us debug, we added a debug mode which can be turned
on or off by changing the file DebugFlags in Engine/game/DebugFlags.

For the final handin, we hope to add a full story/npcs, more enemies, better combat, more interactivity,
and a cave area. We also want to add some more art in the main menu to make it look nice!

Our engine features from previous weeks can be found below.


------------------------------------------

Second Checkpoint:

Playtesting reports are in projects/PlaytestingReports

We currently have a small playable demo where you start off as a character in a map.
This map has an exit on the right side which takes you to a new map with an enemy.
These maps are built using the TileSystem.

Additionally, there is a black fog-like area you can find in the top right of the maps.
This is just to show our lighting/fog-of-war system which we plan to polish up in future weeks.

The bulk of our engine features for points are done, and we mostly will work on polishing up
these engine features and potentially adding a couple more like dialogue as we work more on 
our game.

Our goal for next week is to have a much more finished and playable game that will be more
interesting for playtesters and isn't just a map or two with not much to interact with.


- Tile System
    - Most Related code is in the TileSystem folder under engine/finalGame/
    - Testing code is in projects/final_project
- Sound Engine
    - Related code is in the Audio Component
    - testing code is in projects/WizTesting/
- Lighting System
    - Most of the related code is in the engine/game/components in the DrawFogComponent and 
    LightComponent classes.
    - There is also some code in engine/game/systems/LightingSystem
    
------------------------------------------

First CheckPoint:

Nothing Playable just yet.

- Tile System
    - Most Related code is in the TileSystem folder under engine/finalGame/
    - Testing code is in projects/final_project
- Sound Engine
    - Related code is in the Audio Component
    - testing code is in projects/WizTesting/
    
There are more features that will be added but we can say we are mostly done (close to the point requirement)



