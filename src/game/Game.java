package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;

import game2D.*;

/**
 * 
 * @author Max Ritchie - 2636157
 *
 */
public class Game extends GameCore {

	// Game constants
	private static int screenWidth = 750;
	private static int screenHeight = 500;

	private float gravity = 0.0005f;

	private Image backgroundImageOne = null;
	private Image backgroundImageTwo = null;
	private Image backgroundImageThree = null;

	private Image deathMessage = null;

	private TileMap tileMap = new TileMap();

	private Animation idleRight = null;
	private Animation idleLeft = null;
	private Animation walkingRight = null;
	private Animation walkingLeft = null;
	private Animation jumpingRight = null;
	private Animation jumpingLeft = null;

	private Animation flyingRight = null;
	private Animation flyingLeft = null;

	private Sprite player = null;
	private Sprite enemyOne = null;
	private Sprite enemyTwo = null;

	private Music backgroundMusic = null;;

	private Sound playerDeathSound = null;
	private Sound batDeathSound = null;
	private Sound coinPickUpSound = null;

	private boolean walkForward = false;
	private boolean walkBackward = false;
	private boolean jumping = false;
	private boolean falling = true;
	private boolean endGame = false;
	private boolean collided = false;
	private boolean playerCanBeKilled = false;

	private int playerHeight;

	private int jumps = 0;
	private int playerLives = 3;
	private int playerScore = 0;

	private int playerSpawnX = 150;
	private int playerSpawnY = 160;

	private int backgroundOne = 0;
	private int backgroundTwo = 0;
	private int backgroundThree = 0;

	private int xo = 0;
	private int yo = 0;

	private int xClicked = 0;
	private int yClicked = 0;

	private int mouseButton = 0;

	private int keyPressed = 0;

	private int level = 1;

	private int tileTop = 0;
	private int tileBottom = 0;
	private int tileRightHandSide = 0;
	private int tileLeftHandSide = 0;

	private int deathMenuWidth = 400;
	private int deathMenuHeight = 150;

	private int middleOfScreenWidth = screenWidth / 2;
	private int middleOfScreenHeight = screenHeight / 2;

	private char tileTopRight = 0;
	private char tileTopLeft = 0;
	private char tileBottomRight = 0;
	private char tileBottomLeft = 0;

	private Font deathMenuFont = null;
	private Font scoreBoardFont = null;

	private Rectangle retryButton = null;
	private Rectangle exitButton = null;

	/**
	 * Creates an instance of the class and starts running it
	 * 
	 * @param args List of parameters that are not used
	 */
	public static void main(String[] args) {
		// Creates and initialises a new game object
		Game newGame = new Game();
		// Sets the window unable to be resized
		newGame.setResizable(false);
		// Calls the init method using the game object and passes in the starting tile
		// map
		newGame.init("map.txt");
		// Starts the game by first initialising the game via init and then calling
		// the gameLoop
		newGame.run(false, screenWidth, screenHeight);
	}

	public void init(String mapFile) {
		// Initialises and loads in the passed in tile map
		tileMap.loadMap("maps", mapFile);

		// Initialises and loads a complete animation from an animation sheet and adds
		// each frame in the sheet to the animation with the given frameDuration for the
		// players sprite
		idleRight = new Animation();
		idleRight.loadAnimationFromSheet("images/wizardIdleRight.png", 3, 1, 75);

		idleLeft = new Animation();
		idleLeft.loadAnimationFromSheet("images/wizardIdleLeft.png", 3, 1, 75);

		walkingRight = new Animation();
		walkingRight.loadAnimationFromSheet("images/wizardWalkingRight.png", 4, 1, 20);

		walkingLeft = new Animation();
		walkingLeft.loadAnimationFromSheet("images/wizardWalkingLeft.png", 4, 1, 20);

		jumpingRight = new Animation();
		jumpingRight.loadAnimationFromSheet("images/wizardjumpingRight.png", 4, 1, 20);

		jumpingLeft = new Animation();
		jumpingLeft.loadAnimationFromSheet("images/wizardjumpingLeft.png", 4, 1, 20);

		// Initialises and loads a complete animation from an animation sheet and adds
		// each frame in the sheet to the animation with the given frameDuration for the
		// enemies sprite
		flyingRight = new Animation();
		flyingRight.loadAnimationFromSheet("images/batFlyingRight.png", 5, 1, 125);

		flyingLeft = new Animation();
		flyingLeft.loadAnimationFromSheet("images/batFlyingLeft.png", 5, 1, 125);

		playBackGroundMusic();

		// Initialises a new player sprite object with the specified animation
		player = new Sprite(idleRight);

		// Initialises a new enemy sprite object with the specified animation
		enemyOne = new Sprite(flyingRight);

		// Initialises a new enemy sprite object with the specified animation
		enemyTwo = new Sprite(flyingLeft);

		// Creates and sets the background for the game
		backgroundImageOne = loadImage("images/mountainBackground.jpg");
		backgroundImageTwo = loadImage("images/rockBackground.png");
		backgroundImageThree = loadImage("images/treesBackground.png");

		// Creates and sets the death message
		deathMessage = loadImage("images/deathMessage.png");

		// Initialises the games world
		initialseGame();
	}

	/**
	 * Starts the background music relevant to the level
	 */
	public void playBackGroundMusic() {
		// Passes in the file location for the a sound file and starts playing
		backgroundMusic = new Music();

		// PLays the background music associated with the level that the player is
		// currently on
		if (level == 1) {
			backgroundMusic.playMusic("sounds/backgroundMusic.mid");
		} else if (level == 2) {
			backgroundMusic.playMusic("sounds/backgroundMusicTwo.mid");
		}
	}

	/**
	 * Starts the game in its starting state
	 */
	public void initialseGame() {
		if (!endGame)
		// Sets the spawn point for the player
		player.setX(playerSpawnX);
		player.setY(playerSpawnY);

		// Sets the starting velocity of the player
		player.setVelocityX(0);
		player.setVelocityY(0);

		// Calls the method to set up how the enemy sprite's with start
		enemiesSetup();

		// Displays the players sprite to the screen
		player.show();
		
		// Exit and closes the game when the close button is clicked
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Draws everything required to be displayed to the screen
	 */
	@Override
	public void draw(Graphics2D graphic) {
		// Gets the x and y coordinates to be used for the offsets relative to the
		// current position of the player
		xo = screenWidth / 2 - Math.round(player.getX());
		yo = screenHeight / 2 - Math.round(player.getY());

		// Gets the minimum and maximum values for the screens width
		xo = Math.min(xo, 0);
		xo = Math.max(screenWidth - tileMap.getPixelWidth(), xo);

		// Gets the minimum and maximum values for the screens height
		yo = Math.min(yo, 0);
		yo = Math.max(screenHeight - tileMap.getPixelHeight(), yo);

		// Draws on a white background if there are no background images loaded in
		if (backgroundImageOne == null || backgroundImageTwo == null || backgroundImageThree == null) {
			graphic.setColor(Color.white);
			graphic.fillRect(0, 0, getWidth(), getHeight());
		}

		// Calls the method parralxScrolling to set the images and speed of each
		// background layer
		backgroundOne = parallaxScrolling(backgroundImageOne, 30);
		backgroundTwo = parallaxScrolling(backgroundImageTwo, 20);
		backgroundThree = parallaxScrolling(backgroundImageThree, 10);

		// Draws the backgrounds to the screen in a layer effect using the offsets
		// applied to get images to create a sense of depth
		graphic.drawImage(backgroundImageOne, backgroundOne, 0, null);
		graphic.drawImage(backgroundImageTwo, backgroundTwo, 375, null);
		graphic.drawImage(backgroundImageThree, backgroundThree, 75, null);

		// Draws the tile map to the screen using the x and y coordinates relative to
		// the tile maps pixel width and height
		tileMap.draw(graphic, xo, yo);

		// Sets offsets sprite's using the x and y coordinates relative to
		// the tile maps pixel width and height
		player.setOffsets(xo, yo);
		enemyOne.setOffsets(xo, yo);
		enemyTwo.setOffsets(xo, yo);

		// Draws the enemies sprite's to the screen
		enemyOne.draw(graphic);
		enemyTwo.draw(graphic);

		// Draws the players to the screen
		player.draw(graphic);

		// If endGame is false, the score bard gets displayed to the screen
		if (!endGame) {
			scoreBoard(graphic);
			// If endGame is true, the death menu gets displayed to the screen
		} else {
			deathMenu(graphic);
		}

	}

	public void scoreBoard(Graphics2D graphic) {
		// Initialises and sets the font type and size
		scoreBoardFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);

		graphic.setFont(scoreBoardFont);
		graphic.setColor(Color.gray);
		graphic.fillRect(20, 35, 70, 40);
		graphic.setColor(Color.black);
		graphic.drawRect(19, 34, 71, 41);

		// Displays the players lives in the top left corner of the screen and sets the
		// text colour to black
		graphic.setColor(Color.black);
		graphic.drawString("Lives: " + playerLives, 30, 50);

		// Displays the players score in the top left corner of the screen
		graphic.drawString("Score: " + playerScore, 30, 70);
	}

	/**
	 * This method when called, displays the death menu to the screen when the
	 * players lives reach zero
	 * 
	 * @param graphic passes in the 2D graphic object
	 */
	public void deathMenu(Graphics2D graphic) {
		// Initialises the rectangles used for the two button
		retryButton = new Rectangle();
		exitButton = new Rectangle();

		// Initialises and sets the font for the menu
		deathMenuFont = new Font(Font.SANS_SERIF, Font.PLAIN, 20);

		graphic.setColor(Color.gray);
		graphic.fillRect(middleOfScreenWidth - (deathMenuWidth / 2), middleOfScreenHeight - (deathMenuHeight / 2),
				deathMenuWidth, deathMenuHeight);

		graphic.setColor(Color.black);
		graphic.drawRect(middleOfScreenWidth - (deathMenuWidth / 2), middleOfScreenHeight - (deathMenuHeight / 2),
				deathMenuWidth, deathMenuHeight);

		graphic.drawImage(deathMessage, (screenWidth / 2) - (deathMenuWidth / 3),
				(screenHeight / 2) - (deathMenuHeight / 3), null);

		graphic.setColor(Color.white);
		graphic.setFont(deathMenuFont);
		graphic.drawString("You Scored : " + playerScore, middleOfScreenWidth - (deathMenuWidth / 6),
				middleOfScreenHeight - (deathMenuHeight / 3) + 65);

		graphic.drawRect(middleOfScreenWidth + 40, middleOfScreenHeight + 30, 50, 25);
		graphic.drawString("Exit", middleOfScreenWidth + 50, middleOfScreenHeight + 50);

		graphic.drawRect(middleOfScreenWidth - 100, middleOfScreenHeight + 30, 60, 25);
		graphic.drawString("Retry", middleOfScreenWidth - 95, middleOfScreenHeight + 50);
	}

	/**
	 * This method creates the parallax scrolling effect for the background images
	 * 
	 * @param backgroundImage passes in the background image to be used
	 * @param imageSpeed      passes in the required speed to be used for the
	 *                        relative background image
	 * @return the speed of the image scrolling
	 */
	public int parallaxScrolling(Image backgroundImage, int imageSpeed) {
		// Creates an offset for the background image using the screens and tile map
		// width along with the players current x coordinate
		int offsetX = screenWidth / 2 - Math.round(player.getX()) - tileMap.getMapWidth();
		offsetX = Math.min(offsetX, 0);
		offsetX = Math.max(offsetX, screenWidth - tileMap.getPixelWidth());

		// Sets the speed of the image
		imageSpeed = offsetX * (screenWidth + backgroundImage.getWidth(null))
				/ (screenWidth * imageSpeed - tileMap.getMapWidth());

		// Returns an integer value for the speed of the background image
		return imageSpeed;
	}

	/**
	 * Updates the state of the game/animation based on the amount of elapsed time
	 * that has passed
	 */
	public void update(long elapsed) {
		// If endGame is not equal to true the game will stop and close
		if (!endGame) {
			// Updates all movements and states that the players sprite can be in
			playerMovement(elapsed);

			// Updates the players sprite animation and its position based on the
			// elapsedTime
			player.update(elapsed);

			// Updates the enemies animation and its position based on the elapsedTime
			enemyOne.update(elapsed);
			enemyTwo.update(elapsed);

			// Method call to handle the players collisions with the tile map
			playerTileMapCollision(player, elapsed);

			// Method call to handle the enemies collisions with the tile map
			enemyTileMapCollision(enemyOne, elapsed);
			enemyTileMapCollision(enemyTwo, elapsed);

			// Method call to check the lives of the player
			checkPlayerLives();

			// Method call to handle the collision between the player and enemy sprite's
			spriteCollisions(enemyOne);
			spriteCollisions(enemyTwo);

			// This if statement enables the player to stay on the screen when the player
			// loads into another level, if not implemented, the player just falls through
			// the map
			// the map
			if (player.getY() > 500) {
				if (level == 1) {
					player.setY(256);
				} else if (level == 2) {
					player.setY(192);
				}
			}
		}
	}

	/**
	 * Checks the players lives to see if the game can continue
	 */
	public void checkPlayerLives() {
		// If lives are 0 the game will stop and close
		if (playerLives == 0) {
			// Method call for endGame which can stop the game
			endGame();
		}
	}

	/**
	 * Controls what level is currently being played
	 * 
	 * @param sprite passes in the players sprote
	 */
	public void nextLevel(Sprite sprite) {
		// Stops the background music
		backgroundMusic.stopMusic();

		// If level one, the corresponding tile map is loaded along with the players
		// spawn coordinates
		if (level == 1) {
			tileMap.loadMap("maps", "map.txt");

			// Sets the players new spawn
			playerSpawnX = 156;
			playerSpawnY = 256;

			// Calls the init method, passing in a tile map file
			init("map.txt");
			// If level two, the corresponding tile map is loaded along with the players
			// spawn coordinates
		} else if (level == 2) {
			tileMap.loadMap("maps", "map2.txt");

			// Sets the players ne spawn
			playerSpawnX = 140;
			playerSpawnY = 192;

			// Calls the init method, passing in a tile map file
			init("map2.txt");
		}
	}

	/**
	 * Controls all of the players movements, what actions are to be carried out
	 * when a call has been made
	 * 
	 * @param elapsed passes in the elapsed time
	 */
	public void playerMovement(long elapsed) {
		// Sets the animation speed for the players sprite
		player.setAnimationSpeed(0.2f);

		// If true, the player is set to a falling state with the use of elapsed time
		// and gravity
		if (falling) {
			// Controls and sets the gravity within the game
			player.setVelocityY(player.getVelocityY() + (gravity * elapsed));
		}

		// If true, enables the players sprite to walk to the right (forward)
		if (walkForward) {
			// Updates the players animation, animation speed and velocity
			updatePlayersAnimation(walkingRight, 0.2f, 0.2f);
		}

		// If true, enable the players sprite to walk to the left (backwards)
		if (walkBackward) {
			// Updates the players animation, animation speed and velocity
			updatePlayersAnimation(walkingLeft, 0.2f, -0.2f);
		}

		// If true, enables the player to jump
		if (jumping) {
			// if jumps go above two, the player can no longer jump, caps the player to only
			// be able to double jump
			if (jumps < 2) {
				// The player must be on the ground in order to jump
				if (player.getVelocityY() >= 0) {
					player.setVelocityY(-0.20f);
					jumping = false;
					// Increments the jumps counter
					jumps++;
				}
			}
		}
	}

	/**
	 * Updates the player animation when called
	 * 
	 * @param playerAnimation passes in the animation to be set
	 * @param animationSpeed  passes in the speed for the animation
	 * @param playerVelocity  passes in the velocity of the sprite
	 */
	public void updatePlayersAnimation(Animation playerAnimation, float animationSpeed, float playerVelocity) {
		player.setAnimation(playerAnimation);
		player.setAnimationSpeed(animationSpeed);
		player.setVelocityX(playerVelocity);
	}

	/**
	 * Handles the collision between the players sprite and enemies
	 * 
	 * @param enemy passes in an enemy sprite
	 */
	public void spriteCollisions(Sprite enemy) {
		// Checks to see is the players y position is above the enemy
		playerCanBeKilled = (enemy.getY() < (player.getY() + 60));

		// Calls the the bound box collision to check for collisions between the passed
		// in sprites
		if (boundBoxCollision(enemy, player)) {
			if (playerCanBeKilled && enemyOne.isVisible() == true) {
				collided = true;
				// If the player has more than zero lives, the method playerKilled gets called
				if (playerLives > 0) {
					playerKilled();
					// If the player has zero lives left, the endGame method gets called
				} else {
					endGame();
				}
			} else {
				// If the enemy is visible and the player has collided with the top of it, the
				// player if boosted up in air and the method enemyKilled is called
				if (enemy.isVisible() == true && falling == true) {
					enemyKilled(enemy);
					player.setVelocityY(-0.2f);
				}
			}
		}
	}

	/**
	 * Takes a life off the player and resets the players sprite back to the
	 * beginning of the game
	 */
	public void playerKilled() {
		// Takes a life of the player
		playerLives--;

		// Puts the player back to the start of the game when they have more than zero
		// life left
		if (playerLives > 0) {
			player.setX(playerSpawnX);
			player.setY(playerSpawnY);

			// Plays a sound when the player dies
			playerDeathSound = new Sound("sounds/playerDeathSound.wav");
			playerDeathSound.start();
		} else if (playerLives == 0) {
			// Plays a sound when the player is killed
			playerDeathSound = new Sound("sounds/playerKilledSound.wav");
			playerDeathSound.start();
			player.setX(playerSpawnX);
			player.setY(playerSpawnY);
		}
	}

	/**
	 * Deals with the actions when an enemy has been killed and increases the
	 * players score
	 * 
	 * @param sprite enemy sprite passed in
	 */
	public void enemyKilled(Sprite sprite) {
		// Increments the players score when an enemy is killed
		playerScore++;
		// Initialises and sets a sound file
		batDeathSound = new Sound("sounds/batDeathSound.wav");
		// Plays a sound when an enemy is killed
		batDeathSound.start();
		// Sets x and y velocity of an enemy to disappear off the screen
		sprite.setVelocityX(0);
		sprite.setVelocityY(0.15f);

		// If an enemy travels of the screen the are stopped running and set to hidden
		if (sprite.getY() < tileMap.getMapHeight()) {
			sprite.hide();
			sprite.stop();
		}
	}

	/**
	 * Sets the players animation and velocity when they die then ends the game
	 */
	public void endGame() {
		// Sets the players velocity
		player.setVelocityX(0);
		player.setVelocityY(0);
		// Hides the player from the screen
		player.hide();
		// Stops playing the background music
		backgroundMusic.stopMusic();
		// Initialises a sets the sound file
		playerDeathSound = new Sound("sounds/deathMenuSound.wav");
		// Starts and plays the sound
		playerDeathSound.start();

		endGame = true;
	}

	/**
	 * Sets up the enemies positions, velocity and displays them in the game
	 */
	public void enemiesSetup() {
		// Sets the enemies animations on start up
		enemyOne.setAnimation(flyingRight);
		enemyTwo.setAnimation(flyingLeft);

		// Sets the enemies states for level One
		if (level == 1) {
			enemyOne.setX(600);
			enemyOne.setY(384);

			enemyTwo.setX(915);
			enemyTwo.setY(256);

			enemyOne.setVelocityX(0.1f);
			enemyOne.setVelocityY(0);

			enemyTwo.setVelocityX(-0.1f);
			enemyTwo.setVelocityY(0);
		}

		// Sets the enemies states for level Two
		if (level == 2) {
			enemyOne.setX(850);
			enemyOne.setY(64);

			enemyTwo.setX(850);
			enemyTwo.setY(64);

			enemyOne.setVelocityX(0.1f);
			enemyOne.setVelocityY(0);

			enemyTwo.setVelocityX(-0.1f);
			enemyTwo.setVelocityY(0);
		}

		// Shows the enemies onto the screen
		enemyOne.show();
		enemyTwo.show();
	}

	/**
	 * Restarts the game, resets everything
	 */
	public void restartGame() {
		// Sets the players life back to three
		playerLives = 3;
		// Sets the players score back to zero
		playerScore = 0;
		// Pauses the background music
		backgroundMusic.stopMusic();
		// Sets the level back to level one
		level = 1;
		// Sets the endGame boolean back to false
		endGame = false;
		// Calls the method init passing in a tile map to restart the game
		init("map.txt");
	}

	/**
	 * Handles the enemy collision detection with the tile map
	 * 
	 * @param sprite  passes in the enemy sprite
	 * @param elapsed passes in the games elapses time
	 */
	public void enemyTileMapCollision(Sprite sprite, long elapsed) {
		// Gets the value for the right hand side of a tile
		tileRightHandSide = (int) (sprite.getX() / tileMap.getTileWidth());
		// Gets the value for the left hand side of a tile
		tileLeftHandSide = tileRightHandSide + 1;
		// Gets the value for the top of a tile
		tileTop = (int) ((sprite.getY() + sprite.getHeight()) / tileMap.getTileHeight());
		// Gets the value for the bottom of a tile
		tileBottom = tileTop - 1;

		// Gets the top left corner value of a tile
		tileTopLeft = tileMap.getTileChar(tileLeftHandSide, tileTop);
		// Gets the top right corner value of a tile
		tileTopRight = tileMap.getTileChar(tileRightHandSide, tileTop);
		// Gets the bottom left corner of a tile
		tileBottomLeft = tileMap.getTileChar(tileLeftHandSide, tileBottom);
		// Gets the bottom right corner of a tile
		tileBottomRight = tileMap.getTileChar(tileRightHandSide, tileBottom);

		// Checks the top and bottom right of the tiles e in
		// the tile map to check if the sprite has collided with it
		if (tileTopRight == 'e' || tileBottomRight == 'e') {
			// Sets the sprite's velocity and animation into the right direction when it
			// collides with a tile when travelling left
			sprite.setVelocityX(0.1f);
			sprite.setAnimation(flyingRight);
		}

		// Checks the top and bottom left of the tiles q in
		// the tile map to check if the sprite has collided with it
		if (tileTopLeft == 'q' || tileBottomLeft == 'q') {
			// Sets the sprite's velocity and animation into the left direction when it
			// collides with a tile when travelling right
			sprite.setVelocityX(-0.1f);
			sprite.setAnimation(flyingLeft);
		}

		// Checks the top and bottom right of the tiles r in
		// the tile map to check if the sprite has collided with it
		if (tileTopRight == 'r' || tileBottomRight == 'r') {
			// Sets the sprite's velocity and animation into the left direction when it
			// collides with a tile when travelling left
			sprite.setVelocityX(0.1f);
			sprite.setAnimation(flyingRight);
		}

		// Checks the top and bottom left of the tiles b in
		// the tile map to check if the sprite has collided with it
		if (tileTopLeft == 'b' || tileBottomLeft == 'b') {
			// Sets the sprite's velocity and animation into the left direction when it
			// collides with a tile when travelling right
			sprite.setVelocityX(-0.1f);
			sprite.setAnimation(flyingLeft);
		}

		// Checks the top and bottom left of the tiles l in
		// the tile map to check if the sprite has collided with it
		if (tileTopLeft == 'l' || tileBottomLeft == 'l') {
			// Sets the sprite's velocity and animation into the left direction when it
			// collides with a tile when travelling right
			sprite.setVelocityX(-0.1f);
			sprite.setAnimation(flyingLeft);
		}
	}

	/**
	 * Handles the collisions between two sprite's when they connect with each other
	 * 
	 * @param spriteOne passes in a sprite
	 * @param spriteTwo passes in a second sprite
	 * @return the collision between the two sprite's
	 */
	public boolean boundBoxCollision(Sprite spriteOne, Sprite spriteTwo) {
		return ((spriteOne.getX() + spriteOne.getImage().getWidth(null) > spriteTwo.getX())
				&& (spriteOne.getX() < (spriteTwo.getX() + spriteTwo.getImage().getWidth(null)))
				&& ((spriteOne.getY() + spriteOne.getImage().getHeight(null) > spriteTwo.getY())
						&& (spriteOne.getY() < spriteTwo.getY() + spriteTwo.getImage().getHeight(null))));
	}

	/**
	 * Handles the players collision detection with the tile map
	 * 
	 * @param sprite  passes in the players sprite
	 * @param elapsed passes in the games elapses time
	 */
	public void playerTileMapCollision(Sprite sprite, long elapsed) {
		// Gets the value for the right hand side of a tile
		tileRightHandSide = (int) (sprite.getX() / tileMap.getTileWidth());
		// Gets the value for the left hand side of a tile
		tileLeftHandSide = tileRightHandSide + 1;
		// Gets the value for the top of a tile
		tileTop = (int) ((sprite.getY() + sprite.getHeight()) / tileMap.getTileHeight());
		// Gets the value for the bottom of a tile
		tileBottom = tileTop - 1;

		// Gets the top left corner value of a tile
		tileTopLeft = tileMap.getTileChar(tileLeftHandSide, tileTop);
		// Gets the top right corner value of a tile
		tileTopRight = tileMap.getTileChar(tileRightHandSide, tileTop);
		// Gets the bottom left corner of a tile
		tileBottomLeft = tileMap.getTileChar(tileLeftHandSide, tileBottom);
		// Gets the bottom right corner of a tile
		tileBottomRight = tileMap.getTileChar(tileRightHandSide, tileBottom);

		// Sets the players height to the height of a tile
		playerHeight = tileMap.getTileHeight();

		// Checks if the players height is the same as the tiles height
		// This is done to prevent the players sprite bugging through tiles as it
		// sometimes sets the players sprite to -1 when moving before
		if (playerHeight == tileMap.getTileHeight()) {

			// Checks if the player is touching a tile and states that the player is not
			// falling and sets the jumps counter to 0
			if (tileTopLeft == 'e' || tileTopRight == 'e' || tileTopRight == 'q' || tileTopLeft == 'q'
					|| tileTopRight == 'm' || tileTopLeft == 'm' || tileTopRight == 'g' || tileTopLeft == 'g'
					|| tileTopRight == 'i' || tileTopLeft == 'i' || tileTopLeft == 'l' || tileTopRight == 'l'
					|| tileTopLeft == 'r' || tileTopRight == 'r' || tileTopLeft == 'u' || tileTopRight == 'u') {

				falling = false;
				jumps = 0;

				sprite.setY((float) (tileTop * tileMap.getTileHeight()) - sprite.getHeight());

				// if the player is falling, falling gets set to true
			} else {

				falling = true;
			}

			// Checks the tiles of the
			// the tile map to check if the sprite has collided with it
			if (tileBottomRight == 'e' || tileBottomRight == 'a') {

				if (sprite.getWidth() == tileMap.getTileWidth()) {
					sprite.setX((float) (tileRightHandSide * tileMap.getTileWidth() + sprite.getWidth()));
				}
			}

			// Checks the tiles of the
			// the tile map to check if the sprite has collided with it
			if (tileBottomLeft == 'b' || tileBottomLeft == 'q' || tileTopRight == 'b') {
				sprite.setX((float) (tileLeftHandSide * tileMap.getTileWidth() - sprite.getWidth()));
			}

			// Checks the tiles of the
			// the tile map to check if the sprite has collided with it
			if (tileBottomLeft == 'm' || tileBottomRight == 'm' || tileBottomLeft == 'l' || tileBottomRight == 'l'
					|| tileBottomLeft == 'r' || tileBottomRight == 'r') {
				sprite.setY((float) (tileBottom * tileMap.getTileHeight() + sprite.getHeight()));
				sprite.setVelocityY(0);
			}

			// Checks if the player has collided with the water, if so the method
			// playerKilled gets called
			if (tileTopLeft == 'w' || tileTopRight == 'w') {
				playerKilled();
			}

			// Checks if the player has left the map, if so the method playerKilled gets
			// called
			if (sprite.getX() < tileMap.getMapWidth()) {
				playerKilled();
			}

			// Checks if the player has left the map, if so the method playerKilled gets
			// called
			if (sprite.getX() > tileMap.getMapWidth() * 64) {
				playerKilled();
			}

			// Checks if the player has collided with the sign at the end of the level to
			// progress, if so the level is set to 2 and the nextLevel method gets called
			if (tileTopLeft == 's' || tileTopRight == 's' || tileBottomLeft == 's' || tileBottomRight == 's') {
				if (level == 1) {
					level = 2;

					nextLevel(sprite);
					// If the player has completed the second level, level gets set to one and the
					// nextLevel method gets called
				} else if (level == 2) {
					level = 1;

					nextLevel(sprite);
				}
				System.out.println("Level " + level);
			}

			// Checks if the player collides and picks up a coin, if so the coinPickedUp
			// method gets called
			// All sides of the coin are checked to ensure that score only gets increased by
			// one
			if (tileTopLeft == 'c') {
				coinPickedUp(tileLeftHandSide, tileTop);
			}

			if (tileTopRight == 'c') {
				coinPickedUp(tileRightHandSide, tileTop);
			}

			if (tileBottomRight == 'c') {
				coinPickedUp(tileRightHandSide, tileBottom);
			}
			if (tileBottomRight == 'h') {
				coinPickedUp(tileRightHandSide, tileBottom);
			}
		}
	}

	/**
	 * Carries out the procedure of when the player collides and picks up a coin
	 * 
	 * @param xCo x coordinates of the tile in the tile map that held a coin
	 * @param yCo y coordinates of the tile in the tile map that held a coin
	 */
	public void coinPickedUp(int xCo, int yCo) {
		// Initialises and sets the sound file
		coinPickUpSound = new Sound("sounds/coinPickUpSound.wav");
		// Starts and plays the sound
		coinPickUpSound.start();
		// Changes the tile character to make the coin disappear
		tileMap.setTileChar('.', xCo, yCo);
		// Increases the players score
		playerScore++;
	}

	/**
	 * Handler for the keyPressed event, overrides the GameCore method
	 */
	public void keyPressed(KeyEvent e) {
		keyPressed = e.getKeyCode();

		// Exits and stops the game
		if (keyPressed == KeyEvent.VK_ESCAPE) {
			stop();
		}

		// Moves the players sprite to the right (forward)
		if (keyPressed == KeyEvent.VK_RIGHT || keyPressed == KeyEvent.VK_D) {
			walkForward = true;
		}

		// Moves the players sprite to the left (backwards)
		if (keyPressed == KeyEvent.VK_LEFT || keyPressed == KeyEvent.VK_A) {
			walkBackward = true;
		}

		// Allows the player to jump
		if (keyPressed == KeyEvent.VK_SPACE) {
			jumping = true;
		}

		// Displays the current coordinates of the players sprite
		if (keyPressed == KeyEvent.VK_Q) {
			System.out.println(player.getX() + " " + player.getY());
		}

		// Restarts the game when the player has more than zero lives
		if (playerLives > 0) {
			if (keyPressed == KeyEvent.VK_R) {
				restartGame();
			}
		}

		// Takes a life off the player
		if (keyPressed == KeyEvent.VK_K) {
			playerLives--;
		}
	}

	/**
	 * Handler for the keyTyped event, overrides the GameCore method
	 */
	public void keyReleased(KeyEvent e) {
		keyPressed = e.getKeyCode();

		// Sets walkForward to false along with updating the players sprites animation
		// when the right arrow or d key has been released
		if (keyPressed == KeyEvent.VK_RIGHT || keyPressed == KeyEvent.VK_D) {
			walkForward = false;
			// Updates the players animation, animation speed and velocity
			updatePlayersAnimation(idleRight, 0.2f, 0);
		}

		// Sets walkBakward to false along with updating the players sprites animation
		// when the left arrow or a key has been released
		if (keyPressed == KeyEvent.VK_LEFT || keyPressed == KeyEvent.VK_A) {
			walkBackward = false;
			// Updates the players animation, animation speed and velocity
			updatePlayersAnimation(idleLeft, 0.2f, 0);
		}

		// Sets jumping to false after the space bar has been released
		if (keyPressed == KeyEvent.VK_SPACE) {
			jumping = false;
		}
	}

	/**
	 * Handler for the MouseEvent events, overrides the GameCore method
	 */
	public void mousePressed(MouseEvent e) {
		mouseButton = e.getButton();

		xClicked = e.getX();
		yClicked = e.getY();

		// Handles the click events for the death menu buttons
		if (mouseButton == MouseEvent.BUTTON1) {
			{
				// If mouse clicked in the stated coordinates the game restarts
				if (xClicked >= 275 && xClicked <= 335 && yClicked >= 281 && yClicked <= 304) {
					restartGame();
				}

				// If mouse clicked in the stated coordinates the game stops and exits
				if (xClicked >= 415 && xClicked <= 465 && yClicked >= 281 && yClicked <= 304) {
					stop();
				}
			}
		}
	}
}
