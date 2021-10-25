package com.example.imageclassification;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.jacquard.sdk.JacquardManager;
import com.google.android.jacquard.sdk.tag.AdvertisedJacquardTag;
import java.util.List;


public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.AdvertisedJacquardTagViewHolder> {

  private List<AdvertisedJacquardTag> tagList;

  public TagListAdapter(List<AdvertisedJacquardTag> tagList) {
    this.tagList = tagList;
  }

  @NonNull
  @Override
  public AdvertisedJacquardTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
      int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    // changed from tag_item to activity_main
    // possible bug
    View view = layoutInflater.inflate(R.layout.tag_item, parent, false);

    return new AdvertisedJacquardTagViewHolder(view);
  }

  @Override
  public int getItemCount() {
    return tagList.size();
  }

  @Override
  public void onBindViewHolder(@NonNull AdvertisedJacquardTagViewHolder holder, int position) {
    holder.bindView(tagList.get(position).identifier());
    holder.bindView(tagList.get(position).displayName());
  }

  class AdvertisedJacquardTagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final View view;

    AdvertisedJacquardTagViewHolder(View itemView) {
      super(itemView);
      view = itemView;
      view.setOnClickListener(this);
    }

    void bindView(String name) {
      TextView tv = view.findViewById(R.id.tag_item_name);
      tv.setText(name);
    }

    @Override
    public void onClick(View v) {
      // Item click
      Log.e("Automation", "Clicking the Tag name");
      JacquardManager jacquardManager = JacquardManager.getInstance();


    }
  }
}
