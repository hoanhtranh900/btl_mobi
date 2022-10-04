package com.mrash.instagramclone.View;

//class SearchView(
//        context: Context,
//        attrs: AttributeSet
//) : FrameLayout(context, attrs) {
//
//        init {
//        LayoutInflater.from(context)
//        .inflate(R.layout.view_search, this, true)
//
//        open_search_button.setOnClickListener { openSearch() }
//        close_search_button.setOnClickListener { closeSearch() }
//        }
//        }
//private fun openSearch() {
//        search_input_text.setText("")
//        search_open_view.visibility = View.VISIBLE
//        val circularReveal = ViewAnimationUtils.createCircularReveal(
//        search_open_view,
//        (open_search_button.right + open_search_button.left) / 2,
//        (open_search_button.top + open_search_button.bottom) / 2,
//        0f, width.toFloat()
//        )
//        circularReveal.duration = 300
//        circularReveal.start()
//        }
//private fun closeSearch() {
//        val circularConceal = ViewAnimationUtils.createCircularReveal(
//        search_open_view,
//        (open_search_button.right + open_search_button.left) / 2,
//        (open_search_button.top + open_search_button.bottom) / 2,
//        width.toFloat(), 0f
//        )
//
//        circularConceal.duration = 300
//        circularConceal.start()
//        circularConceal.addListener(object : Animator.AnimatorListener {
//        override fun onAnimationRepeat(animation: Animator?) = Unit
//        override fun onAnimationCancel(animation: Animator?) = Unit
//        override fun onAnimationStart(animation: Animator?) = Unit
//        override fun onAnimationEnd(animation: Animator?) {
//        search_open_view.visibility = View.INVISIBLE
//        search_input_text.setText("")
//        circularConceal.removeAllListeners()
//        }
//        })
//        }
import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import com.mrash.instagramclone.R;

public class SearchView extends FrameLayout {
    private View openSearchButton;
    private View closeSearchButton;
    private EditText searchInputText;
    private RelativeLayout searchOpenView;
    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_search, this, true);
        openSearchButton = findViewById(R.id.open_search_button);
        closeSearchButton = findViewById(R.id.close_search_button);
        openSearchButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                openSearch();
            }


        });
        closeSearchButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                closeSearch();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeSearch() {
        Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(searchOpenView, (openSearchButton.getRight() + openSearchButton.getLeft()) / 2, (openSearchButton.getTop() + openSearchButton.getBottom()) / 2, getWidth(), 0);
        createCircularReveal.setDuration(300);
        createCircularReveal.start();
        createCircularReveal.addListener(new Animator.AnimatorListener() {
            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                searchOpenView.setVisibility(View.INVISIBLE);
                searchInputText.setText("");
                createCircularReveal.removeAllListeners();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openSearch() {
        searchInputText.setText("");
        searchOpenView.setVisibility(View.VISIBLE);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                searchOpenView,
                (openSearchButton.getRight() + openSearchButton.getLeft()) / 2,
                (openSearchButton.getTop() + openSearchButton.getBottom()) / 2,
                0f, getWidth()
        );
        circularReveal.setDuration(300);
        circularReveal.start();
    }
}