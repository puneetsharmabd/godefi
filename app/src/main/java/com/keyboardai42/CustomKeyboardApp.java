package com.keyboardai42;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.keyboardai42.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CustomKeyboardApp extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private StringBuilder mComposing = new StringBuilder();
    private int KEY_123 = -2;
    private int KEY_ABC = -3;
    private int KEY_SYMBOL = -4;
    KeyboardView keyboardView;
    boolean changeList = false;
    boolean changeToneList = false;

    ListView listView;
    Button button3,button2;
    LinearLayout layout;

    List<String> languages = new ArrayList<>();
    List<String> toneChanger = new ArrayList<>();

    @Override
    public View onCreateInputView() {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        scrollView.setBackgroundColor(getResources().getColor(R.color.white));
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button button = new Button(this);
        button.setText("Speech to Text");
        button2 = new Button(this);
        button2.setText("Tone Changer");
        button3 = new Button(this);
        button3.setText("Translate");
        Button button4 = new Button(this);
        button4.setText("Check Grammar");
        Button button5 = new Button(this);
        button5.setText("Ask AI");
        Button button6 = new Button(this);
        button6.setText("Record");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(CustomKeyboardApp.this, "Toast", Toast.LENGTH_SHORT).show();
                speechToText();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askAIForGrammer();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button3.setText("Translate");
                changeList = false;
                if (changeToneList){
                    changeToneList = false;
                    layout.removeView(listView);
                    layout.addView(keyboardView);
                    button2.setText("Tone Changer");
                }else {
                    changeToneList = true;
                    if (listView != null){
                        layout.removeView(listView);
                    }
                    layout.removeView(keyboardView);
                    layout.addView(getListViewToneChange(CustomKeyboardApp.this));
                    button2.setText("Keyboard");
                }
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputConnection inputConnection = getCurrentInputConnection();
                if (inputConnection == null){
                    return;
                }
                recordAudio(inputConnection,CustomKeyboardApp.this);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askAI();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button2.setText("Tone Changer");
                changeToneList = false;
                if (changeList){
                    changeList = false;
                    layout.removeView(listView);
                    layout.addView(keyboardView);
                    button3.setText("Translate");
                }else {
                    changeList = true;
                    if (listView != null){
                        layout.removeView(listView);
                    }
                    layout.removeView(keyboardView);
                    layout.addView(getListView(CustomKeyboardApp.this));
                    button3.setText("Keyboard");
                }
            }
        });

        //button3.setEnabled(false);
        //getLanguageList();
        languages.add("Auto");
        languages.add("English");
        languages.add("Spanish");
        languages.add("RU");
        languages.add("Chinese");
        languages.add("Hebrew");
        toneChanger.add("Friendly");
        toneChanger.add("Business");
        toneChanger.add("Professional");
        toneChanger.add("Academic");
        toneChanger.add("Formal");
        toneChanger.add("Humorous");
        toneChanger.add("Serious");
        toneChanger.add("Technical");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.LEFT);
        linearLayout.addView(button);
        linearLayout.addView(button2);
        linearLayout.addView(button3);
        linearLayout.addView(button4);
        linearLayout.addView(button5);
        linearLayout.addView(button6);
        scrollView.addView(linearLayout);

        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.custom_keyboard_layout,null);
        Keyboard keyboard = new Keyboard(this,R.xml.custom_keypad);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        layout.addView(scrollView);
        layout.addView(keyboardView);
        return layout;
    }

    private void getLanguageList() {
        Utils.Companion.getLanguageList(new LanguageResultCallback() {
            @Override
            public void audioResult(List<String> langList) {
                languages.addAll(langList);
                button3.setEnabled(true);
            }
        }
        );
    }

    @NonNull
    private ListView getListView(Context context) {
        listView = new ListView(context);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,755));
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(context, R.layout.list_item,languages);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputConnection inputConnection = getCurrentInputConnection();
                inputConnection.performContextMenuAction(android.R.id.selectAll);
                CharSequence selectedText = inputConnection.getSelectedText(0);
                inputConnection.commitText("",1);
                if (selectedText != null && selectedText.length()>0){
                    Utils.Companion.getTranslatedText(languages.get(position), selectedText.toString(), new AudioResultCallback() {
                        @Override
                        public void audioResult(String audio) {
                            InputConnection inputConnection = getCurrentInputConnection();
                            if (inputConnection == null || audio == null){
                                return;
                            }
                            inputConnection.commitText(audio,audio.length());
                        }
                    });
                }
            }
        });
        listView.setAdapter(modeAdapter);
        return listView;
    }

    @NonNull
    private ListView getListViewToneChange(Context context) {
        listView = new ListView(context);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,755));
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(context, R.layout.list_item,toneChanger);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                askAIForTone(toneChanger.get(position));
            }
        });
        listView.setAdapter(modeAdapter);
        return listView;
    }

    private void askAI() {
        InputConnection inputConnection = getCurrentInputConnection();
        inputConnection.performContextMenuAction(android.R.id.selectAll);
        CharSequence selectedText = inputConnection.getSelectedText(0);
        inputConnection.commitText("",1);
        if (selectedText.length()==0){
            return;
        }
        Utils.Companion.askAI(this, selectedText.toString(), new AudioResultCallback() {
            @Override
            public void audioResult(String audio) {
                //Toast.makeText(CustomKeyboardApp.this, ""+audio, Toast.LENGTH_SHORT).show();
                InputConnection inputConnection = getCurrentInputConnection();
                if (inputConnection == null){
                    return;
                }
                inputConnection.commitText(audio,audio.length());
            }
        });
    }

    private void askAIForGrammer() {
        InputConnection inputConnection = getCurrentInputConnection();
        inputConnection.performContextMenuAction(android.R.id.selectAll);
        CharSequence selectedText = inputConnection.getSelectedText(0);
        inputConnection.commitText("",1);
        if (selectedText.length()==0){
            return;
        }
        Utils.Companion.askAI(this, "Check and provide right grammar for this text: "+selectedText.toString(), new AudioResultCallback() {
            @Override
            public void audioResult(String audio) {
                //Toast.makeText(CustomKeyboardApp.this, ""+audio, Toast.LENGTH_SHORT).show();
                InputConnection inputConnection = getCurrentInputConnection();
                if (inputConnection == null){
                    return;
                }
                inputConnection.commitText(audio,audio.length());
            }
        });
    }

    private void askAIForTone(String tone) {
        InputConnection inputConnection = getCurrentInputConnection();
        inputConnection.performContextMenuAction(android.R.id.selectAll);
        CharSequence selectedText = inputConnection.getSelectedText(0);
        inputConnection.commitText("",1);
        if (selectedText.length()==0){
            return;
        }
        Utils.Companion.askAI(this, selectedText.toString()+" in "+tone + " tone", new AudioResultCallback() {
            @Override
            public void audioResult(String audio) {
                //Toast.makeText(CustomKeyboardApp.this, ""+audio, Toast.LENGTH_SHORT).show();
                InputConnection inputConnection = getCurrentInputConnection();
                if (inputConnection == null){
                    return;
                }
                inputConnection.commitText(audio,audio.length());
                changeToneList = false;
                layout.removeView(listView);
                layout.addView(keyboardView);
                button2.setText("Tone Changer");
            }
        });
    }

    private void speechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.keyboardai42");

        SpeechRecognizer recognizer = SpeechRecognizer
                .createSpeechRecognizer(this.getApplicationContext());
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("Ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                System.out.println("Speech starting");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                System.err.println("Error listening for speech: " + error);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults == null) {
                    System.out.println("No voice results");
                } else {
                    System.out.println("Printing matches: ");
                    for (String match : voiceResults) {
                        System.out.println(match);
                        InputConnection inputConnection = getCurrentInputConnection();
                        if (inputConnection == null){
                            return;
                        }
                        inputConnection.commitText(match,match.length());
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        };
        recognizer.setRecognitionListener(listener);
        recognizer.startListening(intent);
    }

    @Override
    public View onCreateCandidatesView() {
        return super.onCreateCandidatesView();
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        //Toast.makeText(this, ""+primaryCode, Toast.LENGTH_SHORT).show();
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection == null){
            return;
        }
        if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            keyboardView.setShifted(!keyboardView.isShifted());
            return;
        }
        if (String.valueOf(primaryCode).equalsIgnoreCase("130")){
            recordAudio(inputConnection,this);
            return;
        }
        if (primaryCode == KeyEvent.KEYCODE_DEL){
            handleBackSpaceEvent(inputConnection);
            return;
        }
        if (primaryCode == KEY_123){
            Keyboard keyboard = new Keyboard(this,R.xml.custom_keypad_2);
            keyboardView.setKeyboard(keyboard);
            return;
        }
        if (primaryCode == KEY_ABC){
            Keyboard keyboard = new Keyboard(this,R.xml.custom_keypad);
            keyboardView.setKeyboard(keyboard);
            return;
        }
        if (primaryCode == KEY_SYMBOL){
            Keyboard keyboard = new Keyboard(this,R.xml.custom_keypad_3);
            keyboardView.setKeyboard(keyboard);
            return;
        }
        if (keyboardView.isShifted()){
            inputConnection.commitText(String.valueOf((char) primaryCode).toUpperCase(),1);
            return;
        }
        inputConnection.commitText(String.valueOf((char) primaryCode),1);
        //mComposing.append(String.valueOf((char) primaryCode));
    }

    private void recordAudio(InputConnection inputConnection, CustomKeyboardApp customKeyboardApp) {
        Utils.Companion.surroundingSound(customKeyboardApp, new AudioResultCallback(

        ) {
            @Override
            public void audioResult(String audio) {
                inputConnection.commitText(audio,audio.length());
            }
        });
        return;
    }

    private void handleBackSpaceEvent(InputConnection inputConnection) {
        CharSequence selectedText = inputConnection.getSelectedText(0);
        if (TextUtils.isEmpty(selectedText)) {
            inputConnection.deleteSurroundingText(1, 0);
            //mComposing.delete(1,0);
        } else {
            inputConnection.commitText("", 1);
            //mComposing = new StringBuilder();
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
