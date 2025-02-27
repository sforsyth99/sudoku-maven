# Sudoku Java App

Welcome to a small project that lets you play Sudoku in a standalone Java app.

<div style="text-align:center;">
  <img src="https://i.imgur.com/JootK7X.png" alt="Sudoku Puzzle" height="400">
</div>

### Project status

This app is very much a work in progress. At this point, the game only has a limited number of hardcoded Sudoku puzzles, 
and it's missing features like an on-screen keyboard, hints, a timer, and the ability to choose difficulty level.

### To build:

This is a Maven project written in Java. The main class is Sudoku.java.

## Playing Sudoku 
The goal of the game is to fill in the grid so that every row, column, and 3x3 block contains the digits 1 through 9.
Some of the numbers are provided, and you have to figure out the rest.


### To start playing:

The game starts by displaying a random sudoku puzzle. 
If you would like to play a different game, click
_New Puzzle_. If you want to see the solution, click _Solve Puzzle_. 
Solving the puzzle exercises the solver algorithms that will later
become part of the Sudoku generator, so it can be a bit slow.

Games begin in _Auto Pencil Marks_ mode, which means that when 
you make a guess, the small numbers in the empty cells
are updated to show the possible solutions. If you don't want to see pencil marks, you can uncheck
_Auto Pencil Marks_.

#### To make a guess:
* Ensure that _Pencil Mark Input Mode_ is unchecked.
* Click the cell where you want to make a guess and then press a number from 1 - 9 on the keyboard.

#### To clear a guess:
* Ensure that _Pencil Mark Input Mode_ is unchecked.
* Click the cell and press space, return, delete, or 0.

#### To toggle a pencil mark:
* Ensure that _Pencil Mark Input Mode_ is checked.
* Click the cell that contains the pencil mark, and then press a number from 1 - 9 on the keyboard.

#### To turn off pencil marks
* Uncheck _Auto Pencil Marks_. Only manually-entered pencil marks are then shown.

## Next steps:

I made this project to start brushing up on my Java skills and wanted
to release the MVP before I finished the project. Ultimately I'll convert this project to a web application
becuase most people would probably rather play Sudoku online.

Here are some changes I'd like to make:

* Soliving the puzzle is slow. It would probably make more sense to provide the solution along with the original Sudoku puzzle, and use a different process to generate sudoku puzzles. 
* This project uses the Swing for the UI (I know, I know...) I would update it to use JavaFX, but I I'm going to convert this to a web app, so probably won't bother. 
* The game only contains a few hard-coded Sudoku puzzles. I could use a Sudoku generator to create more.
* Add an on-screen keyboard, timer, hints, and a way to choose the difficulty level.
