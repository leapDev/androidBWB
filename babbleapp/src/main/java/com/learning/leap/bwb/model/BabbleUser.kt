package com.learning.leap.bwb.model

import android.content.Context
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.models.BabblePlayer
import io.realm.Realm
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.text.SimpleDateFormat
import java.util.*

@RealmClass
@DynamoDBTable(tableName = "babbleUsers")
open class BabbleUser() :RealmModel{
    @PrimaryKey
    @DynamoDBHashKey(attributeName = "Id")
    var babbleID: String = ""
    @DynamoDBRangeKey(attributeName = "BabyBirthday")
    var babyBirthday: String = ""
    @DynamoDBAttribute(attributeName = "BabyName")
    var babyName:String = ""
    @DynamoDBAttribute(attributeName = "BabyGender")
    var babyGender:String = "Not Now"

    constructor(babbleID: String,babyBirthday: String,babyName:String,babyGender:String ) : this() {
        this.babbleID = babbleID
        this.babyBirthday = babyBirthday
        this.babyName = babyName
        this.babyGender = babyGender
        setuserAgeInMonth()
    }

    companion object {
        fun loadBabblePlayerFromSharedPref(saveHelper: LocalLoadSaveHelper): BabbleUser {
            return BabbleUser(saveHelper.babbleID,saveHelper.babyBirthDay,saveHelper.babyName,saveHelper.babyGender)
        }

        fun saveUpdatedInfo(context: Context?) {
            val saveHelper = LocalLoadSaveHelper(context)
            val sharedPrefBirthDay = saveHelper.babyBirthDay
            val updatedBabblePlayer = BabbleUser()
            updatedBabblePlayer.babyBirthday = sharedPrefBirthDay
            updatedBabblePlayer.setuserAgeInMonth()
            saveHelper.saveUserBirthDayInMonth(updatedBabblePlayer.userAgeInMonth)
        }

    }

    var userAgeInMonth = 0
    var birthdayDate: Date? = null

    fun setuserAgeInMonth() {
        if (checkDate()) {
            val userAgeInMonthDouble = daysBetween(birthdayDate!!).toDouble() / 30
            userAgeInMonth = Math.floor(userAgeInMonthDouble).toInt()
        }
    }

    private fun checkName(): Boolean {
        return !checkNameIsEmpty()!! && !checkNameIsTooLong()!!
    }

    fun checkNameIsEmpty(): Boolean? {
        return babyName.isEmpty() || babyName == ""
    }

    fun checkNameIsTooLong(): Boolean? {
        return babyName.length >= 20
    }

    fun checkIfPlayerIsValid(): Boolean? {
        return checkDate() && checkName() && userAgeInMonth > 0
    }

    fun checkDate(): Boolean {
        val pattern = "MM/dd/yyyy"
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        format.isLenient = false
        try {
            format.parse(babyBirthday)?.let {
                birthdayDate = it
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

     fun checkIfAgeIsInRange(startMonth: Int, endMonth: Int, age: Int): Boolean? {
        return age in startMonth..endMonth
    }

    private fun updatedAgeCheck(value: Int): Boolean? {
        return userAgeInMonth >= value
    }


    private fun daysBetween(birthday: Date): Float {
        val date = Date()
        return ((date.time - birthday.time) / (1000 * 60 * 60 * 24)).toFloat()
    }

     fun saveBabbleUser(mapper: DynamoDBMapper, saveHelper: LocalLoadSaveHelper) {
        mapper.save(this)
        saveCurrentBabblePlayerSharedPreference(saveHelper)
    }

     private fun saveCurrentBabblePlayerSharedPreference(saveHelper: LocalLoadSaveHelper) {
        saveHelper.saveBabyBirthDay(babyBirthday)
        saveHelper.saveUserBirthDayInMonth(userAgeInMonth)
        saveHelper.saveBabyName(babyName)
        saveHelper.saveBabbleID(babbleID)
        saveHelper.saveBabyGender(babyGender)
    }

//    fun savePlayerObservable(mapper: DynamoDBMapper, checker: NetworkCheckerInterface, saveHelper: LocalLoadSaveHelper) {
//        return
//    }


    fun retrieveNotifications(babyAge: Int, mapper: DynamoDBMapper): PaginatedScanList<BabbleTip> {
            val scanExpression = DynamoDBScanExpression()
            scanExpression.limit = 2000
            val attributeValue = HashMap<String, AttributeValue>()
            val age = Integer.toString(babyAge)
            val falseAttributeValue = AttributeValue()
            falseAttributeValue.s = "false"
            val ageAttributeValue = AttributeValue()
            ageAttributeValue.n = age
            attributeValue[":val"] = ageAttributeValue
            attributeValue[":val2"] = falseAttributeValue
            scanExpression.expressionAttributeValues = attributeValue
            scanExpression.filterExpression = "StartMonth<=:val AND EndMonth>=:val AND Deleted=:val2"
        return mapper.scan(BabbleTip::class.java,scanExpression)

    }

    fun saveUpdatedInfo(context: Context?) {
        val saveHelper = LocalLoadSaveHelper(context)
        val sharedPrefBirthDay = saveHelper.babyBirthDay
        val updatedBabblePlayer = BabblePlayer()
        updatedBabblePlayer.babyBirthday = sharedPrefBirthDay
        updatedBabblePlayer.setuserAgeInMonth()
        saveHelper.saveUserBirthDayInMonth(updatedBabblePlayer.getuserAgeInMonth())
    }

    fun savePlayerToRealm(realm:Realm) {
        Realm.getDefaultInstance().copyToRealmOrUpdate(this)
    }

}


