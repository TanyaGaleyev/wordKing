package org.ivan.wordking;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ivan on 01.03.14.
 */
public class WordKingActivity extends Activity implements View.OnTouchListener {

    public static final int N_DISPLAYED = 4;
    public static final int ANIM_DURATION = 1000;
    public static final int SLIDE_SENS = 100;
    private WSClient wsClient = new WSClient();
    private ViewFlipper flipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordking);
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        findViewById(R.id.root).setOnTouchListener(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        for (int layout : new int[]{R.layout.wordpanel, R.layout.wordpanel})
            flipper.addView(inflater.inflate(layout, null));

//        for (int i = 0; i < flipper.getChildCount(); i++) {
//            Button submitBtn = (Button) flipper.getChildAt(i).findViewById(R.id.submit);
//            submitBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    submit();
//                }
//            });
//        }
        Button submitBtn = (Button) findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        load();
    }

    private View activePage() {
        return flipper.getChildAt(flipper.getDisplayedChild());
    }

    private View fadePanel() {
        return activePage().findViewById(R.id.fade_panel);
    }

    private List<TextView> wordsViews() {
        List<TextView> wordsL = new ArrayList<TextView>();
        View ap = activePage();
        wordsL.add((TextView) ap.findViewById(R.id.w1));
        wordsL.add((TextView) ap.findViewById(R.id.w2));
        wordsL.add((TextView) ap.findViewById(R.id.w3));
        wordsL.add((TextView) ap.findViewById(R.id.w4));
        return wordsL;
    }

    private TextView countLabel() {
        return (TextView) activePage().findViewById(R.id.count);
    }

    private EditText wordInput() {
//        return (EditText) activePage().findViewById(R.id.enterWord);
        return (EditText) findViewById(R.id.enterWord);
    }

    private TextView rangeView() {
        return (TextView) activePage().findViewById(R.id.range);
    }

    private void submit() {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    wsClient.submitWord(wordInput().getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                load();
            }
        }.execute();
    }

    private void load() {
        new AsyncTask<Void,Void,Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return wsClient.getWordsCount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            protected void onPostExecute(Integer wordsCount) {
                countLabel().setText("" + wordsCount);
                if(pos < 0 ) pos = 0;
                if(pos >= wordsCount) pos -= N_DISPLAYED;
                loadWords(pos);
            }
        }.execute();
    }

    private void loadWords(final int start) {

        new AsyncTask<Void,Void,List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                try {
                    return wsClient.getRegionWords(start, N_DISPLAYED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Collections.emptyList();
            }

            @Override
            protected void onPostExecute(List<String> words) {
                rangeView().setText(
                        String.format("Words from %d to %d", (pos + 1), (pos + N_DISPLAYED)));
                Iterator<TextView> viewIt = wordsViews().iterator();
                Iterator<String> wordIt = words.iterator();
                Animation in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(ANIM_DURATION);
                fadePanel().setVisibility(View.VISIBLE);
                fadePanel().startAnimation(in);
                while (viewIt.hasNext() && wordIt.hasNext()) {
                    TextView v = viewIt.next();
                    String w = wordIt.next();
                    v.setVisibility(View.VISIBLE);
                    v.setText(w.toUpperCase());
                }
                while (viewIt.hasNext()) viewIt.next().setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    private float fromPosition;
    private boolean slideFlag = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                slideFlag = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(slideFlag) {
                    float toPosition = event.getX();
                    if (toPosition < fromPosition - SLIDE_SENS) {
                        slideFlag = false;
                        showNext();
                    } else if (toPosition > fromPosition + SLIDE_SENS) {
                        slideFlag = false;
                        showPrevious();
                    }
                }
            default:
                break;
        }
        return true;
    }

    private int pos = 0;

    private void showPrevious() {
        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_out));
        flipper.showPrevious();
        hideWordsViews();
        pos -= N_DISPLAYED;
        load();
    }

    private void showNext() {
        flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_next_out));
        flipper.showNext();
        hideWordsViews();
        pos += N_DISPLAYED;
        load();
    }

    private void hideWordsViews() {
        fadePanel().setVisibility(View.INVISIBLE);
    }
}
