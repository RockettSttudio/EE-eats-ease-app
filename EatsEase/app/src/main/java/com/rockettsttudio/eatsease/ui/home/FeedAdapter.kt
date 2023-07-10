package com.rockettsttudio.eatsease.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.data.models.RecipeFeed

class FeedAdapter(
    private var recipes: List<RecipeFeed>,
    private val itemClickListener: HomeFragment
) : RecyclerView.Adapter<FeedAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setRecipes(recipes: List<RecipeFeed>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipe_image_view_home)
        private val titleTextView: TextView = itemView.findViewById(R.id.recipe_title_view_home)
        private val descTextView: TextView = itemView.findViewById(R.id.recipe_desc_view_home)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(recipe: RecipeFeed) {
            titleTextView.text = recipe.title
            descTextView.text = recipe.description
            Glide.with(itemView.context).load(R.drawable.svg_layer4).into(recipeImageView)
        }

        override fun onClick(v: View) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedRecipe = recipes[position]
                itemClickListener.onItemClick(clickedRecipe)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(recipe: RecipeFeed)
    }

}