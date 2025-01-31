# Dungeon Board
A board game map viewer for DMs and GMs to show players maps/dungeons without spoiling too much


## Changes since forking from McAJBen
- Sorts displays from left to right based on desktop alignment.
- Has Folder-Open Button at upper left of window (opens to resource folder).
- The Paint window now allows the zoom-area center to be all the way to the edge of the window.
  - (Allows centering the displayed screen directly over
    a part of the map that previously would have been at the edge ofthe displayed area).


<a href="https://github.com/McAJBen/DungeonBoard#examples">Examples</a><br>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://github.com/McAJBen/DungeonBoard#paint-utility">- Paint Utility</a><br>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://github.com/McAJBen/DungeonBoard#layer-utility">- Layer Utility</a><br>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://github.com/McAJBen/DungeonBoard#image-utility">- Image Utility</a><br>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://github.com/McAJBen/DungeonBoard#loading-utility">- Loading Utility</a><br>
<a href="https://github.com/McAJBen/DungeonBoard#controls">Controls</a><br>
<a href="https://github.com/McAJBen/DungeonBoard#running-dungeon-board">Running Dungeon Board</a>
## Examples
The following examples will show different use cases for the application.
### Paint Utility
Painting onto a map to reveal known areas to players.
This is the Control Window, it will be only shown to the DM so they can edit behind the scenes.
<br>
<img src="Examples/control0.png" alt="Controls" width="300" height="300">
<br>
This is the Display Window, it is what the players see on the screen.
Notice how only part of the map is displayed to the players, the DM can use the controls to change what they want the players to see.
In this example you can reveal more of the map as the players talk to NPCs and learn more about their world.
<br>
<img src="Examples/view0.png" alt="View" width="300" height="300">
<br>
Here the DM is using the paint function to reveal sections of a dungeon.
This is the most likely use case for the paint function, since it is hard to predict what they players will do in a dungeon.
Being able to dynamically show and hide sections quickly is the main purpose of the painting ability.
<br>
<img src="Examples/control2.png" alt="Controls" width="300" height="300">
<img src="Examples/view2.png" alt="View" width="300" height="300">
<br>
### Layer Utility
Unlike the paint ability this allows the DM to overlay different pictures on top of each other.
The best use case would be for a world map, where you can apply layers that show hidden information not shown on the original map.
In this example the base map is displayed first, then the DM can choose to tell the players of specific areas like the Blacksmith or the Burial Grounds.
<br>
<img src="Examples/control3.png" alt="Controls" width="300" height="300">
<img src="Examples/view3.png" alt="View" width="300" height="300">
<br>
### Image Utility
This is similar to the Layer Utility, but will only show one image at a time.
This is useful when you want to simply use a screen to display a picture.
<br>
<img src="Examples/control4.png" alt="Controls" width="300" height="300">
<img src="Examples/view4.png" alt="View" width="300" height="300">
<br>
### Loading Utility
This ability is useful when the DM needs to kill some time.
The display will alternate randomly between all images in a directory.
The main use case is for the DM to show 'loading tips' to distract the players and also teach them about things they might not know they can do.<br>
<a href="http://imgur.com/a/GB9kA">Example Loading texts</a>
## Controls
<img src="Examples/guide.png" alt="Guide"><br>
## Running Dungeon Board
<a href="https://github.com/grangerx/DungeonBoard/raw/master/Versions/DungeonBoard-grangerx-20190922.jar">Download DungeonBoard--grangerx-20190922 (based on v2.4.1)</a>
<br>
<i>Caution, web browsers do not like .jar files. They can be used to give viruses. Do your research before downloading (Don't take my word for it).</i>
<br>
When you first run Dungeon Board it will create a folder next to the .jar file. Inside of this are 4 folders (Layer, Image, Paint, Loading). Simply place all of your images you want to display in these folders in the .png format.
The next time you run Dungeon Board it will automatically load these on startup.
<br><br>
If you want to run Dungeon Board with more memory allocated you have to run the .jar file from the command line.
<br>
<i>&nbsp;&nbsp;java -jar -Xmx1000m "DungeonBoard-grangerx-20190922.jar"</i>
