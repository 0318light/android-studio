package com.inhatc.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private List<Word> wordList = new ArrayList<>();
    private WordAdapter adapter;
    private WordDao wordDao;
    private android.widget.TextView tvWordCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB DAO 초기화
        wordDao = WordDatabase.getInstance(this).wordDao();
        tvWordCount = findViewById(R.id.tvWordCount);

        setupRecyclerView();
        setupButtons();
        loadWords();
    }

    // RecyclerView 초기화
    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // 삭제 콜백 — 삭제 확인 다이얼로그 표시
        adapter = new WordAdapter(wordList, this::showDeleteDialog);
        rv.setAdapter(adapter);
    }

    // 버튼 클릭 이벤트 세팅
    private void setupButtons() {

        // 단어 추가 버튼
        findViewById(R.id.btnAddWord).setOnClickListener(v -> {
            AddWordDialog dialog = AddWordDialog.newInstance();
            dialog.setOnWordAddedListener(word -> {
                wordList.add(0, word); // 최신 단어 맨 위에 추가
                adapter.notifyItemInserted(0);
                updateWordCount();
            });
            dialog.show(getSupportFragmentManager(), "AddWordDialog");
        });

        // 퀴즈 버튼 — 2주차에 연결 예정
        findViewById(R.id.btnQuiz).setOnClickListener(v -> {
            if (wordList.size() < 4) {
                Toast.makeText(this,
                        "퀴즈는 단어 4개 이상 필요해요", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, QuizMenuActivity.class));
        });

        // 복습 버튼 — 3주차에 연결 예정
        findViewById(R.id.btnReview).setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                List<Word> wrongWords = wordDao.getWrongWords();
                runOnUiThread(() -> {
                    if (wrongWords.isEmpty()) {
                        Toast.makeText(this,
                                "아직 틀린 단어가 없어요!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(new Intent(this, ReviewActivity.class));
                });
            });
        });
    }

    // DB에서 전체 단어 불러오기
    private void loadWords() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Word> loaded = wordDao.getAllWords();
            runOnUiThread(() -> {
                wordList.clear();
                wordList.addAll(loaded);
                adapter.notifyDataSetChanged();
                updateWordCount();
            });
        });
    }

    // 삭제 확인 다이얼로그
    private void showDeleteDialog(Word word) {
        new AlertDialog.Builder(this)
                .setTitle("삭제 확인")
                .setMessage("[" + word.word + "] 을(를) 삭제할까요?")
                .setPositiveButton("삭제", (d, i) -> deleteWord(word))
                .setNegativeButton("취소", null)
                .show();
    }

    // DB에서 단어 삭제 후 목록 갱신
    private void deleteWord(Word word) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            wordDao.delete(word);
            runOnUiThread(() -> {
                int idx = wordList.indexOf(word);
                if (idx != -1) {
                    wordList.remove(idx);
                    adapter.notifyItemRemoved(idx);
                    updateWordCount();
                }
            });
        });
    }

    // 상단 단어 개수 텍스트 업데이트
    private void updateWordCount() {
        tvWordCount.setText("총 " + wordList.size() + "개");
    }

    // 퀴즈 후 복귀 시 목록 새로고침
    @Override
    protected void onResume() {
        super.onResume();
        loadWords();
    }
}