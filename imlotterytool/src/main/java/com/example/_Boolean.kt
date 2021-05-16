package com.example

inline fun Boolean.doTrue(action:() -> Unit){
    if (this) action()
}

inline fun Boolean.doFalse(action:() -> Unit){
    if (!this) action()
}
inline fun Boolean.doIf(trueOfAction:() -> Unit,falseOfAction:() -> Unit){
    if (this) trueOfAction() else falseOfAction()
}