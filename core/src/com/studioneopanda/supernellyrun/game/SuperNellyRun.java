package com.studioneopanda.supernellyrun.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class SuperNellyRun extends ApplicationAdapter {
    private SpriteBatch batch;

    private Texture nextLevelBtn;
    private Texture background;
    private Texture[] man;
    private Texture dizzy;
    private int manState = 0;
    private int pause = 0;
    private float gravity = 0.6f;
    private float velocity = 0;
    private int manY = 0;
    private Rectangle manRectangle;

    private String message = "";
    private BitmapFont annoucement;
    private BitmapFont font;

    private int score = 0;
    private int gameState = 0;

    private Random random;

    private int remainingLife = 3;
    private BitmapFont displayLife;

    private ArrayList<Integer> coinXs = new ArrayList<Integer>();
    private ArrayList<Integer> coinYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
    private Texture coin;
    private int coinCount;

    private ArrayList<Integer> bombXs = new ArrayList<Integer>();
    private ArrayList<Integer> bombYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
    private Texture bomb;
    private int bombCount;

    private ArrayList<Integer> diamondXs = new ArrayList<Integer>();
    private ArrayList<Integer> diamondYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> diamondRectangles = new ArrayList<Rectangle>();
    private Texture diamond;
    private int diamondCount;

    //When we open the app for the first time
    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        manY = Gdx.graphics.getHeight() / 2;

        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        diamond = new Texture("diamond.png");

        random = new Random();

        dizzy = new Texture("dizzy-1.png");
        nextLevelBtn = new Texture("play-button.png");

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        displayLife = new BitmapFont();
        displayLife.setColor(Color.RED);
        displayLife.getData().setScale(10);

        annoucement = new BitmapFont();
        annoucement.setColor(Color.GREEN);
        annoucement.getData().setScale(5);
    }

    //position random coins
    private void makeCoin() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        coinYs.add((int) height);
        coinXs.add(Gdx.graphics.getWidth());
    }

    //position random bombs
    private void makeBomb() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        bombYs.add((int) height);
        bombXs.add(Gdx.graphics.getWidth());
    }

    //position random diamonds
    private void makeDiamond() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        diamondYs.add((int) height);
        diamondXs.add(Gdx.graphics.getWidth());
    }

    // Gets run over and over until we finish the game
    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {
            //Game starts
            //BOMBS START
            if (bombCount < 250) { //each time coinCount reach 100, it makes a coin appear on the screen
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }

            bombRectangles.clear();
            for (int i = 0; i < bombXs.size(); i++) {
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombXs.set(i, bombXs.get(i) - 6);
                bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
            }
            //BOMBS END

            //COINS START
            if (score <= 15) {
                if (coinCount < 100) { //each time coinCount reach 100, it makes a coin appear on the screen
                    coinCount++;
                } else {
                    coinCount = 0;
                    makeCoin();
                }

                coinRectangles.clear();
                for (int i = 0; i < coinXs.size(); i++) {
                    batch.draw(coin, coinXs.get(i), coinYs.get(i));
                    coinXs.set(i, coinXs.get(i) - 4);
                    coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
                }
            }
            //COINS END

            //DIAMONDS START

            if (score >= 15) {
                if (diamondCount < 100) {
                    diamondCount++;
                } else {
                    diamondCount = 0;
                    makeDiamond();
                }

                diamondRectangles.clear();
                for (int i = 0; i < diamondXs.size(); i++) {
                    batch.draw(diamond, diamondXs.get(i), diamondYs.get(i));
                    diamondXs.set(i, diamondXs.get(i) - 4);
                    diamondRectangles.add(new Rectangle(diamondXs.get(i), diamondYs.get(i), diamond.getWidth(), diamond.getHeight()));
                }
            }

            //DIAMONDS END

            //Jumping with the character on screen touch
            if (Gdx.input.justTouched()) {
                velocity = -23;
            }

            if (pause < 6) {
                pause++;
            } else {
                pause = 0;
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }

            velocity += gravity;
            manY -= velocity;

            if (manY <= 0) {
                manY = 0;
            }
        } else if (gameState == 0) {
            //Waiting to start
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            //GAME OVER
            if (Gdx.input.justTouched()) {
                gameState = 1;
                manY = Gdx.graphics.getHeight() / 2;
                score = 0;
                velocity = 0;
                coinXs.clear();
                coinYs.clear();
                coinRectangles.clear();
                coinCount = 0;
                bombXs.clear();
                bombYs.clear();
                bombRectangles.clear();
                bombCount = 0;
                diamondXs.clear();
                diamondYs.clear();
                diamondRectangles.clear();
                //diamondCount.clear(); not working ??
                remainingLife = 3;
            }
        } else if (gameState == 3) {
            batch.draw(dizzy, Gdx.graphics.getWidth() / 2.0f - man[manState].getWidth() / 2.0f, manY);
            batch.draw(nextLevelBtn, 175, 750);
            if (Gdx.input.justTouched()) {

            }
        }

        if (gameState == 2) {
            batch.draw(dizzy, Gdx.graphics.getWidth() / 2.0f - man[manState].getWidth() / 2.0f, manY);
            message = "You died !";
            annoucement.draw(batch, String.valueOf(message), 100, 1150);
        } else {
            batch.draw(man[manState], Gdx.graphics.getWidth() / 2.0f - man[manState].getWidth() / 2.0f, manY);
        }

        manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2.0f - man[manState].getWidth() / 2.0f, manY, man[manState].getWidth(), man[manState].getHeight());

        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
                Gdx.app.log("Coin!", "Collision!");
                score++;

                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }

        for (int i = 0; i < diamondRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, diamondRectangles.get(i))) {
                Gdx.app.log("Diamond!", "Collision!");
                score += 3;

                diamondRectangles.remove(i);
                diamondXs.remove(i);
                diamondYs.remove(i);
                break;
            }
        }

        for (int i = 0; i < bombRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
                Gdx.app.log("BOMB!", "Collision!");
                if (remainingLife > 0) {
                    bombXs.remove(i);
                    bombYs.remove(i);
                    remainingLife--;
                    break;
                }
                if (remainingLife == 0) {
                    gameState = 2;
                }
            }
        }

        if (score > 9 && score < 12) {
            message = "Prochaine étape dans 10 points!";
        } else if (score > 19 && score < 24) {
            message = "Allez, encore un peu pour les diamants!";
        } else if (score > 35 && score < 51) {
            message = "15 petits points...\nEt tu as gagné!";
        } else if (score >= 60) {
            message = "WINNER ! :)";
            gameState = 3;
        } else {
            message = "";
        }

        annoucement.draw(batch, String.valueOf(message), 100, 1150);
        displayLife.draw(batch, String.valueOf("Health: " + remainingLife), 100, 1750);
        font.draw(batch, String.valueOf(score), 100, 200);

        batch.end();
    }

    @Override
    public void dispose() { // never touch this
        batch.dispose();
    }
}
