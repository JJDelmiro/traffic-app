package com.example.trafficsigns;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button bAnswer1, bAnswer2, bAnswer3, bAnswer4;
    ImageView iv_sign;
    List<Signs> list;
    Random r;
    int turn = 1;
    int score = 0; // Variável para rastrear acertos
    final int totalRounds = 10; // Número total de rodadas
    String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        r = new Random();

        // Recebe o nome do jogador da Intent
        playerName = getIntent().getStringExtra("PLAYER_NAME");
        if (playerName == null || playerName.isEmpty()) {
            playerName = "Jogador"; // Nome padrão caso o nome não seja fornecido
        }

        // Adiciona um log para verificar o valor de playerName
        Log.d("MainActivity", "Nome do jogador: " + playerName);

        iv_sign = findViewById(R.id.iv_sign);
        bAnswer1 = findViewById(R.id.bAnswer1);
        bAnswer2 = findViewById(R.id.bAnswer2);
        bAnswer3 = findViewById(R.id.bAnswer3);
        bAnswer4 = findViewById(R.id.bAnswer4);

        list = new ArrayList<>();

        // Adiciona os sinais de trânsito e suas respectivas imagens
        Database database = new Database();
        if (database.signs.length == database.signImages.length) {
            for (int i = 0; i < database.signs.length; i++) {
                list.add(new Signs(database.signs[i], database.signImages[i]));
            }
        } else {
            Log.e("MainActivity", "Arrays de sinais e imagens estão com tamanhos diferentes.");
        }

        if (list.isEmpty()) {
            Log.e("MainActivity", "A lista de sinais está vazia.");
            return;
        }

        Collections.shuffle(list);
        newQuestion(turn);

        View.OnClickListener answerClickListener = v -> {
            Button clickedButton = (Button) v;
            boolean isCorrect = clickedButton.getText().toString().equalsIgnoreCase(list.get(turn - 1).getName());

            Log.d("MainActivity", "Botão clicado: " + clickedButton.getText());
            Log.d("MainActivity", "Resposta correta: " + list.get(turn - 1).getName());

            // Verifica se a resposta está correta
            if (isCorrect) {
                clickedButton.setBackgroundColor(Color.parseColor("#198C19")); // Verde para correta
                score++; // Incrementa pontuação
            } else {
                clickedButton.setBackgroundColor(Color.parseColor("#CC0000")); // Vermelho para incorreta
                highlightCorrectAnswer(); // Mostra a resposta correta
            }

            // Adiciona um pequeno delay antes de mudar para a próxima pergunta
            clickedButton.postDelayed(() -> {
                if (turn < totalRounds) {
                    turn++;
                    newQuestion(turn); // Carrega nova pergunta
                } else {
                    showFinalScreen(); // Exibe tela final
                }
            }, 1500); // Delay de 1.5 segundos
        };

        // Define o listener para os botões
        bAnswer1.setOnClickListener(answerClickListener);
        bAnswer2.setOnClickListener(answerClickListener);
        bAnswer3.setOnClickListener(answerClickListener);
        bAnswer4.setOnClickListener(answerClickListener);
    }

    // Método para resetar as cores dos botões
    private void resetButtonColors() {
        // Reseta as cores dos botões antes de carregar nova pergunta
        Log.d("MainActivity", "Resetando cores dos botões");
        bAnswer1.setBackgroundColor(Color.parseColor("#FFFFFF")); // Branco
        bAnswer2.setBackgroundColor(Color.parseColor("#FFFFFF")); // Branco
        bAnswer3.setBackgroundColor(Color.parseColor("#FFFFFF")); // Branco
        bAnswer4.setBackgroundColor(Color.parseColor("#FFFFFF")); // Branco
    }

    // Método para carregar uma nova pergunta
    private void newQuestion(int number) {
        if (list == null || list.isEmpty() || number <= 0 || number > list.size()) {
            Log.e("MainActivity", "Lista de sinais não está correta ou número da pergunta inválido.");
            return;
        }

        resetButtonColors(); // Reseta as cores quando uma nova pergunta é carregada

        iv_sign.setImageResource(list.get(number - 1).getImage());

        int correctAnswerIndex = r.nextInt(4);
        List<Button> buttons = new ArrayList<>();
        buttons.add(bAnswer1);
        buttons.add(bAnswer2);
        buttons.add(bAnswer3);
        buttons.add(bAnswer4);

        // Define o texto para o botão correto
        buttons.get(correctAnswerIndex).setText(list.get(number - 1).getName());
        buttons.get(correctAnswerIndex).setTag("correct"); // Armazena a tag da resposta correta

        for (int i = 0; i < buttons.size(); i++) {
            if (i != correctAnswerIndex) {
                int randomIndex;
                do {
                    randomIndex = r.nextInt(list.size());
                } while (randomIndex == number - 1 || isDuplicate(randomIndex, buttons));

                buttons.get(i).setText(list.get(randomIndex).getName());
                buttons.get(i).setTag(null); // Certifique-se de que as outras respostas não estejam marcadas como corretas
            }
        }
    }

    private void highlightCorrectAnswer() {
        // Destaca o botão com a resposta correta
        if ("correct".equals(bAnswer1.getTag())) {
            bAnswer1.setBackgroundColor(Color.parseColor("#198C19")); // Cor verde
        } else if ("correct".equals(bAnswer2.getTag())) {
            bAnswer2.setBackgroundColor(Color.parseColor("#198C19")); // Cor verde
        } else if ("correct".equals(bAnswer3.getTag())) {
            bAnswer3.setBackgroundColor(Color.parseColor("#198C19")); // Cor verde
        } else if ("correct".equals(bAnswer4.getTag())) {
            bAnswer4.setBackgroundColor(Color.parseColor("#198C19")); // Cor verde
        }
    }

    // Método para verificar se uma resposta está duplicada
    private boolean isDuplicate(int index, List<Button> buttons) {
        for (Button button : buttons) {
            if (button.getText().toString().equalsIgnoreCase(list.get(index).getName())) {
                return true;
            }
        }
        return false;
    }

    // Método para mostrar a tela final
    private void showFinalScreen() {
        // Finaliza o quiz e vai para a tela final
        Intent finalScreenIntent = new Intent(MainActivity.this, FinalScreenActivity.class);
        finalScreenIntent.putExtra("PLAYER_NAME", playerName); // Envia o nome do jogador
        finalScreenIntent.putExtra("SCORE", score); // Envia a pontuação final

        // Adiciona logs para verificar os valores passados para a FinalScreenActivity
        Log.d("MainActivity", "Finalizando jogo com nome: " + playerName);
        Log.d("MainActivity", "Pontuação final: " + score);

        startActivity(finalScreenIntent);
        finish();
    }
}
