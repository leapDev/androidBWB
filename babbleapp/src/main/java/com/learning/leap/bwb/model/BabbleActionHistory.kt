package com.learning.leap.bwb.model

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import com.learning.leap.bwb.models.Notification

@DynamoDBTable(tableName = "BabbleActionHistory")
data class BabbleActionHistory(
        @DynamoDBHashKey(attributeName = "ActionHistoryID")
        private val actionHistoryID: String,
        @DynamoDBAttribute(attributeName = "Created")
        private val created: String,
        @DynamoDBRangeKey(attributeName = "BabbleID")
                private val babbleID: String,
        @DynamoDBAttribute(attributeName = "ActionTime")
                private val actionTime: String,
        @DynamoDBAttribute(attributeName = "ActionMessage")
                private val actionMessage: String,
        @DynamoDBAttribute(attributeName = "NotificationID")
                private val notificationID: String,
        private var mNotification: BabbleTip

) {
}
