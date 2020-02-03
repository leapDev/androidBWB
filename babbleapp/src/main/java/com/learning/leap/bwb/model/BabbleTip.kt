package com.learning.leap.bwb.model

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
@DynamoDBTable(tableName = "babbleTips")
open class BabbleTip():RealmModel {
        @DynamoDBRangeKey(attributeName = "Created")
        private var created: String = ""
        @DynamoDBHashKey(attributeName = "Tag")
        private var tag: String = ""
        @DynamoDBAttribute(attributeName = "AgeRange")
        private var ageRange: String = ""
        @DynamoDBAttribute(attributeName = "Deleted")
        private var deleted: String = ""
        @DynamoDBAttribute(attributeName = "EndMonth")
        private var endMonth: String = ""
        @DynamoDBAttribute(attributeName = "Message")
        private var message: String = ""
        @DynamoDBAttribute(attributeName = "SoundFileName")
        private var soundFileName: String = ""
        @DynamoDBAttribute(attributeName = "StartMonth")
        private var startMonth: String = ""
        @DynamoDBAttribute(attributeName = "VideoFileName")
        private var videoFileName: String = ""
        @DynamoDBAttribute(attributeName = "Language")
        private var language:String = ""
        @DynamoDBAttribute(attributeName = "Category")
        private var category:String = ""
        @DynamoDBAttribute(attributeName = "Subcategory")
        private var subcategory:String = ""
        private var mPlayToday:Boolean = false
        private var mFavorite :Boolean = false
        @PrimaryKey
        var id:Int = 0
}
