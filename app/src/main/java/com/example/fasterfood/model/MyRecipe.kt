package com.example.fasterfood.model
//simple model class for recipes with some attributes
data class MyRecipe(var recipeId: String ?= null, var Name : String ?= null, var Description : String ?= null, var ImageId: String ?= null, var userId: String ?= null, var deleted: Boolean ?= false)
