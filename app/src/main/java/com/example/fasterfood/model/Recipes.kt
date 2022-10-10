package com.example.fasterfood.model
//simple model class for recipes with attributes
//to get data from firebase
data class Recipes(var deleted: Boolean ?= false, var recipeId: String ?= null, var Name : String ?= null, var Description : String ?= null, var ImageId: String ?= null)
