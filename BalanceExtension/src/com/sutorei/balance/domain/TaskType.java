package com.sutorei.balance.domain;

public enum TaskType {
	ESTABLISH_BALANCE, //DONE fill the balance with objects and establish balance
	CHECKOUT_BALANCE, //DONE fill the biased balance in a correct way
	FIND_MASS, //find mass of several objects and fill the table
	FIND_MASS_USING_EQUATION, //find mass of a total package
	FIND_MASS_INTERACRTIVE, //same but able to move weights
	FIND_MASS_USING_EQUATION_INTERACTIVE, // ----
	LINE_UP_OBJECTS, //sort objects by mass and fill the table
	FIND_THE_DIFFERENCE //find the difference between objects for each balance
	;
}
