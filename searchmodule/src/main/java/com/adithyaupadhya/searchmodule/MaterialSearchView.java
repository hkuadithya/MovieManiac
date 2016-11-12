package com.adithyaupadhya.searchmodule;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


/**
 * @author Miguel Catalan Bañuls
 */
public class MaterialSearchView extends FrameLayout implements
        View.OnClickListener,
        TextView.OnEditorActionListener,
        TextWatcher,
        View.OnFocusChangeListener,
        SearchRecyclerAdapter.SearchItemClickListener, MenuItem.OnMenuItemClickListener {

    public static final int REQUEST_VOICE = 10000;

    private boolean mIsSearchOpen = false;
    private int mAnimationDuration;
    private boolean mClearingFocus;

    //Views
    private View mSearchLayout;
    private View mTintView;
    private RecyclerView mRecyclerView;
    private EditText mSearchSrcTextView;
    private ImageButton mBackBtn;
    private ImageButton mVoiceBtn;
    private ImageButton mEmptyBtn;
    private RelativeLayout mSearchTopBar;

    private CharSequence mUserQuery;

    private OnQueryTextListener mOnQueryChangeListener;
    private SearchViewListener mSearchViewListener;

    private SavedState mSavedState;

    private boolean allowVoiceSearch;

    private Fragment mVoiceSearchFragment;
    private long mOldTimestamp;

    private SearchRecyclerAdapter mSearchAdapter;

    public MaterialSearchView(Context context) {
        this(context, null);
    }

    public MaterialSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        initiateView();

        initStyle(attrs, defStyleAttr);
    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, defStyleAttr, 0);

        if (a != null) {
            if (a.hasValue(R.styleable.MaterialSearchView_searchBackground)) {
                setBackground(a.getDrawable(R.styleable.MaterialSearchView_searchBackground));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_android_textColor)) {
                setTextColor(a.getColor(R.styleable.MaterialSearchView_android_textColor, 0));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_android_textColorHint)) {
                setHintTextColor(a.getColor(R.styleable.MaterialSearchView_android_textColorHint, 0));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_android_hint)) {
                setHint(a.getString(R.styleable.MaterialSearchView_android_hint));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchVoiceIcon)) {
                setVoiceIcon(a.getDrawable(R.styleable.MaterialSearchView_searchVoiceIcon));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchCloseIcon)) {
                setCloseIcon(a.getDrawable(R.styleable.MaterialSearchView_searchCloseIcon));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchBackIcon)) {
                setBackIcon(a.getDrawable(R.styleable.MaterialSearchView_searchBackIcon));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionBackground)) {
                setSuggestionBackground(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionBackground));
            }


            a.recycle();
        }
    }

    private void initiateView() {
        LayoutInflater.from(getContext()).inflate(R.layout.search_view, this, true);
        mSearchLayout = findViewById(R.id.search_layout);

        mSearchTopBar = (RelativeLayout) mSearchLayout.findViewById(R.id.search_top_bar);
        mRecyclerView = (RecyclerView) mSearchLayout.findViewById(R.id.suggestion_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSearchSrcTextView = (EditText) mSearchTopBar.findViewById(R.id.searchTextView);
        mBackBtn = (ImageButton) mSearchTopBar.findViewById(R.id.action_up_btn);
        mVoiceBtn = (ImageButton) mSearchTopBar.findViewById(R.id.action_voice_btn);
        mEmptyBtn = (ImageButton) mSearchTopBar.findViewById(R.id.action_empty_btn);

        mTintView = mSearchLayout.findViewById(R.id.transparent_view);

        mSearchSrcTextView.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mVoiceBtn.setOnClickListener(this);
        mEmptyBtn.setOnClickListener(this);
        mTintView.setOnClickListener(this);

        allowVoiceSearch = false;

        showVoice(true);

        initSearchView();

        setAnimationDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM);

        mSearchAdapter = new SearchRecyclerAdapter(getContext(), this);

        mRecyclerView.setAdapter(mSearchAdapter);

    }

    private void initSearchView() {
        mSearchSrcTextView.setOnEditorActionListener(this);
        mSearchSrcTextView.addTextChangedListener(this);
        mSearchSrcTextView.setOnFocusChangeListener(this);
    }

    private void onVoiceClicked() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);    // setting recognition model, optimized for short phrases – search queries
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);    // quantity of results we want to receive

        if (mVoiceSearchFragment != null) {
            hideKeyboard(this);
            mVoiceSearchFragment.startActivityForResult(intent, REQUEST_VOICE);
        }

    }

    private void onTextChanged(CharSequence newText) {
        mUserQuery = newText;

        if (TextUtils.isEmpty(newText)) {

            mEmptyBtn.setVisibility(GONE);
            showVoice(true);

        } else {

            mEmptyBtn.setVisibility(VISIBLE);
            showVoice(false);

            if (mOnQueryChangeListener != null && newText.length() >= 2 && (System.currentTimeMillis() - mOldTimestamp) > 500) {
                // More than 3 characters and 500 ms should have elapsed
                mOldTimestamp = System.currentTimeMillis();
                mOnQueryChangeListener.onQueryTextChange(newText.toString());
//                Log.d("MMVOLLEY-", "Search API called");
            }
        }

    }

    private void onSubmitQuery() {
        CharSequence query = mSearchSrcTextView.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {

            if (mOnQueryChangeListener != null)
                mOnQueryChangeListener.onQueryTextSubmit(query.toString());

            closeSearch();
            mSearchSrcTextView.setText(null);

        }
    }

    private boolean isVoiceAvailable() {
        return isInEditMode() || SpeechRecognizer.isRecognitionAvailable(getContext());
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showKeyboard(View view) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view.hasFocus()) {
            view.clearFocus();
        }
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    //Public Attributes

    @Override
    public void setBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSearchTopBar.setBackground(background);
        } else {
            mSearchTopBar.setBackgroundDrawable(background);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        mSearchTopBar.setBackgroundColor(color);
    }

    public void setTextColor(int color) {
        mSearchSrcTextView.setTextColor(color);
    }

    public void setHintTextColor(int color) {
        mSearchSrcTextView.setHintTextColor(color);
    }

    public void setHint(CharSequence hint) {
        mSearchSrcTextView.setHint(hint);
    }

    public void setVoiceIcon(Drawable drawable) {
        mVoiceBtn.setImageDrawable(drawable);
    }

    public void setCloseIcon(Drawable drawable) {
        mEmptyBtn.setImageDrawable(drawable);
    }

    public void setBackIcon(Drawable drawable) {
        mBackBtn.setImageDrawable(drawable);
    }


    public void setSuggestionBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRecyclerView.setBackground(background);
        } else {
            mRecyclerView.setBackgroundDrawable(background);
        }
    }

    public void setVoiceSearch(boolean voiceSearch, Fragment voiceSearchFragment) {
        allowVoiceSearch = voiceSearch;
        this.mVoiceSearchFragment = voiceSearchFragment;
    }

    //Public Methods

    private void showSuggestions() {
        if (mRecyclerView != null && mSearchAdapter.getItemCount() > 0 && mRecyclerView.getVisibility() == GONE) {
            mRecyclerView.setVisibility(VISIBLE);
        }
    }


    public void setNewSuggestions(List<String> suggestions) {
        if (suggestions != null && suggestions.size() > 0) {
            mSearchAdapter.setNewSuggestions(suggestions);
            mRecyclerView.setVisibility(VISIBLE);
        }
    }


    private void dismissSuggestions() {
        if (mRecyclerView.getVisibility() == VISIBLE) {
            mRecyclerView.setVisibility(GONE);
        }
    }


    /**
     * Calling this will set the query to search text box. if submit is true, it'll submit the query.
     *
     * @param query  query string shown in search view
     * @param submit flag to indicate whether search action to be taken.
     */
    public void setQuery(CharSequence query, boolean submit) {
        mSearchSrcTextView.setText(query);
        if (query != null) {
            mSearchSrcTextView.setSelection(mSearchSrcTextView.length());
            mUserQuery = query;
            showKeyboard(mSearchSrcTextView);
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    /**
     * if show is true, this will enable voice search. If voice is not available on the device, this method call has not effect.
     *
     * @param show flag to disable/enable voice search
     */
    public void showVoice(boolean show) {
        if (show && isVoiceAvailable() && allowVoiceSearch) {
            mVoiceBtn.setVisibility(VISIBLE);
        } else {
            mVoiceBtn.setVisibility(GONE);
        }
    }

    /**
     * Call this method and pass the menu item so this class can handle click events for the Menu Item.
     *
     * @param menuItem
     */
    public void setMenuItem(MenuItem menuItem) {
        menuItem.setOnMenuItemClickListener(this);
    }

    /**
     * Return true if search is open
     *
     * @return
     */
    public boolean isSearchOpen() {
        return mIsSearchOpen;
    }

    /**
     * Sets animation duration. ONLY FOR PRE-LOLLIPOP!!
     *
     * @param duration duration of the animation
     */
    public void setAnimationDuration(int duration) {
        mAnimationDuration = duration;
    }

    /**
     * Open Search View. This will animate the showing of the view.
     */
    public void showSearch() {
        showSearch(true);
    }

    /**
     * Open Search View. If animate is true, Animate the showing of the view.
     *
     * @param animate true for animate
     */
    public void showSearch(boolean animate) {
        if (isSearchOpen()) {
            return;
        }

        //Request Focus
        mSearchSrcTextView.setText(null);
        mSearchSrcTextView.requestFocus();

        if (animate) {
            setVisibleWithAnimation();

        } else {
            mSearchLayout.setVisibility(VISIBLE);
            if (mSearchViewListener != null) {
                mSearchViewListener.onSearchViewShown();
            }
        }
        mIsSearchOpen = true;
    }

    private void setVisibleWithAnimation() {
        AnimationUtil.AnimationListener animationListener = new AnimationUtil.AnimationListener() {
            @Override
            public boolean onAnimationStart(View view) {
                return false;
            }

            @Override
            public boolean onAnimationEnd(View view) {
                if (mSearchViewListener != null) {
                    mSearchViewListener.onSearchViewShown();
                }
                return false;
            }

            @Override
            public boolean onAnimationCancel(View view) {
                return false;
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSearchLayout.setVisibility(View.VISIBLE);
            AnimationUtil.reveal(mSearchTopBar, animationListener);

        } else {
            AnimationUtil.fadeInView(mSearchLayout, mAnimationDuration, animationListener);
        }
    }

    /**
     * Close search view.
     */
    public void closeSearch() {
        if (!isSearchOpen()) {
            return;
        }

        mSearchSrcTextView.setText(null);
        dismissSuggestions();

        mSearchAdapter.setNewSuggestions(null);

        clearFocus();

        mSearchLayout.setVisibility(GONE);
        if (mSearchViewListener != null) {
            mSearchViewListener.onSearchViewClosed();
        }
        mIsSearchOpen = false;

    }

    /**
     * Set this listener to listen to Query Change events.
     *
     * @param listener
     */
    public void setOnQueryTextListener(OnQueryTextListener listener) {
        mOnQueryChangeListener = listener;
    }

    /**
     * Set this listener to listen to Search View open and close events
     *
     * @param listener
     */
    public void setOnSearchViewListener(SearchViewListener listener) {
        mSearchViewListener = listener;
    }


    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        // Don't accept focus if in the middle of clearing focus
        if (mClearingFocus) return false;
        // Check if SearchView is focusable.
        return isFocusable() && mSearchSrcTextView.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void clearFocus() {
        mClearingFocus = true;
        hideKeyboard(this);
        super.clearFocus();
        mSearchSrcTextView.clearFocus();
        mClearingFocus = false;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        mSavedState = new SavedState(superState);
        mSavedState.query = mUserQuery != null ? mUserQuery.toString() : null;
        mSavedState.isSearchOpen = this.mIsSearchOpen;

        return mSavedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        mSavedState = (SavedState) state;

        if (mSavedState.isSearchOpen) {
            showSearch(false);
            setQuery(mSavedState.query, false);
        }

        super.onRestoreInstanceState(mSavedState.getSuperState());
    }

    static class SavedState extends BaseSavedState {
        String query;
        boolean isSearchOpen;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.query = in.readString();
            this.isSearchOpen = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(query);
            out.writeInt(isSearchOpen ? 1 : 0);
        }

        //required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public interface OnQueryTextListener {

        /**
         * Called when the user submits the query. This could be due to a key press on the
         * keyboard or due to pressing a submit button.
         * The listener can override the standard behavior by returning true
         * to indicate that it has handled the submit request. Otherwise return false to
         * let the SearchView handle the submission by launching any associated intent.
         *
         * @param query the query text that is to be submitted
         * @return true if the query has been handled by the listener, false to let the
         * SearchView perform the default action.
         */
        boolean onQueryTextSubmit(String query);

        /**
         * Called when the query text is changed by the user.
         *
         * @param newText the new content of the query text field.
         * @return false if the SearchView should perform the default action of showing any
         * suggestions if available, true if the action was handled by the listener.
         */
        boolean onQueryTextChange(String newText);
    }

    public interface SearchViewListener {
        void onSearchViewShown();

        void onSearchViewClosed();
    }


    // SEARCH TEXT VIEW LISTENER CALLBACK METHODS
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onSubmitQuery();
        return true;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mUserQuery = s;
        onTextChanged(s);
//        startFilter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            showKeyboard(mSearchSrcTextView);
            showSuggestions();
            mTintView.setVisibility(View.VISIBLE);
        }
    }


    // RECYCLER VIEW ON CLICK & VIEW ON CLICK

    @Override
    public void onSearchItemClick(String searchString) {
        setQuery(searchString, true);
    }
    // ON CLICK EVENT HANDLER

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.action_up_btn) {
            closeSearch();

        } else if (i == R.id.action_voice_btn) {
            onVoiceClicked();

        } else if (i == R.id.action_empty_btn) {
            mSearchSrcTextView.setText(null);
            mSearchAdapter.setNewSuggestions(null);

        } else if (i == R.id.searchTextView) {
            showSuggestions();

        } else if (i == R.id.transparent_view) {
            closeSearch();

        }
    }

    // ON MENU ITEM CLICK
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        showSearch();
        return true;
    }

}