package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Dialog myDialog;
    private Button[][] buttons = new Button[3][3];
    private ImageView[][] buttonOverlay = new ImageView[3][3];
    private Boolean player1Turn = true;
    private int player1Points;
    private int player2Points;
    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    // Shows the explosion gif when called
    public void showPopup(View v){
        myDialog.setContentView(R.layout.popup);
        myDialog.show();
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(this);
        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);
        updatePointsText();
        // Loops through all buttons and imageviews
        for(int i = 0; i<3;i++){
            for(int j = 0;j<3;j++){
                // Dynamically calls the buttons that are in activity_main.xml
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID,"id",getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);

                // Dynamically calls the imageviews that are in activity_main.xml
                String imageID = "imageView_" + i + j;
                int imgID = getResources().getIdentifier(imageID,"id",getPackageName());
                buttonOverlay[i][j] = findViewById(imgID);
            }
        }
        // Separate listener for the reset
        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }

    // Listener for tic tac toe
    @Override
    public void onClick(View v) {
        // Button chosen is already filled
        if (!((Button)v).getText().toString().equals("")){
            return;
        }
        // Player 1 is X
        ((Button)v).setText("X");
        String[][] board = new String[3][3];
        // Loops through all buttons and gets the status, e.g. strings "X" or "O" or ""
        for(int i = 0; i<3;i++){
            for(int j = 0;j<3;j++){
                board[i][j] = buttons[i][j].getText().toString();
                if (board[i][j] == "X"){
                    buttonOverlay[i][j].setImageResource(R.drawable.thecross);
                }
            }
        }
        if (whoWins(board) == -1){
            aiNextMove();
        }
        // Loops through all buttons and gets the status, e.g. strings "X" or "O" or ""
        for(int i = 0; i<3;i++){
            for(int j = 0;j<3;j++){
                board[i][j] = buttons[i][j].getText().toString();
            }
        }
        // Updates points and show explosion gif
        switch (whoWins(board)){
            case 0: draw(); showPopup(v); break;
            case 1: player1Wins(); showPopup(v); break;
            case 2: player2Wins(); showPopup(v); break;
        }
    }

    // Best move made by AI
    private void aiNextMove(){
        String[][] state = new String[3][3];
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[2];
        int score;

        // Loops through all buttons and gets the status, e.g. strings "X" or "O" or ""
        for(int i = 0; i<3;i++){
            for(int j = 0;j<3;j++){
                state[i][j] = buttons[i][j].getText().toString();
            }
        }

        for(int i = 0; i<3;i++) {
            for (int j = 0; j < 3; j++) {
                // Spot is available
                if (state[i][j].equals("")){
                    // AI chooses the max out of all the min that player picks
                    state[i][j] = "O";
                    // Player's turn to minimise
                    score = minimax(state,0,false);
                    state[i][j] = "";
                    // Keeps track of the max score and its respective move
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        // Changes the global buttons
        buttons[bestMove[0]][bestMove[1]].setText("O");
        buttonOverlay[bestMove[0]][bestMove[1]].setImageResource(R.drawable.circle);
    }

    // Minimax Algo, recursive solution
    private int minimax(String[][] state, int depth, boolean maximising) {
        int winner;
        int bestScore;
        winner = whoWins(state);
        // Game has ended
        if (winner != -1){
            switch (winner) {
                // Draw
                case 0: return 0;
                // Player wins X
                case 1: return -10 + depth;
                // AI wins O
                case 2: return 10 - depth;
            }
        }
        // AI is trying to maximise score
        if (maximising) {
            bestScore = Integer.MIN_VALUE;
            for(int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    // Spot is available
                    if (state[i][j].equals("")) {
                        state[i][j] = "O";
                        // Next is Player's turn, wants to minimise
                        bestScore = Math.max(minimax(state, depth+1,false), bestScore);
                        state[i][j] = "";
                    }
                }
            }
            return bestScore;
        }
        // Player is trying to minimise score
        else {
            bestScore = Integer.MAX_VALUE;
            for(int i = 0; i<3;i++) {
                for (int j = 0; j < 3; j++) {
                    // Spot is available
                    if (state[i][j].equals("")) {
                        state[i][j]= "X";
                        // Next is AI's turn, wants to maximise
                        bestScore = Math.min(minimax(state, depth+1,true), bestScore);
                        state[i][j] = "";
                    }
                }
            }
            return bestScore;
        }
    }
    private int whoWins(String[][] state){
        // Player won
        if (checkForWin(state) == 1){
            return 1;
        }
        // AI won
        else if (checkForWin(state) == 2){
            return 2;
        }
        // No win yet
        else if (checkForWin(state) == -1){
            return -1;
        }
        // Draw
        else {
            return 0;
        }
    }
    // Checks if a game is won
    private int checkForWin(String[][] field){
        // Checking rows for win
        for(int i = 0; i<3;i++){
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")){
                if (field[i][0].equals("X")) {
                    return 1;
                }
                else{
                    return 2;
                }
            }
        }
        // Checking columns for win
        for(int i = 0; i<3;i++){
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")){
                if (field[0][i].equals("X")) {
                    return 1;
                }
                else{
                    return 2;
                }
            }
        }
        // Checking left to right diagonal for win
        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")){
            if (field[0][0].equals("X")) {
                return 1;
            }
            else{
                return 2;
            }
        }
        // Checking right to left diagonal for win
        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")){
            if (field[0][2].equals("X")) {
                return 1;
            }
            else{
                return 2;
            }
        }
        // No win yet
        for(int i = 0; i<3;i++){
            for(int j = 0;j<3;j++){
                if (field[i][j] == ""){
                    return -1;
                }
            }
        }
        // Draw
        return 0;
    }
    // Updates point and announces winner
    private void player1Wins(){
        player1Points++;
        Toast.makeText(this,"Player wins!",Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }
    // Updates point and announces winner
    private void player2Wins(){
        player2Points++;
        Toast.makeText(this,"AI wins!",Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }
    // Announces draw
    private void draw(){
        Toast.makeText(this,"Draw!",Toast.LENGTH_SHORT).show();
        resetBoard();
    }
    // Updates the score board
    private void updatePointsText(){
        textViewPlayer1.setText("Player : " + player1Points);
        textViewPlayer2.setText("AI : " + player2Points);
    }
    // Resets all the buttons back to "", player 1 begins again
    private void resetBoard(){
        for (int i = 0; i < 3; i ++){
            for (int j = 0; j < 3; j ++){
                buttons[i][j].setText("");
                buttonOverlay[i][j].setImageResource(android.R.color.transparent);
            }
        }
        player1Turn = true;
    }
    // Resets all the buttons back to "", player 1 begins again, points set to 0
    private void resetGame(){
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
        resetBoard();
    }

    // Press Ctrl O and find onSaveInstanceState, this saves our variables when there is a change in orientation
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Gives it a key the string "", and the variable to save under that key
        outState.putInt("player1Points",player1Points);
        outState.putInt("player2Points",player2Points);
        outState.putBoolean("player1Turn",player1Turn);
    }
    // Press Ctrl O and find onRestoreInstanceState, this restores our variables after orientation change
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Gives the variables their values from the previously saved keys
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1Turn = savedInstanceState.getBoolean("player1Turn");
    }
}
