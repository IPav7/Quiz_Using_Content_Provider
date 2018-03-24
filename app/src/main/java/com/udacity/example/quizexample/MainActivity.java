/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.example.quizexample;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.udacity.example.droidtermsprovider.DroidTermsExampleContract;

import java.util.ArrayList;

/**
 * Gets the data from the ContentProvider and shows a series of flash cards.
 */

public class MainActivity extends AppCompatActivity {

    private Cursor mData;

    // The current state of the app
    private int mCurrentState;

    private int wordIndex, defIndex;


    private Button mButton;

    // This state is when the word definition is hidden and clicking the button will therefore
    // show the definition
    private final int STATE_HIDDEN = 0;

    // This state is when the word definition is shown and clicking the button will therefore
    // advance the app to the next word
    private final int STATE_SHOWN = 1;

    TextView wordView, defView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the views
        mButton = findViewById(R.id.button_next);
        wordView = findViewById(R.id.text_view_word);
        defView = findViewById(R.id.text_view_definition);
        new GetCursor().execute();
    }

    private class GetCursor extends AsyncTask<Void, Void, Cursor>{

        @Override
        protected Cursor doInBackground(Void... voids) {
            Cursor cursor = getContentResolver().query(DroidTermsExampleContract.CONTENT_URI,
                    null,null, null, null);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            mData = cursor;
            defIndex = mData.getColumnIndex(DroidTermsExampleContract.COLUMN_DEFINITION);
            wordIndex = mData.getColumnIndex(DroidTermsExampleContract.COLUMN_WORD);
            nextWord();
        }
    }

    /**
     * This is called from the layout when the button is clicked and switches between the
     * two app states.
     * @param view The view that was clicked
     */
    public void onButtonClick(View view) {

        // Either show the definition of the current word, or if the definition is currently
        // showing, move to the next word.
        switch (mCurrentState) {
            case STATE_HIDDEN:
                showDefinition();
                break;
            case STATE_SHOWN:
                nextWord();
                break;
        }
    }

    public void nextWord() {

        // Change button text




        if(mData != null){
            if(!mData.moveToNext())
                mData.moveToFirst();
            defView.setVisibility(View.INVISIBLE);
            mButton.setText(getString(R.string.show_definition));
            mCurrentState = STATE_HIDDEN;
            wordView.setText(mData.getString(wordIndex));
            defView.setText(mData.getString(defIndex));
        }


    }

    public void showDefinition() {

        if(mData != null) {
            defView.setVisibility(View.VISIBLE);

            // Change button text
            mButton.setText(getString(R.string.next_word));

            mCurrentState = STATE_SHOWN;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mData.close();
    }
}
