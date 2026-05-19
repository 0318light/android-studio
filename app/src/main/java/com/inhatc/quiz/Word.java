package com.inhatc.quiz;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Room DB 테이블 정의 — 테이블명 "words"
@Entity(tableName = "words")
public class Word {

    // 기본키 — 자동 증가
    @PrimaryKey(autoGenerate = true)
    public int id;

    // 단어 (예: apple) — null 불가
    @NonNull
    public String word;

    // 뜻 (예: 사과) — null 불가
    @NonNull
    public String meaning;

    // 틀린 단어 여부 (퀴즈에서 틀리면 true)
    public boolean isWrong;

    // 틀린 횟수 (틀릴 때마다 +1)
    public int wrongCount;
}