package com.inhatc.quiz;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Room DB 설정 — Word 테이블 1개, 버전 1
@Database(entities = {Word.class}, version = 1, exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {

    // 싱글톤 인스턴스
    private static WordDatabase instance;

    // DAO 접근자
    public abstract WordDao wordDao();

    // 싱글톤 패턴 — 앱 전체에서 하나의 인스턴스만 사용
    public static synchronized WordDatabase getInstance(Context context) {
        if (instance == null) {
            // DB 없으면 새로 생성
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            WordDatabase.class,
                            "word_database" // DB 파일명
                    )
                    .fallbackToDestructiveMigration() // 버전 충돌 시 DB 초기화
                    .build();
        }
        return instance;
    }
}