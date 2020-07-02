package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;


    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemTweetBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTweetBinding binding;

        public ViewHolder(ItemTweetBinding b) {
            super(b.getRoot());
            binding = b;

            binding.ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Tweet replyToTweet = tweets.get(position);
                    String replyTo = replyToTweet.idString;
                    String screenName = replyToTweet.user.screenName;
                    showReplyDialog(replyTo, screenName);
                }
            });
        }

        public void bind(Tweet tweet) {
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText("@"+tweet.user.screenName);
            binding.tvName.setText(tweet.user.name);
            binding.tvRelativeTime.setText(tweet.relativeTime);
            binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
            binding.tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
            Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCorners(16)).into(binding.ivProfileImage);
            if (tweet.entities.mediaUrl != null) {
                Glide
                        .with(context)
                        .load(tweet.entities.mediaUrl)
                        .into(binding.ivMedia);
            }

        }

        private void showReplyDialog(String replyTo, String screenName) {
            FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
            ComposeFragment replyFragment = ComposeFragment.newInstance(replyTo, screenName);
            replyFragment.show(fm, "fragment_compose");
        }
    }
}
