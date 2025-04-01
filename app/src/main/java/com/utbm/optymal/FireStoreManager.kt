package com.utbm.optymal

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale


class FireStoreManager {
    private val db = FirebaseFirestore.getInstance()
    val usersStr = "users"
    val carsStr= "cars"

    private fun getUsers(): CollectionReference {
        return db.collection(usersStr)
    }

    private fun getUserDoc(userId: String): DocumentReference {
        return getUsers().document(userId)
    }

    private fun getUserData(userId: String): Task<DocumentSnapshot?> {
        return getUserDoc(userId).get().addOnCompleteListener {
                task -> task.result
        }.addOnFailureListener {
                task -> null
        }
    }


    private fun getCars(): CollectionReference {
        return db.collection(carsStr)
    }

    private fun getCarDoc(carId: String): DocumentReference {
        return getCars().document(carId)
    }

    private fun getCarDoc(): DocumentReference {
        return getCars().document()
    }

    private fun getCarData(carId: String): Task<DocumentSnapshot?> {
        return getCarDoc(carId).get().addOnCompleteListener {
                task -> task.result
        }.addOnFailureListener {
                task -> null
        }
    }



    private fun convertDataToUser(document: DocumentSnapshot?): User? {
        return document?.let {
            User(
                it.getString(UserField.FIRST_NAME.key) ?: "",
                it.getString(UserField.LAST_NAME.key) ?: "",
                it.getString(UserField.EMAIL.key) ?: "",
                it.getString(UserField.PHONE_NUMBER.key) ?: "",
                it.getString(UserField.ADDRESS.key) ?: "",
                it.getString(UserField.CITY.key) ?: "",
                it.get(UserField.OWNED_CARS_UID.key) as? List<String> ?: emptyList(), // Handle null safely
                it.getString(UserField.RENTED_CAR_UID.key) ?: ""
            )
        }
    }

    private fun convertDataToCar(document: DocumentSnapshot?): Car? {
        return document?.let{
            Car(
                it.getString(CarField.BRAND.key) ?: "",
                it.getString(CarField.MODEL.key) ?: "",
                it.getLong(CarField.YEAR.key)?.toInt() ?: 0,
                it.getString(CarField.COLOR.key) ?: "",
                it.getDouble(CarField.PRICE.key) ?: 0.0,
                it.getLong(CarField.MILEAGE.key)?.toInt() ?: 0,
                it.getString(CarField.OWNER_ID.key) ?: "",
                it.getString(CarField.USER_ID.key) ?: ""
            )
        }
    }

    fun getCar(carId: String): Car? {
        return convertDataToCar(getCarData(carId).result)
    }

    fun getUser(userId: String): User? {
        return convertDataToUser(getUserData(userId).result)
    }

    fun addUser(userId: String,user: User) {
        getUserDoc(userId)
            .set(user.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "User created with UID: ${userId}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error creating user", e)
            }
    }

    fun addCar(carId : String, car: Car) {
        val carRef = getCarDoc()
            .set(car.toMap())
                .addOnSuccessListener {
                    Log.d("Firestore", "Car created with ID: ${carId}")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error creating car", e)
                }
    }

    fun addUser(userId: String, userData: Map<UserField, Any>) {
        getUserDoc(userId).set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "User created with UID: $userId")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error creating user", e)
            }
    }

    fun addUser(userId: String, email: String) {
        db.collection(usersStr).document(userId)
            .set(User(email).toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "User created with UID: $userId")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error creating user", e)
            }
    }

    private fun convertUserMapToString(userData: Map<UserField, Any>): Map<String, Any> {
        return userData.mapKeys { entry -> entry.key.name }
    }

    private fun convertCarMapToString(carData: Map<CarField, Any>): Map<String, Any> {
        return carData.mapKeys { entry -> entry.key.name }
    }

    fun updateUser(userId: String, userData: Map<UserField, Any>) {
        val userDocRef = getUserDoc(userId)
        userDocRef.update(convertUserMapToString(userData))
            .addOnSuccessListener {
                Log.d("Firestore", "User batch update successful for userId: $userId!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error performing user batch update for userId: $userId", e)
            }
    }

    fun updateUser(userId: String, key : UserField, value: Any) {
        getUserDoc(userId).update(key.name, value).addOnSuccessListener {
            key.name
        }
    }
    fun updateCar(carId: String, key : CarField, value: Any) {
        getCarDoc(carId).update(key.name, value).addOnSuccessListener {
            key.name
        }
    }

    fun updateCar(carId: String, carData: Map<CarField, Any>) {
        val carDocRef = getUserDoc(carId)
        carDocRef.update(convertCarMapToString(carData))
            .addOnSuccessListener {
                Log.d("Firestore", "Car update successful for carId: $carId!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error performing car update for carId: $carId", e)
            }
    }

    fun getFirstCar(): Car? {
        return convertDataToCar(getCars().limit(1).get().result.documents.firstOrNull())
    }

    fun getFirstUser(): User? {
        return convertDataToUser(getUsers().limit(1).get().result.documents.firstOrNull())
    }


    fun deleteUser(userId: String) {
        getUserDoc(userId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "User deleted successfully!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting user", e)
            }
    }

    fun deleteCar(carId: String) {
        getUserDoc(carId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Car deleted successfully!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting car", e)
            }
    }

    @Composable
    fun ShowCar(car: Car, modifier: Modifier = Modifier) {
        // Formatters for better display
        val priceFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        val mileageFormat = NumberFormat.getNumberInstance(Locale.getDefault())

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "${car.year} ${car.brand} ${car.model}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                DetailText(label = "Color", value = car.color)
                DetailText(label = "Price", value = priceFormat.format(car.price))
                DetailText(label = "Mileage", value = "${mileageFormat.format(car.mileage)} miles") // Adjust unit if needed
                DetailText(label = "Owner ID", value = car.ownerId)
                if (car.userId.isNotBlank()) { // Only show User ID if it's relevant/present
                    DetailText(label = "Current User ID", value = car.userId)
                }
            }
        }
    }

    /**
     * Displays the details of a User object within a Card.
     */
    @Composable
    fun ShowUser(user: User, modifier: Modifier = Modifier) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "${user.firstName} ${user.lastName}".trim(), // Handle cases where names might be empty
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                DetailText(label = "Email", value = user.email)
                if (user.phoneNumber.isNotBlank()) {
                    DetailText(label = "Phone", value = user.phoneNumber)
                }
                if (user.address.isNotBlank()) {
                    DetailText(label = "Address", value = user.address)
                }
                if (user.city.isNotBlank()) {
                    DetailText(label = "City", value = user.city)
                }

                Spacer(modifier = Modifier.height(8.dp)) // Separate personal details from car details

                // Display Owned Car IDs
                if (user.ownedCarsId.isNotEmpty()) {
                    DetailText(label = "Owned Car IDs", value = user.ownedCarsId.joinToString(", "))
                } else {
                    DetailText(label = "Owned Car IDs", value = "None")
                }

                // Display Rented Car ID
                if (user.rentedCarId.isNotBlank()) {
                    DetailText(label = "Rented Car ID", value = user.rentedCarId)
                } else {
                    DetailText(label = "Rented Car ID", value = "None")
                }
            }
        }
    }

    /**
     * Helper composable for displaying a label and value pair.
     */
    @Composable
    private fun DetailText(label: String, value: String, modifier: Modifier = Modifier) {
        if (value.isNotBlank()) { // Avoid displaying empty values unless explicitly handled (like "None")
            Text(
                text = "$label: $value",
                style = MaterialTheme.typography.bodyMedium,
                modifier = modifier.padding(vertical = 2.dp) // Add small vertical padding
            )
        }
    }



}


