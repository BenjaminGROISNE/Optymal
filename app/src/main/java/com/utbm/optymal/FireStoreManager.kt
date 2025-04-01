package com.utbm.optymal

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore











class FireStoreManager {
    private val db = FirebaseFirestore.getInstance()
    val usersStr = "users"
    val carsStr= "cars"

    fun setData() {
       // addCarTest()
     //   addUserTest()
    }


    fun getUsers(): CollectionReference {
        return db.collection(usersStr)
    }

    fun getUserDoc(userId: String): DocumentReference {
        return getUsers().document(userId)
    }

    fun getUserDoc(user: User): DocumentReference {
        return getUsers().document(user.userId)
    }

    fun getUserData(userId: String): Task<DocumentSnapshot?> {
        return getUserDoc(userId).get().addOnCompleteListener {
                task -> task.result
        }.addOnFailureListener {
                task -> null
        }
    }


    fun getCars(): CollectionReference {
        return db.collection(carsStr)
    }

    fun getCarDoc(carId: String): DocumentReference {
        return getCars().document(carId)
    }

    fun getCarDoc(): DocumentReference {
        return getCars().document()
    }

    fun getCarDoc(car: Car): DocumentReference {
        return getCars().document(car.carId)
    }

    fun getCarData(carId: String): Task<DocumentSnapshot?> {
        return getCarDoc(carId).get().addOnCompleteListener {
                task -> task.result
        }.addOnFailureListener {
                task -> null
        }
    }



    fun convertDataToUser(document: DocumentSnapshot?): User? {
        return document?.let {
            User(
                it.getString(UserField.USER_ID.key) ?: "",
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
    fun convertDataToCar(document: DocumentSnapshot?): Car? {
        return document?.let{
            Car(
                it.getString(CarField.CAR_ID.key) ?: "",
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

    fun addUser(user: User) {
        getUserDoc(user.userId)
            .set(user.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "User created with UID: ${user.userId}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error creating user", e)
            }
    }


    fun updateUser(userId: String, userData: Map<String, Any>) {
        getUserDoc(userId).update(userData).addOnSuccessListener {
            Log.d("Firestore", "User updated successfully!")
        }.addOnFailureListener({
            Log.w("Firestore", "Error updating user", it)
        })
    }


        fun addCar(car: Car) {
            val carRef = getCarDoc().let {
                car.setId(it.id)
                it.set(car.toMap())
                    .addOnSuccessListener {
                        Log.d("Firestore", "Car created with ID: ${car.carId}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error creating car", e)
                    }
            }
        }


        fun addUser(userId: String, userData: Map<String, Any>) {
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
                .set(User(userId, email).toMap())
                .addOnSuccessListener {
                    Log.d("Firestore", "User created with UID: $userId")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error creating user", e)
                }
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

}


