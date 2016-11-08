# IA 08 Android Doodle Prototype
CMSC434 Fall 2016 | Victor De Souza

__Draw:__ The app opens to a blank canvas ready to draw without any distracting UI. The app already has a default black color and brush size so it is easy to start drawing as soon as the app launches. 

__Undo Redo:__ The two always visible UI is the redo and undo buttons on the Action Bar. This means that once the app is launched and the user starts drawing, the two most used buttons are always accessible without navigating menus.

__Tool Drawer:__ The tool drawer is accessed by tapping the menu button on the left of the Action Bar or by swiping from the left edge of the screen. Here the user can access:
- Brush settings:
  + change brush size with a seekbar
  + change brush opacity with a seekbar
  + change brush color from a 4x4 grid
  + There is live preview of the brush being changed. 
- The Eraser:
  + Selecting the eraser will hide the color grid and the opacity views since they are not needed for erasing. 
  + Choose the size of the erase with a seekbar and see a live preview of the eraser size 
- Fill the background:
  + Fill the background of the canvas (behind already drawn artwork) with the currently selected color.
  + All tools options (color picker, brush size, opacity amount) will be hidden since this is done automatically.
  + An alert dialog will ask for confirmation before filling the background.

__Options Menu:__ An options menu in the Action Bar next to the undo and redo buttons will show two more features:
- Clear All:
  + Clears the artboard and resets the background back to white.
  + Dont worry! There is an Alert Dialog to confirm delete
- Save Artwork:
  + Save the drawing into the phone's gallery app.
