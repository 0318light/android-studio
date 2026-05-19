package com.inhatc.quiz;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class QuizMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_menu);

        // 플래시카드 버튼
        findViewById(R.id.btnFlashCard).setOnClickListener(v ->
                startActivity(new Intent(this, FlashCardActivity.class))
        );

        // 4지선다 버튼 — 3주차 예정
        findViewById(R.id.btnMultiChoice).setOnClickListener(v ->
                startActivity(new Intent(this, MultiChoiceActivity.class))
        );
    }
}
