package com.example.fasterfood.model

//simple model class for create recipes with some attributes
class CreateRecipes(var userId: String,var Name : String ?= null, var Description : String ?= null, var Ingredients: String ?= null, var imageId: String, var isDeleted: Boolean, var steps: List<String>, var ratings: Rating ?= null)