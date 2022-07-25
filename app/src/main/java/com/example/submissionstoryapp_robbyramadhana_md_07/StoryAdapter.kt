package com.example.submissionstoryapp_robbyramadhana_md_07

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionstoryapp_robbyramadhana_md_07.databinding.ListItemStoryBinding

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private var storyList: List<Story>? = null

    fun setStoryList(storyList: List<Story>?) {
        this.storyList = storyList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = ListItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(storyList?.get(position)!!)
    }

    override fun getItemCount(): Int {
        return if (storyList == null) 0
        else storyList?.size!!
    }

    inner class StoryViewHolder(private val binding: ListItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                intent.putExtra("Story", data)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.listPhoto, "image"),
                        Pair(binding.listName, "name"),
                        Pair(binding.listDescription, "description")
                    )

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }

            binding.apply {
                listName.text = data.name
                listDescription.text = data.description

                Glide.with(itemView)
                    .load(data.photoUrl)
                    .into(listPhoto)
            }

        }
    }

}