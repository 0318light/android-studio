package com.inhatc.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewActivity extends AppCompatActivity {

    private List<Word> wrongWords = new ArrayList<>();
    private WordAdapter adapter;
    private TextView tvWrongCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        tvWrongCount = findViewById(R.id.tvWrongCount);

        setupRecyclerView();
        loadWrongWords();

        // 복습 퀴즈 시작 버튼
        findViewById(R.id.btnReviewQuiz).setOnClickListener(v -> {
            if (wrongWords.size() < 4) {
                android.widget.Toast.makeText(this,
                        "복습 퀴즈는 틀린 단어 4개 이상 필요해요",
                        android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, MultiChoiceActivity.class));
        });

        findViewById(R.id.btnHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 스택 정리
            startActivity(intent);
            finish();
        });
    }

    // RecyclerView 세팅
    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordAdapter(wrongWords, word -> {
            // 복습 화면에서는 삭제 대신 틀린 기록 초기화
            word.isWrong = false;
            word.wrongCount = 0;
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                WordDatabase.getInstance(this).wordDao().update(word);
                runOnUiThread(() -> {
                    int idx = wrongWords.indexOf(word);
                    if (idx != -1) {
                        wrongWords.remove(idx);
                        adapter.notifyItemRemoved(idx);
                        updateWrongCount();
                    }
                });
            });
        });
        rv.setAdapter(adapter);
    }

    // 틀린 단어만 불러오기
    private void loadWrongWords() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Word> loaded = WordDatabase.getInstance(this).wordDao().getWrongWords();
            runOnUiThread(() -> {
                wrongWords.clear();
                wrongWords.addAll(loaded);
                adapter.notifyDataSetChanged();
                updateWrongCount();
            });
        });
    }

    // 틀린 단어 개수 업데이트
    private void updateWrongCount() {
        tvWrongCount.setText("틀린 단어 " + wrongWords.size() + "개");
    }
}
