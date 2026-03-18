package org.fitnessConstructor.utils

class UserNotExistException(val code:Int = 1000) : Exception()
class EmailNotExist(val code:Int = 1001) : Exception()
data class PasswordNotMatch(val code:Int = 1002) : Exception()
class FillInputCorrect(val code:Int = 1003) : Exception()
class CommonException(itemName: String) : Exception(itemName)
