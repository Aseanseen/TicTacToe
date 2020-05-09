package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button[][] buttons = new Button[3][3];
    private Boolean player1Turn = true;
    private int roundCount;
    private int player1Points;
    private int player2Points;
    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);
        updatePointsText();
        // Loops through all buttons
        for(int i = 0; i<3;i++){
            for(int j = 0;j<3;j++){
                // Dynamically calls the buttons that are in activity_main.xml
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID,"id",getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
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
        roundCount++;
        // Player 1 is X
        ((Button)v).setText("X");
        String[][] board = new String[3][3];
        // Loops through all buttons and gets the status, e.g. strings "X" or "O" or ""
        for(int i = 0; i<3;i++){
            for(int j = 0;j<3;j++){
                board[i][j] = buttons[i][j].getText().toString();
            }
        }
        switch (whoWins(board)){
            case 0: draw(); break;
            case 1: player1Wins(); break;
            case 2: player2Wins(); break;
            case -1: aiNextMove(); break;
        }
        // Loops through all buttons and gets the status, e.g. strings "X" or "O" or ""
        for(int i = 0; i<3;i++){
            for(int j = 0;j<3;j++){
                board[i][j] = buttons[i][j].getText().toString();
            }
        }
        switch (whoWins(board)){
            case 0: draw(); break;
            case 1: player1Wins(); break;
            case 2: player2Wins(); break;
        }
        // Player 2 is O
//        else{
//            aiNextMove();
//            ((Button) v).setText("O");
//        }
        // Increases round count


        // Alternate turns
//        else {
//            player1Turn = !player1Turn;
//        }
    }

    // Best move made by AI
    private void aiNextMove(){
        String[][] state = new String[3][3];
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{0,0};
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
                if (state[i][j] == ""){
                    state[i][j] = "O";
                    score = minimax(state,false);
                    if (score > bestScore){
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        buttons[bestMove[0]][bestMove[1]].setText("O");
    }
    private int minimax(String[][] state, boolean maximising) {
        int winner = whoWins(state);
        int score;
        int bestScore;
        // Game has ended
        if (winner != -1){
            switch (winner) {
                // Draw
                case 0: return 0;
                // Player wins
                case 1: return -1;
                // AI wins
                case 2: return 1;
            }
        }
        if (maximising) {
            bestScore = Integer.MIN_VALUE;
            for(int i = 0; i<3;i++) {
                for (int j = 0; j < 3; j++) {
                    // Spot is available
                    if (state[i][j] == "") {
                        // Maximising because its AI's simulated turn
                        state[i][j] = "O";
                        // Next turn is to minimise aka Player
                        score = minimax(state, false);
                        state[i][j] = "";
                        // Find the max score for the AI aka AI does their best move
                        if (score > bestScore) {
                            bestScore = score;
                        }
                    }
                }
            }
            return bestScore;
        }
        else {
            bestScore = Integer.MAX_VALUE;
            for(int i = 0; i<3;i++) {
                for (int j = 0; j < 3; j++) {
                    // Spot is available
                    if (state[i][j] == "") {
                        state[i][j]= "X";
                        // Next turn is to maximise aka AI
                        score = minimax(state, true);
                        state[i][j] = "";
                        // Find the min score for the Player aka Player does their best move
                        if (score < bestScore) {
                            bestScore = score;
                        }
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
        if (checkForWin(state) == 2){
            return 2;
        }
        // 9 rounds have passed, draw
        else if (roundCount == 9){
            return 0;
        }
        // No win yet
        else{
            return -1;
        }
    }
    // Checks if a game is won
    private int checkForWin(String[][] field){
//        String[][] field = new String[3][3];
        // Loops through all buttons and gets the status, e.g. strings "X" or "O" or ""
//        for(int i = 0; i<3;i++){
//            for(int j = 0;j<3;j++){
//                field[i][j] = buttons[i][j].getText().toString();
//            }
//        }
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
        // No win
        return -1;
    }
    private void player1Wins(){
        player1Points++;
        // Announces the winner
        Toast.makeText(this,"Player wins!",Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }
    private void player2Wins(){
        player2Points++;
        // Announces the winner
        Toast.makeText(this,"AI wins!",Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }
    private void draw(){
        // Announces the draw
        Toast.makeText(this,"Draw!",Toast.LENGTH_SHORT).show();
        resetBoard();
    }
    // Updates the score board
    private void updatePointsText(){
        textViewPlayer1.setText("Player : " + player1Points);
        textViewPlayer2.setText("AI : " + player2Points);
    }
    // Resets all the buttons back to "", round count = 0, player 1 begins again
    private void resetBoard(){
        for (int i = 0; i < 3; i ++){
            for (int j = 0; j < 3; j ++){
                buttons[i][j].setText("");
            }
        }
        roundCount = 0;
        player1Turn = true;
    }
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
        outState.putInt("roundCount",roundCount);
        outState.putInt("player1Points",player1Points);
        outState.putInt("player2Points",player2Points);
        outState.putBoolean("player1Turn",player1Turn);
    }
    // Press Ctrl O and find onRestoreInstanceState, this restores our variables after orientation change
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Gives the variables their values from the previously saved keys
        roundCount = savedInstanceState.getInt("roundCount");
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1Turn = savedInstanceState.getBoolean("player1Turn");
    }
}
