package com.example.baseframework.test

fun  main(){
    val arr = arrayOf(11,20,1,3,8,45,55,80,32,59,99,82,76,66,9,25,60,20,90,111,128,131,145,147,159,168,146,22)
    quickSort(arr,0,arr.size-1)
    arr.forEach {
        print("$it ,")
    }
    println()
    println("arr.size---->${arr.size}")
    val target=20
    println("$target   key->  ${binarySearch(arr,target,0,arr.size-1)}")
    println("$target   key->  ${binarySearch(arr,target)}")

}

//快速排序
fun quickSort(array:Array<Int>,low:Int , high:Int){
    if(low < high){
        val p = partition(array,low,high)
        quickSort(array,low,p-1)
        quickSort(array,p+1,high)
    }
}

fun partition(array:Array<Int>,low:Int , high:Int):Int{
    var i = low
    var j = high
    val key = array[i]
    while (i<j){
        while (i<j && array[j]>=key) j--
        if(i<j){
            array[i] = array[j]
            i++
        }
        while (i<j && array[i]<=key) i++
        if(i<j){
            array[j] = array[i]
            j--
        }
    }
    array[i] = key
    return i
}

//二分查找
fun binarySearch(array:Array<Int>,key:Int,low:Int,high:Int):Int{
    if(low<=high){
        val mid = (low + high)/2
        if(array[mid] == key) return mid
        return if(array[mid]<key){
            binarySearch(array,key,mid+1,high)
        }else{
            binarySearch(array,key,low,mid-1)
        }
    }
    return -1
}
fun binarySearch(array:Array<Int>,key:Int):Int{
    var low = 0
    var high = array.size
    var mid = 0
    while (low<=high){
        mid = (low + high)/2
        if (array[mid] == key) return mid
        if(array[mid]<key){
            low = mid+1
        }else{
            high = mid-1
        }
    }
    return -1
}