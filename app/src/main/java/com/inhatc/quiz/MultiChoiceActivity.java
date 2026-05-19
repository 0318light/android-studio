package com.inhatc.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiChoiceActivity extends AppCompatActivity {

    private List<Word> wordList;
    private int currentIndex = 0;
    private int score = 0;

    private TextView tvProgress, tvWord;
    private Button btn1, btn2, btn3, btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_choice);

        // 뷰 초기화
        tvProgress = findViewById(R.id.tvProgress);
        tvWord     = findViewById(R.id.tvWord);
        btn1       = findViewById(R.id.btn1);
        btn2       = findViewById(R.id.btn2);
        btn3       = findViewById(R.id.btn3);
        btn4       = findViewById(R.id.btn4);

        loadWords();
    }

    // DB에서 단어 불러오기
    private void loadWords() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Word> loaded = WordDatabase.getInstance(this)
                    .wordDao().getAllWords();
            runOnUiThread(() -> {
                wordList = loaded;
                Collections.shuffle(wordList); // 랜덤 순서
                showQuestion();
            });
        });
    }

    // 문제 표시
    private void showQuestion() {
        if (currentIndex >= wordList.size()) {
            goToResult();
            return;
        }

        Word current = wordList.get(currentIndex);

        // 진행률
        tvProgress.setText((currentIndex + 1) + " / " + wordList.size());
        tvWord.setText(current.word);

        // 버튼 색상 초기화
        resetButtons();

        // 정답 + 오답 3개 섞기
        List<String> options = getOptions(current);
        Button[] btns = {btn1, btn2, btn3, btn4};
        for (int i = 0; i < 4; i++) {
            final String option = options.get(i);
            btns[i].setText(option);
            btns[i].setOnClickListener(v -> checkAnswer(option, current));
        }
    }

    // 정답 체크
    private void checkAnswer(String selected, Word current) {
        // 버튼 비활성화 (중복 클릭 방지)
        setButtonsEnabled(false);

        Button[] btns = {btn1, btn2, btn3, btn4};

        for (Button btn : btns) {
            if (btn.getText().toString().equals(current.meaning)) {
                // 정답 버튼 초록색
                btn.setBackgroundColor(
                        android.graphics.Color.parseColor("#1D9E75"));
            } else if (btn.getText().toString().equals(selected)
                    && !selected.equals(current.meaning)) {
                // 선택한 오답 버튼 빨간색
                btn.setBackgroundColor(
                        android.graphics.Color.parseColor("#E24B4A"));
            }
        }

        if (selected.equals(current.meaning)) {
            score++;
        } else {
            // 틀린 단어 DB 기록
            current.isWrong = true;
            current.wrongCount++;
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() ->
                    WordDatabase.getInstance(this).wordDao().update(current)
            );
        }

        // 1초 후 다음 문제
        new android.os.Handler().postDelayed(() -> {
            currentIndex++;
            showQuestion();
        }, 1000);
    }

    // 오답 3개 + 정답 1개 섞기
    private List<String> getOptions(Word correct) {
        List<String> opts = new ArrayList<>();
        opts.add(correct.meaning);

        List<Word> others = new ArrayList<>(wordList);
        others.remove(correct);
        Collections.shuffle(others);

        for (int i = 0; i < Math.min(3, others.size()); i++) {
            opts.add(others.get(i).meaning);
        }
        Collections.shuffle(opts);
        return opts;
    }

    // 버튼 색상 초기화
    private void resetButtons() {
        Button[] btns = {btn1, btn2, btn3, btn4};
        for (Button btn : btns) {
            btn.setBackgroundColor(
                    android.graphics.Color.parseColor("#534AB7"));
            btn.setEnabled(true);
        }
    }

    // 버튼 활성/비활성
    private void setButtonsEnabled(boolean enabled) {
        btn1.setEnabled(enabled);
        btn2.setEnabled(enabled);
        btn3.setEnabled(enabled);
        btn4.setEnabled(enabled);
    }

    // 결과 화면으로
    private void goToResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", wordList.size());
        startActivity(intent);
        finish();
    }
}
