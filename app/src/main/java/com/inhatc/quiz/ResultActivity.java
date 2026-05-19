package com.inhatc.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // MainActivity에서 넘어온 점수 데이터 받기
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);
        int pct   = total > 0 ? (score * 100 / total) : 0;

        TextView tvScore   = findViewById(R.id.tvScore);
        TextView tvTotal   = findViewById(R.id.tvTotal);
        TextView tvPct     = findViewById(R.id.tvPct);
        TextView tvComment = findViewById(R.id.tvComment);
        Button btnRetry    = findViewById(R.id.btnRetry);
        Button btnHome     = findViewById(R.id.btnHome);

        // 점수 표시
        tvScore.setText(score + " / " + total);
        tvTotal.setText("정답률 " + pct + "%");

        // 정답률에 따른 코멘트
        if (pct == 100) {
            tvComment.setText("완벽해요! 🎉");
        } else if (pct >= 80) {
            tvComment.setText("잘 하셨어요! 👍");
        } else if (pct >= 60) {
            tvComment.setText("조금 더 연습해봐요! 💪");
        } else {
            tvComment.setText("복습이 필요해요! 📖");
        }

        // 다시 풀기 → 플래시카드로 이동
        btnRetry.setOnClickListener(v -> {
            startActivity(new Intent(this, FlashCardActivity.class));
            finish();
        });

        // 홈으로 → MainActivity로 이동
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 스택 정리
            startActivity(intent);
            finish();
        });
    }
}
