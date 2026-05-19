package com.inhatc.quiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<Word> wordList;
    private OnDeleteListener deleteListener;

    // 삭제 버튼 콜백 인터페이스
    public interface OnDeleteListener {
        void onDelete(Word word);
    }

    // 생성자 — 단어 목록과 삭제 콜백 받음
    public WordAdapter(List<Word> wordList, OnDeleteListener deleteListener) {
        this.wordList = wordList;
        this.deleteListener = deleteListener;
    }

    // 아이템 뷰 생성 (item_word.xml inflate)
    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    // 아이템 데이터 바인딩
    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);

        // 단어, 뜻 세팅
        holder.tvWord.setText(word.word);
        holder.tvMeaning.setText(word.meaning);

        // 틀린 단어면 빨간 뱃지 표시, 아니면 숨김
        if (word.isWrong) {
            holder.tvWrongCount.setVisibility(View.VISIBLE);
            holder.tvWrongCount.setText("✗ " + word.wrongCount + "회");
        } else {
            holder.tvWrongCount.setVisibility(View.GONE);
        }

        // 삭제 버튼 클릭 시 콜백 호출
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(word));
    }

    // 전체 아이템 개수 반환
    @Override
    public int getItemCount() {
        return wordList.size();
    }

    // ViewHolder — 아이템 뷰의 각 위젯 참조
    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord, tvMeaning, tvWrongCount;
        ImageButton btnDelete;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord       = itemView.findViewById(R.id.tvWord);
            tvMeaning    = itemView.findViewById(R.id.tvMeaning);
            tvWrongCount = itemView.findViewById(R.id.tvWrongCount);
            btnDelete    = itemView.findViewById(R.id.btnDelete);
        }
    }
}