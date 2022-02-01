import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles = SIDE * SIDE;
    private int score = 0;
    private static final String MINE = "\uD83D\uDCA9";
    private static final String FLAG = "\uD83D\uDE48";
    private boolean isGameStopped;


    @Override
    public void initialize() {
        // аналог метода main, создаём игровое поле и запускаем игру
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                // выше определяем шанс создать мину
                if (isMine) {
                    countMinesOnField++;
                }
                setCellValue(x, y, "");
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.WHITE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        // на ЛКМ открываем клеточки или перезапуск после проигрыша
        if (isGameStopped) restart();
        else openTile(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        // на ПКМ помечаем клетки как потенц. мины
        markTile(x, y);
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
                // подсчитываем соседей для клетки-аргумента
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (!gameField[i][j].isMine) {
                    for (GameObject gameObject1 : getNeighbors(gameField[i][j])) {
                        if (gameObject1.isMine) gameField[i][j].countMineNeighbors++;
                        // тут определяем количество мин по соседству с клеткой
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (!isGameStopped && !gameField[y][x].isOpen && !gameField[y][x].isFlag) {
            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.HOTPINK, MINE);
                gameOver();
            } else if (!gameField[y][x].isMine) {
                setCellValue(x, y, "");
                setCellColor(x, y, Color.PINK);
                gameField[y][x].isOpen = true;
                score += 5;
                // за успешно открытую клетку добавляем 5 очей
                setScore(score);
                countClosedTiles--;
                if (countClosedTiles == countMinesOnField) win();
                if (gameField[y][x].countMineNeighbors == 0) {
                    for (GameObject g : getNeighbors(gameField[y][x])) {
                        if (!g.isOpen) openTile(g.x, g.y);
                    }
                } else {
                    setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                }
            }
        } else ;
    }

    private void markTile(int x, int y) {
        if (!isGameStopped) {
            GameObject gameObject = gameField[y][x];
            if (gameObject.isOpen) ;
            else if (countFlags == 0 && !gameObject.isFlag) ;
            else if (!gameObject.isFlag) {
                gameObject.isFlag = true;
                --countFlags;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.LIGHTPINK);
            } else {
                gameObject.isFlag = false;
                ++countFlags;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.WHITE);
            }
        } else ;
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "\uD83D\uDE08 Вы наступили в гaвно \uD83D\uDE08", Color.WHITE, 25);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "\uD83D\uDE3D Ваши ботинки \n остались чистыми \uD83D\uDE3D", Color.WHITE, 25);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        createGame();
    }
}