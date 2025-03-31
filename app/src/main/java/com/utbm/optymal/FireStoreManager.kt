package com.utbm.optymal

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

data class Car(val carId: String,
               val brand: String,
               val model: String,
               val year: Int,
               val color: String,
               val price: Double,
               val mileage: Int,
               val ownerId: String,
               val userId: String?
    ){


    fun toMap(): Map<String, Any?> {
        return mapOf(
            "carId" to carId,
            "brand" to brand,
            "model" to model,
            "year" to year,
            "color" to color,
            "price" to price,
            "mileage" to mileage,
            "ownerId" to ownerId,
            "userId" to userId
        )
    }
}

data class User(val userId: String,
                val firstName: String,
                val lastName: String,
                val email: String,
                val phoneNumber: String,
                val address: String,
                val city: String,
                var ownedCarsUid: MutableList<String>,
                var rentedCarUid: String?){



    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "address" to address,
            "city" to city,
            "ownedCarsUid" to ownedCarsUid,
            "rentedCarUid" to rentedCarUid
        )
    }

}


class FireStoreManager {
    private val db = FirebaseFirestore.getInstance()
    val usersStr = "users"
    val carsStr= "cars"
    val user = User("userId123", "John", "Doe", "john.doe@example.com", "1234567890", "123 Main St", "New York", mutableListOf(), "")
    val car = Car("carId123", "Toyota", "Camry", 2022, "Blue", 25000.0, 50000, "userId123", "")


    fun addCar(){
        addCar(car)
    }

    val cityData = hashMapOf(
        "name" to "Los Angeles",
        "state" to "CA",
        "country" to "USA"
    )
    // Add a new document in collection "cities"
    fun setData() {
        addCar()
    }


    fun getUsers(): CollectionReference {
        return db.collection(usersStr)
    }

    fun getUserDoc(userId: String): DocumentReference {
        return getUsers().document(userId)
    }


    fun getUserData(userId: String): Task<DocumentSnapshot?> {
        return getUserDoc(userId).get().addOnCompleteListener {
            task -> task.result
        }.addOnFailureListener {
            task -> null
        }
    }

    fun getUserDoc(user: User): DocumentReference {
        return getUsers().document(user.userId)
    }

    fun getCars(): CollectionReference {
        return db.collection(carsStr)
    }

    fun getCarDoc(carId: String): DocumentReference {
        return getCars().document(carId)
    }

    fun getCarDoc(car: Car): DocumentReference {
        return getCars().document(car.carId)
    }

    fun getCarData(userId: String): Task<DocumentSnapshot?> {
        return getCarDoc(userId).get().addOnCompleteListener {
                task -> task.result
        }.addOnFailureListener {
                task -> null
        }
    }

    fun convertDataToUser(document: DocumentSnapshot?): User? {
        return document?.let{
            User(
                it.getString("userId") ?: "",
                it.getString("firstName") ?: "",
                it.getString("lastName") ?: "",
                it.getString("email") ?: "",
                it.getString("phoneNumber") ?: "",
                it.getString("address") ?: "",
                it.getString("city") ?: "",
                it.get("ownedCarsUid") as MutableList<String>,
                it.getString("rentedCarUid") ?: ""
            )
        }
    }



    fun addUser(user: User) {
        getUsers().document(user.userId)
            .set(user.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "User created with UID: ${user.userId}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error creating user", e)
            }
    }

    fun addCar(car: Car) {
//        getCar(car)
//            .set(user.toMap())
//            .addOnSuccessListener {
//                Log.d("Firestore", "User created with UID: ${user.userId}")
//            }
//            .addOnFailureListener { e ->
//                Log.w("Firestore", "Error creating user", e)
//            }
    }


    fun addUser(userId: String, userData: Map<String, Any>) {
        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "User created with UID: $userId")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error creating user", e)
            }
    }

    fun deleteUser(userId: String) {
        db.collection("users").document(userId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "User deleted successfully!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting user", e)
            }
    }


    fun getDocumentData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document("userId123")  // Specify the document ID
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val email = document.getString("email")
                    println("User data: $firstName $lastName, $email")
                } else {
                    println("No such document!")
                }
            }
            .addOnFailureListener { e ->
                println("Error getting document: $e")
            }
    }

    fun deleteDocument() {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document("userId123")
            .delete()
            .addOnSuccessListener {
                println("Document successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }

    fun deleteField() {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document("userId123")
            .update("email", FieldValue.delete())
            .addOnSuccessListener {
                println("Field successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting field: $e")
            }
    }


    fun listenForRealTimeUpdates() {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document("userId123")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Error listening to updates: $e")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val firstName = snapshot.getString("firstName")
                    val lastName = snapshot.getString("lastName")
                    val email = snapshot.getString("email")
                    println("Updated User data: $firstName $lastName, $email")
                } else {
                    println("No data available!")
                }
            }
    }

    fun updateDocument() {
        val db = FirebaseFirestore.getInstance()

        val updatedData = hashMapOf(
            "firstName" to "Johnathan",
            "email" to "johnathan.doe@example.com"
        )


    }

}