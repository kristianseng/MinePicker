package com.codegym.games.minesweeper;

import com.codegym.engine.cell.Color;
import com.codegym.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;




    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private static final String EMPTY = "";


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if(isGameStopped) {
            restart();
        } else {
            openTile(x, y);
        }
    }


    @Override
    public void onMouseRightClick(int x, int y) {
        if(!isGameStopped) {
            markTile(x,y);
        }
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x, y, "");
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }


    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1 ; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1 ; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (x == gameObject.x && y == gameObject.y) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                GameObject gameObject = gameField[y][x];

                if (!gameObject.isMine) {
                    gameObject.countMineNeighbors = Math.toIntExact(getNeighbors(gameObject).stream().filter(neighbor -> neighbor.isMine).count());
                }
            }
        }
    }

    private void openTile(int x, int y) {
        GameObject gameObject = gameField[y][x];
        if(gameObject.isOpen || isGameStopped || gameObject.isFlag) {
            return;
        }
        if (gameObject.isMine) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        } else if (!gameObject.isMine && gameObject.countMineNeighbors == 0) {
            setCellValue(gameObject.x, gameObject.y, EMPTY);
        } else {
            setCellNumber(x, y, gameObject.countMineNeighbors);
        }
        gameObject.isOpen = true;
        if(!gameObject.isMine) score += 5;
        setScore(score);
        countClosedTiles--;
        setCellColor(x, y, Color.GREEN);



        if(gameObject.countMineNeighbors ==0 & !gameObject.isMine) {
            List<GameObject> neighbors = getNeighbors(gameObject);
            for(GameObject object : neighbors) {
                if(!object.isOpen){
                    openTile(object.x, object.y);
                }
            }
        }

        if((countClosedTiles == countMinesOnField) && !gameObject.isMine) {
            win();
        }
    }

    private void markTile (int x, int y) {
        GameObject gameObject = gameField[y][x];
        if((countFlags == 0 && !gameObject.isFlag) || gameObject.isOpen) {
            return;
        }
        if(gameObject.isFlag) {
            setCellValue(x, y, "");
            setCellColor(x, y, Color.ORANGE);
            countFlags++;
        } else {
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
            countFlags--;
        }
        gameObject.isFlag = !gameObject.isFlag;
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.GREY, "You lost, try again", Color.RED,40);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.GREY, "Congrats, you won!", Color.GOLD, 40);
    }

    private void restart() {
        isGameStopped = false;
        countMinesOnField = 0;
        countFlags = 0;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(0);
        createGame();
    }
}
