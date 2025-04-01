package com.utbm.optymal




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
    USER_ID("userId"),
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
    var carId: String,
    val brand: String,
    val model: String,
    val year: Int,
    val color: String,
    val price: Double,
    val mileage: Int,
    val ownerId: String,
    val userId: String
){

    fun setId(id: String) {
        carId = id
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            CarField.CAR_ID.key to carId,
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
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val city: String,
    var ownedCarsUid: List<String>,
    var rentedCarUid: String
){

    constructor(userId: String,email: String) : this(userId, "", "", email, "", "", "", emptyList(), "")

    fun toMap(): Map<String, Any> {
        return mapOf(
            UserField.USER_ID.key to userId,
            UserField.FIRST_NAME.key to firstName,
            UserField.LAST_NAME.key to lastName,
            UserField.EMAIL.key to email,
            UserField.PHONE_NUMBER.key to phoneNumber,
            UserField.ADDRESS.key to address,
            UserField.CITY.key to city,
            UserField.OWNED_CARS_UID.key to ownedCarsUid,
            UserField.RENTED_CAR_UID.key to rentedCarUid
        )
    }


}



val user1 = User(
    userId = "user_001",
    firstName = "John",
    lastName = "Doe",
    email = "john.doe@example.com",
    phoneNumber = "123-456-7890",
    address = "123 Main St",
    city = "New York",
    ownedCarsUid = listOf("car_101", "car_102"),
    rentedCarUid = "car_103"
)

val user2 = User(
    userId = "user_002",
    firstName = "Jane",
    lastName = "Smith",
    email = "jane.smith@example.com",
    phoneNumber = "987-654-3210",
    address = "456 Elm St",
    city = "Los Angeles",
    ownedCarsUid = listOf("car_104"),
    rentedCarUid = ""
)

val car1 = Car(
    carId = "car_101",
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
    carId = "car_102",
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
    carId = "car_103",
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
    carId = "car_104",
    brand = "Tesla",
    model = "Model 3",
    year = 2022,
    color = "White",
    price = 55000.0,
    mileage = 5000,
    ownerId = "user_002",
    userId = ""
)