package com.inhatc.quiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddWordDialog extends DialogFragment {

    // 단어 추가 완료 콜백 인터페이스
    public interface OnWordAddedListener {
        void onWordAdded(Word word);
    }

    private OnWordAddedListener listener;

    // 인스턴스 생성
    public static AddWordDialog newInstance() {
        return new AddWordDialog();
    }

    // 콜백 세팅
    public void setOnWordAddedListener(OnWordAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // dialog_add_word.xml inflate
        View view = inflater.inflate(R.layout.dialog_add_word, null);

        EditText etWord    = view.findViewById(R.id.etWord);
        EditText etMeaning = view.findViewById(R.id.etMeaning);

        builder.setView(view)
                .setTitle("단어 추가")
                .setPositiveButton("추가", (dialog, id) -> {
                    String word    = etWord.getText().toString().trim();
                    String meaning = etMeaning.getText().toString().trim();

                    // 빈 값 체크
                    if (word.isEmpty() || meaning.isEmpty()) {
                        Toast.makeText(getContext(),
                                "단어와 뜻을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 백그라운드 스레드에서 DB 저장 (Room은 메인 스레드 금지)
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        Word newWord       = new Word();
                        newWord.word       = word;
                        newWord.meaning    = meaning;
                        newWord.isWrong    = false;
                        newWord.wrongCount = 0;

                        // DB insert
                        WordDatabase.getInstance(requireContext())
                                .wordDao().insert(newWord);

                        // UI 업데이트는 반드시 메인 스레드에서
                        requireActivity().runOnUiThread(() -> {
                            if (listener != null) listener.onWordAdded(newWord);
                            Toast.makeText(getContext(),
                                    "추가됐어요!", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("취소", (dialog, id) -> dialog.cancel());

        return builder.create();
    }
}