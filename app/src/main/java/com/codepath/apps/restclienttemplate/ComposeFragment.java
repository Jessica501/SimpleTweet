package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.restclienttemplate.databinding.FragmentComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.apache.commons.codec.binary.StringUtils;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

public class ComposeFragment extends DialogFragment{
    FragmentComposeBinding binding;

    private static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "ComposeFragment";

    TwitterClient client;

    boolean reply;
    String replyTo;
    String replyToScreenName;

    // empty constructor required for DialogFragment
    public ComposeFragment() {
    }

    public interface ComposeListener {
        void onFinishCompose(int resultCode, Parcelable parcelable);
    }

    public static ComposeFragment newInstance(String replyTo, String screenName) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("replyTo", replyTo);
        args.putString("screenName", screenName);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentComposeBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        binding.etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        client = TwitterApp.getRestClient(getContext());

        replyTo = this.getArguments().getString("replyTo");
        replyToScreenName = this.getArguments().getString("screenName");
        reply = replyTo.length() > 0;

        if (reply) {
            binding.tilCompose.setCounterMaxLength(MAX_TWEET_LENGTH-replyToScreenName.length()-2);
            binding.tvReplyTo.setText("replying to @"+replyToScreenName);
        }
        else {
            binding.tilCompose.setCounterMaxLength(MAX_TWEET_LENGTH);
            binding.tvReplyTo.setVisibility(GONE);

        }

        // set a click listener on button
        binding.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = binding.etCompose.getText().toString();
                if (reply) {
                    tweetContent = "@"+replyToScreenName+ " " + tweetContent;
                }
                if (tweetContent.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(getContext(), "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getContext(), tweetContent, Toast.LENGTH_LONG).show();
                // make an API call to twitter to publish the tweet
                client.publishTweet(tweetContent, replyTo, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            ((ComposeListener)getActivity()).onFinishCompose(RESULT_OK, Parcels.wrap(tweet));
                            dismiss();

                        } catch (JSONException e) {
                            Log.e("ComposeFragment", "onFailure to publish tweet", e);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });

            }
        });
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        // Call super onResume after sizing
        super.onResume();
    }

}
