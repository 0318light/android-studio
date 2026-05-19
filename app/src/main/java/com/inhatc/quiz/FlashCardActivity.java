package com.inhatc.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlashCardActivity extends AppCompatActivity {

    private List<Word> wordList;
    private int currentIndex = 0;
    private int score = 0;
    private boolean isMeaningShown = false;

    private TextView tvWord, tvMeaning, tvProgress;
    private Button btnShow, btnKnow, btnDontKnow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);

        // 뷰 초기화
        tvWord      = findViewById(R.id.tvWord);
        tvMeaning   = findViewById(R.id.tvMeaning);
        tvProgress  = findViewById(R.id.tvProgress);
        btnShow     = findViewById(R.id.btnShow);
        btnKnow     = findViewById(R.id.btnKnow);
        btnDontKnow = findViewById(R.id.btnDontKnow);

        // 알았어요/몰랐어요 버튼 처음엔 숨김
        btnKnow.setVisibility(android.view.View.GONE);
        btnDontKnow.setVisibility(android.view.View.GONE);

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
                showCard();
            });
        });
    }

    // 카드 표시
    private void showCard() {
        if (currentIndex >= wordList.size()) {
            goToResult();
            return;
        }

        isMeaningShown = false;
        Word current = wordList.get(currentIndex);

        // 진행률 표시
        tvProgress.setText((currentIndex + 1) + " / " + wordList.size());
        tvWord.setText(current.word);

        // 뜻 숨기기
        tvMeaning.setVisibility(android.view.View.INVISIBLE);
        btnShow.setVisibility(android.view.View.VISIBLE);
        btnKnow.setVisibility(android.view.View.GONE);
        btnDontKnow.setVisibility(android.view.View.GONE);

        // 뜻 보기 버튼
        btnShow.setOnClickListener(v -> showMeaning());

        // 카드 탭해도 뜻 보이게
        tvWord.setOnClickListener(v -> showMeaning());
    }

    // 뜻 공개
    private void showMeaning() {
        if (isMeaningShown) return;
        isMeaningShown = true;

        Word current = wordList.get(currentIndex);
        tvMeaning.setText(current.meaning);
        tvMeaning.setVisibility(android.view.View.VISIBLE);

        // 버튼 전환
        btnShow.setVisibility(android.view.View.GONE);
        btnKnow.setVisibility(android.view.View.VISIBLE);
        btnDontKnow.setVisibility(android.view.View.VISIBLE);

        // 알았어요
        btnKnow.setOnClickListener(v -> {
            score++;
            currentIndex++;
            showCard();
        });

        // 몰랐어요 — DB에 틀린 단어 기록
        btnDontKnow.setOnClickListener(v -> {
            Word word = wordList.get(currentIndex);
            word.isWrong = true;
            word.wrongCount++;
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() ->
                    WordDatabase.getInstance(this).wordDao().update(word)
            );
            currentIndex++;
            showCard();
        });
    }

    // 결과 화면으로 이동
    private void goToResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", wordList.size());
        startActivity(intent);
        finish();
    }
}
