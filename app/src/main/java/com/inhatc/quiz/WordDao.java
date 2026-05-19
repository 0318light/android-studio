package com.inhatc.quiz;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordDao {

    // 단어 삽입
    @Insert
    void insert(Word word);

    // 단어 수정
    @Update
    void update(Word word);

    // 단어 삭제
    @Delete
    void delete(Word word);

    // 전체 단어 조회 (최신순)
    @Query("SELECT * FROM words ORDER BY id DESC")
    List<Word> getAllWords();

    // 틀린 단어만 조회 (많이 틀린 순)
    @Query("SELECT * FROM words WHERE isWrong = 1 ORDER BY wrongCount DESC")
    List<Word> getWrongWords();

    // 전체 단어 개수 조회
    @Query("SELECT COUNT(*) FROM words")
    int getWordCount();
}