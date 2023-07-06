package com.rockettsttudio.eatsease.ui.saved

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.data.models.Recipe
import com.rockettsttudio.eatsease.data.models.SavedRecipes
import com.rockettsttudio.eatsease.ui.recipes.RecipeAdapter
import com.rockettsttudio.eatsease.ui.recipes.RecipesFragment

class SavedAdapter(
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SavedAdapter.SavedRecipeViewHolder>() {

    private val recipes = mutableListOf<SavedRecipes>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.saved_item, parent, false)
        return SavedRecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedRecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setRecipes(savedRecipes: List<SavedRecipes>) {
        recipes.clear()
        recipes.addAll(savedRecipes)
        notifyDataSetChanged()
    }

    inner class SavedRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipe_image_view_saved)
        private val titleTextView: TextView = itemView.findViewById(R.id.recipe_title_view_saved)
        private val descTextView: TextView = itemView.findViewById(R.id.recipe_desc_view_saved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(recipe: SavedRecipes) {
            titleTextView.text = recipe.recipeTitle
            descTextView.text = recipe.recipeSummary.substring(0, 120)
            Glide.with(itemView.context)
                .load(recipe.imgUrl)
                .placeholder(R.drawable.svg_layer4)
                .into(recipeImageView)
        }

        override fun onClick(view: View) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedRecipe = recipes[position]
                itemClickListener.onItemClick(clickedRecipe)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(recipe: SavedRecipes)
    }
}


