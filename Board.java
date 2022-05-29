import java.util.ArrayList;
import java.util.Random;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


/**
 * Klasa reprezentuje planszę do zmiany kolorów prostokątów.
 */
public class Board
{
    private final GridPane gridPane;
    private final int n;
    private final int m;
    private final int k;
    private final double p;

    private final MyThread threads[][];
    private final Control mainWaiter;
    private final Random randomGenerator;

    public Board(GridPane gridPane, int n, int m, int k, double p, int SQUARE_SIZE, int MARGIN)
    {
        this.gridPane = gridPane;

        this.n = n;
        this.m = m;
        this.k = k;
        this.p = p;

        threads = new MyThread[n][m];
        randomGenerator = new Random();

        mainWaiter = new Control();

        drawSquares(SQUARE_SIZE, MARGIN);
    }

    /**
     * Funkcja zmienia szerokość wszystkich prostokątów na planszy.
     * @param width nowa szerokość prostokąta
     */
    public void setSquareWidth(int width)
    {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                threads[i][j].square.setWidth(width);
    }

    /**
     * Funkcja zmienia wysokość wszystkich prostokątów na planszy.
     * @param height nowa wysokość prostokąta
     */
    public void sertSquareHeight(int height)
    {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                threads[i][j].square.setHeight(height);
    }

    private void drawSquares(int SQUARE_SIZE, int MARGIN)
    {
        int currentX = 0, currentY = 0;
        for (int i = 0; i < n; i++)
        {
            currentX = 0;
            for (int j = 0; j < m; j++)
            {
                threads[i][j] = new MyThread(currentX, currentY, SQUARE_SIZE, SQUARE_SIZE, i, j);
                threads[i][j].setDaemon(true);

                gridPane.add(threads[i][j].square, i, j);
                currentX += SQUARE_SIZE + MARGIN;
            }
            currentY += SQUARE_SIZE + MARGIN;
        }

        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                threads[i][j].start();
    }

    private class Control
    {
        public synchronized int[] getColor(MyThread t)
        {
            Color c;
            synchronized (t) { c = (Color)(t.square.getFill()); }
            int colors[] = {(int)(c.getRed() * 255), (int)(c.getBlue() * 255), (int)(c.getGreen() * 255)};
            return colors;
        }
    }

    private class MyThread extends Thread
    {
        public final int posX;
        public final int posY;
        public final Rectangle square;

        private boolean isLocked;
    
        public MyThread(int x, int y, int width, int height, int posX, int posY)
        {
            super();
            square = new Rectangle(x, y, width, height);
            square.setFill(Color.rgb(randomGenerator.nextInt(256), 
                randomGenerator.nextInt(256), randomGenerator.nextInt(256)));
            this.posX = posX;
            this.posY = posY;

            isLocked = false;

            square.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    isLocked = !isLocked;
                    if (isLocked)
                        square.setFill(Color.WHITE);
                }
            });
        }
    
        @Override
        public void run()
        {
            while (true)
            {
                if (isLocked)
                {
                    try { sleep(50); }
                    catch (final Exception ex) { System.out.println(ex.getMessage()); }
                    continue;
                }

                //System.out.println("B " + posX + "; " + posY);

                if (randomGenerator.nextDouble() < p)
                {
                    square.setFill(Color.rgb(randomGenerator.nextInt(256), 
                    randomGenerator.nextInt(256), randomGenerator.nextInt(256)));
                }
                else
                {
                    int red = 0, green = 0, blue = 0;
                    int count = 0;

                    for (MyThread thread : getNeighbours())
                    {
                        int[] colors = {0, 0, 0};

                        if (!thread.isLocked)
                        {
                            colors = mainWaiter.getColor(thread);
                            count++;
                        }

                        red += colors[0];
                        green += colors[1];
                        blue += colors[2];
                    }

                    if (count > 0)
                    {
                        red /= count;
                        green /= count;
                        blue /= count;
                    }

                    square.setFill(Color.rgb(red, green, blue));
                }

                //System.out.println("E " + posX + "; " + posY);
                
                try { sleep((long)((randomGenerator.nextDouble() + 0.5) * k)); }
                catch (final Exception ex) { System.out.println(ex.getMessage()); }
            }
        }

        private ArrayList<MyThread> getNeighbours()
        {
            ArrayList<MyThread> list = new ArrayList<MyThread>();

            list.add(threads[posX][posY == m - 1 ? 0 : posY + 1]);
            list.add(threads[posX][posY == 0 ? m - 1 : posY - 1]);
            list.add(threads[posX == n - 1 ? 0 : posX + 1][posY]);
            list.add(threads[posX == 0 ? n - 1 : posX - 1][posY]);

            return list;
        }
    }
}
