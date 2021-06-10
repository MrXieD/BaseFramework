package com.example.contactroom.database.entity

import androidx.annotation.NonNull
import androidx.room.*
import java.util.*

/**
@author Anthony.H
@date: 2021/6/7
@desription:
 */
@Entity(tableName = "user")
data class User(
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "phone_numbers") val phoneNumbers: String,
    @ColumnInfo(name = "user_id") @PrimaryKey(autoGenerate = true) val userId: Int = 0
)


@Entity(tableName = "group")
data class Group(
    @ColumnInfo(name = "group_name") val groupName: String,
    @ColumnInfo(name = "group_id") @PrimaryKey(autoGenerate = true) @NonNull val groupId: Int = 0
)

@Entity(tableName = "user_group_cross_ref", primaryKeys = ["user_id", "group_id"])
data class UserGroupCoressRef(
    @ColumnInfo(name = "user_id") @NonNull val userId: Int,
    @ColumnInfo(name = "group_id", index = true) @NonNull val groupId: Int
)


data class UserWithGroups(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "group_id",
        associateBy = Junction(UserGroupCoressRef::class)
    ) val groupList: List<Group>
)

data class GroupWithUsers(
    @Embedded val group: Group, @Relation(
        parentColumn = "group_id",
        entityColumn = "user_id",
        associateBy = Junction(UserGroupCoressRef::class)
    ) val groupList: List<User>
)


//引用
@Entity(tableName = "call_record")
data class CallRecord(
    @ColumnInfo(name = "phone_number") val phoneNumber: String,//这个地方？
    @ColumnInfo(name = "record_date") val recordDate: Date,
    @ColumnInfo(name = "record_type") val recordType: Int,
    @ColumnInfo(name = "record_duration") val recordDuration: String,
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int = 0

) {
    companion object {
        const val INCOME_CALL = 0x1
        const val OUT_CALL = 0x2
    }

}


class DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}