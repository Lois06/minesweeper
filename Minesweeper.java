package ch.csbe.minesweeper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/*
 * @author Lois Mula Fervenza
 * 
 */
public class Minesweeper extends Application implements Serializable
{

	
	private static final int TILE_SIZE = 40;
	private static final int W = 800;
	private static final int H = 600;
	
	private static final int X_TILES = W / TILE_SIZE;
	private static final int Y_TILES = H / TILE_SIZE;
	

	private Tile[][] grid = new Tile[X_TILES][Y_TILES];
	private Scene scene;
	
	private int score = 0;
	
	private Text scoreText;
	
	protected Stage score_stage = null;
	protected static Stage mouse_clickStage = null;

	
	@Override
	public void start(Stage primaryStage) throws Exception
	{

		this.score_stage = primaryStage;
		BorderPane shell = new BorderPane();
		shell.setCenter(createContent());
		HBox scorePane = new HBox();
		Label score = new Label("Score: ");
		scoreText = new Text("0");
		scorePane.getChildren().addAll(score, scoreText);
		shell.setTop(scorePane);
		scene = new Scene(shell);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Minesweeper Clone");
		primaryStage.show();

		mouse_clickStage = new Stage();

	}

	private Parent createContent()
	{
		Pane initial = new Pane();
		initial.setPrefSize(W, H);
	
		for (int y = 0; y < Y_TILES; y++)
		{
			for (int x = 0; x < X_TILES; x++)
			{
				Tile tile = new Tile(x, y, Math.random() < 0.2);

				grid[x][y] = tile;
				initial.getChildren().add(tile);

			}
		}
	
		for (int y = 0; y < Y_TILES; y++)
		{
			for (int x = 0; x < X_TILES; x++)
			{
				Tile tile = grid[x][y];

				if (tile.hasBomb)
					continue;

				
				long bombs = getBombs(tile).stream().filter(t -> t.hasBomb).count();

				if (bombs > 0)
				{
					tile.text.setText(String.valueOf(bombs));
					tile.setNeighborBombs(bombs);
				}

			}
		}

		return initial;
	}


	private List<Tile> getBombs(Tile tile)
	{
		List<Tile> potentialBombs = new ArrayList<>();
		
		
		int[] points = new int[]
		{ -1, -1, -1, 0, -1, 1, 0, -1, 0, 1, 1, -1, 1, 0, 1, 1 };

		for (int i = 0; i < points.length; i++)
		{
			int dx = points[i];
			int dy = points[++i];

			int newX = tile.x + dx;
			int newY = tile.y + dy;

	
			if (newX >= 0 && newX < X_TILES && newY >= 0 && newY < Y_TILES)
			{
				potentialBombs.add(grid[newX][newY]);

			}
		}

		return potentialBombs;
	}

	
	private class Tile extends StackPane
	{
		private int x, y;
		private boolean hasBomb;
		private boolean isOpen = false;

		

		private Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
		private Text text = new Text();
		private int neighborBombs;

		public Tile(int x, int y, boolean hasBomb)
		{
			this.x = x;
			this.y = y;
			this.hasBomb = hasBomb;

			border.setStroke(Color.RED);
			
			
			text.setFont(Font.font(18));
			text.setText(hasBomb ? "X" : "");
			text.setVisible(false);

			getChildren().addAll(border, text);

			setTranslateX(x * TILE_SIZE);
			setTranslateY(y * TILE_SIZE);

			setOnMouseClicked(e -> open());
		}

		public void setNeighborBombs(long numBombs)
		{
			neighborBombs = (int) numBombs;

		}

		public void open()
		{
			if (isOpen)
				return;

			if (hasBomb)
			{
				System.out.println("Game Over. " + "Your Score is: " + score);
				scene.setRoot(createContent());
				try
				{
					start(score_stage);
					score = 0;
				} catch (Exception e)
				{
					e.printStackTrace();
				}

				return;
			}

			score = score + neighborBombs * 100;
			scoreText.setText(score + "");
			isOpen = true;
			text.setVisible(true);
			border.setFill(null);

			if (text.getText().isEmpty())
			{
				getBombs(this).forEach(Tile::open);
			}
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}

}