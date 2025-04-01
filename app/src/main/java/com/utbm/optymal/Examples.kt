package com.utbm.optymal

import kotlin.random.Random


enum class CarField(val key: String) {
    CAR_ID("carId"),
    BRAND("brand"),
    MODEL("model"),
    YEAR("year"),
    COLOR("color"),
    PRICE("price"),
    MILEAGE("mileage"),
    OWNER_ID("ownerId"),
    USER_ID("userId");
}


enum class UserField(val key: String) {
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    ADDRESS("address"),
    CITY("city"),
    OWNED_CARS_UID("ownedCarsUid"),
    RENTED_CAR_UID("rentedCarUid");
}

data class Car(
    val brand: String,
    val model: String,
    val year: Int,
    val color: String,
    val price: Double,
    val mileage: Int,
    val ownerId: String,
    val userId: String
){

    fun toMap(): Map<String, Any> {
        return mapOf(
            CarField.BRAND.key to brand,
            CarField.MODEL.key to model,
            CarField.YEAR.key to year,
            CarField.COLOR.key to color,
            CarField.PRICE.key to price,
            CarField.MILEAGE.key to mileage,
            CarField.OWNER_ID.key to ownerId,
            CarField.USER_ID.key to userId
        )
    }

}

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val city: String,
    var ownedCarsId: List<String>,
    var rentedCarId: String
){

    constructor(email: String) : this("", "", email, "", "", "", emptyList(), "")

    fun toMap(): Map<String, Any> {
        return mapOf(
            UserField.FIRST_NAME.key to firstName,
            UserField.LAST_NAME.key to lastName,
            UserField.EMAIL.key to email,
            UserField.PHONE_NUMBER.key to phoneNumber,
            UserField.ADDRESS.key to address,
            UserField.CITY.key to city,
            UserField.OWNED_CARS_UID.key to ownedCarsId,
            UserField.RENTED_CAR_UID.key to rentedCarId
        )
    }


}



val user1 = User(
    firstName = "John",
    lastName = "Doe",
    email = "john.doe@example.com",
    phoneNumber = "123-456-7890",
    address = "123 Main St",
    city = "New York",
    ownedCarsId = listOf("car_101", "car_102"),
    rentedCarId = "car_103"
)

val user2 = User(
    firstName = "Jane",
    lastName = "Smith",
    email = "jane.smith@example.com",
    phoneNumber = "987-654-3210",
    address = "456 Elm St",
    city = "Los Angeles",
    ownedCarsId = listOf("car_104"),
    rentedCarId = ""
)

val car1 = Car(
    brand = "Toyota",
    model = "Camry",
    year = 2020,
    color = "Blue",
    price = 25000.0,
    mileage = 30000,
    ownerId = "user_001",
    userId = ""
)

val car2 = Car(
    brand = "Honda",
    model = "Civic",
    year = 2019,
    color = "Black",
    price = 22000.0,
    mileage = 40000,
    ownerId = "user_001",
    userId = "user_002"  // Currently rented by user_002
)

val car3 = Car(
    brand = "Ford",
    model = "Focus",
    year = 2021,
    color = "Red",
    price = 27000.0,
    mileage = 15000,
    ownerId = "user_003",
    userId = "user_001"  // Rented by user_001
)

val car4 = Car(
    brand = "Tesla",
    model = "Model 3",
    year = 2022,
    color = "White",
    price = 55000.0,
    mileage = 5000,
    ownerId = "user_002",
    userId = ""
)



private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

/**
 * Generates a random alphanumeric string of a specified length.
 */
private fun generateRandomString(length: Int = Random.nextInt(8, 16)): String {
    return (1..length)
        .map { Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}

// --- Randomization Functions ---

/**
 * Creates a Car object with randomized data, including random strings
 * for string fields.
 */
fun randomizeCar(): Car {
    val year = Random.nextInt(1990, 2025) // Year between 1990 and 2024
    val price = Random.nextDouble(3000.0, 95000.0) // Price between $3k and $95k
    val mileage = Random.nextInt(0, 250001) // Mileage between 0 and 250k

    // Decide if the car is currently rented/assigned to a user (e.g., 30% chance)
    val randomUserId = if (Random.nextInt(0, 10) < 3) generateRandomString() else ""

    return Car(
        brand = generateRandomString(),
        model = generateRandomString(),
        year = year,
        color = generateRandomString(length = Random.nextInt(5, 10)), // Shorter for color?
        price = price,
        mileage = mileage,
        ownerId = generateRandomString(), // Typically an ID
        userId = randomUserId         // Can be empty or an ID
    )
}

/**
 * Creates a User object with randomized data, including random strings
 * for string fields.
 */
fun randomizeUser(): User {
    // Generate random number of owned car IDs (e.g., 0 to 5)
    val numOwnedCars = Random.nextInt(0, 6)
    val randomOwnedCarIds = List(numOwnedCars) { generateRandomString() }

    // Decide if the user is currently renting a car (e.g., 50% chance)
    val randomRentedCarId = if (Random.nextBoolean()) generateRandomString() else ""

    return User(
        firstName = generateRandomString(length = Random.nextInt(5, 12)),
        lastName = generateRandomString(length = Random.nextInt(5, 15)),
        email = generateRandomString(length = Random.nextInt(10, 25)), // Just random chars for email too
        phoneNumber = generateRandomString(length = Random.nextInt(10, 15)), // Random chars for phone
        address = generateRandomString(length = Random.nextInt(15, 40)),
        city = generateRandomString(length = Random.nextInt(6, 18)),
        ownedCarsId = randomOwnedCarIds,
        rentedCarId = randomRentedCarId
    )
}