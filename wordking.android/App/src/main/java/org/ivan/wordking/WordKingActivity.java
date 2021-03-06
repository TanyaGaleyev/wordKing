package org.ivan.wordking;

import android.app.Activity;
import android.graphics.Color;
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
        Button submitBtn = (Button) findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        checkedLoad(pos);
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
        return (EditText) findViewById(R.id.enterWord);
    }

    private TextView rangeView() {
        return (TextView) activePage().findViewById(R.id.range);
    }

    private void submit() {
        new AsyncTask<Void,Void,Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return wsClient.submitWord(wordInput().getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            protected void onPostExecute(Integer position) {
                checkedLoad(position);
                System.out.println(position);
            }
        }.execute();
    }

    private void checkedLoad(final int position0) {
        new AsyncTask<Void,Void,Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return wsClient.getWordsCount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return pos;
            }

            @Override
            protected void onPostExecute(Integer wordsCount) {
                countLabel().setText("" + wordsCount);
                int position = position0;
                if(position0 < 0 ) position = 0;
                if(position0 >= wordsCount) position = (wordsCount - 1) - (wordsCount - 1) % N_DISPLAYED;
                pos = position - position % N_DISPLAYED;
                loadWords(pos, position - pos);
            }
        }.execute();
    }

    private void loadWords(final int start, final int highlight) {

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
                    if(words.indexOf(w) == highlight)   v.setTextColor(Color.RED);
                    else                                v.setTextColor(Color.BLACK);
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
        checkedLoad(pos - N_DISPLAYED);
    }

    private void showNext() {
        flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_next_out));
        flipper.showNext();
        hideWordsViews();
        checkedLoad(pos + N_DISPLAYED);
    }

    private void hideWordsViews() {
        fadePanel().setVisibility(View.INVISIBLE);
    }
}
